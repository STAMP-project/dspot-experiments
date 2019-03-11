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


import PTransformTranslation.PAR_DO_TRANSFORM_URN;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.PCollection;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.runners.core.construction.graph.OutputDeduplicator.DeduplicationResult;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PCollectionNode;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link OutputDeduplicator}.
 */
@RunWith(JUnit4.class)
public class OutputDeduplicatorTest {
    @Test
    public void unchangedWithNoDuplicates() {
        /* When all the PCollections are produced by only one transform or stage, the result should be
        empty/identical to the input.

        Pipeline:
                     /-> one -> .out \
        red -> .out ->                -> blue -> .out
                     \-> two -> .out /
         */
        PCollection redOut = PCollection.newBuilder().setUniqueName("red.out").build();
        PTransform red = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putOutputs("out", redOut.getUniqueName()).build();
        PCollection oneOut = PCollection.newBuilder().setUniqueName("one.out").build();
        PTransform one = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", oneOut.getUniqueName()).build();
        PCollection twoOut = PCollection.newBuilder().setUniqueName("two.out").build();
        PTransform two = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", twoOut.getUniqueName()).build();
        PCollection blueOut = PCollection.newBuilder().setUniqueName("blue.out").build();
        PTransform blue = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("one", oneOut.getUniqueName()).putInputs("two", twoOut.getUniqueName()).putOutputs("out", blueOut.getUniqueName()).build();
        RunnerApi.Components components = Components.newBuilder().putTransforms("one", one).putPcollections(oneOut.getUniqueName(), oneOut).putTransforms("two", two).putPcollections(twoOut.getUniqueName(), twoOut).putTransforms("red", red).putPcollections(redOut.getUniqueName(), redOut).putTransforms("blue", blue).putPcollections(blueOut.getUniqueName(), blueOut).build();
        ExecutableStage oneStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("one", one)), ImmutableList.of(PipelineNode.pCollection(oneOut.getUniqueName(), oneOut)));
        ExecutableStage twoStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("two", two)), ImmutableList.of(PipelineNode.pCollection(twoOut.getUniqueName(), twoOut)));
        PTransformNode redTransform = PipelineNode.pTransform("red", red);
        PTransformNode blueTransform = PipelineNode.pTransform("blue", blue);
        QueryablePipeline pipeline = QueryablePipeline.forPrimitivesIn(components);
        DeduplicationResult result = OutputDeduplicator.ensureSingleProducer(pipeline, ImmutableList.of(oneStage, twoStage), ImmutableList.of(redTransform, blueTransform));
        Assert.assertThat(result.getDeduplicatedComponents(), Matchers.equalTo(components));
        Assert.assertThat(result.getDeduplicatedStages().keySet(), Matchers.empty());
        Assert.assertThat(result.getDeduplicatedTransforms().keySet(), Matchers.empty());
        Assert.assertThat(result.getIntroducedTransforms(), Matchers.empty());
    }

    @Test
    public void duplicateOverStages() {
        /* When multiple stages and a runner-executed transform produce a PCollection, all should be
        replaced with synthetic flattens.
        original graph:
                    --> one -> .out \
        red -> .out |                -> shared -> .out -> blue -> .out
                    --> two -> .out /

        fused graph:
                    --> [one -> .out -> shared ->] .out
        red -> .out |                                   (shared.out) -> blue -> .out
                    --> [two -> .out -> shared ->] .out

        deduplicated graph:
                    --> [one -> .out -> shared ->] .out:0 \
        red -> .out |                                      -> shared -> .out -> blue ->.out
                    --> [two -> .out -> shared ->] .out:1 /
         */
        PCollection redOut = PCollection.newBuilder().setUniqueName("red.out").build();
        PTransform red = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putOutputs("out", redOut.getUniqueName()).build();
        PCollection oneOut = PCollection.newBuilder().setUniqueName("one.out").build();
        PTransform one = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", oneOut.getUniqueName()).build();
        PCollection twoOut = PCollection.newBuilder().setUniqueName("two.out").build();
        PTransform two = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", twoOut.getUniqueName()).build();
        PCollection sharedOut = PCollection.newBuilder().setUniqueName("shared.out").build();
        PTransform shared = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("one", oneOut.getUniqueName()).putInputs("two", twoOut.getUniqueName()).putOutputs("shared", sharedOut.getUniqueName()).build();
        PCollection blueOut = PCollection.newBuilder().setUniqueName("blue.out").build();
        PTransform blue = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", sharedOut.getUniqueName()).putOutputs("out", blueOut.getUniqueName()).build();
        RunnerApi.Components components = Components.newBuilder().putTransforms("one", one).putPcollections(oneOut.getUniqueName(), oneOut).putTransforms("two", two).putPcollections(twoOut.getUniqueName(), twoOut).putTransforms("shared", shared).putPcollections(sharedOut.getUniqueName(), sharedOut).putTransforms("red", red).putPcollections(redOut.getUniqueName(), redOut).putTransforms("blue", blue).putPcollections(blueOut.getUniqueName(), blueOut).build();
        ExecutableStage oneStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("one", one), PipelineNode.pTransform("shared", shared)), ImmutableList.of(PipelineNode.pCollection(sharedOut.getUniqueName(), sharedOut)));
        ExecutableStage twoStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("two", two), PipelineNode.pTransform("shared", shared)), ImmutableList.of(PipelineNode.pCollection(sharedOut.getUniqueName(), sharedOut)));
        PTransformNode redTransform = PipelineNode.pTransform("red", red);
        PTransformNode blueTransform = PipelineNode.pTransform("blue", blue);
        QueryablePipeline pipeline = QueryablePipeline.forPrimitivesIn(components);
        DeduplicationResult result = OutputDeduplicator.ensureSingleProducer(pipeline, ImmutableList.of(oneStage, twoStage), ImmutableList.of(redTransform, blueTransform));
        Assert.assertThat(result.getIntroducedTransforms(), Matchers.hasSize(1));
        PTransformNode introduced = getOnlyElement(result.getIntroducedTransforms());
        Assert.assertThat(introduced.getTransform().getOutputsMap().size(), Matchers.equalTo(1));
        Assert.assertThat(getOnlyElement(introduced.getTransform().getOutputsMap().values()), Matchers.equalTo(sharedOut.getUniqueName()));
        Assert.assertThat(result.getDeduplicatedComponents().getPcollectionsMap().keySet(), Matchers.hasItems(introduced.getTransform().getInputsMap().values().toArray(new String[0])));
        Assert.assertThat(result.getDeduplicatedStages().keySet(), Matchers.hasSize(2));
        List<String> stageOutputs = result.getDeduplicatedStages().values().stream().flatMap(( stage) -> stage.getOutputPCollections().stream().map(PCollectionNode::getId)).collect(Collectors.toList());
        Assert.assertThat(stageOutputs, Matchers.containsInAnyOrder(introduced.getTransform().getInputsMap().values().toArray()));
        Assert.assertThat(result.getDeduplicatedTransforms().keySet(), Matchers.empty());
        Assert.assertThat(result.getDeduplicatedComponents().getPcollectionsMap().keySet(), Matchers.hasItems(stageOutputs.toArray(new String[0])));
        Assert.assertThat(result.getDeduplicatedComponents().getTransformsMap(), Matchers.hasEntry(introduced.getId(), introduced.getTransform()));
    }

    @Test
    public void duplicateOverStagesAndTransforms() {
        /* When both a stage and a runner-executed transform produce a PCollection, all should be
        replaced with synthetic flattens.
        original graph:
                    --> one -> .out \
        red -> .out |                -> shared -> .out
                    --------------> /

        fused graph:
                    --> [one -> .out -> shared ->] .out
        red -> .out |
                    ------------------> shared --> .out

        deduplicated graph:
                    --> [one -> .out -> shared ->] .out:0 \
        red -> .out |                                      -> shared -> .out
                    -----------------> shared:0 -> .out:1 /
         */
        PCollection redOut = PCollection.newBuilder().setUniqueName("red.out").build();
        PTransform red = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putOutputs("out", redOut.getUniqueName()).build();
        PCollection oneOut = PCollection.newBuilder().setUniqueName("one.out").build();
        PTransform one = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", oneOut.getUniqueName()).build();
        PCollection sharedOut = PCollection.newBuilder().setUniqueName("shared.out").build();
        PTransform shared = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("one", oneOut.getUniqueName()).putInputs("red", redOut.getUniqueName()).putOutputs("shared", sharedOut.getUniqueName()).build();
        PCollection blueOut = PCollection.newBuilder().setUniqueName("blue.out").build();
        PTransform blue = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", sharedOut.getUniqueName()).putOutputs("out", blueOut.getUniqueName()).build();
        RunnerApi.Components components = Components.newBuilder().putTransforms("one", one).putPcollections(oneOut.getUniqueName(), oneOut).putTransforms("red", red).putPcollections(redOut.getUniqueName(), redOut).putTransforms("shared", shared).putPcollections(sharedOut.getUniqueName(), sharedOut).putTransforms("blue", blue).putPcollections(blueOut.getUniqueName(), blueOut).build();
        PTransformNode sharedTransform = PipelineNode.pTransform("shared", shared);
        ExecutableStage oneStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("one", one), sharedTransform), ImmutableList.of(PipelineNode.pCollection(sharedOut.getUniqueName(), sharedOut)));
        PTransformNode redTransform = PipelineNode.pTransform("red", red);
        PTransformNode blueTransform = PipelineNode.pTransform("blue", blue);
        QueryablePipeline pipeline = QueryablePipeline.forPrimitivesIn(components);
        DeduplicationResult result = OutputDeduplicator.ensureSingleProducer(pipeline, ImmutableList.of(oneStage), ImmutableList.of(redTransform, blueTransform, sharedTransform));
        Assert.assertThat(result.getIntroducedTransforms(), Matchers.hasSize(1));
        PTransformNode introduced = getOnlyElement(result.getIntroducedTransforms());
        Assert.assertThat(introduced.getTransform().getOutputsMap().size(), Matchers.equalTo(1));
        Assert.assertThat(getOnlyElement(introduced.getTransform().getOutputsMap().values()), Matchers.equalTo(sharedOut.getUniqueName()));
        Assert.assertThat(result.getDeduplicatedComponents().getPcollectionsMap().keySet(), Matchers.hasItems(introduced.getTransform().getInputsMap().values().toArray(new String[0])));
        Assert.assertThat(result.getDeduplicatedStages().keySet(), Matchers.hasSize(1));
        Assert.assertThat(result.getDeduplicatedTransforms().keySet(), Matchers.containsInAnyOrder("shared"));
        List<String> introducedOutputs = new ArrayList<>();
        introducedOutputs.addAll(result.getDeduplicatedTransforms().get("shared").getTransform().getOutputsMap().values());
        introducedOutputs.addAll(result.getDeduplicatedStages().get(oneStage).getOutputPCollections().stream().map(PCollectionNode::getId).collect(Collectors.toList()));
        Assert.assertThat(introduced.getTransform().getInputsMap().values(), Matchers.containsInAnyOrder(introducedOutputs.toArray(new String[0])));
        Assert.assertThat(result.getDeduplicatedComponents().getPcollectionsMap().keySet(), Matchers.hasItems(introducedOutputs.toArray(new String[0])));
        Assert.assertThat(result.getDeduplicatedComponents().getTransformsMap(), Matchers.hasEntry(introduced.getId(), introduced.getTransform()));
    }

    @Test
    public void multipleDuplicatesInStages() {
        /* A stage that produces multiple duplicates should have them all synthesized.

        Original Pipeline:
        red -> .out ---> one -> .out -----\
                    \                      -> shared.out
                     \--> two -> .out ----|
                      \                    -> otherShared -> .out
                       \-> three --> .out /

        Fused Pipeline:
             -> .out [-> one -> .out -> shared -> .out] \
            /                                            -> blue -> .out
            |                        -> shared -> .out] /
        red -> .out [-> two -> .out |
            |                        -> otherShared -> .out]
            \
             -> .out [-> three -> .out -> otherShared -> .out]

        Deduplicated Pipeline:
                  [-> one -> .out -> shared -> .out:0] --\
                  |                                       -> shared -> .out -> blue -> .out
                  |                 -> shared -> .out:1] /
        red -> .out [-> two -> .out |
                  |                  -> otherShared -> .out:0] --\
                  |                                               -> otherShared -> .out
                  [-> three -> .out -> otherShared -> .out:1] ---/
         */
        PCollection redOut = PCollection.newBuilder().setUniqueName("red.out").build();
        PTransform red = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putOutputs("out", redOut.getUniqueName()).build();
        PCollection threeOut = PCollection.newBuilder().setUniqueName("three.out").build();
        PTransform three = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", threeOut.getUniqueName()).build();
        PCollection oneOut = PCollection.newBuilder().setUniqueName("one.out").build();
        PTransform one = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", oneOut.getUniqueName()).build();
        PCollection twoOut = PCollection.newBuilder().setUniqueName("two.out").build();
        PTransform two = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", redOut.getUniqueName()).putOutputs("out", twoOut.getUniqueName()).build();
        PCollection sharedOut = PCollection.newBuilder().setUniqueName("shared.out").build();
        PTransform shared = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("one", oneOut.getUniqueName()).putInputs("two", twoOut.getUniqueName()).putOutputs("shared", sharedOut.getUniqueName()).build();
        PCollection otherSharedOut = PCollection.newBuilder().setUniqueName("shared.out2").build();
        PTransform otherShared = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("multi", threeOut.getUniqueName()).putInputs("two", twoOut.getUniqueName()).putOutputs("out", otherSharedOut.getUniqueName()).build();
        PCollection blueOut = PCollection.newBuilder().setUniqueName("blue.out").build();
        PTransform blue = PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn(PAR_DO_TRANSFORM_URN).build()).putInputs("in", sharedOut.getUniqueName()).putOutputs("out", blueOut.getUniqueName()).build();
        RunnerApi.Components components = Components.newBuilder().putTransforms("one", one).putPcollections(oneOut.getUniqueName(), oneOut).putTransforms("two", two).putPcollections(twoOut.getUniqueName(), twoOut).putTransforms("multi", three).putPcollections(threeOut.getUniqueName(), threeOut).putTransforms("shared", shared).putPcollections(sharedOut.getUniqueName(), sharedOut).putTransforms("otherShared", otherShared).putPcollections(otherSharedOut.getUniqueName(), otherSharedOut).putTransforms("red", red).putPcollections(redOut.getUniqueName(), redOut).putTransforms("blue", blue).putPcollections(blueOut.getUniqueName(), blueOut).build();
        ExecutableStage multiStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("multi", three), PipelineNode.pTransform("shared", shared), PipelineNode.pTransform("otherShared", otherShared)), ImmutableList.of(PipelineNode.pCollection(sharedOut.getUniqueName(), sharedOut), PipelineNode.pCollection(otherSharedOut.getUniqueName(), otherSharedOut)));
        ExecutableStage oneStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("one", one), PipelineNode.pTransform("shared", shared)), ImmutableList.of(PipelineNode.pCollection(sharedOut.getUniqueName(), sharedOut)));
        ExecutableStage twoStage = ImmutableExecutableStage.of(components, Environment.getDefaultInstance(), PipelineNode.pCollection(redOut.getUniqueName(), redOut), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(PipelineNode.pTransform("two", two), PipelineNode.pTransform("otherShared", otherShared)), ImmutableList.of(PipelineNode.pCollection(otherSharedOut.getUniqueName(), otherSharedOut)));
        PTransformNode redTransform = PipelineNode.pTransform("red", red);
        PTransformNode blueTransform = PipelineNode.pTransform("blue", blue);
        QueryablePipeline pipeline = QueryablePipeline.forPrimitivesIn(components);
        DeduplicationResult result = OutputDeduplicator.ensureSingleProducer(pipeline, ImmutableList.of(oneStage, twoStage, multiStage), ImmutableList.of(redTransform, blueTransform));
        Assert.assertThat(result.getIntroducedTransforms(), Matchers.hasSize(2));
        Assert.assertThat(result.getDeduplicatedStages().keySet(), Matchers.containsInAnyOrder(multiStage, oneStage, twoStage));
        Assert.assertThat(result.getDeduplicatedTransforms().keySet(), Matchers.empty());
        Collection<String> introducedIds = result.getIntroducedTransforms().stream().flatMap(( pt) -> pt.getTransform().getInputsMap().values().stream()).collect(Collectors.toList());
        String[] stageOutputs = result.getDeduplicatedStages().values().stream().flatMap(( s) -> s.getOutputPCollections().stream().map(PCollectionNode::getId)).toArray(String[]::new);
        Assert.assertThat(introducedIds, Matchers.containsInAnyOrder(stageOutputs));
        Assert.assertThat(result.getDeduplicatedComponents().getPcollectionsMap().keySet(), Matchers.hasItems(introducedIds.toArray(new String[0])));
        Assert.assertThat(result.getDeduplicatedComponents().getTransformsMap().entrySet(), Matchers.hasItems(result.getIntroducedTransforms().stream().collect(Collectors.toMap(PTransformNode::getId, PTransformNode::getTransform)).entrySet().toArray(new Map.Entry[0])));
    }
}

