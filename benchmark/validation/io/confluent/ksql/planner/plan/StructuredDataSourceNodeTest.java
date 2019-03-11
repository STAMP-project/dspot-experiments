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


import ProcessingLogConstants.PREFIX;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import TopologyDescription.Node;
import TopologyDescription.Processor;
import TopologyDescription.Source;
import com.google.common.collect.ImmutableSet;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.logging.processing.ProcessingLoggerUtil;
import io.confluent.ksql.metastore.KsqlTable;
import io.confluent.ksql.metastore.KsqlTopic;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.serde.KsqlTopicSerDe;
import io.confluent.ksql.serde.json.KsqlJsonTopicSerDe;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.streams.MaterializedFactory;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.structured.SchemaKTable;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.QueryLoggerUtil;
import io.confluent.ksql.util.timestamp.LongColumnTimestampExtractionPolicy;
import io.confluent.ksql.util.timestamp.TimestampExtractionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class StructuredDataSourceNodeTest {
    private static final String TIMESTAMP_FIELD = "timestamp";

    private final KsqlConfig realConfig = new KsqlConfig(Collections.emptyMap());

    private SchemaKStream realStream;

    private StreamsBuilder realBuilder;

    private final Schema realSchema = SchemaBuilder.struct().field("field1", OPTIONAL_STRING_SCHEMA).field("field2", OPTIONAL_STRING_SCHEMA).field("field3", OPTIONAL_STRING_SCHEMA).field(StructuredDataSourceNodeTest.TIMESTAMP_FIELD, OPTIONAL_INT64_SCHEMA).field("key", OPTIONAL_STRING_SCHEMA).build();

    private final StructuredDataSourceNode node = new StructuredDataSourceNode(new PlanNodeId("0"), new io.confluent.ksql.metastore.KsqlStream("sqlExpression", "datasource", realSchema, realSchema.field("key"), new LongColumnTimestampExtractionPolicy("timestamp"), new KsqlTopic("topic", "topic", new KsqlJsonTopicSerDe(), false), Serdes.String()), realSchema);

    private final QueryId queryId = new QueryId("source-test");

    private final PlanNodeId realNodeId = new PlanNodeId("source");

    @Mock
    private KsqlTable tableSource;

    @Mock
    private TimestampExtractionPolicy timestampExtractionPolicy;

    @Mock
    private TimestampExtractor timestampExtractor;

    @Mock
    private KsqlTopic ksqlTopic;

    @Mock
    private KsqlTopicSerDe topicSerDe;

    @Mock
    private Serde<GenericRow> rowSerde;

    @Mock
    private Serde<String> keySerde;

    @Mock
    private StreamsBuilder streamsBuilder;

    @Mock
    private KStream kStream;

    @Mock
    private KGroupedStream kGroupedStream;

    @Mock
    private KTable kTable;

    @Mock
    private InternalFunctionRegistry functionRegistry;

    @Mock
    private Function<KsqlConfig, MaterializedFactory> materializedFactorySupplier;

    @Mock
    private MaterializedFactory materializedFactory;

    @Mock
    private Materialized materialized;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    private ServiceContext serviceContext;

    private ProcessingLogContext processingLogContext;

    @Test
    @SuppressWarnings("unchecked")
    public void shouldMaterializeTableCorrectly() {
        // Given:
        final StructuredDataSourceNode node = nodeWithMockTableSource();
        // When:
        node.buildStream(streamsBuilder, realConfig, serviceContext, processingLogContext, functionRegistry, queryId);
        // Then:
        Mockito.verify(materializedFactorySupplier).apply(realConfig);
        Mockito.verify(materializedFactory).create(keySerde, rowSerde, "source-reduce");
        Mockito.verify(kGroupedStream).aggregate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.same(materialized));
    }

    @Test
    public void shouldCreateLoggerForSourceSerde() {
        Assert.assertThat(processingLogContext.getLoggerFactory().getLoggers(), Matchers.hasItem(Matchers.startsWith(ProcessingLoggerUtil.join(PREFIX, QueryLoggerUtil.queryLoggerName(new io.confluent.ksql.structured.QueryContext.Stacker(queryId).push(node.getId().toString(), "source").getQueryContext())))));
    }

    @Test
    public void shouldBuildSourceNode() {
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(realBuilder.build(), PlanTestUtil.SOURCE_NODE)));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        Assert.assertThat(node.predecessors(), Matchers.equalTo(Collections.emptySet()));
        Assert.assertThat(successors, Matchers.equalTo(Collections.singletonList(PlanTestUtil.MAPVALUES_NODE)));
        Assert.assertThat(node.topicSet(), Matchers.equalTo(ImmutableSet.of("topic")));
    }

    @Test
    public void shouldBuildMapNode() {
        PlanTestUtil.verifyProcessorNode(((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(realBuilder.build(), PlanTestUtil.MAPVALUES_NODE))), Collections.singletonList(PlanTestUtil.SOURCE_NODE), Collections.singletonList(PlanTestUtil.TRANSFORM_NODE));
    }

    @Test
    public void shouldBuildTransformNode() {
        final TopologyDescription.Processor node = ((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(realBuilder.build(), PlanTestUtil.TRANSFORM_NODE)));
        PlanTestUtil.verifyProcessorNode(node, Collections.singletonList(PlanTestUtil.MAPVALUES_NODE), Collections.emptyList());
    }

    @Test
    public void shouldHaveNoOutputNode() {
        Assert.assertThat(realStream.outputNode(), Matchers.nullValue());
    }

    @Test
    public void shouldBeOfTypeSchemaKStreamWhenDataSourceIsKsqlStream() {
        Assert.assertThat(realStream.getClass(), Matchers.equalTo(SchemaKStream.class));
    }

    @Test
    public void shouldExtracKeyField() {
        Assert.assertThat(realStream.getKeyField(), Matchers.equalTo(new org.apache.kafka.connect.data.Field("key", 4, Schema.OPTIONAL_STRING_SCHEMA)));
    }

    @Test
    public void shouldBuildSchemaKTableWhenKTableSource() {
        final StructuredDataSourceNode node = new StructuredDataSourceNode(new PlanNodeId("0"), new KsqlTable("sqlExpression", "datasource", realSchema, realSchema.field("field"), new LongColumnTimestampExtractionPolicy("timestamp"), new KsqlTopic("topic2", "topic2", new KsqlJsonTopicSerDe(), false), "statestore", Serdes.String()), realSchema);
        final SchemaKStream result = build(node);
        Assert.assertThat(result.getClass(), Matchers.equalTo(SchemaKTable.class));
    }

    @Test
    public void shouldTransformKStreamToKTableCorrectly() {
        final StructuredDataSourceNode node = new StructuredDataSourceNode(new PlanNodeId("0"), new KsqlTable("sqlExpression", "datasource", realSchema, realSchema.field("field"), new LongColumnTimestampExtractionPolicy("timestamp"), new KsqlTopic("topic2", "topic2", new KsqlJsonTopicSerDe(), false), "statestore", Serdes.String()), realSchema);
        realBuilder = new StreamsBuilder();
        build(node);
        final Topology topology = realBuilder.build();
        final TopologyDescription description = topology.describe();
        final List<String> expectedPlan = Arrays.asList("SOURCE", "MAPVALUES", "TRANSFORMVALUES", "MAPVALUES", "AGGREGATE");
        Assert.assertThat(description.subtopologies().size(), Matchers.equalTo(1));
        final Set<TopologyDescription.Node> nodes = description.subtopologies().iterator().next().nodes();
        // Get the source node
        TopologyDescription.Node streamsNode = nodes.iterator().next();
        while (!(streamsNode.predecessors().isEmpty())) {
            streamsNode = streamsNode.predecessors().iterator().next();
        } 
        // Walk the plan and make sure it matches
        final ListIterator<String> expectedPlanIt = expectedPlan.listIterator();
        Assert.assertThat(nodes.size(), Matchers.equalTo(expectedPlan.size()));
        while (true) {
            Assert.assertThat(streamsNode.name(), Matchers.startsWith(("KSTREAM-" + (expectedPlanIt.next()))));
            if (streamsNode.successors().isEmpty()) {
                Assert.assertThat(expectedPlanIt.hasNext(), Matchers.is(false));
                break;
            }
            Assert.assertThat(expectedPlanIt.hasNext(), Matchers.is(true));
            Assert.assertThat(streamsNode.successors().size(), Matchers.equalTo(1));
            streamsNode = streamsNode.successors().iterator().next();
        } 
    }
}

