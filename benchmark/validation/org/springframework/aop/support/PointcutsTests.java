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


import Pointcut.TRUE;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.TestBean;

import static Pointcuts.GETTERS;
import static Pointcuts.SETTERS;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 */
public class PointcutsTests {
    public static Method TEST_BEAN_SET_AGE;

    public static Method TEST_BEAN_GET_AGE;

    public static Method TEST_BEAN_GET_NAME;

    public static Method TEST_BEAN_ABSQUATULATE;

    static {
        try {
            PointcutsTests.TEST_BEAN_SET_AGE = TestBean.class.getMethod("setAge", int.class);
            PointcutsTests.TEST_BEAN_GET_AGE = TestBean.class.getMethod("getAge");
            PointcutsTests.TEST_BEAN_GET_NAME = TestBean.class.getMethod("getName");
            PointcutsTests.TEST_BEAN_ABSQUATULATE = TestBean.class.getMethod("absquatulate");
        } catch (Exception ex) {
            throw new RuntimeException("Shouldn't happen: error in test suite");
        }
    }

    /**
     * Matches only TestBean class, not subclasses
     */
    public static Pointcut allTestBeanMethodsPointcut = new StaticMethodMatcherPointcut() {
        @Override
        public ClassFilter getClassFilter() {
            return new ClassFilter() {
                @Override
                public boolean matches(Class<?> clazz) {
                    return clazz.equals(TestBean.class);
                }
            };
        }

        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return true;
        }
    };

    public static Pointcut allClassSetterPointcut = SETTERS;

    // Subclass used for matching
    public static class MyTestBean extends TestBean {}

    public static Pointcut myTestBeanSetterPointcut = new StaticMethodMatcherPointcut() {
        @Override
        public ClassFilter getClassFilter() {
            return new RootClassFilter(PointcutsTests.MyTestBean.class);
        }

        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().startsWith("set");
        }
    };

    // Will match MyTestBeanSubclass
    public static Pointcut myTestBeanGetterPointcut = new StaticMethodMatcherPointcut() {
        @Override
        public ClassFilter getClassFilter() {
            return new RootClassFilter(PointcutsTests.MyTestBean.class);
        }

        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().startsWith("get");
        }
    };

    // Still more specific class
    public static class MyTestBeanSubclass extends PointcutsTests.MyTestBean {}

    public static Pointcut myTestBeanSubclassGetterPointcut = new StaticMethodMatcherPointcut() {
        @Override
        public ClassFilter getClassFilter() {
            return new RootClassFilter(PointcutsTests.MyTestBeanSubclass.class);
        }

        @Override
        public boolean matches(Method m, @Nullable
        Class<?> targetClass) {
            return m.getName().startsWith("get");
        }
    };

    public static Pointcut allClassGetterPointcut = GETTERS;

    public static Pointcut allClassGetAgePointcut = new NameMatchMethodPointcut().addMethodName("getAge");

    public static Pointcut allClassGetNamePointcut = new NameMatchMethodPointcut().addMethodName("getName");

    @Test
    public void testTrue() {
        Assert.assertTrue(Pointcuts.matches(TRUE, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(TRUE, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(TRUE, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(TRUE, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(TRUE, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(TRUE, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
    }

    @Test
    public void testMatches() {
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.allClassSetterPointcut, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.allClassSetterPointcut, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.allClassSetterPointcut, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.allClassGetterPointcut, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.allClassGetterPointcut, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.allClassGetterPointcut, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
    }

    /**
     * Should match all setters and getters on any class
     */
    @Test
    public void testUnionOfSettersAndGetters() {
        Pointcut union = Pointcuts.union(PointcutsTests.allClassGetterPointcut, PointcutsTests.allClassSetterPointcut);
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
    }

    @Test
    public void testUnionOfSpecificGetters() {
        Pointcut union = Pointcuts.union(PointcutsTests.allClassGetAgePointcut, PointcutsTests.allClassGetNamePointcut);
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.allClassGetAgePointcut, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        // Union with all setters
        union = Pointcuts.union(union, PointcutsTests.allClassSetterPointcut);
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.allClassGetAgePointcut, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
    }

    /**
     * Tests vertical composition. First pointcut matches all setters.
     * Second one matches all getters in the MyTestBean class. TestBean getters shouldn't pass.
     */
    @Test
    public void testUnionOfAllSettersAndSubclassSetters() {
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.myTestBeanSetterPointcut, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.myTestBeanSetterPointcut, PointcutsTests.TEST_BEAN_SET_AGE, PointcutsTests.MyTestBean.class, new Integer(6)));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.myTestBeanSetterPointcut, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Pointcut union = Pointcuts.union(PointcutsTests.myTestBeanSetterPointcut, PointcutsTests.allClassGetterPointcut);
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBean.class));
        // Still doesn't match superclass setter
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_SET_AGE, PointcutsTests.MyTestBean.class, new Integer(6)));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
    }

    /**
     * Intersection should be MyTestBean getAge() only:
     * it's the union of allClassGetAge and subclass getters
     */
    @Test
    public void testIntersectionOfSpecificGettersAndSubclassGetters() {
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.allClassGetAgePointcut, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.allClassGetAgePointcut, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.myTestBeanGetterPointcut, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(PointcutsTests.myTestBeanGetterPointcut, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.myTestBeanGetterPointcut, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBean.class));
        Assert.assertTrue(Pointcuts.matches(PointcutsTests.myTestBeanGetterPointcut, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBean.class));
        Pointcut intersection = Pointcuts.intersection(PointcutsTests.allClassGetAgePointcut, PointcutsTests.myTestBeanGetterPointcut);
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBean.class));
        Assert.assertTrue(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBean.class));
        // Matches subclass of MyTestBean
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBeanSubclass.class));
        Assert.assertTrue(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBeanSubclass.class));
        // Now intersection with MyTestBeanSubclass getters should eliminate MyTestBean target
        intersection = Pointcuts.intersection(intersection, PointcutsTests.myTestBeanSubclassGetterPointcut);
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBean.class));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBean.class));
        // Still matches subclass of MyTestBean
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBeanSubclass.class));
        Assert.assertTrue(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBeanSubclass.class));
        // Now union with all TestBean methods
        Pointcut union = Pointcuts.union(intersection, PointcutsTests.allTestBeanMethodsPointcut);
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_NAME, TestBean.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBean.class));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBean.class));
        // Still matches subclass of MyTestBean
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_NAME, PointcutsTests.MyTestBeanSubclass.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_GET_AGE, PointcutsTests.MyTestBeanSubclass.class));
        Assert.assertTrue(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(union, PointcutsTests.TEST_BEAN_ABSQUATULATE, PointcutsTests.MyTestBean.class));
    }

    /**
     * The intersection of these two pointcuts leaves nothing.
     */
    @Test
    public void testSimpleIntersection() {
        Pointcut intersection = Pointcuts.intersection(PointcutsTests.allClassGetterPointcut, PointcutsTests.allClassSetterPointcut);
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_SET_AGE, TestBean.class, new Integer(6)));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_GET_AGE, TestBean.class));
        Assert.assertFalse(Pointcuts.matches(intersection, PointcutsTests.TEST_BEAN_ABSQUATULATE, TestBean.class));
    }
}

