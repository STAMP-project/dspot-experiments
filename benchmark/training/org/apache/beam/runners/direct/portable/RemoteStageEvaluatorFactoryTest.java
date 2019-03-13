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


import ParDo.SingleOutput;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.runners.core.construction.graph.GreedyPipelineFuser;
import org.apache.beam.runners.core.construction.graph.PipelineNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PCollectionNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.apache.beam.runners.core.construction.graph.QueryablePipeline;
import org.apache.beam.runners.fnexecution.GrpcFnServer;
import org.apache.beam.runners.fnexecution.control.FnApiControlClientPoolService;
import org.apache.beam.runners.fnexecution.data.GrpcDataService;
import org.apache.beam.runners.fnexecution.logging.GrpcLoggingService;
import org.apache.beam.runners.fnexecution.state.GrpcStateService;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RemoteStageEvaluatorFactory}.
 */
@RunWith(JUnit4.class)
public class RemoteStageEvaluatorFactoryTest implements Serializable {
    private transient RemoteStageEvaluatorFactory factory;

    private transient ExecutorService executor;

    private transient GrpcFnServer<GrpcDataService> dataServer;

    private transient GrpcFnServer<GrpcStateService> stateServer;

    private transient GrpcFnServer<FnApiControlClientPoolService> controlServer;

    private transient GrpcFnServer<GrpcLoggingService> loggingServer;

    private transient BundleFactory bundleFactory;

    @Test
    public void executesRemoteStage() throws Exception {
        Pipeline p = Pipeline.create();
        p.apply("impulse", Impulse.create()).apply("CreateInputs", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], Integer>() {
            @ProcessElement
            public void create(ProcessContext ctxt) {
                ctxt.output(1);
                ctxt.output(2);
                ctxt.output(3);
            }
        })).apply("ParDo", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<Integer, KV<String, Long>>() {
            @ProcessElement
            public void proc(ProcessContext ctxt) {
                ctxt.output(KV.of("foo", ctxt.element().longValue()));
            }
        })).apply(GroupByKey.create());
        RunnerApi.Pipeline fusedPipeline = GreedyPipelineFuser.fuse(PipelineTranslation.toProto(p)).toPipeline();
        QueryablePipeline fusedQP = QueryablePipeline.forPipeline(fusedPipeline);
        PTransformNode impulseTransform = getOnlyElement(fusedQP.getRootTransforms());
        PCollectionNode impulseOutput = getOnlyElement(fusedQP.getOutputPCollections(impulseTransform));
        PTransformNode stage = fusedPipeline.getRootTransformIdsList().stream().map(( id) -> PipelineNode.pTransform(id, fusedPipeline.getComponents().getTransformsOrThrow(id))).filter(( node) -> node.getTransform().getSpec().getUrn().equals(ExecutableStage.URN)).findFirst().orElseThrow(IllegalArgumentException::new);
        WindowedValue<byte[]> impulse = WindowedValue.valueInGlobalWindow(new byte[0]);
        CommittedBundle<byte[]> inputBundle = bundleFactory.<byte[]>createBundle(impulseOutput).add(impulse).commit(Instant.now());
        TransformEvaluator<byte[]> evaluator = factory.forApplication(stage, inputBundle);
        evaluator.processElement(impulse);
        TransformResult<byte[]> result = evaluator.finishBundle();
        Assert.assertThat(Iterables.size(result.getOutputBundles()), Matchers.equalTo(1));
        CommittedBundle<?> outputs = getOnlyElement(result.getOutputBundles()).commit(Instant.now());
        Assert.assertThat(Iterables.size(outputs), Matchers.equalTo(3));
    }

    @Test
    public void executesStageWithFlatten() throws Exception {
        SingleOutput<byte[], KV<Integer, String>> parDo = ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], KV<Integer, String>>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
                ctxt.output(KV.of(1, "foo"));
                ctxt.output(KV.of(1, "bar"));
                ctxt.output(KV.of(2, "foo"));
            }
        });
        Pipeline p = Pipeline.create();
        PCollection<KV<Integer, String>> left = p.apply("left", Impulse.create()).apply(parDo);
        PCollection<KV<Integer, String>> right = p.apply("right", Impulse.create()).apply(parDo);
        PCollectionList.of(left).and(right).apply(Flatten.pCollections()).apply(GroupByKey.create());
        RunnerApi.Pipeline fusedPipeline = GreedyPipelineFuser.fuse(PipelineTranslation.toProto(p)).toPipeline();
        QueryablePipeline fusedQP = QueryablePipeline.forPipeline(fusedPipeline);
        PTransformNode leftRoot = null;
        PTransformNode rightRoot = null;
        for (PTransformNode root : fusedQP.getRootTransforms()) {
            if ("left".equals(root.getId())) {
                leftRoot = root;
            } else {
                rightRoot = root;
            }
        }
        checkState((leftRoot != null));
        checkState((rightRoot != null));
        PTransformNode stage = fusedPipeline.getRootTransformIdsList().stream().map(( id) -> PipelineNode.pTransform(id, fusedPipeline.getComponents().getTransformsOrThrow(id))).filter(( node) -> node.getTransform().getSpec().getUrn().equals(ExecutableStage.URN)).findFirst().orElseThrow(IllegalArgumentException::new);
        WindowedValue<byte[]> impulse = WindowedValue.valueInGlobalWindow(new byte[0]);
        String inputId = getOnlyElement(stage.getTransform().getInputsMap().values());
        CommittedBundle<byte[]> inputBundle = bundleFactory.<byte[]>createBundle(PipelineNode.pCollection(inputId, fusedPipeline.getComponents().getPcollectionsOrThrow(inputId))).add(impulse).commit(Instant.now());
        TransformEvaluator<byte[]> evaluator = factory.forApplication(stage, inputBundle);
        evaluator.processElement(impulse);
        TransformResult<byte[]> result = evaluator.finishBundle();
        Assert.assertThat(Iterables.size(result.getOutputBundles()), Matchers.equalTo(1));
        CommittedBundle<?> outputs = getOnlyElement(result.getOutputBundles()).commit(Instant.now());
        Assert.assertThat(Iterables.size(outputs), Matchers.equalTo(3));
    }
}

