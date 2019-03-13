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


import FencedLock.INVALID_FENCE;
import RaftLockService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raftop.snapshot.RestoreSnapshotOp;
import com.hazelcast.cp.internal.session.AbstractProxySessionManager;
import com.hazelcast.cp.internal.session.ProxySessionManagerService;
import com.hazelcast.cp.internal.session.RaftSessionService;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.ThreadUtil;
import com.hazelcast.util.UuidUtil;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FencedLockAdvancedTest extends HazelcastRaftTestSupport {
    private static final int LOG_ENTRY_COUNT_TO_SNAPSHOT = 10;

    private HazelcastInstance[] instances;

    private HazelcastInstance lockInstance;

    private FencedLock lock;

    private String objectName = "lock";

    private String proxyName = (objectName) + "@group1";

    private int groupSize = 3;

    @Test
    public void testSuccessfulLockClearsWaitTimeouts() {
        lock.lock();
        CPGroupId groupId = lock.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftLockRegistry registry = service.getRegistryOrNull(groupId);
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                latch.countDown();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        lock.unlock();
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testSuccessfulTryLockClearsWaitTimeouts() {
        lock.lock();
        CPGroupId groupId = lock.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftLockRegistry registry = service.getRegistryOrNull(groupId);
        final CountDownLatch latch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.tryLock(10, TimeUnit.MINUTES);
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
        lock.unlock();
        HazelcastTestSupport.assertOpenEventually(latch);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testFailedTryLockClearsWaitTimeouts() {
        FencedLockBasicTest.lockByOtherThread(lock);
        CPGroupId groupId = lock.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        RaftLockRegistry registry = service.getRegistryOrNull(groupId);
        long fence = lock.tryLockAndGetFence(1, TimeUnit.SECONDS);
        Assert.assertEquals(INVALID_FENCE, fence);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testDestroyClearsWaitTimeouts() {
        FencedLockBasicTest.lockByOtherThread(lock);
        CPGroupId groupId = lock.getGroupId();
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftLockRegistry registry = service.getRegistryOrNull(groupId);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.tryLock(10, TimeUnit.MINUTES);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        lock.destroy();
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testNewRaftGroupMemberSchedulesTimeoutsWithSnapshot() throws InterruptedException, ExecutionException {
        final long fence = this.lock.lockAndGetFence();
        Assert.assertTrue((fence > 0));
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.tryLock(10, TimeUnit.MINUTES);
            }
        });
        final CPGroupId groupId = this.lock.getGroupId();
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < (FencedLockAdvancedTest.LOG_ENTRY_COUNT_TO_SNAPSHOT); i++) {
                    lock.isLocked();
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastInstance leader = getLeaderInstance(instances, groupId);
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
                ResourceRegistry registry = service.getRegistryOrNull(groupId);
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
        HazelcastInstance instanceToShutdown = ((instances[0]) == (lockInstance)) ? instances[1] : instances[0];
        instanceToShutdown.shutdown();
        final HazelcastInstance newInstance = factory.newHazelcastInstance(createConfig(groupSize, groupSize));
        newInstance.getCPSubsystem().getCPSubsystemManagementService().promoteToCPMember().get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftNodeImpl raftNode = HazelcastRaftTestSupport.getRaftNode(newInstance, groupId);
                Assert.assertNotNull(raftNode);
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(raftNode).index()) > 0));
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(newInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                RaftLockOwnershipState ownership = registry.getLockOwnershipState(objectName);
                Assert.assertTrue(ownership.isLocked());
                Assert.assertTrue(((ownership.getLockCount()) > 0));
                Assert.assertEquals(fence, ownership.getFence());
            }
        });
    }

    @Test
    public void testInactiveSessionsAreEventuallyClosed() throws InterruptedException, ExecutionException {
        lock.lock();
        final RaftGroupId groupId = ((RaftGroupId) (lock.getGroupId()));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(groupId.name()).get().isEmpty());
                }
            }
        });
        RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(RaftSessionService.SERVICE_NAME);
        long sessionId = sessionService.getAllSessions(groupId.name()).get().iterator().next().id();
        getRaftInvocationManager(lockInstance).invoke(groupId, new com.hazelcast.cp.internal.datastructures.lock.operation.UnlockOp(objectName, sessionId, ThreadUtil.getThreadId(), UuidUtil.newUnsecureUUID())).join();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertTrue(service.getAllSessions(groupId.name()).get().isEmpty());
                }
                ProxySessionManagerService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(ProxySessionManagerService.SERVICE_NAME);
                Assert.assertEquals(AbstractProxySessionManager.NO_SESSION_ID, service.getSession(groupId));
            }
        });
    }

    @Test
    public void testActiveSessionIsNotClosedWhenLockIsHeld() {
        lock.lock();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(lock.getGroupId().name()).get().isEmpty());
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertFalse(sessionService.getAllSessions(lock.getGroupId().name()).get().isEmpty());
                }
            }
        }, 20);
    }

    @Test
    public void testActiveSessionIsNotClosedWhenPendingWaitKey() {
        FencedLock other = null;
        for (HazelcastInstance instance : instances) {
            if (instance != (lockInstance)) {
                other = instance.getCPSubsystem().getLock(proxyName);
                break;
            }
        }
        Assert.assertNotNull(other);
        // lock from another instance
        other.lock();
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.tryLock(30, TimeUnit.MINUTES);
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertEquals(2, sessionService.getAllSessions(lock.getGroupId().name()).get().size());
                }
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(instance).getService(RaftSessionService.SERVICE_NAME);
                    Assert.assertEquals(2, sessionService.getAllSessions(lock.getGroupId().name()).get().size());
                }
            }
        }, 20);
    }

    @Test
    public void testLockAcquired_whenLockOwnerShutsDown() {
        lock.lock();
        final CountDownLatch remoteLockedLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                HazelcastInstance otherInstance = ((instances[0]) == (lockInstance)) ? instances[1] : instances[0];
                FencedLock remoteLock = otherInstance.getCPSubsystem().getLock(proxyName);
                remoteLock.lock();
                remoteLockedLatch.countDown();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftLockService service = HazelcastTestSupport.getNodeEngineImpl(lockInstance).getService(SERVICE_NAME);
                RaftLockRegistry registry = service.getRegistryOrNull(lock.getGroupId());
                Assert.assertNotNull(registry);
                RaftLock raftLock = registry.getResourceOrNull(objectName);
                Assert.assertNotNull(raftLock);
                Assert.assertFalse(raftLock.getInternalWaitKeysMap().isEmpty());
            }
        });
        lockInstance.shutdown();
        HazelcastTestSupport.assertOpenEventually(remoteLockedLatch);
    }
}

