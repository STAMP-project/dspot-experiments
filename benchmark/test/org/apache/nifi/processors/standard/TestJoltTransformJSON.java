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


import CoreAttributes.MIME_TYPE;
import JoltTransformJSON.CARDINALITY;
import JoltTransformJSON.CUSTOMR;
import JoltTransformJSON.CUSTOM_CLASS;
import JoltTransformJSON.DEFAULTR;
import JoltTransformJSON.JOLT_SPEC;
import JoltTransformJSON.JOLT_TRANSFORM;
import JoltTransformJSON.MODIFIER_DEFAULTR;
import JoltTransformJSON.MODULES;
import JoltTransformJSON.REL_FAILURE;
import JoltTransformJSON.REL_SUCCESS;
import JoltTransformJSON.REMOVR;
import JoltTransformJSON.SHIFTR;
import JoltTransformJSON.SORTR;
import StringUtils.EMPTY;
import com.bazaarvoice.jolt.Diffy;
import com.bazaarvoice.jolt.JsonUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.nifi.processor.Processor;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestJoltTransformJSON {
    static final Path JSON_INPUT = Paths.get("src/test/resources/TestJoltTransformJson/input.json");

    static final Diffy DIFFY = new Diffy();

    @Test
    public void testRelationshipsCreated() throws IOException {
        Processor processor = new JoltTransformJSON();
        final TestRunner runner = TestRunners.newTestRunner(processor);
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        Set<Relationship> relationships = processor.getRelationships();
        Assert.assertTrue(relationships.contains(REL_FAILURE));
        Assert.assertTrue(relationships.contains(REL_SUCCESS));
        Assert.assertTrue(((relationships.size()) == 2));
    }

    @Test
    public void testInvalidJOLTSpec() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = "[{}]";
        runner.setProperty(JOLT_SPEC, spec);
        runner.assertNotValid();
    }

    @Test
    public void testIncorrectJOLTSpec() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String chainrSpec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, chainrSpec);
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.assertNotValid();
    }

    @Test
    public void testSpecIsNotSet() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.assertNotValid();
    }

    @Test
    public void testSpecIsEmpty() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        runner.setProperty(JOLT_SPEC, EMPTY);
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.assertNotValid();
    }

    @Test
    public void testSpecNotRequired() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        runner.setProperty(JOLT_TRANSFORM, SORTR);
        runner.assertValid();
    }

    @Test
    public void testNoFlowFileContent() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
    }

    @Test
    public void testInvalidFlowFileContentJson() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue("invalid json");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE);
    }

    @Test
    public void testCustomTransformationWithNoModule() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/customChainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
    }

    @Test
    public void testCustomTransformationWithMissingClassName() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.assertNotValid();
    }

    @Test
    public void testCustomTransformationWithInvalidClassPath() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson/FakeCustomJar.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.assertNotValid();
    }

    @Test
    public void testCustomTransformationWithInvalidClassName() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "FakeCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.assertNotValid();
    }

    @Test
    public void testTransformInputWithChainr() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/chainrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithShiftr() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/shiftrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, SHIFTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/shiftrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithDefaultr() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/defaultrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/defaultrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithRemovr() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/removrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, REMOVR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/removrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithCardinality() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/cardrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, CARDINALITY);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/cardrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithSortr() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        runner.setProperty(JOLT_TRANSFORM, SORTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/sortrOutput.json")));
        String transformedJsonString = JsonUtils.toJsonString(transformedJson);
        String compareJsonString = JsonUtils.toJsonString(compareJson);
        Assert.assertTrue(compareJsonString.equals(transformedJsonString));
    }

    @Test
    public void testTransformInputWithDefaultrExpressionLanguage() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/defaultrELSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        runner.setVariable("quota", "5");
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/defaultrELOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithModifierDefault() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/modifierDefaultSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, MODIFIER_DEFAULTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/modifierDefaultOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithModifierDefine() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/modifierDefineSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, MODIFIER_DEFAULTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/modifierDefineOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithModifierOverwrite() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/modifierOverwriteSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, MODIFIER_DEFAULTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/modifierOverwriteOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithSortrPopulatedSpec() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        runner.setProperty(JOLT_TRANSFORM, SORTR);
        runner.setProperty(JOLT_SPEC, "abcd");
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/sortrOutput.json")));
        String transformedJsonString = JsonUtils.toJsonString(transformedJson);
        String compareJsonString = JsonUtils.toJsonString(compareJson);
        Assert.assertTrue(compareJsonString.equals(transformedJsonString));
    }

    @Test
    public void testTransformInputWithCustomTransformationWithJar() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/chainrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithCustomTransformationWithDir() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/chainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, CUSTOMR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/chainrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputWithChainrEmbeddedCustomTransformation() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/customChainrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(MODULES, customJarPath);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/chainrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testTransformInputCustomTransformationIgnored() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String customJarPath = "src/test/resources/TestJoltTransformJson/TestCustomJoltTransform.jar";
        final String spec = new String(Files.readAllBytes(Paths.get("src/test/resources/TestJoltTransformJson/defaultrSpec.json")));
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(CUSTOM_CLASS, "TestCustomJoltTransform");
        runner.setProperty(MODULES, customJarPath);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/defaultrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testJoltSpecEL() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = "${joltSpec}";
        runner.setProperty(JOLT_SPEC, spec);
        runner.setProperty(JOLT_TRANSFORM, DEFAULTR);
        final Map<String, String> attributes = Collections.singletonMap("joltSpec", "{\"RatingRange\":5,\"rating\":{\"*\":{\"MaxLabel\":\"High\",\"MinLabel\":\"Low\",\"DisplayType\":\"NORMAL\"}}}");
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT, attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile transformed = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        transformed.assertAttributeExists(MIME_TYPE.key());
        transformed.assertAttributeEquals(MIME_TYPE.key(), "application/json");
        Object transformedJson = JsonUtils.jsonToObject(new ByteArrayInputStream(transformed.toByteArray()));
        Object compareJson = JsonUtils.jsonToObject(Files.newInputStream(Paths.get("src/test/resources/TestJoltTransformJson/defaultrOutput.json")));
        Assert.assertTrue(TestJoltTransformJSON.DIFFY.diff(compareJson, transformedJson).isEmpty());
    }

    @Test
    public void testJoltSpecInvalidEL() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new JoltTransformJSON());
        final String spec = "${joltSpec:nonExistingFunction()}";
        runner.setProperty(JOLT_SPEC, spec);
        runner.enqueue(TestJoltTransformJSON.JSON_INPUT);
        runner.assertNotValid();
    }
}

