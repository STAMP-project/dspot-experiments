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
import java.util.concurrent.ExecutionException;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.rpc.RpcGateway;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.exceptions.HandshakeException;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the handshake between rpc endpoints.
 */
public class AkkaRpcActorHandshakeTest extends TestLogger {
    private static final Time timeout = Time.seconds(10L);

    private static AkkaRpcService akkaRpcService1;

    private static AkkaRpcService akkaRpcService2;

    private static AkkaRpcActorHandshakeTest.WrongVersionAkkaRpcService wrongVersionAkkaRpcService;

    @Test
    public void testVersionMatchBetweenRpcComponents() throws Exception {
        AkkaRpcActorTest.DummyRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.DummyRpcEndpoint(AkkaRpcActorHandshakeTest.akkaRpcService1);
        final int value = 42;
        rpcEndpoint.setFoobar(value);
        start();
        try {
            final AkkaRpcActorTest.DummyRpcGateway dummyRpcGateway = AkkaRpcActorHandshakeTest.akkaRpcService2.connect(getAddress(), AkkaRpcActorTest.DummyRpcGateway.class).get();
            Assert.assertThat(dummyRpcGateway.foobar().get(), Matchers.equalTo(value));
        } finally {
            RpcUtils.terminateRpcEndpoint(rpcEndpoint, AkkaRpcActorHandshakeTest.timeout);
        }
    }

    @Test
    public void testVersionMismatchBetweenRpcComponents() throws Exception {
        AkkaRpcActorTest.DummyRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.DummyRpcEndpoint(AkkaRpcActorHandshakeTest.akkaRpcService1);
        start();
        try {
            try {
                AkkaRpcActorHandshakeTest.wrongVersionAkkaRpcService.connect(getAddress(), AkkaRpcActorTest.DummyRpcGateway.class).get();
                Assert.fail("Expected HandshakeException.");
            } catch (ExecutionException ee) {
                Assert.assertThat(ExceptionUtils.stripExecutionException(ee), Matchers.instanceOf(HandshakeException.class));
            }
        } finally {
            RpcUtils.terminateRpcEndpoint(rpcEndpoint, AkkaRpcActorHandshakeTest.timeout);
        }
    }

    /**
     * Tests that we receive a HandshakeException when connecting to a rpc endpoint which
     * does not support the requested rpc gateway.
     */
    @Test
    public void testWrongGatewayEndpointConnection() throws Exception {
        AkkaRpcActorTest.DummyRpcEndpoint rpcEndpoint = new AkkaRpcActorTest.DummyRpcEndpoint(AkkaRpcActorHandshakeTest.akkaRpcService1);
        start();
        CompletableFuture<AkkaRpcActorHandshakeTest.WrongRpcGateway> futureGateway = AkkaRpcActorHandshakeTest.akkaRpcService2.connect(getAddress(), AkkaRpcActorHandshakeTest.WrongRpcGateway.class);
        try {
            futureGateway.get(AkkaRpcActorHandshakeTest.timeout.getSize(), AkkaRpcActorHandshakeTest.timeout.getUnit());
            Assert.fail("We expected a HandshakeException.");
        } catch (ExecutionException executionException) {
            Assert.assertThat(ExceptionUtils.stripExecutionException(executionException), Matchers.instanceOf(HandshakeException.class));
        } finally {
            RpcUtils.terminateRpcEndpoint(rpcEndpoint, AkkaRpcActorHandshakeTest.timeout);
        }
    }

    private static class WrongVersionAkkaRpcService extends AkkaRpcService {
        WrongVersionAkkaRpcService(ActorSystem actorSystem, AkkaRpcServiceConfiguration configuration) {
            super(actorSystem, configuration);
        }

        @Override
        protected int getVersion() {
            return -1;
        }
    }

    private interface WrongRpcGateway extends RpcGateway {
        CompletableFuture<Boolean> barfoo();

        void tell(String message);
    }
}

