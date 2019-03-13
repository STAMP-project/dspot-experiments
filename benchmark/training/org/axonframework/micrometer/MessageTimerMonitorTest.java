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
import io.micrometer.core.instrument.MockClock;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleConfig;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.axonframework.monitoring.MessageMonitor;
import org.junit.Assert;
import org.junit.Test;


public class MessageTimerMonitorTest {
    private static final String PROCESSOR_NAME = "processorName";

    @Test
    public void testSuccessMessage() {
        MockClock testClock = new MockClock();
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, testClock);
        MessageTimerMonitor testSubject = MessageTimerMonitor.buildMonitor(MessageTimerMonitorTest.PROCESSOR_NAME, meterRegistry, testClock);
        MessageMonitor.MonitorCallback monitorCallback = testSubject.onMessageIngested(null);
        testClock.addSeconds(1);
        monitorCallback.reportSuccess();
        Timer all = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".allTimer")).timer());
        Timer successTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".successTimer")).timer());
        Timer failureTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".failureTimer")).timer());
        Timer ignoredTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".ignoredTimer")).timer());
        Assert.assertEquals(1, all.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(1, successTimer.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(0, failureTimer.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(0, ignoredTimer.totalTime(TimeUnit.SECONDS), 0);
    }

    @Test
    public void testFailureMessage() {
        MockClock testClock = new MockClock();
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, testClock);
        MessageTimerMonitor testSubject = MessageTimerMonitor.buildMonitor(MessageTimerMonitorTest.PROCESSOR_NAME, meterRegistry, testClock);
        MessageMonitor.MonitorCallback monitorCallback = testSubject.onMessageIngested(null);
        testClock.addSeconds(1);
        monitorCallback.reportFailure(null);
        Timer all = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".allTimer")).timer());
        Timer successTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".successTimer")).timer());
        Timer failureTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".failureTimer")).timer());
        Timer ignoredTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".ignoredTimer")).timer());
        Assert.assertEquals(1, all.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(0, successTimer.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(1, failureTimer.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(0, ignoredTimer.totalTime(TimeUnit.SECONDS), 0);
    }

    @Test
    public void testIgnoredMessage() {
        MockClock testClock = new MockClock();
        SimpleMeterRegistry meterRegistry = new SimpleMeterRegistry(SimpleConfig.DEFAULT, testClock);
        MessageTimerMonitor testSubject = MessageTimerMonitor.buildMonitor(MessageTimerMonitorTest.PROCESSOR_NAME, meterRegistry, testClock);
        MessageMonitor.MonitorCallback monitorCallback = testSubject.onMessageIngested(null);
        testClock.addSeconds(1);
        monitorCallback.reportIgnored();
        Timer all = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".allTimer")).timer());
        Timer successTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".successTimer")).timer());
        Timer failureTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".failureTimer")).timer());
        Timer ignoredTimer = Objects.requireNonNull(meterRegistry.find(((MessageTimerMonitorTest.PROCESSOR_NAME) + ".ignoredTimer")).timer());
        Assert.assertEquals(1, all.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(0, successTimer.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(0, failureTimer.totalTime(TimeUnit.SECONDS), 0);
        Assert.assertEquals(1, ignoredTimer.totalTime(TimeUnit.SECONDS), 0);
    }
}

