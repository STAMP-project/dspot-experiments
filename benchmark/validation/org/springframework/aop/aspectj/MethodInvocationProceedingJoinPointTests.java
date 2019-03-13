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
package org.springframework.aop.aspectj;


import ExposeInvocationInterceptor.ADVISOR;
import ProceedingJoinPoint.METHOD_EXECUTION;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.JoinPoint.StaticPart;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.reflect.Factory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Chris Beams
 * @author Ramnivas Laddad
 * @since 2.0
 */
public class MethodInvocationProceedingJoinPointTests {
    @Test
    public void testingBindingWithJoinPoint() {
        try {
            AbstractAspectJAdvice.currentJoinPoint();
            Assert.fail("Needs to be bound by interceptor action");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testingBindingWithProceedingJoinPoint() {
        try {
            AbstractAspectJAdvice.currentJoinPoint();
            Assert.fail("Needs to be bound by interceptor action");
        } catch (IllegalStateException ex) {
            // expected
        }
    }

    @Test
    public void testCanGetMethodSignatureFromJoinPoint() {
        final Object raw = new TestBean();
        // Will be set by advice during a method call
        final int newAge = 23;
        ProxyFactory pf = new ProxyFactory(raw);
        pf.setExposeProxy(true);
        pf.addAdvisor(ADVISOR);
        pf.addAdvice(new MethodBeforeAdvice() {
            private int depth;

            @Override
            public void before(Method method, Object[] args, @Nullable
            Object target) throws Throwable {
                JoinPoint jp = AbstractAspectJAdvice.currentJoinPoint();
                Assert.assertTrue("Method named in toString", jp.toString().contains(method.getName()));
                // Ensure that these don't cause problems
                jp.toShortString();
                jp.toLongString();
                Assert.assertSame(target, AbstractAspectJAdvice.currentJoinPoint().getTarget());
                Assert.assertFalse(AopUtils.isAopProxy(AbstractAspectJAdvice.currentJoinPoint().getTarget()));
                ITestBean thisProxy = ((ITestBean) (AbstractAspectJAdvice.currentJoinPoint().getThis()));
                Assert.assertTrue(AopUtils.isAopProxy(AbstractAspectJAdvice.currentJoinPoint().getThis()));
                Assert.assertNotSame(target, thisProxy);
                // Check getting again doesn't cause a problem
                Assert.assertSame(thisProxy, AbstractAspectJAdvice.currentJoinPoint().getThis());
                // Try reentrant call--will go through this advice.
                // Be sure to increment depth to avoid infinite recursion
                if (((depth)++) == 0) {
                    // Check that toString doesn't cause a problem
                    thisProxy.toString();
                    // Change age, so this will be returned by invocation
                    thisProxy.setAge(newAge);
                    Assert.assertEquals(newAge, thisProxy.getAge());
                }
                Assert.assertSame(AopContext.currentProxy(), thisProxy);
                Assert.assertSame(target, raw);
                Assert.assertSame(method.getName(), AbstractAspectJAdvice.currentJoinPoint().getSignature().getName());
                Assert.assertEquals(method.getModifiers(), AbstractAspectJAdvice.currentJoinPoint().getSignature().getModifiers());
                MethodSignature msig = ((MethodSignature) (AbstractAspectJAdvice.currentJoinPoint().getSignature()));
                Assert.assertSame("Return same MethodSignature repeatedly", msig, AbstractAspectJAdvice.currentJoinPoint().getSignature());
                Assert.assertSame("Return same JoinPoint repeatedly", AbstractAspectJAdvice.currentJoinPoint(), AbstractAspectJAdvice.currentJoinPoint());
                Assert.assertEquals(method.getDeclaringClass(), msig.getDeclaringType());
                Assert.assertTrue(Arrays.equals(method.getParameterTypes(), msig.getParameterTypes()));
                Assert.assertEquals(method.getReturnType(), msig.getReturnType());
                Assert.assertTrue(Arrays.equals(method.getExceptionTypes(), msig.getExceptionTypes()));
                msig.toLongString();
                msig.toShortString();
            }
        });
        ITestBean itb = ((ITestBean) (pf.getProxy()));
        // Any call will do
        Assert.assertEquals("Advice reentrantly set age", newAge, itb.getAge());
    }

    @Test
    public void testCanGetSourceLocationFromJoinPoint() {
        final Object raw = new TestBean();
        ProxyFactory pf = new ProxyFactory(raw);
        pf.addAdvisor(ADVISOR);
        pf.addAdvice(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, @Nullable
            Object target) throws Throwable {
                SourceLocation sloc = AbstractAspectJAdvice.currentJoinPoint().getSourceLocation();
                Assert.assertEquals("Same source location must be returned on subsequent requests", sloc, AbstractAspectJAdvice.currentJoinPoint().getSourceLocation());
                Assert.assertEquals(TestBean.class, sloc.getWithinType());
                try {
                    sloc.getLine();
                    Assert.fail("Can't get line number");
                } catch (UnsupportedOperationException ex) {
                    // Expected
                }
                try {
                    sloc.getFileName();
                    Assert.fail("Can't get file name");
                } catch (UnsupportedOperationException ex) {
                    // Expected
                }
            }
        });
        ITestBean itb = ((ITestBean) (pf.getProxy()));
        // Any call will do
        itb.getAge();
    }

    @Test
    public void testCanGetStaticPartFromJoinPoint() {
        final Object raw = new TestBean();
        ProxyFactory pf = new ProxyFactory(raw);
        pf.addAdvisor(ADVISOR);
        pf.addAdvice(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, @Nullable
            Object target) throws Throwable {
                StaticPart staticPart = AbstractAspectJAdvice.currentJoinPoint().getStaticPart();
                Assert.assertEquals("Same static part must be returned on subsequent requests", staticPart, AbstractAspectJAdvice.currentJoinPoint().getStaticPart());
                Assert.assertEquals(METHOD_EXECUTION, staticPart.getKind());
                Assert.assertSame(AbstractAspectJAdvice.currentJoinPoint().getSignature(), staticPart.getSignature());
                Assert.assertEquals(AbstractAspectJAdvice.currentJoinPoint().getSourceLocation(), staticPart.getSourceLocation());
            }
        });
        ITestBean itb = ((ITestBean) (pf.getProxy()));
        // Any call will do
        itb.getAge();
    }

    @Test
    public void toShortAndLongStringFormedCorrectly() throws Exception {
        final Object raw = new TestBean();
        ProxyFactory pf = new ProxyFactory(raw);
        pf.addAdvisor(ADVISOR);
        pf.addAdvice(new MethodBeforeAdvice() {
            @Override
            public void before(Method method, Object[] args, @Nullable
            Object target) throws Throwable {
                // makeEncSJP, although meant for computing the enclosing join point,
                // it serves our purpose here
                JoinPoint.StaticPart aspectJVersionJp = Factory.makeEncSJP(method);
                JoinPoint jp = AbstractAspectJAdvice.currentJoinPoint();
                Assert.assertEquals(aspectJVersionJp.getSignature().toLongString(), jp.getSignature().toLongString());
                Assert.assertEquals(aspectJVersionJp.getSignature().toShortString(), jp.getSignature().toShortString());
                Assert.assertEquals(aspectJVersionJp.getSignature().toString(), jp.getSignature().toString());
                Assert.assertEquals(aspectJVersionJp.toLongString(), jp.toLongString());
                Assert.assertEquals(aspectJVersionJp.toShortString(), jp.toShortString());
                Assert.assertEquals(aspectJVersionJp.toString(), jp.toString());
            }
        });
        ITestBean itb = ((ITestBean) (pf.getProxy()));
        itb.getAge();
        itb.setName("foo");
        itb.getDoctor();
        itb.getStringArray();
        itb.getSpouse();
        itb.setSpouse(new TestBean());
        try {
            itb.unreliableFileOperation();
        } catch (IOException ex) {
            // we don't really care...
        }
    }
}

