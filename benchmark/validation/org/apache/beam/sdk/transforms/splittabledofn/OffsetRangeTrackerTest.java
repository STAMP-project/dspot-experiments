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
package org.apache.beam.sdk.transforms.splittabledofn;


import java.math.BigDecimal;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link OffsetRangeTracker}.
 */
@RunWith(JUnit4.class)
public class OffsetRangeTrackerTest {
    @Rule
    public final ExpectedException expected = ExpectedException.none();

    @Test
    public void testTryClaim() throws Exception {
        OffsetRange range = new OffsetRange(100, 200);
        OffsetRangeTracker tracker = new OffsetRangeTracker(range);
        Assert.assertEquals(range, tracker.currentRestriction());
        Assert.assertTrue(tracker.tryClaim(100L));
        Assert.assertTrue(tracker.tryClaim(150L));
        Assert.assertTrue(tracker.tryClaim(199L));
        Assert.assertFalse(tracker.tryClaim(200L));
    }

    @Test
    public void testCheckpointUnstarted() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        expected.expect(IllegalStateException.class);
        tracker.checkpoint();
    }

    @Test
    public void testCheckpointOnlyFailedClaim() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertFalse(tracker.tryClaim(250L));
        expected.expect(IllegalStateException.class);
        OffsetRange checkpoint = tracker.checkpoint();
    }

    @Test
    public void testCheckpointJustStarted() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(100L));
        OffsetRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(new OffsetRange(100, 101), tracker.currentRestriction());
        Assert.assertEquals(new OffsetRange(101, 200), checkpoint);
    }

    @Test
    public void testCheckpointRegular() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(105L));
        Assert.assertTrue(tracker.tryClaim(110L));
        OffsetRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(new OffsetRange(100, 111), tracker.currentRestriction());
        Assert.assertEquals(new OffsetRange(111, 200), checkpoint);
    }

    @Test
    public void testCheckpointClaimedLast() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(105L));
        Assert.assertTrue(tracker.tryClaim(110L));
        Assert.assertTrue(tracker.tryClaim(199L));
        OffsetRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(new OffsetRange(100, 200), tracker.currentRestriction());
        Assert.assertEquals(new OffsetRange(200, 200), checkpoint);
    }

    @Test
    public void testCheckpointAfterFailedClaim() throws Exception {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(105L));
        Assert.assertTrue(tracker.tryClaim(110L));
        Assert.assertTrue(tracker.tryClaim(160L));
        Assert.assertFalse(tracker.tryClaim(240L));
        OffsetRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(new OffsetRange(100, 161), tracker.currentRestriction());
        Assert.assertEquals(new OffsetRange(161, 200), checkpoint);
    }

    @Test
    public void testNonMonotonicClaim() throws Exception {
        expected.expectMessage("Trying to claim offset 103 while last attempted was 110");
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(105L));
        Assert.assertTrue(tracker.tryClaim(110L));
        tracker.tryClaim(103L);
    }

    @Test
    public void testClaimBeforeStartOfRange() throws Exception {
        expected.expectMessage("Trying to claim offset 90 before start of the range [100, 200)");
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        tracker.tryClaim(90L);
    }

    @Test
    public void testCheckDoneAfterTryClaimPastEndOfRange() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(150L));
        Assert.assertTrue(tracker.tryClaim(175L));
        Assert.assertFalse(tracker.tryClaim(220L));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneAfterTryClaimAtEndOfRange() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(150L));
        Assert.assertTrue(tracker.tryClaim(175L));
        Assert.assertFalse(tracker.tryClaim(200L));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneAfterTryClaimRightBeforeEndOfRange() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(150L));
        Assert.assertTrue(tracker.tryClaim(175L));
        Assert.assertTrue(tracker.tryClaim(199L));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneWhenNotDone() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertTrue(tracker.tryClaim(150L));
        Assert.assertTrue(tracker.tryClaim(175L));
        expected.expectMessage(("Last attempted offset was 175 in range [100, 200), " + "claiming work in [176, 200) was not attempted"));
        tracker.checkDone();
    }

    @Test
    public void testBacklogUnstarted() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(0, 200));
        Assert.assertEquals(BigDecimal.valueOf(200), tracker.getBacklog().backlog());
        tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        Assert.assertEquals(BigDecimal.valueOf(100), tracker.getBacklog().backlog());
    }

    @Test
    public void testBacklogFinished() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(0, 200));
        tracker.tryClaim(300L);
        Assert.assertEquals(BigDecimal.ZERO, tracker.getBacklog().backlog());
        tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        tracker.tryClaim(300L);
        Assert.assertEquals(BigDecimal.ZERO, tracker.getBacklog().backlog());
    }

    @Test
    public void testBacklogPartiallyCompleted() {
        OffsetRangeTracker tracker = new OffsetRangeTracker(new OffsetRange(0, 200));
        tracker.tryClaim(150L);
        Assert.assertEquals(BigDecimal.valueOf(50), tracker.getBacklog().backlog());
        tracker = new OffsetRangeTracker(new OffsetRange(100, 200));
        tracker.tryClaim(150L);
        Assert.assertEquals(BigDecimal.valueOf(50), tracker.getBacklog().backlog());
    }
}

