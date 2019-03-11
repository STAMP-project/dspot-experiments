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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class PatternTopicFilterTest {
    private KafkaConsumer<?, ?> consumerMock;

    @Test
    public void testFilter() {
        Pattern pattern = Pattern.compile("test-\\d+");
        PatternTopicFilter filter = new PatternTopicFilter(pattern);
        String matchingTopicOne = "test-1";
        String matchingTopicTwo = "test-11";
        String unmatchedTopic = "unmatched";
        Map<String, List<PartitionInfo>> allTopics = new HashMap<>();
        allTopics.put(matchingTopicOne, Collections.singletonList(createPartitionInfo(matchingTopicOne, 0)));
        List<PartitionInfo> testTwoPartitions = new ArrayList<>();
        testTwoPartitions.add(createPartitionInfo(matchingTopicTwo, 0));
        testTwoPartitions.add(createPartitionInfo(matchingTopicTwo, 1));
        allTopics.put(matchingTopicTwo, testTwoPartitions);
        allTopics.put(unmatchedTopic, Collections.singletonList(createPartitionInfo(unmatchedTopic, 0)));
        Mockito.when(consumerMock.listTopics()).thenReturn(allTopics);
        Set<TopicPartition> matchedPartitions = filter.getAllSubscribedPartitions(consumerMock);
        Assert.assertThat("Expected topic partitions matching the pattern to be passed by the filter", matchedPartitions, Matchers.containsInAnyOrder(new TopicPartition(matchingTopicOne, 0), new TopicPartition(matchingTopicTwo, 0), new TopicPartition(matchingTopicTwo, 1)));
    }
}

