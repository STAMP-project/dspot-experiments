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


import java.util.Collections;
import org.apache.beam.sdk.io.GenerateSequence;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.PValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PTransformReplacements}.
 */
@RunWith(JUnit4.class)
public class PTransformReplacementsTest {
    @Rule
    public TestPipeline pipeline = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PCollection<Long> mainInput = pipeline.apply(GenerateSequence.from(0));

    private PCollectionView<String> sideInput = pipeline.apply(Create.of("foo")).apply(View.asSingleton());

    private PCollection<Long> output = mainInput.apply(ParDo.of(new PTransformReplacementsTest.TestDoFn()));

    @Test
    public void getMainInputSingleOutputSingleInput() {
        AppliedPTransform<PCollection<Long>, ?, ?> application = AppliedPTransform.of("application", Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Long>(), mainInput), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Long>(), output), ParDo.of(new PTransformReplacementsTest.TestDoFn()), pipeline);
        PCollection<Long> input = PTransformReplacements.getSingletonMainInput(application);
        Assert.assertThat(input, Matchers.equalTo(mainInput));
    }

    @Test
    public void getMainInputSingleOutputSideInputs() {
        AppliedPTransform<PCollection<Long>, ?, ?> application = AppliedPTransform.of("application", ImmutableMap.<org.apache.beam.sdk.values.TupleTag<?>, PValue>builder().put(new org.apache.beam.sdk.values.TupleTag<Long>(), mainInput).put(sideInput.getTagInternal(), sideInput.getPCollection()).build(), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Long>(), output), ParDo.of(new PTransformReplacementsTest.TestDoFn()).withSideInputs(sideInput), pipeline);
        PCollection<Long> input = PTransformReplacements.getSingletonMainInput(application);
        Assert.assertThat(input, Matchers.equalTo(mainInput));
    }

    @Test
    public void getMainInputExtraMainInputsThrows() {
        PCollection<Long> notInParDo = pipeline.apply("otherPCollection", Create.of(1L, 2L, 3L));
        ImmutableMap<org.apache.beam.sdk.values.TupleTag<?>, PValue> inputs = // Not represnted as an input
        ImmutableMap.<org.apache.beam.sdk.values.TupleTag<?>, PValue>builder().putAll(mainInput.expand()).put(new org.apache.beam.sdk.values.TupleTag<Long>(), notInParDo).put(sideInput.getTagInternal(), sideInput.getPCollection()).build();
        AppliedPTransform<PCollection<Long>, ?, ?> application = AppliedPTransform.of("application", inputs, Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Long>(), output), ParDo.of(new PTransformReplacementsTest.TestDoFn()).withSideInputs(sideInput), pipeline);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("multiple inputs");
        thrown.expectMessage("not additional inputs");
        thrown.expectMessage(mainInput.toString());
        thrown.expectMessage(notInParDo.toString());
        PTransformReplacements.getSingletonMainInput(application);
    }

    @Test
    public void getMainInputNoMainInputsThrows() {
        ImmutableMap<org.apache.beam.sdk.values.TupleTag<?>, PValue> inputs = ImmutableMap.<org.apache.beam.sdk.values.TupleTag<?>, PValue>builder().put(sideInput.getTagInternal(), sideInput.getPCollection()).build();
        AppliedPTransform<PCollection<Long>, ?, ?> application = AppliedPTransform.of("application", inputs, Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Long>(), output), ParDo.of(new PTransformReplacementsTest.TestDoFn()).withSideInputs(sideInput), pipeline);
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No main input");
        PTransformReplacements.getSingletonMainInput(application);
    }

    private static class TestDoFn extends DoFn<Long, Long> {
        @ProcessElement
        public void process(ProcessContext context) {
        }
    }
}

