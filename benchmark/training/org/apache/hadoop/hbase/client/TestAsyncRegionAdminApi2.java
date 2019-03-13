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


import HConstants.CATALOG_FAMILY;
import java.util.List;
import java.util.Optional;
import org.apache.hadoop.hbase.AsyncMetaTableAccessor;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Class to test asynchronous region admin operations.
 *
 * @see TestAsyncRegionAdminApi This test and it used to be joined it was taking longer than our
ten minute timeout so they were split.
 */
@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncRegionAdminApi2 extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncRegionAdminApi2.class);

    @Test
    public void testGetRegionLocation() throws Exception {
        RawAsyncHBaseAdmin rawAdmin = ((RawAsyncHBaseAdmin) (TestAsyncAdminBase.ASYNC_CONN.getAdmin()));
        TestAsyncAdminBase.TEST_UTIL.createMultiRegionTable(tableName, CATALOG_FAMILY);
        AsyncTableRegionLocator locator = TestAsyncAdminBase.ASYNC_CONN.getRegionLocator(tableName);
        HRegionLocation regionLocation = locator.getRegionLocation(Bytes.toBytes("mmm")).get();
        RegionInfo region = regionLocation.getRegion();
        byte[] regionName = regionLocation.getRegion().getRegionName();
        HRegionLocation location = rawAdmin.getRegionLocation(regionName).get();
        Assert.assertTrue(Bytes.equals(regionName, location.getRegion().getRegionName()));
        location = rawAdmin.getRegionLocation(region.getEncodedNameAsBytes()).get();
        Assert.assertTrue(Bytes.equals(regionName, location.getRegion().getRegionName()));
    }

    @Test
    public void testSplitSwitch() throws Exception {
        createTableWithDefaultConf(tableName);
        byte[][] families = new byte[][]{ TestAsyncAdminBase.FAMILY };
        final int rows = 10000;
        TestAsyncRegionAdminApi.loadData(tableName, families, rows);
        AsyncTable<AdvancedScanResultConsumer> metaTable = TestAsyncAdminBase.ASYNC_CONN.getTable(TableName.META_TABLE_NAME);
        List<HRegionLocation> regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName)).get();
        int originalCount = regionLocations.size();
        initSplitMergeSwitch();
        Assert.assertTrue(admin.splitSwitch(false).get());
        try {
            admin.split(tableName, Bytes.toBytes((rows / 2))).join();
        } catch (Exception e) {
            // Expected
        }
        int count = admin.getRegions(tableName).get().size();
        Assert.assertTrue((originalCount == count));
        Assert.assertFalse(admin.splitSwitch(true).get());
        admin.split(tableName).join();
        while ((count = admin.getRegions(tableName).get().size()) == originalCount) {
            Threads.sleep(100);
        } 
        Assert.assertTrue((originalCount < count));
    }

    @Test
    public void testMergeRegions() throws Exception {
        byte[][] splitRows = new byte[][]{ Bytes.toBytes("3"), Bytes.toBytes("6") };
        createTableWithDefaultConf(tableName, splitRows);
        AsyncTable<AdvancedScanResultConsumer> metaTable = TestAsyncAdminBase.ASYNC_CONN.getTable(TableName.META_TABLE_NAME);
        List<HRegionLocation> regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName)).get();
        RegionInfo regionA;
        RegionInfo regionB;
        // merge with full name
        Assert.assertEquals(3, regionLocations.size());
        regionA = regionLocations.get(0).getRegion();
        regionB = regionLocations.get(1).getRegion();
        admin.mergeRegions(regionA.getRegionName(), regionB.getRegionName(), false).get();
        regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName)).get();
        Assert.assertEquals(2, regionLocations.size());
        // merge with encoded name
        regionA = regionLocations.get(0).getRegion();
        regionB = regionLocations.get(1).getRegion();
        admin.mergeRegions(regionA.getRegionName(), regionB.getRegionName(), false).get();
        regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName)).get();
        Assert.assertEquals(1, regionLocations.size());
    }

    @Test
    public void testSplitTable() throws Exception {
        initSplitMergeSwitch();
        splitTest(TableName.valueOf("testSplitTable"), 3000, false, null);
        splitTest(TableName.valueOf("testSplitTableWithSplitPoint"), 3000, false, Bytes.toBytes("3"));
        splitTest(TableName.valueOf("testSplitTableRegion"), 3000, true, null);
        splitTest(TableName.valueOf("testSplitTableRegionWithSplitPoint2"), 3000, true, Bytes.toBytes("3"));
    }
}

