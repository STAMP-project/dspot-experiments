/**
 * Copyright 2014-2018 the original author or authors.
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
package org.springframework.batch.core.repository.dao;


import BatchStatus.COMPLETED;
import BatchStatus.STOPPED;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class OptimisticLockingFailureTests {
    @Test
    public void testAsyncStopOfStartingJob() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("org/springframework/batch/core/repository/dao/OptimisticLockingFailureTests-context.xml");
        Job job = applicationContext.getBean(Job.class);
        JobLauncher jobLauncher = applicationContext.getBean(JobLauncher.class);
        JobOperator jobOperator = applicationContext.getBean(JobOperator.class);
        JobExecution jobExecution = jobLauncher.run(job, new JobParametersBuilder().addLong("test", 1L).toJobParameters());
        Thread.sleep(1000);
        jobOperator.stop(jobExecution.getId());
        while (jobExecution.isRunning()) {
            // wait for async launched job to complete execution
        } 
        int numStepExecutions = jobExecution.getStepExecutions().size();
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        String stepName = stepExecution.getStepName();
        BatchStatus stepExecutionStatus = stepExecution.getStatus();
        BatchStatus jobExecutionStatus = jobExecution.getStatus();
        Assert.assertTrue(("Should only be one StepExecution but got: " + numStepExecutions), (numStepExecutions == 1));
        Assert.assertTrue(("Step name for execution should be step1 but got: " + stepName), "step1".equals(stepName));
        Assert.assertTrue(("Step execution status should be STOPPED but got: " + stepExecutionStatus), stepExecutionStatus.equals(STOPPED));
        Assert.assertTrue(("Job execution status should be STOPPED but got:" + jobExecutionStatus), jobExecutionStatus.equals(STOPPED));
        JobExecution restartJobExecution = jobLauncher.run(job, new JobParametersBuilder().addLong("test", 1L).toJobParameters());
        Thread.sleep(1000);
        while (restartJobExecution.isRunning()) {
            // wait for async launched job to complete execution
        } 
        int restartNumStepExecutions = restartJobExecution.getStepExecutions().size();
        Assert.assertTrue(("Should be two StepExecution's on restart but got: " + restartNumStepExecutions), (restartNumStepExecutions == 2));
        for (StepExecution restartStepExecution : restartJobExecution.getStepExecutions()) {
            BatchStatus restartStepExecutionStatus = restartStepExecution.getStatus();
            Assert.assertTrue(("Step execution status should be COMPLETED but got: " + restartStepExecutionStatus), restartStepExecutionStatus.equals(COMPLETED));
        }
        BatchStatus restartJobExecutionStatus = restartJobExecution.getStatus();
        Assert.assertTrue(("Job execution status should be COMPLETED but got:" + restartJobExecutionStatus), restartJobExecutionStatus.equals(COMPLETED));
    }

    public static class Writer implements ItemWriter<String> {
        @Override
        public void write(List<? extends String> items) throws Exception {
            for (String item : items) {
                System.out.println(item);
            }
        }
    }

    public static class SleepingTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            Thread.sleep(2000L);
            return RepeatStatus.FINISHED;
        }
    }
}

