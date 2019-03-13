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
package org.apache.kafka.streams.integration;


import KafkaStreams.State.NOT_RUNNING;
import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValueTimestamp;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.test.IntegrationTest;
import org.apache.kafka.test.TestUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static BufferConfig.maxRecords;


@RunWith(Parameterized.class)
@Category({ IntegrationTest.class })
public class SuppressionDurabilityIntegrationTest {
    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(3, mkProperties(mkMap()), 0L);

    private static final StringDeserializer STRING_DESERIALIZER = new StringDeserializer();

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

    private static final Serde<String> STRING_SERDE = Serdes.String();

    private static final LongDeserializer LONG_DESERIALIZER = new LongDeserializer();

    private static final int COMMIT_INTERVAL = 100;

    private final boolean eosEnabled;

    public SuppressionDurabilityIntegrationTest(final boolean eosEnabled) {
        this.eosEnabled = eosEnabled;
    }

    @Test
    public void shouldRecoverBufferAfterShutdown() {
        final String testId = "-shouldRecoverBufferAfterShutdown";
        final String appId = (getClass().getSimpleName().toLowerCase(Locale.getDefault())) + testId;
        final String input = "input" + testId;
        final String outputSuppressed = "output-suppressed" + testId;
        final String outputRaw = "output-raw" + testId;
        IntegrationTestUtils.cleanStateBeforeTest(SuppressionDurabilityIntegrationTest.CLUSTER, input, outputRaw, outputSuppressed);
        final StreamsBuilder builder = new StreamsBuilder();
        final KTable<String, Long> valueCounts = buildCountsTable(input, builder);
        final KStream<String, Long> suppressedCounts = valueCounts.suppress(Suppressed.untilTimeLimit(Duration.ofMillis(Long.MAX_VALUE), maxRecords(3L).emitEarlyWhenFull())).toStream();
        final AtomicInteger eventCount = new AtomicInteger(0);
        suppressedCounts.foreach(( key, value) -> eventCount.incrementAndGet());
        suppressedCounts.to(outputSuppressed, Produced.with(SuppressionDurabilityIntegrationTest.STRING_SERDE, Serdes.Long()));
        valueCounts.toStream().to(outputRaw, Produced.with(SuppressionDurabilityIntegrationTest.STRING_SERDE, Serdes.Long()));
        final Properties streamsConfig = mkProperties(mkMap(mkEntry(StreamsConfig.APPLICATION_ID_CONFIG, appId), mkEntry(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, SuppressionDurabilityIntegrationTest.CLUSTER.bootstrapServers()), mkEntry(StreamsConfig.POLL_MS_CONFIG, Integer.toString(SuppressionDurabilityIntegrationTest.COMMIT_INTERVAL)), mkEntry(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, Integer.toString(SuppressionDurabilityIntegrationTest.COMMIT_INTERVAL)), mkEntry(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, (eosEnabled ? StreamsConfig.EXACTLY_ONCE : StreamsConfig.AT_LEAST_ONCE)), mkEntry(StreamsConfig.STATE_DIR_CONFIG, TestUtils.tempDirectory().getPath())));
        KafkaStreams driver = IntegrationTestUtils.getStartedStreams(streamsConfig, builder, true);
        try {
            // start by putting some stuff in the buffer
            produceSynchronously(input, Arrays.asList(new KeyValueTimestamp<>("k1", "v1", scaledTime(1L)), new KeyValueTimestamp<>("k2", "v2", scaledTime(2L)), new KeyValueTimestamp<>("k3", "v3", scaledTime(3L))));
            verifyOutput(outputRaw, Arrays.asList(new KeyValueTimestamp<>("v1", 1L, scaledTime(1L)), new KeyValueTimestamp<>("v2", 1L, scaledTime(2L)), new KeyValueTimestamp<>("v3", 1L, scaledTime(3L))));
            MatcherAssert.assertThat(eventCount.get(), CoreMatchers.is(0));
            // flush two of the first three events out.
            produceSynchronously(input, Arrays.asList(new KeyValueTimestamp<>("k4", "v4", scaledTime(4L)), new KeyValueTimestamp<>("k5", "v5", scaledTime(5L))));
            verifyOutput(outputRaw, Arrays.asList(new KeyValueTimestamp<>("v4", 1L, scaledTime(4L)), new KeyValueTimestamp<>("v5", 1L, scaledTime(5L))));
            MatcherAssert.assertThat(eventCount.get(), CoreMatchers.is(2));
            verifyOutput(outputSuppressed, Arrays.asList(new KeyValueTimestamp<>("v1", 1L, scaledTime(1L)), new KeyValueTimestamp<>("v2", 1L, scaledTime(2L))));
            // bounce to ensure that the history, including retractions,
            // get restored properly. (i.e., we shouldn't see those first events again)
            // restart the driver
            driver.close();
            MatcherAssert.assertThat(driver.state(), CoreMatchers.is(NOT_RUNNING));
            driver = IntegrationTestUtils.getStartedStreams(streamsConfig, builder, false);
            // flush those recovered buffered events out.
            produceSynchronously(input, Arrays.asList(new KeyValueTimestamp<>("k6", "v6", scaledTime(6L)), new KeyValueTimestamp<>("k7", "v7", scaledTime(7L)), new KeyValueTimestamp<>("k8", "v8", scaledTime(8L))));
            verifyOutput(outputRaw, Arrays.asList(new KeyValueTimestamp<>("v6", 1L, scaledTime(6L)), new KeyValueTimestamp<>("v7", 1L, scaledTime(7L)), new KeyValueTimestamp<>("v8", 1L, scaledTime(8L))));
            MatcherAssert.assertThat(eventCount.get(), CoreMatchers.is(5));
            verifyOutput(outputSuppressed, Arrays.asList(new KeyValueTimestamp<>("v3", 1L, scaledTime(3L)), new KeyValueTimestamp<>("v4", 1L, scaledTime(4L)), new KeyValueTimestamp<>("v5", 1L, scaledTime(5L))));
        } finally {
            driver.close();
            IntegrationTestUtils.cleanStateAfterTest(SuppressionDurabilityIntegrationTest.CLUSTER, driver);
        }
    }
}

