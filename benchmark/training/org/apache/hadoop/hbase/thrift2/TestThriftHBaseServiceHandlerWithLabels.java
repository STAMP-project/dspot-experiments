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
package org.apache.hadoop.hbase.thrift2;


import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.security.User;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.thrift2.generated.TAppend;
import org.apache.hadoop.hbase.thrift2.generated.TAuthorization;
import org.apache.hadoop.hbase.thrift2.generated.TCellVisibility;
import org.apache.hadoop.hbase.thrift2.generated.TColumn;
import org.apache.hadoop.hbase.thrift2.generated.TColumnIncrement;
import org.apache.hadoop.hbase.thrift2.generated.TColumnValue;
import org.apache.hadoop.hbase.thrift2.generated.TGet;
import org.apache.hadoop.hbase.thrift2.generated.TIllegalArgument;
import org.apache.hadoop.hbase.thrift2.generated.TIncrement;
import org.apache.hadoop.hbase.thrift2.generated.TPut;
import org.apache.hadoop.hbase.thrift2.generated.TResult;
import org.apache.hadoop.hbase.thrift2.generated.TScan;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ ClientTests.class, MediumTests.class })
public class TestThriftHBaseServiceHandlerWithLabels {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftHBaseServiceHandlerWithLabels.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestThriftHBaseServiceHandlerWithLabels.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    // Static names for tables, columns, rows, and values
    private static byte[] tableAname = Bytes.toBytes("tableA");

    private static byte[] familyAname = Bytes.toBytes("familyA");

    private static byte[] familyBname = Bytes.toBytes("familyB");

    private static byte[] qualifierAname = Bytes.toBytes("qualifierA");

    private static byte[] qualifierBname = Bytes.toBytes("qualifierB");

    private static byte[] valueAname = Bytes.toBytes("valueA");

    private static byte[] valueBname = Bytes.toBytes("valueB");

    private static HColumnDescriptor[] families = new HColumnDescriptor[]{ new HColumnDescriptor(TestThriftHBaseServiceHandlerWithLabels.familyAname).setMaxVersions(3), new HColumnDescriptor(TestThriftHBaseServiceHandlerWithLabels.familyBname).setMaxVersions(2) };

    private static final String TOPSECRET = "topsecret";

    private static final String PUBLIC = "public";

    private static final String PRIVATE = "private";

    private static final String CONFIDENTIAL = "confidential";

    private static final String SECRET = "secret";

    private static User SUPERUSER;

    private static Configuration conf;

    @Test
    public void testScanWithVisibilityLabels() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < 10; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testScan" + i))), columnValues);
            if (i == 5) {
                put.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.PUBLIC));
            } else {
                put.setCellVisibility(new TCellVisibility().setExpression(((((((("(" + (TestThriftHBaseServiceHandlerWithLabels.SECRET)) + "|") + (TestThriftHBaseServiceHandlerWithLabels.CONFIDENTIAL)) + ")") + "&") + "!") + (TestThriftHBaseServiceHandlerWithLabels.TOPSECRET))));
            }
            handler.put(table, put);
        }
        // create scan instance
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandlerWithLabels.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandlerWithLabels.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testScan"));
        scan.setStopRow(Bytes.toBytes("testScan\uffff"));
        TAuthorization tauth = new TAuthorization();
        List<String> labels = new ArrayList<>(2);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.SECRET);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.PRIVATE);
        tauth.setLabels(labels);
        scan.setAuthorizations(tauth);
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(9, results.size());
        Assert.assertFalse(Bytes.equals(results.get(5).getRow(), Bytes.toBytes(("testScan" + 5))));
        for (int i = 0; i < 9; i++) {
            if (i < 5) {
                Assert.assertArrayEquals(Bytes.toBytes(("testScan" + i)), results.get(i).getRow());
            } else
                if (i == 5) {
                    continue;
                } else {
                    Assert.assertArrayEquals(Bytes.toBytes(("testScan" + (i + 1))), results.get(i).getRow());
                }

        }
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 9);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 9);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    @Test
    public void testGetScannerResultsWithAuthorizations() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < 20; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testGetScannerResults" + (pad(i, ((byte) (2))))))), columnValues);
            if (i == 3) {
                put.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.PUBLIC));
            } else {
                put.setCellVisibility(new TCellVisibility().setExpression(((((((("(" + (TestThriftHBaseServiceHandlerWithLabels.SECRET)) + "|") + (TestThriftHBaseServiceHandlerWithLabels.CONFIDENTIAL)) + ")") + "&") + "!") + (TestThriftHBaseServiceHandlerWithLabels.TOPSECRET))));
            }
            handler.put(table, put);
        }
        // create scan instance
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandlerWithLabels.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandlerWithLabels.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testGetScannerResults"));
        // get 5 rows and check the returned results
        scan.setStopRow(Bytes.toBytes("testGetScannerResults05"));
        TAuthorization tauth = new TAuthorization();
        List<String> labels = new ArrayList<>(2);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.SECRET);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.PRIVATE);
        tauth.setLabels(labels);
        scan.setAuthorizations(tauth);
        List<TResult> results = handler.getScannerResults(table, scan, 5);
        Assert.assertEquals(4, results.size());
        for (int i = 0; i < 4; i++) {
            if (i < 3) {
                Assert.assertArrayEquals(Bytes.toBytes(("testGetScannerResults" + (pad(i, ((byte) (2)))))), results.get(i).getRow());
            } else
                if (i == 3) {
                    continue;
                } else {
                    Assert.assertArrayEquals(Bytes.toBytes(("testGetScannerResults" + (pad((i + 1), ((byte) (2)))))), results.get(i).getRow());
                }

        }
    }

    @Test
    public void testGetsWithLabels() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testPutGet");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.valueBname)));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        put.setCellVisibility(new TCellVisibility().setExpression(((((((("(" + (TestThriftHBaseServiceHandlerWithLabels.SECRET)) + "|") + (TestThriftHBaseServiceHandlerWithLabels.CONFIDENTIAL)) + ")") + "&") + "!") + (TestThriftHBaseServiceHandlerWithLabels.TOPSECRET))));
        handler.put(table, put);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TAuthorization tauth = new TAuthorization();
        List<String> labels = new ArrayList<>(2);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.SECRET);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.PRIVATE);
        tauth.setLabels(labels);
        get.setAuthorizations(tauth);
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        List<TColumnValue> returnedColumnValues = result.getColumnValues();
        assertTColumnValuesEqual(columnValues, returnedColumnValues);
    }

    @Test
    public void testIncrementWithTags() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testIncrementWithTags");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(Bytes.toBytes(1L))));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        put.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.PRIVATE));
        handler.put(table, put);
        List<TColumnIncrement> incrementColumns = new ArrayList<>(1);
        incrementColumns.add(new TColumnIncrement(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname)));
        TIncrement increment = new TIncrement(ByteBuffer.wrap(rowName), incrementColumns);
        increment.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.SECRET));
        handler.increment(table, increment);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TAuthorization tauth = new TAuthorization();
        List<String> labels = new ArrayList<>(1);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.SECRET);
        tauth.setLabels(labels);
        get.setAuthorizations(tauth);
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        Assert.assertEquals(1, result.getColumnValuesSize());
        TColumnValue columnValue = result.getColumnValues().get(0);
        Assert.assertArrayEquals(Bytes.toBytes(2L), columnValue.getValue());
    }

    @Test
    public void testIncrementWithTagsWithNotMatchLabels() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testIncrementWithTagsWithNotMatchLabels");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(Bytes.toBytes(1L))));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        put.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.PRIVATE));
        handler.put(table, put);
        List<TColumnIncrement> incrementColumns = new ArrayList<>(1);
        incrementColumns.add(new TColumnIncrement(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname)));
        TIncrement increment = new TIncrement(ByteBuffer.wrap(rowName), incrementColumns);
        increment.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.SECRET));
        handler.increment(table, increment);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TAuthorization tauth = new TAuthorization();
        List<String> labels = new ArrayList<>(1);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.PUBLIC);
        tauth.setLabels(labels);
        get.setAuthorizations(tauth);
        TResult result = handler.get(table, get);
        Assert.assertNull(result.getRow());
    }

    @Test
    public void testAppend() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testAppend");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.tableAname);
        byte[] v1 = Bytes.toBytes(1L);
        byte[] v2 = Bytes.toBytes(5L);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(Bytes.toBytes(1L))));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        put.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.PRIVATE));
        handler.put(table, put);
        List<TColumnValue> appendColumns = new ArrayList<>(1);
        appendColumns.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithLabels.qualifierAname), ByteBuffer.wrap(v2)));
        TAppend append = new TAppend(ByteBuffer.wrap(rowName), appendColumns);
        append.setCellVisibility(new TCellVisibility().setExpression(TestThriftHBaseServiceHandlerWithLabels.SECRET));
        handler.append(table, append);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TAuthorization tauth = new TAuthorization();
        List<String> labels = new ArrayList<>(1);
        labels.add(TestThriftHBaseServiceHandlerWithLabels.SECRET);
        tauth.setLabels(labels);
        get.setAuthorizations(tauth);
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        Assert.assertEquals(1, result.getColumnValuesSize());
        TColumnValue columnValue = result.getColumnValues().get(0);
        Assert.assertArrayEquals(Bytes.add(v1, v2), columnValue.getValue());
    }
}

