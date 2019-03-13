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
package org.apache.hadoop.hbase.client;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import junit.framework.TestCase;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellComparator;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestCase;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.ClassRule;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Result.EMPTY_RESULT;


@Category({ SmallTests.class, ClientTests.class })
public class TestResult extends TestCase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestResult.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestResult.class.getName());

    static final byte[] row = Bytes.toBytes("row");

    static final byte[] family = Bytes.toBytes("family");

    static final byte[] value = Bytes.toBytes("value");

    /**
     * Run some tests to ensure Result acts like a proper CellScanner.
     *
     * @throws IOException
     * 		
     */
    public void testResultAsCellScanner() throws IOException {
        Cell[] cells = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 10);
        Arrays.sort(cells, CellComparator.getInstance());
        Result r = Result.create(cells);
        assertSame(r, cells);
        // Assert I run over same result multiple times.
        assertSame(r.cellScanner(), cells);
        assertSame(r.cellScanner(), cells);
        // Assert we are not creating new object when doing cellscanner
        TestCase.assertTrue((r == (r.cellScanner())));
    }

    public void testBasicGetColumn() throws Exception {
        KeyValue[] kvs = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 100);
        Arrays.sort(kvs, CellComparator.getInstance());
        Result r = Result.create(kvs);
        for (int i = 0; i < 100; ++i) {
            final byte[] qf = Bytes.toBytes(i);
            List<Cell> ks = r.getColumnCells(TestResult.family, qf);
            TestCase.assertEquals(1, ks.size());
            TestCase.assertTrue(CellUtil.matchingQualifier(ks.get(0), qf));
            TestCase.assertEquals(ks.get(0), r.getColumnLatestCell(TestResult.family, qf));
        }
    }

    public void testCurrentOnEmptyCell() throws IOException {
        Result r = Result.create(new Cell[0]);
        TestCase.assertFalse(r.advance());
        TestCase.assertNull(r.current());
    }

    public void testAdvanceTwiceOnEmptyCell() throws IOException {
        Result r = Result.create(new Cell[0]);
        TestCase.assertFalse(r.advance());
        try {
            r.advance();
            TestCase.fail("NoSuchElementException should have been thrown!");
        } catch (NoSuchElementException ex) {
            TestResult.LOG.debug(("As expected: " + (ex.getMessage())));
        }
    }

    public void testMultiVersionGetColumn() throws Exception {
        KeyValue[] kvs1 = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 100);
        KeyValue[] kvs2 = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 200, 100);
        KeyValue[] kvs = new KeyValue[(kvs1.length) + (kvs2.length)];
        System.arraycopy(kvs1, 0, kvs, 0, kvs1.length);
        System.arraycopy(kvs2, 0, kvs, kvs1.length, kvs2.length);
        Arrays.sort(kvs, CellComparator.getInstance());
        Result r = Result.create(kvs);
        for (int i = 0; i < 100; ++i) {
            final byte[] qf = Bytes.toBytes(i);
            List<Cell> ks = r.getColumnCells(TestResult.family, qf);
            TestCase.assertEquals(2, ks.size());
            TestCase.assertTrue(CellUtil.matchingQualifier(ks.get(0), qf));
            TestCase.assertEquals(200, ks.get(0).getTimestamp());
            TestCase.assertEquals(ks.get(0), r.getColumnLatestCell(TestResult.family, qf));
        }
    }

    public void testBasicGetValue() throws Exception {
        KeyValue[] kvs = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 100);
        Arrays.sort(kvs, CellComparator.getInstance());
        Result r = Result.create(kvs);
        for (int i = 0; i < 100; ++i) {
            final byte[] qf = Bytes.toBytes(i);
            HBaseTestCase.assertByteEquals(Bytes.add(TestResult.value, Bytes.toBytes(i)), r.getValue(TestResult.family, qf));
            TestCase.assertTrue(r.containsColumn(TestResult.family, qf));
        }
    }

    public void testMultiVersionGetValue() throws Exception {
        KeyValue[] kvs1 = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 100);
        KeyValue[] kvs2 = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 200, 100);
        KeyValue[] kvs = new KeyValue[(kvs1.length) + (kvs2.length)];
        System.arraycopy(kvs1, 0, kvs, 0, kvs1.length);
        System.arraycopy(kvs2, 0, kvs, kvs1.length, kvs2.length);
        Arrays.sort(kvs, CellComparator.getInstance());
        Result r = Result.create(kvs);
        for (int i = 0; i < 100; ++i) {
            final byte[] qf = Bytes.toBytes(i);
            HBaseTestCase.assertByteEquals(Bytes.add(TestResult.value, Bytes.toBytes(i)), r.getValue(TestResult.family, qf));
            TestCase.assertTrue(r.containsColumn(TestResult.family, qf));
        }
    }

    public void testBasicLoadValue() throws Exception {
        KeyValue[] kvs = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 100);
        Arrays.sort(kvs, CellComparator.getInstance());
        Result r = Result.create(kvs);
        ByteBuffer loadValueBuffer = ByteBuffer.allocate(1024);
        for (int i = 0; i < 100; ++i) {
            final byte[] qf = Bytes.toBytes(i);
            loadValueBuffer.clear();
            r.loadValue(TestResult.family, qf, loadValueBuffer);
            loadValueBuffer.flip();
            TestCase.assertEquals(loadValueBuffer, ByteBuffer.wrap(Bytes.add(TestResult.value, Bytes.toBytes(i))));
            TestCase.assertEquals(ByteBuffer.wrap(Bytes.add(TestResult.value, Bytes.toBytes(i))), r.getValueAsByteBuffer(TestResult.family, qf));
        }
    }

    public void testMultiVersionLoadValue() throws Exception {
        KeyValue[] kvs1 = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 1, 100);
        KeyValue[] kvs2 = TestResult.genKVs(TestResult.row, TestResult.family, TestResult.value, 200, 100);
        KeyValue[] kvs = new KeyValue[(kvs1.length) + (kvs2.length)];
        System.arraycopy(kvs1, 0, kvs, 0, kvs1.length);
        System.arraycopy(kvs2, 0, kvs, kvs1.length, kvs2.length);
        Arrays.sort(kvs, CellComparator.getInstance());
        ByteBuffer loadValueBuffer = ByteBuffer.allocate(1024);
        Result r = Result.create(kvs);
        for (int i = 0; i < 100; ++i) {
            final byte[] qf = Bytes.toBytes(i);
            loadValueBuffer.clear();
            r.loadValue(TestResult.family, qf, loadValueBuffer);
            loadValueBuffer.flip();
            TestCase.assertEquals(loadValueBuffer, ByteBuffer.wrap(Bytes.add(TestResult.value, Bytes.toBytes(i))));
            TestCase.assertEquals(ByteBuffer.wrap(Bytes.add(TestResult.value, Bytes.toBytes(i))), r.getValueAsByteBuffer(TestResult.family, qf));
        }
    }

    /**
     * Verify that Result.compareResults(...) behaves correctly.
     */
    public void testCompareResults() throws Exception {
        byte[] value1 = Bytes.toBytes("value1");
        byte[] qual = Bytes.toBytes("qual");
        KeyValue kv1 = new KeyValue(TestResult.row, TestResult.family, qual, TestResult.value);
        KeyValue kv2 = new KeyValue(TestResult.row, TestResult.family, qual, value1);
        Result r1 = Result.create(new KeyValue[]{ kv1 });
        Result r2 = Result.create(new KeyValue[]{ kv2 });
        // no exception thrown
        Result.compareResults(r1, r1);
        try {
            // these are different (HBASE-4800)
            Result.compareResults(r1, r2);
            TestCase.fail();
        } catch (Exception x) {
            TestCase.assertTrue(x.getMessage().startsWith("This result was different:"));
        }
    }

    /**
     * Verifies that one can't modify instance of EMPTY_RESULT.
     */
    public void testEmptyResultIsReadonly() {
        Result emptyResult = EMPTY_RESULT;
        Result otherResult = new Result();
        try {
            emptyResult.copyFrom(otherResult);
            TestCase.fail("UnsupportedOperationException should have been thrown!");
        } catch (UnsupportedOperationException ex) {
            TestResult.LOG.debug(("As expected: " + (ex.getMessage())));
        }
        try {
            emptyResult.setExists(true);
            TestCase.fail("UnsupportedOperationException should have been thrown!");
        } catch (UnsupportedOperationException ex) {
            TestResult.LOG.debug(("As expected: " + (ex.getMessage())));
        }
    }
}

