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


import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSClusterWithNodeGroup;
import org.apache.hadoop.hdfs.NameNodeProxies;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class tests if a balancer schedules tasks correctly.
 */
public class TestBalancerWithNodeGroup {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestBalancerWithNodeGroup");

    private static final long CAPACITY = 5000L;

    private static final String RACK0 = "/rack0";

    private static final String RACK1 = "/rack1";

    private static final String NODEGROUP0 = "/nodegroup0";

    private static final String NODEGROUP1 = "/nodegroup1";

    private static final String NODEGROUP2 = "/nodegroup2";

    private static final String fileName = "/tmp.txt";

    private static final Path filePath = new Path(TestBalancerWithNodeGroup.fileName);

    MiniDFSClusterWithNodeGroup cluster;

    ClientProtocol client;

    static final long TIMEOUT = 40000L;// msec


    static final double CAPACITY_ALLOWED_VARIANCE = 0.005;// 0.5%


    static final double BALANCE_ALLOWED_VARIANCE = 0.11;// 10%+delta


    static final int DEFAULT_BLOCK_SIZE = 100;

    static {
        TestBalancer.initTestSetup();
    }

    /**
     * Create a cluster with even distribution, and a new empty node is added to
     * the cluster, then test rack locality for balancer policy.
     */
    @Test(timeout = 60000)
    public void testBalancerWithRackLocality() throws Exception {
        Configuration conf = TestBalancerWithNodeGroup.createConf();
        long[] capacities = new long[]{ TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY };
        String[] racks = new String[]{ TestBalancerWithNodeGroup.RACK0, TestBalancerWithNodeGroup.RACK1 };
        String[] nodeGroups = new String[]{ TestBalancerWithNodeGroup.NODEGROUP0, TestBalancerWithNodeGroup.NODEGROUP1 };
        int numOfDatanodes = capacities.length;
        Assert.assertEquals(numOfDatanodes, racks.length);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(capacities.length).racks(racks).simulatedCapacities(capacities);
        MiniDFSClusterWithNodeGroup.setNodeGroups(nodeGroups);
        cluster = new MiniDFSClusterWithNodeGroup(builder);
        try {
            cluster.waitActive();
            client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
            long totalCapacity = TestBalancer.sum(capacities);
            // fill up the cluster to be 30% full
            long totalUsedSpace = (totalCapacity * 3) / 10;
            long length = totalUsedSpace / numOfDatanodes;
            TestBalancer.createFile(cluster, TestBalancerWithNodeGroup.filePath, length, ((short) (numOfDatanodes)), 0);
            LocatedBlocks lbs = client.getBlockLocations(TestBalancerWithNodeGroup.filePath.toUri().getPath(), 0, length);
            Set<ExtendedBlock> before = getBlocksOnRack(lbs.getLocatedBlocks(), TestBalancerWithNodeGroup.RACK0);
            long newCapacity = TestBalancerWithNodeGroup.CAPACITY;
            String newRack = TestBalancerWithNodeGroup.RACK1;
            String newNodeGroup = TestBalancerWithNodeGroup.NODEGROUP2;
            // start up an empty node with the same capacity and on the same rack
            cluster.startDataNodes(conf, 1, true, null, new String[]{ newRack }, new long[]{ newCapacity }, new String[]{ newNodeGroup });
            totalCapacity += newCapacity;
            // run balancer and validate results
            runBalancerCanFinish(conf, totalUsedSpace, totalCapacity);
            lbs = client.getBlockLocations(TestBalancerWithNodeGroup.filePath.toUri().getPath(), 0, length);
            Set<ExtendedBlock> after = getBlocksOnRack(lbs.getLocatedBlocks(), TestBalancerWithNodeGroup.RACK0);
            Assert.assertEquals(before, after);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Create a cluster with even distribution, and a new empty node is added to
     * the cluster, then test node-group locality for balancer policy.
     */
    @Test(timeout = 60000)
    public void testBalancerWithNodeGroup() throws Exception {
        Configuration conf = TestBalancerWithNodeGroup.createConf();
        long[] capacities = new long[]{ TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY };
        String[] racks = new String[]{ TestBalancerWithNodeGroup.RACK0, TestBalancerWithNodeGroup.RACK0, TestBalancerWithNodeGroup.RACK1, TestBalancerWithNodeGroup.RACK1 };
        String[] nodeGroups = new String[]{ TestBalancerWithNodeGroup.NODEGROUP0, TestBalancerWithNodeGroup.NODEGROUP0, TestBalancerWithNodeGroup.NODEGROUP1, TestBalancerWithNodeGroup.NODEGROUP2 };
        int numOfDatanodes = capacities.length;
        Assert.assertEquals(numOfDatanodes, racks.length);
        Assert.assertEquals(numOfDatanodes, nodeGroups.length);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(capacities.length).racks(racks).simulatedCapacities(capacities);
        MiniDFSClusterWithNodeGroup.setNodeGroups(nodeGroups);
        cluster = new MiniDFSClusterWithNodeGroup(builder);
        try {
            cluster.waitActive();
            client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
            long totalCapacity = TestBalancer.sum(capacities);
            // fill up the cluster to be 20% full
            long totalUsedSpace = (totalCapacity * 2) / 10;
            TestBalancer.createFile(cluster, TestBalancerWithNodeGroup.filePath, (totalUsedSpace / (numOfDatanodes / 2)), ((short) (numOfDatanodes / 2)), 0);
            long newCapacity = TestBalancerWithNodeGroup.CAPACITY;
            String newRack = TestBalancerWithNodeGroup.RACK1;
            String newNodeGroup = TestBalancerWithNodeGroup.NODEGROUP2;
            // start up an empty node with the same capacity and on NODEGROUP2
            cluster.startDataNodes(conf, 1, true, null, new String[]{ newRack }, new long[]{ newCapacity }, new String[]{ newNodeGroup });
            totalCapacity += newCapacity;
            // run balancer and validate results
            runBalancer(conf, totalUsedSpace, totalCapacity);
        } finally {
            cluster.shutdown();
        }
    }

    /**
     * Create a 4 nodes cluster: 2 nodes (n0, n1) in RACK0/NODEGROUP0, 1 node (n2)
     * in RACK1/NODEGROUP1 and 1 node (n3) in RACK1/NODEGROUP2. Fill the cluster
     * to 60% and 3 replicas, so n2 and n3 will have replica for all blocks according
     * to replica placement policy with NodeGroup. As a result, n2 and n3 will be
     * filled with 80% (60% x 4 / 3), and no blocks can be migrated from n2 and n3
     * to n0 or n1 as balancer policy with node group. Thus, we expect the balancer
     * to end in 5 iterations without move block process.
     */
    @Test(timeout = 60000)
    public void testBalancerEndInNoMoveProgress() throws Exception {
        Configuration conf = TestBalancerWithNodeGroup.createConf();
        long[] capacities = new long[]{ TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY, TestBalancerWithNodeGroup.CAPACITY };
        String[] racks = new String[]{ TestBalancerWithNodeGroup.RACK0, TestBalancerWithNodeGroup.RACK0, TestBalancerWithNodeGroup.RACK1, TestBalancerWithNodeGroup.RACK1 };
        String[] nodeGroups = new String[]{ TestBalancerWithNodeGroup.NODEGROUP0, TestBalancerWithNodeGroup.NODEGROUP0, TestBalancerWithNodeGroup.NODEGROUP1, TestBalancerWithNodeGroup.NODEGROUP2 };
        int numOfDatanodes = capacities.length;
        Assert.assertEquals(numOfDatanodes, racks.length);
        Assert.assertEquals(numOfDatanodes, nodeGroups.length);
        MiniDFSCluster.Builder builder = new MiniDFSCluster.Builder(conf).numDataNodes(capacities.length).racks(racks).simulatedCapacities(capacities);
        MiniDFSClusterWithNodeGroup.setNodeGroups(nodeGroups);
        cluster = new MiniDFSClusterWithNodeGroup(builder);
        try {
            cluster.waitActive();
            client = NameNodeProxies.createProxy(conf, cluster.getFileSystem(0).getUri(), ClientProtocol.class).getProxy();
            long totalCapacity = TestBalancer.sum(capacities);
            // fill up the cluster to be 60% full
            long totalUsedSpace = (totalCapacity * 6) / 10;
            TestBalancer.createFile(cluster, TestBalancerWithNodeGroup.filePath, (totalUsedSpace / 3), ((short) (3)), 0);
            // run balancer which can finish in 5 iterations with no block movement.
            runBalancerCanFinish(conf, totalUsedSpace, totalCapacity);
        } finally {
            cluster.shutdown();
        }
    }
}

