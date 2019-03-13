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
package org.springframework.aop.aspectj.annotation;


import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.SerializationTestUtils;
import test.aop.PerThisAspect;


/**
 *
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class AspectProxyFactoryTests {
    @Test(expected = IllegalArgumentException.class)
    public void testWithNonAspect() {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new AspectProxyFactoryTests.TestBean());
        proxyFactory.addAspect(AspectProxyFactoryTests.TestBean.class);
    }

    @Test
    public void testWithSimpleAspect() throws Exception {
        AspectProxyFactoryTests.TestBean bean = new AspectProxyFactoryTests.TestBean();
        bean.setAge(2);
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(bean);
        proxyFactory.addAspect(MultiplyReturnValue.class);
        AspectProxyFactoryTests.ITestBean proxy = proxyFactory.getProxy();
        Assert.assertEquals("Multiplication did not occur", ((bean.getAge()) * 2), proxy.getAge());
    }

    @Test
    public void testWithPerThisAspect() throws Exception {
        AspectProxyFactoryTests.TestBean bean1 = new AspectProxyFactoryTests.TestBean();
        AspectProxyFactoryTests.TestBean bean2 = new AspectProxyFactoryTests.TestBean();
        AspectJProxyFactory pf1 = new AspectJProxyFactory(bean1);
        pf1.addAspect(PerThisAspect.class);
        AspectJProxyFactory pf2 = new AspectJProxyFactory(bean2);
        pf2.addAspect(PerThisAspect.class);
        AspectProxyFactoryTests.ITestBean proxy1 = pf1.getProxy();
        AspectProxyFactoryTests.ITestBean proxy2 = pf2.getProxy();
        Assert.assertEquals(0, proxy1.getAge());
        Assert.assertEquals(1, proxy1.getAge());
        Assert.assertEquals(0, proxy2.getAge());
        Assert.assertEquals(2, proxy1.getAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithInstanceWithNonAspect() throws Exception {
        AspectJProxyFactory pf = new AspectJProxyFactory();
        pf.addAspect(new AspectProxyFactoryTests.TestBean());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSerializable() throws Exception {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new AspectProxyFactoryTests.TestBean());
        proxyFactory.addAspect(AspectProxyFactoryTests.LoggingAspectOnVarargs.class);
        AspectProxyFactoryTests.ITestBean proxy = proxyFactory.getProxy();
        Assert.assertTrue(proxy.doWithVarargs(AspectProxyFactoryTests.MyEnum.A, AspectProxyFactoryTests.MyOtherEnum.C));
        AspectProxyFactoryTests.ITestBean tb = ((AspectProxyFactoryTests.ITestBean) (SerializationTestUtils.serializeAndDeserialize(proxy)));
        Assert.assertTrue(tb.doWithVarargs(AspectProxyFactoryTests.MyEnum.A, AspectProxyFactoryTests.MyOtherEnum.C));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWithInstance() throws Exception {
        MultiplyReturnValue aspect = new MultiplyReturnValue();
        int multiple = 3;
        aspect.setMultiple(multiple);
        AspectProxyFactoryTests.TestBean target = new AspectProxyFactoryTests.TestBean();
        target.setAge(24);
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(target);
        proxyFactory.addAspect(aspect);
        AspectProxyFactoryTests.ITestBean proxy = proxyFactory.getProxy();
        Assert.assertEquals(((target.getAge()) * multiple), proxy.getAge());
        AspectProxyFactoryTests.ITestBean serializedProxy = ((AspectProxyFactoryTests.ITestBean) (SerializationTestUtils.serializeAndDeserialize(proxy)));
        Assert.assertEquals(((target.getAge()) * multiple), serializedProxy.getAge());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithNonSingletonAspectInstance() throws Exception {
        AspectJProxyFactory pf = new AspectJProxyFactory();
        pf.addAspect(new PerThisAspect());
    }

    // SPR-13328
    @Test
    @SuppressWarnings("unchecked")
    public void testProxiedVarargsWithEnumArray() throws Exception {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new AspectProxyFactoryTests.TestBean());
        proxyFactory.addAspect(AspectProxyFactoryTests.LoggingAspectOnVarargs.class);
        AspectProxyFactoryTests.ITestBean proxy = proxyFactory.getProxy();
        Assert.assertTrue(proxy.doWithVarargs(AspectProxyFactoryTests.MyEnum.A, AspectProxyFactoryTests.MyOtherEnum.C));
    }

    // SPR-13328
    @Test
    @SuppressWarnings("unchecked")
    public void testUnproxiedVarargsWithEnumArray() throws Exception {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(new AspectProxyFactoryTests.TestBean());
        proxyFactory.addAspect(AspectProxyFactoryTests.LoggingAspectOnSetter.class);
        AspectProxyFactoryTests.ITestBean proxy = proxyFactory.getProxy();
        Assert.assertTrue(proxy.doWithVarargs(AspectProxyFactoryTests.MyEnum.A, AspectProxyFactoryTests.MyOtherEnum.C));
    }

    public interface ITestBean {
        int getAge();

        @SuppressWarnings("unchecked")
        <V extends AspectProxyFactoryTests.MyInterface> boolean doWithVarargs(V... args);
    }

    @SuppressWarnings("serial")
    public static class TestBean implements Serializable , AspectProxyFactoryTests.ITestBean {
        private int age;

        @Override
        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <V extends AspectProxyFactoryTests.MyInterface> boolean doWithVarargs(V... args) {
            return true;
        }
    }

    public interface MyInterface {}

    public enum MyEnum implements AspectProxyFactoryTests.MyInterface {

        A,
        B;}

    public enum MyOtherEnum implements AspectProxyFactoryTests.MyInterface {

        C,
        D;}

    @Aspect
    @SuppressWarnings("serial")
    public static class LoggingAspectOnVarargs implements Serializable {
        @Around("execution(* doWithVarargs(*))")
        public Object doLog(ProceedingJoinPoint pjp) throws Throwable {
            LogFactory.getLog(AspectProxyFactoryTests.LoggingAspectOnVarargs.class).debug(Arrays.asList(pjp.getArgs()));
            return pjp.proceed();
        }
    }

    @Aspect
    public static class LoggingAspectOnSetter {
        @Around("execution(* setAge(*))")
        public Object doLog(ProceedingJoinPoint pjp) throws Throwable {
            LogFactory.getLog(AspectProxyFactoryTests.LoggingAspectOnSetter.class).debug(Arrays.asList(pjp.getArgs()));
            return pjp.proceed();
        }
    }
}

