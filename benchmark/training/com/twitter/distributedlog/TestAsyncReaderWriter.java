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


import BookKeeper.DigestType.CRC32;
import CompressionCodec.Type.LZ4;
import DLSN.InitialDLSN;
import DLSN.InvalidDLSN;
import DistributedLogConfiguration.BKDL_BOOKKEEPER_ENSEMBLE_SIZE;
import DistributedLogConfiguration.BKDL_BOOKKEEPER_ENSEMBLE_SIZE_DEFAULT;
import FailpointUtils.FailPointActions.FailPointAction_Throw;
import FailpointUtils.FailPointName.FP_RecoverIncompleteLogSegments;
import LogRecordSet.Writer;
import LogSegmentMetadata.LogSegmentMetadataVersion.VERSION_V4_ENVELOPED_ENTRIES;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.twitter.distributedlog.config.ConcurrentBaseConfiguration;
import com.twitter.distributedlog.config.DynamicDistributedLogConfiguration;
import com.twitter.distributedlog.exceptions.BKTransmitException;
import com.twitter.distributedlog.exceptions.DLIllegalStateException;
import com.twitter.distributedlog.exceptions.EndOfStreamException;
import com.twitter.distributedlog.exceptions.LockingException;
import com.twitter.distributedlog.exceptions.LogRecordTooLongException;
import com.twitter.distributedlog.exceptions.ReadCancelledException;
import com.twitter.distributedlog.exceptions.WriteException;
import com.twitter.distributedlog.lock.DistributedLock;
import com.twitter.distributedlog.namespace.DistributedLogNamespace;
import com.twitter.distributedlog.util.FailpointUtils;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.Utils;
import com.twitter.util.Await;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.Promise;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.bookkeeper.client.BookKeeperAccessor;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.client.LedgerMetadata;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static DLSN.InitialDLSN;
import static DLSN.InvalidDLSN;
import static DistributedLogConfiguration.BKDL_BOOKKEEPER_ENSEMBLE_SIZE_DEFAULT;
import static junit.framework.Assert.assertEquals;


public class TestAsyncReaderWriter extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestAsyncReaderWriter.class);

    protected DistributedLogConfiguration testConf;

    public TestAsyncReaderWriter() {
        this.testConf = new DistributedLogConfiguration();
        this.testConf.loadConf(TestDistributedLogBase.conf);
        this.testConf.setReaderIdleErrorThresholdMillis(1200000);
    }

    @Rule
    public TestName runtime = new TestName();

    /**
     * Test writing control records to writers: writers should be able to write control records, and
     * the readers should skip control records while reading.
     */
    @Test(timeout = 60000)
    public void testWriteControlRecord() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(1024);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        // Write 3 log segments. For each log segments, write one control record and nine user records.
        int txid = 1;
        for (long i = 0; i < 3; i++) {
            final long currentLogSegmentSeqNo = i + 1;
            BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
            DLSN dlsn = Await.result(writer.writeControlRecord(new LogRecord((txid++), "control".getBytes(Charsets.UTF_8))));
            Assert.assertEquals(currentLogSegmentSeqNo, dlsn.getLogSegmentSequenceNo());
            Assert.assertEquals(0, dlsn.getEntryId());
            Assert.assertEquals(0, dlsn.getSlotId());
            for (long j = 1; j < 10; j++) {
                final LogRecord record = DLMTestUtil.getLargeLogRecordInstance((txid++));
                Await.result(writer.write(record));
            }
            writer.closeAndComplete();
        }
        dlm.close();
        // Read all the written data: It should skip control records and only return user records.
        DistributedLogManager readDlm = createNewDLM(confLocal, name);
        LogReader reader = readDlm.getInputStream(1);
        long numTrans = 0;
        long expectedTxId = 2;
        LogRecord record = reader.readNext(false);
        while (null != record) {
            DLMTestUtil.verifyLargeLogRecord(record);
            numTrans++;
            Assert.assertEquals(expectedTxId, record.getTransactionId());
            if ((expectedTxId % 10) == 0) {
                expectedTxId += 2;
            } else {
                ++expectedTxId;
            }
            record = reader.readNext(false);
        } 
        Assert.assertEquals((3 * 9), numTrans);
        Assert.assertEquals((3 * 9), readDlm.getLogRecordCount());
        readDlm.close();
    }

    @Test(timeout = 60000)
    public void testAsyncWritePendingWritesAbortedWhenLedgerRollTriggerFails() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(1024);
        confLocal.setMaxLogSegmentBytes(1024);
        confLocal.setLogSegmentRollingIntervalMinutes(0);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        // Write one record larger than max seg size. Ledger doesn't roll until next write.
        int txid = 1;
        LogRecord record = DLMTestUtil.getLogRecordInstance((txid++), 2048);
        Future<DLSN> result = writer.write(record);
        DLSN dlsn = Await.result(result, Duration.fromSeconds(10));
        Assert.assertEquals(1, dlsn.getLogSegmentSequenceNo());
        record = DLMTestUtil.getLogRecordInstance((txid++), ((LogRecord.MAX_LOGRECORD_SIZE) + 1));
        result = writer.write(record);
        DLMTestUtil.validateFutureFailed(result, LogRecordTooLongException.class);
        record = DLMTestUtil.getLogRecordInstance((txid++), ((LogRecord.MAX_LOGRECORD_SIZE) + 1));
        result = writer.write(record);
        DLMTestUtil.validateFutureFailed(result, WriteException.class);
        record = DLMTestUtil.getLogRecordInstance((txid++), ((LogRecord.MAX_LOGRECORD_SIZE) + 1));
        result = writer.write(record);
        DLMTestUtil.validateFutureFailed(result, WriteException.class);
        writer.closeAndComplete();
        dlm.close();
    }

    /**
     * Test Case: Simple Async Writes. Writes 30 records. They should be written correctly.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testSimpleAsyncWrite() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(1024);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 10;
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        final CountDownLatch syncLatch = new CountDownLatch((numLogSegments * numRecordsPerLogSegment));
        final AtomicBoolean errorsFound = new AtomicBoolean(false);
        final AtomicReference<DLSN> maxDLSN = new AtomicReference<DLSN>(InvalidDLSN);
        int txid = 1;
        for (long i = 0; i < numLogSegments; i++) {
            final long currentLogSegmentSeqNo = i + 1;
            BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
            for (long j = 0; j < numRecordsPerLogSegment; j++) {
                final long currentEntryId = j;
                final LogRecord record = DLMTestUtil.getLargeLogRecordInstance((txid++));
                Future<DLSN> dlsnFuture = writer.write(record);
                dlsnFuture.addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
                    @Override
                    public void onSuccess(DLSN value) {
                        if ((value.getLogSegmentSequenceNo()) != currentLogSegmentSeqNo) {
                            TestAsyncReaderWriter.LOG.debug("LogSegmentSequenceNumber: {}, Expected {}", value.getLogSegmentSequenceNo(), currentLogSegmentSeqNo);
                            errorsFound.set(true);
                        }
                        if ((value.getEntryId()) != currentEntryId) {
                            TestAsyncReaderWriter.LOG.debug("EntryId: {}, Expected {}", value.getEntryId(), currentEntryId);
                            errorsFound.set(true);
                        }
                        if ((value.compareTo(maxDLSN.get())) > 0) {
                            maxDLSN.set(value);
                        }
                        syncLatch.countDown();
                        TestAsyncReaderWriter.LOG.debug("SyncLatch: {}", syncLatch.getCount());
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        TestAsyncReaderWriter.LOG.error("Encountered exception on writing record {} in log segment {}", currentEntryId, currentLogSegmentSeqNo);
                        errorsFound.set(true);
                    }
                });
            }
            writer.closeAndComplete();
        }
        syncLatch.await();
        Assert.assertFalse("Should not encounter any errors for async writes", errorsFound.get());
        LogRecordWithDLSN last = dlm.getLastLogRecord();
        Assert.assertEquals(((("Last DLSN" + (last.getDlsn())) + " isn't the maximum DLSN ") + (maxDLSN.get())), last.getDlsn(), maxDLSN.get());
        Assert.assertEquals(last.getDlsn(), dlm.getLastDLSN());
        Assert.assertEquals(last.getDlsn(), Await.result(dlm.getLastDLSNAsync()));
        DLMTestUtil.verifyLargeLogRecord(last);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testSimpleAsyncRead() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        simpleAsyncReadTest(name, confLocal);
    }

    @Test(timeout = 60000)
    public void testSimpleAsyncReadWriteWithMonitoredFuturePool() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setTaskExecutionWarnTimeMicros(1000);
        confLocal.setEnableTaskExecutionStats(true);
        simpleAsyncReadTest(name, confLocal);
    }

    @Test(timeout = 60000)
    public void testBulkAsyncRead() throws Exception {
        String name = "distrlog-bulkasyncread";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadMaxRecords(10000);
        confLocal.setReadAheadBatchSize(10);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 20;
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, 1L, false);
        final AsyncLogReader reader = dlm.getAsyncLogReader(InitialDLSN);
        int expectedTxID = 1;
        int numReads = 0;
        while (expectedTxID <= (numLogSegments * numRecordsPerLogSegment)) {
            if (expectedTxID == (numLogSegments * numRecordsPerLogSegment)) {
                break;
            }
            List<LogRecordWithDLSN> records = Await.result(reader.readBulk(20));
            TestAsyncReaderWriter.LOG.info("Bulk read {} entries.", records.size());
            Assert.assertTrue(((records.size()) >= 1));
            for (LogRecordWithDLSN record : records) {
                Assert.assertEquals(expectedTxID, record.getTransactionId());
                ++expectedTxID;
            }
            ++numReads;
        } 
        // we expect bulk read works
        Assert.assertTrue((numReads < 60));
        Utils.close(reader);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testBulkAsyncReadWithWriteBatch() throws Exception {
        String name = "distrlog-bulkasyncread-with-writebatch";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setOutputBufferSize(1024000);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadMaxRecords(10000);
        confLocal.setReadAheadBatchSize(10);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 20;
        TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, 1L, false);
        final AsyncLogReader reader = dlm.getAsyncLogReader(InitialDLSN);
        int expectedTxID = 1;
        for (long i = 0; i < 3; i++) {
            // since we batched 20 entries into single bookkeeper entry
            // we should be able to read 20 entries as a batch.
            List<LogRecordWithDLSN> records = Await.result(reader.readBulk(20));
            Assert.assertEquals(20, records.size());
            for (LogRecordWithDLSN record : records) {
                Assert.assertEquals(expectedTxID, record.getTransactionId());
                ++expectedTxID;
            }
        }
        Utils.close(reader);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testAsyncReadEmptyRecords() throws Exception {
        String name = "distrlog-simpleasyncreadempty";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(10);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 10;
        long txid = 1L;
        // write 3 log segments, 10 records per log segment
        txid = TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, txid, true);
        // write another log segment with 5 records and flush every 2 records
        txid = TestAsyncReaderWriter.writeLogSegment(dlm, 5, txid, 2, true);
        AsyncLogReader asyncReader = dlm.getAsyncLogReader(InvalidDLSN);
        Assert.assertEquals((((("Expected stream name = " + name) + " but ") + (asyncReader.getStreamName())) + " found"), name, asyncReader.getStreamName());
        long numTrans = 0;
        DLSN lastDLSN = InvalidDLSN;
        LogRecordWithDLSN record = Await.result(asyncReader.readNext());
        while (null != record) {
            DLMTestUtil.verifyEmptyLogRecord(record);
            Assert.assertEquals(0, record.getDlsn().getSlotId());
            Assert.assertTrue(((record.getDlsn().compareTo(lastDLSN)) > 0));
            lastDLSN = record.getDlsn();
            numTrans++;
            if (numTrans >= (txid - 1)) {
                break;
            }
            record = Await.result(asyncReader.readNext());
        } 
        Assert.assertEquals((txid - 1), numTrans);
        Utils.close(asyncReader);
        dlm.close();
    }

    /**
     * Test Async Read by positioning to a given position in the log
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testSimpleAsyncReadPosition() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(1024);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(10);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 10;
        long txid = 1L;
        // write 3 log segments, 10 records per log segment
        txid = TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, txid, false);
        // write another log segment with 5 records
        txid = TestAsyncReaderWriter.writeLogSegment(dlm, 5, txid, Integer.MAX_VALUE, false);
        final CountDownLatch syncLatch = new CountDownLatch(((int) (txid - 14)));
        final CountDownLatch doneLatch = new CountDownLatch(1);
        final AtomicBoolean errorsFound = new AtomicBoolean(false);
        final AsyncLogReader reader = dlm.getAsyncLogReader(new DLSN(2, 2, 4));
        Assert.assertEquals(name, reader.getStreamName());
        boolean monotonic = LogSegmentMetadata.supportsSequenceId(confLocal.getDLLedgerMetadataLayoutVersion());
        TestAsyncReaderWriter.readNext(reader, new DLSN(2, 3, 0), (monotonic ? 13L : Long.MIN_VALUE), monotonic, syncLatch, doneLatch, errorsFound);
        doneLatch.await();
        Assert.assertFalse("Errors found on reading records", errorsFound.get());
        syncLatch.await();
        Utils.close(reader);
        dlm.close();
    }

    /**
     * Test write/read entries when immediate flush is disabled.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testSimpleAsyncReadWrite() throws Exception {
        testSimpleAsyncReadWriteInternal(runtime.getMethodName(), false);
    }

    /**
     * Test write/read entries when immediate flush is enabled.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testSimpleAsyncReadWriteImmediateFlush() throws Exception {
        testSimpleAsyncReadWriteInternal(runtime.getMethodName(), true);
    }

    /**
     * Test if entries written using log segment metadata that doesn't support enveloping
     * can be read correctly by a reader supporting both.
     *
     * NOTE: An older reader cannot read enveloped entry, so we don't have a test case covering
     *       the other scenario.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testNoEnvelopeWriterEnvelopeReader() throws Exception {
        testSimpleAsyncReadWriteInternal(runtime.getMethodName(), true, ((VERSION_V4_ENVELOPED_ENTRIES.value) - 1));
    }

    static class WriteFutureEventListener implements com.twitter.util.FutureEventListener<DLSN> {
        private final LogRecord record;

        private final long currentLogSegmentSeqNo;

        private final long currentEntryId;

        private final CountDownLatch syncLatch;

        private final AtomicBoolean errorsFound;

        private final boolean verifyEntryId;

        WriteFutureEventListener(LogRecord record, long currentLogSegmentSeqNo, long currentEntryId, CountDownLatch syncLatch, AtomicBoolean errorsFound, boolean verifyEntryId) {
            this.record = record;
            this.currentLogSegmentSeqNo = currentLogSegmentSeqNo;
            this.currentEntryId = currentEntryId;
            this.syncLatch = syncLatch;
            this.errorsFound = errorsFound;
            this.verifyEntryId = verifyEntryId;
        }

        /**
         * Invoked if the computation completes successfully
         */
        @Override
        public void onSuccess(DLSN value) {
            if ((value.getLogSegmentSequenceNo()) != (currentLogSegmentSeqNo)) {
                TestAsyncReaderWriter.LOG.error("Ledger Seq No: {}, Expected: {}", value.getLogSegmentSequenceNo(), currentLogSegmentSeqNo);
                errorsFound.set(true);
            }
            if ((verifyEntryId) && ((value.getEntryId()) != (currentEntryId))) {
                TestAsyncReaderWriter.LOG.error("EntryId: {}, Expected: {}", value.getEntryId(), currentEntryId);
                errorsFound.set(true);
            }
            syncLatch.countDown();
        }

        /**
         * Invoked if the computation completes unsuccessfully
         */
        @Override
        public void onFailure(Throwable cause) {
            TestAsyncReaderWriter.LOG.error("Encountered failures on writing record as (lid = {}, eid = {}) :", new Object[]{ currentLogSegmentSeqNo, currentEntryId, cause });
            errorsFound.set(true);
            syncLatch.countDown();
        }
    }

    /**
     * Test Case: starting reading when the streams don't exist.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testSimpleAsyncReadWriteStartEmpty() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(10);
        confLocal.setOutputBufferSize(1024);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 10;
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        final CountDownLatch readerReadyLatch = new CountDownLatch(1);
        final CountDownLatch readerDoneLatch = new CountDownLatch(1);
        final CountDownLatch readerSyncLatch = new CountDownLatch((numLogSegments * numRecordsPerLogSegment));
        final TestReader reader = new TestReader("test-reader", dlm, InitialDLSN, false, 0, readerReadyLatch, readerSyncLatch, readerDoneLatch);
        reader.start();
        // Increase the probability of reader failure and retry
        Thread.sleep(500);
        final AtomicBoolean writeErrors = new AtomicBoolean(false);
        final CountDownLatch writeLatch = new CountDownLatch(30);
        int txid = 1;
        for (long i = 0; i < 3; i++) {
            final long currentLogSegmentSeqNo = i + 1;
            BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
            for (long j = 0; j < 10; j++) {
                final long currentEntryId = j;
                final LogRecord record = DLMTestUtil.getLargeLogRecordInstance((txid++));
                Future<DLSN> dlsnFuture = writer.write(record);
                dlsnFuture.addEventListener(new TestAsyncReaderWriter.WriteFutureEventListener(record, currentLogSegmentSeqNo, currentEntryId, writeLatch, writeErrors, true));
            }
            writer.closeAndComplete();
        }
        writeLatch.await();
        Assert.assertFalse("All writes should succeed", writeErrors.get());
        readerDoneLatch.await();
        Assert.assertFalse("Should not encounter errors during reading", reader.areErrorsFound());
        readerSyncLatch.await();
        Assert.assertTrue("Should position reader at least once", ((reader.getNumReaderPositions().get()) > 1));
        dlm.close();
    }

    /**
     * Flaky test fixed: readers need to be added to the pendingReaders
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSimpleAsyncReadWriteSimulateErrors() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(10);
        confLocal.setOutputBufferSize(1024);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 20;
        int numRecordsPerLogSegment = 10;
        final CountDownLatch doneLatch = new CountDownLatch(1);
        final CountDownLatch syncLatch = new CountDownLatch((numLogSegments * numRecordsPerLogSegment));
        TestReader reader = new TestReader("test-reader", dlm, InitialDLSN, true, 0, new CountDownLatch(1), syncLatch, doneLatch);
        reader.start();
        final CountDownLatch writeLatch = new CountDownLatch(200);
        final AtomicBoolean writeErrors = new AtomicBoolean(false);
        int txid = 1;
        for (long i = 0; i < numLogSegments; i++) {
            final long currentLogSegmentSeqNo = i + 1;
            BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
            for (long j = 0; j < numRecordsPerLogSegment; j++) {
                final long currentEntryId = j;
                final LogRecord record = DLMTestUtil.getLargeLogRecordInstance((txid++));
                Future<DLSN> dlsnFuture = writer.write(record);
                dlsnFuture.addEventListener(new TestAsyncReaderWriter.WriteFutureEventListener(record, currentLogSegmentSeqNo, currentEntryId, writeLatch, writeErrors, true));
            }
            writer.closeAndComplete();
        }
        writeLatch.await();
        Assert.assertFalse("All writes should succeed", writeErrors.get());
        doneLatch.await();
        Assert.assertFalse("Should not encounter errors during reading", reader.areErrorsFound());
        syncLatch.await();
        Assert.assertTrue("Should position reader at least once", ((reader.getNumReaderPositions().get()) > 1));
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testSimpleAsyncReadWritePiggyBack() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setEnableReadAhead(true);
        confLocal.setReadAheadWaitTime(500);
        confLocal.setReadAheadBatchSize(10);
        confLocal.setReadAheadMaxRecords(100);
        confLocal.setOutputBufferSize(1024);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(100);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        final AsyncLogReader reader = dlm.getAsyncLogReader(InvalidDLSN);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 10;
        final CountDownLatch readLatch = new CountDownLatch(30);
        final CountDownLatch readDoneLatch = new CountDownLatch(1);
        final AtomicBoolean readErrors = new AtomicBoolean(false);
        final CountDownLatch writeLatch = new CountDownLatch(30);
        final AtomicBoolean writeErrors = new AtomicBoolean(false);
        int txid = 1;
        for (long i = 0; i < numLogSegments; i++) {
            final long currentLogSegmentSeqNo = i + 1;
            BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
            for (long j = 0; j < numRecordsPerLogSegment; j++) {
                Thread.sleep(50);
                final LogRecord record = DLMTestUtil.getLargeLogRecordInstance((txid++));
                Future<DLSN> dlsnFuture = writer.write(record);
                dlsnFuture.addEventListener(new TestAsyncReaderWriter.WriteFutureEventListener(record, currentLogSegmentSeqNo, j, writeLatch, writeErrors, false));
                if ((i == 0) && (j == 0)) {
                    boolean monotonic = LogSegmentMetadata.supportsSequenceId(confLocal.getDLLedgerMetadataLayoutVersion());
                    TestAsyncReaderWriter.readNext(reader, InvalidDLSN, (monotonic ? 0L : Long.MIN_VALUE), monotonic, readLatch, readDoneLatch, readErrors);
                }
            }
            writer.closeAndComplete();
        }
        writeLatch.await();
        Assert.assertFalse("All writes should succeed", writeErrors.get());
        readDoneLatch.await();
        Assert.assertFalse("All reads should succeed", readErrors.get());
        readLatch.await();
        Utils.close(reader);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testCancelReadRequestOnReaderClosed() throws Exception {
        final String name = "distrlog-cancel-read-requests-on-reader-closed";
        DistributedLogManager dlm = createNewDLM(testConf, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        writer.write(DLMTestUtil.getLogRecordInstance(1L));
        writer.closeAndComplete();
        final AsyncLogReader reader = dlm.getAsyncLogReader(InitialDLSN);
        LogRecordWithDLSN record = Await.result(reader.readNext());
        Assert.assertEquals(1L, record.getTransactionId());
        DLMTestUtil.verifyLogRecord(record);
        final CountDownLatch readLatch = new CountDownLatch(1);
        final AtomicBoolean receiveExpectedException = new AtomicBoolean(false);
        Thread readThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Await.result(reader.readNext());
                } catch (ReadCancelledException rce) {
                    receiveExpectedException.set(true);
                } catch (Throwable t) {
                    TestAsyncReaderWriter.LOG.error("Receive unexpected exception on reading stream {} : ", name, t);
                }
                readLatch.countDown();
            }
        }, "read-thread");
        readThread.start();
        Thread.sleep(1000);
        // close reader should cancel the pending read next
        Utils.close(reader);
        readLatch.await();
        readThread.join();
        Assert.assertTrue("Read request should be cancelled.", receiveExpectedException.get());
        // closed reader should reject any readNext
        try {
            Await.result(reader.readNext());
            Assert.fail("Reader should reject readNext if it is closed.");
        } catch (ReadCancelledException rce) {
            // expected
        }
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testAsyncWriteWithMinDelayBetweenFlushes() throws Exception {
        String name = "distrlog-asyncwrite-mindelay";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setMinDelayBetweenImmediateFlushMs(100);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        final Thread currentThread = Thread.currentThread();
        final int COUNT = 5000;
        final CountDownLatch syncLatch = new CountDownLatch(COUNT);
        int txid = 1;
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        Stopwatch executionTime = Stopwatch.createStarted();
        for (long i = 0; i < COUNT; i++) {
            Thread.sleep(1);
            final LogRecord record = DLMTestUtil.getLogRecordInstance((txid++));
            Future<DLSN> dlsnFuture = writer.write(record);
            dlsnFuture.addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
                @Override
                public void onSuccess(DLSN value) {
                    syncLatch.countDown();
                    TestAsyncReaderWriter.LOG.debug("SyncLatch: {} ; DLSN: {} ", syncLatch.getCount(), value);
                }

                @Override
                public void onFailure(Throwable cause) {
                    currentThread.interrupt();
                }
            });
        }
        boolean success = false;
        if (!(Thread.interrupted())) {
            try {
                success = syncLatch.await(10, TimeUnit.SECONDS);
            } catch (InterruptedException exc) {
                Thread.currentThread().interrupt();
            }
        }
        // Abort, not graceful close, since the latter will
        // flush as well, and may add an entry.
        writer.abort();
        executionTime.stop();
        assert !(Thread.interrupted());
        assert success;
        LogRecordWithDLSN last = dlm.getLastLogRecord();
        TestAsyncReaderWriter.LOG.info("Last Entry {}; elapsed time {}", last.getDlsn().getEntryId(), executionTime.elapsed(TimeUnit.MILLISECONDS));
        // Regardless of how many records we wrote; the number of BK entries should always be bounded by the min delay.
        // Since there are two flush processes--data flush and control flush, and since control flush may also end up flushing
        // data if data is available, the upper bound is 2*(time/min_delay + 1)
        Assert.assertTrue(((last.getDlsn().getEntryId()) <= ((((executionTime.elapsed(TimeUnit.MILLISECONDS)) / (confLocal.getMinDelayBetweenImmediateFlushMs())) + 1) * 2)));
        DLMTestUtil.verifyLogRecord(last);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testAsyncWriteWithMinDelayBetweenFlushesFlushFailure() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setMinDelayBetweenImmediateFlushMs(1);
        URI uri = createDLMURI(("/" + name));
        ensureURICreated(uri);
        DistributedLogNamespace namespace = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(confLocal).uri(uri).clientId("gabbagoo").build();
        DistributedLogManager dlm = namespace.openLog(name);
        DistributedLogNamespace namespace1 = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(confLocal).uri(uri).clientId("tortellini").build();
        DistributedLogManager dlm1 = namespace1.openLog(name);
        int txid = 1;
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        // First write succeeds since lock isnt checked until transmit, which is scheduled
        Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txid++))));
        writer.flushAndCommit();
        BKLogSegmentWriter perStreamWriter = writer.getCachedLogWriter();
        DistributedLock lock = perStreamWriter.getLock();
        FutureUtils.result(lock.asyncClose());
        // Get second writer, steal lock
        BKAsyncLogWriter writer2 = ((BKAsyncLogWriter) (dlm1.startAsyncLogSegmentNonPartitioned()));
        try {
            // Succeeds, kicks off scheduked flush
            writer.write(DLMTestUtil.getLogRecordInstance((txid++)));
            // Succeeds, kicks off scheduled flush
            Thread.sleep(100);
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txid++))));
            Assert.fail("should have thrown");
        } catch (LockingException ex) {
            TestAsyncReaderWriter.LOG.debug("caught exception ", ex);
        }
        writer.close();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testOutstandingWriteLimitNoLimit() throws Exception {
        writeRecordsWithOutstandingWriteLimit((-1), (-1), false);
    }

    @Test(timeout = 60000)
    public void testOutstandingWriteLimitVeryHighLimit() throws Exception {
        writeRecordsWithOutstandingWriteLimit(Integer.MAX_VALUE, Integer.MAX_VALUE, false);
    }

    @Test(timeout = 60000)
    public void testOutstandingWriteLimitBlockAllStreamLimit() throws Exception {
        writeRecordsWithOutstandingWriteLimit(0, Integer.MAX_VALUE, true);
    }

    @Test(timeout = 60000)
    public void testOutstandingWriteLimitBlockAllGlobalLimit() throws Exception {
        writeRecordsWithOutstandingWriteLimit(Integer.MAX_VALUE, 0, true);
    }

    @Test(timeout = 60000)
    public void testOutstandingWriteLimitBlockAllLimitWithDarkmode() throws Exception {
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setPerWriterOutstandingWriteLimit(0);
        confLocal.setOutstandingWriteLimitDarkmode(true);
        DistributedLogManager dlm = createNewDLM(confLocal, runtime.getMethodName());
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        ArrayList<Future<DLSN>> results = new ArrayList<Future<DLSN>>(1000);
        for (int i = 0; i < 1000; i++) {
            results.add(writer.write(DLMTestUtil.getLogRecordInstance(1L)));
        }
        for (Future<DLSN> result : results) {
            Await.result(result);
        }
        writer.closeAndComplete();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testCloseAndCompleteLogSegmentWhenStreamIsInError() throws Exception {
        String name = "distrlog-close-and-complete-logsegment-when-stream-is-in-error";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        BKDistributedLogManager dlm = ((BKDistributedLogManager) (createNewDLM(confLocal, name)));
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        long txId = 1L;
        for (int i = 0; i < 5; i++) {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txId++))));
        }
        BKLogSegmentWriter logWriter = writer.getCachedLogWriter();
        // fence the ledger
        dlm.getWriterBKC().get().openLedger(logWriter.getLogSegmentId(), CRC32, confLocal.getBKDigestPW().getBytes(Charsets.UTF_8));
        try {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txId++))));
            Assert.fail("Should fail write to a fenced ledger with BKTransmitException");
        } catch (BKTransmitException bkte) {
            // expected
        }
        try {
            writer.closeAndComplete();
            Assert.fail("Should fail to complete a log segment when its ledger is fenced");
        } catch (BKTransmitException bkte) {
            // expected
        }
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertTrue(segments.get(0).isInProgress());
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testCloseAndCompleteLogSegmentWhenCloseFailed() throws Exception {
        String name = "distrlog-close-and-complete-logsegment-when-close-failed";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        BKDistributedLogManager dlm = ((BKDistributedLogManager) (createNewDLM(confLocal, name)));
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        long txId = 1L;
        for (int i = 0; i < 5; i++) {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txId++))));
        }
        BKLogSegmentWriter logWriter = writer.getCachedLogWriter();
        // fence the ledger
        dlm.getWriterBKC().get().openLedger(logWriter.getLogSegmentId(), CRC32, confLocal.getBKDigestPW().getBytes(Charsets.UTF_8));
        try {
            // insert a write to detect the fencing state, to make test more robust.
            writer.write(DLMTestUtil.getLogRecordInstance((txId++)));
            writer.closeAndComplete();
            Assert.fail("Should fail to complete a log segment when its ledger is fenced");
        } catch (IOException ioe) {
            // expected
            TestAsyncReaderWriter.LOG.error("Failed to close and complete log segment {} : ", logWriter.getFullyQualifiedLogSegment(), ioe);
        }
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertTrue(segments.get(0).isInProgress());
        dlm.close();
    }

    @Test(timeout = 10000)
    public void testAsyncReadIdleControlRecord() throws Exception {
        String name = "distrlog-async-reader-idle-error-control";
        testAsyncReadIdleErrorInternal(name, 500, true, false);
    }

    @Test(timeout = 10000)
    public void testAsyncReadIdleError() throws Exception {
        String name = "distrlog-async-reader-idle-error";
        testAsyncReadIdleErrorInternal(name, 1000, false, false);
    }

    @Test(timeout = 10000)
    public void testAsyncReadIdleError2() throws Exception {
        String name = "distrlog-async-reader-idle-error-2";
        testAsyncReadIdleErrorInternal(name, 1000, true, true);
    }

    @Test(timeout = 60000)
    public void testReleaseLockAfterFailedToRecover() throws Exception {
        String name = "release-lock-after-failed-to-recover";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setLockTimeout(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        Await.result(writer.write(DLMTestUtil.getLogRecordInstance(1L)));
        writer.abort();
        for (int i = 0; i < 2; i++) {
            FailpointUtils.setFailpoint(FP_RecoverIncompleteLogSegments, FailPointAction_Throw);
            try {
                dlm.startAsyncLogSegmentNonPartitioned();
                Assert.fail("Should fail during recovering incomplete log segments");
            } catch (IOException ioe) {
                // expected;
            } finally {
                FailpointUtils.removeFailpoint(FP_RecoverIncompleteLogSegments);
            }
        }
        writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        Assert.assertEquals(1, segments.size());
        Assert.assertFalse(segments.get(0).isInProgress());
        writer.close();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testGetLastTxId() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        AsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        int numRecords = 10;
        for (int i = 0; i < numRecords; i++) {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance(i)));
            Assert.assertEquals(("last tx id should become " + i), i, writer.getLastTxId());
        }
        // open a writer to recover the inprogress log segment
        AsyncLogWriter recoverWriter = dlm.startAsyncLogSegmentNonPartitioned();
        Assert.assertEquals(("recovered last tx id should be " + (numRecords - 1)), (numRecords - 1), recoverWriter.getLastTxId());
    }

    @Test(timeout = 60000)
    public void testMaxReadAheadRecords() throws Exception {
        int maxRecords = 1;
        int batchSize = 8;
        int maxAllowedCachedRecords = (maxRecords + batchSize) - 1;
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(Integer.MAX_VALUE);
        confLocal.setReadAheadMaxRecords(maxRecords);
        confLocal.setReadAheadBatchSize(batchSize);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        AsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        int numRecords = 40;
        for (int i = 1; i <= numRecords; i++) {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance(i)));
            Assert.assertEquals(("last tx id should become " + i), i, writer.getLastTxId());
        }
        LogRecord record = DLMTestUtil.getLogRecordInstance(numRecords);
        record.setControl();
        Await.result(writer.write(record));
        BKAsyncLogReaderDLSN reader = ((BKAsyncLogReaderDLSN) (dlm.getAsyncLogReader(InitialDLSN)));
        record = Await.result(reader.readNext());
        TestAsyncReaderWriter.LOG.info("Read record {}", record);
        Assert.assertEquals(1L, record.getTransactionId());
        Assert.assertNotNull(reader.bkLedgerManager.readAheadWorker);
        Assert.assertTrue(((reader.bkLedgerManager.readAheadCache.getNumCachedRecords()) <= maxAllowedCachedRecords));
        for (int i = 2; i <= numRecords; i++) {
            record = Await.result(reader.readNext());
            TestAsyncReaderWriter.LOG.info("Read record {}", record);
            Assert.assertEquals(((long) (i)), record.getTransactionId());
            TimeUnit.MILLISECONDS.sleep(20);
            int numCachedRecords = reader.bkLedgerManager.readAheadCache.getNumCachedRecords();
            Assert.assertTrue((((((("Should cache less than " + batchSize) + " records but already found ") + numCachedRecords) + " records when reading ") + i) + "th record"), (numCachedRecords <= maxAllowedCachedRecords));
        }
    }

    @Test(timeout = 60000)
    public void testMarkEndOfStream() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        final int NUM_RECORDS = 10;
        int i = 1;
        for (; i <= NUM_RECORDS; i++) {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance(i)));
            Assert.assertEquals(("last tx id should become " + i), i, writer.getLastTxId());
        }
        Await.result(writer.markEndOfStream());
        // Multiple end of streams are ok.
        Await.result(writer.markEndOfStream());
        try {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance(i)));
            Assert.fail("Should have thrown");
        } catch (EndOfStreamException ex) {
        }
        BKAsyncLogReaderDLSN reader = ((BKAsyncLogReaderDLSN) (dlm.getAsyncLogReader(InitialDLSN)));
        LogRecord record = null;
        for (int j = 0; j < NUM_RECORDS; j++) {
            record = Await.result(reader.readNext());
            Assert.assertEquals((j + 1), record.getTransactionId());
        }
        try {
            record = Await.result(reader.readNext());
            Assert.fail("Should have thrown");
        } catch (EndOfStreamException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testMarkEndOfStreamAtBeginningOfSegment() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        Await.result(writer.markEndOfStream());
        try {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance(1)));
            Assert.fail("Should have thrown");
        } catch (EndOfStreamException ex) {
        }
        BKAsyncLogReaderDLSN reader = ((BKAsyncLogReaderDLSN) (dlm.getAsyncLogReader(InitialDLSN)));
        try {
            LogRecord record = Await.result(reader.readNext());
            Assert.fail("Should have thrown");
        } catch (EndOfStreamException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testBulkReadWaitingMoreRecords() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        FutureUtils.result(writer.write(DLMTestUtil.getLogRecordInstance(1L)));
        LogRecord controlRecord = DLMTestUtil.getLogRecordInstance(1L);
        controlRecord.setControl();
        FutureUtils.result(writer.write(controlRecord));
        BKAsyncLogReaderDLSN reader = ((BKAsyncLogReaderDLSN) (dlm.getAsyncLogReader(InitialDLSN)));
        Future<List<LogRecordWithDLSN>> bulkReadFuture = reader.readBulk(2, Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        Future<LogRecordWithDLSN> readFuture = reader.readNext();
        // write another records
        for (int i = 0; i < 5; i++) {
            long txid = 2L + i;
            FutureUtils.result(writer.write(DLMTestUtil.getLogRecordInstance(txid)));
            controlRecord = DLMTestUtil.getLogRecordInstance(txid);
            controlRecord.setControl();
            FutureUtils.result(writer.write(controlRecord));
        }
        List<LogRecordWithDLSN> bulkReadRecords = FutureUtils.result(bulkReadFuture);
        Assert.assertEquals(2, bulkReadRecords.size());
        Assert.assertEquals(1L, bulkReadRecords.get(0).getTransactionId());
        Assert.assertEquals(2L, bulkReadRecords.get(1).getTransactionId());
        for (LogRecordWithDLSN record : bulkReadRecords) {
            DLMTestUtil.verifyLogRecord(record);
        }
        LogRecordWithDLSN record = FutureUtils.result(readFuture);
        Assert.assertEquals(3L, record.getTransactionId());
        DLMTestUtil.verifyLogRecord(record);
        Utils.close(reader);
        writer.close();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testBulkReadNotWaitingMoreRecords() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        FutureUtils.result(writer.write(DLMTestUtil.getLogRecordInstance(1L)));
        LogRecord controlRecord = DLMTestUtil.getLogRecordInstance(1L);
        controlRecord.setControl();
        FutureUtils.result(writer.write(controlRecord));
        BKAsyncLogReaderDLSN reader = ((BKAsyncLogReaderDLSN) (dlm.getAsyncLogReader(InitialDLSN)));
        Future<List<LogRecordWithDLSN>> bulkReadFuture = reader.readBulk(2, 0, TimeUnit.MILLISECONDS);
        Future<LogRecordWithDLSN> readFuture = reader.readNext();
        List<LogRecordWithDLSN> bulkReadRecords = FutureUtils.result(bulkReadFuture);
        Assert.assertEquals(1, bulkReadRecords.size());
        Assert.assertEquals(1L, bulkReadRecords.get(0).getTransactionId());
        for (LogRecordWithDLSN record : bulkReadRecords) {
            DLMTestUtil.verifyLogRecord(record);
        }
        // write another records
        for (int i = 0; i < 5; i++) {
            long txid = 2L + i;
            FutureUtils.result(writer.write(DLMTestUtil.getLogRecordInstance(txid)));
            controlRecord = DLMTestUtil.getLogRecordInstance(txid);
            controlRecord.setControl();
            FutureUtils.result(writer.write(controlRecord));
        }
        LogRecordWithDLSN record = FutureUtils.result(readFuture);
        Assert.assertEquals(2L, record.getTransactionId());
        DLMTestUtil.verifyLogRecord(record);
        Utils.close(reader);
        writer.close();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testReadBrokenEntries() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(1);
        confLocal.setPositionGapDetectionEnabled(false);
        confLocal.setReadAheadSkipBrokenEntries(true);
        confLocal.setEIInjectReadAheadBrokenEntries(true);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 10;
        long txid = 1L;
        txid = TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, txid, false);
        AsyncLogReader reader = dlm.getAsyncLogReader(InvalidDLSN);
        // 3 segments, 10 records each, immediate flush, batch size 1, so just the first
        // record in each ledger is discarded, for 30 - 3 = 27 records.
        for (int i = 0; i < 27; i++) {
            LogRecordWithDLSN record = Await.result(reader.readNext());
            Assert.assertFalse((((record.getDlsn().getEntryId()) % 10) == 0));
        }
        Utils.close(reader);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testReadBrokenEntriesWithGapDetection() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(1);
        confLocal.setPositionGapDetectionEnabled(true);
        confLocal.setReadAheadSkipBrokenEntries(true);
        confLocal.setEIInjectReadAheadBrokenEntries(true);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 1;
        int numRecordsPerLogSegment = 100;
        long txid = 1L;
        txid = TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, txid, false);
        AsyncLogReader reader = dlm.getAsyncLogReader(InvalidDLSN);
        try {
            // 3 segments, 10 records each, immediate flush, batch size 1, so just the first
            // record in each ledger is discarded, for 30 - 3 = 27 records.
            for (int i = 0; i < 30; i++) {
                LogRecordWithDLSN record = Await.result(reader.readNext());
                Assert.assertFalse((((record.getDlsn().getEntryId()) % 10) == 0));
            }
            Assert.fail("should have thrown");
        } catch (DLIllegalStateException e) {
        }
        reader.asyncClose();
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testReadBrokenEntriesAndLargeBatchSize() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(5);
        confLocal.setPositionGapDetectionEnabled(false);
        confLocal.setReadAheadSkipBrokenEntries(true);
        confLocal.setEIInjectReadAheadBrokenEntries(true);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 1;
        int numRecordsPerLogSegment = 100;
        long txid = 1L;
        txid = TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, txid, false);
        AsyncLogReader reader = dlm.getAsyncLogReader(InvalidDLSN);
        // Every 10th record broken. Reading 5 at once, beginning from 0:
        // 1. range 0-4 will be corrupted and discarded
        // 2. ranges 1-5, 2-6, 3-7, 4-8, 5-9 will be ok
        // 3. ranges 6-10, 7-11, 8-12, 9-13 will be bad
        // And so on, so 5 records in each 10 will be discarded, for 50 good records.
        for (int i = 0; i < 50; i++) {
            LogRecordWithDLSN record = Await.result(reader.readNext());
            Assert.assertFalse((((record.getDlsn().getEntryId()) % 10) == 0));
        }
        Utils.close(reader);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testReadBrokenEntriesAndLargeBatchSizeCrossSegment() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setReadAheadWaitTime(10);
        confLocal.setReadAheadBatchSize(8);
        confLocal.setPositionGapDetectionEnabled(false);
        confLocal.setReadAheadSkipBrokenEntries(true);
        confLocal.setEIInjectReadAheadBrokenEntries(true);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        int numLogSegments = 3;
        int numRecordsPerLogSegment = 5;
        long txid = 1L;
        txid = TestAsyncReaderWriter.writeRecords(dlm, numLogSegments, numRecordsPerLogSegment, txid, false);
        AsyncLogReader reader = dlm.getAsyncLogReader(InvalidDLSN);
        // Every 10th record broken. Reading 8 at once, beginning from 0:
        // 1. range 0-7 will be corrupted and discarded
        // 2. range 1-8 will be good, but only contain 4 records
        // And so on for the next segment, so 4 records in each segment, for 12 good records
        for (int i = 0; i < 12; i++) {
            LogRecordWithDLSN record = Await.result(reader.readNext());
            Assert.assertFalse((((record.getDlsn().getEntryId()) % 10) == 0));
        }
        Utils.close(reader);
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testCreateLogStreamWithDifferentReplicationFactor() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        ConcurrentBaseConfiguration baseConf = new com.twitter.distributedlog.config.ConcurrentConstConfiguration(confLocal);
        DynamicDistributedLogConfiguration dynConf = new DynamicDistributedLogConfiguration(baseConf);
        dynConf.setProperty(BKDL_BOOKKEEPER_ENSEMBLE_SIZE, ((BKDL_BOOKKEEPER_ENSEMBLE_SIZE_DEFAULT) - 1));
        URI uri = createDLMURI(("/" + name));
        ensureURICreated(uri);
        DistributedLogNamespace namespace = com.twitter.distributedlog.namespace.DistributedLogNamespaceBuilder.newBuilder().conf(confLocal).uri(uri).build();
        // use the pool
        DistributedLogManager dlm = namespace.openLog((name + "-pool"));
        AsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        FutureUtils.result(writer.write(DLMTestUtil.getLogRecordInstance(1L)));
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        Assert.assertEquals(1, segments.size());
        long ledgerId = segments.get(0).getLedgerId();
        LedgerHandle lh = getReaderBKC().get().openLedgerNoRecovery(ledgerId, CRC32, confLocal.getBKDigestPW().getBytes(Charsets.UTF_8));
        LedgerMetadata metadata = BookKeeperAccessor.getLedgerMetadata(lh);
        Assert.assertEquals(BKDL_BOOKKEEPER_ENSEMBLE_SIZE_DEFAULT, metadata.getEnsembleSize());
        lh.close();
        Utils.close(writer);
        dlm.close();
        // use customized configuration
        dlm = namespace.openLog((name + "-custom"), Optional.<DistributedLogConfiguration>absent(), Optional.of(dynConf));
        writer = dlm.startAsyncLogSegmentNonPartitioned();
        FutureUtils.result(writer.write(DLMTestUtil.getLogRecordInstance(1L)));
        segments = dlm.getLogSegments();
        Assert.assertEquals(1, segments.size());
        ledgerId = segments.get(0).getLedgerId();
        lh = getReaderBKC().get().openLedgerNoRecovery(ledgerId, CRC32, confLocal.getBKDigestPW().getBytes(Charsets.UTF_8));
        metadata = BookKeeperAccessor.getLedgerMetadata(lh);
        Assert.assertEquals(((BKDL_BOOKKEEPER_ENSEMBLE_SIZE_DEFAULT) - 1), metadata.getEnsembleSize());
        lh.close();
        Utils.close(writer);
        dlm.close();
        namespace.close();
    }

    @Test(timeout = 60000)
    public void testWriteRecordSet() throws Exception {
        String name = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(testConf);
        confLocal.setOutputBufferSize(0);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        URI uri = createDLMURI(("/" + name));
        ensureURICreated(uri);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        List<Future<DLSN>> writeFutures = Lists.newArrayList();
        for (int i = 0; i < 5; i++) {
            LogRecord record = DLMTestUtil.getLogRecordInstance((1L + i));
            writeFutures.add(writer.write(record));
        }
        List<Future<DLSN>> recordSetFutures = Lists.newArrayList();
        // write another 5 records
        final LogRecordSet.Writer recordSetWriter = LogRecordSet.newWriter(4096, LZ4);
        for (int i = 0; i < 5; i++) {
            LogRecord record = DLMTestUtil.getLogRecordInstance((6L + i));
            Promise<DLSN> writePromise = new Promise<DLSN>();
            recordSetWriter.writeRecord(ByteBuffer.wrap(record.getPayload()), writePromise);
            recordSetFutures.add(writePromise);
        }
        final ByteBuffer recordSetBuffer = recordSetWriter.getBuffer();
        byte[] data = new byte[recordSetBuffer.remaining()];
        recordSetBuffer.get(data);
        LogRecord setRecord = new LogRecord(6L, data);
        setRecord.setRecordSet();
        Future<DLSN> writeRecordSetFuture = writer.write(setRecord);
        writeRecordSetFuture.addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
            @Override
            public void onSuccess(DLSN dlsn) {
                recordSetWriter.completeTransmit(dlsn.getLogSegmentSequenceNo(), dlsn.getEntryId(), dlsn.getSlotId());
            }

            @Override
            public void onFailure(Throwable cause) {
                recordSetWriter.abortTransmit(cause);
            }
        });
        writeFutures.add(writeRecordSetFuture);
        FutureUtils.result(writeRecordSetFuture);
        // write last 5 records
        for (int i = 0; i < 5; i++) {
            LogRecord record = DLMTestUtil.getLogRecordInstance((11L + i));
            Future<DLSN> writeFuture = writer.write(record);
            writeFutures.add(writeFuture);
            // make sure get log record count returns the right count
            if (i == 0) {
                FutureUtils.result(writeFuture);
                Assert.assertEquals(10, dlm.getLogRecordCount());
            }
        }
        List<DLSN> writeResults = FutureUtils.result(Future.collect(writeFutures));
        for (int i = 0; i < 5; i++) {
            assertEquals(new DLSN(1L, i, 0L), writeResults.get(i));
        }
        assertEquals(new DLSN(1L, 5L, 0L), writeResults.get(5));
        for (int i = 0; i < 5; i++) {
            assertEquals(new DLSN(1L, (6L + i), 0L), writeResults.get((6 + i)));
        }
        List<DLSN> recordSetWriteResults = Await.result(Future.collect(recordSetFutures));
        for (int i = 0; i < 5; i++) {
            assertEquals(new DLSN(1L, 5L, i), recordSetWriteResults.get(i));
        }
        FutureUtils.result(writer.flushAndCommit());
        DistributedLogConfiguration readConf1 = new DistributedLogConfiguration();
        readConf1.addConfiguration(confLocal);
        readConf1.setDeserializeRecordSetOnReads(true);
        DistributedLogManager readDLM1 = createNewDLM(readConf1, name);
        AsyncLogReader reader1 = readDLM1.getAsyncLogReader(InitialDLSN);
        for (int i = 0; i < 15; i++) {
            LogRecordWithDLSN record = FutureUtils.result(reader1.readNext());
            if (i < 5) {
                Assert.assertEquals(new DLSN(1L, i, 0L), record.getDlsn());
                Assert.assertEquals((1L + i), record.getTransactionId());
            } else
                if (i >= 10) {
                    Assert.assertEquals(new DLSN(1L, ((6L + i) - 10), 0L), record.getDlsn());
                    Assert.assertEquals(((11L + i) - 10), record.getTransactionId());
                } else {
                    Assert.assertEquals(new DLSN(1L, 5L, (i - 5)), record.getDlsn());
                    Assert.assertEquals(6L, record.getTransactionId());
                }

            Assert.assertEquals((i + 1), record.getPositionWithinLogSegment());
            Assert.assertArrayEquals(DLMTestUtil.generatePayload((i + 1)), record.getPayload());
        }
        DistributedLogConfiguration readConf2 = new DistributedLogConfiguration();
        readConf2.addConfiguration(confLocal);
        readConf2.setDeserializeRecordSetOnReads(false);
        DistributedLogManager readDLM2 = createNewDLM(readConf2, name);
        AsyncLogReader reader2 = readDLM2.getAsyncLogReader(InitialDLSN);
        for (int i = 0; i < 11; i++) {
            LogRecordWithDLSN record = FutureUtils.result(reader2.readNext());
            TestAsyncReaderWriter.LOG.info("Read record {}", record);
            if (i < 5) {
                Assert.assertEquals(new DLSN(1L, i, 0L), record.getDlsn());
                Assert.assertEquals((1L + i), record.getTransactionId());
                Assert.assertEquals((i + 1), record.getPositionWithinLogSegment());
                Assert.assertArrayEquals(DLMTestUtil.generatePayload((i + 1)), record.getPayload());
            } else
                if (i >= 6L) {
                    Assert.assertEquals(new DLSN(1L, ((6L + i) - 6), 0L), record.getDlsn());
                    Assert.assertEquals(((11L + i) - 6), record.getTransactionId());
                    Assert.assertEquals(((11 + i) - 6), record.getPositionWithinLogSegment());
                    Assert.assertArrayEquals(DLMTestUtil.generatePayload(((11L + i) - 6)), record.getPayload());
                } else {
                    Assert.assertEquals(new DLSN(1L, 5L, 0), record.getDlsn());
                    Assert.assertEquals(6L, record.getTransactionId());
                    Assert.assertEquals(6, record.getPositionWithinLogSegment());
                    Assert.assertTrue(record.isRecordSet());
                    Assert.assertEquals(5, LogRecordSet.numRecords(record));
                }

        }
    }
}

