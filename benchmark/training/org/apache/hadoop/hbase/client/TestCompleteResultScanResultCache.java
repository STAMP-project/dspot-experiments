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


import ScanResultCache.EMPTY_RESULT_ARRAY;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SmallTests.class, ClientTests.class })
public class TestCompleteResultScanResultCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestCompleteResultScanResultCache.class);

    private static byte[] CF = Bytes.toBytes("cf");

    private static byte[] CQ1 = Bytes.toBytes("cq1");

    private static byte[] CQ2 = Bytes.toBytes("cq2");

    private static byte[] CQ3 = Bytes.toBytes("cq3");

    private CompleteScanResultCache resultCache;

    @Test
    public void testNoPartial() throws IOException {
        Assert.assertSame(EMPTY_RESULT_ARRAY, resultCache.addAndGet(EMPTY_RESULT_ARRAY, false));
        Assert.assertSame(EMPTY_RESULT_ARRAY, resultCache.addAndGet(EMPTY_RESULT_ARRAY, true));
        int count = 10;
        Result[] results = new Result[count];
        for (int i = 0; i < count; i++) {
            results[i] = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(i, TestCompleteResultScanResultCache.CQ1)));
        }
        Assert.assertSame(results, resultCache.addAndGet(results, false));
    }

    @Test
    public void testCombine1() throws IOException {
        Result previousResult = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(0, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result result1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result result2 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ2)), null, false, true);
        Result result3 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ3)), null, false, true);
        Result[] results = resultCache.addAndGet(new Result[]{ previousResult, result1 }, false);
        Assert.assertEquals(1, results.length);
        Assert.assertSame(previousResult, results[0]);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result2 }, false).length);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result3 }, false).length);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[0], true).length);
        results = resultCache.addAndGet(new Result[0], false);
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(3, results[0].rawCells().length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ2)));
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ3)));
    }

    @Test
    public void testCombine2() throws IOException {
        Result result1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result result2 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ2)), null, false, true);
        Result result3 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ3)), null, false, true);
        Result nextResult1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(2, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result nextToNextResult1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(3, TestCompleteResultScanResultCache.CQ2)), null, false, false);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result1 }, false).length);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result2 }, false).length);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result3 }, false).length);
        Result[] results = resultCache.addAndGet(new Result[]{ nextResult1 }, false);
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(3, results[0].rawCells().length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ2)));
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ3)));
        results = resultCache.addAndGet(new Result[]{ nextToNextResult1 }, false);
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(2, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(1, results[0].rawCells().length);
        Assert.assertEquals(2, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        Assert.assertEquals(3, Bytes.toInt(results[1].getRow()));
        Assert.assertEquals(1, results[1].rawCells().length);
        Assert.assertEquals(3, Bytes.toInt(results[1].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ2)));
    }

    @Test
    public void testCombine3() throws IOException {
        Result result1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result result2 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ2)), null, false, true);
        Result nextResult1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(2, TestCompleteResultScanResultCache.CQ1)), null, false, false);
        Result nextToNextResult1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(3, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result1 }, false).length);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result2 }, false).length);
        Result[] results = resultCache.addAndGet(new Result[]{ nextResult1, nextToNextResult1 }, false);
        Assert.assertEquals(2, results.length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(2, results[0].rawCells().length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ2)));
        Assert.assertEquals(2, Bytes.toInt(results[1].getRow()));
        Assert.assertEquals(1, results[1].rawCells().length);
        Assert.assertEquals(2, Bytes.toInt(results[1].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        results = resultCache.addAndGet(new Result[0], false);
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(3, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(1, results[0].rawCells().length);
        Assert.assertEquals(3, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
    }

    @Test
    public void testCombine4() throws IOException {
        Result result1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result result2 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(1, TestCompleteResultScanResultCache.CQ2)), null, false, false);
        Result nextResult1 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(2, TestCompleteResultScanResultCache.CQ1)), null, false, true);
        Result nextResult2 = Result.create(Arrays.asList(TestCompleteResultScanResultCache.createCell(2, TestCompleteResultScanResultCache.CQ2)), null, false, false);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ result1 }, false).length);
        Result[] results = resultCache.addAndGet(new Result[]{ result2, nextResult1 }, false);
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(2, results[0].rawCells().length);
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        Assert.assertEquals(1, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ2)));
        results = resultCache.addAndGet(new Result[]{ nextResult2 }, false);
        Assert.assertEquals(1, results.length);
        Assert.assertEquals(2, Bytes.toInt(results[0].getRow()));
        Assert.assertEquals(2, results[0].rawCells().length);
        Assert.assertEquals(2, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ1)));
        Assert.assertEquals(2, Bytes.toInt(results[0].getValue(TestCompleteResultScanResultCache.CF, TestCompleteResultScanResultCache.CQ2)));
    }
}

