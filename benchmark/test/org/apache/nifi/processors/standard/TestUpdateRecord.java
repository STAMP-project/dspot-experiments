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


import RecordFieldType.ARRAY;
import RecordFieldType.STRING;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import SchemaAccessUtils.SCHEMA_TEXT;
import SchemaAccessUtils.SCHEMA_TEXT_PROPERTY;
import UpdateRecord.LITERAL_VALUES;
import UpdateRecord.RECORD_PATH_VALUES;
import UpdateRecord.REL_FAILURE;
import UpdateRecord.REL_SUCCESS;
import UpdateRecord.REPLACEMENT_VALUE_STRATEGY;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.json.JsonRecordSetWriter;
import org.apache.nifi.json.JsonTreeReader;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.serialization.record.MockRecordWriter;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestUpdateRecord {
    private TestRunner runner;

    private MockRecordParser readerService;

    private MockRecordWriter writerService;

    @Test
    public void testLiteralReplacementValue() {
        runner.setProperty("/name", "Jane Doe");
        runner.enqueue("");
        readerService.addRecord("John Doe", 35);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("header\nJane Doe,35\n");
    }

    @Test
    public void testLiteralReplacementValueExpressionLanguage() {
        runner.setProperty("/name", "${newName}");
        runner.enqueue("", Collections.singletonMap("newName", "Jane Doe"));
        readerService.addRecord("John Doe", 35);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("header\nJane Doe,35\n");
    }

    @Test
    public void testRecordPathReplacementValue() {
        runner.setProperty("/name", "/age");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES.getValue());
        runner.enqueue("");
        readerService.addRecord("John Doe", 35);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("header\n35,35\n");
    }

    @Test
    public void testInvalidRecordPathUsingExpressionLanguage() {
        runner.setProperty("/name", "${recordPath}");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES.getValue());
        runner.enqueue("", Collections.singletonMap("recordPath", "hello"));
        readerService.addRecord("John Doe", 35);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testReplaceWithMissingRecordPath() throws InitializationException {
        readerService = new MockRecordParser();
        readerService.addSchemaField("name", STRING);
        readerService.addSchemaField("siblings", ARRAY);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.setProperty("/name", "/siblings[0]/name");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES.getValue());
        runner.enqueue("", Collections.singletonMap("recordPath", "hello"));
        readerService.addRecord("John Doe", null);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertContentEquals("header\n,\n");
    }

    @Test
    public void testRelativePath() throws InitializationException {
        readerService = new MockRecordParser();
        readerService.addSchemaField("name", STRING);
        readerService.addSchemaField("nickname", STRING);
        runner.addControllerService("reader", readerService);
        runner.enableControllerService(readerService);
        runner.setProperty("/name", "../nickname");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES.getValue());
        runner.enqueue("", Collections.singletonMap("recordPath", "hello"));
        readerService.addRecord("John Doe", "Johnny");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertContentEquals("header\nJohnny,Johnny\n");
    }

    @Test
    public void testChangingSchema() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-string.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/name", "/name/first");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/person-with-firstname.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testUpdateInArray() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-address.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-address.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person-address.json"));
        runner.setProperty("/address[*]/city", "newCity");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, LITERAL_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/person-with-new-city.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testUpdateInNullArray() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-address.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-address.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person-with-null-array.json"));
        runner.setProperty("/address[*]/city", "newCity");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, LITERAL_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/person-with-null-array.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testAddFieldNotInInputRecord() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-string-fields.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/firstName", "/name/first");
        runner.setProperty("/lastName", "/name/last");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/person-with-firstname-lastname.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testFieldValuesInEL() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/name/last", "${field.value:toUpper()}");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, LITERAL_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/person-with-capital-lastname.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testSetRootPathAbsoluteWithMultipleValues() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/name-fields-only.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/", "/name/*");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/name-fields-only.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testSetRootPathAbsoluteWithSingleValue() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/name-fields-only.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/", "/name");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/name-fields-only.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testSetRootPathRelativeWithMultipleValues() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/name-fields-only.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/name/..", "/name/*");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/name-fields-only.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testSetRootPathRelativeWithSingleValue() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-record.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/name-fields-only.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/name/..", "/name");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/name-fields-only.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testSetAbsolutePathWithAnotherRecord() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-and-mother.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/person-with-name-and-mother.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/person.json"));
        runner.setProperty("/name", "/mother");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/name-and-mother-same.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
    }

    @Test
    public void testUpdateSimpleArray() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/multi-arrays.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/multi-arrays.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, LITERAL_VALUES);
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/numbers[*]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json")));
        expectedOutput = expectedOutput.replaceFirst("1, null, 4", "8, 8, 8");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/numbers[*]");
        runner.clearTransferState();
        runner.enqueue("{\"numbers\":null}");
        runner.setProperty("/numbers[*]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        String content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        Assert.assertTrue(content.contains("\"numbers\" : null"));
        runner.removeProperty("/numbers[*]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/numbers[1]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json")));
        expectedOutput = expectedOutput.replaceFirst("1, null, 4", "1, 8, 4");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/numbers[1]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/numbers[0..1]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json")));
        expectedOutput = expectedOutput.replaceFirst("1, null, 4", "8, 8, 4");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/numbers[0..1]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/numbers[0,2]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json")));
        expectedOutput = expectedOutput.replaceFirst("1, null, 4", "8, null, 8");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/numbers[0,2]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/numbers[0,1..2]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json")));
        expectedOutput = expectedOutput.replaceFirst("1, null, 4", "8, 8, 8");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/numbers[0,1..2]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/numbers[0..-1][. = 4]", "8");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json")));
        expectedOutput = expectedOutput.replaceFirst("1, null, 4", "1, null, 8");
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/numbers[0..-1][. = 4]");
    }

    @Test
    public void testUpdateComplexArrays() throws IOException, InitializationException {
        final JsonTreeReader jsonReader = new JsonTreeReader();
        runner.addControllerService("reader", jsonReader);
        final String inputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/multi-arrays.avsc")));
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/schema/multi-arrays.avsc")));
        runner.setProperty(jsonReader, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonReader, SCHEMA_TEXT, inputSchemaText);
        runner.enableControllerService(jsonReader);
        final JsonRecordSetWriter jsonWriter = new JsonRecordSetWriter();
        runner.addControllerService("writer", jsonWriter);
        runner.setProperty(jsonWriter, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(jsonWriter, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(jsonWriter, "Pretty Print JSON", "true");
        runner.setProperty(jsonWriter, "Schema Write Strategy", "full-schema-attribute");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, RECORD_PATH_VALUES);
        runner.enableControllerService(jsonWriter);
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[*]", "/peoples[3]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        String content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        int count = StringUtils.countMatches(content, "Mary Doe");
        Assert.assertEquals(4, count);
        runner.removeProperty("/peoples[*]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[1]", "/peoples[3]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        count = StringUtils.countMatches(content, "Mary Doe");
        Assert.assertEquals(2, count);
        runner.removeProperty("/peoples[1]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0..1]", "/peoples[3]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        String expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/updateArrays/multi-arrays-0and1.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/peoples[0..1]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0,2]", "/peoples[3]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/updateArrays/multi-arrays-0and2.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/peoples[0,2]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0,1..2]", "/peoples[3]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        count = StringUtils.countMatches(content, "Mary Doe");
        Assert.assertEquals(4, count);
        runner.removeProperty("/peoples[0,1..2]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0..-1][./name != 'Mary Doe']", "/peoples[3]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        count = StringUtils.countMatches(content, "Mary Doe");
        Assert.assertEquals(4, count);
        runner.removeProperty("/peoples[0..-1][./name != 'Mary Doe']");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0..-1][./name != 'Mary Doe']/addresses[*]", "/peoples[3]/addresses[0]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        count = StringUtils.countMatches(content, "1 nifi road");
        Assert.assertEquals(13, count);
        runner.removeProperty("/peoples[0..-1][./name != 'Mary Doe']/addresses[*]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0..-1][./name != 'Mary Doe']/addresses[0,1..2]", "/peoples[3]/addresses[0]");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        expectedOutput = new String(Files.readAllBytes(Paths.get("src/test/resources/TestUpdateRecord/output/updateArrays/multi-arrays-streets.json")));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(expectedOutput);
        runner.removeProperty("/peoples[0..-1][./name != 'Mary Doe']/addresses[0,1..2]");
        runner.clearTransferState();
        runner.enqueue(Paths.get("src/test/resources/TestUpdateRecord/input/multi-arrays.json"));
        runner.setProperty("/peoples[0..-1][./name != 'Mary Doe']/addresses[0,1..2]/city", "newCity");
        runner.setProperty(REPLACEMENT_VALUE_STRATEGY, LITERAL_VALUES);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        content = new String(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray());
        count = StringUtils.countMatches(content, "newCity");
        Assert.assertEquals(9, count);
        runner.removeProperty("/peoples[0..-1][./name != 'Mary Doe']/addresses[0,1..2]/city");
    }
}

