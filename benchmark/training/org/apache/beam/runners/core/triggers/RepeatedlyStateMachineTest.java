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
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.transforms.windowing.GlobalWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
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
 * Tests for {@link RepeatedlyStateMachine}.
 */
@RunWith(JUnit4.class)
public class RepeatedlyStateMachineTest {
    @Mock
    private TriggerStateMachine mockTrigger;

    private TriggerStateMachineTester.SimpleTriggerStateMachineTester<IntervalWindow> tester;

    /**
     * Tests that onElement correctly passes the data on to the subtrigger.
     */
    @Test
    public void testOnElement() throws Exception {
        setUp(FixedWindows.of(Duration.millis(10)));
        tester.injectElements(37);
        Mockito.verify(mockTrigger).onElement(Mockito.any());
    }

    /**
     * Tests that the repeatedly is ready to fire whenever the subtrigger is ready.
     */
    @Test
    public void testShouldFire() throws Exception {
        setUp(FixedWindows.of(Duration.millis(10)));
        Mockito.when(mockTrigger.shouldFire(RepeatedlyStateMachineTest.anyTriggerContext())).thenReturn(true);
        Assert.assertTrue(tester.shouldFire(new IntervalWindow(new Instant(0), new Instant(10))));
        Mockito.when(mockTrigger.shouldFire(Mockito.any())).thenReturn(false);
        Assert.assertFalse(tester.shouldFire(new IntervalWindow(new Instant(0), new Instant(10))));
    }

    @Test
    public void testShouldFireAfterMerge() throws Exception {
        tester = TriggerStateMachineTester.forTrigger(RepeatedlyStateMachine.forever(AfterPaneStateMachine.elementCountAtLeast(2)), Sessions.withGapDuration(Duration.millis(10)));
        tester.injectElements(1);
        IntervalWindow firstWindow = new IntervalWindow(new Instant(1), new Instant(11));
        Assert.assertFalse(tester.shouldFire(firstWindow));
        tester.injectElements(5);
        IntervalWindow secondWindow = new IntervalWindow(new Instant(5), new Instant(15));
        Assert.assertFalse(tester.shouldFire(secondWindow));
        // Merge them, if the merged window were on the second trigger, it would be ready
        tester.mergeWindows();
        IntervalWindow mergedWindow = new IntervalWindow(new Instant(1), new Instant(15));
        Assert.assertTrue(tester.shouldFire(mergedWindow));
    }

    @Test
    public void testRepeatedlyAfterFirstElementCount() throws Exception {
        TriggerStateMachineTester.SimpleTriggerStateMachineTester<GlobalWindow> tester = TriggerStateMachineTester.forTrigger(RepeatedlyStateMachine.forever(AfterFirstStateMachine.of(AfterProcessingTimeStateMachine.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(15)), AfterPaneStateMachine.elementCountAtLeast(5))), new GlobalWindows());
        GlobalWindow window = GlobalWindow.INSTANCE;
        tester.injectElements(1);
        Assert.assertFalse(tester.shouldFire(window));
        tester.injectElements(2, 3, 4, 5);
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.shouldFire(window));
    }

    @Test
    public void testRepeatedlyAfterFirstProcessingTime() throws Exception {
        TriggerStateMachineTester.SimpleTriggerStateMachineTester<GlobalWindow> tester = TriggerStateMachineTester.forTrigger(RepeatedlyStateMachine.forever(AfterFirstStateMachine.of(AfterProcessingTimeStateMachine.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(15)), AfterPaneStateMachine.elementCountAtLeast(5))), new GlobalWindows());
        GlobalWindow window = GlobalWindow.INSTANCE;
        tester.injectElements(1);
        Assert.assertFalse(tester.shouldFire(window));
        tester.advanceProcessingTime(new Instant(0).plus(Duration.standardMinutes(15)));
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.shouldFire(window));
    }

    @Test
    public void testRepeatedlyElementCount() throws Exception {
        TriggerStateMachineTester.SimpleTriggerStateMachineTester<GlobalWindow> tester = TriggerStateMachineTester.forTrigger(RepeatedlyStateMachine.forever(AfterPaneStateMachine.elementCountAtLeast(5)), new GlobalWindows());
        GlobalWindow window = GlobalWindow.INSTANCE;
        tester.injectElements(1);
        Assert.assertFalse(tester.shouldFire(window));
        tester.injectElements(2, 3, 4, 5);
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.shouldFire(window));
    }

    @Test
    public void testRepeatedlyProcessingTime() throws Exception {
        TriggerStateMachineTester.SimpleTriggerStateMachineTester<GlobalWindow> tester = TriggerStateMachineTester.forTrigger(RepeatedlyStateMachine.forever(AfterProcessingTimeStateMachine.pastFirstElementInPane().plusDelayOf(Duration.standardMinutes(15))), new GlobalWindows());
        GlobalWindow window = GlobalWindow.INSTANCE;
        tester.injectElements(1);
        Assert.assertFalse(tester.shouldFire(window));
        tester.advanceProcessingTime(new Instant(0).plus(Duration.standardMinutes(15)));
        Assert.assertTrue(tester.shouldFire(window));
        tester.fireIfShouldFire(window);
        Assert.assertFalse(tester.shouldFire(window));
    }

    @Test
    public void testToString() {
        TriggerStateMachine trigger = RepeatedlyStateMachine.forever(new StubTriggerStateMachine() {
            @Override
            public String toString() {
                return "innerTrigger";
            }
        });
        Assert.assertEquals("Repeatedly.forever(innerTrigger)", trigger.toString());
    }
}

