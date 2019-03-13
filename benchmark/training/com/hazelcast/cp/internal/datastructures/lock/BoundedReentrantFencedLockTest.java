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
package com.hazelcast.cp.internal.datastructures.lock;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.cp.lock.exception.LockAcquireLimitExceededException;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BoundedReentrantFencedLockTest extends HazelcastRaftTestSupport {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected HazelcastInstance[] instances;

    protected HazelcastInstance lockInstance;

    protected FencedLock lock;

    private final String objectName = "lock";

    @Test
    public void testLock() {
        lock.lock();
        lock.lock();
        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testLockAndGetFence() {
        long fence1 = lock.lockAndGetFence();
        long fence2 = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence1);
        Assert.assertEquals(fence1, fence2);
        Assert.assertEquals(fence1, lock.getFence());
        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testTryLock() {
        boolean locked1 = lock.tryLock();
        boolean locked2 = lock.tryLock();
        Assert.assertTrue(locked1);
        Assert.assertTrue(locked2);
        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testTryLockAndGetFence() {
        long fence1 = lock.tryLockAndGetFence();
        long fence2 = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence1);
        Assert.assertEquals(fence1, fence2);
        Assert.assertEquals(fence1, lock.getFence());
        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testTryLockTimeout() {
        boolean locked1 = lock.tryLock(1, TimeUnit.SECONDS);
        boolean locked2 = lock.tryLock(1, TimeUnit.SECONDS);
        Assert.assertTrue(locked1);
        Assert.assertTrue(locked2);
        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testTryLockAndGetFenceTimeout() {
        long fence1 = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        long fence2 = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        FencedLockBasicTest.assertValidFence(fence1);
        Assert.assertEquals(fence1, fence2);
        Assert.assertEquals(fence1, lock.getFence());
        lock.unlock();
        lock.unlock();
    }

    @Test
    public void testTryLockWhileLockedByAnotherEndpoint() {
        FencedLockBasicTest.lockByOtherThread(lock);
        boolean locked = lock.tryLock();
        Assert.assertFalse(locked);
    }

    @Test
    public void testTryLockTimeoutWhileLockedByAnotherEndpoint() {
        FencedLockBasicTest.lockByOtherThread(lock);
        boolean locked = lock.tryLock(1, TimeUnit.SECONDS);
        Assert.assertFalse(locked);
    }

    @Test
    public void testReentrantLockFails() {
        lock.lock();
        lock.lock();
        expectedException.expect(LockAcquireLimitExceededException.class);
        lock.lock();
    }

    @Test
    public void testReentrantTryLockFails() {
        lock.lock();
        lock.lock();
        boolean locked = lock.tryLock();
        Assert.assertFalse(locked);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
        FencedLockBasicTest.assertValidFence(lock.getFence());
    }

    @Test
    public void testReentrantTryLockAndGetFenceFails() {
        lock.lock();
        lock.lock();
        long fence1 = lock.getFence();
        long fence2 = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertInvalidFence(fence2);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
        Assert.assertEquals(fence1, lock.getFence());
    }

    @Test
    public void testReentrantTryLockAndGetFenceWithTimeoutFails() {
        lock.lock();
        lock.lock();
        long fence1 = lock.getFence();
        long fence2 = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        FencedLockBasicTest.assertInvalidFence(fence2);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
        Assert.assertEquals(fence1, lock.getFence());
    }

    @Test
    public void testReentrantLock_afterLockIsReleasedByAnotherEndpoint() {
        lock.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                lock.lock();
                lock.unlock();
                lock.unlock();
                latch.countDown();
            }
        });
        lock.unlock();
        HazelcastTestSupport.assertOpenEventually(latch);
    }
}

