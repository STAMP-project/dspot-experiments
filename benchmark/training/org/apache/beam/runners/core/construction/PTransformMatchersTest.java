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


import DefaultFilenamePolicy.DEFAULT_UNWINDOWED_SHARD_TEMPLATE;
import IsBounded.BOUNDED;
import ParDo.SingleOutput;
import TimeDomain.EVENT_TIME;
import java.io.Serializable;
import java.util.Collections;
import javax.annotation.Nullable;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.io.DefaultFilenamePolicy;
import org.apache.beam.sdk.io.DynamicFileDestinations;
import org.apache.beam.sdk.io.FileBasedSink.FilenamePolicy;
import org.apache.beam.sdk.io.LocalResources;
import org.apache.beam.sdk.io.WriteFiles;
import org.apache.beam.sdk.io.fs.ResourceId;
import org.apache.beam.sdk.options.ValueProvider.StaticValueProvider;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.runners.PTransformMatcher;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.Timer;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.Flatten;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.View.CreatePCollectionView;
import org.apache.beam.sdk.transforms.ViewFn;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.PCollectionViews;
import org.apache.beam.sdk.values.PValue;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.MoreObjects;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PTransformMatcher}.
 */
@RunWith(JUnit4.class)
public class PTransformMatchersTest implements Serializable {
    @Rule
    public transient TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void classEqualToMatchesSameClass() {
        PTransformMatcher matcher = PTransformMatchers.classEqualTo(SingleOutput.class);
        AppliedPTransform<?, ?, ?> application = getAppliedTransform(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
            @ProcessElement
            public void doStuff(ProcessContext ctxt) {
            }
        }));
        Assert.assertThat(matcher.matches(application), Matchers.is(true));
    }

    @Test
    public void classEqualToDoesNotMatchSubclass() {
        class MyPTransform extends PTransform<PCollection<KV<String, Integer>>, PCollection<Integer>> {
            @Override
            public PCollection<Integer> expand(PCollection<KV<String, Integer>> input) {
                return PCollection.createPrimitiveOutputInternal(input.getPipeline(), input.getWindowingStrategy(), input.isBounded(), VarIntCoder.of());
            }
        }
        PTransformMatcher matcher = PTransformMatchers.classEqualTo(MyPTransform.class);
        MyPTransform subclass = new MyPTransform() {};
        Assert.assertThat(subclass.getClass(), Matchers.not(Matchers.<Class<?>>equalTo(MyPTransform.class)));
        Assert.assertThat(subclass, Matchers.instanceOf(MyPTransform.class));
        AppliedPTransform<?, ?, ?> application = getAppliedTransform(subclass);
        Assert.assertThat(matcher.matches(application), Matchers.is(false));
    }

    @Test
    public void classEqualToDoesNotMatchUnrelatedClass() {
        PTransformMatcher matcher = PTransformMatchers.classEqualTo(SingleOutput.class);
        AppliedPTransform<?, ?, ?> application = getAppliedTransform(Window.<KV<String, Integer>>into(new GlobalWindows()));
        Assert.assertThat(matcher.matches(application), Matchers.is(false));
    }

    private org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer> doFn = new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
        @ProcessElement
        public void simpleProcess(ProcessContext ctxt) {
            ctxt.output(((ctxt.element().getValue()) + 1));
        }
    };

    private abstract static class SomeTracker extends RestrictionTracker<Void, Void> {}

    private org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer> splittableDoFn = new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
        @ProcessElement
        public void processElement(ProcessContext context, PTransformMatchersTest.SomeTracker tracker) {
        }

        @GetInitialRestriction
        public Void getInitialRestriction(KV<String, Integer> element) {
            return null;
        }

        @NewTracker
        public PTransformMatchersTest.SomeTracker newTracker(Void restriction) {
            return null;
        }
    };

    private org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer> doFnWithState = new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
        private final String stateId = "mystate";

        @StateId(stateId)
        private final StateSpec<ValueState<Integer>> intState = StateSpecs.value(VarIntCoder.of());

        @ProcessElement
        public void processElement(ProcessContext c, @StateId(stateId)
        ValueState<Integer> state) {
            Integer currentValue = MoreObjects.firstNonNull(state.read(), 0);
            c.output(currentValue);
            state.write((currentValue + 1));
        }
    };

    private org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer> doFnWithTimers = new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Integer>() {
        private final String timerId = "myTimer";

        @TimerId(timerId)
        private final TimerSpec spec = TimerSpecs.timer(EVENT_TIME);

        @ProcessElement
        public void processElement(ProcessContext context, @TimerId(timerId)
        Timer timer) {
            timer.offset(Duration.standardSeconds(1)).setRelative();
            context.output(3);
        }

        @OnTimer(timerId)
        public void onTimer(OnTimerContext context) {
            context.output(42);
        }
    };

    /**
     * Demonstrates that a {@link ParDo.SingleOutput} does not match any ParDo matcher.
     */
    @Test
    public void parDoSingle() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(doFn));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoSingleSplittable() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(splittableDoFn));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoSingleWithState() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(doFnWithState));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoSingleWithTimers() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(doFnWithTimers));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoMulti() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(doFn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoMultiSplittable() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(splittableDoFn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoSplittable() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(splittableDoFn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.splittableParDo().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoMultiWithState() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(doFnWithState).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoWithState() {
        AppliedPTransform<?, ?, ?> statefulApplication = getAppliedTransform(ParDo.of(doFnWithState).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDo().matches(statefulApplication), Matchers.is(true));
        AppliedPTransform<?, ?, ?> splittableApplication = getAppliedTransform(ParDo.of(splittableDoFn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDo().matches(splittableApplication), Matchers.is(false));
    }

    @Test
    public void parDoMultiWithTimers() {
        AppliedPTransform<?, ?, ?> parDoApplication = getAppliedTransform(ParDo.of(doFnWithTimers).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoMulti().matches(parDoApplication), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.splittableParDoMulti().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.splittableParDoSingle().matches(parDoApplication), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.stateOrTimerParDoSingle().matches(parDoApplication), Matchers.is(false));
    }

    @Test
    public void parDoRequiresStableInput() {
        org.apache.beam.sdk.transforms.DoFn<Object, Object> doFnRSI = new org.apache.beam.sdk.transforms.DoFn<Object, Object>() {
            @RequiresStableInput
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        };
        AppliedPTransform<?, ?, ?> single = getAppliedTransform(ParDo.of(doFn));
        AppliedPTransform<?, ?, ?> singleRSI = getAppliedTransform(ParDo.of(doFnRSI));
        AppliedPTransform<?, ?, ?> multi = getAppliedTransform(ParDo.of(doFn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        AppliedPTransform<?, ?, ?> multiRSI = getAppliedTransform(ParDo.of(doFnRSI).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoSingle().matches(single), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoSingle().matches(singleRSI), Matchers.is(true));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoSingle().matches(multi), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoSingle().matches(multiRSI), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoMulti().matches(single), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoMulti().matches(singleRSI), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoMulti().matches(multi), Matchers.is(false));
        Assert.assertThat(PTransformMatchers.requiresStableInputParDoMulti().matches(multiRSI), Matchers.is(true));
    }

    @Test
    public void parDoWithFnTypeWithMatchingType() {
        org.apache.beam.sdk.transforms.DoFn<Object, Object> fn = new org.apache.beam.sdk.transforms.DoFn<Object, Object>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        };
        AppliedPTransform<?, ?, ?> parDoSingle = getAppliedTransform(ParDo.of(fn));
        AppliedPTransform<?, ?, ?> parDoMulti = getAppliedTransform(ParDo.of(fn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        PTransformMatcher matcher = PTransformMatchers.parDoWithFnType(fn.getClass());
        Assert.assertThat(matcher.matches(parDoSingle), Matchers.is(true));
        Assert.assertThat(matcher.matches(parDoMulti), Matchers.is(true));
    }

    @Test
    public void parDoWithFnTypeWithNoMatch() {
        org.apache.beam.sdk.transforms.DoFn<Object, Object> fn = new org.apache.beam.sdk.transforms.DoFn<Object, Object>() {
            @ProcessElement
            public void process(ProcessContext ctxt) {
            }
        };
        AppliedPTransform<?, ?, ?> parDoSingle = getAppliedTransform(ParDo.of(fn));
        AppliedPTransform<?, ?, ?> parDoMulti = getAppliedTransform(ParDo.of(fn).withOutputTags(new org.apache.beam.sdk.values.TupleTag(), TupleTagList.empty()));
        PTransformMatcher matcher = PTransformMatchers.parDoWithFnType(doFnWithState.getClass());
        Assert.assertThat(matcher.matches(parDoSingle), Matchers.is(false));
        Assert.assertThat(matcher.matches(parDoMulti), Matchers.is(false));
    }

    @Test
    public void parDoWithFnTypeNotParDo() {
        AppliedPTransform<?, ?, ?> notParDo = getAppliedTransform(Create.empty(VoidCoder.of()));
        PTransformMatcher matcher = PTransformMatchers.parDoWithFnType(doFnWithState.getClass());
        Assert.assertThat(matcher.matches(notParDo), Matchers.is(false));
    }

    @Test
    public void createViewWithViewFn() {
        PCollection<Integer> input = p.apply(Create.of(1));
        PCollectionView<Iterable<Integer>> view = input.apply(View.asIterable());
        ViewFn<?, ?> viewFn = view.getViewFn();
        CreatePCollectionView<?, ?> createView = CreatePCollectionView.of(view);
        PTransformMatcher matcher = PTransformMatchers.createViewWithViewFn(viewFn.getClass());
        Assert.assertThat(matcher.matches(getAppliedTransform(createView)), Matchers.is(true));
    }

    @Test
    public void createViewWithViewFnDifferentViewFn() {
        PCollection<Integer> input = p.apply(Create.of(1));
        PCollectionView<Iterable<Integer>> view = input.apply(View.asIterable());
        // Purposely create a subclass to get a different class then what was expected.
        ViewFn<?, ?> viewFn = new PCollectionViews.IterableViewFn() {};
        CreatePCollectionView<?, ?> createView = CreatePCollectionView.of(view);
        PTransformMatcher matcher = PTransformMatchers.createViewWithViewFn(viewFn.getClass());
        Assert.assertThat(matcher.matches(getAppliedTransform(createView)), Matchers.is(false));
    }

    @Test
    public void createViewWithViewFnNotCreatePCollectionView() {
        PCollection<Integer> input = p.apply(Create.of(1));
        PCollectionView<Iterable<Integer>> view = input.apply(View.asIterable());
        PTransformMatcher matcher = PTransformMatchers.createViewWithViewFn(view.getViewFn().getClass());
        Assert.assertThat(matcher.matches(getAppliedTransform(View.asIterable())), Matchers.is(false));
    }

    @Test
    public void emptyFlattenWithEmptyFlatten() {
        AppliedPTransform application = AppliedPTransform.of("EmptyFlatten", Collections.emptyMap(), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Flatten.pCollections(), p);
        Assert.assertThat(PTransformMatchers.emptyFlatten().matches(application), Matchers.is(true));
    }

    @Test
    public void emptyFlattenWithNonEmptyFlatten() {
        AppliedPTransform application = AppliedPTransform.of("Flatten", Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Flatten.pCollections(), p);
        Assert.assertThat(PTransformMatchers.emptyFlatten().matches(application), Matchers.is(false));
    }

    @Test
    public void emptyFlattenWithNonFlatten() {
        AppliedPTransform application = /* This isn't actually possible to construct, but for the sake of example */
        AppliedPTransform.<PCollection<Iterable<Integer>>, PCollection<Integer>, Flatten.Iterables<Integer>>of("EmptyFlatten", Collections.emptyMap(), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Flatten.iterables(), p);
        Assert.assertThat(PTransformMatchers.emptyFlatten().matches(application), Matchers.is(false));
    }

    @Test
    public void flattenWithDuplicateInputsWithoutDuplicates() {
        AppliedPTransform application = AppliedPTransform.of("Flatten", Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Flatten.pCollections(), p);
        Assert.assertThat(PTransformMatchers.flattenWithDuplicateInputs().matches(application), Matchers.is(false));
    }

    @Test
    public void flattenWithDuplicateInputsWithDuplicates() {
        PCollection<Integer> duplicate = PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of());
        AppliedPTransform application = AppliedPTransform.of("Flatten", ImmutableMap.<org.apache.beam.sdk.values.TupleTag<?>, PValue>builder().put(new org.apache.beam.sdk.values.TupleTag<Integer>(), duplicate).put(new org.apache.beam.sdk.values.TupleTag<Integer>(), duplicate).build(), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Flatten.pCollections(), p);
        Assert.assertThat(PTransformMatchers.flattenWithDuplicateInputs().matches(application), Matchers.is(true));
    }

    @Test
    public void flattenWithDuplicateInputsNonFlatten() {
        AppliedPTransform application = /* This isn't actually possible to construct, but for the sake of example */
        AppliedPTransform.<PCollection<Iterable<Integer>>, PCollection<Integer>, Flatten.Iterables<Integer>>of("EmptyFlatten", Collections.emptyMap(), Collections.singletonMap(new org.apache.beam.sdk.values.TupleTag<Integer>(), PCollection.createPrimitiveOutputInternal(p, WindowingStrategy.globalDefault(), BOUNDED, VarIntCoder.of())), Flatten.iterables(), p);
        Assert.assertThat(PTransformMatchers.flattenWithDuplicateInputs().matches(application), Matchers.is(false));
    }

    @Test
    public void writeWithRunnerDeterminedSharding() {
        ResourceId outputDirectory = /* isDirectory */
        LocalResources.fromString("/foo/bar", true);
        FilenamePolicy policy = DefaultFilenamePolicy.fromStandardParameters(StaticValueProvider.of(outputDirectory), DEFAULT_UNWINDOWED_SHARD_TEMPLATE, "", false);
        WriteFiles<Integer, Void, Integer> write = WriteFiles.to(new org.apache.beam.sdk.io.FileBasedSink<Integer, Void, Integer>(StaticValueProvider.of(outputDirectory), DynamicFileDestinations.constant(policy)) {
            @Override
            public WriteOperation<Void, Integer> createWriteOperation() {
                return null;
            }
        });
        Assert.assertThat(PTransformMatchers.writeWithRunnerDeterminedSharding().matches(appliedWrite(write)), Matchers.is(true));
        WriteFiles<Integer, Void, Integer> withStaticSharding = write.withNumShards(3);
        Assert.assertThat(PTransformMatchers.writeWithRunnerDeterminedSharding().matches(appliedWrite(withStaticSharding)), Matchers.is(false));
        WriteFiles<Integer, Void, Integer> withCustomSharding = write.withSharding(Sum.integersGlobally().asSingletonView());
        Assert.assertThat(PTransformMatchers.writeWithRunnerDeterminedSharding().matches(appliedWrite(withCustomSharding)), Matchers.is(false));
    }

    private static class FakeFilenamePolicy extends FilenamePolicy {
        @Override
        public ResourceId windowedFilename(int shardNumber, int numShards, BoundedWindow window, PaneInfo paneInfo, org.apache.beam.sdk.io.FileBasedSink.OutputFileHints outputFileHints) {
            throw new UnsupportedOperationException("should not be called");
        }

        @Nullable
        @Override
        public ResourceId unwindowedFilename(int shardNumber, int numShards, org.apache.beam.sdk.io.FileBasedSink.OutputFileHints outputFileHints) {
            throw new UnsupportedOperationException("should not be called");
        }
    }
}

