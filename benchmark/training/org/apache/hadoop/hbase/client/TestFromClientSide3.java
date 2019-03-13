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


import AdminProtos.AdminService.BlockingInterface;
import Durability.ASYNC_WAL;
import HConstants.HBASE_CLIENT_RETRIES_NUMBER;
import MultiRowMutationProtos.MultiRowMutationService;
import MultiRowMutationProtos.MutateRowsRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HRegionLocation;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.coprocessor.MultiRowMutationEndpoint;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.exceptions.UnknownProtocolException;
import org.apache.hadoop.hbase.ipc.CoprocessorRpcUtils;
import org.apache.hadoop.hbase.ipc.ServerRpcController;
import org.apache.hadoop.hbase.protobuf.generated.ClientProtos.MutationProto.MutationType.PUT;
import org.apache.hadoop.hbase.protobuf.generated.MultiRowMutationProtos;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.MiniBatchOperationInProgress;
import org.apache.hadoop.hbase.regionserver.RegionScanner;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.AdminProtos;
import org.apache.hadoop.hbase.shaded.protobuf.org.apache.hadoop.hbase.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ LargeTests.class, ClientTests.class })
public class TestFromClientSide3 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFromClientSide3.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFromClientSide3.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static Random random = new Random();

    private static int SLAVES = 3;

    private static final byte[] ROW = Bytes.toBytes("testRow");

    private static final byte[] ANOTHERROW = Bytes.toBytes("anotherrow");

    private static final byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    private static final byte[] VALUE = Bytes.toBytes("testValue");

    private static final byte[] COL_QUAL = Bytes.toBytes("f1");

    private static final byte[] VAL_BYTES = Bytes.toBytes("v1");

    private static final byte[] ROW_BYTES = Bytes.toBytes("r1");

    @Rule
    public TestName name = new TestName();

    @Test
    public void testScanAfterDeletingSpecifiedRow() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestFromClientSide3.FAMILY)).build();
        TestFromClientSide3.TEST_UTIL.getAdmin().createTable(desc);
        byte[] row = Bytes.toBytes("SpecifiedRow");
        byte[] value0 = Bytes.toBytes("value_0");
        byte[] value1 = Bytes.toBytes("value_1");
        try (Table t = TestFromClientSide3.TEST_UTIL.getConnection().getTable(tableName)) {
            Put put = new Put(row);
            put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
            t.put(put);
            Delete d = new Delete(row);
            t.delete(d);
            put = new Put(row);
            put.addColumn(TestFromClientSide3.FAMILY, null, value0);
            t.put(put);
            put = new Put(row);
            put.addColumn(TestFromClientSide3.FAMILY, null, value1);
            t.put(put);
            List<Cell> cells = TestFromClientSide3.toList(t.getScanner(new Scan()));
            Assert.assertEquals(1, cells.size());
            Assert.assertEquals("value_1", Bytes.toString(CellUtil.cloneValue(cells.get(0))));
            cells = TestFromClientSide3.toList(t.getScanner(new Scan().addFamily(TestFromClientSide3.FAMILY)));
            Assert.assertEquals(1, cells.size());
            Assert.assertEquals("value_1", Bytes.toString(CellUtil.cloneValue(cells.get(0))));
            cells = TestFromClientSide3.toList(t.getScanner(new Scan().addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER)));
            Assert.assertEquals(0, cells.size());
            TestFromClientSide3.TEST_UTIL.getAdmin().flush(tableName);
            cells = TestFromClientSide3.toList(t.getScanner(new Scan()));
            Assert.assertEquals(1, cells.size());
            Assert.assertEquals("value_1", Bytes.toString(CellUtil.cloneValue(cells.get(0))));
            cells = TestFromClientSide3.toList(t.getScanner(new Scan().addFamily(TestFromClientSide3.FAMILY)));
            Assert.assertEquals(1, cells.size());
            Assert.assertEquals("value_1", Bytes.toString(CellUtil.cloneValue(cells.get(0))));
            cells = TestFromClientSide3.toList(t.getScanner(new Scan().addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER)));
            Assert.assertEquals(0, cells.size());
        }
    }

    @Test
    public void testScanAfterDeletingSpecifiedRowV2() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestFromClientSide3.FAMILY)).build();
        TestFromClientSide3.TEST_UTIL.getAdmin().createTable(desc);
        byte[] row = Bytes.toBytes("SpecifiedRow");
        byte[] qual0 = Bytes.toBytes("qual0");
        byte[] qual1 = Bytes.toBytes("qual1");
        try (Table t = TestFromClientSide3.TEST_UTIL.getConnection().getTable(tableName)) {
            Delete d = new Delete(row);
            t.delete(d);
            Put put = new Put(row);
            put.addColumn(TestFromClientSide3.FAMILY, null, TestFromClientSide3.VALUE);
            t.put(put);
            put = new Put(row);
            put.addColumn(TestFromClientSide3.FAMILY, qual1, qual1);
            t.put(put);
            put = new Put(row);
            put.addColumn(TestFromClientSide3.FAMILY, qual0, qual0);
            t.put(put);
            Result r = t.get(new Get(row));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals("testValue", Bytes.toString(CellUtil.cloneValue(r.rawCells()[0])));
            Assert.assertEquals("qual0", Bytes.toString(CellUtil.cloneValue(r.rawCells()[1])));
            Assert.assertEquals("qual1", Bytes.toString(CellUtil.cloneValue(r.rawCells()[2])));
            TestFromClientSide3.TEST_UTIL.getAdmin().flush(tableName);
            r = t.get(new Get(row));
            Assert.assertEquals(3, r.size());
            Assert.assertEquals("testValue", Bytes.toString(CellUtil.cloneValue(r.rawCells()[0])));
            Assert.assertEquals("qual0", Bytes.toString(CellUtil.cloneValue(r.rawCells()[1])));
            Assert.assertEquals("qual1", Bytes.toString(CellUtil.cloneValue(r.rawCells()[2])));
        }
    }

    // override the config settings at the CF level and ensure priority
    @Test
    public void testAdvancedConfigOverride() throws Exception {
        /* Overall idea: (1) create 3 store files and issue a compaction. config's
        compaction.min == 3, so should work. (2) Increase the compaction.min
        toggle in the HTD to 5 and modify table. If we use the HTD value instead
        of the default config value, adding 3 files and issuing a compaction
        SHOULD NOT work (3) Decrease the compaction.min toggle in the HCD to 2
        and modify table. The CF schema should override the Table schema and now
        cause a minor compaction.
         */
        TestFromClientSide3.TEST_UTIL.getConfiguration().setInt("hbase.hstore.compaction.min", 3);
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table hTable = TestFromClientSide3.TEST_UTIL.createTable(tableName, TestFromClientSide3.FAMILY, 10);
        Admin admin = TestFromClientSide3.TEST_UTIL.getAdmin();
        ClusterConnection connection = ((ClusterConnection) (TestFromClientSide3.TEST_UTIL.getConnection()));
        // Create 3 store files.
        byte[] row = Bytes.toBytes(TestFromClientSide3.random.nextInt());
        performMultiplePutAndFlush(((HBaseAdmin) (admin)), hTable, row, TestFromClientSide3.FAMILY, 3, 100);
        try (RegionLocator locator = TestFromClientSide3.TEST_UTIL.getConnection().getRegionLocator(tableName)) {
            // Verify we have multiple store files.
            HRegionLocation loc = locator.getRegionLocation(row, true);
            byte[] regionName = loc.getRegionInfo().getRegionName();
            AdminProtos.AdminService.BlockingInterface server = connection.getAdmin(loc.getServerName());
            Assert.assertTrue(((ProtobufUtil.getStoreFiles(server, regionName, TestFromClientSide3.FAMILY).size()) > 1));
            // Issue a compaction request
            admin.compact(tableName);
            // poll wait for the compactions to happen
            for (int i = 0; i < ((10 * 1000) / 40); ++i) {
                // The number of store files after compaction should be lesser.
                loc = locator.getRegionLocation(row, true);
                if (!(loc.getRegionInfo().isOffline())) {
                    regionName = loc.getRegionInfo().getRegionName();
                    server = connection.getAdmin(loc.getServerName());
                    if ((ProtobufUtil.getStoreFiles(server, regionName, TestFromClientSide3.FAMILY).size()) <= 1) {
                        break;
                    }
                }
                Thread.sleep(40);
            }
            // verify the compactions took place and that we didn't just time out
            Assert.assertTrue(((ProtobufUtil.getStoreFiles(server, regionName, TestFromClientSide3.FAMILY).size()) <= 1));
            // change the compaction.min config option for this table to 5
            TestFromClientSide3.LOG.info("hbase.hstore.compaction.min should now be 5");
            HTableDescriptor htd = new HTableDescriptor(hTable.getTableDescriptor());
            htd.setValue("hbase.hstore.compaction.min", String.valueOf(5));
            admin.modifyTable(tableName, htd);
            Pair<Integer, Integer> st;
            while ((null != (st = admin.getAlterStatus(tableName))) && ((st.getFirst()) > 0)) {
                TestFromClientSide3.LOG.debug(((st.getFirst()) + " regions left to update"));
                Thread.sleep(40);
            } 
            TestFromClientSide3.LOG.info("alter status finished");
            // Create 3 more store files.
            performMultiplePutAndFlush(((HBaseAdmin) (admin)), hTable, row, TestFromClientSide3.FAMILY, 3, 10);
            // Issue a compaction request
            admin.compact(tableName);
            // This time, the compaction request should not happen
            Thread.sleep((10 * 1000));
            loc = locator.getRegionLocation(row, true);
            regionName = loc.getRegionInfo().getRegionName();
            server = connection.getAdmin(loc.getServerName());
            int sfCount = ProtobufUtil.getStoreFiles(server, regionName, TestFromClientSide3.FAMILY).size();
            Assert.assertTrue((sfCount > 1));
            // change an individual CF's config option to 2 & online schema update
            TestFromClientSide3.LOG.info("hbase.hstore.compaction.min should now be 2");
            HColumnDescriptor hcd = new HColumnDescriptor(htd.getFamily(TestFromClientSide3.FAMILY));
            hcd.setValue("hbase.hstore.compaction.min", String.valueOf(2));
            htd.modifyFamily(hcd);
            admin.modifyTable(tableName, htd);
            while ((null != (st = admin.getAlterStatus(tableName))) && ((st.getFirst()) > 0)) {
                TestFromClientSide3.LOG.debug(((st.getFirst()) + " regions left to update"));
                Thread.sleep(40);
            } 
            TestFromClientSide3.LOG.info("alter status finished");
            // Issue a compaction request
            admin.compact(tableName);
            // poll wait for the compactions to happen
            for (int i = 0; i < ((10 * 1000) / 40); ++i) {
                loc = locator.getRegionLocation(row, true);
                regionName = loc.getRegionInfo().getRegionName();
                try {
                    server = connection.getAdmin(loc.getServerName());
                    if ((ProtobufUtil.getStoreFiles(server, regionName, TestFromClientSide3.FAMILY).size()) < sfCount) {
                        break;
                    }
                } catch (Exception e) {
                    TestFromClientSide3.LOG.debug(("Waiting for region to come online: " + (Bytes.toString(regionName))));
                }
                Thread.sleep(40);
            }
            // verify the compaction took place and that we didn't just time out
            Assert.assertTrue(((ProtobufUtil.getStoreFiles(server, regionName, TestFromClientSide3.FAMILY).size()) < sfCount));
            // Finally, ensure that we can remove a custom config value after we made it
            TestFromClientSide3.LOG.info("Removing CF config value");
            TestFromClientSide3.LOG.info("hbase.hstore.compaction.min should now be 5");
            hcd = new HColumnDescriptor(htd.getFamily(TestFromClientSide3.FAMILY));
            hcd.setValue("hbase.hstore.compaction.min", null);
            htd.modifyFamily(hcd);
            admin.modifyTable(tableName, htd);
            while ((null != (st = admin.getAlterStatus(tableName))) && ((st.getFirst()) > 0)) {
                TestFromClientSide3.LOG.debug(((st.getFirst()) + " regions left to update"));
                Thread.sleep(40);
            } 
            TestFromClientSide3.LOG.info("alter status finished");
            Assert.assertNull(hTable.getTableDescriptor().getFamily(TestFromClientSide3.FAMILY).getValue("hbase.hstore.compaction.min"));
        }
    }

    @Test
    public void testHTableBatchWithEmptyPut() throws Exception {
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY });
        try {
            List actions = ((List) (new ArrayList()));
            Object[] results = new Object[2];
            // create an empty Put
            Put put1 = new Put(TestFromClientSide3.ROW);
            actions.add(put1);
            Put put2 = new Put(TestFromClientSide3.ANOTHERROW);
            put2.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
            actions.add(put2);
            table.batch(actions, results);
            Assert.fail("Empty Put should have failed the batch call");
        } catch (IllegalArgumentException iae) {
        } finally {
            table.close();
        }
    }

    // Test Table.batch with large amount of mutations against the same key.
    // It used to trigger read lock's "Maximum lock count exceeded" Error.
    @Test
    public void testHTableWithLargeBatch() throws Exception {
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY });
        int sixtyFourK = 64 * 1024;
        try {
            List actions = new ArrayList();
            Object[] results = new Object[(sixtyFourK + 1) * 2];
            for (int i = 0; i < (sixtyFourK + 1); i++) {
                Put put1 = new Put(TestFromClientSide3.ROW);
                put1.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
                actions.add(put1);
                Put put2 = new Put(TestFromClientSide3.ANOTHERROW);
                put2.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
                actions.add(put2);
            }
            table.batch(actions, results);
        } finally {
            table.close();
        }
    }

    @Test
    public void testBatchWithRowMutation() throws Exception {
        TestFromClientSide3.LOG.info("Starting testBatchWithRowMutation");
        final TableName TABLENAME = TableName.valueOf("testBatchWithRowMutation");
        try (Table t = TestFromClientSide3.TEST_UTIL.createTable(TABLENAME, TestFromClientSide3.FAMILY)) {
            byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b") };
            RowMutations arm = RowMutations.of(Collections.singletonList(new Put(TestFromClientSide3.ROW).addColumn(TestFromClientSide3.FAMILY, QUALIFIERS[0], TestFromClientSide3.VALUE)));
            Object[] batchResult = new Object[1];
            t.batch(Arrays.asList(arm), batchResult);
            Get g = new Get(TestFromClientSide3.ROW);
            Result r = t.get(g);
            Assert.assertEquals(0, Bytes.compareTo(TestFromClientSide3.VALUE, r.getValue(TestFromClientSide3.FAMILY, QUALIFIERS[0])));
            arm = RowMutations.of(Arrays.asList(new Put(TestFromClientSide3.ROW).addColumn(TestFromClientSide3.FAMILY, QUALIFIERS[1], TestFromClientSide3.VALUE), new Delete(TestFromClientSide3.ROW).addColumns(TestFromClientSide3.FAMILY, QUALIFIERS[0])));
            t.batch(Arrays.asList(arm), batchResult);
            r = t.get(g);
            Assert.assertEquals(0, Bytes.compareTo(TestFromClientSide3.VALUE, r.getValue(TestFromClientSide3.FAMILY, QUALIFIERS[1])));
            Assert.assertNull(r.getValue(TestFromClientSide3.FAMILY, QUALIFIERS[0]));
            // Test that we get the correct remote exception for RowMutations from batch()
            try {
                arm = RowMutations.of(Collections.singletonList(new Put(TestFromClientSide3.ROW).addColumn(new byte[]{ 'b', 'o', 'g', 'u', 's' }, QUALIFIERS[0], TestFromClientSide3.VALUE)));
                t.batch(Arrays.asList(arm), batchResult);
                Assert.fail("Expected RetriesExhaustedWithDetailsException with NoSuchColumnFamilyException");
            } catch (RetriesExhaustedWithDetailsException e) {
                String msg = e.getMessage();
                Assert.assertTrue(msg.contains("NoSuchColumnFamilyException"));
            }
        }
    }

    @Test
    public void testHTableExistsMethodSingleRegionSingleGet() throws Exception {
        // Test with a single region table.
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY });
        Put put = new Put(TestFromClientSide3.ROW);
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
        Get get = new Get(TestFromClientSide3.ROW);
        boolean exist = table.exists(get);
        Assert.assertFalse(exist);
        table.put(put);
        exist = table.exists(get);
        Assert.assertTrue(exist);
    }

    @Test
    public void testHTableExistsMethodSingleRegionMultipleGets() throws Exception {
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY });
        Put put = new Put(TestFromClientSide3.ROW);
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
        table.put(put);
        List<Get> gets = new ArrayList<>();
        gets.add(new Get(TestFromClientSide3.ROW));
        gets.add(new Get(TestFromClientSide3.ANOTHERROW));
        boolean[] results = table.exists(gets);
        Assert.assertTrue(results[0]);
        Assert.assertFalse(results[1]);
    }

    @Test
    public void testHTableExistsBeforeGet() throws Exception {
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY });
        try {
            Put put = new Put(TestFromClientSide3.ROW);
            put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
            table.put(put);
            Get get = new Get(TestFromClientSide3.ROW);
            boolean exist = table.exists(get);
            Assert.assertEquals(true, exist);
            Result result = table.get(get);
            Assert.assertEquals(false, result.isEmpty());
            Assert.assertTrue(Bytes.equals(TestFromClientSide3.VALUE, result.getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER)));
        } finally {
            table.close();
        }
    }

    @Test
    public void testHTableExistsAllBeforeGet() throws Exception {
        final byte[] ROW2 = Bytes.add(TestFromClientSide3.ROW, Bytes.toBytes("2"));
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY });
        try {
            Put put = new Put(TestFromClientSide3.ROW);
            put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
            table.put(put);
            put = new Put(ROW2);
            put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
            table.put(put);
            Get get = new Get(TestFromClientSide3.ROW);
            Get get2 = new Get(ROW2);
            ArrayList<Get> getList = new ArrayList(2);
            getList.add(get);
            getList.add(get2);
            boolean[] exists = table.existsAll(getList);
            Assert.assertEquals(true, exists[0]);
            Assert.assertEquals(true, exists[1]);
            Result[] result = table.get(getList);
            Assert.assertEquals(false, result[0].isEmpty());
            Assert.assertTrue(Bytes.equals(TestFromClientSide3.VALUE, result[0].getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER)));
            Assert.assertEquals(false, result[1].isEmpty());
            Assert.assertTrue(Bytes.equals(TestFromClientSide3.VALUE, result[1].getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER)));
        } finally {
            table.close();
        }
    }

    @Test
    public void testHTableExistsMethodMultipleRegionsSingleGet() throws Exception {
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY }, 1, new byte[]{ 0 }, new byte[]{ ((byte) (255)) }, 255);
        Put put = new Put(TestFromClientSide3.ROW);
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
        Get get = new Get(TestFromClientSide3.ROW);
        boolean exist = table.exists(get);
        Assert.assertFalse(exist);
        table.put(put);
        exist = table.exists(get);
        Assert.assertTrue(exist);
    }

    @Test
    public void testHTableExistsMethodMultipleRegionsMultipleGets() throws Exception {
        Table table = TestFromClientSide3.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), new byte[][]{ TestFromClientSide3.FAMILY }, 1, new byte[]{ 0 }, new byte[]{ ((byte) (255)) }, 255);
        Put put = new Put(TestFromClientSide3.ROW);
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
        table.put(put);
        List<Get> gets = new ArrayList<>();
        gets.add(new Get(TestFromClientSide3.ANOTHERROW));
        gets.add(new Get(Bytes.add(TestFromClientSide3.ROW, new byte[]{ 0 })));
        gets.add(new Get(TestFromClientSide3.ROW));
        gets.add(new Get(Bytes.add(TestFromClientSide3.ANOTHERROW, new byte[]{ 0 })));
        TestFromClientSide3.LOG.info("Calling exists");
        boolean[] results = table.existsAll(gets);
        Assert.assertFalse(results[0]);
        Assert.assertFalse(results[1]);
        Assert.assertTrue(results[2]);
        Assert.assertFalse(results[3]);
        // Test with the first region.
        put = new Put(new byte[]{ 0 });
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
        table.put(put);
        gets = new ArrayList();
        gets.add(new Get(new byte[]{ 0 }));
        gets.add(new Get(new byte[]{ 0, 0 }));
        results = table.existsAll(gets);
        Assert.assertTrue(results[0]);
        Assert.assertFalse(results[1]);
        // Test with the last region
        put = new Put(new byte[]{ ((byte) (255)), ((byte) (255)) });
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
        table.put(put);
        gets = new ArrayList();
        gets.add(new Get(new byte[]{ ((byte) (255)) }));
        gets.add(new Get(new byte[]{ ((byte) (255)), ((byte) (255)) }));
        gets.add(new Get(new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)) }));
        results = table.existsAll(gets);
        Assert.assertFalse(results[0]);
        Assert.assertTrue(results[1]);
        Assert.assertFalse(results[2]);
    }

    @Test
    public void testGetEmptyRow() throws Exception {
        // Create a table and put in 1 row
        Admin admin = TestFromClientSide3.TEST_UTIL.getAdmin();
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(Bytes.toBytes(name.getMethodName())));
        desc.addFamily(new HColumnDescriptor(TestFromClientSide3.FAMILY));
        admin.createTable(desc);
        Table table = TestFromClientSide3.TEST_UTIL.getConnection().getTable(desc.getTableName());
        Put put = new Put(TestFromClientSide3.ROW_BYTES);
        put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.COL_QUAL, TestFromClientSide3.VAL_BYTES);
        table.put(put);
        // Try getting the row with an empty row key
        Result res = null;
        try {
            res = table.get(new Get(new byte[0]));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // Expected.
        }
        Assert.assertTrue((res == null));
        res = table.get(new Get(Bytes.toBytes("r1-not-exist")));
        Assert.assertTrue(((res.isEmpty()) == true));
        res = table.get(new Get(TestFromClientSide3.ROW_BYTES));
        Assert.assertTrue(Arrays.equals(res.getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.COL_QUAL), TestFromClientSide3.VAL_BYTES));
        table.close();
    }

    @Test
    public void testConnectionDefaultUsesCodec() throws Exception {
        ClusterConnection con = ((ClusterConnection) (TestFromClientSide3.TEST_UTIL.getConnection()));
        Assert.assertTrue(con.hasCellBlockSupport());
    }

    @Test
    public void testPutWithPreBatchMutate() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testPreBatchMutate(tableName, () -> {
            try {
                Table t = TEST_UTIL.getConnection().getTable(tableName);
                Put put = new Put(ROW);
                put.addColumn(TestFromClientSide3.FAMILY, QUALIFIER, VALUE);
                t.put(put);
            } catch ( ex) {
                throw new <ex>RuntimeException();
            }
        });
    }

    @Test
    public void testRowMutationsWithPreBatchMutate() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        testPreBatchMutate(tableName, () -> {
            try {
                RowMutations rm = new RowMutations(ROW, 1);
                Table t = TEST_UTIL.getConnection().getTable(tableName);
                Put put = new Put(ROW);
                put.addColumn(TestFromClientSide3.FAMILY, QUALIFIER, VALUE);
                rm.add(put);
                t.mutateRow(rm);
            } catch ( ex) {
                throw new <ex>RuntimeException();
            }
        });
    }

    @Test
    public void testLockLeakWithDelta() throws Exception, Throwable {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addCoprocessor(TestFromClientSide3.WaitingForMultiMutationsObserver.class.getName());
        desc.setConfiguration("hbase.rowlock.wait.duration", String.valueOf(5000));
        desc.addFamily(new HColumnDescriptor(TestFromClientSide3.FAMILY));
        TestFromClientSide3.TEST_UTIL.getAdmin().createTable(desc);
        // new a connection for lower retry number.
        Configuration copy = new Configuration(TestFromClientSide3.TEST_UTIL.getConfiguration());
        copy.setInt(HBASE_CLIENT_RETRIES_NUMBER, 2);
        try (Connection con = ConnectionFactory.createConnection(copy)) {
            HRegion region = ((HRegion) (TestFromClientSide3.find(tableName)));
            region.setTimeoutForWriteLock(10);
            ExecutorService putService = Executors.newSingleThreadExecutor();
            putService.execute(() -> {
                try (Table table = con.getTable(tableName)) {
                    Put put = new Put(TestFromClientSide3.ROW);
                    put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
                    // the put will be blocked by WaitingForMultiMutationsObserver.
                    table.put(put);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            ExecutorService appendService = Executors.newSingleThreadExecutor();
            appendService.execute(() -> {
                Append append = new Append(TestFromClientSide3.ROW);
                append.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, TestFromClientSide3.VALUE);
                try (Table table = con.getTable(tableName)) {
                    table.append(append);
                    Assert.fail("The APPEND should fail because the target lock is blocked by previous put");
                } catch (Exception ex) {
                }
            });
            appendService.shutdown();
            appendService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            TestFromClientSide3.WaitingForMultiMutationsObserver observer = TestFromClientSide3.find(tableName, TestFromClientSide3.WaitingForMultiMutationsObserver.class);
            observer.latch.countDown();
            putService.shutdown();
            putService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            try (Table table = con.getTable(tableName)) {
                Result r = table.get(new Get(TestFromClientSide3.ROW));
                Assert.assertFalse(r.isEmpty());
                Assert.assertTrue(Bytes.equals(r.getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER), TestFromClientSide3.VALUE));
            }
        }
        HRegion region = ((HRegion) (TestFromClientSide3.find(tableName)));
        int readLockCount = region.getReadLockCount();
        TestFromClientSide3.LOG.info(("readLockCount:" + readLockCount));
        Assert.assertEquals(0, readLockCount);
    }

    @Test
    public void testMultiRowMutations() throws Exception, Throwable {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addCoprocessor(MultiRowMutationEndpoint.class.getName());
        desc.addCoprocessor(TestFromClientSide3.WaitingForMultiMutationsObserver.class.getName());
        desc.setConfiguration("hbase.rowlock.wait.duration", String.valueOf(5000));
        desc.addFamily(new HColumnDescriptor(TestFromClientSide3.FAMILY));
        TestFromClientSide3.TEST_UTIL.getAdmin().createTable(desc);
        // new a connection for lower retry number.
        Configuration copy = new Configuration(TestFromClientSide3.TEST_UTIL.getConfiguration());
        copy.setInt(HBASE_CLIENT_RETRIES_NUMBER, 2);
        try (Connection con = ConnectionFactory.createConnection(copy)) {
            byte[] row = Bytes.toBytes("ROW-0");
            byte[] rowLocked = Bytes.toBytes("ROW-1");
            byte[] value0 = Bytes.toBytes("VALUE-0");
            byte[] value1 = Bytes.toBytes("VALUE-1");
            byte[] value2 = Bytes.toBytes("VALUE-2");
            TestFromClientSide3.assertNoLocks(tableName);
            ExecutorService putService = Executors.newSingleThreadExecutor();
            putService.execute(() -> {
                try (Table table = con.getTable(tableName)) {
                    Put put0 = new Put(rowLocked);
                    put0.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, value0);
                    // the put will be blocked by WaitingForMultiMutationsObserver.
                    table.put(put0);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            ExecutorService cpService = Executors.newSingleThreadExecutor();
            AtomicBoolean exceptionDuringMutateRows = new AtomicBoolean();
            cpService.execute(() -> {
                Put put1 = new Put(row);
                Put put2 = new Put(rowLocked);
                put1.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, value1);
                put2.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, value2);
                try (Table table = con.getTable(tableName)) {
                    MultiRowMutationProtos.MutateRowsRequest request = MutateRowsRequest.newBuilder().addMutationRequest(org.apache.hadoop.hbase.protobuf.ProtobufUtil.toMutation(PUT, put1)).addMutationRequest(org.apache.hadoop.hbase.protobuf.ProtobufUtil.toMutation(PUT, put2)).build();
                    table.coprocessorService(MultiRowMutationService.class, TestFromClientSide3.ROW, TestFromClientSide3.ROW, (MultiRowMutationProtos.MultiRowMutationService exe) -> {
                        ServerRpcController controller = new ServerRpcController();
                        CoprocessorRpcUtils.BlockingRpcCallback<MultiRowMutationProtos.MutateRowsResponse> rpcCallback = new CoprocessorRpcUtils.BlockingRpcCallback<>();
                        exe.mutateRows(controller, request, rpcCallback);
                        if ((controller.failedOnException()) && (!((controller.getFailedOn()) instanceof UnknownProtocolException))) {
                            exceptionDuringMutateRows.set(true);
                        }
                        return rpcCallback.get();
                    });
                } catch (Throwable ex) {
                    TestFromClientSide3.LOG.error(("encountered " + ex));
                }
            });
            cpService.shutdown();
            cpService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            TestFromClientSide3.WaitingForMultiMutationsObserver observer = TestFromClientSide3.find(tableName, TestFromClientSide3.WaitingForMultiMutationsObserver.class);
            observer.latch.countDown();
            putService.shutdown();
            putService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
            try (Table table = con.getTable(tableName)) {
                Get g0 = new Get(row);
                Get g1 = new Get(rowLocked);
                Result r0 = table.get(g0);
                Result r1 = table.get(g1);
                Assert.assertTrue(r0.isEmpty());
                Assert.assertFalse(r1.isEmpty());
                Assert.assertTrue(Bytes.equals(r1.getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER), value0));
            }
            TestFromClientSide3.assertNoLocks(tableName);
            if (!(exceptionDuringMutateRows.get())) {
                Assert.fail("This cp should fail because the target lock is blocked by previous put");
            }
        }
    }

    /**
     * A test case for issue HBASE-17482
     * After combile seqid with mvcc readpoint, seqid/mvcc is acquired and stamped
     * onto cells in the append thread, a countdown latch is used to ensure that happened
     * before cells can be put into memstore. But the MVCCPreAssign patch(HBASE-16698)
     * make the seqid/mvcc acquirement in handler thread and stamping in append thread
     * No countdown latch to assure cells in memstore are stamped with seqid/mvcc.
     * If cells without mvcc(A.K.A mvcc=0) are put into memstore, then a scanner
     * with a smaller readpoint can see these data, which disobey the multi version
     * concurrency control rules.
     * This test case is to reproduce this scenario.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testMVCCUsingMVCCPreAssign() throws IOException {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor htd = new HTableDescriptor(tableName);
        HColumnDescriptor fam = new HColumnDescriptor(TestFromClientSide3.FAMILY);
        htd.addFamily(fam);
        Admin admin = TestFromClientSide3.TEST_UTIL.getAdmin();
        admin.createTable(htd);
        Table table = admin.getConnection().getTable(TableName.valueOf(name.getMethodName()));
        // put two row first to init the scanner
        Put put = new Put(Bytes.toBytes("0"));
        put.addColumn(TestFromClientSide3.FAMILY, Bytes.toBytes(""), Bytes.toBytes("0"));
        table.put(put);
        put = new Put(Bytes.toBytes("00"));
        put.addColumn(TestFromClientSide3.FAMILY, Bytes.toBytes(""), Bytes.toBytes("0"));
        table.put(put);
        Scan scan = new Scan();
        scan.setTimeRange(0, Long.MAX_VALUE);
        scan.setCaching(1);
        ResultScanner scanner = table.getScanner(scan);
        int rowNum = ((scanner.next()) != null) ? 1 : 0;
        // the started scanner shouldn't see the rows put below
        for (int i = 1; i < 1000; i++) {
            put = new Put(Bytes.toBytes(String.valueOf(i)));
            put.setDurability(ASYNC_WAL);
            put.addColumn(TestFromClientSide3.FAMILY, Bytes.toBytes(""), Bytes.toBytes(i));
            table.put(put);
        }
        for (Result result : scanner) {
            rowNum++;
        }
        // scanner should only see two rows
        Assert.assertEquals(2, rowNum);
        scanner = table.getScanner(scan);
        rowNum = 0;
        for (Result result : scanner) {
            rowNum++;
        }
        // the new scanner should see all rows
        Assert.assertEquals(1001, rowNum);
    }

    @Test
    public void testPutThenGetWithMultipleThreads() throws Exception {
        final TableName tableName = TableName.valueOf(name.getMethodName());
        final int THREAD_NUM = 20;
        final int ROUND_NUM = 10;
        for (int round = 0; round < ROUND_NUM; round++) {
            ArrayList<Thread> threads = new ArrayList<>(THREAD_NUM);
            final AtomicInteger successCnt = new AtomicInteger(0);
            Table ht = TestFromClientSide3.TEST_UTIL.createTable(tableName, TestFromClientSide3.FAMILY);
            for (int i = 0; i < THREAD_NUM; i++) {
                final int index = i;
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final byte[] row = Bytes.toBytes(("row-" + index));
                        final byte[] value = Bytes.toBytes(("v" + index));
                        try {
                            Put put = new Put(row);
                            put.addColumn(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER, value);
                            ht.put(put);
                            Get get = new Get(row);
                            Result result = ht.get(get);
                            byte[] returnedValue = result.getValue(TestFromClientSide3.FAMILY, TestFromClientSide3.QUALIFIER);
                            if (Bytes.equals(value, returnedValue)) {
                                successCnt.getAndIncrement();
                            } else {
                                TestFromClientSide3.LOG.error(((("Should be equal but not, original value: " + (Bytes.toString(value))) + ", returned value: ") + (returnedValue == null ? "null" : Bytes.toString(returnedValue))));
                            }
                        } catch (Throwable e) {
                            // do nothing
                        }
                    }
                });
                threads.add(t);
            }
            for (Thread t : threads) {
                t.start();
            }
            for (Thread t : threads) {
                t.join();
            }
            Assert.assertEquals(("Not equal in round " + round), THREAD_NUM, successCnt.get());
            ht.close();
            TestFromClientSide3.TEST_UTIL.deleteTable(tableName);
        }
    }

    public static class WaitingForMultiMutationsObserver implements RegionCoprocessor , RegionObserver {
        final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postBatchMutate(final ObserverContext<RegionCoprocessorEnvironment> c, final MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            try {
                latch.await();
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }
    }

    public static class WaitingForScanObserver implements RegionCoprocessor , RegionObserver {
        private final CountDownLatch latch = new CountDownLatch(1);

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void postBatchMutate(final ObserverContext<RegionCoprocessorEnvironment> c, final MiniBatchOperationInProgress<Mutation> miniBatchOp) throws IOException {
            try {
                // waiting for scanner
                latch.await();
            } catch (InterruptedException ex) {
                throw new IOException(ex);
            }
        }

        @Override
        public RegionScanner postScannerOpen(final ObserverContext<RegionCoprocessorEnvironment> e, final Scan scan, final RegionScanner s) throws IOException {
            latch.countDown();
            return s;
        }
    }

    @Test
    public void testScanWithBatchSizeReturnIncompleteCells() throws IOException {
        TableName tableName = TableName.valueOf(name.getMethodName());
        TableDescriptor hd = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(TestFromClientSide3.FAMILY).setMaxVersions(3).build()).build();
        Table table = TestFromClientSide3.TEST_UTIL.createTable(hd, null);
        Put put = new Put(TestFromClientSide3.ROW);
        put.addColumn(TestFromClientSide3.FAMILY, Bytes.toBytes(0), TestFromClientSide3.generateHugeValue(((3 * 1024) * 1024)));
        table.put(put);
        put = new Put(TestFromClientSide3.ROW);
        put.addColumn(TestFromClientSide3.FAMILY, Bytes.toBytes(1), TestFromClientSide3.generateHugeValue(((4 * 1024) * 1024)));
        table.put(put);
        for (int i = 2; i < 5; i++) {
            for (int version = 0; version < 2; version++) {
                put = new Put(TestFromClientSide3.ROW);
                put.addColumn(TestFromClientSide3.FAMILY, Bytes.toBytes(i), TestFromClientSide3.generateHugeValue(1024));
                table.put(put);
            }
        }
        Scan scan = new Scan();
        scan.withStartRow(TestFromClientSide3.ROW).withStopRow(TestFromClientSide3.ROW, true).addFamily(TestFromClientSide3.FAMILY).setBatch(3).setMaxResultSize(((4 * 1024) * 1024));
        Result result;
        try (ResultScanner scanner = table.getScanner(scan)) {
            List<Result> list = new ArrayList<>();
            /* The first scan rpc should return a result with 2 cells, because 3MB + 4MB > 4MB; The second
            scan rpc should return a result with 3 cells, because reach the batch limit = 3; The
            mayHaveMoreCellsInRow in last result should be false in the scan rpc. BTW, the
            moreResultsInRegion also would be false. Finally, the client should collect all the cells
            into two result: 2+3 -> 3+2;
             */
            while ((result = scanner.next()) != null) {
                list.add(result);
            } 
            Assert.assertEquals(5, list.stream().mapToInt(Result::size).sum());
            Assert.assertEquals(2, list.size());
            Assert.assertEquals(3, list.get(0).size());
            Assert.assertEquals(2, list.get(1).size());
        }
        scan = new Scan();
        scan.withStartRow(TestFromClientSide3.ROW).withStopRow(TestFromClientSide3.ROW, true).addFamily(TestFromClientSide3.FAMILY).setBatch(2).setMaxResultSize(((4 * 1024) * 1024));
        try (ResultScanner scanner = table.getScanner(scan)) {
            List<Result> list = new ArrayList<>();
            while ((result = scanner.next()) != null) {
                list.add(result);
            } 
            Assert.assertEquals(5, list.stream().mapToInt(Result::size).sum());
            Assert.assertEquals(3, list.size());
            Assert.assertEquals(2, list.get(0).size());
            Assert.assertEquals(2, list.get(1).size());
            Assert.assertEquals(1, list.get(2).size());
        }
    }
}

