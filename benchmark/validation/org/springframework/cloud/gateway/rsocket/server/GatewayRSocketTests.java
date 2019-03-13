/**
 * Copyright 2018-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.gateway.rsocket.server;


import Registry.RegisteredEvent;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.rsocket.Payload;
import io.rsocket.RSocket;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.cloud.gateway.rsocket.autoconfigure.GatewayRSocketProperties;
import org.springframework.cloud.gateway.rsocket.registry.Registry;
import org.springframework.cloud.gateway.rsocket.route.Route;
import org.springframework.cloud.gateway.rsocket.route.Routes;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.test.StepVerifier;


/**
 *
 *
 * @author Rossen Stoyanchev
 */
/* private static class TestExceptionHandler implements WebExceptionHandler {

private Throwable ex;

@Override public Mono<Void> handle(GatewayExchange exchange, Throwable ex) {
this.ex = ex; return Mono.error(ex); } }
 */
public class GatewayRSocketTests {
    private static Log logger = LogFactory.getLog(GatewayRSocketTests.class);

    private Registry registry;

    private Payload incomingPayload;

    @Test
    public void multipleFilters() {
        GatewayRSocketTests.TestFilter filter1 = new GatewayRSocketTests.TestFilter();
        GatewayRSocketTests.TestFilter filter2 = new GatewayRSocketTests.TestFilter();
        GatewayRSocketTests.TestFilter filter3 = new GatewayRSocketTests.TestFilter();
        Payload payload = new GatewayRSocketTests.TestGatewayRSocket(registry, new GatewayRSocketTests.TestRoutes(filter1, filter2, filter3)).requestResponse(incomingPayload).block(Duration.ZERO);
        assertThat(filter1.invoked()).isTrue();
        assertThat(filter2.invoked()).isTrue();
        assertThat(filter3.invoked()).isTrue();
        assertThat(payload).isNotNull();
    }

    @Test
    public void zeroFilters() {
        Payload payload = new GatewayRSocketTests.TestGatewayRSocket(registry, new GatewayRSocketTests.TestRoutes()).requestResponse(incomingPayload).block(Duration.ZERO);
        assertThat(payload).isNotNull();
    }

    @Test
    public void shortcircuitFilter() {
        GatewayRSocketTests.TestFilter filter1 = new GatewayRSocketTests.TestFilter();
        GatewayRSocketTests.ShortcircuitingFilter filter2 = new GatewayRSocketTests.ShortcircuitingFilter();
        GatewayRSocketTests.TestFilter filter3 = new GatewayRSocketTests.TestFilter();
        GatewayRSocketTests.TestGatewayRSocket gatewayRSocket = new GatewayRSocketTests.TestGatewayRSocket(registry, new GatewayRSocketTests.TestRoutes(filter1, filter2, filter3));
        Mono<Payload> response = gatewayRSocket.requestResponse(incomingPayload);
        // a false filter will create a pending rsocket that blocks forever
        // this tweaks the rsocket to compelte.
        gatewayRSocket.processor.onNext(null);
        StepVerifier.withVirtualTime(() -> response).expectSubscription().verifyComplete();
        assertThat(filter1.invoked()).isTrue();
        assertThat(filter2.invoked()).isTrue();
        assertThat(filter3.invoked()).isFalse();
    }

    @Test
    public void asyncFilter() {
        GatewayRSocketTests.AsyncFilter filter = new GatewayRSocketTests.AsyncFilter();
        Payload payload = new GatewayRSocketTests.TestGatewayRSocket(registry, new GatewayRSocketTests.TestRoutes(filter)).requestResponse(incomingPayload).block(Duration.ofSeconds(5));
        assertThat(filter.invoked()).isTrue();
        assertThat(payload).isNotNull();
    }

    // TODO: add exception handlers?
    @Test(expected = IllegalStateException.class)
    public void handleErrorFromFilter() {
        GatewayRSocketTests.ExceptionFilter filter = new GatewayRSocketTests.ExceptionFilter();
        new GatewayRSocketTests.TestGatewayRSocket(registry, new GatewayRSocketTests.TestRoutes(filter)).requestResponse(incomingPayload).block(Duration.ofSeconds(5));
        // assertNull(socket);
    }

    private static class TestGatewayRSocket extends GatewayRSocket {
        private final MonoProcessor<RSocket> processor = MonoProcessor.create();

        TestGatewayRSocket(Registry registry, Routes routes) {
            super(registry, routes, new SimpleMeterRegistry(), new GatewayRSocketProperties(), GatewayRSocketTests.getMetadata());
        }

        @Override
        PendingRequestRSocket constructPendingRSocket(GatewayExchange exchange) {
            Function<Registry.RegisteredEvent, Mono<Route>> routeFinder = ( registeredEvent) -> getRouteMono(registeredEvent, exchange);
            return new PendingRequestRSocket(routeFinder, ( map) -> {
                Tags tags = exchange.getTags().and("responder.id", map.get("id"));
                exchange.setTags(tags);
            }, processor);
        }

        public MonoProcessor<RSocket> getProcessor() {
            return processor;
        }
    }

    private static class TestRoutes implements Routes {
        private final Route route;

        private List<GatewayFilter> filters;

        TestRoutes() {
            this(Collections.emptyList());
        }

        TestRoutes(GatewayFilter... filters) {
            this(Arrays.asList(filters));
        }

        TestRoutes(List<GatewayFilter> filters) {
            this.filters = filters;
            route = Route.builder().id("route1").routingMetadata(org.springframework.cloud.gateway.rsocket.support.Metadata.from("mock").build()).predicate(( exchange) -> Mono.just(true)).filters(filters).build();
        }

        @Override
        public Flux<Route> getRoutes() {
            return Flux.just(route);
        }
    }

    private static class TestFilter implements GatewayFilter {
        private volatile boolean invoked;

        public boolean invoked() {
            return this.invoked;
        }

        @Override
        public Mono<Success> filter(GatewayExchange exchange, GatewayFilterChain chain) {
            this.invoked = true;
            return doFilter(exchange, chain);
        }

        public Mono<Success> doFilter(GatewayExchange exchange, GatewayFilterChain chain) {
            return chain.filter(exchange);
        }
    }

    private static class ShortcircuitingFilter extends GatewayRSocketTests.TestFilter {
        @Override
        public Mono<Success> doFilter(GatewayExchange exchange, GatewayFilterChain chain) {
            return Mono.empty();
        }
    }

    private static class AsyncFilter extends GatewayRSocketTests.TestFilter {
        @Override
        public Mono<Success> doFilter(GatewayExchange exchange, GatewayFilterChain chain) {
            return doAsyncWork().flatMap(( asyncResult) -> {
                org.springframework.cloud.gateway.rsocket.server.logger.debug(("Async result: " + asyncResult));
                return chain.filter(exchange);
            });
        }

        private Mono<String> doAsyncWork() {
            return Mono.delay(Duration.ofMillis(100L)).map(( l) -> "123");
        }
    }

    private static class ExceptionFilter implements GatewayFilter {
        @Override
        public Mono<Success> filter(GatewayExchange exchange, GatewayFilterChain chain) {
            return Mono.error(new IllegalStateException("boo"));
        }
    }
}

