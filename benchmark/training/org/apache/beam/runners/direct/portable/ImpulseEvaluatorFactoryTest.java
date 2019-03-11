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
package org.apache.beam.runners.direct.portable;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.INSTANCE;
import PTransformTranslation.IMPULSE_TRANSFORM_URN;
import RunnerApi.PCollection;
import RunnerApi.Pipeline;
import java.util.Collection;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.runners.core.construction.graph.PipelineNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PCollectionNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.apache.beam.runners.direct.ExecutableGraph;
import org.apache.beam.runners.direct.portable.ImpulseEvaluatorFactory.ImpulseRootProvider;
import org.apache.beam.runners.direct.portable.ImpulseEvaluatorFactory.ImpulseShard;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ImpulseEvaluatorFactory}.
 */
@RunWith(JUnit4.class)
public class ImpulseEvaluatorFactoryTest {
    private BundleFactory bundleFactory = ImmutableListBundleFactory.create();

    private PTransformNode impulseApplication = PipelineNode.pTransform("impulse", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN).build()).putOutputs("output", "impulse.out").build());

    private PCollectionNode impulseOut = PipelineNode.pCollection("impulse.out", PCollection.newBuilder().setUniqueName("impulse.out").build());

    private ExecutableGraph<PTransformNode, PCollectionNode> graph = PortableGraph.forPipeline(Pipeline.newBuilder().addRootTransformIds("impulse").setComponents(Components.newBuilder().putTransforms("impulse", impulseApplication.getTransform()).putPcollections("impulse.out", impulseOut.getPCollection())).build());

    @Test
    public void testImpulse() throws Exception {
        ImpulseEvaluatorFactory factory = new ImpulseEvaluatorFactory(graph, bundleFactory);
        WindowedValue<ImpulseShard> inputShard = WindowedValue.valueInGlobalWindow(new ImpulseShard());
        CommittedBundle<ImpulseShard> inputShardBundle = bundleFactory.<ImpulseShard>createRootBundle().add(inputShard).commit(Instant.now());
        TransformEvaluator<ImpulseShard> evaluator = factory.forApplication(impulseApplication, inputShardBundle);
        evaluator.processElement(inputShard);
        TransformResult<ImpulseShard> result = evaluator.finishBundle();
        Assert.assertThat("Exactly one output from a single ImpulseShard", Iterables.size(result.getOutputBundles()), Matchers.equalTo(1));
        UncommittedBundle<?> outputBundle = result.getOutputBundles().iterator().next();
        CommittedBundle<?> committedOutputBundle = outputBundle.commit(Instant.now());
        Assert.assertThat(committedOutputBundle.getMinimumTimestamp(), Matchers.equalTo(TIMESTAMP_MIN_VALUE));
        Assert.assertThat(committedOutputBundle.getPCollection(), Matchers.equalTo(impulseOut));
        Assert.assertThat("Should only be one impulse element", Iterables.size(committedOutputBundle.getElements()), Matchers.equalTo(1));
        Assert.assertThat(committedOutputBundle.getElements().iterator().next().getWindows(), Matchers.contains(INSTANCE));
        Assert.assertArrayEquals("Output should be an empty byte array", new byte[0], ((byte[]) (committedOutputBundle.getElements().iterator().next().getValue())));
    }

    @Test
    public void testRootProvider() {
        ImpulseRootProvider rootProvider = new ImpulseRootProvider(bundleFactory);
        Collection<? extends CommittedBundle<?>> inputs = rootProvider.getInitialInputs(impulseApplication, 100);
        Assert.assertThat("Only one impulse bundle per application", inputs, Matchers.hasSize(1));
        Assert.assertThat("Only one impulse shard per bundle", Iterables.size(getElements()), Matchers.equalTo(1));
    }
}

