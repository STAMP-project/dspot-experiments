/**
 * Copyright 2008-2013 the original author or authors.
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


import BatchStatus.STARTED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.transaction.annotation.Transactional;


/**
 * Tests for {@link ExecutionContextDao} implementations.
 */
public abstract class AbstractExecutionContextDaoTests extends AbstractTransactionalJUnit4SpringContextTests {
    private JobInstanceDao jobInstanceDao;

    private JobExecutionDao jobExecutionDao;

    private StepExecutionDao stepExecutionDao;

    private ExecutionContextDao contextDao;

    private JobExecution jobExecution;

    private StepExecution stepExecution;

    @Transactional
    @Test
    public void testSaveAndFindJobContext() {
        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        jobExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(jobExecution);
        ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
        Assert.assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testSaveAndFindExecutionContexts() {
        List<StepExecution> stepExecutions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            JobInstance ji = jobInstanceDao.createJobInstance(("testJob" + i), new JobParameters());
            JobExecution je = new JobExecution(ji, new JobParameters());
            jobExecutionDao.saveJobExecution(je);
            StepExecution se = new StepExecution(("step" + i), je);
            se.setStatus(STARTED);
            se.setReadSkipCount(i);
            se.setProcessSkipCount(i);
            se.setWriteSkipCount(i);
            se.setProcessSkipCount(i);
            se.setRollbackCount(i);
            se.setLastUpdated(new Date(System.currentTimeMillis()));
            se.setReadCount(i);
            se.setFilterCount(i);
            se.setWriteCount(i);
            stepExecutions.add(se);
        }
        stepExecutionDao.saveStepExecutions(stepExecutions);
        contextDao.saveExecutionContexts(stepExecutions);
        for (int i = 0; i < 3; i++) {
            ExecutionContext retrieved = contextDao.getExecutionContext(stepExecutions.get(i).getJobExecution());
            Assert.assertEquals(stepExecutions.get(i).getExecutionContext(), retrieved);
        }
    }

    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void testSaveNullExecutionContexts() {
        contextDao.saveExecutionContexts(null);
    }

    @Transactional
    @Test
    public void testSaveEmptyExecutionContexts() {
        contextDao.saveExecutionContexts(new ArrayList());
    }

    @Transactional
    @Test
    public void testSaveAndFindEmptyJobContext() {
        ExecutionContext ctx = new ExecutionContext();
        jobExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(jobExecution);
        ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
        Assert.assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testUpdateContext() {
        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        jobExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(jobExecution);
        ctx.putLong("longKey", 7);
        contextDao.updateExecutionContext(jobExecution);
        ExecutionContext retrieved = contextDao.getExecutionContext(jobExecution);
        Assert.assertEquals(ctx, retrieved);
        Assert.assertEquals(7, retrieved.getLong("longKey"));
    }

    @Transactional
    @Test
    public void testSaveAndFindStepContext() {
        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        stepExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(stepExecution);
        ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
        Assert.assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testSaveAndFindEmptyStepContext() {
        ExecutionContext ctx = new ExecutionContext();
        stepExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(stepExecution);
        ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
        Assert.assertEquals(ctx, retrieved);
    }

    @Transactional
    @Test
    public void testUpdateStepContext() {
        ExecutionContext ctx = new ExecutionContext(Collections.<String, Object>singletonMap("key", "value"));
        stepExecution.setExecutionContext(ctx);
        contextDao.saveExecutionContext(stepExecution);
        ctx.putLong("longKey", 7);
        contextDao.updateExecutionContext(stepExecution);
        ExecutionContext retrieved = contextDao.getExecutionContext(stepExecution);
        Assert.assertEquals(ctx, retrieved);
        Assert.assertEquals(7, retrieved.getLong("longKey"));
    }

    @Transactional
    @Test
    public void testStoreInteger() {
        ExecutionContext ec = new ExecutionContext();
        ec.put("intValue", new Integer(343232));
        stepExecution.setExecutionContext(ec);
        contextDao.saveExecutionContext(stepExecution);
        ExecutionContext restoredEc = contextDao.getExecutionContext(stepExecution);
        Assert.assertEquals(ec, restoredEc);
    }
}

