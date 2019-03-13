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
package org.springframework.aop.aspectj;


import PointcutPrimitive.CALL;
import java.lang.reflect.Method;
import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.UnsupportedPointcutPrimitiveException;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.MethodMatcher;
import org.springframework.aop.Pointcut;
import org.springframework.tests.sample.beans.IOther;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Chris Beams
 */
public class AspectJExpressionPointcutTests {
    public static final String MATCH_ALL_METHODS = "execution(* *(..))";

    private Method getAge;

    private Method setAge;

    private Method setSomeNumber;

    @Test
    public void testMatchExplicit() {
        String expression = "execution(int org.springframework.tests.sample.beans.TestBean.getAge())";
        Pointcut pointcut = getPointcut(expression);
        ClassFilter classFilter = pointcut.getClassFilter();
        MethodMatcher methodMatcher = pointcut.getMethodMatcher();
        assertMatchesTestBeanClass(classFilter);
        // not currently testable in a reliable fashion
        // assertDoesNotMatchStringClass(classFilter);
        Assert.assertFalse("Should not be a runtime match", methodMatcher.isRuntime());
        assertMatchesGetAge(methodMatcher);
        Assert.assertFalse("Expression should match setAge() method", methodMatcher.matches(setAge, TestBean.class));
    }

    @Test
    public void testMatchWithTypePattern() throws Exception {
        String expression = "execution(* *..TestBean.*Age(..))";
        Pointcut pointcut = getPointcut(expression);
        ClassFilter classFilter = pointcut.getClassFilter();
        MethodMatcher methodMatcher = pointcut.getMethodMatcher();
        assertMatchesTestBeanClass(classFilter);
        // not currently testable in a reliable fashion
        // assertDoesNotMatchStringClass(classFilter);
        Assert.assertFalse("Should not be a runtime match", methodMatcher.isRuntime());
        assertMatchesGetAge(methodMatcher);
        Assert.assertTrue("Expression should match setAge(int) method", methodMatcher.matches(setAge, TestBean.class));
    }

    @Test
    public void testThis() throws NoSuchMethodException, SecurityException {
        testThisOrTarget("this");
    }

    @Test
    public void testTarget() throws NoSuchMethodException, SecurityException {
        testThisOrTarget("target");
    }

    @Test
    public void testWithinRootPackage() throws NoSuchMethodException, SecurityException {
        testWithinPackage(false);
    }

    @Test
    public void testWithinRootAndSubpackages() throws NoSuchMethodException, SecurityException {
        testWithinPackage(true);
    }

    @Test
    public void testFriendlyErrorOnNoLocationClassMatching() {
        AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
        try {
            pc.matches(ITestBean.class);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("expression"));
        }
    }

    @Test
    public void testFriendlyErrorOnNoLocation2ArgMatching() {
        AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
        try {
            pc.matches(getAge, ITestBean.class);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("expression"));
        }
    }

    @Test
    public void testFriendlyErrorOnNoLocation3ArgMatching() {
        AspectJExpressionPointcut pc = new AspectJExpressionPointcut();
        try {
            pc.matches(getAge, ITestBean.class, ((Object[]) (null)));
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertTrue(ex.getMessage().contains("expression"));
        }
    }

    @Test
    public void testMatchWithArgs() throws Exception {
        String expression = "execution(void org.springframework.tests.sample.beans.TestBean.setSomeNumber(Number)) && args(Double)";
        Pointcut pointcut = getPointcut(expression);
        ClassFilter classFilter = pointcut.getClassFilter();
        MethodMatcher methodMatcher = pointcut.getMethodMatcher();
        assertMatchesTestBeanClass(classFilter);
        // not currently testable in a reliable fashion
        // assertDoesNotMatchStringClass(classFilter);
        Assert.assertTrue("Should match with setSomeNumber with Double input", methodMatcher.matches(setSomeNumber, TestBean.class, new Double(12)));
        Assert.assertFalse("Should not match setSomeNumber with Integer input", methodMatcher.matches(setSomeNumber, TestBean.class, new Integer(11)));
        Assert.assertFalse("Should not match getAge", methodMatcher.matches(getAge, TestBean.class));
        Assert.assertTrue("Should be a runtime match", methodMatcher.isRuntime());
    }

    @Test
    public void testSimpleAdvice() {
        String expression = "execution(int org.springframework.tests.sample.beans.TestBean.getAge())";
        CallCountingInterceptor interceptor = new CallCountingInterceptor();
        TestBean testBean = getAdvisedProxy(expression, interceptor);
        Assert.assertEquals("Calls should be 0", 0, interceptor.getCount());
        testBean.getAge();
        Assert.assertEquals("Calls should be 1", 1, interceptor.getCount());
        testBean.setAge(90);
        Assert.assertEquals("Calls should still be 1", 1, interceptor.getCount());
    }

    @Test
    public void testDynamicMatchingProxy() {
        String expression = "execution(void org.springframework.tests.sample.beans.TestBean.setSomeNumber(Number)) && args(Double)";
        CallCountingInterceptor interceptor = new CallCountingInterceptor();
        TestBean testBean = getAdvisedProxy(expression, interceptor);
        Assert.assertEquals("Calls should be 0", 0, interceptor.getCount());
        testBean.setSomeNumber(new Double(30));
        Assert.assertEquals("Calls should be 1", 1, interceptor.getCount());
        testBean.setSomeNumber(new Integer(90));
        Assert.assertEquals("Calls should be 1", 1, interceptor.getCount());
    }

    @Test
    public void testInvalidExpression() {
        String expression = "execution(void org.springframework.tests.sample.beans.TestBean.setSomeNumber(Number) && args(Double)";
        try {
            getPointcut(expression).getClassFilter();// call to getClassFilter forces resolution

            Assert.fail("Invalid expression should throw IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testWithUnsupportedPointcutPrimitive() {
        String expression = "call(int org.springframework.tests.sample.beans.TestBean.getAge())";
        try {
            getPointcut(expression).getClassFilter();// call to getClassFilter forces resolution...

            Assert.fail("Should not support call pointcuts");
        } catch (UnsupportedPointcutPrimitiveException ex) {
            Assert.assertEquals("Should not support call pointcut", CALL, ex.getUnsupportedPrimitive());
        }
    }

    @Test
    public void testAndSubstitution() {
        Pointcut pc = getPointcut("execution(* *(..)) and args(String)");
        PointcutExpression expr = getPointcutExpression();
        Assert.assertEquals("execution(* *(..)) && args(String)", expr.getPointcutExpression());
    }

    @Test
    public void testMultipleAndSubstitutions() {
        Pointcut pc = getPointcut("execution(* *(..)) and args(String) and this(Object)");
        PointcutExpression expr = getPointcutExpression();
        Assert.assertEquals("execution(* *(..)) && args(String) && this(Object)", expr.getPointcutExpression());
    }

    public static class OtherIOther implements IOther {
        @Override
        public void absquatulate() {
            // Empty
        }
    }
}

