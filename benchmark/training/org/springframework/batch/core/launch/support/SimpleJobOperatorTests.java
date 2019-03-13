/**
 * Copyright 2006-2018 the original author or authors.
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
package org.springframework.batch.core.launch.support;


import BatchStatus.ABANDONED;
import BatchStatus.STARTED;
import BatchStatus.STOPPING;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.job.AbstractJob;
import org.springframework.batch.core.launch.JobInstanceAlreadyExistsException;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.NoSuchJobExecutionException;
import org.springframework.batch.core.launch.NoSuchJobInstanceException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.StoppableTasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.repeat.RepeatStatus;


/**
 *
 *
 * @author Dave Syer
 * @author Will Schipp
 * @author Mahmoud Ben Hassine
 */
public class SimpleJobOperatorTests {
    private SimpleJobOperator jobOperator;

    protected Job job;

    private JobExplorer jobExplorer;

    private JobRepository jobRepository;

    private JobParameters jobParameters;

    @Test
    public void testMandatoryProperties() throws Exception {
        jobOperator = new SimpleJobOperator();
        try {
            jobOperator.afterPropertiesSet();
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.launch.support.SimpleJobOperator#startNextInstance(java.lang.String)}
     * .
     */
    @Test
    public void testStartNextInstanceSunnyDay() throws Exception {
        jobParameters = new JobParameters();
        JobInstance jobInstance = new JobInstance(321L, "foo");
        Mockito.when(jobExplorer.getJobInstances("foo", 0, 1)).thenReturn(Collections.singletonList(jobInstance));
        Mockito.when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(Collections.singletonList(new JobExecution(jobInstance, new JobParameters())));
        Long value = jobOperator.startNextInstance("foo");
        Assert.assertEquals(999, value.longValue());
    }

    @Test
    public void testStartNewInstanceSunnyDay() throws Exception {
        jobParameters = new JobParameters();
        jobRepository.isJobInstanceExists("foo", jobParameters);
        Long value = jobOperator.start("foo", "a=b");
        Assert.assertEquals(999, value.longValue());
    }

    @Test
    public void testStartNewInstanceAlreadyExists() throws Exception {
        jobParameters = new JobParameters();
        Mockito.when(jobRepository.isJobInstanceExists("foo", jobParameters)).thenReturn(true);
        jobRepository.isJobInstanceExists("foo", jobParameters);
        try {
            jobOperator.start("foo", "a=b");
            Assert.fail("Expected JobInstanceAlreadyExistsException");
        } catch (JobInstanceAlreadyExistsException e) {
            // expected
        }
    }

    @Test
    public void testResumeSunnyDay() throws Exception {
        jobParameters = new JobParameters();
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(new JobExecution(new JobInstance(123L, job.getName()), 111L, jobParameters, null));
        jobExplorer.getJobExecution(111L);
        Long value = jobOperator.restart(111L);
        Assert.assertEquals(999, value.longValue());
    }

    @Test
    public void testGetSummarySunnyDay() throws Exception {
        jobParameters = new JobParameters();
        JobExecution jobExecution = new JobExecution(new JobInstance(123L, job.getName()), 111L, jobParameters, null);
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(jobExecution);
        jobExplorer.getJobExecution(111L);
        String value = jobOperator.getSummary(111L);
        Assert.assertEquals(jobExecution.toString(), value);
    }

    @Test
    public void testGetSummaryNoSuchExecution() throws Exception {
        jobParameters = new JobParameters();
        jobExplorer.getJobExecution(111L);
        try {
            jobOperator.getSummary(111L);
            Assert.fail("Expected NoSuchJobExecutionException");
        } catch (NoSuchJobExecutionException e) {
            // expected
        }
    }

    @Test
    public void testGetStepExecutionSummariesSunnyDay() throws Exception {
        jobParameters = new JobParameters();
        JobExecution jobExecution = new JobExecution(new JobInstance(123L, job.getName()), 111L, jobParameters, null);
        jobExecution.createStepExecution("step1");
        jobExecution.createStepExecution("step2");
        jobExecution.getStepExecutions().iterator().next().setId(21L);
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(jobExecution);
        Map<Long, String> value = jobOperator.getStepExecutionSummaries(111L);
        Assert.assertEquals(2, value.size());
    }

    @Test
    public void testGetStepExecutionSummariesNoSuchExecution() throws Exception {
        jobParameters = new JobParameters();
        jobExplorer.getJobExecution(111L);
        try {
            jobOperator.getStepExecutionSummaries(111L);
            Assert.fail("Expected NoSuchJobExecutionException");
        } catch (NoSuchJobExecutionException e) {
            // expected
        }
    }

    @Test
    public void testFindRunningExecutionsSunnyDay() throws Exception {
        jobParameters = new JobParameters();
        JobExecution jobExecution = new JobExecution(new JobInstance(123L, job.getName()), 111L, jobParameters, null);
        Mockito.when(jobExplorer.findRunningJobExecutions("foo")).thenReturn(Collections.singleton(jobExecution));
        Set<Long> value = jobOperator.getRunningExecutions("foo");
        Assert.assertEquals(111L, value.iterator().next().longValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindRunningExecutionsNoSuchJob() throws Exception {
        jobParameters = new JobParameters();
        Mockito.when(jobExplorer.findRunningJobExecutions("no-such-job")).thenReturn(Collections.EMPTY_SET);
        try {
            jobOperator.getRunningExecutions("no-such-job");
            Assert.fail("Expected NoSuchJobException");
        } catch (NoSuchJobException e) {
            // expected
        }
    }

    @Test
    public void testGetJobParametersSunnyDay() throws Exception {
        final JobParameters jobParameters = new JobParameters();
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(new JobExecution(new JobInstance(123L, job.getName()), 111L, jobParameters, null));
        String value = jobOperator.getParameters(111L);
        Assert.assertEquals("a=b", value);
    }

    @Test
    public void testGetJobParametersNoSuchExecution() throws Exception {
        jobExplorer.getJobExecution(111L);
        try {
            jobOperator.getParameters(111L);
            Assert.fail("Expected NoSuchJobExecutionException");
        } catch (NoSuchJobExecutionException e) {
            // expected
        }
    }

    @Test
    public void testGetLastInstancesSunnyDay() throws Exception {
        jobParameters = new JobParameters();
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        Mockito.when(jobExplorer.getJobInstances("foo", 0, 2)).thenReturn(Collections.singletonList(jobInstance));
        jobExplorer.getJobInstances("foo", 0, 2);
        List<Long> value = jobOperator.getJobInstances("foo", 0, 2);
        Assert.assertEquals(123L, value.get(0).longValue());
    }

    @Test
    public void testGetLastInstancesNoSuchJob() throws Exception {
        jobParameters = new JobParameters();
        jobExplorer.getJobInstances("no-such-job", 0, 2);
        try {
            jobOperator.getJobInstances("no-such-job", 0, 2);
            Assert.fail("Expected NoSuchJobException");
        } catch (NoSuchJobException e) {
            // expected
        }
    }

    @Test
    public void testGetJobNames() throws Exception {
        Set<String> names = jobOperator.getJobNames();
        Assert.assertEquals(2, names.size());
        Assert.assertTrue(("Wrong names: " + names), names.contains("foo"));
    }

    @Test
    public void testGetExecutionsSunnyDay() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        Mockito.when(jobExplorer.getJobInstance(123L)).thenReturn(jobInstance);
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        Mockito.when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(Collections.singletonList(jobExecution));
        List<Long> value = jobOperator.getExecutions(123L);
        Assert.assertEquals(111L, value.iterator().next().longValue());
    }

    @Test
    public void testGetExecutionsNoSuchInstance() throws Exception {
        jobExplorer.getJobInstance(123L);
        try {
            jobOperator.getExecutions(123L);
            Assert.fail("Expected NoSuchJobInstanceException");
        } catch (NoSuchJobInstanceException e) {
            // expected
        }
    }

    @Test
    public void testStop() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(jobExecution);
        jobExplorer.getJobExecution(111L);
        jobRepository.update(jobExecution);
        jobOperator.stop(111L);
        Assert.assertEquals(STOPPING, jobExecution.getStatus());
    }

    @Test
    public void testStopTasklet() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        StoppableTasklet tasklet = Mockito.mock(StoppableTasklet.class);
        TaskletStep taskletStep = new TaskletStep();
        taskletStep.setTasklet(tasklet);
        SimpleJobOperatorTests.MockJob job = new SimpleJobOperatorTests.MockJob();
        job.taskletStep = taskletStep;
        JobRegistry jobRegistry = Mockito.mock(JobRegistry.class);
        TaskletStep step = Mockito.mock(TaskletStep.class);
        Mockito.when(step.getTasklet()).thenReturn(tasklet);
        Mockito.when(step.getName()).thenReturn("test_job.step1");
        Mockito.when(jobRegistry.getJob(ArgumentMatchers.any(String.class))).thenReturn(job);
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(jobExecution);
        jobOperator.setJobRegistry(jobRegistry);
        jobExplorer.getJobExecution(111L);
        jobRepository.update(jobExecution);
        jobOperator.stop(111L);
        Assert.assertEquals(STOPPING, jobExecution.getStatus());
    }

    @Test
    public void testStopTaskletWhenJobNotRegistered() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        StoppableTasklet tasklet = Mockito.mock(StoppableTasklet.class);
        JobRegistry jobRegistry = Mockito.mock(JobRegistry.class);
        TaskletStep step = Mockito.mock(TaskletStep.class);
        Mockito.when(step.getTasklet()).thenReturn(tasklet);
        Mockito.when(jobRegistry.getJob(job.getName())).thenThrow(new NoSuchJobException("Unable to find job"));
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(jobExecution);
        jobOperator.setJobRegistry(jobRegistry);
        jobOperator.stop(111L);
        Assert.assertEquals(STOPPING, jobExecution.getStatus());
        Mockito.verify(tasklet, Mockito.never()).stop();
    }

    @Test
    public void testStopTaskletException() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        StoppableTasklet tasklet = new StoppableTasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                return null;
            }

            @Override
            public void stop() {
                throw new IllegalStateException();
            }
        };
        TaskletStep taskletStep = new TaskletStep();
        taskletStep.setTasklet(tasklet);
        SimpleJobOperatorTests.MockJob job = new SimpleJobOperatorTests.MockJob();
        job.taskletStep = taskletStep;
        JobRegistry jobRegistry = Mockito.mock(JobRegistry.class);
        TaskletStep step = Mockito.mock(TaskletStep.class);
        Mockito.when(step.getTasklet()).thenReturn(tasklet);
        Mockito.when(step.getName()).thenReturn("test_job.step1");
        Mockito.when(jobRegistry.getJob(ArgumentMatchers.any(String.class))).thenReturn(job);
        Mockito.when(jobExplorer.getJobExecution(111L)).thenReturn(jobExecution);
        jobOperator.setJobRegistry(jobRegistry);
        jobExplorer.getJobExecution(111L);
        jobRepository.update(jobExecution);
        jobOperator.stop(111L);
        Assert.assertEquals(STOPPING, jobExecution.getStatus());
    }

    @Test
    public void testAbort() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        jobExecution.setStatus(STOPPING);
        Mockito.when(jobExplorer.getJobExecution(123L)).thenReturn(jobExecution);
        jobRepository.update(jobExecution);
        jobOperator.abandon(123L);
        Assert.assertEquals(ABANDONED, jobExecution.getStatus());
        Assert.assertNotNull(jobExecution.getEndTime());
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void testAbortNonStopping() throws Exception {
        JobInstance jobInstance = new JobInstance(123L, job.getName());
        JobExecution jobExecution = new JobExecution(jobInstance, 111L, jobParameters, null);
        jobExecution.setStatus(STARTED);
        Mockito.when(jobExplorer.getJobExecution(123L)).thenReturn(jobExecution);
        jobRepository.update(jobExecution);
        jobOperator.abandon(123L);
    }

    class MockJob extends AbstractJob {
        private TaskletStep taskletStep;

        @Override
        public Step getStep(String stepName) {
            return taskletStep;
        }

        @Override
        public Collection<String> getStepNames() {
            return Collections.singletonList("test_job.step1");
        }

        @Override
        protected void doExecute(JobExecution execution) throws JobExecutionException {
        }
    }
}

