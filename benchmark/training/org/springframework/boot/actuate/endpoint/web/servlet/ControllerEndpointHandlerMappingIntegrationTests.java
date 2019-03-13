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
package org.springframework.boot.actuate.endpoint.web.servlet;


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
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * Integration tests for {@link ControllerEndpointHandlerMapping}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ControllerEndpointHandlerMappingIntegrationTests {
    private final WebApplicationContextRunner contextRunner = withUserConfiguration(ControllerEndpointHandlerMappingIntegrationTests.EndpointConfiguration.class, ControllerEndpointHandlerMappingIntegrationTests.ExampleMvcEndpoint.class);

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
    @ImportAutoConfiguration({ JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class, WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class })
    static class EndpointConfiguration {
        @Bean
        public TomcatServletWebServerFactory tomcat() {
            return new TomcatServletWebServerFactory(0);
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
    public static class ExampleMvcEndpoint {
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

