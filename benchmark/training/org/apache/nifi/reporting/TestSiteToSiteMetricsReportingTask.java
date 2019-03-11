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


import SiteToSiteMetricsReportingTask.AMBARI_FORMAT;
import SiteToSiteMetricsReportingTask.DESTINATION_URL;
import SiteToSiteMetricsReportingTask.FORMAT;
import SiteToSiteMetricsReportingTask.INSTANCE_URL;
import SiteToSiteMetricsReportingTask.PORT_NAME;
import SiteToSiteMetricsReportingTask.RECORD_FORMAT;
import SiteToSiteMetricsReportingTask.RECORD_WRITER;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import org.apache.nifi.attribute.expression.language.StandardPropertyValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
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


public class TestSiteToSiteMetricsReportingTask {
    private ReportingContext context;

    private ProcessGroupStatus status;

    @Test
    public void testValidationBothAmbariFormatRecordWriter() throws IOException {
        ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        final String urlEL = "http://${hostname(true)}:8080/nifi";
        final String url = "http://localhost:8080/nifi";
        final TestSiteToSiteMetricsReportingTask.MockSiteToSiteMetricsReportingTask task = new TestSiteToSiteMetricsReportingTask.MockSiteToSiteMetricsReportingTask();
        Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(FORMAT, AMBARI_FORMAT.getValue());
        properties.put(DESTINATION_URL, url);
        properties.put(INSTANCE_URL, url);
        properties.put(PORT_NAME, "port");
        final PropertyValue pValueUrl = Mockito.mock(StandardPropertyValue.class);
        Mockito.when(validationContext.newPropertyValue(url)).thenReturn(pValueUrl);
        Mockito.when(validationContext.newPropertyValue(urlEL)).thenReturn(pValueUrl);
        Mockito.when(pValueUrl.evaluateAttributeExpressions()).thenReturn(pValueUrl);
        Mockito.when(pValueUrl.getValue()).thenReturn(url);
        Mockito.doAnswer(new Answer<PropertyValue>() {
            @Override
            public PropertyValue answer(final InvocationOnMock invocation) throws Throwable {
                final PropertyDescriptor descriptor = getArgumentAt(0, PropertyDescriptor.class);
                return new org.apache.nifi.util.MockPropertyValue(properties.get(descriptor));
            }
        }).when(validationContext).getProperty(Mockito.any(PropertyDescriptor.class));
        final PropertyValue pValue = Mockito.mock(StandardPropertyValue.class);
        Mockito.when(validationContext.getProperty(RECORD_WRITER)).thenReturn(pValue);
        Mockito.when(pValue.isSet()).thenReturn(true);
        // should be invalid because both ambari format and record writer are set
        Collection<ValidationResult> list = task.validate(validationContext);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(RECORD_WRITER.getDisplayName(), list.iterator().next().getInput());
    }

    @Test
    public void testValidationRecordFormatNoRecordWriter() throws IOException {
        ValidationContext validationContext = Mockito.mock(ValidationContext.class);
        final String urlEL = "http://${hostname(true)}:8080/nifi";
        final String url = "http://localhost:8080/nifi";
        final TestSiteToSiteMetricsReportingTask.MockSiteToSiteMetricsReportingTask task = new TestSiteToSiteMetricsReportingTask.MockSiteToSiteMetricsReportingTask();
        Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(FORMAT, RECORD_FORMAT.getValue());
        properties.put(DESTINATION_URL, url);
        properties.put(INSTANCE_URL, url);
        properties.put(PORT_NAME, "port");
        final PropertyValue pValueUrl = Mockito.mock(StandardPropertyValue.class);
        Mockito.when(validationContext.newPropertyValue(url)).thenReturn(pValueUrl);
        Mockito.when(validationContext.newPropertyValue(urlEL)).thenReturn(pValueUrl);
        Mockito.when(pValueUrl.evaluateAttributeExpressions()).thenReturn(pValueUrl);
        Mockito.when(pValueUrl.getValue()).thenReturn(url);
        Mockito.doAnswer(new Answer<PropertyValue>() {
            @Override
            public PropertyValue answer(final InvocationOnMock invocation) throws Throwable {
                final PropertyDescriptor descriptor = getArgumentAt(0, PropertyDescriptor.class);
                return new org.apache.nifi.util.MockPropertyValue(properties.get(descriptor));
            }
        }).when(validationContext).getProperty(Mockito.any(PropertyDescriptor.class));
        final PropertyValue pValue = Mockito.mock(StandardPropertyValue.class);
        Mockito.when(validationContext.getProperty(RECORD_WRITER)).thenReturn(pValue);
        Mockito.when(pValue.isSet()).thenReturn(false);
        // should be invalid because both ambari format and record writer are set
        Collection<ValidationResult> list = task.validate(validationContext);
        Assert.assertEquals(1, list.size());
        Assert.assertEquals(RECORD_WRITER.getDisplayName(), list.iterator().next().getInput());
    }

    @Test
    public void testAmbariFormat() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(FORMAT, AMBARI_FORMAT.getValue());
        TestSiteToSiteMetricsReportingTask.MockSiteToSiteMetricsReportingTask task = initTask(properties);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonArray array = jsonReader.readObject().getJsonArray("metrics");
        for (int i = 0; i < (array.size()); i++) {
            JsonObject object = array.getJsonObject(i);
            Assert.assertEquals("nifi", object.getString("appid"));
            Assert.assertEquals("1234", object.getString("instanceid"));
            if (object.getString("metricname").equals("FlowFilesQueued")) {
                for (Map.Entry<String, JsonValue> kv : object.getJsonObject("metrics").entrySet()) {
                    Assert.assertEquals("\"100\"", kv.getValue().toString());
                }
                return;
            }
        }
        Assert.fail();
    }

    @Test
    public void testRecordFormat() throws IOException, InitializationException {
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        properties.put(FORMAT, RECORD_FORMAT.getValue());
        properties.put(RECORD_WRITER, "record-writer");
        TestSiteToSiteMetricsReportingTask.MockSiteToSiteMetricsReportingTask task = initTask(properties);
        task.onTrigger(context);
        Assert.assertEquals(1, task.dataSent.size());
        String[] data = new String(task.dataSent.get(0)).split(",");
        Assert.assertEquals("\"nifi\"", data[0]);
        Assert.assertEquals("\"1234\"", data[1]);
        Assert.assertEquals("\"100\"", data[10]);// FlowFilesQueued

    }

    private static final class MockSiteToSiteMetricsReportingTask extends SiteToSiteMetricsReportingTask {
        public MockSiteToSiteMetricsReportingTask() throws IOException {
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
                        final byte[] data = invocation.getArgumentAt(0, byte[].class);
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
    }
}

