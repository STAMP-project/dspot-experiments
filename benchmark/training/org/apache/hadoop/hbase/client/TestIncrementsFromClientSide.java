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


import HConstants.HBASE_CLIENT_PAUSE;
import HConstants.HBASE_RPC_TIMEOUT_KEY;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.DoNotRetryIOException;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.hadoop.hbase.client.HConnectionTestingUtility.SleepAtFirstRpcCall.SLEEP_TIME_CONF_KEY;


/**
 * Run Increment tests that use the HBase clients; {@link HTable}.
 *
 * Test is parameterized to run the slow and fast increment code paths. If fast, in the @before, we
 * do a rolling restart of the single regionserver so that it can pick up the go fast configuration.
 * Doing it this way should be faster than starting/stopping a cluster per test.
 *
 * Test takes a long time because spin up a cluster between each run -- ugh.
 */
@Category(LargeTests.class)
public class TestIncrementsFromClientSide {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestIncrementsFromClientSide.class);

    final Logger LOG = LoggerFactory.getLogger(getClass());

    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static byte[] ROW = Bytes.toBytes("testRow");

    private static byte[] FAMILY = Bytes.toBytes("testFamily");

    private static byte[] QUALIFIER = Bytes.toBytes("testQualifier");

    // This test depends on there being only one slave running at at a time. See the @Before
    // method where we do rolling restart.
    protected static int SLAVES = 1;

    @Rule
    public TestName name = new TestName();

    /**
     * Test increment result when there are duplicate rpc request.
     */
    @Test
    public void testDuplicateIncrement() throws Exception {
        HTableDescriptor hdt = TestIncrementsFromClientSide.TEST_UTIL.createTableDescriptor(TableName.valueOf(name.getMethodName()));
        Map<String, String> kvs = new HashMap<>();
        kvs.put(SLEEP_TIME_CONF_KEY, "2000");
        hdt.addCoprocessor(HConnectionTestingUtility.SleepAtFirstRpcCall.class.getName(), null, 1, kvs);
        TestIncrementsFromClientSide.TEST_UTIL.createTable(hdt, new byte[][]{ TestIncrementsFromClientSide.ROW }).close();
        Configuration c = new Configuration(TestIncrementsFromClientSide.TEST_UTIL.getConfiguration());
        c.setInt(HBASE_CLIENT_PAUSE, 50);
        // Client will retry beacuse rpc timeout is small than the sleep time of first rpc call
        c.setInt(HBASE_RPC_TIMEOUT_KEY, 1500);
        Connection connection = ConnectionFactory.createConnection(c);
        Table t = connection.getTable(TableName.valueOf(name.getMethodName()));
        if (t instanceof HTable) {
            HTable table = ((HTable) (t));
            table.setOperationTimeout((3 * 1000));
            try {
                Increment inc = new Increment(TestIncrementsFromClientSide.ROW);
                inc.addColumn(TestIncrementsFromClientSide.TEST_UTIL.fam1, TestIncrementsFromClientSide.QUALIFIER, 1);
                Result result = table.increment(inc);
                Cell[] cells = result.rawCells();
                Assert.assertEquals(1, cells.length);
                TestIncrementsFromClientSide.assertIncrementKey(cells[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.TEST_UTIL.fam1, TestIncrementsFromClientSide.QUALIFIER, 1);
                // Verify expected result
                Result readResult = table.get(new Get(TestIncrementsFromClientSide.ROW));
                cells = readResult.rawCells();
                Assert.assertEquals(1, cells.length);
                TestIncrementsFromClientSide.assertIncrementKey(cells[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.TEST_UTIL.fam1, TestIncrementsFromClientSide.QUALIFIER, 1);
            } finally {
                table.close();
                connection.close();
            }
        }
    }

    @Test
    public void testIncrementWithDeletes() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final TableName TABLENAME = TableName.valueOf(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        final byte[] COLUMN = Bytes.toBytes("column");
        ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, COLUMN, 5);
        TestIncrementsFromClientSide.TEST_UTIL.flush(TABLENAME);
        Delete del = new Delete(TestIncrementsFromClientSide.ROW);
        ht.delete(del);
        ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, COLUMN, 5);
        Get get = new Get(TestIncrementsFromClientSide.ROW);
        Result r = ht.get(get);
        Assert.assertEquals(1, r.size());
        Assert.assertEquals(5, Bytes.toLong(r.getValue(TestIncrementsFromClientSide.FAMILY, COLUMN)));
    }

    @Test
    public void testIncrementingInvalidValue() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final TableName TABLENAME = TableName.valueOf(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        final byte[] COLUMN = Bytes.toBytes("column");
        Put p = new Put(TestIncrementsFromClientSide.ROW);
        // write an integer here (not a Long)
        p.addColumn(TestIncrementsFromClientSide.FAMILY, COLUMN, Bytes.toBytes(5));
        ht.put(p);
        try {
            ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, COLUMN, 5);
            Assert.fail("Should have thrown DoNotRetryIOException");
        } catch (DoNotRetryIOException iox) {
            // success
        }
        Increment inc = new Increment(TestIncrementsFromClientSide.ROW);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, COLUMN, 5);
        try {
            ht.increment(inc);
            Assert.fail("Should have thrown DoNotRetryIOException");
        } catch (DoNotRetryIOException iox) {
            // success
        }
    }

    @Test
    public void testBatchIncrementsWithReturnResultFalse() throws Exception {
        LOG.info("Starting testBatchIncrementsWithReturnResultFalse");
        final TableName tableName = TableName.valueOf(name.getMethodName());
        Table table = TestIncrementsFromClientSide.TEST_UTIL.createTable(tableName, TestIncrementsFromClientSide.FAMILY);
        Increment inc1 = new Increment(Bytes.toBytes("row2"));
        inc1.setReturnResults(false);
        inc1.addColumn(TestIncrementsFromClientSide.FAMILY, Bytes.toBytes("f1"), 1);
        Increment inc2 = new Increment(Bytes.toBytes("row2"));
        inc2.setReturnResults(false);
        inc2.addColumn(TestIncrementsFromClientSide.FAMILY, Bytes.toBytes("f1"), 1);
        List<Increment> incs = new ArrayList<>();
        incs.add(inc1);
        incs.add(inc2);
        Object[] results = new Object[2];
        table.batch(incs, results);
        Assert.assertTrue(((results.length) == 2));
        for (Object r : results) {
            Result result = ((Result) (r));
            Assert.assertTrue(result.isEmpty());
        }
        table.close();
    }

    @Test
    public void testIncrementInvalidArguments() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final TableName TABLENAME = TableName.valueOf(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        final byte[] COLUMN = Bytes.toBytes("column");
        try {
            // try null row
            ht.incrementColumnValue(null, TestIncrementsFromClientSide.FAMILY, COLUMN, 5);
            Assert.fail("Should have thrown IOException");
        } catch (IOException iox) {
            // success
        }
        try {
            // try null family
            ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, null, COLUMN, 5);
            Assert.fail("Should have thrown IOException");
        } catch (IOException iox) {
            // success
        }
        // try null row
        try {
            Increment incNoRow = new Increment(((byte[]) (null)));
            incNoRow.addColumn(TestIncrementsFromClientSide.FAMILY, COLUMN, 5);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iax) {
            // success
        } catch (NullPointerException npe) {
            // success
        }
        // try null family
        try {
            Increment incNoFamily = new Increment(TestIncrementsFromClientSide.ROW);
            incNoFamily.addColumn(null, COLUMN, 5);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException iax) {
            // success
        }
    }

    @Test
    public void testIncrementOutOfOrder() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final TableName TABLENAME = TableName.valueOf(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("B"), Bytes.toBytes("A"), Bytes.toBytes("C") };
        Increment inc = new Increment(TestIncrementsFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 1);
        }
        ht.increment(inc);
        // Verify expected results
        Get get = new Get(TestIncrementsFromClientSide.ROW);
        Result r = ht.get(get);
        Cell[] kvs = r.rawCells();
        Assert.assertEquals(3, kvs.length);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 1);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[1], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[0], 1);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[2], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[2], 1);
        // Now try multiple columns again
        inc = new Increment(TestIncrementsFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 1);
        }
        ht.increment(inc);
        // Verify
        r = ht.get(get);
        kvs = r.rawCells();
        Assert.assertEquals(3, kvs.length);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 2);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[1], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[0], 2);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[2], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[2], 2);
    }

    @Test
    public void testIncrementOnSameColumn() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final byte[] TABLENAME = Bytes.toBytes(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TableName.valueOf(TABLENAME), TestIncrementsFromClientSide.FAMILY);
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("A"), Bytes.toBytes("B"), Bytes.toBytes("C") };
        Increment inc = new Increment(TestIncrementsFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 1);
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 1);
        }
        ht.increment(inc);
        // Verify expected results
        Get get = new Get(TestIncrementsFromClientSide.ROW);
        Result r = ht.get(get);
        Cell[] kvs = r.rawCells();
        Assert.assertEquals(3, kvs.length);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[0], 1);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[1], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 1);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[2], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[2], 1);
        // Now try multiple columns again
        inc = new Increment(TestIncrementsFromClientSide.ROW);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 1);
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 1);
        }
        ht.increment(inc);
        // Verify
        r = ht.get(get);
        kvs = r.rawCells();
        Assert.assertEquals(3, kvs.length);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[0], 2);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[1], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 2);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[2], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[2], 2);
        ht.close();
    }

    @Test
    public void testIncrementIncrZeroAtFirst() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final TableName TABLENAME = TableName.valueOf(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        byte[] col1 = Bytes.toBytes("col1");
        byte[] col2 = Bytes.toBytes("col2");
        byte[] col3 = Bytes.toBytes("col3");
        // Now increment zero at first time incr
        Increment inc = new Increment(TestIncrementsFromClientSide.ROW);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, col1, 0);
        ht.increment(inc);
        // Verify expected results
        Get get = new Get(TestIncrementsFromClientSide.ROW);
        Result r = ht.get(get);
        Cell[] kvs = r.rawCells();
        Assert.assertEquals(1, kvs.length);
        Assert.assertNotNull(kvs[0]);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, col1, 0);
        // Now try multiple columns by different amounts
        inc = new Increment(TestIncrementsFromClientSide.ROW);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, col1, 1);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, col2, 0);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, col3, 2);
        ht.increment(inc);
        // Verify
        get = new Get(TestIncrementsFromClientSide.ROW);
        r = ht.get(get);
        kvs = r.rawCells();
        Assert.assertEquals(3, kvs.length);
        Assert.assertNotNull(kvs[0]);
        Assert.assertNotNull(kvs[1]);
        Assert.assertNotNull(kvs[2]);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, col1, 1);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[1], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, col2, 0);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[2], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, col3, 2);
    }

    @Test
    public void testIncrement() throws Exception {
        LOG.info(("Starting " + (this.name.getMethodName())));
        final TableName TABLENAME = TableName.valueOf(TestIncrementsFromClientSide.filterStringSoTableNameSafe(this.name.getMethodName()));
        Table ht = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        byte[][] ROWS = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("c"), Bytes.toBytes("d"), Bytes.toBytes("e"), Bytes.toBytes("f"), Bytes.toBytes("g"), Bytes.toBytes("h"), Bytes.toBytes("i") };
        byte[][] QUALIFIERS = new byte[][]{ Bytes.toBytes("a"), Bytes.toBytes("b"), Bytes.toBytes("c"), Bytes.toBytes("d"), Bytes.toBytes("e"), Bytes.toBytes("f"), Bytes.toBytes("g"), Bytes.toBytes("h"), Bytes.toBytes("i") };
        // Do some simple single-column increments
        // First with old API
        ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[0], 1);
        ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 2);
        ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[2], 3);
        ht.incrementColumnValue(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[3], 4);
        // Now increment things incremented with old and do some new
        Increment inc = new Increment(TestIncrementsFromClientSide.ROW);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 1);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[3], 1);
        inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[4], 1);
        ht.increment(inc);
        // Verify expected results
        Get get = new Get(TestIncrementsFromClientSide.ROW);
        Result r = ht.get(get);
        Cell[] kvs = r.rawCells();
        Assert.assertEquals(5, kvs.length);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[0], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[0], 1);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[1], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[1], 3);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[2], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[2], 3);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[3], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[3], 5);
        TestIncrementsFromClientSide.assertIncrementKey(kvs[4], TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, QUALIFIERS[4], 1);
        // Now try multiple columns by different amounts
        inc = new Increment(ROWS[0]);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], (i + 1));
        }
        ht.increment(inc);
        // Verify
        get = new Get(ROWS[0]);
        r = ht.get(get);
        kvs = r.rawCells();
        Assert.assertEquals(QUALIFIERS.length, kvs.length);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            TestIncrementsFromClientSide.assertIncrementKey(kvs[i], ROWS[0], TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], (i + 1));
        }
        // Re-increment them
        inc = new Increment(ROWS[0]);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], (i + 1));
        }
        ht.increment(inc);
        // Verify
        r = ht.get(get);
        kvs = r.rawCells();
        Assert.assertEquals(QUALIFIERS.length, kvs.length);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            TestIncrementsFromClientSide.assertIncrementKey(kvs[i], ROWS[0], TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], (2 * (i + 1)));
        }
        // Verify that an Increment of an amount of zero, returns current count; i.e. same as for above
        // test, that is: 2 * (i + 1).
        inc = new Increment(ROWS[0]);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            inc.addColumn(TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], 0);
        }
        ht.increment(inc);
        r = ht.get(get);
        kvs = r.rawCells();
        Assert.assertEquals(QUALIFIERS.length, kvs.length);
        for (int i = 0; i < (QUALIFIERS.length); i++) {
            TestIncrementsFromClientSide.assertIncrementKey(kvs[i], ROWS[0], TestIncrementsFromClientSide.FAMILY, QUALIFIERS[i], (2 * (i + 1)));
        }
    }

    @Test
    public void testIncrementWithCustomTimestamp() throws IOException {
        TableName TABLENAME = TableName.valueOf(name.getMethodName());
        Table table = TestIncrementsFromClientSide.TEST_UTIL.createTable(TABLENAME, TestIncrementsFromClientSide.FAMILY);
        long timestamp = 999;
        Increment increment = new Increment(TestIncrementsFromClientSide.ROW);
        increment.add(CellUtil.createCell(TestIncrementsFromClientSide.ROW, TestIncrementsFromClientSide.FAMILY, TestIncrementsFromClientSide.QUALIFIER, timestamp, KeyValue.Type.Put.getCode(), Bytes.toBytes(100L)));
        Result r = table.increment(increment);
        Assert.assertEquals(1, r.size());
        Assert.assertEquals(timestamp, r.rawCells()[0].getTimestamp());
        r = table.get(new Get(TestIncrementsFromClientSide.ROW));
        Assert.assertEquals(1, r.size());
        Assert.assertEquals(timestamp, r.rawCells()[0].getTimestamp());
        r = table.increment(increment);
        Assert.assertEquals(1, r.size());
        Assert.assertNotEquals(timestamp, r.rawCells()[0].getTimestamp());
        r = table.get(new Get(TestIncrementsFromClientSide.ROW));
        Assert.assertEquals(1, r.size());
        Assert.assertNotEquals(timestamp, r.rawCells()[0].getTimestamp());
    }
}

