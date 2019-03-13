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


import PTransformTranslation.ASSIGN_WINDOWS_TRANSFORM_URN;
import PTransformTranslation.FLATTEN_TRANSFORM_URN;
import PTransformTranslation.GROUP_BY_KEY_TRANSFORM_URN;
import PTransformTranslation.IMPULSE_TRANSFORM_URN;
import PTransformTranslation.PAR_DO_TRANSFORM_URN;
import RunnerApi.ExecutableStagePayload.SideInputId;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.beam.model.pipeline.v1.RunnerApi.Coder;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.PCollection;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.model.pipeline.v1.RunnerApi.ParDoPayload;
import org.apache.beam.model.pipeline.v1.RunnerApi.Pipeline;
import org.apache.beam.model.pipeline.v1.RunnerApi.SdkFunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.SideInput;
import org.apache.beam.model.pipeline.v1.RunnerApi.StateSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.TimerSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.WindowIntoPayload;
import org.apache.beam.model.pipeline.v1.RunnerApi.WindowingStrategy;
import org.apache.beam.runners.core.construction.Environments;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GreedyPipelineFuser}.
 */
@RunWith(JUnit4.class)
public class GreedyPipelineFuserTest {
    // Contains the 'go' and 'py' environments, and a default 'impulse' step and output.
    private Components partialComponents;

    /* impulse -> .out -> read -> .out -> parDo -> .out -> window -> .out
    becomes
    (impulse.out) -> read -> read.out -> parDo -> parDo.out -> window
     */
    @Test
    public void singleEnvironmentBecomesASingleStage() {
        String name = "read.out";
        Components components = partialComponents.toBuilder().putTransforms("read", PTransform.newBuilder().setUniqueName("Read").putInputs("input", "impulse.out").putOutputs("output", "read.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("read.out", GreedyPipelineFuserTest.pc(name)).putTransforms("parDo", PTransform.newBuilder().setUniqueName("ParDo").putInputs("input", "read.out").putOutputs("output", "parDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("parDo.out", GreedyPipelineFuserTest.pc("parDo.out")).putTransforms("window", PTransform.newBuilder().setUniqueName("Window").putInputs("input", "parDo.out").putOutputs("output", "window.out").setSpec(FunctionSpec.newBuilder().setUrn(ASSIGN_WINDOWS_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("window.out", GreedyPipelineFuserTest.pc("window.out")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.contains(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.contains(ExecutableStageMatcher.withInput("impulse.out").withNoOutputs().withTransforms("read", "parDo", "window")));
    }

    /* impulse -> .out -> mystery -> .out
                    \
                     -> enigma -> .out
    becomes all runner-executed
     */
    @Test
    public void transformsWithNoEnvironmentBecomeRunnerExecuted() {
        Components components = partialComponents.toBuilder().putTransforms("mystery", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN)).setUniqueName("Mystery").putInputs("input", "impulse.out").putOutputs("output", "mystery.out").build()).putPcollections("mystery.out", GreedyPipelineFuserTest.pc("mystery.out")).putTransforms("enigma", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN)).setUniqueName("Enigma").putInputs("input", "impulse.out").putOutputs("output", "enigma.out").build()).putPcollections("enigma.out", GreedyPipelineFuserTest.pc("enigma.out")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse")), PipelineNode.pTransform("mystery", components.getTransformsOrThrow("mystery")), PipelineNode.pTransform("enigma", components.getTransformsOrThrow("enigma"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.emptyIterable());
    }

    /* impulse -> .out -> read -> .out -> groupByKey -> .out -> parDo -> .out
    becomes
    (impulse.out) -> read -> (read.out)
    (groupByKey.out) -> parDo
     */
    @Test
    public void singleEnvironmentAcrossGroupByKeyMultipleStages() {
        Components components = partialComponents.toBuilder().putTransforms("read", PTransform.newBuilder().setUniqueName("Read").putInputs("input", "impulse.out").putOutputs("output", "read.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("read.out", GreedyPipelineFuserTest.pc("read.out")).putTransforms("groupByKey", PTransform.newBuilder().setUniqueName("GroupByKey").putInputs("input", "read.out").putOutputs("output", "groupByKey.out").setSpec(FunctionSpec.newBuilder().setUrn(GROUP_BY_KEY_TRANSFORM_URN)).build()).putPcollections("groupByKey.out", GreedyPipelineFuserTest.pc("groupByKey.out")).putTransforms("parDo", PTransform.newBuilder().setUniqueName("ParDo").putInputs("input", "groupByKey.out").putOutputs("output", "parDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("parDo.out", GreedyPipelineFuserTest.pc("parDo.out")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse")), PipelineNode.pTransform("groupByKey", components.getTransformsOrThrow("groupByKey"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("impulse.out").withOutputs("read.out").withTransforms("read"), ExecutableStageMatcher.withInput("groupByKey.out").withNoOutputs().withTransforms("parDo")));
    }

    /* impulse -> .out -> read -> .out --> goTransform -> .out
                                     \
                                      -> pyTransform -> .out
    becomes (impulse.out) -> read -> (read.out)
            (read.out) -> goTransform
            (read.out) -> pyTransform
     */
    @Test
    public void multipleEnvironmentsBecomesMultipleStages() {
        Components components = partialComponents.toBuilder().putTransforms("read", PTransform.newBuilder().setUniqueName("Read").putInputs("input", "impulse.out").putOutputs("output", "read.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("read.out", GreedyPipelineFuserTest.pc("read.out")).putTransforms("goTransform", PTransform.newBuilder().setUniqueName("GoTransform").putInputs("input", "read.out").putOutputs("output", "go.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("go")).build().toByteString())).build()).putPcollections("go.out", GreedyPipelineFuserTest.pc("go.out")).putTransforms("pyTransform", PTransform.newBuilder().setUniqueName("PyTransform").putInputs("input", "read.out").putOutputs("output", "py.out").setSpec(FunctionSpec.newBuilder().setUrn(ASSIGN_WINDOWS_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("py.out", GreedyPipelineFuserTest.pc("py.out")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        // Impulse is the runner transform
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.hasSize(1));
        Assert.assertThat(fused.getFusedStages(), Matchers.hasSize(3));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("impulse.out").withOutputs("read.out").withTransforms("read"), ExecutableStageMatcher.withInput("read.out").withNoOutputs().withTransforms("pyTransform"), ExecutableStageMatcher.withInput("read.out").withNoOutputs().withTransforms("goTransform")));
    }

    /* goImpulse -> .out -> goRead -> .out \                    -> goParDo -> .out
                                         -> flatten -> .out |
    pyImpulse -> .out -> pyRead -> .out /                    -> pyParDo -> .out

    becomes
    (goImpulse.out) -> goRead -> goRead.out -> flatten -> (flatten.out_synthetic0)
    (pyImpulse.out) -> pyRead -> pyRead.out -> flatten -> (flatten.out_synthetic1)
    flatten.out_synthetic0 & flatten.out_synthetic1 -> synthetic_flatten -> flatten.out
    (flatten.out) -> goParDo
    (flatten.out) -> pyParDo
     */
    @Test
    public void flattenWithHeterogenousInputsAndOutputsEntirelyMaterialized() {
        Components components = Components.newBuilder().putCoders("coder", Coder.newBuilder().build()).putCoders("windowCoder", Coder.newBuilder().build()).putWindowingStrategies("ws", WindowingStrategy.newBuilder().setWindowCoderId("windowCoder").build()).putTransforms("pyImpulse", PTransform.newBuilder().setUniqueName("PyImpulse").putOutputs("output", "pyImpulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("pyImpulse.out", GreedyPipelineFuserTest.pc("pyImpulse.out")).putTransforms("pyRead", PTransform.newBuilder().setUniqueName("PyRead").putInputs("input", "pyImpulse.out").putOutputs("output", "pyRead.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("pyRead.out", GreedyPipelineFuserTest.pc("pyRead.out")).putTransforms("goImpulse", PTransform.newBuilder().setUniqueName("GoImpulse").putOutputs("output", "goImpulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("goImpulse.out", GreedyPipelineFuserTest.pc("goImpulse.out")).putTransforms("goRead", PTransform.newBuilder().setUniqueName("GoRead").putInputs("input", "goImpulse.out").putOutputs("output", "goRead.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("go")).build().toByteString())).build()).putPcollections("goRead.out", GreedyPipelineFuserTest.pc("goRead.out")).putTransforms("flatten", PTransform.newBuilder().setUniqueName("Flatten").putInputs("goReadInput", "goRead.out").putInputs("pyReadInput", "pyRead.out").putOutputs("output", "flatten.out").setSpec(FunctionSpec.newBuilder().setUrn(FLATTEN_TRANSFORM_URN)).build()).putPcollections("flatten.out", GreedyPipelineFuserTest.pc("flatten.out")).putTransforms("pyParDo", PTransform.newBuilder().setUniqueName("PyParDo").putInputs("input", "flatten.out").putOutputs("output", "pyParDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("pyParDo.out", GreedyPipelineFuserTest.pc("pyParDo.out")).putTransforms("goParDo", PTransform.newBuilder().setUniqueName("GoParDo").putInputs("input", "flatten.out").putOutputs("output", "goParDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("go")).build().toByteString())).build()).putPcollections("goParDo.out", GreedyPipelineFuserTest.pc("goParDo.out")).putEnvironments("go", Environments.createDockerEnvironment("go")).putEnvironments("py", Environments.createDockerEnvironment("py")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.hasSize(3));
        Assert.assertThat("The runner should include the impulses for both languages, plus an introduced flatten", fused.getRunnerExecutedTransforms(), Matchers.hasItems(PipelineNode.pTransform("pyImpulse", components.getTransformsOrThrow("pyImpulse")), PipelineNode.pTransform("goImpulse", components.getTransformsOrThrow("goImpulse"))));
        PTransformNode flattenNode = null;
        for (PTransformNode runnerTransform : fused.getRunnerExecutedTransforms()) {
            if (getOnlyElement(runnerTransform.getTransform().getOutputsMap().values()).equals("flatten.out")) {
                flattenNode = runnerTransform;
            }
        }
        Assert.assertThat(flattenNode, Matchers.not(Matchers.nullValue()));
        Assert.assertThat(flattenNode.getTransform().getSpec().getUrn(), Matchers.equalTo(FLATTEN_TRANSFORM_URN));
        Assert.assertThat(new java.util.HashSet(flattenNode.getTransform().getInputsMap().values()), Matchers.hasSize(2));
        Collection<String> introducedOutputs = flattenNode.getTransform().getInputsMap().values();
        AnyOf<String> anyIntroducedPCollection = Matchers.anyOf(introducedOutputs.stream().map(Matchers::equalTo).collect(Collectors.toSet()));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("goImpulse.out").withOutputs(anyIntroducedPCollection).withTransforms("goRead", "flatten"), ExecutableStageMatcher.withInput("pyImpulse.out").withOutputs(anyIntroducedPCollection).withTransforms("pyRead", "flatten"), ExecutableStageMatcher.withInput("flatten.out").withNoOutputs().withTransforms("goParDo"), ExecutableStageMatcher.withInput("flatten.out").withNoOutputs().withTransforms("pyParDo")));
        Set<String> materializedStageOutputs = fused.getFusedStages().stream().flatMap(( executableStage) -> executableStage.getOutputPCollections().stream()).map(PCollectionNode::getId).collect(Collectors.toSet());
        Assert.assertThat("All materialized stage outputs should be flattened, and no more", materializedStageOutputs, Matchers.containsInAnyOrder(flattenNode.getTransform().getInputsMap().values().toArray(new String[0])));
    }

    /* impulseA -> .out -> goRead -> .out \
                                        -> flatten -> .out -> goParDo -> .out
    impulseB -> .out -> pyRead -> .out /

    becomes
    (impulseA.out) -> goRead -> goRead.out -> flatten -> flatten.out -> goParDo
    (impulseB.out) -> pyRead -> pyRead.out -> flatten -> (flatten.out)
    (flatten.out) -> goParDo
     */
    @Test
    public void flattenWithHeterogeneousInputsSingleEnvOutputPartiallyMaterialized() {
        Components components = Components.newBuilder().putCoders("coder", Coder.newBuilder().build()).putCoders("windowCoder", Coder.newBuilder().build()).putWindowingStrategies("ws", WindowingStrategy.newBuilder().setWindowCoderId("windowCoder").build()).putTransforms("pyImpulse", PTransform.newBuilder().setUniqueName("PyImpulse").putOutputs("output", "pyImpulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("pyImpulse.out", GreedyPipelineFuserTest.pc("pyImpulse.out")).putTransforms("pyRead", PTransform.newBuilder().setUniqueName("PyRead").putInputs("input", "pyImpulse.out").putOutputs("output", "pyRead.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("pyRead.out", GreedyPipelineFuserTest.pc("pyRead.out")).putTransforms("goImpulse", PTransform.newBuilder().setUniqueName("GoImpulse").putOutputs("output", "goImpulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("goImpulse.out", GreedyPipelineFuserTest.pc("goImpulse.out")).putTransforms("goRead", PTransform.newBuilder().setUniqueName("GoRead").putInputs("input", "goImpulse.out").putOutputs("output", "goRead.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("go")).build().toByteString())).build()).putPcollections("goRead.out", GreedyPipelineFuserTest.pc("goRead.out")).putTransforms("flatten", PTransform.newBuilder().setUniqueName("Flatten").putInputs("goReadInput", "goRead.out").putInputs("pyReadInput", "pyRead.out").putOutputs("output", "flatten.out").setSpec(FunctionSpec.newBuilder().setUrn(FLATTEN_TRANSFORM_URN)).build()).putPcollections("flatten.out", GreedyPipelineFuserTest.pc("flatten.out")).putTransforms("goParDo", PTransform.newBuilder().setUniqueName("GoParDo").putInputs("input", "flatten.out").putOutputs("output", "goParDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("go")).build().toByteString())).build()).putPcollections("goParDo.out", GreedyPipelineFuserTest.pc("goParDo.out")).putEnvironments("go", Environments.createDockerEnvironment("go")).putEnvironments("py", Environments.createDockerEnvironment("py")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("pyImpulse", components.getTransformsOrThrow("pyImpulse")), PipelineNode.pTransform("goImpulse", components.getTransformsOrThrow("goImpulse"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("goImpulse.out").withNoOutputs().withTransforms("goRead", "flatten", "goParDo"), ExecutableStageMatcher.withInput("pyImpulse.out").withOutputs("flatten.out").withTransforms("pyRead", "flatten"), ExecutableStageMatcher.withInput("flatten.out").withNoOutputs().withTransforms("goParDo")));
    }

    /* impulseA -> .out -> flatten -> .out -> read -> .out -> parDo -> .out
    becomes
    (flatten.out) -> read -> parDo

    Flatten, specifically, doesn't fuse greedily into downstream environments or act as a sibling
    to any of those nodes, but the routing is instead handled by the Runner.
     */
    @Test
    public void flattenAfterNoEnvDoesNotFuse() {
        Components components = partialComponents.toBuilder().putTransforms("flatten", PTransform.newBuilder().setUniqueName("Flatten").putInputs("impulseInput", "impulse.out").putOutputs("output", "flatten.out").setSpec(FunctionSpec.newBuilder().setUrn(FLATTEN_TRANSFORM_URN).build()).build()).putPcollections("flatten.out", GreedyPipelineFuserTest.pc("flatten.out")).putTransforms("read", PTransform.newBuilder().setUniqueName("Read").putInputs("input", "flatten.out").putOutputs("output", "read.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("read.out", GreedyPipelineFuserTest.pc("read.out")).putTransforms("parDo", PTransform.newBuilder().setUniqueName("ParDo").putInputs("input", "read.out").putOutputs("output", "parDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py").build()).build().toByteString())).build()).putPcollections("parDo.out", GreedyPipelineFuserTest.pc("parDo.out")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse")), PipelineNode.pTransform("flatten", components.getTransformsOrThrow("flatten"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.contains(ExecutableStageMatcher.withInput("flatten.out").withNoOutputs().withTransforms("read", "parDo")));
    }

    /* impulseA -> .out -> read -> .out -> leftParDo -> .out
                                     \ -> rightParDo -> .out
                                      ------> sideInputParDo -> .out
                                           /
    impulseB -> .out -> side_read -> .out /

    becomes
    (impulseA.out) -> read -> (read.out)
    (read.out) -> leftParDo
               \
                -> rightParDo
    (read.out) -> sideInputParDo
    (impulseB.out) -> side_read
     */
    @Test
    public void sideInputRootsNewStage() {
        Components components = Components.newBuilder().putCoders("coder", Coder.newBuilder().build()).putCoders("windowCoder", Coder.newBuilder().build()).putWindowingStrategies("ws", WindowingStrategy.newBuilder().setWindowCoderId("windowCoder").build()).putTransforms("mainImpulse", PTransform.newBuilder().setUniqueName("MainImpulse").putOutputs("output", "mainImpulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("mainImpulse.out", GreedyPipelineFuserTest.pc("mainImpulse.out")).putTransforms("read", PTransform.newBuilder().setUniqueName("Read").putInputs("input", "mainImpulse.out").putOutputs("output", "read.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("read.out", GreedyPipelineFuserTest.pc("read.out")).putTransforms("sideImpulse", PTransform.newBuilder().setUniqueName("SideImpulse").putOutputs("output", "sideImpulse.out").setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN)).build()).putPcollections("sideImpulse.out", GreedyPipelineFuserTest.pc("sideImpulse.out")).putTransforms("sideRead", PTransform.newBuilder().setUniqueName("SideRead").putInputs("input", "sideImpulse.out").putOutputs("output", "sideRead.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("sideRead.out", GreedyPipelineFuserTest.pc("sideRead.out")).putTransforms("leftParDo", PTransform.newBuilder().setUniqueName("LeftParDo").putInputs("main", "read.out").putOutputs("output", "leftParDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString()).build()).build()).putPcollections("leftParDo.out", GreedyPipelineFuserTest.pc("leftParDo.out")).putTransforms("rightParDo", PTransform.newBuilder().setUniqueName("RightParDo").putInputs("main", "read.out").putOutputs("output", "rightParDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString()).build()).build()).putPcollections("rightParDo.out", GreedyPipelineFuserTest.pc("rightParDo.out")).putTransforms("sideParDo", PTransform.newBuilder().setUniqueName("SideParDo").putInputs("main", "read.out").putInputs("side", "sideRead.out").putOutputs("output", "sideParDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).putSideInputs("side", SideInput.getDefaultInstance()).build().toByteString()).build()).build()).putPcollections("sideParDo.out", GreedyPipelineFuserTest.pc("sideParDo.out")).putEnvironments("py", Environments.createDockerEnvironment("py")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("mainImpulse", components.getTransformsOrThrow("mainImpulse")), PipelineNode.pTransform("sideImpulse", components.getTransformsOrThrow("sideImpulse"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("mainImpulse.out").withOutputs("read.out").withTransforms("read"), ExecutableStageMatcher.withInput("read.out").withNoOutputs().withTransforms("leftParDo", "rightParDo"), ExecutableStageMatcher.withInput("read.out").withSideInputs(SideInputId.newBuilder().setTransformId("sideParDo").setLocalName("side").build()).withNoOutputs().withTransforms("sideParDo"), ExecutableStageMatcher.withInput("sideImpulse.out").withOutputs("sideRead.out").withTransforms("sideRead")));
    }

    /* impulse -> .out -> parDo -> .out -> stateful -> .out
    becomes
    (impulse.out) -> parDo -> (parDo.out)
    (parDo.out) -> stateful
     */
    @Test
    public void statefulParDoRootsStage() {
        // (impulse.out) -> parDo -> (parDo.out)
        // (parDo.out) -> stateful -> stateful.out
        // stateful has a state spec which prevents it from fusing with an upstream ParDo
        PTransform parDoTransform = PTransform.newBuilder().setUniqueName("ParDo").putInputs("input", "impulse.out").putOutputs("output", "parDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).build().toByteString())).build();
        PTransform statefulTransform = PTransform.newBuilder().setUniqueName("StatefulParDo").putInputs("input", "parDo.out").putOutputs("output", "stateful.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).putStateSpecs("state", StateSpec.getDefaultInstance()).build().toByteString())).build();
        Components components = partialComponents.toBuilder().putTransforms("parDo", parDoTransform).putPcollections("parDo.out", GreedyPipelineFuserTest.pc("parDo.out")).putTransforms("stateful", statefulTransform).putPcollections("stateful.out", GreedyPipelineFuserTest.pc("stateful.out")).putEnvironments("common", Environments.createDockerEnvironment("common")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("impulse.out").withOutputs("parDo.out").withTransforms("parDo"), ExecutableStageMatcher.withInput("parDo.out").withNoOutputs().withTransforms("stateful")));
    }

    /* impulse -> .out -> parDo -> .out -> timer -> .out
    becomes
    (impulse.out) -> parDo -> (parDo.out)
    (parDo.out) -> timer
     */
    @Test
    public void parDoWithTimerRootsStage() {
        // (impulse.out) -> parDo -> (parDo.out)
        // (parDo.out) -> timer -> timer.out
        // timer has a timer spec which prevents it from fusing with an upstream ParDo
        PTransform parDoTransform = PTransform.newBuilder().setUniqueName("ParDo").putInputs("input", "impulse.out").putOutputs("output", "parDo.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).build().toByteString())).build();
        PTransform timerTransform = PTransform.newBuilder().setUniqueName("TimerParDo").putInputs("input", "parDo.out").putInputs("timer", "timer.out").putOutputs("timer", "timer.out").putOutputs("output", "output.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).putTimerSpecs("timer", TimerSpec.getDefaultInstance()).build().toByteString())).build();
        Components components = partialComponents.toBuilder().putTransforms("parDo", parDoTransform).putPcollections("parDo.out", GreedyPipelineFuserTest.pc("parDo.out")).putTransforms("timer", timerTransform).putPcollections("timer.out", GreedyPipelineFuserTest.pc("timer.out")).putPcollections("output.out", GreedyPipelineFuserTest.pc("output.out")).putEnvironments("common", Environments.createDockerEnvironment("common")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("impulse.out").withOutputs("parDo.out").withTransforms("parDo"), ExecutableStageMatcher.withInput("parDo.out").withNoOutputs().withTransforms("timer")));
    }

    /* Tests that parDo with state and timers is fused correctly and can be queried
    impulse -> .out -> timer -> .out
    becomes
    (impulse.out) -> timer
     */
    @Test
    public void parDoWithStateAndTimerRootsStage() {
        PTransform timerTransform = PTransform.newBuilder().setUniqueName("TimerParDo").putInputs("input", "impulse.out").putInputs("timer", "timer.out").putOutputs("timer", "timer.out").putOutputs("output", "output.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("common")).putStateSpecs("state", StateSpec.getDefaultInstance()).putTimerSpecs("timer", TimerSpec.getDefaultInstance()).build().toByteString())).build();
        Components components = partialComponents.toBuilder().putTransforms("timer", timerTransform).putPcollections("timer.out", GreedyPipelineFuserTest.pc("timer.out")).putPcollections("output.out", GreedyPipelineFuserTest.pc("output.out")).putEnvironments("common", Environments.createDockerEnvironment("common")).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().setComponents(components).build());
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.containsInAnyOrder(PipelineNode.pTransform("impulse", components.getTransformsOrThrow("impulse"))));
        Assert.assertThat(fused.getFusedStages(), Matchers.contains(ExecutableStageMatcher.withInput("impulse.out").withNoOutputs().withTransforms("timer")));
    }

    /* impulse -> .out -> ( read -> .out --> goTransform -> .out )
                                       \
                                        -> pyTransform -> .out )
    becomes (impulse.out) -> read -> (read.out)
            (read.out) -> goTransform
            (read.out) -> pyTransform
     */
    @Test
    public void compositesIgnored() {
        Components components = partialComponents.toBuilder().putTransforms("read", PTransform.newBuilder().setUniqueName("Read").putInputs("input", "impulse.out").putOutputs("output", "read.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("read.out", GreedyPipelineFuserTest.pc("read.out")).putTransforms("goTransform", PTransform.newBuilder().setUniqueName("GoTransform").putInputs("input", "read.out").putOutputs("output", "go.out").setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(ParDoPayload.newBuilder().setDoFn(SdkFunctionSpec.newBuilder().setEnvironmentId("go")).build().toByteString())).build()).putPcollections("go.out", GreedyPipelineFuserTest.pc("go.out")).putTransforms("pyTransform", PTransform.newBuilder().setUniqueName("PyTransform").putInputs("input", "read.out").putOutputs("output", "py.out").setSpec(FunctionSpec.newBuilder().setUrn(ASSIGN_WINDOWS_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build()).putPcollections("py.out", GreedyPipelineFuserTest.pc("py.out")).putTransforms("compositeMultiLang", PTransform.newBuilder().setUniqueName("CompositeMultiLang").putInputs("input", "impulse.out").putOutputs("pyOut", "py.out").putOutputs("goOut", "go.out").addSubtransforms("read").addSubtransforms("goTransform").addSubtransforms("pyTransform").build()).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(Pipeline.newBuilder().addRootTransformIds("impulse").addRootTransformIds("compositeMultiLang").setComponents(components).build());
        // Impulse is the runner transform
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.hasSize(1));
        Assert.assertThat(fused.getFusedStages(), Matchers.hasSize(3));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput("impulse.out").withOutputs("read.out").withTransforms("read"), ExecutableStageMatcher.withInput("read.out").withNoOutputs().withTransforms("pyTransform"), ExecutableStageMatcher.withInput("read.out").withNoOutputs().withTransforms("goTransform")));
    }

    @Test
    public void sanitizedTransforms() throws Exception {
        PCollection flattenOutput = GreedyPipelineFuserTest.pc("flatten.out");
        PCollection read1Output = GreedyPipelineFuserTest.pc("read1.out");
        PCollection read2Output = GreedyPipelineFuserTest.pc("read2.out");
        PCollection impulse1Output = GreedyPipelineFuserTest.pc("impulse1.out");
        PCollection impulse2Output = GreedyPipelineFuserTest.pc("impulse2.out");
        PTransform flattenTransform = PTransform.newBuilder().setUniqueName("Flatten").putInputs(read1Output.getUniqueName(), read1Output.getUniqueName()).putInputs(read2Output.getUniqueName(), read2Output.getUniqueName()).putOutputs(flattenOutput.getUniqueName(), flattenOutput.getUniqueName()).setSpec(FunctionSpec.newBuilder().setUrn(FLATTEN_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build();
        PTransform read1Transform = PTransform.newBuilder().setUniqueName("read1").putInputs(impulse1Output.getUniqueName(), impulse1Output.getUniqueName()).putOutputs(read1Output.getUniqueName(), read1Output.getUniqueName()).setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build();
        PTransform read2Transform = PTransform.newBuilder().setUniqueName("read2").putInputs(impulse2Output.getUniqueName(), impulse2Output.getUniqueName()).putOutputs(read2Output.getUniqueName(), read2Output.getUniqueName()).setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build();
        PTransform impulse1Transform = PTransform.newBuilder().setUniqueName("impulse1").putOutputs(impulse1Output.getUniqueName(), impulse1Output.getUniqueName()).setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build();
        PTransform impulse2Transform = PTransform.newBuilder().setUniqueName("impulse2").putOutputs(impulse2Output.getUniqueName(), impulse2Output.getUniqueName()).setSpec(FunctionSpec.newBuilder().setUrn(IMPULSE_TRANSFORM_URN).setPayload(WindowIntoPayload.newBuilder().setWindowFn(SdkFunctionSpec.newBuilder().setEnvironmentId("py")).build().toByteString())).build();
        Pipeline impulse = Pipeline.newBuilder().addRootTransformIds(impulse1Transform.getUniqueName()).addRootTransformIds(impulse2Transform.getUniqueName()).addRootTransformIds(flattenTransform.getUniqueName()).setComponents(Components.newBuilder().putCoders("coder", Coder.newBuilder().build()).putCoders("windowCoder", Coder.newBuilder().build()).putWindowingStrategies("ws", WindowingStrategy.newBuilder().setWindowCoderId("windowCoder").build()).putEnvironments("py", Environments.createDockerEnvironment("py")).putPcollections(flattenOutput.getUniqueName(), flattenOutput).putTransforms(flattenTransform.getUniqueName(), flattenTransform).putPcollections(read1Output.getUniqueName(), read1Output).putTransforms(read1Transform.getUniqueName(), read1Transform).putPcollections(read2Output.getUniqueName(), read2Output).putTransforms(read2Transform.getUniqueName(), read2Transform).putPcollections(impulse1Output.getUniqueName(), impulse1Output).putTransforms(impulse1Transform.getUniqueName(), impulse1Transform).putPcollections(impulse2Output.getUniqueName(), impulse2Output).putTransforms(impulse2Transform.getUniqueName(), impulse2Transform).build()).build();
        FusedPipeline fused = GreedyPipelineFuser.fuse(impulse);
        Assert.assertThat(fused.getRunnerExecutedTransforms(), Matchers.hasSize(2));
        Assert.assertThat(fused.getFusedStages(), Matchers.hasSize(2));
        Assert.assertThat(fused.getFusedStages(), Matchers.containsInAnyOrder(ExecutableStageMatcher.withInput(impulse1Output.getUniqueName()).withTransforms(flattenTransform.getUniqueName(), read1Transform.getUniqueName()), ExecutableStageMatcher.withInput(impulse2Output.getUniqueName()).withTransforms(flattenTransform.getUniqueName(), read2Transform.getUniqueName())));
        Assert.assertThat(fused.getFusedStages().stream().flatMap(( s) -> s.getComponents().getTransformsOrThrow(flattenTransform.getUniqueName()).getInputsMap().values().stream()).collect(Collectors.toList()), Matchers.containsInAnyOrder(read1Output.getUniqueName(), read2Output.getUniqueName()));
    }
}

