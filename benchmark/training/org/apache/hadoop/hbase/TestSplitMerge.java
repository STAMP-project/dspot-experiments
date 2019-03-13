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


import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hbase.client.AsyncConnection;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.testclassification.MiscTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MiscTests.class, MediumTests.class })
public class TestSplitMerge {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestSplitMerge.class);

    private static final HBaseTestingUtility UTIL = new HBaseTestingUtility();

    @Test
    public void test() throws Exception {
        TableName tableName = TableName.valueOf("SplitMerge");
        byte[] family = Bytes.toBytes("CF");
        TableDescriptor td = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(ColumnFamilyDescriptorBuilder.of(family)).build();
        TestSplitMerge.UTIL.getAdmin().createTable(td, new byte[][]{ Bytes.toBytes(1) });
        TestSplitMerge.UTIL.waitTableAvailable(tableName);
        TestSplitMerge.UTIL.getAdmin().split(tableName, Bytes.toBytes(2));
        waitFor(30000, new org.apache.hadoop.hbase.Waiter.ExplainingPredicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return (TestSplitMerge.UTIL.getMiniHBaseCluster().getRegions(tableName).size()) == 3;
            }

            @Override
            public String explainFailure() throws Exception {
                return "Split has not finished yet";
            }
        });
        RegionInfo regionA = null;
        RegionInfo regionB = null;
        for (RegionInfo region : TestSplitMerge.UTIL.getAdmin().getRegions(tableName)) {
            if ((region.getStartKey().length) == 0) {
                regionA = region;
            } else
                if (Bytes.equals(region.getStartKey(), Bytes.toBytes(1))) {
                    regionB = region;
                }

        }
        Assert.assertNotNull(regionA);
        Assert.assertNotNull(regionB);
        TestSplitMerge.UTIL.getAdmin().mergeRegionsAsync(regionA.getRegionName(), regionB.getRegionName(), false).get(30, TimeUnit.SECONDS);
        Assert.assertEquals(2, TestSplitMerge.UTIL.getAdmin().getRegions(tableName).size());
        ServerName expected = TestSplitMerge.UTIL.getMiniHBaseCluster().getRegionServer(0).getServerName();
        Assert.assertEquals(expected, TestSplitMerge.UTIL.getConnection().getRegionLocator(tableName).getRegionLocation(Bytes.toBytes(1), true).getServerName());
        try (AsyncConnection asyncConn = ConnectionFactory.createAsyncConnection(TestSplitMerge.UTIL.getConfiguration()).get()) {
            Assert.assertEquals(expected, asyncConn.getRegionLocator(tableName).getRegionLocation(Bytes.toBytes(1), true).get().getServerName());
        }
    }
}

