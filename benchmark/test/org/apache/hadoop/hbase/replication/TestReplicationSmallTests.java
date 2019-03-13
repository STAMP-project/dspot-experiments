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
package org.apache.hadoop.hbase.replication;


import HConstants.REPLICATION_SCOPE_GLOBAL;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.client.replication.TableCFs;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.ReplicationTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.wal.WAL;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.apache.hadoop.hbase.wal.WALKeyImpl;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RunWith(Parameterized.class)
@Category({ ReplicationTests.class, LargeTests.class })
public class TestReplicationSmallTests extends TestReplicationBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestReplicationSmallTests.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestReplicationSmallTests.class);

    private static final String PEER_ID = "2";

    @Parameterized.Parameter
    public boolean serialPeer;

    /**
     * Verify that version and column delete marker types are replicated correctly.
     */
    @Test
    public void testDeleteTypes() throws Exception {
        TestReplicationSmallTests.LOG.info("testDeleteTypes");
        final byte[] v1 = Bytes.toBytes("v1");
        final byte[] v2 = Bytes.toBytes("v2");
        final byte[] v3 = Bytes.toBytes("v3");
        TestReplicationBase.htable1 = TestReplicationBase.utility1.getConnection().getTable(TestReplicationBase.tableName);
        long t = EnvironmentEdgeManager.currentTime();
        // create three versions for "row"
        Put put = new Put(TestReplicationBase.row);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, t, v1);
        TestReplicationBase.htable1.put(put);
        put = new Put(TestReplicationBase.row);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, (t + 1), v2);
        TestReplicationBase.htable1.put(put);
        put = new Put(TestReplicationBase.row);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, (t + 2), v3);
        TestReplicationBase.htable1.put(put);
        Get get = new Get(TestReplicationBase.row);
        get.readAllVersions();
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            if (i == ((TestReplicationBase.NB_RETRIES) - 1)) {
                Assert.fail("Waited too much time for put replication");
            }
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) < 3) {
                TestReplicationSmallTests.LOG.info("Rows not available");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            } else {
                Assert.assertArrayEquals(CellUtil.cloneValue(res.rawCells()[0]), v3);
                Assert.assertArrayEquals(CellUtil.cloneValue(res.rawCells()[1]), v2);
                Assert.assertArrayEquals(CellUtil.cloneValue(res.rawCells()[2]), v1);
                break;
            }
        }
        // place a version delete marker (delete last version)
        Delete d = new Delete(TestReplicationBase.row);
        d.addColumn(TestReplicationBase.famName, TestReplicationBase.row, t);
        TestReplicationBase.htable1.delete(d);
        get = new Get(TestReplicationBase.row);
        get.readAllVersions();
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            if (i == ((TestReplicationBase.NB_RETRIES) - 1)) {
                Assert.fail("Waited too much time for put replication");
            }
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) > 2) {
                TestReplicationSmallTests.LOG.info("Version not deleted");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            } else {
                Assert.assertArrayEquals(CellUtil.cloneValue(res.rawCells()[0]), v3);
                Assert.assertArrayEquals(CellUtil.cloneValue(res.rawCells()[1]), v2);
                break;
            }
        }
        // place a column delete marker
        d = new Delete(TestReplicationBase.row);
        d.addColumns(TestReplicationBase.famName, TestReplicationBase.row, (t + 2));
        TestReplicationBase.htable1.delete(d);
        // now *both* of the remaining version should be deleted
        // at the replica
        get = new Get(TestReplicationBase.row);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            if (i == ((TestReplicationBase.NB_RETRIES) - 1)) {
                Assert.fail("Waited too much time for del replication");
            }
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) >= 1) {
                TestReplicationSmallTests.LOG.info("Rows not deleted");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            } else {
                break;
            }
        }
    }

    /**
     * Add a row, check it's replicated, delete it, check's gone
     */
    @Test
    public void testSimplePutDelete() throws Exception {
        TestReplicationSmallTests.LOG.info("testSimplePutDelete");
        TestReplicationBase.runSimplePutDeleteTest();
    }

    /**
     * Try a small batch upload using the write buffer, check it's replicated
     */
    @Test
    public void testSmallBatch() throws Exception {
        TestReplicationSmallTests.LOG.info("testSmallBatch");
        TestReplicationBase.runSmallBatchTest();
    }

    /**
     * Test disable/enable replication, trying to insert, make sure nothing's replicated, enable it,
     * the insert should be replicated
     */
    @Test
    public void testDisableEnable() throws Exception {
        // Test disabling replication
        TestReplicationBase.hbaseAdmin.disableReplicationPeer(TestReplicationSmallTests.PEER_ID);
        byte[] rowkey = Bytes.toBytes("disable enable");
        Put put = new Put(rowkey);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, TestReplicationBase.row);
        TestReplicationBase.htable1.put(put);
        Get get = new Get(rowkey);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) >= 1) {
                Assert.fail("Replication wasn't disabled");
            } else {
                TestReplicationSmallTests.LOG.info("Row not replicated, let's wait a bit more...");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            }
        }
        // Test enable replication
        TestReplicationBase.hbaseAdmin.enableReplicationPeer(TestReplicationSmallTests.PEER_ID);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            Result res = TestReplicationBase.htable2.get(get);
            if (res.isEmpty()) {
                TestReplicationSmallTests.LOG.info("Row not available");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            } else {
                Assert.assertArrayEquals(TestReplicationBase.row, res.value());
                return;
            }
        }
        Assert.fail("Waited too much time for put replication");
    }

    /**
     * Integration test for TestReplicationAdmin, removes and re-add a peer cluster
     */
    @Test
    public void testAddAndRemoveClusters() throws Exception {
        TestReplicationSmallTests.LOG.info("testAddAndRemoveClusters");
        TestReplicationBase.hbaseAdmin.removeReplicationPeer(TestReplicationSmallTests.PEER_ID);
        Thread.sleep(TestReplicationBase.SLEEP_TIME);
        byte[] rowKey = Bytes.toBytes("Won't be replicated");
        Put put = new Put(rowKey);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, TestReplicationBase.row);
        TestReplicationBase.htable1.put(put);
        Get get = new Get(rowKey);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            if (i == ((TestReplicationBase.NB_RETRIES) - 1)) {
                break;
            }
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) >= 1) {
                Assert.fail("Not supposed to be replicated");
            } else {
                TestReplicationSmallTests.LOG.info("Row not replicated, let's wait a bit more...");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            }
        }
        ReplicationPeerConfig rpc = ReplicationPeerConfig.newBuilder().setClusterKey(TestReplicationBase.utility2.getClusterKey()).build();
        TestReplicationBase.hbaseAdmin.addReplicationPeer(TestReplicationSmallTests.PEER_ID, rpc);
        Thread.sleep(TestReplicationBase.SLEEP_TIME);
        rowKey = Bytes.toBytes("do rep");
        put = new Put(rowKey);
        put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, TestReplicationBase.row);
        TestReplicationSmallTests.LOG.info("Adding new row");
        TestReplicationBase.htable1.put(put);
        get = new Get(rowKey);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            if (i == ((TestReplicationBase.NB_RETRIES) - 1)) {
                Assert.fail("Waited too much time for put replication");
            }
            Result res = TestReplicationBase.htable2.get(get);
            if (res.isEmpty()) {
                TestReplicationSmallTests.LOG.info("Row not available");
                Thread.sleep(((TestReplicationBase.SLEEP_TIME) * i));
            } else {
                Assert.assertArrayEquals(TestReplicationBase.row, res.value());
                break;
            }
        }
    }

    /**
     * Do a more intense version testSmallBatch, one that will trigger wal rolling and other
     * non-trivial code paths
     */
    @Test
    public void testLoading() throws Exception {
        TestReplicationSmallTests.LOG.info("Writing out rows to table1 in testLoading");
        List<Put> puts = new ArrayList<>(TestReplicationBase.NB_ROWS_IN_BIG_BATCH);
        for (int i = 0; i < (TestReplicationBase.NB_ROWS_IN_BIG_BATCH); i++) {
            Put put = new Put(Bytes.toBytes(i));
            put.addColumn(TestReplicationBase.famName, TestReplicationBase.row, TestReplicationBase.row);
            puts.add(put);
        }
        // The puts will be iterated through and flushed only when the buffer
        // size is reached.
        TestReplicationBase.htable1.put(puts);
        Scan scan = new Scan();
        ResultScanner scanner = TestReplicationBase.htable1.getScanner(scan);
        Result[] res = scanner.next(TestReplicationBase.NB_ROWS_IN_BIG_BATCH);
        scanner.close();
        Assert.assertEquals(TestReplicationBase.NB_ROWS_IN_BIG_BATCH, res.length);
        TestReplicationSmallTests.LOG.info("Looking in table2 for replicated rows in testLoading");
        long start = System.currentTimeMillis();
        // Retry more than NB_RETRIES. As it was, retries were done in 5 seconds and we'd fail
        // sometimes.
        final long retries = (TestReplicationBase.NB_RETRIES) * 10;
        for (int i = 0; i < retries; i++) {
            scan = new Scan();
            scanner = TestReplicationBase.htable2.getScanner(scan);
            res = scanner.next(TestReplicationBase.NB_ROWS_IN_BIG_BATCH);
            scanner.close();
            if ((res.length) != (TestReplicationBase.NB_ROWS_IN_BIG_BATCH)) {
                if (i == (retries - 1)) {
                    int lastRow = -1;
                    for (Result result : res) {
                        int currentRow = Bytes.toInt(result.getRow());
                        for (int row = lastRow + 1; row < currentRow; row++) {
                            TestReplicationSmallTests.LOG.error(("Row missing: " + row));
                        }
                        lastRow = currentRow;
                    }
                    TestReplicationSmallTests.LOG.error(("Last row: " + lastRow));
                    Assert.fail((((((("Waited too much time for normal batch replication, " + (res.length)) + " instead of ") + (TestReplicationBase.NB_ROWS_IN_BIG_BATCH)) + "; waited=") + ((System.currentTimeMillis()) - start)) + "ms"));
                } else {
                    TestReplicationSmallTests.LOG.info((("Only got " + (res.length)) + " rows... retrying"));
                    Thread.sleep(TestReplicationBase.SLEEP_TIME);
                }
            } else {
                break;
            }
        }
    }

    /**
     * Test for HBASE-8663
     * <p>
     * Create two new Tables with colfamilies enabled for replication then run
     * ReplicationAdmin.listReplicated(). Finally verify the table:colfamilies. Note:
     * TestReplicationAdmin is a better place for this testing but it would need mocks.
     */
    @Test
    public void testVerifyListReplicatedTable() throws Exception {
        TestReplicationSmallTests.LOG.info("testVerifyListReplicatedTable");
        final String tName = "VerifyListReplicated_";
        final String colFam = "cf1";
        final int numOfTables = 3;
        Admin hadmin = TestReplicationBase.utility1.getAdmin();
        // Create Tables
        for (int i = 0; i < numOfTables; i++) {
            hadmin.createTable(TableDescriptorBuilder.newBuilder(TableName.valueOf((tName + i))).setColumnFamily(ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(colFam)).setScope(REPLICATION_SCOPE_GLOBAL).build()).build());
        }
        // verify the result
        List<TableCFs> replicationColFams = TestReplicationBase.hbaseAdmin.listReplicatedTableCFs();
        int[] match = new int[numOfTables];// array of 3 with init value of zero

        for (int i = 0; i < (replicationColFams.size()); i++) {
            TableCFs replicationEntry = replicationColFams.get(i);
            String tn = replicationEntry.getTable().getNameAsString();
            if ((tn.startsWith(tName)) && (replicationEntry.getColumnFamilyMap().containsKey(colFam))) {
                int m = Integer.parseInt(tn.substring(((tn.length()) - 1)));// get the last digit

                (match[m])++;// should only increase once

            }
        }
        // check the matching result
        for (int i = 0; i < (match.length); i++) {
            Assert.assertTrue(("listReplicated() does not match table " + i), ((match[i]) == 1));
        }
        // drop tables
        for (int i = 0; i < numOfTables; i++) {
            TableName tableName = TableName.valueOf((tName + i));
            hadmin.disableTable(tableName);
            hadmin.deleteTable(tableName);
        }
        hadmin.close();
    }

    /**
     * Test for HBase-15259 WALEdits under replay will also be replicated
     */
    @Test
    public void testReplicationInReplay() throws Exception {
        final TableName tableName = TestReplicationBase.htable1.getName();
        HRegion region = TestReplicationBase.utility1.getMiniHBaseCluster().getRegions(tableName).get(0);
        RegionInfo hri = region.getRegionInfo();
        NavigableMap<byte[], Integer> scopes = new java.util.TreeMap(Bytes.BYTES_COMPARATOR);
        for (byte[] fam : TestReplicationBase.htable1.getDescriptor().getColumnFamilyNames()) {
            scopes.put(fam, 1);
        }
        final MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        int index = TestReplicationBase.utility1.getMiniHBaseCluster().getServerWith(hri.getRegionName());
        WAL wal = TestReplicationBase.utility1.getMiniHBaseCluster().getRegionServer(index).getWAL(region.getRegionInfo());
        final byte[] rowName = Bytes.toBytes("testReplicationInReplay");
        final byte[] qualifier = Bytes.toBytes("q");
        final byte[] value = Bytes.toBytes("v");
        WALEdit edit = new WALEdit(true);
        long now = EnvironmentEdgeManager.currentTime();
        edit.add(new KeyValue(rowName, TestReplicationBase.famName, qualifier, now, value));
        WALKeyImpl walKey = new WALKeyImpl(hri.getEncodedNameAsBytes(), tableName, now, mvcc, scopes);
        wal.append(hri, walKey, edit, true);
        wal.sync();
        Get get = new Get(rowName);
        for (int i = 0; i < (TestReplicationBase.NB_RETRIES); i++) {
            if (i == ((TestReplicationBase.NB_RETRIES) - 1)) {
                break;
            }
            Result res = TestReplicationBase.htable2.get(get);
            if ((res.size()) >= 1) {
                Assert.fail(("Not supposed to be replicated for " + (Bytes.toString(res.getRow()))));
            } else {
                TestReplicationSmallTests.LOG.info("Row not replicated, let's wait a bit more...");
                Thread.sleep(TestReplicationBase.SLEEP_TIME);
            }
        }
    }
}

