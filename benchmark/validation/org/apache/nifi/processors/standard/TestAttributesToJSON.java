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


import AttributesToJSON.APPLICATION_JSON;
import AttributesToJSON.ATTRIBUTES_LIST;
import AttributesToJSON.ATTRIBUTES_REGEX;
import AttributesToJSON.DESTINATION;
import AttributesToJSON.DESTINATION_ATTRIBUTE;
import AttributesToJSON.DESTINATION_CONTENT;
import AttributesToJSON.INCLUDE_CORE_ATTRIBUTES;
import AttributesToJSON.JSON_ATTRIBUTE_NAME;
import AttributesToJSON.NULL_VALUE_FOR_EMPTY_STRING;
import AttributesToJSON.REL_FAILURE;
import AttributesToJSON.REL_SUCCESS;
import CoreAttributes.MIME_TYPE;
import CoreAttributes.PATH;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.flowfile.attributes.CoreAttributes;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestAttributesToJSON {
    private static final String TEST_ATTRIBUTE_KEY = "TestAttribute";

    private static final String TEST_ATTRIBUTE_VALUE = "TestValue";

    @Test(expected = AssertionError.class)
    public void testInvalidUserSuppliedAttributeList() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        // Attribute list CANNOT be empty
        testRunner.setProperty(ATTRIBUTES_LIST, "");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
    }

    @Test(expected = AssertionError.class)
    public void testInvalidIncludeCoreAttributesProperty() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(ATTRIBUTES_LIST, "val1,val2");
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        testRunner.setProperty(INCLUDE_CORE_ATTRIBUTES, "maybe");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
    }

    @Test
    public void testNullValueForEmptyAttribute() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        final String NON_PRESENT_ATTRIBUTE_KEY = "NonExistingAttributeKey";
        testRunner.setProperty(ATTRIBUTES_LIST, NON_PRESENT_ATTRIBUTE_KEY);
        testRunner.setProperty(NULL_VALUE_FOR_EMPTY_STRING, "true");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
        // Expecting success transition because Jackson is taking care of escaping the bad JSON characters
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        // Make sure that the value is a true JSON null for the non existing attribute
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        Assert.assertNull(val.get(NON_PRESENT_ATTRIBUTE_KEY));
    }

    @Test
    public void testEmptyStringValueForEmptyAttribute() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        final String NON_PRESENT_ATTRIBUTE_KEY = "NonExistingAttributeKey";
        testRunner.setProperty(ATTRIBUTES_LIST, NON_PRESENT_ATTRIBUTE_KEY);
        testRunner.setProperty(NULL_VALUE_FOR_EMPTY_STRING, "false");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
        // Expecting success transition because Jackson is taking care of escaping the bad JSON characters
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        // Make sure that the value is a true JSON null for the non existing attribute
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        Assert.assertEquals(val.get(NON_PRESENT_ATTRIBUTE_KEY), "");
    }

    @Test
    public void testInvalidJSONValueInAttribute() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        // Create attribute that contains an invalid JSON Character
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, "'badjson'");
        testRunner.enqueue(ff);
        testRunner.run();
        // Expecting success transition because Jackson is taking care of escaping the bad JSON characters
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
    }

    @Test
    public void testAttributes_emptyListUserSpecifiedAttributes() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        Assert.assertTrue(val.get(TestAttributesToJSON.TEST_ATTRIBUTE_KEY).equals(TestAttributesToJSON.TEST_ATTRIBUTE_VALUE));
    }

    @Test
    public void testContent_emptyListUserSpecifiedAttributes() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(INCLUDE_CORE_ATTRIBUTES, "false");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeNotExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals("{}");
    }

    @Test
    public void testAttribute_singleUserDefinedAttribute() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(ATTRIBUTES_LIST, TestAttributesToJSON.TEST_ATTRIBUTE_KEY);
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        Assert.assertTrue(val.get(TestAttributesToJSON.TEST_ATTRIBUTE_KEY).equals(TestAttributesToJSON.TEST_ATTRIBUTE_VALUE));
        Assert.assertTrue(((val.size()) == 1));
    }

    @Test
    public void testAttribute_singleUserDefinedAttributeWithWhiteSpace() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(ATTRIBUTES_LIST, ((" " + (TestAttributesToJSON.TEST_ATTRIBUTE_KEY)) + " "));
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        Assert.assertTrue(val.get(TestAttributesToJSON.TEST_ATTRIBUTE_KEY).equals(TestAttributesToJSON.TEST_ATTRIBUTE_VALUE));
        Assert.assertTrue(((val.size()) == 1));
    }

    @Test
    public void testAttribute_singleNonExistingUserDefinedAttribute() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(ATTRIBUTES_LIST, "NonExistingAttribute");
        testRunner.setProperty(DESTINATION, DESTINATION_ATTRIBUTE);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        // If a Attribute is requested but does not exist then it is placed in the JSON with an empty string
        Assert.assertTrue(val.get("NonExistingAttribute").equals(""));
        Assert.assertTrue(((val.size()) == 1));
    }

    @Test
    public void testAttribute_noIncludeCoreAttributesUserDefined() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(ATTRIBUTES_LIST, ((((" " + (TestAttributesToJSON.TEST_ATTRIBUTE_KEY)) + " , ") + (PATH.key())) + " "));
        testRunner.setProperty(INCLUDE_CORE_ATTRIBUTES, "false");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        ff = session.putAttribute(ff, PATH.key(), TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertAttributeExists(JSON_ATTRIBUTE_NAME);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        String json = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).getAttribute(JSON_ATTRIBUTE_NAME);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(json, HashMap.class);
        Assert.assertEquals(TestAttributesToJSON.TEST_ATTRIBUTE_VALUE, val.get(TestAttributesToJSON.TEST_ATTRIBUTE_KEY));
        Assert.assertEquals(1, val.size());
    }

    @Test
    public void testAttribute_noIncludeCoreAttributesContent() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(INCLUDE_CORE_ATTRIBUTES, "false");
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.putAttribute(ff, TestAttributesToJSON.TEST_ATTRIBUTE_KEY, TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        ff = session.putAttribute(ff, PATH.key(), TestAttributesToJSON.TEST_ATTRIBUTE_VALUE);
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> val = mapper.readValue(testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray(), HashMap.class);
        Assert.assertEquals(TestAttributesToJSON.TEST_ATTRIBUTE_VALUE, val.get(TestAttributesToJSON.TEST_ATTRIBUTE_KEY));
        Assert.assertEquals(1, val.size());
    }

    @Test
    public void testAttribute_includeCoreAttributesContent() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(DESTINATION, DESTINATION_CONTENT);
        testRunner.setProperty(INCLUDE_CORE_ATTRIBUTES, "true");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
        List<MockFlowFile> flowFilesForRelationship = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile flowFile = flowFilesForRelationship.get(0);
        Assert.assertEquals(APPLICATION_JSON, flowFile.getAttribute(MIME_TYPE.key()));
        Map<String, String> val = new ObjectMapper().readValue(flowFile.toByteArray(), HashMap.class);
        Assert.assertEquals(3, val.size());
        Set<String> coreAttributes = Arrays.stream(CoreAttributes.values()).map(CoreAttributes::key).collect(Collectors.toSet());
        val.keySet().forEach(( k) -> Assert.assertTrue(coreAttributes.contains(k)));
    }

    @Test
    public void testAttribute_includeCoreAttributesAttribute() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setProperty(INCLUDE_CORE_ATTRIBUTES, "true");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        testRunner.enqueue(ff);
        testRunner.run();
        List<MockFlowFile> flowFilesForRelationship = testRunner.getFlowFilesForRelationship(REL_SUCCESS);
        testRunner.assertTransferCount(REL_FAILURE, 0);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile flowFile = flowFilesForRelationship.get(0);
        Assert.assertNull(flowFile.getAttribute(MIME_TYPE.key()));
        Map<String, String> val = new ObjectMapper().readValue(flowFile.getAttribute(JSON_ATTRIBUTE_NAME), HashMap.class);
        Assert.assertEquals(3, val.size());
        Set<String> coreAttributes = Arrays.stream(CoreAttributes.values()).map(CoreAttributes::key).collect(Collectors.toSet());
        val.keySet().forEach(( k) -> Assert.assertTrue(coreAttributes.contains(k)));
    }

    @Test
    public void testAttributesRegex() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new AttributesToJSON());
        testRunner.setVariable("regex", "delimited\\.header\\.column\\.[0-9]+");
        testRunner.setProperty(ATTRIBUTES_REGEX, "${regex}");
        testRunner.setProperty(ATTRIBUTES_LIST, "test, test1");
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("delimited.header.column.1", "Registry");
        attributes.put("delimited.header.column.2", "Assignment");
        attributes.put("delimited.header.column.3", "Organization Name");
        attributes.put("delimited.header.column.4", "Organization Address");
        attributes.put("delimited.footer.column.1", "not included");
        attributes.put("test", "test");
        attributes.put("test1", "test1");
        testRunner.enqueue("".getBytes(), attributes);
        testRunner.run();
        testRunner.assertTransferCount(REL_FAILURE, 0);
        testRunner.assertTransferCount(REL_SUCCESS, 1);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Map<String, String> val = new ObjectMapper().readValue(flowFile.getAttribute(JSON_ATTRIBUTE_NAME), HashMap.class);
        Assert.assertTrue(val.keySet().contains("delimited.header.column.1"));
        Assert.assertTrue(val.keySet().contains("delimited.header.column.2"));
        Assert.assertTrue(val.keySet().contains("delimited.header.column.3"));
        Assert.assertTrue(val.keySet().contains("delimited.header.column.4"));
        Assert.assertTrue((!(val.keySet().contains("delimited.footer.column.1"))));
        Assert.assertTrue(val.keySet().contains("test"));
        Assert.assertTrue(val.keySet().contains("test1"));
    }
}

