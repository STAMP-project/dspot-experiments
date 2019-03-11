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
package org.springframework.batch.repeat.support;


import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


/**
 * Simple tests for concurrent behaviour in repeat template, in particular the
 * barrier at the end of the iteration. N.B. these tests may fail if
 * insufficient threads are available (e.g. on a single-core machine, or under
 * load). They shouldn't deadlock though.
 *
 * @author Dave Syer
 */
public class TaskExecutorRepeatTemplateBulkAsynchronousTests {
    static Log logger = LogFactory.getLog(TaskExecutorRepeatTemplateBulkAsynchronousTests.class);

    private int total = 1000;

    private int throttleLimit = 30;

    private volatile int early = Integer.MAX_VALUE;

    private volatile int error = Integer.MAX_VALUE;

    private TaskExecutorRepeatTemplate template;

    private RepeatCallback callback;

    private List<String> items;

    private ThreadPoolTaskExecutor threadPool = new ThreadPoolTaskExecutor();

    @Test
    public void testThrottleLimit() throws Exception {
        template.iterate(callback);
        int frequency = Collections.frequency(items, "null");
        // System.err.println(items);
        // System.err.println("Frequency: " + frequency);
        Assert.assertEquals(total, ((items.size()) - frequency));
        Assert.assertTrue((frequency > 1));
        Assert.assertTrue((frequency <= ((throttleLimit) + 1)));
    }

    @Test
    public void testThrottleLimitEarlyFinish() throws Exception {
        early = 2;
        template.iterate(callback);
        int frequency = Collections.frequency(items, "null");
        // System.err.println("Frequency: " + frequency);
        // System.err.println("Items: " + items);
        Assert.assertEquals(total, ((items.size()) - frequency));
        Assert.assertTrue((frequency > 1));
        Assert.assertTrue((frequency <= ((throttleLimit) + 1)));
    }

    @Test
    public void testThrottleLimitEarlyFinishThreadStarvation() throws Exception {
        early = 2;
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        // Set the concurrency limit below the throttle limit for possible
        // starvation condition
        taskExecutor.setMaxPoolSize(20);
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setQueueCapacity(0);
        // This is the most sensible setting, otherwise the bookkeeping in
        // ResultHolderResultQueue gets out of whack when tasks are aborted.
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.afterPropertiesSet();
        template.setTaskExecutor(taskExecutor);
        template.iterate(callback);
        int frequency = Collections.frequency(items, "null");
        // System.err.println("Frequency: " + frequency);
        // System.err.println("Items: " + items);
        // Extra tasks will be submitted before the termination is detected
        Assert.assertEquals(total, ((items.size()) - frequency));
        Assert.assertTrue((frequency <= ((throttleLimit) + 1)));
        taskExecutor.destroy();
    }

    @Test
    public void testThrottleLimitEarlyFinishOneThread() throws Exception {
        early = 4;
        SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor();
        taskExecutor.setConcurrencyLimit(1);
        // This is kind of slow with only one thread, so reduce size:
        throttleLimit = 10;
        total = 20;
        template.setThrottleLimit(throttleLimit);
        template.setTaskExecutor(taskExecutor);
        template.iterate(callback);
        int frequency = Collections.frequency(items, "null");
        // System.err.println("Frequency: " + frequency);
        // System.err.println("Items: " + items);
        Assert.assertEquals(total, ((items.size()) - frequency));
        Assert.assertTrue((frequency <= ((throttleLimit) + 1)));
    }

    @Test
    public void testThrottleLimitWithEarlyCompletion() throws Exception {
        early = 2;
        template.setCompletionPolicy(new SimpleCompletionPolicy(10));
        template.iterate(callback);
        int frequency = Collections.frequency(items, "null");
        Assert.assertEquals(10, ((items.size()) - frequency));
        // System.err.println("Frequency: " + frequency);
        Assert.assertEquals(0, frequency);
    }

    @Test
    public void testThrottleLimitWithError() throws Exception {
        error = 50;
        try {
            template.iterate(callback);
            Assert.fail("Expected planned exception");
        } catch (Exception e) {
            Assert.assertEquals("Planned", e.getMessage());
        }
        int frequency = Collections.frequency(items, "null");
        Assert.assertEquals(0, frequency);
    }

    @Test
    public void testErrorThrownByCallback() throws Exception {
        callback = new RepeatCallback() {
            private volatile AtomicInteger count = new AtomicInteger(0);

            @Override
            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                int position = count.incrementAndGet();
                if (position == 4) {
                    throw new OutOfMemoryError("Planned");
                } else {
                    return RepeatStatus.CONTINUABLE;
                }
            }
        };
        template.setCompletionPolicy(new SimpleCompletionPolicy(10));
        try {
            template.iterate(callback);
            Assert.fail("Expected planned exception");
        } catch (OutOfMemoryError oome) {
            Assert.assertEquals("Planned", oome.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(("Wrong exception was thrown: " + e));
        }
    }
}

