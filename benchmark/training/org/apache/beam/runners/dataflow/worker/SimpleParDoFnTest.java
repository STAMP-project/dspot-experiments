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


import BatchModeExecutionContext.StepContext;
import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.INSTANCE;
import SimpleParDoFn.OUTPUTS_PER_ELEMENT_EXPERIMENT;
import com.google.api.services.dataflow.model.CounterUpdate;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.beam.runners.core.NullSideInputReader;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.core.metrics.MetricsContainerImpl;
import org.apache.beam.runners.dataflow.options.DataflowPipelineDebugOptions;
import org.apache.beam.runners.dataflow.worker.counters.CounterFactory.CounterDistribution;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoFn;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Receiver;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFnSchemaInformation;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.util.DoFnInfo;
import org.apache.beam.sdk.util.UserCodeException;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static SimpleDoFnRunnerFactory.INSTANCE;
import static org.apache.beam.runners.dataflow.worker.util.CounterHamcrestMatchers.CounterStructuredNameMatcher.hasStructuredName;
import static org.apache.beam.runners.dataflow.worker.util.CounterHamcrestMatchers.CounterUpdateDistributionMatcher.hasDistribution;


/**
 * Tests for {@link SimpleParDoFn}.
 */
@RunWith(JUnit4.class)
public class SimpleParDoFnTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private PipelineOptions options;

    private TestOperationContext operationContext;

    private StepContext stepContext;

    // TODO: Replace TestDoFn usages with a mock DoFn to reduce boilerplate.
    static class TestDoFn extends DoFn<Integer, String> {
        enum State {

            UNSTARTED,
            SET_UP,
            STARTED,
            PROCESSING,
            FINISHED,
            TORN_DOWN;}

        SimpleParDoFnTest.TestDoFn.State state = SimpleParDoFnTest.TestDoFn.State.UNSTARTED;

        final List<TupleTag<String>> outputTags;

        public TestDoFn(List<TupleTag<String>> outputTags) {
            this.outputTags = outputTags;
        }

        @Setup
        public void setup() {
            state = SimpleParDoFnTest.TestDoFn.State.SET_UP;
        }

        @StartBundle
        public void startBundle() {
            Assert.assertThat(state, AnyOf.anyOf(IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.SET_UP), IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.FINISHED)));
            state = SimpleParDoFnTest.TestDoFn.State.STARTED;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            Assert.assertThat(state, AnyOf.anyOf(IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.STARTED), IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.PROCESSING)));
            state = SimpleParDoFnTest.TestDoFn.State.PROCESSING;
            String value = "processing: " + (c.element());
            c.output(value);
            for (TupleTag<String> additionalOutputTupleTag : outputTags) {
                c.output(additionalOutputTupleTag, (((additionalOutputTupleTag.getId()) + ": ") + value));
            }
        }

        @FinishBundle
        public void finishBundle(FinishBundleContext c) {
            Assert.assertThat(state, AnyOf.anyOf(IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.STARTED), IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.PROCESSING)));
            state = SimpleParDoFnTest.TestDoFn.State.FINISHED;
            c.output("finished", TIMESTAMP_MIN_VALUE, INSTANCE);
            for (TupleTag<String> additionalOutputTupleTag : outputTags) {
                c.output(additionalOutputTupleTag, (((additionalOutputTupleTag.getId()) + ": ") + "finished"), TIMESTAMP_MIN_VALUE, INSTANCE);
            }
        }

        @Teardown
        public void teardown() {
            Assert.assertThat(state, Matchers.not(IsEqual.equalTo(SimpleParDoFnTest.TestDoFn.State.TORN_DOWN)));
            state = SimpleParDoFnTest.TestDoFn.State.TORN_DOWN;
        }
    }

    static class TestErrorDoFn extends DoFn<Integer, String> {
        // Used to test nested stack traces.
        private void nestedFunctionBeta(String s) {
            throw new RuntimeException(s);
        }

        private void nestedFunctionAlpha(String s) {
            nestedFunctionBeta(s);
        }

        @StartBundle
        public void startBundle() {
            nestedFunctionAlpha("test error in initialize");
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            nestedFunctionBeta("test error in process");
        }

        @FinishBundle
        public void finishBundle() {
            throw new RuntimeException("test error in finalize");
        }
    }

    static class TestReceiver implements Receiver {
        List<Object> receivedElems = new ArrayList<>();

        @Override
        public void process(Object outputElem) {
            receivedElems.add(outputElem);
        }
    }

    private static final TupleTag<String> MAIN_OUTPUT = new TupleTag("1");

    @Test
    public void testOutputReceivers() throws Exception {
        SimpleParDoFnTest.TestDoFn fn = new SimpleParDoFnTest.TestDoFn(ImmutableList.of(new TupleTag("tag1"), new TupleTag("tag2"), new TupleTag("tag3")));
        DoFnInfo<?, ?> fnInfo = /* side input views */
        /* input coder */
        DoFnInfo.forFn(fn, WindowingStrategy.globalDefault(), null, null, SimpleParDoFnTest.MAIN_OUTPUT, DoFnSchemaInformation.create());
        SimpleParDoFnTest.TestReceiver receiver = new SimpleParDoFnTest.TestReceiver();
        SimpleParDoFnTest.TestReceiver receiver1 = new SimpleParDoFnTest.TestReceiver();
        SimpleParDoFnTest.TestReceiver receiver2 = new SimpleParDoFnTest.TestReceiver();
        SimpleParDoFnTest.TestReceiver receiver3 = new SimpleParDoFnTest.TestReceiver();
        ParDoFn userParDoFn = new SimpleParDoFn(options, DoFnInstanceManagers.cloningPool(fnInfo), new SimpleParDoFnTest.EmptySideInputReader(), SimpleParDoFnTest.MAIN_OUTPUT, ImmutableMap.of(SimpleParDoFnTest.MAIN_OUTPUT, 0, new TupleTag<String>("tag1"), 1, new TupleTag<String>("tag2"), 2, new TupleTag<String>("tag3"), 3), BatchModeExecutionContext.forTesting(options, "testStage").getStepContext(operationContext), operationContext, DoFnSchemaInformation.create(), INSTANCE);
        userParDoFn.startBundle(receiver, receiver1, receiver2, receiver3);
        userParDoFn.processElement(WindowedValue.valueInGlobalWindow(3));
        userParDoFn.processElement(WindowedValue.valueInGlobalWindow(42));
        userParDoFn.processElement(WindowedValue.valueInGlobalWindow(666));
        userParDoFn.finishBundle();
        Object[] expectedReceivedElems = new Object[]{ WindowedValue.valueInGlobalWindow("processing: 3"), WindowedValue.valueInGlobalWindow("processing: 42"), WindowedValue.valueInGlobalWindow("processing: 666"), WindowedValue.valueInGlobalWindow("finished") };
        Assert.assertArrayEquals(expectedReceivedElems, receiver.receivedElems.toArray());
        Object[] expectedReceivedElems1 = new Object[]{ WindowedValue.valueInGlobalWindow("tag1: processing: 3"), WindowedValue.valueInGlobalWindow("tag1: processing: 42"), WindowedValue.valueInGlobalWindow("tag1: processing: 666"), WindowedValue.valueInGlobalWindow("tag1: finished") };
        Assert.assertArrayEquals(expectedReceivedElems1, receiver1.receivedElems.toArray());
        Object[] expectedReceivedElems2 = new Object[]{ WindowedValue.valueInGlobalWindow("tag2: processing: 3"), WindowedValue.valueInGlobalWindow("tag2: processing: 42"), WindowedValue.valueInGlobalWindow("tag2: processing: 666"), WindowedValue.valueInGlobalWindow("tag2: finished") };
        Assert.assertArrayEquals(expectedReceivedElems2, receiver2.receivedElems.toArray());
        Object[] expectedReceivedElems3 = new Object[]{ WindowedValue.valueInGlobalWindow("tag3: processing: 3"), WindowedValue.valueInGlobalWindow("tag3: processing: 42"), WindowedValue.valueInGlobalWindow("tag3: processing: 666"), WindowedValue.valueInGlobalWindow("tag3: finished") };
        Assert.assertArrayEquals(expectedReceivedElems3, receiver3.receivedElems.toArray());
    }

    @Test
    @SuppressWarnings("AssertionFailureIgnored")
    public void testUnexpectedNumberOfReceivers() throws Exception {
        SimpleParDoFnTest.TestDoFn fn = new SimpleParDoFnTest.TestDoFn(Collections.emptyList());
        DoFnInfo<?, ?> fnInfo = /* side input views */
        /* input coder */
        DoFnInfo.forFn(fn, WindowingStrategy.globalDefault(), null, null, SimpleParDoFnTest.MAIN_OUTPUT, DoFnSchemaInformation.create());
        SimpleParDoFnTest.TestReceiver receiver = new SimpleParDoFnTest.TestReceiver();
        ParDoFn userParDoFn = new SimpleParDoFn(options, DoFnInstanceManagers.singleInstance(fnInfo), new SimpleParDoFnTest.EmptySideInputReader(), SimpleParDoFnTest.MAIN_OUTPUT, ImmutableMap.of(SimpleParDoFnTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage").getStepContext(operationContext), operationContext, DoFnSchemaInformation.create(), INSTANCE);
        try {
            userParDoFn.startBundle();
            Assert.fail("should have failed");
        } catch (Throwable exn) {
            Assert.assertThat(exn.toString(), CoreMatchers.containsString("unexpected number of receivers"));
        }
        try {
            userParDoFn.startBundle(receiver, receiver);
            Assert.fail("should have failed");
        } catch (Throwable exn) {
            Assert.assertThat(exn.toString(), CoreMatchers.containsString("unexpected number of receivers"));
        }
    }

    @Test
    public void testErrorPropagation() throws Exception {
        SimpleParDoFnTest.TestErrorDoFn fn = new SimpleParDoFnTest.TestErrorDoFn();
        DoFnInfo<?, ?> fnInfo = /* side input views */
        /* input coder */
        DoFnInfo.forFn(fn, WindowingStrategy.globalDefault(), null, null, SimpleParDoFnTest.MAIN_OUTPUT, DoFnSchemaInformation.create());
        SimpleParDoFnTest.TestReceiver receiver = new SimpleParDoFnTest.TestReceiver();
        ParDoFn userParDoFn = new SimpleParDoFn(options, DoFnInstanceManagers.singleInstance(fnInfo), new SimpleParDoFnTest.EmptySideInputReader(), SimpleParDoFnTest.MAIN_OUTPUT, ImmutableMap.of(SimpleParDoFnTest.MAIN_OUTPUT, 0), BatchModeExecutionContext.forTesting(options, "testStage").getStepContext(operationContext), operationContext, DoFnSchemaInformation.create(), INSTANCE);
        try {
            userParDoFn.startBundle(receiver);
            userParDoFn.processElement(null);
            Assert.fail("should have failed");
        } catch (Exception exn) {
            // Because we're calling this from inside the SDK and not from a
            // user's program (e.g. through Pipeline.run), the error should
            // be thrown as a UserCodeException. The cause of the
            // UserCodeError shouldn't contain any of the stack from within
            // the SDK, since we don't want to overwhelm users with stack
            // frames outside of their control.
            Assert.assertThat(exn, IsInstanceOf.instanceOf(UserCodeException.class));
            // Stack trace of the cause should contain three frames:
            // TestErrorDoFn.nestedFunctionBeta
            // TestErrorDoFn.nestedFunctionAlpha
            // TestErrorDoFn.startBundle
            Assert.assertThat(stackTraceFrameStrings(exn.getCause()), contains(CoreMatchers.containsString("TestErrorDoFn.nestedFunctionBeta"), CoreMatchers.containsString("TestErrorDoFn.nestedFunctionAlpha"), CoreMatchers.containsString("TestErrorDoFn.startBundle")));
            Assert.assertThat(exn.toString(), CoreMatchers.containsString("test error in initialize"));
        }
        try {
            userParDoFn.processElement(WindowedValue.valueInGlobalWindow(3));
            Assert.fail("should have failed");
        } catch (Exception exn) {
            // Exception should be a UserCodeException since we're calling
            // from inside the SDK.
            Assert.assertThat(exn, IsInstanceOf.instanceOf(UserCodeException.class));
            // Stack trace of the cause should contain two frames:
            // TestErrorDoFn.nestedFunctionBeta
            // TestErrorDoFn.processElement
            Assert.assertThat(stackTraceFrameStrings(exn.getCause()), contains(CoreMatchers.containsString("TestErrorDoFn.nestedFunctionBeta"), CoreMatchers.containsString("TestErrorDoFn.processElement")));
            Assert.assertThat(exn.toString(), CoreMatchers.containsString("test error in process"));
        }
        try {
            userParDoFn.finishBundle();
            Assert.fail("should have failed");
        } catch (Exception exn) {
            // Exception should be a UserCodeException since we're calling
            // from inside the SDK.
            Assert.assertThat(exn, IsInstanceOf.instanceOf(UserCodeException.class));
            // Stack trace should only contain a single frame:
            // TestErrorDoFn.finishBundle
            Assert.assertThat(stackTraceFrameStrings(exn.getCause()), contains(CoreMatchers.containsString("TestErrorDoFn.finishBundle")));
            Assert.assertThat(exn.toString(), CoreMatchers.containsString("test error in finalize"));
        }
    }

    @Test
    public void testUndeclaredSideOutputs() throws Exception {
        SimpleParDoFnTest.TestDoFn fn = new SimpleParDoFnTest.TestDoFn(ImmutableList.of(new TupleTag("declared"), new TupleTag("undecl1"), new TupleTag("undecl2"), new TupleTag("undecl3")));
        DoFnInfo<?, ?> fnInfo = /* side input views */
        /* input coder */
        DoFnInfo.forFn(fn, WindowingStrategy.globalDefault(), null, null, SimpleParDoFnTest.MAIN_OUTPUT, DoFnSchemaInformation.create());
        CounterSet counters = new CounterSet();
        TestOperationContext operationContext = TestOperationContext.create(counters);
        ParDoFn userParDoFn = new SimpleParDoFn(options, DoFnInstanceManagers.cloningPool(fnInfo), NullSideInputReader.empty(), SimpleParDoFnTest.MAIN_OUTPUT, ImmutableMap.of(SimpleParDoFnTest.MAIN_OUTPUT, 0, new TupleTag<String>("declared"), 1), BatchModeExecutionContext.forTesting(options, "testStage").getStepContext(operationContext), operationContext, DoFnSchemaInformation.create(), INSTANCE);
        userParDoFn.startBundle(new SimpleParDoFnTest.TestReceiver(), new SimpleParDoFnTest.TestReceiver());
        thrown.expect(UserCodeException.class);
        thrown.expectCause(IsInstanceOf.instanceOf(IllegalArgumentException.class));
        thrown.expectMessage("Unknown output tag");
        userParDoFn.processElement(WindowedValue.valueInGlobalWindow(5));
    }

    @Test
    public void testStateTracking() throws Exception {
        ExecutionStateTracker tracker = ExecutionStateTracker.newForTest();
        TestOperationContext operationContext = TestOperationContext.create(new CounterSet(), NameContextsForTests.nameContextForTest(), new MetricsContainerImpl(NameContextsForTests.ORIGINAL_NAME), tracker);
        class StateTestingDoFn extends DoFn<Integer, String> {
            private boolean startCalled = false;

            @StartBundle
            public void startBundle() throws Exception {
                startCalled = true;
                Assert.assertThat(tracker.getCurrentState(), IsEqual.equalTo(operationContext.getStartState()));
            }

            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                Assert.assertThat(startCalled, IsEqual.equalTo(true));
                Assert.assertThat(tracker.getCurrentState(), IsEqual.equalTo(operationContext.getProcessState()));
            }
        }
        StateTestingDoFn fn = new StateTestingDoFn();
        DoFnInfo<?, ?> fnInfo = /* side input views */
        /* input coder */
        DoFnInfo.forFn(fn, WindowingStrategy.globalDefault(), null, null, SimpleParDoFnTest.MAIN_OUTPUT, DoFnSchemaInformation.create());
        ParDoFn userParDoFn = new SimpleParDoFn(options, DoFnInstanceManagers.singleInstance(fnInfo), NullSideInputReader.empty(), SimpleParDoFnTest.MAIN_OUTPUT, ImmutableMap.of(SimpleParDoFnTest.MAIN_OUTPUT, 0, new TupleTag("declared"), 1), BatchModeExecutionContext.forTesting(options, counterFactory(), "testStage").getStepContext(operationContext), operationContext, DoFnSchemaInformation.create(), INSTANCE);
        // This test ensures proper behavior of the state sampling even with lazy initialization.
        try (Closeable trackerCloser = tracker.activate()) {
            try (Closeable processCloser = enterProcess()) {
                userParDoFn.processElement(WindowedValue.valueInGlobalWindow(5));
            }
        }
    }

    @Test
    public void testOutputsPerElementCounter() throws Exception {
        int[] inputData = new int[]{ 1, 2, 3, 4, 5 };
        CounterDistribution expectedDistribution = CounterDistribution.builder().minMax(1, 5).count(5).sum(((((1 + 2) + 3) + 4) + 5)).sumOfSquares(((((1 + 4) + 9) + 16) + 25)).buckets(1, Lists.newArrayList(1L, 3L, 1L)).build();
        List<CounterUpdate> counterUpdates = executeParDoFnCounterTest(inputData);
        CounterName expectedName = CounterName.named("per-element-output-count").withOriginalName(stepContext.getNameContext());
        Assert.assertThat(counterUpdates, contains(Matchers.allOf(hasStructuredName(expectedName, "DISTRIBUTION"), hasDistribution(expectedDistribution))));
    }

    // TODO: Remove once Distributions has shipped.
    @Test
    public void testOutputsPerElementCounterDisabledViaExperiment() throws Exception {
        DataflowPipelineDebugOptions debugOptions = options.as(DataflowPipelineDebugOptions.class);
        List<String> experiments = debugOptions.getExperiments();
        experiments.remove(OUTPUTS_PER_ELEMENT_EXPERIMENT);
        debugOptions.setExperiments(experiments);
        List<CounterUpdate> counterUpdates = executeParDoFnCounterTest(0);
        CounterName expectedName = CounterName.named("per-element-output-count").withOriginalName(stepContext.getNameContext());
        Assert.assertThat(counterUpdates, Matchers.not(contains(hasStructuredName(expectedName, "DISTRIBUTION"))));
    }

    /**
     * Basic side input reader wrapping a tagged {@link Map} of side input iterables. Encapsulates
     * conversion according to the {@link PCollectionView} and projection to a particular window.
     */
    private static class EmptySideInputReader implements SideInputReader {
        private EmptySideInputReader() {
        }

        @Override
        public <T> boolean contains(PCollectionView<T> view) {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return true;
        }

        @Override
        public <T> T get(PCollectionView<T> view, final BoundedWindow window) {
            throw new IllegalArgumentException("calling getSideInput() with unknown view");
        }
    }
}

