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


import AbstractFileSystem.NO_ABSTRACT_FS_ERROR;
import CreateFlag.CREATE;
import FileContext.DEFAULT_PERM;
import Rename.OVERWRITE;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import org.apache.hadoop.fs.Options.CreateOpts;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Base test for symbolic links
 */
public abstract class SymlinkBaseTest {
    // Re-enable symlinks for tests, see HADOOP-10020 and HADOOP-10052
    static {
        FileSystem.enableSymlinks();
    }

    static final long seed = 3735928559L;

    static final int blockSize = 8192;

    static final int fileSize = 16384;

    static final int numBlocks = (SymlinkBaseTest.fileSize) / (SymlinkBaseTest.blockSize);

    protected static FSTestWrapper wrapper;

    /**
     * The root is not a symlink
     */
    @Test(timeout = 10000)
    public void testStatRoot() throws IOException {
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(new Path("/")).isSymlink());
    }

    /**
     * Test setWorkingDirectory not resolves symlinks
     */
    @Test(timeout = 10000)
    public void testSetWDNotResolvesLinks() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path linkToDir = new Path(((testBaseDir1()) + "/link"));
        SymlinkBaseTest.wrapper.createSymlink(dir, linkToDir, false);
        SymlinkBaseTest.wrapper.setWorkingDirectory(linkToDir);
        Assert.assertEquals(linkToDir.getName(), SymlinkBaseTest.wrapper.getWorkingDirectory().getName());
    }

    /**
     * Test create a dangling link
     */
    @Test(timeout = 10000)
    public void testCreateDanglingLink() throws IOException {
        Path file = new Path("/noSuchFile");
        Path link = new Path(((testBaseDir1()) + "/link"));
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.wrapper.getFileStatus(link);
            Assert.fail("Got file status of non-existant file");
        } catch (FileNotFoundException f) {
            // Expected
        }
        SymlinkBaseTest.wrapper.delete(link, false);
    }

    /**
     * Test create a link to null and empty path
     */
    @Test(timeout = 10000)
    public void testCreateLinkToNullEmpty() throws IOException {
        Path link = new Path(((testBaseDir1()) + "/link"));
        try {
            SymlinkBaseTest.wrapper.createSymlink(null, link, false);
            Assert.fail("Can't create symlink to null");
        } catch (NullPointerException e) {
            // Expected, create* with null yields NPEs
        }
        try {
            SymlinkBaseTest.wrapper.createSymlink(new Path(""), link, false);
            Assert.fail("Can't create symlink to empty string");
        } catch (IllegalArgumentException e) {
            // Expected, Path("") is invalid
        }
    }

    /**
     * Create a link with createParent set
     */
    @Test(timeout = 10000)
    public void testCreateLinkCanCreateParent() throws IOException {
        Path file = new Path(((testBaseDir1()) + "/file"));
        Path link = new Path(((testBaseDir2()) + "/linkToFile"));
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.delete(new Path(testBaseDir2()), true);
        try {
            SymlinkBaseTest.wrapper.createSymlink(file, link, false);
            Assert.fail("Created link without first creating parent dir");
        } catch (IOException x) {
            // Expected. Need to create testBaseDir2() first.
        }
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(new Path(testBaseDir2())));
        SymlinkBaseTest.wrapper.createSymlink(file, link, true);
        SymlinkBaseTest.readFile(link);
    }

    /**
     * Try to create a directory given a path that refers to a symlink
     */
    @Test(timeout = 10000)
    public void testMkdirExistingLink() throws IOException {
        Path file = new Path(((testBaseDir1()) + "/targetFile"));
        SymlinkBaseTest.createAndWriteFile(file);
        Path dir = new Path(((testBaseDir1()) + "/link"));
        SymlinkBaseTest.wrapper.createSymlink(file, dir, false);
        try {
            SymlinkBaseTest.wrapper.mkdir(dir, DEFAULT_PERM, false);
            Assert.fail("Created a dir where a symlink exists");
        } catch (FileAlreadyExistsException e) {
            // Expected. The symlink already exists.
        } catch (IOException e) {
            // LocalFs just throws an IOException
            Assert.assertEquals("file", getScheme());
        }
    }

    /**
     * Try to create a file with parent that is a dangling link
     */
    @Test(timeout = 10000)
    public void testCreateFileViaDanglingLinkParent() throws IOException {
        Path dir = new Path(((testBaseDir1()) + "/dangling"));
        Path file = new Path(((testBaseDir1()) + "/dangling/file"));
        SymlinkBaseTest.wrapper.createSymlink(new Path("/doesNotExist"), dir, false);
        FSDataOutputStream out;
        try {
            out = SymlinkBaseTest.wrapper.create(file, EnumSet.of(CREATE), CreateOpts.repFac(((short) (1))), CreateOpts.blockSize(SymlinkBaseTest.blockSize));
            out.close();
            Assert.fail("Created a link with dangling link parent");
        } catch (FileNotFoundException e) {
            // Expected. The parent is dangling.
        }
    }

    /**
     * Delete a link
     */
    @Test(timeout = 10000)
    public void testDeleteLink() throws IOException {
        Path file = new Path(((testBaseDir1()) + "/file"));
        Path link = new Path(((testBaseDir1()) + "/linkToFile"));
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        SymlinkBaseTest.readFile(link);
        SymlinkBaseTest.wrapper.delete(link, false);
        try {
            SymlinkBaseTest.readFile(link);
            Assert.fail("Symlink should have been deleted");
        } catch (IOException x) {
            // Expected
        }
        // If we deleted the link we can put it back
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
    }

    /**
     * Ensure open resolves symlinks
     */
    @Test(timeout = 10000)
    public void testOpenResolvesLinks() throws IOException {
        Path file = new Path(((testBaseDir1()) + "/noSuchFile"));
        Path link = new Path(((testBaseDir1()) + "/link"));
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.wrapper.open(link);
            Assert.fail("link target does not exist");
        } catch (FileNotFoundException x) {
            // Expected
        }
        SymlinkBaseTest.wrapper.delete(link, false);
    }

    /**
     * Stat a link to a file
     */
    @Test(timeout = 10000)
    public void testStatLinkToFile() throws IOException {
        Path file = new Path(((testBaseDir1()) + "/file"));
        Path linkToFile = new Path(((testBaseDir1()) + "/linkToFile"));
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, linkToFile, false);
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(linkToFile).isDirectory());
        Assert.assertTrue(SymlinkBaseTest.wrapper.isSymlink(linkToFile));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(linkToFile));
        Assert.assertFalse(SymlinkBaseTest.wrapper.isDir(linkToFile));
        Assert.assertEquals(file, SymlinkBaseTest.wrapper.getLinkTarget(linkToFile));
        // The local file system does not fully resolve the link
        // when obtaining the file status
        if (!("file".equals(getScheme()))) {
            Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(file), SymlinkBaseTest.wrapper.getFileStatus(linkToFile));
            Assert.assertEquals(SymlinkBaseTest.wrapper.makeQualified(file), SymlinkBaseTest.wrapper.getFileStatus(linkToFile).getPath());
            Assert.assertEquals(SymlinkBaseTest.wrapper.makeQualified(linkToFile), SymlinkBaseTest.wrapper.getFileLinkStatus(linkToFile).getPath());
        }
    }

    /**
     * Stat a relative link to a file
     */
    @Test(timeout = 10000)
    public void testStatRelLinkToFile() throws IOException {
        Assume.assumeTrue((!("file".equals(getScheme()))));
        Path file = new Path(testBaseDir1(), "file");
        Path linkToFile = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(new Path("file"), linkToFile, false);
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(file), SymlinkBaseTest.wrapper.getFileStatus(linkToFile));
        Assert.assertEquals(SymlinkBaseTest.wrapper.makeQualified(file), SymlinkBaseTest.wrapper.getFileStatus(linkToFile).getPath());
        Assert.assertEquals(SymlinkBaseTest.wrapper.makeQualified(linkToFile), SymlinkBaseTest.wrapper.getFileLinkStatus(linkToFile).getPath());
    }

    /**
     * Stat a link to a directory
     */
    @Test(timeout = 10000)
    public void testStatLinkToDir() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path linkToDir = new Path(((testBaseDir1()) + "/linkToDir"));
        SymlinkBaseTest.wrapper.createSymlink(dir, linkToDir, false);
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileStatus(linkToDir).isSymlink());
        Assert.assertTrue(SymlinkBaseTest.wrapper.isDir(linkToDir));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(linkToDir).isDirectory());
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(linkToDir).isSymlink());
        Assert.assertFalse(SymlinkBaseTest.wrapper.isFile(linkToDir));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isDir(linkToDir));
        Assert.assertEquals(dir, SymlinkBaseTest.wrapper.getLinkTarget(linkToDir));
    }

    /**
     * Stat a dangling link
     */
    @Test(timeout = 10000)
    public void testStatDanglingLink() throws IOException {
        Path file = new Path("/noSuchFile");
        Path link = new Path(((testBaseDir1()) + "/link"));
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(link).isDirectory());
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(link).isSymlink());
    }

    /**
     * Stat a non-existant file
     */
    @Test(timeout = 10000)
    public void testStatNonExistentFiles() throws IOException {
        Path fileAbs = new Path("/doesNotExist");
        try {
            SymlinkBaseTest.wrapper.getFileLinkStatus(fileAbs);
            Assert.fail("Got FileStatus for non-existant file");
        } catch (FileNotFoundException f) {
            // Expected
        }
        try {
            SymlinkBaseTest.wrapper.getLinkTarget(fileAbs);
            Assert.fail("Got link target for non-existant file");
        } catch (FileNotFoundException f) {
            // Expected
        }
    }

    /**
     * Test stat'ing a regular file and directory
     */
    @Test(timeout = 10000)
    public void testStatNonLinks() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(((testBaseDir1()) + "/file"));
        SymlinkBaseTest.createAndWriteFile(file);
        try {
            SymlinkBaseTest.wrapper.getLinkTarget(dir);
            Assert.fail("Lstat'd a non-symlink");
        } catch (IOException e) {
            // Expected.
        }
        try {
            SymlinkBaseTest.wrapper.getLinkTarget(file);
            Assert.fail("Lstat'd a non-symlink");
        } catch (IOException e) {
            // Expected.
        }
    }

    /**
     * Test links that link to each other
     */
    @Test(timeout = 10000)
    public void testRecursiveLinks() throws IOException {
        Path link1 = new Path(((testBaseDir1()) + "/link1"));
        Path link2 = new Path(((testBaseDir1()) + "/link2"));
        SymlinkBaseTest.wrapper.createSymlink(link1, link2, false);
        SymlinkBaseTest.wrapper.createSymlink(link2, link1, false);
        try {
            SymlinkBaseTest.readFile(link1);
            Assert.fail("Read recursive link");
        } catch (FileNotFoundException f) {
            // LocalFs throws sub class of IOException, since File.exists
            // returns false for a link to link.
        } catch (IOException x) {
            Assert.assertEquals(("Possible cyclic loop while following symbolic link " + (link1.toString())), x.getMessage());
        }
    }

    /**
     * Test creating a symlink using relative paths
     */
    @Test(timeout = 10000)
    public void testCreateLinkUsingRelPaths() throws IOException {
        Path fileAbs = new Path(testBaseDir1(), "file");
        Path linkAbs = new Path(testBaseDir1(), "linkToFile");
        Path schemeAuth = new Path(testURI().toString());
        Path fileQual = new Path(schemeAuth, ((testBaseDir1()) + "/file"));
        SymlinkBaseTest.createAndWriteFile(fileAbs);
        SymlinkBaseTest.wrapper.setWorkingDirectory(new Path(testBaseDir1()));
        SymlinkBaseTest.wrapper.createSymlink(new Path("file"), new Path("linkToFile"), false);
        checkLink(linkAbs, new Path("file"), fileQual);
        // Now rename the link's parent. Because the target was specified
        // with a relative path the link should still resolve.
        Path dir1 = new Path(testBaseDir1());
        Path dir2 = new Path(testBaseDir2());
        Path linkViaDir2 = new Path(testBaseDir2(), "linkToFile");
        Path fileViaDir2 = new Path(schemeAuth, ((testBaseDir2()) + "/file"));
        SymlinkBaseTest.wrapper.rename(dir1, dir2, OVERWRITE);
        FileStatus[] stats = SymlinkBaseTest.wrapper.listStatus(dir2);
        Assert.assertEquals(fileViaDir2, SymlinkBaseTest.wrapper.getFileLinkStatus(linkViaDir2).getSymlink());
        SymlinkBaseTest.readFile(linkViaDir2);
    }

    /**
     * Test creating a symlink using absolute paths
     */
    @Test(timeout = 10000)
    public void testCreateLinkUsingAbsPaths() throws IOException {
        Path fileAbs = new Path(((testBaseDir1()) + "/file"));
        Path linkAbs = new Path(((testBaseDir1()) + "/linkToFile"));
        Path schemeAuth = new Path(testURI().toString());
        Path fileQual = new Path(schemeAuth, ((testBaseDir1()) + "/file"));
        SymlinkBaseTest.createAndWriteFile(fileAbs);
        SymlinkBaseTest.wrapper.createSymlink(fileAbs, linkAbs, false);
        checkLink(linkAbs, fileAbs, fileQual);
        // Now rename the link's parent. The target doesn't change and
        // now no longer exists so accessing the link should fail.
        Path dir1 = new Path(testBaseDir1());
        Path dir2 = new Path(testBaseDir2());
        Path linkViaDir2 = new Path(testBaseDir2(), "linkToFile");
        SymlinkBaseTest.wrapper.rename(dir1, dir2, OVERWRITE);
        Assert.assertEquals(fileQual, SymlinkBaseTest.wrapper.getFileLinkStatus(linkViaDir2).getSymlink());
        try {
            SymlinkBaseTest.readFile(linkViaDir2);
            Assert.fail("The target should not exist");
        } catch (FileNotFoundException x) {
            // Expected
        }
    }

    /**
     * Test creating a symlink using fully and partially qualified paths.
     * NB: For local fs this actually tests partially qualified paths,
     * as they don't support fully qualified paths.
     */
    @Test(timeout = 10000)
    public void testCreateLinkUsingFullyQualPaths() throws IOException {
        Path fileAbs = new Path(testBaseDir1(), "file");
        Path linkAbs = new Path(testBaseDir1(), "linkToFile");
        Path fileQual = new Path(testURI().toString(), fileAbs);
        Path linkQual = new Path(testURI().toString(), linkAbs);
        SymlinkBaseTest.createAndWriteFile(fileAbs);
        SymlinkBaseTest.wrapper.createSymlink(fileQual, linkQual, false);
        checkLink(linkAbs, ("file".equals(getScheme()) ? fileAbs : fileQual), fileQual);
        // Now rename the link's parent. The target doesn't change and
        // now no longer exists so accessing the link should fail.
        Path dir1 = new Path(testBaseDir1());
        Path dir2 = new Path(testBaseDir2());
        Path linkViaDir2 = new Path(testBaseDir2(), "linkToFile");
        SymlinkBaseTest.wrapper.rename(dir1, dir2, OVERWRITE);
        Assert.assertEquals(fileQual, SymlinkBaseTest.wrapper.getFileLinkStatus(linkViaDir2).getSymlink());
        try {
            SymlinkBaseTest.readFile(linkViaDir2);
            Assert.fail("The target should not exist");
        } catch (FileNotFoundException x) {
            // Expected
        }
    }

    /**
     * Test creating a symlink using partially qualified paths, ie a scheme
     * but no authority and vice versa. We just test link targets here since
     * creating using a partially qualified path is file system specific.
     */
    @Test(timeout = 10000)
    public void testCreateLinkUsingPartQualPath1() throws IOException {
        // Partially qualified paths are covered for local file systems
        // in the previous test.
        Assume.assumeTrue((!("file".equals(getScheme()))));
        Path schemeAuth = new Path(testURI().toString());
        Path fileWoHost = new Path(((((getScheme()) + "://") + (testBaseDir1())) + "/file"));
        Path link = new Path(((testBaseDir1()) + "/linkToFile"));
        Path linkQual = new Path(schemeAuth, ((testBaseDir1()) + "/linkToFile"));
        FSTestWrapper localWrapper = SymlinkBaseTest.wrapper.getLocalFSWrapper();
        SymlinkBaseTest.wrapper.createSymlink(fileWoHost, link, false);
        // Partially qualified path is stored
        Assert.assertEquals(fileWoHost, SymlinkBaseTest.wrapper.getLinkTarget(linkQual));
        // NB: We do not add an authority
        Assert.assertEquals(fileWoHost.toString(), SymlinkBaseTest.wrapper.getFileLinkStatus(link).getSymlink().toString());
        Assert.assertEquals(fileWoHost.toString(), SymlinkBaseTest.wrapper.getFileLinkStatus(linkQual).getSymlink().toString());
        // Ditto even from another file system
        if ((SymlinkBaseTest.wrapper) instanceof FileContextTestWrapper) {
            Assert.assertEquals(fileWoHost.toString(), localWrapper.getFileLinkStatus(linkQual).getSymlink().toString());
        }
        // Same as if we accessed a partially qualified path directly
        try {
            SymlinkBaseTest.readFile(link);
            Assert.fail("DFS requires URIs with schemes have an authority");
        } catch (RuntimeException e) {
            Assert.assertTrue(((SymlinkBaseTest.wrapper) instanceof FileContextTestWrapper));
            // Expected
        } catch (FileNotFoundException e) {
            Assert.assertTrue(((SymlinkBaseTest.wrapper) instanceof FileSystemTestWrapper));
            GenericTestUtils.assertExceptionContains("File does not exist: /test1/file", e);
        }
    }

    /**
     * Same as above but vice versa (authority but no scheme)
     */
    @Test(timeout = 10000)
    public void testCreateLinkUsingPartQualPath2() throws IOException {
        Path link = new Path(testBaseDir1(), "linkToFile");
        Path fileWoScheme = new Path(((("//" + (testURI().getAuthority())) + (testBaseDir1())) + "/file"));
        if ("file".equals(getScheme())) {
            return;
        }
        SymlinkBaseTest.wrapper.createSymlink(fileWoScheme, link, false);
        Assert.assertEquals(fileWoScheme, SymlinkBaseTest.wrapper.getLinkTarget(link));
        Assert.assertEquals(fileWoScheme.toString(), SymlinkBaseTest.wrapper.getFileLinkStatus(link).getSymlink().toString());
        try {
            SymlinkBaseTest.readFile(link);
            Assert.fail("Accessed a file with w/o scheme");
        } catch (IOException e) {
            // Expected
            if ((SymlinkBaseTest.wrapper) instanceof FileContextTestWrapper) {
                GenericTestUtils.assertExceptionContains(NO_ABSTRACT_FS_ERROR, e);
            } else
                if ((SymlinkBaseTest.wrapper) instanceof FileSystemTestWrapper) {
                    Assert.assertEquals(("No FileSystem for scheme " + (("\"" + "null") + "\"")), e.getMessage());
                }

        }
    }

    /**
     * Lstat and readlink on a normal file and directory
     */
    @Test(timeout = 10000)
    public void testLinkStatusAndTargetWithNonLink() throws IOException {
        Path schemeAuth = new Path(testURI().toString());
        Path dir = new Path(testBaseDir1());
        Path dirQual = new Path(schemeAuth, dir.toString());
        Path file = new Path(testBaseDir1(), "file");
        Path fileQual = new Path(schemeAuth, file.toString());
        SymlinkBaseTest.createAndWriteFile(file);
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(file), SymlinkBaseTest.wrapper.getFileLinkStatus(file));
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(dir), SymlinkBaseTest.wrapper.getFileLinkStatus(dir));
        try {
            SymlinkBaseTest.wrapper.getLinkTarget(file);
            Assert.fail("Get link target on non-link should throw an IOException");
        } catch (IOException x) {
            Assert.assertEquals((("Path " + fileQual) + " is not a symbolic link"), x.getMessage());
        }
        try {
            SymlinkBaseTest.wrapper.getLinkTarget(dir);
            Assert.fail("Get link target on non-link should throw an IOException");
        } catch (IOException x) {
            Assert.assertEquals((("Path " + dirQual) + " is not a symbolic link"), x.getMessage());
        }
    }

    /**
     * Test create symlink to a directory
     */
    @Test(timeout = 10000)
    public void testCreateLinkToDirectory() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(dir1, linkToDir, false);
        Assert.assertFalse(SymlinkBaseTest.wrapper.isFile(linkToDir));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isDir(linkToDir));
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileStatus(linkToDir).isDirectory());
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(linkToDir).isSymlink());
    }

    /**
     * Test create and remove a file through a symlink
     */
    @Test(timeout = 10000)
    public void testCreateFileViaSymlink() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "file");
        SymlinkBaseTest.wrapper.createSymlink(dir, linkToDir, false);
        SymlinkBaseTest.createAndWriteFile(fileViaLink);
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.isDir(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(fileViaLink).isSymlink());
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileStatus(fileViaLink).isDirectory());
        SymlinkBaseTest.readFile(fileViaLink);
        SymlinkBaseTest.wrapper.delete(fileViaLink, true);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(fileViaLink));
    }

    /**
     * Test make and delete directory through a symlink
     */
    @Test(timeout = 10000)
    public void testCreateDirViaSymlink() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path subDir = new Path(testBaseDir1(), "subDir");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path subDirViaLink = new Path(linkToDir, "subDir");
        SymlinkBaseTest.wrapper.createSymlink(dir1, linkToDir, false);
        SymlinkBaseTest.wrapper.mkdir(subDirViaLink, DEFAULT_PERM, true);
        Assert.assertTrue(SymlinkBaseTest.wrapper.isDir(subDirViaLink));
        SymlinkBaseTest.wrapper.delete(subDirViaLink, false);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(subDirViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(subDir));
    }

    /**
     * Create symlink through a symlink
     */
    @Test(timeout = 10000)
    public void testCreateLinkViaLink() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "file");
        Path linkToFile = new Path(linkToDir, "linkToFile");
        /* /b2/linkToDir            -> /b1
        /b2/linkToDir/linkToFile -> /b2/linkToDir/file
         */
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(dir1, linkToDir, false);
        SymlinkBaseTest.wrapper.createSymlink(fileViaLink, linkToFile, false);
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(linkToFile));
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(linkToFile).isSymlink());
        SymlinkBaseTest.readFile(linkToFile);
        Assert.assertEquals(SymlinkBaseTest.fileSize, SymlinkBaseTest.wrapper.getFileStatus(linkToFile).getLen());
        Assert.assertEquals(fileViaLink, SymlinkBaseTest.wrapper.getLinkTarget(linkToFile));
    }

    /**
     * Test create symlink to a directory
     */
    @Test(timeout = 10000)
    public void testListStatusUsingLink() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "link");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(new Path(testBaseDir1()), link, false);
        // The size of the result is file system dependent, Hdfs is 2 (file
        // and link) and LocalFs is 3 (file, link, file crc).
        FileStatus[] stats = SymlinkBaseTest.wrapper.listStatus(link);
        Assert.assertTrue((((stats.length) == 2) || ((stats.length) == 3)));
        RemoteIterator<FileStatus> statsItor = SymlinkBaseTest.wrapper.listStatusIterator(link);
        int dirLen = 0;
        while (statsItor.hasNext()) {
            statsItor.next();
            dirLen++;
        } 
        Assert.assertTrue(((dirLen == 2) || (dirLen == 3)));
    }

    /**
     * Test create symlink using the same path
     */
    @Test(timeout = 10000)
    public void testCreateLinkTwice() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.wrapper.createSymlink(file, link, false);
            Assert.fail("link already exists");
        } catch (IOException x) {
            // Expected
        }
    }

    /**
     * Test access via a symlink to a symlink
     */
    @Test(timeout = 10000)
    public void testCreateLinkToLink() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path linkToLink = new Path(testBaseDir2(), "linkToLink");
        Path fileViaLink = new Path(testBaseDir2(), "linkToLink/file");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(dir1, linkToDir, false);
        SymlinkBaseTest.wrapper.createSymlink(linkToDir, linkToLink, false);
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.isDir(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(fileViaLink).isSymlink());
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileStatus(fileViaLink).isDirectory());
        SymlinkBaseTest.readFile(fileViaLink);
    }

    /**
     * Can not create a file with path that refers to a symlink
     */
    @Test(timeout = 10000)
    public void testCreateFileDirExistingLink() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.createAndWriteFile(link);
            Assert.fail("link already exists");
        } catch (IOException x) {
            // Expected
        }
        try {
            SymlinkBaseTest.wrapper.mkdir(link, FsPermission.getDefault(), false);
            Assert.fail("link already exists");
        } catch (IOException x) {
            // Expected
        }
    }

    /**
     * Test deleting and recreating a symlink
     */
    @Test(timeout = 10000)
    public void testUseLinkAferDeleteLink() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        SymlinkBaseTest.wrapper.delete(link, false);
        try {
            SymlinkBaseTest.readFile(link);
            Assert.fail("link was deleted");
        } catch (IOException x) {
            // Expected
        }
        SymlinkBaseTest.readFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        SymlinkBaseTest.readFile(link);
    }

    /**
     * Test create symlink to .
     */
    @Test(timeout = 10000)
    public void testCreateLinkToDot() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToDot");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.setWorkingDirectory(dir);
        try {
            SymlinkBaseTest.wrapper.createSymlink(new Path("."), link, false);
            Assert.fail("Created symlink to dot");
        } catch (IOException x) {
            // Expected. Path(".") resolves to "" because URI normalizes
            // the dot away and AbstractFileSystem considers "" invalid.
        }
    }

    /**
     * Test create symlink to ..
     */
    @Test(timeout = 10000)
    public void testCreateLinkToDotDot() throws IOException {
        Path file = new Path(testBaseDir1(), "test/file");
        Path dotDot = new Path(testBaseDir1(), "test/..");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "test/file");
        // Symlink to .. is not a problem since the .. is squashed early
        Assert.assertEquals(new Path(testBaseDir1()), dotDot);
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(dotDot, linkToDir, false);
        SymlinkBaseTest.readFile(fileViaLink);
        Assert.assertEquals(SymlinkBaseTest.fileSize, SymlinkBaseTest.wrapper.getFileStatus(fileViaLink).getLen());
    }

    /**
     * Test create symlink to ../file
     */
    @Test(timeout = 10000)
    public void testCreateLinkToDotDotPrefix() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path dir = new Path(testBaseDir1(), "test");
        Path link = new Path(testBaseDir1(), "test/link");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.mkdir(dir, FsPermission.getDefault(), false);
        SymlinkBaseTest.wrapper.setWorkingDirectory(dir);
        SymlinkBaseTest.wrapper.createSymlink(new Path("../file"), link, false);
        SymlinkBaseTest.readFile(link);
        Assert.assertEquals(new Path("../file"), SymlinkBaseTest.wrapper.getLinkTarget(link));
    }

    /**
     * Test rename file using a path that contains a symlink. The rename should
     * work as if the path did not contain a symlink
     */
    @Test(timeout = 10000)
    public void testRenameFileViaSymlink() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "file");
        Path fileNewViaLink = new Path(linkToDir, "fileNew");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(dir, linkToDir, false);
        SymlinkBaseTest.wrapper.rename(fileViaLink, fileNewViaLink);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(file));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(fileNewViaLink));
    }

    /**
     * Test rename a file through a symlink but this time only the
     * destination path has an intermediate symlink. The rename should work
     * as if the path did not contain a symlink
     */
    @Test(timeout = 10000)
    public void testRenameFileToDestViaSymlink() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path subDir = new Path(linkToDir, "subDir");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(dir, linkToDir, false);
        SymlinkBaseTest.wrapper.mkdir(subDir, DEFAULT_PERM, false);
        try {
            SymlinkBaseTest.wrapper.rename(file, subDir);
            Assert.fail("Renamed file to a directory");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(file));
    }

    /**
     * Similar tests as the previous ones but rename a directory
     */
    @Test(timeout = 10000)
    public void testRenameDirViaSymlink() throws IOException {
        Path baseDir = new Path(testBaseDir1());
        Path dir = new Path(baseDir, "dir");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path dirViaLink = new Path(linkToDir, "dir");
        Path dirNewViaLink = new Path(linkToDir, "dirNew");
        SymlinkBaseTest.wrapper.mkdir(dir, DEFAULT_PERM, false);
        SymlinkBaseTest.wrapper.createSymlink(baseDir, linkToDir, false);
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(dirViaLink));
        SymlinkBaseTest.wrapper.rename(dirViaLink, dirNewViaLink);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(dirViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(dir));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(dirNewViaLink));
    }

    /**
     * Similar tests as the previous ones but rename a symlink
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkViaSymlink() throws IOException {
        Path baseDir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "link");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path linkViaLink = new Path(linkToDir, "link");
        Path linkNewViaLink = new Path(linkToDir, "linkNew");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        SymlinkBaseTest.wrapper.createSymlink(baseDir, linkToDir, false);
        SymlinkBaseTest.wrapper.rename(linkViaLink, linkNewViaLink);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(linkViaLink));
        // Check that we didn't rename the link target
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(file));
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(linkNewViaLink).isSymlink());
        SymlinkBaseTest.readFile(linkNewViaLink);
    }

    /**
     * Test rename a directory to a symlink to a directory
     */
    @Test(timeout = 10000)
    public void testRenameDirToSymlinkToDir() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path subDir = new Path(testBaseDir2(), "subDir");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        SymlinkBaseTest.wrapper.mkdir(subDir, DEFAULT_PERM, false);
        SymlinkBaseTest.wrapper.createSymlink(subDir, linkToDir, false);
        try {
            SymlinkBaseTest.wrapper.rename(dir1, linkToDir, OVERWRITE);
            Assert.fail("Renamed directory to a symlink");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(dir1));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(linkToDir));
    }

    /**
     * Test rename a directory to a symlink to a file
     */
    @Test(timeout = 10000)
    public void testRenameDirToSymlinkToFile() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path file = new Path(testBaseDir2(), "file");
        Path linkToFile = new Path(testBaseDir2(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, linkToFile, false);
        try {
            SymlinkBaseTest.wrapper.rename(dir1, linkToFile, OVERWRITE);
            Assert.fail("Renamed directory to a symlink");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(dir1));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(linkToFile));
    }

    /**
     * Test rename a directory to a dangling symlink
     */
    @Test(timeout = 10000)
    public void testRenameDirToDanglingSymlink() throws IOException {
        Path dir = new Path(testBaseDir1());
        Path link = new Path(testBaseDir2(), "linkToFile");
        SymlinkBaseTest.wrapper.createSymlink(new Path("/doesNotExist"), link, false);
        try {
            SymlinkBaseTest.wrapper.rename(dir, link, OVERWRITE);
            Assert.fail("Renamed directory to a symlink");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(dir));
        Assert.assertTrue(((SymlinkBaseTest.wrapper.getFileLinkStatus(link)) != null));
    }

    /**
     * Test rename a file to a symlink to a directory
     */
    @Test(timeout = 10000)
    public void testRenameFileToSymlinkToDir() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path subDir = new Path(testBaseDir1(), "subDir");
        Path link = new Path(testBaseDir1(), "link");
        SymlinkBaseTest.wrapper.mkdir(subDir, DEFAULT_PERM, false);
        SymlinkBaseTest.wrapper.createSymlink(subDir, link, false);
        SymlinkBaseTest.createAndWriteFile(file);
        try {
            SymlinkBaseTest.wrapper.rename(file, link);
            Assert.fail("Renamed file to symlink w/o overwrite");
        } catch (IOException e) {
            // Expected
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        SymlinkBaseTest.wrapper.rename(file, link, OVERWRITE);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(file));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(link));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(link).isSymlink());
    }

    /**
     * Test rename a file to a symlink to a file
     */
    @Test(timeout = 10000)
    public void testRenameFileToSymlinkToFile() throws IOException {
        Path file1 = new Path(testBaseDir1(), "file1");
        Path file2 = new Path(testBaseDir1(), "file2");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file1);
        SymlinkBaseTest.createAndWriteFile(file2);
        SymlinkBaseTest.wrapper.createSymlink(file2, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(file1, link);
            Assert.fail("Renamed file to symlink w/o overwrite");
        } catch (IOException e) {
            // Expected
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        SymlinkBaseTest.wrapper.rename(file1, link, OVERWRITE);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(file1));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(link));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(link).isSymlink());
    }

    /**
     * Test rename a file to a dangling symlink
     */
    @Test(timeout = 10000)
    public void testRenameFileToDanglingSymlink() throws IOException {
        /* NB: Local file system doesn't handle dangling links correctly
        since File.exists(danglinLink) returns false.
         */
        if ("file".equals(getScheme())) {
            return;
        }
        Path file1 = new Path(testBaseDir1(), "file1");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file1);
        SymlinkBaseTest.wrapper.createSymlink(new Path("/doesNotExist"), link, false);
        try {
            SymlinkBaseTest.wrapper.rename(file1, link);
        } catch (IOException e) {
            // Expected
        }
        SymlinkBaseTest.wrapper.rename(file1, link, OVERWRITE);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(file1));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(link));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(link).isSymlink());
    }

    /**
     * Rename a symlink to a new non-existant name
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkNonExistantDest() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link1 = new Path(testBaseDir1(), "linkToFile1");
        Path link2 = new Path(testBaseDir1(), "linkToFile2");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link1, false);
        SymlinkBaseTest.wrapper.rename(link1, link2);
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(link2).isSymlink());
        SymlinkBaseTest.readFile(link2);
        SymlinkBaseTest.readFile(file);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(link1));
    }

    /**
     * Rename a symlink to a file that exists
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkToExistingFile() throws IOException {
        Path file1 = new Path(testBaseDir1(), "file");
        Path file2 = new Path(testBaseDir1(), "someFile");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file1);
        SymlinkBaseTest.createAndWriteFile(file2);
        SymlinkBaseTest.wrapper.createSymlink(file2, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(link, file1);
            Assert.fail("Renamed w/o passing overwrite");
        } catch (IOException e) {
            // Expected
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        SymlinkBaseTest.wrapper.rename(link, file1, OVERWRITE);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(file1).isSymlink());
        Assert.assertEquals(file2, SymlinkBaseTest.wrapper.getLinkTarget(file1));
    }

    /**
     * Rename a symlink to a directory that exists
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkToExistingDir() throws IOException {
        Path dir1 = new Path(testBaseDir1());
        Path dir2 = new Path(testBaseDir2());
        Path subDir = new Path(testBaseDir2(), "subDir");
        Path link = new Path(testBaseDir1(), "linkToDir");
        SymlinkBaseTest.wrapper.createSymlink(dir1, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(link, dir2);
            Assert.fail("Renamed link to a directory");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
        try {
            SymlinkBaseTest.wrapper.rename(link, dir2, OVERWRITE);
            Assert.fail("Renamed link to a directory");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
        // Also fails when dir2 has a sub-directory
        SymlinkBaseTest.wrapper.mkdir(subDir, FsPermission.getDefault(), false);
        try {
            SymlinkBaseTest.wrapper.rename(link, dir2, OVERWRITE);
            Assert.fail("Renamed link to a directory");
        } catch (IOException e) {
            // Expected. Both must be directories.
            Assert.assertTrue(((unwrapException(e)) instanceof IOException));
        }
    }

    /**
     * Rename a symlink to itself
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkToItself() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        SymlinkBaseTest.createAndWriteFile(file);
        Path link = new Path(testBaseDir1(), "linkToFile1");
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(link, link);
            Assert.fail("Failed to get expected IOException");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Fails with overwrite as well
        try {
            SymlinkBaseTest.wrapper.rename(link, link, OVERWRITE);
            Assert.fail("Failed to get expected IOException");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
    }

    /**
     * Rename a symlink
     */
    @Test(timeout = 10000)
    public void testRenameSymlink() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link1 = new Path(testBaseDir1(), "linkToFile1");
        Path link2 = new Path(testBaseDir1(), "linkToFile2");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link1, false);
        SymlinkBaseTest.wrapper.rename(link1, link2);
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileLinkStatus(link2).isSymlink());
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileStatus(link2).isDirectory());
        SymlinkBaseTest.readFile(link2);
        SymlinkBaseTest.readFile(file);
        try {
            SymlinkBaseTest.createAndWriteFile(link2);
            Assert.fail("link was not renamed");
        } catch (IOException x) {
            // Expected
        }
    }

    /**
     * Rename a symlink to the file it links to
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkToFileItLinksTo() throws IOException {
        /* NB: The rename is not atomic, so file is deleted before renaming
        linkToFile. In this interval linkToFile is dangling and local file
        system does not handle dangling links because File.exists returns
        false for dangling links.
         */
        if ("file".equals(getScheme())) {
            return;
        }
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(link, file);
            Assert.fail("Renamed symlink to its target");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Check the rename didn't happen
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(file));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isSymlink(link));
        Assert.assertEquals(file, SymlinkBaseTest.wrapper.getLinkTarget(link));
        try {
            SymlinkBaseTest.wrapper.rename(link, file, OVERWRITE);
            Assert.fail("Renamed symlink to its target");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Check the rename didn't happen
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(file));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isSymlink(link));
        Assert.assertEquals(file, SymlinkBaseTest.wrapper.getLinkTarget(link));
    }

    /**
     * Rename a symlink to the directory it links to
     */
    @Test(timeout = 10000)
    public void testRenameSymlinkToDirItLinksTo() throws IOException {
        /* NB: The rename is not atomic, so dir is deleted before renaming
        linkToFile. In this interval linkToFile is dangling and local file
        system does not handle dangling links because File.exists returns
        false for dangling links.
         */
        if ("file".equals(getScheme())) {
            return;
        }
        Path dir = new Path(testBaseDir1(), "dir");
        Path link = new Path(testBaseDir1(), "linkToDir");
        SymlinkBaseTest.wrapper.mkdir(dir, DEFAULT_PERM, false);
        SymlinkBaseTest.wrapper.createSymlink(dir, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(link, dir);
            Assert.fail("Renamed symlink to its target");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Check the rename didn't happen
        Assert.assertTrue(SymlinkBaseTest.wrapper.isDir(dir));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isSymlink(link));
        Assert.assertEquals(dir, SymlinkBaseTest.wrapper.getLinkTarget(link));
        try {
            SymlinkBaseTest.wrapper.rename(link, dir, OVERWRITE);
            Assert.fail("Renamed symlink to its target");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Check the rename didn't happen
        Assert.assertTrue(SymlinkBaseTest.wrapper.isDir(dir));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(link));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isSymlink(link));
        Assert.assertEquals(dir, SymlinkBaseTest.wrapper.getLinkTarget(link));
    }

    /**
     * Test rename the symlink's target
     */
    @Test(timeout = 10000)
    public void testRenameLinkTarget() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path fileNew = new Path(testBaseDir1(), "fileNew");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        SymlinkBaseTest.wrapper.rename(file, fileNew, OVERWRITE);
        try {
            SymlinkBaseTest.readFile(link);
            Assert.fail("Link should be dangling");
        } catch (IOException x) {
            // Expected
        }
        SymlinkBaseTest.wrapper.rename(fileNew, file, OVERWRITE);
        SymlinkBaseTest.readFile(link);
    }

    /**
     * Test rename a file to path with destination that has symlink parent
     */
    @Test(timeout = 10000)
    public void testRenameFileWithDestParentSymlink() throws IOException {
        Path link = new Path(testBaseDir1(), "link");
        Path file1 = new Path(testBaseDir1(), "file1");
        Path file2 = new Path(testBaseDir1(), "file2");
        Path file3 = new Path(link, "file3");
        Path dir2 = new Path(testBaseDir2());
        // Renaming /dir1/file1 to non-existant file /dir1/link/file3 is OK
        // if link points to a directory...
        SymlinkBaseTest.wrapper.createSymlink(dir2, link, false);
        SymlinkBaseTest.createAndWriteFile(file1);
        SymlinkBaseTest.wrapper.rename(file1, file3);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(file1));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(file3));
        SymlinkBaseTest.wrapper.rename(file3, file1);
        // But fails if link is dangling...
        SymlinkBaseTest.wrapper.delete(link, false);
        SymlinkBaseTest.wrapper.createSymlink(file2, link, false);
        try {
            SymlinkBaseTest.wrapper.rename(file1, file3);
        } catch (IOException e) {
            // Expected
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
        // And if link points to a file...
        SymlinkBaseTest.createAndWriteFile(file2);
        try {
            SymlinkBaseTest.wrapper.rename(file1, file3);
        } catch (IOException e) {
            // Expected
            Assert.assertTrue(((unwrapException(e)) instanceof ParentNotDirectoryException));
        }
    }

    /**
     * Create, write, read, append, rename, get the block locations,
     * checksums, and delete a file using a path with a symlink as an
     * intermediate path component where the link target was specified
     * using an absolute path. Rename is covered in more depth below.
     */
    @Test(timeout = 10000)
    public void testAccessFileViaInterSymlinkAbsTarget() throws IOException {
        Path baseDir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path fileNew = new Path(baseDir, "fileNew");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "file");
        Path fileNewViaLink = new Path(linkToDir, "fileNew");
        SymlinkBaseTest.wrapper.createSymlink(baseDir, linkToDir, false);
        SymlinkBaseTest.createAndWriteFile(fileViaLink);
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(fileViaLink));
        Assert.assertTrue(SymlinkBaseTest.wrapper.isFile(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.isDir(fileViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.getFileLinkStatus(fileViaLink).isSymlink());
        Assert.assertFalse(SymlinkBaseTest.wrapper.isDir(fileViaLink));
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(file), SymlinkBaseTest.wrapper.getFileLinkStatus(file));
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(fileViaLink), SymlinkBaseTest.wrapper.getFileLinkStatus(fileViaLink));
        SymlinkBaseTest.readFile(fileViaLink);
        SymlinkBaseTest.appendToFile(fileViaLink);
        SymlinkBaseTest.wrapper.rename(fileViaLink, fileNewViaLink);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(fileViaLink));
        Assert.assertTrue(SymlinkBaseTest.wrapper.exists(fileNewViaLink));
        SymlinkBaseTest.readFile(fileNewViaLink);
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileBlockLocations(fileNew, 0, 1).length, SymlinkBaseTest.wrapper.getFileBlockLocations(fileNewViaLink, 0, 1).length);
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileChecksum(fileNew), SymlinkBaseTest.wrapper.getFileChecksum(fileNewViaLink));
        SymlinkBaseTest.wrapper.delete(fileNewViaLink, true);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(fileNewViaLink));
    }

    /**
     * Operate on a file using a path with an intermediate symlink where
     * the link target was specified as a fully qualified path.
     */
    @Test(timeout = 10000)
    public void testAccessFileViaInterSymlinkQualTarget() throws IOException {
        Path baseDir = new Path(testBaseDir1());
        Path file = new Path(testBaseDir1(), "file");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "file");
        SymlinkBaseTest.wrapper.createSymlink(SymlinkBaseTest.wrapper.makeQualified(baseDir), linkToDir, false);
        SymlinkBaseTest.createAndWriteFile(fileViaLink);
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(file), SymlinkBaseTest.wrapper.getFileLinkStatus(file));
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(fileViaLink), SymlinkBaseTest.wrapper.getFileLinkStatus(fileViaLink));
        SymlinkBaseTest.readFile(fileViaLink);
    }

    /**
     * Operate on a file using a path with an intermediate symlink where
     * the link target was specified as a relative path.
     */
    @Test(timeout = 10000)
    public void testAccessFileViaInterSymlinkRelTarget() throws IOException {
        Assume.assumeTrue((!("file".equals(getScheme()))));
        Path dir = new Path(testBaseDir1(), "dir");
        Path file = new Path(dir, "file");
        Path linkToDir = new Path(testBaseDir1(), "linkToDir");
        Path fileViaLink = new Path(linkToDir, "file");
        SymlinkBaseTest.wrapper.mkdir(dir, DEFAULT_PERM, false);
        SymlinkBaseTest.wrapper.createSymlink(new Path("dir"), linkToDir, false);
        SymlinkBaseTest.createAndWriteFile(fileViaLink);
        // Note that getFileStatus returns fully qualified paths even
        // when called on an absolute path.
        Assert.assertEquals(SymlinkBaseTest.wrapper.makeQualified(file), SymlinkBaseTest.wrapper.getFileStatus(file).getPath());
        // In each case getFileLinkStatus returns the same FileStatus
        // as getFileStatus since we're not calling it on a link and
        // FileStatus objects are compared by Path.
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(file), SymlinkBaseTest.wrapper.getFileLinkStatus(file));
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(fileViaLink), SymlinkBaseTest.wrapper.getFileLinkStatus(fileViaLink));
        Assert.assertEquals(SymlinkBaseTest.wrapper.getFileStatus(fileViaLink), SymlinkBaseTest.wrapper.getFileLinkStatus(file));
    }

    /**
     * Test create, list, and delete a directory through a symlink
     */
    @Test(timeout = 10000)
    public void testAccessDirViaSymlink() throws IOException {
        Path baseDir = new Path(testBaseDir1());
        Path dir = new Path(testBaseDir1(), "dir");
        Path linkToDir = new Path(testBaseDir2(), "linkToDir");
        Path dirViaLink = new Path(linkToDir, "dir");
        SymlinkBaseTest.wrapper.createSymlink(baseDir, linkToDir, false);
        SymlinkBaseTest.wrapper.mkdir(dirViaLink, DEFAULT_PERM, true);
        Assert.assertTrue(SymlinkBaseTest.wrapper.getFileStatus(dirViaLink).isDirectory());
        FileStatus[] stats = SymlinkBaseTest.wrapper.listStatus(dirViaLink);
        Assert.assertEquals(0, stats.length);
        RemoteIterator<FileStatus> statsItor = SymlinkBaseTest.wrapper.listStatusIterator(dirViaLink);
        Assert.assertFalse(statsItor.hasNext());
        SymlinkBaseTest.wrapper.delete(dirViaLink, false);
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(dirViaLink));
        Assert.assertFalse(SymlinkBaseTest.wrapper.exists(dir));
    }

    /**
     * setTimes affects the target file not the link
     */
    @Test(timeout = 10000)
    public void testSetTimesSymlinkToFile() throws IOException {
        Path file = new Path(testBaseDir1(), "file");
        Path link = new Path(testBaseDir1(), "linkToFile");
        SymlinkBaseTest.createAndWriteFile(file);
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        long at = SymlinkBaseTest.wrapper.getFileLinkStatus(link).getAccessTime();
        // the local file system may not support millisecond timestamps
        SymlinkBaseTest.wrapper.setTimes(link, 2000L, 3000L);
        Assert.assertTrue("The atime of symlink should not be lesser after setTimes()", ((SymlinkBaseTest.wrapper.getFileLinkStatus(link).getAccessTime()) >= at));
        Assert.assertEquals(2000, SymlinkBaseTest.wrapper.getFileStatus(file).getModificationTime());
        Assert.assertEquals(3000, SymlinkBaseTest.wrapper.getFileStatus(file).getAccessTime());
    }

    /**
     * setTimes affects the target directory not the link
     */
    @Test(timeout = 10000)
    public void testSetTimesSymlinkToDir() throws IOException {
        Path dir = new Path(testBaseDir1(), "dir");
        Path link = new Path(testBaseDir1(), "linkToDir");
        SymlinkBaseTest.wrapper.mkdir(dir, DEFAULT_PERM, false);
        SymlinkBaseTest.wrapper.createSymlink(dir, link, false);
        long at = SymlinkBaseTest.wrapper.getFileLinkStatus(link).getAccessTime();
        // the local file system may not support millisecond timestamps
        SymlinkBaseTest.wrapper.setTimes(link, 2000L, 3000L);
        Assert.assertTrue("The atime of symlink should not be lesser after setTimes()", ((SymlinkBaseTest.wrapper.getFileLinkStatus(link).getAccessTime()) >= at));
        Assert.assertEquals(2000, SymlinkBaseTest.wrapper.getFileStatus(dir).getModificationTime());
        Assert.assertEquals(3000, SymlinkBaseTest.wrapper.getFileStatus(dir).getAccessTime());
    }

    /**
     * setTimes does not affect the link even though target does not exist
     */
    @Test(timeout = 10000)
    public void testSetTimesDanglingLink() throws IOException {
        Path file = new Path("/noSuchFile");
        Path link = new Path(((testBaseDir1()) + "/link"));
        SymlinkBaseTest.wrapper.createSymlink(file, link, false);
        long at = SymlinkBaseTest.wrapper.getFileLinkStatus(link).getAccessTime();
        try {
            SymlinkBaseTest.wrapper.setTimes(link, 2000L, 3000L);
            Assert.fail("set times to non-existant file");
        } catch (IOException e) {
            // Expected
        }
        Assert.assertTrue("The atime of symlink should not be lesser after setTimes()", ((SymlinkBaseTest.wrapper.getFileLinkStatus(link).getAccessTime()) >= at));
    }
}

