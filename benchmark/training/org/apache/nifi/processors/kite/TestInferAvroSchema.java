/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.nifi.processors.kite;


import CoreAttributes.MIME_TYPE;
import InferAvroSchema.AVRO_SCHEMA_ATTRIBUTE_NAME;
import InferAvroSchema.CHARSET;
import InferAvroSchema.CSV_HEADER_DEFINITION;
import InferAvroSchema.DELIMITER;
import InferAvroSchema.DESTINATION_ATTRIBUTE;
import InferAvroSchema.ESCAPE_STRING;
import InferAvroSchema.GET_CSV_HEADER_DEFINITION_FROM_INPUT;
import InferAvroSchema.HEADER_LINE_SKIP_COUNT;
import InferAvroSchema.INPUT_CONTENT_TYPE;
import InferAvroSchema.NUM_RECORDS_TO_ANALYZE;
import InferAvroSchema.QUOTE_STRING;
import InferAvroSchema.RECORD_NAME;
import InferAvroSchema.REL_FAILURE;
import InferAvroSchema.REL_ORIGINAL;
import InferAvroSchema.REL_SUCCESS;
import InferAvroSchema.REL_UNSUPPORTED_CONTENT;
import InferAvroSchema.SCHEMA_DESTINATION;
import InferAvroSchema.USE_MIME_TYPE;
import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestInferAvroSchema {
    private TestRunner runner = null;

    @Test
    public void testRecordName() throws Exception {
        // Dot at the end is invalid
        runner.setProperty(RECORD_NAME, "org.apache.nifi.contact.");
        runner.assertNotValid();
        // Dashes are invalid
        runner.setProperty(RECORD_NAME, "avro-schema");
        runner.assertNotValid();
        // Name cannot start with a digit
        runner.setProperty(RECORD_NAME, "1Record");
        runner.assertNotValid();
        // Name cannot start with a dot
        runner.setProperty(RECORD_NAME, ".record");
        runner.assertNotValid();
        runner.setProperty(RECORD_NAME, "avro_schema");
        runner.assertValid();
        runner.setProperty(RECORD_NAME, "org.apache.nifi.contact");
        runner.assertValid();
        runner.setProperty(RECORD_NAME, "${filename}");// EL is valid, although its value may not be when evaluated

        runner.assertValid();
    }

    @Test
    public void inferAvroSchemaFromHeaderDefinitionOfCSVFile() throws Exception {
        runner.assertValid();
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue(new File("src/test/resources/Shapes_Header.csv").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(TestInferAvroSchema.unix2PlatformSpecificLineEndings(new File("src/test/resources/Shapes_header.csv.avro")));
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
    }

    @Test
    public void inferAvroSchemaFromJSONFile() throws Exception {
        runner.assertValid();
        runner.setProperty(INPUT_CONTENT_TYPE, USE_MIME_TYPE);
        // Purposely set to True to test that none of the JSON file is read which would cause issues.
        runner.setProperty(GET_CSV_HEADER_DEFINITION_FROM_INPUT, "true");
        runner.setProperty(SCHEMA_DESTINATION, DESTINATION_ATTRIBUTE);
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/json");
        runner.enqueue(new File("src/test/resources/Shapes.json").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile data = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String avroSchema = data.getAttribute(AVRO_SCHEMA_ATTRIBUTE_NAME);
        String knownSchema = new String(TestInferAvroSchema.unix2PlatformSpecificLineEndings(new File("src/test/resources/Shapes.json.avro")), StandardCharsets.UTF_8);
        Assert.assertEquals(avroSchema, knownSchema);
        // Since that avro schema is written to an attribute this should be teh same as the original
        data.assertAttributeEquals(MIME_TYPE.key(), "application/json");
    }

    @Test
    public void inferAvroSchemaFromCSVFile() throws Exception {
        runner.assertValid();
        // Read in the header
        StringWriter writer = new StringWriter();
        IOUtils.copy(Files.newInputStream(Paths.get("src/test/resources/ShapesHeader.csv"), StandardOpenOption.READ), writer, "UTF-8");
        runner.setProperty(CSV_HEADER_DEFINITION, writer.toString());
        runner.setProperty(GET_CSV_HEADER_DEFINITION_FROM_INPUT, "false");
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue(new File("src/test/resources/Shapes_NoHeader.csv").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile data = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        data.assertContentEquals(TestInferAvroSchema.unix2PlatformSpecificLineEndings(new File("src/test/resources/Shapes_header.csv.avro")));
        data.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
    }

    @Test
    public void inferSchemaFormHeaderLinePropertyOfProcessor() throws Exception {
        final String CSV_HEADER_LINE = FileUtils.readFileToString(new File("src/test/resources/ShapesHeader.csv"));
        runner.assertValid();
        runner.setProperty(GET_CSV_HEADER_DEFINITION_FROM_INPUT, "false");
        runner.setProperty(CSV_HEADER_DEFINITION, CSV_HEADER_LINE);
        runner.setProperty(HEADER_LINE_SKIP_COUNT, "1");
        runner.assertValid();
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue((CSV_HEADER_LINE + "\nJane,Doe,29,55555").getBytes(), attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile data = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        data.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
    }

    @Test
    public void inferSchemaFromEmptyContent() throws Exception {
        runner.assertValid();
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue("", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void inferAvroSchemaFromHeaderDefinitionOfCSVTabDelimitedFile() throws Exception {
        runner.setProperty(DELIMITER, "\\t");
        runner.assertValid();
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue(new File("src/test/resources/Shapes_Header_TabDelimited.csv").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(TestInferAvroSchema.unix2PlatformSpecificLineEndings(new File("src/test/resources/Shapes_header.csv.avro")));
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
    }

    @Test
    public void inferAvroSchemaFromHeaderDefinitionOfCSVTabDelimitedFileNegativeTest() throws Exception {
        // Inproper InferAvroSchema.DELIMITER > original goes to InferAvroSchema.REL_FAILURE
        runner.setProperty(DELIMITER, ";");
        runner.assertValid();
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "text/csv");
        runner.enqueue(new File("src/test/resources/Shapes_Header_TabDelimited.csv").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        flowFile.assertContentEquals(new File("src/test/resources/Shapes_Header_TabDelimited.csv").toPath());
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "text/csv");
    }

    @Test
    public void specifyCSVparametersInExpressionLanguage() throws Exception {
        runner.setProperty(DELIMITER, "${csv.delimiter}");
        runner.setProperty(ESCAPE_STRING, "${csv.escape}");
        runner.setProperty(QUOTE_STRING, "${csv.quote}");
        runner.setProperty(CHARSET, "${csv.charset}");
        runner.setProperty(GET_CSV_HEADER_DEFINITION_FROM_INPUT, "true");
        runner.assertValid();
        @SuppressWarnings("serial")
        Map<String, String> attributes = new HashMap<String, String>() {
            {
                put("csv.delimiter", ",");
                put("csv.escape", "\\");
                put("csv.quote", "\"");
                put("csv.charset", "UTF-8");
                put(MIME_TYPE.key(), "text/csv");
            }
        };
        runner.enqueue(new File("src/test/resources/Shapes_Header.csv").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(TestInferAvroSchema.unix2PlatformSpecificLineEndings(new File("src/test/resources/Shapes_header.csv.avro")));
        flowFile.assertAttributeEquals(MIME_TYPE.key(), "application/avro-binary");
    }

    @Test
    public void specifyJsonParametersInExpressionLanguage() throws Exception {
        runner.assertValid();
        runner.setProperty(INPUT_CONTENT_TYPE, USE_MIME_TYPE);
        // Purposely set to True to test that none of the JSON file is read which would cause issues.
        runner.setProperty(GET_CSV_HEADER_DEFINITION_FROM_INPUT, "true");
        runner.setProperty(SCHEMA_DESTINATION, DESTINATION_ATTRIBUTE);
        runner.setProperty(RECORD_NAME, "${record.name}");
        runner.setProperty(NUM_RECORDS_TO_ANALYZE, "${records.analyze}");
        Map<String, String> attributes = new HashMap<>();
        attributes.put(MIME_TYPE.key(), "application/json");
        attributes.put("record.name", "myrecord");
        attributes.put("records.analyze", "2");
        runner.enqueue(new File("src/test/resources/Shapes.json").toPath(), attributes);
        runner.run();
        runner.assertTransferCount(REL_UNSUPPORTED_CONTENT, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile data = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String avroSchema = data.getAttribute(AVRO_SCHEMA_ATTRIBUTE_NAME);
        Assert.assertTrue(avroSchema.contains("\"name\" : \"myrecord\""));
        // Since that avro schema is written to an attribute this should be teh same as the original
        data.assertAttributeEquals(MIME_TYPE.key(), "application/json");
    }
}

