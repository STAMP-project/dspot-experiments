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


import FileContext.DEFAULT_PERM;
import Options.CreateOpts;
import Rename.NONE;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.EnumSet;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.HadoopIllegalArgumentException;
import org.apache.hadoop.fs.contract.ContractTestUtils;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <p>
 * A collection of tests for the {@link FileContext}.
 * This test should be used for testing an instance of FileContext
 *  that has been initialized to a specific default FileSystem such a
 *  LocalFileSystem, HDFS,S3, etc.
 * </p>
 * <p>
 * To test a given {@link FileSystem} implementation create a subclass of this
 * test and override {@link #setUp()} to initialize the <code>fc</code>
 * {@link FileContext} instance variable.
 *
 * Since this a junit 4 you can also do a single setup before
 * the start of any tests.
 * E.g.
 *
 * @unknown public static void clusterSetupAtBegining()
 * @unknown public static void ClusterShutdownAtEnd()
</p>
 */
public abstract class FileContextMainOperationsBaseTest {
    protected static final Logger LOG = LoggerFactory.getLogger(FileContextMainOperationsBaseTest.class);

    private static String TEST_DIR_AAA2 = "test/hadoop2/aaa";

    private static String TEST_DIR_AAA = "test/hadoop/aaa";

    private static String TEST_DIR_AXA = "test/hadoop/axa";

    private static String TEST_DIR_AXX = "test/hadoop/axx";

    private static int numBlocks = 2;

    public Path localFsRootPath;

    protected final FileContextTestHelper fileContextTestHelper = createFileContextHelper();

    protected static FileContext fc;

    private static final PathFilter DEFAULT_FILTER = new PathFilter() {
        @Override
        public boolean accept(final Path file) {
            return true;
        }
    };

    // A test filter with returns any path containing an "x" or "X"
    private static final PathFilter TEST_X_FILTER = new PathFilter() {
        @Override
        public boolean accept(Path file) {
            if ((file.getName().contains("x")) || (file.getName().contains("X")))
                return true;
            else
                return false;

        }
    };

    private static final byte[] data = FileContextTestHelper.getFileData(FileContextMainOperationsBaseTest.numBlocks, FileContextTestHelper.getDefaultBlockSize());

    @Test
    public void testFsStatus() throws Exception {
        FsStatus fsStatus = FileContextMainOperationsBaseTest.fc.getFsStatus(null);
        Assert.assertNotNull(fsStatus);
        // used, free and capacity are non-negative longs
        Assert.assertTrue(((fsStatus.getUsed()) >= 0));
        Assert.assertTrue(((fsStatus.getRemaining()) >= 0));
        Assert.assertTrue(((fsStatus.getCapacity()) >= 0));
    }

    @Test
    public void testWorkingDirectory() throws Exception {
        // First we cd to our test root
        Path workDir = new Path(fileContextTestHelper.getAbsoluteTestRootPath(FileContextMainOperationsBaseTest.fc), new Path("test"));
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(workDir);
        Assert.assertEquals(workDir, FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(new Path("."));
        Assert.assertEquals(workDir, FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(new Path(".."));
        Assert.assertEquals(workDir.getParent(), FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        // cd using a relative path
        // Go back to our test root
        workDir = new Path(fileContextTestHelper.getAbsoluteTestRootPath(FileContextMainOperationsBaseTest.fc), new Path("test"));
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(workDir);
        Assert.assertEquals(workDir, FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        Path relativeDir = new Path("existingDir1");
        Path absoluteDir = new Path(workDir, "existingDir1");
        FileContextMainOperationsBaseTest.fc.mkdir(absoluteDir, DEFAULT_PERM, true);
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(relativeDir);
        Assert.assertEquals(absoluteDir, FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        // cd using a absolute path
        absoluteDir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/existingDir2");
        FileContextMainOperationsBaseTest.fc.mkdir(absoluteDir, DEFAULT_PERM, true);
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(absoluteDir);
        Assert.assertEquals(absoluteDir, FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        // Now open a file relative to the wd we just set above.
        Path absolutePath = new Path(absoluteDir, "foo");
        FileContextMainOperationsBaseTest.fc.create(absolutePath, EnumSet.of(CREATE)).close();
        FileContextMainOperationsBaseTest.fc.open(new Path("foo")).close();
        // Now mkdir relative to the dir we cd'ed to
        FileContextMainOperationsBaseTest.fc.mkdir(new Path("newDir"), DEFAULT_PERM, true);
        Assert.assertTrue(FileContextTestHelper.isDir(FileContextMainOperationsBaseTest.fc, new Path(absoluteDir, "newDir")));
        absoluteDir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "nonexistingPath");
        try {
            FileContextMainOperationsBaseTest.fc.setWorkingDirectory(absoluteDir);
            Assert.fail("cd to non existing dir should have failed");
        } catch (Exception e) {
            // Exception as expected
        }
        // Try a URI
        absoluteDir = new Path(localFsRootPath, "existingDir");
        FileContextMainOperationsBaseTest.fc.mkdir(absoluteDir, DEFAULT_PERM, true);
        FileContextMainOperationsBaseTest.fc.setWorkingDirectory(absoluteDir);
        Assert.assertEquals(absoluteDir, FileContextMainOperationsBaseTest.fc.getWorkingDirectory());
        Path aRegularFile = new Path("aRegularFile");
        createFile(aRegularFile);
        try {
            FileContextMainOperationsBaseTest.fc.setWorkingDirectory(aRegularFile);
            Assert.fail("An IOException expected.");
        } catch (IOException ioe) {
            // okay
        }
    }

    @Test
    public void testMkdirs() throws Exception {
        Path testDir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop");
        Assert.assertFalse(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDir));
        Assert.assertFalse(FileContextTestHelper.isFile(FileContextMainOperationsBaseTest.fc, testDir));
        FileContextMainOperationsBaseTest.fc.mkdir(testDir, FsPermission.getDefault(), true);
        Assert.assertTrue(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDir));
        Assert.assertFalse(FileContextTestHelper.isFile(FileContextMainOperationsBaseTest.fc, testDir));
        FileContextMainOperationsBaseTest.fc.mkdir(testDir, FsPermission.getDefault(), true);
        Assert.assertTrue(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDir));
        Assert.assertFalse(FileContextTestHelper.isFile(FileContextMainOperationsBaseTest.fc, testDir));
        Path parentDir = testDir.getParent();
        Assert.assertTrue(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, parentDir));
        Assert.assertFalse(FileContextTestHelper.isFile(FileContextMainOperationsBaseTest.fc, parentDir));
        Path grandparentDir = parentDir.getParent();
        Assert.assertTrue(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, grandparentDir));
        Assert.assertFalse(FileContextTestHelper.isFile(FileContextMainOperationsBaseTest.fc, grandparentDir));
    }

    @Test
    public void testMkdirsFailsForSubdirectoryOfExistingFile() throws Exception {
        Path testDir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop");
        Assert.assertFalse(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDir));
        FileContextMainOperationsBaseTest.fc.mkdir(testDir, FsPermission.getDefault(), true);
        Assert.assertTrue(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDir));
        createFile(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file"));
        Path testSubDir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file/subdir");
        try {
            FileContextMainOperationsBaseTest.fc.mkdir(testSubDir, FsPermission.getDefault(), true);
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // expected
        }
        try {
            Assert.assertFalse(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testSubDir));
        } catch (AccessControlException e) {
            // Expected : HDFS-11132 Checks on paths under file may be rejected by
            // file missing execute permission.
        }
        Path testDeepSubDir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file/deep/sub/dir");
        try {
            FileContextMainOperationsBaseTest.fc.mkdir(testDeepSubDir, FsPermission.getDefault(), true);
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // expected
        }
        try {
            Assert.assertFalse(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDeepSubDir));
        } catch (AccessControlException e) {
            // Expected : HDFS-11132 Checks on paths under file may be rejected by
            // file missing execute permission.
        }
    }

    @Test
    public void testGetFileStatusThrowsExceptionForNonExistentFile() throws Exception {
        try {
            FileContextMainOperationsBaseTest.fc.getFileStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file"));
            Assert.fail("Should throw FileNotFoundException");
        } catch (FileNotFoundException e) {
            // expected
        }
    }

    @Test
    public void testListStatusThrowsExceptionForNonExistentFile() throws Exception {
        try {
            FileContextMainOperationsBaseTest.fc.listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file"));
            Assert.fail("Should throw FileNotFoundException");
        } catch (FileNotFoundException fnfe) {
            // expected
        }
    }

    @Test
    public void testListStatus() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a"), getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/b"), getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/c/1") };
        Assert.assertFalse(FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0]));
        for (Path path : testDirs) {
            FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
        }
        // test listStatus that returns an array
        FileStatus[] paths = FileContextMainOperationsBaseTest.fc.util().listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test"));
        Assert.assertEquals(1, paths.length);
        Assert.assertEquals(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop"), paths[0].getPath());
        paths = FileContextMainOperationsBaseTest.fc.util().listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop"));
        Assert.assertEquals(3, paths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a"), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/b"), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/c"), paths));
        paths = FileContextMainOperationsBaseTest.fc.util().listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a"));
        Assert.assertEquals(0, paths.length);
        // test listStatus that returns an iterator
        RemoteIterator<FileStatus> pathsIterator = FileContextMainOperationsBaseTest.fc.listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test"));
        Assert.assertEquals(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop"), pathsIterator.next().getPath());
        Assert.assertFalse(pathsIterator.hasNext());
        pathsIterator = FileContextMainOperationsBaseTest.fc.listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop"));
        FileStatus[] subdirs = new FileStatus[3];
        int i = 0;
        while ((i < 3) && (pathsIterator.hasNext())) {
            subdirs[(i++)] = pathsIterator.next();
        } 
        Assert.assertFalse(pathsIterator.hasNext());
        Assert.assertTrue((i == 3));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a"), subdirs));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/b"), subdirs));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/c"), subdirs));
        pathsIterator = FileContextMainOperationsBaseTest.fc.listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a"));
        Assert.assertFalse(pathsIterator.hasNext());
    }

    @Test
    public void testListFiles() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/dir1"), getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/dir1/dir1"), getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/dir2") };
        Path[] testFiles = new Path[]{ new Path(testDirs[0], "file1"), new Path(testDirs[0], "file2"), new Path(testDirs[1], "file2"), new Path(testDirs[2], "file1") };
        for (Path path : testDirs) {
            FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
        }
        for (Path p : testFiles) {
            FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).build();
            out.writeByte(0);
            out.close();
        }
        RemoteIterator<LocatedFileStatus> filesIterator = FileContextMainOperationsBaseTest.fc.util().listFiles(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test"), true);
        LocatedFileStatus[] fileStats = new LocatedFileStatus[testFiles.length];
        for (int i = 0; i < (fileStats.length); i++) {
            Assert.assertTrue(filesIterator.hasNext());
            fileStats[i] = filesIterator.next();
        }
        Assert.assertFalse(filesIterator.hasNext());
        for (Path p : testFiles) {
            Assert.assertTrue(containsPath(p, fileStats));
        }
    }

    @Test
    public void testListStatusFilterWithNoMatches() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // listStatus with filters returns empty correctly
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test"), FileContextMainOperationsBaseTest.TEST_X_FILTER);
        Assert.assertEquals(0, filteredPaths.length);
    }

    @Test
    public void testListStatusFilterWithSomeMatches() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // should return 2 paths ("/test/hadoop/axa" and "/test/hadoop/axx")
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().listStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop"), FileContextMainOperationsBaseTest.TEST_X_FILTER);
        Assert.assertEquals(2, filteredPaths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), filteredPaths));
    }

    @Test
    public void testGlobStatusNonExistentFile() throws Exception {
        FileStatus[] paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoopfsdf"));
        Assert.assertNull(paths);
        paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoopfsdf/?"));
        Assert.assertEquals(0, paths.length);
        paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoopfsdf/xyz*/?"));
        Assert.assertEquals(0, paths.length);
    }

    @Test
    public void testGlobStatusWithNoMatchesInPath() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // should return nothing
        FileStatus[] paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/?"));
        Assert.assertEquals(0, paths.length);
    }

    @Test
    public void testGlobStatusSomeMatchesInDirectories() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // Should return two items ("/test/hadoop" and "/test/hadoop2")
        FileStatus[] paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop*"));
        Assert.assertEquals(2, paths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop"), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop2"), paths));
    }

    @Test
    public void testGlobStatusWithMultipleWildCardMatches() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // Should return all 4 items ("/test/hadoop/aaa", "/test/hadoop/axa"
        // "/test/hadoop/axx", and "/test/hadoop2/axx")
        FileStatus[] paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop*/*"));
        Assert.assertEquals(4, paths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2), paths));
    }

    @Test
    public void testGlobStatusWithMultipleMatchesOfSingleChar() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA2) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // Should return only 2 items ("/test/hadoop/axa", "/test/hadoop/axx")
        FileStatus[] paths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/ax?"));
        Assert.assertEquals(2, paths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), paths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), paths));
    }

    @Test
    public void testGlobStatusFilterWithEmptyPathResults() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // This should return an empty set
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/?"), FileContextMainOperationsBaseTest.DEFAULT_FILTER);
        Assert.assertEquals(0, filteredPaths.length);
    }

    @Test
    public void testGlobStatusFilterWithSomePathMatchesAndTrivialFilter() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // This should return all three (aaa, axa, axx)
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/*"), FileContextMainOperationsBaseTest.DEFAULT_FILTER);
        Assert.assertEquals(3, filteredPaths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), filteredPaths));
    }

    @Test
    public void testGlobStatusFilterWithMultipleWildCardMatchesAndTrivialFilter() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // This should return all three (aaa, axa, axx)
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a??"), FileContextMainOperationsBaseTest.DEFAULT_FILTER);
        Assert.assertEquals(3, filteredPaths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), filteredPaths));
    }

    @Test
    public void testGlobStatusFilterWithMultiplePathMatchesAndNonTrivialFilter() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // This should return two (axa, axx)
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/*"), FileContextMainOperationsBaseTest.TEST_X_FILTER);
        Assert.assertEquals(2, filteredPaths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), filteredPaths));
    }

    @Test
    public void testGlobStatusFilterWithNoMatchingPathsAndNonTrivialFilter() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // This should return an empty set
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/?"), FileContextMainOperationsBaseTest.TEST_X_FILTER);
        Assert.assertEquals(0, filteredPaths.length);
    }

    @Test
    public void testGlobStatusFilterWithMultiplePathWildcardsAndNonTrivialFilter() throws Exception {
        Path[] testDirs = new Path[]{ getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AAA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX) };
        if ((FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, testDirs[0])) == false) {
            for (Path path : testDirs) {
                FileContextMainOperationsBaseTest.fc.mkdir(path, FsPermission.getDefault(), true);
            }
        }
        // This should return two (axa, axx)
        FileStatus[] filteredPaths = FileContextMainOperationsBaseTest.fc.util().globStatus(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/a??"), FileContextMainOperationsBaseTest.TEST_X_FILTER);
        Assert.assertEquals(2, filteredPaths.length);
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXA), filteredPaths));
        Assert.assertTrue(containsPath(getTestRootPath(FileContextMainOperationsBaseTest.fc, FileContextMainOperationsBaseTest.TEST_DIR_AXX), filteredPaths));
    }

    @Test
    public void testWriteReadAndDeleteEmptyFile() throws Exception {
        writeReadAndDelete(0);
    }

    @Test
    public void testWriteReadAndDeleteHalfABlock() throws Exception {
        writeReadAndDelete(((FileContextTestHelper.getDefaultBlockSize()) / 2));
    }

    @Test
    public void testWriteReadAndDeleteOneBlock() throws Exception {
        writeReadAndDelete(FileContextTestHelper.getDefaultBlockSize());
    }

    @Test
    public void testWriteReadAndDeleteOneAndAHalfBlocks() throws Exception {
        int blockSize = FileContextTestHelper.getDefaultBlockSize();
        writeReadAndDelete((blockSize + (blockSize / 2)));
    }

    @Test
    public void testWriteReadAndDeleteTwoBlocks() throws Exception {
        writeReadAndDelete(((FileContextTestHelper.getDefaultBlockSize()) * 2));
    }

    @Test(expected = HadoopIllegalArgumentException.class)
    public void testNullCreateFlag() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/file");
        FileContextMainOperationsBaseTest.fc.create(p, null);
        Assert.fail("Excepted exception not thrown");
    }

    @Test(expected = HadoopIllegalArgumentException.class)
    public void testEmptyCreateFlag() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/file");
        FileContextMainOperationsBaseTest.fc.create(p, EnumSet.noneOf(CreateFlag.CreateFlag.class));
        Assert.fail("Excepted exception not thrown");
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void testCreateFlagCreateExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagCreateExistingFile");
        createFile(p);
        FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(CREATE));
        Assert.fail("Excepted exception not thrown");
    }

    @Test(expected = FileNotFoundException.class)
    public void testCreateFlagOverwriteNonExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagOverwriteNonExistingFile");
        FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(OVERWRITE));
        Assert.fail("Excepted exception not thrown");
    }

    @Test
    public void testCreateFlagOverwriteExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagOverwriteExistingFile");
        createFile(p);
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(OVERWRITE));
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, FileContextMainOperationsBaseTest.data.length);
    }

    @Test(expected = FileNotFoundException.class)
    public void testCreateFlagAppendNonExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagAppendNonExistingFile");
        FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(APPEND));
        Assert.fail("Excepted exception not thrown");
    }

    @Test
    public void testCreateFlagAppendExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagAppendExistingFile");
        createFile(p);
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(APPEND));
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, (2 * (FileContextMainOperationsBaseTest.data.length)));
    }

    @Test
    public void testCreateFlagCreateAppendNonExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagCreateAppendNonExistingFile");
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(CREATE, APPEND));
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, FileContextMainOperationsBaseTest.data.length);
    }

    @Test
    public void testCreateFlagCreateAppendExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testCreateFlagCreateAppendExistingFile");
        createFile(p);
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(CREATE, APPEND));
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, (2 * (FileContextMainOperationsBaseTest.data.length)));
    }

    @Test(expected = HadoopIllegalArgumentException.class)
    public void testCreateFlagAppendOverwrite() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/nonExistent");
        FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(APPEND, OVERWRITE));
        Assert.fail("Excepted exception not thrown");
    }

    @Test(expected = HadoopIllegalArgumentException.class)
    public void testCreateFlagAppendCreateOverwrite() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/nonExistent");
        FileContextMainOperationsBaseTest.fc.create(p, EnumSet.of(CREATE, APPEND, OVERWRITE));
        Assert.fail("Excepted exception not thrown");
    }

    @Test
    public void testBuilderCreateNonExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testBuilderCreateNonExistingFile");
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).build();
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, FileContextMainOperationsBaseTest.data.length);
    }

    @Test
    public void testBuilderCreateExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testBuilderCreateExistingFile");
        createFile(p);
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).overwrite(true).build();
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, FileContextMainOperationsBaseTest.data.length);
    }

    @Test
    public void testBuilderCreateAppendNonExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testBuilderCreateAppendNonExistingFile");
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).append().build();
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, FileContextMainOperationsBaseTest.data.length);
    }

    @Test
    public void testBuilderCreateAppendExistingFile() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/testBuilderCreateAppendExistingFile");
        createFile(p);
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).append().build();
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, (2 * (FileContextMainOperationsBaseTest.data.length)));
    }

    @Test
    public void testBuilderCreateRecursive() throws IOException {
        Path p = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/parent/no/exist/file1");
        try (FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).build()) {
            Assert.fail("Should throw FileNotFoundException on non-exist directory");
        } catch (FileNotFoundException e) {
        }
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(p).recursive().build();
        FileContextMainOperationsBaseTest.writeData(FileContextMainOperationsBaseTest.fc, p, out, FileContextMainOperationsBaseTest.data, FileContextMainOperationsBaseTest.data.length);
    }

    @Test
    public void testWriteInNonExistentDirectory() throws IOException {
        Path path = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        Assert.assertFalse("Parent doesn't exist", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, path.getParent()));
        createFile(path);
        Assert.assertTrue("Exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, path));
        Assert.assertEquals("Length", FileContextMainOperationsBaseTest.data.length, FileContextMainOperationsBaseTest.fc.getFileStatus(path).getLen());
        Assert.assertTrue("Parent exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, path.getParent()));
    }

    @Test
    public void testDeleteNonExistentFile() throws IOException {
        Path path = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        Assert.assertFalse("Doesn't exist", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, path));
        Assert.assertFalse("No deletion", FileContextMainOperationsBaseTest.fc.delete(path, true));
    }

    @Test
    public void testDeleteRecursively() throws IOException {
        Path dir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop");
        Path file = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        Path subdir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/subdir");
        createFile(file);
        FileContextMainOperationsBaseTest.fc.mkdir(subdir, FsPermission.getDefault(), true);
        Assert.assertTrue("File exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, file));
        Assert.assertTrue("Dir exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, dir));
        Assert.assertTrue("Subdir exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, subdir));
        try {
            FileContextMainOperationsBaseTest.fc.delete(dir, false);
            Assert.fail("Should throw IOException.");
        } catch (IOException e) {
            // expected
        }
        Assert.assertTrue("File still exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, file));
        Assert.assertTrue("Dir still exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, dir));
        Assert.assertTrue("Subdir still exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, subdir));
        Assert.assertTrue("Deleted", FileContextMainOperationsBaseTest.fc.delete(dir, true));
        Assert.assertFalse("File doesn't exist", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, file));
        Assert.assertFalse("Dir doesn't exist", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, dir));
        Assert.assertFalse("Subdir doesn't exist", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, subdir));
    }

    @Test
    public void testDeleteEmptyDirectory() throws IOException {
        Path dir = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop");
        FileContextMainOperationsBaseTest.fc.mkdir(dir, FsPermission.getDefault(), true);
        Assert.assertTrue("Dir exists", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, dir));
        Assert.assertTrue("Deleted", FileContextMainOperationsBaseTest.fc.delete(dir, false));
        Assert.assertFalse("Dir doesn't exist", FileContextTestHelper.exists(FileContextMainOperationsBaseTest.fc, dir));
    }

    @Test
    public void testRenameNonExistentPath() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/nonExistent");
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/newpath");
        try {
            rename(src, dst, false, false, NONE);
            Assert.fail("Should throw FileNotFoundException");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
        try {
            rename(src, dst, false, false, Rename.OVERWRITE);
            Assert.fail("Should throw FileNotFoundException");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
    }

    @Test
    public void testRenameFileToNonExistentDirectory() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/nonExistent/newfile");
        try {
            rename(src, dst, true, false, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
        try {
            rename(src, dst, true, false, Rename.OVERWRITE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
    }

    @Test
    public void testRenameFileToDestinationWithParentFile() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/parentFile/newfile");
        createFile(dst.getParent());
        try {
            rename(src, dst, true, false, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
        }
        try {
            rename(src, dst, true, false, Rename.OVERWRITE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
        }
    }

    @Test
    public void testRenameFileToExistingParent() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/newfile");
        FileContextMainOperationsBaseTest.fc.mkdir(dst.getParent(), DEFAULT_PERM, true);
        rename(src, dst, false, true, Rename.OVERWRITE);
    }

    @Test
    public void testRenameFileToItself() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        try {
            rename(src, src, true, true, NONE);
            Assert.fail("Renamed file to itself");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Also fails with overwrite
        try {
            rename(src, src, true, true, Rename.OVERWRITE);
            Assert.fail("Renamed file to itself");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
    }

    @Test
    public void testRenameFileAsExistingFile() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/existingFile");
        createFile(dst);
        // Fails without overwrite option
        try {
            rename(src, dst, true, true, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Succeeds with overwrite option
        rename(src, dst, false, true, Rename.OVERWRITE);
    }

    @Test
    public void testRenameFileAsExistingDirectory() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/existingDir");
        FileContextMainOperationsBaseTest.fc.mkdir(dst, DEFAULT_PERM, true);
        // Fails without overwrite option
        try {
            rename(src, dst, true, true, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
        }
        // File cannot be renamed as directory
        try {
            rename(src, dst, true, true, Rename.OVERWRITE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
        }
    }

    @Test
    public void testRenameDirectoryToItself() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir");
        FileContextMainOperationsBaseTest.fc.mkdir(src, DEFAULT_PERM, true);
        try {
            rename(src, src, true, true, NONE);
            Assert.fail("Renamed directory to itself");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Also fails with overwrite
        try {
            rename(src, src, true, true, Rename.OVERWRITE);
            Assert.fail("Renamed directory to itself");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
    }

    @Test
    public void testRenameDirectoryToNonExistentParent() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir");
        FileContextMainOperationsBaseTest.fc.mkdir(src, DEFAULT_PERM, true);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/nonExistent/newdir");
        try {
            rename(src, dst, true, false, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
        try {
            rename(src, dst, true, false, Rename.OVERWRITE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            Assert.assertTrue(((unwrapException(e)) instanceof FileNotFoundException));
        }
    }

    @Test
    public void testRenameDirectoryAsNonExistentDirectory() throws Exception {
        testRenameDirectoryAsNonExistentDirectory(NONE);
        tearDown();
        testRenameDirectoryAsNonExistentDirectory(Rename.OVERWRITE);
    }

    @Test
    public void testRenameDirectoryAsEmptyDirectory() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir");
        FileContextMainOperationsBaseTest.fc.mkdir(src, DEFAULT_PERM, true);
        createFile(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir/file1"));
        createFile(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir/subdir/file2"));
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/newdir");
        FileContextMainOperationsBaseTest.fc.mkdir(dst, DEFAULT_PERM, true);
        // Fails without overwrite option
        try {
            rename(src, dst, true, true, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            // Expected (cannot over-write non-empty destination)
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Succeeds with the overwrite option
        rename(src, dst, false, true, Rename.OVERWRITE);
    }

    @Test
    public void testRenameDirectoryAsNonEmptyDirectory() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir");
        FileContextMainOperationsBaseTest.fc.mkdir(src, DEFAULT_PERM, true);
        createFile(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir/file1"));
        createFile(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir/subdir/file2"));
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/newdir");
        FileContextMainOperationsBaseTest.fc.mkdir(dst, DEFAULT_PERM, true);
        createFile(getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/newdir/file1"));
        // Fails without overwrite option
        try {
            rename(src, dst, true, true, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
            // Expected (cannot over-write non-empty destination)
            Assert.assertTrue(((unwrapException(e)) instanceof FileAlreadyExistsException));
        }
        // Fails even with the overwrite option
        try {
            rename(src, dst, true, true, Rename.OVERWRITE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException ex) {
            // Expected (cannot over-write non-empty destination)
        }
    }

    @Test
    public void testRenameDirectoryAsFile() throws Exception {
        if (!(renameSupported()))
            return;

        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/dir");
        FileContextMainOperationsBaseTest.fc.mkdir(src, DEFAULT_PERM, true);
        Path dst = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/new/newfile");
        createFile(dst);
        // Fails without overwrite option
        try {
            rename(src, dst, true, true, NONE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException e) {
        }
        // Directory cannot be renamed as existing file
        try {
            rename(src, dst, true, true, Rename.OVERWRITE);
            Assert.fail("Expected exception was not thrown");
        } catch (IOException ex) {
        }
    }

    @Test
    public void testInputStreamClosedTwice() throws IOException {
        // HADOOP-4760 according to Closeable#close() closing already-closed
        // streams should have no effect.
        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        createFile(src);
        FSDataInputStream in = FileContextMainOperationsBaseTest.fc.open(src);
        in.close();
        in.close();
    }

    @Test
    public void testOutputStreamClosedTwice() throws IOException {
        // HADOOP-4760 according to Closeable#close() closing already-closed
        // streams should have no effect.
        Path src = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test/hadoop/file");
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(src, EnumSet.of(CREATE), CreateOpts.createParent());
        out.writeChar('H');// write some data

        out.close();
        out.close();
    }

    /**
     * Test FileContext APIs when symlinks are not supported
     */
    @Test
    public void testUnsupportedSymlink() throws IOException {
        Path file = getTestRootPath(FileContextMainOperationsBaseTest.fc, "file");
        Path link = getTestRootPath(FileContextMainOperationsBaseTest.fc, "linkToFile");
        if (!(FileContextMainOperationsBaseTest.fc.getDefaultFileSystem().supportsSymlinks())) {
            try {
                FileContextMainOperationsBaseTest.fc.createSymlink(file, link, false);
                Assert.fail(("Created a symlink on a file system that " + "does not support symlinks."));
            } catch (UnsupportedOperationException e) {
                // Expected
            }
            createFile(file);
            try {
                FileContextMainOperationsBaseTest.fc.getLinkTarget(file);
                Assert.fail(("Got a link target on a file system that " + "does not support symlinks."));
            } catch (IOException e) {
                // Expected
            }
            Assert.assertEquals(FileContextMainOperationsBaseTest.fc.getFileStatus(file), FileContextMainOperationsBaseTest.fc.getFileLinkStatus(file));
        }
    }

    @Test
    public void testOpen2() throws IOException {
        final Path rootPath = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test");
        // final Path rootPath = getAbsoluteTestRootPath(fc);
        final Path path = new Path(rootPath, "zoo");
        createFile(path);
        final long length = FileContextMainOperationsBaseTest.fc.getFileStatus(path).getLen();
        try (FSDataInputStream fsdis = FileContextMainOperationsBaseTest.fc.open(path, 2048)) {
            byte[] bb = new byte[((int) (length))];
            fsdis.readFully(bb);
            Assert.assertArrayEquals(FileContextMainOperationsBaseTest.data, bb);
        }
    }

    @Test
    public void testSetVerifyChecksum() throws IOException {
        final Path rootPath = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test");
        final Path path = new Path(rootPath, "zoo");
        FSDataOutputStream out = FileContextMainOperationsBaseTest.fc.create(path, EnumSet.of(CREATE), CreateOpts.createParent());
        try {
            // instruct FS to verify checksum through the FileContext:
            FileContextMainOperationsBaseTest.fc.setVerifyChecksum(true, path);
            out.write(FileContextMainOperationsBaseTest.data, 0, FileContextMainOperationsBaseTest.data.length);
        } finally {
            out.close();
        }
        // NB: underlying FS may be different (this is an abstract test),
        // so we cannot assert .zoo.crc existence.
        // Instead, we check that the file is read correctly:
        FileStatus fileStatus = FileContextMainOperationsBaseTest.fc.getFileStatus(path);
        final long len = fileStatus.getLen();
        Assert.assertTrue((len == (FileContextMainOperationsBaseTest.data.length)));
        byte[] bb = new byte[((int) (len))];
        FSDataInputStream fsdis = FileContextMainOperationsBaseTest.fc.open(path);
        try {
            fsdis.readFully(bb);
        } finally {
            fsdis.close();
        }
        Assert.assertArrayEquals(FileContextMainOperationsBaseTest.data, bb);
    }

    @Test
    public void testListCorruptFileBlocks() throws IOException {
        final Path rootPath = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test");
        final Path path = new Path(rootPath, "zoo");
        createFile(path);
        try {
            final RemoteIterator<Path> remoteIterator = FileContextMainOperationsBaseTest.fc.listCorruptFileBlocks(path);
            if (listCorruptedBlocksSupported()) {
                Assert.assertTrue((remoteIterator != null));
                Path p;
                while (remoteIterator.hasNext()) {
                    p = remoteIterator.next();
                    System.out.println(("corrupted block: " + p));
                } 
                try {
                    remoteIterator.next();
                    Assert.fail();
                } catch (NoSuchElementException nsee) {
                    // okay
                }
            } else {
                Assert.fail();
            }
        } catch (UnsupportedOperationException uoe) {
            if (listCorruptedBlocksSupported()) {
                Assert.fail(uoe.toString());
            } else {
                // okay
            }
        }
    }

    @Test
    public void testDeleteOnExitUnexisting() throws IOException {
        final Path rootPath = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test");
        final Path path = new Path(rootPath, "zoo");
        boolean registered = FileContextMainOperationsBaseTest.fc.deleteOnExit(path);
        // because "zoo" does not exist:
        Assert.assertTrue((!registered));
    }

    @Test
    public void testFileContextStatistics() throws IOException {
        FileContext.clearStatistics();
        final Path rootPath = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test");
        final Path path = new Path(rootPath, "zoo");
        createFile(path);
        byte[] bb = new byte[FileContextMainOperationsBaseTest.data.length];
        FSDataInputStream fsdis = FileContextMainOperationsBaseTest.fc.open(path);
        try {
            fsdis.readFully(bb);
        } finally {
            fsdis.close();
        }
        Assert.assertArrayEquals(FileContextMainOperationsBaseTest.data, bb);
        FileContext.printStatistics();
    }

    /* Test method
     org.apache.hadoop.fs.FileContext.getFileContext(AbstractFileSystem)
     */
    @Test
    public void testGetFileContext1() throws IOException {
        final Path rootPath = getTestRootPath(FileContextMainOperationsBaseTest.fc, "test");
        AbstractFileSystem asf = FileContextMainOperationsBaseTest.fc.getDefaultFileSystem();
        // create FileContext using the protected #getFileContext(1) method:
        FileContext fc2 = FileContext.getFileContext(asf);
        // Now just check that this context can do something reasonable:
        final Path path = new Path(rootPath, "zoo");
        FSDataOutputStream out = fc2.create(path, EnumSet.of(CREATE), CreateOpts.createParent());
        out.close();
        Path pathResolved = fc2.resolvePath(path);
        Assert.assertEquals(pathResolved.toUri().getPath(), path.toUri().getPath());
    }

    @Test
    public void testOpenFileRead() throws Exception {
        final Path path = path("testOpenFileRead");
        createFile(path);
        final long length = FileContextMainOperationsBaseTest.fc.getFileStatus(path).getLen();
        try (FSDataInputStream fsdis = FileContextMainOperationsBaseTest.fc.openFile(path).opt("fs.test.something", true).opt("fs.test.something2", 3).opt("fs.test.something3", "3").build().get()) {
            byte[] bb = new byte[((int) (length))];
            fsdis.readFully(bb);
            Assert.assertArrayEquals(FileContextMainOperationsBaseTest.data, bb);
        }
    }

    @Test
    public void testOpenFileUnknownOption() throws Throwable {
        describe("calling openFile fails when a 'must()' option is unknown");
        final Path path = path("testOpenFileUnknownOption");
        FutureDataInputStreamBuilder builder = FileContextMainOperationsBaseTest.fc.openFile(path).opt("fs.test.something", true).must("fs.test.something", true);
        LambdaTestUtils.intercept(IllegalArgumentException.class, () -> builder.build());
    }

    @Test
    public void testOpenFileLazyFail() throws Throwable {
        describe("openFile fails on a missing file in the get() and not before");
        FutureDataInputStreamBuilder builder = FileContextMainOperationsBaseTest.fc.openFile(path("testOpenFileUnknownOption")).opt("fs.test.something", true);
        LambdaTestUtils.interceptFuture(FileNotFoundException.class, "", builder.build());
    }

    @Test
    public void testOpenFileApplyRead() throws Throwable {
        describe("use the apply sequence");
        Path path = path("testOpenFileApplyRead");
        createFile(path);
        CompletableFuture<Long> readAllBytes = FileContextMainOperationsBaseTest.fc.openFile(path).build().thenApply(ContractTestUtils::readStream);
        Assert.assertEquals("Wrong number of bytes read from stream", FileContextMainOperationsBaseTest.data.length, ((long) (readAllBytes.get())));
    }

    @Test
    public void testOpenFileApplyAsyncRead() throws Throwable {
        describe("verify that async accept callbacks are evaluated");
        Path path = path("testOpenFileApplyAsyncRead");
        createFile(path);
        CompletableFuture<FSDataInputStream> future = FileContextMainOperationsBaseTest.fc.openFile(path).build();
        AtomicBoolean accepted = new AtomicBoolean(false);
        future.thenAcceptAsync(( i) -> accepted.set(true)).get();
        Assert.assertTrue("async accept operation not invoked", accepted.get());
    }
}

