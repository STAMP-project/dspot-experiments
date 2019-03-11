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


import CommandId.Action;
import CommandId.Type;
import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import KsqlConfig.KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG;
import io.confluent.ksql.parser.KsqlParser.PreparedStatement;
import io.confluent.ksql.parser.tree.Statement;
import io.confluent.ksql.rest.server.CommandTopic;
import io.confluent.ksql.util.KsqlConfig;
import io.confluent.ksql.util.KsqlException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.RecordBatch;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsEqual;
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
public class CommandStoreTest {
    private static final String COMMAND_TOPIC = "command";

    private static final TopicPartition COMMAND_TOPIC_PARTITION = new TopicPartition(CommandStoreTest.COMMAND_TOPIC, 0);

    private static final KsqlConfig KSQL_CONFIG = new KsqlConfig(Collections.singletonMap(KSQL_PERSISTENT_QUERY_NAME_PREFIX_CONFIG, "foo"));

    private static final Map<String, Object> OVERRIDE_PROPERTIES = Collections.singletonMap(AUTO_OFFSET_RESET_CONFIG, "earliest");

    private static final Duration TIMEOUT = Duration.ofMillis(1000);

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private static final String statementText = "test-statement";

    private static final Duration NEW_CMDS_TIMEOUT = Duration.ofSeconds(30);

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Mock
    private SequenceNumberFutureStore sequenceNumberFutureStore;

    @Mock
    private CompletableFuture<Void> future;

    @Mock
    private CommandTopic commandTopic;

    @Mock
    private Statement statement;

    @Mock
    private CommandIdAssigner commandIdAssigner;

    private PreparedStatement<?> preparedStatement;

    private final CommandId commandId = new CommandId(Type.STREAM, "foo", Action.CREATE);

    private final Command command = new Command(CommandStoreTest.statementText, Collections.emptyMap(), Collections.emptyMap());

    private final RecordMetadata recordMetadata = new RecordMetadata(CommandStoreTest.COMMAND_TOPIC_PARTITION, 0, 0, RecordBatch.NO_TIMESTAMP, 0L, 0, 0);

    private CommandStore commandStore;

    @Test
    public void shouldFailEnqueueIfCommandWithSameIdRegistered() {
        // Given:
        Mockito.when(commandIdAssigner.getCommandId(ArgumentMatchers.any())).thenReturn(commandId);
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
        expectedException.expect(IllegalStateException.class);
        // When:
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
    }

    @Test
    public void shouldCleanupCommandStatusOnProduceError() {
        // Given:
        Mockito.when(commandTopic.send(ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(new RuntimeException("oops")).thenReturn(recordMetadata);
        expectedException.expect(KsqlException.class);
        expectedException.expectMessage("Could not write the statement 'test-statement' into the command topic.");
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
        // When:
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
    }

    @Test
    public void shouldEnqueueNewAfterHandlingExistingCommand() {
        // Given:
        Mockito.when(commandIdAssigner.getCommandId(ArgumentMatchers.any())).thenReturn(commandId);
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
        commandStore.getNewCommands(CommandStoreTest.NEW_CMDS_TIMEOUT);
        // Should:
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
    }

    @Test
    public void shouldRegisterBeforeDistributeAndReturnStatusOnGetNewCommands() {
        // Given:
        Mockito.when(commandIdAssigner.getCommandId(ArgumentMatchers.any())).thenReturn(commandId);
        Mockito.when(commandTopic.send(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            final QueuedCommand queuedCommand = commandStore.getNewCommands(NEW_CMDS_TIMEOUT).get(0);
            assertThat(queuedCommand.getCommandId(), equalTo(commandId));
            assertThat(queuedCommand.getStatus().isPresent(), equalTo(true));
            assertThat(queuedCommand.getStatus().get().getStatus().getStatus(), equalTo(CommandStatus.Status.QUEUED));
            return recordMetadata;
        });
        // When:
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
        // Then:
        Mockito.verify(commandTopic).send(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void shouldFilterNullCommands() {
        // Given:
        final ConsumerRecords<CommandId, Command> records = CommandStoreTest.buildRecords(commandId, null, commandId, command);
        Mockito.when(commandTopic.getNewCommands(ArgumentMatchers.any())).thenReturn(records);
        // When:
        final List<QueuedCommand> commands = commandStore.getNewCommands(CommandStoreTest.NEW_CMDS_TIMEOUT);
        // Then:
        MatcherAssert.assertThat(commands, Matchers.hasSize(1));
        MatcherAssert.assertThat(commands.get(0).getCommandId(), IsEqual.equalTo(commandId));
        MatcherAssert.assertThat(commands.get(0).getCommand(), IsEqual.equalTo(command));
    }

    @Test
    public void shouldDistributeCommand() {
        Mockito.when(commandIdAssigner.getCommandId(ArgumentMatchers.any())).thenReturn(commandId);
        Mockito.when(commandTopic.send(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(recordMetadata);
        // When:
        commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
        // Then:
        Mockito.verify(commandTopic).send(ArgumentMatchers.same(commandId), ArgumentMatchers.any());
    }

    @Test
    public void shouldIncludeCommandSequenceNumberInSuccessfulQueuedCommandStatus() {
        // When:
        final QueuedCommandStatus commandStatus = commandStore.enqueueCommand(preparedStatement, CommandStoreTest.KSQL_CONFIG, CommandStoreTest.OVERRIDE_PROPERTIES);
        // Then:
        MatcherAssert.assertThat(commandStatus.getCommandSequenceNumber(), IsEqual.equalTo(recordMetadata.offset()));
    }

    @Test
    public void shouldWaitOnSequenceNumberFuture() throws Exception {
        // When:
        commandStore.ensureConsumedPast(2, CommandStoreTest.TIMEOUT);
        // Then:
        Mockito.verify(future).get(ArgumentMatchers.eq(CommandStoreTest.TIMEOUT.toMillis()), ArgumentMatchers.eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void shouldThrowExceptionOnTimeout() throws Exception {
        // Given:
        Mockito.when(future.get(ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenThrow(new TimeoutException());
        expectedException.expect(TimeoutException.class);
        expectedException.expectMessage(String.format("Timeout reached while waiting for command sequence number of 2. (Timeout: %d ms)", CommandStoreTest.TIMEOUT.toMillis()));
        // When:
        commandStore.ensureConsumedPast(2, CommandStoreTest.TIMEOUT);
    }

    @Test
    public void shouldCompleteFuturesWhenGettingNewCommands() {
        // Given:
        Mockito.when(commandTopic.getCommandTopicConsumerPosition()).thenReturn(22L);
        // When:
        commandStore.getNewCommands(CommandStoreTest.NEW_CMDS_TIMEOUT);
        // Then:
        final InOrder inOrder = Mockito.inOrder(sequenceNumberFutureStore, commandTopic);
        inOrder.verify(sequenceNumberFutureStore).completeFuturesUpToAndIncludingSequenceNumber(ArgumentMatchers.eq(21L));
        inOrder.verify(commandTopic).getNewCommands(ArgumentMatchers.any());
    }

    @Test
    public void shouldComputeNotEmptyCorrectly() {
        // Given:
        Mockito.when(commandTopic.getEndOffset()).thenReturn(1L);
        // When/Then:
        MatcherAssert.assertThat(commandStore.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void shouldComputeEmptyCorrectly() {
        // Given:
        Mockito.when(commandTopic.getEndOffset()).thenReturn(0L);
        // When/Then:
        MatcherAssert.assertThat(commandStore.isEmpty(), CoreMatchers.is(true));
    }

    @Test
    public void shouldWakeUp() {
        // When:
        commandStore.wakeup();
        // Then:
        Mockito.verify(commandTopic).wakeup();
    }

    @Test
    public void shouldClose() {
        // When:
        commandStore.close();
        // Then:
        Mockito.verify(commandTopic).close();
    }
}

