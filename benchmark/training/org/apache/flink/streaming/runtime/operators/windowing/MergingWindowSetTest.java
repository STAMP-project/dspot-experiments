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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.shaded.guava18.com.google.common.collect.Lists;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.assigners.MergingWindowAssigner;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.triggers.EventTimeTrigger;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for verifying that {@link MergingWindowSet} correctly merges windows in various situations
 * and that the merge callback is called with the correct sets of windows.
 */
public class MergingWindowSetTest {
    /**
     * This test uses a special (misbehaving) {@code MergingWindowAssigner} that produces cases
     * where windows that don't overlap with the newly added window are being merged. We verify
     * that the merging window set is nevertheless correct and contains all added windows.
     */
    @Test
    public void testNonEagerMerging() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(new MergingWindowSetTest.NonEagerlyMergingWindowAssigner(3000), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        TimeWindow result;
        mergeFunction.reset();
        result = windowSet.addWindow(new TimeWindow(0, 2), mergeFunction);
        Assert.assertNotNull(windowSet.getStateWindow(result));
        mergeFunction.reset();
        result = windowSet.addWindow(new TimeWindow(2, 5), mergeFunction);
        Assert.assertNotNull(windowSet.getStateWindow(result));
        mergeFunction.reset();
        result = windowSet.addWindow(new TimeWindow(1, 2), mergeFunction);
        Assert.assertNotNull(windowSet.getStateWindow(result));
        mergeFunction.reset();
        result = windowSet.addWindow(new TimeWindow(10, 12), mergeFunction);
        Assert.assertNotNull(windowSet.getStateWindow(result));
    }

    @Test
    public void testIncrementalMerging() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        // add initial window
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 4), windowSet.addWindow(new TimeWindow(0, 4), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertTrue(windowSet.getStateWindow(new TimeWindow(0, 4)).equals(new TimeWindow(0, 4)));
        // add some more windows
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 4), windowSet.addWindow(new TimeWindow(0, 4), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 5), windowSet.addWindow(new TimeWindow(3, 5), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 5), mergeFunction.mergeTarget());
        Assert.assertEquals(new TimeWindow(0, 4), mergeFunction.stateWindow());
        MatcherAssert.assertThat(mergeFunction.mergeSources(), Matchers.containsInAnyOrder(new TimeWindow(0, 4)));
        Assert.assertTrue(mergeFunction.mergedStateWindows().isEmpty());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 6), windowSet.addWindow(new TimeWindow(4, 6), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 6), mergeFunction.mergeTarget());
        Assert.assertEquals(new TimeWindow(0, 4), mergeFunction.stateWindow());
        MatcherAssert.assertThat(mergeFunction.mergeSources(), Matchers.containsInAnyOrder(new TimeWindow(0, 5)));
        Assert.assertTrue(mergeFunction.mergedStateWindows().isEmpty());
        Assert.assertEquals(new TimeWindow(0, 4), windowSet.getStateWindow(new TimeWindow(0, 6)));
        // add some windows that falls into the already merged region
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 6), windowSet.addWindow(new TimeWindow(1, 4), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 6), windowSet.addWindow(new TimeWindow(0, 4), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 6), windowSet.addWindow(new TimeWindow(3, 5), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 6), windowSet.addWindow(new TimeWindow(4, 6), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 4), windowSet.getStateWindow(new TimeWindow(0, 6)));
        // add some more windows that don't merge with the first bunch
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(11, 14), windowSet.addWindow(new TimeWindow(11, 14), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 4), windowSet.getStateWindow(new TimeWindow(0, 6)));
        Assert.assertEquals(new TimeWindow(11, 14), windowSet.getStateWindow(new TimeWindow(11, 14)));
        // add some more windows that merge with the second bunch
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(10, 14), windowSet.addWindow(new TimeWindow(10, 13), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(10, 14), mergeFunction.mergeTarget());
        Assert.assertEquals(new TimeWindow(11, 14), mergeFunction.stateWindow());
        MatcherAssert.assertThat(mergeFunction.mergeSources(), Matchers.containsInAnyOrder(new TimeWindow(11, 14)));
        Assert.assertTrue(mergeFunction.mergedStateWindows().isEmpty());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(10, 15), windowSet.addWindow(new TimeWindow(12, 15), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(10, 15), mergeFunction.mergeTarget());
        Assert.assertEquals(new TimeWindow(11, 14), mergeFunction.stateWindow());
        MatcherAssert.assertThat(mergeFunction.mergeSources(), Matchers.containsInAnyOrder(new TimeWindow(10, 14)));
        Assert.assertTrue(mergeFunction.mergedStateWindows().isEmpty());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(10, 15), windowSet.addWindow(new TimeWindow(11, 14), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 4), windowSet.getStateWindow(new TimeWindow(0, 6)));
        Assert.assertEquals(new TimeWindow(11, 14), windowSet.getStateWindow(new TimeWindow(10, 15)));
        // retire the first batch of windows
        windowSet.retireWindow(new TimeWindow(0, 6));
        Assert.assertTrue(((windowSet.getStateWindow(new TimeWindow(0, 6))) == null));
        Assert.assertTrue(windowSet.getStateWindow(new TimeWindow(10, 15)).equals(new TimeWindow(11, 14)));
    }

    @Test
    public void testLateMerging() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        // add several non-overlapping initial windows
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 3), windowSet.addWindow(new TimeWindow(0, 3), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 3), windowSet.getStateWindow(new TimeWindow(0, 3)));
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(5, 8), windowSet.addWindow(new TimeWindow(5, 8), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(5, 8), windowSet.getStateWindow(new TimeWindow(5, 8)));
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(10, 13), windowSet.addWindow(new TimeWindow(10, 13), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(10, 13), windowSet.getStateWindow(new TimeWindow(10, 13)));
        // add a window that merges the later two windows
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(5, 13), windowSet.addWindow(new TimeWindow(8, 10), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(5, 13), mergeFunction.mergeTarget());
        MatcherAssert.assertThat(mergeFunction.stateWindow(), CoreMatchers.anyOf(Is.is(new TimeWindow(5, 8)), Is.is(new TimeWindow(10, 13))));
        MatcherAssert.assertThat(mergeFunction.mergeSources(), Matchers.containsInAnyOrder(new TimeWindow(5, 8), new TimeWindow(10, 13)));
        MatcherAssert.assertThat(mergeFunction.mergedStateWindows(), CoreMatchers.anyOf(Matchers.containsInAnyOrder(new TimeWindow(10, 13)), Matchers.containsInAnyOrder(new TimeWindow(5, 8))));
        MatcherAssert.assertThat(mergeFunction.mergedStateWindows(), IsNot.not(CoreMatchers.hasItem(mergeFunction.mergeTarget())));
        Assert.assertEquals(new TimeWindow(0, 3), windowSet.getStateWindow(new TimeWindow(0, 3)));
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(5, 13), windowSet.addWindow(new TimeWindow(5, 8), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(5, 13), windowSet.addWindow(new TimeWindow(8, 10), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(5, 13), windowSet.addWindow(new TimeWindow(10, 13), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        MatcherAssert.assertThat(windowSet.getStateWindow(new TimeWindow(5, 13)), CoreMatchers.anyOf(Is.is(new TimeWindow(5, 8)), Is.is(new TimeWindow(10, 13))));
        // add a window that merges all of them together
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 13), windowSet.addWindow(new TimeWindow(3, 5), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(0, 13), mergeFunction.mergeTarget());
        MatcherAssert.assertThat(mergeFunction.stateWindow(), CoreMatchers.anyOf(Is.is(new TimeWindow(0, 3)), Is.is(new TimeWindow(5, 8)), Is.is(new TimeWindow(10, 13))));
        MatcherAssert.assertThat(mergeFunction.mergeSources(), Matchers.containsInAnyOrder(new TimeWindow(0, 3), new TimeWindow(5, 13)));
        MatcherAssert.assertThat(mergeFunction.mergedStateWindows(), CoreMatchers.anyOf(Matchers.containsInAnyOrder(new TimeWindow(0, 3)), Matchers.containsInAnyOrder(new TimeWindow(5, 8)), Matchers.containsInAnyOrder(new TimeWindow(10, 13))));
        MatcherAssert.assertThat(mergeFunction.mergedStateWindows(), IsNot.not(CoreMatchers.hasItem(mergeFunction.mergeTarget())));
        MatcherAssert.assertThat(windowSet.getStateWindow(new TimeWindow(0, 13)), CoreMatchers.anyOf(Is.is(new TimeWindow(0, 3)), Is.is(new TimeWindow(5, 8)), Is.is(new TimeWindow(10, 13))));
    }

    /**
     * Test merging of a large new window that covers one existing windows.
     */
    @Test
    public void testMergeLargeWindowCoveringSingleWindow() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        // add an initial small window
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.addWindow(new TimeWindow(1, 2), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.getStateWindow(new TimeWindow(1, 2)));
        // add a new window that completely covers the existing window
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 3), windowSet.addWindow(new TimeWindow(0, 3), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.getStateWindow(new TimeWindow(0, 3)));
    }

    /**
     * Test adding a new window that is identical to an existing window. This should not cause
     * a merge.
     */
    @Test
    public void testAddingIdenticalWindows() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.addWindow(new TimeWindow(1, 2), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.getStateWindow(new TimeWindow(1, 2)));
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.addWindow(new TimeWindow(1, 2), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.getStateWindow(new TimeWindow(1, 2)));
    }

    /**
     * Test merging of a large new window that covers multiple existing windows.
     */
    @Test
    public void testMergeLargeWindowCoveringMultipleWindows() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        // add several non-overlapping initial windows
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(1, 3), windowSet.addWindow(new TimeWindow(1, 3), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(1, 3), windowSet.getStateWindow(new TimeWindow(1, 3)));
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(5, 8), windowSet.addWindow(new TimeWindow(5, 8), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(5, 8), windowSet.getStateWindow(new TimeWindow(5, 8)));
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(10, 13), windowSet.addWindow(new TimeWindow(10, 13), mergeFunction));
        Assert.assertFalse(mergeFunction.hasMerged());
        Assert.assertEquals(new TimeWindow(10, 13), windowSet.getStateWindow(new TimeWindow(10, 13)));
        // add a new window that completely covers the existing windows
        mergeFunction.reset();
        Assert.assertEquals(new TimeWindow(0, 13), windowSet.addWindow(new TimeWindow(0, 13), mergeFunction));
        Assert.assertTrue(mergeFunction.hasMerged());
        MatcherAssert.assertThat(mergeFunction.mergedStateWindows(), CoreMatchers.anyOf(Matchers.containsInAnyOrder(new TimeWindow(0, 3), new TimeWindow(5, 8)), Matchers.containsInAnyOrder(new TimeWindow(0, 3), new TimeWindow(10, 13)), Matchers.containsInAnyOrder(new TimeWindow(5, 8), new TimeWindow(10, 13))));
        MatcherAssert.assertThat(windowSet.getStateWindow(new TimeWindow(0, 13)), CoreMatchers.anyOf(Is.is(new TimeWindow(1, 3)), Is.is(new TimeWindow(5, 8)), Is.is(new TimeWindow(10, 13))));
    }

    @Test
    public void testRestoreFromState() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        Mockito.when(mockState.get()).thenReturn(Lists.newArrayList(new Tuple2(new TimeWindow(17, 42), new TimeWindow(42, 17)), new Tuple2(new TimeWindow(1, 2), new TimeWindow(3, 4))));
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        Assert.assertEquals(new TimeWindow(42, 17), windowSet.getStateWindow(new TimeWindow(17, 42)));
        Assert.assertEquals(new TimeWindow(3, 4), windowSet.getStateWindow(new TimeWindow(1, 2)));
    }

    @Test
    public void testPersist() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        MergingWindowSetTest.TestingMergeFunction mergeFunction = new MergingWindowSetTest.TestingMergeFunction();
        windowSet.addWindow(new TimeWindow(1, 2), mergeFunction);
        windowSet.addWindow(new TimeWindow(17, 42), mergeFunction);
        Assert.assertEquals(new TimeWindow(1, 2), windowSet.getStateWindow(new TimeWindow(1, 2)));
        Assert.assertEquals(new TimeWindow(17, 42), windowSet.getStateWindow(new TimeWindow(17, 42)));
        windowSet.persist();
        Mockito.verify(mockState).add(ArgumentMatchers.eq(new Tuple2(new TimeWindow(1, 2), new TimeWindow(1, 2))));
        Mockito.verify(mockState).add(ArgumentMatchers.eq(new Tuple2(new TimeWindow(17, 42), new TimeWindow(17, 42))));
        Mockito.verify(mockState, Mockito.times(2)).add(org.mockito.Matchers.<Tuple2<TimeWindow, TimeWindow>>anyObject());
    }

    @Test
    public void testPersistOnlyIfHaveUpdates() throws Exception {
        @SuppressWarnings("unchecked")
        ListState<Tuple2<TimeWindow, TimeWindow>> mockState = Mockito.mock(ListState.class);
        Mockito.when(mockState.get()).thenReturn(Lists.newArrayList(new Tuple2(new TimeWindow(17, 42), new TimeWindow(42, 17)), new Tuple2(new TimeWindow(1, 2), new TimeWindow(3, 4))));
        MergingWindowSet<TimeWindow> windowSet = new MergingWindowSet(EventTimeSessionWindows.withGap(Time.milliseconds(3)), mockState);
        Assert.assertEquals(new TimeWindow(42, 17), windowSet.getStateWindow(new TimeWindow(17, 42)));
        Assert.assertEquals(new TimeWindow(3, 4), windowSet.getStateWindow(new TimeWindow(1, 2)));
        windowSet.persist();
        Mockito.verify(mockState, Mockito.times(0)).add(org.mockito.Matchers.<Tuple2<TimeWindow, TimeWindow>>anyObject());
    }

    private static class TestingMergeFunction implements MergingWindowSet.MergeFunction<TimeWindow> {
        private TimeWindow target = null;

        private Collection<TimeWindow> sources = null;

        private TimeWindow stateWindow = null;

        private Collection<TimeWindow> mergedStateWindows = null;

        public void reset() {
            target = null;
            sources = null;
            stateWindow = null;
            mergedStateWindows = null;
        }

        public boolean hasMerged() {
            return (target) != null;
        }

        public TimeWindow mergeTarget() {
            return target;
        }

        public Collection<TimeWindow> mergeSources() {
            return sources;
        }

        public TimeWindow stateWindow() {
            return stateWindow;
        }

        public Collection<TimeWindow> mergedStateWindows() {
            return mergedStateWindows;
        }

        @Override
        public void merge(TimeWindow mergeResult, Collection<TimeWindow> mergedWindows, TimeWindow stateWindowResult, Collection<TimeWindow> mergedStateWindows) throws Exception {
            if ((target) != null) {
                Assert.fail("More than one merge for adding a Window should not occur.");
            }
            this.stateWindow = stateWindowResult;
            this.target = mergeResult;
            this.mergedStateWindows = mergedStateWindows;
            this.sources = mergedWindows;
        }
    }

    /**
     * A special {@link MergingWindowAssigner} that let's windows get larger which leads to windows
     * being merged lazily.
     */
    static class NonEagerlyMergingWindowAssigner extends MergingWindowAssigner<Object, TimeWindow> {
        private static final long serialVersionUID = 1L;

        protected long sessionTimeout;

        public NonEagerlyMergingWindowAssigner(long sessionTimeout) {
            this.sessionTimeout = sessionTimeout;
        }

        @Override
        public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
            return Collections.singletonList(new TimeWindow(timestamp, (timestamp + (sessionTimeout))));
        }

        @Override
        public Trigger<Object, TimeWindow> getDefaultTrigger(StreamExecutionEnvironment env) {
            return EventTimeTrigger.create();
        }

        @Override
        public TypeSerializer<TimeWindow> getWindowSerializer(ExecutionConfig executionConfig) {
            return new TimeWindow.Serializer();
        }

        @Override
        public boolean isEventTime() {
            return true;
        }

        /**
         * Merge overlapping {@link TimeWindow}s.
         */
        @Override
        public void mergeWindows(Collection<TimeWindow> windows, MergingWindowAssigner.MergeCallback<TimeWindow> c) {
            TimeWindow earliestStart = null;
            for (TimeWindow win : windows) {
                if (earliestStart == null) {
                    earliestStart = win;
                } else
                    if ((win.getStart()) < (earliestStart.getStart())) {
                        earliestStart = win;
                    }

            }
            List<TimeWindow> associatedWindows = new ArrayList<>();
            for (TimeWindow win : windows) {
                if (((win.getStart()) < (earliestStart.getEnd())) && ((win.getStart()) >= (earliestStart.getStart()))) {
                    associatedWindows.add(win);
                }
            }
            TimeWindow target = new TimeWindow(earliestStart.getStart(), ((earliestStart.getEnd()) + 1));
            if ((associatedWindows.size()) > 1) {
                c.merge(associatedWindows, target);
            }
        }
    }
}

