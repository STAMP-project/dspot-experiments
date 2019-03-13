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
package org.apache.flink.runtime.webmonitor.retriever.impl;


import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.runtime.highavailability.HighAvailabilityServices;
import org.apache.flink.runtime.leaderretrieval.SettableLeaderRetrievalService;
import org.apache.flink.runtime.rpc.FencedRpcGateway;
import org.apache.flink.runtime.rpc.RpcEndpoint;
import org.apache.flink.runtime.rpc.RpcService;
import org.apache.flink.runtime.rpc.RpcTimeout;
import org.apache.flink.runtime.rpc.RpcUtils;
import org.apache.flink.runtime.rpc.TestingRpcService;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link RpcGatewayRetriever}.
 */
public class RpcGatewayRetrieverTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10L);

    private static TestingRpcService rpcService;

    /**
     * Tests that the RpcGatewayRetriever can retrieve the specified gateway type from a leader retrieval service.
     */
    @Test
    public void testRpcGatewayRetrieval() throws Exception {
        final String expectedValue = "foobar";
        final String expectedValue2 = "barfoo";
        final UUID leaderSessionId = UUID.randomUUID();
        RpcGatewayRetriever<UUID, RpcGatewayRetrieverTest.DummyGateway> gatewayRetriever = new RpcGatewayRetriever(RpcGatewayRetrieverTest.rpcService, RpcGatewayRetrieverTest.DummyGateway.class, Function.identity(), 0, Time.milliseconds(0L));
        SettableLeaderRetrievalService settableLeaderRetrievalService = new SettableLeaderRetrievalService();
        RpcGatewayRetrieverTest.DummyRpcEndpoint dummyRpcEndpoint = new RpcGatewayRetrieverTest.DummyRpcEndpoint(RpcGatewayRetrieverTest.rpcService, "dummyRpcEndpoint1", expectedValue);
        RpcGatewayRetrieverTest.DummyRpcEndpoint dummyRpcEndpoint2 = new RpcGatewayRetrieverTest.DummyRpcEndpoint(RpcGatewayRetrieverTest.rpcService, "dummyRpcEndpoint2", expectedValue2);
        RpcGatewayRetrieverTest.rpcService.registerGateway(getAddress(), getSelfGateway(RpcGatewayRetrieverTest.DummyGateway.class));
        RpcGatewayRetrieverTest.rpcService.registerGateway(getAddress(), getSelfGateway(RpcGatewayRetrieverTest.DummyGateway.class));
        try {
            start();
            start();
            settableLeaderRetrievalService.start(gatewayRetriever);
            final CompletableFuture<RpcGatewayRetrieverTest.DummyGateway> gatewayFuture = gatewayRetriever.getFuture();
            Assert.assertFalse(gatewayFuture.isDone());
            settableLeaderRetrievalService.notifyListener(getAddress(), leaderSessionId);
            final RpcGatewayRetrieverTest.DummyGateway dummyGateway = gatewayFuture.get(RpcGatewayRetrieverTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
            Assert.assertEquals(getAddress(), getAddress());
            Assert.assertEquals(expectedValue, dummyGateway.foobar(RpcGatewayRetrieverTest.TIMEOUT).get(RpcGatewayRetrieverTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS));
            // elect a new leader
            settableLeaderRetrievalService.notifyListener(getAddress(), leaderSessionId);
            final CompletableFuture<RpcGatewayRetrieverTest.DummyGateway> gatewayFuture2 = gatewayRetriever.getFuture();
            final RpcGatewayRetrieverTest.DummyGateway dummyGateway2 = gatewayFuture2.get(RpcGatewayRetrieverTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS);
            Assert.assertEquals(getAddress(), getAddress());
            Assert.assertEquals(expectedValue2, dummyGateway2.foobar(RpcGatewayRetrieverTest.TIMEOUT).get(RpcGatewayRetrieverTest.TIMEOUT.toMilliseconds(), TimeUnit.MILLISECONDS));
        } finally {
            RpcUtils.terminateRpcEndpoint(dummyRpcEndpoint, RpcGatewayRetrieverTest.TIMEOUT);
            RpcUtils.terminateRpcEndpoint(dummyRpcEndpoint2, RpcGatewayRetrieverTest.TIMEOUT);
        }
    }

    /**
     * Testing RpcGateway.
     */
    public interface DummyGateway extends FencedRpcGateway<UUID> {
        CompletableFuture<String> foobar(@RpcTimeout
        Time timeout);
    }

    static class DummyRpcEndpoint extends RpcEndpoint implements RpcGatewayRetrieverTest.DummyGateway {
        private final String value;

        protected DummyRpcEndpoint(RpcService rpcService, String endpointId, String value) {
            super(rpcService, endpointId);
            this.value = value;
        }

        @Override
        public CompletableFuture<String> foobar(Time timeout) {
            return CompletableFuture.completedFuture(value);
        }

        @Override
        public UUID getFencingToken() {
            return HighAvailabilityServices.DEFAULT_LEADER_ID;
        }
    }
}

