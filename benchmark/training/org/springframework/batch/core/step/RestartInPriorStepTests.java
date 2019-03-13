/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.batch.core.step;


import BatchStatus.COMPLETED;
import BatchStatus.STOPPED;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 *
 *
 * @author Michael Minella
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class RestartInPriorStepTests {
    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

    @Test
    public void test() throws Exception {
        JobExecution run1 = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(STOPPED, run1.getStatus());
        Assert.assertEquals(2, run1.getStepExecutions().size());
        JobExecution run2 = jobLauncher.run(job, new JobParameters());
        Assert.assertEquals(COMPLETED, run2.getStatus());
        Assert.assertEquals(6, run2.getStepExecutions().size());
    }

    public static class DecidingTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            Map<String, Object> context = chunkContext.getStepContext().getJobExecutionContext();
            if ((context.get("restart")) != null) {
                contribution.setExitStatus(new ExitStatus("ES3"));
            } else {
                chunkContext.getStepContext().setAttribute("restart", true);
                contribution.setExitStatus(new ExitStatus("ES4"));
            }
            return RepeatStatus.FINISHED;
        }
    }

    public static class CompletionDecider implements JobExecutionDecider {
        private int count = 0;

        @Override
        public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
            (count)++;
            if ((count) > 2) {
                return new FlowExecutionStatus("END");
            } else {
                return new FlowExecutionStatus("CONTINUE");
            }
        }
    }
}

