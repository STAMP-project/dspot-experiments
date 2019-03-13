/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.reactive.function.server.support;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.AbstractHttpHandlerIntegrationTests;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


/**
 *
 *
 * @author Arjen Poutsma
 */
public class DispatcherHandlerIntegrationTests extends AbstractHttpHandlerIntegrationTests {
    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void nested() {
        ResponseEntity<String> result = this.restTemplate.getForEntity((("http://localhost:" + (this.port)) + "/foo/bar"), String.class);
        Assert.assertEquals(200, result.getStatusCodeValue());
    }

    @Configuration
    @EnableWebFlux
    static class TestConfiguration {
        @Bean
        public RouterFunction<ServerResponse> router(DispatcherHandlerIntegrationTests.Handler handler) {
            return route().path("/foo", () -> route().nest(accept(MediaType.APPLICATION_JSON), ( builder) -> builder.GET("/bar", handler::handle)).build()).build();
        }

        @Bean
        public DispatcherHandlerIntegrationTests.Handler handler() {
            return new DispatcherHandlerIntegrationTests.Handler();
        }
    }

    static class Handler {
        public Mono<ServerResponse> handle(ServerRequest request) {
            return ServerResponse.ok().build();
        }
    }
}

