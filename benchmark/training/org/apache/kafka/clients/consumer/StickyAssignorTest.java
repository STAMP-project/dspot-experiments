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


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.kafka.clients.consumer.internals.PartitionAssignor.Subscription;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Utils;
import org.junit.Assert;
import org.junit.Test;


public class StickyAssignorTest {
    private StickyAssignor assignor = new StickyAssignor();

    @Test
    public void testOneConsumerNoTopic() {
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        Map<String, Subscription> subscriptions = Collections.singletonMap(consumerId, new Subscription(Collections.<String>emptyList()));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        Assert.assertTrue(assignment.get(consumerId).isEmpty());
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testOneConsumerNonexistentTopic() {
        String topic = "topic";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 0);
        Map<String, Subscription> subscriptions = Collections.singletonMap(consumerId, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        Assert.assertTrue(assignment.get(consumerId).isEmpty());
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testOneConsumerOneTopic() {
        String topic = "topic";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        Map<String, Subscription> subscriptions = Collections.singletonMap(consumerId, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 0), StickyAssignorTest.tp(topic, 1), StickyAssignorTest.tp(topic, 2)), assignment.get(consumerId));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testOnlyAssignsPartitionsFromSubscribedTopics() {
        String topic = "topic";
        String otherTopic = "other";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        partitionsPerTopic.put(otherTopic, 3);
        Map<String, Subscription> subscriptions = Collections.singletonMap(consumerId, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 0), StickyAssignorTest.tp(topic, 1), StickyAssignorTest.tp(topic, 2)), assignment.get(consumerId));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testOneConsumerMultipleTopics() {
        String topic1 = "topic1";
        String topic2 = "topic2";
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic1, 1);
        partitionsPerTopic.put(topic2, 2);
        Map<String, Subscription> subscriptions = Collections.singletonMap(consumerId, new Subscription(StickyAssignorTest.topics(topic1, topic2)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic1, 0), StickyAssignorTest.tp(topic2, 0), StickyAssignorTest.tp(topic2, 1)), assignment.get(consumerId));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testTwoConsumersOneTopicOnePartition() {
        String topic = "topic";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 1);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic)));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 0)), assignment.get(consumer1));
        Assert.assertEquals(Collections.<TopicPartition>emptyList(), assignment.get(consumer2));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testTwoConsumersOneTopicTwoPartitions() {
        String topic = "topic";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 2);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic)));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 0)), assignment.get(consumer1));
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 1)), assignment.get(consumer2));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testMultipleConsumersMixedTopicSubscriptions() {
        String topic1 = "topic1";
        String topic2 = "topic2";
        String consumer1 = "consumer1";
        String consumer2 = "consumer2";
        String consumer3 = "consumer3";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic1, 3);
        partitionsPerTopic.put(topic2, 2);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic1)));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic1, topic2)));
        subscriptions.put(consumer3, new Subscription(StickyAssignorTest.topics(topic1)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic1, 0), StickyAssignorTest.tp(topic1, 2)), assignment.get(consumer1));
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic2, 0), StickyAssignorTest.tp(topic2, 1)), assignment.get(consumer2));
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic1, 1)), assignment.get(consumer3));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
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
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic1, topic2)));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic1, topic2)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic1, 0), StickyAssignorTest.tp(topic1, 2), StickyAssignorTest.tp(topic2, 1)), assignment.get(consumer1));
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic1, 1), StickyAssignorTest.tp(topic2, 0), StickyAssignorTest.tp(topic2, 2)), assignment.get(consumer2));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testAddRemoveConsumerOneTopic() {
        String topic = "topic";
        String consumer1 = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 0), StickyAssignorTest.tp(topic, 1), StickyAssignorTest.tp(topic, 2)), assignment.get(consumer1));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
        String consumer2 = "consumer2";
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer1))));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic)));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 1), StickyAssignorTest.tp(topic, 2)), assignment.get(consumer1));
        Assert.assertEquals(StickyAssignorTest.partitions(StickyAssignorTest.tp(topic, 0)), assignment.get(consumer2));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
        Assert.assertTrue(assignor.isSticky());
        subscriptions.remove(consumer1);
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer2))));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertTrue(assignment.get(consumer2).contains(StickyAssignorTest.tp(topic, 0)));
        Assert.assertTrue(assignment.get(consumer2).contains(StickyAssignorTest.tp(topic, 1)));
        Assert.assertTrue(assignment.get(consumer2).contains(StickyAssignorTest.tp(topic, 2)));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
        Assert.assertTrue(assignor.isSticky());
    }

    /**
     * This unit test performs sticky assignment for a scenario that round robin assignor handles poorly.
     * Topics (partitions per topic): topic1 (2), topic2 (1), topic3 (2), topic4 (1), topic5 (2)
     * Subscriptions:
     *  - consumer1: topic1, topic2, topic3, topic4, topic5
     *  - consumer2: topic1, topic3, topic5
     *  - consumer3: topic1, topic3, topic5
     *  - consumer4: topic1, topic2, topic3, topic4, topic5
     * Round Robin Assignment Result:
     *  - consumer1: topic1-0, topic3-0, topic5-0
     *  - consumer2: topic1-1, topic3-1, topic5-1
     *  - consumer3:
     *  - consumer4: topic2-0, topic4-0
     * Sticky Assignment Result:
     *  - consumer1: topic2-0, topic3-0
     *  - consumer2: topic1-0, topic3-1
     *  - consumer3: topic1-1, topic5-0
     *  - consumer4: topic4-0, topic5-1
     */
    @Test
    public void testPoorRoundRobinAssignmentScenario() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        for (int i = 1; i <= 5; i++)
            partitionsPerTopic.put(String.format("topic%d", i), ((i % 2) + 1));

        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer1", new Subscription(StickyAssignorTest.topics("topic1", "topic2", "topic3", "topic4", "topic5")));
        subscriptions.put("consumer2", new Subscription(StickyAssignorTest.topics("topic1", "topic3", "topic5")));
        subscriptions.put("consumer3", new Subscription(StickyAssignorTest.topics("topic1", "topic3", "topic5")));
        subscriptions.put("consumer4", new Subscription(StickyAssignorTest.topics("topic1", "topic2", "topic3", "topic4", "topic5")));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
    }

    @Test
    public void testAddRemoveTopicTwoConsumers() {
        String topic = "topic";
        String consumer1 = "consumer";
        String consumer2 = "consumer2";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic)));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        // verify balance
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        // verify stickiness
        List<TopicPartition> consumer1Assignment1 = assignment.get(consumer1);
        List<TopicPartition> consumer2Assignment1 = assignment.get(consumer2);
        Assert.assertTrue(((((consumer1Assignment1.size()) == 1) && ((consumer2Assignment1.size()) == 2)) || (((consumer1Assignment1.size()) == 2) && ((consumer2Assignment1.size()) == 1))));
        String topic2 = "topic2";
        partitionsPerTopic.put(topic2, 3);
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic, topic2), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer1))));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic, topic2), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer2))));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        // verify balance
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
        // verify stickiness
        List<TopicPartition> consumer1assignment = assignment.get(consumer1);
        List<TopicPartition> consumer2assignment = assignment.get(consumer2);
        Assert.assertTrue((((consumer1assignment.size()) == 3) && ((consumer2assignment.size()) == 3)));
        Assert.assertTrue(consumer1assignment.containsAll(consumer1Assignment1));
        Assert.assertTrue(consumer2assignment.containsAll(consumer2Assignment1));
        Assert.assertTrue(assignor.isSticky());
        partitionsPerTopic.remove(topic);
        subscriptions.put(consumer1, new Subscription(StickyAssignorTest.topics(topic2), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer1))));
        subscriptions.put(consumer2, new Subscription(StickyAssignorTest.topics(topic2), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer2))));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        // verify balance
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
        // verify stickiness
        List<TopicPartition> consumer1Assignment3 = assignment.get(consumer1);
        List<TopicPartition> consumer2Assignment3 = assignment.get(consumer2);
        Assert.assertTrue(((((consumer1Assignment3.size()) == 1) && ((consumer2Assignment3.size()) == 2)) || (((consumer1Assignment3.size()) == 2) && ((consumer2Assignment3.size()) == 1))));
        Assert.assertTrue(consumer1assignment.containsAll(consumer1Assignment3));
        Assert.assertTrue(consumer2assignment.containsAll(consumer2Assignment3));
        Assert.assertTrue(assignor.isSticky());
    }

    @Test
    public void testReassignmentAfterOneConsumerLeaves() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        for (int i = 1; i < 20; i++)
            partitionsPerTopic.put(getTopicName(i, 20), i);

        Map<String, Subscription> subscriptions = new HashMap<>();
        for (int i = 1; i < 20; i++) {
            List<String> topics = new ArrayList<String>();
            for (int j = 1; j <= i; j++)
                topics.add(getTopicName(j, 20));

            subscriptions.put(getConsumerName(i, 20), new Subscription(topics));
        }
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        for (int i = 1; i < 20; i++) {
            String consumer = getConsumerName(i, 20);
            subscriptions.put(consumer, new Subscription(subscriptions.get(consumer).topics(), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer))));
        }
        subscriptions.remove("consumer10");
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(assignor.isSticky());
    }

    @Test
    public void testReassignmentAfterOneConsumerAdded() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put("topic", 20);
        Map<String, Subscription> subscriptions = new HashMap<>();
        for (int i = 1; i < 10; i++)
            subscriptions.put(getConsumerName(i, 10), new Subscription(StickyAssignorTest.topics("topic")));

        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        // add a new consumer
        subscriptions.put(getConsumerName(10, 10), new Subscription(StickyAssignorTest.topics("topic")));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(assignor.isSticky());
    }

    @Test
    public void testSameSubscriptions() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        for (int i = 1; i < 15; i++)
            partitionsPerTopic.put(getTopicName(i, 15), i);

        Map<String, Subscription> subscriptions = new HashMap<>();
        for (int i = 1; i < 9; i++) {
            List<String> topics = new ArrayList<String>();
            for (int j = 1; j <= (partitionsPerTopic.size()); j++)
                topics.add(getTopicName(j, 15));

            subscriptions.put(getConsumerName(i, 9), new Subscription(topics));
        }
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        for (int i = 1; i < 9; i++) {
            String consumer = getConsumerName(i, 9);
            subscriptions.put(consumer, new Subscription(subscriptions.get(consumer).topics(), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer))));
        }
        subscriptions.remove(getConsumerName(5, 9));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(assignor.isSticky());
    }

    @Test
    public void testLargeAssignmentWithMultipleConsumersLeaving() {
        Random rand = new Random();
        int topicCount = 40;
        int consumerCount = 200;
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        for (int i = 0; i < topicCount; i++)
            partitionsPerTopic.put(getTopicName(i, topicCount), ((rand.nextInt(10)) + 1));

        Map<String, Subscription> subscriptions = new HashMap<>();
        for (int i = 0; i < consumerCount; i++) {
            List<String> topics = new ArrayList<String>();
            for (int j = 0; j < (rand.nextInt(20)); j++)
                topics.add(getTopicName(rand.nextInt(topicCount), topicCount));

            subscriptions.put(getConsumerName(i, consumerCount), new Subscription(topics));
        }
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        for (int i = 1; i < consumerCount; i++) {
            String consumer = getConsumerName(i, consumerCount);
            subscriptions.put(consumer, new Subscription(subscriptions.get(consumer).topics(), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer))));
        }
        for (int i = 0; i < 50; ++i) {
            String c = getConsumerName(rand.nextInt(consumerCount), consumerCount);
            subscriptions.remove(c);
        }
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(assignor.isSticky());
    }

    @Test
    public void testNewSubscription() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        for (int i = 1; i < 5; i++)
            partitionsPerTopic.put(getTopicName(i, 5), 1);

        Map<String, Subscription> subscriptions = new HashMap<>();
        for (int i = 0; i < 3; i++) {
            List<String> topics = new ArrayList<String>();
            for (int j = i; j <= ((3 * i) - 2); j++)
                topics.add(getTopicName(j, 5));

            subscriptions.put(getConsumerName(i, 3), new Subscription(topics));
        }
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        subscriptions.get(getConsumerName(0, 3)).topics().add(getTopicName(1, 5));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(assignor.isSticky());
    }

    @Test
    public void testReassignmentWithRandomSubscriptionsAndChanges() {
        final int minNumConsumers = 20;
        final int maxNumConsumers = 40;
        final int minNumTopics = 10;
        final int maxNumTopics = 20;
        for (int round = 1; round <= 100; ++round) {
            int numTopics = minNumTopics + (new Random().nextInt((maxNumTopics - minNumTopics)));
            ArrayList<String> topics = new ArrayList<>();
            for (int i = 0; i < numTopics; ++i)
                topics.add(getTopicName(i, maxNumTopics));

            Map<String, Integer> partitionsPerTopic = new HashMap<>();
            for (int i = 0; i < numTopics; ++i)
                partitionsPerTopic.put(getTopicName(i, maxNumTopics), (i + 1));

            int numConsumers = minNumConsumers + (new Random().nextInt((maxNumConsumers - minNumConsumers)));
            Map<String, Subscription> subscriptions = new HashMap<>();
            for (int i = 0; i < numConsumers; ++i) {
                List<String> sub = Utils.sorted(StickyAssignorTest.getRandomSublist(topics));
                subscriptions.put(getConsumerName(i, maxNumConsumers), new Subscription(sub));
            }
            StickyAssignor assignor = new StickyAssignor();
            Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
            StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
            subscriptions.clear();
            for (int i = 0; i < numConsumers; ++i) {
                List<String> sub = Utils.sorted(StickyAssignorTest.getRandomSublist(topics));
                String consumer = getConsumerName(i, maxNumConsumers);
                subscriptions.put(consumer, new Subscription(sub, StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer))));
            }
            assignment = assignor.assign(partitionsPerTopic, subscriptions);
            StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
            Assert.assertTrue(assignor.isSticky());
        }
    }

    @Test
    public void testMoveExistingAssignments() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        for (int i = 1; i <= 6; i++)
            partitionsPerTopic.put(String.format("topic%02d", i), 1);

        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer01", new Subscription(StickyAssignorTest.topics("topic01", "topic02"), StickyAssignor.serializeTopicPartitionAssignment(StickyAssignorTest.partitions(StickyAssignorTest.tp("topic01", 0)))));
        subscriptions.put("consumer02", new Subscription(StickyAssignorTest.topics("topic01", "topic02", "topic03", "topic04"), StickyAssignor.serializeTopicPartitionAssignment(StickyAssignorTest.partitions(StickyAssignorTest.tp("topic02", 0), StickyAssignorTest.tp("topic03", 0)))));
        subscriptions.put("consumer03", new Subscription(StickyAssignorTest.topics("topic02", "topic03", "topic04", "topic05", "topic06"), StickyAssignor.serializeTopicPartitionAssignment(StickyAssignorTest.partitions(StickyAssignorTest.tp("topic04", 0), StickyAssignorTest.tp("topic05", 0), StickyAssignorTest.tp("topic06", 0)))));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
    }

    @Test
    public void testStickiness() {
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put("topic01", 3);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put("consumer01", new Subscription(StickyAssignorTest.topics("topic01")));
        subscriptions.put("consumer02", new Subscription(StickyAssignorTest.topics("topic01")));
        subscriptions.put("consumer03", new Subscription(StickyAssignorTest.topics("topic01")));
        subscriptions.put("consumer04", new Subscription(StickyAssignorTest.topics("topic01")));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Map<String, TopicPartition> partitionsAssigned = new HashMap<>();
        Set<Map.Entry<String, List<TopicPartition>>> assignments = assignment.entrySet();
        for (Map.Entry<String, List<TopicPartition>> entry : assignments) {
            String consumer = entry.getKey();
            List<TopicPartition> topicPartitions = entry.getValue();
            int size = topicPartitions.size();
            Assert.assertTrue((("Consumer " + consumer) + " is assigned more topic partitions than expected."), (size <= 1));
            if (size == 1)
                partitionsAssigned.put(consumer, topicPartitions.get(0));

        }
        // removing the potential group leader
        subscriptions.remove("consumer01");
        subscriptions.put("consumer02", new Subscription(StickyAssignorTest.topics("topic01"), StickyAssignor.serializeTopicPartitionAssignment(assignment.get("consumer02"))));
        subscriptions.put("consumer03", new Subscription(StickyAssignorTest.topics("topic01"), StickyAssignor.serializeTopicPartitionAssignment(assignment.get("consumer03"))));
        subscriptions.put("consumer04", new Subscription(StickyAssignorTest.topics("topic01"), StickyAssignor.serializeTopicPartitionAssignment(assignment.get("consumer04"))));
        assignment = assignor.assign(partitionsPerTopic, subscriptions);
        StickyAssignorTest.verifyValidityAndBalance(subscriptions, assignment);
        Assert.assertTrue(assignor.isSticky());
        assignments = assignment.entrySet();
        for (Map.Entry<String, List<TopicPartition>> entry : assignments) {
            String consumer = entry.getKey();
            List<TopicPartition> topicPartitions = entry.getValue();
            Assert.assertEquals((("Consumer " + consumer) + " is assigned more topic partitions than expected."), 1, topicPartitions.size());
            Assert.assertTrue(("Stickiness was not honored for consumer " + consumer), ((!(partitionsAssigned.containsKey(consumer))) || (assignment.get(consumer).contains(partitionsAssigned.get(consumer)))));
        }
    }

    @Test
    public void testAssignmentUpdatedForDeletedTopic() {
        String consumerId = "consumer";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put("topic01", 1);
        partitionsPerTopic.put("topic03", 100);
        Map<String, Subscription> subscriptions = Collections.singletonMap(consumerId, new Subscription(StickyAssignorTest.topics("topic01", "topic02", "topic03")));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        Assert.assertEquals(assignment.values().stream().mapToInt(( topicPartitions) -> topicPartitions.size()).sum(), (1 + 100));
        Assert.assertEquals(Collections.singleton(consumerId), assignment.keySet());
        Assert.assertTrue(StickyAssignorTest.isFullyBalanced(assignment));
    }

    @Test
    public void testNoExceptionThrownWhenOnlySubscribedTopicDeleted() {
        String topic = "topic01";
        String consumer = "consumer01";
        Map<String, Integer> partitionsPerTopic = new HashMap<>();
        partitionsPerTopic.put(topic, 3);
        Map<String, Subscription> subscriptions = new HashMap<>();
        subscriptions.put(consumer, new Subscription(StickyAssignorTest.topics(topic)));
        Map<String, List<TopicPartition>> assignment = assignor.assign(partitionsPerTopic, subscriptions);
        subscriptions.put(consumer, new Subscription(StickyAssignorTest.topics(topic), StickyAssignor.serializeTopicPartitionAssignment(assignment.get(consumer))));
        assignment = assignor.assign(Collections.emptyMap(), subscriptions);
        Assert.assertEquals(assignment.size(), 1);
        Assert.assertTrue(assignment.get(consumer).isEmpty());
    }
}

