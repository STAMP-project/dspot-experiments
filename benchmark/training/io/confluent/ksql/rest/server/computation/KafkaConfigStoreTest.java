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
package io.confluent.ksql.rest.server.computation;


import KafkaConfigStore.CONFIG_MSG_KEY;
import KsqlConfig.KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.rest.server.computation.ConfigTopicKey.StringKey;
import io.confluent.ksql.rest.server.computation.KafkaConfigStore.KsqlProperties;
import io.confluent.ksql.rest.util.InternalTopicJsonSerdeUtil;
import io.confluent.ksql.util.KsqlConfig;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class KafkaConfigStoreTest {
    private static final String TOPIC_NAME = "topic";

    private final KsqlConfig currentConfig = new KsqlConfig(ImmutableMap.of(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG, "current"));

    private final KsqlConfig savedConfig = new KsqlConfig(ImmutableMap.of(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG, "saved"));

    private final KsqlConfig badConfig = new KsqlConfig(ImmutableMap.of(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG, "bad"));

    private final KsqlProperties properties = new KsqlProperties(KafkaConfigStoreTest.filterNullValues(currentConfig.getAllConfigPropsWithSecretsObfuscated()));

    private final KsqlProperties savedProperties = new KsqlProperties(KafkaConfigStoreTest.filterNullValues(savedConfig.getAllConfigPropsWithSecretsObfuscated()));

    private final KsqlProperties badProperties = new KsqlProperties(KafkaConfigStoreTest.filterNullValues(badConfig.getAllConfigPropsWithSecretsObfuscated()));

    private final TopicPartition topicPartition = new TopicPartition(KafkaConfigStoreTest.TOPIC_NAME, 0);

    private final List<TopicPartition> topicPartitionAsList = Collections.singletonList(topicPartition);

    private final List<ConsumerRecords<byte[], byte[]>> log = new LinkedList<>();

    private final Serializer<StringKey> keySerializer = InternalTopicJsonSerdeUtil.getJsonSerializer(true);

    private final Serializer<KsqlProperties> serializer = InternalTopicJsonSerdeUtil.getJsonSerializer(false);

    @Mock
    private KafkaConsumer<byte[], byte[]> consumerBefore;

    @Mock
    private KafkaConsumer<byte[], byte[]> consumerAfter;

    @Mock
    private Supplier<KafkaConsumer<byte[], byte[]>> consumerSupplier;

    @Mock
    private Supplier<KafkaProducer<StringKey, KsqlProperties>> producerSupplier;

    @Mock
    private KafkaProducer<StringKey, KsqlProperties> producer;

    @Mock
    private KsqlConfig currentConfigProxy;

    @Mock
    private KsqlConfig mergedConfig;

    private InOrder inOrder;

    @Test
    public void shouldIgnoreRecordsWithDifferentKey() {
        // Given:
        addPollResult("foo", "val".getBytes(StandardCharsets.UTF_8));
        addPollResult(CONFIG_MSG_KEY, serializer.serialize("", savedProperties));
        expectRead(consumerBefore);
        // When:
        getKsqlConfig();
        // Then:
        verifyDrainLog(consumerBefore, 2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldIgnoreRecordsWithUnparseableKey() {
        // Given:
        addPollResult("badkey".getBytes(StandardCharsets.UTF_8), "whocares".getBytes(StandardCharsets.UTF_8));
        addPollResult(CONFIG_MSG_KEY, serializer.serialize("", savedProperties));
        expectRead(consumerBefore);
        // When:
        getKsqlConfig();
        // Then:
        verifyDrainLog(consumerBefore, 2);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldIgnoreRecordsWithDifferentKeyWithinPoll() {
        // Given:
        addPollResult("foo", "val".getBytes(StandardCharsets.UTF_8), CONFIG_MSG_KEY, serializer.serialize("", savedProperties));
        expectRead(consumerBefore);
        // When:
        getKsqlConfig();
        // Then:
        verifyDrainLog(consumerBefore, 1);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldPollToEndOfTopic() {
        // Given:
        addPollResult("foo", "val".getBytes(StandardCharsets.UTF_8));
        addPollResult("bar", "baz".getBytes(StandardCharsets.UTF_8));
        expectRead(consumerBefore);
        addPollResult(CONFIG_MSG_KEY, savedProperties);
        expectRead(consumerAfter);
        // When:
        getKsqlConfig();
        // Then:
        verifyDrainLog(consumerBefore, 2);
        verifyProduce();
    }

    @Test
    public void shouldWriteConfigIfNoConfigWritten() {
        // Given:
        expectRead(consumerBefore);
        addPollResult(CONFIG_MSG_KEY, properties);
        expectRead(consumerAfter);
        // When:
        getKsqlConfig();
        // Then:
        verifyDrainLog(consumerBefore, 0);
        verifyProduce();
    }

    @Test
    public void shouldUseFirstPolledConfig() {
        // Given:
        addPollResult(CONFIG_MSG_KEY, savedProperties, badProperties);
        expectRead(consumerBefore);
        // When:
        final KsqlConfig mergedConfig = getKsqlConfig();
        // Then:
        verifyMergedConfig(mergedConfig);
    }

    @Test
    public void shouldNotWriteConfigIfExists() {
        // Given:
        addPollResult(CONFIG_MSG_KEY, savedProperties);
        expectRead(consumerBefore);
        // When:
        getKsqlConfig();
        // Then:
        Mockito.verifyNoMoreInteractions(producer);
    }

    @Test
    public void shouldReadConfigAfterWrite() {
        // Given:
        expectRead(consumerBefore);
        addPollResult(CONFIG_MSG_KEY, savedProperties, properties);
        expectRead(consumerAfter);
        // When:
        final KsqlConfig mergedConfig = getKsqlConfig();
        // Then:
        verifyDrainLog(consumerBefore, 0);
        verifyProduce();
        verifyDrainLog(consumerAfter, 1);
        verifyMergedConfig(mergedConfig);
    }

    @Test
    public void shouldMergeExistingConfigIfExists() {
        // Given:
        addPollResult(CONFIG_MSG_KEY, savedProperties);
        expectRead(consumerBefore);
        // When:
        final KsqlConfig mergedConfig = getKsqlConfig();
        // Then:
        verifyMergedConfig(mergedConfig);
    }

    @Test
    public void shouldDeserializeEmptyContentsToEmptyProps() {
        // When:
        final Deserializer<KafkaConfigStore.KsqlProperties> deserializer = InternalTopicJsonSerdeUtil.getJsonDeserializer(KsqlProperties.class, false);
        final KafkaConfigStore.KsqlProperties ksqlProperties = deserializer.deserialize(KafkaConfigStoreTest.TOPIC_NAME, "{}".getBytes(StandardCharsets.UTF_8));
        // Then:
        Assert.assertThat(ksqlProperties.getKsqlProperties(), CoreMatchers.equalTo(Collections.emptyMap()));
    }
}

