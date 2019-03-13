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


import HdfsConstants.QUOTA_DONT_SET;
import com.google.common.collect.Lists;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.SnapshotAccessControlException;
import org.apache.hadoop.hdfs.server.namenode.AclFeature;
import org.apache.hadoop.hdfs.server.namenode.AclStorage;
import org.apache.hadoop.hdfs.server.namenode.AclTestHelpers;
import org.apache.hadoop.hdfs.server.namenode.FSAclBaseTest;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests interaction of ACLs with snapshots.
 */
public class TestAclWithSnapshot {
    private static final UserGroupInformation BRUCE = UserGroupInformation.createUserForTesting("bruce", new String[]{  });

    private static final UserGroupInformation DIANA = UserGroupInformation.createUserForTesting("diana", new String[]{  });

    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static FileSystem fsAsBruce;

    private static FileSystem fsAsDiana;

    private static DistributedFileSystem hdfs;

    private static int pathCount = 0;

    private static Path path;

    private static Path snapshotPath;

    private static String snapshotName;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testOriginalAclEnforcedForSnapshotRootAfterChange() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        TestAclWithSnapshot.hdfs.setAcl(TestAclWithSnapshot.path, aclSpec);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.path);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, TestAclWithSnapshot.path);
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        // Both original and snapshot still have same ACL.
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4584)), TestAclWithSnapshot.path);
        s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.snapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4584)), TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, TestAclWithSnapshot.snapshotPath);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        TestAclWithSnapshot.hdfs.setAcl(TestAclWithSnapshot.path, aclSpec);
        // Original has changed, but snapshot still has old ACL.
        TestAclWithSnapshot.doSnapshotRootChangeAssertions(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.restart(false);
        TestAclWithSnapshot.doSnapshotRootChangeAssertions(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.restart(true);
        TestAclWithSnapshot.doSnapshotRootChangeAssertions(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotPath);
    }

    @Test
    public void testOriginalAclEnforcedForSnapshotContentsAfterChange() throws Exception {
        Path filePath = new Path(TestAclWithSnapshot.path, "file1");
        Path subdirPath = new Path(TestAclWithSnapshot.path, "subdir1");
        Path fileSnapshotPath = new Path(TestAclWithSnapshot.snapshotPath, "file1");
        Path subdirSnapshotPath = new Path(TestAclWithSnapshot.snapshotPath, "subdir1");
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (511))));
        FileSystem.create(TestAclWithSnapshot.hdfs, filePath, FsPermission.createImmutable(((short) (384)))).close();
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, subdirPath, FsPermission.createImmutable(((short) (448))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        TestAclWithSnapshot.hdfs.setAcl(filePath, aclSpec);
        TestAclWithSnapshot.hdfs.setAcl(subdirPath, aclSpec);
        AclTestHelpers.assertFilePermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, filePath);
        AclTestHelpers.assertFilePermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, filePath);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, subdirPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, subdirPath);
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        // Both original and snapshot still have same ACL.
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) };
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), filePath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(subdirPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), subdirPath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(fileSnapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), fileSnapshotPath);
        AclTestHelpers.assertFilePermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, fileSnapshotPath);
        AclTestHelpers.assertFilePermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, fileSnapshotPath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(subdirSnapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), subdirSnapshotPath);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, subdirSnapshotPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, subdirSnapshotPath);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "diana", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        TestAclWithSnapshot.hdfs.setAcl(filePath, aclSpec);
        TestAclWithSnapshot.hdfs.setAcl(subdirPath, aclSpec);
        // Original has changed, but snapshot still has old ACL.
        TestAclWithSnapshot.doSnapshotContentsChangeAssertions(filePath, fileSnapshotPath, subdirPath, subdirSnapshotPath);
        TestAclWithSnapshot.restart(false);
        TestAclWithSnapshot.doSnapshotContentsChangeAssertions(filePath, fileSnapshotPath, subdirPath, subdirSnapshotPath);
        TestAclWithSnapshot.restart(true);
        TestAclWithSnapshot.doSnapshotContentsChangeAssertions(filePath, fileSnapshotPath, subdirPath, subdirSnapshotPath);
    }

    @Test
    public void testOriginalAclEnforcedForSnapshotRootAfterRemoval() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        TestAclWithSnapshot.hdfs.setAcl(TestAclWithSnapshot.path, aclSpec);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.path);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, TestAclWithSnapshot.path);
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        // Both original and snapshot still have same ACL.
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4584)), TestAclWithSnapshot.path);
        s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.snapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4584)), TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.hdfs.removeAcl(TestAclWithSnapshot.path);
        // Original has changed, but snapshot still has old ACL.
        TestAclWithSnapshot.doSnapshotRootRemovalAssertions(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.restart(false);
        TestAclWithSnapshot.doSnapshotRootRemovalAssertions(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.restart(true);
        TestAclWithSnapshot.doSnapshotRootRemovalAssertions(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotPath);
    }

    @Test
    public void testOriginalAclEnforcedForSnapshotContentsAfterRemoval() throws Exception {
        Path filePath = new Path(TestAclWithSnapshot.path, "file1");
        Path subdirPath = new Path(TestAclWithSnapshot.path, "subdir1");
        Path fileSnapshotPath = new Path(TestAclWithSnapshot.snapshotPath, "file1");
        Path subdirSnapshotPath = new Path(TestAclWithSnapshot.snapshotPath, "subdir1");
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (511))));
        FileSystem.create(TestAclWithSnapshot.hdfs, filePath, FsPermission.createImmutable(((short) (384)))).close();
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, subdirPath, FsPermission.createImmutable(((short) (448))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        TestAclWithSnapshot.hdfs.setAcl(filePath, aclSpec);
        TestAclWithSnapshot.hdfs.setAcl(subdirPath, aclSpec);
        AclTestHelpers.assertFilePermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, filePath);
        AclTestHelpers.assertFilePermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, filePath);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, subdirPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, subdirPath);
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        // Both original and snapshot still have same ACL.
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) };
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), filePath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(subdirPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), subdirPath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(fileSnapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), fileSnapshotPath);
        AclTestHelpers.assertFilePermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, fileSnapshotPath);
        AclTestHelpers.assertFilePermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, fileSnapshotPath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(subdirSnapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4456)), subdirSnapshotPath);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, subdirSnapshotPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, subdirSnapshotPath);
        TestAclWithSnapshot.hdfs.removeAcl(filePath);
        TestAclWithSnapshot.hdfs.removeAcl(subdirPath);
        // Original has changed, but snapshot still has old ACL.
        TestAclWithSnapshot.doSnapshotContentsRemovalAssertions(filePath, fileSnapshotPath, subdirPath, subdirSnapshotPath);
        TestAclWithSnapshot.restart(false);
        TestAclWithSnapshot.doSnapshotContentsRemovalAssertions(filePath, fileSnapshotPath, subdirPath, subdirSnapshotPath);
        TestAclWithSnapshot.restart(true);
        TestAclWithSnapshot.doSnapshotContentsRemovalAssertions(filePath, fileSnapshotPath, subdirPath, subdirSnapshotPath);
    }

    @Test
    public void testModifyReadsCurrentState() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL));
        TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE));
        TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, aclSpec);
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) };
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (4600)), TestAclWithSnapshot.path);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.path);
        TestAclWithSnapshot.assertDirPermissionGranted(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, TestAclWithSnapshot.path);
    }

    @Test
    public void testRemoveReadsCurrentState() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL));
        TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, aclSpec);
        TestAclWithSnapshot.hdfs.removeAcl(TestAclWithSnapshot.path);
        AclEntry[] expected = new AclEntry[]{  };
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        TestAclWithSnapshot.assertPermission(((short) (448)), TestAclWithSnapshot.path);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.path);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsDiana, TestAclWithSnapshot.DIANA, TestAclWithSnapshot.path);
    }

    @Test
    public void testDefaultAclNotCopiedToAccessAclOfNewSnapshot() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_EXECUTE));
        TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, aclSpec);
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, NONE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4544)), TestAclWithSnapshot.path);
        s = TestAclWithSnapshot.hdfs.getAclStatus(TestAclWithSnapshot.snapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, NONE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4544)), TestAclWithSnapshot.snapshotPath);
        TestAclWithSnapshot.assertDirPermissionDenied(TestAclWithSnapshot.fsAsBruce, TestAclWithSnapshot.BRUCE, TestAclWithSnapshot.snapshotPath);
    }

    @Test
    public void testModifyAclEntriesSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce", READ_EXECUTE));
        exception.expect(SnapshotAccessControlException.class);
        TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.snapshotPath, aclSpec);
    }

    @Test
    public void testRemoveAclEntriesSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce"));
        exception.expect(SnapshotAccessControlException.class);
        TestAclWithSnapshot.hdfs.removeAclEntries(TestAclWithSnapshot.snapshotPath, aclSpec);
    }

    @Test
    public void testRemoveDefaultAclSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        exception.expect(SnapshotAccessControlException.class);
        TestAclWithSnapshot.hdfs.removeDefaultAcl(TestAclWithSnapshot.snapshotPath);
    }

    @Test
    public void testRemoveAclSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        exception.expect(SnapshotAccessControlException.class);
        TestAclWithSnapshot.hdfs.removeAcl(TestAclWithSnapshot.snapshotPath);
    }

    @Test
    public void testSetAclSnapshotPath() throws Exception {
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (448))));
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "bruce"));
        exception.expect(SnapshotAccessControlException.class);
        TestAclWithSnapshot.hdfs.setAcl(TestAclWithSnapshot.snapshotPath, aclSpec);
    }

    @Test
    public void testChangeAclExceedsQuota() throws Exception {
        Path filePath = new Path(TestAclWithSnapshot.path, "file1");
        Path fileSnapshotPath = new Path(TestAclWithSnapshot.snapshotPath, "file1");
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (493))));
        TestAclWithSnapshot.hdfs.allowSnapshot(TestAclWithSnapshot.path);
        TestAclWithSnapshot.hdfs.setQuota(TestAclWithSnapshot.path, 3, QUOTA_DONT_SET);
        FileSystem.create(TestAclWithSnapshot.hdfs, filePath, FsPermission.createImmutable(((short) (384)))).close();
        TestAclWithSnapshot.hdfs.setPermission(filePath, FsPermission.createImmutable(((short) (384))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE));
        TestAclWithSnapshot.hdfs.modifyAclEntries(filePath, aclSpec);
        TestAclWithSnapshot.hdfs.createSnapshot(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4528)), filePath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(fileSnapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4528)), filePath);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ));
        TestAclWithSnapshot.hdfs.modifyAclEntries(filePath, aclSpec);
    }

    @Test
    public void testRemoveAclExceedsQuota() throws Exception {
        Path filePath = new Path(TestAclWithSnapshot.path, "file1");
        Path fileSnapshotPath = new Path(TestAclWithSnapshot.snapshotPath, "file1");
        FileSystem.mkdirs(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, FsPermission.createImmutable(((short) (493))));
        TestAclWithSnapshot.hdfs.allowSnapshot(TestAclWithSnapshot.path);
        TestAclWithSnapshot.hdfs.setQuota(TestAclWithSnapshot.path, 3, QUOTA_DONT_SET);
        FileSystem.create(TestAclWithSnapshot.hdfs, filePath, FsPermission.createImmutable(((short) (384)))).close();
        TestAclWithSnapshot.hdfs.setPermission(filePath, FsPermission.createImmutable(((short) (384))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE));
        TestAclWithSnapshot.hdfs.modifyAclEntries(filePath, aclSpec);
        TestAclWithSnapshot.hdfs.createSnapshot(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4528)), filePath);
        s = TestAclWithSnapshot.hdfs.getAclStatus(fileSnapshotPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE) }, returned);
        TestAclWithSnapshot.assertPermission(((short) (4528)), filePath);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ));
        TestAclWithSnapshot.hdfs.removeAcl(filePath);
    }

    @Test
    public void testGetAclStatusDotSnapshotPath() throws Exception {
        TestAclWithSnapshot.hdfs.mkdirs(TestAclWithSnapshot.path);
        SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
        AclStatus s = TestAclWithSnapshot.hdfs.getAclStatus(new Path(TestAclWithSnapshot.path, ".snapshot"));
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
    }

    @Test
    public void testDeDuplication() throws Exception {
        int startSize = AclStorage.getUniqueAclFeatures().getUniqueElementsSize();
        // unique default AclEntries for this test
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "testdeduplicateuser", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, "testdeduplicategroup", ALL));
        TestAclWithSnapshot.hdfs.mkdirs(TestAclWithSnapshot.path);
        TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, aclSpec);
        Assert.assertEquals("One more ACL feature should be unique", (startSize + 1), AclStorage.getUniqueAclFeatures().getUniqueElementsSize());
        Path subdir = new Path(TestAclWithSnapshot.path, "sub-dir");
        TestAclWithSnapshot.hdfs.mkdirs(subdir);
        Path file = new Path(TestAclWithSnapshot.path, "file");
        TestAclWithSnapshot.hdfs.create(file).close();
        AclFeature aclFeature;
        {
            // create the snapshot with root directory having ACLs should refer to
            // same ACLFeature without incrementing the reference count
            aclFeature = FSAclBaseTest.getAclFeature(TestAclWithSnapshot.path, TestAclWithSnapshot.cluster);
            Assert.assertEquals("Reference count should be one before snapshot", 1, aclFeature.getRefCount());
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            AclFeature snapshotAclFeature = FSAclBaseTest.getAclFeature(snapshotPath, TestAclWithSnapshot.cluster);
            Assert.assertSame(aclFeature, snapshotAclFeature);
            Assert.assertEquals("Reference count should be increased", 2, snapshotAclFeature.getRefCount());
        }
        {
            // deleting the snapshot with root directory having ACLs should not alter
            // the reference count of the ACLFeature
            deleteSnapshotWithAclAndVerify(aclFeature, TestAclWithSnapshot.path, startSize);
        }
        {
            TestAclWithSnapshot.hdfs.modifyAclEntries(subdir, aclSpec);
            aclFeature = FSAclBaseTest.getAclFeature(subdir, TestAclWithSnapshot.cluster);
            Assert.assertEquals("Reference count should be 1", 1, aclFeature.getRefCount());
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            Path subdirInSnapshot = new Path(snapshotPath, "sub-dir");
            AclFeature snapshotAcl = FSAclBaseTest.getAclFeature(subdirInSnapshot, TestAclWithSnapshot.cluster);
            Assert.assertSame(aclFeature, snapshotAcl);
            Assert.assertEquals("Reference count should remain same", 1, aclFeature.getRefCount());
            // Delete the snapshot with sub-directory containing the ACLs should not
            // alter the reference count for AclFeature
            deleteSnapshotWithAclAndVerify(aclFeature, subdir, startSize);
        }
        {
            TestAclWithSnapshot.hdfs.modifyAclEntries(file, aclSpec);
            aclFeature = FSAclBaseTest.getAclFeature(file, TestAclWithSnapshot.cluster);
            Assert.assertEquals("Reference count should be 1", 1, aclFeature.getRefCount());
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            Path fileInSnapshot = new Path(snapshotPath, file.getName());
            AclFeature snapshotAcl = FSAclBaseTest.getAclFeature(fileInSnapshot, TestAclWithSnapshot.cluster);
            Assert.assertSame(aclFeature, snapshotAcl);
            Assert.assertEquals("Reference count should remain same", 1, aclFeature.getRefCount());
            // Delete the snapshot with contained file having ACLs should not
            // alter the reference count for AclFeature
            deleteSnapshotWithAclAndVerify(aclFeature, file, startSize);
        }
        {
            // Modifying the ACLs of root directory of the snapshot should refer new
            // AclFeature. And old AclFeature should be referenced by snapshot
            TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, aclSpec);
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            AclFeature snapshotAcl = FSAclBaseTest.getAclFeature(snapshotPath, TestAclWithSnapshot.cluster);
            aclFeature = FSAclBaseTest.getAclFeature(TestAclWithSnapshot.path, TestAclWithSnapshot.cluster);
            Assert.assertEquals("Before modification same ACL should be referenced twice", 2, aclFeature.getRefCount());
            List<AclEntry> newAcl = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "testNewUser", ALL));
            TestAclWithSnapshot.hdfs.modifyAclEntries(TestAclWithSnapshot.path, newAcl);
            aclFeature = FSAclBaseTest.getAclFeature(TestAclWithSnapshot.path, TestAclWithSnapshot.cluster);
            AclFeature snapshotAclPostModification = FSAclBaseTest.getAclFeature(snapshotPath, TestAclWithSnapshot.cluster);
            Assert.assertSame(snapshotAcl, snapshotAclPostModification);
            Assert.assertNotSame(aclFeature, snapshotAclPostModification);
            Assert.assertEquals("Old ACL feature reference count should be same", 1, snapshotAcl.getRefCount());
            Assert.assertEquals("New ACL feature reference should be used", 1, aclFeature.getRefCount());
            deleteSnapshotWithAclAndVerify(aclFeature, TestAclWithSnapshot.path, startSize);
        }
        {
            // Modifying the ACLs of sub directory of the snapshot root should refer
            // new AclFeature. And old AclFeature should be referenced by snapshot
            TestAclWithSnapshot.hdfs.modifyAclEntries(subdir, aclSpec);
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            Path subdirInSnapshot = new Path(snapshotPath, "sub-dir");
            AclFeature snapshotAclFeature = FSAclBaseTest.getAclFeature(subdirInSnapshot, TestAclWithSnapshot.cluster);
            List<AclEntry> newAcl = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "testNewUser", ALL));
            TestAclWithSnapshot.hdfs.modifyAclEntries(subdir, newAcl);
            aclFeature = FSAclBaseTest.getAclFeature(subdir, TestAclWithSnapshot.cluster);
            Assert.assertNotSame(aclFeature, snapshotAclFeature);
            Assert.assertEquals("Reference count should remain same", 1, snapshotAclFeature.getRefCount());
            Assert.assertEquals("New AclFeature should be used", 1, aclFeature.getRefCount());
            deleteSnapshotWithAclAndVerify(aclFeature, subdir, startSize);
        }
        {
            // Modifying the ACLs of file inside the snapshot root should refer new
            // AclFeature. And old AclFeature should be referenced by snapshot
            TestAclWithSnapshot.hdfs.modifyAclEntries(file, aclSpec);
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            Path fileInSnapshot = new Path(snapshotPath, file.getName());
            AclFeature snapshotAclFeature = FSAclBaseTest.getAclFeature(fileInSnapshot, TestAclWithSnapshot.cluster);
            List<AclEntry> newAcl = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "testNewUser", ALL));
            TestAclWithSnapshot.hdfs.modifyAclEntries(file, newAcl);
            aclFeature = FSAclBaseTest.getAclFeature(file, TestAclWithSnapshot.cluster);
            Assert.assertNotSame(aclFeature, snapshotAclFeature);
            Assert.assertEquals("Reference count should remain same", 1, snapshotAclFeature.getRefCount());
            deleteSnapshotWithAclAndVerify(aclFeature, file, startSize);
        }
        {
            // deleting the original directory containing dirs and files with ACLs
            // with snapshot
            TestAclWithSnapshot.hdfs.delete(TestAclWithSnapshot.path, true);
            Path dir = new Path(subdir, "dir");
            TestAclWithSnapshot.hdfs.mkdirs(dir);
            TestAclWithSnapshot.hdfs.modifyAclEntries(dir, aclSpec);
            file = new Path(subdir, "file");
            TestAclWithSnapshot.hdfs.create(file).close();
            aclSpec.add(AclTestHelpers.aclEntry(ACCESS, USER, "testNewUser", ALL));
            TestAclWithSnapshot.hdfs.modifyAclEntries(file, aclSpec);
            AclFeature fileAcl = FSAclBaseTest.getAclFeature(file, TestAclWithSnapshot.cluster);
            AclFeature dirAcl = FSAclBaseTest.getAclFeature(dir, TestAclWithSnapshot.cluster);
            Path snapshotPath = SnapshotTestHelper.createSnapshot(TestAclWithSnapshot.hdfs, TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            Path dirInSnapshot = new Path(snapshotPath, "sub-dir/dir");
            AclFeature snapshotDirAclFeature = FSAclBaseTest.getAclFeature(dirInSnapshot, TestAclWithSnapshot.cluster);
            Path fileInSnapshot = new Path(snapshotPath, "sub-dir/file");
            AclFeature snapshotFileAclFeature = FSAclBaseTest.getAclFeature(fileInSnapshot, TestAclWithSnapshot.cluster);
            Assert.assertSame(fileAcl, snapshotFileAclFeature);
            Assert.assertSame(dirAcl, snapshotDirAclFeature);
            TestAclWithSnapshot.hdfs.delete(subdir, true);
            Assert.assertEquals("Original ACLs references should be maintained for snapshot", 1, snapshotFileAclFeature.getRefCount());
            Assert.assertEquals("Original ACLs references should be maintained for snapshot", 1, snapshotDirAclFeature.getRefCount());
            TestAclWithSnapshot.hdfs.deleteSnapshot(TestAclWithSnapshot.path, TestAclWithSnapshot.snapshotName);
            Assert.assertEquals("ACLs should be deleted from snapshot", startSize, AclStorage.getUniqueAclFeatures().getUniqueElementsSize());
        }
    }
}

