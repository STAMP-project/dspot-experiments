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


import FlushLargeStoresPolicy.HREGION_COLUMNFAMILY_FLUSH_SIZE_LOWER_BOUND_MIN;
import FlushPolicyFactory.HBASE_FLUSH_POLICY_KEY;
import HConstants.HBASE_REGION_SPLIT_POLICY_KEY;
import HConstants.HREGION_MEMSTORE_FLUSH_SIZE;
import HConstants.NO_SEQNUM;
import HStore.BLOCKING_STOREFILES_KEY;
import MutableSegment.DEEP_OVERHEAD;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This test verifies the correctness of the Per Column Family flushing strategy
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestPerColumnFamilyFlush {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestPerColumnFamilyFlush.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestPerColumnFamilyFlush.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Path DIR = getDataTestDir("TestHRegion");

    public static final TableName TABLENAME = TableName.valueOf("TestPerColumnFamilyFlush", "t1");

    public static final byte[][] FAMILIES = new byte[][]{ Bytes.toBytes("f1"), Bytes.toBytes("f2"), Bytes.toBytes("f3"), Bytes.toBytes("f4"), Bytes.toBytes("f5") };

    public static final byte[] FAMILY1 = TestPerColumnFamilyFlush.FAMILIES[0];

    public static final byte[] FAMILY2 = TestPerColumnFamilyFlush.FAMILIES[1];

    public static final byte[] FAMILY3 = TestPerColumnFamilyFlush.FAMILIES[2];

    @Test
    public void testSelectiveFlushWhenEnabled() throws IOException {
        // Set up the configuration, use new one to not conflict with minicluster in other tests
        Configuration conf = new HBaseTestingUtility().getConfiguration();
        conf.setLong(HREGION_MEMSTORE_FLUSH_SIZE, (200 * 1024));
        conf.set(HBASE_FLUSH_POLICY_KEY, FlushAllLargeStoresPolicy.class.getName());
        conf.setLong(HREGION_COLUMNFAMILY_FLUSH_SIZE_LOWER_BOUND_MIN, (40 * 1024));
        // Intialize the region
        HRegion region = initHRegion("testSelectiveFlushWithDataCompaction", conf);
        // Add 1200 entries for CF1, 100 for CF2 and 50 for CF3
        for (int i = 1; i <= 1200; i++) {
            region.put(createPut(1, i));
            if (i <= 100) {
                region.put(createPut(2, i));
                if (i <= 50) {
                    region.put(createPut(3, i));
                }
            }
        }
        long totalMemstoreSize = region.getMemStoreDataSize();
        // Find the smallest LSNs for edits wrt to each CF.
        long smallestSeqCF1 = region.getOldestSeqIdOfStore(TestPerColumnFamilyFlush.FAMILY1);
        long smallestSeqCF2 = region.getOldestSeqIdOfStore(TestPerColumnFamilyFlush.FAMILY2);
        long smallestSeqCF3 = region.getOldestSeqIdOfStore(TestPerColumnFamilyFlush.FAMILY3);
        // Find the sizes of the memstores of each CF.
        MemStoreSize cf1MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize();
        MemStoreSize cf2MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize();
        MemStoreSize cf3MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize();
        // Get the overall smallest LSN in the region's memstores.
        long smallestSeqInRegionCurrentMemstore = getWAL(region).getEarliestMemStoreSeqNum(region.getRegionInfo().getEncodedNameAsBytes());
        // The overall smallest LSN in the region's memstores should be the same as
        // the LSN of the smallest edit in CF1
        Assert.assertEquals(smallestSeqCF1, smallestSeqInRegionCurrentMemstore);
        // Some other sanity checks.
        Assert.assertTrue((smallestSeqCF1 < smallestSeqCF2));
        Assert.assertTrue((smallestSeqCF2 < smallestSeqCF3));
        Assert.assertTrue(((cf1MemstoreSize.getDataSize()) > 0));
        Assert.assertTrue(((cf2MemstoreSize.getDataSize()) > 0));
        Assert.assertTrue(((cf3MemstoreSize.getDataSize()) > 0));
        // The total memstore size should be the same as the sum of the sizes of
        // memstores of CF1, CF2 and CF3.
        Assert.assertEquals(totalMemstoreSize, (((cf1MemstoreSize.getDataSize()) + (cf2MemstoreSize.getDataSize())) + (cf3MemstoreSize.getDataSize())));
        // Flush!
        region.flush(false);
        // Will use these to check if anything changed.
        MemStoreSize oldCF2MemstoreSize = cf2MemstoreSize;
        MemStoreSize oldCF3MemstoreSize = cf3MemstoreSize;
        // Recalculate everything
        cf1MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize();
        cf2MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize();
        cf3MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize();
        totalMemstoreSize = region.getMemStoreDataSize();
        smallestSeqInRegionCurrentMemstore = getWAL(region).getEarliestMemStoreSeqNum(region.getRegionInfo().getEncodedNameAsBytes());
        // We should have cleared out only CF1, since we chose the flush thresholds
        // and number of puts accordingly.
        Assert.assertEquals(0, cf1MemstoreSize.getDataSize());
        Assert.assertEquals(DEEP_OVERHEAD, cf1MemstoreSize.getHeapSize());
        // Nothing should have happened to CF2, ...
        Assert.assertEquals(cf2MemstoreSize, oldCF2MemstoreSize);
        // ... or CF3
        Assert.assertEquals(cf3MemstoreSize, oldCF3MemstoreSize);
        // Now the smallest LSN in the region should be the same as the smallest
        // LSN in the memstore of CF2.
        Assert.assertEquals(smallestSeqInRegionCurrentMemstore, smallestSeqCF2);
        // Of course, this should hold too.
        Assert.assertEquals(totalMemstoreSize, ((cf2MemstoreSize.getDataSize()) + (cf3MemstoreSize.getDataSize())));
        // Now add more puts (mostly for CF2), so that we only flush CF2 this time.
        for (int i = 1200; i < 2400; i++) {
            region.put(createPut(2, i));
            // Add only 100 puts for CF3
            if ((i - 1200) < 100) {
                region.put(createPut(3, i));
            }
        }
        // How much does the CF3 memstore occupy? Will be used later.
        oldCF3MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize();
        // Flush again
        region.flush(false);
        // Recalculate everything
        cf1MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize();
        cf2MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize();
        cf3MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize();
        totalMemstoreSize = region.getMemStoreDataSize();
        smallestSeqInRegionCurrentMemstore = getWAL(region).getEarliestMemStoreSeqNum(region.getRegionInfo().getEncodedNameAsBytes());
        // CF1 and CF2, both should be absent.
        Assert.assertEquals(0, cf1MemstoreSize.getDataSize());
        Assert.assertEquals(DEEP_OVERHEAD, cf1MemstoreSize.getHeapSize());
        Assert.assertEquals(0, cf2MemstoreSize.getDataSize());
        Assert.assertEquals(DEEP_OVERHEAD, cf2MemstoreSize.getHeapSize());
        // CF3 shouldn't have been touched.
        Assert.assertEquals(cf3MemstoreSize, oldCF3MemstoreSize);
        Assert.assertEquals(totalMemstoreSize, cf3MemstoreSize.getDataSize());
        // What happens when we hit the memstore limit, but we are not able to find
        // any Column Family above the threshold?
        // In that case, we should flush all the CFs.
        // Clearing the existing memstores.
        region.flush(true);
        // The memstore limit is 200*1024 and the column family flush threshold is
        // around 50*1024. We try to just hit the memstore limit with each CF's
        // memstore being below the CF flush threshold.
        for (int i = 1; i <= 300; i++) {
            region.put(createPut(1, i));
            region.put(createPut(2, i));
            region.put(createPut(3, i));
            region.put(createPut(4, i));
            region.put(createPut(5, i));
        }
        region.flush(false);
        // Since we won't find any CF above the threshold, and hence no specific
        // store to flush, we should flush all the memstores.
        Assert.assertEquals(0, region.getMemStoreDataSize());
        HBaseTestingUtility.closeRegionAndWAL(region);
    }

    @Test
    public void testSelectiveFlushWhenNotEnabled() throws IOException {
        // Set up the configuration, use new one to not conflict with minicluster in other tests
        Configuration conf = new HBaseTestingUtility().getConfiguration();
        conf.setLong(HREGION_MEMSTORE_FLUSH_SIZE, (200 * 1024));
        conf.set(HBASE_FLUSH_POLICY_KEY, FlushAllStoresPolicy.class.getName());
        // Intialize the HRegion
        HRegion region = initHRegion("testSelectiveFlushWhenNotEnabled", conf);
        // Add 1200 entries for CF1, 100 for CF2 and 50 for CF3
        for (int i = 1; i <= 1200; i++) {
            region.put(createPut(1, i));
            if (i <= 100) {
                region.put(createPut(2, i));
                if (i <= 50) {
                    region.put(createPut(3, i));
                }
            }
        }
        long totalMemstoreSize = region.getMemStoreDataSize();
        // Find the sizes of the memstores of each CF.
        MemStoreSize cf1MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize();
        MemStoreSize cf2MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize();
        MemStoreSize cf3MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize();
        // Some other sanity checks.
        Assert.assertTrue(((cf1MemstoreSize.getDataSize()) > 0));
        Assert.assertTrue(((cf2MemstoreSize.getDataSize()) > 0));
        Assert.assertTrue(((cf3MemstoreSize.getDataSize()) > 0));
        // The total memstore size should be the same as the sum of the sizes of
        // memstores of CF1, CF2 and CF3.
        Assert.assertEquals(totalMemstoreSize, (((cf1MemstoreSize.getDataSize()) + (cf2MemstoreSize.getDataSize())) + (cf3MemstoreSize.getDataSize())));
        // Flush!
        region.flush(false);
        cf1MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize();
        cf2MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize();
        cf3MemstoreSize = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize();
        totalMemstoreSize = region.getMemStoreDataSize();
        long smallestSeqInRegionCurrentMemstore = region.getWAL().getEarliestMemStoreSeqNum(region.getRegionInfo().getEncodedNameAsBytes());
        // Everything should have been cleared
        Assert.assertEquals(0, cf1MemstoreSize.getDataSize());
        Assert.assertEquals(DEEP_OVERHEAD, cf1MemstoreSize.getHeapSize());
        Assert.assertEquals(0, cf2MemstoreSize.getDataSize());
        Assert.assertEquals(DEEP_OVERHEAD, cf2MemstoreSize.getHeapSize());
        Assert.assertEquals(0, cf3MemstoreSize.getDataSize());
        Assert.assertEquals(DEEP_OVERHEAD, cf3MemstoreSize.getHeapSize());
        Assert.assertEquals(0, totalMemstoreSize);
        Assert.assertEquals(NO_SEQNUM, smallestSeqInRegionCurrentMemstore);
        HBaseTestingUtility.closeRegionAndWAL(region);
    }

    // Test Log Replay with Distributed log split on.
    @Test
    public void testLogReplayWithDistributedLogSplit() throws Exception {
        doTestLogReplay();
    }

    /**
     * When a log roll is about to happen, we do a flush of the regions who will be affected by the
     * log roll. These flushes cannot be a selective flushes, otherwise we cannot roll the logs. This
     * test ensures that we do a full-flush in that scenario.
     */
    @Test
    public void testFlushingWhenLogRolling() throws Exception {
        TableName tableName = TableName.valueOf("testFlushingWhenLogRolling");
        Configuration conf = TestPerColumnFamilyFlush.TEST_UTIL.getConfiguration();
        conf.setLong(HREGION_MEMSTORE_FLUSH_SIZE, ((128 * 1024) * 1024));
        conf.set(HBASE_FLUSH_POLICY_KEY, FlushAllLargeStoresPolicy.class.getName());
        long cfFlushSizeLowerBound = 2048;
        conf.setLong(HREGION_COLUMNFAMILY_FLUSH_SIZE_LOWER_BOUND_MIN, cfFlushSizeLowerBound);
        // One hour, prevent periodic rolling
        conf.setLong("hbase.regionserver.logroll.period", ((60L * 60) * 1000));
        // prevent rolling by size
        conf.setLong("hbase.regionserver.hlog.blocksize", ((128L * 1024) * 1024));
        // Make it 10 as max logs before a flush comes on.
        final int maxLogs = 10;
        conf.setInt("hbase.regionserver.maxlogs", maxLogs);
        final int numRegionServers = 1;
        TestPerColumnFamilyFlush.TEST_UTIL.startMiniCluster(numRegionServers);
        try {
            Table table = TestPerColumnFamilyFlush.TEST_UTIL.createTable(tableName, TestPerColumnFamilyFlush.FAMILIES);
            Pair<HRegion, HRegionServer> desiredRegionAndServer = TestPerColumnFamilyFlush.getRegionWithName(tableName);
            final HRegion desiredRegion = desiredRegionAndServer.getFirst();
            Assert.assertTrue("Could not find a region which hosts the new region.", (desiredRegion != null));
            TestPerColumnFamilyFlush.LOG.info(("Writing to region=" + desiredRegion));
            // Add one row for both CFs.
            for (int i = 1; i <= 3; i++) {
                table.put(createPut(i, 0));
            }
            // Now only add row to CF1, make sure when we force a flush, CF1 is larger than the lower
            // bound and CF2 and CF3 are smaller than the lower bound.
            for (int i = 0; i < maxLogs; i++) {
                for (int j = 0; j < 100; j++) {
                    table.put(createPut(1, ((i * 100) + j)));
                }
                // Roll the WAL. The log file count is less than maxLogs so no flush is triggered.
                int currentNumRolledLogFiles = getNumRolledLogFiles(desiredRegion);
                Assert.assertNull(getWAL(desiredRegion).rollWriter());
                while ((getNumRolledLogFiles(desiredRegion)) <= currentNumRolledLogFiles) {
                    Thread.sleep(100);
                } 
            }
            Assert.assertEquals(maxLogs, getNumRolledLogFiles(desiredRegion));
            Assert.assertTrue(((desiredRegion.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize().getHeapSize()) > cfFlushSizeLowerBound));
            Assert.assertTrue(((desiredRegion.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize().getHeapSize()) < cfFlushSizeLowerBound));
            Assert.assertTrue(((desiredRegion.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize().getHeapSize()) < cfFlushSizeLowerBound));
            table.put(createPut(1, 12345678));
            // Make numRolledLogFiles greater than maxLogs
            desiredRegionAndServer.getSecond().walRoller.requestRollAll();
            // Wait for some time till the flush caused by log rolling happens.
            TestPerColumnFamilyFlush.TEST_UTIL.waitFor(30000, new Waiter.ExplainingPredicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return (desiredRegion.getMemStoreDataSize()) == 0;
                }

                @Override
                public String explainFailure() throws Exception {
                    long memstoreSize = desiredRegion.getMemStoreDataSize();
                    if (memstoreSize > 0) {
                        return "Still have unflushed entries in memstore, memstore size is " + memstoreSize;
                    }
                    return "Unknown";
                }
            });
            TestPerColumnFamilyFlush.LOG.info("Finished waiting on flush after too many WALs...");
            // Individual families should have been flushed.
            Assert.assertEquals(DEEP_OVERHEAD, desiredRegion.getStore(TestPerColumnFamilyFlush.FAMILY1).getMemStoreSize().getHeapSize());
            Assert.assertEquals(DEEP_OVERHEAD, desiredRegion.getStore(TestPerColumnFamilyFlush.FAMILY2).getMemStoreSize().getHeapSize());
            Assert.assertEquals(DEEP_OVERHEAD, desiredRegion.getStore(TestPerColumnFamilyFlush.FAMILY3).getMemStoreSize().getHeapSize());
            // let WAL cleanOldLogs
            Assert.assertNull(getWAL(desiredRegion).rollWriter(true));
            Assert.assertTrue(((getNumRolledLogFiles(desiredRegion)) < maxLogs));
        } finally {
            TestPerColumnFamilyFlush.TEST_UTIL.shutdownMiniCluster();
        }
    }

    // Under the same write load, small stores should have less store files when
    // percolumnfamilyflush enabled.
    @Test
    public void testCompareStoreFileCount() throws Exception {
        long memstoreFlushSize = 1024L * 1024;
        Configuration conf = TestPerColumnFamilyFlush.TEST_UTIL.getConfiguration();
        conf.setLong(HREGION_MEMSTORE_FLUSH_SIZE, memstoreFlushSize);
        conf.set(HBASE_FLUSH_POLICY_KEY, FlushAllStoresPolicy.class.getName());
        conf.setInt(BLOCKING_STOREFILES_KEY, 10000);
        conf.set(HBASE_REGION_SPLIT_POLICY_KEY, ConstantSizeRegionSplitPolicy.class.getName());
        HTableDescriptor htd = new HTableDescriptor(TestPerColumnFamilyFlush.TABLENAME);
        htd.setCompactionEnabled(false);
        htd.addFamily(new HColumnDescriptor(TestPerColumnFamilyFlush.FAMILY1));
        htd.addFamily(new HColumnDescriptor(TestPerColumnFamilyFlush.FAMILY2));
        htd.addFamily(new HColumnDescriptor(TestPerColumnFamilyFlush.FAMILY3));
        TestPerColumnFamilyFlush.LOG.info("==============Test with selective flush disabled===============");
        int cf1StoreFileCount = -1;
        int cf2StoreFileCount = -1;
        int cf3StoreFileCount = -1;
        int cf1StoreFileCount1 = -1;
        int cf2StoreFileCount1 = -1;
        int cf3StoreFileCount1 = -1;
        try {
            TestPerColumnFamilyFlush.TEST_UTIL.startMiniCluster(1);
            TestPerColumnFamilyFlush.TEST_UTIL.getAdmin().createNamespace(NamespaceDescriptor.create(TestPerColumnFamilyFlush.TABLENAME.getNamespaceAsString()).build());
            TestPerColumnFamilyFlush.TEST_UTIL.getAdmin().createTable(htd);
            TestPerColumnFamilyFlush.TEST_UTIL.waitTableAvailable(TestPerColumnFamilyFlush.TABLENAME);
            Connection conn = ConnectionFactory.createConnection(conf);
            Table table = conn.getTable(TestPerColumnFamilyFlush.TABLENAME);
            doPut(table, memstoreFlushSize);
            table.close();
            conn.close();
            Region region = TestPerColumnFamilyFlush.getRegionWithName(TestPerColumnFamilyFlush.TABLENAME).getFirst();
            cf1StoreFileCount = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getStorefilesCount();
            cf2StoreFileCount = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getStorefilesCount();
            cf3StoreFileCount = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getStorefilesCount();
        } finally {
            TestPerColumnFamilyFlush.TEST_UTIL.shutdownMiniCluster();
        }
        TestPerColumnFamilyFlush.LOG.info("==============Test with selective flush enabled===============");
        conf.set(HBASE_FLUSH_POLICY_KEY, FlushAllLargeStoresPolicy.class.getName());
        // default value of per-cf flush lower bound is too big, set to a small enough value
        conf.setLong(HREGION_COLUMNFAMILY_FLUSH_SIZE_LOWER_BOUND_MIN, 0);
        try {
            TestPerColumnFamilyFlush.TEST_UTIL.startMiniCluster(1);
            TestPerColumnFamilyFlush.TEST_UTIL.getAdmin().createNamespace(NamespaceDescriptor.create(TestPerColumnFamilyFlush.TABLENAME.getNamespaceAsString()).build());
            TestPerColumnFamilyFlush.TEST_UTIL.getAdmin().createTable(htd);
            Connection conn = ConnectionFactory.createConnection(conf);
            Table table = conn.getTable(TestPerColumnFamilyFlush.TABLENAME);
            doPut(table, memstoreFlushSize);
            table.close();
            conn.close();
            Region region = TestPerColumnFamilyFlush.getRegionWithName(TestPerColumnFamilyFlush.TABLENAME).getFirst();
            cf1StoreFileCount1 = region.getStore(TestPerColumnFamilyFlush.FAMILY1).getStorefilesCount();
            cf2StoreFileCount1 = region.getStore(TestPerColumnFamilyFlush.FAMILY2).getStorefilesCount();
            cf3StoreFileCount1 = region.getStore(TestPerColumnFamilyFlush.FAMILY3).getStorefilesCount();
        } finally {
            TestPerColumnFamilyFlush.TEST_UTIL.shutdownMiniCluster();
        }
        TestPerColumnFamilyFlush.LOG.info(((((((((((("disable selective flush: " + (Bytes.toString(TestPerColumnFamilyFlush.FAMILY1))) + "=>") + cf1StoreFileCount) + ", ") + (Bytes.toString(TestPerColumnFamilyFlush.FAMILY2))) + "=>") + cf2StoreFileCount) + ", ") + (Bytes.toString(TestPerColumnFamilyFlush.FAMILY3))) + "=>") + cf3StoreFileCount));
        TestPerColumnFamilyFlush.LOG.info(((((((((((("enable selective flush: " + (Bytes.toString(TestPerColumnFamilyFlush.FAMILY1))) + "=>") + cf1StoreFileCount1) + ", ") + (Bytes.toString(TestPerColumnFamilyFlush.FAMILY2))) + "=>") + cf2StoreFileCount1) + ", ") + (Bytes.toString(TestPerColumnFamilyFlush.FAMILY3))) + "=>") + cf3StoreFileCount1));
        // small CF will have less store files.
        Assert.assertTrue((cf1StoreFileCount1 < cf1StoreFileCount));
        Assert.assertTrue((cf2StoreFileCount1 < cf2StoreFileCount));
    }
}

