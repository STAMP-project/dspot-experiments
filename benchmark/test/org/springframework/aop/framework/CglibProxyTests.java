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


import java.io.Serializable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.tests.aop.advice.CountingBeforeAdvice;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Additional and overridden tests for CGLIB proxies.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Ramnivas Laddad
 * @author Chris Beams
 */
@SuppressWarnings("serial")
public class CglibProxyTests extends AbstractAopProxyTests implements Serializable {
    private static final String DEPENDENCY_CHECK_CONTEXT = (CglibProxyTests.class.getSimpleName()) + "-with-dependency-checking.xml";

    @Test(expected = IllegalArgumentException.class)
    public void testNullConfig() {
        new CglibAopProxy(null);
    }

    @Test(expected = AopConfigException.class)
    public void testNoTarget() {
        AdvisedSupport pc = new AdvisedSupport(ITestBean.class);
        pc.addAdvice(new NopInterceptor());
        AopProxy aop = createAopProxy(pc);
        aop.getProxy();
    }

    @Test
    public void testProtectedMethodInvocation() {
        CglibProxyTests.ProtectedMethodTestBean bean = new CglibProxyTests.ProtectedMethodTestBean();
        bean.value = "foo";
        mockTargetSource.setTarget(bean);
        AdvisedSupport as = new AdvisedSupport();
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        AopProxy aop = new CglibAopProxy(as);
        CglibProxyTests.ProtectedMethodTestBean proxy = ((CglibProxyTests.ProtectedMethodTestBean) (aop.getProxy()));
        Assert.assertTrue(AopUtils.isCglibProxy(proxy));
        Assert.assertEquals(proxy.getClass().getClassLoader(), bean.getClass().getClassLoader());
        Assert.assertEquals("foo", proxy.getString());
    }

    @Test
    public void testPackageMethodInvocation() {
        CglibProxyTests.PackageMethodTestBean bean = new CglibProxyTests.PackageMethodTestBean();
        bean.value = "foo";
        mockTargetSource.setTarget(bean);
        AdvisedSupport as = new AdvisedSupport();
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        AopProxy aop = new CglibAopProxy(as);
        CglibProxyTests.PackageMethodTestBean proxy = ((CglibProxyTests.PackageMethodTestBean) (aop.getProxy()));
        Assert.assertTrue(AopUtils.isCglibProxy(proxy));
        Assert.assertEquals(proxy.getClass().getClassLoader(), bean.getClass().getClassLoader());
        Assert.assertEquals("foo", proxy.getString());
    }

    @Test
    public void testProxyCanBeClassNotInterface() {
        TestBean raw = new TestBean();
        raw.setAge(32);
        mockTargetSource.setTarget(raw);
        AdvisedSupport pc = new AdvisedSupport();
        pc.setTargetSource(mockTargetSource);
        AopProxy aop = new CglibAopProxy(pc);
        Object proxy = aop.getProxy();
        Assert.assertTrue(AopUtils.isCglibProxy(proxy));
        Assert.assertTrue((proxy instanceof ITestBean));
        Assert.assertTrue((proxy instanceof TestBean));
        TestBean tb = ((TestBean) (proxy));
        Assert.assertEquals(32, tb.getAge());
    }

    @Test
    public void testMethodInvocationDuringConstructor() {
        CglibTestBean bean = new CglibTestBean();
        bean.setName("Rob Harrop");
        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(bean);
        as.addAdvice(new NopInterceptor());
        AopProxy aop = new CglibAopProxy(as);
        CglibTestBean proxy = ((CglibTestBean) (aop.getProxy()));
        Assert.assertEquals("The name property has been overwritten by the constructor", "Rob Harrop", proxy.getName());
    }

    @Test
    public void testToStringInvocation() {
        CglibProxyTests.PrivateCglibTestBean bean = new CglibProxyTests.PrivateCglibTestBean();
        bean.setName("Rob Harrop");
        AdvisedSupport as = new AdvisedSupport();
        as.setTarget(bean);
        as.addAdvice(new NopInterceptor());
        AopProxy aop = new CglibAopProxy(as);
        CglibProxyTests.PrivateCglibTestBean proxy = ((CglibProxyTests.PrivateCglibTestBean) (aop.getProxy()));
        Assert.assertEquals("The name property has been overwritten by the constructor", "Rob Harrop", proxy.toString());
    }

    @Test
    public void testUnadvisedProxyCreationWithCallDuringConstructor() {
        CglibTestBean target = new CglibTestBean();
        target.setName("Rob Harrop");
        AdvisedSupport pc = new AdvisedSupport();
        pc.setFrozen(true);
        pc.setTarget(target);
        CglibAopProxy aop = new CglibAopProxy(pc);
        CglibTestBean proxy = ((CglibTestBean) (aop.getProxy()));
        Assert.assertNotNull("Proxy should not be null", proxy);
        Assert.assertEquals("Constructor overrode the value of name", "Rob Harrop", proxy.getName());
    }

    @Test
    public void testMultipleProxies() {
        TestBean target = new TestBean();
        target.setAge(20);
        TestBean target2 = new TestBean();
        target2.setAge(21);
        ITestBean proxy1 = getAdvisedProxy(target);
        ITestBean proxy2 = getAdvisedProxy(target2);
        Assert.assertSame(proxy1.getClass(), proxy2.getClass());
        Assert.assertEquals(target.getAge(), proxy1.getAge());
        Assert.assertEquals(target2.getAge(), proxy2.getAge());
    }

    @Test
    public void testMultipleProxiesForIntroductionAdvisor() {
        TestBean target1 = new TestBean();
        target1.setAge(20);
        TestBean target2 = new TestBean();
        target2.setAge(21);
        ITestBean proxy1 = getIntroductionAdvisorProxy(target1);
        ITestBean proxy2 = getIntroductionAdvisorProxy(target2);
        Assert.assertSame("Incorrect duplicate creation of proxy classes", proxy1.getClass(), proxy2.getClass());
    }

    @Test
    public void testWithNoArgConstructor() {
        CglibProxyTests.NoArgCtorTestBean target = new CglibProxyTests.NoArgCtorTestBean("b", 1);
        target.reset();
        mockTargetSource.setTarget(target);
        AdvisedSupport pc = new AdvisedSupport();
        pc.setTargetSource(mockTargetSource);
        CglibAopProxy aop = new CglibAopProxy(pc);
        aop.setConstructorArguments(new Object[]{ "Rob Harrop", 22 }, new Class<?>[]{ String.class, int.class });
        CglibProxyTests.NoArgCtorTestBean proxy = ((CglibProxyTests.NoArgCtorTestBean) (aop.getProxy()));
        Assert.assertNotNull(proxy);
    }

    @Test
    public void testProxyAProxy() {
        ITestBean target = new TestBean();
        mockTargetSource.setTarget(target);
        AdvisedSupport as = new AdvisedSupport();
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        CglibAopProxy cglib = new CglibAopProxy(as);
        ITestBean proxy1 = ((ITestBean) (cglib.getProxy()));
        mockTargetSource.setTarget(proxy1);
        as = new AdvisedSupport(new Class<?>[]{  });
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        cglib = new CglibAopProxy(as);
        Assert.assertThat(cglib.getProxy(), CoreMatchers.instanceOf(ITestBean.class));
    }

    @Test
    public void testProxyAProxyWithAdditionalInterface() {
        ITestBean target = new TestBean();
        mockTargetSource.setTarget(target);
        AdvisedSupport as = new AdvisedSupport();
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        as.addInterface(Serializable.class);
        CglibAopProxy cglib = new CglibAopProxy(as);
        ITestBean proxy1 = ((ITestBean) (cglib.getProxy()));
        mockTargetSource.setTarget(proxy1);
        as = new AdvisedSupport(new Class<?>[]{  });
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        cglib = new CglibAopProxy(as);
        ITestBean proxy2 = ((ITestBean) (cglib.getProxy()));
        Assert.assertTrue((proxy2 instanceof Serializable));
    }

    @Test
    public void testExceptionHandling() {
        CglibProxyTests.ExceptionThrower bean = new CglibProxyTests.ExceptionThrower();
        mockTargetSource.setTarget(bean);
        AdvisedSupport as = new AdvisedSupport();
        as.setTargetSource(mockTargetSource);
        as.addAdvice(new NopInterceptor());
        AopProxy aop = new CglibAopProxy(as);
        CglibProxyTests.ExceptionThrower proxy = ((CglibProxyTests.ExceptionThrower) (aop.getProxy()));
        try {
            proxy.doTest();
        } catch (Exception ex) {
            Assert.assertTrue("Invalid exception class", (ex instanceof ApplicationContextException));
        }
        Assert.assertTrue("Catch was not invoked", proxy.isCatchInvoked());
        Assert.assertTrue("Finally was not invoked", proxy.isFinallyInvoked());
    }

    @Test
    @SuppressWarnings("resource")
    public void testWithDependencyChecking() {
        ApplicationContext ctx = new ClassPathXmlApplicationContext(CglibProxyTests.DEPENDENCY_CHECK_CONTEXT, getClass());
        ctx.getBean("testBean");
    }

    @Test
    public void testAddAdviceAtRuntime() {
        TestBean bean = new TestBean();
        CountingBeforeAdvice cba = new CountingBeforeAdvice();
        ProxyFactory pf = new ProxyFactory();
        pf.setTarget(bean);
        pf.setFrozen(false);
        pf.setOpaque(false);
        pf.setProxyTargetClass(true);
        TestBean proxy = ((TestBean) (pf.getProxy()));
        Assert.assertTrue(AopUtils.isCglibProxy(proxy));
        proxy.getAge();
        Assert.assertEquals(0, cba.getCalls());
        ((Advised) (proxy)).addAdvice(cba);
        proxy.getAge();
        Assert.assertEquals(1, cba.getCalls());
    }

    @Test
    public void testProxyProtectedMethod() {
        CountingBeforeAdvice advice = new CountingBeforeAdvice();
        ProxyFactory proxyFactory = new ProxyFactory(new CglibProxyTests.MyBean());
        proxyFactory.addAdvice(advice);
        proxyFactory.setProxyTargetClass(true);
        CglibProxyTests.MyBean proxy = ((CglibProxyTests.MyBean) (proxyFactory.getProxy()));
        Assert.assertEquals(4, proxy.add(1, 3));
        Assert.assertEquals(1, advice.getCalls("add"));
    }

    @Test
    public void testProxyTargetClassInCaseOfNoInterfaces() {
        ProxyFactory proxyFactory = new ProxyFactory(new CglibProxyTests.MyBean());
        CglibProxyTests.MyBean proxy = ((CglibProxyTests.MyBean) (proxyFactory.getProxy()));
        Assert.assertEquals(4, proxy.add(1, 3));
    }

    // SPR-13328
    @Test
    public void testVarargsWithEnumArray() {
        ProxyFactory proxyFactory = new ProxyFactory(new CglibProxyTests.MyBean());
        CglibProxyTests.MyBean proxy = ((CglibProxyTests.MyBean) (proxyFactory.getProxy()));
        Assert.assertTrue(proxy.doWithVarargs(CglibProxyTests.MyEnum.A, CglibProxyTests.MyOtherEnum.C));
    }

    public static class MyBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        protected int add(int x, int y) {
            return x + y;
        }

        @SuppressWarnings("unchecked")
        public <V extends CglibProxyTests.MyInterface> boolean doWithVarargs(V... args) {
            return true;
        }
    }

    public interface MyInterface {}

    public enum MyEnum implements CglibProxyTests.MyInterface {

        A,
        B;}

    public enum MyOtherEnum implements CglibProxyTests.MyInterface {

        C,
        D;}

    public static class ExceptionThrower {
        private boolean catchInvoked;

        private boolean finallyInvoked;

        public boolean isCatchInvoked() {
            return catchInvoked;
        }

        public boolean isFinallyInvoked() {
            return finallyInvoked;
        }

        public void doTest() throws Exception {
            try {
                throw new ApplicationContextException("foo");
            } catch (Exception ex) {
                catchInvoked = true;
                throw ex;
            } finally {
                finallyInvoked = true;
            }
        }
    }

    public static class NoArgCtorTestBean {
        private boolean called = false;

        public NoArgCtorTestBean(String x, int y) {
            called = true;
        }

        public boolean wasCalled() {
            return called;
        }

        public void reset() {
            called = false;
        }
    }

    public static class ProtectedMethodTestBean {
        public String value;

        protected String getString() {
            return this.value;
        }
    }

    public static class PackageMethodTestBean {
        public String value;

        String getString() {
            return this.value;
        }
    }

    private static class PrivateCglibTestBean {
        private String name;

        public PrivateCglibTestBean() {
            setName("Some Default");
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}

