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


import ScanHBase.BULK_SIZE;
import ScanHBase.COLUMNS;
import ScanHBase.END_ROW;
import ScanHBase.HBASE_ROWS_COUNT_ATTR;
import ScanHBase.JSON_FORMAT;
import ScanHBase.JSON_FORMAT_QUALIFIER_AND_VALUE;
import ScanHBase.LIMIT_ROWS;
import ScanHBase.REL_FAILURE;
import ScanHBase.REL_ORIGINAL;
import ScanHBase.REL_SUCCESS;
import ScanHBase.REVERSED_SCAN;
import ScanHBase.START_ROW;
import ScanHBase.TABLE_NAME;
import ScanHBase.TIME_RANGE_MAX;
import ScanHBase.TIME_RANGE_MIN;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestScanHBase {
    private ScanHBase proc;

    private MockHBaseClientService hBaseClientService;

    private TestRunner runner;

    @Test
    public void testColumnsValidation() {
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row1");
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
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row1");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }

    @Test
    public void testInvalidTableName() {
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row1");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }

    @Test
    public void testResultsNotFound() {
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row1");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        flowFile.assertAttributeEquals("scanhbase.results.found", Boolean.FALSE.toString());
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testScanToContentWithStringValues() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        hBaseClientService.addResult("row2", cells, ts1);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row2");
        runner.setProperty(TIME_RANGE_MIN, "0");
        runner.setProperty(TIME_RANGE_MAX, "1111111110");
        runner.setProperty(LIMIT_ROWS, "10");
        runner.setProperty(REVERSED_SCAN, "false");
        runner.setProperty(BULK_SIZE, "10");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals(((((((((((((("[{\"row\":\"row1\", \"cells\": [" + "{\"fam\":\"nifi\",\"qual\":\"cq1\",\"val\":\"val1\",\"ts\":") + ts1) + "}, ") + "{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":") + ts1) + "}]},\n") + "{\"row\":\"row2\", \"cells\": [") + "{\"fam\":\"nifi\",\"qual\":\"cq1\",\"val\":\"val1\",\"ts\":") + ts1) + "}, ") + "{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":") + ts1) + "}]}]"));
        flowFile.assertAttributeEquals(HBASE_ROWS_COUNT_ATTR, "2");
        flowFile = runner.getFlowFilesForRelationship(REL_ORIGINAL).get(0);
        flowFile.assertAttributeEquals("scanhbase.results.found", Boolean.TRUE.toString());
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testScanBulkSize() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        for (int i = 0; i < 15; i++) {
            hBaseClientService.addResult(("row" + i), cells, System.currentTimeMillis());
        }
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(START_ROW, "${hbase.row}1");
        runner.setProperty(END_ROW, "${hbase.row}2");
        runner.setProperty(COLUMNS, "${hbase.cols}");
        runner.setProperty(TIME_RANGE_MIN, "${tr_min}");
        runner.setProperty(TIME_RANGE_MAX, "${tr_max}");
        runner.setProperty(LIMIT_ROWS, "${limit}");
        runner.setProperty(BULK_SIZE, "${bulk.size}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("hbase.table", "table1");
        attributes.put("hbase.row", "row");
        attributes.put("hbase.cols", "nifi:cq2");
        attributes.put("tr_min", "10000000");
        attributes.put("tr_max", "10000001");
        attributes.put("limit", "1000");
        attributes.put("bulk.size", "10");
        runner.enqueue("trigger flow file", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 2);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertAttributeEquals(HBASE_ROWS_COUNT_ATTR, "10");
        flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        flowFile.assertAttributeEquals(HBASE_ROWS_COUNT_ATTR, "5");
    }

    @Test
    public void testScanBatchSizeTimesOfBulkSize() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        for (int i = 0; i < 1000; i++) {
            hBaseClientService.addResult(("row" + i), cells, System.currentTimeMillis());
        }
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(START_ROW, "${hbase.row}1");
        runner.setProperty(END_ROW, "${hbase.row}2");
        runner.setProperty(COLUMNS, "${hbase.cols}");
        runner.setProperty(TIME_RANGE_MIN, "${tr_min}");
        runner.setProperty(TIME_RANGE_MAX, "${tr_max}");
        runner.setProperty(LIMIT_ROWS, "${limit}");
        runner.setProperty(BULK_SIZE, "${bulk.size}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("hbase.table", "table1");
        attributes.put("hbase.row", "row");
        attributes.put("hbase.cols", "nifi:cq2");
        attributes.put("tr_min", "10000000");
        attributes.put("tr_max", "10000001");
        attributes.put("limit", "1000");
        attributes.put("bulk.size", "100");
        runner.enqueue("trigger flow file", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 10);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        runner.getFlowFilesForRelationship(REL_SUCCESS).forEach(( ff) -> {
            ff.assertAttributeEquals(ScanHBase.HBASE_ROWS_COUNT_ATTR, "100");
            Assert.assertNotEquals(0, ff.getId());// since total amount of rows is a multiplication of bulkSize, original FF (with id=0) shouldn't be present on output.

        });
    }

    @Test
    public void testScanBatchSizeTimesCutBulkSize() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        for (int i = 0; i < 1102; i++) {
            hBaseClientService.addResult(("row" + i), cells, System.currentTimeMillis());
        }
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(START_ROW, "${hbase.row}1");
        runner.setProperty(END_ROW, "${hbase.row}2");
        runner.setProperty(COLUMNS, "${hbase.cols}");
        runner.setProperty(TIME_RANGE_MIN, "${tr_min}");
        runner.setProperty(TIME_RANGE_MAX, "${tr_max}");
        runner.setProperty(LIMIT_ROWS, "${limit}");
        runner.setProperty(BULK_SIZE, "${bulk.size}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("hbase.table", "table1");
        attributes.put("hbase.row", "row");
        attributes.put("hbase.cols", "nifi:cq2");
        attributes.put("tr_min", "10000000");
        attributes.put("tr_max", "10000001");
        attributes.put("limit", "1000");
        attributes.put("bulk.size", "110");
        runner.enqueue("trigger flow file", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 11);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        List<MockFlowFile> ffs = runner.getFlowFilesForRelationship(REL_SUCCESS);
        int i = 0;
        for (MockFlowFile ff : ffs)
            ff.assertAttributeEquals(HBASE_ROWS_COUNT_ATTR, new String(((i++) < 10 ? "110" : "2")));
        // last ff should have only 2

    }

    @Test
    public void testScanToContentWithQualifierAndValueJSON() {
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        hBaseClientService.addResult("row1", cells, System.currentTimeMillis());
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row1");
        runner.setProperty(JSON_FORMAT, JSON_FORMAT_QUALIFIER_AND_VALUE);
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals("[{\"cq1\":\"val1\", \"cq2\":\"val2\"}]");
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testScanWithExpressionLanguage() {
        final Map<String, String> cells = new HashMap<>();
        // cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        runner.setProperty(TABLE_NAME, "${hbase.table}");
        runner.setProperty(START_ROW, "${hbase.row}1");
        runner.setProperty(END_ROW, "${hbase.row}2");
        runner.setProperty(COLUMNS, "${hbase.cols}");
        runner.setProperty(TIME_RANGE_MIN, "${tr_min}");
        runner.setProperty(TIME_RANGE_MAX, "${tr_max}");
        runner.setProperty(LIMIT_ROWS, "${limit}");
        runner.setProperty(BULK_SIZE, "${bulk.size}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("hbase.table", "table1");
        attributes.put("hbase.row", "row");
        attributes.put("hbase.cols", "nifi:cq2");
        attributes.put("tr_min", "10000000");
        attributes.put("tr_max", "10000001");
        attributes.put("limit", "1000");
        attributes.put("bulk.size", "10");
        runner.enqueue("trigger flow file", attributes);
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_ORIGINAL, 1);
        final MockFlowFile flowFile = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        flowFile.assertContentEquals((("[{\"row\":\"row1\", \"cells\": [{\"fam\":\"nifi\",\"qual\":\"cq2\",\"val\":\"val2\",\"ts\":" + ts1) + "}]}]"));
        Assert.assertEquals(1, hBaseClientService.getNumScans());
    }

    @Test
    public void testScanWhenScanThrowsException() {
        hBaseClientService.setThrowException(true);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row1");
        runner.enqueue("trigger flow file");
        runner.run();
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }

    @Test
    public void testScanWhenScanThrowsExceptionAfterLineN() {
        hBaseClientService.setLinesBeforeException(1);
        final Map<String, String> cells = new HashMap<>();
        cells.put("cq1", "val1");
        cells.put("cq2", "val2");
        final long ts1 = 123456789;
        hBaseClientService.addResult("row1", cells, ts1);
        hBaseClientService.addResult("row2", cells, ts1);
        runner.setProperty(TABLE_NAME, "table1");
        runner.setProperty(START_ROW, "row1");
        runner.setProperty(END_ROW, "row2");
        runner.enqueue("trigger flow file");
        runner.run();
        hBaseClientService.setLinesBeforeException((-1));
        runner.assertTransferCount(REL_FAILURE, 1);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_ORIGINAL, 0);
        Assert.assertEquals(0, hBaseClientService.getNumScans());
    }
}

