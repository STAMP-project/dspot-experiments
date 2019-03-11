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


import DirectorySnapshottableFeature.SNAPSHOT_QUOTA_DEFAULT;
import HdfsDataOutputStream.SyncFlag.UPDATE_LENGTH;
import INode.LOG;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Random;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.protocol.HdfsConstants;
import org.apache.hadoop.hdfs.server.namenode.FSDirectory;
import org.apache.hadoop.hdfs.server.namenode.FSImageTestUtil;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.INodeDirectory;
import org.apache.hadoop.hdfs.tools.offlineImageViewer.PBImageXmlWriter;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.event.Level;


/**
 * This class tests snapshot functionality. One or multiple snapshots are
 * created. The snapshotted directory is changed and verification is done to
 * ensure snapshots remain unchanges.
 */
public class TestSnapshot {
    {
        GenericTestUtils.setLogLevel(LOG, Level.TRACE);
        SnapshotTestHelper.disableLogs();
    }

    private static final long seed;

    private static final Random random;

    static {
        seed = Time.now();
        random = new Random(TestSnapshot.seed);
        System.out.println(("Random seed: " + (TestSnapshot.seed)));
    }

    protected static final short REPLICATION = 3;

    protected static final int BLOCKSIZE = 1024;

    /**
     * The number of times snapshots are created for a snapshottable directory
     */
    public static final int SNAPSHOT_ITERATION_NUMBER = 20;

    /**
     * Height of directory tree used for testing
     */
    public static final int DIRECTORY_TREE_LEVEL = 5;

    protected Configuration conf;

    protected static MiniDFSCluster cluster;

    protected static FSNamesystem fsn;

    protected static FSDirectory fsdir;

    protected DistributedFileSystem hdfs;

    private static final String testDir = GenericTestUtils.getTestDir().getAbsolutePath();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * The list recording all previous snapshots. Each element in the array
     * records a snapshot root.
     */
    protected static final ArrayList<Path> snapshotList = new ArrayList<Path>();

    /**
     * Check {@link SnapshotTestHelper.TestDirectoryTree}
     */
    private SnapshotTestHelper.TestDirectoryTree dirTree;

    static int modificationCount = 0;

    /**
     * Main test, where we will go in the following loop:
     * <pre>
     *    Create snapshot and check the creation <--+
     * -> Change the current/live files/dir         |
     * -> Check previous snapshots -----------------+
     * </pre>
     */
    @Test
    public void testSnapshot() throws Throwable {
        try {
            runTestSnapshot(TestSnapshot.SNAPSHOT_ITERATION_NUMBER);
        } catch (Throwable t) {
            SnapshotTestHelper.LOG.info("FAILED", t);
            SnapshotTestHelper.dumpTree("FAILED", TestSnapshot.cluster);
            throw t;
        }
    }

    /**
     * Test if the OfflineImageViewerPB can correctly parse a fsimage containing
     * snapshots
     */
    @Test
    public void testOfflineImageViewer() throws Exception {
        runTestSnapshot(1);
        // retrieve the fsimage. Note that we already save namespace to fsimage at
        // the end of each iteration of runTestSnapshot.
        File originalFsimage = FSImageTestUtil.findLatestImageFile(FSImageTestUtil.getFSImage(TestSnapshot.cluster.getNameNode()).getStorage().getStorageDir(0));
        Assert.assertNotNull("Didn't generate or can't find fsimage", originalFsimage);
        PrintStream o = new PrintStream(NullOutputStream.NULL_OUTPUT_STREAM);
        PBImageXmlWriter v = new PBImageXmlWriter(new Configuration(), o);
        v.visit(new RandomAccessFile(originalFsimage, "r"));
    }

    /**
     * A simple test that updates a sub-directory of a snapshottable directory
     * with snapshots
     */
    @Test(timeout = 60000)
    public void testUpdateDirectory() throws Exception {
        Path dir = new Path("/dir");
        Path sub = new Path(dir, "sub");
        Path subFile = new Path(sub, "file");
        DFSTestUtil.createFile(hdfs, subFile, TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed);
        FileStatus oldStatus = hdfs.getFileStatus(sub);
        hdfs.allowSnapshot(dir);
        hdfs.createSnapshot(dir, "s1");
        hdfs.setTimes(sub, 100L, 100L);
        Path snapshotPath = SnapshotTestHelper.getSnapshotPath(dir, "s1", "sub");
        FileStatus snapshotStatus = hdfs.getFileStatus(snapshotPath);
        Assert.assertEquals(oldStatus.getModificationTime(), snapshotStatus.getModificationTime());
        Assert.assertEquals(oldStatus.getAccessTime(), snapshotStatus.getAccessTime());
    }

    /**
     * Test creating a snapshot with illegal name
     */
    @Test
    public void testCreateSnapshotWithIllegalName() throws Exception {
        final Path dir = new Path("/dir");
        hdfs.mkdirs(dir);
        final String name1 = HdfsConstants.DOT_SNAPSHOT_DIR;
        try {
            hdfs.createSnapshot(dir, name1);
            Assert.fail("Exception expected when an illegal name is given");
        } catch (RemoteException e) {
            String errorMsg = "Invalid path name Invalid snapshot name: " + name1;
            GenericTestUtils.assertExceptionContains(errorMsg, e);
        }
        final String[] badNames = new String[]{ "foo" + (Path.SEPARATOR), (Path.SEPARATOR) + "foo", Path.SEPARATOR, ("foo" + (Path.SEPARATOR)) + "bar" };
        for (String badName : badNames) {
            try {
                hdfs.createSnapshot(dir, badName);
                Assert.fail("Exception expected when an illegal name is given");
            } catch (RemoteException e) {
                String errorMsg = "Invalid path name Invalid snapshot name: " + badName;
                GenericTestUtils.assertExceptionContains(errorMsg, e);
            }
        }
    }

    /**
     * Creating snapshots for a directory that is not snapshottable must fail.
     */
    @Test(timeout = 60000)
    public void testSnapshottableDirectory() throws Exception {
        Path dir = new Path("/TestSnapshot/sub");
        Path file0 = new Path(dir, "file0");
        Path file1 = new Path(dir, "file1");
        DFSTestUtil.createFile(hdfs, file0, TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed);
        DFSTestUtil.createFile(hdfs, file1, TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed);
        try {
            hdfs.createSnapshot(dir, "s1");
            Assert.fail((("Exception expected: " + dir) + " is not snapshottable"));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains(("Directory is not a snapshottable directory: " + dir), e);
        }
        try {
            hdfs.deleteSnapshot(dir, "s1");
            Assert.fail((("Exception expected: " + dir) + " is not a snapshottale dir"));
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains(("Directory is not a snapshottable directory: " + dir), e);
        }
        try {
            hdfs.renameSnapshot(dir, "s1", "s2");
            Assert.fail((("Exception expected: " + dir) + " is not a snapshottale dir"));
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains(("Directory is not a snapshottable directory: " + dir), e);
        }
    }

    /**
     * Test multiple calls of allowSnapshot and disallowSnapshot, to make sure
     * they are idempotent
     */
    @Test
    public void testAllowAndDisallowSnapshot() throws Exception {
        final Path dir = new Path("/dir");
        final Path file0 = new Path(dir, "file0");
        final Path file1 = new Path(dir, "file1");
        DFSTestUtil.createFile(hdfs, file0, TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed);
        DFSTestUtil.createFile(hdfs, file1, TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed);
        INodeDirectory dirNode = TestSnapshot.fsdir.getINode4Write(dir.toString()).asDirectory();
        Assert.assertFalse(dirNode.isSnapshottable());
        hdfs.allowSnapshot(dir);
        dirNode = TestSnapshot.fsdir.getINode4Write(dir.toString()).asDirectory();
        Assert.assertTrue(dirNode.isSnapshottable());
        // call allowSnapshot again
        hdfs.allowSnapshot(dir);
        dirNode = TestSnapshot.fsdir.getINode4Write(dir.toString()).asDirectory();
        Assert.assertTrue(dirNode.isSnapshottable());
        // disallowSnapshot on dir
        hdfs.disallowSnapshot(dir);
        dirNode = TestSnapshot.fsdir.getINode4Write(dir.toString()).asDirectory();
        Assert.assertFalse(dirNode.isSnapshottable());
        // do it again
        hdfs.disallowSnapshot(dir);
        dirNode = TestSnapshot.fsdir.getINode4Write(dir.toString()).asDirectory();
        Assert.assertFalse(dirNode.isSnapshottable());
        // same process on root
        final Path root = new Path("/");
        INodeDirectory rootNode = TestSnapshot.fsdir.getINode4Write(root.toString()).asDirectory();
        Assert.assertTrue(rootNode.isSnapshottable());
        // root is snapshottable dir, but with 0 snapshot quota
        Assert.assertEquals(0, rootNode.getDirectorySnapshottableFeature().getSnapshotQuota());
        hdfs.allowSnapshot(root);
        rootNode = TestSnapshot.fsdir.getINode4Write(root.toString()).asDirectory();
        Assert.assertTrue(rootNode.isSnapshottable());
        Assert.assertEquals(SNAPSHOT_QUOTA_DEFAULT, rootNode.getDirectorySnapshottableFeature().getSnapshotQuota());
        // call allowSnapshot again
        hdfs.allowSnapshot(root);
        rootNode = TestSnapshot.fsdir.getINode4Write(root.toString()).asDirectory();
        Assert.assertTrue(rootNode.isSnapshottable());
        Assert.assertEquals(SNAPSHOT_QUOTA_DEFAULT, rootNode.getDirectorySnapshottableFeature().getSnapshotQuota());
        // disallowSnapshot on dir
        hdfs.disallowSnapshot(root);
        rootNode = TestSnapshot.fsdir.getINode4Write(root.toString()).asDirectory();
        Assert.assertTrue(rootNode.isSnapshottable());
        Assert.assertEquals(0, rootNode.getDirectorySnapshottableFeature().getSnapshotQuota());
        // do it again
        hdfs.disallowSnapshot(root);
        rootNode = TestSnapshot.fsdir.getINode4Write(root.toString()).asDirectory();
        Assert.assertTrue(rootNode.isSnapshottable());
        Assert.assertEquals(0, rootNode.getDirectorySnapshottableFeature().getSnapshotQuota());
    }

    private static int snapshotCount = 0;

    /**
     * Base class to present changes applied to current file/dir. A modification
     * can be file creation, deletion, or other modifications such as appending on
     * an existing file. Three abstract methods need to be implemented by
     * subclasses: loadSnapshots() captures the states of snapshots before the
     * modification, modify() applies the modification to the current directory,
     * and checkSnapshots() verifies the snapshots do not change after the
     * modification.
     */
    abstract static class Modification {
        protected final Path file;

        protected final FileSystem fs;

        final String type;

        Modification(Path file, FileSystem fs, String type) {
            this.file = file;
            this.fs = fs;
            this.type = type;
        }

        abstract void loadSnapshots() throws Exception;

        abstract void modify() throws Exception;

        abstract void checkSnapshots() throws Exception;

        @Override
        public String toString() {
            return ((((getClass().getSimpleName()) + ":") + (type)) + ":") + (file);
        }
    }

    /**
     * Modifications that change the file status. We check the FileStatus of
     * snapshot files before/after the modification.
     */
    abstract static class FileStatusChange extends TestSnapshot.Modification {
        protected final HashMap<Path, FileStatus> statusMap;

        FileStatusChange(Path file, FileSystem fs, String type) {
            super(file, fs, type);
            statusMap = new HashMap<Path, FileStatus>();
        }

        @Override
        void loadSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                Path snapshotFile = SnapshotTestHelper.getSnapshotFile(snapshotRoot, file);
                if (snapshotFile != null) {
                    if (fs.exists(snapshotFile)) {
                        FileStatus status = fs.getFileStatus(snapshotFile);
                        statusMap.put(snapshotFile, status);
                    } else {
                        statusMap.put(snapshotFile, null);
                    }
                }
            }
        }

        @Override
        void checkSnapshots() throws Exception {
            for (Path snapshotFile : statusMap.keySet()) {
                FileStatus currentStatus = (fs.exists(snapshotFile)) ? fs.getFileStatus(snapshotFile) : null;
                FileStatus originalStatus = statusMap.get(snapshotFile);
                Assert.assertEquals(currentStatus, originalStatus);
                if (currentStatus != null) {
                    String s = null;
                    if (!(currentStatus.toString().equals(originalStatus.toString()))) {
                        s = (((((((((((("FAILED: " + (getClass().getSimpleName())) + ": file=") + (file)) + ", snapshotFile") + snapshotFile) + "\n\n currentStatus = ") + currentStatus) + "\noriginalStatus = ") + originalStatus) + "\n\nfile        : ") + (TestSnapshot.fsdir.getINode(file.toString()).toDetailString())) + "\n\nsnapshotFile: ") + (TestSnapshot.fsdir.getINode(snapshotFile.toString()).toDetailString());
                        SnapshotTestHelper.dumpTree(s, TestSnapshot.cluster);
                    }
                    Assert.assertEquals(s, currentStatus.toString(), originalStatus.toString());
                }
            }
        }
    }

    /**
     * Change the file permission
     */
    static class FileChangePermission extends TestSnapshot.FileStatusChange {
        private final FsPermission newPermission;

        FileChangePermission(Path file, FileSystem fs, FsPermission newPermission) {
            super(file, fs, "chmod");
            this.newPermission = newPermission;
        }

        @Override
        void modify() throws Exception {
            Assert.assertTrue(fs.exists(file));
            fs.setPermission(file, newPermission);
        }
    }

    /**
     * Change the replication factor of file
     */
    static class FileChangeReplication extends TestSnapshot.FileStatusChange {
        private final short newReplication;

        FileChangeReplication(Path file, FileSystem fs, short replication) {
            super(file, fs, "replication");
            this.newReplication = replication;
        }

        @Override
        void modify() throws Exception {
            Assert.assertTrue(fs.exists(file));
            fs.setReplication(file, newReplication);
        }
    }

    /**
     * Change the owner:group of a file
     */
    static class FileChown extends TestSnapshot.FileStatusChange {
        private final String newUser;

        private final String newGroup;

        FileChown(Path file, FileSystem fs, String user, String group) {
            super(file, fs, "chown");
            this.newUser = user;
            this.newGroup = group;
        }

        @Override
        void modify() throws Exception {
            Assert.assertTrue(fs.exists(file));
            fs.setOwner(file, newUser, newGroup);
        }
    }

    /**
     * Appending a specified length to an existing file
     */
    static class FileAppend extends TestSnapshot.Modification {
        final int appendLen;

        private final HashMap<Path, Long> snapshotFileLengthMap;

        FileAppend(Path file, FileSystem fs, int len) {
            super(file, fs, "append");
            this.appendLen = len;
            this.snapshotFileLengthMap = new HashMap<Path, Long>();
        }

        @Override
        void loadSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                Path snapshotFile = SnapshotTestHelper.getSnapshotFile(snapshotRoot, file);
                if (snapshotFile != null) {
                    long snapshotFileLen = (fs.exists(snapshotFile)) ? fs.getFileStatus(snapshotFile).getLen() : -1L;
                    snapshotFileLengthMap.put(snapshotFile, snapshotFileLen);
                }
            }
        }

        @Override
        void modify() throws Exception {
            Assert.assertTrue(fs.exists(file));
            DFSTestUtil.appendFile(fs, file, appendLen);
        }

        @Override
        void checkSnapshots() throws Exception {
            byte[] buffer = new byte[32];
            for (Path snapshotFile : snapshotFileLengthMap.keySet()) {
                long currentSnapshotFileLen = (fs.exists(snapshotFile)) ? fs.getFileStatus(snapshotFile).getLen() : -1L;
                long originalSnapshotFileLen = snapshotFileLengthMap.get(snapshotFile);
                String s = null;
                if (currentSnapshotFileLen != originalSnapshotFileLen) {
                    s = (((((((((((("FAILED: " + (getClass().getSimpleName())) + ": file=") + (file)) + ", snapshotFile") + snapshotFile) + "\n\n currentSnapshotFileLen = ") + currentSnapshotFileLen) + "\noriginalSnapshotFileLen = ") + originalSnapshotFileLen) + "\n\nfile        : ") + (TestSnapshot.fsdir.getINode(file.toString()).toDetailString())) + "\n\nsnapshotFile: ") + (TestSnapshot.fsdir.getINode(snapshotFile.toString()).toDetailString());
                    SnapshotTestHelper.dumpTree(s, TestSnapshot.cluster);
                }
                Assert.assertEquals(s, originalSnapshotFileLen, currentSnapshotFileLen);
                // Read the snapshot file out of the boundary
                if ((currentSnapshotFileLen != (-1L)) && (!((this) instanceof TestSnapshot.FileAppendNotClose))) {
                    FSDataInputStream input = fs.open(snapshotFile);
                    int readLen = input.read(currentSnapshotFileLen, buffer, 0, 1);
                    if (readLen != (-1)) {
                        s = (((((((((((("FAILED: " + (getClass().getSimpleName())) + ": file=") + (file)) + ", snapshotFile") + snapshotFile) + "\n\n currentSnapshotFileLen = ") + currentSnapshotFileLen) + "\n                readLen = ") + readLen) + "\n\nfile        : ") + (TestSnapshot.fsdir.getINode(file.toString()).toDetailString())) + "\n\nsnapshotFile: ") + (TestSnapshot.fsdir.getINode(snapshotFile.toString()).toDetailString());
                        SnapshotTestHelper.dumpTree(s, TestSnapshot.cluster);
                    }
                    Assert.assertEquals(s, (-1), readLen);
                    input.close();
                }
            }
        }
    }

    /**
     * Appending a specified length to an existing file but not close the file
     */
    static class FileAppendNotClose extends TestSnapshot.FileAppend {
        HdfsDataOutputStream out;

        FileAppendNotClose(Path file, FileSystem fs, int len) {
            super(file, fs, len);
        }

        @Override
        void modify() throws Exception {
            Assert.assertTrue(fs.exists(file));
            byte[] toAppend = new byte[appendLen];
            TestSnapshot.random.nextBytes(toAppend);
            out = ((HdfsDataOutputStream) (fs.append(file)));
            out.write(toAppend);
            out.hsync(EnumSet.of(UPDATE_LENGTH));
        }
    }

    /**
     * Appending a specified length to an existing file
     */
    static class FileAppendClose extends TestSnapshot.FileAppend {
        final TestSnapshot.FileAppendNotClose fileAppendNotClose;

        FileAppendClose(Path file, FileSystem fs, int len, TestSnapshot.FileAppendNotClose fileAppendNotClose) {
            super(file, fs, len);
            this.fileAppendNotClose = fileAppendNotClose;
        }

        @Override
        void modify() throws Exception {
            Assert.assertTrue(fs.exists(file));
            byte[] toAppend = new byte[appendLen];
            TestSnapshot.random.nextBytes(toAppend);
            fileAppendNotClose.out.write(toAppend);
            fileAppendNotClose.out.close();
        }
    }

    /**
     * New file creation
     */
    static class FileCreation extends TestSnapshot.Modification {
        final int fileLen;

        private final HashMap<Path, FileStatus> fileStatusMap;

        FileCreation(Path file, FileSystem fs, int len) {
            super(file, fs, "creation");
            assert len >= 0;
            this.fileLen = len;
            fileStatusMap = new HashMap<Path, FileStatus>();
        }

        @Override
        void loadSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                Path snapshotFile = SnapshotTestHelper.getSnapshotFile(snapshotRoot, file);
                if (snapshotFile != null) {
                    FileStatus status = (fs.exists(snapshotFile)) ? fs.getFileStatus(snapshotFile) : null;
                    fileStatusMap.put(snapshotFile, status);
                }
            }
        }

        @Override
        void modify() throws Exception {
            DFSTestUtil.createFile(fs, file, fileLen, fileLen, TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed);
        }

        @Override
        void checkSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                Path snapshotFile = SnapshotTestHelper.getSnapshotFile(snapshotRoot, file);
                if (snapshotFile != null) {
                    boolean computed = fs.exists(snapshotFile);
                    boolean expected = (fileStatusMap.get(snapshotFile)) != null;
                    Assert.assertEquals(expected, computed);
                    if (computed) {
                        FileStatus currentSnapshotStatus = fs.getFileStatus(snapshotFile);
                        FileStatus originalStatus = fileStatusMap.get(snapshotFile);
                        // We compare the string because it contains all the information,
                        // while FileStatus#equals only compares the path
                        Assert.assertEquals(currentSnapshotStatus.toString(), originalStatus.toString());
                    }
                }
            }
        }
    }

    /**
     * File deletion
     */
    static class FileDeletion extends TestSnapshot.Modification {
        private final HashMap<Path, Boolean> snapshotFileExistenceMap;

        FileDeletion(Path file, FileSystem fs) {
            super(file, fs, "deletion");
            snapshotFileExistenceMap = new HashMap<Path, Boolean>();
        }

        @Override
        void loadSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                boolean existence = (SnapshotTestHelper.getSnapshotFile(snapshotRoot, file)) != null;
                snapshotFileExistenceMap.put(snapshotRoot, existence);
            }
        }

        @Override
        void modify() throws Exception {
            fs.delete(file, true);
        }

        @Override
        void checkSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                boolean currentSnapshotFileExist = (SnapshotTestHelper.getSnapshotFile(snapshotRoot, file)) != null;
                boolean originalSnapshotFileExist = snapshotFileExistenceMap.get(snapshotRoot);
                Assert.assertEquals(currentSnapshotFileExist, originalSnapshotFileExist);
            }
        }
    }

    /**
     * Directory creation or deletion.
     */
    class DirCreationOrDeletion extends TestSnapshot.Modification {
        private final SnapshotTestHelper.TestDirectoryTree.Node node;

        private final boolean isCreation;

        private final Path changedPath;

        private final HashMap<Path, FileStatus> statusMap;

        DirCreationOrDeletion(Path file, FileSystem fs, SnapshotTestHelper.TestDirectoryTree.Node node, boolean isCreation) {
            super(file, fs, "dircreation");
            this.node = node;
            // If the node's nonSnapshotChildren is empty, we still need to create
            // sub-directories
            this.isCreation = isCreation || (node.nonSnapshotChildren.isEmpty());
            if (this.isCreation) {
                // Generate the path for the dir to be created
                changedPath = new Path(node.nodePath, ("sub" + (node.nonSnapshotChildren.size())));
            } else {
                // If deletion, we delete the current last dir in nonSnapshotChildren
                changedPath = node.nonSnapshotChildren.get(((node.nonSnapshotChildren.size()) - 1)).nodePath;
            }
            this.statusMap = new HashMap<Path, FileStatus>();
        }

        @Override
        void loadSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                Path snapshotDir = SnapshotTestHelper.getSnapshotFile(snapshotRoot, changedPath);
                if (snapshotDir != null) {
                    FileStatus status = (fs.exists(snapshotDir)) ? fs.getFileStatus(snapshotDir) : null;
                    statusMap.put(snapshotDir, status);
                    // In each non-snapshottable directory, we also create a file. Thus
                    // here we also need to check the file's status before/after taking
                    // snapshots
                    Path snapshotFile = new Path(snapshotDir, "file0");
                    status = (fs.exists(snapshotFile)) ? fs.getFileStatus(snapshotFile) : null;
                    statusMap.put(snapshotFile, status);
                }
            }
        }

        @Override
        void modify() throws Exception {
            if (isCreation) {
                // creation
                SnapshotTestHelper.TestDirectoryTree.Node newChild = new SnapshotTestHelper.TestDirectoryTree.Node(changedPath, ((node.level) + 1), node, hdfs);
                // create file under the new non-snapshottable directory
                newChild.initFileList(hdfs, node.nodePath.getName(), TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed, 2);
                node.nonSnapshotChildren.add(newChild);
            } else {
                // deletion
                SnapshotTestHelper.TestDirectoryTree.Node childToDelete = node.nonSnapshotChildren.remove(((node.nonSnapshotChildren.size()) - 1));
                hdfs.delete(childToDelete.nodePath, true);
            }
        }

        @Override
        void checkSnapshots() throws Exception {
            for (Path snapshot : statusMap.keySet()) {
                FileStatus currentStatus = (fs.exists(snapshot)) ? fs.getFileStatus(snapshot) : null;
                FileStatus originalStatus = statusMap.get(snapshot);
                Assert.assertEquals(currentStatus, originalStatus);
                if (currentStatus != null) {
                    Assert.assertEquals(currentStatus.toString(), originalStatus.toString());
                }
            }
        }
    }

    /**
     * Directory creation or deletion.
     */
    class DirRename extends TestSnapshot.Modification {
        private final SnapshotTestHelper.TestDirectoryTree.Node srcParent;

        private final SnapshotTestHelper.TestDirectoryTree.Node dstParent;

        private final Path srcPath;

        private final Path dstPath;

        private final HashMap<Path, FileStatus> statusMap;

        DirRename(Path file, FileSystem fs, SnapshotTestHelper.TestDirectoryTree.Node src, SnapshotTestHelper.TestDirectoryTree.Node dst) throws Exception {
            super(file, fs, "dirrename");
            this.srcParent = src;
            this.dstParent = dst;
            dstPath = new Path(dstParent.nodePath, ("sub" + (dstParent.nonSnapshotChildren.size())));
            // If the srcParent's nonSnapshotChildren is empty, we need to create
            // sub-directories
            if (srcParent.nonSnapshotChildren.isEmpty()) {
                srcPath = new Path(srcParent.nodePath, ("sub" + (srcParent.nonSnapshotChildren.size())));
                // creation
                SnapshotTestHelper.TestDirectoryTree.Node newChild = new SnapshotTestHelper.TestDirectoryTree.Node(srcPath, ((srcParent.level) + 1), srcParent, hdfs);
                // create file under the new non-snapshottable directory
                newChild.initFileList(hdfs, srcParent.nodePath.getName(), TestSnapshot.BLOCKSIZE, TestSnapshot.REPLICATION, TestSnapshot.seed, 2);
                srcParent.nonSnapshotChildren.add(newChild);
            } else {
                srcPath = new Path(srcParent.nodePath, ("sub" + ((srcParent.nonSnapshotChildren.size()) - 1)));
            }
            this.statusMap = new HashMap<Path, FileStatus>();
        }

        @Override
        void loadSnapshots() throws Exception {
            for (Path snapshotRoot : TestSnapshot.snapshotList) {
                Path snapshotDir = SnapshotTestHelper.getSnapshotFile(snapshotRoot, srcPath);
                if (snapshotDir != null) {
                    FileStatus status = (fs.exists(snapshotDir)) ? fs.getFileStatus(snapshotDir) : null;
                    statusMap.put(snapshotDir, status);
                    // In each non-snapshottable directory, we also create a file. Thus
                    // here we also need to check the file's status before/after taking
                    // snapshots
                    Path snapshotFile = new Path(snapshotDir, "file0");
                    status = (fs.exists(snapshotFile)) ? fs.getFileStatus(snapshotFile) : null;
                    statusMap.put(snapshotFile, status);
                }
            }
        }

        @Override
        void modify() throws Exception {
            hdfs.rename(srcPath, dstPath);
            SnapshotTestHelper.TestDirectoryTree.Node newDstChild = new SnapshotTestHelper.TestDirectoryTree.Node(dstPath, ((dstParent.level) + 1), dstParent, hdfs);
            dstParent.nonSnapshotChildren.add(newDstChild);
        }

        @Override
        void checkSnapshots() throws Exception {
            for (Path snapshot : statusMap.keySet()) {
                FileStatus currentStatus = (fs.exists(snapshot)) ? fs.getFileStatus(snapshot) : null;
                FileStatus originalStatus = statusMap.get(snapshot);
                Assert.assertEquals(currentStatus, originalStatus);
                if (currentStatus != null) {
                    Assert.assertEquals(currentStatus.toString(), originalStatus.toString());
                }
            }
        }
    }
}

