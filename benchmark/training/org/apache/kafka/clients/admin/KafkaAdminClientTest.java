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
package org.apache.kafka.clients.admin;


import AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import AdminClientConfig.CLIENT_ID_CONFIG;
import AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG;
import AdminClientConfig.RETRIES_CONFIG;
import AdminClientConfig.RETRY_BACKOFF_MS_CONFIG;
import ConfigResource.Type;
import ConsumerProtocol.PROTOCOL_TYPE;
import DeleteRecordsResponse.PartitionResponse;
import Errors.NONE;
import Errors.NOT_CONTROLLER;
import Errors.TOPIC_DELETION_DISABLED;
import Errors.UNKNOWN_TOPIC_OR_PARTITION;
import KafkaAdminClient.Call;
import KafkaAdminClient.TimeoutProcessor;
import KafkaAdminClient.TimeoutProcessorFactory;
import MetadataResponse.PartitionMetadata;
import MetadataResponse.TopicMetadata;
import OffsetFetchResponse.PartitionData;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.kafka.clients.AbstractRequest;
import org.apache.kafka.clients.MockClient;
import org.apache.kafka.clients.NodeApiVersions;
import org.apache.kafka.clients.admin.DeleteAclsResult.FilterResults;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.internals.ConsumerProtocol;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.acl.AclBinding;
import org.apache.kafka.common.acl.AclBindingFilter;
import org.apache.kafka.common.acl.AclOperation;
import org.apache.kafka.common.acl.AclPermissionType;
import org.apache.kafka.common.errors.ClusterAuthorizationException;
import org.apache.kafka.common.errors.GroupAuthorizationException;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.LeaderNotAvailableException;
import org.apache.kafka.common.errors.NotLeaderForPartitionException;
import org.apache.kafka.common.errors.OffsetOutOfRangeException;
import org.apache.kafka.common.errors.SaslAuthenticationException;
import org.apache.kafka.common.errors.SecurityDisabledException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicDeletionDisabledException;
import org.apache.kafka.common.errors.UnknownServerException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.apache.kafka.common.message.DescribeGroupsResponseData;
import org.apache.kafka.common.message.ElectPreferredLeadersResponseData;
import org.apache.kafka.common.message.ElectPreferredLeadersResponseData.PartitionResult;
import org.apache.kafka.common.message.ElectPreferredLeadersResponseData.ReplicaElectionResult;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.ApiError;
import org.apache.kafka.common.requests.CreateAclsResponse.AclCreationResponse;
import org.apache.kafka.common.requests.CreateTopicsRequest;
import org.apache.kafka.common.requests.DeleteAclsResponse.AclDeletionResult;
import org.apache.kafka.common.requests.DeleteRecordsResponse;
import org.apache.kafka.common.requests.DeleteTopicsRequest;
import org.apache.kafka.common.requests.DescribeGroupsResponse;
import org.apache.kafka.common.requests.ListGroupsResponse;
import org.apache.kafka.common.requests.MetadataRequest;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.requests.OffsetFetchResponse;
import org.apache.kafka.common.resource.PatternType;
import org.apache.kafka.common.resource.ResourceType;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.TestCondition;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static AdminClientConfig.RETRIES_CONFIG;


/**
 * A unit test for KafkaAdminClient.
 *
 * See AdminClientIntegrationTest for an integration test.
 */
public class KafkaAdminClientTest {
    private static final Logger log = LoggerFactory.getLogger(KafkaAdminClientTest.class);

    @Rule
    public final Timeout globalTimeout = Timeout.millis(120000);

    @Test
    public void testGetOrCreateListValue() {
        Map<String, List<String>> map = new HashMap<>();
        List<String> fooList = KafkaAdminClient.getOrCreateListValue(map, "foo");
        Assert.assertNotNull(fooList);
        fooList.add("a");
        fooList.add("b");
        List<String> fooList2 = KafkaAdminClient.getOrCreateListValue(map, "foo");
        Assert.assertEquals(fooList, fooList2);
        Assert.assertTrue(fooList2.contains("a"));
        Assert.assertTrue(fooList2.contains("b"));
        List<String> barList = KafkaAdminClient.getOrCreateListValue(map, "bar");
        Assert.assertNotNull(barList);
        Assert.assertTrue(barList.isEmpty());
    }

    @Test
    public void testCalcTimeoutMsRemainingAsInt() {
        Assert.assertEquals(0, KafkaAdminClient.calcTimeoutMsRemainingAsInt(1000, 1000));
        Assert.assertEquals(100, KafkaAdminClient.calcTimeoutMsRemainingAsInt(1000, 1100));
        Assert.assertEquals(Integer.MAX_VALUE, KafkaAdminClient.calcTimeoutMsRemainingAsInt(0, Long.MAX_VALUE));
        Assert.assertEquals(Integer.MIN_VALUE, KafkaAdminClient.calcTimeoutMsRemainingAsInt(Long.MAX_VALUE, 0));
    }

    @Test
    public void testPrettyPrintException() {
        Assert.assertEquals("Null exception.", KafkaAdminClient.prettyPrintException(null));
        Assert.assertEquals("TimeoutException", KafkaAdminClient.prettyPrintException(new TimeoutException()));
        Assert.assertEquals("TimeoutException: The foobar timed out.", KafkaAdminClient.prettyPrintException(new TimeoutException("The foobar timed out.")));
    }

    @Test
    public void testGenerateClientId() {
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            String id = KafkaAdminClient.generateClientId(KafkaAdminClientTest.newConfMap(CLIENT_ID_CONFIG, ""));
            Assert.assertTrue(("Got duplicate id " + id), (!(ids.contains(id))));
            ids.add(id);
        }
        Assert.assertEquals("myCustomId", KafkaAdminClient.generateClientId(KafkaAdminClientTest.newConfMap(CLIENT_ID_CONFIG, "myCustomId")));
    }

    @Test
    public void testCloseAdminClient() {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
        }
    }

    /**
     * Test that the client properly times out when we don't receive any metadata.
     */
    @Test
    public void testTimeoutWithoutMetadata() throws Exception {
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(Time.SYSTEM, KafkaAdminClientTest.mockBootstrapCluster(), KafkaAdminClientTest.newStrMap(REQUEST_TIMEOUT_MS_CONFIG, "10"))) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(1000)).all();
            TestUtils.assertFutureError(future, TimeoutException.class);
        }
    }

    @Test
    public void testConnectionFailureOnMetadataUpdate() throws Exception {
        // This tests the scenario in which we successfully connect to the bootstrap server, but
        // the server disconnects before sending the full response
        Cluster cluster = KafkaAdminClientTest.mockBootstrapCluster();
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(Time.SYSTEM, cluster)) {
            Cluster discoveredCluster = KafkaAdminClientTest.mockCluster(0);
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(( request) -> request instanceof MetadataRequest, null, true);
            env.kafkaClient().prepareResponse(( request) -> request instanceof MetadataRequest, MetadataResponse.prepareResponse(discoveredCluster.nodes(), discoveredCluster.clusterResource().clusterId(), 1, Collections.emptyList()));
            env.kafkaClient().prepareResponse(( body) -> body instanceof CreateTopicsRequest, KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(10000)).all();
            future.get();
        }
    }

    @Test
    public void testUnreachableBootstrapServer() throws Exception {
        // This tests the scenario in which the bootstrap server is unreachable for a short while,
        // which prevents AdminClient from being able to send the initial metadata request
        Cluster cluster = Cluster.bootstrap(Collections.singletonList(new InetSocketAddress("localhost", 8121)));
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(Time.SYSTEM, cluster)) {
            Cluster discoveredCluster = KafkaAdminClientTest.mockCluster(0);
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().setUnreachable(cluster.nodes().get(0), 200);
            env.kafkaClient().prepareResponse(( body) -> body instanceof MetadataRequest, MetadataResponse.prepareResponse(discoveredCluster.nodes(), discoveredCluster.clusterResource().clusterId(), 1, Collections.emptyList()));
            env.kafkaClient().prepareResponse(( body) -> body instanceof CreateTopicsRequest, KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(10000)).all();
            future.get();
        }
    }

    /**
     * Test that we propagate exceptions encountered when fetching metadata.
     */
    @Test
    public void testPropagatedMetadataFetchException() throws Exception {
        Cluster cluster = KafkaAdminClientTest.mockCluster(0);
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(Time.SYSTEM, cluster, KafkaAdminClientTest.newStrMap(BOOTSTRAP_SERVERS_CONFIG, "localhost:8121", REQUEST_TIMEOUT_MS_CONFIG, "10"))) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().createPendingAuthenticationError(cluster.nodeById(0), TimeUnit.DAYS.toMillis(1));
            env.kafkaClient().prepareResponse(KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(1000)).all();
            TestUtils.assertFutureError(future, SaslAuthenticationException.class);
        }
    }

    @Test
    public void testCreateTopics() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(( body) -> body instanceof CreateTopicsRequest, KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(10000)).all();
            future.get();
        }
    }

    @Test
    public void testCreateTopicsRetryBackoff() throws Exception {
        Cluster cluster = KafkaAdminClientTest.mockCluster(0);
        MockTime time = new MockTime();
        int retryBackoff = 100;
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(time, cluster, KafkaAdminClientTest.newStrMap(RETRY_BACKOFF_MS_CONFIG, ("" + retryBackoff)))) {
            MockClient mockClient = env.kafkaClient();
            mockClient.setNodeApiVersions(NodeApiVersions.create());
            AtomicLong firstAttemptTime = new AtomicLong(0);
            AtomicLong secondAttemptTime = new AtomicLong(0);
            mockClient.prepareResponse(( body) -> {
                firstAttemptTime.set(time.milliseconds());
                return body instanceof CreateTopicsRequest;
            }, null, true);
            mockClient.prepareResponse(( body) -> {
                secondAttemptTime.set(time.milliseconds());
                return body instanceof CreateTopicsRequest;
            }, KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(10000)).all();
            // Wait until the first attempt has failed, then advance the time
            TestUtils.waitForCondition(() -> (mockClient.numAwaitingResponses()) == 1, "Failed awaiting CreateTopics first request failure");
            // Wait until the retry call added to the queue in AdminClient
            TestUtils.waitForCondition(() -> (numPendingCalls()) == 1, "Failed to add retry CreateTopics call");
            time.sleep(retryBackoff);
            future.get();
            long actualRetryBackoff = (secondAttemptTime.get()) - (firstAttemptTime.get());
            Assert.assertEquals("CreateTopics retry did not await expected backoff", retryBackoff, actualRetryBackoff);
        }
    }

    @Test
    public void testCreateTopicsHandleNotControllerException() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponseFrom(KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NOT_CONTROLLER), env.cluster().nodeById(0));
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(env.cluster().nodes(), env.cluster().clusterResource().clusterId(), 1, Collections.<MetadataResponse.TopicMetadata>emptyList()));
            env.kafkaClient().prepareResponseFrom(KafkaAdminClientTest.prepareCreateTopicsResponse("myTopic", NONE), env.cluster().nodeById(1));
            KafkaFuture<Void> future = env.adminClient().createTopics(Collections.singleton(new NewTopic("myTopic", Collections.singletonMap(0, Arrays.asList(0, 1, 2)))), new CreateTopicsOptions().timeoutMs(10000)).all();
            future.get();
        }
    }

    @Test
    public void testDeleteTopics() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(( body) -> body instanceof DeleteTopicsRequest, new org.apache.kafka.common.requests.DeleteTopicsResponse(Collections.singletonMap("myTopic", NONE)));
            KafkaFuture<Void> future = env.adminClient().deleteTopics(Collections.singletonList("myTopic"), new DeleteTopicsOptions()).all();
            future.get();
            env.kafkaClient().prepareResponse(( body) -> body instanceof DeleteTopicsRequest, new org.apache.kafka.common.requests.DeleteTopicsResponse(Collections.singletonMap("myTopic", TOPIC_DELETION_DISABLED)));
            future = env.adminClient().deleteTopics(Collections.singletonList("myTopic"), new DeleteTopicsOptions()).all();
            TestUtils.assertFutureError(future, TopicDeletionDisabledException.class);
            env.kafkaClient().prepareResponse(( body) -> body instanceof DeleteTopicsRequest, new org.apache.kafka.common.requests.DeleteTopicsResponse(Collections.singletonMap("myTopic", UNKNOWN_TOPIC_OR_PARTITION)));
            future = env.adminClient().deleteTopics(Collections.singletonList("myTopic"), new DeleteTopicsOptions()).all();
            TestUtils.assertFutureError(future, UnknownTopicOrPartitionException.class);
        }
    }

    @Test
    public void testInvalidTopicNames() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            List<String> sillyTopicNames = Arrays.asList("", null);
            Map<String, KafkaFuture<Void>> deleteFutures = env.adminClient().deleteTopics(sillyTopicNames).values();
            for (String sillyTopicName : sillyTopicNames) {
                TestUtils.assertFutureError(deleteFutures.get(sillyTopicName), InvalidTopicException.class);
            }
            Assert.assertEquals(0, env.kafkaClient().inFlightRequestCount());
            Map<String, KafkaFuture<TopicDescription>> describeFutures = env.adminClient().describeTopics(sillyTopicNames).values();
            for (String sillyTopicName : sillyTopicNames) {
                TestUtils.assertFutureError(describeFutures.get(sillyTopicName), InvalidTopicException.class);
            }
            Assert.assertEquals(0, env.kafkaClient().inFlightRequestCount());
            List<NewTopic> newTopics = new ArrayList<>();
            for (String sillyTopicName : sillyTopicNames) {
                newTopics.add(new NewTopic(sillyTopicName, 1, ((short) (1))));
            }
            Map<String, KafkaFuture<Void>> createFutures = env.adminClient().createTopics(newTopics).values();
            for (String sillyTopicName : sillyTopicNames) {
                TestUtils.assertFutureError(createFutures.get(sillyTopicName), InvalidTopicException.class);
            }
            Assert.assertEquals(0, env.kafkaClient().inFlightRequestCount());
        }
    }

    @Test
    public void testMetadataRetries() throws Exception {
        // We should continue retrying on metadata update failures in spite of retry configuration
        String topic = "topic";
        Cluster bootstrapCluster = Cluster.bootstrap(Collections.singletonList(new InetSocketAddress("localhost", 9999)));
        Cluster initializedCluster = KafkaAdminClientTest.mockCluster(0);
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(Time.SYSTEM, bootstrapCluster, KafkaAdminClientTest.newStrMap(BOOTSTRAP_SERVERS_CONFIG, "localhost:9999", REQUEST_TIMEOUT_MS_CONFIG, "10000000", RETRIES_CONFIG, "0"))) {
            // The first request fails with a disconnect
            env.kafkaClient().prepareResponse(null, true);
            // The next one succeeds and gives us the controller id
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(initializedCluster.nodes(), initializedCluster.clusterResource().clusterId(), initializedCluster.controller().id(), Collections.emptyList()));
            // Then we respond to the DescribeTopic request
            Node leader = initializedCluster.nodes().get(0);
            MetadataResponse.PartitionMetadata partitionMetadata = new MetadataResponse.PartitionMetadata(Errors.NONE, 0, leader, Optional.of(10), Collections.singletonList(leader), Collections.singletonList(leader), Collections.singletonList(leader));
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(initializedCluster.nodes(), initializedCluster.clusterResource().clusterId(), 1, Collections.singletonList(new MetadataResponse.TopicMetadata(Errors.NONE, topic, false, Collections.singletonList(partitionMetadata)))));
            DescribeTopicsResult result = env.adminClient().describeTopics(Collections.singleton(topic));
            Map<String, TopicDescription> topicDescriptions = result.all().get();
            Assert.assertEquals(leader, topicDescriptions.get(topic).partitions().get(0).leader());
        }
    }

    @Test
    public void testAdminClientApisAuthenticationFailure() throws Exception {
        Cluster cluster = KafkaAdminClientTest.mockBootstrapCluster();
        try (final AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(Time.SYSTEM, cluster, KafkaAdminClientTest.newStrMap(REQUEST_TIMEOUT_MS_CONFIG, "1000"))) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().createPendingAuthenticationError(cluster.nodes().get(0), TimeUnit.DAYS.toMillis(1));
            callAdminClientApisAndExpectAnAuthenticationError(env);
        }
    }

    private static final AclBinding ACL1 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic3", PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntry("User:ANONYMOUS", "*", AclOperation.DESCRIBE, AclPermissionType.ALLOW));

    private static final AclBinding ACL2 = new AclBinding(new org.apache.kafka.common.resource.ResourcePattern(ResourceType.TOPIC, "mytopic4", PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntry("User:ANONYMOUS", "*", AclOperation.DESCRIBE, AclPermissionType.DENY));

    private static final AclBindingFilter FILTER1 = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.ANY, null, PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:ANONYMOUS", null, AclOperation.ANY, AclPermissionType.ANY));

    private static final AclBindingFilter FILTER2 = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.ANY, null, PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:bob", null, AclOperation.ANY, AclPermissionType.ANY));

    private static final AclBindingFilter UNKNOWN_FILTER = new AclBindingFilter(new org.apache.kafka.common.resource.ResourcePatternFilter(ResourceType.UNKNOWN, null, PatternType.LITERAL), new org.apache.kafka.common.acl.AccessControlEntryFilter("User:bob", null, AclOperation.ANY, AclPermissionType.ANY));

    @Test
    public void testDescribeAcls() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Test a call where we get back ACL1 and ACL2.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DescribeAclsResponse(0, ApiError.NONE, Arrays.asList(KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2)));
            KafkaAdminClientTest.assertCollectionIs(env.adminClient().describeAcls(KafkaAdminClientTest.FILTER1).values().get(), KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2);
            // Test a call where we get back no results.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DescribeAclsResponse(0, ApiError.NONE, Collections.<AclBinding>emptySet()));
            Assert.assertTrue(env.adminClient().describeAcls(KafkaAdminClientTest.FILTER2).values().get().isEmpty());
            // Test a call where we get back an error.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DescribeAclsResponse(0, new ApiError(Errors.SECURITY_DISABLED, "Security is disabled"), Collections.<AclBinding>emptySet()));
            TestUtils.assertFutureError(env.adminClient().describeAcls(KafkaAdminClientTest.FILTER2).values(), SecurityDisabledException.class);
            // Test a call where we supply an invalid filter.
            TestUtils.assertFutureError(env.adminClient().describeAcls(KafkaAdminClientTest.UNKNOWN_FILTER).values(), InvalidRequestException.class);
        }
    }

    @Test
    public void testCreateAcls() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Test a call where we successfully create two ACLs.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.CreateAclsResponse(0, Arrays.asList(new AclCreationResponse(ApiError.NONE), new AclCreationResponse(ApiError.NONE))));
            CreateAclsResult results = env.adminClient().createAcls(Arrays.asList(KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2));
            KafkaAdminClientTest.assertCollectionIs(results.values().keySet(), KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2);
            for (KafkaFuture<Void> future : results.values().values())
                future.get();

            results.all().get();
            // Test a call where we fail to create one ACL.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.CreateAclsResponse(0, Arrays.asList(new AclCreationResponse(new ApiError(Errors.SECURITY_DISABLED, "Security is disabled")), new AclCreationResponse(ApiError.NONE))));
            results = env.adminClient().createAcls(Arrays.asList(KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2));
            KafkaAdminClientTest.assertCollectionIs(results.values().keySet(), KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2);
            TestUtils.assertFutureError(results.values().get(KafkaAdminClientTest.ACL1), SecurityDisabledException.class);
            results.values().get(KafkaAdminClientTest.ACL2).get();
            TestUtils.assertFutureError(results.all(), SecurityDisabledException.class);
        }
    }

    @Test
    public void testDeleteAcls() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Test a call where one filter has an error.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DeleteAclsResponse(0, Arrays.asList(new org.apache.kafka.common.requests.DeleteAclsResponse.AclFilterResponse(Arrays.asList(new AclDeletionResult(KafkaAdminClientTest.ACL1), new AclDeletionResult(KafkaAdminClientTest.ACL2))), new org.apache.kafka.common.requests.DeleteAclsResponse.AclFilterResponse(new ApiError(Errors.SECURITY_DISABLED, "No security"), Collections.<AclDeletionResult>emptySet()))));
            DeleteAclsResult results = env.adminClient().deleteAcls(Arrays.asList(KafkaAdminClientTest.FILTER1, KafkaAdminClientTest.FILTER2));
            Map<AclBindingFilter, KafkaFuture<FilterResults>> filterResults = results.values();
            FilterResults filter1Results = filterResults.get(KafkaAdminClientTest.FILTER1).get();
            Assert.assertEquals(null, filter1Results.values().get(0).exception());
            Assert.assertEquals(KafkaAdminClientTest.ACL1, filter1Results.values().get(0).binding());
            Assert.assertEquals(null, filter1Results.values().get(1).exception());
            Assert.assertEquals(KafkaAdminClientTest.ACL2, filter1Results.values().get(1).binding());
            TestUtils.assertFutureError(filterResults.get(KafkaAdminClientTest.FILTER2), SecurityDisabledException.class);
            TestUtils.assertFutureError(results.all(), SecurityDisabledException.class);
            // Test a call where one deletion result has an error.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DeleteAclsResponse(0, Arrays.asList(new org.apache.kafka.common.requests.DeleteAclsResponse.AclFilterResponse(Arrays.asList(new AclDeletionResult(KafkaAdminClientTest.ACL1), new AclDeletionResult(new ApiError(Errors.SECURITY_DISABLED, "No security"), KafkaAdminClientTest.ACL2))), new org.apache.kafka.common.requests.DeleteAclsResponse.AclFilterResponse(Collections.<AclDeletionResult>emptySet()))));
            results = env.adminClient().deleteAcls(Arrays.asList(KafkaAdminClientTest.FILTER1, KafkaAdminClientTest.FILTER2));
            Assert.assertTrue(results.values().get(KafkaAdminClientTest.FILTER2).get().values().isEmpty());
            TestUtils.assertFutureError(results.all(), SecurityDisabledException.class);
            // Test a call where there are no errors.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DeleteAclsResponse(0, Arrays.asList(new org.apache.kafka.common.requests.DeleteAclsResponse.AclFilterResponse(Arrays.asList(new AclDeletionResult(KafkaAdminClientTest.ACL1))), new org.apache.kafka.common.requests.DeleteAclsResponse.AclFilterResponse(Arrays.asList(new AclDeletionResult(KafkaAdminClientTest.ACL2))))));
            results = env.adminClient().deleteAcls(Arrays.asList(KafkaAdminClientTest.FILTER1, KafkaAdminClientTest.FILTER2));
            Collection<AclBinding> deleted = results.all().get();
            KafkaAdminClientTest.assertCollectionIs(deleted, KafkaAdminClientTest.ACL1, KafkaAdminClientTest.ACL2);
        }
    }

    @Test
    public void testElectPreferredLeaders() throws Exception {
        TopicPartition topic1 = new TopicPartition("topic", 0);
        TopicPartition topic2 = new TopicPartition("topic", 2);
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Test a call where one partition has an error.
            ApiError value = ApiError.fromThrowable(new ClusterAuthorizationException(null));
            ElectPreferredLeadersResponseData responseData = new ElectPreferredLeadersResponseData();
            ReplicaElectionResult r = new ReplicaElectionResult().setTopic(topic1.topic());
            r.partitionResult().add(new PartitionResult().setPartitionId(topic1.partition()).setErrorCode(ApiError.NONE.error().code()).setErrorMessage(ApiError.NONE.message()));
            r.partitionResult().add(new PartitionResult().setPartitionId(topic2.partition()).setErrorCode(value.error().code()).setErrorMessage(value.message()));
            responseData.replicaElectionResults().add(r);
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.ElectPreferredLeadersResponse(responseData));
            ElectPreferredLeadersResult results = env.adminClient().electPreferredLeaders(Arrays.asList(topic1, topic2));
            results.partitionResult(topic1).get();
            TestUtils.assertFutureError(results.partitionResult(topic2), ClusterAuthorizationException.class);
            TestUtils.assertFutureError(results.all(), ClusterAuthorizationException.class);
            // Test a call where there are no errors.
            r.partitionResult().clear();
            r.partitionResult().add(new PartitionResult().setPartitionId(topic1.partition()).setErrorCode(ApiError.NONE.error().code()).setErrorMessage(ApiError.NONE.message()));
            r.partitionResult().add(new PartitionResult().setPartitionId(topic2.partition()).setErrorCode(ApiError.NONE.error().code()).setErrorMessage(ApiError.NONE.message()));
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.ElectPreferredLeadersResponse(responseData));
            results = env.adminClient().electPreferredLeaders(Arrays.asList(topic1, topic2));
            results.partitionResult(topic1).get();
            results.partitionResult(topic2).get();
            // Now try a timeout
            results = env.adminClient().electPreferredLeaders(Arrays.asList(topic1, topic2), new ElectPreferredLeadersOptions().timeoutMs(100));
            TestUtils.assertFutureError(results.partitionResult(topic1), TimeoutException.class);
            TestUtils.assertFutureError(results.partitionResult(topic2), TimeoutException.class);
        }
    }

    @Test
    public void testDescribeConfigs() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DescribeConfigsResponse(0, Collections.singletonMap(new org.apache.kafka.common.config.ConfigResource(Type.BROKER, "0"), new org.apache.kafka.common.requests.DescribeConfigsResponse.Config(ApiError.NONE, Collections.emptySet()))));
            DescribeConfigsResult result2 = env.adminClient().describeConfigs(Collections.singleton(new org.apache.kafka.common.config.ConfigResource(Type.BROKER, "0")));
            result2.all().get();
        }
    }

    @Test
    public void testCreatePartitions() throws Exception {
        try (AdminClientUnitTestEnv env = KafkaAdminClientTest.mockClientEnv()) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            Map<String, ApiError> m = new HashMap<>();
            m.put("my_topic", ApiError.NONE);
            m.put("other_topic", ApiError.fromThrowable(new InvalidTopicException("some detailed reason")));
            // Test a call where one filter has an error.
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.CreatePartitionsResponse(0, m));
            Map<String, NewPartitions> counts = new HashMap<>();
            counts.put("my_topic", NewPartitions.increaseTo(3));
            counts.put("other_topic", NewPartitions.increaseTo(3, Arrays.asList(Arrays.asList(2), Arrays.asList(3))));
            CreatePartitionsResult results = env.adminClient().createPartitions(counts);
            Map<String, KafkaFuture<Void>> values = results.values();
            KafkaFuture<Void> myTopicResult = values.get("my_topic");
            myTopicResult.get();
            KafkaFuture<Void> otherTopicResult = values.get("other_topic");
            try {
                otherTopicResult.get();
                Assert.fail("get() should throw ExecutionException");
            } catch (ExecutionException e0) {
                Assert.assertTrue(((e0.getCause()) instanceof InvalidTopicException));
                InvalidTopicException e = ((InvalidTopicException) (e0.getCause()));
                Assert.assertEquals("some detailed reason", e.getMessage());
            }
        }
    }

    @Test
    public void testDeleteRecords() throws Exception {
        HashMap<Integer, Node> nodes = new HashMap<>();
        nodes.put(0, new Node(0, "localhost", 8121));
        List<PartitionInfo> partitionInfos = new ArrayList<>();
        partitionInfos.add(new PartitionInfo("my_topic", 0, nodes.get(0), new Node[]{ nodes.get(0) }, new Node[]{ nodes.get(0) }));
        partitionInfos.add(new PartitionInfo("my_topic", 1, nodes.get(0), new Node[]{ nodes.get(0) }, new Node[]{ nodes.get(0) }));
        partitionInfos.add(new PartitionInfo("my_topic", 2, null, new Node[]{ nodes.get(0) }, new Node[]{ nodes.get(0) }));
        partitionInfos.add(new PartitionInfo("my_topic", 3, nodes.get(0), new Node[]{ nodes.get(0) }, new Node[]{ nodes.get(0) }));
        partitionInfos.add(new PartitionInfo("my_topic", 4, nodes.get(0), new Node[]{ nodes.get(0) }, new Node[]{ nodes.get(0) }));
        Cluster cluster = new Cluster("mockClusterId", nodes.values(), partitionInfos, Collections.<String>emptySet(), Collections.<String>emptySet(), nodes.get(0));
        TopicPartition myTopicPartition0 = new TopicPartition("my_topic", 0);
        TopicPartition myTopicPartition1 = new TopicPartition("my_topic", 1);
        TopicPartition myTopicPartition2 = new TopicPartition("my_topic", 2);
        TopicPartition myTopicPartition3 = new TopicPartition("my_topic", 3);
        TopicPartition myTopicPartition4 = new TopicPartition("my_topic", 4);
        try (AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(cluster)) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            Map<TopicPartition, DeleteRecordsResponse.PartitionResponse> m = new HashMap<>();
            m.put(myTopicPartition0, new DeleteRecordsResponse.PartitionResponse(3, Errors.NONE));
            m.put(myTopicPartition1, new DeleteRecordsResponse.PartitionResponse(DeleteRecordsResponse.INVALID_LOW_WATERMARK, Errors.OFFSET_OUT_OF_RANGE));
            m.put(myTopicPartition3, new DeleteRecordsResponse.PartitionResponse(DeleteRecordsResponse.INVALID_LOW_WATERMARK, Errors.NOT_LEADER_FOR_PARTITION));
            m.put(myTopicPartition4, new DeleteRecordsResponse.PartitionResponse(DeleteRecordsResponse.INVALID_LOW_WATERMARK, Errors.UNKNOWN_TOPIC_OR_PARTITION));
            List<MetadataResponse.TopicMetadata> t = new ArrayList<>();
            List<MetadataResponse.PartitionMetadata> p = new ArrayList<>();
            p.add(new MetadataResponse.PartitionMetadata(Errors.NONE, 0, nodes.get(0), Optional.of(5), Collections.singletonList(nodes.get(0)), Collections.singletonList(nodes.get(0)), Collections.emptyList()));
            p.add(new MetadataResponse.PartitionMetadata(Errors.NONE, 1, nodes.get(0), Optional.of(5), Collections.singletonList(nodes.get(0)), Collections.singletonList(nodes.get(0)), Collections.emptyList()));
            p.add(new MetadataResponse.PartitionMetadata(Errors.LEADER_NOT_AVAILABLE, 2, null, Optional.empty(), Collections.singletonList(nodes.get(0)), Collections.singletonList(nodes.get(0)), Collections.emptyList()));
            p.add(new MetadataResponse.PartitionMetadata(Errors.NONE, 3, nodes.get(0), Optional.of(5), Collections.singletonList(nodes.get(0)), Collections.singletonList(nodes.get(0)), Collections.emptyList()));
            p.add(new MetadataResponse.PartitionMetadata(Errors.NONE, 4, nodes.get(0), Optional.of(5), Collections.singletonList(nodes.get(0)), Collections.singletonList(nodes.get(0)), Collections.emptyList()));
            t.add(new MetadataResponse.TopicMetadata(Errors.NONE, "my_topic", false, p));
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(cluster.nodes(), cluster.clusterResource().clusterId(), cluster.controller().id(), t));
            env.kafkaClient().prepareResponse(new DeleteRecordsResponse(0, m));
            Map<TopicPartition, RecordsToDelete> recordsToDelete = new HashMap<>();
            recordsToDelete.put(myTopicPartition0, RecordsToDelete.beforeOffset(3L));
            recordsToDelete.put(myTopicPartition1, RecordsToDelete.beforeOffset(10L));
            recordsToDelete.put(myTopicPartition2, RecordsToDelete.beforeOffset(10L));
            recordsToDelete.put(myTopicPartition3, RecordsToDelete.beforeOffset(10L));
            recordsToDelete.put(myTopicPartition4, RecordsToDelete.beforeOffset(10L));
            DeleteRecordsResult results = env.adminClient().deleteRecords(recordsToDelete);
            // success on records deletion for partition 0
            Map<TopicPartition, KafkaFuture<DeletedRecords>> values = results.lowWatermarks();
            KafkaFuture<DeletedRecords> myTopicPartition0Result = values.get(myTopicPartition0);
            long lowWatermark = myTopicPartition0Result.get().lowWatermark();
            Assert.assertEquals(lowWatermark, 3);
            // "offset out of range" failure on records deletion for partition 1
            KafkaFuture<DeletedRecords> myTopicPartition1Result = values.get(myTopicPartition1);
            try {
                myTopicPartition1Result.get();
                Assert.fail("get() should throw ExecutionException");
            } catch (ExecutionException e0) {
                Assert.assertTrue(((e0.getCause()) instanceof OffsetOutOfRangeException));
            }
            // "leader not available" failure on metadata request for partition 2
            KafkaFuture<DeletedRecords> myTopicPartition2Result = values.get(myTopicPartition2);
            try {
                myTopicPartition2Result.get();
                Assert.fail("get() should throw ExecutionException");
            } catch (ExecutionException e1) {
                Assert.assertTrue(((e1.getCause()) instanceof LeaderNotAvailableException));
            }
            // "not leader for partition" failure on records deletion for partition 3
            KafkaFuture<DeletedRecords> myTopicPartition3Result = values.get(myTopicPartition3);
            try {
                myTopicPartition3Result.get();
                Assert.fail("get() should throw ExecutionException");
            } catch (ExecutionException e1) {
                Assert.assertTrue(((e1.getCause()) instanceof NotLeaderForPartitionException));
            }
            // "unknown topic or partition" failure on records deletion for partition 4
            KafkaFuture<DeletedRecords> myTopicPartition4Result = values.get(myTopicPartition4);
            try {
                myTopicPartition4Result.get();
                Assert.fail("get() should throw ExecutionException");
            } catch (ExecutionException e1) {
                Assert.assertTrue(((e1.getCause()) instanceof UnknownTopicOrPartitionException));
            }
        }
    }

    @Test
    public void testListConsumerGroups() throws Exception {
        final HashMap<Integer, Node> nodes = new HashMap<>();
        Node node0 = new Node(0, "localhost", 8121);
        Node node1 = new Node(1, "localhost", 8122);
        Node node2 = new Node(2, "localhost", 8123);
        Node node3 = new Node(3, "localhost", 8124);
        nodes.put(0, node0);
        nodes.put(1, node1);
        nodes.put(2, node2);
        nodes.put(3, node3);
        final Cluster cluster = new Cluster("mockClusterId", nodes.values(), Collections.emptyList(), Collections.emptySet(), Collections.emptySet(), nodes.get(0));
        try (AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(cluster)) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Empty metadata response should be retried
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(Collections.emptyList(), env.cluster().clusterResource().clusterId(), (-1), Collections.emptyList()));
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(env.cluster().nodes(), env.cluster().clusterResource().clusterId(), env.cluster().controller().id(), Collections.emptyList()));
            env.kafkaClient().prepareResponseFrom(new ListGroupsResponse(Errors.NONE, Arrays.asList(new ListGroupsResponse.Group("group-1", ConsumerProtocol.PROTOCOL_TYPE), new ListGroupsResponse.Group("group-connect-1", "connector"))), node0);
            // handle retriable errors
            env.kafkaClient().prepareResponseFrom(new ListGroupsResponse(Errors.COORDINATOR_NOT_AVAILABLE, Collections.emptyList()), node1);
            env.kafkaClient().prepareResponseFrom(new ListGroupsResponse(Errors.COORDINATOR_LOAD_IN_PROGRESS, Collections.emptyList()), node1);
            env.kafkaClient().prepareResponseFrom(new ListGroupsResponse(Errors.NONE, Arrays.asList(new ListGroupsResponse.Group("group-2", ConsumerProtocol.PROTOCOL_TYPE), new ListGroupsResponse.Group("group-connect-2", "connector"))), node1);
            env.kafkaClient().prepareResponseFrom(new ListGroupsResponse(Errors.NONE, Arrays.asList(new ListGroupsResponse.Group("group-3", ConsumerProtocol.PROTOCOL_TYPE), new ListGroupsResponse.Group("group-connect-3", "connector"))), node2);
            // fatal error
            env.kafkaClient().prepareResponseFrom(new ListGroupsResponse(Errors.UNKNOWN_SERVER_ERROR, Collections.emptyList()), node3);
            final ListConsumerGroupsResult result = env.adminClient().listConsumerGroups();
            TestUtils.assertFutureError(result.all(), UnknownServerException.class);
            Collection<ConsumerGroupListing> listings = result.valid().get();
            Assert.assertEquals(3, listings.size());
            Set<String> groupIds = new HashSet<>();
            for (ConsumerGroupListing listing : listings) {
                groupIds.add(listing.groupId());
            }
            Assert.assertEquals(Utils.mkSet("group-1", "group-2", "group-3"), groupIds);
            Assert.assertEquals(1, result.errors().get().size());
        }
    }

    @Test
    public void testListConsumerGroupsMetadataFailure() throws Exception {
        final HashMap<Integer, Node> nodes = new HashMap<>();
        Node node0 = new Node(0, "localhost", 8121);
        Node node1 = new Node(1, "localhost", 8122);
        Node node2 = new Node(2, "localhost", 8123);
        nodes.put(0, node0);
        nodes.put(1, node1);
        nodes.put(2, node2);
        final Cluster cluster = new Cluster("mockClusterId", nodes.values(), Collections.emptyList(), Collections.emptySet(), Collections.emptySet(), nodes.get(0));
        final Time time = new MockTime();
        try (AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(time, cluster, RETRIES_CONFIG, "0")) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Empty metadata causes the request to fail since we have no list of brokers
            // to send the ListGroups requests to
            env.kafkaClient().prepareResponse(MetadataResponse.prepareResponse(Collections.emptyList(), env.cluster().clusterResource().clusterId(), (-1), Collections.emptyList()));
            final ListConsumerGroupsResult result = env.adminClient().listConsumerGroups();
            TestUtils.assertFutureError(result.all(), KafkaException.class);
        }
    }

    @Test
    public void testDescribeConsumerGroups() throws Exception {
        final HashMap<Integer, Node> nodes = new HashMap<>();
        nodes.put(0, new Node(0, "localhost", 8121));
        final Cluster cluster = new Cluster("mockClusterId", nodes.values(), Collections.<PartitionInfo>emptyList(), Collections.<String>emptySet(), Collections.<String>emptySet(), nodes.get(0));
        try (AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(cluster)) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.FindCoordinatorResponse(Errors.NONE, env.cluster().controller()));
            DescribeGroupsResponseData data = new DescribeGroupsResponseData();
            TopicPartition myTopicPartition0 = new TopicPartition("my_topic", 0);
            TopicPartition myTopicPartition1 = new TopicPartition("my_topic", 1);
            TopicPartition myTopicPartition2 = new TopicPartition("my_topic", 2);
            final List<TopicPartition> topicPartitions = new ArrayList<>();
            topicPartitions.add(0, myTopicPartition0);
            topicPartitions.add(1, myTopicPartition1);
            topicPartitions.add(2, myTopicPartition2);
            final ByteBuffer memberAssignment = ConsumerProtocol.serializeAssignment(new org.apache.kafka.clients.consumer.internals.PartitionAssignor.Assignment(topicPartitions));
            byte[] memberAssignmentBytes = new byte[memberAssignment.remaining()];
            memberAssignment.get(memberAssignmentBytes);
            data.groups().add(DescribeGroupsResponse.groupMetadata("group-0", NONE, "", PROTOCOL_TYPE, "", Arrays.asList(DescribeGroupsResponse.groupMember("0", "clientId0", "clientHost", memberAssignmentBytes, null), DescribeGroupsResponse.groupMember("1", "clientId1", "clientHost", memberAssignmentBytes, null)), Collections.emptySet()));
            data.groups().add(DescribeGroupsResponse.groupMetadata("group-connect-0", NONE, "", "connect", "", Arrays.asList(DescribeGroupsResponse.groupMember("0", "clientId0", "clientHost", memberAssignmentBytes, null), DescribeGroupsResponse.groupMember("1", "clientId1", "clientHost", memberAssignmentBytes, null)), Collections.emptySet()));
            env.kafkaClient().prepareResponse(new DescribeGroupsResponse(data));
            final DescribeConsumerGroupsResult result = env.adminClient().describeConsumerGroups(Collections.singletonList("group-0"));
            final ConsumerGroupDescription groupDescription = result.describedGroups().get("group-0").get();
            Assert.assertEquals(1, result.describedGroups().size());
            Assert.assertEquals("group-0", groupDescription.groupId());
            Assert.assertEquals(2, groupDescription.members().size());
        }
    }

    @Test
    public void testDescribeConsumerGroupOffsets() throws Exception {
        final HashMap<Integer, Node> nodes = new HashMap<>();
        nodes.put(0, new Node(0, "localhost", 8121));
        final Cluster cluster = new Cluster("mockClusterId", nodes.values(), Collections.emptyList(), Collections.emptySet(), Collections.emptySet(), nodes.get(0));
        try (AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(cluster)) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.FindCoordinatorResponse(Errors.NONE, env.cluster().controller()));
            TopicPartition myTopicPartition0 = new TopicPartition("my_topic", 0);
            TopicPartition myTopicPartition1 = new TopicPartition("my_topic", 1);
            TopicPartition myTopicPartition2 = new TopicPartition("my_topic", 2);
            final Map<TopicPartition, OffsetFetchResponse.PartitionData> responseData = new HashMap<>();
            responseData.put(myTopicPartition0, new OffsetFetchResponse.PartitionData(10, Optional.empty(), "", Errors.NONE));
            responseData.put(myTopicPartition1, new OffsetFetchResponse.PartitionData(0, Optional.empty(), "", Errors.NONE));
            responseData.put(myTopicPartition2, new OffsetFetchResponse.PartitionData(20, Optional.empty(), "", Errors.NONE));
            env.kafkaClient().prepareResponse(new OffsetFetchResponse(Errors.NONE, responseData));
            final ListConsumerGroupOffsetsResult result = env.adminClient().listConsumerGroupOffsets("group-0");
            final Map<TopicPartition, OffsetAndMetadata> partitionToOffsetAndMetadata = result.partitionsToOffsetAndMetadata().get();
            Assert.assertEquals(3, partitionToOffsetAndMetadata.size());
            Assert.assertEquals(10, partitionToOffsetAndMetadata.get(myTopicPartition0).offset());
            Assert.assertEquals(0, partitionToOffsetAndMetadata.get(myTopicPartition1).offset());
            Assert.assertEquals(20, partitionToOffsetAndMetadata.get(myTopicPartition2).offset());
        }
    }

    @Test
    public void testDeleteConsumerGroups() throws Exception {
        final HashMap<Integer, Node> nodes = new HashMap<>();
        nodes.put(0, new Node(0, "localhost", 8121));
        final Cluster cluster = new Cluster("mockClusterId", nodes.values(), Collections.<PartitionInfo>emptyList(), Collections.<String>emptySet(), Collections.<String>emptySet(), nodes.get(0));
        final List<String> groupIds = Collections.singletonList("group-0");
        try (AdminClientUnitTestEnv env = new AdminClientUnitTestEnv(cluster)) {
            env.kafkaClient().setNodeApiVersions(NodeApiVersions.create());
            // Retriable FindCoordinatorResponse errors should be retried
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.FindCoordinatorResponse(Errors.COORDINATOR_NOT_AVAILABLE, Node.noNode()));
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.FindCoordinatorResponse(Errors.COORDINATOR_LOAD_IN_PROGRESS, Node.noNode()));
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.FindCoordinatorResponse(Errors.NONE, env.cluster().controller()));
            final Map<String, Errors> response = new HashMap<>();
            response.put("group-0", NONE);
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.DeleteGroupsResponse(response));
            final DeleteConsumerGroupsResult result = env.adminClient().deleteConsumerGroups(groupIds);
            final KafkaFuture<Void> results = result.deletedGroups().get("group-0");
            Assert.assertNull(results.get());
            // should throw error for non-retriable errors
            env.kafkaClient().prepareResponse(new org.apache.kafka.common.requests.FindCoordinatorResponse(Errors.GROUP_AUTHORIZATION_FAILED, Node.noNode()));
            final DeleteConsumerGroupsResult errorResult = env.adminClient().deleteConsumerGroups(groupIds);
            TestUtils.assertFutureError(errorResult.deletedGroups().get("group-0"), GroupAuthorizationException.class);
        }
    }

    public static class FailureInjectingTimeoutProcessorFactory extends KafkaAdminClient.TimeoutProcessorFactory {
        private int numTries = 0;

        private int failuresInjected = 0;

        @Override
        public TimeoutProcessor create(long now) {
            return new KafkaAdminClientTest.FailureInjectingTimeoutProcessorFactory.FailureInjectingTimeoutProcessor(now);
        }

        synchronized boolean shouldInjectFailure() {
            (numTries)++;
            if ((numTries) == 1) {
                (failuresInjected)++;
                return true;
            }
            return false;
        }

        public synchronized int failuresInjected() {
            return failuresInjected;
        }

        public final class FailureInjectingTimeoutProcessor extends KafkaAdminClient.TimeoutProcessor {
            public FailureInjectingTimeoutProcessor(long now) {
                super(now);
            }

            boolean callHasExpired(KafkaAdminClient.Call call) {
                if ((!(call.isInternal())) && (shouldInjectFailure())) {
                    KafkaAdminClientTest.log.debug("Injecting timeout for {}.", call);
                    return true;
                } else {
                    boolean ret = super.callHasExpired(call);
                    KafkaAdminClientTest.log.debug("callHasExpired({}) = {}", call, ret);
                    return ret;
                }
            }
        }
    }
}

