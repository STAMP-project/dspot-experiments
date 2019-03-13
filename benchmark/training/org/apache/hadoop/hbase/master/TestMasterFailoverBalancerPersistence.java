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


import org.apache.hadoop.hbase.ClusterMetrics;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.hbase.StartMiniClusterOption;
import org.apache.hadoop.hbase.testclassification.LargeTests;
import org.apache.hadoop.hbase.testclassification.MasterTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ MasterTests.class, LargeTests.class })
public class TestMasterFailoverBalancerPersistence {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestMasterFailoverBalancerPersistence.class);

    /**
     * Test that if the master fails, the load balancer maintains its
     * state (running or not) when the next master takes over
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testMasterFailoverBalancerPersistence() throws Exception {
        // Start the cluster
        HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();
        StartMiniClusterOption option = StartMiniClusterOption.builder().numMasters(3).build();
        TEST_UTIL.startMiniCluster(option);
        MiniHBaseCluster cluster = TEST_UTIL.getHBaseCluster();
        Assert.assertTrue(cluster.waitForActiveAndReadyMaster());
        HMaster active = cluster.getMaster();
        // check that the balancer is on by default for the active master
        ClusterMetrics clusterStatus = active.getClusterMetrics();
        Assert.assertTrue(clusterStatus.getBalancerOn());
        active = killActiveAndWaitForNewActive(cluster);
        // ensure the load balancer is still running on new master
        clusterStatus = active.getClusterMetrics();
        Assert.assertTrue(clusterStatus.getBalancerOn());
        // turn off the load balancer
        active.balanceSwitch(false);
        // once more, kill active master and wait for new active master to show up
        active = killActiveAndWaitForNewActive(cluster);
        // ensure the load balancer is not running on the new master
        clusterStatus = active.getClusterMetrics();
        Assert.assertFalse(clusterStatus.getBalancerOn());
        // Stop the cluster
        TEST_UTIL.shutdownMiniCluster();
    }
}

