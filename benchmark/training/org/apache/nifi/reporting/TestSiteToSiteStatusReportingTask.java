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


import SiteToSiteStatusReportingTask.BATCH_SIZE;
import SiteToSiteStatusReportingTask.COMPONENT_NAME_FILTER_REGEX;
import SiteToSiteStatusReportingTask.COMPONENT_TYPE_FILTER_REGEX;
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
import javax.json.JsonString;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.status.ProcessGroupStatus;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestSiteToSiteStatusReportingTask {
    private ReportingContext context;

    @Test
    public void testSerializedForm() throws IOException, InitializationException {
        final ProcessGroupStatus pgStatus = TestSiteToSiteStatusReportingTask.generateProcessGroupStatus("root", "Awesome", 1, 0);
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(BATCH_SIZE, "4");
        properties.put(COMPONENT_NAME_FILTER_REGEX, "Awesome.*");
        properties.put(COMPONENT_TYPE_FILTER_REGEX, ".*");
        TestSiteToSiteStatusReportingTask.MockSiteToSiteStatusReportingTask task = initTask(properties, pgStatus);
        task.onTrigger(context);
        Assert.assertEquals(16, task.dataSent.size());
        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonString componentId = jsonReader.readArray().getJsonObject(0).getJsonString("componentId");
        Assert.assertEquals(pgStatus.getId(), componentId.getString());
    }

    @Test
    public void testComponentTypeFilter() throws IOException, InitializationException {
        final ProcessGroupStatus pgStatus = TestSiteToSiteStatusReportingTask.generateProcessGroupStatus("root", "Awesome", 1, 0);
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(BATCH_SIZE, "4");
        properties.put(COMPONENT_NAME_FILTER_REGEX, "Awesome.*");
        properties.put(COMPONENT_TYPE_FILTER_REGEX, "(ProcessGroup|RootProcessGroup)");
        TestSiteToSiteStatusReportingTask.MockSiteToSiteStatusReportingTask task = initTask(properties, pgStatus);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());// Only root pg and 3 child pgs

        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonString componentId = jsonReader.readArray().getJsonObject(0).getJsonString("componentId");
        Assert.assertEquals(pgStatus.getId(), componentId.getString());
    }

    @Test
    public void testConnectionStatus() throws IOException, InitializationException {
        final ProcessGroupStatus pgStatus = TestSiteToSiteStatusReportingTask.generateProcessGroupStatus("root", "Awesome", 1, 0);
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(BATCH_SIZE, "4");
        properties.put(COMPONENT_NAME_FILTER_REGEX, "Awesome.*");
        properties.put(COMPONENT_TYPE_FILTER_REGEX, "(Connection)");
        TestSiteToSiteStatusReportingTask.MockSiteToSiteStatusReportingTask task = initTask(properties, pgStatus);
        task.onTrigger(context);
        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonObject object = jsonReader.readArray().getJsonObject(0);
        JsonString backpressure = object.getJsonString("isBackPressureEnabled");
        JsonString source = object.getJsonString("sourceName");
        Assert.assertEquals("true", backpressure.getString());
        Assert.assertEquals("source", source.getString());
    }

    @Test
    public void testComponentNameFilter() throws IOException, InitializationException {
        final ProcessGroupStatus pgStatus = TestSiteToSiteStatusReportingTask.generateProcessGroupStatus("root", "Awesome", 1, 0);
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(BATCH_SIZE, "4");
        properties.put(COMPONENT_NAME_FILTER_REGEX, "Awesome.*processor.*");
        properties.put(COMPONENT_TYPE_FILTER_REGEX, ".*");
        TestSiteToSiteStatusReportingTask.MockSiteToSiteStatusReportingTask task = initTask(properties, pgStatus);
        task.onTrigger(context);
        Assert.assertEquals(3, task.dataSent.size());// 3 processors for each of 4 groups

        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonString componentId = jsonReader.readArray().getJsonObject(0).getJsonString("componentId");
        Assert.assertEquals("root.1.processor.1", componentId.getString());
    }

    @Test
    public void testComponentNameFilter_nested() throws IOException, InitializationException {
        final ProcessGroupStatus pgStatus = TestSiteToSiteStatusReportingTask.generateProcessGroupStatus("root", "Awesome", 2, 0);
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(BATCH_SIZE, "4");
        properties.put(COMPONENT_NAME_FILTER_REGEX, "Awesome.*processor.*");
        properties.put(COMPONENT_TYPE_FILTER_REGEX, ".*");
        TestSiteToSiteStatusReportingTask.MockSiteToSiteStatusReportingTask task = initTask(properties, pgStatus);
        task.onTrigger(context);
        Assert.assertEquals(10, task.dataSent.size());// 3 + (3 * 3) + (3 * 3 * 3) = 39, or 10 batches of 4

        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonString componentId = jsonReader.readArray().getJsonObject(0).getJsonString("componentId");
        Assert.assertEquals("root.1.1.processor.1", componentId.getString());
    }

    private static final class MockSiteToSiteStatusReportingTask extends SiteToSiteStatusReportingTask {
        public MockSiteToSiteStatusReportingTask() throws IOException {
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

