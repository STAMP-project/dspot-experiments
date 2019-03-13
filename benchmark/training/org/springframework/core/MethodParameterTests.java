/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.core;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class MethodParameterTests {
    private Method method;

    private MethodParameter stringParameter;

    private MethodParameter longParameter;

    private MethodParameter intReturnType;

    @Test
    public void testEquals() throws NoSuchMethodException {
        Assert.assertEquals(stringParameter, stringParameter);
        Assert.assertEquals(longParameter, longParameter);
        Assert.assertEquals(intReturnType, intReturnType);
        Assert.assertFalse(stringParameter.equals(longParameter));
        Assert.assertFalse(stringParameter.equals(intReturnType));
        Assert.assertFalse(longParameter.equals(stringParameter));
        Assert.assertFalse(longParameter.equals(intReturnType));
        Assert.assertFalse(intReturnType.equals(stringParameter));
        Assert.assertFalse(intReturnType.equals(longParameter));
        Method method = getClass().getMethod("method", String.class, Long.TYPE);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        Assert.assertEquals(stringParameter, methodParameter);
        Assert.assertEquals(methodParameter, stringParameter);
        Assert.assertNotEquals(longParameter, methodParameter);
        Assert.assertNotEquals(methodParameter, longParameter);
    }

    @Test
    public void testHashCode() throws NoSuchMethodException {
        Assert.assertEquals(stringParameter.hashCode(), stringParameter.hashCode());
        Assert.assertEquals(longParameter.hashCode(), longParameter.hashCode());
        Assert.assertEquals(intReturnType.hashCode(), intReturnType.hashCode());
        Method method = getClass().getMethod("method", String.class, Long.TYPE);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        Assert.assertEquals(stringParameter.hashCode(), methodParameter.hashCode());
        Assert.assertNotEquals(longParameter.hashCode(), methodParameter.hashCode());
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testFactoryMethods() {
        Assert.assertEquals(stringParameter, MethodParameter.forMethodOrConstructor(method, 0));
        Assert.assertEquals(longParameter, MethodParameter.forMethodOrConstructor(method, 1));
        Assert.assertEquals(stringParameter, MethodParameter.forExecutable(method, 0));
        Assert.assertEquals(longParameter, MethodParameter.forExecutable(method, 1));
        Assert.assertEquals(stringParameter, MethodParameter.forParameter(method.getParameters()[0]));
        Assert.assertEquals(longParameter, MethodParameter.forParameter(method.getParameters()[1]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexValidation() {
        new MethodParameter(method, 2);
    }

    @Test
    public void annotatedConstructorParameterInStaticNestedClass() throws Exception {
        Constructor<?> constructor = MethodParameterTests.NestedClass.class.getDeclaredConstructor(String.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(constructor, 0);
        Assert.assertEquals(String.class, methodParameter.getParameterType());
        Assert.assertNotNull("Failed to find @Param annotation", methodParameter.getParameterAnnotation(MethodParameterTests.Param.class));
    }

    // SPR-16652
    @Test
    public void annotatedConstructorParameterInInnerClass() throws Exception {
        Constructor<?> constructor = MethodParameterTests.InnerClass.class.getConstructor(getClass(), String.class, Callable.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(constructor, 0);
        Assert.assertEquals(getClass(), methodParameter.getParameterType());
        Assert.assertNull(methodParameter.getParameterAnnotation(MethodParameterTests.Param.class));
        methodParameter = MethodParameter.forExecutable(constructor, 1);
        Assert.assertEquals(String.class, methodParameter.getParameterType());
        Assert.assertNotNull("Failed to find @Param annotation", methodParameter.getParameterAnnotation(MethodParameterTests.Param.class));
        methodParameter = MethodParameter.forExecutable(constructor, 2);
        Assert.assertEquals(Callable.class, methodParameter.getParameterType());
        Assert.assertNull(methodParameter.getParameterAnnotation(MethodParameterTests.Param.class));
    }

    // SPR-16734
    @Test
    public void genericConstructorParameterInInnerClass() throws Exception {
        Constructor<?> constructor = MethodParameterTests.InnerClass.class.getConstructor(getClass(), String.class, Callable.class);
        MethodParameter methodParameter = MethodParameter.forExecutable(constructor, 0);
        Assert.assertEquals(getClass(), methodParameter.getParameterType());
        Assert.assertEquals(getClass(), methodParameter.getGenericParameterType());
        methodParameter = MethodParameter.forExecutable(constructor, 1);
        Assert.assertEquals(String.class, methodParameter.getParameterType());
        Assert.assertEquals(String.class, methodParameter.getGenericParameterType());
        methodParameter = MethodParameter.forExecutable(constructor, 2);
        Assert.assertEquals(Callable.class, methodParameter.getParameterType());
        Assert.assertEquals(ResolvableType.forClassWithGenerics(Callable.class, Integer.class).getType(), methodParameter.getGenericParameterType());
    }

    @SuppressWarnings("unused")
    private static class NestedClass {
        NestedClass(@MethodParameterTests.Param
        String s) {
        }
    }

    @SuppressWarnings("unused")
    private class InnerClass {
        public InnerClass(@MethodParameterTests.Param
        String s, Callable<Integer> i) {
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    private @interface Param {}
}

