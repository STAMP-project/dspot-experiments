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


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseIOException;
import org.apache.hadoop.hbase.ServerMetrics;
import org.apache.hadoop.hbase.ServerName;
import org.apache.hadoop.hbase.client.RegionInfo;
import org.apache.hadoop.hbase.master.RegionPlan;
import org.apache.hadoop.hbase.rsgroup.RSGroupBasedLoadBalancer;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


/**
 * Test RSGroupBasedLoadBalancer with StochasticLoadBalancer as internal balancer
 */
@Category(LargeTests.class)
public class TestRSGroupBasedLoadBalancerWithStochasticLoadBalancerAsInternal extends RSGroupableBalancerTestBase {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestRSGroupBasedLoadBalancerWithStochasticLoadBalancerAsInternal.class);

    private static RSGroupBasedLoadBalancer loadBalancer;

    /**
     * Test HBASE-20791
     */
    @Test
    public void testBalanceCluster() throws HBaseIOException {
        // mock cluster State
        Map<ServerName, List<RegionInfo>> clusterState = new HashMap<ServerName, List<RegionInfo>>();
        ServerName serverA = RSGroupableBalancerTestBase.servers.get(0);
        ServerName serverB = RSGroupableBalancerTestBase.servers.get(1);
        ServerName serverC = RSGroupableBalancerTestBase.servers.get(2);
        List<RegionInfo> regionsOnServerA = randomRegions(3);
        List<RegionInfo> regionsOnServerB = randomRegions(3);
        List<RegionInfo> regionsOnServerC = randomRegions(3);
        clusterState.put(serverA, regionsOnServerA);
        clusterState.put(serverB, regionsOnServerB);
        clusterState.put(serverC, regionsOnServerC);
        // mock ClusterMetrics
        Map<ServerName, ServerMetrics> serverMetricsMap = new TreeMap<>();
        serverMetricsMap.put(serverA, mockServerMetricsWithReadRequests(serverA, regionsOnServerA, 0));
        serverMetricsMap.put(serverB, mockServerMetricsWithReadRequests(serverB, regionsOnServerB, 0));
        serverMetricsMap.put(serverC, mockServerMetricsWithReadRequests(serverC, regionsOnServerC, 0));
        ClusterMetrics clusterStatus = Mockito.mock(ClusterMetrics.class);
        Mockito.when(clusterStatus.getLiveServerMetrics()).thenReturn(serverMetricsMap);
        TestRSGroupBasedLoadBalancerWithStochasticLoadBalancerAsInternal.loadBalancer.setClusterMetrics(clusterStatus);
        // ReadRequestCostFunction are Rate based, So doing setClusterMetrics again
        // this time, regions on serverA with more readRequestCount load
        // serverA : 1000,1000,1000
        // serverB : 0,0,0
        // serverC : 0,0,0
        // so should move two regions from serverA to serverB & serverC
        serverMetricsMap = new TreeMap();
        serverMetricsMap.put(serverA, mockServerMetricsWithReadRequests(serverA, regionsOnServerA, 1000));
        serverMetricsMap.put(serverB, mockServerMetricsWithReadRequests(serverB, regionsOnServerB, 0));
        serverMetricsMap.put(serverC, mockServerMetricsWithReadRequests(serverC, regionsOnServerC, 0));
        clusterStatus = Mockito.mock(ClusterMetrics.class);
        Mockito.when(clusterStatus.getLiveServerMetrics()).thenReturn(serverMetricsMap);
        TestRSGroupBasedLoadBalancerWithStochasticLoadBalancerAsInternal.loadBalancer.setClusterMetrics(clusterStatus);
        List<RegionPlan> plans = TestRSGroupBasedLoadBalancerWithStochasticLoadBalancerAsInternal.loadBalancer.balanceCluster(clusterState);
        Set<RegionInfo> regionsMoveFromServerA = new HashSet<>();
        Set<ServerName> targetServers = new HashSet<>();
        for (RegionPlan plan : plans) {
            if (plan.getSource().equals(serverA)) {
                regionsMoveFromServerA.add(plan.getRegionInfo());
                targetServers.add(plan.getDestination());
            }
        }
        // should move 2 regions from serverA, one moves to serverB, the other moves to serverC
        Assert.assertEquals(2, regionsMoveFromServerA.size());
        Assert.assertEquals(2, targetServers.size());
        Assert.assertTrue(regionsOnServerA.containsAll(regionsMoveFromServerA));
    }
}

