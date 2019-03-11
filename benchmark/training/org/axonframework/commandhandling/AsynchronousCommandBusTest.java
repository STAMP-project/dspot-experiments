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
package org.axonframework.commandhandling;


import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import org.axonframework.messaging.InterceptorChain;
import org.axonframework.messaging.MessageDispatchInterceptor;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.messaging.unitofwork.UnitOfWork;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 *
 *
 * @author Allard Buijze
 */
public class AsynchronousCommandBusTest {
    private MessageHandlerInterceptor handlerInterceptor;

    private MessageDispatchInterceptor dispatchInterceptor;

    private MessageHandler<CommandMessage<?>> commandHandler;

    private ExecutorService executorService;

    private AsynchronousCommandBus testSubject;

    @SuppressWarnings("unchecked")
    @Test
    public void testDispatchWithCallback() throws Exception {
        testSubject.subscribe(Object.class.getName(), commandHandler);
        CommandCallback<Object, Object> mockCallback = Mockito.mock(CommandCallback.class);
        CommandMessage<Object> command = GenericCommandMessage.asCommandMessage(new Object());
        testSubject.dispatch(command, mockCallback);
        InOrder inOrder = Mockito.inOrder(mockCallback, executorService, commandHandler, dispatchInterceptor, handlerInterceptor);
        inOrder.verify(dispatchInterceptor).handle(ArgumentMatchers.isA(CommandMessage.class));
        inOrder.verify(executorService).execute(ArgumentMatchers.isA(Runnable.class));
        inOrder.verify(handlerInterceptor).handle(ArgumentMatchers.isA(UnitOfWork.class), ArgumentMatchers.isA(InterceptorChain.class));
        inOrder.verify(commandHandler).handle(ArgumentMatchers.isA(CommandMessage.class));
        ArgumentCaptor<CommandMessage<Object>> commandCaptor = ArgumentCaptor.forClass(CommandMessage.class);
        ArgumentCaptor<CommandResultMessage<Object>> responseCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        inOrder.verify(mockCallback).onResult(commandCaptor.capture(), responseCaptor.capture());
        Assert.assertEquals(command, commandCaptor.getValue());
        Assert.assertNull(responseCaptor.getValue().getPayload());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testDispatchWithoutCallback() throws Exception {
        MessageHandler<CommandMessage<?>> commandHandler = Mockito.mock(MessageHandler.class);
        testSubject.subscribe(Object.class.getName(), commandHandler);
        testSubject.dispatch(GenericCommandMessage.asCommandMessage(new Object()));
        InOrder inOrder = Mockito.inOrder(executorService, commandHandler, dispatchInterceptor, handlerInterceptor);
        inOrder.verify(dispatchInterceptor).handle(ArgumentMatchers.isA(CommandMessage.class));
        inOrder.verify(executorService).execute(ArgumentMatchers.isA(Runnable.class));
        inOrder.verify(handlerInterceptor).handle(ArgumentMatchers.isA(UnitOfWork.class), ArgumentMatchers.isA(InterceptorChain.class));
        inOrder.verify(commandHandler).handle(ArgumentMatchers.isA(CommandMessage.class));
    }

    @Test
    public void testShutdown_ExecutorServiceUsed() {
        testSubject.shutdown();
        Mockito.verify(executorService).shutdown();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testExceptionIsThrownWhenNoHandlerIsRegistered() {
        CommandCallback callback = Mockito.mock(CommandCallback.class);
        CommandMessage<Object> command = GenericCommandMessage.asCommandMessage("test");
        testSubject.dispatch(command, callback);
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.eq(command), commandResultMessageCaptor.capture());
        Assert.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        Assert.assertEquals(NoHandlerForCommandException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test
    public void testShutdown_ExecutorUsed() {
        Executor executor = Mockito.mock(Executor.class);
        AsynchronousCommandBus.builder().executor(executor).build().shutdown();
        Mockito.verify(executor, Mockito.never()).execute(ArgumentMatchers.any(Runnable.class));
    }
}

