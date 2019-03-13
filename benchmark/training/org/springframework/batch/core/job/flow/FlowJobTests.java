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
package org.springframework.batch.core.job.flow;


import BatchStatus.STOPPED;
import BatchStatus.STOPPING;
import BatchStatus.UNKNOWN;
import ExitStatus.COMPLETED;
import ExitStatus.FAILED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.support.DefaultStateTransitionComparator;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.job.flow.support.StateTransition;
import org.springframework.batch.core.job.flow.support.state.DecisionState;
import org.springframework.batch.core.job.flow.support.state.StepState;
import org.springframework.batch.core.jsr.configuration.support.BatchPropertyContext;
import org.springframework.batch.core.jsr.partition.JsrPartitionHandler;
import org.springframework.batch.core.jsr.step.PartitionStep;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.step.StepSupport;

import static FlowExecutionStatus.COMPLETED;
import static FlowExecutionStatus.FAILED;
import static FlowExecutionStatus.STOPPED;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 */
public class FlowJobTests {
    private FlowJob job = new FlowJob();

    private JobExecution jobExecution;

    private JobRepository jobRepository;

    private boolean fail = false;

    private JobExecutionDao jobExecutionDao;

    @Test
    public void testGetSteps() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Assert.assertEquals(2, job.getStepNames().size());
    }

    @Test
    public void testTwoSteps() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StateSupport("step1", FAILED), "step2"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        State step2State = new StateSupport("step2") {
            @Override
            public FlowExecutionStatus handle(FlowExecutor executor) throws Exception {
                JobExecution jobExecution = executor.getJobExecution();
                jobExecution.createStepExecution(getName());
                if (fail) {
                    return FlowExecutionStatus.FAILED;
                } else {
                    return FlowExecutionStatus.COMPLETED;
                }
            }
        };
        transitions.add(StateTransition.createStateTransition(step2State, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2State, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end1")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        State state2 = new StateSupport("step2", FAILED);
        transitions.add(StateTransition.createStateTransition(state2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(state2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(STOPPED, "end0"), "step3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step3")), "end2"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end2")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
    }

    @Test
    public void testInterrupted() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                stepExecution.setStatus(STOPPING);
                jobRepository.update(stepExecution);
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                stepExecution.setStatus(UNKNOWN);
                stepExecution.setTerminateOnly();
                jobRepository.update(stepExecution);
            }
        }), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        SimpleFlow flow1 = new SimpleFlow("flow1");
        SimpleFlow flow2 = new SimpleFlow("flow2");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1") {
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
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow1.setStateTransitions(new ArrayList(transitions));
        flow1.afterPropertiesSet();
        flow2.setStateTransitions(new ArrayList(transitions));
        flow2.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.SplitState(Arrays.<Flow>asList(flow1, flow2), "split"), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                throw new JobInterruptedException("Stopped");
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        SimpleFlow flow1 = new SimpleFlow("flow1");
        SimpleFlow flow2 = new SimpleFlow("flow2");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                throw new JobInterruptedException("Stopped");
            }
        }), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow1.setStateTransitions(new ArrayList(transitions));
        flow1.afterPropertiesSet();
        flow2.setStateTransitions(new ArrayList(transitions));
        flow2.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.SplitState(Arrays.<Flow>asList(flow1, flow2), "split"), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "end"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(STOPPED, "end"), "step2"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
    }

    @Test
    public void testEndStateStoppedWithRestart() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "end"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(STOPPED, "end"), "step2"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end1")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        StepState step1 = new StepState(new FlowJobTests.StubStep("step1"));
        transitions.add(StateTransition.createStateTransition(step1, "step2"));
        transitions.add(StateTransition.createStateTransition(step1, "COMPLETED", "step3"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end1")));
        StepState step3 = new StepState(new FlowJobTests.StubStep("step3"));
        transitions.add(StateTransition.createStateTransition(step3, FAILED.getExitCode(), "end2"));
        transitions.add(StateTransition.createStateTransition(step3, COMPLETED.getExitCode(), "end3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end2")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end3")));
        flow.setStateTransitions(transitions);
        flow.setStateTransitionComparator(new DefaultStateTransitionComparator());
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.doExecute(jobExecution);
        StepExecution stepExecution = getStepExecution(jobExecution, "step3");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testBasicFlow() throws Throwable {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        JobExecutionDecider decider = new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                Assert.assertNotNull(stepExecution);
                return new FlowExecutionStatus("SWITCH");
            }
        };
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "decision"));
        DecisionState decision = new DecisionState(decider, "decision");
        transitions.add(StateTransition.createStateTransition(decision, "step2"));
        transitions.add(StateTransition.createStateTransition(decision, "SWITCH", "step3"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end1")));
        StepState step3 = new StepState(new FlowJobTests.StubStep("step3"));
        transitions.add(StateTransition.createStateTransition(step3, FAILED.getExitCode(), "end2"));
        transitions.add(StateTransition.createStateTransition(step3, COMPLETED.getExitCode(), "end3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end2")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end3")));
        flow.setStateTransitions(transitions);
        flow.setStateTransitionComparator(new DefaultStateTransitionComparator());
        job.setFlow(flow);
        job.doExecute(jobExecution);
        StepExecution stepExecution = getStepExecution(jobExecution, "step3");
        if (!(jobExecution.getAllFailureExceptions().isEmpty())) {
            throw jobExecution.getAllFailureExceptions().get(0);
        }
        Assert.assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
    }

    @Test
    public void testDecisionFlowWithExceptionInDecider() throws Throwable {
        SimpleFlow flow = new SimpleFlow("job");
        JobExecutionDecider decider = new JobExecutionDecider() {
            @Override
            public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
                Assert.assertNotNull(stepExecution);
                throw new RuntimeException("Foo");
            }
        };
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "decision"));
        DecisionState decision = new DecisionState(decider, "decision");
        transitions.add(StateTransition.createStateTransition(decision, "step2"));
        transitions.add(StateTransition.createStateTransition(decision, "SWITCH", "step3"));
        StepState step2 = new StepState(new FlowJobTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end1")));
        StepState step3 = new StepState(new FlowJobTests.StubStep("step3"));
        transitions.add(StateTransition.createStateTransition(step3, FAILED.getExitCode(), "end2"));
        transitions.add(StateTransition.createStateTransition(step3, COMPLETED.getExitCode(), "end3"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end2")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end3")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Step step = job.getStep("step2");
        Assert.assertNotNull(step);
        Assert.assertEquals("step2", step.getName());
    }

    @Test
    public void testGetPartitionedStep() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        PartitionStep step = new PartitionStep();
        step.setName("step1");
        JsrPartitionHandler partitionHandler = new JsrPartitionHandler();
        partitionHandler.setPropertyContext(new BatchPropertyContext());
        partitionHandler.setPartitions(3);
        partitionHandler.setJobRepository(jobRepository);
        partitionHandler.setStep(new FlowJobTests.StubStep("subStep"));
        partitionHandler.afterPropertiesSet();
        step.setPartitionHandler(partitionHandler);
        step.setStepExecutionSplitter(new org.springframework.batch.core.jsr.partition.JsrStepExecutionSplitter(jobRepository, false, "step1", true));
        step.setJobRepository(jobRepository);
        step.afterPropertiesSet();
        transitions.add(StateTransition.createStateTransition(new StepState("job.step", step), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(jobRepository.createJobExecution("partitionJob", new JobParameters()));
        Assert.assertEquals(3, step.getStepNames().size());
        Step subStep = job.getStep("step1:partition0");
        Assert.assertNotNull(subStep);
        Assert.assertEquals("subStep", subStep.getName());
        Assert.assertNull(job.getStep("step that does not exist"));
    }

    @Test
    public void testGetStepExistsWithPrefix() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState("job.step", new FlowJobTests.StubStep("step")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState("job.step", new FlowJobTests.StubStep("step")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.setName(flow.getName());
        job.afterPropertiesSet();
        Assert.assertEquals("[step]", job.getStepNames().toString());
    }

    @Test
    public void testGetStepNotExists() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Step step = job.getStep("foo");
        Assert.assertNull(step);
    }

    @Test
    public void testGetStepNotStepState() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "step2"));
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        flow.afterPropertiesSet();
        job.setFlow(flow);
        job.afterPropertiesSet();
        Step step = job.getStep("end0");
        Assert.assertNull(step);
    }

    @Test
    public void testGetStepNestedFlow() throws Exception {
        SimpleFlow nested = new SimpleFlow("nested");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        nested.setStateTransitions(transitions);
        nested.afterPropertiesSet();
        SimpleFlow flow = new SimpleFlow("job");
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "nested"));
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.FlowState(nested, "nested"), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
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
        SimpleFlow flow = new SimpleFlow("job");
        SimpleFlow flow1 = new SimpleFlow("flow1");
        SimpleFlow flow2 = new SimpleFlow("flow2");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step1")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow1.setStateTransitions(new ArrayList(transitions));
        flow1.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowJobTests.StubStep("step2")), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        flow2.setStateTransitions(new ArrayList(transitions));
        flow2.afterPropertiesSet();
        transitions = new ArrayList();
        transitions.add(StateTransition.createStateTransition(new org.springframework.batch.core.job.flow.support.state.SplitState(Arrays.<Flow>asList(flow1, flow2), "split"), "end2"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end2")));
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
}

