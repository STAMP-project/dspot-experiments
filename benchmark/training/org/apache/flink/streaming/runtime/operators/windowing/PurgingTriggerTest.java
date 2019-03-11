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
package org.apache.flink.streaming.runtime.operators.windowing;


import Trigger.TriggerContext;
import TriggerResult.CONTINUE;
import TriggerResult.FIRE;
import TriggerResult.FIRE_AND_PURGE;
import TriggerResult.PURGE;
import java.lang.reflect.Method;
import java.util.Collections;
import org.apache.flink.streaming.api.windowing.triggers.PurgingTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link PurgingTrigger}.
 */
public class PurgingTriggerTest {
    /**
     * Check if {@link PurgingTrigger} implements all methods of {@link Trigger}, as a sanity
     * check.
     */
    @Test
    public void testAllMethodsImplemented() throws NoSuchMethodException {
        for (Method triggerMethod : Trigger.class.getDeclaredMethods()) {
            // try retrieving the method, this will throw an exception if we can't find it
            PurgingTrigger.class.getDeclaredMethod(triggerMethod.getName(), triggerMethod.getParameterTypes());
        }
    }

    @Test
    public void testForwarding() throws Exception {
        Trigger<Object, TimeWindow> mockTrigger = Mockito.mock(Trigger.class);
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(PurgingTrigger.of(mockTrigger), new TimeWindow.Serializer());
        Mockito.when(mockTrigger.onElement(Matchers.anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(CONTINUE);
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onElement(Matchers.anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE);
        Assert.assertEquals(FIRE_AND_PURGE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onElement(Matchers.anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE_AND_PURGE);
        Assert.assertEquals(FIRE_AND_PURGE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onElement(Matchers.anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(PURGE);
        Assert.assertEquals(PURGE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                // register some timers that we can step through to call onEventTime several
                // times in a row
                context.registerEventTimeTimer(1);
                context.registerEventTimeTimer(2);
                context.registerEventTimeTimer(3);
                context.registerEventTimeTimer(4);
                return TriggerResult.CONTINUE;
            }
        }).when(mockTrigger).onElement(Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // set up our timers
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2));
        Assert.assertEquals(4, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onEventTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(CONTINUE);
        Assert.assertEquals(CONTINUE, testHarness.advanceWatermark(1, new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onEventTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE);
        Assert.assertEquals(FIRE_AND_PURGE, testHarness.advanceWatermark(2, new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onEventTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE_AND_PURGE);
        Assert.assertEquals(FIRE_AND_PURGE, testHarness.advanceWatermark(3, new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onEventTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(PURGE);
        Assert.assertEquals(PURGE, testHarness.advanceWatermark(4, new TimeWindow(0, 2)));
        Mockito.doAnswer(new Answer<TriggerResult>() {
            @Override
            public TriggerResult answer(InvocationOnMock invocation) throws Exception {
                Trigger.TriggerContext context = ((Trigger.TriggerContext) (invocation.getArguments()[3]));
                // register some timers that we can step through to call onEventTime several
                // times in a row
                context.registerProcessingTimeTimer(1);
                context.registerProcessingTimeTimer(2);
                context.registerProcessingTimeTimer(3);
                context.registerProcessingTimeTimer(4);
                return TriggerResult.CONTINUE;
            }
        }).when(mockTrigger).onElement(Matchers.<Integer>anyObject(), ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext());
        // set up our timers
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2));
        Assert.assertEquals(4, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onProcessingTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(CONTINUE);
        Assert.assertEquals(CONTINUE, testHarness.advanceProcessingTime(1, new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onProcessingTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE);
        Assert.assertEquals(FIRE_AND_PURGE, testHarness.advanceProcessingTime(2, new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onProcessingTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(FIRE_AND_PURGE);
        Assert.assertEquals(FIRE_AND_PURGE, testHarness.advanceProcessingTime(3, new TimeWindow(0, 2)));
        Mockito.when(mockTrigger.onProcessingTime(ArgumentMatchers.anyLong(), WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyTriggerContext())).thenReturn(PURGE);
        Assert.assertEquals(PURGE, testHarness.advanceProcessingTime(4, new TimeWindow(0, 2)));
        testHarness.mergeWindows(new TimeWindow(0, 2), Collections.singletonList(new TimeWindow(0, 1)));
        Mockito.verify(mockTrigger, Mockito.times(1)).onMerge(WindowOperatorContractTest.anyTimeWindow(), WindowOperatorContractTest.anyOnMergeContext());
        testHarness.clearTriggerState(new TimeWindow(0, 2));
        Mockito.verify(mockTrigger, Mockito.times(1)).clear(ArgumentMatchers.eq(new TimeWindow(0, 2)), WindowOperatorContractTest.anyTriggerContext());
    }
}

