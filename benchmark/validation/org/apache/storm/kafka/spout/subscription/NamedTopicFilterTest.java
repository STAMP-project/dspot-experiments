/**
 * Copyright 2017 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka.spout.subscription;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class NamedTopicFilterTest {
    private KafkaConsumer<?, ?> consumerMock;

    @Test
    public void testFilter() {
        String matchingTopicOne = "test-1";
        String matchingTopicTwo = "test-11";
        String unmatchedTopic = "unmatched";
        NamedTopicFilter filter = new NamedTopicFilter(matchingTopicOne, matchingTopicTwo);
        Mockito.when(consumerMock.partitionsFor(matchingTopicOne)).thenReturn(Collections.singletonList(createPartitionInfo(matchingTopicOne, 0)));
        List<PartitionInfo> partitionTwoPartitions = new ArrayList<>();
        partitionTwoPartitions.add(createPartitionInfo(matchingTopicTwo, 0));
        partitionTwoPartitions.add(createPartitionInfo(matchingTopicTwo, 1));
        Mockito.when(consumerMock.partitionsFor(matchingTopicTwo)).thenReturn(partitionTwoPartitions);
        Mockito.when(consumerMock.partitionsFor(unmatchedTopic)).thenReturn(Collections.singletonList(createPartitionInfo(unmatchedTopic, 0)));
        Set<TopicPartition> matchedPartitions = filter.getAllSubscribedPartitions(consumerMock);
        Assert.assertThat("Expected filter to pass only topics with exact name matches", matchedPartitions, Matchers.containsInAnyOrder(new TopicPartition(matchingTopicOne, 0), new TopicPartition(matchingTopicTwo, 0), new TopicPartition(matchingTopicTwo, 1)));
    }

    @Test
    public void testFilterOnAbsentTopic() {
        String presentTopic = "present";
        String absentTopic = "absent";
        NamedTopicFilter filter = new NamedTopicFilter(presentTopic, absentTopic);
        Mockito.when(consumerMock.partitionsFor(presentTopic)).thenReturn(Collections.singletonList(createPartitionInfo(presentTopic, 2)));
        Mockito.when(consumerMock.partitionsFor(absentTopic)).thenReturn(null);
        Set<TopicPartition> presentPartitions = filter.getAllSubscribedPartitions(consumerMock);
        Assert.assertThat("Expected filter to pass only topics which are present", presentPartitions, Matchers.contains(new TopicPartition(presentTopic, 2)));
    }
}

