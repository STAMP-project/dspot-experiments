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
package org.springframework.batch.core.test.repository;


import BatchStatus.FAILED;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.test.AbstractIntegrationTests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/simple-job-launcher-context.xml" })
public class JdbcJobRepositoryTests extends AbstractIntegrationTests {
    private JobSupport job;

    private Set<Long> jobExecutionIds = new HashSet<>();

    private Set<Long> jobIds = new HashSet<>();

    private List<Serializable> list = new ArrayList<>();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JobRepository repository;

    /**
     * Logger
     */
    private final Log logger = LogFactory.getLog(getClass());

    @Test
    public void testFindOrCreateJob() throws Exception {
        job.setName("foo");
        int before = 0;
        JobExecution execution = repository.createJobExecution(job.getName(), new JobParameters());
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BATCH_JOB_INSTANCE", Integer.class);
        Assert.assertEquals((before + 1), after);
        Assert.assertNotNull(execution.getId());
    }

    @Test
    public void testFindOrCreateJobWithExecutionContext() throws Exception {
        job.setName("foo");
        int before = 0;
        JobExecution execution = repository.createJobExecution(job.getName(), new JobParameters());
        execution.getExecutionContext().put("foo", "bar");
        repository.updateExecutionContext(execution);
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BATCH_JOB_EXECUTION_CONTEXT", Integer.class);
        Assert.assertEquals((before + 1), after);
        Assert.assertNotNull(execution.getId());
        JobExecution last = repository.getLastJobExecution(job.getName(), new JobParameters());
        Assert.assertEquals(execution, last);
        Assert.assertEquals(execution.getExecutionContext(), last.getExecutionContext());
    }

    @Test
    public void testFindOrCreateJobConcurrently() throws Exception {
        job.setName("bar");
        int before = 0;
        Assert.assertEquals(0, before);
        long t0 = System.currentTimeMillis();
        try {
            doConcurrentStart();
            Assert.fail("Expected JobExecutionAlreadyRunningException");
        } catch (JobExecutionAlreadyRunningException e) {
            // expected
        }
        long t1 = System.currentTimeMillis();
        JobExecution execution = ((JobExecution) (list.get(0)));
        Assert.assertNotNull(execution);
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BATCH_JOB_INSTANCE", Integer.class);
        Assert.assertNotNull(execution.getId());
        Assert.assertEquals((before + 1), after);
        logger.info((("Duration: " + (t1 - t0)) + " - the second transaction did not block if this number is less than about 1000."));
    }

    @Test
    public void testFindOrCreateJobConcurrentlyWhenJobAlreadyExists() throws Exception {
        job = new JobSupport("test-job");
        job.setRestartable(true);
        job.setName("spam");
        JobExecution execution = repository.createJobExecution(job.getName(), new JobParameters());
        cacheJobIds(execution);
        execution.setEndTime(new Timestamp(System.currentTimeMillis()));
        repository.update(execution);
        execution.setStatus(FAILED);
        int before = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BATCH_JOB_INSTANCE", Integer.class);
        Assert.assertEquals(1, before);
        long t0 = System.currentTimeMillis();
        try {
            doConcurrentStart();
            Assert.fail("Expected JobExecutionAlreadyRunningException");
        } catch (JobExecutionAlreadyRunningException e) {
            // expected
        }
        long t1 = System.currentTimeMillis();
        int after = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM BATCH_JOB_INSTANCE", Integer.class);
        Assert.assertNotNull(execution.getId());
        Assert.assertEquals(before, after);
        logger.info((("Duration: " + (t1 - t0)) + " - the second transaction did not block if this number is less than about 1000."));
    }
}

