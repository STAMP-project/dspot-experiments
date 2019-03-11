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
package org.apache.beam.runners.direct;


import TimeDomain.EVENT_TIME;
import TimeDomain.PROCESSING_TIME;
import TimeDomain.SYNCHRONIZED_PROCESSING_TIME;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.runners.direct.WatermarkManager.TimerUpdate.TimerUpdateBuilder;
import org.apache.beam.runners.direct.WatermarkManager.TransformWatermarks;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link DirectTimerInternals}.
 */
@RunWith(JUnit4.class)
public class DirectTimerInternalsTest {
    private MockClock clock;

    @Mock
    private TransformWatermarks watermarks;

    private TimerUpdateBuilder timerUpdateBuilder;

    private DirectTimerInternals internals;

    @Test
    public void setTimerAddsToBuilder() {
        TimerData eventTimer = TimerData.of(StateNamespaces.global(), new Instant(20145L), EVENT_TIME);
        TimerData processingTimer = TimerData.of(StateNamespaces.global(), new Instant(125555555L), PROCESSING_TIME);
        TimerData synchronizedProcessingTimer = TimerData.of(StateNamespaces.global(), new Instant(98745632189L), SYNCHRONIZED_PROCESSING_TIME);
        internals.setTimer(eventTimer);
        internals.setTimer(processingTimer);
        internals.setTimer(synchronizedProcessingTimer);
        Assert.assertThat(internals.getTimerUpdate().getSetTimers(), Matchers.containsInAnyOrder(eventTimer, synchronizedProcessingTimer, processingTimer));
    }

    @Test
    public void deleteTimerDeletesOnBuilder() {
        TimerData eventTimer = TimerData.of(StateNamespaces.global(), new Instant(20145L), EVENT_TIME);
        TimerData processingTimer = TimerData.of(StateNamespaces.global(), new Instant(125555555L), PROCESSING_TIME);
        TimerData synchronizedProcessingTimer = TimerData.of(StateNamespaces.global(), new Instant(98745632189L), SYNCHRONIZED_PROCESSING_TIME);
        internals.deleteTimer(eventTimer);
        internals.deleteTimer(processingTimer);
        internals.deleteTimer(synchronizedProcessingTimer);
        Assert.assertThat(internals.getTimerUpdate().getDeletedTimers(), Matchers.containsInAnyOrder(eventTimer, synchronizedProcessingTimer, processingTimer));
    }

    @Test
    public void getProcessingTimeIsClockNow() {
        Assert.assertThat(internals.currentProcessingTime(), Matchers.equalTo(clock.now()));
        Instant oldProcessingTime = internals.currentProcessingTime();
        clock.advance(Duration.standardHours(12));
        Assert.assertThat(internals.currentProcessingTime(), Matchers.equalTo(clock.now()));
        Assert.assertThat(internals.currentProcessingTime(), Matchers.equalTo(oldProcessingTime.plus(Duration.standardHours(12))));
    }

    @Test
    public void getSynchronizedProcessingTimeIsWatermarkSynchronizedInputTime() {
        Mockito.when(watermarks.getSynchronizedProcessingInputTime()).thenReturn(new Instant(12345L));
        Assert.assertThat(internals.currentSynchronizedProcessingTime(), Matchers.equalTo(new Instant(12345L)));
    }

    @Test
    public void getInputWatermarkTimeUsesWatermarkTime() {
        Mockito.when(watermarks.getInputWatermark()).thenReturn(new Instant(8765L));
        Assert.assertThat(internals.currentInputWatermarkTime(), Matchers.equalTo(new Instant(8765L)));
    }

    @Test
    public void getOutputWatermarkTimeUsesWatermarkTime() {
        Mockito.when(watermarks.getOutputWatermark()).thenReturn(new Instant(25525L));
        Assert.assertThat(internals.currentOutputWatermarkTime(), Matchers.equalTo(new Instant(25525L)));
    }
}

