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


import PutCassandraRecord.BATCH_STATEMENT_TYPE;
import PutCassandraRecord.CONSISTENCY_LEVEL;
import PutCassandraRecord.CONTACT_POINTS;
import PutCassandraRecord.KEYSPACE;
import PutCassandraRecord.PASSWORD;
import PutCassandraRecord.REL_SUCCESS;
import PutCassandraRecord.TABLE;
import PutCassandraRecord.USERNAME;
import RecordFieldType.INT;
import RecordFieldType.STRING;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Configuration;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.net.ssl.SSLContext;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PutCassandraRecordTest {
    private TestRunner testRunner;

    private MockRecordParser recordReader;

    @Test
    public void testProcessorConfigValidity() throws InitializationException {
        testRunner.setProperty(CONTACT_POINTS, "localhost:9042");
        testRunner.assertNotValid();
        testRunner.setProperty(PASSWORD, "password");
        testRunner.assertNotValid();
        testRunner.setProperty(USERNAME, "username");
        testRunner.assertNotValid();
        testRunner.setProperty(CONSISTENCY_LEVEL, "SERIAL");
        testRunner.assertNotValid();
        testRunner.setProperty(BATCH_STATEMENT_TYPE, "LOGGED");
        testRunner.assertNotValid();
        testRunner.setProperty(KEYSPACE, "sampleks");
        testRunner.assertNotValid();
        testRunner.setProperty(TABLE, "sampletbl");
        testRunner.assertNotValid();
        testRunner.addControllerService("reader", recordReader);
        testRunner.enableControllerService(recordReader);
        testRunner.assertValid();
    }

    @Test
    public void testSimplePut() throws InitializationException {
        setUpStandardTestConfig();
        recordReader.addSchemaField("name", STRING);
        recordReader.addSchemaField("age", INT);
        recordReader.addSchemaField("sport", STRING);
        recordReader.addRecord("John Doe", 48, "Soccer");
        recordReader.addRecord("Jane Doe", 47, "Tennis");
        recordReader.addRecord("Sally Doe", 47, "Curling");
        recordReader.addRecord("Jimmy Doe", 14, null);
        recordReader.addRecord("Pizza Doe", 14, null);
        testRunner.enqueue("");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    @Test
    public void testEL() throws InitializationException {
        testRunner.setProperty(CONTACT_POINTS, "${contact.points}");
        testRunner.setProperty(PASSWORD, "${pass}");
        testRunner.setProperty(USERNAME, "${user}");
        testRunner.setProperty(CONSISTENCY_LEVEL, "SERIAL");
        testRunner.setProperty(BATCH_STATEMENT_TYPE, "LOGGED");
        testRunner.setProperty(TABLE, "sampleks.sampletbl");
        testRunner.addControllerService("reader", recordReader);
        testRunner.enableControllerService(recordReader);
        testRunner.assertValid();
        testRunner.setVariable("contact.points", "localhost:9042");
        testRunner.setVariable("user", "username");
        testRunner.setVariable("pass", "password");
        recordReader.addSchemaField("name", STRING);
        recordReader.addSchemaField("age", INT);
        recordReader.addSchemaField("sport", STRING);
        recordReader.addRecord("John Doe", 48, "Soccer");
        recordReader.addRecord("Jane Doe", 47, "Tennis");
        recordReader.addRecord("Sally Doe", 47, "Curling");
        recordReader.addRecord("Jimmy Doe", 14, null);
        recordReader.addRecord("Pizza Doe", 14, null);
        testRunner.enqueue("");
        testRunner.run(1, true, true);
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
    }

    private static class MockPutCassandraRecord extends PutCassandraRecord {
        private Exception exceptionToThrow = null;

        private Session mockSession = Mockito.mock(Session.class);

        @Override
        protected Cluster createCluster(List<InetSocketAddress> contactPoints, SSLContext sslContext, String username, String password) {
            Cluster mockCluster = Mockito.mock(Cluster.class);
            try {
                Metadata mockMetadata = Mockito.mock(Metadata.class);
                Mockito.when(mockMetadata.getClusterName()).thenReturn("cluster1");
                Mockito.when(mockCluster.getMetadata()).thenReturn(mockMetadata);
                Mockito.when(mockCluster.connect()).thenReturn(mockSession);
                Mockito.when(mockCluster.connect(ArgumentMatchers.anyString())).thenReturn(mockSession);
                Configuration config = Configuration.builder().build();
                Mockito.when(mockCluster.getConfiguration()).thenReturn(config);
                ResultSetFuture future = Mockito.mock(ResultSetFuture.class);
                ResultSet rs = CassandraQueryTestUtil.createMockResultSet();
                PreparedStatement ps = Mockito.mock(PreparedStatement.class);
                Mockito.when(mockSession.prepare(ArgumentMatchers.anyString())).thenReturn(ps);
                BoundStatement bs = Mockito.mock(BoundStatement.class);
                Mockito.when(ps.bind()).thenReturn(bs);
                Mockito.when(future.getUninterruptibly()).thenReturn(rs);
                try {
                    Mockito.doReturn(rs).when(future).getUninterruptibly(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class));
                } catch (TimeoutException te) {
                    throw new IllegalArgumentException("Mocked cluster doesn't time out");
                }
                if ((exceptionToThrow) != null) {
                    Mockito.doThrow(exceptionToThrow).when(mockSession).executeAsync(ArgumentMatchers.anyString());
                    Mockito.doThrow(exceptionToThrow).when(mockSession).executeAsync(ArgumentMatchers.any(Statement.class));
                } else {
                    Mockito.when(mockSession.executeAsync(ArgumentMatchers.anyString())).thenReturn(future);
                    Mockito.when(mockSession.executeAsync(ArgumentMatchers.any(Statement.class))).thenReturn(future);
                }
                Mockito.when(mockSession.getCluster()).thenReturn(mockCluster);
            } catch (Exception e) {
                Assert.fail(e.getMessage());
            }
            return mockCluster;
        }
    }
}

