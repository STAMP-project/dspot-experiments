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
import java.util.Properties;
import kafka.utils.MockTime;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.kstream.Reducer;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.test.IntegrationTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


/**
 * Similar to KStreamAggregationIntegrationTest but with dedupping enabled
 * by virtue of having a large commit interval
 */
@Category({ IntegrationTest.class })
public class KStreamAggregationDedupIntegrationTest {
    private static final int NUM_BROKERS = 1;

    private static final long COMMIT_INTERVAL_MS = 300L;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(KStreamAggregationDedupIntegrationTest.NUM_BROKERS);

    private final MockTime mockTime = KStreamAggregationDedupIntegrationTest.CLUSTER.time;

    private static volatile int testNo = 0;

    private StreamsBuilder builder;

    private Properties streamsConfiguration;

    private KafkaStreams kafkaStreams;

    private String streamOneInput;

    private String outputTopic;

    private KGroupedStream<String, String> groupedStream;

    private Reducer<String> reducer;

    private KStream<Integer, String> stream;

    @Test
    public void shouldReduce() throws Exception {
        produceMessages(System.currentTimeMillis());
        groupedStream.reduce(reducer, Materialized.as("reduce-by-key")).toStream().to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        startStreams();
        produceMessages(System.currentTimeMillis());
        validateReceivedMessages(new StringDeserializer(), new StringDeserializer(), Arrays.asList(KeyValue.pair("A", "A:A"), KeyValue.pair("B", "B:B"), KeyValue.pair("C", "C:C"), KeyValue.pair("D", "D:D"), KeyValue.pair("E", "E:E")));
    }

    @Test
    public void shouldReduceWindowed() throws Exception {
        final long firstBatchTimestamp = (System.currentTimeMillis()) - 1000;
        produceMessages(firstBatchTimestamp);
        final long secondBatchTimestamp = System.currentTimeMillis();
        produceMessages(secondBatchTimestamp);
        produceMessages(secondBatchTimestamp);
        groupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(500L))).reduce(reducer, Materialized.as("reduce-time-windows")).toStream(( windowedKey, value) -> ((windowedKey.key()) + "@") + (windowedKey.window().start())).to(outputTopic, Produced.with(Serdes.String(), Serdes.String()));
        startStreams();
        final long firstBatchWindow = (firstBatchTimestamp / 500) * 500;
        final long secondBatchWindow = (secondBatchTimestamp / 500) * 500;
        validateReceivedMessages(new StringDeserializer(), new StringDeserializer(), Arrays.asList(new KeyValue(("A@" + firstBatchWindow), "A"), new KeyValue(("A@" + secondBatchWindow), "A:A"), new KeyValue(("B@" + firstBatchWindow), "B"), new KeyValue(("B@" + secondBatchWindow), "B:B"), new KeyValue(("C@" + firstBatchWindow), "C"), new KeyValue(("C@" + secondBatchWindow), "C:C"), new KeyValue(("D@" + firstBatchWindow), "D"), new KeyValue(("D@" + secondBatchWindow), "D:D"), new KeyValue(("E@" + firstBatchWindow), "E"), new KeyValue(("E@" + secondBatchWindow), "E:E")));
    }

    @Test
    public void shouldGroupByKey() throws Exception {
        final long timestamp = mockTime.milliseconds();
        produceMessages(timestamp);
        produceMessages(timestamp);
        stream.groupByKey(Grouped.with(Serdes.Integer(), Serdes.String())).windowedBy(TimeWindows.of(Duration.ofMillis(500L))).count(Materialized.as("count-windows")).toStream(( windowedKey, value) -> ((windowedKey.key()) + "@") + (windowedKey.window().start())).to(outputTopic, Produced.with(Serdes.String(), Serdes.Long()));
        startStreams();
        final long window = (timestamp / 500) * 500;
        validateReceivedMessages(new StringDeserializer(), new LongDeserializer(), Arrays.asList(KeyValue.pair(("1@" + window), 2L), KeyValue.pair(("2@" + window), 2L), KeyValue.pair(("3@" + window), 2L), KeyValue.pair(("4@" + window), 2L), KeyValue.pair(("5@" + window), 2L)));
    }
}

