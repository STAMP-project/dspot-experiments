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
package org.apache.beam.runners.dataflow;


import ParDo.SingleOutput;
import java.io.Serializable;
import java.util.List;
import org.apache.beam.runners.dataflow.PrimitiveParDoSingleFactory.ParDoSingle;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.runners.PTransformOverrideFactory.PTransformReplacement;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.display.DisplayDataEvaluator;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PrimitiveParDoSingleFactory}.
 */
@RunWith(JUnit4.class)
public class PrimitiveParDoSingleFactoryTest implements Serializable {
    // Create a pipeline for testing Side Input propagation. This won't actually run any Pipelines,
    // so disable enforcement.
    @Rule
    public transient TestPipeline pipeline = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    private transient PrimitiveParDoSingleFactory<Integer, Long> factory = new PrimitiveParDoSingleFactory();

    /**
     * A test that demonstrates that the replacement transform has the Display Data of the {@link ParDo.SingleOutput} it replaces.
     */
    @Test
    public void getReplacementTransformPopulateDisplayData() {
        SingleOutput<Integer, Long> originalTransform = ParDo.of(new PrimitiveParDoSingleFactoryTest.ToLongFn());
        DisplayData originalDisplayData = DisplayData.from(originalTransform);
        PCollection<? extends Integer> input = pipeline.apply(Create.of(1, 2, 3));
        AppliedPTransform<PCollection<? extends Integer>, PCollection<Long>, SingleOutput<Integer, Long>> application = AppliedPTransform.of("original", input.expand(), input.apply(originalTransform).expand(), originalTransform, pipeline);
        PTransformReplacement<PCollection<? extends Integer>, PCollection<Long>> replacement = factory.getReplacementTransform(application);
        DisplayData replacementDisplayData = DisplayData.from(replacement.getTransform());
        Assert.assertThat(replacementDisplayData, Matchers.equalTo(originalDisplayData));
        DisplayData primitiveDisplayData = Iterables.getOnlyElement(DisplayDataEvaluator.create().displayDataForPrimitiveTransforms(replacement.getTransform(), VarIntCoder.of()));
        Assert.assertThat(primitiveDisplayData, Matchers.equalTo(replacementDisplayData));
    }

    @Test
    public void getReplacementTransformGetSideInputs() {
        PCollectionView<Long> sideLong = pipeline.apply("LongSideInputVals", Create.of((-1L), (-2L), (-4L))).apply("SideLongView", Sum.longsGlobally().asSingletonView());
        PCollectionView<List<String>> sideStrings = pipeline.apply("StringSideInputVals", Create.of("foo", "bar", "baz")).apply("SideStringsView", View.asList());
        SingleOutput<Integer, Long> originalTransform = ParDo.of(new PrimitiveParDoSingleFactoryTest.ToLongFn()).withSideInputs(sideLong, sideStrings);
        PCollection<? extends Integer> input = pipeline.apply(Create.of(1, 2, 3));
        AppliedPTransform<PCollection<? extends Integer>, PCollection<Long>, SingleOutput<Integer, Long>> application = AppliedPTransform.of("original", input.expand(), input.apply(originalTransform).expand(), originalTransform, pipeline);
        PTransformReplacement<PCollection<? extends Integer>, PCollection<Long>> replacementTransform = factory.getReplacementTransform(application);
        ParDoSingle<Integer, Long> parDoSingle = ((ParDoSingle<Integer, Long>) (replacementTransform.getTransform()));
        Assert.assertThat(parDoSingle.getSideInputs(), Matchers.containsInAnyOrder(sideStrings, sideLong));
    }

    @Test
    public void getReplacementTransformGetFn() {
        DoFn<Integer, Long> originalFn = new PrimitiveParDoSingleFactoryTest.ToLongFn();
        SingleOutput<Integer, Long> originalTransform = ParDo.of(originalFn);
        PCollection<? extends Integer> input = pipeline.apply(Create.of(1, 2, 3));
        AppliedPTransform<PCollection<? extends Integer>, PCollection<Long>, SingleOutput<Integer, Long>> application = AppliedPTransform.of("original", input.expand(), input.apply(originalTransform).expand(), originalTransform, pipeline);
        PTransformReplacement<PCollection<? extends Integer>, PCollection<Long>> replacementTransform = factory.getReplacementTransform(application);
        ParDoSingle<Integer, Long> parDoSingle = ((ParDoSingle<Integer, Long>) (replacementTransform.getTransform()));
        Assert.assertThat(parDoSingle.getFn(), Matchers.equalTo(originalTransform.getFn()));
        Assert.assertThat(parDoSingle.getFn(), Matchers.equalTo(originalFn));
    }

    private static class ToLongFn extends DoFn<Integer, Long> {
        @ProcessElement
        public void toLong(ProcessContext ctxt) {
            ctxt.output(ctxt.element().longValue());
        }

        @Override
        public boolean equals(Object other) {
            return (other != null) && (other.getClass().equals(getClass()));
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }
    }
}

