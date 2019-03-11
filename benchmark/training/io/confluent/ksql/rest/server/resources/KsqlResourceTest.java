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
package io.confluent.ksql.rest.server.resources;


import ClusterTerminateRequest.DELETE_TOPIC_LIST_PROP;
import Code.BAD_REQUEST;
import Code.INTERNAL_SERVER_ERROR;
import Code.SERVICE_UNAVAILABLE;
import CommandStatus.Status.QUEUED;
import ConsumerConfig.FETCH_MIN_BYTES_CONFIG;
import DataSource.DataSourceType.KSTREAM;
import DataSource.DataSourceType.KTABLE;
import Errors.ERROR_CODE_BAD_REQUEST;
import Errors.ERROR_CODE_BAD_STATEMENT;
import Errors.ERROR_CODE_COMMAND_QUEUE_CATCHUP_TIMEOUT;
import Errors.ERROR_CODE_SERVER_ERROR;
import Errors.ERROR_CODE_SERVER_SHUTTING_DOWN;
import KsqlConfig.KSQL_ACTIVE_PERSISTENT_QUERY_LIMIT_CONFIG;
import KsqlConfig.KSQL_ENABLE_UDFS;
import KsqlConfig.KSQL_WINDOWED_SESSION_KEY_LEGACY_CONFIG;
import KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY;
import KsqlConfig.SSL_CONFIG_NAMES;
import KsqlConstants.LEGACY_RUN_SCRIPT_STATEMENTS_CONTENT;
import ProducerConfig.BUFFER_MEMORY_CONFIG;
import Schema.OPTIONAL_BOOLEAN_SCHEMA;
import Schema.OPTIONAL_STRING_SCHEMA;
import SqlType.STRING;
import StreamsConfig.STATE_CLEANUP_DELAY_MS_CONFIG;
import StreamsConfig.STATE_DIR_CONFIG;
import TerminateCluster.TERMINATE_CLUSTER_STATEMENT_TEXT;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.KsqlExecutionContext;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.metastore.MetaStoreImpl;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.ParserMatchers;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.CreateStreamAsSelect;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.parser.tree.TableElement;
import io.confluent.ksql.parser.tree.TerminateQuery;
import io.confluent.ksql.rest.entity.ClusterTerminateRequest;
import io.confluent.ksql.rest.entity.CommandStatusEntity;
import io.confluent.ksql.rest.entity.FunctionNameList;
import io.confluent.ksql.rest.entity.FunctionType;
import io.confluent.ksql.rest.entity.KsqlEntity;
import io.confluent.ksql.rest.entity.KsqlEntityList;
import io.confluent.ksql.rest.entity.KsqlErrorMessage;
import io.confluent.ksql.rest.entity.KsqlErrorMessageMatchers;
import io.confluent.ksql.rest.entity.KsqlRequest;
import io.confluent.ksql.rest.entity.KsqlStatementErrorMessage;
import io.confluent.ksql.rest.entity.KsqlStatementErrorMessageMatchers;
import io.confluent.ksql.rest.entity.KsqlTopicInfo;
import io.confluent.ksql.rest.entity.KsqlTopicsList;
import io.confluent.ksql.rest.entity.PropertiesList;
import io.confluent.ksql.rest.entity.Queries;
import io.confluent.ksql.rest.entity.QueryDescription;
import io.confluent.ksql.rest.entity.QueryDescriptionEntity;
import io.confluent.ksql.rest.entity.QueryDescriptionList;
import io.confluent.ksql.rest.entity.RunningQuery;
import io.confluent.ksql.rest.entity.SourceDescription;
import io.confluent.ksql.rest.entity.SourceDescriptionEntity;
import io.confluent.ksql.rest.entity.SourceDescriptionList;
import io.confluent.ksql.rest.entity.StreamsList;
import io.confluent.ksql.rest.entity.TablesList;
import io.confluent.ksql.rest.server.KsqlRestConfig;
import io.confluent.ksql.rest.server.computation.CommandStore;
import io.confluent.ksql.rest.server.computation.QueuedCommandStatus;
import io.confluent.ksql.schema.inference.SchemaInjector;
import io.confluent.ksql.services.FakeKafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.test.util.KsqlIdentifierTestUtil;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.KsqlStatementException;
import io.confluent.ksql.util.PersistentQueryMetadata;
import io.confluent.ksql.version.metrics.ActivenessRegistrar;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.SchemaBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings({ "unchecked", "SameParameterValue" })
@RunWith(MockitoJUnitRunner.class)
public class KsqlResourceTest {
    private static final long STATE_CLEANUP_DELAY_MS_DEFAULT = (10 * 60) * 1000L;

    private static final int FETCH_MIN_BYTES_DEFAULT = 1;

    private static final long BUFFER_MEMORY_DEFAULT = (32 * 1024) * 1024L;

    private static final Duration DISTRIBUTED_COMMAND_RESPONSE_TIMEOUT = Duration.ofMillis(1000);

    private static final KsqlRequest VALID_EXECUTABLE_REQUEST = new KsqlRequest("CREATE STREAM S AS SELECT * FROM test_stream;", ImmutableMap.of(KSQL_WINDOWED_SESSION_KEY_LEGACY_CONFIG, true), 0L);

    private static final Schema SINGLE_FIELD_SCHEMA = SchemaBuilder.struct().field("val", OPTIONAL_STRING_SCHEMA);

    private static final ClusterTerminateRequest VALID_TERMINATE_REQUEST = new ClusterTerminateRequest(ImmutableList.of("Foo"));

    private static final List<TableElement> SOME_ELEMENTS = ImmutableList.of(new TableElement("f0", PrimitiveType.of(STRING)));

    private static final PreparedStatement<CreateStream> STMT_0_WITH_SCHEMA = PreparedStatement.of("sql with schema", new CreateStream(QualifiedName.of("bob"), KsqlResourceTest.SOME_ELEMENTS, true, ImmutableMap.of("KAFKA_TOPIC", new StringLiteral("orders-topic"), "VALUE_FORMAT", new StringLiteral("avro"))));

    private static final PreparedStatement<CreateStream> STMT_1_WITH_SCHEMA = PreparedStatement.of("other sql with schema", new CreateStream(QualifiedName.of("john"), KsqlResourceTest.SOME_ELEMENTS, true, ImmutableMap.of("KAFKA_TOPIC", new StringLiteral("orders-topic"), "VALUE_FORMAT", new StringLiteral("avro"))));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private KsqlConfig ksqlConfig;

    private KsqlRestConfig ksqlRestConfig;

    private FakeKafkaTopicClient kafkaTopicClient;

    private KsqlEngine realEngine;

    private KsqlEngine ksqlEngine;

    @Mock
    private KsqlExecutionContext sandbox;

    @Mock
    private CommandStore commandStore;

    @Mock
    private ActivenessRegistrar activenessRegistrar;

    @Mock
    private Function<ServiceContext, SchemaInjector> schemaInjectorFactory;

    @Mock
    private SchemaInjector schemaInjector;

    @Mock
    private SchemaInjector sandboxSchemaInjector;

    private KsqlResource ksqlResource;

    private SchemaRegistryClient schemaRegistryClient;

    private QueuedCommandStatus commandStatus;

    private QueuedCommandStatus commandStatus1;

    private MetaStoreImpl metaStore;

    private ServiceContext serviceContext;

    private String streamName;

    @Test
    public void shouldInstantRegisterTopic() {
        // When:
        final CommandStatusEntity result = makeSingleRequest("REGISTER TOPIC FOO WITH (kafka_topic='bar', value_format='json');", CommandStatusEntity.class);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(new CommandStatusEntity("REGISTER TOPIC FOO WITH (kafka_topic='bar', value_format='json');", commandStatus.getCommandId(), commandStatus.getStatus(), 0L)));
    }

    @Test
    public void shouldListRegisteredTopics() {
        // When:
        final KsqlTopicsList ksqlTopicsList = makeSingleRequest("LIST REGISTERED TOPICS;", KsqlTopicsList.class);
        // Then:
        final Collection<KsqlTopicInfo> expectedTopics = ksqlEngine.getMetaStore().getAllKsqlTopics().values().stream().map(KsqlTopicInfo::new).collect(Collectors.toList());
        MatcherAssert.assertThat(ksqlTopicsList.getTopics(), Matchers.is(expectedTopics));
    }

    @Test
    public void shouldShowNoQueries() {
        // When:
        final Queries queries = makeSingleRequest("SHOW QUERIES;", Queries.class);
        // Then:
        MatcherAssert.assertThat(queries.getQueries(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldListFunctions() {
        // When:
        final FunctionNameList functionList = makeSingleRequest("LIST FUNCTIONS;", FunctionNameList.class);
        // Then:
        MatcherAssert.assertThat(functionList.getFunctions(), CoreMatchers.hasItems(new io.confluent.ksql.rest.entity.SimpleFunctionInfo("EXTRACTJSONFIELD", FunctionType.scalar), new io.confluent.ksql.rest.entity.SimpleFunctionInfo("ARRAYCONTAINS", FunctionType.scalar), new io.confluent.ksql.rest.entity.SimpleFunctionInfo("CONCAT", FunctionType.scalar), new io.confluent.ksql.rest.entity.SimpleFunctionInfo("TOPK", FunctionType.aggregate), new io.confluent.ksql.rest.entity.SimpleFunctionInfo("MAX", FunctionType.aggregate)));
        MatcherAssert.assertThat("shouldn't contain internal functions", functionList.getFunctions(), Matchers.not(CoreMatchers.hasItem(new io.confluent.ksql.rest.entity.SimpleFunctionInfo("FETCH_FIELD_FROM_STRUCT", FunctionType.scalar))));
    }

    @Test
    public void shouldShowStreamsExtended() {
        // Given:
        final Schema schema = SchemaBuilder.struct().field("FIELD1", OPTIONAL_BOOLEAN_SCHEMA).field("FIELD2", OPTIONAL_STRING_SCHEMA);
        givenSource(KSTREAM, "new_stream", "new_topic", "new_ksql_topic", schema);
        // When:
        final SourceDescriptionList descriptionList = makeSingleRequest("SHOW STREAMS EXTENDED;", SourceDescriptionList.class);
        // Then:
        MatcherAssert.assertThat(descriptionList.getSourceDescriptions(), Matchers.containsInAnyOrder(new SourceDescription(ksqlEngine.getMetaStore().getSource("TEST_STREAM"), true, "JSON", Collections.emptyList(), Collections.emptyList(), kafkaTopicClient), new SourceDescription(ksqlEngine.getMetaStore().getSource("new_stream"), true, "JSON", Collections.emptyList(), Collections.emptyList(), kafkaTopicClient)));
    }

    @Test
    public void shouldShowTablesExtended() {
        // Given:
        final Schema schema = SchemaBuilder.struct().field("FIELD1", OPTIONAL_BOOLEAN_SCHEMA).field("FIELD2", OPTIONAL_STRING_SCHEMA);
        givenSource(KTABLE, "new_table", "new_topic", "new_ksql_topic", schema);
        // When:
        final SourceDescriptionList descriptionList = makeSingleRequest("SHOW TABLES EXTENDED;", SourceDescriptionList.class);
        // Then:
        MatcherAssert.assertThat(descriptionList.getSourceDescriptions(), Matchers.containsInAnyOrder(new SourceDescription(ksqlEngine.getMetaStore().getSource("TEST_TABLE"), true, "JSON", Collections.emptyList(), Collections.emptyList(), kafkaTopicClient), new SourceDescription(ksqlEngine.getMetaStore().getSource("new_table"), true, "JSON", Collections.emptyList(), Collections.emptyList(), kafkaTopicClient)));
    }

    @Test
    public void shouldShowQueriesExtended() {
        // Given:
        final Map<String, Object> overriddenProperties = Collections.singletonMap("ksql.streams.auto.offset.reset", "earliest");
        final List<PersistentQueryMetadata> queryMetadata = createQueries(("CREATE STREAM test_describe_1 AS SELECT * FROM test_stream;" + "CREATE STREAM test_describe_2 AS SELECT * FROM test_stream;"), overriddenProperties);
        // When:
        final QueryDescriptionList descriptionList = makeSingleRequest("SHOW QUERIES EXTENDED;", QueryDescriptionList.class);
        // Then:
        MatcherAssert.assertThat(descriptionList.getQueryDescriptions(), Matchers.containsInAnyOrder(QueryDescription.forQueryMetadata(queryMetadata.get(0)), QueryDescription.forQueryMetadata(queryMetadata.get(1))));
    }

    @Test
    public void shouldDescribeStatement() {
        // Given:
        final List<RunningQuery> queries = createRunningQueries(("CREATE STREAM described_stream AS SELECT * FROM test_stream;" + "CREATE STREAM down_stream AS SELECT * FROM described_stream;"), Collections.emptyMap());
        // When:
        final SourceDescriptionEntity description = makeSingleRequest("DESCRIBE DESCRIBED_STREAM;", SourceDescriptionEntity.class);
        // Then:
        final SourceDescription expectedDescription = new SourceDescription(ksqlEngine.getMetaStore().getSource("DESCRIBED_STREAM"), false, "JSON", Collections.singletonList(queries.get(1)), Collections.singletonList(queries.get(0)), null);
        MatcherAssert.assertThat(description.getSourceDescription(), Matchers.is(expectedDescription));
    }

    @Test
    public void shouldListStreamsStatement() {
        // When:
        final StreamsList streamsList = makeSingleRequest("LIST STREAMS;", StreamsList.class);
        // Then:
        MatcherAssert.assertThat(streamsList.getStreams(), Matchers.contains(sourceStream("TEST_STREAM")));
    }

    @Test
    public void shouldListTablesStatement() {
        // When:
        final TablesList tablesList = makeSingleRequest("LIST TABLES;", TablesList.class);
        // Then:
        MatcherAssert.assertThat(tablesList.getTables(), Matchers.contains(sourceTable("TEST_TABLE")));
    }

    @Test
    public void shouldFailForIncorrectCSASStatementResultType() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is(("Invalid result type. Your SELECT query produces a TABLE. " + "Please use CREATE TABLE AS SELECT statement instead.")))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(Matchers.is("CREATE STREAM s1 AS SELECT * FROM test_table;"))));
        // When:
        makeRequest("CREATE STREAM s1 AS SELECT * FROM test_table;");
    }

    @Test
    public void shouldFailForIncorrectCSASStatementResultTypeWithGroupBy() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is(("Invalid result type. Your SELECT query produces a TABLE. " + "Please use CREATE TABLE AS SELECT statement instead.")))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(Matchers.is("CREATE STREAM s2 AS SELECT S2_F1, count(S2_F1) FROM test_stream group by s2_f1;"))));
        // When:
        makeRequest("CREATE STREAM s2 AS SELECT S2_F1, count(S2_F1) FROM test_stream group by s2_f1;");
    }

    @Test
    public void shouldFailForIncorrectCTASStatementResultType() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is(("Invalid result type. Your SELECT query produces a STREAM. " + "Please use CREATE STREAM AS SELECT statement instead.")))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(Matchers.is("CREATE TABLE s1 AS SELECT * FROM test_stream;"))));
        // When:
        makeRequest("CREATE TABLE s1 AS SELECT * FROM test_stream;");
    }

    @Test
    public void shouldFailForIncorrectDropStreamStatement() {
        // When:
        final KsqlErrorMessage result = makeFailingRequest("DROP TABLE test_stream;", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getMessage().toLowerCase(), Matchers.is("incompatible data source type is stream, but statement was drop table"));
    }

    @Test
    public void shouldFailForIncorrectDropTableStatement() {
        // When:
        final KsqlErrorMessage result = makeFailingRequest("DROP STREAM test_table;", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getMessage().toLowerCase(), Matchers.is("incompatible data source type is table, but statement was drop stream"));
    }

    @Test
    public void shouldFailCreateTableWithInferenceWithUnknownKey() {
        // When:
        final KsqlErrorMessage response = makeFailingRequest(("CREATE TABLE orders WITH (KAFKA_TOPIC='orders-topic', " + "VALUE_FORMAT = 'avro', KEY = 'unknownField');"), BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(response, CoreMatchers.instanceOf(KsqlStatementErrorMessage.class));
        MatcherAssert.assertThat(response.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
    }

    @Test
    public void shouldFailBareQuery() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("SELECT and PRINT queries must use the /query endpoint"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(Matchers.is("SELECT * FROM test_table;"))));
        // When:
        makeRequest("SELECT * FROM test_table;");
    }

    @Test
    public void shouldFailPrintTopic() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("SELECT and PRINT queries must use the /query endpoint"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(Matchers.is("PRINT 'orders-topic';"))));
        // When:
        makeRequest("PRINT 'orders-topic';");
    }

    @Test
    public void shouldDistributePersistentQuery() {
        // When:
        makeSingleRequest("CREATE STREAM S AS SELECT * FROM test_stream;", CommandStatusEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatement("CREATE STREAM S AS SELECT * FROM test_stream;", CreateStreamAsSelect.class))), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldDistributeWithConfig() {
        // When:
        makeSingleRequest(KsqlResourceTest.VALID_EXECUTABLE_REQUEST, KsqlEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.eq(ksqlConfig), ArgumentMatchers.eq(KsqlResourceTest.VALID_EXECUTABLE_REQUEST.getStreamsProperties()));
    }

    @Test
    public void shouldReturnStatusEntityFromPersistentQuery() {
        // When:
        final CommandStatusEntity result = makeSingleRequest("CREATE STREAM S AS SELECT * FROM test_stream;", CommandStatusEntity.class);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(new CommandStatusEntity("CREATE STREAM S AS SELECT * FROM test_stream;", commandStatus.getCommandId(), commandStatus.getStatus(), 0L)));
    }

    @Test
    public void shouldFailIfCreateStatementMissingKafkaTopicName() {
        // When:
        final KsqlErrorMessage result = makeFailingRequest("CREATE STREAM S (foo INT) WITH(VALUE_FORMAT='JSON');", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(CoreMatchers.instanceOf(KsqlStatementErrorMessage.class)));
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(result.getMessage(), Matchers.is("Corresponding Kafka topic (KAFKA_TOPIC) should be set in WITH clause."));
        MatcherAssert.assertThat(getStatementText(), Matchers.is("CREATE STREAM S (foo INT) WITH(VALUE_FORMAT='JSON');"));
    }

    @Test
    public void shouldReturnBadStatementIfStatementFailsValidation() {
        // When:
        final KsqlErrorMessage result = makeFailingRequest("DESCRIBE i_do_not_exist;", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result, Matchers.is(CoreMatchers.instanceOf(KsqlStatementErrorMessage.class)));
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(getStatementText(), Matchers.is("DESCRIBE i_do_not_exist;"));
    }

    @Test
    public void shouldNotDistributeCreateStatementIfTopicDoesNotExist() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("Kafka topic does not exist: unknown"))));
        // When:
        makeRequest("CREATE STREAM S (foo INT) WITH(VALUE_FORMAT='JSON', KAFKA_TOPIC='unknown');");
    }

    @Test
    public void shouldDistributeAvoCreateStatementWithColumns() {
        // When:
        makeSingleRequest("CREATE STREAM S (foo INT) WITH(VALUE_FORMAT='AVRO', KAFKA_TOPIC='orders-topic');", CommandStatusEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatement("CREATE STREAM S (foo INT) WITH(VALUE_FORMAT='AVRO', KAFKA_TOPIC='orders-topic');", CreateStream.class))), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldSupportSchemaInference() {
        // Given:
        givenMockEngine();
        final String sql = "CREATE STREAM NO_SCHEMA WITH(VALUE_FORMAT='AVRO', KAFKA_TOPIC='orders-topic');";
        Mockito.when(sandboxSchemaInjector.forStatement(MockitoHamcrest.argThat(ParserMatchers.preparedStatementText(sql)))).thenReturn(((PreparedStatement) (KsqlResourceTest.STMT_0_WITH_SCHEMA)));
        Mockito.when(schemaInjector.forStatement(MockitoHamcrest.argThat(ParserMatchers.preparedStatementText(sql)))).thenReturn(((PreparedStatement) (KsqlResourceTest.STMT_1_WITH_SCHEMA)));
        // When:
        makeRequest(sql);
        // Then:
        Mockito.verify(sandbox).execute(ArgumentMatchers.eq(KsqlResourceTest.STMT_0_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(commandStore).enqueueCommand(ArgumentMatchers.eq(KsqlResourceTest.STMT_1_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldFailWhenAvroInferenceFailsDuringValidate() {
        // Given:
        Mockito.when(sandboxSchemaInjector.forStatement(ArgumentMatchers.any())).thenThrow(new KsqlStatementException("boom", "sql"));
        // When:
        final KsqlErrorMessage result = makeFailingRequest("CREATE STREAM S WITH(VALUE_FORMAT='AVRO', KAFKA_TOPIC='orders-topic');", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(result.getMessage(), Matchers.is("boom"));
    }

    @Test
    public void shouldFailWhenAvroInferenceFailsDuringExecute() {
        // Given:
        Mockito.when(sandboxSchemaInjector.forStatement(ArgumentMatchers.any())).thenReturn(((PreparedStatement) (KsqlResourceTest.STMT_0_WITH_SCHEMA)));
        Mockito.when(schemaInjector.forStatement(ArgumentMatchers.any())).thenThrow(new KsqlStatementException("boom", "some-sql"));
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorCode(Matchers.is(ERROR_CODE_BAD_STATEMENT))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("boom"))));
        // When:
        makeRequest("CREATE STREAM S WITH(VALUE_FORMAT='AVRO', KAFKA_TOPIC='orders-topic');");
    }

    @Test
    public void shouldFailIfNoSchemaAndNotInferred() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorCode(Matchers.is(ERROR_CODE_BAD_STATEMENT))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("The statement does not define any columns."))));
        // When:
        makeRequest("CREATE STREAM S WITH(VALUE_FORMAT='AVRO', KAFKA_TOPIC='orders-topic');");
    }

    @Test
    public void shouldFailWhenAvroSchemaCanNotBeEvolved() {
        // Given:
        givenAvroSchemaNotEvolveable("S1");
        // When:
        final KsqlErrorMessage result = makeFailingRequest("CREATE STREAM S1 WITH(VALUE_FORMAT='AVRO') AS SELECT * FROM test_stream;", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Cannot register avro schema for S1 as the schema registry rejected it"));
    }

    @Test
    public void shouldWaitForLastDistributedStatementBeforeExecutingAnyNonDistributed() throws Exception {
        // Given:
        final String csasSql = "CREATE STREAM S AS SELECT * FROM test_stream;";
        Mockito.doAnswer(executeAgainstEngine(csasSql)).when(commandStore).ensureConsumedPast(commandStatus1.getCommandSequenceNumber(), KsqlResourceTest.DISTRIBUTED_COMMAND_RESPONSE_TIMEOUT);
        // When:
        final List<KsqlEntity> results = makeMultipleRequest((((csasSql + "\n")// <-- commandStatus
         + "CREATE STREAM S2 AS SELECT * FROM test_stream;\n")// <-- commandStatus1
         + "DESCRIBE S;"), KsqlEntity.class);
        // Then:
        Mockito.verify(commandStore).ensureConsumedPast(commandStatus1.getCommandSequenceNumber(), KsqlResourceTest.DISTRIBUTED_COMMAND_RESPONSE_TIMEOUT);
        MatcherAssert.assertThat(results, Matchers.hasSize(3));
        MatcherAssert.assertThat(results.get(2), Matchers.is(CoreMatchers.instanceOf(SourceDescriptionEntity.class)));
    }

    @Test
    public void shouldNotWaitOnAnyDistributedStatementsBeforeDistributingAnother() throws Exception {
        // When:
        makeMultipleRequest(("CREATE STREAM S AS SELECT * FROM test_stream;\n" + "CREATE STREAM S2 AS SELECT * FROM test_stream;"), KsqlEntity.class);
        // Then:
        Mockito.verify(commandStore, Mockito.never()).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    public void shouldNotWaitForLastDistributedStatementBeforeExecutingSyncBlackListedStatement() throws Exception {
        // Given:
        final ImmutableList<String> blackListed = // "LIST TOPICS;" <- mocks don't support required ops,
        ImmutableList.of("LIST FUNCTIONS;", "DESCRIBE FUNCTION LCASE;", "LIST PROPERTIES;", (("SET '" + (KsqlConfig.KSQL_SERVICE_ID_CONFIG)) + "'='FOO';"), (("UNSET '" + (KsqlConfig.KSQL_SERVICE_ID_CONFIG)) + "';"));
        for (final String statement : blackListed) {
            // When:
            makeMultipleRequest(((("CREATE STREAM " + (KsqlIdentifierTestUtil.uniqueIdentifierName())) + " AS SELECT * FROM test_stream;\n") + statement), KsqlEntity.class);
            // Then:
            Mockito.verify(commandStore, Mockito.never()).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        }
    }

    @Test
    public void shouldThrowShutdownIfInterruptedWhileAwaitingPreviousCmdInMultiStatementRequest() throws Exception {
        // Given:
        Mockito.doThrow(new InterruptedException("oh no!")).when(commandStore).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(SERVICE_UNAVAILABLE)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("The server is shutting down"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorCode(Matchers.is(ERROR_CODE_SERVER_SHUTTING_DOWN))));
        // When:
        makeMultipleRequest(("CREATE STREAM S AS SELECT * FROM test_stream;\n" + "DESCRIBE S;"), KsqlEntity.class);
    }

    @Test
    public void shouldThrowTimeoutOnTimeoutAwaitingPreviousCmdInMultiStatementRequest() throws Exception {
        // Given:
        Mockito.doThrow(new TimeoutException("oh no!")).when(commandStore).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(SERVICE_UNAVAILABLE)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString("Timed out"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString(("sequence number: " + (commandStatus.getCommandSequenceNumber()))))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorCode(Matchers.is(ERROR_CODE_COMMAND_QUEUE_CATCHUP_TIMEOUT))));
        // When:
        makeMultipleRequest(("CREATE STREAM S AS SELECT * FROM test_stream;\n" + "DESCRIBE S;"), KsqlEntity.class);
    }

    @Test
    public void shouldFailMultipleStatementsAtomically() {
        // When:
        // <-- duplicate will fail.
        makeFailingRequest(("CREATE STREAM S AS SELECT * FROM test_stream; " + ("CREATE STREAM S2 AS SELECT * FROM S;" + "CREATE STREAM S2 AS SELECT * FROM S;")), BAD_REQUEST);
        // Then:
        Mockito.verify(commandStore, Mockito.never()).enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldDistributeTerminateQuery() {
        // Given:
        final PersistentQueryMetadata queryMetadata = createQuery("CREATE STREAM test_explain AS SELECT * FROM test_stream;", Collections.emptyMap());
        final String terminateSql = ("TERMINATE " + (queryMetadata.getQueryId())) + ";";
        // When:
        final CommandStatusEntity result = makeSingleRequest(terminateSql, CommandStatusEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatement(terminateSql, TerminateQuery.class))), ArgumentMatchers.any(), ArgumentMatchers.any());
        MatcherAssert.assertThat(result.getStatementText(), Matchers.is(terminateSql));
    }

    @Test
    public void shouldThrowOnTerminateUnknownQuery() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("Unknown queryId: unknown_query_id"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(Matchers.is("TERMINATE unknown_query_id;"))));
        // When:
        makeRequest("TERMINATE unknown_query_id;");
    }

    @Test
    public void shouldThrowOnTerminateTerminatedQuery() {
        // Given:
        final String queryId = createQuery("CREATE STREAM test_explain AS SELECT * FROM test_stream;", Collections.emptyMap()).getQueryId().getId();
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString("Unknown queryId:"))));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatementErrorMessage(KsqlStatementErrorMessageMatchers.statement(CoreMatchers.containsString("TERMINATE /*second*/"))));
        // When:
        makeRequest(((((("TERMINATE " + queryId) + ";") + "TERMINATE /*second*/ ") + queryId) + ";"));
    }

    @Test
    public void shouldExplainQueryStatement() {
        // Given:
        final String ksqlQueryString = "SELECT * FROM test_stream;";
        final String ksqlString = "EXPLAIN " + ksqlQueryString;
        // When:
        final QueryDescriptionEntity query = makeSingleRequest(ksqlString, QueryDescriptionEntity.class);
        // Then:
        validateQueryDescription(ksqlQueryString, Collections.emptyMap(), query);
    }

    @Test
    public void shouldExplainCreateAsSelectStatement() {
        // Given:
        final String ksqlQueryString = "CREATE STREAM S3 AS SELECT * FROM test_stream;";
        final String ksqlString = "EXPLAIN " + ksqlQueryString;
        // When:
        final QueryDescriptionEntity query = makeSingleRequest(ksqlString, QueryDescriptionEntity.class);
        // Then:
        MatcherAssert.assertThat("Should not have registered the source", metaStore.getSource("S3"), Matchers.is(CoreMatchers.nullValue()));
        validateQueryDescription(ksqlQueryString, Collections.emptyMap(), query);
    }

    @Test
    public void shouldExplainQueryId() {
        // Given:
        final Map<String, Object> overriddenProperties = Collections.singletonMap("ksql.streams.auto.offset.reset", "earliest");
        final PersistentQueryMetadata queryMetadata = createQuery("CREATE STREAM test_explain AS SELECT * FROM test_stream;", overriddenProperties);
        // When:
        final QueryDescriptionEntity query = makeSingleRequest((("EXPLAIN " + (queryMetadata.getQueryId())) + ";"), QueryDescriptionEntity.class);
        // Then:
        validateQueryDescription(queryMetadata, overriddenProperties, query);
    }

    @Test
    public void shouldReportErrorOnNonQueryExplain() {
        // Given:
        final String ksqlQueryString = "SHOW TOPICS;";
        final String ksqlString = "EXPLAIN " + ksqlQueryString;
        // When:
        final KsqlErrorMessage result = makeFailingRequest(ksqlString, BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(result.getMessage(), Matchers.is("The provided statement does not run a ksql query"));
    }

    @Test
    public void shouldReturn5xxOnSystemError() {
        // Given:
        givenMockEngine();
        Mockito.when(ksqlEngine.parse(ArgumentMatchers.anyString())).thenThrow(new RuntimeException("internal error"));
        // When:
        final KsqlErrorMessage result = makeFailingRequest("CREATE STREAM test_explain AS SELECT * FROM test_stream;", INTERNAL_SERVER_ERROR);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_SERVER_ERROR));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("internal error"));
    }

    @Test
    public void shouldReturn5xxOnStatementSystemError() {
        // Given:
        final String ksqlString = "CREATE STREAM test_explain AS SELECT * FROM test_stream;";
        givenMockEngine();
        // when(sandbox.getMetaStore()).thenReturn(metaStore);
        Mockito.when(sandbox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RuntimeException("internal error"));
        // When:
        final KsqlErrorMessage result = makeFailingRequest(ksqlString, INTERNAL_SERVER_ERROR);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_SERVER_ERROR));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("internal error"));
    }

    @Test
    public void shouldSetProperty() {
        // Given:
        final String csas = ("CREATE STREAM " + (streamName)) + " AS SELECT * FROM test_stream;";
        // When:
        final List<CommandStatusEntity> results = makeMultipleRequest(((("SET '" + (KsqlConfig.KSQL_ENABLE_UDFS)) + "\' = \'false\';\n") + csas), CommandStatusEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatementText(csas))), ArgumentMatchers.any(), ArgumentMatchers.eq(ImmutableMap.of(KSQL_ENABLE_UDFS, "false")));
        MatcherAssert.assertThat(results, Matchers.hasSize(1));
        MatcherAssert.assertThat(getStatementText(), Matchers.is(csas));
    }

    @Test
    public void shouldFailSetPropertyOnInvalidPropertyName() {
        // When:
        final KsqlErrorMessage response = makeFailingRequest("SET 'ksql.unknown.property' = '1';", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(response, CoreMatchers.instanceOf(KsqlStatementErrorMessage.class));
        MatcherAssert.assertThat(response.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(response.getMessage(), CoreMatchers.containsString("Unknown property"));
    }

    @Test
    public void shouldFailSetPropertyOnInvalidPropertyValue() {
        // When:
        final KsqlErrorMessage response = makeFailingRequest((("SET '" + (KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY)) + "' = 'invalid value';"), BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(response, CoreMatchers.instanceOf(KsqlStatementErrorMessage.class));
        MatcherAssert.assertThat(response.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(response.getMessage(), CoreMatchers.containsString(("Invalid value invalid value for configuration ksql.sink.replicas: " + "Not a number of type SHORT")));
    }

    @Test
    public void shouldUnsetProperty() {
        // Given:
        final String csas = ("CREATE STREAM " + (streamName)) + " AS SELECT * FROM test_stream;";
        final Map<String, Object> localOverrides = ImmutableMap.of(SINK_NUMBER_OF_REPLICAS_PROPERTY, "2");
        // When:
        final CommandStatusEntity result = makeSingleRequest(new KsqlRequest(((("UNSET '" + (KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY)) + "\';\n") + csas), localOverrides, null), CommandStatusEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatementText(csas))), ArgumentMatchers.any(), ArgumentMatchers.eq(Collections.emptyMap()));
        MatcherAssert.assertThat(result.getStatementText(), Matchers.is(csas));
    }

    @Test
    public void shouldFailUnsetPropertyOnInvalidPropertyName() {
        // When:
        final KsqlErrorMessage response = makeFailingRequest("UNSET 'ksql.unknown.property';", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(response, CoreMatchers.instanceOf(KsqlStatementErrorMessage.class));
        MatcherAssert.assertThat(response.getErrorCode(), Matchers.is(ERROR_CODE_BAD_STATEMENT));
        MatcherAssert.assertThat(response.getMessage(), CoreMatchers.containsString("Unknown property"));
    }

    @Test
    public void shouldScopeSetPropertyToSingleRequest() {
        // given:
        final String csas = ("CREATE STREAM " + (streamName)) + " AS SELECT * FROM test_stream;";
        makeMultipleRequest((("SET '" + (KsqlConfig.SINK_NUMBER_OF_REPLICAS_PROPERTY)) + "' = '2';"), KsqlEntity.class);
        // When:
        makeSingleRequest(csas, KsqlEntity.class);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatementText(csas))), ArgumentMatchers.any(), ArgumentMatchers.eq(Collections.emptyMap()));
    }

    @Test
    public void shouldFailIfReachedActivePersistentQueriesLimit() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.of(KSQL_ACTIVE_PERSISTENT_QUERY_LIMIT_CONFIG, 3));
        givenMockEngine();
        givenPersistentQueryCount(3);
        // When:
        final KsqlErrorMessage result = makeFailingRequest("CREATE STREAM new_stream AS SELECT * FROM test_stream;", BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_REQUEST));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(("would cause the number of active, persistent queries " + "to exceed the configured limit")));
    }

    @Test
    public void shouldFailAllCommandsIfWouldReachActivePersistentQueriesLimit() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.of(KSQL_ACTIVE_PERSISTENT_QUERY_LIMIT_CONFIG, 3));
        final String ksqlString = "CREATE STREAM new_stream AS SELECT * FROM test_stream;" + "CREATE STREAM another_stream AS SELECT * FROM test_stream;";
        givenMockEngine();
        givenPersistentQueryCount(2);
        // When:
        final KsqlErrorMessage result = makeFailingRequest(ksqlString, BAD_REQUEST);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_BAD_REQUEST));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString(("would cause the number of active, persistent queries " + "to exceed the configured limit")));
        Mockito.verify(commandStore, Mockito.never()).enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldListPropertiesWithOverrides() {
        // Given:
        final Map<String, Object> overrides = Collections.singletonMap("auto.offset.reset", "latest");
        // When:
        final PropertiesList props = makeSingleRequest(new KsqlRequest("list properties;", overrides, null), PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getProperties().get("ksql.streams.auto.offset.reset"), Matchers.is("latest"));
        MatcherAssert.assertThat(props.getOverwrittenProperties(), CoreMatchers.hasItem("ksql.streams.auto.offset.reset"));
    }

    @Test
    public void shouldListPropertiesWithNoOverrides() {
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getOverwrittenProperties(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldListDefaultKsqlProperty() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put(STATE_DIR_CONFIG, "/tmp/kafka-streams").build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), CoreMatchers.hasItem(((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.STATE_DIR_CONFIG))));
    }

    @Test
    public void shouldListServerOverriddenKsqlProperty() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put(STATE_DIR_CONFIG, "/tmp/other").build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), Matchers.not(CoreMatchers.hasItem(CoreMatchers.containsString(STATE_DIR_CONFIG))));
    }

    @Test
    public void shouldListDefaultStreamProperty() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put(STATE_CLEANUP_DELAY_MS_CONFIG, KsqlResourceTest.STATE_CLEANUP_DELAY_MS_DEFAULT).build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), CoreMatchers.hasItem(((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.STATE_CLEANUP_DELAY_MS_CONFIG))));
    }

    @Test
    public void shouldListServerOverriddenStreamProperty() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put(STATE_CLEANUP_DELAY_MS_CONFIG, ((KsqlResourceTest.STATE_CLEANUP_DELAY_MS_DEFAULT) + 1)).build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), Matchers.not(CoreMatchers.hasItem(CoreMatchers.containsString(STATE_CLEANUP_DELAY_MS_CONFIG))));
    }

    @Test
    public void shouldListDefaultConsumerConfig() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), KsqlResourceTest.FETCH_MIN_BYTES_DEFAULT).build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), CoreMatchers.hasItem((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG))));
    }

    @Test
    public void shouldListServerOverriddenConsumerConfig() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.CONSUMER_PREFIX)) + (ConsumerConfig.FETCH_MIN_BYTES_CONFIG)), ((KsqlResourceTest.FETCH_MIN_BYTES_DEFAULT) + 1)).build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), Matchers.not(CoreMatchers.hasItem(CoreMatchers.containsString(FETCH_MIN_BYTES_CONFIG))));
    }

    @Test
    public void shouldListDefaultProducerConfig() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), KsqlResourceTest.BUFFER_MEMORY_DEFAULT).build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), CoreMatchers.hasItem((((KsqlConfig.KSQL_STREAMS_PREFIX) + (StreamsConfig.PRODUCER_PREFIX)) + (ProducerConfig.BUFFER_MEMORY_CONFIG))));
    }

    @Test
    public void shouldListServerOverriddenProducerConfig() {
        // Given:
        givenKsqlConfigWith(ImmutableMap.<String, Object>builder().put(((StreamsConfig.PRODUCER_PREFIX) + (ProducerConfig.BUFFER_MEMORY_CONFIG)), ((KsqlResourceTest.BUFFER_MEMORY_DEFAULT) + 1)).build());
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getDefaultProperties(), Matchers.not(CoreMatchers.hasItem(CoreMatchers.containsString(BUFFER_MEMORY_CONFIG))));
    }

    @Test
    public void shouldNotIncludeSslPropertiesInListPropertiesOutput() {
        // When:
        final PropertiesList props = makeSingleRequest("list properties;", PropertiesList.class);
        // Then:
        MatcherAssert.assertThat(props.getProperties().keySet(), Matchers.not(CoreMatchers.hasItems(SSL_CONFIG_NAMES.toArray(new String[0]))));
    }

    @Test
    public void shouldNotWaitIfNoCommandSequenceNumberSpecified() throws Exception {
        // When:
        makeSingleRequestWithSequenceNumber("list properties;", null, PropertiesList.class);
        // Then:
        Mockito.verify(commandStore, Mockito.never()).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
    }

    @Test
    public void shouldWaitIfCommandSequenceNumberSpecified() throws Exception {
        // When:
        makeSingleRequestWithSequenceNumber("list properties;", 2L, PropertiesList.class);
        // Then:
        Mockito.verify(commandStore).ensureConsumedPast(ArgumentMatchers.eq(2L), ArgumentMatchers.any());
    }

    @Test
    public void shouldReturnServiceUnavailableIfTimeoutWaitingForCommandSequenceNumber() throws Exception {
        // Given:
        Mockito.doThrow(new TimeoutException("timed out!")).when(commandStore).ensureConsumedPast(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        // When:
        final KsqlErrorMessage result = makeFailingRequestWithSequenceNumber("list properties;", 2L, SERVICE_UNAVAILABLE);
        // Then:
        MatcherAssert.assertThat(result.getErrorCode(), Matchers.is(ERROR_CODE_COMMAND_QUEUE_CATCHUP_TIMEOUT));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("Timed out while waiting for a previous command to execute"));
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.containsString("command sequence number: 2"));
    }

    @Test
    public void shouldUpdateTheLastRequestTime() {
        // When:
        ksqlResource.handleKsqlStatements(KsqlResourceTest.VALID_EXECUTABLE_REQUEST);
        // Then:
        Mockito.verify(activenessRegistrar).updateLastRequestTime();
    }

    @Test
    public void shouldHandleTerminateRequestCorrectly() {
        // When:
        final Response response = ksqlResource.terminateCluster(KsqlResourceTest.VALID_TERMINATE_REQUEST);
        // Then:
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        MatcherAssert.assertThat(response.getEntity(), CoreMatchers.instanceOf(KsqlEntityList.class));
        MatcherAssert.assertThat(size(), CoreMatchers.equalTo(1));
        MatcherAssert.assertThat(get(0), CoreMatchers.instanceOf(CommandStatusEntity.class));
        final CommandStatusEntity commandStatusEntity = ((CommandStatusEntity) (get(0)));
        MatcherAssert.assertThat(commandStatusEntity.getCommandStatus().getStatus(), CoreMatchers.equalTo(QUEUED));
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatementText(TERMINATE_CLUSTER_STATEMENT_TEXT))), ArgumentMatchers.any(), ArgumentMatchers.eq(Collections.singletonMap(DELETE_TOPIC_LIST_PROP, ImmutableList.of("Foo"))));
    }

    @Test
    public void shouldFailIfCannotWriteTerminateCommand() {
        // Given:
        Mockito.when(commandStore.enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new KsqlException(""));
        // When:
        final Response response = ksqlResource.terminateCluster(KsqlResourceTest.VALID_TERMINATE_REQUEST);
        // Then:
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(500));
        MatcherAssert.assertThat(response.getEntity().toString(), CoreMatchers.startsWith("Could not write the statement 'TERMINATE CLUSTER;' into the command "));
    }

    @Test
    public void shouldFailTerminateOnInvalidDeleteTopicPattern() {
        // Given:
        final ClusterTerminateRequest request = new ClusterTerminateRequest(ImmutableList.of("[Invalid Regex"));
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("Invalid pattern: [Invalid Regex"))));
        // When:
        ksqlResource.terminateCluster(request);
    }

    @Test
    public void shouldNeverEnqueueIfErrorIsThrown() {
        // Given:
        givenMockEngine();
        // when(ksqlEngine.execute(any(), any(), any())).thenThrow(new KsqlException("Fail"));
        // When:
        makeFailingRequest("REGISTER TOPIC X WITH (kafka_topic='bar', value_format='json';", BAD_REQUEST);
        // Then:
        Mockito.verify(commandStore, Mockito.never()).enqueueCommand(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldFailIfRegisterTopicAlreadyExists() {
        // Given:
        final String registerSql = "REGISTER TOPIC FOO WITH (kafka_topic='bar', value_format='json');";
        givenKsqlTopicRegistered("foo");
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("A topic with name 'FOO' already exists"))));
        // When:
        makeSingleRequest(registerSql, CommandStatusEntity.class);
    }

    @Test
    public void shouldFailIfCreateExistingSourceStream() {
        // Given:
        givenSource(DataSourceType.KSTREAM, "SOURCE", "topic1", "ksqlTopic1", KsqlResourceTest.SINGLE_FIELD_SCHEMA);
        givenKafkaTopicExists("topic2");
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString("Source already exists: SOURCE"))));
        // When:
        final String createSql = "CREATE STREAM SOURCE (val int) WITH (kafka_topic='topic2', value_format='json');";
        makeSingleRequest(createSql, CommandStatusEntity.class);
    }

    @Test
    public void shouldFailIfCreateExistingSourceTable() {
        // Given:
        givenSource(DataSourceType.KTABLE, "SOURCE", "topic1", "ksqlTopic1", KsqlResourceTest.SINGLE_FIELD_SCHEMA);
        givenKafkaTopicExists("topic2");
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString("Source already exists: SOURCE"))));
        // When:
        final String createSql = "CREATE TABLE SOURCE (val int) " + "WITH (kafka_topic='topic2', value_format='json', key='val');";
        makeSingleRequest(createSql, CommandStatusEntity.class);
    }

    @Test
    public void shouldFailIfCreateAsSelectExistingSourceStream() {
        // Given:
        givenSource(DataSourceType.KSTREAM, "SOURCE", "topic1", "ksqlTopic1", KsqlResourceTest.SINGLE_FIELD_SCHEMA);
        givenSource(DataSourceType.KSTREAM, "SINK", "topic2", "ksqlTopic2", KsqlResourceTest.SINGLE_FIELD_SCHEMA);
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString(("Cannot add the new data source. Another data source with the " + "same name already exists: KsqlStream name:SINK")))));
        // When:
        final String createSql = "CREATE STREAM SINK AS SELECT * FROM SOURCE;";
        makeSingleRequest(createSql, CommandStatusEntity.class);
    }

    @Test
    public void shouldFailIfCreateAsSelectExistingSourceTable() {
        // Given:
        givenSource(DataSourceType.KTABLE, "SOURCE", "topic1", "ksqlTopic1", KsqlResourceTest.SINGLE_FIELD_SCHEMA);
        givenSource(DataSourceType.KTABLE, "SINK", "topic2", "ksqlTopic2", KsqlResourceTest.SINGLE_FIELD_SCHEMA);
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(CoreMatchers.containsString(("Cannot add the new data source. Another data source with the " + "same name already exists: KsqlTable name:SINK")))));
        // When:
        final String createSql = "CREATE TABLE SINK AS SELECT * FROM SOURCE;";
        makeSingleRequest(createSql, CommandStatusEntity.class);
    }

    @Test
    public void shouldInlineRunScriptStatements() {
        // Given:
        final Map<String, ?> props = ImmutableMap.of(LEGACY_RUN_SCRIPT_STATEMENTS_CONTENT, (("CREATE STREAM " + (streamName)) + " AS SELECT * FROM test_stream;"));
        // When:
        makeRequest("RUN SCRIPT '/some/script.sql';", props);
        // Then:
        Mockito.verify(commandStore).enqueueCommand(MockitoHamcrest.argThat(Matchers.is(ParserMatchers.preparedStatement(CoreMatchers.instanceOf(CreateStreamAsSelect.class)))), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldThrowOnRunScriptStatementMissingScriptContent() {
        // Then:
        expectedException.expect(KsqlRestException.class);
        expectedException.expect(KsqlRestExceptionMatchers.exceptionStatusCode(Matchers.is(BAD_REQUEST)));
        expectedException.expect(KsqlRestExceptionMatchers.exceptionErrorMessage(KsqlErrorMessageMatchers.errorMessage(Matchers.is("Request is missing script content"))));
        // When:
        makeRequest("RUN SCRIPT '/some/script.sql';");
    }
}

