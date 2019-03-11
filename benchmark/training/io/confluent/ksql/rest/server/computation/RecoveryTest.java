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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.metastore.KsqlTopic;
import io.confluent.ksql.metastore.MetaStore;
import io.confluent.ksql.metastore.StructuredDataSource;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.rest.entity.KsqlRequest;
import io.confluent.ksql.rest.server.computation.CommandId.Action;
import io.confluent.ksql.rest.server.computation.CommandId.Type;
import io.confluent.ksql.rest.server.resources.KsqlResource;
import io.confluent.ksql.rest.util.ClusterTerminator;
import io.confluent.ksql.schema.inference.DefaultSchemaInjector;
import io.confluent.ksql.schema.inference.SchemaInjector;
import io.confluent.ksql.serde.KsqlTopicSerDe;
import io.confluent.ksql.services.FakeKafkaTopicClient;
import io.confluent.ksql.services.ServiceContext;
import io.confluent.ksql.services.TestServiceContext;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.PersistentQueryMetadata;
import io.confluent.ksql.util.timestamp.TimestampExtractionPolicy;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.junit.Test;
import org.mockito.Mockito;


public class RecoveryTest {
    private final KsqlConfig ksqlConfig = new KsqlConfig(ImmutableMap.of("bootstrap.servers", "0.0.0.0"));

    private final List<QueuedCommand> commands = new LinkedList<>();

    private final FakeKafkaTopicClient topicClient = new FakeKafkaTopicClient();

    private final ServiceContext serviceContext = TestServiceContext.create(topicClient);

    private final RecoveryTest.KsqlServer server1 = new RecoveryTest.KsqlServer(commands);

    private final RecoveryTest.KsqlServer server2 = new RecoveryTest.KsqlServer(commands);

    private static class FakeCommandQueue implements CommandQueue {
        private final List<QueuedCommand> commandLog;

        private final CommandIdAssigner commandIdAssigner;

        private int offset;

        FakeCommandQueue(final List<QueuedCommand> commandLog) {
            this.commandIdAssigner = new CommandIdAssigner();
            this.commandLog = commandLog;
        }

        @Override
        public QueuedCommandStatus enqueueCommand(final PreparedStatement<?> statement, final KsqlConfig ksqlConfig, final Map<String, Object> overwriteProperties) {
            final CommandId commandId = commandIdAssigner.getCommandId(statement.getStatement());
            final long commandSequenceNumber = commandLog.size();
            commandLog.add(new QueuedCommand(commandId, new Command(statement.getStatementText(), Collections.emptyMap(), ksqlConfig.getAllConfigPropsWithSecretsObfuscated()), Optional.empty()));
            return new QueuedCommandStatus(commandSequenceNumber, new CommandStatusFuture(commandId));
        }

        @Override
        public List<QueuedCommand> getNewCommands(final Duration timeout) {
            final List<QueuedCommand> commands = commandLog.subList(offset, commandLog.size());
            offset = commandLog.size();
            return commands;
        }

        @Override
        public List<QueuedCommand> getRestoreCommands() {
            final List<QueuedCommand> restoreCommands = ImmutableList.copyOf(commandLog);
            this.offset = commandLog.size();
            return restoreCommands;
        }

        @Override
        public void ensureConsumedPast(final long seqNum, final Duration timeout) {
        }

        @Override
        public boolean isEmpty() {
            return commandLog.isEmpty();
        }

        @Override
        public void wakeup() {
        }

        @Override
        public void close() {
        }
    }

    private class KsqlServer {
        final KsqlEngine ksqlEngine;

        final KsqlResource ksqlResource;

        final RecoveryTest.FakeCommandQueue fakeCommandQueue;

        final StatementExecutor statementExecutor;

        final CommandRunner commandRunner;

        KsqlServer(final List<QueuedCommand> commandLog) {
            this.ksqlEngine = createKsqlEngine();
            this.fakeCommandQueue = new RecoveryTest.FakeCommandQueue(commandLog);
            final Function<ServiceContext, SchemaInjector> schemaInjectorFactory = ( sc) -> new DefaultSchemaInjector(new io.confluent.ksql.schema.inference.SchemaRegistryTopicSchemaSupplier(sc.getSchemaRegistryClient()));
            this.ksqlResource = new KsqlResource(ksqlConfig, ksqlEngine, serviceContext, fakeCommandQueue, Duration.ofMillis(0), () -> {
            }, schemaInjectorFactory);
            this.statementExecutor = new StatementExecutor(ksqlConfig, ksqlEngine, new io.confluent.ksql.rest.server.StatementParser(ksqlEngine));
            this.commandRunner = new CommandRunner(statementExecutor, fakeCommandQueue, ksqlEngine, 1, Mockito.mock(ClusterTerminator.class));
        }

        void recover() {
            this.commandRunner.processPriorCommands();
        }

        void executeCommands() {
            this.commandRunner.fetchAndRunCommands();
        }

        void submitCommands(final String... statements) {
            for (final String statement : statements) {
                final Response response = ksqlResource.handleKsqlStatements(new KsqlRequest(statement, Collections.emptyMap(), null));
                MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
                executeCommands();
            }
        }

        void close() {
            ksqlEngine.close();
        }
    }

    private static class TopicMatcher extends TypeSafeDiagnosingMatcher<KsqlTopic> {
        final Matcher<String> nameMatcher;

        final Matcher<String> kafkaNameMatcher;

        final Matcher<KsqlTopicSerDe> serDeMatcher;

        TopicMatcher(final KsqlTopic topic) {
            this.nameMatcher = CoreMatchers.equalTo(topic.getName());
            this.kafkaNameMatcher = CoreMatchers.equalTo(topic.getKafkaTopicName());
            this.serDeMatcher = CoreMatchers.instanceOf(topic.getKsqlTopicSerDe().getClass());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendList("Topic(", ", ", ")", Arrays.asList(nameMatcher, kafkaNameMatcher, serDeMatcher));
        }

        @Override
        public boolean matchesSafely(final KsqlTopic other, final Description description) {
            if (!(RecoveryTest.test(nameMatcher, other.getName(), description, "name mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(kafkaNameMatcher, other.getKafkaTopicName(), description, "kafka name mismatch: "))) {
                return false;
            }
            return RecoveryTest.test(serDeMatcher, other.getKsqlTopicSerDe(), description, "serde mismatch: ");
        }
    }

    private static class StructuredDataSourceMatcher extends TypeSafeDiagnosingMatcher<StructuredDataSource> {
        final StructuredDataSource source;

        final Matcher<StructuredDataSource.DataSourceType> typeMatcher;

        final Matcher<String> nameMatcher;

        final Matcher<Schema> schemaMatcher;

        final Matcher<String> sqlMatcher;

        final Matcher<TimestampExtractionPolicy> extractionPolicyMatcher;

        final Matcher<KsqlTopic> topicMatcher;

        StructuredDataSourceMatcher(final StructuredDataSource source) {
            this.source = source;
            this.typeMatcher = CoreMatchers.equalTo(source.getDataSourceType());
            this.nameMatcher = CoreMatchers.equalTo(source.getName());
            this.schemaMatcher = CoreMatchers.equalTo(source.getSchema());
            this.sqlMatcher = CoreMatchers.equalTo(source.getSqlExpression());
            this.extractionPolicyMatcher = CoreMatchers.equalTo(source.getTimestampExtractionPolicy());
            this.topicMatcher = RecoveryTest.sameTopic(source.getKsqlTopic());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendList("Source(", ", ", ")", Arrays.asList(nameMatcher, typeMatcher, schemaMatcher, sqlMatcher, extractionPolicyMatcher, topicMatcher));
        }

        @Override
        protected boolean matchesSafely(final StructuredDataSource other, final Description description) {
            if (!(RecoveryTest.test(typeMatcher, other.getDataSourceType(), description, "type mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(nameMatcher, other.getName(), description, "name mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(schemaMatcher, other.getSchema(), description, "schema mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(sqlMatcher, other.getSqlExpression(), description, "sql mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(extractionPolicyMatcher, other.getTimestampExtractionPolicy(), description, "timestamp extraction policy mismatch"))) {
                return false;
            }
            return RecoveryTest.test(topicMatcher, other.getKsqlTopic(), description, "topic mismatch: ");
        }
    }

    private static class MetaStoreMatcher extends TypeSafeDiagnosingMatcher<MetaStore> {
        final Map<String, Matcher<StructuredDataSource>> sourceMatchers;

        MetaStoreMatcher(final MetaStore metaStore) {
            this.sourceMatchers = metaStore.getAllStructuredDataSources().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, ( e) -> sameSource(e.getValue())));
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("Metastore with data sources: ");
            description.appendList("Metastore with sources: ", ", ", "", sourceMatchers.values());
        }

        @Override
        protected boolean matchesSafely(final MetaStore other, final Description description) {
            if (!(RecoveryTest.test(CoreMatchers.equalTo(sourceMatchers.keySet()), other.getAllStructuredDataSources().keySet(), description, "source set mismatch: "))) {
                return false;
            }
            for (final Map.Entry<String, Matcher<StructuredDataSource>> e : sourceMatchers.entrySet()) {
                final String name = e.getKey();
                if (!(RecoveryTest.test(e.getValue(), other.getSource(name), description, (("source " + name) + " mismatch: ")))) {
                    return false;
                }
            }
            return true;
        }
    }

    private static class PersistentQueryMetadataMatcher extends TypeSafeDiagnosingMatcher<PersistentQueryMetadata> {
        private final Matcher<Set<String>> sourcesNamesMatcher;

        private final Matcher<Set<String>> sinkNamesMatcher;

        private final Matcher<Schema> resultSchemaMatcher;

        private final Matcher<String> sqlMatcher;

        private final Matcher<String> stateMatcher;

        PersistentQueryMetadataMatcher(final PersistentQueryMetadata metadata) {
            this.sourcesNamesMatcher = CoreMatchers.equalTo(metadata.getSourceNames());
            this.sinkNamesMatcher = CoreMatchers.equalTo(metadata.getSinkNames());
            this.resultSchemaMatcher = CoreMatchers.equalTo(metadata.getResultSchema());
            this.sqlMatcher = CoreMatchers.equalTo(metadata.getStatementString());
            this.stateMatcher = CoreMatchers.equalTo(metadata.getState());
        }

        @Override
        public void describeTo(final Description description) {
            description.appendList("Query(", ", ", ")", Arrays.asList(sourcesNamesMatcher, sinkNamesMatcher, resultSchemaMatcher, sqlMatcher, stateMatcher));
        }

        @Override
        protected boolean matchesSafely(final PersistentQueryMetadata metadata, final Description description) {
            if (!(RecoveryTest.test(sourcesNamesMatcher, metadata.getSourceNames(), description, "source names mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(sinkNamesMatcher, metadata.getSinkNames(), description, "sink names mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(resultSchemaMatcher, metadata.getResultSchema(), description, "schema mismatch: "))) {
                return false;
            }
            if (!(RecoveryTest.test(sqlMatcher, metadata.getStatementString(), description, "sql mismatch: "))) {
                return false;
            }
            return RecoveryTest.test(stateMatcher, metadata.getState(), description, "state mismatch: ");
        }
    }

    @Test
    public void shouldRecoverCreates() {
        server1.submitCommands("CREATE STREAM A (COLUMN STRING) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B AS SELECT * FROM A;");
        shouldRecover(commands);
    }

    @Test
    public void shouldRecoverRecreates() {
        server1.submitCommands("CREATE STREAM A (C1 STRING, C2 INT) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B AS SELECT C1 FROM A;", "TERMINATE CSAS_B_0;", "DROP STREAM B;", "CREATE STREAM B AS SELECT C2 FROM A;");
        shouldRecover(commands);
    }

    @Test
    public void shouldRecoverTerminates() {
        server1.submitCommands("CREATE STREAM A (COLUMN STRING) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B AS SELECT * FROM A;", "TERMINATE CSAS_B_0;");
        shouldRecover(commands);
    }

    @Test
    public void shouldRecoverDrop() {
        server1.submitCommands("CREATE STREAM A (COLUMN STRING) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B AS SELECT * FROM A;", "TERMINATE CSAS_B_0;", "DROP STREAM B;");
        shouldRecover(commands);
    }

    @Test
    public void shouldRecoverLogWithRepeatedTerminates() {
        server1.submitCommands("CREATE STREAM A (COLUMN STRING) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B AS SELECT * FROM A;");
        server2.executeCommands();
        server1.submitCommands("TERMINATE CSAS_B_0;", "INSERT INTO B SELECT * FROM A;", "TERMINATE InsertQuery_1;");
        server2.submitCommands("TERMINATE CSAS_B_0;");
        shouldRecover(commands);
    }

    @Test
    public void shouldRecoverLogWithDropWithRacingInsert() {
        server1.submitCommands("CREATE STREAM A (COLUMN STRING) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B AS SELECT * FROM A;", "TERMINATE CSAS_B_0;");
        server2.executeCommands();
        server1.submitCommands("INSERT INTO B SELECT * FROM A;");
        server2.submitCommands("DROP STREAM B;");
        shouldRecover(commands);
    }

    @Test
    public void shouldRecoverLogWithTerminateAfterDrop() {
        topicClient.preconditionTopicExists("B");
        server1.submitCommands("CREATE STREAM A (COLUMN STRING) WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');", "CREATE STREAM B (COLUMN STRING) WITH (KAFKA_TOPIC='B', VALUE_FORMAT='JSON');");
        server2.executeCommands();
        server1.submitCommands("INSERT INTO B SELECT * FROM A;");
        server2.submitCommands("DROP STREAM B;");
        server1.submitCommands("TERMINATE InsertQuery_0;");
        shouldRecover(commands);
    }

    @Test
    public void shouldCascade4Dot1Drop() {
        commands.addAll(ImmutableList.of(new QueuedCommand(new CommandId(Type.STREAM, "A", Action.CREATE), new Command(("CREATE STREAM A (COLUMN STRING) " + "WITH (KAFKA_TOPIC='A', VALUE_FORMAT='JSON');"), Collections.emptyMap(), null)), new QueuedCommand(new CommandId(Type.STREAM, "A", Action.CREATE), new Command("CREATE STREAM B AS SELECT * FROM A;", Collections.emptyMap(), null))));
        final RecoveryTest.KsqlServer server = new RecoveryTest.KsqlServer(commands);
        server.recover();
        MatcherAssert.assertThat(server.ksqlEngine.getMetaStore().getAllStructuredDataSources().keySet(), Matchers.contains("A", "B"));
        commands.add(new QueuedCommand(new CommandId(Type.STREAM, "B", Action.DROP), new Command("DROP STREAM B;", Collections.emptyMap(), null)));
        final RecoveryTest.KsqlServer recovered = new RecoveryTest.KsqlServer(commands);
        recovered.recover();
        MatcherAssert.assertThat(recovered.ksqlEngine.getMetaStore().getAllStructuredDataSources().keySet(), Matchers.contains("A"));
    }
}

