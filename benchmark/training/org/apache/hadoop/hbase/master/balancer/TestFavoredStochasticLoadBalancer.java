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
package org.apache.hadoop.hbase.master.balancer;


import FavoredNodeAssignmentHelper.FAVORED_NODES_NUM;
import HConstants.CATALOG_FAMILY;
import JVMClusterUtil.RegionServerThread;
import Option.LIVE_SERVERS;
import TableName.META_TABLE_NAME;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.favored.FavoredNodeAssignmentHelper;
import org.apache.hadoop.hbase.favored.FavoredNodesManager;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.LoadBalancer;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.JVMClusterUtil;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// Disabled
@Ignore
@Category(MediumTests.class)
public class TestFavoredStochasticLoadBalancer extends BalancerTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFavoredStochasticLoadBalancer.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFavoredStochasticLoadBalancer.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int SLAVES = 8;

    private static final int REGION_NUM = (TestFavoredStochasticLoadBalancer.SLAVES) * 3;

    private Admin admin;

    private HMaster master;

    private MiniHBaseCluster cluster;

    @Test
    public void testBasicBalance() throws Exception {
        TableName tableName = TableName.valueOf("testBasicBalance");
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new org.apache.hadoop.hbase.HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), TestFavoredStochasticLoadBalancer.REGION_NUM);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitTableAvailable(tableName);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.loadTable(admin.getConnection().getTable(tableName), CATALOG_FAMILY);
        admin.flush(tableName);
        compactTable(tableName);
        JVMClusterUtil.RegionServerThread rs1 = cluster.startRegionServerAndWait(10000);
        JVMClusterUtil.RegionServerThread rs2 = cluster.startRegionServerAndWait(10000);
        // Now try to run balance, and verify no regions are moved to the 2 region servers recently
        // started.
        admin.setBalancerRunning(true, true);
        Assert.assertTrue("Balancer did not run", admin.balancer());
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitUntilNoRegionsInTransition(120000);
        List<RegionInfo> hris = admin.getRegions(rs1.getRegionServer().getServerName());
        for (RegionInfo hri : hris) {
            Assert.assertFalse(("New RS contains regions belonging to table: " + tableName), hri.getTable().equals(tableName));
        }
        hris = admin.getRegions(rs2.getRegionServer().getServerName());
        for (RegionInfo hri : hris) {
            Assert.assertFalse(("New RS contains regions belonging to table: " + tableName), hri.getTable().equals(tableName));
        }
    }

    @Test
    public void testRoundRobinAssignment() throws Exception {
        TableName tableName = TableName.valueOf("testRoundRobinAssignment");
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new org.apache.hadoop.hbase.HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), TestFavoredStochasticLoadBalancer.REGION_NUM);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitTableAvailable(tableName);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.loadTable(admin.getConnection().getTable(tableName), CATALOG_FAMILY);
        admin.flush(tableName);
        LoadBalancer balancer = master.getLoadBalancer();
        List<RegionInfo> regions = admin.getRegions(tableName);
        regions.addAll(admin.getTableRegions(META_TABLE_NAME));
        List<ServerName> servers = Lists.newArrayList(admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet());
        Map<ServerName, List<RegionInfo>> map = balancer.roundRobinAssignment(regions, servers);
        for (List<RegionInfo> regionInfos : map.values()) {
            regions.removeAll(regionInfos);
        }
        Assert.assertEquals("No region should be missed by balancer", 0, regions.size());
    }

    @Test
    public void testBasicRegionPlacementAndReplicaLoad() throws Exception {
        String tableName = "testBasicRegionPlacement";
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        desc.addFamily(new org.apache.hadoop.hbase.HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), TestFavoredStochasticLoadBalancer.REGION_NUM);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitTableAvailable(desc.getTableName());
        FavoredNodesManager fnm = master.getFavoredNodesManager();
        List<RegionInfo> regionsOfTable = admin.getRegions(TableName.valueOf(tableName));
        for (RegionInfo rInfo : regionsOfTable) {
            Set<ServerName> favNodes = Sets.newHashSet(fnm.getFavoredNodes(rInfo));
            Assert.assertNotNull(favNodes);
            Assert.assertEquals(FAVORED_NODES_NUM, favNodes.size());
        }
        Map<ServerName, List<Integer>> replicaLoadMap = fnm.getReplicaLoad(Lists.newArrayList(admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet()));
        Assert.assertTrue("Not all replica load collected.", ((admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().size()) == (replicaLoadMap.size())));
        for (Map.Entry<ServerName, List<Integer>> entry : replicaLoadMap.entrySet()) {
            Assert.assertTrue(((entry.getValue().size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
            Assert.assertTrue(((entry.getValue().get(0)) >= 0));
            Assert.assertTrue(((entry.getValue().get(1)) >= 0));
            Assert.assertTrue(((entry.getValue().get(2)) >= 0));
        }
        admin.disableTable(TableName.valueOf(tableName));
        admin.deleteTable(TableName.valueOf(tableName));
        replicaLoadMap = fnm.getReplicaLoad(Lists.newArrayList(admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet()));
        Assert.assertTrue((("replica load found " + (replicaLoadMap.size())) + " instead of 0."), ((replicaLoadMap.size()) == (admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().size())));
    }

    @Test
    public void testRandomAssignmentWithNoFavNodes() throws Exception {
        final String tableName = "testRandomAssignmentWithNoFavNodes";
        HTableDescriptor desc = new HTableDescriptor(TableName.valueOf(tableName));
        desc.addFamily(new org.apache.hadoop.hbase.HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitTableAvailable(desc.getTableName());
        RegionInfo hri = admin.getTableRegions(TableName.valueOf(tableName)).get(0);
        FavoredNodesManager fnm = master.getFavoredNodesManager();
        fnm.deleteFavoredNodesForRegions(Lists.newArrayList(hri));
        Assert.assertNull("Favored nodes not found null after delete", fnm.getFavoredNodes(hri));
        LoadBalancer balancer = master.getLoadBalancer();
        ServerName destination = balancer.randomAssignment(hri, Lists.newArrayList(admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)).getLiveServerMetrics().keySet().stream().collect(Collectors.toList())));
        Assert.assertNotNull(destination);
        List<ServerName> favoredNodes = fnm.getFavoredNodes(hri);
        Assert.assertNotNull(favoredNodes);
        boolean containsFN = false;
        for (ServerName sn : favoredNodes) {
            if (ServerName.isSameAddress(destination, sn)) {
                containsFN = true;
            }
        }
        Assert.assertTrue("Destination server does not belong to favored nodes.", containsFN);
    }

    @Test
    public void testBalancerWithoutFavoredNodes() throws Exception {
        TableName tableName = TableName.valueOf("testBalancerWithoutFavoredNodes");
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new org.apache.hadoop.hbase.HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), TestFavoredStochasticLoadBalancer.REGION_NUM);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitTableAvailable(tableName);
        final RegionInfo region = admin.getTableRegions(tableName).get(0);
        TestFavoredStochasticLoadBalancer.LOG.info(("Region thats supposed to be in transition: " + region));
        FavoredNodesManager fnm = master.getFavoredNodesManager();
        List<ServerName> currentFN = fnm.getFavoredNodes(region);
        Assert.assertNotNull(currentFN);
        fnm.deleteFavoredNodesForRegions(Lists.newArrayList(region));
        RegionStates regionStates = master.getAssignmentManager().getRegionStates();
        admin.setBalancerRunning(true, true);
        // Balancer should unassign the region
        Assert.assertTrue("Balancer did not run", admin.balancer());
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitUntilNoRegionsInTransition();
        admin.assign(region.getEncodedNameAsBytes());
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitUntilNoRegionsInTransition(60000);
        currentFN = fnm.getFavoredNodes(region);
        Assert.assertNotNull(currentFN);
        Assert.assertEquals("Expected number of FN not present", FAVORED_NODES_NUM, currentFN.size());
        Assert.assertTrue("Balancer did not run", admin.balancer());
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitUntilNoRegionsInTransition(60000);
        checkFavoredNodeAssignments(tableName, fnm, regionStates);
    }

    @Test
    public void test2FavoredNodesDead() throws Exception {
        TableName tableName = TableName.valueOf("testAllFavoredNodesDead");
        HTableDescriptor desc = new HTableDescriptor(tableName);
        desc.addFamily(new org.apache.hadoop.hbase.HColumnDescriptor(HConstants.CATALOG_FAMILY));
        admin.createTable(desc, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), TestFavoredStochasticLoadBalancer.REGION_NUM);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitTableAvailable(tableName);
        final RegionInfo region = admin.getTableRegions(tableName).get(0);
        TestFavoredStochasticLoadBalancer.LOG.info(("Region that's supposed to be in transition: " + region));
        FavoredNodesManager fnm = master.getFavoredNodesManager();
        List<ServerName> currentFN = fnm.getFavoredNodes(region);
        Assert.assertNotNull(currentFN);
        List<ServerName> serversToStop = Lists.newArrayList(currentFN);
        serversToStop.remove(currentFN.get(0));
        // Lets kill 2 FN for the region. All regions should still be assigned
        stopServersAndWaitUntilProcessed(serversToStop);
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitUntilNoRegionsInTransition();
        final RegionStates regionStates = master.getAssignmentManager().getRegionStates();
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitFor(10000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                return regionStates.getRegionState(region).isOpened();
            }
        });
        Assert.assertEquals("Not all regions are online", TestFavoredStochasticLoadBalancer.REGION_NUM, admin.getTableRegions(tableName).size());
        admin.setBalancerRunning(true, true);
        Assert.assertTrue("Balancer did not run", admin.balancer());
        TestFavoredStochasticLoadBalancer.TEST_UTIL.waitUntilNoRegionsInTransition(60000);
        checkFavoredNodeAssignments(tableName, fnm, regionStates);
    }
}

