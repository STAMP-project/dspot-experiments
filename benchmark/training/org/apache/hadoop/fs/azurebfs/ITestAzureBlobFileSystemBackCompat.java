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


import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


/**
 * Test AzureBlobFileSystem back compatibility with WASB.
 */
public class ITestAzureBlobFileSystemBackCompat extends AbstractAbfsIntegrationTest {
    public ITestAzureBlobFileSystemBackCompat() throws Exception {
        super();
    }

    @Test
    public void testBlobBackCompat() throws Exception {
        final AzureBlobFileSystem fs = this.getFileSystem();
        Assume.assumeFalse("This test does not support namespace enabled account", this.getFileSystem().getIsNamespaceEnabled());
        String storageConnectionString = getBlobConnectionString();
        CloudStorageAccount storageAccount = CloudStorageAccount.parse(storageConnectionString);
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer container = blobClient.getContainerReference(this.getFileSystemName());
        container.createIfNotExists();
        CloudBlockBlob blockBlob = container.getBlockBlobReference("test/10/10/10");
        blockBlob.uploadText("");
        blockBlob = container.getBlockBlobReference("test/10/123/3/2/1/3");
        blockBlob.uploadText("");
        FileStatus[] fileStatuses = fs.listStatus(new Path("/test/10/"));
        Assert.assertEquals(2, fileStatuses.length);
        Assert.assertEquals("10", fileStatuses[0].getPath().getName());
        Assert.assertTrue(fileStatuses[0].isDirectory());
        Assert.assertEquals(0, fileStatuses[0].getLen());
        Assert.assertEquals("123", fileStatuses[1].getPath().getName());
        Assert.assertTrue(fileStatuses[1].isDirectory());
        Assert.assertEquals(0, fileStatuses[1].getLen());
    }
}

