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


import GlobalWindow.INSTANCE;
import IsBounded.BOUNDED;
import PaneInfo.ON_TIME_AND_ONLY_FIRING;
import TimeDomain.EVENT_TIME;
import Timing.LATE;
import Timing.ON_TIME;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.StateTag;
import org.apache.beam.runners.core.StateTags;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.runners.direct.DirectExecutionContext.DirectStepContext;
import org.apache.beam.runners.direct.WatermarkManager.FiredTimers;
import org.apache.beam.runners.direct.WatermarkManager.TimerUpdate;
import org.apache.beam.runners.local.StructuralKey;
import org.apache.beam.sdk.coders.ByteArrayCoder;
import org.apache.beam.sdk.coders.IterableCoder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link EvaluationContext}.
 */
@RunWith(JUnit4.class)
public class EvaluationContextTest {
    private EvaluationContext context;

    private PCollection<Integer> created;

    private PCollection<KV<String, Integer>> downstream;

    private PCollectionView<Iterable<Integer>> view;

    private PCollection<Long> unbounded;

    private DirectGraph graph;

    private AppliedPTransform<?, ?, ?> createdProducer;

    private AppliedPTransform<?, ?, ?> downstreamProducer;

    private AppliedPTransform<?, ?, ?> viewProducer;

    private AppliedPTransform<?, ?, ?> unboundedProducer;

    @Rule
    public TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void writeToViewWriterThenReadReads() {
        PCollectionViewWriter<?, Iterable<Integer>> viewWriter = context.createPCollectionViewWriter(PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, IterableCoder.of(KvCoder.of(VoidCoder.of(), VarIntCoder.of()))), view);
        BoundedWindow window = new EvaluationContextTest.TestBoundedWindow(new Instant(1024L));
        BoundedWindow second = new EvaluationContextTest.TestBoundedWindow(new Instant(899999L));
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asIterable(), 1)) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(1222), window, ON_TIME_AND_ONLY_FIRING));
        }
        for (Object materializedValue : materializeValuesFor(View.asIterable(), 2)) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(8766L), second, PaneInfo.createPane(true, false, ON_TIME, 0, 0)));
        }
        viewWriter.add(((Iterable) (valuesBuilder.build())));
        SideInputReader reader = context.createSideInputReader(ImmutableList.of(view));
        Assert.assertThat(reader.get(view, window), Matchers.containsInAnyOrder(1));
        Assert.assertThat(reader.get(view, second), Matchers.containsInAnyOrder(2));
        ImmutableList.Builder<WindowedValue<?>> overwrittenValuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asIterable(), 4444)) {
            overwrittenValuesBuilder.add(WindowedValue.of(materializedValue, new Instant(8677L), second, PaneInfo.createPane(false, true, LATE, 1, 1)));
        }
        viewWriter.add(((Iterable) (overwrittenValuesBuilder.build())));
        Assert.assertThat(reader.get(view, second), Matchers.containsInAnyOrder(2));
        // The cached value is served in the earlier reader
        reader = context.createSideInputReader(ImmutableList.of(view));
        Assert.assertThat(reader.get(view, second), Matchers.containsInAnyOrder(4444));
    }

    @Test
    public void getExecutionContextSameStepSameKeyState() {
        DirectExecutionContext fooContext = context.getExecutionContext(createdProducer, StructuralKey.of("foo", StringUtf8Coder.of()));
        StateTag<BagState<Integer>> intBag = StateTags.bag("myBag", VarIntCoder.of());
        DirectStepContext stepContext = fooContext.getStepContext("s1");
        stepContext.stateInternals().state(StateNamespaces.global(), intBag).add(1);
        context.handleResult(ImmutableListBundleFactory.create().createKeyedBundle(StructuralKey.of("foo", StringUtf8Coder.of()), created).commit(Instant.now()), ImmutableList.of(), StepTransformResult.withoutHold(createdProducer).withState(stepContext.commitState()).build());
        DirectExecutionContext secondFooContext = context.getExecutionContext(createdProducer, StructuralKey.of("foo", StringUtf8Coder.of()));
        Assert.assertThat(secondFooContext.getStepContext("s1").stateInternals().state(StateNamespaces.global(), intBag).read(), Matchers.contains(1));
    }

    @Test
    public void getExecutionContextDifferentKeysIndependentState() {
        DirectExecutionContext fooContext = context.getExecutionContext(createdProducer, StructuralKey.of("foo", StringUtf8Coder.of()));
        StateTag<BagState<Integer>> intBag = StateTags.bag("myBag", VarIntCoder.of());
        fooContext.getStepContext("s1").stateInternals().state(StateNamespaces.global(), intBag).add(1);
        DirectExecutionContext barContext = context.getExecutionContext(createdProducer, StructuralKey.of("bar", StringUtf8Coder.of()));
        Assert.assertThat(barContext, Matchers.not(Matchers.equalTo(fooContext)));
        Assert.assertThat(barContext.getStepContext("s1").stateInternals().state(StateNamespaces.global(), intBag).read(), Matchers.emptyIterable());
    }

    @Test
    public void getExecutionContextDifferentStepsIndependentState() {
        StructuralKey<?> myKey = StructuralKey.of("foo", StringUtf8Coder.of());
        DirectExecutionContext fooContext = context.getExecutionContext(createdProducer, myKey);
        StateTag<BagState<Integer>> intBag = StateTags.bag("myBag", VarIntCoder.of());
        fooContext.getStepContext("s1").stateInternals().state(StateNamespaces.global(), intBag).add(1);
        DirectExecutionContext barContext = context.getExecutionContext(downstreamProducer, myKey);
        Assert.assertThat(barContext.getStepContext("s1").stateInternals().state(StateNamespaces.global(), intBag).read(), Matchers.emptyIterable());
    }

    @Test
    public void handleResultStoresState() {
        StructuralKey<?> myKey = StructuralKey.of("foo".getBytes(StandardCharsets.UTF_8), ByteArrayCoder.of());
        DirectExecutionContext fooContext = context.getExecutionContext(downstreamProducer, myKey);
        StateTag<BagState<Integer>> intBag = StateTags.bag("myBag", VarIntCoder.of());
        CopyOnAccessInMemoryStateInternals<?> state = fooContext.getStepContext("s1").stateInternals();
        BagState<Integer> bag = state.state(StateNamespaces.global(), intBag);
        bag.add(1);
        bag.add(2);
        bag.add(4);
        TransformResult<?> stateResult = StepTransformResult.withoutHold(downstreamProducer).withState(state).build();
        context.handleResult(context.createKeyedBundle(myKey, created).commit(Instant.now()), ImmutableList.of(), stateResult);
        DirectExecutionContext afterResultContext = context.getExecutionContext(downstreamProducer, myKey);
        CopyOnAccessInMemoryStateInternals<?> afterResultState = afterResultContext.getStepContext("s1").stateInternals();
        Assert.assertThat(afterResultState.state(StateNamespaces.global(), intBag).read(), Matchers.contains(1, 2, 4));
    }

    @Test
    public void callAfterOutputMustHaveBeenProducedAfterEndOfWatermarkCallsback() throws Exception {
        final CountDownLatch callLatch = new CountDownLatch(1);
        Runnable callback = callLatch::countDown;
        // Should call back after the end of the global window
        context.scheduleAfterOutputWouldBeProduced(downstream, INSTANCE, WindowingStrategy.globalDefault(), callback);
        TransformResult<?> result = StepTransformResult.withHold(createdProducer, new Instant(0)).build();
        context.handleResult(null, ImmutableList.of(), result);
        // Difficult to demonstrate that we took no action in a multithreaded world; poll for a bit
        // will likely be flaky if this logic is broken
        Assert.assertThat(callLatch.await(500L, TimeUnit.MILLISECONDS), Matchers.is(false));
        TransformResult<?> finishedResult = StepTransformResult.withoutHold(createdProducer).build();
        context.handleResult(null, ImmutableList.of(), finishedResult);
        context.forceRefresh();
        // Obtain the value via blocking call
        Assert.assertThat(callLatch.await(1, TimeUnit.SECONDS), Matchers.is(true));
    }

    @Test
    public void callAfterOutputMustHaveBeenProducedAlreadyAfterCallsImmediately() throws Exception {
        TransformResult<?> finishedResult = StepTransformResult.withoutHold(createdProducer).build();
        context.handleResult(null, ImmutableList.of(), finishedResult);
        final CountDownLatch callLatch = new CountDownLatch(1);
        context.extractFiredTimers();
        Runnable callback = callLatch::countDown;
        context.scheduleAfterOutputWouldBeProduced(downstream, INSTANCE, WindowingStrategy.globalDefault(), callback);
        Assert.assertThat(callLatch.await(1, TimeUnit.SECONDS), Matchers.is(true));
    }

    @Test
    public void extractFiredTimersExtractsTimers() {
        TransformResult<?> holdResult = StepTransformResult.withHold(createdProducer, new Instant(0)).build();
        context.handleResult(null, ImmutableList.of(), holdResult);
        StructuralKey<?> key = StructuralKey.of("foo".length(), VarIntCoder.of());
        TimerData toFire = TimerData.of(StateNamespaces.global(), new Instant(100L), EVENT_TIME);
        TransformResult<?> timerResult = StepTransformResult.withoutHold(downstreamProducer).withState(CopyOnAccessInMemoryStateInternals.withUnderlying(key, null)).withTimerUpdate(TimerUpdate.builder(key).setTimer(toFire).build()).build();
        // haven't added any timers, must be empty
        Assert.assertThat(context.extractFiredTimers(), Matchers.emptyIterable());
        context.handleResult(context.createKeyedBundle(key, created).commit(Instant.now()), ImmutableList.of(), timerResult);
        // timer hasn't fired
        Assert.assertThat(context.extractFiredTimers(), Matchers.emptyIterable());
        TransformResult<?> advanceResult = StepTransformResult.withoutHold(createdProducer).build();
        // Should cause the downstream timer to fire
        context.handleResult(null, ImmutableList.of(), advanceResult);
        Collection<FiredTimers<AppliedPTransform<?, ?, ?>>> fired = context.extractFiredTimers();
        Assert.assertThat(Iterables.getOnlyElement(fired).getKey(), Matchers.equalTo(key));
        FiredTimers<AppliedPTransform<?, ?, ?>> firedForKey = Iterables.getOnlyElement(fired);
        // Contains exclusively the fired timer
        Assert.assertThat(firedForKey.getTimers(), Matchers.contains(toFire));
        // Don't reextract timers
        Assert.assertThat(context.extractFiredTimers(), Matchers.emptyIterable());
    }

    @Test
    public void createKeyedBundleKeyed() {
        StructuralKey<String> key = StructuralKey.of("foo", StringUtf8Coder.of());
        CommittedBundle<KV<String, Integer>> keyedBundle = context.createKeyedBundle(key, downstream).commit(Instant.now());
        Assert.assertThat(keyedBundle.getKey(), Matchers.equalTo(key));
    }

    @Test
    public void isDoneWithUnboundedPCollection() {
        Assert.assertThat(context.isDone(unboundedProducer), Matchers.is(false));
        context.handleResult(null, ImmutableList.of(), StepTransformResult.withoutHold(unboundedProducer).build());
        context.extractFiredTimers();
        Assert.assertThat(context.isDone(unboundedProducer), Matchers.is(true));
    }

    @Test
    public void isDoneWithPartiallyDone() {
        Assert.assertThat(context.isDone(), Matchers.is(false));
        UncommittedBundle<Integer> rootBundle = context.createBundle(created);
        rootBundle.add(WindowedValue.valueInGlobalWindow(1));
        CommittedResult handleResult = context.handleResult(null, ImmutableList.of(), StepTransformResult.<Integer>withoutHold(createdProducer).addOutput(rootBundle).build());
        @SuppressWarnings("unchecked")
        CommittedBundle<Integer> committedBundle = ((CommittedBundle<Integer>) (Iterables.getOnlyElement(handleResult.getOutputs())));
        context.handleResult(null, ImmutableList.of(), StepTransformResult.withoutHold(unboundedProducer).build());
        Assert.assertThat(context.isDone(), Matchers.is(false));
        for (AppliedPTransform<?, ?, ?> consumers : graph.getPerElementConsumers(created)) {
            context.handleResult(committedBundle, ImmutableList.of(), StepTransformResult.withoutHold(consumers).build());
        }
        context.extractFiredTimers();
        Assert.assertThat(context.isDone(), Matchers.is(true));
    }

    private static class TestBoundedWindow extends BoundedWindow {
        private final Instant ts;

        public TestBoundedWindow(Instant ts) {
            this.ts = ts;
        }

        @Override
        public Instant maxTimestamp() {
            return ts;
        }
    }
}

