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


import AzureServiceErrorCode.AUTHORIZATION_PERMISSION_MISS_MATCH;
import java.io.InputStream;
import java.util.Map;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azurebfs.constants.TestConfigurationKeys;
import org.apache.hadoop.fs.azurebfs.contracts.exceptions.AbfsRestOperationException;
import org.apache.hadoop.fs.azurebfs.services.AuthType;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test Azure Oauth with Blob Data contributor role and Blob Data Reader role.
 * The Test AAD client need to be configured manually through Azure Portal, then save their properties in
 * configuration files.
 */
public class ITestAzureBlobFileSystemOauth extends AbstractAbfsIntegrationTest {
    private static final Path FILE_PATH = new Path("/testFile");

    private static final Path EXISTED_FILE_PATH = new Path("/existedFile");

    private static final Path EXISTED_FOLDER_PATH = new Path("/existedFolder");

    public ITestAzureBlobFileSystemOauth() throws Exception {
        Assume.assumeTrue(((this.getAuthType()) == (AuthType.OAuth)));
    }

    /* BLOB DATA CONTRIBUTOR should have full access to the container and blobs in the container. */
    @Test
    public void testBlobDataContributor() throws Exception {
        String clientId = this.getConfiguration().get(TestConfigurationKeys.FS_AZURE_BLOB_DATA_CONTRIBUTOR_CLIENT_ID);
        Assume.assumeTrue("Contributor client id not provided", (clientId != null));
        String secret = this.getConfiguration().get(TestConfigurationKeys.FS_AZURE_BLOB_DATA_CONTRIBUTOR_CLIENT_SECRET);
        Assume.assumeTrue("Contributor client secret not provided", (secret != null));
        prepareFiles();
        final AzureBlobFileSystem fs = getBlobConributor();
        // create and write into file in current container/fs
        try (FSDataOutputStream stream = fs.create(ITestAzureBlobFileSystemOauth.FILE_PATH)) {
            stream.write(0);
        }
        Assert.assertTrue(fs.exists(ITestAzureBlobFileSystemOauth.FILE_PATH));
        FileStatus fileStatus = fs.getFileStatus(ITestAzureBlobFileSystemOauth.FILE_PATH);
        Assert.assertEquals(1, fileStatus.getLen());
        // delete file
        Assert.assertTrue(fs.delete(ITestAzureBlobFileSystemOauth.FILE_PATH, true));
        Assert.assertFalse(fs.exists(ITestAzureBlobFileSystemOauth.FILE_PATH));
        // Verify Blob Data Contributor has full access to existed folder, file
        // READ FOLDER
        Assert.assertTrue(fs.exists(ITestAzureBlobFileSystemOauth.EXISTED_FOLDER_PATH));
        // DELETE FOLDER
        fs.delete(ITestAzureBlobFileSystemOauth.EXISTED_FOLDER_PATH, true);
        Assert.assertFalse(fs.exists(ITestAzureBlobFileSystemOauth.EXISTED_FOLDER_PATH));
        // READ FILE
        try (FSDataInputStream stream = fs.open(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH)) {
            Assert.assertTrue(((stream.read()) != 0));
        }
        Assert.assertEquals(0, fs.getFileStatus(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH).getLen());
        // WRITE FILE
        try (FSDataOutputStream stream = fs.append(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH)) {
            stream.write(0);
        }
        Assert.assertEquals(1, fs.getFileStatus(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH).getLen());
        // REMOVE FILE
        fs.delete(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH, true);
        Assert.assertFalse(fs.exists(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH));
    }

    /* BLOB DATA READER should have only READ access to the container and blobs in the container. */
    @Test
    public void testBlobDataReader() throws Exception {
        String clientId = this.getConfiguration().get(TestConfigurationKeys.FS_AZURE_BLOB_DATA_READER_CLIENT_ID);
        Assume.assumeTrue("Reader client id not provided", (clientId != null));
        String secret = this.getConfiguration().get(TestConfigurationKeys.FS_AZURE_BLOB_DATA_READER_CLIENT_SECRET);
        Assume.assumeTrue("Reader client secret not provided", (secret != null));
        prepareFiles();
        final AzureBlobFileSystem fs = getBlobReader();
        // Use abfsStore in this test to verify the  ERROR code in AbfsRestOperationException
        AzureBlobFileSystemStore abfsStore = fs.getAbfsStore();
        // TEST READ FS
        Map<String, String> properties = abfsStore.getFilesystemProperties();
        // TEST READ FOLDER
        Assert.assertTrue(fs.exists(ITestAzureBlobFileSystemOauth.EXISTED_FOLDER_PATH));
        // TEST DELETE FOLDER
        try {
            abfsStore.delete(ITestAzureBlobFileSystemOauth.EXISTED_FOLDER_PATH, true);
        } catch (AbfsRestOperationException e) {
            Assert.assertEquals(AUTHORIZATION_PERMISSION_MISS_MATCH, e.getErrorCode());
        }
        // TEST READ  FILE
        try (InputStream inputStream = abfsStore.openFileForRead(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH, null)) {
            Assert.assertTrue(((inputStream.read()) != 0));
        }
        // TEST WRITE FILE
        try {
            abfsStore.openFileForWrite(ITestAzureBlobFileSystemOauth.EXISTED_FILE_PATH, true);
        } catch (AbfsRestOperationException e) {
            Assert.assertEquals(AUTHORIZATION_PERMISSION_MISS_MATCH, e.getErrorCode());
        }
    }
}

