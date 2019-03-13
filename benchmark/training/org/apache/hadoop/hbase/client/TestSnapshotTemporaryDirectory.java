/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.client;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.snapshot.SnapshotDoesNotExistException;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests that the use of a temporary snapshot directory supports snapshot functionality
 * while the temporary directory is on a different file system than the root directory
 * <p>
 * This is an end-to-end test for the snapshot utility
 */
@Category(LargeTests.class)
@RunWith(Parameterized.class)
public class TestSnapshotTemporaryDirectory {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotTemporaryDirectory.class);

    @Parameterized.Parameter
    public int manifestVersion;

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotTemporaryDirectory.class);

    protected static final int NUM_RS = 2;

    protected static String TEMP_DIR = ((Paths.get("").toAbsolutePath().toString()) + (Path.SEPARATOR)) + (UUID.randomUUID().toString());

    protected static Admin admin;

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final String STRING_TABLE_NAME = "test";

    protected static final byte[] TEST_FAM = Bytes.toBytes("fam");

    protected static final TableName TABLE_NAME = TableName.valueOf(TestSnapshotTemporaryDirectory.STRING_TABLE_NAME);

    @Test(timeout = 180000)
    public void testRestoreDisabledSnapshot() throws IOException, InterruptedException {
        long tid = System.currentTimeMillis();
        TableName tableName = TableName.valueOf(("testtb-" + tid));
        byte[] emptySnapshot = Bytes.toBytes(("emptySnaptb-" + tid));
        byte[] snapshotName0 = Bytes.toBytes(("snaptb0-" + tid));
        byte[] snapshotName1 = Bytes.toBytes(("snaptb1-" + tid));
        int snapshot0Rows;
        int snapshot1Rows;
        // create Table and disable it
        SnapshotTestingUtils.createTable(TestSnapshotTemporaryDirectory.UTIL, tableName, getNumReplicas(), TestSnapshotTemporaryDirectory.TEST_FAM);
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        // take an empty snapshot
        takeSnapshot(tableName, Bytes.toString(emptySnapshot), true);
        // enable table and insert data
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.loadData(TestSnapshotTemporaryDirectory.UTIL, tableName, 500, TestSnapshotTemporaryDirectory.TEST_FAM);
        try (Table table = TestSnapshotTemporaryDirectory.UTIL.getConnection().getTable(tableName)) {
            snapshot0Rows = TestSnapshotTemporaryDirectory.UTIL.countRows(table);
        }
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        // take a snapshot
        takeSnapshot(tableName, Bytes.toString(snapshotName0), true);
        // enable table and insert more data
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.loadData(TestSnapshotTemporaryDirectory.UTIL, tableName, 500, TestSnapshotTemporaryDirectory.TEST_FAM);
        try (Table table = TestSnapshotTemporaryDirectory.UTIL.getConnection().getTable(tableName)) {
            snapshot1Rows = TestSnapshotTemporaryDirectory.UTIL.countRows(table);
        }
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot1Rows);
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        takeSnapshot(tableName, Bytes.toString(snapshotName1), true);
        // Restore from snapshot-0
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(snapshotName0);
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot0Rows);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
        // Restore from emptySnapshot
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(emptySnapshot);
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, 0);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
        // Restore from snapshot-1
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(snapshotName1);
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot1Rows);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
        // Restore from snapshot-1
        TestSnapshotTemporaryDirectory.UTIL.deleteTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(snapshotName1);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot1Rows);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
    }

    @Test(timeout = 180000)
    public void testRestoreEnabledSnapshot() throws IOException, InterruptedException {
        long tid = System.currentTimeMillis();
        TableName tableName = TableName.valueOf(("testtb-" + tid));
        byte[] emptySnapshot = Bytes.toBytes(("emptySnaptb-" + tid));
        byte[] snapshotName0 = Bytes.toBytes(("snaptb0-" + tid));
        byte[] snapshotName1 = Bytes.toBytes(("snaptb1-" + tid));
        int snapshot0Rows;
        int snapshot1Rows;
        // create Table
        SnapshotTestingUtils.createTable(TestSnapshotTemporaryDirectory.UTIL, tableName, getNumReplicas(), TestSnapshotTemporaryDirectory.TEST_FAM);
        // take an empty snapshot
        takeSnapshot(tableName, Bytes.toString(emptySnapshot), false);
        // Insert data
        SnapshotTestingUtils.loadData(TestSnapshotTemporaryDirectory.UTIL, tableName, 500, TestSnapshotTemporaryDirectory.TEST_FAM);
        try (Table table = TestSnapshotTemporaryDirectory.UTIL.getConnection().getTable(tableName)) {
            snapshot0Rows = TestSnapshotTemporaryDirectory.UTIL.countRows(table);
        }
        // take a snapshot
        takeSnapshot(tableName, Bytes.toString(snapshotName0), false);
        // Insert more data
        SnapshotTestingUtils.loadData(TestSnapshotTemporaryDirectory.UTIL, tableName, 500, TestSnapshotTemporaryDirectory.TEST_FAM);
        try (Table table = TestSnapshotTemporaryDirectory.UTIL.getConnection().getTable(tableName)) {
            snapshot1Rows = TestSnapshotTemporaryDirectory.UTIL.countRows(table);
        }
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot1Rows);
        takeSnapshot(tableName, Bytes.toString(snapshotName1), false);
        // Restore from snapshot-0
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(snapshotName0);
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot0Rows);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
        // Restore from emptySnapshot
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(emptySnapshot);
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, 0);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
        // Restore from snapshot-1
        TestSnapshotTemporaryDirectory.admin.disableTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(snapshotName1);
        TestSnapshotTemporaryDirectory.admin.enableTable(tableName);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot1Rows);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
        // Restore from snapshot-1
        TestSnapshotTemporaryDirectory.UTIL.deleteTable(tableName);
        TestSnapshotTemporaryDirectory.admin.restoreSnapshot(snapshotName1);
        SnapshotTestingUtils.verifyRowCount(TestSnapshotTemporaryDirectory.UTIL, tableName, snapshot1Rows);
        SnapshotTestingUtils.verifyReplicasCameOnline(tableName, TestSnapshotTemporaryDirectory.admin, getNumReplicas());
    }

    /**
     * Test snapshotting a table that is offline
     *
     * @throws Exception
     * 		if snapshot does not complete successfully
     */
    @Test(timeout = 300000)
    public void testOfflineTableSnapshot() throws Exception {
        Admin admin = TestSnapshotTemporaryDirectory.UTIL.getHBaseAdmin();
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // put some stuff in the table
        Table table = TestSnapshotTemporaryDirectory.UTIL.getConnection().getTable(TestSnapshotTemporaryDirectory.TABLE_NAME);
        TestSnapshotTemporaryDirectory.UTIL.loadTable(table, TestSnapshotTemporaryDirectory.TEST_FAM, false);
        TestSnapshotTemporaryDirectory.LOG.debug("FS state before disable:");
        FSUtils.logFileSystemState(TestSnapshotTemporaryDirectory.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotTemporaryDirectory.UTIL.getConfiguration()), TestSnapshotTemporaryDirectory.LOG);
        // XXX if this is flakey, might want to consider using the async version and looping as
        // disableTable can succeed and still timeout.
        admin.disableTable(TestSnapshotTemporaryDirectory.TABLE_NAME);
        TestSnapshotTemporaryDirectory.LOG.debug("FS state before snapshot:");
        FSUtils.logFileSystemState(TestSnapshotTemporaryDirectory.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotTemporaryDirectory.UTIL.getConfiguration()), TestSnapshotTemporaryDirectory.LOG);
        // take a snapshot of the disabled table
        final String SNAPSHOT_NAME = "offlineTableSnapshot";
        byte[] snapshot = Bytes.toBytes(SNAPSHOT_NAME);
        takeSnapshot(TestSnapshotTemporaryDirectory.TABLE_NAME, SNAPSHOT_NAME, true);
        TestSnapshotTemporaryDirectory.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot, TestSnapshotTemporaryDirectory.TABLE_NAME);
        // make sure its a valid snapshot
        FileSystem fs = TestSnapshotTemporaryDirectory.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getFileSystem();
        Path rootDir = TestSnapshotTemporaryDirectory.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getRootDir();
        TestSnapshotTemporaryDirectory.LOG.debug("FS state after snapshot:");
        FSUtils.logFileSystemState(TestSnapshotTemporaryDirectory.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotTemporaryDirectory.UTIL.getConfiguration()), TestSnapshotTemporaryDirectory.LOG);
        SnapshotTestingUtils.confirmSnapshotValid(ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestSnapshotTemporaryDirectory.TABLE_NAME, TestSnapshotTemporaryDirectory.TEST_FAM, rootDir, admin, fs);
        admin.deleteSnapshot(snapshot);
        SnapshotTestingUtils.assertNoSnapshots(admin);
    }

    /**
     * Tests that snapshot has correct contents by taking snapshot, cloning it, then affirming
     * the contents of the original and cloned table match
     *
     * @throws Exception
     * 		if snapshot does not complete successfully
     */
    @Test(timeout = 180000)
    public void testSnapshotCloneContents() throws Exception {
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(TestSnapshotTemporaryDirectory.admin);
        // put some stuff in the table
        Table table = TestSnapshotTemporaryDirectory.UTIL.getConnection().getTable(TestSnapshotTemporaryDirectory.TABLE_NAME);
        TestSnapshotTemporaryDirectory.UTIL.loadTable(table, TestSnapshotTemporaryDirectory.TEST_FAM);
        table.close();
        String snapshot1 = "TableSnapshot1";
        takeSnapshot(TestSnapshotTemporaryDirectory.TABLE_NAME, snapshot1, false);
        TestSnapshotTemporaryDirectory.LOG.debug("Snapshot1 completed.");
        TableName clone = TableName.valueOf("Table1Clone");
        TestSnapshotTemporaryDirectory.admin.cloneSnapshot(snapshot1, clone, false);
        Scan original = new Scan();
        Scan cloned = new Scan();
        ResultScanner originalScan = TestSnapshotTemporaryDirectory.admin.getConnection().getTable(TestSnapshotTemporaryDirectory.TABLE_NAME).getScanner(original);
        ResultScanner clonedScan = TestSnapshotTemporaryDirectory.admin.getConnection().getTable(TableName.valueOf("Table1Clone")).getScanner(cloned);
        Iterator<Result> i = originalScan.iterator();
        Iterator<Result> i2 = clonedScan.iterator();
        Assert.assertTrue(i.hasNext());
        while (i.hasNext()) {
            Assert.assertTrue(i2.hasNext());
            Assert.assertEquals(Bytes.toString(i.next().getValue(TestSnapshotTemporaryDirectory.TEST_FAM, new byte[]{  })), Bytes.toString(i2.next().getValue(TestSnapshotTemporaryDirectory.TEST_FAM, new byte[]{  })));
        } 
        Assert.assertFalse(i2.hasNext());
        TestSnapshotTemporaryDirectory.admin.deleteSnapshot(snapshot1);
        TestSnapshotTemporaryDirectory.UTIL.deleteTable(clone);
        TestSnapshotTemporaryDirectory.admin.close();
    }

    @Test(timeout = 180000)
    public void testOfflineTableSnapshotWithEmptyRegion() throws Exception {
        // test with an empty table with one region
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(TestSnapshotTemporaryDirectory.admin);
        TestSnapshotTemporaryDirectory.LOG.debug("FS state before disable:");
        FSUtils.logFileSystemState(TestSnapshotTemporaryDirectory.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotTemporaryDirectory.UTIL.getConfiguration()), TestSnapshotTemporaryDirectory.LOG);
        TestSnapshotTemporaryDirectory.admin.disableTable(TestSnapshotTemporaryDirectory.TABLE_NAME);
        TestSnapshotTemporaryDirectory.LOG.debug("FS state before snapshot:");
        FSUtils.logFileSystemState(TestSnapshotTemporaryDirectory.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotTemporaryDirectory.UTIL.getConfiguration()), TestSnapshotTemporaryDirectory.LOG);
        // take a snapshot of the disabled table
        byte[] snapshot = Bytes.toBytes("testOfflineTableSnapshotWithEmptyRegion");
        takeSnapshot(TestSnapshotTemporaryDirectory.TABLE_NAME, Bytes.toString(snapshot), true);
        TestSnapshotTemporaryDirectory.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(TestSnapshotTemporaryDirectory.admin, snapshot, TestSnapshotTemporaryDirectory.TABLE_NAME);
        // make sure its a valid snapshot
        FileSystem fs = TestSnapshotTemporaryDirectory.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getFileSystem();
        Path rootDir = TestSnapshotTemporaryDirectory.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getRootDir();
        TestSnapshotTemporaryDirectory.LOG.debug("FS state after snapshot:");
        FSUtils.logFileSystemState(TestSnapshotTemporaryDirectory.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotTemporaryDirectory.UTIL.getConfiguration()), TestSnapshotTemporaryDirectory.LOG);
        List<byte[]> emptyCfs = Lists.newArrayList(TestSnapshotTemporaryDirectory.TEST_FAM);// no file in the region

        List<byte[]> nonEmptyCfs = Lists.newArrayList();
        SnapshotTestingUtils.confirmSnapshotValid(ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestSnapshotTemporaryDirectory.TABLE_NAME, nonEmptyCfs, emptyCfs, rootDir, TestSnapshotTemporaryDirectory.admin, fs);
        TestSnapshotTemporaryDirectory.admin.deleteSnapshot(snapshot);
        SnapshotTestingUtils.assertNoSnapshots(TestSnapshotTemporaryDirectory.admin);
    }

    // Ensures that the snapshot is transferred to the proper completed snapshot directory
    @Test(timeout = 180000)
    public void testEnsureTemporaryDirectoryTransfer() throws Exception {
        Admin admin = null;
        TableName tableName2 = TableName.valueOf("testListTableSnapshots");
        try {
            admin = TestSnapshotTemporaryDirectory.UTIL.getHBaseAdmin();
            HTableDescriptor htd = new HTableDescriptor(tableName2);
            TestSnapshotTemporaryDirectory.UTIL.createTable(htd, new byte[][]{ TestSnapshotTemporaryDirectory.TEST_FAM }, TestSnapshotTemporaryDirectory.UTIL.getConfiguration());
            String table1Snapshot1 = "Table1Snapshot1";
            takeSnapshot(TestSnapshotTemporaryDirectory.TABLE_NAME, table1Snapshot1, false);
            TestSnapshotTemporaryDirectory.LOG.debug("Snapshot1 completed.");
            String table1Snapshot2 = "Table1Snapshot2";
            takeSnapshot(TestSnapshotTemporaryDirectory.TABLE_NAME, table1Snapshot2, false);
            TestSnapshotTemporaryDirectory.LOG.debug("Snapshot2 completed.");
            String table2Snapshot1 = "Table2Snapshot1";
            takeSnapshot(TestSnapshotTemporaryDirectory.TABLE_NAME, table2Snapshot1, false);
            TestSnapshotTemporaryDirectory.LOG.debug("Table2Snapshot1 completed.");
            List<SnapshotDescription> listTableSnapshots = admin.listTableSnapshots("test.*", ".*");
            List<String> listTableSnapshotNames = new ArrayList<String>();
            Assert.assertEquals(3, listTableSnapshots.size());
            for (SnapshotDescription s : listTableSnapshots) {
                listTableSnapshotNames.add(s.getName());
            }
            Assert.assertTrue(listTableSnapshotNames.contains(table1Snapshot1));
            Assert.assertTrue(listTableSnapshotNames.contains(table1Snapshot2));
            Assert.assertTrue(listTableSnapshotNames.contains(table2Snapshot1));
        } finally {
            if (admin != null) {
                try {
                    admin.deleteSnapshots("Table.*");
                } catch (SnapshotDoesNotExistException ignore) {
                }
                if (admin.tableExists(tableName2)) {
                    TestSnapshotTemporaryDirectory.UTIL.deleteTable(tableName2);
                }
                admin.close();
            }
        }
    }
}

