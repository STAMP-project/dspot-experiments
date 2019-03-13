/**
 * Copyright 2017-2019 the original author or authors.
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
package org.springframework.cloud.gateway.route;


import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.AddResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RemoveResponseHeaderGatewayFilterFactory;
import org.springframework.cloud.gateway.handler.predicate.HostRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.handler.predicate.RoutePredicateFactory;
import org.springframework.core.convert.support.DefaultConversionService;


/**
 *
 *
 * @author Toshiaki Maki
 */
public class RouteDefinitionRouteLocatorTests {
    @Test
    public void contextLoads() {
        List<RoutePredicateFactory> predicates = Arrays.asList(new HostRoutePredicateFactory());
        List<GatewayFilterFactory> gatewayFilterFactories = Arrays.asList(new RemoveResponseHeaderGatewayFilterFactory(), new AddResponseHeaderGatewayFilterFactory(), new RouteDefinitionRouteLocatorTests.TestOrderedGatewayFilterFactory());
        GatewayProperties gatewayProperties = new GatewayProperties();
        gatewayProperties.setRoutes(Arrays.asList(new RouteDefinition() {
            {
                setId("foo");
                setUri(URI.create("http://foo.example.com"));
                setPredicates(Arrays.asList(new PredicateDefinition("Host=*.example.com")));
                setFilters(Arrays.asList(new FilterDefinition("RemoveResponseHeader=Server"), new FilterDefinition("TestOrdered="), new FilterDefinition("AddResponseHeader=X-Response-Foo, Bar")));
            }
        }));
        RouteDefinitionRouteLocator routeDefinitionRouteLocator = new RouteDefinitionRouteLocator(new org.springframework.cloud.gateway.config.PropertiesRouteDefinitionLocator(gatewayProperties), predicates, gatewayFilterFactories, gatewayProperties, new DefaultConversionService());
        List<Route> routes = routeDefinitionRouteLocator.getRoutes().collectList().block();
        List<GatewayFilter> filters = routes.get(0).getFilters();
        assertThat(filters).hasSize(3);
        assertThat(getFilterClassName(filters.get(0))).startsWith("RemoveResponseHeader");
        assertThat(getFilterClassName(filters.get(1))).startsWith("AddResponseHeader");
        assertThat(getFilterClassName(filters.get(2))).startsWith("RouteDefinitionRouteLocatorTests$TestOrderedGateway");
    }

    static class TestOrderedGatewayFilterFactory extends AbstractGatewayFilterFactory {
        @Override
        public GatewayFilter apply(Object config) {
            return new OrderedGatewayFilter(( exchange, chain) -> chain.filter(exchange), 9999);
        }
    }
}

