/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.distributedlog.lock;


import CreateMode.PERSISTENT;
import FailpointUtils.AbstractFailPointAction;
import FailpointUtils.FailPointName.FP_ZooKeeperConnectionLoss;
import ZooDefs.Ids.OPEN_ACL_UNSAFE;
import com.twitter.distributedlog.TestDistributedLogBase;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClientUtils;
import com.twitter.distributedlog.exceptions.LockingException;
import com.twitter.distributedlog.exceptions.OwnershipAcquireFailedException;
import com.twitter.distributedlog.exceptions.UnexpectedException;
import com.twitter.distributedlog.util.FailpointUtils;
import com.twitter.distributedlog.util.FutureUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import com.twitter.distributedlog.util.Utils;
import com.twitter.util.Await;
import com.twitter.util.Future;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Distributed Lock Tests
 */
public class TestDistributedLock extends TestDistributedLogBase {
    static final Logger logger = LoggerFactory.getLogger(TestDistributedLock.class);

    @Rule
    public TestName runtime = new TestName();

    private static final int sessionTimeoutMs = 2000;

    private ZooKeeperClient zkc;

    private ZooKeeperClient zkc0;// used for checking


    private OrderedScheduler lockStateExecutor;

    static class TestLockFactory {
        final String lockPath;

        final String clientId;

        final OrderedScheduler lockStateExecutor;

        public TestLockFactory(String name, ZooKeeperClient defaultZkc, OrderedScheduler lockStateExecutor) throws Exception {
            this.lockPath = ("/" + name) + (System.currentTimeMillis());
            this.clientId = name;
            TestDistributedLock.createLockPath(defaultZkc.get(), lockPath);
            this.lockStateExecutor = lockStateExecutor;
        }

        public ZKDistributedLock createLock(int id, ZooKeeperClient zkc) throws Exception {
            SessionLockFactory lockFactory = new ZKSessionLockFactory(zkc, ((clientId) + id), lockStateExecutor, 0, Long.MAX_VALUE, TestDistributedLock.sessionTimeoutMs, NullStatsLogger.INSTANCE);
            return new ZKDistributedLock(this.lockStateExecutor, lockFactory, this.lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
        }

        public String getLockPath() {
            return lockPath;
        }
    }

    static class CountDownThrowFailPointAction extends FailpointUtils.AbstractFailPointAction {
        final AtomicInteger successCounter;

        final AtomicInteger failureCounter;

        CountDownThrowFailPointAction(int successCount, int failureCount) {
            this.successCounter = new AtomicInteger(successCount);
            this.failureCounter = new AtomicInteger(failureCount);
        }

        @Override
        public boolean checkFailPoint() throws IOException {
            int successCount = successCounter.getAndDecrement();
            if (successCount > 0) {
                return true;
            }
            int count = failureCounter.getAndDecrement();
            if (count > 0) {
                throw new IOException(("counter = " + count));
            }
            return true;
        }
    }

    @Test(timeout = 60000)
    public void testZooKeeperConnectionLossOnLockCreation() throws Exception {
        String lockPath = "/test-zookeeper-connection-loss-on-lock-creation-" + (System.currentTimeMillis());
        String clientId = "zookeeper-connection-loss";
        TestDistributedLock.createLockPath(zkc.get(), lockPath);
        FailpointUtils.setFailpoint(FP_ZooKeeperConnectionLoss, new TestDistributedLock.CountDownThrowFailPointAction(0, Integer.MAX_VALUE));
        SessionLockFactory lockFactory = createLockFactory(clientId, zkc, Long.MAX_VALUE, 0);
        try {
            try {
                ZKDistributedLock lock = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
                FutureUtils.result(lock.asyncAcquire());
                Assert.fail("Should fail on creating lock if couldn't establishing connections to zookeeper");
            } catch (IOException ioe) {
                // expected.
            }
        } finally {
            FailpointUtils.removeFailpoint(FP_ZooKeeperConnectionLoss);
        }
        FailpointUtils.setFailpoint(FP_ZooKeeperConnectionLoss, new TestDistributedLock.CountDownThrowFailPointAction(0, Integer.MAX_VALUE));
        lockFactory = createLockFactory(clientId, zkc, Long.MAX_VALUE, 3);
        try {
            try {
                ZKDistributedLock lock = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
                FutureUtils.result(lock.asyncAcquire());
                Assert.fail("Should fail on creating lock if couldn't establishing connections to zookeeper after 3 retries");
            } catch (IOException ioe) {
                // expected.
            }
        } finally {
            FailpointUtils.removeFailpoint(FP_ZooKeeperConnectionLoss);
        }
        FailpointUtils.setFailpoint(FP_ZooKeeperConnectionLoss, new TestDistributedLock.CountDownThrowFailPointAction(0, 3));
        lockFactory = createLockFactory(clientId, zkc, Long.MAX_VALUE, 5);
        try {
            ZKDistributedLock lock = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
            FutureUtils.result(lock.asyncAcquire());
            Pair<String, Long> lockId1 = getLockId();
            List<String> children = TestDistributedLock.getLockWaiters(zkc, lockPath);
            Assert.assertEquals(1, children.size());
            Assert.assertTrue(lock.haveLock());
            Assert.assertEquals(lockId1, Await.result(ZKSessionLock.asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
            lock.asyncClose();
        } finally {
            FailpointUtils.removeFailpoint(FP_ZooKeeperConnectionLoss);
        }
    }

    @Test(timeout = 60000)
    public void testBasicAcquireRelease() throws Exception {
        String lockPath = "/test-basic-acquire-release-" + (System.currentTimeMillis());
        String clientId = "basic-acquire-release";
        TestDistributedLock.createLockPath(zkc.get(), lockPath);
        SessionLockFactory lockFactory = createLockFactory(clientId, zkc);
        ZKDistributedLock lock = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
        FutureUtils.result(lock.asyncAcquire());
        Pair<String, Long> lockId1 = getLockId();
        List<String> children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(lock.haveLock());
        Assert.assertEquals(lockId1, Await.result(ZKSessionLock.asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
        FutureUtils.result(lock.asyncClose());
        children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(0, children.size());
        Assert.assertFalse(lock.haveLock());
        lock = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
        FutureUtils.result(lock.asyncAcquire());
        Pair<String, Long> lockId2 = getLockId();
        children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(lock.haveLock());
        Assert.assertEquals(lockId2, Await.result(ZKSessionLock.asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
        Assert.assertEquals(lockId1, lockId2);
        FutureUtils.result(lock.asyncClose());
        children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(0, children.size());
        Assert.assertFalse(lock.haveLock());
        try {
            FutureUtils.result(lock.asyncAcquire());
            Assert.fail("Should fail on acquiring a closed lock");
        } catch (UnexpectedException le) {
            // expected.
        }
        children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(0, children.size());
        Assert.assertFalse(lock.haveLock());
    }

    @Test(timeout = 60000)
    public void testCheckWriteLockFailureWhenLockIsAcquiredByOthers() throws Exception {
        String lockPath = "/test-check-write-lock-failure-when-lock-is-acquired-by-others-" + (System.currentTimeMillis());
        String clientId = "test-check-write-lock-failure";
        TestDistributedLock.createLockPath(zkc.get(), lockPath);
        SessionLockFactory lockFactory0 = createLockFactory(clientId, zkc0);
        ZKDistributedLock lock0 = new ZKDistributedLock(lockStateExecutor, lockFactory0, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
        FutureUtils.result(lock0.asyncAcquire());
        Pair<String, Long> lockId0_1 = getLockId();
        List<String> children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(lock0.haveLock());
        Assert.assertEquals(lockId0_1, Await.result(ZKSessionLock.asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
        // expire the session
        ZooKeeperClientUtils.expireSession(zkc0, TestDistributedLogBase.zkServers, TestDistributedLock.sessionTimeoutMs);
        // reacquire the lock and wait reacquire completed
        TestDistributedLock.checkLockAndReacquire(lock0, true);
        Pair<String, Long> lockId0_2 = getLockId();
        Assert.assertFalse("New lock should be created under different session", lockId0_1.equals(lockId0_2));
        children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(lock0.haveLock());
        Assert.assertEquals(lockId0_2, Await.result(ZKSessionLock.asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
        SessionLockFactory lockFactory = createLockFactory(clientId, zkc);
        final ZKDistributedLock lock1 = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, Long.MAX_VALUE, NullStatsLogger.INSTANCE);
        final CountDownLatch lockLatch = new CountDownLatch(1);
        Thread lockThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FutureUtils.result(lock1.asyncAcquire());
                    lockLatch.countDown();
                } catch (IOException e) {
                    TestDistributedLock.logger.error("Failed on locking lock1 : ", e);
                }
            }
        }, "lock-thread");
        lockThread.start();
        // ensure lock1 is waiting for lock0
        do {
            Thread.sleep(1);
            children = TestDistributedLock.getLockWaiters(zkc, lockPath);
        } while ((children.size()) < 2 );
        // expire the session
        ZooKeeperClientUtils.expireSession(zkc0, TestDistributedLogBase.zkServers, TestDistributedLock.sessionTimeoutMs);
        lockLatch.await();
        lockThread.join();
        try {
            TestDistributedLock.checkLockAndReacquire(lock0, true);
            Assert.fail("Should fail on checking write lock since lock is acquired by lock1");
        } catch (LockingException le) {
            // expected
        }
        try {
            TestDistributedLock.checkLockAndReacquire(lock0, false);
            Assert.fail("Should fail on checking write lock since lock is acquired by lock1");
        } catch (LockingException le) {
            // expected
        }
    }

    /**
     * If no lock is acquired between session expired and re-acquisition, check write lock will acquire the lock.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockReacquireSuccessAfterCheckWriteLock() throws Exception {
        testLockReacquireSuccess(true);
    }

    /**
     * If no lock is acquired between session expired and re-acquisition, check write lock will acquire the lock.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockReacquireSuccessWithoutCheckWriteLock() throws Exception {
        testLockReacquireSuccess(false);
    }

    /**
     * If lock is acquired between session expired and re-acquisition, check write lock will be failed.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockReacquireFailureAfterCheckWriteLock() throws Exception {
        testLockReacquireFailure(true);
    }

    /**
     * If lock is acquired between session expired and re-acquisition, check write lock will be failed.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockReacquireFailureWithoutCheckWriteLock() throws Exception {
        testLockReacquireFailure(false);
    }

    @Test(timeout = 60000)
    public void testLockReacquire() throws Exception {
        String lockPath = "/reacquirePath";
        Utils.zkCreateFullPathOptimistic(zkc, lockPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        String clientId = "lockHolder";
        SessionLockFactory lockFactory = createLockFactory(clientId, zkc, TestDistributedLogBase.conf.getLockTimeoutMilliSeconds(), 0);
        ZKDistributedLock lock = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, TestDistributedLogBase.conf.getLockTimeoutMilliSeconds(), NullStatsLogger.INSTANCE);
        FutureUtils.result(lock.asyncAcquire());
        // try and cleanup the underlying lock
        lock.getInternalLock().unlock();
        // This should reacquire the lock
        TestDistributedLock.checkLockAndReacquire(lock, true);
        Assert.assertEquals(true, lock.haveLock());
        Assert.assertEquals(true, lock.getInternalLock().isLockHeld());
        lockFactory = createLockFactory((clientId + "_2"), zkc, TestDistributedLogBase.conf.getLockTimeoutMilliSeconds(), 0);
        ZKDistributedLock lock2 = new ZKDistributedLock(lockStateExecutor, lockFactory, lockPath, 0, NullStatsLogger.INSTANCE);
        boolean exceptionEncountered = false;
        try {
            FutureUtils.result(lock2.asyncAcquire());
        } catch (OwnershipAcquireFailedException exc) {
            Assert.assertEquals(clientId, exc.getCurrentOwner());
            exceptionEncountered = true;
        }
        Assert.assertTrue(exceptionEncountered);
        FutureUtils.result(lock.asyncClose());
        FutureUtils.result(lock2.asyncClose());
    }

    @Test(timeout = 60000)
    public void testLockReacquireMultiple() throws Exception {
        String lockPath = "/reacquirePathMultiple";
        Utils.zkCreateFullPathOptimistic(zkc, lockPath, new byte[0], OPEN_ACL_UNSAFE, PERSISTENT);
        String clientId = "lockHolder";
        SessionLockFactory factory = createLockFactory(clientId, zkc, TestDistributedLogBase.conf.getLockTimeoutMilliSeconds(), 0);
        ZKDistributedLock lock = new ZKDistributedLock(lockStateExecutor, factory, lockPath, TestDistributedLogBase.conf.getLockTimeoutMilliSeconds(), NullStatsLogger.INSTANCE);
        FutureUtils.result(lock.asyncAcquire());
        // try and cleanup the underlying lock
        lock.getInternalLock().unlock();
        // This should reacquire the lock
        TestDistributedLock.checkLockAndReacquire(lock, true);
        Assert.assertEquals(true, lock.haveLock());
        Assert.assertEquals(true, lock.getInternalLock().isLockHeld());
        factory = createLockFactory((clientId + "_2"), zkc, 0, 0);
        ZKDistributedLock lock2 = new ZKDistributedLock(lockStateExecutor, factory, lockPath, 0, NullStatsLogger.INSTANCE);
        boolean exceptionEncountered = false;
        try {
            FutureUtils.result(lock2.asyncAcquire());
        } catch (OwnershipAcquireFailedException exc) {
            Assert.assertEquals(clientId, exc.getCurrentOwner());
            exceptionEncountered = true;
        }
        Assert.assertTrue(exceptionEncountered);
        FutureUtils.result(lock2.asyncClose());
        FutureUtils.result(lock.asyncClose());
        Assert.assertEquals(false, lock.haveLock());
        Assert.assertEquals(false, lock.getInternalLock().isLockHeld());
        factory = createLockFactory((clientId + "_3"), zkc, 0, 0);
        ZKDistributedLock lock3 = new ZKDistributedLock(lockStateExecutor, factory, lockPath, 0, NullStatsLogger.INSTANCE);
        FutureUtils.result(lock3.asyncAcquire());
        Assert.assertEquals(true, lock3.haveLock());
        Assert.assertEquals(true, lock3.getInternalLock().isLockHeld());
        FutureUtils.result(lock3.asyncClose());
    }

    @Test(timeout = 60000)
    public void testAsyncAcquireBasics() throws Exception {
        TestDistributedLock.TestLockFactory locks = new TestDistributedLock.TestLockFactory(runtime.getMethodName(), zkc, lockStateExecutor);
        int count = 3;
        ArrayList<Future<ZKDistributedLock>> results = new ArrayList<Future<ZKDistributedLock>>(count);
        ZKDistributedLock[] lockArray = new ZKDistributedLock[count];
        final CountDownLatch[] latches = new CountDownLatch[count];
        // Set up <count> waiters, save async results, count down a latch when lock is acquired in
        // the future.
        for (int i = 0; i < count; i++) {
            latches[i] = new CountDownLatch(1);
            lockArray[i] = locks.createLock(i, zkc);
            final int index = i;
            results.add(lockArray[i].asyncAcquire().addEventListener(new com.twitter.util.FutureEventListener<ZKDistributedLock>() {
                @Override
                public void onSuccess(ZKDistributedLock lock) {
                    latches[index].countDown();
                }

                @Override
                public void onFailure(Throwable cause) {
                    Assert.fail(("unexpected failure " + cause));
                }
            }));
        }
        // Now await ownership and release ownership of locks one by one (in the order they were
        // acquired).
        for (int i = 0; i < count; i++) {
            latches[i].await();
            assertLatchesSet(latches, (i + 1));
            Await.result(results.get(i));
            FutureUtils.result(lockArray[i].asyncClose());
        }
    }

    @Test(timeout = 60000)
    public void testAsyncAcquireSyncThenAsyncOnSameLock() throws Exception {
        TestDistributedLock.TestLockFactory locks = new TestDistributedLock.TestLockFactory(runtime.getMethodName(), zkc, lockStateExecutor);
        final ZKDistributedLock lock0 = locks.createLock(0, zkc);
        final ZKDistributedLock lock1 = locks.createLock(1, zkc0);
        FutureUtils.result(lock0.asyncAcquire());
        // Initial state.
        assertLockState(lock0, true, true, lock1, false, false, 1, locks.getLockPath());
        Thread lock1Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FutureUtils.result(lock1.asyncAcquire());
                } catch (IOException e) {
                    Assert.fail("shouldn't fail to acquire");
                }
            }
        }, "lock1-thread");
        lock1Thread.start();
        // Wait for lock count to increase, indicating background acquire has succeeded.
        while ((TestDistributedLock.getLockWaiters(zkc, locks.getLockPath()).size()) < 2) {
            Thread.sleep(1);
        } 
        assertLockState(lock0, true, true, lock1, false, false, 2, locks.getLockPath());
        FutureUtils.result(lock0.asyncClose());
        Await.result(lock1.getLockAcquireFuture());
        assertLockState(lock0, false, false, lock1, true, true, 1, locks.getLockPath());
        // Release lock1
        FutureUtils.result(lock1.asyncClose());
        assertLockState(lock0, false, false, lock1, false, false, 0, locks.getLockPath());
    }

    @Test(timeout = 60000)
    public void testAsyncAcquireExpireDuringWait() throws Exception {
        TestDistributedLock.TestLockFactory locks = new TestDistributedLock.TestLockFactory(runtime.getMethodName(), zkc, lockStateExecutor);
        final ZKDistributedLock lock0 = locks.createLock(0, zkc);
        final ZKDistributedLock lock1 = locks.createLock(1, zkc0);
        FutureUtils.result(lock0.asyncAcquire());
        Future<ZKDistributedLock> result = lock1.asyncAcquire();
        // make sure we place a waiter for lock1
        while (null == (lock1.getLockWaiter())) {
            TimeUnit.MILLISECONDS.sleep(20);
        } 
        // Expire causes acquire future to be failed and unset.
        ZooKeeperClientUtils.expireSession(zkc0, TestDistributedLogBase.zkServers, TestDistributedLock.sessionTimeoutMs);
        try {
            Await.result(result);
            Assert.fail("future should have been failed");
        } catch (OwnershipAcquireFailedException ex) {
        }
        assertLockState(lock0, true, true, lock1, false, false, 1, locks.getLockPath());
        lock0.asyncClose();
        lock1.asyncClose();
    }

    @Test(timeout = 60000)
    public void testAsyncAcquireCloseDuringWait() throws Exception {
        TestDistributedLock.TestLockFactory locks = new TestDistributedLock.TestLockFactory(runtime.getMethodName(), zkc, lockStateExecutor);
        final ZKDistributedLock lock0 = locks.createLock(0, zkc);
        final ZKDistributedLock lock1 = locks.createLock(1, zkc0);
        FutureUtils.result(lock0.asyncAcquire());
        Future<ZKDistributedLock> result = lock1.asyncAcquire();
        FutureUtils.result(lock1.asyncClose());
        try {
            Await.result(result);
            Assert.fail("future should have been failed");
        } catch (LockClosedException ex) {
        }
        assertLockState(lock0, true, true, lock1, false, false, 1, locks.getLockPath());
        lock0.asyncClose();
    }

    @Test(timeout = 60000)
    public void testAsyncAcquireCloseAfterAcquire() throws Exception {
        TestDistributedLock.TestLockFactory locks = new TestDistributedLock.TestLockFactory(runtime.getMethodName(), zkc, lockStateExecutor);
        final ZKDistributedLock lock0 = locks.createLock(0, zkc);
        Future<ZKDistributedLock> result = lock0.asyncAcquire();
        Await.result(result);
        FutureUtils.result(lock0.asyncClose());
        // Already have this, stays satisfied.
        Await.result(result);
        // But we no longer have the lock.
        Assert.assertEquals(false, lock0.haveLock());
        Assert.assertEquals(false, lock0.getInternalLock().isLockHeld());
    }
}

