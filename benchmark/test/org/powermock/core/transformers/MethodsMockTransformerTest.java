/**
 * Copyright 2017 the original author or authors.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.powermock.core.transformers;


import MockGateway.PROCEED;
import java.lang.reflect.Method;
import javassist.ClassPool;
import javassist.CtClass;
import net.bytebuddy.utility.RandomString;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import org.powermock.core.transformers.support.DefaultMockTransformerChain;
import org.powermock.reflect.internal.WhiteboxImpl;
import powermock.test.support.MainMockTransformerTestSupport;

import static org.powermock.core.transformers.mock.MockGatewaySpy.ConditionBuilder.registered;


public class MethodsMockTransformerTest extends AbstractBaseMockTransformerTest {
    public MethodsMockTransformerTest(final TransformStrategy strategy, final MockTransformer transformer, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, DefaultMockTransformerChain.newBuilder().append(transformer).build(), mockClassloaderFactory);
    }

    @Test
    public void should_skip_call_to_void_private_method_if_getaway_return_not_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("voidPrivateMethod", "");
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.VoidMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        WhiteboxImpl.invokeMethod(instance, "voidPrivateMethod", "name");
        assertThat(WhiteboxImpl.getInternalState(instance, "lname")).as("Field name is not set").isNull();
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("voidPrivateMethod"));
    }

    @Test
    public void should_skip_call_to_void_public_method_if_getaway_return_not_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("voidMethod", "");
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.VoidMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        WhiteboxImpl.invokeMethod(instance, "voidMethod", "name", "field", 100.0);
        assertThat(WhiteboxImpl.getInternalState(instance, "field")).as("Field name is not set").isNull();
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("voidMethod"));
        assertThat(MockGatewaySpy.methodCalls()).isNot(registered().forMethod("voidPrivateMethod"));
    }

    @Test
    public void should_skip_call_to_final_void_public_method_if_getaway_return_not_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("finalVoidMethod", "");
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.VoidMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        WhiteboxImpl.invokeMethod(instance, "finalVoidMethod", "name", "field", 100.0);
        assertThat(WhiteboxImpl.getInternalState(instance, "field")).as("Field name is not set").isNull();
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("finalVoidMethod"));
    }

    @Test
    public void should_invoke_real_final_void_public_method_if_getaway_return_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("finalVoidMethod", PROCEED);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.VoidMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        final String fieldValue = RandomString.make(10);
        WhiteboxImpl.invokeMethod(instance, "finalVoidMethod", "name", fieldValue, 100.0);
        assertThat(WhiteboxImpl.getInternalState(instance, "field")).as("Field name is not set").isEqualTo(fieldValue);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("finalVoidMethod"));
    }

    @Test
    public void should_return_value_from_getaway_for_non_void_methods_is_it_is_not_PROCEED() throws Exception {
        final String expected = "mocked";
        MockGatewaySpy.returnOnMethodCall("returnMethod", expected);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.ReturnMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        final Object result = WhiteboxImpl.invokeMethod(instance, "returnMethod", "name", "field", 100.0);
        assertThat(result).isEqualTo(expected);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("returnMethod"));
    }

    @Test
    public void should_return_value_from_getaway_for_final_non_void_methods_is_it_is_not_PROCEED() throws Exception {
        final String expected = "mocked";
        MockGatewaySpy.returnOnMethodCall("finalReturnMethod", expected);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.ReturnMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        final Object result = WhiteboxImpl.invokeMethod(instance, "finalReturnMethod", "name", "field", 100.0);
        assertThat(result).isEqualTo(expected);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("finalReturnMethod"));
    }

    @Test
    public void should_return_value_from_getaway_for_final_private_non_void_methods_is_it_is_not_PROCEED() throws Exception {
        final String expected = "mocked";
        MockGatewaySpy.returnOnMethodCall("privateReturnMethod", expected);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.ReturnMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        final Object result = WhiteboxImpl.invokeMethod(instance, "privateReturnMethod", "name");
        assertThat(result).isEqualTo(expected);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("privateReturnMethod"));
    }

    @Test
    public void should_return_real_method_return_value_for_non_void_methods_if_getaway_returns_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("returnMethod", PROCEED);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.ReturnMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        final String name = "name";
        final Object result = WhiteboxImpl.invokeMethod(instance, "returnMethod", name, "field", 100.0);
        String expected = name + "(a)";
        assertThat(result).isEqualTo(expected);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("returnMethod"));
    }

    @Test
    public void should_return_real_method_return_value_for_final_non_void_methods_if_getaway_returns_PROCEED() throws Exception {
        MockGatewaySpy.returnOnMethodCall("finalReturnMethod", PROCEED);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.ReturnMethodsTestClass.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        final String name = "name";
        final Object result = WhiteboxImpl.invokeMethod(instance, "finalReturnMethod", name, "field", 100.0);
        String expected = name + "(a)";
        assertThat(result).isEqualTo(expected);
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("finalReturnMethod"));
    }

    @Test
    public void should_ignore_abstract_methods() throws Exception {
        final Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                loadWithMockClassLoader(MainMockTransformerTestSupport.AbstractMethodTestClass.class.getName());
            }
        });
        assertThat(throwable).as("Abstract class is transformed").isNull();
    }

    @Test
    public void should_modify_bridge_methods() throws Throwable {
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SubclassWithBridgeMethod.class.getName());
        final Object instance = WhiteboxImpl.newInstance(clazz);
        clazz.getMethod("doSomething", String.class).invoke(instance, "value");
        assertThat(MockGatewaySpy.methodCalls()).is(registered().forMethod("doSomething"));
    }

    @Test
    public void should_ignore_synthetic_non_bridge_methods() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "return;");
        final Class<?> clazz = loadWithMockClassLoader(ctClass);
        Method method = null;
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(AbstractBaseMockTransformerTest.SYNTHETIC_METHOD_NAME)) {
                method = m;
                break;
            }
        }
        final Object instance = WhiteboxImpl.newInstance(clazz);
        assertThat(method).isNotNull();
        method.setAccessible(true);
        method.invoke(instance, "");
        assertThat(MockGatewaySpy.methodCalls()).isNot(registered().forMethod(AbstractBaseMockTransformerTest.SYNTHETIC_METHOD_NAME));
    }

    @Test
    public void should_ignore_call_to_synthetic_non_bridge_methods() throws Throwable {
        final ClassPool classPool = new ClassPool(true);
        CtClass ctClass = prepareClassesForTest(classPool, "syntheticMethodIsCalled = true;");
        final Class<?> clazz = loadWithMockClassLoader(ctClass);
        final Object instance = WhiteboxImpl.newInstance(clazz);
        clazz.getMethod("doSomething", Object.class).invoke(instance, new Object());
        assertThat(MockGatewaySpy.methodCalls()).isNot(registered().forMethod(AbstractBaseMockTransformerTest.SYNTHETIC_METHOD_NAME));
        assertThat(WhiteboxImpl.getInternalState(clazz, "syntheticMethodIsCalled")).isEqualTo(true);
    }
}

