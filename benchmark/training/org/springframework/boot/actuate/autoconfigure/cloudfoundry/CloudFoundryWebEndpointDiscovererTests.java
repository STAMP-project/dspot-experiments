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
package org.springframework.boot.actuate.autoconfigure.cloudfoundry;


import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.InvocationContext;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.web.ExposableWebEndpoint;
import org.springframework.boot.actuate.endpoint.web.WebOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.EndpointWebExtension;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Tests for {@link CloudFoundryWebEndpointDiscoverer}.
 *
 * @author Madhura Bhave
 */
public class CloudFoundryWebEndpointDiscovererTests {
    @Test
    public void getEndpointsShouldAddCloudFoundryHealthExtension() {
        load(CloudFoundryWebEndpointDiscovererTests.TestConfiguration.class, ( discoverer) -> {
            Collection<ExposableWebEndpoint> endpoints = discoverer.getEndpoints();
            assertThat(endpoints.size()).isEqualTo(2);
            for (ExposableWebEndpoint endpoint : endpoints) {
                if (endpoint.getEndpointId().equals(EndpointId.of("health"))) {
                    WebOperation operation = findMainReadOperation(endpoint);
                    assertThat(operation.invoke(new InvocationContext(Mockito.mock(SecurityContext.class), Collections.emptyMap()))).isEqualTo("cf");
                }
            }
        });
    }

    @Configuration
    static class TestConfiguration {
        @Bean
        public CloudFoundryWebEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new CloudFoundryWebEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public CloudFoundryWebEndpointDiscovererTests.TestEndpointWebExtension testEndpointWebExtension() {
            return new CloudFoundryWebEndpointDiscovererTests.TestEndpointWebExtension();
        }

        @Bean
        public HealthEndpoint healthEndpoint() {
            return new HealthEndpoint(Mockito.mock(HealthIndicator.class));
        }

        @Bean
        public CloudFoundryWebEndpointDiscovererTests.HealthEndpointWebExtension healthEndpointWebExtension() {
            return new CloudFoundryWebEndpointDiscovererTests.HealthEndpointWebExtension();
        }

        @Bean
        public CloudFoundryWebEndpointDiscovererTests.TestHealthEndpointCloudFoundryExtension testHealthEndpointCloudFoundryExtension() {
            return new CloudFoundryWebEndpointDiscovererTests.TestHealthEndpointCloudFoundryExtension();
        }
    }

    @Endpoint(id = "test")
    static class TestEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = CloudFoundryWebEndpointDiscovererTests.TestEndpoint.class)
    static class TestEndpointWebExtension {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    @EndpointWebExtension(endpoint = HealthEndpoint.class)
    static class HealthEndpointWebExtension {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    @HealthEndpointCloudFoundryExtension
    static class TestHealthEndpointCloudFoundryExtension {
        @ReadOperation
        public Object getAll() {
            return "cf";
        }
    }
}

