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


import Consistency.TIMELINE;
import HConstants.CATALOG_FAMILY;
import StorefileRefresherChore.REGIONSERVER_STOREFILE_REFRESH_PERIOD;
import TableName.META_TABLE_NAME;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TestMetaTableAccessor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for region replicas. Sad that we cannot isolate these without bringing up a whole
 * cluster. See {@link TestRegionServerNoMaster}.
 */
@Category({ RegionServerTests.class, MediumTests.class })
public class TestRegionReplicas {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionReplicas.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionReplicas.class);

    private static final int NB_SERVERS = 1;

    private static Table table;

    private static final byte[] row = Bytes.toBytes("TestRegionReplicas");

    private static HRegionInfo hriPrimary;

    private static HRegionInfo hriSecondary;

    private static final HBaseTestingUtility HTU = new HBaseTestingUtility();

    private static final byte[] f = HConstants.CATALOG_FAMILY;

    @Test
    public void testOpenRegionReplica() throws Exception {
        TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        try {
            // load some data to primary
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 0, 1000);
            // assert that we can read back from primary
            Assert.assertEquals(1000, TestRegionReplicas.HTU.countRows(TestRegionReplicas.table));
        } finally {
            TestRegionReplicas.HTU.deleteNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 0, 1000);
            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }

    /**
     * Tests that the meta location is saved for secondary regions
     */
    @Test
    public void testRegionReplicaUpdatesMetaLocation() throws Exception {
        TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        Table meta = null;
        try {
            meta = TestRegionReplicas.HTU.getConnection().getTable(META_TABLE_NAME);
            TestMetaTableAccessor.assertMetaLocation(meta, TestRegionReplicas.hriPrimary.getRegionName(), getRS().getServerName(), (-1), 1, false);
        } finally {
            if (meta != null)
                meta.close();

            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }

    @Test
    public void testRegionReplicaGets() throws Exception {
        try {
            // load some data to primary
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 0, 1000);
            // assert that we can read back from primary
            Assert.assertEquals(1000, TestRegionReplicas.HTU.countRows(TestRegionReplicas.table));
            // flush so that region replica can read
            HRegion region = getRS().getRegionByEncodedName(TestRegionReplicas.hriPrimary.getEncodedName());
            region.flush(true);
            TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
            // first try directly against region
            region = getRS().getRegion(TestRegionReplicas.hriSecondary.getEncodedName());
            assertGet(region, 42, true);
            assertGetRpc(TestRegionReplicas.hriSecondary, 42, true);
        } finally {
            TestRegionReplicas.HTU.deleteNumericRows(TestRegionReplicas.table, CATALOG_FAMILY, 0, 1000);
            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }

    @Test
    public void testGetOnTargetRegionReplica() throws Exception {
        try {
            // load some data to primary
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 0, 1000);
            // assert that we can read back from primary
            Assert.assertEquals(1000, TestRegionReplicas.HTU.countRows(TestRegionReplicas.table));
            // flush so that region replica can read
            HRegion region = getRS().getRegionByEncodedName(TestRegionReplicas.hriPrimary.getEncodedName());
            region.flush(true);
            TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
            // try directly Get against region replica
            byte[] row = Bytes.toBytes(String.valueOf(42));
            Get get = new Get(row);
            get.setConsistency(TIMELINE);
            get.setReplicaId(1);
            Result result = TestRegionReplicas.table.get(get);
            Assert.assertArrayEquals(row, result.getValue(TestRegionReplicas.f, null));
        } finally {
            TestRegionReplicas.HTU.deleteNumericRows(TestRegionReplicas.table, CATALOG_FAMILY, 0, 1000);
            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }

    @Test
    public void testRefresStoreFiles() throws Exception {
        // enable store file refreshing
        final int refreshPeriod = 2000;// 2 sec

        TestRegionReplicas.HTU.getConfiguration().setInt("hbase.hstore.compactionThreshold", 100);
        TestRegionReplicas.HTU.getConfiguration().setInt(REGIONSERVER_STOREFILE_REFRESH_PERIOD, refreshPeriod);
        // restart the region server so that it starts the refresher chore
        restartRegionServer();
        try {
            TestRegionReplicas.LOG.info(("Opening the secondary region " + (TestRegionReplicas.hriSecondary.getEncodedName())));
            TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
            // load some data to primary
            TestRegionReplicas.LOG.info("Loading data to primary region");
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 0, 1000);
            // assert that we can read back from primary
            Assert.assertEquals(1000, TestRegionReplicas.HTU.countRows(TestRegionReplicas.table));
            // flush so that region replica can read
            TestRegionReplicas.LOG.info("Flushing primary region");
            HRegion region = getRS().getRegionByEncodedName(TestRegionReplicas.hriPrimary.getEncodedName());
            region.flush(true);
            HRegion primaryRegion = region;
            // ensure that chore is run
            TestRegionReplicas.LOG.info(("Sleeping for " + (4 * refreshPeriod)));
            Threads.sleep((4 * refreshPeriod));
            TestRegionReplicas.LOG.info("Checking results from secondary region replica");
            Region secondaryRegion = getRS().getRegion(TestRegionReplicas.hriSecondary.getEncodedName());
            Assert.assertEquals(1, secondaryRegion.getStore(TestRegionReplicas.f).getStorefilesCount());
            assertGet(secondaryRegion, 42, true);
            assertGetRpc(TestRegionReplicas.hriSecondary, 42, true);
            assertGetRpc(TestRegionReplicas.hriSecondary, 1042, false);
            // load some data to primary
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 1000, 1100);
            region = getRS().getRegionByEncodedName(TestRegionReplicas.hriPrimary.getEncodedName());
            region.flush(true);
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, 2000, 2100);
            region = getRS().getRegionByEncodedName(TestRegionReplicas.hriPrimary.getEncodedName());
            region.flush(true);
            // ensure that chore is run
            Threads.sleep((4 * refreshPeriod));
            assertGetRpc(TestRegionReplicas.hriSecondary, 42, true);
            assertGetRpc(TestRegionReplicas.hriSecondary, 1042, true);
            assertGetRpc(TestRegionReplicas.hriSecondary, 2042, true);
            // ensure that we see the 3 store files
            Assert.assertEquals(3, secondaryRegion.getStore(TestRegionReplicas.f).getStorefilesCount());
            // force compaction
            TestRegionReplicas.HTU.compact(TestRegionReplicas.table.getName(), true);
            long wakeUpTime = (System.currentTimeMillis()) + (4 * refreshPeriod);
            while ((System.currentTimeMillis()) < wakeUpTime) {
                assertGetRpc(TestRegionReplicas.hriSecondary, 42, true);
                assertGetRpc(TestRegionReplicas.hriSecondary, 1042, true);
                assertGetRpc(TestRegionReplicas.hriSecondary, 2042, true);
                Threads.sleep(10);
            } 
            // ensure that we see the compacted file only
            // This will be 4 until the cleaner chore runs
            Assert.assertEquals(4, secondaryRegion.getStore(TestRegionReplicas.f).getStorefilesCount());
        } finally {
            TestRegionReplicas.HTU.deleteNumericRows(TestRegionReplicas.table, CATALOG_FAMILY, 0, 1000);
            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }

    @Test
    public void testFlushAndCompactionsInPrimary() throws Exception {
        long runtime = 30 * 1000;
        // enable store file refreshing
        final int refreshPeriod = 100;// 100ms refresh is a lot

        TestRegionReplicas.HTU.getConfiguration().setInt("hbase.hstore.compactionThreshold", 3);
        TestRegionReplicas.HTU.getConfiguration().setInt(REGIONSERVER_STOREFILE_REFRESH_PERIOD, refreshPeriod);
        // restart the region server so that it starts the refresher chore
        restartRegionServer();
        final int startKey = 0;
        final int endKey = 1000;
        try {
            TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
            // load some data to primary so that reader won't fail
            TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, startKey, endKey);
            TestRegionServerNoMaster.flushRegion(TestRegionReplicas.HTU, TestRegionReplicas.hriPrimary);
            // ensure that chore is run
            Threads.sleep((2 * refreshPeriod));
            final AtomicBoolean running = new AtomicBoolean(true);
            @SuppressWarnings("unchecked")
            final AtomicReference<Exception>[] exceptions = new AtomicReference[3];
            for (int i = 0; i < (exceptions.length); i++) {
                exceptions[i] = new AtomicReference<>();
            }
            Runnable writer = new Runnable() {
                int key = startKey;

                @Override
                public void run() {
                    try {
                        while (running.get()) {
                            byte[] data = Bytes.toBytes(String.valueOf(key));
                            Put put = new Put(data);
                            put.addColumn(TestRegionReplicas.f, null, data);
                            TestRegionReplicas.table.put(put);
                            (key)++;
                            if ((key) == endKey)
                                key = startKey;

                        } 
                    } catch (Exception ex) {
                        TestRegionReplicas.LOG.warn(ex.toString(), ex);
                        exceptions[0].compareAndSet(null, ex);
                    }
                }
            };
            Runnable flusherCompactor = new Runnable() {
                Random random = new Random();

                @Override
                public void run() {
                    try {
                        while (running.get()) {
                            // flush or compact
                            if (random.nextBoolean()) {
                                TestRegionServerNoMaster.flushRegion(TestRegionReplicas.HTU, TestRegionReplicas.hriPrimary);
                            } else {
                                TestRegionReplicas.HTU.compact(TestRegionReplicas.table.getName(), random.nextBoolean());
                            }
                        } 
                    } catch (Exception ex) {
                        TestRegionReplicas.LOG.warn(ex.toString(), ex);
                        exceptions[1].compareAndSet(null, ex);
                    }
                }
            };
            Runnable reader = new Runnable() {
                Random random = new Random();

                @Override
                public void run() {
                    try {
                        while (running.get()) {
                            // whether to do a close and open
                            if ((random.nextInt(10)) == 0) {
                                try {
                                    TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
                                } catch (Exception ex) {
                                    TestRegionReplicas.LOG.warn(((("Failed closing the region " + (TestRegionReplicas.hriSecondary)) + " ") + (StringUtils.stringifyException(ex))));
                                    exceptions[2].compareAndSet(null, ex);
                                }
                                try {
                                    TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
                                } catch (Exception ex) {
                                    TestRegionReplicas.LOG.warn(((("Failed opening the region " + (TestRegionReplicas.hriSecondary)) + " ") + (StringUtils.stringifyException(ex))));
                                    exceptions[2].compareAndSet(null, ex);
                                }
                            }
                            int key = (random.nextInt((endKey - startKey))) + startKey;
                            assertGetRpc(TestRegionReplicas.hriSecondary, key, true);
                        } 
                    } catch (Exception ex) {
                        TestRegionReplicas.LOG.warn(((("Failed getting the value in the region " + (TestRegionReplicas.hriSecondary)) + " ") + (StringUtils.stringifyException(ex))));
                        exceptions[2].compareAndSet(null, ex);
                    }
                }
            };
            TestRegionReplicas.LOG.info("Starting writer and reader");
            ExecutorService executor = Executors.newFixedThreadPool(3);
            executor.submit(writer);
            executor.submit(flusherCompactor);
            executor.submit(reader);
            // wait for threads
            Threads.sleep(runtime);
            running.set(false);
            executor.shutdown();
            executor.awaitTermination(30, TimeUnit.SECONDS);
            for (AtomicReference<Exception> exRef : exceptions) {
                Assert.assertNull(exRef.get());
            }
        } finally {
            TestRegionReplicas.HTU.deleteNumericRows(TestRegionReplicas.table, CATALOG_FAMILY, startKey, endKey);
            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }

    @Test
    public void testVerifySecondaryAbilityToReadWithOnFiles() throws Exception {
        // disable the store file refresh chore (we do this by hand)
        TestRegionReplicas.HTU.getConfiguration().setInt(REGIONSERVER_STOREFILE_REFRESH_PERIOD, 0);
        restartRegionServer();
        try {
            TestRegionReplicas.LOG.info(("Opening the secondary region " + (TestRegionReplicas.hriSecondary.getEncodedName())));
            TestRegionServerNoMaster.openRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
            // load some data to primary
            TestRegionReplicas.LOG.info("Loading data to primary region");
            for (int i = 0; i < 3; ++i) {
                TestRegionReplicas.HTU.loadNumericRows(TestRegionReplicas.table, TestRegionReplicas.f, (i * 1000), ((i + 1) * 1000));
                HRegion region = getRS().getRegionByEncodedName(TestRegionReplicas.hriPrimary.getEncodedName());
                region.flush(true);
            }
            HRegion primaryRegion = getRS().getRegion(TestRegionReplicas.hriPrimary.getEncodedName());
            Assert.assertEquals(3, primaryRegion.getStore(TestRegionReplicas.f).getStorefilesCount());
            // Refresh store files on the secondary
            Region secondaryRegion = getRS().getRegion(TestRegionReplicas.hriSecondary.getEncodedName());
            secondaryRegion.getStore(TestRegionReplicas.f).refreshStoreFiles();
            Assert.assertEquals(3, secondaryRegion.getStore(TestRegionReplicas.f).getStorefilesCount());
            // force compaction
            TestRegionReplicas.LOG.info(("Force Major compaction on primary region " + (TestRegionReplicas.hriPrimary)));
            primaryRegion.compact(true);
            Assert.assertEquals(1, primaryRegion.getStore(TestRegionReplicas.f).getStorefilesCount());
            List<RegionServerThread> regionServerThreads = TestRegionReplicas.HTU.getMiniHBaseCluster().getRegionServerThreads();
            HRegionServer hrs = null;
            for (RegionServerThread rs : regionServerThreads) {
                if ((rs.getRegionServer().getOnlineRegion(primaryRegion.getRegionInfo().getRegionName())) != null) {
                    hrs = rs.getRegionServer();
                    break;
                }
            }
            CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(100, null, hrs, false);
            cleaner.chore();
            // scan all the hfiles on the secondary.
            // since there are no read on the secondary when we ask locations to
            // the NN a FileNotFound exception will be returned and the FileLink
            // should be able to deal with it giving us all the result we expect.
            int keys = 0;
            int sum = 0;
            for (HStoreFile sf : getStorefiles()) {
                // Our file does not exist anymore. was moved by the compaction above.
                TestRegionReplicas.LOG.debug(Boolean.toString(getRS().getFileSystem().exists(sf.getPath())));
                Assert.assertFalse(getRS().getFileSystem().exists(sf.getPath()));
                HFileScanner scanner = sf.getReader().getScanner(false, false);
                scanner.seekTo();
                do {
                    keys++;
                    Cell cell = scanner.getCell();
                    sum += Integer.parseInt(Bytes.toString(cell.getRowArray(), cell.getRowOffset(), cell.getRowLength()));
                } while (scanner.next() );
            }
            Assert.assertEquals(3000, keys);
            Assert.assertEquals(4498500, sum);
        } finally {
            TestRegionReplicas.HTU.deleteNumericRows(TestRegionReplicas.table, CATALOG_FAMILY, 0, 1000);
            TestRegionServerNoMaster.closeRegion(TestRegionReplicas.HTU, getRS(), TestRegionReplicas.hriSecondary);
        }
    }
}

