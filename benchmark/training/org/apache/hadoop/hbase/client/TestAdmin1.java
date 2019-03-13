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
import HConstants.NO_NONCE;
import TableDescriptor.COMPARATOR;
import TableState.State.DISABLED;
import TableState.State.ENABLED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.InvalidFamilyOperationException;
import org.apache.hadoop.hbase.MetaTableAccessor;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableExistsException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotDisabledException;
import org.apache.hadoop.hbase.TableNotEnabledException;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.exceptions.MergeRegionException;
import org.apache.hadoop.hbase.regionserver.DisabledRegionSplitPolicy;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HStore;
import org.apache.hadoop.hbase.regionserver.HStoreFile;
import org.apache.hadoop.hbase.shaded.protobuf.RequestConverter;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.MergeTableRegionsRequest;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.Pair;
import org.apache.hadoop.hbase.util.Threads;
import org.apache.hbase.thirdparty.com.google.protobuf.ServiceException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class to test HBaseAdmin.
 * Spins up the minicluster once at test start and then takes it down afterward.
 * Add any testing of HBaseAdmin functionality here.
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestAdmin1 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAdmin1.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestAdmin1.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private Admin admin;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testSplitFlushCompactUnknownTable() throws InterruptedException {
        final TableName unknowntable = TableName.valueOf(name.getMethodName());
        Exception exception = null;
        try {
            this.admin.compact(unknowntable);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.flush(unknowntable);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.split(unknowntable);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
    }

    @Test
    public void testDeleteEditUnknownColumnFamilyAndOrTable() throws IOException {
        // Test we get exception if we try to
        final TableName nonexistentTable = TableName.valueOf("nonexistent");
        final byte[] nonexistentColumn = Bytes.toBytes("nonexistent");
        HColumnDescriptor nonexistentHcd = new HColumnDescriptor(nonexistentColumn);
        Exception exception = null;
        try {
            this.admin.addColumnFamily(nonexistentTable, nonexistentHcd);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.deleteTable(nonexistentTable);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.deleteColumnFamily(nonexistentTable, nonexistentColumn);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.disableTable(nonexistentTable);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.enableTable(nonexistentTable);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            this.admin.modifyColumnFamily(nonexistentTable, nonexistentHcd);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        exception = null;
        try {
            HTableDescriptor htd = new HTableDescriptor(nonexistentTable);
            htd.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
            this.admin.modifyTable(htd.getTableName(), htd);
        } catch (IOException e) {
            exception = e;
        }
        Assert.assertTrue((exception instanceof TableNotFoundException));
        // Now make it so at least the table exists and then do tests against a
        // nonexistent column family -- see if we get right exceptions.
        final TableName tableName = TableName.valueOf(((name.getMethodName()) + (System.currentTimeMillis())));
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(new HColumnDescriptor("cf"));
        this.admin.createTable(htd);
        try {
            exception = null;
            try {
                this.admin.deleteColumnFamily(htd.getTableName(), nonexistentHcd.getName());
            } catch (IOException e) {
                exception = e;
            }
            Assert.assertTrue(("found=" + (exception.getClass().getName())), (exception instanceof InvalidFamilyOperationException));
            exception = null;
            try {
                this.admin.modifyColumnFamily(htd.getTableName(), nonexistentHcd);
            } catch (IOException e) {
                exception = e;
            }
            Assert.assertTrue(("found=" + (exception.getClass().getName())), (exception instanceof InvalidFamilyOperationException));
        } finally {
            this.admin.disableTable(tableName);
            this.admin.deleteTable(tableName);
        }
    }

    @Test
    public void testDisableAndEnableTable() throws IOException {
        final byte[] row = Bytes.toBytes("row");
        final byte[] qualifier = Bytes.toBytes("qualifier");
        final byte[] value = Bytes.toBytes("value");
        final TableName table = TableName.valueOf(name.getMethodName());
        Table ht = TestAdmin1.TEST_UTIL.createTable(table, CATALOG_FAMILY);
        Put put = new Put(row);
        put.addColumn(CATALOG_FAMILY, qualifier, value);
        ht.put(put);
        Get get = new Get(row);
        get.addColumn(CATALOG_FAMILY, qualifier);
        ht.get(get);
        this.admin.disableTable(ht.getName());
        Assert.assertTrue("Table must be disabled.", TestAdmin1.TEST_UTIL.getHBaseCluster().getMaster().getTableStateManager().isTableState(ht.getName(), DISABLED));
        Assert.assertEquals(DISABLED, getStateFromMeta(table));
        // Test that table is disabled
        get = new Get(row);
        get.addColumn(CATALOG_FAMILY, qualifier);
        boolean ok = false;
        try {
            ht.get(get);
        } catch (TableNotEnabledException e) {
            ok = true;
        }
        ok = false;
        // verify that scan encounters correct exception
        Scan scan = new Scan();
        try {
            ResultScanner scanner = ht.getScanner(scan);
            Result res = null;
            do {
                res = scanner.next();
            } while (res != null );
        } catch (TableNotEnabledException e) {
            ok = true;
        }
        Assert.assertTrue(ok);
        this.admin.enableTable(table);
        Assert.assertTrue("Table must be enabled.", TestAdmin1.TEST_UTIL.getHBaseCluster().getMaster().getTableStateManager().isTableState(ht.getName(), ENABLED));
        Assert.assertEquals(ENABLED, getStateFromMeta(table));
        // Test that table is enabled
        try {
            ht.get(get);
        } catch (RetriesExhaustedException e) {
            ok = false;
        }
        Assert.assertTrue(ok);
        ht.close();
    }

    @Test
    public void testDisableAndEnableTables() throws IOException {
        final byte[] row = Bytes.toBytes("row");
        final byte[] qualifier = Bytes.toBytes("qualifier");
        final byte[] value = Bytes.toBytes("value");
        final TableName table1 = TableName.valueOf(((name.getMethodName()) + "1"));
        final TableName table2 = TableName.valueOf(((name.getMethodName()) + "2"));
        Table ht1 = TestAdmin1.TEST_UTIL.createTable(table1, CATALOG_FAMILY);
        Table ht2 = TestAdmin1.TEST_UTIL.createTable(table2, CATALOG_FAMILY);
        Put put = new Put(row);
        put.addColumn(CATALOG_FAMILY, qualifier, value);
        ht1.put(put);
        ht2.put(put);
        Get get = new Get(row);
        get.addColumn(CATALOG_FAMILY, qualifier);
        ht1.get(get);
        ht2.get(get);
        this.admin.disableTables("testDisableAndEnableTable.*");
        // Test that tables are disabled
        get = new Get(row);
        get.addColumn(CATALOG_FAMILY, qualifier);
        boolean ok = false;
        try {
            ht1.get(get);
            ht2.get(get);
        } catch (org.apache.hadoop.hbase.DoNotRetryIOException e) {
            ok = true;
        }
        Assert.assertEquals(DISABLED, getStateFromMeta(table1));
        Assert.assertEquals(DISABLED, getStateFromMeta(table2));
        Assert.assertTrue(ok);
        this.admin.enableTables("testDisableAndEnableTable.*");
        // Test that tables are enabled
        try {
            ht1.get(get);
        } catch (IOException e) {
            ok = false;
        }
        try {
            ht2.get(get);
        } catch (IOException e) {
            ok = false;
        }
        Assert.assertTrue(ok);
        ht1.close();
        ht2.close();
        Assert.assertEquals(ENABLED, getStateFromMeta(table1));
        Assert.assertEquals(ENABLED, getStateFromMeta(table2));
    }

    @Test
    public void testCreateTable() throws IOException {
        HTableDescriptor[] tables = admin.listTables();
        int numTables = tables.length;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin1.TEST_UTIL.createTable(tableName, CATALOG_FAMILY).close();
        tables = this.admin.listTables();
        Assert.assertEquals((numTables + 1), tables.length);
        Assert.assertTrue("Table must be enabled.", TestAdmin1.TEST_UTIL.getHBaseCluster().getMaster().getTableStateManager().isTableState(tableName, ENABLED));
        Assert.assertEquals(ENABLED, getStateFromMeta(tableName));
    }

    @Test
    public void testTruncateTable() throws IOException {
        testTruncateTable(TableName.valueOf(name.getMethodName()), false);
    }

    @Test
    public void testTruncateTablePreservingSplits() throws IOException {
        testTruncateTable(TableName.valueOf(name.getMethodName()), true);
    }

    @Test
    public void testGetTableDescriptor() throws IOException {
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        HColumnDescriptor fam2 = new HColumnDescriptor("fam2");
        HColumnDescriptor fam3 = new HColumnDescriptor("fam3");
        HTableDescriptor htd = new HTableDescriptor(TableName.valueOf(name.getMethodName()));
        htd.addFamily(fam1);
        htd.addFamily(fam2);
        htd.addFamily(fam3);
        this.admin.createTable(htd);
        Table table = TestAdmin1.TEST_UTIL.getConnection().getTable(htd.getTableName());
        TableDescriptor confirmedHtd = table.getDescriptor();
        Assert.assertEquals(0, COMPARATOR.compare(htd, confirmedHtd));
        MetaTableAccessor.fullScanMetaAndPrint(TestAdmin1.TEST_UTIL.getConnection());
        table.close();
    }

    @Test
    public void testCompactionTimestamps() throws Exception {
        HColumnDescriptor fam1 = new HColumnDescriptor("fam1");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(fam1);
        this.admin.createTable(htd);
        Table table = TestAdmin1.TEST_UTIL.getConnection().getTable(htd.getTableName());
        long ts = this.admin.getLastMajorCompactionTimestamp(tableName);
        Assert.assertEquals(0, ts);
        Put p = new Put(Bytes.toBytes("row1"));
        p.addColumn(Bytes.toBytes("fam1"), Bytes.toBytes("fam1"), Bytes.toBytes("fam1"));
        table.put(p);
        ts = this.admin.getLastMajorCompactionTimestamp(tableName);
        // no files written -> no data
        Assert.assertEquals(0, ts);
        this.admin.flush(tableName);
        ts = this.admin.getLastMajorCompactionTimestamp(tableName);
        // still 0, we flushed a file, but no major compaction happened
        Assert.assertEquals(0, ts);
        byte[] regionName;
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            regionName = l.getAllRegionLocations().get(0).getRegionInfo().getRegionName();
        }
        long ts1 = this.admin.getLastMajorCompactionTimestampForRegion(regionName);
        Assert.assertEquals(ts, ts1);
        p = new Put(Bytes.toBytes("row2"));
        p.addColumn(Bytes.toBytes("fam1"), Bytes.toBytes("fam1"), Bytes.toBytes("fam1"));
        table.put(p);
        this.admin.flush(tableName);
        ts = this.admin.getLastMajorCompactionTimestamp(tableName);
        // make sure the region API returns the same value, as the old file is still around
        Assert.assertEquals(ts1, ts);
        TestAdmin1.TEST_UTIL.compact(tableName, true);
        table.put(p);
        // forces a wait for the compaction
        this.admin.flush(tableName);
        ts = this.admin.getLastMajorCompactionTimestamp(tableName);
        // after a compaction our earliest timestamp will have progressed forward
        Assert.assertTrue((ts > ts1));
        // region api still the same
        ts1 = this.admin.getLastMajorCompactionTimestampForRegion(regionName);
        Assert.assertEquals(ts, ts1);
        table.put(p);
        this.admin.flush(tableName);
        ts = this.admin.getLastMajorCompactionTimestamp(tableName);
        Assert.assertEquals(ts, ts1);
        table.close();
    }

    @Test
    public void testHColumnValidName() {
        boolean exceptionThrown;
        try {
            new HColumnDescriptor("\\test\\abc");
        } catch (IllegalArgumentException iae) {
            exceptionThrown = true;
            Assert.assertTrue(exceptionThrown);
        }
    }

    /**
     * Verify schema change for read only table
     */
    @Test
    public void testReadOnlyTableModify() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin1.TEST_UTIL.createTable(tableName, CATALOG_FAMILY).close();
        // Make table read only
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(this.admin.getDescriptor(tableName)).setReadOnly(true).build();
        admin.modifyTable(htd);
        // try to modify the read only table now
        htd = TableDescriptorBuilder.newBuilder(this.admin.getDescriptor(tableName)).setCompactionEnabled(false).build();
        admin.modifyTable(htd);
        // Delete the table
        this.admin.disableTable(tableName);
        this.admin.deleteTable(tableName);
        Assert.assertFalse(this.admin.tableExists(tableName));
    }

    @Test(expected = TableNotDisabledException.class)
    public void testModifyRegionReplicasEnabledTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin1.TEST_UTIL.createTable(tableName, CATALOG_FAMILY).close();
        // Modify region replication count
        TableDescriptor htd = TableDescriptorBuilder.newBuilder(admin.getDescriptor(tableName)).setRegionReplication(3).build();
        try {
            // try to modify the region replication count without disabling the table
            admin.modifyTable(htd);
            Assert.fail("Expected an exception");
        } finally {
            // Delete the table
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
            Assert.assertFalse(admin.tableExists(tableName));
        }
    }

    /**
     * Verify schema modification takes.
     */
    @Test
    public void testOnlineChangeTableSchema() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor[] tables = admin.listTables();
        int numTables = tables.length;
        TestAdmin1.TEST_UTIL.createTable(tableName, CATALOG_FAMILY).close();
        tables = this.admin.listTables();
        Assert.assertEquals((numTables + 1), tables.length);
        // FIRST, do htabledescriptor changes.
        HTableDescriptor htd = this.admin.getTableDescriptor(tableName);
        // Make a copy and assert copy is good.
        HTableDescriptor copy = new HTableDescriptor(htd);
        Assert.assertTrue(htd.equals(copy));
        // Now amend the copy. Introduce differences.
        long newFlushSize = (htd.getMemStoreFlushSize()) / 2;
        if (newFlushSize <= 0) {
            newFlushSize = (HTableDescriptor.DEFAULT_MEMSTORE_FLUSH_SIZE) / 2;
        }
        copy.setMemStoreFlushSize(newFlushSize);
        final String key = "anyoldkey";
        Assert.assertTrue(((htd.getValue(key)) == null));
        copy.setValue(key, key);
        boolean expectedException = false;
        try {
            admin.modifyTable(tableName, copy);
        } catch (TableNotDisabledException re) {
            expectedException = true;
        }
        Assert.assertFalse(expectedException);
        HTableDescriptor modifiedHtd = new HTableDescriptor(this.admin.getTableDescriptor(tableName));
        Assert.assertFalse(htd.equals(modifiedHtd));
        Assert.assertTrue(copy.equals(modifiedHtd));
        Assert.assertEquals(newFlushSize, modifiedHtd.getMemStoreFlushSize());
        Assert.assertEquals(key, modifiedHtd.getValue(key));
        // Now work on column family changes.
        int countOfFamilies = modifiedHtd.getFamilies().size();
        Assert.assertTrue((countOfFamilies > 0));
        HColumnDescriptor hcd = modifiedHtd.getFamilies().iterator().next();
        int maxversions = hcd.getMaxVersions();
        final int newMaxVersions = maxversions + 1;
        hcd.setMaxVersions(newMaxVersions);
        final byte[] hcdName = hcd.getName();
        expectedException = false;
        try {
            this.admin.modifyColumnFamily(tableName, hcd);
        } catch (TableNotDisabledException re) {
            expectedException = true;
        }
        Assert.assertFalse(expectedException);
        modifiedHtd = this.admin.getTableDescriptor(tableName);
        HColumnDescriptor modifiedHcd = modifiedHtd.getFamily(hcdName);
        Assert.assertEquals(newMaxVersions, modifiedHcd.getMaxVersions());
        // Try adding a column
        Assert.assertFalse(this.admin.isTableDisabled(tableName));
        final String xtracolName = "xtracol";
        HColumnDescriptor xtracol = new HColumnDescriptor(xtracolName);
        xtracol.setValue(xtracolName, xtracolName);
        expectedException = false;
        try {
            this.admin.addColumnFamily(tableName, xtracol);
        } catch (TableNotDisabledException re) {
            expectedException = true;
        }
        // Add column should work even if the table is enabled
        Assert.assertFalse(expectedException);
        modifiedHtd = this.admin.getTableDescriptor(tableName);
        hcd = modifiedHtd.getFamily(xtracol.getName());
        Assert.assertTrue((hcd != null));
        Assert.assertTrue(hcd.getValue(xtracolName).equals(xtracolName));
        // Delete the just-added column.
        this.admin.deleteColumnFamily(tableName, xtracol.getName());
        modifiedHtd = this.admin.getTableDescriptor(tableName);
        hcd = modifiedHtd.getFamily(xtracol.getName());
        Assert.assertTrue((hcd == null));
        // Delete the table
        this.admin.disableTable(tableName);
        this.admin.deleteTable(tableName);
        this.admin.listTables();
        Assert.assertFalse(this.admin.tableExists(tableName));
    }

    @Test
    public void testCreateTableNumberOfRegions() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc);
        List<HRegionLocation> regions;
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals("Table should have only 1 region", 1, regions.size());
        }
        TableName TABLE_2 = TableName.valueOf(((tableName.getNameAsString()) + "_2"));
        desc = new HTableDescriptor(TABLE_2);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, new byte[][]{ new byte[]{ 42 } });
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(TABLE_2)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals("Table should have only 2 region", 2, regions.size());
        }
        TableName TABLE_3 = TableName.valueOf(((tableName.getNameAsString()) + "_3"));
        desc = new HTableDescriptor(TABLE_3);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, Bytes.toBytes("a"), Bytes.toBytes("z"), 3);
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(TABLE_3)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals("Table should have only 3 region", 3, regions.size());
        }
        TableName TABLE_4 = TableName.valueOf(((tableName.getNameAsString()) + "_4"));
        desc = new HTableDescriptor(TABLE_4);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        try {
            admin.createTable(desc, Bytes.toBytes("a"), Bytes.toBytes("z"), 2);
            Assert.fail("Should not be able to create a table with only 2 regions using this API.");
        } catch (IllegalArgumentException eae) {
            // Expected
        }
        TableName TABLE_5 = TableName.valueOf(((tableName.getNameAsString()) + "_5"));
        desc = new HTableDescriptor(TABLE_5);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, new byte[]{ 1 }, new byte[]{ 127 }, 16);
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(TABLE_5)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals("Table should have 16 region", 16, regions.size());
        }
    }

    @Test
    public void testCreateTableWithRegions() throws IOException, InterruptedException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 }, new byte[]{ 3, 3, 3 }, new byte[]{ 4, 4, 4 }, new byte[]{ 5, 5, 5 }, new byte[]{ 6, 6, 6 }, new byte[]{ 7, 7, 7 }, new byte[]{ 8, 8, 8 }, new byte[]{ 9, 9, 9 } };
        int expectedRegions = (splitKeys.length) + 1;
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, splitKeys);
        boolean tableAvailable = admin.isTableAvailable(tableName, splitKeys);
        Assert.assertTrue("Table should be created with splitKyes + 1 rows in META", tableAvailable);
        List<HRegionLocation> regions;
        Iterator<HRegionLocation> hris;
        RegionInfo hri;
        ClusterConnection conn = ((ClusterConnection) (TestAdmin1.TEST_UTIL.getConnection()));
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
            System.err.println((("Found " + (regions.size())) + " regions"));
            hris = regions.iterator();
            hri = hris.next().getRegionInfo();
            Assert.assertTrue((((hri.getStartKey()) == null) || ((hri.getStartKey().length) == 0)));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[0]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[0]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[1]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[1]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[2]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[2]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[3]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[3]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[4]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[4]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[5]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[5]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[6]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[6]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[7]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[7]));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), splitKeys[8]));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), splitKeys[8]));
            Assert.assertTrue((((hri.getEndKey()) == null) || ((hri.getEndKey().length) == 0)));
            verifyRoundRobinDistribution(conn, l, expectedRegions);
        }
        // Now test using start/end with a number of regions
        // Use 80 bit numbers to make sure we aren't limited
        byte[] startKey = new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
        byte[] endKey = new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 };
        // Splitting into 10 regions, we expect (null,1) ... (9, null)
        // with (1,2) (2,3) (3,4) (4,5) (5,6) (6,7) (7,8) (8,9) in the middle
        expectedRegions = 10;
        TableName TABLE_2 = TableName.valueOf(((tableName.getNameAsString()) + "_2"));
        desc = new HTableDescriptor(TABLE_2);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin = TestAdmin1.TEST_UTIL.getAdmin();
        admin.createTable(desc, startKey, endKey, expectedRegions);
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(TABLE_2)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
            System.err.println((("Found " + (regions.size())) + " regions"));
            hris = regions.iterator();
            hri = hris.next().getRegionInfo();
            Assert.assertTrue((((hri.getStartKey()) == null) || ((hri.getStartKey().length) == 0)));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 4, 4, 4, 4, 4, 4, 4, 4, 4, 4 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 5, 5, 5, 5, 5, 5, 5, 5, 5, 5 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 6, 6, 6, 6, 6, 6, 6, 6, 6, 6 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 7, 7, 7, 7, 7, 7, 7, 7, 7, 7 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 }));
            Assert.assertTrue(Bytes.equals(hri.getEndKey(), new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 }));
            hri = hris.next().getRegionInfo();
            Assert.assertTrue(Bytes.equals(hri.getStartKey(), new byte[]{ 9, 9, 9, 9, 9, 9, 9, 9, 9, 9 }));
            Assert.assertTrue((((hri.getEndKey()) == null) || ((hri.getEndKey().length) == 0)));
            verifyRoundRobinDistribution(conn, l, expectedRegions);
        }
        // Try once more with something that divides into something infinite
        startKey = new byte[]{ 0, 0, 0, 0, 0, 0 };
        endKey = new byte[]{ 1, 0, 0, 0, 0, 0 };
        expectedRegions = 5;
        TableName TABLE_3 = TableName.valueOf(((tableName.getNameAsString()) + "_3"));
        desc = new HTableDescriptor(TABLE_3);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin = TestAdmin1.TEST_UTIL.getAdmin();
        admin.createTable(desc, startKey, endKey, expectedRegions);
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(TABLE_3)) {
            regions = l.getAllRegionLocations();
            Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
            System.err.println((("Found " + (regions.size())) + " regions"));
            verifyRoundRobinDistribution(conn, l, expectedRegions);
        }
        // Try an invalid case where there are duplicate split keys
        splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 }, new byte[]{ 3, 3, 3 }, new byte[]{ 2, 2, 2 } };
        TableName TABLE_4 = TableName.valueOf(((tableName.getNameAsString()) + "_4"));
        desc = new HTableDescriptor(TABLE_4);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        try {
            admin.createTable(desc, splitKeys);
            Assert.assertTrue(("Should not be able to create this table because of " + "duplicate split keys"), false);
        } catch (IllegalArgumentException iae) {
            // Expected
        }
    }

    @Test
    public void testTableAvailableWithRandomSplitKeys() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor("col"));
        byte[][] splitKeys = new byte[1][];
        splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 } };
        admin.createTable(desc);
        boolean tableAvailable = admin.isTableAvailable(tableName, splitKeys);
        Assert.assertFalse("Table should be created with 1 row in META", tableAvailable);
    }

    @Test
    public void testCreateTableWithOnlyEmptyStartRow() throws IOException {
        final byte[] tableName = Bytes.toBytes(name.getMethodName());
        byte[][] splitKeys = new byte[1][];
        splitKeys[0] = HConstants.EMPTY_BYTE_ARRAY;
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        desc.addFamily(new HColumnDescriptor("col"));
        try {
            admin.createTable(desc, splitKeys);
            Assert.fail("Test case should fail as empty split key is passed.");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    public void testCreateTableWithEmptyRowInTheSplitKeys() throws IOException {
        final byte[] tableName = Bytes.toBytes(name.getMethodName());
        byte[][] splitKeys = new byte[3][];
        splitKeys[0] = Bytes.toBytes("region1");
        splitKeys[1] = HConstants.EMPTY_BYTE_ARRAY;
        splitKeys[2] = Bytes.toBytes("region2");
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        desc.addFamily(new HColumnDescriptor("col"));
        try {
            admin.createTable(desc, splitKeys);
            Assert.fail("Test case should fail as empty split key is passed.");
        } catch (IllegalArgumentException e) {
            TestAdmin1.LOG.info("Expected ", e);
        }
    }

    @Test
    public void testTableExist() throws IOException {
        final TableName table = TableName.valueOf(name.getMethodName());
        boolean exist;
        exist = this.admin.tableExists(table);
        Assert.assertEquals(false, exist);
        TestAdmin1.TEST_UTIL.createTable(table, CATALOG_FAMILY);
        exist = this.admin.tableExists(table);
        Assert.assertEquals(true, exist);
    }

    /**
     * Tests forcing split from client and having scanners successfully ride over split.
     *
     * @throws Exception
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testForceSplit() throws Exception {
        byte[][] familyNames = new byte[][]{ Bytes.toBytes("cf") };
        int[] rowCounts = new int[]{ 6000 };
        int numVersions = HColumnDescriptor.DEFAULT_VERSIONS;
        int blockSize = 256;
        splitTest(null, familyNames, rowCounts, numVersions, blockSize, true);
        byte[] splitKey = Bytes.toBytes(3500);
        splitTest(splitKey, familyNames, rowCounts, numVersions, blockSize, true);
        // test regionSplitSync
        splitTest(splitKey, familyNames, rowCounts, numVersions, blockSize, false);
    }

    /**
     * Test retain assignment on enableTable.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testEnableTableRetainAssignment() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        byte[][] splitKeys = new byte[][]{ new byte[]{ 1, 1, 1 }, new byte[]{ 2, 2, 2 }, new byte[]{ 3, 3, 3 }, new byte[]{ 4, 4, 4 }, new byte[]{ 5, 5, 5 }, new byte[]{ 6, 6, 6 }, new byte[]{ 7, 7, 7 }, new byte[]{ 8, 8, 8 }, new byte[]{ 9, 9, 9 } };
        int expectedRegions = (splitKeys.length) + 1;
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, splitKeys);
        try (RegionLocator l = TestAdmin1.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            List<HRegionLocation> regions = l.getAllRegionLocations();
            Assert.assertEquals((((("Tried to create " + expectedRegions) + " regions ") + "but only found ") + (regions.size())), expectedRegions, regions.size());
            // Disable table.
            admin.disableTable(tableName);
            // Enable table, use retain assignment to assign regions.
            admin.enableTable(tableName);
            List<HRegionLocation> regions2 = l.getAllRegionLocations();
            // Check the assignment.
            Assert.assertEquals(regions.size(), regions2.size());
            Assert.assertTrue(regions2.containsAll(regions));
        }
    }

    /**
     * Multi-family scenario. Tests forcing split from client and
     * having scanners successfully ride over split.
     *
     * @throws Exception
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testForceSplitMultiFamily() throws Exception {
        int numVersions = HColumnDescriptor.DEFAULT_VERSIONS;
        // use small HFile block size so that we can have lots of blocks in HFile
        // Otherwise, if there is only one block,
        // HFileBlockIndex.midKey()'s value == startKey
        int blockSize = 256;
        byte[][] familyNames = new byte[][]{ Bytes.toBytes("cf1"), Bytes.toBytes("cf2") };
        // one of the column families isn't splittable
        int[] rowCounts = new int[]{ 6000, 1 };
        splitTest(null, familyNames, rowCounts, numVersions, blockSize, true);
        rowCounts = new int[]{ 1, 6000 };
        splitTest(null, familyNames, rowCounts, numVersions, blockSize, true);
        // one column family has much smaller data than the other
        // the split key should be based on the largest column family
        rowCounts = new int[]{ 6000, 300 };
        splitTest(null, familyNames, rowCounts, numVersions, blockSize, true);
        rowCounts = new int[]{ 300, 6000 };
        splitTest(null, familyNames, rowCounts, numVersions, blockSize, true);
    }

    @Test
    public void testSplitAndMergeWithReplicaTable() throws Exception {
        // The test tries to directly split replica regions and directly merge replica regions. These
        // are not allowed. The test validates that. Then the test does a valid split/merge of allowed
        // regions.
        // Set up a table with 3 regions and replication set to 3
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.setRegionReplication(3);
        byte[] cf = Bytes.toBytes("f");
        HColumnDescriptor hcd = new HColumnDescriptor(cf);
        desc.addFamily(hcd);
        byte[][] splitRows = new byte[2][];
        splitRows[0] = new byte[]{ ((byte) ('4')) };
        splitRows[1] = new byte[]{ ((byte) ('7')) };
        TestAdmin1.TEST_UTIL.getAdmin().createTable(desc, splitRows);
        List<HRegion> oldRegions;
        do {
            oldRegions = TestAdmin1.TEST_UTIL.getHBaseCluster().getRegions(tableName);
            Thread.sleep(10);
        } while ((oldRegions.size()) != 9 );// 3 regions * 3 replicas

        // write some data to the table
        Table ht = TestAdmin1.TEST_UTIL.getConnection().getTable(tableName);
        List<Put> puts = new ArrayList<>();
        byte[] qualifier = Bytes.toBytes("c");
        Put put = new Put(new byte[]{ ((byte) ('1')) });
        put.addColumn(cf, qualifier, Bytes.toBytes("100"));
        puts.add(put);
        put = new Put(new byte[]{ ((byte) ('6')) });
        put.addColumn(cf, qualifier, Bytes.toBytes("100"));
        puts.add(put);
        put = new Put(new byte[]{ ((byte) ('8')) });
        put.addColumn(cf, qualifier, Bytes.toBytes("100"));
        puts.add(put);
        ht.put(puts);
        ht.close();
        List<Pair<RegionInfo, ServerName>> regions = MetaTableAccessor.getTableRegionsAndLocations(TestAdmin1.TEST_UTIL.getConnection(), tableName);
        boolean gotException = false;
        // the element at index 1 would be a replica (since the metareader gives us ordered
        // regions). Try splitting that region via the split API . Should fail
        try {
            TestAdmin1.TEST_UTIL.getAdmin().splitRegion(regions.get(1).getFirst().getRegionName());
        } catch (IllegalArgumentException ex) {
            gotException = true;
        }
        Assert.assertTrue(gotException);
        gotException = false;
        // the element at index 1 would be a replica (since the metareader gives us ordered
        // regions). Try splitting that region via a different split API (the difference is
        // this API goes direct to the regionserver skipping any checks in the admin). Should fail
        try {
            TestAdmin1.TEST_UTIL.getHBaseAdmin().splitRegionAsync(regions.get(1).getFirst(), new byte[]{ ((byte) ('1')) });
        } catch (IOException ex) {
            gotException = true;
        }
        Assert.assertTrue(gotException);
        gotException = false;
        // testing Sync split operation
        try {
            TestAdmin1.TEST_UTIL.getHBaseAdmin().splitRegionSync(regions.get(1).getFirst().getRegionName(), new byte[]{ ((byte) ('1')) });
        } catch (IllegalArgumentException ex) {
            gotException = true;
        }
        Assert.assertTrue(gotException);
        gotException = false;
        // Try merging a replica with another. Should fail.
        try {
            TestAdmin1.TEST_UTIL.getHBaseAdmin().mergeRegionsSync(regions.get(1).getFirst().getEncodedNameAsBytes(), regions.get(2).getFirst().getEncodedNameAsBytes(), true);
        } catch (IllegalArgumentException m) {
            gotException = true;
        }
        Assert.assertTrue(gotException);
        // Try going to the master directly (that will skip the check in admin)
        try {
            byte[][] nameofRegionsToMerge = new byte[2][];
            nameofRegionsToMerge[0] = regions.get(1).getFirst().getEncodedNameAsBytes();
            nameofRegionsToMerge[1] = regions.get(2).getFirst().getEncodedNameAsBytes();
            MergeTableRegionsRequest request = RequestConverter.buildMergeTableRegionsRequest(nameofRegionsToMerge, true, NO_NONCE, NO_NONCE);
            getMaster().mergeTableRegions(null, request);
        } catch (org.apache.hbase m) {
            Throwable t = m.getCause();
            do {
                if (t instanceof MergeRegionException) {
                    gotException = true;
                    break;
                }
                t = t.getCause();
            } while (t != null );
        }
        Assert.assertTrue(gotException);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHColumnDescriptor() throws IOException {
        new HColumnDescriptor("/cfamily/name");
    }

    @Test
    public void testEnableDisableAddColumnDeleteColumn() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin1.TEST_UTIL.createTable(tableName, CATALOG_FAMILY).close();
        while (!(this.admin.isTableEnabled(TableName.valueOf(name.getMethodName())))) {
            Thread.sleep(10);
        } 
        this.admin.disableTable(tableName);
        try {
            TestAdmin1.TEST_UTIL.getConnection().getTable(tableName);
        } catch (org.apache.hadoop.hbase.DoNotRetryIOException e) {
            // expected
        }
        this.admin.addColumnFamily(tableName, new HColumnDescriptor("col2"));
        this.admin.enableTable(tableName);
        try {
            this.admin.deleteColumnFamily(tableName, Bytes.toBytes("col2"));
        } catch (TableNotDisabledException e) {
            TestAdmin1.LOG.info(e.toString(), e);
        }
        this.admin.disableTable(tableName);
        this.admin.deleteTable(tableName);
    }

    @Test
    public void testDeleteLastColumnFamily() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        TestAdmin1.TEST_UTIL.createTable(tableName, CATALOG_FAMILY).close();
        while (!(this.admin.isTableEnabled(TableName.valueOf(name.getMethodName())))) {
            Thread.sleep(10);
        } 
        // test for enabled table
        try {
            this.admin.deleteColumnFamily(tableName, CATALOG_FAMILY);
            Assert.fail("Should have failed to delete the only column family of a table");
        } catch (InvalidFamilyOperationException ex) {
            // expected
        }
        // test for disabled table
        this.admin.disableTable(tableName);
        try {
            this.admin.deleteColumnFamily(tableName, CATALOG_FAMILY);
            Assert.fail("Should have failed to delete the only column family of a table");
        } catch (InvalidFamilyOperationException ex) {
            // expected
        }
        this.admin.deleteTable(tableName);
    }

    /* Test DFS replication for column families, where one CF has default replication(3) and the other
    is set to 1.
     */
    @Test
    public void testHFileReplication() throws Exception {
        final TableName tableName = TableName.valueOf(this.name.getMethodName());
        String fn1 = "rep1";
        HColumnDescriptor hcd1 = new HColumnDescriptor(fn1);
        hcd1.setDFSReplication(((short) (1)));
        String fn = "defaultRep";
        HColumnDescriptor hcd = new HColumnDescriptor(fn);
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(hcd);
        htd.addFamily(hcd1);
        Table table = TestAdmin1.TEST_UTIL.createTable(htd, null);
        TestAdmin1.TEST_UTIL.waitTableAvailable(tableName);
        Put p = new Put(Bytes.toBytes("defaultRep_rk"));
        byte[] q1 = Bytes.toBytes("q1");
        byte[] v1 = Bytes.toBytes("v1");
        p.addColumn(Bytes.toBytes(fn), q1, v1);
        List<Put> puts = new ArrayList<>(2);
        puts.add(p);
        p = new Put(Bytes.toBytes("rep1_rk"));
        p.addColumn(Bytes.toBytes(fn1), q1, v1);
        puts.add(p);
        try {
            table.put(puts);
            admin.flush(tableName);
            List<HRegion> regions = TestAdmin1.TEST_UTIL.getMiniHBaseCluster().getRegions(tableName);
            for (HRegion r : regions) {
                HStore store = r.getStore(Bytes.toBytes(fn));
                for (HStoreFile sf : store.getStorefiles()) {
                    Assert.assertTrue(sf.toString().contains(fn));
                    Assert.assertTrue((("Column family " + fn) + " should have 3 copies"), ((FSUtils.getDefaultReplication(TestAdmin1.TEST_UTIL.getTestFileSystem(), sf.getPath())) == (sf.getFileInfo().getFileStatus().getReplication())));
                }
                store = r.getStore(Bytes.toBytes(fn1));
                for (HStoreFile sf : store.getStorefiles()) {
                    Assert.assertTrue(sf.toString().contains(fn1));
                    Assert.assertTrue((("Column family " + fn1) + " should have only 1 copy"), (1 == (sf.getFileInfo().getFileStatus().getReplication())));
                }
            }
        } finally {
            if (admin.isTableEnabled(tableName)) {
                this.admin.disableTable(tableName);
                this.admin.deleteTable(tableName);
            }
        }
    }

    @Test
    public void testMergeRegions() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HColumnDescriptor cd = new HColumnDescriptor("d");
        HTableDescriptor td = new HTableDescriptor(tableName);
        td.addFamily(cd);
        byte[][] splitRows = new byte[2][];
        splitRows[0] = new byte[]{ ((byte) ('3')) };
        splitRows[1] = new byte[]{ ((byte) ('6')) };
        try {
            TestAdmin1.TEST_UTIL.createTable(td, splitRows);
            TestAdmin1.TEST_UTIL.waitTableAvailable(tableName);
            List<RegionInfo> tableRegions;
            RegionInfo regionA;
            RegionInfo regionB;
            // merge with full name
            tableRegions = admin.getRegions(tableName);
            Assert.assertEquals(3, admin.getTableRegions(tableName).size());
            regionA = tableRegions.get(0);
            regionB = tableRegions.get(1);
            // TODO convert this to version that is synchronous (See HBASE-16668)
            admin.mergeRegionsAsync(regionA.getRegionName(), regionB.getRegionName(), false).get(60, TimeUnit.SECONDS);
            Assert.assertEquals(2, admin.getTableRegions(tableName).size());
            // merge with encoded name
            tableRegions = admin.getRegions(tableName);
            regionA = tableRegions.get(0);
            regionB = tableRegions.get(1);
            // TODO convert this to version that is synchronous (See HBASE-16668)
            admin.mergeRegionsAsync(regionA.getEncodedNameAsBytes(), regionB.getEncodedNameAsBytes(), false).get(60, TimeUnit.SECONDS);
            Assert.assertEquals(1, admin.getTableRegions(tableName).size());
        } finally {
            this.admin.disableTable(tableName);
            this.admin.deleteTable(tableName);
        }
    }

    @Test
    public void testSplitShouldNotHappenIfSplitIsDisabledForTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = new HTableDescriptor(tableName);
        htd.addFamily(new HColumnDescriptor("f"));
        htd.setRegionSplitPolicyClassName(DisabledRegionSplitPolicy.class.getName());
        Table table = TestAdmin1.TEST_UTIL.createTable(htd, null);
        for (int i = 0; i < 10; i++) {
            Put p = new Put(Bytes.toBytes(("row" + i)));
            byte[] q1 = Bytes.toBytes("q1");
            byte[] v1 = Bytes.toBytes("v1");
            p.addColumn(Bytes.toBytes("f"), q1, v1);
            table.put(p);
        }
        this.admin.flush(tableName);
        try {
            this.admin.split(tableName, Bytes.toBytes("row5"));
            Threads.sleep(10000);
        } catch (Exception e) {
            // Nothing to do.
        }
        // Split should not happen.
        List<RegionInfo> allRegions = MetaTableAccessor.getTableRegions(this.admin.getConnection(), tableName, true);
        Assert.assertEquals(1, allRegions.size());
    }

    @Test
    public void testCloneTableSchema() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        testCloneTableSchema(tableName, newTableName, false);
    }

    @Test
    public void testCloneTableSchemaPreservingSplits() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        testCloneTableSchema(tableName, newTableName, true);
    }

    @Test
    public void testCloneTableSchemaWithNonExistentSourceTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        // test for non-existent source table
        try {
            admin.cloneTableSchema(tableName, newTableName, false);
            Assert.fail("Should have failed to create a new table by cloning non-existent source table.");
        } catch (TableNotFoundException ex) {
            // expected
        }
    }

    @Test
    public void testCloneTableSchemaWithExistentDestinationTable() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final TableName newTableName = TableName.valueOf(((tableName.getNameAsString()) + "_new"));
        byte[] FAMILY_0 = Bytes.toBytes("cf0");
        TestAdmin1.TEST_UTIL.createTable(tableName, FAMILY_0);
        TestAdmin1.TEST_UTIL.createTable(newTableName, FAMILY_0);
        // test for existent destination table
        try {
            admin.cloneTableSchema(tableName, newTableName, false);
            Assert.fail("Should have failed to create a existent table.");
        } catch (TableExistsException ex) {
            // expected
        }
    }

    @Test
    public void testModifyTableOnTableWithRegionReplicas() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(Bytes.toBytes("cf"))).setRegionReplication(5).build();
        admin.createTable(desc);
        int maxFileSize = 10000000;
        TableDescriptor newDesc = TableDescriptorBuilder.newBuilder(desc).setMaxFileSize(maxFileSize).build();
        admin.modifyTable(newDesc);
        TableDescriptor newTableDesc = admin.getDescriptor(tableName);
        Assert.assertEquals(maxFileSize, newTableDesc.getMaxFileSize());
    }
}

