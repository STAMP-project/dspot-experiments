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
package org.apache.nifi.processors.standard;


import ConvertRecord.INCLUDE_ZERO_RECORD_FLOWFILES;
import ConvertRecord.RECORD_READER;
import ConvertRecord.RECORD_WRITER;
import ConvertRecord.REL_FAILURE;
import ConvertRecord.REL_SUCCESS;
import RecordFieldType.INT;
import RecordFieldType.STRING;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import SchemaAccessUtils.SCHEMA_TEXT;
import SchemaAccessUtils.SCHEMA_TEXT_PROPERTY;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.nifi.json.JsonRecordSetWriter;
import org.apache.nifi.json.JsonTreeReader;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.xerial.snappy.SnappyInputStream;


public class TestConvertRecord {
    @Test
    public void testSuccessfulConversion() throws InitializationException {
        final MockRecordParser readerService = new MockRecordParser();
        final MockRecordWriter writerService = new MockRecordWriter("header", false);
        final TestRunner runner = TestRunners.newTestRunner(ConvertRecord.class);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        readerService.addSchemaField("name", STRING);
        readerService.addSchemaField("age", INT);
        readerService.addRecord("John Doe", 48);
        readerService.addRecord("Jane Doe", 47);
        readerService.addRecord("Jimmy Doe", 14);
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertAttributeEquals("record.count", "3");
        out.assertAttributeEquals("mime.type", "text/plain");
        out.assertContentEquals("header\nJohn Doe,48\nJane Doe,47\nJimmy Doe,14\n");
    }

    @Test
    public void testDropEmpty() throws InitializationException {
        final MockRecordParser readerService = new MockRecordParser();
        final MockRecordWriter writerService = new MockRecordWriter("header", false);
        final TestRunner runner = TestRunners.newTestRunner(ConvertRecord.class);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(INCLUDE_ZERO_RECORD_FLOWFILES, "false");
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        readerService.addSchemaField("name", STRING);
        readerService.addSchemaField("age", INT);
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 0);
        readerService.addRecord("John Doe", 48);
        readerService.addRecord("Jane Doe", 47);
        readerService.addRecord("Jimmy Doe", 14);
        runner.enqueue("");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertAttributeEquals("record.count", "3");
        out.assertAttributeEquals("mime.type", "text/plain");
        out.assertContentEquals("header\nJohn Doe,48\nJane Doe,47\nJimmy Doe,14\n");
    }

    @Test
    public void testReadFailure() throws InitializationException {
        final MockRecordParser readerService = new MockRecordParser(2);
        final MockRecordWriter writerService = new MockRecordWriter("header", false);
        final TestRunner runner = TestRunners.newTestRunner(ConvertRecord.class);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        readerService.addSchemaField("name", STRING);
        readerService.addSchemaField("age", INT);
        readerService.addRecord("John Doe", 48);
        readerService.addRecord("Jane Doe", 47);
        readerService.addRecord("Jimmy Doe", 14);
        final MockFlowFile original = runner.enqueue("hello");
        runner.run();
        // Original FlowFile should be routed to 'failure' relationship without modification
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertTrue((original == out));
    }

    @Test
    public void testWriteFailure() throws InitializationException {
        final MockRecordParser readerService = new MockRecordParser();
        final MockRecordWriter writerService = new MockRecordWriter("header", false, 2);
        final TestRunner runner = TestRunners.newTestRunner(ConvertRecord.class);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        readerService.addSchemaField("name", STRING);
        readerService.addSchemaField("age", INT);
        readerService.addRecord("John Doe", 48);
        readerService.addRecord("Jane Doe", 47);
        readerService.addRecord("Jimmy Doe", 14);
        final MockFlowFile original = runner.enqueue("hello");
        runner.run();
        // Original FlowFile should be routed to 'failure' relationship without modification
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        Assert.assertTrue((original == out));
    }

    @Test
    public void testJSONCompression() throws IOException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(ConvertRecord.class);
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestConvertRecord/schema/person.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestConvertRecord/schema/person.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.setProperty(jsonWriter, "compression-format", "snappy");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.run();
        runner.assertAllFlowFilesTransferred(UpdateRecord.REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(ExecuteSQL.REL_SUCCESS).get(0);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (final SnappyInputStream sis = new SnappyInputStream(new ByteArrayInputStream(flowFile.toByteArray()));final OutputStream out = baos) {
            final byte[] buffer = new byte[8192];
            int len;
            while ((len = sis.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            } 
            out.flush();
        }
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestConvertRecord/input/person.json"))), baos.toString(StandardCharsets.UTF_8.name()));
    }
}

