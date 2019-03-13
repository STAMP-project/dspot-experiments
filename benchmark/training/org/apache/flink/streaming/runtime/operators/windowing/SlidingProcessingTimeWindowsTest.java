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
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link SlidingProcessingTimeWindows}.
 */
public class SlidingProcessingTimeWindowsTest extends TestLogger {
    @Test
    public void testWindowAssignment() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        SlidingProcessingTimeWindows assigner = SlidingProcessingTimeWindows.of(Time.milliseconds(5000), Time.milliseconds(1000));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(0L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow((-4000), 1000), StreamRecordMatchers.timeWindow((-3000), 2000), StreamRecordMatchers.timeWindow((-2000), 3000), StreamRecordMatchers.timeWindow((-1000), 4000), StreamRecordMatchers.timeWindow(0, 5000)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(4999L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(0, 5000), StreamRecordMatchers.timeWindow(1000, 6000), StreamRecordMatchers.timeWindow(2000, 7000), StreamRecordMatchers.timeWindow(3000, 8000), StreamRecordMatchers.timeWindow(4000, 9000)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(5000L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(1000, 6000), StreamRecordMatchers.timeWindow(2000, 7000), StreamRecordMatchers.timeWindow(3000, 8000), StreamRecordMatchers.timeWindow(4000, 9000), StreamRecordMatchers.timeWindow(5000, 10000)));
    }

    @Test
    public void testWindowAssignmentWithOffset() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        SlidingProcessingTimeWindows assigner = SlidingProcessingTimeWindows.of(Time.milliseconds(5000), Time.milliseconds(1000), Time.milliseconds(100));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(100L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow((-3900), 1100), StreamRecordMatchers.timeWindow((-2900), 2100), StreamRecordMatchers.timeWindow((-1900), 3100), StreamRecordMatchers.timeWindow((-900), 4100), StreamRecordMatchers.timeWindow(100, 5100)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(5099L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(100, 5100), StreamRecordMatchers.timeWindow(1100, 6100), StreamRecordMatchers.timeWindow(2100, 7100), StreamRecordMatchers.timeWindow(3100, 8100), StreamRecordMatchers.timeWindow(4100, 9100)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(5100L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(1100, 6100), StreamRecordMatchers.timeWindow(2100, 7100), StreamRecordMatchers.timeWindow(3100, 8100), StreamRecordMatchers.timeWindow(4100, 9100), StreamRecordMatchers.timeWindow(5100, 10100)));
    }

    @Test
    public void testWindowAssignmentWithNegativeOffset() {
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        SlidingProcessingTimeWindows assigner = SlidingProcessingTimeWindows.of(Time.milliseconds(5000), Time.milliseconds(1000), Time.milliseconds((-100)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(0L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow((-4100), 900), StreamRecordMatchers.timeWindow((-3100), 1900), StreamRecordMatchers.timeWindow((-2100), 2900), StreamRecordMatchers.timeWindow((-1100), 3900), StreamRecordMatchers.timeWindow((-100), 4900)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(4899L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow((-100), 4900), StreamRecordMatchers.timeWindow(900, 5900), StreamRecordMatchers.timeWindow(1900, 6900), StreamRecordMatchers.timeWindow(2900, 7900), StreamRecordMatchers.timeWindow(3900, 8900)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(4900L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(900, 5900), StreamRecordMatchers.timeWindow(1900, 6900), StreamRecordMatchers.timeWindow(2900, 7900), StreamRecordMatchers.timeWindow(3900, 8900), StreamRecordMatchers.timeWindow(4900, 9900)));
    }

    @Test
    public void testTimeUnits() {
        // sanity check with one other time unit
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        SlidingProcessingTimeWindows assigner = SlidingProcessingTimeWindows.of(Time.seconds(5), Time.seconds(1), Time.milliseconds(500));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(100L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow((-4500), 500), StreamRecordMatchers.timeWindow((-3500), 1500), StreamRecordMatchers.timeWindow((-2500), 2500), StreamRecordMatchers.timeWindow((-1500), 3500), StreamRecordMatchers.timeWindow((-500), 4500)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(5499L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(500, 5500), StreamRecordMatchers.timeWindow(1500, 6500), StreamRecordMatchers.timeWindow(2500, 7500), StreamRecordMatchers.timeWindow(3500, 8500), StreamRecordMatchers.timeWindow(4500, 9500)));
        Mockito.when(mockContext.getCurrentProcessingTime()).thenReturn(5100L);
        Assert.assertThat(assigner.assignWindows("String", Long.MIN_VALUE, mockContext), Matchers.containsInAnyOrder(StreamRecordMatchers.timeWindow(500, 5500), StreamRecordMatchers.timeWindow(1500, 6500), StreamRecordMatchers.timeWindow(2500, 7500), StreamRecordMatchers.timeWindow(3500, 8500), StreamRecordMatchers.timeWindow(4500, 9500)));
    }

    @Test
    public void testInvalidParameters() {
        try {
            SlidingProcessingTimeWindows.of(Time.seconds((-2)), Time.seconds(1));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < slide and size > 0"));
        }
        try {
            SlidingProcessingTimeWindows.of(Time.seconds(2), Time.seconds((-1)));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < slide and size > 0"));
        }
        try {
            SlidingProcessingTimeWindows.of(Time.seconds((-20)), Time.seconds(10), Time.seconds((-1)));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < slide and size > 0"));
        }
        try {
            SlidingProcessingTimeWindows.of(Time.seconds(20), Time.seconds(10), Time.seconds((-11)));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < slide and size > 0"));
        }
        try {
            SlidingProcessingTimeWindows.of(Time.seconds(20), Time.seconds(10), Time.seconds(11));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("abs(offset) < slide and size > 0"));
        }
    }

    @Test
    public void testProperties() {
        SlidingProcessingTimeWindows assigner = SlidingProcessingTimeWindows.of(Time.seconds(5), Time.milliseconds(100));
        Assert.assertFalse(assigner.isEventTime());
        Assert.assertEquals(new TimeWindow.Serializer(), assigner.getWindowSerializer(new ExecutionConfig()));
        Assert.assertThat(assigner.getDefaultTrigger(Mockito.mock(StreamExecutionEnvironment.class)), Matchers.instanceOf(ProcessingTimeTrigger.class));
    }
}

