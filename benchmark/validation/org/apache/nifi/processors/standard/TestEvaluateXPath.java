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


import EvaluateXPath.DESTINATION;
import EvaluateXPath.DESTINATION_ATTRIBUTE;
import EvaluateXPath.DESTINATION_CONTENT;
import EvaluateXPath.REL_FAILURE;
import EvaluateXPath.REL_MATCH;
import EvaluateXPath.REL_NO_MATCH;
import EvaluateXPath.RETURN_TYPE;
import EvaluateXPath.RETURN_TYPE_AUTO;
import EvaluateXPath.RETURN_TYPE_NODESET;
import EvaluateXPath.RETURN_TYPE_STRING;
import EvaluateXPath.VALIDATE_DTD;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.xml.xpath.XPathFactoryConfigurationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestEvaluateXPath {
    private static final Path XML_SNIPPET = Paths.get("src/test/resources/TestXml/xml-snippet.xml");

    private static final Path XML_SNIPPET_EMBEDDED_DOCTYPE = Paths.get("src/test/resources/TestXml/xml-snippet-embedded-doctype.xml");

    private static final Path XML_SNIPPET_NONEXISTENT_DOCTYPE = Paths.get("src/test/resources/TestXml/xml-snippet-external-doctype.xml");

    @Test
    public void testAsAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xpath.result1", "/");
        testRunner.setProperty("xpath.result2", "/*:bundle/node/subNode/value/text()");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("xpath.result2", "Hello");
        Assert.assertTrue(out.getAttribute("xpath.result1").contains("Hello"));
    }

    @Test
    public void testCheckIfElementExists() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty("xpath.result1", "/");
        testRunner.setProperty("xpath.result.exist.1", "boolean(/*:bundle/node)");
        testRunner.setProperty("xpath.result.exist.2", "boolean(/*:bundle/node2)");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        Assert.assertTrue(out.getAttribute("xpath.result1").contains("Hello"));
        out.assertAttributeEquals("xpath.result.exist.1", "true");
        out.assertAttributeEquals("xpath.result.exist.2", "false");
    }

    @Test
    public void testUnmatched() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("xpath.result.exist.2", "/*:bundle/node2");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH, 1);
        testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0).assertContentEquals(TestEvaluateXPath.XML_SNIPPET);
    }

    @Test(expected = AssertionError.class)
    public void testMultipleXPathForContent() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_AUTO);
        testRunner.setProperty("some.property.1", "/*:bundle/node/subNode[1]");
        testRunner.setProperty("some.property.2", "/*:bundle/node/subNode[2]");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
    }

    @Test
    public void testWriteToContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.contains("subNode"));
        Assert.assertTrue(outXml.contains("Hello"));
    }

    @Test
    public void testFailureIfContentMatchesMultipleNodes() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty("some.property", "/*:bundle/node/subNode");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testWriteStringToContent() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_STRING);
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }

    @Test
    public void testWriteNodeSetToAttribute() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_NODESET);
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final String outXml = out.getAttribute("some.property");
        Assert.assertTrue(outXml.contains("subNode"));
        Assert.assertTrue(outXml.contains("Hello"));
    }

    @Test
    public void testSuccessForEmbeddedDocTypeValidation() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_STRING);
        testRunner.setProperty(VALIDATE_DTD, "true");
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET_EMBEDDED_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }

    @Test
    public void testSuccessForEmbeddedDocTypeValidationDisabled() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_STRING);
        testRunner.setProperty(VALIDATE_DTD, "false");
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET_EMBEDDED_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }

    @Test
    public void testFailureForExternalDocTypeWithDocTypeValidationEnabled() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_STRING);
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET_NONEXISTENT_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testSuccessForExternalDocTypeWithDocTypeValidationDisabled() throws IOException, XPathFactoryConfigurationException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EvaluateXPath());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(RETURN_TYPE, RETURN_TYPE_STRING);
        testRunner.setProperty(VALIDATE_DTD, "false");
        testRunner.setProperty("some.property", "/*:bundle/node/subNode[1]/value/text()");
        testRunner.enqueue(TestEvaluateXPath.XML_SNIPPET_NONEXISTENT_DOCTYPE);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        final byte[] outData = testRunner.getContentAsByteArray(out);
        final String outXml = new String(outData, "UTF-8");
        Assert.assertTrue(outXml.trim().equals("Hello"));
    }
}

