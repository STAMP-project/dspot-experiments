/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.core.step.tasklet;


import ExitStatus.COMPLETED;
import ExitStatus.NOOP;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;


/**
 *
 *
 * @author Dave Syer
 */
public class StepHandlerAdapterTests {
    private MethodInvokingTaskletAdapter tasklet = new MethodInvokingTaskletAdapter();

    private Object result = null;

    private StepExecution stepExecution = new StepExecution("systemCommandStep", new org.springframework.batch.core.JobExecution(new JobInstance(1L, "systemCommandJob"), new JobParameters()));

    @Test
    public void testExecuteWithExitStatus() throws Exception {
        tasklet.setTargetMethod("execute");
        StepContribution contribution = stepExecution.createStepContribution();
        tasklet.execute(contribution, null);
        Assert.assertEquals(NOOP, contribution.getExitStatus());
    }

    @Test
    public void testMapResultWithNull() throws Exception {
        tasklet.setTargetMethod("process");
        StepContribution contribution = stepExecution.createStepContribution();
        tasklet.execute(contribution, null);
        Assert.assertEquals(COMPLETED, contribution.getExitStatus());
    }

    @Test
    public void testMapResultWithNonNull() throws Exception {
        tasklet.setTargetMethod("process");
        this.result = "foo";
        StepContribution contribution = stepExecution.createStepContribution();
        tasklet.execute(contribution, null);
        Assert.assertEquals(COMPLETED, contribution.getExitStatus());
    }
}

