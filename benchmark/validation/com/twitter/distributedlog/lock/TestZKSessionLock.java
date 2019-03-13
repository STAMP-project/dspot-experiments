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


import FailpointUtils.AbstractFailPointAction;
import FailpointUtils.DEFAULT_ACTION;
import FailpointUtils.FailPointName.FP_LockTryAcquire;
import FailpointUtils.FailPointName.FP_LockTryCloseRaceCondition;
import FailpointUtils.FailPointName.FP_LockUnlockCleanup;
import KeeperException.Code.NONODE;
import State.CLAIMED;
import State.CLOSED;
import State.CLOSING;
import State.EXPIRED;
import State.WAITING;
import com.twitter.distributedlog.ZooKeeperClient;
import com.twitter.distributedlog.ZooKeeperClientUtils;
import com.twitter.distributedlog.ZooKeeperClusterTestCase;
import com.twitter.distributedlog.exceptions.LockingException;
import com.twitter.distributedlog.exceptions.OwnershipAcquireFailedException;
import com.twitter.distributedlog.util.FailpointUtils;
import com.twitter.distributedlog.util.OrderedScheduler;
import com.twitter.util.Await;
import com.twitter.util.Promise;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.bookkeeper.stats.NullStatsLogger;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.runtime.BoxedUnit;


/**
 * Distributed Lock Tests
 */
public class TestZKSessionLock extends ZooKeeperClusterTestCase {
    @Rule
    public TestName testNames = new TestName();

    static final Logger logger = LoggerFactory.getLogger(TestZKSessionLock.class);

    private static final int sessionTimeoutMs = 2000;

    private ZooKeeperClient zkc;

    private ZooKeeperClient zkc0;// used for checking


    private OrderedScheduler lockStateExecutor;

    @Test(timeout = 60000)
    public void testParseClientID() throws Exception {
        ZooKeeper zk = zkc.get();
        String lockPath = "/test-parse-clientid";
        String clientId = "test-parse-clientid-" + (System.currentTimeMillis());
        Pair<String, Long> lockId = Pair.of(clientId, zk.getSessionId());
        TestZKSessionLock.createLockPath(zk, lockPath);
        // Correct data
        String node1 = getLockIdFromPath(TestZKSessionLock.createLockNodeV1(zk, lockPath, clientId));
        String node2 = getLockIdFromPath(TestZKSessionLock.createLockNodeV2(zk, lockPath, clientId));
        String node3 = getLockIdFromPath(TestZKSessionLock.createLockNodeV3(zk, lockPath, clientId));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node1)));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node2)));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node3)));
        // Bad Lock Node Name
        String node4 = getLockIdFromPath(TestZKSessionLock.createLockNodeWithBadNodeName(zk, lockPath, clientId, "member"));
        String node5 = getLockIdFromPath(TestZKSessionLock.createLockNodeWithBadNodeName(zk, lockPath, clientId, "member_badnode"));
        String node6 = getLockIdFromPath(TestZKSessionLock.createLockNodeWithBadNodeName(zk, lockPath, clientId, "member_badnode_badnode"));
        String node7 = getLockIdFromPath(TestZKSessionLock.createLockNodeWithBadNodeName(zk, lockPath, clientId, "member_badnode_badnode_badnode"));
        String node8 = getLockIdFromPath(TestZKSessionLock.createLockNodeWithBadNodeName(zk, lockPath, clientId, "member_badnode_badnode_badnode_badnode"));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node4)));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node5)));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node6)));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node7)));
        Assert.assertEquals(lockId, Await.result(asyncParseClientID(zk, lockPath, node8)));
        // Malformed Node Name
        String node9 = getLockIdFromPath(TestZKSessionLock.createLockNodeWithBadNodeName(zk, lockPath, clientId, "member_malformed_s12345678_999999"));
        Assert.assertEquals(Pair.of("malformed", 12345678L), Await.result(asyncParseClientID(zk, lockPath, node9)));
    }

    @Test(timeout = 60000)
    public void testParseMemberID() throws Exception {
        Assert.assertEquals(Integer.MAX_VALUE, parseMemberID("badnode"));
        Assert.assertEquals(Integer.MAX_VALUE, parseMemberID("badnode_badnode"));
        Assert.assertEquals(0, parseMemberID("member_000000"));
        Assert.assertEquals(123, parseMemberID("member_000123"));
    }

    @Test(timeout = 60000)
    public void testExecuteLockAction() throws Exception {
        String lockPath = "/test-execute-lock-action";
        String clientId = "test-execute-lock-action-" + (System.currentTimeMillis());
        ZKSessionLock.ZKSessionLock lock = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor);
        final AtomicInteger counter = new AtomicInteger(0);
        // lock action would be executed in same epoch
        final CountDownLatch latch1 = new CountDownLatch(1);
        lock.executeLockAction(lock.getEpoch().get(), new LockAction() {
            @Override
            public void execute() {
                counter.incrementAndGet();
                latch1.countDown();
            }

            @Override
            public String getActionName() {
                return "increment1";
            }
        });
        latch1.await();
        Assert.assertEquals("counter should be increased in same epoch", 1, counter.get());
        // lock action would not be executed in same epoch
        final CountDownLatch latch2 = new CountDownLatch(1);
        lock.executeLockAction(((lock.getEpoch().get()) + 1), new LockAction() {
            @Override
            public void execute() {
                counter.incrementAndGet();
            }

            @Override
            public String getActionName() {
                return "increment2";
            }
        });
        lock.executeLockAction(lock.getEpoch().get(), new LockAction() {
            @Override
            public void execute() {
                latch2.countDown();
            }

            @Override
            public String getActionName() {
                return "countdown";
            }
        });
        latch2.await();
        Assert.assertEquals("counter should not be increased in different epochs", 1, counter.get());
        // lock action would not be executed in same epoch and promise would be satisfied with exception
        Promise<BoxedUnit> promise = new Promise<BoxedUnit>();
        lock.executeLockAction(((lock.getEpoch().get()) + 1), new LockAction() {
            @Override
            public void execute() {
                counter.incrementAndGet();
            }

            @Override
            public String getActionName() {
                return "increment3";
            }
        }, promise);
        try {
            Await.result(promise);
            Assert.fail("Should satisfy promise with epoch changed exception.");
        } catch (EpochChangedException ece) {
            // expected
        }
        Assert.assertEquals("counter should not be increased in different epochs", 1, counter.get());
        lockStateExecutor.shutdown();
    }

    /**
     * Test lock after unlock is called.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockAfterUnlock() throws Exception {
        String lockPath = "/test-lock-after-unlock";
        String clientId = "test-lock-after-unlock";
        ZKSessionLock.ZKSessionLock lock = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor);
        lock.unlock();
        Assert.assertEquals(CLOSED, lock.getLockState());
        try {
            lock.tryLock(0, TimeUnit.MILLISECONDS);
            Assert.fail("Should fail on tryLock since lock state has changed.");
        } catch (LockStateChangedException lsce) {
            // expected
        }
        Assert.assertEquals(CLOSED, lock.getLockState());
        try {
            lock.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            Assert.fail("Should fail on tryLock immediately if lock state has changed.");
        } catch (LockStateChangedException lsce) {
            // expected
        }
        Assert.assertEquals(CLOSED, lock.getLockState());
    }

    class DelayFailpointAction extends FailpointUtils.AbstractFailPointAction {
        long timeout;

        DelayFailpointAction(long timeout) {
            this.timeout = timeout;
        }

        @Override
        public boolean checkFailPoint() throws IOException {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ie) {
            }
            return true;
        }
    }

    /**
     * Test unlock timeout.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testUnlockTimeout() throws Exception {
        String name = testNames.getMethodName();
        String lockPath = "/" + name;
        String clientId = name;
        TestZKSessionLock.createLockPath(zkc.get(), lockPath);
        ZKSessionLock.ZKSessionLock lock = /* op timeout */
        new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor, (1 * 1000), NullStatsLogger.INSTANCE, new DistributedLockContext());
        lock.tryLock(0, TimeUnit.MILLISECONDS);
        Assert.assertEquals(CLAIMED, lock.getLockState());
        try {
            FailpointUtils.setFailpoint(FP_LockUnlockCleanup, new TestZKSessionLock.DelayFailpointAction(((60 * 60) * 1000)));
            lock.unlock();
            Assert.assertEquals(CLOSING, lock.getLockState());
        } finally {
            FailpointUtils.removeFailpoint(FP_LockUnlockCleanup);
        }
    }

    /**
     * Test try-create after close race condition.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testTryCloseRaceCondition() throws Exception {
        String name = testNames.getMethodName();
        String lockPath = "/" + name;
        String clientId = name;
        TestZKSessionLock.createLockPath(zkc.get(), lockPath);
        ZKSessionLock.ZKSessionLock lock = /* op timeout */
        new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor, (1 * 1000), NullStatsLogger.INSTANCE, new DistributedLockContext());
        try {
            FailpointUtils.setFailpoint(FP_LockTryCloseRaceCondition, DEFAULT_ACTION);
            lock.tryLock(0, TimeUnit.MILLISECONDS);
        } catch (LockClosedException ex) {
        } finally {
            FailpointUtils.removeFailpoint(FP_LockTryCloseRaceCondition);
        }
        Assert.assertEquals(CLOSED, lock.getLockState());
        List<String> children = TestZKSessionLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(0, children.size());
    }

    /**
     * Test try acquire timeout.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testTryAcquireTimeout() throws Exception {
        String name = testNames.getMethodName();
        String lockPath = "/" + name;
        String clientId = name;
        TestZKSessionLock.createLockPath(zkc.get(), lockPath);
        ZKSessionLock.ZKSessionLock lock = /* op timeout */
        new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor, 1, NullStatsLogger.INSTANCE, new DistributedLockContext());
        try {
            FailpointUtils.setFailpoint(FP_LockTryAcquire, new TestZKSessionLock.DelayFailpointAction(((60 * 60) * 1000)));
            lock.tryLock(0, TimeUnit.MILLISECONDS);
            Assert.assertEquals(CLOSED, lock.getLockState());
        } catch (LockingException le) {
        } catch (Exception e) {
            Assert.fail("expected locking exception");
        } finally {
            FailpointUtils.removeFailpoint(FP_LockTryAcquire);
        }
    }

    @Test(timeout = 60000)
    public void testBasicLockUnlock0() throws Exception {
        testBasicLockUnlock(0);
    }

    @Test(timeout = 60000)
    public void testBasicLockUnlock1() throws Exception {
        testBasicLockUnlock(Long.MAX_VALUE);
    }

    /**
     * Test lock on non existed lock.
     *
     * - lock should fail on a non existed lock.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockOnNonExistedLock() throws Exception {
        String lockPath = "/test-lock-on-non-existed-lock";
        String clientId = "test-lock-on-non-existed-lock";
        ZKSessionLock.ZKSessionLock lock = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor);
        // lock
        try {
            lock.tryLock(0, TimeUnit.MILLISECONDS);
            Assert.fail("Should fail on locking a non-existed lock.");
        } catch (LockingException le) {
            Throwable cause = le.getCause();
            Assert.assertTrue((cause instanceof KeeperException));
            Assert.assertEquals(NONODE, code());
        }
        Assert.assertEquals(CLOSED, lock.getLockState());
        // lock should failed on a failure lock
        try {
            lock.tryLock(0, TimeUnit.MILLISECONDS);
            Assert.fail("Should fail on locking a failure lock.");
        } catch (LockStateChangedException lsce) {
            // expected
        }
        Assert.assertEquals(CLOSED, lock.getLockState());
    }

    @Test(timeout = 60000)
    public void testLockWhenSomeoneHeldLock0() throws Exception {
        testLockWhenSomeoneHeldLock(0);
    }

    @Test(timeout = 60000)
    public void testLockWhenSomeoneHeldLock1() throws Exception {
        testLockWhenSomeoneHeldLock(500);
    }

    @Test(timeout = 60000)
    public void testLockWhenPreviousLockZnodeStillExists() throws Exception {
        String lockPath = "/test-lock-when-previous-lock-znode-still-exists-" + (System.currentTimeMillis());
        String clientId = "client-id";
        ZooKeeper zk = zkc.get();
        TestZKSessionLock.createLockPath(zk, lockPath);
        final ZKSessionLock.ZKSessionLock lock0 = new ZKSessionLock.ZKSessionLock(zkc0, lockPath, clientId, lockStateExecutor);
        // lock0 lock
        lock0.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        // simulate lock0 expires but znode still exists
        final DistributedLockContext context1 = new DistributedLockContext();
        context1.addLockId(lock0.getLockId());
        final ZKSessionLock.ZKSessionLock lock1 = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor, 60000, NullStatsLogger.INSTANCE, context1);
        lock1.tryLock(0L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(CLAIMED, lock1.getLockState());
        lock1.unlock();
        final DistributedLockContext context2 = new DistributedLockContext();
        context2.addLockId(lock0.getLockId());
        final ZKSessionLock.ZKSessionLock lock2 = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor, 60000, NullStatsLogger.INSTANCE, context2);
        lock2.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        Assert.assertEquals(CLAIMED, lock2.getLockState());
        lock2.unlock();
        lock0.unlock();
    }

    @Test(timeout = 60000)
    public void testWaitForLockUnlock() throws Exception {
        testWaitForLockReleased("/test-wait-for-lock-unlock", true);
    }

    @Test(timeout = 60000)
    public void testWaitForLockExpired() throws Exception {
        testWaitForLockReleased("/test-wait-for-lock-expired", false);
    }

    /**
     * Test session expired after claimed the lock: lock state should be changed to expired and notify
     * the lock listener about expiry.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockListenerOnExpired() throws Exception {
        String lockPath = "/test-lock-listener-on-expired";
        String clientId = "test-lock-listener-on-expired-" + (System.currentTimeMillis());
        TestZKSessionLock.createLockPath(zkc.get(), lockPath);
        final CountDownLatch expiredLatch = new CountDownLatch(1);
        LockListener listener = new LockListener() {
            @Override
            public void onExpired() {
                expiredLatch.countDown();
            }
        };
        final ZKSessionLock.ZKSessionLock lock = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId, lockStateExecutor).setLockListener(listener);
        lock.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        // verification after lock
        Assert.assertEquals(CLAIMED, lock.getLockState());
        List<String> children = TestZKSessionLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertEquals(lock.getLockId(), Await.result(asyncParseClientID(zkc.get(), lockPath, children.get(0))));
        ZooKeeperClientUtils.expireSession(zkc, ZooKeeperClusterTestCase.zkServers, TestZKSessionLock.sessionTimeoutMs);
        expiredLatch.await();
        Assert.assertEquals(EXPIRED, lock.getLockState());
        children = TestZKSessionLock.getLockWaiters(zkc, lockPath);
        Assert.assertEquals(0, children.size());
        try {
            lock.tryLock(0, TimeUnit.MILLISECONDS);
            Assert.fail("Should fail on tryLock since lock state has changed.");
        } catch (LockStateChangedException lsce) {
            // expected
        }
        lock.unlock();
    }

    @Test(timeout = 60000)
    public void testSessionExpiredBeforeLock0() throws Exception {
        testSessionExpiredBeforeLock(0);
    }

    @Test(timeout = 60000)
    public void testSessionExpiredBeforeLock1() throws Exception {
        testSessionExpiredBeforeLock(Long.MAX_VALUE);
    }

    @Test(timeout = 60000)
    public void testSessionExpiredForLockWaiter() throws Exception {
        String lockPath = "/test-session-expired-for-lock-waiter";
        String clientId0 = "test-session-expired-for-lock-waiter-0";
        String clientId1 = "test-session-expired-for-lock-waiter-1";
        TestZKSessionLock.createLockPath(zkc.get(), lockPath);
        final ZKSessionLock.ZKSessionLock lock0 = new ZKSessionLock.ZKSessionLock(zkc0, lockPath, clientId0, lockStateExecutor);
        lock0.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        Assert.assertEquals(CLAIMED, lock0.getLockState());
        List<String> children = TestZKSessionLock.getLockWaiters(zkc0, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertEquals(lock0.getLockId(), Await.result(asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
        final ZKSessionLock.ZKSessionLock lock1 = new ZKSessionLock.ZKSessionLock(zkc, lockPath, clientId1, lockStateExecutor);
        final CountDownLatch lock1DoneLatch = new CountDownLatch(1);
        Thread lock1Thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    lock1.tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
                } catch (OwnershipAcquireFailedException oafe) {
                    lock1DoneLatch.countDown();
                } catch (LockingException e) {
                    TestZKSessionLock.logger.error("Failed on locking lock1 : ", e);
                }
            }
        }, "lock1-thread");
        lock1Thread.start();
        // check lock1 is waiting for lock0
        children = awaitWaiters(2, zkc, lockPath);
        Assert.assertEquals(2, children.size());
        Assert.assertEquals(CLAIMED, lock0.getLockState());
        Assert.assertEquals(lock0.getLockId(), Await.result(asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
        awaitState(WAITING, lock1);
        Assert.assertEquals(lock1.getLockId(), Await.result(asyncParseClientID(zkc.get(), lockPath, children.get(1))));
        // expire lock1
        ZooKeeperClientUtils.expireSession(zkc, ZooKeeperClusterTestCase.zkServers, TestZKSessionLock.sessionTimeoutMs);
        lock1DoneLatch.countDown();
        lock1Thread.join();
        Assert.assertEquals(CLAIMED, lock0.getLockState());
        Assert.assertEquals(CLOSED, lock1.getLockState());
        children = TestZKSessionLock.getLockWaiters(zkc0, lockPath);
        Assert.assertEquals(1, children.size());
        Assert.assertEquals(lock0.getLockId(), Await.result(asyncParseClientID(zkc0.get(), lockPath, children.get(0))));
    }

    @Test(timeout = 60000)
    public void testLockUseSameClientIdButDifferentSessions0() throws Exception {
        testLockUseSameClientIdButDifferentSessions(true);
    }

    @Test(timeout = 60000)
    public void testLockUseSameClientIdButDifferentSessions1() throws Exception {
        testLockUseSameClientIdButDifferentSessions(false);
    }

    /**
     * Immediate lock and unlock first lock
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockWhenSiblingUseDifferentLockId0() throws Exception {
        testLockWhenSiblingUseDifferentLockId(0, true);
    }

    /**
     * Immediate lock and expire first lock
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockWhenSiblingUseDifferentLockId1() throws Exception {
        testLockWhenSiblingUseDifferentLockId(0, false);
    }

    /**
     * Wait Lock and unlock lock0_0 and lock1
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockWhenSiblingUseDifferentLockId2() throws Exception {
        testLockWhenSiblingUseDifferentLockId(Long.MAX_VALUE, true);
    }

    /**
     * Wait Lock and expire first & third lock
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 60000)
    public void testLockWhenSiblingUseDifferentLockId3() throws Exception {
        testLockWhenSiblingUseDifferentLockId(Long.MAX_VALUE, false);
    }

    @Test(timeout = 60000)
    public void testLockWhenSiblingUseSameLockId0() throws Exception {
        testLockWhenSiblingUseSameLockId(0, true);
    }

    @Test(timeout = 60000)
    public void testLockWhenSiblingUseSameLockId1() throws Exception {
        testLockWhenSiblingUseSameLockId(0, false);
    }

    @Test(timeout = 60000)
    public void testLockWhenSiblingUseSameLockId2() throws Exception {
        testLockWhenSiblingUseSameLockId(Long.MAX_VALUE, true);
    }

    @Test(timeout = 60000)
    public void testLockWhenSiblingUseSameLockId3() throws Exception {
        testLockWhenSiblingUseSameLockId(Long.MAX_VALUE, false);
    }
}

