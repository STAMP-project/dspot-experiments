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


import CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_DEFAULT;
import CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import DirOp.READ_LINK;
import FsAction.WRITE;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.AclException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests NameNode interaction for all ACL modification APIs.  This test suite
 * also covers interaction of setPermission with inodes that have ACLs.
 */
public abstract class FSAclBaseTest {
    private static final UserGroupInformation BRUCE = UserGroupInformation.createUserForTesting("bruce", new String[]{  });

    private static final UserGroupInformation DIANA = UserGroupInformation.createUserForTesting("diana", new String[]{  });

    private static final UserGroupInformation SUPERGROUP_MEMBER = UserGroupInformation.createUserForTesting("super", new String[]{ DFSConfigKeys.DFS_PERMISSIONS_SUPERUSERGROUP_DEFAULT });

    // group member
    private static final UserGroupInformation BOB = UserGroupInformation.createUserForTesting("bob", new String[]{ "groupY", "groupZ" });

    protected static MiniDFSCluster cluster;

    protected static Configuration conf;

    private static int pathCount = 0;

    private static Path path;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private FileSystem fs;

    private FileSystem fsAsBruce;

    private FileSystem fsAsDiana;

    private FileSystem fsAsSupergroupMember;

    private FileSystem fsAsBob;

    @Test
    public void testModifyAclEntries() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Assert.assertTrue(((FSAclBaseTest.path) + " should have ACLs in FileStatus!"), fs.getFileStatus(FSAclBaseTest.path).hasAcl());
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        Assert.assertTrue(((FSAclBaseTest.path) + " should have ACLs in FileStatus!"), fs.getFileStatus(FSAclBaseTest.path).hasAcl());
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testModifyAclEntriesOnlyAccess() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_EXECUTE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testModifyAclEntriesOnlyDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testModifyAclEntriesMinimal() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_WRITE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ) }, returned);
        assertPermission(((short) (4528)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testModifyAclEntriesMinimalDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testModifyAclEntriesCustomMask() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, MASK, NONE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ) }, returned);
        assertPermission(((short) (4480)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testModifyAclEntriesStickyBit() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (5096)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void testModifyAclEntriesPathNotFound() throws IOException {
        // Path has not been created.
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testModifyAclEntriesDefaultOnFile() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
    }

    @Test
    public void testRemoveAclEntries() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo"), AclTestHelpers.aclEntry(DEFAULT, USER, "foo"));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testRemoveAclEntriesOnlyAccess() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, USER, "bar", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo"));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "bar", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_WRITE) }, returned);
        assertPermission(((short) (4592)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testRemoveAclEntriesOnlyDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bar", READ_EXECUTE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo"));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "bar", READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testRemoveAclEntriesMinimal() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (496))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo"), AclTestHelpers.aclEntry(ACCESS, MASK));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (496)));
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test
    public void testRemoveAclEntriesMinimalDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo"), AclTestHelpers.aclEntry(ACCESS, MASK), AclTestHelpers.aclEntry(DEFAULT, USER, "foo"), AclTestHelpers.aclEntry(DEFAULT, MASK));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testRemoveAclEntriesStickyBit() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo"), AclTestHelpers.aclEntry(DEFAULT, USER, "foo"));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (5096)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void testRemoveAclEntriesPathNotFound() throws IOException {
        // Path has not been created.
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo"));
        fs.removeAclEntries(FSAclBaseTest.path, aclSpec);
    }

    @Test
    public void testRemoveDefaultAcl() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.removeDefaultAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) }, returned);
        assertPermission(((short) (4600)));
        FSAclBaseTest.assertAclFeature(true);
        // restart of the cluster
        restartCluster();
        s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] afterRestart = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(returned, afterRestart);
    }

    @Test
    public void testRemoveDefaultAclOnlyAccess() throws Exception {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.removeDefaultAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) }, returned);
        assertPermission(((short) (4600)));
        FSAclBaseTest.assertAclFeature(true);
        // restart of the cluster
        restartCluster();
        s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] afterRestart = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(returned, afterRestart);
    }

    @Test
    public void testRemoveDefaultAclOnlyDefault() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.removeDefaultAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (488)));
        FSAclBaseTest.assertAclFeature(false);
        // restart of the cluster
        restartCluster();
        s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] afterRestart = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(returned, afterRestart);
    }

    @Test
    public void testRemoveDefaultAclMinimal() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.removeDefaultAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (488)));
        FSAclBaseTest.assertAclFeature(false);
        // restart of the cluster
        restartCluster();
        s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] afterRestart = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(returned, afterRestart);
    }

    @Test
    public void testRemoveDefaultAclStickyBit() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.removeDefaultAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) }, returned);
        assertPermission(((short) (5112)));
        FSAclBaseTest.assertAclFeature(true);
        // restart of the cluster
        restartCluster();
        s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] afterRestart = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(returned, afterRestart);
    }

    @Test(expected = FileNotFoundException.class)
    public void testRemoveDefaultAclPathNotFound() throws IOException {
        // Path has not been created.
        fs.removeDefaultAcl(FSAclBaseTest.path);
    }

    @Test
    public void testRemoveAcl() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Assert.assertTrue(((FSAclBaseTest.path) + " should have ACLs in FileStatus!"), fs.getFileStatus(FSAclBaseTest.path).hasAcl());
        Assert.assertTrue(((FSAclBaseTest.path) + " should have ACLs in FileStatus#toString()!"), fs.getFileStatus(FSAclBaseTest.path).toString().contains("hasAcl=true"));
        fs.removeAcl(FSAclBaseTest.path);
        Assert.assertFalse(((FSAclBaseTest.path) + " should not have ACLs in FileStatus!"), fs.getFileStatus(FSAclBaseTest.path).hasAcl());
        Assert.assertTrue(((FSAclBaseTest.path) + " should not have ACLs in FileStatus#toString()!"), fs.getFileStatus(FSAclBaseTest.path).toString().contains("hasAcl=false"));
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (488)));
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test
    public void testRemoveAclMinimalAcl() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        fs.removeAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (416)));
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test
    public void testRemoveAclStickyBit() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.removeAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (1000)));
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test
    public void testRemoveAclOnlyDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.removeAcl(FSAclBaseTest.path);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (488)));
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test(expected = FileNotFoundException.class)
    public void testRemoveAclPathNotFound() throws IOException {
        // Path has not been created.
        fs.removeAcl(FSAclBaseTest.path);
    }

    @Test
    public void testSetAcl() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4600)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetAclOnlyAccess() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ) }, returned);
        assertPermission(((short) (4512)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetAclOnlyDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetAclMinimal() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (420))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(((short) (416)));
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test
    public void testSetAclMinimalDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4584)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetAclCustomMask() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, MASK, ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ) }, returned);
        assertPermission(((short) (4536)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetAclStickyBit() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (5112)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void testSetAclPathNotFound() throws IOException {
        // Path has not been created.
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
    }

    @Test(expected = AclException.class)
    public void testSetAclDefaultOnFile() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
    }

    @Test
    public void testSetPermission() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (448))));
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4544)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetPermissionOnlyAccess() throws IOException {
        fs.create(FSAclBaseTest.path).close();
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (384))));
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, READ) }, returned);
        assertPermission(((short) (4480)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetPermissionOnlyDefault() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (448))));
        AclStatus s = fs.getAclStatus(FSAclBaseTest.path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(((short) (4544)));
        FSAclBaseTest.assertAclFeature(true);
    }

    @Test
    public void testSetPermissionCannotSetAclBit() throws IOException {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setPermission(FSAclBaseTest.path, FsPermission.createImmutable(((short) (448))));
        assertPermission(((short) (448)));
        fs.setPermission(FSAclBaseTest.path, new org.apache.hadoop.hdfs.protocol.FsPermissionExtension(FsPermission.createImmutable(((short) (493))), true, true, true));
        INode inode = FSAclBaseTest.cluster.getNamesystem().getFSDirectory().getINode(FSAclBaseTest.path.toUri().getPath(), READ_LINK);
        Assert.assertNotNull(inode);
        FsPermission perm = inode.getFsPermission();
        Assert.assertNotNull(perm);
        Assert.assertEquals(493, perm.toShort());
        FileStatus stat = fs.getFileStatus(FSAclBaseTest.path);
        Assert.assertFalse(stat.hasAcl());
        Assert.assertFalse(stat.isEncrypted());
        Assert.assertFalse(stat.isErasureCoded());
        // backwards-compat check
        Assert.assertEquals(493, perm.toExtendedShort());
        FSAclBaseTest.assertAclFeature(false);
    }

    @Test
    public void testDefaultAclNewFile() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path filePath = new Path(FSAclBaseTest.path, "file1");
        fs.create(filePath).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) }, returned);
        assertPermission(filePath, ((short) (4528)));
        FSAclBaseTest.assertAclFeature(filePath, true);
    }

    @Test
    public void testUMaskDefaultAclNewFile() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        String oldUMask = fs.getConf().get(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY);
        fs.getConf().set(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY, "027");
        FSDirectory fsDirectory = FSAclBaseTest.cluster.getNamesystem().getFSDirectory();
        boolean oldEnabled = fsDirectory.isPosixAclInheritanceEnabled();
        try {
            fsDirectory.setPosixAclInheritanceEnabled(false);
            Path filePath = new Path(FSAclBaseTest.path, "file1");
            fs.create(filePath).close();
            AclStatus s = fs.getAclStatus(filePath);
            AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
            Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_WRITE) }, returned);
            assertPermission(filePath, ((short) (4512)));
            fsDirectory.setPosixAclInheritanceEnabled(true);
            Path file2Path = new Path(FSAclBaseTest.path, "file2");
            fs.create(file2Path).close();
            AclStatus s2 = fs.getAclStatus(file2Path);
            AclEntry[] returned2 = s2.getEntries().toArray(new AclEntry[0]);
            Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_WRITE) }, returned2);
            assertPermission(file2Path, ((short) (4528)));
        } finally {
            fsDirectory.setPosixAclInheritanceEnabled(oldEnabled);
            fs.getConf().set(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY, oldUMask);
        }
    }

    @Test
    public void testOnlyAccessAclNewFile() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        Path filePath = new Path(FSAclBaseTest.path, "file1");
        fs.create(filePath).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(filePath, ((short) (420)));
        FSAclBaseTest.assertAclFeature(filePath, false);
    }

    @Test
    public void testDefaultMinimalAclNewFile() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path filePath = new Path(FSAclBaseTest.path, "file1");
        fs.create(filePath).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(filePath, ((short) (416)));
        FSAclBaseTest.assertAclFeature(filePath, false);
    }

    @Test
    public void testDefaultAclNewDir() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Assert.assertTrue(((FSAclBaseTest.path) + " should have ACLs in FileStatus!"), fs.getFileStatus(FSAclBaseTest.path).hasAcl());
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        fs.mkdirs(dirPath);
        Assert.assertTrue((dirPath + " should have ACLs in FileStatus!"), fs.getFileStatus(dirPath).hasAcl());
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(dirPath, ((short) (4600)));
        FSAclBaseTest.assertAclFeature(dirPath, true);
    }

    @Test
    public void testUMaskDefaultAclNewDir() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        String oldUMask = fs.getConf().get(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY);
        fs.getConf().set(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY, "027");
        FSDirectory fsDirectory = FSAclBaseTest.cluster.getNamesystem().getFSDirectory();
        boolean oldEnabled = fsDirectory.isPosixAclInheritanceEnabled();
        try {
            fsDirectory.setPosixAclInheritanceEnabled(false);
            Path dirPath = new Path(FSAclBaseTest.path, "dir1");
            fs.mkdirs(dirPath);
            AclStatus s = fs.getAclStatus(dirPath);
            AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
            Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
            assertPermission(dirPath, ((short) (4584)));
            fsDirectory.setPosixAclInheritanceEnabled(true);
            Path dir2Path = new Path(FSAclBaseTest.path, "dir2");
            fs.mkdirs(dir2Path);
            AclStatus s2 = fs.getAclStatus(dir2Path);
            AclEntry[] returned2 = s2.getEntries().toArray(new AclEntry[0]);
            Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ALL), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned2);
            assertPermission(dir2Path, ((short) (4600)));
        } finally {
            fsDirectory.setPosixAclInheritanceEnabled(oldEnabled);
            fs.getConf().set(CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY, oldUMask);
        }
    }

    @Test
    public void testOnlyAccessAclNewDir() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL));
        fs.modifyAclEntries(FSAclBaseTest.path, aclSpec);
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        fs.mkdirs(dirPath);
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(dirPath, ((short) (493)));
        FSAclBaseTest.assertAclFeature(dirPath, false);
    }

    @Test
    public void testDefaultMinimalAclNewDir() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        fs.mkdirs(dirPath);
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) }, returned);
        assertPermission(dirPath, ((short) (4584)));
        FSAclBaseTest.assertAclFeature(dirPath, true);
    }

    @Test
    public void testDefaultAclNewFileIntermediate() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        Path filePath = new Path(dirPath, "file1");
        fs.create(filePath).close();
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) };
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(dirPath, ((short) (4584)));
        FSAclBaseTest.assertAclFeature(dirPath, true);
        expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) };
        s = fs.getAclStatus(filePath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(filePath, ((short) (4528)));
        FSAclBaseTest.assertAclFeature(filePath, true);
    }

    @Test
    public void testDefaultAclNewDirIntermediate() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        Path subdirPath = new Path(dirPath, "subdir1");
        fs.mkdirs(subdirPath);
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) };
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(dirPath, ((short) (4584)));
        FSAclBaseTest.assertAclFeature(dirPath, true);
        s = fs.getAclStatus(subdirPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(subdirPath, ((short) (4600)));
        FSAclBaseTest.assertAclFeature(subdirPath, true);
    }

    @Test
    public void testDefaultAclNewSymlinkIntermediate() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (488))));
        Path filePath = new Path(FSAclBaseTest.path, "file1");
        fs.create(filePath).close();
        fs.setPermission(filePath, FsPermission.createImmutable(((short) (416))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        Path linkPath = new Path(dirPath, "link1");
        fs.createSymlink(filePath, linkPath, true);
        AclEntry[] expected = new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, NONE) };
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(dirPath, ((short) (4584)));
        FSAclBaseTest.assertAclFeature(dirPath, true);
        expected = new AclEntry[]{  };
        s = fs.getAclStatus(linkPath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(linkPath, ((short) (416)));
        FSAclBaseTest.assertAclFeature(linkPath, false);
        s = fs.getAclStatus(filePath);
        returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(filePath, ((short) (416)));
        FSAclBaseTest.assertAclFeature(filePath, false);
    }

    @Test
    public void testDefaultAclNewFileWithMode() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (493))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path filePath = new Path(FSAclBaseTest.path, "file1");
        int bufferSize = FSAclBaseTest.cluster.getConfiguration(0).getInt(IO_FILE_BUFFER_SIZE_KEY, IO_FILE_BUFFER_SIZE_DEFAULT);
        fs.create(filePath, new FsPermission(((short) (480))), false, bufferSize, fs.getDefaultReplication(filePath), fs.getDefaultBlockSize(FSAclBaseTest.path), null).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE) }, returned);
        assertPermission(filePath, ((short) (4576)));
        FSAclBaseTest.assertAclFeature(filePath, true);
    }

    @Test
    public void testDefaultAclNewDirWithMode() throws Exception {
        FileSystem.mkdirs(fs, FSAclBaseTest.path, FsPermission.createImmutable(((short) (493))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(FSAclBaseTest.path, aclSpec);
        Path dirPath = new Path(FSAclBaseTest.path, "dir1");
        fs.mkdirs(dirPath, new FsPermission(((short) (480))));
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, "foo", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ALL), AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, READ_EXECUTE) }, returned);
        assertPermission(dirPath, ((short) (4576)));
        FSAclBaseTest.assertAclFeature(dirPath, true);
    }

    @Test
    public void testDefaultAclRenamedFile() throws Exception {
        Path dirPath = new Path(FSAclBaseTest.path, "dir");
        FileSystem.mkdirs(fs, dirPath, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(dirPath, aclSpec);
        Path filePath = new Path(FSAclBaseTest.path, "file1");
        fs.create(filePath).close();
        fs.setPermission(filePath, FsPermission.createImmutable(((short) (416))));
        Path renamedFilePath = new Path(dirPath, "file1");
        fs.rename(filePath, renamedFilePath);
        AclEntry[] expected = new AclEntry[]{  };
        AclStatus s = fs.getAclStatus(renamedFilePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(renamedFilePath, ((short) (416)));
        FSAclBaseTest.assertAclFeature(renamedFilePath, false);
    }

    @Test
    public void testDefaultAclRenamedDir() throws Exception {
        Path dirPath = new Path(FSAclBaseTest.path, "dir");
        FileSystem.mkdirs(fs, dirPath, FsPermission.createImmutable(((short) (488))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "foo", ALL));
        fs.setAcl(dirPath, aclSpec);
        Path subdirPath = new Path(FSAclBaseTest.path, "subdir");
        FileSystem.mkdirs(fs, subdirPath, FsPermission.createImmutable(((short) (488))));
        Path renamedSubdirPath = new Path(dirPath, "subdir");
        fs.rename(subdirPath, renamedSubdirPath);
        AclEntry[] expected = new AclEntry[]{  };
        AclStatus s = fs.getAclStatus(renamedSubdirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(renamedSubdirPath, ((short) (488)));
        FSAclBaseTest.assertAclFeature(renamedSubdirPath, false);
    }

    @Test
    public void testSkipAclEnforcementPermsDisabled() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        fsAsBruce.modifyAclEntries(bruceFile, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana", NONE)));
        AclTestHelpers.assertFilePermissionDenied(fsAsDiana, FSAclBaseTest.DIANA, bruceFile);
        try {
            FSAclBaseTest.conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, false);
            restartCluster();
            AclTestHelpers.assertFilePermissionGranted(fsAsDiana, FSAclBaseTest.DIANA, bruceFile);
        } finally {
            FSAclBaseTest.conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, true);
            restartCluster();
        }
    }

    @Test
    public void testSkipAclEnforcementSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        fsAsBruce.modifyAclEntries(bruceFile, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana", NONE)));
        AclTestHelpers.assertFilePermissionGranted(fs, FSAclBaseTest.DIANA, bruceFile);
        AclTestHelpers.assertFilePermissionGranted(fsAsBruce, FSAclBaseTest.DIANA, bruceFile);
        AclTestHelpers.assertFilePermissionDenied(fsAsDiana, FSAclBaseTest.DIANA, bruceFile);
        AclTestHelpers.assertFilePermissionGranted(fsAsSupergroupMember, FSAclBaseTest.SUPERGROUP_MEMBER, bruceFile);
    }

    @Test
    public void testModifyAclEntriesMustBeOwnerOrSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana", ALL));
        fsAsBruce.modifyAclEntries(bruceFile, aclSpec);
        fs.modifyAclEntries(bruceFile, aclSpec);
        fsAsSupergroupMember.modifyAclEntries(bruceFile, aclSpec);
        exception.expect(AccessControlException.class);
        fsAsDiana.modifyAclEntries(bruceFile, aclSpec);
    }

    @Test
    public void testRemoveAclEntriesMustBeOwnerOrSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "diana"));
        fsAsBruce.removeAclEntries(bruceFile, aclSpec);
        fs.removeAclEntries(bruceFile, aclSpec);
        fsAsSupergroupMember.removeAclEntries(bruceFile, aclSpec);
        exception.expect(AccessControlException.class);
        fsAsDiana.removeAclEntries(bruceFile, aclSpec);
    }

    @Test
    public void testRemoveDefaultAclMustBeOwnerOrSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        fsAsBruce.removeDefaultAcl(bruceFile);
        fs.removeDefaultAcl(bruceFile);
        fsAsSupergroupMember.removeDefaultAcl(bruceFile);
        exception.expect(AccessControlException.class);
        fsAsDiana.removeDefaultAcl(bruceFile);
    }

    @Test
    public void testRemoveAclMustBeOwnerOrSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        fsAsBruce.removeAcl(bruceFile);
        fs.removeAcl(bruceFile);
        fsAsSupergroupMember.removeAcl(bruceFile);
        exception.expect(AccessControlException.class);
        fsAsDiana.removeAcl(bruceFile);
    }

    @Test
    public void testSetAclMustBeOwnerOrSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, READ), AclTestHelpers.aclEntry(ACCESS, OTHER, READ));
        fsAsBruce.setAcl(bruceFile, aclSpec);
        fs.setAcl(bruceFile, aclSpec);
        fsAsSupergroupMember.setAcl(bruceFile, aclSpec);
        exception.expect(AccessControlException.class);
        fsAsDiana.setAcl(bruceFile, aclSpec);
    }

    @Test
    public void testGetAclStatusRequiresTraverseOrSuper() throws Exception {
        Path bruceDir = new Path(FSAclBaseTest.path, "bruce");
        Path bruceFile = new Path(bruceDir, "file");
        fs.mkdirs(bruceDir);
        fs.setOwner(bruceDir, "bruce", null);
        fsAsBruce.create(bruceFile).close();
        fsAsBruce.setAcl(bruceDir, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ALL), AclTestHelpers.aclEntry(ACCESS, USER, "diana", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)));
        fsAsBruce.getAclStatus(bruceFile);
        fs.getAclStatus(bruceFile);
        fsAsSupergroupMember.getAclStatus(bruceFile);
        exception.expect(AccessControlException.class);
        fsAsDiana.getAclStatus(bruceFile);
    }

    @Test
    public void testAccess() throws IOException, InterruptedException {
        Path p1 = new Path("/p1");
        fs.mkdirs(p1);
        fs.setOwner(p1, FSAclBaseTest.BRUCE.getShortUserName(), "groupX");
        fsAsBruce.setAcl(p1, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, READ), AclTestHelpers.aclEntry(ACCESS, USER, "bruce", READ), AclTestHelpers.aclEntry(ACCESS, GROUP, NONE), AclTestHelpers.aclEntry(ACCESS, OTHER, NONE)));
        fsAsBruce.access(p1, FsAction.READ);
        try {
            fsAsBruce.access(p1, WRITE);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            // expected
        }
        Path badPath = new Path("/bad/bad");
        try {
            fsAsBruce.access(badPath, FsAction.READ);
            Assert.fail("The access call should have failed");
        } catch (FileNotFoundException e) {
            // expected
        }
        // Add a named group entry with only READ access
        fsAsBruce.modifyAclEntries(p1, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, GROUP, "groupY", READ)));
        // Now bob should have read access, but not write
        fsAsBob.access(p1, READ);
        try {
            fsAsBob.access(p1, WRITE);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            // expected;
        }
        // Add another named group entry with WRITE access
        fsAsBruce.modifyAclEntries(p1, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, GROUP, "groupZ", WRITE)));
        // Now bob should have write access
        fsAsBob.access(p1, WRITE);
        // Add a named user entry to deny bob
        fsAsBruce.modifyAclEntries(p1, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bob", NONE)));
        try {
            fsAsBob.access(p1, READ);
            Assert.fail("The access call should have failed.");
        } catch (AccessControlException e) {
            // expected;
        }
    }

    @Test
    public void testEffectiveAccess() throws Exception {
        Path p1 = new Path("/testEffectiveAccess");
        fs.mkdirs(p1);
        // give all access at first
        fs.setPermission(p1, FsPermission.valueOf("-rwxrwxrwx"));
        AclStatus aclStatus = fs.getAclStatus(p1);
        Assert.assertEquals("Entries should be empty", 0, aclStatus.getEntries().size());
        Assert.assertEquals("Permission should be carried by AclStatus", fs.getFileStatus(p1).getPermission(), aclStatus.getPermission());
        // Add a named entries with all access
        fs.modifyAclEntries(p1, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "bruce", ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, "groupY", ALL)));
        aclStatus = fs.getAclStatus(p1);
        Assert.assertEquals("Entries should contain owner group entry also", 3, aclStatus.getEntries().size());
        // restrict the access
        fs.setPermission(p1, FsPermission.valueOf("-rwxr-----"));
        // latest permissions should be reflected as effective permission
        aclStatus = fs.getAclStatus(p1);
        List<AclEntry> entries = aclStatus.getEntries();
        for (AclEntry aclEntry : entries) {
            if (((aclEntry.getName()) != null) || ((aclEntry.getType()) == (GROUP))) {
                Assert.assertEquals(FsAction.ALL, aclEntry.getPermission());
                Assert.assertEquals(FsAction.READ, aclStatus.getEffectivePermission(aclEntry));
            }
        }
        fsAsBruce.access(p1, READ);
        try {
            fsAsBruce.access(p1, WRITE);
            Assert.fail("Access should not be given");
        } catch (AccessControlException e) {
            // expected
        }
        fsAsBob.access(p1, READ);
        try {
            fsAsBob.access(p1, WRITE);
            Assert.fail("Access should not be given");
        } catch (AccessControlException e) {
            // expected
        }
    }

    /**
     * Verify the de-duplication of AclFeatures with same entries.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDeDuplication() throws Exception {
        // This test needs to verify the count of the references which is held by
        // static data structure. So shutting down entire cluster to get the fresh
        // data.
        FSAclBaseTest.shutdown();
        AclStorage.getUniqueAclFeatures().clear();
        FSAclBaseTest.startCluster();
        setUp();
        int currentSize = 0;
        Path p1 = new Path("/testDeduplication");
        {
            // unique default AclEntries for this test
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, "testdeduplicateuser", ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, "testdeduplicategroup", ALL));
            fs.mkdirs(p1);
            fs.modifyAclEntries(p1, aclSpec);
            Assert.assertEquals("One more ACL feature should be unique", (currentSize + 1), AclStorage.getUniqueAclFeatures().getUniqueElementsSize());
            currentSize++;
        }
        Path child1 = new Path(p1, "child1");
        AclFeature child1AclFeature;
        {
            // new child dir should copy entries from its parent.
            fs.mkdirs(child1);
            Assert.assertEquals("One more ACL feature should be unique", (currentSize + 1), AclStorage.getUniqueAclFeatures().getUniqueElementsSize());
            child1AclFeature = FSAclBaseTest.getAclFeature(child1, FSAclBaseTest.cluster);
            Assert.assertEquals("Reference count should be 1", 1, child1AclFeature.getRefCount());
            currentSize++;
        }
        Path child2 = new Path(p1, "child2");
        {
            // new child dir should copy entries from its parent. But all entries are
            // same as its sibling without any more acl changes.
            fs.mkdirs(child2);
            Assert.assertEquals("existing AclFeature should be re-used", currentSize, AclStorage.getUniqueAclFeatures().getUniqueElementsSize());
            AclFeature child2AclFeature = FSAclBaseTest.getAclFeature(child1, FSAclBaseTest.cluster);
            Assert.assertSame("Same Aclfeature should be re-used", child1AclFeature, child2AclFeature);
            Assert.assertEquals("Reference count should be 2", 2, child2AclFeature.getRefCount());
        }
        {
            // modification of ACL on should decrement the original reference count
            // and increase new one.
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "user1", ALL));
            fs.modifyAclEntries(child1, aclSpec);
            AclFeature modifiedAclFeature = FSAclBaseTest.getAclFeature(child1, FSAclBaseTest.cluster);
            Assert.assertEquals("Old Reference count should be 1", 1, child1AclFeature.getRefCount());
            Assert.assertEquals("New Reference count should be 1", 1, modifiedAclFeature.getRefCount());
            // removing the new added ACL entry should refer to old ACLfeature
            AclEntry aclEntry = new AclEntry.Builder().setScope(ACCESS).setType(USER).setName("user1").build();
            fs.removeAclEntries(child1, Lists.newArrayList(aclEntry));
            Assert.assertEquals("Old Reference count should be 2 again", 2, child1AclFeature.getRefCount());
            Assert.assertEquals("New Reference count should be 0", 0, modifiedAclFeature.getRefCount());
        }
        {
            // verify the reference count on deletion of Acls
            fs.removeAcl(child2);
            Assert.assertEquals("Reference count should be 1", 1, child1AclFeature.getRefCount());
        }
        {
            // verify the reference count on deletion of dir with ACL
            fs.delete(child1, true);
            Assert.assertEquals("Reference count should be 0", 0, child1AclFeature.getRefCount());
        }
        Path file1 = new Path(p1, "file1");
        Path file2 = new Path(p1, "file2");
        AclFeature fileAclFeature;
        {
            // Using same reference on creation of file
            fs.create(file1).close();
            fileAclFeature = FSAclBaseTest.getAclFeature(file1, FSAclBaseTest.cluster);
            Assert.assertEquals("Reference count should be 1", 1, fileAclFeature.getRefCount());
            fs.create(file2).close();
            Assert.assertEquals("Reference count should be 2", 2, fileAclFeature.getRefCount());
        }
        {
            // modifying ACLs on file should decrease the reference count on old
            // instance and increase on the new instance
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, "user1", ALL));
            // adding new ACL entry
            fs.modifyAclEntries(file1, aclSpec);
            AclFeature modifiedFileAcl = FSAclBaseTest.getAclFeature(file1, FSAclBaseTest.cluster);
            Assert.assertEquals("Old Reference count should be 1", 1, fileAclFeature.getRefCount());
            Assert.assertEquals("New Reference count should be 1", 1, modifiedFileAcl.getRefCount());
            // removing the new added ACL entry should refer to old ACLfeature
            AclEntry aclEntry = new AclEntry.Builder().setScope(ACCESS).setType(USER).setName("user1").build();
            fs.removeAclEntries(file1, Lists.newArrayList(aclEntry));
            Assert.assertEquals("Old Reference count should be 2", 2, fileAclFeature.getRefCount());
            Assert.assertEquals("New Reference count should be 0", 0, modifiedFileAcl.getRefCount());
        }
        {
            // reference count should be decreased on deletion of files with ACLs
            fs.delete(file2, true);
            Assert.assertEquals("Reference count should be decreased on delete of the file", 1, fileAclFeature.getRefCount());
            fs.delete(file1, true);
            Assert.assertEquals("Reference count should be decreased on delete of the file", 0, fileAclFeature.getRefCount());
            // On reference count reaches 0 instance should be removed from map
            fs.create(file1).close();
            AclFeature newFileAclFeature = FSAclBaseTest.getAclFeature(file1, FSAclBaseTest.cluster);
            Assert.assertNotSame("Instance should be different on reference count 0", fileAclFeature, newFileAclFeature);
            fileAclFeature = newFileAclFeature;
        }
        Map<AclFeature, Integer> restartRefCounter = new HashMap<>();
        // Restart the Namenode to check the references.
        // Here reference counts will not be same after restart because, while
        // shutting down namenode will not call any removal of AclFeature.
        // However this is applicable only in case of tests as in real-cluster JVM
        // itself will be new.
        List<AclFeature> entriesBeforeRestart = AclStorage.getUniqueAclFeatures().getEntries();
        {
            // restart by loading edits
            for (AclFeature aclFeature : entriesBeforeRestart) {
                restartRefCounter.put(aclFeature, aclFeature.getRefCount());
            }
            FSAclBaseTest.cluster.restartNameNode(true);
            List<AclFeature> entriesAfterRestart = AclStorage.getUniqueAclFeatures().getEntries();
            Assert.assertEquals("Entries before and after should be same", entriesBeforeRestart, entriesAfterRestart);
            for (AclFeature aclFeature : entriesAfterRestart) {
                int before = restartRefCounter.get(aclFeature);
                Assert.assertEquals("ReferenceCount After Restart should be doubled", (before * 2), aclFeature.getRefCount());
            }
        }
        {
            // restart by loading fsimage
            FSAclBaseTest.cluster.getNameNodeRpc().setSafeMode(SAFEMODE_ENTER, false);
            FSAclBaseTest.cluster.getNameNodeRpc().saveNamespace(0, 0);
            FSAclBaseTest.cluster.getNameNodeRpc().setSafeMode(SAFEMODE_LEAVE, false);
            FSAclBaseTest.cluster.restartNameNode(true);
            List<AclFeature> entriesAfterRestart = AclStorage.getUniqueAclFeatures().getEntries();
            Assert.assertEquals("Entries before and after should be same", entriesBeforeRestart, entriesAfterRestart);
            for (AclFeature aclFeature : entriesAfterRestart) {
                int before = restartRefCounter.get(aclFeature);
                Assert.assertEquals("ReferenceCount After 2 Restarts should be tripled", (before * 3), aclFeature.getRefCount());
            }
        }
    }
}

