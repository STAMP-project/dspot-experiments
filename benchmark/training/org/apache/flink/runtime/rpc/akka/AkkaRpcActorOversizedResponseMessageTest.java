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


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.rpc.RpcEndpoint;
import org.apache.flink.runtime.rpc.RpcGateway;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.akka.exceptions.AkkaRpcException;
import org.apache.flink.runtime.rpc.exceptions.RpcException;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the over sized response message handling of the {@link AkkaRpcActor}.
 */
public class AkkaRpcActorOversizedResponseMessageTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10L);

    private static final int FRAMESIZE = 32000;

    private static final String OVERSIZED_PAYLOAD = new String(new byte[AkkaRpcActorOversizedResponseMessageTest.FRAMESIZE]);

    private static final String PAYLOAD = "Hello";

    private static RpcService rpcService1;

    private static RpcService rpcService2;

    @Test
    public void testOverSizedResponseMsgAsync() throws Exception {
        try {
            runRemoteMessageResponseTest(AkkaRpcActorOversizedResponseMessageTest.OVERSIZED_PAYLOAD, this::requestMessageAsync);
            Assert.fail("Expected the RPC to fail.");
        } catch (ExecutionException e) {
            Assert.assertThat(ExceptionUtils.findThrowable(e, AkkaRpcException.class).isPresent(), Matchers.is(true));
        }
    }

    @Test
    public void testNormalSizedResponseMsgAsync() throws Exception {
        final String message = runRemoteMessageResponseTest(AkkaRpcActorOversizedResponseMessageTest.PAYLOAD, this::requestMessageAsync);
        Assert.assertThat(message, Matchers.is(Matchers.equalTo(AkkaRpcActorOversizedResponseMessageTest.PAYLOAD)));
    }

    @Test
    public void testNormalSizedResponseMsgSync() throws Exception {
        final String message = runRemoteMessageResponseTest(AkkaRpcActorOversizedResponseMessageTest.PAYLOAD, AkkaRpcActorOversizedResponseMessageTest.MessageRpcGateway::messageSync);
        Assert.assertThat(message, Matchers.is(Matchers.equalTo(AkkaRpcActorOversizedResponseMessageTest.PAYLOAD)));
    }

    @Test
    public void testOverSizedResponseMsgSync() throws Exception {
        try {
            runRemoteMessageResponseTest(AkkaRpcActorOversizedResponseMessageTest.OVERSIZED_PAYLOAD, AkkaRpcActorOversizedResponseMessageTest.MessageRpcGateway::messageSync);
            Assert.fail("Expected the RPC to fail.");
        } catch (RpcException e) {
            Assert.assertThat(ExceptionUtils.findThrowable(e, AkkaRpcException.class).isPresent(), Matchers.is(true));
        }
    }

    /**
     * Tests that we can send arbitrarily large objects when communicating locally with
     * the rpc endpoint.
     */
    @Test
    public void testLocalOverSizedResponseMsgSync() throws Exception {
        final String message = runLocalMessageResponseTest(AkkaRpcActorOversizedResponseMessageTest.OVERSIZED_PAYLOAD, AkkaRpcActorOversizedResponseMessageTest.MessageRpcGateway::messageSync);
        Assert.assertThat(message, Matchers.is(Matchers.equalTo(AkkaRpcActorOversizedResponseMessageTest.OVERSIZED_PAYLOAD)));
    }

    /**
     * Tests that we can send arbitrarily large objects when communicating locally with
     * the rpc endpoint.
     */
    @Test
    public void testLocalOverSizedResponseMsgAsync() throws Exception {
        final String message = runLocalMessageResponseTest(AkkaRpcActorOversizedResponseMessageTest.OVERSIZED_PAYLOAD, this::requestMessageAsync);
        Assert.assertThat(message, Matchers.is(Matchers.equalTo(AkkaRpcActorOversizedResponseMessageTest.OVERSIZED_PAYLOAD)));
    }

    // -------------------------------------------------------------------------
    interface MessageRpcGateway extends RpcGateway {
        CompletableFuture<String> messageAsync();

        String messageSync() throws RpcException;
    }

    static class MessageRpcEndpoint extends RpcEndpoint implements AkkaRpcActorOversizedResponseMessageTest.MessageRpcGateway {
        @Nonnull
        private final String message;

        MessageRpcEndpoint(RpcService rpcService, @Nonnull
        String message) {
            super(rpcService);
            this.message = message;
        }

        @Override
        public CompletableFuture<String> messageAsync() {
            return CompletableFuture.completedFuture(message);
        }

        @Override
        public String messageSync() throws RpcException {
            return message;
        }
    }
}

