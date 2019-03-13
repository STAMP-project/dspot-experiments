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
package org.apache.hadoop.fs;


import CommonConfigurationKeys.FS_PERMISSIONS_UMASK_KEY;
import FileSystem.LOG;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.PlatformAssumptions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;


/**
 * This class tests the local file system via the FileSystem abstraction.
 */
public class TestLocalFileSystemPermission {
    public static final Logger LOGGER = LoggerFactory.getLogger(TestFcLocalFsPermission.class);

    static final String TEST_PATH_PREFIX = GenericTestUtils.getTempPath(TestLocalFileSystemPermission.class.getSimpleName());

    static {
        GenericTestUtils.setLogLevel(LOG, Level.DEBUG);
    }

    @Test
    public void testLocalFSDirsetPermission() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        LocalFileSystem localfs = FileSystem.getLocal(new Configuration());
        Configuration conf = localfs.getConf();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "044");
        Path dir = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "dir"));
        localfs.mkdirs(dir);
        Path dir1 = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "dir1"));
        Path dir2 = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "dir2"));
        try {
            FsPermission initialPermission = getPermission(localfs, dir);
            Assert.assertEquals(FsPermission.getDirDefault().applyUMask(FsPermission.getUMask(conf)), initialPermission);
            FsPermission perm = new FsPermission(((short) (493)));
            localfs.mkdirs(dir1, perm);
            initialPermission = getPermission(localfs, dir1);
            Assert.assertEquals(perm.applyUMask(FsPermission.getUMask(conf)), initialPermission);
            localfs.mkdirs(dir2);
            initialPermission = getPermission(localfs, dir2);
            Path copyPath = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "dir_copy"));
            localfs.rename(dir2, copyPath);
            FsPermission copyPermission = getPermission(localfs, copyPath);
            Assert.assertEquals(initialPermission, copyPermission);
            dir2 = copyPath;
        } finally {
            cleanup(localfs, dir);
            cleanup(localfs, dir1);
            if (localfs.exists(dir2)) {
                localfs.delete(dir2, true);
            }
        }
    }

    /**
     * Test LocalFileSystem.setPermission.
     */
    @Test
    public void testLocalFSsetPermission() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        Configuration conf = new Configuration();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "044");
        LocalFileSystem localfs = FileSystem.getLocal(conf);
        Path f = null;
        Path f1 = null;
        Path f2 = null;
        String filename = "foo";
        String filename1 = "foo1";
        String filename2 = "foo2";
        FsPermission perm = new FsPermission(((short) (493)));
        try {
            f = writeFile(localfs, filename);
            f1 = writeFile(localfs, filename1, perm);
            f2 = writeFile(localfs, filename2);
            FsPermission initialPermission = getPermission(localfs, f);
            Assert.assertEquals(FsPermission.getFileDefault().applyUMask(FsPermission.getUMask(conf)), initialPermission);
            initialPermission = getPermission(localfs, f1);
            Assert.assertEquals(perm.applyUMask(FsPermission.getUMask(conf)), initialPermission);
            initialPermission = getPermission(localfs, f2);
            Path copyPath = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "/foo_copy"));
            localfs.rename(f2, copyPath);
            FsPermission copyPermission = getPermission(localfs, copyPath);
            Assert.assertEquals(copyPermission, initialPermission);
            f2 = copyPath;
            // create files and manipulate them.
            FsPermission all = new FsPermission(((short) (511)));
            FsPermission none = new FsPermission(((short) (0)));
            localfs.setPermission(f, none);
            Assert.assertEquals(none, getPermission(localfs, f));
            localfs.setPermission(f, all);
            Assert.assertEquals(all, getPermission(localfs, f));
        } finally {
            cleanup(localfs, f);
            cleanup(localfs, f1);
            if (localfs.exists(f2)) {
                localfs.delete(f2, true);
            }
        }
    }

    /**
     * Test LocalFileSystem.setOwner.
     */
    @Test
    public void testLocalFSsetOwner() throws IOException {
        PlatformAssumptions.assumeNotWindows();
        Configuration conf = new Configuration();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "044");
        LocalFileSystem localfs = FileSystem.getLocal(conf);
        String filename = "bar";
        Path f = writeFile(localfs, filename);
        List<String> groups;
        try {
            groups = TestLocalFileSystemPermission.getGroups();
            TestLocalFileSystemPermission.LOGGER.info("{}: {}", filename, getPermission(localfs, f));
            // create files and manipulate them.
            String g0 = groups.get(0);
            localfs.setOwner(f, null, g0);
            Assert.assertEquals(g0, getGroup(localfs, f));
            if ((groups.size()) > 1) {
                String g1 = groups.get(1);
                localfs.setOwner(f, null, g1);
                Assert.assertEquals(g1, getGroup(localfs, f));
            } else {
                TestLocalFileSystemPermission.LOGGER.info(("Not testing changing the group since user " + "belongs to only one group."));
            }
        } finally {
            cleanup(localfs, f);
        }
    }

    /**
     * Steps:
     * 1. Create a directory with default permissions: 777 with umask 022
     * 2. Check the directory has good permissions: 755
     * 3. Set the umask to 062.
     * 4. Create a new directory with default permissions.
     * 5. For this directory we expect 715 as permission not 755
     *
     * @throws Exception
     * 		we can throw away all the exception.
     */
    @Test
    public void testSetUmaskInRealTime() throws Exception {
        PlatformAssumptions.assumeNotWindows();
        LocalFileSystem localfs = FileSystem.getLocal(new Configuration());
        Configuration conf = localfs.getConf();
        conf.set(FS_PERMISSIONS_UMASK_KEY, "022");
        TestLocalFileSystemPermission.LOGGER.info("Current umask is {}", conf.get(FS_PERMISSIONS_UMASK_KEY));
        Path dir = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "dir"));
        Path dir2 = new Path(((TestLocalFileSystemPermission.TEST_PATH_PREFIX) + "dir2"));
        try {
            Assert.assertTrue(localfs.mkdirs(dir));
            FsPermission initialPermission = getPermission(localfs, dir);
            Assert.assertEquals(("With umask 022 permission should be 755 since the default " + "permission is 777"), new FsPermission("755"), initialPermission);
            // Modify umask and create a new directory
            // and check if new umask is applied
            conf.set(FS_PERMISSIONS_UMASK_KEY, "062");
            Assert.assertTrue(localfs.mkdirs(dir2));
            FsPermission finalPermission = localfs.getFileStatus(dir2).getPermission();
            Assert.assertThat(("With umask 062 permission should not be 755 since the " + "default permission is 777"), new FsPermission("755"), CoreMatchers.is(CoreMatchers.not(finalPermission)));
            Assert.assertEquals("With umask 062 we expect 715 since the default permission is 777", new FsPermission("715"), finalPermission);
        } finally {
            conf.set(FS_PERMISSIONS_UMASK_KEY, "022");
            cleanup(localfs, dir);
            cleanup(localfs, dir2);
        }
    }
}

