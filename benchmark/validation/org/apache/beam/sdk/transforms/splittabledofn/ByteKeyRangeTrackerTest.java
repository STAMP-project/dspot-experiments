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


import ByteKey.EMPTY;
import ByteKeyRange.ALL_KEYS;
import ByteKeyRangeTracker.NO_KEYS;
import java.math.BigDecimal;
import org.apache.beam.sdk.io.range.ByteKey;
import org.apache.beam.sdk.io.range.ByteKeyRange;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ByteKeyRangeTrackerTest}.
 */
@RunWith(JUnit4.class)
public class ByteKeyRangeTrackerTest {
    @Rule
    public final ExpectedException expected = ExpectedException.none();

    @Test
    public void testTryClaim() throws Exception {
        ByteKeyRange range = ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192));
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(range);
        Assert.assertEquals(range, tracker.currentRestriction());
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(16)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(16, 0)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(16, 0, 0)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(153)));
        Assert.assertFalse(tracker.tryClaim(ByteKey.of(192)));
    }

    @Test
    public void testCheckpointUnstarted() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        ByteKeyRange checkpoint = tracker.checkpoint();
        // We expect to get the original range back and that the current restriction
        // is effectively made empty.
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)), checkpoint);
        Assert.assertEquals(NO_KEYS, tracker.currentRestriction());
    }

    @Test
    public void testCheckpointUnstartedForAllKeysRange() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ALL_KEYS);
        ByteKeyRange checkpoint = tracker.checkpoint();
        // We expect to get the original range back and that the current restriction
        // is effectively made empty.
        Assert.assertEquals(ALL_KEYS, checkpoint);
        Assert.assertEquals(NO_KEYS, tracker.currentRestriction());
    }

    @Test
    public void testCheckpointOnlyFailedClaim() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertFalse(tracker.tryClaim(ByteKey.of(208)));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)), tracker.currentRestriction());
        Assert.assertEquals(NO_KEYS, checkpoint);
    }

    @Test
    public void testCheckpointJustStarted() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(16)));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(16, 0)), tracker.currentRestriction());
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16, 0), ByteKey.of(192)), checkpoint);
    }

    @Test
    public void testCheckpointRegular() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(144, 0)), tracker.currentRestriction());
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(144, 0), ByteKey.of(192)), checkpoint);
    }

    @Test
    public void testCheckpointAtLast() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertFalse(tracker.tryClaim(ByteKey.of(192)));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)), tracker.currentRestriction());
        Assert.assertEquals(NO_KEYS, checkpoint);
    }

    @Test
    public void testCheckpointAtLastUsingAllKeysAndEmptyKey() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ALL_KEYS);
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertFalse(tracker.tryClaim(EMPTY));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ALL_KEYS, tracker.currentRestriction());
        Assert.assertEquals(NO_KEYS, checkpoint);
    }

    @Test
    public void testCheckpointAfterLast() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(160)));
        Assert.assertFalse(tracker.tryClaim(ByteKey.of(208)));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)), tracker.currentRestriction());
        Assert.assertEquals(NO_KEYS, checkpoint);
    }

    @Test
    public void testCheckpointAfterLastUsingEmptyKey() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(160)));
        Assert.assertFalse(tracker.tryClaim(EMPTY));
        ByteKeyRange checkpoint = tracker.checkpoint();
        Assert.assertEquals(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)), tracker.currentRestriction());
        Assert.assertEquals(NO_KEYS, checkpoint);
    }

    @Test
    public void testNonMonotonicClaim() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        expected.expectMessage("Trying to claim key [70] while last attempted key was [90]");
        tracker.tryClaim(ByteKey.of(112));
    }

    @Test
    public void testClaimBeforeStartOfRange() throws Exception {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        expected.expectMessage(("Trying to claim key [05] before start of the range " + "ByteKeyRange{startKey=[10], endKey=[c0]}"));
        tracker.tryClaim(ByteKey.of(5));
    }

    @Test
    public void testCheckDoneAfterTryClaimPastEndOfRange() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertFalse(tracker.tryClaim(ByteKey.of(208)));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneAfterTryClaimAtEndOfRange() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertFalse(tracker.tryClaim(ByteKey.of(192)));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneWhenClaimingEndOfRangeForEmptyKey() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), EMPTY));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertFalse(tracker.tryClaim(EMPTY));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneAfterTryClaimRightBeforeEndOfRange() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(191)));
        expected.expectMessage(("Last attempted key was [bf] in range ByteKeyRange{startKey=[10], endKey=[c0]}, " + "claiming work in [[bf00], [c0]) was not attempted"));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneForEmptyRange() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(NO_KEYS);
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneWhenNotDone() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(80)));
        Assert.assertTrue(tracker.tryClaim(ByteKey.of(144)));
        expected.expectMessage(("Last attempted key was [90] in range ByteKeyRange{startKey=[10], endKey=[c0]}, " + "claiming work in [[9000], [c0]) was not attempted"));
        tracker.checkDone();
    }

    @Test
    public void testCheckDoneUnstarted() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        expected.expect(IllegalStateException.class);
        tracker.checkDone();
    }

    @Test
    public void testNextByteKey() {
        Assert.assertEquals(ByteKeyRangeTracker.next(EMPTY), ByteKey.of(0));
        Assert.assertEquals(ByteKeyRangeTracker.next(ByteKey.of(0)), ByteKey.of(0, 0));
        Assert.assertEquals(ByteKeyRangeTracker.next(ByteKey.of(159)), ByteKey.of(159, 0));
        Assert.assertEquals(ByteKeyRangeTracker.next(ByteKey.of(255)), ByteKey.of(255, 0));
        Assert.assertEquals(ByteKeyRangeTracker.next(ByteKey.of(16, 16)), ByteKey.of(16, 16, 0));
        Assert.assertEquals(ByteKeyRangeTracker.next(ByteKey.of(0, 255)), ByteKey.of(0, 255, 0));
        Assert.assertEquals(ByteKeyRangeTracker.next(ByteKey.of(255, 255)), ByteKey.of(255, 255, 0));
    }

    @Test
    public void testBacklogUnstarted() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ALL_KEYS);
        Assert.assertEquals(BigDecimal.ONE, tracker.getBacklog().backlog());
        tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        Assert.assertEquals(BigDecimal.ONE, tracker.getBacklog().backlog());
    }

    @Test
    public void testBacklogFinished() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ALL_KEYS);
        tracker.tryClaim(EMPTY);
        Assert.assertEquals(BigDecimal.ZERO, tracker.getBacklog().backlog());
        tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        tracker.tryClaim(ByteKey.of(208));
        Assert.assertEquals(BigDecimal.ZERO, tracker.getBacklog().backlog());
    }

    @Test
    public void testBacklogPartiallyCompleted() {
        ByteKeyRangeTracker tracker = ByteKeyRangeTracker.of(ALL_KEYS);
        tracker.tryClaim(ByteKey.of(160));
        Assert.assertThat(tracker.getBacklog().backlog(), Matchers.allOf(Matchers.greaterThan(BigDecimal.ZERO), Matchers.lessThan(BigDecimal.ONE)));
        tracker = ByteKeyRangeTracker.of(ByteKeyRange.of(ByteKey.of(16), ByteKey.of(192)));
        tracker.tryClaim(ByteKey.of(160));
        Assert.assertThat(tracker.getBacklog().backlog(), Matchers.allOf(Matchers.greaterThan(BigDecimal.ZERO), Matchers.lessThan(BigDecimal.ONE)));
    }
}

