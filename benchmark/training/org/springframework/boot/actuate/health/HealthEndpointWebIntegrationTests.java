/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.actuate.health;


import HttpStatus.SERVICE_UNAVAILABLE;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.actuate.endpoint.web.test.WebEndpointRunners;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static ShowDetails.ALWAYS;


/**
 * Integration tests for {@link HealthEndpoint} and {@link HealthEndpointWebExtension}
 * exposed by Jersey, Spring MVC, and WebFlux.
 *
 * @author Andy Wilkinson
 */
@RunWith(WebEndpointRunners.class)
public class HealthEndpointWebIntegrationTests {
    private static WebTestClient client;

    private static ConfigurableApplicationContext context;

    @Test
    public void whenHealthIsUp200ResponseIsReturned() {
        HealthEndpointWebIntegrationTests.client.get().uri("/actuator/health").exchange().expectStatus().isOk().expectBody().jsonPath("status").isEqualTo("UP").jsonPath("details.alpha.status").isEqualTo("UP").jsonPath("details.bravo.status").isEqualTo("UP");
    }

    @Test
    public void whenHealthIsDown503ResponseIsReturned() throws Exception {
        withHealthIndicator("charlie", () -> Health.down().build(), () -> Mono.just(Health.down().build()), () -> {
            HealthEndpointWebIntegrationTests.client.get().uri("/actuator/health").exchange().expectStatus().isEqualTo(SERVICE_UNAVAILABLE).expectBody().jsonPath("status").isEqualTo("DOWN").jsonPath("details.alpha.status").isEqualTo("UP").jsonPath("details.bravo.status").isEqualTo("UP").jsonPath("details.charlie.status").isEqualTo("DOWN");
            return null;
        });
    }

    @Test
    public void whenComponentHealthIsDown503ResponseIsReturned() throws Exception {
        withHealthIndicator("charlie", () -> Health.down().build(), () -> Mono.just(Health.down().build()), () -> {
            HealthEndpointWebIntegrationTests.client.get().uri("/actuator/health/charlie").exchange().expectStatus().isEqualTo(SERVICE_UNAVAILABLE).expectBody().jsonPath("status").isEqualTo("DOWN");
            return null;
        });
    }

    @Test
    public void whenComponentInstanceHealthIsDown503ResponseIsReturned() throws Exception {
        CompositeHealthIndicator composite = new CompositeHealthIndicator(new OrderedHealthAggregator(), Collections.singletonMap("one", () -> Health.down().build()));
        CompositeReactiveHealthIndicator reactiveComposite = new CompositeReactiveHealthIndicator(new OrderedHealthAggregator(), new DefaultReactiveHealthIndicatorRegistry(Collections.singletonMap("one", () -> Mono.just(Health.down().build()))));
        withHealthIndicator("charlie", composite, reactiveComposite, () -> {
            HealthEndpointWebIntegrationTests.client.get().uri("/actuator/health/charlie/one").exchange().expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE).expectBody().jsonPath("status").isEqualTo("DOWN");
            return null;
        });
    }

    @Test
    public void whenHealthIndicatorIsRemovedResponseIsAltered() {
        Consumer<String> reactiveRegister = null;
        try {
            ReactiveHealthIndicatorRegistry registry = HealthEndpointWebIntegrationTests.context.getBean(ReactiveHealthIndicatorRegistry.class);
            ReactiveHealthIndicator unregistered = registry.unregister("bravo");
            reactiveRegister = ( name) -> registry.register(name, unregistered);
        } catch (NoSuchBeanDefinitionException ex) {
            // Continue
        }
        HealthIndicatorRegistry registry = HealthEndpointWebIntegrationTests.context.getBean(HealthIndicatorRegistry.class);
        HealthIndicator bravo = registry.unregister("bravo");
        try {
            HealthEndpointWebIntegrationTests.client.get().uri("/actuator/health").exchange().expectStatus().isOk().expectBody().jsonPath("status").isEqualTo("UP").jsonPath("details.alpha.status").isEqualTo("UP").jsonPath("details.bravo.status").doesNotExist();
        } finally {
            registry.register("bravo", bravo);
            if (reactiveRegister != null) {
                reactiveRegister.accept("bravo");
            }
        }
    }

    @Configuration
    public static class TestConfiguration {
        @Bean
        public HealthIndicatorRegistry healthIndicatorFactory(Map<String, HealthIndicator> healthIndicators) {
            return new HealthIndicatorRegistryFactory().createHealthIndicatorRegistry(healthIndicators);
        }

        @Bean
        @ConditionalOnWebApplication(type = Type.REACTIVE)
        public ReactiveHealthIndicatorRegistry reactiveHealthIndicatorRegistry(Map<String, ReactiveHealthIndicator> reactiveHealthIndicators, Map<String, HealthIndicator> healthIndicators) {
            return new ReactiveHealthIndicatorRegistryFactory().createReactiveHealthIndicatorRegistry(reactiveHealthIndicators, healthIndicators);
        }

        @Bean
        public HealthEndpoint healthEndpoint(HealthIndicatorRegistry registry) {
            return new HealthEndpoint(new CompositeHealthIndicator(new OrderedHealthAggregator(), registry));
        }

        @Bean
        @ConditionalOnWebApplication(type = Type.SERVLET)
        public HealthEndpointWebExtension healthWebEndpointExtension(HealthEndpoint healthEndpoint) {
            return new HealthEndpointWebExtension(healthEndpoint, new HealthWebEndpointResponseMapper(new HealthStatusHttpMapper(), ALWAYS, new HashSet(Arrays.asList("ACTUATOR"))));
        }

        @Bean
        @ConditionalOnWebApplication(type = Type.REACTIVE)
        public ReactiveHealthEndpointWebExtension reactiveHealthWebEndpointExtension(ReactiveHealthIndicatorRegistry registry, HealthEndpoint healthEndpoint) {
            return new ReactiveHealthEndpointWebExtension(new CompositeReactiveHealthIndicator(new OrderedHealthAggregator(), registry), new HealthWebEndpointResponseMapper(new HealthStatusHttpMapper(), ALWAYS, new HashSet(Arrays.asList("ACTUATOR"))));
        }

        @Bean
        public HealthIndicator alphaHealthIndicator() {
            return () -> Health.up().build();
        }

        @Bean
        public HealthIndicator bravoHealthIndicator() {
            return () -> Health.up().build();
        }
    }
}

