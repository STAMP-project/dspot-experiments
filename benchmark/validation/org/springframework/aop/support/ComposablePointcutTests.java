/**
 * Copyright 2002-2015 the original author or authors.
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


import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.core.NestedRuntimeException;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class ComposablePointcutTests {
    public static MethodMatcher GETTER_METHOD_MATCHER = new StaticMethodMatcher() {
        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().startsWith("get");
        }
    };

    public static MethodMatcher GET_AGE_METHOD_MATCHER = new StaticMethodMatcher() {
        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().equals("getAge");
        }
    };

    public static MethodMatcher ABSQUATULATE_METHOD_MATCHER = new StaticMethodMatcher() {
        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().equals("absquatulate");
        }
    };

    public static MethodMatcher SETTER_METHOD_MATCHER = new StaticMethodMatcher() {
        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().startsWith("set");
        }
    };

    @Test
    public void testMatchAll() throws NoSuchMethodException {
        Pointcut pc = new ComposablePointcut();
        Assert.assertTrue(pc.getClassFilter().matches(Object.class));
        Assert.assertTrue(pc.getMethodMatcher().matches(Object.class.getMethod("hashCode"), Exception.class));
    }

    @Test
    public void testFilterByClass() throws NoSuchMethodException {
        ComposablePointcut pc = new ComposablePointcut();
        Assert.assertTrue(pc.getClassFilter().matches(Object.class));
        ClassFilter cf = new RootClassFilter(Exception.class);
        pc.intersection(cf);
        Assert.assertFalse(pc.getClassFilter().matches(Object.class));
        Assert.assertTrue(pc.getClassFilter().matches(Exception.class));
        pc.intersection(new RootClassFilter(NestedRuntimeException.class));
        Assert.assertFalse(pc.getClassFilter().matches(Exception.class));
        Assert.assertTrue(pc.getClassFilter().matches(NestedRuntimeException.class));
        Assert.assertFalse(pc.getClassFilter().matches(String.class));
        pc.union(new RootClassFilter(String.class));
        Assert.assertFalse(pc.getClassFilter().matches(Exception.class));
        Assert.assertTrue(pc.getClassFilter().matches(String.class));
        Assert.assertTrue(pc.getClassFilter().matches(NestedRuntimeException.class));
    }

    @Test
    public void testUnionMethodMatcher() {
        // Matches the getAge() method in any class
        ComposablePointcut pc = new ComposablePointcut(ClassFilter.TRUE, ComposablePointcutTests.GET_AGE_METHOD_MATCHER);
        Assert.assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        pc.union(ComposablePointcutTests.GETTER_METHOD_MATCHER);
        // Should now match all getter methods
        Assert.assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        pc.union(ComposablePointcutTests.ABSQUATULATE_METHOD_MATCHER);
        // Should now match absquatulate() as well
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        // But it doesn't match everything
        Assert.assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class));
    }

    @Test
    public void testIntersectionMethodMatcher() {
        ComposablePointcut pc = new ComposablePointcut();
        Assert.assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        pc.intersection(ComposablePointcutTests.GETTER_METHOD_MATCHER);
        Assert.assertFalse(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(pc.getMethodMatcher().matches(PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        pc.intersection(ComposablePointcutTests.GET_AGE_METHOD_MATCHER);
        // Use the Pointcuts matches method
        Assert.assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(pc, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
    }

    @Test
    public void testEqualsAndHashCode() throws Exception {
        ComposablePointcut pc1 = new ComposablePointcut();
        ComposablePointcut pc2 = new ComposablePointcut();
        Assert.assertEquals(pc1, pc2);
        Assert.assertEquals(pc1.hashCode(), pc2.hashCode());
        pc1.intersection(ComposablePointcutTests.GETTER_METHOD_MATCHER);
        Assert.assertFalse(pc1.equals(pc2));
        Assert.assertFalse(((pc1.hashCode()) == (pc2.hashCode())));
        pc2.intersection(ComposablePointcutTests.GETTER_METHOD_MATCHER);
        Assert.assertEquals(pc1, pc2);
        Assert.assertEquals(pc1.hashCode(), pc2.hashCode());
        pc1.union(ComposablePointcutTests.GET_AGE_METHOD_MATCHER);
        pc2.union(ComposablePointcutTests.GET_AGE_METHOD_MATCHER);
        Assert.assertEquals(pc1, pc2);
        Assert.assertEquals(pc1.hashCode(), pc2.hashCode());
    }
}

