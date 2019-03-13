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


import DLSN.InvalidDLSN;
import LogSegmentMetadata.LogSegmentMetadataVersion.VERSION_V2_LEDGER_SEQNO.value;
import TruncationStatus.PARTIALLY_TRUNCATED;
import TruncationStatus.TRUNCATED;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.Utils;
import com.twitter.util.Await;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestTruncate extends TestDistributedLogBase {
    static final Logger LOG = LoggerFactory.getLogger(TestTruncate.class);

    protected static DistributedLogConfiguration conf = new DistributedLogConfiguration().setLockTimeout(10).setOutputBufferSize(0).setPeriodicFlushFrequencyMilliSeconds(10).setSchedulerShutdownTimeoutMs(0).setDLLedgerMetadataLayoutVersion(value);

    @Test(timeout = 60000)
    public void testPurgeLogs() throws Exception {
        String name = "distrlog-purge-logs";
        URI uri = createDLMURI(("/" + name));
        populateData(new HashMap<Long, DLSN>(), TestTruncate.conf, name, 10, 10, false);
        DistributedLogManager distributedLogManager = createNewDLM(TestTruncate.conf, name);
        List<LogSegmentMetadata> segments = distributedLogManager.getLogSegments();
        TestTruncate.LOG.info("Segments before modifying completion time : {}", segments);
        ZooKeeperClient zkc = TestZooKeeperClientBuilder.newBuilder(TestTruncate.conf).uri(uri).build();
        // Update completion time of first 5 segments
        long newTimeMs = (System.currentTimeMillis()) - (((60 * 60) * 1000) * 2);
        for (int i = 0; i < 5; i++) {
            LogSegmentMetadata segment = segments.get(i);
            TestTruncate.updateCompletionTime(zkc, segment, (newTimeMs + i));
        }
        zkc.close();
        segments = distributedLogManager.getLogSegments();
        TestTruncate.LOG.info("Segments after modifying completion time : {}", segments);
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestTruncate.conf);
        confLocal.setRetentionPeriodHours(1);
        confLocal.setExplicitTruncationByApplication(false);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        AsyncLogWriter writer = dlm.startAsyncLogSegmentNonPartitioned();
        long txid = 1 + (10 * 10);
        for (int j = 1; j <= 10; j++) {
            Await.result(writer.write(DLMTestUtil.getLogRecordInstance((txid++))));
        }
        // to make sure the truncation task is executed
        DLSN lastDLSN = Await.result(dlm.getLastDLSNAsync());
        TestTruncate.LOG.info("Get last dlsn of stream {} : {}", name, lastDLSN);
        Assert.assertEquals(6, distributedLogManager.getLogSegments().size());
        Utils.close(writer);
        dlm.close();
        distributedLogManager.close();
    }

    @Test
    public void testTruncation() throws Exception {
        String name = "distrlog-truncation";
        long txid = 1;
        Map<Long, DLSN> txid2DLSN = new HashMap<Long, DLSN>();
        Pair<DistributedLogManager, AsyncLogWriter> pair = populateData(txid2DLSN, TestTruncate.conf, name, 4, 10, true);
        Thread.sleep(1000);
        // delete invalid dlsn
        Assert.assertFalse(Await.result(pair.getRight().truncate(InvalidDLSN)));
        verifyEntries(name, 1, 1, (5 * 10));
        for (int i = 1; i <= 4; i++) {
            int txn = ((i - 1) * 10) + i;
            DLSN dlsn = txid2DLSN.get(((long) (txn)));
            Assert.assertTrue(Await.result(pair.getRight().truncate(dlsn)));
            verifyEntries(name, 1, (((i - 1) * 10) + 1), (((5 - i) + 1) * 10));
        }
        // Delete higher dlsn
        int txn = 43;
        DLSN dlsn = txid2DLSN.get(((long) (txn)));
        Assert.assertTrue(Await.result(pair.getRight().truncate(dlsn)));
        verifyEntries(name, 1, 31, 20);
        Utils.close(pair.getRight());
        pair.getLeft().close();
    }

    @Test
    public void testExplicitTruncation() throws Exception {
        String name = "distrlog-truncation-explicit";
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.loadConf(TestTruncate.conf);
        confLocal.setExplicitTruncationByApplication(true);
        Map<Long, DLSN> txid2DLSN = new HashMap<Long, DLSN>();
        Pair<DistributedLogManager, AsyncLogWriter> pair = populateData(txid2DLSN, confLocal, name, 4, 10, true);
        Thread.sleep(1000);
        for (int i = 1; i <= 4; i++) {
            int txn = ((i - 1) * 10) + i;
            DLSN dlsn = txid2DLSN.get(((long) (txn)));
            Assert.assertTrue(Await.result(pair.getRight().truncate(dlsn)));
            verifyEntries(name, 1, (((i - 1) * 10) + 1), (((5 - i) + 1) * 10));
        }
        // Delete higher dlsn
        int txn = 43;
        DLSN dlsn = txid2DLSN.get(((long) (txn)));
        Assert.assertTrue(Await.result(pair.getRight().truncate(dlsn)));
        verifyEntries(name, 1, 31, 20);
        Utils.close(pair.getRight());
        pair.getLeft().close();
        // Try force truncation
        BKDistributedLogManager dlm = ((BKDistributedLogManager) (createNewDLM(confLocal, name)));
        BKLogWriteHandler handler = dlm.createWriteHandler(true);
        FutureUtils.result(handler.purgeLogSegmentsOlderThanTxnId(Integer.MAX_VALUE));
        verifyEntries(name, 1, 31, 20);
    }

    @Test(timeout = 60000)
    public void testOnlyPurgeSegmentsBeforeNoneFullyTruncatedSegment() throws Exception {
        String name = "distrlog-only-purge-segments-before-none-fully-truncated-segment";
        URI uri = createDLMURI(("/" + name));
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(TestTruncate.conf);
        confLocal.setExplicitTruncationByApplication(true);
        // populate data
        populateData(new HashMap<Long, DLSN>(), confLocal, name, 4, 10, false);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        TestTruncate.LOG.info("Segments before modifying segment status : {}", segments);
        ZooKeeperClient zkc = TestZooKeeperClientBuilder.newBuilder(TestTruncate.conf).uri(uri).build();
        TestTruncate.setTruncationStatus(zkc, segments.get(0), PARTIALLY_TRUNCATED);
        for (int i = 1; i < 4; i++) {
            LogSegmentMetadata segment = segments.get(i);
            TestTruncate.setTruncationStatus(zkc, segment, TRUNCATED);
        }
        List<LogSegmentMetadata> segmentsAfterTruncated = dlm.getLogSegments();
        dlm.purgeLogsOlderThan(999999);
        List<LogSegmentMetadata> newSegments = dlm.getLogSegments();
        TestTruncate.LOG.info("Segments after purge segments older than 999999 : {}", newSegments);
        Assert.assertArrayEquals(segmentsAfterTruncated.toArray(new LogSegmentMetadata[segmentsAfterTruncated.size()]), newSegments.toArray(new LogSegmentMetadata[newSegments.size()]));
        dlm.close();
        // Update completion time of all 4 segments
        long newTimeMs = (System.currentTimeMillis()) - (((60 * 60) * 1000) * 10);
        for (int i = 0; i < 4; i++) {
            LogSegmentMetadata segment = newSegments.get(i);
            TestTruncate.updateCompletionTime(zkc, segment, (newTimeMs + i));
        }
        DistributedLogConfiguration newConf = new DistributedLogConfiguration();
        newConf.addConfiguration(confLocal);
        newConf.setRetentionPeriodHours(1);
        DistributedLogManager newDLM = createNewDLM(newConf, name);
        AsyncLogWriter newWriter = newDLM.startAsyncLogSegmentNonPartitioned();
        long txid = 1 + (4 * 10);
        for (int j = 1; j <= 10; j++) {
            Await.result(newWriter.write(DLMTestUtil.getLogRecordInstance((txid++))));
        }
        // to make sure the truncation task is executed
        DLSN lastDLSN = Await.result(newDLM.getLastDLSNAsync());
        TestTruncate.LOG.info("Get last dlsn of stream {} : {}", name, lastDLSN);
        Assert.assertEquals(5, newDLM.getLogSegments().size());
        Utils.close(newWriter);
        newDLM.close();
        zkc.close();
    }

    @Test(timeout = 60000)
    public void testPartiallyTruncateTruncatedSegments() throws Exception {
        String name = "distrlog-partially-truncate-truncated-segments";
        URI uri = createDLMURI(("/" + name));
        DistributedLogConfiguration confLocal = new DistributedLogConfiguration();
        confLocal.addConfiguration(TestTruncate.conf);
        confLocal.setExplicitTruncationByApplication(true);
        // populate
        Map<Long, DLSN> dlsnMap = new HashMap<Long, DLSN>();
        populateData(dlsnMap, confLocal, name, 4, 10, false);
        DistributedLogManager dlm = createNewDLM(confLocal, name);
        List<LogSegmentMetadata> segments = dlm.getLogSegments();
        TestTruncate.LOG.info("Segments before modifying segment status : {}", segments);
        ZooKeeperClient zkc = TestZooKeeperClientBuilder.newBuilder(TestTruncate.conf).uri(uri).build();
        for (int i = 0; i < 4; i++) {
            LogSegmentMetadata segment = segments.get(i);
            TestTruncate.setTruncationStatus(zkc, segment, TRUNCATED);
        }
        List<LogSegmentMetadata> newSegments = dlm.getLogSegments();
        TestTruncate.LOG.info("Segments after changing truncation status : {}", newSegments);
        dlm.close();
        DistributedLogManager newDLM = createNewDLM(confLocal, name);
        AsyncLogWriter newWriter = newDLM.startAsyncLogSegmentNonPartitioned();
        Await.result(newWriter.truncate(dlsnMap.get(15L)));
        List<LogSegmentMetadata> newSegments2 = newDLM.getLogSegments();
        Assert.assertArrayEquals(newSegments.toArray(new LogSegmentMetadata[4]), newSegments2.toArray(new LogSegmentMetadata[4]));
        Utils.close(newWriter);
        newDLM.close();
        zkc.close();
    }
}

