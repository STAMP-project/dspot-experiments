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
package org.springframework.batch.core.partition.support;


import BatchStatus.ABANDONED;
import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STARTED;
import BatchStatus.UNKNOWN;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ExecutionContext;


public class SimpleStepExecutionSplitterTests {
    private Step step;

    private JobRepository jobRepository;

    private StepExecution stepExecution;

    @Test
    public void testSimpleStepExecutionProviderJobRepositoryStep() throws Exception {
        SimpleStepExecutionSplitter splitter = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Set<StepExecution> execs = splitter.split(stepExecution, 2);
        Assert.assertEquals(2, execs.size());
        for (StepExecution execution : execs) {
            Assert.assertNotNull("step execution partition is saved", execution.getId());
        }
    }

    /**
     * Tests the results of BATCH-2490
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddressabilityOfSetResults() throws Exception {
        SimpleStepExecutionSplitter splitter = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Set<StepExecution> execs = splitter.split(stepExecution, 2);
        Assert.assertEquals(2, execs.size());
        StepExecution execution = execs.iterator().next();
        execs.remove(execution);
        Assert.assertEquals(1, execs.size());
    }

    @Test
    public void testSimpleStepExecutionProviderJobRepositoryStepPartitioner() throws Exception {
        final Map<String, ExecutionContext> map = Collections.singletonMap("foo", new ExecutionContext());
        SimpleStepExecutionSplitter splitter = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new Partitioner() {
            @Override
            public Map<String, ExecutionContext> partition(int gridSize) {
                return map;
            }
        });
        Assert.assertEquals(1, splitter.split(stepExecution, 2).size());
    }

    @Test
    public void testRememberGridSize() throws Exception {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(2, split.size());
        stepExecution = update(split, stepExecution, FAILED);
        Assert.assertEquals(2, provider.split(stepExecution, 3).size());
    }

    @Test
    public void testRememberPartitionNames() throws Exception {
        class CustomPartitioner implements PartitionNameProvider , Partitioner {
            @Override
            public Map<String, ExecutionContext> partition(int gridSize) {
                return Collections.singletonMap("foo", new ExecutionContext());
            }

            @Override
            public Collection<String> getPartitionNames(int gridSize) {
                return Arrays.asList("foo");
            }
        }
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new CustomPartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(1, split.size());
        Assert.assertEquals("step:foo", split.iterator().next().getStepName());
        stepExecution = update(split, stepExecution, FAILED);
        split = provider.split(stepExecution, 2);
        Assert.assertEquals("step:foo", split.iterator().next().getStepName());
    }

    @Test
    public void testGetStepName() {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Assert.assertEquals("step", provider.getStepName());
    }

    @Test
    public void testUnknownStatus() throws Exception {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(2, split.size());
        stepExecution = update(split, stepExecution, UNKNOWN);
        try {
            provider.split(stepExecution, 2);
        } catch (JobExecutionException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("UNKNOWN"));
        }
    }

    @Test
    public void testCompleteStatusAfterFailure() throws Exception {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, false, step.getName(), new SimplePartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(2, split.size());
        StepExecution nextExecution = update(split, stepExecution, COMPLETED, false);
        // If already complete in another JobExecution we don't execute again
        Assert.assertEquals(0, provider.split(nextExecution, 2).size());
    }

    @Test
    public void testCompleteStatusSameJobExecution() throws Exception {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, false, step.getName(), new SimplePartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(2, split.size());
        stepExecution = update(split, stepExecution, COMPLETED);
        // If already complete in the same JobExecution we should execute again
        Assert.assertEquals(2, provider.split(stepExecution, 2).size());
    }

    @Test
    public void testIncompleteStatus() throws Exception {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(2, split.size());
        stepExecution = update(split, stepExecution, STARTED);
        // If not already complete we don't execute again
        try {
            provider.split(stepExecution, 2);
        } catch (JobExecutionException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("STARTED"));
        }
    }

    @Test
    public void testAbandonedStatus() throws Exception {
        SimpleStepExecutionSplitter provider = new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner());
        Set<StepExecution> split = provider.split(stepExecution, 2);
        Assert.assertEquals(2, split.size());
        stepExecution = update(split, stepExecution, ABANDONED);
        // If not already complete we don't execute again
        try {
            provider.split(stepExecution, 2);
        } catch (JobExecutionException e) {
            String message = e.getMessage();
            Assert.assertTrue(("Wrong message: " + message), message.contains("ABANDONED"));
        }
    }
}

