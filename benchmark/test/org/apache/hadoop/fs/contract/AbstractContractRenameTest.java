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
import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test creating files, overwrite options &c
 */
public abstract class AbstractContractRenameTest extends AbstractFSContractTestBase {
    @Test
    public void testRenameNewFileSameDir() throws Throwable {
        describe("rename a file into a new file in the same directory");
        Path renameSrc = path("rename_src");
        Path renameTarget = path("rename_dest");
        byte[] data = ContractTestUtils.dataset(256, 'a', 'z');
        ContractTestUtils.writeDataset(getFileSystem(), renameSrc, data, data.length, (1024 * 1024), false);
        boolean rename = rename(renameSrc, renameTarget);
        Assert.assertTrue((((("rename(" + renameSrc) + ", ") + renameTarget) + ") returned false"), rename);
        ContractTestUtils.assertListStatusFinds(getFileSystem(), renameTarget.getParent(), renameTarget);
        ContractTestUtils.verifyFileContents(getFileSystem(), renameTarget, data);
    }

    @Test
    public void testRenameNonexistentFile() throws Throwable {
        describe("rename a file into a new file in the same directory");
        Path missing = path("testRenameNonexistentFileSrc");
        Path target = path("testRenameNonexistentFileDest");
        boolean renameReturnsFalseOnFailure = isSupported(ContractOptions.RENAME_RETURNS_FALSE_IF_SOURCE_MISSING);
        mkdirs(missing.getParent());
        try {
            boolean renamed = rename(missing, target);
            // expected an exception
            if (!renameReturnsFalseOnFailure) {
                String destDirLS = generateAndLogErrorListing(missing, target);
                Assert.fail((((((((("expected rename(" + missing) + ", ") + target) + " ) to fail,") + " got a result of ") + renamed) + " and a destination directory of ") + destDirLS));
            } else {
                // at least one FS only returns false here, if that is the case
                // warn but continue
                AbstractFSContractTestBase.getLogger().warn("Rename returned {} renaming a nonexistent file", renamed);
                Assert.assertFalse("Renaming a missing file returned true", renamed);
            }
        } catch (FileNotFoundException e) {
            if (renameReturnsFalseOnFailure) {
                ContractTestUtils.fail("Renaming a missing file unexpectedly threw an exception", e);
            }
            handleExpectedException(e);
        } catch (IOException e) {
            handleRelaxedException("rename nonexistent file", "FileNotFoundException", e);
        }
        assertPathDoesNotExist("rename nonexistent file created a destination file", target);
    }

    /**
     * Rename test -handles filesystems that will overwrite the destination
     * as well as those that do not (i.e. HDFS).
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testRenameFileOverExistingFile() throws Throwable {
        describe("Verify renaming a file onto an existing file matches expectations");
        Path srcFile = path("source-256.txt");
        byte[] srcData = ContractTestUtils.dataset(256, 'a', 'z');
        ContractTestUtils.writeDataset(getFileSystem(), srcFile, srcData, srcData.length, 1024, false);
        Path destFile = path("dest-512.txt");
        byte[] destData = ContractTestUtils.dataset(512, 'A', 'Z');
        ContractTestUtils.writeDataset(getFileSystem(), destFile, destData, destData.length, 1024, false);
        assertIsFile(destFile);
        boolean renameOverwritesDest = isSupported(ContractOptions.RENAME_OVERWRITES_DEST);
        boolean renameReturnsFalseOnRenameDestExists = !(isSupported(ContractOptions.RENAME_RETURNS_FALSE_IF_DEST_EXISTS));
        boolean destUnchanged = true;
        try {
            boolean renamed = rename(srcFile, destFile);
            if (renameOverwritesDest) {
                // the filesystem supports rename(file, file2) by overwriting file2
                Assert.assertTrue("Rename returned false", renamed);
                destUnchanged = false;
            } else {
                // rename is rejected by returning 'false' or throwing an exception
                if (renamed && (!renameReturnsFalseOnRenameDestExists)) {
                    // expected an exception
                    String destDirLS = generateAndLogErrorListing(srcFile, destFile);
                    AbstractFSContractTestBase.getLogger().error("dest dir {}", destDirLS);
                    Assert.fail((((((("expected rename(" + srcFile) + ", ") + destFile) + " ) to fail,") + " but got success and destination of ") + destDirLS));
                }
            }
        } catch (FileAlreadyExistsException e) {
            handleExpectedException(e);
        }
        // verify that the destination file is as expected based on the expected
        // outcome
        ContractTestUtils.verifyFileContents(getFileSystem(), destFile, (destUnchanged ? destData : srcData));
    }

    @Test
    public void testRenameDirIntoExistingDir() throws Throwable {
        describe(("Verify renaming a dir into an existing dir puts it underneath" + " and leaves existing files alone"));
        FileSystem fs = getFileSystem();
        String sourceSubdir = "source";
        Path srcDir = path(sourceSubdir);
        Path srcFilePath = new Path(srcDir, "source-256.txt");
        byte[] srcDataset = ContractTestUtils.dataset(256, 'a', 'z');
        ContractTestUtils.writeDataset(fs, srcFilePath, srcDataset, srcDataset.length, 1024, false);
        Path destDir = path("dest");
        Path destFilePath = new Path(destDir, "dest-512.txt");
        byte[] destDateset = ContractTestUtils.dataset(512, 'A', 'Z');
        ContractTestUtils.writeDataset(fs, destFilePath, destDateset, destDateset.length, 1024, false);
        assertIsFile(destFilePath);
        boolean rename = rename(srcDir, destDir);
        Path renamedSrc = new Path(destDir, sourceSubdir);
        assertIsFile(destFilePath);
        assertIsDirectory(renamedSrc);
        ContractTestUtils.verifyFileContents(fs, destFilePath, destDateset);
        Assert.assertTrue("rename returned false though the contents were copied", rename);
    }

    @Test
    public void testRenameFileNonexistentDir() throws Throwable {
        describe("rename a file into a new file in the same directory");
        Path renameSrc = path("testRenameSrc");
        Path renameTarget = path("subdir/testRenameTarget");
        byte[] data = ContractTestUtils.dataset(256, 'a', 'z');
        ContractTestUtils.writeDataset(getFileSystem(), renameSrc, data, data.length, (1024 * 1024), false);
        boolean renameCreatesDestDirs = isSupported(ContractOptions.RENAME_CREATES_DEST_DIRS);
        try {
            boolean rename = rename(renameSrc, renameTarget);
            if (renameCreatesDestDirs) {
                Assert.assertTrue(rename);
                ContractTestUtils.verifyFileContents(getFileSystem(), renameTarget, data);
            } else {
                Assert.assertFalse(rename);
                ContractTestUtils.verifyFileContents(getFileSystem(), renameSrc, data);
            }
        } catch (FileNotFoundException e) {
            // allowed unless that rename flag is set
            Assert.assertFalse(renameCreatesDestDirs);
        }
    }

    @Test
    public void testRenameWithNonEmptySubDir() throws Throwable {
        final Path renameTestDir = path("testRenameWithNonEmptySubDir");
        final Path srcDir = new Path(renameTestDir, "src1");
        final Path srcSubDir = new Path(srcDir, "sub");
        final Path finalDir = new Path(renameTestDir, "dest");
        FileSystem fs = getFileSystem();
        boolean renameRemoveEmptyDest = isSupported(ContractOptions.RENAME_REMOVE_DEST_IF_EMPTY_DIR);
        ContractTestUtils.rm(fs, renameTestDir, true, false);
        fs.mkdirs(srcDir);
        fs.mkdirs(finalDir);
        ContractTestUtils.writeTextFile(fs, new Path(srcDir, "source.txt"), "this is the file in src dir", false);
        ContractTestUtils.writeTextFile(fs, new Path(srcSubDir, "subfile.txt"), "this is the file in src/sub dir", false);
        assertPathExists("not created in src dir", new Path(srcDir, "source.txt"));
        assertPathExists("not created in src/sub dir", new Path(srcSubDir, "subfile.txt"));
        fs.rename(srcDir, finalDir);
        // Accept both POSIX rename behavior and CLI rename behavior
        if (renameRemoveEmptyDest) {
            // POSIX rename behavior
            assertPathExists("not renamed into dest dir", new Path(finalDir, "source.txt"));
            assertPathExists("not renamed into dest/sub dir", new Path(finalDir, "sub/subfile.txt"));
        } else {
            // CLI rename behavior
            assertPathExists("not renamed into dest dir", new Path(finalDir, "src1/source.txt"));
            assertPathExists("not renamed into dest/sub dir", new Path(finalDir, "src1/sub/subfile.txt"));
        }
        assertPathDoesNotExist("not deleted", new Path(srcDir, "source.txt"));
    }

    /**
     * Test that after renaming, the nested subdirectory is moved along with all
     * its ancestors.
     */
    @Test
    public void testRenamePopulatesDirectoryAncestors() throws IOException {
        final FileSystem fs = getFileSystem();
        final Path src = path("testRenamePopulatesDirectoryAncestors/source");
        fs.mkdirs(src);
        final String nestedDir = "/dir1/dir2/dir3/dir4";
        fs.mkdirs(path((src + nestedDir)));
        Path dst = path("testRenamePopulatesDirectoryAncestorsNew");
        fs.rename(src, dst);
        validateAncestorsMoved(src, dst, nestedDir);
    }

    /**
     * Test that after renaming, the nested file is moved along with all its
     * ancestors. It is similar to {@link #testRenamePopulatesDirectoryAncestors}.
     */
    @Test
    public void testRenamePopulatesFileAncestors() throws IOException {
        final FileSystem fs = getFileSystem();
        final Path src = path("testRenamePopulatesFileAncestors/source");
        fs.mkdirs(src);
        final String nestedFile = "/dir1/dir2/dir3/file4";
        byte[] srcDataset = ContractTestUtils.dataset(256, 'a', 'z');
        ContractTestUtils.writeDataset(fs, path((src + nestedFile)), srcDataset, srcDataset.length, 1024, false);
        Path dst = path("testRenamePopulatesFileAncestorsNew");
        fs.rename(src, dst);
        validateAncestorsMoved(src, dst, nestedFile);
    }
}

