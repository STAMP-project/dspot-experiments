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
import LogSegmentMetadata.COMPARATOR;
import LogSegmentMetadata.DESC_COMPARATOR;
import com.google.common.base.Optional;
import com.twitter.distributedlog.exceptions.LogNotFoundException;
import com.twitter.distributedlog.exceptions.OwnershipAcquireFailedException;
import com.twitter.distributedlog.util.Utils;
import com.twitter.util.Await;
import com.twitter.util.Duration;
import com.twitter.util.Future;
import com.twitter.util.TimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link BKLogReadHandler}
 */
public class TestBKLogReadHandler extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestBKLogReadHandler.class);

    @Rule
    public TestName runtime = new TestName();

    @Test(timeout = 60000)
    public void testGetLedgerList() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegments(dlName, 3, 3);
        BKDistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = dlm.createReadHandler();
        List<LogSegmentMetadata> ledgerList = readHandler.getLedgerList(false, false, COMPARATOR, false);
        List<LogSegmentMetadata> ledgerList2 = readHandler.getFilteredLedgerList(true, false);
        List<LogSegmentMetadata> ledgerList3 = readHandler.getLedgerList(false, false, COMPARATOR, false);
        Assert.assertEquals(3, ledgerList.size());
        Assert.assertEquals(3, ledgerList2.size());
        Assert.assertEquals(3, ledgerList3.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(ledgerList3.get(i), ledgerList2.get(i));
        }
    }

    @Test(timeout = 60000)
    public void testForceGetLedgerList() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegments(dlName, 3, 3);
        BKDistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = dlm.createReadHandler();
        List<LogSegmentMetadata> ledgerList = readHandler.getLedgerList(true, false, COMPARATOR, false);
        final AtomicReference<List<LogSegmentMetadata>> resultHolder = new AtomicReference<List<LogSegmentMetadata>>(null);
        final CountDownLatch latch = new CountDownLatch(1);
        readHandler.asyncGetLedgerList(COMPARATOR, null, new BookkeeperInternalCallbacks.GenericCallback<List<LogSegmentMetadata>>() {
            @Override
            public void operationComplete(int rc, List<LogSegmentMetadata> result) {
                resultHolder.set(result);
                latch.countDown();
            }
        });
        latch.await();
        List<LogSegmentMetadata> newLedgerList = resultHolder.get();
        Assert.assertNotNull(newLedgerList);
        TestBKLogReadHandler.LOG.info("Force sync get list : {}", ledgerList);
        TestBKLogReadHandler.LOG.info("Async get list : {}", newLedgerList);
        Assert.assertEquals(3, ledgerList.size());
        Assert.assertEquals(3, newLedgerList.size());
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals(ledgerList.get(i), newLedgerList.get(i));
        }
    }

    @Test(timeout = 60000)
    public void testGetFilteredLedgerListInWriteHandler() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegments(dlName, 11, 3);
        BKDistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        // Get full list.
        BKLogWriteHandler writeHandler0 = dlm.createWriteHandler(false);
        List<LogSegmentMetadata> cachedFullLedgerList = writeHandler0.getCachedLogSegments(DESC_COMPARATOR);
        Assert.assertTrue(((cachedFullLedgerList.size()) <= 1));
        List<LogSegmentMetadata> fullLedgerList = writeHandler0.getFullLedgerListDesc(false, false);
        Assert.assertEquals(11, fullLedgerList.size());
        // Get filtered list.
        BKLogWriteHandler writeHandler1 = dlm.createWriteHandler(false);
        List<LogSegmentMetadata> filteredLedgerListDesc = writeHandler1.getFilteredLedgerListDesc(false, false);
        Assert.assertEquals(1, filteredLedgerListDesc.size());
        Assert.assertEquals(fullLedgerList.get(0), filteredLedgerListDesc.get(0));
        List<LogSegmentMetadata> filteredLedgerList = writeHandler1.getFilteredLedgerList(false, false);
        Assert.assertEquals(1, filteredLedgerList.size());
        Assert.assertEquals(fullLedgerList.get(0), filteredLedgerList.get(0));
    }

    @Test(timeout = 60000)
    public void testGetFirstDLSNWithOpenLedger() throws Exception {
        String dlName = runtime.getMethodName();
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestDistributedLogBase.conf);
        confLocal.setImmediateFlushEnabled(true);
        confLocal.setOutputBufferSize(0);
        int numEntriesPerSegment = 100;
        DistributedLogManager dlm1 = createNewDLM(confLocal, dlName);
        long txid = 1;
        ArrayList<Future<DLSN>> futures = new ArrayList<Future<DLSN>>(numEntriesPerSegment);
        AsyncLogWriter out = dlm1.startAsyncLogSegmentNonPartitioned();
        for (int eid = 0; eid < numEntriesPerSegment; ++eid) {
            futures.add(out.write(DLMTestUtil.getLogRecordInstance(txid)));
            ++txid;
        }
        for (Future<DLSN> future : futures) {
            Await.result(future);
        }
        BKLogReadHandler readHandler = createReadHandler();
        DLSN last = dlm1.getLastDLSN();
        Assert.assertEquals(new DLSN(1, 99, 0), last);
        DLSN first = Await.result(dlm1.getFirstDLSNAsync());
        Assert.assertEquals(new DLSN(1, 0, 0), first);
        Utils.close(out);
    }

    @Test(timeout = 60000)
    public void testGetFirstDLSNNoLogSegments() throws Exception {
        String dlName = runtime.getMethodName();
        BKDistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = dlm.createReadHandler();
        Future<LogRecordWithDLSN> futureRecord = readHandler.asyncGetFirstLogRecord();
        try {
            Await.result(futureRecord);
            Assert.fail("should have thrown exception");
        } catch (LogNotFoundException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testGetFirstDLSNWithLogSegments() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegments(dlName, 3, 3);
        BKDistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = dlm.createReadHandler();
        Future<LogRecordWithDLSN> futureRecord = readHandler.asyncGetFirstLogRecord();
        try {
            LogRecordWithDLSN record = Await.result(futureRecord);
            Assert.assertEquals(new DLSN(1, 0, 0), record.getDlsn());
        } catch (Exception ex) {
            Assert.fail(("should not have thrown exception: " + ex));
        }
    }

    @Test(timeout = 60000)
    public void testGetFirstDLSNAfterCleanTruncation() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 3, 10);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        AsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        Future<Boolean> futureSuccess = writer.truncate(new DLSN(2, 0, 0));
        Boolean success = Await.result(futureSuccess);
        Assert.assertTrue(success);
        Future<LogRecordWithDLSN> futureRecord = readHandler.asyncGetFirstLogRecord();
        LogRecordWithDLSN record = Await.result(futureRecord);
        Assert.assertEquals(new DLSN(2, 0, 0), record.getDlsn());
    }

    @Test(timeout = 60000)
    public void testGetFirstDLSNAfterPartialTruncation() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 3, 10);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        AsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        // Only truncates at ledger boundary.
        Future<Boolean> futureSuccess = writer.truncate(new DLSN(2, 5, 0));
        Boolean success = Await.result(futureSuccess);
        Assert.assertTrue(success);
        Future<LogRecordWithDLSN> futureRecord = readHandler.asyncGetFirstLogRecord();
        LogRecordWithDLSN record = Await.result(futureRecord);
        Assert.assertEquals(new DLSN(2, 0, 0), record.getDlsn());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountEmptyLedger() throws Exception {
        String dlName = runtime.getMethodName();
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(InitialDLSN);
        try {
            Await.result(count);
            Assert.fail("log is empty, should have returned log empty ex");
        } catch (LogNotFoundException ex) {
        }
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountTotalCount() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 11, 3);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(InitialDLSN);
        Assert.assertEquals(33, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountAtLedgerBoundary() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 11, 3);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(2, 0, 0));
        Assert.assertEquals(30, Await.result(count).longValue());
        count = readHandler.asyncGetLogRecordCount(new DLSN(3, 0, 0));
        Assert.assertEquals(27, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountPastEnd() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 11, 3);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(12, 0, 0));
        Assert.assertEquals(0, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountLastRecord() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 11, 3);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(11, 2, 0));
        Assert.assertEquals(1, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountInteriorRecords() throws Exception {
        String dlName = runtime.getMethodName();
        prepareLogSegmentsNonPartitioned(dlName, 5, 10);
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, dlName);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(3, 5, 0));
        Assert.assertEquals(25, Await.result(count).longValue());
        count = readHandler.asyncGetLogRecordCount(new DLSN(2, 5, 0));
        Assert.assertEquals(35, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountWithControlRecords() throws Exception {
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, runtime.getMethodName());
        long txid = 1;
        txid += DLMTestUtil.generateLogSegmentNonPartitioned(dlm, 5, 5, txid);
        txid += DLMTestUtil.generateLogSegmentNonPartitioned(dlm, 0, 10, txid);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(1, 0, 0));
        Assert.assertEquals(15, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountWithAllControlRecords() throws Exception {
        DistributedLogManager dlm = createNewDLM(TestDistributedLogBase.conf, runtime.getMethodName());
        long txid = 1;
        txid += DLMTestUtil.generateLogSegmentNonPartitioned(dlm, 5, 0, txid);
        txid += DLMTestUtil.generateLogSegmentNonPartitioned(dlm, 10, 0, txid);
        BKLogReadHandler readHandler = createReadHandler();
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(1, 0, 0));
        Assert.assertEquals(0, Await.result(count).longValue());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountWithSingleInProgressLedger() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        AsyncLogWriter out = bkdlm.startAsyncLogSegmentNonPartitioned();
        int txid = 1;
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        BKLogReadHandler readHandler = bkdlm.createReadHandler();
        List<LogSegmentMetadata> ledgerList = readHandler.getLedgerList(false, false, COMPARATOR, false);
        Assert.assertEquals(1, ledgerList.size());
        Assert.assertTrue(ledgerList.get(0).isInProgress());
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(1, 0, 0));
        Assert.assertEquals(2, Await.result(count).longValue());
        Utils.close(out);
    }

    @Test(timeout = 60000)
    public void testGetLogRecordCountWithCompletedAndInprogressLedgers() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        long txid = 1;
        txid += DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, txid);
        AsyncLogWriter out = bkdlm.startAsyncLogSegmentNonPartitioned();
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        BKLogReadHandler readHandler = bkdlm.createReadHandler();
        List<LogSegmentMetadata> ledgerList = readHandler.getLedgerList(false, false, COMPARATOR, false);
        Assert.assertEquals(2, ledgerList.size());
        Assert.assertFalse(ledgerList.get(0).isInProgress());
        Assert.assertTrue(ledgerList.get(1).isInProgress());
        Future<Long> count = null;
        count = readHandler.asyncGetLogRecordCount(new DLSN(1, 0, 0));
        Assert.assertEquals(7, Await.result(count).longValue());
        Utils.close(out);
    }

    @Test(timeout = 60000)
    public void testLockStreamWithMissingLog() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        BKLogReadHandler readHandler = bkdlm.createReadHandler();
        try {
            Await.result(readHandler.lockStream());
            Assert.fail("Should fail lock stream if log not found");
        } catch (LogNotFoundException ex) {
        }
        BKLogReadHandler subscriberReadHandler = bkdlm.createReadHandler(Optional.of("test-subscriber"));
        try {
            Await.result(subscriberReadHandler.lockStream());
            Assert.fail("Subscriber should fail lock stream if log not found");
        } catch (LogNotFoundException ex) {
            // expected
        }
    }

    @Test(timeout = 60000)
    public void testLockStreamDifferentSubscribers() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1);
        BKLogReadHandler readHandler = bkdlm.createReadHandler();
        Await.result(readHandler.lockStream());
        // two subscribers could lock stream in parallel
        BKDistributedLogManager bkdlm10 = createNewDLM(TestDistributedLogBase.conf, streamName);
        BKLogReadHandler s10Handler = bkdlm10.createReadHandler(Optional.of("s1"));
        Await.result(s10Handler.lockStream());
        BKDistributedLogManager bkdlm20 = createNewDLM(TestDistributedLogBase.conf, streamName);
        BKLogReadHandler s20Handler = bkdlm20.createReadHandler(Optional.of("s2"));
        Await.result(s20Handler.lockStream());
        readHandler.asyncClose();
        bkdlm.close();
        s10Handler.asyncClose();
        bkdlm10.close();
        s20Handler.asyncClose();
        bkdlm20.close();
    }

    @Test(timeout = 60000)
    public void testLockStreamSameSubscriber() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1);
        BKLogReadHandler readHandler = bkdlm.createReadHandler();
        Await.result(readHandler.lockStream());
        // same subscrbiers couldn't lock stream in parallel
        BKDistributedLogManager bkdlm10 = createNewDLM(TestDistributedLogBase.conf, streamName);
        BKLogReadHandler s10Handler = bkdlm10.createReadHandler(Optional.of("s1"));
        Await.result(s10Handler.lockStream());
        BKDistributedLogManager bkdlm11 = createNewDLM(TestDistributedLogBase.conf, streamName);
        BKLogReadHandler s11Handler = bkdlm11.createReadHandler(Optional.of("s1"));
        try {
            Await.result(s11Handler.lockStream(), Duration.apply(10000, TimeUnit.MILLISECONDS));
            Assert.fail("Should fail lock stream using same subscriber id");
        } catch (OwnershipAcquireFailedException oafe) {
            // expected
        } catch (TimeoutException te) {
            // expected.
        }
        readHandler.asyncClose();
        bkdlm.close();
        s10Handler.asyncClose();
        bkdlm10.close();
        s11Handler.asyncClose();
        bkdlm11.close();
    }
}

