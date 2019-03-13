/**
 * Copyright 2002-2019 the original author or authors.
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


import ExposeInvocationInterceptor.INSTANCE;
import java.io.Serializable;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.tests.sample.beans.IOther;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.TestBean;


/**
 *
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13.03.2003
 */
@SuppressWarnings("serial")
public class JdkDynamicProxyTests extends AbstractAopProxyTests implements Serializable {
    @Test(expected = IllegalArgumentException.class)
    public void testNullConfig() {
        new JdkDynamicAopProxy(null);
    }

    @Test
    public void testProxyIsJustInterface() {
        TestBean raw = new TestBean();
        raw.setAge(32);
        AdvisedSupport pc = new AdvisedSupport(ITestBean.class);
        pc.setTarget(raw);
        JdkDynamicAopProxy aop = new JdkDynamicAopProxy(pc);
        Object proxy = aop.getProxy();
        Assert.assertTrue((proxy instanceof ITestBean));
        Assert.assertFalse((proxy instanceof TestBean));
    }

    @Test
    public void testInterceptorIsInvokedWithNoTarget() {
        // Test return value
        final int age = 25;
        MethodInterceptor mi = ( invocation) -> age;
        AdvisedSupport pc = new AdvisedSupport(ITestBean.class);
        pc.addAdvice(mi);
        AopProxy aop = createAopProxy(pc);
        ITestBean tb = ((ITestBean) (aop.getProxy()));
        Assert.assertEquals("correct return value", age, tb.getAge());
    }

    @Test
    public void testTargetCanGetInvocationWithPrivateClass() {
        final AbstractAopProxyTests.ExposedInvocationTestBean expectedTarget = new AbstractAopProxyTests.ExposedInvocationTestBean() {
            @Override
            protected void assertions(MethodInvocation invocation) {
                Assert.assertEquals(this, invocation.getThis());
                Assert.assertEquals(("Invocation should be on ITestBean: " + (invocation.getMethod())), ITestBean.class, invocation.getMethod().getDeclaringClass());
            }
        };
        AdvisedSupport pc = new AdvisedSupport(ITestBean.class, IOther.class);
        pc.addAdvice(INSTANCE);
        AbstractAopProxyTests.TrapTargetInterceptor tii = new AbstractAopProxyTests.TrapTargetInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                // Assert that target matches BEFORE invocation returns
                Assert.assertEquals("Target is correct", expectedTarget, invocation.getThis());
                return super.invoke(invocation);
            }
        };
        pc.addAdvice(tii);
        pc.setTarget(expectedTarget);
        AopProxy aop = createAopProxy(pc);
        ITestBean tb = ((ITestBean) (aop.getProxy()));
        tb.getName();
    }

    @Test
    public void testProxyNotWrappedIfIncompatible() {
        JdkDynamicProxyTests.FooBar bean = new JdkDynamicProxyTests.FooBar();
        ProxyCreatorSupport as = new ProxyCreatorSupport();
        as.setInterfaces(JdkDynamicProxyTests.Foo.class);
        as.setTarget(bean);
        JdkDynamicProxyTests.Foo proxy = ((JdkDynamicProxyTests.Foo) (createProxy(as)));
        Assert.assertSame("Target should be returned when return types are incompatible", bean, proxy.getBarThis());
        Assert.assertSame("Proxy should be returned when return types are compatible", proxy, proxy.getFooThis());
    }

    @Test
    public void testEqualsAndHashCodeDefined() {
        AdvisedSupport as = new AdvisedSupport(JdkDynamicProxyTests.Named.class);
        as.setTarget(new JdkDynamicProxyTests.Person());
        JdkDynamicAopProxy aopProxy = new JdkDynamicAopProxy(as);
        JdkDynamicProxyTests.Named proxy = ((JdkDynamicProxyTests.Named) (aopProxy.getProxy()));
        JdkDynamicProxyTests.Named named = new JdkDynamicProxyTests.Person();
        Assert.assertEquals("equals()", proxy, named);
        Assert.assertEquals("hashCode()", proxy.hashCode(), named.hashCode());
    }

    // SPR-13328
    @Test
    public void testVarargsWithEnumArray() {
        ProxyFactory proxyFactory = new ProxyFactory(new JdkDynamicProxyTests.VarargTestBean());
        JdkDynamicProxyTests.VarargTestInterface proxy = ((JdkDynamicProxyTests.VarargTestInterface) (proxyFactory.getProxy()));
        Assert.assertTrue(proxy.doWithVarargs(JdkDynamicProxyTests.MyEnum.A, JdkDynamicProxyTests.MyOtherEnum.C));
    }

    public interface Foo {
        JdkDynamicProxyTests.Bar getBarThis();

        JdkDynamicProxyTests.Foo getFooThis();
    }

    public interface Bar {}

    public static class FooBar implements JdkDynamicProxyTests.Bar , JdkDynamicProxyTests.Foo {
        @Override
        public JdkDynamicProxyTests.Bar getBarThis() {
            return this;
        }

        @Override
        public JdkDynamicProxyTests.Foo getFooThis() {
            return this;
        }
    }

    public interface Named {
        String getName();

        @Override
        boolean equals(Object other);

        @Override
        int hashCode();
    }

    public static class Person implements JdkDynamicProxyTests.Named {
        private final String name = "Rob Harrop";

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if ((o == null) || ((getClass()) != (o.getClass())))
                return false;

            JdkDynamicProxyTests.Person person = ((JdkDynamicProxyTests.Person) (o));
            if (!(name.equals(person.name)))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public interface VarargTestInterface {
        <V extends JdkDynamicProxyTests.MyInterface> boolean doWithVarargs(V... args);
    }

    public static class VarargTestBean implements JdkDynamicProxyTests.VarargTestInterface {
        @SuppressWarnings("unchecked")
        @Override
        public <V extends JdkDynamicProxyTests.MyInterface> boolean doWithVarargs(V... args) {
            return true;
        }
    }

    public interface MyInterface {}

    public enum MyEnum implements JdkDynamicProxyTests.MyInterface {

        A,
        B;}

    public enum MyOtherEnum implements JdkDynamicProxyTests.MyInterface {

        C,
        D;}
}

