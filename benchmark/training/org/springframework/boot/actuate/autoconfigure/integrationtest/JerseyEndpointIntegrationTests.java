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


import org.glassfish.jersey.server.ResourceConfig;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpoint;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Integration tests for the Jersey actuator endpoints.
 *
 * @author Andy Wilkinson
 * @author Madhura Bhave
 */
public class JerseyEndpointIntegrationTests {
    @Test
    public void linksAreProvidedToAllEndpointTypes() {
        testJerseyEndpoints(new Class[]{ JerseyEndpointIntegrationTests.EndpointsConfiguration.class, JerseyEndpointIntegrationTests.ResourceConfigConfiguration.class });
    }

    @Test
    public void actuatorEndpointsWhenUserProvidedResourceConfigBeanNotAvailable() {
        testJerseyEndpoints(new Class[]{ JerseyEndpointIntegrationTests.EndpointsConfiguration.class });
    }

    @ControllerEndpoint(id = "controller")
    static class TestControllerEndpoint {}

    @RestControllerEndpoint(id = "restcontroller")
    static class TestRestControllerEndpoint {}

    @Configuration
    static class EndpointsConfiguration {
        @Bean
        JerseyEndpointIntegrationTests.TestControllerEndpoint testControllerEndpoint() {
            return new JerseyEndpointIntegrationTests.TestControllerEndpoint();
        }

        @Bean
        JerseyEndpointIntegrationTests.TestRestControllerEndpoint testRestControllerEndpoint() {
            return new JerseyEndpointIntegrationTests.TestRestControllerEndpoint();
        }
    }

    @Configuration
    static class ResourceConfigConfiguration {
        @Bean
        ResourceConfig testResourceConfig() {
            return new ResourceConfig();
        }
    }
}

