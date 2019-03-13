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
package org.apache.kafka.streams;


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import ConsumerConfig.GROUP_ID_CONFIG;
import ConsumerConfig.ISOLATION_LEVEL_CONFIG;
import ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import ConsumerConfig.METRICS_NUM_SAMPLES_CONFIG;
import ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG;
import ProducerConfig.BUFFER_MEMORY_CONFIG;
import ProducerConfig.CLIENT_ID_CONFIG;
import ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG;
import ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG;
import ProducerConfig.LINGER_MS_CONFIG;
import ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION;
import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.APPLICATION_SERVER_CONFIG;
import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG;
import StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG;
import StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG;
import StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG;
import StreamsConfig.PROCESSING_GUARANTEE_CONFIG;
import StreamsConfig.REPLICATION_FACTOR_CONFIG;
import StreamsConfig.RETRIES_CONFIG;
import StreamsConfig.RETRY_BACKOFF_MS_CONFIG;
import StreamsConfig.WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG;
import TopicConfig.SEGMENT_BYTES_CONFIG;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.requests.IsolationLevel;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.streams.errors.StreamsException;
import org.apache.kafka.streams.processor.FailOnInvalidTimestamp;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.processor.internals.StreamsPartitionAssignor;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class StreamsConfigTest {
    private final Properties props = new Properties();

    private StreamsConfig streamsConfig;

    @Test(expected = ConfigException.class)
    public void shouldThrowExceptionIfApplicationIdIsNotSet() {
        props.remove(APPLICATION_ID_CONFIG);
        new StreamsConfig(props);
    }

    @Test(expected = ConfigException.class)
    public void shouldThrowExceptionIfBootstrapServersIsNotSet() {
        props.remove(BOOTSTRAP_SERVERS_CONFIG);
        new StreamsConfig(props);
    }

    @Test
    public void testGetProducerConfigs() {
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getProducerConfigs(clientId);
        MatcherAssert.assertThat(returnedProps.get(CLIENT_ID_CONFIG), IsEqual.equalTo(clientId));
        MatcherAssert.assertThat(returnedProps.get(LINGER_MS_CONFIG), IsEqual.equalTo("100"));
    }

    @Test
    public void testGetConsumerConfigs() {
        final String groupId = "example-application";
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getMainConsumerConfigs(groupId, clientId);
        MatcherAssert.assertThat(returnedProps.get(ConsumerConfig.CLIENT_ID_CONFIG), IsEqual.equalTo(clientId));
        MatcherAssert.assertThat(returnedProps.get(GROUP_ID_CONFIG), IsEqual.equalTo(groupId));
        MatcherAssert.assertThat(returnedProps.get(MAX_POLL_RECORDS_CONFIG), IsEqual.equalTo("1000"));
    }

    @Test
    public void consumerConfigMustContainStreamPartitionAssignorConfig() {
        props.put(REPLICATION_FACTOR_CONFIG, 42);
        props.put(NUM_STANDBY_REPLICAS_CONFIG, 1);
        props.put(WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG, 7L);
        props.put(APPLICATION_SERVER_CONFIG, "dummy:host");
        props.put(RETRIES_CONFIG, 10);
        props.put(StreamsConfig.adminClientPrefix(RETRIES_CONFIG), 5);
        props.put(StreamsConfig.topicPrefix(SEGMENT_BYTES_CONFIG), 100);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final String groupId = "example-application";
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getMainConsumerConfigs(groupId, clientId);
        Assert.assertEquals(42, returnedProps.get(REPLICATION_FACTOR_CONFIG));
        Assert.assertEquals(1, returnedProps.get(NUM_STANDBY_REPLICAS_CONFIG));
        Assert.assertEquals(StreamsPartitionAssignor.class.getName(), returnedProps.get(PARTITION_ASSIGNMENT_STRATEGY_CONFIG));
        Assert.assertEquals(7L, returnedProps.get(WINDOW_STORE_CHANGE_LOG_ADDITIONAL_RETENTION_MS_CONFIG));
        Assert.assertEquals("dummy:host", returnedProps.get(APPLICATION_SERVER_CONFIG));
        Assert.assertNull(returnedProps.get(RETRIES_CONFIG));
        Assert.assertEquals(5, returnedProps.get(StreamsConfig.adminClientPrefix(RETRIES_CONFIG)));
        Assert.assertEquals(100, returnedProps.get(StreamsConfig.topicPrefix(SEGMENT_BYTES_CONFIG)));
    }

    @Test
    public void consumerConfigShouldContainAdminClientConfigsForRetriesAndRetryBackOffMsWithAdminPrefix() {
        props.put(StreamsConfig.adminClientPrefix(RETRIES_CONFIG), 20);
        props.put(StreamsConfig.adminClientPrefix(RETRY_BACKOFF_MS_CONFIG), 200L);
        props.put(RETRIES_CONFIG, 10);
        props.put(RETRY_BACKOFF_MS_CONFIG, 100L);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final String groupId = "example-application";
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getMainConsumerConfigs(groupId, clientId);
        Assert.assertEquals(20, returnedProps.get(StreamsConfig.adminClientPrefix(RETRIES_CONFIG)));
        Assert.assertEquals(200L, returnedProps.get(StreamsConfig.adminClientPrefix(RETRY_BACKOFF_MS_CONFIG)));
    }

    @Test
    public void testGetMainConsumerConfigsWithMainConsumerOverridenPrefix() {
        props.put(StreamsConfig.consumerPrefix(MAX_POLL_RECORDS_CONFIG), "5");
        props.put(StreamsConfig.mainConsumerPrefix(MAX_POLL_RECORDS_CONFIG), "50");
        final String groupId = "example-application";
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getMainConsumerConfigs(groupId, clientId);
        Assert.assertEquals("50", returnedProps.get(MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    public void testGetRestoreConsumerConfigs() {
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getRestoreConsumerConfigs(clientId);
        Assert.assertEquals(returnedProps.get(ConsumerConfig.CLIENT_ID_CONFIG), clientId);
        Assert.assertNull(returnedProps.get(GROUP_ID_CONFIG));
    }

    @Test
    public void defaultSerdeShouldBeConfigured() {
        final Map<String, Object> serializerConfigs = new HashMap<>();
        serializerConfigs.put("key.serializer.encoding", "UTF8");
        serializerConfigs.put("value.serializer.encoding", "UTF-16");
        final Serializer<String> serializer = Serdes.String().serializer();
        final String str = "my string for testing";
        final String topic = "my topic";
        serializer.configure(serializerConfigs, true);
        Assert.assertEquals("Should get the original string after serialization and deserialization with the configured encoding", str, streamsConfig.defaultKeySerde().deserializer().deserialize(topic, serializer.serialize(topic, str)));
        serializer.configure(serializerConfigs, false);
        Assert.assertEquals("Should get the original string after serialization and deserialization with the configured encoding", str, streamsConfig.defaultValueSerde().deserializer().deserialize(topic, serializer.serialize(topic, str)));
    }

    @Test
    public void shouldSupportMultipleBootstrapServers() {
        final List<String> expectedBootstrapServers = Arrays.asList("broker1:9092", "broker2:9092");
        final String bootstrapServersString = Utils.join(expectedBootstrapServers, ",");
        final Properties props = new Properties();
        props.put(APPLICATION_ID_CONFIG, "irrelevant");
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServersString);
        final StreamsConfig config = new StreamsConfig(props);
        final List<String> actualBootstrapServers = config.getList(BOOTSTRAP_SERVERS_CONFIG);
        Assert.assertEquals(expectedBootstrapServers, actualBootstrapServers);
    }

    @Test
    public void shouldSupportPrefixedConsumerConfigs() {
        props.put(StreamsConfig.consumerPrefix(AUTO_OFFSET_RESET_CONFIG), "earliest");
        props.put(StreamsConfig.consumerPrefix(METRICS_NUM_SAMPLES_CONFIG), 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        Assert.assertEquals("earliest", consumerConfigs.get(AUTO_OFFSET_RESET_CONFIG));
        Assert.assertEquals(1, consumerConfigs.get(METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldSupportPrefixedRestoreConsumerConfigs() {
        props.put(StreamsConfig.consumerPrefix(METRICS_NUM_SAMPLES_CONFIG), 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getRestoreConsumerConfigs("clientId");
        Assert.assertEquals(1, consumerConfigs.get(METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldSupportPrefixedPropertiesThatAreNotPartOfConsumerConfig() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        props.put(StreamsConfig.consumerPrefix("interceptor.statsd.host"), "host");
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        Assert.assertEquals("host", consumerConfigs.get("interceptor.statsd.host"));
    }

    @Test
    public void shouldSupportPrefixedPropertiesThatAreNotPartOfRestoreConsumerConfig() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        props.put(StreamsConfig.consumerPrefix("interceptor.statsd.host"), "host");
        final Map<String, Object> consumerConfigs = streamsConfig.getRestoreConsumerConfigs("clientId");
        Assert.assertEquals("host", consumerConfigs.get("interceptor.statsd.host"));
    }

    @Test
    public void shouldSupportPrefixedPropertiesThatAreNotPartOfProducerConfig() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        props.put(StreamsConfig.producerPrefix("interceptor.statsd.host"), "host");
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        Assert.assertEquals("host", producerConfigs.get("interceptor.statsd.host"));
    }

    @Test
    public void shouldSupportPrefixedProducerConfigs() {
        props.put(StreamsConfig.producerPrefix(BUFFER_MEMORY_CONFIG), 10);
        props.put(StreamsConfig.producerPrefix(METRICS_NUM_SAMPLES_CONFIG), 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> configs = streamsConfig.getProducerConfigs("clientId");
        Assert.assertEquals(10, configs.get(BUFFER_MEMORY_CONFIG));
        Assert.assertEquals(1, configs.get(ProducerConfig.METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldBeSupportNonPrefixedConsumerConfigs() {
        props.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(METRICS_NUM_SAMPLES_CONFIG, 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        Assert.assertEquals("earliest", consumerConfigs.get(AUTO_OFFSET_RESET_CONFIG));
        Assert.assertEquals(1, consumerConfigs.get(METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldBeSupportNonPrefixedRestoreConsumerConfigs() {
        props.put(METRICS_NUM_SAMPLES_CONFIG, 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getRestoreConsumerConfigs("groupId");
        Assert.assertEquals(1, consumerConfigs.get(METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldSupportNonPrefixedProducerConfigs() {
        props.put(BUFFER_MEMORY_CONFIG, 10);
        props.put(METRICS_NUM_SAMPLES_CONFIG, 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> configs = streamsConfig.getProducerConfigs("clientId");
        Assert.assertEquals(10, configs.get(BUFFER_MEMORY_CONFIG));
        Assert.assertEquals(1, configs.get(ProducerConfig.METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldForwardCustomConfigsWithNoPrefixToAllClients() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        props.put("custom.property.host", "host");
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        final Map<String, Object> restoreConsumerConfigs = streamsConfig.getRestoreConsumerConfigs("clientId");
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        final Map<String, Object> adminConfigs = streamsConfig.getAdminConfigs("clientId");
        Assert.assertEquals("host", consumerConfigs.get("custom.property.host"));
        Assert.assertEquals("host", restoreConsumerConfigs.get("custom.property.host"));
        Assert.assertEquals("host", producerConfigs.get("custom.property.host"));
        Assert.assertEquals("host", adminConfigs.get("custom.property.host"));
    }

    @Test
    public void shouldOverrideNonPrefixedCustomConfigsWithPrefixedConfigs() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        props.put("custom.property.host", "host0");
        props.put(StreamsConfig.consumerPrefix("custom.property.host"), "host1");
        props.put(StreamsConfig.producerPrefix("custom.property.host"), "host2");
        props.put(StreamsConfig.adminClientPrefix("custom.property.host"), "host3");
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        final Map<String, Object> restoreConsumerConfigs = streamsConfig.getRestoreConsumerConfigs("clientId");
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        final Map<String, Object> adminConfigs = streamsConfig.getAdminConfigs("clientId");
        Assert.assertEquals("host1", consumerConfigs.get("custom.property.host"));
        Assert.assertEquals("host1", restoreConsumerConfigs.get("custom.property.host"));
        Assert.assertEquals("host2", producerConfigs.get("custom.property.host"));
        Assert.assertEquals("host3", adminConfigs.get("custom.property.host"));
    }

    @Test
    public void shouldSupportNonPrefixedAdminConfigs() {
        props.put(AdminClientConfig.RETRIES_CONFIG, 10);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> configs = streamsConfig.getAdminConfigs("clientId");
        Assert.assertEquals(10, configs.get(AdminClientConfig.RETRIES_CONFIG));
    }

    @Test(expected = StreamsException.class)
    public void shouldThrowStreamsExceptionIfKeySerdeConfigFails() {
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, StreamsConfigTest.MisconfiguredSerde.class);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        streamsConfig.defaultKeySerde();
    }

    @Test(expected = StreamsException.class)
    public void shouldThrowStreamsExceptionIfValueSerdeConfigFails() {
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, StreamsConfigTest.MisconfiguredSerde.class);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        streamsConfig.defaultValueSerde();
    }

    @Test
    public void shouldOverrideStreamsDefaultConsumerConfigs() {
        props.put(StreamsConfig.consumerPrefix(AUTO_OFFSET_RESET_CONFIG), "latest");
        props.put(StreamsConfig.consumerPrefix(MAX_POLL_RECORDS_CONFIG), "10");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        Assert.assertEquals("latest", consumerConfigs.get(AUTO_OFFSET_RESET_CONFIG));
        Assert.assertEquals("10", consumerConfigs.get(MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    public void shouldOverrideStreamsDefaultProducerConfigs() {
        props.put(StreamsConfig.producerPrefix(LINGER_MS_CONFIG), "10000");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        Assert.assertEquals("10000", producerConfigs.get(LINGER_MS_CONFIG));
    }

    @Test
    public void shouldOverrideStreamsDefaultConsumerConifgsOnRestoreConsumer() {
        props.put(StreamsConfig.consumerPrefix(MAX_POLL_RECORDS_CONFIG), "10");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getRestoreConsumerConfigs("clientId");
        Assert.assertEquals("10", consumerConfigs.get(MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    public void shouldResetToDefaultIfConsumerAutoCommitIsOverridden() {
        props.put(StreamsConfig.consumerPrefix(ENABLE_AUTO_COMMIT_CONFIG), "true");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("a", "b");
        Assert.assertEquals("false", consumerConfigs.get(ENABLE_AUTO_COMMIT_CONFIG));
    }

    @Test
    public void shouldResetToDefaultIfRestoreConsumerAutoCommitIsOverridden() {
        props.put(StreamsConfig.consumerPrefix(ENABLE_AUTO_COMMIT_CONFIG), "true");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getRestoreConsumerConfigs("client");
        Assert.assertEquals("false", consumerConfigs.get(ENABLE_AUTO_COMMIT_CONFIG));
    }

    @Test
    public void testGetRestoreConsumerConfigsWithRestoreConsumerOverridenPrefix() {
        props.put(StreamsConfig.consumerPrefix(MAX_POLL_RECORDS_CONFIG), "5");
        props.put(StreamsConfig.restoreConsumerPrefix(MAX_POLL_RECORDS_CONFIG), "50");
        final Map<String, Object> returnedProps = streamsConfig.getRestoreConsumerConfigs("clientId");
        Assert.assertEquals("50", returnedProps.get(MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    public void testGetGlobalConsumerConfigs() {
        final String clientId = "client";
        final Map<String, Object> returnedProps = streamsConfig.getGlobalConsumerConfigs(clientId);
        Assert.assertEquals(returnedProps.get(ConsumerConfig.CLIENT_ID_CONFIG), (clientId + "-global-consumer"));
        Assert.assertNull(returnedProps.get(GROUP_ID_CONFIG));
    }

    @Test
    public void shouldSupportPrefixedGlobalConsumerConfigs() {
        props.put(StreamsConfig.consumerPrefix(METRICS_NUM_SAMPLES_CONFIG), 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getGlobalConsumerConfigs("clientId");
        Assert.assertEquals(1, consumerConfigs.get(METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldSupportPrefixedPropertiesThatAreNotPartOfGlobalConsumerConfig() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        props.put(StreamsConfig.consumerPrefix("interceptor.statsd.host"), "host");
        final Map<String, Object> consumerConfigs = streamsConfig.getGlobalConsumerConfigs("clientId");
        Assert.assertEquals("host", consumerConfigs.get("interceptor.statsd.host"));
    }

    @Test
    public void shouldBeSupportNonPrefixedGlobalConsumerConfigs() {
        props.put(METRICS_NUM_SAMPLES_CONFIG, 1);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getGlobalConsumerConfigs("groupId");
        Assert.assertEquals(1, consumerConfigs.get(METRICS_NUM_SAMPLES_CONFIG));
    }

    @Test
    public void shouldResetToDefaultIfGlobalConsumerAutoCommitIsOverridden() {
        props.put(StreamsConfig.consumerPrefix(ENABLE_AUTO_COMMIT_CONFIG), "true");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getGlobalConsumerConfigs("client");
        Assert.assertEquals("false", consumerConfigs.get(ENABLE_AUTO_COMMIT_CONFIG));
    }

    @Test
    public void testGetGlobalConsumerConfigsWithGlobalConsumerOverridenPrefix() {
        props.put(StreamsConfig.consumerPrefix(MAX_POLL_RECORDS_CONFIG), "5");
        props.put(StreamsConfig.globalConsumerPrefix(MAX_POLL_RECORDS_CONFIG), "50");
        final Map<String, Object> returnedProps = streamsConfig.getGlobalConsumerConfigs("clientId");
        Assert.assertEquals("50", returnedProps.get(MAX_POLL_RECORDS_CONFIG));
    }

    @Test
    public void shouldSetInternalLeaveGroupOnCloseConfigToFalseInConsumer() {
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        MatcherAssert.assertThat(consumerConfigs.get("internal.leave.group.on.close"), CoreMatchers.equalTo(false));
    }

    @Test
    public void shouldAcceptAtLeastOnce() {
        // don't use `StreamsConfig.AT_LEAST_ONCE` to actually do a useful test
        props.put(PROCESSING_GUARANTEE_CONFIG, "at_least_once");
        new StreamsConfig(props);
    }

    @Test
    public void shouldAcceptExactlyOnce() {
        // don't use `StreamsConfig.EXACLTY_ONCE` to actually do a useful test
        props.put(PROCESSING_GUARANTEE_CONFIG, "exactly_once");
        new StreamsConfig(props);
    }

    @Test(expected = ConfigException.class)
    public void shouldThrowExceptionIfNotAtLestOnceOrExactlyOnce() {
        props.put(PROCESSING_GUARANTEE_CONFIG, "bad_value");
        new StreamsConfig(props);
    }

    @Test
    public void shouldResetToDefaultIfConsumerIsolationLevelIsOverriddenIfEosEnabled() {
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(ISOLATION_LEVEL_CONFIG, "anyValue");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        MatcherAssert.assertThat(consumerConfigs.get(ISOLATION_LEVEL_CONFIG), IsEqual.equalTo(IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT)));
    }

    @Test
    public void shouldAllowSettingConsumerIsolationLevelIfEosDisabled() {
        props.put(ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_UNCOMMITTED.name().toLowerCase(Locale.ROOT));
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientrId");
        MatcherAssert.assertThat(consumerConfigs.get(ISOLATION_LEVEL_CONFIG), IsEqual.equalTo(IsolationLevel.READ_UNCOMMITTED.name().toLowerCase(Locale.ROOT)));
    }

    @Test
    public void shouldResetToDefaultIfProducerEnableIdempotenceIsOverriddenIfEosEnabled() {
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(ENABLE_IDEMPOTENCE_CONFIG, "anyValue");
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        Assert.assertTrue(((Boolean) (producerConfigs.get(ENABLE_IDEMPOTENCE_CONFIG))));
    }

    @Test
    public void shouldAllowSettingProducerEnableIdempotenceIfEosDisabled() {
        props.put(ENABLE_IDEMPOTENCE_CONFIG, false);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        MatcherAssert.assertThat(producerConfigs.get(ENABLE_IDEMPOTENCE_CONFIG), IsEqual.equalTo(false));
    }

    @Test
    public void shouldSetDifferentDefaultsIfEosEnabled() {
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> consumerConfigs = streamsConfig.getMainConsumerConfigs("groupId", "clientId");
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        MatcherAssert.assertThat(consumerConfigs.get(ISOLATION_LEVEL_CONFIG), IsEqual.equalTo(IsolationLevel.READ_COMMITTED.name().toLowerCase(Locale.ROOT)));
        Assert.assertTrue(((Boolean) (producerConfigs.get(ENABLE_IDEMPOTENCE_CONFIG))));
        MatcherAssert.assertThat(producerConfigs.get(DELIVERY_TIMEOUT_MS_CONFIG), IsEqual.equalTo(Integer.MAX_VALUE));
        MatcherAssert.assertThat(streamsConfig.getLong(COMMIT_INTERVAL_MS_CONFIG), IsEqual.equalTo(100L));
    }

    @Test
    public void shouldNotOverrideUserConfigRetriesIfExactlyOnceEnabled() {
        final int numberOfRetries = 42;
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(ProducerConfig.RETRIES_CONFIG, numberOfRetries);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        final Map<String, Object> producerConfigs = streamsConfig.getProducerConfigs("clientId");
        MatcherAssert.assertThat(producerConfigs.get(ProducerConfig.RETRIES_CONFIG), IsEqual.equalTo(numberOfRetries));
    }

    @Test
    public void shouldNotOverrideUserConfigCommitIntervalMsIfExactlyOnceEnabled() {
        final long commitIntervalMs = 73L;
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(COMMIT_INTERVAL_MS_CONFIG, commitIntervalMs);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        MatcherAssert.assertThat(streamsConfig.getLong(COMMIT_INTERVAL_MS_CONFIG), IsEqual.equalTo(commitIntervalMs));
    }

    @Test
    public void shouldThrowExceptionIfCommitIntervalMsIsNegative() {
        final long commitIntervalMs = -1;
        props.put(COMMIT_INTERVAL_MS_CONFIG, commitIntervalMs);
        try {
            new StreamsConfig(props);
            Assert.fail("Should throw ConfigException when commitIntervalMs is set to a negative value");
        } catch (final ConfigException e) {
            Assert.assertEquals("Invalid value -1 for configuration commit.interval.ms: Value must be at least 0", e.getMessage());
        }
    }

    @Test
    public void shouldUseNewConfigsWhenPresent() {
        final Properties props = StreamsTestUtils.getStreamsConfig();
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass());
        props.put(DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, StreamsConfigTest.MockTimestampExtractor.class);
        final StreamsConfig config = new StreamsConfig(props);
        Assert.assertTrue(((config.defaultKeySerde()) instanceof Serdes.LongSerde));
        Assert.assertTrue(((config.defaultValueSerde()) instanceof Serdes.LongSerde));
        Assert.assertTrue(((config.defaultTimestampExtractor()) instanceof StreamsConfigTest.MockTimestampExtractor));
    }

    @Test
    public void shouldUseCorrectDefaultsWhenNoneSpecified() {
        final StreamsConfig config = new StreamsConfig(StreamsTestUtils.getStreamsConfig());
        Assert.assertTrue(((config.defaultKeySerde()) instanceof Serdes.ByteArraySerde));
        Assert.assertTrue(((config.defaultValueSerde()) instanceof Serdes.ByteArraySerde));
        Assert.assertTrue(((config.defaultTimestampExtractor()) instanceof FailOnInvalidTimestamp));
    }

    @Test
    public void shouldSpecifyCorrectKeySerdeClassOnError() {
        final Properties props = StreamsTestUtils.getStreamsConfig();
        props.put(DEFAULT_KEY_SERDE_CLASS_CONFIG, StreamsConfigTest.MisconfiguredSerde.class);
        final StreamsConfig config = new StreamsConfig(props);
        try {
            config.defaultKeySerde();
            Assert.fail("Test should throw a StreamsException");
        } catch (final StreamsException e) {
            Assert.assertEquals("Failed to configure key serde class org.apache.kafka.streams.StreamsConfigTest$MisconfiguredSerde", e.getMessage());
        }
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldSpecifyCorrectValueSerdeClassOnError() {
        final Properties props = StreamsTestUtils.getStreamsConfig();
        props.put(DEFAULT_VALUE_SERDE_CLASS_CONFIG, StreamsConfigTest.MisconfiguredSerde.class);
        final StreamsConfig config = new StreamsConfig(props);
        try {
            config.defaultValueSerde();
            Assert.fail("Test should throw a StreamsException");
        } catch (final StreamsException e) {
            Assert.assertEquals("Failed to configure value serde class org.apache.kafka.streams.StreamsConfigTest$MisconfiguredSerde", e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfMaxInFlightRequestsGreaterThanFiveIfEosEnabled() {
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 7);
        final StreamsConfig streamsConfig = new StreamsConfig(props);
        try {
            streamsConfig.getProducerConfigs("clientId");
            Assert.fail("Should throw ConfigException when ESO is enabled and maxInFlight requests exceeds 5");
        } catch (final ConfigException e) {
            Assert.assertEquals("Invalid value 7 for configuration max.in.flight.requests.per.connection: Can't exceed 5 when exactly-once processing is enabled", e.getMessage());
        }
    }

    @Test
    public void shouldAllowToSpecifyMaxInFlightRequestsPerConnectionAsStringIfEosEnabled() {
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "3");
        new StreamsConfig(props).getProducerConfigs("clientId");
    }

    @Test
    public void shouldThrowConfigExceptionIfMaxInFlightRequestsPerConnectionIsInvalidStringIfEosEnabled() {
        props.put(PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE);
        props.put(MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "not-a-number");
        try {
            new StreamsConfig(props).getProducerConfigs("clientId");
            Assert.fail("Should throw ConfigException when EOS is enabled and maxInFlight cannot be paresed into an integer");
        } catch (final ConfigException e) {
            Assert.assertEquals("Invalid value not-a-number for configuration max.in.flight.requests.per.connection: String value could not be parsed as 32-bit integer", e.getMessage());
        }
    }

    @Test
    public void shouldSpecifyNoOptimizationWhenNotExplicitlyAddedToConfigs() {
        final String expectedOptimizeConfig = "none";
        final String actualOptimizedConifig = streamsConfig.getString(StreamsConfig.TOPOLOGY_OPTIMIZATION);
        Assert.assertEquals("Optimization should be \"none\"", expectedOptimizeConfig, actualOptimizedConifig);
    }

    @Test
    public void shouldSpecifyOptimizationWhenNotExplicitlyAddedToConfigs() {
        final String expectedOptimizeConfig = "all";
        props.put(StreamsConfig.TOPOLOGY_OPTIMIZATION, "all");
        final StreamsConfig config = new StreamsConfig(props);
        final String actualOptimizedConifig = config.getString(StreamsConfig.TOPOLOGY_OPTIMIZATION);
        Assert.assertEquals("Optimization should be \"all\"", expectedOptimizeConfig, actualOptimizedConifig);
    }

    @Test(expected = ConfigException.class)
    public void shouldThrowConfigExceptionWhenOptimizationConfigNotValueInRange() {
        props.put(StreamsConfig.TOPOLOGY_OPTIMIZATION, "maybe");
        new StreamsConfig(props);
    }

    static class MisconfiguredSerde implements Serde {
        @Override
        public void configure(final Map configs, final boolean isKey) {
            throw new RuntimeException("boom");
        }

        @Override
        public Serializer serializer() {
            return null;
        }

        @Override
        public Deserializer deserializer() {
            return null;
        }
    }

    public static class MockTimestampExtractor implements TimestampExtractor {
        @Override
        public long extract(final ConsumerRecord<Object, Object> record, final long previousTimestamp) {
            return 0;
        }
    }
}

