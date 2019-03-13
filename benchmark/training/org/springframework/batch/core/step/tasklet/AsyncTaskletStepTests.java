/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.step.tasklet;


import BatchStatus.COMPLETED;
import BatchStatus.FAILED;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.util.StringUtils;


public class AsyncTaskletStepTests {
    private static Log logger = LogFactory.getLog(AsyncTaskletStepTests.class);

    private List<String> processed = new CopyOnWriteArrayList<>();

    private TaskletStep step;

    private int throttleLimit = 20;

    ItemWriter<String> itemWriter = new ItemWriter<String>() {
        @Override
        public void write(List<? extends String> data) throws Exception {
            // Thread.sleep(100L);
            AsyncTaskletStepTests.logger.info(("Items: " + data));
            processed.addAll(data);
            if (data.contains("fail")) {
                throw new RuntimeException("Planned");
            }
        }
    };

    private JobRepository jobRepository;

    private List<String> items;

    private int concurrencyLimit = 300;

    private ItemProcessor<String, String> itemProcessor = new org.springframework.batch.item.support.PassThroughItemProcessor();

    /**
     * StepExecution should be updated after every chunk commit.
     */
    @Test
    public void testStepExecutionUpdates() throws Exception {
        items = new java.util.ArrayList(Arrays.asList(StringUtils.commaDelimitedListToStringArray("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25")));
        setUp();
        JobExecution jobExecution = jobRepository.createJobExecution("JOB", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution(step.getName());
        step.execute(stepExecution);
        Assert.assertEquals(COMPLETED, stepExecution.getStatus());
        // assertEquals(25, stepExecution.getReadCount());
        // assertEquals(25, processed.size());
        Assert.assertTrue(((stepExecution.getReadCount()) >= 25));
        Assert.assertTrue(((processed.size()) >= 25));
        // System.err.println(stepExecution.getCommitCount());
        // System.err.println(processed);
        // Check commit count didn't spin out of control waiting for other
        // threads to finish...
        Assert.assertTrue(("Not enough commits: " + (stepExecution.getCommitCount())), ((stepExecution.getCommitCount()) > ((processed.size()) / 2)));
        Assert.assertTrue(("Too many commits: " + (stepExecution.getCommitCount())), ((stepExecution.getCommitCount()) <= ((((processed.size()) / 2) + (throttleLimit)) + 1)));
    }

    /**
     * StepExecution should fail immediately on error.
     */
    @Test
    public void testStepExecutionFails() throws Exception {
        throttleLimit = 1;
        concurrencyLimit = 1;
        items = Arrays.asList("one", "fail", "three", "four");
        setUp();
        JobExecution jobExecution = jobRepository.createJobExecution("JOB", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution(step.getName());
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getReadCount());
        Assert.assertEquals(2, processed.size());
    }

    /**
     * StepExecution should fail immediately on error in processor.
     */
    @Test
    public void testStepExecutionFailsWithProcessor() throws Exception {
        throttleLimit = 1;
        concurrencyLimit = 1;
        items = Arrays.asList("one", "barf", "three", "four");
        itemProcessor = new ItemProcessor<String, String>() {
            @Override
            public String process(String item) throws Exception {
                AsyncTaskletStepTests.logger.info(("Item: " + item));
                processed.add(item);
                if (item.equals("barf")) {
                    throw new RuntimeException("Planned processor error");
                }
                return item;
            }
        };
        setUp();
        JobExecution jobExecution = jobRepository.createJobExecution("JOB", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution(step.getName());
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(2, stepExecution.getReadCount());
        Assert.assertEquals(2, processed.size());
    }

    /**
     * StepExecution should fail immediately on error.
     */
    @Test
    public void testStepExecutionFailsOnLastItem() throws Exception {
        throttleLimit = 1;
        concurrencyLimit = 1;
        items = Arrays.asList("one", "two", "three", "fail");
        setUp();
        JobExecution jobExecution = jobRepository.createJobExecution("JOB", new JobParameters());
        StepExecution stepExecution = jobExecution.createStepExecution(step.getName());
        step.execute(stepExecution);
        Assert.assertEquals(FAILED, stepExecution.getStatus());
        Assert.assertEquals(4, stepExecution.getReadCount());
        Assert.assertEquals(4, processed.size());
    }
}

