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


import RaftLockService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.cp.lock.exception.LockOwnershipLostException;
import com.hazelcast.spi.exception.DistributedObjectDestroyedException;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static RaftLockService.WAIT_TIMEOUT_TASK_UPPER_BOUND_MILLIS;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FencedLockBasicTest extends HazelcastRaftTestSupport {
    protected HazelcastInstance[] instances;

    protected HazelcastInstance lockInstance;

    protected FencedLock lock;

    private String objectName = "lock";

    private String proxyName = (objectName) + "@group1";

    @Test
    public void testLock_whenNotLocked() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertEquals(fence, lock.getFence());
    }

    @Test
    public void testLockInterruptibly_whenNotLocked() throws InterruptedException {
        lock.lockInterruptibly();
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        FencedLockBasicTest.assertValidFence(lock.getFence());
    }

    @Test
    public void testLock_whenLockedBySelf() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        long newFence = lock.lockAndGetFence();
        Assert.assertEquals(fence, newFence);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test
    public void testLock_whenLockedByOther() throws InterruptedException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
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
        Assert.assertFalse(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testUnlock_whenFree() {
        lock.unlock();
    }

    @Test(expected = IllegalMonitorStateException.class)
    public void testGetFence_whenFree() {
        lock.getFence();
    }

    @Test
    public void testIsLocked_whenFree() {
        Assert.assertFalse(lock.isLocked());
    }

    @Test
    public void testIsLockedByCurrentThread_whenFree() {
        Assert.assertFalse(lock.isLockedByCurrentThread());
    }

    @Test
    public void testGetLockCount_whenFree() {
        Assert.assertEquals(0, lock.getLockCount());
    }

    @Test
    public void testUnlock_whenLockedBySelf() {
        lock.lock();
        lock.unlock();
        Assert.assertFalse(lock.isLocked());
        Assert.assertEquals(0, lock.getLockCount());
        try {
            lock.getFence();
            Assert.fail();
        } catch (IllegalMonitorStateException ignored) {
        }
    }

    @Test
    public void testUnlock_whenReentrantlyLockedBySelf() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        lock.lock();
        lock.unlock();
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertEquals(fence, lock.getFence());
    }

    @Test(timeout = 60000)
    public void testLock_Unlock_thenLock() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        lock.unlock();
        final AtomicReference<Long> newFenceRef = new AtomicReference<Long>();
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                long newFence = lock.lockAndGetFence();
                newFenceRef.set(newFence);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(newFenceRef.get());
            }
        });
        Assert.assertTrue(((newFenceRef.get()) > fence));
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
        Assert.assertFalse(lock.isLockedByCurrentThread());
        try {
            lock.getFence();
            Assert.fail();
        } catch (IllegalMonitorStateException ignored) {
        }
    }

    @Test(timeout = 60000)
    public void testUnlock_whenPendingLockOfOtherThread() {
        long fence = lock.lockAndGetFence();
        final AtomicReference<Long> newFenceRef = new AtomicReference<Long>();
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                long newFence = lock.tryLockAndGetFence(60, TimeUnit.SECONDS);
                newFenceRef.set(newFence);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(instances[0]).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(lock.getGroupId());
                Assert.assertNotNull(registry);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
            }
        });
        lock.unlock();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotNull(newFenceRef.get());
            }
        });
        Assert.assertTrue(((newFenceRef.get()) > fence));
        Assert.assertTrue(lock.isLocked());
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
        try {
            lock.getFence();
            Assert.fail();
        } catch (IllegalMonitorStateException ignored) {
        }
    }

    @Test(timeout = 60000)
    public void testUnlock_whenLockedByOther() {
        FencedLockBasicTest.lockByOtherThread(lock);
        try {
            lock.unlock();
            Assert.fail();
        } catch (IllegalMonitorStateException ignored) {
        }
        Assert.assertTrue(lock.isLocked());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testTryLock_whenNotLocked() {
        long fence = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        Assert.assertEquals(fence, lock.getFence());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testTryLock_whenLockedBySelf() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        long newFence = lock.tryLockAndGetFence();
        Assert.assertEquals(fence, newFence);
        Assert.assertEquals(fence, lock.getFence());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLock_whenLockedByOther() {
        FencedLockBasicTest.lockByOtherThread(lock);
        long fence = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertInvalidFence(fence);
        Assert.assertTrue(lock.isLocked());
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testTryLockTimeout() {
        long fence = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        FencedLockBasicTest.assertValidFence(fence);
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testTryLockTimeout_whenLockedBySelf() {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        long newFence = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        Assert.assertEquals(fence, newFence);
        Assert.assertEquals(fence, lock.getFence());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLockTimeout_whenLockedByOther() {
        FencedLockBasicTest.lockByOtherThread(lock);
        long fence = lock.tryLockAndGetFence(100, TimeUnit.MILLISECONDS);
        FencedLockBasicTest.assertInvalidFence(fence);
        Assert.assertTrue(lock.isLocked());
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test(timeout = 60000)
    public void testTryLockLongTimeout_whenLockedByOther() {
        FencedLockBasicTest.lockByOtherThread(lock);
        long fence = lock.tryLockAndGetFence(((WAIT_TIMEOUT_TASK_UPPER_BOUND_MILLIS) + 1), TimeUnit.MILLISECONDS);
        FencedLockBasicTest.assertInvalidFence(fence);
        Assert.assertTrue(lock.isLocked());
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testReentrantLockFails_whenSessionClosed() throws InterruptedException, ExecutionException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        closeSession(instances[0], groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotEquals(sessionId, sessionManager.getSession(groupId));
            }
        });
        try {
            lock.lock();
        } catch (LockOwnershipLostException ignored) {
        }
    }

    @Test
    public void testReentrantTryLockFails_whenSessionClosed() throws InterruptedException, ExecutionException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        closeSession(instances[0], groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotEquals(sessionId, sessionManager.getSession(groupId));
            }
        });
        try {
            lock.tryLock();
        } catch (LockOwnershipLostException ignored) {
        }
    }

    @Test
    public void testReentrantTryLockWithTimeoutFails_whenSessionClosed() throws InterruptedException, ExecutionException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        closeSession(instances[0], groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotEquals(sessionId, sessionManager.getSession(groupId));
            }
        });
        try {
            lock.tryLock(1, TimeUnit.SECONDS);
        } catch (LockOwnershipLostException ignored) {
        }
    }

    @Test
    public void testUnlockFails_whenSessionClosed() throws InterruptedException, ExecutionException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        closeSession(instances[0], groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotEquals(sessionId, sessionManager.getSession(groupId));
            }
        });
        try {
            lock.unlock();
        } catch (LockOwnershipLostException ignored) {
        }
        Assert.assertFalse(lock.isLockedByCurrentThread());
        Assert.assertFalse(lock.isLocked());
        assertNoLockedSessionId();
    }

    @Test
    public void testUnlockFails_whenNewSessionCreated() throws InterruptedException, ExecutionException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        closeSession(instances[0], groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotEquals(sessionId, sessionManager.getSession(groupId));
            }
        });
        FencedLockBasicTest.lockByOtherThread(lock);
        // now we have a new session
        try {
            lock.unlock();
        } catch (LockOwnershipLostException ignored) {
        }
        Assert.assertFalse(lock.isLockedByCurrentThread());
    }

    @Test
    public void testGetFenceFails_whenNewSessionCreated() throws InterruptedException, ExecutionException {
        long fence = lock.lockAndGetFence();
        FencedLockBasicTest.assertValidFence(fence);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        closeSession(instances[0], groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertNotEquals(sessionId, sessionManager.getSession(groupId));
            }
        });
        FencedLockBasicTest.lockByOtherThread(lock);
        // now we have a new session
        try {
            lock.getFence();
        } catch (LockOwnershipLostException ignored) {
        }
        Assert.assertFalse(lock.isLockedByCurrentThread());
    }

    @Test(timeout = 60000)
    public void testFailedTryLock_doesNotAcquireSession() {
        FencedLockBasicTest.lockByOtherThread(lock);
        final AbstractProxySessionManager sessionManager = getSessionManager(lockInstance);
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        final long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        Assert.assertEquals(1, sessionManager.getSessionAcquireCount(groupId, sessionId));
        long fence = lock.tryLockAndGetFence();
        FencedLockBasicTest.assertInvalidFence(fence);
        Assert.assertEquals(1, sessionManager.getSessionAcquireCount(groupId, sessionId));
    }

    @Test(expected = DistributedObjectDestroyedException.class)
    public void test_destroy() {
        lock.lock();
        lock.destroy();
        assertNoLockedSessionId();
        lock.lock();
    }

    @Test
    public void test_lockInterruptibly() throws InterruptedException {
        HazelcastInstance newInstance = factory.newHazelcastInstance(createConfig(3, 3));
        final FencedLock lock = newInstance.getCPSubsystem().getLock("lock@group1");
        lock.lockInterruptibly();
        lock.unlock();
        for (HazelcastInstance instance : instances) {
            instance.getLifecycleService().terminate();
        }
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> ref = new AtomicReference<Throwable>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.countDown();
                    lock.lockInterruptibly();
                } catch (Throwable t) {
                    ref.set(t);
                }
            }
        });
        thread.start();
        HazelcastTestSupport.assertOpenEventually(latch);
        thread.interrupt();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Throwable t = ref.get();
                Assert.assertTrue((t instanceof InterruptedException));
            }
        });
    }

    @Test
    public void test_lockFailsAfterCPGroupDestroyed() throws InterruptedException, ExecutionException {
        instances[0].getCPSubsystem().getCPSubsystemManagementService().forceDestroyCPGroup(lock.getGroupId().name()).get();
        try {
            lock.lock();
            Assert.fail();
        } catch (CPGroupDestroyedException ignored) {
        }
        lockInstance.getCPSubsystem().getLock(proxyName);
    }
}

