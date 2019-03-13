/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.protocols.raft.session.impl;


import RaftResponse.Status.OK;
import io.atomix.primitive.operation.OperationId;
import io.atomix.primitive.session.SessionId;
import io.atomix.protocols.raft.RaftException;
import io.atomix.protocols.raft.TestPrimitiveType;
import io.atomix.protocols.raft.protocol.CommandRequest;
import io.atomix.protocols.raft.protocol.CommandResponse;
import io.atomix.protocols.raft.protocol.QueryRequest;
import io.atomix.protocols.raft.protocol.QueryResponse;
import io.atomix.utils.concurrent.Scheduled;
import io.atomix.utils.concurrent.ThreadContext;
import java.time.Duration;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Client session submitter test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class RaftSessionInvokerTest {
    private static final OperationId COMMAND = OperationId.command("command");

    private static final OperationId QUERY = OperationId.query("query");

    /**
     * Tests submitting a command to the cluster.
     */
    @Test
    public void testSubmitCommand() throws Throwable {
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.command(ArgumentMatchers.any(CommandRequest.class))).thenReturn(CompletableFuture.completedFuture(CommandResponse.builder().withStatus(OK).withIndex(10).withResult("Hello world!".getBytes()).build()));
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(connection, Mockito.mock(RaftSessionConnection.class), state, new RaftSessionSequencer(state), manager, threadContext);
        Assert.assertArrayEquals(submitter.invoke(operation(RaftSessionInvokerTest.COMMAND, HeapBytes.EMPTY)).get(), "Hello world!".getBytes());
        Assert.assertEquals(1, state.getCommandRequest());
        Assert.assertEquals(1, state.getCommandResponse());
        Assert.assertEquals(10, state.getResponseIndex());
    }

    /**
     * Test resequencing a command response.
     */
    @Test
    public void testResequenceCommand() throws Throwable {
        CompletableFuture<CommandResponse> future1 = new CompletableFuture<>();
        CompletableFuture<CommandResponse> future2 = new CompletableFuture<>();
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.command(ArgumentMatchers.any(CommandRequest.class))).thenReturn(future1).thenReturn(future2);
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(connection, Mockito.mock(RaftSessionConnection.class), state, new RaftSessionSequencer(state), manager, threadContext);
        CompletableFuture<byte[]> result1 = submitter.invoke(operation(RaftSessionInvokerTest.COMMAND));
        CompletableFuture<byte[]> result2 = submitter.invoke(operation(RaftSessionInvokerTest.COMMAND));
        future2.complete(CommandResponse.builder().withStatus(OK).withIndex(10).withResult("Hello world again!".getBytes()).build());
        Assert.assertEquals(2, state.getCommandRequest());
        Assert.assertEquals(0, state.getCommandResponse());
        Assert.assertEquals(1, state.getResponseIndex());
        Assert.assertFalse(result1.isDone());
        Assert.assertFalse(result2.isDone());
        future1.complete(CommandResponse.builder().withStatus(OK).withIndex(9).withResult("Hello world!".getBytes()).build());
        Assert.assertTrue(result1.isDone());
        Assert.assertTrue(Arrays.equals(result1.get(), "Hello world!".getBytes()));
        Assert.assertTrue(result2.isDone());
        Assert.assertTrue(Arrays.equals(result2.get(), "Hello world again!".getBytes()));
        Assert.assertEquals(2, state.getCommandRequest());
        Assert.assertEquals(2, state.getCommandResponse());
        Assert.assertEquals(10, state.getResponseIndex());
    }

    /**
     * Tests submitting a query to the cluster.
     */
    @Test
    public void testSubmitQuery() throws Throwable {
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.query(ArgumentMatchers.any(QueryRequest.class))).thenReturn(CompletableFuture.completedFuture(QueryResponse.builder().withStatus(OK).withIndex(10).withResult("Hello world!".getBytes()).build()));
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(Mockito.mock(RaftSessionConnection.class), connection, state, new RaftSessionSequencer(state), manager, threadContext);
        Assert.assertTrue(Arrays.equals(submitter.invoke(operation(RaftSessionInvokerTest.QUERY)).get(), "Hello world!".getBytes()));
        Assert.assertEquals(10, state.getResponseIndex());
    }

    /**
     * Tests resequencing a query response.
     */
    @Test
    public void testResequenceQuery() throws Throwable {
        CompletableFuture<QueryResponse> future1 = new CompletableFuture<>();
        CompletableFuture<QueryResponse> future2 = new CompletableFuture<>();
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.query(ArgumentMatchers.any(QueryRequest.class))).thenReturn(future1).thenReturn(future2);
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(Mockito.mock(RaftSessionConnection.class), connection, state, new RaftSessionSequencer(state), manager, threadContext);
        CompletableFuture<byte[]> result1 = submitter.invoke(operation(RaftSessionInvokerTest.QUERY));
        CompletableFuture<byte[]> result2 = submitter.invoke(operation(RaftSessionInvokerTest.QUERY));
        future2.complete(QueryResponse.builder().withStatus(OK).withIndex(10).withResult("Hello world again!".getBytes()).build());
        Assert.assertEquals(1, state.getResponseIndex());
        Assert.assertFalse(result1.isDone());
        Assert.assertFalse(result2.isDone());
        future1.complete(QueryResponse.builder().withStatus(OK).withIndex(9).withResult("Hello world!".getBytes()).build());
        Assert.assertTrue(result1.isDone());
        Assert.assertTrue(Arrays.equals(result1.get(), "Hello world!".getBytes()));
        Assert.assertTrue(result2.isDone());
        Assert.assertTrue(Arrays.equals(result2.get(), "Hello world again!".getBytes()));
        Assert.assertEquals(10, state.getResponseIndex());
    }

    /**
     * Tests skipping over a failed query attempt.
     */
    @Test
    public void testSkippingOverFailedQuery() throws Throwable {
        CompletableFuture<QueryResponse> future1 = new CompletableFuture<>();
        CompletableFuture<QueryResponse> future2 = new CompletableFuture<>();
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.query(ArgumentMatchers.any(QueryRequest.class))).thenReturn(future1).thenReturn(future2);
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(Mockito.mock(RaftSessionConnection.class), connection, state, new RaftSessionSequencer(state), manager, threadContext);
        CompletableFuture<byte[]> result1 = submitter.invoke(operation(RaftSessionInvokerTest.QUERY));
        CompletableFuture<byte[]> result2 = submitter.invoke(operation(RaftSessionInvokerTest.QUERY));
        Assert.assertEquals(1, state.getResponseIndex());
        Assert.assertFalse(result1.isDone());
        Assert.assertFalse(result2.isDone());
        future1.completeExceptionally(new RaftException.QueryFailure("failure"));
        future2.complete(QueryResponse.builder().withStatus(OK).withIndex(10).withResult("Hello world!".getBytes()).build());
        Assert.assertTrue(result1.isCompletedExceptionally());
        Assert.assertTrue(result2.isDone());
        Assert.assertTrue(Arrays.equals(result2.get(), "Hello world!".getBytes()));
        Assert.assertEquals(10, state.getResponseIndex());
    }

    /**
     * Tests that the client's session is expired when an UnknownSessionException is received from the cluster.
     */
    @Test
    public void testExpireSessionOnCommandFailure() throws Throwable {
        CompletableFuture<CommandResponse> future = new CompletableFuture<>();
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.command(ArgumentMatchers.any(CommandRequest.class))).thenReturn(future);
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(connection, Mockito.mock(RaftSessionConnection.class), state, new RaftSessionSequencer(state), manager, threadContext);
        CompletableFuture<byte[]> result = submitter.invoke(operation(RaftSessionInvokerTest.COMMAND));
        Assert.assertEquals(1, state.getResponseIndex());
        Assert.assertFalse(result.isDone());
        future.completeExceptionally(new RaftException.UnknownSession("unknown session"));
        Assert.assertTrue(result.isCompletedExceptionally());
    }

    /**
     * Tests that the client's session is expired when an UnknownSessionException is received from the cluster.
     */
    @Test
    public void testExpireSessionOnQueryFailure() throws Throwable {
        CompletableFuture<QueryResponse> future = new CompletableFuture<>();
        RaftSessionConnection connection = Mockito.mock(RaftSessionConnection.class);
        Mockito.when(connection.query(ArgumentMatchers.any(QueryRequest.class))).thenReturn(future);
        RaftSessionState state = new RaftSessionState("test", SessionId.from(1), UUID.randomUUID().toString(), TestPrimitiveType.instance(), 1000);
        RaftSessionManager manager = Mockito.mock(RaftSessionManager.class);
        ThreadContext threadContext = new RaftSessionInvokerTest.TestContext();
        RaftSessionInvoker submitter = new RaftSessionInvoker(Mockito.mock(RaftSessionConnection.class), connection, state, new RaftSessionSequencer(state), manager, threadContext);
        CompletableFuture<byte[]> result = submitter.invoke(operation(RaftSessionInvokerTest.QUERY));
        Assert.assertEquals(1, state.getResponseIndex());
        Assert.assertFalse(result.isDone());
        future.completeExceptionally(new RaftException.UnknownSession("unknown session"));
        Assert.assertTrue(result.isCompletedExceptionally());
    }

    /**
     * Test thread context.
     */
    private static class TestContext implements ThreadContext {
        @Override
        public Scheduled schedule(Duration delay, Runnable callback) {
            return null;
        }

        @Override
        public Scheduled schedule(Duration initialDelay, Duration interval, Runnable callback) {
            return null;
        }

        @Override
        public void close() {
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }

        @Override
        public boolean isBlocked() {
            return false;
        }

        @Override
        public void block() {
        }

        @Override
        public void unblock() {
        }
    }
}

