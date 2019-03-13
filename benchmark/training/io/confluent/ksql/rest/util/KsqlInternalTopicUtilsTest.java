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
package io.confluent.ksql.rest.util;


import KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY;
import TopicConfig.CLEANUP_POLICY_CONFIG;
import TopicConfig.CLEANUP_POLICY_DELETE;
import TopicConfig.RETENTION_MS_CONFIG;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.confluent.ksql.exception.KafkaTopicExistsException;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KsqlInternalTopicUtilsTest {
    private static final String TOPIC_NAME = "topic";

    private static final short NREPLICAS = 2;

    private final Map<String, ?> commandTopicConfig = ImmutableMap.of(RETENTION_MS_CONFIG, Long.MAX_VALUE, CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_DELETE);

    @Mock
    private KafkaTopicClient topicClient;

    @Mock
    private KsqlConfig ksqlConfig;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldCreateInternalTopicIfItDoesNotExist() {
        // When:
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
        // Then:
        Mockito.verify(topicClient).createTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, 1, KsqlInternalTopicUtilsTest.NREPLICAS, commandTopicConfig);
    }

    @Test
    public void shouldNotAttemptToCreateInternalTopicIfItExists() {
        // Given:
        whenTopicExistsWith(1, KsqlInternalTopicUtilsTest.NREPLICAS);
        // When:
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
        // Then:
        Mockito.verify(topicClient, Mockito.never()).createTopic(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyMap());
    }

    @Test
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public void shouldEnsureInternalTopicHasInfiniteRetention() {
        // Given:
        final Map<String, Object> retentionConfig = ImmutableMap.of(RETENTION_MS_CONFIG, Long.MAX_VALUE);
        whenTopicExistsWith(1, KsqlInternalTopicUtilsTest.NREPLICAS);
        // When:
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
        // Then:
        Mockito.verify(topicClient).addTopicConfig(KsqlInternalTopicUtilsTest.TOPIC_NAME, retentionConfig);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldCreateInternalTopicWithNumReplicasFromConfig() {
        // Given:
        Mockito.when(ksqlConfig.getShort(SINK_NUMBER_OF_REPLICAS_PROPERTY)).thenReturn(((short) (3)));
        // When:
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
        // Then:
        Mockito.verify(topicClient).createTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, 1, ((short) (3)), commandTopicConfig);
    }

    @Test
    public void shouldFailIfTopicExistsOnCreationWithDifferentConfigs() {
        // Given:
        Mockito.doThrow(new KafkaTopicExistsException("exists")).when(topicClient).createTopic(ArgumentMatchers.any(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort(), ArgumentMatchers.anyMap());
        // When/Then:
        expectedException.expect(KafkaTopicExistsException.class);
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
    }

    @Test
    public void shouldFailIfTopicExistsWithInvalidNPartitions() {
        // Given:
        whenTopicExistsWith(2, KsqlInternalTopicUtilsTest.NREPLICAS);
        // When/Then:
        expectedException.expect(IllegalStateException.class);
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
    }

    @Test
    public void shouldFailIfTopicExistsWithInvalidNReplicas() {
        // Given:
        whenTopicExistsWith(1, 1);
        // When/Then:
        expectedException.expect(IllegalStateException.class);
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
    }

    @Test
    public void hsouldNotFailIfTopicIsOverreplicated() {
        // Given:
        whenTopicExistsWith(1, ((KsqlInternalTopicUtilsTest.NREPLICAS) + 1));
        // When/Then (no error):
        KsqlInternalTopicUtils.ensureTopic(KsqlInternalTopicUtilsTest.TOPIC_NAME, ksqlConfig, topicClient);
    }
}

