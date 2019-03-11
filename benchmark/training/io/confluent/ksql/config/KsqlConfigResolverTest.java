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
package io.confluent.ksql.config;


import ConsumerConfig.FETCH_MIN_BYTES_CONFIG;
import KsqlConfig.KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG;
import KsqlConfig.SINK_NUMBER_OF_PARTITIONS_PROPERTY;
import ProducerConfig.BUFFER_MEMORY_CONFIG;
import StreamsConfig.COMMIT_INTERVAL_MS_CONFIG;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.streams.StreamsConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class KsqlConfigResolverTest {
    private static final ConfigDef STREAMS_CONFIG_DEF = StreamsConfig.configDef();

    private static final ConfigDef CONSUMER_CONFIG_DEF = KsqlConfigResolver.getConfigDef(ConsumerConfig.class);

    private static final ConfigDef PRODUCER_CONFIG_DEF = KsqlConfigResolver.getConfigDef(ProducerConfig.class);

    private static final ConfigDef KSQL_CONFIG_DEF = KsqlConfig.CURRENT_DEF;

    private KsqlConfigResolver resolver;

    @Test
    public void shouldResolveKsqlProperty() {
        MatcherAssert.assertThat(resolver.resolve(SINK_NUMBER_OF_PARTITIONS_PROPERTY, true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(SINK_NUMBER_OF_PARTITIONS_PROPERTY, KsqlConfigResolverTest.KSQL_CONFIG_DEF)));
    }

    @Test
    public void shouldNotFindPrefixedKsqlProperty() {
        assertNotFound(((KsqlConfig.KSQL_CONFIG_PROPERTY_PREFIX) + (KsqlConfig.SINK_NUMBER_OF_PARTITIONS_PROPERTY)));
    }

    @Test
    public void shouldNotFindUnknownKsqlProperty() {
        assertNotFound(((KsqlConfig.KSQL_CONFIG_PROPERTY_PREFIX) + "you.won't.find.me...right"));
    }

    @Test
    public void shouldResolveKnownKsqlFunctionProperty() {
        MatcherAssert.assertThat(resolver.resolve(KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG, true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(KSQL_FUNCTIONS_SUBSTRING_LEGACY_ARGS_CONFIG, KsqlConfigResolverTest.KSQL_CONFIG_DEF)));
    }

    @Test
    public void shouldReturnUnresolvedForOtherKsqlFunctionProperty() {
        MatcherAssert.assertThat(resolver.resolve(((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "some_udf.some.prop"), true), CoreMatchers.is(KsqlConfigResolverTest.unresolvedItem(((KsqlConfig.KSQL_FUNCTIONS_PROPERTY_PREFIX) + "some_udf.some.prop"))));
    }

    @Test
    public void shouldResolveStreamsConfig() {
        MatcherAssert.assertThat(resolver.resolve(COMMIT_INTERVAL_MS_CONFIG, true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(COMMIT_INTERVAL_MS_CONFIG, KsqlConfigResolverTest.STREAMS_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveKsqlStreamPrefixedStreamConfig() {
        MatcherAssert.assertThat(resolver.resolve(((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.COMMIT_INTERVAL_MS_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(COMMIT_INTERVAL_MS_CONFIG, KsqlConfigResolverTest.STREAMS_CONFIG_DEF)));
    }

    @Test
    public void shouldNotFindUnknownStreamsProperty() {
        assertNotFound(((KsqlConfig.KSQL_STREAMS_PREFIX) + "you.won't.find.me...right"));
    }

    @Test
    public void shouldResolveConsumerConfig() {
        MatcherAssert.assertThat(resolver.resolve(FETCH_MIN_BYTES_CONFIG, true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(FETCH_MIN_BYTES_CONFIG, KsqlConfigResolverTest.CONSUMER_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveConsumerPrefixedConsumerConfig() {
        MatcherAssert.assertThat(resolver.resolve(((StreamsConfig.CONSUMER_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(FETCH_MIN_BYTES_CONFIG, KsqlConfigResolverTest.CONSUMER_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveKsqlPrefixedConsumerConfig() {
        MatcherAssert.assertThat(resolver.resolve(((KsqlConfig.KSQL_STREAMS_PREFIX) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(FETCH_MIN_BYTES_CONFIG, KsqlConfigResolverTest.CONSUMER_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveKsqlConsumerPrefixedConsumerConfig() {
        MatcherAssert.assertThat(resolver.resolve((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(FETCH_MIN_BYTES_CONFIG, KsqlConfigResolverTest.CONSUMER_CONFIG_DEF)));
    }

    @Test
    public void shouldNotFindUnknownConsumerProperty() {
        assertNotFound(((StreamsConfig.CONSUMER_PREFIX) + "you.won't.find.me...right"));
    }

    @Test
    public void shouldResolveProducerConfig() {
        MatcherAssert.assertThat(resolver.resolve(BUFFER_MEMORY_CONFIG, true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(BUFFER_MEMORY_CONFIG, KsqlConfigResolverTest.PRODUCER_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveProducerPrefixedProducerConfig() {
        MatcherAssert.assertThat(resolver.resolve(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(BUFFER_MEMORY_CONFIG, KsqlConfigResolverTest.PRODUCER_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveKsqlPrefixedProducerConfig() {
        MatcherAssert.assertThat(resolver.resolve(((KsqlConfig.KSQL_STREAMS_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(BUFFER_MEMORY_CONFIG, KsqlConfigResolverTest.PRODUCER_CONFIG_DEF)));
    }

    @Test
    public void shouldResolveKsqlProducerPrefixedProducerConfig() {
        MatcherAssert.assertThat(resolver.resolve((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.PRODUCER_PREFIX)) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), true), CoreMatchers.is(KsqlConfigResolverTest.resolvedItem(BUFFER_MEMORY_CONFIG, KsqlConfigResolverTest.PRODUCER_CONFIG_DEF)));
    }

    @Test
    public void shouldNotFindUnknownProducerProperty() {
        assertNotFound(((StreamsConfig.PRODUCER_PREFIX) + "you.won't.find.me...right"));
    }

    @Test
    public void shouldReturnUnresolvedForOtherConfigIfNotStrict() {
        MatcherAssert.assertThat(resolver.resolve("confluent.monitoring.interceptor.topic", false), CoreMatchers.is(KsqlConfigResolverTest.unresolvedItem("confluent.monitoring.interceptor.topic")));
    }

    @Test
    public void shouldReturnEmptyForOtherConfigIfStrict() {
        MatcherAssert.assertThat(resolver.resolve("confluent.monitoring.interceptor.topic", true), CoreMatchers.is(Optional.empty()));
    }
}

