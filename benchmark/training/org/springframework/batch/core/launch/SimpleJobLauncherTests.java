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
package org.springframework.batch.core.launch;


import BatchStatus.FAILED;
import BatchStatus.STARTED;
import BatchStatus.STARTING;
import BatchStatus.STOPPING;
import BatchStatus.UNKNOWN;
import ExitStatus.COMPLETED;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.core.job.JobSupport;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.task.TaskExecutor;
import org.springframework.core.task.TaskRejectedException;


/**
 *
 *
 * @author Lucas Ward
 * @author Will Schipp
 */
public class SimpleJobLauncherTests {
    private SimpleJobLauncher jobLauncher;

    private JobSupport job = new JobSupport("foo") {
        @Override
        public void execute(JobExecution execution) {
            execution.setExitStatus(COMPLETED);
            return;
        }
    };

    private JobParameters jobParameters = new JobParameters();

    private JobRepository jobRepository;

    @Test
    public void testRun() throws Exception {
        run(COMPLETED);
    }

    @Test(expected = JobParametersInvalidException.class)
    public void testRunWithValidator() throws Exception {
        job.setJobParametersValidator(new DefaultJobParametersValidator(new String[]{ "missing-and-required" }, new String[0]));
        Mockito.when(jobRepository.getLastJobExecution(job.getName(), jobParameters)).thenReturn(null);
        jobLauncher.afterPropertiesSet();
        jobLauncher.run(job, jobParameters);
    }

    @Test
    public void testRunRestartableJobInstanceTwice() throws Exception {
        job = new JobSupport("foo") {
            @Override
            public boolean isRestartable() {
                return true;
            }

            @Override
            public void execute(JobExecution execution) {
                execution.setExitStatus(COMPLETED);
                return;
            }
        };
        testRun();
        Mockito.when(jobRepository.getLastJobExecution(job.getName(), jobParameters)).thenReturn(new JobExecution(new JobInstance(1L, job.getName()), jobParameters));
        Mockito.when(jobRepository.createJobExecution(job.getName(), jobParameters)).thenReturn(new JobExecution(new JobInstance(1L, job.getName()), jobParameters));
        jobLauncher.run(job, jobParameters);
    }

    /* Non-restartable JobInstance can be run only once - attempt to run
    existing non-restartable JobInstance causes error.
     */
    @Test
    public void testRunNonRestartableJobInstanceTwice() throws Exception {
        job = new JobSupport("foo") {
            @Override
            public boolean isRestartable() {
                return false;
            }

            @Override
            public void execute(JobExecution execution) {
                execution.setExitStatus(COMPLETED);
                return;
            }
        };
        testRun();
        try {
            Mockito.when(jobRepository.getLastJobExecution(job.getName(), jobParameters)).thenReturn(new JobExecution(new JobInstance(1L, job.getName()), jobParameters));
            jobLauncher.run(job, jobParameters);
            Assert.fail("Expected JobRestartException");
        } catch (JobRestartException e) {
            // expected
        }
    }

    @Test
    public void testTaskExecutor() throws Exception {
        final List<String> list = new ArrayList<>();
        jobLauncher.setTaskExecutor(new TaskExecutor() {
            @Override
            public void execute(Runnable task) {
                list.add("execute");
                task.run();
            }
        });
        testRun();
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testTaskExecutorRejects() throws Exception {
        final List<String> list = new ArrayList<>();
        jobLauncher.setTaskExecutor(new TaskExecutor() {
            @Override
            public void execute(Runnable task) {
                list.add("execute");
                throw new TaskRejectedException("Planned failure");
            }
        });
        JobExecution jobExecution = new JobExecution(((JobInstance) (null)), ((JobParameters) (null)));
        Mockito.when(jobRepository.getLastJobExecution(job.getName(), jobParameters)).thenReturn(null);
        Mockito.when(jobRepository.createJobExecution(job.getName(), jobParameters)).thenReturn(jobExecution);
        jobRepository.update(jobExecution);
        jobLauncher.afterPropertiesSet();
        try {
            jobLauncher.run(job, jobParameters);
        } finally {
            Assert.assertEquals(FAILED, jobExecution.getStatus());
            Assert.assertEquals(ExitStatus.FAILED.getExitCode(), jobExecution.getExitStatus().getExitCode());
        }
        Assert.assertEquals(1, list.size());
    }

    @Test
    public void testRunWithException() throws Exception {
        job = new JobSupport() {
            @Override
            public void execute(JobExecution execution) {
                execution.setExitStatus(ExitStatus.FAILED);
                throw new RuntimeException("foo");
            }
        };
        try {
            run(ExitStatus.FAILED);
            Assert.fail("Expected RuntimeException");
        } catch (RuntimeException e) {
            Assert.assertEquals("foo", e.getMessage());
        }
    }

    @Test
    public void testRunWithError() throws Exception {
        job = new JobSupport() {
            @Override
            public void execute(JobExecution execution) {
                execution.setExitStatus(ExitStatus.FAILED);
                throw new Error("foo");
            }
        };
        try {
            run(ExitStatus.FAILED);
            Assert.fail("Expected Error");
        } catch (Error e) {
            Assert.assertEquals("foo", e.getMessage());
        }
    }

    @Test
    public void testInitialiseWithoutRepository() throws Exception {
        try {
            new SimpleJobLauncher().afterPropertiesSet();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalStateException e) {
            // expected
            Assert.assertTrue(("Message did not contain repository: " + (e.getMessage())), contains(e.getMessage().toLowerCase(), "repository"));
        }
    }

    @Test
    public void testInitialiseWithRepository() throws Exception {
        jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.afterPropertiesSet();// no error

    }

    /**
     * Test to support BATCH-1770 -> throw in parent thread JobRestartException when
     * a stepExecution is UNKNOWN
     */
    @Test(expected = JobRestartException.class)
    public void testRunStepStatusUnknown() throws Exception {
        testRestartStepExecutionInvalidStatus(UNKNOWN);
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void testRunStepStatusStarting() throws Exception {
        testRestartStepExecutionInvalidStatus(STARTING);
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void testRunStepStatusStarted() throws Exception {
        testRestartStepExecutionInvalidStatus(STARTED);
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void testRunStepStatusStopping() throws Exception {
        testRestartStepExecutionInvalidStatus(STOPPING);
    }
}

