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


import FetchHBaseRow.AUTHORIZATIONS;
import FetchHBaseRow.COLUMNS;
import FetchHBaseRow.DESTINATION;
import FetchHBaseRow.DESTINATION_ATTRIBUTES;
import FetchHBaseRow.DESTINATION_CONTENT;
import FetchHBaseRow.ENCODING_BASE64;
import FetchHBaseRow.HBASE_ROW_ATTR;
import FetchHBaseRow.JSON_FORMAT;
import FetchHBaseRow.JSON_FORMAT_QUALIFIER_AND_VALUE;
import FetchHBaseRow.JSON_VALUE_ENCODING;
import FetchHBaseRow.REL_FAILURE;
import FetchHBaseRow.REL_NOT_FOUND;
import FetchHBaseRow.REL_SUCCESS;
import FetchHBaseRow.ROW_ID;
import FetchHBaseRow.TABLE_NAME;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestFetchHBaseRow {
    private FetchHBaseRow proc;

    private MockHBaseClientService hBaseClientService;

    private TestRunner runner;

    @Test
    public void testColumnsValidation() {
        runner.setProperty(AUTHORIZATIONS, "");
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.assertValid();
        runner.setProperty(COLUMNS, "cf1:cq1");
        runner.assertValid();
        runner.setProperty(COLUMNS, "cf1");
        runner.assertValid();
        runner.setProperty(COLUMNS, "cf1:cq1,cf2:cq2,cf3:cq3");
        runner.assertValid();
        runner.setProperty(COLUMNS, "cf1,cf2:cq1,cf3");
        runner.assertValid();
        runner.setProperty(COLUMNS, "cf1 cf2,cf3");
        runner.assertNotValid();
        runner.setProperty(COLUMNS, "cf1:,cf2,cf3");
        runner.assertNotValid();
        runner.setProperty(COLUMNS, "cf1:cq1,");
        runner.assertNotValid();
    }

    @Test
    public void testNoIncomingFlowFile() {
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(AUTHORIZATIONS, "");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }

    @Test
    public void testInvalidTableName() {
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(AUTHORIZATIONS, "");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }

    @Test
    public void testInvalidRowId() {
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "${hbase.row}");
        runner.setProperty(AUTHORIZATIONS, "");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchToAttributesWithStringValues() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(AUTHORIZATIONS, "");
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(HBASE_ROW_ATTR, (((((("{\"row\":\"row1\", \"cells\": [" + "{\"fam\":\"nifi\",\"qual\":\"cq1\",\"val\":\"val1\",\"ts\":") + ts1) + "}, ") + "{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":") + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchSpecificColumnsToAttributesWithStringValues() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(AUTHORIZATIONS, "");
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(COLUMNS, "nifi:cq2");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(HBASE_ROW_ATTR, (("{\"row\":\"row1\", \"cells\": [{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":" + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchToAttributesWithBase64Values() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.setProperty(JSON_VALUE_ENCODING, ENCODING_BASE64);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final String rowBase64 = Base64.encodeBase64String("row1".getBytes(StandardCharsets.UTF_8));
        final String fam1Base64 = Base64.encodeBase64String("nifi".getBytes(StandardCharsets.UTF_8));
        final String qual1Base64 = Base64.encodeBase64String("cq1".getBytes(StandardCharsets.UTF_8));
        final String val1Base64 = Base64.encodeBase64String("val1".getBytes(StandardCharsets.UTF_8));
        final String fam2Base64 = Base64.encodeBase64String("nifi".getBytes(StandardCharsets.UTF_8));
        final String qual2Base64 = Base64.encodeBase64String("cq2".getBytes(StandardCharsets.UTF_8));
        final String val2Base64 = Base64.encodeBase64String("val2".getBytes(StandardCharsets.UTF_8));
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(HBASE_ROW_ATTR, (((((((((((((((((((("{\"row\":\"" + rowBase64) + "\", \"cells\": [") + "{\"fam\":\"") + fam1Base64) + "\",\"qual\":\"") + qual1Base64) + "\",\"val\":\"") + val1Base64) + "\",\"ts\":") + ts1) + "}, ") + "{\"fam\":\"") + fam2Base64) + "\",\"qual\":\"") + qual2Base64) + "\",\"val\":\"") + val2Base64) + "\",\"ts\":") + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchToAttributesNoResults() {
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 1);
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchToContentWithStringValues() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals((((((("{\"row\":\"row1\", \"cells\": [" + "{\"fam\":\"nifi\",\"qual\":\"cq1\",\"val\":\"val1\",\"ts\":") + ts1) + "}, ") + "{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":") + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchSpecificColumnsToContentWithStringValues() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.setProperty(COLUMNS, "nifi:cq2");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals((("{\"row\":\"row1\", \"cells\": [{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":" + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchSpecificColumnsToContentWithBase64() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.setProperty(JSON_VALUE_ENCODING, ENCODING_BASE64);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final String rowBase64 = Base64.encodeBase64String("row1".getBytes(StandardCharsets.UTF_8));
        final String fam1Base64 = Base64.encodeBase64String("nifi".getBytes(StandardCharsets.UTF_8));
        final String qual1Base64 = Base64.encodeBase64String("cq1".getBytes(StandardCharsets.UTF_8));
        final String val1Base64 = Base64.encodeBase64String("val1".getBytes(StandardCharsets.UTF_8));
        final String fam2Base64 = Base64.encodeBase64String("nifi".getBytes(StandardCharsets.UTF_8));
        final String qual2Base64 = Base64.encodeBase64String("cq2".getBytes(StandardCharsets.UTF_8));
        final String val2Base64 = Base64.encodeBase64String("val2".getBytes(StandardCharsets.UTF_8));
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals((((((((((((((((((((("{\"row\":\"" + rowBase64) + "\", \"cells\": [") + "{\"fam\":\"") + fam1Base64) + "\",\"qual\":\"") + qual1Base64) + "\",\"val\":\"") + val1Base64) + "\",\"ts\":") + ts1) + "}, ") + "{\"fam\":\"") + fam2Base64) + "\",\"qual\":\"") + qual2Base64) + "\",\"val\":\"") + val2Base64) + "\",\"ts\":") + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchToContentWithQualifierAndValueJSON() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        hBaseClientService.addResult("row1", cells, System.currentTimeMillis());
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        runner.setProperty(JSON_FORMAT, JSON_FORMAT_QUALIFIER_AND_VALUE);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals("{\"cq1\":\"val1\", \"cq2\":\"val2\"}");
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchWithExpressionLanguage() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(ROW_ID, "${hbase.row}");
        runner.setProperty(COLUMNS, "${hbase.cols}");
        runner.setProperty(DESTINATION, DESTINATION_CONTENT);
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("hbase.table", "table1");
        attributes.put("hbase.row", "row1");
        attributes.put("hbase.cols", "nifi:cq2");
        runner.enqueue("trigger flow file", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals((("{\"row\":\"row1\", \"cells\": [{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":" + ts1) + "}]}"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testFetchWhenScanThrowsException() {
        hBaseClientService.setThrowException(true);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(ROW_ID, "row1");
        runner.setProperty(DESTINATION, DESTINATION_ATTRIBUTES);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_NOT_FOUND, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }
}

