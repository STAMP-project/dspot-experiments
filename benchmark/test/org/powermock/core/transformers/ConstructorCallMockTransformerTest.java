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


import java.lang.reflect.Constructor;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.Test;
import org.powermock.core.IndicateReloadClass;
import org.powermock.core.MockGateway;
import org.powermock.core.test.MockClassLoaderFactory;
import org.powermock.core.transformers.mock.MockGatewaySpy;
import powermock.test.support.MainMockTransformerTestSupport;

import static powermock.test.support.MainMockTransformerTestSupport.ConstructorCall.SupperClassThrowsException.MESSAGE;


public class ConstructorCallMockTransformerTest extends AbstractBaseMockTransformerTest {
    public ConstructorCallMockTransformerTest(final TransformStrategy strategy, final MockTransformerChain mockTransformerChain, final MockClassLoaderFactory mockClassloaderFactory) {
        super(strategy, mockTransformerChain, mockClassloaderFactory);
    }

    @Test
    public void should_not_change_constructors_of_test_class() throws Exception {
        assumeClassLoaderMode();
        final Class<MainMockTransformerTestSupport.SupportClasses.MultipleConstructors> testClass = MainMockTransformerTestSupport.SupportClasses.MultipleConstructors.class;
        setTestClassToTransformers(testClass);
        final Class<?> modifiedClass = reloadClass(testClass);
        assertThat(modifiedClass.getConstructors()).hasSameSizeAs(testClass.getConstructors());
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                modifiedClass.getConstructor(IndicateReloadClass.class);
            }
        }).withFailMessage("A public defer-constructor is added.").isExactlyInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void should_not_change_constructors_of_nested_test_classes() throws Exception {
        assumeClassLoaderMode();
        setTestClassToTransformers(MainMockTransformerTestSupport.ParentTestClass.class);
        final Class<?> originalClazz = MainMockTransformerTestSupport.ParentTestClass.NestedTestClass.class;
        final Class<?> modifiedClass = reloadClass(originalClazz);
        assertThat(modifiedClass.getConstructors()).hasSameSizeAs(originalClazz.getConstructors());
        assertThatThrownBy(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                modifiedClass.getConstructor(IndicateReloadClass.class);
            }
        }).withFailMessage("A public defer-constructor is added.").isExactlyInstanceOf(NoSuchMethodException.class);
    }

    @Test
    public void should_add_additional_defer_constructor_which_call_default_if_parent_Object_and_strategy_classloader() throws Exception {
        assumeClassLoaderMode();
        assumeClassLoaderIsByteBuddy();
        Class<?> clazz = reloadClass(MainMockTransformerTestSupport.SupportClasses.PublicSuperClass.class);
        assertThat(clazz.getConstructors()).as("Number of constructors in modified class").hasSize(2);
        assertThat(clazz.getConstructor(IndicateReloadClass.class)).as("Defer-constructor returnOnMethodCall").isNotNull();
    }

    @Test
    public void should_add_additional_defer_constructor_which_call_default_if_parent_not_Object_and_strategy_classloader() throws Exception {
        assumeClassLoaderMode();
        Class<?> clazz = reloadClass(MainMockTransformerTestSupport.SupportClasses.SubClass.class);
        assertThat(clazz.getConstructors()).as("Number of constructors in modified class").hasSize(2);
        assertThat(clazz.getConstructor(IndicateReloadClass.class)).as("Defer-constructor returnOnMethodCall").isNotNull();
    }

    @Test
    public void should_not_add_additional_defer_constructor_if_strategy_is_not_classloader() throws Exception {
        assumeAgentMode();
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.SubClass.class.getName());
        assertThat(clazz.getConstructors()).as("Number of constructors in modified class").hasSameSizeAs(MainMockTransformerTestSupport.SupportClasses.SubClass.class.getConstructors());
    }

    @Test
    public void should_not_add_defer_constructor_to_interface() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SomeInterface.class.getName());
        assertThat(clazz.getConstructors()).as("Number of constructors in modified interface same as in original").hasSameSizeAs(MainMockTransformerTestSupport.SomeInterface.class.getConstructors());
    }

    @Test
    public void should_not_add_defer_constructor_to_enum() throws Exception {
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SupportClasses.EnumClass.class.getName());
        assertThat(clazz.getConstructors()).as("Number of constructors in modified class same as in original").hasSameSizeAs(MainMockTransformerTestSupport.SupportClasses.EnumClass.class.getConstructors());
    }

    @Test
    public void should_suppress_call_to_super_constructor_if_getaway_return_SUPPRESS() throws Exception {
        assumeClassLoaderMode();
        MockGatewaySpy.returnOnMethodCall(MockGateway.SUPPRESS);
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SuperClassCallSuperConstructor.class.getName());
        final Constructor<?> constructor = clazz.getConstructor(String.class, String.class, double.class);
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                try {
                    constructor.newInstance("name", "field", 100);
                } catch (Exception e) {
                    throw (e.getCause()) == null ? e : e.getCause();
                }
            }
        });
        assertThat(throwable).as("Call to super is suppressed").isNull();
    }

    @Test
    public void should_not_suppress_call_to_super_constructor_if_getaway_return_PROCEED() throws Exception {
        assumeClassLoaderMode();
        MockGatewaySpy.returnOnMethodCall(MockGateway.PROCEED);
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SuperClassCallSuperConstructor.class.getName());
        final Constructor<?> constructor = clazz.getConstructor(String.class, String.class, double.class);
        Throwable throwable = catchThrowable(new ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                try {
                    constructor.newInstance("name", "field", 100);
                } catch (Exception e) {
                    throw (e.getCause()) == null ? e : e.getCause();
                }
            }
        });
        assertThat(throwable).as("Call to super is not suppressed").isInstanceOf(IllegalArgumentException.class).hasMessage(MESSAGE);
    }

    @Test
    public void should_provide_correct_constructor_param_and_arguments() throws Exception {
        assumeClassLoaderMode();
        MockGatewaySpy.returnOnMethodCall(MockGateway.SUPPRESS);
        Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SuperClassCallSuperConstructor.class.getName());
        final Constructor<?> constructor = clazz.getConstructor(String.class, String.class, double.class);
        constructor.newInstance("name", "field", 100);
        assertThatCorrectConstructorTypeProvided();
        final MockGatewaySpy.MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
        assertThat(methodCall.args).as("Correct constructor arguments are provided").containsExactly("name", 100.0);
        assertThat(methodCall.sig).as("Correct constructor signature is provided").containsExactly(String.class, double.class);
    }

    @Test
    public void should_provide_correct_constructor_param_and_arguments_when_cast_required() throws Exception {
        assumeClassLoaderMode();
        MockGatewaySpy.returnOnMethodCall(MockGateway.SUPPRESS);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SuperClassCallSuperConstructorWithCast.class.getName());
        final Class<?> paramClass = loadWithMockClassLoader(MainMockTransformerTestSupport.ParameterInterface.class.getName());
        final Object param = loadWithMockClassLoader(MainMockTransformerTestSupport.ParameterImpl.class.getName()).newInstance();
        final Constructor<?> constructor = clazz.getConstructor(paramClass);
        constructor.newInstance(param);
        assertThatCorrectConstructorTypeProvided();
        final MockGatewaySpy.MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
        assertThat(methodCall.args).as("Correct constructor arguments are provided").containsExactly(param);
        assertThat(methodCall.sig).as("Correct constructor signature is provided").hasSize(1).extracting(new org.assertj.core.api.iterable.Extractor<Class<?>, Object>() {
            @Override
            public Object extract(final Class<?> input) {
                return input.getName();
            }
        }).containsExactly(MainMockTransformerTestSupport.ParameterImpl.class.getName());
    }

    @Test
    public void should_provide_correct_constructor_param_and_arguments_when_parameters_vararg() throws Exception {
        assumeClassLoaderMode();
        MockGatewaySpy.returnOnMethodCall(MockGateway.SUPPRESS);
        final Class<?> clazz = loadWithMockClassLoader(MainMockTransformerTestSupport.SuperClassCallSuperConstructorWithVararg.class.getName());
        final Class<?> paramClass = long[].class;
        final Constructor<?> constructor = clazz.getConstructor(paramClass);
        long[] params = new long[]{ 1, 5, 6 };
        constructor.newInstance(new Object[]{ params });
        assertThatCorrectConstructorTypeProvided();
        final MockGatewaySpy.MethodCall methodCall = MockGatewaySpy.constructorCalls().get(0);
        assertThat(methodCall.args).as("Constructor arguments have correct size").hasSize(1);
        assertThat(((long[]) (methodCall.args[0]))).as("Correct constructor arguments are provided").containsExactly(params);
        assertThat(methodCall.sig).as("Correct constructor signature is provided").hasSize(1).containsExactly(long[].class);
    }
}

