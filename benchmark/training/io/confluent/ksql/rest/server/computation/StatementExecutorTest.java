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
package io.confluent.ksql.rest.server.computation;


import CommandStatus.Status.ERROR;
import CommandStatus.Status.EXECUTING;
import CommandStatus.Status.PARSING;
import CommandStatus.Status.SUCCESS;
import KsqlConfig.KSQL_ACTIVE_PERSISTENT_QUERY_LIMIT_CONFIG;
import KsqlConfig.KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG;
import com.google.common.collect.ImmutableSet;
import io.confluent.ksql.KsqlExecutionContext.ExecuteResult;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.metastore.StructuredDataSource;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.tree.DropStream;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.query.QueryId;
import io.confluent.ksql.rest.entity.CommandStatus;
import io.confluent.ksql.rest.entity.CommandStatus.Status;
import io.confluent.ksql.rest.server.StatementParser;
import io.confluent.ksql.rest.server.computation.CommandId.Action;
import io.confluent.ksql.rest.server.computation.CommandId.Type;
import io.confluent.ksql.rest.server.utils.TestUtils;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.test.util.EmbeddedSingleNodeKafkaCluster;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.Pair;
import io.confluent.ksql.util.PersistentQueryMetadata;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.easymock.EasyMockSupport;
import org.easymock.IArgumentMatcher;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


@SuppressWarnings("ConstantConditions")
public class StatementExecutorTest extends EasyMockSupport {
    private static final Map<String, String> PRE_VERSION_5_NULL_ORIGINAL_PROPS = null;

    private KsqlEngine ksqlEngine;

    private StatementExecutor statementExecutor;

    private KsqlConfig ksqlConfig;

    private final StatementParser mockParser = niceMock(StatementParser.class);

    private final KsqlEngine mockEngine = strictMock(KsqlEngine.class);

    private final MetaStore mockMetaStore = niceMock(MetaStore.class);

    private final PersistentQueryMetadata mockQueryMetadata = niceMock(PersistentQueryMetadata.class);

    private StatementExecutor statementExecutorWithMocks;

    private ServiceContext serviceContext;

    @ClassRule
    public static final EmbeddedSingleNodeKafkaCluster CLUSTER = EmbeddedSingleNodeKafkaCluster.build();

    @Test
    public void shouldHandleCorrectDDLStatement() {
        final Command command = new Command(("REGISTER TOPIC users_topic " + "WITH (value_format = 'json', kafka_topic='user_topic_json');"), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId commandId = new CommandId(Type.TOPIC, "_CorrectTopicGen", Action.CREATE);
        handleStatement(command, commandId, Optional.empty());
        final Map<CommandId, CommandStatus> statusStore = statementExecutor.getStatuses();
        Assert.assertNotNull(statusStore);
        Assert.assertEquals(statusStore.size(), 1);
        Assert.assertEquals(getStatus(), SUCCESS);
    }

    @Test
    public void shouldHandleIncorrectDDLStatement() {
        final Command command = new Command(("REGIST ER TOPIC users_topic " + "WITH (value_format = 'json', kafka_topic='user_topic_json');"), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId commandId = new CommandId(Type.TOPIC, "_IncorrectTopicGen", Action.CREATE);
        handleStatement(command, commandId, Optional.empty());
        final Map<CommandId, CommandStatus> statusStore = statementExecutor.getStatuses();
        Assert.assertNotNull(statusStore);
        Assert.assertEquals(statusStore.size(), 1);
        Assert.assertEquals(getStatus(), ERROR);
    }

    @Test
    public void shouldThrowOnUnexpectedException() {
        // Given:
        final String statementText = "mama said knock you out";
        final StatementParser statementParser = mock(StatementParser.class);
        final KsqlEngine mockEngine = mock(KsqlEngine.class);
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());
        final StatementExecutor statementExecutor = new StatementExecutor(ksqlConfig, mockEngine, statementParser);
        final RuntimeException exception = new RuntimeException("i'm gonna knock you out");
        expect(statementParser.parseSingleStatement(statementText)).andThrow(exception);
        final Command command = new Command(statementText, Collections.emptyMap(), Collections.emptyMap());
        final CommandId commandId = new CommandId(Type.STREAM, "_CSASGen", Action.CREATE);
        replay(statementParser);
        // When:
        try {
            StatementExecutorTest.handleStatement(statementExecutor, command, commandId, Optional.empty());
            Assert.fail("handleStatement should throw");
        } catch (final RuntimeException caughtException) {
            // Then:
            MatcherAssert.assertThat(caughtException, Matchers.is(exception));
        }
    }

    @Test
    public void shouldBuildQueriesWithPersistedConfig() {
        final KsqlConfig originalConfig = new KsqlConfig(Collections.singletonMap(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG, "not-the-default"));
        // get a statement instance
        final String ddlText = "CREATE STREAM pageviews (viewtime bigint, pageid varchar) " + "WITH (kafka_topic='pageview_topic', VALUE_FORMAT='json');";
        final String statementText = "CREATE STREAM user1pv AS select * from pageviews WHERE userid = 'user1';";
        final StatementParser realParser = new StatementParser(ksqlEngine);
        final PreparedStatement<?> ddlStatement = realParser.parseSingleStatement(ddlText);
        ksqlEngine.execute(ddlStatement, originalConfig, Collections.emptyMap());
        final PreparedStatement<Statement> csasStatement = realParser.parseSingleStatement(statementText);
        expect(mockQueryMetadata.getQueryId()).andStubReturn(mock(QueryId.class));
        final KsqlConfig ksqlConfig = new KsqlConfig(Collections.emptyMap());
        final KsqlConfig expectedConfig = ksqlConfig.overrideBreakingConfigsWithOriginalValues(originalConfig.getAllConfigPropsWithSecretsObfuscated());
        final StatementExecutor statementExecutor = new StatementExecutor(ksqlConfig, mockEngine, mockParser);
        final Command csasCommand = new Command(statementText, Collections.emptyMap(), originalConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId csasCommandId = new CommandId(Type.STREAM, "_CSASGen", Action.CREATE);
        expect(mockParser.parseSingleStatement(statementText)).andReturn(csasStatement);
        expect(mockEngine.numberOfPersistentQueries()).andReturn(0);
        expect(mockEngine.execute(csasStatement, expectedConfig, Collections.emptyMap())).andReturn(ExecuteResult.of(mockQueryMetadata));
        mockQueryMetadata.start();
        expectLastCall();
        replay(mockParser, mockEngine, mockMetaStore, mockQueryMetadata);
        StatementExecutorTest.handleStatement(statementExecutor, csasCommand, csasCommandId, Optional.empty());
        verify(mockParser, mockEngine, mockMetaStore, mockQueryMetadata);
    }

    @Test
    public void shouldHandleCSAS_CTASStatement() {
        final Command topicCommand = new Command(("REGISTER TOPIC pageview_topic WITH " + ("(value_format = 'json', " + "kafka_topic='pageview_topic_json');")), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId topicCommandId = new CommandId(Type.TOPIC, "_CSASTopicGen", Action.CREATE);
        handleStatement(topicCommand, topicCommandId, Optional.empty());
        final Command csCommand = new Command(("CREATE STREAM pageview " + ("(viewtime bigint, pageid varchar, userid varchar) " + "WITH (registered_topic = 'pageview_topic');")), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId csCommandId = new CommandId(Type.STREAM, "_CSASStreamGen", Action.CREATE);
        handleStatement(csCommand, csCommandId, Optional.empty());
        final Command csasCommand = new Command(("CREATE STREAM user1pv " + " AS select * from pageview WHERE userid = 'user1';"), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId csasCommandId = new CommandId(Type.STREAM, "_CSASGen", Action.CREATE);
        handleStatement(csasCommand, csasCommandId, Optional.empty());
        final Command badCtasCommand = new Command(("CREATE TABLE user1pvtb " + ((" AS select * from pageview window tumbling(size 5 " + "second) WHERE userid = ") + "'user1' group by pageid;")), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId ctasCommandId = new CommandId(Type.TABLE, "_CTASGen", Action.CREATE);
        handleStatement(badCtasCommand, ctasCommandId, Optional.empty());
        final Command terminateCommand = new Command("TERMINATE CSAS_USER1PV_0;", Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId terminateCmdId = new CommandId(Type.TABLE, "_TerminateGen", Action.CREATE);
        handleStatement(terminateCommand, terminateCmdId, Optional.empty());
        final Map<CommandId, CommandStatus> statusStore = statementExecutor.getStatuses();
        MatcherAssert.assertThat(statusStore, Matchers.is(Matchers.notNullValue()));
        MatcherAssert.assertThat(statusStore.keySet(), Matchers.containsInAnyOrder(topicCommandId, csCommandId, csasCommandId, ctasCommandId, terminateCmdId));
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(SUCCESS));
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(SUCCESS));
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(SUCCESS));
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(ERROR));
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(SUCCESS));
    }

    private static class StatusMatcher implements IArgumentMatcher {
        final Status status;

        StatusMatcher(final CommandStatus.Status status) {
            this.status = status;
        }

        @Override
        public boolean matches(final Object item) {
            return (item instanceof CommandStatus) && (getStatus().equals(status));
        }

        @Override
        public void appendTo(final StringBuffer buffer) {
            buffer.append("status(").append(status).append(")");
        }
    }

    @Test
    public void shouldCompleteFutureOnSuccess() {
        final Command command = new Command(("CREATE STREAM foo (" + ((("biz bigint," + " baz varchar) ") + "WITH (kafka_topic = 'foo', ") + "value_format = 'json');")), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId commandId = new CommandId(Type.STREAM, "foo", Action.CREATE);
        final CommandStatusFuture status = mock(CommandStatusFuture.class);
        status.setStatus(StatementExecutorTest.sameStatus(PARSING));
        expectLastCall();
        status.setStatus(StatementExecutorTest.sameStatus(EXECUTING));
        expectLastCall();
        status.setFinalStatus(StatementExecutorTest.sameStatus(SUCCESS));
        expectLastCall();
        replay(status);
        handleStatement(command, commandId, Optional.of(status));
        verify(status);
    }

    @Test
    public void shouldCompleteFutureOnFailure() {
        shouldCompleteFutureOnSuccess();
        final Command command = new Command(("CREATE STREAM foo (" + ((("biz bigint," + " baz varchar) ") + "WITH (kafka_topic = 'foo', ") + "value_format = 'json');")), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId commandId = new CommandId(Type.STREAM, "foo", Action.CREATE);
        final CommandStatusFuture status = mock(CommandStatusFuture.class);
        status.setStatus(StatementExecutorTest.sameStatus(PARSING));
        expectLastCall();
        status.setStatus(StatementExecutorTest.sameStatus(Status.EXECUTING));
        expectLastCall();
        status.setFinalStatus(StatementExecutorTest.sameStatus(ERROR));
        expectLastCall();
        replay(status);
        handleStatement(command, commandId, Optional.of(status));
        verify(status);
    }

    @Test
    public void shouldHandlePriorStatements() {
        final TestUtils testUtils = new TestUtils();
        final List<Pair<CommandId, Command>> priorCommands = testUtils.getAllPriorCommandRecords();
        final CommandId topicCommandId = new CommandId(Type.TOPIC, "_CSASTopicGen", Action.CREATE);
        final CommandId csCommandId = new CommandId(Type.STREAM, "_CSASStreamGen", Action.CREATE);
        final CommandId csasCommandId = new CommandId(Type.STREAM, "_CSASGen", Action.CREATE);
        final CommandId ctasCommandId = new CommandId(Type.TABLE, "_CTASGen", Action.CREATE);
        priorCommands.forEach(( pair) -> statementExecutor.handleRestore(new QueuedCommand(pair.left, pair.right)));
        final Map<CommandId, CommandStatus> statusStore = statementExecutor.getStatuses();
        Assert.assertNotNull(statusStore);
        Assert.assertEquals(4, statusStore.size());
        Assert.assertEquals(SUCCESS, getStatus());
        Assert.assertEquals(SUCCESS, getStatus());
        Assert.assertEquals(SUCCESS, getStatus());
        Assert.assertEquals(ERROR, getStatus());
    }

    @Test
    public void shouldEnforceReferentialIntegrity() {
        createStreamsAndStartTwoPersistentQueries();
        // Now try to drop streams/tables to test referential integrity
        tryDropThatViolatesReferentialIntegrity();
        // Terminate the queries using the stream/table
        terminateQueries();
        // Now drop should be successful
        final Command dropTableCommand2 = new Command("drop table table1;", Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId dropTableCommandId2 = new CommandId(Type.TABLE, "_TABLE1", Action.DROP);
        StatementExecutorTest.handleStatement(statementExecutor, dropTableCommand2, dropTableCommandId2, Optional.empty());
        // DROP should succed since no query is using the table
        final Optional<CommandStatus> dropTableCommandStatus2 = statementExecutor.getStatus(dropTableCommandId2);
        Assert.assertTrue(dropTableCommandStatus2.isPresent());
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(SUCCESS));
        // DROP should succeed since no query is using the stream.
        final Command dropStreamCommand3 = new Command("drop stream pageview;", Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated());
        final CommandId dropStreamCommandId3 = new CommandId(Type.STREAM, "_user1pv", Action.DROP);
        StatementExecutorTest.handleStatement(statementExecutor, dropStreamCommand3, dropStreamCommandId3, Optional.empty());
        final Optional<CommandStatus> dropStreamCommandStatus3 = statementExecutor.getStatus(dropStreamCommandId3);
        MatcherAssert.assertThat(getStatus(), CoreMatchers.equalTo(SUCCESS));
    }

    @Test
    public void shouldSkipStartWhenReplayingLog() {
        // Given:
        final QueryId queryId = new QueryId("csas-query-id");
        final String name = "foo";
        final PersistentQueryMetadata mockQuery = mockReplayCSAS("CSAS", name, queryId);
        replayAll();
        // When:
        statementExecutorWithMocks.handleRestore(new QueuedCommand(new CommandId(Type.STREAM, name, Action.CREATE), new Command("CSAS", Collections.emptyMap(), Collections.emptyMap())));
        // Then:
        verify(mockParser, mockEngine, mockQuery);
    }

    @Test
    public void shouldCascade4Dot1DropStreamCommand() {
        // Given:
        final DropStream mockDropStream = mockDropStream("foo");
        expect(mockMetaStore.getSource("foo")).andStubReturn(mock(StructuredDataSource.class));
        expect(mockMetaStore.getQueriesWithSink("foo")).andStubReturn(ImmutableSet.of("query-id"));
        expect(mockEngine.getMetaStore()).andStubReturn(mockMetaStore);
        expect(mockEngine.getPersistentQuery(new QueryId("query-id"))).andReturn(Optional.of(mockQueryMetadata));
        mockQueryMetadata.close();
        expectLastCall();
        expect(mockEngine.execute(eq(PreparedStatement.of("DROP", mockDropStream)), anyObject(), anyObject())).andReturn(ExecuteResult.of("SUCCESS"));
        replayAll();
        // When:
        statementExecutorWithMocks.handleRestore(new QueuedCommand(new CommandId(Type.STREAM, "foo", Action.DROP), new Command("DROP", Collections.emptyMap(), StatementExecutorTest.PRE_VERSION_5_NULL_ORIGINAL_PROPS)));
        // Then:
        verify(mockParser, mockEngine, mockMetaStore);
    }

    @Test
    public void shouldNotCascadeDropStreamCommand() {
        // Given:
        final String drop = "DROP";
        final DropStream mockDropStream = mockDropStream("foo");
        final PreparedStatement<DropStream> statement = PreparedStatement.of(drop, mockDropStream);
        expect(mockEngine.execute(eq(statement), anyObject(), anyObject())).andReturn(ExecuteResult.of("SUCCESS"));
        replayAll();
        // When:
        statementExecutorWithMocks.handleRestore(new QueuedCommand(new CommandId(Type.STREAM, "foo", Action.DROP), new Command(drop, Collections.emptyMap(), Collections.emptyMap())));
        // Then:
        verify(mockParser, mockEngine, mockMetaStore);
    }

    @Test
    public void shouldFailCreateAsSelectIfExceedActivePersistentQueriesLimit() {
        // Given:
        createStreamsAndStartTwoPersistentQueries();
        // Prepare to try adding a third
        final KsqlConfig cmdConfig = StatementExecutorTest.givenCommandConfig(KSQL_ACTIVE_PERSISTENT_QUERY_LIMIT_CONFIG, 2);
        final Command csasCommand = StatementExecutorTest.givenCommand("CREATE STREAM user2pv AS select * from pageview;", cmdConfig);
        final CommandId csasCommandId = new CommandId(Type.STREAM, "_CSASGen2", Action.CREATE);
        // When:
        handleStatement(csasCommand, csasCommandId, Optional.empty());
        // Then:
        final CommandStatus commandStatus = getCommandStatus(csasCommandId);
        MatcherAssert.assertThat("CSAS statement should fail since exceeds limit of 2 active persistent queries", commandStatus.getStatus(), Matchers.is(ERROR));
        MatcherAssert.assertThat(commandStatus.getMessage(), CoreMatchers.containsString(("would cause the number of active, persistent queries " + "to exceed the configured limit")));
    }

    @Test
    public void shouldFailInsertIntoIfExceedActivePersistentQueriesLimit() {
        // Given:
        createStreamsAndStartTwoPersistentQueries();
        // Set limit and prepare to try adding a query that exceeds the limit
        final KsqlConfig cmdConfig = StatementExecutorTest.givenCommandConfig(KSQL_ACTIVE_PERSISTENT_QUERY_LIMIT_CONFIG, 1);
        final Command insertIntoCommand = StatementExecutorTest.givenCommand("INSERT INTO user1pv select * from pageview;", cmdConfig);
        final CommandId insertIntoCommandId = new CommandId(Type.STREAM, "_InsertQuery1", Action.CREATE);
        // When:
        handleStatement(insertIntoCommand, insertIntoCommandId, Optional.empty());
        // Then: statement should fail since exceeds limit of 1 active persistent query
        final CommandStatus commandStatus = getCommandStatus(insertIntoCommandId);
        MatcherAssert.assertThat(commandStatus.getStatus(), Matchers.is(ERROR));
        MatcherAssert.assertThat(commandStatus.getMessage(), CoreMatchers.containsString(("would cause the number of active, persistent queries " + "to exceed the configured limit")));
    }

    @Test
    public void shouldHandleLegacyRunScriptCommand() {
        // Given:
        final String runScriptStatement = "run script";
        final String queryStatement = "a query";
        final PersistentQueryMetadata mockQuery = mockReplayRunScript(runScriptStatement, queryStatement);
        mockQuery.start();
        expectLastCall().once();
        replayAll();
        // When:
        statementExecutorWithMocks.handleStatement(new QueuedCommand(new CommandId(Type.STREAM, "RunScript", Action.EXECUTE), new Command(runScriptStatement, Collections.singletonMap("ksql.run.script.statements", queryStatement), Collections.emptyMap())));
        // Then:
        verify(mockParser, mockEngine, mockQuery);
    }

    @Test
    public void shouldRestoreLegacyRunScriptCommand() {
        // Given:
        final String runScriptStatement = "run script";
        final String queryStatement = "a persistent query";
        final PersistentQueryMetadata mockQuery = mockReplayRunScript(runScriptStatement, queryStatement);
        replayAll();
        // When:
        statementExecutorWithMocks.handleRestore(new QueuedCommand(new CommandId(Type.STREAM, "RunScript", Action.EXECUTE), new Command(runScriptStatement, Collections.singletonMap("ksql.run.script.statements", queryStatement), Collections.emptyMap())));
        // Then:
        verify(mockParser, mockEngine, mockQuery);
    }
}

