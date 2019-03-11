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


import MergingWindowAssigner.MergeCallback;
import WindowAssigner.WindowAssignerContext;
import java.util.Collection;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.DynamicEventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.MergingWindowAssigner;
import org.apache.flink.streaming.api.windowing.assigners.SessionWindowTimeGapExtractor;
import org.apache.flink.streaming.api.windowing.assigners.WindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;


/**
 * Tests for {@link EventTimeSessionWindows}.
 */
public class EventTimeSessionWindowsTest extends TestLogger {
    @Test
    public void testWindowAssignment() {
        final int sessionGap = 5000;
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.milliseconds(sessionGap));
        Assert.assertThat(assigner.assignWindows("String", 0L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(0, (0 + sessionGap))));
        Assert.assertThat(assigner.assignWindows("String", 4999L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(4999, (4999 + sessionGap))));
        Assert.assertThat(assigner.assignWindows("String", 5000L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(5000, (5000 + sessionGap))));
    }

    @Test
    public void testMergeSinglePointWindow() {
        MergingWindowAssigner.MergeCallback callback = Mockito.mock(MergeCallback.class);
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.milliseconds(5000));
        assigner.mergeWindows(Lists.newArrayList(new TimeWindow(0, 0)), callback);
        Mockito.verify(callback, Mockito.never()).merge(ArgumentMatchers.anyCollection(), org.mockito.Matchers.anyObject());
    }

    @Test
    public void testMergeSingleWindow() {
        MergingWindowAssigner.MergeCallback callback = Mockito.mock(MergeCallback.class);
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.milliseconds(5000));
        assigner.mergeWindows(Lists.newArrayList(new TimeWindow(0, 1)), callback);
        Mockito.verify(callback, Mockito.never()).merge(ArgumentMatchers.anyCollection(), org.mockito.Matchers.anyObject());
    }

    @Test
    public void testMergeConsecutiveWindows() {
        MergingWindowAssigner.MergeCallback callback = Mockito.mock(MergeCallback.class);
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.milliseconds(5000));
        assigner.mergeWindows(Lists.newArrayList(new TimeWindow(0, 1), new TimeWindow(1, 2), new TimeWindow(2, 3), new TimeWindow(4, 5), new TimeWindow(5, 6)), callback);
        Mockito.verify(callback, Mockito.times(1)).merge(((Collection<TimeWindow>) (MockitoHamcrest.argThat(Matchers.containsInAnyOrder(new TimeWindow(0, 1), new TimeWindow(1, 2), new TimeWindow(2, 3))))), ArgumentMatchers.eq(new TimeWindow(0, 3)));
        Mockito.verify(callback, Mockito.times(1)).merge(((Collection<TimeWindow>) (MockitoHamcrest.argThat(Matchers.containsInAnyOrder(new TimeWindow(4, 5), new TimeWindow(5, 6))))), ArgumentMatchers.eq(new TimeWindow(4, 6)));
        Mockito.verify(callback, Mockito.times(2)).merge(ArgumentMatchers.anyCollection(), org.mockito.Matchers.anyObject());
    }

    @Test
    public void testMergeCoveringWindow() {
        MergingWindowAssigner.MergeCallback callback = Mockito.mock(MergeCallback.class);
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.milliseconds(5000));
        assigner.mergeWindows(Lists.newArrayList(new TimeWindow(1, 1), new TimeWindow(0, 2), new TimeWindow(4, 7), new TimeWindow(5, 6)), callback);
        Mockito.verify(callback, Mockito.times(1)).merge(((Collection<TimeWindow>) (MockitoHamcrest.argThat(Matchers.containsInAnyOrder(new TimeWindow(1, 1), new TimeWindow(0, 2))))), ArgumentMatchers.eq(new TimeWindow(0, 2)));
        Mockito.verify(callback, Mockito.times(1)).merge(((Collection<TimeWindow>) (MockitoHamcrest.argThat(Matchers.containsInAnyOrder(new TimeWindow(5, 6), new TimeWindow(4, 7))))), ArgumentMatchers.eq(new TimeWindow(4, 7)));
        Mockito.verify(callback, Mockito.times(2)).merge(ArgumentMatchers.anyCollection(), org.mockito.Matchers.anyObject());
    }

    @Test
    public void testTimeUnits() {
        // sanity check with one other time unit
        final int sessionGap = 5000;
        WindowAssigner.WindowAssignerContext mockContext = Mockito.mock(WindowAssignerContext.class);
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.seconds((sessionGap / 1000)));
        Assert.assertThat(assigner.assignWindows("String", 0L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(0, (0 + sessionGap))));
        Assert.assertThat(assigner.assignWindows("String", 4999L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(4999, (4999 + sessionGap))));
        Assert.assertThat(assigner.assignWindows("String", 5000L, mockContext), Matchers.contains(StreamRecordMatchers.timeWindow(5000, (5000 + sessionGap))));
    }

    @Test
    public void testInvalidParameters() {
        try {
            EventTimeSessionWindows.withGap(Time.seconds((-1)));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("0 < size"));
        }
        try {
            EventTimeSessionWindows.withGap(Time.seconds(0));
            Assert.fail("should fail");
        } catch (IllegalArgumentException e) {
            Assert.assertThat(e.toString(), CoreMatchers.containsString("0 < size"));
        }
    }

    @Test
    public void testProperties() {
        EventTimeSessionWindows assigner = EventTimeSessionWindows.withGap(Time.seconds(5));
        Assert.assertTrue(assigner.isEventTime());
        Assert.assertEquals(new TimeWindow.Serializer(), assigner.getWindowSerializer(new ExecutionConfig()));
        Assert.assertThat(assigner.getDefaultTrigger(Mockito.mock(StreamExecutionEnvironment.class)), Matchers.instanceOf(EventTimeTrigger.class));
    }

    @Test
    public void testDynamicGapProperties() {
        SessionWindowTimeGapExtractor<String> extractor = Mockito.mock(SessionWindowTimeGapExtractor.class);
        DynamicEventTimeSessionWindows<String> assigner = EventTimeSessionWindows.withDynamicGap(extractor);
        Assert.assertNotNull(assigner);
        Assert.assertTrue(assigner.isEventTime());
    }
}

