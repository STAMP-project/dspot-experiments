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
package org.axonframework.commandhandling.distributed;


import MessageMonitor.MonitorCallback;
import java.util.Optional;
import junit.framework.TestCase;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.GenericCommandMessage;
import org.axonframework.commandhandling.GenericCommandResultMessage;
import org.axonframework.commandhandling.NoHandlerForCommandException;
import org.axonframework.common.Registration;
import org.axonframework.messaging.MessageHandler;
import org.axonframework.messaging.MessageHandlerInterceptor;
import org.axonframework.monitoring.MessageMonitor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class DistributedCommandBusTest {
    private DistributedCommandBus testSubject;

    @Mock
    private CommandRouter mockCommandRouter;

    @Spy
    private CommandBusConnector mockConnector = new DistributedCommandBusTest.StubCommandBusConnector();

    @Mock
    private MessageMonitor<? super CommandMessage<?>> mockMessageMonitor;

    @Mock
    private MonitorCallback mockMonitorCallback;

    @Mock
    private Member mockMember;

    @Test
    public void testDispatchWithoutCallbackWithMessageMonitor() throws Exception {
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("test");
        testSubject.dispatch(testCommandMessage);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockMessageMonitor).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback).reportSuccess();
    }

    @Test
    public void testDispatchFailingCommandWithoutCallbackWithMessageMonitor() throws Exception {
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("fail");
        testSubject.dispatch(testCommandMessage);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockMessageMonitor).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback).reportFailure(ArgumentMatchers.isA(Exception.class));
    }

    @Test
    public void testDispatchWithoutCallbackAndWithoutMessageMonitor() throws Exception {
        testSubject = DistributedCommandBus.builder().commandRouter(mockCommandRouter).connector(mockConnector).build();
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("test");
        testSubject.dispatch(testCommandMessage);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector, Mockito.never()).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockConnector).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage));
        Mockito.verify(mockMessageMonitor, Mockito.never()).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback, Mockito.never()).reportSuccess();
    }

    @Test
    public void testUnknownCommandWithoutCallbackAndWithoutMessageMonitor() throws Exception {
        testSubject = DistributedCommandBus.builder().commandRouter(mockCommandRouter).connector(mockConnector).build();
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("unknown");
        Mockito.when(mockCommandRouter.findDestination(testCommandMessage)).thenReturn(Optional.empty());
        CommandCallback callback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(testCommandMessage, callback);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector, Mockito.never()).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockConnector, Mockito.never()).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage));
        Mockito.verify(mockMessageMonitor, Mockito.never()).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback, Mockito.never()).reportSuccess();
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        TestCase.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        TestCase.assertEquals(NoHandlerForCommandException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test
    public void testDispatchWithCallbackAndMessageMonitor() throws Exception {
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("test");
        CommandCallback mockCallback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(testCommandMessage, mockCallback);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockMessageMonitor).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback).reportSuccess();
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(mockCallback).onResult(ArgumentMatchers.eq(testCommandMessage), commandResultMessageCaptor.capture());
        Assert.assertFalse(commandResultMessageCaptor.getValue().isExceptional());
        Assert.assertNull(commandResultMessageCaptor.getValue().getPayload());
    }

    @Test
    public void testUnknownCommandWithCallbackAndMessageMonitor() throws Exception {
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("test");
        Mockito.when(mockCommandRouter.findDestination(testCommandMessage)).thenReturn(Optional.empty());
        CommandCallback mockCallback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(testCommandMessage, mockCallback);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector, Mockito.never()).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockConnector, Mockito.never()).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage));
        Mockito.verify(mockMessageMonitor).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback).reportFailure(ArgumentMatchers.any());
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(mockCallback).onResult(ArgumentMatchers.eq(testCommandMessage), commandResultMessageCaptor.capture());
        TestCase.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        TestCase.assertEquals(NoHandlerForCommandException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test
    public void testDispatchFailingCommandWithCallbackAndMessageMonitor() throws Exception {
        CommandMessage<Object> testCommandMessage = GenericCommandMessage.asCommandMessage("fail");
        CommandCallback mockCallback = Mockito.mock(CommandCallback.class);
        testSubject.dispatch(testCommandMessage, mockCallback);
        Mockito.verify(mockCommandRouter).findDestination(testCommandMessage);
        Mockito.verify(mockConnector).send(ArgumentMatchers.eq(mockMember), ArgumentMatchers.eq(testCommandMessage), ArgumentMatchers.any(CommandCallback.class));
        Mockito.verify(mockMessageMonitor).onMessageIngested(ArgumentMatchers.any());
        Mockito.verify(mockMonitorCallback).reportFailure(ArgumentMatchers.isA(Exception.class));
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(mockCallback).onResult(ArgumentMatchers.eq(testCommandMessage), commandResultMessageCaptor.capture());
        TestCase.assertEquals(Exception.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    private static class StubCommandBusConnector implements CommandBusConnector {
        @Override
        public <C> void send(Member destination, CommandMessage<? extends C> command) {
            // Do nothing
        }

        @Override
        public <C, R> void send(Member destination, CommandMessage<C> command, CommandCallback<? super C, R> callback) {
            if ("fail".equals(command.getPayload())) {
                callback.onResult(command, GenericCommandResultMessage.asCommandResultMessage(new Exception("Failing")));
            } else {
                callback.onResult(command, new GenericCommandResultMessage(((R) (null))));
            }
        }

        @Override
        public Registration subscribe(String commandName, MessageHandler<? super CommandMessage<?>> handler) {
            return null;
        }

        @Override
        public Registration registerHandlerInterceptor(MessageHandlerInterceptor<? super CommandMessage<?>> handlerInterceptor) {
            return null;
        }
    }
}

