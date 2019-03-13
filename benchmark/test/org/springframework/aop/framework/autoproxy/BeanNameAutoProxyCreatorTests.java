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
package org.springframework.aop.framework.autoproxy;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.tests.TimeStamped;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;
import test.mixin.Lockable;
import test.mixin.LockedException;


/**
 *
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Chris Beams
 */
public class BeanNameAutoProxyCreatorTests {
    private BeanFactory beanFactory;

    @Test
    public void testNoProxy() {
        TestBean tb = ((TestBean) (beanFactory.getBean("noproxy")));
        Assert.assertFalse(AopUtils.isAopProxy(tb));
        Assert.assertEquals("noproxy", tb.getName());
    }

    @Test
    public void testJdkProxyWithExactNameMatch() {
        ITestBean tb = ((ITestBean) (beanFactory.getBean("onlyJdk")));
        jdkAssertions(tb, 1);
        Assert.assertEquals("onlyJdk", tb.getName());
    }

    @Test
    public void testJdkProxyWithDoubleProxying() {
        ITestBean tb = ((ITestBean) (beanFactory.getBean("doubleJdk")));
        jdkAssertions(tb, 2);
        Assert.assertEquals("doubleJdk", tb.getName());
    }

    @Test
    public void testJdkIntroduction() {
        ITestBean tb = ((ITestBean) (beanFactory.getBean("introductionUsingJdk")));
        NopInterceptor nop = ((NopInterceptor) (beanFactory.getBean("introductionNopInterceptor")));
        Assert.assertEquals(0, nop.getCount());
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(tb));
        int age = 5;
        tb.setAge(age);
        Assert.assertEquals(age, tb.getAge());
        Assert.assertTrue("Introduction was made", (tb instanceof TimeStamped));
        Assert.assertEquals(0, getTimeStamp());
        Assert.assertEquals(3, nop.getCount());
        Assert.assertEquals("introductionUsingJdk", tb.getName());
        ITestBean tb2 = ((ITestBean) (beanFactory.getBean("second-introductionUsingJdk")));
        // Check two per-instance mixins were distinct
        Lockable lockable1 = ((Lockable) (tb));
        Lockable lockable2 = ((Lockable) (tb2));
        Assert.assertFalse(lockable1.locked());
        Assert.assertFalse(lockable2.locked());
        tb.setAge(65);
        Assert.assertEquals(65, tb.getAge());
        lockable1.lock();
        Assert.assertTrue(lockable1.locked());
        // Shouldn't affect second
        Assert.assertFalse(lockable2.locked());
        // Can still mod second object
        tb2.setAge(12);
        // But can't mod first
        try {
            tb.setAge(6);
            Assert.fail("Mixin should have locked this object");
        } catch (LockedException ex) {
            // Ok
        }
    }

    @Test
    public void testJdkIntroductionAppliesToCreatedObjectsNotFactoryBean() {
        ITestBean tb = ((ITestBean) (beanFactory.getBean("factory-introductionUsingJdk")));
        NopInterceptor nop = ((NopInterceptor) (beanFactory.getBean("introductionNopInterceptor")));
        Assert.assertEquals("NOP should not have done any work yet", 0, nop.getCount());
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(tb));
        int age = 5;
        tb.setAge(age);
        Assert.assertEquals(age, tb.getAge());
        Assert.assertTrue("Introduction was made", (tb instanceof TimeStamped));
        Assert.assertEquals(0, getTimeStamp());
        Assert.assertEquals(3, nop.getCount());
        ITestBean tb2 = ((ITestBean) (beanFactory.getBean("second-introductionUsingJdk")));
        // Check two per-instance mixins were distinct
        Lockable lockable1 = ((Lockable) (tb));
        Lockable lockable2 = ((Lockable) (tb2));
        Assert.assertFalse(lockable1.locked());
        Assert.assertFalse(lockable2.locked());
        tb.setAge(65);
        Assert.assertEquals(65, tb.getAge());
        lockable1.lock();
        Assert.assertTrue(lockable1.locked());
        // Shouldn't affect second
        Assert.assertFalse(lockable2.locked());
        // Can still mod second object
        tb2.setAge(12);
        // But can't mod first
        try {
            tb.setAge(6);
            Assert.fail("Mixin should have locked this object");
        } catch (LockedException ex) {
            // Ok
        }
    }

    @Test
    public void testJdkProxyWithWildcardMatch() {
        ITestBean tb = ((ITestBean) (beanFactory.getBean("jdk1")));
        jdkAssertions(tb, 1);
        Assert.assertEquals("jdk1", tb.getName());
    }

    @Test
    public void testCglibProxyWithWildcardMatch() {
        TestBean tb = ((TestBean) (beanFactory.getBean("cglib1")));
        cglibAssertions(tb);
        Assert.assertEquals("cglib1", tb.getName());
    }

    @Test
    public void testWithFrozenProxy() {
        ITestBean testBean = ((ITestBean) (beanFactory.getBean("frozenBean")));
        Assert.assertTrue(isFrozen());
    }
}

