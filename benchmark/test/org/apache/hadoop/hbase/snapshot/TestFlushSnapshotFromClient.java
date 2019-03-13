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
package org.apache.hadoop.hbase.snapshot;


import SnapshotManager.ONLINE_SNAPSHOT_CONTROLLER_DESCRIPTION;
import SnapshotType.FLUSH;
import SnapshotType.SKIPFLUSH;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.TableNotFoundException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.SnapshotDescription;
import org.apache.hadoop.hbase.client.SnapshotType;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.shaded.protobuf.ProtobufUtil;
import org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test creating/using/deleting snapshots from the client
 * <p>
 * This is an end-to-end test for the snapshot utility
 *
 * TODO This is essentially a clone of TestSnapshotFromClient.  This is worth refactoring this
 * because there will be a few more flavors of snapshots that need to run these tests.
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestFlushSnapshotFromClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFlushSnapshotFromClient.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFlushSnapshotFromClient.class);

    protected static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    protected static final int NUM_RS = 2;

    protected static final byte[] TEST_FAM = Bytes.toBytes("fam");

    protected static final TableName TABLE_NAME = TableName.valueOf("test");

    protected final int DEFAULT_NUM_ROWS = 100;

    protected Admin admin = null;

    /**
     * Test simple flush snapshotting a table that is online
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFlushTableSnapshot() throws Exception {
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // put some stuff in the table
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, DEFAULT_NUM_ROWS, TestFlushSnapshotFromClient.TEST_FAM);
        TestFlushSnapshotFromClient.LOG.debug("FS state before snapshot:");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        // take a snapshot of the enabled table
        String snapshotString = "offlineTableSnapshot";
        byte[] snapshot = Bytes.toBytes(snapshotString);
        admin.snapshot(snapshotString, TestFlushSnapshotFromClient.TABLE_NAME, FLUSH);
        TestFlushSnapshotFromClient.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot, TestFlushSnapshotFromClient.TABLE_NAME);
        // make sure its a valid snapshot
        TestFlushSnapshotFromClient.LOG.debug("FS state after snapshot:");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        SnapshotTestingUtils.confirmSnapshotValid(TestFlushSnapshotFromClient.UTIL, ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestFlushSnapshotFromClient.TABLE_NAME, TestFlushSnapshotFromClient.TEST_FAM);
    }

    /**
     * Test snapshotting a table that is online without flushing
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSkipFlushTableSnapshot() throws Exception {
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // put some stuff in the table
        Table table = TestFlushSnapshotFromClient.UTIL.getConnection().getTable(TestFlushSnapshotFromClient.TABLE_NAME);
        TestFlushSnapshotFromClient.UTIL.loadTable(table, TestFlushSnapshotFromClient.TEST_FAM);
        TestFlushSnapshotFromClient.UTIL.flush(TestFlushSnapshotFromClient.TABLE_NAME);
        TestFlushSnapshotFromClient.LOG.debug("FS state before snapshot:");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        // take a snapshot of the enabled table
        String snapshotString = "skipFlushTableSnapshot";
        byte[] snapshot = Bytes.toBytes(snapshotString);
        admin.snapshot(snapshotString, TestFlushSnapshotFromClient.TABLE_NAME, SKIPFLUSH);
        TestFlushSnapshotFromClient.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot, TestFlushSnapshotFromClient.TABLE_NAME);
        // make sure its a valid snapshot
        TestFlushSnapshotFromClient.LOG.debug("FS state after snapshot:");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        SnapshotTestingUtils.confirmSnapshotValid(TestFlushSnapshotFromClient.UTIL, ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestFlushSnapshotFromClient.TABLE_NAME, TestFlushSnapshotFromClient.TEST_FAM);
        admin.deleteSnapshot(snapshot);
        snapshots = admin.listSnapshots();
        SnapshotTestingUtils.assertNoSnapshots(admin);
    }

    /**
     * Test simple flush snapshotting a table that is online
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFlushTableSnapshotWithProcedure() throws Exception {
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // put some stuff in the table
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, DEFAULT_NUM_ROWS, TestFlushSnapshotFromClient.TEST_FAM);
        TestFlushSnapshotFromClient.LOG.debug("FS state before snapshot:");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        // take a snapshot of the enabled table
        String snapshotString = "offlineTableSnapshot";
        byte[] snapshot = Bytes.toBytes(snapshotString);
        Map<String, String> props = new HashMap<>();
        props.put("table", TestFlushSnapshotFromClient.TABLE_NAME.getNameAsString());
        admin.execProcedure(ONLINE_SNAPSHOT_CONTROLLER_DESCRIPTION, snapshotString, props);
        TestFlushSnapshotFromClient.LOG.debug("Snapshot completed.");
        // make sure we have the snapshot
        List<SnapshotDescription> snapshots = SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot, TestFlushSnapshotFromClient.TABLE_NAME);
        // make sure its a valid snapshot
        TestFlushSnapshotFromClient.LOG.debug("FS state after snapshot:");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        SnapshotTestingUtils.confirmSnapshotValid(TestFlushSnapshotFromClient.UTIL, ProtobufUtil.createHBaseProtosSnapshotDesc(snapshots.get(0)), TestFlushSnapshotFromClient.TABLE_NAME, TestFlushSnapshotFromClient.TEST_FAM);
    }

    @Test
    public void testSnapshotFailsOnNonExistantTable() throws Exception {
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        TableName tableName = TableName.valueOf("_not_a_table");
        // make sure the table doesn't exist
        boolean fail = false;
        do {
            try {
                admin.getTableDescriptor(tableName);
                fail = true;
                TestFlushSnapshotFromClient.LOG.error((("Table:" + tableName) + " already exists, checking a new name"));
                tableName = TableName.valueOf((tableName + "!"));
            } catch (TableNotFoundException e) {
                fail = false;
            }
        } while (fail );
        // snapshot the non-existant table
        try {
            admin.snapshot("fail", tableName, FLUSH);
            Assert.fail("Snapshot succeeded even though there is not table.");
        } catch (SnapshotCreationException e) {
            TestFlushSnapshotFromClient.LOG.info(("Correctly failed to snapshot a non-existant table:" + (e.getMessage())));
        }
    }

    @Test
    public void testAsyncFlushSnapshot() throws Exception {
        SnapshotProtos.SnapshotDescription snapshot = SnapshotProtos.SnapshotDescription.newBuilder().setName("asyncSnapshot").setTable(TestFlushSnapshotFromClient.TABLE_NAME.getNameAsString()).setType(SnapshotProtos.SnapshotDescription.Type.FLUSH).build();
        // take the snapshot async
        admin.takeSnapshotAsync(new SnapshotDescription("asyncSnapshot", TestFlushSnapshotFromClient.TABLE_NAME, SnapshotType.FLUSH));
        // constantly loop, looking for the snapshot to complete
        HMaster master = TestFlushSnapshotFromClient.UTIL.getMiniHBaseCluster().getMaster();
        SnapshotTestingUtils.waitForSnapshotToComplete(master, snapshot, 200);
        TestFlushSnapshotFromClient.LOG.info(" === Async Snapshot Completed ===");
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        // make sure we get the snapshot
        SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshot);
    }

    @Test
    public void testSnapshotStateAfterMerge() throws Exception {
        int numRows = DEFAULT_NUM_ROWS;
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // load the table so we have some data
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, numRows, TestFlushSnapshotFromClient.TEST_FAM);
        // Take a snapshot
        String snapshotBeforeMergeName = "snapshotBeforeMerge";
        admin.snapshot(snapshotBeforeMergeName, TestFlushSnapshotFromClient.TABLE_NAME, FLUSH);
        // Clone the table
        TableName cloneBeforeMergeName = TableName.valueOf("cloneBeforeMerge");
        admin.cloneSnapshot(snapshotBeforeMergeName, cloneBeforeMergeName);
        SnapshotTestingUtils.waitForTableToBeOnline(TestFlushSnapshotFromClient.UTIL, cloneBeforeMergeName);
        // Merge two regions
        List<HRegionInfo> regions = admin.getTableRegions(TestFlushSnapshotFromClient.TABLE_NAME);
        Collections.sort(regions, new Comparator<HRegionInfo>() {
            @Override
            public int compare(HRegionInfo r1, HRegionInfo r2) {
                return Bytes.compareTo(r1.getStartKey(), r2.getStartKey());
            }
        });
        int numRegions = admin.getTableRegions(TestFlushSnapshotFromClient.TABLE_NAME).size();
        int numRegionsAfterMerge = numRegions - 2;
        admin.mergeRegionsAsync(regions.get(1).getEncodedNameAsBytes(), regions.get(2).getEncodedNameAsBytes(), true);
        admin.mergeRegionsAsync(regions.get(4).getEncodedNameAsBytes(), regions.get(5).getEncodedNameAsBytes(), true);
        // Verify that there's one region less
        waitRegionsAfterMerge(numRegionsAfterMerge);
        Assert.assertEquals(numRegionsAfterMerge, admin.getTableRegions(TestFlushSnapshotFromClient.TABLE_NAME).size());
        // Clone the table
        TableName cloneAfterMergeName = TableName.valueOf("cloneAfterMerge");
        admin.cloneSnapshot(snapshotBeforeMergeName, cloneAfterMergeName);
        SnapshotTestingUtils.waitForTableToBeOnline(TestFlushSnapshotFromClient.UTIL, cloneAfterMergeName);
        verifyRowCount(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, numRows);
        verifyRowCount(TestFlushSnapshotFromClient.UTIL, cloneBeforeMergeName, numRows);
        verifyRowCount(TestFlushSnapshotFromClient.UTIL, cloneAfterMergeName, numRows);
        // test that we can delete the snapshot
        TestFlushSnapshotFromClient.UTIL.deleteTable(cloneAfterMergeName);
        TestFlushSnapshotFromClient.UTIL.deleteTable(cloneBeforeMergeName);
    }

    @Test
    public void testTakeSnapshotAfterMerge() throws Exception {
        int numRows = DEFAULT_NUM_ROWS;
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // load the table so we have some data
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, numRows, TestFlushSnapshotFromClient.TEST_FAM);
        // Merge two regions
        List<HRegionInfo> regions = admin.getTableRegions(TestFlushSnapshotFromClient.TABLE_NAME);
        Collections.sort(regions, new Comparator<HRegionInfo>() {
            @Override
            public int compare(HRegionInfo r1, HRegionInfo r2) {
                return Bytes.compareTo(r1.getStartKey(), r2.getStartKey());
            }
        });
        int numRegions = admin.getTableRegions(TestFlushSnapshotFromClient.TABLE_NAME).size();
        int numRegionsAfterMerge = numRegions - 2;
        admin.mergeRegionsAsync(regions.get(1).getEncodedNameAsBytes(), regions.get(2).getEncodedNameAsBytes(), true);
        admin.mergeRegionsAsync(regions.get(4).getEncodedNameAsBytes(), regions.get(5).getEncodedNameAsBytes(), true);
        waitRegionsAfterMerge(numRegionsAfterMerge);
        Assert.assertEquals(numRegionsAfterMerge, admin.getTableRegions(TestFlushSnapshotFromClient.TABLE_NAME).size());
        // Take a snapshot
        String snapshotName = "snapshotAfterMerge";
        SnapshotTestingUtils.snapshot(admin, snapshotName, TestFlushSnapshotFromClient.TABLE_NAME, FLUSH, 3);
        // Clone the table
        TableName cloneName = TableName.valueOf("cloneMerge");
        admin.cloneSnapshot(snapshotName, cloneName);
        SnapshotTestingUtils.waitForTableToBeOnline(TestFlushSnapshotFromClient.UTIL, cloneName);
        verifyRowCount(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, numRows);
        verifyRowCount(TestFlushSnapshotFromClient.UTIL, cloneName, numRows);
        // test that we can delete the snapshot
        TestFlushSnapshotFromClient.UTIL.deleteTable(cloneName);
    }

    /**
     * Basic end-to-end test of simple-flush-based snapshots
     */
    @Test
    public void testFlushCreateListDestroy() throws Exception {
        TestFlushSnapshotFromClient.LOG.debug("------- Starting Snapshot test -------------");
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // load the table so we have some data
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, DEFAULT_NUM_ROWS, TestFlushSnapshotFromClient.TEST_FAM);
        String snapshotName = "flushSnapshotCreateListDestroy";
        FileSystem fs = TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getFileSystem();
        Path rootDir = TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().getRootDir();
        SnapshotTestingUtils.createSnapshotAndValidate(admin, TestFlushSnapshotFromClient.TABLE_NAME, Bytes.toString(TestFlushSnapshotFromClient.TEST_FAM), snapshotName, rootDir, fs, true);
    }

    /**
     * Demonstrate that we reject snapshot requests if there is a snapshot already running on the
     * same table currently running and that concurrent snapshots on different tables can both
     * succeed concurretly.
     */
    @Test
    public void testConcurrentSnapshottingAttempts() throws IOException, InterruptedException {
        final TableName TABLE2_NAME = TableName.valueOf(((TestFlushSnapshotFromClient.TABLE_NAME) + "2"));
        int ssNum = 20;
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // create second testing table
        SnapshotTestingUtils.createTable(TestFlushSnapshotFromClient.UTIL, TABLE2_NAME, TestFlushSnapshotFromClient.TEST_FAM);
        // load the table so we have some data
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TestFlushSnapshotFromClient.TABLE_NAME, DEFAULT_NUM_ROWS, TestFlushSnapshotFromClient.TEST_FAM);
        SnapshotTestingUtils.loadData(TestFlushSnapshotFromClient.UTIL, TABLE2_NAME, DEFAULT_NUM_ROWS, TestFlushSnapshotFromClient.TEST_FAM);
        final CountDownLatch toBeSubmitted = new CountDownLatch(ssNum);
        // We'll have one of these per thread
        class SSRunnable implements Runnable {
            SnapshotDescription ss;

            SSRunnable(SnapshotDescription ss) {
                this.ss = ss;
            }

            @Override
            public void run() {
                try {
                    TestFlushSnapshotFromClient.LOG.info(("Submitting snapshot request: " + (ClientSnapshotDescriptionUtils.toString(ProtobufUtil.createHBaseProtosSnapshotDesc(ss)))));
                    admin.takeSnapshotAsync(ss);
                } catch (Exception e) {
                    TestFlushSnapshotFromClient.LOG.info((("Exception during snapshot request: " + (ClientSnapshotDescriptionUtils.toString(ProtobufUtil.createHBaseProtosSnapshotDesc(ss)))) + ".  This is ok, we expect some"), e);
                }
                TestFlushSnapshotFromClient.LOG.info(("Submitted snapshot request: " + (ClientSnapshotDescriptionUtils.toString(ProtobufUtil.createHBaseProtosSnapshotDesc(ss)))));
                toBeSubmitted.countDown();
            }
        }
        // build descriptions
        SnapshotDescription[] descs = new SnapshotDescription[ssNum];
        for (int i = 0; i < ssNum; i++) {
            if ((i % 2) == 0) {
                descs[i] = new SnapshotDescription(("ss" + i), TestFlushSnapshotFromClient.TABLE_NAME, SnapshotType.FLUSH);
            } else {
                descs[i] = new SnapshotDescription(("ss" + i), TABLE2_NAME, SnapshotType.FLUSH);
            }
        }
        // kick each off its own thread
        for (int i = 0; i < ssNum; i++) {
            new Thread(new SSRunnable(descs[i])).start();
        }
        // wait until all have been submitted
        toBeSubmitted.await();
        // loop until all are done.
        while (true) {
            int doneCount = 0;
            for (SnapshotDescription ss : descs) {
                try {
                    if (admin.isSnapshotFinished(ss)) {
                        doneCount++;
                    }
                } catch (Exception e) {
                    TestFlushSnapshotFromClient.LOG.warn(("Got an exception when checking for snapshot " + (ss.getName())), e);
                    doneCount++;
                }
            }
            if (doneCount == (descs.length)) {
                break;
            }
            Thread.sleep(100);
        } 
        // dump for debugging
        TestFlushSnapshotFromClient.UTIL.getHBaseCluster().getMaster().getMasterFileSystem().logFileSystemState(TestFlushSnapshotFromClient.LOG);
        List<SnapshotDescription> taken = admin.listSnapshots();
        int takenSize = taken.size();
        TestFlushSnapshotFromClient.LOG.info(((("Taken " + takenSize) + " snapshots:  ") + taken));
        Assert.assertTrue(("We expect at least 1 request to be rejected because of we concurrently" + " issued many requests"), ((takenSize < ssNum) && (takenSize > 0)));
        // Verify that there's at least one snapshot per table
        int t1SnapshotsCount = 0;
        int t2SnapshotsCount = 0;
        for (SnapshotDescription ss : taken) {
            if (ss.getTableName().equals(TestFlushSnapshotFromClient.TABLE_NAME)) {
                t1SnapshotsCount++;
            } else
                if (ss.getTableName().equals(TABLE2_NAME)) {
                    t2SnapshotsCount++;
                }

        }
        Assert.assertTrue("We expect at least 1 snapshot of table1 ", (t1SnapshotsCount > 0));
        Assert.assertTrue("We expect at least 1 snapshot of table2 ", (t2SnapshotsCount > 0));
        TestFlushSnapshotFromClient.UTIL.deleteTable(TABLE2_NAME);
    }
}

