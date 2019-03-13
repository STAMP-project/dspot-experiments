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


import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;


/**
 * Tests for RegisteredRpcConnection, validating the successful, failure and close behavior.
 */
public class RegisteredRpcConnectionTest extends TestLogger {
    private TestingRpcService rpcService;

    @Test
    public void testSuccessfulRpcConnection() throws Exception {
        final String testRpcConnectionEndpointAddress = "<TestRpcConnectionEndpointAddress>";
        final UUID leaderId = UUID.randomUUID();
        final String connectionID = "Test RPC Connection ID";
        // an endpoint that immediately returns success
        TestRegistrationGateway testGateway = new TestRegistrationGateway(new RetryingRegistrationTest.TestRegistrationSuccess(connectionID));
        try {
            rpcService.registerGateway(testRpcConnectionEndpointAddress, testGateway);
            RegisteredRpcConnectionTest.TestRpcConnection connection = new RegisteredRpcConnectionTest.TestRpcConnection(testRpcConnectionEndpointAddress, leaderId, getExecutor(), rpcService);
            start();
            // wait for connection established
            final String actualConnectionId = connection.getConnectionFuture().get();
            // validate correct invocation and result
            Assert.assertTrue(isConnected());
            Assert.assertEquals(testRpcConnectionEndpointAddress, getTargetAddress());
            Assert.assertEquals(leaderId, getTargetLeaderId());
            Assert.assertEquals(testGateway, getTargetGateway());
            Assert.assertEquals(connectionID, actualConnectionId);
        } finally {
            testGateway.stop();
        }
    }

    @Test
    public void testRpcConnectionFailures() throws Exception {
        final String connectionFailureMessage = "Test RPC Connection failure";
        final String testRpcConnectionEndpointAddress = "<TestRpcConnectionEndpointAddress>";
        final UUID leaderId = UUID.randomUUID();
        // gateway that upon calls Throw an exception
        TestRegistrationGateway testGateway = Mockito.mock(TestRegistrationGateway.class);
        final RuntimeException registrationException = new RuntimeException(connectionFailureMessage);
        Mockito.when(testGateway.registrationCall(ArgumentMatchers.any(UUID.class), ArgumentMatchers.anyLong())).thenThrow(registrationException);
        rpcService.registerGateway(testRpcConnectionEndpointAddress, testGateway);
        RegisteredRpcConnectionTest.TestRpcConnection connection = new RegisteredRpcConnectionTest.TestRpcConnection(testRpcConnectionEndpointAddress, leaderId, getExecutor(), rpcService);
        start();
        // wait for connection failure
        try {
            connection.getConnectionFuture().get();
            Assert.fail("expected failure.");
        } catch (ExecutionException ee) {
            Assert.assertEquals(registrationException, ee.getCause());
        }
        // validate correct invocation and result
        Assert.assertFalse(isConnected());
        Assert.assertEquals(testRpcConnectionEndpointAddress, getTargetAddress());
        Assert.assertEquals(leaderId, getTargetLeaderId());
        Assert.assertNull(getTargetGateway());
    }

    @Test
    public void testRpcConnectionClose() throws Exception {
        final String testRpcConnectionEndpointAddress = "<TestRpcConnectionEndpointAddress>";
        final UUID leaderId = UUID.randomUUID();
        final String connectionID = "Test RPC Connection ID";
        TestRegistrationGateway testGateway = new TestRegistrationGateway(new RetryingRegistrationTest.TestRegistrationSuccess(connectionID));
        try {
            rpcService.registerGateway(testRpcConnectionEndpointAddress, testGateway);
            RegisteredRpcConnectionTest.TestRpcConnection connection = new RegisteredRpcConnectionTest.TestRpcConnection(testRpcConnectionEndpointAddress, leaderId, getExecutor(), rpcService);
            start();
            // close the connection
            close();
            // validate connection is closed
            Assert.assertEquals(testRpcConnectionEndpointAddress, getTargetAddress());
            Assert.assertEquals(leaderId, getTargetLeaderId());
            Assert.assertTrue(isClosed());
        } finally {
            testGateway.stop();
        }
    }

    @Test
    public void testReconnect() throws Exception {
        final String connectionId1 = "Test RPC Connection ID 1";
        final String connectionId2 = "Test RPC Connection ID 2";
        final String testRpcConnectionEndpointAddress = "<TestRpcConnectionEndpointAddress>";
        final UUID leaderId = UUID.randomUUID();
        final TestRegistrationGateway testGateway = new TestRegistrationGateway(new RetryingRegistrationTest.TestRegistrationSuccess(connectionId1), new RetryingRegistrationTest.TestRegistrationSuccess(connectionId2));
        rpcService.registerGateway(testRpcConnectionEndpointAddress, testGateway);
        RegisteredRpcConnectionTest.TestRpcConnection connection = new RegisteredRpcConnectionTest.TestRpcConnection(testRpcConnectionEndpointAddress, leaderId, getExecutor(), rpcService);
        start();
        final String actualConnectionId1 = connection.getConnectionFuture().get();
        Assert.assertEquals(actualConnectionId1, connectionId1);
        Assert.assertTrue(connection.tryReconnect());
        final String actualConnectionId2 = connection.getConnectionFuture().get();
        Assert.assertEquals(actualConnectionId2, connectionId2);
    }

    // ------------------------------------------------------------------------
    // test RegisteredRpcConnection
    // ------------------------------------------------------------------------
    private static class TestRpcConnection extends RegisteredRpcConnection<UUID, TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> {
        private final Object lock = new Object();

        private final RpcService rpcService;

        private CompletableFuture<String> connectionFuture;

        public TestRpcConnection(String targetAddress, UUID targetLeaderId, Executor executor, RpcService rpcService) {
            super(LoggerFactory.getLogger(RegisteredRpcConnectionTest.class), targetAddress, targetLeaderId, executor);
            this.rpcService = rpcService;
            this.connectionFuture = new CompletableFuture<>();
        }

        @Override
        protected RetryingRegistration<UUID, TestRegistrationGateway, RetryingRegistrationTest.TestRegistrationSuccess> generateRegistration() {
            return new RetryingRegistrationTest.TestRetryingRegistration(rpcService, getTargetAddress(), getTargetLeaderId());
        }

        @Override
        protected void onRegistrationSuccess(RetryingRegistrationTest.TestRegistrationSuccess success) {
            synchronized(lock) {
                connectionFuture.complete(success.getCorrelationId());
            }
        }

        @Override
        protected void onRegistrationFailure(Throwable failure) {
            synchronized(lock) {
                connectionFuture.completeExceptionally(failure);
            }
        }

        @Override
        public boolean tryReconnect() {
            synchronized(lock) {
                connectionFuture.cancel(false);
                connectionFuture = new CompletableFuture<>();
            }
            return super.tryReconnect();
        }

        public CompletableFuture<String> getConnectionFuture() {
            synchronized(lock) {
                return connectionFuture;
            }
        }
    }
}

