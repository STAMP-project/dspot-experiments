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
package org.springframework.batch.core.step.item;


import Step.STEP_TYPE_KEY;
import TaskletStep.TASKLET_TYPE_KEY;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobInterruptedException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamSupport;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.UnexpectedRollbackException;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionSynchronizationManager;


/**
 * Tests for the behavior of TaskletStep in a failure scenario.
 *
 * @author Lucas Ward
 * @author Dave Syer
 * @author David Turanski
 */
public class TaskletStepExceptionTests {
    TaskletStep taskletStep;

    StepExecution stepExecution;

    TaskletStepExceptionTests.UpdateCountingJobRepository jobRepository;

    static RuntimeException taskletException = new RuntimeException("Static planned test exception.");

    static JobInterruptedException interruptedException = new JobInterruptedException("");

    @Test
    public void testApplicationException() throws Exception {
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertEquals(BatchStatus.FAILED.toString(), stepExecution.getExitStatus().getExitCode());
    }

    @Test
    public void testInterrupted() throws Exception {
        taskletStep.setStepExecutionListeners(new StepExecutionListener[]{ new TaskletStepExceptionTests.InterruptionListener() });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.STOPPED, stepExecution.getStatus());
        Assert.assertEquals(BatchStatus.STOPPED.toString(), stepExecution.getExitStatus().getExitCode());
    }

    @Test
    public void testInterruptedWithCustomStatus() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
                contribution.setExitStatus(new ExitStatus("FUNNY"));
                throw new JobInterruptedException("Planned");
            }
        });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.STOPPED, stepExecution.getStatus());
        Assert.assertEquals("FUNNY", stepExecution.getExitStatus().getExitCode());
    }

    @Test
    public void testOpenFailure() throws Exception {
        final RuntimeException exception = new RuntimeException();
        taskletStep.setStreams(new ItemStream[]{ new ItemStreamSupport() {
            @Override
            public void open(ExecutionContext executionContext) throws ItemStreamException {
                throw exception;
            }
        } });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(exception));
        Assert.assertEquals(2, jobRepository.getUpdateCount());
    }

    @Test
    public void testBeforeStepFailure() throws Exception {
        final RuntimeException exception = new RuntimeException();
        taskletStep.setStepExecutionListeners(new StepExecutionListenerSupport[]{ new StepExecutionListenerSupport() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                throw exception;
            }
        } });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(exception));
        Assert.assertEquals(2, jobRepository.getUpdateCount());
    }

    @Test
    public void testAfterStepFailureWhenTaskletSucceeds() throws Exception {
        final RuntimeException exception = new RuntimeException();
        taskletStep.setStepExecutionListeners(new StepExecutionListenerSupport[]{ new StepExecutionListenerSupport() {
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                throw exception;
            }
        } });
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
        Assert.assertFalse(stepExecution.getFailureExceptions().contains(exception));
        Assert.assertEquals(3, jobRepository.getUpdateCount());
    }

    /* Exception in afterStep is ignored (only logged). */
    @Test
    public void testAfterStepFailureWhenTaskletFails() throws Exception {
        final RuntimeException exception = new RuntimeException();
        taskletStep.setStepExecutionListeners(new StepExecutionListenerSupport[]{ new StepExecutionListenerSupport() {
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                throw exception;
            }
        } });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(TaskletStepExceptionTests.taskletException));
        Assert.assertFalse(stepExecution.getFailureExceptions().contains(exception));
        Assert.assertEquals(2, jobRepository.getUpdateCount());
    }

    @Test
    public void testCloseError() throws Exception {
        final RuntimeException exception = new RuntimeException();
        taskletStep.setStreams(new ItemStream[]{ new ItemStreamSupport() {
            @Override
            public void close() throws ItemStreamException {
                super.close();
                throw exception;
            }
        } });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(TaskletStepExceptionTests.taskletException));
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(exception));
        Assert.assertEquals(2, jobRepository.getUpdateCount());
    }

    @SuppressWarnings("serial")
    @Test
    public void testCommitError() throws Exception {
        taskletStep.setTransactionManager(new ResourcelessTransactionManager() {
            @Override
            protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
                throw new RuntimeException("bar");
            }

            @Override
            protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
                throw new RuntimeException("foo");
            }
        });
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                attributes.getStepContext().getStepExecution().getExecutionContext().putString("foo", "bar");
                return RepeatStatus.FINISHED;
            }
        });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("foo", e.getMessage());
        Assert.assertEquals(0, stepExecution.getCommitCount());
        Assert.assertEquals(1, stepExecution.getRollbackCount());// Failed transaction

        // counts as
        // rollback
        Assert.assertEquals(2, stepExecution.getExecutionContext().size());
        Assert.assertTrue(stepExecution.getExecutionContext().containsKey(STEP_TYPE_KEY));
        Assert.assertTrue(stepExecution.getExecutionContext().containsKey(TASKLET_TYPE_KEY));
    }

    @SuppressWarnings("serial")
    @Test
    public void testUnexpectedRollback() throws Exception {
        taskletStep.setTransactionManager(new ResourcelessTransactionManager() {
            @Override
            protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
                super.doRollback(status);
                throw new UnexpectedRollbackException("bar");
            }
        });
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                attributes.getStepContext().getStepExecution().getExecutionContext().putString("foo", "bar");
                return RepeatStatus.FINISHED;
            }
        });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("bar", e.getMessage());
        Assert.assertEquals(0, stepExecution.getCommitCount());
        Assert.assertEquals(1, stepExecution.getRollbackCount());// Failed transaction

        // counts as
        // rollback
        Assert.assertEquals(2, stepExecution.getExecutionContext().size());
        Assert.assertTrue(stepExecution.getExecutionContext().containsKey(STEP_TYPE_KEY));
        Assert.assertTrue(stepExecution.getExecutionContext().containsKey(TASKLET_TYPE_KEY));
    }

    @Test
    public void testRepositoryErrorOnExecutionContext() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        jobRepository.setFailOnUpdateExecutionContext(true);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("Expected exception in step execution context persistence", e.getMessage());
    }

    @Test
    public void testRepositoryErrorOnExecutionContextInTransaction() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        jobRepository.setFailOnUpdateExecutionContext(true);
        jobRepository.setFailInTransaction(true);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("JobRepository failure forcing rollback", e.getMessage());
    }

    @Test
    public void testRepositoryErrorOnExecutionContextInTransactionRollbackFailed() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        taskletStep.setTransactionManager(new TaskletStepExceptionTests.FailingRollbackTransactionManager());
        jobRepository.setFailOnUpdateExecutionContext(true);
        jobRepository.setFailInTransaction(true);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("Expected exception in rollback", e.getMessage());
    }

    @Test
    public void testRepositoryErrorOnUpdateStepExecution() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        jobRepository.setFailOnUpdateStepExecution(2);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("Expected exception in step execution persistence", e.getMessage());
    }

    @Test
    public void testRepositoryErrorOnUpdateStepExecutionInTransaction() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        jobRepository.setFailOnUpdateStepExecution(1);
        jobRepository.setFailInTransaction(true);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.FAILED, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("JobRepository failure forcing rollback", e.getMessage());
    }

    @Test
    public void testRepositoryErrorOnUpdateStepExecutionInTransactionRollbackFailed() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                return RepeatStatus.FINISHED;
            }
        });
        taskletStep.setTransactionManager(new TaskletStepExceptionTests.FailingRollbackTransactionManager());
        jobRepository.setFailOnUpdateStepExecution(1);
        jobRepository.setFailInTransaction(true);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("Expected exception in rollback", e.getMessage());
    }

    @Test
    public void testRepositoryErrorOnFailure() throws Exception {
        taskletStep.setTasklet(new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext attributes) throws Exception {
                throw new RuntimeException("Tasklet exception");
            }
        });
        jobRepository.setFailOnUpdateExecutionContext(true);
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Throwable e = stepExecution.getFailureExceptions().get(0);
        Assert.assertEquals("Expected exception in step execution context persistence", e.getMessage());
    }

    @Test
    public void testUpdateError() throws Exception {
        final RuntimeException exception = new RuntimeException();
        taskletStep.setJobRepository(new TaskletStepExceptionTests.UpdateCountingJobRepository() {
            boolean firstCall = true;

            @Override
            public void update(StepExecution arg0) {
                if (firstCall) {
                    firstCall = false;
                    return;
                }
                throw exception;
            }
        });
        taskletStep.execute(stepExecution);
        Assert.assertEquals(BatchStatus.UNKNOWN, stepExecution.getStatus());
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(exception));
        Assert.assertTrue(stepExecution.getFailureExceptions().contains(TaskletStepExceptionTests.taskletException));
    }

    private static class ExceptionTasklet implements Tasklet {
        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            throw TaskletStepExceptionTests.taskletException;
        }
    }

    private static class InterruptionListener extends StepExecutionListenerSupport {
        @Override
        public void beforeStep(StepExecution stepExecution) {
            stepExecution.setTerminateOnly();
        }
    }

    private static class UpdateCountingJobRepository implements JobRepository {
        private int updateCount = 0;

        private boolean failOnUpdateContext = false;

        private int failOnUpdateExecution = -1;

        private boolean failInTransaction = false;

        public void setFailOnUpdateExecutionContext(boolean failOnUpdate) {
            this.failOnUpdateContext = failOnUpdate;
        }

        public void setFailOnUpdateStepExecution(int failOnUpdate) {
            this.failOnUpdateExecution = failOnUpdate;
        }

        public void setFailInTransaction(boolean failInTransaction) {
            this.failInTransaction = failInTransaction;
        }

        @Override
        public void add(StepExecution stepExecution) {
        }

        @Override
        public JobExecution createJobExecution(String jobName, JobParameters jobParameters) throws JobExecutionAlreadyRunningException, JobInstanceAlreadyCompleteException, JobRestartException {
            return null;
        }

        @Override
        public StepExecution getLastStepExecution(JobInstance jobInstance, String stepName) {
            return null;
        }

        @Override
        public int getStepExecutionCount(JobInstance jobInstance, String stepName) {
            return 0;
        }

        @Override
        public boolean isJobInstanceExists(String jobName, JobParameters jobParameters) {
            return false;
        }

        @Override
        public void update(JobExecution jobExecution) {
        }

        @Override
        public void update(StepExecution stepExecution) {
            if (((updateCount)++) == (failOnUpdateExecution)) {
                if ((!(failInTransaction)) || ((failInTransaction) && (TransactionSynchronizationManager.isActualTransactionActive()))) {
                    throw new RuntimeException("Expected exception in step execution persistence");
                }
            }
        }

        @Override
        public void updateExecutionContext(StepExecution stepExecution) {
            if (failOnUpdateContext) {
                if ((!(failInTransaction)) || ((failInTransaction) && (TransactionSynchronizationManager.isActualTransactionActive()))) {
                    throw new RuntimeException("Expected exception in step execution context persistence");
                }
            }
        }

        public int getUpdateCount() {
            return updateCount;
        }

        @Override
        public JobExecution getLastJobExecution(String jobName, JobParameters jobParameters) {
            return null;
        }

        @Override
        public void updateExecutionContext(JobExecution jobExecution) {
        }

        @Override
        public void addAll(Collection<StepExecution> stepExecutions) {
        }

        @Override
        public JobInstance createJobInstance(String jobName, JobParameters jobParameters) {
            return null;
        }

        @Override
        public JobExecution createJobExecution(JobInstance jobInstance, JobParameters jobParameters, String jobConfigurationLocation) {
            return null;
        }
    }

    @SuppressWarnings("serial")
    private static class FailingRollbackTransactionManager extends ResourcelessTransactionManager {
        @Override
        protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
            super.doRollback(status);
            throw new RuntimeException("Expected exception in rollback");
        }
    }
}

