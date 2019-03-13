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
package org.springframework.cloud.gateway.handler.predicate;


import HttpHeaders.HOST;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.handler.RoutePredicateHandlerMapping;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class BetweenRoutePredicateFactoryIntegrationTests extends BaseWebClientTests {
    @Test
    public void betweenPredicateWithValidDates() {
        testClient.get().uri("/get").header(HOST, "www.betweenvalid.org").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.HANDLER_MAPPER_HEADER, RoutePredicateHandlerMapping.class.getSimpleName()).expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "test_between_valid");
    }

    @Test
    public void notBetweenPredicateWorks() {
        // should NOT be not_between_test because Between dates are in the past
        testClient.get().uri("/get").header(HOST, "www.notbetween.org").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.HANDLER_MAPPER_HEADER, RoutePredicateHandlerMapping.class.getSimpleName()).expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "default_path_to_httpbin");
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Value("${test.uri}")
        String uri;

        @Bean
        public RouteLocator testRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("test_between_valid", ( r) -> r.host("**.betweenvalid.org").and().between(java.time.ZonedDateTime.now().minusDays(1), java.time.ZonedDateTime.now().plusDays(1)).filters(( f) -> f.prefixPath("/httpbin")).uri(uri)).build();
        }
    }
}

