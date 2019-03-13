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


import AfterWatermarkStateMachine.AfterWatermarkEarlyAndLate;
import AfterWatermarkStateMachine.FromEndOfWindow;
import RunnerApi.TimestampTransform;
import RunnerApi.TimestampTransform.AlignTo;
import RunnerApi.TimestampTransform.Delay;
import RunnerApi.Trigger;
import RunnerApi.Trigger.AfterAll;
import RunnerApi.Trigger.AfterAny;
import RunnerApi.Trigger.AfterEach;
import RunnerApi.Trigger.AfterEndOfWindow;
import RunnerApi.Trigger.AfterProcessingTime;
import RunnerApi.Trigger.Default;
import RunnerApi.Trigger.ElementCount;
import RunnerApi.Trigger.Never;
import RunnerApi.Trigger.OrFinally;
import RunnerApi.Trigger.Repeat;
import TimeDomain.PROCESSING_TIME;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests the {@link TriggerStateMachines} static utility methods.
 */
@RunWith(JUnit4.class)
public class TriggerStateMachinesTest {
    // 
    // Tests for leaf trigger translation
    // 
    @Test
    public void testStateMachineForAfterPane() {
        int count = 37;
        RunnerApi.Trigger trigger = Trigger.newBuilder().setElementCount(ElementCount.newBuilder().setElementCount(count)).build();
        AfterPaneStateMachine machine = ((AfterPaneStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine.getElementCount(), Matchers.equalTo(trigger.getElementCount().getElementCount()));
    }

    // TODO: make these all build the proto
    @Test
    public void testStateMachineForAfterProcessingTime() {
        Duration minutes = Duration.standardMinutes(94);
        Duration hours = Duration.standardHours(13);
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterProcessingTime(AfterProcessingTime.newBuilder().addTimestampTransforms(TimestampTransform.newBuilder().setDelay(Delay.newBuilder().setDelayMillis(minutes.getMillis()))).addTimestampTransforms(TimestampTransform.newBuilder().setAlignTo(AlignTo.newBuilder().setPeriod(hours.getMillis())))).build();
        AfterDelayFromFirstElementStateMachine machine = ((AfterDelayFromFirstElementStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine.getTimeDomain(), Matchers.equalTo(PROCESSING_TIME));
    }

    @Test
    public void testStateMachineForAfterWatermark() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterEndOfWindow(AfterEndOfWindow.getDefaultInstance()).build();
        AfterWatermarkStateMachine.FromEndOfWindow machine = ((AfterWatermarkStateMachine.FromEndOfWindow) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(TriggerStateMachines.stateMachineForTrigger(trigger), Matchers.instanceOf(FromEndOfWindow.class));
    }

    @Test
    public void testDefaultTriggerTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setDefault(Default.getDefaultInstance()).build();
        Assert.assertThat(TriggerStateMachines.stateMachineForTrigger(trigger), Matchers.instanceOf(DefaultTriggerStateMachine.class));
    }

    @Test
    public void testNeverTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setNever(Never.getDefaultInstance()).build();
        NeverStateMachine machine = ((NeverStateMachine) (checkNotNull(TriggerStateMachines.stateMachineForTrigger(trigger))));
        // No parameters, so if it doesn't crash, we win!
    }

    // 
    // Tests for composite trigger translation
    // 
    // These check just that translation was invoked recursively using somewhat random
    // leaf subtriggers; by induction it all holds together. Beyond this, explicit tests
    // of particular triggers will suffice.
    private static final int ELEM_COUNT = 472;

    private static final Duration DELAY = Duration.standardSeconds(95673);

    private final Trigger subtrigger1 = Trigger.newBuilder().setElementCount(ElementCount.newBuilder().setElementCount(TriggerStateMachinesTest.ELEM_COUNT)).build();

    private final Trigger subtrigger2 = Trigger.newBuilder().setAfterProcessingTime(AfterProcessingTime.newBuilder().addTimestampTransforms(TimestampTransform.newBuilder().setDelay(Delay.newBuilder().setDelayMillis(TriggerStateMachinesTest.DELAY.getMillis())))).build();

    private final TriggerStateMachine submachine1 = TriggerStateMachines.stateMachineForTrigger(subtrigger1);

    private final TriggerStateMachine submachine2 = TriggerStateMachines.stateMachineForTrigger(subtrigger2);

    @Test
    public void testAfterEachTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterEach(AfterEach.newBuilder().addSubtriggers(subtrigger1).addSubtriggers(subtrigger2)).build();
        AfterEachStateMachine machine = ((AfterEachStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(AfterEachStateMachine.inOrder(submachine1, submachine2)));
    }

    @Test
    public void testAfterFirstTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterAny(AfterAny.newBuilder().addSubtriggers(subtrigger1).addSubtriggers(subtrigger2)).build();
        AfterFirstStateMachine machine = ((AfterFirstStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(AfterFirstStateMachine.of(submachine1, submachine2)));
    }

    @Test
    public void testAfterAllTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterAll(AfterAll.newBuilder().addSubtriggers(subtrigger1).addSubtriggers(subtrigger2)).build();
        AfterAllStateMachine machine = ((AfterAllStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(AfterAllStateMachine.of(submachine1, submachine2)));
    }

    @Test
    public void testAfterWatermarkEarlyTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterEndOfWindow(AfterEndOfWindow.newBuilder().setEarlyFirings(subtrigger1)).build();
        AfterWatermarkStateMachine.AfterWatermarkEarlyAndLate machine = ((AfterWatermarkStateMachine.AfterWatermarkEarlyAndLate) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(submachine1)));
    }

    @Test
    public void testAfterWatermarkEarlyLateTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setAfterEndOfWindow(AfterEndOfWindow.newBuilder().setEarlyFirings(subtrigger1).setLateFirings(subtrigger2)).build();
        AfterWatermarkStateMachine.AfterWatermarkEarlyAndLate machine = ((AfterWatermarkStateMachine.AfterWatermarkEarlyAndLate) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(AfterWatermarkStateMachine.pastEndOfWindow().withEarlyFirings(submachine1).withLateFirings(submachine2)));
    }

    @Test
    public void testOrFinallyTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setOrFinally(OrFinally.newBuilder().setMain(subtrigger1).setFinally(subtrigger2)).build();
        OrFinallyStateMachine machine = ((OrFinallyStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(submachine1.orFinally(submachine2)));
    }

    @Test
    public void testRepeatedlyTranslation() {
        RunnerApi.Trigger trigger = Trigger.newBuilder().setRepeat(Repeat.newBuilder().setSubtrigger(subtrigger1)).build();
        RepeatedlyStateMachine machine = ((RepeatedlyStateMachine) (TriggerStateMachines.stateMachineForTrigger(trigger)));
        Assert.assertThat(machine, Matchers.equalTo(RepeatedlyStateMachine.forever(submachine1)));
    }
}

