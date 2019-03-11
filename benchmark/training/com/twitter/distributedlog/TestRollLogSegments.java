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
import CoreFeatureKeys.DISABLE_LOGSEGMENT_ROLLING;
import DLSN.InitialDLSN;
import FailpointUtils.FailPointActions.FailPointAction_Throw;
import FailpointUtils.FailPointName.FP_StartLogSegmentBeforeLedgerCreate;
import com.google.common.base.Charsets;
import com.twitter.distributedlog.annotations.DistributedLogAnnotations.FlakyTest;
import com.twitter.distributedlog.util.FailpointUtils;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.Utils;
import com.twitter.util.Await;
import com.twitter.util.Future;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.feature.SettableFeature;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRollLogSegments extends TestDistributedLogBase {
    static final Logger logger = LoggerFactory.getLogger(TestRollLogSegments.class);

    @Test(timeout = 60000)
    public void testDisableRollingLogSegments() throws Exception {
        String name = "distrlog-disable-rolling-log-segments";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        confLocal.setLogSegmentRollingIntervalMinutes(0);
        confLocal.setMaxLogSegmentBytes(40);
        int numEntries = 100;
        BKDistributedLogManager dlm = ((BKDistributedLogManager) (createNewDLM(confLocal, name)));
        BKAsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        SettableFeature disableLogSegmentRolling = ((SettableFeature) (dlm.getFeatureProvider().getFeature(DISABLE_LOGSEGMENT_ROLLING.name().toLowerCase())));
        disableLogSegmentRolling.set(true);
        final CountDownLatch latch = new CountDownLatch(numEntries);
        // send requests in parallel
        for (int i = 1; i <= numEntries; i++) {
            final int entryId = i;
            writer.write(DLMTestUtil.getLogRecordInstance(entryId)).addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
                @Override
                public void onSuccess(DLSN value) {
                    TestRollLogSegments.logger.info("Completed entry {} : {}.", entryId, value);
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable cause) {
                    // nope
                }
            });
        }
        latch.await();
        // make sure all ensure blocks were executed
        writer.closeAndComplete();
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        Assert.assertEquals(1, segments.size());
        dlm.close();
    }

    @Test(timeout = 600000)
    public void testLastDLSNInRollingLogSegments() throws Exception {
        final Map<Long, DLSN> lastDLSNs = new HashMap<Long, DLSN>();
        String name = "distrlog-lastdlsn-in-rolling-log-segments";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        confLocal.setLogSegmentRollingIntervalMinutes(0);
        confLocal.setMaxLogSegmentBytes(40);
        int numEntries = 100;
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        final CountDownLatch latch = new CountDownLatch(numEntries);
        // send requests in parallel to have outstanding requests
        for (int i = 1; i <= numEntries; i++) {
            final int entryId = i;
            Future<DLSN> writeFuture = writer.write(DLMTestUtil.getLogRecordInstance(entryId)).addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
                @Override
                public void onSuccess(DLSN value) {
                    TestRollLogSegments.logger.info("Completed entry {} : {}.", entryId, value);
                    synchronized(lastDLSNs) {
                        DLSN lastDLSN = lastDLSNs.get(value.getLogSegmentSequenceNo());
                        if ((null == lastDLSN) || ((lastDLSN.compareTo(value)) < 0)) {
                            lastDLSNs.put(value.getLogSegmentSequenceNo(), value);
                        }
                    }
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable cause) {
                }
            });
            if (i == 1) {
                // wait for first log segment created
                FutureUtils.result(writeFuture);
            }
        }
        latch.await();
        // make sure all ensure blocks were executed.
        writer.closeAndComplete();
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        TestRollLogSegments.logger.info("lastDLSNs after writes {} {}", lastDLSNs.size(), lastDLSNs);
        TestRollLogSegments.logger.info("segments after writes {} {}", segments.size(), segments);
        Assert.assertTrue(((segments.size()) >= 2));
        Assert.assertTrue(((lastDLSNs.size()) >= 2));
        Assert.assertEquals(lastDLSNs.size(), segments.size());
        for (LogSegmentMetadata segment : segments) {
            DLSN dlsnInMetadata = segment.getLastDLSN();
            DLSN dlsnSeen = lastDLSNs.get(segment.getLogSegmentSequenceNumber());
            Assert.assertNotNull(dlsnInMetadata);
            Assert.assertNotNull(dlsnSeen);
            if ((dlsnInMetadata.compareTo(dlsnSeen)) != 0) {
                TestRollLogSegments.logger.error("Last dlsn recorded in log segment {} is different from the one already seen {}.", dlsnInMetadata, dlsnSeen);
            }
            Assert.assertEquals(0, dlsnInMetadata.compareTo(dlsnSeen));
        }
        dlm.close();
    }

    @Test(timeout = 60000)
    public void testUnableToRollLogSegments() throws Exception {
        String name = "distrlog-unable-to-roll-log-segments";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        confLocal.setLogSegmentRollingIntervalMinutes(0);
        confLocal.setMaxLogSegmentBytes(1);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        long txId = 1L;
        // Create Log Segments
        Await.result(writer.write(DLMTestUtil.getLogRecordInstance(txId)));
        FailpointUtils.setFailpoint(FP_StartLogSegmentBeforeLedgerCreate, FailPointAction_Throw);
        try {
            // If we couldn't open new log segment, we should keep using the old one
            final int numRecords = 10;
            final CountDownLatch latch = new CountDownLatch(numRecords);
            for (int i = 0; i < numRecords; i++) {
                writer.write(DLMTestUtil.getLogRecordInstance((++txId))).addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
                    @Override
                    public void onSuccess(DLSN value) {
                        TestRollLogSegments.logger.info("Completed entry : {}.", value);
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Throwable cause) {
                        TestRollLogSegments.logger.error("Failed to write entries : ", cause);
                    }
                });
            }
            latch.await();
            writer.close();
            List<LogSegmentMetadata> segments = dlm.getLogSegments();
            TestRollLogSegments.logger.info("LogSegments: {}", segments);
            Assert.assertEquals(1, segments.size());
            long expectedTxID = 1L;
            LogReader reader = dlm.getInputStream(InitialDLSN);
            LogRecordWithDLSN record = reader.readNext(false);
            while (null != record) {
                DLMTestUtil.verifyLogRecord(record);
                Assert.assertEquals((expectedTxID++), record.getTransactionId());
                Assert.assertEquals(((record.getTransactionId()) - 1), record.getSequenceId());
                record = reader.readNext(false);
            } 
            Assert.assertEquals(12L, expectedTxID);
            reader.close();
            dlm.close();
        } finally {
            FailpointUtils.removeFailpoint(FP_StartLogSegmentBeforeLedgerCreate);
        }
    }

    @Test(timeout = 60000)
    public void testRollingLogSegments() throws Exception {
        TestRollLogSegments.logger.info("start testRollingLogSegments");
        String name = "distrlog-rolling-logsegments-hightraffic";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        confLocal.setLogSegmentRollingIntervalMinutes(0);
        confLocal.setMaxLogSegmentBytes(1);
        int numLogSegments = 10;
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKAsyncLogWriter writer = ((BKAsyncLogWriter) (dlm.startAsyncLogSegmentNonPartitioned()));
        final CountDownLatch latch = new CountDownLatch(numLogSegments);
        long startTime = System.currentTimeMillis();
        // send requests in parallel to have outstanding requests
        for (int i = 1; i <= numLogSegments; i++) {
            final int entryId = i;
            Future<DLSN> writeFuture = writer.write(DLMTestUtil.getLogRecordInstance(entryId)).addEventListener(new com.twitter.util.FutureEventListener<DLSN>() {
                @Override
                public void onSuccess(DLSN value) {
                    TestRollLogSegments.logger.info("Completed entry {} : {}.", entryId, value);
                    latch.countDown();
                }

                @Override
                public void onFailure(Throwable cause) {
                    TestRollLogSegments.logger.error("Failed to write entries : {}", cause);
                }
            });
            if (i == 1) {
                // wait for first log segment created
                FutureUtils.result(writeFuture);
            }
        }
        latch.await();
        TestRollLogSegments.logger.info("Took {} ms to completed all requests.", ((System.currentTimeMillis()) - startTime));
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        TestRollLogSegments.logger.info("LogSegments : {}", segments);
        Assert.assertTrue(((segments.size()) >= 2));
        TestRollLogSegments.ensureOnlyOneInprogressLogSegments(segments);
        int numSegmentsAfterAsyncWrites = segments.size();
        // writer should work after rolling log segments
        // there would be (numLogSegments/2) segments based on current rolling policy
        for (int i = 1; i <= numLogSegments; i++) {
            DLSN newDLSN = Await.result(writer.write(DLMTestUtil.getLogRecordInstance((numLogSegments + i))));
            TestRollLogSegments.logger.info("Completed entry {} : {}", (numLogSegments + i), newDLSN);
        }
        segments = dlm.getLogSegments();
        TestRollLogSegments.logger.info("LogSegments : {}", segments);
        Assert.assertEquals((numSegmentsAfterAsyncWrites + (numLogSegments / 2)), segments.size());
        TestRollLogSegments.ensureOnlyOneInprogressLogSegments(segments);
        writer.close();
        dlm.close();
    }

    @FlakyTest
    @Test(timeout = 60000)
    public void testCaughtUpReaderOnLogSegmentRolling() throws Exception {
        String name = "distrlog-caughtup-reader-on-logsegment-rolling";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setPeriodicFlushFrequencyMilliSeconds(0);
        confLocal.setImmediateFlushEnabled(false);
        confLocal.setOutputBufferSize(((4 * 1024) * 1024));
        confLocal.setTraceReadAheadMetadataChanges(true);
        confLocal.setEnsembleSize(1);
        confLocal.setWriteQuorumSize(1);
        confLocal.setAckQuorumSize(1);
        confLocal.setReadLACLongPollTimeout(99999999);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        BKSyncLogWriter writer = ((BKSyncLogWriter) (dlm.startLogSegmentNonPartitioned()));
        // 1) writer added 5 entries.
        final int numEntries = 5;
        for (int i = 1; i <= numEntries; i++) {
            writer.write(DLMTestUtil.getLogRecordInstance(i));
            writer.setReadyToFlush();
            writer.flushAndSync();
        }
        BKDistributedLogManager readDLM = ((BKDistributedLogManager) (createNewDLM(confLocal, name)));
        final BKAsyncLogReaderDLSN reader = ((BKAsyncLogReaderDLSN) (readDLM.getAsyncLogReader(InitialDLSN)));
        // 2) reader should be able to read 5 entries.
        for (long i = 1; i <= numEntries; i++) {
            LogRecordWithDLSN record = Await.result(reader.readNext());
            DLMTestUtil.verifyLogRecord(record);
            Assert.assertEquals(i, record.getTransactionId());
            Assert.assertEquals(((record.getTransactionId()) - 1), record.getSequenceId());
        }
        BKLogSegmentWriter perStreamWriter = writer.segmentWriter;
        BookKeeperClient bkc = readDLM.getReaderBKC();
        LedgerHandle readLh = bkc.get().openLedgerNoRecovery(getLedgerHandle(perStreamWriter).getId(), CRC32, TestDistributedLogBase.conf.getBKDigestPW().getBytes(Charsets.UTF_8));
        // Writer moved to lac = 9, while reader knows lac = 8 and moving to wait on 9
        checkAndWaitWriterReaderPosition(perStreamWriter, 9, reader, 9, readLh, 8);
        // write 6th record
        writer.write(DLMTestUtil.getLogRecordInstance((numEntries + 1)));
        writer.setReadyToFlush();
        // Writer moved to lac = 10, while reader knows lac = 9 and moving to wait on 10
        checkAndWaitWriterReaderPosition(perStreamWriter, 10, reader, 10, readLh, 9);
        // write records without commit to simulate similar failure cases
        writer.write(DLMTestUtil.getLogRecordInstance((numEntries + 2)));
        writer.setReadyToFlush();
        // Writer moved to lac = 11, while reader knows lac = 10 and moving to wait on 11
        checkAndWaitWriterReaderPosition(perStreamWriter, 11, reader, 11, readLh, 10);
        while (null == (reader.bkLedgerManager.readAheadWorker.getMetadataNotification())) {
            Thread.sleep(1000);
        } 
        TestRollLogSegments.logger.info("Waiting for long poll getting interrupted with metadata changed");
        // simulate a recovery without closing ledger causing recording wrong last dlsn
        BKLogWriteHandler writeHandler = writer.getCachedWriteHandler();
        writeHandler.completeAndCloseLogSegment(writeHandler.inprogressZNodeName(perStreamWriter.getLogSegmentId(), perStreamWriter.getStartTxId(), perStreamWriter.getLogSegmentSequenceNumber()), perStreamWriter.getLogSegmentSequenceNumber(), perStreamWriter.getLogSegmentId(), perStreamWriter.getStartTxId(), perStreamWriter.getLastTxId(), ((perStreamWriter.getPositionWithinLogSegment()) - 1), 9, 0);
        BKSyncLogWriter anotherWriter = ((BKSyncLogWriter) (dlm.startLogSegmentNonPartitioned()));
        anotherWriter.write(DLMTestUtil.getLogRecordInstance((numEntries + 3)));
        anotherWriter.setReadyToFlush();
        anotherWriter.flushAndSync();
        anotherWriter.closeAndComplete();
        for (long i = numEntries + 1; i <= (numEntries + 3); i++) {
            LogRecordWithDLSN record = Await.result(reader.readNext());
            DLMTestUtil.verifyLogRecord(record);
            Assert.assertEquals(i, record.getTransactionId());
        }
        Utils.close(reader);
        readDLM.close();
    }
}

