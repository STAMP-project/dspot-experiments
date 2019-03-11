/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.clients.consumer.internals;


import AbstractCoordinator.Generation;
import Errors.COORDINATOR_LOAD_IN_PROGRESS;
import Errors.COORDINATOR_NOT_AVAILABLE;
import Errors.GROUP_AUTHORIZATION_FAILED;
import Errors.ILLEGAL_GENERATION;
import Errors.INVALID_GROUP_ID;
import Errors.INVALID_SESSION_TIMEOUT;
import Errors.MEMBER_ID_REQUIRED;
import Errors.NONE;
import Errors.NOT_COORDINATOR;
import Errors.OFFSET_METADATA_TOO_LARGE;
import Errors.REBALANCE_IN_PROGRESS;
import Errors.UNKNOWN_MEMBER_ID;
import Errors.UNKNOWN_SERVER_ERROR;
import Errors.UNKNOWN_TOPIC_OR_PARTITION;
import OffsetCommitRequest.DEFAULT_MEMBER_ID;
import OffsetCommitRequest.PartitionData;
import OffsetResetStrategy.EARLIEST;
import PartitionAssignor.Subscription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import org.apache.kafka.clients.ClientResponse;
import org.apache.kafka.clients.MockClient;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.clients.consumer.RangeAssignor;
import org.apache.kafka.clients.consumer.RetriableCommitFailedException;
import org.apache.kafka.clients.consumer.RoundRobinAssignor;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ApiException;
import org.apache.kafka.common.errors.AuthenticationException;
import org.apache.kafka.common.errors.DisconnectException;
import org.apache.kafka.common.errors.GroupAuthorizationException;
import org.apache.kafka.common.errors.OffsetMetadataTooLarge;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.message.LeaveGroupResponseData;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.AbstractRequest;
import org.apache.kafka.common.requests.JoinGroupRequest;
import org.apache.kafka.common.requests.JoinGroupRequest.ProtocolMetadata;
import org.apache.kafka.common.requests.LeaveGroupRequest;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.requests.OffsetCommitRequest;
import org.apache.kafka.common.requests.OffsetFetchResponse;
import org.apache.kafka.common.requests.SyncGroupRequest;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.test.TestCondition;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerCoordinatorTest {
    private final String topic1 = "test1";

    private final String topic2 = "test2";

    private final TopicPartition t1p = new TopicPartition(topic1, 0);

    private final TopicPartition t2p = new TopicPartition(topic2, 0);

    private final String groupId = "test-group";

    private final int rebalanceTimeoutMs = 60000;

    private final int sessionTimeoutMs = 10000;

    private final int heartbeatIntervalMs = 5000;

    private final long retryBackoffMs = 100;

    private final int autoCommitIntervalMs = 2000;

    private final int requestTimeoutMs = 30000;

    private final MockTime time = new MockTime();

    private final Heartbeat heartbeat = new Heartbeat(time, sessionTimeoutMs, heartbeatIntervalMs, rebalanceTimeoutMs, retryBackoffMs);

    private MockPartitionAssignor partitionAssignor = new MockPartitionAssignor();

    private List<PartitionAssignor> assignors = Collections.singletonList(partitionAssignor);

    private MockClient client;

    private MetadataResponse metadataResponse = TestUtils.metadataUpdateWith(1, new HashMap<String, Integer>() {
        {
            put(topic1, 1);
            put(topic2, 1);
        }
    });

    private Node node = metadataResponse.brokers().iterator().next();

    private SubscriptionState subscriptions;

    private ConsumerMetadata metadata;

    private Metrics metrics;

    private ConsumerNetworkClient consumerClient;

    private ConsumerCoordinatorTest.MockRebalanceListener rebalanceListener;

    private ConsumerCoordinatorTest.MockCommitCallback mockOffsetCommitCallback;

    private ConsumerCoordinator coordinator;

    @Test
    public void testNormalHeartbeat() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // normal heartbeat
        time.sleep(sessionTimeoutMs);
        RequestFuture<Void> future = coordinator.sendHeartbeatRequest();// should send out the heartbeat

        Assert.assertEquals(1, consumerClient.pendingRequestCount());
        Assert.assertFalse(future.isDone());
        client.prepareResponse(heartbeatResponse(NONE));
        consumerClient.poll(time.timer(0));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.succeeded());
    }

    @Test(expected = GroupAuthorizationException.class)
    public void testGroupDescribeUnauthorized() {
        client.prepareResponse(groupCoordinatorResponse(node, GROUP_AUTHORIZATION_FAILED));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
    }

    @Test(expected = GroupAuthorizationException.class)
    public void testGroupReadUnauthorized() {
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        client.prepareResponse(joinGroupLeaderResponse(0, "memberId", Collections.<String, List<String>>emptyMap(), GROUP_AUTHORIZATION_FAILED));
        coordinator.poll(timer(Long.MAX_VALUE));
    }

    @Test
    public void testCoordinatorNotAvailable() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // COORDINATOR_NOT_AVAILABLE will mark coordinator as unknown
        time.sleep(sessionTimeoutMs);
        RequestFuture<Void> future = coordinator.sendHeartbeatRequest();// should send out the heartbeat

        Assert.assertEquals(1, consumerClient.pendingRequestCount());
        Assert.assertFalse(future.isDone());
        client.prepareResponse(heartbeatResponse(COORDINATOR_NOT_AVAILABLE));
        time.sleep(sessionTimeoutMs);
        consumerClient.poll(time.timer(0));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.failed());
        Assert.assertEquals(COORDINATOR_NOT_AVAILABLE.exception(), future.exception());
        Assert.assertTrue(coordinator.coordinatorUnknown());
    }

    @Test
    public void testManyInFlightAsyncCommitsWithCoordinatorDisconnect() throws Exception {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        int numRequests = 1000;
        TopicPartition tp = new TopicPartition("foo", 0);
        final AtomicInteger responses = new AtomicInteger(0);
        for (int i = 0; i < numRequests; i++) {
            Map<TopicPartition, OffsetAndMetadata> offsets = Collections.singletonMap(tp, new OffsetAndMetadata(i));
            coordinator.commitOffsetsAsync(offsets, new OffsetCommitCallback() {
                @Override
                public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                    responses.incrementAndGet();
                    Throwable cause = exception.getCause();
                    Assert.assertTrue(("Unexpected exception cause type: " + (cause == null ? null : cause.getClass())), (cause instanceof DisconnectException));
                }
            });
        }
        coordinator.markCoordinatorUnknown();
        consumerClient.pollNoWakeup();
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertEquals(numRequests, responses.get());
    }

    @Test
    public void testCoordinatorUnknownInUnsentCallbacksAfterCoordinatorDead() throws Exception {
        // When the coordinator is marked dead, all unsent or in-flight requests are cancelled
        // with a disconnect error. This test case ensures that the corresponding callbacks see
        // the coordinator as unknown which prevents additional retries to the same coordinator.
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        final AtomicBoolean asyncCallbackInvoked = new AtomicBoolean(false);
        Map<TopicPartition, OffsetCommitRequest.PartitionData> offsets = Collections.singletonMap(new TopicPartition("foo", 0), new OffsetCommitRequest.PartitionData(13L, Optional.empty(), ""));
        consumerClient.send(coordinator.checkAndGetCoordinator(), new OffsetCommitRequest.Builder(groupId, offsets)).compose(new RequestFutureAdapter<ClientResponse, Object>() {
            @Override
            public void onSuccess(ClientResponse value, RequestFuture<Object> future) {
            }

            @Override
            public void onFailure(RuntimeException e, RequestFuture<Object> future) {
                Assert.assertTrue(("Unexpected exception type: " + (e.getClass())), (e instanceof DisconnectException));
                Assert.assertTrue(coordinator.coordinatorUnknown());
                asyncCallbackInvoked.set(true);
            }
        });
        coordinator.markCoordinatorUnknown();
        consumerClient.pollNoWakeup();
        Assert.assertTrue(asyncCallbackInvoked.get());
    }

    @Test
    public void testNotCoordinator() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // not_coordinator will mark coordinator as unknown
        time.sleep(sessionTimeoutMs);
        RequestFuture<Void> future = coordinator.sendHeartbeatRequest();// should send out the heartbeat

        Assert.assertEquals(1, consumerClient.pendingRequestCount());
        Assert.assertFalse(future.isDone());
        client.prepareResponse(heartbeatResponse(NOT_COORDINATOR));
        time.sleep(sessionTimeoutMs);
        consumerClient.poll(time.timer(0));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.failed());
        Assert.assertEquals(NOT_COORDINATOR.exception(), future.exception());
        Assert.assertTrue(coordinator.coordinatorUnknown());
    }

    @Test
    public void testIllegalGeneration() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // illegal_generation will cause re-partition
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        subscriptions.assignFromSubscribed(Collections.singletonList(t1p));
        time.sleep(sessionTimeoutMs);
        RequestFuture<Void> future = coordinator.sendHeartbeatRequest();// should send out the heartbeat

        Assert.assertEquals(1, consumerClient.pendingRequestCount());
        Assert.assertFalse(future.isDone());
        client.prepareResponse(heartbeatResponse(ILLEGAL_GENERATION));
        time.sleep(sessionTimeoutMs);
        consumerClient.poll(time.timer(0));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.failed());
        Assert.assertEquals(ILLEGAL_GENERATION.exception(), future.exception());
        Assert.assertTrue(coordinator.rejoinNeededOrPending());
    }

    @Test
    public void testUnknownConsumerId() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // illegal_generation will cause re-partition
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        subscriptions.assignFromSubscribed(Collections.singletonList(t1p));
        time.sleep(sessionTimeoutMs);
        RequestFuture<Void> future = coordinator.sendHeartbeatRequest();// should send out the heartbeat

        Assert.assertEquals(1, consumerClient.pendingRequestCount());
        Assert.assertFalse(future.isDone());
        client.prepareResponse(heartbeatResponse(UNKNOWN_MEMBER_ID));
        time.sleep(sessionTimeoutMs);
        consumerClient.poll(time.timer(0));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.failed());
        Assert.assertEquals(UNKNOWN_MEMBER_ID.exception(), future.exception());
        Assert.assertTrue(coordinator.rejoinNeededOrPending());
    }

    @Test
    public void testCoordinatorDisconnect() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // coordinator disconnect will mark coordinator as unknown
        time.sleep(sessionTimeoutMs);
        RequestFuture<Void> future = coordinator.sendHeartbeatRequest();// should send out the heartbeat

        Assert.assertEquals(1, consumerClient.pendingRequestCount());
        Assert.assertFalse(future.isDone());
        client.prepareResponse(heartbeatResponse(NONE), true);// return disconnected

        time.sleep(sessionTimeoutMs);
        consumerClient.poll(time.timer(0));
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.failed());
        Assert.assertTrue(((future.exception()) instanceof DisconnectException));
        Assert.assertTrue(coordinator.coordinatorUnknown());
    }

    @Test(expected = ApiException.class)
    public void testJoinGroupInvalidGroupId() {
        final String consumerId = "leader";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        // ensure metadata is up-to-date for leader
        client.updateMetadata(metadataResponse);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        client.prepareResponse(joinGroupLeaderResponse(0, consumerId, Collections.<String, List<String>>emptyMap(), INVALID_GROUP_ID));
        coordinator.poll(timer(Long.MAX_VALUE));
    }

    @Test
    public void testNormalJoinGroupLeader() {
        final String consumerId = "leader";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        // ensure metadata is up-to-date for leader
        client.updateMetadata(metadataResponse);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // normal join group
        Map<String, List<String>> memberSubscriptions = Collections.singletonMap(consumerId, Collections.singletonList(topic1));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t1p)));
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberSubscriptions, NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId));
            }
        }, syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
        Assert.assertEquals(Collections.singleton(topic1), subscriptions.groupSubscription());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.emptySet(), rebalanceListener.revoked);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
    }

    @Test
    public void testOutdatedCoordinatorAssignment() {
        final String consumerId = "outdated_assignment";
        subscriptions.subscribe(Collections.singleton(topic2), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // Test coordinator returning unsubscribed partitions
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t1p)));
        // First incorrect assignment for subscription
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, Collections.singletonMap(consumerId, Collections.singletonList(topic2)), NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId));
            }
        }, syncGroupResponse(Arrays.asList(t2p), NONE));
        // Second correct assignment for subscription
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, Collections.singletonMap(consumerId, Collections.singletonList(topic1)), NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId));
            }
        }, syncGroupResponse(Collections.singletonList(t1p), NONE));
        // Poll once so that the join group future gets created and complete
        coordinator.poll(time.timer(0));
        // Before the sync group response gets completed change the subscription
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        coordinator.poll(time.timer(0));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
        Assert.assertEquals(Collections.singleton(topic1), subscriptions.groupSubscription());
        Assert.assertEquals(2, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.emptySet(), rebalanceListener.revoked);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
    }

    @Test
    public void testInvalidCoordinatorAssignment() {
        final String consumerId = "invalid_assignment";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // normal join group
        Map<String, List<String>> memberSubscriptions = Collections.singletonMap(consumerId, Collections.singletonList(topic2));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t2p)));
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberSubscriptions, NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId));
            }
        }, syncGroupResponse(Collections.singletonList(t2p), NONE));
        Assert.assertThrows(IllegalStateException.class, () -> coordinator.poll(time.timer(Long.MAX_VALUE)));
    }

    @Test
    public void testPatternJoinGroupLeader() {
        final String consumerId = "leader";
        subscriptions.subscribe(Pattern.compile("test.*"), rebalanceListener);
        // partially update the metadata with one topic first,
        // let the leader to refresh metadata during assignment
        client.updateMetadata(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 1)));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // normal join group
        Map<String, List<String>> memberSubscriptions = Collections.singletonMap(consumerId, Collections.singletonList(topic1));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Arrays.asList(t1p, t2p)));
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberSubscriptions, NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId));
            }
        }, syncGroupResponse(Arrays.asList(t1p, t2p), NONE));
        // expect client to force updating the metadata, if yes gives it both topics
        client.prepareMetadataUpdate(metadataResponse);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(2, subscriptions.numAssignedPartitions());
        Assert.assertEquals(2, subscriptions.groupSubscription().size());
        Assert.assertEquals(2, subscriptions.subscription().size());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.emptySet(), rebalanceListener.revoked);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(2, rebalanceListener.assigned.size());
    }

    @Test
    public void testMetadataRefreshDuringRebalance() {
        final String consumerId = "leader";
        subscriptions.subscribe(Pattern.compile(".*"), rebalanceListener);
        client.updateMetadata(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 1)));
        coordinator.maybeUpdateSubscriptionMetadata();
        Assert.assertEquals(Collections.singleton(topic1), subscriptions.subscription());
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        Map<String, List<String>> initialSubscription = Collections.singletonMap(consumerId, Collections.singletonList(topic1));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t1p)));
        // the metadata will be updated in flight with a new topic added
        final List<String> updatedSubscription = Arrays.asList(topic1, topic2);
        final Set<String> updatedSubscriptionSet = new HashSet<>(updatedSubscription);
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, initialSubscription, NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                final Map<String, Integer> updatedPartitions = new HashMap<>();
                for (String topic : updatedSubscription)
                    updatedPartitions.put(topic, 1);

                client.updateMetadata(TestUtils.metadataUpdateWith(1, updatedPartitions));
                return true;
            }
        }, syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        List<TopicPartition> newAssignment = Arrays.asList(t1p, t2p);
        Set<TopicPartition> newAssignmentSet = new HashSet(newAssignment);
        Map<String, List<String>> updatedSubscriptions = Collections.singletonMap(consumerId, Arrays.asList(topic1, topic2));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, newAssignment));
        // we expect to see a second rebalance with the new-found topics
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                JoinGroupRequest join = ((JoinGroupRequest) (body));
                ProtocolMetadata protocolMetadata = join.groupProtocols().iterator().next();
                PartitionAssignor.Subscription subscription = ConsumerProtocol.deserializeSubscription(protocolMetadata.metadata());
                protocolMetadata.metadata().rewind();
                return subscription.topics().containsAll(updatedSubscriptionSet);
            }
        }, joinGroupLeaderResponse(2, consumerId, updatedSubscriptions, NONE));
        client.prepareResponse(syncGroupResponse(newAssignment, NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(updatedSubscriptionSet, subscriptions.subscription());
        Assert.assertEquals(newAssignmentSet, subscriptions.assignedPartitions());
        Assert.assertEquals(2, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.revoked);
        Assert.assertEquals(2, rebalanceListener.assignedCount);
        Assert.assertEquals(newAssignmentSet, rebalanceListener.assigned);
    }

    @Test
    public void testForceMetadataRefreshForPatternSubscriptionDuringRebalance() {
        // Set up a non-leader consumer with pattern subscription and a cluster containing one topic matching the
        // pattern.
        final String consumerId = "consumer";
        subscriptions.subscribe(Pattern.compile(".*"), rebalanceListener);
        client.updateMetadata(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 1)));
        coordinator.maybeUpdateSubscriptionMetadata();
        Assert.assertEquals(Collections.singleton(topic1), subscriptions.subscription());
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // Instrument the test so that metadata will contain two topics after next refresh.
        client.prepareMetadataUpdate(metadataResponse);
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().isEmpty());
            }
        }, syncGroupResponse(Collections.singletonList(t1p), NONE));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t1p)));
        // This will trigger rebalance.
        coordinator.poll(timer(Long.MAX_VALUE));
        // Make sure that the metadata was refreshed during the rebalance and thus subscriptions now contain two topics.
        final Set<String> updatedSubscriptionSet = new HashSet<>(Arrays.asList(topic1, topic2));
        Assert.assertEquals(updatedSubscriptionSet, subscriptions.subscription());
        // Refresh the metadata again. Since there have been no changes since the last refresh, it won't trigger
        // rebalance again.
        metadata.requestUpdate();
        consumerClient.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
    }

    @Test
    public void testWakeupDuringJoin() {
        final String consumerId = "leader";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        // ensure metadata is up-to-date for leader
        client.updateMetadata(metadataResponse);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        Map<String, List<String>> memberSubscriptions = Collections.singletonMap(consumerId, Collections.singletonList(topic1));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t1p)));
        // prepare only the first half of the join and then trigger the wakeup
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberSubscriptions, NONE));
        consumerClient.wakeup();
        try {
            coordinator.poll(timer(Long.MAX_VALUE));
        } catch (WakeupException e) {
            // ignore
        }
        // now complete the second half
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.emptySet(), rebalanceListener.revoked);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
    }

    @Test
    public void testNormalJoinGroupFollower() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // normal join group
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().isEmpty());
            }
        }, syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
        Assert.assertEquals(Collections.singleton(topic1), subscriptions.groupSubscription());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.emptySet(), rebalanceListener.revoked);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
    }

    @Test
    public void testUpdateLastHeartbeatPollWhenCoordinatorUnknown() throws Exception {
        // If we are part of an active group and we cannot find the coordinator, we should nevertheless
        // continue to update the last poll time so that we do not expire the consumer
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // Join the group, but signal a coordinator change after the first heartbeat
        client.prepareResponse(joinGroupFollowerResponse(1, "consumer", "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        client.prepareResponse(heartbeatResponse(NOT_COORDINATOR));
        coordinator.poll(timer(Long.MAX_VALUE));
        time.sleep(heartbeatIntervalMs);
        // Await the first heartbeat which forces us to find a new coordinator
        TestUtils.waitForCondition(() -> !(client.hasPendingResponses()), "Failed to observe expected heartbeat from background thread");
        Assert.assertTrue(coordinator.coordinatorUnknown());
        Assert.assertFalse(coordinator.poll(time.timer(0)));
        Assert.assertEquals(time.milliseconds(), heartbeat.lastPollTime());
        time.sleep(((rebalanceTimeoutMs) - 1));
        Assert.assertFalse(heartbeat.pollTimeoutExpired(time.milliseconds()));
    }

    @Test
    public void testPatternJoinGroupFollower() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Pattern.compile("test.*"), rebalanceListener);
        // partially update the metadata with one topic first,
        // let the leader to refresh metadata during assignment
        client.updateMetadata(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 1)));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // normal join group
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                return ((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().isEmpty());
            }
        }, syncGroupResponse(Arrays.asList(t1p, t2p), NONE));
        // expect client to force updating the metadata, if yes gives it both topics
        client.prepareMetadataUpdate(metadataResponse);
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(2, subscriptions.numAssignedPartitions());
        Assert.assertEquals(2, subscriptions.subscription().size());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(2, rebalanceListener.assigned.size());
    }

    @Test
    public void testLeaveGroupOnClose() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        joinAsFollowerAndReceiveAssignment(consumerId, coordinator, Collections.singletonList(t1p));
        final AtomicBoolean received = new AtomicBoolean(false);
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                received.set(true);
                LeaveGroupRequest leaveRequest = ((LeaveGroupRequest) (body));
                return (leaveRequest.data().memberId().equals(consumerId)) && (leaveRequest.data().groupId().equals(groupId));
            }
        }, new org.apache.kafka.common.requests.LeaveGroupResponse(new LeaveGroupResponseData().setErrorCode(NONE.code())));
        coordinator.close(time.timer(0));
        Assert.assertTrue(received.get());
    }

    @Test
    public void testMaybeLeaveGroup() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        joinAsFollowerAndReceiveAssignment(consumerId, coordinator, Collections.singletonList(t1p));
        final AtomicBoolean received = new AtomicBoolean(false);
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                received.set(true);
                LeaveGroupRequest leaveRequest = ((LeaveGroupRequest) (body));
                return (leaveRequest.data().memberId().equals(consumerId)) && (leaveRequest.data().groupId().equals(groupId));
            }
        }, new org.apache.kafka.common.requests.LeaveGroupResponse(new LeaveGroupResponseData().setErrorCode(NONE.code())));
        coordinator.maybeLeaveGroup();
        Assert.assertTrue(received.get());
        AbstractCoordinator.Generation generation = coordinator.generation();
        Assert.assertNull(generation);
    }

    /**
     * This test checks if a consumer that has a valid member ID but an invalid generation
     * ({@link org.apache.kafka.clients.consumer.internals.AbstractCoordinator.Generation#NO_GENERATION})
     * can still execute a leave group request. Such a situation may arise when a consumer has initiated a JoinGroup
     * request without a memberId, but is shutdown or restarted before it has a chance to initiate and complete the
     * second request.
     */
    @Test
    public void testPendingMemberShouldLeaveGroup() {
        final String consumerId = "consumer-id";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // here we return a DEFAULT_GENERATION_ID, but valid member id and leader id.
        client.prepareResponse(joinGroupFollowerResponse((-1), consumerId, "leader-id", MEMBER_ID_REQUIRED));
        // execute join group
        coordinator.joinGroupIfNeeded(time.timer(0));
        final AtomicBoolean received = new AtomicBoolean(false);
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                received.set(true);
                LeaveGroupRequest leaveRequest = ((LeaveGroupRequest) (body));
                return leaveRequest.data().memberId().equals(consumerId);
            }
        }, new org.apache.kafka.common.requests.LeaveGroupResponse(new LeaveGroupResponseData().setErrorCode(NONE.code())));
        coordinator.maybeLeaveGroup();
        Assert.assertTrue(received.get());
    }

    @Test(expected = KafkaException.class)
    public void testUnexpectedErrorOnSyncGroup() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // join initially, but let coordinator rebalance on sync
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.emptyList(), UNKNOWN_SERVER_ERROR));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
    }

    @Test
    public void testUnknownMemberIdOnSyncGroup() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // join initially, but let coordinator returns unknown member id
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.<TopicPartition>emptyList(), UNKNOWN_MEMBER_ID));
        // now we should see a new join with the empty UNKNOWN_MEMBER_ID
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                JoinGroupRequest joinRequest = ((JoinGroupRequest) (body));
                return joinRequest.memberId().equals(JoinGroupRequest.UNKNOWN_MEMBER_ID);
            }
        }, joinGroupFollowerResponse(2, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
    }

    @Test
    public void testRebalanceInProgressOnSyncGroup() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // join initially, but let coordinator rebalance on sync
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.<TopicPartition>emptyList(), REBALANCE_IN_PROGRESS));
        // then let the full join/sync finish successfully
        client.prepareResponse(joinGroupFollowerResponse(2, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
    }

    @Test
    public void testIllegalGenerationOnSyncGroup() {
        final String consumerId = "consumer";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // join initially, but let coordinator rebalance on sync
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.<TopicPartition>emptyList(), ILLEGAL_GENERATION));
        // then let the full join/sync finish successfully
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                JoinGroupRequest joinRequest = ((JoinGroupRequest) (body));
                return joinRequest.memberId().equals(JoinGroupRequest.UNKNOWN_MEMBER_ID);
            }
        }, joinGroupFollowerResponse(2, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
    }

    @Test
    public void testMetadataChangeTriggersRebalance() {
        final String consumerId = "consumer";
        // ensure metadata is up-to-date for leader
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.updateMetadata(metadataResponse);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        Map<String, List<String>> memberSubscriptions = Collections.singletonMap(consumerId, Collections.singletonList(topic1));
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(t1p)));
        // the leader is responsible for picking up metadata changes and forcing a group rebalance
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberSubscriptions, NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        // a new partition is added to the topic
        metadata.update(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 2)), time.milliseconds());
        coordinator.maybeUpdateSubscriptionMetadata();
        // we should detect the change and ask for reassignment
        Assert.assertTrue(coordinator.rejoinNeededOrPending());
    }

    @Test
    public void testUpdateMetadataDuringRebalance() {
        final String topic1 = "topic1";
        final String topic2 = "topic2";
        TopicPartition tp1 = new TopicPartition(topic1, 0);
        TopicPartition tp2 = new TopicPartition(topic2, 0);
        final String consumerId = "leader";
        List<String> topics = Arrays.asList(topic1, topic2);
        subscriptions.subscribe(new HashSet(topics), rebalanceListener);
        // we only have metadata for one topic initially
        client.updateMetadata(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 1)));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // prepare initial rebalance
        Map<String, List<String>> memberSubscriptions = Collections.singletonMap(consumerId, topics);
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(tp1)));
        client.prepareResponse(joinGroupLeaderResponse(1, consumerId, memberSubscriptions, NONE));
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                SyncGroupRequest sync = ((SyncGroupRequest) (body));
                if (((sync.memberId().equals(consumerId)) && ((sync.generationId()) == 1)) && (sync.groupAssignment().containsKey(consumerId))) {
                    // trigger the metadata update including both topics after the sync group request has been sent
                    Map<String, Integer> topicPartitionCounts = new HashMap<>();
                    topicPartitionCounts.put(topic1, 1);
                    topicPartitionCounts.put(topic2, 1);
                    client.updateMetadata(TestUtils.metadataUpdateWith(1, topicPartitionCounts));
                    return true;
                }
                return false;
            }
        }, syncGroupResponse(Collections.singletonList(tp1), NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        // the metadata update should trigger a second rebalance
        client.prepareResponse(joinGroupLeaderResponse(2, consumerId, memberSubscriptions, NONE));
        client.prepareResponse(syncGroupResponse(Arrays.asList(tp1, tp2), NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(new HashSet(Arrays.asList(tp1, tp2)), subscriptions.assignedPartitions());
    }

    @Test
    public void testWakeupFromAssignmentCallback() {
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, false, true);
        final String topic = "topic1";
        TopicPartition partition = new TopicPartition(topic, 0);
        final String consumerId = "follower";
        Set<String> topics = Collections.singleton(topic);
        ConsumerCoordinatorTest.MockRebalanceListener rebalanceListener = new ConsumerCoordinatorTest.MockRebalanceListener() {
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                boolean raiseWakeup = (this.assignedCount) == 0;
                super.onPartitionsAssigned(partitions);
                if (raiseWakeup)
                    throw new WakeupException();

            }
        };
        subscriptions.subscribe(topics, rebalanceListener);
        // we only have metadata for one topic initially
        client.updateMetadata(TestUtils.metadataUpdateWith(1, Collections.singletonMap(topic1, 1)));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // prepare initial rebalance
        partitionAssignor.prepare(Collections.singletonMap(consumerId, Collections.singletonList(partition)));
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(partition), NONE));
        // The first call to poll should raise the exception from the rebalance listener
        try {
            coordinator.poll(timer(Long.MAX_VALUE));
            Assert.fail("Expected exception thrown from assignment callback");
        } catch (WakeupException e) {
        }
        // The second call should retry the assignment callback and succeed
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(2, rebalanceListener.assignedCount);
    }

    @Test
    public void testRebalanceAfterTopicUnavailableWithSubscribe() {
        unavailableTopicTest(false, Collections.emptySet());
    }

    @Test
    public void testRebalanceAfterTopicUnavailableWithPatternSubscribe() {
        unavailableTopicTest(true, Collections.emptySet());
    }

    @Test
    public void testRebalanceAfterNotMatchingTopicUnavailableWithPatternSubscribe() {
        unavailableTopicTest(true, Collections.singleton("notmatching"));
    }

    @Test
    public void testExcludeInternalTopicsConfigOption() {
        testInternalTopicInclusion(false);
    }

    @Test
    public void testIncludeInternalTopicsConfigOption() {
        testInternalTopicInclusion(true);
    }

    @Test
    public void testRejoinGroup() {
        String otherTopic = "otherTopic";
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        // join the group once
        joinAsFollowerAndReceiveAssignment("consumer", coordinator, Collections.singletonList(t1p));
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertTrue(rebalanceListener.revoked.isEmpty());
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
        // and join the group again
        subscriptions.subscribe(new HashSet(Arrays.asList(topic1, otherTopic)), rebalanceListener);
        client.prepareResponse(joinGroupFollowerResponse(2, "consumer", "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(2, rebalanceListener.revokedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.revoked);
        Assert.assertEquals(2, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
    }

    @Test
    public void testDisconnectInJoin() {
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // disconnected from original coordinator will cause re-discover and join again
        client.prepareResponse(joinGroupFollowerResponse(1, "consumer", "leader", NONE), true);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        client.prepareResponse(joinGroupFollowerResponse(1, "consumer", "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.rejoinNeededOrPending());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.assignedPartitions());
        Assert.assertEquals(1, rebalanceListener.revokedCount);
        Assert.assertEquals(1, rebalanceListener.assignedCount);
        Assert.assertEquals(Collections.singleton(t1p), rebalanceListener.assigned);
    }

    @Test(expected = ApiException.class)
    public void testInvalidSessionTimeout() {
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // coordinator doesn't like the session timeout
        client.prepareResponse(joinGroupFollowerResponse(0, "consumer", "", INVALID_SESSION_TIMEOUT));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
    }

    @Test
    public void testCommitOffsetOnly() {
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        AtomicBoolean success = new AtomicBoolean(false);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), callback(success));
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertTrue(success.get());
    }

    @Test
    public void testCoordinatorDisconnectAfterNotCoordinatorError() {
        testInFlightRequestsFailedAfterCoordinatorMarkedDead(NOT_COORDINATOR);
    }

    @Test
    public void testCoordinatorDisconnectAfterCoordinatorNotAvailableError() {
        testInFlightRequestsFailedAfterCoordinatorMarkedDead(COORDINATOR_NOT_AVAILABLE);
    }

    @Test
    public void testAutoCommitDynamicAssignment() {
        final String consumerId = "consumer";
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        joinAsFollowerAndReceiveAssignment(consumerId, coordinator, Collections.singletonList(t1p));
        subscriptions.seek(t1p, 100);
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        time.sleep(autoCommitIntervalMs);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(client.hasPendingResponses());
    }

    @Test
    public void testAutoCommitRetryBackoff() {
        final String consumerId = "consumer";
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        joinAsFollowerAndReceiveAssignment(consumerId, coordinator, Collections.singletonList(t1p));
        subscriptions.seek(t1p, 100);
        time.sleep(autoCommitIntervalMs);
        // Send an offset commit, but let it fail with a retriable error
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NOT_COORDINATOR);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertTrue(coordinator.coordinatorUnknown());
        // After the disconnect, we should rediscover the coordinator
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.poll(timer(Long.MAX_VALUE));
        subscriptions.seek(t1p, 200);
        // Until the retry backoff has expired, we should not retry the offset commit
        time.sleep(((retryBackoffMs) / 2));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(0, client.inFlightRequestCount());
        // Once the backoff expires, we should retry
        time.sleep(((retryBackoffMs) / 2));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(1, client.inFlightRequestCount());
        respondToOffsetCommitRequest(Collections.singletonMap(t1p, 200L), NONE);
    }

    @Test
    public void testAutoCommitAwaitsInterval() {
        final String consumerId = "consumer";
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        joinAsFollowerAndReceiveAssignment(consumerId, coordinator, Collections.singletonList(t1p));
        subscriptions.seek(t1p, 100);
        time.sleep(autoCommitIntervalMs);
        // Send the offset commit request, but do not respond
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(1, client.inFlightRequestCount());
        time.sleep(((autoCommitIntervalMs) / 2));
        // Ensure that no additional offset commit is sent
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(1, client.inFlightRequestCount());
        respondToOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(0, client.inFlightRequestCount());
        subscriptions.seek(t1p, 200);
        // If we poll again before the auto-commit interval, there should be no new sends
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(0, client.inFlightRequestCount());
        // After the remainder of the interval passes, we send a new offset commit
        time.sleep(((autoCommitIntervalMs) / 2));
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertEquals(1, client.inFlightRequestCount());
        respondToOffsetCommitRequest(Collections.singletonMap(t1p, 200L), NONE);
    }

    @Test
    public void testAutoCommitDynamicAssignmentRebalance() {
        final String consumerId = "consumer";
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // haven't joined, so should not cause a commit
        time.sleep(autoCommitIntervalMs);
        consumerClient.poll(time.timer(0));
        client.prepareResponse(joinGroupFollowerResponse(1, consumerId, "leader", NONE));
        client.prepareResponse(syncGroupResponse(Collections.singletonList(t1p), NONE));
        coordinator.joinGroupIfNeeded(timer(Long.MAX_VALUE));
        subscriptions.seek(t1p, 100);
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        time.sleep(autoCommitIntervalMs);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(client.hasPendingResponses());
    }

    @Test
    public void testAutoCommitManualAssignment() {
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.assignFromUser(Collections.singleton(t1p));
        subscriptions.seek(t1p, 100);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        time.sleep(autoCommitIntervalMs);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(client.hasPendingResponses());
    }

    @Test
    public void testAutoCommitManualAssignmentCoordinatorUnknown() {
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.assignFromUser(Collections.singleton(t1p));
        subscriptions.seek(t1p, 100);
        // no commit initially since coordinator is unknown
        consumerClient.poll(time.timer(0));
        time.sleep(autoCommitIntervalMs);
        consumerClient.poll(time.timer(0));
        // now find the coordinator
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // sleep only for the retry backoff
        time.sleep(retryBackoffMs);
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        coordinator.poll(timer(Long.MAX_VALUE));
        Assert.assertFalse(client.hasPendingResponses());
    }

    @Test
    public void testCommitOffsetMetadata() {
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        AtomicBoolean success = new AtomicBoolean(false);
        Map<TopicPartition, OffsetAndMetadata> offsets = Collections.singletonMap(t1p, new OffsetAndMetadata(100L, "hello"));
        coordinator.commitOffsetsAsync(offsets, callback(offsets, success));
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertTrue(success.get());
    }

    @Test
    public void testCommitOffsetAsyncWithDefaultCallback() {
        int invokedBeforeTest = mockOffsetCommitCallback.invoked;
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), mockOffsetCommitCallback);
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertEquals((invokedBeforeTest + 1), mockOffsetCommitCallback.invoked);
        Assert.assertNull(mockOffsetCommitCallback.exception);
    }

    @Test
    public void testCommitAfterLeaveGroup() {
        // enable auto-assignment
        subscriptions.subscribe(Collections.singleton(topic1), rebalanceListener);
        joinAsFollowerAndReceiveAssignment("consumer", coordinator, Collections.singletonList(t1p));
        // now switch to manual assignment
        client.prepareResponse(new org.apache.kafka.common.requests.LeaveGroupResponse(new LeaveGroupResponseData().setErrorCode(NONE.code())));
        subscriptions.unsubscribe();
        coordinator.maybeLeaveGroup();
        subscriptions.assignFromUser(Collections.singleton(t1p));
        // the client should not reuse generation/memberId from auto-subscribed generation
        client.prepareResponse(new MockClient.RequestMatcher() {
            @Override
            public boolean matches(AbstractRequest body) {
                OffsetCommitRequest commitRequest = ((OffsetCommitRequest) (body));
                return (commitRequest.memberId().equals(DEFAULT_MEMBER_ID)) && ((commitRequest.generationId()) == (OffsetCommitRequest.DEFAULT_GENERATION_ID));
            }
        }, offsetCommitResponse(Collections.singletonMap(t1p, NONE)));
        AtomicBoolean success = new AtomicBoolean(false);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), callback(success));
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertTrue(success.get());
    }

    @Test
    public void testCommitOffsetAsyncFailedWithDefaultCallback() {
        int invokedBeforeTest = mockOffsetCommitCallback.invoked;
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), COORDINATOR_NOT_AVAILABLE);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), mockOffsetCommitCallback);
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertEquals((invokedBeforeTest + 1), mockOffsetCommitCallback.invoked);
        Assert.assertTrue(((mockOffsetCommitCallback.exception) instanceof RetriableCommitFailedException));
    }

    @Test
    public void testCommitOffsetAsyncCoordinatorNotAvailable() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // async commit with coordinator not available
        ConsumerCoordinatorTest.MockCommitCallback cb = new ConsumerCoordinatorTest.MockCommitCallback();
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), COORDINATOR_NOT_AVAILABLE);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), cb);
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertTrue(coordinator.coordinatorUnknown());
        Assert.assertEquals(1, cb.invoked);
        Assert.assertTrue(((cb.exception) instanceof RetriableCommitFailedException));
    }

    @Test
    public void testCommitOffsetAsyncNotCoordinator() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // async commit with not coordinator
        ConsumerCoordinatorTest.MockCommitCallback cb = new ConsumerCoordinatorTest.MockCommitCallback();
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), COORDINATOR_NOT_AVAILABLE);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), cb);
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertTrue(coordinator.coordinatorUnknown());
        Assert.assertEquals(1, cb.invoked);
        Assert.assertTrue(((cb.exception) instanceof RetriableCommitFailedException));
    }

    @Test
    public void testCommitOffsetAsyncDisconnected() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // async commit with coordinator disconnected
        ConsumerCoordinatorTest.MockCommitCallback cb = new ConsumerCoordinatorTest.MockCommitCallback();
        prepareOffsetCommitRequestDisconnect(Collections.singletonMap(t1p, 100L));
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), cb);
        coordinator.invokeCompletedOffsetCommitCallbacks();
        Assert.assertTrue(coordinator.coordinatorUnknown());
        Assert.assertEquals(1, cb.invoked);
        Assert.assertTrue(((cb.exception) instanceof RetriableCommitFailedException));
    }

    @Test
    public void testCommitOffsetSyncNotCoordinator() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // sync commit with coordinator disconnected (should connect, get metadata, and then submit the commit request)
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NOT_COORDINATOR);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), timer(Long.MAX_VALUE));
    }

    @Test
    public void testCommitOffsetSyncCoordinatorNotAvailable() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // sync commit with coordinator disconnected (should connect, get metadata, and then submit the commit request)
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), COORDINATOR_NOT_AVAILABLE);
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), timer(Long.MAX_VALUE));
    }

    @Test
    public void testCommitOffsetSyncCoordinatorDisconnected() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // sync commit with coordinator disconnected (should connect, get metadata, and then submit the commit request)
        prepareOffsetCommitRequestDisconnect(Collections.singletonMap(t1p, 100L));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), timer(Long.MAX_VALUE));
    }

    @Test
    public void testAsyncCommitCallbacksInvokedPriorToSyncCommitCompletion() throws Exception {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        final List<OffsetAndMetadata> committedOffsets = Collections.synchronizedList(new ArrayList<OffsetAndMetadata>());
        final OffsetAndMetadata firstOffset = new OffsetAndMetadata(0L);
        final OffsetAndMetadata secondOffset = new OffsetAndMetadata(1L);
        coordinator.commitOffsetsAsync(Collections.singletonMap(t1p, firstOffset), new OffsetCommitCallback() {
            @Override
            public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                committedOffsets.add(firstOffset);
            }
        });
        // Do a synchronous commit in the background so that we can send both responses at the same time
        Thread thread = new Thread() {
            @Override
            public void run() {
                coordinator.commitOffsetsSync(Collections.singletonMap(t1p, secondOffset), time.timer(10000));
                committedOffsets.add(secondOffset);
            }
        };
        thread.start();
        client.waitForRequests(2, 5000);
        respondToOffsetCommitRequest(Collections.singletonMap(t1p, firstOffset.offset()), NONE);
        respondToOffsetCommitRequest(Collections.singletonMap(t1p, secondOffset.offset()), NONE);
        thread.join();
        Assert.assertEquals(Arrays.asList(firstOffset, secondOffset), committedOffsets);
    }

    @Test
    public void testRetryCommitUnknownTopicOrPartition() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        client.prepareResponse(offsetCommitResponse(Collections.singletonMap(t1p, UNKNOWN_TOPIC_OR_PARTITION)));
        client.prepareResponse(offsetCommitResponse(Collections.singletonMap(t1p, NONE)));
        Assert.assertTrue(coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L, "metadata")), time.timer(10000)));
    }

    @Test(expected = OffsetMetadataTooLarge.class)
    public void testCommitOffsetMetadataTooLarge() {
        // since offset metadata is provided by the user, we have to propagate the exception so they can handle it
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), OFFSET_METADATA_TOO_LARGE);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L, "metadata")), timer(Long.MAX_VALUE));
    }

    @Test(expected = CommitFailedException.class)
    public void testCommitOffsetIllegalGeneration() {
        // we cannot retry if a rebalance occurs before the commit completed
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), ILLEGAL_GENERATION);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L, "metadata")), timer(Long.MAX_VALUE));
    }

    @Test(expected = CommitFailedException.class)
    public void testCommitOffsetUnknownMemberId() {
        // we cannot retry if a rebalance occurs before the commit completed
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), UNKNOWN_MEMBER_ID);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L, "metadata")), timer(Long.MAX_VALUE));
    }

    @Test(expected = CommitFailedException.class)
    public void testCommitOffsetRebalanceInProgress() {
        // we cannot retry if a rebalance occurs before the commit completed
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), REBALANCE_IN_PROGRESS);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L, "metadata")), timer(Long.MAX_VALUE));
    }

    @Test(expected = KafkaException.class)
    public void testCommitOffsetSyncCallbackWithNonRetriableException() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // sync commit with invalid partitions should throw if we have no callback
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), UNKNOWN_SERVER_ERROR);
        coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), timer(Long.MAX_VALUE));
    }

    @Test
    public void testCommitOffsetSyncWithoutFutureGetsCompleted() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        Assert.assertFalse(coordinator.commitOffsetsSync(Collections.singletonMap(t1p, new OffsetAndMetadata(100L)), time.timer(0)));
    }

    @Test
    public void testRefreshOffset() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(offsetFetchResponse(t1p, NONE, "", 100L));
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(Collections.emptySet(), subscriptions.missingFetchPositions());
        Assert.assertTrue(subscriptions.hasAllFetchPositions());
        Assert.assertEquals(100L, subscriptions.position(t1p).longValue());
    }

    @Test
    public void testFetchCommittedOffsets() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        long offset = 500L;
        String metadata = "blahblah";
        Optional<Integer> leaderEpoch = Optional.of(15);
        OffsetFetchResponse.PartitionData data = new OffsetFetchResponse.PartitionData(offset, leaderEpoch, metadata, Errors.NONE);
        client.prepareResponse(new OffsetFetchResponse(Errors.NONE, Collections.singletonMap(t1p, data)));
        Map<TopicPartition, OffsetAndMetadata> fetchedOffsets = coordinator.fetchCommittedOffsets(Collections.singleton(t1p), timer(Long.MAX_VALUE));
        Assert.assertNotNull(fetchedOffsets);
        Assert.assertEquals(new OffsetAndMetadata(offset, leaderEpoch, metadata), fetchedOffsets.get(t1p));
    }

    @Test
    public void testRefreshOffsetLoadInProgress() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(offsetFetchResponse(COORDINATOR_LOAD_IN_PROGRESS));
        client.prepareResponse(offsetFetchResponse(t1p, NONE, "", 100L));
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(Collections.emptySet(), subscriptions.missingFetchPositions());
        Assert.assertTrue(subscriptions.hasAllFetchPositions());
        Assert.assertEquals(100L, subscriptions.position(t1p).longValue());
    }

    @Test
    public void testRefreshOffsetsGroupNotAuthorized() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(offsetFetchResponse(GROUP_AUTHORIZATION_FAILED));
        try {
            coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
            Assert.fail("Expected group authorization error");
        } catch (GroupAuthorizationException e) {
            Assert.assertEquals(groupId, e.groupId());
        }
    }

    @Test(expected = KafkaException.class)
    public void testRefreshOffsetUnknownTopicOrPartition() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(offsetFetchResponse(t1p, UNKNOWN_TOPIC_OR_PARTITION, "", 100L));
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
    }

    @Test
    public void testRefreshOffsetNotCoordinatorForConsumer() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(offsetFetchResponse(NOT_COORDINATOR));
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        client.prepareResponse(offsetFetchResponse(t1p, NONE, "", 100L));
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(Collections.emptySet(), subscriptions.missingFetchPositions());
        Assert.assertTrue(subscriptions.hasAllFetchPositions());
        Assert.assertEquals(100L, subscriptions.position(t1p).longValue());
    }

    @Test
    public void testRefreshOffsetWithNoFetchableOffsets() {
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        subscriptions.assignFromUser(Collections.singleton(t1p));
        client.prepareResponse(offsetFetchResponse(t1p, NONE, "", (-1L)));
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.missingFetchPositions());
        Assert.assertEquals(Collections.emptySet(), subscriptions.partitionsNeedingReset(time.milliseconds()));
        Assert.assertFalse(subscriptions.hasAllFetchPositions());
        Assert.assertEquals(null, subscriptions.position(t1p));
    }

    @Test
    public void testNoCoordinatorDiscoveryIfPositionsKnown() {
        Assert.assertTrue(coordinator.coordinatorUnknown());
        subscriptions.assignFromUser(Collections.singleton(t1p));
        subscriptions.seek(t1p, 500L);
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(Collections.emptySet(), subscriptions.missingFetchPositions());
        Assert.assertTrue(subscriptions.hasAllFetchPositions());
        Assert.assertEquals(500L, subscriptions.position(t1p).longValue());
        Assert.assertTrue(coordinator.coordinatorUnknown());
    }

    @Test
    public void testNoCoordinatorDiscoveryIfPartitionAwaitingReset() {
        Assert.assertTrue(coordinator.coordinatorUnknown());
        subscriptions.assignFromUser(Collections.singleton(t1p));
        subscriptions.requestOffsetReset(t1p, EARLIEST);
        coordinator.refreshCommittedOffsetsIfNeeded(timer(Long.MAX_VALUE));
        Assert.assertEquals(Collections.emptySet(), subscriptions.missingFetchPositions());
        Assert.assertFalse(subscriptions.hasAllFetchPositions());
        Assert.assertEquals(Collections.singleton(t1p), subscriptions.partitionsNeedingReset(time.milliseconds()));
        Assert.assertEquals(EARLIEST, subscriptions.resetStrategy(t1p));
        Assert.assertTrue(coordinator.coordinatorUnknown());
    }

    @Test
    public void testAuthenticationFailureInEnsureActiveGroup() {
        client.createPendingAuthenticationError(node, 300);
        try {
            coordinator.ensureActiveGroup();
            Assert.fail("Expected an authentication error.");
        } catch (AuthenticationException e) {
            // OK
        }
    }

    @Test
    public void testProtocolMetadataOrder() {
        RoundRobinAssignor roundRobin = new RoundRobinAssignor();
        RangeAssignor range = new RangeAssignor();
        try (Metrics metrics = new Metrics(time)) {
            ConsumerCoordinator coordinator = buildCoordinator(metrics, Arrays.<PartitionAssignor>asList(roundRobin, range), false, true);
            List<ProtocolMetadata> metadata = coordinator.metadata();
            Assert.assertEquals(2, metadata.size());
            Assert.assertEquals(roundRobin.name(), metadata.get(0).name());
            Assert.assertEquals(range.name(), metadata.get(1).name());
        }
        try (Metrics metrics = new Metrics(time)) {
            ConsumerCoordinator coordinator = buildCoordinator(metrics, Arrays.<PartitionAssignor>asList(range, roundRobin), false, true);
            List<ProtocolMetadata> metadata = coordinator.metadata();
            Assert.assertEquals(2, metadata.size());
            Assert.assertEquals(range.name(), metadata.get(0).name());
            Assert.assertEquals(roundRobin.name(), metadata.get(1).name());
        }
    }

    @Test
    public void testThreadSafeAssignedPartitionsMetric() throws Exception {
        // Get the assigned-partitions metric
        final Metric metric = metrics.metric(new MetricName("assigned-partitions", (("consumer" + (groupId)) + "-coordinator-metrics"), "", Collections.<String, String>emptyMap()));
        // Start polling the metric in the background
        final AtomicBoolean doStop = new AtomicBoolean();
        final AtomicReference<Exception> exceptionHolder = new AtomicReference<>();
        final AtomicInteger observedSize = new AtomicInteger();
        Thread poller = new Thread() {
            @Override
            public void run() {
                // Poll as fast as possible to reproduce ConcurrentModificationException
                while (!(doStop.get())) {
                    try {
                        int size = ((Double) (metric.metricValue())).intValue();
                        observedSize.set(size);
                    } catch (Exception e) {
                        exceptionHolder.set(e);
                        return;
                    }
                } 
            }
        };
        poller.start();
        // Assign two partitions to trigger a metric change that can lead to ConcurrentModificationException
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        coordinator.ensureCoordinatorReady(timer(Long.MAX_VALUE));
        // Change the assignment several times to increase likelihood of concurrent updates
        Set<TopicPartition> partitions = new HashSet<>();
        int totalPartitions = 10;
        for (int partition = 0; partition < totalPartitions; partition++) {
            partitions.add(new TopicPartition(topic1, partition));
            subscriptions.assignFromUser(partitions);
        }
        // Wait for the metric poller to observe the final assignment change or raise an error
        TestUtils.waitForCondition(new TestCondition() {
            @Override
            public boolean conditionMet() {
                return ((observedSize.get()) == totalPartitions) || ((exceptionHolder.get()) != null);
            }
        }, "Failed to observe expected assignment change");
        doStop.set(true);
        poller.join();
        Assert.assertNull("Failed fetching the metric at least once", exceptionHolder.get());
    }

    @Test
    public void testCloseDynamicAssignment() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        gracefulCloseTest(coordinator, true);
    }

    @Test
    public void testCloseManualAssignment() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(false, true, true);
        gracefulCloseTest(coordinator, false);
    }

    @Test
    public void shouldNotLeaveGroupWhenLeaveGroupFlagIsFalse() throws Exception {
        final ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, false);
        gracefulCloseTest(coordinator, false);
    }

    @Test
    public void testCloseCoordinatorNotKnownManualAssignment() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(false, true, true);
        makeCoordinatorUnknown(coordinator, NOT_COORDINATOR);
        time.sleep(autoCommitIntervalMs);
        closeVerifyTimeout(coordinator, 1000, 1000, 1000);
    }

    @Test
    public void testCloseCoordinatorNotKnownNoCommits() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, false, true);
        makeCoordinatorUnknown(coordinator, NOT_COORDINATOR);
        closeVerifyTimeout(coordinator, 1000, 0, 0);
    }

    @Test
    public void testCloseCoordinatorNotKnownWithCommits() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        makeCoordinatorUnknown(coordinator, NOT_COORDINATOR);
        time.sleep(autoCommitIntervalMs);
        closeVerifyTimeout(coordinator, 1000, 1000, 1000);
    }

    @Test
    public void testCloseCoordinatorUnavailableNoCommits() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, false, true);
        makeCoordinatorUnknown(coordinator, COORDINATOR_NOT_AVAILABLE);
        closeVerifyTimeout(coordinator, 1000, 0, 0);
    }

    @Test
    public void testCloseTimeoutCoordinatorUnavailableForCommit() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        makeCoordinatorUnknown(coordinator, COORDINATOR_NOT_AVAILABLE);
        time.sleep(autoCommitIntervalMs);
        closeVerifyTimeout(coordinator, 1000, 1000, 1000);
    }

    @Test
    public void testCloseMaxWaitCoordinatorUnavailableForCommit() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        makeCoordinatorUnknown(coordinator, COORDINATOR_NOT_AVAILABLE);
        time.sleep(autoCommitIntervalMs);
        closeVerifyTimeout(coordinator, Long.MAX_VALUE, requestTimeoutMs, requestTimeoutMs);
    }

    @Test
    public void testCloseNoResponseForCommit() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        time.sleep(autoCommitIntervalMs);
        closeVerifyTimeout(coordinator, Long.MAX_VALUE, requestTimeoutMs, requestTimeoutMs);
    }

    @Test
    public void testCloseNoResponseForLeaveGroup() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, false, true);
        closeVerifyTimeout(coordinator, Long.MAX_VALUE, requestTimeoutMs, requestTimeoutMs);
    }

    @Test
    public void testCloseNoWait() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        time.sleep(autoCommitIntervalMs);
        closeVerifyTimeout(coordinator, 0, 0, 0);
    }

    @Test
    public void testHeartbeatThreadClose() throws Exception {
        ConsumerCoordinator coordinator = prepareCoordinatorForCloseTest(true, true, true);
        coordinator.ensureActiveGroup();
        time.sleep(((heartbeatIntervalMs) + 100));
        Thread.yield();// Give heartbeat thread a chance to attempt heartbeat

        closeVerifyTimeout(coordinator, Long.MAX_VALUE, requestTimeoutMs, requestTimeoutMs);
        Thread[] threads = new Thread[Thread.activeCount()];
        int threadCount = Thread.enumerate(threads);
        for (int i = 0; i < threadCount; i++)
            Assert.assertFalse("Heartbeat thread active after close", threads[i].getName().contains(groupId));

    }

    @Test
    public void testAutoCommitAfterCoordinatorBackToService() {
        ConsumerCoordinator coordinator = buildCoordinator(new Metrics(), assignors, true, true);
        subscriptions.assignFromUser(Collections.singleton(t1p));
        subscriptions.seek(t1p, 100L);
        coordinator.markCoordinatorUnknown();
        Assert.assertTrue(coordinator.coordinatorUnknown());
        client.prepareResponse(groupCoordinatorResponse(node, NONE));
        prepareOffsetCommitRequest(Collections.singletonMap(t1p, 100L), NONE);
        // async commit offset should find coordinator
        time.sleep(autoCommitIntervalMs);// sleep for a while to ensure auto commit does happen

        coordinator.maybeAutoCommitOffsetsAsync(time.milliseconds());
        Assert.assertFalse(coordinator.coordinatorUnknown());
        Assert.assertEquals(100L, subscriptions.position(t1p).longValue());
    }

    private static class MockCommitCallback implements OffsetCommitCallback {
        public int invoked = 0;

        public Exception exception = null;

        @Override
        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
            (invoked)++;
            this.exception = exception;
        }
    }

    private static class MockRebalanceListener implements ConsumerRebalanceListener {
        public Collection<TopicPartition> revoked;

        public Collection<TopicPartition> assigned;

        public int revokedCount = 0;

        public int assignedCount = 0;

        @Override
        public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
            this.assigned = partitions;
            (assignedCount)++;
        }

        @Override
        public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
            this.revoked = partitions;
            (revokedCount)++;
        }
    }
}

