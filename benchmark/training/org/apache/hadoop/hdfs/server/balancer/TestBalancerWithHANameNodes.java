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
package org.apache.hadoop.hdfs.server.balancer;


import HdfsClientConfigKeys.DFS_NAMENODE_RPC_PORT_DEFAULT;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.NameNodeProxies;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.hdfs.server.namenode.ha.ObserverReadProxyProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test balancer with HA NameNodes
 */
public class TestBalancerWithHANameNodes {
    private MiniDFSCluster cluster;

    ClientProtocol client;

    // array of racks for original nodes in cluster
    private static final String[] TEST_RACKS = new String[]{ TestBalancer.RACK0, TestBalancer.RACK1 };

    // array of capacities for original nodes in cluster
    private static final long[] TEST_CAPACITIES = new long[]{ TestBalancer.CAPACITY, TestBalancer.CAPACITY };

    static {
        TestBalancer.initTestSetup();
    }

    /**
     * Test a cluster with even distribution, then a new empty node is added to
     * the cluster. Test start a cluster with specified number of nodes, and fills
     * it to be 30% full (with a single file replicated identically to all
     * datanodes); It then adds one new empty node and starts balancing.
     */
    @Test(timeout = 60000)
    public void testBalancerWithHANameNodes() throws Exception {
        Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        Assert.assertEquals(TestBalancerWithHANameNodes.TEST_CAPACITIES.length, TestBalancerWithHANameNodes.TEST_RACKS.length);
        MiniDFSNNTopology.NNConf nn1Conf = new MiniDFSNNTopology.NNConf("nn1");
        nn1Conf.setIpcPort(DFS_NAMENODE_RPC_PORT_DEFAULT);
        Configuration copiedConf = new Configuration(conf);
        cluster = new MiniDFSCluster.Builder(copiedConf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(TestBalancerWithHANameNodes.TEST_CAPACITIES.length).racks(TestBalancerWithHANameNodes.TEST_RACKS).simulatedCapacities(TestBalancerWithHANameNodes.TEST_CAPACITIES).build();
        HATestUtil.setFailoverConfigurations(cluster, conf);
        try {
            cluster.waitActive();
            cluster.transitionToActive(0);
            Thread.sleep(500);
            client = NameNodeProxies.createProxy(conf, FileSystem.getDefaultUri(conf), ClientProtocol.class).getProxy();
            doTest(conf);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Test Balancer with ObserverNodes.
     */
    @Test(timeout = 60000)
    public void testBalancerWithObserver() throws Exception {
        final Configuration conf = new HdfsConfiguration();
        TestBalancer.initConf(conf);
        MiniQJMHACluster qjmhaCluster = null;
        try {
            qjmhaCluster = HATestUtil.setUpObserverCluster(conf, 2, TestBalancerWithHANameNodes.TEST_CAPACITIES.length, true, TestBalancerWithHANameNodes.TEST_CAPACITIES, TestBalancerWithHANameNodes.TEST_RACKS);
            cluster = qjmhaCluster.getDfsCluster();
            cluster.waitClusterUp();
            cluster.waitActive();
            DistributedFileSystem dfs = HATestUtil.configureObserverReadFs(cluster, conf, ObserverReadProxyProvider.class, true);
            client = dfs.getClient().getNamenode();
            doTest(conf);
        } finally {
            if (qjmhaCluster != null) {
                qjmhaCluster.shutdown();
            }
        }
    }
}

