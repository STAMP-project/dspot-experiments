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


import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * With filter we may stop at a middle of row and think that we still have more cells for the
 * current row but actually all the remaining cells will be filtered out by the filter. So it will
 * lead to a Result that mayHaveMoreCellsInRow is true but actually there are no cells for the same
 * row. Here we want to test if our limited scan still works.
 */
@Category({ MediumTests.class, ClientTests.class })
public class TestRawAsyncTableLimitedScanWithFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRawAsyncTableLimitedScanWithFilter.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("TestRegionScanner");

    private static final byte[] FAMILY = Bytes.toBytes("cf");

    private static final byte[][] CQS = new byte[][]{ Bytes.toBytes("cq1"), Bytes.toBytes("cq2"), Bytes.toBytes("cq3"), Bytes.toBytes("cq4") };

    private static int ROW_COUNT = 10;

    private static AsyncConnection CONN;

    private static AsyncTable<?> TABLE;

    @Test
    public void testCompleteResult() throws InterruptedException, ExecutionException {
        int limit = 5;
        Scan scan = new Scan().setFilter(new ColumnCountOnRowFilter(2)).setMaxResultSize(1).setLimit(limit);
        List<Result> results = TestRawAsyncTableLimitedScanWithFilter.TABLE.scanAll(scan).get();
        Assert.assertEquals(limit, results.size());
        IntStream.range(0, limit).forEach(( i) -> {
            Result result = results.get(i);
            Assert.assertEquals(i, Bytes.toInt(result.getRow()));
            Assert.assertEquals(2, result.size());
            Assert.assertFalse(result.mayHaveMoreCellsInRow());
            Assert.assertEquals(i, Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[0])));
            Assert.assertEquals((2 * i), Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[1])));
        });
    }

    @Test
    public void testAllowPartial() throws InterruptedException, ExecutionException {
        int limit = 5;
        Scan scan = new Scan().setFilter(new ColumnCountOnRowFilter(2)).setMaxResultSize(1).setAllowPartialResults(true).setLimit(limit);
        List<Result> results = TestRawAsyncTableLimitedScanWithFilter.TABLE.scanAll(scan).get();
        Assert.assertEquals((2 * limit), results.size());
        IntStream.range(0, (2 * limit)).forEach(( i) -> {
            int key = i / 2;
            Result result = results.get(i);
            Assert.assertEquals(key, Bytes.toInt(result.getRow()));
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(result.mayHaveMoreCellsInRow());
            int cqIndex = i % 2;
            Assert.assertEquals((key * (cqIndex + 1)), Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[cqIndex])));
        });
    }

    @Test
    public void testBatchAllowPartial() throws InterruptedException, ExecutionException {
        int limit = 5;
        Scan scan = new Scan().setFilter(new ColumnCountOnRowFilter(3)).setBatch(2).setMaxResultSize(1).setAllowPartialResults(true).setLimit(limit);
        List<Result> results = TestRawAsyncTableLimitedScanWithFilter.TABLE.scanAll(scan).get();
        Assert.assertEquals((3 * limit), results.size());
        IntStream.range(0, (3 * limit)).forEach(( i) -> {
            int key = i / 3;
            Result result = results.get(i);
            Assert.assertEquals(key, Bytes.toInt(result.getRow()));
            Assert.assertEquals(1, result.size());
            Assert.assertTrue(result.mayHaveMoreCellsInRow());
            int cqIndex = i % 3;
            Assert.assertEquals((key * (cqIndex + 1)), Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[cqIndex])));
        });
    }

    @Test
    public void testBatch() throws InterruptedException, ExecutionException {
        int limit = 5;
        Scan scan = new Scan().setFilter(new ColumnCountOnRowFilter(2)).setBatch(2).setMaxResultSize(1).setLimit(limit);
        List<Result> results = TestRawAsyncTableLimitedScanWithFilter.TABLE.scanAll(scan).get();
        Assert.assertEquals(limit, results.size());
        IntStream.range(0, limit).forEach(( i) -> {
            Result result = results.get(i);
            Assert.assertEquals(i, Bytes.toInt(result.getRow()));
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(result.mayHaveMoreCellsInRow());
            Assert.assertEquals(i, Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[0])));
            Assert.assertEquals((2 * i), Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[1])));
        });
    }

    @Test
    public void testBatchAndFilterDiffer() throws InterruptedException, ExecutionException {
        int limit = 5;
        Scan scan = new Scan().setFilter(new ColumnCountOnRowFilter(3)).setBatch(2).setMaxResultSize(1).setLimit(limit);
        List<Result> results = TestRawAsyncTableLimitedScanWithFilter.TABLE.scanAll(scan).get();
        Assert.assertEquals((2 * limit), results.size());
        IntStream.range(0, limit).forEach(( i) -> {
            Result result = results.get((2 * i));
            Assert.assertEquals(i, Bytes.toInt(result.getRow()));
            Assert.assertEquals(2, result.size());
            Assert.assertTrue(result.mayHaveMoreCellsInRow());
            Assert.assertEquals(i, Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[0])));
            Assert.assertEquals((2 * i), Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[1])));
            result = results.get(((2 * i) + 1));
            Assert.assertEquals(i, Bytes.toInt(result.getRow()));
            Assert.assertEquals(1, result.size());
            Assert.assertFalse(result.mayHaveMoreCellsInRow());
            Assert.assertEquals((3 * i), Bytes.toInt(result.getValue(TestRawAsyncTableLimitedScanWithFilter.FAMILY, TestRawAsyncTableLimitedScanWithFilter.CQS[2])));
        });
    }
}

