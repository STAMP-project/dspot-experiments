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
package com.hazelcast.client.lock;


import GroupProperty.LOCK_MAX_LEASE_TIME_SECONDS;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.config.Config;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ILock;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ExceptionUtil;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientLockTest extends HazelcastTestSupport {
    private TestHazelcastFactory factory = new TestHazelcastFactory();

    @Test
    public void testLock() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        lock.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                if (!(lock.tryLock())) {
                    latch.countDown();
                }
            }
        }.start();
        assertOpenEventually(latch);
        lock.forceUnlock();
    }

    @Test
    public void testTryLockShouldSucceedWhenLockTTLisFinished() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        final int lockTimeout = 3;
        lock.lock(lockTimeout, TimeUnit.SECONDS);
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    // Allow half the ASSERT_TRUE_EVENTUALLY_TIMEOUT for any possible gc and other pauses
                    if (lock.tryLock((lockTimeout + ((ASSERT_TRUE_EVENTUALLY_TIMEOUT) / 2)), TimeUnit.SECONDS)) {
                        latch.countDown();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        assertOpenEventually(latch);
    }

    @Test
    public void testTryLock() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        Assert.assertTrue(lock.tryLock(2, TimeUnit.SECONDS));
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    if (!(lock.tryLock(2, TimeUnit.SECONDS))) {
                        latch.countDown();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Assert.assertTrue(latch.await(100, TimeUnit.SECONDS));
        Assert.assertTrue(lock.isLocked());
        final CountDownLatch latch2 = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    if (lock.tryLock(20, TimeUnit.SECONDS)) {
                        latch2.countDown();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        Thread.sleep(1000);
        lock.unlock();
        Assert.assertTrue(latch2.await(100, TimeUnit.SECONDS));
        Assert.assertTrue(lock.isLocked());
        lock.forceUnlock();
    }

    @Test
    public void testTryLockwithZeroTTL() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        boolean lockWithZeroTTL = lock.tryLock(0, TimeUnit.SECONDS);
        Assert.assertTrue(lockWithZeroTTL);
    }

    @Test
    public void testTryLockwithZeroTTLWithExistingLock() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        lock.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                try {
                    if (!(lock.tryLock(0, TimeUnit.SECONDS))) {
                        latch.countDown();
                    }
                } catch (InterruptedException e) {
                }
            }
        }.start();
        assertOpenEventually(latch);
        lock.forceUnlock();
    }

    @Test
    public void testForceUnlock() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        lock.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                lock.forceUnlock();
                latch.countDown();
            }
        }.start();
        Assert.assertTrue(latch.await(100, TimeUnit.SECONDS));
        Assert.assertFalse(lock.isLocked());
    }

    @Test
    public void testStats() throws InterruptedException {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
        Assert.assertEquals((-1L), lock.getRemainingLeaseTime());
        lock.lock(1, TimeUnit.MINUTES);
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertTrue(((lock.getRemainingLeaseTime()) > (1000 * 30)));
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread() {
            public void run() {
                Assert.assertTrue(lock.isLocked());
                Assert.assertFalse(lock.isLockedByCurrentThread());
                Assert.assertEquals(1, lock.getLockCount());
                Assert.assertTrue(((lock.getRemainingLeaseTime()) > (1000 * 30)));
                latch.countDown();
            }
        }.start();
        Assert.assertTrue(latch.await(1, TimeUnit.MINUTES));
    }

    @Test
    public void testObtainLock_FromDifferentClients() throws InterruptedException {
        newHazelcastInstance();
        String name = randomName();
        HazelcastInstance clientA = factory.newHazelcastClient();
        ILock lockA = clientA.getLock(name);
        lockA.lock();
        HazelcastInstance clientB = factory.newHazelcastClient();
        ILock lockB = clientB.getLock(name);
        boolean lockObtained = lockB.tryLock();
        Assert.assertFalse("Lock obtained by 2 client ", lockObtained);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockFree() throws InterruptedException {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        boolean isLocked = lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(isLocked);
    }

    @Test(timeout = 60000)
    public void testTryLockLeaseTime_whenLockAcquiredByOther() throws InterruptedException {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
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
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        lock.tryLock(1000, TimeUnit.MILLISECONDS, 1000, TimeUnit.MILLISECONDS);
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse(lock.isLocked());
            }
        }, 30);
    }

    @Test
    public void testMaxLockLeaseTime() {
        Config config = new Config();
        config.setProperty(LOCK_MAX_LEASE_TIME_SECONDS.getName(), "1");
        factory.newHazelcastInstance(config);
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        lock.lock();
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse("Lock should be released after lease expires!", lock.isLocked());
            }
        }, 30);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLockFail_whenGreaterThanMaxLeaseTimeUsed() {
        Config config = new Config();
        config.setProperty(LOCK_MAX_LEASE_TIME_SECONDS.getName(), "1");
        factory.newHazelcastInstance(config);
        HazelcastInstance hz = factory.newHazelcastClient();
        ILock lock = hz.getLock(randomName());
        lock.lock(10, TimeUnit.SECONDS);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLockWithZeroTTL() {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(randomName());
        final long ttl = 0;
        lock.lock(ttl, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testLockLease_withStringPartitionAwareName() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(((randomName()) + "@hazelcast"));
        spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock(5, TimeUnit.SECONDS);
            }
        }).get();
        Assert.assertTrue("Lock should have been released after lease expires", lock.tryLock(2, TimeUnit.MINUTES));
    }

    @Test
    public void testTryLockLease_withStringPartitionAwareName() throws Exception {
        newHazelcastInstance();
        HazelcastInstance hz = factory.newHazelcastClient();
        final ILock lock = hz.getLock(((randomName()) + "@hazelcast"));
        spawn(new Runnable() {
            @Override
            public void run() {
                long timeout = 10;
                long lease = 5;
                try {
                    Assert.assertTrue(lock.tryLock(timeout, TimeUnit.SECONDS, lease, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    throw ExceptionUtil.rethrow(e);
                }
            }
        }).get();
        Assert.assertTrue("Lock should have been released after lease expires", lock.tryLock(2, TimeUnit.MINUTES));
    }
}

