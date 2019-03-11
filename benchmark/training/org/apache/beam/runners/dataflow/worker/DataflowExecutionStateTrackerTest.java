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
package org.apache.beam.runners.dataflow.worker;


import java.io.Closeable;
import java.io.IOException;
import org.apache.beam.runners.core.metrics.ExecutionStateSampler;
import org.apache.beam.runners.core.metrics.ExecutionStateTracker;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.sdk.options.PipelineOptions;
import org.hamcrest.Matchers;
import org.joda.time.DateTimeUtils.MillisProvider;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link DataflowExecutionStateTrackerTest}.
 */
public class DataflowExecutionStateTrackerTest {
    private PipelineOptions options;

    private MillisProvider clock;

    private ExecutionStateSampler sampler;

    private CounterSet counterSet;

    private final NameContext step1 = NameContext.create("stage", "originalStep1", "systemStep1", "userStep1");

    private final TestOperationContext.TestDataflowExecutionState step1Process = new TestOperationContext.TestDataflowExecutionState(step1, ExecutionStateTracker.PROCESS_STATE_NAME);

    @Test
    public void testReportsElementExecutionTime() throws IOException {
        enableTimePerElementExperiment();
        ExecutionStateTracker tracker = createTracker();
        try (Closeable c1 = tracker.activate(new Thread())) {
            try (Closeable c2 = tracker.enterState(step1Process)) {
            }
            sampler.doSampling(30);
            // Execution time split evenly between executions: IDLE, step1, IDLE
        }
        assertElementProcessingTimeCounter(step1, 10, 4);
    }

    /**
     * {@link DataflowExecutionStateTrackerTest} should take one last sample when a tracker is
     * deactivated.
     */
    @Test
    public void testTakesSampleOnDeactivate() throws IOException {
        enableTimePerElementExperiment();
        ExecutionStateTracker tracker = createTracker();
        try (Closeable c1 = tracker.activate(new Thread())) {
            try (Closeable c2 = tracker.enterState(step1Process)) {
                sampler.doSampling(100);
                Assert.assertThat(step1Process.getTotalMillis(), Matchers.equalTo(100L));
            }
        }
        sampler.doSampling(100);
        Assert.assertThat(step1Process.getTotalMillis(), Matchers.equalTo(100L));
    }
}

