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


import AbstractJsonPathProcessor.NULL_STRING_OPTION;
import CoreAttributes.FILENAME;
import SplitJson.ARRAY_JSON_PATH_EXPRESSION;
import SplitJson.NULL_VALUE_DEFAULT_REPRESENTATION;
import SplitJson.REL_FAILURE;
import SplitJson.REL_ORIGINAL;
import SplitJson.REL_SPLIT;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.apache.nifi.flowfile.FlowFile;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.processor.io.OutputStreamCallback;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import static SplitJson.REL_FAILURE;


public class TestSplitJson {
    private static final Path JSON_SNIPPET = Paths.get("src/test/resources/TestJson/json-sample.json");

    private static final Path XML_SNIPPET = Paths.get("src/test/resources/TestXml/xml-snippet.xml");

    @Test(expected = AssertionError.class)
    public void testInvalidJsonPath() {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$..");
        Assert.fail("An improper JsonPath expression was not detected as being invalid.");
    }

    @Test
    public void testInvalidJsonDocument() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$");
        testRunner.enqueue(TestSplitJson.XML_SNIPPET);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        // Verify that the content was unchanged
        out.assertContentEquals(TestSplitJson.XML_SNIPPET);
    }

    @Test
    public void testSplit_nonArrayResult() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$[0]._id");
        testRunner.enqueue(TestSplitJson.JSON_SNIPPET);
        testRunner.run();
        Relationship expectedRel = REL_FAILURE;
        testRunner.assertAllFlowFilesTransferred(expectedRel, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(expectedRel).get(0);
        out.assertContentEquals(TestSplitJson.JSON_SNIPPET);
    }

    @Test
    public void testSplit_arrayResult_oneValue() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$[0].range[?(@ == 0)]");
        testRunner.enqueue(TestSplitJson.JSON_SNIPPET);
        testRunner.run();
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), "1");
        testRunner.assertTransferCount(REL_SPLIT, 1);
        testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertContentEquals(TestSplitJson.JSON_SNIPPET);
        testRunner.getFlowFilesForRelationship(REL_SPLIT).get(0).assertContentEquals("0");
    }

    @Test
    public void testSplit_arrayResult_multipleValues() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$[0].range");
        testRunner.enqueue(TestSplitJson.JSON_SNIPPET);
        testRunner.run();
        int numSplitsExpected = 10;
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0).assertAttributeEquals(FRAGMENT_COUNT.key(), String.valueOf(numSplitsExpected));
        testRunner.assertTransferCount(REL_SPLIT, numSplitsExpected);
        final MockFlowFile originalOut = testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalOut.assertContentEquals(TestSplitJson.JSON_SNIPPET);
    }

    @Test
    public void testSplit_arrayResult_nonScalarValues() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$[*].name");
        testRunner.enqueue(TestSplitJson.JSON_SNIPPET, new HashMap<String, String>() {
            {
                put(FILENAME.key(), "test.json");
            }
        });
        testRunner.run();
        testRunner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile originalFlowFile = testRunner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        originalFlowFile.assertAttributeExists(FRAGMENT_ID.key());
        originalFlowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "7");
        originalFlowFile.assertContentEquals(TestSplitJson.JSON_SNIPPET);
        testRunner.assertTransferCount(REL_SPLIT, 7);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SPLIT).get(0);
        flowFile.assertContentEquals("{\"first\":\"Shaffer\",\"last\":\"Pearson\"}");
        flowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "7");
        flowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "0");
        flowFile.assertAttributeEquals(SEGMENT_ORIGINAL_FILENAME.key(), "test.json");
        flowFile = testRunner.getFlowFilesForRelationship(REL_SPLIT).get(6);
        flowFile.assertAttributeEquals(FRAGMENT_COUNT.key(), "7");
        flowFile.assertAttributeEquals(FRAGMENT_INDEX.key(), "6");
        flowFile.assertAttributeEquals(SEGMENT_ORIGINAL_FILENAME.key(), "test.json");
    }

    @Test
    public void testSplit_pathNotFound() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$.nonexistent");
        testRunner.enqueue(TestSplitJson.JSON_SNIPPET);
        testRunner.run();
        testRunner.assertTransferCount(REL_FAILURE, 1);
        testRunner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals(TestSplitJson.JSON_SNIPPET);
    }

    @Test
    public void testSplit_pathToNullValue() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$.nullField");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.write(ff, new OutputStreamCallback() {
            @Override
            public void process(OutputStream out) throws IOException {
                try (OutputStream outputStream = new BufferedOutputStream(out)) {
                    outputStream.write("{\"stringField\": \"String Value\", \"nullField\": null}".getBytes(StandardCharsets.UTF_8));
                }
            }
        });
        testRunner.enqueue(ff);
        testRunner.run();
        testRunner.assertTransferCount(REL_FAILURE, 1);
    }

    @Test
    public void testSplit_pathToArrayWithNulls_emptyStringRepresentation() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$.arrayOfNulls");
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.write(ff, new OutputStreamCallback() {
            @Override
            public void process(OutputStream out) throws IOException {
                try (OutputStream outputStream = new BufferedOutputStream(out)) {
                    outputStream.write("{\"stringField\": \"String Value\", \"arrayOfNulls\": [null, null, null]}".getBytes(StandardCharsets.UTF_8));
                }
            }
        });
        testRunner.enqueue(ff);
        testRunner.run();
        /* assert that three files were transferred to split and each is empty */
        int expectedFiles = 3;
        testRunner.assertTransferCount(REL_SPLIT, expectedFiles);
        for (int i = 0; i < expectedFiles; i++) {
            testRunner.getFlowFilesForRelationship(REL_SPLIT).get(i).assertContentEquals("");
        }
    }

    @Test
    public void testSplit_pathToArrayWithNulls_nullStringRepresentation() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new SplitJson());
        testRunner.setProperty(ARRAY_JSON_PATH_EXPRESSION, "$.arrayOfNulls");
        testRunner.setProperty(NULL_VALUE_DEFAULT_REPRESENTATION, NULL_STRING_OPTION);
        ProcessSession session = testRunner.getProcessSessionFactory().createSession();
        FlowFile ff = session.create();
        ff = session.write(ff, new OutputStreamCallback() {
            @Override
            public void process(OutputStream out) throws IOException {
                try (OutputStream outputStream = new BufferedOutputStream(out)) {
                    outputStream.write("{\"stringField\": \"String Value\", \"arrayOfNulls\": [null, null, null]}".getBytes(StandardCharsets.UTF_8));
                }
            }
        });
        testRunner.enqueue(ff);
        testRunner.run();
        /* assert that three files were transferred to split and each has the word null in it */
        int expectedFiles = 3;
        testRunner.assertTransferCount(REL_SPLIT, expectedFiles);
        for (int i = 0; i < expectedFiles; i++) {
            testRunner.getFlowFilesForRelationship(REL_SPLIT).get(i).assertContentEquals("null");
        }
    }
}

