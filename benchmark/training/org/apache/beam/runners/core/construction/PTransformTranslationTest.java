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


import com.google.auto.value.AutoValue;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PValue;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link PTransformTranslation}.
 */
@RunWith(Parameterized.class)
public class PTransformTranslationTest {
    @AutoValue
    abstract static class ToAndFromProtoSpec {
        public static PTransformTranslationTest.ToAndFromProtoSpec leaf(AppliedPTransform<?, ?, ?> transform) {
            return new AutoValue_PTransformTranslationTest_ToAndFromProtoSpec(transform, Collections.emptyList());
        }

        public static PTransformTranslationTest.ToAndFromProtoSpec composite(AppliedPTransform<?, ?, ?> topLevel, PTransformTranslationTest.ToAndFromProtoSpec spec, PTransformTranslationTest.ToAndFromProtoSpec... specs) {
            List<PTransformTranslationTest.ToAndFromProtoSpec> childSpecs = new ArrayList<>();
            childSpecs.add(spec);
            childSpecs.addAll(Arrays.asList(specs));
            return new AutoValue_PTransformTranslationTest_ToAndFromProtoSpec(topLevel, childSpecs);
        }

        abstract AppliedPTransform<?, ?, ?> getTransform();

        abstract Collection<PTransformTranslationTest.ToAndFromProtoSpec> getChildren();
    }

    @Parameterized.Parameter(0)
    public PTransformTranslationTest.ToAndFromProtoSpec spec;

    @Test
    public void toAndFromProto() throws IOException {
        SdkComponents components = SdkComponents.create(spec.getTransform().getPipeline().getOptions());
        RunnerApi.PTransform converted = convert(spec, components);
        Components protoComponents = components.toComponents();
        // Sanity checks
        Assert.assertThat(converted.getInputsCount(), Matchers.equalTo(spec.getTransform().getInputs().size()));
        Assert.assertThat(converted.getOutputsCount(), Matchers.equalTo(spec.getTransform().getOutputs().size()));
        Assert.assertThat(converted.getSubtransformsCount(), Matchers.equalTo(spec.getChildren().size()));
        Assert.assertThat(converted.getUniqueName(), Matchers.equalTo(spec.getTransform().getFullName()));
        for (PValue inputValue : spec.getTransform().getInputs().values()) {
            PCollection<?> inputPc = ((PCollection<?>) (inputValue));
            protoComponents.getPcollectionsOrThrow(components.registerPCollection(inputPc));
        }
        for (PValue outputValue : spec.getTransform().getOutputs().values()) {
            PCollection<?> outputPc = ((PCollection<?>) (outputValue));
            protoComponents.getPcollectionsOrThrow(components.registerPCollection(outputPc));
        }
    }

    private static class TestDoFn extends DoFn<Long, KV<Long, String>> {
        // Exists to stop the ParDo application from throwing
        @ProcessElement
        public void process(ProcessContext context) {
        }
    }
}

