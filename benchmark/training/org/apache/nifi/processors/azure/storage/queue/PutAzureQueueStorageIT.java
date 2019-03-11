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
import PutAzureQueueStorage.QUEUE;
import PutAzureQueueStorage.REL_SUCCESS;
import PutAzureQueueStorage.TTL;
import PutAzureQueueStorage.VISIBILITY_DELAY;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.queue.CloudQueue;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import org.apache.nifi.processors.azure.storage.AzureTestUtil;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class PutAzureQueueStorageIT {
    private final TestRunner runner = TestRunners.newTestRunner(PutAzureQueueStorage.class);

    private static CloudQueue cloudQueue;

    @Test
    public void testSimplePut() throws StorageException, URISyntaxException, InvalidKeyException {
        runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
        runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
        runner.setProperty(QUEUE, AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.enqueue("Dummy message");
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testSimplePutWithEL() throws StorageException, URISyntaxException, InvalidKeyException {
        runner.setValidateExpressionUsage(true);
        runner.setVariable("account.name", AzureTestUtil.getAccountName());
        runner.setVariable("account.key", AzureTestUtil.getAccountKey());
        runner.setVariable("queue.name", AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(ACCOUNT_NAME, "${account.name}");
        runner.setProperty(ACCOUNT_KEY, "${account.key}");
        runner.setProperty(QUEUE, "${queue.name}");
        runner.enqueue("Dummy message");
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testPutWithTTL() throws StorageException, InterruptedException {
        PutAzureQueueStorageIT.cloudQueue.clear();
        runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
        runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
        runner.setProperty(QUEUE, AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(TTL, "2 secs");
        runner.enqueue("Dummy message");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(1, AzureTestUtil.getQueueCount());
        Thread.sleep(2400);
        Assert.assertEquals(0, AzureTestUtil.getQueueCount());
    }

    @Test
    public void testPutWithVisibilityDelay() throws StorageException, InterruptedException {
        PutAzureQueueStorageIT.cloudQueue.clear();
        PutAzureQueueStorageIT.cloudQueue.clear();
        runner.setProperty(ACCOUNT_NAME, AzureTestUtil.getAccountName());
        runner.setProperty(ACCOUNT_KEY, AzureTestUtil.getAccountKey());
        runner.setProperty(QUEUE, AzureTestUtil.TEST_STORAGE_QUEUE);
        runner.setProperty(VISIBILITY_DELAY, "2 secs");
        runner.enqueue("Dummy message");
        runner.run(1);
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(0, AzureTestUtil.getQueueCount());
        Thread.sleep(2400);
        Assert.assertEquals(1, AzureTestUtil.getQueueCount());
    }
}

