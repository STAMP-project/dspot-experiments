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


import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the {@link DefaultTriggerStateMachine}, which should be equivalent to {@code Repeatedly.forever(AfterWatermark.pastEndOfWindow())}.
 */
@RunWith(JUnit4.class)
public class DefaultTriggerStateMachineTest {
    TriggerStateMachineTester.SimpleTriggerStateMachineTester<IntervalWindow> tester;

    @Test
    public void testDefaultTriggerFixedWindows() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(DefaultTriggerStateMachine.of(), FixedWindows.of(Duration.millis(100)));
        // [0, 100)
        tester.injectElements(1, 101);// [100, 200)

        IntervalWindow firstWindow = new IntervalWindow(new Instant(0), new Instant(100));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(100), new Instant(200));
        // Advance the watermark almost to the end of the first window.
        tester.advanceInputWatermark(new Instant(99));
        Assert.assertFalse(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        // Advance watermark past end of the first window, which is then ready
        tester.advanceInputWatermark(new Instant(100));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        // Fire, but the first window is still allowed to fire
        tester.fireIfShouldFire(firstWindow);
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        // Advance watermark to 200, then both are ready
        tester.advanceInputWatermark(new Instant(200));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.isMarkedFinished(firstWindow));
        Assert.assertFalse(tester.isMarkedFinished(secondWindow));
    }

    @Test
    public void testDefaultTriggerSlidingWindows() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(DefaultTriggerStateMachine.of(), SlidingWindows.of(Duration.millis(100)).every(Duration.millis(50)));
        // [-50, 50), [0, 100)
        tester.injectElements(1, 50);// [0, 100), [50, 150)

        IntervalWindow firstWindow = new IntervalWindow(new Instant((-50)), new Instant(50));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(0), new Instant(100));
        IntervalWindow thirdWindow = new IntervalWindow(new Instant(50), new Instant(150));
        Assert.assertFalse(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(thirdWindow));
        // At 50, the first becomes ready; it stays ready after firing
        tester.advanceInputWatermark(new Instant(50));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(thirdWindow));
        tester.fireIfShouldFire(firstWindow);
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(thirdWindow));
        // At 99, the first is still the only one ready
        tester.advanceInputWatermark(new Instant(99));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(thirdWindow));
        // At 100, the first and second are ready
        tester.advanceInputWatermark(new Instant(100));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(thirdWindow));
        tester.fireIfShouldFire(firstWindow);
        Assert.assertFalse(tester.isMarkedFinished(firstWindow));
        Assert.assertFalse(tester.isMarkedFinished(secondWindow));
        Assert.assertFalse(tester.isMarkedFinished(thirdWindow));
    }

    @Test
    public void testDefaultTriggerSessions() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(DefaultTriggerStateMachine.of(), Sessions.withGapDuration(Duration.millis(100)));
        // [1, 101)
        tester.injectElements(1, 50);// [50, 150)

        tester.mergeWindows();
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(101));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(50), new Instant(150));
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(150));
        // Not ready in any window yet
        tester.advanceInputWatermark(new Instant(100));
        Assert.assertFalse(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        // The first window is "ready": the caller owns knowledge of which windows are merged away
        tester.advanceInputWatermark(new Instant(149));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        // Now ready on all windows
        tester.advanceInputWatermark(new Instant(150));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        Assert.assertTrue(tester.shouldFire(secondWindow));
        Assert.assertTrue(tester.shouldFire(mergedWindow));
        // Ensure it repeats
        tester.fireIfShouldFire(mergedWindow);
        Assert.assertTrue(tester.shouldFire(mergedWindow));
        Assert.assertFalse(tester.isMarkedFinished(mergedWindow));
    }
}

