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
package com.twitter.distributedlog;


import DLSN.InitialDLSN;
import DistributedLogAnnotations.FlakyTest;
import com.twitter.distributedlog.annotations.DistributedLogAnnotations;
import com.twitter.distributedlog.exceptions.IdleReaderException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * {@link https://issues.apache.org/jira/browse/DL-12}
 */
@DistributedLogAnnotations.FlakyTest
@Ignore
public class TestNonBlockingReads extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestNonBlockingReads.class);

    static {
        TestDistributedLogBase.conf.setOutputBufferSize(0);
        TestDistributedLogBase.conf.setImmediateFlushEnabled(true);
    }

    @Test(timeout = 100000)
    public void testNonBlockingRead() throws Exception {
        String name = "distrlog-non-blocking-reader";
        final DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setReadAheadBatchSize(1);
        confLocal.setReadAheadMaxRecords(1);
        confLocal.setReaderIdleWarnThresholdMillis(100);
        final DistributedLogManager dlm = createNewDLM(confLocal, name);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        ScheduledFuture writerClosedFuture = null;
        try {
            final Thread currentThread = Thread.currentThread();
            writerClosedFuture = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        NonBlockingReadsTestUtil.writeRecordsForNonBlockingReads(confLocal, dlm, false);
                    } catch (Exception exc) {
                        currentThread.interrupt();
                    }
                }
            }, 100, TimeUnit.MILLISECONDS);
            NonBlockingReadsTestUtil.readNonBlocking(dlm, false);
            Assert.assertFalse(currentThread.isInterrupted());
        } finally {
            if (writerClosedFuture != null) {
                // ensure writer.closeAndComplete is done before we close dlm
                writerClosedFuture.get();
            }
            executor.shutdown();
            dlm.close();
        }
    }

    @Test(timeout = 100000)
    public void testNonBlockingReadRecovery() throws Exception {
        String name = "distrlog-non-blocking-reader-recovery";
        final DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setReadAheadBatchSize(10);
        confLocal.setReadAheadMaxRecords(10);
        final DistributedLogManager dlm = createNewDLM(confLocal, name);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        ScheduledFuture writerClosedFuture = null;
        try {
            final Thread currentThread = Thread.currentThread();
            writerClosedFuture = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        NonBlockingReadsTestUtil.writeRecordsForNonBlockingReads(confLocal, dlm, true);
                    } catch (Exception exc) {
                        currentThread.interrupt();
                    }
                }
            }, 100, TimeUnit.MILLISECONDS);
            NonBlockingReadsTestUtil.readNonBlocking(dlm, false);
            Assert.assertFalse(currentThread.isInterrupted());
        } finally {
            if (writerClosedFuture != null) {
                // ensure writer.closeAndComplete is done before we close dlm
                writerClosedFuture.get();
            }
            executor.shutdown();
            dlm.close();
        }
    }

    @Test(timeout = 100000)
    public void testNonBlockingReadIdleError() throws Exception {
        String name = "distrlog-non-blocking-reader-error";
        final DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setReadAheadBatchSize(1);
        confLocal.setReadAheadMaxRecords(1);
        confLocal.setReaderIdleWarnThresholdMillis(50);
        confLocal.setReaderIdleErrorThresholdMillis(100);
        final DistributedLogManager dlm = createNewDLM(confLocal, name);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        ScheduledFuture writerClosedFuture = null;
        try {
            final Thread currentThread = Thread.currentThread();
            writerClosedFuture = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        NonBlockingReadsTestUtil.writeRecordsForNonBlockingReads(confLocal, dlm, false);
                    } catch (Exception exc) {
                        currentThread.interrupt();
                    }
                }
            }, 100, TimeUnit.MILLISECONDS);
            boolean exceptionEncountered = false;
            try {
                NonBlockingReadsTestUtil.readNonBlocking(dlm, false, NonBlockingReadsTestUtil.DEFAULT_SEGMENT_SIZE, true);
            } catch (IdleReaderException exc) {
                exceptionEncountered = true;
            }
            Assert.assertTrue(exceptionEncountered);
            Assert.assertFalse(currentThread.isInterrupted());
        } finally {
            if (writerClosedFuture != null) {
                // ensure writer.closeAndComplete is done before we close dlm
                writerClosedFuture.get();
            }
            executor.shutdown();
            dlm.close();
        }
    }

    @Test(timeout = 60000)
    public void testNonBlockingReadAheadStall() throws Exception {
        String name = "distrlog-non-blocking-reader-stall";
        final DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setReadAheadBatchSize(1);
        confLocal.setReadAheadMaxRecords(3);
        confLocal.setReaderIdleWarnThresholdMillis(500);
        confLocal.setReaderIdleErrorThresholdMillis(30000);
        final DistributedLogManager dlm = createNewDLM(confLocal, name);
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        ScheduledFuture writerClosedFuture = null;
        try {
            final Thread currentThread = Thread.currentThread();
            writerClosedFuture = executor.schedule(new Runnable() {
                @Override
                public void run() {
                    try {
                        NonBlockingReadsTestUtil.writeRecordsForNonBlockingReads(confLocal, dlm, false, 3);
                    } catch (Exception exc) {
                        currentThread.interrupt();
                    }
                }
            }, 10, TimeUnit.MILLISECONDS);
            boolean exceptionEncountered = false;
            try {
                NonBlockingReadsTestUtil.readNonBlocking(dlm, false, 3, false);
            } catch (IdleReaderException exc) {
                TestNonBlockingReads.LOG.info("Exception encountered", exc);
                exceptionEncountered = true;
            }
            Assert.assertFalse(exceptionEncountered);
            Assert.assertFalse(currentThread.isInterrupted());
        } finally {
            if (writerClosedFuture != null) {
                // ensure writer.closeAndComplete is done before we close dlm
                writerClosedFuture.get();
            }
            executor.shutdown();
            dlm.close();
        }
    }

    @Test(timeout = 60000)
    public void testHandleInconsistentMetadata() throws Exception {
        String name = "distrlog-inconsistent-metadata-blocking-read";
        long numRecordsWritten = createStreamWithInconsistentMetadata(name);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, name);
        try {
            LogReader reader = dlm.getInputStream(45);
            long numRecordsRead = 0;
            LogRecord record = reader.readNext(false);
            long lastTxId = -1;
            while (numRecordsRead < (numRecordsWritten / 2)) {
                if (null != record) {
                    DLMTestUtil.verifyLogRecord(record);
                    Assert.assertTrue((lastTxId < (record.getTransactionId())));
                    lastTxId = record.getTransactionId();
                    numRecordsRead++;
                } else {
                    Thread.sleep(1);
                }
                record = reader.readNext(false);
            } 
            reader.close();
            Assert.assertEquals((numRecordsWritten / 2), numRecordsRead);
        } finally {
            dlm.close();
        }
    }

    @Test(timeout = 15000)
    public void testHandleInconsistentMetadataNonBlocking() throws Exception {
        String name = "distrlog-inconsistent-metadata-nonblocking-read";
        long numRecordsWritten = createStreamWithInconsistentMetadata(name);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, name);
        try {
            LogReader reader = dlm.getInputStream(45);
            long numRecordsRead = 0;
            long lastTxId = -1;
            while (numRecordsRead < (numRecordsWritten / 2)) {
                LogRecord record = reader.readNext(false);
                if (record != null) {
                    DLMTestUtil.verifyLogRecord(record);
                    Assert.assertTrue((lastTxId < (record.getTransactionId())));
                    lastTxId = record.getTransactionId();
                    numRecordsRead++;
                } else {
                    Thread.sleep(1);
                }
            } 
            reader.close();
        } finally {
            dlm.close();
        }
    }

    @Test(timeout = 15000)
    public void testHandleInconsistentMetadataDLSNNonBlocking() throws Exception {
        String name = "distrlog-inconsistent-metadata-nonblocking-read-dlsn";
        long numRecordsWritten = createStreamWithInconsistentMetadata(name);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, name);
        try {
            LogReader reader = dlm.getInputStream(InitialDLSN);
            long numRecordsRead = 0;
            long lastTxId = -1;
            while (numRecordsRead < numRecordsWritten) {
                LogRecord record = reader.readNext(false);
                if (record != null) {
                    DLMTestUtil.verifyLogRecord(record);
                    Assert.assertTrue((lastTxId < (record.getTransactionId())));
                    lastTxId = record.getTransactionId();
                    numRecordsRead++;
                } else {
                    Thread.sleep(1);
                }
            } 
            reader.close();
        } finally {
            dlm.close();
        }
    }
}

