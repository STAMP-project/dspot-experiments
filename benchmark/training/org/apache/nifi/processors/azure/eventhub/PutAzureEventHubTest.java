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
package org.apache.nifi.processors.azure.eventhub;


import PutAzureEventHub.ACCESS_POLICY;
import PutAzureEventHub.EVENT_HUB_NAME;
import PutAzureEventHub.NAMESPACE;
import PutAzureEventHub.POLICY_PRIMARY_KEY;
import PutAzureEventHub.REL_FAILURE;
import PutAzureEventHub.REL_SUCCESS;
import com.microsoft.azure.eventhubs.EventHubClient;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class PutAzureEventHubTest {
    private static final String namespaceName = "nifi-azure-hub";

    private static final String eventHubName = "get-test";

    private static final String sasKeyName = "bogus-policy";

    private static final String sasKey = "9rHmHqxoOVWOb8wS09dvqXYxnNiLqxNMCbmt6qMaQyU!";

    private TestRunner testRunner;

    private PutAzureEventHubTest.MockPutAzureEventHub processor;

    @Test
    public void testProcessorConfigValidity() {
        testRunner.setProperty(EVENT_HUB_NAME, PutAzureEventHubTest.eventHubName);
        testRunner.assertNotValid();
        testRunner.setProperty(NAMESPACE, PutAzureEventHubTest.namespaceName);
        testRunner.assertNotValid();
        testRunner.setProperty(ACCESS_POLICY, PutAzureEventHubTest.sasKeyName);
        testRunner.assertNotValid();
        testRunner.setProperty(POLICY_PRIMARY_KEY, PutAzureEventHubTest.sasKey);
        testRunner.assertValid();
    }

    @Test
    public void verifyRelationships() {
        assert 2 == (getRelationships().size());
    }

    @Test
    public void testNoFlow() {
        setUpStandardTestConfig();
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        testRunner.clearTransferState();
    }

    @Test
    public void testNormalFlow() {
        setUpStandardTestConfig();
        String flowFileContents = "TEST MESSAGE";
        testRunner.enqueue(flowFileContents);
        testRunner.run(1, true);
        assert flowFileContents.contentEquals(new String(processor.getReceivedBuffer()));
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        testRunner.clearTransferState();
    }

    @Test
    public void testSendMessageThrows() {
        PutAzureEventHubTest.OnSendThrowingMockPutAzureEventHub throwingProcessor = new PutAzureEventHubTest.OnSendThrowingMockPutAzureEventHub();
        testRunner = TestRunners.newTestRunner(throwingProcessor);
        setUpStandardTestConfig();
        String flowFileContents = "TEST MESSAGE";
        testRunner.enqueue(flowFileContents);
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE);
        testRunner.clearTransferState();
    }

    @Test(expected = AssertionError.class)
    public void testBadConnectionString() {
        PutAzureEventHubTest.BogusConnectionStringMockPutAzureEventHub badConnectionStringProcessor = new PutAzureEventHubTest.BogusConnectionStringMockPutAzureEventHub();
        testRunner = TestRunners.newTestRunner(badConnectionStringProcessor);
        setUpStandardTestConfig();
        testRunner.run(1, true);
    }

    private static class MockPutAzureEventHub extends PutAzureEventHub {
        byte[] receivedBuffer = null;

        byte[] getReceivedBuffer() {
            return receivedBuffer;
        }

        @Override
        protected EventHubClient createEventHubClient(final String namespace, final String eventHubName, final String policyName, final String policyKey) throws ProcessException {
            return null;
        }

        @Override
        protected void sendMessage(final byte[] buffer) throws ProcessException {
            receivedBuffer = buffer;
        }
    }

    private static class OnSendThrowingMockPutAzureEventHub extends PutAzureEventHub {
        @Override
        protected EventHubClient createEventHubClient(final String namespace, final String eventHubName, final String policyName, final String policyKey) throws ProcessException {
            return null;
        }
    }

    private static class BogusConnectionStringMockPutAzureEventHub extends PutAzureEventHub {
        @Override
        protected String getConnectionString(final String namespace, final String eventHubName, final String policyName, final String policyKey) {
            return "Bogus Connection String";
        }
    }
}

