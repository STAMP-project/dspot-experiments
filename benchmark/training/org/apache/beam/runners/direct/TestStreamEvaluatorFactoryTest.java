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


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import BoundedWindow.TIMESTAMP_MIN_VALUE;
import java.util.Collection;
import java.util.Collections;
import org.apache.beam.runners.direct.TestStreamEvaluatorFactory.TestClock;
import org.apache.beam.runners.direct.TestStreamEvaluatorFactory.TestStreamIndex;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TimestampedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link TestStreamEvaluatorFactory}.
 */
@RunWith(JUnit4.class)
public class TestStreamEvaluatorFactoryTest {
    private TestStreamEvaluatorFactory factory;

    private BundleFactory bundleFactory;

    private EvaluationContext context;

    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    private DirectRunner runner;

    /**
     * Demonstrates that returned evaluators produce elements in sequence.
     */
    @Test
    public void producesElementsInSequence() throws Exception {
        TestStream<Integer> testStream = TestStream.create(VarIntCoder.of()).addElements(1, 2, 3).advanceWatermarkTo(new Instant(0)).addElements(TimestampedValue.atMinimumTimestamp(4), TimestampedValue.atMinimumTimestamp(5), TimestampedValue.atMinimumTimestamp(6)).advanceProcessingTime(Duration.standardMinutes(10)).advanceWatermarkToInfinity();
        PCollection<Integer> streamVals = p.apply(new org.apache.beam.runners.direct.TestStreamEvaluatorFactory.DirectTestStreamFactory.DirectTestStream(runner, testStream));
        TestClock clock = new TestClock();
        Mockito.when(context.getClock()).thenReturn(clock);
        Mockito.when(context.createRootBundle()).thenReturn(bundleFactory.createRootBundle());
        Mockito.when(context.createBundle(streamVals)).thenReturn(bundleFactory.createBundle(streamVals), bundleFactory.createBundle(streamVals));
        AppliedPTransform<?, ?, ?> streamProducer = DirectGraphs.getProducer(streamVals);
        Collection<CommittedBundle<?>> initialInputs = new TestStreamEvaluatorFactory.InputProvider(context).getInitialInputs(streamProducer, 1);
        @SuppressWarnings("unchecked")
        CommittedBundle<TestStreamIndex<Integer>> initialBundle = ((CommittedBundle<TestStreamIndex<Integer>>) (Iterables.getOnlyElement(initialInputs)));
        TransformEvaluator<TestStreamIndex<Integer>> firstEvaluator = factory.forApplication(streamProducer, initialBundle);
        firstEvaluator.processElement(Iterables.getOnlyElement(initialBundle.getElements()));
        TransformResult<TestStreamIndex<Integer>> firstResult = firstEvaluator.finishBundle();
        WindowedValue<TestStreamIndex<Integer>> firstResidual = ((WindowedValue<TestStreamIndex<Integer>>) (Iterables.getOnlyElement(firstResult.getUnprocessedElements())));
        Assert.assertThat(firstResidual.getValue().getIndex(), Matchers.equalTo(1));
        Assert.assertThat(firstResidual.getTimestamp(), Matchers.equalTo(TIMESTAMP_MIN_VALUE));
        CommittedBundle<TestStreamIndex<Integer>> secondBundle = initialBundle.withElements(Collections.singleton(firstResidual));
        TransformEvaluator<TestStreamIndex<Integer>> secondEvaluator = factory.forApplication(streamProducer, secondBundle);
        secondEvaluator.processElement(firstResidual);
        TransformResult<TestStreamIndex<Integer>> secondResult = secondEvaluator.finishBundle();
        WindowedValue<TestStreamIndex<Integer>> secondResidual = ((WindowedValue<TestStreamIndex<Integer>>) (Iterables.getOnlyElement(secondResult.getUnprocessedElements())));
        Assert.assertThat(secondResidual.getValue().getIndex(), Matchers.equalTo(2));
        Assert.assertThat(secondResidual.getTimestamp(), Matchers.equalTo(new Instant(0)));
        CommittedBundle<TestStreamIndex<Integer>> thirdBundle = secondBundle.withElements(Collections.singleton(secondResidual));
        TransformEvaluator<TestStreamIndex<Integer>> thirdEvaluator = factory.forApplication(streamProducer, thirdBundle);
        thirdEvaluator.processElement(secondResidual);
        TransformResult<TestStreamIndex<Integer>> thirdResult = thirdEvaluator.finishBundle();
        WindowedValue<TestStreamIndex<Integer>> thirdResidual = ((WindowedValue<TestStreamIndex<Integer>>) (Iterables.getOnlyElement(thirdResult.getUnprocessedElements())));
        Assert.assertThat(thirdResidual.getValue().getIndex(), Matchers.equalTo(3));
        Assert.assertThat(thirdResidual.getTimestamp(), Matchers.equalTo(new Instant(0)));
        Instant start = clock.now();
        CommittedBundle<TestStreamIndex<Integer>> fourthBundle = thirdBundle.withElements(Collections.singleton(thirdResidual));
        TransformEvaluator<TestStreamIndex<Integer>> fourthEvaluator = factory.forApplication(streamProducer, fourthBundle);
        fourthEvaluator.processElement(thirdResidual);
        TransformResult<TestStreamIndex<Integer>> fourthResult = fourthEvaluator.finishBundle();
        Assert.assertThat(clock.now(), Matchers.equalTo(start.plus(Duration.standardMinutes(10))));
        WindowedValue<TestStreamIndex<Integer>> fourthResidual = ((WindowedValue<TestStreamIndex<Integer>>) (Iterables.getOnlyElement(fourthResult.getUnprocessedElements())));
        Assert.assertThat(fourthResidual.getValue().getIndex(), Matchers.equalTo(4));
        Assert.assertThat(fourthResidual.getTimestamp(), Matchers.equalTo(new Instant(0)));
        CommittedBundle<TestStreamIndex<Integer>> fifthBundle = thirdBundle.withElements(Collections.singleton(fourthResidual));
        TransformEvaluator<TestStreamIndex<Integer>> fifthEvaluator = factory.forApplication(streamProducer, fifthBundle);
        fifthEvaluator.processElement(fourthResidual);
        TransformResult<TestStreamIndex<Integer>> fifthResult = fifthEvaluator.finishBundle();
        Assert.assertThat(Iterables.getOnlyElement(firstResult.getOutputBundles()).commit(Instant.now()).getElements(), Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(1), WindowedValue.valueInGlobalWindow(2), WindowedValue.valueInGlobalWindow(3)));
        Assert.assertThat(Iterables.getOnlyElement(thirdResult.getOutputBundles()).commit(Instant.now()).getElements(), Matchers.containsInAnyOrder(WindowedValue.valueInGlobalWindow(4), WindowedValue.valueInGlobalWindow(5), WindowedValue.valueInGlobalWindow(6)));
        Assert.assertThat(fifthResult.getOutputBundles(), Matchers.emptyIterable());
        Assert.assertThat(fifthResult.getWatermarkHold(), Matchers.equalTo(TIMESTAMP_MAX_VALUE));
        Assert.assertThat(fifthResult.getUnprocessedElements(), Matchers.emptyIterable());
    }
}

