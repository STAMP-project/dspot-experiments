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
package org.springframework.aop.aspectj;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import org.junit.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.ThrowsAdvice;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.core.OverridingClassLoader;
import org.springframework.lang.Nullable;


/**
 *
 *
 * @author Dave Syer
 */
public class TrickyAspectJPointcutExpressionTests {
    @Test
    public void testManualProxyJavaWithUnconditionalPointcut() throws Exception {
        TrickyAspectJPointcutExpressionTests.TestService target = new TrickyAspectJPointcutExpressionTests.TestServiceImpl();
        TrickyAspectJPointcutExpressionTests.LogUserAdvice logAdvice = new TrickyAspectJPointcutExpressionTests.LogUserAdvice();
        testAdvice(new DefaultPointcutAdvisor(logAdvice), logAdvice, target, "TestServiceImpl");
    }

    @Test
    public void testManualProxyJavaWithStaticPointcut() throws Exception {
        TrickyAspectJPointcutExpressionTests.TestService target = new TrickyAspectJPointcutExpressionTests.TestServiceImpl();
        TrickyAspectJPointcutExpressionTests.LogUserAdvice logAdvice = new TrickyAspectJPointcutExpressionTests.LogUserAdvice();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.format("execution(* %s.TestService.*(..))", getClass().getName()));
        testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, target, "TestServiceImpl");
    }

    @Test
    public void testManualProxyJavaWithDynamicPointcut() throws Exception {
        TrickyAspectJPointcutExpressionTests.TestService target = new TrickyAspectJPointcutExpressionTests.TestServiceImpl();
        TrickyAspectJPointcutExpressionTests.LogUserAdvice logAdvice = new TrickyAspectJPointcutExpressionTests.LogUserAdvice();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.format("@within(%s.Log)", getClass().getName()));
        testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, target, "TestServiceImpl");
    }

    @Test
    public void testManualProxyJavaWithDynamicPointcutAndProxyTargetClass() throws Exception {
        TrickyAspectJPointcutExpressionTests.TestService target = new TrickyAspectJPointcutExpressionTests.TestServiceImpl();
        TrickyAspectJPointcutExpressionTests.LogUserAdvice logAdvice = new TrickyAspectJPointcutExpressionTests.LogUserAdvice();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.format("@within(%s.Log)", getClass().getName()));
        testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, target, "TestServiceImpl", true);
    }

    @Test
    public void testManualProxyJavaWithStaticPointcutAndTwoClassLoaders() throws Exception {
        TrickyAspectJPointcutExpressionTests.LogUserAdvice logAdvice = new TrickyAspectJPointcutExpressionTests.LogUserAdvice();
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(String.format("execution(* %s.TestService.*(..))", getClass().getName()));
        // Test with default class loader first...
        testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, new TrickyAspectJPointcutExpressionTests.TestServiceImpl(), "TestServiceImpl");
        // Then try again with a different class loader on the target...
        TrickyAspectJPointcutExpressionTests.SimpleThrowawayClassLoader loader = new TrickyAspectJPointcutExpressionTests.SimpleThrowawayClassLoader(new TrickyAspectJPointcutExpressionTests.TestServiceImpl().getClass().getClassLoader());
        // Make sure the interface is loaded from the  parent class loader
        excludeClass(TrickyAspectJPointcutExpressionTests.TestService.class.getName());
        excludeClass(TrickyAspectJPointcutExpressionTests.TestException.class.getName());
        TrickyAspectJPointcutExpressionTests.TestService other = ((TrickyAspectJPointcutExpressionTests.TestService) (loadClass(TrickyAspectJPointcutExpressionTests.TestServiceImpl.class.getName()).newInstance()));
        testAdvice(new DefaultPointcutAdvisor(pointcut, logAdvice), logAdvice, other, "TestServiceImpl");
    }

    public static class SimpleThrowawayClassLoader extends OverridingClassLoader {
        /**
         * Create a new SimpleThrowawayClassLoader for the given class loader.
         *
         * @param parent
         * 		the ClassLoader to build a throwaway ClassLoader for
         */
        public SimpleThrowawayClassLoader(ClassLoader parent) {
            super(parent);
        }
    }

    @SuppressWarnings("serial")
    public static class TestException extends RuntimeException {
        public TestException(String string) {
            super(string);
        }
    }

    @Target({ ElementType.METHOD, ElementType.TYPE })
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Inherited
    public static @interface Log {}

    public static interface TestService {
        public String sayHello();
    }

    @TrickyAspectJPointcutExpressionTests.Log
    public static class TestServiceImpl implements TrickyAspectJPointcutExpressionTests.TestService {
        @Override
        public String sayHello() {
            throw new TrickyAspectJPointcutExpressionTests.TestException("TestServiceImpl");
        }
    }

    public class LogUserAdvice implements MethodBeforeAdvice , ThrowsAdvice {
        private int countBefore = 0;

        private int countThrows = 0;

        @Override
        public void before(Method method, Object[] objects, @Nullable
        Object o) throws Throwable {
            (countBefore)++;
        }

        public void afterThrowing(Exception ex) throws Throwable {
            (countThrows)++;
            throw ex;
        }

        public int getCountBefore() {
            return countBefore;
        }

        public int getCountThrows() {
            return countThrows;
        }

        public void reset() {
            countThrows = 0;
            countBefore = 0;
        }
    }
}

