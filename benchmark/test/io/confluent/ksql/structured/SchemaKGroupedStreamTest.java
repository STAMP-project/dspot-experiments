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
package io.confluent.ksql.structured;


import KsqlConfig.KSQL_WINDOWED_SESSION_KEY_LEGACY_CONFIG;
import QueryContext.Stacker;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.FunctionRegistry;
import io.confluent.ksql.function.KsqlAggregateFunction;
import io.confluent.ksql.parser.tree.KsqlWindowExpression;
import io.confluent.ksql.parser.tree.WindowExpression;
import io.confluent.ksql.streams.MaterializedFactory;
import io.confluent.ksql.streams.StreamsUtil;
import io.confluent.ksql.util.KsqlConfig;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.kstream.Initializer;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


@SuppressWarnings("unchecked")
public class SchemaKGroupedStreamTest {
    @Mock
    private Schema schema;

    @Mock
    private KGroupedStream groupedStream;

    @Mock
    private Field keyField;

    @Mock
    private List<SchemaKStream> sourceStreams;

    @Mock
    private KsqlConfig config;

    @Mock
    private FunctionRegistry funcRegistry;

    @Mock
    private Initializer initializer;

    @Mock
    private Serde<GenericRow> topicValueSerDe;

    @Mock
    private KsqlAggregateFunction windowStartFunc;

    @Mock
    private KsqlAggregateFunction windowEndFunc;

    @Mock
    private KsqlAggregateFunction otherFunc;

    @Mock
    private KTable table;

    @Mock
    private KTable table2;

    @Mock
    private WindowExpression windowExp;

    @Mock
    private KsqlWindowExpression ksqlWindowExp;

    @Mock
    private Serde<Windowed<String>> windowedKeySerde;

    @Mock
    private MaterializedFactory materializedFactory;

    @Mock
    private Materialized materialized;

    private final Stacker queryContext = push("node");

    private SchemaKGroupedStream schemaGroupedStream;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldNoUseSelectMapperForNonWindowed() {
        // Given:
        final Map<Integer, KsqlAggregateFunction> invalidWindowFuncs = ImmutableMap.of(2, windowStartFunc, 4, windowEndFunc);
        // When:
        assertDoesNotInstallWindowSelectMapper(null, invalidWindowFuncs);
    }

    @Test
    public void shouldNotUseSelectMapperForWindowedWithoutWindowSelects() {
        // Given:
        final Map<Integer, KsqlAggregateFunction> nonWindowFuncs = ImmutableMap.of(2, otherFunc);
        // When:
        assertDoesNotInstallWindowSelectMapper(windowExp, nonWindowFuncs);
    }

    @Test
    public void shouldUseSelectMapperForWindowedWithWindowStart() {
        // Given:
        Map<Integer, KsqlAggregateFunction> funcMapWithWindowStart = ImmutableMap.of(0, otherFunc, 1, windowStartFunc);
        // Then:
        assertDoesInstallWindowSelectMapper(funcMapWithWindowStart);
    }

    @Test
    public void shouldUseSelectMapperForWindowedWithWindowEnd() {
        // Given:
        Map<Integer, KsqlAggregateFunction> funcMapWithWindowEnd = ImmutableMap.of(0, windowEndFunc, 1, otherFunc);
        // Then:
        assertDoesInstallWindowSelectMapper(funcMapWithWindowEnd);
    }

    @Test
    public void shouldUseStringKeySerdeForNoneWindowed() {
        // When:
        final SchemaKTable result = schemaGroupedStream.aggregate(initializer, Collections.emptyMap(), Collections.emptyMap(), null, topicValueSerDe, queryContext);
        // Then:
        MatcherAssert.assertThat(result.getKeySerde(), Matchers.instanceOf(Serdes.String().getClass()));
    }

    @Test
    public void shouldUseWindowExpressionKeySerde() {
        // When:
        final SchemaKTable result = schemaGroupedStream.aggregate(initializer, Collections.emptyMap(), Collections.emptyMap(), windowExp, topicValueSerDe, queryContext);
        // Then:
        MatcherAssert.assertThat(result.getKeySerde(), Matchers.is(Matchers.sameInstance(windowedKeySerde)));
    }

    @Test
    public void shouldUseTimeWindowKeySerdeForWindowedIfLegacyConfig() {
        // Given:
        Mockito.when(config.getBoolean(KSQL_WINDOWED_SESSION_KEY_LEGACY_CONFIG)).thenReturn(true);
        Mockito.when(config.getKsqlStreamConfigProps()).thenReturn(Collections.emptyMap());
        // When:
        final SchemaKTable result = schemaGroupedStream.aggregate(initializer, Collections.emptyMap(), Collections.emptyMap(), windowExp, topicValueSerDe, queryContext);
        // Then:
        MatcherAssert.assertThat(result.getKeySerde(), Matchers.is(Matchers.instanceOf(WindowedSerdes.timeWindowedSerdeFrom(String.class).getClass())));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseMaterializedFactoryForStateStore() {
        // Given:
        final Materialized materialized = whenMaterializedFactoryCreates();
        final KTable mockKTable = Mockito.mock(KTable.class);
        Mockito.when(groupedStream.aggregate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.same(materialized))).thenReturn(mockKTable);
        // When:
        schemaGroupedStream.aggregate(() -> null, Collections.emptyMap(), Collections.emptyMap(), null, topicValueSerDe, queryContext);
        // Then:
        Mockito.verify(materializedFactory).create(ArgumentMatchers.any(Serdes.String().getClass()), ArgumentMatchers.same(topicValueSerDe), ArgumentMatchers.eq(StreamsUtil.buildOpName(queryContext.getQueryContext())));
        Mockito.verify(groupedStream, Mockito.times(1)).aggregate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.same(materialized));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldUseMaterializedFactoryWindowedStateStore() {
        // Given:
        final Materialized materialized = whenMaterializedFactoryCreates();
        Mockito.when(ksqlWindowExp.getKeySerde(String.class)).thenReturn(windowedKeySerde);
        Mockito.when(ksqlWindowExp.applyAggregate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.same(materialized))).thenReturn(table);
        // When:
        schemaGroupedStream.aggregate(() -> null, Collections.emptyMap(), Collections.emptyMap(), windowExp, topicValueSerDe, queryContext);
        // Then:
        Mockito.verify(materializedFactory).create(ArgumentMatchers.any(Serdes.String().getClass()), ArgumentMatchers.same(topicValueSerDe), ArgumentMatchers.eq(StreamsUtil.buildOpName(queryContext.getQueryContext())));
        Mockito.verify(ksqlWindowExp, Mockito.times(1)).applyAggregate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.same(materialized));
    }
}

