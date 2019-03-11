/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.runtime.rpc.akka;


import akka.actor.ActorSystem;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.rpc.RpcEndpoint;
import org.apache.flink.runtime.rpc.RpcGateway;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.RpcTimeout;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.akka.exceptions.AkkaRpcException;
import org.apache.flink.runtime.rpc.exceptions.RpcConnectionException;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.FlinkRuntimeException;
import org.apache.flink.util.Preconditions;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import scala.concurrent.Await;


/**
 * Tests for the {@link AkkaRpcActor}.
 */
public class AkkaRpcActorTest extends TestLogger {
    // ------------------------------------------------------------------------
    // shared test members
    // ------------------------------------------------------------------------
    private static Time timeout = Time.milliseconds(10000L);

    private static AkkaRpcService akkaRpcService;

    /**
     * Tests that the rpc endpoint and the associated rpc gateway have the same addresses.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAddressResolution() throws Exception {
        AkkaRpcActorTest.DummyRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.DummyRpcEndpoint(AkkaRpcActorTest.akkaRpcService);
        CompletableFuture<AkkaRpcActorTest.DummyRpcGateway> futureRpcGateway = AkkaRpcActorTest.akkaRpcService.connect(getAddress(), AkkaRpcActorTest.DummyRpcGateway.class);
        AkkaRpcActorTest.DummyRpcGateway rpcGateway = futureRpcGateway.get(AkkaRpcActorTest.timeout.getSize(), AkkaRpcActorTest.timeout.getUnit());
        Assert.assertEquals(getAddress(), getAddress());
    }

    /**
     * Tests that a {@link RpcConnectionException} is thrown if the rpc endpoint cannot be connected to.
     */
    @Test
    public void testFailingAddressResolution() throws Exception {
        CompletableFuture<AkkaRpcActorTest.DummyRpcGateway> futureRpcGateway = AkkaRpcActorTest.akkaRpcService.connect("foobar", AkkaRpcActorTest.DummyRpcGateway.class);
        try {
            futureRpcGateway.get(AkkaRpcActorTest.timeout.getSize(), AkkaRpcActorTest.timeout.getUnit());
            Assert.fail("The rpc connection resolution should have failed.");
        } catch (ExecutionException exception) {
            // we're expecting a RpcConnectionException
            Assert.assertTrue(((exception.getCause()) instanceof RpcConnectionException));
        }
    }

    /**
     * Tests that the {@link AkkaRpcActor} discards messages until the corresponding
     * {@link RpcEndpoint} has been started.
     */
    @Test
    public void testMessageDiscarding() throws Exception {
        int expectedValue = 1337;
        AkkaRpcActorTest.DummyRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.DummyRpcEndpoint(AkkaRpcActorTest.akkaRpcService);
        AkkaRpcActorTest.DummyRpcGateway rpcGateway = getSelfGateway(AkkaRpcActorTest.DummyRpcGateway.class);
        // this message should be discarded and completed with an AkkaRpcException
        CompletableFuture<Integer> result = rpcGateway.foobar();
        try {
            result.get(AkkaRpcActorTest.timeout.getSize(), AkkaRpcActorTest.timeout.getUnit());
            Assert.fail("Expected an AkkaRpcException.");
        } catch (ExecutionException ee) {
            // expected this exception, because the endpoint has not been started
            Assert.assertTrue(((ee.getCause()) instanceof AkkaRpcException));
        }
        // set a new value which we expect to be returned
        rpcEndpoint.setFoobar(expectedValue);
        // start the endpoint so that it can process messages
        start();
        try {
            // send the rpc again
            result = rpcGateway.foobar();
            // now we should receive a result :-)
            Integer actualValue = result.get(AkkaRpcActorTest.timeout.getSize(), AkkaRpcActorTest.timeout.getUnit());
            Assert.assertThat("The new foobar value should have been returned.", actualValue, Is.is(expectedValue));
        } finally {
            RpcUtils.terminateRpcEndpoint(rpcEndpoint, AkkaRpcActorTest.timeout);
        }
    }

    /**
     * Tests that we can wait for a RpcEndpoint to terminate.
     *
     * @throws ExecutionException
     * 		
     * @throws InterruptedException
     * 		
     */
    @Test(timeout = 5000)
    public void testRpcEndpointTerminationFuture() throws Exception {
        final AkkaRpcActorTest.DummyRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.DummyRpcEndpoint(AkkaRpcActorTest.akkaRpcService);
        start();
        CompletableFuture<Void> terminationFuture = getTerminationFuture();
        Assert.assertFalse(terminationFuture.isDone());
        CompletableFuture.runAsync(rpcEndpoint::closeAsync, AkkaRpcActorTest.akkaRpcService.getExecutor());
        // wait until the rpc endpoint has terminated
        terminationFuture.get();
    }

    @Test
    public void testExceptionPropagation() throws Exception {
        AkkaRpcActorTest.ExceptionalEndpoint rpcEndpoint = new AkkaRpcActorTest.ExceptionalEndpoint(AkkaRpcActorTest.akkaRpcService);
        start();
        AkkaRpcActorTest.ExceptionalGateway rpcGateway = rpcEndpoint.getSelfGateway(AkkaRpcActorTest.ExceptionalGateway.class);
        CompletableFuture<Integer> result = rpcGateway.doStuff();
        try {
            result.get(AkkaRpcActorTest.timeout.getSize(), AkkaRpcActorTest.timeout.getUnit());
            Assert.fail("this should fail with an exception");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            Assert.assertEquals(RuntimeException.class, cause.getClass());
            Assert.assertEquals("my super specific test exception", cause.getMessage());
        }
    }

    @Test
    public void testExceptionPropagationFuturePiping() throws Exception {
        AkkaRpcActorTest.ExceptionalFutureEndpoint rpcEndpoint = new AkkaRpcActorTest.ExceptionalFutureEndpoint(AkkaRpcActorTest.akkaRpcService);
        start();
        AkkaRpcActorTest.ExceptionalGateway rpcGateway = rpcEndpoint.getSelfGateway(AkkaRpcActorTest.ExceptionalGateway.class);
        CompletableFuture<Integer> result = rpcGateway.doStuff();
        try {
            result.get(AkkaRpcActorTest.timeout.getSize(), AkkaRpcActorTest.timeout.getUnit());
            Assert.fail("this should fail with an exception");
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            Assert.assertEquals(Exception.class, cause.getClass());
            Assert.assertEquals("some test", cause.getMessage());
        }
    }

    /**
     * Tests that exception thrown in the onStop method are returned by the termination
     * future.
     */
    @Test
    public void testOnStopExceptionPropagation() throws Exception {
        AkkaRpcActorTest.FailingOnStopEndpoint rpcEndpoint = new AkkaRpcActorTest.FailingOnStopEndpoint(AkkaRpcActorTest.akkaRpcService, "FailingOnStopEndpoint");
        start();
        CompletableFuture<Void> terminationFuture = closeAsync();
        try {
            terminationFuture.get();
        } catch (ExecutionException e) {
            Assert.assertTrue(((e.getCause()) instanceof AkkaRpcActorTest.FailingOnStopEndpoint.OnStopException));
        }
    }

    /**
     * Checks that the onStop callback is executed within the main thread.
     */
    @Test
    public void testOnStopExecutedByMainThread() throws Exception {
        AkkaRpcActorTest.SimpleRpcEndpoint simpleRpcEndpoint = new AkkaRpcActorTest.SimpleRpcEndpoint(AkkaRpcActorTest.akkaRpcService, "SimpleRpcEndpoint");
        start();
        CompletableFuture<Void> terminationFuture = closeAsync();
        // check that we executed the onStop method in the main thread, otherwise an exception
        // would be thrown here.
        terminationFuture.get();
    }

    /**
     * Tests that actors are properly terminated when the AkkaRpcService is shut down.
     */
    @Test
    public void testActorTerminationWhenServiceShutdown() throws Exception {
        final ActorSystem rpcActorSystem = AkkaUtils.createDefaultActorSystem();
        final RpcService rpcService = new AkkaRpcService(rpcActorSystem, AkkaRpcServiceConfiguration.defaultConfiguration());
        try {
            AkkaRpcActorTest.SimpleRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.SimpleRpcEndpoint(rpcService, AkkaRpcActorTest.SimpleRpcEndpoint.class.getSimpleName());
            start();
            CompletableFuture<Void> terminationFuture = getTerminationFuture();
            rpcService.stopService();
            terminationFuture.get(AkkaRpcActorTest.timeout.toMilliseconds(), TimeUnit.MILLISECONDS);
        } finally {
            rpcActorSystem.terminate();
            Await.ready(rpcActorSystem.whenTerminated(), FutureUtils.toFiniteDuration(AkkaRpcActorTest.timeout));
        }
    }

    /**
     * Tests that the {@link AkkaRpcActor} only completes after the asynchronous
     * post stop action has completed.
     */
    @Test
    public void testActorTerminationWithAsynchronousOnStopAction() throws Exception {
        final CompletableFuture<Void> onStopFuture = new CompletableFuture<>();
        final AkkaRpcActorTest.AsynchronousOnStopEndpoint endpoint = new AkkaRpcActorTest.AsynchronousOnStopEndpoint(AkkaRpcActorTest.akkaRpcService, onStopFuture);
        try {
            start();
            final CompletableFuture<Void> terminationFuture = closeAsync();
            Assert.assertFalse(terminationFuture.isDone());
            onStopFuture.complete(null);
            // the onStopFuture completion should allow the endpoint to terminate
            terminationFuture.get();
        } finally {
            RpcUtils.terminateRpcEndpoint(endpoint, AkkaRpcActorTest.timeout);
        }
    }

    /**
     * Tests that we can still run commands via the main thread executor when the onStop method
     * is called.
     */
    @Test
    public void testMainThreadExecutionOnStop() throws Exception {
        final AkkaRpcActorTest.MainThreadExecutorOnStopEndpoint endpoint = new AkkaRpcActorTest.MainThreadExecutorOnStopEndpoint(AkkaRpcActorTest.akkaRpcService);
        try {
            start();
            CompletableFuture<Void> terminationFuture = closeAsync();
            terminationFuture.get();
        } finally {
            RpcUtils.terminateRpcEndpoint(endpoint, AkkaRpcActorTest.timeout);
        }
    }

    /**
     * Tests that when the onStop future completes that no other messages will be
     * processed.
     */
    @Test
    public void testOnStopFutureCompletionDirectlyTerminatesAkkaRpcActor() throws Exception {
        final CompletableFuture<Void> onStopFuture = new CompletableFuture<>();
        final AkkaRpcActorTest.TerminatingAfterOnStopFutureCompletionEndpoint endpoint = new AkkaRpcActorTest.TerminatingAfterOnStopFutureCompletionEndpoint(AkkaRpcActorTest.akkaRpcService, onStopFuture);
        try {
            start();
            final AkkaRpcActorTest.AsyncOperationGateway asyncOperationGateway = endpoint.getSelfGateway(AkkaRpcActorTest.AsyncOperationGateway.class);
            final CompletableFuture<Void> terminationFuture = closeAsync();
            Assert.assertThat(terminationFuture.isDone(), Matchers.is(false));
            final CompletableFuture<Integer> firstAsyncOperationFuture = asyncOperationGateway.asyncOperation(AkkaRpcActorTest.timeout);
            final CompletableFuture<Integer> secondAsyncOperationFuture = asyncOperationGateway.asyncOperation(AkkaRpcActorTest.timeout);
            endpoint.awaitEnterAsyncOperation();
            // complete stop operation which should prevent the second async operation from being executed
            onStopFuture.complete(null);
            // we can only complete the termination after the first async operation has been completed
            Assert.assertThat(terminationFuture.isDone(), Matchers.is(false));
            endpoint.triggerUnblockAsyncOperation();
            Assert.assertThat(firstAsyncOperationFuture.get(), Matchers.is(42));
            terminationFuture.get();
            Assert.assertThat(endpoint.getNumberAsyncOperationCalls(), Matchers.is(1));
            Assert.assertThat(secondAsyncOperationFuture.isDone(), Matchers.is(false));
        } finally {
            RpcUtils.terminateRpcEndpoint(endpoint, AkkaRpcActorTest.timeout);
        }
    }

    /**
     * Tests that the {@link RpcEndpoint#onStart()} method is called when the {@link RpcEndpoint}
     * is started.
     */
    @Test
    public void testOnStartIsCalledWhenRpcEndpointStarts() throws Exception {
        final AkkaRpcActorTest.OnStartEndpoint onStartEndpoint = new AkkaRpcActorTest.OnStartEndpoint(AkkaRpcActorTest.akkaRpcService, null);
        try {
            start();
            onStartEndpoint.awaitUntilOnStartCalled();
        } finally {
            RpcUtils.terminateRpcEndpoint(onStartEndpoint, AkkaRpcActorTest.timeout);
        }
    }

    /**
     * Tests that if onStart fails, then the endpoint terminates.
     */
    @Test
    public void testOnStartFails() throws Exception {
        final FlinkException testException = new FlinkException("Test exception");
        final AkkaRpcActorTest.OnStartEndpoint onStartEndpoint = new AkkaRpcActorTest.OnStartEndpoint(AkkaRpcActorTest.akkaRpcService, testException);
        start();
        onStartEndpoint.awaitUntilOnStartCalled();
        try {
            onStartEndpoint.getTerminationFuture().get();
            Assert.fail("Expected that the rpc endpoint failed onStart and thus has terminated.");
        } catch (ExecutionException ee) {
            Assert.assertThat(ExceptionUtils.findThrowable(ee, ( exception) -> exception.equals(testException)).isPresent(), Matchers.is(true));
        }
    }

    // ------------------------------------------------------------------------
    interface DummyRpcGateway extends RpcGateway {
        // Test Actors and Interfaces
        // ------------------------------------------------------------------------
        CompletableFuture<Integer> foobar();
    }

    static class DummyRpcEndpoint extends RpcEndpoint implements AkkaRpcActorTest.DummyRpcGateway {
        private volatile int foobar = 42;

        protected DummyRpcEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        @Override
        public CompletableFuture<Integer> foobar() {
            return CompletableFuture.completedFuture(foobar);
        }

        public void setFoobar(int value) {
            foobar = value;
        }
    }

    // ------------------------------------------------------------------------
    private interface ExceptionalGateway extends RpcGateway {
        CompletableFuture<Integer> doStuff();
    }

    private static class ExceptionalEndpoint extends RpcEndpoint implements AkkaRpcActorTest.ExceptionalGateway {
        protected ExceptionalEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        @Override
        public CompletableFuture<Integer> doStuff() {
            throw new RuntimeException("my super specific test exception");
        }
    }

    private static class ExceptionalFutureEndpoint extends RpcEndpoint implements AkkaRpcActorTest.ExceptionalGateway {
        protected ExceptionalFutureEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        @Override
        public CompletableFuture<Integer> doStuff() {
            final CompletableFuture<Integer> future = new CompletableFuture<>();
            // complete the future slightly in the, well, future...
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ignored) {
                    }
                    future.completeExceptionally(new Exception("some test"));
                }
            }.start();
            return future;
        }
    }

    // ------------------------------------------------------------------------
    private static class SimpleRpcEndpoint extends RpcEndpoint implements RpcGateway {
        protected SimpleRpcEndpoint(RpcService rpcService, String endpointId) {
            super(rpcService, endpointId);
        }
    }

    // ------------------------------------------------------------------------
    private static class FailingOnStopEndpoint extends RpcEndpoint implements RpcGateway {
        protected FailingOnStopEndpoint(RpcService rpcService, String endpointId) {
            super(rpcService, endpointId);
        }

        @Override
        public CompletableFuture<Void> onStop() {
            return FutureUtils.completedExceptionally(new AkkaRpcActorTest.FailingOnStopEndpoint.OnStopException("Test exception."));
        }

        private static class OnStopException extends FlinkException {
            private static final long serialVersionUID = 6701096588415871592L;

            public OnStopException(String message) {
                super(message);
            }
        }
    }

    // ------------------------------------------------------------------------
    static class AsynchronousOnStopEndpoint extends RpcEndpoint {
        private final CompletableFuture<Void> onStopFuture;

        protected AsynchronousOnStopEndpoint(RpcService rpcService, CompletableFuture<Void> onStopFuture) {
            super(rpcService);
            this.onStopFuture = Preconditions.checkNotNull(onStopFuture);
        }

        @Override
        public CompletableFuture<Void> onStop() {
            return onStopFuture;
        }
    }

    // ------------------------------------------------------------------------
    private static class MainThreadExecutorOnStopEndpoint extends RpcEndpoint {
        protected MainThreadExecutorOnStopEndpoint(RpcService rpcService) {
            super(rpcService);
        }

        @Override
        public CompletableFuture<Void> onStop() {
            return CompletableFuture.runAsync(() -> {
            }, getMainThreadExecutor());
        }
    }

    // ------------------------------------------------------------------------
    interface AsyncOperationGateway extends RpcGateway {
        CompletableFuture<Integer> asyncOperation(@RpcTimeout
        Time timeout);
    }

    private static class TerminatingAfterOnStopFutureCompletionEndpoint extends RpcEndpoint implements AkkaRpcActorTest.AsyncOperationGateway {
        private final CompletableFuture<Void> onStopFuture;

        private final OneShotLatch blockAsyncOperation = new OneShotLatch();

        private final OneShotLatch enterAsyncOperation = new OneShotLatch();

        private final AtomicInteger asyncOperationCounter = new AtomicInteger(0);

        protected TerminatingAfterOnStopFutureCompletionEndpoint(RpcService rpcService, CompletableFuture<Void> onStopFuture) {
            super(rpcService);
            this.onStopFuture = onStopFuture;
        }

        @Override
        public CompletableFuture<Integer> asyncOperation(Time timeout) {
            asyncOperationCounter.incrementAndGet();
            enterAsyncOperation.trigger();
            try {
                blockAsyncOperation.await();
            } catch (InterruptedException e) {
                throw new FlinkRuntimeException(e);
            }
            return CompletableFuture.completedFuture(42);
        }

        @Override
        public CompletableFuture<Void> onStop() {
            return onStopFuture;
        }

        void awaitEnterAsyncOperation() throws InterruptedException {
            enterAsyncOperation.await();
        }

        void triggerUnblockAsyncOperation() {
            blockAsyncOperation.trigger();
        }

        int getNumberAsyncOperationCalls() {
            return asyncOperationCounter.get();
        }
    }

    // ------------------------------------------------------------------------
    private static final class OnStartEndpoint extends RpcEndpoint {
        private final CountDownLatch countDownLatch;

        @Nullable
        private final Exception exception;

        OnStartEndpoint(RpcService rpcService, @Nullable
        Exception exception) {
            super(rpcService);
            this.countDownLatch = new CountDownLatch(1);
            this.exception = exception;
            // remove this endpoint from the rpc service once it terminates (normally or exceptionally)
            getTerminationFuture().whenComplete(( aVoid, throwable) -> closeAsync());
        }

        @Override
        public void onStart() throws Exception {
            countDownLatch.countDown();
            ExceptionUtils.tryRethrowException(exception);
        }

        public void awaitUntilOnStartCalled() throws InterruptedException {
            countDownLatch.await();
        }
    }
}

