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


import ExecutableStage.URN;
import PTransformTranslation.ASSIGN_WINDOWS_TRANSFORM_URN;
import PTransformTranslation.IMPULSE_TRANSFORM_URN;
import PTransformTranslation.PAR_DO_TRANSFORM_URN;
import java.util.Collections;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.model.pipeline.v1.RunnerApi.ExecutableStagePayload;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.PCollection;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.model.pipeline.v1.RunnerApi.ParDoPayload;
import org.apache.beam.model.pipeline.v1.RunnerApi.SdkFunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.SideInput;
import org.apache.beam.model.pipeline.v1.RunnerApi.StateSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.TimerSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.WindowIntoPayload;
import org.apache.beam.runners.core.construction.Environments;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.apache.beam.runners.core.construction.org.apache.beam.runners.core.construction.Environments;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for the default and static methods of {@link ExecutableStage}.
 */
@RunWith(JUnit4.class)
public class ExecutableStageTest {
    @Test
    public void testRoundTripToFromTransform() throws Exception {
        Environment env = org.apache.beam.runners.core.construction.Environments.createDockerEnvironment("foo");
        PTransform pt = PTransform.newBuilder().putInputs("input", "input.out").putInputs("side_input", "sideInput.in").putInputs("timer", "timer.out").putOutputs("output", "output.out").putOutputs("timer", "timer.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("foo")).putSideInputs("side_input", SideInput.getDefaultInstance()).putStateSpecs("user_state", StateSpec.getDefaultInstance()).putTimerSpecs("timer", TimerSpec.getDefaultInstance()).build().toByteString())).build();
        PCollection input = PCollection.newBuilder().setUniqueName("input.out").build();
        PCollection sideInput = PCollection.newBuilder().setUniqueName("sideInput.in").build();
        PCollection timer = PCollection.newBuilder().setUniqueName("timer.out").build();
        PCollection output = PCollection.newBuilder().setUniqueName("output.out").build();
        Components components = Components.newBuilder().putTransforms("pt", pt).putPcollections("input.out", input).putPcollections("sideInput.in", sideInput).putPcollections("timer.out", timer).putPcollections("output.out", output).putEnvironments("foo", env).build();
        PTransformNode transformNode = PipelineNode.pTransform("pt", pt);
        SideInputReference sideInputRef = SideInputReference.of(transformNode, "side_input", PipelineNode.pCollection("sideInput.in", sideInput));
        UserStateReference userStateRef = UserStateReference.of(transformNode, "user_state", PipelineNode.pCollection("input.out", input));
        TimerReference timerRef = TimerReference.of(transformNode, "timer");
        ImmutableExecutableStage stage = ImmutableExecutableStage.of(components, env, PipelineNode.pCollection("input.out", input), Collections.singleton(sideInputRef), Collections.singleton(userStateRef), Collections.singleton(timerRef), Collections.singleton(PipelineNode.pTransform("pt", pt)), Collections.singleton(PipelineNode.pCollection("output.out", output)));
        PTransform stagePTransform = stage.toPTransform("foo");
        Assert.assertThat(stagePTransform.getOutputsMap(), Matchers.hasValue("output.out"));
        Assert.assertThat(stagePTransform.getOutputsCount(), Matchers.equalTo(1));
        Assert.assertThat(stagePTransform.getInputsMap(), Matchers.allOf(Matchers.hasValue("input.out"), Matchers.hasValue("sideInput.in")));
        Assert.assertThat(stagePTransform.getInputsCount(), Matchers.equalTo(2));
        ExecutableStagePayload payload = ExecutableStagePayload.parseFrom(stagePTransform.getSpec().getPayload());
        Assert.assertThat(payload.getTransformsList(), Matchers.contains("pt"));
        Assert.assertThat(ExecutableStage.fromPayload(payload), Matchers.equalTo(stage));
    }

    @Test
    public void testRoundTripToFromTransformFused() throws Exception {
        PTransform parDoTransform = PTransform.newBuilder().putInputs("input", "impulse.out").putOutputs("output", "parDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).build().toByteString())).build();
        PTransform windowTransform = PTransform.newBuilder().putInputs("input", "impulse.out").putOutputs("output", "window.out").setSpec(FunctionSpec.newBuilder().setUrn(ASSIGN_WINDOWS_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).build().toByteString())).build();
        Components components = Components.newBuilder().putTransforms("impulse", PTransform.newBuilder().putOutputs("output", "impulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("impulse.out", PCollection.newBuilder().setUniqueName("impulse.out").build()).putTransforms("parDo", parDoTransform).putPcollections("parDo.out", PCollection.newBuilder().setUniqueName("parDo.out").build()).putTransforms("window", windowTransform).putPcollections("window.out", PCollection.newBuilder().setUniqueName("window.out").build()).putEnvironments("common", Environments.createDockerEnvironment("common")).build();
        QueryablePipeline p = QueryablePipeline.forPrimitivesIn(components);
        ExecutableStage subgraph = GreedyStageFuser.forGrpcPortRead(p, PipelineNode.pCollection("impulse.out", PCollection.newBuilder().setUniqueName("impulse.out").build()), ImmutableSet.of(PipelineNode.pTransform("parDo", parDoTransform), PipelineNode.pTransform("window", windowTransform)));
        PTransform ptransform = subgraph.toPTransform("foo");
        Assert.assertThat(ptransform.getSpec().getUrn(), Matchers.equalTo(URN));
        Assert.assertThat(ptransform.getInputsMap().values(), Matchers.containsInAnyOrder("impulse.out"));
        Assert.assertThat(ptransform.getOutputsMap().values(), Matchers.emptyIterable());
        ExecutableStagePayload payload = ExecutableStagePayload.parseFrom(ptransform.getSpec().getPayload());
        Assert.assertThat(payload.getTransformsList(), Matchers.contains("parDo", "window"));
        ExecutableStage desered = ExecutableStage.fromPayload(payload);
        Assert.assertThat(desered, Matchers.equalTo(subgraph));
    }
}

