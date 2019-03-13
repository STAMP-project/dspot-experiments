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


import AmqpConstants.ENQUEUED_TIME_UTC_ANNOTATION_NAME;
import AmqpConstants.OFFSET_ANNOTATION_NAME;
import AmqpConstants.PARTITION_KEY_ANNOTATION_NAME;
import AmqpConstants.SEQUENCE_NUMBER_ANNOTATION_NAME;
import GetAzureEventHub.ACCESS_POLICY;
import GetAzureEventHub.ENQUEUE_TIME;
import GetAzureEventHub.EVENT_HUB_NAME;
import GetAzureEventHub.NAMESPACE;
import GetAzureEventHub.NUM_PARTITIONS;
import GetAzureEventHub.POLICY_PRIMARY_KEY;
import GetAzureEventHub.RECEIVER_FETCH_SIZE;
import GetAzureEventHub.RECEIVER_FETCH_TIMEOUT;
import GetAzureEventHub.REL_SUCCESS;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventData.SystemProperties;
import com.microsoft.azure.eventhubs.PartitionReceiver;
import com.microsoft.azure.servicebus.ServiceBusException;
import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;


public class GetAzureEventHubTest {
    private static final String namespaceName = "nifi-azure-hub";

    private static final String eventHubName = "get-test";

    private static final String sasKeyName = "bogus-policy";

    private static final String sasKey = "9rHmHqxoOVWOb8wS09dvqXYxnNiLqxNMCbmt6qMaQyU!";

    private static final Date ENQUEUED_TIME_VALUE = Date.from(Clock.fixed(Instant.now(), ZoneId.systemDefault()).instant());

    public static final long SEQUENCE_NUMBER_VALUE = 13L;

    public static final String OFFSET_VALUE = "100";

    public static final String PARTITION_KEY_VALUE = "0";

    private TestRunner testRunner;

    private GetAzureEventHubTest.MockGetAzureEventHub processor;

    @Test
    public void testProcessorConfigValidity() {
        testRunner.setProperty(EVENT_HUB_NAME, GetAzureEventHubTest.eventHubName);
        testRunner.assertNotValid();
        testRunner.setProperty(NAMESPACE, GetAzureEventHubTest.namespaceName);
        testRunner.assertNotValid();
        testRunner.setProperty(ACCESS_POLICY, GetAzureEventHubTest.sasKeyName);
        testRunner.assertNotValid();
        testRunner.setProperty(POLICY_PRIMARY_KEY, GetAzureEventHubTest.sasKey);
        testRunner.assertNotValid();
        testRunner.setProperty(NUM_PARTITIONS, "4");
        testRunner.assertValid();
        testRunner.setProperty(ENQUEUE_TIME, "2015-12-22T21:55:10.000Z");
        testRunner.assertValid();
        testRunner.setProperty(RECEIVER_FETCH_SIZE, "5");
        testRunner.assertValid();
        testRunner.setProperty(RECEIVER_FETCH_TIMEOUT, "10000");
        testRunner.assertValid();
    }

    @Test
    public void verifyRelationships() {
        assert 1 == (getRelationships().size());
    }

    @Test
    public void testNoPartitions() {
        GetAzureEventHubTest.MockGetAzureEventHubNoPartitions mockProcessor = new GetAzureEventHubTest.MockGetAzureEventHubNoPartitions();
        testRunner = TestRunners.newTestRunner(mockProcessor);
        setUpStandardTestConfig();
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        testRunner.clearTransferState();
    }

    @Test
    public void testNullRecieve() {
        setUpStandardTestConfig();
        processor.nullReceive = true;
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        testRunner.clearTransferState();
    }

    @Test(expected = AssertionError.class)
    public void testThrowGetReceiver() {
        setUpStandardTestConfig();
        processor.getReceiverThrow = true;
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        testRunner.clearTransferState();
    }

    @Test
    public void testNormalFlow() throws Exception {
        setUpStandardTestConfig();
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals("test event number: 0");
        flowFile.assertAttributeEquals("eventhub.enqueued.timestamp", GetAzureEventHubTest.ENQUEUED_TIME_VALUE.toInstant().toString());
        flowFile.assertAttributeEquals("eventhub.offset", GetAzureEventHubTest.OFFSET_VALUE);
        flowFile.assertAttributeEquals("eventhub.sequence", String.valueOf(GetAzureEventHubTest.SEQUENCE_NUMBER_VALUE));
        flowFile.assertAttributeEquals("eventhub.name", GetAzureEventHubTest.eventHubName);
        testRunner.clearTransferState();
    }

    @Test
    public void testNormalNotReceivedEventsFlow() throws Exception {
        setUpStandardTestConfig();
        processor.received = false;
        testRunner.run(1, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 10);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals("test event number: 0");
        flowFile.assertAttributeNotExists("eventhub.enqueued.timestamp");
        flowFile.assertAttributeNotExists("eventhub.offset");
        flowFile.assertAttributeNotExists("eventhub.sequence");
        flowFile.assertAttributeEquals("eventhub.name", GetAzureEventHubTest.eventHubName);
        testRunner.clearTransferState();
    }

    /**
     * Provides a stubbed processor instance for testing
     */
    public static class MockGetAzureEventHub extends GetAzureEventHub {
        boolean nullReceive = false;

        boolean getReceiverThrow = false;

        boolean received = true;

        @Override
        protected void setupReceiver(final String connectionString) throws ProcessException {
            // do nothing
        }

        @Override
        protected PartitionReceiver getReceiver(final ProcessContext context, final String partitionId) throws ServiceBusException, IOException, InterruptedException, ExecutionException {
            if (getReceiverThrow) {
                throw new IOException("Could not create receiver");
            }
            return null;
        }

        @Override
        protected Iterable<EventData> receiveEvents(final ProcessContext context, final String partitionId) throws ProcessException {
            if (nullReceive) {
                return null;
            }
            if (getReceiverThrow) {
                throw new ProcessException("Could not create receiver");
            }
            final LinkedList<EventData> receivedEvents = new LinkedList<>();
            for (int i = 0; i < 10; i++) {
                EventData eventData = new EventData(String.format("test event number: %d", i).getBytes());
                if (received) {
                    HashMap<String, Object> properties = new HashMap<>();
                    properties.put(PARTITION_KEY_ANNOTATION_NAME, GetAzureEventHubTest.PARTITION_KEY_VALUE);
                    properties.put(OFFSET_ANNOTATION_NAME, GetAzureEventHubTest.OFFSET_VALUE);
                    properties.put(SEQUENCE_NUMBER_ANNOTATION_NAME, GetAzureEventHubTest.SEQUENCE_NUMBER_VALUE);
                    properties.put(ENQUEUED_TIME_UTC_ANNOTATION_NAME, GetAzureEventHubTest.ENQUEUED_TIME_VALUE);
                    SystemProperties systemProperties = new SystemProperties(properties);
                    Whitebox.setInternalState(eventData, "systemProperties", systemProperties);
                }
                receivedEvents.add(eventData);
            }
            return receivedEvents;
        }
    }

    public static class MockGetAzureEventHubNoPartitions extends GetAzureEventHub {
        @Override
        protected void setupReceiver(final String connectionString) throws ProcessException {
            // do nothing
        }

        @Override
        public void onScheduled(final ProcessContext context) throws ProcessException {
        }
    }
}

