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
package io.confluent.ksql.planner.plan;


import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_OFF;
import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.TOPOLOGY_OPTIMIZATION;
import TopologyDescription.Processor;
import TopologyDescription.Sink;
import TopologyDescription.Source;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.function.MutableFunctionRegistry;
import io.confluent.ksql.function.UdfLoaderUtil;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.structured.SchemaKTable;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.LimitedProxyBuilder;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.kstream.Aggregator;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Predicate;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.kstream.ValueMapperWithKey;
import org.apache.kafka.streams.kstream.ValueTransformerSupplier;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class AggregateNodeTest {
    private static final MutableFunctionRegistry functionRegistry = new InternalFunctionRegistry();

    static {
        UdfLoaderUtil.load(AggregateNodeTest.functionRegistry);
    }

    @Mock
    private ServiceContext serviceContext;

    private final KsqlConfig ksqlConfig = new KsqlConfig(new HashMap());

    private StreamsBuilder builder = new StreamsBuilder();

    private final ProcessingLogContext processingLogContext = ProcessingLogContext.create();

    private final QueryId queryId = new QueryId("queryid");

    @Test
    public void shouldBuildSourceNode() {
        // When:
        buildQuery(("SELECT col0, sum(col3), count(col3) FROM test1 " + ("window TUMBLING (size 2 second) " + "WHERE col0 > 100 GROUP BY col0;")));
        // Then:
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), PlanTestUtil.SOURCE_NODE)));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList(PlanTestUtil.MAPVALUES_NODE)));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.equalTo(ImmutableSet.of("test1")));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseConsistentOrderInPreAggSelectMapper() {
        // Given:
        final AggregateNodeTest.StreamBuilderMocker mocker = new AggregateNodeTest.StreamBuilderMocker();
        builder = mocker.createMockStreamBuilder();
        // When:
        buildQuery(("SELECT col0, col1, col2, sum(col3), count(col3) FROM test1 " + "GROUP BY col0,col1,col2;"));
        // Then:
        final List<ValueMapper> valueMappers = mocker.collectValueMappers();
        MatcherAssert.assertThat("invalid test", valueMappers, Matchers.hasSize(Matchers.greaterThanOrEqualTo(1)));
        final ValueMapper preAggSelectMapper = valueMappers.get(0);
        final GenericRow result = ((GenericRow) (preAggSelectMapper.apply(new GenericRow("rowtime", "rowkey", "0", "1", "2", "3"))));
        MatcherAssert.assertThat("should select col0, col1, col2, col3", result.getColumns(), Matchers.contains(0L, "1", "2", 3.0));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseConsistentOrderInPostAggSelectMapper() {
        // Given:
        final AggregateNodeTest.StreamBuilderMocker mocker = new AggregateNodeTest.StreamBuilderMocker();
        builder = mocker.createMockStreamBuilder();
        // When:
        buildQuery("SELECT col0, sum(col3), count(col3), max(col3) FROM test1 GROUP BY col0;");
        // Then:
        final List<ValueMapper> valueMappers = mocker.collectValueMappers();
        MatcherAssert.assertThat("invalid test", valueMappers, Matchers.hasSize(Matchers.greaterThanOrEqualTo(2)));
        final ValueMapper postAggSelect = valueMappers.get(1);
        final GenericRow result = ((GenericRow) (postAggSelect.apply(new GenericRow("0", "-1", "2", "3", "4"))));
        MatcherAssert.assertThat("should select col0, agg1, agg2", result.getColumns(), Matchers.contains(0L, 2.0, 3L, 4.0));
    }

    @Test
    public void shouldHaveOneSubTopologyIfGroupByKey() {
        // When:
        buildQuery(("SELECT col0, sum(col3), count(col3) FROM test1 " + ("window TUMBLING (size 2 second) " + "WHERE col0 > 100 GROUP BY col0;")));
        // Then:
        MatcherAssert.assertThat(builder.build().describe().subtopologies(), Matchers.hasSize(1));
    }

    @Test
    public void shouldHaveTwoSubTopologies() {
        // When:
        buildQuery(("SELECT col1, sum(col3), count(col3) FROM test1 " + ("window TUMBLING (size 2 second) " + "GROUP BY col1;")));
        // Then:
        MatcherAssert.assertThat(builder.build().describe().subtopologies(), Matchers.hasSize(2));
    }

    @Test
    public void shouldHaveSourceNodeForSecondSubtopolgyWithLegacyNameForRepartition() {
        // When:
        buildRequireRekey(ksqlConfig.overrideBreakingConfigsWithOriginalValues(ImmutableMap.of(KSQL_USE_NAMED_INTERNAL_TOPICS, String.valueOf(KSQL_USE_NAMED_INTERNAL_TOPICS_OFF))));
        // Then:
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-SOURCE-0000000010")));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList("KSTREAM-AGGREGATE-0000000007")));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.hasItem(CoreMatchers.equalTo("KSTREAM-AGGREGATE-STATE-STORE-0000000006-repartition")));
    }

    @Test
    public void shouldHaveSourceNodeForSecondSubtopolgyWithKsqlNameForRepartition() {
        // When:
        buildRequireRekey();
        // Then:
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-SOURCE-0000000009")));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList("KSTREAM-AGGREGATE-0000000006")));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.hasItem(CoreMatchers.equalTo("Aggregate-groupby-repartition")));
    }

    @Test
    public void shouldHaveSourceNodeForSecondSubtopolgyWithDefaultNameForRepartition() {
        buildRequireRekey(new KsqlConfig(ImmutableMap.of(TOPOLOGY_OPTIMIZATION, NO_OPTIMIZATION, KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_OFF)));
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-SOURCE-0000000010")));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList("KSTREAM-AGGREGATE-0000000007")));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.hasItem(CoreMatchers.containsString("KSTREAM-AGGREGATE-STATE-STORE-0000000006")));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.hasItem(CoreMatchers.containsString("-repartition")));
    }

    @Test
    public void shouldHaveDefaultNameForAggregationStateStoreIfInternalTopicNamingOff() {
        build(new KsqlConfig(ImmutableMap.of(TOPOLOGY_OPTIMIZATION, NO_OPTIMIZATION, KSQL_USE_NAMED_INTERNAL_TOPICS, KSQL_USE_NAMED_INTERNAL_TOPICS_OFF)));
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-AGGREGATE-0000000006")));
        MatcherAssert.assertThat(node.stores(), CoreMatchers.hasItem(CoreMatchers.equalTo("KSTREAM-AGGREGATE-STATE-STORE-0000000005")));
    }

    @Test
    public void shouldHaveKsqlNameForAggregationStateStore() {
        build();
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-AGGREGATE-0000000005")));
        MatcherAssert.assertThat(node.stores(), CoreMatchers.hasItem(CoreMatchers.equalTo("Aggregate-aggregate")));
    }

    @Test
    public void shouldHaveSinkNodeWithSameTopicAsSecondSource() {
        // When:
        buildQuery(("SELECT col1, sum(col3), count(col3) FROM test1 " + ("window TUMBLING (size 2 second) " + "GROUP BY col1;")));
        // Then:
        final TopologyDescription.Sink sink = ((TopologyDescription.Sink) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-SINK-0000000007")));
        final TopologyDescription.Source source = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), "KSTREAM-SOURCE-0000000009")));
        MatcherAssert.assertThat(sink.successors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(source.topicSet(), CoreMatchers.hasItem(sink.topic()));
    }

    @Test
    public void shouldBuildCorrectAggregateSchema() {
        // When:
        final SchemaKStream stream = buildQuery(("SELECT col0, sum(col3), count(col3) FROM test1 " + ("window TUMBLING (size 2 second) " + "WHERE col0 > 100 GROUP BY col0;")));
        // Then:
        MatcherAssert.assertThat(stream.getSchema().fields(), Matchers.contains(new org.apache.kafka.connect.data.Field("COL0", 0, Schema.OPTIONAL_INT64_SCHEMA), new org.apache.kafka.connect.data.Field("KSQL_COL_1", 1, Schema.OPTIONAL_FLOAT64_SCHEMA), new org.apache.kafka.connect.data.Field("KSQL_COL_2", 2, Schema.OPTIONAL_INT64_SCHEMA)));
    }

    @Test
    public void shouldBeSchemaKTableResult() {
        final SchemaKStream stream = build();
        MatcherAssert.assertThat(stream.getClass(), CoreMatchers.equalTo(SchemaKTable.class));
    }

    @Test
    public void shouldBeWindowedWhenStatementSpecifiesWindowing() {
        final SchemaKStream stream = build();
        MatcherAssert.assertThat(stream.getKeySerde(), Matchers.is(Matchers.not(Optional.empty())));
    }

    @Test
    public void shouldCreateLoggerForRepartition() {
        shouldCreateLogger("groupby");
    }

    @Test
    public void shouldCreateLoggerForStatestore() {
        shouldCreateLogger("aggregate");
    }

    @Test
    public void shouldGroupByFunction() {
        // Given:
        final SchemaKStream stream = buildQuery(("SELECT UCASE(col1), sum(col3), count(col3) FROM test1 " + "GROUP BY UCASE(col1);"));
        // Then:
        MatcherAssert.assertThat(stream.getKeyField().name(), Matchers.is("UCASE(KSQL_INTERNAL_COL_0)"));
    }

    @Test
    public void shouldGroupByArithmetic() {
        // Given:
        final SchemaKStream stream = buildQuery(("SELECT col0 + 10, sum(col3), count(col3) FROM test1 " + "GROUP BY col0 + 10;"));
        // Then:
        MatcherAssert.assertThat(stream.getKeyField().name(), Matchers.is("(KSQL_INTERNAL_COL_0 + 10)"));
    }

    private static final class StreamBuilderMocker {
        private final Map<String, AggregateNodeTest.StreamBuilderMocker.FakeKStream> sources = new HashMap<>();

        private StreamsBuilder createMockStreamBuilder() {
            final StreamsBuilder builder = Mockito.mock(StreamsBuilder.class);
            Mockito.when(builder.stream(ArgumentMatchers.anyString(), ArgumentMatchers.any())).thenAnswer(( inv) -> {
                final io.confluent.ksql.planner.plan.FakeKStream stream = new io.confluent.ksql.planner.plan.FakeKStream();
                sources.put(inv.getArgument(0), stream);
                return stream.createProxy();
            });
            return builder;
        }

        List<ValueMapper> collectValueMappers() {
            return sources.values().stream().flatMap(( stream) -> Streams.concat(Stream.of(stream), stream.stream())).flatMap(( stream) -> Streams.concat(stream.mapValues.keySet().stream(), stream.groupStreams().flatMap(io.confluent.ksql.planner.plan.FakeKGroupedStream::tables).flatMap(( t) -> t.mapValues.keySet().stream()))).collect(Collectors.toList());
        }

        private static final class FakeKStream {
            private final Map<ValueMapper, AggregateNodeTest.StreamBuilderMocker.FakeKStream> mapValues = new IdentityHashMap<>();

            private final Map<ValueMapperWithKey, AggregateNodeTest.StreamBuilderMocker.FakeKStream> mapValuesWithKey = new IdentityHashMap<>();

            private final Map<ValueTransformerSupplier, AggregateNodeTest.StreamBuilderMocker.FakeKStream> transformValues = new IdentityHashMap<>();

            private final Map<Predicate, AggregateNodeTest.StreamBuilderMocker.FakeKStream> filter = new IdentityHashMap<>();

            private final Map<Grouped, AggregateNodeTest.StreamBuilderMocker.FakeKGroupedStream> groupByKey = new IdentityHashMap<>();

            KStream createProxy() {
                return LimitedProxyBuilder.forClass(KStream.class).forward("mapValues", LimitedProxyBuilder.methodParams(ValueMapper.class), this).forward("mapValues", LimitedProxyBuilder.methodParams(ValueMapperWithKey.class), this).forward("transformValues", LimitedProxyBuilder.methodParams(ValueTransformerSupplier.class, String[].class), this).forward("filter", LimitedProxyBuilder.methodParams(Predicate.class), this).forward("groupByKey", LimitedProxyBuilder.methodParams(Grouped.class), this).forward("groupBy", LimitedProxyBuilder.methodParams(KeyValueMapper.class, Grouped.class), this).build();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KStream mapValues(final ValueMapper mapper) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKStream stream = new AggregateNodeTest.StreamBuilderMocker.FakeKStream();
                mapValues.put(mapper, stream);
                return stream.createProxy();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KStream mapValues(final ValueMapperWithKey mapper) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKStream stream = new AggregateNodeTest.StreamBuilderMocker.FakeKStream();
                mapValuesWithKey.put(mapper, stream);
                return stream.createProxy();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KStream transformValues(final ValueTransformerSupplier valueTransformerSupplier, final String... stateStoreNames) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKStream stream = new AggregateNodeTest.StreamBuilderMocker.FakeKStream();
                transformValues.put(valueTransformerSupplier, stream);
                return stream.createProxy();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KStream filter(final Predicate predicate) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKStream stream = new AggregateNodeTest.StreamBuilderMocker.FakeKStream();
                filter.put(predicate, stream);
                return stream.createProxy();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KGroupedStream groupByKey(final Grouped grouped) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKGroupedStream stream = new AggregateNodeTest.StreamBuilderMocker.FakeKGroupedStream();
                groupByKey.put(grouped, stream);
                return stream.createProxy();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KGroupedStream groupBy(final KeyValueMapper selector, final Grouped grouped) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKGroupedStream stream = new AggregateNodeTest.StreamBuilderMocker.FakeKGroupedStream();
                groupByKey.put(grouped, stream);
                return stream.createProxy();
            }

            Stream<AggregateNodeTest.StreamBuilderMocker.FakeKStream> stream() {
                final Stream<AggregateNodeTest.StreamBuilderMocker.FakeKStream> children = Streams.concat(mapValues.values().stream(), mapValuesWithKey.values().stream(), filter.values().stream(), transformValues.values().stream());
                final Stream<AggregateNodeTest.StreamBuilderMocker.FakeKStream> grandChildren = Streams.concat(mapValues.values().stream(), mapValuesWithKey.values().stream(), filter.values().stream(), transformValues.values().stream()).flatMap(AggregateNodeTest.StreamBuilderMocker.FakeKStream::stream);
                return Streams.concat(children, grandChildren);
            }

            Stream<AggregateNodeTest.StreamBuilderMocker.FakeKGroupedStream> groupStreams() {
                return groupByKey.values().stream();
            }
        }

        private static final class FakeKGroupedStream {
            private final Map<Aggregator, AggregateNodeTest.StreamBuilderMocker.FakeKTable> aggregate = new IdentityHashMap<>();

            KGroupedStream createProxy() {
                return LimitedProxyBuilder.forClass(KGroupedStream.class).forward("aggregate", LimitedProxyBuilder.methodParams(Initializer.class, Aggregator.class, Materialized.class), this).build();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KTable aggregate(final Initializer initializer, final Aggregator aggregator, final Materialized materialized) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKTable table = new AggregateNodeTest.StreamBuilderMocker.FakeKTable();
                aggregate.put(aggregator, table);
                return table.createProxy();
            }

            Stream<AggregateNodeTest.StreamBuilderMocker.FakeKTable> tables() {
                return aggregate.values().stream();
            }
        }

        private static final class FakeKTable {
            private final Map<ValueMapper, AggregateNodeTest.StreamBuilderMocker.FakeKTable> mapValues = new IdentityHashMap<>();

            KTable createProxy() {
                return LimitedProxyBuilder.forClass(KTable.class).forward("mapValues", LimitedProxyBuilder.methodParams(ValueMapper.class), this).build();
            }

            // Invoked via reflection.
            @SuppressWarnings("unused")
            private KTable mapValues(final ValueMapper mapper) {
                final AggregateNodeTest.StreamBuilderMocker.FakeKTable table = new AggregateNodeTest.StreamBuilderMocker.FakeKTable();
                mapValues.put(mapper, table);
                return table.createProxy();
            }
        }
    }
}

