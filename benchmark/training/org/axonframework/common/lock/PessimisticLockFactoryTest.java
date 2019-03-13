/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common.lock;


import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;

import static java.lang.Thread.State.TIMED_WAITING;
import static java.lang.Thread.State.WAITING;


/**
 *
 *
 * @author Allard Buijze
 */
public class PessimisticLockFactoryTest {
    private String identifier = "mockId";

    @Test
    public void testLockReferenceCleanedUpAtUnlock() throws IllegalAccessException, NoSuchFieldException {
        PessimisticLockFactory manager = PessimisticLockFactory.builder().build();
        Lock lock = manager.obtainLock(identifier);
        lock.release();
        Field locksField = manager.getClass().getDeclaredField("locks");
        locksField.setAccessible(true);
        Map locks = ((Map) (locksField.get(manager)));
        Assert.assertEquals("Expected lock to be cleaned up", 0, locks.size());
    }

    @Test
    public void testLockOnlyCleanedUpIfNoLocksAreHeld() throws IllegalAccessException, NoSuchFieldException {
        PessimisticLockFactory manager = PessimisticLockFactory.builder().build();
        Lock lock1 = manager.obtainLock(identifier);
        Lock lock2 = manager.obtainLock(identifier);
        lock1.release();
        Field locksField = manager.getClass().getDeclaredField("locks");
        locksField.setAccessible(true);
        Map locks = ((Map) (locksField.get(manager)));
        Assert.assertEquals("Expected lock not to be cleaned up", 1, locks.size());
        lock2.release();
        locks = ((Map) (locksField.get(manager)));
        Assert.assertEquals("Expected locks to be cleaned up", 0, locks.size());
    }

    @Test(timeout = 8000)
    public void testDeadlockDetected_TwoThreadsInVector() throws InterruptedException {
        final PessimisticLockFactory lock = PessimisticLockFactory.builder().build();
        final CountDownLatch starter = new CountDownLatch(1);
        final CountDownLatch cdl = new CountDownLatch(1);
        final AtomicBoolean deadlockInThread = new AtomicBoolean(false);
        Thread t1 = createThread(starter, cdl, deadlockInThread, lock, "id1", lock, "id2");
        t1.start();
        lock.obtainLock("id2");
        starter.await();
        cdl.countDown();
        try {
            lock.obtainLock("id1");
            Assert.assertTrue(deadlockInThread.get());
        } catch (DeadlockException e) {
            // this is ok!
        }
    }

    @Test(timeout = 7500)
    public void testDeadlockDetected_TwoDifferentLockInstances() throws InterruptedException {
        final PessimisticLockFactory lock1 = PessimisticLockFactory.builder().build();
        final PessimisticLockFactory lock2 = PessimisticLockFactory.builder().build();
        final CountDownLatch starter = new CountDownLatch(1);
        final CountDownLatch cdl = new CountDownLatch(1);
        final AtomicBoolean deadlockInThread = new AtomicBoolean(false);
        Thread t1 = createThread(starter, cdl, deadlockInThread, lock1, "id1", lock2, "id1");
        t1.start();
        lock2.obtainLock("id1");
        starter.await();
        cdl.countDown();
        try {
            lock1.obtainLock("id1");
            Assert.assertTrue(deadlockInThread.get());
        } catch (DeadlockException e) {
            // this is ok!
        }
    }

    @Test(timeout = 8000)
    public void testDeadlockDetected_ThreeThreadsInVector() throws InterruptedException {
        final PessimisticLockFactory lock = PessimisticLockFactory.builder().build();
        final CountDownLatch starter = new CountDownLatch(3);
        final CountDownLatch cdl = new CountDownLatch(1);
        final AtomicBoolean deadlockInThread = new AtomicBoolean(false);
        Thread t1 = createThread(starter, cdl, deadlockInThread, lock, "id1", lock, "id2");
        Thread t2 = createThread(starter, cdl, deadlockInThread, lock, "id2", lock, "id3");
        Thread t3 = createThread(starter, cdl, deadlockInThread, lock, "id3", lock, "id4");
        t1.start();
        t2.start();
        t3.start();
        lock.obtainLock("id4");
        starter.await();
        cdl.countDown();
        try {
            lock.obtainLock("id1");
            Assert.assertTrue(deadlockInThread.get());
        } catch (DeadlockException e) {
            // this is ok!
        }
    }

    @Test(timeout = 5000, expected = LockAcquisitionFailedException.class)
    public void testAquireBackoff() {
        final PessimisticLockFactory lockFactory = PessimisticLockFactory.builder().acquireAttempts(10).queueLengthThreshold(Integer.MAX_VALUE).lockAttemptTimeout(0).build();
        final CountDownLatch rendezvous = new CountDownLatch(1);
        try {
            final AtomicReference<Exception> exceptionInThread = new AtomicReference<>();
            final String id = "aggregateId";
            // Obtain the lock
            createThreadObtainLockAndWaitForState(lockFactory, WAITING, rendezvous, exceptionInThread, id);
            // backoff triggers, too many spins
            lockFactory.obtainLock(id);
        } finally {
            rendezvous.countDown();
        }
    }

    @Test(timeout = 5000, expected = LockAcquisitionFailedException.class)
    public void testQueueBackoff() {
        final PessimisticLockFactory lockFactory = PessimisticLockFactory.builder().acquireAttempts(Integer.MAX_VALUE).queueLengthThreshold(2).lockAttemptTimeout(10000).build();
        final CountDownLatch rendezvous = new CountDownLatch(1);
        try {
            final AtomicReference<Exception> exceptionInThread = new AtomicReference<>();
            final String id = "aggregateId";
            // Obtain the lock
            createThreadObtainLockAndWaitForState(lockFactory, WAITING, rendezvous, exceptionInThread, id);
            // Fill Queue 1/2
            createThreadObtainLockAndWaitForState(lockFactory, TIMED_WAITING, rendezvous, exceptionInThread, id);
            // Fill Queue 2/2
            createThreadObtainLockAndWaitForState(lockFactory, TIMED_WAITING, rendezvous, exceptionInThread, id);
            // backoff triggers, queue
            lockFactory.obtainLock(id);
        } finally {
            rendezvous.countDown();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBackoffParametersConstructorAquireAttempts() {
        int illegalValue = 0;
        PessimisticLockFactory.builder().acquireAttempts(illegalValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBackoffParametersConstructorMaximumQueued() {
        int illegalValue = 0;
        PessimisticLockFactory.builder().queueLengthThreshold(illegalValue);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBackoffParametersConstructorSpinTime() {
        int illegalValue = -1;
        PessimisticLockFactory.builder().lockAttemptTimeout(illegalValue);
    }
}

