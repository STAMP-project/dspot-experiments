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


import WindowAssigner.WindowAssignerContext;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link TumblingEventTimeWindows}.
 */
public class TumblingEventTimeWindowsTest extends TestLogger {
    @Test
    public void testWindowAssignment() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        TumblingEventTimeWindows assigner = TumblingEventTimeWindows.of(Time.milliseconds(5000));
        Assert.assertThat(assigner.assignWindows("String", 0L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(0, 5000)));
        Assert.assertThat(assigner.assignWindows("String", 4999L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(0, 5000)));
        Assert.assertThat(assigner.assignWindows("String", 5000L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(5000, 10000)));
    }

    @Test
    public void testWindowAssignmentWithOffset() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        TumblingEventTimeWindows assigner = TumblingEventTimeWindows.of(Time.milliseconds(5000), Time.milliseconds(100));
        Assert.assertThat(assigner.assignWindows("String", 100L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(100, 5100)));
        Assert.assertThat(assigner.assignWindows("String", 5099L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(100, 5100)));
        Assert.assertThat(assigner.assignWindows("String", 5100L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(5100, 10100)));
    }

    @Test
    public void testWindowAssignmentWithNegativeOffset() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        TumblingEventTimeWindows assigner = TumblingEventTimeWindows.of(Time.milliseconds(5000), Time.milliseconds((-100)));
        Assert.assertThat(assigner.assignWindows("String", 0L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow((-100), 4900)));
        Assert.assertThat(assigner.assignWindows("String", 4899L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow((-100), 4900)));
        Assert.assertThat(assigner.assignWindows("String", 4900L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(4900, 9900)));
    }

    @Test
    public void testTimeUnits() {
        // sanity check with one other time unit
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        TumblingEventTimeWindows assigner = TumblingEventTimeWindows.of(Time.seconds(5), Time.seconds(1));
        Assert.assertThat(assigner.assignWindows("String", 1000L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(1000, 6000)));
        Assert.assertThat(assigner.assignWindows("String", 5999L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(1000, 6000)));
        Assert.assertThat(assigner.assignWindows("String", 6000L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(6000, 11000)));
    }

    @Test
    public void testInvalidParameters() {
        try {
            TumblingEventTimeWindows.of(Time.seconds((-1)));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < size"));
        }
        try {
            TumblingEventTimeWindows.of(Time.seconds(10), Time.seconds(20));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < size"));
        }
        try {
            TumblingEventTimeWindows.of(Time.seconds(10), Time.seconds((-11)));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < size"));
        }
    }

    @Test
    public void testProperties() {
        TumblingEventTimeWindows assigner = TumblingEventTimeWindows.of(Time.seconds(5), Time.milliseconds(100));
        Assert.assertTrue(assigner.isEventTime());
        Assert.assertEquals(new TimeWindow.Serializer(), assigner.getWindowSerializer(new ExecutionConfig()));
        Assert.assertThat(assigner.getDefaultTrigger(Mockito.mock(StreamExecutionEnvironment.class)), Matchers.instanceOf(EventTimeTrigger.class));
    }
}

