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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class SemaphoreReadWriteLockJUnitTest {
    private static final long OPERATION_TIMEOUT_MILLIS = 10 * 1000;

    private CountDownLatch latch;

    private CountDownLatch waitToLock;

    @Rule
    public Timeout timeout = new Timeout(30, TimeUnit.SECONDS);

    @Test
    public void testReaderWaitsForWriter() throws Exception {
        SemaphoreReadWriteLock rwl = new SemaphoreReadWriteLock();
        final Lock rl = rwl.readLock();
        final Lock wl = rwl.writeLock();
        wl.lock();
        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                waitToLock.countDown();
                rl.lock();
                latch.countDown();
                rl.unlock();
            }
        });
        writer.start();
        Assert.assertTrue(waitToLock.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, latch.getCount());
        wl.unlock();
        Assert.assertTrue(latch.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testWriterWaitsForReader() throws Exception {
        SemaphoreReadWriteLock rwl = new SemaphoreReadWriteLock();
        final Lock rl = rwl.readLock();
        final Lock wl = rwl.writeLock();
        rl.lock();
        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                waitToLock.countDown();
                wl.lock();
                latch.countDown();
                wl.unlock();
            }
        });
        writer.start();
        Assert.assertTrue(waitToLock.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, latch.getCount());
        rl.unlock();
        Assert.assertTrue(latch.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testReadersNotBlockedByReaders() throws Exception {
        SemaphoreReadWriteLock rwl = new SemaphoreReadWriteLock();
        final Lock rl = rwl.readLock();
        final Lock wl = rwl.writeLock();
        rl.lock();
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                waitToLock.countDown();
                rl.lock();
                latch.countDown();
            }
        });
        reader.start();
        Assert.assertTrue(waitToLock.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testWritersBlockedByWriters() throws Exception {
        SemaphoreReadWriteLock rwl = new SemaphoreReadWriteLock();
        final Lock rl = rwl.readLock();
        final Lock wl = rwl.writeLock();
        wl.lock();
        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                waitToLock.countDown();
                wl.lock();
                latch.countDown();
                wl.unlock();
            }
        });
        writer.start();
        Assert.assertTrue(waitToLock.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertEquals(1, latch.getCount());
        wl.unlock();
        Assert.assertTrue(latch.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
    }

    @Test
    public void testTryLock() throws Exception {
        SemaphoreReadWriteLock rwl = new SemaphoreReadWriteLock();
        final Lock rl = rwl.readLock();
        final Lock wl = rwl.writeLock();
        Assert.assertTrue(wl.tryLock());
        final AtomicBoolean failed = new AtomicBoolean(false);
        Thread reader = new Thread(new Runnable() {
            @Override
            public void run() {
                waitToLock.countDown();
                if (rl.tryLock()) {
                    failed.set(true);
                }
                latch.countDown();
            }
        });
        reader.start();
        Assert.assertTrue(waitToLock.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertTrue(latch.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
        Assert.assertFalse(failed.get());
    }

    @Test
    public void testLockAndReleaseByDifferentThreads() throws Exception {
        SemaphoreReadWriteLock rwl = new SemaphoreReadWriteLock();
        final Lock rl = rwl.readLock();
        final Lock wl = rwl.writeLock();
        rl.lock();
        Thread writer = new Thread(new Runnable() {
            @Override
            public void run() {
                waitToLock.countDown();
                try {
                    Assert.assertTrue(wl.tryLock(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
                latch.countDown();
            }
        });
        writer.start();
        Thread reader2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Assert.assertTrue(waitToLock.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
                rl.unlock();
            }
        });
        reader2.start();
        Assert.assertTrue(latch.await(SemaphoreReadWriteLockJUnitTest.OPERATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS));
    }
}

