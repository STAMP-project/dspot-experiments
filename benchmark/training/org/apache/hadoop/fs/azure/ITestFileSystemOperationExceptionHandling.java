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
package org.apache.hadoop.fs.azure;


import java.io.FileNotFoundException;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsAction;
import org.junit.Assert;
import org.junit.Test;


/**
 * Single threaded exception handling.
 */
public class ITestFileSystemOperationExceptionHandling extends AbstractWasbTestBase {
    private FSDataInputStream inputStream = null;

    private Path testPath;

    private Path testFolderPath;

    /**
     * Tests a basic single threaded read scenario for Page blobs.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedPageBlobReadScenario() throws Throwable {
        AzureBlobStorageTestAccount testAccount = ExceptionHandlingTestHelper.getPageBlobTestStorageAccount();
        setupInputStreamToTest(testAccount);
        byte[] readBuffer = new byte[512];
        inputStream.read(readBuffer);
    }

    /**
     * Tests a basic single threaded seek scenario for Page blobs.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedPageBlobSeekScenario() throws Throwable {
        AzureBlobStorageTestAccount testAccount = ExceptionHandlingTestHelper.getPageBlobTestStorageAccount();
        setupInputStreamToTest(testAccount);
        inputStream.seek(5);
    }

    /**
     * Test a basic single thread seek scenario for Block blobs.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadBlockBlobSeekScenario() throws Throwable {
        AzureBlobStorageTestAccount testAccount = createTestAccount();
        setupInputStreamToTest(testAccount);
        inputStream.seek(5);
        inputStream.read();
    }

    /**
     * Tests a basic single threaded read scenario for Block blobs.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingledThreadBlockBlobReadScenario() throws Throwable {
        AzureBlobStorageTestAccount testAccount = createTestAccount();
        setupInputStreamToTest(testAccount);
        byte[] readBuffer = new byte[512];
        inputStream.read(readBuffer);
    }

    /**
     * Tests basic single threaded setPermission scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedBlockBlobSetPermissionScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(createTestAccount(), testPath);
        fs.delete(testPath, true);
        fs.setPermission(testPath, new org.apache.hadoop.fs.permission.FsPermission(FsAction.EXECUTE, FsAction.READ, FsAction.READ));
    }

    /**
     * Tests basic single threaded setPermission scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedPageBlobSetPermissionScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(ExceptionHandlingTestHelper.getPageBlobTestStorageAccount(), testPath);
        fs.delete(testPath, true);
        fs.setOwner(testPath, "testowner", "testgroup");
    }

    /**
     * Tests basic single threaded setPermission scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedBlockBlobSetOwnerScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(createTestAccount(), testPath);
        fs.delete(testPath, true);
        fs.setOwner(testPath, "testowner", "testgroup");
    }

    /**
     * Tests basic single threaded setPermission scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedPageBlobSetOwnerScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(ExceptionHandlingTestHelper.getPageBlobTestStorageAccount(), testPath);
        fs.delete(testPath, true);
        fs.setPermission(testPath, new org.apache.hadoop.fs.permission.FsPermission(FsAction.EXECUTE, FsAction.READ, FsAction.READ));
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedBlockBlobListStatusScenario() throws Throwable {
        ExceptionHandlingTestHelper.createTestFolder(createTestAccount(), testFolderPath);
        fs.delete(testFolderPath, true);
        fs.listStatus(testFolderPath);
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedPageBlobListStatusScenario() throws Throwable {
        ExceptionHandlingTestHelper.createTestFolder(ExceptionHandlingTestHelper.getPageBlobTestStorageAccount(), testFolderPath);
        fs.delete(testFolderPath, true);
        fs.listStatus(testFolderPath);
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test
    public void testSingleThreadedBlockBlobRenameScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(createTestAccount(), testPath);
        Path dstPath = new Path("dstFile.dat");
        fs.delete(testPath, true);
        boolean renameResult = fs.rename(testPath, dstPath);
        Assert.assertFalse(renameResult);
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test
    public void testSingleThreadedPageBlobRenameScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(ExceptionHandlingTestHelper.getPageBlobTestStorageAccount(), testPath);
        Path dstPath = new Path("dstFile.dat");
        fs.delete(testPath, true);
        boolean renameResult = fs.rename(testPath, dstPath);
        Assert.assertFalse(renameResult);
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test
    public void testSingleThreadedBlockBlobDeleteScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(createTestAccount(), testPath);
        fs.delete(testPath, true);
        boolean deleteResult = fs.delete(testPath, true);
        Assert.assertFalse(deleteResult);
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test
    public void testSingleThreadedPageBlobDeleteScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(ExceptionHandlingTestHelper.getPageBlobTestStorageAccount(), testPath);
        fs.delete(testPath, true);
        boolean deleteResult = fs.delete(testPath, true);
        Assert.assertFalse(deleteResult);
    }

    /**
     * Test basic single threaded listStatus scenario.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedBlockBlobOpenScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(createTestAccount(), testPath);
        fs.delete(testPath, true);
        inputStream = fs.open(testPath);
    }

    /**
     * Test delete then open a file.
     */
    @Test(expected = FileNotFoundException.class)
    public void testSingleThreadedPageBlobOpenScenario() throws Throwable {
        ExceptionHandlingTestHelper.createEmptyFile(ExceptionHandlingTestHelper.getPageBlobTestStorageAccount(), testPath);
        fs.delete(testPath, true);
        inputStream = fs.open(testPath);
    }
}

