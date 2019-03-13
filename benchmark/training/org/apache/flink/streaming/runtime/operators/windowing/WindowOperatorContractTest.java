/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.operators.windowing;


import Trigger.TriggerContext;
import TriggerResult.FIRE;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.runtime.checkpoint.OperatorSubtaskState;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.MergingWindowAssigner;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalWindowFunction;
import org.apache.flink.streaming.util.AbstractStreamOperatorTestHarness;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;


/**
 * Base for window operator tests that verify correct interaction with the other windowing
 * components: {@link org.apache.flink.streaming.api.windowing.assigners.WindowAssigner},
 * {@link org.apache.flink.streaming.api.windowing.triggers.Trigger}.
 * {@link org.apache.flink.streaming.api.functions.windowing.WindowFunction} and window state.
 *
 * <p>These tests document the implicit contract that exists between the windowing components.
 */
public abstract class WindowOperatorContractTest extends TestLogger {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static ValueStateDescriptor<String> valueStateDescriptor = new ValueStateDescriptor("string-state", StringSerializer.INSTANCE, null);

    /**
     * Verify that there is no late-data side output if the {@code WindowAssigner} does
     * not assign any windows.
     */
    @Test
    public void testNoLateSideOutputForSkippedWindows() throws Exception {
        OutputTag<Integer> lateOutputTag = new OutputTag<Integer>("late") {};
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        OneInputStreamOperatorTestHarness<Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction, lateOutputTag);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.<TimeWindow>emptyList());
        testHarness.processWatermark(0);
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 5L));
        Mockito.verify(mockAssigner, Mockito.times(1)).assignWindows(ArgumentMatchers.eq(0), ArgumentMatchers.eq(5L), WindowOperatorContractTest.anyAssignerContext());
        Assert.assertTrue((((testHarness.getSideOutput(lateOutputTag)) == null) || (testHarness.getSideOutput(lateOutputTag).isEmpty())));
    }

    @Test
    public void testLateSideOutput() throws Exception {
        OutputTag<Integer> lateOutputTag = new OutputTag<Integer>("late") {};
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        OneInputStreamOperatorTestHarness<Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction, lateOutputTag);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 0)));
        testHarness.processWatermark(20);
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 5L));
        Mockito.verify(mockAssigner, Mockito.times(1)).assignWindows(ArgumentMatchers.eq(0), ArgumentMatchers.eq(5L), WindowOperatorContractTest.anyAssignerContext());
        Assert.assertThat(testHarness.getSideOutput(lateOutputTag), Matchers.contains(StreamRecordMatchers.isStreamRecord(0, 5L)));
        // we should also see side output if the WindowAssigner assigns no windows
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.<TimeWindow>emptyList());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 10L));
        Mockito.verify(mockAssigner, Mockito.times(1)).assignWindows(ArgumentMatchers.eq(0), ArgumentMatchers.eq(5L), WindowOperatorContractTest.anyAssignerContext());
        Mockito.verify(mockAssigner, Mockito.times(1)).assignWindows(ArgumentMatchers.eq(0), ArgumentMatchers.eq(10L), WindowOperatorContractTest.anyAssignerContext());
        Assert.assertThat(testHarness.getSideOutput(lateOutputTag), Matchers.contains(StreamRecordMatchers.isStreamRecord(0, 5L), StreamRecordMatchers.isStreamRecord(0, 10L)));
    }

    /**
     * This also verifies that the timestamps ouf side-emitted records is correct.
     */
    @Test
    public void testSideOutput() throws Exception {
        final OutputTag<Integer> integerOutputTag = new OutputTag<Integer>("int-out") {};
        final OutputTag<Long> longOutputTag = new OutputTag<Long>("long-out") {};
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> windowFunction = new InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow>() {
            @Override
            public void process(Integer integer, TimeWindow window, InternalWindowContext ctx, Iterable<Integer> input, Collector<Void> out) throws Exception {
                Integer inputValue = input.iterator().next();
                ctx.output(integerOutputTag, inputValue);
                ctx.output(longOutputTag, inputValue.longValue());
            }

            @Override
            public void clear(TimeWindow window, InternalWindowContext context) throws Exception {
            }
        };
        OneInputStreamOperatorTestHarness<Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, windowFunction);
        testHarness.open();
        final long windowEnd = 42L;
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, windowEnd)));
        WindowOperatorContractTest.shouldFireOnElement(mockTrigger);
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(17, 5L));
        Assert.assertThat(testHarness.getSideOutput(integerOutputTag), Matchers.contains(StreamRecordMatchers.isStreamRecord(17, (windowEnd - 1))));
        Assert.assertThat(testHarness.getSideOutput(longOutputTag), Matchers.contains(StreamRecordMatchers.isStreamRecord(17L, (windowEnd - 1))));
    }

    @Test
    public void testAssignerIsInvokedOncePerElement() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        OneInputStreamOperatorTestHarness<Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 0)));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockAssigner, Mockito.times(1)).assignWindows(ArgumentMatchers.eq(0), ArgumentMatchers.eq(0L), WindowOperatorContractTest.anyAssignerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockAssigner, Mockito.times(2)).assignWindows(ArgumentMatchers.eq(0), ArgumentMatchers.eq(0L), WindowOperatorContractTest.anyAssignerContext());
    }

    @Test
    public void testAssignerWithMultipleWindows() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        OneInputStreamOperatorTestHarness<Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        WindowOperatorContractTest.shouldFireOnElement(mockTrigger);
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.eq(0), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
    }

    @Test
    public void testWindowsDontInterfere() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 2)));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 1)));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        // no output so far
        Assert.assertTrue(testHarness.extractOutputStreamRecords().isEmpty());
        // state for two windows
        Assert.assertEquals(2, testHarness.numKeyedStateEntries());
        Assert.assertEquals(2, testHarness.numEventTimeTimers());
        // now we fire
        WindowOperatorContractTest.shouldFireOnElement(mockTrigger);
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 1)));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 2)));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.anyInt(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0, 0), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(1), ArgumentMatchers.eq(new TimeWindow(0, 1)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(1, 1), WindowOperatorContractTest.<Void>anyCollector());
    }

    @Test
    public void testOnElementCalledPerWindow() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        OneInputStreamOperatorTestHarness<Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(42, 1L));
        Mockito.verify(mockTrigger).onElement(ArgumentMatchers.eq(42), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyTriggerContext());
        Mockito.verify(mockTrigger).onElement(ArgumentMatchers.eq(42), ArgumentMatchers.eq(1L), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyTriggerContext());
        Mockito.verify(mockTrigger, Mockito.times(2)).onElement(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
    }

    @Test
    public void testEmittingFromWindowFunction() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, String, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, String> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Collections.singletonList(new TimeWindow(0, 2)));
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                return TriggerResult.FIRE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Exception {
                @SuppressWarnings("unchecked")
                Collector<String> out = invocation.getArgument(4);
                out.collect("Hallo");
                out.collect("Ciao");
                return null;
            }
        }).when(mockWindowFunction).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<String>anyCollector());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<String>anyCollector());
        Assert.assertThat(testHarness.extractOutputStreamRecords(), Matchers.contains(StreamRecordMatchers.isStreamRecord("Hallo", 1L), StreamRecordMatchers.isStreamRecord("Ciao", 1L)));
    }

    @Test
    public void testEmittingFromWindowFunctionOnEventTime() throws Exception {
        testEmittingFromWindowFunction(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testEmittingFromWindowFunctionOnProcessingTime() throws Exception {
        testEmittingFromWindowFunction(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testOnElementContinue() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                TimeWindow window = ((TimeWindow) (invocation.getArguments()[2]));
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(window.getEnd());
                context.getPartitionedState(WindowOperatorContractTest.valueStateDescriptor).update("hello");
                return TriggerResult.CONTINUE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // CONTINUE should not purge contents
        Assert.assertEquals(4, testHarness.numKeyedStateEntries());// window contents plus trigger state

        Assert.assertEquals(4, testHarness.numEventTimeTimers());// window timers/gc timers

        // there should be no firing
        Assert.assertEquals(0, testHarness.getOutput().size());
    }

    @Test
    public void testOnElementFire() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                TimeWindow window = ((TimeWindow) (invocation.getArguments()[2]));
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(window.getEnd());
                context.getPartitionedState(WindowOperatorContractTest.valueStateDescriptor).update("hello");
                return TriggerResult.FIRE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.eq(0), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // FIRE should not purge contents
        Assert.assertEquals(4, testHarness.numKeyedStateEntries());// window contents plus trigger state

        Assert.assertEquals(4, testHarness.numEventTimeTimers());// window timers/gc timers

    }

    @Test
    public void testOnElementFireAndPurge() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                TimeWindow window = ((TimeWindow) (invocation.getArguments()[2]));
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(window.getEnd());
                context.getPartitionedState(WindowOperatorContractTest.valueStateDescriptor).update("hello");
                return TriggerResult.FIRE_AND_PURGE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.eq(0), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // FIRE_AND_PURGE should purge contents
        Assert.assertEquals(2, testHarness.numKeyedStateEntries());// trigger state will stick around until GC time

        // timers will stick around
        Assert.assertEquals(4, testHarness.numEventTimeTimers());
    }

    @Test
    public void testOnElementPurge() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(0L);
                context.getPartitionedState(WindowOperatorContractTest.valueStateDescriptor).update("hello");
                return TriggerResult.PURGE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // PURGE should purge contents
        Assert.assertEquals(2, testHarness.numKeyedStateEntries());// trigger state will stick around until GC time

        // timers will stick around
        Assert.assertEquals(4, testHarness.numEventTimeTimers());// trigger timer and GC timer

        // no output
        Assert.assertEquals(0, testHarness.getOutput().size());
    }

    @Test
    public void testOnEventTimeContinue() throws Exception {
        testOnTimeContinue(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testOnProcessingTimeContinue() throws Exception {
        testOnTimeContinue(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testOnEventTimeFire() throws Exception {
        testOnTimeFire(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testOnProcessingTimeFire() throws Exception {
        testOnTimeFire(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testOnEventTimeFireAndPurge() throws Exception {
        testOnTimeFireAndPurge(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testOnProcessingTimeFireAndPurge() throws Exception {
        testOnTimeFireAndPurge(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testOnEventTimePurge() throws Exception {
        testOnTimePurge(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testOnProcessingTimePurge() throws Exception {
        testOnTimePurge(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testNoEventTimeFiringForPurgedWindow() throws Exception {
        testNoTimerFiringForPurgedWindow(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testNoProcessingTimeFiringForPurgedWindow() throws Exception {
        testNoTimerFiringForPurgedWindow(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testNoEventTimeFiringForPurgedMergingWindow() throws Exception {
        testNoTimerFiringForPurgedMergingWindow(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testNoProcessingTimeFiringForPurgedMergingWindow() throws Exception {
        testNoTimerFiringForPurgedMergingWindow(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testNoEventTimeFiringForGarbageCollectedMergingWindow() throws Exception {
        testNoTimerFiringForGarbageCollectedMergingWindow(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testNoProcessingTimeFiringForGarbageCollectedMergingWindow() throws Exception {
        testNoTimerFiringForGarbageCollectedMergingWindow(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testEventTimeTimerCreationAndDeletion() throws Exception {
        testTimerCreationAndDeletion(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeTimerCreationAndDeletion() throws Exception {
        testTimerCreationAndDeletion(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testEventTimeTimerFiring() throws Exception {
        testTimerFiring(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeTimerFiring() throws Exception {
        testTimerFiring(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testEventTimeDeletedTimerDoesNotFire() throws Exception {
        testDeletedTimerDoesNotFire(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeDeletedTimerDoesNotFire() throws Exception {
        testDeletedTimerDoesNotFire(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testMergeWindowsIsCalled() throws Exception {
        MergingWindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockMergingAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockAssigner).mergeWindows(ArgumentMatchers.eq(Collections.singletonList(new TimeWindow(2, 4))), WindowOperatorContractTest.anyMergeCallback());
        Mockito.verify(mockAssigner).mergeWindows(ArgumentMatchers.eq(Collections.singletonList(new TimeWindow(2, 4))), WindowOperatorContractTest.anyMergeCallback());
        Mockito.verify(mockAssigner, Mockito.times(2)).mergeWindows(ArgumentMatchers.anyCollection(), WindowOperatorContractTest.anyMergeCallback());
    }

    @Test
    public void testEventTimeWindowsAreMergedEagerly() throws Exception {
        testWindowsAreMergedEagerly(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeWindowsAreMergedEagerly() throws Exception {
        testWindowsAreMergedEagerly(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testRejectShrinkingMergingEventTimeWindows() throws Exception {
        testRejectShrinkingMergingWindows(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testRejectShrinkingMergingProcessingTimeWindows() throws Exception {
        testRejectShrinkingMergingWindows(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testMergingOfExistingEventTimeWindows() throws Exception {
        testMergingOfExistingWindows(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testMergingOfExistingProcessingTimeWindows() throws Exception {
        testMergingOfExistingWindows(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testOnElementPurgeDoesNotCleanupMergingSet() throws Exception {
        MergingWindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockMergingAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                return TriggerResult.PURGE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Assert.assertEquals(1, testHarness.numKeyedStateEntries());// the merging window set

        Assert.assertEquals(1, testHarness.numEventTimeTimers());// one cleanup timer

        Assert.assertEquals(0, testHarness.getOutput().size());
    }

    @Test
    public void testOnEventTimePurgeDoesNotCleanupMergingSet() throws Exception {
        testOnTimePurgeDoesNotCleanupMergingSet(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testOnProcessingTimePurgeDoesNotCleanupMergingSet() throws Exception {
        testOnTimePurgeDoesNotCleanupMergingSet(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testNoEventTimeGarbageCollectionTimerForGlobalWindow() throws Exception {
        testNoGarbageCollectionTimerForGlobalWindow(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testNoProcessingTimeGarbageCollectionTimerForGlobalWindow() throws Exception {
        testNoGarbageCollectionTimerForGlobalWindow(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testNoEventTimeGarbageCollectionTimerForLongMax() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 20L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, ((Long.MAX_VALUE) - 10))));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        // just the window contents
        Assert.assertEquals(1, testHarness.numKeyedStateEntries());
        // no GC timer
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
    }

    @Test
    public void testProcessingTimeGarbageCollectionTimerIsAlwaysWindowMaxTimestamp() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Mockito.when(mockAssigner.isEventTime()).thenReturn(false);
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 20L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, ((Long.MAX_VALUE) - 10))));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        // just the window contents
        Assert.assertEquals(1, testHarness.numKeyedStateEntries());
        // no GC timer
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers());
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.setProcessingTime(((Long.MAX_VALUE) - 10));
        Mockito.verify(mockTrigger, Mockito.times(1)).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
    }

    @Test
    public void testEventTimeGarbageCollectionTimer() throws Exception {
        testGarbageCollectionTimer(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeGarbageCollectionTimer() throws Exception {
        testGarbageCollectionTimer(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testEventTimeTriggerTimerAndGarbageCollectionTimerCoincide() throws Exception {
        testTriggerTimerAndGarbageCollectionTimerCoincide(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeTriggerTimerAndGarbageCollectionTimerCoincide() throws Exception {
        testTriggerTimerAndGarbageCollectionTimerCoincide(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testStateAndTimerCleanupAtEventTimeGarbageCollection() throws Exception {
        testStateAndTimerCleanupAtEventTimeGarbageCollection(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testStateAndTimerCleanupAtProcessingTimeGarbageCollection() throws Exception {
        testStateAndTimerCleanupAtEventTimeGarbageCollection(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testStateAndTimerCleanupAtEventTimeGarbageCollectionWithPurgingTrigger() throws Exception {
        testStateAndTimerCleanupAtEventTimeGCWithPurgingTrigger(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testStateAndTimerCleanupAtProcessingTimeGarbageCollectionWithPurgingTrigger() throws Exception {
        testStateAndTimerCleanupAtEventTimeGCWithPurgingTrigger(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testStateAndTimerCleanupAtEventTimeGarbageCollectionWithPurgingTriggerAndMergingWindows() throws Exception {
        testStateAndTimerCleanupAtGarbageCollectionWithPurgingTriggerAndMergingWindows(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testStateAndTimerCleanupAtProcessingTimeGarbageCollectionWithPurgingTriggerAndMergingWindows() throws Exception {
        testStateAndTimerCleanupAtGarbageCollectionWithPurgingTriggerAndMergingWindows(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testMergingWindowSetClearedAtEventTimeGarbageCollection() throws Exception {
        testMergingWindowSetClearedAtGarbageCollection(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testMergingWindowSetClearedAtProcessingTimeGarbageCollection() throws Exception {
        testMergingWindowSetClearedAtGarbageCollection(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testProcessingElementsWithinAllowedLateness() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 20L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        WindowOperatorContractTest.shouldFireOnElement(mockTrigger);
        // 20 is just at the limit, window.maxTime() is 1 and allowed lateness is 20
        testHarness.processWatermark(new Watermark(20));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // FIRE should not purge contents
        Assert.assertEquals(1, testHarness.numKeyedStateEntries());// window contents plus trigger state

        Assert.assertEquals(1, testHarness.numEventTimeTimers());// just the GC timer

    }

    @Test
    public void testLateWindowDropping() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 20L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        WindowOperatorContractTest.shouldFireOnElement(mockTrigger);
        // window.maxTime() == 1 plus 20L of allowed lateness
        testHarness.processWatermark(new Watermark(21));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        // there should be nothing
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        // there should be two elements now
        Assert.assertEquals(0, testHarness.extractOutputStreamRecords().size());
    }

    @Test
    public void testStateSnapshotAndRestore() throws Exception {
        MergingWindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockMergingAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(0L);
                context.getPartitionedState(WindowOperatorContractTest.valueStateDescriptor).update("hello");
                return TriggerResult.CONTINUE;
            }
        }).when(mockTrigger).onElement(org.mockito.Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        WindowOperatorContractTest.shouldFireAndPurgeOnEventTime(mockTrigger);
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        // window-contents and trigger state for two windows plus merging window set
        Assert.assertEquals(5, testHarness.numKeyedStateEntries());
        Assert.assertEquals(4, testHarness.numEventTimeTimers());// timers/gc timers for two windows

        OperatorSubtaskState snapshot = testHarness.snapshot(0, 0);
        // restore
        mockAssigner = WindowOperatorContractTest.mockMergingAssigner();
        mockTrigger = WindowOperatorContractTest.mockTrigger();
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Exception {
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[1]));
                context.deleteEventTimeTimer(0L);
                context.getPartitionedState(WindowOperatorContractTest.valueStateDescriptor).clear();
                return null;
            }
        }).when(mockTrigger).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // only fire on the timestamp==0L timers, not the gc timers
        Mockito.when(mockTrigger.onEventTime(ArgumentMatchers.eq(0L), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE);
        mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, mockWindowFunction);
        testHarness.setup();
        testHarness.initializeState(snapshot);
        testHarness.open();
        Assert.assertEquals(0, testHarness.extractOutputStreamRecords().size());
        // verify that we still have all the state/timers/merging window set
        Assert.assertEquals(5, testHarness.numKeyedStateEntries());
        Assert.assertEquals(4, testHarness.numEventTimeTimers());// timers/gc timers for two windows

        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processWatermark(new Watermark(20L));
        Mockito.verify(mockTrigger, Mockito.times(2)).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.eq(0), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(0), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.intIterable(0), WindowOperatorContractTest.<Void>anyCollector());
        // it's also called for the cleanup timers
        Mockito.verify(mockTrigger, Mockito.times(4)).onEventTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        Mockito.verify(mockTrigger, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(0L), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyTriggerContext());
        Mockito.verify(mockTrigger, Mockito.times(1)).onEventTime(ArgumentMatchers.eq(0L), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyTriggerContext());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
    }

    @Test
    public void testPerWindowStateSetAndClearedOnEventTimePurge() throws Exception {
        testPerWindowStateSetAndClearedOnPurge(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testPerWindowStateSetAndClearedOnProcessingTimePurge() throws Exception {
        testPerWindowStateSetAndClearedOnPurge(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testWindowStateNotAvailableToMergingWindows() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockMergingAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 20L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockTrigger.onElement(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE);
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, 20)));
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                InternalWindowFunction.InternalWindowContext context = ((InternalWindowFunction.InternalWindowContext) (invocationOnMock.getArguments()[2]));
                context.windowState().getState(WindowOperatorContractTest.valueStateDescriptor).update("hello");
                return null;
            }
        }).when(mockWindowFunction).process(ArgumentMatchers.anyInt(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        expectedException.expect(UnsupportedOperationException.class);
        expectedException.expectMessage("Per-window state is not allowed when using merging windows.");
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
    }

    @Test
    public void testEventTimeQuerying() throws Exception {
        testCurrentTimeQuerying(new WindowOperatorContractTest.EventTimeAdaptor());
    }

    @Test
    public void testProcessingTimeQuerying() throws Exception {
        testCurrentTimeQuerying(new WindowOperatorContractTest.ProcessingTimeAdaptor());
    }

    @Test
    public void testStateTypeIsConsistentFromWindowStateAndGlobalState() throws Exception {
        class NoOpAggregateFunction implements AggregateFunction<String, String, String> {
            @Override
            public String createAccumulator() {
                return null;
            }

            @Override
            public String add(String value, String accumulator) {
                return null;
            }

            @Override
            public String getResult(String accumulator) {
                return null;
            }

            @Override
            public String merge(String a, String b) {
                return null;
            }
        }
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Iterable<Integer>, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 20L, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockTrigger.onElement(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE);
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(0, 20)));
        AtomicBoolean processWasInvoked = new AtomicBoolean(false);
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                InternalWindowFunction.InternalWindowContext context = ((InternalWindowFunction.InternalWindowContext) (invocationOnMock.getArguments()[2]));
                org.apache.flink.api.common.state.KeyedStateStore windowKeyedStateStore = context.windowState();
                org.apache.flink.api.common.state.KeyedStateStore globalKeyedStateStore = context.globalState();
                org.apache.flink.api.common.state.ListStateDescriptor<String> windowListStateDescriptor = new org.apache.flink.api.common.state.ListStateDescriptor<String>("windowListState", String.class);
                org.apache.flink.api.common.state.ListStateDescriptor<String> globalListStateDescriptor = new org.apache.flink.api.common.state.ListStateDescriptor<String>("globalListState", String.class);
                Assert.assertEquals(windowKeyedStateStore.getListState(windowListStateDescriptor).getClass(), globalKeyedStateStore.getListState(globalListStateDescriptor).getClass());
                ValueStateDescriptor<String> windowValueStateDescriptor = new ValueStateDescriptor<String>("windowValueState", String.class);
                ValueStateDescriptor<String> globalValueStateDescriptor = new ValueStateDescriptor<String>("globalValueState", String.class);
                Assert.assertEquals(windowKeyedStateStore.getState(windowValueStateDescriptor).getClass(), globalKeyedStateStore.getState(globalValueStateDescriptor).getClass());
                org.apache.flink.api.common.state.AggregatingStateDescriptor<String, String, String> windowAggStateDesc = new org.apache.flink.api.common.state.AggregatingStateDescriptor<String, String, String>("windowAgg", new NoOpAggregateFunction(), String.class);
                org.apache.flink.api.common.state.AggregatingStateDescriptor<String, String, String> globalAggStateDesc = new org.apache.flink.api.common.state.AggregatingStateDescriptor<String, String, String>("globalAgg", new NoOpAggregateFunction(), String.class);
                Assert.assertEquals(windowKeyedStateStore.getAggregatingState(windowAggStateDesc).getClass(), globalKeyedStateStore.getAggregatingState(globalAggStateDesc).getClass());
                org.apache.flink.api.common.state.ReducingStateDescriptor<String> windowReducingStateDesc = new org.apache.flink.api.common.state.ReducingStateDescriptor<String>("windowReducing", ( a, b) -> a, String.class);
                org.apache.flink.api.common.state.ReducingStateDescriptor<String> globalReducingStateDesc = new org.apache.flink.api.common.state.ReducingStateDescriptor<String>("globalReducing", ( a, b) -> a, String.class);
                Assert.assertEquals(windowKeyedStateStore.getReducingState(windowReducingStateDesc).getClass(), globalKeyedStateStore.getReducingState(globalReducingStateDesc).getClass());
                org.apache.flink.api.common.state.FoldingStateDescriptor<String, String> windowFoldingStateDescriptor = new org.apache.flink.api.common.state.FoldingStateDescriptor<String, String>("windowFolding", "", ( a, b) -> a, String.class);
                org.apache.flink.api.common.state.FoldingStateDescriptor<String, String> globalFoldingStateDescriptor = new org.apache.flink.api.common.state.FoldingStateDescriptor<String, String>("globalFolding", "", ( a, b) -> a, String.class);
                Assert.assertEquals(windowKeyedStateStore.getFoldingState(windowFoldingStateDescriptor).getClass(), globalKeyedStateStore.getFoldingState(globalFoldingStateDescriptor).getClass());
                org.apache.flink.api.common.state.MapStateDescriptor<String, String> windowMapStateDescriptor = new org.apache.flink.api.common.state.MapStateDescriptor<String, String>("windowMapState", String.class, String.class);
                org.apache.flink.api.common.state.MapStateDescriptor<String, String> globalMapStateDescriptor = new org.apache.flink.api.common.state.MapStateDescriptor<String, String>("globalMapState", String.class, String.class);
                Assert.assertEquals(windowKeyedStateStore.getMapState(windowMapStateDescriptor).getClass(), globalKeyedStateStore.getMapState(globalMapStateDescriptor).getClass());
                processWasInvoked.set(true);
                return null;
            }
        }).when(mockWindowFunction).process(ArgumentMatchers.anyInt(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), WindowOperatorContractTest.anyIntIterable(), WindowOperatorContractTest.<Void>anyCollector());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(0, 0L));
        Assert.assertTrue(processWasInvoked.get());
    }

    private interface TimeDomainAdaptor {
        void setIsEventTime(WindowAssigner<?, ?> mockAssigner);

        void advanceTime(OneInputStreamOperatorTestHarness testHarness, long timestamp) throws Exception;

        void registerTimer(Trigger.TriggerContext ctx, long timestamp);

        void deleteTimer(Trigger.TriggerContext ctx, long timestamp);

        int numTimers(AbstractStreamOperatorTestHarness testHarness);

        int numTimersOtherDomain(AbstractStreamOperatorTestHarness testHarness);

        void shouldRegisterTimerOnElement(Trigger<?, TimeWindow> mockTrigger, long timestamp) throws Exception;

        void shouldDeleteTimerOnElement(Trigger<?, TimeWindow> mockTrigger, long timestamp) throws Exception;

        void shouldContinueOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception;

        void shouldFireOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception;

        void shouldFireAndPurgeOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception;

        void shouldPurgeOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception;

        void verifyTriggerCallback(Trigger<?, TimeWindow> mockTrigger, VerificationMode verificationMode, Long time, TimeWindow window) throws Exception;

        void verifyCorrectTime(OneInputStreamOperatorTestHarness testHarness, InternalWindowFunction.InternalWindowContext context);
    }

    private static class EventTimeAdaptor implements WindowOperatorContractTest.TimeDomainAdaptor {
        @Override
        public void setIsEventTime(WindowAssigner<?, ?> mockAssigner) {
            Mockito.when(mockAssigner.isEventTime()).thenReturn(true);
        }

        public void advanceTime(OneInputStreamOperatorTestHarness testHarness, long timestamp) throws Exception {
            testHarness.processWatermark(new Watermark(timestamp));
        }

        @Override
        public void registerTimer(Trigger.TriggerContext ctx, long timestamp) {
            ctx.registerEventTimeTimer(timestamp);
        }

        @Override
        public void deleteTimer(Trigger.TriggerContext ctx, long timestamp) {
            ctx.deleteEventTimeTimer(timestamp);
        }

        @Override
        public int numTimers(AbstractStreamOperatorTestHarness testHarness) {
            return testHarness.numEventTimeTimers();
        }

        @Override
        public int numTimersOtherDomain(AbstractStreamOperatorTestHarness testHarness) {
            return testHarness.numProcessingTimeTimers();
        }

        @Override
        public void shouldRegisterTimerOnElement(Trigger<?, TimeWindow> mockTrigger, long timestamp) throws Exception {
            WindowOperatorContractTest.shouldRegisterEventTimeTimerOnElement(mockTrigger, timestamp);
        }

        @Override
        public void shouldDeleteTimerOnElement(Trigger<?, TimeWindow> mockTrigger, long timestamp) throws Exception {
            WindowOperatorContractTest.shouldDeleteEventTimeTimerOnElement(mockTrigger, timestamp);
        }

        @Override
        public void shouldContinueOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldContinueOnEventTime(mockTrigger);
        }

        @Override
        public void shouldFireOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldFireOnEventTime(mockTrigger);
        }

        @Override
        public void shouldFireAndPurgeOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldFireAndPurgeOnEventTime(mockTrigger);
        }

        @Override
        public void shouldPurgeOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldPurgeOnEventTime(mockTrigger);
        }

        @Override
        public void verifyTriggerCallback(Trigger<?, TimeWindow> mockTrigger, VerificationMode verificationMode, Long time, TimeWindow window) throws Exception {
            if ((time == null) && (window == null)) {
                Mockito.verify(mockTrigger, verificationMode).onEventTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
            } else
                if (time == null) {
                    Mockito.verify(mockTrigger, verificationMode).onEventTime(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(window), WindowOperatorContractTest.anyTriggerContext());
                } else
                    if (window == null) {
                        Mockito.verify(mockTrigger, verificationMode).onEventTime(ArgumentMatchers.eq(time), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
                    } else {
                        Mockito.verify(mockTrigger, verificationMode).onEventTime(ArgumentMatchers.eq(time), ArgumentMatchers.eq(window), WindowOperatorContractTest.anyTriggerContext());
                    }


        }

        @Override
        public void verifyCorrectTime(OneInputStreamOperatorTestHarness testHarness, InternalWindowFunction.InternalWindowContext context) {
            Assert.assertEquals(testHarness.getCurrentWatermark(), context.currentWatermark());
        }
    }

    private static class ProcessingTimeAdaptor implements WindowOperatorContractTest.TimeDomainAdaptor {
        @Override
        public void setIsEventTime(WindowAssigner<?, ?> mockAssigner) {
            Mockito.when(mockAssigner.isEventTime()).thenReturn(false);
        }

        public void advanceTime(OneInputStreamOperatorTestHarness testHarness, long timestamp) throws Exception {
            testHarness.setProcessingTime(timestamp);
        }

        @Override
        public void registerTimer(Trigger.TriggerContext ctx, long timestamp) {
            ctx.registerProcessingTimeTimer(timestamp);
        }

        @Override
        public void deleteTimer(Trigger.TriggerContext ctx, long timestamp) {
            ctx.deleteProcessingTimeTimer(timestamp);
        }

        @Override
        public int numTimers(AbstractStreamOperatorTestHarness testHarness) {
            return testHarness.numProcessingTimeTimers();
        }

        @Override
        public int numTimersOtherDomain(AbstractStreamOperatorTestHarness testHarness) {
            return testHarness.numEventTimeTimers();
        }

        @Override
        public void shouldRegisterTimerOnElement(Trigger<?, TimeWindow> mockTrigger, long timestamp) throws Exception {
            WindowOperatorContractTest.shouldRegisterProcessingTimeTimerOnElement(mockTrigger, timestamp);
        }

        @Override
        public void shouldDeleteTimerOnElement(Trigger<?, TimeWindow> mockTrigger, long timestamp) throws Exception {
            WindowOperatorContractTest.shouldDeleteProcessingTimeTimerOnElement(mockTrigger, timestamp);
        }

        @Override
        public void shouldContinueOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldContinueOnProcessingTime(mockTrigger);
        }

        @Override
        public void shouldFireOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldFireOnProcessingTime(mockTrigger);
        }

        @Override
        public void shouldFireAndPurgeOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldFireAndPurgeOnProcessingTime(mockTrigger);
        }

        @Override
        public void shouldPurgeOnTime(Trigger<?, TimeWindow> mockTrigger) throws Exception {
            WindowOperatorContractTest.shouldPurgeOnProcessingTime(mockTrigger);
        }

        @Override
        public void verifyTriggerCallback(Trigger<?, TimeWindow> mockTrigger, VerificationMode verificationMode, Long time, TimeWindow window) throws Exception {
            if ((time == null) && (window == null)) {
                Mockito.verify(mockTrigger, verificationMode).onProcessingTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
            } else
                if (time == null) {
                    Mockito.verify(mockTrigger, verificationMode).onProcessingTime(ArgumentMatchers.anyLong(), ArgumentMatchers.eq(window), WindowOperatorContractTest.anyTriggerContext());
                } else
                    if (window == null) {
                        Mockito.verify(mockTrigger, verificationMode).onProcessingTime(ArgumentMatchers.eq(time), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
                    } else {
                        Mockito.verify(mockTrigger, verificationMode).onProcessingTime(ArgumentMatchers.eq(time), ArgumentMatchers.eq(window), WindowOperatorContractTest.anyTriggerContext());
                    }


        }

        @Override
        public void verifyCorrectTime(OneInputStreamOperatorTestHarness testHarness, InternalWindowFunction.InternalWindowContext context) {
            Assert.assertEquals(testHarness.getProcessingTime(), context.currentProcessingTime());
        }
    }
}

