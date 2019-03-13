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
package com.hazelcast.cp.internal.session;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastTestSupport;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public abstract class AbstractProxySessionManagerTest extends HazelcastRaftTestSupport {
    private static final int sessionTTLSeconds = 5;

    HazelcastInstance[] members;

    protected RaftGroupId groupId;

    @Test
    public void getSession_returnsNoSessionId_whenNoSessionCreated() {
        AbstractProxySessionManager sessionManager = getSessionManager();
        Assert.assertEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionManager.getSession(groupId));
    }

    @Test
    public void acquireSession_createsNewSession_whenSessionNotExists() {
        AbstractProxySessionManager sessionManager = getSessionManager();
        final long sessionId = sessionManager.acquireSession(groupId);
        Assert.assertNotEquals(AbstractProxySessionManager.NO_SESSION_ID, sessionId);
        Assert.assertEquals(sessionId, sessionManager.getSession(groupId));
        Assert.assertEquals(1, sessionManager.getSessionAcquireCount(groupId, sessionId));
        final SessionAccessor sessionAccessor = getSessionAccessor();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(sessionAccessor.isActive(groupId, sessionId));
            }
        });
    }

    @Test
    public void acquireSession_returnsExistingSession_whenSessionExists() {
        AbstractProxySessionManager sessionManager = getSessionManager();
        long newSessionId = sessionManager.acquireSession(groupId);
        long sessionId = sessionManager.acquireSession(groupId);
        Assert.assertEquals(newSessionId, sessionId);
        Assert.assertEquals(sessionId, sessionManager.getSession(groupId));
        Assert.assertEquals(2, sessionManager.getSessionAcquireCount(groupId, sessionId));
    }

    @Test
    public void acquireSession_returnsTheSameSessionId_whenExecutedConcurrently() throws Exception {
        final AbstractProxySessionManager sessionManager = getSessionManager();
        Callable<Long> acquireSessionCall = new Callable<Long>() {
            @Override
            public Long call() {
                return sessionManager.acquireSession(groupId);
            }
        };
        Future<Long>[] futures = new Future[5];
        for (int i = 0; i < (futures.length); i++) {
            futures[i] = HazelcastTestSupport.spawn(acquireSessionCall);
        }
        long[] sessions = new long[futures.length];
        for (int i = 0; i < (futures.length); i++) {
            sessions[i] = futures[i].get();
        }
        long expectedSessionId = sessionManager.getSession(groupId);
        for (long sessionId : sessions) {
            Assert.assertEquals(expectedSessionId, sessionId);
        }
        Assert.assertEquals(sessions.length, sessionManager.getSessionAcquireCount(groupId, expectedSessionId));
    }

    @Test
    public void releaseSession_hasNoEffect_whenSessionNotExists() {
        AbstractProxySessionManager sessionManager = getSessionManager();
        sessionManager.releaseSession(groupId, 1);
    }

    @Test
    public void releaseSession_whenSessionExists() {
        AbstractProxySessionManager sessionManager = getSessionManager();
        long sessionId = sessionManager.acquireSession(groupId);
        sessionManager.releaseSession(groupId, sessionId);
        Assert.assertEquals(0, sessionManager.getSessionAcquireCount(groupId, sessionId));
    }

    @Test
    public void sessionHeartbeatsAreNotSent_whenSessionNotExists() {
        final AbstractProxySessionManager sessionManager = getSessionManager();
        final long sessionId = 1;
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Mockito.verify(sessionManager, Mockito.never()).heartbeat(groupId, sessionId);
            }
        }, 5);
    }

    @Test
    public void sessionHeartbeatsAreSent_whenSessionInUse() {
        final AbstractProxySessionManager sessionManager = getSessionManager();
        final long sessionId = sessionManager.acquireSession(groupId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Mockito.verify(sessionManager, Mockito.atLeastOnce()).heartbeat(groupId, sessionId);
            }
        });
        final SessionAccessor sessionAccessor = getSessionAccessor();
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(sessionAccessor.isActive(groupId, sessionId));
            }
        }, AbstractProxySessionManagerTest.sessionTTLSeconds);
    }

    @Test
    public void sessionHeartbeatsAreNotSent_whenSessionReleased() {
        final AbstractProxySessionManager sessionManager = getSessionManager();
        final long sessionId = sessionManager.acquireSession(groupId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Mockito.verify(sessionManager, Mockito.atLeastOnce()).heartbeat(groupId, sessionId);
            }
        });
        sessionManager.releaseSession(groupId, sessionId);
        final SessionAccessor sessionAccessor = getSessionAccessor();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(sessionAccessor.isActive(groupId, sessionId));
            }
        });
    }

    @Test
    public void acquireSession_returnsTheExistingSession_whenSessionInUse() {
        final AbstractProxySessionManager sessionManager = getSessionManager();
        final long sessionId = sessionManager.acquireSession(groupId);
        Mockito.when(sessionManager.heartbeat(groupId, sessionId)).thenReturn(completedFuture());
        final SessionAccessor sessionAccessor = getSessionAccessor();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(sessionAccessor.isActive(groupId, sessionId));
            }
        });
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(sessionId, sessionManager.acquireSession(groupId));
            }
        }, 3);
    }

    @Test
    public void acquireSession_returnsNewSession_whenSessionExpiredAndNotInUse() {
        final AbstractProxySessionManager sessionManager = getSessionManager();
        final long sessionId = sessionManager.acquireSession(groupId);
        Mockito.when(sessionManager.heartbeat(groupId, sessionId)).thenReturn(completedFuture());
        final SessionAccessor sessionAccessor = getSessionAccessor();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(sessionAccessor.isActive(groupId, sessionId));
            }
        });
        sessionManager.releaseSession(groupId, sessionId);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                long newSessionId = sessionManager.acquireSession(groupId);
                sessionManager.releaseSession(groupId, newSessionId);
                Assert.assertNotEquals(sessionId, newSessionId);
            }
        });
    }

    private static class CallerRunsExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}

