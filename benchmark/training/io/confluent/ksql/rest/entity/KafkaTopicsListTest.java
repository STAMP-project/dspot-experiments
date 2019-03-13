/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.rest.entity;


import JsonMapper.INSTANCE;
import KafkaConsumerGroupClientImpl.ConsumerGroupSummary;
import KafkaConsumerGroupClientImpl.ConsumerSummary;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.metastore.KsqlTopic;
import io.confluent.ksql.util.KafkaConsumerGroupClient;
import io.confluent.ksql.util.KafkaConsumerGroupClientImpl;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class KafkaTopicsListTest {
    @Test
    public void shouldBuildValidTopicList() {
        final Collection<KsqlTopic> ksqlTopics = Collections.emptyList();
        // represent the full list of topics
        final Map<String, TopicDescription> topicDescriptions = new HashMap<>();
        final TopicPartitionInfo topicPartitionInfo = new TopicPartitionInfo(1, new Node(1, "", 8088), Collections.emptyList(), Collections.emptyList());
        topicDescriptions.put("test-topic", new TopicDescription("test-topic", false, Collections.singletonList(topicPartitionInfo)));
        /**
         * Return POJO for consumerGroupClient
         */
        final TopicPartition topicPartition = new TopicPartition("test-topic", 1);
        final KafkaConsumerGroupClientImpl.ConsumerSummary consumerSummary = new KafkaConsumerGroupClientImpl.ConsumerSummary("consumer-id");
        consumerSummary.addPartition(topicPartition);
        final KafkaConsumerGroupClientImpl.ConsumerGroupSummary consumerGroupSummary = new KafkaConsumerGroupClientImpl.ConsumerGroupSummary(Collections.singleton(consumerSummary));
        final KafkaConsumerGroupClient consumerGroupClient = mock(KafkaConsumerGroupClient.class);
        expect(consumerGroupClient.listGroups()).andReturn(Collections.singletonList("test-topic"));
        expect(consumerGroupClient.describeConsumerGroup("test-topic")).andReturn(consumerGroupSummary);
        replay(consumerGroupClient);
        /**
         * Test
         */
        final KafkaTopicsList topicsList = KafkaTopicsList.build("statement test", ksqlTopics, topicDescriptions, new KsqlConfig(Collections.EMPTY_MAP), consumerGroupClient);
        MatcherAssert.assertThat(topicsList.getTopics().size(), CoreMatchers.equalTo(1));
        final KafkaTopicInfo first = topicsList.getTopics().iterator().next();
        MatcherAssert.assertThat(first.getConsumerGroupCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(first.getConsumerCount(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(first.getReplicaInfo().size(), CoreMatchers.equalTo(1));
    }

    @Test
    public void testSerde() throws Exception {
        final ObjectMapper mapper = INSTANCE.mapper;
        final KafkaTopicsList expected = new KafkaTopicsList("SHOW TOPICS;", ImmutableList.of(new KafkaTopicInfo("thetopic", true, ImmutableList.of(1, 2, 3), 42, 12)));
        final String json = mapper.writeValueAsString(expected);
        Assert.assertEquals(("{\"@type\":\"kafka_topics\",\"statementText\":\"SHOW TOPICS;\"," + (("\"topics\":[{\"name\":\"thetopic\",\"registered\":true," + "\"replicaInfo\":[1,2,3],\"consumerCount\":42,") + "\"consumerGroupCount\":12}]}")), json);
        final KafkaTopicsList actual = mapper.readValue(json, KafkaTopicsList.class);
        Assert.assertEquals(expected, actual);
    }
}

