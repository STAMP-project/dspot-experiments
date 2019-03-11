/**
 * Copyright 2019 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.cloud.azure;


import com.github.ambry.commons.BlobId;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import java.io.InputStream;
import java.security.InvalidKeyException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Test cases for {@link AzureCloudDestination}
 */
@RunWith(MockitoJUnitRunner.class)
public class AzureCloudDestinationTest {
    private String configSpec = "AccountName=ambry;AccountKey=ambry-kay";

    private AzureCloudDestination azureDest;

    private CloudStorageAccount mockAzureAccount;

    private CloudBlobClient mockAzureClient;

    private CloudBlobContainer mockAzureContainer;

    private CloudBlockBlob mockBlob;

    private int blobSize = 1024;

    private BlobId blobId;

    /**
     * Test normal upload.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUpload() throws Exception {
        InputStream inputStream = AzureCloudDestinationTest.getBlobInputStream(blobSize);
        Assert.assertTrue("Expected upload to return true", azureDest.uploadBlob(blobId, blobSize, inputStream));
    }

    /**
     * Test normal delete.
     */
    @Test
    public void testDelete() throws Exception {
        Mockito.when(mockBlob.exists()).thenReturn(true);
        Assert.assertTrue("Expected deletion to return true", azureDest.deleteBlob(blobId));
    }

    /**
     * Test upload of existing blob.
     */
    @Test
    public void testUploadExists() throws Exception {
        Mockito.when(mockBlob.exists()).thenReturn(true);
        InputStream inputStream = AzureCloudDestinationTest.getBlobInputStream(blobSize);
        Assert.assertFalse("Upload on existing blob should return false", azureDest.uploadBlob(blobId, blobSize, inputStream));
    }

    /**
     * Test delete of nonexistent blob.
     */
    @Test
    public void testDeleteNotExists() throws Exception {
        Mockito.when(mockBlob.exists()).thenReturn(false);
        Assert.assertFalse("Delete of nonexistent blob should return false", azureDest.deleteBlob(blobId));
    }

    /**
     * Test blob existence check.
     */
    @Test
    public void testExistenceCheck() throws Exception {
        Mockito.when(mockBlob.exists()).thenReturn(true);
        Assert.assertTrue("Expected doesBlobExist to return true", azureDest.doesBlobExist(blobId));
        Mockito.when(mockBlob.exists()).thenReturn(false);
        Assert.assertFalse("Expected doesBlobExist to return false", azureDest.doesBlobExist(blobId));
    }

    /**
     * Test constructor with invalid config spec.
     */
    @Test(expected = InvalidKeyException.class)
    public void testInitClientException() throws Exception {
        azureDest = new AzureCloudDestination(configSpec);
    }

    /**
     * Test upload when client throws exception.
     */
    @Test
    public void testUploadContainerReferenceException() throws Exception {
        Mockito.when(mockAzureClient.getContainerReference(ArgumentMatchers.anyString())).thenThrow(StorageException.class);
        InputStream inputStream = AzureCloudDestinationTest.getBlobInputStream(blobSize);
        expectCloudStorageException(() -> azureDest.uploadBlob(blobId, blobSize, inputStream), StorageException.class);
    }

    /**
     * Test upload when container throws exception.
     */
    @Test
    public void testUploadContainerException() throws Exception {
        Mockito.when(mockAzureContainer.getBlockBlobReference(ArgumentMatchers.anyString())).thenThrow(StorageException.class);
        InputStream inputStream = AzureCloudDestinationTest.getBlobInputStream(blobSize);
        expectCloudStorageException(() -> azureDest.uploadBlob(blobId, blobSize, inputStream), StorageException.class);
    }

    /**
     * Test upload when blob throws exception.
     */
    @Test
    public void testUploadBlobException() throws Exception {
        Mockito.doThrow(StorageException.class).when(mockBlob).upload(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        InputStream inputStream = AzureCloudDestinationTest.getBlobInputStream(blobSize);
        expectCloudStorageException(() -> azureDest.uploadBlob(blobId, blobSize, inputStream), StorageException.class);
    }

    /**
     * Test delete when blob throws exception.
     */
    @Test
    public void testDeleteBlobException() throws Exception {
        Mockito.when(mockBlob.exists()).thenReturn(true);
        Mockito.doThrow(StorageException.class).when(mockBlob).delete();
        expectCloudStorageException(() -> azureDest.deleteBlob(blobId), StorageException.class);
    }
}

