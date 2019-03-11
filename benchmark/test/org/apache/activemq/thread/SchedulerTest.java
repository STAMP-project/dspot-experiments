/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.thread;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class SchedulerTest {
    private static final String schedulerName = "testScheduler";

    private Scheduler scheduler;

    @Test
    public void testExecutePeriodically() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        scheduler.executePeriodically(new SchedulerTest.CountDownRunnable(latch), 10);
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void executeAfterDelay() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        scheduler.executeAfterDelay(new SchedulerTest.CountDownRunnable(latch), 10);
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testExecutePeriodicallyReplace() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        final SchedulerTest.CountDownRunnable task = new SchedulerTest.CountDownRunnable(latch);
        scheduler.executePeriodically(task, 500);
        scheduler.executePeriodically(task, 500);
        scheduler.cancel(task);
        // make sure the task never runs
        Assert.assertFalse(latch.await(1000, TimeUnit.MILLISECONDS));
    }

    private static class CountDownRunnable implements Runnable {
        final CountDownLatch latch;

        CountDownRunnable(final CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            latch.countDown();
        }
    }
}

