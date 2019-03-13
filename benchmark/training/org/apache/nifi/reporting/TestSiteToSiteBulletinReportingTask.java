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


import SiteToSiteBulletinReportingTask.BATCH_SIZE;
import SiteToSiteBulletinReportingTask.PLATFORM;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.apache.nifi.attribute.expression.language.StandardPropertyValue;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.PropertyValue;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.remote.Transaction;
import org.apache.nifi.remote.TransferDirection;
import org.apache.nifi.remote.client.SiteToSiteClient;
import org.apache.nifi.reporting.AbstractSiteToSiteReportingTask.NiFiUrlValidator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestSiteToSiteBulletinReportingTask {
    @Test
    public void testUrls() throws IOException {
        final ValidationContext context = Mockito.mock(ValidationContext.class);
        Mockito.when(context.newPropertyValue(Mockito.anyString())).then(new Answer<PropertyValue>() {
            @Override
            public PropertyValue answer(InvocationOnMock invocation) throws Throwable {
                String value = ((String) (invocation.getArguments()[0]));
                return new StandardPropertyValue(value, null);
            }
        });
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "http://localhost:8080/nifi", context).isValid());
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "http://localhost:8080", context).isValid());
        Assert.assertFalse(new NiFiUrlValidator().validate("url", "", context).isValid());
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "https://localhost:8080/nifi", context).isValid());
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "https://localhost:8080/nifi,https://localhost:8080/nifi", context).isValid());
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "https://localhost:8080/nifi, https://localhost:8080/nifi", context).isValid());
        Assert.assertFalse(new NiFiUrlValidator().validate("url", "http://localhost:8080/nifi, https://localhost:8080/nifi", context).isValid());
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "http://localhost:8080/nifi,http://localhost:8080/nifi", context).isValid());
        Assert.assertTrue(new NiFiUrlValidator().validate("url", "http://localhost:8080/nifi,http://localhost:8080", context).isValid());
    }

    @Test
    public void testSerializedForm() throws IOException, InitializationException {
        // creating the list of bulletins
        final List<Bulletin> bulletins = new ArrayList<Bulletin>();
        bulletins.add(BulletinFactory.createBulletin("group-id", "group-name", "source-id", "source-name", "category", "severity", "message"));
        // mock the access to the list of bulletins
        final ReportingContext context = Mockito.mock(ReportingContext.class);
        final BulletinRepository repository = Mockito.mock(BulletinRepository.class);
        Mockito.when(context.getBulletinRepository()).thenReturn(repository);
        Mockito.when(repository.findBulletins(Mockito.any(BulletinQuery.class))).thenReturn(bulletins);
        // creating reporting task
        final TestSiteToSiteBulletinReportingTask.MockSiteToSiteBulletinReportingTask task = new TestSiteToSiteBulletinReportingTask.MockSiteToSiteBulletinReportingTask();
        // settings properties and mocking access to properties
        final Map<PropertyDescriptor, String> properties = new HashMap<>();
        for (final PropertyDescriptor descriptor : getSupportedPropertyDescriptors()) {
            properties.put(descriptor, descriptor.getDefaultValue());
        }
        properties.put(BATCH_SIZE, "1000");
        properties.put(PLATFORM, "nifi");
        Mockito.doAnswer(new Answer<PropertyValue>() {
            @Override
            public PropertyValue answer(final InvocationOnMock invocation) throws Throwable {
                final PropertyDescriptor descriptor = getArgumentAt(0, PropertyDescriptor.class);
                return new org.apache.nifi.util.MockPropertyValue(properties.get(descriptor));
            }
        }).when(context).getProperty(Mockito.any(PropertyDescriptor.class));
        // setup the mock initialization context
        final ComponentLog logger = Mockito.mock(ComponentLog.class);
        final ReportingInitializationContext initContext = Mockito.mock(ReportingInitializationContext.class);
        Mockito.when(initContext.getIdentifier()).thenReturn(UUID.randomUUID().toString());
        Mockito.when(initContext.getLogger()).thenReturn(logger);
        task.initialize(initContext);
        task.onTrigger(context);
        // test checking
        Assert.assertEquals(1, task.dataSent.size());
        final String msg = new String(task.dataSent.get(0), StandardCharsets.UTF_8);
        JsonReader jsonReader = Json.createReader(new ByteArrayInputStream(msg.getBytes()));
        JsonObject bulletinJson = jsonReader.readArray().getJsonObject(0);
        Assert.assertEquals("message", bulletinJson.getString("bulletinMessage"));
        Assert.assertEquals("group-name", bulletinJson.getString("bulletinGroupName"));
    }

    private static final class MockSiteToSiteBulletinReportingTask extends SiteToSiteBulletinReportingTask {
        public MockSiteToSiteBulletinReportingTask() throws IOException {
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
                }).when(transaction).send(Mockito.any(byte[].class), Mockito.anyMapOf(String.class, String.class));
                Mockito.when(client.createTransaction(Mockito.any(TransferDirection.class))).thenReturn(transaction);
            } catch (final Exception e) {
                e.printStackTrace();
                Assert.fail(e.toString());
            }
            return client;
        }
    }
}

