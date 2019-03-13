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


import ForkRecord.INCLUDE_PARENT_FIELDS;
import ForkRecord.MODE;
import ForkRecord.MODE_EXTRACT;
import ForkRecord.MODE_SPLIT;
import ForkRecord.RECORD_READER;
import ForkRecord.RECORD_WRITER;
import ForkRecord.REL_FORK;
import ForkRecord.REL_ORIGINAL;
import RecordFieldType.ARRAY;
import RecordFieldType.DATE;
import RecordFieldType.DOUBLE;
import RecordFieldType.INT;
import RecordFieldType.RECORD;
import RecordFieldType.STRING;
import RecordFieldType.TIME;
import RecordFieldType.TIMESTAMP;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import SchemaAccessUtils.SCHEMA_TEXT;
import SchemaAccessUtils.SCHEMA_TEXT_PROPERTY;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.json.JsonRecordSetWriter;
import org.apache.nifi.json.JsonTreeReader;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.schema.access.SchemaNotFoundException;
import org.apache.nifi.serialization.MalformedRecordException;
import org.apache.nifi.serialization.RecordReader;
import org.apache.nifi.serialization.RecordReaderFactory;
import org.apache.nifi.serialization.record.DataType;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.serialization.record.RecordField;
import org.apache.nifi.serialization.record.RecordSchema;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestForkRecord {
    private final String dateFormat = DATE.getDefaultFormat();

    private final String timeFormat = TIME.getDefaultFormat();

    private final String timestampFormat = TIMESTAMP.getDefaultFormat();

    @Test
    public void testForkExtractSimpleWithoutParentFields() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, getAccountSchema());
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty("my-path", "/accounts");
        runner.enqueue(new File("src/test/resources/TestForkRecord/single-element-nested-array.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "2");
        mff.assertContentEquals("header\n42,4750.89\n43,48212.38\n");
    }

    @Test
    public void testForkExtractSimpleWithParentFields() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<RecordField> fieldsWrite = getDefaultFields();
        fieldsWrite.add(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema schemaWrite = new org.apache.nifi.serialization.SimpleRecordSchema(fieldsWrite);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, schemaWrite);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "true");
        runner.setProperty("my-path", "/accounts");
        runner.enqueue(new File("src/test/resources/TestForkRecord/single-element-nested-array.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "2");
        mff.assertContentEquals("header\n42,4750.89,John Doe,123 My Street,My City,MS,11111,USA\n43,48212.38,John Doe,123 My Street,My City,MS,11111,USA\n");
    }

    @Test
    public void testForkExtractNotAnArray() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<RecordField> fieldsWrite = getDefaultFields();
        fieldsWrite.add(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema schemaWrite = new org.apache.nifi.serialization.SimpleRecordSchema(fieldsWrite);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, schemaWrite);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "true");
        runner.setProperty("my-path", "/country");
        runner.enqueue(new File("src/test/resources/TestForkRecord/single-element-nested-array.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "0");
    }

    @Test
    public void testForkExtractNotAnArrayOfRecords() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = STRING.getDataType();
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<RecordField> fieldsWrite = getDefaultFields();
        fieldsWrite.add(new RecordField("balance", DOUBLE.getDataType()));
        final RecordSchema schemaWrite = new org.apache.nifi.serialization.SimpleRecordSchema(fieldsWrite);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, schemaWrite);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "true");
        runner.setProperty("my-path", "/accounts");
        runner.enqueue(new File("src/test/resources/TestForkRecord/single-element-nested-array-strings.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "0");
    }

    @Test
    public void testForkExtractComplexWithParentFields() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountWithTransactionSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<RecordField> fieldsWrite = getDefaultFields();
        fieldsWrite.add(new RecordField("balance", DOUBLE.getDataType()));
        fieldsWrite.add(new RecordField("amount", DOUBLE.getDataType()));
        final RecordSchema schemaWrite = new org.apache.nifi.serialization.SimpleRecordSchema(fieldsWrite);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, schemaWrite);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "true");
        runner.setProperty("my-path", "/accounts[*]/transactions");
        runner.enqueue(new File("src/test/resources/TestForkRecord/single-element-nested-nested-array.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "4");
        mff.assertContentEquals(("header\n5,150.31,John Doe,123 My Street,My City,MS,11111,USA,4750.89\n6,-15.31,John Doe,123 My Street,My City,MS,11111,USA,4750.89\n" + "7,36.78,John Doe,123 My Street,My City,MS,11111,USA,48212.38\n8,-21.34,John Doe,123 My Street,My City,MS,11111,USA,48212.38\n"));
    }

    @Test
    public void testForkExtractComplexWithoutParentFields() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountWithTransactionSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<RecordField> fieldsWrite = new ArrayList<RecordField>();
        fieldsWrite.add(new RecordField("id", INT.getDataType()));
        fieldsWrite.add(new RecordField("amount", DOUBLE.getDataType()));
        final RecordSchema schemaWrite = new org.apache.nifi.serialization.SimpleRecordSchema(fieldsWrite);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, schemaWrite);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "false");
        runner.setProperty("my-path", "/accounts[*]/transactions");
        runner.enqueue(new File("src/test/resources/TestForkRecord/single-element-nested-nested-array.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "4");
        mff.assertContentEquals("header\n5,150.31\n6,-15.31\n7,36.78\n8,-21.34\n");
    }

    @Test
    public void testForkExtractComplexWithParentFieldsAndNull() throws IOException, InitializationException, MalformedRecordException {
        TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final DataType accountRecordType = RECORD.getRecordDataType(getAccountWithTransactionSchema());
        final DataType accountsType = ARRAY.getArrayDataType(accountRecordType);
        final List<RecordField> fields = getDefaultFields();
        fields.add(new RecordField("accounts", accountsType));
        final RecordSchema schema = new org.apache.nifi.serialization.SimpleRecordSchema(fields);
        final List<RecordField> fieldsWrite = getDefaultFields();
        fieldsWrite.add(new RecordField("balance", DOUBLE.getDataType()));
        fieldsWrite.add(new RecordField("amount", DOUBLE.getDataType()));
        final RecordSchema schemaWrite = new org.apache.nifi.serialization.SimpleRecordSchema(fieldsWrite);
        final TestForkRecord.JsonRecordReader readerService = new TestForkRecord.JsonRecordReader(schema);
        final MockRecordWriter writerService = new TestForkRecord.CustomRecordWriter("header", false, schemaWrite);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.addControllerService("writer", writerService);
        runner.enableControllerService(writerService);
        runner.setProperty(RECORD_READER, "reader");
        runner.setProperty(RECORD_WRITER, "writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "true");
        runner.setProperty("my-path", "/accounts[*]/transactions");
        runner.enqueue(new File("src/test/resources/TestForkRecord/two-elements-nested-nested-array-null.json").toPath());
        runner.run(1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_FORK).get(0);
        mff.assertAttributeEquals("record.count", "4");
        mff.assertContentEquals(("header\n5,150.31,John Doe,123 My Street,My City,MS,11111,USA,4750.89\n6,-15.31,John Doe,123 My Street,My City,MS,11111,USA,4750.89\n" + "7,36.78,John Doe,123 My Street,My City,MS,11111,USA,48212.38\n8,-21.34,John Doe,123 My Street,My City,MS,11111,USA,48212.38\n"));
    }

    @Test
    public void testSplitMode() throws IOException, InitializationException {
        String expectedOutput = null;
        final TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("record-reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/schema/schema.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/schema/schema.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        runner.setProperty(RECORD_READER, "record-reader");
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("record-writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.setProperty(RECORD_WRITER, "record-writer");
        runner.setProperty(MODE, MODE_SPLIT);
        runner.setProperty("my-path", "/address");
        runner.enqueue(Paths.get("src/test/resources/TestForkRecord/input/complex-input-json.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/output/split-address.json")));
        runner.getFlowFilesForRelationship(REL_FORK).get(0).assertContentEquals(expectedOutput);
        runner.getFlowFilesForRelationship(REL_FORK).get(0).assertAttributeEquals("record.count", "5");
        runner.clearTransferState();
        runner.setProperty("my-path", "/bankAccounts[*]/last5Transactions");
        runner.enqueue(Paths.get("src/test/resources/TestForkRecord/input/complex-input-json.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/output/split-transactions.json")));
        runner.getFlowFilesForRelationship(REL_FORK).get(0).assertContentEquals(expectedOutput);
        runner.getFlowFilesForRelationship(REL_FORK).get(0).assertAttributeEquals("record.count", "6");
    }

    @Test
    public void testExtractMode() throws IOException, InitializationException {
        String expectedOutput = null;
        final TestRunner runner = TestRunners.newTestRunner(new ForkRecord());
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("record-reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/schema/schema.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/schema/extract-schema.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        runner.setProperty(RECORD_READER, "record-reader");
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("record-writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.setProperty(RECORD_WRITER, "record-writer");
        runner.setProperty(MODE, MODE_EXTRACT);
        runner.setProperty(INCLUDE_PARENT_FIELDS, "true");
        runner.setProperty("my-path", "/bankAccounts[*]/last5Transactions");
        runner.enqueue(Paths.get("src/test/resources/TestForkRecord/input/complex-input-json.json"));
        runner.run();
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_FORK, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestForkRecord/output/extract-transactions.json")));
        runner.getFlowFilesForRelationship(REL_FORK).get(0).assertContentEquals(expectedOutput);
        runner.getFlowFilesForRelationship(REL_FORK).get(0).assertAttributeEquals("record.count", "6");
    }

    private class JsonRecordReader extends AbstractControllerService implements RecordReaderFactory {
        RecordSchema schema;

        public JsonRecordReader(RecordSchema schema) {
            this.schema = schema;
        }

        @Override
        public RecordReader createRecordReader(FlowFile flowFile, InputStream in, ComponentLog logger) throws IOException, SchemaNotFoundException, MalformedRecordException {
            return new org.apache.nifi.json.JsonTreeRowRecordReader(in, logger, schema, dateFormat, timeFormat, timestampFormat);
        }

        @Override
        public RecordReader createRecordReader(Map<String, String> variables, InputStream in, ComponentLog logger) throws IOException, SchemaNotFoundException, MalformedRecordException {
            return new org.apache.nifi.json.JsonTreeRowRecordReader(in, logger, schema, dateFormat, timeFormat, timestampFormat);
        }
    }

    private class CustomRecordWriter extends MockRecordWriter {
        RecordSchema schema;

        public CustomRecordWriter(final String header, final boolean quoteValues, RecordSchema schema) {
            super(header, quoteValues);
            this.schema = schema;
        }

        @Override
        public RecordSchema getSchema(Map<String, String> variables, RecordSchema readSchema) throws IOException, SchemaNotFoundException {
            return this.schema;
        }
    }
}

