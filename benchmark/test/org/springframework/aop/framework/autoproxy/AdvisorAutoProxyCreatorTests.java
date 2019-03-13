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
package org.springframework.aop.framework.autoproxy;


import CountingTestBean.count;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.CommonsPool2TargetSource;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.aop.target.PrototypeTargetSource;
import org.springframework.aop.target.ThreadLocalTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.aop.advice.CountingBeforeAdvice;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.CountingTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import test.mixin.Lockable;


/**
 * Tests for auto proxy creation by advisor recognition.
 *
 * @see org.springframework.aop.framework.autoproxy.AdvisorAutoProxyCreatorIntegrationTests
 * @author Rod Johnson
 * @author Dave Syer
 * @author Chris Beams
 */
@SuppressWarnings("resource")
public class AdvisorAutoProxyCreatorTests {
    private static final Class<?> CLASS = AdvisorAutoProxyCreatorTests.class;

    private static final String CLASSNAME = AdvisorAutoProxyCreatorTests.CLASS.getSimpleName();

    private static final String DEFAULT_CONTEXT = (AdvisorAutoProxyCreatorTests.CLASSNAME) + "-context.xml";

    private static final String COMMON_INTERCEPTORS_CONTEXT = (AdvisorAutoProxyCreatorTests.CLASSNAME) + "-common-interceptors.xml";

    private static final String CUSTOM_TARGETSOURCE_CONTEXT = (AdvisorAutoProxyCreatorTests.CLASSNAME) + "-custom-targetsource.xml";

    private static final String QUICK_TARGETSOURCE_CONTEXT = (AdvisorAutoProxyCreatorTests.CLASSNAME) + "-quick-targetsource.xml";

    private static final String OPTIMIZED_CONTEXT = (AdvisorAutoProxyCreatorTests.CLASSNAME) + "-optimized.xml";

    /**
     * Check that we can provide a common interceptor that will
     * appear in the chain before "specific" interceptors,
     * which are sourced from matching advisors
     */
    @Test
    public void testCommonInterceptorAndAdvisor() throws Exception {
        BeanFactory bf = new ClassPathXmlApplicationContext(AdvisorAutoProxyCreatorTests.COMMON_INTERCEPTORS_CONTEXT, AdvisorAutoProxyCreatorTests.CLASS);
        ITestBean test1 = ((ITestBean) (bf.getBean("test1")));
        Assert.assertTrue(AopUtils.isAopProxy(test1));
        Lockable lockable1 = ((Lockable) (test1));
        NopInterceptor nop1 = ((NopInterceptor) (bf.getBean("nopInterceptor")));
        NopInterceptor nop2 = ((NopInterceptor) (bf.getBean("pointcutAdvisor", Advisor.class).getAdvice()));
        ITestBean test2 = ((ITestBean) (bf.getBean("test2")));
        Lockable lockable2 = ((Lockable) (test2));
        // Locking should be independent; nop is shared
        Assert.assertFalse(lockable1.locked());
        Assert.assertFalse(lockable2.locked());
        // equals 2 calls on shared nop, because it's first and sees calls
        // against the Lockable interface introduced by the specific advisor
        Assert.assertEquals(2, nop1.getCount());
        Assert.assertEquals(0, nop2.getCount());
        lockable1.lock();
        Assert.assertTrue(lockable1.locked());
        Assert.assertFalse(lockable2.locked());
        Assert.assertEquals(5, nop1.getCount());
        Assert.assertEquals(0, nop2.getCount());
        PackageVisibleMethod packageVisibleMethod = ((PackageVisibleMethod) (bf.getBean("packageVisibleMethod")));
        Assert.assertEquals(5, nop1.getCount());
        Assert.assertEquals(0, nop2.getCount());
        packageVisibleMethod.doSomething();
        Assert.assertEquals(6, nop1.getCount());
        Assert.assertEquals(1, nop2.getCount());
        Assert.assertTrue((packageVisibleMethod instanceof Lockable));
        Lockable lockable3 = ((Lockable) (packageVisibleMethod));
        lockable3.lock();
        Assert.assertTrue(lockable3.locked());
        lockable3.unlock();
        Assert.assertFalse(lockable3.locked());
    }

    /**
     * We have custom TargetSourceCreators but there's no match, and
     * hence no proxying, for this bean
     */
    @Test
    public void testCustomTargetSourceNoMatch() throws Exception {
        BeanFactory bf = new ClassPathXmlApplicationContext(AdvisorAutoProxyCreatorTests.CUSTOM_TARGETSOURCE_CONTEXT, AdvisorAutoProxyCreatorTests.CLASS);
        ITestBean test = ((ITestBean) (bf.getBean("test")));
        Assert.assertFalse(AopUtils.isAopProxy(test));
        Assert.assertEquals("Rod", test.getName());
        Assert.assertEquals("Kerry", test.getSpouse().getName());
    }

    @Test
    public void testCustomPrototypeTargetSource() throws Exception {
        CountingTestBean.count = 0;
        BeanFactory bf = new ClassPathXmlApplicationContext(AdvisorAutoProxyCreatorTests.CUSTOM_TARGETSOURCE_CONTEXT, AdvisorAutoProxyCreatorTests.CLASS);
        ITestBean test = ((ITestBean) (bf.getBean("prototypeTest")));
        Assert.assertTrue(AopUtils.isAopProxy(test));
        Advised advised = ((Advised) (test));
        Assert.assertTrue(((advised.getTargetSource()) instanceof PrototypeTargetSource));
        Assert.assertEquals("Rod", test.getName());
        // Check that references survived prototype creation
        Assert.assertEquals("Kerry", test.getSpouse().getName());
        Assert.assertEquals("Only 2 CountingTestBeans instantiated", 2, count);
        CountingTestBean.count = 0;
    }

    @Test
    public void testLazyInitTargetSource() throws Exception {
        CountingTestBean.count = 0;
        BeanFactory bf = new ClassPathXmlApplicationContext(AdvisorAutoProxyCreatorTests.CUSTOM_TARGETSOURCE_CONTEXT, AdvisorAutoProxyCreatorTests.CLASS);
        ITestBean test = ((ITestBean) (bf.getBean("lazyInitTest")));
        Assert.assertTrue(AopUtils.isAopProxy(test));
        Advised advised = ((Advised) (test));
        Assert.assertTrue(((advised.getTargetSource()) instanceof LazyInitTargetSource));
        Assert.assertEquals("No CountingTestBean instantiated yet", 0, count);
        Assert.assertEquals("Rod", test.getName());
        Assert.assertEquals("Kerry", test.getSpouse().getName());
        Assert.assertEquals("Only 1 CountingTestBean instantiated", 1, count);
        CountingTestBean.count = 0;
    }

    @Test
    public void testQuickTargetSourceCreator() throws Exception {
        ClassPathXmlApplicationContext bf = new ClassPathXmlApplicationContext(AdvisorAutoProxyCreatorTests.QUICK_TARGETSOURCE_CONTEXT, AdvisorAutoProxyCreatorTests.CLASS);
        ITestBean test = ((ITestBean) (bf.getBean("test")));
        Assert.assertFalse(AopUtils.isAopProxy(test));
        Assert.assertEquals("Rod", test.getName());
        // Check that references survived pooling
        Assert.assertEquals("Kerry", test.getSpouse().getName());
        // Now test the pooled one
        test = ((ITestBean) (bf.getBean(":test")));
        Assert.assertTrue(AopUtils.isAopProxy(test));
        Advised advised = ((Advised) (test));
        Assert.assertTrue(((advised.getTargetSource()) instanceof CommonsPool2TargetSource));
        Assert.assertEquals("Rod", test.getName());
        // Check that references survived pooling
        Assert.assertEquals("Kerry", test.getSpouse().getName());
        // Now test the ThreadLocal one
        test = ((ITestBean) (bf.getBean("%test")));
        Assert.assertTrue(AopUtils.isAopProxy(test));
        advised = ((Advised) (test));
        Assert.assertTrue(((advised.getTargetSource()) instanceof ThreadLocalTargetSource));
        Assert.assertEquals("Rod", test.getName());
        // Check that references survived pooling
        Assert.assertEquals("Kerry", test.getSpouse().getName());
        // Now test the Prototype TargetSource
        test = ((ITestBean) (bf.getBean("!test")));
        Assert.assertTrue(AopUtils.isAopProxy(test));
        advised = ((Advised) (test));
        Assert.assertTrue(((advised.getTargetSource()) instanceof PrototypeTargetSource));
        Assert.assertEquals("Rod", test.getName());
        // Check that references survived pooling
        Assert.assertEquals("Kerry", test.getSpouse().getName());
        ITestBean test2 = ((ITestBean) (bf.getBean("!test")));
        Assert.assertFalse("Prototypes cannot be the same object", (test == test2));
        Assert.assertEquals("Rod", test2.getName());
        Assert.assertEquals("Kerry", test2.getSpouse().getName());
        bf.close();
    }

    @Test
    public void testWithOptimizedProxy() throws Exception {
        BeanFactory beanFactory = new ClassPathXmlApplicationContext(AdvisorAutoProxyCreatorTests.OPTIMIZED_CONTEXT, AdvisorAutoProxyCreatorTests.CLASS);
        ITestBean testBean = ((ITestBean) (beanFactory.getBean("optimizedTestBean")));
        Assert.assertTrue(AopUtils.isAopProxy(testBean));
        CountingBeforeAdvice beforeAdvice = ((CountingBeforeAdvice) (beanFactory.getBean("countingAdvice")));
        testBean.setAge(23);
        testBean.getAge();
        Assert.assertEquals("Incorrect number of calls to proxy", 2, beforeAdvice.getCalls());
    }
}

