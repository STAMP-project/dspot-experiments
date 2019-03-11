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


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.INSTANCE;
import java.util.Collection;
import org.apache.beam.runners.direct.ImpulseEvaluatorFactory.ImpulseRootProvider;
import org.apache.beam.runners.direct.ImpulseEvaluatorFactory.ImpulseShard;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ImpulseEvaluatorFactory}.
 */
@RunWith(JUnit4.class)
public class ImpulseEvaluatorFactoryTest {
    private BundleFactory bundleFactory = ImmutableListBundleFactory.create();

    @Mock
    private EvaluationContext context;

    @Test
    public void testImpulse() throws Exception {
        Pipeline p = Pipeline.create();
        PCollection<byte[]> impulseOut = p.apply(Impulse.create());
        AppliedPTransform<?, ?, ?> impulseApplication = DirectGraphs.getProducer(impulseOut);
        ImpulseEvaluatorFactory factory = new ImpulseEvaluatorFactory(context);
        WindowedValue<ImpulseShard> inputShard = WindowedValue.valueInGlobalWindow(new ImpulseShard());
        CommittedBundle<ImpulseShard> inputShardBundle = bundleFactory.<ImpulseShard>createRootBundle().add(inputShard).commit(Instant.now());
        Mockito.when(context.createBundle(impulseOut)).thenReturn(bundleFactory.createBundle(impulseOut));
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
        Pipeline p = Pipeline.create();
        PCollection<byte[]> impulseOut = p.apply(Impulse.create());
        // Add a second impulse to demonstrate no crosstalk between applications
        @SuppressWarnings("unused")
        PCollection<byte[]> impulseOutTwo = p.apply(Impulse.create());
        AppliedPTransform<?, ?, ?> impulseApplication = DirectGraphs.getProducer(impulseOut);
        ImpulseRootProvider rootProvider = new ImpulseRootProvider(context);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        Collection<CommittedBundle<?>> inputs = rootProvider.getInitialInputs(((AppliedPTransform) (impulseApplication)), 100);
        Assert.assertThat("Only one impulse bundle per application", inputs, Matchers.hasSize(1));
        Assert.assertThat("Only one impulse shard per bundle", Iterables.size(inputs.iterator().next().getElements()), Matchers.equalTo(1));
    }
}

