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
package org.apache.kafka.trogdor.common;


import ProducerConfig.ACKS_CONFIG;
import ProducerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ProducerConfig.CLIENT_ID_CONFIG;
import ProducerConfig.LINGER_MS_CONFIG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.admin.MockAdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.utils.Utils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WorkerUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(WorkerUtilsTest.class);

    private final Node broker1 = new Node(0, "testHost-1", 1234);

    private final Node broker2 = new Node(1, "testHost-2", 1234);

    private final Node broker3 = new Node(1, "testHost-3", 1234);

    private final List<Node> cluster = Arrays.asList(broker1, broker2, broker3);

    private final List<Node> singleReplica = Collections.singletonList(broker1);

    private static final String TEST_TOPIC = "test-topic-1";

    private static final short TEST_REPLICATION_FACTOR = 1;

    private static final int TEST_PARTITIONS = 1;

    private static final NewTopic NEW_TEST_TOPIC = new NewTopic(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.TEST_PARTITIONS, WorkerUtilsTest.TEST_REPLICATION_FACTOR);

    private MockAdminClient adminClient;

    @Test
    public void testCreateOneTopic() throws Throwable {
        Map<String, NewTopic> newTopics = Collections.singletonMap(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.NEW_TEST_TOPIC);
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, newTopics, true);
        Assert.assertEquals(Collections.singleton(WorkerUtilsTest.TEST_TOPIC), adminClient.listTopics().names().get());
        Assert.assertEquals(new org.apache.kafka.clients.admin.TopicDescription(WorkerUtilsTest.TEST_TOPIC, false, Collections.singletonList(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()))), adminClient.describeTopics(Collections.singleton(WorkerUtilsTest.TEST_TOPIC)).values().get(WorkerUtilsTest.TEST_TOPIC).get());
    }

    @Test
    public void testCreateRetriesOnTimeout() throws Throwable {
        adminClient.timeoutNextRequest(1);
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, Collections.singletonMap(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.NEW_TEST_TOPIC), true);
        Assert.assertEquals(new org.apache.kafka.clients.admin.TopicDescription(WorkerUtilsTest.TEST_TOPIC, false, Collections.singletonList(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()))), adminClient.describeTopics(Collections.singleton(WorkerUtilsTest.TEST_TOPIC)).values().get(WorkerUtilsTest.TEST_TOPIC).get());
    }

    @Test
    public void testCreateZeroTopicsDoesNothing() throws Throwable {
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, Collections.<String, NewTopic>emptyMap(), true);
        Assert.assertEquals(0, adminClient.listTopics().names().get().size());
    }

    @Test(expected = TopicExistsException.class)
    public void testCreateTopicsFailsIfAtLeastOneTopicExists() throws Throwable {
        adminClient.addTopic(false, WorkerUtilsTest.TEST_TOPIC, Collections.singletonList(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList())), null);
        Map<String, NewTopic> newTopics = new HashMap<>();
        newTopics.put(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.NEW_TEST_TOPIC);
        newTopics.put("another-topic", new NewTopic("another-topic", WorkerUtilsTest.TEST_PARTITIONS, WorkerUtilsTest.TEST_REPLICATION_FACTOR));
        newTopics.put("one-more-topic", new NewTopic("one-more-topic", WorkerUtilsTest.TEST_PARTITIONS, WorkerUtilsTest.TEST_REPLICATION_FACTOR));
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, newTopics, true);
    }

    @Test(expected = RuntimeException.class)
    public void testExistingTopicsMustHaveRequestedNumberOfPartitions() throws Throwable {
        List<TopicPartitionInfo> tpInfo = new ArrayList<>();
        tpInfo.add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
        tpInfo.add(new TopicPartitionInfo(1, broker2, singleReplica, Collections.<Node>emptyList()));
        adminClient.addTopic(false, WorkerUtilsTest.TEST_TOPIC, tpInfo, null);
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, Collections.singletonMap(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.NEW_TEST_TOPIC), false);
    }

    @Test
    public void testExistingTopicsNotCreated() throws Throwable {
        final String existingTopic = "existing-topic";
        List<TopicPartitionInfo> tpInfo = new ArrayList<>();
        tpInfo.add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
        tpInfo.add(new TopicPartitionInfo(1, broker2, singleReplica, Collections.<Node>emptyList()));
        tpInfo.add(new TopicPartitionInfo(2, broker3, singleReplica, Collections.<Node>emptyList()));
        adminClient.addTopic(false, existingTopic, tpInfo, null);
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, Collections.singletonMap(existingTopic, new NewTopic(existingTopic, tpInfo.size(), WorkerUtilsTest.TEST_REPLICATION_FACTOR)), false);
        Assert.assertEquals(Collections.singleton(existingTopic), adminClient.listTopics().names().get());
    }

    @Test
    public void testCreatesNotExistingTopics() throws Throwable {
        // should be no topics before the call
        Assert.assertEquals(0, adminClient.listTopics().names().get().size());
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, Collections.singletonMap(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.NEW_TEST_TOPIC), false);
        Assert.assertEquals(Collections.singleton(WorkerUtilsTest.TEST_TOPIC), adminClient.listTopics().names().get());
        Assert.assertEquals(new org.apache.kafka.clients.admin.TopicDescription(WorkerUtilsTest.TEST_TOPIC, false, Collections.singletonList(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()))), adminClient.describeTopics(Collections.singleton(WorkerUtilsTest.TEST_TOPIC)).values().get(WorkerUtilsTest.TEST_TOPIC).get());
    }

    @Test
    public void testCreatesOneTopicVerifiesOneTopic() throws Throwable {
        final String existingTopic = "existing-topic";
        List<TopicPartitionInfo> tpInfo = new ArrayList<>();
        tpInfo.add(new TopicPartitionInfo(0, broker1, singleReplica, Collections.<Node>emptyList()));
        tpInfo.add(new TopicPartitionInfo(1, broker2, singleReplica, Collections.<Node>emptyList()));
        adminClient.addTopic(false, existingTopic, tpInfo, null);
        Map<String, NewTopic> topics = new HashMap<>();
        topics.put(existingTopic, new NewTopic(existingTopic, tpInfo.size(), WorkerUtilsTest.TEST_REPLICATION_FACTOR));
        topics.put(WorkerUtilsTest.TEST_TOPIC, WorkerUtilsTest.NEW_TEST_TOPIC);
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, topics, false);
        Assert.assertEquals(Utils.mkSet(existingTopic, WorkerUtilsTest.TEST_TOPIC), adminClient.listTopics().names().get());
    }

    @Test
    public void testCreateNonExistingTopicsWithZeroTopicsDoesNothing() throws Throwable {
        WorkerUtils.createTopics(WorkerUtilsTest.log, adminClient, Collections.<String, NewTopic>emptyMap(), false);
        Assert.assertEquals(0, adminClient.listTopics().names().get().size());
    }

    @Test
    public void testAddConfigsToPropertiesAddsAllConfigs() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        Properties resultProps = new Properties();
        resultProps.putAll(props);
        resultProps.put(CLIENT_ID_CONFIG, "test-client");
        resultProps.put(LINGER_MS_CONFIG, "1000");
        WorkerUtils.addConfigsToProperties(props, Collections.singletonMap(CLIENT_ID_CONFIG, "test-client"), Collections.singletonMap(LINGER_MS_CONFIG, "1000"));
        Assert.assertEquals(resultProps, props);
    }

    @Test
    public void testCommonConfigOverwritesDefaultProps() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        Properties resultProps = new Properties();
        resultProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        resultProps.put(ACKS_CONFIG, "1");
        resultProps.put(LINGER_MS_CONFIG, "1000");
        WorkerUtils.addConfigsToProperties(props, Collections.singletonMap(ACKS_CONFIG, "1"), Collections.singletonMap(LINGER_MS_CONFIG, "1000"));
        Assert.assertEquals(resultProps, props);
    }

    @Test
    public void testClientConfigOverwritesBothDefaultAndCommonConfigs() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ACKS_CONFIG, "all");
        Properties resultProps = new Properties();
        resultProps.put(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        resultProps.put(ACKS_CONFIG, "0");
        WorkerUtils.addConfigsToProperties(props, Collections.singletonMap(ACKS_CONFIG, "1"), Collections.singletonMap(ACKS_CONFIG, "0"));
        Assert.assertEquals(resultProps, props);
    }

    @Test
    public void testGetMatchingTopicPartitionsCorrectlyMatchesExactTopicName() throws Throwable {
        final String topic1 = "existing-topic";
        final String topic2 = "another-topic";
        makeExistingTopicWithOneReplica(topic1, 10);
        makeExistingTopicWithOneReplica(topic2, 20);
        Collection<TopicPartition> topicPartitions = WorkerUtils.getMatchingTopicPartitions(adminClient, topic2, 0, 2);
        Assert.assertEquals(Utils.mkSet(new TopicPartition(topic2, 0), new TopicPartition(topic2, 1), new TopicPartition(topic2, 2)), new java.util.HashSet(topicPartitions));
    }

    @Test
    public void testGetMatchingTopicPartitionsCorrectlyMatchesTopics() throws Throwable {
        final String topic1 = "test-topic";
        final String topic2 = "another-test-topic";
        final String topic3 = "one-more";
        makeExistingTopicWithOneReplica(topic1, 10);
        makeExistingTopicWithOneReplica(topic2, 20);
        makeExistingTopicWithOneReplica(topic3, 30);
        Collection<TopicPartition> topicPartitions = WorkerUtils.getMatchingTopicPartitions(adminClient, ".*-topic$", 0, 1);
        Assert.assertEquals(Utils.mkSet(new TopicPartition(topic1, 0), new TopicPartition(topic1, 1), new TopicPartition(topic2, 0), new TopicPartition(topic2, 1)), new java.util.HashSet(topicPartitions));
    }
}

