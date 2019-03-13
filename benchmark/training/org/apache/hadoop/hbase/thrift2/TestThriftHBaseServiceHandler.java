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


import TCompareOp.EQUAL;
import TConsistency.STRONG;
import TConsistency.TIMELINE;
import TDataBlockEncoding.DIFF;
import TDataBlockEncoding.PREFIX;
import TDeleteType.DELETE_COLUMN;
import TDeleteType.DELETE_COLUMNS;
import TDeleteType.DELETE_FAMILY;
import TDeleteType.DELETE_FAMILY_VERSION;
import TDurability.ASYNC_WAL;
import TDurability.FSYNC_WAL;
import TDurability.SKIP_WAL;
import TDurability.SYNC_WAL;
import THBaseService.Iface;
import TReadType.PREAD;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompatibilityFactory;
import org.apache.hadoop.hbase.CoprocessorEnvironment;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Increment;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.filter.ParseFilter;
import org.apache.hadoop.hbase.security.UserProvider;
import org.apache.hadoop.hbase.test.MetricsAssertHelper;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.thrift.ErrorThrowingGetObserver;
import org.apache.hadoop.hbase.thrift.HBaseServiceHandler;
import org.apache.hadoop.hbase.thrift.HbaseHandlerMetricsProxy;
import org.apache.hadoop.hbase.thrift.ThriftMetrics;
import org.apache.hadoop.hbase.thrift2.generated.TAppend;
import org.apache.hadoop.hbase.thrift2.generated.TColumn;
import org.apache.hadoop.hbase.thrift2.generated.TColumnFamilyDescriptor;
import org.apache.hadoop.hbase.thrift2.generated.TColumnIncrement;
import org.apache.hadoop.hbase.thrift2.generated.TColumnValue;
import org.apache.hadoop.hbase.thrift2.generated.TDelete;
import org.apache.hadoop.hbase.thrift2.generated.TDurability;
import org.apache.hadoop.hbase.thrift2.generated.TGet;
import org.apache.hadoop.hbase.thrift2.generated.THBaseService;
import org.apache.hadoop.hbase.thrift2.generated.TIOError;
import org.apache.hadoop.hbase.thrift2.generated.TIllegalArgument;
import org.apache.hadoop.hbase.thrift2.generated.TIncrement;
import org.apache.hadoop.hbase.thrift2.generated.TMutation;
import org.apache.hadoop.hbase.thrift2.generated.TNamespaceDescriptor;
import org.apache.hadoop.hbase.thrift2.generated.TPut;
import org.apache.hadoop.hbase.thrift2.generated.TResult;
import org.apache.hadoop.hbase.thrift2.generated.TRowMutations;
import org.apache.hadoop.hbase.thrift2.generated.TScan;
import org.apache.hadoop.hbase.thrift2.generated.TTableDescriptor;
import org.apache.hadoop.hbase.thrift2.generated.TTableName;
import org.apache.hadoop.hbase.thrift2.generated.TTimeRange;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.org.apache.commons.collections4.CollectionUtils;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.thrift.ErrorThrowingGetObserver.ErrorType.values;


/**
 * Unit testing for ThriftServer.HBaseServiceHandler, a part of the org.apache.hadoop.hbase.thrift2
 * package.
 */
@Category({ ClientTests.class, MediumTests.class })
public class TestThriftHBaseServiceHandler {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftHBaseServiceHandler.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestThriftHBaseServiceHandler.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    // Static names for tables, columns, rows, and values
    private static byte[] tableAname = Bytes.toBytes("tableA");

    private static byte[] familyAname = Bytes.toBytes("familyA");

    private static byte[] familyBname = Bytes.toBytes("familyB");

    private static byte[] qualifierAname = Bytes.toBytes("qualifierA");

    private static byte[] qualifierBname = Bytes.toBytes("qualifierB");

    private static byte[] valueAname = Bytes.toBytes("valueA");

    private static byte[] valueBname = Bytes.toBytes("valueB");

    private static HColumnDescriptor[] families = new HColumnDescriptor[]{ new HColumnDescriptor(TestThriftHBaseServiceHandler.familyAname).setMaxVersions(3), new HColumnDescriptor(TestThriftHBaseServiceHandler.familyBname).setMaxVersions(2) };

    private static final MetricsAssertHelper metricsHelper = CompatibilityFactory.getInstance(MetricsAssertHelper.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void testExists() throws TIOError, TException {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testExists");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        Assert.assertFalse(handler.exists(table, get));
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname)));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        Assert.assertTrue(handler.exists(table, get));
    }

    @Test
    public void testExistsAll() throws TIOError, TException {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName1 = Bytes.toBytes("testExistsAll1");
        byte[] rowName2 = Bytes.toBytes("testExistsAll2");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TGet> gets = new ArrayList<>();
        gets.add(new TGet(ByteBuffer.wrap(rowName2)));
        gets.add(new TGet(ByteBuffer.wrap(rowName2)));
        List<Boolean> existsResult1 = handler.existsAll(table, gets);
        Assert.assertFalse(existsResult1.get(0));
        Assert.assertFalse(existsResult1.get(1));
        List<TColumnValue> columnValues = new ArrayList<TColumnValue>();
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname)));
        List<TPut> puts = new ArrayList<TPut>();
        puts.add(new TPut(ByteBuffer.wrap(rowName1), columnValues));
        puts.add(new TPut(ByteBuffer.wrap(rowName2), columnValues));
        handler.putMultiple(table, puts);
        List<Boolean> existsResult2 = handler.existsAll(table, gets);
        Assert.assertTrue(existsResult2.get(0));
        Assert.assertTrue(existsResult2.get(1));
    }

    @Test
    public void testPutGet() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testPutGet");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname)));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        List<TColumnValue> returnedColumnValues = result.getColumnValues();
        assertTColumnValuesEqual(columnValues, returnedColumnValues);
    }

    @Test
    public void testPutGetMultiple() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        byte[] rowName1 = Bytes.toBytes("testPutGetMultiple1");
        byte[] rowName2 = Bytes.toBytes("testPutGetMultiple2");
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname)));
        List<TPut> puts = new ArrayList<>(2);
        puts.add(new TPut(ByteBuffer.wrap(rowName1), columnValues));
        puts.add(new TPut(ByteBuffer.wrap(rowName2), columnValues));
        handler.putMultiple(table, puts);
        List<TGet> gets = new ArrayList<>(2);
        gets.add(new TGet(ByteBuffer.wrap(rowName1)));
        gets.add(new TGet(ByteBuffer.wrap(rowName2)));
        List<TResult> results = handler.getMultiple(table, gets);
        Assert.assertEquals(2, results.size());
        Assert.assertArrayEquals(rowName1, results.get(0).getRow());
        assertTColumnValuesEqual(columnValues, results.get(0).getColumnValues());
        Assert.assertArrayEquals(rowName2, results.get(1).getRow());
        assertTColumnValuesEqual(columnValues, results.get(1).getColumnValues());
    }

    @Test
    public void testDeleteMultiple() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        byte[] rowName1 = Bytes.toBytes("testDeleteMultiple1");
        byte[] rowName2 = Bytes.toBytes("testDeleteMultiple2");
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname)));
        List<TPut> puts = new ArrayList<>(2);
        puts.add(new TPut(ByteBuffer.wrap(rowName1), columnValues));
        puts.add(new TPut(ByteBuffer.wrap(rowName2), columnValues));
        handler.putMultiple(table, puts);
        List<TDelete> deletes = new ArrayList<>(2);
        deletes.add(new TDelete(ByteBuffer.wrap(rowName1)));
        deletes.add(new TDelete(ByteBuffer.wrap(rowName2)));
        List<TDelete> deleteResults = handler.deleteMultiple(table, deletes);
        // 0 means they were all successfully applies
        Assert.assertEquals(0, deleteResults.size());
        Assert.assertFalse(handler.exists(table, new TGet(ByteBuffer.wrap(rowName1))));
        Assert.assertFalse(handler.exists(table, new TGet(ByteBuffer.wrap(rowName2))));
    }

    @Test
    public void testDelete() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testDelete");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(2);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        TColumnValue columnValueB = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname));
        columnValues.add(columnValueA);
        columnValues.add(columnValueB);
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TColumn> deleteColumns = new ArrayList<>(1);
        TColumn deleteColumn = new TColumn(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname));
        deleteColumn.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        deleteColumns.add(deleteColumn);
        delete.setColumns(deleteColumns);
        handler.deleteSingle(table, delete);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        List<TColumnValue> returnedColumnValues = result.getColumnValues();
        List<TColumnValue> expectedColumnValues = new ArrayList<>(1);
        expectedColumnValues.add(columnValueB);
        assertTColumnValuesEqual(expectedColumnValues, returnedColumnValues);
    }

    @Test
    public void testDeleteAllTimestamps() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testDeleteAllTimestamps");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValueA.setTimestamp(((System.currentTimeMillis()) - 10));
        columnValues.add(columnValueA);
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        columnValueA.setTimestamp(System.currentTimeMillis());
        handler.put(table, put);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        get.setMaxVersions(2);
        TResult result = handler.get(table, get);
        Assert.assertEquals(2, result.getColumnValuesSize());
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TColumn> deleteColumns = new ArrayList<>(1);
        TColumn deleteColumn = new TColumn(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname));
        deleteColumn.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        deleteColumns.add(deleteColumn);
        delete.setColumns(deleteColumns);
        delete.setDeleteType(DELETE_COLUMNS);// This is the default anyway.

        handler.deleteSingle(table, delete);
        get = new TGet(ByteBuffer.wrap(rowName));
        result = handler.get(table, get);
        Assert.assertNull(result.getRow());
        Assert.assertEquals(0, result.getColumnValuesSize());
    }

    @Test
    public void testDeleteSingleTimestamp() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testDeleteSingleTimestamp");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        long timestamp1 = (System.currentTimeMillis()) - 10;
        long timestamp2 = System.currentTimeMillis();
        List<TColumnValue> columnValues = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValueA.setTimestamp(timestamp1);
        columnValues.add(columnValueA);
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        columnValueA.setTimestamp(timestamp2);
        handler.put(table, put);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        get.setMaxVersions(2);
        TResult result = handler.get(table, get);
        Assert.assertEquals(2, result.getColumnValuesSize());
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TColumn> deleteColumns = new ArrayList<>(1);
        TColumn deleteColumn = new TColumn(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname));
        deleteColumn.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        deleteColumns.add(deleteColumn);
        delete.setColumns(deleteColumns);
        delete.setDeleteType(DELETE_COLUMN);
        handler.deleteSingle(table, delete);
        get = new TGet(ByteBuffer.wrap(rowName));
        result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        Assert.assertEquals(1, result.getColumnValuesSize());
        // the older timestamp should remain.
        Assert.assertEquals(timestamp1, result.getColumnValues().get(0).getTimestamp());
    }

    @Test
    public void testDeleteFamily() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testDeleteFamily");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        long timestamp1 = (System.currentTimeMillis()) - 10;
        long timestamp2 = System.currentTimeMillis();
        List<TColumnValue> columnValues = new ArrayList<>();
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValueA.setTimestamp(timestamp1);
        columnValues.add(columnValueA);
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        columnValueA.setTimestamp(timestamp2);
        handler.put(table, put);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        get.setMaxVersions(2);
        TResult result = handler.get(table, get);
        Assert.assertEquals(2, result.getColumnValuesSize());
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TColumn> deleteColumns = new ArrayList<>();
        TColumn deleteColumn = new TColumn(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname));
        deleteColumns.add(deleteColumn);
        delete.setColumns(deleteColumns);
        delete.setDeleteType(DELETE_FAMILY);
        handler.deleteSingle(table, delete);
        get = new TGet(ByteBuffer.wrap(rowName));
        result = handler.get(table, get);
        Assert.assertArrayEquals(null, result.getRow());
        Assert.assertEquals(0, result.getColumnValuesSize());
    }

    @Test
    public void testDeleteFamilyVersion() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testDeleteFamilyVersion");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        long timestamp1 = (System.currentTimeMillis()) - 10;
        long timestamp2 = System.currentTimeMillis();
        List<TColumnValue> columnValues = new ArrayList<>();
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValueA.setTimestamp(timestamp1);
        columnValues.add(columnValueA);
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        columnValueA.setTimestamp(timestamp2);
        handler.put(table, put);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        get.setMaxVersions(2);
        TResult result = handler.get(table, get);
        Assert.assertEquals(2, result.getColumnValuesSize());
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TColumn> deleteColumns = new ArrayList<>();
        TColumn deleteColumn = new TColumn(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname));
        deleteColumn.setTimestamp(timestamp1);
        deleteColumns.add(deleteColumn);
        delete.setColumns(deleteColumns);
        delete.setDeleteType(DELETE_FAMILY_VERSION);
        handler.deleteSingle(table, delete);
        get = new TGet(ByteBuffer.wrap(rowName));
        result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        Assert.assertEquals(1, result.getColumnValuesSize());
        Assert.assertEquals(timestamp2, result.getColumnValues().get(0).getTimestamp());
    }

    @Test
    public void testIncrement() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testIncrement");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(Bytes.toBytes(1L))));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        List<TColumnIncrement> incrementColumns = new ArrayList<>(1);
        incrementColumns.add(new TColumnIncrement(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname)));
        TIncrement increment = new TIncrement(ByteBuffer.wrap(rowName), incrementColumns);
        handler.increment(table, increment);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        Assert.assertEquals(1, result.getColumnValuesSize());
        TColumnValue columnValue = result.getColumnValues().get(0);
        Assert.assertArrayEquals(Bytes.toBytes(2L), columnValue.getValue());
    }

    @Test
    public void testAppend() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testAppend");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        byte[] v1 = Bytes.toBytes("42");
        byte[] v2 = Bytes.toBytes("23");
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(v1)));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        List<TColumnValue> appendColumns = new ArrayList<>(1);
        appendColumns.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(v2)));
        TAppend append = new TAppend(ByteBuffer.wrap(rowName), appendColumns);
        handler.append(table, append);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        Assert.assertEquals(1, result.getColumnValuesSize());
        TColumnValue columnValue = result.getColumnValues().get(0);
        Assert.assertArrayEquals(Bytes.add(v1, v2), columnValue.getValue());
    }

    /**
     * check that checkAndPut fails if the cell does not exist, then put in the cell, then check
     * that the checkAndPut succeeds.
     */
    @Test
    public void testCheckAndPut() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testCheckAndPut");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValuesA = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValuesA.add(columnValueA);
        TPut putA = new TPut(ByteBuffer.wrap(rowName), columnValuesA);
        putA.setColumnValues(columnValuesA);
        List<TColumnValue> columnValuesB = new ArrayList<>(1);
        TColumnValue columnValueB = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname));
        columnValuesB.add(columnValueB);
        TPut putB = new TPut(ByteBuffer.wrap(rowName), columnValuesB);
        putB.setColumnValues(columnValuesB);
        Assert.assertFalse(handler.checkAndPut(table, ByteBuffer.wrap(rowName), ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname), putB));
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertEquals(0, result.getColumnValuesSize());
        handler.put(table, putA);
        Assert.assertTrue(handler.checkAndPut(table, ByteBuffer.wrap(rowName), ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname), putB));
        result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        List<TColumnValue> returnedColumnValues = result.getColumnValues();
        List<TColumnValue> expectedColumnValues = new ArrayList<>(2);
        expectedColumnValues.add(columnValueA);
        expectedColumnValues.add(columnValueB);
        assertTColumnValuesEqual(expectedColumnValues, returnedColumnValues);
    }

    /**
     * check that checkAndDelete fails if the cell does not exist, then put in the cell, then
     * check that the checkAndDelete succeeds.
     */
    @Test
    public void testCheckAndDelete() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testCheckAndDelete");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValuesA = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValuesA.add(columnValueA);
        TPut putA = new TPut(ByteBuffer.wrap(rowName), columnValuesA);
        putA.setColumnValues(columnValuesA);
        List<TColumnValue> columnValuesB = new ArrayList<>(1);
        TColumnValue columnValueB = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname));
        columnValuesB.add(columnValueB);
        TPut putB = new TPut(ByteBuffer.wrap(rowName), columnValuesB);
        putB.setColumnValues(columnValuesB);
        // put putB so that we know whether the row has been deleted or not
        handler.put(table, putB);
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        Assert.assertFalse(handler.checkAndDelete(table, ByteBuffer.wrap(rowName), ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname), delete));
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        assertTColumnValuesEqual(columnValuesB, result.getColumnValues());
        handler.put(table, putA);
        Assert.assertTrue(handler.checkAndDelete(table, ByteBuffer.wrap(rowName), ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname), delete));
        result = handler.get(table, get);
        Assert.assertFalse(result.isSetRow());
        Assert.assertEquals(0, result.getColumnValuesSize());
    }

    @Test
    public void testScan() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < 10; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testScan" + i))), columnValues);
            handler.put(table, put);
        }
        // create scan instance
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandler.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testScan"));
        scan.setStopRow(Bytes.toBytes("testScan\uffff"));
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testScan" + i)), results.get(i).getRow());
        }
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 10);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    /**
     * Tests keeping a HBase scanner alive for long periods of time. Each call to getScannerRow()
     * should reset the ConnectionCache timeout for the scanner's connection.
     */
    @Test
    public void testLongLivedScan() throws Exception {
        int numTrials = 6;
        int trialPause = 1000;
        int cleanUpInterval = 100;
        Configuration conf = new Configuration(TestThriftHBaseServiceHandler.UTIL.getConfiguration());
        // Set the ConnectionCache timeout to trigger halfway through the trials
        conf.setInt(HBaseServiceHandler.MAX_IDLETIME, ((numTrials / 2) * trialPause));
        conf.setInt(HBaseServiceHandler.CLEANUP_INTERVAL, cleanUpInterval);
        ThriftHBaseServiceHandler handler = new ThriftHBaseServiceHandler(conf, UserProvider.instantiate(conf));
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < numTrials; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testScan" + i))), columnValues);
            handler.put(table, put);
        }
        // create scan instance
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandler.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testScan"));
        scan.setStopRow(Bytes.toBytes("testScan\uffff"));
        // Prevent the scanner from caching results
        scan.setCaching(1);
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        for (int i = 0; i < numTrials; i++) {
            // Make sure that the Scanner doesn't throw an exception after the ConnectionCache timeout
            List<TResult> results = handler.getScannerRows(scanId, 1);
            Assert.assertArrayEquals(Bytes.toBytes(("testScan" + i)), results.get(0).getRow());
            Thread.sleep(trialPause);
        }
    }

    @Test
    public void testReverseScan() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < 10; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testReverseScan" + i))), columnValues);
            handler.put(table, put);
        }
        // create reverse scan instance
        TScan scan = new TScan();
        scan.setReversed(true);
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandler.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testReverseScan\uffff"));
        scan.setStopRow(Bytes.toBytes("testReverseScan"));
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testReverseScan" + (9 - i))), results.get(i).getRow());
        }
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 10);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    @Test
    public void testScanWithFilter() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < 10; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testScanWithFilter" + i))), columnValues);
            handler.put(table, put);
        }
        // create scan instance with filter
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandler.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testScanWithFilter"));
        scan.setStopRow(Bytes.toBytes("testScanWithFilter\uffff"));
        // only get the key part
        scan.setFilterString(ByteBuffer.wrap(Bytes.toBytes("KeyOnlyFilter()")));
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testScanWithFilter" + i)), results.get(i).getRow());
            // check that the value is indeed stripped by the filter
            Assert.assertEquals(0, results.get(i).getColumnValues().get(0).getValue().length);
        }
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 10);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    @Test
    public void testScanWithColumnFamilyTimeRange() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue familyAColumnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        TColumnValue familyBColumnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname));
        long minTimestamp = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            familyAColumnValue.setTimestamp((minTimestamp + i));
            familyBColumnValue.setTimestamp((minTimestamp + i));
            List<TColumnValue> columnValues = new ArrayList<>(2);
            columnValues.add(familyAColumnValue);
            columnValues.add(familyBColumnValue);
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testScanWithColumnFamilyTimeRange" + i))), columnValues);
            handler.put(table, put);
        }
        // create scan instance with column family time range
        TScan scan = new TScan();
        Map<ByteBuffer, TTimeRange> colFamTimeRangeMap = new HashMap<>(2);
        colFamTimeRangeMap.put(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), new TTimeRange((minTimestamp + 3), (minTimestamp + 5)));
        colFamTimeRangeMap.put(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), new TTimeRange((minTimestamp + 6), (minTimestamp + 9)));
        scan.setColFamTimeRangeMap(colFamTimeRangeMap);
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = handler.getScannerRows(scanId, 5);
        Assert.assertEquals(5, results.size());
        int familyACount = 0;
        int familyBCount = 0;
        for (TResult result : results) {
            List<TColumnValue> columnValues = result.getColumnValues();
            if (CollectionUtils.isNotEmpty(columnValues)) {
                if (Bytes.equals(TestThriftHBaseServiceHandler.familyAname, columnValues.get(0).getFamily())) {
                    familyACount++;
                } else
                    if (Bytes.equals(TestThriftHBaseServiceHandler.familyBname, columnValues.get(0).getFamily())) {
                        familyBCount++;
                    }

            }
        }
        Assert.assertEquals(2, familyACount);
        Assert.assertEquals(3, familyBCount);
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 1);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 1);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    @Test
    public void testSmallScan() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>();
        columnValues.add(columnValue);
        for (int i = 0; i < 10; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testSmallScan" + i))), columnValues);
            handler.put(table, put);
        }
        // small scan instance
        TScan scan = new TScan();
        scan.setStartRow(Bytes.toBytes("testSmallScan"));
        scan.setStopRow(Bytes.toBytes("testSmallScan\uffff"));
        scan.setReadType(PREAD);
        scan.setCaching(2);
        // get scanner and rows
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testSmallScan" + i)), results.get(i).getRow());
        }
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 10);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 10);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    @Test
    public void testPutTTL() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testPutTTL");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        // Add some dummy data
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(Bytes.toBytes(1L))));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        Map<ByteBuffer, ByteBuffer> attributes = new HashMap<>();
        // Time in ms for the kv's to live.
        long ttlTimeMs = 2000L;
        // the _ttl attribute is a number of ms ttl for key values in this put.
        attributes.put(ByteBuffer.wrap(Bytes.toBytes("_ttl")), ByteBuffer.wrap(Bytes.toBytes(ttlTimeMs)));
        // Attach the attributes
        put.setAttributes(attributes);
        // Send it.
        handler.put(table, put);
        // Now get the data back
        TGet getOne = new TGet(ByteBuffer.wrap(rowName));
        TResult resultOne = handler.get(table, getOne);
        // It's there.
        Assert.assertArrayEquals(rowName, resultOne.getRow());
        Assert.assertEquals(1, resultOne.getColumnValuesSize());
        // Sleep 30 seconds just to make 100% sure that the key value should be expired.
        Thread.sleep((ttlTimeMs * 15));
        TGet getTwo = new TGet(ByteBuffer.wrap(rowName));
        TResult resultTwo = handler.get(table, getTwo);
        // Nothing should be there since it's ttl'd out.
        Assert.assertNull(resultTwo.getRow());
        Assert.assertEquals(0, resultTwo.getColumnValuesSize());
    }

    @Test
    public void testScanWithBatchSize() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        List<TColumnValue> columnValues = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            String colNum = pad(i, ((byte) (3)));
            TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(Bytes.toBytes(("col" + colNum))), ByteBuffer.wrap(Bytes.toBytes(("val" + colNum))));
            columnValues.add(columnValue);
        }
        TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes("testScanWithBatchSize")), columnValues);
        handler.put(table, put);
        // create scan instance
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandler.familyAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testScanWithBatchSize"));
        scan.setStopRow(Bytes.toBytes("testScanWithBatchSize\uffff"));
        // set batch size to 10 columns per call
        scan.setBatchSize(10);
        // get scanner
        int scanId = handler.openScanner(table, scan);
        List<TResult> results = null;
        for (int i = 0; i < 10; i++) {
            // get batch for single row (10x10 is what we expect)
            results = handler.getScannerRows(scanId, 1);
            Assert.assertEquals(1, results.size());
            // check length of batch
            List<TColumnValue> cols = results.get(0).getColumnValues();
            Assert.assertEquals(10, cols.size());
            // check if the columns are returned and in order
            for (int y = 0; y < 10; y++) {
                int colNum = y + (10 * i);
                String colNumPad = pad(colNum, ((byte) (3)));
                Assert.assertArrayEquals(Bytes.toBytes(("col" + colNumPad)), cols.get(y).getQualifier());
            }
        }
        // check that we are at the end of the scan
        results = handler.getScannerRows(scanId, 1);
        Assert.assertEquals(0, results.size());
        // close scanner and check that it was indeed closed
        handler.closeScanner(scanId);
        try {
            handler.getScannerRows(scanId, 1);
            Assert.fail("Scanner id should be invalid");
        } catch (TIllegalArgument e) {
        }
    }

    @Test
    public void testGetScannerResults() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        // insert data
        TColumnValue columnValue = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(columnValue);
        for (int i = 0; i < 20; i++) {
            TPut put = new TPut(ByteBuffer.wrap(Bytes.toBytes(("testGetScannerResults" + (pad(i, ((byte) (2))))))), columnValues);
            handler.put(table, put);
        }
        // create scan instance
        TScan scan = new TScan();
        List<TColumn> columns = new ArrayList<>(1);
        TColumn column = new TColumn();
        column.setFamily(TestThriftHBaseServiceHandler.familyAname);
        column.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        columns.add(column);
        scan.setColumns(columns);
        scan.setStartRow(Bytes.toBytes("testGetScannerResults"));
        // get 5 rows and check the returned results
        scan.setStopRow(Bytes.toBytes("testGetScannerResults05"));
        List<TResult> results = handler.getScannerResults(table, scan, 5);
        Assert.assertEquals(5, results.size());
        for (int i = 0; i < 5; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testGetScannerResults" + (pad(i, ((byte) (2)))))), results.get(i).getRow());
        }
        // get 10 rows and check the returned results
        scan.setStopRow(Bytes.toBytes("testGetScannerResults10"));
        results = handler.getScannerResults(table, scan, 10);
        Assert.assertEquals(10, results.size());
        for (int i = 0; i < 10; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testGetScannerResults" + (pad(i, ((byte) (2)))))), results.get(i).getRow());
        }
        // get 20 rows and check the returned results
        scan.setStopRow(Bytes.toBytes("testGetScannerResults20"));
        results = handler.getScannerResults(table, scan, 20);
        Assert.assertEquals(20, results.size());
        for (int i = 0; i < 20; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testGetScannerResults" + (pad(i, ((byte) (2)))))), results.get(i).getRow());
        }
        // reverse scan
        scan = new TScan();
        scan.setColumns(columns);
        scan.setReversed(true);
        scan.setStartRow(Bytes.toBytes("testGetScannerResults20"));
        scan.setStopRow(Bytes.toBytes("testGetScannerResults"));
        results = handler.getScannerResults(table, scan, 20);
        Assert.assertEquals(20, results.size());
        for (int i = 0; i < 20; i++) {
            // check if the rows are returned and in order
            Assert.assertArrayEquals(Bytes.toBytes(("testGetScannerResults" + (pad((19 - i), ((byte) (2)))))), results.get(i).getRow());
        }
    }

    @Test
    public void testFilterRegistration() throws Exception {
        Configuration conf = TestThriftHBaseServiceHandler.UTIL.getConfiguration();
        conf.set("hbase.thrift.filters", "MyFilter:filterclass");
        ThriftServer.registerFilters(conf);
        Map<String, String> registeredFilters = ParseFilter.getAllFilters();
        Assert.assertEquals("filterclass", registeredFilters.get("MyFilter"));
    }

    @Test
    public void testMetrics() throws Exception {
        Configuration conf = TestThriftHBaseServiceHandler.UTIL.getConfiguration();
        ThriftMetrics metrics = TestThriftHBaseServiceHandler.getMetrics(conf);
        ThriftHBaseServiceHandler hbaseHandler = createHandler();
        THBaseService.Iface handler = HbaseHandlerMetricsProxy.newInstance(hbaseHandler, metrics, conf);
        byte[] rowName = Bytes.toBytes("testMetrics");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        Assert.assertFalse(handler.exists(table, get));
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname)));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        put.setColumnValues(columnValues);
        handler.put(table, put);
        Assert.assertTrue(handler.exists(table, get));
        TestThriftHBaseServiceHandler.metricsHelper.assertCounter("put_num_ops", 1, metrics.getSource());
        TestThriftHBaseServiceHandler.metricsHelper.assertCounter("exists_num_ops", 2, metrics.getSource());
    }

    @Test
    public void testMetricsWithException() throws Exception {
        byte[] rowkey = Bytes.toBytes("row1");
        byte[] family = Bytes.toBytes("f");
        byte[] col = Bytes.toBytes("c");
        // create a table which will throw exceptions for requests
        TableName tableName = TableName.valueOf(name.getMethodName());
        HTableDescriptor tableDesc = new HTableDescriptor(tableName);
        tableDesc.addCoprocessor(ErrorThrowingGetObserver.class.getName());
        tableDesc.addFamily(new HColumnDescriptor(family));
        Table table = TestThriftHBaseServiceHandler.UTIL.createTable(tableDesc, null);
        table.put(new Put(rowkey).addColumn(family, col, Bytes.toBytes("val1")));
        ThriftHBaseServiceHandler hbaseHandler = createHandler();
        ThriftMetrics metrics = TestThriftHBaseServiceHandler.getMetrics(TestThriftHBaseServiceHandler.UTIL.getConfiguration());
        THBaseService.Iface handler = HbaseHandlerMetricsProxy.newInstance(hbaseHandler, metrics, null);
        ByteBuffer tTableName = ByteBuffer.wrap(tableName.getName());
        // check metrics increment with a successful get
        long preGetCounter = (TestThriftHBaseServiceHandler.metricsHelper.checkCounterExists("get_num_ops", metrics.getSource())) ? TestThriftHBaseServiceHandler.metricsHelper.getCounter("get_num_ops", metrics.getSource()) : 0;
        TGet tGet = new TGet(ByteBuffer.wrap(rowkey));
        TResult tResult = handler.get(tTableName, tGet);
        List<TColumnValue> expectedColumnValues = Lists.newArrayList(new TColumnValue(ByteBuffer.wrap(family), ByteBuffer.wrap(col), ByteBuffer.wrap(Bytes.toBytes("val1"))));
        Assert.assertArrayEquals(rowkey, tResult.getRow());
        List<TColumnValue> returnedColumnValues = tResult.getColumnValues();
        assertTColumnValuesEqual(expectedColumnValues, returnedColumnValues);
        TestThriftHBaseServiceHandler.metricsHelper.assertCounter("get_num_ops", (preGetCounter + 1), metrics.getSource());
        // check metrics increment when the get throws each exception type
        for (ErrorThrowingGetObserver.ErrorType type : values()) {
            testExceptionType(handler, metrics, tTableName, rowkey, type);
        }
    }

    /**
     * See HBASE-17611
     *
     * Latency metrics were capped at ~ 2 seconds due to the use of an int variable to capture the
     * duration.
     */
    @Test
    public void testMetricsPrecision() throws Exception {
        byte[] rowkey = Bytes.toBytes("row1");
        byte[] family = Bytes.toBytes("f");
        byte[] col = Bytes.toBytes("c");
        // create a table which will throw exceptions for requests
        TableName tableName = TableName.valueOf("testMetricsPrecision");
        HTableDescriptor tableDesc = new HTableDescriptor(tableName);
        tableDesc.addCoprocessor(TestThriftHBaseServiceHandler.DelayingRegionObserver.class.getName());
        tableDesc.addFamily(new HColumnDescriptor(family));
        Table table = null;
        try {
            table = TestThriftHBaseServiceHandler.UTIL.createTable(tableDesc, null);
            table.put(new Put(rowkey).addColumn(family, col, Bytes.toBytes("val1")));
            ThriftHBaseServiceHandler hbaseHandler = createHandler();
            ThriftMetrics metrics = TestThriftHBaseServiceHandler.getMetrics(TestThriftHBaseServiceHandler.UTIL.getConfiguration());
            THBaseService.Iface handler = HbaseHandlerMetricsProxy.newInstance(hbaseHandler, metrics, null);
            ByteBuffer tTableName = ByteBuffer.wrap(tableName.getName());
            // check metrics latency with a successful get
            TGet tGet = new TGet(ByteBuffer.wrap(rowkey));
            TResult tResult = handler.get(tTableName, tGet);
            List<TColumnValue> expectedColumnValues = Lists.newArrayList(new TColumnValue(ByteBuffer.wrap(family), ByteBuffer.wrap(col), ByteBuffer.wrap(Bytes.toBytes("val1"))));
            Assert.assertArrayEquals(rowkey, tResult.getRow());
            List<TColumnValue> returnedColumnValues = tResult.getColumnValues();
            assertTColumnValuesEqual(expectedColumnValues, returnedColumnValues);
            TestThriftHBaseServiceHandler.metricsHelper.assertGaugeGt("get_max", 3000L, metrics.getSource());
        } finally {
            if (table != null) {
                try {
                    table.close();
                } catch (IOException ignored) {
                }
                TestThriftHBaseServiceHandler.UTIL.deleteTable(tableName);
            }
        }
    }

    @Test
    public void testAttribute() throws Exception {
        byte[] rowName = Bytes.toBytes("testAttribute");
        byte[] attributeKey = Bytes.toBytes("attribute1");
        byte[] attributeValue = Bytes.toBytes("value1");
        Map<ByteBuffer, ByteBuffer> attributes = new HashMap<>();
        attributes.put(ByteBuffer.wrap(attributeKey), ByteBuffer.wrap(attributeValue));
        TGet tGet = new TGet(ByteBuffer.wrap(rowName));
        tGet.setAttributes(attributes);
        Get get = ThriftUtilities.getFromThrift(tGet);
        Assert.assertArrayEquals(get.getAttribute("attribute1"), attributeValue);
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        TPut tPut = new TPut(ByteBuffer.wrap(rowName), columnValues);
        tPut.setAttributes(attributes);
        Put put = ThriftUtilities.putFromThrift(tPut);
        Assert.assertArrayEquals(put.getAttribute("attribute1"), attributeValue);
        TScan tScan = new TScan();
        tScan.setAttributes(attributes);
        Scan scan = ThriftUtilities.scanFromThrift(tScan);
        Assert.assertArrayEquals(scan.getAttribute("attribute1"), attributeValue);
        List<TColumnIncrement> incrementColumns = new ArrayList<>(1);
        incrementColumns.add(new TColumnIncrement(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname)));
        TIncrement tIncrement = new TIncrement(ByteBuffer.wrap(rowName), incrementColumns);
        tIncrement.setAttributes(attributes);
        Increment increment = ThriftUtilities.incrementFromThrift(tIncrement);
        Assert.assertArrayEquals(increment.getAttribute("attribute1"), attributeValue);
        TDelete tDelete = new TDelete(ByteBuffer.wrap(rowName));
        tDelete.setAttributes(attributes);
        Delete delete = ThriftUtilities.deleteFromThrift(tDelete);
        Assert.assertArrayEquals(delete.getAttribute("attribute1"), attributeValue);
    }

    /**
     * Put valueA to a row, make sure put has happened, then create a mutation object to put valueB
     * and delete ValueA, then check that the row value is only valueB.
     */
    @Test
    public void testMutateRow() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testMutateRow");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        List<TColumnValue> columnValuesA = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname));
        columnValuesA.add(columnValueA);
        TPut putA = new TPut(ByteBuffer.wrap(rowName), columnValuesA);
        putA.setColumnValues(columnValuesA);
        handler.put(table, putA);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        TResult result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        List<TColumnValue> returnedColumnValues = result.getColumnValues();
        List<TColumnValue> expectedColumnValues = new ArrayList<>(1);
        expectedColumnValues.add(columnValueA);
        assertTColumnValuesEqual(expectedColumnValues, returnedColumnValues);
        List<TColumnValue> columnValuesB = new ArrayList<>(1);
        TColumnValue columnValueB = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname));
        columnValuesB.add(columnValueB);
        TPut putB = new TPut(ByteBuffer.wrap(rowName), columnValuesB);
        putB.setColumnValues(columnValuesB);
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TColumn> deleteColumns = new ArrayList<>(1);
        TColumn deleteColumn = new TColumn(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname));
        deleteColumn.setQualifier(TestThriftHBaseServiceHandler.qualifierAname);
        deleteColumns.add(deleteColumn);
        delete.setColumns(deleteColumns);
        List<TMutation> mutations = new ArrayList<>(2);
        TMutation mutationA = TMutation.put(putB);
        mutations.add(mutationA);
        TMutation mutationB = TMutation.deleteSingle(delete);
        mutations.add(mutationB);
        TRowMutations tRowMutations = new TRowMutations(ByteBuffer.wrap(rowName), mutations);
        handler.mutateRow(table, tRowMutations);
        result = handler.get(table, get);
        Assert.assertArrayEquals(rowName, result.getRow());
        returnedColumnValues = result.getColumnValues();
        expectedColumnValues = new ArrayList(1);
        expectedColumnValues.add(columnValueB);
        assertTColumnValuesEqual(expectedColumnValues, returnedColumnValues);
    }

    /**
     * Create TPut, TDelete , TIncrement objects, set durability then call ThriftUtility
     * functions to get Put , Delete and Increment respectively. Use getDurability to make sure
     * the returned objects have the appropriate durability setting.
     */
    @Test
    public void testDurability() throws Exception {
        byte[] rowName = Bytes.toBytes("testDurability");
        List<TColumnValue> columnValues = new ArrayList<>(1);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname)));
        List<TColumnIncrement> incrementColumns = new ArrayList<>(1);
        incrementColumns.add(new TColumnIncrement(ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname)));
        TDelete tDelete = new TDelete(ByteBuffer.wrap(rowName));
        tDelete.setDurability(SKIP_WAL);
        Delete delete = ThriftUtilities.deleteFromThrift(tDelete);
        Assert.assertEquals(Durability.SKIP_WAL, delete.getDurability());
        tDelete.setDurability(ASYNC_WAL);
        delete = ThriftUtilities.deleteFromThrift(tDelete);
        Assert.assertEquals(Durability.ASYNC_WAL, delete.getDurability());
        tDelete.setDurability(SYNC_WAL);
        delete = ThriftUtilities.deleteFromThrift(tDelete);
        Assert.assertEquals(Durability.SYNC_WAL, delete.getDurability());
        tDelete.setDurability(FSYNC_WAL);
        delete = ThriftUtilities.deleteFromThrift(tDelete);
        Assert.assertEquals(Durability.FSYNC_WAL, delete.getDurability());
        TPut tPut = new TPut(ByteBuffer.wrap(rowName), columnValues);
        tPut.setDurability(SKIP_WAL);
        Put put = ThriftUtilities.putFromThrift(tPut);
        Assert.assertEquals(Durability.SKIP_WAL, put.getDurability());
        tPut.setDurability(ASYNC_WAL);
        put = ThriftUtilities.putFromThrift(tPut);
        Assert.assertEquals(Durability.ASYNC_WAL, put.getDurability());
        tPut.setDurability(SYNC_WAL);
        put = ThriftUtilities.putFromThrift(tPut);
        Assert.assertEquals(Durability.SYNC_WAL, put.getDurability());
        tPut.setDurability(FSYNC_WAL);
        put = ThriftUtilities.putFromThrift(tPut);
        Assert.assertEquals(Durability.FSYNC_WAL, put.getDurability());
        TIncrement tIncrement = new TIncrement(ByteBuffer.wrap(rowName), incrementColumns);
        tIncrement.setDurability(SKIP_WAL);
        Increment increment = ThriftUtilities.incrementFromThrift(tIncrement);
        Assert.assertEquals(Durability.SKIP_WAL, increment.getDurability());
        tIncrement.setDurability(ASYNC_WAL);
        increment = ThriftUtilities.incrementFromThrift(tIncrement);
        Assert.assertEquals(Durability.ASYNC_WAL, increment.getDurability());
        tIncrement.setDurability(SYNC_WAL);
        increment = ThriftUtilities.incrementFromThrift(tIncrement);
        Assert.assertEquals(Durability.SYNC_WAL, increment.getDurability());
        tIncrement.setDurability(FSYNC_WAL);
        increment = ThriftUtilities.incrementFromThrift(tIncrement);
        Assert.assertEquals(Durability.FSYNC_WAL, increment.getDurability());
    }

    @Test
    public void testCheckAndMutate() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandler.tableAname);
        ByteBuffer row = ByteBuffer.wrap(Bytes.toBytes("row"));
        ByteBuffer family = ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyAname);
        ByteBuffer qualifier = ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierAname);
        ByteBuffer value = ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueAname);
        // Create a mutation to write to 'B', our "mutate" of "checkAndMutate"
        List<TColumnValue> columnValuesB = new ArrayList<>(1);
        TColumnValue columnValueB = new TColumnValue(family, ByteBuffer.wrap(TestThriftHBaseServiceHandler.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandler.valueBname));
        columnValuesB.add(columnValueB);
        TPut putB = new TPut(row, columnValuesB);
        putB.setColumnValues(columnValuesB);
        TRowMutations tRowMutations = new TRowMutations(row, Arrays.<TMutation>asList(TMutation.put(putB)));
        // Empty table when we begin
        TResult result = handler.get(table, new TGet(row));
        Assert.assertEquals(0, result.getColumnValuesSize());
        // checkAndMutate -- condition should fail because the value doesn't exist.
        Assert.assertFalse("Expected condition to not pass", handler.checkAndMutate(table, row, family, qualifier, EQUAL, value, tRowMutations));
        List<TColumnValue> columnValuesA = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(family, qualifier, value);
        columnValuesA.add(columnValueA);
        // Put an update 'A'
        handler.put(table, new TPut(row, columnValuesA));
        // Verify that the update is there
        result = handler.get(table, new TGet(row));
        Assert.assertEquals(1, result.getColumnValuesSize());
        assertTColumnValueEqual(columnValueA, result.getColumnValues().get(0));
        // checkAndMutate -- condition should pass since we added the value
        Assert.assertTrue("Expected condition to pass", handler.checkAndMutate(table, row, family, qualifier, EQUAL, value, tRowMutations));
        result = handler.get(table, new TGet(row));
        Assert.assertEquals(2, result.getColumnValuesSize());
        assertTColumnValueEqual(columnValueA, result.getColumnValues().get(0));
        assertTColumnValueEqual(columnValueB, result.getColumnValues().get(1));
    }

    @Test
    public void testConsistency() throws Exception {
        byte[] rowName = Bytes.toBytes("testConsistency");
        TGet tGet = new TGet(ByteBuffer.wrap(rowName));
        tGet.setConsistency(STRONG);
        Get get = ThriftUtilities.getFromThrift(tGet);
        Assert.assertEquals(Consistency.STRONG, get.getConsistency());
        tGet.setConsistency(TIMELINE);
        tGet.setTargetReplicaId(1);
        get = ThriftUtilities.getFromThrift(tGet);
        Assert.assertEquals(Consistency.TIMELINE, get.getConsistency());
        Assert.assertEquals(1, get.getReplicaId());
        TScan tScan = new TScan();
        tScan.setConsistency(STRONG);
        Scan scan = ThriftUtilities.scanFromThrift(tScan);
        Assert.assertEquals(Consistency.STRONG, scan.getConsistency());
        tScan.setConsistency(TIMELINE);
        tScan.setTargetReplicaId(1);
        scan = ThriftUtilities.scanFromThrift(tScan);
        Assert.assertEquals(Consistency.TIMELINE, scan.getConsistency());
        Assert.assertEquals(1, scan.getReplicaId());
        TResult tResult = new TResult();
        Assert.assertFalse(tResult.isSetStale());
        tResult.setStale(true);
        Assert.assertTrue(tResult.isSetStale());
    }

    @Test
    public void testDDLOpertions() throws Exception {
        String namespace = "testDDLOpertionsNamespace";
        String table = "testDDLOpertionsTable";
        TTableName tTableName = new TTableName();
        tTableName.setNs(Bytes.toBytes(namespace));
        tTableName.setQualifier(Bytes.toBytes(table));
        ThriftHBaseServiceHandler handler = createHandler();
        // create name space
        TNamespaceDescriptor namespaceDescriptor = new TNamespaceDescriptor();
        namespaceDescriptor.setName(namespace);
        namespaceDescriptor.putToConfiguration("key1", "value1");
        namespaceDescriptor.putToConfiguration("key2", "value2");
        handler.createNamespace(namespaceDescriptor);
        // list namespace
        List<TNamespaceDescriptor> namespaceDescriptors = handler.listNamespaceDescriptors();
        // should have 3 namespace, default hbase and testDDLOpertionsNamespace
        Assert.assertTrue(((namespaceDescriptors.size()) == 3));
        // modify namesapce
        namespaceDescriptor.putToConfiguration("kye3", "value3");
        handler.modifyNamespace(namespaceDescriptor);
        // get namespace
        TNamespaceDescriptor namespaceDescriptorReturned = handler.getNamespaceDescriptor(namespace);
        Assert.assertTrue(((namespaceDescriptorReturned.getConfiguration().size()) == 3));
        // create table
        TTableDescriptor tableDescriptor = new TTableDescriptor();
        tableDescriptor.setTableName(tTableName);
        TColumnFamilyDescriptor columnFamilyDescriptor1 = new TColumnFamilyDescriptor();
        columnFamilyDescriptor1.setName(TestThriftHBaseServiceHandler.familyAname);
        columnFamilyDescriptor1.setDataBlockEncoding(DIFF);
        tableDescriptor.addToColumns(columnFamilyDescriptor1);
        List<ByteBuffer> splitKeys = new ArrayList<>();
        splitKeys.add(ByteBuffer.wrap(Bytes.toBytes(5)));
        handler.createTable(tableDescriptor, splitKeys);
        // modify table
        tableDescriptor.setDurability(ASYNC_WAL);
        handler.modifyTable(tableDescriptor);
        // modify column family
        columnFamilyDescriptor1.setInMemory(true);
        handler.modifyColumnFamily(tTableName, columnFamilyDescriptor1);
        // add column family
        TColumnFamilyDescriptor columnFamilyDescriptor2 = new TColumnFamilyDescriptor();
        columnFamilyDescriptor2.setName(TestThriftHBaseServiceHandler.familyBname);
        columnFamilyDescriptor2.setDataBlockEncoding(PREFIX);
        handler.addColumnFamily(tTableName, columnFamilyDescriptor2);
        // get table descriptor
        TTableDescriptor tableDescriptorReturned = handler.getTableDescriptor(tTableName);
        Assert.assertTrue(((tableDescriptorReturned.getColumns().size()) == 2));
        Assert.assertTrue(((tableDescriptorReturned.getDurability()) == (TDurability.ASYNC_WAL)));
        TColumnFamilyDescriptor columnFamilyDescriptor1Returned = tableDescriptorReturned.getColumns().stream().filter(( desc) -> Bytes.equals(desc.getName(), TestThriftHBaseServiceHandler.familyAname)).findFirst().get();
        Assert.assertTrue(((columnFamilyDescriptor1Returned.isInMemory()) == true));
        // delete column family
        handler.deleteColumnFamily(tTableName, ByteBuffer.wrap(TestThriftHBaseServiceHandler.familyBname));
        tableDescriptorReturned = handler.getTableDescriptor(tTableName);
        Assert.assertTrue(((tableDescriptorReturned.getColumns().size()) == 1));
        // disable table
        handler.disableTable(tTableName);
        Assert.assertTrue(handler.isTableDisabled(tTableName));
        // enable table
        handler.enableTable(tTableName);
        Assert.assertTrue(handler.isTableEnabled(tTableName));
        Assert.assertTrue(handler.isTableAvailable(tTableName));
        // truncate table
        handler.disableTable(tTableName);
        handler.truncateTable(tTableName, true);
        Assert.assertTrue(handler.isTableAvailable(tTableName));
        // delete table
        handler.disableTable(tTableName);
        handler.deleteTable(tTableName);
        Assert.assertFalse(handler.tableExists(tTableName));
        // delete namespace
        handler.deleteNamespace(namespace);
        namespaceDescriptors = handler.listNamespaceDescriptors();
        // should have 2 namespace, default and hbase
        Assert.assertTrue(((namespaceDescriptors.size()) == 2));
    }

    @Test
    public void testGetTableDescriptor() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        TTableDescriptor tableDescriptor = handler.getTableDescriptor(ThriftUtilities.tableNameFromHBase(TableName.valueOf(TestThriftHBaseServiceHandler.tableAname)));
        TableDescriptor table = ThriftUtilities.tableDescriptorFromThrift(tableDescriptor);
        Assert.assertTrue(table.getTableName().equals(TableName.valueOf(TestThriftHBaseServiceHandler.tableAname)));
        Assert.assertTrue(((table.getColumnFamilies().length) == 2));
        Assert.assertTrue(((table.getColumnFamily(TestThriftHBaseServiceHandler.familyAname).getMaxVersions()) == 3));
        Assert.assertTrue(((table.getColumnFamily(TestThriftHBaseServiceHandler.familyBname).getMaxVersions()) == 2));
    }

    public static class DelayingRegionObserver implements RegionCoprocessor , RegionObserver {
        private static final Logger LOG = LoggerFactory.getLogger(TestThriftHBaseServiceHandler.DelayingRegionObserver.class);

        // sleep time in msec
        private long delayMillis;

        @Override
        public Optional<RegionObserver> getRegionObserver() {
            return Optional.of(this);
        }

        @Override
        public void start(CoprocessorEnvironment e) throws IOException {
            this.delayMillis = e.getConfiguration().getLong("delayingregionobserver.delay", 3000);
        }

        @Override
        public void preGetOp(ObserverContext<RegionCoprocessorEnvironment> e, Get get, List<Cell> results) throws IOException {
            try {
                long start = System.currentTimeMillis();
                TimeUnit.MILLISECONDS.sleep(delayMillis);
                if (TestThriftHBaseServiceHandler.DelayingRegionObserver.LOG.isTraceEnabled()) {
                    TestThriftHBaseServiceHandler.DelayingRegionObserver.LOG.trace((("Slept for " + ((System.currentTimeMillis()) - start)) + " msec"));
                }
            } catch (InterruptedException ie) {
                throw new InterruptedIOException("Interrupted while sleeping");
            }
        }
    }
}

