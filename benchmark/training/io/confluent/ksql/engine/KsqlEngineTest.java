/**
 * Copyright 2019 Confluent Inc.
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
package io.confluent.ksql.engine;


import StreamsConfig.BOOTSTRAP_SERVERS_CONFIG;
import Type.INT;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.KsqlExecutionContext;
import io.confluent.ksql.KsqlExecutionContext.ExecuteResult;
import io.confluent.ksql.metastore.MutableMetaStore;
import io.confluent.ksql.parser.KsqlParser.ParsedStatement;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.exception.ParseFailedException;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.CreateStreamAsSelect;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.DropTable;
import io.confluent.ksql.parser.tree.SetProperty;
import io.confluent.ksql.parser.tree.UnsetProperty;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.serde.KsqlTopicSerDe;
import io.confluent.ksql.serde.json.KsqlJsonTopicSerDe;
import io.confluent.ksql.services.FakeKafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.KsqlExceptionMatcher;
import io.confluent.ksql.util.KsqlStatementException;
import io.confluent.ksql.util.PersistentQueryMetadata;
import io.confluent.ksql.util.QueryMetadata;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.kafka.common.utils.Utils;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings({ "ConstantConditions", "SameParameterValue" })
@RunWith(MockitoJUnitRunner.class)
public class KsqlEngineTest {
    private static final KsqlConfig KSQL_CONFIG = new KsqlConfig(ImmutableMap.of(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private MutableMetaStore metaStore;

    @Spy
    private final KsqlTopicSerDe jsonKsqlSerde = new KsqlJsonTopicSerDe();

    @Spy
    private final SchemaRegistryClient schemaRegistryClient = new MockSchemaRegistryClient();

    private final Supplier<SchemaRegistryClient> schemaRegistryClientFactory = () -> schemaRegistryClient;

    private KsqlEngine ksqlEngine;

    private ServiceContext serviceContext;

    @Spy
    private final FakeKafkaTopicClient topicClient = new FakeKafkaTopicClient();

    private KsqlExecutionContext sandbox;

    @Test
    public void shouldCreatePersistentQueries() {
        final List<QueryMetadata> queries = KsqlEngineTestUtil.execute(ksqlEngine, ("create table bar as select * from test2;" + "create table foo as select * from test2;"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        MatcherAssert.assertThat(queries.size(), Matchers.equalTo(2));
        final PersistentQueryMetadata queryOne = ((PersistentQueryMetadata) (queries.get(0)));
        final PersistentQueryMetadata queryTwo = ((PersistentQueryMetadata) (queries.get(1)));
        MatcherAssert.assertThat(queryOne.getEntity(), Matchers.equalTo("BAR"));
        MatcherAssert.assertThat(queryTwo.getEntity(), Matchers.equalTo("FOO"));
    }

    @Test
    public void shouldThrowOnTerminateAsNotExecutable() {
        // Given:
        final PersistentQueryMetadata query = ((PersistentQueryMetadata) (KsqlEngineTestUtil.execute(ksqlEngine, "create table bar as select * from test2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0)));
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("Statement not executable")));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("TERMINATE CTAS_BAR_0;")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, (("TERMINATE " + (query.getQueryId())) + ";"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldExecuteInsertIntoStreamOnSandBox() {
        // Given:
        final List<ParsedStatement> statements = parse(("create stream bar as select * from orders;" + "insert into bar select * from orders;"));
        givenStatementAlreadyExecuted(statements.get(0));
        // When:
        final ExecuteResult result = sandbox.execute(sandbox.prepare(statements.get(1)), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(result.getQuery(), Matchers.is(Matchers.not(Optional.empty())));
    }

    @Test
    public void shouldThrowWhenExecutingInsertIntoTable() {
        // Given:
        KsqlEngineTestUtil.execute(ksqlEngine, "create table bar as select * from test2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        final ParsedStatement parsed = ksqlEngine.parse("insert into bar select * from test2;").get(0);
        expectedException.expect(ParseFailedException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString("INSERT INTO can only be used to insert into a stream. BAR is a table.")));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("insert into bar select * from test2;")));
        // When:
        prepare(parsed);
    }

    @Test
    public void shouldExecuteInsertIntoStream() {
        // Given:
        KsqlEngineTestUtil.execute(ksqlEngine, "create stream bar as select * from orders;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // When:
        final List<QueryMetadata> queries = KsqlEngineTestUtil.execute(ksqlEngine, "insert into bar select * from orders;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(queries, Matchers.hasSize(1));
    }

    @Test
    public void shouldMaintainOrderOfReturnedQueries() {
        // When:
        final List<QueryMetadata> queries = KsqlEngineTestUtil.execute(ksqlEngine, ("create stream foo as select * from orders;" + "create stream bar as select * from orders;"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(queries, Matchers.hasSize(2));
        MatcherAssert.assertThat(queries.get(0).getStatementString(), Matchers.containsString("create stream foo as"));
        MatcherAssert.assertThat(queries.get(1).getStatementString(), Matchers.containsString("create stream bar as"));
    }

    @Test(expected = ParseFailedException.class)
    public void shouldFailToCreateQueryIfSelectingFromNonExistentEntity() {
        KsqlEngineTestUtil.execute(ksqlEngine, "select * from bar;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test(expected = ParseFailedException.class)
    public void shouldFailWhenSyntaxIsInvalid() {
        KsqlEngineTestUtil.execute(ksqlEngine, "blah;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldUpdateReferentialIntegrityTableCorrectly() {
        KsqlEngineTestUtil.execute(ksqlEngine, ("create table bar as select * from test2;" + "create table foo as select * from test2;"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        MatcherAssert.assertThat(metaStore.getQueriesWithSource("TEST2"), Matchers.equalTo(Utils.mkSet("CTAS_BAR_0", "CTAS_FOO_1")));
        MatcherAssert.assertThat(metaStore.getQueriesWithSink("BAR"), Matchers.equalTo(Utils.mkSet("CTAS_BAR_0")));
        MatcherAssert.assertThat(metaStore.getQueriesWithSink("FOO"), Matchers.equalTo(Utils.mkSet("CTAS_FOO_1")));
    }

    @Test
    public void shouldFailIfReferentialIntegrityIsViolated() {
        // Given:
        KsqlEngineTestUtil.execute(ksqlEngine, ("create table bar as select * from test2;" + "create table foo as select * from test2;"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Cannot drop FOO.\n" + (("The following queries read from this source: [].\n" + "The following queries write into this source: [CTAS_FOO_1].\n") + "You need to terminate them before dropping FOO.")))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("drop table foo;")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "drop table foo;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldFailDDLStatementIfTopicDoesNotExist() {
        // Given:
        final ParsedStatement stmt = parse(("CREATE STREAM S1_NOTEXIST (COL1 BIGINT, COL2 VARCHAR) " + "WITH  (KAFKA_TOPIC = 'S1_NOTEXIST', VALUE_FORMAT = 'JSON');")).get(0);
        final PreparedStatement<?> prepared = prepare(stmt);
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expectMessage("Kafka topic does not exist: S1_NOTEXIST");
        // When:
        sandbox.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldDropTableIfAllReferencedQueriesTerminated() {
        // Given:
        final QueryMetadata secondQuery = KsqlEngineTestUtil.execute(ksqlEngine, ("create table bar as select * from test2;" + "create table foo as select * from test2;"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(1);
        secondQuery.close();
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "drop table foo;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(metaStore.getSource("foo"), Matchers.nullValue());
    }

    @Test
    public void shouldEnforceTopicExistenceCorrectly() {
        serviceContext.getTopicClient().createTopic("s1_topic", 1, ((short) (1)));
        final String runScriptContent = "CREATE STREAM S1 (COL1 BIGINT) WITH  (KAFKA_TOPIC = \'s1_topic\', VALUE_FORMAT = \'JSON\');\n" + ("CREATE TABLE T1 AS SELECT COL1, count(*) FROM S1 GROUP BY COL1;\n" + "CREATE STREAM S2 (C1 BIGINT) WITH (KAFKA_TOPIC = \'T1\', VALUE_FORMAT = \'JSON\');\n");
        KsqlEngineTestUtil.execute(ksqlEngine, runScriptContent, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        Assert.assertTrue(serviceContext.getTopicClient().isTopicExists("T1"));
    }

    @Test
    public void shouldNotEnforceTopicExistenceWhileParsing() {
        final String runScriptContent = "CREATE STREAM S1 (COL1 BIGINT, COL2 VARCHAR) " + (((("WITH  (KAFKA_TOPIC = \'s1_topic\', VALUE_FORMAT = \'JSON\');\n" + "CREATE TABLE T1 AS SELECT COL1, count(*) FROM ") + "S1 GROUP BY COL1;\n") + "CREATE STREAM S2 (C1 BIGINT, C2 BIGINT) ") + "WITH (KAFKA_TOPIC = \'T1\', VALUE_FORMAT = \'JSON\');\n");
        final List<?> parsedStatements = ksqlEngine.parse(runScriptContent);
        MatcherAssert.assertThat(parsedStatements.size(), Matchers.equalTo(3));
    }

    @Test
    public void shouldThrowFromSandBoxOnPrepareIfSourceTopicDoesNotExist() {
        // Given:
        final PreparedStatement<?> statement = prepare(parse(("CREATE STREAM S1 (COL1 BIGINT) " + "WITH (KAFKA_TOPIC = 'i_do_not_exist', VALUE_FORMAT = 'JSON');")).get(0));
        // Expect:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("Kafka topic does not exist: i_do_not_exist")));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is(("CREATE STREAM S1 (COL1 BIGINT)" + " WITH (KAFKA_TOPIC = 'i_do_not_exist', VALUE_FORMAT = 'JSON');"))));
        // When:
        sandbox.execute(statement, KsqlEngineTest.KSQL_CONFIG, new HashMap());
    }

    @Test
    public void shouldThrowFromExecuteIfSourceTopicDoesNotExist() {
        // Given:
        final PreparedStatement<?> statement = prepare(parse(("CREATE STREAM S1 (COL1 BIGINT) " + "WITH (KAFKA_TOPIC = 'i_do_not_exist', VALUE_FORMAT = 'JSON');")).get(0));
        // Expect:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("Kafka topic does not exist: i_do_not_exist")));
        // When:
        ksqlEngine.execute(statement, KsqlEngineTest.KSQL_CONFIG, new HashMap());
    }

    @Test
    public void shouldThrowFromTryExecuteIfSinkTopicExistsWithWrongPartitionCount() {
        // Given:
        serviceContext.getTopicClient().createTopic("source", 1, ((short) (1)));
        serviceContext.getTopicClient().createTopic("sink", 2, ((short) (1)));
        final List<ParsedStatement> statements = parse(("CREATE STREAM S1 (C1 BIGINT) WITH (KAFKA_TOPIC=\'source\', VALUE_FORMAT=\'JSON\');\n" + "CREATE STREAM S2 WITH (KAFKA_TOPIC=\'sink\') AS SELECT * FROM S1;\n"));
        givenStatementAlreadyExecuted(statements.get(0));
        final PreparedStatement<?> prepared = prepare(statements.get(1));
        // Expect:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("A Kafka topic with the name 'sink' already exists, " + "with different partition/replica configuration than required"))));
        // When:
        sandbox.execute(prepared, KsqlEngineTest.KSQL_CONFIG, new HashMap());
    }

    @Test
    public void shouldThrowFromExecuteIfSinkTopicExistsWithWrongPartitionCount() {
        // Given:
        final List<ParsedStatement> statements = parse(("CREATE STREAM S1 (C1 BIGINT) WITH (KAFKA_TOPIC=\'source\', VALUE_FORMAT=\'JSON\');\n" + "CREATE STREAM S2 WITH (KAFKA_TOPIC=\'sink\') AS SELECT * FROM S1;\n"));
        serviceContext.getTopicClient().createTopic("source", 1, ((short) (1)));
        serviceContext.getTopicClient().createTopic("sink", 2, ((short) (1)));
        ksqlEngine.execute(prepare(statements.get(0)), KsqlEngineTest.KSQL_CONFIG, new HashMap());
        final PreparedStatement<?> prepared = prepare(statements.get(1));
        // Expect:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("A Kafka topic with the name 'sink' already exists, " + "with different partition/replica configuration than required"))));
        // When:
        ksqlEngine.execute(prepared, KsqlEngineTest.KSQL_CONFIG, new HashMap());
    }

    @Test
    public void shouldThrowFromTryExecuteIfSinkTopicExistsWithWrongReplicaCount() {
        // Given:
        final List<ParsedStatement> statements = parse(("CREATE STREAM S1 (C1 BIGINT) WITH (KAFKA_TOPIC=\'source\', VALUE_FORMAT=\'JSON\');\n" + "CREATE STREAM S2 WITH (KAFKA_TOPIC=\'sink\') AS SELECT * FROM S1;\n"));
        serviceContext.getTopicClient().createTopic("sink", 1, ((short) (2)));
        serviceContext.getTopicClient().createTopic("source", 1, ((short) (3)));
        givenStatementAlreadyExecuted(statements.get(0));
        final PreparedStatement<?> prepared = prepare(statements.get(1));
        // Expect:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("A Kafka topic with the name 'sink' already exists, " + "with different partition/replica configuration than required"))));
        // When:
        sandbox.execute(prepared, KsqlEngineTest.KSQL_CONFIG, new HashMap());
    }

    @Test
    public void shouldThrowFromExecuteIfSinkTopicExistsWithWrongReplicaCount() {
        // Given:
        final List<ParsedStatement> statements = parse(("CREATE STREAM S1 (C1 BIGINT) WITH (KAFKA_TOPIC=\'source\', VALUE_FORMAT=\'JSON\');\n" + "CREATE STREAM S2 WITH (KAFKA_TOPIC=\'sink\') AS SELECT * FROM S1;\n"));
        serviceContext.getTopicClient().createTopic("source", 1, ((short) (3)));
        serviceContext.getTopicClient().createTopic("sink", 1, ((short) (2)));
        givenStatementAlreadyExecuted(statements.get(0));
        final PreparedStatement<?> prepared = prepare(statements.get(1));
        // Expect:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("A Kafka topic with the name 'sink' already exists, " + "with different partition/replica configuration than required"))));
        // When:
        ksqlEngine.execute(prepared, KsqlEngineTest.KSQL_CONFIG, new HashMap());
    }

    @Test
    public void shouldHandleCommandsSpreadOverMultipleLines() {
        final String runScriptContent = "CREATE STREAM S1 \n" + (("(COL1 BIGINT, COL2 VARCHAR)\n" + " WITH \n") + "(KAFKA_TOPIC = \'s1_topic\', VALUE_FORMAT = \'JSON\');\n");
        final List<?> parsedStatements = ksqlEngine.parse(runScriptContent);
        MatcherAssert.assertThat(parsedStatements, Matchers.hasSize(1));
    }

    @Test
    public void shouldCleanupSchemaAndTopicForStream() throws Exception {
        // Given:
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "create stream bar with (value_format = 'avro') as select * from test1;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        query.close();
        final Schema schema = SchemaBuilder.record("Test").fields().name("clientHash").type().fixed("MD5").size(16).noDefault().endRecord();
        schemaRegistryClient.register("BAR-value", schema);
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "DROP STREAM bar DELETE TOPIC;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(serviceContext.getTopicClient().isTopicExists("BAR"), Matchers.equalTo(false));
        MatcherAssert.assertThat(schemaRegistryClient.getAllSubjects(), Matchers.not(Matchers.hasItem("BAR-value")));
    }

    @Test
    public void shouldCleanupSchemaAndTopicForTable() throws Exception {
        // Given:
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "create table bar with (value_format = 'avro') as select * from test2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        query.close();
        final Schema schema = SchemaBuilder.record("Test").fields().name("clientHash").type().fixed("MD5").size(16).noDefault().endRecord();
        schemaRegistryClient.register("BAR-value", schema);
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "DROP TABLE bar DELETE TOPIC;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(serviceContext.getTopicClient().isTopicExists("BAR"), Matchers.equalTo(false));
        MatcherAssert.assertThat(schemaRegistryClient.getAllSubjects(), Matchers.not(Matchers.hasItem("BAR-value")));
    }

    @Test
    public void shouldNotDeleteSchemaNorTopicForStream() throws Exception {
        // Given:
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, ("create stream bar with (value_format = 'avro') as select * from test1;" + "create stream foo as select * from test1;"), KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        query.close();
        final Schema schema = SchemaBuilder.record("Test").fields().name("clientHash").type().fixed("MD5").size(16).noDefault().endRecord();
        schemaRegistryClient.register("BAR-value", schema);
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "DROP STREAM bar;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(serviceContext.getTopicClient().isTopicExists("BAR"), Matchers.equalTo(true));
        MatcherAssert.assertThat(schemaRegistryClient.getAllSubjects(), Matchers.hasItem("BAR-value"));
    }

    @Test
    public void shouldThrowIfSchemaNotPresent() {
        // Given:
        givenTopicsExist("bar");
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString("The statement does not define any columns.")));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("create stream bar with (value_format='avro', kafka_topic='bar');")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "create stream bar with (value_format='avro', kafka_topic='bar');", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldFailIfAvroSchemaNotEvolvable() {
        // Given:
        givenTopicWithSchema("T", Schema.create(INT));
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("Cannot register avro schema for T as the schema registry rejected it, " + "(maybe schema evolution issues?)"))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE TABLE T WITH(VALUE_FORMAT='AVRO') AS SELECT * FROM TEST2;")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "CREATE TABLE T WITH(VALUE_FORMAT='AVRO') AS SELECT * FROM TEST2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldNotFailIfAvroSchemaEvolvable() {
        // Given:
        final Schema evolvableSchema = SchemaBuilder.record("Test").fields().nullableInt("f1", 1).endRecord();
        givenTopicWithSchema("T", evolvableSchema);
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "CREATE TABLE T WITH(VALUE_FORMAT='AVRO') AS SELECT * FROM TEST2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(metaStore.getSource("T"), Matchers.is(Matchers.notNullValue()));
    }

    @Test
    public void shouldNotDeleteSchemaNorTopicForTable() throws Exception {
        // Given:
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "create table bar with (value_format = 'avro') as select * from test2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        query.close();
        final Schema schema = SchemaBuilder.record("Test").fields().name("clientHash").type().fixed("MD5").size(16).noDefault().endRecord();
        schemaRegistryClient.register("BAR-value", schema);
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "DROP TABLE bar;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(serviceContext.getTopicClient().isTopicExists("BAR"), Matchers.equalTo(true));
        MatcherAssert.assertThat(schemaRegistryClient.getAllSubjects(), Matchers.hasItem("BAR-value"));
    }

    @Test
    public void shouldCleanUpInternalTopicsOnClose() {
        // Given:
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "select * from test1;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        query.start();
        // When:
        query.close();
        // Then:
        Mockito.verify(topicClient).deleteInternalTopics(query.getQueryApplicationId());
    }

    @Test
    public void shouldNotCleanUpInternalTopicsOnCloseIfQueryNeverStarted() {
        // Given:
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "create stream s1 with (value_format = 'avro') as select * from test1;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        // When:
        query.close();
        // Then:
        Mockito.verify(topicClient, Mockito.never()).deleteInternalTopics(ArgumentMatchers.any());
    }

    @Test
    public void shouldRemovePersistentQueryFromEngineWhenClosed() {
        // Given:
        final int startingLiveQueries = ksqlEngine.numberOfLiveQueries();
        final int startingPersistentQueries = ksqlEngine.numberOfPersistentQueries();
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "create stream s1 with (value_format = 'avro') as select * from test1;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        // When:
        query.close();
        // Then:
        MatcherAssert.assertThat(ksqlEngine.getPersistentQuery(KsqlEngineTest.getQueryId(query)), Matchers.is(Optional.empty()));
        MatcherAssert.assertThat(ksqlEngine.numberOfLiveQueries(), Matchers.is(startingLiveQueries));
        MatcherAssert.assertThat(ksqlEngine.numberOfPersistentQueries(), Matchers.is(startingPersistentQueries));
    }

    @Test
    public void shouldRemoveTransientQueryFromEngineWhenClosed() {
        // Given:
        final int startingLiveQueries = ksqlEngine.numberOfLiveQueries();
        final QueryMetadata query = KsqlEngineTestUtil.execute(ksqlEngine, "select * from test1;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap()).get(0);
        // When:
        query.close();
        // Then:
        MatcherAssert.assertThat(ksqlEngine.numberOfLiveQueries(), Matchers.is(startingLiveQueries));
    }

    @Test
    public void shouldUseSerdeSupplierToBuildQueries() {
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "create table bar as select * from test2;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        Mockito.verify(jsonKsqlSerde, Mockito.atLeastOnce()).getGenericRowSerde(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.eq(schemaRegistryClientFactory), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldHandleMultipleStatements() {
        // Given:
        final String sql = "" + (((((((((((((((((("-- single line comment\n" + "/*\n") + "   Multi-line comment\n") + "*/\n") + "CREATE STREAM S0 (a INT, b VARCHAR) ") + "      WITH (kafka_topic=\'s0_topic\', value_format=\'DELIMITED\');\n") + "\n") + "SET \'auto.offset.reset\'=\'earliest\';\n") + "\n") + "CREATE TABLE T1 (f0 BIGINT, f1 DOUBLE) ") + "     WITH (kafka_topic=\'t1_topic\', value_format=\'JSON\', key = \'f0\');\n") + "\n") + "CREATE STREAM S1 AS SELECT * FROM S0;\n") + "\n") + "UNSET \'auto.offset.reset\';\n") + "\n") + "CREATE STREAM S2 AS SELECT * FROM S0;\n") + "\n") + "DROP TABLE T1;");
        givenTopicsExist("s0_topic", "t1_topic");
        final List<QueryMetadata> queries = new ArrayList<>();
        // When:
        final List<PreparedStatement<?>> preparedStatements = ksqlEngine.parse(sql).stream().map(( stmt) -> {
            final PreparedStatement<?> prepared = ksqlEngine.prepare(stmt);
            final ExecuteResult result = ksqlEngine.execute(prepared, KSQL_CONFIG, new HashMap<>());
            result.getQuery().ifPresent(queries::add);
            return prepared;
        }).collect(Collectors.toList());
        // Then:
        final List<?> statements = preparedStatements.stream().map(PreparedStatement::getStatement).collect(Collectors.toList());
        MatcherAssert.assertThat(statements, Matchers.contains(Matchers.instanceOf(CreateStream.class), Matchers.instanceOf(SetProperty.class), Matchers.instanceOf(CreateTable.class), Matchers.instanceOf(CreateStreamAsSelect.class), Matchers.instanceOf(UnsetProperty.class), Matchers.instanceOf(CreateStreamAsSelect.class), Matchers.instanceOf(DropTable.class)));
        MatcherAssert.assertThat(queries, Matchers.hasSize(2));
    }

    @Test
    public void shouldSetPropertyInRunScript() {
        final Map<String, Object> overriddenProperties = new HashMap<>();
        KsqlEngineTestUtil.execute(ksqlEngine, "SET 'auto.offset.reset' = 'earliest';", KsqlEngineTest.KSQL_CONFIG, overriddenProperties);
        MatcherAssert.assertThat(overriddenProperties.get("auto.offset.reset"), Matchers.equalTo("earliest"));
    }

    @Test
    public void shouldUnsetPropertyInRunScript() {
        final Map<String, Object> overriddenProperties = new HashMap<>();
        KsqlEngineTestUtil.execute(ksqlEngine, ("SET 'auto.offset.reset' = 'earliest';" + "UNSET 'auto.offset.reset';"), KsqlEngineTest.KSQL_CONFIG, overriddenProperties);
        MatcherAssert.assertThat(overriddenProperties.keySet(), Matchers.not(Matchers.hasItem("auto.offset.reset")));
    }

    @Test
    public void shouldNotThrowWhenPreparingDuplicateTable() {
        // Given:
        final List<ParsedStatement> parsed = ksqlEngine.parse(("CREATE TABLE FOO AS SELECT * FROM TEST2; " + "CREATE TABLE FOO WITH (KAFKA_TOPIC='BAR') AS SELECT * FROM TEST2;"));
        givenStatementAlreadyExecuted(parsed.get(0));
        // When:
        ksqlEngine.prepare(parsed.get(1));
        // Then: no exception thrown
    }

    @Test
    public void shouldThrowWhenExecutingDuplicateTable() {
        // Given:
        final List<ParsedStatement> parsed = ksqlEngine.parse(("CREATE TABLE FOO AS SELECT * FROM TEST2; " + "CREATE TABLE FOO WITH (KAFKA_TOPIC='BAR') AS SELECT * FROM TEST2;"));
        givenStatementAlreadyExecuted(parsed.get(0));
        final PreparedStatement<?> prepared = prepare(parsed.get(1));
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Cannot add the new data source. " + "Another data source with the same name already exists: KsqlTable name:FOO"))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE TABLE FOO WITH (KAFKA_TOPIC='BAR') AS SELECT * FROM TEST2;")));
        // When:
        ksqlEngine.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldThrowWhenPreparingUnknownSource() {
        // Given:
        final ParsedStatement stmt = ksqlEngine.parse("CREATE STREAM FOO AS SELECT * FROM UNKNOWN;").get(0);
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("Failed to prepare statement: UNKNOWN does not exist.")));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE STREAM FOO AS SELECT * FROM UNKNOWN;")));
        // When:
        ksqlEngine.prepare(stmt);
    }

    @Test
    public void shouldNotThrowWhenPreparingDuplicateStream() {
        // Given:
        final ParsedStatement stmt = ksqlEngine.parse(("CREATE STREAM FOO AS SELECT * FROM ORDERS; " + "CREATE STREAM FOO WITH (KAFKA_TOPIC='BAR') AS SELECT * FROM ORDERS;")).get(0);
        // When:
        ksqlEngine.prepare(stmt);
        // Then: No exception thrown.
    }

    @Test
    public void shouldThrowWhenExecutingDuplicateStream() {
        // Given:
        final List<ParsedStatement> parsed = ksqlEngine.parse(("CREATE STREAM FOO AS SELECT * FROM ORDERS; " + "CREATE STREAM FOO WITH (KAFKA_TOPIC='BAR') AS SELECT * FROM ORDERS;"));
        givenStatementAlreadyExecuted(parsed.get(0));
        final PreparedStatement<?> prepared = ksqlEngine.prepare(parsed.get(1));
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Cannot add the new data source. " + "Another data source with the same name already exists: KsqlStream name:FOO"))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE STREAM FOO WITH (KAFKA_TOPIC='BAR') AS SELECT * FROM ORDERS;")));
        // When:
        ksqlEngine.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldThrowWhenExecutingQueriesIfCsasCreatesTable() {
        // Given:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("Invalid result type. Your SELECT query produces a TABLE. " + "Please use CREATE TABLE AS SELECT statement instead."))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE STREAM FOO AS SELECT COUNT(ORDERID) FROM ORDERS GROUP BY ORDERID;")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "CREATE STREAM FOO AS SELECT COUNT(ORDERID) FROM ORDERS GROUP BY ORDERID;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldThrowWhenExecutingQueriesIfCtasCreatesStream() {
        // Given:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("Invalid result type. Your SELECT query produces a STREAM. " + "Please use CREATE STREAM AS SELECT statement instead."))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE TABLE FOO AS SELECT * FROM ORDERS;")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "CREATE TABLE FOO AS SELECT * FROM ORDERS;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldThrowWhenTryExecuteCsasThatCreatesTable() {
        // Given:
        final PreparedStatement<?> statement = prepare(parse("CREATE STREAM FOO AS SELECT COUNT(ORDERID) FROM ORDERS GROUP BY ORDERID;").get(0));
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.containsString(("Invalid result type. Your SELECT query produces a TABLE. " + "Please use CREATE TABLE AS SELECT statement instead."))));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE STREAM FOO AS SELECT COUNT(ORDERID) FROM ORDERS GROUP BY ORDERID;")));
        // When:
        sandbox.execute(statement, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldThrowWhenTryExecuteCtasThatCreatesStream() {
        // Given:
        final PreparedStatement<?> statement = prepare(parse("CREATE TABLE FOO AS SELECT * FROM ORDERS;").get(0));
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("CREATE TABLE FOO AS SELECT * FROM ORDERS;")));
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is(("Invalid result type. Your SELECT query produces a STREAM. " + "Please use CREATE STREAM AS SELECT statement instead."))));
        // When:
        sandbox.execute(statement, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldThrowIfStatementMissingTopicConfig() {
        final List<ParsedStatement> parsed = parse(("CREATE TABLE FOO (viewtime BIGINT, pageid VARCHAR) WITH (VALUE_FORMAT='AVRO');" + (("CREATE STREAM FOO (viewtime BIGINT, pageid VARCHAR) WITH (VALUE_FORMAT='AVRO');" + "CREATE TABLE FOO (viewtime BIGINT, pageid VARCHAR) WITH (VALUE_FORMAT='JSON');") + "CREATE STREAM FOO (viewtime BIGINT, pageid VARCHAR) WITH (VALUE_FORMAT='JSON');")));
        for (final ParsedStatement statement : parsed) {
            final PreparedStatement<?> prepared = ksqlEngine.prepare(statement);
            try {
                ksqlEngine.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
                Assert.fail();
            } catch (final KsqlStatementException e) {
                MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Corresponding Kafka topic (KAFKA_TOPIC) should be set in WITH clause."));
            }
        }
    }

    @Test
    public void shouldThrowIfStatementMissingValueFormatConfig() {
        // Given:
        givenTopicsExist("foo");
        final List<ParsedStatement> parsed = parse(("CREATE TABLE FOO (viewtime BIGINT, pageid VARCHAR) WITH (KAFKA_TOPIC='foo');" + "CREATE STREAM FOO (viewtime BIGINT, pageid VARCHAR) WITH (KAFKA_TOPIC='foo');"));
        for (final ParsedStatement statement : parsed) {
            final PreparedStatement<?> prepared = ksqlEngine.prepare(statement);
            try {
                // When:
                ksqlEngine.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
                // Then:
                Assert.fail();
            } catch (final KsqlStatementException e) {
                MatcherAssert.assertThat(e.getMessage(), Matchers.containsString("Topic format(VALUE_FORMAT) should be set in WITH clause."));
            }
        }
    }

    @Test
    public void shouldThrowOnNoneExecutableDdlStatement() {
        // Given:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expect(KsqlExceptionMatcher.rawMessage(Matchers.is("Statement not executable")));
        expectedException.expect(KsqlExceptionMatcher.statementText(Matchers.is("SHOW STREAMS;")));
        // When:
        KsqlEngineTestUtil.execute(ksqlEngine, "SHOW STREAMS;", KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
    }

    @Test
    public void shouldNotUpdateMetaStoreDuringTryExecute() {
        // Given:
        final int numberOfLiveQueries = ksqlEngine.numberOfLiveQueries();
        final int numPersistentQueries = ksqlEngine.numberOfPersistentQueries();
        final List<ParsedStatement> statements = parse(("SET 'auto.offset.reset' = 'earliest';" + ((("CREATE STREAM S1 (COL1 BIGINT) WITH (KAFKA_TOPIC = 's1_topic', VALUE_FORMAT = 'JSON');" + "CREATE TABLE BAR AS SELECT * FROM TEST2;") + "CREATE TABLE FOO AS SELECT * FROM TEST2;") + "DROP TABLE TEST3;")));
        topicClient.preconditionTopicExists("s1_topic", 1, ((short) (1)), Collections.emptyMap());
        // When:
        statements.forEach(( stmt) -> sandbox.execute(sandbox.prepare(stmt), KSQL_CONFIG, new HashMap<>()));
        // Then:
        MatcherAssert.assertThat(metaStore.getSource("TEST3"), Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(metaStore.getQueriesWithSource("TEST2"), Matchers.is(Matchers.empty()));
        MatcherAssert.assertThat(metaStore.getSource("BAR"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat(metaStore.getSource("FOO"), Matchers.is(Matchers.nullValue()));
        MatcherAssert.assertThat("live", ksqlEngine.numberOfLiveQueries(), Matchers.is(numberOfLiveQueries));
        MatcherAssert.assertThat("peristent", ksqlEngine.numberOfPersistentQueries(), Matchers.is(numPersistentQueries));
    }

    @Test
    public void shouldNotCreateAnyTopicsDuringTryExecute() {
        // Given:
        topicClient.preconditionTopicExists("s1_topic", 1, ((short) (1)), Collections.emptyMap());
        final List<ParsedStatement> statements = parse(("CREATE STREAM S1 (COL1 BIGINT) WITH (KAFKA_TOPIC = 's1_topic', VALUE_FORMAT = 'JSON');" + (("CREATE TABLE BAR AS SELECT * FROM TEST2;" + "CREATE TABLE FOO AS SELECT * FROM TEST2;") + "DROP TABLE TEST3;")));
        // When:
        statements.forEach(( stmt) -> sandbox.execute(sandbox.prepare(stmt), KSQL_CONFIG, Collections.emptyMap()));
        // Then:
        MatcherAssert.assertThat("no topics should be created during a tryExecute call", topicClient.createdTopics().keySet(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldNotIncrementQueryIdCounterDuringTryExecute() {
        // Given:
        final String sql = "create table foo as select * from test2;";
        final PreparedStatement<?> statement = prepare(parse(sql).get(0));
        // When:
        sandbox.execute(statement, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        final List<QueryMetadata> queries = KsqlEngineTestUtil.execute(ksqlEngine, sql, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        MatcherAssert.assertThat("query id of actual execute should not be affected by previous tryExecute", getQueryId(), Matchers.is(new QueryId("CTAS_FOO_0")));
    }

    @Test
    public void shouldNotRegisterAnySchemasDuringSandboxExecute() throws Exception {
        // Given:
        final List<ParsedStatement> statements = parse(("create table foo WITH(VALUE_FORMAT='AVRO') as select * from test2;" + "create stream foo2 WITH(VALUE_FORMAT='AVRO') as select * from orders;"));
        givenStatementAlreadyExecuted(statements.get(0));
        final PreparedStatement<?> prepared = prepare(statements.get(1));
        // When:
        sandbox.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        Mockito.verify(schemaRegistryClient, Mockito.never()).register(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldOnlyUpdateSandboxOnQueryClose() {
        // Given:
        givenSqlAlreadyExecuted("create table bar as select * from test2;");
        final QueryId queryId = ksqlEngine.getPersistentQueries().get(0).getQueryId();
        final PersistentQueryMetadata sandBoxQuery = sandbox.getPersistentQuery(queryId).get();
        // When:
        sandBoxQuery.close();
        // Then:
        MatcherAssert.assertThat("main engine should not be updated", ksqlEngine.getPersistentQuery(queryId), Matchers.is(Matchers.not(Optional.empty())));
        MatcherAssert.assertThat("sand box should be updated", sandbox.getPersistentQuery(queryId), Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldRegisterPersistentQueriesOnlyInSandbox() {
        // Given:
        final PreparedStatement<?> prepared = prepare(parse("create table bar as select * from test2;").get(0));
        // When:
        final ExecuteResult result = sandbox.execute(prepared, KsqlEngineTest.KSQL_CONFIG, Collections.emptyMap());
        // Then:
        MatcherAssert.assertThat(result.getQuery(), Matchers.is(Matchers.not(Optional.empty())));
        MatcherAssert.assertThat(sandbox.getPersistentQuery(KsqlEngineTest.getQueryId(result.getQuery().get())), Matchers.is(Matchers.not(Optional.empty())));
        MatcherAssert.assertThat(ksqlEngine.getPersistentQuery(KsqlEngineTest.getQueryId(result.getQuery().get())), Matchers.is(Optional.empty()));
    }

    @Test
    public void shouldExecuteDdlStatement() {
        // Given:
        final PreparedStatement<?> statement = prepare(parse("SET 'auto.offset.reset' = 'earliest';").get(0));
        // When:
        final ExecuteResult result = sandbox.execute(statement, KsqlEngineTest.KSQL_CONFIG, new HashMap());
        // Then:
        MatcherAssert.assertThat(result.getCommandResult(), Matchers.is(Optional.of("property:auto.offset.reset set to earliest")));
    }

    @Test
    public void shouldBeAbleToParseInvalidThings() {
        // Given:
        // No Stream called 'I_DO_NOT_EXIST' exists
        // When:
        final List<ParsedStatement> parsed = ksqlEngine.parse("CREATE STREAM FOO AS SELECT * FROM I_DO_NOT_EXIST;");
        // Then:
        MatcherAssert.assertThat(parsed, Matchers.hasSize(1));
    }

    @Test
    public void shouldThrowOnPrepareIfSourcesDoNotExist() {
        // Given:
        final ParsedStatement parsed = ksqlEngine.parse("CREATE STREAM FOO AS SELECT * FROM I_DO_NOT_EXIST;").get(0);
        // Then:
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Failed to prepare statement: I_DO_NOT_EXIST does not exist");
        // When:
        ksqlEngine.prepare(parsed);
    }

    @Test
    public void shouldBeAbleToPrepareTerminateAndDrop() {
        // Given:
        givenSqlAlreadyExecuted("CREATE STREAM FOO AS SELECT * FROM TEST1;");
        final List<ParsedStatement> parsed = ksqlEngine.parse(("TERMINATE CSAS_FOO_0;" + "DROP STREAM FOO;"));
        // When:
        parsed.forEach(ksqlEngine::prepare);
        // Then: did not throw.
    }
}

