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


import java.util.concurrent.ExecutionException;
import java.util.function.LongBinaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.stream.LongStream;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.coprocessor.AsyncAggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.testclassification.CoprocessorTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MediumTests.class, CoprocessorTests.class })
public class TestAsyncAggregationClient {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncAggregationClient.class);

    private static HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static TableName TABLE_NAME = TableName.valueOf("TestAsyncAggregationClient");

    private static byte[] CF = Bytes.toBytes("CF");

    private static byte[] CQ = Bytes.toBytes("CQ");

    private static byte[] CQ2 = Bytes.toBytes("CQ2");

    private static int COUNT = 1000;

    private static AsyncConnection CONN;

    private static AsyncTable<AdvancedScanResultConsumer> TABLE;

    @Test
    public void testMax() throws InterruptedException, ExecutionException {
        Assert.assertEquals(((TestAsyncAggregationClient.COUNT) - 1), AsyncAggregationClient.max(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().longValue());
    }

    @Test
    public void testMin() throws InterruptedException, ExecutionException {
        Assert.assertEquals(0, AsyncAggregationClient.min(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().longValue());
    }

    @Test
    public void testRowCount() throws InterruptedException, ExecutionException {
        Assert.assertEquals(TestAsyncAggregationClient.COUNT, AsyncAggregationClient.rowCount(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().longValue());
    }

    @Test
    public void testSum() throws InterruptedException, ExecutionException {
        Assert.assertEquals((((TestAsyncAggregationClient.COUNT) * ((TestAsyncAggregationClient.COUNT) - 1)) / 2), AsyncAggregationClient.sum(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().longValue());
    }

    private static final double DELTA = 0.001;

    @Test
    public void testAvg() throws InterruptedException, ExecutionException {
        Assert.assertEquals((((TestAsyncAggregationClient.COUNT) - 1) / 2.0), AsyncAggregationClient.avg(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().doubleValue(), TestAsyncAggregationClient.DELTA);
    }

    @Test
    public void testStd() throws InterruptedException, ExecutionException {
        double avgSq = (LongStream.range(0, TestAsyncAggregationClient.COUNT).map(( l) -> l * l).reduce(( l1, l2) -> l1 + l2).getAsLong()) / ((double) (TestAsyncAggregationClient.COUNT));
        double avg = ((TestAsyncAggregationClient.COUNT) - 1) / 2.0;
        double std = Math.sqrt((avgSq - (avg * avg)));
        Assert.assertEquals(std, AsyncAggregationClient.std(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().doubleValue(), TestAsyncAggregationClient.DELTA);
    }

    @Test
    public void testMedian() throws InterruptedException, ExecutionException {
        long halfSum = ((TestAsyncAggregationClient.COUNT) * ((TestAsyncAggregationClient.COUNT) - 1)) / 4;
        long median = 0L;
        long sum = 0L;
        for (int i = 0; i < (TestAsyncAggregationClient.COUNT); i++) {
            sum += i;
            if (sum > halfSum) {
                median = i - 1;
                break;
            }
        }
        Assert.assertEquals(median, AsyncAggregationClient.median(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ)).get().longValue());
    }

    @Test
    public void testMedianWithWeight() throws InterruptedException, ExecutionException {
        long halfSum = (LongStream.range(0, TestAsyncAggregationClient.COUNT).map(( l) -> l * l).reduce(( l1, l2) -> l1 + l2).getAsLong()) / 2;
        long median = 0L;
        long sum = 0L;
        for (int i = 0; i < (TestAsyncAggregationClient.COUNT); i++) {
            sum += i * i;
            if (sum > halfSum) {
                median = i - 1;
                break;
            }
        }
        Assert.assertEquals(median, AsyncAggregationClient.median(TestAsyncAggregationClient.TABLE, new LongColumnInterpreter(), new Scan().addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ).addColumn(TestAsyncAggregationClient.CF, TestAsyncAggregationClient.CQ2)).get().longValue());
    }
}

