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
package org.apache.beam.runners.dataflow.worker.util.common.worker;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link GroupingShuffleRangeTracker}.
 */
@RunWith(JUnit4.class)
public class GroupingShuffleRangeTrackerTest {
    @Rule
    public final ExpectedException expected = ExpectedException.none();

    @Test
    public void testTryReturnRecordInfiniteRange() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(null, null);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 5)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 6, 8, 10)));
    }

    @Test
    public void testTryReturnRecordFiniteRange() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(1, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 5)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 6, 8, 10)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(4, 255, 255, 255, 255)));
        // Should fail on lexicographically larger positions.
        Assert.assertFalse(tracker.copy().tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0)));
        Assert.assertFalse(tracker.copy().tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 1)));
        Assert.assertFalse(tracker.copy().tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(6, 0, 0)));
    }

    @Test
    public void testTryReturnRecordWithNonSplitPoints() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(1, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 5)));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 5)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 6, 8, 10)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(4, 255, 255, 255, 255)));
    }

    @Test
    public void testFirstRecordNonSplitPoint() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(3, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(false, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5));
    }

    @Test
    public void testNonSplitPointRecordWithDifferentPosition() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(3, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5));
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(false, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 6));
    }

    @Test
    public void testTryReturnRecordBeforeStart() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(3, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3));
    }

    @Test
    public void testTryReturnNonMonotonic() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(3, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5));
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 6));
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 1));
    }

    @Test
    public void testTryReturnIdenticalPositions() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(3, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(5, 0, 0));
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5));
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5));
    }

    @Test
    public void testTrySplitAtPositionInfiniteRange() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(null, null);
        // Should fail before first record is returned.
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        // Should now succeed.
        Assert.assertTrue(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6)));
        // Should not split at same or larger position.
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6)));
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6, 7)));
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(4, 5, 6, 7)));
        // Should split at smaller position.
        Assert.assertTrue(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 1)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(2, 3, 4)));
        // Should not split at a position we're already past.
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(2, 3, 4)));
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(2, 3, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 0)));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 1)));
    }

    @Test
    public void testTrySplitAtPositionFiniteRange() throws Exception {
        GroupingShuffleRangeTracker tracker = new GroupingShuffleRangeTracker(GroupingShuffleRangeTrackerTest.ofBytes(0, 0, 0), GroupingShuffleRangeTrackerTest.ofBytes(10, 20, 30));
        // Should fail before first record is returned.
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(0, 0, 0)));
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        // Should now succeed.
        Assert.assertTrue(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6)));
        // Should not split at same or larger position.
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6)));
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 4, 5, 6, 7)));
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(4, 5, 6, 7)));
        // Should split at smaller position.
        Assert.assertTrue(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 1)));
        // But not at a position at or before last returned record.
        Assert.assertFalse(tracker.trySplitAtPosition(GroupingShuffleRangeTrackerTest.ofBytes(1, 2, 3)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(2, 3, 4)));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 0)));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, GroupingShuffleRangeTrackerTest.ofBytes(3, 2, 1)));
    }
}

