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
package org.apache.flume.source.taildir;


import LifecycleState.START;
import LifecycleState.START_OR_ERROR;
import LifecycleState.STOP;
import LifecycleState.STOP_OR_ERROR;
import Status.BACKOFF;
import Status.READY;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.PollableSource.Status;
import org.apache.flume.Transaction;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.lifecycle.LifecycleController;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;


public class TestTaildirSource {
    static TaildirSource source;

    static MemoryChannel channel;

    private File tmpDir;

    private String posFilePath;

    @Test
    public void testRegexFileNameFilteringEndToEnd() throws IOException {
        File f1 = new File(tmpDir, "a.log");
        File f2 = new File(tmpDir, "a.log.1");
        File f3 = new File(tmpDir, "b.log");
        File f4 = new File(tmpDir, "c.log.yyyy-MM-01");
        File f5 = new File(tmpDir, "c.log.yyyy-MM-02");
        Files.write("a.log\n", f1, Charsets.UTF_8);
        Files.write("a.log.1\n", f2, Charsets.UTF_8);
        Files.write("b.log\n", f3, Charsets.UTF_8);
        Files.write("c.log.yyyy-MM-01\n", f4, Charsets.UTF_8);
        Files.write("c.log.yyyy-MM-02\n", f5, Charsets.UTF_8);
        Context context = new Context();
        context.put(TaildirSourceConfigurationConstants.POSITION_FILE, posFilePath);
        context.put(TaildirSourceConfigurationConstants.FILE_GROUPS, "ab c");
        // Tail a.log and b.log
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "ab"), ((tmpDir.getAbsolutePath()) + "/[ab].log"));
        // Tail files that starts with c.log
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "c"), ((tmpDir.getAbsolutePath()) + "/c.log.*"));
        Configurables.configure(TestTaildirSource.source, context);
        TestTaildirSource.source.start();
        TestTaildirSource.source.process();
        Transaction txn = TestTaildirSource.channel.getTransaction();
        txn.begin();
        List<String> out = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            Event e = TestTaildirSource.channel.take();
            if (e != null) {
                out.add(TestTaildirEventReader.bodyAsString(e));
            }
        }
        txn.commit();
        txn.close();
        Assert.assertEquals(4, out.size());
        // Make sure we got every file
        Assert.assertTrue(out.contains("a.log"));
        Assert.assertFalse(out.contains("a.log.1"));
        Assert.assertTrue(out.contains("b.log"));
        Assert.assertTrue(out.contains("c.log.yyyy-MM-01"));
        Assert.assertTrue(out.contains("c.log.yyyy-MM-02"));
    }

    @Test
    public void testHeaderMapping() throws IOException {
        File f1 = new File(tmpDir, "file1");
        File f2 = new File(tmpDir, "file2");
        File f3 = new File(tmpDir, "file3");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Files.write("file2line1\nfile2line2\n", f2, Charsets.UTF_8);
        Files.write("file3line1\nfile3line2\n", f3, Charsets.UTF_8);
        Context context = new Context();
        context.put(TaildirSourceConfigurationConstants.POSITION_FILE, posFilePath);
        context.put(TaildirSourceConfigurationConstants.FILE_GROUPS, "f1 f2 f3");
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "f1"), ((tmpDir.getAbsolutePath()) + "/file1$"));
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "f2"), ((tmpDir.getAbsolutePath()) + "/file2$"));
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "f3"), ((tmpDir.getAbsolutePath()) + "/file3$"));
        context.put(((TaildirSourceConfigurationConstants.HEADERS_PREFIX) + "f1.headerKeyTest"), "value1");
        context.put(((TaildirSourceConfigurationConstants.HEADERS_PREFIX) + "f2.headerKeyTest"), "value2");
        context.put(((TaildirSourceConfigurationConstants.HEADERS_PREFIX) + "f2.headerKeyTest2"), "value2-2");
        Configurables.configure(TestTaildirSource.source, context);
        TestTaildirSource.source.start();
        TestTaildirSource.source.process();
        Transaction txn = TestTaildirSource.channel.getTransaction();
        txn.begin();
        for (int i = 0; i < 6; i++) {
            Event e = TestTaildirSource.channel.take();
            String body = new String(e.getBody(), Charsets.UTF_8);
            String headerValue = e.getHeaders().get("headerKeyTest");
            String headerValue2 = e.getHeaders().get("headerKeyTest2");
            if (body.startsWith("file1")) {
                Assert.assertEquals("value1", headerValue);
                Assert.assertNull(headerValue2);
            } else
                if (body.startsWith("file2")) {
                    Assert.assertEquals("value2", headerValue);
                    Assert.assertEquals("value2-2", headerValue2);
                } else
                    if (body.startsWith("file3")) {
                        // No header
                        Assert.assertNull(headerValue);
                        Assert.assertNull(headerValue2);
                    }


        }
        txn.commit();
        txn.close();
    }

    @Test
    public void testLifecycle() throws IOException, InterruptedException {
        File f1 = new File(tmpDir, "file1");
        Files.write("file1line1\nfile1line2\n", f1, Charsets.UTF_8);
        Context context = new Context();
        context.put(TaildirSourceConfigurationConstants.POSITION_FILE, posFilePath);
        context.put(TaildirSourceConfigurationConstants.FILE_GROUPS, "f1");
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "f1"), ((tmpDir.getAbsolutePath()) + "/file1$"));
        Configurables.configure(TestTaildirSource.source, context);
        for (int i = 0; i < 3; i++) {
            TestTaildirSource.source.start();
            TestTaildirSource.source.process();
            Assert.assertTrue("Reached start or error", LifecycleController.waitForOneOf(TestTaildirSource.source, START_OR_ERROR));
            Assert.assertEquals("Server is started", START, TestTaildirSource.source.getLifecycleState());
            TestTaildirSource.source.stop();
            Assert.assertTrue("Reached stop or error", LifecycleController.waitForOneOf(TestTaildirSource.source, STOP_OR_ERROR));
            Assert.assertEquals("Server is stopped", STOP, TestTaildirSource.source.getLifecycleState());
        }
    }

    @Test
    public void testFileConsumeOrder() throws IOException {
        ArrayList<String> consumedOrder = Lists.newArrayList();
        ArrayList<String> expected = prepareFileConsumeOrder();
        TestTaildirSource.source.start();
        TestTaildirSource.source.process();
        Transaction txn = TestTaildirSource.channel.getTransaction();
        txn.begin();
        for (int i = 0; i < 12; i++) {
            Event e = TestTaildirSource.channel.take();
            String body = new String(e.getBody(), Charsets.UTF_8);
            consumedOrder.add(body);
        }
        txn.commit();
        txn.close();
        System.out.println(consumedOrder);
        Assert.assertArrayEquals("Files not consumed in expected order", expected.toArray(), consumedOrder.toArray());
    }

    @Test
    public void testPutFilenameHeader() throws IOException {
        File f1 = configureSource();
        TestTaildirSource.source.start();
        TestTaildirSource.source.process();
        Transaction txn = TestTaildirSource.channel.getTransaction();
        txn.begin();
        Event e = TestTaildirSource.channel.take();
        txn.commit();
        txn.close();
        Assert.assertNotNull(e.getHeaders().get("path"));
        Assert.assertEquals(f1.getAbsolutePath(), e.getHeaders().get("path"));
    }

    @Test
    public void testErrorCounterEventReadFail() throws Exception {
        configureSource();
        TestTaildirSource.source.start();
        ReliableTaildirEventReader reader = Mockito.mock(ReliableTaildirEventReader.class);
        Whitebox.setInternalState(TestTaildirSource.source, "reader", reader);
        Mockito.when(reader.updateTailFiles()).thenReturn(Collections.singletonList(123L));
        Mockito.when(reader.getTailFiles()).thenThrow(new RuntimeException("hello"));
        TestTaildirSource.source.process();
        Assert.assertEquals(1, TestTaildirSource.source.getSourceCounter().getEventReadFail());
        TestTaildirSource.source.stop();
    }

    @Test
    public void testErrorCounterFileHandlingFail() throws Exception {
        configureSource();
        Whitebox.setInternalState(TestTaildirSource.source, "idleTimeout", 0);
        Whitebox.setInternalState(TestTaildirSource.source, "checkIdleInterval", 60);
        TestTaildirSource.source.start();
        ReliableTaildirEventReader reader = Mockito.mock(ReliableTaildirEventReader.class);
        Mockito.when(reader.getTailFiles()).thenThrow(new RuntimeException("hello"));
        Whitebox.setInternalState(TestTaildirSource.source, "reader", reader);
        TimeUnit.MILLISECONDS.sleep(200);
        Assert.assertTrue((0 < (TestTaildirSource.source.getSourceCounter().getGenericProcessingFail())));
        TestTaildirSource.source.stop();
    }

    @Test
    public void testErrorCounterChannelWriteFail() throws Exception {
        prepareFileConsumeOrder();
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        TestTaildirSource.source.setChannelProcessor(cp);
        Mockito.doThrow(new ChannelException("dummy")).doNothing().when(cp).processEventBatch(ArgumentMatchers.anyListOf(Event.class));
        TestTaildirSource.source.start();
        TestTaildirSource.source.process();
        Assert.assertEquals(1, TestTaildirSource.source.getSourceCounter().getChannelWriteFail());
        TestTaildirSource.source.stop();
    }

    @Test
    public void testMaxBatchCount() throws IOException {
        File f1 = new File(tmpDir, "file1");
        File f2 = new File(tmpDir, "file2");
        Files.write(("file1line1\nfile1line2\n" + "file1line3\nfile1line4\n"), f1, Charsets.UTF_8);
        Files.write(("file2line1\nfile2line2\n" + "file2line3\nfile2line4\n"), f2, Charsets.UTF_8);
        Context context = new Context();
        context.put(TaildirSourceConfigurationConstants.POSITION_FILE, posFilePath);
        context.put(TaildirSourceConfigurationConstants.FILE_GROUPS, "fg");
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "fg"), ((tmpDir.getAbsolutePath()) + "/file.*"));
        context.put(TaildirSourceConfigurationConstants.BATCH_SIZE, String.valueOf(1));
        context.put(TaildirSourceConfigurationConstants.MAX_BATCH_COUNT, String.valueOf(2));
        Configurables.configure(TestTaildirSource.source, context);
        TestTaildirSource.source.start();
        // 2 x 4 lines will be processed in 2 rounds
        TestTaildirSource.source.process();
        TestTaildirSource.source.process();
        List<Event> eventList = new ArrayList<Event>();
        for (int i = 0; i < 8; i++) {
            Transaction txn = TestTaildirSource.channel.getTransaction();
            txn.begin();
            Event e = TestTaildirSource.channel.take();
            txn.commit();
            txn.close();
            if (e == null) {
                break;
            }
            eventList.add(e);
        }
        Assert.assertEquals("1", context.getString(TaildirSourceConfigurationConstants.BATCH_SIZE));
        Assert.assertEquals("2", context.getString(TaildirSourceConfigurationConstants.MAX_BATCH_COUNT));
        Assert.assertEquals(8, eventList.size());
        // the processing order of the files is not deterministic
        String firstFile = new String(eventList.get(0).getBody()).substring(0, 5);
        String secondFile = (firstFile.equals("file1")) ? "file2" : "file1";
        Assert.assertEquals((firstFile + "line1"), new String(eventList.get(0).getBody()));
        Assert.assertEquals((firstFile + "line2"), new String(eventList.get(1).getBody()));
        Assert.assertEquals((secondFile + "line1"), new String(eventList.get(2).getBody()));
        Assert.assertEquals((secondFile + "line2"), new String(eventList.get(3).getBody()));
        Assert.assertEquals((firstFile + "line3"), new String(eventList.get(4).getBody()));
        Assert.assertEquals((firstFile + "line4"), new String(eventList.get(5).getBody()));
        Assert.assertEquals((secondFile + "line3"), new String(eventList.get(6).getBody()));
        Assert.assertEquals((secondFile + "line4"), new String(eventList.get(7).getBody()));
    }

    @Test
    public void testStatus() throws IOException {
        File f1 = new File(tmpDir, "file1");
        File f2 = new File(tmpDir, "file2");
        Files.write(("file1line1\nfile1line2\n" + "file1line3\nfile1line4\nfile1line5\n"), f1, Charsets.UTF_8);
        Files.write(("file2line1\nfile2line2\n" + "file2line3\n"), f2, Charsets.UTF_8);
        Context context = new Context();
        context.put(TaildirSourceConfigurationConstants.POSITION_FILE, posFilePath);
        context.put(TaildirSourceConfigurationConstants.FILE_GROUPS, "fg");
        context.put(((TaildirSourceConfigurationConstants.FILE_GROUPS_PREFIX) + "fg"), ((tmpDir.getAbsolutePath()) + "/file.*"));
        context.put(TaildirSourceConfigurationConstants.BATCH_SIZE, String.valueOf(1));
        context.put(TaildirSourceConfigurationConstants.MAX_BATCH_COUNT, String.valueOf(2));
        Configurables.configure(TestTaildirSource.source, context);
        TestTaildirSource.source.start();
        Status status;
        status = TestTaildirSource.source.process();
        Assert.assertEquals(READY, status);
        status = TestTaildirSource.source.process();
        Assert.assertEquals(READY, status);
        status = TestTaildirSource.source.process();
        Assert.assertEquals(BACKOFF, status);
        status = TestTaildirSource.source.process();
        Assert.assertEquals(BACKOFF, status);
    }
}

