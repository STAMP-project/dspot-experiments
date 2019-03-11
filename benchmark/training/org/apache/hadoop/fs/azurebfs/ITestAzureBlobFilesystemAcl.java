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
package org.apache.hadoop.fs.azurebfs;


import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathIOException;
import org.apache.hadoop.fs.azurebfs.utils.AclTestHelpers;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test acl operations.
 */
public class ITestAzureBlobFilesystemAcl extends AbstractAbfsIntegrationTest {
    private static final FsAction ALL = FsAction.ALL;

    private static final FsAction NONE = FsAction.NONE;

    private static final FsAction READ = FsAction.READ;

    private static final FsAction EXECUTE = FsAction.EXECUTE;

    private static final FsAction READ_EXECUTE = FsAction.READ_EXECUTE;

    private static final FsAction READ_WRITE = FsAction.READ_WRITE;

    private static final short RW = 384;

    private static final short RWX = 448;

    private static final short RWX_R = 480;

    private static final short RWX_RW = 496;

    private static final short RWX_RWX = 504;

    private static final short RWX_RX = 488;

    private static final short RWX_RX_RX = 493;

    private static final short RW_R = 416;

    private static final short RW_X = 392;

    private static final short RW_RW = 432;

    private static final short RW_RWX = 440;

    private static final short RW_R_R = 420;

    private static final short STICKY_RWX_RWX = 1016;

    private static final String FOO = UUID.randomUUID().toString();

    private static final String BAR = UUID.randomUUID().toString();

    private static final String TEST_OWNER = UUID.randomUUID().toString();

    private static final String TEST_GROUP = UUID.randomUUID().toString();

    private static Path testRoot = new Path("/test");

    private Path path;

    public ITestAzureBlobFilesystemAcl() throws Exception {
        super();
    }

    @Test
    public void testModifyAclEntries() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.mkdirs(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testModifyAclEntriesOnlyAccess() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testModifyAclEntriesOnlyDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testModifyAclEntriesMinimal() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_WRITE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW_RW)));
    }

    @Test
    public void testModifyAclEntriesMinimalDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testModifyAclEntriesCustomMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.NONE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW)));
    }

    @Test
    public void testModifyAclEntriesStickyBit() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE));
        fs.modifyAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (1000)));
    }

    @Test(expected = FileNotFoundException.class)
    public void testModifyAclEntriesPathNotFound() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        // Path has not been created.
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.modifyAclEntries(path, aclSpec);
    }

    @Test(expected = Exception.class)
    public void testModifyAclEntriesDefaultOnFile() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.modifyAclEntries(path, aclSpec);
    }

    @Test
    public void testModifyAclEntriesWithDefaultMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.EXECUTE));
        fs.setAcl(path, aclSpec);
        List<AclEntry> modifyAclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.READ_WRITE));
        fs.modifyAclEntries(path, modifyAclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testModifyAclEntriesWithAccessMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.EXECUTE));
        fs.setAcl(path, aclSpec);
        List<AclEntry> modifyAclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE));
        fs.modifyAclEntries(path, modifyAclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW_X)));
    }

    @Test(expected = PathIOException.class)
    public void testModifyAclEntriesWithDuplicateEntries() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.EXECUTE));
        fs.setAcl(path, aclSpec);
        List<AclEntry> modifyAclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE));
        fs.modifyAclEntries(path, modifyAclSpec);
    }

    @Test
    public void testRemoveAclEntries() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO));
        fs.removeAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testRemoveAclEntriesOnlyAccess() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO));
        fs.removeAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_WRITE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RW)));
    }

    @Test
    public void testRemoveAclEntriesOnlyDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.READ_EXECUTE));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO));
        fs.removeAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testRemoveAclEntriesMinimal() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RW))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO), AclTestHelpers.aclEntry(ACCESS, MASK));
        fs.removeAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RW)));
    }

    @Test
    public void testRemoveAclEntriesMinimalDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO), AclTestHelpers.aclEntry(ACCESS, MASK), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO), AclTestHelpers.aclEntry(DEFAULT, MASK));
        fs.removeAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testRemoveAclEntriesStickyBit() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO));
        fs.removeAclEntries(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (1000)));
    }

    @Test(expected = FileNotFoundException.class)
    public void testRemoveAclEntriesPathNotFound() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        // Path has not been created.
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO));
        fs.removeAclEntries(path, aclSpec);
    }

    @Test(expected = PathIOException.class)
    public void testRemoveAclEntriesAccessMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.EXECUTE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeAclEntries(path, Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.NONE)));
    }

    @Test(expected = PathIOException.class)
    public void testRemoveAclEntriesDefaultMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeAclEntries(path, Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.NONE)));
    }

    @Test(expected = PathIOException.class)
    public void testRemoveAclEntriesWithDuplicateEntries() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.EXECUTE));
        fs.setAcl(path, aclSpec);
        List<AclEntry> removeAclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.READ_WRITE));
        fs.removeAclEntries(path, removeAclSpec);
    }

    @Test
    public void testRemoveDefaultAcl() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeDefaultAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RWX)));
    }

    @Test
    public void testRemoveDefaultAclOnlyAccess() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        fs.removeDefaultAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RWX)));
    }

    @Test
    public void testRemoveDefaultAclOnlyDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeDefaultAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testRemoveDefaultAclMinimal() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        fs.removeDefaultAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testRemoveDefaultAclStickyBit() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeDefaultAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.STICKY_RWX_RWX)));
    }

    @Test(expected = FileNotFoundException.class)
    public void testRemoveDefaultAclPathNotFound() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        // Path has not been created.
        fs.removeDefaultAcl(path);
    }

    @Test
    public void testRemoveAcl() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testRemoveAclMinimalAcl() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        fs.removeAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW_R)));
    }

    @Test
    public void testRemoveAclStickyBit() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (1000)));
    }

    @Test
    public void testRemoveAclOnlyDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.removeAcl(path);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test(expected = FileNotFoundException.class)
    public void testRemoveAclPathNotFound() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        // Path has not been created.
        fs.removeAcl(path);
    }

    @Test
    public void testSetAcl() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RWX)));
    }

    @Test
    public void testSetAclOnlyAccess() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW_R)));
    }

    @Test
    public void testSetAclOnlyDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testSetAclMinimal() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW_R)));
    }

    @Test
    public void testSetAclMinimalDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testSetAclCustomMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW_RWX)));
    }

    @Test
    public void testSetAclStickyBit() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (1000))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.STICKY_RWX_RWX)));
    }

    @Test(expected = FileNotFoundException.class)
    public void testSetAclPathNotFound() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        // Path has not been created.
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
    }

    @Test(expected = Exception.class)
    public void testSetAclDefaultOnFile() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
    }

    @Test
    public void testSetAclDoesNotChangeDefaultMask() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.EXECUTE));
        fs.setAcl(path, aclSpec);
        // only change access acl, and default mask should not change.
        List<AclEntry> aclSpec2 = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.READ_EXECUTE));
        fs.setAcl(path, aclSpec2);
        // get acl status and check result.
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX_RX)));
    }

    @Test(expected = PathIOException.class)
    public void testSetAclWithDuplicateEntries() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.EXECUTE), AclTestHelpers.aclEntry(ACCESS, MASK, ITestAzureBlobFilesystemAcl.EXECUTE));
        fs.setAcl(path, aclSpec);
    }

    @Test
    public void testSetPermission() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX))));
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX)));
    }

    @Test
    public void testSetPermissionOnlyAccess() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        fs.create(path).close();
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.READ_WRITE), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW))));
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.READ), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RW)));
    }

    @Test
    public void testSetPermissionOnlyDefault() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(ACCESS, OTHER, ITestAzureBlobFilesystemAcl.NONE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        fs.setPermission(path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX))));
        AclStatus s = fs.getAclStatus(path);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, ((short) (ITestAzureBlobFilesystemAcl.RWX)));
    }

    @Test
    public void testDefaultAclNewFile() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        Path filePath = new Path(path, "file1");
        fs.create(filePath).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, filePath, ((short) (ITestAzureBlobFilesystemAcl.RW_R)));
    }

    @Test
    public void testDefaultMinimalAclNewFile() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        Path filePath = new Path(path, "file1");
        fs.create(filePath).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, filePath, ((short) (ITestAzureBlobFilesystemAcl.RW_R)));
    }

    @Test
    public void testDefaultAclNewDir() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        Path dirPath = new Path(path, "dir1");
        fs.mkdirs(dirPath);
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, dirPath, ((short) (ITestAzureBlobFilesystemAcl.RWX_RWX)));
    }

    @Test
    public void testOnlyAccessAclNewDir() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.modifyAclEntries(path, aclSpec);
        Path dirPath = new Path(path, "dir1");
        fs.mkdirs(dirPath);
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{  }, returned);
        assertPermission(fs, dirPath, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX_RX)));
    }

    @Test
    public void testDefaultMinimalAclNewDir() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE));
        fs.setAcl(path, aclSpec);
        Path dirPath = new Path(path, "dir1");
        fs.mkdirs(dirPath);
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.NONE) }, returned);
        assertPermission(fs, dirPath, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testDefaultAclNewFileWithMode() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        Path filePath = new Path(path, "file1");
        int bufferSize = (4 * 1024) * 1024;
        fs.create(filePath, new FsPermission(((short) (ITestAzureBlobFilesystemAcl.RWX_R))), false, bufferSize, fs.getDefaultReplication(filePath), fs.getDefaultBlockSize(path), null).close();
        AclStatus s = fs.getAclStatus(filePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, filePath, ((short) (ITestAzureBlobFilesystemAcl.RWX_R)));
    }

    @Test
    public void testDefaultAclNewDirWithMode() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        FileSystem.mkdirs(fs, path, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(path, aclSpec);
        Path dirPath = new Path(path, "dir1");
        fs.mkdirs(dirPath, new FsPermission(((short) (ITestAzureBlobFilesystemAcl.RWX_R))));
        AclStatus s = fs.getAclStatus(dirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(new AclEntry[]{ AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.READ_EXECUTE), AclTestHelpers.aclEntry(DEFAULT, MASK, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(DEFAULT, OTHER, ITestAzureBlobFilesystemAcl.READ_EXECUTE) }, returned);
        assertPermission(fs, dirPath, ((short) (ITestAzureBlobFilesystemAcl.RWX_R)));
    }

    @Test
    public void testDefaultAclRenamedFile() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        Path dirPath = new Path(path, "dir");
        FileSystem.mkdirs(fs, dirPath, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(dirPath, aclSpec);
        Path filePath = new Path(path, "file1");
        fs.create(filePath).close();
        fs.setPermission(filePath, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RW_R))));
        Path renamedFilePath = new Path(dirPath, "file1");
        fs.rename(filePath, renamedFilePath);
        AclEntry[] expected = new AclEntry[]{  };
        AclStatus s = fs.getAclStatus(renamedFilePath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(fs, renamedFilePath, ((short) (ITestAzureBlobFilesystemAcl.RW_R)));
    }

    @Test
    public void testDefaultAclRenamedDir() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        path = new Path(ITestAzureBlobFilesystemAcl.testRoot, UUID.randomUUID().toString());
        Path dirPath = new Path(path, "dir");
        FileSystem.mkdirs(fs, dirPath, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(dirPath, aclSpec);
        Path subdirPath = new Path(path, "subdir");
        FileSystem.mkdirs(fs, subdirPath, FsPermission.createImmutable(((short) (ITestAzureBlobFilesystemAcl.RWX_RX))));
        Path renamedSubdirPath = new Path(dirPath, "subdir");
        fs.rename(subdirPath, renamedSubdirPath);
        AclEntry[] expected = new AclEntry[]{  };
        AclStatus s = fs.getAclStatus(renamedSubdirPath);
        AclEntry[] returned = s.getEntries().toArray(new AclEntry[0]);
        Assert.assertArrayEquals(expected, returned);
        assertPermission(fs, renamedSubdirPath, ((short) (ITestAzureBlobFilesystemAcl.RWX_RX)));
    }

    @Test
    public void testEnsureAclOperationWorksForRoot() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue(fs.getIsNamespaceEnabled());
        Path rootPath = new Path("/");
        List<AclEntry> aclSpec1 = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.ALL));
        fs.setAcl(rootPath, aclSpec1);
        fs.getAclStatus(rootPath);
        fs.setOwner(rootPath, ITestAzureBlobFilesystemAcl.TEST_OWNER, ITestAzureBlobFilesystemAcl.TEST_GROUP);
        fs.setPermission(rootPath, new FsPermission("777"));
        List<AclEntry> aclSpec2 = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, USER, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, USER, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.ALL));
        fs.modifyAclEntries(rootPath, aclSpec2);
        fs.removeAclEntries(rootPath, aclSpec2);
        fs.removeDefaultAcl(rootPath);
        fs.removeAcl(rootPath);
    }

    @Test
    public void testSetOwnerForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        Assert.assertTrue(fs.exists(filePath));
        FileStatus oldFileStatus = fs.getFileStatus(filePath);
        fs.setOwner(filePath, ITestAzureBlobFilesystemAcl.TEST_OWNER, ITestAzureBlobFilesystemAcl.TEST_GROUP);
        FileStatus newFileStatus = fs.getFileStatus(filePath);
        Assert.assertEquals(oldFileStatus.getOwner(), newFileStatus.getOwner());
        Assert.assertEquals(oldFileStatus.getGroup(), newFileStatus.getGroup());
    }

    @Test
    public void testSetPermissionForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        Assert.assertTrue(fs.exists(filePath));
        FsPermission oldPermission = fs.getFileStatus(filePath).getPermission();
        // default permission for non-namespace enabled account is "777"
        FsPermission newPermission = new FsPermission("557");
        Assert.assertNotEquals(oldPermission, newPermission);
        fs.setPermission(filePath, newPermission);
        FsPermission updatedPermission = fs.getFileStatus(filePath).getPermission();
        Assert.assertEquals(oldPermission, updatedPermission);
    }

    @Test
    public void testModifyAclEntriesForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        try {
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.ALL));
            fs.modifyAclEntries(filePath, aclSpec);
            Assert.assertFalse("UnsupportedOperationException is expected", false);
        } catch (UnsupportedOperationException ex) {
            // no-op
        }
    }

    @Test
    public void testRemoveAclEntriesEntriesForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        try {
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.ALL));
            fs.removeAclEntries(filePath, aclSpec);
            Assert.assertFalse("UnsupportedOperationException is expected", false);
        } catch (UnsupportedOperationException ex) {
            // no-op
        }
    }

    @Test
    public void testRemoveDefaultAclForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        try {
            fs.removeDefaultAcl(filePath);
            Assert.assertFalse("UnsupportedOperationException is expected", false);
        } catch (UnsupportedOperationException ex) {
            // no-op
        }
    }

    @Test
    public void testRemoveAclForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        try {
            fs.removeAcl(filePath);
            Assert.assertFalse("UnsupportedOperationException is expected", false);
        } catch (UnsupportedOperationException ex) {
            // no-op
        }
    }

    @Test
    public void testSetAclForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        try {
            List<AclEntry> aclSpec = Lists.newArrayList(AclTestHelpers.aclEntry(DEFAULT, GROUP, ITestAzureBlobFilesystemAcl.FOO, ITestAzureBlobFilesystemAcl.ALL), AclTestHelpers.aclEntry(ACCESS, GROUP, ITestAzureBlobFilesystemAcl.BAR, ITestAzureBlobFilesystemAcl.ALL));
            fs.setAcl(filePath, aclSpec);
            Assert.assertFalse("UnsupportedOperationException is expected", false);
        } catch (UnsupportedOperationException ex) {
            // no-op
        }
    }

    @Test
    public void testGetAclStatusForNonNamespaceEnabledAccount() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeTrue((!(fs.getIsNamespaceEnabled())));
        final Path filePath = new Path(methodName.getMethodName());
        fs.create(filePath);
        try {
            AclStatus aclSpec = fs.getAclStatus(filePath);
            Assert.assertFalse("UnsupportedOperationException is expected", false);
        } catch (UnsupportedOperationException ex) {
            // no-op
        }
    }
}

