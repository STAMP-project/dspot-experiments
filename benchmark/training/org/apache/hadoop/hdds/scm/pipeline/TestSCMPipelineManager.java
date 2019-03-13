/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.pipeline;


import HddsProtos.ReplicationFactor.THREE;
import HddsProtos.ReplicationType.RATIS;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.chillmode.SCMChillModeManager;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.MockNodeManager;
import org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.PipelineReportFromDatanode;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases to verify PipelineManager.
 */
public class TestSCMPipelineManager {
    private static MockNodeManager nodeManager;

    private static File testDir;

    private static Configuration conf;

    @Test
    public void testPipelineReload() throws IOException {
        SCMPipelineManager pipelineManager = new SCMPipelineManager(TestSCMPipelineManager.conf, TestSCMPipelineManager.nodeManager, new EventQueue());
        PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(TestSCMPipelineManager.nodeManager, pipelineManager.getStateManager(), TestSCMPipelineManager.conf);
        pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
        Set<Pipeline> pipelines = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            Pipeline pipeline = pipelineManager.createPipeline(RATIS, THREE);
            pipelines.add(pipeline);
        }
        pipelineManager.close();
        // new pipeline manager should be able to load the pipelines from the db
        pipelineManager = new SCMPipelineManager(TestSCMPipelineManager.conf, TestSCMPipelineManager.nodeManager, new EventQueue());
        mockRatisProvider = new MockRatisPipelineProvider(TestSCMPipelineManager.nodeManager, pipelineManager.getStateManager(), TestSCMPipelineManager.conf);
        pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
        for (Pipeline p : pipelines) {
            pipelineManager.openPipeline(p.getId());
        }
        List<Pipeline> pipelineList = pipelineManager.getPipelines(RATIS);
        Assert.assertEquals(pipelines, new HashSet(pipelineList));
        // clean up
        for (Pipeline pipeline : pipelines) {
            pipelineManager.finalizePipeline(pipeline.getId());
            pipelineManager.removePipeline(pipeline.getId());
        }
        pipelineManager.close();
    }

    @Test
    public void testRemovePipeline() throws IOException {
        SCMPipelineManager pipelineManager = new SCMPipelineManager(TestSCMPipelineManager.conf, TestSCMPipelineManager.nodeManager, new EventQueue());
        PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(TestSCMPipelineManager.nodeManager, pipelineManager.getStateManager(), TestSCMPipelineManager.conf);
        pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
        Pipeline pipeline = pipelineManager.createPipeline(RATIS, THREE);
        pipelineManager.openPipeline(pipeline.getId());
        pipelineManager.addContainerToPipeline(pipeline.getId(), ContainerID.valueof(1));
        pipelineManager.finalizePipeline(pipeline.getId());
        pipelineManager.removeContainerFromPipeline(pipeline.getId(), ContainerID.valueof(1));
        pipelineManager.removePipeline(pipeline.getId());
        pipelineManager.close();
        // new pipeline manager should not be able to load removed pipelines
        pipelineManager = new SCMPipelineManager(TestSCMPipelineManager.conf, TestSCMPipelineManager.nodeManager, new EventQueue());
        try {
            pipelineManager.getPipeline(pipeline.getId());
            Assert.fail("Pipeline should not have been retrieved");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("not found"));
        }
        // clean up
        pipelineManager.close();
    }

    @Test
    public void testPipelineReport() throws IOException {
        EventQueue eventQueue = new EventQueue();
        SCMPipelineManager pipelineManager = new SCMPipelineManager(TestSCMPipelineManager.conf, TestSCMPipelineManager.nodeManager, eventQueue);
        PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(TestSCMPipelineManager.nodeManager, pipelineManager.getStateManager(), TestSCMPipelineManager.conf);
        pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
        SCMChillModeManager scmChillModeManager = new SCMChillModeManager(new OzoneConfiguration(), new ArrayList(), pipelineManager, eventQueue);
        // create a pipeline in allocated state with no dns yet reported
        Pipeline pipeline = pipelineManager.createPipeline(RATIS, THREE);
        Assert.assertFalse(pipelineManager.getPipeline(pipeline.getId()).isHealthy());
        Assert.assertTrue(pipelineManager.getPipeline(pipeline.getId()).isOpen());
        // get pipeline report from each dn in the pipeline
        PipelineReportHandler pipelineReportHandler = new PipelineReportHandler(scmChillModeManager, pipelineManager, TestSCMPipelineManager.conf);
        for (DatanodeDetails dn : pipeline.getNodes()) {
            PipelineReportFromDatanode pipelineReportFromDatanode = TestUtils.getPipelineReportFromDatanode(dn, pipeline.getId());
            // pipeline is not healthy until all dns report
            Assert.assertFalse(pipelineManager.getPipeline(pipeline.getId()).isHealthy());
            pipelineReportHandler.onMessage(pipelineReportFromDatanode, new EventQueue());
        }
        // pipeline is healthy when all dns report
        Assert.assertTrue(pipelineManager.getPipeline(pipeline.getId()).isHealthy());
        // pipeline should now move to open state
        Assert.assertTrue(pipelineManager.getPipeline(pipeline.getId()).isOpen());
        // close the pipeline
        pipelineManager.finalizePipeline(pipeline.getId());
        for (DatanodeDetails dn : pipeline.getNodes()) {
            PipelineReportFromDatanode pipelineReportFromDatanode = TestUtils.getPipelineReportFromDatanode(dn, pipeline.getId());
            // pipeline report for a closed pipeline should destroy the pipeline
            // and remove it from the pipeline manager
            pipelineReportHandler.onMessage(pipelineReportFromDatanode, new EventQueue());
        }
        try {
            pipelineManager.getPipeline(pipeline.getId());
            Assert.fail("Pipeline should not have been retrieved");
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("not found"));
        }
        // clean up
        pipelineManager.close();
    }
}

