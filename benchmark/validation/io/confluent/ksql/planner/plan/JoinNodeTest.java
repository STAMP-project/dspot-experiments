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


import DataSource.DataSourceType;
import JoinNode.JoinType;
import JoinNode.JoinType.INNER;
import JoinNode.JoinType.LEFT;
import JoinNode.JoinType.OUTER;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS;
import KsqlConfig.KSQL_USE_NAMED_INTERNAL_TOPICS_OFF;
import QueryContext.Stacker;
import TopologyDescription.Processor;
import TopologyDescription.Source;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.function.FunctionRegistry;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.metastore.StructuredDataSource;
import io.confluent.ksql.parser.tree.WithinExpression;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.structured.SchemaKStream;
import io.confluent.ksql.structured.SchemaKTable;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.MetaStoreFixture;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.utils.Utils;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("SameParameterValue")
public class JoinNodeTest {
    private final KsqlConfig ksqlConfig = new KsqlConfig(new HashMap());

    private StreamsBuilder builder = new StreamsBuilder();

    private SchemaKStream stream;

    private JoinNode joinNode;

    private StreamsBuilder mockStreamsBuilder;

    private KsqlConfig mockKsqlConfig;

    private KsqlConfig mockKsqlConfigClonedWithOffsetReset;

    private KafkaTopicClient mockKafkaTopicClient;

    private FunctionRegistry mockFunctionRegistry;

    private Supplier<SchemaRegistryClient> mockSchemaRegistryClientFactory;

    private final Schema leftSchema = JoinNodeTest.createSchema();

    private final Schema rightSchema = JoinNodeTest.createSchema();

    private final Schema joinSchema = joinSchema();

    private static final String leftAlias = "left";

    private static final String rightAlias = "right";

    private static final String leftKeyFieldName = "COL0";

    private static final String rightKeyFieldName = "COL1";

    private static final PlanNodeId nodeId = new PlanNodeId("join");

    private static final QueryId queryId = new QueryId("join-query");

    private static final Stacker CONTEXT_STACKER = new io.confluent.ksql.structured.QueryContext.Stacker(JoinNodeTest.queryId).push(JoinNodeTest.nodeId.toString());

    private StructuredDataSourceNode left;

    private StructuredDataSourceNode right;

    private SchemaKStream leftSchemaKStream;

    private SchemaKStream rightSchemaKStream;

    private SchemaKTable leftSchemaKTable;

    private SchemaKTable rightSchemaKTable;

    private Field joinKey;

    private ServiceContext serviceContext;

    private ProcessingLogContext processingLogContext = ProcessingLogContext.create();

    @Test
    public void shouldBuildSourceNode() {
        setupTopicClientExpectations(1, 1);
        buildJoin();
        final TopologyDescription.Source node = ((TopologyDescription.Source) (PlanTestUtil.getNodeByName(builder.build(), PlanTestUtil.SOURCE_NODE)));
        final List<String> successors = node.successors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(node.predecessors(), CoreMatchers.equalTo(Collections.emptySet()));
        MatcherAssert.assertThat(successors, CoreMatchers.equalTo(Collections.singletonList(PlanTestUtil.MAPVALUES_NODE)));
        MatcherAssert.assertThat(node.topicSet(), CoreMatchers.equalTo(ImmutableSet.of("test2")));
    }

    @Test
    public void shouldUseLegacyNameForReduceTopicIfOptimizationsOff() {
        setupTopicClientExpectations(1, 1);
        buildJoin(ksqlConfig.overrideBreakingConfigsWithOriginalValues(ImmutableMap.of(KSQL_USE_NAMED_INTERNAL_TOPICS, String.valueOf(KSQL_USE_NAMED_INTERNAL_TOPICS_OFF))));
        final Topology topology = builder.build();
        final TopologyDescription.Processor leftJoin = ((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(topology, "KSTREAM-LEFTJOIN-0000000015")));
        MatcherAssert.assertThat(leftJoin.stores(), CoreMatchers.equalTo(Utils.mkSet("KSTREAM-AGGREGATE-STATE-STORE-0000000004")));
    }

    @Test
    public void shouldHaveLeftJoin() {
        setupTopicClientExpectations(1, 1);
        buildJoin();
        final Topology topology = builder.build();
        final TopologyDescription.Processor leftJoin = ((TopologyDescription.Processor) (PlanTestUtil.getNodeByName(topology, "KSTREAM-LEFTJOIN-0000000014")));
        final List<String> predecessors = leftJoin.predecessors().stream().map(TopologyDescription.Node::name).collect(Collectors.toList());
        MatcherAssert.assertThat(leftJoin.stores(), CoreMatchers.equalTo(Utils.mkSet("KafkaTopic_Right-reduce")));
        MatcherAssert.assertThat(predecessors, CoreMatchers.equalTo(Collections.singletonList("KSTREAM-SOURCE-0000000013")));
    }

    @Test
    public void shouldThrowOnPartitionMismatch() {
        setupTopicClientExpectations(1, 2);
        try {
            buildJoin(("SELECT t1.col0, t2.col0, t2.col1 " + "FROM test1 t1 LEFT JOIN test2 t2 ON t1.col0 = t2.col0;"), ksqlConfig);
        } catch (final KsqlException e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo(("Can't join TEST1 with TEST2 since the number of partitions don't match. TEST1 " + ("partitions = 1; TEST2 partitions = 2. Please repartition either one so that the " + "number of partitions match."))));
        }
        verify(mockKafkaTopicClient);
    }

    @Test
    public void shouldHaveAllFieldsFromJoinedInputs() {
        setupTopicClientExpectations(1, 1);
        buildJoin();
        final MetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());
        final StructuredDataSource source1 = metaStore.getSource("TEST1");
        final StructuredDataSource source2 = metaStore.getSource("TEST2");
        final Set<String> expected = source1.getSchema().fields().stream().map(( field) -> "T1." + (field.name())).collect(Collectors.toSet());
        expected.addAll(source2.getSchema().fields().stream().map(( field) -> "T2." + (field.name())).collect(Collectors.toSet()));
        final Set<String> fields = stream.getSchema().fields().stream().map(Field::name).collect(Collectors.toSet());
        MatcherAssert.assertThat(fields, CoreMatchers.equalTo(expected));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToStreamLeftJoin() {
        // Given:
        setupStream(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKStream, JoinNodeTest.leftKeyFieldName);
        setupStream(right, JoinNodeTest.CONTEXT_STACKER, rightSchemaKStream, rightSchema, 2);
        final WithinExpression withinExpression = new WithinExpression(10, TimeUnit.SECONDS);
        expect(leftSchemaKStream.leftJoin(eq(rightSchemaKStream), eq(joinSchema), eq(joinKey), eq(withinExpression.joinWindow()), anyObject(Serde.class), anyObject(Serde.class), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKStream.class));
        replay(left, right, leftSchemaKStream, rightSchemaKStream);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.LEFT, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, withinExpression, DataSourceType.KSTREAM, DataSourceType.KSTREAM);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKStream, rightSchemaKStream);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(LEFT, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToStreamInnerJoin() {
        // Given:
        setupStream(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKStream, JoinNodeTest.leftKeyFieldName);
        setupStream(right, JoinNodeTest.CONTEXT_STACKER, rightSchemaKStream, rightSchema, 2);
        final WithinExpression withinExpression = new WithinExpression(10, TimeUnit.SECONDS);
        expect(leftSchemaKStream.join(eq(rightSchemaKStream), eq(joinSchema), eq(joinKey), eq(withinExpression.joinWindow()), anyObject(Serde.class), anyObject(Serde.class), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKStream.class));
        replay(left, right, leftSchemaKStream, rightSchemaKStream);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.INNER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, withinExpression, DataSourceType.KSTREAM, DataSourceType.KSTREAM);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKStream, rightSchemaKStream);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(INNER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToStreamOuterJoin() {
        // Given:
        setupStream(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKStream, JoinNodeTest.leftKeyFieldName);
        setupStream(right, JoinNodeTest.CONTEXT_STACKER, rightSchemaKStream, rightSchema, 2);
        final WithinExpression withinExpression = new WithinExpression(10, TimeUnit.SECONDS);
        expect(leftSchemaKStream.outerJoin(eq(rightSchemaKStream), eq(joinSchema), eq(joinKey), eq(withinExpression.joinWindow()), anyObject(Serde.class), anyObject(Serde.class), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKStream.class));
        replay(left, right, leftSchemaKStream, rightSchemaKStream);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.OUTER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, withinExpression, DataSourceType.KSTREAM, DataSourceType.KSTREAM);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKStream, rightSchemaKStream);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(OUTER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotPerformStreamStreamJoinWithoutJoinWindow() {
        expect(left.getSchema()).andReturn(leftSchema);
        expect(left.getPartitions(mockKafkaTopicClient)).andReturn(2);
        expect(right.getSchema()).andReturn(rightSchema);
        expect(right.getPartitions(mockKafkaTopicClient)).andReturn(2);
        replay(left, right, leftSchemaKStream, rightSchemaKStream);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.INNER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KSTREAM, DataSourceType.KSTREAM);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
            Assert.fail("Should have raised an exception since no join window was specified");
        } catch (final KsqlException e) {
            Assert.assertTrue(e.getMessage().startsWith(("Stream-Stream joins must have a WITHIN clause specified" + ". None was provided.")));
        }
        verify(left, right, leftSchemaKStream, rightSchemaKStream);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(INNER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotPerformJoinIfInputPartitionsMisMatch() {
        expect(left.getSchema()).andReturn(leftSchema);
        expect(left.getPartitions(mockKafkaTopicClient)).andReturn(3);
        expect(right.getSchema()).andReturn(rightSchema);
        expect(right.getPartitions(mockKafkaTopicClient)).andReturn(2);
        JoinNodeTest.expectSourceName(left);
        JoinNodeTest.expectSourceName(right);
        final WithinExpression withinExpression = new WithinExpression(10, TimeUnit.SECONDS);
        replay(left, right, leftSchemaKStream, rightSchemaKStream);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.OUTER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, withinExpression, DataSourceType.KSTREAM, DataSourceType.KSTREAM);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
            Assert.fail(("should have raised an exception since the number of partitions on the input sources " + "don't match"));
        } catch (final KsqlException e) {
            Assert.assertTrue(e.getMessage().startsWith(("Can't join Foobar with Foobar since the number of " + "partitions don't match.")));
        }
        verify(left, right, leftSchemaKStream, rightSchemaKStream);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(OUTER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFailJoinIfTableCriteriaColumnIsNotKey() {
        setupStream(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        JoinNodeTest.expectKeyField(rightSchemaKTable, JoinNodeTest.rightKeyFieldName);
        replay(left, right, leftSchemaKStream, rightSchemaKTable);
        final String rightCriteriaColumn = JoinNodeTest.getNonKeyColumn(rightSchema, JoinNodeTest.rightKeyFieldName).get();
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.LEFT, left, right, JoinNodeTest.leftKeyFieldName, rightCriteriaColumn, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KSTREAM, DataSourceType.KTABLE);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo(String.format(("Source table (%s) key column (%s) is not the column " + "used in the join criteria (%s)."), JoinNodeTest.rightAlias, JoinNodeTest.rightKeyFieldName, rightCriteriaColumn)));
            return;
        }
        Assert.fail("buildStream did not throw exception");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToTableLeftJoin() {
        // Given:
        setupStream(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKStream, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        expect(leftSchemaKStream.leftJoin(eq(rightSchemaKTable), eq(joinSchema), eq(joinKey), anyObject(Serde.class), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKStream.class));
        replay(left, right, leftSchemaKStream, rightSchemaKTable);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.LEFT, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KSTREAM, DataSourceType.KTABLE);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKStream, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(LEFT, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToTableInnerJoin() {
        // Given:
        setupStream(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKStream, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        expect(leftSchemaKStream.join(eq(rightSchemaKTable), eq(joinSchema), eq(joinKey), anyObject(Serde.class), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKStream.class));
        replay(left, right, leftSchemaKStream, rightSchemaKTable);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.INNER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KSTREAM, DataSourceType.KTABLE);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKStream, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(INNER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotAllowStreamToTableOuterJoin() {
        // Given:
        setupStreamWithoutSerde(left, JoinNodeTest.CONTEXT_STACKER, leftSchemaKStream, leftSchema, 2);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        replay(left, right, leftSchemaKStream, rightSchemaKTable);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.OUTER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KSTREAM, DataSourceType.KTABLE);
        // When:
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
            Assert.fail(("Should have failed to build the stream since stream-table outer joins are not " + "supported"));
        } catch (final KsqlException e) {
            // Then:
            Assert.assertEquals(("Full outer joins between streams and tables (stream: left, table: right) are " + "not supported."), e.getMessage());
        }
        // Then:
        verify(left, right, leftSchemaKStream, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(OUTER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotPerformStreamToTableJoinIfJoinWindowIsSpecified() {
        expect(left.getSchema()).andReturn(leftSchema);
        expect(left.getPartitions(mockKafkaTopicClient)).andReturn(3);
        expect(right.getSchema()).andReturn(rightSchema);
        expect(right.getPartitions(mockKafkaTopicClient)).andReturn(3);
        final WithinExpression withinExpression = new WithinExpression(10, TimeUnit.SECONDS);
        replay(left, right, leftSchemaKStream, rightSchemaKTable);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.OUTER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, withinExpression, DataSourceType.KSTREAM, DataSourceType.KTABLE);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
            Assert.fail(("should have raised an exception since a join window was provided for a stream-table " + "join"));
        } catch (final KsqlException e) {
            Assert.assertTrue(e.getMessage().startsWith(("A window definition was provided for a " + "Stream-Table join.")));
        }
        verify(left, right, leftSchemaKStream, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(OUTER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFailTableTableJoinIfLeftCriteriaColumnIsNotKey() {
        setupTable(left, leftSchemaKTable, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKTable, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        replay(left, right, leftSchemaKTable, rightSchemaKTable);
        final String leftCriteriaColumn = JoinNodeTest.getNonKeyColumn(leftSchema, JoinNodeTest.leftKeyFieldName).get();
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.LEFT, left, right, leftCriteriaColumn, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KTABLE, DataSourceType.KTABLE);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo(String.format(("Source table (%s) key column (%s) is not the column " + "used in the join criteria (%s)."), JoinNodeTest.leftAlias, JoinNodeTest.leftKeyFieldName, leftCriteriaColumn)));
            return;
        }
        Assert.fail("buildStream did not throw exception");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldFailTableTableJoinIfRightCriteriaColumnIsNotKey() {
        setupTable(left, leftSchemaKTable, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKTable, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        JoinNodeTest.expectKeyField(rightSchemaKTable, JoinNodeTest.rightKeyFieldName);
        replay(left, right, leftSchemaKTable, rightSchemaKTable);
        final String rightCriteriaColumn = JoinNodeTest.getNonKeyColumn(rightSchema, JoinNodeTest.rightKeyFieldName).get();
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.LEFT, left, right, JoinNodeTest.leftKeyFieldName, rightCriteriaColumn, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KTABLE, DataSourceType.KTABLE);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        } catch (final KsqlException e) {
            MatcherAssert.assertThat(e.getMessage(), CoreMatchers.equalTo(String.format(("Source table (%s) key column (%s) is not the column " + "used in the join criteria (%s)."), JoinNodeTest.rightAlias, JoinNodeTest.rightKeyFieldName, rightCriteriaColumn)));
            return;
        }
        Assert.fail("buildStream did not throw exception");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformTableToTableInnerJoin() {
        // Given:
        setupTable(left, leftSchemaKTable, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKTable, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        replay(left, right);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.INNER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KTABLE, DataSourceType.KTABLE);
        expect(leftSchemaKTable.join(eq(rightSchemaKTable), eq(joinSchema), eq(joinKey), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKTable.class));
        replay(leftSchemaKTable, rightSchemaKTable);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKTable, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(INNER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformTableToTableLeftJoin() {
        // Given:
        setupTable(left, leftSchemaKTable, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKTable, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        replay(left, right);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.LEFT, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KTABLE, DataSourceType.KTABLE);
        expect(leftSchemaKTable.leftJoin(eq(rightSchemaKTable), eq(joinSchema), eq(joinKey), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKTable.class));
        replay(leftSchemaKTable, rightSchemaKTable);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKTable, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(LEFT, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformTableToTableOuterJoin() {
        // Given:
        setupTable(left, leftSchemaKTable, leftSchema, 2);
        JoinNodeTest.expectKeyField(leftSchemaKTable, JoinNodeTest.leftKeyFieldName);
        setupTable(right, rightSchemaKTable, rightSchema, 2);
        replay(left, right);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.OUTER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, null, DataSourceType.KTABLE, DataSourceType.KTABLE);
        expect(leftSchemaKTable.outerJoin(eq(rightSchemaKTable), eq(joinSchema), eq(joinKey), eq(JoinNodeTest.CONTEXT_STACKER))).andReturn(niceMock(SchemaKTable.class));
        replay(leftSchemaKTable, rightSchemaKTable);
        // When:
        joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
        // Then:
        verify(left, right, leftSchemaKTable, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(OUTER, joinNode.getJoinType());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotPerformTableToTableJoinIfJoinWindowIsSpecified() {
        expect(left.getSchema()).andReturn(leftSchema);
        expect(left.getPartitions(mockKafkaTopicClient)).andReturn(3);
        expect(right.getSchema()).andReturn(rightSchema);
        expect(right.getPartitions(mockKafkaTopicClient)).andReturn(3);
        final WithinExpression withinExpression = new WithinExpression(10, TimeUnit.SECONDS);
        replay(left, right, leftSchemaKTable, rightSchemaKTable);
        final JoinNode joinNode = new JoinNode(JoinNodeTest.nodeId, JoinType.OUTER, left, right, JoinNodeTest.leftKeyFieldName, JoinNodeTest.rightKeyFieldName, JoinNodeTest.leftAlias, JoinNodeTest.rightAlias, withinExpression, DataSourceType.KTABLE, DataSourceType.KTABLE);
        try {
            joinNode.buildStream(mockStreamsBuilder, mockKsqlConfig, serviceContext, processingLogContext, mockFunctionRegistry, JoinNodeTest.queryId);
            Assert.fail(("should have raised an exception since a join window was provided for a stream-table " + "join"));
        } catch (final KsqlException e) {
            Assert.assertTrue(e.getMessage().startsWith(("A window definition was provided for a " + "Table-Table join.")));
        }
        verify(left, right, leftSchemaKTable, rightSchemaKTable);
        Assert.assertEquals(JoinNodeTest.leftKeyFieldName, joinNode.getLeftKeyFieldName());
        Assert.assertEquals(JoinNodeTest.rightKeyFieldName, joinNode.getRightKeyFieldName());
        Assert.assertEquals(JoinNodeTest.leftAlias, joinNode.getLeftAlias());
        Assert.assertEquals(JoinNodeTest.rightAlias, joinNode.getRightAlias());
        Assert.assertEquals(OUTER, joinNode.getJoinType());
    }
}

