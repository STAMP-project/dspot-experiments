/**
 * Copyright 2010-2014 the original author or authors.
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
import ExitStatus.UNKNOWN;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.job.flow.support.StateTransition;
import org.springframework.batch.core.job.flow.support.state.StepState;
import org.springframework.batch.core.step.StepSupport;

import static FlowExecutionStatus.COMPLETED;
import static FlowExecutionStatus.FAILED;


/**
 * Test suite for various failure scenarios during job processing.
 *
 * @author Lucas Ward
 * @author Dave Syer
 */
public class FlowJobFailureTests {
    private FlowJob job = new FlowJob();

    private JobExecution execution;

    @Test
    public void testStepFailure() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        StepState step = new StepState(new StepSupport("step"));
        transitions.add(StateTransition.createStateTransition(step, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step, COMPLETED.getExitCode(), "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(execution);
        Assert.assertEquals(BatchStatus.FAILED, execution.getStatus());
    }

    @Test
    public void testStepStatusUnknown() throws Exception {
        SimpleFlow flow = new SimpleFlow("job");
        List<StateTransition> transitions = new ArrayList<>();
        StepState step = new StepState(new StepSupport("step") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
                // This is what happens if the repository meta-data cannot be
                // updated
                stepExecution.setExitStatus(UNKNOWN);
                stepExecution.setStatus(BatchStatus.UNKNOWN);
            }
        });
        transitions.add(StateTransition.createStateTransition(step, FAILED.getExitCode(), "end0"));
        transitions.add(StateTransition.createStateTransition(step, "*", "end1"));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(FAILED, "end0")));
        transitions.add(StateTransition.createEndStateTransition(new org.springframework.batch.core.job.flow.support.state.EndState(COMPLETED, "end1")));
        flow.setStateTransitions(transitions);
        job.setFlow(flow);
        job.afterPropertiesSet();
        job.execute(execution);
        Assert.assertEquals(BatchStatus.UNKNOWN, execution.getStatus());
    }
}

