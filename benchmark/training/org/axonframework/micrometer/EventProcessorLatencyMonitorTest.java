/**
 * Copyright (c) 2010-2019. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.micrometer;


import MessageMonitor.MonitorCallback;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.monitoring.MessageMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
public class EventProcessorLatencyMonitorTest {
    private static final String METER_NAME_PREFIX = "processor";

    private MeterRegistry meterRegistry;

    @Test
    public void testMessages() {
        EventProcessorLatencyMonitor testSubject = EventProcessorLatencyMonitor.buildMonitor(EventProcessorLatencyMonitorTest.METER_NAME_PREFIX, meterRegistry);
        EventMessage<?> firstEventMessage = Mockito.mock(EventMessage.class);
        Mockito.when(firstEventMessage.getTimestamp()).thenReturn(Instant.ofEpochMilli(0));
        EventMessage<?> secondEventMessage = Mockito.mock(EventMessage.class);
        Mockito.when(secondEventMessage.getTimestamp()).thenReturn(Instant.ofEpochMilli(1000));
        Map<? super EventMessage<?>, MessageMonitor.MonitorCallback> callbacks = testSubject.onMessagesIngested(Arrays.asList(firstEventMessage, secondEventMessage));
        callbacks.get(firstEventMessage).reportSuccess();
        Gauge latencyGauge = Objects.requireNonNull(meterRegistry.find(((EventProcessorLatencyMonitorTest.METER_NAME_PREFIX) + ".latency")).gauge());
        Assert.assertEquals(1000, latencyGauge.value(), 0);
    }

    @Test
    public void testFailureMessage() {
        EventProcessorLatencyMonitor testSubject = EventProcessorLatencyMonitor.buildMonitor(EventProcessorLatencyMonitorTest.METER_NAME_PREFIX, meterRegistry);
        EventMessage<?> firstEventMessage = Mockito.mock(EventMessage.class);
        Mockito.when(firstEventMessage.getTimestamp()).thenReturn(Instant.ofEpochMilli(0));
        EventMessage<?> secondEventMessage = Mockito.mock(EventMessage.class);
        Mockito.when(secondEventMessage.getTimestamp()).thenReturn(Instant.ofEpochMilli(1000));
        Map<? super EventMessage<?>, MessageMonitor.MonitorCallback> callbacks = testSubject.onMessagesIngested(Arrays.asList(firstEventMessage, secondEventMessage));
        callbacks.get(firstEventMessage).reportFailure(null);
        Gauge latencyGauge = Objects.requireNonNull(meterRegistry.find(((EventProcessorLatencyMonitorTest.METER_NAME_PREFIX) + ".latency")).gauge());
        Assert.assertEquals(1000, latencyGauge.value(), 0);
    }

    @Test
    public void testNullMessage() {
        EventProcessorLatencyMonitor testSubject = EventProcessorLatencyMonitor.buildMonitor(EventProcessorLatencyMonitorTest.METER_NAME_PREFIX, meterRegistry);
        MessageMonitor.MonitorCallback monitorCallback = testSubject.onMessageIngested(null);
        monitorCallback.reportSuccess();
        Gauge latencyGauge = Objects.requireNonNull(meterRegistry.find(((EventProcessorLatencyMonitorTest.METER_NAME_PREFIX) + ".latency")).gauge());
        Assert.assertEquals(0, latencyGauge.value(), 0);
    }
}

