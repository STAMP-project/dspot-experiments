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
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import kafka.utils.MockTime;
import org.apache.kafka.streams.integration.utils.EmbeddedKafkaCluster;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.apache.kafka.test.IntegrationTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ IntegrationTest.class })
public class RepartitionOptimizingIntegrationTest {
    private static final int NUM_BROKERS = 1;

    private static final String INPUT_TOPIC = "input";

    private static final String COUNT_TOPIC = "outputTopic_0";

    private static final String AGGREGATION_TOPIC = "outputTopic_1";

    private static final String REDUCE_TOPIC = "outputTopic_2";

    private static final String JOINED_TOPIC = "joinedOutputTopic";

    private static final int ONE_REPARTITION_TOPIC = 1;

    private static final int FOUR_REPARTITION_TOPICS = 4;

    private final Pattern repartitionTopicPattern = Pattern.compile("Sink: .*-repartition");

    private Properties streamsConfiguration;

    @ClassRule
    public static final EmbeddedKafkaCluster CLUSTER = new EmbeddedKafkaCluster(RepartitionOptimizingIntegrationTest.NUM_BROKERS);

    private final MockTime mockTime = RepartitionOptimizingIntegrationTest.CLUSTER.time;

    @Test
    public void shouldSendCorrectRecords_OPTIMIZED() throws Exception {
        runIntegrationTest(OPTIMIZE, RepartitionOptimizingIntegrationTest.ONE_REPARTITION_TOPIC);
    }

    @Test
    public void shouldSendCorrectResults_NO_OPTIMIZATION() throws Exception {
        runIntegrationTest(NO_OPTIMIZATION, RepartitionOptimizingIntegrationTest.FOUR_REPARTITION_TOPICS);
    }

    private static class SimpleProcessor extends AbstractProcessor<String, String> {
        final List<String> valueList;

        SimpleProcessor(final List<String> valueList) {
            this.valueList = valueList;
        }

        @Override
        public void process(final String key, final String value) {
            valueList.add(value);
        }
    }

    private static final String EXPECTED_OPTIMIZED_TOPOLOGY = "Topologies:\n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("   Sub-topology: 0\n" + "    Source: KSTREAM-SOURCE-0000000000 (topics: [input])\n") + "      --> KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-MAP-0000000001 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000002, KSTREAM-FILTER-0000000040\n") + "      <-- KSTREAM-SOURCE-0000000000\n") + "    Processor: KSTREAM-FILTER-0000000002 (stores: [])\n") + "      --> KSTREAM-MAPVALUES-0000000003\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000040 (stores: [])\n") + "      --> KSTREAM-SINK-0000000039\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-MAPVALUES-0000000003 (stores: [])\n") + "      --> KSTREAM-PROCESSOR-0000000004\n") + "      <-- KSTREAM-FILTER-0000000002\n") + "    Processor: KSTREAM-PROCESSOR-0000000004 (stores: [])\n") + "      --> none\n") + "      <-- KSTREAM-MAPVALUES-0000000003\n") + "    Sink: KSTREAM-SINK-0000000039 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition)\n") + "      <-- KSTREAM-FILTER-0000000040\n") + "\n") + "  Sub-topology: 1\n") + "    Source: KSTREAM-SOURCE-0000000041 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition])\n") + "      --> KSTREAM-FILTER-0000000020, KSTREAM-AGGREGATE-0000000007, KSTREAM-AGGREGATE-0000000014, KSTREAM-FILTER-0000000029\n") + "    Processor: KSTREAM-AGGREGATE-0000000007 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000006])\n") + "      --> KTABLE-TOSTREAM-0000000011\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KTABLE-TOSTREAM-0000000011 (stores: [])\n") + "      --> KSTREAM-SINK-0000000012, KSTREAM-WINDOWED-0000000034\n") + "      <-- KSTREAM-AGGREGATE-0000000007\n") + "    Processor: KSTREAM-FILTER-0000000020 (stores: [])\n") + "      --> KSTREAM-PEEK-0000000021\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KSTREAM-FILTER-0000000029 (stores: [])\n") + "      --> KSTREAM-WINDOWED-0000000033\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KSTREAM-PEEK-0000000021 (stores: [])\n") + "      --> KSTREAM-REDUCE-0000000023\n") + "      <-- KSTREAM-FILTER-0000000020\n") + "    Processor: KSTREAM-WINDOWED-0000000033 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-JOINTHIS-0000000035\n") + "      <-- KSTREAM-FILTER-0000000029\n") + "    Processor: KSTREAM-WINDOWED-0000000034 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-JOINOTHER-0000000036\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Processor: KSTREAM-AGGREGATE-0000000014 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000013])\n") + "      --> KTABLE-TOSTREAM-0000000018\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KSTREAM-JOINOTHER-0000000036 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000034\n") + "    Processor: KSTREAM-JOINTHIS-0000000035 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000033\n") + "    Processor: KSTREAM-REDUCE-0000000023 (stores: [KSTREAM-REDUCE-STATE-STORE-0000000022])\n") + "      --> KTABLE-TOSTREAM-0000000027\n") + "      <-- KSTREAM-PEEK-0000000021\n") + "    Processor: KSTREAM-MERGE-0000000037 (stores: [])\n") + "      --> KSTREAM-SINK-0000000038\n") + "      <-- KSTREAM-JOINTHIS-0000000035, KSTREAM-JOINOTHER-0000000036\n") + "    Processor: KTABLE-TOSTREAM-0000000018 (stores: [])\n") + "      --> KSTREAM-SINK-0000000019\n") + "      <-- KSTREAM-AGGREGATE-0000000014\n") + "    Processor: KTABLE-TOSTREAM-0000000027 (stores: [])\n") + "      --> KSTREAM-SINK-0000000028\n") + "      <-- KSTREAM-REDUCE-0000000023\n") + "    Sink: KSTREAM-SINK-0000000012 (topic: outputTopic_0)\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Sink: KSTREAM-SINK-0000000019 (topic: outputTopic_1)\n") + "      <-- KTABLE-TOSTREAM-0000000018\n") + "    Sink: KSTREAM-SINK-0000000028 (topic: outputTopic_2)\n") + "      <-- KTABLE-TOSTREAM-0000000027\n") + "    Sink: KSTREAM-SINK-0000000038 (topic: joinedOutputTopic)\n") + "      <-- KSTREAM-MERGE-0000000037\n\n");

    private static final String EXPECTED_UNOPTIMIZED_TOPOLOGY = "Topologies:\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("   Sub-topology: 0\n" + "    Source: KSTREAM-SOURCE-0000000000 (topics: [input])\n") + "      --> KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-MAP-0000000001 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000020, KSTREAM-FILTER-0000000002, KSTREAM-FILTER-0000000009, KSTREAM-FILTER-0000000016, KSTREAM-FILTER-0000000029\n") + "      <-- KSTREAM-SOURCE-0000000000\n") + "    Processor: KSTREAM-FILTER-0000000020 (stores: [])\n") + "      --> KSTREAM-PEEK-0000000021\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000002 (stores: [])\n") + "      --> KSTREAM-MAPVALUES-0000000003\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000029 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000031\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-PEEK-0000000021 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000025\n") + "      <-- KSTREAM-FILTER-0000000020\n") + "    Processor: KSTREAM-FILTER-0000000009 (stores: [])\n") + "      --> KSTREAM-SINK-0000000008\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000016 (stores: [])\n") + "      --> KSTREAM-SINK-0000000015\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000025 (stores: [])\n") + "      --> KSTREAM-SINK-0000000024\n") + "      <-- KSTREAM-PEEK-0000000021\n") + "    Processor: KSTREAM-FILTER-0000000031 (stores: [])\n") + "      --> KSTREAM-SINK-0000000030\n") + "      <-- KSTREAM-FILTER-0000000029\n") + "    Processor: KSTREAM-MAPVALUES-0000000003 (stores: [])\n") + "      --> KSTREAM-PROCESSOR-0000000004\n") + "      <-- KSTREAM-FILTER-0000000002\n") + "    Processor: KSTREAM-PROCESSOR-0000000004 (stores: [])\n") + "      --> none\n") + "      <-- KSTREAM-MAPVALUES-0000000003\n") + "    Sink: KSTREAM-SINK-0000000008 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition)\n") + "      <-- KSTREAM-FILTER-0000000009\n") + "    Sink: KSTREAM-SINK-0000000015 (topic: KSTREAM-AGGREGATE-STATE-STORE-0000000013-repartition)\n") + "      <-- KSTREAM-FILTER-0000000016\n") + "    Sink: KSTREAM-SINK-0000000024 (topic: KSTREAM-REDUCE-STATE-STORE-0000000022-repartition)\n") + "      <-- KSTREAM-FILTER-0000000025\n") + "    Sink: KSTREAM-SINK-0000000030 (topic: KSTREAM-FILTER-0000000029-repartition)\n") + "      <-- KSTREAM-FILTER-0000000031\n") + "\n") + "  Sub-topology: 1\n") + "    Source: KSTREAM-SOURCE-0000000010 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000007\n") + "    Processor: KSTREAM-AGGREGATE-0000000007 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000006])\n") + "      --> KTABLE-TOSTREAM-0000000011\n") + "      <-- KSTREAM-SOURCE-0000000010\n") + "    Processor: KTABLE-TOSTREAM-0000000011 (stores: [])\n") + "      --> KSTREAM-SINK-0000000012, KSTREAM-WINDOWED-0000000034\n") + "      <-- KSTREAM-AGGREGATE-0000000007\n") + "    Source: KSTREAM-SOURCE-0000000032 (topics: [KSTREAM-FILTER-0000000029-repartition])\n") + "      --> KSTREAM-WINDOWED-0000000033\n") + "    Processor: KSTREAM-WINDOWED-0000000033 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-JOINTHIS-0000000035\n") + "      <-- KSTREAM-SOURCE-0000000032\n") + "    Processor: KSTREAM-WINDOWED-0000000034 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-JOINOTHER-0000000036\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Processor: KSTREAM-JOINOTHER-0000000036 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000034\n") + "    Processor: KSTREAM-JOINTHIS-0000000035 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000033\n") + "    Processor: KSTREAM-MERGE-0000000037 (stores: [])\n") + "      --> KSTREAM-SINK-0000000038\n") + "      <-- KSTREAM-JOINTHIS-0000000035, KSTREAM-JOINOTHER-0000000036\n") + "    Sink: KSTREAM-SINK-0000000012 (topic: outputTopic_0)\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Sink: KSTREAM-SINK-0000000038 (topic: joinedOutputTopic)\n") + "      <-- KSTREAM-MERGE-0000000037\n") + "\n") + "  Sub-topology: 2\n") + "    Source: KSTREAM-SOURCE-0000000017 (topics: [KSTREAM-AGGREGATE-STATE-STORE-0000000013-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000014\n") + "    Processor: KSTREAM-AGGREGATE-0000000014 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000013])\n") + "      --> KTABLE-TOSTREAM-0000000018\n") + "      <-- KSTREAM-SOURCE-0000000017\n") + "    Processor: KTABLE-TOSTREAM-0000000018 (stores: [])\n") + "      --> KSTREAM-SINK-0000000019\n") + "      <-- KSTREAM-AGGREGATE-0000000014\n") + "    Sink: KSTREAM-SINK-0000000019 (topic: outputTopic_1)\n") + "      <-- KTABLE-TOSTREAM-0000000018\n") + "\n") + "  Sub-topology: 3\n") + "    Source: KSTREAM-SOURCE-0000000026 (topics: [KSTREAM-REDUCE-STATE-STORE-0000000022-repartition])\n") + "      --> KSTREAM-REDUCE-0000000023\n") + "    Processor: KSTREAM-REDUCE-0000000023 (stores: [KSTREAM-REDUCE-STATE-STORE-0000000022])\n") + "      --> KTABLE-TOSTREAM-0000000027\n") + "      <-- KSTREAM-SOURCE-0000000026\n") + "    Processor: KTABLE-TOSTREAM-0000000027 (stores: [])\n") + "      --> KSTREAM-SINK-0000000028\n") + "      <-- KSTREAM-REDUCE-0000000023\n") + "    Sink: KSTREAM-SINK-0000000028 (topic: outputTopic_2)\n") + "      <-- KTABLE-TOSTREAM-0000000027\n\n");
}

