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


import DdlConfig.PARTITION_BY_PROPERTY;
import KsqlConfig.SINK_NUMBER_OF_PARTITIONS_PROPERTY;
import KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY;
import KsqlConstants.legacyDefaultSinkPartitionCount;
import KsqlConstants.legacyDefaultSinkReplicaCount;
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import TopicConfig.CLEANUP_POLICY_COMPACT;
import TopicConfig.CLEANUP_POLICY_CONFIG;
import TopologyDescription.Sink;
import TopologyDescription.Source;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.confluent.ksql.metastore.KsqlStream;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.serde.KsqlTopicSerDe;
import io.confluent.ksql.serde.json.KsqlJsonTopicSerDe;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.structured.SchemaKTable;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.QueryIdGenerator;
import io.confluent.ksql.util.QueryLoggerUtil;
import io.confluent.ksql.util.timestamp.LongColumnTimestampExtractionPolicy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TopologyDescription;
import org.apache.kafka.streams.kstream.WindowedSerdes;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class KsqlStructuredDataOutputNodeTest {
    private static final String MAPVALUES_OUTPUT_NODE = "KSTREAM-MAPVALUES-0000000003";

    private static final String OUTPUT_NODE = "KSTREAM-SINK-0000000004";

    private static final String QUERY_ID_STRING = "output-test";

    private static final QueryId QUERY_ID = new QueryId(KsqlStructuredDataOutputNodeTest.QUERY_ID_STRING);

    private static final String SOURCE_TOPIC_NAME = "input";

    private static final String SOURCE_KAFKA_TOPIC_NAME = "input_kafka";

    private static final String SINK_TOPIC_NAME = "output";

    private static final String SINK_KAFKA_TOPIC_NAME = "output_kafka";

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private final Schema schema = SchemaBuilder.struct().field("field1", OPTIONAL_STRING_SCHEMA).field("field2", OPTIONAL_STRING_SCHEMA).field("field3", OPTIONAL_STRING_SCHEMA).field("timestamp", OPTIONAL_INT64_SCHEMA).field("key", OPTIONAL_STRING_SCHEMA).build();

    private final KsqlStream dataSource = new KsqlStream("sqlExpression", "datasource", schema, schema.field("key"), new LongColumnTimestampExtractionPolicy("timestamp"), new io.confluent.ksql.metastore.KsqlTopic(KsqlStructuredDataOutputNodeTest.SOURCE_TOPIC_NAME, KsqlStructuredDataOutputNodeTest.SOURCE_KAFKA_TOPIC_NAME, new KsqlJsonTopicSerDe(), false), Serdes.String());

    private final StructuredDataSourceNode sourceNode = new StructuredDataSourceNode(new PlanNodeId("0"), dataSource, schema);

    private StreamsBuilder builder = new StreamsBuilder();

    private KsqlStructuredDataOutputNode outputNode;

    private SchemaKStream stream;

    private ServiceContext serviceContext;

    @Mock
    private KsqlConfig ksqlConfig;

    @Mock
    private KafkaTopicClient mockTopicClient;

    @Mock
    private QueryIdGenerator queryIdGenerator;

    @Mock
    private TopicDescription topicDescription;

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void shouldBuildSourceNode() {
        // Then:
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), PlanTestUtil.SOURCE_NODE)));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList(PlanTestUtil.MAPVALUES_NODE)));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.equalTo(ImmutableSet.of(KsqlStructuredDataOutputNodeTest.SOURCE_KAFKA_TOPIC_NAME)));
    }

    @Test
    public void shouldBuildMapNodePriorToOutput() {
        // Then:
        PlanTestUtil.verifyProcessorNode(((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(builder.build(), KsqlStructuredDataOutputNodeTest.MAPVALUES_OUTPUT_NODE))), Collections.singletonList(PlanTestUtil.TRANSFORM_NODE), Collections.singletonList(KsqlStructuredDataOutputNodeTest.OUTPUT_NODE));
    }

    @Test
    public void shouldBuildOutputNode() {
        // Then:
        final TopologyDescription.Sink sink = ((TopologyDescription.Sink) (PlanTestUtil.getNodeByName(builder.build(), KsqlStructuredDataOutputNodeTest.OUTPUT_NODE)));
        final List<String> predecessors = sink.predecessors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(sink.successors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(predecessors, CoreMatchers.equalTo(Collections.singletonList(KsqlStructuredDataOutputNodeTest.MAPVALUES_OUTPUT_NODE)));
        MatcherAssert.assertThat(sink.topic(), CoreMatchers.equalTo(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME));
    }

    @Test
    public void shouldSetOutputNodeOnStream() {
        // Then:
        MatcherAssert.assertThat(stream.outputNode(), IsInstanceOf.instanceOf(KsqlStructuredDataOutputNode.class));
    }

    @Test
    public void shouldHaveCorrectOutputNodeSchema() {
        // Then:
        final List<Field> expected = Arrays.asList(new Field("ROWTIME", 0, Schema.OPTIONAL_INT64_SCHEMA), new Field("ROWKEY", 1, Schema.OPTIONAL_STRING_SCHEMA), new Field("field1", 2, Schema.OPTIONAL_STRING_SCHEMA), new Field("field2", 3, Schema.OPTIONAL_STRING_SCHEMA), new Field("field3", 4, Schema.OPTIONAL_STRING_SCHEMA), new Field("timestamp", 5, Schema.OPTIONAL_INT64_SCHEMA), new Field("key", 6, Schema.OPTIONAL_STRING_SCHEMA));
        final List<Field> fields = stream.outputNode().getSchema().fields();
        MatcherAssert.assertThat(fields, CoreMatchers.equalTo(expected));
    }

    @Test
    public void shouldPartitionByFieldNameInPartitionByProperty() {
        // Given:
        createOutputNode(Collections.singletonMap(PARTITION_BY_PROPERTY, "field2"), true);
        // When:
        stream = buildStream();
        // Then:
        final Field keyField = stream.getKeyField();
        MatcherAssert.assertThat(keyField, CoreMatchers.equalTo(new Field("field2", 1, Schema.OPTIONAL_STRING_SCHEMA)));
        MatcherAssert.assertThat(stream.getSchema().fields(), CoreMatchers.equalTo(schema.fields()));
    }

    @Test
    public void shouldCreateSinkTopic() {
        // Then:
        Mockito.verify(mockTopicClient, Mockito.times(1)).createTopic(ArgumentMatchers.eq(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME), ArgumentMatchers.eq(4), ArgumentMatchers.eq(((short) (3))), ArgumentMatchers.eq(Collections.emptyMap()));
    }

    @Test
    public void shouldCreateSinkWithCorrectCleanupPolicyNonWindowedTable() {
        // Given:
        outputNode = getKsqlStructuredDataOutputNodeForTable(Serdes.String());
        // When:
        stream = buildStream();
        // Then:
        MatcherAssert.assertThat(stream, IsInstanceOf.instanceOf(SchemaKTable.class));
        final Map<String, String> topicConfig = ImmutableMap.of(CLEANUP_POLICY_CONFIG, CLEANUP_POLICY_COMPACT);
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 4, ((short) (3)), topicConfig);
    }

    @Test
    public void shouldCreateSinkWithCorrectCleanupPolicyWindowedTable() {
        // Given:
        Mockito.reset(mockTopicClient);
        outputNode = getKsqlStructuredDataOutputNodeForTable(WindowedSerdes.timeWindowedSerdeFrom(String.class));
        // When:
        stream = buildStream();
        // Then:
        MatcherAssert.assertThat(stream, IsInstanceOf.instanceOf(SchemaKTable.class));
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 4, ((short) (3)), Collections.emptyMap());
    }

    @Test
    public void shouldCreateSinkWithCorrectCleanupPolicyStream() {
        // Then:
        MatcherAssert.assertThat(stream, IsInstanceOf.instanceOf(SchemaKStream.class));
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 4, ((short) (3)), Collections.emptyMap());
    }

    @Test
    public void shouldCreateSinkWithTheSourcePartitionReplication() {
        // Given:
        createOutputNode(Collections.emptyMap(), true);
        // When:
        stream = buildStream();
        // Then:
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 1, ((short) (2)), Collections.emptyMap());
    }

    @Test
    public void shouldNotFetchSourceTopicPropsIfProvided() {
        // Given:
        createOutputNode(ImmutableMap.of(SINK_NUMBER_OF_PARTITIONS_PROPERTY, 5, SINK_NUMBER_OF_REPLICAS_PROPERTY, ((short) (3))), true);
        // When:
        stream = buildStream();
        // Then:
        Mockito.verify(mockTopicClient, Mockito.never()).describeTopics(ArgumentMatchers.any());
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 5, ((short) (3)), Collections.emptyMap());
    }

    @Test
    public void shouldCreateSinkWithTheSourceReplicationAndProvidedPartition() {
        // Given:
        createOutputNode(Collections.singletonMap(SINK_NUMBER_OF_PARTITIONS_PROPERTY, 5), true);
        // When:
        stream = buildStream();
        // Then:
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 5, ((short) (2)), Collections.emptyMap());
    }

    @Test
    public void shouldCreateSinkWithTheSourcePartitionAndProvidedReplication() {
        // Given:
        createOutputNode(Collections.singletonMap(SINK_NUMBER_OF_REPLICAS_PROPERTY, ((short) (2))), true);
        // When:
        stream = buildStream();
        // Then:
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 1, ((short) (2)), Collections.emptyMap());
    }

    @Test
    public void shouldThrowIfSinkTopicHasDifferentPropertiesThanRequested() {
        // Given:
        Mockito.doThrow(KsqlException.class).when(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 1, ((short) (2)), Collections.emptyMap());
        createOutputNode(Collections.singletonMap(SINK_NUMBER_OF_REPLICAS_PROPERTY, ((short) (2))), true);
        // Then:
        expectedException.expect(KsqlException.class);
        // When:
        buildStream();
    }

    @Test
    public void shouldUseLegacySinkPartitionCountIfLegacyIsTrue() {
        // Given:
        Mockito.<Object>when(ksqlConfig.values()).thenReturn(ImmutableMap.<String, Object>of(SINK_NUMBER_OF_PARTITIONS_PROPERTY, legacyDefaultSinkPartitionCount));
        Mockito.when(ksqlConfig.getInt(SINK_NUMBER_OF_PARTITIONS_PROPERTY)).thenReturn(legacyDefaultSinkPartitionCount);
        createOutputNode(Collections.singletonMap(SINK_NUMBER_OF_REPLICAS_PROPERTY, ((short) (2))), true);
        // When:
        stream = buildStream();
        // Then:
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, legacyDefaultSinkPartitionCount, ((short) (2)), Collections.emptyMap());
    }

    @Test
    public void shouldUseLegacySinkReplicasCountIfLegacyIsTrue() {
        // Given:
        Mockito.<Object>when(ksqlConfig.values()).thenReturn(ImmutableMap.<String, Object>of(SINK_NUMBER_OF_REPLICAS_PROPERTY, legacyDefaultSinkReplicaCount));
        Mockito.when(ksqlConfig.getShort(SINK_NUMBER_OF_REPLICAS_PROPERTY)).thenReturn(legacyDefaultSinkReplicaCount);
        createOutputNode(Collections.singletonMap(SINK_NUMBER_OF_PARTITIONS_PROPERTY, 5), true);
        // When:
        stream = buildStream();
        // Then:
        Mockito.verify(mockTopicClient).createTopic(KsqlStructuredDataOutputNodeTest.SINK_KAFKA_TOPIC_NAME, 5, legacyDefaultSinkReplicaCount, Collections.emptyMap());
    }

    @Test
    public void shouldComputeQueryIdCorrectlyForStream() {
        // When:
        final QueryId queryId = outputNode.getQueryId(queryIdGenerator);
        // Then:
        Mockito.verify(queryIdGenerator, Mockito.times(1)).getNextId();
        MatcherAssert.assertThat(queryId, CoreMatchers.equalTo(new QueryId(("CSAS_0_" + (KsqlStructuredDataOutputNodeTest.QUERY_ID_STRING)))));
    }

    @Test
    public void shouldComputeQueryIdCorrectlyForTable() {
        // Given:
        final KsqlStructuredDataOutputNode outputNode = getKsqlStructuredDataOutputNodeForTable(Serdes.String());
        // When:
        final QueryId queryId = outputNode.getQueryId(queryIdGenerator);
        // Then:
        Mockito.verify(queryIdGenerator, Mockito.times(1)).getNextId();
        MatcherAssert.assertThat(queryId, CoreMatchers.equalTo(new QueryId(("CTAS_0_" + (KsqlStructuredDataOutputNodeTest.QUERY_ID_STRING)))));
    }

    @Test
    public void shouldComputeQueryIdCorrectlyForInsertInto() {
        // Given:
        createOutputNode(Collections.emptyMap(), false);
        // When:
        final QueryId queryId = outputNode.getQueryId(queryIdGenerator);
        // Then:
        Mockito.verify(queryIdGenerator, Mockito.times(1)).getNextId();
        MatcherAssert.assertThat(queryId, CoreMatchers.equalTo(new QueryId(("InsertQuery_" + (KsqlStructuredDataOutputNodeTest.QUERY_ID_STRING)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUseCorrectLoggerNameForSerializer() {
        // Given:
        final KsqlTopicSerDe topicSerde = Mockito.mock(KsqlTopicSerDe.class);
        final Serde serde = Mockito.mock(Serde.class);
        Mockito.when(topicSerde.getGenericRowSerde(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(serde);
        outputNode = new KsqlStructuredDataOutputNode(new PlanNodeId("0"), sourceNode, schema, new LongColumnTimestampExtractionPolicy("timestamp"), schema.field("key"), KsqlStructuredDataOutputNodeTest.mockTopic(topicSerde), "output", Collections.emptyMap(), Optional.empty(), false);
        // When:
        buildStream();
        // Then:
        Mockito.verify(topicSerde).getGenericRowSerde(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.startsWith(QueryLoggerUtil.queryLoggerName(new io.confluent.ksql.structured.QueryContext.Stacker(KsqlStructuredDataOutputNodeTest.QUERY_ID).push(outputNode.getId().toString()).getQueryContext())), ArgumentMatchers.any());
    }
}

