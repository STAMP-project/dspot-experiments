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


import io.confluent.ksql.engine.KsqlEngine;
import io.confluent.ksql.rest.util.ClusterTerminator;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CommandRunnerTest {
    @Mock
    private StatementExecutor statementExecutor;

    @Mock
    private CommandStore commandStore;

    @Mock
    private KsqlEngine ksqlEngine;

    @Mock
    private ClusterTerminator clusterTerminator;

    @Mock
    private Command command;

    @Mock
    private Command clusterTerminate;

    @Mock
    private QueuedCommand queuedCommand1;

    @Mock
    private QueuedCommand queuedCommand2;

    @Mock
    private QueuedCommand queuedCommand3;

    @Mock
    private ExecutorService executor;

    private CommandRunner commandRunner;

    @Test
    public void shouldRunThePriorCommandsCorrectly() {
        // Given:
        givenQueuedCommands(queuedCommand1, queuedCommand2, queuedCommand3);
        // When:
        commandRunner.processPriorCommands();
        // Then:
        final InOrder inOrder = Mockito.inOrder(statementExecutor);
        inOrder.verify(statementExecutor).handleRestore(ArgumentMatchers.eq(queuedCommand1));
        inOrder.verify(statementExecutor).handleRestore(ArgumentMatchers.eq(queuedCommand2));
        inOrder.verify(statementExecutor).handleRestore(ArgumentMatchers.eq(queuedCommand3));
    }

    @Test
    public void shouldRunThePriorCommandsWithTerminateCorrectly() {
        // Given:
        givenQueuedCommands(queuedCommand1);
        Mockito.when(queuedCommand1.getCommand()).thenReturn(clusterTerminate);
        // When:
        commandRunner.processPriorCommands();
        // Then:
        Mockito.verify(ksqlEngine).stopAcceptingStatements();
        Mockito.verify(commandStore).close();
        Mockito.verify(clusterTerminator).terminateCluster(ArgumentMatchers.anyList());
        Mockito.verify(statementExecutor, Mockito.never()).handleRestore(ArgumentMatchers.any());
    }

    @Test
    public void shouldEarlyOutIfRestoreContainsTerminate() {
        // Given:
        givenQueuedCommands(queuedCommand1, queuedCommand2, queuedCommand3);
        Mockito.when(queuedCommand2.getCommand()).thenReturn(clusterTerminate);
        // When:
        commandRunner.processPriorCommands();
        // Then:
        Mockito.verify(ksqlEngine).stopAcceptingStatements();
        Mockito.verify(statementExecutor, Mockito.never()).handleRestore(ArgumentMatchers.any());
    }

    @Test
    public void shouldPullAndRunStatements() {
        // Given:
        givenQueuedCommands(queuedCommand1, queuedCommand2, queuedCommand3);
        // When:
        commandRunner.fetchAndRunCommands();
        // Then:
        final InOrder inOrder = Mockito.inOrder(statementExecutor);
        inOrder.verify(statementExecutor).handleStatement(queuedCommand1);
        inOrder.verify(statementExecutor).handleStatement(queuedCommand2);
        inOrder.verify(statementExecutor).handleStatement(queuedCommand3);
    }

    @Test
    public void shouldEarlyOutIfNewCommandsContainsTerminate() {
        // Given:
        givenQueuedCommands(queuedCommand1, queuedCommand2, queuedCommand3);
        Mockito.when(queuedCommand2.getCommand()).thenReturn(clusterTerminate);
        // When:
        commandRunner.fetchAndRunCommands();
        // Then:
        Mockito.verify(ksqlEngine).stopAcceptingStatements();
        Mockito.verify(statementExecutor, Mockito.never()).handleRestore(queuedCommand1);
        Mockito.verify(statementExecutor, Mockito.never()).handleRestore(queuedCommand2);
        Mockito.verify(statementExecutor, Mockito.never()).handleRestore(queuedCommand3);
    }

    @Test
    public void shouldEarlyOutOnShutdown() {
        // Given:
        givenQueuedCommands(queuedCommand1, queuedCommand2);
        Mockito.doAnswer(closeRunner()).when(statementExecutor).handleStatement(queuedCommand1);
        // When:
        commandRunner.fetchAndRunCommands();
        // Then:
        Mockito.verify(statementExecutor, Mockito.never()).handleRestore(queuedCommand2);
    }

    @Test
    public void shouldNotBlockIndefinitelyPollingForNewCommands() {
        // When:
        commandRunner.fetchAndRunCommands();
        // Then:
        Mockito.verify(commandStore).getNewCommands(MockitoHamcrest.argThat(Matchers.not(Duration.ofMillis(Long.MAX_VALUE))));
    }

    @Test
    public void shouldSubmitTaskOnStart() {
        // When:
        commandRunner.start();
        // Then:
        final InOrder inOrder = Mockito.inOrder(executor);
        inOrder.verify(executor).submit(ArgumentMatchers.any(Runnable.class));
        inOrder.verify(executor).shutdown();
    }

    @Test
    public void shouldCloseTheCommandRunnerCorrectly() throws Exception {
        // When:
        commandRunner.close();
        // Then:
        final InOrder inOrder = Mockito.inOrder(executor, commandStore);
        inOrder.verify(commandStore).wakeup();
        inOrder.verify(executor).awaitTermination(ArgumentMatchers.anyLong(), ArgumentMatchers.any());
        inOrder.verify(commandStore).close();
    }

    @Test(expected = RuntimeException.class)
    public void shouldThrowExceptionIfCannotCloseCommandStore() {
        // Given:
        Mockito.doThrow(RuntimeException.class).when(commandStore).close();
        // When:
        commandRunner.close();
    }
}

