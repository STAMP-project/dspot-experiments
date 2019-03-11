/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zeppelin.interpreter.remote;


import AppendOutputRunner.BUFFER_TIME_MS;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class AppendOutputRunnerTest {
    private static final int NUM_EVENTS = 10000;

    private static final int NUM_CLUBBED_EVENTS = 100;

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private static ScheduledFuture<?> future = null;

    /* It is being accessed by multiple threads.
    While loop for 'loopForBufferCompletion' could
    run for-ever.
     */
    private static volatile int numInvocations = 0;

    @Test
    public void testSingleEvent() throws InterruptedException {
        RemoteInterpreterProcessListener listener = Mockito.mock(RemoteInterpreterProcessListener.class);
        String[][] buffer = new String[][]{ new String[]{ "note", "para", "data\n" } };
        loopForCompletingEvents(listener, 1, buffer);
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(String.class));
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend("note", "para", 0, "data\n");
    }

    @Test
    public void testMultipleEventsOfSameParagraph() throws InterruptedException {
        RemoteInterpreterProcessListener listener = Mockito.mock(RemoteInterpreterProcessListener.class);
        String note1 = "note1";
        String para1 = "para1";
        String[][] buffer = new String[][]{ new String[]{ note1, para1, "data1\n" }, new String[]{ note1, para1, "data2\n" }, new String[]{ note1, para1, "data3\n" } };
        loopForCompletingEvents(listener, 1, buffer);
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(String.class));
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(note1, para1, 0, "data1\ndata2\ndata3\n");
    }

    @Test
    public void testMultipleEventsOfDifferentParagraphs() throws InterruptedException {
        RemoteInterpreterProcessListener listener = Mockito.mock(RemoteInterpreterProcessListener.class);
        String note1 = "note1";
        String note2 = "note2";
        String para1 = "para1";
        String para2 = "para2";
        String[][] buffer = new String[][]{ new String[]{ note1, para1, "data1\n" }, new String[]{ note1, para2, "data2\n" }, new String[]{ note2, para1, "data3\n" }, new String[]{ note2, para2, "data4\n" } };
        loopForCompletingEvents(listener, 4, buffer);
        Mockito.verify(listener, Mockito.times(4)).onOutputAppend(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(String.class));
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(note1, para1, 0, "data1\n");
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(note1, para2, 0, "data2\n");
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(note2, para1, 0, "data3\n");
        Mockito.verify(listener, Mockito.times(1)).onOutputAppend(note2, para2, 0, "data4\n");
    }

    @Test
    public void testClubbedData() throws InterruptedException {
        RemoteInterpreterProcessListener listener = Mockito.mock(RemoteInterpreterProcessListener.class);
        AppendOutputRunner runner = new AppendOutputRunner(listener);
        AppendOutputRunnerTest.future = AppendOutputRunnerTest.service.scheduleWithFixedDelay(runner, 0, BUFFER_TIME_MS, TimeUnit.MILLISECONDS);
        Thread thread = new Thread(new AppendOutputRunnerTest.BombardEvents(runner));
        thread.start();
        thread.join();
        Thread.sleep(1000);
        /* NUM_CLUBBED_EVENTS is a heuristic number.
        It has been observed that for 10,000 continuos event
        calls, 30-40 Web-socket calls are made. Keeping
        the unit-test to a pessimistic 100 web-socket calls.
         */
        Mockito.verify(listener, Mockito.atMost(AppendOutputRunnerTest.NUM_CLUBBED_EVENTS)).onOutputAppend(ArgumentMatchers.any(String.class), ArgumentMatchers.any(String.class), ArgumentMatchers.anyInt(), ArgumentMatchers.any(String.class));
    }

    @Test
    public void testWarnLoggerForLargeData() throws InterruptedException {
        RemoteInterpreterProcessListener listener = Mockito.mock(RemoteInterpreterProcessListener.class);
        AppendOutputRunner runner = new AppendOutputRunner(listener);
        String data = "data\n";
        int numEvents = 100000;
        for (int i = 0; i < numEvents; i++) {
            runner.appendBuffer("noteId", "paraId", 0, data);
        }
        AppendOutputRunnerTest.TestAppender appender = new AppendOutputRunnerTest.TestAppender();
        Logger logger = Logger.getRootLogger();
        logger.addAppender(appender);
        runner.run();
        List<LoggingEvent> log;
        int warnLogCounter;
        LoggingEvent sizeWarnLogEntry = null;
        do {
            warnLogCounter = 0;
            log = appender.getLog();
            for (LoggingEvent logEntry : log) {
                if (Level.WARN.equals(logEntry.getLevel())) {
                    sizeWarnLogEntry = logEntry;
                    warnLogCounter += 1;
                }
            }
        } while (warnLogCounter != 2 );
        String loggerString = ("Processing size for buffered append-output is high: " + ((data.length()) * numEvents)) + " characters.";
        Assert.assertTrue(loggerString.equals(sizeWarnLogEntry.getMessage()));
    }

    private class BombardEvents implements Runnable {
        private final AppendOutputRunner runner;

        private BombardEvents(AppendOutputRunner runner) {
            this.runner = runner;
        }

        @Override
        public void run() {
            String noteId = "noteId";
            String paraId = "paraId";
            for (int i = 0; i < (AppendOutputRunnerTest.NUM_EVENTS); i++) {
                runner.appendBuffer(noteId, paraId, 0, "data\n");
            }
        }
    }

    private class TestAppender extends AppenderSkeleton {
        private final List<LoggingEvent> log = new ArrayList<>();

        @Override
        public boolean requiresLayout() {
            return false;
        }

        @Override
        protected void append(final LoggingEvent loggingEvent) {
            log.add(loggingEvent);
        }

        @Override
        public void close() {
        }

        public List<LoggingEvent> getLog() {
            return new ArrayList<>(log);
        }
    }
}

