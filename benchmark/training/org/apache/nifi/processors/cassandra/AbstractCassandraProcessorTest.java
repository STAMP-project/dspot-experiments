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
package org.apache.nifi.processors.cassandra;


import AbstractCassandraProcessor.CHARSET;
import AbstractCassandraProcessor.CLIENT_AUTH;
import AbstractCassandraProcessor.CONNECTION_PROVIDER_SERVICE;
import AbstractCassandraProcessor.CONSISTENCY_LEVEL;
import AbstractCassandraProcessor.CONTACT_POINTS;
import AbstractCassandraProcessor.DEFAULT_CASSANDRA_PORT;
import AbstractCassandraProcessor.KEYSPACE;
import AbstractCassandraProcessor.PASSWORD;
import AbstractCassandraProcessor.PROP_SSL_CONTEXT_SERVICE;
import AbstractCassandraProcessor.USERNAME;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Configuration;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Row;
import com.google.common.collect.Sets;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.SSLContext;
import org.apache.nifi.annotation.lifecycle.OnEnabled;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.controller.ConfigurationContext;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.service.CassandraSessionProvider;
import org.apache.nifi.ssl.SSLContextService;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Unit tests for the AbstractCassandraProcessor class
 */
public class AbstractCassandraProcessorTest {
    AbstractCassandraProcessorTest.MockAbstractCassandraProcessor processor;

    private TestRunner testRunner;

    @Test
    public void testCustomValidate() throws Exception {
        testRunner.setProperty(CONTACT_POINTS, "");
        testRunner.assertNotValid();
        testRunner.setProperty(CONTACT_POINTS, "localhost");
        testRunner.assertNotValid();
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042");
        testRunner.assertValid();
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042, node2: 4399");
        testRunner.assertValid();
        testRunner.setProperty(CONTACT_POINTS, " localhost : 9042, node2: 4399");
        testRunner.assertValid();
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042, node2");
        testRunner.assertNotValid();
        testRunner.setProperty(CONTACT_POINTS, "localhost:65536");
        testRunner.assertNotValid();
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042");
        testRunner.setProperty(USERNAME, "user");
        testRunner.assertNotValid();// Needs a password set if user is set

        testRunner.setProperty(PASSWORD, "password");
        testRunner.assertValid();
    }

    @Test
    public void testCustomValidateEL() throws Exception {
        testRunner.setProperty(CONTACT_POINTS, "${host}");
        testRunner.setProperty(KEYSPACE, "${keyspace}");
        testRunner.setProperty(USERNAME, "${user}");
        testRunner.setProperty(PASSWORD, "${password}");
        testRunner.setProperty(CHARSET, "${charset}");
        testRunner.assertValid();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testGetCassandraObject() throws Exception {
        Row row = CassandraQueryTestUtil.createRow("user1", "Joe", "Smith", Sets.newHashSet("jsmith@notareal.com", "joes@fakedomain.com"), Arrays.asList("New York, NY", "Santa Clara, CA"), new HashMap<Date, String>() {
            {
                put(Calendar.getInstance().getTime(), "Set my alarm for a month from now");
            }
        }, true, 1.0F, 2.0);
        Assert.assertEquals("user1", AbstractCassandraProcessor.getCassandraObject(row, 0, DataType.text()));
        Assert.assertEquals("Joe", AbstractCassandraProcessor.getCassandraObject(row, 1, DataType.text()));
        Assert.assertEquals("Smith", AbstractCassandraProcessor.getCassandraObject(row, 2, DataType.text()));
        Set<String> emails = ((Set<String>) (AbstractCassandraProcessor.getCassandraObject(row, 3, DataType.set(DataType.text()))));
        Assert.assertNotNull(emails);
        Assert.assertEquals(2, emails.size());
        List<String> topPlaces = ((List<String>) (AbstractCassandraProcessor.getCassandraObject(row, 4, DataType.list(DataType.text()))));
        Assert.assertNotNull(topPlaces);
        Map<Date, String> todoMap = ((Map<Date, String>) (AbstractCassandraProcessor.getCassandraObject(row, 5, DataType.map(DataType.timestamp(), DataType.text()))));
        Assert.assertNotNull(todoMap);
        Assert.assertEquals(1, todoMap.values().size());
        Boolean registered = ((Boolean) (AbstractCassandraProcessor.getCassandraObject(row, 6, DataType.cboolean())));
        Assert.assertNotNull(registered);
        Assert.assertTrue(registered);
    }

    @Test
    public void testGetSchemaForType() throws Exception {
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("string").getType().getName(), "string");
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("boolean").getType().getName(), "boolean");
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("int").getType().getName(), "int");
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("long").getType().getName(), "long");
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("float").getType().getName(), "float");
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("double").getType().getName(), "double");
        Assert.assertEquals(AbstractCassandraProcessor.getSchemaForType("bytes").getType().getName(), "bytes");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetSchemaForTypeBadType() throws Exception {
        AbstractCassandraProcessor.getSchemaForType("nothing");
    }

    @Test
    public void testGetPrimitiveAvroTypeFromCassandraType() throws Exception {
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.ascii()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.text()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.varchar()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.timestamp()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.timeuuid()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.uuid()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.inet()));
        Assert.assertEquals("string", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.varint()));
        Assert.assertEquals("boolean", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.cboolean()));
        Assert.assertEquals("int", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.cint()));
        Assert.assertEquals("long", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.bigint()));
        Assert.assertEquals("long", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.counter()));
        Assert.assertEquals("float", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.cfloat()));
        Assert.assertEquals("double", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.cdouble()));
        Assert.assertEquals("bytes", AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(DataType.blob()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetPrimitiveAvroTypeFromCassandraTypeBadType() throws Exception {
        DataType mockDataType = Mockito.mock(DataType.class);
        AbstractCassandraProcessor.getPrimitiveAvroTypeFromCassandraType(mockDataType);
    }

    @Test
    public void testGetPrimitiveDataTypeFromString() {
        Assert.assertEquals(DataType.ascii(), AbstractCassandraProcessor.getPrimitiveDataTypeFromString("ascii"));
    }

    @Test
    public void testGetContactPoints() throws Exception {
        List<InetSocketAddress> contactPoints = getContactPoints("");
        Assert.assertNotNull(contactPoints);
        Assert.assertEquals(1, contactPoints.size());
        Assert.assertEquals("localhost", contactPoints.get(0).getHostName());
        Assert.assertEquals(DEFAULT_CASSANDRA_PORT, contactPoints.get(0).getPort());
        contactPoints = getContactPoints("192.168.99.100:9042");
        Assert.assertNotNull(contactPoints);
        Assert.assertEquals(1, contactPoints.size());
        Assert.assertEquals("192.168.99.100", contactPoints.get(0).getAddress().getHostAddress());
        Assert.assertEquals(9042, contactPoints.get(0).getPort());
        contactPoints = getContactPoints("192.168.99.100:9042, mydomain.com : 4000");
        Assert.assertNotNull(contactPoints);
        Assert.assertEquals(2, contactPoints.size());
        Assert.assertEquals("192.168.99.100", contactPoints.get(0).getAddress().getHostAddress());
        Assert.assertEquals(9042, contactPoints.get(0).getPort());
        Assert.assertEquals("mydomain.com", contactPoints.get(1).getHostName());
        Assert.assertEquals(4000, contactPoints.get(1).getPort());
    }

    @Test
    public void testConnectToCassandra() throws Exception {
        // Follow the non-null path
        Cluster cluster = Mockito.mock(Cluster.class);
        processor.setCluster(cluster);
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        processor.connectToCassandra(testRunner.getProcessContext());
        processor.stop(testRunner.getProcessContext());
        Assert.assertNull(processor.getCluster());
        // Now do a connect where a cluster is "built"
        processor.connectToCassandra(testRunner.getProcessContext());
        Assert.assertEquals("cluster1", processor.getCluster().getMetadata().getClusterName());
    }

    @Test
    public void testConnectToCassandraWithSSL() throws Exception {
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        testRunner.addControllerService("ssl-context", sslService);
        testRunner.enableControllerService(sslService);
        testRunner.setProperty(PROP_SSL_CONTEXT_SERVICE, "ssl-context");
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        testRunner.assertValid(sslService);
        processor.connectToCassandra(testRunner.getProcessContext());
        Assert.assertNotNull(processor.getCluster());
        processor.setCluster(null);
        // Try with a ClientAuth value
        testRunner.setProperty(CLIENT_AUTH, "WANT");
        processor.connectToCassandra(testRunner.getProcessContext());
        Assert.assertNotNull(processor.getCluster());
    }

    @Test(expected = IllegalStateException.class)
    public void testConnectToCassandraWithSSLBadClientAuth() throws Exception {
        SSLContextService sslService = Mockito.mock(SSLContextService.class);
        Mockito.when(sslService.getIdentifier()).thenReturn("ssl-context");
        testRunner.addControllerService("ssl-context", sslService);
        testRunner.enableControllerService(sslService);
        testRunner.setProperty(PROP_SSL_CONTEXT_SERVICE, "ssl-context");
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        testRunner.assertValid(sslService);
        processor.connectToCassandra(testRunner.getProcessContext());
        Assert.assertNotNull(processor.getCluster());
        processor.setCluster(null);
        // Try with a ClientAuth value
        testRunner.setProperty(CLIENT_AUTH, "BAD");
        processor.connectToCassandra(testRunner.getProcessContext());
    }

    @Test
    public void testConnectToCassandraUsernamePassword() throws Exception {
        testRunner.setProperty(USERNAME, "user");
        testRunner.setProperty(PASSWORD, "password");
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        // Now do a connect where a cluster is "built"
        processor.connectToCassandra(testRunner.getProcessContext());
        Assert.assertNotNull(processor.getCluster());
    }

    @Test
    public void testCustomValidateCassandraConnectionConfiguration() throws InitializationException {
        AbstractCassandraProcessorTest.MockCassandraSessionProvider sessionProviderService = new AbstractCassandraProcessorTest.MockCassandraSessionProvider();
        testRunner.addControllerService("cassandra-connection-provider", sessionProviderService);
        testRunner.setProperty(sessionProviderService, CassandraSessionProvider.CONTACT_POINTS, "localhost:9042");
        testRunner.setProperty(sessionProviderService, CassandraSessionProvider.KEYSPACE, "somekyespace");
        testRunner.setProperty(CONNECTION_PROVIDER_SERVICE, "cassandra-connection-provider");
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042");
        testRunner.setProperty(KEYSPACE, "some-keyspace");
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        testRunner.setProperty(USERNAME, "user");
        testRunner.setProperty(PASSWORD, "password");
        testRunner.enableControllerService(sessionProviderService);
        testRunner.assertNotValid();
        testRunner.removeProperty(CONTACT_POINTS);
        testRunner.removeProperty(KEYSPACE);
        testRunner.removeProperty(CONSISTENCY_LEVEL);
        testRunner.removeProperty(USERNAME);
        testRunner.removeProperty(PASSWORD);
        testRunner.assertValid();
    }

    /**
     * Provides a stubbed processor instance for testing
     */
    public static class MockAbstractCassandraProcessor extends AbstractCassandraProcessor {
        @Override
        protected List<PropertyDescriptor> getSupportedPropertyDescriptors() {
            return Arrays.asList(CONNECTION_PROVIDER_SERVICE, CONTACT_POINTS, KEYSPACE, USERNAME, PASSWORD, CONSISTENCY_LEVEL, CHARSET);
        }

        @Override
        public void onTrigger(ProcessContext context, ProcessSession session) throws ProcessException {
        }

        @Override
        protected Cluster createCluster(List<InetSocketAddress> contactPoints, SSLContext sslContext, String username, String password) {
            Cluster mockCluster = Mockito.mock(Cluster.class);
            Metadata mockMetadata = Mockito.mock(Metadata.class);
            Mockito.when(mockMetadata.getClusterName()).thenReturn("cluster1");
            Mockito.when(mockCluster.getMetadata()).thenReturn(mockMetadata);
            Configuration config = Configuration.builder().build();
            Mockito.when(mockCluster.getConfiguration()).thenReturn(config);
            return mockCluster;
        }

        public Cluster getCluster() {
            return cluster.get();
        }

        public void setCluster(Cluster newCluster) {
            this.cluster.set(newCluster);
        }
    }

    /**
     * Mock CassandraSessionProvider implementation for testing purpose
     */
    private class MockCassandraSessionProvider extends CassandraSessionProvider {
        @OnEnabled
        public void onEnabled(final ConfigurationContext context) {
        }
    }
}

