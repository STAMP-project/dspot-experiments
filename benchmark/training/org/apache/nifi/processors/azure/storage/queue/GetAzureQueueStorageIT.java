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
package org.apache.nifi.processors.azure.storage.queue;


import AzureStorageUtils.ACCOUNT_KEY;
import AzureStorageUtils.ACCOUNT_NAME;
import GetAzureQueueStorage.AUTO_DELETE;
import GetAzureQueueStorage.BATCH_SIZE;
import GetAzureQueueStorage.QUEUE;
import GetAzureQueueStorage.REL_SUCCESS;
import GetAzureQueueStorage.VISIBILITY_TIMEOUT;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import java.util.List;
import org.apache.nifi.processors.azure.storage.AzureTestUtil;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class GetAzureQueueStorageIT {
    private final TestRunner runner = TestRunners.newTestRunner(GetAzureQueueStorage.class);

    private static CloudQueue cloudQueue;

    @Test
    public void testGetWithAutoDeleteFalse() throws StorageException, InterruptedException {
        GetAzureQueueStorageIT.cloudQueue.clear();
        GetAzureQueueStorageIT.insertDummyMessages();
        runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
        runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
        runner.setProperty(QUEUE, AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(AUTO_DELETE, "false");
        runner.setProperty(VISIBILITY_TIMEOUT, "1 secs");
        runner.run(1);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertFalse(mockFlowFiles.isEmpty());
        Thread.sleep(1500);
        GetAzureQueueStorageIT.cloudQueue.downloadAttributes();
        Assert.assertEquals(3, GetAzureQueueStorageIT.cloudQueue.getApproximateMessageCount());
    }

    @Test
    public void testGetWithELAndAutoDeleteTrue() throws StorageException, InterruptedException {
        GetAzureQueueStorageIT.cloudQueue.clear();
        GetAzureQueueStorageIT.insertDummyMessages();
        runner.setValidateExpressionUsage(true);
        runner.setVariable("account.name", AzureTestUtil.getAccountName());
        runner.setVariable("account.key", AzureTestUtil.getAccountKey());
        runner.setVariable("queue.name", AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(ACCOUNT_NAME, "${account.name}");
        runner.setProperty(ACCOUNT_KEY, "${account.key}");
        runner.setProperty(QUEUE, "${queue.name}");
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(AUTO_DELETE, "true");
        runner.setProperty(VISIBILITY_TIMEOUT, "1 secs");
        runner.run(1);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertFalse(mockFlowFiles.isEmpty());
        Thread.sleep(1500);
        GetAzureQueueStorageIT.cloudQueue.downloadAttributes();
        Assert.assertEquals(0, GetAzureQueueStorageIT.cloudQueue.getApproximateMessageCount());
    }

    @Test
    public void testGetWithVisibilityTimeout() throws StorageException, InterruptedException {
        GetAzureQueueStorageIT.cloudQueue.clear();
        GetAzureQueueStorageIT.insertDummyMessages();
        runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
        runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
        runner.setProperty(QUEUE, AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(BATCH_SIZE, "10");
        runner.setProperty(AUTO_DELETE, "false");
        runner.setProperty(VISIBILITY_TIMEOUT, "1 secs");
        runner.run(1);
        final List<MockFlowFile> mockFlowFiles = runner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertFalse(mockFlowFiles.isEmpty());
        Assert.assertEquals(0, AzureTestUtil.getQueueCount());
        Thread.sleep(1500);
        Assert.assertEquals(3, AzureTestUtil.getQueueCount());
    }

    @Test
    public void testGetWithBatchSize() throws StorageException {
        GetAzureQueueStorageIT.cloudQueue.clear();
        GetAzureQueueStorageIT.insertDummyMessages();
        runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
        runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
        runner.setProperty(QUEUE, AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(BATCH_SIZE, "2");
        runner.setProperty(AUTO_DELETE, "true");
        runner.setProperty(VISIBILITY_TIMEOUT, "1 secs");
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
    }
}

