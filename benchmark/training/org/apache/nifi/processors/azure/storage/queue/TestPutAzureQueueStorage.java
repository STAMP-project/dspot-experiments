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
import GetAzureQueueStorage.QUEUE;
import PutAzureQueueStorage.TTL;
import PutAzureQueueStorage.VISIBILITY_DELAY;
import com.microsoft.azure.storage.StorageException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestPutAzureQueueStorage {
    private final TestRunner runner = TestRunners.newTestRunner(PutAzureQueueStorage.class);

    @Test
    public void testInvalidTTLAndVisibilityDelay() throws StorageException, URISyntaxException, InvalidKeyException {
        runner.setProperty(ACCOUNT_NAME, "dummy-storage");
        runner.setProperty(ACCOUNT_KEY, "dummy-key");
        runner.setProperty(QUEUE, "dummyqueue");
        runner.setProperty(TTL, "8 days");
        runner.setProperty(VISIBILITY_DELAY, "9 days");
        ProcessContext processContext = runner.getProcessContext();
        Collection<ValidationResult> results = new HashSet<>();
        if (processContext instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(2, results.size());
        Iterator<ValidationResult> iterator = results.iterator();
        Assert.assertTrue(iterator.next().toString().contains(((TTL.getDisplayName()) + " exceeds the allowed limit of 7 days. Set a value less than 7 days")));
        Assert.assertTrue(iterator.next().toString().contains(((VISIBILITY_DELAY.getDisplayName()) + " should be greater than or equal to 0 and less than")));
    }

    @Test
    public void testAllValidProperties() {
        runner.setProperty(ACCOUNT_NAME, "dummy-storage");
        runner.setProperty(ACCOUNT_KEY, "dummy-key");
        runner.setProperty(QUEUE, "dummyqueue");
        runner.setProperty(TTL, "6 days");
        runner.setProperty(VISIBILITY_DELAY, "5 days");
        ProcessContext processContext = runner.getProcessContext();
        Collection<ValidationResult> results = new HashSet<>();
        if (processContext instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
    }
}

