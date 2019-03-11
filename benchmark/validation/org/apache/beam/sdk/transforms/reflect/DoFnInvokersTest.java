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
package org.apache.beam.sdk.transforms.reflect;


import DoFn.FinishBundleContext;
import DoFn.ProcessContext;
import DoFn.StartBundleContext;
import DoFnInvoker.ArgumentProvider;
import TimeDomain.EVENT_TIME;
import TimeDomain.PROCESSING_TIME;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.coders.AtomicCoder;
import org.apache.beam.sdk.coders.CoderProviders;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.Timer;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.DoFn.MultiOutputReceiver;
import org.apache.beam.sdk.transforms.DoFn.OutputReceiver;
import org.apache.beam.sdk.transforms.reflect.testhelper.DoFnInvokersTestHelper;
import org.apache.beam.sdk.transforms.splittabledofn.HasDefaultTracker;
import org.apache.beam.sdk.transforms.splittabledofn.RestrictionTracker;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.UserCodeException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.AdditionalAnswers;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;

import static ProcessContinuation.resume;
import static ProcessContinuation.stop;


/**
 * Tests for {@link DoFnInvokers}.
 */
@RunWith(JUnit4.class)
public class DoFnInvokersTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private StartBundleContext mockStartBundleContext;

    @Mock
    private FinishBundleContext mockFinishBundleContext;

    @Mock
    private ProcessContext mockProcessContext;

    private String mockElement;

    private Instant mockTimestamp;

    @Mock
    private OutputReceiver<String> mockOutputReceiver;

    @Mock
    private MultiOutputReceiver mockMultiOutputReceiver;

    @Mock
    private IntervalWindow mockWindow;

    // @Mock private PaneInfo mockPaneInfo;
    @Mock
    private ArgumentProvider<String, String> mockArgumentProvider;

    @Test
    public void testDoFnInvokersReused() throws Exception {
        // Ensures that we don't create a new Invoker class for every instance of the DoFn.
        DoFnInvokersTest.IdentityParent fn1 = new DoFnInvokersTest.IdentityParent();
        DoFnInvokersTest.IdentityParent fn2 = new DoFnInvokersTest.IdentityParent();
        Assert.assertSame("Invoker classes should only be generated once for each type", DoFnInvokers.invokerFor(fn1).getClass(), DoFnInvokers.invokerFor(fn2).getClass());
    }

    // ---------------------------------------------------------------------------------------
    // Tests for general invocations of DoFn methods.
    // ---------------------------------------------------------------------------------------
    @Test
    public void testDoFnWithNoExtraContext() throws Exception {
        class MockFn extends DoFn<String, String> {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
            }
        }
        MockFn mockFn = Mockito.mock(MockFn.class);
        Assert.assertEquals(stop(), invokeProcessElement(mockFn));
        Mockito.verify(mockFn).processElement(mockProcessContext);
    }

    interface InterfaceWithProcessElement {
        @DoFn.ProcessElement
        void processElement(ProcessContext c) {
        }
    }

    interface LayersOfInterfaces extends DoFnInvokersTest.InterfaceWithProcessElement {}

    private static class IdentityUsingInterfaceWithProcessElement extends DoFn<String, String> implements DoFnInvokersTest.LayersOfInterfaces {
        @Override
        public void processElement(ProcessContext c) {
        }
    }

    @Test
    public void testDoFnWithProcessElementInterface() throws Exception {
        DoFnInvokersTest.IdentityUsingInterfaceWithProcessElement fn = Mockito.mock(DoFnInvokersTest.IdentityUsingInterfaceWithProcessElement.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).processElement(mockProcessContext);
    }

    private static class IdentityParent extends DoFn<String, String> {
        @ProcessElement
        public void process(ProcessContext c) {
        }
    }

    @SuppressWarnings("ClassCanBeStatic")
    private class IdentityChildWithoutOverride extends DoFnInvokersTest.IdentityParent {}

    @SuppressWarnings("ClassCanBeStatic")
    private class IdentityChildWithOverride extends DoFnInvokersTest.IdentityParent {
        @Override
        public void process(ProcessContext c) {
            super.process(c);
        }
    }

    @Test
    public void testDoFnWithMethodInSuperclass() throws Exception {
        DoFnInvokersTest.IdentityChildWithoutOverride fn = Mockito.mock(DoFnInvokersTest.IdentityChildWithoutOverride.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).process(mockProcessContext);
    }

    @Test
    public void testDoFnWithMethodInSubclass() throws Exception {
        DoFnInvokersTest.IdentityChildWithOverride fn = Mockito.mock(DoFnInvokersTest.IdentityChildWithOverride.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).process(mockProcessContext);
    }

    @Test
    public void testDoFnWithWindow() throws Exception {
        class MockFn extends DoFn<String, String> {
            @DoFn.ProcessElement
            public void processElement(ProcessContext c, IntervalWindow w) throws Exception {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).processElement(mockProcessContext, mockWindow);
    }

    @Test
    public void testDoFnWithAllParameters() throws Exception {
        class MockFn extends DoFn<String, String> {
            @DoFn.ProcessElement
            public void processElement(ProcessContext c, @Element
            String element, @Timestamp
            Instant timestamp, IntervalWindow w, // PaneInfo p,
            OutputReceiver<String> receiver, MultiOutputReceiver multiReceiver) throws Exception {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).processElement(mockProcessContext, mockElement, mockTimestamp, mockWindow, mockOutputReceiver, mockMultiOutputReceiver);
    }

    /**
     * Tests that the generated {@link DoFnInvoker} passes the state parameter that it should.
     */
    @Test
    public void testDoFnWithState() throws Exception {
        ValueState<Integer> mockState = Mockito.mock(ValueState.class);
        final String stateId = "my-state-id-here";
        Mockito.when(mockArgumentProvider.state(stateId)).thenReturn(mockState);
        class MockFn extends DoFn<String, String> {
            @StateId(stateId)
            private final StateSpec<ValueState<Integer>> spec = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void processElement(ProcessContext c, @StateId(stateId)
            ValueState<Integer> valueState) throws Exception {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).processElement(mockProcessContext, mockState);
    }

    /**
     * Tests that the generated {@link DoFnInvoker} passes the timer parameter that it should.
     */
    @Test
    public void testDoFnWithTimer() throws Exception {
        Timer mockTimer = Mockito.mock(Timer.class);
        final String timerId = "my-timer-id-here";
        Mockito.when(mockArgumentProvider.timer(timerId)).thenReturn(mockTimer);
        class MockFn extends DoFn<String, String> {
            @TimerId(timerId)
            private final TimerSpec spec = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void processElement(ProcessContext c, @TimerId(timerId)
            Timer timer) throws Exception {
            }

            @OnTimer(timerId)
            public void onTimer() {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).processElement(mockProcessContext, mockTimer);
    }

    @Test
    public void testOnWindowExpirationWithNoParam() throws Exception {
        class MockFn extends DoFn<String, String> {
            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnWindowExpiration
            public void onWindowExpiration() {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        invoker.invokeOnWindowExpiration(mockArgumentProvider);
        Mockito.verify(fn).onWindowExpiration();
    }

    @Test
    public void testOnWindowExpirationWithParam() {
        class MockFn extends DoFn<String, String> {
            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnWindowExpiration
            public void onWindowExpiration(BoundedWindow window) {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        invoker.invokeOnWindowExpiration(mockArgumentProvider);
        Mockito.verify(fn).onWindowExpiration(mockWindow);
    }

    @Test
    public void testDoFnWithReturn() throws Exception {
        class MockFn extends DoFn<String, String> {
            @DoFn.ProcessElement
            public ProcessContinuation processElement(ProcessContext c, DoFnInvokersTest.SomeRestrictionTracker tracker) throws Exception {
                return null;
            }

            @GetInitialRestriction
            public DoFnInvokersTest.SomeRestriction getInitialRestriction(String element) {
                return null;
            }

            @NewTracker
            public DoFnInvokersTest.SomeRestrictionTracker newTracker(DoFnInvokersTest.SomeRestriction restriction) {
                return null;
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        Mockito.when(fn.processElement(mockProcessContext, null)).thenReturn(resume());
        Assert.assertEquals(resume(), invokeProcessElement(fn));
    }

    @Test
    public void testDoFnWithStartBundleSetupTeardown() throws Exception {
        class MockFn extends DoFn<String, String> {
            @ProcessElement
            public void processElement(ProcessContext c) {
            }

            @StartBundle
            public void startBundle(StartBundleContext c) {
            }

            @FinishBundle
            public void finishBundle(FinishBundleContext c) {
            }

            @Setup
            public void before() {
            }

            @Teardown
            public void after() {
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        invoker.invokeSetup();
        invoker.invokeStartBundle(mockStartBundleContext);
        invoker.invokeFinishBundle(mockFinishBundleContext);
        invoker.invokeTeardown();
        Mockito.verify(fn).before();
        Mockito.verify(fn).startBundle(mockStartBundleContext);
        Mockito.verify(fn).finishBundle(mockFinishBundleContext);
        Mockito.verify(fn).after();
    }

    // ---------------------------------------------------------------------------------------
    // Tests for invoking Splittable DoFn methods
    // ---------------------------------------------------------------------------------------
    private static class SomeRestriction {}

    private abstract static class SomeRestrictionTracker extends RestrictionTracker<DoFnInvokersTest.SomeRestriction, Void> {}

    private static class SomeRestrictionCoder extends AtomicCoder<DoFnInvokersTest.SomeRestriction> {
        public static DoFnInvokersTest.SomeRestrictionCoder of() {
            return new DoFnInvokersTest.SomeRestrictionCoder();
        }

        @Override
        public void encode(DoFnInvokersTest.SomeRestriction value, OutputStream outStream) {
        }

        @Override
        public DoFnInvokersTest.SomeRestriction decode(InputStream inStream) {
            return null;
        }
    }

    /**
     * Public so Mockito can do "delegatesTo()" in the test below.
     */
    public static class MockFn extends DoFn<String, String> {
        @ProcessElement
        public ProcessContinuation processElement(ProcessContext c, DoFnInvokersTest.SomeRestrictionTracker tracker) {
            return null;
        }

        @GetInitialRestriction
        public DoFnInvokersTest.SomeRestriction getInitialRestriction(String element) {
            return null;
        }

        @SplitRestriction
        public void splitRestriction(String element, DoFnInvokersTest.SomeRestriction restriction, OutputReceiver<DoFnInvokersTest.SomeRestriction> receiver) {
        }

        @NewTracker
        public DoFnInvokersTest.SomeRestrictionTracker newTracker(DoFnInvokersTest.SomeRestriction restriction) {
            return null;
        }

        @GetRestrictionCoder
        public DoFnInvokersTest.SomeRestrictionCoder getRestrictionCoder() {
            return null;
        }
    }

    @Test
    public void testSplittableDoFnWithAllMethods() throws Exception {
        DoFnInvokersTest.MockFn fn = Mockito.mock(DoFnInvokersTest.MockFn.class);
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        final DoFnInvokersTest.SomeRestrictionTracker tracker = Mockito.mock(DoFnInvokersTest.SomeRestrictionTracker.class);
        final DoFnInvokersTest.SomeRestrictionCoder coder = Mockito.mock(DoFnInvokersTest.SomeRestrictionCoder.class);
        DoFnInvokersTest.SomeRestriction restriction = new DoFnInvokersTest.SomeRestriction();
        final DoFnInvokersTest.SomeRestriction part1 = new DoFnInvokersTest.SomeRestriction();
        final DoFnInvokersTest.SomeRestriction part2 = new DoFnInvokersTest.SomeRestriction();
        final DoFnInvokersTest.SomeRestriction part3 = new DoFnInvokersTest.SomeRestriction();
        Mockito.when(fn.getRestrictionCoder()).thenReturn(coder);
        Mockito.when(fn.getInitialRestriction("blah")).thenReturn(restriction);
        Mockito.doAnswer(AdditionalAnswers.delegatesTo(new DoFnInvokersTest.MockFn() {
            @DoFn.SplitRestriction
            @Override
            public void splitRestriction(String element, DoFnInvokersTest.SomeRestriction restriction, DoFn.OutputReceiver<DoFnInvokersTest.SomeRestriction> receiver) {
                receiver.output(part1);
                receiver.output(part2);
                receiver.output(part3);
            }
        })).when(fn).splitRestriction(ArgumentMatchers.eq("blah"), ArgumentMatchers.same(restriction), Mockito.any());
        Mockito.when(fn.newTracker(restriction)).thenReturn(tracker);
        Mockito.when(fn.processElement(mockProcessContext, tracker)).thenReturn(resume());
        Assert.assertEquals(coder, invoker.invokeGetRestrictionCoder(CoderRegistry.createDefault()));
        Assert.assertEquals(restriction, invoker.invokeGetInitialRestriction("blah"));
        final List<DoFnInvokersTest.SomeRestriction> outputs = new ArrayList<>();
        invoker.invokeSplitRestriction("blah", restriction, new OutputReceiver<DoFnInvokersTest.SomeRestriction>() {
            @Override
            public void output(DoFnInvokersTest.SomeRestriction output) {
                outputs.add(output);
            }

            @Override
            public void outputWithTimestamp(DoFnInvokersTest.SomeRestriction output, Instant timestamp) {
                outputs.add(output);
            }
        });
        Assert.assertEquals(Arrays.asList(part1, part2, part3), outputs);
        Assert.assertEquals(tracker, invoker.invokeNewTracker(restriction));
        Assert.assertEquals(resume(), invoker.invokeProcessElement(new org.apache.beam.sdk.transforms.reflect.DoFnInvoker.FakeArgumentProvider<String, String>() {
            @Override
            public ProcessContext processContext(DoFn<String, String> fn) {
                return mockProcessContext;
            }

            @Override
            public RestrictionTracker<?, ?> restrictionTracker() {
                return tracker;
            }
        }));
    }

    private static class RestrictionWithDefaultTracker implements HasDefaultTracker<DoFnInvokersTest.RestrictionWithDefaultTracker, DoFnInvokersTest.DefaultTracker> {
        @Override
        public DoFnInvokersTest.DefaultTracker newTracker() {
            return new DoFnInvokersTest.DefaultTracker();
        }
    }

    private static class DefaultTracker extends RestrictionTracker<DoFnInvokersTest.RestrictionWithDefaultTracker, Void> {
        @Override
        protected boolean tryClaimImpl(Void position) {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoFnInvokersTest.RestrictionWithDefaultTracker currentRestriction() {
            throw new UnsupportedOperationException();
        }

        @Override
        public DoFnInvokersTest.RestrictionWithDefaultTracker checkpoint() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkDone() throws IllegalStateException {
        }
    }

    private static class CoderForDefaultTracker extends AtomicCoder<DoFnInvokersTest.RestrictionWithDefaultTracker> {
        public static DoFnInvokersTest.CoderForDefaultTracker of() {
            return new DoFnInvokersTest.CoderForDefaultTracker();
        }

        @Override
        public void encode(DoFnInvokersTest.RestrictionWithDefaultTracker value, OutputStream outStream) {
        }

        @Override
        public DoFnInvokersTest.RestrictionWithDefaultTracker decode(InputStream inStream) {
            return null;
        }
    }

    @Test
    public void testSplittableDoFnDefaultMethods() throws Exception {
        class MockFn extends DoFn<String, String> {
            @ProcessElement
            public void processElement(ProcessContext c, DoFnInvokersTest.DefaultTracker tracker) {
            }

            @GetInitialRestriction
            public DoFnInvokersTest.RestrictionWithDefaultTracker getInitialRestriction(String element) {
                return null;
            }
        }
        MockFn fn = Mockito.mock(MockFn.class);
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        CoderRegistry coderRegistry = CoderRegistry.createDefault();
        coderRegistry.registerCoderProvider(CoderProviders.fromStaticMethods(DoFnInvokersTest.RestrictionWithDefaultTracker.class, DoFnInvokersTest.CoderForDefaultTracker.class));
        Assert.assertThat(invoker.<DoFnInvokersTest.RestrictionWithDefaultTracker>invokeGetRestrictionCoder(coderRegistry), CoreMatchers.instanceOf(DoFnInvokersTest.CoderForDefaultTracker.class));
        invoker.invokeSplitRestriction("blah", "foo", new DoFn.OutputReceiver<String>() {
            private boolean invoked;

            @Override
            public void output(String output) {
                assertFalse(invoked);
                invoked = true;
                assertEquals("foo", output);
            }

            @Override
            public void outputWithTimestamp(String output, Instant instant) {
                assertFalse(invoked);
                invoked = true;
                assertEquals("foo", output);
            }
        });
        Assert.assertEquals(stop(), invoker.invokeProcessElement(mockArgumentProvider));
        Assert.assertThat(invoker.invokeNewTracker(new DoFnInvokersTest.RestrictionWithDefaultTracker()), CoreMatchers.instanceOf(DoFnInvokersTest.DefaultTracker.class));
    }

    // ---------------------------------------------------------------------------------------
    // Tests for ability to invoke @OnTimer for private, inner and anonymous classes.
    // ---------------------------------------------------------------------------------------
    private static final String TIMER_ID = "test-timer-id";

    private static class PrivateDoFnWithTimers extends DoFn<String, String> {
        @ProcessElement
        public void processThis(ProcessContext c) {
        }

        @TimerId(DoFnInvokersTest.TIMER_ID)
        private final TimerSpec myTimer = TimerSpecs.timer(PROCESSING_TIME);

        @OnTimer(DoFnInvokersTest.TIMER_ID)
        public void onTimer(BoundedWindow w) {
        }
    }

    @Test
    public void testLocalPrivateDoFnWithTimers() throws Exception {
        DoFnInvokersTest.PrivateDoFnWithTimers fn = Mockito.mock(DoFnInvokersTest.PrivateDoFnWithTimers.class);
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        Mockito.verify(fn).onTimer(mockWindow);
    }

    @Test
    public void testStaticPackagePrivateDoFnWithTimers() throws Exception {
        DoFn<String, String> fn = Mockito.mock(DoFnInvokersTestHelper.newStaticPackagePrivateDoFnWithTimers().getClass());
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        DoFnInvokersTestHelper.verifyStaticPackagePrivateDoFnWithTimers(fn, mockWindow);
    }

    @Test
    public void testInnerPackagePrivateDoFnWithTimers() throws Exception {
        DoFn<String, String> fn = Mockito.mock(new DoFnInvokersTestHelper().newInnerPackagePrivateDoFnWithTimers().getClass());
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        DoFnInvokersTestHelper.verifyInnerPackagePrivateDoFnWithTimers(fn, mockWindow);
    }

    @Test
    public void testStaticPrivateDoFnWithTimers() throws Exception {
        DoFn<String, String> fn = Mockito.mock(DoFnInvokersTestHelper.newStaticPrivateDoFnWithTimers().getClass());
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        DoFnInvokersTestHelper.verifyStaticPrivateDoFnWithTimers(fn, mockWindow);
    }

    @Test
    public void testInnerPrivateDoFnWithTimers() throws Exception {
        DoFn<String, String> fn = Mockito.mock(new DoFnInvokersTestHelper().newInnerPrivateDoFnWithTimers().getClass());
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        DoFnInvokersTestHelper.verifyInnerPrivateDoFnWithTimers(fn, mockWindow);
    }

    @Test
    public void testAnonymousInnerDoFnWithTimers() throws Exception {
        DoFn<String, String> fn = Mockito.mock(new DoFnInvokersTestHelper().newInnerAnonymousDoFnWithTimers().getClass());
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        DoFnInvokersTestHelper.verifyInnerAnonymousDoFnWithTimers(fn, mockWindow);
    }

    @Test
    public void testStaticAnonymousDoFnWithTimersInOtherPackage() throws Exception {
        // Can't use mockito for this one - the anonymous class is final and can't be mocked.
        DoFn<String, String> fn = DoFnInvokersTestHelper.newStaticAnonymousDoFnWithTimers();
        invokeOnTimer(DoFnInvokersTest.TIMER_ID, fn);
        DoFnInvokersTestHelper.verifyStaticAnonymousDoFnWithTimersInvoked(fn, mockWindow);
    }

    // ---------------------------------------------------------------------------------------
    // Tests for ability to invoke @ProcessElement for private, inner and anonymous classes.
    // ---------------------------------------------------------------------------------------
    private static class PrivateDoFnClass extends DoFn<String, String> {
        @ProcessElement
        public void processThis(ProcessContext c) {
        }
    }

    @Test
    public void testLocalPrivateDoFnClass() throws Exception {
        DoFnInvokersTest.PrivateDoFnClass fn = Mockito.mock(DoFnInvokersTest.PrivateDoFnClass.class);
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        Mockito.verify(fn).processThis(mockProcessContext);
    }

    @Test
    public void testStaticPackagePrivateDoFnClass() throws Exception {
        DoFn<String, String> fn = Mockito.mock(DoFnInvokersTestHelper.newStaticPackagePrivateDoFn().getClass());
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        DoFnInvokersTestHelper.verifyStaticPackagePrivateDoFn(fn, mockProcessContext);
    }

    @Test
    public void testInnerPackagePrivateDoFnClass() throws Exception {
        DoFn<String, String> fn = Mockito.mock(new DoFnInvokersTestHelper().newInnerPackagePrivateDoFn().getClass());
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        DoFnInvokersTestHelper.verifyInnerPackagePrivateDoFn(fn, mockProcessContext);
    }

    @Test
    public void testStaticPrivateDoFnClass() throws Exception {
        DoFn<String, String> fn = Mockito.mock(DoFnInvokersTestHelper.newStaticPrivateDoFn().getClass());
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        DoFnInvokersTestHelper.verifyStaticPrivateDoFn(fn, mockProcessContext);
    }

    @Test
    public void testInnerPrivateDoFnClass() throws Exception {
        DoFn<String, String> fn = Mockito.mock(new DoFnInvokersTestHelper().newInnerPrivateDoFn().getClass());
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        DoFnInvokersTestHelper.verifyInnerPrivateDoFn(fn, mockProcessContext);
    }

    @Test
    public void testAnonymousInnerDoFn() throws Exception {
        DoFn<String, String> fn = Mockito.mock(new DoFnInvokersTestHelper().newInnerAnonymousDoFn().getClass());
        Assert.assertEquals(stop(), invokeProcessElement(fn));
        DoFnInvokersTestHelper.verifyInnerAnonymousDoFn(fn, mockProcessContext);
    }

    @Test
    public void testStaticAnonymousDoFnInOtherPackage() throws Exception {
        // Can't use mockito for this one - the anonymous class is final and can't be mocked.
        DoFn<String, String> fn = DoFnInvokersTestHelper.newStaticAnonymousDoFn();
        invokeProcessElement(fn);
        DoFnInvokersTestHelper.verifyStaticAnonymousDoFnInvoked(fn, mockProcessContext);
    }

    // ---------------------------------------------------------------------------------------
    // Tests for wrapping exceptions.
    // ---------------------------------------------------------------------------------------
    @Test
    public void testProcessElementException() throws Exception {
        DoFnInvoker<Integer, Integer> invoker = DoFnInvokers.invokerFor(new DoFn<Integer, Integer>() {
            @ProcessElement
            public void processElement(@SuppressWarnings("unused")
            ProcessContext c) {
                throw new IllegalArgumentException("bogus");
            }
        });
        thrown.expect(UserCodeException.class);
        thrown.expectMessage("bogus");
        invoker.invokeProcessElement(new org.apache.beam.sdk.transforms.reflect.DoFnInvoker.FakeArgumentProvider<Integer, Integer>() {
            @Override
            public ProcessContext processContext(DoFn<Integer, Integer> fn) {
                return null;
            }
        });
    }

    @Test
    public void testProcessElementExceptionWithReturn() throws Exception {
        thrown.expect(UserCodeException.class);
        thrown.expectMessage("bogus");
        DoFnInvokers.invokerFor(new DoFn<Integer, Integer>() {
            @ProcessElement
            public ProcessContinuation processElement(@SuppressWarnings("unused")
            ProcessContext c, DoFnInvokersTest.SomeRestrictionTracker tracker) {
                throw new IllegalArgumentException("bogus");
            }

            @GetInitialRestriction
            public DoFnInvokersTest.SomeRestriction getInitialRestriction(Integer element) {
                return null;
            }

            @NewTracker
            public DoFnInvokersTest.SomeRestrictionTracker newTracker(DoFnInvokersTest.SomeRestriction restriction) {
                return null;
            }
        }).invokeProcessElement(new org.apache.beam.sdk.transforms.reflect.DoFnInvoker.FakeArgumentProvider<Integer, Integer>() {
            @Override
            public ProcessContext processContext(DoFn<Integer, Integer> doFn) {
                return null;// will not be touched

            }

            @Override
            public RestrictionTracker<?, ?> restrictionTracker() {
                return null;// will not be touched

            }
        });
    }

    @Test
    public void testStartBundleException() throws Exception {
        DoFnInvoker<Integer, Integer> invoker = DoFnInvokers.invokerFor(new DoFn<Integer, Integer>() {
            @StartBundle
            public void startBundle(@SuppressWarnings("unused")
            StartBundleContext c) {
                throw new IllegalArgumentException("bogus");
            }

            @ProcessElement
            public void processElement(@SuppressWarnings("unused")
            ProcessContext c) {
            }
        });
        thrown.expect(UserCodeException.class);
        thrown.expectMessage("bogus");
        invoker.invokeStartBundle(null);
    }

    @Test
    public void testFinishBundleException() throws Exception {
        DoFnInvoker<Integer, Integer> invoker = DoFnInvokers.invokerFor(new DoFn<Integer, Integer>() {
            @FinishBundle
            public void finishBundle(@SuppressWarnings("unused")
            FinishBundleContext c) {
                throw new IllegalArgumentException("bogus");
            }

            @ProcessElement
            public void processElement(@SuppressWarnings("unused")
            ProcessContext c) {
            }
        });
        thrown.expect(UserCodeException.class);
        thrown.expectMessage("bogus");
        invoker.invokeFinishBundle(null);
    }

    @Test
    public void testOnTimerHelloWord() throws Exception {
        final String timerId = "my-timer-id";
        class SimpleTimerDoFn extends DoFn<String, String> {
            public String status = "not yet";

            @TimerId(timerId)
            private final TimerSpec myTimer = TimerSpecs.timer(PROCESSING_TIME);

            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnTimer(timerId)
            public void onMyTimer() {
                status = "OK now";
            }
        }
        SimpleTimerDoFn fn = new SimpleTimerDoFn();
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        invoker.invokeOnTimer(timerId, mockArgumentProvider);
        Assert.assertThat(fn.status, Matchers.equalTo("OK now"));
    }

    @Test
    public void testOnTimerWithWindow() throws Exception {
        final String timerId = "my-timer-id";
        final IntervalWindow testWindow = new IntervalWindow(new Instant(0), new Instant(15));
        Mockito.when(mockArgumentProvider.window()).thenReturn(testWindow);
        class SimpleTimerDoFn extends DoFn<String, String> {
            public IntervalWindow window = null;

            @TimerId(timerId)
            private final TimerSpec myTimer = TimerSpecs.timer(PROCESSING_TIME);

            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnTimer(timerId)
            public void onMyTimer(IntervalWindow w) {
                window = w;
            }
        }
        SimpleTimerDoFn fn = new SimpleTimerDoFn();
        DoFnInvoker<String, String> invoker = DoFnInvokers.invokerFor(fn);
        invoker.invokeOnTimer(timerId, mockArgumentProvider);
        Assert.assertThat(fn.window, Matchers.equalTo(testWindow));
    }

    static class StableNameTestDoFn extends DoFn<Void, Void> {
        @ProcessElement
        public void process() {
        }
    }

    /**
     * This is a change-detector test that the generated name is stable across runs.
     */
    @Test
    public void testStableName() {
        DoFnInvoker<Void, Void> invoker = DoFnInvokers.invokerFor(new DoFnInvokersTest.StableNameTestDoFn());
        Assert.assertThat(invoker.getClass().getName(), Matchers.equalTo(String.format("%s$%s", DoFnInvokersTest.StableNameTestDoFn.class.getName(), DoFnInvoker.class.getSimpleName())));
    }
}

