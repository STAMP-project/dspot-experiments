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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.util.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * A collection of tests for the contract of the {@link FileSystem}.
 * This test should be used for general-purpose implementations of
 * {@link FileSystem}, that is, implementations that provide implementations
 * of all of the functionality of {@link FileSystem}.
 * </p>
 * <p>
 * To test a given {@link FileSystem} implementation create a subclass of this
 * test and add a @Before method to initialize the <code>fs</code>
 * {@link FileSystem} instance variable.
 * </p>
 */
public abstract class FileSystemContractBaseTest {
    private static final Logger LOG = LoggerFactory.getLogger(FileSystemContractBaseTest.class);

    protected static final String TEST_UMASK = "062";

    protected FileSystem fs;

    protected byte[] data = dataset(((getBlockSize()) * 2), 0, 255);

    @Rule
    public Timeout globalTimeout = new Timeout(getGlobalTimeout());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFsStatus() throws Exception {
        FsStatus fsStatus = fs.getStatus();
        Assert.assertNotNull(fsStatus);
        // used, free and capacity are non-negative longs
        Assert.assertTrue(((fsStatus.getUsed()) >= 0));
        Assert.assertTrue(((fsStatus.getRemaining()) >= 0));
        Assert.assertTrue(((fsStatus.getCapacity()) >= 0));
    }

    @Test
    public void testWorkingDirectory() throws Exception {
        Path workDir = path(getDefaultWorkingDirectory());
        Assert.assertEquals(workDir, fs.getWorkingDirectory());
        fs.setWorkingDirectory(fs.makeQualified(new Path(".")));
        Assert.assertEquals(workDir, fs.getWorkingDirectory());
        fs.setWorkingDirectory(fs.makeQualified(new Path("..")));
        Assert.assertEquals(workDir.getParent(), fs.getWorkingDirectory());
        Path relativeDir = fs.makeQualified(new Path("testWorkingDirectory"));
        fs.setWorkingDirectory(relativeDir);
        Assert.assertEquals(relativeDir, fs.getWorkingDirectory());
        Path absoluteDir = path("/FileSystemContractBaseTest/testWorkingDirectory");
        fs.setWorkingDirectory(absoluteDir);
        Assert.assertEquals(absoluteDir, fs.getWorkingDirectory());
    }

    @Test
    public void testMkdirs() throws Exception {
        Path testDir = path("testMkdirs");
        Assert.assertFalse(fs.exists(testDir));
        Assert.assertFalse(fs.isFile(testDir));
        Assert.assertTrue(fs.mkdirs(testDir));
        Assert.assertTrue(fs.exists(testDir));
        Assert.assertFalse(fs.isFile(testDir));
        Assert.assertTrue(fs.mkdirs(testDir));
        Assert.assertTrue(fs.exists(testDir));
        Assert.assertTrue("Should be a directory", fs.isDirectory(testDir));
        Assert.assertFalse(fs.isFile(testDir));
        Path parentDir = testDir.getParent();
        Assert.assertTrue(fs.exists(parentDir));
        Assert.assertFalse(fs.isFile(parentDir));
        Path grandparentDir = parentDir.getParent();
        Assert.assertTrue(fs.exists(grandparentDir));
        Assert.assertFalse(fs.isFile(grandparentDir));
    }

    @Test
    public void testMkdirsFailsForSubdirectoryOfExistingFile() throws Exception {
        Path testDir = path("testMkdirsFailsForSubdirectoryOfExistingFile");
        Assert.assertFalse(fs.exists(testDir));
        Assert.assertTrue(fs.mkdirs(testDir));
        Assert.assertTrue(fs.exists(testDir));
        createFile(path("testMkdirsFailsForSubdirectoryOfExistingFile/file"));
        Path testSubDir = path("testMkdirsFailsForSubdirectoryOfExistingFile/file/subdir");
        try {
            fs.mkdirs(testSubDir);
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // expected
        }
        try {
            Assert.assertFalse(fs.exists(testSubDir));
        } catch (AccessControlException e) {
            // Expected : HDFS-11132 Checks on paths under file may be rejected by
            // file missing execute permission.
        }
        Path testDeepSubDir = path("testMkdirsFailsForSubdirectoryOfExistingFile/file/deep/sub/dir");
        try {
            fs.mkdirs(testDeepSubDir);
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // expected
        }
        try {
            Assert.assertFalse(fs.exists(testDeepSubDir));
        } catch (AccessControlException e) {
            // Expected : HDFS-11132 Checks on paths under file may be rejected by
            // file missing execute permission.
        }
    }

    @Test
    public void testMkdirsWithUmask() throws Exception {
        Configuration conf = fs.getConf();
        String oldUmask = conf.get(FS_PERMISSIONS_UMASK_KEY);
        try {
            conf.set(FS_PERMISSIONS_UMASK_KEY, FileSystemContractBaseTest.TEST_UMASK);
            final Path dir = path("newDir");
            Assert.assertTrue(fs.mkdirs(dir, new FsPermission(((short) (511)))));
            FileStatus status = fs.getFileStatus(dir);
            Assert.assertTrue(status.isDirectory());
            Assert.assertEquals(((short) (461)), status.getPermission().toShort());
        } finally {
            conf.set(FS_PERMISSIONS_UMASK_KEY, oldUmask);
        }
    }

    @Test
    public void testGetFileStatusThrowsExceptionForNonExistentFile() throws Exception {
        try {
            fs.getFileStatus(path("testGetFileStatusThrowsExceptionForNonExistentFile/file"));
            Assert.fail("Should throw FileNotFoundException");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testListStatusThrowsExceptionForNonExistentFile() throws Exception {
        try {
            fs.listStatus(path("testListStatusThrowsExceptionForNonExistentFile/file"));
            Assert.fail("Should throw FileNotFoundException");
        } catch (FileNotFoundException fnfe) {
            // expected
        }
    }

    @Test
    public void testListStatus() throws Exception {
        final Path[] testDirs = new Path[]{ path("testListStatus/a"), path("testListStatus/b"), path("testListStatus/c/1") };
        Assert.assertFalse(fs.exists(testDirs[0]));
        for (Path path : testDirs) {
            Assert.assertTrue(fs.mkdirs(path));
        }
        FileStatus[] paths = fs.listStatus(path("."));
        Assert.assertEquals(1, paths.length);
        Assert.assertEquals(path("testListStatus"), paths[0].getPath());
        paths = fs.listStatus(path("testListStatus"));
        Assert.assertEquals(3, paths.length);
        ArrayList<Path> list = new ArrayList<Path>();
        for (FileStatus fileState : paths) {
            list.add(fileState.getPath());
        }
        Assert.assertTrue(list.contains(path("testListStatus/a")));
        Assert.assertTrue(list.contains(path("testListStatus/b")));
        Assert.assertTrue(list.contains(path("testListStatus/c")));
        paths = fs.listStatus(path("testListStatus/a"));
        Assert.assertEquals(0, paths.length);
    }

    @Test
    public void testWriteReadAndDeleteEmptyFile() throws Exception {
        writeReadAndDelete(0);
    }

    @Test
    public void testWriteReadAndDeleteHalfABlock() throws Exception {
        writeReadAndDelete(((getBlockSize()) / 2));
    }

    @Test
    public void testWriteReadAndDeleteOneBlock() throws Exception {
        writeReadAndDelete(getBlockSize());
    }

    @Test
    public void testWriteReadAndDeleteOneAndAHalfBlocks() throws Exception {
        writeReadAndDelete(((getBlockSize()) + ((getBlockSize()) / 2)));
    }

    @Test
    public void testWriteReadAndDeleteTwoBlocks() throws Exception {
        writeReadAndDelete(((getBlockSize()) * 2));
    }

    @Test
    public void testOverwrite() throws IOException {
        Path path = path("testOverwrite/file");
        fs.mkdirs(path.getParent());
        createFile(path);
        Assert.assertTrue("Exists", fs.exists(path));
        Assert.assertEquals("Length", data.length, fs.getFileStatus(path).getLen());
        try {
            fs.create(path, false).close();
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // Expected
        }
        FSDataOutputStream out = fs.create(path, true);
        out.write(data, 0, data.length);
        out.close();
        Assert.assertTrue("Exists", fs.exists(path));
        Assert.assertEquals("Length", data.length, fs.getFileStatus(path).getLen());
    }

    @Test
    public void testWriteInNonExistentDirectory() throws IOException {
        Path path = path("testWriteInNonExistentDirectory/file");
        Assert.assertFalse("Parent exists", fs.exists(path.getParent()));
        createFile(path);
        Assert.assertTrue("Exists", fs.exists(path));
        Assert.assertEquals("Length", data.length, fs.getFileStatus(path).getLen());
        Assert.assertTrue("Parent exists", fs.exists(path.getParent()));
    }

    @Test
    public void testDeleteNonExistentFile() throws IOException {
        Path path = path("testDeleteNonExistentFile/file");
        Assert.assertFalse(("Path exists: " + path), fs.exists(path));
        Assert.assertFalse("No deletion", fs.delete(path, true));
    }

    @Test
    public void testDeleteRecursively() throws IOException {
        Path dir = path("testDeleteRecursively");
        Path file = path("testDeleteRecursively/file");
        Path subdir = path("testDeleteRecursively/subdir");
        createFile(file);
        Assert.assertTrue("Created subdir", fs.mkdirs(subdir));
        Assert.assertTrue("File exists", fs.exists(file));
        Assert.assertTrue("Dir exists", fs.exists(dir));
        Assert.assertTrue("Subdir exists", fs.exists(subdir));
        try {
            fs.delete(dir, false);
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // expected
        }
        Assert.assertTrue("File still exists", fs.exists(file));
        Assert.assertTrue("Dir still exists", fs.exists(dir));
        Assert.assertTrue("Subdir still exists", fs.exists(subdir));
        Assert.assertTrue("Deleted", fs.delete(dir, true));
        Assert.assertFalse("File doesn't exist", fs.exists(file));
        Assert.assertFalse("Dir doesn't exist", fs.exists(dir));
        Assert.assertFalse("Subdir doesn't exist", fs.exists(subdir));
    }

    @Test
    public void testDeleteEmptyDirectory() throws IOException {
        Path dir = path("testDeleteEmptyDirectory");
        Assert.assertTrue(fs.mkdirs(dir));
        Assert.assertTrue("Dir exists", fs.exists(dir));
        Assert.assertTrue("Deleted", fs.delete(dir, false));
        Assert.assertFalse("Dir doesn't exist", fs.exists(dir));
    }

    @Test
    public void testRenameNonExistentPath() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameNonExistentPath/path");
        Path dst = path("testRenameNonExistentPathNew/newpath");
        rename(src, dst, false, false, false);
    }

    @Test
    public void testRenameFileMoveToNonExistentDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameFileMoveToNonExistentDirectory/file");
        createFile(src);
        Path dst = path("testRenameFileMoveToNonExistentDirectoryNew/newfile");
        rename(src, dst, false, true, false);
    }

    @Test
    public void testRenameFileMoveToExistingDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameFileMoveToExistingDirectory/file");
        createFile(src);
        Path dst = path("testRenameFileMoveToExistingDirectoryNew/newfile");
        fs.mkdirs(dst.getParent());
        rename(src, dst, true, false, true);
    }

    @Test
    public void testRenameFileAsExistingFile() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameFileAsExistingFile/file");
        createFile(src);
        Path dst = path("testRenameFileAsExistingFileNew/newfile");
        createFile(dst);
        rename(src, dst, false, true, true);
    }

    @Test
    public void testRenameFileAsExistingDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameFileAsExistingDirectory/file");
        createFile(src);
        Path dst = path("testRenameFileAsExistingDirectoryNew/newdir");
        fs.mkdirs(dst);
        rename(src, dst, true, false, true);
        assertIsFile(path("testRenameFileAsExistingDirectoryNew/newdir/file"));
    }

    @Test
    public void testRenameDirectoryMoveToNonExistentDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameDirectoryMoveToNonExistentDirectory/dir");
        fs.mkdirs(src);
        Path dst = path("testRenameDirectoryMoveToNonExistentDirectoryNew/newdir");
        rename(src, dst, false, true, false);
    }

    @Test
    public void testRenameDirectoryMoveToExistingDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameDirectoryMoveToExistingDirectory/dir");
        fs.mkdirs(src);
        createFile(path((src + "/file1")));
        createFile(path((src + "/subdir/file2")));
        Path dst = path("testRenameDirectoryMoveToExistingDirectoryNew/newdir");
        fs.mkdirs(dst.getParent());
        rename(src, dst, true, false, true);
        Assert.assertFalse("Nested file1 exists", fs.exists(path((src + "/file1"))));
        Assert.assertFalse("Nested file2 exists", fs.exists(path((src + "/subdir/file2"))));
        Assert.assertTrue("Renamed nested file1 exists", fs.exists(path((dst + "/file1"))));
        Assert.assertTrue("Renamed nested exists", fs.exists(path((dst + "/subdir/file2"))));
    }

    @Test
    public void testRenameDirectoryAsExistingFile() throws Exception {
        Assume.assumeTrue(renameSupported());
        Path src = path("testRenameDirectoryAsExistingFile/dir");
        fs.mkdirs(src);
        Path dst = path("testRenameDirectoryAsExistingFileNew/newfile");
        createFile(dst);
        rename(src, dst, false, true, true);
    }

    @Test
    public void testRenameDirectoryAsExistingDirectory() throws Exception {
        Assume.assumeTrue(renameSupported());
        final Path src = path("testRenameDirectoryAsExistingDirectory/dir");
        fs.mkdirs(src);
        createFile(path((src + "/file1")));
        createFile(path((src + "/subdir/file2")));
        final Path dst = path("testRenameDirectoryAsExistingDirectoryNew/newdir");
        fs.mkdirs(dst);
        rename(src, dst, true, false, true);
        Assert.assertTrue("Destination changed", fs.exists(path((dst + "/dir"))));
        Assert.assertFalse("Nested file1 exists", fs.exists(path((src + "/file1"))));
        Assert.assertFalse("Nested file2 exists", fs.exists(path((src + "/dir/subdir/file2"))));
        Assert.assertTrue("Renamed nested file1 exists", fs.exists(path((dst + "/dir/file1"))));
        Assert.assertTrue("Renamed nested exists", fs.exists(path((dst + "/dir/subdir/file2"))));
    }

    @Test
    public void testInputStreamClosedTwice() throws IOException {
        // HADOOP-4760 according to Closeable#close() closing already-closed
        // streams should have no effect.
        Path src = path("testInputStreamClosedTwice/file");
        createFile(src);
        FSDataInputStream in = fs.open(src);
        in.close();
        in.close();
    }

    @Test
    public void testOutputStreamClosedTwice() throws IOException {
        // HADOOP-4760 according to Closeable#close() closing already-closed
        // streams should have no effect.
        Path src = path("testOutputStreamClosedTwice/file");
        FSDataOutputStream out = fs.create(src);
        out.writeChar('H');// write some data

        out.close();
        out.close();
    }

    /**
     * Verify that if you take an existing file and overwrite it, the new values
     * get picked up.
     * This is a test for the behavior of eventually consistent
     * filesystems.
     *
     * @throws Exception
     * 		on any failure
     */
    @Test
    public void testOverWriteAndRead() throws Exception {
        int blockSize = getBlockSize();
        byte[] filedata1 = dataset((blockSize * 2), 'A', 26);
        byte[] filedata2 = dataset((blockSize * 2), 'a', 26);
        Path path = path("testOverWriteAndRead/file-overwrite");
        writeAndRead(path, filedata1, blockSize, true, false);
        writeAndRead(path, filedata2, blockSize, true, false);
        writeAndRead(path, filedata1, (blockSize * 2), true, false);
        writeAndRead(path, filedata2, (blockSize * 2), true, false);
        writeAndRead(path, filedata1, blockSize, true, false);
        writeAndRead(path, filedata2, (blockSize * 2), true, false);
    }

    /**
     * Assert that a filesystem is case sensitive.
     * This is done by creating a mixed-case filename and asserting that
     * its lower case version is not there.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testFilesystemIsCaseSensitive() throws Exception {
        if (!(filesystemIsCaseSensitive())) {
            FileSystemContractBaseTest.LOG.info("Skipping test");
            return;
        }
        String mixedCaseFilename = "testFilesystemIsCaseSensitive";
        Path upper = path(mixedCaseFilename);
        Path lower = path(StringUtils.toLowerCase(mixedCaseFilename));
        Assert.assertFalse(("File exists" + upper), fs.exists(upper));
        Assert.assertFalse(("File exists" + lower), fs.exists(lower));
        FSDataOutputStream out = fs.create(upper);
        out.writeUTF("UPPER");
        out.close();
        FileStatus upperStatus = fs.getFileStatus(upper);
        Assert.assertTrue(("File does not exist" + upper), fs.exists(upper));
        // verify the lower-case version of the filename doesn't exist
        Assert.assertFalse(("File exists" + lower), fs.exists(lower));
        // now overwrite the lower case version of the filename with a
        // new version.
        out = fs.create(lower);
        out.writeUTF("l");
        out.close();
        Assert.assertTrue(("File does not exist" + lower), fs.exists(lower));
        // verify the length of the upper file hasn't changed
        FileStatus newStatus = fs.getFileStatus(upper);
        Assert.assertEquals(((("Expected status:" + upperStatus) + " actual status ") + newStatus), upperStatus.getLen(), newStatus.getLen());
    }

    /**
     * Asserts that a zero byte file has a status of file and not
     * directory or symlink
     *
     * @throws Exception
     * 		on failures
     */
    @Test
    public void testZeroByteFilesAreFiles() throws Exception {
        Path src = path("testZeroByteFilesAreFiles");
        // create a zero byte file
        FSDataOutputStream out = fs.create(src);
        out.close();
        assertIsFile(src);
    }

    /**
     * Asserts that a zero byte file has a status of file and not
     * directory or symlink
     *
     * @throws Exception
     * 		on failures
     */
    @Test
    public void testMultiByteFilesAreFiles() throws Exception {
        Path src = path("testMultiByteFilesAreFiles");
        FSDataOutputStream out = fs.create(src);
        out.writeUTF("testMultiByteFilesAreFiles");
        out.close();
        assertIsFile(src);
    }

    /**
     * Assert that root directory renames are not allowed
     *
     * @throws Exception
     * 		on failures
     */
    @Test
    public void testRootDirAlwaysExists() throws Exception {
        // this will throw an exception if the path is not found
        fs.getFileStatus(path("/"));
        // this catches overrides of the base exists() method that don't
        // use getFileStatus() as an existence probe
        Assert.assertTrue("FileSystem.exists() fails for root", fs.exists(path("/")));
    }

    /**
     * Assert that root directory renames are not allowed
     *
     * @throws Exception
     * 		on failures
     */
    @Test
    public void testRenameRootDirForbidden() throws Exception {
        Assume.assumeTrue(rootDirTestEnabled());
        Assume.assumeTrue(renameSupported());
        rename(path("/"), path("testRenameRootDirForbidden"), false, true, false);
    }

    /**
     * Assert that renaming a parent directory to be a child
     * of itself is forbidden
     *
     * @throws Exception
     * 		on failures
     */
    @Test
    public void testRenameChildDirForbidden() throws Exception {
        Assume.assumeTrue(renameSupported());
        FileSystemContractBaseTest.LOG.info("testRenameChildDirForbidden");
        Path parentdir = path("testRenameChildDirForbidden");
        fs.mkdirs(parentdir);
        Path childFile = new Path(parentdir, "childfile");
        createFile(childFile);
        // verify one level down
        Path childdir = new Path(parentdir, "childdir");
        rename(parentdir, childdir, false, true, false);
        // now another level
        fs.mkdirs(childdir);
        Path childchilddir = new Path(childdir, "childdir");
        rename(parentdir, childchilddir, false, true, false);
    }

    /**
     * This a sanity check to make sure that any filesystem's handling of
     * renames non-empty dirs doesn't cause any regressions.
     */
    @Test
    public void testRenameToDirWithSamePrefixAllowed() throws Throwable {
        Assume.assumeTrue(renameSupported());
        final Path parentdir = path("testRenameToDirWithSamePrefixAllowed");
        fs.mkdirs(parentdir);
        // Before renaming, we create one file under the source parent directory
        createFile(new Path(parentdir, "mychild"));
        final Path dest = path("testRenameToDirWithSamePrefixAllowedDest");
        rename(parentdir, dest, true, false, true);
    }

    /**
     * trying to rename a directory onto itself should fail,
     * preserving everything underneath.
     */
    @Test
    public void testRenameDirToSelf() throws Throwable {
        Assume.assumeTrue(renameSupported());
        Path parentdir = path("testRenameDirToSelf");
        fs.mkdirs(parentdir);
        Path child = new Path(parentdir, "child");
        createFile(child);
        rename(parentdir, parentdir, false, true, true);
        // verify the child is still there
        assertIsFile(child);
    }

    /**
     * trying to rename a directory onto its parent dir will build
     * a destination path of its original name, which should then fail.
     * The source path and the destination path should still exist afterwards
     */
    @Test
    public void testMoveDirUnderParent() throws Throwable {
        Assume.assumeTrue(renameSupported());
        Path testdir = path("testMoveDirUnderParent");
        fs.mkdirs(testdir);
        Path parent = testdir.getParent();
        // the outcome here is ambiguous, so is not checked
        fs.rename(testdir, parent);
        Assert.assertEquals(("Source exists: " + testdir), true, fs.exists(testdir));
        Assert.assertEquals(("Destination exists" + parent), true, fs.exists(parent));
    }

    /**
     * trying to rename a file onto itself should succeed (it's a no-op)
     */
    @Test
    public void testRenameFileToSelf() throws Throwable {
        Assume.assumeTrue(renameSupported());
        Path filepath = path("testRenameFileToSelf");
        createFile(filepath);
        // HDFS expects rename src, src -> true
        rename(filepath, filepath, true, true, true);
        // verify the file is still there
        assertIsFile(filepath);
    }

    /**
     * trying to move a file into it's parent dir should succeed
     * again: no-op
     */
    @Test
    public void testMoveFileUnderParent() throws Throwable {
        Assume.assumeTrue(renameSupported());
        Path filepath = path("testMoveFileUnderParent");
        createFile(filepath);
        // HDFS expects rename src, src -> true
        rename(filepath, filepath, true, true, true);
        // verify the file is still there
        assertIsFile(filepath);
    }

    @Test
    public void testLSRootDir() throws Throwable {
        Assume.assumeTrue(rootDirTestEnabled());
        Path dir = path("/");
        Path child = path("/FileSystemContractBaseTest");
        createFile(child);
        assertListFilesFinds(dir, child);
    }

    @Test
    public void testListStatusRootDir() throws Throwable {
        Assume.assumeTrue(rootDirTestEnabled());
        Path dir = path("/");
        Path child = path("/FileSystemContractBaseTest");
        createFile(child);
        assertListStatusFinds(dir, child);
    }
}

