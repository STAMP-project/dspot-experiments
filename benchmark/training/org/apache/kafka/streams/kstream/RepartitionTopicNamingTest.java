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
package org.apache.kafka.streams.kstream;


import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.OPTIMIZE;
import StreamsConfig.TOPOLOGY_OPTIMIZATION;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.TopologyException;
import org.apache.kafka.streams.processor.AbstractProcessor;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class RepartitionTopicNamingTest {
    private final KeyValueMapper<String, String, String> kvMapper = ( k, v) -> k + v;

    private static final String INPUT_TOPIC = "input";

    private static final String COUNT_TOPIC = "outputTopic_0";

    private static final String AGGREGATION_TOPIC = "outputTopic_1";

    private static final String REDUCE_TOPIC = "outputTopic_2";

    private static final String JOINED_TOPIC = "outputTopicForJoin";

    private final String firstRepartitionTopicName = "count-stream";

    private final String secondRepartitionTopicName = "aggregate-stream";

    private final String thirdRepartitionTopicName = "reduced-stream";

    private final String fourthRepartitionTopicName = "joined-stream";

    private final Pattern repartitionTopicPattern = Pattern.compile("Sink: .*-repartition");

    @Test
    public void shouldReuseFirstRepartitionTopicNameWhenOptimizing() {
        final String optimizedTopology = buildTopology(OPTIMIZE).describe().toString();
        final String unOptimizedTopology = buildTopology(NO_OPTIMIZATION).describe().toString();
        MatcherAssert.assertThat(optimizedTopology, CoreMatchers.is(RepartitionTopicNamingTest.EXPECTED_OPTIMIZED_TOPOLOGY));
        // only one repartition topic
        MatcherAssert.assertThat(1, CoreMatchers.is(getCountOfRepartitionTopicsFound(optimizedTopology, repartitionTopicPattern)));
        // the first named repartition topic
        Assert.assertTrue(optimizedTopology.contains(((firstRepartitionTopicName) + "-repartition")));
        MatcherAssert.assertThat(unOptimizedTopology, CoreMatchers.is(RepartitionTopicNamingTest.EXPECTED_UNOPTIMIZED_TOPOLOGY));
        // now 4 repartition topic
        MatcherAssert.assertThat(4, CoreMatchers.is(getCountOfRepartitionTopicsFound(unOptimizedTopology, repartitionTopicPattern)));
        // all 4 named repartition topics present
        Assert.assertTrue(unOptimizedTopology.contains(((firstRepartitionTopicName) + "-repartition")));
        Assert.assertTrue(unOptimizedTopology.contains(((secondRepartitionTopicName) + "-repartition")));
        Assert.assertTrue(unOptimizedTopology.contains(((thirdRepartitionTopicName) + "-repartition")));
        Assert.assertTrue(unOptimizedTopology.contains(((fourthRepartitionTopicName) + "-left-repartition")));
    }

    // can't use same repartition topic name
    @Test
    public void shouldFailWithSameRepartitionTopicName() {
        try {
            final StreamsBuilder builder = new StreamsBuilder();
            builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping")).count().toStream();
            builder.<String, String>stream("topicII").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping")).count().toStream();
            builder.build();
            Assert.fail("Should not build re-using repartition topic name");
        } catch (final TopologyException te) {
            // ok
        }
    }

    @Test
    public void shouldNotFailWithSameRepartitionTopicNameUsingSameKGroupedStream() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedStream<String, String> kGroupedStream = builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping"));
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10L))).count().toStream().to("output-one");
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(30L))).count().toStream().to("output-two");
        final String topologyString = builder.build().describe().toString();
        MatcherAssert.assertThat(1, CoreMatchers.is(getCountOfRepartitionTopicsFound(topologyString, repartitionTopicPattern)));
        Assert.assertTrue(topologyString.contains("grouping-repartition"));
    }

    @Test
    public void shouldNotFailWithSameRepartitionTopicNameUsingSameTimeWindowStream() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedStream<String, String> kGroupedStream = builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping"));
        final TimeWindowedKStream<String, String> timeWindowedKStream = kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10L)));
        timeWindowedKStream.count().toStream().to("output-one");
        timeWindowedKStream.reduce(( v, v2) -> v + v2).toStream().to("output-two");
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(30L))).count().toStream().to("output-two");
        final String topologyString = builder.build().describe().toString();
        MatcherAssert.assertThat(1, CoreMatchers.is(getCountOfRepartitionTopicsFound(topologyString, repartitionTopicPattern)));
        Assert.assertTrue(topologyString.contains("grouping-repartition"));
    }

    @Test
    public void shouldNotFailWithSameRepartitionTopicNameUsingSameSessionWindowStream() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedStream<String, String> kGroupedStream = builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping"));
        final SessionWindowedKStream<String, String> sessionWindowedKStream = kGroupedStream.windowedBy(SessionWindows.with(Duration.ofMillis(10L)));
        sessionWindowedKStream.count().toStream().to("output-one");
        sessionWindowedKStream.reduce(( v, v2) -> v + v2).toStream().to("output-two");
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(30L))).count().toStream().to("output-two");
        final String topologyString = builder.build().describe().toString();
        MatcherAssert.assertThat(1, CoreMatchers.is(getCountOfRepartitionTopicsFound(topologyString, repartitionTopicPattern)));
        Assert.assertTrue(topologyString.contains("grouping-repartition"));
    }

    @Test
    public void shouldNotFailWithSameRepartitionTopicNameUsingSameKGroupedTable() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedTable<String, String> kGroupedTable = builder.<String, String>table("topic").groupBy(KeyValue::pair, Grouped.as("grouping"));
        kGroupedTable.count().toStream().to("output-count");
        kGroupedTable.reduce(( v, v2) -> v2, ( v, v2) -> v2).toStream().to("output-reduce");
        final String topologyString = builder.build().describe().toString();
        MatcherAssert.assertThat(1, CoreMatchers.is(getCountOfRepartitionTopicsFound(topologyString, repartitionTopicPattern)));
        Assert.assertTrue(topologyString.contains("grouping-repartition"));
    }

    @Test
    public void shouldNotReuseRepartitionNodeWithUnamedRepartitionTopics() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedStream<String, String> kGroupedStream = builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey();
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10L))).count().toStream().to("output-one");
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(30L))).count().toStream().to("output-two");
        final String topologyString = builder.build().describe().toString();
        MatcherAssert.assertThat(2, CoreMatchers.is(getCountOfRepartitionTopicsFound(topologyString, repartitionTopicPattern)));
    }

    @Test
    public void shouldNotReuseRepartitionNodeWithUnamedRepartitionTopicsKGroupedTable() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedTable<String, String> kGroupedTable = builder.<String, String>table("topic").groupBy(KeyValue::pair);
        kGroupedTable.count().toStream().to("output-count");
        kGroupedTable.reduce(( v, v2) -> v2, ( v, v2) -> v2).toStream().to("output-reduce");
        final String topologyString = builder.build().describe().toString();
        MatcherAssert.assertThat(2, CoreMatchers.is(getCountOfRepartitionTopicsFound(topologyString, repartitionTopicPattern)));
    }

    @Test
    public void shouldNotFailWithSameRepartitionTopicNameUsingSameKGroupedStreamOptimizationsOn() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KGroupedStream<String, String> kGroupedStream = builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping"));
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10L))).count();
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(30L))).count();
        final Properties properties = new Properties();
        properties.put(TOPOLOGY_OPTIMIZATION, OPTIMIZE);
        final Topology topology = builder.build(properties);
        MatcherAssert.assertThat(getCountOfRepartitionTopicsFound(topology.describe().toString(), repartitionTopicPattern), CoreMatchers.is(1));
    }

    // can't use same repartition topic name in joins
    @Test
    public void shouldFailWithSameRepartitionTopicNameInJoin() {
        try {
            final StreamsBuilder builder = new StreamsBuilder();
            final KStream<String, String> stream1 = builder.<String, String>stream("topic").selectKey(( k, v) -> k);
            final KStream<String, String> stream2 = builder.<String, String>stream("topic2").selectKey(( k, v) -> k);
            final KStream<String, String> stream3 = builder.<String, String>stream("topic3").selectKey(( k, v) -> k);
            final KStream<String, String> joined = stream1.join(stream2, ( v1, v2) -> v1 + v2, JoinWindows.of(Duration.ofMillis(30L)), Joined.named("join-repartition"));
            joined.join(stream3, ( v1, v2) -> v1 + v2, JoinWindows.of(Duration.ofMillis(30L)), Joined.named("join-repartition"));
            builder.build();
            Assert.fail("Should not build re-using repartition topic name");
        } catch (final TopologyException te) {
            // ok
        }
    }

    @Test
    public void shouldPassWithSameRepartitionTopicNameUsingSameKGroupedStreamOptimized() {
        final StreamsBuilder builder = new StreamsBuilder();
        final Properties properties = new Properties();
        properties.put(TOPOLOGY_OPTIMIZATION, OPTIMIZE);
        final KGroupedStream<String, String> kGroupedStream = builder.<String, String>stream("topic").selectKey(( k, v) -> k).groupByKey(Grouped.as("grouping"));
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(10L))).count();
        kGroupedStream.windowedBy(TimeWindows.of(Duration.ofMillis(30L))).count();
        builder.build(properties);
    }

    @Test
    public void shouldKeepRepartitionTopicNameForJoins() {
        final String expectedLeftRepartitionTopic = "(topic: my-join-left-repartition)";
        final String expectedRightRepartitionTopic = "(topic: my-join-right-repartition)";
        final String joinTopologyFirst = buildStreamJoin(false);
        Assert.assertTrue(joinTopologyFirst.contains(expectedLeftRepartitionTopic));
        Assert.assertTrue(joinTopologyFirst.contains(expectedRightRepartitionTopic));
        final String joinTopologyUpdated = buildStreamJoin(true);
        Assert.assertTrue(joinTopologyUpdated.contains(expectedLeftRepartitionTopic));
        Assert.assertTrue(joinTopologyUpdated.contains(expectedRightRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionTopicNameForGroupByKeyTimeWindows() {
        final String expectedTimeWindowRepartitionTopic = "(topic: time-window-grouping-repartition)";
        final String timeWindowGroupingRepartitionTopology = buildStreamGroupByKeyTimeWindows(false, true);
        Assert.assertTrue(timeWindowGroupingRepartitionTopology.contains(expectedTimeWindowRepartitionTopic));
        final String timeWindowGroupingUpdatedTopology = buildStreamGroupByKeyTimeWindows(true, true);
        Assert.assertTrue(timeWindowGroupingUpdatedTopology.contains(expectedTimeWindowRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionTopicNameForGroupByTimeWindows() {
        final String expectedTimeWindowRepartitionTopic = "(topic: time-window-grouping-repartition)";
        final String timeWindowGroupingRepartitionTopology = buildStreamGroupByKeyTimeWindows(false, false);
        Assert.assertTrue(timeWindowGroupingRepartitionTopology.contains(expectedTimeWindowRepartitionTopic));
        final String timeWindowGroupingUpdatedTopology = buildStreamGroupByKeyTimeWindows(true, false);
        Assert.assertTrue(timeWindowGroupingUpdatedTopology.contains(expectedTimeWindowRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionTopicNameForGroupByKeyNoWindows() {
        final String expectedNoWindowRepartitionTopic = "(topic: kstream-grouping-repartition)";
        final String noWindowGroupingRepartitionTopology = buildStreamGroupByKeyNoWindows(false, true);
        Assert.assertTrue(noWindowGroupingRepartitionTopology.contains(expectedNoWindowRepartitionTopic));
        final String noWindowGroupingUpdatedTopology = buildStreamGroupByKeyNoWindows(true, true);
        Assert.assertTrue(noWindowGroupingUpdatedTopology.contains(expectedNoWindowRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionTopicNameForGroupByNoWindows() {
        final String expectedNoWindowRepartitionTopic = "(topic: kstream-grouping-repartition)";
        final String noWindowGroupingRepartitionTopology = buildStreamGroupByKeyNoWindows(false, false);
        Assert.assertTrue(noWindowGroupingRepartitionTopology.contains(expectedNoWindowRepartitionTopic));
        final String noWindowGroupingUpdatedTopology = buildStreamGroupByKeyNoWindows(true, false);
        Assert.assertTrue(noWindowGroupingUpdatedTopology.contains(expectedNoWindowRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionTopicNameForGroupByKeySessionWindows() {
        final String expectedSessionWindowRepartitionTopic = "(topic: session-window-grouping-repartition)";
        final String sessionWindowGroupingRepartitionTopology = buildStreamGroupByKeySessionWindows(false, true);
        Assert.assertTrue(sessionWindowGroupingRepartitionTopology.contains(expectedSessionWindowRepartitionTopic));
        final String sessionWindowGroupingUpdatedTopology = buildStreamGroupByKeySessionWindows(true, true);
        Assert.assertTrue(sessionWindowGroupingUpdatedTopology.contains(expectedSessionWindowRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionTopicNameForGroupBySessionWindows() {
        final String expectedSessionWindowRepartitionTopic = "(topic: session-window-grouping-repartition)";
        final String sessionWindowGroupingRepartitionTopology = buildStreamGroupByKeySessionWindows(false, false);
        Assert.assertTrue(sessionWindowGroupingRepartitionTopology.contains(expectedSessionWindowRepartitionTopic));
        final String sessionWindowGroupingUpdatedTopology = buildStreamGroupByKeySessionWindows(true, false);
        Assert.assertTrue(sessionWindowGroupingUpdatedTopology.contains(expectedSessionWindowRepartitionTopic));
    }

    @Test
    public void shouldKeepRepartitionNameForGroupByKTable() {
        final String expectedKTableGroupByRepartitionTopic = "(topic: ktable-group-by-repartition)";
        final String ktableGroupByTopology = buildKTableGroupBy(false);
        Assert.assertTrue(ktableGroupByTopology.contains(expectedKTableGroupByRepartitionTopic));
        final String ktableUpdatedGroupByTopology = buildKTableGroupBy(true);
        Assert.assertTrue(ktableUpdatedGroupByTopology.contains(expectedKTableGroupByRepartitionTopic));
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

    private static final String EXPECTED_OPTIMIZED_TOPOLOGY = "Topologies:\n" + ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("   Sub-topology: 0\n" + "    Source: KSTREAM-SOURCE-0000000000 (topics: [input])\n") + "      --> KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-MAP-0000000001 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000002, KSTREAM-FILTER-0000000040\n") + "      <-- KSTREAM-SOURCE-0000000000\n") + "    Processor: KSTREAM-FILTER-0000000002 (stores: [])\n") + "      --> KSTREAM-MAPVALUES-0000000003\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000040 (stores: [])\n") + "      --> KSTREAM-SINK-0000000039\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-MAPVALUES-0000000003 (stores: [])\n") + "      --> KSTREAM-PROCESSOR-0000000004\n") + "      <-- KSTREAM-FILTER-0000000002\n") + "    Processor: KSTREAM-PROCESSOR-0000000004 (stores: [])\n") + "      --> none\n") + "      <-- KSTREAM-MAPVALUES-0000000003\n") + "    Sink: KSTREAM-SINK-0000000039 (topic: count-stream-repartition)\n") + "      <-- KSTREAM-FILTER-0000000040\n") + "\n") + "  Sub-topology: 1\n") + "    Source: KSTREAM-SOURCE-0000000041 (topics: [count-stream-repartition])\n") + "      --> KSTREAM-FILTER-0000000020, KSTREAM-AGGREGATE-0000000007, KSTREAM-AGGREGATE-0000000014, KSTREAM-FILTER-0000000029\n") + "    Processor: KSTREAM-AGGREGATE-0000000007 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000006])\n") + "      --> KTABLE-TOSTREAM-0000000011\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KTABLE-TOSTREAM-0000000011 (stores: [])\n") + "      --> KSTREAM-SINK-0000000012, KSTREAM-WINDOWED-0000000034\n") + "      <-- KSTREAM-AGGREGATE-0000000007\n") + "    Processor: KSTREAM-FILTER-0000000020 (stores: [])\n") + "      --> KSTREAM-PEEK-0000000021\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KSTREAM-FILTER-0000000029 (stores: [])\n") + "      --> KSTREAM-WINDOWED-0000000033\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KSTREAM-PEEK-0000000021 (stores: [])\n") + "      --> KSTREAM-REDUCE-0000000023\n") + "      <-- KSTREAM-FILTER-0000000020\n") + "    Processor: KSTREAM-WINDOWED-0000000033 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-JOINTHIS-0000000035\n") + "      <-- KSTREAM-FILTER-0000000029\n") + "    Processor: KSTREAM-WINDOWED-0000000034 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-JOINOTHER-0000000036\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Processor: KSTREAM-AGGREGATE-0000000014 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000013])\n") + "      --> KTABLE-TOSTREAM-0000000018\n") + "      <-- KSTREAM-SOURCE-0000000041\n") + "    Processor: KSTREAM-JOINOTHER-0000000036 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000034\n") + "    Processor: KSTREAM-JOINTHIS-0000000035 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000033\n") + "    Processor: KSTREAM-REDUCE-0000000023 (stores: [KSTREAM-REDUCE-STATE-STORE-0000000022])\n") + "      --> KTABLE-TOSTREAM-0000000027\n") + "      <-- KSTREAM-PEEK-0000000021\n") + "    Processor: KSTREAM-MERGE-0000000037 (stores: [])\n") + "      --> KSTREAM-SINK-0000000038\n") + "      <-- KSTREAM-JOINTHIS-0000000035, KSTREAM-JOINOTHER-0000000036\n") + "    Processor: KTABLE-TOSTREAM-0000000018 (stores: [])\n") + "      --> KSTREAM-SINK-0000000019\n") + "      <-- KSTREAM-AGGREGATE-0000000014\n") + "    Processor: KTABLE-TOSTREAM-0000000027 (stores: [])\n") + "      --> KSTREAM-SINK-0000000028\n") + "      <-- KSTREAM-REDUCE-0000000023\n") + "    Sink: KSTREAM-SINK-0000000012 (topic: outputTopic_0)\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Sink: KSTREAM-SINK-0000000019 (topic: outputTopic_1)\n") + "      <-- KTABLE-TOSTREAM-0000000018\n") + "    Sink: KSTREAM-SINK-0000000028 (topic: outputTopic_2)\n") + "      <-- KTABLE-TOSTREAM-0000000027\n") + "    Sink: KSTREAM-SINK-0000000038 (topic: outputTopicForJoin)\n") + "      <-- KSTREAM-MERGE-0000000037\n\n");

    private static final String EXPECTED_UNOPTIMIZED_TOPOLOGY = "Topologies:\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("   Sub-topology: 0\n" + "    Source: KSTREAM-SOURCE-0000000000 (topics: [input])\n") + "      --> KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-MAP-0000000001 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000020, KSTREAM-FILTER-0000000002, KSTREAM-FILTER-0000000009, KSTREAM-FILTER-0000000016, KSTREAM-FILTER-0000000029\n") + "      <-- KSTREAM-SOURCE-0000000000\n") + "    Processor: KSTREAM-FILTER-0000000020 (stores: [])\n") + "      --> KSTREAM-PEEK-0000000021\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000002 (stores: [])\n") + "      --> KSTREAM-MAPVALUES-0000000003\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000029 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000031\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-PEEK-0000000021 (stores: [])\n") + "      --> KSTREAM-FILTER-0000000025\n") + "      <-- KSTREAM-FILTER-0000000020\n") + "    Processor: KSTREAM-FILTER-0000000009 (stores: [])\n") + "      --> KSTREAM-SINK-0000000008\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000016 (stores: [])\n") + "      --> KSTREAM-SINK-0000000015\n") + "      <-- KSTREAM-MAP-0000000001\n") + "    Processor: KSTREAM-FILTER-0000000025 (stores: [])\n") + "      --> KSTREAM-SINK-0000000024\n") + "      <-- KSTREAM-PEEK-0000000021\n") + "    Processor: KSTREAM-FILTER-0000000031 (stores: [])\n") + "      --> KSTREAM-SINK-0000000030\n") + "      <-- KSTREAM-FILTER-0000000029\n") + "    Processor: KSTREAM-MAPVALUES-0000000003 (stores: [])\n") + "      --> KSTREAM-PROCESSOR-0000000004\n") + "      <-- KSTREAM-FILTER-0000000002\n") + "    Processor: KSTREAM-PROCESSOR-0000000004 (stores: [])\n") + "      --> none\n") + "      <-- KSTREAM-MAPVALUES-0000000003\n") + "    Sink: KSTREAM-SINK-0000000008 (topic: count-stream-repartition)\n") + "      <-- KSTREAM-FILTER-0000000009\n") + "    Sink: KSTREAM-SINK-0000000015 (topic: aggregate-stream-repartition)\n") + "      <-- KSTREAM-FILTER-0000000016\n") + "    Sink: KSTREAM-SINK-0000000024 (topic: reduced-stream-repartition)\n") + "      <-- KSTREAM-FILTER-0000000025\n") + "    Sink: KSTREAM-SINK-0000000030 (topic: joined-stream-left-repartition)\n") + "      <-- KSTREAM-FILTER-0000000031\n") + "\n") + "  Sub-topology: 1\n") + "    Source: KSTREAM-SOURCE-0000000010 (topics: [count-stream-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000007\n") + "    Processor: KSTREAM-AGGREGATE-0000000007 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000006])\n") + "      --> KTABLE-TOSTREAM-0000000011\n") + "      <-- KSTREAM-SOURCE-0000000010\n") + "    Processor: KTABLE-TOSTREAM-0000000011 (stores: [])\n") + "      --> KSTREAM-SINK-0000000012, KSTREAM-WINDOWED-0000000034\n") + "      <-- KSTREAM-AGGREGATE-0000000007\n") + "    Source: KSTREAM-SOURCE-0000000032 (topics: [joined-stream-left-repartition])\n") + "      --> KSTREAM-WINDOWED-0000000033\n") + "    Processor: KSTREAM-WINDOWED-0000000033 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-JOINTHIS-0000000035\n") + "      <-- KSTREAM-SOURCE-0000000032\n") + "    Processor: KSTREAM-WINDOWED-0000000034 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-JOINOTHER-0000000036\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Processor: KSTREAM-JOINOTHER-0000000036 (stores: [KSTREAM-JOINTHIS-0000000035-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000034\n") + "    Processor: KSTREAM-JOINTHIS-0000000035 (stores: [KSTREAM-JOINOTHER-0000000036-store])\n") + "      --> KSTREAM-MERGE-0000000037\n") + "      <-- KSTREAM-WINDOWED-0000000033\n") + "    Processor: KSTREAM-MERGE-0000000037 (stores: [])\n") + "      --> KSTREAM-SINK-0000000038\n") + "      <-- KSTREAM-JOINTHIS-0000000035, KSTREAM-JOINOTHER-0000000036\n") + "    Sink: KSTREAM-SINK-0000000012 (topic: outputTopic_0)\n") + "      <-- KTABLE-TOSTREAM-0000000011\n") + "    Sink: KSTREAM-SINK-0000000038 (topic: outputTopicForJoin)\n") + "      <-- KSTREAM-MERGE-0000000037\n") + "\n") + "  Sub-topology: 2\n") + "    Source: KSTREAM-SOURCE-0000000017 (topics: [aggregate-stream-repartition])\n") + "      --> KSTREAM-AGGREGATE-0000000014\n") + "    Processor: KSTREAM-AGGREGATE-0000000014 (stores: [KSTREAM-AGGREGATE-STATE-STORE-0000000013])\n") + "      --> KTABLE-TOSTREAM-0000000018\n") + "      <-- KSTREAM-SOURCE-0000000017\n") + "    Processor: KTABLE-TOSTREAM-0000000018 (stores: [])\n") + "      --> KSTREAM-SINK-0000000019\n") + "      <-- KSTREAM-AGGREGATE-0000000014\n") + "    Sink: KSTREAM-SINK-0000000019 (topic: outputTopic_1)\n") + "      <-- KTABLE-TOSTREAM-0000000018\n") + "\n") + "  Sub-topology: 3\n") + "    Source: KSTREAM-SOURCE-0000000026 (topics: [reduced-stream-repartition])\n") + "      --> KSTREAM-REDUCE-0000000023\n") + "    Processor: KSTREAM-REDUCE-0000000023 (stores: [KSTREAM-REDUCE-STATE-STORE-0000000022])\n") + "      --> KTABLE-TOSTREAM-0000000027\n") + "      <-- KSTREAM-SOURCE-0000000026\n") + "    Processor: KTABLE-TOSTREAM-0000000027 (stores: [])\n") + "      --> KSTREAM-SINK-0000000028\n") + "      <-- KSTREAM-REDUCE-0000000023\n") + "    Sink: KSTREAM-SINK-0000000028 (topic: outputTopic_2)\n") + "      <-- KTABLE-TOSTREAM-0000000027\n\n");
}

