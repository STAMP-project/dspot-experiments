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
package org.apache.hadoop.hdds.scm.container;


import CloseContainerEventHandler.LOG;
import GenericTestUtils.LogCapturer;
import HddsProtos.LifeCycleState.CLOSING;
import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationFactor.THREE;
import HddsProtos.ReplicationType.RATIS;
import java.io.File;
import java.io.IOException;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.pipeline.SCMPipelineManager;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the closeContainerEventHandler class.
 */
public class TestCloseContainerEventHandler {
    private static Configuration configuration;

    private static MockNodeManager nodeManager;

    private static SCMPipelineManager pipelineManager;

    private static SCMContainerManager containerManager;

    private static long size;

    private static File testDir;

    private static EventQueue eventQueue;

    @Test
    public void testIfCloseContainerEventHadnlerInvoked() {
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LOG);
        TestCloseContainerEventHandler.eventQueue.fireEvent(CLOSE_CONTAINER, new ContainerID(Math.abs(RandomUtils.nextInt())));
        TestCloseContainerEventHandler.eventQueue.processAll(1000);
        Assert.assertTrue(logCapturer.getOutput().contains("Close container Event triggered for container"));
    }

    @Test
    public void testCloseContainerEventWithInvalidContainer() {
        long id = Math.abs(RandomUtils.nextInt());
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LOG);
        TestCloseContainerEventHandler.eventQueue.fireEvent(CLOSE_CONTAINER, new ContainerID(id));
        TestCloseContainerEventHandler.eventQueue.processAll(1000);
        Assert.assertTrue(logCapturer.getOutput().contains("Failed to close the container"));
    }

    @Test
    public void testCloseContainerEventWithValidContainers() throws IOException {
        ContainerInfo container = TestCloseContainerEventHandler.containerManager.allocateContainer(RATIS, ONE, "ozone");
        ContainerID id = container.containerID();
        DatanodeDetails datanode = TestCloseContainerEventHandler.pipelineManager.getPipeline(container.getPipelineID()).getFirstNode();
        int closeCount = TestCloseContainerEventHandler.nodeManager.getCommandCount(datanode);
        TestCloseContainerEventHandler.eventQueue.fireEvent(CLOSE_CONTAINER, id);
        TestCloseContainerEventHandler.eventQueue.processAll(1000);
        Assert.assertEquals((closeCount + 1), TestCloseContainerEventHandler.nodeManager.getCommandCount(datanode));
        Assert.assertEquals(CLOSING, TestCloseContainerEventHandler.containerManager.getContainer(id).getState());
    }

    @Test
    public void testCloseContainerEventWithRatis() throws IOException {
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LOG);
        ContainerInfo container = TestCloseContainerEventHandler.containerManager.allocateContainer(RATIS, THREE, "ozone");
        ContainerID id = container.containerID();
        int[] closeCount = new int[3];
        TestCloseContainerEventHandler.eventQueue.fireEvent(CLOSE_CONTAINER, id);
        TestCloseContainerEventHandler.eventQueue.processAll(1000);
        int i = 0;
        for (DatanodeDetails details : TestCloseContainerEventHandler.pipelineManager.getPipeline(container.getPipelineID()).getNodes()) {
            closeCount[i] = TestCloseContainerEventHandler.nodeManager.getCommandCount(details);
            i++;
        }
        i = 0;
        for (DatanodeDetails details : TestCloseContainerEventHandler.pipelineManager.getPipeline(container.getPipelineID()).getNodes()) {
            Assert.assertEquals(closeCount[i], TestCloseContainerEventHandler.nodeManager.getCommandCount(details));
            i++;
        }
        TestCloseContainerEventHandler.eventQueue.fireEvent(CLOSE_CONTAINER, id);
        TestCloseContainerEventHandler.eventQueue.processAll(1000);
        i = 0;
        // Make sure close is queued for each datanode on the pipeline
        for (DatanodeDetails details : TestCloseContainerEventHandler.pipelineManager.getPipeline(container.getPipelineID()).getNodes()) {
            Assert.assertEquals(((closeCount[i]) + 1), TestCloseContainerEventHandler.nodeManager.getCommandCount(details));
            Assert.assertEquals(CLOSING, TestCloseContainerEventHandler.containerManager.getContainer(id).getState());
            i++;
        }
    }
}

