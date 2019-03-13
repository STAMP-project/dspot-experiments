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
package org.apache.hadoop.fs.aliyun.oss;


import java.io.FileNotFoundException;
import java.io.IOException;
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystemContractBaseTest;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static data.length;


/**
 * Tests a live Aliyun OSS system.
 */
public class TestAliyunOSSFileSystemContract extends FileSystemContractBaseTest {
    public static final String TEST_FS_OSS_NAME = "test.fs.oss.name";

    private static Path testRootPath = new Path(AliyunOSSTestUtils.generateUniqueTestPath());

    @Test
    public void testMkdirsWithUmask() throws Exception {
        // not supported
    }

    @Test
    public void testRootDirAlwaysExists() throws Exception {
        // this will throw an exception if the path is not found
        fs.getFileStatus(path("/"));
        // this catches overrides of the base exists() method that don't
        // use getFileStatus() as an existence probe
        Assert.assertTrue("FileSystem.exists() fails for root", fs.exists(path("/")));
    }

    @Test
    public void testRenameRootDirForbidden() throws Exception {
        Assume.assumeTrue(renameSupported());
        rename(path("/"), path("/test/newRootDir"), false, true, false);
    }

    @Test
    public void testListStatus() throws IOException {
        Path file = this.path("/test/hadoop/file");
        this.createFile(file);
        Assert.assertTrue("File exists", this.fs.exists(file));
        FileStatus fs = this.fs.getFileStatus(file);
        Assert.assertEquals(fs.getOwner(), UserGroupInformation.getCurrentUser().getShortUserName());
        Assert.assertEquals(fs.getGroup(), UserGroupInformation.getCurrentUser().getShortUserName());
    }

    @Test
    public void testDeleteSubdir() throws IOException {
        Path parentDir = this.path("/test/hadoop");
        Path file = this.path("/test/hadoop/file");
        Path subdir = this.path("/test/hadoop/subdir");
        this.createFile(file);
        Assert.assertTrue("Created subdir", this.fs.mkdirs(subdir));
        Assert.assertTrue("File exists", this.fs.exists(file));
        Assert.assertTrue("Parent dir exists", this.fs.exists(parentDir));
        Assert.assertTrue("Subdir exists", this.fs.exists(subdir));
        Assert.assertTrue("Deleted subdir", this.fs.delete(subdir, true));
        Assert.assertTrue("Parent should exist", this.fs.exists(parentDir));
        Assert.assertTrue("Deleted file", this.fs.delete(file, false));
        Assert.assertTrue("Parent should exist", this.fs.exists(parentDir));
    }

    @Test
    public void testRenameNonExistentPath() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = this.path("/test/hadoop/path");
        Path dst = this.path("/test/new/newpath");
        try {
            super.rename(src, dst, false, false, false);
            Assert.fail("Should throw FileNotFoundException!");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testRenameFileMoveToNonExistentDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = this.path("/test/hadoop/file");
        this.createFile(src);
        Path dst = this.path("/test/new/newfile");
        try {
            super.rename(src, dst, false, true, false);
            Assert.fail("Should throw FileNotFoundException!");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testRenameDirectoryConcurrent() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = this.path("/test/hadoop/file/");
        Path child1 = this.path("/test/hadoop/file/1");
        Path child2 = this.path("/test/hadoop/file/2");
        Path child3 = this.path("/test/hadoop/file/3");
        Path child4 = this.path("/test/hadoop/file/4");
        this.createFile(child1);
        this.createFile(child2);
        this.createFile(child3);
        this.createFile(child4);
        Path dst = this.path("/test/new");
        super.rename(src, dst, true, false, true);
        Assert.assertEquals(4, this.fs.listStatus(dst).length);
    }

    @Test
    public void testRenameDirectoryCopyTaskAllSucceed() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path srcOne = this.path("/test/hadoop/file/1");
        this.createFile(srcOne);
        Path dstOne = this.path("/test/new/file/1");
        Path dstTwo = this.path("/test/new/file/2");
        AliyunOSSCopyFileContext copyFileContext = new AliyunOSSCopyFileContext();
        AliyunOSSFileSystemStore store = getStore();
        store.storeEmptyFile("test/new/file/");
        AliyunOSSCopyFileTask oneCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstOne.toUri().getPath().substring(1), copyFileContext);
        oneCopyFileTask.run();
        Assume.assumeFalse(copyFileContext.isCopyFailure());
        AliyunOSSCopyFileTask twoCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstTwo.toUri().getPath().substring(1), copyFileContext);
        twoCopyFileTask.run();
        Assume.assumeFalse(copyFileContext.isCopyFailure());
        copyFileContext.lock();
        try {
            copyFileContext.awaitAllFinish(2);
        } catch (InterruptedException e) {
            throw new Exception(e);
        } finally {
            copyFileContext.unlock();
        }
        Assume.assumeFalse(copyFileContext.isCopyFailure());
    }

    @Test
    public void testRenameDirectoryCopyTaskAllFailed() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path srcOne = this.path("/test/hadoop/file/1");
        this.createFile(srcOne);
        Path dstOne = new Path("1");
        Path dstTwo = new Path("2");
        AliyunOSSCopyFileContext copyFileContext = new AliyunOSSCopyFileContext();
        AliyunOSSFileSystemStore store = getStore();
        // store.storeEmptyFile("test/new/file/");
        AliyunOSSCopyFileTask oneCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstOne.toUri().getPath().substring(1), copyFileContext);
        oneCopyFileTask.run();
        Assume.assumeTrue(copyFileContext.isCopyFailure());
        AliyunOSSCopyFileTask twoCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstTwo.toUri().getPath().substring(1), copyFileContext);
        twoCopyFileTask.run();
        Assume.assumeTrue(copyFileContext.isCopyFailure());
        copyFileContext.lock();
        try {
            copyFileContext.awaitAllFinish(2);
        } catch (InterruptedException e) {
            throw new Exception(e);
        } finally {
            copyFileContext.unlock();
        }
        Assume.assumeTrue(copyFileContext.isCopyFailure());
    }

    @Test
    public void testRenameDirectoryCopyTaskPartialFailed() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path srcOne = this.path("/test/hadoop/file/1");
        this.createFile(srcOne);
        Path dstOne = new Path("1");
        Path dstTwo = new Path("/test/new/file/2");
        Path dstThree = new Path("3");
        AliyunOSSCopyFileContext copyFileContext = new AliyunOSSCopyFileContext();
        AliyunOSSFileSystemStore store = getStore();
        // store.storeEmptyFile("test/new/file/");
        AliyunOSSCopyFileTask oneCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstOne.toUri().getPath().substring(1), copyFileContext);
        oneCopyFileTask.run();
        Assume.assumeTrue(copyFileContext.isCopyFailure());
        AliyunOSSCopyFileTask twoCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstTwo.toUri().getPath().substring(1), copyFileContext);
        twoCopyFileTask.run();
        Assume.assumeTrue(copyFileContext.isCopyFailure());
        AliyunOSSCopyFileTask threeCopyFileTask = new AliyunOSSCopyFileTask(store, srcOne.toUri().getPath().substring(1), length, dstThree.toUri().getPath().substring(1), copyFileContext);
        threeCopyFileTask.run();
        Assume.assumeTrue(copyFileContext.isCopyFailure());
        copyFileContext.lock();
        try {
            copyFileContext.awaitAllFinish(3);
        } catch (InterruptedException e) {
            throw new Exception(e);
        } finally {
            copyFileContext.unlock();
        }
        Assume.assumeTrue(copyFileContext.isCopyFailure());
    }

    @Test
    public void testRenameDirectoryMoveToNonExistentDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = this.path("/test/hadoop/dir");
        this.fs.mkdirs(src);
        Path dst = this.path("/test/new/newdir");
        try {
            super.rename(src, dst, false, true, false);
            Assert.fail("Should throw FileNotFoundException!");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testRenameFileMoveToExistingDirectory() throws Exception {
        super.testRenameFileMoveToExistingDirectory();
    }

    @Test
    public void testRenameFileAsExistingFile() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = this.path("/test/hadoop/file");
        this.createFile(src);
        Path dst = this.path("/test/new/newfile");
        this.createFile(dst);
        try {
            super.rename(src, dst, false, true, true);
            Assert.fail("Should throw FileAlreadyExistsException");
        } catch (FileAlreadyExistsException e) {
            // expected
        }
    }

    @Test
    public void testRenameDirectoryAsExistingFile() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = this.path("/test/hadoop/dir");
        this.fs.mkdirs(src);
        Path dst = this.path("/test/new/newfile");
        this.createFile(dst);
        try {
            super.rename(src, dst, false, true, true);
            Assert.fail("Should throw FileAlreadyExistsException");
        } catch (FileAlreadyExistsException e) {
            // expected
        }
    }

    @Test
    public void testGetFileStatusFileAndDirectory() throws Exception {
        Path filePath = this.path("/test/oss/file1");
        this.createFile(filePath);
        Assert.assertTrue("Should be file", this.fs.getFileStatus(filePath).isFile());
        Assert.assertFalse("Should not be directory", this.fs.getFileStatus(filePath).isDirectory());
        Path dirPath = this.path("/test/oss/dir");
        this.fs.mkdirs(dirPath);
        Assert.assertTrue("Should be directory", this.fs.getFileStatus(dirPath).isDirectory());
        Assert.assertFalse("Should not be file", this.fs.getFileStatus(dirPath).isFile());
        Path parentPath = this.path("/test/oss");
        for (FileStatus fileStatus : fs.listStatus(parentPath)) {
            Assert.assertTrue("file and directory should be new", ((fileStatus.getModificationTime()) > 0L));
        }
    }

    @Test
    public void testMkdirsForExistingFile() throws Exception {
        Path testFile = this.path("/test/hadoop/file");
        Assert.assertFalse(this.fs.exists(testFile));
        this.createFile(testFile);
        Assert.assertTrue(this.fs.exists(testFile));
        try {
            this.fs.mkdirs(testFile);
            Assert.fail("Should throw FileAlreadyExistsException!");
        } catch (FileAlreadyExistsException e) {
            // expected
        }
    }
}

