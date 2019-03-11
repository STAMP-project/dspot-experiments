/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.diskbalancer;


import DFSConfigKeys.DFS_DISK_BALANCER_ENABLED;
import DiskBalancerException.Result;
import DiskBalancerWorkStatus.DiskBalancerWorkEntry;
import DiskBalancerWorkStatus.Result.PLAN_CANCELLED;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DiskBalancer;
import org.apache.hadoop.hdfs.server.datanode.DiskBalancerWorkItem;
import org.apache.hadoop.hdfs.server.datanode.DiskBalancerWorkStatus;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.diskbalancer.connectors.ClusterConnector;
import org.apache.hadoop.hdfs.server.diskbalancer.connectors.ConnectorFactory;
import org.apache.hadoop.hdfs.server.diskbalancer.datamodel.DiskBalancerCluster;
import org.apache.hadoop.hdfs.server.diskbalancer.datamodel.DiskBalancerDataNode;
import org.apache.hadoop.hdfs.server.diskbalancer.planner.GreedyPlanner;
import org.apache.hadoop.hdfs.server.diskbalancer.planner.MoveStep;
import org.apache.hadoop.hdfs.server.diskbalancer.planner.NodePlan;
import org.apache.hadoop.hdfs.server.diskbalancer.planner.Step;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static DiskBalancerWorkStatus.Result.PLAN_DONE;


/**
 * Tests diskbalancer with a mock mover.
 */
public class TestDiskBalancerWithMockMover {
    static final Logger LOG = LoggerFactory.getLogger(TestDiskBalancerWithMockMover.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String PLAN_FILE = "/system/current.plan.json";

    private MiniDFSCluster cluster;

    private String sourceName;

    private String destName;

    private String sourceUUID;

    private String destUUID;

    private String nodeID;

    private DataNode dataNode;

    /**
     * Checks that we return the right error if diskbalancer is not enabled.
     */
    @Test
    public void testDiskBalancerDisabled() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_DISK_BALANCER_ENABLED, false);
        restartDataNode();
        TestDiskBalancerWithMockMover.TestMover blockMover = new TestDiskBalancerWithMockMover.TestMover(cluster.getDataNodes().get(0).getFSDataset());
        DiskBalancer balancer = new TestDiskBalancerWithMockMover.DiskBalancerBuilder(conf).setMover(blockMover).build();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.DISK_BALANCER_NOT_ENABLED));
        balancer.queryWorkStatus();
    }

    /**
     * Checks that Enable flag works correctly.
     *
     * @throws DiskBalancerException
     * 		
     */
    @Test
    public void testDiskBalancerEnabled() throws DiskBalancerException {
        Configuration conf = new HdfsConfiguration();
        conf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
        TestDiskBalancerWithMockMover.TestMover blockMover = new TestDiskBalancerWithMockMover.TestMover(cluster.getDataNodes().get(0).getFSDataset());
        DiskBalancer balancer = new TestDiskBalancerWithMockMover.DiskBalancerBuilder(conf).setMover(blockMover).build();
        DiskBalancerWorkStatus status = balancer.queryWorkStatus();
        Assert.assertEquals(NO_PLAN, status.getResult());
    }

    /**
     * Test a second submit plan fails.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testResubmitDiskBalancerPlan() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        // ask block mover to get stuck in copy block
        mockMoverHelper.getBlockMover().setSleep();
        executeSubmitPlan(plan, balancer);
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.PLAN_ALREADY_IN_PROGRESS));
        executeSubmitPlan(plan, balancer);
        // Not needed but this is the cleanup step.
        mockMoverHelper.getBlockMover().clearSleep();
    }

    @Test
    public void testSubmitDiskBalancerPlan() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        final DiskBalancer balancer = mockMoverHelper.getBalancer();
        executeSubmitPlan(plan, balancer);
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    return (balancer.queryWorkStatus().getResult()) == (PLAN_DONE);
                } catch (IOException ex) {
                    return false;
                }
            }
        }, 1000, 100000);
        // Asserts that submit plan caused an execution in the background.
        Assert.assertTrue(((mockMoverHelper.getBlockMover().getRunCount()) == 1));
    }

    @Test
    public void testSubmitWithOlderPlan() throws Exception {
        final long millisecondInAnHour = (1000 * 60) * 60L;
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        plan.setTimeStamp(((Time.now()) - (32 * millisecondInAnHour)));
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.OLD_PLAN_SUBMITTED));
        executeSubmitPlan(plan, balancer);
    }

    @Test
    public void testSubmitWithOldInvalidVersion() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.INVALID_PLAN_VERSION));
        // Plan version is invalid -- there is no version 0.
        executeSubmitPlan(plan, balancer, 0);
    }

    @Test
    public void testSubmitWithNullPlan() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        String planJson = plan.toJson();
        String planID = DigestUtils.shaHex(planJson);
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.INVALID_PLAN));
        balancer.submitPlan(planID, 1, "no-plan-file.json", null, false);
    }

    @Test
    public void testSubmitWithInvalidHash() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        String planJson = plan.toJson();
        String planID = DigestUtils.shaHex(planJson);
        char repChar = planID.charAt(0);
        repChar++;
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.INVALID_PLAN_HASH));
        balancer.submitPlan(planID.replace(planID.charAt(0), repChar), 1, TestDiskBalancerWithMockMover.PLAN_FILE, planJson, false);
    }

    /**
     * Test Cancel Plan.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCancelDiskBalancerPlan() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        // ask block mover to delay execution
        mockMoverHelper.getBlockMover().setSleep();
        executeSubmitPlan(plan, balancer);
        String planJson = plan.toJson();
        String planID = DigestUtils.shaHex(planJson);
        balancer.cancelPlan(planID);
        DiskBalancerWorkStatus status = balancer.queryWorkStatus();
        Assert.assertEquals(PLAN_CANCELLED, status.getResult());
        executeSubmitPlan(plan, balancer);
        // Send a Wrong cancellation request.
        char first = planID.charAt(0);
        first++;
        thrown.expect(DiskBalancerException.class);
        thrown.expect(new DiskBalancerResultVerifier(Result.NO_SUCH_PLAN));
        balancer.cancelPlan(planID.replace(planID.charAt(0), first));
        // Now cancel the real one
        balancer.cancelPlan(planID);
        mockMoverHelper.getBlockMover().clearSleep();// unblock mover.

        status = balancer.queryWorkStatus();
        Assert.assertEquals(PLAN_CANCELLED, status.getResult());
    }

    /**
     * Test Custom bandwidth.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCustomBandwidth() throws Exception {
        TestDiskBalancerWithMockMover.MockMoverHelper mockMoverHelper = new TestDiskBalancerWithMockMover.MockMoverHelper().invoke();
        NodePlan plan = mockMoverHelper.getPlan();
        DiskBalancer balancer = mockMoverHelper.getBalancer();
        for (Step step : plan.getVolumeSetPlans()) {
            MoveStep tempStep = ((MoveStep) (step));
            tempStep.setBandwidth(100);
        }
        executeSubmitPlan(plan, balancer);
        DiskBalancerWorkStatus status = balancer.queryWorkStatus();
        Assert.assertNotNull(status);
        DiskBalancerWorkStatus.DiskBalancerWorkEntry entry = balancer.queryWorkStatus().getCurrentState().get(0);
        Assert.assertEquals(100L, entry.getWorkItem().getBandwidth());
    }

    /**
     * Allows us to control mover class for test purposes.
     */
    public static class TestMover implements DiskBalancer.BlockMover {
        private AtomicBoolean shouldRun;

        private FsDatasetSpi dataset;

        private int runCount;

        private volatile boolean sleepInCopyBlocks;

        private long delay;

        public TestMover(FsDatasetSpi dataset) {
            this.dataset = dataset;
            this.shouldRun = new AtomicBoolean(false);
        }

        public void setSleep() {
            sleepInCopyBlocks = true;
        }

        public void clearSleep() {
            sleepInCopyBlocks = false;
        }

        public void setDelay(long milliseconds) {
            this.delay = milliseconds;
        }

        /**
         * Copies blocks from a set of volumes.
         *
         * @param pair
         * 		- Source and Destination Volumes.
         * @param item
         * 		- Number of bytes to move from volumes.
         */
        @Override
        public void copyBlocks(DiskBalancer.VolumePair pair, DiskBalancerWorkItem item) {
            try {
                // get stuck if we are asked to sleep.
                while (sleepInCopyBlocks) {
                    if (!(this.shouldRun())) {
                        return;
                    }
                    Thread.sleep(10);
                } 
                if ((delay) > 0) {
                    Thread.sleep(delay);
                }
                synchronized(this) {
                    if (shouldRun()) {
                        (runCount)++;
                    }
                }
            } catch (InterruptedException ex) {
                // A failure here can be safely ignored with no impact for tests.
                TestDiskBalancerWithMockMover.LOG.error(ex.toString());
            }
        }

        /**
         * Sets copyblocks into runnable state.
         */
        @Override
        public void setRunnable() {
            this.shouldRun.set(true);
        }

        /**
         * Signals copy block to exit.
         */
        @Override
        public void setExitFlag() {
            this.shouldRun.set(false);
        }

        /**
         * Returns the shouldRun boolean flag.
         */
        public boolean shouldRun() {
            return this.shouldRun.get();
        }

        @Override
        public FsDatasetSpi getDataset() {
            return this.dataset;
        }

        /**
         * Returns time when this plan started executing.
         *
         * @return Start time in milliseconds.
         */
        @Override
        public long getStartTime() {
            return 0;
        }

        /**
         * Number of seconds elapsed.
         *
         * @return time in seconds
         */
        @Override
        public long getElapsedSeconds() {
            return 0;
        }

        public int getRunCount() {
            synchronized(this) {
                TestDiskBalancerWithMockMover.LOG.info(("Run count : " + (runCount)));
                return runCount;
            }
        }
    }

    private class MockMoverHelper {
        private DiskBalancer balancer;

        private NodePlan plan;

        private TestDiskBalancerWithMockMover.TestMover blockMover;

        public DiskBalancer getBalancer() {
            return balancer;
        }

        public NodePlan getPlan() {
            return plan;
        }

        public TestDiskBalancerWithMockMover.TestMover getBlockMover() {
            return blockMover;
        }

        public TestDiskBalancerWithMockMover.MockMoverHelper invoke() throws Exception {
            Configuration conf = new HdfsConfiguration();
            conf.setBoolean(DFS_DISK_BALANCER_ENABLED, true);
            restartDataNode();
            blockMover = new TestDiskBalancerWithMockMover.TestMover(dataNode.getFSDataset());
            blockMover.setRunnable();
            balancer = new TestDiskBalancerWithMockMover.DiskBalancerBuilder(conf).setMover(blockMover).setNodeID(nodeID).build();
            DiskBalancerCluster diskBalancerCluster = new TestDiskBalancerWithMockMover.DiskBalancerClusterBuilder().setClusterSource("/diskBalancer/data-cluster-3node-3disk.json").build();
            plan = new TestDiskBalancerWithMockMover.PlanBuilder(diskBalancerCluster, nodeID).setPathMap(sourceName, destName).setUUIDMap(sourceUUID, destUUID).build();
            return this;
        }
    }

    private static class DiskBalancerBuilder {
        private TestDiskBalancerWithMockMover.TestMover blockMover;

        private Configuration conf;

        private String nodeID;

        public DiskBalancerBuilder(Configuration conf) {
            this.conf = conf;
        }

        public TestDiskBalancerWithMockMover.DiskBalancerBuilder setNodeID(String nodeID) {
            this.nodeID = nodeID;
            return this;
        }

        public TestDiskBalancerWithMockMover.DiskBalancerBuilder setConf(Configuration conf) {
            this.conf = conf;
            return this;
        }

        public TestDiskBalancerWithMockMover.DiskBalancerBuilder setMover(TestDiskBalancerWithMockMover.TestMover mover) {
            this.blockMover = mover;
            return this;
        }

        public TestDiskBalancerWithMockMover.DiskBalancerBuilder setRunnable() {
            blockMover.setRunnable();
            return this;
        }

        public DiskBalancer build() {
            Preconditions.checkNotNull(blockMover);
            return new DiskBalancer(nodeID, conf, blockMover);
        }
    }

    private static class DiskBalancerClusterBuilder {
        private String jsonFilePath;

        private Configuration conf;

        public TestDiskBalancerWithMockMover.DiskBalancerClusterBuilder setConf(Configuration conf) {
            this.conf = conf;
            return this;
        }

        public TestDiskBalancerWithMockMover.DiskBalancerClusterBuilder setClusterSource(String jsonFilePath) throws Exception {
            this.jsonFilePath = jsonFilePath;
            return this;
        }

        public DiskBalancerCluster build() throws Exception {
            DiskBalancerCluster diskBalancerCluster;
            URI clusterJson = getClass().getResource(jsonFilePath).toURI();
            ClusterConnector jsonConnector = ConnectorFactory.getCluster(clusterJson, conf);
            diskBalancerCluster = new DiskBalancerCluster(jsonConnector);
            diskBalancerCluster.readClusterInfo();
            diskBalancerCluster.setNodesToProcess(diskBalancerCluster.getNodes());
            return diskBalancerCluster;
        }
    }

    private static class PlanBuilder {
        private String sourcePath;

        private String destPath;

        private String sourceUUID;

        private String destUUID;

        private DiskBalancerCluster balancerCluster;

        private String nodeID;

        public PlanBuilder(DiskBalancerCluster balancerCluster, String nodeID) {
            this.balancerCluster = balancerCluster;
            this.nodeID = nodeID;
        }

        public TestDiskBalancerWithMockMover.PlanBuilder setPathMap(String sourcePath, String destPath) {
            this.sourcePath = sourcePath;
            this.destPath = destPath;
            return this;
        }

        public TestDiskBalancerWithMockMover.PlanBuilder setUUIDMap(String sourceUUID, String destUUID) {
            this.sourceUUID = sourceUUID;
            this.destUUID = destUUID;
            return this;
        }

        public NodePlan build() throws Exception {
            final int dnIndex = 0;
            Preconditions.checkNotNull(balancerCluster);
            Preconditions.checkState(((nodeID.length()) > 0));
            DiskBalancerDataNode node = balancerCluster.getNodes().get(dnIndex);
            node.setDataNodeUUID(nodeID);
            GreedyPlanner planner = new GreedyPlanner(10.0F, node);
            NodePlan plan = new NodePlan(node.getDataNodeName(), node.getDataNodePort());
            planner.balanceVolumeSet(node, node.getVolumeSets().get("DISK"), plan);
            setVolumeNames(plan);
            return plan;
        }

        private void setVolumeNames(NodePlan plan) {
            Iterator<Step> iter = plan.getVolumeSetPlans().iterator();
            while (iter.hasNext()) {
                MoveStep nextStep = ((MoveStep) (iter.next()));
                nextStep.getSourceVolume().setPath(sourcePath);
                nextStep.getSourceVolume().setUuid(sourceUUID);
                nextStep.getDestinationVolume().setPath(destPath);
                nextStep.getDestinationVolume().setUuid(destUUID);
            } 
        }
    }
}

