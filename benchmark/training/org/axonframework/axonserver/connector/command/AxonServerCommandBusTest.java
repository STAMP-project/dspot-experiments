/**
 * Copyright (c) 2010-2019. Axon Framework
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
package org.axonframework.axonserver.connector.command;


import ErrorCode.COMMAND_DISPATCH_ERROR;
import com.google.protobuf.ByteString;
import io.axoniq.axonserver.grpc.MetaDataValue;
import io.axoniq.axonserver.grpc.SerializedObject;
import io.axoniq.axonserver.grpc.command.Command;
import io.axoniq.axonserver.grpc.command.CommandProviderInbound;
import io.axoniq.axonserver.grpc.command.CommandProviderOutbound;
import io.grpc.stub.StreamObserver;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.axonframework.axonserver.connector.AxonServerConfiguration;
import org.axonframework.axonserver.connector.AxonServerConnectionManager;
import org.axonframework.axonserver.connector.utils.AssertUtils;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.common.Registration;
import org.axonframework.modelling.command.ConcurrencyException;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 *
 *
 * @author Marc Gathier
 */
public class AxonServerCommandBusTest {
    private AxonServerCommandBus testSubject;

    private DummyMessagePlatformServer dummyMessagePlatformServer;

    private AxonServerConfiguration conf;

    private XStreamSerializer ser;

    private SimpleCommandBus localSegment;

    private AxonServerConnectionManager axonServerConnectionManager;

    @Test
    public void dispatch() throws Exception {
        CommandMessage<String> commandMessage = new org.axonframework.commandhandling.GenericCommandMessage("this is the payload");
        CountDownLatch waiter = new CountDownLatch(1);
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicBoolean failure = new AtomicBoolean(false);
        testSubject.dispatch(commandMessage, ((CommandCallback<String, String>) (( cm, result) -> {
            if (result.isExceptional()) {
                failure.set(true);
            } else {
                resultHolder.set(result.getPayload());
            }
            waiter.countDown();
        })));
        waiter.await();
        Assert.assertEquals(resultHolder.get(), "this is the payload");
        Assert.assertFalse(failure.get());
    }

    @Test
    public void dispatchWhenChannelThrowsAnException() throws InterruptedException {
        CommandMessage<String> commandMessage = new org.axonframework.commandhandling.GenericCommandMessage("this is the payload");
        CountDownLatch waiter = new CountDownLatch(1);
        AtomicBoolean failure = new AtomicBoolean(false);
        AtomicReference<Throwable> throwable = new AtomicReference<>();
        Mockito.when(axonServerConnectionManager.getChannel()).thenThrow(new RuntimeException("oops"));
        testSubject.dispatch(commandMessage, ((CommandCallback<String, String>) (( cm, result) -> {
            if (result.isExceptional()) {
                failure.set(true);
                throwable.set(result.exceptionResult());
            }
            waiter.countDown();
        })));
        waiter.await();
        Assert.assertTrue(failure.get());
        Assert.assertTrue(((throwable.get()) instanceof AxonServerCommandDispatchException));
        Assert.assertEquals(COMMAND_DISPATCH_ERROR.errorCode(), getErrorCode());
    }

    @Test
    public void dispatchWithError() throws Exception {
        CommandMessage<String> commandMessage = new org.axonframework.commandhandling.GenericCommandMessage("this is an error request");
        CountDownLatch waiter = new CountDownLatch(1);
        AtomicReference<String> resultHolder = new AtomicReference<>();
        AtomicBoolean failure = new AtomicBoolean(false);
        testSubject.dispatch(commandMessage, ((CommandCallback<String, String>) (( cm, result) -> {
            if (result.isExceptional()) {
                failure.set(true);
            } else {
                resultHolder.set(result.getPayload());
            }
            waiter.countDown();
        })));
        waiter.await();
        Assert.assertTrue(failure.get());
    }

    @Test
    public void dispatchWithConcurrencyException() throws Exception {
        CommandMessage<String> commandMessage = new org.axonframework.commandhandling.GenericCommandMessage("this is a concurrency issue");
        CountDownLatch waiter = new CountDownLatch(1);
        AtomicReference<CommandResultMessage> resultHolder = new AtomicReference<>();
        testSubject.dispatch(commandMessage, ((CommandCallback<String, String>) (( cm, result) -> {
            resultHolder.set(result);
            waiter.countDown();
        })));
        waiter.await();
        Assert.assertTrue(resultHolder.get().isExceptional());
        Assert.assertTrue(((resultHolder.get().exceptionResult()) instanceof ConcurrencyException));
    }

    @Test
    public void dispatchWithExceptionFromHandler() throws Exception {
        CommandMessage<String> commandMessage = new org.axonframework.commandhandling.GenericCommandMessage("give me an exception");
        CountDownLatch waiter = new CountDownLatch(1);
        AtomicReference<CommandResultMessage> resultHolder = new AtomicReference<>();
        testSubject.dispatch(commandMessage, ((CommandCallback<String, String>) (( cm, result) -> {
            resultHolder.set(result);
            waiter.countDown();
        })));
        waiter.await();
        Assert.assertTrue(resultHolder.get().isExceptional());
        Assert.assertTrue(((resultHolder.get().exceptionResult()) instanceof CommandExecutionException));
    }

    @Test
    public void subscribe() {
        Registration registration = testSubject.subscribe(String.class.getName(), ( c) -> "Done");
        AssertUtils.assertWithin(100, TimeUnit.MILLISECONDS, () -> Assert.assertNotNull(dummyMessagePlatformServer.subscriptions(String.class.getName())));
        registration.cancel();
        AssertUtils.assertWithin(100, TimeUnit.MILLISECONDS, () -> Assert.assertNull(dummyMessagePlatformServer.subscriptions(String.class.getName())));
    }

    @Test
    public void processCommand() {
        AxonServerConnectionManager mockAxonServerConnectionManager = Mockito.mock(AxonServerConnectionManager.class);
        AtomicReference<StreamObserver<CommandProviderInbound>> inboundStreamObserverRef = new AtomicReference<>();
        Mockito.doAnswer(( invocationOnMock) -> {
            inboundStreamObserverRef.set(invocationOnMock.getArgument(0));
            return new StreamObserver<CommandProviderOutbound>() {
                @Override
                public void onNext(CommandProviderOutbound commandProviderOutbound) {
                    System.out.println(commandProviderOutbound);
                }

                @Override
                public void onError(Throwable throwable) {
                }

                @Override
                public void onCompleted() {
                }
            };
        }).when(mockAxonServerConnectionManager).getCommandStream(ArgumentMatchers.any(), ArgumentMatchers.any());
        AxonServerCommandBus testSubject2 = new AxonServerCommandBus(mockAxonServerConnectionManager, conf, localSegment, ser, ( command) -> "RoutingKey", CommandPriorityCalculator.defaultCommandPriorityCalculator());
        testSubject2.subscribe(String.class.getName(), ( c) -> c.getMetaData().get("test1"));
        Command command = Command.newBuilder().setName(String.class.getName()).setPayload(SerializedObject.newBuilder().setType(String.class.getName()).setData(ByteString.copyFromUtf8("<string>test</string>"))).putMetaData("test1", MetaDataValue.newBuilder().setTextValue("Text").build()).build();
        inboundStreamObserverRef.get().onNext(CommandProviderInbound.newBuilder().setCommand(command).build());
    }

    @Test
    public void resubscribe() throws Exception {
        testSubject.subscribe(String.class.getName(), ( c) -> "Done");
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertNotNull(dummyMessagePlatformServer.subscriptions(String.class.getName())));
        dummyMessagePlatformServer.stop();
        Assert.assertNull(dummyMessagePlatformServer.subscriptions(String.class.getName()));
        dummyMessagePlatformServer.start();
        AssertUtils.assertWithin(5, TimeUnit.SECONDS, () -> Assert.assertNotNull(dummyMessagePlatformServer.subscriptions(String.class.getName())));
    }

    @Test
    public void dispatchInterceptor() {
        List<Object> results = new LinkedList<>();
        testSubject.registerDispatchInterceptor(( messages) -> ( a, b) -> {
            results.add(b.getPayload());
            return b;
        });
        testSubject.dispatch(new org.axonframework.commandhandling.GenericCommandMessage("payload"));
        Assert.assertEquals("payload", results.get(0));
        Assert.assertEquals(1, results.size());
    }

    @Test
    public void reconnectAfterConnectionLost() {
        testSubject.subscribe(String.class.getName(), ( c) -> "Done");
        AssertUtils.assertWithin(1, TimeUnit.SECONDS, () -> Assert.assertNotNull(dummyMessagePlatformServer.subscriptions(String.class.getName())));
        dummyMessagePlatformServer.onError(String.class.getName());
        AssertUtils.assertWithin(2, TimeUnit.SECONDS, () -> Assert.assertNotNull(dummyMessagePlatformServer.subscriptions(String.class.getName())));
    }
}

