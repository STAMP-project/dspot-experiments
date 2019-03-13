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
package org.apache.hadoop.fs.permission;


import FSExceptionMessages.PERMISSION_DENIED_BY_STICKY_BIT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestStickyBit {
    static final UserGroupInformation user1 = UserGroupInformation.createUserForTesting("theDoctor", new String[]{ "tardis" });

    static final UserGroupInformation user2 = UserGroupInformation.createUserForTesting("rose", new String[]{ "powellestates" });

    static final Logger LOG = LoggerFactory.getLogger(TestStickyBit.class);

    private static MiniDFSCluster cluster;

    private static Configuration conf;

    private static FileSystem hdfs;

    private static FileSystem hdfsAsUser1;

    private static FileSystem hdfsAsUser2;

    @Test
    public void testGeneralSBBehavior() throws Exception {
        Path baseDir = new Path("/mcgann");
        TestStickyBit.hdfs.mkdirs(baseDir);
        // Create a tmp directory with wide-open permissions and sticky bit
        Path p = new Path(baseDir, "tmp");
        TestStickyBit.hdfs.mkdirs(p);
        TestStickyBit.hdfs.setPermission(p, new FsPermission(((short) (1023))));
        confirmCanAppend(TestStickyBit.conf, p);
        baseDir = new Path("/eccleston");
        TestStickyBit.hdfs.mkdirs(baseDir);
        p = new Path(baseDir, "roguetraders");
        TestStickyBit.hdfs.mkdirs(p);
        confirmSettingAndGetting(TestStickyBit.hdfs, p, baseDir);
        baseDir = new Path("/tennant");
        TestStickyBit.hdfs.mkdirs(baseDir);
        p = new Path(baseDir, "contemporary");
        TestStickyBit.hdfs.mkdirs(p);
        TestStickyBit.hdfs.setPermission(p, new FsPermission(((short) (1023))));
        confirmDeletingFiles(TestStickyBit.conf, p);
        baseDir = new Path("/smith");
        TestStickyBit.hdfs.mkdirs(baseDir);
        p = new Path(baseDir, "scissorsisters");
        // Turn on its sticky bit
        TestStickyBit.hdfs.mkdirs(p, new FsPermission(((short) (950))));
        confirmStickyBitDoesntPropagate(TestStickyBit.hdfs, baseDir);
    }

    @Test
    public void testAclGeneralSBBehavior() throws Exception {
        Path baseDir = new Path("/mcgann");
        TestStickyBit.hdfs.mkdirs(baseDir);
        // Create a tmp directory with wide-open permissions and sticky bit
        Path p = new Path(baseDir, "tmp");
        TestStickyBit.hdfs.mkdirs(p);
        TestStickyBit.hdfs.setPermission(p, new FsPermission(((short) (1023))));
        TestStickyBit.applyAcl(p);
        confirmCanAppend(TestStickyBit.conf, p);
        baseDir = new Path("/eccleston");
        TestStickyBit.hdfs.mkdirs(baseDir);
        p = new Path(baseDir, "roguetraders");
        TestStickyBit.hdfs.mkdirs(p);
        TestStickyBit.applyAcl(p);
        confirmSettingAndGetting(TestStickyBit.hdfs, p, baseDir);
        baseDir = new Path("/tennant");
        TestStickyBit.hdfs.mkdirs(baseDir);
        p = new Path(baseDir, "contemporary");
        TestStickyBit.hdfs.mkdirs(p);
        TestStickyBit.hdfs.setPermission(p, new FsPermission(((short) (1023))));
        TestStickyBit.applyAcl(p);
        confirmDeletingFiles(TestStickyBit.conf, p);
        baseDir = new Path("/smith");
        TestStickyBit.hdfs.mkdirs(baseDir);
        p = new Path(baseDir, "scissorsisters");
        // Turn on its sticky bit
        TestStickyBit.hdfs.mkdirs(p, new FsPermission(((short) (950))));
        TestStickyBit.applyAcl(p);
        confirmStickyBitDoesntPropagate(TestStickyBit.hdfs, p);
    }

    /**
     * Test that one user can't rename/move another user's file when the sticky
     * bit is set.
     */
    @Test
    public void testMovingFiles() throws Exception {
        testMovingFiles(false);
    }

    @Test
    public void testAclMovingFiles() throws Exception {
        testMovingFiles(true);
    }

    /**
     * Ensure that when we set a sticky bit and shut down the file system, we get
     * the sticky bit back on re-start, and that no extra sticky bits appear after
     * re-start.
     */
    @Test
    public void testStickyBitPersistence() throws Exception {
        // A tale of three directories...
        Path sbSet = new Path("/Housemartins");
        Path sbNotSpecified = new Path("/INXS");
        Path sbSetOff = new Path("/Easyworld");
        for (Path p : new Path[]{ sbSet, sbNotSpecified, sbSetOff })
            TestStickyBit.hdfs.mkdirs(p);

        // Two directories had there sticky bits set explicitly...
        TestStickyBit.hdfs.setPermission(sbSet, new FsPermission(((short) (1023))));
        TestStickyBit.hdfs.setPermission(sbSetOff, new FsPermission(((short) (511))));
        TestStickyBit.shutdown();
        // Start file system up again
        TestStickyBit.initCluster(false);
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbSet));
        Assert.assertTrue(TestStickyBit.hdfs.getFileStatus(sbSet).getPermission().getStickyBit());
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbNotSpecified));
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbNotSpecified).getPermission().getStickyBit());
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbSetOff));
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbSetOff).getPermission().getStickyBit());
    }

    /**
     * Sticky bit set on a directory can be reset either explicitly (like 0777)
     * or by omitting the bit (like 777) in the permission. Ensure that the
     * directory gets its sticky bit reset whenever it is omitted in permission.
     */
    @Test
    public void testStickyBitReset() throws Exception {
        Path sbExplicitTestDir = new Path("/DirToTestExplicitStickyBit");
        Path sbOmittedTestDir = new Path("/DirToTestOmittedStickyBit");
        // Creation of directories and verification of their existence
        TestStickyBit.hdfs.mkdirs(sbExplicitTestDir);
        TestStickyBit.hdfs.mkdirs(sbOmittedTestDir);
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbExplicitTestDir));
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbOmittedTestDir));
        // Setting sticky bit explicitly on sbExplicitTestDir and verification
        TestStickyBit.hdfs.setPermission(sbExplicitTestDir, new FsPermission(((short) (1023))));
        TestStickyBit.LOG.info("Dir: {}, permission: {}", sbExplicitTestDir.getName(), TestStickyBit.hdfs.getFileStatus(sbExplicitTestDir).getPermission());
        Assert.assertTrue(TestStickyBit.hdfs.getFileStatus(sbExplicitTestDir).getPermission().getStickyBit());
        // Sticky bit omitted on sbOmittedTestDir should behave like reset
        TestStickyBit.hdfs.setPermission(sbOmittedTestDir, new FsPermission(((short) (511))));
        TestStickyBit.LOG.info("Dir: {}, permission: {}", sbOmittedTestDir.getName(), TestStickyBit.hdfs.getFileStatus(sbOmittedTestDir).getPermission());
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbOmittedTestDir).getPermission().getStickyBit());
        // Resetting sticky bit explicitly on sbExplicitTestDir and verification
        TestStickyBit.hdfs.setPermission(sbExplicitTestDir, new FsPermission(((short) (511))));
        TestStickyBit.LOG.info("Dir: {}, permission: {}", sbExplicitTestDir.getName(), TestStickyBit.hdfs.getFileStatus(sbExplicitTestDir).getPermission());
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbExplicitTestDir).getPermission().getStickyBit());
        // Set the sticky bit and reset again by omitting in the permission
        TestStickyBit.hdfs.setPermission(sbOmittedTestDir, new FsPermission(((short) (1023))));
        TestStickyBit.hdfs.setPermission(sbOmittedTestDir, new FsPermission(((short) (511))));
        TestStickyBit.LOG.info("Dir: {}, permission: {}", sbOmittedTestDir.getName(), TestStickyBit.hdfs.getFileStatus(sbOmittedTestDir).getPermission());
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbOmittedTestDir).getPermission().getStickyBit());
    }

    @Test
    public void testAclStickyBitPersistence() throws Exception {
        // A tale of three directories...
        Path sbSet = new Path("/Housemartins");
        Path sbNotSpecified = new Path("/INXS");
        Path sbSetOff = new Path("/Easyworld");
        for (Path p : new Path[]{ sbSet, sbNotSpecified, sbSetOff })
            TestStickyBit.hdfs.mkdirs(p);

        // Two directories had there sticky bits set explicitly...
        TestStickyBit.hdfs.setPermission(sbSet, new FsPermission(((short) (1023))));
        TestStickyBit.applyAcl(sbSet);
        TestStickyBit.hdfs.setPermission(sbSetOff, new FsPermission(((short) (511))));
        TestStickyBit.applyAcl(sbSetOff);
        TestStickyBit.shutdown();
        // Start file system up again
        TestStickyBit.initCluster(false);
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbSet));
        Assert.assertTrue(TestStickyBit.hdfs.getFileStatus(sbSet).getPermission().getStickyBit());
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbNotSpecified));
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbNotSpecified).getPermission().getStickyBit());
        Assert.assertTrue(TestStickyBit.hdfs.exists(sbSetOff));
        Assert.assertFalse(TestStickyBit.hdfs.getFileStatus(sbSetOff).getPermission().getStickyBit());
    }

    @Test
    public void testStickyBitRecursiveDeleteFile() throws Exception {
        Path root = new Path(("/" + (GenericTestUtils.getMethodName())));
        Path tmp = new Path(root, "tmp");
        Path file = new Path(tmp, "file");
        // Create a tmp directory with wide-open permissions and sticky bit
        TestStickyBit.hdfs.mkdirs(tmp);
        TestStickyBit.hdfs.setPermission(root, new FsPermission(((short) (511))));
        TestStickyBit.hdfs.setPermission(tmp, new FsPermission(((short) (1023))));
        // Create a file protected by sticky bit
        TestStickyBit.writeFile(TestStickyBit.hdfsAsUser1, file);
        TestStickyBit.hdfs.setPermission(file, new FsPermission(((short) (438))));
        try {
            TestStickyBit.hdfsAsUser2.delete(tmp, true);
            Assert.fail(("Non-owner can not delete a file protected by sticky bit" + " recursively"));
        } catch (AccessControlException e) {
            GenericTestUtils.assertExceptionContains(PERMISSION_DENIED_BY_STICKY_BIT, e);
        }
        // Owner can delete a file protected by sticky bit recursively
        TestStickyBit.hdfsAsUser1.delete(tmp, true);
    }

    @Test
    public void testStickyBitRecursiveDeleteDir() throws Exception {
        Path root = new Path(("/" + (GenericTestUtils.getMethodName())));
        Path tmp = new Path(root, "tmp");
        Path dir = new Path(tmp, "dir");
        Path file = new Path(dir, "file");
        // Create a tmp directory with wide-open permissions and sticky bit
        TestStickyBit.hdfs.mkdirs(tmp);
        TestStickyBit.hdfs.setPermission(root, new FsPermission(((short) (511))));
        TestStickyBit.hdfs.setPermission(tmp, new FsPermission(((short) (1023))));
        // Create a dir protected by sticky bit
        TestStickyBit.hdfsAsUser1.mkdirs(dir);
        TestStickyBit.hdfsAsUser1.setPermission(dir, new FsPermission(((short) (511))));
        // Create a file in dir
        TestStickyBit.writeFile(TestStickyBit.hdfsAsUser1, file);
        TestStickyBit.hdfs.setPermission(file, new FsPermission(((short) (438))));
        try {
            TestStickyBit.hdfsAsUser2.delete(tmp, true);
            Assert.fail(("Non-owner can not delete a directory protected by sticky bit" + " recursively"));
        } catch (AccessControlException e) {
            GenericTestUtils.assertExceptionContains(PERMISSION_DENIED_BY_STICKY_BIT, e);
        }
        // Owner can delete a directory protected by sticky bit recursively
        TestStickyBit.hdfsAsUser1.delete(tmp, true);
    }
}

