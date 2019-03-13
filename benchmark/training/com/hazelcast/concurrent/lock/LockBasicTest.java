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
import com.hazelcast.core.ILock;
import com.hazelcast.internal.partition.InternalPartitionService;
import com.hazelcast.internal.partition.impl.InternalPartitionImpl;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public abstract class LockBasicTest extends HazelcastTestSupport {
    protected HazelcastInstance[] instances;

    protected ILock lock;

    // ======================== lock ==================================================
    @Test(timeout = 60000)
    public void testLock_whenNotLocked() {
        lock.lock();
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testLock_whenLockedBySelf() {
        lock.lock();
        lock.lock();
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testLock_whenLockedByOther() throws InterruptedException {
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread() {
            public void run() {
                lock.lock();
                latch.countDown();
            }
        };
        t.start();
        Assert.assertFalse(latch.await(3000, TimeUnit.MILLISECONDS));
    }

    // ======================== try lock ==============================================
    @Test(timeout = 60000)
    public void testTryLock_whenNotLocked() {
        boolean result = lock.tryLock();
        Assert.assertTrue(result);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLock_whenLockedBySelf() {
        lock.lock();
        boolean result = lock.tryLock();
        Assert.assertTrue(result);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLock_whenLockedByOther() {
        LockTestUtils.lockByOtherThread(lock);
        boolean result = lock.tryLock();
        Assert.assertFalse(result);
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
    }

    // ======================== try lock with timeout ==============================================
    @Test(timeout = 60000)
    public void testTryLockTimeout_whenNotLocked() throws InterruptedException {
        boolean result = lock.tryLock(1, TimeUnit.SECONDS);
        Assert.assertTrue(result);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLockTimeout_whenLockedBySelf() throws InterruptedException {
        lock.lock();
        boolean result = lock.tryLock(1, TimeUnit.SECONDS);
        Assert.assertTrue(result);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLockTimeout_whenLockedByOtherAndTimeout() throws InterruptedException {
        LockTestUtils.lockByOtherThread(lock);
        boolean result = lock.tryLock(1, TimeUnit.SECONDS);
        Assert.assertFalse(result);
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testTryLockTimeout_whenLockedByOtherAndEventuallyAvailable() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                latch.countDown();
                HazelcastTestSupport.sleepSeconds(1);
                lock.unlock();
            }
        }).start();
        latch.await();
        Assert.assertTrue(lock.tryLock(30, TimeUnit.SECONDS));
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
    }

    @Test(timeout = 60000, expected = NullPointerException.class)
    public void testTryLockTimeout_whenNullTimeout() throws InterruptedException {
        lock.tryLock(1, null);
    }

    // ======================== unlock ==============================================
    @Test(expected = IllegalMonitorStateException.class)
    public void testUnlock_whenFree() {
        lock.unlock();
    }

    @Test(timeout = 60000)
    public void testUnlock_whenLockedBySelf() {
        lock.lock();
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testUnlock_whenReentrantlyLockedBySelf() {
        lock.lock();
        lock.lock();
        lock.unlock();
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testUnlock_whenPendingLockOfOtherThread() throws InterruptedException {
        lock.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                latch.countDown();
            }
        });
        thread.start();
        lock.unlock();
        latch.await();
        Assert.assertTrue(lock.isLocked());
        Assert.assertFalse(lock.isLockedByCurrentThread());
    }

    @Test(timeout = 60000)
    public void testUnlock_whenLockedByOther() {
        LockTestUtils.lockByOtherThread(lock);
        try {
            lock.unlock();
            Assert.fail();
        } catch (IllegalMonitorStateException expected) {
        }
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
    }

    // ======================== force unlock ==============================================
    @Test(timeout = 60000)
    public void testForceUnlock_whenLockNotOwned() {
        lock.forceUnlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testForceUnlock_whenOwnedByOtherThread() {
        lock.lock();
        lock.forceUnlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testForceUnlock_whenAcquiredByCurrentThread() {
        lock.lock();
        lock.forceUnlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testForceUnlock_whenAcquiredMultipleTimesByCurrentThread() {
        lock.lock();
        lock.lock();
        lock.forceUnlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
    }

    // ========================= lease time ==============================================
    @Test
    public void testLockLeaseTime_whenLockAcquiredTwice() {
        lock.lock(1000, TimeUnit.MILLISECONDS);
        lock.lock(1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        }, 20);
    }

    @Test(expected = NullPointerException.class, timeout = 60000)
    public void testLockLeaseTime_whenNullTimeout() {
        lock.lock(1000, null);
    }

    @Test
    public void testLockLeaseTime_lockIsReleasedEventuallyWhenPartitionIsMigrating() {
        final InternalPartitionService ps = HazelcastTestSupport.getNode(instances[((instances.length) - 1)]).nodeEngine.getPartitionService();
        final int partitionId = ps.getPartitionId(lock.getName());
        final InternalPartitionImpl partition = ((InternalPartitionImpl) (ps.getPartition(partitionId)));
        final int leaseTime = 1000;
        lock.lock(leaseTime, TimeUnit.MILLISECONDS);
        partition.setMigrating(true);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep((leaseTime + 4000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                partition.setMigrating(false);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        }, 30);
    }

    @Test(timeout = 60000)
    public void testLockLeaseTime_whenLockFree() {
        lock.lock(1000, TimeUnit.MILLISECONDS);
    }

    @Test(timeout = 60000)
    public void testLockLeaseTime_whenLockAcquiredByOther() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                lock.lock();
                latch.countDown();
                HazelcastTestSupport.sleepMillis(500);
                lock.unlock();
            }
        }.start();
        latch.await();
        lock.lock(4000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(lock.isLocked());
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        });
    }

    @Test
    public void testLockLeaseTime_lockIsReleasedEventually() throws InterruptedException {
        lock.lock(1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        }, 30);
    }

    // ========================= tryLock with lease time ==============================================
    @Test
    public void testTryLockLeaseTime_whenLockAcquiredTwice() throws InterruptedException {
        lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        }, 5);
    }

    @Test(expected = NullPointerException.class, timeout = 60000)
    public void testTryLockLeaseTime_whenNullTimeout() throws InterruptedException {
        lock.tryLock(1000, null, 1000, TimeUnit.MILLISECONDS);
    }

    @Test(expected = NullPointerException.class, timeout = 60000)
    public void testTryLockLeaseTime_whenNullLeaseTimeout() throws InterruptedException {
        lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, null);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockFree() throws InterruptedException {
        boolean isLocked = lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(isLocked);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockAcquiredByOther() throws InterruptedException {
        Thread thread = new Thread() {
            public void run() {
                lock.lock();
            }
        };
        thread.start();
        thread.join();
        boolean isLocked = lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertFalse(isLocked);
    }

    @Test
    public void testTryLockLeaseTime_lockIsReleasedEventually() throws InterruptedException {
        lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        }, 30);
    }

    // =======================================================================
    @Test(timeout = 60000)
    public void testTryLock_whenMultipleThreads() throws InterruptedException {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        lock.lock();
        Runnable tryLockRunnable = new Runnable() {
            public void run() {
                if (lock.tryLock()) {
                    atomicInteger.incrementAndGet();
                }
            }
        };
        Thread thread1 = new Thread(tryLockRunnable);
        thread1.start();
        thread1.join();
        Assert.assertEquals(0, atomicInteger.get());
        lock.unlock();
        Thread thread2 = new Thread(tryLockRunnable);
        thread2.start();
        thread2.join();
        Assert.assertEquals(1, atomicInteger.get());
        Assert.assertTrue(lock.isLocked());
        Assert.assertFalse(lock.isLockedByCurrentThread());
    }

    @Test(timeout = 60000)
    public void testLockUnlock() {
        Assert.assertFalse(lock.isLocked());
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
    }

    @Test(timeout = 60000)
    public void testTryLock() {
        Assert.assertFalse(lock.isLocked());
        Assert.assertTrue(lock.tryLock());
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
    }

    @Test(timeout = 60000, expected = DistributedObjectDestroyedException.class)
    public void testDestroyLockWhenOtherWaitingOnLock() throws InterruptedException {
        Thread t = new Thread(new Runnable() {
            public void run() {
                lock.lock();
            }
        });
        t.start();
        t.join();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.destroy();
            }
        }).start();
        lock.lock();
    }

    @Test
    public void test_whenLockDestroyed_thenUnlocked() {
        lock.lock();
        lock.destroy();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse("Lock should have been unlocked by destroy.", lock.isLocked());
            }
        });
    }

    @Test
    public void test_whenLockDestroyedFromAnotherThread_thenUnlocked() {
        lock.lock();
        Thread thread = new Thread() {
            public void run() {
                lock.destroy();
            }
        };
        thread.start();
        HazelcastTestSupport.assertJoinable(thread);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse("Lock should have been unlocked by destroy.", lock.isLocked());
            }
        });
    }

    @Test
    public void testLockCount() throws Exception {
        lock.lock();
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertTrue(lock.tryLock());
        Assert.assertEquals(2, lock.getLockCount());
        lock.unlock();
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertTrue(lock.isLocked());
        lock.unlock();
        Assert.assertEquals(0, lock.getLockCount());
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals((-1L), lock.getRemainingLeaseTime());
    }
}

