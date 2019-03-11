/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.util;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.geode.internal.util.AbortableTaskService.AbortableTask;
import org.apache.geode.test.junit.rules.ExecutorServiceRule;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class AbortableTaskServiceJUnitTest {
    private static final long TIMEOUT_SECONDS = 10;

    private volatile CountDownLatch delay;

    private AbortableTaskService tasks;

    @Rule
    public ExecutorServiceRule executorServiceRule = new ExecutorServiceRule();

    @Test
    public void testExecute() throws Exception {
        AbortableTaskServiceJUnitTest.DelayedTask dt = new AbortableTaskServiceJUnitTest.DelayedTask();
        this.tasks.execute(dt);
        Future<Boolean> future = executorServiceRule.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                tasks.waitForCompletion();
                return tasks.isCompleted();
            }
        });
        this.delay.countDown();
        Assert.assertTrue(future.get(AbortableTaskServiceJUnitTest.TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertFalse(dt.wasAborted.get());
        Assert.assertTrue(dt.wasRun.get());
        Assert.assertTrue(this.tasks.isCompleted());
    }

    @Test
    public void testAbortDuringExecute() throws Exception {
        AbortableTaskServiceJUnitTest.DelayedTask dt = new AbortableTaskServiceJUnitTest.DelayedTask();
        this.tasks.execute(dt);
        Future<Boolean> future = executorServiceRule.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                tasks.waitForCompletion();
                return tasks.isCompleted();
            }
        });
        this.tasks.abortAll();
        this.delay.countDown();
        Assert.assertTrue(future.get(AbortableTaskServiceJUnitTest.TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(dt.wasAborted.get());
        // assertTrue(dt.wasRun.get()); -- race condition can result in true or false
        Assert.assertTrue(this.tasks.isCompleted());
    }

    @Test
    public void testAbortBeforeExecute() throws Exception {
        // delay underlying call to execute(Runnable) until after abortAll() is invoked
        Executor executor = ((Executor) (Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ Executor.class }, new AbortableTaskServiceJUnitTest.DelayedExecutorHandler(Executors.newSingleThreadExecutor(), "execute"))));
        this.tasks = new AbortableTaskService(executor);
        AbortableTaskServiceJUnitTest.DelayedTask dt = new AbortableTaskServiceJUnitTest.DelayedTask();
        AbortableTaskServiceJUnitTest.DelayedTask dt2 = new AbortableTaskServiceJUnitTest.DelayedTask();
        this.tasks.execute(dt);
        this.tasks.execute(dt2);
        Future<Boolean> future = executorServiceRule.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                tasks.waitForCompletion();
                return tasks.isCompleted();
            }
        });
        this.tasks.abortAll();
        this.delay.countDown();
        Assert.assertTrue(future.get(AbortableTaskServiceJUnitTest.TIMEOUT_SECONDS, TimeUnit.SECONDS));
        Assert.assertTrue(dt.wasAborted.get());
        Assert.assertTrue(dt2.wasAborted.get());
        Assert.assertFalse(dt.wasRun.get());
        Assert.assertFalse(dt2.wasRun.get());
        Assert.assertTrue(this.tasks.isCompleted());
    }

    /**
     * AbortableTask that waits on the CountDownLatch proceeding.
     */
    private class DelayedTask implements AbortableTask {
        private final AtomicBoolean wasAborted = new AtomicBoolean(false);

        private final AtomicBoolean wasRun = new AtomicBoolean(false);

        @Override
        public void runOrAbort(AtomicBoolean aborted) {
            try {
                delay.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            this.wasRun.set(true);
            this.wasAborted.set(aborted.get());
        }

        @Override
        public void abortBeforeRun() {
            this.wasAborted.set(true);
        }
    }

    /**
     * Proxy handler which invokes methodName asynchronously AND delays the invocation to the
     * underlying methodName until after CountDownLatch is opened.
     */
    private class DelayedExecutorHandler implements InvocationHandler {
        private final Executor executor;

        private final String methodName;

        private final Executor async;

        public DelayedExecutorHandler(Executor executor, String methodName) {
            this.executor = executor;
            this.methodName = methodName;
            this.async = Executors.newSingleThreadExecutor();
        }

        @Override
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            this.async.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (method.getName().equals(methodName)) {
                            delay.await();
                        }
                        method.invoke(executor, args);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
            });
            return null;
        }
    }
}

