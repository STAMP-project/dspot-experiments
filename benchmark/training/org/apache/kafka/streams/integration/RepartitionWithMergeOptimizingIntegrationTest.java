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


import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.OPTIMIZE;
import java.util.Properties;
import java.util.regex.Pattern;
import kafka.utils.MockTime;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.test.IntegrationTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class RepartitionWithMergeOptimizingIntegrationTest {
    private static final int NUM_BROKERS = 1;

    private static final String INPUT_A_TOPIC = "inputA";

    private static final String INPUT_B_TOPIC = "inputB";

    private static final String COUNT_TOPIC = "outputTopic_0";

    private static final String COUNT_STRING_TOPIC = "outputTopic_1";

    private static final int ONE_REPARTITION_TOPIC = 1;

    private static final int TWO_REPARTITION_TOPICS = 2;

    private final Pattern repartitionTopicPattern = Pattern.compile("Sink: .*-repartition");

    private Properties streamsConfiguration;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(RepartitionWithMergeOptimizingIntegrationTest.NUM_BROKERS);

    private final MockTime mockTime = RepartitionWithMergeOptimizingIntegrationTest.CLUSTER.time;

    @Test
    public void shouldSendCorrectRecords_OPTIMIZED() throws Exception {
        runIntegrationTest(OPTIMIZE, RepartitionWithMergeOptimizingIntegrationTest.ONE_REPARTITION_TOPIC);
    }

    @Test
    public void shouldSendCorrectResults_NO_OPTIMIZATION() throws Exception {
        runIntegrationTest(NO_OPTIMIZATION, RepartitionWithMergeOptimizingIntegrationTest.TWO_REPARTITION_TOPICS);
    }

    private static final String EXPECTED_OPTIMIZED_TOPOLOGY = "Topologies:\n" + ((((((((((((((((((((((((((((((((((((((((("   Sub-topology: 0\n" + "    Source: KSTREAM-SOURCE-0000000000 (topics: [inputA])\n") + "      --> KSTREAM-MAP-0000000002\n") + "    Source: KSTREAM-SOURCE-0000000001 (topics: [inputB])\n") + "      --> KSTREAM-MAP-0000000003\n") + "    Processor: KSTREAM-MAP-0000000002 (stores: [])\n") + "      --> KSTREAM-MERGE-0000000004\n") + "      <-- KSTREAM-SOURCE-0000000000\n") + "    Processor: KSTREAM-MAP-0000000003 (stores: [])\n") + "      --> KSTREAM-MERGE-0000000004\n") + "      <-- KSTREAM-SOURCE-0000000001\n") + "    Processor: KSTREAM-MERGE-0000000004 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000021\n") + "      <-- KSTREAM-MAP-0000000002, KSTREAM-MAP-0000000003\n") + "    Processor: KSTREAM-FILTER-0000000021 (stores: [])\n") + "      --> KSTREAM-SINK-0000000020\n") + "      <-- KSTREAM-MERGE-0000000004\n") + "    Sink: KSTREAM-SINK-0000000020 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000005-repartition)\n") + "      <-- KSTREAM-FILTER-0000000021\n") + "\n") + "  Sub-topology: 1\n") + "    Source: KSTREAM-SOURCE-0000000022 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000005-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000006, KSTREAM-AGGREGATE-0000000013\n") + "    Processor: KSTREAM-AGGREGATE-0000000013 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000012])\n") + "      --> KTABLE-TOSTREAM-0000000017\n") + "      <-- KSTREAM-SOURCE-0000000022\n") + "    Processor: KSTREAM-AGGREGATE-0000000006 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000005])\n") + "      --> KTABLE-TOSTREAM-0000000010\n") + "      <-- KSTREAM-SOURCE-0000000022\n") + "    Processor: KTABLE-TOSTREAM-0000000017 (stores: [])\n") + "      --> KSTREAM-MAPVALUES-0000000018\n") + "      <-- KSTREAM-AGGREGATE-0000000013\n") + "    Processor: KSTREAM-MAPVALUES-0000000018 (stores: [])\n") + "      --> KSTREAM-SINK-0000000019\n") + "      <-- KTABLE-TOSTREAM-0000000017\n") + "    Processor: KTABLE-TOSTREAM-0000000010 (stores: [])\n") + "      --> KSTREAM-SINK-0000000011\n") + "      <-- KSTREAM-AGGREGATE-0000000006\n") + "    Sink: KSTREAM-SINK-0000000011 (topic: outputTopic_0)\n") + "      <-- KTABLE-TOSTREAM-0000000010\n") + "    Sink: KSTREAM-SINK-0000000019 (topic: outputTopic_1)\n") + "      <-- KSTREAM-MAPVALUES-0000000018\n\n");

    private static final String EXPECTED_UNOPTIMIZED_TOPOLOGY = "Topologies:\n" + (((((((((((((((((((((((((((((((((((((((((((((((((("   Sub-topology: 0\n" + "    Source: KSTREAM-SOURCE-0000000000 (topics: [inputA])\n") + "      --> KSTREAM-MAP-0000000002\n") + "    Source: KSTREAM-SOURCE-0000000001 (topics: [inputB])\n") + "      --> KSTREAM-MAP-0000000003\n") + "    Processor: KSTREAM-MAP-0000000002 (stores: [])\n") + "      --> KSTREAM-MERGE-0000000004\n") + "      <-- KSTREAM-SOURCE-0000000000\n") + "    Processor: KSTREAM-MAP-0000000003 (stores: [])\n") + "      --> KSTREAM-MERGE-0000000004\n") + "      <-- KSTREAM-SOURCE-0000000001\n") + "    Processor: KSTREAM-MERGE-0000000004 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000008, KSTREAM-FILTER-0000000015\n") + "      <-- KSTREAM-MAP-0000000002, KSTREAM-MAP-0000000003\n") + "    Processor: KSTREAM-FILTER-0000000008 (stores: [])\n") + "      --> KSTREAM-SINK-0000000007\n") + "      <-- KSTREAM-MERGE-0000000004\n") + "    Processor: KSTREAM-FILTER-0000000015 (stores: [])\n") + "      --> KSTREAM-SINK-0000000014\n") + "      <-- KSTREAM-MERGE-0000000004\n") + "    Sink: KSTREAM-SINK-0000000007 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000005-repartition)\n") + "      <-- KSTREAM-FILTER-0000000008\n") + "    Sink: KSTREAM-SINK-0000000014 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000012-repartition)\n") + "      <-- KSTREAM-FILTER-0000000015\n") + "\n") + "  Sub-topology: 1\n") + "    Source: KSTREAM-SOURCE-0000000009 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000005-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000006\n") + "    Processor: KSTREAM-AGGREGATE-0000000006 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000005])\n") + "      --> KTABLE-TOSTREAM-0000000010\n") + "      <-- KSTREAM-SOURCE-0000000009\n") + "    Processor: KTABLE-TOSTREAM-0000000010 (stores: [])\n") + "      --> KSTREAM-SINK-0000000011\n") + "      <-- KSTREAM-AGGREGATE-0000000006\n") + "    Sink: KSTREAM-SINK-0000000011 (topic: outputTopic_0)\n") + "      <-- KTABLE-TOSTREAM-0000000010\n") + "\n") + "  Sub-topology: 2\n") + "    Source: KSTREAM-SOURCE-0000000016 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000012-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000013\n") + "    Processor: KSTREAM-AGGREGATE-0000000013 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000012])\n") + "      --> KTABLE-TOSTREAM-0000000017\n") + "      <-- KSTREAM-SOURCE-0000000016\n") + "    Processor: KTABLE-TOSTREAM-0000000017 (stores: [])\n") + "      --> KSTREAM-MAPVALUES-0000000018\n") + "      <-- KSTREAM-AGGREGATE-0000000013\n") + "    Processor: KSTREAM-MAPVALUES-0000000018 (stores: [])\n") + "      --> KSTREAM-SINK-0000000019\n") + "      <-- KTABLE-TOSTREAM-0000000017\n") + "    Sink: KSTREAM-SINK-0000000019 (topic: outputTopic_1)\n") + "      <-- KSTREAM-MAPVALUES-0000000018\n\n");
}

