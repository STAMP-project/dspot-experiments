/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flink.runtime.taskexecutor;


import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.executiongraph.ExecutionAttemptID;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link StackTraceSampleService}.
 */
public class StackTraceSampleServiceTest extends TestLogger {
    private ScheduledExecutorService scheduledExecutorService;

    private StackTraceSampleService stackTraceSampleService;

    @Test
    public void testShouldReturnStackTraces() throws Exception {
        final int numSamples = 10;
        final List<StackTraceElement[]> stackTraces = stackTraceSampleService.requestStackTraceSample(new StackTraceSampleServiceTest.TestTask(), numSamples, Time.milliseconds(0), (-1)).get();
        Assert.assertThat(stackTraces, Matchers.hasSize(numSamples));
        final StackTraceElement[] firstStackTrace = stackTraces.get(0);
        Assert.assertThat(firstStackTrace[1].getClassName(), Matchers.is(Matchers.equalTo(StackTraceSampleServiceTest.TestTask.class.getName())));
    }

    @Test
    public void testShouldThrowExceptionIfNumSamplesIsNegative() {
        try {
            stackTraceSampleService.requestStackTraceSample(new StackTraceSampleServiceTest.TestTask(), (-1), Time.milliseconds(0), 10);
            Assert.fail("Expected exception not thrown");
        } catch (final IllegalArgumentException e) {
            Assert.assertThat(e.getMessage(), Matchers.is(Matchers.equalTo("numSamples must be positive")));
        }
    }

    @Test
    public void testShouldTruncateStackTraceIfLimitIsSpecified() throws Exception {
        final int maxStackTraceDepth = 1;
        final List<StackTraceElement[]> stackTraces = stackTraceSampleService.requestStackTraceSample(new StackTraceSampleServiceTest.TestTask(), 10, Time.milliseconds(0), maxStackTraceDepth).get();
        Assert.assertThat(stackTraces.get(0), Matchers.is(Matchers.arrayWithSize(maxStackTraceDepth)));
    }

    @Test
    public void testShouldReturnPartialResultIfTaskStopsRunningDuringSampling() throws Exception {
        final List<StackTraceElement[]> stackTraces = stackTraceSampleService.requestStackTraceSample(new StackTraceSampleServiceTest.NotRunningAfterBeingSampledTask(), 10, Time.milliseconds(0), 1).get();
        Assert.assertThat(stackTraces, Matchers.hasSize(Matchers.lessThan(10)));
    }

    @Test
    public void testShouldThrowExceptionIfTaskIsNotRunningBeforeSampling() {
        try {
            stackTraceSampleService.requestStackTraceSample(new StackTraceSampleServiceTest.NotRunningTask(), 10, Time.milliseconds(0), (-1));
            Assert.fail("Expected exception not thrown");
        } catch (final IllegalStateException e) {
            Assert.assertThat(e.getMessage(), Matchers.containsString("Cannot sample task"));
        }
    }

    /**
     * Task that is always running.
     */
    private static class TestTask implements StackTraceSampleableTask {
        private final ExecutionAttemptID executionAttemptID = new ExecutionAttemptID();

        @Override
        public boolean isRunning() {
            return true;
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            return Thread.currentThread().getStackTrace();
        }

        @Override
        public ExecutionAttemptID getExecutionId() {
            return executionAttemptID;
        }
    }

    /**
     * Task that stops running after being sampled for the first time.
     */
    private static class NotRunningAfterBeingSampledTask extends StackTraceSampleServiceTest.TestTask {
        private volatile boolean stackTraceSampled;

        @Override
        public boolean isRunning() {
            return !(stackTraceSampled);
        }

        @Override
        public StackTraceElement[] getStackTrace() {
            stackTraceSampled = true;
            return super.getStackTrace();
        }
    }

    /**
     * Task that never runs.
     */
    private static class NotRunningTask extends StackTraceSampleServiceTest.TestTask {
        @Override
        public boolean isRunning() {
            return false;
        }
    }
}

