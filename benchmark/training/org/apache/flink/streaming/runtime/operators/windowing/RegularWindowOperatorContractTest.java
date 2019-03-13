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
import java.util.Arrays;
import org.apache.flink.api.common.state.FoldingStateDescriptor;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.base.IntSerializer;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalWindowFunction;
import org.apache.flink.streaming.util.KeyedOneInputStreamOperatorTestHarness;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * These tests verify that {@link WindowOperator} correctly interacts with the other windowing
 * components: {@link WindowAssigner},
 * {@link Trigger}.
 * {@link org.apache.flink.streaming.api.functions.windowing.WindowFunction} and window state.
 *
 * <p>These tests document the implicit contract that exists between the windowing components.
 */
public class RegularWindowOperatorContractTest extends WindowOperatorContractTest {
    @Test
    public void testReducingWindow() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Integer, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        ReducingStateDescriptor<Integer> intReduceSumDescriptor = new ReducingStateDescriptor("int-reduce", new org.apache.flink.api.common.functions.ReduceFunction<Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer reduce(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }, IntSerializer.INSTANCE);
        final ValueStateDescriptor<String> valueStateDescriptor = new ValueStateDescriptor("string-state", StringSerializer.INSTANCE);
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, intReduceSumDescriptor, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        // insert two elements without firing
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                TimeWindow window = ((TimeWindow) (invocation.getArguments()[2]));
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(window.getEnd());
                context.getPartitionedState(valueStateDescriptor).update("hello");
                return TriggerResult.FIRE;
            }
        }).when(mockTrigger).onElement(Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.eq(1), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), ArgumentMatchers.anyInt(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(1), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), ArgumentMatchers.eq(3), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(1), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyInternalWindowContext(), ArgumentMatchers.eq(3), WindowOperatorContractTest.<Void>anyCollector());
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // FIRE should not purge contents
        Assert.assertEquals(4, testHarness.numKeyedStateEntries());// window contents plus trigger state

        Assert.assertEquals(4, testHarness.numEventTimeTimers());// window timers/gc timers

    }

    @Test
    public void testFoldingWindow() throws Exception {
        WindowAssigner<Integer, TimeWindow> mockAssigner = WindowOperatorContractTest.mockTimeWindowAssigner();
        Trigger<Integer, TimeWindow> mockTrigger = WindowOperatorContractTest.mockTrigger();
        InternalWindowFunction<Integer, Void, Integer, TimeWindow> mockWindowFunction = WindowOperatorContractTest.mockWindowFunction();
        FoldingStateDescriptor<Integer, Integer> intFoldSumDescriptor = new FoldingStateDescriptor("int-fold", 0, new org.apache.flink.api.common.functions.FoldFunction<Integer, Integer>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer fold(Integer accumulator, Integer value) throws Exception {
                return accumulator + value;
            }
        }, IntSerializer.INSTANCE);
        final ValueStateDescriptor<String> valueStateDescriptor = new ValueStateDescriptor("string-state", StringSerializer.INSTANCE);
        KeyedOneInputStreamOperatorTestHarness<Integer, Integer, Void> testHarness = createWindowOperator(mockAssigner, mockTrigger, 0L, intFoldSumDescriptor, mockWindowFunction);
        testHarness.open();
        Mockito.when(mockAssigner.assignWindows(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyAssignerContext())).thenReturn(Arrays.asList(new TimeWindow(2, 4), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.getOutput().size());
        Assert.assertEquals(0, testHarness.numKeyedStateEntries());
        // insert two elements without firing
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                TimeWindow window = ((TimeWindow) (invocation.getArguments()[2]));
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                context.registerEventTimeTimer(window.getEnd());
                context.getPartitionedState(valueStateDescriptor).update("hello");
                return TriggerResult.FIRE;
            }
        }).when(mockTrigger).onElement(Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(1, 0L));
        Mockito.verify(mockWindowFunction, Mockito.times(2)).process(ArgumentMatchers.eq(1), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyInternalWindowContext(), ArgumentMatchers.anyInt(), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(1), ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyInternalWindowContext(), ArgumentMatchers.eq(3), WindowOperatorContractTest.<Void>anyCollector());
        Mockito.verify(mockWindowFunction, Mockito.times(1)).process(ArgumentMatchers.eq(1), ArgumentMatchers.eq(new TimeWindow(2, 4)), WindowOperatorContractTest.anyInternalWindowContext(), ArgumentMatchers.eq(3), WindowOperatorContractTest.<Void>anyCollector());
        // clear is only called at cleanup time/GC time
        Mockito.verify(mockTrigger, Mockito.never()).clear(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // FIRE should not purge contents
        Assert.assertEquals(4, testHarness.numKeyedStateEntries());// window contents plus trigger state

        Assert.assertEquals(4, testHarness.numEventTimeTimers());// window timers/gc timers

    }
}

