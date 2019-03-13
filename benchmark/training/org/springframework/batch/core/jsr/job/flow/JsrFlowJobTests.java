/**
 * Copyright 2006-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.jsr.job.flow;


import BatchStatus.STOPPED;
import BatchStatus.STOPPING;
import BatchStatus.UNKNOWN;
import ExitStatus.COMPLETED;
import ExitStatus.FAILED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.batch.api.Decider;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.FlowExecutor;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.job.flow.State;
import org.springframework.batch.core.job.flow.StateSupport;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.job.flow.support.StateTransition;
import org.springframework.batch.core.job.flow.support.state.DecisionState;
import org.springframework.batch.core.job.flow.support.state.StepState;
import org.springframework.batch.core.jsr.job.flow.support.JsrFlow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.step.StepSupport;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 */
public class JsrFlowJobTests {
    private JsrFlowJob job;

    private JobExecution jobExecution;

    private JobRepository jobRepository;

    private JobExplorer jobExplorer;

    private boolean fail = false;

    private JobExecutionDao jobExecutionDao;

    @Test
    public void testGetSteps() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Assert.assertEquals(2, job.getStepNames().size());
    }

    @Test
    public void testTwoSteps() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        StepExecution stepExecution = getStepExecution(jobExecution, "step2");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testFailedStep() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StateSupport("step1", FlowExecutionStatus.FAILED), "step2"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        StepExecution stepExecution = getStepExecution(jobExecution, "step2");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        Assert.assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testFailedStepRestarted() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        State step2State = new StateSupport("step2") {
            @Override
            public FlowExecutionStatus handle(FlowExecutor executor) throws Exception {
                JobExecution jobExecution = executor.getJobExecution();
                StepExecution stepExecution = jobExecution.createStepExecution(getName());
                jobRepository.add(stepExecution);
                if (fail) {
                    return FlowExecutionStatus.FAILED;
                } else {
                    return FlowExecutionStatus.COMPLETED;
                }
            }
        };
        transitions.add(StateTransition.createStateTransition(step2State, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2State, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        fail = true;
        job.execute(jobExecution);
        Assert.assertEquals(FAILED, jobExecution.getExitStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
        jobRepository.update(jobExecution);
        jobExecution = jobRepository.createJobExecution("job", new JobParameters());
        fail = false;
        job.execute(jobExecution);
        Assert.assertEquals(COMPLETED, jobExecution.getExitStatus());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testStoppingStep() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        State state2 = new StateSupport("step2", FlowExecutionStatus.FAILED);
        transitions.add(StateTransition.createStateTransition(state2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(state2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.STOPPED, "end0"), "step3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end1")));
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step3")), "end2"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end2")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
    }

    @Test
    public void testInterrupted() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                stepExecution.setStatus(STOPPING);
                jobRepository.update(stepExecution);
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        checkRepository(STOPPED, ExitStatus.STOPPED);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(JobInterruptedException.class, jobExecution.getFailureExceptions().get(0).getClass());
    }

    @Test
    public void testUnknownStatusStopsJob() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                stepExecution.setStatus(UNKNOWN);
                stepExecution.setTerminateOnly();
                jobRepository.update(stepExecution);
            }
        }), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(jobExecution);
        Assert.assertEquals(UNKNOWN, jobExecution.getStatus());
        checkRepository(UNKNOWN, ExitStatus.STOPPED);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(JobInterruptedException.class, jobExecution.getFailureExceptions().get(0).getClass());
    }

    @Test
    public void testInterruptedSplit() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        SimpleFlow flow1 = new JsrFlow("flow1");
        SimpleFlow flow2 = new JsrFlow("flow2");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                if (!(stepExecution.getJobExecution().getExecutionContext().containsKey("STOPPED"))) {
                    stepExecution.getJobExecution().getExecutionContext().put("STOPPED", true);
                    stepExecution.setStatus(STOPPED);
                    jobRepository.update(stepExecution);
                } else {
                    Assert.fail("The Job should have stopped by now");
                }
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow1.setStateTransitions(new ArrayList(transitions));
        flow1.afterPropertiesSet();
        flow2.setStateTransitions(new ArrayList(transitions));
        flow2.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.SplitState(Arrays.<Flow>asList(flow1, flow2), "split"), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        checkRepository(STOPPED, ExitStatus.STOPPED);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(JobInterruptedException.class, jobExecution.getFailureExceptions().get(0).getClass());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        for (StepExecution stepExecution : jobExecution.getStepExecutions()) {
            Assert.assertEquals(STOPPED, stepExecution.getStatus());
        }
    }

    @Test
    public void testInterruptedException() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                throw new JobInterruptedException("Stopped");
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        checkRepository(STOPPED, ExitStatus.STOPPED);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(JobInterruptedException.class, jobExecution.getFailureExceptions().get(0).getClass());
    }

    @Test
    public void testInterruptedSplitException() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        SimpleFlow flow1 = new JsrFlow("flow1");
        SimpleFlow flow2 = new JsrFlow("flow2");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                throw new JobInterruptedException("Stopped");
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow1.setStateTransitions(new ArrayList(transitions));
        flow1.afterPropertiesSet();
        flow2.setStateTransitions(new ArrayList(transitions));
        flow2.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.SplitState(Arrays.<Flow>asList(flow1, flow2), "split"), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        checkRepository(STOPPED, ExitStatus.STOPPED);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(JobInterruptedException.class, jobExecution.getFailureExceptions().get(0).getClass());
    }

    @Test
    public void testEndStateStopped() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "end"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.STOPPED, "end"), "step2"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
    }

    @Test
    public void testEndStateStoppedWithRestart() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "end"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.STOPPED, "end"), "step2"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        // To test a restart we have to use the AbstractJob.execute()...
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        jobExecution = jobRepository.createJobExecution("job", new JobParameters());
        job.execute(jobExecution);
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testBranching() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        StepState step1 = new StepState(new JsrFlowJobTests.StubStep("step1"));
        transitions.add(StateTransition.createStateTransition(step1, "step2"));
        transitions.add(StateTransition.createStateTransition(step1, "COMPLETED", "step3"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end1")));
        StepState step3 = new StepState(new JsrFlowJobTests.StubStep("step3"));
        transitions.add(StateTransition.createStateTransition(step3, FAILED.getExitCode(), "end2"));
        transitions.add(StateTransition.createStateTransition(step3, COMPLETED.getExitCode(), "end3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end2")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end3")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        StepExecution stepExecution = getStepExecution(jobExecution, "step2");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testBasicFlow() throws Throwable {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.execute(jobExecution);
        if (!(jobExecution.getAllFailureExceptions().isEmpty())) {
            throw jobExecution.getAllFailureExceptions().get(0);
        }
        Assert.assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void testDecisionFlow() throws Throwable {
        SimpleFlow flow = new JsrFlow("job");
        Decider decider = new Decider() {
            @Override
            public String decide(javax.batch[] executions) throws Exception {
                Assert.assertNotNull(executions);
                return "SWITCH";
            }
        };
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "decision"));
        StepState decision = new StepState(new JsrFlowJobTests.StubDecisionStep("decision", decider));
        transitions.add(StateTransition.createStateTransition(decision, "SWITCH", "step3"));
        transitions.add(StateTransition.createStateTransition(decision, "step2"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end1")));
        StepState step3 = new StepState(new JsrFlowJobTests.StubStep("step3"));
        transitions.add(StateTransition.createStateTransition(step3, FAILED.getExitCode(), "end2"));
        transitions.add(StateTransition.createStateTransition(step3, COMPLETED.getExitCode(), "end3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end2")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end3")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.doExecute(jobExecution);
        StepExecution stepExecution = getStepExecution(jobExecution, "step3");
        if (!(jobExecution.getAllFailureExceptions().isEmpty())) {
            throw jobExecution.getAllFailureExceptions().get(0);
        }
        Assert.assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(3, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testDecisionFlowWithExceptionInDecider() throws Throwable {
        SimpleFlow flow = new JsrFlow("job");
        JobExecutionDecider decider = new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                Assert.assertNotNull(stepExecution);
                throw new RuntimeException("Foo");
            }
        };
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "decision"));
        DecisionState decision = new DecisionState(decider, "decision");
        transitions.add(StateTransition.createStateTransition(decision, "step2"));
        transitions.add(StateTransition.createStateTransition(decision, "SWITCH", "step3"));
        StepState step2 = new StepState(new JsrFlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end1")));
        StepState step3 = new StepState(new JsrFlowJobTests.StubStep("step3"));
        transitions.add(StateTransition.createStateTransition(step3, FAILED.getExitCode(), "end2"));
        transitions.add(StateTransition.createStateTransition(step3, COMPLETED.getExitCode(), "end3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.FAILED, "end2")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end3")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        try {
            job.execute(jobExecution);
        } finally {
            Assert.assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
            Assert.assertEquals(1, jobExecution.getStepExecutions().size());
            Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
            Assert.assertEquals("Foo", jobExecution.getAllFailureExceptions().get(0).getCause().getCause().getMessage());
        }
    }

    @Test
    public void testGetStepExists() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Step step = job.getStep("step2");
        Assert.assertNotNull(step);
        Assert.assertEquals("step2", step.getName());
    }

    @Test
    public void testGetStepExistsWithPrefix() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState("job.step", new JsrFlowJobTests.StubStep("step")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.setName(flow.getName());
        job.afterPropertiesSet();
        Step step = job.getStep("step");
        Assert.assertNotNull(step);
        Assert.assertEquals("step", step.getName());
    }

    @Test
    public void testGetStepNamesWithPrefix() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState("job.step", new JsrFlowJobTests.StubStep("step")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.setName(flow.getName());
        job.afterPropertiesSet();
        Assert.assertEquals("[step]", job.getStepNames().toString());
    }

    @Test
    public void testGetStepNotExists() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Step step = job.getStep("foo");
        Assert.assertNull(step);
    }

    @Test
    public void testGetStepNotStepState() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Step step = job.getStep("end0");
        Assert.assertNull(step);
    }

    @Test
    public void testGetStepNestedFlow() throws Exception {
        SimpleFlow nested = new JsrFlow("nested");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end1")));
        nested.setStateTransitions(transitions);
        nested.afterPropertiesSet();
        SimpleFlow flow = new JsrFlow("job");
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "nested"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.FlowState(nested, "nested"), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        List<String> names = new ArrayList(job.getStepNames());
        Collections.sort(names);
        Assert.assertEquals("[step1, step2]", names.toString());
    }

    @Test
    public void testGetStepSplitFlow() throws Exception {
        SimpleFlow flow = new JsrFlow("job");
        SimpleFlow flow1 = new JsrFlow("flow1");
        SimpleFlow flow2 = new JsrFlow("flow2");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step1")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end0")));
        flow1.setStateTransitions(new ArrayList(transitions));
        flow1.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new StepState(new JsrFlowJobTests.StubStep("step2")), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end1")));
        flow2.setStateTransitions(new ArrayList(transitions));
        flow2.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.SplitState(Arrays.<Flow>asList(flow1, flow2), "split"), "end2"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FlowExecutionStatus.COMPLETED, "end2")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        List<String> names = new ArrayList(job.getStepNames());
        Collections.sort(names);
        Assert.assertEquals("[step1, step2]", names.toString());
    }

    /**
     *
     *
     * @author Dave Syer
     */
    private class StubStep extends StepSupport {
        private StubStep(String name) {
            super(name);
        }

        @Override
        public void execute(StepExecution stepExecution) throws JobInterruptedException {
            stepExecution.setStatus(BatchStatus.COMPLETED);
            stepExecution.setExitStatus(COMPLETED);
            jobRepository.update(stepExecution);
        }
    }

    /**
     *
     *
     * @author Michael Minella
     */
    private class StubDecisionStep extends StepSupport {
        private Decider decider;

        private StubDecisionStep(String name, Decider decider) {
            super(name);
            this.decider = decider;
        }

        @Override
        public void execute(StepExecution stepExecution) throws JobInterruptedException {
            stepExecution.setStatus(BatchStatus.COMPLETED);
            try {
                stepExecution.setExitStatus(new org.springframework.batch.core.ExitStatus(decider.decide(new javax.batch.runtime.StepExecution[]{ new org.springframework.batch.core.jsr.JsrStepExecution(stepExecution) })));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            jobRepository.update(stepExecution);
        }
    }
}

