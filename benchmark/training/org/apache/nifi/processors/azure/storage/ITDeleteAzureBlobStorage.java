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


import AzureStorageUtils.ACCOUNT_KEY;
import AzureStorageUtils.ACCOUNT_NAME;
import AzureStorageUtils.CONTAINER;
import DeleteAzureBlobStorage.BLOB;
import DeleteAzureBlobStorage.REL_SUCCESS;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.UUID;
import org.apache.nifi.processors.azure.AbstractAzureBlobStorageIT;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class ITDeleteAzureBlobStorage extends AbstractAzureBlobStorageIT {
    @Test
    public void testDeleteBlob() throws StorageException, IOException, URISyntaxException, InvalidKeyException {
        String containerName = String.format("%s-%s", AzureTestUtil.TEST_CONTAINER_NAME_PREFIX, UUID.randomUUID());
        CloudBlobContainer container = AzureTestUtil.getContainer(containerName);
        container.createIfNotExists();
        uploadBlob(containerName, getFileFromResource(AbstractAzureBlobStorageIT.SAMPLE_FILE_NAME));
        final TestRunner runner = TestRunners.newTestRunner(DeleteAzureBlobStorage.class);
        try {
            runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
            runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
            runner.setProperty(CONTAINER, containerName);
            runner.setProperty(BLOB, AzureTestUtil.TEST_BLOB_NAME);
            runner.enqueue(new byte[0]);
            runner.run(1);
            runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        } finally {
            container.deleteIfExists();
        }
    }
}

