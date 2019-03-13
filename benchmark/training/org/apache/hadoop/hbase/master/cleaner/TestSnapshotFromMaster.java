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
package org.apache.hadoop.hbase.master.cleaner;


import IsSnapshotDoneRequest.Builder;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.SnapshotType;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.snapshot.DisabledTableSnapshotHandler;
import org.apache.hadoop.hbase.master.snapshot.SnapshotHFileCleaner;
import org.apache.hadoop.hbase.regionserver.CompactedHFilesDischarger;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.DeleteSnapshotRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.GetCompletedSnapshotsRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.GetCompletedSnapshotsResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.IsSnapshotDoneRequest;
import org.apache.hadoop.hbase.shaded.protobuf.generated.MasterProtos.IsSnapshotDoneResponse;
import org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos.SnapshotDescription;
import org.apache.hadoop.hbase.snapshot.SnapshotDescriptionUtils;
import org.apache.hadoop.hbase.snapshot.SnapshotReferenceUtil;
import org.apache.hadoop.hbase.snapshot.SnapshotTestingUtils;
import org.apache.hadoop.hbase.snapshot.UnknownSnapshotException;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.EnvironmentEdgeManager;
import org.apache.hadoop.hbase.util.FSUtils;
import org.apache.hadoop.hbase.util.JVMClusterUtil.RegionServerThread;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test the master-related aspects of a snapshot
 */
@Category({ MasterTests.class, MediumTests.class })
public class TestSnapshotFromMaster {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSnapshotFromMaster.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotFromMaster.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final int NUM_RS = 2;

    private static Path rootDir;

    private static FileSystem fs;

    private static HMaster master;

    // for hfile archiving test.
    private static Path archiveDir;

    private static final byte[] TEST_FAM = Bytes.toBytes("fam");

    private static final TableName TABLE_NAME = TableName.valueOf("test");

    // refresh the cache every 1/2 second
    private static final long cacheRefreshPeriod = 500;

    private static final int blockingStoreFiles = 12;

    /**
     * Test that the contract from the master for checking on a snapshot are valid.
     * <p>
     * <ol>
     * <li>If a snapshot fails with an error, we expect to get the source error.</li>
     * <li>If there is no snapshot name supplied, we should get an error.</li>
     * <li>If asking about a snapshot has hasn't occurred, you should get an error.</li>
     * </ol>
     */
    @Test
    public void testIsDoneContract() throws Exception {
        IsSnapshotDoneRequest.Builder builder = IsSnapshotDoneRequest.newBuilder();
        String snapshotName = "asyncExpectedFailureTest";
        // check that we get an exception when looking up snapshot where one hasn't happened
        SnapshotTestingUtils.expectSnapshotDoneException(TestSnapshotFromMaster.master, builder.build(), UnknownSnapshotException.class);
        // and that we get the same issue, even if we specify a name
        SnapshotDescription desc = SnapshotDescription.newBuilder().setName(snapshotName).setTable(TestSnapshotFromMaster.TABLE_NAME.getNameAsString()).build();
        builder.setSnapshot(desc);
        SnapshotTestingUtils.expectSnapshotDoneException(TestSnapshotFromMaster.master, builder.build(), UnknownSnapshotException.class);
        // set a mock handler to simulate a snapshot
        DisabledTableSnapshotHandler mockHandler = Mockito.mock(DisabledTableSnapshotHandler.class);
        Mockito.when(mockHandler.getException()).thenReturn(null);
        Mockito.when(mockHandler.getSnapshot()).thenReturn(desc);
        Mockito.when(mockHandler.isFinished()).thenReturn(Boolean.TRUE);
        Mockito.when(mockHandler.getCompletionTimestamp()).thenReturn(EnvironmentEdgeManager.currentTime());
        TestSnapshotFromMaster.master.getSnapshotManager().setSnapshotHandlerForTesting(TestSnapshotFromMaster.TABLE_NAME, mockHandler);
        // if we do a lookup without a snapshot name, we should fail - you should always know your name
        builder = IsSnapshotDoneRequest.newBuilder();
        SnapshotTestingUtils.expectSnapshotDoneException(TestSnapshotFromMaster.master, builder.build(), UnknownSnapshotException.class);
        // then do the lookup for the snapshot that it is done
        builder.setSnapshot(desc);
        IsSnapshotDoneResponse response = TestSnapshotFromMaster.master.getMasterRpcServices().isSnapshotDone(null, builder.build());
        Assert.assertTrue("Snapshot didn't complete when it should have.", response.getDone());
        // now try the case where we are looking for a snapshot we didn't take
        builder.setSnapshot(SnapshotDescription.newBuilder().setName("Not A Snapshot").build());
        SnapshotTestingUtils.expectSnapshotDoneException(TestSnapshotFromMaster.master, builder.build(), UnknownSnapshotException.class);
        // then create a snapshot to the fs and make sure that we can find it when checking done
        snapshotName = "completed";
        desc = createSnapshot(snapshotName);
        builder.setSnapshot(desc);
        response = TestSnapshotFromMaster.master.getMasterRpcServices().isSnapshotDone(null, builder.build());
        Assert.assertTrue("Completed, on-disk snapshot not found", response.getDone());
    }

    @Test
    public void testGetCompletedSnapshots() throws Exception {
        // first check when there are no snapshots
        GetCompletedSnapshotsRequest request = GetCompletedSnapshotsRequest.newBuilder().build();
        GetCompletedSnapshotsResponse response = TestSnapshotFromMaster.master.getMasterRpcServices().getCompletedSnapshots(null, request);
        Assert.assertEquals("Found unexpected number of snapshots", 0, response.getSnapshotsCount());
        // write one snapshot to the fs
        String snapshotName = "completed";
        SnapshotDescription snapshot = createSnapshot(snapshotName);
        // check that we get one snapshot
        response = TestSnapshotFromMaster.master.getMasterRpcServices().getCompletedSnapshots(null, request);
        Assert.assertEquals("Found unexpected number of snapshots", 1, response.getSnapshotsCount());
        List<SnapshotDescription> snapshots = response.getSnapshotsList();
        List<SnapshotDescription> expected = Lists.newArrayList(snapshot);
        Assert.assertEquals("Returned snapshots don't match created snapshots", expected, snapshots);
        // write a second snapshot
        snapshotName = "completed_two";
        snapshot = createSnapshot(snapshotName);
        expected.add(snapshot);
        // check that we get one snapshot
        response = TestSnapshotFromMaster.master.getMasterRpcServices().getCompletedSnapshots(null, request);
        Assert.assertEquals("Found unexpected number of snapshots", 2, response.getSnapshotsCount());
        snapshots = response.getSnapshotsList();
        Assert.assertEquals("Returned snapshots don't match created snapshots", expected, snapshots);
    }

    @Test
    public void testDeleteSnapshot() throws Exception {
        String snapshotName = "completed";
        SnapshotDescription snapshot = SnapshotDescription.newBuilder().setName(snapshotName).build();
        DeleteSnapshotRequest request = DeleteSnapshotRequest.newBuilder().setSnapshot(snapshot).build();
        try {
            TestSnapshotFromMaster.master.getMasterRpcServices().deleteSnapshot(null, request);
            Assert.fail("Master didn't throw exception when attempting to delete snapshot that doesn't exist");
        } catch (org.apache.hbase e) {
            // Expected
        }
        // write one snapshot to the fs
        createSnapshot(snapshotName);
        // then delete the existing snapshot,which shouldn't cause an exception to be thrown
        TestSnapshotFromMaster.master.getMasterRpcServices().deleteSnapshot(null, request);
    }

    /**
     * Test that the snapshot hfile archive cleaner works correctly. HFiles that are in snapshots
     * should be retained, while those that are not in a snapshot should be deleted.
     *
     * @throws Exception
     * 		on failure
     */
    @Test
    public void testSnapshotHFileArchiving() throws Exception {
        Admin admin = TestSnapshotFromMaster.UTIL.getAdmin();
        // make sure we don't fail on listing snapshots
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // recreate test table with disabled compactions; otherwise compaction may happen before
        // snapshot, the call after snapshot will be a no-op and checks will fail
        TestSnapshotFromMaster.UTIL.deleteTable(TestSnapshotFromMaster.TABLE_NAME);
        TableDescriptor td = TableDescriptorBuilder.newBuilder(TestSnapshotFromMaster.TABLE_NAME).setColumnFamily(ColumnFamilyDescriptorBuilder.of(TestSnapshotFromMaster.TEST_FAM)).setCompactionEnabled(false).build();
        TestSnapshotFromMaster.UTIL.getAdmin().createTable(td);
        // load the table
        for (int i = 0; i < ((TestSnapshotFromMaster.blockingStoreFiles) / 2); i++) {
            TestSnapshotFromMaster.UTIL.loadTable(TestSnapshotFromMaster.UTIL.getConnection().getTable(TestSnapshotFromMaster.TABLE_NAME), TestSnapshotFromMaster.TEST_FAM);
            TestSnapshotFromMaster.UTIL.flush(TestSnapshotFromMaster.TABLE_NAME);
        }
        // disable the table so we can take a snapshot
        admin.disableTable(TestSnapshotFromMaster.TABLE_NAME);
        // take a snapshot of the table
        String snapshotName = "snapshot";
        byte[] snapshotNameBytes = Bytes.toBytes(snapshotName);
        admin.snapshot(snapshotName, TestSnapshotFromMaster.TABLE_NAME);
        TestSnapshotFromMaster.LOG.info("After snapshot File-System state");
        FSUtils.logFileSystemState(TestSnapshotFromMaster.fs, TestSnapshotFromMaster.rootDir, TestSnapshotFromMaster.LOG);
        // ensure we only have one snapshot
        SnapshotTestingUtils.assertOneSnapshotThatMatches(admin, snapshotNameBytes, TestSnapshotFromMaster.TABLE_NAME);
        td = TableDescriptorBuilder.newBuilder(td).setCompactionEnabled(true).build();
        // enable compactions now
        admin.modifyTable(td);
        // renable the table so we can compact the regions
        admin.enableTable(TestSnapshotFromMaster.TABLE_NAME);
        // compact the files so we get some archived files for the table we just snapshotted
        List<HRegion> regions = TestSnapshotFromMaster.UTIL.getHBaseCluster().getRegions(TestSnapshotFromMaster.TABLE_NAME);
        for (HRegion region : regions) {
            region.waitForFlushesAndCompactions();// enable can trigger a compaction, wait for it.

            region.compactStores();// min is 2 so will compact and archive

        }
        List<RegionServerThread> regionServerThreads = TestSnapshotFromMaster.UTIL.getMiniHBaseCluster().getRegionServerThreads();
        HRegionServer hrs = null;
        for (RegionServerThread rs : regionServerThreads) {
            if (!(rs.getRegionServer().getRegions(TestSnapshotFromMaster.TABLE_NAME).isEmpty())) {
                hrs = rs.getRegionServer();
                break;
            }
        }
        CompactedHFilesDischarger cleaner = new CompactedHFilesDischarger(100, null, hrs, false);
        cleaner.chore();
        TestSnapshotFromMaster.LOG.info("After compaction File-System state");
        FSUtils.logFileSystemState(TestSnapshotFromMaster.fs, TestSnapshotFromMaster.rootDir, TestSnapshotFromMaster.LOG);
        // make sure the cleaner has run
        TestSnapshotFromMaster.LOG.debug("Running hfile cleaners");
        TestSnapshotFromMaster.ensureHFileCleanersRun();
        TestSnapshotFromMaster.LOG.info(("After cleaners File-System state: " + (TestSnapshotFromMaster.rootDir)));
        FSUtils.logFileSystemState(TestSnapshotFromMaster.fs, TestSnapshotFromMaster.rootDir, TestSnapshotFromMaster.LOG);
        // get the snapshot files for the table
        Path snapshotTable = SnapshotDescriptionUtils.getCompletedSnapshotDir(snapshotName, TestSnapshotFromMaster.rootDir);
        Set<String> snapshotHFiles = SnapshotReferenceUtil.getHFileNames(TestSnapshotFromMaster.UTIL.getConfiguration(), TestSnapshotFromMaster.fs, snapshotTable);
        // check that the files in the archive contain the ones that we need for the snapshot
        TestSnapshotFromMaster.LOG.debug("Have snapshot hfiles:");
        for (String fileName : snapshotHFiles) {
            TestSnapshotFromMaster.LOG.debug(fileName);
        }
        // get the archived files for the table
        Collection<String> archives = getHFiles(TestSnapshotFromMaster.archiveDir, TestSnapshotFromMaster.fs, TestSnapshotFromMaster.TABLE_NAME);
        // get the hfiles for the table
        Collection<String> hfiles = getHFiles(TestSnapshotFromMaster.rootDir, TestSnapshotFromMaster.fs, TestSnapshotFromMaster.TABLE_NAME);
        // and make sure that there is a proper subset
        for (String fileName : snapshotHFiles) {
            boolean exist = (archives.contains(fileName)) || (hfiles.contains(fileName));
            Assert.assertTrue(((((("Archived hfiles " + archives) + " and table hfiles ") + hfiles) + " is missing snapshot file:") + fileName), exist);
        }
        // delete the existing snapshot
        admin.deleteSnapshot(snapshotNameBytes);
        SnapshotTestingUtils.assertNoSnapshots(admin);
        // make sure that we don't keep around the hfiles that aren't in a snapshot
        // make sure we wait long enough to refresh the snapshot hfile
        List<BaseHFileCleanerDelegate> delegates = TestSnapshotFromMaster.UTIL.getMiniHBaseCluster().getMaster().getHFileCleaner().cleanersChain;
        for (BaseHFileCleanerDelegate delegate : delegates) {
            if (delegate instanceof SnapshotHFileCleaner) {
                getFileCacheForTesting().triggerCacheRefreshForTesting();
            }
        }
        // run the cleaner again
        TestSnapshotFromMaster.LOG.debug("Running hfile cleaners");
        TestSnapshotFromMaster.ensureHFileCleanersRun();
        TestSnapshotFromMaster.LOG.info("After delete snapshot cleaners run File-System state");
        FSUtils.logFileSystemState(TestSnapshotFromMaster.fs, TestSnapshotFromMaster.rootDir, TestSnapshotFromMaster.LOG);
        archives = getHFiles(TestSnapshotFromMaster.archiveDir, TestSnapshotFromMaster.fs, TestSnapshotFromMaster.TABLE_NAME);
        Assert.assertEquals("Still have some hfiles in the archive, when their snapshot has been deleted.", 0, archives.size());
    }

    @Test
    public void testAsyncSnapshotWillNotBlockSnapshotHFileCleaner() throws Exception {
        // Write some data
        Table table = TestSnapshotFromMaster.UTIL.getConnection().getTable(TestSnapshotFromMaster.TABLE_NAME);
        for (int i = 0; i < 10; i++) {
            Put put = new Put(Bytes.toBytes(i)).addColumn(TestSnapshotFromMaster.TEST_FAM, Bytes.toBytes("q"), Bytes.toBytes(i));
            table.put(put);
        }
        String snapshotName = "testAsyncSnapshotWillNotBlockSnapshotHFileCleaner01";
        Future<Void> future = TestSnapshotFromMaster.UTIL.getAdmin().snapshotAsync(new org.apache.hadoop.hbase.shaded.protobuf.generated.SnapshotProtos.org.apache.hadoop.hbase.client.SnapshotDescription(snapshotName, TestSnapshotFromMaster.TABLE_NAME, SnapshotType.FLUSH));
        Waiter.waitFor(TestSnapshotFromMaster.UTIL.getConfiguration(), (10 * 1000L), 200L, () -> (UTIL.getAdmin().listSnapshots(Pattern.compile(snapshotName)).size()) == 1);
        Assert.assertTrue(TestSnapshotFromMaster.master.getSnapshotManager().isTakingAnySnapshot());
        future.get();
        Assert.assertFalse(TestSnapshotFromMaster.master.getSnapshotManager().isTakingAnySnapshot());
    }
}

