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
package org.springframework.boot.actuate.endpoint.invoke.reflect;


import java.util.Collections;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.actuate.endpoint.InvocationContext;
import org.springframework.boot.actuate.endpoint.OperationType;
import org.springframework.boot.actuate.endpoint.SecurityContext;
import org.springframework.boot.actuate.endpoint.invoke.MissingParametersException;
import org.springframework.boot.actuate.endpoint.invoke.ParameterValueMapper;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;


/**
 * Tests for {@link ReflectiveOperationInvoker}.
 *
 * @author Phillip Webb
 */
public class ReflectiveOperationInvokerTests {
    private ReflectiveOperationInvokerTests.Example target;

    private OperationMethod operationMethod;

    private ParameterValueMapper parameterValueMapper;

    @Test
    public void createWhenTargetIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ReflectiveOperationInvoker(null, this.operationMethod, this.parameterValueMapper)).withMessageContaining("Target must not be null");
    }

    @Test
    public void createWhenOperationMethodIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ReflectiveOperationInvoker(this.target, null, this.parameterValueMapper)).withMessageContaining("OperationMethod must not be null");
    }

    @Test
    public void createWhenParameterValueMapperIsNullShouldThrowException() {
        assertThatIllegalArgumentException().isThrownBy(() -> new ReflectiveOperationInvoker(this.target, this.operationMethod, null)).withMessageContaining("ParameterValueMapper must not be null");
    }

    @Test
    public void invokeShouldInvokeMethod() {
        ReflectiveOperationInvoker invoker = new ReflectiveOperationInvoker(this.target, this.operationMethod, this.parameterValueMapper);
        Object result = invoker.invoke(new InvocationContext(Mockito.mock(SecurityContext.class), Collections.singletonMap("name", "boot")));
        assertThat(result).isEqualTo("toob");
    }

    @Test
    public void invokeWhenMissingNonNullableArgumentShouldThrowException() {
        ReflectiveOperationInvoker invoker = new ReflectiveOperationInvoker(this.target, this.operationMethod, this.parameterValueMapper);
        assertThatExceptionOfType(MissingParametersException.class).isThrownBy(() -> invoker.invoke(new InvocationContext(mock(.class), Collections.singletonMap("name", null))));
    }

    @Test
    public void invokeWhenMissingNullableArgumentShouldInvoke() {
        OperationMethod operationMethod = new OperationMethod(ReflectionUtils.findMethod(ReflectiveOperationInvokerTests.Example.class, "reverseNullable", String.class), OperationType.READ);
        ReflectiveOperationInvoker invoker = new ReflectiveOperationInvoker(this.target, operationMethod, this.parameterValueMapper);
        Object result = invoker.invoke(new InvocationContext(Mockito.mock(SecurityContext.class), Collections.singletonMap("name", null)));
        assertThat(result).isEqualTo("llun");
    }

    @Test
    public void invokeShouldResolveParameters() {
        ReflectiveOperationInvoker invoker = new ReflectiveOperationInvoker(this.target, this.operationMethod, this.parameterValueMapper);
        Object result = invoker.invoke(new InvocationContext(Mockito.mock(SecurityContext.class), Collections.singletonMap("name", 1234)));
        assertThat(result).isEqualTo("4321");
    }

    static class Example {
        String reverse(String name) {
            return new StringBuilder(name).reverse().toString();
        }

        String reverseNullable(@Nullable
        String name) {
            return new StringBuilder(String.valueOf(name)).reverse().toString();
        }
    }
}

