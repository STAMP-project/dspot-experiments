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
package org.springframework.batch.core.partition.support;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import BatchStatus.STOPPED;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.partition.PartitionHandler;
import org.springframework.batch.core.partition.StepExecutionSplitter;
import org.springframework.batch.core.repository.JobRepository;


/**
 *
 *
 * @author Dave Syer
 */
public class PartitionStepTests {
    private PartitionStep step = new PartitionStep();

    private JobRepository jobRepository;

    @Test
    public void testVanillaStepExecution() throws Exception {
        step.setStepExecutionSplitter(new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner()));
        step.setPartitionHandler(new PartitionHandler() {
            @Override
            public Collection<StepExecution> handle(StepExecutionSplitter stepSplitter, StepExecution stepExecution) throws Exception {
                Set<StepExecution> executions = stepSplitter.split(stepExecution, 2);
                for (StepExecution execution : executions) {
                    execution.setStatus(COMPLETED);
                    execution.setExitStatus(ExitStatus.COMPLETED);
                }
                return executions;
            }
        });
        step.afterPropertiesSet();
        JobExecution jobExecution = jobRepository.createJobExecution("vanillaJob", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        // one master and two workers
        Assert.assertEquals(3, stepExecution.getJobExecution().getStepExecutions().size());
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
    }

    @Test
    public void testFailedStepExecution() throws Exception {
        step.setStepExecutionSplitter(new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner()));
        step.setPartitionHandler(new PartitionHandler() {
            @Override
            public Collection<StepExecution> handle(StepExecutionSplitter stepSplitter, StepExecution stepExecution) throws Exception {
                Set<StepExecution> executions = stepSplitter.split(stepExecution, 2);
                for (StepExecution execution : executions) {
                    execution.setStatus(FAILED);
                    execution.setExitStatus(ExitStatus.FAILED);
                }
                return executions;
            }
        });
        step.afterPropertiesSet();
        JobExecution jobExecution = jobRepository.createJobExecution("vanillaJob", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        // one master and two workers
        Assert.assertEquals(3, stepExecution.getJobExecution().getStepExecutions().size());
        Assert.assertEquals(FAILED, stepExecution.getStatus());
    }

    @Test
    public void testRestartStepExecution() throws Exception {
        final AtomicBoolean started = new AtomicBoolean(false);
        step.setStepExecutionSplitter(new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner()));
        step.setPartitionHandler(new PartitionHandler() {
            @Override
            public Collection<StepExecution> handle(StepExecutionSplitter stepSplitter, StepExecution stepExecution) throws Exception {
                Set<StepExecution> executions = stepSplitter.split(stepExecution, 2);
                if (!(started.get())) {
                    started.set(true);
                    for (StepExecution execution : executions) {
                        execution.setStatus(FAILED);
                        execution.setExitStatus(ExitStatus.FAILED);
                        execution.getExecutionContext().putString("foo", execution.getStepName());
                    }
                } else {
                    for (StepExecution execution : executions) {
                        // On restart the execution context should have been restored
                        Assert.assertEquals(execution.getStepName(), execution.getExecutionContext().getString("foo"));
                    }
                }
                for (StepExecution execution : executions) {
                    jobRepository.update(execution);
                    jobRepository.updateExecutionContext(execution);
                }
                return executions;
            }
        });
        step.afterPropertiesSet();
        JobExecution jobExecution = jobRepository.createJobExecution("vanillaJob", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        jobExecution.setStatus(FAILED);
        jobExecution.setEndTime(new Date());
        jobRepository.update(jobExecution);
        // Now restart...
        jobExecution = jobRepository.createJobExecution("vanillaJob", new JobParameters());
        stepExecution = jobExecution.createStepExecution("foo");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        // one master and two workers
        Assert.assertEquals(3, stepExecution.getJobExecution().getStepExecutions().size());
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
    }

    @Test
    public void testStoppedStepExecution() throws Exception {
        step.setStepExecutionSplitter(new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner()));
        step.setPartitionHandler(new PartitionHandler() {
            @Override
            public Collection<StepExecution> handle(StepExecutionSplitter stepSplitter, StepExecution stepExecution) throws Exception {
                Set<StepExecution> executions = stepSplitter.split(stepExecution, 2);
                for (StepExecution execution : executions) {
                    execution.setStatus(STOPPED);
                    execution.setExitStatus(ExitStatus.STOPPED);
                }
                return executions;
            }
        });
        step.afterPropertiesSet();
        JobExecution jobExecution = jobRepository.createJobExecution("vanillaJob", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        // one master and two workers
        Assert.assertEquals(3, stepExecution.getJobExecution().getStepExecutions().size());
        Assert.assertEquals(STOPPED, stepExecution.getStatus());
    }

    @Test
    public void testStepAggregator() throws Exception {
        step.setStepExecutionAggregator(new DefaultStepExecutionAggregator() {
            @Override
            public void aggregate(StepExecution result, Collection<StepExecution> executions) {
                super.aggregate(result, executions);
                result.getExecutionContext().put("aggregated", true);
            }
        });
        step.setStepExecutionSplitter(new SimpleStepExecutionSplitter(jobRepository, true, step.getName(), new SimplePartitioner()));
        step.setPartitionHandler(new PartitionHandler() {
            @Override
            public Collection<StepExecution> handle(StepExecutionSplitter stepSplitter, StepExecution stepExecution) throws Exception {
                return Arrays.asList(stepExecution);
            }
        });
        step.afterPropertiesSet();
        JobExecution jobExecution = jobRepository.createJobExecution("vanillaJob", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution("foo");
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        Assert.assertEquals(true, stepExecution.getExecutionContext().get("aggregated"));
    }
}

