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
package io.confluent.ksql.physical;


import ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG;
import DataSource.DataSourceSerDe.DELIMITED;
import DataSource.DataSourceType.KSTREAM;
import ProductionExceptionHandlerUtil.KSQL_PRODUCTION_ERROR_LOGGER;
import StreamsConfig.NO_OPTIMIZATION;
import StreamsConfig.OPTIMIZE;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.engine.KsqlEngineTestUtil;
import io.confluent.ksql.function.InternalFunctionRegistry;
import io.confluent.ksql.logging.processing.ProcessingLogContext;
import io.confluent.ksql.logging.processing.ProcessingLogger;
import io.confluent.ksql.logging.processing.ProcessingLoggerFactory;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.metrics.ConsumerCollector;
import io.confluent.ksql.metrics.ProducerCollector;
import io.confluent.ksql.planner.plan.KsqlBareOutputNode;
import io.confluent.ksql.planner.plan.KsqlStructuredDataOutputNode;
import io.confluent.ksql.planner.plan.OutputNode;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.services.FakeKafkaTopicClient;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.testutils.AnalysisTestUtil;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlConstants;
import io.confluent.ksql.util.KsqlExceptionMatcher;
import io.confluent.ksql.util.KsqlStatementException;
import io.confluent.ksql.util.MetaStoreFixture;
import io.confluent.ksql.util.QueryMetadata;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class PhysicalPlanBuilderTest {
    private static final String createStream = "CREATE STREAM TEST1 (COL0 BIGINT, COL1 VARCHAR, COL2 DOUBLE) WITH ( " + "KAFKA_TOPIC = 'test1', VALUE_FORMAT = 'JSON' );";

    private static final String simpleSelectFilter = "SELECT col0, col2, col3 FROM test1 WHERE col0 > 100;";

    private PhysicalPlanBuilder physicalPlanBuilder;

    private final MutableMetaStore metaStore = MetaStoreFixture.getNewMetaStore(new InternalFunctionRegistry());

    private final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092", "commit.interval.ms", 0, "cache.max.bytes.buffering", 0, "auto.offset.reset", "earliest"));

    private final KafkaTopicClient kafkaTopicClient = new FakeKafkaTopicClient();

    private KsqlEngine ksqlEngine;

    private ProcessingLogContext processingLogContext;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private ServiceContext serviceContext;

    @Mock
    private Consumer<QueryMetadata> queryCloseCallback;

    // Test implementation of KafkaStreamsBuilder that tracks calls and returned values
    private static class TestKafkaStreamsBuilder implements KafkaStreamsBuilder {
        private final ServiceContext serviceContext;

        private TestKafkaStreamsBuilder(final ServiceContext serviceContext) {
            this.serviceContext = serviceContext;
        }

        private static class Call {
            private final Properties props;

            private Call(final Properties props) {
                this.props = props;
            }
        }

        private final List<PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call> calls = new LinkedList<>();

        @Override
        public KafkaStreams buildKafkaStreams(final StreamsBuilder builder, final Map<String, Object> conf) {
            final Properties props = new Properties();
            props.putAll(conf);
            final KafkaStreams kafkaStreams = new KafkaStreams(builder.build(), props, serviceContext.getKafkaClientSupplier());
            calls.add(new PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call(props));
            return kafkaStreams;
        }

        List<PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call> getCalls() {
            return calls;
        }
    }

    private PhysicalPlanBuilderTest.TestKafkaStreamsBuilder testKafkaStreamsBuilder;

    @Test
    public void shouldHaveKStreamDataSource() {
        final QueryMetadata metadata = buildPhysicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter);
        MatcherAssert.assertThat(metadata.getDataSourceType(), CoreMatchers.equalTo(KSTREAM));
    }

    @Test
    public void shouldHaveOutputNode() {
        final QueryMetadata queryMetadata = buildPhysicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter);
        MatcherAssert.assertThat(queryMetadata.getOutputNode(), IsInstanceOf.instanceOf(KsqlBareOutputNode.class));
    }

    @Test
    public void shouldCreateExecutionPlan() {
        final String queryString = "SELECT col0, sum(col3), count(col3) FROM test1 " + "WHERE col0 > 100 GROUP BY col0;";
        final QueryMetadata metadata = buildPhysicalPlan(queryString);
        final String planText = metadata.getExecutionPlan();
        final String[] lines = planText.split("\n");
        MatcherAssert.assertThat(lines[0], Matchers.startsWith(" > [ SINK ] | Schema: [COL0 : BIGINT, KSQL_COL_1 : DOUBLE, KSQL_COL_2 : BIGINT] |"));
        MatcherAssert.assertThat(lines[1], Matchers.startsWith(("\t\t > [ AGGREGATE ] | Schema: [KSQL_INTERNAL_COL_0 : BIGINT, " + ("KSQL_INTERNAL_COL_1 : DOUBLE, KSQL_AGG_VARIABLE_0 : DOUBLE, " + "KSQL_AGG_VARIABLE_1 : BIGINT] |"))));
        MatcherAssert.assertThat(lines[2], Matchers.startsWith(("\t\t\t\t > [ PROJECT ] | Schema: [KSQL_INTERNAL_COL_0 : BIGINT, " + "KSQL_INTERNAL_COL_1 : DOUBLE] |")));
        MatcherAssert.assertThat(lines[3], Matchers.startsWith(("\t\t\t\t\t\t > [ FILTER ] | Schema: [TEST1.ROWTIME : BIGINT, TEST1.ROWKEY : BIGINT, " + (("TEST1.COL0 : BIGINT, TEST1.COL1 : VARCHAR, TEST1.COL2 : VARCHAR, " + "TEST1.COL3 : DOUBLE, TEST1.COL4 : ARRAY<DOUBLE>, ") + "TEST1.COL5 : MAP<VARCHAR,DOUBLE>] |"))));
        MatcherAssert.assertThat(lines[4], Matchers.startsWith(("\t\t\t\t\t\t\t\t > [ SOURCE ] | Schema: [TEST1.ROWTIME : BIGINT, TEST1.ROWKEY : BIGINT, " + (("TEST1.COL0 : BIGINT, TEST1.COL1 : VARCHAR, TEST1.COL2 : VARCHAR, " + "TEST1.COL3 : DOUBLE, TEST1.COL4 : ARRAY<DOUBLE>, ") + "TEST1.COL5 : MAP<VARCHAR,DOUBLE>] |"))));
    }

    @Test
    public void shouldCreateExecutionPlanForInsert() {
        final String csasQuery = "CREATE STREAM s1 WITH (value_format = 'delimited') AS SELECT col0, col1, " + ("col2 FROM " + "test1;");
        final String insertIntoQuery = "INSERT INTO s1 SELECT col0, col1, col2 FROM test1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        final List<QueryMetadata> queryMetadataList = KsqlEngineTestUtil.execute(ksqlEngine, (((((PhysicalPlanBuilderTest.createStream) + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
        Assert.assertTrue(((queryMetadataList.size()) == 2));
        final String planText = queryMetadataList.get(1).getExecutionPlan();
        final String[] lines = planText.split("\n");
        Assert.assertTrue(((lines.length) == 3));
        Assert.assertEquals(lines[0], " > [ SINK ] | Schema: [COL0 : BIGINT, COL1 : VARCHAR, COL2 : DOUBLE] | Logger: InsertQuery_1.S1");
        Assert.assertEquals(lines[1], "\t\t > [ PROJECT ] | Schema: [COL0 : BIGINT, COL1 : VARCHAR, COL2 : DOUBLE] | Logger: InsertQuery_1.Project");
        Assert.assertEquals(lines[2], "\t\t\t\t > [ SOURCE ] | Schema: [TEST1.ROWTIME : BIGINT, TEST1.ROWKEY : VARCHAR, TEST1.COL0 : BIGINT, TEST1.COL1 : VARCHAR, TEST1.COL2 : DOUBLE] | Logger: InsertQuery_1.KsqlTopic");
        MatcherAssert.assertThat(queryMetadataList.get(1).getOutputNode(), IsInstanceOf.instanceOf(KsqlStructuredDataOutputNode.class));
        final KsqlStructuredDataOutputNode ksqlStructuredDataOutputNode = ((KsqlStructuredDataOutputNode) (queryMetadataList.get(1).getOutputNode()));
        MatcherAssert.assertThat(ksqlStructuredDataOutputNode.getKsqlTopic().getKsqlTopicSerDe().getSerDe(), CoreMatchers.equalTo(DELIMITED));
        PhysicalPlanBuilderTest.closeQueries(queryMetadataList);
    }

    @Test
    public void shouldFailIfInsertSinkDoesNotExist() {
        // Given:
        final String insertIntoQuery = "INSERT INTO s1 SELECT col0, col1, col2 FROM test1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("INSERT INTO s1 SELECT col0, col1, col2 FROM test1;")));
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("Sink does not exist for the INSERT INTO statement: S1")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, (((PhysicalPlanBuilderTest.createStream) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldFailInsertIfTheResultSchemaDoesNotMatch() {
        // Given:
        final String csasQuery = "CREATE STREAM s1 AS SELECT col0, col1 FROM test1;";
        final String insertIntoQuery = "INSERT INTO s1 SELECT col0, col1, col2 FROM test1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Incompatible schema between results and sink. Result schema is " + ("[COL0 : BIGINT, COL1 : VARCHAR, COL2 : DOUBLE], " + "but the sink schema is [COL0 : BIGINT, COL1 : VARCHAR].")))));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, (((((PhysicalPlanBuilderTest.createStream) + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldThrowOnInsertIntoTableFromTable() {
        // Given:
        final String createTable = "CREATE TABLE T1 (COL0 BIGINT, COL1 VARCHAR, COL2 DOUBLE, COL3 " + (("DOUBLE) " + "WITH ( ") + "KAFKA_TOPIC = 'test1', VALUE_FORMAT = 'JSON', KEY = 'COL1' );");
        final String csasQuery = "CREATE TABLE T2 AS SELECT * FROM T1;";
        final String insertIntoQuery = "INSERT INTO T2 SELECT *  FROM T1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)));
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("INSERT INTO can only be used to insert into a stream. T2 is a table.")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, ((((createTable + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldCreatePlanForInsertIntoStreamFromStream() {
        // Given:
        final String cs = "CREATE STREAM test1 (col0 INT) " + "WITH (KAFKA_TOPIC='test1', VALUE_FORMAT='JSON');";
        final String csas = "CREATE STREAM s0 AS SELECT * FROM test1;";
        final String insertInto = "INSERT INTO s0 SELECT * FROM test1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)));
        // When:
        final List<QueryMetadata> queries = KsqlEngineTestUtil.execute(ksqlEngine, ((cs + csas) + insertInto), ksqlConfig, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(queries, Matchers.hasSize(2));
        final String planText = queries.get(1).getExecutionPlan();
        final String[] lines = planText.split("\n");
        MatcherAssert.assertThat(lines.length, CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(lines[0], CoreMatchers.containsString(("> [ SINK ] | " + "Schema: [ROWTIME : BIGINT, ROWKEY : VARCHAR, COL0 : INT]")));
        MatcherAssert.assertThat(lines[1], CoreMatchers.containsString(("> [ PROJECT ] | " + "Schema: [ROWTIME : BIGINT, ROWKEY : VARCHAR, COL0 : INT]")));
        MatcherAssert.assertThat(lines[2], CoreMatchers.containsString(("> [ SOURCE ] | " + "Schema: [TEST1.ROWTIME : BIGINT, TEST1.ROWKEY : VARCHAR, TEST1.COL0 : INT]")));
        PhysicalPlanBuilderTest.closeQueries(queries);
    }

    @Test
    public void shouldFailInsertIfTheResultTypesDoNotMatch() {
        // Given:
        final String createTable = "CREATE TABLE T1 (COL0 BIGINT, COL1 VARCHAR, COL2 DOUBLE, COL3 " + (("DOUBLE) " + "WITH ( ") + "KAFKA_TOPIC = 't1', VALUE_FORMAT = 'JSON', KEY = 'COL1' );");
        final String csasQuery = "CREATE STREAM S2 AS SELECT * FROM TEST1;";
        final String insertIntoQuery = "INSERT INTO S2 SELECT col0, col1, col2, col3 FROM T1;";
        // No need for setting the correct clean up policy in test.
        kafkaTopicClient.createTopic("t1", 1, ((short) (1)), Collections.emptyMap());
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Incompatible data sink and query result. " + "Data sink (S2) type is KTABLE but select query result is KSTREAM."))));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, ((((((createTable + "\n ") + (PhysicalPlanBuilderTest.createStream)) + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldCheckSinkAndResultKeysDoNotMatch() {
        final String csasQuery = "CREATE STREAM s1 AS SELECT col0, col1, col2 FROM test1 PARTITION BY col0;";
        final String insertIntoQuery = "INSERT INTO s1 SELECT col0, col1, col2 FROM test1 PARTITION BY col0;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        final List<QueryMetadata> queryMetadataList = KsqlEngineTestUtil.execute(ksqlEngine, (((((PhysicalPlanBuilderTest.createStream) + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
        Assert.assertTrue(((queryMetadataList.size()) == 2));
        final String planText = queryMetadataList.get(1).getExecutionPlan();
        final String[] lines = planText.split("\n");
        MatcherAssert.assertThat(lines.length, CoreMatchers.equalTo(4));
        MatcherAssert.assertThat(lines[0], CoreMatchers.equalTo((" > [ REKEY ] | Schema: [COL0 : BIGINT, COL1 : VARCHAR, COL2 : DOUBLE] " + "| Logger: InsertQuery_1.S1")));
        MatcherAssert.assertThat(lines[1], CoreMatchers.equalTo(("\t\t > [ SINK ] | Schema: [COL0 : BIGINT, COL1 : VARCHAR, COL2 " + ": DOUBLE] | Logger: InsertQuery_1.S1")));
        MatcherAssert.assertThat(lines[2], CoreMatchers.equalTo(("\t\t\t\t > [ PROJECT ] | Schema: [COL0 : BIGINT, COL1 : VARCHAR" + ", COL2 : DOUBLE] | Logger: InsertQuery_1.Project")));
        PhysicalPlanBuilderTest.closeQueries(queryMetadataList);
    }

    @Test
    public void shouldFailIfSinkAndResultKeysDoNotMatch() {
        final String csasQuery = "CREATE STREAM s1 AS SELECT col0, col1, col2 FROM test1 PARTITION BY col0;";
        final String insertIntoQuery = "INSERT INTO s1 SELECT col0, col1, col2 FROM test1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Incompatible key fields for sink and results. " + ("Sink key field is COL0 (type: Schema{INT64}) " + "while result key field is null (type: null)")))));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, (((((PhysicalPlanBuilderTest.createStream) + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldAddMetricsInterceptors() throws Exception {
        buildPhysicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter);
        final List<PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call> calls = testKafkaStreamsBuilder.getCalls();
        Assert.assertEquals(1, calls.size());
        final Properties props = calls.get(0).props;
        Object val = props.get(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        final List<String> consumerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(consumerInterceptors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(ConsumerCollector.class, CoreMatchers.equalTo(Class.forName(consumerInterceptors.get(0))));
        val = props.get(StreamsConfig.producerPrefix(INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        final List<String> producerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(producerInterceptors.size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(ProducerCollector.class, CoreMatchers.equalTo(Class.forName(producerInterceptors.get(0))));
    }

    @Test
    public void shouldUseOptimizationConfigProvidedWhenOn() {
        shouldUseProvidedOptimizationConfig(OPTIMIZE);
    }

    @Test
    public void shouldUseOptimizationConfigProvidedWhenOff() {
        shouldUseProvidedOptimizationConfig(NO_OPTIMIZATION);
    }

    public static class DummyConsumerInterceptor implements ConsumerInterceptor {
        public ConsumerRecords onConsume(final ConsumerRecords consumerRecords) {
            return consumerRecords;
        }

        public void close() {
        }

        public void onCommit(final Map map) {
        }

        public void configure(final Map<String, ?> map) {
        }
    }

    public static class DummyProducerInterceptor implements ProducerInterceptor {
        public void onAcknowledgement(final RecordMetadata rm, final Exception e) {
        }

        public ProducerRecord onSend(final ProducerRecord producerRecords) {
            return producerRecords;
        }

        public void close() {
        }

        public void configure(final Map<String, ?> map) {
        }
    }

    @Test
    public void shouldAddMetricsInterceptorsToExistingList() throws Exception {
        // Initialize override properties with lists for producer/consumer interceptors
        final Map<String, Object> overrideProperties = new HashMap<>();
        List<String> consumerInterceptors = new LinkedList<>();
        consumerInterceptors.add(PhysicalPlanBuilderTest.DummyConsumerInterceptor.class.getName());
        overrideProperties.put(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG), consumerInterceptors);
        List<String> producerInterceptors = new LinkedList<>();
        producerInterceptors.add(PhysicalPlanBuilderTest.DummyProducerInterceptor.class.getName());
        overrideProperties.put(StreamsConfig.producerPrefix(INTERCEPTOR_CLASSES_CONFIG), producerInterceptors);
        physicalPlanBuilder = buildPhysicalPlanBuilder(overrideProperties);
        buildPhysicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter);
        final List<PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call> calls = testKafkaStreamsBuilder.getCalls();
        Assert.assertEquals(1, calls.size());
        final Properties props = calls.get(0).props;
        Object val = props.get(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        consumerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(consumerInterceptors.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(PhysicalPlanBuilderTest.DummyConsumerInterceptor.class.getName(), CoreMatchers.equalTo(consumerInterceptors.get(0)));
        MatcherAssert.assertThat(ConsumerCollector.class, CoreMatchers.equalTo(Class.forName(consumerInterceptors.get(1))));
        val = props.get(StreamsConfig.producerPrefix(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        producerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(producerInterceptors.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(PhysicalPlanBuilderTest.DummyProducerInterceptor.class.getName(), CoreMatchers.equalTo(producerInterceptors.get(0)));
        MatcherAssert.assertThat(ProducerCollector.class, CoreMatchers.equalTo(Class.forName(producerInterceptors.get(1))));
    }

    @Test
    public void shouldAddMetricsInterceptorsToExistingString() throws Exception {
        // Initialize override properties with class name strings for producer/consumer interceptors
        final Map<String, Object> overrideProperties = new HashMap<>();
        overrideProperties.put(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG), PhysicalPlanBuilderTest.DummyConsumerInterceptor.class.getName());
        overrideProperties.put(StreamsConfig.producerPrefix(INTERCEPTOR_CLASSES_CONFIG), PhysicalPlanBuilderTest.DummyProducerInterceptor.class.getName());
        physicalPlanBuilder = buildPhysicalPlanBuilder(overrideProperties);
        buildPhysicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter);
        final List<PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call> calls = testKafkaStreamsBuilder.getCalls();
        MatcherAssert.assertThat(calls.size(), CoreMatchers.equalTo(1));
        final Properties props = calls.get(0).props;
        Object val = props.get(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        final List<String> consumerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(consumerInterceptors.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(PhysicalPlanBuilderTest.DummyConsumerInterceptor.class.getName(), CoreMatchers.equalTo(consumerInterceptors.get(0)));
        MatcherAssert.assertThat(ConsumerCollector.class, CoreMatchers.equalTo(Class.forName(consumerInterceptors.get(1))));
        val = props.get(StreamsConfig.producerPrefix(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        final List<String> producerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(producerInterceptors.size(), CoreMatchers.equalTo(2));
        MatcherAssert.assertThat(PhysicalPlanBuilderTest.DummyProducerInterceptor.class.getName(), CoreMatchers.equalTo(producerInterceptors.get(0)));
        MatcherAssert.assertThat(ProducerCollector.class, CoreMatchers.equalTo(Class.forName(producerInterceptors.get(1))));
    }

    public static class DummyConsumerInterceptor2 implements ConsumerInterceptor {
        public ConsumerRecords onConsume(final ConsumerRecords consumerRecords) {
            return consumerRecords;
        }

        public void close() {
        }

        public void onCommit(final Map map) {
        }

        public void configure(final Map<String, ?> map) {
        }
    }

    @Test
    public void shouldConfigureProducerErrorHandlerLogger() {
        // Given:
        processingLogContext = Mockito.mock(ProcessingLogContext.class);
        final ProcessingLoggerFactory loggerFactory = Mockito.mock(ProcessingLoggerFactory.class);
        final ProcessingLogger logger = Mockito.mock(ProcessingLogger.class);
        Mockito.when(processingLogContext.getLoggerFactory()).thenReturn(loggerFactory);
        final OutputNode spyNode = Mockito.spy(((OutputNode) (AnalysisTestUtil.buildLogicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter, metaStore))));
        Mockito.doReturn(new QueryId("foo")).when(spyNode).getQueryId(ArgumentMatchers.any());
        Mockito.when(loggerFactory.getLogger("foo")).thenReturn(logger);
        Mockito.when(loggerFactory.getLogger(ArgumentMatchers.startsWith("foo."))).thenReturn(Mockito.mock(ProcessingLogger.class));
        physicalPlanBuilder = buildPhysicalPlanBuilder(Collections.emptyMap());
        // When:
        physicalPlanBuilder.buildPhysicalPlan(new io.confluent.ksql.planner.LogicalPlanNode(PhysicalPlanBuilderTest.simpleSelectFilter, spyNode));
        // Then:
        final PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call call = testKafkaStreamsBuilder.calls.get(0);
        MatcherAssert.assertThat(call.props.get(KSQL_PRODUCTION_ERROR_LOGGER), Matchers.is(logger));
    }

    @Test
    public void shouldAddMetricsInterceptorsToExistingStringList() throws Exception {
        // Initialize override properties with class name strings for producer/consumer interceptors
        final Map<String, Object> overrideProperties = new HashMap<>();
        final String consumerInterceptorStr = ((PhysicalPlanBuilderTest.DummyConsumerInterceptor.class.getName()) + " , ") + (PhysicalPlanBuilderTest.DummyConsumerInterceptor2.class.getName());
        overrideProperties.put(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG), consumerInterceptorStr);
        physicalPlanBuilder = buildPhysicalPlanBuilder(overrideProperties);
        buildPhysicalPlan(PhysicalPlanBuilderTest.simpleSelectFilter);
        final List<PhysicalPlanBuilderTest.TestKafkaStreamsBuilder.Call> calls = testKafkaStreamsBuilder.getCalls();
        Assert.assertEquals(1, calls.size());
        final Properties props = calls.get(0).props;
        final Object val = props.get(StreamsConfig.consumerPrefix(INTERCEPTOR_CLASSES_CONFIG));
        Assert.assertThat(val, IsInstanceOf.instanceOf(List.class));
        final List<String> consumerInterceptors = ((List<String>) (val));
        MatcherAssert.assertThat(consumerInterceptors.size(), CoreMatchers.equalTo(3));
        MatcherAssert.assertThat(PhysicalPlanBuilderTest.DummyConsumerInterceptor.class.getName(), CoreMatchers.equalTo(consumerInterceptors.get(0)));
        MatcherAssert.assertThat(PhysicalPlanBuilderTest.DummyConsumerInterceptor2.class.getName(), CoreMatchers.equalTo(consumerInterceptors.get(1)));
        MatcherAssert.assertThat(ConsumerCollector.class, CoreMatchers.equalTo(Class.forName(consumerInterceptors.get(2))));
    }

    @Test
    public void shouldCreateExpectedServiceId() {
        final String serviceId = physicalPlanBuilder.getServiceId();
        MatcherAssert.assertThat(serviceId, CoreMatchers.equalTo(((KsqlConstants.KSQL_INTERNAL_TOPIC_PREFIX) + (KsqlConfig.KSQL_SERVICE_ID_DEFAULT))));
    }

    @Test
    public void shouldHaveOptionalFieldsInResultSchema() {
        final String csasQuery = "CREATE STREAM s1 WITH (value_format = 'delimited') AS SELECT col0, col1, " + ("col2 FROM " + "test1;");
        final String insertIntoQuery = "INSERT INTO s1 SELECT col0, col1, col2 FROM test1;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        final List<QueryMetadata> queryMetadataList = KsqlEngineTestUtil.execute(ksqlEngine, (((((PhysicalPlanBuilderTest.createStream) + "\n ") + csasQuery) + "\n ") + insertIntoQuery), ksqlConfig, Collections.emptyMap());
        final Schema resultSchema = queryMetadataList.get(0).getOutputNode().getSchema();
        resultSchema.fields().forEach(( field) -> Assert.assertTrue(field.schema().isOptional()));
        PhysicalPlanBuilderTest.closeQueries(queryMetadataList);
    }

    @Test
    public void shouldSetIsKSQLSinkInMetastoreCorrectly() {
        final String csasQuery = "CREATE STREAM s1 AS SELECT col0, col1, col2 FROM test1;";
        final String ctasQuery = "CREATE TABLE t1 AS SELECT col0, COUNT(*) FROM test1 GROUP BY col0;";
        kafkaTopicClient.createTopic("test1", 1, ((short) (1)), Collections.emptyMap());
        KsqlEngineTestUtil.execute(ksqlEngine, (((((PhysicalPlanBuilderTest.createStream) + "\n ") + csasQuery) + "\n ") + ctasQuery), ksqlConfig, Collections.emptyMap());
        MatcherAssert.assertThat(ksqlEngine.getMetaStore().getSource("TEST1").getKsqlTopic().isKsqlSink(), CoreMatchers.equalTo(false));
        MatcherAssert.assertThat(ksqlEngine.getMetaStore().getSource("S1").getKsqlTopic().isKsqlSink(), CoreMatchers.equalTo(true));
        MatcherAssert.assertThat(ksqlEngine.getMetaStore().getSource("T1").getKsqlTopic().isKsqlSink(), CoreMatchers.equalTo(true));
    }
}

