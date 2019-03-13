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


import GroupProperty.LOCK_MAX_LEASE_TIME_SECONDS;
import GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS;
import LockService.SERVICE_NAME;
import QuorumType.WRITE;
import com.hazelcast.config.Config;
import com.hazelcast.config.QuorumConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.core.ILock;
import com.hazelcast.instance.TestUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ObjectNamespace;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class LockAdvancedTest extends HazelcastTestSupport {
    @Test(expected = HazelcastInstanceNotActiveException.class)
    public void testShutDownNodeWhenOtherWaitingOnLockLocalKey() throws InterruptedException {
        testShutDownNodeWhenOtherWaitingOnLock(true);
    }

    @Test(expected = HazelcastInstanceNotActiveException.class)
    public void testShutDownNodeWhenOtherWaitingOnLockRemoteKey() throws InterruptedException {
        testShutDownNodeWhenOtherWaitingOnLock(false);
    }

    @Test
    public void testCleanupOperationIgnoresQuorum() {
        Config config = getConfig();
        QuorumConfig quorum = new QuorumConfig("quorum", true, 2).setType(WRITE);
        config.getQuorumConfigs().put("quorum", quorum);
        config.getLockConfig("default").setQuorumName("quorum");
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance[] instances = nodeFactory.newInstances(config);
        String lockName = "lock";
        HazelcastInstance i1 = instances[0];
        HazelcastInstance i2 = instances[1];
        final ILock l1 = i1.getLock(lockName);
        ILock l2 = i2.getLock(lockName);
        l2.lock();
        Assert.assertTrue(l1.isLocked());
        Assert.assertTrue(l2.isLocked());
        i2.shutdown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(l1.isLocked());
            }
        });
    }

    @Test(timeout = 100000)
    public void testLockEvictionLocalKey() throws Exception {
        testLockEviction(true);
    }

    @Test(timeout = 100000)
    public void testLockEvictionRemoteKey() throws Exception {
        testLockEviction(false);
    }

    /**
     * Test for issue #39
     */
    @Test
    public void testIsLocked() throws InterruptedException {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        final HazelcastInstance h1 = nodeFactory.newHazelcastInstance();
        final HazelcastInstance h2 = nodeFactory.newHazelcastInstance();
        final HazelcastInstance h3 = nodeFactory.newHazelcastInstance();
        final String key = "testLockIsLocked";
        final ILock lock = h1.getLock(key);
        final ILock lock2 = h2.getLock(key);
        Assert.assertFalse(lock.isLocked());
        Assert.assertFalse(lock2.isLocked());
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock2.isLocked());
        final CountDownLatch latch = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(1);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                ILock lock3 = h3.getLock(key);
                Assert.assertTrue(lock3.isLocked());
                try {
                    latch2.countDown();
                    while (lock3.isLocked()) {
                        Thread.sleep(100);
                    } 
                    latch.countDown();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
        latch2.await(3, TimeUnit.SECONDS);
        Thread.sleep(500);
        lock.unlock();
        HazelcastTestSupport.assertOpenEventually(latch, 5);
    }

    // todo:   what does isLocked2 test?
    @Test(timeout = 60000)
    public void testIsLocked2() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        final HazelcastInstance instance2 = nodeFactory.newHazelcastInstance();
        final String key = HazelcastTestSupport.randomString();
        final ILock lock = instance1.getLock(key);
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Assert.assertTrue(lock.tryLock());
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
        final AtomicBoolean result = new AtomicBoolean();
        final Thread thread = new Thread() {
            public void run() {
                result.set(lock.isLockedByCurrentThread());
            }
        };
        thread.start();
        thread.join();
        Assert.assertFalse(result.get());
        lock.unlock();
        Assert.assertTrue(lock.isLocked());
        Assert.assertTrue(lock.isLockedByCurrentThread());
    }

    @Test(timeout = 60000)
    public void testLockInterruption() throws InterruptedException {
        Config config = new Config();
        config.setProperty(OPERATION_CALL_TIMEOUT_MILLIS.getName(), String.valueOf(TimeUnit.SECONDS.toMillis(30)));
        final HazelcastInstance hz = createHazelcastInstance(config);
        final Lock lock = hz.getLock("testLockInterruption2");
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    lock.tryLock(60, TimeUnit.SECONDS);
                } catch (InterruptedException ignored) {
                    latch.countDown();
                }
            }
        });
        lock.lock();
        t.start();
        Thread.sleep(2000);
        t.interrupt();
        HazelcastTestSupport.assertOpenEventually("tryLock() is not interrupted!", latch);
        lock.unlock();
        Assert.assertTrue("Could not acquire lock!", lock.tryLock());
    }

    // ====================== tests to make sure the lock can deal with cluster member failure ====================
    @Test(timeout = 100000)
    public void testLockOwnerDies() throws Exception {
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance lockOwner = nodeFactory.newHazelcastInstance();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        final String name = HazelcastTestSupport.randomString();
        final ILock lock = lockOwner.getLock(name);
        lock.lock();
        Assert.assertTrue(lock.isLocked());
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                final ILock lock = instance1.getLock(name);
                lock.lock();
                latch.countDown();
            }
        });
        t.start();
        lockOwner.shutdown();
        HazelcastTestSupport.assertOpenEventually(latch, 10);
    }

    @Test(timeout = 100000)
    public void testLockOwnerDies_withMultipleLocks() throws Exception {
        int lockCount = 10;
        TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        HazelcastInstance lockOwner = nodeFactory.newHazelcastInstance();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        final String[] names = generateKeysBelongingToSamePartitionsOwnedBy(instance1, lockCount);
        final ILock[] locks = getLocks(lockOwner, names);
        lockAll(locks);
        assertAllLocked(locks);
        final CountDownLatch latch = new CountDownLatch(1);
        Thread t = new Thread(new Runnable() {
            public void run() {
                final ILock[] locks = getLocks(instance1, names);
                lockAll(locks);
                latch.countDown();
            }
        });
        t.start();
        lockOwner.shutdown();
        HazelcastTestSupport.assertOpenEventually(latch, 10);
    }

    @Test(timeout = 100000)
    public void testKeyOwnerDies() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(3);
        final HazelcastInstance keyOwner = nodeFactory.newHazelcastInstance();
        final HazelcastInstance instance1 = nodeFactory.newHazelcastInstance();
        final HazelcastInstance instance2 = nodeFactory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(keyOwner, instance1, instance2);
        final String key = HazelcastTestSupport.generateKeyOwnedBy(keyOwner);
        final ILock lock1 = instance1.getLock(key);
        lock1.lock();
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            public void run() {
                final ILock lock = instance2.getLock(key);
                lock.lock();
                latch.countDown();
            }
        }).start();
        Thread.sleep(1000);
        keyOwner.shutdown();
        Assert.assertTrue(lock1.isLocked());
        Assert.assertTrue(lock1.isLockedByCurrentThread());
        Assert.assertTrue(lock1.tryLock());
        lock1.unlock();
        lock1.unlock();
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test(timeout = 100000)
    public void testScheduledLockActionForDeadMember() throws Exception {
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(2);
        final HazelcastInstance h1 = nodeFactory.newHazelcastInstance();
        final ILock lock1 = h1.getLock("default");
        final HazelcastInstance h2 = nodeFactory.newHazelcastInstance();
        final ILock lock2 = h2.getLock("default");
        Assert.assertTrue(lock1.tryLock());
        final AtomicBoolean error = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            public void run() {
                try {
                    lock2.lock();
                    error.set(true);
                } catch (Throwable ignored) {
                }
            }
        });
        thread.start();
        Thread.sleep(5000);
        Assert.assertTrue(lock1.isLocked());
        h2.shutdown();
        thread.join(10000);
        Assert.assertFalse(thread.isAlive());
        Assert.assertFalse(error.get());
        Assert.assertTrue(lock1.isLocked());
        lock1.unlock();
        Assert.assertFalse(lock1.isLocked());
        Assert.assertTrue(lock1.tryLock());
    }

    @Test
    public void testLockInterruptibly() throws Exception {
        Config config = new Config();
        final TestHazelcastInstanceFactory nodeFactory = createHazelcastInstanceFactory(1);
        final HazelcastInstance h1 = nodeFactory.newHazelcastInstance(config);
        final ILock lock = h1.getLock(HazelcastTestSupport.randomString());
        final CountDownLatch latch = new CountDownLatch(1);
        lock.lock();
        Thread t = new Thread() {
            public void run() {
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e) {
                    latch.countDown();
                }
            }
        };
        t.start();
        HazelcastTestSupport.sleepMillis(5000);
        t.interrupt();
        HazelcastTestSupport.assertOpenEventually(latch);
    }

    @Test
    public void testLockLeaseTime_whenKeyOwnerMemberDies() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz1 = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        HazelcastTestSupport.warmUpPartitions(hz1, hz2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(hz1);
        final ILock lock = hz2.getLock(key);
        lock.lock(3, TimeUnit.SECONDS);
        TestUtil.terminateInstance(hz1);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse("Lock should be released after lease expires!", lock.isLocked());
            }
        }, 30);
    }

    @Test
    public void testMaxLockLeaseTime() {
        Config config = new Config();
        config.setProperty(LOCK_MAX_LEASE_TIME_SECONDS.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        final ILock lock = hz.getLock(HazelcastTestSupport.randomName());
        lock.lock();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse("Lock should be released after lease expires!", lock.isLocked());
            }
        }, 30);
    }

    @Test
    public void testLockLease_withStringPartitionAwareName() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance hz = factory.newHazelcastInstance();
        final ILock lock = hz.getLock(((HazelcastTestSupport.randomName()) + "@hazelcast"));
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock(5, TimeUnit.SECONDS);
            }
        }).get();
        Assert.assertTrue("Lock should have been released after lease expires", lock.tryLock(2, TimeUnit.MINUTES));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLockFail_whenGreaterThanMaxLeaseTimeUsed() {
        Config config = new Config();
        config.setProperty(LOCK_MAX_LEASE_TIME_SECONDS.getName(), "1");
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(1);
        HazelcastInstance hz = factory.newHazelcastInstance(config);
        ILock lock = hz.getLock(HazelcastTestSupport.randomName());
        lock.lock(10, TimeUnit.SECONDS);
    }

    @Test
    public void testLockCleanup_whenInvokingMemberDies() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        HazelcastInstance hz = factory.newHazelcastInstance();
        HazelcastInstance hz2 = factory.newHazelcastInstance();
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(hz2);
        InternalOperationService operationService = HazelcastTestSupport.getOperationService(hz2);
        HazelcastTestSupport.warmUpPartitions(hz2);
        String name = HazelcastTestSupport.randomNameOwnedBy(hz);
        Data key = nodeEngine.toData(name);
        int partitionId = nodeEngine.getPartitionService().getPartitionId(key);
        operationService.invokeOnPartition(SERVICE_NAME, new LockAdvancedTest.SlowLockOperation(name, key, 2000), partitionId);
        HazelcastTestSupport.sleepMillis(500);
        TestUtil.terminateInstance(hz2);
        final ILock lock = hz.getLock(name);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                Assert.assertFalse("Lock owned by dead member should have been released!", lock.isLocked());
            }
        }, 30);
    }

    private static class SlowLockOperation extends Operation {
        Data key;

        ObjectNamespace ns;

        long sleepMillis;

        public SlowLockOperation() {
        }

        public SlowLockOperation(String name, Data key, long sleepMillis) {
            this.key = key;
            this.ns = new InternalLockNamespace(name);
            this.sleepMillis = sleepMillis;
        }

        protected final LockStoreImpl getLockStore() {
            LockServiceImpl service = getService();
            return service.getLockStore(HazelcastTestSupport.getPartitionId(), ns);
        }

        @Override
        public void run() throws Exception {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(1000));
            getLockStore().lock(key, getCallerUuid(), 1, 1, (-1));
        }

        @Override
        protected void writeInternal(ObjectDataOutput out) throws IOException {
            super.writeInternal(out);
            out.writeLong(sleepMillis);
            out.writeByteArray(key.toByteArray());
            out.writeUTF(ns.getObjectName());
        }

        @Override
        protected void readInternal(ObjectDataInput in) throws IOException {
            super.readInternal(in);
            sleepMillis = in.readLong();
            key = new com.hazelcast.internal.serialization.impl.HeapData(in.readByteArray());
            ns = new InternalLockNamespace(in.readUTF());
        }
    }
}

