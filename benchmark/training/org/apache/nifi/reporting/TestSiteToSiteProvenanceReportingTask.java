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
package org.apache.nifi.reporting;


import Scope.LOCAL;
import SiteToSiteProvenanceReportingTask.BATCH_SIZE;
import SiteToSiteProvenanceReportingTask.FILTER_COMPONENT_ID;
import SiteToSiteProvenanceReportingTask.FILTER_COMPONENT_NAME;
import SiteToSiteProvenanceReportingTask.FILTER_COMPONENT_NAME_EXCLUDE;
import SiteToSiteProvenanceReportingTask.FILTER_COMPONENT_TYPE;
import SiteToSiteProvenanceReportingTask.FILTER_COMPONENT_TYPE_EXCLUDE;
import SiteToSiteProvenanceReportingTask.FILTER_EVENT_TYPE;
import SiteToSiteProvenanceReportingTask.FILTER_EVENT_TYPE_EXCLUDE;
import SiteToSiteProvenanceReportingTask.LAST_EVENT_ID_KEY;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.provenance.ProvenanceEventRecord;
import org.apache.nifi.provenance.ProvenanceEventRepository;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.state.MockStateManager;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestSiteToSiteProvenanceReportingTask {
    private final ReportingContext context = Mockito.mock(ReportingContext.class);

    private final ReportingInitializationContext initContext = Mockito.mock(ReportingInitializationContext.class);

    private final ConfigurationContext confContext = Mockito.mock(ConfigurationContext.class);

    @Test
    public void testSerializedForm() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());
        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonObject msgArray = jsonReader.readArray().getJsonObject(0).getJsonObject("updatedAttributes");
        Assert.assertEquals(msgArray.getString("abc"), event.getAttributes().get("abc"));
    }

    @Test
    public void testFilterComponentIdSuccess() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_ID, "2345, 5678,  1234");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());
    }

    @Test
    public void testFilterComponentIdNoResult() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_ID, "9999");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterComponentTypeSuccess() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_TYPE, "dummy.*");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());
    }

    @Test
    public void testFilterComponentName() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_NAME, "Processor in .*");
        properties.put(FILTER_COMPONENT_NAME_EXCLUDE, ".*PGB");
        // A001 has name "Processor in PGA" and should be picked
        ProvenanceEventRecord event = createProvenanceEventRecord("A001", "dummy");
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        JsonNode reportedEvent = new ObjectMapper().readTree(task.dataSent.get(0)).get(0);
        Assert.assertEquals("A001", reportedEvent.get("componentId").asText());
        Assert.assertEquals("Processor in PGA", reportedEvent.get("componentName").asText());
        // B001 has name "Processor in PGB" and should not be picked
        event = createProvenanceEventRecord("B001", "dummy");
        task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterComponentTypeExcludeSuccess() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_TYPE_EXCLUDE, "dummy.*");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterComponentTypeNoResult() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_TYPE, "proc.*");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterComponentTypeNoResultExcluded() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_TYPE_EXCLUDE, "proc.*");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());
    }

    @Test
    public void testFilterEventTypeSuccess() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_EVENT_TYPE, "RECEIVE, notExistingType, DROP");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());
    }

    @Test
    public void testFilterEventTypeExcludeSuccess() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_EVENT_TYPE_EXCLUDE, "RECEIVE, notExistingType, DROP");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterEventTypeNoResult() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_EVENT_TYPE, "DROP");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterMultiFilterNoResult() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_ID, "2345, 5678,  1234");
        properties.put(FILTER_COMPONENT_TYPE, "dummy.*");
        properties.put(FILTER_EVENT_TYPE, "DROP");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterMultiFilterSuccess() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_ID, "2345, 5678,  1234");
        properties.put(FILTER_COMPONENT_TYPE, "dummy.*");
        properties.put(FILTER_EVENT_TYPE, "RECEIVE");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());
    }

    @Test
    public void testFilterMultiFilterExcludeTakesPrecedence() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_TYPE_EXCLUDE, "dummy.*");
        properties.put(FILTER_EVENT_TYPE, "RECEIVE");
        ProvenanceEventRecord event = createProvenanceEventRecord();
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testFilterProcessGroupId() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_ID, "pgB2");
        // B201 belongs to ProcessGroup B2, so it should be picked.
        ProvenanceEventRecord event = createProvenanceEventRecord("B201", "dummy");
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        JsonNode reportedEvent = new ObjectMapper().readTree(task.dataSent.get(0)).get(0);
        Assert.assertEquals("B201", reportedEvent.get("componentId").asText());
        Assert.assertEquals("Processor in PGB2", reportedEvent.get("componentName").asText());
        // B301 belongs to PG B3, whose parent is PGB2, so it should be picked, too.
        event = createProvenanceEventRecord("B301", "dummy");
        task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        reportedEvent = new ObjectMapper().readTree(task.dataSent.get(0)).get(0);
        Assert.assertEquals("B301", reportedEvent.get("componentId").asText());
        Assert.assertEquals("Processor in PGB3", reportedEvent.get("componentName").asText());
        // A001 belongs to PG A, whose parent is the root PG, so it should be filtered out.
        event = createProvenanceEventRecord("A001", "dummy");
        task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    @Test
    public void testRemotePorts() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(FILTER_COMPONENT_ID, "riB2,roB3");
        // riB2 is a Remote Input Port in Process Group B2.
        ProvenanceEventRecord event = createProvenanceEventRecord("riB2", "Remote Input Port");
        TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        JsonNode reportedEvent = new ObjectMapper().readTree(task.dataSent.get(0)).get(0);
        Assert.assertEquals("riB2", reportedEvent.get("componentId").asText());
        Assert.assertEquals("Remote Input Port name", reportedEvent.get("componentName").asText());
        Assert.assertEquals("pgB2", reportedEvent.get("processGroupId").asText());
        // roB3 is a Remote Output Port in Process Group B3.
        event = createProvenanceEventRecord("roB3", "Remote Output Port");
        task = setup(event, properties, 1);
        task.initialize(initContext);
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        reportedEvent = new ObjectMapper().readTree(task.dataSent.get(0)).get(0);
        Assert.assertEquals("roB3", reportedEvent.get("componentId").asText());
        Assert.assertEquals("Remote Output Port name", reportedEvent.get("componentName").asText());
        Assert.assertEquals("pgB3", reportedEvent.get("processGroupId").asText());
    }

    @Test
    public void testWhenProvenanceMaxIdEqualToLastEventIdInStateManager() throws IOException, InitializationException {
        final long maxEventId = 2500;
        // create the mock reporting task and mock state manager
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        final TestSiteToSiteProvenanceReportingTask.MockSiteToSiteProvenanceReportingTask task = setup(null, properties);
        final MockStateManager stateManager = new MockStateManager(task);
        // create the state map and set the last id to the same value as maxEventId
        final Map<String, String> state = new HashMap<>();
        state.put(LAST_EVENT_ID_KEY, String.valueOf(maxEventId));
        stateManager.setState(state, LOCAL);
        // setup the mock provenance repository to return maxEventId
        final ProvenanceEventRepository provenanceRepository = Mockito.mock(ProvenanceEventRepository.class);
        Mockito.doAnswer(new Answer<Long>() {
            @Override
            public Long answer(final InvocationOnMock invocation) throws Throwable {
                return maxEventId;
            }
        }).when(provenanceRepository).getMaxEventId();
        // setup the mock EventAccess to return the mock provenance repository
        final EventAccess eventAccess = Mockito.mock(EventAccess.class);
        Mockito.when(eventAccess.getProvenanceRepository()).thenReturn(provenanceRepository);
        task.initialize(initContext);
        // execute the reporting task and should not produce any data b/c max id same as previous id
        task.onScheduled(confContext);
        task.onTrigger(context);
        Assert.assertEquals(0, task.dataSent.size());
    }

    private static final class MockSiteToSiteProvenanceReportingTask extends SiteToSiteProvenanceReportingTask {
        public MockSiteToSiteProvenanceReportingTask() throws IOException {
            super();
        }

        final List<byte[]> dataSent = new ArrayList<>();

        @Override
        protected SiteToSiteClient getClient() {
            final SiteToSiteClient client = Mockito.mock(SiteToSiteClient.class);
            final Transaction transaction = Mockito.mock(Transaction.class);
            try {
                Mockito.doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(final InvocationOnMock invocation) throws Throwable {
                        final byte[] data = getArgumentAt(0, byte[].class);
                        dataSent.add(data);
                        return null;
                    }
                }).when(transaction).send(Mockito.any(byte[].class), Mockito.any(Map.class));
                Mockito.when(client.createTransaction(Mockito.any(TransferDirection.class))).thenReturn(transaction);
            } catch (final Exception e) {
                e.printStackTrace();
                Assert.fail(e.toString());
            }
            return client;
        }

        public List<byte[]> getDataSent() {
            return dataSent;
        }
    }
}

