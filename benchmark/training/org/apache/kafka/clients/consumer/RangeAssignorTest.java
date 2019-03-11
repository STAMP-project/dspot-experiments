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
package org.apache.kafka.clients.consumer;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.internals.PartitionAssignor.Subscription;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Test;


public class RangeAssignorTest {
    private RangeAssignor assignor = new RangeAssignor();

    @Test
    public void testOneConsumerNoTopic() {
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, Collections.singletonMap(consumerId, new Subscription(Collections.<String>emptyList())));
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        Assert.assertTrue(assignment.get(consumerId).isEmpty());
    }

    @Test
    public void testOneConsumerNonexistentTopic() {
        String topic = "topic";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, Collections.singletonMap(consumerId, new Subscription(RangeAssignorTest.topics(topic))));
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        Assert.assertTrue(assignment.get(consumerId).isEmpty());
    }

    @Test
    public void testOneConsumerOneTopic() {
        String topic = "topic";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, Collections.singletonMap(consumerId, new Subscription(RangeAssignorTest.topics(topic))));
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic, 0), RangeAssignorTest.tp(topic, 1), RangeAssignorTest.tp(topic, 2)), assignment.get(consumerId));
    }

    @Test
    public void testOnlyAssignsPartitionsFromSubscribedTopics() {
        String topic = "topic";
        String otherTopic = "other";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        partitionsPerTopic.put(otherTopic, 3);
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, Collections.singletonMap(consumerId, new Subscription(RangeAssignorTest.topics(topic))));
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic, 0), RangeAssignorTest.tp(topic, 1), RangeAssignorTest.tp(topic, 2)), assignment.get(consumerId));
    }

    @Test
    public void testOneConsumerMultipleTopics() {
        String topic1 = "topic1";
        String topic2 = "topic2";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic1, 1);
        partitionsPerTopic.put(topic2, 2);
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, Collections.singletonMap(consumerId, new Subscription(RangeAssignorTest.topics(topic1, topic2))));
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic1, 0), RangeAssignorTest.tp(topic2, 0), RangeAssignorTest.tp(topic2, 1)), assignment.get(consumerId));
    }

    @Test
    public void testTwoConsumersOneTopicOnePartition() {
        String topic = "topic";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 1);
        Map<String, Subscription> consumers = new HashMap<>();
        consumers.put(consumer1, new Subscription(RangeAssignorTest.topics(topic)));
        consumers.put(consumer2, new Subscription(RangeAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, consumers);
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic, 0)), assignment.get(consumer1));
        assertAssignment(Collections.<TopicPartition>emptyList(), assignment.get(consumer2));
    }

    @Test
    public void testTwoConsumersOneTopicTwoPartitions() {
        String topic = "topic";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 2);
        Map<String, Subscription> consumers = new HashMap<>();
        consumers.put(consumer1, new Subscription(RangeAssignorTest.topics(topic)));
        consumers.put(consumer2, new Subscription(RangeAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, consumers);
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic, 0)), assignment.get(consumer1));
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic, 1)), assignment.get(consumer2));
    }

    @Test
    public void testMultipleConsumersMixedTopics() {
        String topic1 = "topic1";
        String topic2 = "topic2";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        String consumer3 = "consumer3";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic1, 3);
        partitionsPerTopic.put(topic2, 2);
        Map<String, Subscription> consumers = new HashMap<>();
        consumers.put(consumer1, new Subscription(RangeAssignorTest.topics(topic1)));
        consumers.put(consumer2, new Subscription(RangeAssignorTest.topics(topic1, topic2)));
        consumers.put(consumer3, new Subscription(RangeAssignorTest.topics(topic1)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, consumers);
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic1, 0)), assignment.get(consumer1));
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic1, 1), RangeAssignorTest.tp(topic2, 0), RangeAssignorTest.tp(topic2, 1)), assignment.get(consumer2));
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic1, 2)), assignment.get(consumer3));
    }

    @Test
    public void testTwoConsumersTwoTopicsSixPartitions() {
        String topic1 = "topic1";
        String topic2 = "topic2";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic1, 3);
        partitionsPerTopic.put(topic2, 3);
        Map<String, Subscription> consumers = new HashMap<>();
        consumers.put(consumer1, new Subscription(RangeAssignorTest.topics(topic1, topic2)));
        consumers.put(consumer2, new Subscription(RangeAssignorTest.topics(topic1, topic2)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, consumers);
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic1, 0), RangeAssignorTest.tp(topic1, 1), RangeAssignorTest.tp(topic2, 0), RangeAssignorTest.tp(topic2, 1)), assignment.get(consumer1));
        assertAssignment(RangeAssignorTest.partitions(RangeAssignorTest.tp(topic1, 2), RangeAssignorTest.tp(topic2, 2)), assignment.get(consumer2));
    }
}

