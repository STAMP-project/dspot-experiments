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
package org.apache.nifi.processors.mongodb;


import AbstractMongoProcessor.COLLECTION_NAME;
import AbstractMongoProcessor.DATABASE_NAME;
import AbstractMongoProcessor.URI;
import PutMongoRecord.CLIENT_SERVICE;
import PutMongoRecord.RECORD_READER_FACTORY;
import PutMongoRecord.REL_FAILURE;
import PutMongoRecord.REL_SUCCESS;
import PutMongoRecord.WRITE_CONCERN;
import PutMongoRecord.WRITE_CONCERN_UNACKNOWLEDGED;
import RecordFieldType.INT;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import SchemaAccessUtils.SCHEMA_REGISTRY;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.avro.Schema;
import org.apache.nifi.avro.AvroTypeUtil;
import org.apache.nifi.components.ValidationResult;
import org.apache.nifi.json.JsonTreeReader;
import org.apache.nifi.mongodb.MongoDBClientService;
import org.apache.nifi.mongodb.MongoDBControllerService;
import org.apache.nifi.processor.ProcessContext;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.MockSchemaRegistry;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;


public class PutMongoRecordIT extends MongoWriteTestBase {
    private MockRecordParser recordReader;

    @Test
    public void testValidators() throws Exception {
        TestRunner runner = TestRunners.newTestRunner(PutMongoRecord.class);
        runner.addControllerService("reader", recordReader);
        runner.enableControllerService(recordReader);
        Collection<ValidationResult> results;
        ProcessContext pc;
        // missing uri, db, collection, RecordReader
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        results = new HashSet();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(3, results.size());
        Iterator<ValidationResult> it = results.iterator();
        Assert.assertTrue(it.next().toString().contains("is invalid because Mongo Database Name is required"));
        Assert.assertTrue(it.next().toString().contains("is invalid because Mongo Collection Name is required"));
        Assert.assertTrue(it.next().toString().contains("is invalid because Record Reader is required"));
        // invalid write concern
        runner.setProperty(URI, MongoWriteTestBase.MONGO_URI);
        runner.setProperty(AbstractMongoProcessor.DATABASE_NAME, DATABASE_NAME);
        runner.setProperty(AbstractMongoProcessor.COLLECTION_NAME, MongoWriteTestBase.COLLECTION_NAME);
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.setProperty(WRITE_CONCERN, "xyz");
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        results = new HashSet();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(1, results.size());
        Assert.assertTrue(results.iterator().next().toString().matches("'Write Concern' .* is invalid because Given value not found in allowed set .*"));
        // valid write concern
        runner.setProperty(WRITE_CONCERN, WRITE_CONCERN_UNACKNOWLEDGED);
        runner.enqueue(new byte[0]);
        pc = runner.getProcessContext();
        results = new HashSet();
        if (pc instanceof MockProcessContext) {
            results = validate();
        }
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testInsertFlatRecords() throws Exception {
        TestRunner runner = init();
        recordReader.addSchemaField("name", STRING);
        recordReader.addSchemaField("age", INT);
        recordReader.addSchemaField("sport", STRING);
        recordReader.addRecord("John Doe", 48, "Soccer");
        recordReader.addRecord("Jane Doe", 47, "Tennis");
        recordReader.addRecord("Sally Doe", 47, "Curling");
        recordReader.addRecord("Jimmy Doe", 14, null);
        recordReader.addRecord("Pizza Doe", 14, null);
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        // verify 1 doc inserted into the collection
        Assert.assertEquals(5, collection.count());
        // assertEquals(doc, collection.find().first());
        runner.clearTransferState();
        /* Test it with the client service. */
        MongoDBClientService clientService = new MongoDBControllerService();
        runner.addControllerService("clientService", clientService);
        runner.removeProperty(PutMongoRecord.URI);
        runner.setProperty(clientService, MongoDBControllerService.URI, MongoWriteTestBase.MONGO_URI);
        runner.setProperty(CLIENT_SERVICE, "clientService");
        runner.enableControllerService(clientService);
        runner.assertValid();
        collection.deleteMany(new Document());
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertEquals(5, collection.count());
    }

    @Test
    public void testInsertNestedRecords() throws Exception {
        TestRunner runner = init();
        recordReader.addSchemaField("id", INT);
        final List<RecordField> personFields = new ArrayList<>();
        final RecordField nameField = new RecordField("name", STRING.getDataType());
        final RecordField ageField = new RecordField("age", INT.getDataType());
        final RecordField sportField = new RecordField("sport", STRING.getDataType());
        personFields.add(nameField);
        personFields.add(ageField);
        personFields.add(sportField);
        final RecordSchema personSchema = new org.apache.nifi.serialization.SimpleRecordSchema(personFields);
        recordReader.addSchemaField("person", RECORD);
        recordReader.addRecord(1, new org.apache.nifi.serialization.record.MapRecord(personSchema, new HashMap<String, Object>() {
            {
                put("name", "John Doe");
                put("age", 48);
                put("sport", "Soccer");
            }
        }));
        recordReader.addRecord(2, new org.apache.nifi.serialization.record.MapRecord(personSchema, new HashMap<String, Object>() {
            {
                put("name", "Jane Doe");
                put("age", 47);
                put("sport", "Tennis");
            }
        }));
        recordReader.addRecord(3, new org.apache.nifi.serialization.record.MapRecord(personSchema, new HashMap<String, Object>() {
            {
                put("name", "Sally Doe");
                put("age", 47);
                put("sport", "Curling");
            }
        }));
        recordReader.addRecord(4, new org.apache.nifi.serialization.record.MapRecord(personSchema, new HashMap<String, Object>() {
            {
                put("name", "Jimmy Doe");
                put("age", 14);
                put("sport", null);
            }
        }));
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        // verify 1 doc inserted into the collection
        Assert.assertEquals(4, collection.count());
        // assertEquals(doc, collection.find().first());
    }

    @Test
    public void testArrayConversion() throws Exception {
        TestRunner runner = init(PutMongoRecord.class);
        MockSchemaRegistry registry = new MockSchemaRegistry();
        String rawSchema = "{\"type\":\"record\",\"name\":\"Test\",\"fields\":[{\"name\":\"name\",\"type\":\"string\"}," + "{\"name\":\"arrayTest\",\"type\":{\"type\":\"array\",\"items\":\"string\"}}]}";
        RecordSchema schema = AvroTypeUtil.createSchema(new Schema.Parser().parse(rawSchema));
        registry.addSchema("test", schema);
        JsonTreeReader reader = new JsonTreeReader();
        runner.addControllerService("registry", registry);
        runner.addControllerService("reader", reader);
        runner.setProperty(reader, SCHEMA_REGISTRY, "registry");
        runner.setProperty(RECORD_READER_FACTORY, "reader");
        runner.enableControllerService(registry);
        runner.enableControllerService(reader);
        runner.assertValid();
        Map<String, String> attrs = new HashMap<>();
        attrs.put("schema.name", "test");
        runner.enqueue("{\"name\":\"John Smith\",\"arrayTest\":[\"a\",\"b\",\"c\"]}", attrs);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
    }
}

