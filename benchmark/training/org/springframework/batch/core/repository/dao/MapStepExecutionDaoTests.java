/**
 * Copyright 2008-2012 the original author or authors.
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
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;


@RunWith(JUnit4.class)
public class MapStepExecutionDaoTests extends AbstractStepExecutionDaoTests {
    /**
     * Modifications to saved entity do not affect the persisted object.
     */
    @Test
    public void testPersistentCopy() {
        StepExecutionDao tested = new MapStepExecutionDao();
        JobExecution jobExecution = new JobExecution(77L);
        StepExecution stepExecution = new StepExecution("stepName", jobExecution);
        Assert.assertNull(stepExecution.getEndTime());
        tested.saveStepExecution(stepExecution);
        stepExecution.setEndTime(new Date());
        StepExecution retrieved = tested.getStepExecution(jobExecution, stepExecution.getId());
        Assert.assertNull(retrieved.getEndTime());
        stepExecution.setEndTime(null);
        tested.updateStepExecution(stepExecution);
        stepExecution.setEndTime(new Date());
        StepExecution stored = tested.getStepExecution(jobExecution, stepExecution.getId());
        Assert.assertNull(stored.getEndTime());
    }

    @Test
    public void testAddStepExecutions() {
        StepExecutionDao tested = new MapStepExecutionDao();
        JobExecution jobExecution = new JobExecution(88L);
        // Create step execution with status STARTED
        StepExecution stepExecution = new StepExecution("Step one", jobExecution);
        stepExecution.setStatus(STARTED);
        // Save and check id
        tested.saveStepExecution(stepExecution);
        Assert.assertNotNull(stepExecution.getId());
        // Job execution instance doesn't contain step execution instances
        Assert.assertEquals(0, jobExecution.getStepExecutions().size());
        // Load all execution steps and check
        tested.addStepExecutions(jobExecution);
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        // Check the first (and only) step execution instance of the job instance
        StepExecution jobStepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(STARTED, jobStepExecution.getStatus());
        Assert.assertEquals(stepExecution.getId(), jobStepExecution.getId());
        // Load the step execution instance from the repository and check is it the same
        StepExecution repoStepExecution = tested.getStepExecution(jobExecution, stepExecution.getId());
        Assert.assertEquals(stepExecution.getId(), repoStepExecution.getId());
        Assert.assertEquals(STARTED, repoStepExecution.getStatus());
        // Update the step execution instance
        repoStepExecution.setStatus(COMPLETED);
        // Update the step execution in the repository and check
        tested.updateStepExecution(repoStepExecution);
        StepExecution updatedStepExecution = tested.getStepExecution(jobExecution, stepExecution.getId());
        Assert.assertEquals(stepExecution.getId(), updatedStepExecution.getId());
        Assert.assertEquals(COMPLETED, updatedStepExecution.getStatus());
        // Now, add step executions from the repository and check
        tested.addStepExecutions(jobExecution);
        jobStepExecution = jobExecution.getStepExecutions().iterator().next();
        Assert.assertEquals(1, jobExecution.getStepExecutions().size());
        Assert.assertEquals(stepExecution.getId(), jobStepExecution.getId());
        Assert.assertEquals(COMPLETED, jobStepExecution.getStatus());
    }
}

