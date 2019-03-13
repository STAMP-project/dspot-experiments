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
import AbstractCassandraProcessor.CONSISTENCY_LEVEL;
import AbstractCassandraProcessor.CONTACT_POINTS;
import AbstractCassandraProcessor.PASSWORD;
import AbstractCassandraProcessor.USERNAME;
import QueryCassandra.CQL_SELECT_QUERY;
import QueryCassandra.FETCH_SIZE;
import QueryCassandra.JSON_FORMAT;
import QueryCassandra.OUTPUT_FORMAT;
import QueryCassandra.QUERY_TIMEOUT;
import QueryCassandra.REL_FAILURE;
import QueryCassandra.REL_RETRY;
import QueryCassandra.REL_SUCCESS;
import Schema.Field;
import Schema.Type;
import Schema.Type.ARRAY;
import Schema.Type.BOOLEAN;
import Schema.Type.DOUBLE;
import Schema.Type.FLOAT;
import Schema.Type.MAP;
import Schema.Type.NULL;
import Schema.Type.RECORD;
import Schema.Type.STRING;
import Schema.Type.UNION;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Configuration;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.InvalidQueryException;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import org.apache.avro.Schema;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class QueryCassandraTest {
    private TestRunner testRunner;

    private QueryCassandraTest.MockQueryCassandra processor;

    @Test
    public void testProcessorConfigValid() {
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042");
        testRunner.assertNotValid();
        testRunner.setProperty(CQL_SELECT_QUERY, "select * from test");
        testRunner.assertValid();
        testRunner.setProperty(PASSWORD, "password");
        testRunner.assertNotValid();
        testRunner.setProperty(USERNAME, "username");
        testRunner.assertValid();
    }

    @Test
    public void testProcessorELConfigValid() {
        testRunner.setProperty(CONSISTENCY_LEVEL, "ONE");
        testRunner.setProperty(CONTACT_POINTS, "${hosts}");
        testRunner.setProperty(CQL_SELECT_QUERY, "${query}");
        testRunner.setProperty(PASSWORD, "${pass}");
        testRunner.setProperty(USERNAME, "${user}");
        testRunner.assertValid();
    }

    @Test
    public void testProcessorNoInputFlowFileAndExceptions() {
        setUpStandardProcessorConfig();
        // Test no input flowfile
        testRunner.setIncomingConnection(false);
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        testRunner.clearTransferState();
        // Test exceptions
        processor.setExceptionToThrow(new NoHostAvailableException(new HashMap<InetSocketAddress, Throwable>()));
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(new com.datastax.driver.core.exceptions.ReadTimeoutException(new InetSocketAddress("localhost", 9042), ConsistencyLevel.ANY, 0, 1, false));
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(new InvalidQueryException(new InetSocketAddress("localhost", 9042), "invalid query"));
        testRunner.run(1, true, true);
        // No files transferred to failure if there was no incoming connection
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 0);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(new ProcessException());
        testRunner.run(1, true, true);
        // No files transferred to failure if there was no incoming connection
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 0);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(null);
    }

    @Test
    public void testProcessorJsonOutput() {
        setUpStandardProcessorConfig();
        testRunner.setIncomingConnection(false);
        // Test JSON output
        testRunner.setProperty(OUTPUT_FORMAT, JSON_FORMAT);
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> files = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertNotNull(files);
        Assert.assertEquals("One file should be transferred to success", 1, files.size());
        Assert.assertEquals(("{\"results\":[{\"user_id\":\"user1\",\"first_name\":\"Joe\",\"last_name\":\"Smith\"," + (((((("\"emails\":[\"jsmith@notareal.com\"],\"top_places\":[\"New York, NY\",\"Santa Clara, CA\"]," + "\"todo\":{\"2016-01-03 05:00:00+0000\":\"Set my alarm \\\"for\\\" a month from now\"},") + "\"registered\":\"false\",\"scale\":1.0,\"metric\":2.0},") + "{\"user_id\":\"user2\",\"first_name\":\"Mary\",\"last_name\":\"Jones\",") + "\"emails\":[\"mjones@notareal.com\"],\"top_places\":[\"Orlando, FL\"],") + "\"todo\":{\"2016-02-03 05:00:00+0000\":\"Get milk and bread\"},") + "\"registered\":\"true\",\"scale\":3.0,\"metric\":4.0}]}")), new String(files.get(0).toByteArray()));
    }

    @Test
    public void testProcessorELConfigJsonOutput() {
        testRunner.setProperty(CONTACT_POINTS, "${hosts}");
        testRunner.setProperty(CQL_SELECT_QUERY, "${query}");
        testRunner.setProperty(PASSWORD, "${pass}");
        testRunner.setProperty(USERNAME, "${user}");
        testRunner.setProperty(CHARSET, "${charset}");
        testRunner.setProperty(QUERY_TIMEOUT, "${timeout}");
        testRunner.setProperty(FETCH_SIZE, "${fetch}");
        testRunner.setIncomingConnection(false);
        testRunner.assertValid();
        testRunner.setVariable("hosts", "localhost:9042");
        testRunner.setVariable("user", "username");
        testRunner.setVariable("pass", "password");
        testRunner.setVariable("charset", "UTF-8");
        testRunner.setVariable("timeout", "30 sec");
        testRunner.setVariable("fetch", "0");
        // Test JSON output
        testRunner.setProperty(OUTPUT_FORMAT, JSON_FORMAT);
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> files = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertNotNull(files);
        Assert.assertEquals("One file should be transferred to success", 1, files.size());
        Assert.assertEquals(("{\"results\":[{\"user_id\":\"user1\",\"first_name\":\"Joe\",\"last_name\":\"Smith\"," + (((((("\"emails\":[\"jsmith@notareal.com\"],\"top_places\":[\"New York, NY\",\"Santa Clara, CA\"]," + "\"todo\":{\"2016-01-03 05:00:00+0000\":\"Set my alarm \\\"for\\\" a month from now\"},") + "\"registered\":\"false\",\"scale\":1.0,\"metric\":2.0},") + "{\"user_id\":\"user2\",\"first_name\":\"Mary\",\"last_name\":\"Jones\",") + "\"emails\":[\"mjones@notareal.com\"],\"top_places\":[\"Orlando, FL\"],") + "\"todo\":{\"2016-02-03 05:00:00+0000\":\"Get milk and bread\"},") + "\"registered\":\"true\",\"scale\":3.0,\"metric\":4.0}]}")), new String(files.get(0).toByteArray()));
    }

    @Test
    public void testProcessorJsonOutputWithQueryTimeout() {
        setUpStandardProcessorConfig();
        testRunner.setProperty(QUERY_TIMEOUT, "5 sec");
        testRunner.setIncomingConnection(false);
        // Test JSON output
        testRunner.setProperty(OUTPUT_FORMAT, JSON_FORMAT);
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        List<MockFlowFile> files = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        Assert.assertNotNull(files);
        Assert.assertEquals("One file should be transferred to success", 1, files.size());
    }

    @Test
    public void testProcessorEmptyFlowFileAndExceptions() {
        setUpStandardProcessorConfig();
        // Run with empty flowfile
        testRunner.setIncomingConnection(true);
        processor.setExceptionToThrow(null);
        testRunner.enqueue("".getBytes());
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        testRunner.clearTransferState();
        // Test exceptions
        processor.setExceptionToThrow(new NoHostAvailableException(new HashMap<InetSocketAddress, Throwable>()));
        testRunner.enqueue("".getBytes());
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(new com.datastax.driver.core.exceptions.ReadTimeoutException(new InetSocketAddress("localhost", 9042), ConsistencyLevel.ANY, 0, 1, false));
        testRunner.enqueue("".getBytes());
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_RETRY, 1);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(new InvalidQueryException(new InetSocketAddress("localhost", 9042), "invalid query"));
        testRunner.enqueue("".getBytes());
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        testRunner.clearTransferState();
        processor.setExceptionToThrow(new ProcessException());
        testRunner.enqueue("".getBytes());
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testCreateSchemaOneColumn() throws Exception {
        ResultSet rs = CassandraQueryTestUtil.createMockResultSetOneColumn();
        Schema schema = QueryCassandra.createSchema(rs);
        Assert.assertNotNull(schema);
        Assert.assertEquals(schema.getName(), "users");
    }

    @Test
    public void testCreateSchema() throws Exception {
        ResultSet rs = CassandraQueryTestUtil.createMockResultSet();
        Schema schema = QueryCassandra.createSchema(rs);
        Assert.assertNotNull(schema);
        Assert.assertEquals(RECORD, schema.getType());
        // Check record fields, starting with user_id
        Schema.Field field = schema.getField("user_id");
        Assert.assertNotNull(field);
        Schema fieldSchema = field.schema();
        Schema.Type type = fieldSchema.getType();
        Assert.assertEquals(UNION, type);
        // Assert individual union types, first is null
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, fieldSchema.getTypes().get(1).getType());
        field = schema.getField("first_name");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        Assert.assertEquals(UNION, type);
        // Assert individual union types, first is null
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, fieldSchema.getTypes().get(1).getType());
        field = schema.getField("last_name");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        Assert.assertEquals(UNION, type);
        // Assert individual union types, first is null
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, fieldSchema.getTypes().get(1).getType());
        field = schema.getField("emails");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        // Should be a union of null and array
        Assert.assertEquals(UNION, type);
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(ARRAY, fieldSchema.getTypes().get(1).getType());
        Schema arraySchema = fieldSchema.getTypes().get(1);
        // Assert individual array element types are unions of null and String
        Schema elementSchema = arraySchema.getElementType();
        Assert.assertEquals(UNION, elementSchema.getType());
        Assert.assertEquals(NULL, elementSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, elementSchema.getTypes().get(1).getType());
        field = schema.getField("top_places");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        // Should be a union of null and array
        Assert.assertEquals(UNION, type);
        Assert.assertEquals(ARRAY, fieldSchema.getTypes().get(1).getType());
        arraySchema = fieldSchema.getTypes().get(1);
        // Assert individual array element types are unions of null and String
        elementSchema = arraySchema.getElementType();
        Assert.assertEquals(UNION, elementSchema.getType());
        Assert.assertEquals(NULL, elementSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, elementSchema.getTypes().get(1).getType());
        field = schema.getField("todo");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        // Should be a union of null and map
        Assert.assertEquals(UNION, type);
        Assert.assertEquals(MAP, fieldSchema.getTypes().get(1).getType());
        Schema mapSchema = fieldSchema.getTypes().get(1);
        // Assert individual map value types are unions of null and String
        Schema valueSchema = mapSchema.getValueType();
        Assert.assertEquals(NULL, valueSchema.getTypes().get(0).getType());
        Assert.assertEquals(STRING, valueSchema.getTypes().get(1).getType());
        field = schema.getField("registered");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        Assert.assertEquals(UNION, type);
        // Assert individual union types, first is null
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(BOOLEAN, fieldSchema.getTypes().get(1).getType());
        field = schema.getField("scale");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        Assert.assertEquals(UNION, type);
        // Assert individual union types, first is null
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(FLOAT, fieldSchema.getTypes().get(1).getType());
        field = schema.getField("metric");
        Assert.assertNotNull(field);
        fieldSchema = field.schema();
        type = fieldSchema.getType();
        Assert.assertEquals(UNION, type);
        // Assert individual union types, first is null
        Assert.assertEquals(NULL, fieldSchema.getTypes().get(0).getType());
        Assert.assertEquals(DOUBLE, fieldSchema.getTypes().get(1).getType());
    }

    @Test
    public void testConvertToAvroStream() throws Exception {
        ResultSet rs = CassandraQueryTestUtil.createMockResultSet();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long numberOfRows = QueryCassandra.convertToAvroStream(rs, baos, 0, null);
        Assert.assertEquals(2, numberOfRows);
    }

    @Test
    public void testConvertToJSONStream() throws Exception {
        ResultSet rs = CassandraQueryTestUtil.createMockResultSet();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        long numberOfRows = QueryCassandra.convertToJsonStream(rs, baos, StandardCharsets.UTF_8, 0, null);
        Assert.assertEquals(2, numberOfRows);
    }

    /**
     * Provides a stubbed processor instance for testing
     */
    private static class MockQueryCassandra extends QueryCassandra {
        private Exception exceptionToThrow = null;

        @Override
        protected Cluster createCluster(List<InetSocketAddress> contactPoints, SSLContext sslContext, String username, String password) {
            Cluster mockCluster = Mockito.mock(Cluster.class);
            try {
                Metadata mockMetadata = Mockito.mock(Metadata.class);
                Mockito.when(mockMetadata.getClusterName()).thenReturn("cluster1");
                Mockito.when(mockCluster.getMetadata()).thenReturn(mockMetadata);
                Session mockSession = Mockito.mock(Session.class);
                Mockito.when(mockCluster.connect()).thenReturn(mockSession);
                Mockito.when(mockCluster.connect(ArgumentMatchers.anyString())).thenReturn(mockSession);
                Configuration config = Configuration.builder().build();
                Mockito.when(mockCluster.getConfiguration()).thenReturn(config);
                ResultSetFuture future = Mockito.mock(ResultSetFuture.class);
                ResultSet rs = CassandraQueryTestUtil.createMockResultSet();
                Mockito.when(future.getUninterruptibly()).thenReturn(rs);
                try {
                    Mockito.doReturn(rs).when(future).getUninterruptibly(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
                } catch (TimeoutException te) {
                    throw new IllegalArgumentException("Mocked cluster doesn't time out");
                }
                if ((exceptionToThrow) != null) {
                    Mockito.when(mockSession.executeAsync(ArgumentMatchers.anyString())).thenThrow(exceptionToThrow);
                } else {
                    Mockito.when(mockSession.executeAsync(ArgumentMatchers.anyString())).thenReturn(future);
                }
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            return mockCluster;
        }

        public void setExceptionToThrow(Exception e) {
            this.exceptionToThrow = e;
        }
    }
}

