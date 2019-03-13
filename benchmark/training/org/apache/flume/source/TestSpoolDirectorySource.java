/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.flume.source;


import LifecycleState.START;
import LifecycleState.START_OR_ERROR;
import LifecycleState.STOP;
import LifecycleState.STOP_OR_ERROR;
import SpoolDirectorySourceConfigurationConstants.BASENAME_HEADER;
import SpoolDirectorySourceConfigurationConstants.BASENAME_HEADER_KEY;
import SpoolDirectorySourceConfigurationConstants.BATCH_SIZE;
import SpoolDirectorySourceConfigurationConstants.CONSUME_ORDER;
import SpoolDirectorySourceConfigurationConstants.FILENAME_HEADER;
import SpoolDirectorySourceConfigurationConstants.FILENAME_HEADER_KEY;
import SpoolDirectorySourceConfigurationConstants.RECURSIVE_DIRECTORY_SEARCH;
import SpoolDirectorySourceConfigurationConstants.SPOOL_DIRECTORY;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.flume.ChannelException;
import org.apache.flume.ChannelFullException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.client.avro.ReliableSpoolingFileEventReader;
import org.apache.flume.conf.Configurables;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.lifecycle.LifecycleController;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;


public class TestSpoolDirectorySource {
    static SpoolDirectorySource source;

    static MemoryChannel channel;

    private File tmpDir;

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSortOrder() {
        Context context = new Context();
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(CONSUME_ORDER, "undefined");
        Configurables.configure(TestSpoolDirectorySource.source, context);
    }

    @Test
    public void testValidSortOrder() {
        Context context = new Context();
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(CONSUME_ORDER, "oLdESt");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        context.put(CONSUME_ORDER, "yoUnGest");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        context.put(CONSUME_ORDER, "rAnDom");
        Configurables.configure(TestSpoolDirectorySource.source, context);
    }

    @Test
    public void testPutFilenameHeader() throws IOException, InterruptedException {
        Context context = new Context();
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(FILENAME_HEADER, "true");
        context.put(FILENAME_HEADER_KEY, "fileHeaderKeyTest");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        while ((TestSpoolDirectorySource.source.getSourceCounter().getEventAcceptedCount()) < 8) {
            Thread.sleep(10);
        } 
        Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
        txn.begin();
        Event e = TestSpoolDirectorySource.channel.take();
        Assert.assertNotNull("Event must not be null", e);
        Assert.assertNotNull("Event headers must not be null", e.getHeaders());
        Assert.assertNotNull(e.getHeaders().get("fileHeaderKeyTest"));
        Assert.assertEquals(f1.getAbsolutePath(), e.getHeaders().get("fileHeaderKeyTest"));
        txn.commit();
        txn.close();
    }

    /**
     * Tests if SpoolDirectorySource sets basename headers on events correctly
     */
    @Test
    public void testPutBasenameHeader() throws IOException, InterruptedException {
        Context context = new Context();
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(BASENAME_HEADER, "true");
        context.put(BASENAME_HEADER_KEY, "basenameHeaderKeyTest");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        while ((TestSpoolDirectorySource.source.getSourceCounter().getEventAcceptedCount()) < 8) {
            Thread.sleep(10);
        } 
        Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
        txn.begin();
        Event e = TestSpoolDirectorySource.channel.take();
        Assert.assertNotNull("Event must not be null", e);
        Assert.assertNotNull("Event headers must not be null", e.getHeaders());
        Assert.assertNotNull(e.getHeaders().get("basenameHeaderKeyTest"));
        Assert.assertEquals(f1.getName(), e.getHeaders().get("basenameHeaderKeyTest"));
        txn.commit();
        txn.close();
    }

    /**
     * Tests SpoolDirectorySource with parameter recursion set to true
     */
    @Test
    public void testRecursion_SetToTrue() throws IOException, InterruptedException {
        File subDir = new File(tmpDir, "directorya/directoryb/directoryc");
        boolean directoriesCreated = subDir.mkdirs();
        Assert.assertTrue("source directories must be created", directoriesCreated);
        final String FILE_NAME = "recursion_file.txt";
        File f1 = new File(subDir, FILE_NAME);
        String origBody = "file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n";
        Files.write(origBody, f1, Charsets.UTF_8);
        Context context = new Context();
        context.put(RECURSIVE_DIRECTORY_SEARCH, "true");// enable recursion, so we should find the file we created above

        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());// spool set to root dir

        context.put(FILENAME_HEADER, "true");// put the file name in the "file" header

        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        Assert.assertTrue("Recursion setting in source is correct", TestSpoolDirectorySource.source.getRecursiveDirectorySearch());
        Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
        txn.begin();
        long startTime = System.currentTimeMillis();
        Event e = null;
        while ((((System.currentTimeMillis()) - startTime) < 300) && (e == null)) {
            e = TestSpoolDirectorySource.channel.take();
            Thread.sleep(10);
        } 
        Assert.assertNotNull("Event must not be null", e);
        Assert.assertNotNull("Event headers must not be null", e.getHeaders());
        Assert.assertTrue("File header value did not end with expected filename", e.getHeaders().get("file").endsWith(FILE_NAME));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        do {
            // collecting the whole body
            baos.write(e.getBody());
            baos.write('\n');// newline characters are consumed in the process

            e = TestSpoolDirectorySource.channel.take();
        } while (e != null );
        Assert.assertEquals("Event body is correct", Arrays.toString(origBody.getBytes()), Arrays.toString(baos.toByteArray()));
        txn.commit();
        txn.close();
    }

    /**
     * This test will place a file into a sub-directory of the spool directory
     * since the recursion setting is false there should not be any transactions
     * to take from the channel.  The 500 ms is arbitrary and simply follows
     * what the other tests use to "assume" that since there is no data then this worked.
     */
    @Test
    public void testRecursion_SetToFalse() throws IOException, InterruptedException {
        Context context = new Context();
        File subDir = new File(tmpDir, "directory");
        boolean directoriesCreated = subDir.mkdirs();
        Assert.assertTrue("source directories must be created", directoriesCreated);
        File f1 = new File(((subDir.getAbsolutePath()) + "/file1.txt"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        context.put(RECURSIVE_DIRECTORY_SEARCH, "false");
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(FILENAME_HEADER, "true");
        context.put(FILENAME_HEADER_KEY, "fileHeaderKeyTest");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        // check the source to ensure the setting has been set via the context object
        Assert.assertFalse(("Recursion setting in source is not set to false (this" + "test does not want recursion enabled)"), TestSpoolDirectorySource.source.getRecursiveDirectorySearch());
        Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
        txn.begin();
        long startTime = System.currentTimeMillis();
        Event e = null;
        while ((((System.currentTimeMillis()) - startTime) < 300) && (e == null)) {
            e = TestSpoolDirectorySource.channel.take();
            Thread.sleep(10);
        } 
        Assert.assertNull("Event must be null", e);
        txn.commit();
        txn.close();
    }

    @Test
    public void testLifecycle() throws IOException, InterruptedException {
        Context context = new Context();
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        Configurables.configure(TestSpoolDirectorySource.source, context);
        for (int i = 0; i < 10; i++) {
            TestSpoolDirectorySource.source.start();
            Assert.assertTrue("Reached start or error", LifecycleController.waitForOneOf(TestSpoolDirectorySource.source, START_OR_ERROR));
            Assert.assertEquals("Server is started", START, TestSpoolDirectorySource.source.getLifecycleState());
            TestSpoolDirectorySource.source.stop();
            Assert.assertTrue("Reached stop or error", LifecycleController.waitForOneOf(TestSpoolDirectorySource.source, STOP_OR_ERROR));
            Assert.assertEquals("Server is stopped", STOP, TestSpoolDirectorySource.source.getLifecycleState());
        }
    }

    @Test
    public void testReconfigure() throws IOException, InterruptedException {
        final int NUM_RECONFIGS = 20;
        for (int i = 0; i < NUM_RECONFIGS; i++) {
            Context context = new Context();
            File file = new File((((tmpDir.getAbsolutePath()) + "/file-") + i));
            Files.write(("File " + i), file, Charsets.UTF_8);
            context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
            Configurables.configure(TestSpoolDirectorySource.source, context);
            TestSpoolDirectorySource.source.start();
            Thread.sleep(TimeUnit.SECONDS.toMillis(1));
            Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
            txn.begin();
            try {
                Event event = TestSpoolDirectorySource.channel.take();
                String content = new String(event.getBody(), Charsets.UTF_8);
                Assert.assertEquals(("File " + i), content);
                txn.commit();
            } catch (Throwable t) {
                txn.rollback();
            } finally {
                txn.close();
            }
            TestSpoolDirectorySource.source.stop();
            Assert.assertFalse(("Fatal error on iteration " + i), TestSpoolDirectorySource.source.hasFatalError());
        }
    }

    @Test
    public void testSourceDoesNotDieOnFullChannel() throws Exception {
        Context chContext = new Context();
        chContext.put("capacity", "2");
        chContext.put("transactionCapacity", "2");
        chContext.put("keep-alive", "0");
        TestSpoolDirectorySource.channel.stop();
        Configurables.configure(TestSpoolDirectorySource.channel, chContext);
        TestSpoolDirectorySource.channel.start();
        Context context = new Context();
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write(("file1line1\nfile1line2\nfile1line3\nfile1line4\n" + "file1line5\nfile1line6\nfile1line7\nfile1line8\n"), f1, Charsets.UTF_8);
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(BATCH_SIZE, "2");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.setBackOff(false);
        TestSpoolDirectorySource.source.start();
        // Wait for the source to read enough events to fill up the channel.
        long startTime = System.currentTimeMillis();
        while ((((System.currentTimeMillis()) - startTime) < 5000) && (!(TestSpoolDirectorySource.source.didHitChannelFullException()))) {
            Thread.sleep(10);
        } 
        Assert.assertTrue("Expected to hit ChannelFullException, but did not!", TestSpoolDirectorySource.source.didHitChannelFullException());
        List<String> dataOut = Lists.newArrayList();
        for (int i = 0; i < 8;) {
            Transaction tx = TestSpoolDirectorySource.channel.getTransaction();
            tx.begin();
            Event e = TestSpoolDirectorySource.channel.take();
            if (e != null) {
                dataOut.add(new String(e.getBody(), "UTF-8"));
                i++;
            }
            e = TestSpoolDirectorySource.channel.take();
            if (e != null) {
                dataOut.add(new String(e.getBody(), "UTF-8"));
                i++;
            }
            tx.commit();
            tx.close();
        }
        Assert.assertEquals(8, dataOut.size());
        TestSpoolDirectorySource.source.stop();
    }

    @Test
    public void testEndWithZeroByteFiles() throws IOException, InterruptedException {
        Context context = new Context();
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write("file1line1\n", f1, Charsets.UTF_8);
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        File f3 = new File(((tmpDir.getAbsolutePath()) + "/file3"));
        File f4 = new File(((tmpDir.getAbsolutePath()) + "/file4"));
        Files.touch(f2);
        Files.touch(f3);
        Files.touch(f4);
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        // Need better way to ensure all files were processed.
        Thread.sleep(5000);
        Assert.assertFalse("Server did not error", TestSpoolDirectorySource.source.hasFatalError());
        Assert.assertEquals("Four messages were read", 4, TestSpoolDirectorySource.source.getSourceCounter().getEventAcceptedCount());
        TestSpoolDirectorySource.source.stop();
    }

    @Test
    public void testWithAllEmptyFiles() throws IOException, InterruptedException {
        Context context = new Context();
        File[] f = new File[10];
        for (int i = 0; i < 10; i++) {
            f[i] = new File((((tmpDir.getAbsolutePath()) + "/file") + i));
            Files.write(new byte[0], f[i]);
        }
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        context.put(FILENAME_HEADER, "true");
        context.put(FILENAME_HEADER_KEY, "fileHeaderKeyTest");
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        Thread.sleep(10);
        for (int i = 0; i < 10; i++) {
            Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
            txn.begin();
            Event e = TestSpoolDirectorySource.channel.take();
            Assert.assertNotNull("Event must not be null", e);
            Assert.assertNotNull("Event headers must not be null", e.getHeaders());
            Assert.assertNotNull(e.getHeaders().get("fileHeaderKeyTest"));
            Assert.assertEquals(f[i].getAbsolutePath(), e.getHeaders().get("fileHeaderKeyTest"));
            Assert.assertArrayEquals(new byte[0], e.getBody());
            txn.commit();
            txn.close();
        }
        TestSpoolDirectorySource.source.stop();
    }

    @Test
    public void testWithEmptyAndDataFiles() throws IOException, InterruptedException {
        Context context = new Context();
        File f1 = new File(((tmpDir.getAbsolutePath()) + "/file1"));
        Files.write("some data".getBytes(), f1);
        File f2 = new File(((tmpDir.getAbsolutePath()) + "/file2"));
        Files.write(new byte[0], f2);
        context.put(SPOOL_DIRECTORY, tmpDir.getAbsolutePath());
        Configurables.configure(TestSpoolDirectorySource.source, context);
        TestSpoolDirectorySource.source.start();
        Thread.sleep(10);
        for (int i = 0; i < 2; i++) {
            Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
            txn.begin();
            Event e = TestSpoolDirectorySource.channel.take();
            txn.commit();
            txn.close();
        }
        Transaction txn = TestSpoolDirectorySource.channel.getTransaction();
        txn.begin();
        Assert.assertNull(TestSpoolDirectorySource.channel.take());
        txn.commit();
        txn.close();
        TestSpoolDirectorySource.source.stop();
    }

    @Test
    public void testErrorCounters() throws Exception {
        SourceCounter sc = errorCounterCommonInit();
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new ChannelException("dummy")).doThrow(new ChannelFullException("dummy")).doThrow(new RuntimeException("runtime")).when(cp).processEventBatch(Matchers.anyListOf(Event.class));
        TestSpoolDirectorySource.source.setChannelProcessor(cp);
        ReliableSpoolingFileEventReader reader = Mockito.mock(ReliableSpoolingFileEventReader.class);
        List<Event> events = new ArrayList<>();
        events.add(Mockito.mock(Event.class));
        Mockito.doReturn(events).doReturn(events).doReturn(events).doThrow(new IOException("dummy")).when(reader).readEvents(Mockito.anyInt());
        Runnable runner = TestSpoolDirectorySource.source.new SpoolDirectoryRunnable(reader, sc);
        try {
            runner.run();
        } catch (Exception ex) {
            // Expected
        }
        Assert.assertEquals(2, sc.getChannelWriteFail());
        Assert.assertEquals(1, sc.getGenericProcessingFail());
    }

    @Test
    public void testErrorCounterEventReadFail() throws Exception {
        SourceCounter sc = errorCounterCommonInit();
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        TestSpoolDirectorySource.source.setChannelProcessor(cp);
        ReliableSpoolingFileEventReader reader = Mockito.mock(ReliableSpoolingFileEventReader.class);
        List<Event> events = new ArrayList<>();
        events.add(Mockito.mock(Event.class));
        Mockito.doReturn(events).doThrow(new IOException("dummy")).when(reader).readEvents(Mockito.anyInt());
        Runnable runner = TestSpoolDirectorySource.source.new SpoolDirectoryRunnable(reader, sc);
        try {
            runner.run();
        } catch (Exception ex) {
            // Expected
        }
        Assert.assertEquals(1, sc.getEventReadFail());
    }
}

