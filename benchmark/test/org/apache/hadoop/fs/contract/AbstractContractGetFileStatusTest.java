/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.hadoop.fs.contract;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FilterFileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.test.LambdaTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test getFileStatus and related listing operations.
 */
public abstract class AbstractContractGetFileStatusTest extends AbstractFSContractTestBase {
    private Path testPath;

    private Path target;

    // the tree parameters. Kept small to avoid killing object store test
    // runs too much.
    private static final int TREE_DEPTH = 2;

    private static final int TREE_WIDTH = 3;

    private static final int TREE_FILES = 4;

    private static final int TREE_FILESIZE = 512;

    @Test
    public void testGetFileStatusNonexistentFile() throws Throwable {
        try {
            FileStatus status = getFileSystem().getFileStatus(target);
            // got here: trouble
            Assert.fail(("expected a failure, got " + status));
        } catch (FileNotFoundException e) {
            // expected
            handleExpectedException(e);
        }
    }

    @Test
    public void testGetFileStatusRoot() throws Throwable {
        ContractTestUtils.assertIsDirectory(getFileSystem().getFileStatus(new Path("/")));
    }

    @Test
    public void testListStatusEmptyDirectory() throws IOException {
        describe("List status on an empty directory");
        Path subfolder = createDirWithEmptySubFolder();
        FileSystem fs = getFileSystem();
        Path path = getContract().getTestPath();
        new ContractTestUtils.TreeScanResults(fs.listStatus(path)).assertSizeEquals((("listStatus(" + path) + ")"), 0, 1, 0);
        describe("Test on empty subdirectory");
        new ContractTestUtils.TreeScanResults(fs.listStatus(subfolder)).assertSizeEquals("listStatus(empty subfolder)", 0, 0, 0);
    }

    @Test
    public void testListFilesEmptyDirectoryNonrecursive() throws IOException {
        listFilesOnEmptyDir(false);
    }

    @Test
    public void testListFilesEmptyDirectoryRecursive() throws IOException {
        listFilesOnEmptyDir(true);
    }

    @Test
    public void testListLocatedStatusEmptyDirectory() throws IOException {
        describe(("Invoke listLocatedStatus() on empty directories;" + " expect directories to be found"));
        Path subfolder = createDirWithEmptySubFolder();
        FileSystem fs = getFileSystem();
        new ContractTestUtils.TreeScanResults(fs.listLocatedStatus(getContract().getTestPath())).assertSizeEquals("listLocatedStatus(test dir)", 0, 1, 0);
        describe("Test on empty subdirectory");
        new ContractTestUtils.TreeScanResults(fs.listLocatedStatus(subfolder)).assertSizeEquals("listLocatedStatus(empty subfolder)", 0, 0, 0);
    }

    /**
     * All tests cases against complex directories are aggregated into one, so
     * that the setup and teardown costs against object stores can be shared.
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testComplexDirActions() throws Throwable {
        ContractTestUtils.TreeScanResults tree = createTestTree();
        checkListStatusStatusComplexDir(tree);
        checkListLocatedStatusStatusComplexDir(tree);
        checkListFilesComplexDirNonRecursive(tree);
        checkListFilesComplexDirRecursive(tree);
    }

    @Test
    public void testListFilesNoDir() throws Throwable {
        describe("test the listFiles calls on a path which is not present");
        Path path = path("missing");
        try {
            RemoteIterator<LocatedFileStatus> iterator = getFileSystem().listFiles(path, false);
            Assert.fail(("Expected an exception, got an iterator: " + iterator));
        } catch (FileNotFoundException expected) {
            // expected
        }
        try {
            RemoteIterator<LocatedFileStatus> iterator = getFileSystem().listFiles(path, true);
            Assert.fail(("Expected an exception, got an iterator: " + iterator));
        } catch (FileNotFoundException expected) {
            // expected
        }
    }

    @Test
    public void testLocatedStatusNoDir() throws Throwable {
        describe("test the LocatedStatus call on a path which is not present");
        LambdaTestUtils.intercept(FileNotFoundException.class, () -> getFileSystem().listLocatedStatus(path("missing")));
    }

    @Test
    public void testListStatusNoDir() throws Throwable {
        describe("test the listStatus(path) call on a path which is not present");
        LambdaTestUtils.intercept(FileNotFoundException.class, () -> getFileSystem().listStatus(path("missing")));
    }

    @Test
    public void testListStatusFilteredNoDir() throws Throwable {
        describe("test the listStatus(path, filter) call on a missing path");
        LambdaTestUtils.intercept(FileNotFoundException.class, () -> getFileSystem().listStatus(path("missing"), AbstractContractGetFileStatusTest.ALL_PATHS));
    }

    @Test
    public void testListStatusFilteredFile() throws Throwable {
        describe("test the listStatus(path, filter) on a file");
        Path f = touchf("liststatus");
        Assert.assertEquals(0, getFileSystem().listStatus(f, AbstractContractGetFileStatusTest.NO_PATHS).length);
    }

    @Test
    public void testListStatusFile() throws Throwable {
        describe("test the listStatus(path) on a file");
        Path f = touchf("liststatusfile");
        verifyStatusArrayMatchesFile(f, getFileSystem().listStatus(f));
    }

    @Test
    public void testListFilesFile() throws Throwable {
        describe("test the listStatus(path) on a file");
        Path f = touchf("listfilesfile");
        List<LocatedFileStatus> statusList = ContractTestUtils.toList(getFileSystem().listFiles(f, false));
        Assert.assertEquals("size of file list returned", 1, statusList.size());
        assertIsNamedFile(f, statusList.get(0));
        List<LocatedFileStatus> statusList2 = ContractTestUtils.toListThroughNextCallsAlone(getFileSystem().listFiles(f, false));
        Assert.assertEquals("size of file list returned through next() calls", 1, statusList2.size());
        assertIsNamedFile(f, statusList2.get(0));
    }

    @Test
    public void testListFilesFileRecursive() throws Throwable {
        describe("test the listFiles(path, true) on a file");
        Path f = touchf("listfilesRecursive");
        List<LocatedFileStatus> statusList = ContractTestUtils.toList(getFileSystem().listFiles(f, true));
        Assert.assertEquals("size of file list returned", 1, statusList.size());
        assertIsNamedFile(f, statusList.get(0));
        List<LocatedFileStatus> statusList2 = ContractTestUtils.toListThroughNextCallsAlone(getFileSystem().listFiles(f, true));
        Assert.assertEquals("size of file list returned", 1, statusList2.size());
    }

    @Test
    public void testListLocatedStatusFile() throws Throwable {
        describe("test the listLocatedStatus(path) on a file");
        Path f = touchf("listLocatedStatus");
        List<LocatedFileStatus> statusList = ContractTestUtils.toList(getFileSystem().listLocatedStatus(f));
        Assert.assertEquals("size of file list returned", 1, statusList.size());
        assertIsNamedFile(f, statusList.get(0));
        List<LocatedFileStatus> statusList2 = ContractTestUtils.toListThroughNextCallsAlone(getFileSystem().listLocatedStatus(f));
        Assert.assertEquals("size of file list returned through next() calls", 1, statusList2.size());
    }

    @Test
    public void testListStatusFiltering() throws Throwable {
        describe("Call listStatus() against paths and directories with filtering");
        Path file1 = touchf("file-1.txt");
        touchf("file-2.txt");
        Path parent = file1.getParent();
        FileStatus[] result;
        verifyListStatus(0, parent, AbstractContractGetFileStatusTest.NO_PATHS);
        verifyListStatus(2, parent, AbstractContractGetFileStatusTest.ALL_PATHS);
        AbstractContractGetFileStatusTest.MatchesNameFilter file1Filter = new AbstractContractGetFileStatusTest.MatchesNameFilter("file-1.txt");
        result = verifyListStatus(1, parent, file1Filter);
        Assert.assertEquals(file1, result[0].getPath());
        verifyListStatus(0, file1, AbstractContractGetFileStatusTest.NO_PATHS);
        result = verifyListStatus(1, file1, AbstractContractGetFileStatusTest.ALL_PATHS);
        Assert.assertEquals(file1, result[0].getPath());
        result = verifyListStatus(1, file1, file1Filter);
        Assert.assertEquals(file1, result[0].getPath());
        // empty subdirectory
        Path subdir = path("subdir");
        mkdirs(subdir);
        verifyListStatus(0, subdir, AbstractContractGetFileStatusTest.NO_PATHS);
        verifyListStatus(0, subdir, AbstractContractGetFileStatusTest.ALL_PATHS);
        verifyListStatus(0, subdir, new AbstractContractGetFileStatusTest.MatchesNameFilter("subdir"));
    }

    @Test
    public void testListLocatedStatusFiltering() throws Throwable {
        describe("Call listLocatedStatus() with filtering");
        describe("Call listStatus() against paths and directories with filtering");
        Path file1 = touchf("file-1.txt");
        Path file2 = touchf("file-2.txt");
        Path parent = file1.getParent();
        FileSystem fs = getFileSystem();
        ContractTestUtils.touch(fs, file1);
        ContractTestUtils.touch(fs, file2);
        // this is not closed: ignore any IDE warnings.
        AbstractContractGetFileStatusTest.ExtendedFilterFS xfs = new AbstractContractGetFileStatusTest.ExtendedFilterFS(fs);
        List<LocatedFileStatus> result;
        verifyListStatus(0, parent, AbstractContractGetFileStatusTest.NO_PATHS);
        verifyListStatus(2, parent, AbstractContractGetFileStatusTest.ALL_PATHS);
        AbstractContractGetFileStatusTest.MatchesNameFilter file1Filter = new AbstractContractGetFileStatusTest.MatchesNameFilter("file-1.txt");
        result = verifyListLocatedStatus(xfs, 1, parent, file1Filter);
        Assert.assertEquals(file1, result.get(0).getPath());
        verifyListLocatedStatus(xfs, 0, file1, AbstractContractGetFileStatusTest.NO_PATHS);
        verifyListLocatedStatus(xfs, 1, file1, AbstractContractGetFileStatusTest.ALL_PATHS);
        Assert.assertEquals(file1, result.get(0).getPath());
        verifyListLocatedStatus(xfs, 1, file1, file1Filter);
        Assert.assertEquals(file1, result.get(0).getPath());
        verifyListLocatedStatusNextCalls(xfs, 1, file1, file1Filter);
        // empty subdirectory
        Path subdir = path("subdir");
        mkdirs(subdir);
        verifyListLocatedStatus(xfs, 0, subdir, AbstractContractGetFileStatusTest.NO_PATHS);
        verifyListLocatedStatus(xfs, 0, subdir, AbstractContractGetFileStatusTest.ALL_PATHS);
        verifyListLocatedStatusNextCalls(xfs, 0, subdir, AbstractContractGetFileStatusTest.ALL_PATHS);
        verifyListLocatedStatus(xfs, 0, subdir, new AbstractContractGetFileStatusTest.MatchesNameFilter("subdir"));
    }

    private static final PathFilter ALL_PATHS = new AbstractContractGetFileStatusTest.AllPathsFilter();

    private static final PathFilter NO_PATHS = new AbstractContractGetFileStatusTest.NoPathsFilter();

    /**
     * Accept everything.
     */
    private static final class AllPathsFilter implements PathFilter {
        @Override
        public boolean accept(Path path) {
            return true;
        }
    }

    /**
     * Accept nothing.
     */
    private static final class NoPathsFilter implements PathFilter {
        @Override
        public boolean accept(Path path) {
            return false;
        }
    }

    /**
     * Path filter which only expects paths whose final name element
     * equals the {@code match} field.
     */
    private static final class MatchesNameFilter implements PathFilter {
        private final String match;

        MatchesNameFilter(String match) {
            this.match = match;
        }

        @Override
        public boolean accept(Path path) {
            return match.equals(path.getName());
        }
    }

    /**
     * A filesystem filter which exposes the protected method
     * {@link #listLocatedStatus(Path, PathFilter)}.
     */
    protected static final class ExtendedFilterFS extends FilterFileSystem {
        public ExtendedFilterFS(FileSystem fs) {
            super(fs);
        }

        @Override
        public RemoteIterator<LocatedFileStatus> listLocatedStatus(Path f, PathFilter filter) throws IOException {
            return super.listLocatedStatus(f, filter);
        }
    }
}

