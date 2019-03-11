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


import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test creating files, overwrite options etc.
 */
public abstract class AbstractContractCreateTest extends AbstractFSContractTestBase {
    /**
     * How long to wait for a path to become visible.
     */
    public static final int CREATE_TIMEOUT = 15000;

    @Test
    public void testCreateNewFile() throws Throwable {
        testCreateNewFile(true);
        testCreateNewFile(false);
    }

    @Test
    public void testCreateFileOverExistingFileNoOverwrite() throws Throwable {
        testCreateFileOverExistingFileNoOverwrite(false);
        testCreateFileOverExistingFileNoOverwrite(true);
    }

    /**
     * This test catches some eventual consistency problems that blobstores exhibit,
     * as we are implicitly verifying that updates are consistent. This
     * is why different file lengths and datasets are used
     *
     * @throws Throwable
     * 		
     */
    @Test
    public void testOverwriteExistingFile() throws Throwable {
        testOverwriteExistingFile(false);
        testOverwriteExistingFile(true);
    }

    @Test
    public void testOverwriteEmptyDirectory() throws Throwable {
        testOverwriteEmptyDirectory(false);
        testOverwriteEmptyDirectory(true);
    }

    @Test
    public void testOverwriteNonEmptyDirectory() throws Throwable {
        testOverwriteNonEmptyDirectory(false);
        testOverwriteNonEmptyDirectory(true);
    }

    @Test
    public void testCreatedFileIsImmediatelyVisible() throws Throwable {
        describe("verify that a newly created file exists as soon as open returns");
        Path path = path("testCreatedFileIsImmediatelyVisible");
        try (FSDataOutputStream out = getFileSystem().create(path, false, 4096, ((short) (1)), 1024)) {
            if (!(getFileSystem().exists(path))) {
                if (isSupported(ContractOptions.CREATE_VISIBILITY_DELAYED)) {
                    // For some file systems, downgrade to a skip so that the failure is
                    // visible in test results.
                    ContractTestUtils.skip("This Filesystem delays visibility of newly created files");
                }
                assertPathExists("expected path to be visible before anything written", path);
            }
        }
    }

    @Test
    public void testCreatedFileIsVisibleOnFlush() throws Throwable {
        describe(("verify that a newly created file exists once a flush has taken " + "place"));
        Path path = path("testCreatedFileIsVisibleOnFlush");
        FileSystem fs = getFileSystem();
        try (FSDataOutputStream out = fs.create(path, false, 4096, ((short) (1)), 1024)) {
            out.write('a');
            out.flush();
            if (!(fs.exists(path))) {
                if ((isSupported(ContractOptions.IS_BLOBSTORE)) || (isSupported(ContractOptions.CREATE_VISIBILITY_DELAYED))) {
                    // object store or some file systems: downgrade to a skip so that the
                    // failure is visible in test results
                    ContractTestUtils.skip(("For object store or some file systems, newly created files are" + " not immediately visible"));
                }
                assertPathExists("expected path to be visible before file closed", path);
            }
        }
    }

    @Test
    public void testCreatedFileIsEventuallyVisible() throws Throwable {
        describe("verify a written to file is visible after the stream is closed");
        Path path = path("testCreatedFileIsEventuallyVisible");
        FileSystem fs = getFileSystem();
        try (FSDataOutputStream out = fs.create(path, false, 4096, ((short) (1)), 1024)) {
            out.write(1);
            out.close();
            ContractTestUtils.getFileStatusEventually(fs, path, AbstractContractCreateTest.CREATE_TIMEOUT);
        }
    }

    @Test
    public void testFileStatusBlocksizeNonEmptyFile() throws Throwable {
        describe("validate the block size of a filesystem and files within it");
        FileSystem fs = getFileSystem();
        long rootPath = fs.getDefaultBlockSize(path("/"));
        Assert.assertTrue(("Root block size is invalid " + rootPath), (rootPath > 0));
        Path path = path("testFileStatusBlocksizeNonEmptyFile");
        byte[] data = ContractTestUtils.dataset(256, 'a', 'z');
        ContractTestUtils.writeDataset(fs, path, data, data.length, (1024 * 1024), false);
        validateBlockSize(fs, path, 1);
    }

    @Test
    public void testFileStatusBlocksizeEmptyFile() throws Throwable {
        describe("check that an empty file may return a 0-byte blocksize");
        FileSystem fs = getFileSystem();
        Path path = path("testFileStatusBlocksizeEmptyFile");
        ContractTestUtils.touch(fs, path);
        validateBlockSize(fs, path, 0);
    }

    @Test
    public void testCreateMakesParentDirs() throws Throwable {
        describe("check that after creating a file its parent directories exist");
        FileSystem fs = getFileSystem();
        Path grandparent = path("testCreateCreatesAndPopulatesParents");
        Path parent = new Path(grandparent, "parent");
        Path child = new Path(parent, "child");
        ContractTestUtils.touch(fs, child);
        Assert.assertEquals("List status of parent should include the 1 child file", 1, fs.listStatus(parent).length);
        Assert.assertTrue("Parent directory does not appear to be a directory", fs.getFileStatus(parent).isDirectory());
        Assert.assertEquals("List status of grandparent should include the 1 parent dir", 1, fs.listStatus(grandparent).length);
        Assert.assertTrue("Grandparent directory does not appear to be a directory", fs.getFileStatus(grandparent).isDirectory());
    }
}

