/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.??See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.??The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 *  with the License.??You may obtain a copy of the License at
 *
 * ???? http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.chillmode;


import GenericTestUtils.LogCapturer;
import HddsConfigKeys.HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK;
import HddsConfigKeys.OZONE_METADATA_DIRS;
import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationFactor.THREE;
import HddsProtos.ReplicationType.RATIS;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.HddsTestUtils;
import org.apache.hadoop.hdds.scm.container.ContainerInfo;
import org.apache.hadoop.hdds.scm.container.MockNodeManager;
import org.apache.hadoop.hdds.scm.pipeline.MockRatisPipelineProvider;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.PipelineProvider;
import org.apache.hadoop.hdds.scm.pipeline.SCMPipelineManager;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;


/**
 * This class tests HealthyPipelineChillMode rule.
 */
public class TestHealthyPipelineChillModeRule {
    @Test
    public void testHealthyPipelineChillModeRuleWithNoPipelines() throws Exception {
        String storageDir = GenericTestUtils.getTempPath(((TestHealthyPipelineChillModeRule.class.getName()) + (UUID.randomUUID())));
        try {
            EventQueue eventQueue = new EventQueue();
            List<ContainerInfo> containers = new ArrayList<>();
            containers.addAll(HddsTestUtils.getContainerInfo(1));
            OzoneConfiguration config = new OzoneConfiguration();
            MockNodeManager nodeManager = new MockNodeManager(true, 0);
            config.set(OZONE_METADATA_DIRS, storageDir);
            // enable pipeline check
            config.setBoolean(HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK, true);
            SCMPipelineManager pipelineManager = new SCMPipelineManager(config, nodeManager, eventQueue);
            PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(nodeManager, pipelineManager.getStateManager(), config);
            pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
            SCMChillModeManager scmChillModeManager = new SCMChillModeManager(config, containers, pipelineManager, eventQueue);
            HealthyPipelineChillModeRule healthyPipelineChillModeRule = scmChillModeManager.getHealthyPipelineChillModeRule();
            // This should be immediately satisfied, as no pipelines are there yet.
            Assert.assertTrue(healthyPipelineChillModeRule.validate());
        } finally {
            FileUtil.fullyDelete(new File(storageDir));
        }
    }

    @Test
    public void testHealthyPipelineChillModeRuleWithPipelines() throws Exception {
        String storageDir = GenericTestUtils.getTempPath(((TestHealthyPipelineChillModeRule.class.getName()) + (UUID.randomUUID())));
        try {
            EventQueue eventQueue = new EventQueue();
            List<ContainerInfo> containers = new ArrayList<>();
            containers.addAll(HddsTestUtils.getContainerInfo(1));
            OzoneConfiguration config = new OzoneConfiguration();
            // In Mock Node Manager, first 8 nodes are healthy, next 2 nodes are
            // stale and last one is dead, and this repeats. So for a 12 node, 9
            // healthy, 2 stale and one dead.
            MockNodeManager nodeManager = new MockNodeManager(true, 12);
            config.set(OZONE_METADATA_DIRS, storageDir);
            // enable pipeline check
            config.setBoolean(HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK, true);
            SCMPipelineManager pipelineManager = new SCMPipelineManager(config, nodeManager, eventQueue);
            PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(nodeManager, pipelineManager.getStateManager(), config);
            pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
            // Create 3 pipelines
            Pipeline pipeline1 = pipelineManager.createPipeline(RATIS, THREE);
            Pipeline pipeline2 = pipelineManager.createPipeline(RATIS, THREE);
            Pipeline pipeline3 = pipelineManager.createPipeline(RATIS, THREE);
            SCMChillModeManager scmChillModeManager = new SCMChillModeManager(config, containers, pipelineManager, eventQueue);
            HealthyPipelineChillModeRule healthyPipelineChillModeRule = scmChillModeManager.getHealthyPipelineChillModeRule();
            // No datanodes have sent pipelinereport from datanode
            Assert.assertFalse(healthyPipelineChillModeRule.validate());
            // Fire pipeline report from all datanodes in first pipeline, as here we
            // have 3 pipelines, 10% is 0.3, when doing ceil it is 1. So, we should
            // validate should return true after fire pipeline event
            // Here testing with out pipelinereport handler, so not moving created
            // pipelines to allocated state, as pipelines changing to healthy is
            // handled by pipeline report handler. So, leaving pipeline's in pipeline
            // manager in open state for test case simplicity.
            firePipelineEvent(pipeline1, eventQueue);
            GenericTestUtils.waitFor(() -> healthyPipelineChillModeRule.validate(), 1000, 5000);
        } finally {
            FileUtil.fullyDelete(new File(storageDir));
        }
    }

    @Test
    public void testHealthyPipelineChillModeRuleWithMixedPipelines() throws Exception {
        String storageDir = GenericTestUtils.getTempPath(((TestHealthyPipelineChillModeRule.class.getName()) + (UUID.randomUUID())));
        try {
            EventQueue eventQueue = new EventQueue();
            List<ContainerInfo> containers = new ArrayList<>();
            containers.addAll(HddsTestUtils.getContainerInfo(1));
            OzoneConfiguration config = new OzoneConfiguration();
            // In Mock Node Manager, first 8 nodes are healthy, next 2 nodes are
            // stale and last one is dead, and this repeats. So for a 12 node, 9
            // healthy, 2 stale and one dead.
            MockNodeManager nodeManager = new MockNodeManager(true, 12);
            config.set(OZONE_METADATA_DIRS, storageDir);
            // enable pipeline check
            config.setBoolean(HDDS_SCM_CHILLMODE_PIPELINE_AVAILABILITY_CHECK, true);
            SCMPipelineManager pipelineManager = new SCMPipelineManager(config, nodeManager, eventQueue);
            PipelineProvider mockRatisProvider = new MockRatisPipelineProvider(nodeManager, pipelineManager.getStateManager(), config);
            pipelineManager.setPipelineProvider(RATIS, mockRatisProvider);
            // Create 3 pipelines
            Pipeline pipeline1 = pipelineManager.createPipeline(RATIS, ONE);
            Pipeline pipeline2 = pipelineManager.createPipeline(RATIS, THREE);
            Pipeline pipeline3 = pipelineManager.createPipeline(RATIS, THREE);
            SCMChillModeManager scmChillModeManager = new SCMChillModeManager(config, containers, pipelineManager, eventQueue);
            HealthyPipelineChillModeRule healthyPipelineChillModeRule = scmChillModeManager.getHealthyPipelineChillModeRule();
            // No datanodes have sent pipelinereport from datanode
            Assert.assertFalse(healthyPipelineChillModeRule.validate());
            GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LoggerFactory.getLogger(SCMChillModeManager.class));
            // fire event with pipeline report with ratis type and factor 1
            // pipeline, validate() should return false
            firePipelineEvent(pipeline1, eventQueue);
            GenericTestUtils.waitFor(() -> logCapturer.getOutput().contains("reported count is 0"), 1000, 5000);
            Assert.assertFalse(healthyPipelineChillModeRule.validate());
            firePipelineEvent(pipeline2, eventQueue);
            firePipelineEvent(pipeline3, eventQueue);
            GenericTestUtils.waitFor(() -> healthyPipelineChillModeRule.validate(), 1000, 5000);
        } finally {
            FileUtil.fullyDelete(new File(storageDir));
        }
    }
}

