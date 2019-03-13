/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.client.spi.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientExecutionServiceImplTest {
    private static ClientExecutionServiceImpl executionService;

    @Test
    public void testExecuteInternal() {
        ClientExecutionServiceImplTest.TestRunnable runnable = new ClientExecutionServiceImplTest.TestRunnable();
        ClientExecutionServiceImplTest.executionService.execute(runnable);
        runnable.await();
    }

    @Test
    public void testExecute() {
        ClientExecutionServiceImplTest.TestRunnable runnable = new ClientExecutionServiceImplTest.TestRunnable();
        ClientExecutionServiceImplTest.executionService.execute(runnable);
        runnable.await();
    }

    @Test
    public void testSchedule() throws Exception {
        ClientExecutionServiceImplTest.TestRunnable runnable = new ClientExecutionServiceImplTest.TestRunnable();
        ScheduledFuture<?> future = ClientExecutionServiceImplTest.executionService.schedule(runnable, 0, TimeUnit.SECONDS);
        Object result = future.get();
        Assert.assertTrue(runnable.isExecuted());
        Assert.assertNull(result);
    }

    @Test
    public void testScheduleWithRepetition() throws Exception {
        ClientExecutionServiceImplTest.TestRunnable runnable = new ClientExecutionServiceImplTest.TestRunnable(5);
        ScheduledFuture<?> future = ClientExecutionServiceImplTest.executionService.scheduleWithRepetition(runnable, 0, 100, TimeUnit.MILLISECONDS);
        runnable.await();
        boolean result = future.cancel(true);
        Assert.assertTrue(result);
    }

    private static class TestRunnable implements Runnable {
        private final CountDownLatch isExecuted;

        TestRunnable() {
            this(1);
        }

        TestRunnable(int executions) {
            this.isExecuted = new CountDownLatch(executions);
        }

        @Override
        public void run() {
            isExecuted.countDown();
        }

        private boolean isExecuted() {
            return (isExecuted.getCount()) == 0;
        }

        private void await() {
            try {
                isExecuted.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

