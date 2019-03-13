/**
 * Copyright 2006-2013 the original author or authors.
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


import ExitStatus.COMPLETED;
import ExitStatus.FAILED;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.job.flow.support.StateTransition;
import org.springframework.batch.core.job.flow.support.state.StepState;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.StepSupport;

import static FlowExecutionStatus.COMPLETED;
import static FlowExecutionStatus.FAILED;


/**
 *
 *
 * @author Dave Syer
 */
public class FlowStepTests {
    private JobRepository jobRepository;

    private JobExecution jobExecution;

    /**
     * Test method for {@link org.springframework.batch.core.job.flow.FlowStep#afterPropertiesSet()}.
     */
    @Test(expected = IllegalStateException.class)
    public void testAfterPropertiesSet() throws Exception {
        FlowStep step = new FlowStep();
        step.setJobRepository(jobRepository);
        step.afterPropertiesSet();
    }

    /**
     * Test method for {@link org.springframework.batch.core.job.flow.FlowStep#doExecute(org.springframework.batch.core.StepExecution)}.
     */
    @Test
    public void testDoExecute() throws Exception {
        FlowStep step = new FlowStep();
        step.setJobRepository(jobRepository);
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowStepTests.StubStep("step1")), "step2"));
        StepState step2 = new StepState(new FlowStepTests.StubStep("step2"));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        step.setFlow(flow);
        step.afterPropertiesSet();
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        stepExecution = getStepExecution(jobExecution, "step");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        stepExecution = getStepExecution(jobExecution, "step2");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        Assert.assertEquals(3, jobExecution.getStepExecutions().size());
    }

    // BATCH-1620
    @Test
    public void testDoExecuteAndFail() throws Exception {
        FlowStep step = new FlowStep();
        step.setJobRepository(jobRepository);
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowStepTests.StubStep("step1")), "step2"));
        StepState step2 = new StepState(new FlowStepTests.StubStep("step2", true));
        transitions.add(StateTransition.createStateTransition(step2, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step2, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        step.setFlow(flow);
        step.afterPropertiesSet();
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        stepExecution = getStepExecution(jobExecution, "step1");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        stepExecution = getStepExecution(jobExecution, "step2");
        Assert.assertEquals(FAILED, stepExecution.getExitStatus());
        stepExecution = getStepExecution(jobExecution, "step");
        Assert.assertEquals(FAILED, stepExecution.getExitStatus());
        Assert.assertEquals(3, jobExecution.getStepExecutions().size());
    }

    /**
     * Test method for {@link org.springframework.batch.core.job.flow.FlowStep#doExecute(org.springframework.batch.core.StepExecution)}.
     */
    @Test
    public void testExecuteWithParentContext() throws Exception {
        FlowStep step = new FlowStep();
        step.setJobRepository(jobRepository);
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        transitions.add(StateTransition.createStateTransition(new StepState(new FlowStepTests.StubStep("step1")), "end0"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end0")));
        flow.setStateTransitions(transitions);
        step.setFlow(flow);
        step.afterPropertiesSet();
        StepExecution stepExecution = jobExecution.createStepExecution("step");
        stepExecution.getExecutionContext().put("foo", "bar");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        stepExecution = getStepExecution(jobExecution, "step");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        stepExecution = getStepExecution(jobExecution, "step1");
        Assert.assertEquals(COMPLETED, stepExecution.getExitStatus());
        Assert.assertEquals("bar", stepExecution.getExecutionContext().get("foo"));
    }

    /**
     *
     *
     * @author Dave Syer
     */
    private class StubStep extends StepSupport {
        private final boolean fail;

        private StubStep(String name) {
            this(name, false);
        }

        private StubStep(String name, boolean fail) {
            super(name);
            this.fail = fail;
        }

        @Override
        public void execute(StepExecution stepExecution) throws JobInterruptedException {
            BatchStatus status = BatchStatus.COMPLETED;
            ExitStatus exitStatus = ExitStatus.COMPLETED;
            if (fail) {
                status = BatchStatus.FAILED;
                exitStatus = ExitStatus.FAILED;
            }
            stepExecution.setStatus(status);
            stepExecution.setExitStatus(exitStatus);
            jobRepository.update(stepExecution);
        }
    }
}

