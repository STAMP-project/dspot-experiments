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
import java.io.IOException;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.protocol.DatanodeDetails;
import org.apache.hadoop.hdds.scm.container.ContainerID;
import org.apache.hadoop.hdds.scm.container.ContainerManager;
import org.apache.hadoop.hdds.scm.container.common.helpers.ContainerWithPipeline;
import org.apache.hadoop.hdds.scm.server.StorageContainerManager;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for the Node2Pipeline map.
 */
public class TestNode2PipelineMap {
    private static MiniOzoneCluster cluster;

    private static OzoneConfiguration conf;

    private static StorageContainerManager scm;

    private static ContainerWithPipeline ratisContainer;

    private static ContainerManager containerManager;

    private static PipelineManager pipelineManager;

    @Test
    public void testPipelineMap() throws IOException {
        Set<ContainerID> set = TestNode2PipelineMap.pipelineManager.getContainersInPipeline(TestNode2PipelineMap.ratisContainer.getPipeline().getId());
        ContainerID cId = TestNode2PipelineMap.ratisContainer.getContainerInfo().containerID();
        Assert.assertEquals(1, set.size());
        set.forEach(( containerID) -> Assert.assertEquals(containerID, cId));
        List<DatanodeDetails> dns = TestNode2PipelineMap.ratisContainer.getPipeline().getNodes();
        Assert.assertEquals(3, dns.size());
        // get pipeline details by dnid
        Set<PipelineID> pipelines = TestNode2PipelineMap.scm.getScmNodeManager().getPipelines(dns.get(0));
        Assert.assertTrue(pipelines.contains(TestNode2PipelineMap.ratisContainer.getPipeline().getId()));
        // Now close the container and it should not show up while fetching
        // containers by pipeline
        TestNode2PipelineMap.containerManager.updateContainerState(cId, FINALIZE);
        TestNode2PipelineMap.containerManager.updateContainerState(cId, CLOSE);
        Set<ContainerID> set2 = TestNode2PipelineMap.pipelineManager.getContainersInPipeline(TestNode2PipelineMap.ratisContainer.getPipeline().getId());
        Assert.assertEquals(0, set2.size());
        TestNode2PipelineMap.pipelineManager.finalizePipeline(TestNode2PipelineMap.ratisContainer.getPipeline().getId());
        TestNode2PipelineMap.pipelineManager.removePipeline(TestNode2PipelineMap.ratisContainer.getPipeline().getId());
        pipelines = TestNode2PipelineMap.scm.getScmNodeManager().getPipelines(dns.get(0));
        Assert.assertFalse(pipelines.contains(TestNode2PipelineMap.ratisContainer.getPipeline().getId()));
    }
}

