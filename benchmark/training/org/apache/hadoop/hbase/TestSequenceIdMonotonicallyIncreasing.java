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
package org.apache.hadoop.hbase;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionLocator;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.regionserver.HRegion;
import org.apache.hadoop.hbase.regionserver.HRegionServer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.RegionServerTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Testcase for HBASE-20066
 */
@Category({ RegionServerTests.class, LargeTests.class })
public class TestSequenceIdMonotonicallyIncreasing {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSequenceIdMonotonicallyIncreasing.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    private static final TableName NAME = TableName.valueOf("test");

    private static final byte[] CF = Bytes.toBytes("cf");

    private static final byte[] CQ = Bytes.toBytes("cq");

    @Test
    public void testSplit() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        try (Table table = createTable(false)) {
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(0)).addColumn(TestSequenceIdMonotonicallyIncreasing.CF, TestSequenceIdMonotonicallyIncreasing.CQ, Bytes.toBytes(0)));
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(1)).addColumn(TestSequenceIdMonotonicallyIncreasing.CF, TestSequenceIdMonotonicallyIncreasing.CQ, Bytes.toBytes(0)));
        }
        TestSequenceIdMonotonicallyIncreasing.UTIL.flush(TestSequenceIdMonotonicallyIncreasing.NAME);
        HRegionServer rs = TestSequenceIdMonotonicallyIncreasing.UTIL.getRSForFirstRegionInTable(TestSequenceIdMonotonicallyIncreasing.NAME);
        RegionInfo region = TestSequenceIdMonotonicallyIncreasing.UTIL.getMiniHBaseCluster().getRegions(TestSequenceIdMonotonicallyIncreasing.NAME).get(0).getRegionInfo();
        TestSequenceIdMonotonicallyIncreasing.UTIL.getAdmin().splitRegionAsync(region.getRegionName(), Bytes.toBytes(1)).get(1, TimeUnit.MINUTES);
        long maxSeqId = getMaxSeqId(rs, region);
        RegionLocator locator = TestSequenceIdMonotonicallyIncreasing.UTIL.getConnection().getRegionLocator(TestSequenceIdMonotonicallyIncreasing.NAME);
        HRegionLocation locA = locator.getRegionLocation(Bytes.toBytes(0), true);
        HRegionLocation locB = locator.getRegionLocation(Bytes.toBytes(1), true);
        Assert.assertEquals((maxSeqId + 1), locA.getSeqNum());
        Assert.assertEquals((maxSeqId + 1), locB.getSeqNum());
    }

    @Test
    public void testMerge() throws IOException, InterruptedException, ExecutionException, TimeoutException {
        try (Table table = createTable(true)) {
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(0)).addColumn(TestSequenceIdMonotonicallyIncreasing.CF, TestSequenceIdMonotonicallyIncreasing.CQ, Bytes.toBytes(0)));
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(1)).addColumn(TestSequenceIdMonotonicallyIncreasing.CF, TestSequenceIdMonotonicallyIncreasing.CQ, Bytes.toBytes(0)));
            table.put(new org.apache.hadoop.hbase.client.Put(Bytes.toBytes(2)).addColumn(TestSequenceIdMonotonicallyIncreasing.CF, TestSequenceIdMonotonicallyIncreasing.CQ, Bytes.toBytes(0)));
        }
        TestSequenceIdMonotonicallyIncreasing.UTIL.flush(TestSequenceIdMonotonicallyIncreasing.NAME);
        MiniHBaseCluster cluster = TestSequenceIdMonotonicallyIncreasing.UTIL.getMiniHBaseCluster();
        List<HRegion> regions = cluster.getRegions(TestSequenceIdMonotonicallyIncreasing.NAME);
        HRegion regionA = regions.get(0);
        HRegion regionB = regions.get(1);
        HRegionServer rsA = cluster.getRegionServer(cluster.getServerWith(regionA.getRegionInfo().getRegionName()));
        HRegionServer rsB = cluster.getRegionServer(cluster.getServerWith(regionB.getRegionInfo().getRegionName()));
        TestSequenceIdMonotonicallyIncreasing.UTIL.getAdmin().mergeRegionsAsync(regionA.getRegionInfo().getRegionName(), regionB.getRegionInfo().getRegionName(), false).get(1, TimeUnit.MINUTES);
        long maxSeqIdA = getMaxSeqId(rsA, regionA.getRegionInfo());
        long maxSeqIdB = getMaxSeqId(rsB, regionB.getRegionInfo());
        HRegionLocation loc = TestSequenceIdMonotonicallyIncreasing.UTIL.getConnection().getRegionLocator(TestSequenceIdMonotonicallyIncreasing.NAME).getRegionLocation(Bytes.toBytes(0), true);
        Assert.assertEquals(((Math.max(maxSeqIdA, maxSeqIdB)) + 1), loc.getSeqNum());
    }
}

