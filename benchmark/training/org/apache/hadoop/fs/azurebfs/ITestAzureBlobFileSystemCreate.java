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
package org.apache.hadoop.fs.azurebfs;


import CreateFlag.CREATE;
import CreateFlag.OVERWRITE;
import java.io.FileNotFoundException;
import java.util.EnumSet;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test create operation.
 */
public class ITestAzureBlobFileSystemCreate extends AbstractAbfsIntegrationTest {
    private static final Path TEST_FILE_PATH = new Path("testfile");

    private static final Path TEST_FOLDER_PATH = new Path("testFolder");

    private static final String TEST_CHILD_FILE = "childFile";

    public ITestAzureBlobFileSystemCreate() throws Exception {
        super();
    }

    @Test
    public void testEnsureFileCreatedImmediately() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        FSDataOutputStream out = fs.create(ITestAzureBlobFileSystemCreate.TEST_FILE_PATH);
        try {
            assertIsFile(fs, ITestAzureBlobFileSystemCreate.TEST_FILE_PATH);
        } finally {
            out.close();
        }
        assertIsFile(fs, ITestAzureBlobFileSystemCreate.TEST_FILE_PATH);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCreateNonRecursive() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Path testFile = new Path(ITestAzureBlobFileSystemCreate.TEST_FOLDER_PATH, ITestAzureBlobFileSystemCreate.TEST_CHILD_FILE);
        try {
            fs.createNonRecursive(testFile, true, 1024, ((short) (1)), 1024, null);
            Assert.fail("Should've thrown");
        } catch (FileNotFoundException expected) {
        }
        fs.mkdirs(ITestAzureBlobFileSystemCreate.TEST_FOLDER_PATH);
        fs.createNonRecursive(testFile, true, 1024, ((short) (1)), 1024, null).close();
        assertIsFile(fs, testFile);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCreateNonRecursive1() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Path testFile = new Path(ITestAzureBlobFileSystemCreate.TEST_FOLDER_PATH, ITestAzureBlobFileSystemCreate.TEST_CHILD_FILE);
        try {
            fs.createNonRecursive(testFile, FsPermission.getDefault(), EnumSet.of(CREATE, OVERWRITE), 1024, ((short) (1)), 1024, null);
            Assert.fail("Should've thrown");
        } catch (FileNotFoundException expected) {
        }
        fs.mkdirs(ITestAzureBlobFileSystemCreate.TEST_FOLDER_PATH);
        fs.createNonRecursive(testFile, true, 1024, ((short) (1)), 1024, null).close();
        assertIsFile(fs, testFile);
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testCreateNonRecursive2() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        Path testFile = new Path(ITestAzureBlobFileSystemCreate.TEST_FOLDER_PATH, ITestAzureBlobFileSystemCreate.TEST_CHILD_FILE);
        try {
            fs.createNonRecursive(testFile, FsPermission.getDefault(), false, 1024, ((short) (1)), 1024, null);
            Assert.fail("Should've thrown");
        } catch (FileNotFoundException e) {
        }
        fs.mkdirs(ITestAzureBlobFileSystemCreate.TEST_FOLDER_PATH);
        fs.createNonRecursive(testFile, true, 1024, ((short) (1)), 1024, null).close();
        assertIsFile(fs, testFile);
    }
}

