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
package org.apache.hadoop.hbase.client;


import RegionLocateType.AFTER;
import RegionLocateType.BEFORE;
import RegionLocateType.CURRENT;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.NotServingRegionException;
import org.apache.hadoop.hbase.RegionLocations;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncNonMetaRegionLocator {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncNonMetaRegionLocator.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("async");

    private static byte[] FAMILY = Bytes.toBytes("cf");

    private static AsyncConnectionImpl CONN;

    private static AsyncNonMetaRegionLocator LOCATOR;

    private static byte[][] SPLIT_KEYS;

    @Test
    public void testNoTable() throws InterruptedException {
        for (RegionLocateType locateType : RegionLocateType.values()) {
            try {
                getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, locateType, false).get();
            } catch (ExecutionException e) {
                Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(TableNotFoundException.class));
            }
        }
    }

    @Test
    public void testDisableTable() throws IOException, InterruptedException {
        createSingleRegionTable();
        TestAsyncNonMetaRegionLocator.TEST_UTIL.getAdmin().disableTable(TestAsyncNonMetaRegionLocator.TABLE_NAME);
        for (RegionLocateType locateType : RegionLocateType.values()) {
            try {
                getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, locateType, false).get();
            } catch (ExecutionException e) {
                Assert.assertThat(e.getCause(), CoreMatchers.instanceOf(TableNotFoundException.class));
            }
        }
    }

    @Test
    public void testSingleRegionTable() throws IOException, InterruptedException, ExecutionException {
        createSingleRegionTable();
        ServerName serverName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getRSForFirstRegionInTable(TestAsyncNonMetaRegionLocator.TABLE_NAME).getServerName();
        for (RegionLocateType locateType : RegionLocateType.values()) {
            assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, serverName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, locateType, false).get());
        }
        byte[] randKey = new byte[ThreadLocalRandom.current().nextInt(128)];
        ThreadLocalRandom.current().nextBytes(randKey);
        for (RegionLocateType locateType : RegionLocateType.values()) {
            assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, serverName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, randKey, locateType, false).get());
        }
    }

    @Test
    public void testMultiRegionTable() throws IOException, InterruptedException {
        createMultiRegionTable();
        byte[][] startKeys = TestAsyncNonMetaRegionLocator.getStartKeys();
        ServerName[] serverNames = getLocations(startKeys);
        IntStream.range(0, 2).forEach(( n) -> IntStream.range(0, startKeys.length).forEach(( i) -> {
            try {
                assertLocEquals(startKeys[i], (i == ((startKeys.length) - 1) ? HConstants.EMPTY_END_ROW : startKeys[(i + 1)]), serverNames[i], getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, startKeys[i], CURRENT, false).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }));
        TestAsyncNonMetaRegionLocator.LOCATOR.clearCache(TestAsyncNonMetaRegionLocator.TABLE_NAME);
        IntStream.range(0, 2).forEach(( n) -> IntStream.range(0, startKeys.length).forEach(( i) -> {
            try {
                assertLocEquals(startKeys[i], (i == ((startKeys.length) - 1) ? HConstants.EMPTY_END_ROW : startKeys[(i + 1)]), serverNames[i], getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, startKeys[i], AFTER, false).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }));
        TestAsyncNonMetaRegionLocator.LOCATOR.clearCache(TestAsyncNonMetaRegionLocator.TABLE_NAME);
        byte[][] endKeys = TestAsyncNonMetaRegionLocator.getEndKeys();
        IntStream.range(0, 2).forEach(( n) -> IntStream.range(0, endKeys.length).map(( i) -> ((endKeys.length) - 1) - i).forEach(( i) -> {
            try {
                assertLocEquals((i == 0 ? HConstants.EMPTY_START_ROW : endKeys[(i - 1)]), endKeys[i], serverNames[i], getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, endKeys[i], BEFORE, false).get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Test
    public void testRegionMove() throws IOException, InterruptedException, ExecutionException {
        createSingleRegionTable();
        ServerName serverName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getRSForFirstRegionInTable(TestAsyncNonMetaRegionLocator.TABLE_NAME).getServerName();
        HRegionLocation loc = getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, false).get();
        assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, serverName, loc);
        ServerName newServerName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( t) -> t.getRegionServer().getServerName()).filter(( sn) -> !(sn.equals(serverName))).findAny().get();
        TestAsyncNonMetaRegionLocator.TEST_UTIL.getAdmin().move(Bytes.toBytes(loc.getRegion().getEncodedName()), Bytes.toBytes(newServerName.getServerName()));
        while (!(TestAsyncNonMetaRegionLocator.TEST_UTIL.getRSForFirstRegionInTable(TestAsyncNonMetaRegionLocator.TABLE_NAME).getServerName().equals(newServerName))) {
            Thread.sleep(100);
        } 
        // Should be same as it is in cache
        Assert.assertSame(loc, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, false).get());
        TestAsyncNonMetaRegionLocator.LOCATOR.updateCachedLocationOnError(loc, null);
        // null error will not trigger a cache cleanup
        Assert.assertSame(loc, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, false).get());
        TestAsyncNonMetaRegionLocator.LOCATOR.updateCachedLocationOnError(loc, new NotServingRegionException());
        assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, newServerName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, false).get());
    }

    // usually locate after will return the same result, so we add a test to make it return different
    // result.
    @Test
    public void testLocateAfter() throws IOException, InterruptedException, ExecutionException {
        byte[] row = Bytes.toBytes("1");
        byte[] splitKey = Arrays.copyOf(row, 2);
        TestAsyncNonMetaRegionLocator.TEST_UTIL.createTable(TestAsyncNonMetaRegionLocator.TABLE_NAME, TestAsyncNonMetaRegionLocator.FAMILY, new byte[][]{ splitKey });
        TestAsyncNonMetaRegionLocator.TEST_UTIL.waitTableAvailable(TestAsyncNonMetaRegionLocator.TABLE_NAME);
        HRegionLocation currentLoc = getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, row, CURRENT, false).get();
        ServerName currentServerName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getRSForFirstRegionInTable(TestAsyncNonMetaRegionLocator.TABLE_NAME).getServerName();
        assertLocEquals(HConstants.EMPTY_START_ROW, splitKey, currentServerName, currentLoc);
        HRegionLocation afterLoc = getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, row, AFTER, false).get();
        ServerName afterServerName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( t) -> t.getRegionServer()).filter(( rs) -> rs.getRegions(TestAsyncNonMetaRegionLocator.TABLE_NAME).stream().anyMatch(( r) -> Bytes.equals(splitKey, r.getRegionInfo().getStartKey()))).findAny().get().getServerName();
        assertLocEquals(splitKey, HConstants.EMPTY_END_ROW, afterServerName, afterLoc);
        Assert.assertSame(afterLoc, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, row, AFTER, false).get());
    }

    // For HBASE-17402
    @Test
    public void testConcurrentLocate() throws IOException, InterruptedException, ExecutionException {
        createMultiRegionTable();
        byte[][] startKeys = TestAsyncNonMetaRegionLocator.getStartKeys();
        byte[][] endKeys = TestAsyncNonMetaRegionLocator.getEndKeys();
        ServerName[] serverNames = getLocations(startKeys);
        for (int i = 0; i < 100; i++) {
            TestAsyncNonMetaRegionLocator.LOCATOR.clearCache(TestAsyncNonMetaRegionLocator.TABLE_NAME);
            List<CompletableFuture<HRegionLocation>> futures = IntStream.range(0, 1000).mapToObj(( n) -> String.format("%03d", n)).map(( s) -> Bytes.toBytes(s)).map(( r) -> getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, r, RegionLocateType.CURRENT, false)).collect(Collectors.toList());
            for (int j = 0; j < 1000; j++) {
                int index = Math.min(8, (j / 111));
                assertLocEquals(startKeys[index], endKeys[index], serverNames[index], futures.get(j).get());
            }
        }
    }

    @Test
    public void testReload() throws Exception {
        createSingleRegionTable();
        ServerName serverName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getRSForFirstRegionInTable(TestAsyncNonMetaRegionLocator.TABLE_NAME).getServerName();
        for (RegionLocateType locateType : RegionLocateType.values()) {
            assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, serverName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, locateType, false).get());
        }
        ServerName newServerName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( t) -> t.getRegionServer().getServerName()).filter(( sn) -> !(sn.equals(serverName))).findAny().get();
        Admin admin = TestAsyncNonMetaRegionLocator.TEST_UTIL.getAdmin();
        RegionInfo region = admin.getRegions(TestAsyncNonMetaRegionLocator.TABLE_NAME).stream().findAny().get();
        admin.move(region.getEncodedNameAsBytes(), Bytes.toBytes(newServerName.getServerName()));
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                ServerName newServerName = TestAsyncNonMetaRegionLocator.TEST_UTIL.getRSForFirstRegionInTable(TestAsyncNonMetaRegionLocator.TABLE_NAME).getServerName();
                return (newServerName != null) && (!(newServerName.equals(serverName)));
            }

            @Override
            public String explainFailure() throws Exception {
                return ((region.getRegionNameAsString()) + " is still on ") + serverName;
            }
        });
        // The cached location will not change
        for (RegionLocateType locateType : RegionLocateType.values()) {
            assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, serverName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, locateType, false).get());
        }
        // should get the new location when reload = true
        assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, newServerName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, CURRENT, true).get());
        // the cached location should be replaced
        for (RegionLocateType locateType : RegionLocateType.values()) {
            assertLocEquals(HConstants.EMPTY_START_ROW, HConstants.EMPTY_END_ROW, newServerName, getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_START_ROW, locateType, false).get());
        }
    }

    // Testcase for HBASE-20822
    @Test
    public void testLocateBeforeLastRegion() throws IOException, InterruptedException, ExecutionException {
        createMultiRegionTable();
        getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, TestAsyncNonMetaRegionLocator.SPLIT_KEYS[0], CURRENT, false).join();
        HRegionLocation loc = getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, HConstants.EMPTY_END_ROW, BEFORE, false).get();
        // should locate to the last region
        Assert.assertArrayEquals(loc.getRegion().getEndKey(), HConstants.EMPTY_END_ROW);
    }

    @Test
    public void testRegionReplicas() throws Exception {
        TestAsyncNonMetaRegionLocator.TEST_UTIL.getAdmin().createTable(TableDescriptorBuilder.newBuilder(TestAsyncNonMetaRegionLocator.TABLE_NAME).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncNonMetaRegionLocator.FAMILY)).setRegionReplication(3).build());
        TestAsyncNonMetaRegionLocator.TEST_UTIL.waitUntilAllRegionsAssigned(TestAsyncNonMetaRegionLocator.TABLE_NAME);
        RegionReplicaTestHelper.testLocator(TestAsyncNonMetaRegionLocator.TEST_UTIL, TestAsyncNonMetaRegionLocator.TABLE_NAME, new RegionReplicaTestHelper.Locator() {
            @Override
            public void updateCachedLocationOnError(HRegionLocation loc, Throwable error) throws Exception {
                TestAsyncNonMetaRegionLocator.LOCATOR.updateCachedLocationOnError(loc, error);
            }

            @Override
            public RegionLocations getRegionLocations(TableName tableName, int replicaId, boolean reload) throws Exception {
                return TestAsyncNonMetaRegionLocator.LOCATOR.getRegionLocations(tableName, HConstants.EMPTY_START_ROW, replicaId, CURRENT, reload).get();
            }
        });
    }

    // Testcase for HBASE-21961
    @Test
    public void testLocateBeforeInOnlyRegion() throws IOException, InterruptedException {
        createSingleRegionTable();
        HRegionLocation loc = getDefaultRegionLocation(TestAsyncNonMetaRegionLocator.TABLE_NAME, Bytes.toBytes(1), BEFORE, false).join();
        // should locate to the only region
        Assert.assertArrayEquals(loc.getRegion().getStartKey(), HConstants.EMPTY_START_ROW);
        Assert.assertArrayEquals(loc.getRegion().getEndKey(), HConstants.EMPTY_END_ROW);
    }
}

