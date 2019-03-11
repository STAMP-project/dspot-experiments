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
package org.apache.geode.internal.util.concurrent;


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class ReentrantSemaphoreJUnitTest {
    private static final long OPERATION_TIMEOUT_MILLIS = 10 * 1000;

    private CountDownLatch done;

    private CountDownLatch acquired;

    @Rule
    public Timeout timeout = new Timeout(30, TimeUnit.SECONDS);

    @Test
    public void testOneThread() throws Exception {
        final ReentrantSemaphore semaphore = new ReentrantSemaphore(2);
        semaphore.acquire();
        semaphore.acquire();
        Assert.assertEquals(1, semaphore.availablePermits());
        semaphore.release();
        semaphore.release();
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    @Test
    public void testMultipleThreads() throws Exception {
        final ReentrantSemaphore sem = new ReentrantSemaphore(2);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread t1 = new Thread() {
            @Override
            public void run() {
                try {
                    sem.acquire();
                    sem.acquire();
                    sem.acquire();
                    acquired.countDown();
                    Assert.assertTrue(done.await(ReentrantSemaphoreJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                    sem.release();
                    sem.release();
                    sem.release();
                } catch (Exception e) {
                    failure.compareAndSet(null, e);
                }
            }
        };
        t1.start();
        Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    sem.acquire();
                    sem.acquire();
                    sem.acquire();
                    acquired.countDown();
                    Assert.assertTrue(done.await(ReentrantSemaphoreJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                    sem.release();
                    sem.release();
                    sem.release();
                } catch (Exception e) {
                    failure.compareAndSet(null, e);
                }
            }
        };
        t2.start();
        Thread t3 = new Thread() {
            @Override
            public void run() {
                try {
                    Assert.assertTrue(acquired.await(ReentrantSemaphoreJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                    Assert.assertEquals(0, sem.availablePermits());
                    Assert.assertFalse(sem.tryAcquire(1, TimeUnit.SECONDS));
                } catch (Exception e) {
                    failure.compareAndSet(null, e);
                }
            }
        };
        t3.start();
        t3.join(ReentrantSemaphoreJUnitTest.OPERATION_TIMEOUT_MILLIS);
        Assert.assertFalse(t3.isAlive());
        done.countDown();
        t2.join(ReentrantSemaphoreJUnitTest.OPERATION_TIMEOUT_MILLIS);
        Assert.assertFalse(t3.isAlive());
        t1.join(ReentrantSemaphoreJUnitTest.OPERATION_TIMEOUT_MILLIS);
        Assert.assertFalse(t1.isAlive());
        if ((failure.get()) != null) {
            throw new AssertionError(failure.get());
        }
        Assert.assertEquals(2, sem.availablePermits());
    }
}

