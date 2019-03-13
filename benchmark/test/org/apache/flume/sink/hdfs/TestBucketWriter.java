/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.sink.hdfs;


import SequenceFile.CompressionType.BLOCK;
import com.google.common.base.Charsets;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.flume.Clock;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.SystemClock;
import org.apache.flume.auth.FlumeAuthenticationUtil;
import org.apache.flume.auth.PrivilegedExecutor;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.hdfs.HDFSEventSink.WriterCallback;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestBucketWriter {
    private static Logger logger = LoggerFactory.getLogger(TestBucketWriter.class);

    private Context ctx = new Context();

    private static ScheduledExecutorService timedRollerPool;

    private static PrivilegedExecutor proxy;

    @Test
    public void testEventCountingRoller() throws IOException, InterruptedException {
        int maxEvents = 100;
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollCount(maxEvents).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        for (int i = 0; i < 1000; i++) {
            bucketWriter.append(e);
        }
        TestBucketWriter.logger.info("Number of events written: {}", hdfsWriter.getEventsWritten());
        TestBucketWriter.logger.info("Number of bytes written: {}", hdfsWriter.getBytesWritten());
        TestBucketWriter.logger.info("Number of files opened: {}", hdfsWriter.getFilesOpened());
        Assert.assertEquals("events written", 1000, hdfsWriter.getEventsWritten());
        Assert.assertEquals("bytes written", 3000, hdfsWriter.getBytesWritten());
        Assert.assertEquals("files opened", 10, hdfsWriter.getFilesOpened());
    }

    @Test
    public void testSizeRoller() throws IOException, InterruptedException {
        int maxBytes = 300;
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollSize(maxBytes).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        for (int i = 0; i < 1000; i++) {
            bucketWriter.append(e);
        }
        TestBucketWriter.logger.info("Number of events written: {}", hdfsWriter.getEventsWritten());
        TestBucketWriter.logger.info("Number of bytes written: {}", hdfsWriter.getBytesWritten());
        TestBucketWriter.logger.info("Number of files opened: {}", hdfsWriter.getFilesOpened());
        Assert.assertEquals("events written", 1000, hdfsWriter.getEventsWritten());
        Assert.assertEquals("bytes written", 3000, hdfsWriter.getBytesWritten());
        Assert.assertEquals("files opened", 10, hdfsWriter.getFilesOpened());
    }

    @Test
    public void testIntervalRoller() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1;// seconds

        final int NUM_EVENTS = 10;
        final AtomicBoolean calledBack = new AtomicBoolean(false);
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setOnCloseCallback(new HDFSEventSink.WriterCallback() {
            @Override
            public void run(String filePath) {
                calledBack.set(true);
            }
        }).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        long startNanos = System.nanoTime();
        for (int i = 0; i < (NUM_EVENTS - 1); i++) {
            bucketWriter.append(e);
        }
        // sleep to force a roll... wait 2x interval just to be sure
        Thread.sleep(((2 * ROLL_INTERVAL) * 1000L));
        Assert.assertTrue(bucketWriter.closed.get());
        Assert.assertTrue(calledBack.get());
        bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).build();
        // write one more event (to reopen a new file so we will roll again later)
        bucketWriter.append(e);
        long elapsedMillis = TimeUnit.MILLISECONDS.convert(((System.nanoTime()) - startNanos), TimeUnit.NANOSECONDS);
        long elapsedSeconds = elapsedMillis / 1000L;
        TestBucketWriter.logger.info("Time elapsed: {} milliseconds", elapsedMillis);
        TestBucketWriter.logger.info("Number of events written: {}", hdfsWriter.getEventsWritten());
        TestBucketWriter.logger.info("Number of bytes written: {}", hdfsWriter.getBytesWritten());
        TestBucketWriter.logger.info("Number of files opened: {}", hdfsWriter.getFilesOpened());
        TestBucketWriter.logger.info("Number of files closed: {}", hdfsWriter.getFilesClosed());
        Assert.assertEquals("events written", NUM_EVENTS, hdfsWriter.getEventsWritten());
        Assert.assertEquals("bytes written", ((e.getBody().length) * NUM_EVENTS), hdfsWriter.getBytesWritten());
        Assert.assertEquals("files opened", 2, hdfsWriter.getFilesOpened());
        // before auto-roll
        Assert.assertEquals("files closed", 1, hdfsWriter.getFilesClosed());
        TestBucketWriter.logger.info("Waiting for roll...");
        Thread.sleep(((2 * ROLL_INTERVAL) * 1000L));
        TestBucketWriter.logger.info("Number of files closed: {}", hdfsWriter.getFilesClosed());
        Assert.assertEquals("files closed", 2, hdfsWriter.getFilesClosed());
    }

    @Test
    public void testIntervalRollerBug() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1;// seconds

        final int NUM_EVENTS = 10;
        HDFSWriter hdfsWriter = new HDFSWriter() {
            private volatile boolean open = false;

            public void configure(Context context) {
            }

            public void sync() throws IOException {
                if (!(open)) {
                    throw new IOException("closed");
                }
            }

            public void open(String filePath, CompressionCodec codec, CompressionType cType) throws IOException {
                open = true;
            }

            public void open(String filePath) throws IOException {
                open = true;
            }

            public void close() throws IOException {
                open = false;
            }

            @Override
            public boolean isUnderReplicated() {
                return false;
            }

            public void append(Event e) throws IOException {
                // we just re-open in append if closed
                open = true;
            }
        };
        File tmpFile = File.createTempFile("flume", "test");
        tmpFile.deleteOnExit();
        String path = tmpFile.getParent();
        String name = tmpFile.getName();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setFilePath(path).setFileName(name).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        for (int i = 0; i < (NUM_EVENTS - 1); i++) {
            bucketWriter.append(e);
        }
        // sleep to force a roll... wait 2x interval just to be sure
        Thread.sleep(((2 * ROLL_INTERVAL) * 1000L));
        bucketWriter.flush();// throws closed exception

    }

    @Test
    public void testFileSuffixNotGiven() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1000;// seconds. Make sure it doesn't change in course of test

        final String suffix = null;
        // Need to override system time use for test so we know what to expect
        final long testTime = System.currentTimeMillis();
        Clock testClock = new Clock() {
            public long currentTimeMillis() {
                return testTime;
            }
        };
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setFileSuffix(suffix).setClock(testClock).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        bucketWriter.append(e);
        Assert.assertTrue("Incorrect suffix", hdfsWriter.getOpenedFilePath().endsWith(((Long.toString((testTime + 1))) + ".tmp")));
    }

    @Test
    public void testFileSuffixGiven() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1000;// seconds. Make sure it doesn't change in course of test

        final String suffix = ".avro";
        // Need to override system time use for test so we know what to expect
        final long testTime = System.currentTimeMillis();
        Clock testClock = new Clock() {
            public long currentTimeMillis() {
                return testTime;
            }
        };
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setFileSuffix(suffix).setClock(testClock).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        bucketWriter.append(e);
        Assert.assertTrue("Incorrect suffix", hdfsWriter.getOpenedFilePath().endsWith((((Long.toString((testTime + 1))) + suffix) + ".tmp")));
    }

    @Test
    public void testFileSuffixCompressed() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1000;// seconds. Make sure it doesn't change in course of test

        final String suffix = ".foo";
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        // Need to override system time use for test so we know what to expect
        final long testTime = System.currentTimeMillis();
        Clock testClock = new Clock() {
            public long currentTimeMillis() {
                return testTime;
            }
        };
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setFileSuffix(suffix).setCodeC(HDFSEventSink.getCodec("gzip")).setCompType(BLOCK).setClock(testClock).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        bucketWriter.append(e);
        Assert.assertTrue("Incorrect suffix", hdfsWriter.getOpenedFilePath().endsWith((((Long.toString((testTime + 1))) + suffix) + ".tmp")));
    }

    @Test
    public void testInUsePrefix() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1000;// seconds. Make sure it doesn't change in course of test

        final String PREFIX = "BRNO_IS_CITY_IN_CZECH_REPUBLIC";
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        HDFSTextSerializer formatter = new HDFSTextSerializer();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setInUsePrefix(PREFIX).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        bucketWriter.append(e);
        Assert.assertTrue("Incorrect in use prefix", hdfsWriter.getOpenedFilePath().contains(PREFIX));
    }

    @Test
    public void testInUseSuffix() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1000;// seconds. Make sure it doesn't change in course of test

        final String SUFFIX = "WELCOME_TO_THE_HELLMOUNTH";
        MockHDFSWriter hdfsWriter = new MockHDFSWriter();
        HDFSTextSerializer serializer = new HDFSTextSerializer();
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setRollInterval(ROLL_INTERVAL).setInUseSuffix(SUFFIX).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        bucketWriter.append(e);
        Assert.assertTrue("Incorrect in use suffix", hdfsWriter.getOpenedFilePath().contains(SUFFIX));
    }

    @Test
    public void testCallbackOnClose() throws IOException, InterruptedException {
        final int ROLL_INTERVAL = 1000;// seconds. Make sure it doesn't change in course of test

        final String SUFFIX = "WELCOME_TO_THE_EREBOR";
        final AtomicBoolean callbackCalled = new AtomicBoolean(false);
        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder().setRollInterval(ROLL_INTERVAL).setInUseSuffix(SUFFIX).setOnCloseCallback(new HDFSEventSink.WriterCallback() {
            @Override
            public void run(String filePath) {
                callbackCalled.set(true);
            }
        }).setOnCloseCallbackPath("blah").build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        bucketWriter.append(e);
        bucketWriter.close(true);
        Assert.assertTrue(callbackCalled.get());
    }

    @Test
    public void testSequenceFileRenameRetries() throws Exception {
        sequenceFileRenameRetryCoreTest(1, true);
        sequenceFileRenameRetryCoreTest(5, true);
        sequenceFileRenameRetryCoreTest(2, true);
        sequenceFileRenameRetryCoreTest(1, false);
        sequenceFileRenameRetryCoreTest(5, false);
        sequenceFileRenameRetryCoreTest(2, false);
    }

    @Test
    public void testSequenceFileCloseRetries() throws Exception {
        sequenceFileCloseRetryCoreTest(5);
        sequenceFileCloseRetryCoreTest(1);
    }

    // Test that we don't swallow IOExceptions in secure mode. We should close the bucket writer
    // and rethrow the exception. Regression test for FLUME-3049.
    @Test
    public void testRotateBucketOnIOException() throws IOException, InterruptedException {
        MockHDFSWriter hdfsWriter = Mockito.spy(new MockHDFSWriter());
        PrivilegedExecutor ugiProxy = FlumeAuthenticationUtil.getAuthenticator(null, null).proxyAs("alice");
        final int ROLL_COUNT = 1;// Cause a roll after every successful append().

        BucketWriter bucketWriter = new TestBucketWriter.BucketWriterBuilder(hdfsWriter).setProxyUser(ugiProxy).setRollCount(ROLL_COUNT).build();
        Event e = EventBuilder.withBody("foo", Charsets.UTF_8);
        // Write one event successfully.
        bucketWriter.append(e);
        // Fail the next write.
        IOException expectedIOException = new IOException("Test injected IOException");
        Mockito.doThrow(expectedIOException).when(hdfsWriter).append(Mockito.any(Event.class));
        // The second time we try to write we should get an IOException.
        try {
            bucketWriter.append(e);
            Assert.fail("Expected IOException wasn't thrown during append");
        } catch (IOException ex) {
            Assert.assertEquals(expectedIOException, ex);
            TestBucketWriter.logger.info("Caught expected IOException", ex);
        }
        // The third time we try to write we should get a BucketClosedException, because the
        // BucketWriter should attempt to close itself before rethrowing the IOException on the first
        // call.
        try {
            bucketWriter.append(e);
            Assert.fail("BucketWriter should be already closed, BucketClosedException expected");
        } catch (BucketClosedException ex) {
            TestBucketWriter.logger.info("Caught expected BucketClosedException", ex);
        }
        Assert.assertEquals("events written", 1, hdfsWriter.getEventsWritten());
        Assert.assertEquals("2 files should be closed", 2, hdfsWriter.getFilesClosed());
    }

    private class BucketWriterBuilder {
        private long rollInterval = 0;

        private long rollSize = 0;

        private long rollCount = 0;

        private long batchSize = 0;

        private Context context = TestBucketWriter.this.ctx;

        private String filePath = "/tmp";

        private String fileName = "file";

        private String inUsePrefix = "";

        private String inUseSuffix = ".tmp";

        private String fileSuffix = null;

        private CompressionCodec codeC = null;

        private CompressionType compType = CompressionType.NONE;

        private HDFSWriter writer = null;

        private ScheduledExecutorService timedRollerPool = TestBucketWriter.timedRollerPool;

        private PrivilegedExecutor proxyUser = TestBucketWriter.proxy;

        private SinkCounter sinkCounter = new SinkCounter(("test-bucket-writer-" + (System.currentTimeMillis())));

        private int idleTimeout = 0;

        private WriterCallback onCloseCallback = null;

        private String onCloseCallbackPath = null;

        private long callTimeout = 30000;

        private ExecutorService callTimeoutPool = Executors.newSingleThreadExecutor();

        private long retryInterval = 0;

        private int maxCloseTries = 0;

        private Clock clock = null;

        public BucketWriterBuilder() {
        }

        public BucketWriterBuilder(HDFSWriter writer) {
            this.writer = writer;
        }

        public TestBucketWriter.BucketWriterBuilder setRollInterval(long rollInterval) {
            this.rollInterval = rollInterval;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setRollSize(long rollSize) {
            this.rollSize = rollSize;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setRollCount(long rollCount) {
            this.rollCount = rollCount;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setBatchSize(long batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setContext(Context context) {
            this.context = context;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setFilePath(String filePath) {
            this.filePath = filePath;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setInUsePrefix(String inUsePrefix) {
            this.inUsePrefix = inUsePrefix;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setInUseSuffix(String inUseSuffix) {
            this.inUseSuffix = inUseSuffix;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setFileSuffix(String fileSuffix) {
            this.fileSuffix = fileSuffix;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setCodeC(CompressionCodec codeC) {
            this.codeC = codeC;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setCompType(CompressionType compType) {
            this.compType = compType;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setTimedRollerPool(ScheduledExecutorService timedRollerPool) {
            this.timedRollerPool = timedRollerPool;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setProxyUser(PrivilegedExecutor proxyUser) {
            this.proxyUser = proxyUser;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setSinkCounter(SinkCounter sinkCounter) {
            this.sinkCounter = sinkCounter;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setOnCloseCallback(WriterCallback onCloseCallback) {
            this.onCloseCallback = onCloseCallback;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setOnCloseCallbackPath(String onCloseCallbackPath) {
            this.onCloseCallbackPath = onCloseCallbackPath;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setCallTimeout(long callTimeout) {
            this.callTimeout = callTimeout;
            return this;
        }

        @SuppressWarnings("unused")
        public TestBucketWriter.BucketWriterBuilder setCallTimeoutPool(ExecutorService callTimeoutPool) {
            this.callTimeoutPool = callTimeoutPool;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setRetryInterval(long retryInterval) {
            this.retryInterval = retryInterval;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setMaxCloseTries(int maxCloseTries) {
            this.maxCloseTries = maxCloseTries;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setWriter(HDFSWriter writer) {
            this.writer = writer;
            return this;
        }

        public TestBucketWriter.BucketWriterBuilder setClock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public BucketWriter build() {
            if ((clock) == null) {
                clock = new SystemClock();
            }
            if ((writer) == null) {
                writer = new MockHDFSWriter();
            }
            return new BucketWriter(rollInterval, rollSize, rollCount, batchSize, context, filePath, fileName, inUsePrefix, inUseSuffix, fileSuffix, codeC, compType, writer, timedRollerPool, proxyUser, sinkCounter, idleTimeout, onCloseCallback, onCloseCallbackPath, callTimeout, callTimeoutPool, retryInterval, maxCloseTries, clock);
        }
    }
}

