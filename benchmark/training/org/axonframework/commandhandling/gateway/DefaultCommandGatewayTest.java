/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.commandhandling.gateway;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import org.axonframework.commandhandling.GenericCommandResultMessage;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.unitofwork.CurrentUnitOfWork;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.axonframework.utils.MockException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Allard Buijze
 * @author Nakul Mishra
 */
public class DefaultCommandGatewayTest {
    private DefaultCommandGateway testSubject;

    private CommandBus mockCommandBus;

    private RetryScheduler mockRetryScheduler;

    private MessageDispatchInterceptor mockCommandMessageTransformer;

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testSendWithCallbackCommandIsRetried() {
        Mockito.doAnswer(( invocation) -> {
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage(new RuntimeException(new RuntimeException())));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Mockito.when(mockRetryScheduler.scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), ArgumentMatchers.isA(List.class), ArgumentMatchers.isA(Runnable.class))).thenAnswer(new DefaultCommandGatewayTest.RescheduleCommand()).thenReturn(false);
        final AtomicReference<CommandResultMessage> actualResult = new AtomicReference<>();
        testSubject.send("Command", ((CommandCallback<Object, Object>) (( commandMessage, commandResultMessage) -> actualResult.set(commandResultMessage))));
        Mockito.verify(mockCommandMessageTransformer).handle(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockRetryScheduler, Mockito.times(2)).scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), captor.capture(), ArgumentMatchers.isA(Runnable.class));
        Mockito.verify(mockCommandBus, Mockito.times(2)).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Assert.assertTrue(actualResult.get().isExceptional());
        Assert.assertTrue(((actualResult.get().exceptionResult()) instanceof RuntimeException));
        Assert.assertEquals(1, captor.getAllValues().get(0).size());
        Assert.assertEquals(2, captor.getValue().size());
        Assert.assertEquals(2, ((Class<? extends Throwable>[]) (captor.getValue().get(0))).length);
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testSendWithoutCallbackCommandIsRetried() {
        Mockito.doAnswer(( invocation) -> {
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage(new RuntimeException(new RuntimeException())));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Mockito.when(mockRetryScheduler.scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), ArgumentMatchers.isA(List.class), ArgumentMatchers.isA(Runnable.class))).thenAnswer(new DefaultCommandGatewayTest.RescheduleCommand()).thenReturn(false);
        CompletableFuture<?> future = testSubject.send("Command");
        Mockito.verify(mockCommandMessageTransformer).handle(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockRetryScheduler, Mockito.times(2)).scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), captor.capture(), ArgumentMatchers.isA(Runnable.class));
        Mockito.verify(mockCommandBus, Mockito.times(2)).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Assert.assertEquals(1, captor.getAllValues().get(0).size());
        Assert.assertEquals(2, captor.getValue().size());
        Assert.assertEquals(2, ((Class<? extends Throwable>[]) (captor.getValue().get(0))).length);
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.isCompletedExceptionally());
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testSendWithoutCallback() throws InterruptedException, ExecutionException {
        Mockito.doAnswer(( invocation) -> {
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage("returnValue"));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        CompletableFuture<?> future = testSubject.send("Command");
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), "returnValue");
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testSendAndWaitCommandIsRetried() {
        final RuntimeException failure = new RuntimeException(new RuntimeException());
        Mockito.doAnswer(( invocation) -> {
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage(failure));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Mockito.when(mockRetryScheduler.scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), ArgumentMatchers.isA(List.class), ArgumentMatchers.isA(Runnable.class))).thenAnswer(new DefaultCommandGatewayTest.RescheduleCommand()).thenReturn(false);
        try {
            testSubject.sendAndWait("Command");
        } catch (RuntimeException rte) {
            Assert.assertSame(failure, rte);
        }
        Mockito.verify(mockCommandMessageTransformer).handle(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockRetryScheduler, Mockito.times(2)).scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), captor.capture(), ArgumentMatchers.isA(Runnable.class));
        Mockito.verify(mockCommandBus, Mockito.times(2)).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Assert.assertEquals(1, captor.getAllValues().get(0).size());
        Assert.assertEquals(2, captor.getValue().size());
        Assert.assertEquals(2, ((Class<? extends Throwable>[]) (captor.getValue().get(0))).length);
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void testSendAndWaitWithTimeoutCommandIsRetried() {
        final RuntimeException failure = new RuntimeException(new RuntimeException());
        Mockito.doAnswer(( invocation) -> {
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage(failure));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Mockito.when(mockRetryScheduler.scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), ArgumentMatchers.isA(List.class), ArgumentMatchers.isA(Runnable.class))).thenAnswer(new DefaultCommandGatewayTest.RescheduleCommand()).thenReturn(false);
        try {
            testSubject.sendAndWait("Command", 1, TimeUnit.SECONDS);
        } catch (RuntimeException rte) {
            Assert.assertSame(failure, rte);
        }
        Mockito.verify(mockCommandMessageTransformer).handle(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockRetryScheduler, Mockito.times(2)).scheduleRetry(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(RuntimeException.class), captor.capture(), ArgumentMatchers.isA(Runnable.class));
        Mockito.verify(mockCommandBus, Mockito.times(2)).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Assert.assertEquals(1, captor.getAllValues().get(0).size());
        Assert.assertEquals(2, captor.getValue().size());
        Assert.assertEquals(2, ((Class<? extends Throwable>[]) (captor.getValue().get(0))).length);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendAndWaitNullOnInterrupt() {
        Mockito.doAnswer(( invocation) -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Assert.assertNull(testSubject.sendAndWait("Hello"));
        Assert.assertTrue("Interrupt flag should be set on thread", Thread.interrupted());
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendAndWaitWithTimeoutNullOnInterrupt() {
        Mockito.doAnswer(( invocation) -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Assert.assertNull(testSubject.sendAndWait("Hello", 60, TimeUnit.SECONDS));
        Assert.assertTrue("Interrupt flag should be set on thread", Thread.interrupted());
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testSendAndWaitWithTimeoutNullOnTimeout() {
        Assert.assertNull(testSubject.sendAndWait("Hello", 10, TimeUnit.MILLISECONDS));
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.isA(org.axonframework.commandhandling.CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCorrelationDataIsAttachedToCommandAsObject() {
        UnitOfWork<CommandMessage<?>> unitOfWork = DefaultUnitOfWork.startAndGet(null);
        unitOfWork.registerCorrelationDataProvider(( message) -> Collections.singletonMap("correlationId", "test"));
        testSubject.send("Hello");
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.argThat(( x) -> "test".equals(x.getMetaData().get("correlationId"))), ArgumentMatchers.isA(CommandCallback.class));
        CurrentUnitOfWork.clear(unitOfWork);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCorrelationDataIsAttachedToCommandAsMessage() {
        final Map<String, String> data = new HashMap<>();
        data.put("correlationId", "test");
        data.put("header", "someValue");
        UnitOfWork<CommandMessage<?>> unitOfWork = DefaultUnitOfWork.startAndGet(null);
        unitOfWork.registerCorrelationDataProvider(( message) -> data);
        testSubject.send(new GenericCommandMessage("Hello", Collections.singletonMap("header", "value")));
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.argThat(( x) -> ("test".equals(x.getMetaData().get("correlationId"))) && ("value".equals(x.getMetaData().get("header")))), ArgumentMatchers.isA(CommandCallback.class));
        CurrentUnitOfWork.clear(unitOfWork);
    }

    @Test
    public void testPayloadExtractionProblemsReportedInException() throws InterruptedException, ExecutionException {
        Mockito.doAnswer(( i) -> {
            CommandCallback<String, String> callback = i.getArgument(1);
            callback.onResult(i.getArgument(0), new GenericCommandResultMessage<String>("result") {
                @Override
                public String getPayload() {
                    throw new MockException("Faking serialization problem");
                }
            });
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.any(), ArgumentMatchers.any());
        CompletableFuture<String> actual = testSubject.send("command");
        Assert.assertTrue(actual.isDone());
        Assert.assertTrue(actual.isCompletedExceptionally());
        Assert.assertEquals("Faking serialization problem", actual.exceptionally(Throwable::getMessage).get());
    }

    private static class RescheduleCommand implements Answer<Boolean> {
        @Override
        public Boolean answer(InvocationOnMock invocation) {
            ((Runnable) (invocation.getArguments()[3])).run();
            return true;
        }
    }
}

