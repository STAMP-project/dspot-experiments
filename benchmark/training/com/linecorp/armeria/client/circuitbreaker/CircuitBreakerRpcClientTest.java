/**
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client.circuitbreaker;


import com.google.common.testing.FakeTicker;
import com.linecorp.armeria.client.Client;
import com.linecorp.armeria.client.ClientRequestContext;
import com.linecorp.armeria.common.RpcRequest;
import com.linecorp.armeria.common.RpcResponse;
import com.linecorp.armeria.common.util.Exceptions;
import com.linecorp.armeria.testing.internal.AnticipatedException;
import java.time.Duration;
import java.util.function.Function;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CircuitBreakerRpcClientTest {
    private static final String remoteServiceName = "testService";

    // Remote invocation parameters
    private static final RpcRequest reqA = RpcRequest.of(Object.class, "methodA", "a", "b");

    private static final ClientRequestContext ctxA = ClientRequestContext.of(CircuitBreakerRpcClientTest.reqA, "h2c://dummyhost:8080/");

    private static final RpcRequest reqB = RpcRequest.of(Object.class, "methodB", "c", "d");

    private static final ClientRequestContext ctxB = ClientRequestContext.of(CircuitBreakerRpcClientTest.reqB, "h2c://dummyhost:8080/");

    private static final RpcResponse successRes = RpcResponse.of(null);

    private static final RpcResponse failureRes = RpcResponse.ofFailure(Exceptions.clearTrace(new AnticipatedException()));

    private static final Duration circuitOpenWindow = Duration.ofSeconds(60);

    private static final Duration counterSlidingWindow = Duration.ofSeconds(180);

    private static final Duration counterUpdateInterval = Duration.ofMillis(1);

    private static final int minimumRequestThreshold = 2;

    @Test
    public void testSingletonDecorator() {
        final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        Mockito.when(circuitBreaker.canRequest()).thenReturn(false);
        final int COUNT = 1;
        CircuitBreakerRpcClientTest.failFastInvocation(CircuitBreakerRpcClient.newDecorator(circuitBreaker, CircuitBreakerRpcClientTest.strategy()), COUNT);
        Mockito.verify(circuitBreaker, Mockito.times(COUNT)).canRequest();
    }

    @Test
    public void testPerMethodDecorator() {
        final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        Mockito.when(circuitBreaker.canRequest()).thenReturn(false);
        @SuppressWarnings("unchecked")
        final Function<String, CircuitBreaker> factory = Mockito.mock(Function.class);
        Mockito.when(factory.apply(ArgumentMatchers.any())).thenReturn(circuitBreaker);
        final int COUNT = 2;
        CircuitBreakerRpcClientTest.failFastInvocation(CircuitBreakerRpcClient.newPerMethodDecorator(factory, CircuitBreakerRpcClientTest.strategy()), COUNT);
        Mockito.verify(circuitBreaker, Mockito.times(COUNT)).canRequest();
        Mockito.verify(factory, Mockito.times(1)).apply("methodA");
    }

    @Test
    public void testPerHostDecorator() {
        final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        Mockito.when(circuitBreaker.canRequest()).thenReturn(false);
        @SuppressWarnings("unchecked")
        final Function<String, CircuitBreaker> factory = Mockito.mock(Function.class);
        Mockito.when(factory.apply(ArgumentMatchers.any())).thenReturn(circuitBreaker);
        final int COUNT = 2;
        CircuitBreakerRpcClientTest.failFastInvocation(CircuitBreakerRpcClient.newPerHostDecorator(factory, CircuitBreakerRpcClientTest.strategy()), COUNT);
        Mockito.verify(circuitBreaker, Mockito.times(COUNT)).canRequest();
        Mockito.verify(factory, Mockito.times(1)).apply("dummyhost:8080");
    }

    @Test
    public void testPerHostAndMethodDecorator() {
        final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        Mockito.when(circuitBreaker.canRequest()).thenReturn(false);
        @SuppressWarnings("unchecked")
        final Function<String, CircuitBreaker> factory = Mockito.mock(Function.class);
        Mockito.when(factory.apply(ArgumentMatchers.any())).thenReturn(circuitBreaker);
        final int COUNT = 2;
        CircuitBreakerRpcClientTest.failFastInvocation(CircuitBreakerRpcClient.newPerHostAndMethodDecorator(factory, CircuitBreakerRpcClientTest.strategy()), COUNT);
        Mockito.verify(circuitBreaker, Mockito.times(COUNT)).canRequest();
        Mockito.verify(factory, Mockito.times(1)).apply("dummyhost:8080#methodA");
    }

    @Test
    public void testDelegate() throws Exception {
        final FakeTicker ticker = new FakeTicker();
        final CircuitBreaker circuitBreaker = new CircuitBreakerBuilder(CircuitBreakerRpcClientTest.remoteServiceName).ticker(ticker).build();
        @SuppressWarnings("unchecked")
        final Client<RpcRequest, RpcResponse> delegate = Mockito.mock(Client.class);
        Mockito.when(delegate.execute(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CircuitBreakerRpcClientTest.successRes);
        final CircuitBreakerRpcClient stub = new CircuitBreakerRpcClient(delegate, ( ctx, req) -> circuitBreaker, CircuitBreakerRpcClientTest.strategy());
        stub.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA);
        Mockito.verify(delegate, Mockito.times(1)).execute(ArgumentMatchers.eq(CircuitBreakerRpcClientTest.ctxA), ArgumentMatchers.eq(CircuitBreakerRpcClientTest.reqA));
    }

    @Test
    public void testDelegateIfFailToGetCircuitBreaker() throws Exception {
        @SuppressWarnings("unchecked")
        final Client<RpcRequest, RpcResponse> delegate = Mockito.mock(Client.class);
        Mockito.when(delegate.execute(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(CircuitBreakerRpcClientTest.successRes);
        final CircuitBreakerMapping mapping = ( ctx, req) -> {
            throw Exceptions.clearTrace(new AnticipatedException("bug!"));
        };
        final CircuitBreakerRpcClient stub = new CircuitBreakerRpcClient(delegate, mapping, CircuitBreakerRpcClientTest.strategy());
        stub.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA);
        // make sure that remote service is invoked even if cb mapping is failed
        Mockito.verify(delegate, Mockito.times(1)).execute(ArgumentMatchers.eq(CircuitBreakerRpcClientTest.ctxA), ArgumentMatchers.eq(CircuitBreakerRpcClientTest.reqA));
    }

    @Test
    public void testStateTransition() throws Exception {
        final FakeTicker ticker = new FakeTicker();
        final CircuitBreaker circuitBreaker = CircuitBreakerRpcClientTest.buildCircuitBreaker(ticker);
        @SuppressWarnings("unchecked")
        final Client<RpcRequest, RpcResponse> delegate = Mockito.mock(Client.class);
        // return failed future
        Mockito.when(delegate.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA)).thenReturn(CircuitBreakerRpcClientTest.failureRes);
        final CircuitBreakerRpcClient stub = new CircuitBreakerRpcClient(delegate, ( ctx, req) -> circuitBreaker, CircuitBreakerRpcClientTest.strategy());
        // CLOSED
        for (int i = 0; i < ((CircuitBreakerRpcClientTest.minimumRequestThreshold) + 1); i++) {
            // Need to call execute() one more to change the state of the circuit breaker.
            assertThat(stub.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA).cause()).isInstanceOf(AnticipatedException.class);
            ticker.advance(Duration.ofMillis(1).toNanos());
        }
        // OPEN
        assertThatThrownBy(() -> stub.execute(ctxA, reqA)).isInstanceOf(FailFastException.class);
        ticker.advance(CircuitBreakerRpcClientTest.circuitOpenWindow.toNanos());
        // return success future
        Mockito.when(delegate.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA)).thenReturn(CircuitBreakerRpcClientTest.successRes);
        // HALF OPEN
        assertThat(stub.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA).join()).isNull();
        // CLOSED
        assertThat(stub.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA).join()).isNull();
    }

    @Test
    public void testServiceScope() throws Exception {
        final FakeTicker ticker = new FakeTicker();
        final CircuitBreaker circuitBreaker = CircuitBreakerRpcClientTest.buildCircuitBreaker(ticker);
        @SuppressWarnings("unchecked")
        final Client<RpcRequest, RpcResponse> delegate = Mockito.mock(Client.class);
        // Always return failed future for methodA
        Mockito.when(delegate.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA)).thenReturn(CircuitBreakerRpcClientTest.failureRes);
        // Always return success future for methodB
        Mockito.when(delegate.execute(CircuitBreakerRpcClientTest.ctxB, CircuitBreakerRpcClientTest.reqB)).thenReturn(CircuitBreakerRpcClientTest.successRes);
        final CircuitBreakerRpcClient stub = new CircuitBreakerRpcClient(delegate, ( ctx, req) -> circuitBreaker, CircuitBreakerRpcClientTest.strategy());
        // CLOSED
        for (int i = 0; i < ((CircuitBreakerRpcClientTest.minimumRequestThreshold) + 1); i++) {
            // Need to call execute() one more to change the state of the circuit breaker.
            assertThatThrownBy(() -> stub.execute(ctxA, reqA).join()).hasCauseInstanceOf(AnticipatedException.class);
            ticker.advance(Duration.ofMillis(1).toNanos());
        }
        // OPEN (methodA)
        assertThatThrownBy(() -> stub.execute(ctxA, reqA)).isInstanceOf(FailFastException.class);
        // OPEN (methodB)
        assertThatThrownBy(() -> stub.execute(ctxB, reqB)).isInstanceOf(FailFastException.class);
    }

    @Test
    public void testPerMethodScope() throws Exception {
        final FakeTicker ticker = new FakeTicker();
        final Function<String, CircuitBreaker> factory = ( method) -> CircuitBreakerRpcClientTest.buildCircuitBreaker(ticker);
        @SuppressWarnings("unchecked")
        final Client<RpcRequest, RpcResponse> delegate = Mockito.mock(Client.class);
        // Always return failed future for methodA
        Mockito.when(delegate.execute(CircuitBreakerRpcClientTest.ctxA, CircuitBreakerRpcClientTest.reqA)).thenReturn(CircuitBreakerRpcClientTest.failureRes);
        // Always return success future for methodB
        Mockito.when(delegate.execute(CircuitBreakerRpcClientTest.ctxB, CircuitBreakerRpcClientTest.reqB)).thenReturn(CircuitBreakerRpcClientTest.successRes);
        final CircuitBreakerRpcClient stub = CircuitBreakerRpcClient.newPerMethodDecorator(factory, CircuitBreakerRpcClientTest.strategy()).apply(delegate);
        // CLOSED (methodA)
        for (int i = 0; i < ((CircuitBreakerRpcClientTest.minimumRequestThreshold) + 1); i++) {
            // Need to call execute() one more to change the state of the circuit breaker.
            assertThatThrownBy(() -> stub.execute(ctxA, reqA).join()).hasCauseInstanceOf(AnticipatedException.class);
            ticker.advance(Duration.ofMillis(1).toNanos());
        }
        // OPEN (methodA)
        assertThatThrownBy(() -> stub.execute(ctxA, reqA)).isInstanceOf(FailFastException.class);
        // CLOSED (methodB)
        assertThat(stub.execute(CircuitBreakerRpcClientTest.ctxB, CircuitBreakerRpcClientTest.reqB).join()).isNull();
    }
}

