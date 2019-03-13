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
package org.apache.hadoop.hbase.master;


import RegionState.State.OFFLINE;
import java.util.AbstractMap;
import java.util.List;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot;
import org.apache.hadoop.hbase.quotas.SpaceQuotaSnapshot.SpaceQuotaStatus;
import org.apache.hadoop.hbase.quotas.SpaceViolationPolicy;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.PairOfSameType;
import org.apache.hadoop.hbase.util.Threads;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestMasterMetricsWrapper {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterMetricsWrapper.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestMasterMetricsWrapper.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int NUM_RS = 4;

    @Test
    public void testInfo() {
        HMaster master = TestMasterMetricsWrapper.TEST_UTIL.getHBaseCluster().getMaster();
        MetricsMasterWrapperImpl info = new MetricsMasterWrapperImpl(master);
        Assert.assertEquals(master.getSplitPlanCount(), info.getSplitPlanCount(), 0);
        Assert.assertEquals(master.getMergePlanCount(), info.getMergePlanCount(), 0);
        Assert.assertEquals(master.getAverageLoad(), info.getAverageLoad(), 0);
        Assert.assertEquals(master.getClusterId(), info.getClusterId());
        Assert.assertEquals(master.getMasterActiveTime(), info.getActiveTime());
        Assert.assertEquals(master.getMasterStartTime(), info.getStartTime());
        Assert.assertEquals(master.getMasterCoprocessors().length, info.getCoprocessors().length);
        Assert.assertEquals(master.getServerManager().getOnlineServersList().size(), info.getNumRegionServers());
        int regionServerCount = (TestMasterMetricsWrapper.NUM_RS) + (LoadBalancer.isTablesOnMaster(TestMasterMetricsWrapper.TEST_UTIL.getConfiguration()) ? 1 : 0);
        Assert.assertEquals(regionServerCount, info.getNumRegionServers());
        String zkServers = info.getZookeeperQuorum();
        Assert.assertEquals(zkServers.split(",").length, getZkCluster().getZooKeeperServerNum());
        final int index = 3;
        TestMasterMetricsWrapper.LOG.info(("Stopping " + (TestMasterMetricsWrapper.TEST_UTIL.getMiniHBaseCluster().getRegionServer(index))));
        TestMasterMetricsWrapper.TEST_UTIL.getMiniHBaseCluster().stopRegionServer(index, false);
        TestMasterMetricsWrapper.TEST_UTIL.getMiniHBaseCluster().waitOnRegionServer(index);
        // We stopped the regionserver but could take a while for the master to notice it so hang here
        // until it does... then move forward to see if metrics wrapper notices.
        while ((TestMasterMetricsWrapper.TEST_UTIL.getHBaseCluster().getMaster().getServerManager().getOnlineServers().size()) == regionServerCount) {
            Threads.sleep(10);
        } 
        Assert.assertEquals((regionServerCount - 1), info.getNumRegionServers());
        Assert.assertEquals(1, info.getNumDeadRegionServers());
        Assert.assertEquals(1, info.getNumWALFiles());
    }

    @Test
    public void testQuotaSnapshotConversion() {
        MetricsMasterWrapperImpl info = new MetricsMasterWrapperImpl(TestMasterMetricsWrapper.TEST_UTIL.getHBaseCluster().getMaster());
        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<Long, Long>(1024L, 2048L), info.convertSnapshot(new SpaceQuotaSnapshot(SpaceQuotaStatus.notInViolation(), 1024L, 2048L)));
        Assert.assertEquals(new AbstractMap.SimpleImmutableEntry<Long, Long>(4096L, 2048L), info.convertSnapshot(new SpaceQuotaSnapshot(new SpaceQuotaStatus(SpaceViolationPolicy.NO_INSERTS), 4096L, 2048L)));
    }

    /**
     * tests online and offline region number
     */
    @Test(timeout = 30000)
    public void testOfflineRegion() throws Exception {
        HMaster master = TestMasterMetricsWrapper.TEST_UTIL.getHBaseCluster().getMaster();
        MetricsMasterWrapperImpl info = new MetricsMasterWrapperImpl(master);
        TableName table = TableName.valueOf("testRegionNumber");
        try {
            RegionInfo hri;
            HTableDescriptor desc = new HTableDescriptor(table);
            byte[] FAMILY = Bytes.toBytes("FAMILY");
            desc.addFamily(new HColumnDescriptor(FAMILY));
            TestMasterMetricsWrapper.TEST_UTIL.getHBaseAdmin().createTable(desc, Bytes.toBytes("A"), Bytes.toBytes("Z"), 5);
            // wait till the table is assigned
            long timeoutTime = (System.currentTimeMillis()) + 1000;
            while (true) {
                List<RegionInfo> regions = master.getAssignmentManager().getRegionStates().getRegionsOfTable(table);
                if ((regions.size()) > 3) {
                    hri = regions.get(2);
                    break;
                }
                long now = System.currentTimeMillis();
                if (now > timeoutTime) {
                    Assert.fail("Could not find an online region");
                }
                Thread.sleep(10);
            } 
            PairOfSameType<Integer> regionNumberPair = info.getRegionCounts();
            Assert.assertEquals(5, regionNumberPair.getFirst().intValue());
            Assert.assertEquals(0, regionNumberPair.getSecond().intValue());
            TestMasterMetricsWrapper.TEST_UTIL.getHBaseAdmin().offline(hri.getRegionName());
            timeoutTime = (System.currentTimeMillis()) + 800;
            RegionStates regionStates = master.getAssignmentManager().getRegionStates();
            while (true) {
                if (regionStates.getRegionByStateOfTable(table).get(OFFLINE).contains(hri)) {
                    break;
                }
                long now = System.currentTimeMillis();
                if (now > timeoutTime) {
                    Assert.fail("Failed to offline the region in time");
                    break;
                }
                Thread.sleep(10);
            } 
            regionNumberPair = info.getRegionCounts();
            Assert.assertEquals(4, regionNumberPair.getFirst().intValue());
            Assert.assertEquals(1, regionNumberPair.getSecond().intValue());
        } finally {
            TestMasterMetricsWrapper.TEST_UTIL.deleteTable(table);
        }
    }
}

