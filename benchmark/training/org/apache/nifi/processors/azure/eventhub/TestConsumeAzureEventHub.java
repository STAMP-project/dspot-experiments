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


import ConsumeAzureEventHub.EventProcessor;
import ConsumeAzureEventHub.REL_PARSE_FAILURE;
import ConsumeAzureEventHub.REL_SUCCESS;
import ProvenanceEventType.RECEIVE;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventprocessorhost.PartitionContext;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessSession;
import org.apache.nifi.util.SharedSessionState;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestConsumeAzureEventHub {
    private EventProcessor eventProcessor;

    private MockProcessSession processSession;

    private SharedSessionState sharedState;

    private PartitionContext partitionContext;

    private ConsumeAzureEventHub processor;

    @Test
    public void testReceiveOne() throws Exception {
        final Iterable<EventData> eventDataList = Arrays.asList(new EventData("one".getBytes(StandardCharsets.UTF_8)));
        eventProcessor.onEvents(partitionContext, eventDataList);
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile msg1 = flowFiles.get(0);
        msg1.assertContentEquals("one");
        msg1.assertAttributeEquals("eventhub.name", "eventhub-name");
        msg1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent1 = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenanceEvent1.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent1.getTransitUri());
    }

    @Test
    public void testReceiveTwo() throws Exception {
        final Iterable<EventData> eventDataList = Arrays.asList(new EventData("one".getBytes(StandardCharsets.UTF_8)), new EventData("two".getBytes(StandardCharsets.UTF_8)));
        eventProcessor.onEvents(partitionContext, eventDataList);
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        final MockFlowFile msg1 = flowFiles.get(0);
        msg1.assertContentEquals("one");
        final MockFlowFile msg2 = flowFiles.get(1);
        msg2.assertContentEquals("two");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
    }

    @Test
    public void testCheckpointFailure() throws Exception {
        final Iterable<EventData> eventDataList = Arrays.asList(new EventData("one".getBytes(StandardCharsets.UTF_8)), new EventData("two".getBytes(StandardCharsets.UTF_8)));
        Mockito.doThrow(new RuntimeException("Failed to create a checkpoint.")).when(partitionContext).checkpoint();
        eventProcessor.onEvents(partitionContext, eventDataList);
        // Even if it fails to create a checkpoint, these FlowFiles are already committed.
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(2, flowFiles.size());
        final MockFlowFile msg1 = flowFiles.get(0);
        msg1.assertContentEquals("one");
        final MockFlowFile msg2 = flowFiles.get(1);
        msg2.assertContentEquals("two");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
    }

    @Test
    public void testReceiveRecords() throws Exception {
        final List<EventData> eventDataList = Arrays.asList(new EventData("one".getBytes(StandardCharsets.UTF_8)), new EventData("two".getBytes(StandardCharsets.UTF_8)));
        setupRecordReader(eventDataList);
        setupRecordWriter();
        eventProcessor.onEvents(partitionContext, eventDataList);
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile ff1 = flowFiles.get(0);
        ff1.assertContentEquals("onetwo");
        ff1.assertAttributeEquals("eventhub.name", "eventhub-name");
        ff1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent1 = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenanceEvent1.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent1.getTransitUri());
    }

    @Test
    public void testReceiveRecordReaderFailure() throws Exception {
        final List<EventData> eventDataList = Arrays.asList(new EventData("one".getBytes(StandardCharsets.UTF_8)), new EventData("two".getBytes(StandardCharsets.UTF_8)), new EventData("three".getBytes(StandardCharsets.UTF_8)), new EventData("four".getBytes(StandardCharsets.UTF_8)));
        setupRecordReader(eventDataList, 2, null);
        setupRecordWriter();
        eventProcessor.onEvents(partitionContext, eventDataList);
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile ff1 = flowFiles.get(0);
        ff1.assertContentEquals("onetwofour");
        ff1.assertAttributeEquals("eventhub.name", "eventhub-name");
        ff1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<MockFlowFile> failedFFs = processSession.getFlowFilesForRelationship(REL_PARSE_FAILURE);
        Assert.assertEquals(1, failedFFs.size());
        final MockFlowFile failed1 = failedFFs.get(0);
        failed1.assertContentEquals("three");
        failed1.assertAttributeEquals("eventhub.name", "eventhub-name");
        failed1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent1 = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenanceEvent1.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent1.getTransitUri());
        final ProvenanceEventRecord provenanceEvent2 = provenanceEvents.get(1);
        Assert.assertEquals(RECEIVE, provenanceEvent2.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent2.getTransitUri());
    }

    @Test
    public void testReceiveAllRecordFailure() throws Exception {
        final List<EventData> eventDataList = Collections.singletonList(new EventData("one".getBytes(StandardCharsets.UTF_8)));
        setupRecordReader(eventDataList, 0, null);
        setupRecordWriter();
        eventProcessor.onEvents(partitionContext, eventDataList);
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(0, flowFiles.size());
        final List<MockFlowFile> failedFFs = processSession.getFlowFilesForRelationship(REL_PARSE_FAILURE);
        Assert.assertEquals(1, failedFFs.size());
        final MockFlowFile failed1 = failedFFs.get(0);
        failed1.assertContentEquals("one");
        failed1.assertAttributeEquals("eventhub.name", "eventhub-name");
        failed1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(1, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent1 = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenanceEvent1.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent1.getTransitUri());
    }

    @Test
    public void testReceiveRecordWriterFailure() throws Exception {
        final List<EventData> eventDataList = Arrays.asList(new EventData("one".getBytes(StandardCharsets.UTF_8)), new EventData("two".getBytes(StandardCharsets.UTF_8)), new EventData("three".getBytes(StandardCharsets.UTF_8)), new EventData("four".getBytes(StandardCharsets.UTF_8)));
        setupRecordReader(eventDataList, (-1), "two");
        setupRecordWriter("two");
        eventProcessor.onEvents(partitionContext, eventDataList);
        processSession.assertCommitted();
        final List<MockFlowFile> flowFiles = processSession.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertEquals(1, flowFiles.size());
        final MockFlowFile ff1 = flowFiles.get(0);
        ff1.assertContentEquals("onethreefour");
        ff1.assertAttributeEquals("eventhub.name", "eventhub-name");
        ff1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<MockFlowFile> failedFFs = processSession.getFlowFilesForRelationship(REL_PARSE_FAILURE);
        Assert.assertEquals(1, failedFFs.size());
        final MockFlowFile failed1 = failedFFs.get(0);
        failed1.assertContentEquals("two");
        failed1.assertAttributeEquals("eventhub.name", "eventhub-name");
        failed1.assertAttributeEquals("eventhub.partition", "partition-id");
        final List<ProvenanceEventRecord> provenanceEvents = sharedState.getProvenanceEvents();
        Assert.assertEquals(2, provenanceEvents.size());
        final ProvenanceEventRecord provenanceEvent1 = provenanceEvents.get(0);
        Assert.assertEquals(RECEIVE, provenanceEvent1.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent1.getTransitUri());
        final ProvenanceEventRecord provenanceEvent2 = provenanceEvents.get(1);
        Assert.assertEquals(RECEIVE, provenanceEvent2.getEventType());
        Assert.assertEquals(("amqps://namespace.servicebus.windows.net/" + "eventhub-name/ConsumerGroups/consumer-group/Partitions/partition-id"), provenanceEvent2.getTransitUri());
    }
}

