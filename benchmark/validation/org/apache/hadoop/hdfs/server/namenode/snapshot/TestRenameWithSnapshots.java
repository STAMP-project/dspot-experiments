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


import DiffType.CREATE;
import DiffType.DELETE;
import DiffType.MODIFY;
import DiffType.RENAME;
import INodeReference.WithName;
import ReadOnlyList.Util;
import Rename.OVERWRITE;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import Snapshot.CURRENT_STATE_ID;
import SyncFlag.UPDATE_LENGTH;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.FSNamesystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.protocol.NSQuotaExceededException;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport;
import org.apache.hadoop.hdfs.protocol.SnapshotDiffReport.DiffReportEntry;
import org.apache.hadoop.hdfs.protocol.SnapshottableDirectoryStatus;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.INodeReference.WithCount;
import org.apache.hadoop.hdfs.server.namenode.snapshot.DirectoryWithSnapshotFeature.ChildrenDiff;
import org.apache.hadoop.hdfs.server.namenode.snapshot.DirectoryWithSnapshotFeature.DirectoryDiff;
import org.apache.hadoop.hdfs.util.ReadOnlyList;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.Whitebox;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Testing rename with snapshots.
 */
public class TestRenameWithSnapshots {
    static {
        SnapshotTestHelper.disableLogs();
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestRenameWithSnapshots.class);

    private static final long SEED = 0;

    private static final short REPL = 3;

    private static final short REPL_1 = 2;

    private static final short REPL_2 = 1;

    private static final long BLOCKSIZE = 1024;

    private static final Configuration conf = new Configuration();

    private static MiniDFSCluster cluster;

    private static FSNamesystem fsn;

    private static FSDirectory fsdir;

    private static DistributedFileSystem hdfs;

    private static final String testDir = GenericTestUtils.getTestDir().getAbsolutePath();

    private static final Path dir = new Path("/testRenameWithSnapshots");

    private static final Path sub1 = new Path(TestRenameWithSnapshots.dir, "sub1");

    private static final Path file1 = new Path(TestRenameWithSnapshots.sub1, "file1");

    private static final Path file2 = new Path(TestRenameWithSnapshots.sub1, "file2");

    private static final Path file3 = new Path(TestRenameWithSnapshots.sub1, "file3");

    private static final String snap1 = "snap1";

    private static final String snap2 = "snap2";

    @Test(timeout = 300000)
    public void testRenameFromSDir2NonSDir() throws Exception {
        final String dirStr = "/testRenameWithSnapshot";
        final String abcStr = dirStr + "/abc";
        final Path abc = new Path(abcStr);
        TestRenameWithSnapshots.hdfs.mkdirs(abc, new FsPermission(((short) (511))));
        TestRenameWithSnapshots.hdfs.allowSnapshot(abc);
        final Path foo = new Path(abc, "foo");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, foo, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.createSnapshot(abc, "s0");
        try {
            TestRenameWithSnapshots.hdfs.rename(abc, new Path(dirStr, "tmp"));
            Assert.fail((("Expect exception since " + abc) + " is snapshottable and already has snapshots"));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains((abcStr + " is snapshottable and already has snapshots"), e);
        }
        final String xyzStr = dirStr + "/xyz";
        final Path xyz = new Path(xyzStr);
        TestRenameWithSnapshots.hdfs.mkdirs(xyz, new FsPermission(((short) (511))));
        final Path bar = new Path(xyz, "bar");
        TestRenameWithSnapshots.hdfs.rename(foo, bar);
        final INode fooRef = TestRenameWithSnapshots.fsdir.getINode(SnapshotTestHelper.getSnapshotPath(abc, "s0", "foo").toString());
        Assert.assertTrue(fooRef.isReference());
        Assert.assertTrue(((fooRef.asReference()) instanceof INodeReference.WithName));
        final INodeReference.WithCount withCount = ((INodeReference.WithCount) (fooRef.asReference().getReferredINode()));
        Assert.assertEquals(2, withCount.getReferenceCount());
        final INode barRef = TestRenameWithSnapshots.fsdir.getINode(bar.toString());
        Assert.assertTrue(barRef.isReference());
        Assert.assertSame(withCount, barRef.asReference().getReferredINode());
        TestRenameWithSnapshots.hdfs.delete(bar, false);
        Assert.assertEquals(1, withCount.getReferenceCount());
    }

    /**
     * Rename a file under a snapshottable directory, file does not exist
     * in a snapshot.
     */
    @Test(timeout = 60000)
    public void testRenameFileNotInSnapshot() throws Exception {
        TestRenameWithSnapshots.hdfs.mkdirs(TestRenameWithSnapshots.sub1);
        TestRenameWithSnapshots.hdfs.allowSnapshot(TestRenameWithSnapshots.sub1);
        TestRenameWithSnapshots.hdfs.createSnapshot(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, TestRenameWithSnapshots.file1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.rename(TestRenameWithSnapshots.file1, TestRenameWithSnapshots.file2);
        // Query the diff report and make sure it looks as expected.
        SnapshotDiffReport diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1, "");
        List<DiffReportEntry> entries = diffReport.getDiffList();
        Assert.assertTrue(((entries.size()) == 2));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, CREATE, TestRenameWithSnapshots.file2.getName(), null));
    }

    /**
     * Rename a file under a snapshottable directory, file exists
     * in a snapshot.
     */
    @Test
    public void testRenameFileInSnapshot() throws Exception {
        TestRenameWithSnapshots.hdfs.mkdirs(TestRenameWithSnapshots.sub1);
        TestRenameWithSnapshots.hdfs.allowSnapshot(TestRenameWithSnapshots.sub1);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, TestRenameWithSnapshots.file1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.createSnapshot(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1);
        TestRenameWithSnapshots.hdfs.rename(TestRenameWithSnapshots.file1, TestRenameWithSnapshots.file2);
        // Query the diff report and make sure it looks as expected.
        SnapshotDiffReport diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1, "");
        System.out.println(("DiffList is " + (diffReport.toString())));
        List<DiffReportEntry> entries = diffReport.getDiffList();
        Assert.assertTrue(((entries.size()) == 2));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, TestRenameWithSnapshots.file1.getName(), TestRenameWithSnapshots.file2.getName()));
    }

    @Test(timeout = 60000)
    public void testRenameTwiceInSnapshot() throws Exception {
        TestRenameWithSnapshots.hdfs.mkdirs(TestRenameWithSnapshots.sub1);
        TestRenameWithSnapshots.hdfs.allowSnapshot(TestRenameWithSnapshots.sub1);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, TestRenameWithSnapshots.file1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.createSnapshot(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1);
        TestRenameWithSnapshots.hdfs.rename(TestRenameWithSnapshots.file1, TestRenameWithSnapshots.file2);
        TestRenameWithSnapshots.hdfs.createSnapshot(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap2);
        TestRenameWithSnapshots.hdfs.rename(TestRenameWithSnapshots.file2, TestRenameWithSnapshots.file3);
        SnapshotDiffReport diffReport;
        // Query the diff report and make sure it looks as expected.
        diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1, TestRenameWithSnapshots.snap2);
        TestRenameWithSnapshots.LOG.info(("DiffList is " + (diffReport.toString())));
        List<DiffReportEntry> entries = diffReport.getDiffList();
        Assert.assertTrue(((entries.size()) == 2));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, TestRenameWithSnapshots.file1.getName(), TestRenameWithSnapshots.file2.getName()));
        diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap2, "");
        TestRenameWithSnapshots.LOG.info(("DiffList is " + (diffReport.toString())));
        entries = diffReport.getDiffList();
        Assert.assertTrue(((entries.size()) == 2));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, TestRenameWithSnapshots.file2.getName(), TestRenameWithSnapshots.file3.getName()));
        diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, TestRenameWithSnapshots.snap1, "");
        TestRenameWithSnapshots.LOG.info(("DiffList is " + (diffReport.toString())));
        entries = diffReport.getDiffList();
        Assert.assertTrue(((entries.size()) == 2));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, TestRenameWithSnapshots.file1.getName(), TestRenameWithSnapshots.file3.getName()));
    }

    @Test(timeout = 60000)
    public void testRenameFileInSubDirOfDirWithSnapshot() throws Exception {
        final Path sub2 = new Path(TestRenameWithSnapshots.sub1, "sub2");
        final Path sub2file1 = new Path(sub2, "sub2file1");
        final Path sub2file2 = new Path(sub2, "sub2file2");
        final String sub1snap1 = "sub1snap1";
        TestRenameWithSnapshots.hdfs.mkdirs(TestRenameWithSnapshots.sub1);
        TestRenameWithSnapshots.hdfs.mkdirs(sub2);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, sub2file1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, TestRenameWithSnapshots.sub1, sub1snap1);
        // Rename the file in the subdirectory.
        TestRenameWithSnapshots.hdfs.rename(sub2file1, sub2file2);
        // Query the diff report and make sure it looks as expected.
        SnapshotDiffReport diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, sub1snap1, "");
        TestRenameWithSnapshots.LOG.info((("DiffList is \n\"" + (diffReport.toString())) + "\""));
        List<DiffReportEntry> entries = diffReport.getDiffList();
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, sub2.getName(), null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, (((sub2.getName()) + "/") + (sub2file1.getName())), (((sub2.getName()) + "/") + (sub2file2.getName()))));
    }

    @Test(timeout = 60000)
    public void testRenameDirectoryInSnapshot() throws Exception {
        final Path sub2 = new Path(TestRenameWithSnapshots.sub1, "sub2");
        final Path sub3 = new Path(TestRenameWithSnapshots.sub1, "sub3");
        final Path sub2file1 = new Path(sub2, "sub2file1");
        final String sub1snap1 = "sub1snap1";
        TestRenameWithSnapshots.hdfs.mkdirs(TestRenameWithSnapshots.sub1);
        TestRenameWithSnapshots.hdfs.mkdirs(sub2);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, sub2file1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, TestRenameWithSnapshots.sub1, sub1snap1);
        // First rename the sub-directory.
        TestRenameWithSnapshots.hdfs.rename(sub2, sub3);
        // Query the diff report and make sure it looks as expected.
        SnapshotDiffReport diffReport = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(TestRenameWithSnapshots.sub1, sub1snap1, "");
        TestRenameWithSnapshots.LOG.info((("DiffList is \n\"" + (diffReport.toString())) + "\""));
        List<DiffReportEntry> entries = diffReport.getDiffList();
        Assert.assertEquals(2, entries.size());
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, sub2.getName(), sub3.getName()));
    }

    /**
     * After the following steps:
     * <pre>
     * 1. Take snapshot s1 on /dir1 at time t1.
     * 2. Take snapshot s2 on /dir2 at time t2.
     * 3. Modify the subtree of /dir2/foo/ to make it a dir with snapshots.
     * 4. Take snapshot s3 on /dir1 at time t3.
     * 5. Rename /dir2/foo/ to /dir1/foo/.
     * </pre>
     * When changes happening on foo, the diff should be recorded in snapshot s2.
     */
    @Test(timeout = 60000)
    public void testRenameDirAcrossSnapshottableDirs() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir2, "foo");
        final Path bar = new Path(foo, "bar");
        final Path bar2 = new Path(foo, "bar2");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar2, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        TestRenameWithSnapshots.hdfs.setReplication(bar2, TestRenameWithSnapshots.REPL_1);
        TestRenameWithSnapshots.hdfs.delete(bar, true);
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s3");
        final Path newfoo = new Path(sdir1, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        // still can visit the snapshot copy of bar through
        // /dir2/.snapshot/s2/foo/bar
        final Path snapshotBar = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(snapshotBar));
        // delete bar2
        final Path newBar2 = new Path(newfoo, "bar2");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(newBar2));
        TestRenameWithSnapshots.hdfs.delete(newBar2, true);
        // /dir2/.snapshot/s2/foo/bar2 should still work
        final Path bar2_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar2");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        FileStatus status = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_s2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, status.getReplication());
        final Path bar2_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo/bar2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
    }

    /**
     * Rename a single file across snapshottable dirs.
     */
    @Test(timeout = 60000)
    public void testRenameFileAcrossSnapshottableDirs() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir2, "foo");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, foo, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s3");
        final Path newfoo = new Path(sdir1, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        // change the replication factor of foo
        TestRenameWithSnapshots.hdfs.setReplication(newfoo, TestRenameWithSnapshots.REPL_1);
        // /dir2/.snapshot/s2/foo should still work
        final Path foo_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(foo_s2));
        FileStatus status = TestRenameWithSnapshots.hdfs.getFileStatus(foo_s2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, status.getReplication());
        final Path foo_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s3));
        INodeDirectory sdir2Node = TestRenameWithSnapshots.fsdir.getINode(sdir2.toString()).asDirectory();
        Snapshot s2 = sdir2Node.getSnapshot(DFSUtil.string2Bytes("s2"));
        INodeFile sfoo = TestRenameWithSnapshots.fsdir.getINode(newfoo.toString()).asFile();
        Assert.assertEquals(s2.getId(), sfoo.getDiffs().getLastSnapshotId());
    }

    /**
     * Test renaming a dir and then delete snapshots.
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_1() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir2, "foo");
        final Path bar = new Path(foo, "bar");
        final Path bar2 = new Path(foo, "bar2");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar2, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s3");
        final Path newfoo = new Path(sdir1, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        final Path newbar = new Path(newfoo, bar.getName());
        final Path newbar2 = new Path(newfoo, bar2.getName());
        final Path newbar3 = new Path(newfoo, "bar3");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, newbar3, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s4");
        TestRenameWithSnapshots.hdfs.delete(newbar, true);
        TestRenameWithSnapshots.hdfs.delete(newbar3, true);
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(newbar3));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar));
        final Path bar_s4 = SnapshotTestHelper.getSnapshotPath(sdir1, "s4", "foo/bar");
        final Path bar3_s4 = SnapshotTestHelper.getSnapshotPath(sdir1, "s4", "foo/bar3");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s4));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar3_s4));
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s5");
        TestRenameWithSnapshots.hdfs.delete(newbar2, true);
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2));
        final Path bar2_s5 = SnapshotTestHelper.getSnapshotPath(sdir1, "s5", "foo/bar2");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s5));
        // delete snapshot s5. The diff of s5 should be combined to s4
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s5");
        restartClusterAndCheckImage(true);
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s5));
        final Path bar2_s4 = SnapshotTestHelper.getSnapshotPath(sdir1, "s4", "foo/bar2");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s4));
        // delete snapshot s4. The diff of s4 should be combined to s2 instead of
        // s3.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s4");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s4));
        Path bar_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo/bar");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s3));
        bar_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo/bar");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s3));
        final Path bar_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s4));
        Path bar2_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo/bar2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        bar2_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo/bar2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        final Path bar2_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar2");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar3_s4));
        Path bar3_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo/bar3");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar3_s3));
        bar3_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo/bar3");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar3_s3));
        final Path bar3_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar3");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar3_s2));
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // delete snapshot s2.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir2, "s2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s3");
        restartClusterAndCheckImage(true);
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s1");
        restartClusterAndCheckImage(true);
    }

    /**
     * Test renaming a file and then delete snapshots.
     */
    @Test
    public void testRenameFileAndDeleteSnapshot() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir2, "foo");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, foo, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s3");
        final Path newfoo = new Path(sdir1, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        TestRenameWithSnapshots.hdfs.setReplication(newfoo, TestRenameWithSnapshots.REPL_1);
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s4");
        TestRenameWithSnapshots.hdfs.setReplication(newfoo, TestRenameWithSnapshots.REPL_2);
        FileStatus status = TestRenameWithSnapshots.hdfs.getFileStatus(newfoo);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, status.getReplication());
        final Path foo_s4 = SnapshotTestHelper.getSnapshotPath(sdir1, "s4", "foo");
        status = TestRenameWithSnapshots.hdfs.getFileStatus(foo_s4);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, status.getReplication());
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s5");
        final Path foo_s5 = SnapshotTestHelper.getSnapshotPath(sdir1, "s5", "foo");
        status = TestRenameWithSnapshots.hdfs.getFileStatus(foo_s5);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, status.getReplication());
        // delete snapshot s5.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s5");
        restartClusterAndCheckImage(true);
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s5));
        status = TestRenameWithSnapshots.hdfs.getFileStatus(foo_s4);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, status.getReplication());
        // delete snapshot s4.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s4");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s4));
        Path foo_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s3));
        foo_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s3));
        final Path foo_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(foo_s2));
        status = TestRenameWithSnapshots.hdfs.getFileStatus(foo_s2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, status.getReplication());
        INodeFile snode = TestRenameWithSnapshots.fsdir.getINode(newfoo.toString()).asFile();
        Assert.assertEquals(1, snode.getDiffs().asList().size());
        INodeDirectory sdir2Node = TestRenameWithSnapshots.fsdir.getINode(sdir2.toString()).asDirectory();
        Snapshot s2 = sdir2Node.getSnapshot(DFSUtil.string2Bytes("s2"));
        Assert.assertEquals(s2.getId(), snode.getDiffs().getLastSnapshotId());
        // restart cluster
        restartClusterAndCheckImage(true);
        // delete snapshot s2.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir2, "s2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s2));
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s3");
        restartClusterAndCheckImage(true);
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s1");
        restartClusterAndCheckImage(true);
    }

    /**
     * Test rename a dir and a file multiple times across snapshottable
     * directories: /dir1/foo -> /dir2/foo -> /dir3/foo -> /dir2/foo -> /dir1/foo
     *
     * Only create snapshots in the beginning (before the rename).
     */
    @Test
    public void testRenameMoreThanOnceAcrossSnapDirs() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path sdir3 = new Path("/dir3");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir3);
        final Path foo_dir1 = new Path(sdir1, "foo");
        final Path bar1_dir1 = new Path(foo_dir1, "bar1");
        final Path bar2_dir1 = new Path(sdir1, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar1_dir1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar2_dir1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir3, "s3");
        // 1. /dir1/foo -> /dir2/foo, /dir1/bar -> /dir2/bar
        final Path foo_dir2 = new Path(sdir2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo_dir1, foo_dir2);
        final Path bar2_dir2 = new Path(sdir2, "bar");
        TestRenameWithSnapshots.hdfs.rename(bar2_dir1, bar2_dir2);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // modification on /dir2/foo and /dir2/bar
        final Path bar1_dir2 = new Path(foo_dir2, "bar1");
        TestRenameWithSnapshots.hdfs.setReplication(bar1_dir2, TestRenameWithSnapshots.REPL_1);
        TestRenameWithSnapshots.hdfs.setReplication(bar2_dir2, TestRenameWithSnapshots.REPL_1);
        // check
        final Path bar1_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "foo/bar1");
        final Path bar2_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "bar");
        final Path bar1_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar1");
        final Path bar2_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s1));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        FileStatus statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_dir2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, statusBar1.getReplication());
        FileStatus statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar2.getReplication());
        statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_dir2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, statusBar2.getReplication());
        // 2. /dir2/foo -> /dir3/foo, /dir2/bar -> /dir3/bar
        final Path foo_dir3 = new Path(sdir3, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo_dir2, foo_dir3);
        final Path bar2_dir3 = new Path(sdir3, "bar");
        TestRenameWithSnapshots.hdfs.rename(bar2_dir2, bar2_dir3);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // modification on /dir3/foo and /dir3/bar
        final Path bar1_dir3 = new Path(foo_dir3, "bar1");
        TestRenameWithSnapshots.hdfs.setReplication(bar1_dir3, TestRenameWithSnapshots.REPL_2);
        TestRenameWithSnapshots.hdfs.setReplication(bar2_dir3, TestRenameWithSnapshots.REPL_2);
        // check
        final Path bar1_s3 = SnapshotTestHelper.getSnapshotPath(sdir3, "s3", "foo/bar1");
        final Path bar2_s3 = SnapshotTestHelper.getSnapshotPath(sdir3, "s3", "bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s1));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s3));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_dir3);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar1.getReplication());
        statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar2.getReplication());
        statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_dir3);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar2.getReplication());
        // 3. /dir3/foo -> /dir2/foo, /dir3/bar -> /dir2/bar
        TestRenameWithSnapshots.hdfs.rename(foo_dir3, foo_dir2);
        TestRenameWithSnapshots.hdfs.rename(bar2_dir3, bar2_dir2);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // modification on /dir2/foo
        TestRenameWithSnapshots.hdfs.setReplication(bar1_dir2, TestRenameWithSnapshots.REPL);
        TestRenameWithSnapshots.hdfs.setReplication(bar2_dir2, TestRenameWithSnapshots.REPL);
        // check
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s1));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s3));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_dir2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar2.getReplication());
        statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_dir2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar2.getReplication());
        // 4. /dir2/foo -> /dir1/foo, /dir2/bar -> /dir1/bar
        TestRenameWithSnapshots.hdfs.rename(foo_dir2, foo_dir1);
        TestRenameWithSnapshots.hdfs.rename(bar2_dir2, bar2_dir1);
        // check the internal details
        INodeReference fooRef = TestRenameWithSnapshots.fsdir.getINode4Write(foo_dir1.toString()).asReference();
        INodeReference.WithCount fooWithCount = ((WithCount) (fooRef.getReferredINode()));
        // only 2 references: one in deleted list of sdir1, one in created list of
        // sdir1
        Assert.assertEquals(2, fooWithCount.getReferenceCount());
        INodeDirectory foo = fooWithCount.asDirectory();
        Assert.assertEquals(1, foo.getDiffs().asList().size());
        INodeDirectory sdir1Node = TestRenameWithSnapshots.fsdir.getINode(sdir1.toString()).asDirectory();
        Snapshot s1 = sdir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        Assert.assertEquals(s1.getId(), foo.getDirectoryWithSnapshotFeature().getLastSnapshotId());
        INodeFile bar1 = TestRenameWithSnapshots.fsdir.getINode4Write(bar1_dir1.toString()).asFile();
        Assert.assertEquals(1, bar1.getDiffs().asList().size());
        Assert.assertEquals(s1.getId(), bar1.getDiffs().getLastSnapshotId());
        INodeReference barRef = TestRenameWithSnapshots.fsdir.getINode4Write(bar2_dir1.toString()).asReference();
        INodeReference.WithCount barWithCount = ((WithCount) (barRef.getReferredINode()));
        Assert.assertEquals(2, barWithCount.getReferenceCount());
        INodeFile bar = barWithCount.asFile();
        Assert.assertEquals(1, bar.getDiffs().asList().size());
        Assert.assertEquals(s1.getId(), bar.getDiffs().getLastSnapshotId());
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // delete foo
        TestRenameWithSnapshots.hdfs.delete(foo_dir1, true);
        restartClusterAndCheckImage(true);
        TestRenameWithSnapshots.hdfs.delete(bar2_dir1, true);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // check
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s1));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s2));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s3));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_dir1));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_dir1));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_dir1));
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar2 = TestRenameWithSnapshots.hdfs.getFileStatus(bar2_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar2.getReplication());
        final Path foo_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "foo");
        fooRef = TestRenameWithSnapshots.fsdir.getINode(foo_s1.toString()).asReference();
        fooWithCount = ((WithCount) (fooRef.getReferredINode()));
        Assert.assertEquals(1, fooWithCount.getReferenceCount());
        barRef = TestRenameWithSnapshots.fsdir.getINode(bar2_s1.toString()).asReference();
        barWithCount = ((WithCount) (barRef.getReferredINode()));
        Assert.assertEquals(1, barWithCount.getReferenceCount());
    }

    /**
     * Test rename a dir multiple times across snapshottable directories:
     * /dir1/foo -> /dir2/foo -> /dir3/foo -> /dir2/foo -> /dir1/foo
     *
     * Create snapshots after each rename.
     */
    @Test
    public void testRenameMoreThanOnceAcrossSnapDirs_2() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path sdir3 = new Path("/dir3");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir3);
        final Path foo_dir1 = new Path(sdir1, "foo");
        final Path bar1_dir1 = new Path(foo_dir1, "bar1");
        final Path bar_dir1 = new Path(sdir1, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar1_dir1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar_dir1, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir3, "s3");
        // 1. /dir1/foo -> /dir2/foo, /dir1/bar -> /dir2/bar
        final Path foo_dir2 = new Path(sdir2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo_dir1, foo_dir2);
        final Path bar_dir2 = new Path(sdir2, "bar");
        TestRenameWithSnapshots.hdfs.rename(bar_dir1, bar_dir2);
        // modification on /dir2/foo and /dir2/bar
        final Path bar1_dir2 = new Path(foo_dir2, "bar1");
        TestRenameWithSnapshots.hdfs.setReplication(bar1_dir2, TestRenameWithSnapshots.REPL_1);
        TestRenameWithSnapshots.hdfs.setReplication(bar_dir2, TestRenameWithSnapshots.REPL_1);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // create snapshots
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s11");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s22");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir3, "s33");
        // 2. /dir2/foo -> /dir3/foo
        final Path foo_dir3 = new Path(sdir3, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo_dir2, foo_dir3);
        final Path bar_dir3 = new Path(sdir3, "bar");
        TestRenameWithSnapshots.hdfs.rename(bar_dir2, bar_dir3);
        // modification on /dir3/foo
        final Path bar1_dir3 = new Path(foo_dir3, "bar1");
        TestRenameWithSnapshots.hdfs.setReplication(bar1_dir3, TestRenameWithSnapshots.REPL_2);
        TestRenameWithSnapshots.hdfs.setReplication(bar_dir3, TestRenameWithSnapshots.REPL_2);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // create snapshots
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s111");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s222");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir3, "s333");
        // check
        final Path bar1_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "foo/bar1");
        final Path bar1_s22 = SnapshotTestHelper.getSnapshotPath(sdir2, "s22", "foo/bar1");
        final Path bar1_s333 = SnapshotTestHelper.getSnapshotPath(sdir3, "s333", "foo/bar1");
        final Path bar_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "bar");
        final Path bar_s22 = SnapshotTestHelper.getSnapshotPath(sdir2, "s22", "bar");
        final Path bar_s333 = SnapshotTestHelper.getSnapshotPath(sdir3, "s333", "bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s22));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s333));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s22));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s333));
        FileStatus statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_dir3);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s22);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s333);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar1.getReplication());
        FileStatus statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_dir3);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s22);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s333);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar.getReplication());
        // 3. /dir3/foo -> /dir2/foo
        TestRenameWithSnapshots.hdfs.rename(foo_dir3, foo_dir2);
        TestRenameWithSnapshots.hdfs.rename(bar_dir3, bar_dir2);
        // modification on /dir2/foo
        TestRenameWithSnapshots.hdfs.setReplication(bar1_dir2, TestRenameWithSnapshots.REPL);
        TestRenameWithSnapshots.hdfs.setReplication(bar_dir2, TestRenameWithSnapshots.REPL);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // create snapshots
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1111");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2222");
        // check
        final Path bar1_s2222 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2222", "foo/bar1");
        final Path bar_s2222 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2222", "bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s22));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s333));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s2222));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s22));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s333));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s2222));
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_dir2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s22);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s333);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar1.getReplication());
        statusBar1 = TestRenameWithSnapshots.hdfs.getFileStatus(bar1_s2222);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar1.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s1);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_dir2);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s22);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_1, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s333);
        Assert.assertEquals(TestRenameWithSnapshots.REPL_2, statusBar.getReplication());
        statusBar = TestRenameWithSnapshots.hdfs.getFileStatus(bar_s2222);
        Assert.assertEquals(TestRenameWithSnapshots.REPL, statusBar.getReplication());
        // 4. /dir2/foo -> /dir1/foo
        TestRenameWithSnapshots.hdfs.rename(foo_dir2, foo_dir1);
        TestRenameWithSnapshots.hdfs.rename(bar_dir2, bar_dir1);
        // check the internal details
        INodeDirectory sdir1Node = TestRenameWithSnapshots.fsdir.getINode(sdir1.toString()).asDirectory();
        INodeDirectory sdir2Node = TestRenameWithSnapshots.fsdir.getINode(sdir2.toString()).asDirectory();
        INodeDirectory sdir3Node = TestRenameWithSnapshots.fsdir.getINode(sdir3.toString()).asDirectory();
        INodeReference fooRef = TestRenameWithSnapshots.fsdir.getINode4Write(foo_dir1.toString()).asReference();
        INodeReference.WithCount fooWithCount = ((WithCount) (fooRef.getReferredINode()));
        // 5 references: s1, s22, s333, s2222, current tree of sdir1
        Assert.assertEquals(5, fooWithCount.getReferenceCount());
        INodeDirectory foo = fooWithCount.asDirectory();
        DiffList<DirectoryDiff> fooDiffs = foo.getDiffs().asList();
        Assert.assertEquals(4, fooDiffs.size());
        Snapshot s2222 = sdir2Node.getSnapshot(DFSUtil.string2Bytes("s2222"));
        Snapshot s333 = sdir3Node.getSnapshot(DFSUtil.string2Bytes("s333"));
        Snapshot s22 = sdir2Node.getSnapshot(DFSUtil.string2Bytes("s22"));
        Snapshot s1 = sdir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        Assert.assertEquals(s2222.getId(), fooDiffs.get(3).getSnapshotId());
        Assert.assertEquals(s333.getId(), fooDiffs.get(2).getSnapshotId());
        Assert.assertEquals(s22.getId(), fooDiffs.get(1).getSnapshotId());
        Assert.assertEquals(s1.getId(), fooDiffs.get(0).getSnapshotId());
        INodeFile bar1 = TestRenameWithSnapshots.fsdir.getINode4Write(bar1_dir1.toString()).asFile();
        DiffList<FileDiff> bar1Diffs = bar1.getDiffs().asList();
        Assert.assertEquals(3, bar1Diffs.size());
        Assert.assertEquals(s333.getId(), bar1Diffs.get(2).getSnapshotId());
        Assert.assertEquals(s22.getId(), bar1Diffs.get(1).getSnapshotId());
        Assert.assertEquals(s1.getId(), bar1Diffs.get(0).getSnapshotId());
        INodeReference barRef = TestRenameWithSnapshots.fsdir.getINode4Write(bar_dir1.toString()).asReference();
        INodeReference.WithCount barWithCount = ((WithCount) (barRef.getReferredINode()));
        // 5 references: s1, s22, s333, s2222, current tree of sdir1
        Assert.assertEquals(5, barWithCount.getReferenceCount());
        INodeFile bar = barWithCount.asFile();
        DiffList<FileDiff> barDiffs = bar.getDiffs().asList();
        Assert.assertEquals(4, barDiffs.size());
        Assert.assertEquals(s2222.getId(), barDiffs.get(3).getSnapshotId());
        Assert.assertEquals(s333.getId(), barDiffs.get(2).getSnapshotId());
        Assert.assertEquals(s22.getId(), barDiffs.get(1).getSnapshotId());
        Assert.assertEquals(s1.getId(), barDiffs.get(0).getSnapshotId());
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // delete foo
        TestRenameWithSnapshots.hdfs.delete(foo_dir1, true);
        TestRenameWithSnapshots.hdfs.delete(bar_dir1, true);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // check
        final Path bar1_s1111 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1111", "foo/bar1");
        final Path bar_s1111 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1111", "bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s22));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s333));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar1_s2222));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar1_s1111));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s1));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s22));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s333));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s2222));
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s1111));
        final Path foo_s2222 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2222", "foo");
        fooRef = TestRenameWithSnapshots.fsdir.getINode(foo_s2222.toString()).asReference();
        fooWithCount = ((WithCount) (fooRef.getReferredINode()));
        Assert.assertEquals(4, fooWithCount.getReferenceCount());
        foo = fooWithCount.asDirectory();
        fooDiffs = foo.getDiffs().asList();
        Assert.assertEquals(4, fooDiffs.size());
        Assert.assertEquals(s2222.getId(), fooDiffs.get(3).getSnapshotId());
        bar1Diffs = bar1.getDiffs().asList();
        Assert.assertEquals(3, bar1Diffs.size());
        Assert.assertEquals(s333.getId(), bar1Diffs.get(2).getSnapshotId());
        barRef = TestRenameWithSnapshots.fsdir.getINode(bar_s2222.toString()).asReference();
        barWithCount = ((WithCount) (barRef.getReferredINode()));
        Assert.assertEquals(4, barWithCount.getReferenceCount());
        bar = barWithCount.asFile();
        barDiffs = bar.getDiffs().asList();
        Assert.assertEquals(4, barDiffs.size());
        Assert.assertEquals(s2222.getId(), barDiffs.get(3).getSnapshotId());
    }

    /**
     * Test rename from a non-snapshottable dir to a snapshottable dir
     */
    @Test(timeout = 60000)
    public void testRenameFromNonSDir2SDir() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, TestRenameWithSnapshots.snap1);
        final Path newfoo = new Path(sdir2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        INode fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(newfoo.toString());
        Assert.assertTrue((fooNode instanceof INodeDirectory));
    }

    /**
     * Test rename where the src/dst directories are both snapshottable
     * directories without snapshots. In such case we need to update the
     * snapshottable dir list in SnapshotManager.
     */
    @Test(timeout = 60000)
    public void testRenameAndUpdateSnapshottableDirs() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(sdir2, "bar");
        TestRenameWithSnapshots.hdfs.mkdirs(foo);
        TestRenameWithSnapshots.hdfs.mkdirs(bar);
        TestRenameWithSnapshots.hdfs.allowSnapshot(foo);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.snap1);
        Assert.assertEquals(2, TestRenameWithSnapshots.fsn.getSnapshottableDirListing().length);
        INodeDirectory fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(foo.toString()).asDirectory();
        long fooId = fooNode.getId();
        try {
            TestRenameWithSnapshots.hdfs.rename(foo, bar, OVERWRITE);
            Assert.fail((("Expect exception since " + bar) + " is snapshottable and already has snapshots"));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(((bar.toString()) + " is snapshottable and already has snapshots"), e);
        }
        TestRenameWithSnapshots.hdfs.deleteSnapshot(bar, TestRenameWithSnapshots.snap1);
        TestRenameWithSnapshots.hdfs.rename(foo, bar, OVERWRITE);
        SnapshottableDirectoryStatus[] dirs = TestRenameWithSnapshots.fsn.getSnapshottableDirListing();
        Assert.assertEquals(1, dirs.length);
        Assert.assertEquals(bar, dirs[0].getFullPath());
        Assert.assertEquals(fooId, dirs[0].getDirStatus().getFileId());
    }

    /**
     * After rename, delete the snapshot in src
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_2() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir2, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s3");
        final Path newfoo = new Path(sdir1, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        final Path bar2 = new Path(newfoo, "bar2");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar2, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir1, "s4");
        TestRenameWithSnapshots.hdfs.delete(newfoo, true);
        final Path bar2_s4 = SnapshotTestHelper.getSnapshotPath(sdir1, "s4", "foo/bar2");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar2_s4));
        final Path bar_s4 = SnapshotTestHelper.getSnapshotPath(sdir1, "s4", "foo/bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s4));
        // delete snapshot s4. The diff of s4 should be combined to s3
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s4");
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        Path bar_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo/bar");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s3));
        bar_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo/bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s3));
        Path bar2_s3 = SnapshotTestHelper.getSnapshotPath(sdir1, "s3", "foo/bar2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        bar2_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo/bar2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar2_s3));
        // delete snapshot s3
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir2, "s3");
        final Path bar_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo/bar");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar_s2));
        // check internal details
        INodeDirectory sdir2Node = TestRenameWithSnapshots.fsdir.getINode(sdir2.toString()).asDirectory();
        Snapshot s2 = sdir2Node.getSnapshot(DFSUtil.string2Bytes("s2"));
        final Path foo_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo");
        INodeReference fooRef = TestRenameWithSnapshots.fsdir.getINode(foo_s2.toString()).asReference();
        Assert.assertTrue((fooRef instanceof INodeReference.WithName));
        INodeReference.WithCount fooWC = ((WithCount) (fooRef.getReferredINode()));
        Assert.assertEquals(1, fooWC.getReferenceCount());
        INodeDirectory fooDir = fooWC.getReferredINode().asDirectory();
        DiffList<DirectoryDiff> diffs = fooDir.getDiffs().asList();
        Assert.assertEquals(1, diffs.size());
        Assert.assertEquals(s2.getId(), diffs.get(0).getSnapshotId());
        // restart the cluster and check fsimage
        restartClusterAndCheckImage(true);
        // delete snapshot s2.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir2, "s2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(bar_s2));
        restartClusterAndCheckImage(true);
        // make sure the whole referred subtree has been destroyed
        QuotaCounts q = TestRenameWithSnapshots.fsdir.getRoot().getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(3, q.getNameSpace());
        Assert.assertEquals(0, q.getStorageSpace());
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, "s1");
        restartClusterAndCheckImage(true);
        q = TestRenameWithSnapshots.fsdir.getRoot().getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(3, q.getNameSpace());
        Assert.assertEquals(0, q.getStorageSpace());
    }

    /**
     * Rename a file and then append the same file.
     */
    @Test
    public void testRenameAndAppend() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir1, "foo");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, foo, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, TestRenameWithSnapshots.snap1);
        final Path foo2 = new Path(sdir2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        INode fooRef = TestRenameWithSnapshots.fsdir.getINode4Write(foo2.toString());
        Assert.assertTrue((fooRef instanceof INodeReference.DstReference));
        FSDataOutputStream out = TestRenameWithSnapshots.hdfs.append(foo2);
        try {
            byte[] content = new byte[1024];
            new Random().nextBytes(content);
            out.write(content);
            fooRef = TestRenameWithSnapshots.fsdir.getINode4Write(foo2.toString());
            Assert.assertTrue((fooRef instanceof INodeReference.DstReference));
            INodeFile fooNode = fooRef.asFile();
            Assert.assertTrue(fooNode.isWithSnapshot());
            Assert.assertTrue(fooNode.isUnderConstruction());
        } finally {
            if (out != null) {
                out.close();
            }
        }
        fooRef = TestRenameWithSnapshots.fsdir.getINode4Write(foo2.toString());
        Assert.assertTrue((fooRef instanceof INodeReference.DstReference));
        INodeFile fooNode = fooRef.asFile();
        Assert.assertTrue(fooNode.isWithSnapshot());
        Assert.assertFalse(fooNode.isUnderConstruction());
        restartClusterAndCheckImage(true);
    }

    /**
     * Test the undo section of rename. Before the rename, we create the renamed
     * file/dir before taking the snapshot.
     */
    @Test
    public void testRenameUndo_1() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        final Path dir2file = new Path(sdir2, "file");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, dir2file, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        INodeDirectory dir2 = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        INodeDirectory mockDir2 = Mockito.spy(dir2);
        Mockito.doReturn(false).when(mockDir2).addChild(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        INodeDirectory root = TestRenameWithSnapshots.fsdir.getINode4Write("/").asDirectory();
        root.replaceChild(dir2, mockDir2, TestRenameWithSnapshots.fsdir.getINodeMap());
        final Path newfoo = new Path(sdir2, "foo");
        boolean result = TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        Assert.assertFalse(result);
        // check the current internal details
        INodeDirectory dir1Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir1.toString()).asDirectory();
        Snapshot s1 = dir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        ReadOnlyList<INode> dir1Children = dir1Node.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, dir1Children.size());
        Assert.assertEquals(foo.getName(), dir1Children.get(0).getLocalName());
        DiffList<DirectoryDiff> dir1Diffs = dir1Node.getDiffs().asList();
        Assert.assertEquals(1, dir1Diffs.size());
        Assert.assertEquals(s1.getId(), dir1Diffs.get(0).getSnapshotId());
        // after the undo of rename, both the created and deleted list of sdir1
        // should be empty
        ChildrenDiff childrenDiff = dir1Diffs.get(0).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(0, 0, childrenDiff);
        INode fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(((fooNode.isDirectory()) && (fooNode.asDirectory().isWithSnapshot())));
        DiffList<DirectoryDiff> fooDiffs = fooNode.asDirectory().getDiffs().asList();
        Assert.assertEquals(1, fooDiffs.size());
        Assert.assertEquals(s1.getId(), fooDiffs.get(0).getSnapshotId());
        final Path foo_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "foo");
        INode fooNode_s1 = TestRenameWithSnapshots.fsdir.getINode(foo_s1.toString());
        Assert.assertTrue((fooNode_s1 == fooNode));
        // check sdir2
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(newfoo));
        INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        Assert.assertFalse(dir2Node.isWithSnapshot());
        ReadOnlyList<INode> dir2Children = dir2Node.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, dir2Children.size());
        Assert.assertEquals(dir2file.getName(), dir2Children.get(0).getLocalName());
    }

    /**
     * Test the undo section of rename. Before the rename, we create the renamed
     * file/dir after taking the snapshot.
     */
    @Test
    public void testRenameUndo_2() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        final Path dir2file = new Path(sdir2, "file");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, dir2file, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        // create foo after taking snapshot
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        INodeDirectory dir2 = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        INodeDirectory mockDir2 = Mockito.spy(dir2);
        Mockito.doReturn(false).when(mockDir2).addChild(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        INodeDirectory root = TestRenameWithSnapshots.fsdir.getINode4Write("/").asDirectory();
        root.replaceChild(dir2, mockDir2, TestRenameWithSnapshots.fsdir.getINodeMap());
        final Path newfoo = new Path(sdir2, "foo");
        boolean result = TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        Assert.assertFalse(result);
        // check the current internal details
        INodeDirectory dir1Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir1.toString()).asDirectory();
        Snapshot s1 = dir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        ReadOnlyList<INode> dir1Children = dir1Node.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, dir1Children.size());
        Assert.assertEquals(foo.getName(), dir1Children.get(0).getLocalName());
        DiffList<DirectoryDiff> dir1Diffs = dir1Node.getDiffs().asList();
        Assert.assertEquals(1, dir1Diffs.size());
        Assert.assertEquals(s1.getId(), dir1Diffs.get(0).getSnapshotId());
        // after the undo of rename, the created list of sdir1 should contain
        // 1 element
        ChildrenDiff childrenDiff = dir1Diffs.get(0).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(1, 0, childrenDiff);
        INode fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(foo.toString());
        Assert.assertTrue((fooNode instanceof INodeDirectory));
        Assert.assertTrue(((childrenDiff.getCreatedUnmodifiable().get(0)) == fooNode));
        final Path foo_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", "foo");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s1));
        // check sdir2
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(newfoo));
        INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        Assert.assertFalse(dir2Node.isWithSnapshot());
        ReadOnlyList<INode> dir2Children = dir2Node.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, dir2Children.size());
        Assert.assertEquals(dir2file.getName(), dir2Children.get(0).getLocalName());
    }

    /**
     * Test the undo section of the second-time rename.
     */
    @Test
    public void testRenameUndo_3() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path sdir3 = new Path("/dir3");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir3);
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        INodeDirectory dir3 = TestRenameWithSnapshots.fsdir.getINode4Write(sdir3.toString()).asDirectory();
        INodeDirectory mockDir3 = Mockito.spy(dir3);
        Mockito.doReturn(false).when(mockDir3).addChild(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        INodeDirectory root = TestRenameWithSnapshots.fsdir.getINode4Write("/").asDirectory();
        root.replaceChild(dir3, mockDir3, TestRenameWithSnapshots.fsdir.getINodeMap());
        final Path foo_dir2 = new Path(sdir2, "foo2");
        final Path foo_dir3 = new Path(sdir3, "foo3");
        TestRenameWithSnapshots.hdfs.rename(foo, foo_dir2);
        boolean result = TestRenameWithSnapshots.hdfs.rename(foo_dir2, foo_dir3);
        Assert.assertFalse(result);
        // check the current internal details
        INodeDirectory dir1Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir1.toString()).asDirectory();
        Snapshot s1 = dir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        Snapshot s2 = dir2Node.getSnapshot(DFSUtil.string2Bytes("s2"));
        ReadOnlyList<INode> dir2Children = dir2Node.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, dir2Children.size());
        DiffList<DirectoryDiff> dir2Diffs = dir2Node.getDiffs().asList();
        Assert.assertEquals(1, dir2Diffs.size());
        Assert.assertEquals(s2.getId(), dir2Diffs.get(0).getSnapshotId());
        ChildrenDiff childrenDiff = dir2Diffs.get(0).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(1, 0, childrenDiff);
        final Path foo_s2 = SnapshotTestHelper.getSnapshotPath(sdir2, "s2", "foo2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s2));
        INode fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(foo_dir2.toString());
        Assert.assertTrue(((childrenDiff.getCreatedUnmodifiable().get(0)) == fooNode));
        Assert.assertTrue((fooNode instanceof INodeReference.DstReference));
        DiffList<DirectoryDiff> fooDiffs = fooNode.asDirectory().getDiffs().asList();
        Assert.assertEquals(1, fooDiffs.size());
        Assert.assertEquals(s1.getId(), fooDiffs.get(0).getSnapshotId());
        // create snapshot on sdir2 and rename again
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir2, "s3");
        result = TestRenameWithSnapshots.hdfs.rename(foo_dir2, foo_dir3);
        Assert.assertFalse(result);
        // check internal details again
        dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        Snapshot s3 = dir2Node.getSnapshot(DFSUtil.string2Bytes("s3"));
        fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(foo_dir2.toString());
        dir2Children = dir2Node.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, dir2Children.size());
        dir2Diffs = dir2Node.getDiffs().asList();
        Assert.assertEquals(2, dir2Diffs.size());
        Assert.assertEquals(s2.getId(), dir2Diffs.get(0).getSnapshotId());
        Assert.assertEquals(s3.getId(), dir2Diffs.get(1).getSnapshotId());
        childrenDiff = dir2Diffs.get(0).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(1, 0, childrenDiff);
        Assert.assertTrue(((childrenDiff.getCreatedUnmodifiable().get(0)) == fooNode));
        childrenDiff = dir2Diffs.get(1).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(0, 0, childrenDiff);
        final Path foo_s3 = SnapshotTestHelper.getSnapshotPath(sdir2, "s3", "foo2");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(foo_s2));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(foo_s3));
        Assert.assertTrue((fooNode instanceof INodeReference.DstReference));
        fooDiffs = fooNode.asDirectory().getDiffs().asList();
        Assert.assertEquals(2, fooDiffs.size());
        Assert.assertEquals(s1.getId(), fooDiffs.get(0).getSnapshotId());
        Assert.assertEquals(s3.getId(), fooDiffs.get(1).getSnapshotId());
    }

    /**
     * Test undo where dst node being overwritten is a reference node
     */
    @Test
    public void testRenameUndo_4() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path sdir3 = new Path("/dir3");
        TestRenameWithSnapshots.hdfs.mkdirs(sdir1);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir3);
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        final Path foo2 = new Path(sdir2, "foo2");
        TestRenameWithSnapshots.hdfs.mkdirs(foo2);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        // rename foo2 to foo3, so that foo3 will be a reference node
        final Path foo3 = new Path(sdir3, "foo3");
        TestRenameWithSnapshots.hdfs.rename(foo2, foo3);
        INode foo3Node = TestRenameWithSnapshots.fsdir.getINode4Write(foo3.toString());
        Assert.assertTrue(foo3Node.isReference());
        INodeDirectory dir3 = TestRenameWithSnapshots.fsdir.getINode4Write(sdir3.toString()).asDirectory();
        INodeDirectory mockDir3 = Mockito.spy(dir3);
        // fail the rename but succeed in undo
        Mockito.doReturn(false).when(mockDir3).addChild(Mockito.isNull(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt());
        Mockito.when(mockDir3.addChild(Mockito.isNotNull(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyInt())).thenReturn(false).thenCallRealMethod();
        INodeDirectory root = TestRenameWithSnapshots.fsdir.getINode4Write("/").asDirectory();
        root.replaceChild(dir3, mockDir3, TestRenameWithSnapshots.fsdir.getINodeMap());
        foo3Node.setParent(mockDir3);
        try {
            TestRenameWithSnapshots.hdfs.rename(foo, foo3, OVERWRITE);
            Assert.fail((((("the rename from " + foo) + " to ") + foo3) + " should fail"));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains((((("rename from " + foo) + " to ") + foo3) + " failed."), e);
        }
        // make sure the undo is correct
        final INode foo3Node_undo = TestRenameWithSnapshots.fsdir.getINode4Write(foo3.toString());
        Assert.assertSame(foo3Node, foo3Node_undo);
        INodeReference.WithCount foo3_wc = ((WithCount) (foo3Node.asReference().getReferredINode()));
        Assert.assertEquals(2, foo3_wc.getReferenceCount());
        Assert.assertSame(foo3Node, foo3_wc.getParentReference());
    }

    /**
     * Test rename while the rename operation will exceed the quota in the dst
     * tree.
     */
    @Test
    public void testRenameUndo_5() throws Exception {
        final Path test = new Path("/test");
        final Path dir1 = new Path(test, "dir1");
        final Path dir2 = new Path(test, "dir2");
        final Path subdir2 = new Path(dir2, "subdir2");
        TestRenameWithSnapshots.hdfs.mkdirs(dir1);
        TestRenameWithSnapshots.hdfs.mkdirs(subdir2);
        final Path foo = new Path(dir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir2, "s2");
        // set ns quota of dir2 to 4, so the current remaining is 2 (already has
        // dir2, and subdir2)
        TestRenameWithSnapshots.hdfs.setQuota(dir2, 4, ((Long.MAX_VALUE) - 1));
        final Path foo2 = new Path(subdir2, foo.getName());
        FSDirectory fsdir2 = Mockito.spy(TestRenameWithSnapshots.fsdir);
        Mockito.doThrow(new NSQuotaExceededException("fake exception")).when(fsdir2).addLastINode(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        Whitebox.setInternalState(TestRenameWithSnapshots.fsn, "dir", fsdir2);
        // rename /test/dir1/foo to /test/dir2/subdir2/foo.
        // FSDirectory#verifyQuota4Rename will pass since the remaining quota is 2.
        // However, the rename operation will fail since we let addLastINode throw
        // NSQuotaExceededException
        boolean rename = TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        Assert.assertFalse(rename);
        // check the undo
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(foo));
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar));
        INodeDirectory dir1Node = fsdir2.getINode4Write(dir1.toString()).asDirectory();
        List<INode> childrenList = Util.asList(dir1Node.getChildrenList(CURRENT_STATE_ID));
        Assert.assertEquals(1, childrenList.size());
        INode fooNode = childrenList.get(0);
        Assert.assertTrue(fooNode.asDirectory().isWithSnapshot());
        INode barNode = fsdir2.getINode4Write(bar.toString());
        Assert.assertTrue(((barNode.getClass()) == (org.apache.hadoop.hdfs.server.namenode.INodeFile.class)));
        Assert.assertSame(fooNode, barNode.getParent());
        DiffList<DirectoryDiff> diffList = dir1Node.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        DirectoryDiff diff = diffList.get(0);
        TestRenameWithSnapshots.assertSizes(0, 0, diff.getChildrenDiff());
        // check dir2
        INodeDirectory dir2Node = fsdir2.getINode4Write(dir2.toString()).asDirectory();
        Assert.assertTrue(dir2Node.isSnapshottable());
        QuotaCounts counts = dir2Node.computeQuotaUsage(TestRenameWithSnapshots.fsdir.getBlockStoragePolicySuite());
        Assert.assertEquals(2, counts.getNameSpace());
        Assert.assertEquals(0, counts.getStorageSpace());
        childrenList = Util.asList(dir2Node.asDirectory().getChildrenList(CURRENT_STATE_ID));
        Assert.assertEquals(1, childrenList.size());
        INode subdir2Node = childrenList.get(0);
        Assert.assertSame(dir2Node, subdir2Node.getParent());
        Assert.assertSame(subdir2Node, fsdir2.getINode4Write(subdir2.toString()));
        diffList = dir2Node.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        diff = diffList.get(0);
        TestRenameWithSnapshots.assertSizes(0, 0, diff.getChildrenDiff());
    }

    /**
     * Test the rename undo when removing dst node fails
     */
    @Test
    public void testRenameUndo_6() throws Exception {
        final Path test = new Path("/test");
        final Path dir1 = new Path(test, "dir1");
        final Path dir2 = new Path(test, "dir2");
        final Path sub_dir2 = new Path(dir2, "subdir");
        final Path subsub_dir2 = new Path(sub_dir2, "subdir");
        TestRenameWithSnapshots.hdfs.mkdirs(dir1);
        TestRenameWithSnapshots.hdfs.mkdirs(subsub_dir2);
        final Path foo = new Path(dir1, "foo");
        TestRenameWithSnapshots.hdfs.mkdirs(foo);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir2, "s2");
        // set ns quota of dir2 to 4, so the current remaining is 1 (already has
        // dir2, sub_dir2, and subsub_dir2)
        TestRenameWithSnapshots.hdfs.setQuota(dir2, 4, ((Long.MAX_VALUE) - 1));
        FSDirectory fsdir2 = Mockito.spy(TestRenameWithSnapshots.fsdir);
        Mockito.doThrow(new RuntimeException("fake exception")).when(fsdir2).removeLastINode(ArgumentMatchers.any());
        Whitebox.setInternalState(TestRenameWithSnapshots.fsn, "dir", fsdir2);
        // rename /test/dir1/foo to /test/dir2/sub_dir2/subsub_dir2.
        // FSDirectory#verifyQuota4Rename will pass since foo only be counted
        // as 1 in NS quota. However, the rename operation will fail when removing
        // subsub_dir2.
        try {
            TestRenameWithSnapshots.hdfs.rename(foo, subsub_dir2, OVERWRITE);
            Assert.fail("Expect QuotaExceedException");
        } catch (Exception e) {
            String msg = "fake exception";
            GenericTestUtils.assertExceptionContains(msg, e);
        }
        // check the undo
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(foo));
        INodeDirectory dir1Node = fsdir2.getINode4Write(dir1.toString()).asDirectory();
        List<INode> childrenList = Util.asList(dir1Node.getChildrenList(CURRENT_STATE_ID));
        Assert.assertEquals(1, childrenList.size());
        INode fooNode = childrenList.get(0);
        Assert.assertTrue(fooNode.asDirectory().isWithSnapshot());
        Assert.assertSame(dir1Node, fooNode.getParent());
        DiffList<DirectoryDiff> diffList = dir1Node.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        DirectoryDiff diff = diffList.get(0);
        TestRenameWithSnapshots.assertSizes(0, 0, diff.getChildrenDiff());
        // check dir2
        INodeDirectory dir2Node = fsdir2.getINode4Write(dir2.toString()).asDirectory();
        Assert.assertTrue(dir2Node.isSnapshottable());
        QuotaCounts counts = dir2Node.computeQuotaUsage(TestRenameWithSnapshots.fsdir.getBlockStoragePolicySuite());
        Assert.assertEquals(3, counts.getNameSpace());
        Assert.assertEquals(0, counts.getStorageSpace());
        childrenList = Util.asList(dir2Node.asDirectory().getChildrenList(CURRENT_STATE_ID));
        Assert.assertEquals(1, childrenList.size());
        INode subdir2Node = childrenList.get(0);
        Assert.assertSame(dir2Node, subdir2Node.getParent());
        Assert.assertSame(subdir2Node, fsdir2.getINode4Write(sub_dir2.toString()));
        INode subsubdir2Node = fsdir2.getINode4Write(subsub_dir2.toString());
        Assert.assertTrue(((subsubdir2Node.getClass()) == (INodeDirectory.class)));
        Assert.assertSame(subdir2Node, subsubdir2Node.getParent());
        diffList = dir2Node.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        diff = diffList.get(0);
        TestRenameWithSnapshots.assertSizes(0, 0, diff.getChildrenDiff());
    }

    /**
     * Test rename to an invalid name (xxx/.snapshot)
     */
    @Test
    public void testRenameUndo_7() throws Exception {
        final Path root = new Path("/");
        final Path foo = new Path(root, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // create a snapshot on root
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, TestRenameWithSnapshots.snap1);
        // rename bar to /foo/.snapshot which is invalid
        final Path invalid = new Path(foo, HdfsConstants.DOT_SNAPSHOT_DIR);
        try {
            TestRenameWithSnapshots.hdfs.rename(bar, invalid);
            Assert.fail("expect exception since invalid name is used for rename");
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains((("\"" + (HdfsConstants.DOT_SNAPSHOT_DIR)) + "\" is a reserved name"), e);
        }
        // check
        INodeDirectory rootNode = TestRenameWithSnapshots.fsdir.getINode4Write(root.toString()).asDirectory();
        INodeDirectory fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(foo.toString()).asDirectory();
        ReadOnlyList<INode> children = fooNode.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, children.size());
        DiffList<DirectoryDiff> diffList = fooNode.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        DirectoryDiff diff = diffList.get(0);
        // this diff is generated while renaming
        Snapshot s1 = rootNode.getSnapshot(DFSUtil.string2Bytes(TestRenameWithSnapshots.snap1));
        Assert.assertEquals(s1.getId(), diff.getSnapshotId());
        // after undo, the diff should be empty
        TestRenameWithSnapshots.assertSizes(0, 0, diff.getChildrenDiff());
        // bar was converted to filewithsnapshot while renaming
        INodeFile barNode = TestRenameWithSnapshots.fsdir.getINode4Write(bar.toString()).asFile();
        Assert.assertSame(barNode, children.get(0));
        Assert.assertSame(fooNode, barNode.getParent());
        DiffList<FileDiff> barDiffList = barNode.getDiffs().asList();
        Assert.assertEquals(1, barDiffList.size());
        FileDiff barDiff = barDiffList.get(0);
        Assert.assertEquals(s1.getId(), barDiff.getSnapshotId());
        // restart cluster multiple times to make sure the fsimage and edits log are
        // correct. Note that when loading fsimage, foo and bar will be converted
        // back to normal INodeDirectory and INodeFile since they do not store any
        // snapshot data
        TestRenameWithSnapshots.hdfs.setSafeMode(SAFEMODE_ENTER);
        TestRenameWithSnapshots.hdfs.saveNamespace();
        TestRenameWithSnapshots.hdfs.setSafeMode(SAFEMODE_LEAVE);
        TestRenameWithSnapshots.cluster.shutdown();
        TestRenameWithSnapshots.cluster = new MiniDFSCluster.Builder(TestRenameWithSnapshots.conf).format(false).numDataNodes(TestRenameWithSnapshots.REPL).build();
        TestRenameWithSnapshots.cluster.waitActive();
        restartClusterAndCheckImage(true);
    }

    /**
     * Test the rename undo when quota of dst tree is exceeded after rename.
     */
    @Test
    public void testRenameExceedQuota() throws Exception {
        final Path test = new Path("/test");
        final Path dir1 = new Path(test, "dir1");
        final Path dir2 = new Path(test, "dir2");
        final Path sub_dir2 = new Path(dir2, "subdir");
        final Path subfile_dir2 = new Path(sub_dir2, "subfile");
        TestRenameWithSnapshots.hdfs.mkdirs(dir1);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, subfile_dir2, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        final Path foo = new Path(dir1, "foo");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, foo, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir2, "s2");
        // set ns quota of dir2 to 4, so the current remaining is 1 (already has
        // dir2, sub_dir2, subfile_dir2, and s2)
        TestRenameWithSnapshots.hdfs.setQuota(dir2, 5, ((Long.MAX_VALUE) - 1));
        // rename /test/dir1/foo to /test/dir2/sub_dir2/subfile_dir2.
        // FSDirectory#verifyQuota4Rename will pass since foo only be counted
        // as 1 in NS quota. The rename operation will succeed while the real quota
        // of dir2 will become 7 (dir2, s2 in dir2, sub_dir2, s2 in sub_dir2,
        // subfile_dir2 in deleted list, new subfile, s1 in new subfile).
        TestRenameWithSnapshots.hdfs.rename(foo, subfile_dir2, OVERWRITE);
        // check dir2
        INode dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(dir2.toString());
        Assert.assertTrue(dir2Node.asDirectory().isSnapshottable());
        QuotaCounts counts = dir2Node.computeQuotaUsage(TestRenameWithSnapshots.fsdir.getBlockStoragePolicySuite());
        Assert.assertEquals(4, counts.getNameSpace());
        Assert.assertEquals((((TestRenameWithSnapshots.BLOCKSIZE) * (TestRenameWithSnapshots.REPL)) * 2), counts.getStorageSpace());
    }

    @Test
    public void testRename2PreDescendant() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        TestRenameWithSnapshots.hdfs.mkdirs(bar);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, TestRenameWithSnapshots.snap1);
        // /dir1/foo/bar -> /dir2/bar
        final Path bar2 = new Path(sdir2, "bar");
        TestRenameWithSnapshots.hdfs.rename(bar, bar2);
        // /dir1/foo -> /dir2/bar/foo
        final Path foo2 = new Path(bar2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        restartClusterAndCheckImage(true);
        // delete snap1
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir1, TestRenameWithSnapshots.snap1);
        restartClusterAndCheckImage(true);
    }

    /**
     * move a directory to its prior descendant
     */
    @Test
    public void testRename2PreDescendant_2() throws Exception {
        final Path root = new Path("/");
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        final Path file1InBar = new Path(bar, "file1");
        final Path file2InBar = new Path(bar, "file2");
        TestRenameWithSnapshots.hdfs.mkdirs(bar);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file1InBar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file2InBar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.setQuota(sdir1, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        TestRenameWithSnapshots.hdfs.setQuota(sdir2, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        TestRenameWithSnapshots.hdfs.setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        TestRenameWithSnapshots.hdfs.setQuota(bar, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        // create snapshot on root
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, TestRenameWithSnapshots.snap1);
        // delete file1InBar
        TestRenameWithSnapshots.hdfs.delete(file1InBar, true);
        // create another snapshot on root
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, TestRenameWithSnapshots.snap2);
        // delete file2InBar
        TestRenameWithSnapshots.hdfs.delete(file2InBar, true);
        // /dir1/foo/bar -> /dir2/bar
        final Path bar2 = new Path(sdir2, "bar2");
        TestRenameWithSnapshots.hdfs.rename(bar, bar2);
        // /dir1/foo -> /dir2/bar/foo
        final Path foo2 = new Path(bar2, "foo2");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        restartClusterAndCheckImage(true);
        // delete snapshot snap2
        TestRenameWithSnapshots.hdfs.deleteSnapshot(root, TestRenameWithSnapshots.snap2);
        // after deleteing snap2, the WithName node "bar", which originally was
        // stored in the deleted list of "foo" for snap2, is moved to its deleted
        // list for snap1. In that case, it will not be counted when calculating
        // quota for "foo". However, we do not update this quota usage change while
        // deleting snap2.
        restartClusterAndCheckImage(false);
    }

    /**
     * move a directory to its prior descedant
     */
    @Test
    public void testRename2PreDescendant_3() throws Exception {
        final Path root = new Path("/");
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        final Path fileInBar = new Path(bar, "file");
        TestRenameWithSnapshots.hdfs.mkdirs(bar);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, fileInBar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.setQuota(sdir1, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        TestRenameWithSnapshots.hdfs.setQuota(sdir2, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        TestRenameWithSnapshots.hdfs.setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        TestRenameWithSnapshots.hdfs.setQuota(bar, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        // create snapshot on root
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, TestRenameWithSnapshots.snap1);
        // delete fileInBar
        TestRenameWithSnapshots.hdfs.delete(fileInBar, true);
        // create another snapshot on root
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, TestRenameWithSnapshots.snap2);
        // /dir1/foo/bar -> /dir2/bar
        final Path bar2 = new Path(sdir2, "bar2");
        TestRenameWithSnapshots.hdfs.rename(bar, bar2);
        // /dir1/foo -> /dir2/bar/foo
        final Path foo2 = new Path(bar2, "foo2");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        restartClusterAndCheckImage(true);
        // delete snapshot snap1
        TestRenameWithSnapshots.hdfs.deleteSnapshot(root, TestRenameWithSnapshots.snap1);
        restartClusterAndCheckImage(true);
    }

    /**
     * After the following operations:
     * Rename a dir -> create a snapshot s on dst tree -> delete the renamed dir
     * -> delete snapshot s on dst tree
     *
     * Make sure we destroy everything created after the rename under the renamed
     * dir.
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_3() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        final Path foo2 = new Path(sdir2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        // create two new files under foo2
        final Path bar2 = new Path(foo2, "bar2");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar2, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        final Path bar3 = new Path(foo2, "bar3");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar3, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // create a new snapshot on sdir2
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir2, "s3");
        // delete foo2
        TestRenameWithSnapshots.hdfs.delete(foo2, true);
        // delete s3
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir2, "s3");
        // check
        final INodeDirectory dir1Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir1.toString()).asDirectory();
        QuotaCounts q1 = dir1Node.getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(3, q1.getNameSpace());
        final INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        QuotaCounts q2 = dir2Node.getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(1, q2.getNameSpace());
        final Path foo_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", foo.getName());
        INode fooRef = TestRenameWithSnapshots.fsdir.getINode(foo_s1.toString());
        Assert.assertTrue((fooRef instanceof INodeReference.WithName));
        INodeReference.WithCount wc = ((WithCount) (fooRef.asReference().getReferredINode()));
        Assert.assertEquals(1, wc.getReferenceCount());
        INodeDirectory fooNode = wc.getReferredINode().asDirectory();
        ReadOnlyList<INode> children = fooNode.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(1, children.size());
        Assert.assertEquals(bar.getName(), children.get(0).getLocalName());
        DiffList<DirectoryDiff> diffList = fooNode.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        Snapshot s1 = dir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        Assert.assertEquals(s1.getId(), diffList.get(0).getSnapshotId());
        ChildrenDiff diff = diffList.get(0).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(0, 0, diff);
        restartClusterAndCheckImage(true);
    }

    /**
     * After the following operations:
     * Rename a dir -> create a snapshot s on dst tree -> rename the renamed dir
     * again -> delete snapshot s on dst tree
     *
     * Make sure we only delete the snapshot s under the renamed dir.
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_4() throws Exception {
        final Path sdir1 = new Path("/dir1");
        final Path sdir2 = new Path("/dir2");
        final Path foo = new Path(sdir1, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        TestRenameWithSnapshots.hdfs.mkdirs(sdir2);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, sdir2, "s2");
        final Path foo2 = new Path(sdir2, "foo");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        // create two new files under foo2
        final Path bar2 = new Path(foo2, "bar2");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar2, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        final Path bar3 = new Path(foo2, "bar3");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar3, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // create a new snapshot on sdir2
        TestRenameWithSnapshots.hdfs.createSnapshot(sdir2, "s3");
        // rename foo2 again
        TestRenameWithSnapshots.hdfs.rename(foo2, foo);
        // delete snapshot s3
        TestRenameWithSnapshots.hdfs.deleteSnapshot(sdir2, "s3");
        // check
        final INodeDirectory dir1Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir1.toString()).asDirectory();
        // sdir1 + s1 + foo_s1 (foo) + foo (foo + s1 + bar~bar3)
        QuotaCounts q1 = dir1Node.getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(7, q1.getNameSpace());
        final INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(sdir2.toString()).asDirectory();
        QuotaCounts q2 = dir2Node.getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(1, q2.getNameSpace());
        final Path foo_s1 = SnapshotTestHelper.getSnapshotPath(sdir1, "s1", foo.getName());
        final INode fooRef = TestRenameWithSnapshots.fsdir.getINode(foo_s1.toString());
        Assert.assertTrue((fooRef instanceof INodeReference.WithName));
        INodeReference.WithCount wc = ((WithCount) (fooRef.asReference().getReferredINode()));
        Assert.assertEquals(2, wc.getReferenceCount());
        INodeDirectory fooNode = wc.getReferredINode().asDirectory();
        ReadOnlyList<INode> children = fooNode.getChildrenList(CURRENT_STATE_ID);
        Assert.assertEquals(3, children.size());
        Assert.assertEquals(bar.getName(), children.get(0).getLocalName());
        Assert.assertEquals(bar2.getName(), children.get(1).getLocalName());
        Assert.assertEquals(bar3.getName(), children.get(2).getLocalName());
        DiffList<DirectoryDiff> diffList = fooNode.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        Snapshot s1 = dir1Node.getSnapshot(DFSUtil.string2Bytes("s1"));
        Assert.assertEquals(s1.getId(), diffList.get(0).getSnapshotId());
        ChildrenDiff diff = diffList.get(0).getChildrenDiff();
        // bar2 and bar3 in the created list
        TestRenameWithSnapshots.assertSizes(2, 0, diff);
        final INode fooRef2 = TestRenameWithSnapshots.fsdir.getINode4Write(foo.toString());
        Assert.assertTrue((fooRef2 instanceof INodeReference.DstReference));
        INodeReference.WithCount wc2 = ((WithCount) (fooRef2.asReference().getReferredINode()));
        Assert.assertSame(wc, wc2);
        Assert.assertSame(fooRef2, wc.getParentReference());
        restartClusterAndCheckImage(true);
    }

    /**
     * This test demonstrates that
     * {@link INodeDirectory#removeChild}
     * and
     * {@link INodeDirectory#addChild}
     * should use {@link INode#isInLatestSnapshot} to check if the
     * added/removed child should be recorded in snapshots.
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_5() throws Exception {
        final Path dir1 = new Path("/dir1");
        final Path dir2 = new Path("/dir2");
        final Path dir3 = new Path("/dir3");
        TestRenameWithSnapshots.hdfs.mkdirs(dir1);
        TestRenameWithSnapshots.hdfs.mkdirs(dir2);
        TestRenameWithSnapshots.hdfs.mkdirs(dir3);
        final Path foo = new Path(dir1, "foo");
        TestRenameWithSnapshots.hdfs.mkdirs(foo);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir1, "s1");
        final Path bar = new Path(foo, "bar");
        // create file bar, and foo will become an INodeDirectory with snapshot
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // delete snapshot s1. now foo is not in any snapshot
        TestRenameWithSnapshots.hdfs.deleteSnapshot(dir1, "s1");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir2, "s2");
        // rename /dir1/foo to /dir2/foo
        final Path foo2 = new Path(dir2, foo.getName());
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        // rename /dir2/foo/bar to /dir3/foo/bar
        final Path bar2 = new Path(dir2, "foo/bar");
        final Path bar3 = new Path(dir3, "bar");
        TestRenameWithSnapshots.hdfs.rename(bar2, bar3);
        // delete /dir2/foo. Since it is not in any snapshot, we will call its
        // destroy function. If we do not use isInLatestSnapshot in removeChild and
        // addChild methods in INodeDirectory (with snapshot), the file bar will be
        // stored in the deleted list of foo, and will be destroyed.
        TestRenameWithSnapshots.hdfs.delete(foo2, true);
        // check if /dir3/bar still exists
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(bar3));
        INodeFile barNode = ((INodeFile) (TestRenameWithSnapshots.fsdir.getINode4Write(bar3.toString())));
        Assert.assertSame(TestRenameWithSnapshots.fsdir.getINode4Write(dir3.toString()), barNode.getParent());
    }

    /**
     * Rename and deletion snapshot under the same the snapshottable directory.
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_6() throws Exception {
        final Path test = new Path("/test");
        final Path dir1 = new Path(test, "dir1");
        final Path dir2 = new Path(test, "dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(dir1);
        TestRenameWithSnapshots.hdfs.mkdirs(dir2);
        final Path foo = new Path(dir2, "foo");
        final Path bar = new Path(foo, "bar");
        final Path file = new Path(bar, "file");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // take a snapshot on /test
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, test, "s0");
        // delete /test/dir2/foo/bar/file after snapshot s0, so that there is a
        // snapshot copy recorded in bar
        TestRenameWithSnapshots.hdfs.delete(file, true);
        // rename foo from dir2 to dir1
        final Path newfoo = new Path(dir1, foo.getName());
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        final Path foo_s0 = SnapshotTestHelper.getSnapshotPath(test, "s0", "dir2/foo");
        Assert.assertTrue((("the snapshot path " + foo_s0) + " should exist"), TestRenameWithSnapshots.hdfs.exists(foo_s0));
        // delete snapshot s0. The deletion will first go down through dir1, and
        // find foo in the created list of dir1. Then it will use null as the prior
        // snapshot and continue the snapshot deletion process in the subtree of
        // foo. We need to make sure the snapshot s0 can be deleted cleanly in the
        // foo subtree.
        TestRenameWithSnapshots.hdfs.deleteSnapshot(test, "s0");
        // check the internal
        Assert.assertFalse((("after deleting s0, " + foo_s0) + " should not exist"), TestRenameWithSnapshots.hdfs.exists(foo_s0));
        INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(dir2.toString()).asDirectory();
        Assert.assertTrue((("the diff list of " + dir2) + " should be empty after deleting s0"), dir2Node.getDiffs().asList().isEmpty());
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(newfoo));
        INode fooRefNode = TestRenameWithSnapshots.fsdir.getINode4Write(newfoo.toString());
        Assert.assertTrue((fooRefNode instanceof INodeReference.DstReference));
        INodeDirectory fooNode = fooRefNode.asDirectory();
        // fooNode should be still INodeDirectory (With Snapshot) since we call
        // recordModification before the rename
        Assert.assertTrue(fooNode.isWithSnapshot());
        Assert.assertTrue(fooNode.getDiffs().asList().isEmpty());
        INodeDirectory barNode = fooNode.getChildrenList(CURRENT_STATE_ID).get(0).asDirectory();
        // bar should also be INodeDirectory (With Snapshot), and both of its diff
        // list and children list are empty
        Assert.assertTrue(barNode.getDiffs().asList().isEmpty());
        Assert.assertTrue(barNode.getChildrenList(CURRENT_STATE_ID).isEmpty());
        restartClusterAndCheckImage(true);
    }

    /**
     * Unit test for HDFS-4842.
     */
    @Test
    public void testRenameDirAndDeleteSnapshot_7() throws Exception {
        TestRenameWithSnapshots.fsn.getSnapshotManager().setAllowNestedSnapshots(true);
        final Path test = new Path("/test");
        final Path dir1 = new Path(test, "dir1");
        final Path dir2 = new Path(test, "dir2");
        TestRenameWithSnapshots.hdfs.mkdirs(dir1);
        TestRenameWithSnapshots.hdfs.mkdirs(dir2);
        final Path foo = new Path(dir2, "foo");
        final Path bar = new Path(foo, "bar");
        final Path file = new Path(bar, "file");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // take a snapshot s0 and s1 on /test
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, test, "s0");
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, test, "s1");
        // delete file so we have a snapshot copy for s1 in bar
        TestRenameWithSnapshots.hdfs.delete(file, true);
        // create another snapshot on dir2
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, dir2, "s2");
        // rename foo from dir2 to dir1
        final Path newfoo = new Path(dir1, foo.getName());
        TestRenameWithSnapshots.hdfs.rename(foo, newfoo);
        // delete snapshot s1
        TestRenameWithSnapshots.hdfs.deleteSnapshot(test, "s1");
        // make sure the snapshot copy of file in s1 is merged to s0. For
        // HDFS-4842, we need to make sure that we do not wrongly use s2 as the
        // prior snapshot of s1.
        final Path file_s2 = SnapshotTestHelper.getSnapshotPath(dir2, "s2", "foo/bar/file");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(file_s2));
        final Path file_s0 = SnapshotTestHelper.getSnapshotPath(test, "s0", "dir2/foo/bar/file");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(file_s0));
        // check dir1: foo should be in the created list of s0
        INodeDirectory dir1Node = TestRenameWithSnapshots.fsdir.getINode4Write(dir1.toString()).asDirectory();
        DiffList<DirectoryDiff> dir1DiffList = dir1Node.getDiffs().asList();
        Assert.assertEquals(1, dir1DiffList.size());
        final ChildrenDiff childrenDiff = dir1DiffList.get(0).getChildrenDiff();
        TestRenameWithSnapshots.assertSizes(1, 0, childrenDiff);
        INode cNode = childrenDiff.getCreatedUnmodifiable().get(0);
        INode fooNode = TestRenameWithSnapshots.fsdir.getINode4Write(newfoo.toString());
        Assert.assertSame(cNode, fooNode);
        // check foo and its subtree
        final Path newbar = new Path(newfoo, bar.getName());
        INodeDirectory barNode = TestRenameWithSnapshots.fsdir.getINode4Write(newbar.toString()).asDirectory();
        Assert.assertSame(fooNode.asDirectory(), barNode.getParent());
        // bar should only have a snapshot diff for s0
        DiffList<DirectoryDiff> barDiffList = barNode.getDiffs().asList();
        Assert.assertEquals(1, barDiffList.size());
        DirectoryDiff diff = barDiffList.get(0);
        INodeDirectory testNode = TestRenameWithSnapshots.fsdir.getINode4Write(test.toString()).asDirectory();
        Snapshot s0 = testNode.getSnapshot(DFSUtil.string2Bytes("s0"));
        Assert.assertEquals(s0.getId(), diff.getSnapshotId());
        // and file should be stored in the deleted list of this snapshot diff
        Assert.assertEquals("file", diff.getChildrenDiff().getDeletedUnmodifiable().get(0).getLocalName());
        // check dir2: a WithName instance for foo should be in the deleted list
        // of the snapshot diff for s2
        INodeDirectory dir2Node = TestRenameWithSnapshots.fsdir.getINode4Write(dir2.toString()).asDirectory();
        DiffList<DirectoryDiff> dir2DiffList = dir2Node.getDiffs().asList();
        // dir2Node should contain 1 snapshot diffs for s2
        Assert.assertEquals(1, dir2DiffList.size());
        final List<INode> dList = dir2DiffList.get(0).getChildrenDiff().getDeletedUnmodifiable();
        Assert.assertEquals(1, dList.size());
        final Path foo_s2 = SnapshotTestHelper.getSnapshotPath(dir2, "s2", foo.getName());
        INodeReference.WithName fooNode_s2 = ((INodeReference.WithName) (TestRenameWithSnapshots.fsdir.getINode(foo_s2.toString())));
        Assert.assertSame(dList.get(0), fooNode_s2);
        Assert.assertSame(fooNode.asReference().getReferredINode(), fooNode_s2.getReferredINode());
        restartClusterAndCheckImage(true);
    }

    /**
     * Make sure we clean the whole subtree under a DstReference node after
     * deleting a snapshot.
     * see HDFS-5476.
     */
    @Test
    public void testCleanDstReference() throws Exception {
        final Path test = new Path("/test");
        final Path foo = new Path(test, "foo");
        final Path bar = new Path(foo, "bar");
        TestRenameWithSnapshots.hdfs.mkdirs(bar);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, test, "s0");
        // create file after s0 so that the file should not be included in s0
        final Path fileInBar = new Path(bar, "file");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, fileInBar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        // rename foo --> foo2
        final Path foo2 = new Path(test, "foo2");
        TestRenameWithSnapshots.hdfs.rename(foo, foo2);
        // create snapshot s1, note the file is included in s1
        TestRenameWithSnapshots.hdfs.createSnapshot(test, "s1");
        // delete bar and foo2
        TestRenameWithSnapshots.hdfs.delete(new Path(foo2, "bar"), true);
        TestRenameWithSnapshots.hdfs.delete(foo2, true);
        final Path sfileInBar = SnapshotTestHelper.getSnapshotPath(test, "s1", "foo2/bar/file");
        Assert.assertTrue(TestRenameWithSnapshots.hdfs.exists(sfileInBar));
        TestRenameWithSnapshots.hdfs.deleteSnapshot(test, "s1");
        Assert.assertFalse(TestRenameWithSnapshots.hdfs.exists(sfileInBar));
        restartClusterAndCheckImage(true);
        // make sure the file under bar is deleted
        final Path barInS0 = SnapshotTestHelper.getSnapshotPath(test, "s0", "foo/bar");
        INodeDirectory barNode = TestRenameWithSnapshots.fsdir.getINode(barInS0.toString()).asDirectory();
        Assert.assertEquals(0, barNode.getChildrenList(CURRENT_STATE_ID).size());
        DiffList<DirectoryDiff> diffList = barNode.getDiffs().asList();
        Assert.assertEquals(1, diffList.size());
        DirectoryDiff diff = diffList.get(0);
        TestRenameWithSnapshots.assertSizes(0, 0, diff.getChildrenDiff());
    }

    /**
     * Rename of the underconstruction file in snapshot should not fail NN restart
     * after checkpoint. Unit test for HDFS-5425.
     */
    @Test
    public void testRenameUCFileInSnapshot() throws Exception {
        final Path test = new Path("/test");
        final Path foo = new Path(test, "foo");
        final Path bar = new Path(foo, "bar");
        TestRenameWithSnapshots.hdfs.mkdirs(foo);
        // create a file and keep it as underconstruction.
        TestRenameWithSnapshots.hdfs.create(bar);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, test, "s0");
        // rename bar --> bar2
        final Path bar2 = new Path(foo, "bar2");
        TestRenameWithSnapshots.hdfs.rename(bar, bar2);
        // save namespace and restart
        restartClusterAndCheckImage(true);
    }

    /**
     * Similar with testRenameUCFileInSnapshot, but do renaming first and then
     * append file without closing it. Unit test for HDFS-5425.
     */
    @Test
    public void testAppendFileAfterRenameInSnapshot() throws Exception {
        final Path test = new Path("/test");
        final Path foo = new Path(test, "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, bar, TestRenameWithSnapshots.BLOCKSIZE, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, test, "s0");
        // rename bar --> bar2
        final Path bar2 = new Path(foo, "bar2");
        TestRenameWithSnapshots.hdfs.rename(bar, bar2);
        // append file and keep it as underconstruction.
        FSDataOutputStream out = TestRenameWithSnapshots.hdfs.append(bar2);
        out.writeByte(0);
        ((DFSOutputStream) (out.getWrappedStream())).hsync(EnumSet.of(UPDATE_LENGTH));
        // save namespace and restart
        restartClusterAndCheckImage(true);
    }

    @Test
    public void testRenameWithOverWrite() throws Exception {
        final Path root = new Path("/");
        final Path foo = new Path(root, "foo");
        final Path file1InFoo = new Path(foo, "file1");
        final Path file2InFoo = new Path(foo, "file2");
        final Path file3InFoo = new Path(foo, "file3");
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file1InFoo, 1L, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file2InFoo, 1L, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        DFSTestUtil.createFile(TestRenameWithSnapshots.hdfs, file3InFoo, 1L, TestRenameWithSnapshots.REPL, TestRenameWithSnapshots.SEED);
        final Path bar = new Path(root, "bar");
        TestRenameWithSnapshots.hdfs.mkdirs(bar);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, "s0");
        // move file1 from foo to bar
        final Path fileInBar = new Path(bar, "file1");
        TestRenameWithSnapshots.hdfs.rename(file1InFoo, fileInBar);
        // rename bar to newDir
        final Path newDir = new Path(root, "newDir");
        TestRenameWithSnapshots.hdfs.rename(bar, newDir);
        // move file2 from foo to newDir
        final Path file2InNewDir = new Path(newDir, "file2");
        TestRenameWithSnapshots.hdfs.rename(file2InFoo, file2InNewDir);
        // move file3 from foo to newDir and rename it to file1, this will overwrite
        // the original file1
        final Path file1InNewDir = new Path(newDir, "file1");
        TestRenameWithSnapshots.hdfs.rename(file3InFoo, file1InNewDir, OVERWRITE);
        SnapshotTestHelper.createSnapshot(TestRenameWithSnapshots.hdfs, root, "s1");
        SnapshotDiffReport report = TestRenameWithSnapshots.hdfs.getSnapshotDiffReport(root, "s0", "s1");
        TestRenameWithSnapshots.LOG.info((("DiffList is \n\"" + (report.toString())) + "\""));
        List<DiffReportEntry> entries = report.getDiffList();
        Assert.assertEquals(7, entries.size());
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, "", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, foo.getName(), null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, MODIFY, bar.getName(), null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, DELETE, "foo/file1", null));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, "bar", "newDir"));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, "foo/file2", "newDir/file2"));
        Assert.assertTrue(TestRenameWithSnapshots.existsInDiffReport(entries, RENAME, "foo/file3", "newDir/file1"));
    }
}

