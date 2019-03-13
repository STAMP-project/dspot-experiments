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


import TableName.META_TABLE_NAME;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.snapshot.SnapshotCreationException;
import org.apache.hadoop.hbase.snapshot.SnapshotDoesNotExistException;
import org.apache.hadoop.hbase.snapshot.SnapshotManifestV1;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static SnapshotType.DISABLED;


/**
 * Test create/using/deleting snapshots from the client
 * <p>
 * This is an end-to-end test for the snapshot utility
 */
@Category({ LargeTests.class, ClientTests.class })
public class TestSnapshotFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotFromClient.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotFromClient.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final int NUM_RS = 2;

    protected static final String STRING_TABLE_NAME = "test";

    protected static final byte[] TEST_FAM = Bytes.toBytes("fam");

    protected static final TableName TABLE_NAME = TableName.valueOf(TestSnapshotFromClient.STRING_TABLE_NAME);

    private static final Pattern MATCH_ALL = Pattern.compile(".*");

    @Rule
    public TestName name = new TestName();

    /**
     * Test snapshotting not allowed hbase:meta and -ROOT-
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMetaTablesSnapshot() throws Exception {
        Admin admin = TestSnapshotFromClient.UTIL.getAdmin();
        byte[] snapshotName = Bytes.toBytes("metaSnapshot");
        try {
            admin.snapshot(snapshotName, META_TABLE_NAME);
            Assert.fail("taking a snapshot of hbase:meta should not be allowed");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test HBaseAdmin#deleteSnapshots(String) which deletes snapshots whose names match the parameter
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSnapshotDeletionWithRegex() throws Exception {
        Admin admin = TestSnapshotFromClient.UTIL.getAdmin();
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // put some stuff in the table
        Table table = TestSnapshotFromClient.UTIL.getConnection().getTable(TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.UTIL.loadTable(table, TestSnapshotFromClient.TEST_FAM);
        table.close();
        byte[] snapshot1 = Bytes.toBytes("TableSnapshot1");
        admin.snapshot(snapshot1, TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.LOG.debug("Snapshot1 completed.");
        byte[] snapshot2 = Bytes.toBytes("TableSnapshot2");
        admin.snapshot(snapshot2, TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.LOG.debug("Snapshot2 completed.");
        String snapshot3 = "3rdTableSnapshot";
        admin.snapshot(Bytes.toBytes(snapshot3), TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.LOG.debug((snapshot3 + " completed."));
        // delete the first two snapshots
        admin.deleteSnapshots(Pattern.compile("TableSnapshot.*"));
        List<SnapshotDescription> snapshots = admin.listSnapshots();
        Assert.assertEquals(1, snapshots.size());
        Assert.assertEquals(snapshot3, snapshots.get(0).getName());
        admin.deleteSnapshot(snapshot3);
        admin.close();
    }

    /**
     * Test snapshotting a table that is offline
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testOfflineTableSnapshot() throws Exception {
        Admin admin = TestSnapshotFromClient.UTIL.getAdmin();
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // put some stuff in the table
        Table table = TestSnapshotFromClient.UTIL.getConnection().getTable(TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.UTIL.loadTable(table, TestSnapshotFromClient.TEST_FAM, false);
        TestSnapshotFromClient.LOG.debug("FS state before disable:");
        FSUtils.logFileSystemState(TestSnapshotFromClient.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotFromClient.UTIL.getConfiguration()), TestSnapshotFromClient.LOG);
        // XXX if this is flakey, might want to consider using the async version and looping as
        // disableTable can succeed and still timeout.
        admin.disableTable(TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.LOG.debug("FS state before snapshot:");
        FSUtils.logFileSystemState(TestSnapshotFromClient.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotFromClient.UTIL.getConfiguration()), TestSnapshotFromClient.LOG);
        // take a snapshot of the disabled table
        final String SNAPSHOT_NAME = "offlineTableSnapshot";
        byte[] snapshot = Bytes.toBytes(SNAPSHOT_NAME);
        admin.snapshot(new SnapshotDescription(SNAPSHOT_NAME, TestSnapshotFromClient.TABLE_NAME, DISABLED, null, (-1), SnapshotManifestV1.DESCRIPTOR_VERSION));
        TestSnapshotFromClient.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot, TestSnapshotFromClient.TABLE_NAME);
        // make sure its a valid snapshot
        FileSystem fs = TestSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getFileSystem();
        Path rootDir = TestSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getRootDir();
        TestSnapshotFromClient.LOG.debug("FS state after snapshot:");
        FSUtils.logFileSystemState(TestSnapshotFromClient.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotFromClient.UTIL.getConfiguration()), TestSnapshotFromClient.LOG);
        SnapshotTestingUtils.confirmSnapshotValid(ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestSnapshotFromClient.TABLE_NAME, TestSnapshotFromClient.TEST_FAM, rootDir, admin, fs);
        admin.deleteSnapshot(snapshot);
        snapshots = admin.listSnapshots();
        SnapshotTestingUtils.assertNoSnapshots(admin);
    }

    @Test
    public void testSnapshotFailsOnNonExistantTable() throws Exception {
        Admin admin = TestSnapshotFromClient.UTIL.getAdmin();
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        String tableName = "_not_a_table";
        // make sure the table doesn't exist
        boolean fail = false;
        do {
            try {
                admin.getTableDescriptor(TableName.valueOf(tableName));
                fail = true;
                TestSnapshotFromClient.LOG.error((("Table:" + tableName) + " already exists, checking a new name"));
                tableName = tableName + "!";
            } catch (TableNotFoundException e) {
                fail = false;
            }
        } while (fail );
        // snapshot the non-existant table
        try {
            admin.snapshot("fail", TableName.valueOf(tableName));
            Assert.fail("Snapshot succeeded even though there is not table.");
        } catch (SnapshotCreationException e) {
            TestSnapshotFromClient.LOG.info(("Correctly failed to snapshot a non-existant table:" + (e.getMessage())));
        }
    }

    @Test
    public void testOfflineTableSnapshotWithEmptyRegions() throws Exception {
        // test with an empty table with one region
        Admin admin = TestSnapshotFromClient.UTIL.getAdmin();
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        TestSnapshotFromClient.LOG.debug("FS state before disable:");
        FSUtils.logFileSystemState(TestSnapshotFromClient.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotFromClient.UTIL.getConfiguration()), TestSnapshotFromClient.LOG);
        admin.disableTable(TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.LOG.debug("FS state before snapshot:");
        FSUtils.logFileSystemState(TestSnapshotFromClient.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotFromClient.UTIL.getConfiguration()), TestSnapshotFromClient.LOG);
        // take a snapshot of the disabled table
        byte[] snapshot = Bytes.toBytes("testOfflineTableSnapshotWithEmptyRegions");
        admin.snapshot(snapshot, TestSnapshotFromClient.TABLE_NAME);
        TestSnapshotFromClient.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot, TestSnapshotFromClient.TABLE_NAME);
        // make sure its a valid snapshot
        FileSystem fs = TestSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getFileSystem();
        Path rootDir = TestSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getRootDir();
        TestSnapshotFromClient.LOG.debug("FS state after snapshot:");
        FSUtils.logFileSystemState(TestSnapshotFromClient.UTIL.getTestFileSystem(), FSUtils.getRootDir(TestSnapshotFromClient.UTIL.getConfiguration()), TestSnapshotFromClient.LOG);
        List<byte[]> emptyCfs = Lists.newArrayList(TestSnapshotFromClient.TEST_FAM);// no file in the region

        List<byte[]> nonEmptyCfs = Lists.newArrayList();
        SnapshotTestingUtils.confirmSnapshotValid(ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestSnapshotFromClient.TABLE_NAME, nonEmptyCfs, emptyCfs, rootDir, admin, fs);
        admin.deleteSnapshot(snapshot);
        snapshots = admin.listSnapshots();
        SnapshotTestingUtils.assertNoSnapshots(admin);
    }

    @Test
    public void testListTableSnapshots() throws Exception {
        Admin admin = null;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try {
            admin = TestSnapshotFromClient.UTIL.getAdmin();
            HTableDescriptor htd = new HTableDescriptor(tableName);
            TestSnapshotFromClient.UTIL.createTable(htd, new byte[][]{ TestSnapshotFromClient.TEST_FAM }, TestSnapshotFromClient.UTIL.getConfiguration());
            String table1Snapshot1 = "Table1Snapshot1";
            admin.snapshot(table1Snapshot1, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot1 completed.");
            String table1Snapshot2 = "Table1Snapshot2";
            admin.snapshot(table1Snapshot2, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot2 completed.");
            String table2Snapshot1 = "Table2Snapshot1";
            admin.snapshot(Bytes.toBytes(table2Snapshot1), tableName);
            TestSnapshotFromClient.LOG.debug((table2Snapshot1 + " completed."));
            List<SnapshotDescription> listTableSnapshots = admin.listTableSnapshots(Pattern.compile("test.*"), TestSnapshotFromClient.MATCH_ALL);
            List<String> listTableSnapshotNames = new ArrayList<>();
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
                    admin.deleteSnapshots(Pattern.compile("Table.*"));
                } catch (SnapshotDoesNotExistException ignore) {
                }
                if (admin.tableExists(tableName)) {
                    TestSnapshotFromClient.UTIL.deleteTable(tableName);
                }
                admin.close();
            }
        }
    }

    @Test
    public void testListTableSnapshotsWithRegex() throws Exception {
        Admin admin = null;
        try {
            admin = TestSnapshotFromClient.UTIL.getAdmin();
            String table1Snapshot1 = "Table1Snapshot1";
            admin.snapshot(table1Snapshot1, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot1 completed.");
            String table1Snapshot2 = "Table1Snapshot2";
            admin.snapshot(table1Snapshot2, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot2 completed.");
            String table2Snapshot1 = "Table2Snapshot1";
            admin.snapshot(Bytes.toBytes(table2Snapshot1), TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug((table2Snapshot1 + " completed."));
            List<SnapshotDescription> listTableSnapshots = admin.listTableSnapshots(Pattern.compile("test.*"), Pattern.compile("Table1.*"));
            List<String> listTableSnapshotNames = new ArrayList<>();
            Assert.assertEquals(2, listTableSnapshots.size());
            for (SnapshotDescription s : listTableSnapshots) {
                listTableSnapshotNames.add(s.getName());
            }
            Assert.assertTrue(listTableSnapshotNames.contains(table1Snapshot1));
            Assert.assertTrue(listTableSnapshotNames.contains(table1Snapshot2));
            Assert.assertFalse(listTableSnapshotNames.contains(table2Snapshot1));
        } finally {
            if (admin != null) {
                try {
                    admin.deleteSnapshots(Pattern.compile("Table.*"));
                } catch (SnapshotDoesNotExistException ignore) {
                }
                admin.close();
            }
        }
    }

    @Test
    public void testDeleteTableSnapshots() throws Exception {
        Admin admin = null;
        final TableName tableName = TableName.valueOf(name.getMethodName());
        try {
            admin = TestSnapshotFromClient.UTIL.getAdmin();
            HTableDescriptor htd = new HTableDescriptor(tableName);
            TestSnapshotFromClient.UTIL.createTable(htd, new byte[][]{ TestSnapshotFromClient.TEST_FAM }, TestSnapshotFromClient.UTIL.getConfiguration());
            String table1Snapshot1 = "Table1Snapshot1";
            admin.snapshot(table1Snapshot1, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot1 completed.");
            String table1Snapshot2 = "Table1Snapshot2";
            admin.snapshot(table1Snapshot2, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot2 completed.");
            String table2Snapshot1 = "Table2Snapshot1";
            admin.snapshot(Bytes.toBytes(table2Snapshot1), tableName);
            TestSnapshotFromClient.LOG.debug((table2Snapshot1 + " completed."));
            Pattern tableNamePattern = Pattern.compile("test.*");
            admin.deleteTableSnapshots(tableNamePattern, TestSnapshotFromClient.MATCH_ALL);
            Assert.assertEquals(0, admin.listTableSnapshots(tableNamePattern, TestSnapshotFromClient.MATCH_ALL).size());
        } finally {
            if (admin != null) {
                if (admin.tableExists(tableName)) {
                    TestSnapshotFromClient.UTIL.deleteTable(tableName);
                }
                admin.close();
            }
        }
    }

    @Test
    public void testDeleteTableSnapshotsWithRegex() throws Exception {
        Admin admin = null;
        Pattern tableNamePattern = Pattern.compile("test.*");
        try {
            admin = TestSnapshotFromClient.UTIL.getAdmin();
            String table1Snapshot1 = "Table1Snapshot1";
            admin.snapshot(table1Snapshot1, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot1 completed.");
            String table1Snapshot2 = "Table1Snapshot2";
            admin.snapshot(table1Snapshot2, TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug("Snapshot2 completed.");
            String table2Snapshot1 = "Table2Snapshot1";
            admin.snapshot(Bytes.toBytes(table2Snapshot1), TestSnapshotFromClient.TABLE_NAME);
            TestSnapshotFromClient.LOG.debug((table2Snapshot1 + " completed."));
            admin.deleteTableSnapshots(tableNamePattern, Pattern.compile("Table1.*"));
            Assert.assertEquals(1, admin.listTableSnapshots(tableNamePattern, TestSnapshotFromClient.MATCH_ALL).size());
        } finally {
            if (admin != null) {
                try {
                    admin.deleteTableSnapshots(tableNamePattern, TestSnapshotFromClient.MATCH_ALL);
                } catch (SnapshotDoesNotExistException ignore) {
                }
                admin.close();
            }
        }
    }
}

