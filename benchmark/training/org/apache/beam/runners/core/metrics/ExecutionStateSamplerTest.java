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
package org.apache.beam.runners.core.metrics;


import java.io.Closeable;
import java.util.concurrent.TimeUnit;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker.ExecutionState;
import org.hamcrest.Matchers;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link org.apache.beam.runners.core.metrics.ExecutionStateSampler}.
 */
public class ExecutionStateSamplerTest {
    private MillisProvider clock;

    private ExecutionStateSampler sampler;

    private static class TestExecutionState extends ExecutionState {
        private long totalMillis = 0;

        private boolean lullReported = false;

        public TestExecutionState(String stateName) {
            super(stateName);
        }

        @Override
        public void takeSample(long millisSinceLastSample) {
            totalMillis += millisSinceLastSample;
        }

        @Override
        public void reportLull(Thread trackedThread, long millis) {
            lullReported = true;
        }
    }

    private final ExecutionStateSamplerTest.TestExecutionState step1act1 = new ExecutionStateSamplerTest.TestExecutionState("activity1");

    private final ExecutionStateSamplerTest.TestExecutionState step1act2 = new ExecutionStateSamplerTest.TestExecutionState("activity2");

    private final ExecutionStateSamplerTest.TestExecutionState step2act1 = new ExecutionStateSamplerTest.TestExecutionState("activity1");

    @Test
    public void testOneThreadSampling() throws Exception {
        ExecutionStateTracker tracker = createTracker();
        try (Closeable c1 = tracker.activate(new Thread())) {
            try (Closeable c2 = tracker.enterState(step1act1)) {
                sampler.doSampling(400);
                Assert.assertThat(step1act1.totalMillis, Matchers.equalTo(400L));
                sampler.doSampling(200);
                Assert.assertThat(step1act1.totalMillis, Matchers.equalTo((400L + 200L)));
            }
            sampler.doSampling(300);// no current state

            Assert.assertThat(step1act1.totalMillis, Matchers.equalTo((400L + 200L)));
            Assert.assertThat(step1act1.lullReported, Matchers.equalTo(false));
        }
    }

    @Test
    public void testMultipleThreads() throws Exception {
        ExecutionStateTracker tracker1 = createTracker();
        ExecutionStateTracker tracker2 = createTracker();
        try (Closeable t1 = tracker1.activate(new Thread())) {
            try (Closeable t2 = tracker2.activate(new Thread())) {
                Closeable c1 = tracker1.enterState(step1act1);
                sampler.doSampling(101);
                Closeable c2 = tracker2.enterState(step2act1);
                sampler.doSampling(102);
                Closeable c3 = tracker1.enterState(step1act2);
                sampler.doSampling(203);
                c3.close();
                sampler.doSampling(104);
                c1.close();
                sampler.doSampling(105);
                c2.close();
            }
        }
        Assert.assertThat(step1act1.totalMillis, Matchers.equalTo(((101L + 102L) + 104L)));
        Assert.assertThat(step1act2.totalMillis, Matchers.equalTo(203L));
        Assert.assertThat(step2act1.totalMillis, Matchers.equalTo((((102L + 203L) + 104L) + 105L)));
        Assert.assertThat(step1act1.lullReported, Matchers.equalTo(false));
        Assert.assertThat(step1act2.lullReported, Matchers.equalTo(false));
        Assert.assertThat(step2act1.lullReported, Matchers.equalTo(false));
    }

    @Test
    public void testLullDetectionOccurs() throws Exception {
        ExecutionStateTracker tracker1 = createTracker();
        try (Closeable t1 = tracker1.activate(new Thread())) {
            try (Closeable c = tracker1.enterState(step1act1)) {
                sampler.doSampling(TimeUnit.MINUTES.toMillis(6));
            }
        }
        Assert.assertThat(step1act1.lullReported, Matchers.equalTo(true));
    }
}

