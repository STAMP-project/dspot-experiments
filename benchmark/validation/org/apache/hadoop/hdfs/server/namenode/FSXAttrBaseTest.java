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


import XAttrSetFlag.CREATE;
import XAttrSetFlag.REPLACE;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntryScope;
import org.apache.hadoop.fs.permission.AclEntryType;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests NameNode interaction for all XAttr APIs.
 * This test suite covers restarting the NN, saving a new checkpoint.
 */
public class FSXAttrBaseTest {
    protected static MiniDFSCluster dfsCluster;

    protected static Configuration conf;

    private static int pathCount = 0;

    protected static Path path;

    protected static Path filePath;

    protected static Path rawPath;

    protected static Path rawFilePath;

    // XAttrs
    protected static final String name1 = "user.a1";

    protected static final byte[] value1 = new byte[]{ 49, 50, 51 };

    protected static final byte[] newValue1 = new byte[]{ 49, 49, 49 };

    protected static final String name2 = "user.a2";

    protected static final byte[] value2 = new byte[]{ 55, 56, 57 };

    protected static final String name3 = "user.a3";

    protected static final String name4 = "user.a4";

    protected static final String raw1 = "raw.a1";

    protected static final String raw2 = "raw.a2";

    protected static final String security1 = HdfsServerConstants.SECURITY_XATTR_UNREADABLE_BY_SUPERUSER;

    private static final int MAX_SIZE = FSXAttrBaseTest.security1.length();

    protected FileSystem fs;

    private static final UserGroupInformation BRUCE = UserGroupInformation.createUserForTesting("bruce", new String[]{  });

    private static final UserGroupInformation DIANA = UserGroupInformation.createUserForTesting("diana", new String[]{  });

    /**
     * Tests for creating xattr
     * 1. Create an xattr using XAttrSetFlag.CREATE.
     * 2. Create an xattr which already exists and expect an exception.
     * 3. Create multiple xattrs.
     * 4. Restart NN and save checkpoint scenarios.
     */
    @Test(timeout = 120000)
    public void testCreateXAttr() throws Exception {
        Map<String, byte[]> expectedXAttrs = Maps.newHashMap();
        expectedXAttrs.put(FSXAttrBaseTest.name1, FSXAttrBaseTest.value1);
        expectedXAttrs.put(FSXAttrBaseTest.name2, null);
        expectedXAttrs.put(FSXAttrBaseTest.security1, null);
        doTestCreateXAttr(FSXAttrBaseTest.filePath, expectedXAttrs);
        expectedXAttrs.put(FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1);
        doTestCreateXAttr(FSXAttrBaseTest.rawFilePath, expectedXAttrs);
    }

    /**
     * Tests for replacing xattr
     * 1. Replace an xattr using XAttrSetFlag.REPLACE.
     * 2. Replace an xattr which doesn't exist and expect an exception.
     * 3. Create multiple xattrs and replace some.
     * 4. Restart NN and save checkpoint scenarios.
     */
    @Test(timeout = 120000)
    public void testReplaceXAttr() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.newValue1, EnumSet.of(REPLACE));
        Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(FSXAttrBaseTest.newValue1, xattrs.get(FSXAttrBaseTest.name1));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        // Replace xattr which does not exist.
        try {
            fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(REPLACE));
            Assert.fail("Replacing xattr which does not exist should fail.");
        } catch (IOException e) {
        }
        // Create two xattrs, then replace one
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, null, EnumSet.of(REPLACE));
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name2));
        FSXAttrBaseTest.restart(false);
        initFileSystem();
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name2));
        FSXAttrBaseTest.restart(true);
        initFileSystem();
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name2));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
    }

    /**
     * Tests for setting xattr
     * 1. Set xattr with XAttrSetFlag.CREATE|XAttrSetFlag.REPLACE flag.
     * 2. Set xattr with illegal name.
     * 3. Set xattr without XAttrSetFlag.
     * 4. Set xattr and total number exceeds max limit.
     * 5. Set xattr and name is too long.
     * 6. Set xattr and value is too long.
     */
    @Test(timeout = 120000)
    public void testSetXAttr() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE, REPLACE));
        Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        // Set xattr with null name
        try {
            fs.setXAttr(FSXAttrBaseTest.path, null, FSXAttrBaseTest.value1, EnumSet.of(CREATE, REPLACE));
            Assert.fail("Setting xattr with null name should fail.");
        } catch (NullPointerException e) {
            GenericTestUtils.assertExceptionContains("XAttr name cannot be null", e);
        } catch (RemoteException e) {
            GenericTestUtils.assertExceptionContains(("Required param xattr.name for " + "op: SETXATTR is null or empty"), e);
        }
        // Set xattr with empty name: "user."
        try {
            fs.setXAttr(FSXAttrBaseTest.path, "user.", FSXAttrBaseTest.value1, EnumSet.of(CREATE, REPLACE));
            Assert.fail("Setting xattr with empty name should fail.");
        } catch (RemoteException e) {
            Assert.assertEquals(("Unexpected RemoteException: " + e), e.getClassName(), HadoopIllegalArgumentException.class.getCanonicalName());
            GenericTestUtils.assertExceptionContains("XAttr name cannot be empty", e);
        } catch (HadoopIllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("XAttr name cannot be empty", e);
        }
        // Set xattr with invalid name: "a1"
        try {
            fs.setXAttr(FSXAttrBaseTest.path, "a1", FSXAttrBaseTest.value1, EnumSet.of(CREATE, REPLACE));
            Assert.fail(("Setting xattr with invalid name prefix or without " + "name prefix should fail."));
        } catch (RemoteException e) {
            Assert.assertEquals(("Unexpected RemoteException: " + e), e.getClassName(), HadoopIllegalArgumentException.class.getCanonicalName());
            GenericTestUtils.assertExceptionContains("XAttr name must be prefixed", e);
        } catch (HadoopIllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("XAttr name must be prefixed", e);
        }
        // Set xattr without XAttrSetFlag
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1);
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        // XAttr exists, and replace it using CREATE|REPLACE flag.
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.newValue1, EnumSet.of(CREATE, REPLACE));
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(FSXAttrBaseTest.newValue1, xattrs.get(FSXAttrBaseTest.name1));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        // Total number exceeds max limit
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1);
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2);
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name3, null);
        try {
            fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name4, null);
            Assert.fail(("Setting xattr should fail if total number of xattrs " + "for inode exceeds max limit."));
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Cannot add additional XAttr", e);
        }
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name3);
        // Name length exceeds max limit
        String longName = "user.0123456789abcdefX0123456789abcdefX0123456789abcdef";
        try {
            fs.setXAttr(FSXAttrBaseTest.path, longName, null);
            Assert.fail("Setting xattr should fail if name is too long.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("XAttr is too big", e);
            GenericTestUtils.assertExceptionContains("total size is 50", e);
        }
        // Value length exceeds max limit
        byte[] longValue = new byte[FSXAttrBaseTest.MAX_SIZE];
        try {
            fs.setXAttr(FSXAttrBaseTest.path, "user.a", longValue);
            Assert.fail("Setting xattr should fail if value is too long.");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("XAttr is too big", e);
            GenericTestUtils.assertExceptionContains("total size is 38", e);
        }
        // Name + value exactly equal the limit
        String name = "user.111";
        byte[] value = new byte[(FSXAttrBaseTest.MAX_SIZE) - 3];
        fs.setXAttr(FSXAttrBaseTest.path, name, value);
    }

    /**
     * getxattr tests. Test that getxattr throws an exception if any of
     * the following are true:
     * an xattr that was requested doesn't exist
     * the caller specifies an unknown namespace
     * the caller doesn't have access to the namespace
     * the caller doesn't have permission to get the value of the xattr
     * the caller does not have search access to the parent directory
     * the caller has only read access to the owning directory
     * the caller has only search access to the owning directory and
     * execute/search access to the actual entity
     * the caller does not have search access to the owning directory and read
     * access to the actual entity
     */
    @Test(timeout = 120000)
    public void testGetXAttrs() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        final byte[] theValue = fs.getXAttr(FSXAttrBaseTest.path, "USER.a2");
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, theValue);
        /* An XAttr that was requested does not exist. */
        try {
            final byte[] value = fs.getXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name3);
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("At least one of the attributes provided was not found.", e);
        }
        /* Throw an exception if an xattr that was requested does not exist. */
        {
            final List<String> names = Lists.newArrayList();
            names.add(FSXAttrBaseTest.name1);
            names.add(FSXAttrBaseTest.name2);
            names.add(FSXAttrBaseTest.name3);
            try {
                final Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.path, names);
                Assert.fail("expected IOException");
            } catch (IOException e) {
                GenericTestUtils.assertExceptionContains("At least one of the attributes provided was not found.", e);
            }
        }
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
        /* Unknown namespace should throw an exception. */
        try {
            final byte[] xattr = fs.getXAttr(FSXAttrBaseTest.path, "wackynamespace.foo");
            Assert.fail("expected IOException");
        } catch (Exception e) {
            GenericTestUtils.assertExceptionContains(("An XAttr name must be prefixed with " + ("user/trusted/security/system/raw, " + "followed by a '.'")), e);
        }
        /* The 'trusted' namespace should not be accessible and should throw an
        exception.
         */
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        fs.setXAttr(FSXAttrBaseTest.path, "trusted.foo", "1234".getBytes());
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    final byte[] xattr = userFs.getXAttr(FSXAttrBaseTest.path, "trusted.foo");
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("User doesn't have permission", e);
        }
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, "1234".getBytes());
        /* Test that an exception is thrown if the caller doesn't have permission to
        get the value of the xattr.
         */
        /* Set access so that only the owner has access. */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (448))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    final byte[] xattr = userFs.getXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* The caller must have search access to the parent directory. */
        final Path childDir = new Path(FSXAttrBaseTest.path, ("child" + (FSXAttrBaseTest.pathCount)));
        /* Set access to parent so that only the owner has access. */
        FileSystem.mkdirs(fs, childDir, FsPermission.createImmutable(((short) (448))));
        fs.setXAttr(childDir, FSXAttrBaseTest.name1, "1234".getBytes());
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    final byte[] xattr = userFs.getXAttr(childDir, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that read access to the owning directory is not good enough. */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (452))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    final byte[] xattr = userFs.getXAttr(childDir, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that search access to the owning directory and search/execute
        access to the actual entity with extended attributes is not good enough.
         */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (449))));
        fs.setPermission(childDir, new FsPermission(((short) (449))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    final byte[] xattr = userFs.getXAttr(childDir, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that search access to the owning directory and read access to
        the actual entity with the extended attribute is good enough.
         */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (449))));
        fs.setPermission(childDir, new FsPermission(((short) (452))));
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                final byte[] xattr = userFs.getXAttr(childDir, FSXAttrBaseTest.name1);
                return null;
            }
        });
    }

    /**
     * Tests for removing xattr
     * 1. Remove xattr.
     * 2. Restart NN and save checkpoint scenarios.
     */
    @Test(timeout = 120000)
    public void testRemoveXAttr() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name3, null, EnumSet.of(CREATE));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
        Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name3));
        FSXAttrBaseTest.restart(false);
        initFileSystem();
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name3));
        FSXAttrBaseTest.restart(true);
        initFileSystem();
        xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 1);
        Assert.assertArrayEquals(new byte[0], xattrs.get(FSXAttrBaseTest.name3));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name3);
    }

    /**
     * removexattr tests. Test that removexattr throws an exception if any of
     * the following are true:
     * an xattr that was requested doesn't exist
     * the caller specifies an unknown namespace
     * the caller doesn't have access to the namespace
     * the caller doesn't have permission to get the value of the xattr
     * the caller does not have "execute" (scan) access to the parent directory
     * the caller has only read access to the owning directory
     * the caller has only execute access to the owning directory and execute
     * access to the actual entity
     * the caller does not have execute access to the owning directory and write
     * access to the actual entity
     */
    @Test(timeout = 120000)
    public void testRemoveXAttrPermissions() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name3, null, EnumSet.of(CREATE));
        try {
            fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
            fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("No matching attributes found", e);
        }
        /* Unknown namespace should throw an exception. */
        final String expectedExceptionString = "An XAttr name must be prefixed " + "with user/trusted/security/system/raw, followed by a '.'";
        try {
            fs.removeXAttr(FSXAttrBaseTest.path, "wackynamespace.foo");
            Assert.fail("expected IOException");
        } catch (RemoteException e) {
            Assert.assertEquals(("Unexpected RemoteException: " + e), e.getClassName(), HadoopIllegalArgumentException.class.getCanonicalName());
            GenericTestUtils.assertExceptionContains(expectedExceptionString, e);
        } catch (HadoopIllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains(expectedExceptionString, e);
        }
        /* The 'trusted' namespace should not be accessible and should throw an
        exception.
         */
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        fs.setXAttr(FSXAttrBaseTest.path, "trusted.foo", "1234".getBytes());
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.removeXAttr(FSXAttrBaseTest.path, "trusted.foo");
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("User doesn't have permission", e);
        } finally {
            fs.removeXAttr(FSXAttrBaseTest.path, "trusted.foo");
        }
        /* Test that an exception is thrown if the caller doesn't have permission to
        get the value of the xattr.
         */
        /* Set access so that only the owner has access. */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (448))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* The caller must have "execute" (scan) access to the parent directory. */
        final Path childDir = new Path(FSXAttrBaseTest.path, ("child" + (FSXAttrBaseTest.pathCount)));
        /* Set access to parent so that only the owner has access. */
        FileSystem.mkdirs(fs, childDir, FsPermission.createImmutable(((short) (448))));
        fs.setXAttr(childDir, FSXAttrBaseTest.name1, "1234".getBytes());
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.removeXAttr(childDir, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that read access to the owning directory is not good enough. */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (452))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.removeXAttr(childDir, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that execute access to the owning directory and scan access to
        the actual entity with extended attributes is not good enough.
         */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (449))));
        fs.setPermission(childDir, new FsPermission(((short) (449))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.removeXAttr(childDir, FSXAttrBaseTest.name1);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that execute access to the owning directory and write access to
        the actual entity with extended attributes is good enough.
         */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (449))));
        fs.setPermission(childDir, new FsPermission(((short) (454))));
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                userFs.removeXAttr(childDir, FSXAttrBaseTest.name1);
                return null;
            }
        });
    }

    @Test(timeout = 120000)
    public void testRenameFileWithXAttr() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        Path renamePath = new Path(((FSXAttrBaseTest.path.toString()) + "-rename"));
        fs.rename(FSXAttrBaseTest.path, renamePath);
        Map<String, byte[]> xattrs = fs.getXAttrs(renamePath);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, xattrs.get(FSXAttrBaseTest.name2));
        fs.removeXAttr(renamePath, FSXAttrBaseTest.name1);
        fs.removeXAttr(renamePath, FSXAttrBaseTest.name2);
    }

    /**
     * Test the listXAttrs api.
     * listXAttrs on a path that doesn't exist.
     * listXAttrs on a path with no XAttrs
     * Check basic functionality.
     * Check that read access to parent dir is not enough to get xattr names
     * Check that write access to the parent dir is not enough to get names
     * Check that execute/scan access to the parent dir is sufficient to get
     *  xattr names.
     */
    @Test(timeout = 120000)
    public void testListXAttrs() throws Exception {
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        /* listXAttrs in a path that doesn't exist. */
        try {
            fs.listXAttrs(FSXAttrBaseTest.path);
            Assert.fail("expected FileNotFoundException");
        } catch (FileNotFoundException e) {
            GenericTestUtils.assertExceptionContains("cannot find", e);
        }
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        /* listXAttrs on a path with no XAttrs. */
        final List<String> noXAttrs = fs.listXAttrs(FSXAttrBaseTest.path);
        Assert.assertTrue("XAttrs were found?", ((noXAttrs.size()) == 0));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        /**
         * Check basic functionality.
         */
        final List<String> xattrNames = fs.listXAttrs(FSXAttrBaseTest.path);
        Assert.assertTrue(xattrNames.contains(FSXAttrBaseTest.name1));
        Assert.assertTrue(xattrNames.contains(FSXAttrBaseTest.name2));
        Assert.assertTrue(((xattrNames.size()) == 2));
        /* Check that read access to parent dir is not enough to get xattr names. */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (452))));
        final Path childDir = new Path(FSXAttrBaseTest.path, ("child" + (FSXAttrBaseTest.pathCount)));
        FileSystem.mkdirs(fs, childDir, FsPermission.createImmutable(((short) (448))));
        fs.setXAttr(childDir, FSXAttrBaseTest.name1, "1234".getBytes());
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.listXAttrs(childDir);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that write access to the parent dir is not enough to get names. */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (450))));
        try {
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.listXAttrs(childDir);
                    return null;
                }
            });
            Assert.fail("expected IOException");
        } catch (IOException e) {
            GenericTestUtils.assertExceptionContains("Permission denied", e);
        }
        /* Check that execute/scan access to the parent dir is not
        sufficient to get xattr names.
         */
        fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (449))));
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                try {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    userFs.listXAttrs(childDir);
                    Assert.fail("expected AccessControlException");
                } catch (AccessControlException ace) {
                    GenericTestUtils.assertExceptionContains("Permission denied", ace);
                }
                return null;
            }
        });
        /* Test that xattrs in the "trusted" namespace are filtered correctly. */
        // Allow the user to read child path.
        fs.setPermission(childDir, new FsPermission(((short) (452))));
        fs.setXAttr(childDir, "trusted.myxattr", "1234".getBytes());
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                List<String> xattrs = userFs.listXAttrs(childDir);
                Assert.assertTrue(((xattrs.size()) == 1));
                Assert.assertEquals(FSXAttrBaseTest.name1, xattrs.get(0));
                return null;
            }
        });
        Assert.assertTrue(((fs.listXAttrs(childDir).size()) == 2));
    }

    /**
     * Steps:
     * 1) Set xattrs on a file.
     * 2) Remove xattrs from that file.
     * 3) Save a checkpoint and restart NN.
     * 4) Set xattrs again on the same file.
     * 5) Remove xattrs from that file.
     * 6) Restart NN without saving a checkpoint.
     * 7) Set xattrs again on the same file.
     */
    @Test(timeout = 120000)
    public void testCleanupXAttrs() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
        FSXAttrBaseTest.restart(true);
        initFileSystem();
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
        FSXAttrBaseTest.restart(false);
        initFileSystem();
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fs.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
        fs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
        Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertEquals(xattrs.size(), 2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, xattrs.get(FSXAttrBaseTest.name2));
    }

    @Test(timeout = 120000)
    public void testXAttrAcl() throws Exception {
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setOwner(FSXAttrBaseTest.path, FSXAttrBaseTest.BRUCE.getUserName(), null);
        FileSystem fsAsBruce = createFileSystem(FSXAttrBaseTest.BRUCE);
        FileSystem fsAsDiana = createFileSystem(FSXAttrBaseTest.DIANA);
        fsAsBruce.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1, FSXAttrBaseTest.value1);
        Map<String, byte[]> xattrs;
        try {
            xattrs = fsAsDiana.getXAttrs(FSXAttrBaseTest.path);
            Assert.fail("Diana should not have read access to get xattrs");
        } catch (AccessControlException e) {
            // Ignore
        }
        // Give Diana read permissions to the path
        fsAsBruce.modifyAclEntries(FSXAttrBaseTest.path, Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FSXAttrBaseTest.DIANA.getUserName(), FsAction.READ)));
        xattrs = fsAsDiana.getXAttrs(FSXAttrBaseTest.path);
        Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.name1));
        try {
            fsAsDiana.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
            Assert.fail("Diana should not have write access to remove xattrs");
        } catch (AccessControlException e) {
            // Ignore
        }
        try {
            fsAsDiana.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2);
            Assert.fail("Diana should not have write access to set xattrs");
        } catch (AccessControlException e) {
            // Ignore
        }
        fsAsBruce.modifyAclEntries(FSXAttrBaseTest.path, Lists.newArrayList(AclTestHelpers.aclEntry(AclEntryScope.ACCESS, AclEntryType.USER, FSXAttrBaseTest.DIANA.getUserName(), FsAction.ALL)));
        fsAsDiana.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2, FSXAttrBaseTest.value2);
        Assert.assertArrayEquals(FSXAttrBaseTest.value2, fsAsDiana.getXAttrs(FSXAttrBaseTest.path).get(FSXAttrBaseTest.name2));
        fsAsDiana.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name1);
        fsAsDiana.removeXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.name2);
    }

    @Test(timeout = 120000)
    public void testRawXAttrs() throws Exception {
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        FileSystem.mkdirs(fs, FSXAttrBaseTest.path, FsPermission.createImmutable(((short) (488))));
        fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1, EnumSet.of(CREATE, REPLACE));
        {
            // getXAttr
            final byte[] value = fs.getXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
            Assert.assertArrayEquals(value, FSXAttrBaseTest.value1);
        }
        {
            // getXAttrs
            final Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.rawPath);
            Assert.assertEquals(xattrs.size(), 1);
            Assert.assertArrayEquals(FSXAttrBaseTest.value1, xattrs.get(FSXAttrBaseTest.raw1));
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
        }
        {
            // replace and re-get
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.newValue1, EnumSet.of(CREATE, REPLACE));
            final Map<String, byte[]> xattrs = fs.getXAttrs(FSXAttrBaseTest.rawPath);
            Assert.assertEquals(xattrs.size(), 1);
            Assert.assertArrayEquals(FSXAttrBaseTest.newValue1, xattrs.get(FSXAttrBaseTest.raw1));
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
        }
        {
            // listXAttrs on rawPath ensuring raw.* xattrs are returned
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
            final List<String> xattrNames = fs.listXAttrs(FSXAttrBaseTest.rawPath);
            Assert.assertTrue(xattrNames.contains(FSXAttrBaseTest.raw1));
            Assert.assertTrue(xattrNames.contains(FSXAttrBaseTest.raw2));
            Assert.assertTrue(((xattrNames.size()) == 2));
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw2);
        }
        {
            // listXAttrs on non-rawPath ensuring no raw.* xattrs returned
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1, EnumSet.of(CREATE));
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw2, FSXAttrBaseTest.value2, EnumSet.of(CREATE));
            final List<String> xattrNames = fs.listXAttrs(FSXAttrBaseTest.path);
            Assert.assertTrue(((xattrNames.size()) == 0));
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw2);
        }
        {
            /* Test non-root user operations in the "raw.*" namespace. */
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    // Test that non-root can not set xattrs in the "raw.*" namespace
                    try {
                        // non-raw path
                        userFs.setXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1);
                        Assert.fail("setXAttr should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    try {
                        // raw path
                        userFs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1);
                        Assert.fail("setXAttr should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    // Test that non-root can not do getXAttrs in the "raw.*" namespace
                    try {
                        // non-raw path
                        userFs.getXAttrs(FSXAttrBaseTest.rawPath);
                        Assert.fail("getXAttrs should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    try {
                        // raw path
                        userFs.getXAttrs(FSXAttrBaseTest.path);
                        Assert.fail("getXAttrs should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    // Test that non-root can not do getXAttr in the "raw.*" namespace
                    try {
                        // non-raw path
                        userFs.getXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
                        Assert.fail("getXAttr should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    try {
                        // raw path
                        userFs.getXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.raw1);
                        Assert.fail("getXAttr should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    return null;
                }
            });
        }
        {
            /* Test that user who don'r have read access
             can not do getXAttr in the "raw.*" namespace
             */
            fs.setXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1);
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    try {
                        // raw path
                        userFs.getXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
                        Assert.fail("getXAttr should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    try {
                        // non-raw path
                        userFs.getXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.raw1);
                        Assert.fail("getXAttr should have thrown");
                    } catch (AccessControlException e) {
                        // ignore
                    }
                    /* Test that user who have parent directory execute access
                     can also not see raw.* xattrs returned from listXAttr
                     */
                    try {
                        // non-raw path
                        userFs.listXAttrs(FSXAttrBaseTest.path);
                        Assert.fail("listXAttr should have thrown AccessControlException");
                    } catch (AccessControlException ace) {
                        // expected
                    }
                    try {
                        // raw path
                        userFs.listXAttrs(FSXAttrBaseTest.rawPath);
                        Assert.fail("listXAttr should have thrown AccessControlException");
                    } catch (AccessControlException ace) {
                        // expected
                    }
                    return null;
                }
            });
            /* Test user who have read access can list xattrs in "raw.*" namespace */
            fs.setPermission(FSXAttrBaseTest.path, new FsPermission(((short) (489))));
            final Path childDir = new Path(FSXAttrBaseTest.path, ("child" + (FSXAttrBaseTest.pathCount)));
            FileSystem.mkdirs(fs, childDir, FsPermission.createImmutable(((short) (452))));
            final Path rawChildDir = new Path(("/.reserved/raw" + (childDir.toString())));
            fs.setXAttr(rawChildDir, FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1);
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    // raw path
                    List<String> xattrs = userFs.listXAttrs(rawChildDir);
                    Assert.assertEquals(1, xattrs.size());
                    Assert.assertEquals(FSXAttrBaseTest.raw1, xattrs.get(0));
                    return null;
                }
            });
            fs.removeXAttr(FSXAttrBaseTest.rawPath, FSXAttrBaseTest.raw1);
        }
        {
            /* Tests that user who have read access are able to do getattr. */
            Path parentPath = new Path("/foo");
            fs.mkdirs(parentPath);
            fs.setOwner(parentPath, "user", "mygroup");
            // Set only execute permission for others on parent directory so that
            // any user can traverse down the directory.
            fs.setPermission(parentPath, new FsPermission("701"));
            Path childPath = new Path("/foo/bar");
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final DistributedFileSystem dfs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    DFSTestUtil.createFile(dfs, childPath, 1024, ((short) (1)), 65261);
                    dfs.setPermission(childPath, new FsPermission("740"));
                    return null;
                }
            });
            Path rawChildPath = new Path(("/.reserved/raw" + (childPath.toString())));
            fs.setXAttr(new Path("/.reserved/raw/foo/bar"), FSXAttrBaseTest.raw1, FSXAttrBaseTest.value1);
            user.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final DistributedFileSystem dfs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    // Make sure user have access to raw xattr.
                    byte[] xattr = dfs.getXAttr(rawChildPath, FSXAttrBaseTest.raw1);
                    Assert.assertEquals(Arrays.toString(FSXAttrBaseTest.value1), Arrays.toString(xattr));
                    return null;
                }
            });
            final UserGroupInformation fakeUser = UserGroupInformation.createUserForTesting("fakeUser", new String[]{ "fakeGroup" });
            fakeUser.doAs(new PrivilegedExceptionAction<Object>() {
                @Override
                public Object run() throws Exception {
                    final DistributedFileSystem dfs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                    try {
                        // Make sure user who don't have read access to file can't access
                        // raw xattr.
                        dfs.getXAttr(FSXAttrBaseTest.path, FSXAttrBaseTest.raw1);
                        Assert.fail("should have thrown AccessControlException");
                    } catch (AccessControlException ace) {
                        // expected
                    }
                    return null;
                }
            });
            // fs.removeXAttr(rawPath, raw1);
        }
    }

    /**
     * This tests the "unreadable by superuser" xattr which denies access to a
     * file for the superuser. See HDFS-6705 for details.
     */
    @Test(timeout = 120000)
    public void testUnreadableBySuperuserXAttr() throws Exception {
        // Run tests as superuser...
        doTestUnreadableBySuperuserXAttr(fs, true);
        // ...and again as non-superuser
        final UserGroupInformation user = UserGroupInformation.createUserForTesting("user", new String[]{ "mygroup" });
        user.doAs(new PrivilegedExceptionAction<Object>() {
            @Override
            public Object run() throws Exception {
                final FileSystem userFs = FSXAttrBaseTest.dfsCluster.getFileSystem();
                doTestUnreadableBySuperuserXAttr(userFs, false);
                return null;
            }
        });
    }
}

