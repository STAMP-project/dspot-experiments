/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.fs.ozone;


import OzoneConsts.OZONE_URI_SCHEME;
import java.io.IOException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.web.handlers.UserArgs;
import org.apache.hadoop.ozone.web.interfaces.StorageHandler;
import org.apache.hadoop.ozone.web.response.KeyInfo;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


/**
 * Ozone file system tests that are not covered by contract tests.
 */
public class TestOzoneFileSystem {
    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    private static MiniOzoneCluster cluster = null;

    private static FileSystem fs;

    private static OzoneFileSystem o3fs;

    private static StorageHandler storageHandler;

    private static UserArgs userArgs;

    private String volumeName;

    private String bucketName;

    private String userName;

    private String rootPath;

    @Test
    public void testOzoneFsServiceLoader() throws IOException {
        Assert.assertEquals(FileSystem.getFileSystemClass(OZONE_URI_SCHEME, null), OzoneFileSystem.class);
    }

    @Test
    public void testCreateDoesNotAddParentDirKeys() throws Exception {
        Path grandparent = new Path("/testCreateDoesNotAddParentDirKeys");
        Path parent = new Path(grandparent, "parent");
        Path child = new Path(parent, "child");
        ContractTestUtils.touch(TestOzoneFileSystem.fs, child);
        KeyInfo key = getKey(child, false);
        Assert.assertEquals(key.getKeyName(), TestOzoneFileSystem.o3fs.pathToKey(child));
        // Creating a child should not add parent keys to the bucket
        try {
            getKey(parent, true);
        } catch (IOException ex) {
            assertKeyNotFoundException(ex);
        }
        // List status on the parent should show the child file
        Assert.assertEquals("List status of parent should include the 1 child file", 1L, ((long) (TestOzoneFileSystem.fs.listStatus(parent).length)));
        Assert.assertTrue("Parent directory does not appear to be a directory", TestOzoneFileSystem.fs.getFileStatus(parent).isDirectory());
    }

    @Test
    public void testDeleteCreatesFakeParentDir() throws Exception {
        Path grandparent = new Path("/testDeleteCreatesFakeParentDir");
        Path parent = new Path(grandparent, "parent");
        Path child = new Path(parent, "child");
        ContractTestUtils.touch(TestOzoneFileSystem.fs, child);
        // Verify that parent dir key does not exist
        // Creating a child should not add parent keys to the bucket
        try {
            getKey(parent, true);
        } catch (IOException ex) {
            assertKeyNotFoundException(ex);
        }
        // Delete the child key
        TestOzoneFileSystem.fs.delete(child, false);
        // Deleting the only child should create the parent dir key if it does
        // not exist
        String parentKey = (TestOzoneFileSystem.o3fs.pathToKey(parent)) + "/";
        KeyInfo parentKeyInfo = getKey(parent, true);
        Assert.assertEquals(parentKey, parentKeyInfo.getKeyName());
    }

    @Test
    public void testListStatus() throws Exception {
        Path parent = new Path("/testListStatus");
        Path file1 = new Path(parent, "key1");
        Path file2 = new Path(parent, "key1/key2");
        ContractTestUtils.touch(TestOzoneFileSystem.fs, file1);
        ContractTestUtils.touch(TestOzoneFileSystem.fs, file2);
        // ListStatus on a directory should return all subdirs along with
        // files, even if there exists a file and sub-dir with the same name.
        FileStatus[] fileStatuses = TestOzoneFileSystem.o3fs.listStatus(parent);
        Assert.assertEquals("FileStatus did not return all children of the directory", 2, fileStatuses.length);
        // ListStatus should return only the immediate children of a directory.
        Path file3 = new Path(parent, "dir1/key3");
        Path file4 = new Path(parent, "dir1/key4");
        ContractTestUtils.touch(TestOzoneFileSystem.fs, file3);
        ContractTestUtils.touch(TestOzoneFileSystem.fs, file4);
        fileStatuses = TestOzoneFileSystem.o3fs.listStatus(parent);
        Assert.assertEquals("FileStatus did not return all children of the directory", 3, fileStatuses.length);
    }

    /**
     * Tests listStatus operation on root directory.
     */
    @Test
    public void testListStatusOnRoot() throws Exception {
        Path root = new Path("/");
        Path dir1 = new Path(root, "dir1");
        Path dir12 = new Path(dir1, "dir12");
        Path dir2 = new Path(root, "dir2");
        TestOzoneFileSystem.fs.mkdirs(dir12);
        TestOzoneFileSystem.fs.mkdirs(dir2);
        // ListStatus on root should return dir1 (even though /dir1 key does not
        // exist) and dir2 only. dir12 is not an immediate child of root and
        // hence should not be listed.
        FileStatus[] fileStatuses = TestOzoneFileSystem.o3fs.listStatus(root);
        Assert.assertEquals("FileStatus should return only the immediate children", 2, fileStatuses.length);
        // Verify that dir12 is not included in the result of the listStatus on root
        String fileStatus1 = fileStatuses[0].getPath().toUri().getPath();
        String fileStatus2 = fileStatuses[1].getPath().toUri().getPath();
        Assert.assertFalse(fileStatus1.equals(dir12.toString()));
        Assert.assertFalse(fileStatus2.equals(dir12.toString()));
    }

    /**
     * Tests listStatus on a path with subdirs.
     */
    @Test
    public void testListStatusOnSubDirs() throws Exception {
        // Create the following key structure
        // /dir1/dir11/dir111
        // /dir1/dir12
        // /dir1/dir12/file121
        // /dir2
        // ListStatus on /dir1 should return all its immediated subdirs only
        // which are /dir1/dir11 and /dir1/dir12. Super child files/dirs
        // (/dir1/dir12/file121 and /dir1/dir11/dir111) should not be returned by
        // listStatus.
        Path dir1 = new Path("/dir1");
        Path dir11 = new Path(dir1, "dir11");
        Path dir111 = new Path(dir11, "dir111");
        Path dir12 = new Path(dir1, "dir12");
        Path file121 = new Path(dir12, "file121");
        Path dir2 = new Path("/dir2");
        TestOzoneFileSystem.fs.mkdirs(dir111);
        TestOzoneFileSystem.fs.mkdirs(dir12);
        ContractTestUtils.touch(TestOzoneFileSystem.fs, file121);
        TestOzoneFileSystem.fs.mkdirs(dir2);
        FileStatus[] fileStatuses = TestOzoneFileSystem.o3fs.listStatus(dir1);
        Assert.assertEquals("FileStatus should return only the immediate children", 2, fileStatuses.length);
        // Verify that the two children of /dir1 returned by listStatus operation
        // are /dir1/dir11 and /dir1/dir12.
        String fileStatus1 = fileStatuses[0].getPath().toUri().getPath();
        String fileStatus2 = fileStatuses[1].getPath().toUri().getPath();
        Assert.assertTrue(((fileStatus1.equals(dir11.toString())) || (fileStatus1.equals(dir12.toString()))));
        Assert.assertTrue(((fileStatus2.equals(dir11.toString())) || (fileStatus2.equals(dir12.toString()))));
    }
}

