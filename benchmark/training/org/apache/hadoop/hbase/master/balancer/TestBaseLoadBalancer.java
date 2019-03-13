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


import RegionInfoBuilder.FIRST_META_REGIONINFO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.client.RegionReplicaUtil;
import org.apache.hadoop.hbase.master.LoadBalancer;
import org.apache.hadoop.hbase.master.RackManager;
import org.apache.hadoop.hbase.master.RegionPlan;
import org.apache.hadoop.hbase.master.balancer.BaseLoadBalancer.Cluster;
import org.apache.hadoop.hbase.master.balancer.BaseLoadBalancer.Cluster.MoveRegionAction;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hbase.thirdparty.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestBaseLoadBalancer extends BalancerTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestBaseLoadBalancer.class);

    private static LoadBalancer loadBalancer;

    private static final Logger LOG = LoggerFactory.getLogger(TestBaseLoadBalancer.class);

    private static final ServerName master = ServerName.valueOf("fake-master", 0, 1L);

    private static RackManager rackManager;

    private static final int NUM_SERVERS = 15;

    private static ServerName[] servers = new ServerName[TestBaseLoadBalancer.NUM_SERVERS];

    int[][] regionsAndServersMocks = new int[][]{ // { num regions, num servers }
    new int[]{ 0, 0 }, new int[]{ 0, 1 }, new int[]{ 1, 1 }, new int[]{ 2, 1 }, new int[]{ 10, 1 }, new int[]{ 1, 2 }, new int[]{ 2, 2 }, new int[]{ 3, 2 }, new int[]{ 1, 3 }, new int[]{ 2, 3 }, new int[]{ 3, 3 }, new int[]{ 25, 3 }, new int[]{ 2, 10 }, new int[]{ 2, 100 }, new int[]{ 12, 10 }, new int[]{ 12, 100 } };

    @Rule
    public TestName name = new TestName();

    public static class MockBalancer extends BaseLoadBalancer {
        @Override
        public List<RegionPlan> balanceCluster(Map<ServerName, List<RegionInfo>> clusterState) {
            return null;
        }

        @Override
        public List<RegionPlan> balanceCluster(TableName tableName, Map<ServerName, List<RegionInfo>> clusterState) throws HBaseIOException {
            return null;
        }
    }

    /**
     * Tests the bulk assignment used during cluster startup.
     *
     * Round-robin. Should yield a balanced cluster so same invariant as the load
     * balancer holds, all servers holding either floor(avg) or ceiling(avg).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testBulkAssignment() throws Exception {
        List<ServerName> tmp = getListOfServerNames(randomServers(5, 0));
        List<RegionInfo> hris = randomRegions(20);
        hris.add(FIRST_META_REGIONINFO);
        tmp.add(TestBaseLoadBalancer.master);
        Map<ServerName, List<RegionInfo>> plans = TestBaseLoadBalancer.loadBalancer.roundRobinAssignment(hris, tmp);
        if (LoadBalancer.isTablesOnMaster(TestBaseLoadBalancer.loadBalancer.getConf())) {
            Assert.assertTrue(plans.get(TestBaseLoadBalancer.master).contains(FIRST_META_REGIONINFO));
            Assert.assertEquals(1, plans.get(TestBaseLoadBalancer.master).size());
        }
        int totalRegion = 0;
        for (List<RegionInfo> regions : plans.values()) {
            totalRegion += regions.size();
        }
        Assert.assertEquals(hris.size(), totalRegion);
        for (int[] mock : regionsAndServersMocks) {
            TestBaseLoadBalancer.LOG.debug((((("testBulkAssignment with " + (mock[0])) + " regions and ") + (mock[1])) + " servers"));
            List<RegionInfo> regions = randomRegions(mock[0]);
            List<ServerAndLoad> servers = randomServers(mock[1], 0);
            List<ServerName> list = getListOfServerNames(servers);
            Map<ServerName, List<RegionInfo>> assignments = TestBaseLoadBalancer.loadBalancer.roundRobinAssignment(regions, list);
            float average = ((float) (regions.size())) / (servers.size());
            int min = ((int) (Math.floor(average)));
            int max = ((int) (Math.ceil(average)));
            if ((assignments != null) && (!(assignments.isEmpty()))) {
                for (List<RegionInfo> regionList : assignments.values()) {
                    Assert.assertTrue((((regionList.size()) == min) || ((regionList.size()) == max)));
                }
            }
            returnRegions(regions);
            returnServers(list);
        }
    }

    /**
     * Test the cluster startup bulk assignment which attempts to retain
     * assignment info.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testRetainAssignment() throws Exception {
        // Test simple case where all same servers are there
        List<ServerAndLoad> servers = randomServers(10, 10);
        List<RegionInfo> regions = randomRegions(100);
        Map<RegionInfo, ServerName> existing = new java.util.TreeMap(RegionInfo.COMPARATOR);
        for (int i = 0; i < (regions.size()); i++) {
            ServerName sn = servers.get((i % (servers.size()))).getServerName();
            // The old server would have had same host and port, but different
            // start code!
            ServerName snWithOldStartCode = ServerName.valueOf(sn.getHostname(), sn.getPort(), ((sn.getStartcode()) - 10));
            existing.put(regions.get(i), snWithOldStartCode);
        }
        List<ServerName> listOfServerNames = getListOfServerNames(servers);
        Map<ServerName, List<RegionInfo>> assignment = TestBaseLoadBalancer.loadBalancer.retainAssignment(existing, listOfServerNames);
        assertRetainedAssignment(existing, listOfServerNames, assignment);
        // Include two new servers that were not there before
        List<ServerAndLoad> servers2 = new ArrayList(servers);
        servers2.add(randomServer(10));
        servers2.add(randomServer(10));
        listOfServerNames = getListOfServerNames(servers2);
        assignment = TestBaseLoadBalancer.loadBalancer.retainAssignment(existing, listOfServerNames);
        assertRetainedAssignment(existing, listOfServerNames, assignment);
        // Remove two of the servers that were previously there
        List<ServerAndLoad> servers3 = new ArrayList(servers);
        servers3.remove(0);
        servers3.remove(0);
        listOfServerNames = getListOfServerNames(servers3);
        assignment = TestBaseLoadBalancer.loadBalancer.retainAssignment(existing, listOfServerNames);
        assertRetainedAssignment(existing, listOfServerNames, assignment);
    }

    @Test
    public void testRandomAssignment() throws Exception {
        for (int i = 1; i != 5; ++i) {
            TestBaseLoadBalancer.LOG.info(("run testRandomAssignment() with idle servers:" + i));
            testRandomAssignment(i);
        }
    }

    @Test
    public void testRegionAvailability() throws Exception {
        // Create a cluster with a few servers, assign them to specific racks
        // then assign some regions. The tests should check whether moving a
        // replica from one node to a specific other node or rack lowers the
        // availability of the region or not
        List<RegionInfo> list0 = new ArrayList<>();
        List<RegionInfo> list1 = new ArrayList<>();
        List<RegionInfo> list2 = new ArrayList<>();
        // create a region (region1)
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes("key1")).setEndKey(Bytes.toBytes("key2")).setSplit(false).setRegionId(100).build();
        // create a replica of the region (replica_of_region1)
        RegionInfo hri2 = RegionReplicaUtil.getRegionInfoForReplica(hri1, 1);
        // create a second region (region2)
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes("key2")).setEndKey(Bytes.toBytes("key3")).setSplit(false).setRegionId(101).build();
        list0.add(hri1);// only region1

        list1.add(hri2);// only replica_of_region1

        list2.add(hri3);// only region2

        Map<ServerName, List<RegionInfo>> clusterState = new LinkedHashMap<>();
        clusterState.put(TestBaseLoadBalancer.servers[0], list0);// servers[0] hosts region1

        clusterState.put(TestBaseLoadBalancer.servers[1], list1);// servers[1] hosts replica_of_region1

        clusterState.put(TestBaseLoadBalancer.servers[2], list2);// servers[2] hosts region2

        // create a cluster with the above clusterState. The way in which the
        // cluster is created (constructor code) would make sure the indices of
        // the servers are in the order in which it is inserted in the clusterState
        // map (linkedhashmap is important). A similar thing applies to the region lists
        Cluster cluster = new Cluster(clusterState, null, null, TestBaseLoadBalancer.rackManager);
        // check whether a move of region1 from servers[0] to servers[1] would lower
        // the availability of region1
        Assert.assertTrue(cluster.wouldLowerAvailability(hri1, TestBaseLoadBalancer.servers[1]));
        // check whether a move of region1 from servers[0] to servers[2] would lower
        // the availability of region1
        Assert.assertTrue((!(cluster.wouldLowerAvailability(hri1, TestBaseLoadBalancer.servers[2]))));
        // check whether a move of replica_of_region1 from servers[0] to servers[2] would lower
        // the availability of replica_of_region1
        Assert.assertTrue((!(cluster.wouldLowerAvailability(hri2, TestBaseLoadBalancer.servers[2]))));
        // check whether a move of region2 from servers[0] to servers[1] would lower
        // the availability of region2
        Assert.assertTrue((!(cluster.wouldLowerAvailability(hri3, TestBaseLoadBalancer.servers[1]))));
        // now lets have servers[1] host replica_of_region2
        list1.add(RegionReplicaUtil.getRegionInfoForReplica(hri3, 1));
        // create a new clusterState with the above change
        cluster = new Cluster(clusterState, null, null, TestBaseLoadBalancer.rackManager);
        // now check whether a move of a replica from servers[0] to servers[1] would lower
        // the availability of region2
        Assert.assertTrue(cluster.wouldLowerAvailability(hri3, TestBaseLoadBalancer.servers[1]));
        // start over again
        clusterState.clear();
        clusterState.put(TestBaseLoadBalancer.servers[0], list0);// servers[0], rack1 hosts region1

        clusterState.put(TestBaseLoadBalancer.servers[5], list1);// servers[5], rack2 hosts replica_of_region1 and replica_of_region2

        clusterState.put(TestBaseLoadBalancer.servers[6], list2);// servers[6], rack2 hosts region2

        clusterState.put(TestBaseLoadBalancer.servers[10], new ArrayList());// servers[10], rack3 hosts no region

        // create a cluster with the above clusterState
        cluster = new Cluster(clusterState, null, null, TestBaseLoadBalancer.rackManager);
        // check whether a move of region1 from servers[0],rack1 to servers[6],rack2 would
        // lower the availability
        Assert.assertTrue(cluster.wouldLowerAvailability(hri1, TestBaseLoadBalancer.servers[0]));
        // now create a cluster without the rack manager
        cluster = new Cluster(clusterState, null, null, null);
        // now repeat check whether a move of region1 from servers[0] to servers[6] would
        // lower the availability
        Assert.assertTrue((!(cluster.wouldLowerAvailability(hri1, TestBaseLoadBalancer.servers[6]))));
    }

    @Test
    public void testRegionAvailabilityWithRegionMoves() throws Exception {
        List<RegionInfo> list0 = new ArrayList<>();
        List<RegionInfo> list1 = new ArrayList<>();
        List<RegionInfo> list2 = new ArrayList<>();
        // create a region (region1)
        RegionInfo hri1 = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes("key1")).setEndKey(Bytes.toBytes("key2")).setSplit(false).setRegionId(100).build();
        // create a replica of the region (replica_of_region1)
        RegionInfo hri2 = RegionReplicaUtil.getRegionInfoForReplica(hri1, 1);
        // create a second region (region2)
        RegionInfo hri3 = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).setStartKey(Bytes.toBytes("key2")).setEndKey(Bytes.toBytes("key3")).setSplit(false).setRegionId(101).build();
        list0.add(hri1);// only region1

        list1.add(hri2);// only replica_of_region1

        list2.add(hri3);// only region2

        Map<ServerName, List<RegionInfo>> clusterState = new LinkedHashMap<>();
        clusterState.put(TestBaseLoadBalancer.servers[0], list0);// servers[0] hosts region1

        clusterState.put(TestBaseLoadBalancer.servers[1], list1);// servers[1] hosts replica_of_region1

        clusterState.put(TestBaseLoadBalancer.servers[2], list2);// servers[2] hosts region2

        // create a cluster with the above clusterState. The way in which the
        // cluster is created (constructor code) would make sure the indices of
        // the servers are in the order in which it is inserted in the clusterState
        // map (linkedhashmap is important).
        Cluster cluster = new Cluster(clusterState, null, null, TestBaseLoadBalancer.rackManager);
        // check whether moving region1 from servers[1] to servers[2] would lower availability
        Assert.assertTrue((!(cluster.wouldLowerAvailability(hri1, TestBaseLoadBalancer.servers[2]))));
        // now move region1 from servers[0] to servers[2]
        cluster.doAction(new MoveRegionAction(0, 0, 2));
        // check that the numMaxRegionsPerTable for "table" has increased to 2
        Assert.assertEquals(2, cluster.numMaxRegionsPerTable[0]);
        // now repeat check whether moving region1 from servers[1] to servers[2]
        // would lower availability
        Assert.assertTrue(cluster.wouldLowerAvailability(hri1, TestBaseLoadBalancer.servers[2]));
        // start over again
        clusterState.clear();
        List<RegionInfo> list3 = new ArrayList<>();
        RegionInfo hri4 = RegionReplicaUtil.getRegionInfoForReplica(hri3, 1);
        list3.add(hri4);
        clusterState.put(TestBaseLoadBalancer.servers[0], list0);// servers[0], rack1 hosts region1

        clusterState.put(TestBaseLoadBalancer.servers[5], list1);// servers[5], rack2 hosts replica_of_region1

        clusterState.put(TestBaseLoadBalancer.servers[6], list2);// servers[6], rack2 hosts region2

        clusterState.put(TestBaseLoadBalancer.servers[12], list3);// servers[12], rack3 hosts replica_of_region2

        // create a cluster with the above clusterState
        cluster = new Cluster(clusterState, null, null, TestBaseLoadBalancer.rackManager);
        // check whether a move of replica_of_region2 from servers[12],rack3 to servers[0],rack1 would
        // lower the availability
        Assert.assertTrue((!(cluster.wouldLowerAvailability(hri4, TestBaseLoadBalancer.servers[0]))));
        // now move region2 from servers[6],rack2 to servers[0],rack1
        cluster.doAction(new MoveRegionAction(2, 2, 0));
        // now repeat check if replica_of_region2 from servers[12],rack3 to servers[0],rack1 would
        // lower the availability
        Assert.assertTrue(cluster.wouldLowerAvailability(hri3, TestBaseLoadBalancer.servers[0]));
    }

    @Test
    public void testClusterServersWithSameHostPort() {
        // tests whether the BaseLoadBalancer.Cluster can be constructed with servers
        // sharing same host and port
        List<ServerName> servers = getListOfServerNames(randomServers(10, 10));
        List<RegionInfo> regions = randomRegions(101);
        Map<ServerName, List<RegionInfo>> clusterState = new HashMap<>();
        assignRegions(regions, servers, clusterState);
        // construct another list of servers, but sharing same hosts and ports
        List<ServerName> oldServers = new ArrayList(servers.size());
        for (ServerName sn : servers) {
            // The old server would have had same host and port, but different start code!
            oldServers.add(ServerName.valueOf(sn.getHostname(), sn.getPort(), ((sn.getStartcode()) - 10)));
        }
        regions = randomRegions(9);// some more regions

        assignRegions(regions, oldServers, clusterState);
        // should not throw exception:
        BaseLoadBalancer.Cluster cluster = new Cluster(clusterState, null, null, null);
        Assert.assertEquals((101 + 9), cluster.numRegions);
        Assert.assertEquals(10, cluster.numServers);// only 10 servers because they share the same host + port

    }

    @Test
    public void testClusterRegionLocations() {
        // tests whether region locations are handled correctly in Cluster
        List<ServerName> servers = getListOfServerNames(randomServers(10, 10));
        List<RegionInfo> regions = randomRegions(101);
        Map<ServerName, List<RegionInfo>> clusterState = new HashMap<>();
        assignRegions(regions, servers, clusterState);
        // mock block locality for some regions
        RegionLocationFinder locationFinder = Mockito.mock(RegionLocationFinder.class);
        // block locality: region:0   => {server:0}
        // region:1   => {server:0, server:1}
        // region:42 => {server:4, server:9, server:5}
        Mockito.when(locationFinder.getTopBlockLocations(regions.get(0))).thenReturn(Lists.newArrayList(servers.get(0)));
        Mockito.when(locationFinder.getTopBlockLocations(regions.get(1))).thenReturn(Lists.newArrayList(servers.get(0), servers.get(1)));
        Mockito.when(locationFinder.getTopBlockLocations(regions.get(42))).thenReturn(Lists.newArrayList(servers.get(4), servers.get(9), servers.get(5)));
        Mockito.when(locationFinder.getTopBlockLocations(regions.get(43))).thenReturn(Lists.newArrayList(ServerName.valueOf("foo", 0, 0)));// this server does not exists in clusterStatus

        BaseLoadBalancer.Cluster cluster = new Cluster(clusterState, null, locationFinder, null);
        int r0 = ArrayUtils.indexOf(cluster.regions, regions.get(0));// this is ok, it is just a test

        int r1 = ArrayUtils.indexOf(cluster.regions, regions.get(1));
        int r10 = ArrayUtils.indexOf(cluster.regions, regions.get(10));
        int r42 = ArrayUtils.indexOf(cluster.regions, regions.get(42));
        int r43 = ArrayUtils.indexOf(cluster.regions, regions.get(43));
        int s0 = cluster.serversToIndex.get(servers.get(0).getHostAndPort());
        int s1 = cluster.serversToIndex.get(servers.get(1).getHostAndPort());
        int s4 = cluster.serversToIndex.get(servers.get(4).getHostAndPort());
        int s5 = cluster.serversToIndex.get(servers.get(5).getHostAndPort());
        int s9 = cluster.serversToIndex.get(servers.get(9).getHostAndPort());
        // region 0 locations
        Assert.assertEquals(1, cluster.regionLocations[r0].length);
        Assert.assertEquals(s0, cluster.regionLocations[r0][0]);
        // region 1 locations
        Assert.assertEquals(2, cluster.regionLocations[r1].length);
        Assert.assertEquals(s0, cluster.regionLocations[r1][0]);
        Assert.assertEquals(s1, cluster.regionLocations[r1][1]);
        // region 10 locations
        Assert.assertEquals(0, cluster.regionLocations[r10].length);
        // region 42 locations
        Assert.assertEquals(3, cluster.regionLocations[r42].length);
        Assert.assertEquals(s4, cluster.regionLocations[r42][0]);
        Assert.assertEquals(s9, cluster.regionLocations[r42][1]);
        Assert.assertEquals(s5, cluster.regionLocations[r42][2]);
        // region 43 locations
        Assert.assertEquals(1, cluster.regionLocations[r43].length);
        Assert.assertEquals((-1), cluster.regionLocations[r43][0]);
    }
}

