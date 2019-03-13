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
package org.apache.nifi.processors.script;


import ScriptingComponentUtils.MODULES;
import ScriptingComponentUtils.SCRIPT_BODY;
import ScriptingComponentUtils.SCRIPT_FILE;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.MockProcessContext;
import org.apache.nifi.util.MockProcessorInitializationContext;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestInvokeGroovy extends BaseScriptTest {
    /**
     * Tests a script that has a Groovy Processor that that reads the first line of text from the flowfiles content and stores the value in an attribute of the outgoing flowfile.
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testReadFlowFileContentAndStoreInFlowFileAttribute() throws Exception {
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        runner.setProperty(SCRIPT_FILE, "target/test/resources/groovy/test_reader.groovy");
        runner.setProperty(MODULES, "target/test/resources/groovy");
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred("test", 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship("test");
        result.get(0).assertAttributeEquals("from-content", "test content");
    }

    /**
     * Tests a script that has a Groovy Processor that that reads the first line of text from the flowfiles content and
     * stores the value in an attribute of the outgoing flowfile.
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testScriptDefinedAttribute() throws Exception {
        InvokeScriptedProcessor processor = new InvokeScriptedProcessor();
        MockProcessContext context = new MockProcessContext(processor);
        MockProcessorInitializationContext initContext = new MockProcessorInitializationContext(processor, context);
        processor.initialize(initContext);
        context.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        context.setProperty(SCRIPT_FILE, "target/test/resources/groovy/test_reader.groovy");
        context.setProperty(MODULES, "target/test/resources/groovy");
        // State Manger is unused, and a null reference is specified
        processor.customValidate(new org.apache.nifi.util.MockValidationContext(context));
        processor.setup(context);
        List<PropertyDescriptor> descriptors = processor.getSupportedPropertyDescriptors();
        Assert.assertNotNull(descriptors);
        Assert.assertTrue(((descriptors.size()) > 0));
        boolean found = false;
        for (PropertyDescriptor descriptor : descriptors) {
            if (descriptor.getName().equals("test-attribute")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    /**
     * Tests a script that has a Groovy Processor that that reads the first line of text from the flowfiles content and
     * stores the value in an attribute of the outgoing flowfile.
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testScriptDefinedRelationship() throws Exception {
        InvokeScriptedProcessor processor = new InvokeScriptedProcessor();
        MockProcessContext context = new MockProcessContext(processor);
        MockProcessorInitializationContext initContext = new MockProcessorInitializationContext(processor, context);
        processor.initialize(initContext);
        context.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        context.setProperty(SCRIPT_FILE, "target/test/resources/groovy/test_reader.groovy");
        // State Manger is unused, and a null reference is specified
        processor.customValidate(new org.apache.nifi.util.MockValidationContext(context));
        processor.setup(context);
        Set<Relationship> relationships = processor.getRelationships();
        Assert.assertNotNull(relationships);
        Assert.assertTrue(((relationships.size()) > 0));
        boolean found = false;
        for (Relationship relationship : relationships) {
            if (relationship.getName().equals("test")) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
    }

    /**
     * Tests a script that throws a ProcessException within. The expected result is that the exception will be
     * propagated
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test(expected = AssertionError.class)
    public void testInvokeScriptCausesException() throws Exception {
        final TestRunner runner = TestRunners.newTestRunner(new InvokeScriptedProcessor());
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        runner.setProperty(SCRIPT_BODY, getFileContentsAsString(((TEST_RESOURCE_LOCATION) + "groovy/testInvokeScriptCausesException.groovy")));
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
    }

    /**
     * Tests a script that routes the FlowFile to failure.
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testScriptRoutesToFailure() throws Exception {
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        runner.setProperty(SCRIPT_BODY, getFileContentsAsString(((TEST_RESOURCE_LOCATION) + "groovy/testScriptRoutesToFailure.groovy")));
        runner.assertValid();
        runner.enqueue("test content".getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred("FAILURE", 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship("FAILURE");
        Assert.assertFalse(result.isEmpty());
    }

    @Test
    public void testValidationResultsReset() throws Exception {
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        runner.setProperty(SCRIPT_FILE, "target/test/resources/groovy/test_reader.groovy");
        runner.setProperty(MODULES, "target/test/resources/groovy");
        runner.assertValid();
        runner.setProperty("test-attribute", "test");
        runner.assertValid();
    }

    /**
     * Tests a script that derive from AbstractProcessor as base class
     *
     * @throws Exception
     * 		Any error encountered while testing
     */
    @Test
    public void testAbstractProcessorImplementationWithBodyScriptFile() throws Exception {
        runner.setValidateExpressionUsage(false);
        runner.setProperty(scriptingComponent.getScriptingComponentHelper().SCRIPT_ENGINE, "Groovy");
        runner.setProperty(SCRIPT_BODY, getFileContentsAsString(((TEST_RESOURCE_LOCATION) + "groovy/test_implementingabstractProcessor.groovy")));
        runner.setProperty(MODULES, ((TEST_RESOURCE_LOCATION) + "groovy"));
        runner.setProperty("custom_prop", "bla bla");
        runner.assertValid();
        runner.enqueue("test".getBytes(StandardCharsets.UTF_8));
        runner.run();
        runner.assertAllFlowFilesTransferred("success", 1);
        final List<MockFlowFile> result = runner.getFlowFilesForRelationship("success");
        Assert.assertTrue(((result.size()) == 1));
        final String expectedOutput = new String(Hex.encodeHex(MessageDigest.getInstance("MD5").digest("testbla bla".getBytes())));
        final MockFlowFile outputFlowFile = result.get(0);
        outputFlowFile.assertContentEquals(expectedOutput);
        outputFlowFile.assertAttributeEquals("outAttr", expectedOutput);
    }
}

