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


import TriggerResult.CONTINUE;
import TriggerResult.FIRE;
import java.util.Collection;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ContinuousEventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ContinuousEventTimeTrigger}.
 */
public class ContinuousEventTimeTriggerTest {
    /**
     * Verify that the trigger doesn't fail with an NPE if we insert a timer firing when there is
     * no trigger state.
     */
    @Test
    public void testTriggerHandlesAllOnTimerCalls() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ContinuousEventTimeTrigger.<TimeWindow>of(Time.milliseconds(5)), new TimeWindow.Serializer());
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        // this will make the elements we now process fall into late windows, i.e. no trigger state
        // will be created
        testHarness.advanceWatermark(10);
        // late fires immediately
        Assert.assertEquals(FIRE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        // simulate a GC timer firing
        testHarness.invokeOnEventTime(20, new TimeWindow(0, 2));
    }

    /**
     * Verify that state &lt;TimeWindow&gt;of separate windows does not leak into other windows.
     */
    @Test
    public void testWindowSeparationAndFiring() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ContinuousEventTimeTrigger.<TimeWindow>of(Time.hours(1)), new TimeWindow.Serializer());
        // inject several elements
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(2, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(4, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(2, 4)));
        Collection<Tuple2<TimeWindow, TriggerResult>> triggerResults = testHarness.advanceWatermark(2);
        boolean sawFiring = false;
        for (Tuple2<TimeWindow, TriggerResult> r : triggerResults) {
            if (r.f0.equals(new TimeWindow(0, 2))) {
                sawFiring = true;
                Assert.assertTrue(r.f1.equals(FIRE));
            }
        }
        Assert.assertTrue(sawFiring);
        Assert.assertEquals(2, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(3, testHarness.numEventTimeTimers());
        Assert.assertEquals(1, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(2, 4)));
        triggerResults = testHarness.advanceWatermark(4);
        sawFiring = false;
        for (Tuple2<TimeWindow, TriggerResult> r : triggerResults) {
            if (r.f0.equals(new TimeWindow(2, 4))) {
                sawFiring = true;
                Assert.assertTrue(r.f1.equals(FIRE));
            }
        }
        Assert.assertTrue(sawFiring);
        Assert.assertEquals(2, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(2, testHarness.numEventTimeTimers());
    }

    /**
     * Verify that late elements trigger immediately and also that we don't set a timer
     * for those.
     */
    @Test
    public void testLateElementTriggersImmediately() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ContinuousEventTimeTrigger.<TimeWindow>of(Time.hours(1)), new TimeWindow.Serializer());
        testHarness.advanceWatermark(2);
        Assert.assertEquals(FIRE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
    }

    /**
     * Verify that clear() does not leak across windows.
     */
    @Test
    public void testClear() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ContinuousEventTimeTrigger.<TimeWindow>of(Time.hours(1)), new TimeWindow.Serializer());
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(2, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(4, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(2, 4)));
        testHarness.clearTriggerState(new TimeWindow(2, 4));
        Assert.assertEquals(1, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(3, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(1, testHarness.numEventTimeTimers(new TimeWindow(2, 4)));
        testHarness.clearTriggerState(new TimeWindow(0, 2));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(2, testHarness.numEventTimeTimers());// doesn't clean up timers

    }

    @Test
    public void testMergingWindows() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ContinuousEventTimeTrigger.<TimeWindow>of(Time.hours(1)), new TimeWindow.Serializer());
        Assert.assertTrue(ContinuousEventTimeTrigger.<TimeWindow>of(Time.hours(1)).canMerge());
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(2, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(4, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(2, 4)));
        testHarness.mergeWindows(new TimeWindow(0, 4), Lists.newArrayList(new TimeWindow(0, 2), new TimeWindow(2, 4)));
        Assert.assertEquals(1, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(5, testHarness.numEventTimeTimers());// on merging, timers are not cleaned up

        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(2, testHarness.numEventTimeTimers(new TimeWindow(2, 4)));
        Assert.assertEquals(1, testHarness.numEventTimeTimers(new TimeWindow(0, 4)));
        Collection<Tuple2<TimeWindow, TriggerResult>> triggerResults = testHarness.advanceWatermark(4);
        boolean sawFiring = false;
        for (Tuple2<TimeWindow, TriggerResult> r : triggerResults) {
            if (r.f0.equals(new TimeWindow(0, 4))) {
                sawFiring = true;
                Assert.assertTrue(r.f1.equals(FIRE));
            }
        }
        Assert.assertTrue(sawFiring);
        Assert.assertEquals(1, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(1, testHarness.numEventTimeTimers());
    }
}

