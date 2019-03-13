/**
 * Copyright 2002-2016 the original author or authors.
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
package org.springframework.core.annotation;


import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.MethodParameter;


/**
 *
 *
 * @author Juergen Hoeller
 * @since 5.0
 */
public class SynthesizingMethodParameterTests {
    private Method method;

    private SynthesizingMethodParameter stringParameter;

    private SynthesizingMethodParameter longParameter;

    private SynthesizingMethodParameter intReturnType;

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
        MethodParameter methodParameter = new SynthesizingMethodParameter(method, 0);
        Assert.assertEquals(stringParameter, methodParameter);
        Assert.assertEquals(methodParameter, stringParameter);
        Assert.assertNotEquals(longParameter, methodParameter);
        Assert.assertNotEquals(methodParameter, longParameter);
        methodParameter = new MethodParameter(method, 0);
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
        SynthesizingMethodParameter methodParameter = new SynthesizingMethodParameter(method, 0);
        Assert.assertEquals(stringParameter.hashCode(), methodParameter.hashCode());
        Assert.assertNotEquals(longParameter.hashCode(), methodParameter.hashCode());
    }

    @Test
    public void testFactoryMethods() {
        Assert.assertEquals(stringParameter, SynthesizingMethodParameter.forExecutable(method, 0));
        Assert.assertEquals(longParameter, SynthesizingMethodParameter.forExecutable(method, 1));
        Assert.assertEquals(stringParameter, SynthesizingMethodParameter.forParameter(method.getParameters()[0]));
        Assert.assertEquals(longParameter, SynthesizingMethodParameter.forParameter(method.getParameters()[1]));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIndexValidation() {
        new SynthesizingMethodParameter(method, 2);
    }
}

