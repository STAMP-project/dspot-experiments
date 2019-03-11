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


import DataflowElementExecutionTracker.TIME_PER_ELEMENT_EXPERIMENT;
import java.io.IOException;
import java.util.List;
import org.apache.beam.runners.dataflow.options.DataflowPipelineDebugOptions;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ElementExecutionTracker;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DataflowElementExecutionTracker}.
 */
@RunWith(JUnit4.class)
public class DataflowElementExecutionTrackerTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private CounterSet counters;

    private DataflowPipelineDebugOptions options;

    private ElementExecutionTracker tracker;

    /**
     * Typical usage scenario.
     */
    @Test
    public void testTypicalUsage() throws IOException {
        NameContext stepA = createStep("A");
        NameContext stepB = createStep("B");
        NameContext stepC = createStep("C");
        NameContext stepD = createStep("D");
        // Comments track journal of executions for next sample, and partial timings not yet reported
        tracker.enter(stepA);// IDLE A1 | {}

        tracker.enter(stepB);// IDLE A1 B1 | {}

        tracker.exit();// IDLE A1 B1 A1 | {}

        tracker.takeSample(40);// A1 | {A1:2}

        Assert.assertThat(getCounterValue(stepB), Matchers.equalTo(distribution(10)));
        tracker.enter(stepB);// A1 B2 | {A1:2}

        tracker.exit();// A1 B2 A1 | {A1:2}

        tracker.enter(stepC);// A1 B2 A1 C1 | {A1:2}

        tracker.enter(stepD);// A1 B2 A1 C1 D1 | {A1:2}

        tracker.takeSample(50);// D1 | {A1:4 C1:1 D1:1}

        Assert.assertThat(getCounterValue(stepB), Matchers.equalTo(distribution(10, 10)));
        tracker.exit();// D1 C1 | {A1:4 C1:1 D1:1}

        tracker.exit();// D1 C1 A1 | {A1:4 C1:1 D1:1}

        tracker.enter(stepC);// D1 C1 A1 C2 | {A1:4 C1:1 D1:1}

        tracker.takeSample(40);// C2 | {A1:5 C2:1}

        Assert.assertThat(getCounterValue(stepC), Matchers.equalTo(distribution(20)));
        Assert.assertThat(getCounterValue(stepD), Matchers.equalTo(distribution(20)));
        tracker.exit();// C2 A1 | {A1:5 C2:1}

        tracker.exit();// C2 A1 IDLE | {A1:5 C2:1}

        tracker.takeSample(30);// done

        Assert.assertThat(getCounterValue(stepA), Matchers.equalTo(distribution(60)));
        Assert.assertThat(getCounterValue(stepB), Matchers.equalTo(distribution(10, 10)));
        Assert.assertThat(getCounterValue(stepC), Matchers.equalTo(distribution(20, 20)));
        Assert.assertThat(getCounterValue(stepD), Matchers.equalTo(distribution(20)));
    }

    /**
     * Test that counter values are reported when a processing operation finishes.
     */
    @Test
    public void testCounterReportedOnClose() throws IOException {
        NameContext step = createStep("A");
        tracker.enter(step);
        tracker.takeSample(10);// half of time attributed to initial IDLE execution

        Assert.assertThat(getCounter(step), Matchers.nullValue());
        tracker.exit();
        tracker.takeSample(10);// half of time attributed to final IDLE execution

        Assert.assertThat(getCounterValue(step), Matchers.equalTo(distribution(10)));
    }

    /**
     * Ensure functionality is correctly disabled when the experiment is not set.
     */
    @Test
    public void testDisabledByExperiment() throws IOException {
        List<String> experiments = options.getExperiments();
        experiments.remove(TIME_PER_ELEMENT_EXPERIMENT);
        options.setExperiments(experiments);
        tracker = DataflowElementExecutionTracker.create(counters, options);
        NameContext step = createStep("A");
        tracker.enter(step);
        tracker.exit();
        tracker.takeSample(10);
        Assert.assertThat(getCounter(step), Matchers.nullValue());
    }

    /**
     * Test that the sampling time is distributed evenly between all execution fragments since last
     * sampling.
     */
    @Test
    public void testSampledTimeDistributedBetweenExecutionFragments() throws IOException {
        NameContext stepA = createStep("A");
        NameContext stepB = createStep("B");
        tracker.enter(stepA);
        tracker.exit();
        tracker.enter(stepB);
        tracker.exit();
        // Expected journal: IDLE A1 IDLE B1 IDLE
        tracker.takeSample(50);
        Assert.assertThat(getCounterValue(stepA), Matchers.equalTo(distribution(10)));
        Assert.assertThat(getCounterValue(stepB), Matchers.equalTo(distribution(10)));
    }

    /**
     * Verify that each entry into a step tracks a separate element execution.
     */
    @Test
    public void testElementsTrackedIndividuallyForAStep() throws IOException {
        NameContext stepA = createStep("A");
        NameContext stepB = createStep("B");
        tracker.enter(stepA);
        tracker.enter(stepB);
        tracker.exit();
        tracker.enter(stepB);
        tracker.exit();
        tracker.exit();
        // Expected journal: IDLE A1 B1 A1 B2 A1 IDLE
        tracker.takeSample(70);
        Assert.assertThat(getCounterValue(stepA), Matchers.equalTo(distribution(30)));
        Assert.assertThat(getCounterValue(stepB), Matchers.equalTo(distribution(10, 10)));
    }

    /**
     * Test that the currently processing element at time of sampling is also counted in the next
     * sampling period.
     */
    @Test
    public void testCurrentOperationCountedInNextSample() throws IOException {
        NameContext step = createStep("A");
        tracker.enter(step);
        tracker.takeSample(20);// Journal: IDLE A1

        tracker.takeSample(10);// Journal: A1

        tracker.exit();
        tracker.takeSample(20);// Journal: A1 IDLE

        Assert.assertThat(getCounterValue(step), Matchers.equalTo(distribution(30)));
    }

    /**
     * Ensure that sampling is properly handled when there are no new executions since the last
     * sampling period.
     */
    @Test
    public void testNoExecutionsSinceLastSample() throws IOException {
        tracker.takeSample(10);// Journal: IDLE

        NameContext step = createStep("A");
        tracker.enter(step);
        tracker.takeSample(10);// Journal: IDLE A1

        tracker.takeSample(10);// Journal: A1

        tracker.exit();
        tracker.takeSample(10);// Journal: A1 IDLE

        Assert.assertThat(getCounterValue(step), Matchers.equalTo(distribution(20)));
        tracker.takeSample(10);// Journal: IDLE

        Assert.assertThat(getCounterValue(step), Matchers.equalTo(distribution(20)));
    }

    @Test
    public void testThrowsOnExitBeforeEnter() {
        thrown.expect(IllegalStateException.class);
        tracker.exit();
    }
}

