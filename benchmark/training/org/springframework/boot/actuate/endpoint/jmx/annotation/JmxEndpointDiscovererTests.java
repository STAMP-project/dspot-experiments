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
package org.springframework.boot.actuate.endpoint.jmx.annotation;


import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.invoker.cache.CachingOperationInvoker;
import org.springframework.boot.actuate.endpoint.jmx.ExposableJmxEndpoint;
import org.springframework.boot.actuate.endpoint.jmx.JmxOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;


/**
 * Tests for {@link JmxEndpointDiscoverer}.
 *
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class JmxEndpointDiscovererTests {
    @Test
    public void getEndpointsWhenNoEndpointBeansShouldReturnEmptyCollection() {
        load(JmxEndpointDiscovererTests.EmptyConfiguration.class, ( discoverer) -> assertThat(discoverer.getEndpoints()).isEmpty());
    }

    @Test
    public void getEndpointsShouldDiscoverStandardEndpoints() {
        load(JmxEndpointDiscovererTests.TestEndpoint.class, ( discoverer) -> {
            Map<EndpointId, ExposableJmxEndpoint> endpoints = discover(discoverer);
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<String, JmxOperation> operationByName = mapOperations(endpoints.get(EndpointId.of("test")).getOperations());
            assertThat(operationByName).containsOnlyKeys("getAll", "getSomething", "update", "deleteSomething");
            JmxOperation getAll = operationByName.get("getAll");
            assertThat(getAll.getDescription()).isEqualTo("Invoke getAll for endpoint test");
            assertThat(getAll.getOutputType()).isEqualTo(Object.class);
            assertThat(getAll.getParameters()).isEmpty();
            JmxOperation getSomething = operationByName.get("getSomething");
            assertThat(getSomething.getDescription()).isEqualTo("Invoke getSomething for endpoint test");
            assertThat(getSomething.getOutputType()).isEqualTo(String.class);
            assertThat(getSomething.getParameters()).hasSize(1);
            hasDefaultParameter(getSomething, 0, String.class);
            JmxOperation update = operationByName.get("update");
            assertThat(update.getDescription()).isEqualTo("Invoke update for endpoint test");
            assertThat(update.getOutputType()).isEqualTo(Void.TYPE);
            assertThat(update.getParameters()).hasSize(2);
            hasDefaultParameter(update, 0, String.class);
            hasDefaultParameter(update, 1, String.class);
            JmxOperation deleteSomething = operationByName.get("deleteSomething");
            assertThat(deleteSomething.getDescription()).isEqualTo("Invoke deleteSomething for endpoint test");
            assertThat(deleteSomething.getOutputType()).isEqualTo(Void.TYPE);
            assertThat(deleteSomething.getParameters()).hasSize(1);
            hasDefaultParameter(deleteSomething, 0, String.class);
        });
    }

    @Test
    public void getEndpointsWhenHasFilteredEndpointShouldOnlyDiscoverJmxEndpoints() {
        load(JmxEndpointDiscovererTests.MultipleEndpointsConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableJmxEndpoint> endpoints = discover(discoverer);
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"), EndpointId.of("jmx"));
        });
    }

    @Test
    public void getEndpointsWhenJmxExtensionIsMissingEndpointShouldThrowException() {
        load(JmxEndpointDiscovererTests.TestJmxEndpointExtension.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Invalid extension 'jmxEndpointDiscovererTests.TestJmxEndpointExtension': no endpoint found with id 'test'"));
    }

    @Test
    public void getEndpointsWhenHasJmxExtensionShouldOverrideStandardEndpoint() {
        load(JmxEndpointDiscovererTests.OverriddenOperationJmxEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableJmxEndpoint> endpoints = discover(discoverer);
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            assertJmxTestEndpoint(endpoints.get(EndpointId.of("test")));
        });
    }

    @Test
    public void getEndpointsWhenHasJmxExtensionWithNewOperationAddsExtraOperation() {
        load(JmxEndpointDiscovererTests.AdditionalOperationJmxEndpointConfiguration.class, ( discoverer) -> {
            Map<EndpointId, ExposableJmxEndpoint> endpoints = discover(discoverer);
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<String, JmxOperation> operationByName = mapOperations(endpoints.get(EndpointId.of("test")).getOperations());
            assertThat(operationByName).containsOnlyKeys("getAll", "getSomething", "update", "deleteSomething", "getAnother");
            JmxOperation getAnother = operationByName.get("getAnother");
            assertThat(getAnother.getDescription()).isEqualTo("Get another thing");
            assertThat(getAnother.getOutputType()).isEqualTo(Object.class);
            assertThat(getAnother.getParameters()).isEmpty();
        });
    }

    @Test
    public void getEndpointsWhenHasCacheWithTtlShouldCacheReadOperationWithTtlValue() {
        load(JmxEndpointDiscovererTests.TestEndpoint.class, ( id) -> 500L, ( discoverer) -> {
            Map<EndpointId, ExposableJmxEndpoint> endpoints = discover(discoverer);
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<String, JmxOperation> operationByName = mapOperations(endpoints.get(EndpointId.of("test")).getOperations());
            assertThat(operationByName).containsOnlyKeys("getAll", "getSomething", "update", "deleteSomething");
            JmxOperation getAll = operationByName.get("getAll");
            assertThat(getInvoker(getAll)).isInstanceOf(CachingOperationInvoker.class);
            assertThat(getTimeToLive()).isEqualTo(500);
        });
    }

    @Test
    public void getEndpointsShouldCacheReadOperations() {
        load(JmxEndpointDiscovererTests.AdditionalOperationJmxEndpointConfiguration.class, ( id) -> 500L, ( discoverer) -> {
            Map<EndpointId, ExposableJmxEndpoint> endpoints = discover(discoverer);
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<String, JmxOperation> operationByName = mapOperations(endpoints.get(EndpointId.of("test")).getOperations());
            assertThat(operationByName).containsOnlyKeys("getAll", "getSomething", "update", "deleteSomething", "getAnother");
            JmxOperation getAll = operationByName.get("getAll");
            assertThat(getInvoker(getAll)).isInstanceOf(CachingOperationInvoker.class);
            assertThat(getTimeToLive()).isEqualTo(500);
            JmxOperation getAnother = operationByName.get("getAnother");
            assertThat(getInvoker(getAnother)).isInstanceOf(CachingOperationInvoker.class);
            assertThat(getTimeToLive()).isEqualTo(500);
        });
    }

    @Test
    public void getEndpointsWhenTwoExtensionsHaveTheSameEndpointTypeShouldThrowException() {
        load(JmxEndpointDiscovererTests.ClashingJmxEndpointConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Found multiple extensions for the endpoint bean testEndpoint (testExtensionOne, testExtensionTwo)"));
    }

    @Test
    public void getEndpointsWhenTwoStandardEndpointsHaveTheSameIdShouldThrowException() {
        load(JmxEndpointDiscovererTests.ClashingStandardEndpointConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Found two endpoints with the id 'test': "));
    }

    @Test
    public void getEndpointsWhenWhenEndpointHasTwoOperationsWithTheSameNameShouldThrowException() {
        load(JmxEndpointDiscovererTests.ClashingOperationsEndpoint.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Unable to map duplicate endpoint operations: [MBean call 'getAll'] to jmxEndpointDiscovererTests.ClashingOperationsEndpoint"));
    }

    @Test
    public void getEndpointsWhenWhenExtensionHasTwoOperationsWithTheSameNameShouldThrowException() {
        load(JmxEndpointDiscovererTests.AdditionalClashingOperationsConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Unable to map duplicate endpoint operations: [MBean call 'getAll'] to testEndpoint (clashingOperationsJmxEndpointExtension)"));
    }

    @Test
    public void getEndpointsWhenExtensionIsNotCompatibleWithTheEndpointTypeShouldThrowException() {
        load(JmxEndpointDiscovererTests.InvalidJmxExtensionConfiguration.class, ( discoverer) -> assertThatIllegalStateException().isThrownBy(discoverer::getEndpoints).withMessageContaining("Endpoint bean 'nonJmxEndpoint' cannot support the extension bean 'nonJmxJmxEndpointExtension'"));
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    static class MultipleEndpointsConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.TestJmxEndpoint testJmxEndpoint() {
            return new JmxEndpointDiscovererTests.TestJmxEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.NonJmxEndpoint nonJmxEndpoint() {
            return new JmxEndpointDiscovererTests.NonJmxEndpoint();
        }
    }

    @Configuration
    static class OverriddenOperationJmxEndpointConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.TestJmxEndpointExtension testJmxEndpointExtension() {
            return new JmxEndpointDiscovererTests.TestJmxEndpointExtension();
        }
    }

    @Configuration
    static class AdditionalOperationJmxEndpointConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.AdditionalOperationJmxEndpointExtension additionalOperationJmxEndpointExtension() {
            return new JmxEndpointDiscovererTests.AdditionalOperationJmxEndpointExtension();
        }
    }

    @Configuration
    static class AdditionalClashingOperationsConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.ClashingOperationsJmxEndpointExtension clashingOperationsJmxEndpointExtension() {
            return new JmxEndpointDiscovererTests.ClashingOperationsJmxEndpointExtension();
        }
    }

    @Configuration
    static class ClashingJmxEndpointConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.TestJmxEndpointExtension testExtensionOne() {
            return new JmxEndpointDiscovererTests.TestJmxEndpointExtension();
        }

        @Bean
        public JmxEndpointDiscovererTests.TestJmxEndpointExtension testExtensionTwo() {
            return new JmxEndpointDiscovererTests.TestJmxEndpointExtension();
        }
    }

    @Configuration
    static class ClashingStandardEndpointConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpointTwo() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.TestEndpoint testEndpointOne() {
            return new JmxEndpointDiscovererTests.TestEndpoint();
        }
    }

    @Configuration
    static class InvalidJmxExtensionConfiguration {
        @Bean
        public JmxEndpointDiscovererTests.NonJmxEndpoint nonJmxEndpoint() {
            return new JmxEndpointDiscovererTests.NonJmxEndpoint();
        }

        @Bean
        public JmxEndpointDiscovererTests.NonJmxJmxEndpointExtension nonJmxJmxEndpointExtension() {
            return new JmxEndpointDiscovererTests.NonJmxJmxEndpointExtension();
        }
    }

    @Endpoint(id = "test")
    private static class TestEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public String getSomething(TimeUnit timeUnit) {
            return null;
        }

        @WriteOperation
        public void update(String foo, String bar) {
        }

        @DeleteOperation
        public void deleteSomething(TimeUnit timeUnit) {
        }
    }

    @JmxEndpoint(id = "jmx")
    private static class TestJmxEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    @EndpointJmxExtension(endpoint = JmxEndpointDiscovererTests.TestEndpoint.class)
    private static class TestJmxEndpointExtension {
        @ManagedOperation(description = "Get all the things")
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        @ManagedOperation(description = "Get something based on a timeUnit")
        @ManagedOperationParameters({ @ManagedOperationParameter(name = "unitMs", description = "Number of milliseconds") })
        public String getSomething(Long timeUnit) {
            return null;
        }

        @WriteOperation
        @ManagedOperation(description = "Update something based on bar")
        @ManagedOperationParameters({ @ManagedOperationParameter(name = "foo", description = "Foo identifier"), @ManagedOperationParameter(name = "bar", description = "Bar value") })
        public void update(String foo, String bar) {
        }

        @DeleteOperation
        @ManagedOperation(description = "Delete something based on a timeUnit")
        @ManagedOperationParameters({ @ManagedOperationParameter(name = "unitMs", description = "Number of milliseconds") })
        public void deleteSomething(Long timeUnit) {
        }
    }

    @EndpointJmxExtension(endpoint = JmxEndpointDiscovererTests.TestEndpoint.class)
    private static class AdditionalOperationJmxEndpointExtension {
        @ManagedOperation(description = "Get another thing")
        @ReadOperation
        public Object getAnother() {
            return null;
        }
    }

    @Endpoint(id = "test")
    static class ClashingOperationsEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public Object getAll(String param) {
            return null;
        }
    }

    @EndpointJmxExtension(endpoint = JmxEndpointDiscovererTests.TestEndpoint.class)
    static class ClashingOperationsJmxEndpointExtension {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public Object getAll(String param) {
            return null;
        }
    }

    @WebEndpoint(id = "nonjmx")
    private static class NonJmxEndpoint {
        @ReadOperation
        public Object getData() {
            return null;
        }
    }

    @EndpointJmxExtension(endpoint = JmxEndpointDiscovererTests.NonJmxEndpoint.class)
    private static class NonJmxJmxEndpointExtension {
        @ReadOperation
        public Object getSomething() {
            return null;
        }
    }
}

