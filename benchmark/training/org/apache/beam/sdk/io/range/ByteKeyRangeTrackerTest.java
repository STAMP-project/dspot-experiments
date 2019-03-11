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
package org.apache.beam.sdk.io.range;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ByteKeyRangeTracker}.
 */
@RunWith(JUnit4.class)
public class ByteKeyRangeTrackerTest {
    private static final ByteKey BEFORE_START_KEY = ByteKey.of(17);

    private static final ByteKey INITIAL_START_KEY = ByteKey.of(18);

    private static final ByteKey AFTER_START_KEY = ByteKey.of(19);

    private static final ByteKey INITIAL_MIDDLE_KEY = ByteKey.of(35);

    private static final ByteKey NEW_START_KEY = ByteKey.of(20);

    private static final ByteKey NEW_MIDDLE_KEY = ByteKey.of(36);

    private static final ByteKey BEFORE_END_KEY = ByteKey.of(51);

    private static final ByteKey END_KEY = ByteKey.of(52);

    private static final ByteKey KEY_LARGER_THAN_END = ByteKey.of(53);

    private static final double INITIAL_RANGE_SIZE = 52 - 18;

    private static final ByteKeyRange INITIAL_RANGE = ByteKeyRange.of(ByteKeyRangeTrackerTest.INITIAL_START_KEY, ByteKeyRangeTrackerTest.END_KEY);

    private static final double NEW_RANGE_SIZE = 52 - 20;

    private static final ByteKeyRange NEW_RANGE = ByteKeyRange.of(ByteKeyRangeTrackerTest.NEW_START_KEY, ByteKeyRangeTrackerTest.END_KEY);

    @Rule
    public final ExpectedException expected = ExpectedException.none();

    /**
     * Tests for {@link ByteKeyRangeTracker#toString}.
     */
    @Test
    public void testToString() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        String expected = String.format("ByteKeyRangeTracker{range=%s, position=null}", ByteKeyRangeTrackerTest.INITIAL_RANGE);
        Assert.assertEquals(expected, tracker.toString());
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY);
        expected = String.format("ByteKeyRangeTracker{range=%s, position=%s}", ByteKeyRangeTrackerTest.INITIAL_RANGE, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY);
        Assert.assertEquals(expected, tracker.toString());
    }

    /**
     * Tests for updating the start key to the first record returned.
     */
    @Test
    public void testUpdateStartKey() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.NEW_START_KEY);
        String expected = String.format("ByteKeyRangeTracker{range=%s, position=%s}", ByteKeyRangeTrackerTest.NEW_RANGE, ByteKeyRangeTrackerTest.NEW_START_KEY);
        Assert.assertEquals(expected, tracker.toString());
    }

    /**
     * Tests for {@link ByteKeyRangeTracker#of}.
     */
    @Test
    public void testBuilding() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        Assert.assertEquals(ByteKeyRangeTrackerTest.INITIAL_START_KEY, tracker.getStartPosition());
        Assert.assertEquals(ByteKeyRangeTrackerTest.END_KEY, tracker.getStopPosition());
    }

    /**
     * Tests for {@link ByteKeyRangeTracker#getFractionConsumed()}.
     */
    @Test
    public void testGetFractionConsumed() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        double delta = 1.0E-5;
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), delta);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY);
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), delta);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY);
        Assert.assertEquals(0.5, tracker.getFractionConsumed(), delta);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.BEFORE_END_KEY);
        Assert.assertEquals((1 - (1 / (ByteKeyRangeTrackerTest.INITIAL_RANGE_SIZE))), tracker.getFractionConsumed(), delta);
    }

    @Test
    public void testGetFractionConsumedAfterDone() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        double delta = 1.0E-5;
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        tracker.markDone();
        Assert.assertEquals(1.0, tracker.getFractionConsumed(), delta);
    }

    @Test
    public void testGetFractionConsumedAfterOutOfRangeClaim() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        double delta = 1.0E-5;
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(false, ByteKeyRangeTrackerTest.KEY_LARGER_THAN_END));
        Assert.assertEquals(1.0, tracker.getFractionConsumed(), delta);
    }

    /**
     * Tests for {@link ByteKeyRangeTracker#getFractionConsumed()} with updated start key.
     */
    @Test
    public void testGetFractionConsumedUpdateStartKey() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        double delta = 1.0E-5;
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.NEW_START_KEY);
        Assert.assertEquals(0.0, tracker.getFractionConsumed(), delta);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.NEW_MIDDLE_KEY);
        Assert.assertEquals(0.5, tracker.getFractionConsumed(), delta);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.BEFORE_END_KEY);
        Assert.assertEquals((1 - (1 / (ByteKeyRangeTrackerTest.NEW_RANGE_SIZE))), tracker.getFractionConsumed(), delta);
    }

    /**
     * Tests for {@link ByteKeyRangeTracker#tryReturnRecordAt}.
     */
    @Test
    public void testTryReturnRecordAt() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        // Should be able to emit at the same key twice, should that happen.
        // Should be able to emit within range (in order, but system guarantees won't try out of order).
        // Should not be able to emit past end of range.
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.BEFORE_END_KEY));
        Assert.assertFalse(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.END_KEY));// after end

        Assert.assertFalse(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.BEFORE_END_KEY));// false because done

    }

    @Test
    public void testTryReturnFirstRecordNotSplitPoint() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(false, ByteKeyRangeTrackerTest.INITIAL_START_KEY);
    }

    @Test
    public void testTryReturnBeforeStartKey() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.BEFORE_START_KEY);
    }

    @Test
    public void testTryReturnBeforeLastReturnedRecord() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        expected.expect(IllegalStateException.class);
        tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.AFTER_START_KEY);
    }

    /**
     * Tests for {@link ByteKeyRangeTracker#trySplitAtPosition}.
     */
    @Test
    public void testSplitAtPosition() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        // Unstarted, should not split.
        Assert.assertFalse(tracker.trySplitAtPosition(ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        // Start it, split it before the end.
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        Assert.assertTrue(tracker.trySplitAtPosition(ByteKeyRangeTrackerTest.BEFORE_END_KEY));
        Assert.assertEquals(ByteKeyRangeTrackerTest.BEFORE_END_KEY, tracker.getStopPosition());
        // Should not be able to split it after the end.
        Assert.assertFalse(tracker.trySplitAtPosition(ByteKeyRangeTrackerTest.END_KEY));
        // Should not be able to split after emitting.
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        Assert.assertFalse(tracker.trySplitAtPosition(ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
    }

    /**
     * Tests for {@link ByteKeyRangeTracker#getSplitPointsConsumed()}.
     */
    @Test
    public void testGetSplitPointsConsumed() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRangeTrackerTest.INITIAL_RANGE);
        Assert.assertEquals(0, tracker.getSplitPointsConsumed());
        // Started, 0 split points consumed
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.INITIAL_START_KEY));
        Assert.assertEquals(0, tracker.getSplitPointsConsumed());
        // Processing new split point, 1 split point consumed
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.AFTER_START_KEY));
        Assert.assertEquals(1, tracker.getSplitPointsConsumed());
        // Processing new non-split point, 1 split point consumed
        Assert.assertTrue(tracker.tryReturnRecordAt(false, ByteKeyRangeTrackerTest.INITIAL_MIDDLE_KEY));
        Assert.assertEquals(1, tracker.getSplitPointsConsumed());
        // Processing new split point, 2 split points consumed
        Assert.assertTrue(tracker.tryReturnRecordAt(true, ByteKeyRangeTrackerTest.BEFORE_END_KEY));
        Assert.assertEquals(2, tracker.getSplitPointsConsumed());
        // Mark tracker as done, 3 split points consumed
        tracker.markDone();
        Assert.assertEquals(3, tracker.getSplitPointsConsumed());
    }
}

