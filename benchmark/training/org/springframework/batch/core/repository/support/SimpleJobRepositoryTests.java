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
package org.springframework.batch.core.repository.support;


import BatchStatus.COMPLETED;
import BatchStatus.STARTED;
import BatchStatus.STOPPING;
import BatchStatus.UNKNOWN;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.JobSupport;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;


/**
 * Test SimpleJobRepository. The majority of test cases are tested using
 * EasyMock, however, there were some issues with using it for the stepExecutionDao when
 * testing finding or creating steps, so an actual mock class had to be written.
 *
 * @author Lucas Ward
 * @author Will Schipp
 * @author Dimitrios Liapis
 */
public class SimpleJobRepositoryTests {
    SimpleJobRepository jobRepository;

    JobSupport job;

    JobParameters jobParameters;

    Step stepConfiguration1;

    Step stepConfiguration2;

    JobExecutionDao jobExecutionDao;

    JobInstanceDao jobInstanceDao;

    StepExecutionDao stepExecutionDao;

    ExecutionContextDao ecDao;

    JobInstance jobInstance;

    String databaseStep1;

    String databaseStep2;

    List<String> steps;

    JobExecution jobExecution;

    @Test
    public void testSaveOrUpdateInvalidJobExecution() {
        // failure scenario - must have job ID
        JobExecution jobExecution = new JobExecution(((JobInstance) (null)), ((JobParameters) (null)));
        try {
            jobRepository.update(jobExecution);
            Assert.fail();
        } catch (Exception ex) {
            // expected
        }
    }

    @Test
    public void testUpdateValidJobExecution() throws Exception {
        JobExecution jobExecution = new JobExecution(new JobInstance(1L, job.getName()), 1L, jobParameters, null);
        // new execution - call update on job DAO
        jobExecutionDao.updateJobExecution(jobExecution);
        jobRepository.update(jobExecution);
        Assert.assertNotNull(jobExecution.getLastUpdated());
    }

    @Test
    public void testSaveOrUpdateStepExecutionException() {
        StepExecution stepExecution = new StepExecution("stepName", null);
        // failure scenario -- no step id set.
        try {
            jobRepository.add(stepExecution);
            Assert.fail();
        } catch (Exception ex) {
            // expected
        }
    }

    @Test
    public void testSaveStepExecutionSetsLastUpdated() {
        StepExecution stepExecution = new StepExecution("stepName", jobExecution);
        long before = System.currentTimeMillis();
        jobRepository.add(stepExecution);
        Assert.assertNotNull(stepExecution.getLastUpdated());
        long lastUpdated = stepExecution.getLastUpdated().getTime();
        Assert.assertTrue((lastUpdated > (before - 1000)));
    }

    @Test
    public void testSaveStepExecutions() {
        List<StepExecution> stepExecutions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            StepExecution stepExecution = new StepExecution(("stepName" + i), jobExecution);
            stepExecutions.add(stepExecution);
        }
        jobRepository.addAll(stepExecutions);
        Mockito.verify(stepExecutionDao).saveStepExecutions(stepExecutions);
        Mockito.verify(ecDao).saveExecutionContexts(stepExecutions);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSaveNullStepExecutions() {
        jobRepository.addAll(null);
    }

    @Test
    public void testUpdateStepExecutionSetsLastUpdated() {
        StepExecution stepExecution = new StepExecution("stepName", jobExecution);
        stepExecution.setId(2343L);
        long before = System.currentTimeMillis();
        jobRepository.update(stepExecution);
        Assert.assertNotNull(stepExecution.getLastUpdated());
        long lastUpdated = stepExecution.getLastUpdated().getTime();
        Assert.assertTrue((lastUpdated > (before - 1000)));
    }

    @Test
    public void testInterrupted() {
        jobExecution.setStatus(STOPPING);
        StepExecution stepExecution = new StepExecution("stepName", jobExecution);
        stepExecution.setId(323L);
        jobRepository.update(stepExecution);
        Assert.assertTrue(stepExecution.isTerminateOnly());
    }

    @Test
    public void testIsJobInstanceFalse() throws Exception {
        jobInstanceDao.getJobInstance("foo", new JobParameters());
        Assert.assertFalse(jobRepository.isJobInstanceExists("foo", new JobParameters()));
    }

    @Test
    public void testIsJobInstanceTrue() throws Exception {
        Mockito.when(jobInstanceDao.getJobInstance("foo", new JobParameters())).thenReturn(jobInstance);
        jobInstanceDao.getJobInstance("foo", new JobParameters());
        Assert.assertTrue(jobRepository.isJobInstanceExists("foo", new JobParameters()));
    }

    @Test(expected = JobExecutionAlreadyRunningException.class)
    public void testCreateJobExecutionAlreadyRunning() throws Exception {
        jobExecution.setStatus(STARTED);
        jobExecution.setStartTime(new Date());
        jobExecution.setEndTime(null);
        Mockito.when(jobInstanceDao.getJobInstance("foo", new JobParameters())).thenReturn(jobInstance);
        Mockito.when(jobExecutionDao.findJobExecutions(jobInstance)).thenReturn(Arrays.asList(jobExecution));
        jobRepository.createJobExecution("foo", new JobParameters());
    }

    @Test(expected = JobRestartException.class)
    public void testCreateJobExecutionStatusUnknown() throws Exception {
        jobExecution.setStatus(UNKNOWN);
        jobExecution.setEndTime(new Date());
        Mockito.when(jobInstanceDao.getJobInstance("foo", new JobParameters())).thenReturn(jobInstance);
        Mockito.when(jobExecutionDao.findJobExecutions(jobInstance)).thenReturn(Arrays.asList(jobExecution));
        jobRepository.createJobExecution("foo", new JobParameters());
    }

    @Test(expected = JobInstanceAlreadyCompleteException.class)
    public void testCreateJobExecutionAlreadyComplete() throws Exception {
        jobExecution.setStatus(COMPLETED);
        jobExecution.setEndTime(new Date());
        Mockito.when(jobInstanceDao.getJobInstance("foo", new JobParameters())).thenReturn(jobInstance);
        Mockito.when(jobExecutionDao.findJobExecutions(jobInstance)).thenReturn(Arrays.asList(jobExecution));
        jobRepository.createJobExecution("foo", new JobParameters());
    }
}

