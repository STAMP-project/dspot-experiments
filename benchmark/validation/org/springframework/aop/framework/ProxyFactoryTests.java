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
package org.springframework.aop.framework;


import java.util.ArrayList;
import java.util.List;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultIntroductionAdvisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.Order;
import org.springframework.tests.TimeStamped;
import org.springframework.tests.aop.advice.CountingBeforeAdvice;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.IOther;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Also tests AdvisedSupport and ProxyCreatorSupport superclasses.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 14.05.2003
 */
public class ProxyFactoryTests {
    @Test
    public void testIndexOfMethods() {
        TestBean target = new TestBean();
        ProxyFactory pf = new ProxyFactory(target);
        NopInterceptor nop = new NopInterceptor();
        Advisor advisor = new DefaultPointcutAdvisor(new CountingBeforeAdvice());
        Advised advised = ((Advised) (pf.getProxy()));
        // Can use advised and ProxyFactory interchangeably
        advised.addAdvice(nop);
        pf.addAdvisor(advisor);
        Assert.assertEquals((-1), pf.indexOf(new NopInterceptor()));
        Assert.assertEquals(0, pf.indexOf(nop));
        Assert.assertEquals(1, pf.indexOf(advisor));
        Assert.assertEquals((-1), advised.indexOf(new DefaultPointcutAdvisor(null)));
    }

    @Test
    public void testRemoveAdvisorByReference() {
        TestBean target = new TestBean();
        ProxyFactory pf = new ProxyFactory(target);
        NopInterceptor nop = new NopInterceptor();
        CountingBeforeAdvice cba = new CountingBeforeAdvice();
        Advisor advisor = new DefaultPointcutAdvisor(cba);
        pf.addAdvice(nop);
        pf.addAdvisor(advisor);
        ITestBean proxied = ((ITestBean) (pf.getProxy()));
        proxied.setAge(5);
        Assert.assertEquals(1, cba.getCalls());
        Assert.assertEquals(1, nop.getCount());
        Assert.assertTrue(pf.removeAdvisor(advisor));
        Assert.assertEquals(5, proxied.getAge());
        Assert.assertEquals(1, cba.getCalls());
        Assert.assertEquals(2, nop.getCount());
        Assert.assertFalse(pf.removeAdvisor(new DefaultPointcutAdvisor(null)));
    }

    @Test
    public void testRemoveAdvisorByIndex() {
        TestBean target = new TestBean();
        ProxyFactory pf = new ProxyFactory(target);
        NopInterceptor nop = new NopInterceptor();
        CountingBeforeAdvice cba = new CountingBeforeAdvice();
        Advisor advisor = new DefaultPointcutAdvisor(cba);
        pf.addAdvice(nop);
        pf.addAdvisor(advisor);
        NopInterceptor nop2 = new NopInterceptor();
        pf.addAdvice(nop2);
        ITestBean proxied = ((ITestBean) (pf.getProxy()));
        proxied.setAge(5);
        Assert.assertEquals(1, cba.getCalls());
        Assert.assertEquals(1, nop.getCount());
        Assert.assertEquals(1, nop2.getCount());
        // Removes counting before advisor
        pf.removeAdvisor(1);
        Assert.assertEquals(5, proxied.getAge());
        Assert.assertEquals(1, cba.getCalls());
        Assert.assertEquals(2, nop.getCount());
        Assert.assertEquals(2, nop2.getCount());
        // Removes Nop1
        pf.removeAdvisor(0);
        Assert.assertEquals(5, proxied.getAge());
        Assert.assertEquals(1, cba.getCalls());
        Assert.assertEquals(2, nop.getCount());
        Assert.assertEquals(3, nop2.getCount());
        // Check out of bounds
        try {
            pf.removeAdvisor((-1));
        } catch (AopConfigException ex) {
            // Ok
        }
        try {
            pf.removeAdvisor(2);
        } catch (AopConfigException ex) {
            // Ok
        }
        Assert.assertEquals(5, proxied.getAge());
        Assert.assertEquals(4, nop2.getCount());
    }

    @Test
    public void testReplaceAdvisor() {
        TestBean target = new TestBean();
        ProxyFactory pf = new ProxyFactory(target);
        NopInterceptor nop = new NopInterceptor();
        CountingBeforeAdvice cba1 = new CountingBeforeAdvice();
        CountingBeforeAdvice cba2 = new CountingBeforeAdvice();
        Advisor advisor1 = new DefaultPointcutAdvisor(cba1);
        Advisor advisor2 = new DefaultPointcutAdvisor(cba2);
        pf.addAdvisor(advisor1);
        pf.addAdvice(nop);
        ITestBean proxied = ((ITestBean) (pf.getProxy()));
        // Use the type cast feature
        // Replace etc methods on advised should be same as on ProxyFactory
        Advised advised = ((Advised) (proxied));
        proxied.setAge(5);
        Assert.assertEquals(1, cba1.getCalls());
        Assert.assertEquals(0, cba2.getCalls());
        Assert.assertEquals(1, nop.getCount());
        Assert.assertFalse(advised.replaceAdvisor(new DefaultPointcutAdvisor(new NopInterceptor()), advisor2));
        Assert.assertTrue(advised.replaceAdvisor(advisor1, advisor2));
        Assert.assertEquals(advisor2, pf.getAdvisors()[0]);
        Assert.assertEquals(5, proxied.getAge());
        Assert.assertEquals(1, cba1.getCalls());
        Assert.assertEquals(2, nop.getCount());
        Assert.assertEquals(1, cba2.getCalls());
        Assert.assertFalse(pf.replaceAdvisor(new DefaultPointcutAdvisor(null), advisor1));
    }

    @Test
    public void testAddRepeatedInterface() {
        TimeStamped tst = new TimeStamped() {
            @Override
            public long getTimeStamp() {
                throw new UnsupportedOperationException("getTimeStamp");
            }
        };
        ProxyFactory pf = new ProxyFactory(tst);
        // We've already implicitly added this interface.
        // This call should be ignored without error
        pf.addInterface(TimeStamped.class);
        // All cool
        Assert.assertThat(pf.getProxy(), CoreMatchers.instanceOf(TimeStamped.class));
    }

    @Test
    public void testGetsAllInterfaces() throws Exception {
        // Extend to get new interface
        class TestBeanSubclass extends TestBean implements Comparable<Object> {
            @Override
            public int compareTo(Object arg0) {
                throw new UnsupportedOperationException("compareTo");
            }
        }
        TestBeanSubclass raw = new TestBeanSubclass();
        ProxyFactory factory = new ProxyFactory(raw);
        // System.out.println("Proxied interfaces are " + StringUtils.arrayToDelimitedString(factory.getProxiedInterfaces(), ","));
        Assert.assertEquals("Found correct number of interfaces", 5, factory.getProxiedInterfaces().length);
        ITestBean tb = ((ITestBean) (factory.getProxy()));
        Assert.assertThat("Picked up secondary interface", tb, CoreMatchers.instanceOf(IOther.class));
        setAge(25);
        Assert.assertTrue(((tb.getAge()) == (getAge())));
        long t = 555555L;
        ProxyFactoryTests.TimestampIntroductionInterceptor ti = new ProxyFactoryTests.TimestampIntroductionInterceptor(t);
        Class<?>[] oldProxiedInterfaces = factory.getProxiedInterfaces();
        factory.addAdvisor(0, new DefaultIntroductionAdvisor(ti, TimeStamped.class));
        Class<?>[] newProxiedInterfaces = factory.getProxiedInterfaces();
        Assert.assertEquals("Advisor proxies one more interface after introduction", ((oldProxiedInterfaces.length) + 1), newProxiedInterfaces.length);
        TimeStamped ts = ((TimeStamped) (factory.getProxy()));
        Assert.assertTrue(((ts.getTimeStamp()) == t));
        // Shouldn't fail;
        absquatulate();
    }

    @Test
    public void testInterceptorInclusionMethods() {
        class MyInterceptor implements MethodInterceptor {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                throw new UnsupportedOperationException();
            }
        }
        NopInterceptor di = new NopInterceptor();
        NopInterceptor diUnused = new NopInterceptor();
        ProxyFactory factory = new ProxyFactory(new TestBean());
        factory.addAdvice(0, di);
        Assert.assertThat(factory.getProxy(), CoreMatchers.instanceOf(ITestBean.class));
        Assert.assertTrue(factory.adviceIncluded(di));
        Assert.assertTrue((!(factory.adviceIncluded(diUnused))));
        Assert.assertTrue(((factory.countAdvicesOfType(NopInterceptor.class)) == 1));
        Assert.assertTrue(((factory.countAdvicesOfType(MyInterceptor.class)) == 0));
        factory.addAdvice(0, diUnused);
        Assert.assertTrue(factory.adviceIncluded(diUnused));
        Assert.assertTrue(((factory.countAdvicesOfType(NopInterceptor.class)) == 2));
    }

    /**
     * Should see effect immediately on behavior.
     */
    @Test
    public void testCanAddAndRemoveAspectInterfacesOnSingleton() {
        ProxyFactory config = new ProxyFactory(new TestBean());
        Assert.assertFalse("Shouldn't implement TimeStamped before manipulation", ((config.getProxy()) instanceof TimeStamped));
        long time = 666L;
        ProxyFactoryTests.TimestampIntroductionInterceptor ti = new ProxyFactoryTests.TimestampIntroductionInterceptor();
        ti.setTime(time);
        // Add to front of interceptor chain
        int oldCount = config.getAdvisors().length;
        config.addAdvisor(0, new DefaultIntroductionAdvisor(ti, TimeStamped.class));
        Assert.assertTrue(((config.getAdvisors().length) == (oldCount + 1)));
        TimeStamped ts = ((TimeStamped) (config.getProxy()));
        Assert.assertTrue(((ts.getTimeStamp()) == time));
        // Can remove
        config.removeAdvice(ti);
        Assert.assertTrue(((config.getAdvisors().length) == oldCount));
        try {
            // Existing reference will fail
            ts.getTimeStamp();
            Assert.fail("Existing object won't implement this interface any more");
        } catch (RuntimeException ex) {
        }
        Assert.assertFalse("Should no longer implement TimeStamped", ((config.getProxy()) instanceof TimeStamped));
        // Now check non-effect of removing interceptor that isn't there
        config.removeAdvice(new DebugInterceptor());
        Assert.assertTrue(((config.getAdvisors().length) == oldCount));
        ITestBean it = ((ITestBean) (ts));
        DebugInterceptor debugInterceptor = new DebugInterceptor();
        config.addAdvice(0, debugInterceptor);
        it.getSpouse();
        Assert.assertEquals(1, debugInterceptor.getCount());
        config.removeAdvice(debugInterceptor);
        it.getSpouse();
        // not invoked again
        Assert.assertTrue(((debugInterceptor.getCount()) == 1));
    }

    @Test
    public void testProxyTargetClassWithInterfaceAsTarget() {
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetClass(ITestBean.class);
        Object proxy = pf.getProxy();
        Assert.assertTrue("Proxy is a JDK proxy", AopUtils.isJdkDynamicProxy(proxy));
        Assert.assertTrue((proxy instanceof ITestBean));
        Assert.assertEquals(ITestBean.class, AopProxyUtils.ultimateTargetClass(proxy));
        ProxyFactory pf2 = new ProxyFactory(proxy);
        Object proxy2 = pf2.getProxy();
        Assert.assertTrue("Proxy is a JDK proxy", AopUtils.isJdkDynamicProxy(proxy2));
        Assert.assertTrue((proxy2 instanceof ITestBean));
        Assert.assertEquals(ITestBean.class, AopProxyUtils.ultimateTargetClass(proxy2));
    }

    @Test
    public void testProxyTargetClassWithConcreteClassAsTarget() {
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetClass(TestBean.class);
        Object proxy = pf.getProxy();
        Assert.assertTrue("Proxy is a CGLIB proxy", AopUtils.isCglibProxy(proxy));
        Assert.assertTrue((proxy instanceof TestBean));
        Assert.assertEquals(TestBean.class, AopProxyUtils.ultimateTargetClass(proxy));
        ProxyFactory pf2 = new ProxyFactory(proxy);
        pf2.setProxyTargetClass(true);
        Object proxy2 = pf2.getProxy();
        Assert.assertTrue("Proxy is a CGLIB proxy", AopUtils.isCglibProxy(proxy2));
        Assert.assertTrue((proxy2 instanceof TestBean));
        Assert.assertEquals(TestBean.class, AopProxyUtils.ultimateTargetClass(proxy2));
    }

    @Test
    public void testInterfaceProxiesCanBeOrderedThroughAnnotations() {
        Object proxy1 = new ProxyFactory(new ProxyFactoryTests.A()).getProxy();
        Object proxy2 = new ProxyFactory(new ProxyFactoryTests.B()).getProxy();
        List<Object> list = new ArrayList<>(2);
        list.add(proxy1);
        list.add(proxy2);
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertSame(proxy2, list.get(0));
        Assert.assertSame(proxy1, list.get(1));
    }

    @Test
    public void testTargetClassProxiesCanBeOrderedThroughAnnotations() {
        ProxyFactory pf1 = new ProxyFactory(new ProxyFactoryTests.A());
        pf1.setProxyTargetClass(true);
        ProxyFactory pf2 = new ProxyFactory(new ProxyFactoryTests.B());
        pf2.setProxyTargetClass(true);
        Object proxy1 = pf1.getProxy();
        Object proxy2 = pf2.getProxy();
        List<Object> list = new ArrayList<>(2);
        list.add(proxy1);
        list.add(proxy2);
        AnnotationAwareOrderComparator.sort(list);
        Assert.assertSame(proxy2, list.get(0));
        Assert.assertSame(proxy1, list.get(1));
    }

    @Test
    public void testInterceptorWithoutJoinpoint() {
        final TestBean target = new TestBean("tb");
        ITestBean proxy = ProxyFactory.getProxy(ITestBean.class, ((MethodInterceptor) (( invocation) -> {
            assertNull(invocation.getThis());
            return invocation.getMethod().invoke(target, invocation.getArguments());
        })));
        Assert.assertEquals("tb", proxy.getName());
    }

    @SuppressWarnings("serial")
    private static class TimestampIntroductionInterceptor extends DelegatingIntroductionInterceptor implements TimeStamped {
        private long ts;

        public TimestampIntroductionInterceptor() {
        }

        public TimestampIntroductionInterceptor(long ts) {
            this.ts = ts;
        }

        public void setTime(long ts) {
            this.ts = ts;
        }

        @Override
        public long getTimeStamp() {
            return ts;
        }
    }

    @Order(2)
    public static class A implements Runnable {
        @Override
        public void run() {
        }
    }

    @Order(1)
    public static class B implements Runnable {
        @Override
        public void run() {
        }
    }
}

