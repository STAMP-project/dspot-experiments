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


import RaftServiceDataSerializerHook.APPEND_REQUEST_OP;
import RaftServiceDataSerializerHook.F_ID;
import RaftServiceDataSerializerHook.INSTALL_SNAPSHOT_OP;
import RaftSessionService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.RaftInvocationManager;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.RaftTestApplyOp;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.cp.session.CPSession;
import com.hazelcast.instance.Node;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.PacketFiltersUtil;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftSessionServiceTest extends HazelcastRaftTestSupport {
    private static final String RAFT_GROUP_NAME = "sessions";

    private static final int LOG_ENTRY_COUNT_TO_SNAPSHOT = 10;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private HazelcastInstance[] instances;

    private RaftInvocationManager invocationManager;

    private CPGroupId groupId;

    @Test
    public void testSessionCreate() throws InterruptedException, UnknownHostException, ExecutionException {
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSession session = registry.getSession(response.getSessionId());
                    Assert.assertNotNull(session);
                    Collection<CPSession> sessions = service.getAllSessions(groupId.name()).get();
                    Assert.assertThat(sessions, Matchers.hasItem(session));
                }
            }
        });
    }

    @Test
    public void testSessionHeartbeat() throws InterruptedException, UnknownHostException, ExecutionException {
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        final CPSessionInfo[] sessions = new CPSessionInfo[instances.length];
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (instances.length); i++) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instances[i]).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSessionInfo session = registry.getSession(response.getSessionId());
                    Assert.assertNotNull(session);
                    sessions[i] = session;
                }
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.session.operation.HeartbeatSessionOp(response.getSessionId())).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (instances.length); i++) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instances[i]).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSessionInfo session = registry.getSession(response.getSessionId());
                    Assert.assertNotNull(session);
                    Assert.assertTrue(((session.version()) > (sessions[i].version())));
                }
            }
        });
    }

    @Test
    public void testSessionClose() throws InterruptedException, UnknownHostException, ExecutionException {
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    Assert.assertNotNull(registry.getSession(response.getSessionId()));
                }
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.session.operation.CloseSessionOp(response.getSessionId())).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    Assert.assertNull(registry.getSession(response.getSessionId()));
                    Assert.assertThat(service.getAllSessions(groupId.name()).get(), Matchers.empty());
                }
            }
        });
    }

    @Test
    public void testHeartbeatFailsAfterSessionClose() throws InterruptedException, UnknownHostException, ExecutionException {
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    Assert.assertNotNull(registry.getSession(response.getSessionId()));
                }
            }
        });
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.session.operation.CloseSessionOp(response.getSessionId())).get();
        exception.expectCause(Is.isA(SessionExpiredException.class));
        invocationManager.invoke(groupId, new com.hazelcast.cp.internal.session.operation.HeartbeatSessionOp(response.getSessionId())).get();
    }

    @Test
    public void testLeaderFailureShiftsSessionExpirationTimes() throws InterruptedException, UnknownHostException, ExecutionException {
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        final CPSessionInfo[] sessions = new CPSessionInfo[instances.length];
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (instances.length); i++) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instances[i]).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSessionInfo session = registry.getSession(response.getSessionId());
                    Assert.assertNotNull(session);
                    sessions[i] = session;
                }
            }
        });
        CPMemberInfo leaderEndpoint = RaftUtil.getLeaderMember(HazelcastRaftTestSupport.getRaftNode(instances[0], groupId));
        final HazelcastInstance leader = factory.getInstance(leaderEndpoint.getAddress());
        leader.getLifecycleService().terminate();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (int i = 0; i < (instances.length); i++) {
                    Node node;
                    try {
                        node = HazelcastTestSupport.getNode(instances[i]);
                    } catch (IllegalArgumentException ignored) {
                        continue;
                    }
                    RaftSessionService service = node.nodeEngine.getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSessionInfo session = registry.getSession(response.getSessionId());
                    Assert.assertNotNull(session);
                    Assert.assertTrue(((session.version()) > (sessions[i].version())));
                }
            }
        });
    }

    @Test
    public void testSessionHeartbeatTimeout() throws InterruptedException, UnknownHostException, ExecutionException {
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSessionInfo session = registry.getSession(response.getSessionId());
                    Assert.assertNotNull(session);
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftSessionService service = HazelcastTestSupport.getNodeEngineImpl(instance).getService(SERVICE_NAME);
                    RaftSessionRegistry registry = service.getSessionRegistryOrNull(groupId);
                    Assert.assertNotNull(registry);
                    CPSessionInfo session = registry.getSession(response.getSessionId());
                    Assert.assertNull(session);
                }
            }
        });
    }

    @Test
    public void testSnapshotRestore() throws InterruptedException, UnknownHostException, ExecutionException {
        final HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final HazelcastInstance follower = getRandomFollowerInstance(instances, groupId);
        // the follower falls behind the leader. It neither append entries nor installs snapshots.
        PacketFiltersUtil.dropOperationsBetween(leader, follower, F_ID, Arrays.asList(APPEND_REQUEST_OP, INSTALL_SNAPSHOT_OP));
        final SessionResponse response = invocationManager.<SessionResponse>invoke(groupId, newCreateSessionOp()).get();
        for (int i = 0; i < (RaftSessionServiceTest.LOG_ENTRY_COUNT_TO_SNAPSHOT); i++) {
            invocationManager.invoke(groupId, new RaftTestApplyOp(("value" + i))).get();
        }
        final RaftNodeImpl leaderRaftNode = ((RaftNodeImpl) (((RaftService) (HazelcastTestSupport.getNodeEngineImpl(leader).getService(RaftService.SERVICE_NAME))).getRaftNode(groupId)));
        final RaftNodeImpl followerRaftNode = ((RaftNodeImpl) (((RaftService) (HazelcastTestSupport.getNodeEngineImpl(follower).getService(RaftService.SERVICE_NAME))).getRaftNode(groupId)));
        // the leader takes a snapshot
        final long[] leaderSnapshotIndex = new long[1];
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                long idx = RaftUtil.getSnapshotEntry(leaderRaftNode).index();
                Assert.assertTrue((idx > 0));
                leaderSnapshotIndex[0] = idx;
            }
        });
        // the follower doesn't have it since its raft log is still empty
        HazelcastTestSupport.assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                Assert.assertEquals(0, RaftUtil.getSnapshotEntry(followerRaftNode).index());
            }
        }, 10);
        PacketFiltersUtil.resetPacketFiltersFrom(leader);
        // the follower installs the snapshot after it hears from the leader
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(followerRaftNode).index()) > 0));
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftSessionService sessionService = HazelcastTestSupport.getNodeEngineImpl(follower).getService(SERVICE_NAME);
                RaftSessionRegistry registry = sessionService.getSessionRegistryOrNull(groupId);
                Assert.assertNotNull(registry.getSession(response.getSessionId()));
            }
        });
        // the follower disconnects from the leader again
        PacketFiltersUtil.dropOperationsBetween(leader, follower, F_ID, Arrays.asList(APPEND_REQUEST_OP, INSTALL_SNAPSHOT_OP));
        for (int i = 0; i < (RaftSessionServiceTest.LOG_ENTRY_COUNT_TO_SNAPSHOT); i++) {
            invocationManager.invoke(groupId, new com.hazelcast.cp.internal.session.operation.HeartbeatSessionOp(response.getSessionId())).get();
        }
        // the leader takes a new snapshot
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(((RaftUtil.getSnapshotEntry(leaderRaftNode).index()) > (leaderSnapshotIndex[0])));
            }
        });
        PacketFiltersUtil.resetPacketFiltersFrom(leader);
        // the follower installs the new snapshot after it hears from the leader
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                CPSessionInfo leaderSession = getSession(leader, groupId, response.getSessionId());
                CPSessionInfo followerSession = getSession(follower, groupId, response.getSessionId());
                Assert.assertNotNull(leaderSession);
                Assert.assertNotNull(followerSession);
                Assert.assertEquals(leaderSession.version(), followerSession.version());
            }
        });
    }
}

