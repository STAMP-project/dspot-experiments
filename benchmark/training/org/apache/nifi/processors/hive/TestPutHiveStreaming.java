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
package org.apache.nifi.processors.hive;


import HiveWriter.ConnectFailure;
import PutHiveStreaming.AUTOCREATE_PARTITIONS;
import PutHiveStreaming.DB_NAME;
import PutHiveStreaming.HEARTBEAT_INTERVAL;
import PutHiveStreaming.HIVE_CONFIGURATION_RESOURCES;
import PutHiveStreaming.METASTORE_URI;
import PutHiveStreaming.PARTITION_COLUMNS;
import PutHiveStreaming.RECORDS_PER_TXN;
import PutHiveStreaming.REL_FAILURE;
import PutHiveStreaming.REL_RETRY;
import PutHiveStreaming.REL_SUCCESS;
import PutHiveStreaming.ROLLBACK_ON_FAILURE;
import PutHiveStreaming.TABLE_NAME;
import PutHiveStreaming.TXNS_PER_BATCH;
import SecurityUtil.HADOOP_SECURITY_AUTHENTICATION;
import SecurityUtil.KERBEROS;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.hcatalog.streaming.HiveEndPoint;
import org.apache.hive.hcatalog.streaming.RecordWriter;
import org.apache.hive.hcatalog.streaming.SerializationError;
import org.apache.hive.hcatalog.streaming.StreamingConnection;
import org.apache.hive.hcatalog.streaming.StreamingException;
import org.apache.hive.hcatalog.streaming.TransactionBatch;
import org.apache.nifi.hadoop.KerberosProperties;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.hive.AuthenticationFailedException;
import org.apache.nifi.util.hive.HiveConfigurator;
import org.apache.nifi.util.hive.HiveOptions;
import org.apache.nifi.util.hive.HiveWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for PutHiveStreaming processor.
 */
public class TestPutHiveStreaming {
    private TestRunner runner;

    private TestPutHiveStreaming.MockPutHiveStreaming processor;

    private KerberosProperties kerberosPropsWithFile;

    private HiveConfigurator hiveConfigurator;

    private HiveConf hiveConf;

    private UserGroupInformation ugi;

    @Test
    public void testSetup() throws Exception {
        runner.assertNotValid();
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.assertNotValid();
        runner.setProperty(TABLE_NAME, "users");
        runner.assertValid();
        runner.run();
    }

    @Test
    public void testUgiGetsCleared() {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        processor.ugi = Mockito.mock(UserGroupInformation.class);
        runner.run();
        Assert.assertNull(processor.ugi);
    }

    @Test
    public void testUgiGetsSetIfSecure() throws IOException, AuthenticationFailedException {
        Mockito.when(hiveConf.get(HADOOP_SECURITY_AUTHENTICATION)).thenReturn(KERBEROS);
        ugi = Mockito.mock(UserGroupInformation.class);
        Mockito.when(hiveConfigurator.authenticate(ArgumentMatchers.eq(hiveConf), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(ugi);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
    }

    @Test
    public void testSetupBadPartitionColumns() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.assertValid();
        runner.setProperty(PARTITION_COLUMNS, "favorite_number,,");
        runner.setProperty(AUTOCREATE_PARTITIONS, "true");
        runner.assertNotValid();
    }

    @Test(expected = AssertionError.class)
    public void testSetupWithKerberosAuthFailed() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, "src/test/resources/core-site-security.xml, src/test/resources/hive-site-security.xml");
        runner.setProperty(kerberosPropsWithFile.getKerberosPrincipal(), "test@REALM");
        runner.setProperty(kerberosPropsWithFile.getKerberosKeytab(), "src/test/resources/fake.keytab");
        runner.run();
    }

    @Test
    public void testSingleBatchInvalid() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "2");
        runner.assertValid();
        runner.setProperty(TXNS_PER_BATCH, "1");
        runner.assertNotValid();
    }

    @Test
    public void onTrigger() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHiveStreaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHiveQLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerBadInput() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.enqueue("I am not an Avro record".getBytes());
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void onTriggerBadInputRollbackOnFailure() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        runner.enqueue("I am not an Avro record".getBytes());
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_FAILURE, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerMultipleRecordsSingleTransaction() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(RECORDS_PER_TXN, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        Map<String, Object> user3 = new HashMap<String, Object>() {
            {
                put("name", "Matt");
                put("favorite_number", 3);
            }
        };
        final List<Map<String, Object>> users = Arrays.asList(user1, user2, user3);
        runner.enqueue(createAvroRecord(users));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile resultFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        assertOutputAvroRecords(users, resultFlowFile);
    }

    @Test
    public void onTriggerMultipleRecordsMultipleTransaction() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(RECORDS_PER_TXN, "2");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        Map<String, Object> user3 = new HashMap<String, Object>() {
            {
                put("name", "Matt");
                put("favorite_number", 3);
            }
        };
        final List<Map<String, Object>> users = Arrays.asList(user1, user2, user3);
        runner.enqueue(createAvroRecord(users));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile resultFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        assertOutputAvroRecords(users, resultFlowFile);
    }

    @Test
    public void onTriggerMultipleRecordsFailInMiddle() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(RECORDS_PER_TXN, "2");
        processor.setGenerateWriteFailure(true, 1);
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        Map<String, Object> user3 = new HashMap<String, Object>() {
            {
                put("name", "Matt");
                put("favorite_number", 3);
            }
        };
        Map<String, Object> user4 = new HashMap<String, Object>() {
            {
                put("name", "Mike");
                put("favorite_number", 345);
            }
        };
        final List<Map<String, Object>> users = Arrays.asList(user1, user2, user3, user4);
        runner.enqueue(createAvroRecord(users));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        MockFlowFile resultFlowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        assertOutputAvroRecords(Arrays.asList(user1, user3, user4), resultFlowFile);
        final MockFlowFile failedFlowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        assertOutputAvroRecords(Arrays.asList(user2), failedFlowFile);
    }

    @Test
    public void onTriggerMultipleRecordsFailInMiddleRollbackOnFailure() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(RECORDS_PER_TXN, "2");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        processor.setGenerateWriteFailure(true, 1);
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        Map<String, Object> user3 = new HashMap<String, Object>() {
            {
                put("name", "Matt");
                put("favorite_number", 3);
            }
        };
        runner.enqueue(createAvroRecord(Arrays.asList(user1, user2, user3)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown, because any Hive Transaction is committed yet.");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerMultipleRecordsFailInMiddleRollbackOnFailureCommitted() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(RECORDS_PER_TXN, "2");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        // The first two records are committed, then an issue will happen at the 3rd record.
        processor.setGenerateWriteFailure(true, 2);
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        Map<String, Object> user3 = new HashMap<String, Object>() {
            {
                put("name", "Matt");
                put("favorite_number", 3);
            }
        };
        Map<String, Object> user4 = new HashMap<String, Object>() {
            {
                put("name", "Mike");
                put("favorite_number", 345);
            }
        };
        runner.enqueue(createAvroRecord(Arrays.asList(user1, user2, user3, user4)));
        // ProcessException should NOT be thrown, because a Hive Transaction is already committed.
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        // Assert transferred FlowFile.
        assertOutputAvroRecords(Arrays.asList(user1, user2), runner.getFlowFilesForRelationship(PutHiveStreaming.REL_SUCCESS).get(0));
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithPartitionColumns() throws Exception {
        runner.setVariable("metastore", "thrift://localhost:9083");
        runner.setVariable("database", "default");
        runner.setVariable("table", "users");
        runner.setVariable("partitions", "favorite_number, favorite_color");
        runner.setProperty(METASTORE_URI, "${metastore}");
        runner.setProperty(DB_NAME, "${database}");
        runner.setProperty(TABLE_NAME, "${table}");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(PARTITION_COLUMNS, "${partitions}");
        runner.setProperty(AUTOCREATE_PARTITIONS, "true");
        runner.assertValid();
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
                put("favorite_color", "blue");
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHiveStreaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHiveQLProcessor.ATTR_OUTPUT_TABLES));
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_RETRY, 0);
    }

    @Test
    public void onTriggerWithPartitionColumnsNotInRecord() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(PARTITION_COLUMNS, "favorite_food");
        runner.setProperty(AUTOCREATE_PARTITIONS, "false");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
                put("favorite_color", "blue");
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 0);
    }

    @Test
    public void onTriggerWithPartitionColumnsNotInRecordRollbackOnFailure() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(PARTITION_COLUMNS, "favorite_food");
        runner.setProperty(AUTOCREATE_PARTITIONS, "false");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
                put("favorite_color", "blue");
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithRetireWriters() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "2");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        for (int i = 0; i < 10; i++) {
            runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        }
        runner.run(10);
        runner.assertTransferCount(REL_SUCCESS, 10);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_RETRY, 0);
    }

    @Test
    public void onTriggerWithHeartbeat() throws Exception {
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(HEARTBEAT_INTERVAL, "1");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run(1, false);
        // Wait for a heartbeat
        Thread.sleep(1000);
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run(1, true);
        runner.assertTransferCount(REL_SUCCESS, 2);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_RETRY, 0);
    }

    @Test
    public void onTriggerWithConnectFailure() throws Exception {
        processor.setGenerateConnectFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_RETRY, 1);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void onTriggerWithConnectFailureRollbackOnFailure() throws Exception {
        processor.setGenerateConnectFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_RETRY, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithInterruptedException() throws Exception {
        processor.setGenerateInterruptedExceptionOnCreateWriter(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_RETRY, 1);
    }

    @Test
    public void onTriggerWithInterruptedExceptionRollbackOnFailure() throws Exception {
        processor.setGenerateInterruptedExceptionOnCreateWriter(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_RETRY, 0);
    }

    @Test
    public void onTriggerWithWriteFailure() throws Exception {
        processor.setGenerateWriteFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        runner.enqueue(createAvroRecord(Arrays.asList(user1, user2)));
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertEquals("2", flowFile.getAttribute(PutHiveStreaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHiveQLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerWithWriteFailureRollbackOnFailure() throws Exception {
        processor.setGenerateWriteFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        Map<String, Object> user2 = new HashMap<String, Object>() {
            {
                put("name", "Mary");
                put("favorite_number", 42);
            }
        };
        runner.enqueue(createAvroRecord(Arrays.asList(user1, user2)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_FAILURE, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithSerializationError() throws Exception {
        processor.setGenerateSerializationError(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void onTriggerWithSerializationErrorRollbackOnFailure() throws Exception {
        processor.setGenerateSerializationError(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithCommitFailure() throws Exception {
        processor.setGenerateCommitFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 1);
    }

    @Test
    public void onTriggerWithCommitFailureRollbackOnFailure() throws Exception {
        processor.setGenerateCommitFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithTransactionFailure() throws Exception {
        processor.setGenerateTransactionFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 1);
    }

    @Test
    public void onTriggerWithTransactionFailureRollbackOnFailure() throws Exception {
        processor.setGenerateTransactionFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        try {
            runner.run();
            Assert.fail("ProcessException should be thrown");
        } catch (AssertionError e) {
            Assert.assertTrue(((e.getCause()) instanceof ProcessException));
        }
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 0);
        // Assert incoming FlowFile stays in input queue.
        Assert.assertEquals(1, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void onTriggerWithExceptionOnFlushAndClose() throws Exception {
        processor.setGenerateExceptionOnFlushAndClose(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(TXNS_PER_BATCH, "100");
        Map<String, Object> user1 = new HashMap<String, Object>() {
            {
                put("name", "Joe");
                put("favorite_number", 146);
            }
        };
        runner.enqueue(createAvroRecord(Collections.singletonList(user1)));
        runner.run();
    }

    @Test
    public void cleanup() throws Exception {
        processor.cleanup();
    }

    private class MockPutHiveStreaming extends PutHiveStreaming {
        private KerberosProperties kerberosProperties;

        private boolean generateConnectFailure = false;

        private boolean generateInterruptedExceptionOnCreateWriter = false;

        private boolean generateWriteFailure = false;

        private Integer generateWriteFailureRecordIndex;

        private boolean generateSerializationError = false;

        private boolean generateCommitFailure = false;

        private boolean generateTransactionFailure = false;

        private boolean generateExceptionOnFlushAndClose = false;

        private HiveEndPoint hiveEndPoint = Mockito.mock(HiveEndPoint.class);

        @Override
        public KerberosProperties getKerberosProperties() {
            return this.kerberosProperties;
        }

        public void setKerberosProperties(KerberosProperties kerberosProperties) {
            this.kerberosProperties = kerberosProperties;
        }

        @Override
        public HiveEndPoint makeHiveEndPoint(List<String> partitionValues, HiveOptions hiveOptions) {
            return hiveEndPoint;
        }

        @Override
        protected HiveWriter makeHiveWriter(HiveEndPoint endPoint, ExecutorService callTimeoutPool, UserGroupInformation ugi, HiveOptions options) throws ConnectFailure, InterruptedException {
            if (generateConnectFailure) {
                throw new HiveWriter.ConnectFailure(endPoint, new Exception());
            }
            if (generateInterruptedExceptionOnCreateWriter) {
                throw new InterruptedException();
            }
            TestPutHiveStreaming.MockHiveWriter hiveWriter = new TestPutHiveStreaming.MockHiveWriter(endPoint, options.getTxnsPerBatch(), options.getAutoCreatePartitions(), options.getCallTimeOut(), callTimeoutPool, ugi, hiveConfig);
            hiveWriter.setGenerateWriteFailure(generateWriteFailure, generateWriteFailureRecordIndex);
            hiveWriter.setGenerateSerializationError(generateSerializationError);
            hiveWriter.setGenerateCommitFailure(generateCommitFailure);
            hiveWriter.setGenerateTransactionFailure(generateTransactionFailure);
            hiveWriter.setGenerateExceptionOnFlushAndClose(generateExceptionOnFlushAndClose);
            return hiveWriter;
        }

        public void setGenerateConnectFailure(boolean generateConnectFailure) {
            this.generateConnectFailure = generateConnectFailure;
        }

        public void setGenerateInterruptedExceptionOnCreateWriter(boolean generateInterruptedExceptionOnCreateWriter) {
            this.generateInterruptedExceptionOnCreateWriter = generateInterruptedExceptionOnCreateWriter;
        }

        public void setGenerateWriteFailure(boolean generateWriteFailure) {
            this.generateWriteFailure = generateWriteFailure;
        }

        public void setGenerateWriteFailure(boolean generateWriteFailure, int generateWriteFailureRecordIndex) {
            this.generateWriteFailure = generateWriteFailure;
            this.generateWriteFailureRecordIndex = generateWriteFailureRecordIndex;
        }

        public void setGenerateSerializationError(boolean generateSerializationError) {
            this.generateSerializationError = generateSerializationError;
        }

        public void setGenerateCommitFailure(boolean generateCommitFailure) {
            this.generateCommitFailure = generateCommitFailure;
        }

        public void setGenerateTransactionFailure(boolean generateTransactionFailure) {
            this.generateTransactionFailure = generateTransactionFailure;
        }

        public void setGenerateExceptionOnFlushAndClose(boolean generateExceptionOnFlushAndClose) {
            this.generateExceptionOnFlushAndClose = generateExceptionOnFlushAndClose;
        }
    }

    private class MockHiveWriter extends HiveWriter {
        private boolean generateWriteFailure = false;

        private Integer generateWriteFailureRecordIndex;

        private boolean generateSerializationError = false;

        private boolean generateCommitFailure = false;

        private boolean generateTransactionFailure = false;

        private boolean generateExceptionOnFlushAndClose = false;

        private int writeAttemptCount = 0;

        private int totalRecords = 0;

        private HiveEndPoint endPoint;

        public MockHiveWriter(HiveEndPoint endPoint, int txnsPerBatch, boolean autoCreatePartitions, long callTimeout, ExecutorService callTimeoutPool, UserGroupInformation ugi, HiveConf hiveConf) throws InterruptedException, ConnectFailure {
            super(endPoint, txnsPerBatch, autoCreatePartitions, callTimeout, callTimeoutPool, ugi, hiveConf);
            Assert.assertEquals(TestPutHiveStreaming.this.ugi, ugi);
            this.endPoint = endPoint;
        }

        @Override
        public synchronized void write(byte[] record) throws InterruptedException, SerializationError, WriteFailure {
            try {
                if ((generateWriteFailure) && (((generateWriteFailureRecordIndex) == null) || ((writeAttemptCount) == (generateWriteFailureRecordIndex)))) {
                    throw new WriteFailure(endPoint, 1L, new Exception());
                }
                if (generateSerializationError) {
                    throw new SerializationError("Test Serialization Error", new Exception());
                }
                (totalRecords)++;
            } finally {
                (writeAttemptCount)++;
            }
        }

        public void setGenerateWriteFailure(boolean generateWriteFailure, Integer generateWriteFailureRecordIndex) {
            this.generateWriteFailure = generateWriteFailure;
            this.generateWriteFailureRecordIndex = generateWriteFailureRecordIndex;
        }

        public void setGenerateSerializationError(boolean generateSerializationError) {
            this.generateSerializationError = generateSerializationError;
        }

        public void setGenerateCommitFailure(boolean generateCommitFailure) {
            this.generateCommitFailure = generateCommitFailure;
        }

        public void setGenerateTransactionFailure(boolean generateTransactionFailure) {
            this.generateTransactionFailure = generateTransactionFailure;
        }

        public void setGenerateExceptionOnFlushAndClose(boolean generateExceptionOnFlushAndClose) {
            this.generateExceptionOnFlushAndClose = generateExceptionOnFlushAndClose;
        }

        @Override
        protected RecordWriter getRecordWriter(HiveEndPoint endPoint, UserGroupInformation ugi, HiveConf conf) throws StreamingException {
            Assert.assertEquals(hiveConf, conf);
            return Mockito.mock(RecordWriter.class);
        }

        @Override
        protected StreamingConnection newConnection(HiveEndPoint endPoint, boolean autoCreatePartitions, HiveConf conf, UserGroupInformation ugi) throws InterruptedException, ConnectFailure {
            StreamingConnection connection = Mockito.mock(StreamingConnection.class);
            Assert.assertEquals(hiveConf, conf);
            return connection;
        }

        @Override
        public void flush(boolean rollToNext) throws InterruptedException, CommitFailure, TxnBatchFailure, TxnFailure {
            if (generateCommitFailure) {
                throw new HiveWriter.CommitFailure(endPoint, 1L, new Exception());
            }
            if (generateTransactionFailure) {
                throw new HiveWriter.TxnFailure(Mockito.mock(TransactionBatch.class), new Exception());
            }
        }

        @Override
        public void heartBeat() throws InterruptedException {
        }

        @Override
        public void flushAndClose() throws IOException, InterruptedException, CommitFailure, TxnBatchFailure, TxnFailure {
            if (generateExceptionOnFlushAndClose) {
                throw new IOException();
            }
        }

        @Override
        public void close() throws IOException, InterruptedException {
        }

        @Override
        public void abort() throws InterruptedException, StreamingException, TxnBatchFailure {
        }

        @Override
        protected void closeConnection() throws InterruptedException {
            // Empty
        }

        @Override
        protected void commitTxn() throws InterruptedException, CommitFailure {
            // Empty
        }

        @Override
        protected TransactionBatch nextTxnBatch(RecordWriter recordWriter) throws InterruptedException, TxnBatchFailure {
            TransactionBatch txnBatch = Mockito.mock(TransactionBatch.class);
            return txnBatch;
        }

        @Override
        protected void closeTxnBatch() throws InterruptedException {
            // Empty
        }

        @Override
        protected void abortTxn() throws InterruptedException {
            // Empty
        }

        @Override
        protected void nextTxn(boolean rollToNext) throws InterruptedException, StreamingException, TxnBatchFailure {
            // Empty
        }

        @Override
        public int getTotalRecords() {
            return totalRecords;
        }
    }
}

