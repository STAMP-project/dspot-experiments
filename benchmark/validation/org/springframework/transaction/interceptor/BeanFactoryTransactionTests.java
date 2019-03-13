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
package org.springframework.transaction.interceptor;


import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.tests.sample.beans.DerivedTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.tests.transaction.CallCountingTransactionManager;


/**
 * Test cases for AOP transaction management.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 23.04.2003
 */
public class BeanFactoryTransactionTests {
    private DefaultListableBeanFactory factory;

    @Test
    public void testGetsAreNotTransactionalWithProxyFactory1() {
        ITestBean testBean = ((ITestBean) (factory.getBean("proxyFactory1")));
        Assert.assertTrue("testBean is a dynamic proxy", Proxy.isProxyClass(testBean.getClass()));
        Assert.assertFalse((testBean instanceof TransactionalProxy));
        doTestGetsAreNotTransactional(testBean);
    }

    @Test
    public void testGetsAreNotTransactionalWithProxyFactory2DynamicProxy() {
        this.factory.preInstantiateSingletons();
        ITestBean testBean = ((ITestBean) (factory.getBean("proxyFactory2DynamicProxy")));
        Assert.assertTrue("testBean is a dynamic proxy", Proxy.isProxyClass(testBean.getClass()));
        Assert.assertTrue((testBean instanceof TransactionalProxy));
        doTestGetsAreNotTransactional(testBean);
    }

    @Test
    public void testGetsAreNotTransactionalWithProxyFactory2Cglib() {
        ITestBean testBean = ((ITestBean) (factory.getBean("proxyFactory2Cglib")));
        Assert.assertTrue("testBean is CGLIB advised", AopUtils.isCglibProxy(testBean));
        Assert.assertTrue((testBean instanceof TransactionalProxy));
        doTestGetsAreNotTransactional(testBean);
    }

    @Test
    public void testProxyFactory2Lazy() {
        ITestBean testBean = ((ITestBean) (factory.getBean("proxyFactory2Lazy")));
        Assert.assertFalse(factory.containsSingleton("target"));
        Assert.assertEquals(666, testBean.getAge());
        Assert.assertTrue(factory.containsSingleton("target"));
    }

    @Test
    public void testCglibTransactionProxyImplementsNoInterfaces() {
        ImplementsNoInterfaces ini = ((ImplementsNoInterfaces) (factory.getBean("cglibNoInterfaces")));
        Assert.assertTrue("testBean is CGLIB advised", AopUtils.isCglibProxy(ini));
        Assert.assertTrue((ini instanceof TransactionalProxy));
        String newName = "Gordon";
        // Install facade
        CallCountingTransactionManager ptm = new CallCountingTransactionManager();
        PlatformTransactionManagerFacade.delegate = ptm;
        ini.setName(newName);
        Assert.assertEquals(newName, ini.getName());
        Assert.assertEquals(2, ptm.commits);
    }

    @Test
    public void testGetsAreNotTransactionalWithProxyFactory3() {
        ITestBean testBean = ((ITestBean) (factory.getBean("proxyFactory3")));
        Assert.assertTrue("testBean is a full proxy", (testBean instanceof DerivedTestBean));
        Assert.assertTrue((testBean instanceof TransactionalProxy));
        BeanFactoryTransactionTests.InvocationCounterPointcut txnCounter = ((BeanFactoryTransactionTests.InvocationCounterPointcut) (factory.getBean("txnInvocationCounterPointcut")));
        BeanFactoryTransactionTests.InvocationCounterInterceptor preCounter = ((BeanFactoryTransactionTests.InvocationCounterInterceptor) (factory.getBean("preInvocationCounterInterceptor")));
        BeanFactoryTransactionTests.InvocationCounterInterceptor postCounter = ((BeanFactoryTransactionTests.InvocationCounterInterceptor) (factory.getBean("postInvocationCounterInterceptor")));
        txnCounter.counter = 0;
        preCounter.counter = 0;
        postCounter.counter = 0;
        doTestGetsAreNotTransactional(testBean);
        // Can't assert it's equal to 4 as the pointcut may be optimized and only invoked once
        Assert.assertTrue(((0 < (txnCounter.counter)) && ((txnCounter.counter) <= 4)));
        Assert.assertEquals(4, preCounter.counter);
        Assert.assertEquals(4, postCounter.counter);
    }

    @Test
    public void testGetBeansOfTypeWithAbstract() {
        Map<String, ITestBean> beansOfType = factory.getBeansOfType(ITestBean.class, true, true);
        Assert.assertNotNull(beansOfType);
    }

    /**
     * Check that we fail gracefully if the user doesn't set any transaction attributes.
     */
    @Test
    public void testNoTransactionAttributeSource() {
        try {
            DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
            new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource("noTransactionAttributeSource.xml", getClass()));
            bf.getBean("noTransactionAttributeSource");
            Assert.fail("Should require TransactionAttributeSource to be set");
        } catch (FatalBeanException ex) {
            // Ok
        }
    }

    /**
     * Test that we can set the target to a dynamic TargetSource.
     */
    @Test
    public void testDynamicTargetSource() {
        // Install facade
        CallCountingTransactionManager txMan = new CallCountingTransactionManager();
        PlatformTransactionManagerFacade.delegate = txMan;
        TestBean tb = ((TestBean) (factory.getBean("hotSwapped")));
        Assert.assertEquals(666, tb.getAge());
        int newAge = 557;
        tb.setAge(newAge);
        Assert.assertEquals(newAge, tb.getAge());
        TestBean target2 = new TestBean();
        target2.setAge(65);
        HotSwappableTargetSource ts = ((HotSwappableTargetSource) (factory.getBean("swapper")));
        ts.swap(target2);
        Assert.assertEquals(target2.getAge(), tb.getAge());
        tb.setAge(newAge);
        Assert.assertEquals(newAge, target2.getAge());
        Assert.assertEquals(0, txMan.inflight);
        Assert.assertEquals(2, txMan.commits);
        Assert.assertEquals(0, txMan.rollbacks);
    }

    public static class InvocationCounterPointcut extends StaticMethodMatcherPointcut {
        int counter = 0;

        @Override
        public boolean matches(Method method, @Nullable
        Class<?> clazz) {
            (counter)++;
            return true;
        }
    }

    public static class InvocationCounterInterceptor implements MethodInterceptor {
        int counter = 0;

        @Override
        public Object invoke(MethodInvocation methodInvocation) throws Throwable {
            (counter)++;
            return methodInvocation.proceed();
        }
    }
}

