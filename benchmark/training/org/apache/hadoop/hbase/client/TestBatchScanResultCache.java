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
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ SmallTests.class, ClientTests.class })
public class TestBatchScanResultCache {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBatchScanResultCache.class);

    private static byte[] CF = Bytes.toBytes("cf");

    private BatchScanResultCache resultCache;

    @Test
    public void test() throws IOException {
        Assert.assertSame(EMPTY_RESULT_ARRAY, resultCache.addAndGet(EMPTY_RESULT_ARRAY, false));
        Assert.assertSame(EMPTY_RESULT_ARRAY, resultCache.addAndGet(EMPTY_RESULT_ARRAY, true));
        Cell[] cells1 = TestBatchScanResultCache.createCells(TestBatchScanResultCache.CF, 1, 10);
        Cell[] cells2 = TestBatchScanResultCache.createCells(TestBatchScanResultCache.CF, 2, 10);
        Cell[] cells3 = TestBatchScanResultCache.createCells(TestBatchScanResultCache.CF, 3, 10);
        Assert.assertEquals(0, resultCache.addAndGet(new Result[]{ Result.create(Arrays.copyOf(cells1, 3), null, false, true) }, false).length);
        Result[] results = resultCache.addAndGet(new Result[]{ Result.create(Arrays.copyOfRange(cells1, 3, 7), null, false, true), Result.create(Arrays.copyOfRange(cells1, 7, 10), null, false, true) }, false);
        Assert.assertEquals(2, results.length);
        assertResultEquals(results[0], 1, 0, 4);
        assertResultEquals(results[1], 1, 4, 8);
        results = resultCache.addAndGet(EMPTY_RESULT_ARRAY, false);
        Assert.assertEquals(1, results.length);
        assertResultEquals(results[0], 1, 8, 10);
        results = resultCache.addAndGet(new Result[]{ Result.create(Arrays.copyOfRange(cells2, 0, 4), null, false, true), Result.create(Arrays.copyOfRange(cells2, 4, 8), null, false, true), Result.create(Arrays.copyOfRange(cells2, 8, 10), null, false, true), Result.create(Arrays.copyOfRange(cells3, 0, 4), null, false, true), Result.create(Arrays.copyOfRange(cells3, 4, 8), null, false, true), Result.create(Arrays.copyOfRange(cells3, 8, 10), null, false, false) }, false);
        Assert.assertEquals(6, results.length);
        assertResultEquals(results[0], 2, 0, 4);
        assertResultEquals(results[1], 2, 4, 8);
        assertResultEquals(results[2], 2, 8, 10);
        assertResultEquals(results[3], 3, 0, 4);
        assertResultEquals(results[4], 3, 4, 8);
        assertResultEquals(results[5], 3, 8, 10);
    }
}

