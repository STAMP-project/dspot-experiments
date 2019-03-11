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
package org.apache.kafka.streams.kstream.internals;


import AutoOffsetReset.EARLIEST;
import AutoOffsetReset.LATEST;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.internals.InternalTopologyBuilder;
import org.apache.kafka.streams.processor.internals.ProcessorTopology;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.test.MockMapper;
import org.apache.kafka.test.MockTimestampExtractor;
import org.apache.kafka.test.MockValueJoiner;
import org.apache.kafka.test.StreamsTestUtils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class InternalStreamsBuilderTest {
    private static final String APP_ID = "app-id";

    private final InternalStreamsBuilder builder = new InternalStreamsBuilder(new InternalTopologyBuilder());

    private final ConsumedInternal<String, String> consumed = new ConsumedInternal();

    private final String storePrefix = "prefix-";

    private final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materialized = new MaterializedInternal(Materialized.as("test-store"), builder, storePrefix);

    @Test
    public void testNewName() {
        Assert.assertEquals("X-0000000000", builder.newProcessorName("X-"));
        Assert.assertEquals("Y-0000000001", builder.newProcessorName("Y-"));
        Assert.assertEquals("Z-0000000002", builder.newProcessorName("Z-"));
        final InternalStreamsBuilder newBuilder = new InternalStreamsBuilder(new InternalTopologyBuilder());
        Assert.assertEquals("X-0000000000", newBuilder.newProcessorName("X-"));
        Assert.assertEquals("Y-0000000001", newBuilder.newProcessorName("Y-"));
        Assert.assertEquals("Z-0000000002", newBuilder.newProcessorName("Z-"));
    }

    @Test
    public void testNewStoreName() {
        Assert.assertEquals("X-STATE-STORE-0000000000", builder.newStoreName("X-"));
        Assert.assertEquals("Y-STATE-STORE-0000000001", builder.newStoreName("Y-"));
        Assert.assertEquals("Z-STATE-STORE-0000000002", builder.newStoreName("Z-"));
        final InternalStreamsBuilder newBuilder = new InternalStreamsBuilder(new InternalTopologyBuilder());
        Assert.assertEquals("X-STATE-STORE-0000000000", newBuilder.newStoreName("X-"));
        Assert.assertEquals("Y-STATE-STORE-0000000001", newBuilder.newStoreName("Y-"));
        Assert.assertEquals("Z-STATE-STORE-0000000002", newBuilder.newStoreName("Z-"));
    }

    @Test
    public void shouldHaveCorrectSourceTopicsForTableFromMergedStream() {
        final String topic1 = "topic-1";
        final String topic2 = "topic-2";
        final String topic3 = "topic-3";
        final KStream<String, String> source1 = builder.stream(Collections.singleton(topic1), consumed);
        final KStream<String, String> source2 = builder.stream(Collections.singleton(topic2), consumed);
        final KStream<String, String> source3 = builder.stream(Collections.singleton(topic3), consumed);
        final KStream<String, String> processedSource1 = source1.mapValues(new org.apache.kafka.streams.kstream.ValueMapper<String, String>() {
            @Override
            public String apply(final String value) {
                return value;
            }
        }).filter(new org.apache.kafka.streams.kstream.Predicate<String, String>() {
            @Override
            public boolean test(final String key, final String value) {
                return true;
            }
        });
        final KStream<String, String> processedSource2 = source2.filter(new org.apache.kafka.streams.kstream.Predicate<String, String>() {
            @Override
            public boolean test(final String key, final String value) {
                return true;
            }
        });
        final KStream<String, String> merged = processedSource1.merge(processedSource2).merge(source3);
        merged.groupByKey().count(Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("my-table"));
        builder.buildAndOptimizeTopology();
        final Map<String, List<String>> actual = builder.internalTopologyBuilder.stateStoreNameToSourceTopics();
        Assert.assertEquals(Arrays.asList("topic-1", "topic-2", "topic-3"), actual.get("my-table"));
    }

    @Test
    public void shouldNotMaterializeSourceKTableIfNotRequired() {
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.with(null, null), builder, storePrefix);
        final KTable table1 = builder.table("topic2", consumed, materializedInternal);
        builder.buildAndOptimizeTopology();
        final ProcessorTopology topology = builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID))).build(null);
        Assert.assertEquals(0, topology.stateStores().size());
        Assert.assertEquals(0, topology.storeToChangelogTopic().size());
        Assert.assertNull(table1.queryableStoreName());
    }

    @Test
    public void shouldBuildGlobalTableWithNonQueryableStoreName() {
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.with(null, null), builder, storePrefix);
        final GlobalKTable<String, String> table1 = builder.globalTable("topic2", consumed, materializedInternal);
        Assert.assertNull(table1.queryableStoreName());
    }

    @Test
    public void shouldBuildGlobalTableWithQueryaIbleStoreName() {
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.as("globalTable"), builder, storePrefix);
        final GlobalKTable<String, String> table1 = builder.globalTable("topic2", consumed, materializedInternal);
        Assert.assertEquals("globalTable", table1.queryableStoreName());
    }

    @Test
    public void shouldBuildSimpleGlobalTableTopology() {
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.as("globalTable"), builder, storePrefix);
        builder.globalTable("table", consumed, materializedInternal);
        builder.buildAndOptimizeTopology();
        final ProcessorTopology topology = builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID))).buildGlobalStateTopology();
        final List<StateStore> stateStores = topology.globalStateStores();
        Assert.assertEquals(1, stateStores.size());
        Assert.assertEquals("globalTable", stateStores.get(0).name());
    }

    @Test
    public void shouldBuildGlobalTopologyWithAllGlobalTables() {
        {
            final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.as("global1"), builder, storePrefix);
            builder.globalTable("table", consumed, materializedInternal);
        }
        {
            final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.as("global2"), builder, storePrefix);
            builder.globalTable("table2", consumed, materializedInternal);
        }
        builder.buildAndOptimizeTopology();
        doBuildGlobalTopologyWithAllGlobalTables();
    }

    @Test
    public void shouldAddGlobalTablesToEachGroup() {
        final String one = "globalTable";
        final String two = "globalTable2";
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.as(one), builder, storePrefix);
        final GlobalKTable<String, String> globalTable = builder.globalTable("table", consumed, materializedInternal);
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal2 = new MaterializedInternal(Materialized.as(two), builder, storePrefix);
        final GlobalKTable<String, String> globalTable2 = builder.globalTable("table2", consumed, materializedInternal2);
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternalNotGlobal = new MaterializedInternal(Materialized.as("not-global"), builder, storePrefix);
        builder.table("not-global", consumed, materializedInternalNotGlobal);
        final KeyValueMapper<String, String, String> kvMapper = ( key, value) -> value;
        final KStream<String, String> stream = builder.stream(Collections.singleton("t1"), consumed);
        stream.leftJoin(globalTable, kvMapper, MockValueJoiner.TOSTRING_JOINER);
        final KStream<String, String> stream2 = builder.stream(Collections.singleton("t2"), consumed);
        stream2.leftJoin(globalTable2, kvMapper, MockValueJoiner.TOSTRING_JOINER);
        final Map<Integer, Set<String>> nodeGroups = builder.internalTopologyBuilder.nodeGroups();
        for (final Integer groupId : nodeGroups.keySet()) {
            final ProcessorTopology topology = builder.internalTopologyBuilder.build(groupId);
            final List<StateStore> stateStores = topology.globalStateStores();
            final Set<String> names = new HashSet<>();
            for (final StateStore stateStore : stateStores) {
                names.add(stateStore.name());
            }
            Assert.assertEquals(2, stateStores.size());
            Assert.assertTrue(names.contains(one));
            Assert.assertTrue(names.contains(two));
        }
    }

    @Test
    public void shouldMapStateStoresToCorrectSourceTopics() {
        final KStream<String, String> playEvents = builder.stream(Collections.singleton("events"), consumed);
        final MaterializedInternal<String, String, KeyValueStore<Bytes, byte[]>> materializedInternal = new MaterializedInternal(Materialized.as("table-store"), builder, storePrefix);
        final KTable<String, String> table = builder.table("table-topic", consumed, materializedInternal);
        final KStream<String, String> mapped = playEvents.map(MockMapper.<String, String>selectValueKeyValueMapper());
        mapped.leftJoin(table, MockValueJoiner.TOSTRING_JOINER).groupByKey().count(Materialized.as("count"));
        builder.buildAndOptimizeTopology();
        builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID)));
        Assert.assertEquals(Collections.singletonList("table-topic"), builder.internalTopologyBuilder.stateStoreNameToSourceTopics().get("table-store"));
        Assert.assertEquals(Collections.singletonList(((InternalStreamsBuilderTest.APP_ID) + "-KSTREAM-MAP-0000000003-repartition")), builder.internalTopologyBuilder.stateStoreNameToSourceTopics().get("count"));
    }

    @Test
    public void shouldAddTopicToEarliestAutoOffsetResetList() {
        final String topicName = "topic-1";
        final ConsumedInternal consumed = new ConsumedInternal(Consumed.with(EARLIEST));
        builder.stream(Collections.singleton(topicName), consumed);
        builder.buildAndOptimizeTopology();
        Assert.assertTrue(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicName).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicName).matches());
    }

    @Test
    public void shouldAddTopicToLatestAutoOffsetResetList() {
        final String topicName = "topic-1";
        final ConsumedInternal consumed = new ConsumedInternal(Consumed.with(LATEST));
        builder.stream(Collections.singleton(topicName), consumed);
        builder.buildAndOptimizeTopology();
        Assert.assertTrue(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicName).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicName).matches());
    }

    @Test
    public void shouldAddTableToEarliestAutoOffsetResetList() {
        final String topicName = "topic-1";
        builder.table(topicName, new ConsumedInternal(Consumed.<String, String>with(EARLIEST)), materialized);
        builder.buildAndOptimizeTopology();
        Assert.assertTrue(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicName).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicName).matches());
    }

    @Test
    public void shouldAddTableToLatestAutoOffsetResetList() {
        final String topicName = "topic-1";
        builder.table(topicName, new ConsumedInternal(Consumed.<String, String>with(LATEST)), materialized);
        builder.buildAndOptimizeTopology();
        Assert.assertTrue(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicName).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicName).matches());
    }

    @Test
    public void shouldNotAddTableToOffsetResetLists() {
        final String topicName = "topic-1";
        builder.table(topicName, consumed, materialized);
        Assert.assertFalse(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicName).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicName).matches());
    }

    @Test
    public void shouldNotAddRegexTopicsToOffsetResetLists() {
        final Pattern topicPattern = Pattern.compile("topic-\\d");
        final String topic = "topic-5";
        builder.stream(topicPattern, consumed);
        Assert.assertFalse(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topic).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topic).matches());
    }

    @Test
    public void shouldAddRegexTopicToEarliestAutoOffsetResetList() {
        final Pattern topicPattern = Pattern.compile("topic-\\d+");
        final String topicTwo = "topic-500000";
        builder.stream(topicPattern, new ConsumedInternal(Consumed.with(EARLIEST)));
        builder.buildAndOptimizeTopology();
        Assert.assertTrue(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicTwo).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicTwo).matches());
    }

    @Test
    public void shouldAddRegexTopicToLatestAutoOffsetResetList() {
        final Pattern topicPattern = Pattern.compile("topic-\\d+");
        final String topicTwo = "topic-1000000";
        builder.stream(topicPattern, new ConsumedInternal(Consumed.with(LATEST)));
        builder.buildAndOptimizeTopology();
        Assert.assertTrue(builder.internalTopologyBuilder.latestResetTopicsPattern().matcher(topicTwo).matches());
        Assert.assertFalse(builder.internalTopologyBuilder.earliestResetTopicsPattern().matcher(topicTwo).matches());
    }

    @Test
    public void shouldHaveNullTimestampExtractorWhenNoneSupplied() {
        builder.stream(Collections.singleton("topic"), consumed);
        builder.buildAndOptimizeTopology();
        builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID)));
        final ProcessorTopology processorTopology = builder.internalTopologyBuilder.build(null);
        Assert.assertNull(processorTopology.source("topic").getTimestampExtractor());
    }

    @Test
    public void shouldUseProvidedTimestampExtractor() {
        final ConsumedInternal consumed = new ConsumedInternal(Consumed.with(new MockTimestampExtractor()));
        builder.stream(Collections.singleton("topic"), consumed);
        builder.buildAndOptimizeTopology();
        final ProcessorTopology processorTopology = builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID))).build(null);
        MatcherAssert.assertThat(processorTopology.source("topic").getTimestampExtractor(), IsInstanceOf.instanceOf(MockTimestampExtractor.class));
    }

    @Test
    public void ktableShouldHaveNullTimestampExtractorWhenNoneSupplied() {
        builder.table("topic", consumed, materialized);
        builder.buildAndOptimizeTopology();
        final ProcessorTopology processorTopology = builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID))).build(null);
        Assert.assertNull(processorTopology.source("topic").getTimestampExtractor());
    }

    @Test
    public void ktableShouldUseProvidedTimestampExtractor() {
        final ConsumedInternal<String, String> consumed = new ConsumedInternal(Consumed.<String, String>with(new MockTimestampExtractor()));
        builder.table("topic", consumed, materialized);
        builder.buildAndOptimizeTopology();
        final ProcessorTopology processorTopology = builder.internalTopologyBuilder.rewriteTopology(new StreamsConfig(StreamsTestUtils.getStreamsConfig(InternalStreamsBuilderTest.APP_ID))).build(null);
        MatcherAssert.assertThat(processorTopology.source("topic").getTimestampExtractor(), IsInstanceOf.instanceOf(MockTimestampExtractor.class));
    }
}

