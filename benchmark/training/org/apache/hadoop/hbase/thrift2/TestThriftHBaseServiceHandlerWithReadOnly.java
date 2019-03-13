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
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.thrift2.generated.TAppend;
import org.apache.hadoop.hbase.thrift2.generated.TColumnIncrement;
import org.apache.hadoop.hbase.thrift2.generated.TColumnValue;
import org.apache.hadoop.hbase.thrift2.generated.TDelete;
import org.apache.hadoop.hbase.thrift2.generated.TGet;
import org.apache.hadoop.hbase.thrift2.generated.TIOError;
import org.apache.hadoop.hbase.thrift2.generated.TIncrement;
import org.apache.hadoop.hbase.thrift2.generated.TMutation;
import org.apache.hadoop.hbase.thrift2.generated.TPut;
import org.apache.hadoop.hbase.thrift2.generated.TRowMutations;
import org.apache.hadoop.hbase.thrift2.generated.TScan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.thrift.TException;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ ClientTests.class, MediumTests.class })
public class TestThriftHBaseServiceHandlerWithReadOnly {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestThriftHBaseServiceHandlerWithReadOnly.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    // Static names for tables, columns, rows, and values
    private static byte[] tableAname = Bytes.toBytes("tableA");

    private static byte[] familyAname = Bytes.toBytes("familyA");

    private static byte[] familyBname = Bytes.toBytes("familyB");

    private static byte[] qualifierAname = Bytes.toBytes("qualifierA");

    private static byte[] qualifierBname = Bytes.toBytes("qualifierB");

    private static byte[] valueAname = Bytes.toBytes("valueA");

    private static byte[] valueBname = Bytes.toBytes("valueB");

    private static HColumnDescriptor[] families = new HColumnDescriptor[]{ new HColumnDescriptor(TestThriftHBaseServiceHandlerWithReadOnly.familyAname).setMaxVersions(3), new HColumnDescriptor(TestThriftHBaseServiceHandlerWithReadOnly.familyBname).setMaxVersions(2) };

    @Test
    public void testExistsWithReadOnly() throws TException {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testExists");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        boolean exceptionCaught = false;
        try {
            handler.exists(table, get);
        } catch (TIOError e) {
            exceptionCaught = true;
        } finally {
            Assert.assertFalse(exceptionCaught);
        }
    }

    @Test
    public void testExistsAllWithReadOnly() throws TException {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName1 = Bytes.toBytes("testExistsAll1");
        byte[] rowName2 = Bytes.toBytes("testExistsAll2");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        List<TGet> gets = new ArrayList<>();
        gets.add(new TGet(ByteBuffer.wrap(rowName1)));
        gets.add(new TGet(ByteBuffer.wrap(rowName2)));
        boolean exceptionCaught = false;
        try {
            handler.existsAll(table, gets);
        } catch (TIOError e) {
            exceptionCaught = true;
        } finally {
            Assert.assertFalse(exceptionCaught);
        }
    }

    @Test
    public void testGetWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testGet");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        TGet get = new TGet(ByteBuffer.wrap(rowName));
        boolean exceptionCaught = false;
        try {
            handler.get(table, get);
        } catch (TIOError e) {
            exceptionCaught = true;
        } finally {
            Assert.assertFalse(exceptionCaught);
        }
    }

    @Test
    public void testGetMultipleWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        byte[] rowName1 = Bytes.toBytes("testGetMultiple1");
        byte[] rowName2 = Bytes.toBytes("testGetMultiple2");
        List<TGet> gets = new ArrayList<>(2);
        gets.add(new TGet(ByteBuffer.wrap(rowName1)));
        gets.add(new TGet(ByteBuffer.wrap(rowName2)));
        boolean exceptionCaught = false;
        try {
            handler.getMultiple(table, gets);
        } catch (TIOError e) {
            exceptionCaught = true;
        } finally {
            Assert.assertFalse(exceptionCaught);
        }
    }

    @Test
    public void testPutWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        byte[] rowName = Bytes.toBytes("testPut");
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueBname)));
        TPut put = new TPut(ByteBuffer.wrap(rowName), columnValues);
        boolean exceptionCaught = false;
        try {
            handler.put(table, put);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testCheckAndPutWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testCheckAndPut");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        List<TColumnValue> columnValuesA = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname));
        columnValuesA.add(columnValueA);
        TPut putA = new TPut(ByteBuffer.wrap(rowName), columnValuesA);
        putA.setColumnValues(columnValuesA);
        List<TColumnValue> columnValuesB = new ArrayList<>(1);
        TColumnValue columnValueB = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueBname));
        columnValuesB.add(columnValueB);
        TPut putB = new TPut(ByteBuffer.wrap(rowName), columnValuesB);
        putB.setColumnValues(columnValuesB);
        boolean exceptionCaught = false;
        try {
            handler.checkAndPut(table, ByteBuffer.wrap(rowName), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname), putB);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testPutMultipleWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        byte[] rowName1 = Bytes.toBytes("testPutMultiple1");
        byte[] rowName2 = Bytes.toBytes("testPutMultiple2");
        List<TColumnValue> columnValues = new ArrayList<>(2);
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname)));
        columnValues.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueBname)));
        List<TPut> puts = new ArrayList<>(2);
        puts.add(new TPut(ByteBuffer.wrap(rowName1), columnValues));
        puts.add(new TPut(ByteBuffer.wrap(rowName2), columnValues));
        boolean exceptionCaught = false;
        try {
            handler.putMultiple(table, puts);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testDeleteWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testDelete");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        boolean exceptionCaught = false;
        try {
            handler.deleteSingle(table, delete);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testDeleteMultipleWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        byte[] rowName1 = Bytes.toBytes("testDeleteMultiple1");
        byte[] rowName2 = Bytes.toBytes("testDeleteMultiple2");
        List<TDelete> deletes = new ArrayList<>(2);
        deletes.add(new TDelete(ByteBuffer.wrap(rowName1)));
        deletes.add(new TDelete(ByteBuffer.wrap(rowName2)));
        boolean exceptionCaught = false;
        try {
            handler.deleteMultiple(table, deletes);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testCheckAndMutateWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        ByteBuffer row = ByteBuffer.wrap(Bytes.toBytes("row"));
        ByteBuffer family = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname);
        ByteBuffer qualifier = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname);
        ByteBuffer value = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname);
        List<TColumnValue> columnValuesB = new ArrayList<>(1);
        TColumnValue columnValueB = new TColumnValue(family, ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierBname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueBname));
        columnValuesB.add(columnValueB);
        TPut putB = new TPut(row, columnValuesB);
        putB.setColumnValues(columnValuesB);
        TRowMutations tRowMutations = new TRowMutations(row, Arrays.<TMutation>asList(TMutation.put(putB)));
        boolean exceptionCaught = false;
        try {
            handler.checkAndMutate(table, row, family, qualifier, EQUAL, value, tRowMutations);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testCheckAndDeleteWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testCheckAndDelete");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        boolean exceptionCaught = false;
        try {
            handler.checkAndDelete(table, ByteBuffer.wrap(rowName), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname), delete);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testIncrementWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testIncrement");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        List<TColumnIncrement> incrementColumns = new ArrayList<>(1);
        incrementColumns.add(new TColumnIncrement(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname)));
        TIncrement increment = new TIncrement(ByteBuffer.wrap(rowName), incrementColumns);
        boolean exceptionCaught = false;
        try {
            handler.increment(table, increment);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testAppendWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testAppend");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        byte[] v1 = Bytes.toBytes("42");
        List<TColumnValue> appendColumns = new ArrayList<>(1);
        appendColumns.add(new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(v1)));
        TAppend append = new TAppend(ByteBuffer.wrap(rowName), appendColumns);
        boolean exceptionCaught = false;
        try {
            handler.append(table, append);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testMutateRowWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        byte[] rowName = Bytes.toBytes("testMutateRow");
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        List<TColumnValue> columnValuesA = new ArrayList<>(1);
        TColumnValue columnValueA = new TColumnValue(ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.familyAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.qualifierAname), ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.valueAname));
        columnValuesA.add(columnValueA);
        TPut putA = new TPut(ByteBuffer.wrap(rowName), columnValuesA);
        putA.setColumnValues(columnValuesA);
        TDelete delete = new TDelete(ByteBuffer.wrap(rowName));
        List<TMutation> mutations = new ArrayList<>(2);
        TMutation mutationA = TMutation.put(putA);
        mutations.add(mutationA);
        TMutation mutationB = TMutation.deleteSingle(delete);
        mutations.add(mutationB);
        TRowMutations tRowMutations = new TRowMutations(ByteBuffer.wrap(rowName), mutations);
        boolean exceptionCaught = false;
        try {
            handler.mutateRow(table, tRowMutations);
        } catch (TIOError e) {
            exceptionCaught = true;
            Assert.assertTrue(((e.getCause()) instanceof DoNotRetryIOException));
            Assert.assertEquals("Thrift Server is in Read-only mode.", e.getMessage());
        } finally {
            Assert.assertTrue(exceptionCaught);
        }
    }

    @Test
    public void testScanWithReadOnly() throws Exception {
        ThriftHBaseServiceHandler handler = createHandler();
        ByteBuffer table = ByteBuffer.wrap(TestThriftHBaseServiceHandlerWithReadOnly.tableAname);
        TScan scan = new TScan();
        boolean exceptionCaught = false;
        try {
            int scanId = handler.openScanner(table, scan);
            handler.getScannerRows(scanId, 10);
            handler.closeScanner(scanId);
        } catch (TIOError e) {
            exceptionCaught = true;
        } finally {
            Assert.assertFalse(exceptionCaught);
        }
    }
}

