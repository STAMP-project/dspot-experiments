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


import java.util.Collection;
import org.junit.Test;
import org.springframework.boot.actuate.endpoint.EndpointFilter;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.Operation;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.context.ApplicationContext;


/**
 * Tests for {@link DiscovererEndpointFilter}.
 *
 * @author Phillip Webb
 */
public class DiscovererEndpointFilterTests {
    @Test
    public void createWhenDiscovererIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new org.springframework.boot.actuate.endpoint.annotation.TestDiscovererEndpointFilter(null)).withMessageContaining("Discoverer must not be null");
    }

    @Test
    public void matchWhenDiscoveredByDiscovererShouldReturnTrue() {
        DiscovererEndpointFilter filter = new DiscovererEndpointFilterTests.TestDiscovererEndpointFilter(DiscovererEndpointFilterTests.TestDiscovererA.class);
        DiscoveredEndpoint<?> endpoint = mockDiscoveredEndpoint(DiscovererEndpointFilterTests.TestDiscovererA.class);
        assertThat(filter.match(endpoint)).isTrue();
    }

    @Test
    public void matchWhenNotDiscoveredByDiscovererShouldReturnFalse() {
        DiscovererEndpointFilter filter = new DiscovererEndpointFilterTests.TestDiscovererEndpointFilter(DiscovererEndpointFilterTests.TestDiscovererA.class);
        DiscoveredEndpoint<?> endpoint = mockDiscoveredEndpoint(DiscovererEndpointFilterTests.TestDiscovererB.class);
        assertThat(filter.match(endpoint)).isFalse();
    }

    static class TestDiscovererEndpointFilter extends DiscovererEndpointFilter {
        TestDiscovererEndpointFilter(Class<? extends EndpointDiscoverer<?, ?>> discoverer) {
            super(discoverer);
        }
    }

    abstract static class TestDiscovererA extends EndpointDiscoverer<ExposableEndpoint<Operation>, Operation> {
        TestDiscovererA(ApplicationContext applicationContext, ParameterValueMapper parameterValueMapper, Collection<OperationInvokerAdvisor> invokerAdvisors, Collection<EndpointFilter<ExposableEndpoint<Operation>>> filters) {
            super(applicationContext, parameterValueMapper, invokerAdvisors, filters);
        }
    }

    abstract static class TestDiscovererB extends EndpointDiscoverer<ExposableEndpoint<Operation>, Operation> {
        TestDiscovererB(ApplicationContext applicationContext, ParameterValueMapper parameterValueMapper, Collection<OperationInvokerAdvisor> invokerAdvisors, Collection<EndpointFilter<ExposableEndpoint<Operation>>> filters) {
            super(applicationContext, parameterValueMapper, invokerAdvisors, filters);
        }
    }
}

