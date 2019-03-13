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
package org.springframework.boot.actuate.autoconfigure.integrationtest;


import org.junit.Test;
import org.springframework.boot.actuate.autoconfigure.beans.BeansEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.http.codec.CodecsAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.reactive.server.WebTestClient;


/**
 * Integration tests for the WebFlux actuator endpoints.
 *
 * @author Andy Wilkinson
 */
public class WebFluxEndpointIntegrationTests {
    @Test
    public void linksAreProvidedToAllEndpointTypes() throws Exception {
        new ReactiveWebApplicationContextRunner().withConfiguration(AutoConfigurations.of(JacksonAutoConfiguration.class, CodecsAutoConfiguration.class, WebFluxAutoConfiguration.class, HttpHandlerAutoConfiguration.class, EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class, ManagementContextAutoConfiguration.class, ReactiveManagementContextAutoConfiguration.class, BeansEndpointAutoConfiguration.class)).withUserConfiguration(WebFluxEndpointIntegrationTests.EndpointsConfiguration.class).withPropertyValues("management.endpoints.web.exposure.include:*").run(( context) -> {
            WebTestClient client = createWebTestClient(context);
            client.get().uri("/actuator").exchange().expectStatus().isOk().expectBody().jsonPath("_links.beans").isNotEmpty().jsonPath("_links.restcontroller").isNotEmpty().jsonPath("_links.controller").isNotEmpty();
        });
    }

    @ControllerEndpoint(id = "controller")
    static class TestControllerEndpoint {}

    @RestControllerEndpoint(id = "restcontroller")
    static class TestRestControllerEndpoint {}

    @Configuration
    static class EndpointsConfiguration {
        @Bean
        WebFluxEndpointIntegrationTests.TestControllerEndpoint testControllerEndpoint() {
            return new WebFluxEndpointIntegrationTests.TestControllerEndpoint();
        }

        @Bean
        WebFluxEndpointIntegrationTests.TestRestControllerEndpoint testRestControllerEndpoint() {
            return new WebFluxEndpointIntegrationTests.TestRestControllerEndpoint();
        }
    }
}

