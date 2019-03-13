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
package org.apache.beam.runners.core.construction.graph;


import java.io.Serializable;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.WithKeys;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FusedPipeline}.
 */
@RunWith(JUnit4.class)
public class FusedPipelineTest implements Serializable {
    @Test
    public void testToProto() {
        Pipeline p = Pipeline.create();
        p.apply("impulse", Impulse.create()).apply("map", MapElements.into(TypeDescriptors.integers()).via(( bytes) -> bytes.length)).apply("key", WithKeys.of("foo")).apply("gbk", GroupByKey.create()).apply("values", Values.create());
        RunnerApi.Pipeline protoPipeline = PipelineTranslation.toProto(p);
        checkState(protoPipeline.getRootTransformIdsList().containsAll(ImmutableList.of("impulse", "map", "key", "gbk", "values")), "Unexpected Root Transform IDs %s", protoPipeline.getRootTransformIdsList());
        FusedPipeline fused = GreedyPipelineFuser.fuse(protoPipeline);
        checkState(((fused.getRunnerExecutedTransforms().size()) == 2), "Unexpected number of runner transforms %s", fused.getRunnerExecutedTransforms());
        checkState(((fused.getFusedStages().size()) == 2), "Unexpected number of fused stages %s", fused.getFusedStages());
        RunnerApi.Pipeline fusedPipelineProto = fused.toPipeline();
        Assert.assertThat("Root Transforms should all be present in the Pipeline Components", fusedPipelineProto.getComponents().getTransformsMap().keySet(), Matchers.hasItems(fusedPipelineProto.getRootTransformIdsList().toArray(new String[0])));
        Assert.assertThat("Should contain Impulse, GroupByKey, and two Environment Stages", fusedPipelineProto.getRootTransformIdsCount(), Matchers.equalTo(4));
        Assert.assertThat(fusedPipelineProto.getRootTransformIdsList(), Matchers.hasItems("impulse", "gbk"));
        assertRootsInTopologicalOrder(fusedPipelineProto);
        // Since MapElements, WithKeys, and Values are all composites of a ParDo, we do prefix matching
        // instead of looking at the inside of their expansions
        Assert.assertThat("Fused transforms should be present in the components", fusedPipelineProto.getComponents().getTransformsMap(), Matchers.allOf(Matchers.hasKey(Matchers.startsWith("map")), Matchers.hasKey(Matchers.startsWith("key")), Matchers.hasKey(Matchers.startsWith("values"))));
        Assert.assertThat("Fused transforms shouldn't be present in the root IDs", fusedPipelineProto.getRootTransformIdsList(), Matchers.not(Matchers.hasItems(Matchers.startsWith("map"), Matchers.startsWith("key"), Matchers.startsWith("values"))));
        // The other components should be those of the original pipeline.
        Assert.assertThat(fusedPipelineProto.getComponents().getCodersMap(), Matchers.equalTo(protoPipeline.getComponents().getCodersMap()));
        Assert.assertThat(fusedPipelineProto.getComponents().getWindowingStrategiesMap(), Matchers.equalTo(protoPipeline.getComponents().getWindowingStrategiesMap()));
        Assert.assertThat(fusedPipelineProto.getComponents().getEnvironmentsMap(), Matchers.equalTo(protoPipeline.getComponents().getEnvironmentsMap()));
        Assert.assertThat(fusedPipelineProto.getComponents().getPcollectionsMap(), Matchers.equalTo(protoPipeline.getComponents().getPcollectionsMap()));
    }
}

