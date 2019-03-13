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
package org.apache.hadoop.hbase.regionserver;


import Durability.SKIP_WAL;
import Durability.SYNC_WAL;
import EventType.REGION_OPEN;
import FlushAction.COMMIT_FLUSH;
import FlushAction.START_FLUSH;
import FlushLifeCycleTracker.DUMMY;
import FlushResultImpl.Result.CANNOT_FLUSH;
import FlushResultImpl.Result.CANNOT_FLUSH_MEMSTORE_EMPTY;
import NoLimitThroughputController.INSTANCE;
import WAL.Entry;
import WAL.Reader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.regionserver.HRegion.FlushResultImpl;
import org.apache.hadoop.hbase.regionserver.HRegion.PrepareFlushResult;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.ClientProtos.MutationProto.MutationType;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.BulkLoadDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.CompactionDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.FlushDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.FlushDescriptor.FlushAction;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.FlushDescriptor.StoreFlushDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.RegionEventDescriptor;
import org.apache.hadoop.hbase.shaded.protobuf.generated.WALProtos.StoreDescriptor;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.apache.hadoop.hbase.wal.WALSplitter.MutationReplay;
import org.apache.hadoop.util.StringUtils;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.protobuf.UnsafeByteOperations;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static MutableSegment.DEEP_OVERHEAD;


/**
 * Tests of HRegion methods for replaying flush, compaction, region open, etc events for secondary
 * region replicas
 */
@Category(LargeTests.class)
public class TestHRegionReplayEvents {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestHRegionReplayEvents.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestHRegion.class);

    @Rule
    public TestName name = new TestName();

    private static HBaseTestingUtility TEST_UTIL;

    public static Configuration CONF;

    private String dir;

    private byte[][] families = new byte[][]{ Bytes.toBytes("cf1"), Bytes.toBytes("cf2"), Bytes.toBytes("cf3") };

    // Test names
    protected byte[] tableName;

    protected String method;

    protected final byte[] row = Bytes.toBytes("rowA");

    protected final byte[] row2 = Bytes.toBytes("rowB");

    protected byte[] cq = Bytes.toBytes("cq");

    // per test fields
    private Path rootDir;

    private TableDescriptor htd;

    private RegionServerServices rss;

    private RegionInfo primaryHri;

    private RegionInfo secondaryHri;

    private HRegion primaryRegion;

    private HRegion secondaryRegion;

    private WAL walPrimary;

    private WAL walSecondary;

    private Reader reader;

    // Some of the test cases are as follows:
    // 1. replay flush start marker again
    // 2. replay flush with smaller seqId than what is there in memstore snapshot
    // 3. replay flush with larger seqId than what is there in memstore snapshot
    // 4. replay flush commit without flush prepare. non droppable memstore
    // 5. replay flush commit without flush prepare. droppable memstore
    // 6. replay open region event
    // 7. replay open region event after flush start
    // 8. replay flush form an earlier seqId (test ignoring seqIds)
    // 9. start flush does not prevent region from closing.
    @Test
    public void testRegionReplicaSecondaryCannotFlush() throws IOException {
        // load some data and flush ensure that the secondary replica will not execute the flush
        // load some data to secondary by replaying
        putDataByReplay(secondaryRegion, 0, 1000, cq, families);
        TestHRegion.verifyData(secondaryRegion, 0, 1000, cq, families);
        // flush region
        FlushResultImpl flush = ((FlushResultImpl) (secondaryRegion.flush(true)));
        Assert.assertEquals(CANNOT_FLUSH, flush.result);
        TestHRegion.verifyData(secondaryRegion, 0, 1000, cq, families);
        // close the region, and inspect that it has not flushed
        Map<byte[], List<HStoreFile>> files = secondaryRegion.close(false);
        // assert that there are no files (due to flush)
        for (List<HStoreFile> f : files.values()) {
            Assert.assertTrue(f.isEmpty());
        }
    }

    /**
     * Tests a case where we replay only a flush start marker, then the region is closed. This region
     * should not block indefinitely
     */
    @Test
    public void testOnlyReplayingFlushStartDoesNotHoldUpRegionClose() throws IOException {
        // load some data to primary and flush
        int start = 0;
        TestHRegionReplayEvents.LOG.info(((("-- Writing some data to primary from " + start) + " to ") + (start + 100)));
        TestHRegion.putData(primaryRegion, SYNC_WAL, start, 100, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Flushing primary, creating 3 files for 3 stores");
        primaryRegion.flush(true);
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and flush events in secondary");
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    TestHRegionReplayEvents.LOG.info("-- Replaying flush start in secondary");
                    secondaryRegion.replayWALFlushStartMarker(flushDesc);
                } else
                    if ((flushDesc.getAction()) == (FlushAction.COMMIT_FLUSH)) {
                        TestHRegionReplayEvents.LOG.info("-- NOT Replaying flush commit in secondary");
                    }

            } else {
                TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
            }
        } 
        Assert.assertTrue(((rss.getRegionServerAccounting().getGlobalMemStoreDataSize()) > 0));
        // now close the region which should not cause hold because of un-committed flush
        secondaryRegion.close();
        // verify that the memstore size is back to what it was
        Assert.assertEquals(0, rss.getRegionServerAccounting().getGlobalMemStoreDataSize());
    }

    @Test
    public void testBatchReplayWithMultipleNonces() throws IOException {
        try {
            MutationReplay[] mutations = new MutationReplay[100];
            for (int i = 0; i < 100; i++) {
                Put put = new Put(Bytes.toBytes(i));
                put.setDurability(SYNC_WAL);
                for (byte[] familly : this.families) {
                    put.addColumn(familly, this.cq, null);
                    long nonceNum = i / 10;
                    mutations[i] = new MutationReplay(MutationType.PUT, put, nonceNum, nonceNum);
                }
            }
            primaryRegion.batchReplay(mutations, 20);
        } catch (Exception e) {
            String msg = "Error while replay of batch with multiple nonces. ";
            TestHRegionReplayEvents.LOG.error(msg, e);
            Assert.fail((msg + (e.getMessage())));
        }
    }

    @Test
    public void testReplayFlushesAndCompactions() throws IOException {
        // initiate a secondary region with some data.
        // load some data to primary and flush. 3 flushes and some more unflushed data
        putDataWithFlushes(primaryRegion, 100, 300, 100);
        // compaction from primary
        TestHRegionReplayEvents.LOG.info("-- Compacting primary, only 1 store");
        primaryRegion.compactStore(Bytes.toBytes("cf1"), INSTANCE);
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and flush events in secondary");
        int lastReplayed = 0;
        int expectedStoreFileCount = 0;
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            CompactionDescriptor compactionDesc = WALEdit.getCompaction(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                // first verify that everything is replayed and visible before flush event replay
                TestHRegion.verifyData(secondaryRegion, 0, lastReplayed, cq, families);
                HStore store = secondaryRegion.getStore(Bytes.toBytes("cf1"));
                long storeMemstoreSize = store.getMemStoreSize().getHeapSize();
                long regionMemstoreSize = secondaryRegion.getMemStoreDataSize();
                MemStoreSize mss = store.getFlushableSize();
                long storeSize = store.getSize();
                long storeSizeUncompressed = store.getStoreSizeUncompressed();
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    TestHRegionReplayEvents.LOG.info("-- Replaying flush start in secondary");
                    PrepareFlushResult result = secondaryRegion.replayWALFlushStartMarker(flushDesc);
                    Assert.assertNull(result.result);
                    Assert.assertEquals(result.flushOpSeqId, flushDesc.getFlushSequenceNumber());
                    // assert that the store memstore is smaller now
                    long newStoreMemstoreSize = store.getMemStoreSize().getHeapSize();
                    TestHRegionReplayEvents.LOG.info(("Memstore size reduced by:" + (StringUtils.humanReadableInt((newStoreMemstoreSize - storeMemstoreSize)))));
                    Assert.assertTrue((storeMemstoreSize > newStoreMemstoreSize));
                } else
                    if ((flushDesc.getAction()) == (FlushAction.COMMIT_FLUSH)) {
                        TestHRegionReplayEvents.LOG.info("-- Replaying flush commit in secondary");
                        secondaryRegion.replayWALFlushCommitMarker(flushDesc);
                        // assert that the flush files are picked
                        expectedStoreFileCount++;
                        for (HStore s : secondaryRegion.getStores()) {
                            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
                        }
                        MemStoreSize newMss = store.getFlushableSize();
                        Assert.assertTrue(((mss.getHeapSize()) > (newMss.getHeapSize())));
                        // assert that the region memstore is smaller now
                        long newRegionMemstoreSize = secondaryRegion.getMemStoreDataSize();
                        Assert.assertTrue((regionMemstoreSize > newRegionMemstoreSize));
                        // assert that the store sizes are bigger
                        Assert.assertTrue(((store.getSize()) > storeSize));
                        Assert.assertTrue(((store.getStoreSizeUncompressed()) > storeSizeUncompressed));
                        Assert.assertEquals(store.getSize(), store.getStorefilesSize());
                    }

                // after replay verify that everything is still visible
                TestHRegion.verifyData(secondaryRegion, 0, (lastReplayed + 1), cq, families);
            } else
                if (compactionDesc != null) {
                    secondaryRegion.replayWALCompactionMarker(compactionDesc, true, false, Long.MAX_VALUE);
                    // assert that the compaction is applied
                    for (HStore store : secondaryRegion.getStores()) {
                        if (store.getColumnFamilyName().equals("cf1")) {
                            Assert.assertEquals(1, store.getStorefilesCount());
                        } else {
                            Assert.assertEquals(expectedStoreFileCount, store.getStorefilesCount());
                        }
                    }
                } else {
                    lastReplayed = TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
                }

        } 
        Assert.assertEquals((400 - 1), lastReplayed);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, 400, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary. Ensuring that files are not deleted");
        TestHRegion.verifyData(primaryRegion, 0, lastReplayed, cq, families);
        for (HStore store : primaryRegion.getStores()) {
            if (store.getColumnFamilyName().equals("cf1")) {
                Assert.assertEquals(1, store.getStorefilesCount());
            } else {
                Assert.assertEquals(expectedStoreFileCount, store.getStorefilesCount());
            }
        }
    }

    /**
     * Tests cases where we prepare a flush with some seqId and we receive other flush start markers
     * equal to, greater or less than the previous flush start marker.
     */
    @Test
    public void testReplayFlushStartMarkers() throws IOException {
        // load some data to primary and flush. 1 flush and some more unflushed data
        putDataWithFlushes(primaryRegion, 100, 100, 100);
        int numRows = 200;
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and flush events in secondary");
        FlushDescriptor startFlushDesc = null;
        int lastReplayed = 0;
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                // first verify that everything is replayed and visible before flush event replay
                HStore store = secondaryRegion.getStore(Bytes.toBytes("cf1"));
                long storeMemstoreSize = store.getMemStoreSize().getHeapSize();
                long regionMemstoreSize = secondaryRegion.getMemStoreDataSize();
                MemStoreSize mss = store.getFlushableSize();
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    startFlushDesc = flushDesc;
                    TestHRegionReplayEvents.LOG.info("-- Replaying flush start in secondary");
                    PrepareFlushResult result = secondaryRegion.replayWALFlushStartMarker(startFlushDesc);
                    Assert.assertNull(result.result);
                    Assert.assertEquals(result.flushOpSeqId, startFlushDesc.getFlushSequenceNumber());
                    Assert.assertTrue((regionMemstoreSize > 0));
                    Assert.assertTrue(((mss.getHeapSize()) > 0));
                    // assert that the store memstore is smaller now
                    long newStoreMemstoreSize = store.getMemStoreSize().getHeapSize();
                    TestHRegionReplayEvents.LOG.info(("Memstore size reduced by:" + (StringUtils.humanReadableInt((newStoreMemstoreSize - storeMemstoreSize)))));
                    Assert.assertTrue((storeMemstoreSize > newStoreMemstoreSize));
                    TestHRegion.verifyData(secondaryRegion, 0, (lastReplayed + 1), cq, families);
                }
                // after replay verify that everything is still visible
                TestHRegion.verifyData(secondaryRegion, 0, (lastReplayed + 1), cq, families);
            } else {
                lastReplayed = TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
            }
        } 
        // at this point, there should be some data (rows 0-100) in memstore snapshot
        // and some more data in memstores (rows 100-200)
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // Test case 1: replay the same flush start marker again
        TestHRegionReplayEvents.LOG.info("-- Replaying same flush start in secondary again");
        PrepareFlushResult result = secondaryRegion.replayWALFlushStartMarker(startFlushDesc);
        Assert.assertNull(result);// this should return null. Ignoring the flush start marker

        // assert that we still have prepared flush with the previous setup.
        Assert.assertNotNull(secondaryRegion.getPrepareFlushResult());
        Assert.assertEquals(secondaryRegion.getPrepareFlushResult().flushOpSeqId, startFlushDesc.getFlushSequenceNumber());
        Assert.assertTrue(((secondaryRegion.getMemStoreDataSize()) > 0));// memstore is not empty

        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // Test case 2: replay a flush start marker with a smaller seqId
        FlushDescriptor startFlushDescSmallerSeqId = clone(startFlushDesc, ((startFlushDesc.getFlushSequenceNumber()) - 50));
        TestHRegionReplayEvents.LOG.info(("-- Replaying same flush start in secondary again " + startFlushDescSmallerSeqId));
        result = secondaryRegion.replayWALFlushStartMarker(startFlushDescSmallerSeqId);
        Assert.assertNull(result);// this should return null. Ignoring the flush start marker

        // assert that we still have prepared flush with the previous setup.
        Assert.assertNotNull(secondaryRegion.getPrepareFlushResult());
        Assert.assertEquals(secondaryRegion.getPrepareFlushResult().flushOpSeqId, startFlushDesc.getFlushSequenceNumber());
        Assert.assertTrue(((secondaryRegion.getMemStoreDataSize()) > 0));// memstore is not empty

        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // Test case 3: replay a flush start marker with a larger seqId
        FlushDescriptor startFlushDescLargerSeqId = clone(startFlushDesc, ((startFlushDesc.getFlushSequenceNumber()) + 50));
        TestHRegionReplayEvents.LOG.info(("-- Replaying same flush start in secondary again " + startFlushDescLargerSeqId));
        result = secondaryRegion.replayWALFlushStartMarker(startFlushDescLargerSeqId);
        Assert.assertNull(result);// this should return null. Ignoring the flush start marker

        // assert that we still have prepared flush with the previous setup.
        Assert.assertNotNull(secondaryRegion.getPrepareFlushResult());
        Assert.assertEquals(secondaryRegion.getPrepareFlushResult().flushOpSeqId, startFlushDesc.getFlushSequenceNumber());
        Assert.assertTrue(((secondaryRegion.getMemStoreDataSize()) > 0));// memstore is not empty

        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary.");
        TestHRegion.verifyData(primaryRegion, 0, numRows, cq, families);
    }

    /**
     * Tests the case where we prepare a flush with some seqId and we receive a flush commit marker
     * less than the previous flush start marker.
     */
    @Test
    public void testReplayFlushCommitMarkerSmallerThanFlushStartMarker() throws IOException {
        // load some data to primary and flush. 2 flushes and some more unflushed data
        putDataWithFlushes(primaryRegion, 100, 200, 100);
        int numRows = 300;
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and flush events in secondary");
        FlushDescriptor startFlushDesc = null;
        FlushDescriptor commitFlushDesc = null;
        int lastReplayed = 0;
        while (true) {
            System.out.println(lastReplayed);
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    // don't replay the first flush start marker, hold on to it, replay the second one
                    if (startFlushDesc == null) {
                        startFlushDesc = flushDesc;
                    } else {
                        TestHRegionReplayEvents.LOG.info("-- Replaying flush start in secondary");
                        startFlushDesc = flushDesc;
                        PrepareFlushResult result = secondaryRegion.replayWALFlushStartMarker(startFlushDesc);
                        Assert.assertNull(result.result);
                    }
                } else
                    if ((flushDesc.getAction()) == (FlushAction.COMMIT_FLUSH)) {
                        // do not replay any flush commit yet
                        if (commitFlushDesc == null) {
                            commitFlushDesc = flushDesc;// hold on to the first flush commit marker

                        }
                    }

                // after replay verify that everything is still visible
                TestHRegion.verifyData(secondaryRegion, 0, (lastReplayed + 1), cq, families);
            } else {
                lastReplayed = TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
            }
        } 
        // at this point, there should be some data (rows 0-200) in memstore snapshot
        // and some more data in memstores (rows 200-300)
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // no store files in the region
        int expectedStoreFileCount = 0;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        long regionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        // Test case 1: replay the a flush commit marker smaller than what we have prepared
        TestHRegionReplayEvents.LOG.info(((("Testing replaying flush COMMIT " + commitFlushDesc) + " on top of flush START") + startFlushDesc));
        Assert.assertTrue(((commitFlushDesc.getFlushSequenceNumber()) < (startFlushDesc.getFlushSequenceNumber())));
        TestHRegionReplayEvents.LOG.info(("-- Replaying flush commit in secondary" + commitFlushDesc));
        secondaryRegion.replayWALFlushCommitMarker(commitFlushDesc);
        // assert that the flush files are picked
        expectedStoreFileCount++;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        HStore store = secondaryRegion.getStore(Bytes.toBytes("cf1"));
        MemStoreSize mss = store.getFlushableSize();
        Assert.assertTrue(((mss.getHeapSize()) > 0));// assert that the memstore is not dropped

        // assert that the region memstore is same as before
        long newRegionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        Assert.assertEquals(regionMemstoreSize, newRegionMemstoreSize);
        Assert.assertNotNull(secondaryRegion.getPrepareFlushResult());// not dropped

        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary.");
        TestHRegion.verifyData(primaryRegion, 0, numRows, cq, families);
    }

    /**
     * Tests the case where we prepare a flush with some seqId and we receive a flush commit marker
     * larger than the previous flush start marker.
     */
    @Test
    public void testReplayFlushCommitMarkerLargerThanFlushStartMarker() throws IOException {
        // load some data to primary and flush. 1 flush and some more unflushed data
        putDataWithFlushes(primaryRegion, 100, 100, 100);
        int numRows = 200;
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and flush events in secondary");
        FlushDescriptor startFlushDesc = null;
        FlushDescriptor commitFlushDesc = null;
        int lastReplayed = 0;
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    if (startFlushDesc == null) {
                        TestHRegionReplayEvents.LOG.info("-- Replaying flush start in secondary");
                        startFlushDesc = flushDesc;
                        PrepareFlushResult result = secondaryRegion.replayWALFlushStartMarker(startFlushDesc);
                        Assert.assertNull(result.result);
                    }
                } else
                    if ((flushDesc.getAction()) == (FlushAction.COMMIT_FLUSH)) {
                        // do not replay any flush commit yet
                        // hold on to the flush commit marker but simulate a larger
                        // flush commit seqId
                        commitFlushDesc = FlushDescriptor.newBuilder(flushDesc).setFlushSequenceNumber(((flushDesc.getFlushSequenceNumber()) + 50)).build();
                    }

                // after replay verify that everything is still visible
                TestHRegion.verifyData(secondaryRegion, 0, (lastReplayed + 1), cq, families);
            } else {
                lastReplayed = TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
            }
        } 
        // at this point, there should be some data (rows 0-100) in memstore snapshot
        // and some more data in memstores (rows 100-200)
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // no store files in the region
        int expectedStoreFileCount = 0;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        long regionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        // Test case 1: replay the a flush commit marker larger than what we have prepared
        TestHRegionReplayEvents.LOG.info(((("Testing replaying flush COMMIT " + commitFlushDesc) + " on top of flush START") + startFlushDesc));
        Assert.assertTrue(((commitFlushDesc.getFlushSequenceNumber()) > (startFlushDesc.getFlushSequenceNumber())));
        TestHRegionReplayEvents.LOG.info(("-- Replaying flush commit in secondary" + commitFlushDesc));
        secondaryRegion.replayWALFlushCommitMarker(commitFlushDesc);
        // assert that the flush files are picked
        expectedStoreFileCount++;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        HStore store = secondaryRegion.getStore(Bytes.toBytes("cf1"));
        MemStoreSize mss = store.getFlushableSize();
        Assert.assertTrue(((mss.getHeapSize()) > 0));// assert that the memstore is not dropped

        // assert that the region memstore is smaller than before, but not empty
        long newRegionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        Assert.assertTrue((newRegionMemstoreSize > 0));
        Assert.assertTrue((regionMemstoreSize > newRegionMemstoreSize));
        Assert.assertNull(secondaryRegion.getPrepareFlushResult());// prepare snapshot should be dropped

        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary.");
        TestHRegion.verifyData(primaryRegion, 0, numRows, cq, families);
    }

    /**
     * Tests the case where we receive a flush commit before receiving any flush prepare markers.
     * The memstore edits should be dropped after the flush commit replay since they should be in
     * flushed files
     */
    @Test
    public void testReplayFlushCommitMarkerWithoutFlushStartMarkerDroppableMemstore() throws IOException {
        testReplayFlushCommitMarkerWithoutFlushStartMarker(true);
    }

    /**
     * Tests the case where we receive a flush commit before receiving any flush prepare markers.
     * The memstore edits should be not dropped after the flush commit replay since not every edit
     * will be in flushed files (based on seqId)
     */
    @Test
    public void testReplayFlushCommitMarkerWithoutFlushStartMarkerNonDroppableMemstore() throws IOException {
        testReplayFlushCommitMarkerWithoutFlushStartMarker(false);
    }

    /**
     * Tests replaying region open markers from primary region. Checks whether the files are picked up
     */
    @Test
    public void testReplayRegionOpenEvent() throws IOException {
        putDataWithFlushes(primaryRegion, 100, 0, 100);// no flush

        int numRows = 100;
        // close the region and open again.
        primaryRegion.close();
        primaryRegion = HRegion.openHRegion(rootDir, primaryHri, htd, walPrimary, TestHRegionReplayEvents.CONF, rss, null);
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        List<RegionEventDescriptor> regionEvents = Lists.newArrayList();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and region events in secondary");
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            RegionEventDescriptor regionEventDesc = WALEdit.getRegionEventDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                // don't replay flush events
            } else
                if (regionEventDesc != null) {
                    regionEvents.add(regionEventDesc);
                } else {
                    // don't replay edits
                }

        } 
        // we should have 1 open, 1 close and 1 open event
        Assert.assertEquals(3, regionEvents.size());
        // replay the first region open event.
        secondaryRegion.replayWALRegionEventMarker(regionEvents.get(0));
        // replay the close event as well
        secondaryRegion.replayWALRegionEventMarker(regionEvents.get(1));
        // no store files in the region
        int expectedStoreFileCount = 0;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        long regionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        Assert.assertTrue((regionMemstoreSize == 0));
        // now replay the region open event that should contain new file locations
        TestHRegionReplayEvents.LOG.info(("Testing replaying region open event " + (regionEvents.get(2))));
        secondaryRegion.replayWALRegionEventMarker(regionEvents.get(2));
        // assert that the flush files are picked
        expectedStoreFileCount++;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        HStore store = secondaryRegion.getStore(Bytes.toBytes("cf1"));
        MemStoreSize mss = store.getFlushableSize();
        Assert.assertTrue(((mss.getHeapSize()) == (DEEP_OVERHEAD)));
        // assert that the region memstore is empty
        long newRegionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        Assert.assertTrue((newRegionMemstoreSize == 0));
        Assert.assertNull(secondaryRegion.getPrepareFlushResult());// prepare snapshot should be dropped if any

        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary.");
        TestHRegion.verifyData(primaryRegion, 0, numRows, cq, families);
    }

    /**
     * Tests the case where we replay a region open event after a flush start but before receiving
     * flush commit
     */
    @Test
    public void testReplayRegionOpenEventAfterFlushStart() throws IOException {
        putDataWithFlushes(primaryRegion, 100, 100, 100);
        int numRows = 200;
        // close the region and open again.
        primaryRegion.close();
        primaryRegion = HRegion.openHRegion(rootDir, primaryHri, htd, walPrimary, TestHRegionReplayEvents.CONF, rss, null);
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        List<RegionEventDescriptor> regionEvents = Lists.newArrayList();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and region events in secondary");
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            RegionEventDescriptor regionEventDesc = WALEdit.getRegionEventDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                // only replay flush start
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    secondaryRegion.replayWALFlushStartMarker(flushDesc);
                }
            } else
                if (regionEventDesc != null) {
                    regionEvents.add(regionEventDesc);
                } else {
                    TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
                }

        } 
        // at this point, there should be some data (rows 0-100) in the memstore snapshot
        // and some more data in memstores (rows 100-200)
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // we should have 1 open, 1 close and 1 open event
        Assert.assertEquals(3, regionEvents.size());
        // no store files in the region
        int expectedStoreFileCount = 0;
        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        // now replay the region open event that should contain new file locations
        TestHRegionReplayEvents.LOG.info(("Testing replaying region open event " + (regionEvents.get(2))));
        secondaryRegion.replayWALRegionEventMarker(regionEvents.get(2));
        // assert that the flush files are picked
        expectedStoreFileCount = 2;// two flushes happened

        for (HStore s : secondaryRegion.getStores()) {
            Assert.assertEquals(expectedStoreFileCount, s.getStorefilesCount());
        }
        HStore store = secondaryRegion.getStore(Bytes.toBytes("cf1"));
        MemStoreSize newSnapshotSize = store.getSnapshotSize();
        Assert.assertTrue(((newSnapshotSize.getDataSize()) == 0));
        // assert that the region memstore is empty
        long newRegionMemstoreSize = secondaryRegion.getMemStoreDataSize();
        Assert.assertTrue((newRegionMemstoreSize == 0));
        Assert.assertNull(secondaryRegion.getPrepareFlushResult());// prepare snapshot should be dropped if any

        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary.");
        TestHRegion.verifyData(primaryRegion, 0, numRows, cq, families);
    }

    /**
     * Tests whether edits coming in for replay are skipped which have smaller seq id than the seqId
     * of the last replayed region open event.
     */
    @Test
    public void testSkippingEditsWithSmallerSeqIdAfterRegionOpenEvent() throws IOException {
        putDataWithFlushes(primaryRegion, 100, 100, 0);
        int numRows = 100;
        // close the region and open again.
        primaryRegion.close();
        primaryRegion = HRegion.openHRegion(rootDir, primaryHri, htd, walPrimary, TestHRegionReplayEvents.CONF, rss, null);
        // now replay the edits and the flush marker
        reader = createWALReaderForPrimary();
        List<RegionEventDescriptor> regionEvents = Lists.newArrayList();
        List<WAL.Entry> edits = Lists.newArrayList();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and region events in secondary");
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            RegionEventDescriptor regionEventDesc = WALEdit.getRegionEventDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                // don't replay flushes
            } else
                if (regionEventDesc != null) {
                    regionEvents.add(regionEventDesc);
                } else {
                    edits.add(entry);
                }

        } 
        // replay the region open of first open, but with the seqid of the second open
        // this way non of the flush files will be picked up.
        secondaryRegion.replayWALRegionEventMarker(RegionEventDescriptor.newBuilder(regionEvents.get(0)).setLogSequenceNumber(regionEvents.get(2).getLogSequenceNumber()).build());
        // replay edits from the before region close. If replay does not
        // skip these the following verification will NOT fail.
        for (WAL.Entry entry : edits) {
            TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
        }
        boolean expectedFail = false;
        try {
            TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        } catch (AssertionError e) {
            expectedFail = true;// expected

        }
        if (!expectedFail) {
            Assert.fail("Should have failed this verification");
        }
    }

    @Test
    public void testReplayFlushSeqIds() throws IOException {
        // load some data to primary and flush
        int start = 0;
        TestHRegionReplayEvents.LOG.info(((("-- Writing some data to primary from " + start) + " to ") + (start + 100)));
        TestHRegion.putData(primaryRegion, SYNC_WAL, start, 100, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Flushing primary, creating 3 files for 3 stores");
        primaryRegion.flush(true);
        // now replay the flush marker
        reader = createWALReaderForPrimary();
        long flushSeqId = -1;
        TestHRegionReplayEvents.LOG.info("-- Replaying flush events in secondary");
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flushDesc = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flushDesc != null) {
                if ((flushDesc.getAction()) == (FlushAction.START_FLUSH)) {
                    TestHRegionReplayEvents.LOG.info("-- Replaying flush start in secondary");
                    secondaryRegion.replayWALFlushStartMarker(flushDesc);
                    flushSeqId = flushDesc.getFlushSequenceNumber();
                } else
                    if ((flushDesc.getAction()) == (FlushAction.COMMIT_FLUSH)) {
                        TestHRegionReplayEvents.LOG.info("-- Replaying flush commit in secondary");
                        secondaryRegion.replayWALFlushCommitMarker(flushDesc);
                        Assert.assertEquals(flushSeqId, flushDesc.getFlushSequenceNumber());
                    }

            }
            // else do not replay
        } 
        // TODO: what to do with this?
        // assert that the newly picked up flush file is visible
        long readPoint = secondaryRegion.getMVCC().getReadPoint();
        Assert.assertEquals(flushSeqId, readPoint);
        // after replay verify that everything is still visible
        TestHRegion.verifyData(secondaryRegion, 0, 100, cq, families);
    }

    @Test
    public void testSeqIdsFromReplay() throws IOException {
        // test the case where seqId's coming from replayed WALEdits are made persisted with their
        // original seqIds and they are made visible through mvcc read point upon replay
        String method = name.getMethodName();
        byte[] tableName = Bytes.toBytes(method);
        byte[] family = Bytes.toBytes("family");
        HRegion region = TestHRegionReplayEvents.initHRegion(tableName, method, family);
        try {
            // replay an entry that is bigger than current read point
            long readPoint = region.getMVCC().getReadPoint();
            long origSeqId = readPoint + 100;
            Put put = new Put(row).addColumn(family, row, row);
            put.setDurability(SKIP_WAL);// we replay with skip wal

            replay(region, put, origSeqId);
            // read point should have advanced to this seqId
            TestHRegion.assertGet(region, family, row);
            // region seqId should have advanced at least to this seqId
            Assert.assertEquals(origSeqId, region.getReadPoint(null));
            // replay an entry that is smaller than current read point
            // caution: adding an entry below current read point might cause partial dirty reads. Normal
            // replay does not allow reads while replay is going on.
            put = new Put(row2).addColumn(family, row2, row2);
            put.setDurability(SKIP_WAL);
            replay(region, put, (origSeqId - 50));
            TestHRegion.assertGet(region, family, row2);
        } finally {
            region.close();
        }
    }

    /**
     * Tests that a region opened in secondary mode would not write region open / close
     * events to its WAL.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testSecondaryRegionDoesNotWriteRegionEventsToWAL() throws IOException {
        secondaryRegion.close();
        walSecondary = Mockito.spy(walSecondary);
        // test for region open and close
        secondaryRegion = HRegion.openHRegion(secondaryHri, htd, walSecondary, TestHRegionReplayEvents.CONF, rss, null);
        Mockito.verify(walSecondary, Mockito.times(0)).append(ArgumentMatchers.any(RegionInfo.class), ArgumentMatchers.any(WALKeyImpl.class), ArgumentMatchers.any(WALEdit.class), ArgumentMatchers.anyBoolean());
        // test for replay prepare flush
        putDataByReplay(secondaryRegion, 0, 10, cq, families);
        secondaryRegion.replayWALFlushStartMarker(FlushDescriptor.newBuilder().setFlushSequenceNumber(10).setTableName(UnsafeByteOperations.unsafeWrap(primaryRegion.getTableDescriptor().getTableName().getName())).setAction(START_FLUSH).setEncodedRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getEncodedNameAsBytes())).setRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getRegionName())).build());
        Mockito.verify(walSecondary, Mockito.times(0)).append(ArgumentMatchers.any(RegionInfo.class), ArgumentMatchers.any(WALKeyImpl.class), ArgumentMatchers.any(WALEdit.class), ArgumentMatchers.anyBoolean());
        secondaryRegion.close();
        Mockito.verify(walSecondary, Mockito.times(0)).append(ArgumentMatchers.any(RegionInfo.class), ArgumentMatchers.any(WALKeyImpl.class), ArgumentMatchers.any(WALEdit.class), ArgumentMatchers.anyBoolean());
    }

    /**
     * Tests the reads enabled flag for the region. When unset all reads should be rejected
     */
    @Test
    public void testRegionReadsEnabledFlag() throws IOException {
        putDataByReplay(secondaryRegion, 0, 100, cq, families);
        TestHRegion.verifyData(secondaryRegion, 0, 100, cq, families);
        // now disable reads
        secondaryRegion.setReadsEnabled(false);
        try {
            TestHRegion.verifyData(secondaryRegion, 0, 100, cq, families);
            Assert.fail("Should have failed with IOException");
        } catch (IOException ex) {
            // expected
        }
        // verify that we can still replay data
        putDataByReplay(secondaryRegion, 100, 100, cq, families);
        // now enable reads again
        secondaryRegion.setReadsEnabled(true);
        TestHRegion.verifyData(secondaryRegion, 0, 200, cq, families);
    }

    /**
     * Tests the case where a request for flush cache is sent to the region, but region cannot flush.
     * It should write the flush request marker instead.
     */
    @Test
    public void testWriteFlushRequestMarker() throws IOException {
        // primary region is empty at this point. Request a flush with writeFlushRequestWalMarker=false
        FlushResultImpl result = primaryRegion.flushcache(true, false, DUMMY);
        Assert.assertNotNull(result);
        Assert.assertEquals(CANNOT_FLUSH_MEMSTORE_EMPTY, result.result);
        Assert.assertFalse(result.wroteFlushWalMarker);
        // request flush again, but this time with writeFlushRequestWalMarker = true
        result = primaryRegion.flushcache(true, true, DUMMY);
        Assert.assertNotNull(result);
        Assert.assertEquals(CANNOT_FLUSH_MEMSTORE_EMPTY, result.result);
        Assert.assertTrue(result.wroteFlushWalMarker);
        List<FlushDescriptor> flushes = Lists.newArrayList();
        reader = createWALReaderForPrimary();
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flush = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flush != null) {
                flushes.add(flush);
            }
        } 
        Assert.assertEquals(1, flushes.size());
        Assert.assertNotNull(flushes.get(0));
        Assert.assertEquals(FlushDescriptor.FlushAction.CANNOT_FLUSH, flushes.get(0).getAction());
    }

    /**
     * Test the case where the secondary region replica is not in reads enabled state because it is
     * waiting for a flush or region open marker from primary region. Replaying CANNOT_FLUSH
     * flush marker entry should restore the reads enabled status in the region and allow the reads
     * to continue.
     */
    @Test
    public void testReplayingFlushRequestRestoresReadsEnabledState() throws IOException {
        disableReads(secondaryRegion);
        // Test case 1: Test that replaying CANNOT_FLUSH request marker assuming this came from
        // triggered flush restores readsEnabled
        primaryRegion.flushcache(true, true, DUMMY);
        reader = createWALReaderForPrimary();
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flush = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flush != null) {
                secondaryRegion.replayWALFlushMarker(flush, entry.getKey().getSequenceId());
            }
        } 
        // now reads should be enabled
        secondaryRegion.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0)));
    }

    /**
     * Test the case where the secondary region replica is not in reads enabled state because it is
     * waiting for a flush or region open marker from primary region. Replaying flush start and commit
     * entries should restore the reads enabled status in the region and allow the reads
     * to continue.
     */
    @Test
    public void testReplayingFlushRestoresReadsEnabledState() throws IOException {
        // Test case 2: Test that replaying FLUSH_START and FLUSH_COMMIT markers assuming these came
        // from triggered flush restores readsEnabled
        disableReads(secondaryRegion);
        // put some data in primary
        TestHRegion.putData(primaryRegion, SYNC_WAL, 0, 100, cq, families);
        primaryRegion.flush(true);
        // I seem to need to push more edits through so the WAL flushes on local fs. This was not
        // needed before HBASE-15028. Not sure whats up. I can see that we have not flushed if I
        // look at the WAL if I pause the test here and then use WALPrettyPrinter to look at content..
        // Doing same check before HBASE-15028 I can see all edits flushed to the WAL. Somethings up
        // but can't figure it... and this is only test that seems to suffer this flush issue.
        // St.Ack 20160201
        TestHRegion.putData(primaryRegion, SYNC_WAL, 0, 100, cq, families);
        reader = createWALReaderForPrimary();
        while (true) {
            WAL.Entry entry = reader.next();
            TestHRegionReplayEvents.LOG.info(Objects.toString(entry));
            if (entry == null) {
                break;
            }
            FlushDescriptor flush = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flush != null) {
                secondaryRegion.replayWALFlushMarker(flush, entry.getKey().getSequenceId());
            } else {
                TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
            }
        } 
        // now reads should be enabled
        TestHRegion.verifyData(secondaryRegion, 0, 100, cq, families);
    }

    /**
     * Test the case where the secondary region replica is not in reads enabled state because it is
     * waiting for a flush or region open marker from primary region. Replaying flush start and commit
     * entries should restore the reads enabled status in the region and allow the reads
     * to continue.
     */
    @Test
    public void testReplayingFlushWithEmptyMemstoreRestoresReadsEnabledState() throws IOException {
        // Test case 2: Test that replaying FLUSH_START and FLUSH_COMMIT markers assuming these came
        // from triggered flush restores readsEnabled
        disableReads(secondaryRegion);
        // put some data in primary
        TestHRegion.putData(primaryRegion, SYNC_WAL, 0, 100, cq, families);
        primaryRegion.flush(true);
        reader = createWALReaderForPrimary();
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flush = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flush != null) {
                secondaryRegion.replayWALFlushMarker(flush, entry.getKey().getSequenceId());
            }
        } 
        // now reads should be enabled
        TestHRegion.verifyData(secondaryRegion, 0, 100, cq, families);
    }

    /**
     * Test the case where the secondary region replica is not in reads enabled state because it is
     * waiting for a flush or region open marker from primary region. Replaying region open event
     * entry from primary should restore the reads enabled status in the region and allow the reads
     * to continue.
     */
    @Test
    public void testReplayingRegionOpenEventRestoresReadsEnabledState() throws IOException {
        // Test case 3: Test that replaying region open event markers restores readsEnabled
        disableReads(secondaryRegion);
        primaryRegion.close();
        primaryRegion = HRegion.openHRegion(rootDir, primaryHri, htd, walPrimary, TestHRegionReplayEvents.CONF, rss, null);
        reader = createWALReaderForPrimary();
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            RegionEventDescriptor regionEventDesc = WALEdit.getRegionEventDescriptor(entry.getEdit().getCells().get(0));
            if (regionEventDesc != null) {
                secondaryRegion.replayWALRegionEventMarker(regionEventDesc);
            }
        } 
        // now reads should be enabled
        secondaryRegion.get(new org.apache.hadoop.hbase.client.Get(Bytes.toBytes(0)));
    }

    @Test
    public void testRefresStoreFiles() throws IOException {
        Assert.assertEquals(0, primaryRegion.getStoreFileList(families).size());
        Assert.assertEquals(0, secondaryRegion.getStoreFileList(families).size());
        // Test case 1: refresh with an empty region
        secondaryRegion.refreshStoreFiles();
        Assert.assertEquals(0, secondaryRegion.getStoreFileList(families).size());
        // do one flush
        putDataWithFlushes(primaryRegion, 100, 100, 0);
        int numRows = 100;
        // refresh the store file list, and ensure that the files are picked up.
        secondaryRegion.refreshStoreFiles();
        assertPathListsEqual(primaryRegion.getStoreFileList(families), secondaryRegion.getStoreFileList(families));
        Assert.assertEquals(families.length, secondaryRegion.getStoreFileList(families).size());
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        // Test case 2: 3 some more flushes
        putDataWithFlushes(primaryRegion, 100, 300, 0);
        numRows = 300;
        // refresh the store file list, and ensure that the files are picked up.
        secondaryRegion.refreshStoreFiles();
        assertPathListsEqual(primaryRegion.getStoreFileList(families), secondaryRegion.getStoreFileList(families));
        Assert.assertEquals(((families.length) * 4), secondaryRegion.getStoreFileList(families).size());
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        if (FSUtils.WINDOWS) {
            // compaction cannot move files while they are open in secondary on windows. Skip remaining.
            return;
        }
        // Test case 3: compact primary files
        primaryRegion.compactStores();
        List<HRegion> regions = new ArrayList<>();
        regions.add(primaryRegion);
        Mockito.doReturn(regions).when(rss).getRegions();
        CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(100, null, rss, false);
        cleaner.chore();
        secondaryRegion.refreshStoreFiles();
        assertPathListsEqual(primaryRegion.getStoreFileList(families), secondaryRegion.getStoreFileList(families));
        Assert.assertEquals(families.length, secondaryRegion.getStoreFileList(families).size());
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Replaying edits in secondary");
        // Test case 4: replay some edits, ensure that memstore is dropped.
        Assert.assertTrue(((secondaryRegion.getMemStoreDataSize()) == 0));
        putDataWithFlushes(primaryRegion, 400, 400, 0);
        numRows = 400;
        reader = createWALReaderForPrimary();
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            FlushDescriptor flush = WALEdit.getFlushDescriptor(entry.getEdit().getCells().get(0));
            if (flush != null) {
                // do not replay flush
            } else {
                TestHRegionReplayEvents.replayEdit(secondaryRegion, entry);
            }
        } 
        Assert.assertTrue(((secondaryRegion.getMemStoreDataSize()) > 0));
        secondaryRegion.refreshStoreFiles();
        Assert.assertTrue(((secondaryRegion.getMemStoreDataSize()) == 0));
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from primary");
        TestHRegion.verifyData(primaryRegion, 0, numRows, cq, families);
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        TestHRegion.verifyData(secondaryRegion, 0, numRows, cq, families);
    }

    /**
     * Tests replaying region open markers from primary region. Checks whether the files are picked up
     */
    @Test
    public void testReplayBulkLoadEvent() throws IOException {
        TestHRegionReplayEvents.LOG.info("testReplayBulkLoadEvent starts");
        putDataWithFlushes(primaryRegion, 100, 0, 100);// no flush

        // close the region and open again.
        primaryRegion.close();
        primaryRegion = HRegion.openHRegion(rootDir, primaryHri, htd, walPrimary, TestHRegionReplayEvents.CONF, rss, null);
        // bulk load a file into primary region
        Random random = new Random();
        byte[] randomValues = new byte[20];
        random.nextBytes(randomValues);
        Path testPath = TestHRegionReplayEvents.TEST_UTIL.getDataTestDirOnTestFS();
        List<Pair<byte[], String>> familyPaths = new ArrayList<>();
        int expectedLoadFileCount = 0;
        for (byte[] family : families) {
            familyPaths.add(new Pair(family, createHFileForFamilies(testPath, family, randomValues)));
            expectedLoadFileCount++;
        }
        primaryRegion.bulkLoadHFiles(familyPaths, false, null);
        // now replay the edits and the bulk load marker
        reader = createWALReaderForPrimary();
        TestHRegionReplayEvents.LOG.info("-- Replaying edits and region events in secondary");
        BulkLoadDescriptor bulkloadEvent = null;
        while (true) {
            WAL.Entry entry = reader.next();
            if (entry == null) {
                break;
            }
            bulkloadEvent = WALEdit.getBulkLoadDescriptor(entry.getEdit().getCells().get(0));
            if (bulkloadEvent != null) {
                break;
            }
        } 
        // we should have 1 bulk load event
        Assert.assertTrue((bulkloadEvent != null));
        Assert.assertEquals(expectedLoadFileCount, bulkloadEvent.getStoresCount());
        // replay the bulk load event
        secondaryRegion.replayWALBulkLoadEventMarker(bulkloadEvent);
        List<String> storeFileName = new ArrayList<>();
        for (StoreDescriptor storeDesc : bulkloadEvent.getStoresList()) {
            storeFileName.addAll(storeDesc.getStoreFileList());
        }
        // assert that the bulk loaded files are picked
        for (HStore s : secondaryRegion.getStores()) {
            for (HStoreFile sf : s.getStorefiles()) {
                storeFileName.remove(sf.getPath().getName());
            }
        }
        Assert.assertTrue(("Found some store file isn't loaded:" + storeFileName), storeFileName.isEmpty());
        TestHRegionReplayEvents.LOG.info("-- Verifying edits from secondary");
        for (byte[] family : families) {
            TestHRegion.assertGet(secondaryRegion, family, randomValues);
        }
    }

    @Test
    public void testReplayingFlushCommitWithFileAlreadyDeleted() throws IOException {
        // tests replaying flush commit marker, but the flush file has already been compacted
        // from primary and also deleted from the archive directory
        secondaryRegion.replayWALFlushCommitMarker(FlushDescriptor.newBuilder().setFlushSequenceNumber(Long.MAX_VALUE).setTableName(UnsafeByteOperations.unsafeWrap(primaryRegion.getTableDescriptor().getTableName().getName())).setAction(COMMIT_FLUSH).setEncodedRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getEncodedNameAsBytes())).setRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getRegionName())).addStoreFlushes(StoreFlushDescriptor.newBuilder().setFamilyName(UnsafeByteOperations.unsafeWrap(families[0])).setStoreHomeDir("/store_home_dir").addFlushOutput("/foo/baz/123").build()).build());
    }

    @Test
    public void testReplayingCompactionWithFileAlreadyDeleted() throws IOException {
        // tests replaying compaction marker, but the compaction output file has already been compacted
        // from primary and also deleted from the archive directory
        secondaryRegion.replayWALCompactionMarker(CompactionDescriptor.newBuilder().setTableName(UnsafeByteOperations.unsafeWrap(primaryRegion.getTableDescriptor().getTableName().getName())).setEncodedRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getEncodedNameAsBytes())).setFamilyName(UnsafeByteOperations.unsafeWrap(families[0])).addCompactionInput("/123").addCompactionOutput("/456").setStoreHomeDir("/store_home_dir").setRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getRegionName())).build(), true, true, Long.MAX_VALUE);
    }

    @Test
    public void testReplayingRegionOpenEventWithFileAlreadyDeleted() throws IOException {
        // tests replaying region open event marker, but the region files have already been compacted
        // from primary and also deleted from the archive directory
        secondaryRegion.replayWALRegionEventMarker(RegionEventDescriptor.newBuilder().setTableName(UnsafeByteOperations.unsafeWrap(primaryRegion.getTableDescriptor().getTableName().getName())).setEncodedRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getEncodedNameAsBytes())).setRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getRegionName())).setEventType(REGION_OPEN).setServer(ProtobufUtil.toServerName(ServerName.valueOf("foo", 1, 1))).setLogSequenceNumber(Long.MAX_VALUE).addStores(StoreDescriptor.newBuilder().setFamilyName(UnsafeByteOperations.unsafeWrap(families[0])).setStoreHomeDir("/store_home_dir").addStoreFile("/123").build()).build());
    }

    @Test
    public void testReplayingBulkLoadEventWithFileAlreadyDeleted() throws IOException {
        // tests replaying bulk load event marker, but the bulk load files have already been compacted
        // from primary and also deleted from the archive directory
        secondaryRegion.replayWALBulkLoadEventMarker(BulkLoadDescriptor.newBuilder().setTableName(ProtobufUtil.toProtoTableName(primaryRegion.getTableDescriptor().getTableName())).setEncodedRegionName(UnsafeByteOperations.unsafeWrap(primaryRegion.getRegionInfo().getEncodedNameAsBytes())).setBulkloadSeqNum(Long.MAX_VALUE).addStores(StoreDescriptor.newBuilder().setFamilyName(UnsafeByteOperations.unsafeWrap(families[0])).setStoreHomeDir("/store_home_dir").addStoreFile("/123").build()).build());
    }
}

