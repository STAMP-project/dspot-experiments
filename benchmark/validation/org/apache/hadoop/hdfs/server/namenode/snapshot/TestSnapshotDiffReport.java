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
package org.apache.hadoop.hdfs.server.namenode.snapshot;


import DFSConfigKeys.DFS_NAMENODE_SNAPSHOT_DIFF_ALLOW_SNAP_ROOT_DESCENDANT;
import DFSConfigKeys.DFS_NAMENODE_SNAPSHOT_DIFF_ALLOW_SNAP_ROOT_DESCENDANT_DEFAULT;
import Rename.OVERWRITE;
import SnapshotDiffReportListing.DiffReportListingEntry;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.apache.commons.collections.list.TreeList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.impl.SnapshotDiffReportGenerator;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffReportEntry;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffType;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReportListing;
import org.apache.hadoop.hdfs.protocol.SnapshotException;
import org.apache.hadoop.hdfs.server.namenode.INodeDirectory;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests snapshot deletion.
 */
public class TestSnapshotDiffReport {
    private static final Logger LOG = LoggerFactory.getLogger(TestSnapshotDiffReport.class);

    private static final long SEED = 0;

    private static final short REPLICATION = 3;

    private static final short REPLICATION_1 = 2;

    private static final long BLOCKSIZE = 1024;

    private static final long BUFFERLEN = (TestSnapshotDiffReport.BLOCKSIZE) / 2;

    private static final long FILELEN = (TestSnapshotDiffReport.BLOCKSIZE) * 2;

    private final Path dir = new Path("/TestSnapshot");

    private final Path sub1 = new Path(dir, "sub1");

    protected Configuration conf;

    protected MiniDFSCluster cluster;

    protected DistributedFileSystem hdfs;

    private final HashMap<Path, Integer> snapshotNumberMap = new HashMap<Path, Integer>();

    /**
     * Test the computation and representation of diff between snapshots.
     */
    @Test(timeout = 60000)
    public void testDiffReport() throws Exception {
        cluster.getNamesystem().getSnapshotManager().setAllowNestedSnapshots(true);
        Path subsub1 = new Path(sub1, "subsub1");
        Path subsubsub1 = new Path(subsub1, "subsubsub1");
        hdfs.mkdirs(subsubsub1);
        modifyAndCreateSnapshot(sub1, new Path[]{ sub1, subsubsub1 });
        modifyAndCreateSnapshot(subsubsub1, new Path[]{ sub1, subsubsub1 });
        final String invalidName = "invalid";
        try {
            hdfs.getSnapshotDiffReport(sub1, invalidName, invalidName);
            Assert.fail(("Expect exception when providing invalid snapshot name " + "for diff report"));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(((("Cannot find the snapshot of directory " + (sub1)) + " with name ") + invalidName), e);
        }
        // diff between the same snapshot
        SnapshotDiffReport report = hdfs.getSnapshotDiffReport(sub1, "s0", "s0");
        TestSnapshotDiffReport.LOG.info(report.toString());
        Assert.assertEquals(0, report.getDiffList().size());
        report = hdfs.getSnapshotDiffReport(sub1, "", "");
        TestSnapshotDiffReport.LOG.info(report.toString());
        Assert.assertEquals(0, report.getDiffList().size());
        try {
            report = hdfs.getSnapshotDiffReport(subsubsub1, null, "s2");
            Assert.fail("Expect exception when providing null fromSnapshot ");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("null fromSnapshot", e);
        }
        report = hdfs.getSnapshotDiffReport(subsubsub1, "s0", "s2");
        TestSnapshotDiffReport.LOG.info(report.toString());
        Assert.assertEquals(0, report.getDiffList().size());
        // test path with scheme also works
        report = hdfs.getSnapshotDiffReport(hdfs.makeQualified(subsubsub1), "s0", "s2");
        TestSnapshotDiffReport.LOG.info(report.toString());
        Assert.assertEquals(0, report.getDiffList().size());
        verifyDiffReport(sub1, "s0", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("file15")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("file12")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("file11")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("file11")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("file13")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("link13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("link13")));
        verifyDiffReport(sub1, "s0", "s5", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("file15")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("file12")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("file10")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("file11")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("file11")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("file13")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("link13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("link13")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file10")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file11")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/link13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file15")));
        verifyDiffReport(sub1, "s2", "s5", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("file10")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file10")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file11")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/link13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file15")));
        verifyDiffReport(sub1, "s3", "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file15")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/subsubsub1/file12")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1/file10")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/subsubsub1/file11")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file11")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1/file13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/link13")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/subsubsub1/link13")));
    }

    @Test(timeout = 60000)
    public void testSnapRootDescendantDiffReport() throws Exception {
        Assume.assumeTrue(conf.getBoolean(DFS_NAMENODE_SNAPSHOT_DIFF_ALLOW_SNAP_ROOT_DESCENDANT, DFS_NAMENODE_SNAPSHOT_DIFF_ALLOW_SNAP_ROOT_DESCENDANT_DEFAULT));
        Path subSub = new Path(sub1, "subsub1");
        Path subSubSub = new Path(subSub, "subsubsub1");
        Path nonSnapDir = new Path(dir, "non_snap");
        hdfs.mkdirs(subSubSub);
        hdfs.mkdirs(nonSnapDir);
        modifyAndCreateSnapshot(sub1, new Path[]{ sub1 });
        modifyAndCreateSnapshot(subSub, new Path[]{ sub1 });
        modifyAndCreateSnapshot(subSubSub, new Path[]{ sub1 });
        try {
            hdfs.getSnapshotDiffReport(subSub, "s1", "s2");
            hdfs.getSnapshotDiffReport(subSubSub, "s1", "s2");
        } catch (IOException e) {
            Assert.fail(((("Unexpected exception when getting snapshot diff report " + subSub) + ": ") + e));
        }
        try {
            hdfs.getSnapshotDiffReport(nonSnapDir, "s1", "s2");
            Assert.fail((("Snapshot diff report on a non snapshot directory '" + (nonSnapDir.getName())) + "'should fail!"));
        } catch (SnapshotException e) {
            GenericTestUtils.assertExceptionContains("Directory is neither snapshottable nor under a snap root!", e);
        }
        final String invalidName = "invalid";
        try {
            hdfs.getSnapshotDiffReport(subSub, invalidName, invalidName);
            Assert.fail(("Expect exception when providing invalid snapshot name " + "for diff report"));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(((("Cannot find the snapshot of directory " + (sub1)) + " with name ") + invalidName), e);
        }
        // diff between the same snapshot
        SnapshotDiffReport report = hdfs.getSnapshotDiffReport(subSub, "s0", "s0");
        Assert.assertEquals(0, report.getDiffList().size());
        report = hdfs.getSnapshotDiffReport(subSub, "", "");
        Assert.assertEquals(0, report.getDiffList().size());
        report = hdfs.getSnapshotDiffReport(subSubSub, "s0", "s2");
        Assert.assertEquals(0, report.getDiffList().size());
        report = hdfs.getSnapshotDiffReport(hdfs.makeQualified(subSubSub), "s0", "s2");
        Assert.assertEquals(0, report.getDiffList().size());
        verifyDescendantDiffReports(sub1, subSub, subSubSub);
    }

    @Test
    public void testSnapRootDescendantDiffReportWithRename() throws Exception {
        Assume.assumeTrue(conf.getBoolean(DFS_NAMENODE_SNAPSHOT_DIFF_ALLOW_SNAP_ROOT_DESCENDANT, DFS_NAMENODE_SNAPSHOT_DIFF_ALLOW_SNAP_ROOT_DESCENDANT_DEFAULT));
        Path subSub = new Path(sub1, "subsub1");
        Path subSubSub = new Path(subSub, "subsubsub1");
        Path nonSnapDir = new Path(dir, "non_snap");
        hdfs.mkdirs(subSubSub);
        hdfs.mkdirs(nonSnapDir);
        hdfs.allowSnapshot(sub1);
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        Path file20 = new Path(subSubSub, "file20");
        DFSTestUtil.createFile(hdfs, file20, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION_1, TestSnapshotDiffReport.SEED);
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        // Case 1: Move a file away from a descendant dir, but within the snap root.
        // mv <snaproot>/<subsub>/<subsubsub>/file20 <snaproot>/<subsub>/file20
        hdfs.rename(file20, new Path(subSub, file20.getName()));
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        // The snapshot diff for the snap root detects the change as file rename
        // as the file move happened within the snap root.
        verifyDiffReport(sub1, "s1", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("subsub1/subsubsub1/file20"), DFSUtil.string2Bytes("subsub1/file20")));
        // The snapshot diff for the descendant dir <subsub> still detects the
        // change as file rename as the file move happened under the snap root
        // descendant dir.
        verifyDiffReport(subSub, "s1", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsubsub1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("subsubsub1/file20"), DFSUtil.string2Bytes("file20")));
        // The snapshot diff for the descendant dir <subsubsub> detects the
        // change as file delete as the file got moved from its scope.
        verifyDiffReport(subSubSub, "s1", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("file20")));
        // Case 2: Move the file from the snap root descendant dir to any
        // non snap root dir. mv <snaproot>/<subsub>/file20 <nonsnaproot>/file20.
        hdfs.rename(new Path(subSub, file20.getName()), new Path(dir, file20.getName()));
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        // The snapshot diff for the snap root detects the change as file delete
        // as the file got moved away from the snap root dir to some non snap
        // root dir.
        verifyDiffReport(sub1, "s2", "s3", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/file20")));
        // The snapshot diff for the snap root descendant <subsub> detects the
        // change as file delete as the file was previously under its scope and
        // got moved away from its scope.
        verifyDiffReport(subSub, "s2", "s3", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("file20")));
        // The file was already not under the descendant dir <subsubsub> scope.
        // So, the snapshot diff report for the descendant dir doesn't
        // show the file rename at all.
        verifyDiffReport(subSubSub, "s2", "s3", new DiffReportEntry[]{  });
        // Case 3: Move the file from the non-snap root dir to snap root dir
        // mv <nonsnaproot>/file20 <snaproot>/file20
        hdfs.rename(new Path(dir, file20.getName()), new Path(sub1, file20.getName()));
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        // Snap root directory should show the file moved in as a new file.
        verifyDiffReport(sub1, "s3", "s4", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("file20")));
        // Snap descendant directories don't have visibility to the moved in file.
        verifyDiffReport(subSub, "s3", "s4", new DiffReportEntry[]{  });
        verifyDiffReport(subSubSub, "s3", "s4", new DiffReportEntry[]{  });
        hdfs.rename(new Path(sub1, file20.getName()), new Path(subSub, file20.getName()));
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        // Snap root directory now shows the rename as both source and
        // destination paths are under the snap root.
        verifyDiffReport(sub1, "s4", "s5", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("file20"), DFSUtil.string2Bytes("subsub1/file20")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1")));
        // For the descendant directory under the snap root, the file
        // moved in shows up as a new file created.
        verifyDiffReport(subSub, "s4", "s5", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("file20")));
        verifyDiffReport(subSubSub, "s4", "s5", new DiffReportEntry[]{  });
        // Case 4: Snapshot diff for the newly created descendant directory.
        Path subSubSub2 = new Path(subSub, "subsubsub2");
        hdfs.mkdirs(subSubSub2);
        Path file30 = new Path(subSubSub2, "file30");
        DFSTestUtil.createFile(hdfs, file30, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION_1, TestSnapshotDiffReport.SEED);
        hdfs.createFile(file30);
        hdfs.createSnapshot(sub1, genSnapshotName(sub1));
        verifyDiffReport(sub1, "s5", "s6", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub2")));
        verifyDiffReport(subSubSub2, "s5", "s6", new DiffReportEntry[]{  });
        verifyDiffReport(subSubSub2, "s1", "s2", new DiffReportEntry[]{  });
    }

    @Test
    public void testSnapshotDiffInfo() throws Exception {
        Path snapshotRootDirPath = dir;
        Path snapshotDirDescendantPath = new Path(snapshotRootDirPath, "desc");
        Path snapshotDirNonDescendantPath = new Path("/dummy/non/snap/desc");
        hdfs.mkdirs(snapshotDirDescendantPath);
        hdfs.mkdirs(snapshotDirNonDescendantPath);
        hdfs.allowSnapshot(snapshotRootDirPath);
        hdfs.createSnapshot(snapshotRootDirPath, "s0");
        hdfs.createSnapshot(snapshotRootDirPath, "s1");
        INodeDirectory snapshotRootDir = cluster.getNameNode().getNamesystem().getFSDirectory().getINode(snapshotRootDirPath.toUri().getPath()).asDirectory();
        INodeDirectory snapshotRootDescendantDir = cluster.getNameNode().getNamesystem().getFSDirectory().getINode(snapshotDirDescendantPath.toUri().getPath()).asDirectory();
        INodeDirectory snapshotRootNonDescendantDir = cluster.getNameNode().getNamesystem().getFSDirectory().getINode(snapshotDirNonDescendantPath.toUri().getPath()).asDirectory();
        try {
            SnapshotDiffInfo sdi = new SnapshotDiffInfo(snapshotRootDir, snapshotRootDescendantDir, new Snapshot(0, "s0", snapshotRootDescendantDir), new Snapshot(0, "s1", snapshotRootDescendantDir));
            TestSnapshotDiffReport.LOG.info(((("SnapshotDiffInfo: " + (sdi.getFrom())) + " - ") + (sdi.getTo())));
        } catch (IllegalArgumentException iae) {
            Assert.fail(("Unexpected exception when constructing SnapshotDiffInfo: " + iae));
        }
        try {
            SnapshotDiffInfo sdi = new SnapshotDiffInfo(snapshotRootDir, snapshotRootNonDescendantDir, new Snapshot(0, "s0", snapshotRootNonDescendantDir), new Snapshot(0, "s1", snapshotRootNonDescendantDir));
            TestSnapshotDiffReport.LOG.info(((("SnapshotDiffInfo: " + (sdi.getFrom())) + " - ") + (sdi.getTo())));
            Assert.fail(("SnapshotDiffInfo construction should fail for non snapshot root " + "or non snapshot root descendant directories!"));
        } catch (IllegalArgumentException iae) {
            // expected exception
        }
    }

    /**
     * Make changes under a sub-directory, then delete the sub-directory. Make
     * sure the diff report computation correctly retrieve the diff from the
     * deleted sub-directory.
     */
    @Test(timeout = 60000)
    public void testDiffReport2() throws Exception {
        Path subsub1 = new Path(sub1, "subsub1");
        Path subsubsub1 = new Path(subsub1, "subsubsub1");
        hdfs.mkdirs(subsubsub1);
        modifyAndCreateSnapshot(subsubsub1, new Path[]{ sub1 });
        // delete subsub1
        hdfs.delete(subsub1, true);
        // check diff report between s0 and s2
        verifyDiffReport(sub1, "s0", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file15")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/subsubsub1/file12")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/subsubsub1/file11")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/file11")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("subsub1/subsubsub1/file13")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("subsub1/subsubsub1/link13")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1/subsubsub1/link13")));
        // check diff report between s0 and the current status
        verifyDiffReport(sub1, "s0", "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("subsub1")));
    }

    /**
     * Rename a directory to its prior descendant, and verify the diff report.
     */
    @Test
    public void testDiffReportWithRename() throws Exception {
        final Path root = new Path("/");
        final Path sdir1 = new Path(root, "dir1");
        final Path sdir2 = new Path(root, "dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        hdfs.mkdirs(bar);
        hdfs.mkdirs(sdir2);
        // create snapshot on root
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        // /dir1/foo/bar -> /dir2/bar
        final Path bar2 = new Path(sdir2, "bar");
        hdfs.rename(bar, bar2);
        // /dir1/foo -> /dir2/bar/foo
        final Path foo2 = new Path(bar2, "foo");
        hdfs.rename(foo, foo2);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s2");
        // let's delete /dir2 to make things more complicated
        hdfs.delete(sdir2, true);
        verifyDiffReport(root, "s1", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/foo"), DFSUtil.string2Bytes("dir2/bar/foo")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir2")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1/foo/bar")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1/foo")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/foo/bar"), DFSUtil.string2Bytes("dir2/bar")));
    }

    /**
     * Rename a file/dir outside of the snapshottable dir should be reported as
     * deleted. Rename a file/dir from outside should be reported as created.
     */
    @Test
    public void testDiffReportWithRenameOutside() throws Exception {
        final Path root = new Path("/");
        final Path dir1 = new Path(root, "dir1");
        final Path dir2 = new Path(root, "dir2");
        final Path foo = new Path(dir1, "foo");
        final Path fileInFoo = new Path(foo, "file");
        final Path bar = new Path(dir2, "bar");
        final Path fileInBar = new Path(bar, "file");
        DFSTestUtil.createFile(hdfs, fileInFoo, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        DFSTestUtil.createFile(hdfs, fileInBar, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        // create snapshot on /dir1
        SnapshotTestHelper.createSnapshot(hdfs, dir1, "s0");
        // move bar into dir1
        final Path newBar = new Path(dir1, "newBar");
        hdfs.rename(bar, newBar);
        // move foo out of dir1 into dir2
        final Path newFoo = new Path(dir2, "new");
        hdfs.rename(foo, newFoo);
        SnapshotTestHelper.createSnapshot(hdfs, dir1, "s1");
        verifyDiffReport(dir1, "s0", "s1", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes(newBar.getName())), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes(foo.getName())));
    }

    /**
     * Renaming a file/dir then delete the ancestor dir of the rename target
     * should be reported as deleted.
     */
    @Test
    public void testDiffReportWithRenameAndDelete() throws Exception {
        final Path root = new Path("/");
        final Path dir1 = new Path(root, "dir1");
        final Path dir2 = new Path(root, "dir2");
        final Path foo = new Path(dir1, "foo");
        final Path fileInFoo = new Path(foo, "file");
        final Path bar = new Path(dir2, "bar");
        final Path fileInBar = new Path(bar, "file");
        DFSTestUtil.createFile(hdfs, fileInFoo, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        DFSTestUtil.createFile(hdfs, fileInBar, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        hdfs.rename(fileInFoo, fileInBar, OVERWRITE);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        verifyDiffReport(root, "s0", "s1", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1/foo")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir2/bar")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir2/bar/file")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/foo/file"), DFSUtil.string2Bytes("dir2/bar/file")));
        // delete bar
        hdfs.delete(bar, true);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s2");
        verifyDiffReport(root, "s0", "s2", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1/foo")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir2")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir2/bar")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir1/foo/file")));
    }

    @Test
    public void testDiffReportWithRenameToNewDir() throws Exception {
        final Path root = new Path("/");
        final Path foo = new Path(root, "foo");
        final Path fileInFoo = new Path(foo, "file");
        DFSTestUtil.createFile(hdfs, fileInFoo, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        final Path bar = new Path(root, "bar");
        hdfs.mkdirs(bar);
        final Path fileInBar = new Path(bar, "file");
        hdfs.rename(fileInFoo, fileInBar);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        verifyDiffReport(root, "s0", "s1", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("foo")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("bar")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("foo/file"), DFSUtil.string2Bytes("bar/file")));
    }

    /**
     * Rename a file and then append some data to it
     */
    @Test
    public void testDiffReportWithRenameAndAppend() throws Exception {
        final Path root = new Path("/");
        final Path foo = new Path(root, "foo");
        DFSTestUtil.createFile(hdfs, foo, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        final Path bar = new Path(root, "bar");
        hdfs.rename(foo, bar);
        DFSTestUtil.appendFile(hdfs, bar, 10);// append 10 bytes

        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        // we always put modification on the file before rename
        verifyDiffReport(root, "s0", "s1", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("foo")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("foo"), DFSUtil.string2Bytes("bar")));
    }

    /**
     * Nested renamed dir/file and the withNameList in the WithCount node of the
     * parental directory is empty due to snapshot deletion. See HDFS-6996 for
     * details.
     */
    @Test
    public void testDiffReportWithRenameAndSnapshotDeletion() throws Exception {
        final Path root = new Path("/");
        final Path foo = new Path(root, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(hdfs, bar, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        // rename /foo to /foo2
        final Path foo2 = new Path(root, "foo2");
        hdfs.rename(foo, foo2);
        // now /foo/bar becomes /foo2/bar
        final Path bar2 = new Path(foo2, "bar");
        // delete snapshot s0 so that the withNameList inside of the WithCount node
        // of foo becomes empty
        hdfs.deleteSnapshot(root, "s0");
        // create snapshot s1 and rename bar again
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        final Path bar3 = new Path(foo2, "bar-new");
        hdfs.rename(bar2, bar3);
        // we always put modification on the file before rename
        verifyDiffReport(root, "s1", "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("foo2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("foo2/bar"), DFSUtil.string2Bytes("foo2/bar-new")));
    }

    /**
     * Test Snapshot diff report for snapshots with open files captures in them.
     * Also verify if the diff report remains the same across NameNode restarts.
     */
    @Test(timeout = 120000)
    public void testDiffReportWithOpenFiles() throws Exception {
        // Construct the directory tree
        final Path level0A = new Path("/level_0_A");
        final Path flumeSnapRootDir = level0A;
        final String flumeFileName = "flume.log";
        final String flumeSnap1Name = "flume_snap_1";
        final String flumeSnap2Name = "flume_snap_2";
        // Create files and open a stream
        final Path flumeFile = new Path(level0A, flumeFileName);
        createFile(flumeFile);
        FSDataOutputStream flumeOutputStream = hdfs.append(flumeFile);
        // Create Snapshot S1
        final Path flumeS1Dir = SnapshotTestHelper.createSnapshot(hdfs, flumeSnapRootDir, flumeSnap1Name);
        final Path flumeS1Path = new Path(flumeS1Dir, flumeFileName);
        final long flumeFileLengthAfterS1 = hdfs.getFileStatus(flumeFile).getLen();
        // Verify if Snap S1 file length is same as the the live one
        Assert.assertEquals(flumeFileLengthAfterS1, hdfs.getFileStatus(flumeS1Path).getLen());
        verifyDiffReport(level0A, flumeSnap1Name, "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")));
        long flumeFileWrittenDataLength = flumeFileLengthAfterS1;
        int newWriteLength = ((int) ((TestSnapshotDiffReport.BLOCKSIZE) * 1.5));
        byte[] buf = new byte[newWriteLength];
        Random random = new Random();
        random.nextBytes(buf);
        // Write more data to flume file
        flumeFileWrittenDataLength += writeToStream(flumeOutputStream, buf);
        // Create Snapshot S2
        final Path flumeS2Dir = SnapshotTestHelper.createSnapshot(hdfs, flumeSnapRootDir, flumeSnap2Name);
        final Path flumeS2Path = new Path(flumeS2Dir, flumeFileName);
        // Verify live files length is same as all data written till now
        final long flumeFileLengthAfterS2 = hdfs.getFileStatus(flumeFile).getLen();
        Assert.assertEquals(flumeFileWrittenDataLength, flumeFileLengthAfterS2);
        // Verify if Snap S2 file length is same as the live one
        Assert.assertEquals(flumeFileLengthAfterS2, hdfs.getFileStatus(flumeS2Path).getLen());
        verifyDiffReport(level0A, flumeSnap1Name, "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes(flumeFileName)));
        verifyDiffReport(level0A, flumeSnap2Name, "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")));
        verifyDiffReport(level0A, flumeSnap1Name, flumeSnap2Name, new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes(flumeFileName)));
        // Write more data to flume file
        flumeFileWrittenDataLength += writeToStream(flumeOutputStream, buf);
        // Verify old flume snapshots have point-in-time / frozen file lengths
        // even after the live file have moved forward.
        Assert.assertEquals(flumeFileLengthAfterS1, hdfs.getFileStatus(flumeS1Path).getLen());
        Assert.assertEquals(flumeFileLengthAfterS2, hdfs.getFileStatus(flumeS2Path).getLen());
        flumeOutputStream.close();
        // Verify if Snap S2 file length is same as the live one
        Assert.assertEquals(flumeFileWrittenDataLength, hdfs.getFileStatus(flumeFile).getLen());
        // Verify old flume snapshots have point-in-time / frozen file lengths
        // even after the live file have moved forward.
        Assert.assertEquals(flumeFileLengthAfterS1, hdfs.getFileStatus(flumeS1Path).getLen());
        Assert.assertEquals(flumeFileLengthAfterS2, hdfs.getFileStatus(flumeS2Path).getLen());
        verifyDiffReport(level0A, flumeSnap1Name, "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes(flumeFileName)));
        verifyDiffReport(level0A, flumeSnap2Name, "", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes(flumeFileName)));
        verifyDiffReport(level0A, flumeSnap1Name, flumeSnap2Name, new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes(flumeFileName)));
        restartNameNode();
        verifyDiffReport(level0A, flumeSnap1Name, flumeSnap2Name, new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes(flumeFileName)));
    }

    /**
     * Check to see access time is not captured in snapshot when applicable.
     * When DFS_NAMENODE_SNAPSHOT_SKIP_CAPTURE_ACCESSTIME_ONLY_CHANGE
     * is set to true, and if a file's access time changed between two
     * snapshots but has no other modification, then the access time is not
     * captured in snapshot.
     */
    @Test
    public void testDontCaptureAccessTimeOnlyChangeReport() throws Exception {
        final Path froot = new Path("/");
        final Path root = new Path(froot, "/testSdiffCalc");
        // items created pre enabling snapshot
        final Path filePreSS = new Path(root, "fParent/filePreSS");
        final Path dirPreSS = new Path(root, "dirPreSS");
        final Path dirPreSSChild = new Path(dirPreSS, "dirPreSSChild");
        // items created after enabling snapshot
        final Path filePostSS = new Path(root, "fParent/filePostSS");
        final Path dirPostSS = new Path(root, "dirPostSS");
        final Path dirPostSSChild = new Path(dirPostSS, "dirPostSSChild");
        DFSTestUtil.createFile(hdfs, filePreSS, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        DFSTestUtil.createFile(hdfs, dirPreSSChild, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        printAtime(filePreSS, root, "s0");
        printAtime(dirPreSS, root, "s0");
        // items created after creating the first snapshot
        DFSTestUtil.createFile(hdfs, filePostSS, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        DFSTestUtil.createFile(hdfs, dirPostSSChild, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
        Thread.sleep(3000);
        long now = Time.now();
        hdfs.setTimes(filePreSS, (-1), now);
        hdfs.setTimes(filePostSS, (-1), now);
        hdfs.setTimes(dirPreSS, (-1), now);
        hdfs.setTimes(dirPostSS, (-1), now);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        printAtime(filePreSS, root, "s1");
        printAtime(dirPreSS, root, "s1");
        printAtime(filePostSS, root, "s1");
        printAtime(dirPostSS, root, "s1");
        Thread.sleep(3000);
        now = Time.now();
        hdfs.setTimes(filePreSS, (-1), now);
        hdfs.setTimes(filePostSS, (-1), now);
        hdfs.setTimes(dirPreSS, (-1), now);
        hdfs.setTimes(dirPostSS, (-1), now);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s2");
        printAtime(filePreSS, root, "s2");
        printAtime(dirPreSS, root, "s2");
        printAtime(filePostSS, root, "s2");
        printAtime(dirPostSS, root, "s2");
        Thread.sleep(3000);
        now = Time.now();
        // modify filePostSS, and change access time
        hdfs.setReplication(filePostSS, ((short) ((TestSnapshotDiffReport.REPLICATION) - 1)));
        hdfs.setTimes(filePostSS, (-1), now);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s3");
        TestSnapshotDiffReport.LOG.info("\nsnapshotDiff s0 -> s1:");
        TestSnapshotDiffReport.LOG.info(hdfs.getSnapshotDiffReport(root, "s0", "s1").toString());
        TestSnapshotDiffReport.LOG.info("\nsnapshotDiff s1 -> s2:");
        TestSnapshotDiffReport.LOG.info(hdfs.getSnapshotDiffReport(root, "s1", "s2").toString());
        assertAtimeEquals(filePreSS, root, "s0", "s1");
        assertAtimeEquals(dirPreSS, root, "s0", "s1");
        assertAtimeEquals(filePreSS, root, "s1", "s2");
        assertAtimeEquals(dirPreSS, root, "s1", "s2");
        assertAtimeEquals(filePostSS, root, "s1", "s2");
        assertAtimeEquals(dirPostSS, root, "s1", "s2");
        // access time should be captured in snapshot due to
        // other modification
        assertAtimeNotEquals(filePostSS, root, "s2", "s3");
        // restart NN, and see the access time relationship
        // still stands (no change caused by edit logs
        // loading)
        cluster.restartNameNodes();
        cluster.waitActive();
        assertAtimeEquals(filePreSS, root, "s0", "s1");
        assertAtimeEquals(dirPreSS, root, "s0", "s1");
        assertAtimeEquals(filePreSS, root, "s1", "s2");
        assertAtimeEquals(dirPreSS, root, "s1", "s2");
        assertAtimeEquals(filePostSS, root, "s1", "s2");
        assertAtimeEquals(dirPostSS, root, "s1", "s2");
        assertAtimeNotEquals(filePostSS, root, "s2", "s3");
    }

    /**
     * Tests to verfy the diff report with maximum SnapsdiffReportEntries limit
     * over an rpc being set to 3.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDiffReportWithRpcLimit() throws Exception {
        final Path root = new Path("/");
        hdfs.mkdirs(root);
        for (int i = 1; i < 4; i++) {
            final Path path = new Path(root, ("dir" + i));
            hdfs.mkdirs(path);
        }
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        for (int i = 1; i < 4; i++) {
            final Path path = new Path(root, ("dir" + i));
            for (int j = 1; j < 4; j++) {
                final Path file = new Path(path, ("file" + j));
                DFSTestUtil.createFile(hdfs, file, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
            }
        }
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        verifyDiffReport(root, "s0", "s1", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir1/file1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir1/file2")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir1/file3")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir2")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir2/file1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir2/file2")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir2/file3")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir3")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir3/file2")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir3/file3")));
    }

    @Test
    public void testDiffReportWithRpcLimit2() throws Exception {
        final Path root = new Path("/");
        hdfs.mkdirs(root);
        for (int i = 1; i <= 3; i++) {
            final Path path = new Path(root, ("dir" + i));
            hdfs.mkdirs(path);
        }
        for (int i = 1; i <= 3; i++) {
            final Path path = new Path(root, ("dir" + i));
            for (int j = 1; j < 4; j++) {
                final Path file = new Path(path, ("file" + j));
                DFSTestUtil.createFile(hdfs, file, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
            }
        }
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        Path targetDir = new Path(root, "dir4");
        // create directory dir4
        hdfs.mkdirs(targetDir);
        // moves files from dir1 to dir4
        Path path = new Path(root, "dir1");
        for (int j = 1; j < 4; j++) {
            final Path srcPath = new Path(path, ("file" + j));
            final Path targetPath = new Path(targetDir, ("file" + j));
            hdfs.rename(srcPath, targetPath);
        }
        targetDir = new Path(root, "dir3");
        // overwrite existing files in dir3 from files in dir1
        path = new Path(root, "dir2");
        for (int j = 1; j < 4; j++) {
            final Path srcPath = new Path(path, ("file" + j));
            final Path targetPath = new Path(targetDir, ("file" + j));
            hdfs.rename(srcPath, targetPath, OVERWRITE);
        }
        final Path pathToRename = new Path(root, "dir2");
        // move dir2 inside dir3
        hdfs.rename(pathToRename, targetDir);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        verifyDiffReport(root, "s0", "s1", new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir4")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2"), DFSUtil.string2Bytes("dir3/dir2")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/file1"), DFSUtil.string2Bytes("dir4/file1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/file2"), DFSUtil.string2Bytes("dir4/file2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/file3"), DFSUtil.string2Bytes("dir4/file3")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2/file1"), DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2/file2"), DFSUtil.string2Bytes("dir3/file2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2/file3"), DFSUtil.string2Bytes("dir3/file3")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir3")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir3/file3")));
    }

    @Test
    public void testSnapshotDiffReportRemoteIterator() throws Exception {
        final Path root = new Path("/");
        hdfs.mkdirs(root);
        for (int i = 1; i <= 3; i++) {
            final Path path = new Path(root, ("dir" + i));
            hdfs.mkdirs(path);
        }
        for (int i = 1; i <= 3; i++) {
            final Path path = new Path(root, ("dir" + i));
            for (int j = 1; j < 4; j++) {
                final Path file = new Path(path, ("file" + j));
                DFSTestUtil.createFile(hdfs, file, TestSnapshotDiffReport.BLOCKSIZE, TestSnapshotDiffReport.REPLICATION, TestSnapshotDiffReport.SEED);
            }
        }
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        Path targetDir = new Path(root, "dir4");
        // create directory dir4
        hdfs.mkdirs(targetDir);
        // moves files from dir1 to dir4
        Path path = new Path(root, "dir1");
        for (int j = 1; j < 4; j++) {
            final Path srcPath = new Path(path, ("file" + j));
            final Path targetPath = new Path(targetDir, ("file" + j));
            hdfs.rename(srcPath, targetPath);
        }
        targetDir = new Path(root, "dir3");
        // overwrite existing files in dir3 from files in dir1
        path = new Path(root, "dir2");
        for (int j = 1; j < 4; j++) {
            final Path srcPath = new Path(path, ("file" + j));
            final Path targetPath = new Path(targetDir, ("file" + j));
            hdfs.rename(srcPath, targetPath, OVERWRITE);
        }
        final Path pathToRename = new Path(root, "dir2");
        // move dir2 inside dir3
        hdfs.rename(pathToRename, targetDir);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s1");
        RemoteIterator<SnapshotDiffReportListing> iterator = hdfs.snapshotDiffReportListingRemoteIterator(root, "s0", "s1");
        SnapshotDiffReportGenerator snapshotDiffReport;
        List<SnapshotDiffReportListing.DiffReportListingEntry> modifiedList = new TreeList();
        List<SnapshotDiffReportListing.DiffReportListingEntry> createdList = new org.apache.hadoop.util.ChunkedArrayList();
        List<SnapshotDiffReportListing.DiffReportListingEntry> deletedList = new org.apache.hadoop.util.ChunkedArrayList();
        SnapshotDiffReportListing report = null;
        List<SnapshotDiffReportListing> reportList = new ArrayList<>();
        while (iterator.hasNext()) {
            report = iterator.next();
            reportList.add(report);
            modifiedList.addAll(report.getModifyList());
            createdList.addAll(report.getCreateList());
            deletedList.addAll(report.getDeleteList());
        } 
        try {
            iterator.next();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("No more entry in SnapshotDiffReport for /"));
        }
        Assert.assertNotEquals(0, reportList.size());
        // generate the snapshotDiffReport and Verify
        snapshotDiffReport = new SnapshotDiffReportGenerator("/", "s0", "s1", report.getIsFromEarlier(), modifiedList, createdList, deletedList);
        verifyDiffReportForGivenReport(root, "s0", "s1", snapshotDiffReport.generateReport(), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("")), new DiffReportEntry(DiffType.CREATE, DFSUtil.string2Bytes("dir4")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2"), DFSUtil.string2Bytes("dir3/dir2")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/file1"), DFSUtil.string2Bytes("dir4/file1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/file2"), DFSUtil.string2Bytes("dir4/file2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir1/file3"), DFSUtil.string2Bytes("dir4/file3")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2/file1"), DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2/file2"), DFSUtil.string2Bytes("dir3/file2")), new DiffReportEntry(DiffType.RENAME, DFSUtil.string2Bytes("dir2/file3"), DFSUtil.string2Bytes("dir3/file3")), new DiffReportEntry(DiffType.MODIFY, DFSUtil.string2Bytes("dir3")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir3/file1")), new DiffReportEntry(DiffType.DELETE, DFSUtil.string2Bytes("dir3/file3")));
    }

    @Test
    public void testSnapshotDiffReportRemoteIterator2() throws Exception {
        final Path root = new Path("/");
        hdfs.mkdirs(root);
        SnapshotTestHelper.createSnapshot(hdfs, root, "s0");
        try {
            hdfs.snapshotDiffReportListingRemoteIterator(root, "s0", "");
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains(("Remote Iterator is" + "supported for snapshotDiffReport between two snapshots")));
        }
    }
}

