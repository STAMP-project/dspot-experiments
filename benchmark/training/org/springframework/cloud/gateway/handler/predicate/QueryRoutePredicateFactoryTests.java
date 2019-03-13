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


import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
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
public class QueryRoutePredicateFactoryTests extends BaseWebClientTests {
    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void noQueryParamWorks() {
        testClient.get().uri("/get").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "default_path_to_httpbin");
        output.expect(Matchers.not(Matchers.containsString("Error applying predicate for route: foo_query_param")));
    }

    @Test
    public void queryParamWorks() {
        testClient.get().uri("/get?foo=bar").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "foo_query_param");
    }

    @Test
    public void emptyQueryParamWorks() {
        testClient.get().uri("/get?foo").exchange().expectStatus().isOk().expectHeader().valueEquals(BaseWebClientTests.ROUTE_ID_HEADER, "default_path_to_httpbin");
        output.expect(Matchers.not(Matchers.containsString("Error applying predicate for route: foo_query_param")));
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Value("${test.uri}")
        private String uri;

        @Bean
        RouteLocator queryRouteLocator(RouteLocatorBuilder builder) {
            return builder.routes().route("foo_query_param", ( r) -> r.query("foo", "bar").filters(( f) -> f.prefixPath("/httpbin")).uri(uri)).build();
        }
    }
}

