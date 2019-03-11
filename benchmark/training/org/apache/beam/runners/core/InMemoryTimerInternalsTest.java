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
package org.apache.beam.runners.core;


import TimeDomain.EVENT_TIME;
import TimeDomain.PROCESSING_TIME;
import TimeDomain.SYNCHRONIZED_PROCESSING_TIME;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link InMemoryTimerInternals}.
 */
@RunWith(JUnit4.class)
public class InMemoryTimerInternalsTest {
    private static final StateNamespace NS1 = new StateNamespaceForTest("NS1");

    private static final String ID1 = "id1";

    private static final String ID2 = "id2";

    @Test
    public void testFiringEventTimers() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        TimerData eventTimer1 = TimerData.of(InMemoryTimerInternalsTest.ID1, InMemoryTimerInternalsTest.NS1, new Instant(19), EVENT_TIME);
        TimerData eventTimer2 = TimerData.of(InMemoryTimerInternalsTest.ID2, InMemoryTimerInternalsTest.NS1, new Instant(29), EVENT_TIME);
        underTest.setTimer(eventTimer1);
        underTest.setTimer(eventTimer2);
        underTest.advanceInputWatermark(new Instant(20));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(eventTimer1));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
        // Advancing just a little shouldn't refire
        underTest.advanceInputWatermark(new Instant(21));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
        // Adding the timer and advancing a little should refire
        underTest.setTimer(eventTimer1);
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(eventTimer1));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
        // And advancing the rest of the way should still have the other timer
        underTest.advanceInputWatermark(new Instant(30));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(eventTimer2));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
    }

    @Test
    public void testResetById() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        Instant earlyTimestamp = new Instant(13);
        Instant laterTimestamp = new Instant(42);
        underTest.advanceInputWatermark(new Instant(0));
        underTest.setTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1, earlyTimestamp, EVENT_TIME);
        underTest.setTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1, laterTimestamp, EVENT_TIME);
        underTest.advanceInputWatermark(earlyTimestamp.plus(1L));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
        underTest.advanceInputWatermark(laterTimestamp.plus(1L));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(TimerData.of(InMemoryTimerInternalsTest.ID1, InMemoryTimerInternalsTest.NS1, laterTimestamp, EVENT_TIME)));
    }

    @Test
    public void testDeletionIdempotent() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        Instant timestamp = new Instant(42);
        underTest.setTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1, timestamp, EVENT_TIME);
        underTest.deleteTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1);
        underTest.deleteTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1);
    }

    @Test
    public void testDeletionById() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        Instant timestamp = new Instant(42);
        underTest.advanceInputWatermark(new Instant(0));
        underTest.setTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1, timestamp, EVENT_TIME);
        underTest.deleteTimer(InMemoryTimerInternalsTest.NS1, InMemoryTimerInternalsTest.ID1);
        underTest.advanceInputWatermark(new Instant(43));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
    }

    @Test
    public void testFiringProcessingTimeTimers() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        TimerData processingTime1 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(19), PROCESSING_TIME);
        TimerData processingTime2 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(29), PROCESSING_TIME);
        underTest.setTimer(processingTime1);
        underTest.setTimer(processingTime2);
        underTest.advanceProcessingTime(new Instant(20));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.equalTo(processingTime1));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
        // Advancing just a little shouldn't refire
        underTest.advanceProcessingTime(new Instant(21));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
        // Adding the timer and advancing a little should fire again
        underTest.setTimer(processingTime1);
        underTest.advanceProcessingTime(new Instant(21));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.equalTo(processingTime1));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
        // And advancing the rest of the way should still have the other timer
        underTest.advanceProcessingTime(new Instant(30));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.equalTo(processingTime2));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
    }

    @Test
    public void testTimerOrdering() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        TimerData eventTime1 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(19), EVENT_TIME);
        TimerData processingTime1 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(19), PROCESSING_TIME);
        TimerData synchronizedProcessingTime1 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(19), SYNCHRONIZED_PROCESSING_TIME);
        TimerData eventTime2 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(29), EVENT_TIME);
        TimerData processingTime2 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(29), PROCESSING_TIME);
        TimerData synchronizedProcessingTime2 = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(29), SYNCHRONIZED_PROCESSING_TIME);
        underTest.setTimer(processingTime1);
        underTest.setTimer(eventTime1);
        underTest.setTimer(synchronizedProcessingTime1);
        underTest.setTimer(processingTime2);
        underTest.setTimer(eventTime2);
        underTest.setTimer(synchronizedProcessingTime2);
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
        underTest.advanceInputWatermark(new Instant(30));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(eventTime1));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(eventTime2));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
        underTest.advanceProcessingTime(new Instant(30));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.equalTo(processingTime1));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.equalTo(processingTime2));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
        Assert.assertThat(underTest.removeNextSynchronizedProcessingTimer(), Matchers.nullValue());
        underTest.advanceSynchronizedProcessingTime(new Instant(30));
        Assert.assertThat(underTest.removeNextSynchronizedProcessingTimer(), Matchers.equalTo(synchronizedProcessingTime1));
        Assert.assertThat(underTest.removeNextSynchronizedProcessingTimer(), Matchers.equalTo(synchronizedProcessingTime2));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
    }

    @Test
    public void testDeduplicate() throws Exception {
        InMemoryTimerInternals underTest = new InMemoryTimerInternals();
        TimerData eventTime = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(19), EVENT_TIME);
        TimerData processingTime = TimerData.of(InMemoryTimerInternalsTest.NS1, new Instant(19), PROCESSING_TIME);
        underTest.setTimer(eventTime);
        underTest.setTimer(eventTime);
        underTest.setTimer(processingTime);
        underTest.setTimer(processingTime);
        underTest.advanceProcessingTime(new Instant(20));
        underTest.advanceInputWatermark(new Instant(20));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.equalTo(processingTime));
        Assert.assertThat(underTest.removeNextProcessingTimer(), Matchers.nullValue());
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.equalTo(eventTime));
        Assert.assertThat(underTest.removeNextEventTimer(), Matchers.nullValue());
    }
}

