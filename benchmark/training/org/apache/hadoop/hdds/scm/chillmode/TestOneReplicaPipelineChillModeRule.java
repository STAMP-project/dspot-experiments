/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership.  The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.hdds.scm.chillmode;


import GenericTestUtils.LogCapturer;
import HddsProtos.ReplicationFactor.ONE;
import HddsProtos.ReplicationFactor.THREE;
import HddsProtos.ReplicationType.RATIS;
import java.util.List;
import org.apache.hadoop.hdds.scm.pipeline.Pipeline;
import org.apache.hadoop.hdds.scm.pipeline.SCMPipelineManager;
import org.apache.hadoop.hdds.server.events.EventQueue;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.LoggerFactory;


/**
 * This class tests OneReplicaPipelineChillModeRule.
 */
public class TestOneReplicaPipelineChillModeRule {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private OneReplicaPipelineChillModeRule rule;

    private SCMPipelineManager pipelineManager;

    private EventQueue eventQueue;

    @Test
    public void testOneReplicaPipelineRule() throws Exception {
        // As with 30 nodes, We can create 7 pipelines with replication factor 3.
        // (This is because in node manager for every 10 nodes, 7 nodes are
        // healthy, 2 are stale one is dead.)
        int nodes = 30;
        int pipelineFactorThreeCount = 7;
        int pipelineCountOne = 0;
        setup(nodes, pipelineFactorThreeCount, pipelineCountOne);
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LoggerFactory.getLogger(SCMChillModeManager.class));
        List<Pipeline> pipelines = pipelineManager.getPipelines();
        for (int i = 0; i < (pipelineFactorThreeCount - 1); i++) {
            firePipelineEvent(pipelines.get(i));
        }
        // As 90% of 7 with ceil is 7, if we send 6 pipeline reports, rule
        // validate should be still false.
        GenericTestUtils.waitFor(() -> logCapturer.getOutput().contains("reported count is 6"), 1000, 5000);
        Assert.assertFalse(rule.validate());
        // Fire last pipeline event from datanode.
        firePipelineEvent(pipelines.get((pipelineFactorThreeCount - 1)));
        GenericTestUtils.waitFor(() -> rule.validate(), 1000, 5000);
    }

    @Test
    public void testOneReplicaPipelineRuleMixedPipelines() throws Exception {
        // As with 30 nodes, We can create 7 pipelines with replication factor 3.
        // (This is because in node manager for every 10 nodes, 7 nodes are
        // healthy, 2 are stale one is dead.)
        int nodes = 30;
        int pipelineCountThree = 7;
        int pipelineCountOne = 21;
        setup(nodes, pipelineCountThree, pipelineCountOne);
        GenericTestUtils.LogCapturer logCapturer = LogCapturer.captureLogs(LoggerFactory.getLogger(SCMChillModeManager.class));
        List<Pipeline> pipelines = pipelineManager.getPipelines(RATIS, ONE);
        for (int i = 0; i < pipelineCountOne; i++) {
            firePipelineEvent(pipelines.get(i));
        }
        GenericTestUtils.waitFor(() -> logCapturer.getOutput().contains("reported count is 0"), 1000, 5000);
        // fired events for one node ratis pipeline, so we will be still false.
        Assert.assertFalse(rule.validate());
        pipelines = pipelineManager.getPipelines(RATIS, THREE);
        for (int i = 0; i < (pipelineCountThree - 1); i++) {
            firePipelineEvent(pipelines.get(i));
        }
        GenericTestUtils.waitFor(() -> logCapturer.getOutput().contains("reported count is 6"), 1000, 5000);
        // Fire last pipeline event from datanode.
        firePipelineEvent(pipelines.get((pipelineCountThree - 1)));
        GenericTestUtils.waitFor(() -> rule.validate(), 1000, 5000);
    }
}

