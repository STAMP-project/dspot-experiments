/**
 * Copyright 2006-2007 the original author or authors.
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
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 *
 * @author Dave Syer
 */
public abstract class AbstractJobDaoTests {
    protected JobInstanceDao jobInstanceDao;

    protected JobExecutionDao jobExecutionDao;

    protected JobParameters jobParameters = new JobParametersBuilder().addString("job.key", "jobKey").addLong("long", ((long) (1))).addDate("date", new Date(7)).addDouble("double", 7.7).toJobParameters();

    protected JobInstance jobInstance;

    protected String jobName = "Job1";

    protected JobExecution jobExecution;

    protected Date jobExecutionStartTime = new Date(System.currentTimeMillis());

    protected JdbcTemplate jdbcTemplate;

    @Transactional
    @Test
    public void testVersionIsNotNullForJob() throws Exception {
        int version = jdbcTemplate.queryForObject(("select version from BATCH_JOB_INSTANCE where JOB_INSTANCE_ID=" + (jobInstance.getId())), Integer.class);
        Assert.assertEquals(0, version);
    }

    @Transactional
    @Test
    public void testVersionIsNotNullForJobExecution() throws Exception {
        int version = jdbcTemplate.queryForObject(("select version from BATCH_JOB_EXECUTION where JOB_EXECUTION_ID=" + (jobExecution.getId())), Integer.class);
        Assert.assertEquals(0, version);
    }

    @Transactional
    @Test
    public void testFindNonExistentJob() {
        // No job should be found since it hasn't been created.
        JobInstance jobInstance = jobInstanceDao.getJobInstance("nonexistentJob", jobParameters);
        Assert.assertNull(jobInstance);
    }

    @Transactional
    @Test
    public void testFindJob() {
        JobInstance instance = jobInstanceDao.getJobInstance(jobName, jobParameters);
        Assert.assertNotNull(instance);
        Assert.assertTrue(jobInstance.equals(instance));
    }

    @Transactional
    @Test
    public void testFindJobWithNullRuntime() {
        try {
            jobInstanceDao.getJobInstance(null, null);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    /**
     * Test that ensures that if you create a job with a given name, then find a
     * job with the same name, but other pieces of the identifier different, you
     * get no result, not the existing one.
     */
    @Transactional
    @Test
    public void testCreateJobWithExistingName() {
        String scheduledJob = "ScheduledJob";
        jobInstanceDao.createJobInstance(scheduledJob, jobParameters);
        // Modifying the key should bring back a completely different
        // JobInstance
        JobParameters tempProps = new JobParametersBuilder().addString("job.key", "testKey1").toJobParameters();
        JobInstance instance;
        instance = jobInstanceDao.getJobInstance(scheduledJob, jobParameters);
        Assert.assertNotNull(instance);
        instance = jobInstanceDao.getJobInstance(scheduledJob, tempProps);
        Assert.assertNull(instance);
    }

    @Transactional
    @Test
    public void testUpdateJobExecution() {
        jobExecution.setStatus(COMPLETED);
        jobExecution.setExitStatus(ExitStatus.COMPLETED);
        jobExecution.setEndTime(new Date(System.currentTimeMillis()));
        jobExecutionDao.updateJobExecution(jobExecution);
        List<JobExecution> executions = jobExecutionDao.findJobExecutions(jobInstance);
        Assert.assertEquals(executions.size(), 1);
        validateJobExecution(jobExecution, executions.get(0));
    }

    @Transactional
    @Test
    public void testSaveJobExecution() {
        List<JobExecution> executions = jobExecutionDao.findJobExecutions(jobInstance);
        Assert.assertEquals(executions.size(), 1);
        validateJobExecution(jobExecution, executions.get(0));
    }

    @Transactional
    @Test
    public void testUpdateInvalidJobExecution() {
        // id is invalid
        JobExecution execution = new JobExecution(jobInstance, ((long) (29432)), jobParameters, null);
        execution.incrementVersion();
        try {
            jobExecutionDao.updateJobExecution(execution);
            Assert.fail("Expected NoSuchBatchDomainObjectException");
        } catch (NoSuchObjectException ex) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testUpdateNullIdJobExecution() {
        JobExecution execution = new JobExecution(jobInstance, jobParameters);
        try {
            jobExecutionDao.updateJobExecution(execution);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testJobWithSimpleJobIdentifier() throws Exception {
        String testJob = "test";
        // Create job.
        jobInstance = jobInstanceDao.createJobInstance(testJob, jobParameters);
        List<Map<String, Object>> jobs = jdbcTemplate.queryForList("SELECT * FROM BATCH_JOB_INSTANCE where JOB_INSTANCE_ID=?", jobInstance.getId());
        Assert.assertEquals(1, jobs.size());
        Assert.assertEquals("test", jobs.get(0).get("JOB_NAME"));
    }

    @Transactional
    @Test
    public void testJobWithDefaultJobIdentifier() throws Exception {
        String testDefaultJob = "testDefault";
        // Create job.
        jobInstance = jobInstanceDao.createJobInstance(testDefaultJob, jobParameters);
        JobInstance instance = jobInstanceDao.getJobInstance(testDefaultJob, jobParameters);
        Assert.assertNotNull(instance);
    }

    @Transactional
    @Test
    public void testFindJobExecutions() {
        List<JobExecution> results = jobExecutionDao.findJobExecutions(jobInstance);
        Assert.assertEquals(results.size(), 1);
        validateJobExecution(jobExecution, results.get(0));
    }

    @Transactional
    @Test
    public void testGetLastJobExecution() {
        JobExecution lastExecution = new JobExecution(jobInstance, jobParameters);
        lastExecution.setStatus(STARTED);
        int JUMP_INTO_FUTURE = 1000;// makes sure start time is 'greatest'

        lastExecution.setCreateTime(new Date(((System.currentTimeMillis()) + JUMP_INTO_FUTURE)));
        jobExecutionDao.saveJobExecution(lastExecution);
        Assert.assertEquals(lastExecution, jobExecutionDao.getLastJobExecution(jobInstance));
        Assert.assertNotNull(lastExecution.getJobParameters());
        Assert.assertEquals("jobKey", lastExecution.getJobParameters().getString("job.key"));
    }

    /**
     * Trying to create instance twice for the same job+parameters causes error
     */
    @Transactional
    @Test
    public void testCreateDuplicateInstance() {
        jobParameters = new JobParameters();
        jobInstanceDao.createJobInstance(jobName, jobParameters);
        try {
            jobInstanceDao.createJobInstance(jobName, jobParameters);
            Assert.fail();
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Transactional
    @Test
    public void testCreationAddsVersion() {
        jobInstance = jobInstanceDao.createJobInstance("testCreationAddsVersion", new JobParameters());
        Assert.assertNotNull(jobInstance.getVersion());
    }

    @Transactional
    @Test
    public void testSaveAddsVersionAndId() {
        JobExecution jobExecution = new JobExecution(jobInstance, jobParameters);
        Assert.assertNull(jobExecution.getId());
        Assert.assertNull(jobExecution.getVersion());
        jobExecutionDao.saveJobExecution(jobExecution);
        Assert.assertNotNull(jobExecution.getId());
        Assert.assertNotNull(jobExecution.getVersion());
    }

    @Transactional
    @Test
    public void testUpdateIncrementsVersion() {
        int version = jobExecution.getVersion();
        jobExecutionDao.updateJobExecution(jobExecution);
        Assert.assertEquals((version + 1), jobExecution.getVersion().intValue());
    }
}

