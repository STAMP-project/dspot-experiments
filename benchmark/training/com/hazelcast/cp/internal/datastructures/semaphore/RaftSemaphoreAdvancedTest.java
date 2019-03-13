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
package com.hazelcast.cp.internal.datastructures.semaphore;


import RaftSemaphoreService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ISemaphore;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.datastructures.semaphore.proxy.RaftSessionAwareSemaphoreProxy;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.cp.internal.datastructures.spi.blocking.WaitKeyContainer;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raftop.snapshot.RestoreSnapshotOp;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;
import com.hazelcast.cp.internal.session.ProxySessionManagerService;
import com.hazelcast.cp.internal.session.RaftSessionService;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import static RaftSemaphoreService.SERVICE_NAME;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftSemaphoreAdvancedTest extends HazelcastRaftTestSupport {
    private static final int LOG_ENTRY_COUNT_TO_SNAPSHOT = 10;

    private HazelcastInstance[] instances;

    private HazelcastInstance semaphoreInstance;

    private RaftSessionAwareSemaphoreProxy semaphore;

    private String objectName = "semaphore";

    private String proxyName = (objectName) + "@group1";

    private int groupSize = 3;

    @Test
    public void testSuccessfulAcquireClearsWaitTimeouts() {
        semaphore.init(1);
        CPGroupId groupId = semaphore.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.acquire(2);
                latch.countDown();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        semaphore.increasePermits(1);
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testSuccessfulTryAcquireClearsWaitTimeouts() {
        semaphore.init(1);
        CPGroupId groupId = semaphore.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.tryAcquire(2, 10, TimeUnit.MINUTES);
                latch.countDown();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        semaphore.increasePermits(1);
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testFailedTryAcquireClearsWaitTimeouts() {
        semaphore.init(1);
        CPGroupId groupId = semaphore.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
        boolean success = semaphore.tryAcquire(2, 1, TimeUnit.SECONDS);
        Assert.assertFalse(success);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testPermitIncreaseClearsWaitTimeouts() {
        semaphore.init(1);
        CPGroupId groupId = semaphore.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.tryAcquire(2, 10, TimeUnit.MINUTES);
                latch.countDown();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        semaphore.increasePermits(1);
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testDestroyClearsWaitTimeouts() {
        semaphore.init(1);
        CPGroupId groupId = semaphore.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.tryAcquire(2, 10, TimeUnit.MINUTES);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        semaphore.destroy();
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testNewRaftGroupMemberSchedulesTimeoutsWithSnapshot() throws InterruptedException, ExecutionException {
        semaphore.init(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.tryAcquire(2, 10, TimeUnit.MINUTES);
            }
        });
        for (int i = 0; i < (RaftSemaphoreAdvancedTest.LOG_ENTRY_COUNT_TO_SNAPSHOT); i++) {
            semaphore.acquire();
            semaphore.release();
        }
        final CPGroupId groupId = semaphore.getGroupId();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastInstance leader = getLeaderInstance(instances, groupId);
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftNodeImpl raftNode = HazelcastRaftTestSupport.getRaftNode(instance, groupId);
                    Assert.assertNotNull(raftNode);
                    LogEntry snapshotEntry = RaftUtil.getSnapshotEntry(raftNode);
                    Assert.assertTrue(((snapshotEntry.index()) > 0));
                    List<RestoreSnapshotOp> ops = ((List<RestoreSnapshotOp>) (snapshotEntry.operation()));
                    for (RestoreSnapshotOp op : ops) {
                        if (op.getServiceName().equals(SERVICE_NAME)) {
                            ResourceRegistry registry = ((ResourceRegistry) (op.getSnapshot()));
                            Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                            return;
                        }
                    }
                    Assert.fail();
                }
            }
        });
        instances[1].shutdown();
        final HazelcastInstance newInstance = factory.newHazelcastInstance(createConfig(groupSize, groupSize));
        newInstance.getCPSubsystem().getCPSubsystemManagementService().promoteToCPMember().get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(newInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertEquals(1, registry.availablePermits(objectName));
            }
        });
    }

    @Test
    public void testInactiveSessionsAreEventuallyClosed() throws InterruptedException, ExecutionException {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = semaphore.getGroupId();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(groupId.name()).get().isEmpty());
                }
            }
        });
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance);
        final ProxySessionManagerService service = nodeEngine.getService(ProxySessionManagerService.SERVICE_NAME);
        long sessionId = service.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftOp op = new com.hazelcast.cp.internal.datastructures.semaphore.operation.ReleasePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), UuidUtil.newUnsecureUUID(), 1);
        getRaftInvocationManager(semaphoreInstance).invoke(groupId, op).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertTrue(service.getAllSessions(groupId.name()).get().isEmpty());
                }
                Assert.assertEquals(AbstractProxySessionManager.NO_SESSION_ID, service.getSession(groupId));
            }
        });
    }

    @Test
    public void testActiveSessionIsNotClosed() {
        semaphore.init(1);
        semaphore.acquire();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(semaphore.getGroupId().name()).get().isEmpty());
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(semaphore.getGroupId().name()).get().isEmpty());
                }
            }
        }, 20);
    }

    @Test
    public void testActiveSessionWithPendingPermitIsNotClosed() {
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.acquire();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(semaphore.getGroupId().name()).get().isEmpty());
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(semaphore.getGroupId().name()).get().isEmpty());
                }
            }
        }, 20);
    }

    @Test
    public void testRetriedReleaseIsSuccessfulAfterAcquiredByAnotherEndpoint() {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = semaphore.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.ReleasePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 1)).join();
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.acquire();
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.ReleasePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 1)).join();
    }

    @Test
    public void testRetriedIncreasePermitsAppliedOnlyOnce() {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // we guarantee that there is a session id now...
        final RaftGroupId groupId = semaphore.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 1)).join();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 1)).join();
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    @Test
    public void testRetriedDecreasePermitsAppliedOnlyOnce() {
        semaphore.init(2);
        semaphore.acquire();
        semaphore.release();
        // we guarantee that there is a session id now...
        final RaftGroupId groupId = semaphore.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, (-1))).join();
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.ChangePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, (-1))).join();
        Assert.assertEquals(1, semaphore.availablePermits());
    }

    @Test
    public void testRetriedDrainPermitsAppliedOnlyOnce() throws InterruptedException, ExecutionException {
        semaphore.increasePermits(3);
        final RaftGroupId groupId = semaphore.getGroupId();
        long sessionId = getSessionManager().getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        int drained1 = invocationManager.<Integer>invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.DrainPermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid)).join();
        Assert.assertEquals(3, drained1);
        Assert.assertEquals(0, semaphore.availablePermits());
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.increasePermits(1);
            }
        }).get();
        int drained2 = invocationManager.<Integer>invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.DrainPermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid)).join();
        Assert.assertEquals(3, drained2);
        Assert.assertEquals(1, semaphore.availablePermits());
    }

    @Test
    public void testRetriedWaitKeysAreExpiredTogether() {
        semaphore.init(1);
        final CountDownLatch releaseLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.acquire();
                HazelcastTestSupport.assertOpenEventually(releaseLatch);
                semaphore.release();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, semaphore.availablePermits());
            }
        });
        // there is a session id now
        final RaftGroupId groupId = semaphore.getGroupId();
        final NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance);
        final RaftSemaphoreService service = nodeEngine.getService(SERVICE_NAME);
        ProxySessionManagerService sessionManager = nodeEngine.getService(ProxySessionManagerService.SERVICE_NAME);
        long sessionId = sessionManager.getSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        UUID invUid = UuidUtil.newUnsecureUUID();
        final Tuple2[] acquireWaitTimeoutKeyRef = new Tuple2[1];
        InternalCompletableFuture<Boolean> f1 = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.AcquirePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 1, TimeUnit.SECONDS.toMillis(300)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Map<Tuple2<String, UUID>, Tuple2<Long, Long>> waitTimeouts = registry.getWaitTimeouts();
                Assert.assertEquals(1, waitTimeouts.size());
                acquireWaitTimeoutKeyRef[0] = waitTimeouts.keySet().iterator().next();
            }
        });
        InternalCompletableFuture<Boolean> f2 = invocationManager.invoke(groupId, new com.hazelcast.cp.internal.datastructures.semaphore.operation.AcquirePermitsOp(objectName, sessionId, ThreadUtil.getThreadId(), invUid, 1, TimeUnit.SECONDS.toMillis(300)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                final int partitionId = nodeEngine.getPartitionService().getPartitionId(groupId);
                final RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
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
                        RaftSemaphore raftSemaphore = registry.getResourceOrNull(objectName);
                        final Map<Object, WaitKeyContainer<AcquireInvocationKey>> waitKeys = raftSemaphore.getInternalWaitKeysMap();
                        verified[0] = ((waitKeys.size()) == 1) && ((waitKeys.values().iterator().next().retryCount()) == 1);
                        latch.countDown();
                    }
                });
                HazelcastTestSupport.assertOpenEventually(latch);
                Assert.assertTrue(verified[0]);
            }
        });
        RaftOp op = new com.hazelcast.cp.internal.datastructures.spi.blocking.operation.ExpireWaitKeysOp(SERVICE_NAME, Collections.<Tuple2<String, UUID>>singletonList(acquireWaitTimeoutKeyRef[0]));
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
                Assert.assertEquals(1, semaphore.availablePermits());
            }
        });
        Assert.assertFalse(f1.join());
        Assert.assertFalse(f2.join());
    }

    @Test
    public void testPermitAcquired_whenPermitOwnerShutsDown() {
        semaphore.init(1);
        semaphore.acquire();
        final CountDownLatch acquiredLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance otherInstance = ((instances[0]) == (semaphoreInstance)) ? instances[1] : instances[0];
                ISemaphore remoteSemaphore = otherInstance.getCPSubsystem().getSemaphore(proxyName);
                try {
                    remoteSemaphore.acquire();
                    acquiredLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(semaphore.getGroupId());
                Assert.assertNotNull(registry);
                RaftSemaphore semaphore = registry.getResourceOrNull(objectName);
                Assert.assertNotNull(semaphore);
                Assert.assertFalse(semaphore.getInternalWaitKeysMap().isEmpty());
            }
        });
        semaphoreInstance.shutdown();
        HazelcastTestSupport.assertOpenEventually(acquiredLatch);
    }
}

