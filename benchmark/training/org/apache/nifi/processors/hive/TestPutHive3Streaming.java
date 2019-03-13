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


import MetastoreConf.ConfVars.THRIFT_URIS;
import PutHive3Streaming.DB_NAME;
import PutHive3Streaming.HIVE_CONFIGURATION_RESOURCES;
import PutHive3Streaming.METASTORE_URI;
import PutHive3Streaming.RECORD_READER;
import PutHive3Streaming.REL_FAILURE;
import PutHive3Streaming.REL_RETRY;
import PutHive3Streaming.REL_SUCCESS;
import PutHive3Streaming.ROLLBACK_ON_FAILURE;
import PutHive3Streaming.TABLE_NAME;
import SecurityUtil.HADOOP_SECURITY_AUTHENTICATION;
import SecurityUtil.KERBEROS;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.apache.avro.Schema;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.ql.io.orc.OrcOutputFormat;
import org.apache.hadoop.hive.ql.metadata.Table;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hive.streaming.ConnectionStats;
import org.apache.hive.streaming.HiveRecordWriter;
import org.apache.hive.streaming.PartitionInfo;
import org.apache.hive.streaming.RecordWriter;
import org.apache.hive.streaming.StreamingConnection;
import org.apache.hive.streaming.StreamingException;
import org.apache.hive.streaming.StubConnectionError;
import org.apache.hive.streaming.StubSerializationError;
import org.apache.hive.streaming.StubStreamingIOFailure;
import org.apache.hive.streaming.StubTransactionError;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.components.ValidationContext;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ControllerServiceInitializationContext;
import org.apache.nifi.kerberos.KerberosCredentialsService;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.RecordReader;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.Record;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.apache.nifi.util.hive.HiveConfigurator;
import org.apache.nifi.util.hive.HiveOptions;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Unit tests for PutHive3Streaming processor.
 */
public class TestPutHive3Streaming {
    private static final String TEST_CONF_PATH = "src/test/resources/core-site.xml";

    private static final String TARGET_HIVE = "target/hive";

    private TestRunner runner;

    private TestPutHive3Streaming.MockPutHive3Streaming processor;

    private HiveConfigurator hiveConfigurator;

    private HiveConf hiveConf;

    private UserGroupInformation ugi;

    private Schema schema;

    @Test
    public void testSetup() throws Exception {
        configure(processor, 0);
        runner.assertNotValid();
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.assertNotValid();
        runner.setProperty(TABLE_NAME, "users");
        runner.assertValid();
        runner.run();
    }

    @Test
    public void testUgiGetsCleared() throws Exception {
        configure(processor, 0);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        processor.ugi = Mockito.mock(UserGroupInformation.class);
        runner.run();
        Assert.assertNull(processor.ugi);
    }

    @Test
    public void testUgiGetsSetIfSecure() throws Exception {
        configure(processor, 1);
        hiveConf.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS);
        KerberosCredentialsService kcs = new TestPutHive3Streaming.MockKerberosCredentialsService();
        runner.addControllerService("kcs", kcs);
        runner.setProperty(PutHive3Streaming.KERBEROS_CREDENTIALS_SERVICE, "kcs");
        runner.enableControllerService(kcs);
        ugi = Mockito.mock(UserGroupInformation.class);
        Mockito.when(hiveConfigurator.authenticate(ArgumentMatchers.eq(hiveConf), ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(ugi);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
    }

    @Test(expected = AssertionError.class)
    public void testSetupWithKerberosAuthFailed() throws Exception {
        configure(processor, 0);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, "src/test/resources/core-site-security.xml, src/test/resources/hive-site-security.xml");
        hiveConf.set(HADOOP_SECURITY_AUTHENTICATION, KERBEROS);
        KerberosCredentialsService kcs = new TestPutHive3Streaming.MockKerberosCredentialsService(null, null);
        runner.addControllerService("kcs", kcs);
        runner.setProperty(PutHive3Streaming.KERBEROS_CREDENTIALS_SERVICE, "kcs");
        runner.enableControllerService(kcs);
        runner.assertNotValid();
        runner.run();
    }

    @Test
    public void onTrigger() throws Exception {
        configure(processor, 1);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerMultipleURIs() throws Exception {
        configure(processor, 1);
        runner.setProperty(METASTORE_URI, "thrift://host1:9083,thrift://host2:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerURIFromConfigFile() throws Exception {
        configure(processor, 1);
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerComplex() throws Exception {
        configureComplex(processor, 10, (-1), null);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        // Schema is an array of size 10, so only one record is output
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerBadInput() throws Exception {
        configure(processor, 1, false, 0);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue("I am not an Avro record".getBytes());
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void onTriggerBadInputRollbackOnFailure() throws Exception {
        configure(processor, 1, false, 0);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
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
    public void onTriggerBadCreate() throws Exception {
        configure(processor, 1, true, 0);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void onTriggerBadCreateRollbackOnFailure() throws Exception {
        configure(processor, 1, true, 0);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        runner.enqueue(new byte[0]);
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
        configure(processor, 3);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
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
        configure(processor, 4);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        processor.setGenerateWriteFailure(true);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_RETRY, 0);
    }

    @Test
    public void onTriggerMultipleRecordsFailInMiddleRollbackOnFailure() throws Exception {
        configure(processor, 3);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        processor.setGenerateWriteFailure(true);
        runner.enqueue(new byte[0]);
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
    public void onTriggerWithConnectFailure() throws Exception {
        configure(processor, 1);
        processor.setGenerateConnectFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
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
    public void onTriggerWithConnectFailureRollbackOnFailure() throws Exception {
        configure(processor, 1);
        processor.setGenerateConnectFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        runner.enqueue(new byte[0]);
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
    public void onTriggerWithWriteFailure() throws Exception {
        configure(processor, 2);
        processor.setGenerateWriteFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertEquals("0", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void onTriggerWithWriteFailureRollbackOnFailure() throws Exception {
        configure(processor, 2);
        processor.setGenerateWriteFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
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
        configure(processor, 1);
        processor.setGenerateSerializationError(true);
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
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void onTriggerWithSerializationErrorRollbackOnFailure() throws Exception {
        configure(processor, 1);
        processor.setGenerateSerializationError(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
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
        configure(processor, 1);
        processor.setGenerateCommitFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(ROLLBACK_ON_FAILURE, "false");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_RETRY, 1);
    }

    @Test
    public void onTriggerWithCommitFailureRollbackOnFailure() throws Exception {
        configure(processor, 1);
        processor.setGenerateCommitFailure(true);
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.setProperty(ROLLBACK_ON_FAILURE, "true");
        runner.enqueue(new byte[0]);
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
    public void testDataTypeConversions() throws Exception {
        final String avroSchema = IOUtils.toString(new FileInputStream("src/test/resources/datatype_test.avsc"), StandardCharsets.UTF_8);
        schema = new Schema.Parser().parse(avroSchema);
        processor.setFields(Arrays.asList(new FieldSchema("uuid", serdeConstants.STRING_TYPE_NAME, "uuid"), new FieldSchema("stringc", serdeConstants.STRING_TYPE_NAME, "stringc"), new FieldSchema("charc", ((serdeConstants.CHAR_TYPE_NAME) + "(1)"), "charc"), new FieldSchema("varcharc", ((serdeConstants.VARCHAR_TYPE_NAME) + "(100)"), "varcharc"), new FieldSchema("intc", serdeConstants.INT_TYPE_NAME, "intc"), new FieldSchema("tinyintc", serdeConstants.TINYINT_TYPE_NAME, "tinyintc"), new FieldSchema("smallintc", serdeConstants.SMALLINT_TYPE_NAME, "smallintc"), new FieldSchema("bigintc", serdeConstants.BIGINT_TYPE_NAME, "bigintc"), new FieldSchema("booleanc", serdeConstants.BOOLEAN_TYPE_NAME, "booleanc"), new FieldSchema("floatc", serdeConstants.FLOAT_TYPE_NAME, "floatc"), new FieldSchema("doublec", serdeConstants.DOUBLE_TYPE_NAME, "doublec"), new FieldSchema("bytesc", serdeConstants.BINARY_TYPE_NAME, "bytesc"), new FieldSchema("listc", ((((serdeConstants.LIST_TYPE_NAME) + "<") + (serdeConstants.STRING_TYPE_NAME)) + ">"), "listc"), new FieldSchema("structc", ((((((((((serdeConstants.STRUCT_TYPE_NAME) + "<sint:") + (serdeConstants.INT_TYPE_NAME)) + ",") + "sboolean:") + (serdeConstants.BOOLEAN_TYPE_NAME)) + ",") + "sstring:") + (serdeConstants.STRING_TYPE_NAME)) + ">"), "structc"), new FieldSchema("mapc", ((((((serdeConstants.MAP_TYPE_NAME) + "<") + (serdeConstants.STRING_TYPE_NAME)) + ",") + (serdeConstants.INT_TYPE_NAME)) + ">"), "mapc"), new FieldSchema("datec", serdeConstants.DATE_TYPE_NAME, "datec"), new FieldSchema("timestampc", serdeConstants.TIMESTAMP_TYPE_NAME, "timestampc"), new FieldSchema("decimalc", ((serdeConstants.DECIMAL_TYPE_NAME) + "(4,2)"), "decimalc"), new FieldSchema("enumc", serdeConstants.STRING_TYPE_NAME, "enumc")));
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, TestPutHive3Streaming.TEST_CONF_PATH);
        MockRecordParser readerFactory = new MockRecordParser();
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        for (final RecordField recordField : recordSchema.getFields()) {
            readerFactory.addSchemaField(recordField.getFieldName(), recordField.getDataType().getFieldType(), recordField.isNullable());
        }
        List<String> enumc = Arrays.asList("SPADES", "HEARTS", "DIAMONDS", "CLUBS");
        Random r = new Random();
        for (int index = 0; index < 10; index++) {
            final int i = index;
            Record structRecord = // Get non-null type in union
            new org.apache.nifi.serialization.record.MapRecord(AvroTypeUtil.createSchema(schema.getField("structc").schema().getTypes().get(1)), new HashMap<String, Object>() {
                {
                    put("sint", (i + 2));// {"name": "sint", "type": "int"},

                    if ((i % 3) == 2) {
                        put("sboolean", null);
                    } else {
                        put("sboolean", ((i % 3) == 1));// {"name": "sboolean", "type": ["null","boolean"]},

                    }
                    put("sstring", "world");// {"name": "sstring", "type": "string"}

                }
            });
            // {"name": "uuid", "type": "string"},
            // {"name": "stringc", "type": "string"},
            // {"name": "intc", "type": "int"},
            // {"name": "tinyintc", "type": ["null", "int"]},
            // {"name": "smallintc", "type": "int"},
            // {"name": "bigintc", "type": "long"},
            // {"name": "booleanc", "type": "boolean"},
            // {"name": "floatc", "type": "floatc"},
            // {"name": "doublec", "type": "double"},
            // {"name": "listc", "type": ["null", {"type": "array", "items": "string"}]},
            // {"name": "enumc", "type": {"type": "enum", "name": "Suit", "symbols": ["SPADES","HEARTS","DIAMONDS","CLUBS"]}}
            readerFactory.addRecord(UUID.randomUUID(), "hello", 'a', "world", i, (i + 1), (i * 10), (i * (Integer.MAX_VALUE)), ((i % 2) == 0), (i * 100.0F), (i * 100.0), "Hello".getBytes(), new String[]{ "a", "b" }, structRecord, new HashMap<String, Integer>() {
                {
                    put("sint1", (i + 2));// {"name": "sint", "type": "int"},

                    put("sint2", i);// {"name": "x", "type": "int"},

                }
            }, new Date(Calendar.getInstance().getTimeInMillis()), Timestamp.from(Instant.now()), ((i * 99.0) / 100), enumc.get(r.nextInt(4)));
        }
        runner.addControllerService("mock-reader-factory", readerFactory);
        runner.enableControllerService(readerFactory);
        runner.setProperty(RECORD_READER, "mock-reader-factory");
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "users");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("10", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.users", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    // logical types
    @Test
    public void testNullDateHandling() throws IOException, InitializationException, MalformedRecordException {
        String schemaText = "{ \"name\":\"test\", \"type\":\"record\", \"fields\":[ { \"name\":\"dob\", \"type\": [ \"null\", { \"type\":\"int\", \"logicalType\":\"date\"  }  ] } ] }";
        schema = new Schema.Parser().parse(schemaText);
        processor.setFields(Arrays.asList(new FieldSchema("dob", serdeConstants.DATE_TYPE_NAME, "null dob")));
        // setup runner
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, TestPutHive3Streaming.TEST_CONF_PATH);
        MockRecordParser readerFactory = new MockRecordParser();
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        for (final RecordField recordField : recordSchema.getFields()) {
            readerFactory.addSchemaField(recordField.getFieldName(), recordField.getDataType().getFieldType(), recordField.isNullable());
        }
        readerFactory.addRecord(new Object[]{ null });
        runner.addControllerService("mock-reader-factory", readerFactory);
        runner.enableControllerService(readerFactory);
        runner.setProperty(RECORD_READER, "mock-reader-factory");
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "dobs");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.dobs", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void testNullTimestampHandling() throws IOException, InitializationException, MalformedRecordException {
        String schemaText = "{ \"name\":\"test\", \"type\":\"record\", \"fields\":[ { \"name\":\"dob\", \"type\": [ \"null\", { \"type\":\"long\", \"logicalType\":\"timestamp-millis\"  }  ] } ] }";
        schema = new Schema.Parser().parse(schemaText);
        processor.setFields(Arrays.asList(new FieldSchema("dob", serdeConstants.TIMESTAMP_TYPE_NAME, "null dob")));
        // setup runner
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, TestPutHive3Streaming.TEST_CONF_PATH);
        MockRecordParser readerFactory = new MockRecordParser();
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        for (final RecordField recordField : recordSchema.getFields()) {
            readerFactory.addSchemaField(recordField.getFieldName(), recordField.getDataType().getFieldType(), recordField.isNullable());
        }
        readerFactory.addRecord(new Object[]{ null });
        runner.addControllerService("mock-reader-factory", readerFactory);
        runner.enableControllerService(readerFactory);
        runner.setProperty(RECORD_READER, "mock-reader-factory");
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "ts");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.ts", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void testNullDecimalHandling() throws IOException, InitializationException, MalformedRecordException {
        String schemaText = "{ \"name\":\"test\", \"type\":\"record\", \"fields\":[ { \"name\":\"amount\", \"type\": [ \"null\", { \"type\":\"bytes\", " + "\"logicalType\":\"decimal\", \"precision\":18, \"scale\":2  }  ] } ] }";
        schema = new Schema.Parser().parse(schemaText);
        processor.setFields(Arrays.asList(new FieldSchema("amount", serdeConstants.DECIMAL_TYPE_NAME, "null amount")));
        // setup runner
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, TestPutHive3Streaming.TEST_CONF_PATH);
        MockRecordParser readerFactory = new MockRecordParser();
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        for (final RecordField recordField : recordSchema.getFields()) {
            readerFactory.addSchemaField(recordField.getFieldName(), recordField.getDataType().getFieldType(), recordField.isNullable());
        }
        readerFactory.addRecord(new Object[]{ null });
        runner.addControllerService("mock-reader-factory", readerFactory);
        runner.enableControllerService(readerFactory);
        runner.setProperty(RECORD_READER, "mock-reader-factory");
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "transactions");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.transactions", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void testNullArrayHandling() throws IOException, InitializationException, MalformedRecordException {
        String schemaText = "{ \"name\":\"test\", \"type\":\"record\", \"fields\":[ { \"name\":\"groups\", \"type\": [ \"null\", { \"type\":\"array\", \"items\":\"string\" }  ] } ] }";
        schema = new Schema.Parser().parse(schemaText);
        processor.setFields(Arrays.asList(new FieldSchema("groups", "array<string>", "null groups")));
        // setup runner
        runner = TestRunners.newTestRunner(processor);
        runner.setProperty(HIVE_CONFIGURATION_RESOURCES, TestPutHive3Streaming.TEST_CONF_PATH);
        MockRecordParser readerFactory = new MockRecordParser();
        final RecordSchema recordSchema = AvroTypeUtil.createSchema(schema);
        for (final RecordField recordField : recordSchema.getFields()) {
            readerFactory.addSchemaField(recordField.getFieldName(), recordField.getDataType().getFieldType(), recordField.isNullable());
        }
        readerFactory.addRecord(new Object[]{ null });
        runner.addControllerService("mock-reader-factory", readerFactory);
        runner.enableControllerService(readerFactory);
        runner.setProperty(RECORD_READER, "mock-reader-factory");
        runner.setProperty(METASTORE_URI, "thrift://localhost:9083");
        runner.setProperty(DB_NAME, "default");
        runner.setProperty(TABLE_NAME, "groups");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals("1", flowFile.getAttribute(PutHive3Streaming.HIVE_STREAMING_RECORD_COUNT_ATTR));
        Assert.assertEquals("default.groups", flowFile.getAttribute(AbstractHive3QLProcessor.ATTR_OUTPUT_TABLES));
    }

    @Test
    public void cleanup() {
        processor.cleanup();
    }

    private class MockPutHive3Streaming extends PutHive3Streaming {
        private boolean generateConnectFailure = false;

        private boolean generateWriteFailure = false;

        private boolean generateSerializationError = false;

        private boolean generateCommitFailure = false;

        private List<FieldSchema> schema = Arrays.asList(new FieldSchema("name", serdeConstants.STRING_TYPE_NAME, ""), new FieldSchema("favorite_number", serdeConstants.INT_TYPE_NAME, ""), new FieldSchema("favorite_color", serdeConstants.STRING_TYPE_NAME, ""), new FieldSchema("scale", serdeConstants.DOUBLE_TYPE_NAME, ""));

        @Override
        StreamingConnection makeStreamingConnection(HiveOptions options, RecordReader reader) throws StreamingException {
            // Test here to ensure the 'hive.metastore.uris' property matches the options.getMetastoreUri() value (if it is set)
            String userDefinedMetastoreURI = options.getMetaStoreURI();
            if (null != userDefinedMetastoreURI) {
                Assert.assertEquals(userDefinedMetastoreURI, options.getHiveConf().get(THRIFT_URIS.getHiveName()));
            }
            if (generateConnectFailure) {
                throw new StubConnectionError("Unit Test - Connection Error");
            }
            HiveRecordWriter hiveRecordWriter = new HiveRecordWriter(reader, getLogger());
            TestPutHive3Streaming.MockHiveStreamingConnection hiveConnection = new TestPutHive3Streaming.MockHiveStreamingConnection(options, reader, hiveRecordWriter, schema);
            hiveConnection.setGenerateWriteFailure(generateWriteFailure);
            hiveConnection.setGenerateSerializationError(generateSerializationError);
            hiveConnection.setGenerateCommitFailure(generateCommitFailure);
            return hiveConnection;
        }

        void setGenerateConnectFailure(boolean generateConnectFailure) {
            this.generateConnectFailure = generateConnectFailure;
        }

        void setGenerateWriteFailure(boolean generateWriteFailure) {
            this.generateWriteFailure = generateWriteFailure;
        }

        void setGenerateSerializationError(boolean generateSerializationError) {
            this.generateSerializationError = generateSerializationError;
        }

        void setGenerateCommitFailure(boolean generateCommitFailure) {
            this.generateCommitFailure = generateCommitFailure;
        }

        void setFields(List<FieldSchema> schema) {
            this.schema = schema;
        }
    }

    private class MockHiveStreamingConnection implements StreamingConnection {
        private boolean generateWriteFailure = false;

        private boolean generateSerializationError = false;

        private boolean generateCommitFailure = false;

        private int writeAttemptCount = 0;

        private ConnectionStats connectionStats;

        private HiveOptions options;

        private RecordWriter writer;

        private HiveConf hiveConf;

        private Table table;

        private String metastoreURI;

        MockHiveStreamingConnection(HiveOptions options, RecordReader reader, RecordWriter recordWriter, List<FieldSchema> schema) {
            this.options = options;
            metastoreURI = options.getMetaStoreURI();
            this.writer = recordWriter;
            this.hiveConf = this.options.getHiveConf();
            connectionStats = new ConnectionStats();
            this.table = new Table(Table.getEmptyTable(options.getDatabaseName(), options.getTableName()));
            this.table.setFields(schema);
            StorageDescriptor sd = this.table.getSd();
            sd.setOutputFormat(OrcOutputFormat.class.getName());
            sd.setLocation(TestPutHive3Streaming.TARGET_HIVE);
        }

        @Override
        public HiveConf getHiveConf() {
            return hiveConf;
        }

        @Override
        public void beginTransaction() throws StreamingException {
            writer.init(this, 0, 100);
        }

        @Override
        public synchronized void write(byte[] record) throws StreamingException {
            throw new UnsupportedOperationException(((this.getClass().getName()) + " does not support writing of records via bytes, only via an InputStream"));
        }

        @Override
        public void write(InputStream inputStream) throws StreamingException {
            try {
                if (generateWriteFailure) {
                    throw new StubStreamingIOFailure("Unit Test - Streaming IO Failure");
                }
                if (generateSerializationError) {
                    throw new StubSerializationError("Unit Test - Serialization error", new Exception());
                }
                this.writer.write(writeAttemptCount, inputStream);
            } finally {
                (writeAttemptCount)++;
            }
        }

        @Override
        public void commitTransaction() throws StreamingException {
            if (generateCommitFailure) {
                throw new StubTransactionError("Unit Test - Commit Failure");
            }
            connectionStats.incrementCommittedTransactions();
        }

        @Override
        public void abortTransaction() throws StreamingException {
            connectionStats.incrementAbortedTransactions();
        }

        @Override
        public void close() {
            // closing the connection shouldn't throw an exception
        }

        @Override
        public ConnectionStats getConnectionStats() {
            return connectionStats;
        }

        public void setGenerateWriteFailure(boolean generateWriteFailure) {
            this.generateWriteFailure = generateWriteFailure;
        }

        public void setGenerateSerializationError(boolean generateSerializationError) {
            this.generateSerializationError = generateSerializationError;
        }

        public void setGenerateCommitFailure(boolean generateCommitFailure) {
            this.generateCommitFailure = generateCommitFailure;
        }

        @Override
        public String getMetastoreUri() {
            return metastoreURI;
        }

        @Override
        public Table getTable() {
            return table;
        }

        @Override
        public List<String> getStaticPartitionValues() {
            return null;
        }

        @Override
        public boolean isPartitionedTable() {
            return false;
        }

        @Override
        public boolean isDynamicPartitioning() {
            return false;
        }

        @Override
        public String getAgentInfo() {
            return null;
        }

        @Override
        public PartitionInfo createPartitionIfNotExists(List<String> list) throws StreamingException {
            return null;
        }
    }

    private static class MockKerberosCredentialsService implements ControllerService , KerberosCredentialsService {
        private String keytab = "src/test/resources/fake.keytab";

        private String principal = "test@REALM.COM";

        public MockKerberosCredentialsService() {
        }

        public MockKerberosCredentialsService(String keytab, String principal) {
            this.keytab = keytab;
            this.principal = principal;
        }

        @Override
        public String getKeytab() {
            return keytab;
        }

        @Override
        public String getPrincipal() {
            return principal;
        }

        @Override
        public void initialize(ControllerServiceInitializationContext context) throws InitializationException {
        }

        @Override
        public Collection<ValidationResult> validate(ValidationContext context) {
            return Collections.EMPTY_LIST;
        }

        @Override
        public PropertyDescriptor getPropertyDescriptor(String name) {
            return null;
        }

        @Override
        public void onPropertyModified(PropertyDescriptor descriptor, String oldValue, String newValue) {
        }

        @Override
        public List<PropertyDescriptor> getPropertyDescriptors() {
            return null;
        }

        @Override
        public String getIdentifier() {
            return "kcs";
        }
    }
}

