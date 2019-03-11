/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.azure.storage;


import AbstractAzureBlobProcessor.REL_SUCCESS;
import AzureStorageUtils.ACCOUNT_KEY;
import AzureStorageUtils.ACCOUNT_NAME;
import AzureStorageUtils.BLOCK;
import AzureStorageUtils.CONTAINER;
import FetchAzureBlobStorage.BLOB;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class ITFetchAzureBlobStorage {
    @Test
    public void testFetchingBlob() throws StorageException, IOException, URISyntaxException, InvalidKeyException {
        String containerName = String.format("%s-%s", AzureTestUtil.TEST_CONTAINER_NAME_PREFIX, UUID.randomUUID());
        CloudBlobContainer container = AzureTestUtil.getContainer(containerName);
        container.createIfNotExists();
        CloudBlob blob = container.getBlockBlobReference(AzureTestUtil.TEST_BLOB_NAME);
        byte[] buf = "0123456789".getBytes();
        InputStream in = new ByteArrayInputStream(buf);
        blob.upload(in, 10);
        final TestRunner runner = TestRunners.newTestRunner(new FetchAzureBlobStorage());
        try {
            runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
            runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
            runner.setProperty(CONTAINER, containerName);
            runner.setProperty(BLOB, "${azure.blobname}");
            final Map<String, String> attributes = new HashMap<>();
            attributes.put("azure.primaryUri", ((((("https://" + (AzureTestUtil.getAccountName())) + ".blob.core.windows.net/") + containerName) + "/") + (AzureTestUtil.TEST_BLOB_NAME)));
            attributes.put("azure.blobname", AzureTestUtil.TEST_BLOB_NAME);
            attributes.put("azure.blobtype", BLOCK);
            runner.enqueue(new byte[0], attributes);
            runner.run();
            runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            List<MockFlowFile> flowFilesForRelationship = runner.getFlowFilesForRelationship(FetchAzureBlobStorage.REL_SUCCESS);
            for (MockFlowFile flowFile : flowFilesForRelationship) {
                flowFile.assertContentEquals("0123456789".getBytes());
                flowFile.assertAttributeEquals("azure.length", "10");
            }
        } finally {
            container.deleteIfExists();
        }
    }
}

