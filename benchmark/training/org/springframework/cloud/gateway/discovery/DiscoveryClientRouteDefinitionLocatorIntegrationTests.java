/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.discovery;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DiscoveryClientRouteDefinitionLocatorIntegrationTests.Config.class, properties = { "spring.cloud.gateway.discovery.locator.enabled=true", "spring.cloud.gateway.discovery.locator.route-id-prefix=test__" })
public class DiscoveryClientRouteDefinitionLocatorIntegrationTests {
    @Autowired
    private RouteLocator routeLocator;

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private DiscoveryClientRouteDefinitionLocatorIntegrationTests.TestDiscoveryClient discoveryClient;

    @Test
    public void newServiceAddsRoute() {
        List<Route> routes = routeLocator.getRoutes().filter(( route) -> route.getId().startsWith("test__")).collectList().block();
        assertThat(routes).hasSize(1);
        discoveryClient.multiple();
        publisher.publishEvent(new HeartbeatEvent(this, 1L));
        routes = routeLocator.getRoutes().filter(( route) -> route.getId().startsWith("test__")).collectList().block();
        assertThat(routes).hasSize(2);
    }

    @SpringBootConfiguration
    @EnableAutoConfiguration
    protected static class Config {
        @Bean
        DiscoveryClientRouteDefinitionLocatorIntegrationTests.TestDiscoveryClient discoveryClient() {
            return new DiscoveryClientRouteDefinitionLocatorIntegrationTests.TestDiscoveryClient();
        }
    }

    private static class TestDiscoveryClient implements DiscoveryClient {
        AtomicBoolean single = new AtomicBoolean(true);

        DefaultServiceInstance instance1 = new DefaultServiceInstance("service1_1", "service1", "localhost", 8001, false);

        DefaultServiceInstance instance2 = new DefaultServiceInstance("service2_1", "service2", "localhost", 8001, false);

        public void multiple() {
            single.set(false);
        }

        @Override
        public String description() {
            return null;
        }

        @Override
        public List<ServiceInstance> getInstances(String serviceId) {
            if (serviceId.equals("service1")) {
                return Collections.singletonList(instance1);
            }
            if (serviceId.equals("service2")) {
                return Collections.singletonList(instance2);
            }
            return Collections.emptyList();
        }

        @Override
        public List<String> getServices() {
            if (single.get()) {
                return Collections.singletonList("service1");
            }
            return Arrays.asList("service1", "service2");
        }
    }
}

