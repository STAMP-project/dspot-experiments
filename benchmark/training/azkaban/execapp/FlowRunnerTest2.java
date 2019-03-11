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


import FailureAction.CANCEL_ALL;
import FailureAction.FINISH_ALL_POSSIBLE;
import FailureAction.FINISH_CURRENTLY_RUNNING;
import Status.CANCELLED;
import Status.DISABLED;
import Status.FAILED;
import Status.FAILED_FINISHING;
import Status.KILLED;
import Status.PAUSED;
import Status.READY;
import Status.RUNNING;
import Status.SKIPPED;
import Status.SUCCEEDED;
import azkaban.executor.ExecutableFlow;
import azkaban.executor.ExecutableFlowBase;
import azkaban.executor.ExecutableNode;
import azkaban.executor.ExecutionOptions;
import azkaban.executor.InteractiveTestJob;
import azkaban.utils.Props;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the flow run, especially with embedded flows.
 *
 * This test uses executions/embedded2. It also mainly uses the flow named jobf. The test is
 * designed to control success/failures explicitly so we don't have to time the flow exactly.
 *
 * Flow jobf looks like the following:
 *
 * <pre>
 *       joba       joba1
 *      /  |  \      |
 *     /   |   \     |
 *  jobb  jobd jobc  |
 *     \   |   /    /
 *      \  |  /    /
 *        jobe    /
 *         |     /
 *         |    /
 *        jobf
 * </pre>
 *
 * The job 'jobb' is an embedded flow:
 *
 * jobb:innerFlow
 *
 * <pre>
 *        innerJobA
 *        /       \
 *   innerJobB   innerJobC
 *        \       /
 *        innerFlow
 *
 * </pre>
 *
 * The job 'jobd' is a simple embedded flow:
 *
 * jobd:innerFlow2
 *
 * <pre>
 *       innerJobA
 *           |
 *       innerFlow2
 * </pre>
 *
 * The following tests checks each stage of the flow run by forcing jobs to succeed or fail.
 */
public class FlowRunnerTest2 extends FlowRunnerTestBase {
    private FlowRunnerTestUtil testUtil;

    /**
     * Tests the basic successful flow run, and also tests all output variables from each job.
     */
    @Test
    public void testBasicRun() throws Exception {
        final Map<String, String> flowParams = new HashMap<>();
        flowParams.put("param4", "override.4");
        flowParams.put("param10", "override.10");
        flowParams.put("param11", "override.11");
        final ExecutionOptions options = new ExecutionOptions();
        options.setFailureAction(FINISH_CURRENTLY_RUNNING);
        this.runner = this.testUtil.createFromFlowMap("jobf", options, flowParams, new Props());
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        final Props joba = this.runner.getExecutableFlow().getExecutableNodePath("joba").getInputProps();
        Assert.assertEquals("joba.1", joba.get("param1"));
        Assert.assertEquals("test1.2", joba.get("param2"));
        Assert.assertEquals("test1.3", joba.get("param3"));
        Assert.assertEquals("override.4", joba.get("param4"));
        Assert.assertEquals("test2.5", joba.get("param5"));
        Assert.assertEquals("test2.6", joba.get("param6"));
        Assert.assertEquals("test2.7", joba.get("param7"));
        Assert.assertEquals("test2.8", joba.get("param8"));
        final Props joba1 = this.runner.getExecutableFlow().getExecutableNodePath("joba1").getInputProps();
        Assert.assertEquals("test1.1", joba1.get("param1"));
        Assert.assertEquals("test1.2", joba1.get("param2"));
        Assert.assertEquals("test1.3", joba1.get("param3"));
        Assert.assertEquals("override.4", joba1.get("param4"));
        Assert.assertEquals("test2.5", joba1.get("param5"));
        Assert.assertEquals("test2.6", joba1.get("param6"));
        Assert.assertEquals("test2.7", joba1.get("param7"));
        Assert.assertEquals("test2.8", joba1.get("param8"));
        // 2. JOB A COMPLETES SUCCESSFULLY
        InteractiveTestJob.getTestJob("joba").succeedJob(Props.of("output.joba", "joba", "output.override", "joba"));
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        final ExecutableNode node = this.runner.getExecutableFlow().getExecutableNodePath("jobb");
        Assert.assertEquals(RUNNING, node.getStatus());
        final Props jobb = node.getInputProps();
        Assert.assertEquals("override.4", jobb.get("param4"));
        // Test that jobb properties overwrites the output properties
        Assert.assertEquals("moo", jobb.get("testprops"));
        Assert.assertEquals("jobb", jobb.get("output.override"));
        Assert.assertEquals("joba", jobb.get("output.joba"));
        final Props jobbInnerJobA = this.runner.getExecutableFlow().getExecutableNodePath("jobb:innerJobA").getInputProps();
        Assert.assertEquals("test1.1", jobbInnerJobA.get("param1"));
        Assert.assertEquals("test1.2", jobbInnerJobA.get("param2"));
        Assert.assertEquals("test1.3", jobbInnerJobA.get("param3"));
        Assert.assertEquals("override.4", jobbInnerJobA.get("param4"));
        Assert.assertEquals("test2.5", jobbInnerJobA.get("param5"));
        Assert.assertEquals("test2.6", jobbInnerJobA.get("param6"));
        Assert.assertEquals("test2.7", jobbInnerJobA.get("param7"));
        Assert.assertEquals("test2.8", jobbInnerJobA.get("param8"));
        Assert.assertEquals("joba", jobbInnerJobA.get("output.joba"));
        // 3. jobb:Inner completes
        // / innerJobA completes
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob(Props.of("output.jobb.innerJobA", "jobb.innerJobA"));
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        final Props jobbInnerJobB = this.runner.getExecutableFlow().getExecutableNodePath("jobb:innerJobB").getInputProps();
        Assert.assertEquals("test1.1", jobbInnerJobB.get("param1"));
        Assert.assertEquals("override.4", jobbInnerJobB.get("param4"));
        Assert.assertEquals("jobb.innerJobA", jobbInnerJobB.get("output.jobb.innerJobA"));
        Assert.assertEquals("moo", jobbInnerJobB.get("testprops"));
        // / innerJobB, C completes
        InteractiveTestJob.getTestJob("jobb:innerJobB").succeedJob(Props.of("output.jobb.innerJobB", "jobb.innerJobB"));
        InteractiveTestJob.getTestJob("jobb:innerJobC").succeedJob(Props.of("output.jobb.innerJobC", "jobb.innerJobC"));
        assertStatus("jobb:innerJobB", SUCCEEDED);
        assertStatus("jobb:innerJobC", SUCCEEDED);
        assertStatus("jobb:innerFlow", RUNNING);
        final Props jobbInnerJobD = this.runner.getExecutableFlow().getExecutableNodePath("jobb:innerFlow").getInputProps();
        Assert.assertEquals("test1.1", jobbInnerJobD.get("param1"));
        Assert.assertEquals("override.4", jobbInnerJobD.get("param4"));
        Assert.assertEquals("jobb.innerJobB", jobbInnerJobD.get("output.jobb.innerJobB"));
        Assert.assertEquals("jobb.innerJobC", jobbInnerJobD.get("output.jobb.innerJobC"));
        // 4. Finish up on inner flow for jobb
        InteractiveTestJob.getTestJob("jobb:innerFlow").succeedJob(Props.of("output1.jobb", "test1", "output2.jobb", "test2"));
        assertStatus("jobb:innerFlow", SUCCEEDED);
        assertStatus("jobb", SUCCEEDED);
        final Props jobbOutput = this.runner.getExecutableFlow().getExecutableNodePath("jobb").getOutputProps();
        Assert.assertEquals("test1", jobbOutput.get("output1.jobb"));
        Assert.assertEquals("test2", jobbOutput.get("output2.jobb"));
        // 5. Finish jobc, jobd
        InteractiveTestJob.getTestJob("jobc").succeedJob(Props.of("output.jobc", "jobc"));
        assertStatus("jobc", SUCCEEDED);
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerFlow2").succeedJob();
        assertStatus("jobd:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerFlow2", SUCCEEDED);
        assertStatus("jobd", SUCCEEDED);
        assertStatus("jobe", RUNNING);
        final Props jobd = this.runner.getExecutableFlow().getExecutableNodePath("jobe").getInputProps();
        Assert.assertEquals("test1", jobd.get("output1.jobb"));
        Assert.assertEquals("jobc", jobd.get("output.jobc"));
        // 6. Finish off flow
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobe").succeedJob();
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobe", SUCCEEDED);
        assertStatus("jobf", RUNNING);
        InteractiveTestJob.getTestJob("jobf").succeedJob();
        assertStatus("jobf", SUCCEEDED);
        waitForAndAssertFlowStatus(SUCCEEDED);
    }

    /**
     * Tests a flow with Disabled jobs and flows. They should properly SKIP executions
     */
    @Test
    public void testDisabledNormal() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        flow.getExecutableNode("jobb").setStatus(DISABLED);
        getExecutableNode("innerJobA").setStatus(DISABLED);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY, others should be skipped
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", SKIPPED);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", SKIPPED);
        assertStatus("jobd:innerFlow2", RUNNING);
        assertStatus("jobb:innerJobA", READY);
        assertStatus("jobb:innerJobB", READY);
        assertStatus("jobb:innerJobC", READY);
        assertStatus("jobb:innerFlow", READY);
        // 3. jobb:Inner completes
        // / innerJobA completes
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerFlow2").succeedJob();
        assertStatus("jobd:innerFlow2", SUCCEEDED);
        assertStatus("jobd", SUCCEEDED);
        assertStatus("jobc", SUCCEEDED);
        assertStatus("jobe", RUNNING);
        InteractiveTestJob.getTestJob("jobe").succeedJob();
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        assertStatus("jobe", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobf", RUNNING);
        // 4. Finish up on inner flow for jobb
        InteractiveTestJob.getTestJob("jobf").succeedJob();
        assertStatus("jobf", SUCCEEDED);
        waitForAndAssertFlowStatus(SUCCEEDED);
        assertThreadShutDown();
    }

    /**
     * Tests a failure with the default FINISH_CURRENTLY_RUNNING. After the first failure, every job
     * that started should complete, and the rest of the jobs should be skipped.
     */
    @Test
    public void testNormalFailure1() throws Exception {
        // Test propagation of KILLED status to embedded flows.
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY, others should be skipped
        InteractiveTestJob.getTestJob("joba").failJob();
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        assertStatus("joba", FAILED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", CANCELLED);
        assertStatus("jobc", CANCELLED);
        assertStatus("jobd", CANCELLED);
        assertStatus("jobd:innerJobA", READY);
        assertStatus("jobd:innerFlow2", READY);
        assertStatus("jobb:innerJobA", READY);
        assertStatus("jobb:innerFlow", READY);
        assertStatus("jobe", CANCELLED);
        // 3. jobb:Inner completes
        // / innerJobA completes
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(FAILED);
        assertThreadShutDown();
    }

    /**
     * Test #2 on the default failure case.
     */
    @Test
    public void testNormalFailure2() throws Exception {
        // Test propagation of KILLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY, others should be skipped
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("joba1").failJob();
        assertStatus("joba1", FAILED);
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        // 3. joba completes, everything is killed
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerJobA", SUCCEEDED);
        assertStatus("jobb:innerJobB", CANCELLED);
        assertStatus("jobb:innerJobC", CANCELLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobb", KILLED);
        assertStatus("jobd", KILLED);
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        assertStatus("jobc", SUCCEEDED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(FAILED);
        assertThreadShutDown();
    }

    @Test
    public void testNormalFailure3() throws Exception {
        // Test propagation of CANCELLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB in subflow FAILS
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("jobb:innerJobB").failJob();
        assertStatus("jobb", FAILED_FINISHING);
        assertStatus("jobb:innerJobB", FAILED);
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        InteractiveTestJob.getTestJob("jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        assertStatus("jobd:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobd", KILLED);
        assertStatus("jobb:innerJobC", SUCCEEDED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobb", FAILED);
        // 3. jobc completes, everything is killed
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        assertStatus("jobc", SUCCEEDED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(FAILED);
        assertThreadShutDown();
    }

    /**
     * Tests failures when the fail behaviour is FINISH_ALL_POSSIBLE. In this case, all jobs which
     * have had its pre-requisite met can continue to run. Finishes when the failure is propagated to
     * the last node of the flow.
     */
    @Test
    public void testFailedFinishingFailure3() throws Exception {
        // Test propagation of KILLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_ALL_POSSIBLE);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB in subflow FAILS
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("jobb:innerJobB").failJob();
        assertStatus("jobb", FAILED_FINISHING);
        assertStatus("jobb:innerJobB", FAILED);
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        InteractiveTestJob.getTestJob("jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        assertStatus("jobb", FAILED);
        assertStatus("jobd:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerFlow2", RUNNING);
        assertStatus("jobb:innerJobC", SUCCEEDED);
        assertStatus("jobb:innerFlow", CANCELLED);
        InteractiveTestJob.getTestJob("jobd:innerFlow2").succeedJob();
        assertStatus("jobd:innerFlow2", SUCCEEDED);
        assertStatus("jobd", SUCCEEDED);
        // 3. jobc completes, everything is killed
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        assertStatus("jobc", SUCCEEDED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(FAILED);
        assertThreadShutDown();
    }

    /**
     * Tests the failure condition when a failure invokes a cancel (or killed) on the flow.
     *
     * Any jobs that are running will be assigned a KILLED state, and any nodes which were skipped due
     * to prior errors will be given a CANCELLED state.
     */
    @Test
    public void testCancelOnFailure() throws Exception {
        // Test propagation of KILLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", CANCEL_ALL);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB in subflow FAILS
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("jobb:innerJobB").failJob();
        assertStatus("jobb", FAILED);
        assertStatus("jobb:innerJobB", FAILED);
        assertStatus("jobb:innerJobC", KILLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobc", KILLED);
        assertStatus("jobd", KILLED);
        assertStatus("jobd:innerJobA", KILLED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        assertThreadShutDown();
        waitForAndAssertFlowStatus(KILLED);
    }

    /**
     * Tests retries after a failure
     */
    @Test
    public void testRetryOnFailure() throws Exception {
        // Test propagation of KILLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        final ExecutableFlow flow = this.runner.getExecutableFlow();
        flow.getExecutableNode("joba").setStatus(DISABLED);
        getExecutableNode("innerFlow").setStatus(DISABLED);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        assertStatus("joba", SKIPPED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("jobb:innerJobB").failJob();
        InteractiveTestJob.getTestJob("jobb:innerJobC").failJob();
        assertStatus("jobb:innerJobB", FAILED);
        assertStatus("jobb:innerJobC", FAILED);
        assertStatus("jobb", FAILED);
        assertStatus("jobb:innerFlow", SKIPPED);
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        assertStatus("jobd:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobd", KILLED);
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        final ExecutableNode node = this.runner.getExecutableFlow().getExecutableNodePath("jobd:innerFlow2");
        final ExecutableFlowBase base = node.getParentFlow();
        for (final String nodeId : node.getInNodes()) {
            final ExecutableNode inNode = base.getExecutableNode(nodeId);
            System.out.println((((inNode.getId()) + " > ") + (inNode.getStatus())));
        }
        assertStatus("jobb:innerFlow", SKIPPED);
        InteractiveTestJob.clearTestJobs("jobb:innerJobB", "jobb:innerJobC");
        this.runner.retryFailures("me");
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerFlow", DISABLED);
        assertStatus("jobd:innerFlow2", RUNNING);
        waitForAndAssertFlowStatus(RUNNING);
        assertThreadRunning();
        InteractiveTestJob.getTestJob("jobb:innerJobB").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerFlow2").succeedJob();
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        assertStatus("jobb:innerFlow", SKIPPED);
        assertStatus("jobb", SUCCEEDED);
        assertStatus("jobb:innerJobB", SUCCEEDED);
        assertStatus("jobb:innerJobC", SUCCEEDED);
        assertStatus("jobc", SUCCEEDED);
        assertStatus("jobd", SUCCEEDED);
        assertStatus("jobd:innerFlow2", SUCCEEDED);
        assertStatus("jobe", RUNNING);
        InteractiveTestJob.getTestJob("jobe").succeedJob();
        assertStatus("jobe", SUCCEEDED);
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobf", RUNNING);
        InteractiveTestJob.getTestJob("jobf").succeedJob();
        assertStatus("jobf", SUCCEEDED);
        waitForAndAssertFlowStatus(SUCCEEDED);
        assertThreadShutDown();
    }

    /**
     * Tests the manual Killing of a flow. In this case, the flow is just fine before the cancel is
     * called.
     */
    @Test
    public void testCancel() throws Exception {
        // Test propagation of KILLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", CANCEL_ALL);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB in subflow FAILS
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        this.runner.kill("me");
        assertStatus("jobb", KILLED);
        assertStatus("jobb:innerJobB", KILLED);
        assertStatus("jobb:innerJobC", KILLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobc", KILLED);
        assertStatus("jobd", KILLED);
        assertStatus("jobd:innerJobA", KILLED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(KILLED);
        assertThreadShutDown();
    }

    /**
     * Tests the manual invocation of cancel on a flow that is FAILED_FINISHING
     */
    @Test
    public void testManualCancelOnFailure() throws Exception {
        // Test propagation of KILLED status to embedded flows different branch
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB in subflow FAILS
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        InteractiveTestJob.getTestJob("jobb:innerJobB").failJob();
        assertStatus("jobb:innerJobB", FAILED);
        assertStatus("jobb", FAILED_FINISHING);
        waitForAndAssertFlowStatus(FAILED_FINISHING);
        this.runner.kill("me");
        assertStatus("jobb", FAILED);
        assertStatus("jobb:innerJobC", KILLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobc", KILLED);
        assertStatus("jobd", KILLED);
        assertStatus("jobd:innerJobA", KILLED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(KILLED);
        assertThreadShutDown();
    }

    /**
     * Tests that pause and resume work
     */
    @Test
    public void testPause() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        this.runner.pause("test");
        InteractiveTestJob.getTestJob("joba").succeedJob();
        // 2.1 JOB A COMPLETES SUCCESSFULLY AFTER PAUSE
        assertStatus("joba", SUCCEEDED);
        waitForAndAssertFlowStatus(PAUSED);
        // 2.2 Flow is unpaused
        this.runner.resume("test");
        waitForAndAssertFlowStatus(RUNNING);
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        // 3. jobb:Inner completes
        this.runner.pause("test");
        // / innerJobA completes, but paused
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob(Props.of("output.jobb.innerJobA", "jobb.innerJobA"));
        assertStatus("jobb:innerJobA", SUCCEEDED);
        this.runner.resume("test");
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        // / innerJobB, C completes
        InteractiveTestJob.getTestJob("jobb:innerJobB").succeedJob(Props.of("output.jobb.innerJobB", "jobb.innerJobB"));
        InteractiveTestJob.getTestJob("jobb:innerJobC").succeedJob(Props.of("output.jobb.innerJobC", "jobb.innerJobC"));
        assertStatus("jobb:innerJobB", SUCCEEDED);
        assertStatus("jobb:innerJobC", SUCCEEDED);
        assertStatus("jobb:innerFlow", RUNNING);
        // 4. Finish up on inner flow for jobb
        InteractiveTestJob.getTestJob("jobb:innerFlow").succeedJob(Props.of("output1.jobb", "test1", "output2.jobb", "test2"));
        assertStatus("jobb:innerFlow", SUCCEEDED);
        assertStatus("jobb", SUCCEEDED);
        // 5. Finish jobc, jobd
        InteractiveTestJob.getTestJob("jobc").succeedJob(Props.of("output.jobc", "jobc"));
        assertStatus("jobc", SUCCEEDED);
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerFlow2").succeedJob();
        assertStatus("jobd:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerFlow2", SUCCEEDED);
        assertStatus("jobd", SUCCEEDED);
        assertStatus("jobe", RUNNING);
        // 6. Finish off flow
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobe").succeedJob();
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobe", SUCCEEDED);
        assertStatus("jobf", RUNNING);
        InteractiveTestJob.getTestJob("jobf").succeedJob();
        assertStatus("jobf", SUCCEEDED);
        waitForAndAssertFlowStatus(SUCCEEDED);
        assertThreadShutDown();
    }

    /**
     * Test the condition for a manual invocation of a KILL (cancel) on a flow that has been paused.
     * The flow should unpause and be killed immediately.
     */
    @Test
    public void testPauseKill() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        this.runner.pause("me");
        waitForAndAssertFlowStatus(PAUSED);
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerJobA").succeedJob();
        assertStatus("jobb:innerJobA", SUCCEEDED);
        assertStatus("jobd:innerJobA", SUCCEEDED);
        this.runner.kill("me");
        assertStatus("joba1", KILLED);
        assertStatus("jobb:innerJobB", CANCELLED);
        assertStatus("jobb:innerJobC", CANCELLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobb", KILLED);
        assertStatus("jobc", KILLED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobd", KILLED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(KILLED);
        assertThreadShutDown();
    }

    /**
     * Tests the case where a failure occurs on a Paused flow. In this case, the flow should stay
     * paused.
     */
    @Test
    public void testPauseFail() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_CURRENTLY_RUNNING);
        final EventCollectorListener eventCollector = new EventCollectorListener();
        this.runner.addListener(eventCollector);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        this.runner.pause("me");
        waitForAndAssertFlowStatus(PAUSED);
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerJobA").failJob();
        assertStatus("jobd:innerJobA", FAILED);
        assertStatus("jobb:innerJobA", SUCCEEDED);
        // When flow is paused, no new jobs are started. So these two jobs that were already running
        // are allowed to finish, but their dependencies aren't started.
        // Now, ensure that jobd:innerJobA has completely finished as failed before resuming.
        // If we would resume before the job failure has been completely processed, FlowRunner would be
        // able to start some new jobs instead of cancelling everything.
        FlowRunnerTestUtil.waitEventFired(eventCollector, "jobd:innerJobA", FAILED);
        waitForAndAssertFlowStatus(PAUSED);
        this.runner.resume("me");
        assertStatus("jobb:innerJobB", CANCELLED);
        assertStatus("jobb:innerJobC", CANCELLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobb", KILLED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobd", FAILED);
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        assertStatus("jobc", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobf", CANCELLED);
        assertStatus("jobe", CANCELLED);
        waitForAndAssertFlowStatus(FAILED);
        assertThreadShutDown();
    }

    /**
     * Test the condition when a Finish all possible is called during a pause. The Failure is not
     * acted upon until the flow is resumed.
     */
    @Test
    public void testPauseFailFinishAll() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", FINISH_ALL_POSSIBLE);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        this.runner.pause("me");
        waitForAndAssertFlowStatus(PAUSED);
        InteractiveTestJob.getTestJob("jobb:innerJobA").succeedJob();
        InteractiveTestJob.getTestJob("jobd:innerJobA").failJob();
        assertStatus("jobd:innerJobA", FAILED);
        assertStatus("jobb:innerJobA", SUCCEEDED);
        this.runner.resume("me");
        assertStatus("jobb:innerJobB", RUNNING);
        assertStatus("jobb:innerJobC", RUNNING);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobd", FAILED);
        InteractiveTestJob.getTestJob("jobc").succeedJob();
        InteractiveTestJob.getTestJob("joba1").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobB").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerJobC").succeedJob();
        InteractiveTestJob.getTestJob("jobb:innerFlow").succeedJob();
        assertStatus("jobc", SUCCEEDED);
        assertStatus("joba1", SUCCEEDED);
        assertStatus("jobb:innerJobB", SUCCEEDED);
        assertStatus("jobb:innerJobC", SUCCEEDED);
        assertStatus("jobb:innerFlow", SUCCEEDED);
        assertStatus("jobb", SUCCEEDED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        waitForAndAssertFlowStatus(FAILED);
        assertThreadShutDown();
    }

    /**
     * Tests the case when a job is killed by SLA causing a flow to fail. The flow should be in
     * "killed" status.
     */
    @Test
    public void testFlowKilledByJobLevelSLA() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", CANCEL_ALL);
        FlowRunnerTestUtil.startThread(this.runner);
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        for (final JobRunner jobRunner : this.runner.getActiveJobRunners()) {
            if (jobRunner.getJobId().equals("joba")) {
                jobRunner.killBySLA();
                break;
            }
        }
        waitForAndAssertFlowStatus(KILLED);
        assertThreadShutDown();
    }

    /**
     * Tests the case when a flow is paused and a failure causes a kill. The flow should die
     * immediately regardless of the 'paused' status.
     */
    @Test
    public void testPauseFailKill() throws Exception {
        this.runner = this.testUtil.createFromFlowMap("jobf", CANCEL_ALL);
        // 1. START FLOW
        FlowRunnerTestUtil.startThread(this.runner);
        // After it starts up, only joba should be running
        assertStatus("joba", RUNNING);
        assertStatus("joba1", RUNNING);
        // 2. JOB A COMPLETES SUCCESSFULLY
        InteractiveTestJob.getTestJob("joba").succeedJob();
        assertStatus("joba", SUCCEEDED);
        assertStatus("joba1", RUNNING);
        assertStatus("jobb", RUNNING);
        assertStatus("jobc", RUNNING);
        assertStatus("jobd", RUNNING);
        assertStatus("jobd:innerJobA", RUNNING);
        assertStatus("jobb:innerJobA", RUNNING);
        this.runner.pause("me");
        waitForAndAssertFlowStatus(PAUSED);
        InteractiveTestJob.getTestJob("jobd:innerJobA").failJob();
        assertStatus("jobd:innerJobA", FAILED);
        assertStatus("jobd:innerFlow2", CANCELLED);
        assertStatus("jobd", FAILED);
        assertStatus("jobb:innerJobA", KILLED);
        assertStatus("jobb:innerJobB", CANCELLED);
        assertStatus("jobb:innerJobC", CANCELLED);
        assertStatus("jobb:innerFlow", CANCELLED);
        assertStatus("jobb", KILLED);
        assertStatus("jobc", KILLED);
        assertStatus("jobe", CANCELLED);
        assertStatus("jobf", CANCELLED);
        assertStatus("joba1", KILLED);
        waitForAndAssertFlowStatus(KILLED);
        assertThreadShutDown();
    }
}

