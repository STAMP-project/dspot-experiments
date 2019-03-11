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
package org.apache.beam.runners.dataflow.worker;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.INSTANCE;
import PaneInfo.NO_FIRING;
import SimpleParDoFn.CLEANUP_TIMER_ID;
import TimeDomain.EVENT_TIME;
import java.util.Collections;
import java.util.List;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.NullSideInputReader;
import org.apache.beam.runners.core.StateInternals;
import org.apache.beam.runners.core.StateNamespace;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.StateTag;
import org.apache.beam.runners.core.StateTags;
import org.apache.beam.runners.core.TimerInternals;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.worker.DataflowExecutionContext.DataflowStepContext;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OutputReceiver;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoFn;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Receiver;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsEqual;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link UserParDoFnFactory}.
 */
@RunWith(JUnit4.class)
public class UserParDoFnFactoryTest {
    static class TestDoFn extends DoFn<Integer, String> {
        enum State {

            UNSTARTED,
            SET_UP,
            STARTED,
            PROCESSING,
            FINISHED,
            TORN_DOWN;}

        UserParDoFnFactoryTest.TestDoFn.State state = UserParDoFnFactoryTest.TestDoFn.State.UNSTARTED;

        final List<TupleTag<String>> outputTags;

        public TestDoFn(List<TupleTag<String>> outputTags) {
            this.outputTags = outputTags;
        }

        @Setup
        public void setup() {
            state = UserParDoFnFactoryTest.TestDoFn.State.SET_UP;
        }

        @StartBundle
        public void startBundle() {
            Assert.assertThat(state, AnyOf.anyOf(IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.SET_UP), IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.FINISHED)));
            state = UserParDoFnFactoryTest.TestDoFn.State.STARTED;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            Assert.assertThat(state, AnyOf.anyOf(IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.STARTED), IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.PROCESSING)));
            state = UserParDoFnFactoryTest.TestDoFn.State.PROCESSING;
            String value = "processing: " + (c.element());
            c.output(value);
            for (TupleTag<String> additionalOutputTupleTag : outputTags) {
                c.output(additionalOutputTupleTag, (((additionalOutputTupleTag.getId()) + ": ") + value));
            }
        }

        @FinishBundle
        public void finishBundle(FinishBundleContext c) {
            Assert.assertThat(state, AnyOf.anyOf(IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.STARTED), IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.PROCESSING)));
            state = UserParDoFnFactoryTest.TestDoFn.State.FINISHED;
            c.output("finished", TIMESTAMP_MIN_VALUE, INSTANCE);
            for (TupleTag<String> additionalOutputTupleTag : outputTags) {
                c.output(additionalOutputTupleTag, (((additionalOutputTupleTag.getId()) + ": ") + "finished"), TIMESTAMP_MIN_VALUE, INSTANCE);
            }
        }

        @Teardown
        public void teardown() {
            Assert.assertThat(state, Matchers.not(IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.TORN_DOWN)));
            state = UserParDoFnFactoryTest.TestDoFn.State.TORN_DOWN;
        }
    }

    private static class TestStatefulDoFn extends DoFn<KV<String, Integer>, Void> {
        public static final String STATE_ID = "state-id";

        @StateId(UserParDoFnFactoryTest.TestStatefulDoFn.STATE_ID)
        private final StateSpec<ValueState<String>> spec = StateSpecs.value(StringUtf8Coder.of());

        @ProcessElement
        public void processElement(ProcessContext c) {
        }
    }

    private static final TupleTag<String> MAIN_OUTPUT = new TupleTag("1");

    private UserParDoFnFactory factory = UserParDoFnFactory.createDefault();

    @Test
    public void testFactoryReuseInStep() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        CounterSet counters = new CounterSet();
        UserParDoFnFactoryTest.TestDoFn initialFn = new UserParDoFnFactoryTest.TestDoFn(Collections.<TupleTag<String>>emptyList());
        CloudObject cloudObject = getCloudObject(initialFn);
        TestOperationContext operationContext = TestOperationContext.create(counters);
        ParDoFn parDoFn = factory.create(options, cloudObject, null, UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage"), operationContext);
        Receiver rcvr = new OutputReceiver();
        parDoFn.startBundle(rcvr);
        parDoFn.processElement(WindowedValue.valueInGlobalWindow("foo"));
        UserParDoFnFactoryTest.TestDoFn fn = ((UserParDoFnFactoryTest.TestDoFn) (getDoFnInfo().getDoFn()));
        Assert.assertThat(fn, Matchers.not(Matchers.theInstance(initialFn)));
        parDoFn.finishBundle();
        Assert.assertThat(fn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.FINISHED));
        // The fn should be reused for the second call to create
        ParDoFn secondParDoFn = factory.create(options, cloudObject, null, UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage"), operationContext);
        // The fn should still be finished from the last call; it should not be set up again
        Assert.assertThat(fn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.FINISHED));
        secondParDoFn.startBundle(rcvr);
        secondParDoFn.processElement(WindowedValue.valueInGlobalWindow("spam"));
        UserParDoFnFactoryTest.TestDoFn reobtainedFn = ((UserParDoFnFactoryTest.TestDoFn) (getDoFnInfo().getDoFn()));
        secondParDoFn.finishBundle();
        Assert.assertThat(reobtainedFn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.FINISHED));
        Assert.assertThat(fn, Matchers.theInstance(reobtainedFn));
    }

    @Test
    public void testFactorySimultaneousUse() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        CounterSet counters = new CounterSet();
        UserParDoFnFactoryTest.TestDoFn initialFn = new UserParDoFnFactoryTest.TestDoFn(Collections.<TupleTag<String>>emptyList());
        CloudObject cloudObject = getCloudObject(initialFn);
        ParDoFn parDoFn = factory.create(options, cloudObject, null, UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage"), TestOperationContext.create(counters));
        // The fn should not be reused while the first ParDoFn is not finished
        ParDoFn secondParDoFn = factory.create(options, cloudObject, null, UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage"), TestOperationContext.create(counters));
        Receiver rcvr = new OutputReceiver();
        parDoFn.startBundle(rcvr);
        parDoFn.processElement(WindowedValue.valueInGlobalWindow("foo"));
        // Must be after the first call to process element for reallyStartBundle to have been called
        UserParDoFnFactoryTest.TestDoFn firstDoFn = ((UserParDoFnFactoryTest.TestDoFn) (getDoFnInfo().getDoFn()));
        secondParDoFn.startBundle(rcvr);
        secondParDoFn.processElement(WindowedValue.valueInGlobalWindow("spam"));
        // Must be after the first call to process element for reallyStartBundle to have been called
        UserParDoFnFactoryTest.TestDoFn secondDoFn = ((UserParDoFnFactoryTest.TestDoFn) (getDoFnInfo().getDoFn()));
        parDoFn.finishBundle();
        secondParDoFn.finishBundle();
        Assert.assertThat(firstDoFn, Matchers.not(Matchers.theInstance(secondDoFn)));
        Assert.assertThat(firstDoFn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.FINISHED));
        Assert.assertThat(secondDoFn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.FINISHED));
    }

    @Test
    public void testFactoryDoesNotReuseAfterAborted() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        CounterSet counters = new CounterSet();
        UserParDoFnFactoryTest.TestDoFn initialFn = new UserParDoFnFactoryTest.TestDoFn(Collections.<TupleTag<String>>emptyList());
        CloudObject cloudObject = getCloudObject(initialFn);
        ParDoFn parDoFn = factory.create(options, cloudObject, null, UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage"), TestOperationContext.create(counters));
        Receiver rcvr = new OutputReceiver();
        parDoFn.startBundle(rcvr);
        parDoFn.processElement(WindowedValue.valueInGlobalWindow("foo"));
        UserParDoFnFactoryTest.TestDoFn fn = ((UserParDoFnFactoryTest.TestDoFn) (getDoFnInfo().getDoFn()));
        parDoFn.abort();
        Assert.assertThat(fn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.TORN_DOWN));
        // The fn should not be torn down here
        ParDoFn secondParDoFn = factory.create(options, cloudObject.clone(), null, UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.<TupleTag<?>, Integer>of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage"), TestOperationContext.create(counters));
        secondParDoFn.startBundle(rcvr);
        secondParDoFn.processElement(WindowedValue.valueInGlobalWindow("foo"));
        UserParDoFnFactoryTest.TestDoFn secondFn = ((UserParDoFnFactoryTest.TestDoFn) (getDoFnInfo().getDoFn()));
        Assert.assertThat(secondFn, Matchers.not(Matchers.theInstance(fn)));
        Assert.assertThat(fn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.TORN_DOWN));
        Assert.assertThat(secondFn.state, IsEqual.equalTo(UserParDoFnFactoryTest.TestDoFn.State.PROCESSING));
    }

    @Test
    public void testCleanupRegistered() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        CounterSet counters = new CounterSet();
        DoFn<?, ?> initialFn = new UserParDoFnFactoryTest.TestStatefulDoFn();
        CloudObject cloudObject = getCloudObject(initialFn, WindowingStrategy.globalDefault().withWindowFn(FixedWindows.of(Duration.millis(10))));
        TimerInternals timerInternals = Mockito.mock(TimerInternals.class);
        DataflowStepContext stepContext = Mockito.mock(DataflowStepContext.class);
        Mockito.when(stepContext.timerInternals()).thenReturn(timerInternals);
        DataflowExecutionContext<DataflowStepContext> executionContext = Mockito.mock(DataflowExecutionContext.class);
        TestOperationContext operationContext = TestOperationContext.create(counters);
        Mockito.when(executionContext.getStepContext(operationContext)).thenReturn(stepContext);
        Mockito.when(executionContext.getSideInputReader(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(NullSideInputReader.empty());
        ParDoFn parDoFn = factory.create(options, cloudObject, Collections.emptyList(), UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), executionContext, operationContext);
        Receiver rcvr = new OutputReceiver();
        parDoFn.startBundle(rcvr);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(0), new Instant(10));
        parDoFn.processElement(WindowedValue.of("foo", new Instant(1), firstWindow, NO_FIRING));
        Mockito.verify(stepContext).setStateCleanupTimer(CLEANUP_TIMER_ID, firstWindow, IntervalWindow.getCoder(), firstWindow.maxTimestamp().plus(1L));
    }

    @Test
    public void testCleanupWorks() throws Exception {
        PipelineOptions options = PipelineOptionsFactory.create();
        CounterSet counters = new CounterSet();
        DoFn<?, ?> initialFn = new UserParDoFnFactoryTest.TestStatefulDoFn();
        CloudObject cloudObject = getCloudObject(initialFn, WindowingStrategy.of(FixedWindows.of(Duration.millis(10))));
        StateInternals stateInternals = InMemoryStateInternals.forKey("dummy");
        // The overarching step context that only ParDoFn gets
        DataflowStepContext stepContext = Mockito.mock(DataflowStepContext.class);
        // The user step context that the DoFnRunner gets a handle on
        DataflowStepContext userStepContext = Mockito.mock(DataflowStepContext.class);
        Mockito.when(stepContext.namespacedToUser()).thenReturn(userStepContext);
        Mockito.when(userStepContext.stateInternals()).thenReturn(((StateInternals) (stateInternals)));
        DataflowExecutionContext<DataflowStepContext> executionContext = Mockito.mock(DataflowExecutionContext.class);
        TestOperationContext operationContext = TestOperationContext.create(counters);
        Mockito.when(executionContext.getStepContext(operationContext)).thenReturn(stepContext);
        Mockito.when(executionContext.getSideInputReader(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(NullSideInputReader.empty());
        ParDoFn parDoFn = factory.create(options, cloudObject, Collections.emptyList(), UserParDoFnFactoryTest.MAIN_OUTPUT, ImmutableMap.of(UserParDoFnFactoryTest.MAIN_OUTPUT, 0), executionContext, operationContext);
        Receiver rcvr = new OutputReceiver();
        parDoFn.startBundle(rcvr);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(0), new Instant(9));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(10), new Instant(19));
        Coder<IntervalWindow> windowCoder = IntervalWindow.getCoder();
        StateNamespace firstWindowNamespace = StateNamespaces.window(windowCoder, firstWindow);
        StateNamespace secondWindowNamespace = StateNamespaces.window(windowCoder, secondWindow);
        StateTag<ValueState<String>> tag = StateTags.tagForSpec(UserParDoFnFactoryTest.TestStatefulDoFn.STATE_ID, StateSpecs.value(StringUtf8Coder.of()));
        // Set up non-empty state. We don't mock + verify calls to clear() but instead
        // check that state is actually empty. We musn't care how it is accomplished.
        stateInternals.state(firstWindowNamespace, tag).write("first");
        stateInternals.state(secondWindowNamespace, tag).write("second");
        Mockito.when(userStepContext.getNextFiredTimer(windowCoder)).thenReturn(null);
        Mockito.when(stepContext.getNextFiredTimer(windowCoder)).thenReturn(TimerData.of(CLEANUP_TIMER_ID, firstWindowNamespace, firstWindow.maxTimestamp().plus(1L), EVENT_TIME)).thenReturn(null);
        // This should fire the timer to clean up the first window
        parDoFn.processTimers();
        Assert.assertThat(stateInternals.state(firstWindowNamespace, tag).read(), Matchers.nullValue());
        Assert.assertThat(stateInternals.state(secondWindowNamespace, tag).read(), IsEqual.equalTo("second"));
        Mockito.when(stepContext.getNextFiredTimer(((Coder) (windowCoder)))).thenReturn(TimerData.of(CLEANUP_TIMER_ID, secondWindowNamespace, secondWindow.maxTimestamp().plus(1L), EVENT_TIME)).thenReturn(null);
        // And this should clean up the second window
        parDoFn.processTimers();
        Assert.assertThat(stateInternals.state(firstWindowNamespace, tag).read(), Matchers.nullValue());
        Assert.assertThat(stateInternals.state(secondWindowNamespace, tag).read(), Matchers.nullValue());
    }
}

