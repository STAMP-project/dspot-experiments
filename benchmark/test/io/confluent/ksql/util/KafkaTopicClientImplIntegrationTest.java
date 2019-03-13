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
package io.confluent.ksql.util;


import KafkaTopicClient.TopicCleanupPolicy.DELETE;
import TopicConfig.CLEANUP_POLICY_CONFIG;
import TopicConfig.COMPRESSION_TYPE_CONFIG;
import TopicConfig.RETENTION_MS_CONFIG;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.exception.KafkaResponseGetFailedException;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.test.util.EmbeddedSingleNodeKafkaCluster;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.test.IntegrationTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;


@Category({ IntegrationTest.class })
public class KafkaTopicClientImplIntegrationTest {
    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster KAFKA = EmbeddedSingleNodeKafkaCluster.newBuilder().build();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private String testTopic;

    private KafkaTopicClient client;

    private AdminClient adminClient;

    @Test
    public void shouldGetTopicConfig() {
        // When:
        final Map<String, String> config = client.getTopicConfig(testTopic);
        // Then:
        MatcherAssert.assertThat(config.keySet(), Matchers.hasItems(RETENTION_MS_CONFIG, CLEANUP_POLICY_CONFIG, COMPRESSION_TYPE_CONFIG));
    }

    @Test
    public void shouldSetTopicConfig() {
        // When:
        final boolean changed = client.addTopicConfig(testTopic, ImmutableMap.of(RETENTION_MS_CONFIG, "1245678"));
        // Then:
        MatcherAssert.assertThat(changed, Matchers.is(true));
        assertThatEventually(() -> getTopicConfig(TopicConfig.RETENTION_MS_CONFIG), Matchers.is("1245678"));
    }

    @Test
    public void shouldNotSetTopicConfigWhenNothingChanged() {
        // Given:
        client.addTopicConfig(testTopic, ImmutableMap.of(RETENTION_MS_CONFIG, "56784567"));
        // When:
        final boolean changed = client.addTopicConfig(testTopic, ImmutableMap.of(RETENTION_MS_CONFIG, "56784567"));
        // Then:
        MatcherAssert.assertThat(changed, Matchers.is(false));
    }

    @Test
    public void shouldNotRemovePreviousOverridesWhenAddingNew() {
        // Given:
        client.addTopicConfig(testTopic, ImmutableMap.of(COMPRESSION_TYPE_CONFIG, "snappy"));
        // When:
        client.addTopicConfig(testTopic, ImmutableMap.of(RETENTION_MS_CONFIG, "987654321"));
        // Then:
        assertThatEventually(() -> getTopicConfig(TopicConfig.RETENTION_MS_CONFIG), Matchers.is("987654321"));
        MatcherAssert.assertThat(getTopicConfig(COMPRESSION_TYPE_CONFIG), Matchers.is("snappy"));
    }

    @Test
    public void shouldGetTopicCleanupPolicy() {
        // Given:
        client.addTopicConfig(testTopic, ImmutableMap.of(CLEANUP_POLICY_CONFIG, "delete"));
        // Then:
        assertThatEventually(() -> client.getTopicCleanupPolicy(testTopic), Matchers.is(DELETE));
    }

    @Test
    public void shouldListTopics() {
        // When:
        final Set<String> topicNames = client.listTopicNames();
        // Then:
        MatcherAssert.assertThat(topicNames, Matchers.hasItem(testTopic));
    }

    @Test
    public void shouldDetectIfTopicExists() {
        MatcherAssert.assertThat(client.isTopicExists(testTopic), Matchers.is(true));
        MatcherAssert.assertThat(client.isTopicExists("Unknown"), Matchers.is(false));
    }

    @Test
    public void shouldDeleteTopics() {
        // When:
        client.deleteTopics(Collections.singletonList(testTopic));
        // Then:
        MatcherAssert.assertThat(client.isTopicExists(testTopic), Matchers.is(false));
    }

    @Test
    public void shouldCreateTopic() {
        // Given:
        final String topicName = UUID.randomUUID().toString();
        // When:
        client.createTopic(topicName, 3, ((short) (1)));
        // Then:
        assertThatEventually(() -> topicExists(topicName), Matchers.is(true));
        final TopicDescription topicDescription = getTopicDescription(topicName);
        MatcherAssert.assertThat(topicDescription.partitions(), Matchers.hasSize(3));
        MatcherAssert.assertThat(topicDescription.partitions().get(0).replicas(), Matchers.hasSize(1));
    }

    @Test
    public void shouldCreateTopicWithConfig() {
        // Given:
        final String topicName = UUID.randomUUID().toString();
        final Map<String, String> config = ImmutableMap.of(COMPRESSION_TYPE_CONFIG, "snappy");
        // When:
        client.createTopic(topicName, 2, ((short) (1)), config);
        // Then:
        assertThatEventually(() -> topicExists(topicName), Matchers.is(true));
        final TopicDescription topicDescription = getTopicDescription(topicName);
        MatcherAssert.assertThat(topicDescription.partitions(), Matchers.hasSize(2));
        MatcherAssert.assertThat(topicDescription.partitions().get(0).replicas(), Matchers.hasSize(1));
        final Map<String, String> configs = client.getTopicConfig(topicName);
        MatcherAssert.assertThat(configs.get(COMPRESSION_TYPE_CONFIG), Matchers.is("snappy"));
    }

    @Test
    public void shouldThrowOnDescribeIfTopicDoesNotExist() {
        // Expect
        expectedException.expect(KafkaResponseGetFailedException.class);
        expectedException.expectMessage("Failed to Describe Kafka Topic(s):");
        expectedException.expectMessage("i_do_not_exist");
        // When:
        client.describeTopic("i_do_not_exist");
    }
}

