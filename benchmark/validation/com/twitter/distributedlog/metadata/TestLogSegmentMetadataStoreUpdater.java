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
package com.twitter.distributedlog.metadata;


import CreateMode.PERSISTENT;
import LogSegmentMetadata.LEDGER_METADATA_CURRENT_LAYOUT_VERSION;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.twitter.distributedlog.DLMTestUtil;
import com.twitter.distributedlog.DLSN;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.LogRecordWithDLSN;
import com.twitter.distributedlog.LogSegmentMetadata;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import com.twitter.distributedlog.logsegment.LogSegmentMetadataStore;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestLogSegmentMetadataStoreUpdater extends ZooKeeperClusterTestCase {
    static final Logger LOG = LoggerFactory.getLogger(TestLogSegmentMetadataStoreUpdater.class);

    private ZooKeeperClient zkc;

    private OrderedScheduler scheduler;

    private LogSegmentMetadataStore metadataStore;

    private DistributedLogConfiguration conf = new DistributedLogConfiguration().setDLLedgerMetadataLayoutVersion(LEDGER_METADATA_CURRENT_LAYOUT_VERSION);

    @Test(timeout = 60000)
    public void testChangeSequenceNumber() throws Exception {
        String ledgerPath = "/testChangeSequenceNumber";
        zkc.get().create(ledgerPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Map<Long, LogSegmentMetadata> completedLogSegments = new HashMap<Long, LogSegmentMetadata>();
        // Create 5 completed log segments
        for (int i = 1; i <= 5; i++) {
            LogSegmentMetadata segment = DLMTestUtil.completedLogSegment(ledgerPath, i, ((i - 1) * 100), ((i * 100) - 1), 100, i, 100, 0);
            completedLogSegments.put(((long) (i)), segment);
            TestLogSegmentMetadataStoreUpdater.LOG.info("Create completed segment {} : {}", segment.getZkPath(), segment);
            segment.write(zkc);
        }
        // Create a smaller inprogress log segment
        long inprogressSeqNo = 3;
        LogSegmentMetadata segment = DLMTestUtil.inprogressLogSegment(ledgerPath, inprogressSeqNo, (5 * 100), inprogressSeqNo);
        TestLogSegmentMetadataStoreUpdater.LOG.info("Create inprogress segment {} : {}", segment.getZkPath(), segment);
        segment.write(zkc);
        Map<Long, LogSegmentMetadata> segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(5, segmentList.size());
        // Dryrun
        MetadataUpdater dryrunUpdater = new DryrunLogSegmentMetadataStoreUpdater(conf, metadataStore);
        FutureUtils.result(dryrunUpdater.changeSequenceNumber(segment, 6L));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(5, segmentList.size());
        // Fix the inprogress log segments
        MetadataUpdater updater = LogSegmentMetadataStoreUpdater.createMetadataUpdater(conf, metadataStore);
        FutureUtils.result(updater.changeSequenceNumber(segment, 6L));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(6, segmentList.size());
        // check first 5 log segments
        for (int i = 1; i <= 5; i++) {
            LogSegmentMetadata s = segmentList.get(((long) (i)));
            Assert.assertNotNull(s);
            Assert.assertEquals(completedLogSegments.get(((long) (i))), s);
        }
        // get log segment 6
        LogSegmentMetadata segmentChanged = segmentList.get(6L);
        Assert.assertNotNull(segmentChanged);
        Assert.assertEquals(6L, segmentChanged.getLogSegmentSequenceNumber());
        Assert.assertTrue(segmentChanged.isInProgress());
        Assert.assertEquals((5 * 100), segmentChanged.getFirstTxId());
        Assert.assertEquals(3L, segmentChanged.getLedgerId());
    }

    @Test(timeout = 60000)
    public void testUpdateLastDLSN() throws Exception {
        String ledgerPath = "/testUpdateLastDLSN";
        zkc.get().create(ledgerPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        // Create 1 completed log segment
        LogSegmentMetadata completedLogSegment = DLMTestUtil.completedLogSegment(ledgerPath, 1L, 0L, 99L, 100, 1L, 99L, 0L);
        completedLogSegment.write(zkc);
        // Create 1 inprogress log segment
        LogSegmentMetadata inprogressLogSegment = DLMTestUtil.inprogressLogSegment(ledgerPath, 2L, 100L, 2L);
        inprogressLogSegment.write(zkc);
        DLSN badLastDLSN = new DLSN(99L, 0L, 0L);
        DLSN goodLastDLSN1 = new DLSN(1L, 100L, 0L);
        DLSN goodLastDLSN2 = new DLSN(2L, 200L, 0L);
        LogRecordWithDLSN badRecord = DLMTestUtil.getLogRecordWithDLSNInstance(badLastDLSN, 100L);
        LogRecordWithDLSN goodRecord1 = DLMTestUtil.getLogRecordWithDLSNInstance(goodLastDLSN1, 100L);
        LogRecordWithDLSN goodRecord2 = DLMTestUtil.getLogRecordWithDLSNInstance(goodLastDLSN2, 200L);
        // Dryrun
        MetadataUpdater dryrunUpdater = new DryrunLogSegmentMetadataStoreUpdater(conf, metadataStore);
        try {
            FutureUtils.result(dryrunUpdater.updateLastRecord(completedLogSegment, badRecord));
            Assert.fail("Should fail on updating dlsn that in different log segment");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            FutureUtils.result(dryrunUpdater.updateLastRecord(inprogressLogSegment, goodRecord2));
            Assert.fail("Should fail on updating dlsn for an inprogress log segment");
        } catch (IllegalStateException ise) {
            // expected
        }
        LogSegmentMetadata updatedCompletedLogSegment = FutureUtils.result(dryrunUpdater.updateLastRecord(completedLogSegment, goodRecord1));
        Assert.assertEquals(goodLastDLSN1, updatedCompletedLogSegment.getLastDLSN());
        Assert.assertEquals(goodRecord1.getTransactionId(), updatedCompletedLogSegment.getLastTxId());
        Assert.assertTrue(updatedCompletedLogSegment.isRecordLastPositioninThisSegment(goodRecord1));
        Map<Long, LogSegmentMetadata> segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(2, segmentList.size());
        LogSegmentMetadata readCompletedLogSegment = segmentList.get(1L);
        Assert.assertNotNull(readCompletedLogSegment);
        Assert.assertEquals(completedLogSegment, readCompletedLogSegment);
        LogSegmentMetadata readInprogressLogSegment = segmentList.get(2L);
        Assert.assertNotNull(readInprogressLogSegment);
        Assert.assertEquals(inprogressLogSegment, readInprogressLogSegment);
        // Fix the last dlsn
        MetadataUpdater updater = LogSegmentMetadataStoreUpdater.createMetadataUpdater(conf, metadataStore);
        try {
            FutureUtils.result(updater.updateLastRecord(completedLogSegment, badRecord));
            Assert.fail("Should fail on updating dlsn that in different log segment");
        } catch (IllegalArgumentException iae) {
            // expected
        }
        try {
            FutureUtils.result(updater.updateLastRecord(inprogressLogSegment, goodRecord2));
            Assert.fail("Should fail on updating dlsn for an inprogress log segment");
        } catch (IllegalStateException ise) {
            // expected
        }
        updatedCompletedLogSegment = FutureUtils.result(updater.updateLastRecord(completedLogSegment, goodRecord1));
        Assert.assertEquals(goodLastDLSN1, updatedCompletedLogSegment.getLastDLSN());
        Assert.assertEquals(goodRecord1.getTransactionId(), updatedCompletedLogSegment.getLastTxId());
        Assert.assertTrue(updatedCompletedLogSegment.isRecordLastPositioninThisSegment(goodRecord1));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(2, segmentList.size());
        readCompletedLogSegment = segmentList.get(1L);
        Assert.assertNotNull(readCompletedLogSegment);
        Assert.assertEquals(goodLastDLSN1, readCompletedLogSegment.getLastDLSN());
        Assert.assertEquals(goodRecord1.getTransactionId(), readCompletedLogSegment.getLastTxId());
        Assert.assertTrue(readCompletedLogSegment.isRecordLastPositioninThisSegment(goodRecord1));
        Assert.assertEquals(updatedCompletedLogSegment, readCompletedLogSegment);
        Assert.assertEquals(completedLogSegment.getCompletionTime(), readCompletedLogSegment.getCompletionTime());
        Assert.assertEquals(completedLogSegment.getFirstTxId(), readCompletedLogSegment.getFirstTxId());
        Assert.assertEquals(completedLogSegment.getLedgerId(), readCompletedLogSegment.getLedgerId());
        Assert.assertEquals(completedLogSegment.getLogSegmentSequenceNumber(), readCompletedLogSegment.getLogSegmentSequenceNumber());
        Assert.assertEquals(completedLogSegment.getRegionId(), readCompletedLogSegment.getRegionId());
        Assert.assertEquals(completedLogSegment.getZkPath(), readCompletedLogSegment.getZkPath());
        Assert.assertEquals(completedLogSegment.getZNodeName(), readCompletedLogSegment.getZNodeName());
        readInprogressLogSegment = segmentList.get(2L);
        Assert.assertNotNull(readInprogressLogSegment);
        Assert.assertEquals(inprogressLogSegment, readInprogressLogSegment);
    }

    @Test
    public void testChangeTruncationStatus() throws Exception {
        String ledgerPath = "/ledgers2";
        zkc.get().create(ledgerPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        Map<Long, LogSegmentMetadata> completedLogSegments = new HashMap<Long, LogSegmentMetadata>();
        // Create 5 completed log segments
        for (int i = 1; i <= 5; i++) {
            LogSegmentMetadata segment = DLMTestUtil.completedLogSegment(ledgerPath, i, ((i - 1) * 100), ((i * 100) - 1), 100, i, 100, 0);
            completedLogSegments.put(((long) (i)), segment);
            TestLogSegmentMetadataStoreUpdater.LOG.info("Create completed segment {} : {}", segment.getZkPath(), segment);
            segment.write(zkc);
        }
        Map<Long, LogSegmentMetadata> segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(5, segmentList.size());
        long segmentToModify = 1L;
        // Dryrun
        MetadataUpdater dryrunUpdater = new DryrunLogSegmentMetadataStoreUpdater(conf, metadataStore);
        FutureUtils.result(dryrunUpdater.setLogSegmentTruncated(segmentList.get(segmentToModify)));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(false, segmentList.get(segmentToModify).isTruncated());
        // change truncation for the 1st log segment
        MetadataUpdater updater = LogSegmentMetadataStoreUpdater.createMetadataUpdater(conf, metadataStore);
        FutureUtils.result(updater.setLogSegmentTruncated(segmentList.get(segmentToModify)));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(true, segmentList.get(segmentToModify).isTruncated());
        Assert.assertEquals(false, segmentList.get(segmentToModify).isPartiallyTruncated());
        updater = LogSegmentMetadataStoreUpdater.createMetadataUpdater(conf, metadataStore);
        FutureUtils.result(updater.setLogSegmentActive(segmentList.get(segmentToModify)));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(false, segmentList.get(segmentToModify).isTruncated());
        Assert.assertEquals(false, segmentList.get(segmentToModify).isPartiallyTruncated());
        updater = LogSegmentMetadataStoreUpdater.createMetadataUpdater(conf, metadataStore);
        FutureUtils.result(updater.setLogSegmentPartiallyTruncated(segmentList.get(segmentToModify), segmentList.get(segmentToModify).getFirstDLSN()));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(false, segmentList.get(segmentToModify).isTruncated());
        Assert.assertEquals(true, segmentList.get(segmentToModify).isPartiallyTruncated());
        updater = LogSegmentMetadataStoreUpdater.createMetadataUpdater(conf, metadataStore);
        FutureUtils.result(updater.setLogSegmentActive(segmentList.get(segmentToModify)));
        segmentList = readLogSegments(ledgerPath);
        Assert.assertEquals(false, segmentList.get(segmentToModify).isTruncated());
        Assert.assertEquals(false, segmentList.get(segmentToModify).isPartiallyTruncated());
    }
}

