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
package com.hazelcast.durableexecutor;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.Member;
import com.hazelcast.executor.ExecutorServiceTestSupport;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.executor.ExecutorServiceTestSupport.BasicTestCallable.RESULT;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class DurableSingleNodeTest extends ExecutorServiceTestSupport {
    private DurableExecutorService executor;

    @Test
    public void hazelcastInstanceAware_expectInjection() throws Throwable {
        ExecutorServiceTestSupport.HazelcastInstanceAwareRunnable task = new ExecutorServiceTestSupport.HazelcastInstanceAwareRunnable();
        try {
            executor.submit(task).get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }
    }

    @Test(expected = NullPointerException.class)
    @SuppressWarnings("ConstantConditions")
    public void submitNullTask_expectFailure() {
        executor.submit(((Callable<?>) (null)));
    }

    @Test
    public void submitBasicTask() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        Future future = executor.submit(task);
        Assert.assertEquals(future.get(), RESULT);
    }

    @Test
    public void executionCallback_notifiedOnSuccess() {
        final CountDownLatch latch = new CountDownLatch(1);
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        ExecutionCallback<String> executionCallback = new ExecutionCallback<String>() {
            public void onResponse(String response) {
                latch.countDown();
            }

            public void onFailure(Throwable t) {
            }
        };
        executor.submit(task).andThen(executionCallback);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void executionCallback_notifiedOnFailure() {
        final CountDownLatch latch = new CountDownLatch(1);
        ExecutorServiceTestSupport.FailingTestTask task = new ExecutorServiceTestSupport.FailingTestTask();
        ExecutionCallback<String> executionCallback = new ExecutionCallback<String>() {
            public void onResponse(String response) {
            }

            public void onFailure(Throwable t) {
                latch.countDown();
            }
        };
        executor.submit(task).andThen(executionCallback);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void isDoneAfterGet() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        Future future = executor.submit(task);
        Assert.assertEquals(future.get(), RESULT);
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void issue129() throws Exception {
        for (int i = 0; i < 1000; i++) {
            Callable<String> task1 = new ExecutorServiceTestSupport.BasicTestCallable();
            Callable<String> task2 = new ExecutorServiceTestSupport.BasicTestCallable();
            Future<String> future1 = executor.submit(task1);
            Future<String> future2 = executor.submit(task2);
            Assert.assertEquals(future2.get(), RESULT);
            Assert.assertTrue(future2.isDone());
            Assert.assertEquals(future1.get(), RESULT);
            Assert.assertTrue(future1.isDone());
        }
    }

    @Test
    public void issue292() throws Exception {
        final BlockingQueue<Member> responseQueue = new ArrayBlockingQueue<Member>(1);
        executor.submit(new ExecutorServiceTestSupport.MemberCheck()).andThen(new ExecutionCallback<Member>() {
            public void onResponse(Member response) {
                responseQueue.offer(response);
            }

            public void onFailure(Throwable t) {
            }
        });
        Assert.assertNotNull(responseQueue.poll(10, TimeUnit.SECONDS));
    }

    @Test(timeout = 10000)
    public void taskSubmitsNestedTask() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.NestedExecutorTask();
        executor.submit(task).get();
    }

    @Test
    public void getManyTimesFromSameFuture() throws Exception {
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        Future<String> future = executor.submit(task);
        for (int i = 0; i < 4; i++) {
            Assert.assertEquals(future.get(), RESULT);
            Assert.assertTrue(future.isDone());
        }
    }

    /**
     * Shutdown-related method behaviour when the cluster is running.
     */
    @Test
    public void shutdownBehaviour() {
        // fresh instance, is not shutting down
        Assert.assertFalse(executor.isShutdown());
        Assert.assertFalse(executor.isTerminated());
        executor.shutdown();
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // shutdownNow() should return an empty list and be ignored
        List<Runnable> pending = executor.shutdownNow();
        Assert.assertTrue(pending.isEmpty());
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // awaitTermination() should return immediately false
        try {
            boolean terminated = executor.awaitTermination(60L, TimeUnit.SECONDS);
            Assert.assertFalse(terminated);
        } catch (InterruptedException ie) {
            Assert.fail("InterruptedException");
        }
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
    }

    /**
     * Shutting down the cluster should act as the ExecutorService shutdown
     */
    @Test(expected = RejectedExecutionException.class)
    public void clusterShutdown() {
        shutdownNodeFactory();
        HazelcastTestSupport.sleepSeconds(2);
        Assert.assertNotNull(executor);
        Assert.assertTrue(executor.isShutdown());
        Assert.assertTrue(executor.isTerminated());
        // new tasks must be rejected
        Callable<String> task = new ExecutorServiceTestSupport.BasicTestCallable();
        executor.submit(task);
    }

    // FIXME as soon as executor.getLocalExecutorStats() is implemented
    // @Test
    // public void executorServiceStats() throws Exception {
    // final int executeCount = 10;
    // LatchRunnable.latch = new CountDownLatch(executeCount);
    // final LatchRunnable r = new LatchRunnable();
    // for (int i = 0; i < executeCount; i++) {
    // executor.execute(r);
    // }
    // assertOpenEventually(LatchRunnable.latch);
    // final Future<Boolean> future = executor.submit(new SleepingTask(10));
    // future.cancel(true);
    // try {
    // future.get();
    // } catch (CancellationException ignored) {
    // }
    // 
    // assertTrueEventually(new AssertTask() {
    // @Override
    // public void run()
    // throws Exception {
    // final LocalExecutorStats stats = executor.getLocalExecutorStats();
    // assertEquals(executeCount + 1, stats.getStartedTaskCount());
    // assertEquals(executeCount, stats.getCompletedTaskCount());
    // assertEquals(0, stats.getPendingTaskCount());
    // assertEquals(1, stats.getCancelledTaskCount());
    // }
    // });
    // }
    static class LatchRunnable implements Serializable , Runnable {
        static CountDownLatch latch;

        @Override
        public void run() {
            DurableSingleNodeTest.LatchRunnable.latch.countDown();
        }
    }
}

