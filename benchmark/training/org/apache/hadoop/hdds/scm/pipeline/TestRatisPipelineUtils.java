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
package org.apache.hadoop.hdds.scm.pipeline;


import HddsProtos.ReplicationFactor.THREE;
import HddsProtos.ReplicationType.RATIS;
import Pipeline.PipelineState.OPEN;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.hdds.conf.OzoneConfiguration;
import org.apache.hadoop.hdds.scm.ScmConfigKeys;
import org.apache.hadoop.io.MultipleIOException;
import org.apache.hadoop.ozone.HddsDatanodeService;
import org.apache.hadoop.ozone.MiniOzoneCluster;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for RatisPipelineUtils.
 */
public class TestRatisPipelineUtils {
    private static MiniOzoneCluster cluster;

    private OzoneConfiguration conf = new OzoneConfiguration();

    private static PipelineManager pipelineManager;

    @Test(timeout = 30000)
    public void testAutomaticPipelineCreationOnPipelineDestroy() throws Exception {
        init(6);
        // make sure two pipelines are created
        waitForPipelines(2);
        List<Pipeline> pipelines = TestRatisPipelineUtils.pipelineManager.getPipelines(RATIS, THREE, OPEN);
        for (Pipeline pipeline : pipelines) {
            RatisPipelineUtils.finalizeAndDestroyPipeline(TestRatisPipelineUtils.pipelineManager, pipeline, conf, false);
        }
        // make sure two pipelines are created
        waitForPipelines(2);
    }

    @Test(timeout = 30000)
    public void testPipelineCreationOnNodeRestart() throws Exception {
        conf.setTimeDuration(ScmConfigKeys.OZONE_SCM_STALENODE_INTERVAL, 5, TimeUnit.SECONDS);
        init(3);
        // make sure a pipelines is created
        waitForPipelines(1);
        List<HddsDatanodeService> dns = new java.util.ArrayList(TestRatisPipelineUtils.cluster.getHddsDatanodes());
        List<Pipeline> pipelines = TestRatisPipelineUtils.pipelineManager.getPipelines(RATIS, THREE);
        for (HddsDatanodeService dn : dns) {
            TestRatisPipelineUtils.cluster.shutdownHddsDatanode(dn.getDatanodeDetails());
        }
        // try creating another pipeline now
        try {
            RatisPipelineUtils.createPipeline(pipelines.get(0), conf);
            Assert.fail("pipeline creation should fail after shutting down pipeline");
        } catch (IOException ioe) {
            // in case the pipeline creation fails, MultipleIOException is thrown
            Assert.assertTrue((ioe instanceof MultipleIOException));
        }
        // make sure pipelines is destroyed
        waitForPipelines(0);
        for (HddsDatanodeService dn : dns) {
            TestRatisPipelineUtils.cluster.restartHddsDatanode(dn.getDatanodeDetails(), false);
        }
        // make sure pipelines is created after node start
        waitForPipelines(1);
    }
}

