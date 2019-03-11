/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.timelineservice.storage.flow;


import AggregationOperation.SUM;
import AggregationOperation.SUM_FINAL;
import FlowRunColumnFamily.INFO;
import FlowRunTableRW.DEFAULT_TABLE_NAME;
import FlowRunTableRW.TABLE_NAME_CONF_NAME;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Tag;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timelineservice.TimelineEntity;
import org.apache.hadoop.yarn.server.timelineservice.collector.TimelineCollectorContext;
import org.apache.hadoop.yarn.server.timelineservice.storage.HBaseTimelineWriterImpl;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.BaseTableRW;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.HBaseTimelineServerUtils;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.LongConverter;
import org.apache.hadoop.yarn.server.timelineservice.storage.common.TimestampGenerator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the FlowRun and FlowActivity Tables.
 */
public class TestHBaseStorageFlowRunCompaction {
    private static HBaseTestingUtility util;

    private static final String METRIC1 = "MAP_SLOT_MILLIS";

    private static final String METRIC2 = "HDFS_BYTES_READ";

    private final byte[] aRowKey = Bytes.toBytes("a");

    private final byte[] aFamily = Bytes.toBytes("family");

    private final byte[] aQualifier = Bytes.toBytes("qualifier");

    /**
     * writes non numeric data into flow run table.
     * reads it back
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWriteNonNumericData() throws Exception {
        String rowKey = "nonNumericRowKey";
        String column = "nonNumericColumnName";
        String value = "nonNumericValue";
        byte[] rowKeyBytes = Bytes.toBytes(rowKey);
        byte[] columnNameBytes = Bytes.toBytes(column);
        byte[] valueBytes = Bytes.toBytes(value);
        Put p = new Put(rowKeyBytes);
        p.addColumn(INFO.getBytes(), columnNameBytes, valueBytes);
        Configuration hbaseConf = TestHBaseStorageFlowRunCompaction.util.getConfiguration();
        Connection conn = null;
        conn = ConnectionFactory.createConnection(hbaseConf);
        Table flowRunTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME));
        flowRunTable.put(p);
        Get g = new Get(rowKeyBytes);
        Result r = flowRunTable.get(g);
        Assert.assertNotNull(r);
        Assert.assertTrue(((r.size()) >= 1));
        Cell actualValue = r.getColumnLatestCell(INFO.getBytes(), columnNameBytes);
        Assert.assertNotNull(CellUtil.cloneValue(actualValue));
        Assert.assertEquals(Bytes.toString(CellUtil.cloneValue(actualValue)), value);
    }

    @Test
    public void testWriteScanBatchLimit() throws Exception {
        String rowKey = "nonNumericRowKey";
        String column = "nonNumericColumnName";
        String value = "nonNumericValue";
        String column2 = "nonNumericColumnName2";
        String value2 = "nonNumericValue2";
        String column3 = "nonNumericColumnName3";
        String value3 = "nonNumericValue3";
        String column4 = "nonNumericColumnName4";
        String value4 = "nonNumericValue4";
        byte[] rowKeyBytes = Bytes.toBytes(rowKey);
        byte[] columnNameBytes = Bytes.toBytes(column);
        byte[] valueBytes = Bytes.toBytes(value);
        byte[] columnName2Bytes = Bytes.toBytes(column2);
        byte[] value2Bytes = Bytes.toBytes(value2);
        byte[] columnName3Bytes = Bytes.toBytes(column3);
        byte[] value3Bytes = Bytes.toBytes(value3);
        byte[] columnName4Bytes = Bytes.toBytes(column4);
        byte[] value4Bytes = Bytes.toBytes(value4);
        Put p = new Put(rowKeyBytes);
        p.addColumn(INFO.getBytes(), columnNameBytes, valueBytes);
        p.addColumn(INFO.getBytes(), columnName2Bytes, value2Bytes);
        p.addColumn(INFO.getBytes(), columnName3Bytes, value3Bytes);
        p.addColumn(INFO.getBytes(), columnName4Bytes, value4Bytes);
        Configuration hbaseConf = TestHBaseStorageFlowRunCompaction.util.getConfiguration();
        Connection conn = null;
        conn = ConnectionFactory.createConnection(hbaseConf);
        Table flowRunTable = conn.getTable(BaseTableRW.getTableName(hbaseConf, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME));
        flowRunTable.put(p);
        String rowKey2 = "nonNumericRowKey2";
        byte[] rowKey2Bytes = Bytes.toBytes(rowKey2);
        p = new Put(rowKey2Bytes);
        p.addColumn(INFO.getBytes(), columnNameBytes, valueBytes);
        p.addColumn(INFO.getBytes(), columnName2Bytes, value2Bytes);
        p.addColumn(INFO.getBytes(), columnName3Bytes, value3Bytes);
        p.addColumn(INFO.getBytes(), columnName4Bytes, value4Bytes);
        flowRunTable.put(p);
        String rowKey3 = "nonNumericRowKey3";
        byte[] rowKey3Bytes = Bytes.toBytes(rowKey3);
        p = new Put(rowKey3Bytes);
        p.addColumn(INFO.getBytes(), columnNameBytes, valueBytes);
        p.addColumn(INFO.getBytes(), columnName2Bytes, value2Bytes);
        p.addColumn(INFO.getBytes(), columnName3Bytes, value3Bytes);
        p.addColumn(INFO.getBytes(), columnName4Bytes, value4Bytes);
        flowRunTable.put(p);
        Scan s = new Scan();
        s.addFamily(INFO.getBytes());
        s.setStartRow(rowKeyBytes);
        // set number of cells to fetch per scanner next invocation
        int batchLimit = 2;
        s.setBatch(batchLimit);
        ResultScanner scanner = flowRunTable.getScanner(s);
        for (Result result : scanner) {
            Assert.assertNotNull(result);
            Assert.assertTrue((!(result.isEmpty())));
            Assert.assertTrue(((result.rawCells().length) <= batchLimit));
            Map<byte[], byte[]> values = result.getFamilyMap(INFO.getBytes());
            Assert.assertTrue(((values.size()) <= batchLimit));
        }
        s = new Scan();
        s.addFamily(INFO.getBytes());
        s.setStartRow(rowKeyBytes);
        // set number of cells to fetch per scanner next invocation
        batchLimit = 3;
        s.setBatch(batchLimit);
        scanner = flowRunTable.getScanner(s);
        for (Result result : scanner) {
            Assert.assertNotNull(result);
            Assert.assertTrue((!(result.isEmpty())));
            Assert.assertTrue(((result.rawCells().length) <= batchLimit));
            Map<byte[], byte[]> values = result.getFamilyMap(INFO.getBytes());
            Assert.assertTrue(((values.size()) <= batchLimit));
        }
        s = new Scan();
        s.addFamily(INFO.getBytes());
        s.setStartRow(rowKeyBytes);
        // set number of cells to fetch per scanner next invocation
        batchLimit = 1000;
        s.setBatch(batchLimit);
        scanner = flowRunTable.getScanner(s);
        int rowCount = 0;
        for (Result result : scanner) {
            Assert.assertNotNull(result);
            Assert.assertTrue((!(result.isEmpty())));
            Assert.assertTrue(((result.rawCells().length) <= batchLimit));
            Map<byte[], byte[]> values = result.getFamilyMap(INFO.getBytes());
            Assert.assertTrue(((values.size()) <= batchLimit));
            // we expect all back in one next call
            Assert.assertEquals(4, values.size());
            rowCount++;
        }
        // should get back 1 row with each invocation
        // if scan batch is set sufficiently high
        Assert.assertEquals(3, rowCount);
        // test with a negative number
        // should have same effect as setting it to a high number
        s = new Scan();
        s.addFamily(INFO.getBytes());
        s.setStartRow(rowKeyBytes);
        // set number of cells to fetch per scanner next invocation
        batchLimit = -2992;
        s.setBatch(batchLimit);
        scanner = flowRunTable.getScanner(s);
        rowCount = 0;
        for (Result result : scanner) {
            Assert.assertNotNull(result);
            Assert.assertTrue((!(result.isEmpty())));
            Assert.assertEquals(4, result.rawCells().length);
            Map<byte[], byte[]> values = result.getFamilyMap(INFO.getBytes());
            // we expect all back in one next call
            Assert.assertEquals(4, values.size());
            rowCount++;
        }
        // should get back 1 row with each invocation
        // if scan batch is set sufficiently high
        Assert.assertEquals(3, rowCount);
    }

    @Test
    public void testWriteFlowRunCompaction() throws Exception {
        String cluster = "kompaction_cluster1";
        String user = "kompaction_FlowRun__user1";
        String flow = "kompaction_flowRun_flow_name";
        String flowVersion = "AF1021C19F1351";
        long runid = 1449526652000L;
        int start = 10;
        int count = 2000;
        int appIdSuffix = 1;
        HBaseTimelineWriterImpl hbi = null;
        long insertTs = (System.currentTimeMillis()) - count;
        Configuration c1 = TestHBaseStorageFlowRunCompaction.util.getConfiguration();
        TimelineEntities te1 = null;
        TimelineEntity entityApp1 = null;
        UserGroupInformation remoteUser = UserGroupInformation.createRemoteUser(user);
        try {
            hbi = new HBaseTimelineWriterImpl();
            hbi.init(c1);
            // now insert count * ( 100 + 100) metrics
            // each call to getEntityMetricsApp1 brings back 100 values
            // of metric1 and 100 of metric2
            for (int i = start; i < (start + count); i++) {
                String appName = "application_10240000000000_" + appIdSuffix;
                insertTs++;
                te1 = new TimelineEntities();
                entityApp1 = TestFlowDataGenerator.getEntityMetricsApp1(insertTs, c1);
                te1.addEntity(entityApp1);
                hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appName), te1, remoteUser);
                appName = "application_2048000000000_7" + appIdSuffix;
                insertTs++;
                te1 = new TimelineEntities();
                entityApp1 = TestFlowDataGenerator.getEntityMetricsApp2(insertTs);
                te1.addEntity(entityApp1);
                hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appName), te1, remoteUser);
            }
        } finally {
            String appName = "application_10240000000000_" + appIdSuffix;
            te1 = new TimelineEntities();
            entityApp1 = TestFlowDataGenerator.getEntityMetricsApp1Complete((insertTs + 1), c1);
            te1.addEntity(entityApp1);
            if (hbi != null) {
                hbi.write(new TimelineCollectorContext(cluster, user, flow, flowVersion, runid, appName), te1, remoteUser);
                hbi.flush();
                hbi.close();
            }
        }
        // check in flow run table
        TableName flowRunTable = BaseTableRW.getTableName(c1, TABLE_NAME_CONF_NAME, DEFAULT_TABLE_NAME);
        HRegionServer server = TestHBaseStorageFlowRunCompaction.util.getRSForFirstRegionInTable(flowRunTable);
        // flush and compact all the regions of the primary table
        int regionNum = HBaseTimelineServerUtils.flushCompactTableRegions(server, flowRunTable);
        Assert.assertTrue("Didn't find any regions for primary table!", (regionNum > 0));
        // check flow run for one flow many apps
        checkFlowRunTable(cluster, user, flow, runid, c1, 4);
    }

    @Test
    public void checkProcessSummationMoreCellsSumFinal2() throws IOException {
        long cellValue1 = 1236L;
        long cellValue2 = 28L;
        long cellValue3 = 1236L;
        long cellValue4 = 1236L;
        FlowScanner fs = getFlowScannerForTestingCompaction();
        // note down the current timestamp
        long currentTimestamp = System.currentTimeMillis();
        long cell1Ts = 1200120L;
        long cell2Ts = TimestampGenerator.getSupplementedTimestamp(System.currentTimeMillis(), "application_123746661110_11202");
        long cell3Ts = 1277719L;
        long cell4Ts = currentTimestamp - 10;
        SortedSet<Cell> currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        List<Tag> tags = new ArrayList<>();
        Tag t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), "application_1234588888_91188");
        tags.add(t);
        byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        // create a cell with a VERY old timestamp and attribute SUM_FINAL
        Cell c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cell1Ts, Bytes.toBytes(cellValue1), tagByteArray);
        currentColumnCells.add(c1);
        tags = new ArrayList();
        t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), "application_12700000001_29102");
        tags.add(t);
        tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        // create a cell with a recent timestamp and attribute SUM_FINAL
        Cell c2 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cell2Ts, Bytes.toBytes(cellValue2), tagByteArray);
        currentColumnCells.add(c2);
        tags = new ArrayList();
        t = HBaseTimelineServerUtils.createTag(SUM.getTagType(), "application_191780000000001_8195");
        tags.add(t);
        tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        // create a cell with a VERY old timestamp but has attribute SUM
        Cell c3 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cell3Ts, Bytes.toBytes(cellValue3), tagByteArray);
        currentColumnCells.add(c3);
        tags = new ArrayList();
        t = HBaseTimelineServerUtils.createTag(SUM.getTagType(), "application_191780000000001_98104");
        tags.add(t);
        tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        // create a cell with a VERY old timestamp but has attribute SUM
        Cell c4 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cell4Ts, Bytes.toBytes(cellValue4), tagByteArray);
        currentColumnCells.add(c4);
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, new LongConverter(), currentTimestamp);
        Assert.assertNotNull(cells);
        // we should be getting back 4 cells
        // one is the flow sum cell
        // two are the cells with SUM attribute
        // one cell with SUM_FINAL
        Assert.assertEquals(4, cells.size());
        for (int i = 0; i < (cells.size()); i++) {
            Cell returnedCell = cells.get(0);
            Assert.assertNotNull(returnedCell);
            long returnTs = returnedCell.getTimestamp();
            long returnValue = Bytes.toLong(CellUtil.cloneValue(returnedCell));
            if (returnValue == cellValue2) {
                Assert.assertTrue((returnTs == cell2Ts));
            } else
                if (returnValue == cellValue3) {
                    Assert.assertTrue((returnTs == cell3Ts));
                } else
                    if (returnValue == cellValue4) {
                        Assert.assertTrue((returnTs == cell4Ts));
                    } else
                        if (returnValue == cellValue1) {
                            Assert.assertTrue((returnTs != cell1Ts));
                            Assert.assertTrue((returnTs > cell1Ts));
                            Assert.assertTrue((returnTs >= currentTimestamp));
                        } else {
                            // raise a failure since we expect only these two values back
                            Assert.fail();
                        }



        }
    }

    // tests with many cells
    // of type SUM and SUM_FINAL
    // all cells of SUM_FINAL will expire
    @Test
    public void checkProcessSummationMoreCellsSumFinalMany() throws IOException {
        FlowScanner fs = getFlowScannerForTestingCompaction();
        int count = 200000;
        long cellValueFinal = 1000L;
        long cellValueNotFinal = 28L;
        // note down the current timestamp
        long currentTimestamp = System.currentTimeMillis();
        long cellTsFinalStart = 10001120L;
        long cellTsFinal = cellTsFinalStart;
        long cellTsNotFinalStart = currentTimestamp - 5;
        long cellTsNotFinal = cellTsNotFinalStart;
        SortedSet<Cell> currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        List<Tag> tags = null;
        Tag t = null;
        Cell c1 = null;
        // insert SUM_FINAL cells
        for (int i = 0; i < count; i++) {
            tags = new ArrayList();
            t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), ((("application_123450000" + i) + "01_19") + i));
            tags.add(t);
            byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
            // create a cell with a VERY old timestamp and attribute SUM_FINAL
            c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cellTsFinal, Bytes.toBytes(cellValueFinal), tagByteArray);
            currentColumnCells.add(c1);
            cellTsFinal++;
        }
        // add SUM cells
        for (int i = 0; i < count; i++) {
            tags = new ArrayList();
            t = HBaseTimelineServerUtils.createTag(SUM.getTagType(), ((("application_1987650000" + i) + "83_911") + i));
            tags.add(t);
            byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
            // create a cell with attribute SUM
            c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cellTsNotFinal, Bytes.toBytes(cellValueNotFinal), tagByteArray);
            currentColumnCells.add(c1);
            cellTsNotFinal++;
        }
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, new LongConverter(), currentTimestamp);
        Assert.assertNotNull(cells);
        // we should be getting back count + 1 cells
        // one is the flow sum cell
        // others are the cells with SUM attribute
        Assert.assertEquals((count + 1), cells.size());
        for (int i = 0; i < (cells.size()); i++) {
            Cell returnedCell = cells.get(0);
            Assert.assertNotNull(returnedCell);
            long returnTs = returnedCell.getTimestamp();
            long returnValue = Bytes.toLong(CellUtil.cloneValue(returnedCell));
            if (returnValue == (count * cellValueFinal)) {
                Assert.assertTrue((returnTs > (cellTsFinalStart + count)));
                Assert.assertTrue((returnTs >= currentTimestamp));
            } else
                if ((returnValue >= cellValueNotFinal) && (returnValue <= (cellValueNotFinal * count))) {
                    Assert.assertTrue((returnTs >= cellTsNotFinalStart));
                    Assert.assertTrue((returnTs <= (cellTsNotFinalStart * count)));
                } else {
                    // raise a failure since we expect only these values back
                    Assert.fail();
                }

        }
    }

    // tests with many cells
    // of type SUM and SUM_FINAL
    // NOT cells of SUM_FINAL will expire
    @Test
    public void checkProcessSummationMoreCellsSumFinalVariedTags() throws IOException {
        FlowScanner fs = getFlowScannerForTestingCompaction();
        int countFinal = 20100;
        int countNotFinal = 1000;
        int countFinalNotExpire = 7009;
        long cellValueFinal = 1000L;
        long cellValueNotFinal = 28L;
        // note down the current timestamp
        long currentTimestamp = System.currentTimeMillis();
        long cellTsFinalStart = 10001120L;
        long cellTsFinal = cellTsFinalStart;
        long cellTsFinalStartNotExpire = TimestampGenerator.getSupplementedTimestamp(System.currentTimeMillis(), "application_10266666661166_118821");
        long cellTsFinalNotExpire = cellTsFinalStartNotExpire;
        long cellTsNotFinalStart = currentTimestamp - 5;
        long cellTsNotFinal = cellTsNotFinalStart;
        SortedSet<Cell> currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        List<Tag> tags = null;
        Tag t = null;
        Cell c1 = null;
        // insert SUM_FINAL cells which will expire
        for (int i = 0; i < countFinal; i++) {
            tags = new ArrayList();
            t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), ((("application_123450000" + i) + "01_19") + i));
            tags.add(t);
            byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
            // create a cell with a VERY old timestamp and attribute SUM_FINAL
            c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cellTsFinal, Bytes.toBytes(cellValueFinal), tagByteArray);
            currentColumnCells.add(c1);
            cellTsFinal++;
        }
        // insert SUM_FINAL cells which will NOT expire
        for (int i = 0; i < countFinalNotExpire; i++) {
            tags = new ArrayList();
            t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), ((("application_123450000" + i) + "01_19") + i));
            tags.add(t);
            byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
            // create a cell with a VERY old timestamp and attribute SUM_FINAL
            c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cellTsFinalNotExpire, Bytes.toBytes(cellValueFinal), tagByteArray);
            currentColumnCells.add(c1);
            cellTsFinalNotExpire++;
        }
        // add SUM cells
        for (int i = 0; i < countNotFinal; i++) {
            tags = new ArrayList();
            t = HBaseTimelineServerUtils.createTag(SUM.getTagType(), ((("application_1987650000" + i) + "83_911") + i));
            tags.add(t);
            byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
            // create a cell with attribute SUM
            c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, cellTsNotFinal, Bytes.toBytes(cellValueNotFinal), tagByteArray);
            currentColumnCells.add(c1);
            cellTsNotFinal++;
        }
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, new LongConverter(), currentTimestamp);
        Assert.assertNotNull(cells);
        // we should be getting back
        // countNotFinal + countFinalNotExpire + 1 cells
        // one is the flow sum cell
        // count = the cells with SUM attribute
        // count = the cells with SUM_FINAL attribute but not expired
        Assert.assertEquals(((countFinalNotExpire + countNotFinal) + 1), cells.size());
        for (int i = 0; i < (cells.size()); i++) {
            Cell returnedCell = cells.get(0);
            Assert.assertNotNull(returnedCell);
            long returnTs = returnedCell.getTimestamp();
            long returnValue = Bytes.toLong(CellUtil.cloneValue(returnedCell));
            if (returnValue == (countFinal * cellValueFinal)) {
                Assert.assertTrue((returnTs > (cellTsFinalStart + countFinal)));
                Assert.assertTrue((returnTs >= currentTimestamp));
            } else
                if (returnValue == cellValueNotFinal) {
                    Assert.assertTrue((returnTs >= cellTsNotFinalStart));
                    Assert.assertTrue((returnTs <= (cellTsNotFinalStart + countNotFinal)));
                } else
                    if (returnValue == cellValueFinal) {
                        Assert.assertTrue((returnTs >= cellTsFinalStartNotExpire));
                        Assert.assertTrue((returnTs <= (cellTsFinalStartNotExpire + countFinalNotExpire)));
                    } else {
                        // raise a failure since we expect only these values back
                        Assert.fail();
                    }


        }
    }

    @Test
    public void testProcessSummationMoreCellsSumFinal() throws IOException {
        FlowScanner fs = getFlowScannerForTestingCompaction();
        // note down the current timestamp
        long currentTimestamp = System.currentTimeMillis();
        long cellValue1 = 1236L;
        long cellValue2 = 28L;
        List<Tag> tags = new ArrayList<>();
        Tag t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), "application_1234588888_999888");
        tags.add(t);
        byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        SortedSet<Cell> currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        // create a cell with a VERY old timestamp and attribute SUM_FINAL
        Cell c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, 120L, Bytes.toBytes(cellValue1), tagByteArray);
        currentColumnCells.add(c1);
        tags = new ArrayList();
        t = HBaseTimelineServerUtils.createTag(SUM.getTagType(), "application_100000000001_119101");
        tags.add(t);
        tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        // create a cell with a VERY old timestamp but has attribute SUM
        Cell c2 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, 130L, Bytes.toBytes(cellValue2), tagByteArray);
        currentColumnCells.add(c2);
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, new LongConverter(), currentTimestamp);
        Assert.assertNotNull(cells);
        // we should be getting back two cells
        // one is the flow sum cell
        // another is the cell with SUM attribute
        Assert.assertEquals(2, cells.size());
        Cell returnedCell = cells.get(0);
        Assert.assertNotNull(returnedCell);
        long inputTs1 = c1.getTimestamp();
        long inputTs2 = c2.getTimestamp();
        long returnTs = returnedCell.getTimestamp();
        long returnValue = Bytes.toLong(CellUtil.cloneValue(returnedCell));
        // the returned Ts will be far greater than input ts as well as the noted
        // current timestamp
        if (returnValue == cellValue2) {
            Assert.assertTrue((returnTs == inputTs2));
        } else
            if (returnValue == cellValue1) {
                Assert.assertTrue((returnTs >= currentTimestamp));
                Assert.assertTrue((returnTs != inputTs1));
            } else {
                // raise a failure since we expect only these two values back
                Assert.fail();
            }

    }

    @Test
    public void testProcessSummationOneCellSumFinal() throws IOException {
        FlowScanner fs = getFlowScannerForTestingCompaction();
        // note down the current timestamp
        long currentTimestamp = System.currentTimeMillis();
        List<Tag> tags = new ArrayList<>();
        Tag t = HBaseTimelineServerUtils.createTag(SUM_FINAL.getTagType(), "application_123458888888_999888");
        tags.add(t);
        byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        SortedSet<Cell> currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        // create a cell with a VERY old timestamp
        Cell c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, 120L, Bytes.toBytes(1110L), tagByteArray);
        currentColumnCells.add(c1);
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, new LongConverter(), currentTimestamp);
        Assert.assertNotNull(cells);
        // we should not get the same cell back
        // but we get back the flow cell
        Assert.assertEquals(1, cells.size());
        Cell returnedCell = cells.get(0);
        // it's NOT the same cell
        Assert.assertNotEquals(c1, returnedCell);
        long inputTs = c1.getTimestamp();
        long returnTs = returnedCell.getTimestamp();
        // the returned Ts will be far greater than input ts as well as the noted
        // current timestamp
        Assert.assertTrue((returnTs > inputTs));
        Assert.assertTrue((returnTs >= currentTimestamp));
    }

    @Test
    public void testProcessSummationOneCell() throws IOException {
        FlowScanner fs = getFlowScannerForTestingCompaction();
        // note down the current timestamp
        long currentTimestamp = System.currentTimeMillis();
        // try for 1 cell with tag SUM
        List<Tag> tags = new ArrayList<>();
        Tag t = HBaseTimelineServerUtils.createTag(SUM.getTagType(), "application_123458888888_999888");
        tags.add(t);
        byte[] tagByteArray = HBaseTimelineServerUtils.convertTagListToByteArray(tags);
        SortedSet<Cell> currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        Cell c1 = HBaseTimelineServerUtils.createNewCell(aRowKey, aFamily, aQualifier, currentTimestamp, Bytes.toBytes(1110L), tagByteArray);
        currentColumnCells.add(c1);
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, new LongConverter(), currentTimestamp);
        Assert.assertNotNull(cells);
        // we expect the same cell back
        Assert.assertEquals(1, cells.size());
        Cell c2 = cells.get(0);
        Assert.assertEquals(c1, c2);
        Assert.assertEquals(currentTimestamp, c2.getTimestamp());
    }

    @Test
    public void testProcessSummationEmpty() throws IOException {
        FlowScanner fs = getFlowScannerForTestingCompaction();
        long currentTimestamp = System.currentTimeMillis();
        LongConverter longConverter = new LongConverter();
        SortedSet<Cell> currentColumnCells = null;
        List<Cell> cells = fs.processSummationMajorCompaction(currentColumnCells, longConverter, currentTimestamp);
        Assert.assertNotNull(cells);
        Assert.assertEquals(0, cells.size());
        currentColumnCells = new TreeSet<Cell>(KeyValue.COMPARATOR);
        cells = fs.processSummationMajorCompaction(currentColumnCells, longConverter, currentTimestamp);
        Assert.assertNotNull(cells);
        Assert.assertEquals(0, cells.size());
    }
}

