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


import BatchStatus.FAILED;
import BatchStatus.STOPPED;
import java.util.concurrent.Semaphore;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;


public class StepExecutorInterruptionTests {
    private TaskletStep step;

    private JobExecution jobExecution;

    private ItemWriter<Object> itemWriter;

    private StepExecution stepExecution;

    private JobRepository jobRepository;

    @Test
    public void testInterruptStep() throws Exception {
        configureStep(new TaskletStep("step"));
        Thread processingThread = createThread(stepExecution);
        RepeatTemplate template = new RepeatTemplate();
        // N.B, If we don't set the completion policy it might run forever
        template.setCompletionPolicy(new SimpleCompletionPolicy(2));
        step.setTasklet(new TestingChunkOrientedTasklet(new org.springframework.batch.item.ItemReader<Object>() {
            @Override
            public Object read() throws Exception {
                // do something non-trivial (and not Thread.sleep())
                double foo = 1;
                for (int i = 2; i < 250; i++) {
                    foo = foo * i;
                }
                if (foo != 1) {
                    return new Double(foo);
                } else {
                    return null;
                }
            }
        }, itemWriter, template));
        processingThread.start();
        Thread.sleep(100);
        processingThread.interrupt();
        int count = 0;
        while ((processingThread.isAlive()) && (count < 1000)) {
            Thread.sleep(20);
            count++;
        } 
        Assert.assertTrue("Timed out waiting for step to be interrupted.", (count < 1000));
        Assert.assertFalse(processingThread.isAlive());
        Assert.assertEquals(STOPPED, stepExecution.getStatus());
    }

    @Test
    public void testInterruptOnInterruptedException() throws Exception {
        // This simulates the unlikely sounding, but in practice all too common
        // in Bamboo situation where the thread is interrupted before the lock
        // is taken.
        configureStep(new TaskletStep("step") {
            @SuppressWarnings("serial")
            @Override
            protected Semaphore createSemaphore() {
                return new Semaphore(1) {
                    @Override
                    public void acquire() throws InterruptedException {
                        Thread.currentThread().interrupt();
                        throw new InterruptedException();
                    }

                    @Override
                    public void release() {
                    }
                };
            }
        });
        Thread processingThread = createThread(stepExecution);
        step.setTasklet(new TestingChunkOrientedTasklet(new org.springframework.batch.item.ItemReader<Object>() {
            @Override
            public Object read() throws Exception {
                return null;
            }
        }, itemWriter));
        processingThread.start();
        Thread.sleep(100);
        int count = 0;
        while ((processingThread.isAlive()) && (count < 1000)) {
            Thread.sleep(20);
            count++;
        } 
        Assert.assertTrue("Timed out waiting for step to be interrupted.", (count < 1000));
        Assert.assertFalse(processingThread.isAlive());
        Assert.assertEquals(STOPPED, stepExecution.getStatus());
    }

    @Test
    public void testLockNotReleasedIfChunkFails() throws Exception {
        configureStep(new TaskletStep("step") {
            @SuppressWarnings("serial")
            @Override
            protected Semaphore createSemaphore() {
                return new Semaphore(1) {
                    private boolean locked = false;

                    @Override
                    public void acquire() throws InterruptedException {
                        locked = true;
                    }

                    @Override
                    public void release() {
                        Assert.assertTrue("Lock released before it is acquired", locked);
                    }
                };
            }
        });
        step.setTasklet(new TestingChunkOrientedTasklet(new org.springframework.batch.item.ItemReader<Object>() {
            @Override
            public Object read() throws Exception {
                throw new RuntimeException("Planned!");
            }
        }, itemWriter));
        jobRepository.add(stepExecution);
        step.execute(stepExecution);
        Assert.assertEquals("Planned!", stepExecution.getFailureExceptions().get(0).getMessage());
        Assert.assertEquals(FAILED, stepExecution.getStatus());
    }
}

