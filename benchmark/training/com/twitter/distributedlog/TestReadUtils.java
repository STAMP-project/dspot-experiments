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


import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.Utils;
import com.twitter.util.Await;
import com.twitter.util.Future;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test {@link ReadUtils}
 */
public class TestReadUtils extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestReadUtils.class);

    @Rule
    public TestName runtime = new TestName();

    @Test(timeout = 60000)
    public void testForwardScanFirstRecord() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1);
        DLSN dlsn = new DLSN(1, 0, 0);
        Future<LogRecordWithDLSN> futureLogrec = getFirstGreaterThanRecord(bkdlm, 0, dlsn);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals("should be an exact match", dlsn, logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testForwardScanNotFirstRecord() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1);
        DLSN dlsn = new DLSN(1, 1, 0);
        Future<LogRecordWithDLSN> futureLogrec = getFirstGreaterThanRecord(bkdlm, 0, dlsn);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals("should be an exact match", dlsn, logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testForwardScanValidButNonExistentRecord() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1);
        DLSN dlsn = new DLSN(1, 0, 1);
        Future<LogRecordWithDLSN> futureLogrec = getFirstGreaterThanRecord(bkdlm, 0, dlsn);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(new DLSN(1, 1, 0), logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testForwardScanForRecordAfterLedger() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* user recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1);
        DLSN dlsn = new DLSN(2, 0, 0);
        Future<LogRecordWithDLSN> futureLogrec = getFirstGreaterThanRecord(bkdlm, 0, dlsn);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(null, logrec);
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testForwardScanForRecordBeforeLedger() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        long txid = 1;
        txid += /* user recs */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, txid);
        txid += /* user recs */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, txid);
        txid += /* user recs */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, txid);
        DLSN dlsn = new DLSN(1, 3, 0);
        Future<LogRecordWithDLSN> futureLogrec = getFirstGreaterThanRecord(bkdlm, 1, dlsn);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(new DLSN(2, 0, 0), logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testForwardScanControlRecord() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 5, 5, 1);
        DLSN dlsn = new DLSN(1, 3, 0);
        Future<LogRecordWithDLSN> futureLogrec = getFirstGreaterThanRecord(bkdlm, 0, dlsn);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(new DLSN(1, 5, 0), logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testGetLastRecordUserRecord() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 5, 5, 1);
        Future<LogRecordWithDLSN> futureLogrec = getLastUserRecord(bkdlm, 0);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(new DLSN(1, 9, 0), logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testGetLastRecordControlRecord() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        AsyncLogWriter out = bkdlm.startAsyncLogSegmentNonPartitioned();
        int txid = 1;
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), false)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), true)));
        Await.result(out.write(DLMTestUtil.getLargeLogRecordInstance((txid++), true)));
        Utils.close(out);
        Future<LogRecordWithDLSN> futureLogrec = getLastUserRecord(bkdlm, 0);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(new DLSN(1, 2, 0), logrec.getDlsn());
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testGetLastRecordAllControlRecords() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = ((BKDistributedLogManager) (createNewDLM(TestDistributedLogBase.conf, streamName)));
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 5, 0, 1);
        Future<LogRecordWithDLSN> futureLogrec = getLastUserRecord(bkdlm, 0);
        LogRecordWithDLSN logrec = Await.result(futureLogrec);
        Assert.assertEquals(null, logrec);
        bkdlm.close();
    }

    @Test(timeout = 60000)
    public void testGetEntriesToSearch() throws Exception {
        Assert.assertTrue(ReadUtils.getEntriesToSearch(2L, 1L, 10).isEmpty());
        Assert.assertEquals(Lists.newArrayList(1L), ReadUtils.getEntriesToSearch(1L, 1L, 10));
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L), ReadUtils.getEntriesToSearch(1L, 10L, 10));
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L), ReadUtils.getEntriesToSearch(1L, 9L, 10));
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L), ReadUtils.getEntriesToSearch(1L, 8L, 10));
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 11L), ReadUtils.getEntriesToSearch(1L, 11L, 10));
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 12L), ReadUtils.getEntriesToSearch(1L, 12L, 10));
    }

    @Test(timeout = 60000)
    public void testGetEntriesToSearchByTxnId() throws Exception {
        LogRecordWithDLSN firstRecord = DLMTestUtil.getLogRecordWithDLSNInstance(new DLSN(1L, 0L, 0L), 999L);
        LogRecordWithDLSN secondRecord = DLMTestUtil.getLogRecordWithDLSNInstance(new DLSN(1L, 10L, 0L), 99L);
        LogRecordWithDLSN thirdRecord = DLMTestUtil.getLogRecordWithDLSNInstance(new DLSN(1L, 100L, 0L), 1099L);
        // out-of-order sequence
        Assert.assertTrue(ReadUtils.getEntriesToSearch(888L, firstRecord, secondRecord, 10).isEmpty());
        // same transaction id
        Assert.assertTrue(ReadUtils.getEntriesToSearch(888L, firstRecord, firstRecord, 10).isEmpty());
        // small nways (nways = 2)
        Assert.assertEquals(2, ReadUtils.getEntriesToSearch(888L, firstRecord, thirdRecord, 2).size());
        // small nways with equal transaction id
        Assert.assertEquals(3, ReadUtils.getEntriesToSearch(1099L, firstRecord, thirdRecord, 2).size());
        LogRecordWithDLSN record1 = DLMTestUtil.getLogRecordWithDLSNInstance(new DLSN(1L, 0L, 0L), 88L);
        LogRecordWithDLSN record2 = DLMTestUtil.getLogRecordWithDLSNInstance(new DLSN(1L, 12L, 0L), 888L);
        LogRecordWithDLSN record3 = DLMTestUtil.getLogRecordWithDLSNInstance(new DLSN(1L, 12L, 0L), 999L);
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 10L, 11L), ReadUtils.getEntriesToSearch(888L, record1, record2, 10));
        Assert.assertEquals(Lists.newArrayList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 11L), ReadUtils.getEntriesToSearch(888L, record1, record3, 10));
    }

    @Test(timeout = 60000)
    public void testGetLogRecordNotLessThanTxIdWithGreaterTxId() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 1, 1);
        Optional<LogRecordWithDLSN> result = FutureUtils.result(getLogRecordNotLessThanTxId(bkdlm, 0, 999L));
        Assert.assertFalse(result.isPresent());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordNotLessThanTxIdWithLessTxId() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 1, 999L);
        Optional<LogRecordWithDLSN> result = FutureUtils.result(getLogRecordNotLessThanTxId(bkdlm, 0, 99L));
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(999L, result.get().getTransactionId());
        Assert.assertEquals(0L, result.get().getDlsn().getEntryId());
        Assert.assertEquals(0L, result.get().getDlsn().getSlotId());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordNotLessThanTxIdOnSmallSegment() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 5, 1L);
        Optional<LogRecordWithDLSN> result = FutureUtils.result(getLogRecordNotLessThanTxId(bkdlm, 0, 3L));
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(3L, result.get().getTransactionId());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordNotLessThanTxIdOnLargeSegment() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 100, 1L);
        Optional<LogRecordWithDLSN> result = FutureUtils.result(getLogRecordNotLessThanTxId(bkdlm, 0, 9L));
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(9L, result.get().getTransactionId());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordGreaterThanTxIdOnLargeSegment() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        /* control recs */
        /* txid */
        DLMTestUtil.generateLogSegmentNonPartitioned(bkdlm, 0, 100, 1L, 3L);
        Optional<LogRecordWithDLSN> result = FutureUtils.result(getLogRecordNotLessThanTxId(bkdlm, 0, 23L));
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(25L, result.get().getTransactionId());
    }

    @Test(timeout = 60000)
    public void testGetLogRecordGreaterThanTxIdOnSameTxId() throws Exception {
        String streamName = runtime.getMethodName();
        BKDistributedLogManager bkdlm = createNewDLM(TestDistributedLogBase.conf, streamName);
        AsyncLogWriter out = bkdlm.startAsyncLogSegmentNonPartitioned();
        long txid = 1L;
        for (int i = 0; i < 10; ++i) {
            LogRecord record = DLMTestUtil.getLargeLogRecordInstance(txid);
            Await.result(out.write(record));
            txid += 1;
        }
        long txidToSearch = txid;
        for (int i = 0; i < 10; ++i) {
            LogRecord record = DLMTestUtil.getLargeLogRecordInstance(txidToSearch);
            Await.result(out.write(record));
        }
        for (int i = 0; i < 10; ++i) {
            LogRecord record = DLMTestUtil.getLargeLogRecordInstance(txid);
            Await.result(out.write(record));
            txid += 1;
        }
        Utils.close(out);
        Optional<LogRecordWithDLSN> result = FutureUtils.result(getLogRecordNotLessThanTxId(bkdlm, 0, txidToSearch));
        Assert.assertTrue(result.isPresent());
        Assert.assertEquals(10L, result.get().getDlsn().getEntryId());
    }
}

