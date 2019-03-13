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
package com.hazelcast.concurrent.lock;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICondition;
import com.hazelcast.core.ILock;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Test;


public abstract class ConditionAbstractTest extends HazelcastTestSupport {
    private static final int THIRTY_SECONDS = 30;

    protected HazelcastInstance[] instances;

    private HazelcastInstance callerInstance;

    @Test(expected = UnsupportedOperationException.class)
    public void testNewConditionWithoutNameIsNotSupported() {
        ILock lock = callerInstance.getLock(newName());
        lock.newCondition();
    }

    @Test(timeout = 60000, expected = NullPointerException.class)
    public void testNewCondition_whenNullName() {
        ILock lock = callerInstance.getLock(newName());
        lock.newCondition(null);
    }

    @Test
    public void testAwaitNanos_remainingTime() throws InterruptedException {
        String name = newName();
        ILock lock = callerInstance.getLock(name);
        ICondition condition = lock.newCondition(name);
        lock.lock();
        long remainingTimeout = condition.awaitNanos(1000L);
        Assert.assertTrue(("Remaining timeout should be <= 0, but it's = " + remainingTimeout), (remainingTimeout <= 0));
    }

    @Test(timeout = 60000)
    public void testMultipleConditionsForSameLock() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition0 = lock.newCondition(newName());
        ICondition condition1 = lock.newCondition(newName());
        CountDownLatch allAwaited = new CountDownLatch(2);
        CountDownLatch allSignalled = new CountDownLatch(2);
        startThreadWaitingOnCondition(lock, condition0, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock, condition1, allAwaited, allSignalled);
        HazelcastTestSupport.assertOpenEventually("All threads should have been reached await", allAwaited);
        ConditionAbstractTest.signal(lock, condition0);
        ConditionAbstractTest.signal(lock, condition1);
        HazelcastTestSupport.assertOpenEventually("All threads should have been signalled", allSignalled);
    }

    @Test(timeout = 60000)
    public void testSignalAll() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        CountDownLatch allAwaited = new CountDownLatch(2);
        CountDownLatch allSignalled = new CountDownLatch(2);
        startThreadWaitingOnCondition(lock, condition, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock, condition, allAwaited, allSignalled);
        HazelcastTestSupport.assertOpenEventually("All threads should have been reached await", allAwaited);
        ConditionAbstractTest.signalAll(lock, condition);
        HazelcastTestSupport.assertOpenEventually("All threads should have been signalled", allSignalled);
    }

    @Test(timeout = 60000)
    public void testSignalAll_whenMultipleConditions() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition0 = lock.newCondition(newName());
        ICondition condition1 = lock.newCondition(newName());
        CountDownLatch allAwaited = new CountDownLatch(2);
        CountDownLatch allSignalled = new CountDownLatch(10);
        startThreadWaitingOnCondition(lock, condition0, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock, condition1, allAwaited, allSignalled);
        HazelcastTestSupport.assertOpenEventually("All threads should have been reached await", allAwaited);
        ConditionAbstractTest.signalAll(lock, condition0);
        HazelcastTestSupport.assertCountEventually("Condition has not been signalled", 9, allSignalled, ConditionAbstractTest.THIRTY_SECONDS);
    }

    @Test(timeout = 60000)
    public void testSameConditionRetrievedMultipleTimesForSameLock() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        String name = newName();
        ICondition condition0 = lock.newCondition(name);
        ICondition condition1 = lock.newCondition(name);
        CountDownLatch allAwaited = new CountDownLatch(2);
        CountDownLatch allSignalled = new CountDownLatch(2);
        startThreadWaitingOnCondition(lock, condition0, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock, condition1, allAwaited, allSignalled);
        HazelcastTestSupport.assertOpenEventually("All threads should have been reached await", allAwaited);
        ConditionAbstractTest.signalAll(lock, condition0);
        HazelcastTestSupport.assertOpenEventually("All threads should have been signalled", allSignalled);
    }

    @Test
    public void testAwaitTime_whenTimeout() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        lock.lock();
        boolean success = condition.await(1, TimeUnit.MILLISECONDS);
        Assert.assertFalse(success);
        Assert.assertTrue(lock.isLockedByCurrentThread());
    }

    @Test(timeout = 60000)
    public void testConditionsWithSameNameButDifferentLocksAreIndependent() throws InterruptedException {
        String name = newName();
        ILock lock0 = callerInstance.getLock(newName());
        ICondition condition0 = lock0.newCondition(name);
        ILock lock1 = callerInstance.getLock(newName());
        ICondition condition1 = lock1.newCondition(name);
        CountDownLatch allAwaited = new CountDownLatch(2);
        CountDownLatch allSignalled = new CountDownLatch(2);
        startThreadWaitingOnCondition(lock0, condition0, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock1, condition1, allAwaited, allSignalled);
        HazelcastTestSupport.assertOpenEventually("All threads should have been reached await", allAwaited);
        ConditionAbstractTest.signalAll(lock0, condition0);
        ConditionAbstractTest.signalAll(lock1, condition1);
        HazelcastTestSupport.assertOpenEventually(allSignalled);
    }

    @Test(timeout = 60000)
    public void testSignalWithSingleWaiter() throws InterruptedException {
        String lockName = newName();
        String conditionName = newName();
        final ILock lock = callerInstance.getLock(lockName);
        final ICondition condition = lock.newCondition(conditionName);
        final AtomicInteger count = new AtomicInteger(0);
        final CountDownLatch threadLockedTheLock = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    lock.lock();
                    if (lock.isLockedByCurrentThread()) {
                        count.incrementAndGet();
                    }
                    threadLockedTheLock.countDown();
                    condition.await();
                    if (lock.isLockedByCurrentThread()) {
                        count.incrementAndGet();
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    lock.unlock();
                }
            }
        });
        t.start();
        threadLockedTheLock.await();
        ConditionAbstractTest.assertUnlockedEventually(lock, ConditionAbstractTest.THIRTY_SECONDS);
        ConditionAbstractTest.signal(lock, condition);
        HazelcastTestSupport.assertAtomicEventually("Locks was not always locked by the expected thread", 2, count, ConditionAbstractTest.THIRTY_SECONDS);
    }

    @Test(timeout = 60000)
    public void testSignalAllWithSingleWaiter() throws InterruptedException {
        String lockName = newName();
        String conditionName = newName();
        final ILock lock = callerInstance.getLock(lockName);
        final ICondition condition = lock.newCondition(conditionName);
        final AtomicInteger count = new AtomicInteger(0);
        final int k = 50;
        final CountDownLatch allAwaited = new CountDownLatch(k);
        final CountDownLatch allFinished = new CountDownLatch(k);
        for (int i = 0; i < k; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        lock.lock();
                        if (lock.isLockedByCurrentThread()) {
                            count.incrementAndGet();
                        }
                        allAwaited.countDown();
                        condition.await();
                        if (lock.isLockedByCurrentThread()) {
                            count.incrementAndGet();
                        }
                    } catch (InterruptedException ignored) {
                    } finally {
                        lock.unlock();
                        allFinished.countDown();
                    }
                }
            }).start();
        }
        allAwaited.await(1, TimeUnit.MINUTES);
        ConditionAbstractTest.assertUnlockedEventually(lock, ConditionAbstractTest.THIRTY_SECONDS);
        // Make sure that all threads are waiting on condition await call
        Thread.sleep(3000);
        ConditionAbstractTest.signalAll(lock, condition);
        allFinished.await(1, TimeUnit.MINUTES);
        Assert.assertEquals((k * 2), count.get());
    }

    /**
     * Testcase for #3025. Tests that an await with short duration in a highly-contended lock does not generate an
     * IllegalStateException (previously due to a race condition in the waiter list for the condition).
     */
    @Test(timeout = 60000)
    public void testContendedLockUnlockWithVeryShortAwait() throws InterruptedException {
        String lockName = newName();
        String conditionName = newName();
        final ILock lock = callerInstance.getLock(lockName);
        final ICondition condition = lock.newCondition(conditionName);
        final AtomicBoolean running = new AtomicBoolean(true);
        final AtomicReference<Exception> errorRef = new AtomicReference<Exception>();
        final int numberOfThreads = 8;
        final CountDownLatch allFinished = new CountDownLatch(numberOfThreads);
        ExecutorService ex = Executors.newCachedThreadPool();
        for (int i = 0; i < numberOfThreads; i++) {
            ex.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (running.get()) {
                            lock.lock();
                            try {
                                condition.await(1, TimeUnit.MILLISECONDS);
                            } catch (InterruptedException ignored) {
                            } catch (IllegalStateException e) {
                                errorRef.set(e);
                                running.set(false);
                            } finally {
                                lock.unlock();
                            }
                        } 
                    } finally {
                        allFinished.countDown();
                    }
                }
            });
        }
        ex.execute(new Runnable() {
            @Override
            public void run() {
                LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(10));
                running.set(false);
            }
        });
        try {
            allFinished.await(30, TimeUnit.SECONDS);
            Assert.assertNull("await() on condition threw IllegalStateException!", errorRef.get());
        } finally {
            ex.shutdownNow();
        }
    }

    @Test
    public void testAwaitExpiration_whenLockIsNotAcquired() throws InterruptedException {
        String lockName = newName();
        String conditionName = newName();
        final ILock lock = callerInstance.getLock(lockName);
        final ICondition condition = lock.newCondition(conditionName);
        final AtomicInteger expires = new AtomicInteger(0);
        final int awaitCount = 10;
        final CountDownLatch awaitLatch = new CountDownLatch(awaitCount);
        for (int i = 0; i < awaitCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        lock.lock();
                        awaitLatch.countDown();
                        boolean signalled = condition.await(1, TimeUnit.SECONDS);
                        Assert.assertFalse(signalled);
                        expires.incrementAndGet();
                    } catch (InterruptedException ignored) {
                    } finally {
                        lock.unlock();
                    }
                }
            }).start();
        }
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(awaitCount, expires.get());
            }
        });
    }

    @Test
    public void testAwaitExpiration_whenLockIsAcquiredByAnotherThread() throws InterruptedException {
        String lockName = newName();
        String conditionName = newName();
        final ILock lock = callerInstance.getLock(lockName);
        final ICondition condition = lock.newCondition(conditionName);
        final AtomicInteger expires = new AtomicInteger(0);
        final int awaitCount = 10;
        final CountDownLatch awaitLatch = new CountDownLatch(awaitCount);
        for (int i = 0; i < awaitCount; i++) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        lock.lock();
                        awaitLatch.countDown();
                        boolean signalled = condition.await(1, TimeUnit.SECONDS);
                        Assert.assertFalse(signalled);
                        expires.incrementAndGet();
                    } catch (InterruptedException ignored) {
                    } finally {
                        lock.unlock();
                    }
                }
            }).start();
        }
        awaitLatch.await(2, TimeUnit.MINUTES);
        lock.lock();
        HazelcastTestSupport.sleepSeconds(2);
        lock.unlock();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertEquals(awaitCount, expires.get());
            }
        });
    }

    // if there are multiple waiters, then only 1 waiter should be notified.
    @Test
    public void testSignalWithMultipleWaiters() {
        final ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        CountDownLatch allAwaited = new CountDownLatch(3);
        CountDownLatch allSignalled = new CountDownLatch(10);
        startThreadWaitingOnCondition(lock, condition, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock, condition, allAwaited, allSignalled);
        startThreadWaitingOnCondition(lock, condition, allAwaited, allSignalled);
        HazelcastTestSupport.assertOpenEventually("All threads should have been reached await", allAwaited);
        ConditionAbstractTest.signal(lock, condition);
        HazelcastTestSupport.assertCountEventually("Condition has not been signalled", 9, allSignalled, ConditionAbstractTest.THIRTY_SECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(lock.isLocked());
            }
        });
    }

    // A signal send to wake up threads is not a flag set on the condition
    // Threads calling await() after signal() has been called will hand on waiting.
    @Test(timeout = 60000)
    public void testSignalIsNotStored() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        CountDownLatch signalled = new CountDownLatch(1);
        ConditionAbstractTest.signal(lock, condition);
        startThreadWaitingOnCondition(lock, condition, new CountDownLatch(0), signalled);
        Assert.assertFalse("The time should elapse but the latch reached zero unexpectedly", signalled.await(3000, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000, expected = IllegalMonitorStateException.class)
    public void testAwaitOnConditionOfFreeLock() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition("condition");
        condition.await();
    }

    @Test(timeout = 60000, expected = IllegalMonitorStateException.class)
    public void testSignalOnConditionOfFreeLock() {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition("condition");
        condition.signal();
    }

    @Test(timeout = 60000, expected = IllegalMonitorStateException.class)
    public void testAwait_whenOwnedByOtherThread() throws InterruptedException {
        final ILock lock = callerInstance.getLock(newName());
        final ICondition condition = lock.newCondition(newName());
        releaseLockInSeparateThread(lock);
        condition.await();
    }

    @Test(timeout = 60000, expected = IllegalMonitorStateException.class)
    public void testSignal_whenOwnedByOtherThread() throws InterruptedException {
        final ILock lock = callerInstance.getLock(newName());
        final ICondition condition = lock.newCondition(newName());
        releaseLockInSeparateThread(lock);
        condition.signal();
    }

    @Test(timeout = 60000)
    public void testAwaitTimeout_whenFail() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        lock.lock();
        Assert.assertFalse(condition.await(1, TimeUnit.MILLISECONDS));
    }

    @Test(timeout = 60000)
    public void testAwaitTimeout_whenSuccess() throws InterruptedException {
        final ILock lock = callerInstance.getLock(newName());
        final ICondition condition = lock.newCondition(newName());
        final CountDownLatch locked = new CountDownLatch(1);
        final AtomicBoolean signalledCorrectly = new AtomicBoolean(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                locked.countDown();
                try {
                    if (condition.await(10, TimeUnit.SECONDS)) {
                        signalledCorrectly.set(true);
                    }
                } catch (InterruptedException e) {
                    HazelcastTestSupport.ignore(e);
                }
            }
        }).start();
        locked.await();
        ConditionAbstractTest.signal(lock, condition);
        HazelcastTestSupport.assertAtomicEventually("awaiting thread should have been signalled", true, signalledCorrectly, ConditionAbstractTest.THIRTY_SECONDS);
    }

    @Test(timeout = 60000)
    public void testAwaitUntil_whenSuccess() throws InterruptedException {
        final ILock lock = callerInstance.getLock(newName());
        final ICondition condition = lock.newCondition(newName());
        final CountDownLatch locked = new CountDownLatch(1);
        final AtomicBoolean signalledCorrectly = new AtomicBoolean(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                locked.countDown();
                try {
                    if (condition.awaitUntil(ConditionAbstractTest.currentTimeAfterGivenMillis(10000))) {
                        signalledCorrectly.set(true);
                    }
                } catch (InterruptedException e) {
                    HazelcastTestSupport.ignore(e);
                }
            }
        }).start();
        locked.await();
        ConditionAbstractTest.signal(lock, condition);
        HazelcastTestSupport.assertAtomicEventually("awaiting thread should have been signalled", true, signalledCorrectly, ConditionAbstractTest.THIRTY_SECONDS);
    }

    @Test(timeout = 60000)
    public void testAwaitUntil_whenFail() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        lock.lock();
        Assert.assertFalse(condition.awaitUntil(ConditionAbstractTest.currentTimeAfterGivenMillis(1000)));
    }

    @Test(timeout = 60000)
    public void testAwaitUntil_whenDeadLineInThePast() throws InterruptedException {
        ILock lock = callerInstance.getLock(newName());
        ICondition condition = lock.newCondition(newName());
        lock.lock();
        Assert.assertFalse(condition.awaitUntil(ConditionAbstractTest.currentTimeAfterGivenMillis((-1000))));
    }
}

