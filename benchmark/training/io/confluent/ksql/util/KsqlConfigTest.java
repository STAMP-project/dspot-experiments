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


import AdminClientConfig.RETRIES_CONFIG;
import ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG;
import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.FETCH_MIN_BYTES_CONFIG;
import KsqlConfig.FAIL_ON_DESERIALIZATION_ERROR_CONFIG;
import KsqlConfig.FAIL_ON_PRODUCTION_ERROR_CONFIG;
import KsqlConfig.KSQL_ENABLE_UDFS;
import KsqlConfig.KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG;
import KsqlConfig.KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG;
import KsqlConfig.KSQL_SERVICE_ID_CONFIG;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_OFF;
import KsqlConfig.SINK_NUMBER_OF_PARTITIONS_PROPERTY;
import KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY;
import ProducerConfig.BUFFER_MEMORY_CONFIG;
import SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG;
import StreamsConfig.APPLICATION_ID_CONFIG;
import StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import StreamsConfig.DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG;
import StreamsConfig.DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG;
import StreamsConfig.METADATA_MAX_AGE_CONFIG;
import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.NUM_STREAM_THREADS_CONFIG;
import StreamsConfig.OPTIMIZE;
import StreamsConfig.TOPOLOGY_OPTIMIZATION;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.errors.LogMetricAndContinueExceptionHandler;
import io.confluent.ksql.errors.ProductionExceptionHandlerUtil.LogAndContinueProductionExceptionHandler;
import io.confluent.ksql.errors.ProductionExceptionHandlerUtil.LogAndFailProductionExceptionHandler;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.streams.StreamsConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX;
import static KsqlConfig.KSQL_STREAMS_PREFIX;
import static KsqlConfig.KSQ_FUNCTIONS_GLOBAL_PROPERTY_PREFIX;


public class KsqlConfigTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldSetInitialValuesCorrectly() {
        final Map<String, Object> initialProps = new HashMap<>();
        initialProps.put(SINK_NUMBER_OF_PARTITIONS_PROPERTY, 10);
        initialProps.put(SINK_NUMBER_OF_REPLICAS_PROPERTY, ((short) (3)));
        initialProps.put(COMMIT_INTERVAL_MS_CONFIG, 800);
        initialProps.put(NUM_STREAM_THREADS_CONFIG, 5);
        final KsqlConfig ksqlConfig = new KsqlConfig(initialProps);
        MatcherAssert.assertThat(ksqlConfig.getInt(SINK_NUMBER_OF_PARTITIONS_PROPERTY), CoreMatchers.equalTo(10));
        MatcherAssert.assertThat(ksqlConfig.getShort(SINK_NUMBER_OF_REPLICAS_PROPERTY), CoreMatchers.equalTo(((short) (3))));
    }

    @Test
    public void shouldSetLogAndContinueExceptionHandlerByDefault() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(LogMetricAndContinueExceptionHandler.class));
    }

    @Test
    public void shouldSetLogAndContinueExceptionHandlerWhenFailOnDeserializationErrorFalse() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(FAIL_ON_DESERIALIZATION_ERROR_CONFIG, false));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(LogMetricAndContinueExceptionHandler.class));
    }

    @Test
    public void shouldNotSetDeserializationExceptionHandlerWhenFailOnDeserializationErrorTrue() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(FAIL_ON_DESERIALIZATION_ERROR_CONFIG, true));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(DEFAULT_DESERIALIZATION_EXCEPTION_HANDLER_CLASS_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.nullValue());
    }

    @Test
    public void shouldSetLogAndContinueExceptionHandlerWhenFailOnProductionErrorFalse() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(FAIL_ON_PRODUCTION_ERROR_CONFIG, false));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(LogAndContinueProductionExceptionHandler.class));
    }

    @Test
    public void shouldNotSetDeserializationExceptionHandlerWhenFailOnProductionErrorTrue() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(FAIL_ON_PRODUCTION_ERROR_CONFIG, true));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(LogAndFailProductionExceptionHandler.class));
    }

    @Test
    public void shouldFailOnProductionErrorByDefault() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(DEFAULT_PRODUCTION_EXCEPTION_HANDLER_CLASS_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(LogAndFailProductionExceptionHandler.class));
    }

    @Test
    public void shouldSetStreamsConfigConsumerUnprefixedProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(AUTO_OFFSET_RESET_CONFIG, "earliest"));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(AUTO_OFFSET_RESET_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo("earliest"));
    }

    @Test
    public void shouldSetStreamsConfigConsumerPrefixedProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(((StreamsConfig.CONSUMER_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), "100"));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(((StreamsConfig.CONSUMER_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG))), CoreMatchers.equalTo(100));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(FETCH_MIN_BYTES_CONFIG), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldSetStreamsConfigConsumerKsqlPrefixedProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap((((KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), "100"));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(((StreamsConfig.CONSUMER_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG))), CoreMatchers.equalTo(100));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(FETCH_MIN_BYTES_CONFIG), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get((((KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG))), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldSetStreamsConfigProducerUnprefixedProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(BUFFER_MEMORY_CONFIG, "1024"));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(BUFFER_MEMORY_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(1024L));
    }

    @Test
    public void shouldSetStreamsConfigProducerPrefixedProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), "1024"));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG))), CoreMatchers.equalTo(1024L));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(BUFFER_MEMORY_CONFIG), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldSetStreamsConfigKsqlProducerPrefixedProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap((((KSQL_STREAMS_PREFIX) + (StreamsConfig.PRODUCER_PREFIX)) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), "1024"));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG))), CoreMatchers.equalTo(1024L));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(BUFFER_MEMORY_CONFIG), CoreMatchers.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get((((KSQL_STREAMS_PREFIX) + (StreamsConfig.PRODUCER_PREFIX)) + (ProducerConfig.BUFFER_MEMORY_CONFIG))), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldSetStreamsConfigAdminClientProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(RETRIES_CONFIG, 3));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(RETRIES_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(3));
    }

    @Test
    public void shouldSetStreamsConfigProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(CACHE_MAX_BYTES_BUFFERING_CONFIG, "128"));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get(CACHE_MAX_BYTES_BUFFERING_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(128L));
    }

    @Test
    public void shouldSetPrefixedStreamsConfigProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(((KSQL_STREAMS_PREFIX) + (StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG)), "128"));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(CACHE_MAX_BYTES_BUFFERING_CONFIG), CoreMatchers.equalTo(128L));
        MatcherAssert.assertThat(ksqlConfig.getKsqlStreamConfigProps().get(((KSQL_STREAMS_PREFIX) + (StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG))), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldSetMonitoringInterceptorConfigProperties() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap("confluent.monitoring.interceptor.topic", "foo"));
        final Object result = ksqlConfig.getKsqlStreamConfigProps().get("confluent.monitoring.interceptor.topic");
        MatcherAssert.assertThat(result, CoreMatchers.equalTo("foo"));
    }

    @Test
    public void shouldFilterPropertiesForWhichTypeUnknown() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap("you.shall.not.pass", "wizard"));
        MatcherAssert.assertThat(ksqlConfig.getAllConfigPropsWithSecretsObfuscated().keySet(), CoreMatchers.not(CoreMatchers.hasItem("you.shall.not.pass")));
    }

    @Test
    public void shouldCloneWithKsqlPropertyOverwrite() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(KSQL_SERVICE_ID_CONFIG, "test"));
        final KsqlConfig ksqlConfigClone = ksqlConfig.cloneWithPropertyOverwrite(Collections.singletonMap(KSQL_SERVICE_ID_CONFIG, "test-2"));
        final String result = ksqlConfigClone.getString(KSQL_SERVICE_ID_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo("test-2"));
    }

    @Test
    public void shouldCloneWithStreamPropertyOverwrite() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(FETCH_MIN_BYTES_CONFIG, "100"));
        final KsqlConfig ksqlConfigClone = ksqlConfig.cloneWithPropertyOverwrite(Collections.singletonMap(FETCH_MIN_BYTES_CONFIG, "200"));
        final Object result = ksqlConfigClone.getKsqlStreamConfigProps().get(FETCH_MIN_BYTES_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldCloneWithMultipleOverwrites() {
        final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(AUTO_COMMIT_INTERVAL_MS_CONFIG, "123", AUTO_OFFSET_RESET_CONFIG, "latest"));
        final KsqlConfig clone = ksqlConfig.cloneWithPropertyOverwrite(ImmutableMap.of(NUM_STREAM_THREADS_CONFIG, "2", AUTO_COMMIT_INTERVAL_MS_CONFIG, "456"));
        final KsqlConfig cloneClone = clone.cloneWithPropertyOverwrite(ImmutableMap.of(AUTO_OFFSET_RESET_CONFIG, "earliest", METADATA_MAX_AGE_CONFIG, "13"));
        final Map<String, ?> props = cloneClone.getKsqlStreamConfigProps();
        MatcherAssert.assertThat(props.get(AUTO_COMMIT_INTERVAL_MS_CONFIG), CoreMatchers.equalTo(456));
        MatcherAssert.assertThat(props.get(AUTO_OFFSET_RESET_CONFIG), CoreMatchers.equalTo("earliest"));
        MatcherAssert.assertThat(props.get(NUM_STREAM_THREADS_CONFIG), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(props.get(METADATA_MAX_AGE_CONFIG), CoreMatchers.equalTo(13L));
    }

    @Test
    public void shouldCloneWithPrefixedStreamPropertyOverwrite() {
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.singletonMap(((KSQL_STREAMS_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), "100"));
        final KsqlConfig ksqlConfigClone = ksqlConfig.cloneWithPropertyOverwrite(Collections.singletonMap(((KSQL_STREAMS_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), "200"));
        final Object result = ksqlConfigClone.getKsqlStreamConfigProps().get(FETCH_MIN_BYTES_CONFIG);
        MatcherAssert.assertThat(result, CoreMatchers.equalTo(200));
    }

    @Test
    public void shouldPreserveOriginalCompatibilitySensitiveConfigs() {
        final Map<String, String> originalProperties = ImmutableMap.of(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG, "not_the_default");
        final KsqlConfig currentConfig = new KsqlConfig(Collections.emptyMap());
        final KsqlConfig compatibleConfig = currentConfig.overrideBreakingConfigsWithOriginalValues(originalProperties);
        MatcherAssert.assertThat(compatibleConfig.getString(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG), CoreMatchers.equalTo("not_the_default"));
    }

    @Test
    public void shouldUseCurrentValueForCompatibilityInsensitiveConfigs() {
        final Map<String, String> originalProperties = Collections.singletonMap(KSQL_ENABLE_UDFS, "false");
        final KsqlConfig currentConfig = new KsqlConfig(Collections.singletonMap(KSQL_ENABLE_UDFS, true));
        final KsqlConfig compatibleConfig = currentConfig.overrideBreakingConfigsWithOriginalValues(originalProperties);
        MatcherAssert.assertThat(compatibleConfig.getBoolean(KSQL_ENABLE_UDFS), CoreMatchers.is(true));
    }

    @Test
    public void shouldReturnUdfConfig() {
        // Given:
        final String functionName = "bob";
        final String udfConfigName = ((KSQL_FUNCTIONS_PROPERTY_PREFIX) + functionName) + ".some-setting";
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(udfConfigName, "should-be-visible"));
        // When:
        final Map<String, ?> udfProps = config.getKsqlFunctionsConfigProps(functionName);
        // Then:
        MatcherAssert.assertThat(udfProps.get(udfConfigName), CoreMatchers.is("should-be-visible"));
    }

    @Test
    public void shouldReturnUdfConfigOnlyIfLowercase() {
        // Given:
        final String functionName = "BOB";
        final String correctConfigName = ((KSQL_FUNCTIONS_PROPERTY_PREFIX) + (functionName.toLowerCase())) + ".some-setting";
        final String invalidConfigName = ((KSQL_FUNCTIONS_PROPERTY_PREFIX) + functionName) + ".some-other-setting";
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(invalidConfigName, "should-not-be-visible", correctConfigName, "should-be-visible"));
        // When:
        final Map<String, ?> udfProps = config.getKsqlFunctionsConfigProps(functionName);
        // Then:
        MatcherAssert.assertThat(udfProps.keySet(), Matchers.contains(correctConfigName));
    }

    @Test
    public void shouldReturnUdfConfigAfterMerge() {
        final String functionName = "BOB";
        final String correctConfigName = ((KSQL_FUNCTIONS_PROPERTY_PREFIX) + (functionName.toLowerCase())) + ".some-setting";
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(correctConfigName, "should-be-visible"));
        final KsqlConfig merged = config.overrideBreakingConfigsWithOriginalValues(Collections.emptyMap());
        // When:
        final Map<String, ?> udfProps = merged.getKsqlFunctionsConfigProps(functionName);
        // Then:
        MatcherAssert.assertThat(udfProps.keySet(), CoreMatchers.hasItem(correctConfigName));
    }

    @Test
    public void shouldReturnGlobalUdfConfig() {
        // Given:
        final String globalConfigName = (KSQ_FUNCTIONS_GLOBAL_PROPERTY_PREFIX) + ".some-setting";
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(globalConfigName, "global"));
        // When:
        final Map<String, ?> udfProps = config.getKsqlFunctionsConfigProps("what-eva");
        // Then:
        MatcherAssert.assertThat(udfProps.get(globalConfigName), CoreMatchers.is("global"));
    }

    @Test
    public void shouldNotReturnNoneUdfConfig() {
        // Given:
        final String functionName = "bob";
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, "not a udf property", ((KSQL_FUNCTIONS_PROPERTY_PREFIX) + "different_udf.some-setting"), "different udf property"));
        // When:
        final Map<String, ?> udfProps = config.getKsqlFunctionsConfigProps(functionName);
        // Then:
        MatcherAssert.assertThat(udfProps.keySet(), CoreMatchers.is(Matchers.empty()));
    }

    @Test
    public void shouldListKnownKsqlConfig() {
        // Given:
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(KSQL_SERVICE_ID_CONFIG, "not sensitive", SSL_KEYSTORE_PASSWORD_CONFIG, "sensitive!"));
        // When:
        final Map<String, String> result = config.getAllConfigPropsWithSecretsObfuscated();
        // Then:
        MatcherAssert.assertThat(result.get(KSQL_SERVICE_ID_CONFIG), CoreMatchers.is("not sensitive"));
    }

    @Test
    public void shouldListKnownKsqlFunctionConfig() {
        // Given:
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG, "true"));
        // When:
        final Map<String, String> result = config.getAllConfigPropsWithSecretsObfuscated();
        // Then:
        MatcherAssert.assertThat(result.get(KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG), CoreMatchers.is("true"));
    }

    @Test
    public void shouldListUnknownKsqlFunctionConfigObfuscated() {
        // Given:
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(((KSQL_FUNCTIONS_PROPERTY_PREFIX) + "some_udf.some.prop"), "maybe sensitive"));
        // When:
        final Map<String, String> result = config.getAllConfigPropsWithSecretsObfuscated();
        // Then:
        MatcherAssert.assertThat(result.get(((KSQL_FUNCTIONS_PROPERTY_PREFIX) + "some_udf.some.prop")), CoreMatchers.is("[hidden]"));
    }

    @Test
    public void shouldListKnownStreamsConfigObfuscated() {
        // Given:
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of(APPLICATION_ID_CONFIG, "not sensitive", ((KSQL_STREAMS_PREFIX) + (SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)), "sensitive!", (((KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG)), "sensitive!"));
        // When:
        final Map<String, String> result = config.getAllConfigPropsWithSecretsObfuscated();
        // Then:
        MatcherAssert.assertThat(result.get(((KSQL_STREAMS_PREFIX) + (StreamsConfig.APPLICATION_ID_CONFIG))), CoreMatchers.is("not sensitive"));
        MatcherAssert.assertThat(result.get(((KSQL_STREAMS_PREFIX) + (SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG))), CoreMatchers.is("[hidden]"));
        MatcherAssert.assertThat(result.get((((KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG))), CoreMatchers.is("[hidden]"));
    }

    @Test
    public void shouldNotListUnresolvedServerConfig() {
        // Given:
        final KsqlConfig config = new KsqlConfig(ImmutableMap.of("some.random.property", "might be sensitive"));
        // When:
        final Map<String, String> result = config.getAllConfigPropsWithSecretsObfuscated();
        // Then:
        MatcherAssert.assertThat(result.get("some.random.property"), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void shouldDefaultOptimizationsToOn() {
        // When:
        final KsqlConfig config = new KsqlConfig(Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(config.getKsqlStreamConfigProps().get(TOPOLOGY_OPTIMIZATION), CoreMatchers.equalTo(OPTIMIZE));
    }

    @Test
    public void shouldDefaultOptimizationsToOffForOldConfigs() {
        // When:
        final KsqlConfig config = new KsqlConfig(Collections.emptyMap()).overrideBreakingConfigsWithOriginalValues(Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(config.getKsqlStreamConfigProps().get(TOPOLOGY_OPTIMIZATION), CoreMatchers.equalTo(NO_OPTIMIZATION));
    }

    @Test
    public void shouldPreserveOriginalOptimizationConfig() {
        // Given:
        final KsqlConfig config = new KsqlConfig(Collections.singletonMap(TOPOLOGY_OPTIMIZATION, OPTIMIZE));
        final KsqlConfig saved = new KsqlConfig(Collections.singletonMap(TOPOLOGY_OPTIMIZATION, NO_OPTIMIZATION));
        // When:
        final KsqlConfig merged = config.overrideBreakingConfigsWithOriginalValues(saved.getAllConfigPropsWithSecretsObfuscated());
        // Then:
        MatcherAssert.assertThat(merged.getKsqlStreamConfigProps().get(TOPOLOGY_OPTIMIZATION), CoreMatchers.equalTo(NO_OPTIMIZATION));
    }

    @Test
    public void shouldRaiseIfInternalTopicNamingOffAndStreamsOptimizationsOn() {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage("Internal topic naming must be enabled if streams optimizations enabled");
        new KsqlConfig(ImmutableMap.of(KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_OFF, TOPOLOGY_OPTIMIZATION, OPTIMIZE));
    }

    @Test
    public void shouldRaiseOnInvalidInternalTopicNamingValue() {
        expectedException.expect(ConfigException.class);
        new KsqlConfig(Collections.singletonMap(KSQL_USE_NAMED_INTERNAL_TOPICS, "foobar"));
    }
}

