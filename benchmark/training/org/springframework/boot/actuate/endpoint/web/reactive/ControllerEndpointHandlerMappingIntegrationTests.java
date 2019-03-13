/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.endpoint.web.reactive;


import HttpHeaders.LOCATION;
import HttpStatus.NOT_ACCEPTABLE;
import MediaType.APPLICATION_JSON;
import MediaType.TEXT_PLAIN;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.web.EndpointMapping;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointDiscoverer;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.adapter.WebHttpHandlerBuilder;


/**
 * Integration tests for {@link ControllerEndpointHandlerMapping}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ControllerEndpointHandlerMappingIntegrationTests {
    private final ReactiveWebApplicationContextRunner contextRunner = withUserConfiguration(ControllerEndpointHandlerMappingIntegrationTests.EndpointConfiguration.class, ControllerEndpointHandlerMappingIntegrationTests.ExampleWebFluxEndpoint.class);

    @Test
    public void get() {
        this.contextRunner.run(withWebTestClient(( webTestClient) -> webTestClient.get().uri("/actuator/example/one").accept(TEXT_PLAIN).exchange().expectStatus().isOk().expectHeader().contentTypeCompatibleWith(TEXT_PLAIN).expectBody(String.class).isEqualTo("One")));
    }

    @Test
    public void getWithUnacceptableContentType() {
        this.contextRunner.run(withWebTestClient(( webTestClient) -> webTestClient.get().uri("/actuator/example/one").accept(APPLICATION_JSON).exchange().expectStatus().isEqualTo(NOT_ACCEPTABLE)));
    }

    @Test
    public void post() {
        this.contextRunner.run(withWebTestClient(( webTestClient) -> webTestClient.post().uri("/actuator/example/two").syncBody(Collections.singletonMap("id", "test")).exchange().expectStatus().isCreated().expectHeader().valueEquals(LOCATION, "/example/test")));
    }

    @Configuration
    @ImportAutoConfiguration({ JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, WebFluxAutoConfiguration.class })
    static class EndpointConfiguration {
        @Bean
        public NettyReactiveWebServerFactory netty() {
            return new NettyReactiveWebServerFactory(0);
        }

        @Bean
        public HttpHandler httpHandler(ApplicationContext applicationContext) {
            return WebHttpHandlerBuilder.applicationContext(applicationContext).build();
        }

        @Bean
        public ControllerEndpointDiscoverer webEndpointDiscoverer(ApplicationContext applicationContext) {
            return new ControllerEndpointDiscoverer(applicationContext, null, Collections.emptyList());
        }

        @Bean
        public ControllerEndpointHandlerMapping webEndpointHandlerMapping(ControllerEndpointsSupplier endpointsSupplier) {
            return new ControllerEndpointHandlerMapping(new EndpointMapping("actuator"), endpointsSupplier.getEndpoints(), null);
        }
    }

    @RestControllerEndpoint(id = "example")
    public static class ExampleWebFluxEndpoint {
        @GetMapping(path = "one", produces = MediaType.TEXT_PLAIN_VALUE)
        public String one() {
            return "One";
        }

        @PostMapping("/two")
        public ResponseEntity<String> two(@RequestBody
        Map<String, Object> content) {
            return ResponseEntity.created(URI.create(("/example/" + (content.get("id"))))).build();
        }
    }
}

