/**
 * Copyright 2018 LINE Corporation
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


import HttpMethod.GET;
import com.linecorp.armeria.client.ClientRequestContext;
import com.linecorp.armeria.client.ClientRequestContextBuilder;
import com.linecorp.armeria.client.Endpoint;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.testing.server.ServerRule;
import java.util.function.Function;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class CircuitBreakerHttpClientTest {
    private static final String remoteServiceName = "testService";

    private static final ClientRequestContext ctx = ClientRequestContextBuilder.of(HttpRequest.of(GET, "/")).endpoint(Endpoint.of("dummyhost", 8080)).build();

    @ClassRule
    public static final ServerRule server = new ServerRule() {
        @Override
        protected void configure(ServerBuilder sb) throws Exception {
            sb.service("/unavailable", ( ctx, req) -> HttpResponse.of(HttpStatus.SERVICE_UNAVAILABLE));
        }
    };

    @Test
    public void testPerMethodDecorator() {
        final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        Mockito.when(circuitBreaker.canRequest()).thenReturn(false);
        @SuppressWarnings("unchecked")
        final Function<String, CircuitBreaker> factory = Mockito.mock(Function.class);
        Mockito.when(factory.apply(ArgumentMatchers.any())).thenReturn(circuitBreaker);
        final int COUNT = 2;
        CircuitBreakerHttpClientTest.failFastInvocation(CircuitBreakerHttpClient.newPerMethodDecorator(factory, CircuitBreakerHttpClientTest.strategy()), GET, COUNT);
        Mockito.verify(circuitBreaker, Mockito.times(COUNT)).canRequest();
        Mockito.verify(factory, Mockito.times(1)).apply("GET");
    }

    @Test
    public void testPerHostDecorator() {
        final CircuitBreaker circuitBreaker = Mockito.mock(CircuitBreaker.class);
        Mockito.when(circuitBreaker.canRequest()).thenReturn(false);
        @SuppressWarnings("unchecked")
        final Function<String, CircuitBreaker> factory = Mockito.mock(Function.class);
        Mockito.when(factory.apply(ArgumentMatchers.any())).thenReturn(circuitBreaker);
        final int COUNT = 2;
        CircuitBreakerHttpClientTest.failFastInvocation(CircuitBreakerHttpClient.newPerHostDecorator(factory, CircuitBreakerHttpClientTest.strategy()), GET, COUNT);
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
        CircuitBreakerHttpClientTest.failFastInvocation(CircuitBreakerHttpClient.newPerHostAndMethodDecorator(factory, CircuitBreakerHttpClientTest.strategy()), GET, COUNT);
        Mockito.verify(circuitBreaker, Mockito.times(COUNT)).canRequest();
        Mockito.verify(factory, Mockito.times(1)).apply("dummyhost:8080#GET");
    }

    @Test
    public void strategyWithoutContent() {
        final CircuitBreakerStrategy strategy = CircuitBreakerStrategy.onServerErrorStatus();
        CircuitBreakerHttpClientTest.circuitBreakerIsOpenOnServerError(new CircuitBreakerHttpClientBuilder(strategy));
    }

    @Test
    public void strategyWithContent() {
        final CircuitBreakerStrategyWithContent<HttpResponse> strategy = ( ctx, response) -> response.aggregate().handle(( msg, unused1) -> (msg.status().codeClass()) != HttpStatusClass.SERVER_ERROR);
        CircuitBreakerHttpClientTest.circuitBreakerIsOpenOnServerError(new CircuitBreakerHttpClientBuilder(strategy));
    }
}

