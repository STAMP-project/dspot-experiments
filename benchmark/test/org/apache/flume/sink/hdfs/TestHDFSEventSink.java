/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.sink.hdfs;


import CommonConfigurationKeys.HADOOP_SECURITY_AUTHENTICATION;
import Status.BACKOFF;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.flume.Channel;
import org.apache.flume.ChannelException;
import org.apache.flume.Clock;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Sink.Status;
import org.apache.flume.SystemClock;
import org.apache.flume.Transaction;
import org.apache.flume.channel.BasicTransactionSemantics;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.lifecycle.LifecycleException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestHDFSEventSink {
    private HDFSEventSink sink;

    private String testPath;

    private static final Logger LOG = LoggerFactory.getLogger(HDFSEventSink.class);

    static {
        System.setProperty("java.security.krb5.realm", "flume");
        System.setProperty("java.security.krb5.kdc", "blah");
    }

    @Test
    public void testTextBatchAppend() throws Exception {
        doTestTextBatchAppend(false);
    }

    @Test
    public void testTextBatchAppendRawFS() throws Exception {
        doTestTextBatchAppend(true);
    }

    @Test
    public void testLifecycle() throws InterruptedException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        Context context = new Context();
        context.put("hdfs.path", testPath);
        /* context.put("hdfs.rollInterval", String.class);
        context.get("hdfs.rollSize", String.class); context.get("hdfs.rollCount",
        String.class);
         */
        Configurables.configure(sink, context);
        sink.setChannel(new MemoryChannel());
        sink.start();
        sink.stop();
    }

    @Test
    public void testEmptyChannelResultsInStatusBackoff() throws InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        Context context = new Context();
        Channel channel = new MemoryChannel();
        context.put("hdfs.path", testPath);
        context.put("keep-alive", "0");
        Configurables.configure(sink, context);
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Assert.assertEquals(BACKOFF, sink.process());
        sink.stop();
    }

    @Test
    public void testKerbFileAccess() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting testKerbFileAccess() ...");
        final String fileName = "FlumeData";
        final long rollCount = 5;
        final long batchSize = 2;
        String newPath = (testPath) + "/singleBucket";
        String kerbConfPrincipal = "user1/localhost@EXAMPLE.COM";
        String kerbKeytab = "/usr/lib/flume/nonexistkeytabfile";
        // turn security on
        Configuration conf = new Configuration();
        conf.set(HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        UserGroupInformation.setConfiguration(conf);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.kerberosPrincipal", kerbConfPrincipal);
        context.put("hdfs.kerberosKeytab", kerbKeytab);
        try {
            Configurables.configure(sink, context);
            Assert.fail("no exception thrown");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(expected.getMessage().contains("Keytab is not a readable file"));
        } finally {
            // turn security off
            conf.set(HADOOP_SECURITY_AUTHENTICATION, "simple");
            UserGroupInformation.setConfiguration(conf);
        }
    }

    @Test
    public void testTextAppend() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final long rollCount = 3;
        final long batchSize = 2;
        final String fileName = "FlumeData";
        String newPath = (testPath) + "/singleTextBucket";
        int totalEvents = 0;
        int i = 1;
        int j = 1;
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        // context.put("hdfs.path", testPath + "/%Y-%m-%d/%H");
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.writeFormat", "Text");
        context.put("hdfs.fileType", "DataStream");
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < 4; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
                totalEvents++;
            }
            txn.commit();
            txn.close();
            // execute sink to process the events
            sink.process();
        }
        sink.stop();
        // loop through all the files generated and check their contains
        FileStatus[] dirStat = fs.listStatus(dirPath);
        Path[] fList = FileUtil.stat2Paths(dirStat);
        // check that the roll happened correctly for the given data
        long expectedFiles = totalEvents / rollCount;
        if ((totalEvents % rollCount) > 0)
            expectedFiles++;

        Assert.assertEquals(("num files wrong, found: " + (Lists.newArrayList(fList))), expectedFiles, fList.length);
        verifyOutputTextFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
    }

    @Test
    public void testAvroAppend() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final long rollCount = 3;
        final long batchSize = 2;
        final String fileName = "FlumeData";
        String newPath = (testPath) + "/singleTextBucket";
        int totalEvents = 0;
        int i = 1;
        int j = 1;
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        // context.put("hdfs.path", testPath + "/%Y-%m-%d/%H");
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.writeFormat", "Text");
        context.put("hdfs.fileType", "DataStream");
        context.put("serializer", "AVRO_EVENT");
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < 4; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
                totalEvents++;
            }
            txn.commit();
            txn.close();
            // execute sink to process the events
            sink.process();
        }
        sink.stop();
        // loop through all the files generated and check their contains
        FileStatus[] dirStat = fs.listStatus(dirPath);
        Path[] fList = FileUtil.stat2Paths(dirStat);
        // check that the roll happened correctly for the given data
        long expectedFiles = totalEvents / rollCount;
        if ((totalEvents % rollCount) > 0)
            expectedFiles++;

        Assert.assertEquals(("num files wrong, found: " + (Lists.newArrayList(fList))), expectedFiles, fList.length);
        verifyOutputAvroFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
    }

    @Test
    public void testSimpleAppend() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final String fileName = "FlumeData";
        final long rollCount = 5;
        final long batchSize = 2;
        final int numBatches = 4;
        String newPath = (testPath) + "/singleBucket";
        int totalEvents = 0;
        int i = 1;
        int j = 1;
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < numBatches; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
                totalEvents++;
            }
            txn.commit();
            txn.close();
            // execute sink to process the events
            sink.process();
        }
        sink.stop();
        // loop through all the files generated and check their contains
        FileStatus[] dirStat = fs.listStatus(dirPath);
        Path[] fList = FileUtil.stat2Paths(dirStat);
        // check that the roll happened correctly for the given data
        long expectedFiles = totalEvents / rollCount;
        if ((totalEvents % rollCount) > 0)
            expectedFiles++;

        Assert.assertEquals(("num files wrong, found: " + (Lists.newArrayList(fList))), expectedFiles, fList.length);
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
    }

    @Test
    public void testSimpleAppendLocalTime() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        final long currentTime = System.currentTimeMillis();
        Clock clk = new Clock() {
            @Override
            public long currentTimeMillis() {
                return currentTime;
            }
        };
        TestHDFSEventSink.LOG.debug("Starting...");
        final String fileName = "FlumeData";
        final long rollCount = 5;
        final long batchSize = 2;
        final int numBatches = 4;
        String newPath = (testPath) + "/singleBucket/%s";
        String expectedPath = ((testPath) + "/singleBucket/") + (String.valueOf((currentTime / 1000)));
        int totalEvents = 0;
        int i = 1;
        int j = 1;
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(expectedPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.useLocalTimeStamp", String.valueOf(true));
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.setBucketClock(clk);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < numBatches; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
                totalEvents++;
            }
            txn.commit();
            txn.close();
            // execute sink to process the events
            sink.process();
        }
        sink.stop();
        // loop through all the files generated and check their contains
        FileStatus[] dirStat = fs.listStatus(dirPath);
        Path[] fList = FileUtil.stat2Paths(dirStat);
        // check that the roll happened correctly for the given data
        long expectedFiles = totalEvents / rollCount;
        if ((totalEvents % rollCount) > 0)
            expectedFiles++;

        Assert.assertEquals(("num files wrong, found: " + (Lists.newArrayList(fList))), expectedFiles, fList.length);
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
        // The clock in bucketpath is static, so restore the real clock
        sink.setBucketClock(new SystemClock());
    }

    @Test
    public void testAppend() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final long rollCount = 3;
        final long batchSize = 2;
        final String fileName = "FlumeData";
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(testPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", ((testPath) + "/%Y-%m-%d/%H"));
        context.put("hdfs.timeZone", "UTC");
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (int i = 1; i < 4; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (int j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                channel.put(event);
            }
            txn.commit();
            txn.close();
            // execute sink to process the events
            sink.process();
        }
        sink.stop();
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
    }

    // inject fault and make sure that the txn is rolled back and retried
    @Test
    public void testBadSimpleAppend() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final String fileName = "FlumeData";
        final long rollCount = 5;
        final long batchSize = 2;
        final int numBatches = 4;
        String newPath = (testPath) + "/singleBucket";
        int totalEvents = 0;
        int i = 1;
        int j = 1;
        HDFSTestWriterFactory badWriterFactory = new HDFSTestWriterFactory();
        sink = new HDFSEventSink(badWriterFactory);
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.fileType", HDFSTestWriterFactory.TestSequenceFileType);
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < numBatches; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                // inject fault
                if ((totalEvents % 30) == 1) {
                    event.getHeaders().put("fault-once", "");
                }
                channel.put(event);
                totalEvents++;
            }
            txn.commit();
            txn.close();
            TestHDFSEventSink.LOG.info(("Process events: " + (sink.process())));
        }
        TestHDFSEventSink.LOG.info(("Process events to end of transaction max: " + (sink.process())));
        TestHDFSEventSink.LOG.info(("Process events to injected fault: " + (sink.process())));
        TestHDFSEventSink.LOG.info(("Process events remaining events: " + (sink.process())));
        sink.stop();
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
        SinkCounter sc = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        Assert.assertEquals(1, sc.getEventWriteFail());
    }

    /**
     * Ensure that when a write throws an IOException we are
     * able to continue to progress in the next process() call.
     * This relies on Transactional rollback semantics for durability and
     * the behavior of the BucketWriter class of close()ing upon IOException.
     */
    @Test
    public void testCloseReopen() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final int numBatches = 4;
        final String fileName = "FlumeData";
        final long rollCount = 5;
        final long batchSize = 2;
        String newPath = (testPath) + "/singleBucket";
        int i = 1;
        int j = 1;
        HDFSTestWriterFactory badWriterFactory = new HDFSTestWriterFactory();
        sink = new HDFSEventSink(badWriterFactory);
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.fileType", HDFSTestWriterFactory.TestSequenceFileType);
        Configurables.configure(sink, context);
        MemoryChannel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < numBatches; i++) {
            channel.getTransaction().begin();
            try {
                for (j = 1; j <= batchSize; j++) {
                    Event event = new SimpleEvent();
                    eventDate.clear();
                    eventDate.set(2011, i, i, i, 0);// yy mm dd

                    event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                    event.getHeaders().put("hostname", ("Host" + i));
                    String body = (("Test." + i) + ".") + j;
                    event.setBody(body.getBytes());
                    bodies.add(body);
                    // inject fault
                    event.getHeaders().put("fault-until-reopen", "");
                    channel.put(event);
                }
                channel.getTransaction().commit();
            } finally {
                channel.getTransaction().close();
            }
            TestHDFSEventSink.LOG.info(("execute sink to process the events: " + (sink.process())));
        }
        TestHDFSEventSink.LOG.info(("clear any events pending due to errors: " + (sink.process())));
        sink.stop();
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
        SinkCounter sc = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        Assert.assertEquals(1, sc.getEventWriteFail());
    }

    /**
     * Test that the old bucket writer is closed at the end of rollInterval and
     * a new one is used for the next set of events.
     */
    @Test
    public void testCloseReopenOnRollTime() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final int numBatches = 4;
        final String fileName = "FlumeData";
        final long batchSize = 2;
        String newPath = (testPath) + "/singleBucket";
        int i = 1;
        int j = 1;
        HDFSTestWriterFactory badWriterFactory = new HDFSTestWriterFactory();
        sink = new HDFSEventSink(badWriterFactory);
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(0));
        context.put("hdfs.rollSize", String.valueOf(0));
        context.put("hdfs.rollInterval", String.valueOf(2));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.fileType", HDFSTestWriterFactory.TestSequenceFileType);
        Configurables.configure(sink, context);
        MemoryChannel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        for (i = 1; i < numBatches; i++) {
            channel.getTransaction().begin();
            try {
                for (j = 1; j <= batchSize; j++) {
                    Event event = new SimpleEvent();
                    eventDate.clear();
                    eventDate.set(2011, i, i, i, 0);// yy mm dd

                    event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                    event.getHeaders().put("hostname", ("Host" + i));
                    String body = (("Test." + i) + ".") + j;
                    event.setBody(body.getBytes());
                    bodies.add(body);
                    // inject fault
                    event.getHeaders().put("count-check", "");
                    channel.put(event);
                }
                channel.getTransaction().commit();
            } finally {
                channel.getTransaction().close();
            }
            TestHDFSEventSink.LOG.info(("execute sink to process the events: " + (sink.process())));
            // Make sure the first file gets rolled due to rollTimeout.
            if (i == 1) {
                Thread.sleep(2001);
            }
        }
        TestHDFSEventSink.LOG.info(("clear any events pending due to errors: " + (sink.process())));
        sink.stop();
        Assert.assertTrue(((badWriterFactory.openCount.get()) >= 2));
        TestHDFSEventSink.LOG.info("Total number of bucket writers opened: {}", badWriterFactory.openCount.get());
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
    }

    /**
     * Test that a close due to roll interval removes the bucketwriter from
     * sfWriters map.
     */
    @Test
    public void testCloseRemovesFromSFWriters() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final String fileName = "FlumeData";
        final long batchSize = 2;
        String newPath = (testPath) + "/singleBucket";
        int i = 1;
        int j = 1;
        HDFSTestWriterFactory badWriterFactory = new HDFSTestWriterFactory();
        sink = new HDFSEventSink(badWriterFactory);
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(0));
        context.put("hdfs.rollSize", String.valueOf(0));
        context.put("hdfs.rollInterval", String.valueOf(1));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.fileType", HDFSTestWriterFactory.TestSequenceFileType);
        String expectedLookupPath = newPath + "/FlumeData";
        Configurables.configure(sink, context);
        MemoryChannel channel = new MemoryChannel();
        Configurables.configure(channel, new Context());
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        List<String> bodies = Lists.newArrayList();
        // push the event batches into channel
        channel.getTransaction().begin();
        try {
            for (j = 1; j <= (2 * batchSize); j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                String body = (("Test." + i) + ".") + j;
                event.setBody(body.getBytes());
                bodies.add(body);
                // inject fault
                event.getHeaders().put("count-check", "");
                channel.put(event);
            }
            channel.getTransaction().commit();
        } finally {
            channel.getTransaction().close();
        }
        TestHDFSEventSink.LOG.info(("execute sink to process the events: " + (sink.process())));
        Assert.assertTrue(sink.getSfWriters().containsKey(expectedLookupPath));
        // Make sure the first file gets rolled due to rollTimeout.
        Thread.sleep(2001);
        Assert.assertFalse(sink.getSfWriters().containsKey(expectedLookupPath));
        TestHDFSEventSink.LOG.info(("execute sink to process the events: " + (sink.process())));
        // A new bucket writer should have been created for this bucket. So
        // sfWriters map should not have the same key again.
        Assert.assertTrue(sink.getSfWriters().containsKey(expectedLookupPath));
        sink.stop();
        TestHDFSEventSink.LOG.info("Total number of bucket writers opened: {}", badWriterFactory.openCount.get());
        verifyOutputSequenceFiles(fs, conf, dirPath.toUri().getPath(), fileName, bodies);
    }

    /* append using slow sink writer.
    verify that the process returns backoff due to timeout
     */
    @Test
    public void testSlowAppendFailure() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        final String fileName = "FlumeData";
        final long rollCount = 5;
        final long batchSize = 2;
        final int numBatches = 2;
        String newPath = (testPath) + "/singleBucket";
        int i = 1;
        int j = 1;
        // clear the test directory
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(newPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        // create HDFS sink with slow writer
        HDFSTestWriterFactory badWriterFactory = new HDFSTestWriterFactory();
        sink = new HDFSEventSink(badWriterFactory);
        Context context = new Context();
        context.put("hdfs.path", newPath);
        context.put("hdfs.filePrefix", fileName);
        context.put("hdfs.rollCount", String.valueOf(rollCount));
        context.put("hdfs.batchSize", String.valueOf(batchSize));
        context.put("hdfs.fileType", HDFSTestWriterFactory.TestSequenceFileType);
        context.put("hdfs.callTimeout", Long.toString(1000));
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Calendar eventDate = Calendar.getInstance();
        // push the event batches into channel
        for (i = 0; i < numBatches; i++) {
            Transaction txn = channel.getTransaction();
            txn.begin();
            for (j = 1; j <= batchSize; j++) {
                Event event = new SimpleEvent();
                eventDate.clear();
                eventDate.set(2011, i, i, i, 0);// yy mm dd

                event.getHeaders().put("timestamp", String.valueOf(eventDate.getTimeInMillis()));
                event.getHeaders().put("hostname", ("Host" + i));
                event.getHeaders().put("slow", "1500");
                event.setBody(((("Test." + i) + ".") + j).getBytes());
                channel.put(event);
            }
            txn.commit();
            txn.close();
            // execute sink to process the events
            Status satus = sink.process();
            // verify that the append returned backoff due to timeotu
            Assert.assertEquals(satus, BACKOFF);
        }
        sink.stop();
        SinkCounter sc = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        Assert.assertEquals(2, sc.getEventWriteFail());
    }

    /* append using slow sink writer with long append timeout
    verify that the data is written correctly to files
     */
    @Test
    public void testSlowAppendWithLongTimeout() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        slowAppendTestHelper(3000);
    }

    /* append using slow sink writer with no timeout to make append
    synchronous. Verify that the data is written correctly to files
     */
    @Test
    public void testSlowAppendWithoutTimeout() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        TestHDFSEventSink.LOG.debug("Starting...");
        slowAppendTestHelper(0);
    }

    @Test
    public void testCloseOnIdle() throws IOException, InterruptedException, EventDeliveryException {
        String hdfsPath = (testPath) + "/idleClose";
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.get(conf);
        Path dirPath = new Path(hdfsPath);
        fs.delete(dirPath, true);
        fs.mkdirs(dirPath);
        Context context = new Context();
        context.put("hdfs.path", hdfsPath);
        /* All three rolling methods are disabled so the only
        way a file can roll is through the idle timeout.
         */
        context.put("hdfs.rollCount", "0");
        context.put("hdfs.rollSize", "0");
        context.put("hdfs.rollInterval", "0");
        context.put("hdfs.batchSize", "2");
        context.put("hdfs.idleTimeout", "1");
        Configurables.configure(sink, context);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Transaction txn = channel.getTransaction();
        txn.begin();
        for (int i = 0; i < 10; i++) {
            Event event = new SimpleEvent();
            event.setBody(("test event " + i).getBytes());
            channel.put(event);
        }
        txn.commit();
        txn.close();
        sink.process();
        sink.process();
        Thread.sleep(1001);
        // previous file should have timed out now
        // this can throw BucketClosedException(from the bucketWriter having
        // closed),this is not an issue as the sink will retry and get a fresh
        // bucketWriter so long as the onClose handler properly removes
        // bucket writers that were closed.
        sink.process();
        sink.process();
        Thread.sleep(500);// shouldn't be enough for a timeout to occur

        sink.process();
        sink.process();
        sink.stop();
        FileStatus[] dirStat = fs.listStatus(dirPath);
        Path[] fList = FileUtil.stat2Paths(dirStat);
        Assert.assertEquals(("Incorrect content of the directory " + (StringUtils.join(fList, ","))), 2, fList.length);
        Assert.assertTrue(((!(fList[0].getName().endsWith(".tmp"))) && (!(fList[1].getName().endsWith(".tmp")))));
        fs.close();
    }

    /**
     * This test simulates what happens when a batch of events is written to a compressed sequence
     * file (and thus hsync'd to hdfs) but the file is not yet closed.
     *
     * When this happens, the data that we wrote should still be readable.
     */
    @Test
    public void testBlockCompressSequenceFileWriterSync() throws IOException, EventDeliveryException {
        String hdfsPath = (testPath) + "/sequenceFileWriterSync";
        FileSystem fs = FileSystem.get(new Configuration());
        // Since we are reading a partial file we don't want to use checksums
        fs.setVerifyChecksum(false);
        fs.setWriteChecksum(false);
        // Compression codecs that don't require native hadoop libraries
        String[] codecs = new String[]{ "BZip2Codec", "DeflateCodec" };
        for (String codec : codecs) {
            sequenceFileWriteAndVerifyEvents(fs, hdfsPath, codec, Collections.singletonList("single-event"));
            sequenceFileWriteAndVerifyEvents(fs, hdfsPath, codec, Arrays.asList("multiple-events-1", "multiple-events-2", "multiple-events-3", "multiple-events-4", "multiple-events-5"));
        }
        fs.close();
    }

    @Test
    public void testBadConfigurationForRetryIntervalZero() throws Exception {
        Context context = getContextForRetryTests();
        context.put("hdfs.retryInterval", "0");
        Configurables.configure(sink, context);
        Assert.assertEquals(1, sink.getTryCount());
    }

    @Test
    public void testBadConfigurationForRetryIntervalNegative() throws Exception {
        Context context = getContextForRetryTests();
        context.put("hdfs.retryInterval", "-1");
        Configurables.configure(sink, context);
        Assert.assertEquals(1, sink.getTryCount());
    }

    @Test
    public void testBadConfigurationForRetryCountZero() throws Exception {
        Context context = getContextForRetryTests();
        context.put("hdfs.closeTries", "0");
        Configurables.configure(sink, context);
        Assert.assertEquals(Integer.MAX_VALUE, sink.getTryCount());
    }

    @Test
    public void testBadConfigurationForRetryCountNegative() throws Exception {
        Context context = getContextForRetryTests();
        context.put("hdfs.closeTries", "-4");
        Configurables.configure(sink, context);
        Assert.assertEquals(Integer.MAX_VALUE, sink.getTryCount());
    }

    @Test
    public void testRetryRename() throws IOException, InterruptedException, EventDeliveryException, LifecycleException {
        testRetryRename(true);
        testRetryRename(false);
    }

    /**
     * BucketWriter.append() can throw a BucketClosedException when called from
     * HDFSEventSink.process() due to a race condition between HDFSEventSink.process() and the
     * BucketWriter's close threads.
     * This test case tests whether if this happens the newly created BucketWriter will be flushed.
     * For more details see FLUME-3085
     */
    @Test
    public void testFlushedIfAppendFailedWithBucketClosedException() throws Exception {
        final Set<BucketWriter> bucketWriters = new HashSet<>();
        sink = new HDFSEventSink() {
            @Override
            BucketWriter initializeBucketWriter(String realPath, String realName, String lookupPath, HDFSWriter hdfsWriter, WriterCallback closeCallback) {
                BucketWriter bw = Mockito.spy(super.initializeBucketWriter(realPath, realName, lookupPath, hdfsWriter, closeCallback));
                try {
                    // create mock BucketWriters where the first append() succeeds but the
                    // the second call throws a BucketClosedException
                    Mockito.doCallRealMethod().doThrow(BucketClosedException.class).when(bw).append(Mockito.any(Event.class));
                } catch (IOException | InterruptedException e) {
                    Assert.fail("This shouldn't happen, as append() is called during mocking.");
                }
                bucketWriters.add(bw);
                return bw;
            }
        };
        Context context = new Context(ImmutableMap.of("hdfs.path", testPath));
        Configurables.configure(sink, context);
        Channel channel = Mockito.spy(new MemoryChannel());
        Configurables.configure(channel, new Context());
        final Iterator<Event> events = Iterators.forArray(EventBuilder.withBody("test1".getBytes()), EventBuilder.withBody("test2".getBytes()));
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return events.hasNext() ? events.next() : null;
            }
        }).when(channel).take();
        sink.setChannel(channel);
        sink.start();
        sink.process();
        // channel.take() should have called 3 times (2 events + 1 null)
        Mockito.verify(channel, Mockito.times(3)).take();
        FileSystem fs = FileSystem.get(new Configuration());
        int fileCount = 0;
        for (RemoteIterator<LocatedFileStatus> i = fs.listFiles(new Path(testPath), false); i.hasNext(); i.next()) {
            fileCount++;
        }
        Assert.assertEquals(2, fileCount);
        Assert.assertEquals(2, bucketWriters.size());
        // It is expected that flush() method was called exactly once for every BucketWriter
        for (BucketWriter bw : bucketWriters) {
            Mockito.verify(bw, Mockito.times(1)).flush();
        }
        sink.stop();
    }

    @Test
    public void testChannelException() {
        TestHDFSEventSink.LOG.debug("Starting...");
        Context context = new Context();
        context.put("hdfs.path", testPath);
        context.put("keep-alive", "0");
        Configurables.configure(sink, context);
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(channel.take()).thenThrow(new ChannelException("dummy"));
        Mockito.when(channel.getTransaction()).thenReturn(Mockito.mock(BasicTransactionSemantics.class));
        sink.setChannel(channel);
        sink.start();
        try {
            sink.process();
        } catch (EventDeliveryException e) {
            // 
        }
        sink.stop();
        SinkCounter sc = ((SinkCounter) (Whitebox.getInternalState(sink, "sinkCounter")));
        Assert.assertEquals(1, sc.getChannelReadFail());
    }

    @Test
    public void testEmptyInUseSuffix() {
        String inUseSuffixConf = "aaaa";
        Context context = new Context();
        context.put("hdfs.path", testPath);
        context.put("hdfs.inUseSuffix", inUseSuffixConf);
        // hdfs.emptyInUseSuffix not defined
        Configurables.configure(sink, context);
        String inUseSuffix = ((String) (Whitebox.getInternalState(sink, "inUseSuffix")));
        Assert.assertEquals(inUseSuffixConf, inUseSuffix);
        context.put("hdfs.emptyInUseSuffix", "true");
        Configurables.configure(sink, context);
        inUseSuffix = ((String) (Whitebox.getInternalState(sink, "inUseSuffix")));
        Assert.assertEquals("", inUseSuffix);
        context.put("hdfs.emptyInUseSuffix", "false");
        Configurables.configure(sink, context);
        inUseSuffix = ((String) (Whitebox.getInternalState(sink, "inUseSuffix")));
        Assert.assertEquals(inUseSuffixConf, inUseSuffix);
    }
}

