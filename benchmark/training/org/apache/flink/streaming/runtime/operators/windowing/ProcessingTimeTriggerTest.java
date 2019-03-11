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
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ProcessingTimeTrigger}.
 */
public class ProcessingTimeTriggerTest {
    /**
     * Verify that state of separate windows does not leak into other windows.
     */
    @Test
    public void testWindowSeparationAndFiring() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ProcessingTimeTrigger.create(), new TimeWindow.Serializer());
        // inject several elements
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        Assert.assertEquals(FIRE, testHarness.advanceProcessingTime(2, new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        Assert.assertEquals(FIRE, testHarness.advanceProcessingTime(4, new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
    }

    /**
     * Verify that clear() does not leak across windows.
     */
    @Test
    public void testClear() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ProcessingTimeTrigger.create(), new TimeWindow.Serializer());
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        testHarness.clearTriggerState(new TimeWindow(2, 4));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        testHarness.clearTriggerState(new TimeWindow(0, 2));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
    }

    @Test
    public void testMergingWindows() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ProcessingTimeTrigger.create(), new TimeWindow.Serializer());
        Assert.assertTrue(ProcessingTimeTrigger.create().canMerge());
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        testHarness.mergeWindows(new TimeWindow(0, 4), Lists.newArrayList(new TimeWindow(0, 2), new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(0, 4)));
        Assert.assertEquals(FIRE, testHarness.advanceProcessingTime(4, new TimeWindow(0, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
    }

    /**
     * Merging a late window should not register a timer, otherwise we would get two firings:
     * one from onElement() on the merged window and one from the timer.
     */
    @Test
    public void testMergingLateWindows() throws Exception {
        TriggerTestHarness<Object, TimeWindow> testHarness = new TriggerTestHarness(ProcessingTimeTrigger.create(), new TimeWindow.Serializer());
        Assert.assertTrue(ProcessingTimeTrigger.create().canMerge());
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(0, 2)));
        Assert.assertEquals(CONTINUE, testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord<Object>(1), new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(2, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(1, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        testHarness.advanceProcessingTime(10);
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        testHarness.mergeWindows(new TimeWindow(0, 4), Lists.newArrayList(new TimeWindow(0, 2), new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numStateEntries());
        Assert.assertEquals(0, testHarness.numEventTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers());
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(0, 2)));
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(2, 4)));
        Assert.assertEquals(0, testHarness.numProcessingTimeTimers(new TimeWindow(0, 4)));
    }
}

