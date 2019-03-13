/**
 * Copyright 2008-2014 the original author or authors.
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
package org.springframework.batch.core.job;


import BatchStatus.FAILED;
import BatchStatus.UNKNOWN;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.step.StepSupport;


/**
 * Test suite for various failure scenarios during job processing.
 *
 * @author Lucas Ward
 * @author Dave Syer
 */
public class SimpleJobFailureTests {
    private SimpleJob job = new SimpleJob("job");

    private JobExecution execution;

    @Test
    public void testStepFailure() throws Exception {
        job.setSteps(Arrays.<Step>asList(new StepSupport("step")));
        job.execute(execution);
        Assert.assertEquals(FAILED, execution.getStatus());
    }

    @Test
    public void testStepStatusUnknown() throws Exception {
        job.setSteps(Arrays.<Step>asList(new StepSupport("step1") {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
                // This is what happens if the repository meta-data cannot be updated
                stepExecution.setStatus(UNKNOWN);
                stepExecution.setTerminateOnly();
            }
        }, new StepSupport("step2")));
        job.execute(execution);
        Assert.assertEquals(UNKNOWN, execution.getStatus());
        Assert.assertEquals(1, execution.getStepExecutions().size());
    }
}

