/**
 * Copyright 2013-2018 the original author or authors.
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
package org.springframework.batch.core.jsr.launch;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STOPPED;
import JsrJobOperator.BaseContextHolder;
import JsrJobParametersConverter.JOB_RUN_ID;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.batch.api.AbstractBatchlet;
import javax.batch.api.Batchlet;
import javax.batch.operations.JobExecutionIsRunningException;
import javax.batch.operations.JobOperator;
import javax.batch.operations.JobRestartException;
import javax.batch.operations.JobStartException;
import javax.batch.operations.NoSuchJobException;
import javax.batch.operations.NoSuchJobExecutionException;
import javax.batch.operations.NoSuchJobInstanceException;
import javax.batch.runtime.BatchRuntime;
import javax.sql.DataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.BatchStatus.ABANDONED;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.configuration.annotation.DataSourceConfiguration;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.converter.JobParametersConverter;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.jsr.AbstractJsrTestCase;
import org.springframework.batch.core.jsr.JsrJobParametersConverter;
import org.springframework.batch.core.org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.JobRepositorySupport;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;


/**
 * Tests for {@link JsrJobOperator}.
 */
public class JsrJobOperatorTests extends AbstractJsrTestCase {
    private JobOperator jsrJobOperator;

    @Mock
    private JobExplorer jobExplorer;

    @Mock
    private JobRepository jobRepository;

    private JobParametersConverter parameterConverter;

    private static final long TIMEOUT = 10000L;

    @Test
    public void testLoadingWithBatchRuntime() {
        jsrJobOperator = BatchRuntime.getJobOperator();
        Assert.assertNotNull(jsrJobOperator);
    }

    @Test
    public void testNullsInConstructor() {
        try {
            new JsrJobOperator(null, new JobRepositorySupport(), parameterConverter, null);
            Assert.fail("JobExplorer should be required");
        } catch (IllegalArgumentException correct) {
        }
        try {
            new JsrJobOperator(new SimpleJobExplorer(null, null, null, null), null, parameterConverter, null);
            Assert.fail("JobRepository should be required");
        } catch (IllegalArgumentException correct) {
        }
        try {
            new JsrJobOperator(new SimpleJobExplorer(null, null, null, null), new JobRepositorySupport(), null, null);
            Assert.fail("ParameterConverter should be required");
        } catch (IllegalArgumentException correct) {
        }
        try {
            new JsrJobOperator(new SimpleJobExplorer(null, null, null, null), new JobRepositorySupport(), parameterConverter, null);
        } catch (IllegalArgumentException correct) {
        }
        new JsrJobOperator(new SimpleJobExplorer(null, null, null, null), new JobRepositorySupport(), parameterConverter, new ResourcelessTransactionManager());
    }

    @Test
    public void testCustomBaseContextJsrCompliant() throws Exception {
        System.setProperty("JSR-352-BASE-CONTEXT", "META-INF/alternativeJsrBaseContext.xml");
        ReflectionTestUtils.setField(BaseContextHolder.class, "instance", null);
        JobOperator jobOperator = BatchRuntime.getJobOperator();
        Object transactionManager = ReflectionTestUtils.getField(jobOperator, "transactionManager");
        Assert.assertTrue((transactionManager instanceof ResourcelessTransactionManager));
        long executionId = jobOperator.start("longRunningJob", null);
        // Give the job a chance to get started
        Thread.sleep(1000L);
        jobOperator.stop(executionId);
        // Give the job the chance to finish stopping
        Thread.sleep(1000L);
        Assert.assertEquals(STOPPED, jobOperator.getJobExecution(executionId).getBatchStatus());
        System.getProperties().remove("JSR-352-BASE-CONTEXT");
    }

    @Test
    public void testCustomBaseContextCustomWired() throws Exception {
        GenericApplicationContext context = new AnnotationConfigApplicationContext(JsrJobOperatorTests.BatchConfgiuration.class);
        JobOperator jobOperator = ((JobOperator) (context.getBean("jobOperator")));
        Assert.assertEquals(context, ReflectionTestUtils.getField(jobOperator, "baseContext"));
        long executionId = jobOperator.start("longRunningJob", null);
        // Give the job a chance to get started
        Thread.sleep(1000L);
        jobOperator.stop(executionId);
        // Give the job the chance to finish stopping
        Thread.sleep(1000L);
        Assert.assertEquals(STOPPED, jobOperator.getJobExecution(executionId).getBatchStatus());
        System.getProperties().remove("JSR-352-BASE-CONTEXT");
    }

    @Test
    public void testDefaultTaskExecutor() throws Exception {
        JsrJobOperator jsrJobOperatorImpl = ((JsrJobOperator) (jsrJobOperator));
        jsrJobOperatorImpl.afterPropertiesSet();
        Assert.assertNotNull(jsrJobOperatorImpl.getTaskExecutor());
        Assert.assertTrue(((jsrJobOperatorImpl.getTaskExecutor()) instanceof AsyncTaskExecutor));
    }

    @Test
    public void testCustomTaskExecutor() throws Exception {
        JsrJobOperator jsrJobOperatorImpl = ((JsrJobOperator) (jsrJobOperator));
        jsrJobOperatorImpl.setTaskExecutor(new SyncTaskExecutor());
        jsrJobOperatorImpl.afterPropertiesSet();
        Assert.assertNotNull(jsrJobOperatorImpl.getTaskExecutor());
        Assert.assertTrue(((jsrJobOperatorImpl.getTaskExecutor()) instanceof SyncTaskExecutor));
    }

    @Test
    public void testAbandonRoseyScenario() throws Exception {
        JobExecution jobExecution = new JobExecution(5L);
        jobExecution.setEndTime(new Date());
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(jobExecution);
        jsrJobOperator.abandon(5L);
        ArgumentCaptor<JobExecution> executionCaptor = ArgumentCaptor.forClass(JobExecution.class);
        Mockito.verify(jobRepository).update(executionCaptor.capture());
        Assert.assertEquals(ABANDONED, executionCaptor.getValue().getStatus());
    }

    @Test(expected = NoSuchJobExecutionException.class)
    public void testAbandonNoSuchJob() throws Exception {
        jsrJobOperator.abandon(5L);
    }

    @Test(expected = JobExecutionIsRunningException.class)
    public void testAbandonJobRunning() throws Exception {
        JobExecution jobExecution = new JobExecution(5L);
        jobExecution.setStartTime(new Date(1L));
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(jobExecution);
        jsrJobOperator.abandon(5L);
    }

    @Test
    public void testGetJobExecutionRoseyScenario() {
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(new JobExecution(5L));
        Assert.assertEquals(5L, jsrJobOperator.getJobExecution(5L).getExecutionId());
    }

    @Test(expected = NoSuchJobExecutionException.class)
    public void testGetJobExecutionNoExecutionFound() {
        jsrJobOperator.getJobExecution(5L);
    }

    @Test
    public void testGetJobExecutionsRoseyScenario() {
        org.springframework.batch.core.JobInstance jobInstance = new org.springframework.batch.core.JobInstance(5L, "my job");
        List<JobExecution> executions = new ArrayList<>();
        executions.add(new JobExecution(2L));
        Mockito.when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(executions);
        List<javax.batch.runtime.JobExecution> jobExecutions = jsrJobOperator.getJobExecutions(jobInstance);
        Assert.assertEquals(1, jobExecutions.size());
        Assert.assertEquals(2L, executions.get(0).getId().longValue());
    }

    @Test(expected = NoSuchJobInstanceException.class)
    public void testGetJobExecutionsNullJobInstance() {
        jsrJobOperator.getJobExecutions(null);
    }

    @Test(expected = NoSuchJobInstanceException.class)
    public void testGetJobExecutionsNullReturned() {
        org.springframework.batch.core.JobInstance jobInstance = new org.springframework.batch.core.JobInstance(5L, "my job");
        jsrJobOperator.getJobExecutions(jobInstance);
    }

    @Test(expected = NoSuchJobInstanceException.class)
    public void testGetJobExecutionsNoneReturned() {
        org.springframework.batch.core.JobInstance jobInstance = new org.springframework.batch.core.JobInstance(5L, "my job");
        List<JobExecution> executions = new ArrayList<>();
        Mockito.when(jobExplorer.getJobExecutions(jobInstance)).thenReturn(executions);
        jsrJobOperator.getJobExecutions(jobInstance);
    }

    @Test
    public void testGetJobInstanceRoseyScenario() {
        JobInstance instance = new JobInstance(1L, "my job");
        JobExecution execution = new JobExecution(5L);
        execution.setJobInstance(instance);
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(execution);
        Mockito.when(jobExplorer.getJobInstance(1L)).thenReturn(instance);
        javax.batch.runtime.JobInstance jobInstance = jsrJobOperator.getJobInstance(5L);
        Assert.assertEquals(1L, jobInstance.getInstanceId());
        Assert.assertEquals("my job", jobInstance.getJobName());
    }

    @Test(expected = NoSuchJobExecutionException.class)
    public void testGetJobInstanceNoExecution() {
        JobInstance instance = new JobInstance(1L, "my job");
        JobExecution execution = new JobExecution(5L);
        execution.setJobInstance(instance);
        jsrJobOperator.getJobInstance(5L);
    }

    @Test
    public void testGetJobInstanceCount() throws Exception {
        Mockito.when(jobExplorer.getJobInstanceCount("myJob")).thenReturn(4);
        Assert.assertEquals(4, jsrJobOperator.getJobInstanceCount("myJob"));
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetJobInstanceCountNoSuchJob() throws Exception {
        Mockito.when(jobExplorer.getJobInstanceCount("myJob")).thenThrow(new javax.batch.operations.org.springframework.batch.core.launch.NoSuchJobException("expected"));
        jsrJobOperator.getJobInstanceCount("myJob");
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetJobInstanceCountZeroInstancesReturned() throws Exception {
        Mockito.when(jobExplorer.getJobInstanceCount("myJob")).thenReturn(0);
        jsrJobOperator.getJobInstanceCount("myJob");
    }

    @Test
    public void testGetJobInstancesRoseyScenario() {
        List<JobInstance> instances = new ArrayList<>();
        instances.add(new JobInstance(1L, "myJob"));
        instances.add(new JobInstance(2L, "myJob"));
        instances.add(new JobInstance(3L, "myJob"));
        Mockito.when(jobExplorer.getJobInstances("myJob", 0, 3)).thenReturn(instances);
        List<javax.batch.runtime.JobInstance> jobInstances = jsrJobOperator.getJobInstances("myJob", 0, 3);
        Assert.assertEquals(3, jobInstances.size());
        Assert.assertEquals(1L, jobInstances.get(0).getInstanceId());
        Assert.assertEquals(2L, jobInstances.get(1).getInstanceId());
        Assert.assertEquals(3L, jobInstances.get(2).getInstanceId());
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetJobInstancesNullInstancesReturned() {
        jsrJobOperator.getJobInstances("myJob", 0, 3);
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetJobInstancesZeroInstancesReturned() {
        List<JobInstance> instances = new ArrayList<>();
        Mockito.when(jobExplorer.getJobInstances("myJob", 0, 3)).thenReturn(instances);
        jsrJobOperator.getJobInstances("myJob", 0, 3);
    }

    @Test
    public void testGetJobNames() {
        List<String> jobNames = new ArrayList<>();
        jobNames.add("job1");
        jobNames.add("job2");
        Mockito.when(jobExplorer.getJobNames()).thenReturn(jobNames);
        Set<String> result = jsrJobOperator.getJobNames();
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.contains("job1"));
        Assert.assertTrue(result.contains("job2"));
    }

    @Test
    public void testGetParametersRoseyScenario() {
        JobExecution jobExecution = new JobExecution(5L, new JobParametersBuilder().addString("key1", "value1").addLong(JOB_RUN_ID, 5L).toJobParameters());
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(jobExecution);
        Properties params = jsrJobOperator.getParameters(5L);
        Assert.assertEquals("value1", params.get("key1"));
        Assert.assertNull(params.get(JOB_RUN_ID));
    }

    @Test(expected = NoSuchJobExecutionException.class)
    public void testGetParametersNoExecution() {
        jsrJobOperator.getParameters(5L);
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetNoRunningExecutions() {
        Set<JobExecution> executions = new HashSet<>();
        Mockito.when(jobExplorer.findRunningJobExecutions("myJob")).thenReturn(executions);
        jsrJobOperator.getRunningExecutions("myJob");
    }

    @Test
    public void testGetRunningExecutions() {
        Set<JobExecution> executions = new HashSet<>();
        executions.add(new JobExecution(5L));
        Mockito.when(jobExplorer.findRunningJobExecutions("myJob")).thenReturn(executions);
        Assert.assertEquals(5L, jsrJobOperator.getRunningExecutions("myJob").get(0).longValue());
    }

    @Test
    public void testGetStepExecutionsRoseyScenario() {
        JobExecution jobExecution = new JobExecution(5L);
        List<StepExecution> stepExecutions = new ArrayList<>();
        stepExecutions.add(new StepExecution("step1", jobExecution, 1L));
        stepExecutions.add(new StepExecution("step2", jobExecution, 2L));
        jobExecution.addStepExecutions(stepExecutions);
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(jobExecution);
        Mockito.when(jobExplorer.getStepExecution(5L, 1L)).thenReturn(new StepExecution("step1", jobExecution, 1L));
        Mockito.when(jobExplorer.getStepExecution(5L, 2L)).thenReturn(new StepExecution("step2", jobExecution, 2L));
        List<javax.batch.runtime.StepExecution> results = jsrJobOperator.getStepExecutions(5L);
        Assert.assertEquals("step1", results.get(0).getStepName());
        Assert.assertEquals("step2", results.get(1).getStepName());
    }

    @Test(expected = NoSuchJobException.class)
    public void testGetStepExecutionsNoExecutionReturned() {
        jsrJobOperator.getStepExecutions(5L);
    }

    @Test
    public void testGetStepExecutionsPartitionedStepScenario() {
        JobExecution jobExecution = new JobExecution(5L);
        List<StepExecution> stepExecutions = new ArrayList<>();
        stepExecutions.add(new StepExecution("step1", jobExecution, 1L));
        stepExecutions.add(new StepExecution("step2", jobExecution, 2L));
        stepExecutions.add(new StepExecution("step2:partition0", jobExecution, 2L));
        stepExecutions.add(new StepExecution("step2:partition1", jobExecution, 2L));
        stepExecutions.add(new StepExecution("step2:partition2", jobExecution, 2L));
        jobExecution.addStepExecutions(stepExecutions);
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(jobExecution);
        Mockito.when(jobExplorer.getStepExecution(5L, 1L)).thenReturn(new StepExecution("step1", jobExecution, 1L));
        Mockito.when(jobExplorer.getStepExecution(5L, 2L)).thenReturn(new StepExecution("step2", jobExecution, 2L));
        List<javax.batch.runtime.StepExecution> results = jsrJobOperator.getStepExecutions(5L);
        Assert.assertEquals("step1", results.get(0).getStepName());
        Assert.assertEquals("step2", results.get(1).getStepName());
    }

    @Test
    public void testGetStepExecutionsNoStepExecutions() {
        JobExecution jobExecution = new JobExecution(5L);
        Mockito.when(jobExplorer.getJobExecution(5L)).thenReturn(jobExecution);
        List<javax.batch.runtime.StepExecution> results = jsrJobOperator.getStepExecutions(5L);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testStartRoseyScenario() throws Exception {
        javax.batch.runtime.JobExecution execution = AbstractJsrTestCase.runJob("jsrJobOperatorTestJob", new Properties(), JsrJobOperatorTests.TIMEOUT);
        Assert.assertEquals(COMPLETED, execution.getBatchStatus());
    }

    @Test
    public void testStartMultipleTimesSameParameters() throws Exception {
        jsrJobOperator = BatchRuntime.getJobOperator();
        int jobInstanceCountBefore = 0;
        try {
            jobInstanceCountBefore = jsrJobOperator.getJobInstanceCount("myJob3");
        } catch (NoSuchJobException ignore) {
        }
        javax.batch.runtime.JobExecution execution1 = AbstractJsrTestCase.runJob("jsrJobOperatorTestJob", new Properties(), JsrJobOperatorTests.TIMEOUT);
        javax.batch.runtime.JobExecution execution2 = AbstractJsrTestCase.runJob("jsrJobOperatorTestJob", new Properties(), JsrJobOperatorTests.TIMEOUT);
        javax.batch.runtime.JobExecution execution3 = AbstractJsrTestCase.runJob("jsrJobOperatorTestJob", new Properties(), JsrJobOperatorTests.TIMEOUT);
        Assert.assertEquals(COMPLETED, execution1.getBatchStatus());
        Assert.assertEquals(COMPLETED, execution2.getBatchStatus());
        Assert.assertEquals(COMPLETED, execution3.getBatchStatus());
        int jobInstanceCountAfter = jsrJobOperator.getJobInstanceCount("myJob3");
        Assert.assertTrue(((jobInstanceCountAfter - jobInstanceCountBefore) == 3));
    }

    @Test
    public void testRestartRoseyScenario() throws Exception {
        javax.batch.runtime.JobExecution execution = AbstractJsrTestCase.runJob("jsrJobOperatorTestRestartJob", new Properties(), JsrJobOperatorTests.TIMEOUT);
        Assert.assertEquals(FAILED, execution.getBatchStatus());
        execution = AbstractJsrTestCase.restartJob(execution.getExecutionId(), null, JsrJobOperatorTests.TIMEOUT);
        Assert.assertEquals(COMPLETED, execution.getBatchStatus());
    }

    @Test(expected = JobRestartException.class)
    public void testNonRestartableJob() throws Exception {
        javax.batch.runtime.JobExecution jobExecutionStart = AbstractJsrTestCase.runJob("jsrJobOperatorTestNonRestartableJob", new Properties(), JsrJobOperatorTests.TIMEOUT);
        Assert.assertEquals(FAILED, jobExecutionStart.getBatchStatus());
        AbstractJsrTestCase.restartJob(jobExecutionStart.getExecutionId(), null, JsrJobOperatorTests.TIMEOUT);
    }

    @Test(expected = JobRestartException.class)
    public void testRestartAbandoned() throws Exception {
        jsrJobOperator = BatchRuntime.getJobOperator();
        javax.batch.runtime.JobExecution execution = AbstractJsrTestCase.runJob("jsrJobOperatorTestRestartAbandonJob", null, JsrJobOperatorTests.TIMEOUT);
        Assert.assertEquals(FAILED, execution.getBatchStatus());
        jsrJobOperator.abandon(execution.getExecutionId());
        jsrJobOperator.restart(execution.getExecutionId(), null);
    }

    @Test
    public void testGetNoRestartJobParameters() {
        JsrJobOperator jobOperator = ((JsrJobOperator) (jsrJobOperator));
        Properties properties = jobOperator.getJobRestartProperties(null, null);
        Assert.assertTrue(properties.isEmpty());
    }

    @Test
    public void testGetRestartJobParameters() {
        JsrJobOperator jobOperator = ((JsrJobOperator) (jsrJobOperator));
        JobExecution jobExecution = new JobExecution(1L, new JobParametersBuilder().addString("prevKey1", "prevVal1").toJobParameters());
        Properties userProperties = new Properties();
        userProperties.put("userKey1", "userVal1");
        Properties properties = jobOperator.getJobRestartProperties(userProperties, jobExecution);
        Assert.assertTrue(((properties.size()) == 2));
        Assert.assertTrue(properties.getProperty("prevKey1").equals("prevVal1"));
        Assert.assertTrue(properties.getProperty("userKey1").equals("userVal1"));
    }

    @Test
    public void testGetRestartJobParametersWithDefaults() {
        JsrJobOperator jobOperator = ((JsrJobOperator) (jsrJobOperator));
        JobExecution jobExecution = new JobExecution(1L, new JobParametersBuilder().addString("prevKey1", "prevVal1").addString("prevKey2", "prevVal2").toJobParameters());
        Properties defaultProperties = new Properties();
        defaultProperties.setProperty("prevKey2", "not value 2");
        Properties userProperties = new Properties(defaultProperties);
        Properties properties = jobOperator.getJobRestartProperties(userProperties, jobExecution);
        Assert.assertTrue(((properties.size()) == 2));
        Assert.assertTrue(properties.getProperty("prevKey1").equals("prevVal1"));
        Assert.assertTrue(("prevKey2 = " + (properties.getProperty("prevKey2"))), properties.getProperty("prevKey2").equals("not value 2"));
    }

    @Test
    public void testNewJobParametersOverridePreviousRestartParameters() {
        JsrJobOperator jobOperator = ((JsrJobOperator) (jsrJobOperator));
        JobExecution jobExecution = new JobExecution(1L, new JobParametersBuilder().addString("prevKey1", "prevVal1").addString("overrideTest", "jobExecution").toJobParameters());
        Properties userProperties = new Properties();
        userProperties.put("userKey1", "userVal1");
        userProperties.put("overrideTest", "userProperties");
        Properties properties = jobOperator.getJobRestartProperties(userProperties, jobExecution);
        Assert.assertTrue(((properties.size()) == 3));
        Assert.assertTrue(properties.getProperty("prevKey1").equals("prevVal1"));
        Assert.assertTrue(properties.getProperty("userKey1").equals("userVal1"));
        Assert.assertTrue(properties.getProperty("overrideTest").equals("userProperties"));
    }

    @Test(expected = JobStartException.class)
    public void testBeanCreationExceptionOnStart() throws Exception {
        jsrJobOperator = BatchRuntime.getJobOperator();
        try {
            jsrJobOperator.start("jsrJobOperatorTestBeanCreationException", null);
        } catch (JobStartException e) {
            Assert.assertTrue(((e.getCause()) instanceof BeanCreationException));
            throw e;
        }
        Assert.fail("Should have failed");
    }

    @SuppressWarnings("unchecked")
    @Test(expected = JobStartException.class)
    public void testStartUnableToCreateJobExecution() throws Exception {
        Mockito.when(jobRepository.createJobExecution("myJob", null)).thenThrow(RuntimeException.class);
        jsrJobOperator.start("myJob", null);
    }

    @Test
    public void testJobStopRoseyScenario() throws Exception {
        jsrJobOperator = BatchRuntime.getJobOperator();
        long executionId = jsrJobOperator.start("longRunningJob", null);
        // Give the job a chance to get started
        Thread.sleep(1000L);
        jsrJobOperator.stop(executionId);
        // Give the job the chance to finish stopping
        Thread.sleep(1000L);
        Assert.assertEquals(STOPPED, jsrJobOperator.getJobExecution(executionId).getBatchStatus());
    }

    @Test
    public void testApplicationContextClosingAfterJobSuccessful() throws Exception {
        for (int i = 0; i < 3; i++) {
            javax.batch.runtime.JobExecution execution = AbstractJsrTestCase.runJob("contextClosingTests", new Properties(), JsrJobOperatorTests.TIMEOUT);
            Assert.assertEquals(COMPLETED, execution.getBatchStatus());
            // Added to allow time for the context to finish closing before running the job again
            Thread.sleep(1000L);
        }
    }

    public static class LongRunningBatchlet implements Batchlet {
        private boolean stopped = false;

        @Override
        public String process() throws Exception {
            while (!(stopped)) {
                Thread.sleep(250);
            } 
            return null;
        }

        @Override
        public void stop() throws Exception {
            stopped = true;
        }
    }

    public static class FailingBatchlet extends AbstractBatchlet {
        @Override
        public String process() throws Exception {
            throw new RuntimeException("blah");
        }
    }

    public static class MustBeClosedBatchlet extends AbstractBatchlet {
        public static boolean closed = true;

        public MustBeClosedBatchlet() {
            if (!(JsrJobOperatorTests.MustBeClosedBatchlet.closed)) {
                throw new RuntimeException("Batchlet wasn't closed last time");
            }
        }

        public void close() {
            JsrJobOperatorTests.MustBeClosedBatchlet.closed = true;
        }

        @Override
        public String process() throws Exception {
            JsrJobOperatorTests.MustBeClosedBatchlet.closed = false;
            return null;
        }
    }

    @Configuration
    @Import(DataSourceConfiguration.class)
    @EnableBatchProcessing
    public static class BatchConfgiuration {
        @Bean
        public JsrJobOperator jobOperator(JobExplorer jobExplorer, JobRepository jobrepository, DataSource dataSource, PlatformTransactionManager transactionManager) throws Exception {
            JsrJobParametersConverter jobParametersConverter = new JsrJobParametersConverter(dataSource);
            jobParametersConverter.afterPropertiesSet();
            return new JsrJobOperator(jobExplorer, jobrepository, jobParametersConverter, transactionManager);
        }
    }
}

