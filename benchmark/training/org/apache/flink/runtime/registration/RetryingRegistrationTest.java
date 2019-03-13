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
package org.apache.flink.runtime.registration;


import RegistrationResponse.Success;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.concurrent.FutureUtils;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.slf4j.LoggerFactory;


/**
 * Tests for the generic retrying registration class, validating the failure, retry, and back-off behavior.
 */
public class RetryingRegistrationTest extends TestLogger {
    private TestingRpcService rpcService;

    @Test
    public void testSimpleSuccessfulRegistration() throws Exception {
        final String testId = "laissez les bon temps roulez";
        final String testEndpointAddress = "<test-address>";
        final UUID leaderId = UUID.randomUUID();
        // an endpoint that immediately returns success
        TestRegistrationGateway testGateway = new TestRegistrationGateway(new RetryingRegistrationTest.TestRegistrationSuccess(testId));
        try {
            rpcService.registerGateway(testEndpointAddress, testGateway);
            RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpcService, testEndpointAddress, leaderId);
            startRegistration();
            CompletableFuture<Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess>> future = getFuture();
            Assert.assertNotNull(future);
            // multiple accesses return the same future
            Assert.assertEquals(future, registration.getFuture());
            Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> success = future.get(10L, TimeUnit.SECONDS);
            // validate correct invocation and result
            Assert.assertEquals(testId, success.f1.getCorrelationId());
            Assert.assertEquals(leaderId, testGateway.getInvocations().take().leaderId());
        } finally {
            testGateway.stop();
        }
    }

    @Test
    public void testPropagateFailures() throws Exception {
        final String testExceptionMessage = "testExceptionMessage";
        // RPC service that fails with exception upon the connection
        RpcService rpc = Mockito.mock(RpcService.class);
        Mockito.when(rpc.connect(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class.class))).thenThrow(new RuntimeException(testExceptionMessage));
        RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpc, "testaddress", UUID.randomUUID());
        startRegistration();
        CompletableFuture<?> future = getFuture();
        Assert.assertTrue(future.isDone());
        try {
            future.get();
            Assert.fail("We expected an ExecutionException.");
        } catch (ExecutionException e) {
            Assert.assertEquals(testExceptionMessage, e.getCause().getMessage());
        }
    }

    @Test
    public void testRetryConnectOnFailure() throws Exception {
        final String testId = "laissez les bon temps roulez";
        final UUID leaderId = UUID.randomUUID();
        ExecutorService executor = TestingUtils.defaultExecutor();
        TestRegistrationGateway testGateway = new TestRegistrationGateway(new RetryingRegistrationTest.TestRegistrationSuccess(testId));
        try {
            // RPC service that fails upon the first connection, but succeeds on the second
            RpcService rpc = Mockito.mock(RpcService.class);
            // first connection attempt fails
            // second connection attempt succeeds
            Mockito.when(rpc.connect(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class.class))).thenReturn(FutureUtils.completedExceptionally(new Exception("test connect failure")), CompletableFuture.completedFuture(testGateway));
            Mockito.when(rpc.getExecutor()).thenReturn(executor);
            Mockito.when(rpc.scheduleRunnable(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(TimeUnit.class))).thenAnswer((InvocationOnMock invocation) -> {
                final Runnable runnable = invocation.getArgument(0);
                final long delay = invocation.getArgument(1);
                final TimeUnit timeUnit = invocation.getArgument(2);
                return TestingUtils.defaultScheduledExecutor().schedule(runnable, delay, timeUnit);
            });
            RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpc, "foobar address", leaderId);
            long start = System.currentTimeMillis();
            startRegistration();
            Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> success = registration.getFuture().get(10L, TimeUnit.SECONDS);
            // measure the duration of the registration --> should be longer than the error delay
            long duration = (System.currentTimeMillis()) - start;
            Assert.assertTrue("The registration should have failed the first time. Thus the duration should be longer than at least a single error delay.", (duration > (RetryingRegistrationTest.TestRetryingRegistration.DELAY_ON_ERROR)));
            // validate correct invocation and result
            Assert.assertEquals(testId, success.f1.getCorrelationId());
            Assert.assertEquals(leaderId, testGateway.getInvocations().take().leaderId());
        } finally {
            testGateway.stop();
        }
    }

    @Test(timeout = 10000)
    public void testRetriesOnTimeouts() throws Exception {
        final String testId = "rien ne va plus";
        final String testEndpointAddress = "<test-address>";
        final UUID leaderId = UUID.randomUUID();
        // an endpoint that immediately returns futures with timeouts before returning a successful future
        TestRegistrationGateway testGateway = // timeout
        // timeout
        // success
        new TestRegistrationGateway(null, null, new RetryingRegistrationTest.TestRegistrationSuccess(testId));
        try {
            rpcService.registerGateway(testEndpointAddress, testGateway);
            final long initialTimeout = 20L;
            RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpcService, testEndpointAddress, leaderId, // make sure that we timeout in case of an error
            new RetryingRegistrationConfiguration(initialTimeout, 1000L, 15000L, 15000L));
            long started = System.nanoTime();
            startRegistration();
            CompletableFuture<Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess>> future = getFuture();
            Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> success = future.get(10L, TimeUnit.SECONDS);
            long finished = System.nanoTime();
            long elapsedMillis = (finished - started) / 1000000;
            // validate correct invocation and result
            Assert.assertEquals(testId, success.f1.getCorrelationId());
            Assert.assertEquals(leaderId, testGateway.getInvocations().take().leaderId());
            // validate that some retry-delay / back-off behavior happened
            Assert.assertTrue("retries did not properly back off", (elapsedMillis >= (3 * initialTimeout)));
        } finally {
            testGateway.stop();
        }
    }

    @Test
    public void testDecline() throws Exception {
        final String testId = "qui a coupe le fromage";
        final String testEndpointAddress = "<test-address>";
        final UUID leaderId = UUID.randomUUID();
        TestRegistrationGateway testGateway = // timeout
        // timeout
        // success
        new TestRegistrationGateway(null, new RegistrationResponse.Decline("no reason "), null, new RetryingRegistrationTest.TestRegistrationSuccess(testId));
        try {
            rpcService.registerGateway(testEndpointAddress, testGateway);
            RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpcService, testEndpointAddress, leaderId);
            long started = System.nanoTime();
            startRegistration();
            CompletableFuture<Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess>> future = getFuture();
            Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> success = future.get(10L, TimeUnit.SECONDS);
            long finished = System.nanoTime();
            long elapsedMillis = (finished - started) / 1000000;
            // validate correct invocation and result
            Assert.assertEquals(testId, success.f1.getCorrelationId());
            Assert.assertEquals(leaderId, testGateway.getInvocations().take().leaderId());
            // validate that some retry-delay / back-off behavior happened
            Assert.assertTrue("retries did not properly back off", (elapsedMillis >= ((2 * (RetryingRegistrationTest.TestRetryingRegistration.INITIAL_TIMEOUT)) + (RetryingRegistrationTest.TestRetryingRegistration.DELAY_ON_DECLINE))));
        } finally {
            testGateway.stop();
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRetryOnError() throws Exception {
        final String testId = "Petit a petit, l'oiseau fait son nid";
        final String testEndpointAddress = "<test-address>";
        final UUID leaderId = UUID.randomUUID();
        // gateway that upon calls first responds with a failure, then with a success
        TestRegistrationGateway testGateway = Mockito.mock(TestRegistrationGateway.class);
        Mockito.when(testGateway.registrationCall(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyLong())).thenReturn(FutureUtils.completedExceptionally(new Exception("test exception")), CompletableFuture.completedFuture(new RetryingRegistrationTest.TestRegistrationSuccess(testId)));
        rpcService.registerGateway(testEndpointAddress, testGateway);
        RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpcService, testEndpointAddress, leaderId);
        long started = System.nanoTime();
        startRegistration();
        CompletableFuture<Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess>> future = getFuture();
        Tuple2<TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> success = future.get(10, TimeUnit.SECONDS);
        long finished = System.nanoTime();
        long elapsedMillis = (finished - started) / 1000000;
        Assert.assertEquals(testId, success.f1.getCorrelationId());
        // validate that some retry-delay / back-off behavior happened
        Assert.assertTrue("retries did not properly back off", (elapsedMillis >= (RetryingRegistrationTest.TestRetryingRegistration.DELAY_ON_ERROR)));
    }

    @Test
    public void testCancellation() throws Exception {
        final String testEndpointAddress = "my-test-address";
        final UUID leaderId = UUID.randomUUID();
        CompletableFuture<RegistrationResponse> result = new CompletableFuture<>();
        TestRegistrationGateway testGateway = Mockito.mock(TestRegistrationGateway.class);
        Mockito.when(testGateway.registrationCall(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyLong())).thenReturn(result);
        rpcService.registerGateway(testEndpointAddress, testGateway);
        RetryingRegistrationTest.TestRetryingRegistration registration = new RetryingRegistrationTest.TestRetryingRegistration(rpcService, testEndpointAddress, leaderId);
        startRegistration();
        // cancel and fail the current registration attempt
        cancel();
        result.completeExceptionally(new TimeoutException());
        // there should not be a second registration attempt
        Mockito.verify(testGateway, Mockito.atMost(1)).registrationCall(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyLong());
    }

    // ------------------------------------------------------------------------
    // test registration
    // ------------------------------------------------------------------------
    static class TestRegistrationSuccess extends RegistrationResponse.Success {
        private static final long serialVersionUID = 5542698790917150604L;

        private final String correlationId;

        public TestRegistrationSuccess(String correlationId) {
            this.correlationId = correlationId;
        }

        public String getCorrelationId() {
            return correlationId;
        }
    }

    static class TestRetryingRegistration extends RetryingRegistration<UUID, TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> {
        // we use shorter timeouts here to speed up the tests
        static final long INITIAL_TIMEOUT = 20;

        static final long MAX_TIMEOUT = 200;

        static final long DELAY_ON_ERROR = 200;

        static final long DELAY_ON_DECLINE = 200;

        static final RetryingRegistrationConfiguration RETRYING_REGISTRATION_CONFIGURATION = new RetryingRegistrationConfiguration(RetryingRegistrationTest.TestRetryingRegistration.INITIAL_TIMEOUT, RetryingRegistrationTest.TestRetryingRegistration.MAX_TIMEOUT, RetryingRegistrationTest.TestRetryingRegistration.DELAY_ON_ERROR, RetryingRegistrationTest.TestRetryingRegistration.DELAY_ON_DECLINE);

        public TestRetryingRegistration(RpcService rpc, String targetAddress, UUID leaderId) {
            this(rpc, targetAddress, leaderId, RetryingRegistrationTest.TestRetryingRegistration.RETRYING_REGISTRATION_CONFIGURATION);
        }

        public TestRetryingRegistration(RpcService rpc, String targetAddress, UUID leaderId, RetryingRegistrationConfiguration retryingRegistrationConfiguration) {
            super(LoggerFactory.getLogger(RetryingRegistrationTest.class), rpc, "TestEndpoint", TestRegistrationGateway.class, targetAddress, leaderId, retryingRegistrationConfiguration);
        }

        @Override
        protected CompletableFuture<RegistrationResponse> invokeRegistration(TestRegistrationGateway gateway, UUID leaderId, long timeoutMillis) {
            return gateway.registrationCall(leaderId, timeoutMillis);
        }
    }
}

