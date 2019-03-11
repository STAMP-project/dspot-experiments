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


import DoFn.StartBundleContext;
import DoFnSignature.OnTimerMethod;
import DoFnSignature.StateDeclaration;
import DoFnSignature.TimerDeclaration;
import TimeDomain.EVENT_TIME;
import TimeDomain.PROCESSING_TIME;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VarLongCoder;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.schemas.FieldAccessDescriptor;
import org.apache.beam.sdk.state.CombiningState;
import org.apache.beam.sdk.state.GroupingState;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.TimeDomain;
import org.apache.beam.sdk.state.Timer;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.state.WatermarkHoldState;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.ElementParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.OutputReceiverParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.PaneInfoParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.PipelineOptionsParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.ProcessContextParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.SchemaElementParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.StateParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.TaggedOutputReceiverParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.TimeDomainParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.TimerParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.TimestampParameter;
import org.apache.beam.sdk.transforms.reflect.DoFnSignature.Parameter.WindowParameter;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.Row;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.apache.beam.sdk.transforms.DoFn.<init>;


/**
 * Tests for {@link DoFnSignatures}.
 */
@RunWith(JUnit4.class)
public class DoFnSignaturesTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testBasicDoFnProcessContext() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.processElement().extraParameters().get(0), Matchers.instanceOf(ProcessContextParameter.class));
    }

    @Test
    public void testBasicDoFnAllParameters() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(@Element
            String element, @Timestamp
            Instant timestamp, BoundedWindow window, PaneInfo paneInfo, OutputReceiver<String> receiver, PipelineOptions options) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(6));
        Assert.assertThat(sig.processElement().extraParameters().get(0), Matchers.instanceOf(ElementParameter.class));
        Assert.assertThat(sig.processElement().extraParameters().get(1), Matchers.instanceOf(TimestampParameter.class));
        Assert.assertThat(sig.processElement().extraParameters().get(2), Matchers.instanceOf(WindowParameter.class));
        Assert.assertThat(sig.processElement().extraParameters().get(3), Matchers.instanceOf(PaneInfoParameter.class));
        Assert.assertThat(sig.processElement().extraParameters().get(4), Matchers.instanceOf(OutputReceiverParameter.class));
        Assert.assertThat(sig.processElement().extraParameters().get(5), Matchers.instanceOf(PipelineOptionsParameter.class));
    }

    @Test
    public void testBasicDoFnMultiOutputReceiver() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(MultiOutputReceiver receiver) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.processElement().extraParameters().get(0), Matchers.instanceOf(TaggedOutputReceiverParameter.class));
    }

    @Test
    public void testMismatchingElementType() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(@Element
            Integer element) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.processElement().extraParameters().get(0), Matchers.instanceOf(SchemaElementParameter.class));
    }

    @Test
    public void testWrongTimestampType() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("@Timestamp argument must have type org.joda.time.Instant");
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(@Timestamp
            String timestamp) {
            }
        }.getClass());
    }

    @Test
    public void testWrongOutputReceiverType() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("OutputReceiver should be parameterized by java.lang.String");
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(OutputReceiver<Integer> receiver) {
            }
        }.getClass());
    }

    @Test
    public void testRowParameterWithoutFieldAccess() {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(@Element
            Row row) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().getSchemaElementParameter(), Matchers.notNullValue());
    }

    @Test
    public void testFieldAccess() throws IllegalAccessException {
        FieldAccessDescriptor descriptor = FieldAccessDescriptor.withFieldNames("foo", "bar");
        org.apache.beam.sdk.transforms.DoFn<String, String> doFn = new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @FieldAccess("foo")
            final FieldAccessDescriptor fieldAccess = descriptor;

            @ProcessElement
            public void process(@FieldAccess("foo")
            @Element
            Row row) {
            }
        };
        DoFnSignature sig = DoFnSignatures.getSignature(doFn.getClass());
        Assert.assertThat(sig.fieldAccessDeclarations().get("foo"), Matchers.notNullValue());
        Field field = sig.fieldAccessDeclarations().get("foo").field();
        Assert.assertThat(field.getName(), Matchers.equalTo("fieldAccess"));
        Assert.assertThat(field.get(doFn), Matchers.equalTo(descriptor));
        Assert.assertThat(sig.processElement().getSchemaElementParameter(), Matchers.notNullValue());
    }

    @Test
    public void testRowReceiver() {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(OutputReceiver<Row> rowReceiver) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().getMainOutputReceiver().isRowReceiver(), Matchers.is(true));
    }

    @Test
    public void testRequiresStableInputProcessElement() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            @RequiresStableInput
            public void process(ProcessContext c) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().requiresStableInput(), Matchers.is(true));
    }

    @Test
    public void testBadExtraContext() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Must take a single argument of type DoFn<Integer, String>.StartBundleContext");
        DoFnSignatures.analyzeStartBundleMethod(DoFnSignaturesTestUtils.errors(), TypeDescriptor.of(DoFnSignaturesTestUtils.FakeDoFn.class), new DoFnSignaturesTestUtils.AnonymousMethod() {
            void method(StartBundleContext c, int n) {
            }
        }.getMethod(), TypeDescriptor.of(Integer.class), TypeDescriptor.of(String.class));
    }

    @Test
    public void testMultipleStartBundleElement() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Found multiple methods annotated with @StartBundle");
        thrown.expectMessage("bar()");
        thrown.expectMessage("baz()");
        thrown.expectMessage(((getClass().getName()) + "$"));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void foo() {
            }

            @StartBundle
            public void bar() {
            }

            @StartBundle
            public void baz() {
            }
        }.getClass());
    }

    @Test
    public void testMultipleFinishBundleMethods() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Found multiple methods annotated with @FinishBundle");
        thrown.expectMessage("bar(FinishBundleContext)");
        thrown.expectMessage("baz(FinishBundleContext)");
        thrown.expectMessage(((getClass().getName()) + "$"));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void foo(ProcessContext context) {
            }

            @FinishBundle
            public void bar(FinishBundleContext context) {
            }

            @FinishBundle
            public void baz(FinishBundleContext context) {
            }
        }.getClass());
    }

    @Test
    public void testPrivateStartBundle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("startBundle()");
        thrown.expectMessage("Must be public");
        thrown.expectMessage(((getClass().getName()) + "$"));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void processElement() {
            }

            @StartBundle
            void startBundle() {
            }
        }.getClass());
    }

    @Test
    public void testPrivateFinishBundle() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("finishBundle()");
        thrown.expectMessage("Must be public");
        thrown.expectMessage(((getClass().getName()) + "$"));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void processElement() {
            }

            @FinishBundle
            void finishBundle() {
            }
        }.getClass());
    }

    @Test
    public void testTimerIdWithWrongType() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("TimerId");
        thrown.expectMessage("TimerSpec");
        thrown.expectMessage("bizzle");
        thrown.expectMessage(Matchers.not(mentionsState()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @TimerId("foo")
            private final String bizzle = "bazzle";

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testTimerIdNoCallback() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("No callback registered");
        thrown.expectMessage("my-id");
        thrown.expectMessage(Matchers.not(mentionsState()));
        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("my-id")
            private final TimerSpec myfield1 = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testOnTimerNoDeclaration() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Callback");
        thrown.expectMessage("undeclared timer");
        thrown.expectMessage("onFoo");
        thrown.expectMessage("my-id");
        thrown.expectMessage(Matchers.not(mentionsState()));
        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @OnTimer("my-id")
            public void onFoo() {
            }

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testOnTimerDeclaredInSuperclass() throws Exception {
        class DoFnDeclaringTimerAndProcessElement extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
            public static final String TIMER_ID = "my-timer-id";

            @TimerId(DoFnDeclaringTimerAndProcessElement.TIMER_ID)
            private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }
        DoFnDeclaringTimerAndProcessElement fn = new DoFnDeclaringTimerAndProcessElement() {
            @OnTimer(DoFnDeclaringTimerAndProcessElement.TIMER_ID)
            public void onTimerFoo() {
            }
        };
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Callback");
        thrown.expectMessage("declared in a different class");
        thrown.expectMessage(DoFnDeclaringTimerAndProcessElement.TIMER_ID);
        thrown.expectMessage(fn.getClass().getSimpleName());
        thrown.expectMessage(Matchers.not(mentionsState()));
        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(fn.getClass());
    }

    @Test
    public void testUsageOfTimerDeclaredInSuperclass() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("process");
        thrown.expectMessage("declared in a different class");
        thrown.expectMessage(DoFnSignaturesTest.DoFnDeclaringTimerAndCallback.TIMER_ID);
        thrown.expectMessage(Matchers.not(mentionsState()));
        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(new DoFnSignaturesTest.DoFnDeclaringTimerAndCallback() {
            @ProcessElement
            public void process(ProcessContext context, @TimerId(DoFnSignaturesTest.DoFnDeclaringTimerAndCallback.TIMER_ID)
            Timer timer) {
            }
        }.getClass());
    }

    @Test
    public void testTimerParameterDuplicate() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("duplicate");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myProcessElement");
        thrown.expectMessage("index 2");
        thrown.expectMessage(Matchers.not(mentionsState()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("my-id")
            private final TimerSpec myfield = TimerSpecs.timer(PROCESSING_TIME);

            @ProcessElement
            public void myProcessElement(ProcessContext context, @TimerId("my-id")
            Timer one, @TimerId("my-id")
            Timer two) {
            }

            @OnTimer("my-id")
            public void onWhatever() {
            }
        }.getClass());
    }

    @Test
    public void testOnTimerDeclaredInSubclass() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Callback");
        thrown.expectMessage("declared in a different class");
        thrown.expectMessage(DoFnSignaturesTest.DoFnWithOnlyCallback.TIMER_ID);
        thrown.expectMessage(Matchers.not(mentionsState()));
        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(new DoFnSignaturesTest.DoFnWithOnlyCallback() {
            @TimerId(DoFnSignaturesTest.DoFnWithOnlyCallback.TIMER_ID)
            private final TimerSpec myfield1 = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            @Override
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testWindowParamOnTimer() throws Exception {
        final String timerId = "some-timer-id";
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @TimerId(timerId)
            private final TimerSpec myfield1 = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnTimer(timerId)
            public void onTimer(BoundedWindow w) {
            }
        }.getClass());
        Assert.assertThat(sig.onTimerMethods().get(timerId).extraParameters().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.onTimerMethods().get(timerId).extraParameters().get(0), Matchers.instanceOf(WindowParameter.class));
    }

    @Test
    public void testAllParamsOnTimer() throws Exception {
        final String timerId = "some-timer-id";
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @TimerId(timerId)
            private final TimerSpec myfield1 = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnTimer(timerId)
            public void onTimer(@Timestamp
            Instant timestamp, TimeDomain timeDomain, BoundedWindow w) {
            }
        }.getClass());
        Assert.assertThat(sig.onTimerMethods().get(timerId).extraParameters().size(), Matchers.equalTo(3));
        Assert.assertThat(sig.onTimerMethods().get(timerId).extraParameters().get(0), Matchers.instanceOf(TimestampParameter.class));
        Assert.assertThat(sig.onTimerMethods().get(timerId).extraParameters().get(1), Matchers.instanceOf(TimeDomainParameter.class));
        Assert.assertThat(sig.onTimerMethods().get(timerId).extraParameters().get(2), Matchers.instanceOf(WindowParameter.class));
    }

    @Test
    public void testPipelineOptionsParameter() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c, PipelineOptions options) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().extraParameters(), Matchers.hasItem(Matchers.instanceOf(PipelineOptionsParameter.class)));
    }

    @Test
    public void testDeclAndUsageOfTimerInSuperclass() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new DoFnSignaturesTest.DoFnOverridingAbstractTimerUse().getClass());
        Assert.assertThat(sig.timerDeclarations().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(2));
        DoFnSignature.TimerDeclaration decl = sig.timerDeclarations().get(DoFnSignaturesTest.DoFnOverridingAbstractTimerUse.TIMER_ID);
        TimerParameter timerParam = ((TimerParameter) (sig.processElement().extraParameters().get(1)));
        Assert.assertThat(decl.field(), Matchers.equalTo(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractUse.class.getDeclaredField("myTimerSpec")));
        // The method we pull out is the superclass method; this is what allows validation to remain
        // simple. The later invokeDynamic instruction causes it to invoke the actual implementation.
        Assert.assertThat(timerParam.referent(), Matchers.equalTo(decl));
    }

    /**
     * In this particular test, the super class annotated both the timer and the callback, and the
     * subclass overrides an abstract method. This is allowed.
     */
    @Test
    public void testOnTimerDeclaredAndUsedInSuperclass() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new DoFnSignaturesTest.DoFnOverridingAbstractCallback().getClass());
        Assert.assertThat(sig.timerDeclarations().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.onTimerMethods().size(), Matchers.equalTo(1));
        DoFnSignature.TimerDeclaration decl = sig.timerDeclarations().get(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback.TIMER_ID);
        DoFnSignature.OnTimerMethod callback = sig.onTimerMethods().get(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback.TIMER_ID);
        Assert.assertThat(decl.field(), Matchers.equalTo(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback.class.getDeclaredField("myTimerSpec")));
        // The method we pull out is the superclass method; this is what allows validation to remain
        // simple. The later invokeDynamic instruction causes it to invoke the actual implementation.
        Assert.assertThat(callback.targetMethod(), Matchers.equalTo(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback.class.getDeclaredMethod("onMyTimer")));
    }

    @Test
    public void testTimerIdDuplicate() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate");
        thrown.expectMessage("TimerId");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myfield1");
        thrown.expectMessage("myfield2");
        thrown.expectMessage(Matchers.not(Matchers.containsString("State")));// lowercase "state" is in the package name

        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("my-id")
            private final TimerSpec myfield1 = TimerSpecs.timer(EVENT_TIME);

            @TimerId("my-id")
            private final TimerSpec myfield2 = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testTimerIdNonFinal() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Timer declarations must be final");
        thrown.expectMessage("Non-final field");
        thrown.expectMessage("myfield");
        thrown.expectMessage(Matchers.not(Matchers.containsString("State")));// lowercase "state" is in the package name

        thrown.expectMessage(mentionsTimers());
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("my-timer-id")
            private TimerSpec myfield = TimerSpecs.timer(PROCESSING_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testSimpleTimerIdAnonymousDoFn() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("foo")
            private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }

            @OnTimer("foo")
            public void onFoo() {
            }
        }.getClass());
        Assert.assertThat(sig.timerDeclarations().size(), Matchers.equalTo(1));
        DoFnSignature.TimerDeclaration decl = sig.timerDeclarations().get("foo");
        Assert.assertThat(decl.id(), Matchers.equalTo("foo"));
        Assert.assertThat(decl.field().getName(), Matchers.equalTo("bizzle"));
    }

    @Test
    public void testSimpleTimerWithContext() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("foo")
            private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }

            @OnTimer("foo")
            public void onFoo(OnTimerContext c) {
            }
        }.getClass());
        Assert.assertThat(sig.timerDeclarations().size(), Matchers.equalTo(1));
        DoFnSignature.TimerDeclaration decl = sig.timerDeclarations().get("foo");
        Assert.assertThat(decl.id(), Matchers.equalTo("foo"));
        Assert.assertThat(decl.field().getName(), Matchers.equalTo("bizzle"));
        Assert.assertThat(sig.onTimerMethods().get("foo").extraParameters().get(0), Matchers.equalTo(((Parameter) (Parameter.onTimerContext()))));
    }

    @Test
    public void testProcessElementWithOnTimerContextRejected() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        // The message should at least mention @ProcessElement and OnTimerContext
        thrown.expectMessage(("@" + (DoFn.ProcessElement.class.getSimpleName())));
        thrown.expectMessage(DoFn.OnTimerContext.class.getSimpleName());
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @TimerId("foo")
            private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context, OnTimerContext bogus) {
            }

            @OnTimer("foo")
            public void onFoo() {
            }
        }.getClass());
    }

    @Test
    public void testSimpleTimerIdNamedDoFn() throws Exception {
        class DoFnForTestSimpleTimerIdNamedDoFn extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
            @TimerId("foo")
            private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo(ProcessContext context) {
            }

            @OnTimer("foo")
            public void onFoo() {
            }
        }
        // Test classes at the bottom of the file
        DoFnSignature sig = DoFnSignatures.signatureForDoFn(new DoFnForTestSimpleTimerIdNamedDoFn());
        Assert.assertThat(sig.timerDeclarations().size(), Matchers.equalTo(1));
        DoFnSignature.TimerDeclaration decl = sig.timerDeclarations().get("foo");
        Assert.assertThat(decl.id(), Matchers.equalTo("foo"));
        Assert.assertThat(decl.field(), Matchers.equalTo(DoFnForTestSimpleTimerIdNamedDoFn.class.getDeclaredField("bizzle")));
    }

    @Test
    public void testStateIdWithWrongType() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("StateId");
        thrown.expectMessage("StateSpec");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @StateId("foo")
            private final String bizzle = "bazzle";

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testStateIdDuplicate() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Duplicate");
        thrown.expectMessage("StateId");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myfield1");
        thrown.expectMessage("myfield2");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("my-id")
            private final StateSpec<ValueState<Integer>> myfield1 = StateSpecs.value(VarIntCoder.of());

            @StateId("my-id")
            private final StateSpec<ValueState<Long>> myfield2 = StateSpecs.value(VarLongCoder.of());

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testStateIdNonFinal() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("State declarations must be final");
        thrown.expectMessage("Non-final field");
        thrown.expectMessage("myfield");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("my-id")
            private StateSpec<ValueState<Integer>> myfield = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
    }

    @Test
    public void testStateParameterNoAnnotation() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("missing StateId annotation");
        thrown.expectMessage("myProcessElement");
        thrown.expectMessage("index 1");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @ProcessElement
            public void myProcessElement(ProcessContext context, ValueState<Integer> noAnnotation) {
            }
        }.getClass());
    }

    @Test
    public void testStateParameterUndeclared() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("undeclared");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myProcessElement");
        thrown.expectMessage("index 1");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @ProcessElement
            public void myProcessElement(ProcessContext context, @StateId("my-id")
            ValueState<Integer> undeclared) {
            }
        }.getClass());
    }

    @Test
    public void testStateParameterDuplicate() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("duplicate");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myProcessElement");
        thrown.expectMessage("index 2");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("my-id")
            private final StateSpec<ValueState<Integer>> myfield = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void myProcessElement(ProcessContext context, @StateId("my-id")
            ValueState<Integer> one, @StateId("my-id")
            ValueState<Integer> two) {
            }
        }.getClass());
    }

    @Test
    public void testStateParameterWrongStateType() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("WatermarkHoldState");
        thrown.expectMessage("reference to");
        thrown.expectMessage("supertype");
        thrown.expectMessage("ValueState");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myProcessElement");
        thrown.expectMessage("index 1");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("my-id")
            private final StateSpec<ValueState<Integer>> myfield = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void myProcessElement(ProcessContext context, @StateId("my-id")
            WatermarkHoldState watermark) {
            }
        }.getClass());
    }

    @Test
    public void testStateParameterWrongGenericType() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("ValueState<String>");
        thrown.expectMessage("reference to");
        thrown.expectMessage("supertype");
        thrown.expectMessage("ValueState<Integer>");
        thrown.expectMessage("my-id");
        thrown.expectMessage("myProcessElement");
        thrown.expectMessage("index 1");
        thrown.expectMessage(Matchers.not(mentionsTimers()));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("my-id")
            private final StateSpec<ValueState<Integer>> myfield = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void myProcessElement(ProcessContext context, @StateId("my-id")
            ValueState<String> stringState) {
            }
        }.getClass());
    }

    @Test
    public void testGoodStateParameterSuperclassStateType() throws Exception {
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("my-id")
            private final StateSpec<CombiningState<Integer, int[], Integer>> state = StateSpecs.combining(Sum.ofIntegers());

            @ProcessElement
            public void myProcessElement(ProcessContext context, @StateId("my-id")
            GroupingState<Integer, Integer> groupingState) {
            }
        }.getClass());
    }

    @Test
    public void testSimpleStateIdAnonymousDoFn() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("foo")
            private final StateSpec<ValueState<Integer>> bizzle = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }.getClass());
        Assert.assertThat(sig.stateDeclarations().size(), Matchers.equalTo(1));
        DoFnSignature.StateDeclaration decl = sig.stateDeclarations().get("foo");
        Assert.assertThat(decl.id(), Matchers.equalTo("foo"));
        Assert.assertThat(decl.field().getName(), Matchers.equalTo("bizzle"));
        Assert.assertThat(decl.stateType(), Matchers.<TypeDescriptor<?>>equalTo(new TypeDescriptor<ValueState<Integer>>() {}));
    }

    @Test
    public void testUsageOfStateDeclaredInSuperclass() throws Exception {
        DoFnSignaturesTest.DoFnDeclaringState fn = new DoFnSignaturesTest.DoFnDeclaringState() {
            @ProcessElement
            public void process(ProcessContext context, @StateId(DoFnSignaturesTest.DoFnDeclaringState.STATE_ID)
            ValueState<Integer> state) {
            }
        };
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("process");
        thrown.expectMessage("declared in a different class");
        thrown.expectMessage(DoFnSignaturesTest.DoFnDeclaringState.STATE_ID);
        thrown.expectMessage(fn.getClass().getSimpleName());
        DoFnSignatures.getSignature(fn.getClass());
    }

    @Test
    public void testDeclOfStateUsedInSuperclass() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("process");
        thrown.expectMessage("declared in a different class");
        thrown.expectMessage(DoFnSignaturesTest.DoFnUsingState.STATE_ID);
        DoFnSignatures.getSignature(new DoFnSignaturesTest.DoFnUsingState() {
            @StateId(DoFnSignaturesTest.DoFnUsingState.STATE_ID)
            private final StateSpec<ValueState<Integer>> spec = StateSpecs.value(VarIntCoder.of());
        }.getClass());
    }

    @Test
    public void testDeclAndUsageOfStateInSuperclass() throws Exception {
        class DoFnOverridingAbstractStateUse extends DoFnSignaturesTest.DoFnDeclaringStateAndAbstractUse {
            @Override
            public void processWithState(ProcessContext c, ValueState<String> state) {
            }
        }
        DoFnSignature sig = DoFnSignatures.getSignature(new DoFnOverridingAbstractStateUse().getClass());
        Assert.assertThat(sig.stateDeclarations().size(), Matchers.equalTo(1));
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(2));
        DoFnSignature.StateDeclaration decl = sig.stateDeclarations().get(DoFnOverridingAbstractStateUse.STATE_ID);
        StateParameter stateParam = ((StateParameter) (sig.processElement().extraParameters().get(1)));
        Assert.assertThat(decl.field(), Matchers.equalTo(DoFnSignaturesTest.DoFnDeclaringStateAndAbstractUse.class.getDeclaredField("myStateSpec")));
        // The method we pull out is the superclass method; this is what allows validation to remain
        // simple. The later invokeDynamic instruction causes it to invoke the actual implementation.
        Assert.assertThat(stateParam.referent(), Matchers.equalTo(decl));
    }

    /**
     * Assuming the proper parsing of declarations, testing elsewhere, this test ensures that a simple
     * reference to such a declaration is correctly resolved.
     */
    @Test
    public void testSimpleStateIdRefAnonymousDoFn() throws Exception {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long>() {
            @StateId("foo")
            private final StateSpec<ValueState<Integer>> bizzleDecl = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void foo(ProcessContext context, @StateId("foo")
            ValueState<Integer> bizzle) {
            }
        }.getClass());
        Assert.assertThat(sig.processElement().extraParameters().size(), Matchers.equalTo(2));
        final DoFnSignature.StateDeclaration decl = sig.stateDeclarations().get("foo");
        sig.processElement().extraParameters().get(1).match(new Parameter.Cases.WithDefault<Void>() {
            @Override
            protected Void dispatchDefault(Parameter p) {
                fail(String.format("Expected a state parameter but got %s", p));
                return null;
            }

            @Override
            public Void dispatch(StateParameter stateParam) {
                assertThat(stateParam.referent(), equalTo(decl));
                return null;
            }
        });
    }

    @Test
    public void testSimpleStateIdNamedDoFn() throws Exception {
        class DoFnForTestSimpleStateIdNamedDoFn extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
            @StateId("foo")
            private final StateSpec<ValueState<Integer>> bizzle = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }
        // Test classes at the bottom of the file
        DoFnSignature sig = DoFnSignatures.signatureForDoFn(new DoFnForTestSimpleStateIdNamedDoFn());
        Assert.assertThat(sig.stateDeclarations().size(), Matchers.equalTo(1));
        DoFnSignature.StateDeclaration decl = sig.stateDeclarations().get("foo");
        Assert.assertThat(decl.id(), Matchers.equalTo("foo"));
        Assert.assertThat(decl.field(), Matchers.equalTo(DoFnForTestSimpleStateIdNamedDoFn.class.getDeclaredField("bizzle")));
        Assert.assertThat(decl.stateType(), Matchers.<TypeDescriptor<?>>equalTo(new TypeDescriptor<ValueState<Integer>>() {}));
    }

    @Test
    public void testGenericStatefulDoFn() throws Exception {
        class DoFnForTestGenericStatefulDoFn<T> extends org.apache.beam.sdk.transforms.DoFn<KV<String, T>, Long> {
            // Note that in order to have a coder for T it will require initialization in the constructor,
            // but that isn't important for this test
            @StateId("foo")
            private final StateSpec<ValueState<T>> bizzle = null;

            @ProcessElement
            public void foo(ProcessContext context) {
            }
        }
        // Test classes at the bottom of the file
        org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> myDoFn = new DoFnForTestGenericStatefulDoFn<Integer>() {};
        DoFnSignature sig = DoFnSignatures.signatureForDoFn(myDoFn);
        Assert.assertThat(sig.stateDeclarations().size(), Matchers.equalTo(1));
        DoFnSignature.StateDeclaration decl = sig.stateDeclarations().get("foo");
        Assert.assertThat(decl.id(), Matchers.equalTo("foo"));
        Assert.assertThat(decl.field(), Matchers.equalTo(DoFnForTestGenericStatefulDoFn.class.getDeclaredField("bizzle")));
        Assert.assertThat(decl.stateType(), Matchers.<TypeDescriptor<?>>equalTo(new TypeDescriptor<ValueState<Integer>>() {}));
    }

    @Test
    public void testOnWindowExpirationMultipleAnnotation() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Found multiple methods annotated with @OnWindowExpiration");
        thrown.expectMessage("bar()");
        thrown.expectMessage("baz()");
        thrown.expectMessage(((getClass().getName()) + "$"));
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void foo() {
            }

            @OnWindowExpiration
            public void bar() {
            }

            @OnWindowExpiration
            public void baz() {
            }
        }.getClass());
    }

    @Test
    public void testOnWindowExpirationMustBePublic() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("OnWindowExpiration");
        thrown.expectMessage("Must be public");
        thrown.expectMessage("bar()");
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void foo() {
            }

            @OnWindowExpiration
            void bar() {
            }
        }.getClass());
    }

    @Test
    public void testOnWindowExpirationDisallowedParameter() throws Exception {
        // Timers are not allowed in OnWindowExpiration
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Illegal parameter type");
        thrown.expectMessage("TimerParameter");
        thrown.expectMessage("myTimer");
        DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @TimerId("foo")
            private final TimerSpec myTimer = TimerSpecs.timer(EVENT_TIME);

            @ProcessElement
            public void foo() {
            }

            @OnTimer("foo")
            public void onFoo() {
            }

            @OnWindowExpiration
            public void bar(@TimerId("foo")
            Timer t) {
            }
        }.getClass());
    }

    @Test
    public void testOnWindowExpirationNoParam() {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnWindowExpiration
            public void bar() {
            }
        }.getClass());
        Assert.assertThat(sig.onWindowExpiration().extraParameters().size(), Matchers.equalTo(0));
    }

    @Test
    public void testOnWindowExpirationWithAllowedParams() {
        DoFnSignature sig = DoFnSignatures.getSignature(new org.apache.beam.sdk.transforms.DoFn<String, String>() {
            @StateId("foo")
            private final StateSpec<ValueState<Integer>> bizzle = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnWindowExpiration
            public void bar(BoundedWindow b, @StateId("foo")
            ValueState<Integer> s, PipelineOptions p, OutputReceiver<String> o, MultiOutputReceiver m) {
            }
        }.getClass());
        List<Parameter> params = sig.onWindowExpiration().extraParameters();
        Assert.assertThat(params.size(), Matchers.equalTo(5));
        Assert.assertThat(params.get(0), Matchers.instanceOf(WindowParameter.class));
        Assert.assertThat(params.get(1), Matchers.instanceOf(StateParameter.class));
        Assert.assertThat(params.get(2), Matchers.instanceOf(PipelineOptionsParameter.class));
        Assert.assertThat(params.get(3), Matchers.instanceOf(OutputReceiverParameter.class));
        Assert.assertThat(params.get(4), Matchers.instanceOf(TaggedOutputReceiverParameter.class));
    }

    private abstract static class DoFnDeclaringState extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String STATE_ID = "my-state-id";

        @StateId(DoFnSignaturesTest.DoFnDeclaringState.STATE_ID)
        private final StateSpec<ValueState<Integer>> bizzle = StateSpecs.value(VarIntCoder.of());
    }

    private abstract static class DoFnUsingState extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String STATE_ID = "my-state-id";

        @ProcessElement
        public void process(ProcessContext context, @StateId(DoFnSignaturesTest.DoFnUsingState.STATE_ID)
        ValueState<Integer> state) {
        }
    }

    private abstract static class DoFnDeclaringStateAndAbstractUse extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String STATE_ID = "my-state-id";

        @StateId(DoFnSignaturesTest.DoFnDeclaringStateAndAbstractUse.STATE_ID)
        private final StateSpec<ValueState<String>> myStateSpec = StateSpecs.value(StringUtf8Coder.of());

        @ProcessElement
        public abstract void processWithState(ProcessContext context, @StateId(DoFnSignaturesTest.DoFnDeclaringStateAndAbstractUse.STATE_ID)
        ValueState<String> state);
    }

    private abstract static class DoFnDeclaringMyTimerId extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        @TimerId("my-timer-id")
        private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

        @ProcessElement
        public void foo(ProcessContext context) {
        }
    }

    private abstract static class DoFnDeclaringTimerAndCallback extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String TIMER_ID = "my-timer-id";

        @TimerId(DoFnSignaturesTest.DoFnDeclaringTimerAndCallback.TIMER_ID)
        private final TimerSpec bizzle = TimerSpecs.timer(EVENT_TIME);

        @OnTimer(DoFnSignaturesTest.DoFnDeclaringTimerAndCallback.TIMER_ID)
        public void onTimer() {
        }
    }

    private abstract static class DoFnWithOnlyCallback extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String TIMER_ID = "my-timer-id";

        @OnTimer(DoFnSignaturesTest.DoFnWithOnlyCallback.TIMER_ID)
        public void onMyTimer() {
        }

        @ProcessElement
        public void foo(ProcessContext context) {
        }
    }

    private abstract static class DoFnDeclaringTimerAndAbstractCallback extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String TIMER_ID = "my-timer-id";

        @TimerId(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback.TIMER_ID)
        private final TimerSpec myTimerSpec = TimerSpecs.timer(EVENT_TIME);

        @ProcessElement
        public void foo(ProcessContext context) {
        }

        @OnTimer(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback.TIMER_ID)
        public abstract void onMyTimer();
    }

    private static class DoFnOverridingAbstractCallback extends DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractCallback {
        @Override
        public void onMyTimer() {
        }

        @ProcessElement
        @Override
        public void foo(ProcessContext context) {
        }
    }

    private abstract static class DoFnDeclaringTimerAndAbstractUse extends org.apache.beam.sdk.transforms.DoFn<KV<String, Integer>, Long> {
        public static final String TIMER_ID = "my-timer-id";

        @TimerId(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractUse.TIMER_ID)
        private final TimerSpec myTimerSpec = TimerSpecs.timer(EVENT_TIME);

        @ProcessElement
        public abstract void processWithTimer(ProcessContext context, @TimerId(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractUse.TIMER_ID)
        Timer timer);

        @OnTimer(DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractUse.TIMER_ID)
        public abstract void onMyTimer();
    }

    private static class DoFnOverridingAbstractTimerUse extends DoFnSignaturesTest.DoFnDeclaringTimerAndAbstractUse {
        @Override
        public void onMyTimer() {
        }

        @Override
        public void processWithTimer(ProcessContext context, Timer timer) {
        }
    }
}

