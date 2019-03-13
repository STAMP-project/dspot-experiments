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


import Cluster.Action;
import Cluster.Action.Type;
import Cluster.MoveRegionAction;
import HConstants.CATALOG_FAMILY;
import Option.LIVE_SERVERS;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.Waiter;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptor;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.TableDescriptor;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.favored.FavoredNodesManager;
import org.apache.hadoop.hbase.master.HMaster;
import org.apache.hadoop.hbase.master.assignment.RegionStates;
import org.apache.hadoop.hbase.master.balancer.BaseLoadBalancer.Cluster;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.apache.hbase.thirdparty.com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category(LargeTests.class)
public class TestFavoredStochasticBalancerPickers extends BalancerTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFavoredStochasticBalancerPickers.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestFavoredStochasticBalancerPickers.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int SLAVES = 6;

    private static final int REGIONS = (TestFavoredStochasticBalancerPickers.SLAVES) * 3;

    private static Configuration conf;

    private Admin admin;

    private MiniHBaseCluster cluster;

    @Rule
    public TestName name = new TestName();

    @Test
    public void testPickers() throws Exception {
        TableName tableName = TableName.valueOf(name.getMethodName());
        ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder.newBuilder(CATALOG_FAMILY).build();
        TableDescriptor desc = TableDescriptorBuilder.newBuilder(tableName).setColumnFamily(columnFamilyDescriptor).build();
        admin.createTable(desc, Bytes.toBytes("aaa"), Bytes.toBytes("zzz"), TestFavoredStochasticBalancerPickers.REGIONS);
        TestFavoredStochasticBalancerPickers.TEST_UTIL.waitUntilAllRegionsAssigned(tableName);
        TestFavoredStochasticBalancerPickers.TEST_UTIL.loadTable(admin.getConnection().getTable(tableName), CATALOG_FAMILY);
        admin.flush(tableName);
        HMaster master = cluster.getMaster();
        FavoredNodesManager fnm = master.getFavoredNodesManager();
        ServerName masterServerName = master.getServerName();
        List<ServerName> excludedServers = Lists.newArrayList(masterServerName);
        final ServerName mostLoadedServer = getRSWithMaxRegions(tableName, excludedServers);
        Assert.assertNotNull(mostLoadedServer);
        int numRegions = getTableRegionsFromServer(tableName, mostLoadedServer).size();
        excludedServers.add(mostLoadedServer);
        // Lets find another server with more regions to calculate number of regions to move
        ServerName source = getRSWithMaxRegions(tableName, excludedServers);
        Assert.assertNotNull(source);
        int regionsToMove = (getTableRegionsFromServer(tableName, source).size()) / 2;
        // Since move only works if the target is part of favored nodes of the region, lets get all
        // regions that are movable to mostLoadedServer
        List<RegionInfo> hris = getRegionsThatCanBeMoved(tableName, mostLoadedServer);
        RegionStates rst = master.getAssignmentManager().getRegionStates();
        for (int i = 0; i < regionsToMove; i++) {
            final RegionInfo regionInfo = hris.get(i);
            admin.move(regionInfo.getEncodedNameAsBytes(), Bytes.toBytes(mostLoadedServer.getServerName()));
            TestFavoredStochasticBalancerPickers.LOG.info(((("Moving region: " + (hris.get(i).getRegionNameAsString())) + " to ") + mostLoadedServer));
            TestFavoredStochasticBalancerPickers.TEST_UTIL.waitFor(60000, new Waiter.Predicate<Exception>() {
                @Override
                public boolean evaluate() throws Exception {
                    return ServerName.isSameAddress(rst.getRegionServerOfRegion(regionInfo), mostLoadedServer);
                }
            });
        }
        final int finalRegions = numRegions + regionsToMove;
        TestFavoredStochasticBalancerPickers.TEST_UTIL.waitUntilNoRegionsInTransition(60000);
        TestFavoredStochasticBalancerPickers.TEST_UTIL.waitFor(60000, new Waiter.Predicate<Exception>() {
            @Override
            public boolean evaluate() throws Exception {
                int numRegions = getTableRegionsFromServer(tableName, mostLoadedServer).size();
                return numRegions == finalRegions;
            }
        });
        TestFavoredStochasticBalancerPickers.TEST_UTIL.getHBaseCluster().startRegionServerAndWait(60000);
        Map<ServerName, List<RegionInfo>> serverAssignments = Maps.newHashMap();
        ClusterMetrics status = admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS));
        for (ServerName sn : status.getLiveServerMetrics().keySet()) {
            if (!(ServerName.isSameAddress(sn, masterServerName))) {
                serverAssignments.put(sn, getTableRegionsFromServer(tableName, sn));
            }
        }
        RegionLocationFinder regionFinder = new RegionLocationFinder();
        regionFinder.setClusterMetrics(admin.getClusterMetrics(EnumSet.of(LIVE_SERVERS)));
        regionFinder.setConf(TestFavoredStochasticBalancerPickers.conf);
        regionFinder.setServices(TestFavoredStochasticBalancerPickers.TEST_UTIL.getMiniHBaseCluster().getMaster());
        Cluster cluster = new Cluster(serverAssignments, null, regionFinder, new org.apache.hadoop.hbase.master.RackManager(TestFavoredStochasticBalancerPickers.conf));
        LoadOnlyFavoredStochasticBalancer balancer = ((LoadOnlyFavoredStochasticBalancer) (TestFavoredStochasticBalancerPickers.TEST_UTIL.getMiniHBaseCluster().getMaster().getLoadBalancer()));
        cluster.sortServersByRegionCount();
        Integer[] servers = cluster.serverIndicesSortedByRegionCount;
        TestFavoredStochasticBalancerPickers.LOG.info(("Servers sorted by region count:" + (Arrays.toString(servers))));
        TestFavoredStochasticBalancerPickers.LOG.info(("Cluster dump: " + cluster));
        if (!(mostLoadedServer.equals(cluster.servers[servers[((servers.length) - 1)]]))) {
            TestFavoredStochasticBalancerPickers.LOG.error(((("Most loaded server: " + mostLoadedServer) + " does not match: ") + (cluster.servers[servers[((servers.length) - 1)]])));
        }
        Assert.assertEquals(mostLoadedServer, cluster.servers[servers[((servers.length) - 1)]]);
        FavoredStochasticBalancer.FavoredNodeLoadPicker loadPicker = balancer.new FavoredNodeLoadPicker();
        boolean userRegionPicked = false;
        for (int i = 0; i < 100; i++) {
            if (userRegionPicked) {
                break;
            } else {
                Cluster.Action action = loadPicker.generate(cluster);
                if ((action.type) == (Type.MOVE_REGION)) {
                    Cluster.MoveRegionAction moveRegionAction = ((Cluster.MoveRegionAction) (action));
                    RegionInfo region = cluster.regions[moveRegionAction.region];
                    Assert.assertNotEquals((-1), moveRegionAction.toServer);
                    ServerName destinationServer = cluster.servers[moveRegionAction.toServer];
                    Assert.assertEquals(cluster.servers[moveRegionAction.fromServer], mostLoadedServer);
                    if (!(region.getTable().isSystemTable())) {
                        List<ServerName> favNodes = fnm.getFavoredNodes(region);
                        Assert.assertTrue(favNodes.contains(ServerName.valueOf(destinationServer.getHostAndPort(), (-1))));
                        userRegionPicked = true;
                    }
                }
            }
        }
        Assert.assertTrue("load picker did not pick expected regions in 100 iterations.", userRegionPicked);
    }
}

