/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.wildfly.clustering.dispatcher;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.wildfly.clustering.group.Node;


/**
 * Validates behavior of default implementation of deprecated methods of {@link CommandDispatcher}.
 *
 * @author Paul Ferraro
 */
@SuppressWarnings("deprecation")
public class CommandDispatcherTestCase {
    @Test
    public void testExecuteOnNode() throws CommandDispatcherException {
        CommandDispatcher<Void> dispatcher = Mockito.mock(CommandDispatcher.class);
        try (CommandDispatcher<Void> subject = new TestCommandDispatcher(dispatcher)) {
            Command<Object, Object> command = Mockito.mock(Command.class);
            Node member = Mockito.mock(Node.class);
            Object result = new Object();
            CompletableFuture<Object> future = new CompletableFuture<>();
            Mockito.when(dispatcher.executeOnMember(ArgumentMatchers.same(command), ArgumentMatchers.same(member))).thenReturn(future);
            future.complete(result);
            CommandResponse<Object> response = subject.executeOnNode(command, member);
            try {
                Assert.assertSame(result, response.get());
            } catch (ExecutionException e) {
                Assert.fail(e.getMessage());
            }
            Exception exception = new Exception();
            Mockito.when(dispatcher.executeOnMember(ArgumentMatchers.same(command), ArgumentMatchers.same(member))).thenReturn(future);
            future.obtrudeException(exception);
            try {
                subject.executeOnNode(command, member).get();
                Assert.fail("Expected exception");
            } catch (ExecutionException e) {
                Assert.assertSame(exception, e.getCause());
            }
            exception = new CancellationException();
            future.obtrudeException(exception);
            try {
                subject.executeOnNode(command, member).get();
                Assert.fail("Expected exception");
            } catch (CancellationException e) {
                Assert.assertSame(exception, e);
            } catch (ExecutionException e) {
                Assert.fail(e.getMessage());
            }
        }
        Mockito.verify(dispatcher).close();
    }

    @Test
    public void testSubmitOnNode() throws CommandDispatcherException {
        CommandDispatcher<Void> dispatcher = Mockito.mock(CommandDispatcher.class);
        try (CommandDispatcher<Void> subject = new TestCommandDispatcher(dispatcher)) {
            Command<Object, Object> command = Mockito.mock(Command.class);
            Node member = Mockito.mock(Node.class);
            Object result = new Object();
            CompletableFuture<Object> future = new CompletableFuture<>();
            Mockito.when(dispatcher.executeOnMember(ArgumentMatchers.same(command), ArgumentMatchers.same(member))).thenReturn(future);
            future.complete(result);
            Future<Object> response = subject.submitOnNode(command, member);
            Assert.assertSame(future, response);
        }
        Mockito.verify(dispatcher).close();
    }

    @Test
    public void testExecuteOnCluster() throws CommandDispatcherException {
        CommandDispatcher<Void> dispatcher = Mockito.mock(CommandDispatcher.class);
        try (CommandDispatcher<Void> subject = new TestCommandDispatcher(dispatcher)) {
            Command<Object, Object> command = Mockito.mock(Command.class);
            Node completedMember = Mockito.mock(Node.class);
            Node exceptionMember = Mockito.mock(Node.class);
            Node cancelledMember = Mockito.mock(Node.class);
            Object result = new Object();
            Exception exception = new Exception();
            CompletableFuture<Object> completedFuture = CompletableFuture.completedFuture(result);
            CompletableFuture<Object> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(exception);
            CompletableFuture<Object> cancelledFuture = new CompletableFuture<>();
            cancelledFuture.cancel(false);
            Map<Node, CompletionStage<Object>> futures = new HashMap<>();
            futures.put(completedMember, completedFuture);
            futures.put(exceptionMember, exceptionFuture);
            futures.put(cancelledMember, cancelledFuture);
            Mockito.when(dispatcher.executeOnGroup(ArgumentMatchers.same(command))).thenReturn(futures);
            Map<Node, CommandResponse<Object>> responses = subject.executeOnCluster(command);
            Assert.assertNotNull(responses.get(completedMember));
            Assert.assertNotNull(responses.get(exceptionMember));
            Assert.assertNull(responses.get(cancelledMember));
            try {
                Assert.assertSame(result, responses.get(completedMember).get());
            } catch (ExecutionException e) {
                Assert.fail(e.getMessage());
            }
            try {
                Assert.assertSame(result, responses.get(exceptionMember).get());
                Assert.fail("Expected exception");
            } catch (ExecutionException e) {
                Assert.assertSame(exception, e.getCause());
            }
        }
        Mockito.verify(dispatcher).close();
    }

    @Test
    public void testSubmitOnCluster() throws CommandDispatcherException {
        CommandDispatcher<Void> dispatcher = Mockito.mock(CommandDispatcher.class);
        try (CommandDispatcher<Void> subject = new TestCommandDispatcher(dispatcher)) {
            Command<Object, Object> command = Mockito.mock(Command.class);
            Node completedMember = Mockito.mock(Node.class);
            Node exceptionMember = Mockito.mock(Node.class);
            Node cancelledMember = Mockito.mock(Node.class);
            Object result = new Object();
            Exception exception = new Exception();
            CompletableFuture<Object> completedFuture = CompletableFuture.completedFuture(result);
            CompletableFuture<Object> exceptionFuture = new CompletableFuture<>();
            exceptionFuture.completeExceptionally(exception);
            CompletableFuture<Object> cancelledFuture = new CompletableFuture<>();
            cancelledFuture.cancel(false);
            Map<Node, CompletionStage<Object>> futures = new HashMap<>();
            futures.put(completedMember, completedFuture);
            futures.put(exceptionMember, exceptionFuture);
            futures.put(cancelledMember, cancelledFuture);
            Mockito.when(dispatcher.executeOnGroup(ArgumentMatchers.same(command))).thenReturn(futures);
            Map<Node, Future<Object>> responses = subject.submitOnCluster(command);
            Assert.assertSame(completedFuture, responses.get(completedMember));
            Assert.assertSame(exceptionFuture, responses.get(exceptionMember));
            Assert.assertSame(cancelledFuture, responses.get(cancelledMember));
        }
        Mockito.verify(dispatcher).close();
    }
}

