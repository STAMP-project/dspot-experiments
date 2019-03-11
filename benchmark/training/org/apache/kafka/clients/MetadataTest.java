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
package org.apache.kafka.clients;


import Errors.INVALID_TOPIC_EXCEPTION;
import Errors.TOPIC_AUTHORIZATION_FAILED;
import Metadata.MetadataRequestAndVersion;
import Topic.GROUP_METADATA_TOPIC_NAME;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.apache.kafka.common.internals.ClusterResourceListeners;
import org.apache.kafka.common.protocol.Errors;
import org.apache.kafka.common.requests.MetadataResponse;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.test.MockClusterResourceListener;
import org.apache.kafka.test.Node;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class MetadataTest {
    private long refreshBackoffMs = 100;

    private long metadataExpireMs = 1000;

    private Metadata metadata = new Metadata(refreshBackoffMs, metadataExpireMs, new LogContext(), new ClusterResourceListeners());

    @Test(expected = IllegalStateException.class)
    public void testMetadataUpdateAfterClose() {
        metadata.close();
        metadata.update(MetadataTest.emptyMetadataResponse(), 1000);
    }

    @Test
    public void testTimeToNextUpdate() {
        MetadataTest.checkTimeToNextUpdate(100, 1000);
        MetadataTest.checkTimeToNextUpdate(1000, 100);
        MetadataTest.checkTimeToNextUpdate(0, 0);
        MetadataTest.checkTimeToNextUpdate(0, 100);
        MetadataTest.checkTimeToNextUpdate(100, 0);
    }

    @Test
    public void testTimeToNextUpdate_RetryBackoff() {
        long now = 10000;
        // lastRefreshMs updated to now.
        metadata.failedUpdate(now, null);
        // Backing off. Remaining time until next try should be returned.
        Assert.assertEquals(refreshBackoffMs, metadata.timeToNextUpdate(now));
        // Even though metadata update requested explicitly, still respects backoff.
        metadata.requestUpdate();
        Assert.assertEquals(refreshBackoffMs, metadata.timeToNextUpdate(now));
        // refreshBackoffMs elapsed.
        now += refreshBackoffMs;
        // It should return 0 to let next try.
        Assert.assertEquals(0, metadata.timeToNextUpdate(now));
        Assert.assertEquals(0, metadata.timeToNextUpdate((now + 1)));
    }

    @Test
    public void testFailedUpdate() {
        long time = 100;
        metadata.update(MetadataTest.emptyMetadataResponse(), time);
        Assert.assertEquals(100, metadata.timeToNextUpdate(1000));
        metadata.failedUpdate(1100, null);
        Assert.assertEquals(100, metadata.timeToNextUpdate(1100));
        Assert.assertEquals(100, metadata.lastSuccessfulUpdate());
        metadata.update(MetadataTest.emptyMetadataResponse(), time);
        Assert.assertEquals(100, metadata.timeToNextUpdate(1000));
    }

    @Test
    public void testClusterListenerGetsNotifiedOfUpdate() {
        long time = 0;
        MockClusterResourceListener mockClusterListener = new MockClusterResourceListener();
        ClusterResourceListeners listeners = new ClusterResourceListeners();
        listeners.maybeAdd(mockClusterListener);
        metadata = new Metadata(refreshBackoffMs, metadataExpireMs, new LogContext(), listeners);
        String hostName = "www.example.com";
        metadata.bootstrap(Collections.singletonList(new InetSocketAddress(hostName, 9002)), time);
        Assert.assertFalse("ClusterResourceListener should not called when metadata is updated with bootstrap Cluster", MockClusterResourceListener.IS_ON_UPDATE_CALLED.get());
        Map<String, Integer> partitionCounts = new HashMap<>();
        partitionCounts.put("topic", 1);
        partitionCounts.put("topic1", 1);
        MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, partitionCounts);
        metadata.update(metadataResponse, 100);
        Assert.assertEquals("MockClusterResourceListener did not get cluster metadata correctly", "dummy", mockClusterListener.clusterResource().clusterId());
        Assert.assertTrue("MockClusterResourceListener should be called when metadata is updated with non-bootstrap Cluster", MockClusterResourceListener.IS_ON_UPDATE_CALLED.get());
    }

    @Test
    public void testRequestUpdate() {
        Assert.assertFalse(metadata.updateRequested());
        int[] epochs = new int[]{ 42, 42, 41, 41, 42, 43, 43, 42, 41, 44 };
        boolean[] updateResult = new boolean[]{ true, false, false, false, false, true, false, false, false, true };
        TopicPartition tp = new TopicPartition("topic", 0);
        for (int i = 0; i < (epochs.length); i++) {
            metadata.updateLastSeenEpochIfNewer(tp, epochs[i]);
            if (updateResult[i]) {
                Assert.assertTrue((("Expected metadata update to be requested [" + i) + "]"), metadata.updateRequested());
            } else {
                Assert.assertFalse((("Did not expect metadata update to be requested [" + i) + "]"), metadata.updateRequested());
            }
            metadata.update(MetadataTest.emptyMetadataResponse(), 0L);
            Assert.assertFalse(metadata.updateRequested());
        }
    }

    @Test
    public void testRejectOldMetadata() {
        Map<String, Integer> partitionCounts = new HashMap<>();
        partitionCounts.put("topic-1", 1);
        TopicPartition tp = new TopicPartition("topic-1", 0);
        metadata.update(MetadataTest.emptyMetadataResponse(), 0L);
        // First epoch seen, accept it
        {
            MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), partitionCounts, ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(100), replicas, isr, offlineReplicas));
            metadata.update(metadataResponse, 10L);
            Assert.assertNotNull(metadata.fetch().partition(tp));
            Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 100);
        }
        // Fake an empty ISR, but with an older epoch, should reject it
        {
            MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), partitionCounts, ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(99), replicas, Collections.emptyList(), offlineReplicas));
            metadata.update(metadataResponse, 20L);
            Assert.assertEquals(metadata.fetch().partition(tp).inSyncReplicas().length, 1);
            Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 100);
        }
        // Fake an empty ISR, with same epoch, accept it
        {
            MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), partitionCounts, ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(100), replicas, Collections.emptyList(), offlineReplicas));
            metadata.update(metadataResponse, 20L);
            Assert.assertEquals(metadata.fetch().partition(tp).inSyncReplicas().length, 0);
            Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 100);
        }
        // Empty metadata response, should not keep old partition but should keep the last-seen epoch
        {
            MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), Collections.emptyMap(), MetadataResponse.PartitionMetadata::new);
            metadata.update(metadataResponse, 20L);
            Assert.assertNull(metadata.fetch().partition(tp));
            Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 100);
        }
        // Back in the metadata, with old epoch, should not get added
        {
            MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), partitionCounts, ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(99), replicas, isr, offlineReplicas));
            metadata.update(metadataResponse, 10L);
            Assert.assertNull(metadata.fetch().partition(tp));
            Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 100);
        }
    }

    @Test
    public void testMaybeRequestUpdate() {
        TopicPartition tp = new TopicPartition("topic-1", 0);
        metadata.update(MetadataTest.emptyMetadataResponse(), 0L);
        Assert.assertTrue(metadata.updateLastSeenEpochIfNewer(tp, 1));
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 1);
        metadata.update(MetadataTest.emptyMetadataResponse(), 1L);
        Assert.assertFalse(metadata.updateLastSeenEpochIfNewer(tp, 1));
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 1);
        metadata.update(MetadataTest.emptyMetadataResponse(), 2L);
        Assert.assertFalse(metadata.updateLastSeenEpochIfNewer(tp, 0));
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 1);
        metadata.update(MetadataTest.emptyMetadataResponse(), 3L);
        Assert.assertTrue(metadata.updateLastSeenEpochIfNewer(tp, 2));
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 2);
    }

    @Test
    public void testOutOfBandEpochUpdate() {
        Map<String, Integer> partitionCounts = new HashMap<>();
        partitionCounts.put("topic-1", 5);
        TopicPartition tp = new TopicPartition("topic-1", 0);
        metadata.update(MetadataTest.emptyMetadataResponse(), 0L);
        Assert.assertTrue(metadata.updateLastSeenEpochIfNewer(tp, 99));
        // Update epoch to 100
        MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), partitionCounts, ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(100), replicas, isr, offlineReplicas));
        metadata.update(metadataResponse, 10L);
        Assert.assertNotNull(metadata.fetch().partition(tp));
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 100);
        // Simulate a leader epoch from another response, like a fetch response (not yet implemented)
        Assert.assertTrue(metadata.updateLastSeenEpochIfNewer(tp, 101));
        // Cache of partition stays, but current partition info is not available since it's stale
        Assert.assertNotNull(metadata.fetch().partition(tp));
        Assert.assertEquals(metadata.fetch().partitionCountForTopic("topic-1").longValue(), 5);
        Assert.assertFalse(metadata.partitionInfoIfCurrent(tp).isPresent());
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 101);
        // Metadata with older epoch is rejected, metadata state is unchanged
        metadata.update(metadataResponse, 20L);
        Assert.assertNotNull(metadata.fetch().partition(tp));
        Assert.assertEquals(metadata.fetch().partitionCountForTopic("topic-1").longValue(), 5);
        Assert.assertFalse(metadata.partitionInfoIfCurrent(tp).isPresent());
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 101);
        // Metadata with equal or newer epoch is accepted
        metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), partitionCounts, ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.of(101), replicas, isr, offlineReplicas));
        metadata.update(metadataResponse, 30L);
        Assert.assertNotNull(metadata.fetch().partition(tp));
        Assert.assertEquals(metadata.fetch().partitionCountForTopic("topic-1").longValue(), 5);
        Assert.assertTrue(metadata.partitionInfoIfCurrent(tp).isPresent());
        Assert.assertEquals(metadata.lastSeenLeaderEpoch(tp).get().longValue(), 101);
    }

    @Test
    public void testNoEpoch() {
        metadata.update(MetadataTest.emptyMetadataResponse(), 0L);
        MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 1, Collections.emptyMap(), Collections.singletonMap("topic-1", 1), ( error, partition, leader, leaderEpoch, replicas, isr, offlineReplicas) -> new MetadataResponse.PartitionMetadata(error, partition, leader, Optional.empty(), replicas, isr, offlineReplicas));
        metadata.update(metadataResponse, 10L);
        TopicPartition tp = new TopicPartition("topic-1", 0);
        // no epoch
        Assert.assertFalse(metadata.lastSeenLeaderEpoch(tp).isPresent());
        // still works
        Assert.assertTrue(metadata.partitionInfoIfCurrent(tp).isPresent());
        Assert.assertEquals(metadata.partitionInfoIfCurrent(tp).get().partitionInfo().partition(), 0);
        Assert.assertEquals(metadata.partitionInfoIfCurrent(tp).get().partitionInfo().leader().id(), 0);
    }

    @Test
    public void testClusterCopy() {
        Map<String, Integer> counts = new HashMap<>();
        Map<String, Errors> errors = new HashMap<>();
        counts.put("topic1", 2);
        counts.put("topic2", 3);
        counts.put(GROUP_METADATA_TOPIC_NAME, 3);
        errors.put("topic3", INVALID_TOPIC_EXCEPTION);
        errors.put("topic4", TOPIC_AUTHORIZATION_FAILED);
        MetadataResponse metadataResponse = TestUtils.metadataUpdateWith("dummy", 4, errors, counts);
        metadata.update(metadataResponse, 0L);
        Cluster cluster = metadata.fetch();
        Assert.assertEquals(cluster.clusterResource().clusterId(), "dummy");
        Assert.assertEquals(cluster.nodes().size(), 4);
        // topic counts
        Assert.assertEquals(cluster.invalidTopics(), Collections.singleton("topic3"));
        Assert.assertEquals(cluster.unauthorizedTopics(), Collections.singleton("topic4"));
        Assert.assertEquals(cluster.topics().size(), 3);
        Assert.assertEquals(cluster.internalTopics(), Collections.singleton(GROUP_METADATA_TOPIC_NAME));
        // partition counts
        Assert.assertEquals(cluster.partitionsForTopic("topic1").size(), 2);
        Assert.assertEquals(cluster.partitionsForTopic("topic2").size(), 3);
        // Sentinel instances
        InetSocketAddress address = InetSocketAddress.createUnresolved("localhost", 0);
        Cluster fromMetadata = MetadataCache.bootstrap(Collections.singletonList(address)).cluster();
        Cluster fromCluster = Cluster.bootstrap(Collections.singletonList(address));
        Assert.assertEquals(fromMetadata, fromCluster);
        Cluster fromMetadataEmpty = MetadataCache.empty().cluster();
        Cluster fromClusterEmpty = Cluster.empty();
        Assert.assertEquals(fromMetadataEmpty, fromClusterEmpty);
    }

    @Test
    public void testRequestVersion() {
        Time time = new MockTime();
        metadata.requestUpdate();
        Metadata.MetadataRequestAndVersion versionAndBuilder = metadata.newMetadataRequestAndVersion();
        metadata.update(versionAndBuilder.requestVersion, TestUtils.metadataUpdateWith(1, Collections.singletonMap("topic", 1)), time.milliseconds());
        Assert.assertFalse(metadata.updateRequested());
        // bump the request version for new topics added to the metadata
        metadata.requestUpdateForNewTopics();
        // simulating a bump while a metadata request is in flight
        versionAndBuilder = metadata.newMetadataRequestAndVersion();
        metadata.requestUpdateForNewTopics();
        metadata.update(versionAndBuilder.requestVersion, TestUtils.metadataUpdateWith(1, Collections.singletonMap("topic", 1)), time.milliseconds());
        // metadata update is still needed
        Assert.assertTrue(metadata.updateRequested());
        // the next update will resolve it
        versionAndBuilder = metadata.newMetadataRequestAndVersion();
        metadata.update(versionAndBuilder.requestVersion, TestUtils.metadataUpdateWith(1, Collections.singletonMap("topic", 1)), time.milliseconds());
        Assert.assertFalse(metadata.updateRequested());
    }

    @Test
    public void testInvalidTopicError() {
        Time time = new MockTime();
        String invalidTopic = "topic dfsa";
        MetadataResponse invalidTopicResponse = TestUtils.metadataUpdateWith("clusterId", 1, Collections.singletonMap(invalidTopic, INVALID_TOPIC_EXCEPTION), Collections.emptyMap());
        metadata.update(invalidTopicResponse, time.milliseconds());
        InvalidTopicException e = Assert.assertThrows(InvalidTopicException.class, () -> metadata.maybeThrowException());
        Assert.assertEquals(Collections.singleton(invalidTopic), e.invalidTopics());
        // We clear the exception once it has been raised to the user
        Assert.assertNull(metadata.getAndClearMetadataException());
        // Reset the invalid topic error
        metadata.update(invalidTopicResponse, time.milliseconds());
        // If we get a good update, the error should clear even if we haven't had a chance to raise it to the user
        metadata.update(MetadataTest.emptyMetadataResponse(), time.milliseconds());
        Assert.assertNull(metadata.getAndClearMetadataException());
    }

    @Test
    public void testTopicAuthorizationError() {
        Time time = new MockTime();
        String invalidTopic = "foo";
        MetadataResponse unauthorizedTopicResponse = TestUtils.metadataUpdateWith("clusterId", 1, Collections.singletonMap(invalidTopic, TOPIC_AUTHORIZATION_FAILED), Collections.emptyMap());
        metadata.update(unauthorizedTopicResponse, time.milliseconds());
        TopicAuthorizationException e = Assert.assertThrows(TopicAuthorizationException.class, () -> metadata.maybeThrowException());
        Assert.assertEquals(Collections.singleton(invalidTopic), e.unauthorizedTopics());
        // We clear the exception once it has been raised to the user
        Assert.assertNull(metadata.getAndClearMetadataException());
        // Reset the unauthorized topic error
        metadata.update(unauthorizedTopicResponse, time.milliseconds());
        // If we get a good update, the error should clear even if we haven't had a chance to raise it to the user
        metadata.update(MetadataTest.emptyMetadataResponse(), time.milliseconds());
        Assert.assertNull(metadata.getAndClearMetadataException());
    }
}

