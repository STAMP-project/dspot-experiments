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
package org.apache.hadoop.security;


import CommonConfigurationKeys.IO_FILE_BUFFER_SIZE_KEY;
import DFSConfigKeys.DFS_PERMISSIONS_ENABLED_KEY;
import FsPermission.UMASK_LABEL;
import java.io.FileNotFoundException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.util.StringUtils;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Unit tests for permission
 */
public class TestPermission {
    public static final Logger LOG = LoggerFactory.getLogger(TestPermission.class);

    private static final Path ROOT_PATH = new Path("/data");

    private static final Path CHILD_DIR1 = new Path(TestPermission.ROOT_PATH, "child1");

    private static final Path CHILD_DIR2 = new Path(TestPermission.ROOT_PATH, "child2");

    private static final Path CHILD_DIR3 = new Path(TestPermission.ROOT_PATH, "child3");

    private static final Path CHILD_FILE1 = new Path(TestPermission.ROOT_PATH, "file1");

    private static final Path CHILD_FILE2 = new Path(TestPermission.ROOT_PATH, "file2");

    private static final Path CHILD_FILE3 = new Path(TestPermission.ROOT_PATH, "file3");

    private static final int FILE_LEN = 100;

    private static final Random RAN = new Random();

    private static final String USER_NAME = "user" + (TestPermission.RAN.nextInt());

    private static final String[] GROUP_NAMES = new String[]{ "group1", "group2" };

    private static final String NOUSER = "nouser";

    private static final String NOGROUP = "nogroup";

    private FileSystem nnfs;

    private FileSystem userfs;

    /**
     * Tests backward compatibility. Configuration can be
     * either set with old param dfs.umask that takes decimal umasks
     * or dfs.umaskmode that takes symbolic or octal umask.
     */
    @Test
    public void testBackwardCompatibility() {
        // Test 1 - old configuration key with decimal
        // umask value should be handled when set using
        // FSPermission.setUMask() API
        FsPermission perm = new FsPermission(((short) (18)));
        Configuration conf = new Configuration();
        FsPermission.setUMask(conf, perm);
        Assert.assertEquals(18, FsPermission.getUMask(conf).toShort());
        // Test 2 - new configuration key is handled
        conf = new Configuration();
        conf.set(UMASK_LABEL, "022");
        Assert.assertEquals(18, FsPermission.getUMask(conf).toShort());
        // Test 3 - equivalent valid umask
        conf = new Configuration();
        conf.set(UMASK_LABEL, "0022");
        Assert.assertEquals(18, FsPermission.getUMask(conf).toShort());
        // Test 4 - invalid umask
        conf = new Configuration();
        conf.set(UMASK_LABEL, "1222");
        try {
            FsPermission.getUMask(conf);
            Assert.fail("expect IllegalArgumentException happen");
        } catch (IllegalArgumentException e) {
            // pass, exception successfully trigger
        }
        // Test 5 - invalid umask
        conf = new Configuration();
        conf.set(UMASK_LABEL, "01222");
        try {
            FsPermission.getUMask(conf);
            Assert.fail("expect IllegalArgumentException happen");
        } catch (IllegalArgumentException e) {
            // pass, exception successfully trigger
        }
    }

    @Test
    public void testCreate() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, true);
        conf.set(UMASK_LABEL, "000");
        MiniDFSCluster cluster = null;
        FileSystem fs = null;
        try {
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
            cluster.waitActive();
            fs = FileSystem.get(conf);
            FsPermission rootPerm = TestPermission.checkPermission(fs, "/", null);
            FsPermission inheritPerm = FsPermission.createImmutable(((short) ((rootPerm.toShort()) | 192)));
            FsPermission dirPerm = new FsPermission(((short) (511)));
            fs.mkdirs(new Path("/a1/a2/a3"), dirPerm);
            TestPermission.checkPermission(fs, "/a1", dirPerm);
            TestPermission.checkPermission(fs, "/a1/a2", dirPerm);
            TestPermission.checkPermission(fs, "/a1/a2/a3", dirPerm);
            dirPerm = new FsPermission(((short) (83)));
            FsPermission permission = FsPermission.createImmutable(((short) ((dirPerm.toShort()) | 192)));
            fs.mkdirs(new Path("/aa/1/aa/2/aa/3"), dirPerm);
            TestPermission.checkPermission(fs, "/aa/1", permission);
            TestPermission.checkPermission(fs, "/aa/1/aa/2", permission);
            TestPermission.checkPermission(fs, "/aa/1/aa/2/aa/3", dirPerm);
            FsPermission filePerm = new FsPermission(((short) (292)));
            Path p = new Path("/b1/b2/b3.txt");
            FSDataOutputStream out = fs.create(p, filePerm, true, conf.getInt(IO_FILE_BUFFER_SIZE_KEY, 4096), fs.getDefaultReplication(p), fs.getDefaultBlockSize(p), null);
            out.write(123);
            out.close();
            TestPermission.checkPermission(fs, "/b1", inheritPerm);
            TestPermission.checkPermission(fs, "/b1/b2", inheritPerm);
            TestPermission.checkPermission(fs, "/b1/b2/b3.txt", filePerm);
            conf.set(UMASK_LABEL, "022");
            permission = FsPermission.createImmutable(((short) (438)));
            FileSystem.mkdirs(fs, new Path("/c1"), new FsPermission(permission));
            FileSystem.create(fs, new Path("/c1/c2.txt"), new FsPermission(permission));
            TestPermission.checkPermission(fs, "/c1", permission);
            TestPermission.checkPermission(fs, "/c1/c2.txt", permission);
        } finally {
            try {
                if (fs != null)
                    fs.close();

            } catch (Exception e) {
                TestPermission.LOG.error(StringUtils.stringifyException(e));
            }
            try {
                if (cluster != null)
                    cluster.shutdown();

            } catch (Exception e) {
                TestPermission.LOG.error(StringUtils.stringifyException(e));
            }
        }
    }

    @Test
    public void testFilePermission() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_PERMISSIONS_ENABLED_KEY, true);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        cluster.waitActive();
        try {
            nnfs = FileSystem.get(conf);
            // test permissions on files that do not exist
            Assert.assertFalse(nnfs.exists(TestPermission.CHILD_FILE1));
            try {
                nnfs.setPermission(TestPermission.CHILD_FILE1, new FsPermission(((short) (511))));
                Assert.assertTrue(false);
            } catch (FileNotFoundException e) {
                TestPermission.LOG.info(("GOOD: got " + e));
            }
            // make sure nn can take user specified permission (with default fs
            // permission umask applied)
            FSDataOutputStream out = nnfs.create(TestPermission.CHILD_FILE1, new FsPermission(((short) (511))), true, 1024, ((short) (1)), 1024, null);
            FileStatus status = nnfs.getFileStatus(TestPermission.CHILD_FILE1);
            // FS_PERMISSIONS_UMASK_DEFAULT is 0022
            Assert.assertTrue(status.getPermission().toString().equals("rwxr-xr-x"));
            nnfs.delete(TestPermission.CHILD_FILE1, false);
            // following dir/file creations are legal
            nnfs.mkdirs(TestPermission.CHILD_DIR1);
            status = nnfs.getFileStatus(TestPermission.CHILD_DIR1);
            Assert.assertThat("Expect 755 = 777 (default dir) - 022 (default umask)", status.getPermission().toString(), Is.is("rwxr-xr-x"));
            out = nnfs.create(TestPermission.CHILD_FILE1);
            status = nnfs.getFileStatus(TestPermission.CHILD_FILE1);
            Assert.assertTrue(status.getPermission().toString().equals("rw-r--r--"));
            byte[] data = new byte[TestPermission.FILE_LEN];
            TestPermission.RAN.nextBytes(data);
            out.write(data);
            out.close();
            nnfs.setPermission(TestPermission.CHILD_FILE1, new FsPermission("700"));
            status = nnfs.getFileStatus(TestPermission.CHILD_FILE1);
            Assert.assertTrue(status.getPermission().toString().equals("rwx------"));
            // mkdirs with null permission
            nnfs.mkdirs(TestPermission.CHILD_DIR3, null);
            status = nnfs.getFileStatus(TestPermission.CHILD_DIR3);
            Assert.assertThat("Expect 755 = 777 (default dir) - 022 (default umask)", status.getPermission().toString(), Is.is("rwxr-xr-x"));
            // following read is legal
            byte[] dataIn = new byte[TestPermission.FILE_LEN];
            FSDataInputStream fin = nnfs.open(TestPermission.CHILD_FILE1);
            int bytesRead = fin.read(dataIn);
            Assert.assertTrue((bytesRead == (TestPermission.FILE_LEN)));
            for (int i = 0; i < (TestPermission.FILE_LEN); i++) {
                Assert.assertEquals(data[i], dataIn[i]);
            }
            // test execution bit support for files
            nnfs.setPermission(TestPermission.CHILD_FILE1, new FsPermission("755"));
            status = nnfs.getFileStatus(TestPermission.CHILD_FILE1);
            Assert.assertTrue(status.getPermission().toString().equals("rwxr-xr-x"));
            nnfs.setPermission(TestPermission.CHILD_FILE1, new FsPermission("744"));
            status = nnfs.getFileStatus(TestPermission.CHILD_FILE1);
            Assert.assertTrue(status.getPermission().toString().equals("rwxr--r--"));
            nnfs.setPermission(TestPermission.CHILD_FILE1, new FsPermission("700"));
            // //////////////////////////////////////////////////////////////
            // test illegal file/dir creation
            UserGroupInformation userGroupInfo = UserGroupInformation.createUserForTesting(TestPermission.USER_NAME, TestPermission.GROUP_NAMES);
            userfs = DFSTestUtil.getFileSystemAs(userGroupInfo, conf);
            // make sure mkdir of a existing directory that is not owned by
            // this user does not throw an exception.
            userfs.mkdirs(TestPermission.CHILD_DIR1);
            // illegal mkdir
            Assert.assertTrue((!(TestPermission.canMkdirs(userfs, TestPermission.CHILD_DIR2))));
            // illegal file creation
            Assert.assertTrue((!(TestPermission.canCreate(userfs, TestPermission.CHILD_FILE2))));
            // illegal file open
            Assert.assertTrue((!(TestPermission.canOpen(userfs, TestPermission.CHILD_FILE1))));
            nnfs.setPermission(TestPermission.ROOT_PATH, new FsPermission(((short) (493))));
            nnfs.setPermission(TestPermission.CHILD_DIR1, new FsPermission("777"));
            nnfs.setPermission(new Path("/"), new FsPermission(((short) (511))));
            final Path RENAME_PATH = new Path("/foo/bar");
            userfs.mkdirs(RENAME_PATH);
            Assert.assertTrue(TestPermission.canRename(userfs, RENAME_PATH, TestPermission.CHILD_DIR1));
            // test permissions on files that do not exist
            Assert.assertFalse(userfs.exists(TestPermission.CHILD_FILE3));
            try {
                userfs.setPermission(TestPermission.CHILD_FILE3, new FsPermission(((short) (511))));
                Assert.fail("setPermission should fail for non-exist file");
            } catch (FileNotFoundException ignored) {
            }
            // Make sure any user can create file in root.
            nnfs.setPermission(TestPermission.ROOT_PATH, new FsPermission("777"));
            testSuperCanChangeOwnerGroup();
            testNonSuperCanChangeToOwnGroup();
            testNonSuperCannotChangeToOtherGroup();
            testNonSuperCannotChangeGroupForOtherFile();
            testNonSuperCannotChangeGroupForNonExistentFile();
            testNonSuperCannotChangeOwner();
            testNonSuperCannotChangeOwnerForOtherFile();
            testNonSuperCannotChangeOwnerForNonExistentFile();
        } finally {
            cluster.shutdown();
        }
    }
}

