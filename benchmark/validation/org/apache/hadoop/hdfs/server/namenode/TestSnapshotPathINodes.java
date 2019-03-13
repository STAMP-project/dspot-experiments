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
package org.apache.hadoop.hdfs.server.namenode;


import Path.SEPARATOR;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DFSUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshotException;
import org.apache.hadoop.hdfs.server.namenode.snapshot.Snapshot;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test snapshot related operations.
 */
public class TestSnapshotPathINodes {
    private static final long seed = 0;

    private static final short REPLICATION = 3;

    private static final Path dir = new Path("/TestSnapshot");

    private static final Path sub1 = new Path(TestSnapshotPathINodes.dir, "sub1");

    private static final Path file1 = new Path(TestSnapshotPathINodes.sub1, "file1");

    private static final Path file2 = new Path(TestSnapshotPathINodes.sub1, "file2");

    private static MiniDFSCluster cluster;

    private static FSDirectory fsdir;

    private static DistributedFileSystem hdfs;

    /**
     * Test allow-snapshot operation.
     */
    @Test(timeout = 15000)
    public void testAllowSnapshot() throws Exception {
        final String pathStr = TestSnapshotPathINodes.sub1.toString();
        final INode before = TestSnapshotPathINodes.fsdir.getINode(pathStr);
        // Before a directory is snapshottable
        Assert.assertFalse(before.asDirectory().isSnapshottable());
        // After a directory is snapshottable
        final Path path = new Path(pathStr);
        TestSnapshotPathINodes.hdfs.allowSnapshot(path);
        {
            final INode after = TestSnapshotPathINodes.fsdir.getINode(pathStr);
            Assert.assertTrue(after.asDirectory().isSnapshottable());
        }
        TestSnapshotPathINodes.hdfs.disallowSnapshot(path);
        {
            final INode after = TestSnapshotPathINodes.fsdir.getINode(pathStr);
            Assert.assertFalse(after.asDirectory().isSnapshottable());
        }
    }

    /**
     * for normal (non-snapshot) file.
     */
    @Test(timeout = 15000)
    public void testNonSnapshotPathINodes() throws Exception {
        // Get the inodes by resolving the path of a normal file
        byte[][] components = INode.getPathComponents(TestSnapshotPathINodes.file1.toString());
        INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // The number of inodes should be equal to components.length
        Assert.assertEquals(nodesInPath.length(), components.length);
        // The returned nodesInPath should be non-snapshot
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, false, null, (-1));
        // verify components are correct
        for (int i = 0; i < (components.length); i++) {
            Assert.assertEquals(components[i], nodesInPath.getPathComponent(i));
        }
        // The last INode should be associated with file1
        Assert.assertTrue(((("file1=" + (TestSnapshotPathINodes.file1)) + ", nodesInPath=") + nodesInPath), ((nodesInPath.getINode(((components.length) - 1))) != null));
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 1)).getFullPathName(), TestSnapshotPathINodes.file1.toString());
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 2)).getFullPathName(), TestSnapshotPathINodes.sub1.toString());
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 3)).getFullPathName(), TestSnapshotPathINodes.dir.toString());
        Assert.assertEquals(SEPARATOR, nodesInPath.getPath(0));
        Assert.assertEquals(TestSnapshotPathINodes.dir.toString(), nodesInPath.getPath(1));
        Assert.assertEquals(TestSnapshotPathINodes.sub1.toString(), nodesInPath.getPath(2));
        Assert.assertEquals(TestSnapshotPathINodes.file1.toString(), nodesInPath.getPath(3));
        Assert.assertEquals(TestSnapshotPathINodes.file1.getParent().toString(), nodesInPath.getParentINodesInPath().getPath());
        nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        Assert.assertEquals(nodesInPath.length(), components.length);
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, false, null, (-1));
        Assert.assertEquals(nodesInPath.getLastINode().getFullPathName(), TestSnapshotPathINodes.file1.toString());
    }

    /**
     * for snapshot file.
     */
    @Test(timeout = 15000)
    public void testSnapshotPathINodes() throws Exception {
        // Create a snapshot for the dir, and check the inodes for the path
        // pointing to a snapshot file
        TestSnapshotPathINodes.hdfs.allowSnapshot(TestSnapshotPathINodes.sub1);
        TestSnapshotPathINodes.hdfs.createSnapshot(TestSnapshotPathINodes.sub1, "s1");
        // The path when accessing the snapshot file of file1 is
        // /TestSnapshot/sub1/.snapshot/s1/file1
        String snapshotPath = (TestSnapshotPathINodes.sub1.toString()) + "/.snapshot/s1/file1";
        byte[][] components = INode.getPathComponents(snapshotPath);
        INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // Length of inodes should be (components.length - 1), since we will ignore
        // ".snapshot"
        Assert.assertEquals(nodesInPath.length(), ((components.length) - 1));
        // SnapshotRootIndex should be 3: {root, Testsnapshot, sub1, s1, file1}
        final Snapshot snapshot = TestSnapshotPathINodes.getSnapshot(nodesInPath, "s1", 3);
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, true, snapshot, 3);
        Assert.assertEquals(".snapshot/s1", DFSUtil.bytes2String(nodesInPath.getPathComponent(3)));
        Assert.assertTrue(((nodesInPath.getINode(3)) instanceof Snapshot.Root));
        Assert.assertEquals("s1", nodesInPath.getINode(3).getLocalName());
        // Check the INode for file1 (snapshot file)
        INode snapshotFileNode = nodesInPath.getLastINode();
        TestSnapshotPathINodes.assertINodeFile(snapshotFileNode, TestSnapshotPathINodes.file1);
        Assert.assertTrue(snapshotFileNode.getParent().isWithSnapshot());
        // Call getExistingPathINodes and request only one INode.
        nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        Assert.assertEquals(nodesInPath.length(), ((components.length) - 1));
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, true, snapshot, 3);
        // Check the INode for file1 (snapshot file)
        TestSnapshotPathINodes.assertINodeFile(nodesInPath.getLastINode(), TestSnapshotPathINodes.file1);
        // Resolve the path "/TestSnapshot/sub1/.snapshot"
        String dotSnapshotPath = (TestSnapshotPathINodes.sub1.toString()) + "/.snapshot";
        components = INode.getPathComponents(dotSnapshotPath);
        nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // The number of INodes returned should still be components.length
        // since we put a null in the inode array for ".snapshot"
        Assert.assertEquals(nodesInPath.length(), components.length);
        Assert.assertEquals(".snapshot", DFSUtil.bytes2String(nodesInPath.getLastLocalName()));
        Assert.assertNull(nodesInPath.getLastINode());
        // ensure parent inodes can strip the .snapshot
        Assert.assertEquals(TestSnapshotPathINodes.sub1.toString(), nodesInPath.getParentINodesInPath().getPath());
        // No SnapshotRoot dir is included in the resolved inodes
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, true, snapshot, (-1));
        // The last INode should be null, the last but 1 should be sub1
        Assert.assertNull(nodesInPath.getLastINode());
        Assert.assertEquals(nodesInPath.getINode((-2)).getFullPathName(), TestSnapshotPathINodes.sub1.toString());
        Assert.assertTrue(nodesInPath.getINode((-2)).isDirectory());
        String[] invalidPathComponent = new String[]{ "invalidDir", "foo", ".snapshot", "bar" };
        Path invalidPath = new Path(invalidPathComponent[0]);
        for (int i = 1; i < (invalidPathComponent.length); i++) {
            invalidPath = new Path(invalidPath, invalidPathComponent[i]);
            try {
                TestSnapshotPathINodes.hdfs.getFileStatus(invalidPath);
                Assert.fail();
            } catch (FileNotFoundException fnfe) {
                System.out.println(("The exception is expected: " + fnfe));
            }
        }
        TestSnapshotPathINodes.hdfs.deleteSnapshot(TestSnapshotPathINodes.sub1, "s1");
        TestSnapshotPathINodes.hdfs.disallowSnapshot(TestSnapshotPathINodes.sub1);
    }

    /**
     * for snapshot file after deleting the original file.
     */
    @Test(timeout = 15000)
    public void testSnapshotPathINodesAfterDeletion() throws Exception {
        // Create a snapshot for the dir, and check the inodes for the path
        // pointing to a snapshot file
        TestSnapshotPathINodes.hdfs.allowSnapshot(TestSnapshotPathINodes.sub1);
        TestSnapshotPathINodes.hdfs.createSnapshot(TestSnapshotPathINodes.sub1, "s2");
        // Delete the original file /TestSnapshot/sub1/file1
        TestSnapshotPathINodes.hdfs.delete(TestSnapshotPathINodes.file1, false);
        final Snapshot snapshot;
        {
            // Resolve the path for the snapshot file
            // /TestSnapshot/sub1/.snapshot/s2/file1
            String snapshotPath = (TestSnapshotPathINodes.sub1.toString()) + "/.snapshot/s2/file1";
            byte[][] components = INode.getPathComponents(snapshotPath);
            INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
            // Length of inodes should be (components.length - 1), since we will ignore
            // ".snapshot"
            Assert.assertEquals(nodesInPath.length(), ((components.length) - 1));
            // SnapshotRootIndex should be 3: {root, Testsnapshot, sub1, s2, file1}
            snapshot = TestSnapshotPathINodes.getSnapshot(nodesInPath, "s2", 3);
            TestSnapshotPathINodes.assertSnapshot(nodesInPath, true, snapshot, 3);
            // Check the INode for file1 (snapshot file)
            final INode inode = nodesInPath.getLastINode();
            Assert.assertEquals(TestSnapshotPathINodes.file1.getName(), inode.getLocalName());
            Assert.assertTrue(inode.asFile().isWithSnapshot());
        }
        // Check the INodes for path /TestSnapshot/sub1/file1
        byte[][] components = INode.getPathComponents(TestSnapshotPathINodes.file1.toString());
        INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // The length of inodes should be equal to components.length
        Assert.assertEquals(nodesInPath.length(), components.length);
        // The number of non-null elements should be components.length - 1 since
        // file1 has been deleted
        Assert.assertEquals(getNumNonNull(nodesInPath), ((components.length) - 1));
        // The returned nodesInPath should be non-snapshot
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, false, snapshot, (-1));
        // The last INode should be null, and the one before should be associated
        // with sub1
        Assert.assertNull(nodesInPath.getINode(((components.length) - 1)));
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 2)).getFullPathName(), TestSnapshotPathINodes.sub1.toString());
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 3)).getFullPathName(), TestSnapshotPathINodes.dir.toString());
        TestSnapshotPathINodes.hdfs.deleteSnapshot(TestSnapshotPathINodes.sub1, "s2");
        TestSnapshotPathINodes.hdfs.disallowSnapshot(TestSnapshotPathINodes.sub1);
    }

    /**
     * for snapshot file while adding a new file after snapshot.
     */
    @Test(timeout = 15000)
    public void testSnapshotPathINodesWithAddedFile() throws Exception {
        // Create a snapshot for the dir, and check the inodes for the path
        // pointing to a snapshot file
        TestSnapshotPathINodes.hdfs.allowSnapshot(TestSnapshotPathINodes.sub1);
        TestSnapshotPathINodes.hdfs.createSnapshot(TestSnapshotPathINodes.sub1, "s4");
        // Add a new file /TestSnapshot/sub1/file3
        final Path file3 = new Path(TestSnapshotPathINodes.sub1, "file3");
        DFSTestUtil.createFile(TestSnapshotPathINodes.hdfs, file3, 1024, TestSnapshotPathINodes.REPLICATION, TestSnapshotPathINodes.seed);
        Snapshot s4;
        {
            // Check the inodes for /TestSnapshot/sub1/.snapshot/s4/file3
            String snapshotPath = (TestSnapshotPathINodes.sub1.toString()) + "/.snapshot/s4/file3";
            byte[][] components = INode.getPathComponents(snapshotPath);
            INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
            // Length of inodes should be (components.length - 1), since we will ignore
            // ".snapshot"
            Assert.assertEquals(nodesInPath.length(), ((components.length) - 1));
            // The number of non-null inodes should be components.length - 2, since
            // snapshot of file3 does not exist
            Assert.assertEquals(getNumNonNull(nodesInPath), ((components.length) - 2));
            s4 = TestSnapshotPathINodes.getSnapshot(nodesInPath, "s4", 3);
            // SnapshotRootIndex should still be 3: {root, Testsnapshot, sub1, s4, null}
            TestSnapshotPathINodes.assertSnapshot(nodesInPath, true, s4, 3);
            // Check the last INode in inodes, which should be null
            Assert.assertNull(nodesInPath.getINode(((nodesInPath.length()) - 1)));
        }
        // Check the inodes for /TestSnapshot/sub1/file3
        byte[][] components = INode.getPathComponents(file3.toString());
        INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // The number of inodes should be equal to components.length
        Assert.assertEquals(nodesInPath.length(), components.length);
        // The returned nodesInPath should be non-snapshot
        TestSnapshotPathINodes.assertSnapshot(nodesInPath, false, s4, (-1));
        // The last INode should be associated with file3
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 1)).getFullPathName(), file3.toString());
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 2)).getFullPathName(), TestSnapshotPathINodes.sub1.toString());
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 3)).getFullPathName(), TestSnapshotPathINodes.dir.toString());
        TestSnapshotPathINodes.hdfs.deleteSnapshot(TestSnapshotPathINodes.sub1, "s4");
        TestSnapshotPathINodes.hdfs.disallowSnapshot(TestSnapshotPathINodes.sub1);
    }

    /**
     * for snapshot file while modifying file after snapshot.
     */
    @Test(timeout = 15000)
    public void testSnapshotPathINodesAfterModification() throws Exception {
        // First check the INode for /TestSnapshot/sub1/file1
        byte[][] components = INode.getPathComponents(TestSnapshotPathINodes.file1.toString());
        INodesInPath nodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // The number of inodes should be equal to components.length
        Assert.assertEquals(nodesInPath.length(), components.length);
        // The last INode should be associated with file1
        Assert.assertEquals(nodesInPath.getINode(((components.length) - 1)).getFullPathName(), TestSnapshotPathINodes.file1.toString());
        // record the modification time of the inode
        final long modTime = nodesInPath.getINode(((nodesInPath.length()) - 1)).getModificationTime();
        // Create a snapshot for the dir, and check the inodes for the path
        // pointing to a snapshot file
        TestSnapshotPathINodes.hdfs.allowSnapshot(TestSnapshotPathINodes.sub1);
        TestSnapshotPathINodes.hdfs.createSnapshot(TestSnapshotPathINodes.sub1, "s3");
        // Modify file1
        DFSTestUtil.appendFile(TestSnapshotPathINodes.hdfs, TestSnapshotPathINodes.file1, "the content for appending");
        // Check the INodes for snapshot of file1
        String snapshotPath = (TestSnapshotPathINodes.sub1.toString()) + "/.snapshot/s3/file1";
        components = INode.getPathComponents(snapshotPath);
        INodesInPath ssNodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        // Length of ssInodes should be (components.length - 1), since we will
        // ignore ".snapshot"
        Assert.assertEquals(ssNodesInPath.length(), ((components.length) - 1));
        final Snapshot s3 = TestSnapshotPathINodes.getSnapshot(ssNodesInPath, "s3", 3);
        TestSnapshotPathINodes.assertSnapshot(ssNodesInPath, true, s3, 3);
        // Check the INode for snapshot of file1
        INode snapshotFileNode = ssNodesInPath.getLastINode();
        Assert.assertEquals(snapshotFileNode.getLocalName(), TestSnapshotPathINodes.file1.getName());
        Assert.assertTrue(snapshotFileNode.asFile().isWithSnapshot());
        // The modification time of the snapshot INode should be the same with the
        // original INode before modification
        Assert.assertEquals(modTime, snapshotFileNode.getModificationTime(ssNodesInPath.getPathSnapshotId()));
        // Check the INode for /TestSnapshot/sub1/file1 again
        components = INode.getPathComponents(TestSnapshotPathINodes.file1.toString());
        INodesInPath newNodesInPath = INodesInPath.resolve(TestSnapshotPathINodes.fsdir.rootDir, components, false);
        TestSnapshotPathINodes.assertSnapshot(newNodesInPath, false, s3, (-1));
        // The number of inodes should be equal to components.length
        Assert.assertEquals(newNodesInPath.length(), components.length);
        // The last INode should be associated with file1
        final int last = (components.length) - 1;
        Assert.assertEquals(newNodesInPath.getINode(last).getFullPathName(), TestSnapshotPathINodes.file1.toString());
        // The modification time of the INode for file3 should have been changed
        Assert.assertFalse((modTime == (newNodesInPath.getINode(last).getModificationTime())));
        TestSnapshotPathINodes.hdfs.deleteSnapshot(TestSnapshotPathINodes.sub1, "s3");
        TestSnapshotPathINodes.hdfs.disallowSnapshot(TestSnapshotPathINodes.sub1);
    }

    @Test
    public void testShortCircuitSnapshotSearch() throws SnapshotException {
        FSNamesystem fsn = TestSnapshotPathINodes.cluster.getNamesystem();
        SnapshotManager sm = fsn.getSnapshotManager();
        Assert.assertEquals(0, sm.getNumSnapshottableDirs());
        INodesInPath iip = Mockito.mock(INodesInPath.class);
        List<INodeDirectory> snapDirs = new ArrayList<>();
        FSDirSnapshotOp.checkSnapshot(fsn.getFSDirectory(), iip, snapDirs);
        Mockito.verifyZeroInteractions(iip);
    }
}

