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


import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.metrics.ScanMetrics;
import org.apache.hadoop.hbase.testclassification.ClientTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@Category({ MediumTests.class, ClientTests.class })
public class TestAsyncTableScanMetrics {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestAsyncTableScanMetrics.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName TABLE_NAME = TableName.valueOf("ScanMetrics");

    private static final byte[] CF = Bytes.toBytes("cf");

    private static final byte[] CQ = Bytes.toBytes("cq");

    private static final byte[] VALUE = Bytes.toBytes("value");

    private static AsyncConnection CONN;

    private static int NUM_REGIONS;

    @FunctionalInterface
    private interface ScanWithMetrics {
        Pair<List<Result>, ScanMetrics> scan(Scan scan) throws Exception;
    }

    @Parameterized.Parameter(0)
    public String methodName;

    @Parameterized.Parameter(1)
    public TestAsyncTableScanMetrics.ScanWithMetrics method;

    @Test
    public void testNoScanMetrics() throws Exception {
        Pair<List<Result>, ScanMetrics> pair = method.scan(new Scan());
        Assert.assertEquals(3, pair.getFirst().size());
        Assert.assertNull(pair.getSecond());
    }

    @Test
    public void testScanMetrics() throws Exception {
        Pair<List<Result>, ScanMetrics> pair = method.scan(new Scan().setScanMetricsEnabled(true));
        List<Result> results = pair.getFirst();
        Assert.assertEquals(3, results.size());
        long bytes = results.stream().flatMap(( r) -> Arrays.asList(r.rawCells()).stream()).mapToLong(( c) -> PrivateCellUtil.estimatedSerializedSizeOf(c)).sum();
        ScanMetrics scanMetrics = pair.getSecond();
        Assert.assertEquals(TestAsyncTableScanMetrics.NUM_REGIONS, scanMetrics.countOfRegions.get());
        Assert.assertEquals(bytes, scanMetrics.countOfBytesInResults.get());
        Assert.assertEquals(TestAsyncTableScanMetrics.NUM_REGIONS, scanMetrics.countOfRPCcalls.get());
        // also assert a server side metric to ensure that we have published them into the client side
        // metrics.
        Assert.assertEquals(3, scanMetrics.countOfRowsScanned.get());
    }
}

