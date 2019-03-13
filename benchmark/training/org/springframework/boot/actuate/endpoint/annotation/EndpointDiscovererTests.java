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
package org.springframework.boot.actuate.endpoint.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.boot.actuate.endpoint.invoke.convert.ConversionServiceParameterValueMapper;
import org.springframework.boot.actuate.endpoint.invoker.cache.CachingOperationInvoker;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.util.ReflectionUtils;


/**
 * Tests for {@link EndpointDiscoverer}.
 *
 * @author Andy Wilkinson
 * @author Stephane Nicoll
 * @author Phillip Webb
 */
public class EndpointDiscovererTests {
    @Test
    public void createWhenApplicationContextIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new org.springframework.boot.actuate.endpoint.annotation.TestEndpointDiscoverer(null, mock(.class), Collections.emptyList(), Collections.emptyList())).withMessageContaining("ApplicationContext must not be null");
    }

    @Test
    public void createWhenParameterValueMapperIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new org.springframework.boot.actuate.endpoint.annotation.TestEndpointDiscoverer(mock(.class), null, Collections.emptyList(), Collections.emptyList())).withMessageContaining("ParameterValueMapper must not be null");
    }

    @Test
    public void createWhenInvokerAdvisorsIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new org.springframework.boot.actuate.endpoint.annotation.TestEndpointDiscoverer(mock(.class), mock(.class), null, Collections.emptyList())).withMessageContaining("InvokerAdvisors must not be null");
    }

    @Test
    public void createWhenFiltersIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new org.springframework.boot.actuate.endpoint.annotation.TestEndpointDiscoverer(mock(.class), mock(.class), Collections.emptyList(), null)).withMessageContaining("Filters must not be null");
    }

    @Test
    public void getEndpointsWhenNoEndpointBeansShouldReturnEmptyCollection() {
        load(EndpointDiscovererTests.EmptyConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpointDiscoverer discoverer = new EndpointDiscovererTests.TestEndpointDiscoverer(context);
            Collection<EndpointDiscovererTests.TestExposableEndpoint> endpoints = getEndpoints();
            assertThat(endpoints).isEmpty();
        });
    }

    @Test
    public void getEndpointsWhenHasEndpointShouldReturnEndpoint() {
        load(EndpointDiscovererTests.TestEndpointConfiguration.class, this::hasTestEndpoint);
    }

    @Test
    public void getEndpointsWhenHasEndpointInParentContextShouldReturnEndpoint() {
        AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(EndpointDiscovererTests.TestEndpointConfiguration.class);
        loadWithParent(parent, EndpointDiscovererTests.EmptyConfiguration.class, this::hasTestEndpoint);
    }

    @Test
    public void getEndpointsWhenHasSubclassedEndpointShouldReturnEndpoint() {
        load(EndpointDiscovererTests.TestEndpointSubclassConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpointDiscoverer discoverer = new EndpointDiscovererTests.TestEndpointDiscoverer(context);
            Map<EndpointId, EndpointDiscovererTests.TestExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<Method, EndpointDiscovererTests.TestOperation> operations = mapOperations(endpoints.get(EndpointId.of("test")));
            assertThat(operations).hasSize(5);
            assertThat(operations).containsKeys(testEndpointMethods());
            assertThat(operations).containsKeys(ReflectionUtils.findMethod(EndpointDiscovererTests.TestEndpointSubclass.class, "updateWithMoreArguments", String.class, String.class, String.class));
        });
    }

    @Test
    public void getEndpointsWhenTwoEndpointsHaveTheSameIdShouldThrowException() {
        load(EndpointDiscovererTests.ClashingEndpointConfiguration.class, ( context) -> assertThatIllegalStateException().isThrownBy(new EndpointDiscovererTests.TestEndpointDiscoverer(context)::getEndpoints).withMessageContaining("Found two endpoints with the id 'test': "));
    }

    @Test
    public void getEndpointsWhenEndpointsArePrefixedWithScopedTargetShouldRegisterOnlyOneEndpoint() {
        load(EndpointDiscovererTests.ScopedTargetEndpointConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpoint expectedEndpoint = context.getBean(EndpointDiscovererTests.ScopedTargetEndpointConfiguration.class).testEndpoint();
            Collection<EndpointDiscovererTests.TestExposableEndpoint> endpoints = getEndpoints();
            assertThat(endpoints).flatExtracting(EndpointDiscovererTests.TestExposableEndpoint::getEndpointBean).containsOnly(expectedEndpoint);
        });
    }

    @Test
    public void getEndpointsWhenTtlSetToZeroShouldNotCacheInvokeCalls() {
        load(EndpointDiscovererTests.TestEndpointConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpointDiscoverer discoverer = new EndpointDiscovererTests.TestEndpointDiscoverer(context, ( endpointId) -> 0L);
            Map<EndpointId, EndpointDiscovererTests.TestExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<Method, EndpointDiscovererTests.TestOperation> operations = mapOperations(endpoints.get(EndpointId.of("test")));
            operations.values().forEach(( operation) -> assertThat(operation.getInvoker()).isNotInstanceOf(CachingOperationInvoker.class));
        });
    }

    @Test
    public void getEndpointsWhenTtlSetByIdAndIdDoesNotMatchShouldNotCacheInvokeCalls() {
        load(EndpointDiscovererTests.TestEndpointConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpointDiscoverer discoverer = new EndpointDiscovererTests.TestEndpointDiscoverer(context, ( endpointId) -> endpointId.equals("foo") ? 500L : 0L);
            Map<EndpointId, EndpointDiscovererTests.TestExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<Method, EndpointDiscovererTests.TestOperation> operations = mapOperations(endpoints.get(EndpointId.of("test")));
            operations.values().forEach(( operation) -> assertThat(operation.getInvoker()).isNotInstanceOf(CachingOperationInvoker.class));
        });
    }

    @Test
    public void getEndpointsWhenTtlSetByIdAndIdMatchesShouldCacheInvokeCalls() {
        load(EndpointDiscovererTests.TestEndpointConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpointDiscoverer discoverer = new EndpointDiscovererTests.TestEndpointDiscoverer(context, ( endpointId) -> endpointId.equals(EndpointId.of("test")) ? 500L : 0L);
            Map<EndpointId, EndpointDiscovererTests.TestExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
            Map<Method, EndpointDiscovererTests.TestOperation> operations = mapOperations(endpoints.get(EndpointId.of("test")));
            EndpointDiscovererTests.TestOperation getAll = operations.get(findTestEndpointMethod("getAll"));
            EndpointDiscovererTests.TestOperation getOne = operations.get(findTestEndpointMethod("getOne", String.class));
            EndpointDiscovererTests.TestOperation update = operations.get(ReflectionUtils.findMethod(EndpointDiscovererTests.TestEndpoint.class, "update", String.class, String.class));
            assertThat(getTimeToLive()).isEqualTo(500);
            assertThat(getOne.getInvoker()).isNotInstanceOf(CachingOperationInvoker.class);
            assertThat(update.getInvoker()).isNotInstanceOf(CachingOperationInvoker.class);
        });
    }

    @Test
    public void getEndpointsWhenHasSpecializedFiltersInNonSpecializedDiscovererShouldFilterEndpoints() {
        load(EndpointDiscovererTests.SpecializedEndpointsConfiguration.class, ( context) -> {
            EndpointDiscovererTests.TestEndpointDiscoverer discoverer = new EndpointDiscovererTests.TestEndpointDiscoverer(context);
            Map<EndpointId, EndpointDiscovererTests.TestExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
        });
    }

    @Test
    public void getEndpointsWhenHasSpecializedFiltersInSpecializedDiscovererShouldNotFilterEndpoints() {
        load(EndpointDiscovererTests.SpecializedEndpointsConfiguration.class, ( context) -> {
            EndpointDiscovererTests.SpecializedEndpointDiscoverer discoverer = new EndpointDiscovererTests.SpecializedEndpointDiscoverer(context);
            Map<EndpointId, EndpointDiscovererTests.SpecializedExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"), EndpointId.of("specialized"));
        });
    }

    @Test
    public void getEndpointsShouldApplyExtensions() {
        load(EndpointDiscovererTests.SpecializedEndpointsConfiguration.class, ( context) -> {
            EndpointDiscovererTests.SpecializedEndpointDiscoverer discoverer = new EndpointDiscovererTests.SpecializedEndpointDiscoverer(context);
            Map<EndpointId, EndpointDiscovererTests.SpecializedExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            Map<Method, EndpointDiscovererTests.SpecializedOperation> operations = mapOperations(endpoints.get(EndpointId.of("specialized")));
            assertThat(operations).containsKeys(ReflectionUtils.findMethod(EndpointDiscovererTests.SpecializedExtension.class, "getSpecial"));
        });
    }

    @Test
    public void getEndpointShouldFindParentExtension() {
        load(EndpointDiscovererTests.SubSpecializedEndpointsConfiguration.class, ( context) -> {
            EndpointDiscovererTests.SpecializedEndpointDiscoverer discoverer = new EndpointDiscovererTests.SpecializedEndpointDiscoverer(context);
            Map<EndpointId, EndpointDiscovererTests.SpecializedExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            Map<Method, EndpointDiscovererTests.SpecializedOperation> operations = mapOperations(endpoints.get(EndpointId.of("specialized")));
            assertThat(operations).containsKeys(ReflectionUtils.findMethod(EndpointDiscovererTests.SpecializedTestEndpoint.class, "getAll"));
            assertThat(operations).containsKeys(ReflectionUtils.findMethod(EndpointDiscovererTests.SubSpecializedTestEndpoint.class, "getSpecialOne", String.class));
            assertThat(operations).containsKeys(ReflectionUtils.findMethod(EndpointDiscovererTests.SpecializedExtension.class, "getSpecial"));
            assertThat(operations).hasSize(3);
        });
    }

    @Test
    public void getEndpointsShouldApplyFilters() {
        load(EndpointDiscovererTests.SpecializedEndpointsConfiguration.class, ( context) -> {
            EndpointFilter<EndpointDiscovererTests.SpecializedExposableEndpoint> filter = ( endpoint) -> {
                EndpointId id = endpoint.getEndpointId();
                return !(id.equals(EndpointId.of("specialized")));
            };
            EndpointDiscovererTests.SpecializedEndpointDiscoverer discoverer = new EndpointDiscovererTests.SpecializedEndpointDiscoverer(context, Collections.singleton(filter));
            Map<EndpointId, EndpointDiscovererTests.SpecializedExposableEndpoint> endpoints = mapEndpoints(discoverer.getEndpoints());
            assertThat(endpoints).containsOnlyKeys(EndpointId.of("test"));
        });
    }

    @Configuration
    static class EmptyConfiguration {}

    @Configuration
    static class TestEndpointConfiguration {
        @Bean
        public EndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new EndpointDiscovererTests.TestEndpoint();
        }
    }

    @Configuration
    static class TestEndpointSubclassConfiguration {
        @Bean
        public EndpointDiscovererTests.TestEndpointSubclass testEndpointSubclass() {
            return new EndpointDiscovererTests.TestEndpointSubclass();
        }
    }

    @Configuration
    static class ClashingEndpointConfiguration {
        @Bean
        public EndpointDiscovererTests.TestEndpoint testEndpointTwo() {
            return new EndpointDiscovererTests.TestEndpoint();
        }

        @Bean
        public EndpointDiscovererTests.TestEndpoint testEndpointOne() {
            return new EndpointDiscovererTests.TestEndpoint();
        }
    }

    @Configuration
    static class ScopedTargetEndpointConfiguration {
        @Bean
        public EndpointDiscovererTests.TestEndpoint testEndpoint() {
            return new EndpointDiscovererTests.TestEndpoint();
        }

        @Bean(name = "scopedTarget.testEndpoint")
        public EndpointDiscovererTests.TestEndpoint scopedTargetTestEndpoint() {
            return new EndpointDiscovererTests.TestEndpoint();
        }
    }

    @Import({ EndpointDiscovererTests.TestEndpoint.class, EndpointDiscovererTests.SpecializedTestEndpoint.class, EndpointDiscovererTests.SpecializedExtension.class })
    static class SpecializedEndpointsConfiguration {}

    @Import({ EndpointDiscovererTests.TestEndpoint.class, EndpointDiscovererTests.SubSpecializedTestEndpoint.class, EndpointDiscovererTests.SpecializedExtension.class })
    static class SubSpecializedEndpointsConfiguration {}

    @Endpoint(id = "test")
    static class TestEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }

        @ReadOperation
        public Object getOne(@Selector
        String id) {
            return null;
        }

        @WriteOperation
        public void update(String foo, String bar) {
        }

        @DeleteOperation
        public void deleteOne(@Selector
        String id) {
        }

        public void someOtherMethod() {
        }
    }

    static class TestEndpointSubclass extends EndpointDiscovererTests.TestEndpoint {
        @WriteOperation
        public void updateWithMoreArguments(String foo, String bar, String baz) {
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Endpoint
    @FilteredEndpoint(EndpointDiscovererTests.SpecializedEndpointFilter.class)
    public @interface SpecializedEndpoint {
        @AliasFor(annotation = Endpoint.class)
        String id();
    }

    @EndpointExtension(endpoint = EndpointDiscovererTests.SpecializedTestEndpoint.class, filter = EndpointDiscovererTests.SpecializedEndpointFilter.class)
    public static class SpecializedExtension {
        @ReadOperation
        public Object getSpecial() {
            return null;
        }
    }

    static class SpecializedEndpointFilter extends DiscovererEndpointFilter {
        SpecializedEndpointFilter() {
            super(EndpointDiscovererTests.SpecializedEndpointDiscoverer.class);
        }
    }

    @EndpointDiscovererTests.SpecializedEndpoint(id = "specialized")
    static class SpecializedTestEndpoint {
        @ReadOperation
        public Object getAll() {
            return null;
        }
    }

    static class SubSpecializedTestEndpoint extends EndpointDiscovererTests.SpecializedTestEndpoint {
        @ReadOperation
        public Object getSpecialOne(@Selector
        String id) {
            return null;
        }
    }

    static class TestEndpointDiscoverer extends EndpointDiscoverer<EndpointDiscovererTests.TestExposableEndpoint, EndpointDiscovererTests.TestOperation> {
        TestEndpointDiscoverer(ApplicationContext applicationContext) {
            this(applicationContext, ( id) -> null);
        }

        TestEndpointDiscoverer(ApplicationContext applicationContext, Function<EndpointId, Long> timeToLive) {
            this(applicationContext, timeToLive, Collections.emptyList());
        }

        TestEndpointDiscoverer(ApplicationContext applicationContext, Function<EndpointId, Long> timeToLive, Collection<EndpointFilter<EndpointDiscovererTests.TestExposableEndpoint>> filters) {
            this(applicationContext, new ConversionServiceParameterValueMapper(), Collections.singleton(new org.springframework.boot.actuate.endpoint.invoker.cache.CachingOperationInvokerAdvisor(timeToLive)), filters);
        }

        TestEndpointDiscoverer(ApplicationContext applicationContext, ParameterValueMapper parameterValueMapper, Collection<OperationInvokerAdvisor> invokerAdvisors, Collection<EndpointFilter<EndpointDiscovererTests.TestExposableEndpoint>> filters) {
            super(applicationContext, parameterValueMapper, invokerAdvisors, filters);
        }

        @Override
        protected EndpointDiscovererTests.TestExposableEndpoint createEndpoint(Object endpointBean, EndpointId id, boolean enabledByDefault, Collection<EndpointDiscovererTests.TestOperation> operations) {
            return new EndpointDiscovererTests.TestExposableEndpoint(this, endpointBean, id, enabledByDefault, operations);
        }

        @Override
        protected EndpointDiscovererTests.TestOperation createOperation(EndpointId endpointId, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
            return new EndpointDiscovererTests.TestOperation(operationMethod, invoker);
        }

        @Override
        protected OperationKey createOperationKey(EndpointDiscovererTests.TestOperation operation) {
            return new OperationKey(getOperationMethod(), () -> "TestOperation " + (operation.getOperationMethod()));
        }
    }

    static class SpecializedEndpointDiscoverer extends EndpointDiscoverer<EndpointDiscovererTests.SpecializedExposableEndpoint, EndpointDiscovererTests.SpecializedOperation> {
        SpecializedEndpointDiscoverer(ApplicationContext applicationContext) {
            this(applicationContext, Collections.emptyList());
        }

        SpecializedEndpointDiscoverer(ApplicationContext applicationContext, Collection<EndpointFilter<EndpointDiscovererTests.SpecializedExposableEndpoint>> filters) {
            super(applicationContext, new ConversionServiceParameterValueMapper(), Collections.emptyList(), filters);
        }

        @Override
        protected EndpointDiscovererTests.SpecializedExposableEndpoint createEndpoint(Object endpointBean, EndpointId id, boolean enabledByDefault, Collection<EndpointDiscovererTests.SpecializedOperation> operations) {
            return new EndpointDiscovererTests.SpecializedExposableEndpoint(this, endpointBean, id, enabledByDefault, operations);
        }

        @Override
        protected EndpointDiscovererTests.SpecializedOperation createOperation(EndpointId endpointId, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
            return new EndpointDiscovererTests.SpecializedOperation(operationMethod, invoker);
        }

        @Override
        protected OperationKey createOperationKey(EndpointDiscovererTests.SpecializedOperation operation) {
            return new OperationKey(getOperationMethod(), () -> "TestOperation " + (operation.getOperationMethod()));
        }
    }

    static class TestExposableEndpoint extends AbstractDiscoveredEndpoint<EndpointDiscovererTests.TestOperation> {
        TestExposableEndpoint(EndpointDiscoverer<?, ?> discoverer, Object endpointBean, EndpointId id, boolean enabledByDefault, Collection<? extends EndpointDiscovererTests.TestOperation> operations) {
            super(discoverer, endpointBean, id, enabledByDefault, operations);
        }
    }

    static class SpecializedExposableEndpoint extends AbstractDiscoveredEndpoint<EndpointDiscovererTests.SpecializedOperation> {
        SpecializedExposableEndpoint(EndpointDiscoverer<?, ?> discoverer, Object endpointBean, EndpointId id, boolean enabledByDefault, Collection<? extends EndpointDiscovererTests.SpecializedOperation> operations) {
            super(discoverer, endpointBean, id, enabledByDefault, operations);
        }
    }

    static class TestOperation extends AbstractDiscoveredOperation {
        private final OperationInvoker invoker;

        TestOperation(DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
            super(operationMethod, invoker);
            this.invoker = invoker;
        }

        public OperationInvoker getInvoker() {
            return this.invoker;
        }
    }

    static class SpecializedOperation extends EndpointDiscovererTests.TestOperation {
        SpecializedOperation(DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
            super(operationMethod, invoker);
        }
    }
}

