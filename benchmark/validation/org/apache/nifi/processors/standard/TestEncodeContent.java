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


import EncodeContent.BASE32_ENCODING;
import EncodeContent.BASE64_ENCODING;
import EncodeContent.DECODE_MODE;
import EncodeContent.ENCODE_MODE;
import EncodeContent.ENCODING;
import EncodeContent.HEX_ENCODING;
import EncodeContent.MODE;
import EncodeContent.REL_FAILURE;
import EncodeContent.REL_SUCCESS;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestEncodeContent {
    @Test
    public void testBase64RoundTrip() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, ENCODE_MODE);
        testRunner.setProperty(ENCODING, BASE64_ENCODING);
        testRunner.enqueue(Paths.get("src/test/resources/hello.txt"));
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        testRunner.assertQueueEmpty();
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.enqueue(flowFile);
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(new File("src/test/resources/hello.txt"));
    }

    @Test
    public void testFailDecodeNotBase64() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.setProperty(ENCODING, BASE64_ENCODING);
        testRunner.enqueue(Paths.get("src/test/resources/hello.txt"));
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testFailDecodeNotBase64ButIsAMultipleOfFourBytes() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.setProperty(ENCODING, BASE64_ENCODING);
        testRunner.enqueue("four@@@@multiple".getBytes());
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testBase32RoundTrip() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, ENCODE_MODE);
        testRunner.setProperty(ENCODING, BASE32_ENCODING);
        testRunner.enqueue(Paths.get("src/test/resources/hello.txt"));
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        testRunner.assertQueueEmpty();
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.enqueue(flowFile);
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(new File("src/test/resources/hello.txt"));
    }

    @Test
    public void testFailDecodeNotBase32() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.setProperty(ENCODING, BASE32_ENCODING);
        testRunner.enqueue(Paths.get("src/test/resources/hello.txt"));
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testHexRoundTrip() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, ENCODE_MODE);
        testRunner.setProperty(ENCODING, HEX_ENCODING);
        testRunner.enqueue(Paths.get("src/test/resources/hello.txt"));
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        testRunner.assertQueueEmpty();
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.enqueue(flowFile);
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        flowFile = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(new File("src/test/resources/hello.txt"));
    }

    @Test
    public void testFailDecodeNotHex() throws IOException {
        final TestRunner testRunner = TestRunners.newTestRunner(new EncodeContent());
        testRunner.setProperty(MODE, DECODE_MODE);
        testRunner.setProperty(ENCODING, HEX_ENCODING);
        testRunner.enqueue(Paths.get("src/test/resources/hello.txt"));
        testRunner.clearTransferState();
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }
}

