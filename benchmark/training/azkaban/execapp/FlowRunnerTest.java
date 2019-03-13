/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.execapp;


import EventType.FLOW_FINISHED;
import EventType.FLOW_STARTED;
import EventType.JOB_FINISHED;
import EventType.JOB_STARTED;
import EventType.JOB_STATUS_CHANGED;
import FailureAction.CANCEL_ALL;
import FailureAction.FINISH_ALL_POSSIBLE;
import Status.CANCELLED;
import Status.DISABLED;
import Status.FAILED;
import Status.KILLED;
import Status.KILLING;
import Status.READY;
import Status.SKIPPED;
import Status.SUCCEEDED;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutionOptions;
import azkaban.executor.InteractiveTestJob;
import org.junit.Assert;
import org.junit.Test;


public class FlowRunnerTest extends FlowRunnerTestBase {
    private FlowRunnerTestUtil testUtil;

    @Test
    public void exec1Normal() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        this.runner = this.testUtil.createFromFlowFile(eventCollector, "exec1");
        FlowRunnerTestUtil.startThread(this.runner);
        succeedJobs("job3", "job4", "job6");
        waitForAndAssertFlowStatus(SUCCEEDED);
        assertThreadShutDown();
        compareFinishedRuntime(this.runner);
        assertStatus("job1", SUCCEEDED);
        assertStatus("job2", SUCCEEDED);
        assertStatus("job3", SUCCEEDED);
        assertStatus("job4", SUCCEEDED);
        assertStatus("job5", SUCCEEDED);
        assertStatus("job6", SUCCEEDED);
        assertStatus("job7", SUCCEEDED);
        assertStatus("job8", SUCCEEDED);
        assertStatus("job10", SUCCEEDED);
        eventCollector.assertEvents(FLOW_STARTED, FLOW_FINISHED);
    }

    @Test
    public void exec1Disabled() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        this.runner = this.testUtil.createFromFlowFile(eventCollector, "exec1");
        final ExecutableFlow exFlow = this.runner.getExecutableFlow();
        // Disable couple in the middle and at the end.
        exFlow.getExecutableNode("job1").setStatus(DISABLED);
        exFlow.getExecutableNode("job6").setStatus(DISABLED);
        exFlow.getExecutableNode("job5").setStatus(DISABLED);
        exFlow.getExecutableNode("job10").setStatus(DISABLED);
        Assert.assertTrue((!(this.runner.isKilled())));
        waitForAndAssertFlowStatus(READY);
        FlowRunnerTestUtil.startThread(this.runner);
        succeedJobs("job3", "job4");
        assertThreadShutDown();
        compareFinishedRuntime(this.runner);
        waitForAndAssertFlowStatus(SUCCEEDED);
        assertStatus("job1", SKIPPED);
        assertStatus("job2", SUCCEEDED);
        assertStatus("job3", SUCCEEDED);
        assertStatus("job4", SUCCEEDED);
        assertStatus("job5", SKIPPED);
        assertStatus("job6", SKIPPED);
        assertStatus("job7", SUCCEEDED);
        assertStatus("job8", SUCCEEDED);
        assertStatus("job10", SKIPPED);
        eventCollector.assertEvents(FLOW_STARTED, FLOW_FINISHED);
    }

    @Test
    public void exec1Failed() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        this.runner = this.testUtil.createFromFlowFile(eventCollector, "exec2");
        FlowRunnerTestUtil.startThread(this.runner);
        succeedJobs("job6");
        Assert.assertTrue((!(this.runner.isKilled())));
        waitForAndAssertFlowStatus(FAILED);
        assertStatus("job1", SUCCEEDED);
        assertStatus("job2d", FAILED);
        assertStatus("job3", CANCELLED);
        assertStatus("job4", CANCELLED);
        assertStatus("job5", CANCELLED);
        assertStatus("job6", SUCCEEDED);
        assertStatus("job7", CANCELLED);
        assertStatus("job8", CANCELLED);
        assertStatus("job9", CANCELLED);
        assertStatus("job10", CANCELLED);
        assertThreadShutDown();
        eventCollector.assertEvents(FLOW_STARTED, FLOW_FINISHED);
    }

    @Test
    public void exec1FailedKillAll() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        final ExecutionOptions options = new ExecutionOptions();
        options.setFailureAction(CANCEL_ALL);
        this.runner = this.testUtil.createFromFlowFile("exec2", eventCollector, options);
        FlowRunnerTestUtil.startThread(this.runner);
        assertThreadShutDown();
        Assert.assertTrue(this.runner.isKilled());
        waitForAndAssertFlowStatus(KILLED);
        assertStatus("job1", SUCCEEDED);
        assertStatus("job2d", FAILED);
        assertStatus("job3", CANCELLED);
        assertStatus("job4", CANCELLED);
        assertStatus("job5", CANCELLED);
        assertStatus("job6", KILLED);
        assertStatus("job7", CANCELLED);
        assertStatus("job8", CANCELLED);
        assertStatus("job9", CANCELLED);
        assertStatus("job10", CANCELLED);
        eventCollector.assertEvents(FLOW_STARTED, FLOW_FINISHED);
    }

    @Test
    public void exec1FailedFinishRest() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        final ExecutionOptions options = new ExecutionOptions();
        options.setFailureAction(FINISH_ALL_POSSIBLE);
        this.runner = this.testUtil.createFromFlowFile("exec3", eventCollector, options);
        FlowRunnerTestUtil.startThread(this.runner);
        succeedJobs("job3");
        waitForAndAssertFlowStatus(FAILED);
        assertStatus("job1", SUCCEEDED);
        assertStatus("job2d", FAILED);
        assertStatus("job3", SUCCEEDED);
        assertStatus("job4", CANCELLED);
        assertStatus("job5", CANCELLED);
        assertStatus("job6", CANCELLED);
        assertStatus("job7", SUCCEEDED);
        assertStatus("job8", SUCCEEDED);
        assertStatus("job9", SUCCEEDED);
        assertStatus("job10", CANCELLED);
        assertThreadShutDown();
        eventCollector.assertEvents(FLOW_STARTED, FLOW_FINISHED);
    }

    @Test
    public void execAndCancel() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        this.runner = this.testUtil.createFromFlowFile(eventCollector, "exec1");
        FlowRunnerTestUtil.startThread(this.runner);
        assertStatus("job1", SUCCEEDED);
        assertStatus("job2", SUCCEEDED);
        waitJobsStarted(this.runner, "job3", "job4", "job6");
        InteractiveTestJob.getTestJob("job3").ignoreCancel();
        this.runner.kill("me");
        assertStatus("job3", KILLING);
        assertFlowStatus(this.runner.getExecutableFlow(), KILLING);
        InteractiveTestJob.getTestJob("job3").failJob();
        Assert.assertTrue(this.runner.isKilled());
        assertStatus("job5", CANCELLED);
        assertStatus("job7", CANCELLED);
        assertStatus("job8", CANCELLED);
        assertStatus("job10", CANCELLED);
        assertStatus("job3", KILLED);
        assertStatus("job4", KILLED);
        assertStatus("job6", KILLED);
        assertThreadShutDown();
        waitForAndAssertFlowStatus(KILLED);
        eventCollector.assertEvents(FLOW_STARTED, FLOW_FINISHED);
    }

    @Test
    public void execRetries() throws Exception {
        final EventCollectorListener eventCollector = new EventCollectorListener();
        eventCollector.setEventFilterOut(JOB_FINISHED, JOB_STARTED, JOB_STATUS_CHANGED);
        this.runner = this.testUtil.createFromFlowFile(eventCollector, "exec4-retry");
        FlowRunnerTestUtil.startThread(this.runner);
        assertThreadShutDown();
        assertStatus("job-retry", SUCCEEDED);
        assertStatus("job-pass", SUCCEEDED);
        assertStatus("job-retry-fail", FAILED);
        assertAttempts("job-retry", 3);
        assertAttempts("job-pass", 0);
        assertAttempts("job-retry-fail", 2);
        waitForAndAssertFlowStatus(FAILED);
    }
}

