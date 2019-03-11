/**
 * Copyright 2006-2019 the original author or authors.
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


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STOPPED;
import BatchStatus.UNKNOWN;
import ExitStatus.NOOP;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.dao.ExecutionContextDao;
import org.springframework.batch.core.repository.dao.JobExecutionDao;
import org.springframework.batch.core.repository.dao.JobInstanceDao;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.core.step.StepSupport;
import org.springframework.batch.item.ExecutionContext;


/**
 * Tests for DefaultJobLifecycle. MapJobDao and MapStepExecutionDao are used
 * instead of a mock repository to test that status is being stored correctly.
 *
 * @author Lucas Ward
 * @author Will Schipp
 * @author Mahmoud Ben Hassine
 */
public class SimpleJobTests {
    private JobRepository jobRepository;

    private JobInstanceDao jobInstanceDao;

    private JobExecutionDao jobExecutionDao;

    private StepExecutionDao stepExecutionDao;

    private ExecutionContextDao ecDao;

    private List<Serializable> list = new ArrayList<>();

    private JobInstance jobInstance;

    private JobExecution jobExecution;

    private StepExecution stepExecution1;

    private StepExecution stepExecution2;

    private SimpleJobTests.StubStep step1;

    private SimpleJobTests.StubStep step2;

    private JobParameters jobParameters = new JobParameters();

    private SimpleJob job;

    /**
     * Test method for {@link SimpleJob#setSteps(java.util.List)}.
     */
    @Test
    public void testSetSteps() {
        job.setSteps(Collections.singletonList(((Step) (new StepSupport("step")))));
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
    }

    /**
     * Test method for {@link SimpleJob#setSteps(java.util.List)}.
     */
    @Test
    public void testGetSteps() {
        Assert.assertEquals(2, job.getStepNames().size());
    }

    /**
     * Test method for
     * {@link SimpleJob#addStep(org.springframework.batch.core.Step)}.
     */
    @Test
    public void testAddStep() {
        job.setSteps(Collections.<Step>emptyList());
        job.addStep(new StepSupport("step"));
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
    }

    // Test to ensure the exit status returned by the last step is returned
    @Test
    public void testExitStatusReturned() throws JobExecutionException {
        final ExitStatus customStatus = new ExitStatus("test");
        Step testStep = new Step() {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException {
                stepExecution.setExitStatus(customStatus);
            }

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public int getStartLimit() {
                return 1;
            }

            @Override
            public boolean isAllowStartIfComplete() {
                return false;
            }
        };
        List<Step> steps = new ArrayList<>();
        steps.add(testStep);
        job.setSteps(steps);
        job.execute(jobExecution);
        Assert.assertEquals(customStatus, jobExecution.getExitStatus());
    }

    @Test
    public void testRunNormally() throws Exception {
        step1.setStartLimit(5);
        step2.setStartLimit(5);
        job.execute(jobExecution);
        Assert.assertEquals(2, list.size());
        checkRepository(COMPLETED);
        Assert.assertNotNull(jobExecution.getEndTime());
        Assert.assertNotNull(jobExecution.getStartTime());
        Assert.assertTrue(step1.passedInJobContext.isEmpty());
        Assert.assertFalse(step2.passedInJobContext.isEmpty());
    }

    @Test
    public void testRunNormallyWithListener() throws Exception {
        job.setJobExecutionListeners(new JobExecutionListenerSupport[]{ new JobExecutionListenerSupport() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                list.add("before");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                list.add("after");
            }
        } });
        job.execute(jobExecution);
        Assert.assertEquals(4, list.size());
    }

    @Test
    public void testRunWithSimpleStepExecutor() throws Exception {
        job.setJobRepository(jobRepository);
        // do not set StepExecutorFactory...
        step1.setStartLimit(5);
        step2.setStartLimit(5);
        job.execute(jobExecution);
        Assert.assertEquals(2, list.size());
        checkRepository(COMPLETED, ExitStatus.COMPLETED);
    }

    @Test
    public void testExecutionContextIsSet() throws Exception {
        testRunNormally();
        Assert.assertEquals(jobInstance, jobExecution.getJobInstance());
        Assert.assertEquals(2, jobExecution.getStepExecutions().size());
        Assert.assertEquals(step1.getName(), stepExecution1.getStepName());
        Assert.assertEquals(step2.getName(), stepExecution2.getStepName());
    }

    @Test
    public void testInterrupted() throws Exception {
        step1.setStartLimit(5);
        step2.setStartLimit(5);
        final JobInterruptedException exception = new JobInterruptedException("Interrupt!");
        step1.setProcessException(exception);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(exception, jobExecution.getStepExecutions().iterator().next().getFailureExceptions().get(0));
        Assert.assertEquals(0, list.size());
        checkRepository(STOPPED, ExitStatus.STOPPED);
    }

    @Test
    public void testInterruptedAfterUnknownStatus() throws Exception {
        step1.setStartLimit(5);
        step2.setStartLimit(5);
        final JobInterruptedException exception = new JobInterruptedException("Interrupt!", BatchStatus.UNKNOWN);
        step1.setProcessException(exception);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(exception, jobExecution.getStepExecutions().iterator().next().getFailureExceptions().get(0));
        Assert.assertEquals(0, list.size());
        checkRepository(UNKNOWN, ExitStatus.STOPPED);
    }

    @Test
    public void testFailed() throws Exception {
        step1.setStartLimit(5);
        step2.setStartLimit(5);
        final RuntimeException exception = new RuntimeException("Foo!");
        step1.setProcessException(exception);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(exception, jobExecution.getAllFailureExceptions().get(0));
        Assert.assertEquals(0, list.size());
        Assert.assertEquals(FAILED, jobExecution.getStatus());
        checkRepository(FAILED, ExitStatus.FAILED);
    }

    @Test
    public void testFailedWithListener() throws Exception {
        job.setJobExecutionListeners(new JobExecutionListenerSupport[]{ new JobExecutionListenerSupport() {
            @Override
            public void afterJob(JobExecution jobExecution) {
                list.add("afterJob");
            }
        } });
        final RuntimeException exception = new RuntimeException("Foo!");
        step1.setProcessException(exception);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(exception, jobExecution.getAllFailureExceptions().get(0));
        Assert.assertEquals(1, list.size());
        checkRepository(FAILED, ExitStatus.FAILED);
    }

    @Test
    public void testFailedWithError() throws Exception {
        step1.setStartLimit(5);
        step2.setStartLimit(5);
        final Error exception = new Error("Foo!");
        step1.setProcessException(exception);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Assert.assertEquals(exception, jobExecution.getAllFailureExceptions().get(0));
        Assert.assertEquals(0, list.size());
        checkRepository(FAILED, ExitStatus.FAILED);
    }

    @Test
    public void testStepShouldNotStart() throws Exception {
        // Start policy will return false, keeping the step from being started.
        step1.setStartLimit(0);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getFailureExceptions().size());
        Throwable ex = jobExecution.getFailureExceptions().get(0);
        Assert.assertTrue(("Wrong message in exception: " + (ex.getMessage())), ((ex.getMessage().indexOf("start limit exceeded")) >= 0));
    }

    @Test
    public void testStepAlreadyComplete() throws Exception {
        stepExecution1.setStatus(COMPLETED);
        jobRepository.add(stepExecution1);
        jobExecution.setEndTime(new Date());
        jobRepository.update(jobExecution);
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        job.execute(jobExecution);
        Assert.assertEquals(0, jobExecution.getFailureExceptions().size());
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        Assert.assertEquals(stepExecution2.getStepName(), jobExecution.getStepExecutions().iterator().next().getStepName());
    }

    @Test
    public void testStepAlreadyCompleteInSameExecution() throws Exception {
        List<Step> steps = new ArrayList<>();
        steps.add(step1);
        steps.add(step2);
        // Two steps with the same name should both be executed, since
        // the user might actually want it to happen twice.  On a restart
        // it would be executed twice again, even if it failed on the
        // second execution.  This seems reasonable.
        steps.add(step2);
        job.setSteps(steps);
        job.execute(jobExecution);
        Assert.assertEquals(0, jobExecution.getFailureExceptions().size());
        Assert.assertEquals(3, jobExecution.getStepExecutions().size());
        Assert.assertEquals(stepExecution1.getStepName(), jobExecution.getStepExecutions().iterator().next().getStepName());
    }

    @Test
    public void testNoSteps() throws Exception {
        job.setSteps(new ArrayList());
        job.execute(jobExecution);
        ExitStatus exitStatus = jobExecution.getExitStatus();
        Assert.assertTrue(("Wrong message in execution: " + exitStatus), ((exitStatus.getExitDescription().indexOf("no steps configured")) >= 0));
    }

    @Test
    public void testNotExecutedIfAlreadyStopped() throws Exception {
        jobExecution.stop();
        job.execute(jobExecution);
        Assert.assertEquals(0, list.size());
        checkRepository(STOPPED, NOOP);
        ExitStatus exitStatus = jobExecution.getExitStatus();
        Assert.assertEquals(NOOP.getExitCode(), exitStatus.getExitCode());
    }

    @Test
    public void testRestart() throws Exception {
        step1.setAllowStartIfComplete(true);
        final RuntimeException exception = new RuntimeException("Foo!");
        step2.setProcessException(exception);
        job.execute(jobExecution);
        Throwable e = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertSame(exception, e);
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        job.execute(jobExecution);
        e = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertSame(exception, e);
        Assert.assertTrue(step1.passedInStepContext.isEmpty());
        Assert.assertFalse(step2.passedInStepContext.isEmpty());
    }

    @Test
    public void testRestartWithNullParameter() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addString("foo", null).toJobParameters();
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        jobInstance = jobExecution.getJobInstance();
        step1.setAllowStartIfComplete(true);
        final RuntimeException exception = new RuntimeException("Foo!");
        step2.setProcessException(exception);
        job.execute(jobExecution);
        Throwable e = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertSame(exception, e);
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        job.execute(jobExecution);
        e = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertSame(exception, e);
        Assert.assertTrue(step1.passedInStepContext.isEmpty());
        Assert.assertFalse(step2.passedInStepContext.isEmpty());
    }

    @Test
    public void testInterruptWithListener() throws Exception {
        step1.setProcessException(new JobInterruptedException("job interrupted!"));
        JobExecutionListener listener = Mockito.mock(JobExecutionListener.class);
        listener.beforeJob(jobExecution);
        listener.afterJob(jobExecution);
        job.setJobExecutionListeners(new JobExecutionListener[]{ listener });
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
    }

    /**
     * Execution context should be restored on restart.
     */
    @Test
    public void testRestartAndExecutionContextRestored() throws Exception {
        job.setRestartable(true);
        step1.setAllowStartIfComplete(true);
        final RuntimeException exception = new RuntimeException("Foo!");
        step2.setProcessException(exception);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Throwable e = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertSame(exception, e);
        Assert.assertTrue(step1.passedInJobContext.isEmpty());
        Assert.assertFalse(step2.passedInJobContext.isEmpty());
        Assert.assertFalse(jobExecution.getExecutionContext().isEmpty());
        jobExecution = jobRepository.createJobExecution(job.getName(), jobParameters);
        job.execute(jobExecution);
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        e = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertSame(exception, e);
        Assert.assertFalse(step1.passedInJobContext.isEmpty());
        Assert.assertFalse(step2.passedInJobContext.isEmpty());
    }

    @Test
    public void testInterruptJob() throws Exception {
        step1 = new SimpleJobTests.StubStep("interruptStep", jobRepository) {
            @Override
            public void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
                stepExecution.getJobExecution().stop();
                super.execute(stepExecution);
            }
        };
        job.setSteps(Arrays.asList(new Step[]{ step1, step2 }));
        job.execute(jobExecution);
        Assert.assertEquals(STOPPED, jobExecution.getStatus());
        Assert.assertEquals(1, jobExecution.getAllFailureExceptions().size());
        Throwable expected = jobExecution.getAllFailureExceptions().get(0);
        Assert.assertTrue(("Wrong exception " + expected), (expected instanceof JobInterruptedException));
        Assert.assertEquals("JobExecution interrupted.", expected.getMessage());
        Assert.assertNull("Second step was not supposed to be executed", step2.passedInStepContext);
    }

    @Test
    public void testGetStepExists() {
        step1 = new SimpleJobTests.StubStep("step1", jobRepository);
        step2 = new SimpleJobTests.StubStep("step2", jobRepository);
        job.setSteps(Arrays.asList(new Step[]{ step1, step2 }));
        Step step = job.getStep("step2");
        Assert.assertNotNull(step);
        Assert.assertEquals("step2", step.getName());
    }

    @Test
    public void testGetStepNotExists() {
        step1 = new SimpleJobTests.StubStep("step1", jobRepository);
        step2 = new SimpleJobTests.StubStep("step2", jobRepository);
        job.setSteps(Arrays.asList(new Step[]{ step1, step2 }));
        Step step = job.getStep("foo");
        Assert.assertNull(step);
    }

    private static class StubStep extends StepSupport {
        private Runnable runnable;

        private Throwable exception;

        private JobRepository jobRepository;

        private ExecutionContext passedInStepContext;

        private ExecutionContext passedInJobContext;

        /**
         *
         *
         * @param string
         * 		
         */
        public StubStep(String string, JobRepository jobRepository) {
            super(string);
            this.jobRepository = jobRepository;
        }

        /**
         *
         *
         * @param exception
         * 		
         */
        public void setProcessException(Throwable exception) {
            this.exception = exception;
        }

        /**
         *
         *
         * @param runnable
         * 		
         */
        public void setCallback(Runnable runnable) {
            this.runnable = runnable;
        }

        /* (non-Javadoc)

        @see org.springframework.batch.core.step.StepSupport#execute(org.
        springframework.batch.core.StepExecution)
         */
        @Override
        public void execute(StepExecution stepExecution) throws JobInterruptedException, UnexpectedJobExecutionException {
            passedInJobContext = new ExecutionContext(stepExecution.getJobExecution().getExecutionContext());
            passedInStepContext = new ExecutionContext(stepExecution.getExecutionContext());
            stepExecution.getExecutionContext().putString("stepKey", "stepValue");
            stepExecution.getJobExecution().getExecutionContext().putString("jobKey", "jobValue");
            jobRepository.update(stepExecution);
            jobRepository.updateExecutionContext(stepExecution);
            if ((exception) instanceof JobInterruptedException) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(getStatus());
                stepExecution.addFailureException(exception);
                throw ((JobInterruptedException) (exception));
            }
            if ((exception) instanceof RuntimeException) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(FAILED);
                stepExecution.addFailureException(exception);
                return;
            }
            if ((exception) instanceof Error) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(FAILED);
                stepExecution.addFailureException(exception);
                return;
            }
            if ((exception) instanceof JobInterruptedException) {
                stepExecution.setExitStatus(ExitStatus.FAILED);
                stepExecution.setStatus(FAILED);
                stepExecution.addFailureException(exception);
                return;
            }
            if ((runnable) != null) {
                runnable.run();
            }
            stepExecution.setExitStatus(ExitStatus.COMPLETED);
            stepExecution.setStatus(COMPLETED);
            jobRepository.update(stepExecution);
            jobRepository.updateExecutionContext(stepExecution);
        }
    }
}

