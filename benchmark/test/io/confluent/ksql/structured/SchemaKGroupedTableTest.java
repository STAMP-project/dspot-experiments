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


import QueryContext.Stacker;
import Schema.OPTIONAL_INT32_SCHEMA;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.function.KsqlAggregateFunction;
import io.confluent.ksql.function.udaf.KudafInitializer;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.metastore.KsqlTable;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.parser.tree.TumblingWindowExpression;
import io.confluent.ksql.parser.tree.WindowExpression;
import io.confluent.ksql.serde.json.KsqlJsonTopicSerDe;
import io.confluent.ksql.streams.MaterializedFactory;
import io.confluent.ksql.streams.StreamsUtil;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.MetaStoreFixture;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.streams.kstream.KGroupedTable;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("unchecked")
public class SchemaKGroupedTableTest {
    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    private final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();

    private final ProcessingLogContext processingLogContext = ProcessingLogContext.create();

    private final KGroupedTable mockKGroupedTable = mock(KGroupedTable.class);

    private final Schema schema = SchemaBuilder.struct().field("GROUPING_COLUMN", OPTIONAL_STRING_SCHEMA).field("AGG_VALUE", OPTIONAL_INT32_SCHEMA).build();

    private final MaterializedFactory materializedFactory = mock(MaterializedFactory.class);

    private final MetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    private final Stacker queryContext = push("node");

    private KTable kTable;

    private KsqlTable ksqlTable;

    @Test
    public void shouldFailWindowedTableAggregation() {
        final SchemaKGroupedTable kGroupedTable = buildSchemaKGroupedTableFromQuery("SELECT col0, col1, col2 FROM test1;", "COL1", "COL2");
        final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();
        final WindowExpression windowExpression = new WindowExpression("window", new TumblingWindowExpression(30, TimeUnit.SECONDS));
        try {
            kGroupedTable.aggregate(new KudafInitializer(1), Collections.singletonMap(0, functionRegistry.getAggregate("SUM", OPTIONAL_INT64_SCHEMA)), Collections.singletonMap(0, 0), windowExpression, new KsqlJsonTopicSerDe().getGenericRowSerde(ksqlTable.getSchema(), ksqlConfig, false, () -> null, "test", processingLogContext), queryContext);
            Assert.fail("Should fail to build topology for aggregation with window");
        } catch (final KsqlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("Windowing not supported for table aggregations."));
        }
    }

    @Test
    public void shouldFailUnsupportedAggregateFunction() {
        final SchemaKGroupedTable kGroupedTable = buildSchemaKGroupedTableFromQuery("SELECT col0, col1, col2 FROM test1;", "COL1", "COL2");
        final InternalFunctionRegistry functionRegistry = new InternalFunctionRegistry();
        try {
            final Map<Integer, KsqlAggregateFunction> aggValToFunctionMap = new HashMap<>();
            aggValToFunctionMap.put(0, functionRegistry.getAggregate("MAX", OPTIONAL_INT64_SCHEMA));
            aggValToFunctionMap.put(1, functionRegistry.getAggregate("MIN", OPTIONAL_INT64_SCHEMA));
            kGroupedTable.aggregate(new KudafInitializer(1), aggValToFunctionMap, Collections.singletonMap(0, 0), null, new KsqlJsonTopicSerDe().getGenericRowSerde(ksqlTable.getSchema(), ksqlConfig, false, () -> null, "test", processingLogContext), queryContext);
            Assert.fail("Should fail to build topology for aggregation with unsupported function");
        } catch (final KsqlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("The aggregation function(s) (MAX, MIN) cannot be applied to a table."));
        }
    }

    @Test
    public void shouldUseMaterializedFactoryForStateStore() {
        // Given:
        final Serde<GenericRow> valueSerde = mock(Serde.class);
        final Materialized materialized = MaterializedFactory.create(ksqlConfig).create(Serdes.String(), valueSerde, StreamsUtil.buildOpName(queryContext.getQueryContext()));
        expect(materializedFactory.create(anyObject(Serdes.String().getClass()), same(valueSerde), eq(StreamsUtil.buildOpName(queryContext.getQueryContext())))).andReturn(materialized);
        final KTable mockKTable = mock(KTable.class);
        expect(mockKGroupedTable.aggregate(anyObject(), anyObject(), anyObject(), same(materialized))).andReturn(mockKTable);
        replay(materializedFactory, mockKGroupedTable);
        final SchemaKGroupedTable groupedTable = buildSchemaKGroupedTable(mockKGroupedTable, materializedFactory);
        // When:
        groupedTable.aggregate(() -> null, Collections.emptyMap(), Collections.emptyMap(), null, valueSerde, queryContext);
        // Then:
        verify(materializedFactory, mockKGroupedTable);
    }
}

