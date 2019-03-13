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
package org.apache.nifi.processors.network;


import ParseNetflowv5.DESTINATION_ATTRIBUTES;
import ParseNetflowv5.DESTINATION_CONTENT;
import ParseNetflowv5.FIELDS_DESTINATION;
import ParseNetflowv5.REL_FAILURE;
import ParseNetflowv5.REL_ORIGINAL;
import ParseNetflowv5.REL_SUCCESS;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestParseNetflowv5 {
    private static final byte[] sample1 = new byte[]{ // Header
    0, 5, 0, 1, 4, -48, 19, 36, 88, 71, -44, 73, 0, 0, 0, 0, 0, 0, 17, -22, 0, 0, 0, 0, // Record 1
    10, 0, 0, 2, 10, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 1, 0, 0, 0, 64, 4, -49, 40, -60, 4, -48, 19, 36, 16, -110, 0, 80, 0, 0, 17, 1, 0, 2, 0, 3, 32, 31, 0, 0 };

    private static final byte[] sample2 = new byte[]{ // Header
    0, 5, 0, 3, 4, -48, 19, 36, 88, 71, -44, 73, 0, 0, 0, 0, 0, 0, 17, -22, 0, 0, 0, 0, // Record 1
    10, 0, 0, 2, 10, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 1, 0, 0, 0, 64, 4, -49, 40, -60, 4, -48, 19, 36, 16, -110, 0, 80, 0, 0, 17, 1, 0, 2, 0, 3, 32, 31, 0, 0, // Record 2
    10, 0, 0, 2, 10, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 1, 0, 0, 0, 64, 4, -49, 40, -60, 4, -48, 19, 36, 16, -110, 0, 80, 0, 0, 17, 1, 0, 2, 0, 3, 32, 31, 0, 1, // Record 3
    10, 0, 0, 2, 10, 0, 0, 3, 0, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 1, 0, 0, 0, 64, 4, -49, 40, -60, 4, -48, 19, 36, 16, -110, 0, 80, 0, 0, 17, 1, 0, 2, 0, 3, 32, 31, 0, 2 };

    @Test
    public void testSuccessfulParseToAttributes() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ParseNetflowv5());
        runner.setProperty(FIELDS_DESTINATION, DESTINATION_ATTRIBUTES);
        runner.enqueue(TestParseNetflowv5.sample1);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        mff.assertAttributeEquals("netflowv5.record.dPkts", "1");
        mff.assertAttributeEquals("netflowv5.record.dOctets", "64");
    }

    @Test
    public void testSuccessfulParseToAttributesMultipleRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ParseNetflowv5());
        runner.setProperty(FIELDS_DESTINATION, DESTINATION_ATTRIBUTES);
        runner.enqueue(TestParseNetflowv5.sample2);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 3);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        for (int i = 0; i < 3; i++) {
            final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(i);
            mff.assertAttributeEquals("netflowv5.record.srcaddr", "10.0.0.2");
            mff.assertAttributeEquals("netflowv5.record.dstaddr", "10.0.0.3");
            mff.assertAttributeEquals("netflowv5.record.nexthop", "0.0.0.0");
            mff.assertAttributeEquals("netflowv5.record.input", "3");
            mff.assertAttributeEquals("netflowv5.record.pad2", String.valueOf(i));
        }
    }

    @Test
    public void testSuccessfulParseToContent() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ParseNetflowv5());
        runner.setProperty(FIELDS_DESTINATION, DESTINATION_CONTENT);
        runner.enqueue(TestParseNetflowv5.sample1);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        byte[] rawJson = mff.toByteArray();
        JsonNode record = new ObjectMapper().readTree(rawJson).get("record");
        Assert.assertEquals(3, record.get("input").intValue());
        Assert.assertEquals(5, record.get("output").intValue());
    }

    @Test
    public void testSuccessfulParseToContentMultipleRecords() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ParseNetflowv5());
        runner.setProperty(FIELDS_DESTINATION, DESTINATION_CONTENT);
        runner.enqueue(TestParseNetflowv5.sample2);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 3);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        for (int i = 0; i < 3; i++) {
            final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(i);
            byte[] rawJson = mff.toByteArray();
            JsonNode results = new ObjectMapper().readTree(rawJson);
            JsonNode header = results.get("header");
            JsonNode record = results.get("record");
            Assert.assertEquals(4586, header.get("flow_sequence").longValue());
            Assert.assertEquals(80685252, record.get("first").longValue());
            Assert.assertEquals(80745252, record.get("last").longValue());
            Assert.assertEquals(4242, record.get("srcport").intValue());
            Assert.assertEquals(80, record.get("dstport").intValue());
        }
    }

    @Test
    public void testInvalidPacket() {
        final TestRunner runner = TestRunners.newTestRunner(new ParseNetflowv5());
        runner.enqueue("Junk Data Loaded\n".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testReadUDPPort() throws JsonProcessingException, IOException {
        final TestRunner runner = TestRunners.newTestRunner(new ParseNetflowv5());
        runner.setProperty(FIELDS_DESTINATION, DESTINATION_CONTENT);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("udp.port", "2055");
        runner.enqueue(TestParseNetflowv5.sample1, attributes);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        byte[] rawJson = mff.toByteArray();
        JsonNode results = new ObjectMapper().readTree(rawJson);
        Assert.assertEquals(2055, results.get("port").intValue());
    }
}

