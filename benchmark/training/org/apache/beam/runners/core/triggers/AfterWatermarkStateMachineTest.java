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
package org.apache.beam.runners.core.triggers;


import TimeDomain.EVENT_TIME;
import org.apache.beam.runners.core.triggers.TriggerStateMachine.OnMergeContext;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests the {@link AfterWatermarkStateMachine} triggers.
 */
@RunWith(JUnit4.class)
public class AfterWatermarkStateMachineTest {
    @Mock
    private TriggerStateMachine mockEarly;

    @Mock
    private TriggerStateMachine mockLate;

    private TriggerStateMachineTester.SimpleTriggerStateMachineTester<IntervalWindow> tester;

    @Test
    public void testEarlyAndAtWatermark() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(mockEarly), FixedWindows.of(Duration.millis(100)));
        injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(100));
        testRunningAsTrigger(mockEarly, window);
        // Fire due to watermark
        Mockito.when(mockEarly.shouldFire(AfterWatermarkStateMachineTest.anyTriggerContext())).thenReturn(false);
        tester.advanceInputWatermark(new Instant(100));
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertTrue(tester.isMarkedFinished(window));
    }

    @Test
    public void testTimerForEndOfWindow() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow(), FixedWindows.of(Duration.millis(100)));
        Assert.assertThat(tester.getNextTimer(EVENT_TIME), Matchers.nullValue());
        injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(100));
        Assert.assertThat(tester.getNextTimer(EVENT_TIME), Matchers.equalTo(window.maxTimestamp()));
    }

    @Test
    public void testTimerForEndOfWindowCompound() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(NeverStateMachine.ever()), FixedWindows.of(Duration.millis(100)));
        Assert.assertThat(tester.getNextTimer(EVENT_TIME), Matchers.nullValue());
        injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(100));
        Assert.assertThat(tester.getNextTimer(EVENT_TIME), Matchers.equalTo(window.maxTimestamp()));
    }

    @Test
    public void testAtWatermarkAndLate() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withLateFirings(mockLate), FixedWindows.of(Duration.millis(100)));
        injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(100));
        // No early firing, just double checking
        Mockito.when(mockEarly.shouldFire(AfterWatermarkStateMachineTest.anyTriggerContext())).thenReturn(true);
        Assert.assertFalse(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.isMarkedFinished(window));
        // Fire due to watermark
        Mockito.when(mockEarly.shouldFire(AfterWatermarkStateMachineTest.anyTriggerContext())).thenReturn(false);
        tester.advanceInputWatermark(new Instant(100));
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.isMarkedFinished(window));
        testRunningAsTrigger(mockLate, window);
    }

    @Test
    public void testEarlyAndAtWatermarkAndLate() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(mockEarly).withLateFirings(mockLate), FixedWindows.of(Duration.millis(100)));
        injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(100));
        testRunningAsTrigger(mockEarly, window);
        // Fire due to watermark
        Mockito.when(mockEarly.shouldFire(AfterWatermarkStateMachineTest.anyTriggerContext())).thenReturn(false);
        tester.advanceInputWatermark(new Instant(100));
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.isMarkedFinished(window));
        testRunningAsTrigger(mockLate, window);
    }

    /**
     * Tests that if the EOW is finished in both as well as the merged window, then it is finished in
     * the merged result.
     *
     * <p>Because windows are discarded when a trigger finishes, we need to embed this in a sequence
     * in order to check that it is re-activated. So this test is potentially sensitive to other
     * triggers' correctness.
     */
    @Test
    public void testOnMergeAlreadyFinished() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterEachStateMachine.inOrder(AfterWatermarkStateMachine.pastEndOfWindow(), RepeatedlyStateMachine.forever(AfterPaneStateMachine.elementCountAtLeast(1))), Sessions.withGapDuration(Duration.millis(10)));
        tester.injectElements(1);
        tester.injectElements(5);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(11));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(5), new Instant(15));
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(15));
        // Finish the AfterWatermark.pastEndOfWindow() trigger in both windows
        tester.advanceInputWatermark(new Instant(15));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        tester.fireIfShouldFire(firstWindow);
        tester.fireIfShouldFire(secondWindow);
        // Confirm that we are on the second trigger by probing
        Assert.assertFalse(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        tester.injectElements(1);
        tester.injectElements(5);
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        tester.fireIfShouldFire(firstWindow);
        tester.fireIfShouldFire(secondWindow);
        // Merging should leave it finished
        tester.mergeWindows();
        // Confirm that we are on the second trigger by probing
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        tester.injectElements(1);
        Assert.assertTrue(tester.shouldFire(mergedWindow));
    }

    /**
     * Tests that the trigger rewinds to be non-finished in the merged window.
     *
     * <p>Because windows are discarded when a trigger finishes, we need to embed this in a sequence
     * in order to check that it is re-activated. So this test is potentially sensitive to other
     * triggers' correctness.
     */
    @Test
    public void testOnMergeRewinds() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterEachStateMachine.inOrder(AfterWatermarkStateMachine.pastEndOfWindow(), RepeatedlyStateMachine.forever(AfterPaneStateMachine.elementCountAtLeast(1))), Sessions.withGapDuration(Duration.millis(10)));
        tester.injectElements(1);
        tester.injectElements(5);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(11));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(5), new Instant(15));
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(15));
        // Finish the AfterWatermark.pastEndOfWindow() trigger in only the first window
        tester.advanceInputWatermark(new Instant(11));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        tester.fireIfShouldFire(firstWindow);
        // Confirm that we are on the second trigger by probing
        Assert.assertFalse(tester.shouldFire(firstWindow));
        tester.injectElements(1);
        Assert.assertTrue(tester.shouldFire(firstWindow));
        tester.fireIfShouldFire(firstWindow);
        // Merging should re-activate the watermark trigger in the merged window
        tester.mergeWindows();
        // Confirm that we are not on the second trigger by probing
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        tester.injectElements(1);
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        // And confirm that advancing the watermark fires again
        tester.advanceInputWatermark(new Instant(15));
        Assert.assertTrue(tester.shouldFire(mergedWindow));
    }

    /**
     * Tests that if the EOW is finished in both as well as the merged window, then it is finished in
     * the merged result.
     *
     * <p>Because windows are discarded when a trigger finishes, we need to embed this in a sequence
     * in order to check that it is re-activated. So this test is potentially sensitive to other
     * triggers' correctness.
     */
    @Test
    public void testEarlyAndLateOnMergeAlreadyFinished() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(AfterPaneStateMachine.elementCountAtLeast(100)).withLateFirings(AfterPaneStateMachine.elementCountAtLeast(1)), Sessions.withGapDuration(Duration.millis(10)));
        tester.injectElements(1);
        tester.injectElements(5);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(11));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(5), new Instant(15));
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(15));
        // Finish the AfterWatermark.pastEndOfWindow() bit of the trigger in both windows
        tester.advanceInputWatermark(new Instant(15));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        tester.fireIfShouldFire(firstWindow);
        tester.fireIfShouldFire(secondWindow);
        // Confirm that we are on the late trigger by probing
        Assert.assertFalse(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        tester.injectElements(1);
        tester.injectElements(5);
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        tester.fireIfShouldFire(firstWindow);
        tester.fireIfShouldFire(secondWindow);
        // Merging should leave it on the late trigger
        tester.mergeWindows();
        // Confirm that we are on the late trigger by probing
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        tester.injectElements(1);
        Assert.assertTrue(tester.shouldFire(mergedWindow));
    }

    @Test
    public void testEarlyAndLateOnMergeSubtriggerMerges() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(mockEarly).withLateFirings(mockLate), Sessions.withGapDuration(Duration.millis(10)));
        tester.injectElements(1);
        tester.injectElements(5);
        // Merging should re-activate the early trigger in the merged window
        tester.mergeWindows();
        Mockito.verify(mockEarly).onMerge(Mockito.any(OnMergeContext.class));
    }

    /**
     * Tests that the trigger rewinds to be non-finished in the merged window.
     *
     * <p>Because windows are discarded when a trigger finishes, we need to embed this in a sequence
     * in order to check that it is re-activated. So this test is potentially sensitive to other
     * triggers' correctness.
     */
    @Test
    public void testEarlyAndLateOnMergeRewinds() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(AfterPaneStateMachine.elementCountAtLeast(100)).withLateFirings(AfterPaneStateMachine.elementCountAtLeast(1)), Sessions.withGapDuration(Duration.millis(10)));
        tester.injectElements(1);
        tester.injectElements(5);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(11));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(5), new Instant(15));
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(15));
        // Finish the AfterWatermark.pastEndOfWindow() bit of the trigger in only the first window
        tester.advanceInputWatermark(new Instant(11));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        tester.fireIfShouldFire(firstWindow);
        // Confirm that we are on the late trigger by probing
        Assert.assertFalse(tester.shouldFire(firstWindow));
        tester.injectElements(1);
        Assert.assertTrue(tester.shouldFire(firstWindow));
        tester.fireIfShouldFire(firstWindow);
        // Merging should re-activate the early trigger in the merged window
        tester.mergeWindows();
        // Confirm that we are not on the second trigger by probing
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        tester.injectElements(1);
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        // And confirm that advancing the watermark fires again
        tester.advanceInputWatermark(new Instant(15));
        Assert.assertTrue(tester.shouldFire(mergedWindow));
    }

    @Test
    public void testFromEndOfWindowToString() {
        TriggerStateMachine trigger = AfterWatermarkStateMachine.pastEndOfWindow();
        Assert.assertEquals("AfterWatermark.pastEndOfWindow()", trigger.toString());
    }

    @Test
    public void testEarlyFiringsToString() {
        TriggerStateMachine trigger = AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(StubTriggerStateMachine.named("t1"));
        Assert.assertEquals("AfterWatermark.pastEndOfWindow().withEarlyFirings(t1)", trigger.toString());
    }

    @Test
    public void testLateFiringsToString() {
        TriggerStateMachine trigger = AfterWatermarkStateMachine.pastEndOfWindow().withLateFirings(StubTriggerStateMachine.named("t1"));
        Assert.assertEquals("AfterWatermark.pastEndOfWindow().withLateFirings(t1)", trigger.toString());
    }

    @Test
    public void testEarlyAndLateFiringsToString() {
        TriggerStateMachine trigger = AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(StubTriggerStateMachine.named("t1")).withLateFirings(StubTriggerStateMachine.named("t2"));
        Assert.assertEquals("AfterWatermark.pastEndOfWindow().withEarlyFirings(t1).withLateFirings(t2)", trigger.toString());
    }

    @Test
    public void testToStringExcludesNeverTrigger() {
        TriggerStateMachine trigger = AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(NeverStateMachine.ever()).withLateFirings(NeverStateMachine.ever());
        Assert.assertEquals("AfterWatermark.pastEndOfWindow()", trigger.toString());
    }
}

