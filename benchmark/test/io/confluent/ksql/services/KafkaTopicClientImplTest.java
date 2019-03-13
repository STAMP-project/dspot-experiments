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
package io.confluent.ksql.services;


import ConfigResource.Type;
import KsqlConstants.CONFLUENT_INTERNAL_TOPIC_PREFIX;
import KsqlConstants.KSQL_INTERNAL_TOPIC_PREFIX;
import TopicConfig.CLEANUP_POLICY_COMPACT;
import TopicConfig.CLEANUP_POLICY_CONFIG;
import TopicConfig.COMPRESSION_TYPE_CONFIG;
import TopicConfig.RETENTION_MS_CONFIG;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.exception.KafkaResponseGetFailedException;
import io.confluent.ksql.exception.KafkaTopicExistsException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.DisconnectException;
import org.apache.kafka.common.utils.Utils;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(EasyMockRunner.class)
public class KafkaTopicClientImplTest {
    private static final String topicName1 = "topic1";

    private static final String topicName2 = "topic2";

    private static final String topicName3 = "topic3";

    private static final String internalTopic1 = String.format("%s%s_%s", KSQL_INTERNAL_TOPIC_PREFIX, "default", ("query_CTAS_USERS_BY_CITY-KSTREAM-AGGREGATE" + "-STATE-STORE-0000000006-repartition"));

    private static final String internalTopic2 = String.format("%s%s_%s", KSQL_INTERNAL_TOPIC_PREFIX, "default", ("query_CTAS_USERS_BY_CITY-KSTREAM-AGGREGATE" + "-STATE-STORE-0000000006-changelog"));

    private static final String confluentInternalTopic = String.format("%s-%s", CONFLUENT_INTERNAL_TOPIC_PREFIX, "confluent-control-center");

    private Node node;

    @Mock
    private AdminClient adminClient;

    @Test
    public void shouldCreateTopic() {
        expect(adminClient.listTopics()).andReturn(getListTopicsResult());
        expect(adminClient.createTopics(anyObject())).andReturn(getCreateTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.createTopic("test", 1, ((short) (1)));
        verify(adminClient);
    }

    @Test
    public void shouldUseExistingTopicWithTheSameSpecsInsteadOfCreate() {
        expect(adminClient.listTopics()).andReturn(getListTopicsResult());
        expect(adminClient.describeTopics(anyObject())).andReturn(getDescribeTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.createTopic(KafkaTopicClientImplTest.topicName1, 1, ((short) (1)));
        verify(adminClient);
    }

    @Test(expected = KafkaTopicExistsException.class)
    public void shouldFailCreateExistingTopic() {
        expect(adminClient.createTopics(anyObject())).andReturn(getCreateTopicsResult());
        expect(adminClient.listTopics()).andReturn(getListTopicsResult());
        expect(adminClient.describeTopics(anyObject())).andReturn(getDescribeTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.createTopic(KafkaTopicClientImplTest.topicName1, 1, ((short) (2)));
        verify(adminClient);
    }

    @Test
    public void shouldNotFailIfTopicAlreadyExistsWhenCreating() {
        expect(adminClient.listTopics()).andReturn(getEmptyListTopicResult());
        expect(adminClient.createTopics(anyObject())).andReturn(createTopicReturningTopicExistsException());
        expect(adminClient.describeTopics(anyObject())).andReturn(getDescribeTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.createTopic(KafkaTopicClientImplTest.topicName1, 1, ((short) (1)));
        verify(adminClient);
    }

    @Test
    public void shouldRetryDescribeTopicOnRetriableException() {
        expect(adminClient.listTopics()).andReturn(getEmptyListTopicResult());
        expect(adminClient.createTopics(anyObject())).andReturn(createTopicReturningTopicExistsException());
        expect(adminClient.describeTopics(anyObject())).andReturn(KafkaTopicClientImplTest.describeTopicReturningUnknownPartitionException()).once();
        // The second time, return the right response.
        expect(adminClient.describeTopics(anyObject())).andReturn(getDescribeTopicsResult()).once();
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.createTopic(KafkaTopicClientImplTest.topicName1, 1, ((short) (1)));
        verify(adminClient);
    }

    @Test(expected = KafkaResponseGetFailedException.class)
    public void shouldFailToDescribeTopicsWhenRetriesExpire() {
        expect(adminClient.listTopics()).andReturn(getEmptyListTopicResult());
        expect(adminClient.describeTopics(anyObject())).andReturn(KafkaTopicClientImplTest.describeTopicReturningUnknownPartitionException()).andReturn(KafkaTopicClientImplTest.describeTopicReturningUnknownPartitionException()).andReturn(KafkaTopicClientImplTest.describeTopicReturningUnknownPartitionException()).andReturn(KafkaTopicClientImplTest.describeTopicReturningUnknownPartitionException()).andReturn(KafkaTopicClientImplTest.describeTopicReturningUnknownPartitionException());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.describeTopics(Collections.singleton(KafkaTopicClientImplTest.topicName1));
        verify(adminClient);
    }

    @Test
    public void shouldRetryListTopics() {
        expect(adminClient.listTopics()).andReturn(listTopicResultWithNotControllerException()).once();
        expect(adminClient.listTopics()).andReturn(getListTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final Set<String> names = kafkaTopicClient.listTopicNames();
        MatcherAssert.assertThat(names, CoreMatchers.equalTo(Utils.mkSet(KafkaTopicClientImplTest.topicName1, KafkaTopicClientImplTest.topicName2, KafkaTopicClientImplTest.topicName3)));
        verify(adminClient);
    }

    @Test
    public void shouldFilterInternalTopics() {
        expect(adminClient.listTopics()).andReturn(getListTopicsResultWithInternalTopics());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final Set<String> names = kafkaTopicClient.listNonInternalTopicNames();
        MatcherAssert.assertThat(names, CoreMatchers.equalTo(Utils.mkSet(KafkaTopicClientImplTest.topicName1, KafkaTopicClientImplTest.topicName2, KafkaTopicClientImplTest.topicName3)));
        verify(adminClient);
    }

    @Test
    public void shouldListTopicNames() {
        expect(adminClient.listTopics()).andReturn(getListTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final Set<String> names = kafkaTopicClient.listTopicNames();
        MatcherAssert.assertThat(names, CoreMatchers.equalTo(Utils.mkSet(KafkaTopicClientImplTest.topicName1, KafkaTopicClientImplTest.topicName2, KafkaTopicClientImplTest.topicName3)));
        verify(adminClient);
    }

    @Test
    public void shouldDeleteTopics() {
        expect(adminClient.deleteTopics(anyObject())).andReturn(getDeleteTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final List<String> topics = Collections.singletonList(KafkaTopicClientImplTest.topicName2);
        kafkaTopicClient.deleteTopics(topics);
        verify(adminClient);
    }

    @Test
    public void shouldReturnIfDeleteTopicsIsEmpty() {
        // Given:
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        // When:
        kafkaTopicClient.deleteTopics(Collections.emptyList());
        verify(adminClient);
    }

    @Test
    public void shouldDeleteInternalTopics() {
        expect(adminClient.listTopics()).andReturn(getListTopicsResultWithInternalTopics());
        expect(adminClient.deleteTopics(Arrays.asList(KafkaTopicClientImplTest.internalTopic2, KafkaTopicClientImplTest.internalTopic1))).andReturn(getDeleteInternalTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final String applicationId = String.format("%s%s", KSQL_INTERNAL_TOPIC_PREFIX, "default_query_CTAS_USERS_BY_CITY");
        kafkaTopicClient.deleteInternalTopics(applicationId);
        verify(adminClient);
    }

    @Test
    public void shouldDeleteTopicsIfDeleteTopicEnableTrue() {
        // Given:
        givenDeleteTopicEnableTrue();
        expect(adminClient.deleteTopics(anyObject())).andReturn(getDeleteTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        // When:
        kafkaTopicClient.deleteTopics(Collections.singletonList(KafkaTopicClientImplTest.topicName2));
        // Then:
        verify(adminClient);
    }

    @Test
    public void shouldDeleteTopicsIfBrokerDoesNotReturnValueForDeleteTopicEnable() {
        // Given:
        givenDeleteTopicEnableNotReturnedByBroker();
        expect(adminClient.deleteTopics(anyObject())).andReturn(getDeleteTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        // When:
        kafkaTopicClient.deleteTopics(Collections.singletonList(KafkaTopicClientImplTest.topicName2));
        // Then:
        verify(adminClient);
    }

    @Test
    public void shouldNotDeleteTopicIfDeleteTopicEnableFalse() {
        // Given:
        givenDeleteTopicEnableFalse();
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        // When:
        kafkaTopicClient.deleteTopics(Collections.singletonList(KafkaTopicClientImplTest.topicName2));
        // Then:
        verify(adminClient);
    }

    @Test
    public void shouldGetTopicConfig() {
        expect(adminClient.describeConfigs(KafkaTopicClientImplTest.topicConfigsRequest("fred"))).andReturn(KafkaTopicClientImplTest.topicConfigResponse("fred", overriddenConfigEntry(RETENTION_MS_CONFIG, "12345"), defaultConfigEntry(COMPRESSION_TYPE_CONFIG, "snappy")));
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final Map<String, String> config = kafkaTopicClient.getTopicConfig("fred");
        MatcherAssert.assertThat(config.get(RETENTION_MS_CONFIG), Matchers.is("12345"));
        MatcherAssert.assertThat(config.get(COMPRESSION_TYPE_CONFIG), Matchers.is("snappy"));
    }

    @Test(expected = KafkaResponseGetFailedException.class)
    public void shouldThrowOnNoneRetriableGetTopicConfigError() {
        expect(adminClient.describeConfigs(anyObject())).andReturn(KafkaTopicClientImplTest.topicConfigResponse(new RuntimeException()));
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final Map<String, String> config = kafkaTopicClient.getTopicConfig("fred");
        MatcherAssert.assertThat(config.get(RETENTION_MS_CONFIG), Matchers.is("12345"));
        MatcherAssert.assertThat(config.get(COMPRESSION_TYPE_CONFIG), Matchers.is("snappy"));
    }

    @Test
    public void shouldHandleRetriableGetTopicConfigError() {
        expect(adminClient.describeConfigs(anyObject())).andReturn(KafkaTopicClientImplTest.topicConfigResponse(new DisconnectException())).andReturn(KafkaTopicClientImplTest.topicConfigResponse("fred", overriddenConfigEntry(RETENTION_MS_CONFIG, "12345"), defaultConfigEntry(COMPRESSION_TYPE_CONFIG, "producer")));
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        final Map<String, String> config = kafkaTopicClient.getTopicConfig("fred");
        MatcherAssert.assertThat(config.get(RETENTION_MS_CONFIG), Matchers.is("12345"));
        MatcherAssert.assertThat(config.get(COMPRESSION_TYPE_CONFIG), Matchers.is("producer"));
    }

    @Test
    public void shouldSetTopicCleanupPolicyToCompact() {
        expect(adminClient.listTopics()).andReturn(getEmptyListTopicResult());
        // Verify that the new topic configuration being passed to the admin client is what we expect.
        final NewTopic newTopic = new NewTopic(KafkaTopicClientImplTest.topicName1, 1, ((short) (1)));
        newTopic.configs(Collections.singletonMap("cleanup.policy", "compact"));
        expect(adminClient.createTopics(KafkaTopicClientImplTest.singleNewTopic(newTopic))).andReturn(getCreateTopicsResult());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.createTopic(KafkaTopicClientImplTest.topicName1, 1, ((short) (1)), Collections.singletonMap("cleanup.policy", "compact"));
        verify(adminClient);
    }

    @Test
    public void shouldAddTopicConfig() {
        final Map<String, ?> overrides = ImmutableMap.of(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT);
        expect(adminClient.describeConfigs(KafkaTopicClientImplTest.topicConfigsRequest("peter"))).andReturn(KafkaTopicClientImplTest.topicConfigResponse("peter", overriddenConfigEntry(RETENTION_MS_CONFIG, "12345"), defaultConfigEntry(COMPRESSION_TYPE_CONFIG, "snappy")));
        expect(adminClient.alterConfigs(KafkaTopicClientImplTest.withResourceConfig(new org.apache.kafka.common.config.ConfigResource(Type.TOPIC, "peter"), new org.apache.kafka.clients.admin.ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, "12345"), new org.apache.kafka.clients.admin.ConfigEntry(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT)))).andReturn(KafkaTopicClientImplTest.alterTopicConfigResponse());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.addTopicConfig("peter", overrides);
        verify(adminClient);
    }

    @Test
    public void shouldNotAlterConfigIfConfigNotChanged() {
        final Map<String, ?> overrides = ImmutableMap.of(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT);
        expect(adminClient.describeConfigs(KafkaTopicClientImplTest.topicConfigsRequest("peter"))).andReturn(KafkaTopicClientImplTest.topicConfigResponse("peter", overriddenConfigEntry(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT)));
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.addTopicConfig("peter", overrides);
        verify(adminClient);
    }

    @Test
    public void shouldRetryAddingTopicConfig() {
        final Map<String, ?> overrides = ImmutableMap.of(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT);
        expect(adminClient.describeConfigs(anyObject())).andReturn(KafkaTopicClientImplTest.topicConfigResponse("peter", overriddenConfigEntry(RETENTION_MS_CONFIG, "12345"), defaultConfigEntry(COMPRESSION_TYPE_CONFIG, "snappy")));
        expect(adminClient.alterConfigs(anyObject())).andReturn(KafkaTopicClientImplTest.alterTopicConfigResponse(new DisconnectException())).andReturn(KafkaTopicClientImplTest.alterTopicConfigResponse());
        replay(adminClient);
        final KafkaTopicClient kafkaTopicClient = new KafkaTopicClientImpl(adminClient);
        kafkaTopicClient.addTopicConfig("peter", overrides);
        verify(adminClient);
    }
}

