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
package com.hazelcast.executor;


import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static com.hazelcast.executor.ExecutorServiceTestSupport.BasicTestCallable.RESULT;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SingleNodeTest extends ExecutorServiceTestSupport {
    private IExecutorService executor;

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

    @Test(expected = RejectedExecutionException.class)
    public void alwaysFalseMemberSelector_expectRejection() {
        ExecutorServiceTestSupport.HazelcastInstanceAwareRunnable task = new ExecutorServiceTestSupport.HazelcastInstanceAwareRunnable();
        executor.execute(task, new MemberSelector() {
            @Override
            public boolean select(Member member) {
                return false;
            }
        });
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
        executor.submit(task, executionCallback);
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
        executor.submit(task, executionCallback);
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test(expected = CancellationException.class)
    public void timeOut_thenCancel() throws InterruptedException, ExecutionException {
        ExecutorServiceTestSupport.SleepingTask task = new ExecutorServiceTestSupport.SleepingTask(1);
        Future future = executor.submit(task);
        try {
            future.get(1, TimeUnit.MILLISECONDS);
            Assert.fail("Should throw TimeoutException!");
        } catch (TimeoutException expected) {
            HazelcastTestSupport.ignore(expected);
        }
        Assert.assertFalse(future.isDone());
        Assert.assertTrue(future.cancel(true));
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void cancelWhileQueued() throws InterruptedException, ExecutionException {
        Callable task1 = new ExecutorServiceTestSupport.SleepingTask(100);
        Future inProgressFuture = executor.submit(task1);
        Callable task2 = new ExecutorServiceTestSupport.BasicTestCallable();
        // this future should not be an instance of CompletedFuture,
        // because even if we get an exception, isDone is returning true
        Future queuedFuture = executor.submit(task2);
        try {
            Assert.assertFalse(queuedFuture.isDone());
            Assert.assertTrue(queuedFuture.cancel(true));
            Assert.assertTrue(queuedFuture.isCancelled());
            Assert.assertTrue(queuedFuture.isDone());
        } finally {
            inProgressFuture.cancel(true);
        }
        queuedFuture.get();
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
        final BlockingQueue<Member> qResponse = new ArrayBlockingQueue<Member>(1);
        executor.submit(new ExecutorServiceTestSupport.MemberCheck(), new ExecutionCallback<Member>() {
            public void onResponse(Member response) {
                qResponse.offer(response);
            }

            public void onFailure(Throwable t) {
            }
        });
        Assert.assertNotNull(qResponse.poll(10, TimeUnit.SECONDS));
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

    @Test
    public void invokeAll() throws Exception {
        // only one task
        ArrayList<Callable<String>> tasks = new ArrayList<Callable<String>>();
        tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        List<Future<String>> futures = executor.invokeAll(tasks);
        Assert.assertEquals(futures.size(), 1);
        Assert.assertEquals(futures.get(0).get(), RESULT);
        // more tasks
        tasks.clear();
        for (int i = 0; i < 1000; i++) {
            tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        }
        futures = executor.invokeAll(tasks);
        Assert.assertEquals(futures.size(), 1000);
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(futures.get(i).get(), RESULT);
        }
    }

    @Test
    public void invokeAllTimeoutCancelled() throws Exception {
        List<? extends Callable<Boolean>> singleTask = Collections.singletonList(new ExecutorServiceTestSupport.SleepingTask(0));
        List<Future<Boolean>> futures = executor.invokeAll(singleTask, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), 1);
        Assert.assertEquals(futures.get(0).get(), Boolean.TRUE);
        List<Callable<Boolean>> tasks = new ArrayList<Callable<Boolean>>();
        for (int i = 0; i < 1000; i++) {
            tasks.add(new ExecutorServiceTestSupport.SleepingTask((i < 2 ? 0 : 20)));
        }
        futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), 1000);
        for (int i = 0; i < 1000; i++) {
            if (i < 2) {
                Assert.assertEquals(futures.get(i).get(), Boolean.TRUE);
            } else {
                try {
                    futures.get(i).get();
                    Assert.fail();
                } catch (CancellationException expected) {
                    HazelcastTestSupport.ignore(expected);
                }
            }
        }
    }

    @Test
    public void invokeAllTimeoutSuccess() throws Exception {
        // only one task
        ArrayList<Callable<String>> tasks = new ArrayList<Callable<String>>();
        tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        List<Future<String>> futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), 1);
        Assert.assertEquals(futures.get(0).get(), RESULT);
        // more tasks
        tasks.clear();
        for (int i = 0; i < 1000; i++) {
            tasks.add(new ExecutorServiceTestSupport.BasicTestCallable());
        }
        futures = executor.invokeAll(tasks, 5, TimeUnit.SECONDS);
        Assert.assertEquals(futures.size(), 1000);
        for (int i = 0; i < 1000; i++) {
            Assert.assertEquals(futures.get(i).get(), RESULT);
        }
    }

    /**
     * Shutdown-related method behaviour when the cluster is running
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

    @Test
    public void executorServiceStats() throws InterruptedException, ExecutionException {
        final int iterations = 10;
        SingleNodeTest.LatchRunnable.latch = new CountDownLatch(iterations);
        SingleNodeTest.LatchRunnable runnable = new SingleNodeTest.LatchRunnable();
        for (int i = 0; i < iterations; i++) {
            executor.execute(runnable);
        }
        HazelcastTestSupport.assertOpenEventually(SingleNodeTest.LatchRunnable.latch);
        Future<Boolean> future = executor.submit(new ExecutorServiceTestSupport.SleepingTask(10));
        future.cancel(true);
        try {
            future.get();
        } catch (CancellationException ignored) {
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                LocalExecutorStats stats = executor.getLocalExecutorStats();
                Assert.assertEquals((iterations + 1), stats.getStartedTaskCount());
                Assert.assertEquals(iterations, stats.getCompletedTaskCount());
                Assert.assertEquals(0, stats.getPendingTaskCount());
                Assert.assertEquals(1, stats.getCancelledTaskCount());
            }
        });
    }

    static class LatchRunnable implements Serializable , Runnable {
        static CountDownLatch latch;

        @Override
        public void run() {
            SingleNodeTest.LatchRunnable.latch.countDown();
        }
    }
}

