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
import Schema.OPTIONAL_INT64_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import Schema.Type.FLOAT64;
import Schema.Type.INT32;
import Schema.Type.INT64;
import Schema.Type.STRING;
import SchemaKStream.KsqlValueJoiner;
import SchemaKStream.Type.JOIN;
import com.google.common.collect.ImmutableList;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.ksql.GenericRow;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.metastore.KsqlStream;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.parser.tree.DereferenceExpression;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.planner.plan.FilterNode;
import io.confluent.ksql.planner.plan.PlanNode;
import io.confluent.ksql.planner.plan.ProjectNode;
import io.confluent.ksql.streams.GroupedFactory;
import io.confluent.ksql.streams.JoinedFactory;
import io.confluent.ksql.streams.StreamsUtil;
import io.confluent.ksql.structured.SchemaKStream.Type;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.MetaStoreFixture;
import io.confluent.ksql.util.SelectExpression;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serdes.StringSerde;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.Joined;
import org.apache.kafka.streams.kstream.KGroupedStream;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Predicate;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class SchemaKStreamTest {
    private static final Expression COL0 = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("TEST1")), "COL0");

    private static final Expression COL1 = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("TEST1")), "COL1");

    private final MockSchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();

    private SchemaKStream initialSchemaKStream;

    private final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    private final MetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    private final Grouped grouped = Grouped.with("group", Serdes.String(), Serdes.String());

    private final Joined joined = Joined.with(Serdes.String(), Serdes.String(), Serdes.String(), "join");

    private KStream kStream;

    private KsqlStream ksqlStream;

    private InternalFunctionRegistry functionRegistry;

    private SchemaKStream secondSchemaKStream;

    private SchemaKTable schemaKTable;

    private Serde<GenericRow> leftSerde;

    private Serde<GenericRow> rightSerde;

    private Schema joinSchema;

    private Serde<GenericRow> rowSerde;

    private final Schema simpleSchema = SchemaBuilder.struct().field("key", OPTIONAL_STRING_SCHEMA).field("val", OPTIONAL_INT64_SCHEMA).build();

    private final Stacker queryContext = push("node");

    private final QueryContext parentContext = queryContext.push("parent").getQueryContext();

    private final Stacker childContextStacker = queryContext.push("child");

    private final ProcessingLogContext processingLogContext = ProcessingLogContext.create();

    @Mock
    private GroupedFactory mockGroupedFactory;

    @Mock
    private JoinedFactory mockJoinedFactory;

    @Mock
    private KStream mockKStream;

    @Test
    public void testSelectSchemaKStream() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;");
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        final List<SelectExpression> selectExpressions = projectNode.getProjectSelectExpressions();
        final SchemaKStream projectedSchemaKStream = initialSchemaKStream.select(selectExpressions, childContextStacker, processingLogContext);
        Assert.assertEquals(3, projectedSchemaKStream.getSchema().fields().size());
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL0"), projectedSchemaKStream.getSchema().fields().get(0));
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL2"), projectedSchemaKStream.getSchema().fields().get(1));
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL3"), projectedSchemaKStream.getSchema().fields().get(2));
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL0").schema().type(), INT64);
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL2").schema().type(), STRING);
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL3").schema().type(), FLOAT64);
        Assert.assertSame(projectedSchemaKStream.getSourceSchemaKStreams().get(0), initialSchemaKStream);
    }

    @Test
    public void shouldUpdateKeyIfRenamed() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT col0 as NEWKEY, col2, col3 FROM test1;");
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        final List<SelectExpression> selectExpressions = projectNode.getProjectSelectExpressions();
        final SchemaKStream projectedSchemaKStream = initialSchemaKStream.select(selectExpressions, childContextStacker, processingLogContext);
        MatcherAssert.assertThat(projectedSchemaKStream.getKeyField(), Matchers.equalTo(new org.apache.kafka.connect.data.Field("NEWKEY", 0, Schema.OPTIONAL_INT64_SCHEMA)));
    }

    @Test
    public void shouldPreserveKeyOnSelectStar() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT * FROM test1;");
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        final List<SelectExpression> selectExpressions = projectNode.getProjectSelectExpressions();
        final SchemaKStream projectedSchemaKStream = initialSchemaKStream.select(selectExpressions, childContextStacker, processingLogContext);
        MatcherAssert.assertThat(projectedSchemaKStream.getKeyField(), Matchers.equalTo(initialSchemaKStream.getKeyField()));
    }

    @Test
    public void shouldUpdateKeyIfMovedToDifferentIndex() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT col2, col0, col3 FROM test1;");
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        final List<SelectExpression> selectExpressions = projectNode.getProjectSelectExpressions();
        final SchemaKStream projectedSchemaKStream = initialSchemaKStream.select(selectExpressions, childContextStacker, processingLogContext);
        MatcherAssert.assertThat(projectedSchemaKStream.getKeyField(), Matchers.equalTo(new org.apache.kafka.connect.data.Field("COL0", 1, Schema.OPTIONAL_INT64_SCHEMA)));
    }

    @Test
    public void shouldDropKeyIfNotSelected() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT col2, col3 FROM test1;");
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        final List<SelectExpression> selectExpressions = projectNode.getProjectSelectExpressions();
        final SchemaKStream projectedSchemaKStream = initialSchemaKStream.select(selectExpressions, childContextStacker, processingLogContext);
        MatcherAssert.assertThat(projectedSchemaKStream.getKeyField(), CoreMatchers.nullValue());
    }

    @Test
    public void testSelectWithExpression() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT col0, LEN(UCASE(col2)), col3*3+5 FROM test1 WHERE col0 > 100;");
        final ProjectNode projectNode = ((ProjectNode) (logicalPlan.getSources().get(0)));
        final SchemaKStream projectedSchemaKStream = initialSchemaKStream.select(projectNode.getProjectSelectExpressions(), childContextStacker, processingLogContext);
        Assert.assertTrue(((projectedSchemaKStream.getSchema().fields().size()) == 3));
        Assert.assertTrue(((projectedSchemaKStream.getSchema().field("COL0")) == (projectedSchemaKStream.getSchema().fields().get(0))));
        Assert.assertTrue(((projectedSchemaKStream.getSchema().field("KSQL_COL_1")) == (projectedSchemaKStream.getSchema().fields().get(1))));
        Assert.assertTrue(((projectedSchemaKStream.getSchema().field("KSQL_COL_2")) == (projectedSchemaKStream.getSchema().fields().get(2))));
        Assert.assertSame(projectedSchemaKStream.getSchema().field("COL0").schema().type(), INT64);
        Assert.assertSame(projectedSchemaKStream.getSchema().fields().get(1).schema().type(), INT32);
        Assert.assertSame(projectedSchemaKStream.getSchema().fields().get(2).schema().type(), FLOAT64);
        Assert.assertSame(projectedSchemaKStream.getSourceSchemaKStreams().get(0), initialSchemaKStream);
    }

    @Test
    public void testFilter() {
        final PlanNode logicalPlan = givenInitialKStreamOf("SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;");
        final FilterNode filterNode = ((FilterNode) (logicalPlan.getSources().get(0).getSources().get(0)));
        final SchemaKStream filteredSchemaKStream = initialSchemaKStream.filter(filterNode.getPredicate(), childContextStacker, processingLogContext);
        Assert.assertTrue(((filteredSchemaKStream.getSchema().fields().size()) == 8));
        Assert.assertTrue(((filteredSchemaKStream.getSchema().field("TEST1.COL0")) == (filteredSchemaKStream.getSchema().fields().get(2))));
        Assert.assertTrue(((filteredSchemaKStream.getSchema().field("TEST1.COL1")) == (filteredSchemaKStream.getSchema().fields().get(3))));
        Assert.assertTrue(((filteredSchemaKStream.getSchema().field("TEST1.COL2")) == (filteredSchemaKStream.getSchema().fields().get(4))));
        Assert.assertTrue(((filteredSchemaKStream.getSchema().field("TEST1.COL3")) == (filteredSchemaKStream.getSchema().fields().get(5))));
        Assert.assertSame(filteredSchemaKStream.getSchema().field("TEST1.COL0").schema().type(), INT64);
        Assert.assertSame(filteredSchemaKStream.getSchema().field("TEST1.COL1").schema().type(), STRING);
        Assert.assertSame(filteredSchemaKStream.getSchema().field("TEST1.COL2").schema().type(), STRING);
        Assert.assertSame(filteredSchemaKStream.getSchema().field("TEST1.COL3").schema().type(), FLOAT64);
        Assert.assertSame(filteredSchemaKStream.getSourceSchemaKStreams().get(0), initialSchemaKStream);
    }

    @Test
    public void testSelectKey() {
        givenInitialKStreamOf("SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;");
        final SchemaKStream rekeyedSchemaKStream = initialSchemaKStream.selectKey(initialSchemaKStream.getSchema().fields().get(3), true, childContextStacker);
        MatcherAssert.assertThat(rekeyedSchemaKStream.getKeyField().name().toUpperCase(), Matchers.equalTo("TEST1.COL1"));
    }

    @Test
    public void testGroupByKey() {
        // Given:
        givenInitialKStreamOf("SELECT col0, col1 FROM test1 WHERE col0 > 100;");
        final List<Expression> groupBy = Collections.singletonList(new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("TEST1")), "COL0"));
        // When:
        final SchemaKGroupedStream groupedSchemaKStream = initialSchemaKStream.groupBy(rowSerde, groupBy, childContextStacker);
        // Then:
        MatcherAssert.assertThat(groupedSchemaKStream.getKeyField().name(), Matchers.is("COL0"));
        MatcherAssert.assertThat(groupedSchemaKStream.getKeyField().index(), Matchers.is(2));
    }

    @Test
    public void testGroupByMultipleColumns() {
        // Given:
        givenInitialKStreamOf("SELECT col0, col1 FROM test1 WHERE col0 > 100;");
        final List<Expression> groupBy = ImmutableList.of(new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("TEST1")), "COL1"), new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of("TEST1")), "COL0"));
        // When:
        final SchemaKGroupedStream groupedSchemaKStream = initialSchemaKStream.groupBy(rowSerde, groupBy, childContextStacker);
        // Then:
        MatcherAssert.assertThat(groupedSchemaKStream.getKeyField().name(), Matchers.is("TEST1.COL1|+|TEST1.COL0"));
        MatcherAssert.assertThat(groupedSchemaKStream.getKeyField().index(), Matchers.is((-1)));
    }

    @Test
    public void testGroupByMoreComplexExpression() {
        // Given:
        givenInitialKStreamOf("SELECT col0, col1 FROM test1 WHERE col0 > 100;");
        final Expression groupBy = new io.confluent.ksql.parser.tree.FunctionCall(QualifiedName.of("UCASE"), ImmutableList.of(SchemaKStreamTest.COL1));
        // When:
        final SchemaKGroupedStream groupedSchemaKStream = initialSchemaKStream.groupBy(rowSerde, ImmutableList.of(groupBy), childContextStacker);
        // Then:
        MatcherAssert.assertThat(groupedSchemaKStream.getKeyField().name(), Matchers.is("UCASE(TEST1.COL1)"));
        MatcherAssert.assertThat(groupedSchemaKStream.getKeyField().index(), Matchers.is((-1)));
    }

    @Test
    public void shouldUseFactoryForGroupedWithoutRekey() {
        // Given:
        final KGroupedStream groupedStream = Mockito.mock(KGroupedStream.class);
        Mockito.when(mockKStream.groupByKey(ArgumentMatchers.any(Grouped.class))).thenReturn(groupedStream);
        final Expression keyExpression = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of(ksqlStream.getName())), ksqlStream.getKeyField().name());
        final List<Expression> groupByExpressions = Collections.singletonList(keyExpression);
        initialSchemaKStream = buildSchemaKStream(mockKStream, mockGroupedFactory, mockJoinedFactory);
        // When:
        initialSchemaKStream.groupBy(leftSerde, groupByExpressions, childContextStacker);
        // Then:
        Mockito.verify(mockGroupedFactory).create(ArgumentMatchers.eq(StreamsUtil.buildOpName(childContextStacker.getQueryContext())), ArgumentMatchers.any(StringSerde.class), ArgumentMatchers.same(leftSerde));
        Mockito.verify(mockKStream).groupByKey(ArgumentMatchers.same(grouped));
    }

    @Test
    public void shouldUseFactoryForGrouped() {
        // Given:
        Mockito.when(mockKStream.filter(ArgumentMatchers.any(Predicate.class))).thenReturn(mockKStream);
        final KGroupedStream groupedStream = Mockito.mock(KGroupedStream.class);
        Mockito.when(mockKStream.groupBy(ArgumentMatchers.any(KeyValueMapper.class), ArgumentMatchers.any(Grouped.class))).thenReturn(groupedStream);
        final Expression col0Expression = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of(ksqlStream.getName())), "COL0");
        final Expression col1Expression = new DereferenceExpression(new io.confluent.ksql.parser.tree.QualifiedNameReference(QualifiedName.of(ksqlStream.getName())), "COL1");
        final List<Expression> groupByExpressions = Arrays.asList(col1Expression, col0Expression);
        initialSchemaKStream = buildSchemaKStream(mockKStream, mockGroupedFactory, mockJoinedFactory);
        // When:
        initialSchemaKStream.groupBy(leftSerde, groupByExpressions, childContextStacker);
        // Then:
        Mockito.verify(mockGroupedFactory).create(ArgumentMatchers.eq(StreamsUtil.buildOpName(childContextStacker.getQueryContext())), ArgumentMatchers.any(StringSerde.class), ArgumentMatchers.same(leftSerde));
        Mockito.verify(mockKStream).groupBy(ArgumentMatchers.any(KeyValueMapper.class), ArgumentMatchers.same(grouped));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToStreamLeftJoin() {
        // Given:
        final SchemaKStream initialSchemaKStream = buildSchemaKStreamForJoin(ksqlStream, mockKStream, mockGroupedFactory, mockJoinedFactory);
        final JoinWindows joinWindow = JoinWindows.of(Duration.ofMillis(10L));
        Mockito.when(mockKStream.leftJoin(ArgumentMatchers.any(KStream.class), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.any(JoinWindows.class), ArgumentMatchers.any(Joined.class))).thenReturn(mockKStream);
        // When:
        final SchemaKStream joinedKStream = initialSchemaKStream.leftJoin(secondSchemaKStream, joinSchema, joinSchema.fields().get(0), joinWindow, leftSerde, rightSerde, childContextStacker);
        // Then:
        verifyCreateJoined(rightSerde);
        Mockito.verify(mockKStream).leftJoin(ArgumentMatchers.eq(secondSchemaKStream.kstream), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.eq(joinWindow), ArgumentMatchers.same(joined));
        MatcherAssert.assertThat(joinedKStream, CoreMatchers.instanceOf(SchemaKStream.class));
        Assert.assertEquals(JOIN, joinedKStream.type);
        Assert.assertEquals(joinSchema, joinedKStream.schema);
        Assert.assertEquals(joinSchema.fields().get(0), joinedKStream.keyField);
        Assert.assertEquals(Arrays.asList(initialSchemaKStream, secondSchemaKStream), joinedKStream.sourceSchemaKStreams);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToStreamInnerJoin() {
        // Given:
        final SchemaKStream initialSchemaKStream = buildSchemaKStreamForJoin(ksqlStream, mockKStream, mockGroupedFactory, mockJoinedFactory);
        final JoinWindows joinWindow = JoinWindows.of(Duration.ofMillis(10L));
        Mockito.when(mockKStream.join(ArgumentMatchers.any(KStream.class), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.any(JoinWindows.class), ArgumentMatchers.any(Joined.class))).thenReturn(mockKStream);
        // When:
        final SchemaKStream joinedKStream = initialSchemaKStream.join(secondSchemaKStream, joinSchema, joinSchema.fields().get(0), joinWindow, leftSerde, rightSerde, childContextStacker);
        // Then:
        verifyCreateJoined(rightSerde);
        Mockito.verify(mockKStream).join(ArgumentMatchers.eq(secondSchemaKStream.kstream), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.eq(joinWindow), ArgumentMatchers.same(joined));
        MatcherAssert.assertThat(joinedKStream, CoreMatchers.instanceOf(SchemaKStream.class));
        Assert.assertEquals(JOIN, joinedKStream.type);
        Assert.assertEquals(joinSchema, joinedKStream.schema);
        Assert.assertEquals(joinSchema.fields().get(0), joinedKStream.keyField);
        Assert.assertEquals(Arrays.asList(initialSchemaKStream, secondSchemaKStream), joinedKStream.sourceSchemaKStreams);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToStreamOuterJoin() {
        // Given:
        final SchemaKStream initialSchemaKStream = buildSchemaKStreamForJoin(ksqlStream, mockKStream, mockGroupedFactory, mockJoinedFactory);
        final JoinWindows joinWindow = JoinWindows.of(Duration.ofMillis(10L));
        Mockito.when(mockKStream.outerJoin(ArgumentMatchers.any(KStream.class), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.any(JoinWindows.class), ArgumentMatchers.any(Joined.class))).thenReturn(mockKStream);
        // When:
        final SchemaKStream joinedKStream = initialSchemaKStream.outerJoin(secondSchemaKStream, joinSchema, joinSchema.fields().get(0), joinWindow, leftSerde, rightSerde, childContextStacker);
        // Then:
        verifyCreateJoined(rightSerde);
        Mockito.verify(mockKStream).outerJoin(ArgumentMatchers.eq(secondSchemaKStream.kstream), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.eq(joinWindow), ArgumentMatchers.same(joined));
        MatcherAssert.assertThat(joinedKStream, CoreMatchers.instanceOf(SchemaKStream.class));
        Assert.assertEquals(JOIN, joinedKStream.type);
        Assert.assertEquals(joinSchema, joinedKStream.schema);
        Assert.assertEquals(joinSchema.fields().get(0), joinedKStream.keyField);
        Assert.assertEquals(Arrays.asList(initialSchemaKStream, secondSchemaKStream), joinedKStream.sourceSchemaKStreams);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToTableLeftJoin() {
        // Given:
        final SchemaKStream initialSchemaKStream = buildSchemaKStreamForJoin(ksqlStream, mockKStream, mockGroupedFactory, mockJoinedFactory);
        Mockito.when(mockKStream.leftJoin(ArgumentMatchers.any(KTable.class), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.any(Joined.class))).thenReturn(mockKStream);
        // When:
        final SchemaKStream joinedKStream = initialSchemaKStream.leftJoin(schemaKTable, joinSchema, joinSchema.fields().get(0), leftSerde, childContextStacker);
        // Then:
        verifyCreateJoined(null);
        Mockito.verify(mockKStream).leftJoin(ArgumentMatchers.eq(schemaKTable.getKtable()), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.same(joined));
        MatcherAssert.assertThat(joinedKStream, CoreMatchers.instanceOf(SchemaKStream.class));
        Assert.assertEquals(JOIN, joinedKStream.type);
        Assert.assertEquals(joinSchema, joinedKStream.schema);
        Assert.assertEquals(joinSchema.fields().get(0), joinedKStream.keyField);
        Assert.assertEquals(Arrays.asList(initialSchemaKStream, schemaKTable), joinedKStream.sourceSchemaKStreams);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldPerformStreamToTableInnerJoin() {
        // Given:
        final SchemaKStream initialSchemaKStream = buildSchemaKStreamForJoin(ksqlStream, mockKStream, mockGroupedFactory, mockJoinedFactory);
        Mockito.when(mockKStream.join(ArgumentMatchers.any(KTable.class), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.any(Joined.class))).thenReturn(mockKStream);
        // When:
        final SchemaKStream joinedKStream = initialSchemaKStream.join(schemaKTable, joinSchema, joinSchema.fields().get(0), leftSerde, childContextStacker);
        // Then:
        verifyCreateJoined(null);
        Mockito.verify(mockKStream).join(ArgumentMatchers.eq(schemaKTable.getKtable()), ArgumentMatchers.any(KsqlValueJoiner.class), ArgumentMatchers.same(joined));
        MatcherAssert.assertThat(joinedKStream, CoreMatchers.instanceOf(SchemaKStream.class));
        Assert.assertEquals(JOIN, joinedKStream.type);
        Assert.assertEquals(joinSchema, joinedKStream.schema);
        Assert.assertEquals(joinSchema.fields().get(0), joinedKStream.keyField);
        Assert.assertEquals(Arrays.asList(initialSchemaKStream, schemaKTable), joinedKStream.sourceSchemaKStreams);
    }

    @Test
    public void shouldSummarizeExecutionPlanCorrectly() {
        // Given:
        final SchemaKStream parentSchemaKStream = Mockito.mock(SchemaKStream.class);
        Mockito.when(parentSchemaKStream.getExecutionPlan(ArgumentMatchers.anyString())).thenReturn("parent plan");
        final SchemaKStream schemaKtream = new SchemaKStream(simpleSchema, Mockito.mock(KStream.class), simpleSchema.field("key"), ImmutableList.of(parentSchemaKStream), Serdes.String(), Type.SOURCE, ksqlConfig, functionRegistry, queryContext.push("source").getQueryContext());
        // When/Then:
        final String expected = " > [ SOURCE ] | Schema: [key : VARCHAR, val : BIGINT] | Logger: query.node.source\n\t" + "parent plan";
        MatcherAssert.assertThat(schemaKtream.getExecutionPlan(""), Matchers.equalTo(expected));
    }

    @Test
    public void shouldSummarizeExecutionPlanCorrectlyForRoot() {
        // Given:
        final SchemaKStream schemaKtream = new SchemaKStream(simpleSchema, Mockito.mock(KStream.class), simpleSchema.field("key"), Collections.emptyList(), Serdes.String(), Type.SOURCE, ksqlConfig, functionRegistry, queryContext.push("source").getQueryContext());
        // When/Then:
        final String expected = " > [ SOURCE ] | Schema: [key : VARCHAR, val : BIGINT] | Logger: query.node.source\n";
        MatcherAssert.assertThat(schemaKtream.getExecutionPlan(""), Matchers.equalTo(expected));
    }

    @Test
    public void shouldSummarizeExecutionPlanCorrectlyWhenMultipleParents() {
        // Given:
        final SchemaKStream parentSchemaKStream1 = Mockito.mock(SchemaKStream.class);
        Mockito.when(parentSchemaKStream1.getExecutionPlan(ArgumentMatchers.anyString())).thenReturn("parent 1 plan");
        final SchemaKStream parentSchemaKStream2 = Mockito.mock(SchemaKStream.class);
        Mockito.when(parentSchemaKStream2.getExecutionPlan(ArgumentMatchers.anyString())).thenReturn("parent 2 plan");
        final SchemaKStream schemaKtream = new SchemaKStream(simpleSchema, Mockito.mock(KStream.class), simpleSchema.field("key"), ImmutableList.of(parentSchemaKStream1, parentSchemaKStream2), Serdes.String(), Type.SOURCE, ksqlConfig, functionRegistry, queryContext.push("source").getQueryContext());
        // When/Then:
        final String expected = " > [ SOURCE ] | Schema: [key : VARCHAR, val : BIGINT] | Logger: query.node.source\n" + ("\tparent 1 plan" + "\tparent 2 plan");
        MatcherAssert.assertThat(schemaKtream.getExecutionPlan(""), Matchers.equalTo(expected));
    }
}

