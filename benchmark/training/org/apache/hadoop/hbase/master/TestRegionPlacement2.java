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


import Position.PRIMARY;
import Position.SECONDARY;
import Position.TERTIARY;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.client.RegionInfoBuilder;
import org.apache.hadoop.hbase.favored.FavoredNodeAssignmentHelper;
import org.apache.hadoop.hbase.favored.FavoredNodeLoadBalancer;
import org.apache.hadoop.hbase.master.balancer.LoadBalancerFactory;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ MasterTests.class, MediumTests.class })
public class TestRegionPlacement2 {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRegionPlacement2.class);

    private static final Logger LOG = LoggerFactory.getLogger(TestRegionPlacement2.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final int SLAVES = 7;

    private static final int PRIMARY = Position.PRIMARY.ordinal();

    private static final int SECONDARY = Position.SECONDARY.ordinal();

    private static final int TERTIARY = Position.TERTIARY.ordinal();

    @Rule
    public TestName name = new TestName();

    @Test
    public void testFavoredNodesPresentForRoundRobinAssignment() throws HBaseIOException {
        LoadBalancer balancer = LoadBalancerFactory.getLoadBalancer(TestRegionPlacement2.TEST_UTIL.getConfiguration());
        balancer.setMasterServices(TestRegionPlacement2.TEST_UTIL.getMiniHBaseCluster().getMaster());
        balancer.initialize();
        List<ServerName> servers = new ArrayList<>();
        for (int i = 0; i < (TestRegionPlacement2.SLAVES); i++) {
            ServerName server = TestRegionPlacement2.TEST_UTIL.getMiniHBaseCluster().getRegionServer(i).getServerName();
            servers.add(server);
        }
        List<RegionInfo> regions = new ArrayList<>(1);
        RegionInfo region = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        regions.add(region);
        Map<ServerName, List<RegionInfo>> assignmentMap = balancer.roundRobinAssignment(regions, servers);
        Set<ServerName> serverBefore = assignmentMap.keySet();
        List<ServerName> favoredNodesBefore = ((FavoredNodeLoadBalancer) (balancer)).getFavoredNodes(region);
        Assert.assertTrue(((favoredNodesBefore.size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
        // the primary RS should be the one that the balancer's assignment returns
        Assert.assertTrue(ServerName.isSameAddress(serverBefore.iterator().next(), favoredNodesBefore.get(TestRegionPlacement2.PRIMARY)));
        // now remove the primary from the list of available servers
        List<ServerName> removedServers = removeMatchingServers(serverBefore, servers);
        // call roundRobinAssignment with the modified servers list
        assignmentMap = balancer.roundRobinAssignment(regions, servers);
        List<ServerName> favoredNodesAfter = ((FavoredNodeLoadBalancer) (balancer)).getFavoredNodes(region);
        Assert.assertTrue(((favoredNodesAfter.size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
        // We don't expect the favored nodes assignments to change in multiple calls
        // to the roundRobinAssignment method in the balancer (relevant for AssignmentManager.assign
        // failures)
        Assert.assertTrue(favoredNodesAfter.containsAll(favoredNodesBefore));
        Set<ServerName> serverAfter = assignmentMap.keySet();
        // We expect the new RegionServer assignee to be one of the favored nodes
        // chosen earlier.
        Assert.assertTrue(((ServerName.isSameAddress(serverAfter.iterator().next(), favoredNodesBefore.get(TestRegionPlacement2.SECONDARY))) || (ServerName.isSameAddress(serverAfter.iterator().next(), favoredNodesBefore.get(TestRegionPlacement2.TERTIARY)))));
        // put back the primary in the list of available servers
        servers.addAll(removedServers);
        // now roundRobinAssignment with the modified servers list should return the primary
        // as the regionserver assignee
        assignmentMap = balancer.roundRobinAssignment(regions, servers);
        Set<ServerName> serverWithPrimary = assignmentMap.keySet();
        Assert.assertTrue(serverBefore.containsAll(serverWithPrimary));
        // Make all the favored nodes unavailable for assignment
        removeMatchingServers(favoredNodesAfter, servers);
        // call roundRobinAssignment with the modified servers list
        assignmentMap = balancer.roundRobinAssignment(regions, servers);
        List<ServerName> favoredNodesNow = ((FavoredNodeLoadBalancer) (balancer)).getFavoredNodes(region);
        Assert.assertTrue(((favoredNodesNow.size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
        Assert.assertTrue((((!(favoredNodesNow.contains(favoredNodesAfter.get(TestRegionPlacement2.PRIMARY)))) && (!(favoredNodesNow.contains(favoredNodesAfter.get(TestRegionPlacement2.SECONDARY))))) && (!(favoredNodesNow.contains(favoredNodesAfter.get(TestRegionPlacement2.TERTIARY))))));
    }

    @Test
    public void testFavoredNodesPresentForRandomAssignment() throws HBaseIOException {
        LoadBalancer balancer = LoadBalancerFactory.getLoadBalancer(TestRegionPlacement2.TEST_UTIL.getConfiguration());
        balancer.setMasterServices(TestRegionPlacement2.TEST_UTIL.getMiniHBaseCluster().getMaster());
        balancer.initialize();
        List<ServerName> servers = new ArrayList<>();
        for (int i = 0; i < (TestRegionPlacement2.SLAVES); i++) {
            ServerName server = TestRegionPlacement2.TEST_UTIL.getMiniHBaseCluster().getRegionServer(i).getServerName();
            servers.add(server);
        }
        List<RegionInfo> regions = new ArrayList<>(1);
        RegionInfo region = RegionInfoBuilder.newBuilder(TableName.valueOf(name.getMethodName())).build();
        regions.add(region);
        ServerName serverBefore = balancer.randomAssignment(region, servers);
        List<ServerName> favoredNodesBefore = ((FavoredNodeLoadBalancer) (balancer)).getFavoredNodes(region);
        Assert.assertTrue(((favoredNodesBefore.size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
        // the primary RS should be the one that the balancer's assignment returns
        Assert.assertTrue(ServerName.isSameAddress(serverBefore, favoredNodesBefore.get(TestRegionPlacement2.PRIMARY)));
        // now remove the primary from the list of servers
        removeMatchingServers(serverBefore, servers);
        // call randomAssignment with the modified servers list
        ServerName serverAfter = balancer.randomAssignment(region, servers);
        List<ServerName> favoredNodesAfter = ((FavoredNodeLoadBalancer) (balancer)).getFavoredNodes(region);
        Assert.assertTrue(((favoredNodesAfter.size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
        // We don't expect the favored nodes assignments to change in multiple calls
        // to the randomAssignment method in the balancer (relevant for AssignmentManager.assign
        // failures)
        Assert.assertTrue(favoredNodesAfter.containsAll(favoredNodesBefore));
        // We expect the new RegionServer assignee to be one of the favored nodes
        // chosen earlier.
        Assert.assertTrue(((ServerName.isSameAddress(serverAfter, favoredNodesBefore.get(TestRegionPlacement2.SECONDARY))) || (ServerName.isSameAddress(serverAfter, favoredNodesBefore.get(TestRegionPlacement2.TERTIARY)))));
        // Make all the favored nodes unavailable for assignment
        removeMatchingServers(favoredNodesAfter, servers);
        // call randomAssignment with the modified servers list
        balancer.randomAssignment(region, servers);
        List<ServerName> favoredNodesNow = ((FavoredNodeLoadBalancer) (balancer)).getFavoredNodes(region);
        Assert.assertTrue(((favoredNodesNow.size()) == (FavoredNodeAssignmentHelper.FAVORED_NODES_NUM)));
        Assert.assertTrue((((!(favoredNodesNow.contains(favoredNodesAfter.get(TestRegionPlacement2.PRIMARY)))) && (!(favoredNodesNow.contains(favoredNodesAfter.get(TestRegionPlacement2.SECONDARY))))) && (!(favoredNodesNow.contains(favoredNodesAfter.get(TestRegionPlacement2.TERTIARY))))));
    }
}

