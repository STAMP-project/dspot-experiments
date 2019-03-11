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
package org.apache.hadoop.hdds.scm.block;


import HddsProtos.NodeState.HEALTHY;
import HddsProtos.ReplicationFactor;
import HddsProtos.ReplicationType;
import SCMEvents.CHILL_MODE_STATUS;
import SCMEvents.CLOSE_CONTAINER;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.MockNodeManager;
import org.apache.hadoop.hdds.scm.container.SCMContainerManager;
import org.apache.hadoop.hdds.scm.container.common.helpers.AllocatedBlock;
import org.apache.hadoop.hdds.scm.container.common.helpers.ExcludeList;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.PipelineManager;
import org.apache.hadoop.hdds.scm.pipeline.RatisPipelineUtils;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.hdds.server.events.EventHandler;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.ozone.OzoneConsts;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for SCM Block Manager.
 */
public class TestBlockManager implements EventHandler<Boolean> {
    private StorageContainerManager scm;

    private SCMContainerManager mapping;

    private MockNodeManager nodeManager;

    private PipelineManager pipelineManager;

    private BlockManagerImpl blockManager;

    private File testDir;

    private static final long DEFAULT_BLOCK_SIZE = 128 * (OzoneConsts.MB);

    private static ReplicationFactor factor;

    private static ReplicationType type;

    private static String containerOwner = "OZONE";

    private static EventQueue eventQueue;

    private int numContainerPerOwnerInPipeline;

    private OzoneConfiguration conf;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void testAllocateBlock() throws Exception {
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, false);
        GenericTestUtils.waitFor(() -> {
            return !(blockManager.isScmInChillMode());
        }, 10, (1000 * 5));
        AllocatedBlock block = blockManager.allocateBlock(TestBlockManager.DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
        Assert.assertNotNull(block);
    }

    @Test
    public void testAllocateOversizedBlock() throws Exception {
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, false);
        GenericTestUtils.waitFor(() -> {
            return !(blockManager.isScmInChillMode());
        }, 10, (1000 * 5));
        long size = 6 * (OzoneConsts.GB);
        thrown.expectMessage("Unsupported block size");
        AllocatedBlock block = blockManager.allocateBlock(size, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
    }

    @Test
    public void testAllocateBlockFailureInChillMode() throws Exception {
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, true);
        GenericTestUtils.waitFor(() -> {
            return blockManager.isScmInChillMode();
        }, 10, (1000 * 5));
        // Test1: In chill mode expect an SCMException.
        thrown.expectMessage(("ChillModePrecheck failed for " + "allocateBlock"));
        blockManager.allocateBlock(TestBlockManager.DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
    }

    @Test
    public void testAllocateBlockSucInChillMode() throws Exception {
        // Test2: Exit chill mode and then try allocateBock again.
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, false);
        GenericTestUtils.waitFor(() -> {
            return !(blockManager.isScmInChillMode());
        }, 10, (1000 * 5));
        Assert.assertNotNull(blockManager.allocateBlock(TestBlockManager.DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList()));
    }

    @Test(timeout = 10000)
    public void testMultipleBlockAllocation() throws IOException, InterruptedException, TimeoutException {
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, false);
        GenericTestUtils.waitFor(() -> !(blockManager.isScmInChillMode()), 10, (1000 * 5));
        pipelineManager.createPipeline(TestBlockManager.type, TestBlockManager.factor);
        pipelineManager.createPipeline(TestBlockManager.type, TestBlockManager.factor);
        AllocatedBlock allocatedBlock = blockManager.allocateBlock(TestBlockManager.DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
        // block should be allocated in different pipelines
        GenericTestUtils.waitFor(() -> {
            try {
                AllocatedBlock block = blockManager.allocateBlock(DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
                return !(block.getPipeline().getId().equals(allocatedBlock.getPipeline().getId()));
            } catch ( e) {
            }
            return false;
        }, 100, 1000);
    }

    @Test(timeout = 10000)
    public void testMultipleBlockAllocationWithClosedContainer() throws IOException, InterruptedException, TimeoutException {
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, false);
        GenericTestUtils.waitFor(() -> !(blockManager.isScmInChillMode()), 10, (1000 * 5));
        // create pipelines
        for (int i = 0; i < (nodeManager.getNodes(HEALTHY).size()); i++) {
            pipelineManager.createPipeline(TestBlockManager.type, TestBlockManager.factor);
        }
        // wait till each pipeline has the configured number of containers.
        // After this each pipeline has numContainerPerOwnerInPipeline containers
        // for each owner
        GenericTestUtils.waitFor(() -> {
            try {
                blockManager.allocateBlock(DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
            } catch ( e) {
            }
            return verifyNumberOfContainersInPipelines(numContainerPerOwnerInPipeline);
        }, 10, 1000);
        // close all the containers in all the pipelines
        for (Pipeline pipeline : pipelineManager.getPipelines(TestBlockManager.type, TestBlockManager.factor)) {
            for (ContainerID cid : pipelineManager.getContainersInPipeline(pipeline.getId())) {
                TestBlockManager.eventQueue.fireEvent(CLOSE_CONTAINER, cid);
            }
        }
        // wait till no containers are left in the pipelines
        GenericTestUtils.waitFor(() -> verifyNumberOfContainersInPipelines(0), 10, 5000);
        // allocate block so that each pipeline has the configured number of
        // containers.
        GenericTestUtils.waitFor(() -> {
            try {
                blockManager.allocateBlock(DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList());
            } catch ( e) {
            }
            return verifyNumberOfContainersInPipelines(numContainerPerOwnerInPipeline);
        }, 10, 1000);
    }

    @Test(timeout = 10000)
    public void testBlockAllocationWithNoAvailablePipelines() throws IOException, InterruptedException, TimeoutException {
        TestBlockManager.eventQueue.fireEvent(CHILL_MODE_STATUS, false);
        GenericTestUtils.waitFor(() -> !(blockManager.isScmInChillMode()), 10, (1000 * 5));
        for (Pipeline pipeline : pipelineManager.getPipelines()) {
            RatisPipelineUtils.finalizeAndDestroyPipeline(pipelineManager, pipeline, conf, false);
        }
        Assert.assertEquals(0, pipelineManager.getPipelines(TestBlockManager.type, TestBlockManager.factor).size());
        Assert.assertNotNull(blockManager.allocateBlock(TestBlockManager.DEFAULT_BLOCK_SIZE, TestBlockManager.type, TestBlockManager.factor, TestBlockManager.containerOwner, new ExcludeList()));
        Assert.assertEquals(1, pipelineManager.getPipelines(TestBlockManager.type, TestBlockManager.factor).size());
    }
}

