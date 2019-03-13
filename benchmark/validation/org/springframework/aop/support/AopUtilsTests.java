/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.aop.support;


import EmptyTargetSource.INSTANCE;
import MethodMatcher.TRUE;
import Pointcuts.GETTERS;
import Pointcuts.SETTERS;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.SerializationTestUtils;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class AopUtilsTests {
    @Test
    public void testPointcutCanNeverApply() {
        class TestPointcut extends StaticMethodMatcherPointcut {
            @Override
            public boolean matches(Method method, @Nullable
            Class<?> clazzy) {
                return false;
            }
        }
        Pointcut no = new TestPointcut();
        Assert.assertFalse(AopUtils.canApply(no, Object.class));
    }

    @Test
    public void testPointcutAlwaysApplies() {
        Assert.assertTrue(AopUtils.canApply(new DefaultPointcutAdvisor(new NopInterceptor()), Object.class));
        Assert.assertTrue(AopUtils.canApply(new DefaultPointcutAdvisor(new NopInterceptor()), TestBean.class));
    }

    @Test
    public void testPointcutAppliesToOneMethodOnObject() {
        class TestPointcut extends StaticMethodMatcherPointcut {
            @Override
            public boolean matches(Method method, @Nullable
            Class<?> clazz) {
                return method.getName().equals("hashCode");
            }
        }
        Pointcut pc = new TestPointcut();
        // will return true if we're not proxying interfaces
        Assert.assertTrue(AopUtils.canApply(pc, Object.class));
    }

    /**
     * Test that when we serialize and deserialize various canonical instances
     * of AOP classes, they return the same instance, not a new instance
     * that's subverted the singleton construction limitation.
     */
    @Test
    public void testCanonicalFrameworkClassesStillCanonicalOnDeserialization() throws Exception {
        Assert.assertSame(TRUE, SerializationTestUtils.serializeAndDeserialize(TRUE));
        Assert.assertSame(ClassFilter.TRUE, SerializationTestUtils.serializeAndDeserialize(ClassFilter.TRUE));
        Assert.assertSame(Pointcut.TRUE, SerializationTestUtils.serializeAndDeserialize(Pointcut.TRUE));
        Assert.assertSame(INSTANCE, SerializationTestUtils.serializeAndDeserialize(INSTANCE));
        Assert.assertSame(SETTERS, SerializationTestUtils.serializeAndDeserialize(SETTERS));
        Assert.assertSame(GETTERS, SerializationTestUtils.serializeAndDeserialize(GETTERS));
        Assert.assertSame(ExposeInvocationInterceptor.INSTANCE, SerializationTestUtils.serializeAndDeserialize(ExposeInvocationInterceptor.INSTANCE));
    }
}

