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
package io.confluent.ksql.rest.server;


import KsqlModuleType.SERVER;
import ProcessingLogConfig.TOPIC_AUTO_CREATE;
import ProcessingLogConfig.TOPIC_NAME;
import SqlType.STRING;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
import io.confluent.ksql.KsqlExecutionContext;
import io.confluent.ksql.KsqlExecutionContext.ExecuteResult;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.function.UdfLoader;
import io.confluent.ksql.logging.processing.ProcessingLogConfig;
import io.confluent.ksql.parser.KsqlParser.ParsedStatement;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.SqlBaseParser.SingleStatementContext;
import io.confluent.ksql.parser.tree.CreateStream;
import io.confluent.ksql.parser.tree.CreateTable;
import io.confluent.ksql.parser.tree.Expression;
import io.confluent.ksql.parser.tree.PrimitiveType;
import io.confluent.ksql.parser.tree.QualifiedName;
import io.confluent.ksql.parser.tree.Query;
import io.confluent.ksql.parser.tree.SetProperty;
import io.confluent.ksql.parser.tree.StringLiteral;
import io.confluent.ksql.parser.tree.TableElement;
import io.confluent.ksql.parser.tree.UnsetProperty;
import io.confluent.ksql.schema.inference.SchemaInjector;
import io.confluent.ksql.services.KafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import io.confluent.ksql.util.KsqlStatementException;
import io.confluent.ksql.util.PersistentQueryMetadata;
import io.confluent.ksql.util.QueryMetadata;
import io.confluent.ksql.version.metrics.VersionCheckerAgent;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class StandaloneExecutorTest {
    private static final String PROCESSING_LOG_TOPIC_NAME = "proclogtop";

    private static final ProcessingLogConfig processingLogConfig = new ProcessingLogConfig(ImmutableMap.of(TOPIC_AUTO_CREATE, true, TOPIC_NAME, StandaloneExecutorTest.PROCESSING_LOG_TOPIC_NAME));

    private static final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());

    private static final List<TableElement> SOME_ELEMENTS = ImmutableList.of(new TableElement("bob", PrimitiveType.of(STRING)));

    private static final QualifiedName SOME_NAME = QualifiedName.of("Bob");

    private static final ImmutableMap<String, Expression> JSON_PROPS = ImmutableMap.of("VALUE_FORMAT", new StringLiteral("json"));

    private static final String SOME_TOPIC = "some-topic";

    private static final ImmutableMap<String, Expression> AVRO_PROPS = ImmutableMap.of("VALUE_FORMAT", new StringLiteral("avro"), "KAFKA_TOPIC", new StringLiteral(StandaloneExecutorTest.SOME_TOPIC));

    private static final CreateStream CREATE_STREAM = new CreateStream(StandaloneExecutorTest.SOME_NAME, StandaloneExecutorTest.SOME_ELEMENTS, true, StandaloneExecutorTest.JSON_PROPS);

    private static final ParsedStatement PARSED_STMT_0 = ParsedStatement.of("sql 0", Mockito.mock(SingleStatementContext.class));

    private static final ParsedStatement PARSED_STMT_1 = ParsedStatement.of("sql 1", Mockito.mock(SingleStatementContext.class));

    private static final PreparedStatement<?> PREPARED_STMT_0 = PreparedStatement.of("sql 0", StandaloneExecutorTest.CREATE_STREAM);

    private static final PreparedStatement<?> PREPARED_STMT_1 = PreparedStatement.of("sql 1", StandaloneExecutorTest.CREATE_STREAM);

    private static final PreparedStatement<CreateStream> STMT_0_WITH_SCHEMA = PreparedStatement.of("sql 0", new CreateStream(QualifiedName.of("CS 0"), StandaloneExecutorTest.SOME_ELEMENTS, true, Collections.emptyMap()));

    private static final PreparedStatement<CreateStream> STMT_1_WITH_SCHEMA = PreparedStatement.of("sql 1", new CreateStream(QualifiedName.of("CS 1"), StandaloneExecutorTest.SOME_ELEMENTS, true, Collections.emptyMap()));

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private Query query;

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private KsqlExecutionContext sandBox;

    @Mock
    private UdfLoader udfLoader;

    @Mock
    private PersistentQueryMetadata persistentQuery;

    @Mock
    private PersistentQueryMetadata sandBoxQuery;

    @Mock
    private QueryMetadata nonPersistentQueryMd;

    @Mock
    private VersionCheckerAgent versionChecker;

    @Mock
    private ServiceContext serviceContext;

    @Mock
    private KafkaTopicClient kafkaTopicClient;

    @Mock
    private SchemaRegistryClient srClient;

    @Mock
    private Function<ServiceContext, SchemaInjector> schemaInjectorFactory;

    @Mock
    private SchemaInjector schemaInjector;

    @Mock
    private SchemaInjector sandBoxSchemaInjector;

    private Path queriesFile;

    private StandaloneExecutor standaloneExecutor;

    @Test
    public void shouldStartTheVersionCheckerAgent() {
        // When:
        standaloneExecutor.start();
        Mockito.verify(versionChecker).start(ArgumentMatchers.eq(SERVER), ArgumentMatchers.any());
    }

    @Test
    public void shouldLoadQueryFile() {
        // Given:
        givenQueryFileContains("This statement");
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).parse("This statement");
    }

    @Test
    public void shouldThrowIfCanNotLoadQueryFile() {
        // Given:
        givenFileDoesNotExist();
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Could not read the query file");
        // When:
        standaloneExecutor.start();
    }

    @Test
    public void shouldLoadUdfs() {
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(udfLoader).load();
    }

    @Test
    public void shouldCreateProcessingLogTopic() {
        // When:
        standaloneExecutor.start();
        // Then
        Mockito.verify(kafkaTopicClient).createTopic(ArgumentMatchers.eq(StandaloneExecutorTest.PROCESSING_LOG_TOPIC_NAME), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort());
    }

    @Test
    public void shouldNotCreateProcessingLogTopicIfNotConfigured() {
        // Given:
        standaloneExecutor = new StandaloneExecutor(serviceContext, new ProcessingLogConfig(ImmutableMap.of(TOPIC_AUTO_CREATE, false, TOPIC_NAME, StandaloneExecutorTest.PROCESSING_LOG_TOPIC_NAME)), StandaloneExecutorTest.ksqlConfig, ksqlEngine, queriesFile.toString(), udfLoader, false, versionChecker, schemaInjectorFactory);
        // When:
        standaloneExecutor.start();
        // Then
        Mockito.verify(kafkaTopicClient, Mockito.times(0)).createTopic(ArgumentMatchers.eq(StandaloneExecutorTest.PROCESSING_LOG_TOPIC_NAME), ArgumentMatchers.anyInt(), ArgumentMatchers.anyShort());
    }

    @Test
    public void shouldFailOnDropStatement() {
        // Given:
        givenQueryFileParsesTo(PreparedStatement.of("DROP Test", new io.confluent.ksql.parser.tree.DropStream(StandaloneExecutorTest.SOME_NAME, false, false)));
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expectMessage(("Unsupported statement. " + ((((((("Only the following statements are supporting in standalone mode:\n" + "CREAETE STREAM AS SELECT\n") + "CREATE STREAM\n") + "CREATE TABLE\n") + "CREATE TABLE AS SELECT\n") + "INSERT INTO\n") + "SET\n") + "UNSET")));
        // When:
        standaloneExecutor.start();
    }

    @Test
    public void shouldFailIfNoPersistentQueries() {
        // Given:
        givenExecutorWillFailOnNoQueries();
        givenQueryFileParsesTo(PreparedStatement.of("SET PROP", new SetProperty(Optional.empty(), "name", "value")));
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("The SQL file did not contain any queries");
        // When:
        standaloneExecutor.start();
    }

    @Test
    public void shouldRunCsStatement() {
        // Given:
        final PreparedStatement<CreateStream> cs = PreparedStatement.of("CS", new CreateStream(StandaloneExecutorTest.SOME_NAME, StandaloneExecutorTest.SOME_ELEMENTS, false, StandaloneExecutorTest.JSON_PROPS));
        givenQueryFileParsesTo(cs);
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(cs, StandaloneExecutorTest.ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldRunCtStatement() {
        // Given:
        final PreparedStatement<CreateTable> ct = PreparedStatement.of("CT", new CreateTable(StandaloneExecutorTest.SOME_NAME, StandaloneExecutorTest.SOME_ELEMENTS, false, StandaloneExecutorTest.JSON_PROPS));
        givenQueryFileParsesTo(ct);
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(ct, StandaloneExecutorTest.ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldRunSetStatements() {
        // Given:
        final PreparedStatement<SetProperty> setProp = PreparedStatement.of("SET PROP", new SetProperty(Optional.empty(), "name", "value"));
        final PreparedStatement<CreateStream> cs = PreparedStatement.of("CS", new CreateStream(StandaloneExecutorTest.SOME_NAME, StandaloneExecutorTest.SOME_ELEMENTS, false, StandaloneExecutorTest.JSON_PROPS));
        givenQueryFileParsesTo(setProp, cs);
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(ArgumentMatchers.eq(cs), ArgumentMatchers.any(), ArgumentMatchers.eq(ImmutableMap.of("name", "value")));
    }

    @Test
    public void shouldRunUnSetStatements() {
        // Given:
        final PreparedStatement<SetProperty> setProp = PreparedStatement.of("SET", new SetProperty(Optional.empty(), "name", "value"));
        final PreparedStatement<UnsetProperty> unsetProp = PreparedStatement.of("UNSET", new UnsetProperty(Optional.empty(), "name"));
        final PreparedStatement<CreateStream> cs = PreparedStatement.of("CS", new CreateStream(StandaloneExecutorTest.SOME_NAME, StandaloneExecutorTest.SOME_ELEMENTS, false, StandaloneExecutorTest.JSON_PROPS));
        givenQueryFileParsesTo(setProp, unsetProp, cs);
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(ArgumentMatchers.eq(cs), ArgumentMatchers.any(), ArgumentMatchers.eq(Collections.emptyMap()));
    }

    @Test
    public void shouldRunCsasStatements() {
        // Given:
        final PreparedStatement<?> csas = PreparedStatement.of("CSAS1", new io.confluent.ksql.parser.tree.CreateStreamAsSelect(StandaloneExecutorTest.SOME_NAME, query, false, Collections.emptyMap(), Optional.empty()));
        givenQueryFileParsesTo(csas);
        Mockito.when(sandBox.execute(ArgumentMatchers.eq(csas), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(persistentQuery));
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(csas, StandaloneExecutorTest.ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldRunCtasStatements() {
        // Given:
        final PreparedStatement<?> ctas = PreparedStatement.of("CTAS", new io.confluent.ksql.parser.tree.CreateTableAsSelect(StandaloneExecutorTest.SOME_NAME, query, false, Collections.emptyMap()));
        givenQueryFileParsesTo(ctas);
        Mockito.when(sandBox.execute(ArgumentMatchers.eq(ctas), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(persistentQuery));
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(ctas, StandaloneExecutorTest.ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldRunInsertIntoStatements() {
        // Given:
        final PreparedStatement<?> insertInto = PreparedStatement.of("InsertInto", new io.confluent.ksql.parser.tree.InsertInto(StandaloneExecutorTest.SOME_NAME, query, Optional.empty()));
        givenQueryFileParsesTo(insertInto);
        Mockito.when(sandBox.execute(ArgumentMatchers.eq(insertInto), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(persistentQuery));
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(ksqlEngine).execute(insertInto, StandaloneExecutorTest.ksqlConfig, Collections.emptyMap());
    }

    @Test
    public void shouldThrowIfExecutingPersistentQueryDoesNotReturnQuery() {
        // Given:
        givenFileContainsAPersistentQuery();
        Mockito.when(sandBox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of("well, this is unexpected."));
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Could not build the query");
        // When:
        standaloneExecutor.start();
    }

    @Test
    public void shouldThrowIfExecutingPersistentQueryReturnsNonPersistentMetaData() {
        // Given:
        givenFileContainsAPersistentQuery();
        Mockito.when(sandBox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(nonPersistentQueryMd));
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Could not build the query");
        // When:
        standaloneExecutor.start();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfParseThrows() {
        // Given:
        Mockito.when(ksqlEngine.parse(ArgumentMatchers.any())).thenThrow(new RuntimeException("Boom!"));
        // When:
        standaloneExecutor.start();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowIfExecuteThrows() {
        // Given:
        Mockito.when(ksqlEngine.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RuntimeException("Boom!"));
        // When:
        standaloneExecutor.start();
    }

    @Test
    public void shouldCloseEngineOnStop() {
        // When:
        standaloneExecutor.stop();
        // Then:
        Mockito.verify(ksqlEngine).close();
    }

    @Test
    public void shouldCloseServiceContextOnStop() {
        // When:
        standaloneExecutor.stop();
        // Then:
        Mockito.verify(serviceContext).close();
    }

    @Test
    public void shouldStartQueries() {
        // Given:
        Mockito.when(ksqlEngine.getPersistentQueries()).thenReturn(ImmutableList.of(persistentQuery));
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(persistentQuery).start();
    }

    @Test
    public void shouldNotStartValidationPhaseQueries() {
        // Given:
        givenFileContainsAPersistentQuery();
        Mockito.when(sandBox.execute(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(ExecuteResult.of(sandBoxQuery));
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(sandBoxQuery, Mockito.never()).start();
    }

    @Test
    public void shouldOnlyPrepareNextStatementOncePreviousStatementHasBeenExecuted() {
        // Given:
        Mockito.when(ksqlEngine.parse(ArgumentMatchers.any())).thenReturn(ImmutableList.of(StandaloneExecutorTest.PARSED_STMT_0, StandaloneExecutorTest.PARSED_STMT_1));
        // When:
        standaloneExecutor.start();
        // Then:
        final InOrder inOrder = Mockito.inOrder(ksqlEngine);
        inOrder.verify(ksqlEngine).prepare(StandaloneExecutorTest.PARSED_STMT_0);
        inOrder.verify(ksqlEngine).execute(ArgumentMatchers.eq(StandaloneExecutorTest.PREPARED_STMT_0), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(ksqlEngine).prepare(StandaloneExecutorTest.PARSED_STMT_1);
        inOrder.verify(ksqlEngine).execute(ArgumentMatchers.eq(StandaloneExecutorTest.PREPARED_STMT_1), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldThrowOnCreateStatementWithNoElements() {
        // Given:
        final PreparedStatement<CreateStream> cs = PreparedStatement.of("CS", new CreateStream(StandaloneExecutorTest.SOME_NAME, Collections.emptyList(), false, StandaloneExecutorTest.JSON_PROPS));
        givenQueryFileParsesTo(cs);
        // Then:
        expectedException.expect(KsqlStatementException.class);
        expectedException.expectMessage("statement does not define the schema and the supplied format does not support schema inference");
        // When:
        standaloneExecutor.start();
    }

    @Test
    public void shouldSupportSchemaInference() {
        // Given:
        final PreparedStatement<CreateStream> cs = PreparedStatement.of("CS", new CreateStream(StandaloneExecutorTest.SOME_NAME, Collections.emptyList(), false, StandaloneExecutorTest.AVRO_PROPS));
        givenQueryFileParsesTo(cs);
        Mockito.when(sandBoxSchemaInjector.forStatement(cs)).thenReturn(StandaloneExecutorTest.STMT_0_WITH_SCHEMA);
        Mockito.when(schemaInjector.forStatement(cs)).thenReturn(StandaloneExecutorTest.STMT_1_WITH_SCHEMA);
        // When:
        standaloneExecutor.start();
        // Then:
        Mockito.verify(sandBox).execute(ArgumentMatchers.eq(StandaloneExecutorTest.STMT_0_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(ksqlEngine).execute(ArgumentMatchers.eq(StandaloneExecutorTest.STMT_1_WITH_SCHEMA), ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

