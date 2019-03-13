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
package org.springframework.batch.core;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STARTING;
import ExitStatus.UNKNOWN;
import java.util.Arrays;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationUtils;


/**
 *
 *
 * @author Dave Syer
 * @author Dimitrios Liapis
 */
public class JobExecutionTests {
    private JobExecution execution = new JobExecution(new JobInstance(new Long(11), "foo"), new Long(12), new JobParameters(), null);

    @Test
    public void testJobExecution() {
        Assert.assertNull(getId());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getEndTime()}.
     */
    @Test
    public void testGetEndTime() {
        Assert.assertNull(execution.getEndTime());
        execution.setEndTime(new Date(100L));
        Assert.assertEquals(100L, execution.getEndTime().getTime());
    }

    @Test
    public void testGetJobConfigurationName() {
        execution = new JobExecution(new JobInstance(null, "foo"), null, "/META-INF/batch-jobs/someJob.xml");
        Assert.assertEquals("/META-INF/batch-jobs/someJob.xml", execution.getJobConfigurationName());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getEndTime()}.
     */
    @Test
    public void testIsRunning() {
        execution.setStartTime(new Date());
        Assert.assertTrue(execution.isRunning());
        execution.setEndTime(new Date(100L));
        Assert.assertFalse(execution.isRunning());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getEndTime()}.
     */
    @Test
    public void testIsRunningWithStoppedExecution() {
        execution.setStartTime(new Date());
        Assert.assertTrue(execution.isRunning());
        execution.stop();
        Assert.assertTrue(execution.isRunning());
        Assert.assertTrue(execution.isStopping());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getStartTime()}.
     */
    @Test
    public void testGetStartTime() {
        execution.setStartTime(new Date(0L));
        Assert.assertEquals(0L, execution.getStartTime().getTime());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getStatus()}.
     */
    @Test
    public void testGetStatus() {
        Assert.assertEquals(STARTING, execution.getStatus());
        execution.setStatus(COMPLETED);
        Assert.assertEquals(COMPLETED, execution.getStatus());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getStatus()}.
     */
    @Test
    public void testUpgradeStatus() {
        Assert.assertEquals(STARTING, execution.getStatus());
        execution.upgradeStatus(COMPLETED);
        Assert.assertEquals(COMPLETED, execution.getStatus());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getStatus()}.
     */
    @Test
    public void testDowngradeStatus() {
        execution.setStatus(FAILED);
        execution.upgradeStatus(COMPLETED);
        Assert.assertEquals(FAILED, execution.getStatus());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getJobId()}.
     */
    @Test
    public void testGetJobId() {
        Assert.assertEquals(11, execution.getJobId().longValue());
        execution = new JobExecution(new JobInstance(new Long(23), "testJob"), null, new JobParameters(), null);
        Assert.assertEquals(23, execution.getJobId().longValue());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getJobId()}.
     */
    @Test
    public void testGetJobIdForNullJob() {
        execution = new JobExecution(((JobInstance) (null)), ((JobParameters) (null)));
        Assert.assertEquals(null, execution.getJobId());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getJobId()}.
     */
    @Test
    public void testGetJob() {
        Assert.assertNotNull(execution.getJobInstance());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getExitStatus()}.
     */
    @Test
    public void testGetExitCode() {
        Assert.assertEquals(UNKNOWN, execution.getExitStatus());
        execution.setExitStatus(new ExitStatus("23"));
        Assert.assertEquals("23", execution.getExitStatus().getExitCode());
    }

    @Test
    public void testContextContainsInfo() throws Exception {
        Assert.assertEquals("foo", execution.getJobInstance().getJobName());
    }

    @Test
    public void testAddAndRemoveStepExecution() throws Exception {
        Assert.assertEquals(0, execution.getStepExecutions().size());
        execution.createStepExecution("step");
        Assert.assertEquals(1, execution.getStepExecutions().size());
    }

    @Test
    public void testStepExecutionsWithSameName() throws Exception {
        Assert.assertEquals(0, execution.getStepExecutions().size());
        execution.createStepExecution("step");
        Assert.assertEquals(1, execution.getStepExecutions().size());
        execution.createStepExecution("step");
        Assert.assertEquals(2, execution.getStepExecutions().size());
    }

    @Test
    public void testSetStepExecutions() throws Exception {
        Assert.assertEquals(0, execution.getStepExecutions().size());
        execution.addStepExecutions(Arrays.asList(new StepExecution("step", execution)));
        Assert.assertEquals(1, execution.getStepExecutions().size());
    }

    @Test
    public void testSetStepExecutionsWithIds() throws Exception {
        Assert.assertEquals(0, execution.getStepExecutions().size());
        new StepExecution("step", execution, 1L);
        Assert.assertEquals(1, execution.getStepExecutions().size());
        new StepExecution("step", execution, 2L);
        Assert.assertEquals(2, execution.getStepExecutions().size());
    }

    @Test
    public void testStop() throws Exception {
        StepExecution stepExecution = execution.createStepExecution("step");
        Assert.assertFalse(stepExecution.isTerminateOnly());
        execution.stop();
        Assert.assertTrue(stepExecution.isTerminateOnly());
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertTrue("JobExecution string does not contain id", ((execution.toString().indexOf("id=")) >= 0));
        Assert.assertTrue(("JobExecution string does not contain name: " + (execution)), ((execution.toString().indexOf("foo")) >= 0));
    }

    @Test
    public void testToStringWithNullJob() throws Exception {
        execution = new JobExecution(new JobInstance(null, "foo"), null);
        Assert.assertTrue("JobExecution string does not contain id", ((execution.toString().indexOf("id=")) >= 0));
        Assert.assertTrue(("JobExecution string does not contain job: " + (execution)), ((execution.toString().indexOf("job=")) >= 0));
    }

    @Test
    public void testSerialization() {
        byte[] serialized = SerializationUtils.serialize(execution);
        JobExecution deserialize = ((JobExecution) (SerializationUtils.deserialize(serialized)));
        Assert.assertEquals(execution, deserialize);
        Assert.assertNotNull(deserialize.createStepExecution("foo"));
        Assert.assertNotNull(deserialize.getFailureExceptions());
    }
}

