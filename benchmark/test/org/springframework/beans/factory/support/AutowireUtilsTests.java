/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.beans.factory.support;


import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;


/**
 * Unit tests for {@link AutowireUtils}.
 *
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Lo?c Ledoyen
 */
public class AutowireUtilsTests {
    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void genericMethodReturnTypes() {
        Method notParameterized = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "notParameterized");
        Assert.assertEquals(String.class, AutowireUtils.resolveReturnTypeForFactoryMethod(notParameterized, new Object[]{  }, getClass().getClassLoader()));
        Method notParameterizedWithArguments = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "notParameterizedWithArguments", Integer.class, Boolean.class);
        Assert.assertEquals(String.class, AutowireUtils.resolveReturnTypeForFactoryMethod(notParameterizedWithArguments, new Object[]{ 99, true }, getClass().getClassLoader()));
        Method createProxy = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "createProxy", Object.class);
        Assert.assertEquals(String.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createProxy, new Object[]{ "foo" }, getClass().getClassLoader()));
        Method createNamedProxyWithDifferentTypes = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "createNamedProxy", String.class, Object.class);
        Assert.assertEquals(Long.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createNamedProxyWithDifferentTypes, new Object[]{ "enigma", 99L }, getClass().getClassLoader()));
        Method createNamedProxyWithDuplicateTypes = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "createNamedProxy", String.class, Object.class);
        Assert.assertEquals(String.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createNamedProxyWithDuplicateTypes, new Object[]{ "enigma", "foo" }, getClass().getClassLoader()));
        Method createMock = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "createMock", Class.class);
        Assert.assertEquals(Runnable.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createMock, new Object[]{ Runnable.class }, getClass().getClassLoader()));
        Assert.assertEquals(Runnable.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createMock, new Object[]{ Runnable.class.getName() }, getClass().getClassLoader()));
        Method createNamedMock = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "createNamedMock", String.class, Class.class);
        Assert.assertEquals(Runnable.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createNamedMock, new Object[]{ "foo", Runnable.class }, getClass().getClassLoader()));
        Method createVMock = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "createVMock", Object.class, Class.class);
        Assert.assertEquals(Runnable.class, AutowireUtils.resolveReturnTypeForFactoryMethod(createVMock, new Object[]{ "foo", Runnable.class }, getClass().getClassLoader()));
        // Ideally we would expect String.class instead of Object.class, but
        // resolveReturnTypeForFactoryMethod() does not currently support this form of
        // look-up.
        Method extractValueFrom = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "extractValueFrom", AutowireUtilsTests.MyInterfaceType.class);
        Assert.assertEquals(Object.class, AutowireUtils.resolveReturnTypeForFactoryMethod(extractValueFrom, new Object[]{ new AutowireUtilsTests.MySimpleInterfaceType() }, getClass().getClassLoader()));
        // Ideally we would expect Boolean.class instead of Object.class, but this
        // information is not available at run-time due to type erasure.
        Map<Integer, Boolean> map = new HashMap<>();
        map.put(0, false);
        map.put(1, true);
        Method extractMagicValue = ReflectionUtils.findMethod(AutowireUtilsTests.MyTypeWithMethods.class, "extractMagicValue", Map.class);
        Assert.assertEquals(Object.class, AutowireUtils.resolveReturnTypeForFactoryMethod(extractMagicValue, new Object[]{ map }, getClass().getClassLoader()));
    }

    @Test
    public void isAutowirablePreconditions() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Parameter must not be null");
        AutowireUtils.isAutowirable(null, 0);
    }

    @Test
    public void annotatedParametersInMethodAreCandidatesForAutowiring() throws Exception {
        Method method = getClass().getDeclaredMethod("autowirableMethod", String.class, String.class, String.class, String.class);
        assertAutowirableParameters(method);
    }

    @Test
    public void annotatedParametersInTopLevelClassConstructorAreCandidatesForAutowiring() throws Exception {
        Constructor<?> constructor = AutowireUtilsTests.AutowirableClass.class.getConstructor(String.class, String.class, String.class, String.class);
        assertAutowirableParameters(constructor);
    }

    @Test
    public void annotatedParametersInInnerClassConstructorAreCandidatesForAutowiring() throws Exception {
        Class<?> innerClass = AutowireUtilsTests.AutowirableClass.InnerAutowirableClass.class;
        Assert.assertTrue(ClassUtils.isInnerClass(innerClass));
        Constructor<?> constructor = innerClass.getConstructor(AutowireUtilsTests.AutowirableClass.class, String.class, String.class);
        assertAutowirableParameters(constructor);
    }

    @Test
    public void nonAnnotatedParametersInTopLevelClassConstructorAreNotCandidatesForAutowiring() throws Exception {
        Constructor<?> notAutowirableConstructor = AutowireUtilsTests.AutowirableClass.class.getConstructor(String.class);
        Parameter[] parameters = notAutowirableConstructor.getParameters();
        for (int parameterIndex = 0; parameterIndex < (parameters.length); parameterIndex++) {
            Parameter parameter = parameters[parameterIndex];
            Assert.assertFalse((("Parameter " + parameter) + " must not be autowirable"), AutowireUtils.isAutowirable(parameter, parameterIndex));
        }
    }

    @Test
    public void resolveDependencyPreconditionsForParameter() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Parameter must not be null");
        AutowireUtils.resolveDependency(null, 0, null, Mockito.mock(AutowireCapableBeanFactory.class));
    }

    @Test
    public void resolveDependencyPreconditionsForContainingClass() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Containing class must not be null");
        AutowireUtils.resolveDependency(getParameter(), 0, null, null);
    }

    @Test
    public void resolveDependencyPreconditionsForBeanFactory() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("AutowireCapableBeanFactory must not be null");
        AutowireUtils.resolveDependency(getParameter(), 0, getClass(), null);
    }

    @Test
    public void resolveDependencyForAnnotatedParametersInTopLevelClassConstructor() throws Exception {
        Constructor<?> constructor = AutowireUtilsTests.AutowirableClass.class.getConstructor(String.class, String.class, String.class, String.class);
        AutowireCapableBeanFactory beanFactory = Mockito.mock(AutowireCapableBeanFactory.class);
        // Configure the mocked BeanFactory to return the DependencyDescriptor for convenience and
        // to avoid using an ArgumentCaptor.
        Mockito.when(beanFactory.resolveDependency(ArgumentMatchers.any(), ArgumentMatchers.isNull())).thenAnswer(( invocation) -> invocation.getArgument(0));
        Parameter[] parameters = constructor.getParameters();
        for (int parameterIndex = 0; parameterIndex < (parameters.length); parameterIndex++) {
            Parameter parameter = parameters[parameterIndex];
            DependencyDescriptor intermediateDependencyDescriptor = ((DependencyDescriptor) (AutowireUtils.resolveDependency(parameter, parameterIndex, AutowireUtilsTests.AutowirableClass.class, beanFactory)));
            Assert.assertEquals(constructor, intermediateDependencyDescriptor.getAnnotatedElement());
            Assert.assertEquals(parameter, intermediateDependencyDescriptor.getMethodParameter().getParameter());
        }
    }

    public interface MyInterfaceType<T> {}

    public class MySimpleInterfaceType implements AutowireUtilsTests.MyInterfaceType<String> {}

    public static class MyTypeWithMethods<T> {
        /**
         * Simulates a factory method that wraps the supplied object in a proxy of the
         * same type.
         */
        public static <T> T createProxy(T object) {
            return null;
        }

        /**
         * Similar to {@link #createProxy(Object)} but adds an additional argument before
         * the argument of type {@code T}. Note that they may potentially be of the same
         * time when invoked!
         */
        public static <T> T createNamedProxy(String name, T object) {
            return null;
        }

        /**
         * Simulates factory methods found in libraries such as Mockito and EasyMock.
         */
        public static <MOCK> MOCK createMock(Class<MOCK> toMock) {
            return null;
        }

        /**
         * Similar to {@link #createMock(Class)} but adds an additional method argument
         * before the parameterized argument.
         */
        public static <T> T createNamedMock(String name, Class<T> toMock) {
            return null;
        }

        /**
         * Similar to {@link #createNamedMock(String, Class)} but adds an additional
         * parameterized type.
         */
        public static <V extends Object, T> T createVMock(V name, Class<T> toMock) {
            return null;
        }

        /**
         * Extract some value of the type supported by the interface (i.e., by a concrete,
         * non-generic implementation of the interface).
         */
        public static <T> T extractValueFrom(AutowireUtilsTests.MyInterfaceType<T> myInterfaceType) {
            return null;
        }

        /**
         * Extract some magic value from the supplied map.
         */
        public static <K, V> V extractMagicValue(Map<K, V> map) {
            return null;
        }

        public AutowireUtilsTests.MyInterfaceType<Integer> integer() {
            return null;
        }

        public AutowireUtilsTests.MySimpleInterfaceType string() {
            return null;
        }

        public Object object() {
            return null;
        }

        @SuppressWarnings("rawtypes")
        public AutowireUtilsTests.MyInterfaceType raw() {
            return null;
        }

        public String notParameterized() {
            return null;
        }

        public String notParameterizedWithArguments(Integer x, Boolean b) {
            return null;
        }

        public void readIntegerInputMessage(AutowireUtilsTests.MyInterfaceType<Integer> message) {
        }

        public void readIntegerArrayInputMessage(AutowireUtilsTests.MyInterfaceType<Integer>[] message) {
        }

        public void readGenericArrayInputMessage(T[] message) {
        }
    }

    public static class AutowirableClass {
        public AutowirableClass(@Autowired
        String firstParameter, @Qualifier("someQualifier")
        String secondParameter, @Value("${someValue}")
        String thirdParameter, @Autowired(required = false)
        String fourthParameter) {
        }

        public AutowirableClass(String notAutowirableParameter) {
        }

        public class InnerAutowirableClass {
            public InnerAutowirableClass(@Autowired
            String firstParameter, @Qualifier("someQualifier")
            String secondParameter) {
            }
        }
    }
}

