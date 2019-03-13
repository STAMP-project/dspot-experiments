/**
 * Copyright 2006-2014 the original author or authors.
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
import ExitStatus.EXECUTING;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.step.StepSupport;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.util.SerializationUtils;

import static ExitStatus.NOOP;


/**
 *
 *
 * @author Dave Syer
 */
public class StepExecutionTests {
    private StepExecution execution = newStepExecution(new StepSupport("stepName"), new Long(23));

    private StepExecution blankExecution = newStepExecution(new StepSupport("blank"), null);

    private ExecutionContext foobarEc = new ExecutionContext();

    @Test
    public void testStepExecution() {
        Assert.assertNull(new StepExecution("step", null).getId());
    }

    @Test
    public void testStepExecutionWithNullId() {
        Assert.assertNull(new StepExecution("stepName", new JobExecution(new JobInstance(null, "foo"), null)).getId());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getEndTime()}.
     */
    @Test
    public void testGetEndTime() {
        Assert.assertNull(execution.getEndTime());
        execution.setEndTime(new Date(0L));
        Assert.assertEquals(0L, execution.getEndTime().getTime());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getStartTime()}.
     */
    @Test
    public void testGetStartTime() {
        Assert.assertNotNull(execution.getStartTime());
        execution.setStartTime(new Date(10L));
        Assert.assertEquals(10L, execution.getStartTime().getTime());
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
     * {@link org.springframework.batch.core.JobExecution#getJobId()}.
     */
    @Test
    public void testGetJobId() {
        Assert.assertEquals(23, execution.getJobExecutionId().longValue());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.JobExecution#getExitStatus()}.
     */
    @Test
    public void testGetExitCode() {
        Assert.assertEquals(EXECUTING, execution.getExitStatus());
        execution.setExitStatus(ExitStatus.COMPLETED);
        Assert.assertEquals(ExitStatus.COMPLETED, execution.getExitStatus());
    }

    /**
     * Test method for
     * {@link org.springframework.batch.core.StepExecution#getCommitCount()}.
     */
    @Test
    public void testGetCommitCount() {
        execution.setCommitCount(123);
        Assert.assertEquals(123, execution.getCommitCount());
    }

    @Test
    public void testGetFilterCount() {
        execution.setFilterCount(123);
        Assert.assertEquals(123, execution.getFilterCount());
    }

    @Test
    public void testGetJobExecution() throws Exception {
        Assert.assertNotNull(execution.getJobExecution());
    }

    @Test
    public void testApplyContribution() throws Exception {
        StepContribution contribution = execution.createStepContribution();
        contribution.incrementReadSkipCount();
        contribution.incrementWriteSkipCount();
        contribution.incrementReadCount();
        contribution.incrementWriteCount(7);
        contribution.incrementFilterCount(1);
        execution.apply(contribution);
        Assert.assertEquals(1, execution.getReadSkipCount());
        Assert.assertEquals(1, execution.getWriteSkipCount());
        Assert.assertEquals(1, execution.getReadCount());
        Assert.assertEquals(7, execution.getWriteCount());
        Assert.assertEquals(1, execution.getFilterCount());
    }

    @Test
    public void testTerminateOnly() throws Exception {
        Assert.assertFalse(execution.isTerminateOnly());
        execution.setTerminateOnly();
        Assert.assertTrue(execution.isTerminateOnly());
    }

    @Test
    public void testNullNameIsIllegal() throws Exception {
        try {
            new StepExecution(null, new JobExecution(new JobInstance(null, "job"), null));
            Assert.fail();
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testToString() throws Exception {
        Assert.assertTrue(("Should contain read count: " + (execution.toString())), ((execution.toString().indexOf("read")) >= 0));
        Assert.assertTrue(("Should contain write count: " + (execution.toString())), ((execution.toString().indexOf("write")) >= 0));
        Assert.assertTrue(("Should contain filter count: " + (execution.toString())), ((execution.toString().indexOf("filter")) >= 0));
        Assert.assertTrue(("Should contain commit count: " + (execution.toString())), ((execution.toString().indexOf("commit")) >= 0));
        Assert.assertTrue(("Should contain rollback count: " + (execution.toString())), ((execution.toString().indexOf("rollback")) >= 0));
    }

    @Test
    public void testExecutionContext() throws Exception {
        Assert.assertNotNull(execution.getExecutionContext());
        ExecutionContext context = new ExecutionContext();
        context.putString("foo", "bar");
        execution.setExecutionContext(context);
        Assert.assertEquals("bar", execution.getExecutionContext().getString("foo"));
    }

    @Test
    public void testEqualsWithSameName() throws Exception {
        Step step = new StepSupport("stepName");
        Entity stepExecution1 = newStepExecution(step, 11L, 4L);
        Entity stepExecution2 = newStepExecution(step, 11L, 5L);
        Assert.assertFalse(stepExecution1.equals(stepExecution2));
    }

    @Test
    public void testEqualsWithSameIdentifier() throws Exception {
        Step step = new StepSupport("stepName");
        Entity stepExecution1 = newStepExecution(step, new Long(11));
        Entity stepExecution2 = newStepExecution(step, new Long(11));
        Assert.assertEquals(stepExecution1, stepExecution2);
    }

    @Test
    public void testEqualsWithNull() throws Exception {
        Entity stepExecution = newStepExecution(new StepSupport("stepName"), new Long(11));
        Assert.assertFalse(stepExecution.equals(null));
    }

    @Test
    public void testEqualsWithNullIdentifiers() throws Exception {
        Entity stepExecution = newStepExecution(new StepSupport("stepName"), new Long(11));
        Assert.assertFalse(stepExecution.equals(blankExecution));
    }

    @Test
    public void testEqualsWithNullJob() throws Exception {
        Entity stepExecution = newStepExecution(new StepSupport("stepName"), new Long(11));
        Assert.assertFalse(stepExecution.equals(blankExecution));
    }

    @Test
    public void testEqualsWithSelf() throws Exception {
        Assert.assertTrue(execution.equals(execution));
    }

    @Test
    public void testEqualsWithDifferent() throws Exception {
        Entity stepExecution = newStepExecution(new StepSupport("foo"), new Long(13));
        Assert.assertFalse(execution.equals(stepExecution));
    }

    @Test
    public void testEqualsWithNullStepId() throws Exception {
        Step step = new StepSupport("name");
        execution = newStepExecution(step, new Long(31));
        Assert.assertEquals("name", execution.getStepName());
        StepExecution stepExecution = newStepExecution(step, new Long(31));
        Assert.assertEquals(stepExecution.getJobExecutionId(), execution.getJobExecutionId());
        Assert.assertTrue(execution.equals(stepExecution));
    }

    @Test
    public void testHashCode() throws Exception {
        Assert.assertTrue("Hash code same as parent", ((new Entity(execution.getId()).hashCode()) != (execution.hashCode())));
    }

    @Test
    public void testHashCodeWithNullIds() throws Exception {
        Assert.assertTrue("Hash code not same as parent", ((new Entity(execution.getId()).hashCode()) != (blankExecution.hashCode())));
    }

    @Test
    public void testHashCodeViaHashSet() throws Exception {
        Set<StepExecution> set = new HashSet<>();
        set.add(execution);
        Assert.assertTrue(set.contains(execution));
        execution.setExecutionContext(foobarEc);
        Assert.assertTrue(set.contains(execution));
    }

    @Test
    public void testSerialization() throws Exception {
        ExitStatus status = NOOP;
        execution.setExitStatus(status);
        execution.setExecutionContext(foobarEc);
        byte[] serialized = SerializationUtils.serialize(execution);
        StepExecution deserialized = ((StepExecution) (SerializationUtils.deserialize(serialized)));
        Assert.assertEquals(execution, deserialized);
        Assert.assertEquals(status, deserialized.getExitStatus());
        Assert.assertNotNull(deserialized.getFailureExceptions());
    }

    @Test
    public void testAddException() throws Exception {
        RuntimeException exception = new RuntimeException();
        Assert.assertEquals(0, execution.getFailureExceptions().size());
        execution.addFailureException(exception);
        Assert.assertEquals(1, execution.getFailureExceptions().size());
        Assert.assertEquals(exception, execution.getFailureExceptions().get(0));
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
}

