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
package org.apache.hadoop.tools.util;


import DistCpOptionSwitch.PRESERVE_STATUS_DEFAULT;
import FileAttribute.ACL;
import FileAttribute.BLOCKSIZE;
import FileAttribute.CHECKSUMTYPE;
import FileAttribute.GROUP;
import FileAttribute.PERMISSION;
import FileAttribute.REPLICATION;
import FileAttribute.TIMES;
import FileAttribute.USER;
import FileAttribute.XATTR;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.tools.CopyListingFileStatus;
import org.apache.hadoop.tools.DistCpOptions.FileAttribute;
import org.apache.hadoop.util.ToolRunner;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestDistCpUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestDistCpUtils.class);

    private static final Configuration config = new Configuration();

    private static MiniDFSCluster cluster;

    private static final FsPermission fullPerm = new FsPermission(((short) (777)));

    private static final FsPermission almostFullPerm = new FsPermission(((short) (666)));

    private static final FsPermission noPerm = new FsPermission(((short) (0)));

    @Test
    public void testGetRelativePathRoot() {
        Path root = new Path("/");
        Path child = new Path("/a");
        Assert.assertEquals(DistCpUtils.getRelativePath(root, child), "/a");
    }

    @Test
    public void testGetRelativePath() {
        Path root = new Path("/tmp/abc");
        Path child = new Path("/tmp/abc/xyz/file");
        Assert.assertEquals(DistCpUtils.getRelativePath(root, child), "/xyz/file");
    }

    @Test
    public void testPackAttributes() {
        EnumSet<FileAttribute> attributes = EnumSet.noneOf(FileAttribute.class);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "");
        attributes.add(REPLICATION);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "R");
        attributes.add(BLOCKSIZE);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "RB");
        attributes.add(USER);
        attributes.add(CHECKSUMTYPE);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "RBUC");
        attributes.add(GROUP);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "RBUGC");
        attributes.add(PERMISSION);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "RBUGPC");
        attributes.add(TIMES);
        Assert.assertEquals(DistCpUtils.packAttributes(attributes), "RBUGPCT");
    }

    @Test
    public void testUnpackAttributes() {
        EnumSet<FileAttribute> attributes = EnumSet.allOf(FileAttribute.class);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes("RCBUGPAXT"));
        attributes.remove(REPLICATION);
        attributes.remove(CHECKSUMTYPE);
        attributes.remove(ACL);
        attributes.remove(XATTR);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes("BUGPT"));
        attributes.remove(TIMES);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes("BUGP"));
        attributes.remove(BLOCKSIZE);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes("UGP"));
        attributes.remove(GROUP);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes("UP"));
        attributes.remove(USER);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes("P"));
        attributes.remove(PERMISSION);
        Assert.assertEquals(attributes, DistCpUtils.unpackAttributes(""));
    }

    @Test
    public void testPreserveDefaults() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        // preserve replication, block size, user, group, permission,
        // checksum type and timestamps
        EnumSet<FileAttribute> attributes = DistCpUtils.unpackAttributes(PRESERVE_STATUS_DEFAULT.substring(1));
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertTrue(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreserveAclsforDefaultACL() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(ACL, PERMISSION, XATTR, GROUP, USER, REPLICATION, XATTR, TIMES);
        Path dest = new Path("/tmpdest");
        Path src = new Path("/testsrc");
        fs.mkdirs(src);
        fs.mkdirs(dest);
        List<AclEntry> acls = Lists.newArrayList(aclEntry(DEFAULT, USER, "foo", READ_EXECUTE), aclEntry(ACCESS, USER, READ_WRITE), aclEntry(ACCESS, GROUP, READ), aclEntry(ACCESS, OTHER, READ), aclEntry(ACCESS, USER, "bar", ALL));
        final List<AclEntry> acls1 = Lists.newArrayList(aclEntry(ACCESS, USER, ALL), aclEntry(ACCESS, USER, "user1", ALL), aclEntry(ACCESS, GROUP, READ_EXECUTE), aclEntry(ACCESS, OTHER, EXECUTE));
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setAcl(src, acls);
        fs.setPermission(dest, TestDistCpUtils.noPerm);
        fs.setOwner(dest, "nobody", "nobody-group");
        fs.setTimes(dest, 100, 100);
        fs.setReplication(dest, ((short) (2)));
        fs.setAcl(dest, acls1);
        List<AclEntry> en1 = fs.getAclStatus(src).getEntries();
        List<AclEntry> dd2 = fs.getAclStatus(dest).getEntries();
        Assert.assertNotEquals(en1, dd2);
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        en1 = srcStatus.getAclEntries();
        DistCpUtils.preserve(fs, dest, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dest));
        dd2 = dstStatus.getAclEntries();
        en1 = srcStatus.getAclEntries();
        // FileStatus.equals only compares path field, must explicitly compare all
        // fields
        Assert.assertEquals("getPermission", srcStatus.getPermission(), dstStatus.getPermission());
        Assert.assertEquals("Owner", srcStatus.getOwner(), dstStatus.getOwner());
        Assert.assertEquals("Group", srcStatus.getGroup(), dstStatus.getGroup());
        Assert.assertEquals("AccessTime", srcStatus.getAccessTime(), dstStatus.getAccessTime());
        Assert.assertEquals("ModificationTime", srcStatus.getModificationTime(), dstStatus.getModificationTime());
        Assert.assertEquals("Replication", srcStatus.getReplication(), dstStatus.getReplication());
        Assert.assertArrayEquals(en1.toArray(), dd2.toArray());
    }

    @Test
    public void testPreserveNothingOnDirectory() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.noneOf(FileAttribute.class);
        Path dst = new Path("/tmp/abc");
        Path src = new Path("/tmp/src");
        TestDistCpUtils.createDirectory(fs, src);
        TestDistCpUtils.createDirectory(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertTrue(((dstStatus.getAccessTime()) == 100));
        Assert.assertTrue(((dstStatus.getModificationTime()) == 100));
        Assert.assertTrue(((dstStatus.getReplication()) == 0));
    }

    @Test
    public void testPreservePermissionOnDirectory() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(PERMISSION);
        Path dst = new Path("/tmp/abc");
        Path src = new Path("/tmp/src");
        TestDistCpUtils.createDirectory(fs, src);
        TestDistCpUtils.createDirectory(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertTrue(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
    }

    @Test
    public void testPreserveGroupOnDirectory() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(GROUP);
        Path dst = new Path("/tmp/abc");
        Path src = new Path("/tmp/src");
        TestDistCpUtils.createDirectory(fs, src);
        TestDistCpUtils.createDirectory(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(dstStatus.getGroup()));
    }

    @Test
    public void testPreserveUserOnDirectory() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(USER);
        Path dst = new Path("/tmp/abc");
        Path src = new Path("/tmp/src");
        TestDistCpUtils.createDirectory(fs, src);
        TestDistCpUtils.createDirectory(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
    }

    @Test
    public void testPreserveReplicationOnDirectory() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(REPLICATION);
        Path dst = new Path("/tmp/abc");
        Path src = new Path("/tmp/src");
        TestDistCpUtils.createDirectory(fs, src);
        TestDistCpUtils.createDirectory(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        // Replication shouldn't apply to dirs so this should still be 0 == 0
        Assert.assertTrue(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreserveTimestampOnDirectory() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(TIMES);
        Path dst = new Path("/tmp/abc");
        Path src = new Path("/tmp/src");
        TestDistCpUtils.createDirectory(fs, src);
        TestDistCpUtils.createDirectory(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
    }

    @Test
    public void testPreserveNothingOnFile() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.noneOf(FileAttribute.class);
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreservePermissionOnFile() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(PERMISSION);
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertTrue(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreserveGroupOnFile() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(GROUP);
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreserveUserOnFile() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(USER);
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreserveReplicationOnFile() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(REPLICATION);
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test(timeout = 60000)
    public void testReplFactorNotPreservedOnErasureCodedFile() throws Exception {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        // Case 1: Verify replication attribute not preserved when the source
        // file is erasure coded and the target file is replicated.
        Path srcECDir = new Path("/tmp/srcECDir");
        Path srcECFile = new Path(srcECDir, "srcECFile");
        Path dstReplDir = new Path("/tmp/dstReplDir");
        Path dstReplFile = new Path(dstReplDir, "destReplFile");
        fs.mkdirs(srcECDir);
        fs.mkdirs(dstReplDir);
        String[] args = new String[]{ "-setPolicy", "-path", "/tmp/srcECDir", "-policy", "XOR-2-1-1024k" };
        int res = ToolRunner.run(TestDistCpUtils.config, new org.apache.hadoop.hdfs.tools.ECAdmin(TestDistCpUtils.config), args);
        Assert.assertEquals("Setting EC policy should succeed!", 0, res);
        verifyReplFactorNotPreservedOnErasureCodedFile(srcECFile, true, dstReplFile, false);
        // Case 2: Verify replication attribute not preserved when the source
        // file is replicated and the target file is erasure coded.
        Path srcReplDir = new Path("/tmp/srcReplDir");
        Path srcReplFile = new Path(srcReplDir, "srcReplFile");
        Path dstECDir = new Path("/tmp/dstECDir");
        Path dstECFile = new Path(dstECDir, "destECFile");
        fs.mkdirs(srcReplDir);
        fs.mkdirs(dstECDir);
        args = new String[]{ "-setPolicy", "-path", "/tmp/dstECDir", "-policy", "XOR-2-1-1024k" };
        res = ToolRunner.run(TestDistCpUtils.config, new org.apache.hadoop.hdfs.tools.ECAdmin(TestDistCpUtils.config), args);
        Assert.assertEquals("Setting EC policy should succeed!", 0, res);
        verifyReplFactorNotPreservedOnErasureCodedFile(srcReplFile, false, dstECFile, true);
        // Case 3: Verify replication attribute not altered from the default
        // INodeFile.DEFAULT_REPL_FOR_STRIPED_BLOCKS when both source and
        // target files are erasure coded.
        verifyReplFactorNotPreservedOnErasureCodedFile(srcECFile, true, dstECFile, true);
    }

    @Test
    public void testPreserveTimestampOnFile() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.of(TIMES);
        Path dst = new Path("/tmp/dest2");
        Path src = new Path("/tmp/src2");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, dst);
        fs.setPermission(src, TestDistCpUtils.fullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(dst, TestDistCpUtils.noPerm);
        fs.setOwner(dst, "nobody", "nobody-group");
        fs.setTimes(dst, 100, 100);
        fs.setReplication(dst, ((short) (2)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, dst, srcStatus, attributes, false);
        CopyListingFileStatus dstStatus = new CopyListingFileStatus(fs.getFileStatus(dst));
        // FileStatus.equals only compares path field, must explicitly compare all fields
        Assert.assertFalse(srcStatus.getPermission().equals(dstStatus.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(dstStatus.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(dstStatus.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (dstStatus.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (dstStatus.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (dstStatus.getReplication())));
    }

    @Test
    public void testPreserveOnFileUpwardRecursion() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.allOf(FileAttribute.class);
        // Remove ACL because tests run with dfs.namenode.acls.enabled false
        attributes.remove(ACL);
        Path src = new Path("/tmp/src2");
        Path f0 = new Path("/f0");
        Path f1 = new Path("/d1/f1");
        Path f2 = new Path("/d1/d2/f2");
        Path d1 = new Path("/d1/");
        Path d2 = new Path("/d1/d2/");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, f0);
        TestDistCpUtils.createFile(fs, f1);
        TestDistCpUtils.createFile(fs, f2);
        fs.setPermission(src, TestDistCpUtils.almostFullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(d1, TestDistCpUtils.fullPerm);
        fs.setOwner(d1, "anybody", "anybody-group");
        fs.setTimes(d1, 400, 400);
        fs.setReplication(d1, ((short) (3)));
        fs.setPermission(d2, TestDistCpUtils.fullPerm);
        fs.setOwner(d2, "anybody", "anybody-group");
        fs.setTimes(d2, 300, 300);
        fs.setReplication(d2, ((short) (3)));
        fs.setPermission(f0, TestDistCpUtils.fullPerm);
        fs.setOwner(f0, "anybody", "anybody-group");
        fs.setTimes(f0, 200, 200);
        fs.setReplication(f0, ((short) (3)));
        fs.setPermission(f1, TestDistCpUtils.fullPerm);
        fs.setOwner(f1, "anybody", "anybody-group");
        fs.setTimes(f1, 200, 200);
        fs.setReplication(f1, ((short) (3)));
        fs.setPermission(f2, TestDistCpUtils.fullPerm);
        fs.setOwner(f2, "anybody", "anybody-group");
        fs.setTimes(f2, 200, 200);
        fs.setReplication(f2, ((short) (3)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, f2, srcStatus, attributes, false);
        TestDistCpUtils.cluster.triggerHeartbeats();
        // FileStatus.equals only compares path field, must explicitly compare all fields
        // attributes of src -> f2 ? should be yes
        CopyListingFileStatus f2Status = new CopyListingFileStatus(fs.getFileStatus(f2));
        Assert.assertTrue(srcStatus.getPermission().equals(f2Status.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(f2Status.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(f2Status.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (f2Status.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (f2Status.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) == (f2Status.getReplication())));
        // attributes of src -> f1 ? should be no
        CopyListingFileStatus f1Status = new CopyListingFileStatus(fs.getFileStatus(f1));
        Assert.assertFalse(srcStatus.getPermission().equals(f1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f1Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f1Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f1Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f1Status.getReplication())));
        // attributes of src -> f0 ? should be no
        CopyListingFileStatus f0Status = new CopyListingFileStatus(fs.getFileStatus(f0));
        Assert.assertFalse(srcStatus.getPermission().equals(f0Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f0Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f0Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f0Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f0Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f0Status.getReplication())));
        // attributes of src -> d2 ? should be no
        CopyListingFileStatus d2Status = new CopyListingFileStatus(fs.getFileStatus(d2));
        Assert.assertFalse(srcStatus.getPermission().equals(d2Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d2Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d2Status.getGroup()));
        Assert.assertTrue(((d2Status.getAccessTime()) == 300));
        Assert.assertTrue(((d2Status.getModificationTime()) == 300));
        Assert.assertFalse(((srcStatus.getReplication()) == (d2Status.getReplication())));
        // attributes of src -> d1 ? should be no
        CopyListingFileStatus d1Status = new CopyListingFileStatus(fs.getFileStatus(d1));
        Assert.assertFalse(srcStatus.getPermission().equals(d1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d1Status.getGroup()));
        Assert.assertTrue(((d1Status.getAccessTime()) == 400));
        Assert.assertTrue(((d1Status.getModificationTime()) == 400));
        Assert.assertFalse(((srcStatus.getReplication()) == (d1Status.getReplication())));
    }

    @Test
    public void testPreserveOnDirectoryUpwardRecursion() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.allOf(FileAttribute.class);
        // Remove ACL because tests run with dfs.namenode.acls.enabled false
        attributes.remove(ACL);
        Path src = new Path("/tmp/src2");
        Path f0 = new Path("/f0");
        Path f1 = new Path("/d1/f1");
        Path f2 = new Path("/d1/d2/f2");
        Path d1 = new Path("/d1/");
        Path d2 = new Path("/d1/d2/");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, f0);
        TestDistCpUtils.createFile(fs, f1);
        TestDistCpUtils.createFile(fs, f2);
        fs.setPermission(src, TestDistCpUtils.almostFullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(d1, TestDistCpUtils.fullPerm);
        fs.setOwner(d1, "anybody", "anybody-group");
        fs.setTimes(d1, 400, 400);
        fs.setReplication(d1, ((short) (3)));
        fs.setPermission(d2, TestDistCpUtils.fullPerm);
        fs.setOwner(d2, "anybody", "anybody-group");
        fs.setTimes(d2, 300, 300);
        fs.setReplication(d2, ((short) (3)));
        fs.setPermission(f0, TestDistCpUtils.fullPerm);
        fs.setOwner(f0, "anybody", "anybody-group");
        fs.setTimes(f0, 200, 200);
        fs.setReplication(f0, ((short) (3)));
        fs.setPermission(f1, TestDistCpUtils.fullPerm);
        fs.setOwner(f1, "anybody", "anybody-group");
        fs.setTimes(f1, 200, 200);
        fs.setReplication(f1, ((short) (3)));
        fs.setPermission(f2, TestDistCpUtils.fullPerm);
        fs.setOwner(f2, "anybody", "anybody-group");
        fs.setTimes(f2, 200, 200);
        fs.setReplication(f2, ((short) (3)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, d2, srcStatus, attributes, false);
        TestDistCpUtils.cluster.triggerHeartbeats();
        // FileStatus.equals only compares path field, must explicitly compare all fields
        // attributes of src -> d2 ? should be yes
        CopyListingFileStatus d2Status = new CopyListingFileStatus(fs.getFileStatus(d2));
        Assert.assertTrue(srcStatus.getPermission().equals(d2Status.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(d2Status.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(d2Status.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (d2Status.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (d2Status.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) != (d2Status.getReplication())));
        // attributes of src -> d1 ? should be no
        CopyListingFileStatus d1Status = new CopyListingFileStatus(fs.getFileStatus(d1));
        Assert.assertFalse(srcStatus.getPermission().equals(d1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d1Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (d1Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (d1Status.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) != (d1Status.getReplication())));
        // attributes of src -> f2 ? should be no
        CopyListingFileStatus f2Status = new CopyListingFileStatus(fs.getFileStatus(f2));
        Assert.assertFalse(srcStatus.getPermission().equals(f2Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f2Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f2Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f2Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f2Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f2Status.getReplication())));
        // attributes of src -> f1 ? should be no
        CopyListingFileStatus f1Status = new CopyListingFileStatus(fs.getFileStatus(f1));
        Assert.assertFalse(srcStatus.getPermission().equals(f1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f1Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f1Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f1Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f1Status.getReplication())));
        // attributes of src -> f0 ? should be no
        CopyListingFileStatus f0Status = new CopyListingFileStatus(fs.getFileStatus(f0));
        Assert.assertFalse(srcStatus.getPermission().equals(f0Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f0Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f0Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f0Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f0Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f0Status.getReplication())));
    }

    @Test
    public void testPreserveOnFileDownwardRecursion() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.allOf(FileAttribute.class);
        // Remove ACL because tests run with dfs.namenode.acls.enabled false
        attributes.remove(ACL);
        Path src = new Path("/tmp/src2");
        Path f0 = new Path("/f0");
        Path f1 = new Path("/d1/f1");
        Path f2 = new Path("/d1/d2/f2");
        Path d1 = new Path("/d1/");
        Path d2 = new Path("/d1/d2/");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, f0);
        TestDistCpUtils.createFile(fs, f1);
        TestDistCpUtils.createFile(fs, f2);
        fs.setPermission(src, TestDistCpUtils.almostFullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(d1, TestDistCpUtils.fullPerm);
        fs.setOwner(d1, "anybody", "anybody-group");
        fs.setTimes(d1, 400, 400);
        fs.setReplication(d1, ((short) (3)));
        fs.setPermission(d2, TestDistCpUtils.fullPerm);
        fs.setOwner(d2, "anybody", "anybody-group");
        fs.setTimes(d2, 300, 300);
        fs.setReplication(d2, ((short) (3)));
        fs.setPermission(f0, TestDistCpUtils.fullPerm);
        fs.setOwner(f0, "anybody", "anybody-group");
        fs.setTimes(f0, 200, 200);
        fs.setReplication(f0, ((short) (3)));
        fs.setPermission(f1, TestDistCpUtils.fullPerm);
        fs.setOwner(f1, "anybody", "anybody-group");
        fs.setTimes(f1, 200, 200);
        fs.setReplication(f1, ((short) (3)));
        fs.setPermission(f2, TestDistCpUtils.fullPerm);
        fs.setOwner(f2, "anybody", "anybody-group");
        fs.setTimes(f2, 200, 200);
        fs.setReplication(f2, ((short) (3)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, f0, srcStatus, attributes, false);
        TestDistCpUtils.cluster.triggerHeartbeats();
        // FileStatus.equals only compares path field, must explicitly compare all fields
        // attributes of src -> f0 ? should be yes
        CopyListingFileStatus f0Status = new CopyListingFileStatus(fs.getFileStatus(f0));
        Assert.assertTrue(srcStatus.getPermission().equals(f0Status.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(f0Status.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(f0Status.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (f0Status.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (f0Status.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) == (f0Status.getReplication())));
        // attributes of src -> f1 ? should be no
        CopyListingFileStatus f1Status = new CopyListingFileStatus(fs.getFileStatus(f1));
        Assert.assertFalse(srcStatus.getPermission().equals(f1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f1Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f1Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f1Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f1Status.getReplication())));
        // attributes of src -> f2 ? should be no
        CopyListingFileStatus f2Status = new CopyListingFileStatus(fs.getFileStatus(f2));
        Assert.assertFalse(srcStatus.getPermission().equals(f2Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f2Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f2Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f2Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f2Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f2Status.getReplication())));
        // attributes of src -> d1 ? should be no
        CopyListingFileStatus d1Status = new CopyListingFileStatus(fs.getFileStatus(d1));
        Assert.assertFalse(srcStatus.getPermission().equals(d1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d1Status.getGroup()));
        Assert.assertTrue(((d1Status.getAccessTime()) == 400));
        Assert.assertTrue(((d1Status.getModificationTime()) == 400));
        Assert.assertFalse(((srcStatus.getReplication()) == (d1Status.getReplication())));
        // attributes of src -> d2 ? should be no
        CopyListingFileStatus d2Status = new CopyListingFileStatus(fs.getFileStatus(d2));
        Assert.assertFalse(srcStatus.getPermission().equals(d2Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d2Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d2Status.getGroup()));
        Assert.assertTrue(((d2Status.getAccessTime()) == 300));
        Assert.assertTrue(((d2Status.getModificationTime()) == 300));
        Assert.assertFalse(((srcStatus.getReplication()) == (d2Status.getReplication())));
    }

    @Test
    public void testPreserveOnDirectoryDownwardRecursion() throws IOException {
        FileSystem fs = FileSystem.get(TestDistCpUtils.config);
        EnumSet<FileAttribute> attributes = EnumSet.allOf(FileAttribute.class);
        // Remove ACL because tests run with dfs.namenode.acls.enabled false
        attributes.remove(ACL);
        Path src = new Path("/tmp/src2");
        Path f0 = new Path("/f0");
        Path f1 = new Path("/d1/f1");
        Path f2 = new Path("/d1/d2/f2");
        Path d1 = new Path("/d1/");
        Path d2 = new Path("/d1/d2/");
        Path root = new Path("/");
        TestDistCpUtils.createFile(fs, src);
        TestDistCpUtils.createFile(fs, f0);
        TestDistCpUtils.createFile(fs, f1);
        TestDistCpUtils.createFile(fs, f2);
        fs.setPermission(src, TestDistCpUtils.almostFullPerm);
        fs.setOwner(src, "somebody", "somebody-group");
        fs.setTimes(src, 0, 0);
        fs.setReplication(src, ((short) (1)));
        fs.setPermission(root, TestDistCpUtils.fullPerm);
        fs.setOwner(root, "anybody", "anybody-group");
        fs.setTimes(root, 400, 400);
        fs.setReplication(root, ((short) (3)));
        fs.setPermission(d1, TestDistCpUtils.fullPerm);
        fs.setOwner(d1, "anybody", "anybody-group");
        fs.setTimes(d1, 400, 400);
        fs.setReplication(d1, ((short) (3)));
        fs.setPermission(d2, TestDistCpUtils.fullPerm);
        fs.setOwner(d2, "anybody", "anybody-group");
        fs.setTimes(d2, 300, 300);
        fs.setReplication(d2, ((short) (3)));
        fs.setPermission(f0, TestDistCpUtils.fullPerm);
        fs.setOwner(f0, "anybody", "anybody-group");
        fs.setTimes(f0, 200, 200);
        fs.setReplication(f0, ((short) (3)));
        fs.setPermission(f1, TestDistCpUtils.fullPerm);
        fs.setOwner(f1, "anybody", "anybody-group");
        fs.setTimes(f1, 200, 200);
        fs.setReplication(f1, ((short) (3)));
        fs.setPermission(f2, TestDistCpUtils.fullPerm);
        fs.setOwner(f2, "anybody", "anybody-group");
        fs.setTimes(f2, 200, 200);
        fs.setReplication(f2, ((short) (3)));
        CopyListingFileStatus srcStatus = new CopyListingFileStatus(fs.getFileStatus(src));
        DistCpUtils.preserve(fs, root, srcStatus, attributes, false);
        TestDistCpUtils.cluster.triggerHeartbeats();
        // FileStatus.equals only compares path field, must explicitly compare all fields
        // attributes of src -> root ? should be yes
        CopyListingFileStatus rootStatus = new CopyListingFileStatus(fs.getFileStatus(root));
        Assert.assertTrue(srcStatus.getPermission().equals(rootStatus.getPermission()));
        Assert.assertTrue(srcStatus.getOwner().equals(rootStatus.getOwner()));
        Assert.assertTrue(srcStatus.getGroup().equals(rootStatus.getGroup()));
        Assert.assertTrue(((srcStatus.getAccessTime()) == (rootStatus.getAccessTime())));
        Assert.assertTrue(((srcStatus.getModificationTime()) == (rootStatus.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) != (rootStatus.getReplication())));
        // attributes of src -> d1 ? should be no
        CopyListingFileStatus d1Status = new CopyListingFileStatus(fs.getFileStatus(d1));
        Assert.assertFalse(srcStatus.getPermission().equals(d1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d1Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (d1Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (d1Status.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) != (d1Status.getReplication())));
        // attributes of src -> d2 ? should be no
        CopyListingFileStatus d2Status = new CopyListingFileStatus(fs.getFileStatus(d2));
        Assert.assertFalse(srcStatus.getPermission().equals(d2Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(d2Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(d2Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (d2Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (d2Status.getModificationTime())));
        Assert.assertTrue(((srcStatus.getReplication()) != (d2Status.getReplication())));
        // attributes of src -> f0 ? should be no
        CopyListingFileStatus f0Status = new CopyListingFileStatus(fs.getFileStatus(f0));
        Assert.assertFalse(srcStatus.getPermission().equals(f0Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f0Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f0Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f0Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f0Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f0Status.getReplication())));
        // attributes of src -> f1 ? should be no
        CopyListingFileStatus f1Status = new CopyListingFileStatus(fs.getFileStatus(f1));
        Assert.assertFalse(srcStatus.getPermission().equals(f1Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f1Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f1Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f1Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f1Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f1Status.getReplication())));
        // attributes of src -> f2 ? should be no
        CopyListingFileStatus f2Status = new CopyListingFileStatus(fs.getFileStatus(f2));
        Assert.assertFalse(srcStatus.getPermission().equals(f2Status.getPermission()));
        Assert.assertFalse(srcStatus.getOwner().equals(f2Status.getOwner()));
        Assert.assertFalse(srcStatus.getGroup().equals(f2Status.getGroup()));
        Assert.assertFalse(((srcStatus.getAccessTime()) == (f2Status.getAccessTime())));
        Assert.assertFalse(((srcStatus.getModificationTime()) == (f2Status.getModificationTime())));
        Assert.assertFalse(((srcStatus.getReplication()) == (f2Status.getReplication())));
    }

    private static Random rand = new Random();
}

