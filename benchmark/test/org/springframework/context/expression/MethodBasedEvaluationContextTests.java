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
package org.springframework.context.expression;


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.ReflectionUtils;


/**
 * Unit tests for {@link MethodBasedEvaluationContext}.
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 * @author Sergey Podgurskiy
 */
public class MethodBasedEvaluationContextTests {
    private final ParameterNameDiscoverer paramDiscover = new DefaultParameterNameDiscoverer();

    @Test
    public void simpleArguments() {
        Method method = ReflectionUtils.findMethod(MethodBasedEvaluationContextTests.SampleMethods.class, "hello", String.class, Boolean.class);
        MethodBasedEvaluationContext context = createEvaluationContext(method, "test", true);
        Assert.assertEquals("test", context.lookupVariable("a0"));
        Assert.assertEquals("test", context.lookupVariable("p0"));
        Assert.assertEquals("test", context.lookupVariable("foo"));
        Assert.assertEquals(true, context.lookupVariable("a1"));
        Assert.assertEquals(true, context.lookupVariable("p1"));
        Assert.assertEquals(true, context.lookupVariable("flag"));
        Assert.assertNull(context.lookupVariable("a2"));
        Assert.assertNull(context.lookupVariable("p2"));
    }

    @Test
    public void nullArgument() {
        Method method = ReflectionUtils.findMethod(MethodBasedEvaluationContextTests.SampleMethods.class, "hello", String.class, Boolean.class);
        MethodBasedEvaluationContext context = createEvaluationContext(method, null, null);
        Assert.assertNull(context.lookupVariable("a0"));
        Assert.assertNull(context.lookupVariable("p0"));
        Assert.assertNull(context.lookupVariable("foo"));
        Assert.assertNull(context.lookupVariable("a1"));
        Assert.assertNull(context.lookupVariable("p1"));
        Assert.assertNull(context.lookupVariable("flag"));
    }

    @Test
    public void varArgEmpty() {
        Method method = ReflectionUtils.findMethod(MethodBasedEvaluationContextTests.SampleMethods.class, "hello", Boolean.class, String[].class);
        MethodBasedEvaluationContext context = createEvaluationContext(method, new Object[]{ null });
        Assert.assertNull(context.lookupVariable("a0"));
        Assert.assertNull(context.lookupVariable("p0"));
        Assert.assertNull(context.lookupVariable("flag"));
        Assert.assertNull(context.lookupVariable("a1"));
        Assert.assertNull(context.lookupVariable("p1"));
        Assert.assertNull(context.lookupVariable("vararg"));
    }

    @Test
    public void varArgNull() {
        Method method = ReflectionUtils.findMethod(MethodBasedEvaluationContextTests.SampleMethods.class, "hello", Boolean.class, String[].class);
        MethodBasedEvaluationContext context = createEvaluationContext(method, null, null);
        Assert.assertNull(context.lookupVariable("a0"));
        Assert.assertNull(context.lookupVariable("p0"));
        Assert.assertNull(context.lookupVariable("flag"));
        Assert.assertNull(context.lookupVariable("a1"));
        Assert.assertNull(context.lookupVariable("p1"));
        Assert.assertNull(context.lookupVariable("vararg"));
    }

    @Test
    public void varArgSingle() {
        Method method = ReflectionUtils.findMethod(MethodBasedEvaluationContextTests.SampleMethods.class, "hello", Boolean.class, String[].class);
        MethodBasedEvaluationContext context = createEvaluationContext(method, null, "hello");
        Assert.assertNull(context.lookupVariable("a0"));
        Assert.assertNull(context.lookupVariable("p0"));
        Assert.assertNull(context.lookupVariable("flag"));
        Assert.assertEquals("hello", context.lookupVariable("a1"));
        Assert.assertEquals("hello", context.lookupVariable("p1"));
        Assert.assertEquals("hello", context.lookupVariable("vararg"));
    }

    @Test
    public void varArgMultiple() {
        Method method = ReflectionUtils.findMethod(MethodBasedEvaluationContextTests.SampleMethods.class, "hello", Boolean.class, String[].class);
        MethodBasedEvaluationContext context = createEvaluationContext(method, null, "hello", "hi");
        Assert.assertNull(context.lookupVariable("a0"));
        Assert.assertNull(context.lookupVariable("p0"));
        Assert.assertNull(context.lookupVariable("flag"));
        Assert.assertArrayEquals(new Object[]{ "hello", "hi" }, ((Object[]) (context.lookupVariable("a1"))));
        Assert.assertArrayEquals(new Object[]{ "hello", "hi" }, ((Object[]) (context.lookupVariable("p1"))));
        Assert.assertArrayEquals(new Object[]{ "hello", "hi" }, ((Object[]) (context.lookupVariable("vararg"))));
    }

    @SuppressWarnings("unused")
    private static class SampleMethods {
        private void hello(String foo, Boolean flag) {
        }

        private void hello(Boolean flag, String... vararg) {
        }
    }
}

