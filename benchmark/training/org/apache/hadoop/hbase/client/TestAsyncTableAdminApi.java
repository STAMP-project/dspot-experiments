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


import TableState.State.ENABLED;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import org.apache.hadoop.hbase.AsyncMetaTableAccessor;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.master.LoadBalancer;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Class to test asynchronous table admin operations.
 *
 * @see TestAsyncTableAdminApi2 This test and it used to be joined it was taking longer than our
ten minute timeout so they were split.
 * @see TestAsyncTableAdminApi3 Another split out from this class so each runs under ten minutes.
 */
@RunWith(Parameterized.class)
@Category({ LargeTests.class, ClientTests.class })
public class TestAsyncTableAdminApi extends TestAsyncAdminBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableAdminApi.class);

    @Test
    public void testCreateTable() throws Exception {
        List<TableDescriptor> tables = admin.listTableDescriptors().get();
        int numTables = tables.size();
        createTableWithDefaultConf(tableName);
        tables = admin.listTableDescriptors().get();
        Assert.assertEquals((numTables + 1), tables.size());
        Assert.assertTrue("Table must be enabled.", TestAsyncAdminBase.TEST_UTIL.getHBaseCluster().getMaster().getTableStateManager().isTableState(tableName, ENABLED));
        Assert.assertEquals(ENABLED, TestAsyncTableAdminApi.getStateFromMeta(tableName));
    }

    @Test
    public void testCreateTableNumberOfRegions() throws Exception {
        AsyncTable<AdvancedScanResultConsumer> metaTable = TestAsyncAdminBase.ASYNC_CONN.getTable(TableName.META_TABLE_NAME);
        createTableWithDefaultConf(tableName);
        List<HRegionLocation> regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName)).get();
        Assert.assertEquals("Table should have only 1 region", 1, regionLocations.size());
        final TableName tableName2 = TableName.valueOf(((tableName.getNameAsString()) + "_2"));
        createTableWithDefaultConf(tableName2, new byte[][]{ new byte[]{ 42 } });
        regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName2)).get();
        Assert.assertEquals("Table should have only 2 region", 2, regionLocations.size());
        final TableName tableName3 = TableName.valueOf(((tableName.getNameAsString()) + "_3"));
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName3);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY));
        admin.createTable(builder.build(), Bytes.toBytes("a"), Bytes.toBytes("z"), 3).join();
        regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName3)).get();
        Assert.assertEquals("Table should have only 3 region", 3, regionLocations.size());
        final TableName tableName4 = TableName.valueOf(((tableName.getNameAsString()) + "_4"));
        builder = TableDescriptorBuilder.newBuilder(tableName4);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY));
        try {
            admin.createTable(builder.build(), Bytes.toBytes("a"), Bytes.toBytes("z"), 2).join();
            Assert.fail("Should not be able to create a table with only 2 regions using this API.");
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
        final TableName tableName5 = TableName.valueOf(((tableName.getNameAsString()) + "_5"));
        builder = TableDescriptorBuilder.newBuilder(tableName5);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY));
        admin.createTable(builder.build(), new byte[]{ 1 }, new byte[]{ 127 }, 16).join();
        regionLocations = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName5)).get();
        Assert.assertEquals("Table should have 16 region", 16, regionLocations.size());
    }

    @Test
    public void testCreateTableWithRegions() throws Exception {
        byte[][] splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 }, new byte[]{ 3, 3, 3 }, new byte[]{ 4, 4, 4 }, new byte[]{ 5, 5, 5 }, new byte[]{ 6, 6, 6 }, new byte[]{ 7, 7, 7 }, new byte[]{ 8, 8, 8 }, new byte[]{ 9, 9, 9 } };
        int expectedRegions = (splitKeys.length) + 1;
        boolean tablesOnMaster = LoadBalancer.isTablesOnMaster(TestAsyncAdminBase.TEST_UTIL.getConfiguration());
        createTableWithDefaultConf(tableName, splitKeys);
        boolean tableAvailable = admin.isTableAvailable(tableName, splitKeys).get();
        Assert.assertTrue("Table should be created with splitKyes + 1 rows in META", tableAvailable);
        AsyncTable<AdvancedScanResultConsumer> metaTable = TestAsyncAdminBase.ASYNC_CONN.getTable(TableName.META_TABLE_NAME);
        List<HRegionLocation> regions = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName)).get();
        Iterator<HRegionLocation> hris = regions.iterator();
        Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
        System.err.println((("Found " + (regions.size())) + " regions"));
        RegionInfo hri;
        hris = regions.iterator();
        hri = hris.next().getRegion();
        Assert.assertTrue((((hri.getStartKey()) == null) || ((hri.getStartKey().length) == 0)));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[0]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[0]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[1]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[1]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[2]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[2]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[3]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[3]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[4]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[4]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[5]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[5]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[6]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[6]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[7]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[7]));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[8]));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[8]));
        Assert.assertTrue((((hri.getEndKey()) == null) || ((hri.getEndKey().length) == 0)));
        if (tablesOnMaster) {
            verifyRoundRobinDistribution(regions, expectedRegions);
        }
        // Now test using start/end with a number of regions
        // Use 80 bit numbers to make sure we aren't limited
        byte[] startKey = new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        byte[] endKey = new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 };
        // Splitting into 10 regions, we expect (null,1) ... (9, null)
        // with (1,2) (2,3) (3,4) (4,5) (5,6) (6,7) (7,8) (8,9) in the middle
        expectedRegions = 10;
        final TableName tableName2 = TableName.valueOf(((tableName.getNameAsString()) + "_2"));
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableName2);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY));
        admin.createTable(builder.build(), startKey, endKey, expectedRegions).join();
        regions = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName2)).get();
        Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
        System.err.println((("Found " + (regions.size())) + " regions"));
        hris = regions.iterator();
        hri = hris.next().getRegion();
        Assert.assertTrue((((hri.getStartKey()) == null) || ((hri.getStartKey().length) == 0)));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 }));
        Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 }));
        hri = hris.next().getRegion();
        Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 }));
        Assert.assertTrue((((hri.getEndKey()) == null) || ((hri.getEndKey().length) == 0)));
        if (tablesOnMaster) {
            // This don't work if master is not carrying regions. FIX. TODO.
            verifyRoundRobinDistribution(regions, expectedRegions);
        }
        // Try once more with something that divides into something infinite
        startKey = new byte[]{ 0, 0, 0, 0, 0, 0 };
        endKey = new byte[]{ 1, 0, 0, 0, 0, 0 };
        expectedRegions = 5;
        final TableName tableName3 = TableName.valueOf(((tableName.getNameAsString()) + "_3"));
        builder = TableDescriptorBuilder.newBuilder(tableName3);
        builder.setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestAsyncAdminBase.FAMILY));
        admin.createTable(builder.build(), startKey, endKey, expectedRegions).join();
        regions = AsyncMetaTableAccessor.getTableHRegionLocations(metaTable, Optional.of(tableName3)).get();
        Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
        System.err.println((("Found " + (regions.size())) + " regions"));
        if (tablesOnMaster) {
            // This don't work if master is not carrying regions. FIX. TODO.
            verifyRoundRobinDistribution(regions, expectedRegions);
        }
        // Try an invalid case where there are duplicate split keys
        splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 }, new byte[]{ 3, 3, 3 }, new byte[]{ 2, 2, 2 } };
        final TableName tableName4 = TableName.valueOf(((tableName.getNameAsString()) + "_4"));
        try {
            createTableWithDefaultConf(tableName4, splitKeys);
            Assert.fail(("Should not be able to create this table because of " + "duplicate split keys"));
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testCreateTableWithOnlyEmptyStartRow() throws Exception {
        byte[][] splitKeys = new byte[1][];
        splitKeys[0] = HConstants.EMPTY_BYTE_ARRAY;
        try {
            createTableWithDefaultConf(tableName, splitKeys);
            Assert.fail("Test case should fail as empty split key is passed.");
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testCreateTableWithEmptyRowInTheSplitKeys() throws Exception {
        byte[][] splitKeys = new byte[3][];
        splitKeys[0] = Bytes.toBytes("region1");
        splitKeys[1] = HConstants.EMPTY_BYTE_ARRAY;
        splitKeys[2] = Bytes.toBytes("region2");
        try {
            createTableWithDefaultConf(tableName, splitKeys);
            Assert.fail("Test case should fail as empty split key is passed.");
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof IllegalArgumentException));
        }
    }

    @Test
    public void testDeleteTable() throws Exception {
        createTableWithDefaultConf(tableName);
        Assert.assertTrue(admin.tableExists(tableName).get());
        TestAsyncAdminBase.TEST_UTIL.getAdmin().disableTable(tableName);
        admin.deleteTable(tableName).join();
        Assert.assertFalse(admin.tableExists(tableName).get());
    }

    @Test
    public void testTruncateTable() throws Exception {
        testTruncateTable(tableName, false);
    }

    @Test
    public void testTruncateTablePreservingSplits() throws Exception {
        testTruncateTable(tableName, true);
    }

    @Test
    public void testCloneTableSchema() throws Exception {
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        testCloneTableSchema(tableName, newTableName, false);
    }

    @Test
    public void testCloneTableSchemaPreservingSplits() throws Exception {
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        testCloneTableSchema(tableName, newTableName, true);
    }

    @Test
    public void testCloneTableSchemaWithNonExistentSourceTable() throws Exception {
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        // test for non-existent source table
        try {
            admin.cloneTableSchema(tableName, newTableName, false).join();
            Assert.fail("Should have failed when source table doesn't exist.");
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TableNotFoundException));
        }
    }

    @Test
    public void testCloneTableSchemaWithExistentDestinationTable() throws Exception {
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        byte[] FAMILY_0 = Bytes.toBytes("cf0");
        TestAsyncAdminBase.TEST_UTIL.createTable(tableName, FAMILY_0);
        TestAsyncAdminBase.TEST_UTIL.createTable(newTableName, FAMILY_0);
        // test for existent destination table
        try {
            admin.cloneTableSchema(tableName, newTableName, false).join();
            Assert.fail("Should have failed when destination table exists.");
        } catch (CompletionException e) {
            Assert.assertTrue(((e.getCause()) instanceof TableExistsException));
        }
    }
}

