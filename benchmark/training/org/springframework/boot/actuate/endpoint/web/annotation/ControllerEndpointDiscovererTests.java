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
package org.springframework.boot.actuate.endpoint.web.annotation;


import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.annotation.DiscoveredEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.validation.annotation.Validated;


/**
 * Tests for {@link ControllerEndpointDiscoverer}.
 *
 * @author Phillip Webb
 * @author Stephane Nicoll
 */
public class ControllerEndpointDiscovererTests {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Test
    public void getEndpointsWhenNoEndpointBeansShouldReturnEmptyCollection() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.EmptyConfiguration.class).run(assertDiscoverer(( discoverer) -> assertThat(discoverer.getEndpoints()).isEmpty()));
    }

    @Test
    public void getEndpointsShouldIncludeControllerEndpoints() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.TestControllerEndpoint.class).run(assertDiscoverer(( discoverer) -> {
            Collection<ExposableControllerEndpoint> endpoints = discoverer.getEndpoints();
            assertThat(endpoints).hasSize(1);
            ExposableControllerEndpoint endpoint = endpoints.iterator().next();
            assertThat(endpoint.getEndpointId()).isEqualTo(EndpointId.of("testcontroller"));
            assertThat(endpoint.getController()).isInstanceOf(ControllerEndpointDiscovererTests.TestControllerEndpoint.class);
            assertThat(endpoint).isInstanceOf(DiscoveredEndpoint.class);
        }));
    }

    @Test
    public void getEndpointsShouldDiscoverProxyControllerEndpoints() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.TestProxyControllerEndpoint.class).withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).run(assertDiscoverer(( discoverer) -> {
            Collection<ExposableControllerEndpoint> endpoints = discoverer.getEndpoints();
            assertThat(endpoints).hasSize(1);
            ExposableControllerEndpoint endpoint = endpoints.iterator().next();
            assertThat(endpoint.getEndpointId()).isEqualTo(EndpointId.of("testcontroller"));
            assertThat(endpoint.getController()).isInstanceOf(ControllerEndpointDiscovererTests.TestProxyControllerEndpoint.class);
            assertThat(endpoint).isInstanceOf(DiscoveredEndpoint.class);
        }));
    }

    @Test
    public void getEndpointsShouldIncludeRestControllerEndpoints() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.TestRestControllerEndpoint.class).run(assertDiscoverer(( discoverer) -> {
            Collection<ExposableControllerEndpoint> endpoints = discoverer.getEndpoints();
            assertThat(endpoints).hasSize(1);
            ExposableControllerEndpoint endpoint = endpoints.iterator().next();
            assertThat(endpoint.getEndpointId()).isEqualTo(EndpointId.of("testrestcontroller"));
            assertThat(endpoint.getController()).isInstanceOf(ControllerEndpointDiscovererTests.TestRestControllerEndpoint.class);
        }));
    }

    @Test
    public void getEndpointsShouldDiscoverProxyRestControllerEndpoints() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.TestProxyRestControllerEndpoint.class).withConfiguration(AutoConfigurations.of(ValidationAutoConfiguration.class)).run(assertDiscoverer(( discoverer) -> {
            Collection<ExposableControllerEndpoint> endpoints = discoverer.getEndpoints();
            assertThat(endpoints).hasSize(1);
            ExposableControllerEndpoint endpoint = endpoints.iterator().next();
            assertThat(endpoint.getEndpointId()).isEqualTo(EndpointId.of("testrestcontroller"));
            assertThat(endpoint.getController()).isInstanceOf(ControllerEndpointDiscovererTests.TestProxyRestControllerEndpoint.class);
            assertThat(endpoint).isInstanceOf(DiscoveredEndpoint.class);
        }));
    }

    @Test
    public void getEndpointsShouldNotDiscoverRegularEndpoints() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.WithRegularEndpointConfiguration.class).run(assertDiscoverer(( discoverer) -> {
            Collection<ExposableControllerEndpoint> endpoints = discoverer.getEndpoints();
            List<EndpointId> ids = endpoints.stream().map(ExposableEndpoint::getEndpointId).collect(Collectors.toList());
            assertThat(ids).containsOnly(EndpointId.of("testcontroller"), EndpointId.of("testrestcontroller"));
        }));
    }

    @Test
    public void getEndpointWhenEndpointHasOperationsShouldThrowException() {
        this.contextRunner.withUserConfiguration(ControllerEndpointDiscovererTests.TestControllerWithOperation.class).run(assertDiscoverer(( discoverer) -> assertThatExceptionOfType(IllegalStateException.class).isThrownBy(discoverer::getEndpoints).withMessageContaining("ControllerEndpoints must not declare operations")));
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    @Import({ ControllerEndpointDiscovererTests.TestEndpoint.class, ControllerEndpointDiscovererTests.TestControllerEndpoint.class, ControllerEndpointDiscovererTests.TestRestControllerEndpoint.class })
    static class WithRegularEndpointConfiguration {}

    @ControllerEndpoint(id = "testcontroller")
    static class TestControllerEndpoint {}

    @ControllerEndpoint(id = "testcontroller")
    @Validated
    static class TestProxyControllerEndpoint {}

    @RestControllerEndpoint(id = "testrestcontroller")
    static class TestRestControllerEndpoint {}

    @RestControllerEndpoint(id = "testrestcontroller")
    @Validated
    static class TestProxyRestControllerEndpoint {}

    @Endpoint(id = "test")
    static class TestEndpoint {}

    @ControllerEndpoint(id = "testcontroller")
    static class TestControllerWithOperation {
        @ReadOperation
        public String read() {
            return "error";
        }
    }
}

