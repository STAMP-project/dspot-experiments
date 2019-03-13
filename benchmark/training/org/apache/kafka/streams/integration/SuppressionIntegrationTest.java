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


import java.time.Duration;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValueTimestamp;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.integration.utils.IntegrationTestUtils;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Suppressed;
import org.apache.kafka.test.IntegrationTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static BufferConfig.maxBytes;
import static BufferConfig.maxRecords;


@Category({ IntegrationTest.class })
public class SuppressionIntegrationTest {
    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(1, mkProperties(mkMap()), 0L);

    private static final StringSerializer STRING_SERIALIZER = new StringSerializer();

    private static final Serde<String> STRING_SERDE = Serdes.String();

    private static final int COMMIT_INTERVAL = 100;

    @Test
    public void shouldShutdownWhenRecordConstraintIsViolated() throws InterruptedException {
        final String testId = "-shouldShutdownWhenRecordConstraintIsViolated";
        final String appId = (getClass().getSimpleName().toLowerCase(Locale.getDefault())) + testId;
        final String input = "input" + testId;
        final String outputSuppressed = "output-suppressed" + testId;
        final String outputRaw = "output-raw" + testId;
        IntegrationTestUtils.cleanStateBeforeTest(SuppressionIntegrationTest.CLUSTER, input, outputRaw, outputSuppressed);
        final StreamsBuilder builder = new StreamsBuilder();
        final KTable<String, Long> valueCounts = buildCountsTable(input, builder);
        valueCounts.suppress(Suppressed.untilTimeLimit(Duration.ofMillis(Long.MAX_VALUE), maxRecords(1L).shutDownWhenFull())).toStream().to(outputSuppressed, Produced.with(SuppressionIntegrationTest.STRING_SERDE, Serdes.Long()));
        valueCounts.toStream().to(outputRaw, Produced.with(SuppressionIntegrationTest.STRING_SERDE, Serdes.Long()));
        final Properties streamsConfig = getStreamsConfig(appId);
        final KafkaStreams driver = IntegrationTestUtils.getStartedStreams(streamsConfig, builder, true);
        try {
            produceSynchronously(input, Arrays.asList(new KeyValueTimestamp<>("k1", "v1", scaledTime(0L)), new KeyValueTimestamp<>("k1", "v2", scaledTime(1L)), new KeyValueTimestamp<>("k2", "v1", scaledTime(2L)), new KeyValueTimestamp<>("x", "x", scaledTime(3L))));
            verifyErrorShutdown(driver);
        } finally {
            driver.close();
            IntegrationTestUtils.cleanStateAfterTest(SuppressionIntegrationTest.CLUSTER, driver);
        }
    }

    @Test
    public void shouldShutdownWhenBytesConstraintIsViolated() throws InterruptedException {
        final String testId = "-shouldShutdownWhenBytesConstraintIsViolated";
        final String appId = (getClass().getSimpleName().toLowerCase(Locale.getDefault())) + testId;
        final String input = "input" + testId;
        final String outputSuppressed = "output-suppressed" + testId;
        final String outputRaw = "output-raw" + testId;
        IntegrationTestUtils.cleanStateBeforeTest(SuppressionIntegrationTest.CLUSTER, input, outputRaw, outputSuppressed);
        final StreamsBuilder builder = new StreamsBuilder();
        final KTable<String, Long> valueCounts = buildCountsTable(input, builder);
        // this is a bit brittle, but I happen to know that the entries are a little over 100 bytes in size.
        valueCounts.suppress(Suppressed.untilTimeLimit(Duration.ofMillis(Long.MAX_VALUE), maxBytes(200L).shutDownWhenFull())).toStream().to(outputSuppressed, Produced.with(SuppressionIntegrationTest.STRING_SERDE, Serdes.Long()));
        valueCounts.toStream().to(outputRaw, Produced.with(SuppressionIntegrationTest.STRING_SERDE, Serdes.Long()));
        final Properties streamsConfig = getStreamsConfig(appId);
        final KafkaStreams driver = IntegrationTestUtils.getStartedStreams(streamsConfig, builder, true);
        try {
            produceSynchronously(input, Arrays.asList(new KeyValueTimestamp<>("k1", "v1", scaledTime(0L)), new KeyValueTimestamp<>("k1", "v2", scaledTime(1L)), new KeyValueTimestamp<>("k2", "v1", scaledTime(2L)), new KeyValueTimestamp<>("x", "x", scaledTime(3L))));
            verifyErrorShutdown(driver);
        } finally {
            driver.close();
            IntegrationTestUtils.cleanStateAfterTest(SuppressionIntegrationTest.CLUSTER, driver);
        }
    }
}

