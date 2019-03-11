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
package org.apache.nifi.hbase;


import PutHBaseCell.BATCH_SIZE;
import PutHBaseCell.COLUMN_FAMILY;
import PutHBaseCell.COLUMN_QUALIFIER;
import PutHBaseCell.HBASE_CLIENT_SERVICE;
import PutHBaseCell.REL_FAILURE;
import PutHBaseCell.REL_SUCCESS;
import PutHBaseCell.ROW_ID;
import PutHBaseCell.ROW_ID_ENCODING_BINARY;
import PutHBaseCell.ROW_ID_ENCODING_STRATEGY;
import PutHBaseCell.TABLE_NAME;
import PutHBaseCell.TIMESTAMP;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.hbase.put.PutFlowFile;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestPutHBaseCell {
    @Test
    public void testSingleFlowFileNoTimestamp() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final TestRunner runner = TestRunners.newTestRunner(PutHBaseCell.class);
        runner.setProperty(TABLE_NAME, tableName);
        runner.setProperty(ROW_ID, row);
        runner.setProperty(COLUMN_FAMILY, columnFamily);
        runner.setProperty(COLUMN_QUALIFIER, columnQualifier);
        runner.setProperty(BATCH_SIZE, "1");
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final String content = "some content";
        runner.enqueue(content.getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        Assert.assertNotNull(hBaseClient.getFlowFilePuts());
        Assert.assertEquals(1, hBaseClient.getFlowFilePuts().size());
        List<PutFlowFile> puts = hBaseClient.getFlowFilePuts().get(tableName);
        Assert.assertEquals(1, puts.size());
        verifyPut(row, columnFamily, columnQualifier, null, content, puts.get(0));
        Assert.assertEquals(1, runner.getProvenanceEvents().size());
    }

    @Test
    public void testSingleFlowFileWithTimestamp() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final Long timestamp = 1L;
        final TestRunner runner = TestRunners.newTestRunner(PutHBaseCell.class);
        runner.setProperty(TABLE_NAME, tableName);
        runner.setProperty(ROW_ID, row);
        runner.setProperty(COLUMN_FAMILY, columnFamily);
        runner.setProperty(COLUMN_QUALIFIER, columnQualifier);
        runner.setProperty(TIMESTAMP, timestamp.toString());
        runner.setProperty(BATCH_SIZE, "1");
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final String content = "some content";
        runner.enqueue(content.getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        Assert.assertNotNull(hBaseClient.getFlowFilePuts());
        Assert.assertEquals(1, hBaseClient.getFlowFilePuts().size());
        List<PutFlowFile> puts = hBaseClient.getFlowFilePuts().get(tableName);
        Assert.assertEquals(1, puts.size());
        verifyPut(row, columnFamily, columnQualifier, timestamp, content, puts.get(0));
        Assert.assertEquals(1, runner.getProvenanceEvents().size());
    }

    @Test
    public void testSingleFlowFileWithInvalidTimestamp() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final String timestamp = "not-a-timestamp";
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        runner.setProperty(TIMESTAMP, "${hbase.timestamp}");
        runner.setProperty(BATCH_SIZE, "1");
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final String content = "some content";
        final Map<String, String> attributes = getAttributeMapWithEL(tableName, row, columnFamily, columnQualifier);
        attributes.put("hbase.timestamp", timestamp);
        runner.enqueue(content.getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testSingleFlowFileWithEL() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final Long timestamp = 1L;
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        runner.setProperty(TIMESTAMP, "${hbase.timestamp}");
        runner.setProperty(BATCH_SIZE, "1");
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final String content = "some content";
        final Map<String, String> attributes = getAttributeMapWithEL(tableName, row, columnFamily, columnQualifier);
        attributes.put("hbase.timestamp", timestamp.toString());
        runner.enqueue(content.getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        Assert.assertNotNull(hBaseClient.getFlowFilePuts());
        Assert.assertEquals(1, hBaseClient.getFlowFilePuts().size());
        List<PutFlowFile> puts = hBaseClient.getFlowFilePuts().get(tableName);
        Assert.assertEquals(1, puts.size());
        verifyPut(row, columnFamily, columnQualifier, timestamp, content, puts.get(0));
        Assert.assertEquals(1, runner.getProvenanceEvents().size());
    }

    @Test
    public void testSingleFlowFileWithELMissingAttributes() throws IOException, InitializationException {
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        runner.setProperty(BATCH_SIZE, "1");
        final MockHBaseClientService hBaseClient = new MockHBaseClientService();
        runner.addControllerService("hbaseClient", hBaseClient);
        runner.enableControllerService(hBaseClient);
        runner.setProperty(HBASE_CLIENT_SERVICE, "hbaseClient");
        getHBaseClientService(runner);
        final String content = "some content";
        runner.enqueue(content.getBytes("UTF-8"), new HashMap<String, String>());
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 1);
        Assert.assertEquals(0, runner.getProvenanceEvents().size());
    }

    @Test
    public void testMultipleFlowFileWithELOneMissingAttributes() throws IOException, InitializationException {
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        runner.setProperty(BATCH_SIZE, "10");
        final MockHBaseClientService hBaseClient = new MockHBaseClientService();
        runner.addControllerService("hbaseClient", hBaseClient);
        runner.enableControllerService(hBaseClient);
        runner.setProperty(HBASE_CLIENT_SERVICE, "hbaseClient");
        getHBaseClientService(runner);
        // this one will go to failure
        final String content = "some content";
        runner.enqueue(content.getBytes("UTF-8"), new HashMap<String, String>());
        // this will go to success
        final String content2 = "some content2";
        final Map<String, String> attributes = getAttributeMapWithEL("table", "row", "cf", "cq");
        runner.enqueue(content2.getBytes("UTF-8"), attributes);
        runner.run();
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        Assert.assertEquals(1, runner.getProvenanceEvents().size());
    }

    @Test
    public void testMultipleFlowFilesSameTableDifferentRow() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row1 = "row1";
        final String row2 = "row2";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final String content1 = "some content1";
        final Map<String, String> attributes1 = getAttributeMapWithEL(tableName, row1, columnFamily, columnQualifier);
        runner.enqueue(content1.getBytes("UTF-8"), attributes1);
        final String content2 = "some content1";
        final Map<String, String> attributes2 = getAttributeMapWithEL(tableName, row2, columnFamily, columnQualifier);
        runner.enqueue(content2.getBytes("UTF-8"), attributes2);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outFile.assertContentEquals(content1);
        Assert.assertNotNull(hBaseClient.getFlowFilePuts());
        Assert.assertEquals(1, hBaseClient.getFlowFilePuts().size());
        List<PutFlowFile> puts = hBaseClient.getFlowFilePuts().get(tableName);
        Assert.assertEquals(2, puts.size());
        verifyPut(row1, columnFamily, columnQualifier, null, content1, puts.get(0));
        verifyPut(row2, columnFamily, columnQualifier, null, content2, puts.get(1));
        Assert.assertEquals(2, runner.getProvenanceEvents().size());
    }

    @Test
    public void testMultipleFlowFilesSameTableDifferentRowFailure() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row1 = "row1";
        final String row2 = "row2";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        hBaseClient.setThrowException(true);
        final String content1 = "some content1";
        final Map<String, String> attributes1 = getAttributeMapWithEL(tableName, row1, columnFamily, columnQualifier);
        runner.enqueue(content1.getBytes("UTF-8"), attributes1);
        final String content2 = "some content1";
        final Map<String, String> attributes2 = getAttributeMapWithEL(tableName, row2, columnFamily, columnQualifier);
        runner.enqueue(content2.getBytes("UTF-8"), attributes2);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 2);
        Assert.assertEquals(0, runner.getProvenanceEvents().size());
    }

    @Test
    public void testMultipleFlowFilesSameTableSameRow() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "row1";
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final PutHBaseCell proc = new PutHBaseCell();
        final TestRunner runner = getTestRunnerWithEL(proc);
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final String content1 = "some content1";
        final Map<String, String> attributes1 = getAttributeMapWithEL(tableName, row, columnFamily, columnQualifier);
        runner.enqueue(content1.getBytes("UTF-8"), attributes1);
        final String content2 = "some content1";
        runner.enqueue(content2.getBytes("UTF-8"), attributes1);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outFile.assertContentEquals(content1);
        Assert.assertNotNull(hBaseClient.getFlowFilePuts());
        Assert.assertEquals(1, hBaseClient.getFlowFilePuts().size());
        List<PutFlowFile> puts = hBaseClient.getFlowFilePuts().get(tableName);
        Assert.assertEquals(2, puts.size());
        verifyPut(row, columnFamily, columnQualifier, null, content1, puts.get(0));
        verifyPut(row, columnFamily, columnQualifier, null, content2, puts.get(1));
        Assert.assertEquals(2, runner.getProvenanceEvents().size());
    }

    @Test
    public void testSingleFlowFileWithBinaryRowKey() throws IOException, InitializationException {
        final String tableName = "nifi";
        final String row = "\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00" + (((((((("\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00" + "\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00") + "\\x00\\x00\\x00\\x00\\x00\\x01\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00") + "\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x01\\x01\\x00\\x00\\x00\\x00\\x00") + "\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00") + "\\x00\\x00\\x00\\x01\\x00\\x00\\x01\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00\\x00") + "\\x01\\x00\\x00\\x01\\x00\\x00\\x00\\x00\\x01\\x01\\x01\\x00\\x01\\x00\\x01\\x01\\x01\\x00\\x00\\x00") + "\\x00\\x00\\x00\\x01\\x01\\x01\\x01\\x00\\x00\\x00\\x00\\x00\\x00\\x01\\x01\\x00\\x01\\x00\\x01\\x00") + "\\x00\\x01\\x01\\x01\\x01\\x00\\x00\\x01\\x01\\x01\\x00\\x01\\x00\\x00");
        final String columnFamily = "family1";
        final String columnQualifier = "qualifier1";
        final TestRunner runner = TestRunners.newTestRunner(PutHBaseCell.class);
        runner.setProperty(TABLE_NAME, tableName);
        runner.setProperty(ROW_ID, row);
        runner.setProperty(ROW_ID_ENCODING_STRATEGY, ROW_ID_ENCODING_BINARY.getValue());
        runner.setProperty(COLUMN_FAMILY, columnFamily);
        runner.setProperty(COLUMN_QUALIFIER, columnQualifier);
        runner.setProperty(BATCH_SIZE, "1");
        final MockHBaseClientService hBaseClient = getHBaseClientService(runner);
        final byte[] expectedRowKey = hBaseClient.toBytesBinary(row);
        final String content = "some content";
        runner.enqueue(content.getBytes("UTF-8"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        outFile.assertContentEquals(content);
        Assert.assertNotNull(hBaseClient.getFlowFilePuts());
        Assert.assertEquals(1, hBaseClient.getFlowFilePuts().size());
        List<PutFlowFile> puts = hBaseClient.getFlowFilePuts().get(tableName);
        Assert.assertEquals(1, puts.size());
        verifyPut(expectedRowKey, columnFamily.getBytes(StandardCharsets.UTF_8), columnQualifier.getBytes(StandardCharsets.UTF_8), null, content, puts.get(0));
        Assert.assertEquals(1, runner.getProvenanceEvents().size());
    }
}

