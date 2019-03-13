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
package org.springframework.cloud.gateway.filter.factory;


import com.fasterxml.jackson.databind.JsonNode;
import java.net.URI;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.test.BaseWebClientTests;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;


/**
 *
 *
 * @author Toshiaki Maki
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class RequestHeaderToRequestUriGatewayFilterFactoryIntegrationTests extends BaseWebClientTests {
    @LocalServerPort
    int port;

    @Test
    public void changeUriWorkWithCustomLogic() {
        testClient.get().uri(( b) -> b.path("/").queryParam("url", (("http://localhost:" + (port)) + "/actuator/health")).build()).header("Host", "www.changeuri.org").exchange().expectBody(JsonNode.class).consumeWith(( r) -> assertThat(r.getResponseBody().has("status")).isTrue());
    }

    @EnableAutoConfiguration
    @SpringBootConfiguration
    @Import(BaseWebClientTests.DefaultTestConfig.class)
    public static class TestConfig {
        @Bean
        public RouteLocator routeLocator(RouteLocatorBuilder builder) {
            return builder.routes().route(( r) -> r.host("**.changeuri.org").and().header("X-Next-Url").filters(( f) -> f.requestHeaderToRequestUri("X-Next-Url")).uri("http://example.com")).route(( r) -> r.host("**.changeuri.org").and().query("url").filters(( f) -> f.changeRequestUri(( e) -> Optional.of(URI.create(e.getRequest().getQueryParams().getFirst("url"))))).uri("http://example.com")).build();
        }
    }
}

