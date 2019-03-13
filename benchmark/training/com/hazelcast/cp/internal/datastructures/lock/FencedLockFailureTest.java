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
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.exception.WaitKeyCancelledException;
import com.hazelcast.cp.internal.datastructures.lock.proxy.RaftFencedLockProxy;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKeyContainer;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.PartitionSpecificRunnable;
import com.hazelcast.spi.impl.operationservice.impl.OperationServiceImpl;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.UuidUtil;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static RaftLockService.SERVICE_NAME;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FencedLockFailureTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private HazelcastInstance lockInstance;

    private RaftFencedLockProxy lock;

    private String objectName = "lock";

    private String proxyName = (objectName) + "@group1";

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProxyOnMetadataCPGroup() {
        lockInstance.getCPSubsystem().getLock(((objectName) + "@metadata"));
    }

    @Test
    public void testRetriedLockDoesNotCancelPendingLockRequest() {
        lockByOtherThread();
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid));
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        }, 10);
    }

    @Test(timeout = 30000)
    public void testNewLockCancelsPendingLockRequest() {
        lockByOtherThread();
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid2));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test
    public void testRetriedTryLockWithTimeoutDoesNotCancelPendingLockRequest() {
        lockByOtherThread();
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        final NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(lockInstance);
        final RaftLockService service = nodeEngine.getService(SERVICE_NAME);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertNotNull(registry.getResourceOrNull(objectName));
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final int partitionId = nodeEngine.getPartitionService().getPartitionId(groupId);
                final RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                final boolean[] verified = new boolean[1];
                final CountDownLatch latch = new CountDownLatch(1);
                OperationServiceImpl operationService = ((OperationServiceImpl) (nodeEngine.getOperationService()));
                operationService.execute(new PartitionSpecificRunnable() {
                    @Override
                    public int getPartitionId() {
                        return partitionId;
                    }

                    @Override
                    public void run() {
                        RaftLock raftLock = registry.getResourceOrNull(objectName);
                        final Map<Object, WaitKeyContainer<LockInvocationKey>> waitKeys = raftLock.getInternalWaitKeysMap();
                        verified[0] = ((waitKeys.size()) == 1) && ((waitKeys.values().iterator().next().retryCount()) == 1);
                        latch.countDown();
                    }
                });
                latch.await(60, TimeUnit.SECONDS);
                Assert.assertTrue(verified[0]);
            }
        });
    }

    @Test(timeout = 30000)
    public void testNewTryLockWithTimeoutCancelsPendingLockRequest() {
        lockByOtherThread();
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid2, TimeUnit.MINUTES.toMillis(5)));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test
    public void testRetriedTryLockWithoutTimeoutDoesNotCancelPendingLockRequest() {
        lockByOtherThread();
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 0));
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        }, 10);
    }

    @Test(timeout = 30000)
    public void testNewUnlockCancelsPendingLockRequest() {
        lockByOtherThread();
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        try {
            lock.unlock();
            Assert.fail();
        } catch (IllegalMonitorStateException ignored) {
        }
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test
    public void testLockAcquireRetry() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid)).join();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid)).join();
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testLockReentrantAcquireRetry() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid1)).join();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid2)).join();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid2)).join();
        Assert.assertEquals(2, lock.getLockCount());
    }

    @Test
    public void testPendingLockAcquireRetry() {
        final CountDownLatch unlockLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                HazelcastTestSupport.assertOpenEventually(unlockLatch);
                lock.unlock();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(lock.isLocked());
            }
        });
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        InternalCompletableFuture<Long> f1 = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                Assert.assertFalse(service.getRegistryOrNull(groupId).getWaitTimeouts().isEmpty());
            }
        });
        unlockLatch.countDown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                Assert.assertTrue(service.getRegistryOrNull(groupId).getWaitTimeouts().isEmpty());
                Assert.assertTrue(lock.isLocked());
            }
        });
        InternalCompletableFuture<Long> f2 = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.MINUTES.toMillis(5)));
        long fence1 = f1.join();
        long fence2 = f2.join();
        Assert.assertEquals(fence1, lock.getFence());
        Assert.assertEquals(fence1, fence2);
        Assert.assertEquals(1, lock.getLockCount());
    }

    @Test
    public void testRetriedUnlockIsSuccessfulAfterLockedByAnotherEndpoint() {
        lock.lock();
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.UnlockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid)).join();
        lockByOtherThread();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.UnlockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid)).join();
    }

    @Test
    public void testIsLockedByCurrentThreadCallInitializesLockedSessionId() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        long threadId = ThreadUtil.getThreadId();
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, threadId, UuidUtil.newUnsecureUUID())).join();
        // the current thread acquired the lock once and we pretend that there was a operation timeout in lock.lock() call
        Assert.assertTrue(lock.isLockedByCurrentThread());
        Long lockedSessionId = lock.getLockedSessionId(threadId);
        Assert.assertNotNull(lockedSessionId);
        Assert.assertEquals(sessionId, ((long) (lockedSessionId)));
    }

    @Test
    public void testLockCallInitializesLockedSessionId() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        long threadId = ThreadUtil.getThreadId();
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, threadId, UuidUtil.newUnsecureUUID())).join();
        lock.lock();
        Long lockedSessionId = lock.getLockedSessionId(threadId);
        Assert.assertNotNull(lockedSessionId);
        Assert.assertEquals(sessionId, ((long) (lockedSessionId)));
    }

    @Test
    public void testUnlockCallInitializesLockedSessionId() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        long threadId = ThreadUtil.getThreadId();
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, threadId, UuidUtil.newUnsecureUUID())).join();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, threadId, UuidUtil.newUnsecureUUID())).join();
        lock.unlock();
        Long lockedSessionId = lock.getLockedSessionId(threadId);
        Assert.assertNotNull(lockedSessionId);
        Assert.assertEquals(sessionId, ((long) (lockedSessionId)));
    }

    @Test
    public void testIsLockedCallInitializesLockedSessionId() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        long threadId = ThreadUtil.getThreadId();
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, threadId, UuidUtil.newUnsecureUUID())).join();
        boolean locked = lock.isLocked();
        Assert.assertTrue(locked);
        Long lockedSessionId = lock.getLockedSessionId(threadId);
        Assert.assertNotNull(lockedSessionId);
        Assert.assertEquals(sessionId, ((long) (lockedSessionId)));
    }

    @Test
    public void testGetLockCountCallInitializesLockedSessionId() {
        lock.lock();
        lock.unlock();
        // there is a session id now
        long threadId = ThreadUtil.getThreadId();
        RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.LockOp(objectName, sessionId, threadId, UuidUtil.newUnsecureUUID())).join();
        int getLockCount = lock.getLockCount();
        Assert.assertEquals(1, getLockCount);
        Long lockedSessionId = lock.getLockedSessionId(threadId);
        Assert.assertNotNull(lockedSessionId);
        Assert.assertEquals(sessionId, ((long) (lockedSessionId)));
    }

    @Test
    public void testRetriedWaitKeysAreExpiredTogether() {
        final CountDownLatch releaseLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                HazelcastTestSupport.assertOpenEventually(releaseLatch);
                lock.unlock();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(lock.isLocked());
            }
        });
        // there is a session id now
        final RaftGroupId groupId = lock.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(lockInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        final Tuple2[] lockWaitTimeoutKeyRef = new Tuple2[1];
        InternalCompletableFuture<Long> f1 = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.SECONDS.toMillis(300)));
        final NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(lockInstance);
        final RaftLockService service = nodeEngine.getService(SERVICE_NAME);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Map<Tuple2<String, UUID>, Tuple2<Long, Long>> waitTimeouts = registry.getWaitTimeouts();
                Assert.assertEquals(1, waitTimeouts.size());
                lockWaitTimeoutKeyRef[0] = waitTimeouts.keySet().iterator().next();
            }
        });
        InternalCompletableFuture<Long> f2 = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.TryLockOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, TimeUnit.SECONDS.toMillis(300)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final int partitionId = nodeEngine.getPartitionService().getPartitionId(groupId);
                final RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                final boolean[] verified = new boolean[1];
                final CountDownLatch latch = new CountDownLatch(1);
                OperationServiceImpl operationService = ((OperationServiceImpl) (nodeEngine.getOperationService()));
                operationService.execute(new PartitionSpecificRunnable() {
                    @Override
                    public int getPartitionId() {
                        return partitionId;
                    }

                    @Override
                    public void run() {
                        RaftLock raftLock = registry.getResourceOrNull(objectName);
                        final Map<Object, WaitKeyContainer<LockInvocationKey>> waitKeys = raftLock.getInternalWaitKeysMap();
                        verified[0] = ((waitKeys.size()) == 1) && ((waitKeys.values().iterator().next().retryCount()) == 1);
                        latch.countDown();
                    }
                });
                HazelcastTestSupport.assertOpenEventually(latch);
                Assert.assertTrue(verified[0]);
            }
        });
        RaftOp op = new com.hazelcast.cp.internal.datastructures.spi.blocking.operation.ExpireWaitKeysOp(SERVICE_NAME, Collections.<Tuple2<String, UUID>>singletonList(lockWaitTimeoutKeyRef[0]));
        invocationManager.invoke(groupId, op).join();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(service.getRegistryOrNull(groupId).getWaitTimeouts().isEmpty());
            }
        });
        releaseLatch.countDown();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(lock.isLocked());
            }
        });
        long fence1 = f1.join();
        long fence2 = f2.join();
        FencedLockBasicTest.assertInvalidFence(fence1);
        FencedLockBasicTest.assertInvalidFence(fence2);
    }
}

