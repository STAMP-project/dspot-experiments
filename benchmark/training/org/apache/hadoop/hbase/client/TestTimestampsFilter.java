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


import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Run tests related to {@link TimestampsFilter} using HBase client APIs.
 * Sets up the HBase mini cluster once at start. Each creates a table
 * named for the method and does its stuff against that.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestTimestampsFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestTimestampsFilter.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestTimestampsFilter.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    @Rule
    public TestName name = new TestName();

    /**
     * Test from client side for TimestampsFilter.
     *
     * The TimestampsFilter provides the ability to request cells (KeyValues)
     * whose timestamp/version is in the specified list of timestamps/version.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTimestampsFilter() throws Exception {
        final byte[] TABLE = Bytes.toBytes(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        Cell[] kvs;
        // create table; set versions to max...
        Table ht = TestTimestampsFilter.TEST_UTIL.createTable(TableName.valueOf(TABLE), FAMILIES, Integer.MAX_VALUE);
        for (int rowIdx = 0; rowIdx < 5; rowIdx++) {
            for (int colIdx = 0; colIdx < 5; colIdx++) {
                // insert versions 201..300
                putNVersions(ht, FAMILY, rowIdx, colIdx, 201, 300);
                // insert versions 1..100
                putNVersions(ht, FAMILY, rowIdx, colIdx, 1, 100);
            }
        }
        // do some verification before flush
        verifyInsertedValues(ht, FAMILY);
        TestTimestampsFilter.TEST_UTIL.flush();
        // do some verification after flush
        verifyInsertedValues(ht, FAMILY);
        // Insert some more versions after flush. These should be in memstore.
        // After this we should have data in both memstore & HFiles.
        for (int rowIdx = 0; rowIdx < 5; rowIdx++) {
            for (int colIdx = 0; colIdx < 5; colIdx++) {
                putNVersions(ht, FAMILY, rowIdx, colIdx, 301, 400);
                putNVersions(ht, FAMILY, rowIdx, colIdx, 101, 200);
            }
        }
        for (int rowIdx = 0; rowIdx < 5; rowIdx++) {
            for (int colIdx = 0; colIdx < 5; colIdx++) {
                kvs = getNVersions(ht, FAMILY, rowIdx, colIdx, Arrays.asList(505L, 5L, 105L, 305L, 205L));
                Assert.assertEquals(4, kvs.length);
                checkOneCell(kvs[0], FAMILY, rowIdx, colIdx, 305);
                checkOneCell(kvs[1], FAMILY, rowIdx, colIdx, 205);
                checkOneCell(kvs[2], FAMILY, rowIdx, colIdx, 105);
                checkOneCell(kvs[3], FAMILY, rowIdx, colIdx, 5);
            }
        }
        // Request an empty list of versions using the Timestamps filter;
        // Should return none.
        kvs = getNVersions(ht, FAMILY, 2, 2, new ArrayList());
        Assert.assertEquals(0, (kvs == null ? 0 : kvs.length));
        // 
        // Test the filter using a Scan operation
        // Scan rows 0..4. For each row, get all its columns, but only
        // those versions of the columns with the specified timestamps.
        Result[] results = scanNVersions(ht, FAMILY, 0, 4, Arrays.asList(6L, 106L, 306L));
        Assert.assertEquals("# of rows returned from scan", 5, results.length);
        for (int rowIdx = 0; rowIdx < 5; rowIdx++) {
            kvs = results[rowIdx].rawCells();
            // each row should have 5 columns.
            // And we have requested 3 versions for each.
            Assert.assertEquals(("Number of KeyValues in result for row:" + rowIdx), (3 * 5), kvs.length);
            for (int colIdx = 0; colIdx < 5; colIdx++) {
                int offset = colIdx * 3;
                checkOneCell(kvs[(offset + 0)], FAMILY, rowIdx, colIdx, 306);
                checkOneCell(kvs[(offset + 1)], FAMILY, rowIdx, colIdx, 106);
                checkOneCell(kvs[(offset + 2)], FAMILY, rowIdx, colIdx, 6);
            }
        }
        ht.close();
    }

    @Test
    public void testMultiColumns() throws Exception {
        final byte[] TABLE = Bytes.toBytes(name.getMethodName());
        byte[] FAMILY = Bytes.toBytes("event_log");
        byte[][] FAMILIES = new byte[][]{ FAMILY };
        // create table; set versions to max...
        Table ht = TestTimestampsFilter.TEST_UTIL.createTable(TableName.valueOf(TABLE), FAMILIES, Integer.MAX_VALUE);
        Put p = new Put(Bytes.toBytes("row"));
        p.addColumn(FAMILY, Bytes.toBytes("column0"), 3L, Bytes.toBytes("value0-3"));
        p.addColumn(FAMILY, Bytes.toBytes("column1"), 3L, Bytes.toBytes("value1-3"));
        p.addColumn(FAMILY, Bytes.toBytes("column2"), 1L, Bytes.toBytes("value2-1"));
        p.addColumn(FAMILY, Bytes.toBytes("column2"), 2L, Bytes.toBytes("value2-2"));
        p.addColumn(FAMILY, Bytes.toBytes("column2"), 3L, Bytes.toBytes("value2-3"));
        p.addColumn(FAMILY, Bytes.toBytes("column3"), 2L, Bytes.toBytes("value3-2"));
        p.addColumn(FAMILY, Bytes.toBytes("column4"), 1L, Bytes.toBytes("value4-1"));
        p.addColumn(FAMILY, Bytes.toBytes("column4"), 2L, Bytes.toBytes("value4-2"));
        p.addColumn(FAMILY, Bytes.toBytes("column4"), 3L, Bytes.toBytes("value4-3"));
        ht.put(p);
        ArrayList<Long> timestamps = new ArrayList<>();
        timestamps.add(new Long(3));
        TimestampsFilter filter = new TimestampsFilter(timestamps);
        Get g = new Get(Bytes.toBytes("row"));
        g.setFilter(filter);
        g.setMaxVersions();
        g.addColumn(FAMILY, Bytes.toBytes("column2"));
        g.addColumn(FAMILY, Bytes.toBytes("column4"));
        Result result = ht.get(g);
        for (Cell kv : result.listCells()) {
            System.out.println(((((("found row " + (Bytes.toString(CellUtil.cloneRow(kv)))) + ", column ") + (Bytes.toString(CellUtil.cloneQualifier(kv)))) + ", value ") + (Bytes.toString(CellUtil.cloneValue(kv)))));
        }
        Assert.assertEquals(2, result.listCells().size());
        Assert.assertTrue(CellUtil.matchingValue(result.listCells().get(0), Bytes.toBytes("value2-3")));
        Assert.assertTrue(CellUtil.matchingValue(result.listCells().get(1), Bytes.toBytes("value4-3")));
        ht.close();
    }

    /**
     * Test TimestampsFilter in the presence of version deletes.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWithVersionDeletes() throws Exception {
        // first test from memstore (without flushing).
        testWithVersionDeletes(false);
        // run same test against HFiles (by forcing a flush).
        testWithVersionDeletes(true);
    }
}

