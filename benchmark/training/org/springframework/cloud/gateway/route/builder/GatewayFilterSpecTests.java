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
package org.springframework.cloud.gateway.route.builder;


import Route.AsyncBuilder;
import RouteLocatorBuilder.Builder;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


public class GatewayFilterSpecTests {
    @Test
    public void orderedInterfaceRespected() {
        testFilter(GatewayFilterSpecTests.MyOrderedFilter.class, new GatewayFilterSpecTests.MyOrderedFilter(), 1000);
    }

    @Test
    public void unorderedWithDefaultOrder() {
        testFilter(OrderedGatewayFilter.class, new GatewayFilterSpecTests.MyUnorderedFilter(), 0);
    }

    @Test
    public void testFilters() {
        ConfigurableApplicationContext context = Mockito.mock(ConfigurableApplicationContext.class);
        Route.AsyncBuilder routeBuilder = Route.async().id("123").uri("abc:123").predicate(( exchange) -> true);
        RouteLocatorBuilder.Builder routes = routes();
        GatewayFilterSpec spec = new GatewayFilterSpec(routeBuilder, routes);
        spec.filters(new GatewayFilterSpecTests.MyUnorderedFilter(), new GatewayFilterSpecTests.MyOrderedFilter());
        Route route = routeBuilder.build();
        assertThat(route.getFilters()).hasSize(2);
        assertFilter(route.getFilters().get(0), OrderedGatewayFilter.class, 0);
        assertFilter(route.getFilters().get(1), GatewayFilterSpecTests.MyOrderedFilter.class, 1000);
    }

    protected static class MyOrderedFilter implements GatewayFilter , Ordered {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return Mono.empty();
        }

        @Override
        public int getOrder() {
            return 1000;
        }
    }

    protected static class MyUnorderedFilter implements GatewayFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            return Mono.empty();
        }
    }
}

