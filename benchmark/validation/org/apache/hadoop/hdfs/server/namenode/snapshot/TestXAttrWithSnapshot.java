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


import XAttrSetFlag.REPLACE;
import java.util.EnumSet;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshotAccessControlException;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests interaction of XAttrs with snapshots.
 */
public class TestXAttrWithSnapshot {
    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static DistributedFileSystem hdfs;

    private static int pathCount = 0;

    private static Path path;

    private static Path snapshotPath;

    private static Path snapshotPath2;

    private static Path snapshotPath3;

    private static String snapshotName;

    private static String snapshotName2;

    private static String snapshotName3;

    private final int SUCCESS = 0;

    // XAttrs
    private static final String name1 = "user.a1";

    private static final byte[] value1 = new byte[]{ 49, 50, 51 };

    private static final byte[] newValue1 = new byte[]{ 49, 49, 49 };

    private static final String name2 = "user.a2";

    private static final byte[] value2 = new byte[]{ 55, 56, 57 };

    @Rule
    public ExpectedException exception = ExpectedException.none();

    /**
     * Tests modifying xattrs on a directory that has been snapshotted
     */
    @Test(timeout = 120000)
    public void testModifyReadsCurrentState() throws Exception {
        // Init
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value2);
        // Verify that current path reflects xattrs, snapshot doesn't
        Map<String, byte[]> xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(xattrs.size(), 0);
        // Modify each xattr and make sure it's reflected
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value2, EnumSet.of(REPLACE));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value1, EnumSet.of(REPLACE));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name2));
        // Paranoia checks
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(xattrs.size(), 0);
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1);
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2);
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 0);
    }

    /**
     * Tests removing xattrs on a directory that has been snapshotted
     */
    @Test(timeout = 120000)
    public void testRemoveReadsCurrentState() throws Exception {
        // Init
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value2);
        // Verify that current path reflects xattrs, snapshot doesn't
        Map<String, byte[]> xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(xattrs.size(), 0);
        // Remove xattrs and verify one-by-one
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2);
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1);
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 0);
    }

    /**
     * 1) Save xattrs, then create snapshot. Assert that inode of original and
     * snapshot have same xattrs. 2) Change the original xattrs, assert snapshot
     * still has old xattrs.
     */
    @Test
    public void testXAttrForSnapshotRootAfterChange() throws Exception {
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value2);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        // Both original and snapshot have same XAttrs.
        Map<String, byte[]> xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        // Original XAttrs have changed, but snapshot still has old XAttrs.
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.newValue1);
        TestXAttrWithSnapshot.doSnapshotRootChangeAssertions(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotPath);
        TestXAttrWithSnapshot.restart(false);
        TestXAttrWithSnapshot.doSnapshotRootChangeAssertions(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotPath);
        TestXAttrWithSnapshot.restart(true);
        TestXAttrWithSnapshot.doSnapshotRootChangeAssertions(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotPath);
    }

    /**
     * 1) Save xattrs, then create snapshot. Assert that inode of original and
     * snapshot have same xattrs. 2) Remove some original xattrs, assert snapshot
     * still has old xattrs.
     */
    @Test
    public void testXAttrForSnapshotRootAfterRemove() throws Exception {
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value2);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        // Both original and snapshot have same XAttrs.
        Map<String, byte[]> xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        // Original XAttrs have been removed, but snapshot still has old XAttrs.
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1);
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2);
        TestXAttrWithSnapshot.doSnapshotRootRemovalAssertions(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotPath);
        TestXAttrWithSnapshot.restart(false);
        TestXAttrWithSnapshot.doSnapshotRootRemovalAssertions(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotPath);
        TestXAttrWithSnapshot.restart(true);
        TestXAttrWithSnapshot.doSnapshotRootRemovalAssertions(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotPath);
    }

    /**
     * Test successive snapshots in between modifications of XAttrs.
     * Also verify that snapshot XAttrs are not altered when a
     * snapshot is deleted.
     */
    @Test
    public void testSuccessiveSnapshotXAttrChanges() throws Exception {
        // First snapshot
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        Map<String, byte[]> xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(1, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        // Second snapshot
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.newValue1);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value2);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName2);
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath2);
        Assert.assertEquals(2, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.newValue1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        // Third snapshot
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName3);
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath3);
        Assert.assertEquals(1, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        // Check that the first and second snapshots'
        // XAttrs have stayed constant
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(1, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath2);
        Assert.assertEquals(2, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.newValue1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
        // Remove the second snapshot and verify the first and
        // third snapshots' XAttrs have stayed constant
        TestXAttrWithSnapshot.hdfs.deleteSnapshot(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName2);
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath);
        Assert.assertEquals(1, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(TestXAttrWithSnapshot.snapshotPath3);
        Assert.assertEquals(1, xattrs.size());
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        TestXAttrWithSnapshot.hdfs.deleteSnapshot(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        TestXAttrWithSnapshot.hdfs.deleteSnapshot(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName3);
    }

    /**
     * Assert exception of setting xattr on read-only snapshot.
     */
    @Test
    public void testSetXAttrSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        exception.expect(SnapshotAccessControlException.class);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.snapshotPath, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
    }

    /**
     * Assert exception of removing xattr on read-only snapshot.
     */
    @Test
    public void testRemoveXAttrSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        exception.expect(SnapshotAccessControlException.class);
        TestXAttrWithSnapshot.hdfs.removeXAttr(TestXAttrWithSnapshot.snapshotPath, TestXAttrWithSnapshot.name1);
    }

    /**
     * Test that users can copy a snapshot while preserving its xattrs.
     */
    @Test(timeout = 120000)
    public void testCopySnapshotShouldPreserveXAttrs() throws Exception {
        FileSystem.mkdirs(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name1, TestXAttrWithSnapshot.value1);
        TestXAttrWithSnapshot.hdfs.setXAttr(TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.name2, TestXAttrWithSnapshot.value2);
        SnapshotTestHelper.createSnapshot(TestXAttrWithSnapshot.hdfs, TestXAttrWithSnapshot.path, TestXAttrWithSnapshot.snapshotName);
        Path snapshotCopy = new Path(((TestXAttrWithSnapshot.path.toString()) + "-copy"));
        String[] argv = new String[]{ "-cp", "-px", TestXAttrWithSnapshot.snapshotPath.toUri().toString(), snapshotCopy.toUri().toString() };
        int ret = ToolRunner.run(new org.apache.hadoop.fs.FsShell(TestXAttrWithSnapshot.conf), argv);
        Assert.assertEquals("cp -px is not working on a snapshot", SUCCESS, ret);
        Map<String, byte[]> xattrs = TestXAttrWithSnapshot.hdfs.getXAttrs(snapshotCopy);
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value1, xattrs.get(TestXAttrWithSnapshot.name1));
        Assert.assertArrayEquals(TestXAttrWithSnapshot.value2, xattrs.get(TestXAttrWithSnapshot.name2));
    }
}

