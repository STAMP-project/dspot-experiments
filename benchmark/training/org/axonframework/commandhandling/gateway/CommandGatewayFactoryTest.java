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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandCallback;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.CommandMessage;
import org.axonframework.commandhandling.CommandResultMessage;
import org.axonframework.commandhandling.GenericCommandResultMessage;
import org.axonframework.common.lock.DeadlockException;
import org.axonframework.messaging.MetaData;
import org.axonframework.messaging.annotation.MetaDataValue;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.messaging.unitofwork.DefaultUnitOfWork;
import org.axonframework.messaging.unitofwork.UnitOfWork;
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
@SuppressWarnings({ "unchecked", "ThrowableResultOfMethodCallIgnored" })
public class CommandGatewayFactoryTest {
    private CommandBus mockCommandBus;

    private CommandGatewayFactory testSubject;

    private CommandGatewayFactoryTest.CompleteGateway gateway;

    private RetryScheduler mockRetryScheduler;

    private CommandCallback callback;

    // (timeout = 2000)
    @Test
    public void testGatewayFireAndForget() {
        Mockito.doAnswer(( i) -> {
            ((CommandCallback) (i.getArguments()[1])).onResult(((CommandMessage) (i.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage(null));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        testSubject.registerCommandCallback(callback, ResponseTypes.instanceOf(Void.class));
        final Object metaTest = new Object();
        gateway.fireAndForget("Command", null, metaTest, "value");
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.argThat(( x) -> ((x.getMetaData().get("test")) == metaTest) && ("value".equals(x.getMetaData().get("key")))), ArgumentMatchers.isA(RetryingCallback.class));
        // check that the callback is invoked, despite the null return value
        Mockito.verify(callback).onResult(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.any());
    }

    @Test(timeout = 2000)
    public void testGatewayFireAndForgetWithoutRetryScheduler() {
        final Object metaTest = new Object();
        CommandGatewayFactory testSubject = CommandGatewayFactory.builder().commandBus(mockCommandBus).build();
        CommandGatewayFactoryTest.CompleteGateway gateway = testSubject.createGateway(CommandGatewayFactoryTest.CompleteGateway.class);
        gateway.fireAndForget("Command", MetaData.from(Collections.singletonMap("otherKey", "otherVal")), metaTest, "value");
        // in this case, no callback is used
        Mockito.verify(mockCommandBus).dispatch(ArgumentMatchers.argThat(( x) -> (((x.getMetaData().get("test")) == metaTest) && ("otherVal".equals(x.getMetaData().get("otherKey")))) && ("value".equals(x.getMetaData().get("key")))));
    }

    @Test(timeout = 2000)
    public void testGatewayTimeout() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        Mockito.doAnswer(new CommandGatewayFactoryTest.CountDown(cdl)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Thread t = new Thread(() -> gateway.fireAndWait("Command"));
        t.start();
        Assert.assertTrue("Expected command bus to be invoked", cdl.await(1, TimeUnit.SECONDS));
        Assert.assertTrue(t.isAlive());
        t.interrupt();
    }

    @Test(timeout = 2000)
    public void testGatewayWithReturnValueReturns() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        CommandResultMessage<String> returnValue = GenericCommandResultMessage.asCommandResultMessage("ReturnValue");
        Mockito.doAnswer(new CommandGatewayFactoryTest.Success(cdl, returnValue)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Thread t = new Thread(() -> result.set(gateway.waitForReturnValue("Command")));
        t.start();
        Assert.assertTrue("Expected command bus to be invoked", cdl.await(1, TimeUnit.SECONDS));
        t.join();
        Assert.assertEquals("ReturnValue", result.get());
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(returnValue));
    }

    @Test(timeout = 2000)
    public void testGatewayWithReturnValueUndeclaredException() throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(cdl, new CommandGatewayFactoryTest.ExpectedException())).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.waitForReturnValue("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        Assert.assertTrue("Expected command bus to be invoked", cdl.await(1, TimeUnit.SECONDS));
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertTrue(((error.get()) instanceof CommandExecutionException));
        Assert.assertTrue(((error.get().getCause()) instanceof CommandGatewayFactoryTest.ExpectedException));
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        Assert.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        Assert.assertEquals(CommandGatewayFactoryTest.ExpectedException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test(timeout = 2000)
    public void testGatewayWithReturnValueInterrupted() throws InterruptedException {
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.waitForReturnValue("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.interrupt();
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertNull(error.get());
    }

    @Test
    public void testGatewayWithReturnValueRuntimeException() {
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        RuntimeException runtimeException = new RuntimeException();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(null, runtimeException)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        try {
            result.set(gateway.waitForReturnValue("Command"));
        } catch (Throwable e) {
            error.set(e);
        }
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertSame("Expected exact instance of RunTimeException being propagated", runtimeException, error.get());
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        Assert.assertEquals(RuntimeException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test(timeout = 2000)
    public void testGatewayWaitForExceptionInterrupted() throws InterruptedException {
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                gateway.waitForException("Command");
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.interrupt();
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertTrue(((error.get()) instanceof InterruptedException));
    }

    @Test(timeout = 2000)
    public void testFireAndWaitWithTimeoutParameterReturns() throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        Mockito.doAnswer(new CommandGatewayFactoryTest.Success(cdl, GenericCommandResultMessage.asCommandResultMessage("OK!"))).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                gateway.fireAndWaitWithTimeoutParameter("Command", 1, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        Assert.assertTrue(cdl.await(1, TimeUnit.SECONDS));
        t.interrupt();
        // the return type is void, so return value is ignored
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertNull("Did not expect exception", error.get());
    }

    @Test(timeout = 2000)
    public void testFireAndWaitWithTimeoutParameterTimeout() throws InterruptedException {
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                gateway.fireAndWaitWithTimeoutParameter("Command", 1, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertNull("Did not expect exception", error.get());
    }

    @Test(timeout = 2000)
    public void testFireAndWaitWithTimeoutParameterTimeoutException() throws InterruptedException {
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                gateway.fireAndWaitWithTimeoutParameterAndException("Command", 1, TimeUnit.MILLISECONDS);
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertTrue(((error.get()) instanceof TimeoutException));
    }

    @Test(timeout = 2000)
    public void testFireAndWaitWithTimeoutParameterInterrupted() throws InterruptedException {
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                gateway.fireAndWaitWithTimeoutParameter("Command", 1, TimeUnit.SECONDS);
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.interrupt();
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertNull("Did not expect exception", error.get());
    }

    @Test(timeout = 2000)
    public void testFireAndWaitForCheckedException() throws InterruptedException {
        CountDownLatch cdl = new CountDownLatch(1);
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(cdl, new CommandGatewayFactoryTest.ExpectedException())).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        final AtomicReference<String> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                gateway.fireAndWaitForCheckedException("Command");
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        Assert.assertTrue(cdl.await(1, TimeUnit.SECONDS));
        t.join();
        Assert.assertNull("Did not expect ReturnValue", result.get());
        Assert.assertTrue(((error.get()) instanceof CommandGatewayFactoryTest.ExpectedException));
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        Assert.assertTrue(commandResultMessageCaptor.getValue().isExceptional());
        Assert.assertEquals(CommandGatewayFactoryTest.ExpectedException.class, commandResultMessageCaptor.getValue().exceptionResult().getClass());
    }

    @Test(timeout = 2000)
    public void testFireAndGetFuture() throws InterruptedException {
        final AtomicReference<Future<Object>> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.fireAndGetFuture("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        Assert.assertNotNull("Expected to get a Future return value", result.get());
        Assert.assertNull(error.get());
    }

    @Test(timeout = 2000)
    public void testFireAndGetCompletableFuture() throws InterruptedException {
        final AtomicReference<CompletableFuture<Object>> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.fireAndGetCompletableFuture("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        Assert.assertNotNull("Expected to get a Future return value", result.get());
        Assert.assertNull(error.get());
    }

    @Test(timeout = 2000)
    public void testFireAndGetFutureWithTimeout() throws Throwable {
        final AtomicReference<Future<Object>> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.futureWithTimeout("Command", 100, TimeUnit.SECONDS));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        if ((error.get()) != null) {
            throw error.get();
        }
        Assert.assertNotNull("Expected to get a Future return value", result.get());
    }

    @Test(timeout = 2000)
    public void testFireAndGetCompletionStageWithTimeout() throws Throwable {
        final AtomicReference<CompletionStage<Object>> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.fireAndGetCompletionStage("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        if ((error.get()) != null) {
            throw error.get();
        }
        Assert.assertNotNull("Expected to get a CompletionStage return value", result.get());
    }

    @Test(timeout = 2000)
    public void testRetrySchedulerInvokedOnFailure() throws Throwable {
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(new CommandGatewayFactoryTest.SomeRuntimeException())).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.waitForReturnValue("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        Mockito.verify(mockRetryScheduler).scheduleRetry(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandGatewayFactoryTest.SomeRuntimeException.class), ArgumentMatchers.anyList(), ArgumentMatchers.any(Runnable.class));
        Assert.assertNotNull(error.get());
        Assert.assertNull("Did not Expect to get a Future return value", result.get());
    }

    @Test(timeout = 2000)
    public void testRetrySchedulerNotInvokedOnCheckedException() throws Throwable {
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(new CommandGatewayFactoryTest.ExpectedException())).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Thread t = new Thread(() -> {
            try {
                result.set(gateway.waitForReturnValue("Command"));
            } catch (Throwable e) {
                error.set(e);
            }
        });
        t.start();
        t.join();
        Mockito.verify(mockRetryScheduler, Mockito.never()).scheduleRetry(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.any(RuntimeException.class), ArgumentMatchers.anyList(), ArgumentMatchers.any(Runnable.class));
        Assert.assertNotNull(error.get());
        Assert.assertNull("Did not Expect to get a Future return value", result.get());
    }

    @Test(timeout = 2000)
    public void testRetrySchedulerInvokedOnExceptionCausedByDeadlock() {
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(new RuntimeException(new DeadlockException("Mock")))).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        try {
            result.set(gateway.waitForReturnValue("Command"));
        } catch (Exception e) {
            error.set(e);
        }
        Mockito.verify(mockRetryScheduler).scheduleRetry(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.any(RuntimeException.class), ArgumentMatchers.anyList(), ArgumentMatchers.any(Runnable.class));
        Assert.assertNotNull(error.get());
        Assert.assertNull("Did not Expect to get a Future return value", result.get());
    }

    @Test(timeout = 2000)
    public void testCreateGatewayWaitForResultAndInvokeCallbacksSuccess() {
        CountDownLatch cdl = new CountDownLatch(1);
        final CommandCallback callback1 = Mockito.mock(CommandCallback.class);
        final CommandCallback callback2 = Mockito.mock(CommandCallback.class);
        CommandResultMessage<String> ok = GenericCommandResultMessage.asCommandResultMessage("OK");
        Mockito.doAnswer(new CommandGatewayFactoryTest.Success(cdl, ok)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Object result = gateway.fireAndWaitAndInvokeCallbacks("Command", callback1, callback2);
        Assert.assertEquals(0, cdl.getCount());
        Assert.assertNotNull(result);
        Mockito.verify(callback1).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(ok));
        Mockito.verify(callback2).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(ok));
    }

    @Test(timeout = 2000)
    public void testCreateGatewayWaitForResultAndInvokeCallbacksFailure() {
        final CommandCallback callback1 = Mockito.mock(CommandCallback.class);
        final CommandCallback callback2 = Mockito.mock(CommandCallback.class);
        final RuntimeException exception = new RuntimeException();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(exception)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        try {
            gateway.fireAndWaitAndInvokeCallbacks("Command", callback1, callback2);
            Assert.fail("Expected exception");
        } catch (RuntimeException e) {
            ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
            Mockito.verify(callback1).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
            Mockito.verify(callback2).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
            Assert.assertEquals(2, commandResultMessageCaptor.getAllValues().size());
            Assert.assertEquals(exception, commandResultMessageCaptor.getAllValues().get(0).exceptionResult());
            Assert.assertEquals(exception, commandResultMessageCaptor.getAllValues().get(1).exceptionResult());
        }
    }

    @Test(timeout = 2000)
    public void testCreateGatewayAsyncWithCallbacksSuccess() {
        CountDownLatch cdl = new CountDownLatch(1);
        final CommandCallback callback1 = Mockito.mock(CommandCallback.class);
        final CommandCallback callback2 = Mockito.mock(CommandCallback.class);
        CommandResultMessage<String> ok = GenericCommandResultMessage.asCommandResultMessage("OK");
        Mockito.doAnswer(new CommandGatewayFactoryTest.Success(cdl, ok)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        gateway.fireAsyncWithCallbacks("Command", callback1, callback2);
        Assert.assertEquals(0, cdl.getCount());
        Mockito.verify(callback1).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(ok));
        Mockito.verify(callback2).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(ok));
    }

    @Test(timeout = 2000)
    public void testCreateGatewayAsyncWithCallbacksSuccessButReturnTypeDoesNotMatchCallback() {
        CountDownLatch cdl = new CountDownLatch(1);
        final CommandCallback callback1 = Mockito.mock(CommandCallback.class);
        final CommandCallback callback2 = Mockito.mock(CommandCallback.class);
        CommandResultMessage<Object> resultMessage = GenericCommandResultMessage.asCommandResultMessage(42);
        Mockito.doAnswer(new CommandGatewayFactoryTest.Success(cdl, resultMessage)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        gateway.fireAsyncWithCallbacks("Command", callback1, callback2);
        Assert.assertEquals(0, cdl.getCount());
        Mockito.verify(callback1).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(resultMessage));
        Mockito.verify(callback2).onResult(ArgumentMatchers.any(), ArgumentMatchers.eq(resultMessage));
        Mockito.verify(callback, Mockito.never()).onResult(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test(timeout = 2000)
    public void testCreateGatewayAsyncWithCallbacksFailure() {
        final CommandCallback callback1 = Mockito.mock(CommandCallback.class);
        final CommandCallback callback2 = Mockito.mock(CommandCallback.class);
        final RuntimeException exception = new RuntimeException();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(exception)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        gateway.fireAsyncWithCallbacks("Command", callback1, callback2);
        ArgumentCaptor<CommandResultMessage> commandResultMessageCaptor = ArgumentCaptor.forClass(CommandResultMessage.class);
        Mockito.verify(callback1).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        Mockito.verify(callback2).onResult(ArgumentMatchers.any(), commandResultMessageCaptor.capture());
        Assert.assertEquals(2, commandResultMessageCaptor.getAllValues().size());
        Assert.assertEquals(exception, commandResultMessageCaptor.getAllValues().get(0).exceptionResult());
        Assert.assertEquals(exception, commandResultMessageCaptor.getAllValues().get(1).exceptionResult());
    }

    @Test(timeout = 2000)
    public void testCreateGatewayCompletableFutureFailure() {
        final RuntimeException exception = new RuntimeException();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(exception)).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        CompletableFuture future = gateway.fireAndGetCompletableFuture("Command");
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.isCompletedExceptionally());
    }

    @Test(timeout = 2000)
    public void testCreateGatewayCompletableFutureSuccessfulResult() throws Throwable {
        Mockito.doAnswer(( invocationOnMock) -> {
            ((CommandCallback) (invocationOnMock.getArguments()[1])).onResult(((CommandMessage) (invocationOnMock.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage("returnValue"));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        CompletableFuture future = gateway.fireAndGetCompletableFuture("Command");
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), "returnValue");
    }

    @Test(timeout = 2000)
    public void testCreateGatewayFutureSuccessfulResult() throws Throwable {
        Mockito.doAnswer(( invocationOnMock) -> {
            ((CommandCallback) (invocationOnMock.getArguments()[1])).onResult(((CommandMessage) (invocationOnMock.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage("returnValue"));
            return null;
        }).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        Future future = gateway.fireAndGetFuture("Command");
        Assert.assertTrue(future.isDone());
        Assert.assertEquals(future.get(), "returnValue");
    }

    @Test(timeout = 2000)
    public void testRetrySchedulerNotInvokedOnExceptionCausedByDeadlockAndActiveUnitOfWork() {
        final AtomicReference<Object> result = new AtomicReference<>();
        final AtomicReference<Throwable> error = new AtomicReference<>();
        Mockito.doAnswer(new CommandGatewayFactoryTest.Failure(new RuntimeException(new DeadlockException("Mock")))).when(mockCommandBus).dispatch(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.isA(CommandCallback.class));
        UnitOfWork<CommandMessage<?>> uow = DefaultUnitOfWork.startAndGet(null);
        try {
            result.set(gateway.waitForReturnValue("Command"));
        } catch (Exception e) {
            error.set(e);
        } finally {
            uow.rollback();
        }
        Mockito.verify(mockRetryScheduler, Mockito.never()).scheduleRetry(ArgumentMatchers.isA(CommandMessage.class), ArgumentMatchers.any(RuntimeException.class), ArgumentMatchers.anyList(), ArgumentMatchers.any(Runnable.class));
        Assert.assertNotNull(error.get());
        Assert.assertNull("Did not Expect to get a Future return value", result.get());
    }

    @Test(timeout = 2000)
    public void testCreateGatewayEqualsAndHashCode() {
        CommandGatewayFactoryTest.CompleteGateway gateway2 = testSubject.createGateway(CommandGatewayFactoryTest.CompleteGateway.class);
        Assert.assertNotSame(gateway, gateway2);
        Assert.assertNotEquals(gateway, gateway2);
    }

    @SuppressWarnings("UnusedReturnValue")
    private interface CompleteGateway {
        void fireAndForget(Object command, MetaData meta, @MetaDataValue("test")
        Object metaTest, @MetaDataValue("key")
        Object metaKey);

        String waitForReturnValue(Object command);

        void waitForException(Object command) throws InterruptedException;

        @Timeout(value = 1, unit = TimeUnit.SECONDS)
        void fireAndWait(Object command);

        void fireAndWaitWithTimeoutParameter(Object command, long timeout, TimeUnit unit);

        Object fireAndWaitWithTimeoutParameterAndException(Object command, long timeout, TimeUnit unit) throws TimeoutException;

        Object fireAndWaitForCheckedException(Object command) throws CommandGatewayFactoryTest.ExpectedException;

        Future<Object> fireAndGetFuture(Object command);

        CompletableFuture<Object> fireAndGetCompletableFuture(Object command);

        CompletionStage<Object> fireAndGetCompletionStage(Object command);

        CompletableFuture<Object> futureWithTimeout(Object command, int timeout, TimeUnit unit);

        Object fireAndWaitAndInvokeCallbacks(Object command, CommandCallback first, CommandCallback second);

        void fireAsyncWithCallbacks(Object command, CommandCallback first, CommandCallback second);
    }

    private static class ExpectedException extends Exception {}

    private static class CountDown implements Answer {
        private final CountDownLatch cdl;

        CountDown(CountDownLatch cdl) {
            this.cdl = cdl;
        }

        @Override
        public Object answer(InvocationOnMock invocation) {
            cdl.countDown();
            return null;
        }
    }

    private static class Success implements Answer {
        private final CountDownLatch cdl;

        private final CommandResultMessage<?> returnValue;

        Success(CountDownLatch cdl, CommandResultMessage<?> returnValue) {
            this.cdl = cdl;
            this.returnValue = returnValue;
        }

        @Override
        public Object answer(InvocationOnMock invocation) {
            cdl.countDown();
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), returnValue);
            return null;
        }
    }

    public static class StringCommandCallback implements CommandCallback<Object, String> {
        @Override
        public void onResult(CommandMessage<?> commandMessage, CommandResultMessage<? extends String> commandResultMessage) {
        }
    }

    private class Failure implements Answer {
        private final CountDownLatch cdl;

        private final Exception e;

        Failure(CountDownLatch cdl, Exception e) {
            this.cdl = cdl;
            this.e = e;
        }

        Failure(Exception e) {
            this(null, e);
        }

        @Override
        public Object answer(InvocationOnMock invocation) {
            if ((cdl) != null) {
                cdl.countDown();
            }
            ((CommandCallback) (invocation.getArguments()[1])).onResult(((CommandMessage) (invocation.getArguments()[0])), GenericCommandResultMessage.asCommandResultMessage(e));
            return null;
        }
    }

    private class SomeRuntimeException extends RuntimeException {}
}

