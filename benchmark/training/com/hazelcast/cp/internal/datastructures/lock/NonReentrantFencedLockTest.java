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
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class NonReentrantFencedLockTest extends HazelcastRaftTestSupport {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    protected HazelcastInstance[] instances;

    protected HazelcastInstance lockInstance;

    protected FencedLock lock;

    private final String objectName = "lock";

    @Test
    public void testLock() {
        lock.lock();
        lock.unlock();
    }

    @Test
    public void testLockAndGetFence() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        lock.unlock();
    }

    @Test
    public void testTryLock() {
        boolean locked = lock.tryLock();
        Assert.assertTrue(locked);
        lock.unlock();
    }

    @Test
    public void testTryLockAndGetFence() {
        long fence = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        lock.unlock();
    }

    @Test
    public void testTryLockTimeout() {
        boolean locked = lock.tryLock(1, TimeUnit.SECONDS);
        Assert.assertTrue(locked);
        lock.unlock();
    }

    @Test
    public void testTryLockAndGetFenceTimeout() {
        long fence = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        FencedLockBasicTest.assertValidFence(fence);
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
        expectedException.expect(LockAcquireLimitExceededException.class);
        lock.lock();
    }

    @Test
    public void testReentrantTryLockFails() {
        lock.lock();
        long fence = lock.getFence();
        FencedLockBasicTest.assertValidFence(fence);
        boolean locked = lock.tryLock();
        Assert.assertFalse(locked);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertEquals(fence, lock.getFence());
    }

    @Test
    public void testReentrantTryLockAndGetFenceFails() {
        lock.lock();
        long fence1 = lock.getFence();
        FencedLockBasicTest.assertValidFence(fence1);
        long fence2 = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertInvalidFence(fence2);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertEquals(fence1, lock.getFence());
    }

    @Test
    public void testReentrantTryLockAndGetFenceWithTimeoutFails() {
        lock.lock();
        long fence1 = lock.getFence();
        FencedLockBasicTest.assertValidFence(fence1);
        long fence2 = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        FencedLockBasicTest.assertInvalidFence(fence2);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertEquals(fence1, lock.getFence());
    }
}

