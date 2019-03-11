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
package org.apache.beam.runners.core.construction;


import RunnerApi.ExecutableStagePayload;
import java.io.Serializable;
import java.util.Arrays;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.construction.graph.ExecutableStage;
import org.apache.beam.runners.core.construction.graph.GreedyPipelineFuser;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.sdk.transforms.ParDo;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ExecutableStageTranslation}.
 */
public class ExecutableStageTranslationTest implements Serializable {
    /* Test for generating readable operator names during translation. */
    @Test
    public void testOperatorNameGeneration() throws Exception {
        Pipeline p = Pipeline.create();
        // Avoid nested Anonymous ParDo
        // Name ParDo
        // Anonymous ParDo
        p.apply(Impulse.create()).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<byte[], String>() {
            @ProcessElement
            public void processElement(ProcessContext processContext, OutputReceiver<String> outputReceiver) {
            }
        })).apply("MyName", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<String, Integer>() {
            @ProcessElement
            public void processElement(ProcessContext processContext, OutputReceiver<Integer> outputReceiver) {
            }
        })).apply("Composite/Nested/ParDo", ParDo.of(new org.apache.beam.sdk.transforms.DoFn<Integer, Integer>() {
            @ProcessElement
            public void processElement(ProcessContext processContext, OutputReceiver<Integer> outputReceiver) {
            }
        }));
        ExecutableStage firstEnvStage = GreedyPipelineFuser.fuse(PipelineTranslation.toProto(p)).getFusedStages().stream().findFirst().get();
        RunnerApi.ExecutableStagePayload basePayload = ExecutableStagePayload.parseFrom(firstEnvStage.toPTransform("foo").getSpec().getPayload());
        String executableStageName = ExecutableStageTranslation.generateNameFromStagePayload(basePayload);
        Assert.assertThat(executableStageName, Is.is("[3]{ParDo(Anonymous), MyName, Composite}"));
    }

    @Test
    public void testOperatorNameGenerationFromNames() {
        assertGeneratedNames("A", "A", Arrays.asList("A"));
        assertGeneratedNames("A/a1", "A/a1", Arrays.asList("A/a1"));
        assertGeneratedNames("A/{a1, a2}", "A/{a1, a2}", Arrays.asList("A/a1", "A/a2"));
        assertGeneratedNames("A/{a1, a2}", "A/{a1, a2/{a2.1, a2.2}}", Arrays.asList("A/a1", "A/a2/a2.1", "A/a2/a2.2"));
        assertGeneratedNames("{A, B}", "{A/{a1, a2}, B}", Arrays.asList("A/a1", "A/a2", "B"));
        assertGeneratedNames("{A, B, C}", "{A/{a1, a2}, B, C/c/cc}", Arrays.asList("A/a1", "A/a2", "B", "C/c/cc"));
        assertGeneratedNames("{A, B, C}", "{A/{a1, a2}, B, C/c/cc/{ccc1, ccc2}}", Arrays.asList("A/a1", "A/a2", "B", "C/c/cc/ccc1", "C/c/cc/ccc2"));
        assertGeneratedNames("{Count.PerElement, Format, Write}", "{Count.PerElement/Combine.perKey(Count)/Combine.GroupedValues/ParDo(Anonymous), Format, Write/{RewindowIntoGlobal, WriteUnshardedBundlesToTempFiles, GatherTempFileResults/...}}", Arrays.asList("Count.PerElement/Combine.perKey(Count)/Combine.GroupedValues/ParDo(Anonymous)", "Format", "Write/RewindowIntoGlobal", "Write/WriteUnshardedBundlesToTempFiles", "Write/GatherTempFileResults/..."));
    }
}

