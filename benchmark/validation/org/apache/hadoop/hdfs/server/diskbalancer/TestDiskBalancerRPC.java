/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdfs.server.diskbalancer;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_BYTES_PER_CHECKSUM_KEY;
import DFSConfigKeys.DFS_DISK_BALANCER_ENABLED;
import DiskBalancerConstants.DISKBALANCER_BANDWIDTH;
import DiskBalancerConstants.DISKBALANCER_VOLUME_NAME;
import FsDatasetSpi.FsVolumeReferences;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DiskBalancerWorkStatus;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.impl.FsVolumeImpl;
import org.apache.hadoop.hdfs.server.diskbalancer.DiskBalancerException.Result;
import org.apache.hadoop.hdfs.server.diskbalancer.connectors.ClusterConnector;
import org.apache.hadoop.hdfs.server.diskbalancer.connectors.ConnectorFactory;
import org.apache.hadoop.hdfs.server.diskbalancer.datamodel.DiskBalancerCluster;
import org.apache.hadoop.hdfs.server.diskbalancer.datamodel.DiskBalancerDataNode;
import org.apache.hadoop.hdfs.server.diskbalancer.planner.GreedyPlanner;
import org.apache.hadoop.hdfs.server.diskbalancer.planner.NodePlan;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Test DiskBalancer RPC.
 */
public class TestDiskBalancerRPC {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String PLAN_FILE = "/system/current.plan.json";

    private MiniDFSCluster cluster;

    private Configuration conf;

    @Test
    public void testSubmitPlan() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        int planVersion = rpcTestHelper.getPlanVersion();
        NodePlan plan = rpcTestHelper.getPlan();
        dataNode.submitDiskBalancerPlan(planHash, planVersion, TestDiskBalancerRPC.PLAN_FILE, plan.toJson(), false);
    }

    @Test
    public void testSubmitPlanWithInvalidHash() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        char[] hashArray = planHash.toCharArray();
        (hashArray[0])++;
        planHash = String.valueOf(hashArray);
        int planVersion = rpcTestHelper.getPlanVersion();
        NodePlan plan = rpcTestHelper.getPlan();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.INVALID_PLAN_HASH));
        dataNode.submitDiskBalancerPlan(planHash, planVersion, TestDiskBalancerRPC.PLAN_FILE, plan.toJson(), false);
    }

    @Test
    public void testSubmitPlanWithInvalidVersion() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        int planVersion = rpcTestHelper.getPlanVersion();
        planVersion++;
        NodePlan plan = rpcTestHelper.getPlan();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.INVALID_PLAN_VERSION));
        dataNode.submitDiskBalancerPlan(planHash, planVersion, TestDiskBalancerRPC.PLAN_FILE, plan.toJson(), false);
    }

    @Test
    public void testSubmitPlanWithInvalidPlan() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        int planVersion = rpcTestHelper.getPlanVersion();
        NodePlan plan = rpcTestHelper.getPlan();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.INVALID_PLAN));
        dataNode.submitDiskBalancerPlan(planHash, planVersion, "", "", false);
    }

    @Test
    public void testCancelPlan() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        int planVersion = rpcTestHelper.getPlanVersion();
        NodePlan plan = rpcTestHelper.getPlan();
        dataNode.submitDiskBalancerPlan(planHash, planVersion, TestDiskBalancerRPC.PLAN_FILE, plan.toJson(), false);
        dataNode.cancelDiskBalancePlan(planHash);
    }

    @Test
    public void testCancelNonExistentPlan() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        char[] hashArray = planHash.toCharArray();
        (hashArray[0])++;
        planHash = String.valueOf(hashArray);
        NodePlan plan = rpcTestHelper.getPlan();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.NO_SUCH_PLAN));
        dataNode.cancelDiskBalancePlan(planHash);
    }

    @Test
    public void testCancelEmptyPlan() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = "";
        NodePlan plan = rpcTestHelper.getPlan();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.NO_SUCH_PLAN));
        dataNode.cancelDiskBalancePlan(planHash);
    }

    @Test
    public void testGetDiskBalancerVolumeMapping() throws Exception {
        final int dnIndex = 0;
        DataNode dataNode = cluster.getDataNodes().get(dnIndex);
        String volumeNameJson = dataNode.getDiskBalancerSetting(DISKBALANCER_VOLUME_NAME);
        Assert.assertNotNull(volumeNameJson);
        ObjectMapper mapper = new ObjectMapper();
        @SuppressWarnings("unchecked")
        Map<String, String> volumemap = mapper.readValue(volumeNameJson, HashMap.class);
        Assert.assertEquals(2, volumemap.size());
    }

    @Test
    public void testGetDiskBalancerInvalidSetting() throws Exception {
        final int dnIndex = 0;
        final String invalidSetting = "invalidSetting";
        DataNode dataNode = cluster.getDataNodes().get(dnIndex);
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.UNKNOWN_KEY));
        dataNode.getDiskBalancerSetting(invalidSetting);
    }

    @Test
    public void testgetDiskBalancerBandwidth() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        int planVersion = rpcTestHelper.getPlanVersion();
        NodePlan plan = rpcTestHelper.getPlan();
        dataNode.submitDiskBalancerPlan(planHash, planVersion, TestDiskBalancerRPC.PLAN_FILE, plan.toJson(), false);
        String bandwidthString = dataNode.getDiskBalancerSetting(DISKBALANCER_BANDWIDTH);
        long value = Long.decode(bandwidthString);
        Assert.assertEquals(10L, value);
    }

    @Test
    public void testQueryPlan() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        String planHash = rpcTestHelper.getPlanHash();
        int planVersion = rpcTestHelper.getPlanVersion();
        NodePlan plan = rpcTestHelper.getPlan();
        dataNode.submitDiskBalancerPlan(planHash, planVersion, TestDiskBalancerRPC.PLAN_FILE, plan.toJson(), false);
        DiskBalancerWorkStatus status = dataNode.queryDiskBalancerPlan();
        Assert.assertTrue((((status.getResult()) == (PLAN_UNDER_PROGRESS)) || ((status.getResult()) == (PLAN_DONE))));
    }

    @Test
    public void testQueryPlanWithoutSubmit() throws Exception {
        TestDiskBalancerRPC.RpcTestHelper rpcTestHelper = new TestDiskBalancerRPC.RpcTestHelper().invoke();
        DataNode dataNode = rpcTestHelper.getDataNode();
        DiskBalancerWorkStatus status = dataNode.queryDiskBalancerPlan();
        Assert.assertTrue(((status.getResult()) == (NO_PLAN)));
    }

    @Test
    public void testMoveBlockAcrossVolume() throws Exception {
        Configuration conf = new HdfsConfiguration();
        final int defaultBlockSize = 100;
        conf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        conf.setLong(DFS_BLOCK_SIZE_KEY, defaultBlockSize);
        conf.setInt(DFS_BYTES_PER_CHECKSUM_KEY, defaultBlockSize);
        String fileName = "/tmp.txt";
        Path filePath = new Path(fileName);
        final int numDatanodes = 1;
        final int dnIndex = 0;
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDatanodes).build();
        FsVolumeImpl source = null;
        FsVolumeImpl dest = null;
        try {
            cluster.waitActive();
            Random r = new Random();
            FileSystem fs = cluster.getFileSystem(dnIndex);
            DFSTestUtil.createFile(fs, filePath, (10 * 1024), ((short) (1)), r.nextLong());
            DataNode dnNode = cluster.getDataNodes().get(dnIndex);
            FsDatasetSpi.FsVolumeReferences refs = dnNode.getFSDataset().getFsVolumeReferences();
            try {
                source = ((FsVolumeImpl) (refs.get(0)));
                dest = ((FsVolumeImpl) (refs.get(1)));
                DiskBalancerTestUtil.moveAllDataToDestVolume(dnNode.getFSDataset(), source, dest);
                Assert.assertEquals(0, DiskBalancerTestUtil.getBlockCount(source, false));
            } finally {
                refs.close();
            }
        } finally {
            cluster.shutdown();
        }
    }

    private class RpcTestHelper {
        private NodePlan plan;

        private int planVersion;

        private DataNode dataNode;

        private String planHash;

        public NodePlan getPlan() {
            return plan;
        }

        public int getPlanVersion() {
            return planVersion;
        }

        public DataNode getDataNode() {
            return dataNode;
        }

        public String getPlanHash() {
            return planHash;
        }

        public TestDiskBalancerRPC.RpcTestHelper invoke() throws Exception {
            final int dnIndex = 0;
            cluster.restartDataNode(dnIndex);
            cluster.waitActive();
            ClusterConnector nameNodeConnector = ConnectorFactory.getCluster(cluster.getFileSystem(0).getUri(), conf);
            DiskBalancerCluster diskBalancerCluster = new DiskBalancerCluster(nameNodeConnector);
            diskBalancerCluster.readClusterInfo();
            Assert.assertEquals(cluster.getDataNodes().size(), diskBalancerCluster.getNodes().size());
            diskBalancerCluster.setNodesToProcess(diskBalancerCluster.getNodes());
            dataNode = cluster.getDataNodes().get(dnIndex);
            DiskBalancerDataNode node = diskBalancerCluster.getNodeByUUID(dataNode.getDatanodeUuid());
            GreedyPlanner planner = new GreedyPlanner(10.0F, node);
            plan = new NodePlan(node.getDataNodeName(), node.getDataNodePort());
            planner.balanceVolumeSet(node, node.getVolumeSets().get("DISK"), plan);
            planVersion = 1;
            planHash = DigestUtils.shaHex(plan.toJson());
            return this;
        }
    }
}

