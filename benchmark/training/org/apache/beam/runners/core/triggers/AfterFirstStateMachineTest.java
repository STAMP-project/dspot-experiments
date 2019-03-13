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
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link AfterFirstStateMachine}.
 */
@RunWith(JUnit4.class)
public class AfterFirstStateMachineTest {
    @Mock
    private TriggerStateMachine mockTrigger1;

    @Mock
    private TriggerStateMachine mockTrigger2;

    private TriggerStateMachineTester.SimpleTriggerStateMachineTester<IntervalWindow> tester;

    @Test
    public void testNeitherShouldFireFixedWindows() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterFirstStateMachine.of(mockTrigger1, mockTrigger2), FixedWindows.of(Duration.millis(10)));
        tester.injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(10));
        Mockito.when(mockTrigger1.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(false);
        Mockito.when(mockTrigger2.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(false);
        Assert.assertFalse(tester.shouldFire(window));// should not fire

        Assert.assertFalse(tester.isMarkedFinished(window));// not finished

    }

    @Test
    public void testOnlyT1ShouldFireFixedWindows() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterFirstStateMachine.of(mockTrigger1, mockTrigger2), FixedWindows.of(Duration.millis(10)));
        tester.injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(1), new Instant(11));
        Mockito.when(mockTrigger1.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(true);
        Mockito.when(mockTrigger2.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(false);
        Assert.assertTrue(tester.shouldFire(window));// should fire

        tester.fireIfShouldFire(window);
        Assert.assertTrue(tester.isMarkedFinished(window));
    }

    @Test
    public void testOnlyT2ShouldFireFixedWindows() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterFirstStateMachine.of(mockTrigger1, mockTrigger2), FixedWindows.of(Duration.millis(10)));
        tester.injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(1), new Instant(11));
        Mockito.when(mockTrigger1.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(false);
        Mockito.when(mockTrigger2.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(true);
        Assert.assertTrue(tester.shouldFire(window));// should fire

        tester.fireIfShouldFire(window);// now finished

        Assert.assertTrue(tester.isMarkedFinished(window));
    }

    @Test
    public void testBothShouldFireFixedWindows() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterFirstStateMachine.of(mockTrigger1, mockTrigger2), FixedWindows.of(Duration.millis(10)));
        tester.injectElements(1);
        IntervalWindow window = new IntervalWindow(new Instant(1), new Instant(11));
        Mockito.when(mockTrigger1.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(true);
        Mockito.when(mockTrigger2.shouldFire(AfterFirstStateMachineTest.anyTriggerContext())).thenReturn(true);
        Assert.assertTrue(tester.shouldFire(window));// should fire

        tester.fireIfShouldFire(window);
        Assert.assertTrue(tester.isMarkedFinished(window));
    }

    /**
     * Tests that if the first trigger rewinds to be non-finished in the merged window, then it
     * becomes the currently active trigger again, with real triggers.
     */
    @Test
    public void testShouldFireAfterMerge() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(AfterEachStateMachine.inOrder(AfterFirstStateMachine.of(AfterPaneStateMachine.elementCountAtLeast(5), AfterWatermarkStateMachine.pastEndOfWindow()), RepeatedlyStateMachine.forever(AfterPaneStateMachine.elementCountAtLeast(1))), Sessions.withGapDuration(Duration.millis(10)));
        // Finished the AfterFirst in the first window
        tester.injectElements(1);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(11));
        Assert.assertFalse(tester.shouldFire(firstWindow));
        tester.advanceInputWatermark(new Instant(11));
        Assert.assertTrue(tester.shouldFire(firstWindow));
        tester.fireIfShouldFire(firstWindow);
        // Set up second window where it is not done
        tester.injectElements(5);
        IntervalWindow secondWindow = new IntervalWindow(new Instant(5), new Instant(15));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        // Merge them, if the merged window were on the second trigger, it would be ready
        tester.mergeWindows();
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(15));
        Assert.assertFalse(tester.shouldFire(mergedWindow));
        // Now adding 3 more makes the AfterFirst ready to fire
        tester.injectElements(1, 2, 3, 4, 5);
        tester.mergeWindows();
        Assert.assertTrue(tester.shouldFire(mergedWindow));
    }
}

