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


import ModifyBytes.END_OFFSET;
import ModifyBytes.REL_SUCCESS;
import ModifyBytes.REMOVE_ALL;
import ModifyBytes.START_OFFSET;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestModifyBytes {
    /* ModifyBytes treats FlowFiles as binary content, not line oriented text, so the tests use byte offsets
    and are not line oriented.  Any changes to the test data files needs to be considered based on the
    byte offset impacts of any end-of-line changing edits.

    The test data files are assumed to be in Unix end-of-line format (i.e. LF).
     */
    private final Path testFilePath = Paths.get("src/test/resources/TestModifyBytes/testFile.txt");

    private final Path noFooterPath = Paths.get("src/test/resources/TestModifyBytes/noFooter.txt");

    private final Path noHeaderPath = Paths.get("src/test/resources/TestModifyBytes/noHeader.txt");

    private final Path noFooterNoHeaderPath = Paths.get("src/test/resources/TestModifyBytes/noFooter_noHeader.txt");

    private final File testFile = testFilePath.toFile();

    private final File noFooterFile = noFooterPath.toFile();

    private final File noHeaderFile = noHeaderPath.toFile();

    private final File noFooterNoHeaderFile = noFooterNoHeaderPath.toFile();

    @Test
    public void testReturnEmptyFile() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "1 MB");
        runner.setProperty(END_OFFSET, "1 MB");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("".getBytes("UTF-8"));
    }

    @Test
    public void testReturnSameFile() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "0 MB");
        runner.setProperty(END_OFFSET, "0 MB");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(testFile);
    }

    @Test
    public void testRemoveHeader() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "12 B");// REMOVE - '<<<HEADER>>>'

        runner.setProperty(END_OFFSET, "0 MB");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        out.assertContentEquals(noHeaderFile);
    }

    @Test
    public void testRemoveHeaderEL() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "${numBytes}");// REMOVE - '<<<HEADER>>>'

        runner.setProperty(END_OFFSET, "0 MB");
        runner.enqueue(testFilePath, new HashMap<String, String>() {
            {
                put("numBytes", "12 B");
            }
        });
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        out.assertContentEquals(noHeaderFile);
    }

    @Test
    public void testKeepFooter() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "181 B");
        runner.setProperty(END_OFFSET, "0 B");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        out.assertContentEquals("<<<FOOTER>>>".getBytes("UTF-8"));
    }

    @Test
    public void testKeepHeader() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "0 B");
        runner.setProperty(END_OFFSET, "181 B");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("<<<HEADER>>>".getBytes("UTF-8"));
    }

    @Test
    public void testKeepHeaderEL() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "0 B");
        runner.setProperty(END_OFFSET, "${numBytes}");
        runner.enqueue(testFilePath, new HashMap<String, String>() {
            {
                put("numBytes", "181 B");
            }
        });
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("<<<HEADER>>>".getBytes("UTF-8"));
    }

    @Test
    public void testRemoveFooter() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "0 B");
        runner.setProperty(END_OFFSET, "12 B");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        out.assertContentEquals(noFooterFile);
    }

    @Test
    public void testRemoveHeaderAndFooter() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "12 B");
        runner.setProperty(END_OFFSET, "12 B");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        out.assertContentEquals(noFooterNoHeaderFile);
    }

    @Test
    public void testReturnZeroByteFile() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "97 B");
        runner.setProperty(END_OFFSET, "97 B");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("".getBytes("UTF-8"));
    }

    @Test
    public void testDew() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "94 B");
        runner.setProperty(END_OFFSET, "96 B");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        out.assertContentEquals("Dew".getBytes("UTF-8"));
    }

    @Test
    public void testRemoveAllContent() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "0 B");
        runner.setProperty(END_OFFSET, "0 B");
        runner.setProperty(REMOVE_ALL, "true");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(0L, out.getSize());
    }

    @Test
    public void testRemoveAllOverridesWhenSet() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "10 B");
        runner.setProperty(END_OFFSET, "10 B");
        runner.setProperty(REMOVE_ALL, "true");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(0L, out.getSize());
    }

    @Test
    public void testRemoveAllNoOverridesWhenFalse() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(START_OFFSET, "10 B");
        runner.setProperty(END_OFFSET, "10 B");
        runner.setProperty(REMOVE_ALL, "false");
        runner.enqueue(testFilePath);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(((testFile.length()) - 20), out.getSize());
    }

    @Test
    public void testCheckAllowableValues() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ModifyBytes());
        runner.setProperty(REMOVE_ALL, "maybe");
        runner.assertNotValid();
        runner.setProperty(REMOVE_ALL, "true");
        runner.assertValid();
        runner.setProperty(REMOVE_ALL, "false");
        runner.assertValid();
        runner.setProperty(REMOVE_ALL, "certainly");
        runner.assertNotValid();
    }
}

