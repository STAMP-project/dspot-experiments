/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdds.scm.pipeline;


import HddsProtos.ReplicationFactor;
import HddsProtos.ReplicationType.STAND_ALONE;
import Pipeline.PipelineState.OPEN;
import java.io.IOException;
import org.apache.hadoop.hdds.protocol.proto.HddsProtos;
import org.apache.hadoop.hdds.scm.node.NodeManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for SimplePipelineProvider.
 */
public class TestSimplePipelineProvider {
    private NodeManager nodeManager;

    private PipelineProvider provider;

    private PipelineStateManager stateManager;

    @Test
    public void testCreatePipelineWithFactor() throws IOException {
        HddsProtos.ReplicationFactor factor = ReplicationFactor.THREE;
        Pipeline pipeline = provider.create(factor);
        stateManager.addPipeline(pipeline);
        Assert.assertEquals(pipeline.getType(), STAND_ALONE);
        Assert.assertEquals(pipeline.getFactor(), factor);
        Assert.assertEquals(pipeline.getPipelineState(), OPEN);
        Assert.assertEquals(pipeline.getNodes().size(), factor.getNumber());
        factor = ReplicationFactor.ONE;
        Pipeline pipeline1 = provider.create(factor);
        stateManager.addPipeline(pipeline1);
        Assert.assertEquals(pipeline1.getType(), STAND_ALONE);
        Assert.assertEquals(pipeline1.getFactor(), factor);
        Assert.assertEquals(pipeline1.getPipelineState(), OPEN);
        Assert.assertEquals(pipeline1.getNodes().size(), factor.getNumber());
    }

    @Test
    public void testCreatePipelineWithNodes() throws IOException {
        HddsProtos.ReplicationFactor factor = ReplicationFactor.THREE;
        Pipeline pipeline = provider.create(factor, createListOfNodes(factor.getNumber()));
        Assert.assertEquals(pipeline.getType(), STAND_ALONE);
        Assert.assertEquals(pipeline.getFactor(), factor);
        Assert.assertEquals(pipeline.getPipelineState(), OPEN);
        Assert.assertEquals(pipeline.getNodes().size(), factor.getNumber());
        factor = ReplicationFactor.ONE;
        pipeline = provider.create(factor, createListOfNodes(factor.getNumber()));
        Assert.assertEquals(pipeline.getType(), STAND_ALONE);
        Assert.assertEquals(pipeline.getFactor(), factor);
        Assert.assertEquals(pipeline.getPipelineState(), OPEN);
        Assert.assertEquals(pipeline.getNodes().size(), factor.getNumber());
    }
}

