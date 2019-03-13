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


import OperationType.DELETE;
import OperationType.READ;
import OperationType.WRITE;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.EndpointId;
import org.springframework.boot.actuate.endpoint.InvocationContext;
import org.springframework.boot.actuate.endpoint.OperationType;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvoker;
import org.springframework.boot.actuate.endpoint.invoke.OperationInvokerAdvisor;
import org.springframework.boot.actuate.endpoint.invoke.OperationParameters;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.boot.actuate.endpoint.invoke.reflect.OperationMethod;


/**
 * Tests for {@link DiscoveredOperationsFactory}.
 *
 * @author Phillip Webb
 */
public class DiscoveredOperationsFactoryTests {
    private DiscoveredOperationsFactoryTests.TestDiscoveredOperationsFactory factory;

    private ParameterValueMapper parameterValueMapper;

    private List<OperationInvokerAdvisor> invokerAdvisors;

    @Test
    public void createOperationsWhenHasReadMethodShouldCreateOperation() {
        Collection<DiscoveredOperationsFactoryTests.TestOperation> operations = this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleRead());
        assertThat(operations).hasSize(1);
        DiscoveredOperationsFactoryTests.TestOperation operation = getFirst(operations);
        assertThat(getType()).isEqualTo(READ);
    }

    @Test
    public void createOperationsWhenHasWriteMethodShouldCreateOperation() {
        Collection<DiscoveredOperationsFactoryTests.TestOperation> operations = this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleWrite());
        assertThat(operations).hasSize(1);
        DiscoveredOperationsFactoryTests.TestOperation operation = getFirst(operations);
        assertThat(getType()).isEqualTo(WRITE);
    }

    @Test
    public void createOperationsWhenHasDeleteMethodShouldCreateOperation() {
        Collection<DiscoveredOperationsFactoryTests.TestOperation> operations = this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleDelete());
        assertThat(operations).hasSize(1);
        DiscoveredOperationsFactoryTests.TestOperation operation = getFirst(operations);
        assertThat(getType()).isEqualTo(DELETE);
    }

    @Test
    public void createOperationsWhenMultipleShouldReturnMultiple() {
        Collection<DiscoveredOperationsFactoryTests.TestOperation> operations = this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleMultiple());
        assertThat(operations).hasSize(2);
        assertThat(operations.stream().map(DiscoveredOperationsFactoryTests.TestOperation::getType)).containsOnly(READ, WRITE);
    }

    @Test
    public void createOperationsShouldProvideOperationMethod() {
        DiscoveredOperationsFactoryTests.TestOperation operation = getFirst(this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleWithParams()));
        OperationMethod operationMethod = getOperationMethod();
        assertThat(operationMethod.getMethod().getName()).isEqualTo("read");
        assertThat(operationMethod.getParameters().hasParameters()).isTrue();
    }

    @Test
    public void createOperationsShouldProviderInvoker() {
        DiscoveredOperationsFactoryTests.TestOperation operation = getFirst(this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleWithParams()));
        Map<String, Object> params = Collections.singletonMap("name", 123);
        Object result = operation.invoke(new InvocationContext(Mockito.mock(SecurityContext.class), params));
        assertThat(result).isEqualTo("123");
    }

    @Test
    public void createOperationShouldApplyAdvisors() {
        DiscoveredOperationsFactoryTests.TestOperationInvokerAdvisor advisor = new DiscoveredOperationsFactoryTests.TestOperationInvokerAdvisor();
        this.invokerAdvisors.add(advisor);
        DiscoveredOperationsFactoryTests.TestOperation operation = getFirst(this.factory.createOperations(EndpointId.of("test"), new DiscoveredOperationsFactoryTests.ExampleRead()));
        operation.invoke(new InvocationContext(Mockito.mock(SecurityContext.class), Collections.emptyMap()));
        assertThat(advisor.getEndpointId()).isEqualTo(EndpointId.of("test"));
        assertThat(advisor.getOperationType()).isEqualTo(READ);
        assertThat(advisor.getParameters()).isEmpty();
    }

    static class ExampleRead {
        @ReadOperation
        public String read() {
            return "read";
        }
    }

    static class ExampleWrite {
        @WriteOperation
        public String write() {
            return "write";
        }
    }

    static class ExampleDelete {
        @DeleteOperation
        public String delete() {
            return "delete";
        }
    }

    static class ExampleMultiple {
        @ReadOperation
        public String read() {
            return "read";
        }

        @WriteOperation
        public String write() {
            return "write";
        }
    }

    static class ExampleWithParams {
        @ReadOperation
        public String read(String name) {
            return name;
        }
    }

    static class TestDiscoveredOperationsFactory extends DiscoveredOperationsFactory<DiscoveredOperationsFactoryTests.TestOperation> {
        TestDiscoveredOperationsFactory(ParameterValueMapper parameterValueMapper, Collection<OperationInvokerAdvisor> invokerAdvisors) {
            super(parameterValueMapper, invokerAdvisors);
        }

        @Override
        protected DiscoveredOperationsFactoryTests.TestOperation createOperation(EndpointId endpointId, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
            return new DiscoveredOperationsFactoryTests.TestOperation(endpointId, operationMethod, invoker);
        }
    }

    static class TestOperation extends AbstractDiscoveredOperation {
        TestOperation(EndpointId endpointId, DiscoveredOperationMethod operationMethod, OperationInvoker invoker) {
            super(operationMethod, invoker);
        }
    }

    static class TestOperationInvokerAdvisor implements OperationInvokerAdvisor {
        private EndpointId endpointId;

        private OperationType operationType;

        private OperationParameters parameters;

        @Override
        public OperationInvoker apply(EndpointId endpointId, OperationType operationType, OperationParameters parameters, OperationInvoker invoker) {
            this.endpointId = endpointId;
            this.operationType = operationType;
            this.parameters = parameters;
            return invoker;
        }

        public EndpointId getEndpointId() {
            return this.endpointId;
        }

        public OperationType getOperationType() {
            return this.operationType;
        }

        public OperationParameters getParameters() {
            return this.parameters;
        }
    }
}

