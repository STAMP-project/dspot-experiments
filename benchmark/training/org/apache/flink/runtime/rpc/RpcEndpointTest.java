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
package org.apache.flink.runtime.rpc;


import akka.actor.ActorSystem;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the RpcEndpoint and its self gateways.
 */
public class RpcEndpointTest extends TestLogger {
    private static final Time TIMEOUT = Time.seconds(10L);

    private static ActorSystem actorSystem = null;

    private static RpcService rpcService = null;

    /**
     * Tests that we can obtain the self gateway from a RpcEndpoint and can interact with
     * it via the self gateway.
     */
    @Test
    public void testSelfGateway() throws Exception {
        int expectedValue = 1337;
        RpcEndpointTest.BaseEndpoint baseEndpoint = new RpcEndpointTest.BaseEndpoint(RpcEndpointTest.rpcService, expectedValue);
        try {
            start();
            RpcEndpointTest.BaseGateway baseGateway = getSelfGateway(RpcEndpointTest.BaseGateway.class);
            CompletableFuture<Integer> foobar = baseGateway.foobar();
            Assert.assertEquals(Integer.valueOf(expectedValue), foobar.get());
        } finally {
            RpcUtils.terminateRpcEndpoint(baseEndpoint, RpcEndpointTest.TIMEOUT);
        }
    }

    /**
     * Tests that we cannot accidentally obtain a wrong self gateway type which is
     * not implemented by the RpcEndpoint.
     */
    @Test(expected = RuntimeException.class)
    public void testWrongSelfGateway() throws Exception {
        int expectedValue = 1337;
        RpcEndpointTest.BaseEndpoint baseEndpoint = new RpcEndpointTest.BaseEndpoint(RpcEndpointTest.rpcService, expectedValue);
        try {
            start();
            RpcEndpointTest.DifferentGateway differentGateway = baseEndpoint.getSelfGateway(RpcEndpointTest.DifferentGateway.class);
            Assert.fail("Expected to fail with a RuntimeException since we requested the wrong gateway type.");
        } finally {
            RpcUtils.terminateRpcEndpoint(baseEndpoint, RpcEndpointTest.TIMEOUT);
        }
    }

    /**
     * Tests that we can extend existing RpcEndpoints and can communicate with them via the
     * self gateways.
     */
    @Test
    public void testEndpointInheritance() throws Exception {
        int foobar = 1;
        int barfoo = 2;
        String foo = "foobar";
        RpcEndpointTest.ExtendedEndpoint endpoint = new RpcEndpointTest.ExtendedEndpoint(RpcEndpointTest.rpcService, foobar, barfoo, foo);
        try {
            start();
            RpcEndpointTest.BaseGateway baseGateway = getSelfGateway(RpcEndpointTest.BaseGateway.class);
            RpcEndpointTest.ExtendedGateway extendedGateway = endpoint.getSelfGateway(RpcEndpointTest.ExtendedGateway.class);
            RpcEndpointTest.DifferentGateway differentGateway = endpoint.getSelfGateway(RpcEndpointTest.DifferentGateway.class);
            Assert.assertEquals(Integer.valueOf(foobar), baseGateway.foobar().get());
            Assert.assertEquals(Integer.valueOf(foobar), extendedGateway.foobar().get());
            Assert.assertEquals(Integer.valueOf(barfoo), extendedGateway.barfoo().get());
            Assert.assertEquals(foo, differentGateway.foo().get());
        } finally {
            RpcUtils.terminateRpcEndpoint(endpoint, RpcEndpointTest.TIMEOUT);
        }
    }

    public interface BaseGateway extends RpcGateway {
        CompletableFuture<Integer> foobar();
    }

    public interface ExtendedGateway extends RpcEndpointTest.BaseGateway {
        CompletableFuture<Integer> barfoo();
    }

    public interface DifferentGateway extends RpcGateway {
        CompletableFuture<String> foo();
    }

    public static class BaseEndpoint extends RpcEndpoint implements RpcEndpointTest.BaseGateway {
        private final int foobarValue;

        protected BaseEndpoint(RpcService rpcService, int foobarValue) {
            super(rpcService);
            this.foobarValue = foobarValue;
        }

        @Override
        public CompletableFuture<Integer> foobar() {
            return CompletableFuture.completedFuture(foobarValue);
        }
    }

    public static class ExtendedEndpoint extends RpcEndpointTest.BaseEndpoint implements RpcEndpointTest.DifferentGateway , RpcEndpointTest.ExtendedGateway {
        private final int barfooValue;

        private final String fooString;

        protected ExtendedEndpoint(RpcService rpcService, int foobarValue, int barfooValue, String fooString) {
            super(rpcService, foobarValue);
            this.barfooValue = barfooValue;
            this.fooString = fooString;
        }

        @Override
        public CompletableFuture<Integer> barfoo() {
            return CompletableFuture.completedFuture(barfooValue);
        }

        @Override
        public CompletableFuture<String> foo() {
            return CompletableFuture.completedFuture(fooString);
        }
    }
}

