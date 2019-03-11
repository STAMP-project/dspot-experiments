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


import StreamsConfig.EXACTLY_ONCE;
import StreamsConfig.PROCESSING_GUARANTEE_CONFIG;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.utils.Time;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.apache.kafka.test.IntegrationTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class KTableSourceTopicRestartIntegrationTest {
    private static final int NUM_BROKERS = 3;

    private static final String SOURCE_TOPIC = "source-topic";

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(KTableSourceTopicRestartIntegrationTest.NUM_BROKERS);

    private final Time time = KTableSourceTopicRestartIntegrationTest.CLUSTER.time;

    private KafkaStreams streamsOne;

    private final StreamsBuilder streamsBuilder = new StreamsBuilder();

    private final Map<String, String> readKeyValues = new ConcurrentHashMap<>();

    private static final Properties PRODUCER_CONFIG = new Properties();

    private static final Properties STREAMS_CONFIG = new Properties();

    private Map<String, String> expectedInitialResultsMap;

    private Map<String, String> expectedResultsWithDataWrittenDuringRestoreMap;

    @Test
    public void shouldRestoreAndProgressWhenTopicWrittenToDuringRestorationWithEosDisabled() throws Exception {
        try {
            streamsOne = new KafkaStreams(streamsBuilder.build(), KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG);
            streamsOne.start();
            produceKeyValues("a", "b", "c");
            assertNumberValuesRead(readKeyValues, expectedInitialResultsMap, "Table did not read all values");
            streamsOne.close();
            streamsOne = new KafkaStreams(streamsBuilder.build(), KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG);
            // the state restore listener will append one record to the log
            streamsOne.setGlobalStateRestoreListener(new KTableSourceTopicRestartIntegrationTest.UpdatingSourceTopicOnRestoreStartStateRestoreListener());
            streamsOne.start();
            produceKeyValues("f", "g", "h");
            assertNumberValuesRead(readKeyValues, expectedResultsWithDataWrittenDuringRestoreMap, "Table did not get all values after restart");
        } finally {
            streamsOne.close(Duration.ofSeconds(5));
        }
    }

    @Test
    public void shouldRestoreAndProgressWhenTopicWrittenToDuringRestorationWithEosEnabled() throws Exception {
        try {
            KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG.put(PROCESSING_GUARANTEE_CONFIG, EXACTLY_ONCE);
            streamsOne = new KafkaStreams(streamsBuilder.build(), KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG);
            streamsOne.start();
            produceKeyValues("a", "b", "c");
            assertNumberValuesRead(readKeyValues, expectedInitialResultsMap, "Table did not read all values");
            streamsOne.close();
            streamsOne = new KafkaStreams(streamsBuilder.build(), KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG);
            // the state restore listener will append one record to the log
            streamsOne.setGlobalStateRestoreListener(new KTableSourceTopicRestartIntegrationTest.UpdatingSourceTopicOnRestoreStartStateRestoreListener());
            streamsOne.start();
            produceKeyValues("f", "g", "h");
            assertNumberValuesRead(readKeyValues, expectedResultsWithDataWrittenDuringRestoreMap, "Table did not get all values after restart");
        } finally {
            streamsOne.close(Duration.ofSeconds(5));
        }
    }

    @Test
    public void shouldRestoreAndProgressWhenTopicNotWrittenToDuringRestoration() throws Exception {
        try {
            streamsOne = new KafkaStreams(streamsBuilder.build(), KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG);
            streamsOne.start();
            produceKeyValues("a", "b", "c");
            assertNumberValuesRead(readKeyValues, expectedInitialResultsMap, "Table did not read all values");
            streamsOne.close();
            streamsOne = new KafkaStreams(streamsBuilder.build(), KTableSourceTopicRestartIntegrationTest.STREAMS_CONFIG);
            streamsOne.start();
            produceKeyValues("f", "g", "h");
            final Map<String, String> expectedValues = createExpectedResultsMap("a", "b", "c", "f", "g", "h");
            assertNumberValuesRead(readKeyValues, expectedValues, "Table did not get all values after restart");
        } finally {
            streamsOne.close(Duration.ofSeconds(5));
        }
    }

    private class UpdatingSourceTopicOnRestoreStartStateRestoreListener implements StateRestoreListener {
        @Override
        public void onRestoreStart(final TopicPartition topicPartition, final String storeName, final long startingOffset, final long endingOffset) {
            try {
                produceKeyValues("d");
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onBatchRestored(final TopicPartition topicPartition, final String storeName, final long batchEndOffset, final long numRestored) {
        }

        @Override
        public void onRestoreEnd(final TopicPartition topicPartition, final String storeName, final long totalRestored) {
        }
    }
}

