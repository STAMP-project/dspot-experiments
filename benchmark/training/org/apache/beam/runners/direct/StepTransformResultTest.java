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
package org.apache.beam.runners.direct;


import OutputType.PCOLLECTION_VIEW;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.values.PCollection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link StepTransformResult}.
 */
@RunWith(JUnit4.class)
public class StepTransformResultTest {
    private AppliedPTransform<?, ?, ?> transform;

    private BundleFactory bundleFactory;

    private PCollection<Integer> pc;

    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void producedBundlesProducedOutputs() {
        UncommittedBundle<Integer> bundle = bundleFactory.createBundle(pc);
        TransformResult<Integer> result = StepTransformResult.<Integer>withoutHold(transform).addOutput(bundle).build();
        Assert.assertThat(result.getOutputBundles(), Matchers.containsInAnyOrder(bundle));
    }

    @Test
    public void withAdditionalOutputProducedOutputs() {
        TransformResult<Integer> result = StepTransformResult.<Integer>withoutHold(transform).withAdditionalOutput(PCOLLECTION_VIEW).build();
        Assert.assertThat(result.getOutputTypes(), Matchers.containsInAnyOrder(PCOLLECTION_VIEW));
    }

    @Test
    public void producedBundlesAndAdditionalOutputProducedOutputs() {
        TransformResult<Integer> result = StepTransformResult.<Integer>withoutHold(transform).addOutput(bundleFactory.createBundle(pc)).withAdditionalOutput(PCOLLECTION_VIEW).build();
        Assert.assertThat(result.getOutputTypes(), Matchers.hasItem(PCOLLECTION_VIEW));
    }

    @Test
    public void noBundlesNoAdditionalOutputProducedOutputsFalse() {
        TransformResult<Integer> result = StepTransformResult.<Integer>withoutHold(transform).build();
        Assert.assertThat(result.getOutputTypes(), Matchers.emptyIterable());
    }
}

