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
package org.apache.nifi.processors.jolt.record;


import CoreAttributes.MIME_TYPE;
import JoltTransformRecord.CARDINALITY;
import JoltTransformRecord.CUSTOMR;
import JoltTransformRecord.CUSTOM_CLASS;
import JoltTransformRecord.DEFAULTR;
import JoltTransformRecord.JOLT_SPEC;
import JoltTransformRecord.JOLT_TRANSFORM;
import JoltTransformRecord.MODIFIER_DEFAULTR;
import JoltTransformRecord.MODULES;
import JoltTransformRecord.REL_FAILURE;
import JoltTransformRecord.REL_ORIGINAL;
import JoltTransformRecord.REL_SUCCESS;
import JoltTransformRecord.REMOVR;
import JoltTransformRecord.SHIFTR;
import JoltTransformRecord.SORTR;
import SchemaAccessUtils.SCHEMA_ACCESS_STRATEGY;
import SchemaAccessUtils.SCHEMA_TEXT;
import SchemaAccessUtils.SCHEMA_TEXT_PROPERTY;
import StringUtils.EMPTY;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.json.JsonRecordSetWriter;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.serialization.record.MockRecordParser;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestJoltTransformRecord {
    private TestRunner runner;

    private JoltTransformRecord processor;

    private MockRecordParser parser;

    private JsonRecordSetWriter writer;

    @Test
    public void testRelationshipsCreated() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue(new byte[0]);
        Set<Relationship> relationships = processor.getRelationships();
        Assert.assertTrue(relationships.contains(REL_FAILURE));
        Assert.assertTrue(relationships.contains(REL_SUCCESS));
        Assert.assertTrue(relationships.contains(REL_ORIGINAL));
        Assert.assertEquals(3, relationships.size());
    }

    @Test
    public void testInvalidJOLTSpec() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/shiftrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = "[{}]";
        runner.setProperty(JOLT_SPEC, spec);
        runner.assertNotValid();
    }

    @Test
    public void testIncorrectJOLTSpec() throws IOException {
        final String chainrSpec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, chainrSpec);
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.assertNotValid();
    }

    @Test
    public void testSpecIsNotSet() {
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.assertNotValid();
    }

    @Test
    public void testSpecIsEmpty() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/shiftrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        runner.setProperty(JOLT_SPEC, EMPTY);
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.assertNotValid();
    }

    @Test
    public void testSpecNotRequired() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/shiftrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        runner.setProperty(JOLT_TRANSFORM, SORTR);
        runner.assertValid();
    }

    @Test
    public void testNoFlowFileContent() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void testInvalidFlowFileContent() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutputSchema.avsc")));
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.setProperty(JOLT_SPEC, spec);
        runner.enableControllerService(writer);
        parser.failAfter(0);
        runner.enqueue("invalid json");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void testCustomTransformationWithNoModule() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/customChainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.assertNotValid();
    }

    @Test
    public void testCustomTransformationWithMissingClassName() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String customJarPath = "src/test/resources/TestJoltTransformRecord/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(new byte[0]);
        runner.assertNotValid();
    }

    @Test
    public void testCustomTransformationWithInvalidClassPath() throws IOException {
        final String customJarPath = "src/test/resources/TestJoltTransformRecord/FakeCustomJar.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(new byte[0]);
        runner.assertNotValid();
    }

    @Test
    public void testCustomTransformationWithInvalidClassName() throws IOException {
        final String customJarPath = "src/test/resources/TestJoltTransformRecord/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "FakeCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(new byte[0]);
        runner.assertNotValid();
    }

    @Test
    public void testTransformInputWithChainr() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/chainrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithShiftr() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/shiftrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/shiftrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/shiftrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithDefaultr() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithRemovr() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/removrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/removrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, REMOVR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/removrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithCardinality() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/cardrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/cardrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, CARDINALITY);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/cardrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithSortr() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/sortrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        runner.setProperty(JOLT_TRANSFORM, SORTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/sortrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithDefaultrExpressionLanguage() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrELOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrELSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        runner.setVariable("quota", "5");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrELOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithModifierDefault() throws IOException {
        generateTestData(1, null);
        // Input schema = output schema, just modifying values
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/inputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierDefaultSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, MODIFIER_DEFAULTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierDefaultOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithModifierDefine() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierDefineOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierDefineSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, MODIFIER_DEFAULTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierDefineOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithModifierOverwrite() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierOverwriteOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierOverwriteSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, MODIFIER_DEFAULTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/modifierOverwriteOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputWithSortrPopulatedSpec() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/sortrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        runner.setProperty(JOLT_TRANSFORM, SORTR);
        runner.setProperty(JOLT_SPEC, "abcd");
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/sortrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testTransformInputCustomTransformationIgnored() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        final String customJarPath = "src/test/resources/TestJoltTransformRecord/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testJoltSpecEL() throws IOException {
        generateTestData(1, null);
        final String outputSchemaText = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrOutputSchema.avsc")));
        runner.setProperty(writer, SCHEMA_ACCESS_STRATEGY, SCHEMA_TEXT_PROPERTY);
        runner.setProperty(writer, SCHEMA_TEXT, outputSchemaText);
        runner.setProperty(writer, "Pretty Print JSON", "true");
        runner.enableControllerService(writer);
        runner.setProperty(JOLT_SPEC, "${joltSpec}");
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        final Map<String, String> attributes = Collections.singletonMap("joltSpec", "{\"RatingRange\":5,\"rating\":{\"*\":{\"MaxLabel\":\"High\",\"MinLabel\":\"Low\",\"DisplayType\":\"NORMAL\"}}}");
        runner.enqueue(new byte[0], attributes);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformRecord/defaultrOutput.json"))), new String(transformed.toByteArray()));
    }

    @Test
    public void testJoltSpecInvalidEL() {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformRecord());
        final String spec = "${joltSpec:nonExistingFunction()}";
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue(new byte[0]);
        runner.assertNotValid();
    }
}

