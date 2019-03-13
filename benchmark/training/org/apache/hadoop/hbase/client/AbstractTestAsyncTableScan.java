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
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.regionserver.NoSuchColumnFamilyException;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractTestAsyncTableScan {
    protected static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    protected static TableName TABLE_NAME = TableName.valueOf("async");

    protected static byte[] FAMILY = Bytes.toBytes("cf");

    protected static byte[] CQ1 = Bytes.toBytes("cq1");

    protected static byte[] CQ2 = Bytes.toBytes("cq2");

    protected static int COUNT = 1000;

    protected static AsyncConnection ASYNC_CONN;

    @Test
    public void testScanAll() throws Exception {
        List<Result> results = doScan(createScan());
        // make sure all scanners are closed at RS side
        AbstractTestAsyncTableScan.TEST_UTIL.getHBaseCluster().getRegionServerThreads().stream().map(( t) -> t.getRegionServer()).forEach(( rs) -> assertEquals(((("The scanner count of " + (rs.getServerName())) + " is ") + (rs.getRSRpcServices().getScannersCount())), 0, rs.getRSRpcServices().getScannersCount()));
        Assert.assertEquals(AbstractTestAsyncTableScan.COUNT, results.size());
        IntStream.range(0, AbstractTestAsyncTableScan.COUNT).forEach(( i) -> {
            Result result = results.get(i);
            Assert.assertEquals(String.format("%03d", i), Bytes.toString(result.getRow()));
            Assert.assertEquals(i, Bytes.toInt(result.getValue(AbstractTestAsyncTableScan.FAMILY, AbstractTestAsyncTableScan.CQ1)));
        });
    }

    @Test
    public void testReversedScanAll() throws Exception {
        List<Result> results = doScan(createScan().setReversed(true));
        Assert.assertEquals(AbstractTestAsyncTableScan.COUNT, results.size());
        IntStream.range(0, AbstractTestAsyncTableScan.COUNT).forEach(( i) -> assertResultEquals(results.get(i), (((AbstractTestAsyncTableScan.COUNT) - i) - 1)));
    }

    @Test
    public void testScanNoStopKey() throws Exception {
        int start = 345;
        List<Result> results = doScan(createScan().withStartRow(Bytes.toBytes(String.format("%03d", start))));
        Assert.assertEquals(((AbstractTestAsyncTableScan.COUNT) - start), results.size());
        IntStream.range(0, ((AbstractTestAsyncTableScan.COUNT) - start)).forEach(( i) -> assertResultEquals(results.get(i), (start + i)));
    }

    @Test
    public void testReverseScanNoStopKey() throws Exception {
        int start = 765;
        List<Result> results = doScan(createScan().withStartRow(Bytes.toBytes(String.format("%03d", start))).setReversed(true));
        Assert.assertEquals((start + 1), results.size());
        IntStream.range(0, (start + 1)).forEach(( i) -> assertResultEquals(results.get(i), (start - i)));
    }

    @Test
    public void testScanWrongColumnFamily() throws Exception {
        try {
            doScan(createScan().addFamily(Bytes.toBytes("WrongColumnFamily")));
        } catch (Exception e) {
            Assert.assertTrue(((e instanceof NoSuchColumnFamilyException) || ((e.getCause()) instanceof NoSuchColumnFamilyException)));
        }
    }

    @Test
    public void testScanWithStartKeyAndStopKey() throws Exception {
        testScan(1, true, 998, false, (-1));// from first region to last region

        testScan(123, true, 345, true, (-1));
        testScan(234, true, 456, false, (-1));
        testScan(345, false, 567, true, (-1));
        testScan(456, false, 678, false, (-1));
    }

    @Test
    public void testReversedScanWithStartKeyAndStopKey() throws Exception {
        testReversedScan(998, true, 1, false, (-1));// from last region to first region

        testReversedScan(543, true, 321, true, (-1));
        testReversedScan(654, true, 432, false, (-1));
        testReversedScan(765, false, 543, true, (-1));
        testReversedScan(876, false, 654, false, (-1));
    }

    @Test
    public void testScanAtRegionBoundary() throws Exception {
        testScan(222, true, 333, true, (-1));
        testScan(333, true, 444, false, (-1));
        testScan(444, false, 555, true, (-1));
        testScan(555, false, 666, false, (-1));
    }

    @Test
    public void testReversedScanAtRegionBoundary() throws Exception {
        testReversedScan(333, true, 222, true, (-1));
        testReversedScan(444, true, 333, false, (-1));
        testReversedScan(555, false, 444, true, (-1));
        testReversedScan(666, false, 555, false, (-1));
    }

    @Test
    public void testScanWithLimit() throws Exception {
        testScan(1, true, 998, false, 900);// from first region to last region

        testScan(123, true, 234, true, 100);
        testScan(234, true, 456, false, 100);
        testScan(345, false, 567, true, 100);
        testScan(456, false, 678, false, 100);
    }

    @Test
    public void testScanWithLimitGreaterThanActualCount() throws Exception {
        testScan(1, true, 998, false, 1000);// from first region to last region

        testScan(123, true, 345, true, 200);
        testScan(234, true, 456, false, 200);
        testScan(345, false, 567, true, 200);
        testScan(456, false, 678, false, 200);
    }

    @Test
    public void testReversedScanWithLimit() throws Exception {
        testReversedScan(998, true, 1, false, 900);// from last region to first region

        testReversedScan(543, true, 321, true, 100);
        testReversedScan(654, true, 432, false, 100);
        testReversedScan(765, false, 543, true, 100);
        testReversedScan(876, false, 654, false, 100);
    }

    @Test
    public void testReversedScanWithLimitGreaterThanActualCount() throws Exception {
        testReversedScan(998, true, 1, false, 1000);// from last region to first region

        testReversedScan(543, true, 321, true, 200);
        testReversedScan(654, true, 432, false, 200);
        testReversedScan(765, false, 543, true, 200);
        testReversedScan(876, false, 654, false, 200);
    }
}

