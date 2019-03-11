/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdds.scm.pipeline;


import HddsProtos.LifeCycleEvent.CLOSE;
import HddsProtos.LifeCycleEvent.FINALIZE;
import Pipeline.PipelineState.CLOSED;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.protocol.proto.StorageContainerDatanodeProtocolProtos.PipelineReport;
import org.apache.hadoop.hdds.scm.TestUtils;
import org.apache.hadoop.hdds.scm.chillmode.SCMChillModeManager;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerManager;
import org.apache.hadoop.hdds.scm.container.common.helpers.ContainerWithPipeline;
import org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.PipelineActionsFromDatanode;
import org.apache.hadoop.hdds.scm.server.SCMDatanodeHeartbeatDispatcher.PipelineReportFromDatanode;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.apache.hadoop.ozone.container.ozoneimpl.OzoneContainer;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for Pipeline Closing.
 */
public class TestPipelineClose {
    private MiniOzoneCluster cluster;

    private OzoneConfiguration conf;

    private StorageContainerManager scm;

    private ContainerWithPipeline ratisContainer;

    private ContainerManager containerManager;

    private PipelineManager pipelineManager;

    private long pipelineDestroyTimeoutInMillis;

    @Test
    public void testPipelineCloseWithClosedContainer() throws IOException {
        Set<ContainerID> set = pipelineManager.getContainersInPipeline(ratisContainer.getPipeline().getId());
        ContainerID cId = ratisContainer.getContainerInfo().containerID();
        Assert.assertEquals(1, set.size());
        set.forEach(( containerID) -> Assert.assertEquals(containerID, cId));
        // Now close the container and it should not show up while fetching
        // containers by pipeline
        containerManager.updateContainerState(cId, FINALIZE);
        containerManager.updateContainerState(cId, CLOSE);
        Set<ContainerID> setClosed = pipelineManager.getContainersInPipeline(ratisContainer.getPipeline().getId());
        Assert.assertEquals(0, setClosed.size());
        pipelineManager.finalizePipeline(ratisContainer.getPipeline().getId());
        Pipeline pipeline1 = pipelineManager.getPipeline(ratisContainer.getPipeline().getId());
        Assert.assertEquals(CLOSED, pipeline1.getPipelineState());
        pipelineManager.removePipeline(pipeline1.getId());
        for (DatanodeDetails dn : ratisContainer.getPipeline().getNodes()) {
            // Assert that the pipeline has been removed from Node2PipelineMap as well
            Assert.assertFalse(scm.getScmNodeManager().getPipelines(dn).contains(ratisContainer.getPipeline().getId()));
        }
    }

    @Test
    public void testPipelineCloseWithOpenContainer() throws IOException, InterruptedException, TimeoutException {
        Set<ContainerID> setOpen = pipelineManager.getContainersInPipeline(ratisContainer.getPipeline().getId());
        Assert.assertEquals(1, setOpen.size());
        ContainerID cId2 = ratisContainer.getContainerInfo().containerID();
        pipelineManager.finalizePipeline(ratisContainer.getPipeline().getId());
        Assert.assertEquals(CLOSED, pipelineManager.getPipeline(ratisContainer.getPipeline().getId()).getPipelineState());
        Pipeline pipeline2 = pipelineManager.getPipeline(ratisContainer.getPipeline().getId());
        Assert.assertEquals(CLOSED, pipeline2.getPipelineState());
    }

    @Test
    public void testPipelineCloseWithPipelineAction() throws Exception {
        List<DatanodeDetails> dns = ratisContainer.getPipeline().getNodes();
        PipelineActionsFromDatanode pipelineActionsFromDatanode = TestUtils.getPipelineActionFromDatanode(dns.get(0), ratisContainer.getPipeline().getId());
        // send closing action for pipeline
        PipelineActionHandler pipelineActionHandler = new PipelineActionHandler(pipelineManager, conf);
        pipelineActionHandler.onMessage(pipelineActionsFromDatanode, new EventQueue());
        Thread.sleep(((int) ((pipelineDestroyTimeoutInMillis) * 1.2)));
        OzoneContainer ozoneContainer = cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer();
        List<PipelineReport> pipelineReports = ozoneContainer.getPipelineReport().getPipelineReportList();
        for (PipelineReport pipelineReport : pipelineReports) {
            // ensure the pipeline is not reported by any dn
            Assert.assertNotEquals(PipelineID.getFromProtobuf(pipelineReport.getPipelineID()), ratisContainer.getPipeline().getId());
        }
        try {
            pipelineManager.getPipeline(ratisContainer.getPipeline().getId());
            Assert.fail("Pipeline should not exist in SCM");
        } catch (PipelineNotFoundException e) {
        }
    }

    @Test
    public void testPipelineCloseWithPipelineReport() throws IOException {
        Pipeline pipeline = ratisContainer.getPipeline();
        pipelineManager.finalizePipeline(pipeline.getId());
        // remove pipeline from SCM
        pipelineManager.removePipeline(pipeline.getId());
        for (DatanodeDetails dn : pipeline.getNodes()) {
            PipelineReportFromDatanode pipelineReport = TestUtils.getPipelineReportFromDatanode(dn, pipeline.getId());
            EventQueue eventQueue = new EventQueue();
            SCMChillModeManager scmChillModeManager = new SCMChillModeManager(new OzoneConfiguration(), new ArrayList(), pipelineManager, eventQueue);
            PipelineReportHandler pipelineReportHandler = new PipelineReportHandler(scmChillModeManager, pipelineManager, conf);
            // on receiving pipeline report for the pipeline, pipeline report handler
            // should destroy the pipeline for the dn
            pipelineReportHandler.onMessage(pipelineReport, eventQueue);
        }
        OzoneContainer ozoneContainer = cluster.getHddsDatanodes().get(0).getDatanodeStateMachine().getContainer();
        List<PipelineReport> pipelineReports = ozoneContainer.getPipelineReport().getPipelineReportList();
        for (PipelineReport pipelineReport : pipelineReports) {
            // pipeline should not be reported by any dn
            Assert.assertNotEquals(PipelineID.getFromProtobuf(pipelineReport.getPipelineID()), ratisContainer.getPipeline().getId());
        }
    }
}

