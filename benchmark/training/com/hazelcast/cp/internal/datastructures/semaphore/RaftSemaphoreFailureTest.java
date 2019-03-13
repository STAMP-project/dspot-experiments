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
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.datastructures.exception.WaitKeyCancelledException;
import com.hazelcast.cp.internal.datastructures.semaphore.operation.AcquirePermitsOp;
import com.hazelcast.cp.internal.session.ProxySessionManagerService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.util.UuidUtil;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public abstract class RaftSemaphoreFailureTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private HazelcastInstance semaphoreInstance;

    private ProxySessionManagerService sessionManagerService;

    private ISemaphore semaphore;

    private String objectName = "semaphore";

    private String proxyName = (objectName) + "@group1";

    @Test
    public void testRetriedAcquireDoesNotCancelPendingAcquireRequestWhenAlreadyAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid, 1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid, 1, (-1)));
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        }, 10);
    }

    @Test(timeout = 30000)
    public void testNewAcquireCancelsPendingAcquireRequestWhenAlreadyAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid2, 1, (-1)));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testNewAcquireCancelsPendingAcquireRequestWhenNotAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // if the session-aware semaphore is used, we guarantee that there is a session id now...
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid2, 1, (-1)));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testTryAcquireWithTimeoutCancelsPendingAcquireRequestWhenAlreadyAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid2, 1, 100));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testNewTryAcquireWithTimeoutCancelsPendingAcquireRequestWhenNotAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // if the session-aware semaphore is used, we guarantee that there is a session id now...
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid2, 1, 100));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testNewTryAcquireWithoutTimeoutCancelsPendingAcquireRequestWhenAlreadyAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid2, 1, 0));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testNewTryAcquireWithoutTimeoutCancelsPendingAcquireRequestsWhenNotAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // if the session-aware semaphore is used, we guarantee that there is a session id now...
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        UUID invUid2 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid2, 1, 0));
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testReleaseCancelsPendingAcquireRequestWhenPermitsAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid, 1, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        try {
            semaphore.release();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test
    public void testReleaseCancelsPendingAcquireRequestWhenNoPermitsAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // if the session-aware semaphore is used, we guarantee that there is a session id now...
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        try {
            semaphore.release();
        } catch (IllegalArgumentException ignored) {
        }
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testDrainCancelsPendingAcquireRequestWhenNotAcquired() throws InterruptedException {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // if the session-aware semaphore is used, we guarantee that there is a session id now...
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        semaphore.drainPermits();
        try {
            f.join();
            Assert.fail();
        } catch (WaitKeyCancelledException ignored) {
        }
    }

    @Test(timeout = 30000)
    public void testRetriedAcquireReceivesPermitsOnlyOnce() throws InterruptedException, ExecutionException {
        semaphore.init(1);
        semaphore.acquire();
        semaphore.release();
        // if the session-aware semaphore is used, we guarantee that there is a session id now...
        final RaftGroupId groupId = getGroupId(semaphore);
        long sessionId = getSessionId(semaphoreInstance, groupId);
        long threadId = getThreadId(groupId);
        UUID invUid1 = UuidUtil.newUnsecureUUID();
        RaftInvocationManager invocationManager = getRaftInvocationManager(semaphoreInstance);
        InternalCompletableFuture<Object> f1 = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertEquals(1, registry.getWaitTimeouts().size());
            }
        });
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    semaphore.tryAcquire(20, 5, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertEquals(2, registry.getWaitTimeouts().size());
            }
        });
        InternalCompletableFuture<Object> f2 = invocationManager.invoke(groupId, new AcquirePermitsOp(objectName, sessionId, threadId, invUid1, 2, TimeUnit.MINUTES.toMillis(5)));
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSemaphoreService service = HazelcastTestSupport.getNodeEngineImpl(semaphoreInstance).getService(SERVICE_NAME);
                RaftSemaphoreRegistry registry = service.getRegistryOrNull(groupId);
                RaftSemaphore raftSemaphore = registry.getResourceOrNull(objectName);
                Assert.assertEquals(2, raftSemaphore.getInternalWaitKeysMap().size());
            }
        });
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                semaphore.increasePermits(3);
            }
        }).get();
        f1.join();
        f2.join();
        Assert.assertEquals(2, semaphore.availablePermits());
    }

    @Test
    public void testAcquireOnMultipleProxies() {
        HazelcastInstance otherInstance = ((instances[0]) == (semaphoreInstance)) ? instances[1] : instances[0];
        ISemaphore semaphore2 = otherInstance.getCPSubsystem().getSemaphore(proxyName);
        semaphore.init(1);
        semaphore.tryAcquire(1);
        Assert.assertFalse(semaphore2.tryAcquire());
    }
}

