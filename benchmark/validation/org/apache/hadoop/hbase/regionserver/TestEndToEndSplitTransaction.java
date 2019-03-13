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


import CompactionState.NONE;
import HConstants.EMPTY_END_ROW;
import HConstants.EMPTY_START_ROW;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ChoreService;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.ScheduledChore;
import org.apache.hadoop.hbase.Stoppable;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.CompactionState;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.util.RetryCounter;
import org.apache.hadoop.hbase.util.StoppableImplementation;
import org.apache.hbase.thirdparty.com.google.common.collect.Iterators;
import org.apache.hbase.thirdparty.com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestEndToEndSplitTransaction {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestEndToEndSplitTransaction.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestEndToEndSplitTransaction.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Configuration CONF = TestEndToEndSplitTransaction.TEST_UTIL.getConfiguration();

    @Rule
    public TestName name = new TestName();

    /* This is the test for : HBASE-20940 This test will split the region and try to open an reference
    over store file. Once store file has any reference, it makes sure that region can't be split
    @throws Exception
     */
    @Test
    public void testCanSplitJustAfterASplit() throws Exception {
        TestEndToEndSplitTransaction.LOG.info("Starting testCanSplitJustAfterASplit");
        byte[] fam = Bytes.toBytes("cf_split");
        TableName tableName = TableName.valueOf("CanSplitTable");
        Table source = TestEndToEndSplitTransaction.TEST_UTIL.getConnection().getTable(tableName);
        Admin admin = TestEndToEndSplitTransaction.TEST_UTIL.getAdmin();
        Map<String, StoreFileReader> scanner = Maps.newHashMap();
        try {
            TableDescriptor htd = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(fam)).build();
            admin.createTable(htd);
            TestEndToEndSplitTransaction.TEST_UTIL.loadTable(source, fam);
            List<HRegion> regions = TestEndToEndSplitTransaction.TEST_UTIL.getHBaseCluster().getRegions(tableName);
            regions.get(0).forceSplit(null);
            admin.split(tableName);
            while ((regions.size()) <= 1) {
                regions = TestEndToEndSplitTransaction.TEST_UTIL.getHBaseCluster().getRegions(tableName);
                regions.stream().forEach(( r) -> r.getStores().get(0).getStorefiles().stream().filter(( s) -> (s.isReference()) && (!(scanner.containsKey(r.getRegionInfo().getEncodedName())))).forEach(( sf) -> {
                    StoreFileReader reader = ((HStoreFile) (sf)).getReader();
                    reader.getStoreFileScanner(true, false, false, 0, 0, false);
                    scanner.put(r.getRegionInfo().getEncodedName(), reader);
                    LOG.info(((("Got reference to file = " + (sf.getPath())) + ",for region = ") + (r.getRegionInfo().getEncodedName())));
                }));
            } 
            Assert.assertTrue("Regions did not split properly", ((regions.size()) > 1));
            Assert.assertTrue("Could not get reference any of the store file", ((scanner.size()) > 1));
            RetryCounter retrier = new RetryCounter(30, 1, TimeUnit.SECONDS);
            while (((CompactionState.NONE) != (admin.getCompactionState(tableName))) && (retrier.shouldRetry())) {
                retrier.sleepUntilNextRetry();
            } 
            Assert.assertEquals("Compaction did not complete in 30 secs", NONE, admin.getCompactionState(tableName));
            regions.stream().filter(( region) -> scanner.containsKey(region.getRegionInfo().getEncodedName())).forEach(( r) -> Assert.assertTrue("Contains an open file reference which can be split", (!(r.getStores().get(0).canSplit()))));
        } finally {
            scanner.values().stream().forEach(( s) -> {
                try {
                    s.close(true);
                } catch ( ioe) {
                    LOG.error("Failed while closing store file", ioe);
                }
            });
            scanner.clear();
            if (source != null) {
                source.close();
            }
            TestEndToEndSplitTransaction.TEST_UTIL.deleteTableIfAny(tableName);
        }
    }

    /**
     * Tests that the client sees meta table changes as atomic during splits
     */
    @Test
    public void testFromClientSideWhileSplitting() throws Throwable {
        TestEndToEndSplitTransaction.LOG.info("Starting testFromClientSideWhileSplitting");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final byte[] FAMILY = Bytes.toBytes("family");
        // SplitTransaction will update the meta table by offlining the parent region, and adding info
        // for daughters.
        Table table = TestEndToEndSplitTransaction.TEST_UTIL.createTable(tableName, FAMILY);
        Stoppable stopper = new StoppableImplementation();
        TestEndToEndSplitTransaction.RegionSplitter regionSplitter = new TestEndToEndSplitTransaction.RegionSplitter(table);
        TestEndToEndSplitTransaction.RegionChecker regionChecker = new TestEndToEndSplitTransaction.RegionChecker(TestEndToEndSplitTransaction.CONF, stopper, tableName);
        final ChoreService choreService = new ChoreService("TEST_SERVER");
        choreService.scheduleChore(regionChecker);
        regionSplitter.start();
        // wait until the splitter is finished
        regionSplitter.join();
        stopper.stop(null);
        if ((regionChecker.ex) != null) {
            throw new AssertionError("regionChecker", regionChecker.ex);
        }
        if ((regionSplitter.ex) != null) {
            throw new AssertionError("regionSplitter", regionSplitter.ex);
        }
        // one final check
        regionChecker.verify();
    }

    static class RegionSplitter extends Thread {
        final Connection connection;

        Throwable ex;

        Table table;

        TableName tableName;

        byte[] family;

        Admin admin;

        HRegionServer rs;

        RegionSplitter(Table table) throws IOException {
            this.table = table;
            this.tableName = table.getName();
            this.family = table.getTableDescriptor().getFamiliesKeys().iterator().next();
            admin = TestEndToEndSplitTransaction.TEST_UTIL.getAdmin();
            rs = TestEndToEndSplitTransaction.TEST_UTIL.getMiniHBaseCluster().getRegionServer(0);
            connection = TestEndToEndSplitTransaction.TEST_UTIL.getConnection();
        }

        @Override
        public void run() {
            try {
                Random random = new Random();
                for (int i = 0; i < 5; i++) {
                    List<RegionInfo> regions = MetaTableAccessor.getTableRegions(connection, tableName, true);
                    if (regions.isEmpty()) {
                        continue;
                    }
                    int regionIndex = random.nextInt(regions.size());
                    // pick a random region and split it into two
                    RegionInfo region = Iterators.get(regions.iterator(), regionIndex);
                    // pick the mid split point
                    int start = 0;
                    int end = Integer.MAX_VALUE;
                    if ((region.getStartKey().length) > 0) {
                        start = Bytes.toInt(region.getStartKey());
                    }
                    if ((region.getEndKey().length) > 0) {
                        end = Bytes.toInt(region.getEndKey());
                    }
                    int mid = start + ((end - start) / 2);
                    byte[] splitPoint = Bytes.toBytes(mid);
                    // put some rows to the regions
                    addData(start);
                    addData(mid);
                    TestEndToEndSplitTransaction.flushAndBlockUntilDone(admin, rs, region.getRegionName());
                    TestEndToEndSplitTransaction.compactAndBlockUntilDone(admin, rs, region.getRegionName());
                    TestEndToEndSplitTransaction.log(("Initiating region split for:" + (region.getRegionNameAsString())));
                    try {
                        admin.splitRegion(region.getRegionName(), splitPoint);
                        // wait until the split is complete
                        TestEndToEndSplitTransaction.blockUntilRegionSplit(TestEndToEndSplitTransaction.CONF, 50000, region.getRegionName(), true);
                    } catch (NotServingRegionException ex) {
                        // ignore
                    }
                }
            } catch (Throwable ex) {
                this.ex = ex;
            }
        }

        void addData(int start) throws IOException {
            List<Put> puts = new ArrayList<>();
            for (int i = start; i < (start + 100); i++) {
                Put put = new Put(Bytes.toBytes(i));
                put.addColumn(family, family, Bytes.toBytes(i));
                puts.add(put);
            }
            table.put(puts);
        }
    }

    /**
     * Checks regions using MetaTableAccessor and HTable methods
     */
    static class RegionChecker extends ScheduledChore {
        Connection connection;

        Configuration conf;

        TableName tableName;

        Throwable ex;

        RegionChecker(Configuration conf, Stoppable stopper, TableName tableName) throws IOException {
            super("RegionChecker", stopper, 100);
            this.conf = conf;
            this.tableName = tableName;
            this.connection = ConnectionFactory.createConnection(conf);
        }

        /**
         * verify region boundaries obtained from MetaScanner
         */
        void verifyRegionsUsingMetaTableAccessor() throws Exception {
            List<RegionInfo> regionList = MetaTableAccessor.getTableRegions(connection, tableName, true);
            verifyTableRegions(regionList.stream().collect(Collectors.toCollection(() -> new TreeSet<>(RegionInfo.COMPARATOR))));
            regionList = MetaTableAccessor.getAllRegions(connection, true);
            verifyTableRegions(regionList.stream().collect(Collectors.toCollection(() -> new TreeSet<>(RegionInfo.COMPARATOR))));
        }

        /**
         * verify region boundaries obtained from HTable.getStartEndKeys()
         */
        void verifyRegionsUsingHTable() throws IOException {
            Table table = null;
            try {
                // HTable.getStartEndKeys()
                table = connection.getTable(tableName);
                try (RegionLocator rl = connection.getRegionLocator(tableName)) {
                    Pair<byte[][], byte[][]> keys = rl.getStartEndKeys();
                    verifyStartEndKeys(keys);
                    Set<RegionInfo> regions = new java.util.TreeSet(RegionInfo.COMPARATOR);
                    for (HRegionLocation loc : rl.getAllRegionLocations()) {
                        regions.add(loc.getRegionInfo());
                    }
                    verifyTableRegions(regions);
                }
            } finally {
                IOUtils.closeQuietly(table);
            }
        }

        void verify() throws Exception {
            verifyRegionsUsingMetaTableAccessor();
            verifyRegionsUsingHTable();
        }

        void verifyTableRegions(Set<RegionInfo> regions) {
            TestEndToEndSplitTransaction.log(((("Verifying " + (regions.size())) + " regions: ") + regions));
            byte[][] startKeys = new byte[regions.size()][];
            byte[][] endKeys = new byte[regions.size()][];
            int i = 0;
            for (RegionInfo region : regions) {
                startKeys[i] = region.getStartKey();
                endKeys[i] = region.getEndKey();
                i++;
            }
            Pair<byte[][], byte[][]> keys = new Pair(startKeys, endKeys);
            verifyStartEndKeys(keys);
        }

        void verifyStartEndKeys(Pair<byte[][], byte[][]> keys) {
            byte[][] startKeys = keys.getFirst();
            byte[][] endKeys = keys.getSecond();
            Assert.assertEquals(startKeys.length, endKeys.length);
            Assert.assertTrue("Found 0 regions for the table", ((startKeys.length) > 0));
            Assert.assertArrayEquals("Start key for the first region is not byte[0]", EMPTY_START_ROW, startKeys[0]);
            byte[] prevEndKey = HConstants.EMPTY_START_ROW;
            // ensure that we do not have any gaps
            for (int i = 0; i < (startKeys.length); i++) {
                Assert.assertArrayEquals(((("Hole in hbase:meta is detected. prevEndKey=" + (Bytes.toStringBinary(prevEndKey))) + " ,regionStartKey=") + (Bytes.toStringBinary(startKeys[i]))), prevEndKey, startKeys[i]);
                prevEndKey = endKeys[i];
            }
            Assert.assertArrayEquals("End key for the last region is not byte[0]", EMPTY_END_ROW, endKeys[((endKeys.length) - 1)]);
        }

        @Override
        protected void chore() {
            try {
                verify();
            } catch (Throwable ex) {
                this.ex = ex;
                getStopper().stop("caught exception");
            }
        }
    }
}

