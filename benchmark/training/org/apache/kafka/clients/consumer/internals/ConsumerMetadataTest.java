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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.LogContext;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.test.TestUtils;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerMetadataTest {
    private final Node node = new Node(1, "localhost", 9092);

    private final SubscriptionState subscription = new SubscriptionState(new LogContext(), OffsetResetStrategy.EARLIEST);

    private final Time time = new MockTime();

    @Test
    public void testPatternSubscriptionNoInternalTopics() {
        testPatternSubscription(false);
    }

    @Test
    public void testPatternSubscriptionIncludeInternalTopics() {
        testPatternSubscription(true);
    }

    @Test
    public void testUserAssignment() {
        subscription.assignFromUser(Utils.mkSet(new TopicPartition("foo", 0), new TopicPartition("bar", 0), new TopicPartition("__consumer_offsets", 0)));
        testBasicSubscription(Utils.mkSet("foo", "bar"), Utils.mkSet("__consumer_offsets"));
    }

    @Test
    public void testNormalSubscription() {
        subscription.subscribe(Utils.mkSet("foo", "bar", "__consumer_offsets"), new NoOpConsumerRebalanceListener());
        subscription.groupSubscribe(Utils.mkSet("baz"));
        testBasicSubscription(Utils.mkSet("foo", "bar", "baz"), Utils.mkSet("__consumer_offsets"));
    }

    @Test
    public void testTransientTopics() {
        subscription.subscribe(Collections.singleton("foo"), new NoOpConsumerRebalanceListener());
        ConsumerMetadata metadata = newConsumerMetadata(false);
        metadata.update(TestUtils.metadataUpdateWith(1, Collections.singletonMap("foo", 1)), time.milliseconds());
        Assert.assertFalse(metadata.updateRequested());
        metadata.addTransientTopics(Collections.singleton("foo"));
        Assert.assertFalse(metadata.updateRequested());
        metadata.addTransientTopics(Collections.singleton("bar"));
        Assert.assertTrue(metadata.updateRequested());
        Map<String, Integer> topicPartitionCounts = new HashMap<>();
        topicPartitionCounts.put("foo", 1);
        topicPartitionCounts.put("bar", 1);
        metadata.update(TestUtils.metadataUpdateWith(1, topicPartitionCounts), time.milliseconds());
        Assert.assertFalse(metadata.updateRequested());
        Assert.assertEquals(Utils.mkSet("foo", "bar"), new java.util.HashSet(metadata.fetch().topics()));
        metadata.clearTransientTopics();
        metadata.update(TestUtils.metadataUpdateWith(1, topicPartitionCounts), time.milliseconds());
        Assert.assertEquals(Collections.singleton("foo"), new java.util.HashSet(metadata.fetch().topics()));
    }
}

