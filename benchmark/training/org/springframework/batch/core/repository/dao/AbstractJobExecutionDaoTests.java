/**
 * Copyright 2008-2018 the original author or authors.
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
import BatchStatus.STARTED;
import BatchStatus.STOPPING;
import ExitStatus.UNKNOWN;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;


/**
 * Parent Test Class for {@link JdbcJobExecutionDao} and {@link MapJobExecutionDao}.
 */
public abstract class AbstractJobExecutionDaoTests {
    protected JobExecutionDao dao;

    protected JobInstance jobInstance;

    protected JobExecution execution;

    protected JobParameters jobParameters;

    /**
     * Save and find a job execution.
     */
    @Transactional
    @Test
    public void testSaveAndFind() {
        execution.setStartTime(new Date(System.currentTimeMillis()));
        execution.setLastUpdated(new Date(System.currentTimeMillis()));
        execution.setExitStatus(UNKNOWN);
        execution.setEndTime(new Date(System.currentTimeMillis()));
        dao.saveJobExecution(execution);
        List<JobExecution> executions = dao.findJobExecutions(jobInstance);
        Assert.assertEquals(1, executions.size());
        Assert.assertEquals(execution, executions.get(0));
        assertExecutionsAreEqual(execution, executions.get(0));
    }

    /**
     * Executions should be returned in the reverse order they were saved.
     */
    @Transactional
    @Test
    public void testFindExecutionsOrdering() {
        List<JobExecution> execs = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            JobExecution exec = new JobExecution(jobInstance, jobParameters);
            exec.setCreateTime(new Date(i));
            execs.add(exec);
            dao.saveJobExecution(exec);
        }
        List<JobExecution> retrieved = dao.findJobExecutions(jobInstance);
        Collections.reverse(retrieved);
        for (int i = 0; i < 10; i++) {
            assertExecutionsAreEqual(execs.get(i), retrieved.get(i));
        }
    }

    /**
     * Save and find a job execution.
     */
    @Transactional
    @Test
    public void testFindNonExistentExecutions() {
        List<JobExecution> executions = dao.findJobExecutions(jobInstance);
        Assert.assertEquals(0, executions.size());
    }

    /**
     * Saving sets id to the entity.
     */
    @Transactional
    @Test
    public void testSaveAddsIdAndVersion() {
        Assert.assertNull(execution.getId());
        Assert.assertNull(execution.getVersion());
        dao.saveJobExecution(execution);
        Assert.assertNotNull(execution.getId());
        Assert.assertNotNull(execution.getVersion());
    }

    /**
     * Update and retrieve job execution - check attributes have changed as
     * expected.
     */
    @Transactional
    @Test
    public void testUpdateExecution() {
        execution.setStatus(STARTED);
        dao.saveJobExecution(execution);
        execution.setLastUpdated(new Date(0));
        execution.setStatus(COMPLETED);
        dao.updateJobExecution(execution);
        JobExecution updated = dao.findJobExecutions(jobInstance).get(0);
        Assert.assertEquals(execution, updated);
        Assert.assertEquals(COMPLETED, updated.getStatus());
        assertExecutionsAreEqual(execution, updated);
    }

    /**
     * Check the execution with most recent start time is returned
     */
    @Transactional
    @Test
    public void testGetLastExecution() {
        JobExecution exec1 = new JobExecution(jobInstance, jobParameters);
        exec1.setCreateTime(new Date(0));
        JobExecution exec2 = new JobExecution(jobInstance, jobParameters);
        exec2.setCreateTime(new Date(1));
        dao.saveJobExecution(exec1);
        dao.saveJobExecution(exec2);
        JobExecution last = dao.getLastJobExecution(jobInstance);
        Assert.assertEquals(exec2, last);
    }

    /**
     * Check the execution is returned
     */
    @Transactional
    @Test
    public void testGetMissingLastExecution() {
        JobExecution value = dao.getLastJobExecution(jobInstance);
        Assert.assertNull(value);
    }

    /**
     * Check the execution is returned
     */
    @Transactional
    @Test
    public void testFindRunningExecutions() {
        // Normally completed JobExecution as EndTime is populated
        JobExecution exec = new JobExecution(jobInstance, jobParameters);
        exec.setCreateTime(new Date(0));
        exec.setStartTime(new Date(1L));
        exec.setEndTime(new Date(2L));
        exec.setLastUpdated(new Date(5L));
        dao.saveJobExecution(exec);
        // BATCH-2675
        // Abnormal JobExecution as both StartTime and EndTime are null
        // This can occur when SimpleJobLauncher#run() submission to taskExecutor throws a TaskRejectedException
        exec = new JobExecution(jobInstance, jobParameters);
        exec.setLastUpdated(new Date(5L));
        dao.saveJobExecution(exec);
        // Running JobExecution as StartTime is populated but EndTime is null
        exec = new JobExecution(jobInstance, jobParameters);
        exec.setStartTime(new Date(2L));
        exec.setLastUpdated(new Date(5L));
        exec.createStepExecution("step");
        dao.saveJobExecution(exec);
        StepExecutionDao stepExecutionDao = getStepExecutionDao();
        if (stepExecutionDao != null) {
            for (StepExecution stepExecution : exec.getStepExecutions()) {
                stepExecutionDao.saveStepExecution(stepExecution);
            }
        }
        Set<JobExecution> values = dao.findRunningJobExecutions(exec.getJobInstance().getJobName());
        Assert.assertEquals(1, values.size());
        JobExecution value = values.iterator().next();
        Assert.assertEquals(exec, value);
        Assert.assertEquals(5L, value.getLastUpdated().getTime());
    }

    /**
     * Check the execution is returned
     */
    @Transactional
    @Test
    public void testNoRunningExecutions() {
        Set<JobExecution> values = dao.findRunningJobExecutions("no-such-job");
        Assert.assertEquals(0, values.size());
    }

    /**
     * Check the execution is returned
     */
    @Transactional
    @Test
    public void testGetExecution() {
        JobExecution exec = new JobExecution(jobInstance, jobParameters);
        exec.setCreateTime(new Date(0));
        exec.createStepExecution("step");
        dao.saveJobExecution(exec);
        StepExecutionDao stepExecutionDao = getStepExecutionDao();
        if (stepExecutionDao != null) {
            for (StepExecution stepExecution : exec.getStepExecutions()) {
                stepExecutionDao.saveStepExecution(stepExecution);
            }
        }
        JobExecution value = dao.getJobExecution(exec.getId());
        Assert.assertEquals(exec, value);
        // N.B. the job instance is not re-hydrated in the JDBC case...
    }

    /**
     * Check the execution is returned
     */
    @Transactional
    @Test
    public void testGetMissingExecution() {
        JobExecution value = dao.getJobExecution(54321L);
        Assert.assertNull(value);
    }

    /**
     * Exception should be raised when the version of update argument doesn't
     * match the version of persisted entity.
     */
    @Transactional
    @Test
    public void testConcurrentModificationException() {
        JobExecution exec1 = new JobExecution(jobInstance, jobParameters);
        dao.saveJobExecution(exec1);
        JobExecution exec2 = new JobExecution(jobInstance, jobParameters);
        exec2.setId(exec1.getId());
        exec2.incrementVersion();
        Assert.assertEquals(((Integer) (0)), exec1.getVersion());
        Assert.assertEquals(exec1.getVersion(), exec2.getVersion());
        dao.updateJobExecution(exec1);
        Assert.assertEquals(((Integer) (1)), exec1.getVersion());
        try {
            dao.updateJobExecution(exec2);
            Assert.fail();
        } catch (OptimisticLockingFailureException e) {
            // expected
        }
    }

    /**
     * Successful synchronization from STARTED to STOPPING status.
     */
    @Transactional
    @Test
    public void testSynchronizeStatusUpgrade() {
        JobExecution exec1 = new JobExecution(jobInstance, jobParameters);
        exec1.setStatus(STOPPING);
        dao.saveJobExecution(exec1);
        JobExecution exec2 = new JobExecution(jobInstance, jobParameters);
        Assert.assertTrue(((exec1.getId()) != null));
        exec2.setId(exec1.getId());
        exec2.setStatus(STARTED);
        exec2.setVersion(7);
        Assert.assertTrue(((exec1.getVersion()) != (exec2.getVersion())));
        Assert.assertTrue(((exec1.getStatus()) != (exec2.getStatus())));
        dao.synchronizeStatus(exec2);
        Assert.assertEquals(exec1.getVersion(), exec2.getVersion());
        Assert.assertEquals(exec1.getStatus(), exec2.getStatus());
    }

    /**
     * UNKNOWN status won't be changed by synchronizeStatus, because it is the
     * 'largest' BatchStatus (will not downgrade).
     */
    @Transactional
    @Test
    public void testSynchronizeStatusDowngrade() {
        JobExecution exec1 = new JobExecution(jobInstance, jobParameters);
        exec1.setStatus(STARTED);
        dao.saveJobExecution(exec1);
        JobExecution exec2 = new JobExecution(jobInstance, jobParameters);
        Assert.assertTrue(((exec1.getId()) != null));
        exec2.setId(exec1.getId());
        exec2.setStatus(BatchStatus.UNKNOWN);
        exec2.setVersion(7);
        Assert.assertTrue(((exec1.getVersion()) != (exec2.getVersion())));
        Assert.assertTrue(exec1.getStatus().isLessThan(exec2.getStatus()));
        dao.synchronizeStatus(exec2);
        Assert.assertEquals(exec1.getVersion(), exec2.getVersion());
        Assert.assertEquals(BatchStatus.UNKNOWN, exec2.getStatus());
    }
}

