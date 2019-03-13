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


import HdfsConstants.DOT_SNAPSHOT_DIR;
import HdfsConstants.QUOTA_DONT_SET;
import Snapshot.ID_COMPARATOR;
import java.io.IOException;
import java.util.Random;
import java.util.regex.Pattern;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.fs.permission.PermissionStatus;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshotException;
import org.apache.hadoop.hdfs.server.namenode.EditLogFileOutputStream;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.INode;
import org.apache.hadoop.hdfs.server.namenode.INodeDirectory;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing nested snapshots.
 */
public class TestNestedSnapshots {
    static {
        // These tests generate a large number of edits, and repeated edit log
        // flushes can be a bottleneck.
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    {
        SnapshotTestHelper.disableLogs();
    }

    private static final long SEED = 0;

    private static final Random RANDOM = new Random(TestNestedSnapshots.SEED);

    private static final short REPLICATION = 3;

    private static final long BLOCKSIZE = 1024;

    private static final Configuration conf = new Configuration();

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem hdfs;

    /**
     * Create a snapshot for /test/foo and create another snapshot for
     * /test/foo/bar.  Files created before the snapshots should appear in both
     * snapshots and the files created after the snapshots should not appear in
     * any of the snapshots.
     */
    @Test(timeout = 300000)
    public void testNestedSnapshots() throws Exception {
        TestNestedSnapshots.cluster.getNamesystem().getSnapshotManager().setAllowNestedSnapshots(true);
        final Path foo = new Path("/testNestedSnapshots/foo");
        final Path bar = new Path(foo, "bar");
        final Path file1 = new Path(bar, "file1");
        DFSTestUtil.createFile(TestNestedSnapshots.hdfs, file1, TestNestedSnapshots.BLOCKSIZE, TestNestedSnapshots.REPLICATION, TestNestedSnapshots.SEED);
        TestNestedSnapshots.print(("create file " + file1));
        final String s1name = "foo-s1";
        final Path s1path = SnapshotTestHelper.getSnapshotRoot(foo, s1name);
        TestNestedSnapshots.hdfs.allowSnapshot(foo);
        TestNestedSnapshots.print(("allow snapshot " + foo));
        TestNestedSnapshots.hdfs.createSnapshot(foo, s1name);
        TestNestedSnapshots.print(("create snapshot " + s1name));
        final String s2name = "bar-s2";
        final Path s2path = SnapshotTestHelper.getSnapshotRoot(bar, s2name);
        TestNestedSnapshots.hdfs.allowSnapshot(bar);
        TestNestedSnapshots.print(("allow snapshot " + bar));
        TestNestedSnapshots.hdfs.createSnapshot(bar, s2name);
        TestNestedSnapshots.print(("create snapshot " + s2name));
        final Path file2 = new Path(bar, "file2");
        DFSTestUtil.createFile(TestNestedSnapshots.hdfs, file2, TestNestedSnapshots.BLOCKSIZE, TestNestedSnapshots.REPLICATION, TestNestedSnapshots.SEED);
        TestNestedSnapshots.print(("create file " + file2));
        TestNestedSnapshots.assertFile(s1path, s2path, file1, true, true, true);
        TestNestedSnapshots.assertFile(s1path, s2path, file2, true, false, false);
        // test root
        final String rootStr = "/";
        final Path rootPath = new Path(rootStr);
        TestNestedSnapshots.hdfs.allowSnapshot(rootPath);
        TestNestedSnapshots.print(("allow snapshot " + rootStr));
        final Path rootSnapshot = TestNestedSnapshots.hdfs.createSnapshot(rootPath);
        TestNestedSnapshots.print(("create snapshot " + rootSnapshot));
        TestNestedSnapshots.hdfs.deleteSnapshot(rootPath, rootSnapshot.getName());
        TestNestedSnapshots.print(("delete snapshot " + rootSnapshot));
        TestNestedSnapshots.hdfs.disallowSnapshot(rootPath);
        TestNestedSnapshots.print(("disallow snapshot " + rootStr));
        // change foo to non-snapshottable
        TestNestedSnapshots.hdfs.deleteSnapshot(foo, s1name);
        TestNestedSnapshots.hdfs.disallowSnapshot(foo);
        // test disallow nested snapshots
        TestNestedSnapshots.cluster.getNamesystem().getSnapshotManager().setAllowNestedSnapshots(false);
        try {
            TestNestedSnapshots.hdfs.allowSnapshot(rootPath);
            Assert.fail();
        } catch (SnapshotException se) {
            TestNestedSnapshots.assertNestedSnapshotException(se, "subdirectory");
        }
        try {
            TestNestedSnapshots.hdfs.allowSnapshot(foo);
            Assert.fail();
        } catch (SnapshotException se) {
            TestNestedSnapshots.assertNestedSnapshotException(se, "subdirectory");
        }
        final Path sub1Bar = new Path(bar, "sub1");
        final Path sub2Bar = new Path(sub1Bar, "sub2");
        TestNestedSnapshots.hdfs.mkdirs(sub2Bar);
        try {
            TestNestedSnapshots.hdfs.allowSnapshot(sub1Bar);
            Assert.fail();
        } catch (SnapshotException se) {
            TestNestedSnapshots.assertNestedSnapshotException(se, "ancestor");
        }
        try {
            TestNestedSnapshots.hdfs.allowSnapshot(sub2Bar);
            Assert.fail();
        } catch (SnapshotException se) {
            TestNestedSnapshots.assertNestedSnapshotException(se, "ancestor");
        }
    }

    /**
     * Test the snapshot limit of a single snapshottable directory.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSnapshotLimit() throws Exception {
        final int step = 1000;
        final String dirStr = "/testSnapshotLimit/dir";
        final Path dir = new Path(dirStr);
        TestNestedSnapshots.hdfs.mkdirs(dir, new FsPermission(((short) (511))));
        TestNestedSnapshots.hdfs.allowSnapshot(dir);
        int s = 0;
        for (; s < (DirectorySnapshottableFeature.SNAPSHOT_QUOTA_DEFAULT); s++) {
            final String snapshotName = "s" + s;
            TestNestedSnapshots.hdfs.createSnapshot(dir, snapshotName);
            // create a file occasionally
            if ((s % step) == 0) {
                final Path file = new Path(dirStr, ("f" + s));
                DFSTestUtil.createFile(TestNestedSnapshots.hdfs, file, TestNestedSnapshots.BLOCKSIZE, TestNestedSnapshots.REPLICATION, TestNestedSnapshots.SEED);
            }
        }
        try {
            TestNestedSnapshots.hdfs.createSnapshot(dir, ("s" + s));
            Assert.fail("Expected to fail to create snapshot, but didn't.");
        } catch (IOException ioe) {
            SnapshotTestHelper.LOG.info("The exception is expected.", ioe);
        }
        for (int f = 0; f < (DirectorySnapshottableFeature.SNAPSHOT_QUOTA_DEFAULT); f += step) {
            final String file = "f" + f;
            s = TestNestedSnapshots.RANDOM.nextInt(step);
            for (; s < (DirectorySnapshottableFeature.SNAPSHOT_QUOTA_DEFAULT); s += TestNestedSnapshots.RANDOM.nextInt(step)) {
                final Path p = SnapshotTestHelper.getSnapshotPath(dir, ("s" + s), file);
                // the file #f exists in snapshot #s iff s > f.
                Assert.assertEquals((s > f), TestNestedSnapshots.hdfs.exists(p));
            }
        }
    }

    @Test(timeout = 300000)
    public void testSnapshotName() throws Exception {
        final String dirStr = "/testSnapshotWithQuota/dir";
        final Path dir = new Path(dirStr);
        TestNestedSnapshots.hdfs.mkdirs(dir, new FsPermission(((short) (511))));
        TestNestedSnapshots.hdfs.allowSnapshot(dir);
        // set namespace quota
        final int NS_QUOTA = 6;
        TestNestedSnapshots.hdfs.setQuota(dir, NS_QUOTA, QUOTA_DONT_SET);
        // create object to use up the quota.
        final Path foo = new Path(dir, "foo");
        final Path f1 = new Path(foo, "f1");
        DFSTestUtil.createFile(TestNestedSnapshots.hdfs, f1, TestNestedSnapshots.BLOCKSIZE, TestNestedSnapshots.REPLICATION, TestNestedSnapshots.SEED);
        {
            // create a snapshot with default snapshot name
            final Path snapshotPath = TestNestedSnapshots.hdfs.createSnapshot(dir);
            // check snapshot path and the default snapshot name
            final String snapshotName = snapshotPath.getName();
            Assert.assertTrue(("snapshotName=" + snapshotName), Pattern.matches("s\\d\\d\\d\\d\\d\\d\\d\\d-\\d\\d\\d\\d\\d\\d\\.\\d\\d\\d", snapshotName));
            final Path parent = snapshotPath.getParent();
            Assert.assertEquals(DOT_SNAPSHOT_DIR, parent.getName());
            Assert.assertEquals(dir, parent.getParent());
        }
    }

    /**
     * Test {@link Snapshot#ID_COMPARATOR}.
     */
    @Test(timeout = 300000)
    public void testIdCmp() {
        final PermissionStatus perm = PermissionStatus.createImmutable("user", "group", FsPermission.createImmutable(((short) (0))));
        final INodeDirectory snapshottable = new INodeDirectory(0, DFSUtil.string2Bytes("foo"), perm, 0L);
        snapshottable.addSnapshottableFeature();
        final Snapshot[] snapshots = new Snapshot[]{ new Snapshot(1, "s1", snapshottable), new Snapshot(1, "s1", snapshottable), new Snapshot(2, "s2", snapshottable), new Snapshot(2, "s2", snapshottable) };
        Assert.assertEquals(0, ID_COMPARATOR.compare(null, null));
        for (Snapshot s : snapshots) {
            Assert.assertTrue(((ID_COMPARATOR.compare(null, s)) > 0));
            Assert.assertTrue(((ID_COMPARATOR.compare(s, null)) < 0));
            for (Snapshot t : snapshots) {
                final int expected = s.getRoot().getLocalName().compareTo(t.getRoot().getLocalName());
                final int computed = ID_COMPARATOR.compare(s, t);
                Assert.assertEquals((expected > 0), (computed > 0));
                Assert.assertEquals((expected == 0), (computed == 0));
                Assert.assertEquals((expected < 0), (computed < 0));
            }
        }
    }

    /**
     * When we have nested snapshottable directories and if we try to reset the
     * snapshottable descendant back to an regular directory, we need to replace
     * the snapshottable descendant with an INodeDirectoryWithSnapshot
     */
    @Test
    public void testDisallowNestedSnapshottableDir() throws Exception {
        TestNestedSnapshots.cluster.getNamesystem().getSnapshotManager().setAllowNestedSnapshots(true);
        final Path dir = new Path("/dir");
        final Path sub = new Path(dir, "sub");
        TestNestedSnapshots.hdfs.mkdirs(sub);
        SnapshotTestHelper.createSnapshot(TestNestedSnapshots.hdfs, dir, "s1");
        final Path file = new Path(sub, "file");
        DFSTestUtil.createFile(TestNestedSnapshots.hdfs, file, TestNestedSnapshots.BLOCKSIZE, TestNestedSnapshots.REPLICATION, TestNestedSnapshots.SEED);
        FSDirectory fsdir = TestNestedSnapshots.cluster.getNamesystem().getFSDirectory();
        INode subNode = fsdir.getINode(sub.toString());
        Assert.assertTrue(subNode.asDirectory().isWithSnapshot());
        TestNestedSnapshots.hdfs.allowSnapshot(sub);
        subNode = fsdir.getINode(sub.toString());
        Assert.assertTrue(((subNode.isDirectory()) && (subNode.asDirectory().isSnapshottable())));
        TestNestedSnapshots.hdfs.disallowSnapshot(sub);
        subNode = fsdir.getINode(sub.toString());
        Assert.assertTrue(subNode.asDirectory().isWithSnapshot());
    }
}

