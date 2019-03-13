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
package org.springframework.aop.framework;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.List;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.IntroductionAdvisor;
import org.springframework.aop.IntroductionInterceptor;
import org.springframework.aop.interceptor.DebugInterceptor;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.DynamicMethodMatcherPointcut;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.tests.TimeStamped;
import org.springframework.tests.aop.advice.CountingBeforeAdvice;
import org.springframework.tests.aop.advice.MyThrowsHandler;
import org.springframework.tests.aop.interceptor.NopInterceptor;
import org.springframework.tests.aop.interceptor.TimestampIntroductionInterceptor;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.Person;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.SerializationTestUtils;
import test.mixin.Lockable;
import test.mixin.LockedException;


/**
 *
 *
 * @since 13.03.2003
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class ProxyFactoryBeanTests {
    private static final Class<?> CLASS = ProxyFactoryBeanTests.class;

    private static final String CLASSNAME = ProxyFactoryBeanTests.CLASS.getSimpleName();

    private static final String CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-context.xml";

    private static final String SERIALIZATION_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-serialization.xml";

    private static final String AUTOWIRING_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-autowiring.xml";

    private static final String DBL_TARGETSOURCE_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-double-targetsource.xml";

    private static final String NOTLAST_TARGETSOURCE_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-notlast-targetsource.xml";

    private static final String TARGETSOURCE_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-targetsource.xml";

    private static final String INVALID_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-invalid.xml";

    private static final String FROZEN_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-frozen.xml";

    private static final String PROTOTYPE_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-prototype.xml";

    private static final String THROWS_ADVICE_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-throws-advice.xml";

    private static final String INNER_BEAN_TARGET_CONTEXT = (ProxyFactoryBeanTests.CLASSNAME) + "-inner-bean-target.xml";

    private BeanFactory factory;

    @Test
    public void testIsDynamicProxyWhenInterfaceSpecified() {
        ITestBean test1 = ((ITestBean) (factory.getBean("test1")));
        Assert.assertTrue("test1 is a dynamic proxy", Proxy.isProxyClass(test1.getClass()));
    }

    @Test
    public void testIsDynamicProxyWhenInterfaceSpecifiedForPrototype() {
        ITestBean test1 = ((ITestBean) (factory.getBean("test2")));
        Assert.assertTrue("test2 is a dynamic proxy", Proxy.isProxyClass(test1.getClass()));
    }

    @Test
    public void testIsDynamicProxyWhenAutodetectingInterfaces() {
        ITestBean test1 = ((ITestBean) (factory.getBean("test3")));
        Assert.assertTrue("test3 is a dynamic proxy", Proxy.isProxyClass(test1.getClass()));
    }

    @Test
    public void testIsDynamicProxyWhenAutodetectingInterfacesForPrototype() {
        ITestBean test1 = ((ITestBean) (factory.getBean("test4")));
        Assert.assertTrue("test4 is a dynamic proxy", Proxy.isProxyClass(test1.getClass()));
    }

    /**
     * Test that it's forbidden to specify TargetSource in both
     * interceptor chain and targetSource property.
     */
    @Test
    public void testDoubleTargetSourcesAreRejected() {
        testDoubleTargetSourceIsRejected("doubleTarget");
        // Now with conversion from arbitrary bean to a TargetSource
        testDoubleTargetSourceIsRejected("arbitraryTarget");
    }

    @Test
    public void testTargetSourceNotAtEndOfInterceptorNamesIsRejected() {
        try {
            DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
            new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.NOTLAST_TARGETSOURCE_CONTEXT, ProxyFactoryBeanTests.CLASS));
            bf.getBean("targetSourceNotLast");
            Assert.fail("TargetSource or non-advised object must be last in interceptorNames");
        } catch (BeanCreationException ex) {
            // Root cause of the problem must be an AOP exception
            AopConfigException aex = ((AopConfigException) (ex.getCause()));
            Assert.assertTrue(aex.getMessage().contains("interceptorNames"));
        }
    }

    @Test
    public void testGetObjectTypeWithDirectTarget() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.TARGETSOURCE_CONTEXT, ProxyFactoryBeanTests.CLASS));
        // We have a counting before advice here
        CountingBeforeAdvice cba = ((CountingBeforeAdvice) (bf.getBean("countingBeforeAdvice")));
        Assert.assertEquals(0, cba.getCalls());
        ITestBean tb = ((ITestBean) (bf.getBean("directTarget")));
        Assert.assertTrue(tb.getName().equals("Adam"));
        Assert.assertEquals(1, cba.getCalls());
        ProxyFactoryBean pfb = ((ProxyFactoryBean) (bf.getBean("&directTarget")));
        Assert.assertTrue("Has correct object type", TestBean.class.isAssignableFrom(pfb.getObjectType()));
    }

    @Test
    public void testGetObjectTypeWithTargetViaTargetSource() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.TARGETSOURCE_CONTEXT, ProxyFactoryBeanTests.CLASS));
        ITestBean tb = ((ITestBean) (bf.getBean("viaTargetSource")));
        Assert.assertTrue(tb.getName().equals("Adam"));
        ProxyFactoryBean pfb = ((ProxyFactoryBean) (bf.getBean("&viaTargetSource")));
        Assert.assertTrue("Has correct object type", TestBean.class.isAssignableFrom(pfb.getObjectType()));
    }

    @Test
    public void testGetObjectTypeWithNoTargetOrTargetSource() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.TARGETSOURCE_CONTEXT, ProxyFactoryBeanTests.CLASS));
        ITestBean tb = ((ITestBean) (bf.getBean("noTarget")));
        try {
            tb.getName();
            Assert.fail();
        } catch (UnsupportedOperationException ex) {
            Assert.assertEquals("getName", ex.getMessage());
        }
        FactoryBean<?> pfb = ((ProxyFactoryBean) (bf.getBean("&noTarget")));
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(pfb.getObjectType()));
    }

    /**
     * The instances are equal, but do not have object identity.
     * Interceptors and interfaces and the target are the same.
     */
    @Test
    public void testSingletonInstancesAreEqual() {
        ITestBean test1 = ((ITestBean) (factory.getBean("test1")));
        ITestBean test1_1 = ((ITestBean) (factory.getBean("test1")));
        // assertTrue("Singleton instances ==", test1 == test1_1);
        Assert.assertEquals("Singleton instances ==", test1, test1_1);
        test1.setAge(25);
        Assert.assertEquals(test1.getAge(), test1_1.getAge());
        test1.setAge(250);
        Assert.assertEquals(test1.getAge(), test1_1.getAge());
        Advised pc1 = ((Advised) (test1));
        Advised pc2 = ((Advised) (test1_1));
        Assert.assertArrayEquals(pc1.getAdvisors(), pc2.getAdvisors());
        int oldLength = pc1.getAdvisors().length;
        NopInterceptor di = new NopInterceptor();
        pc1.addAdvice(1, di);
        Assert.assertArrayEquals(pc1.getAdvisors(), pc2.getAdvisors());
        Assert.assertEquals("Now have one more advisor", (oldLength + 1), pc2.getAdvisors().length);
        Assert.assertEquals(di.getCount(), 0);
        test1.setAge(5);
        Assert.assertEquals(test1_1.getAge(), test1.getAge());
        Assert.assertEquals(di.getCount(), 3);
    }

    @Test
    public void testPrototypeInstancesAreNotEqual() {
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(factory.getType("prototype")));
        ITestBean test2 = ((ITestBean) (factory.getBean("prototype")));
        ITestBean test2_1 = ((ITestBean) (factory.getBean("prototype")));
        Assert.assertTrue("Prototype instances !=", (test2 != test2_1));
        Assert.assertTrue("Prototype instances equal", test2.equals(test2_1));
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(factory.getType("prototype")));
    }

    @Test
    public void testCglibPrototypeInstance() {
        Object prototype = testPrototypeInstancesAreIndependent("cglibPrototype");
        Assert.assertTrue("It's a cglib proxy", AopUtils.isCglibProxy(prototype));
        Assert.assertFalse("It's not a dynamic proxy", AopUtils.isJdkDynamicProxy(prototype));
    }

    /**
     * Test invoker is automatically added to manipulate target.
     */
    @Test
    public void testAutoInvoker() {
        String name = "Hieronymous";
        TestBean target = ((TestBean) (factory.getBean("test")));
        target.setName(name);
        ITestBean autoInvoker = ((ITestBean) (factory.getBean("autoInvoker")));
        Assert.assertTrue(autoInvoker.getName().equals(name));
    }

    @Test
    public void testCanGetFactoryReferenceAndManipulate() {
        ProxyFactoryBean config = ((ProxyFactoryBean) (factory.getBean("&test1")));
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(config.getObjectType()));
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(factory.getType("test1")));
        // Trigger lazy initialization.
        config.getObject();
        Assert.assertEquals("Have one advisors", 1, config.getAdvisors().length);
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(config.getObjectType()));
        Assert.assertTrue("Has correct object type", ITestBean.class.isAssignableFrom(factory.getType("test1")));
        ITestBean tb = ((ITestBean) (factory.getBean("test1")));
        // no exception
        tb.hashCode();
        final Exception ex = new UnsupportedOperationException("invoke");
        // Add evil interceptor to head of list
        config.addAdvice(0, new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                throw ex;
            }
        });
        Assert.assertEquals("Have correct advisor count", 2, config.getAdvisors().length);
        tb = ((ITestBean) (factory.getBean("test1")));
        try {
            // Will fail now
            tb.toString();
            Assert.fail("Evil interceptor added programmatically should fail all method calls");
        } catch (Exception thrown) {
            Assert.assertTrue((thrown == ex));
        }
    }

    /**
     * Test that inner bean for target means that we can use
     * autowire without ambiguity from target and proxy
     */
    @Test
    public void testTargetAsInnerBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.INNER_BEAN_TARGET_CONTEXT, ProxyFactoryBeanTests.CLASS));
        ITestBean itb = ((ITestBean) (bf.getBean("testBean")));
        Assert.assertEquals("innerBeanTarget", itb.getName());
        Assert.assertEquals("Only have proxy and interceptor: no target", 3, bf.getBeanDefinitionCount());
        ProxyFactoryBeanTests.DependsOnITestBean doit = ((ProxyFactoryBeanTests.DependsOnITestBean) (bf.getBean("autowireCheck")));
        Assert.assertSame(itb, doit.tb);
    }

    /**
     * Try adding and removing interfaces and interceptors on prototype.
     * Changes will only affect future references obtained from the factory.
     * Each instance will be independent.
     */
    @Test
    public void testCanAddAndRemoveAspectInterfacesOnPrototype() {
        Assert.assertThat("Shouldn't implement TimeStamped before manipulation", factory.getBean("test2"), CoreMatchers.not(CoreMatchers.instanceOf(TimeStamped.class)));
        ProxyFactoryBean config = ((ProxyFactoryBean) (factory.getBean("&test2")));
        long time = 666L;
        TimestampIntroductionInterceptor ti = new TimestampIntroductionInterceptor();
        ti.setTime(time);
        // Add to head of interceptor chain
        int oldCount = config.getAdvisors().length;
        config.addAdvisor(0, new org.springframework.aop.support.DefaultIntroductionAdvisor(ti, TimeStamped.class));
        Assert.assertTrue(((config.getAdvisors().length) == (oldCount + 1)));
        TimeStamped ts = ((TimeStamped) (factory.getBean("test2")));
        Assert.assertEquals(time, ts.getTimeStamp());
        // Can remove
        config.removeAdvice(ti);
        Assert.assertTrue(((config.getAdvisors().length) == oldCount));
        // Check no change on existing object reference
        Assert.assertTrue(((ts.getTimeStamp()) == time));
        Assert.assertThat("Should no longer implement TimeStamped", factory.getBean("test2"), CoreMatchers.not(CoreMatchers.instanceOf(TimeStamped.class)));
        // Now check non-effect of removing interceptor that isn't there
        config.removeAdvice(new DebugInterceptor());
        Assert.assertTrue(((config.getAdvisors().length) == oldCount));
        ITestBean it = ((ITestBean) (ts));
        DebugInterceptor debugInterceptor = new DebugInterceptor();
        config.addAdvice(0, debugInterceptor);
        it.getSpouse();
        // Won't affect existing reference
        Assert.assertTrue(((debugInterceptor.getCount()) == 0));
        it = ((ITestBean) (factory.getBean("test2")));
        it.getSpouse();
        Assert.assertEquals(1, debugInterceptor.getCount());
        config.removeAdvice(debugInterceptor);
        it.getSpouse();
        // Still invoked with old reference
        Assert.assertEquals(2, debugInterceptor.getCount());
        // not invoked with new object
        it = ((ITestBean) (factory.getBean("test2")));
        it.getSpouse();
        Assert.assertEquals(2, debugInterceptor.getCount());
        // Our own timestamped reference should still work
        Assert.assertEquals(time, ts.getTimeStamp());
    }

    /**
     * Note that we can't add or remove interfaces without reconfiguring the
     * singleton.
     */
    @Test
    public void testCanAddAndRemoveAdvicesOnSingleton() {
        ITestBean it = ((ITestBean) (factory.getBean("test1")));
        Advised pc = ((Advised) (it));
        it.getAge();
        NopInterceptor di = new NopInterceptor();
        pc.addAdvice(0, di);
        Assert.assertEquals(0, di.getCount());
        it.setAge(25);
        Assert.assertEquals(25, it.getAge());
        Assert.assertEquals(2, di.getCount());
    }

    @Test
    public void testMethodPointcuts() {
        ITestBean tb = ((ITestBean) (factory.getBean("pointcuts")));
        ProxyFactoryBeanTests.PointcutForVoid.reset();
        Assert.assertTrue("No methods intercepted", ProxyFactoryBeanTests.PointcutForVoid.methodNames.isEmpty());
        tb.getAge();
        Assert.assertTrue("Not void: shouldn't have intercepted", ProxyFactoryBeanTests.PointcutForVoid.methodNames.isEmpty());
        tb.setAge(1);
        tb.getAge();
        tb.setName("Tristan");
        tb.toString();
        Assert.assertEquals("Recorded wrong number of invocations", 2, ProxyFactoryBeanTests.PointcutForVoid.methodNames.size());
        Assert.assertTrue(ProxyFactoryBeanTests.PointcutForVoid.methodNames.get(0).equals("setAge"));
        Assert.assertTrue(ProxyFactoryBeanTests.PointcutForVoid.methodNames.get(1).equals("setName"));
    }

    @Test
    public void testCanAddThrowsAdviceWithoutAdvisor() throws Throwable {
        DefaultListableBeanFactory f = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(f).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.THROWS_ADVICE_CONTEXT, ProxyFactoryBeanTests.CLASS));
        MyThrowsHandler th = ((MyThrowsHandler) (f.getBean("throwsAdvice")));
        CountingBeforeAdvice cba = ((CountingBeforeAdvice) (f.getBean("countingBeforeAdvice")));
        Assert.assertEquals(0, cba.getCalls());
        Assert.assertEquals(0, th.getCalls());
        IEcho echo = ((IEcho) (f.getBean("throwsAdvised")));
        int i = 12;
        echo.setA(i);
        Assert.assertEquals(i, echo.getA());
        Assert.assertEquals(2, cba.getCalls());
        Assert.assertEquals(0, th.getCalls());
        Exception expected = new Exception();
        try {
            echo.echoException(1, expected);
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertEquals(expected, ex);
        }
        // No throws handler method: count should still be 0
        Assert.assertEquals(0, th.getCalls());
        // Handler knows how to handle this exception
        expected = new FileNotFoundException();
        try {
            echo.echoException(1, expected);
            Assert.fail();
        } catch (IOException ex) {
            Assert.assertEquals(expected, ex);
        }
        // One match
        Assert.assertEquals(1, th.getCalls("ioException"));
    }

    // These two fail the whole bean factory
    // TODO put in sep file to check quality of error message
    /* @Test
    public void testNoInterceptorNamesWithoutTarget() {
    try {
    ITestBean tb = (ITestBean) factory.getBean("noInterceptorNamesWithoutTarget");
    fail("Should require interceptor names");
    }
    catch (AopConfigException ex) {
    // Ok
    }
    }

    @Test
    public void testNoInterceptorNamesWithTarget() {
    ITestBean tb = (ITestBean) factory.getBean("noInterceptorNamesWithoutTarget");
    }
     */
    @Test
    public void testEmptyInterceptorNames() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.INVALID_CONTEXT, ProxyFactoryBeanTests.CLASS));
        try {
            bf.getBean("emptyInterceptorNames");
            Assert.fail("Interceptor names cannot be empty");
        } catch (BeanCreationException ex) {
            // Ok
        }
    }

    /**
     * Globals must be followed by a target.
     */
    @Test
    public void testGlobalsWithoutTarget() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.INVALID_CONTEXT, ProxyFactoryBeanTests.CLASS));
        try {
            bf.getBean("globalsWithoutTarget");
            Assert.fail("Should require target name");
        } catch (BeanCreationException ex) {
            Assert.assertTrue(((ex.getCause()) instanceof AopConfigException));
        }
    }

    /**
     * Checks that globals get invoked,
     * and that they can add aspect interfaces unavailable
     * to other beans. These interfaces don't need
     * to be included in proxiedInterface [].
     */
    @Test
    public void testGlobalsCanAddAspectInterfaces() {
        ProxyFactoryBeanTests.AddedGlobalInterface agi = ((ProxyFactoryBeanTests.AddedGlobalInterface) (factory.getBean("autoInvoker")));
        Assert.assertTrue(((agi.globalsAdded()) == (-1)));
        ProxyFactoryBean pfb = ((ProxyFactoryBean) (factory.getBean("&validGlobals")));
        // Trigger lazy initialization.
        pfb.getObject();
        // 2 globals + 2 explicit
        Assert.assertEquals("Have 2 globals and 2 explicit advisors", 3, pfb.getAdvisors().length);
        ApplicationListener<?> l = ((ApplicationListener<?>) (factory.getBean("validGlobals")));
        agi = ((ProxyFactoryBeanTests.AddedGlobalInterface) (l));
        Assert.assertTrue(((agi.globalsAdded()) == (-1)));
        try {
            agi = ((ProxyFactoryBeanTests.AddedGlobalInterface) (factory.getBean("test1")));
            Assert.fail("Aspect interface should't be implemeneted without globals");
        } catch (ClassCastException ex) {
        }
    }

    @Test
    public void testSerializableSingletonProxy() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.SERIALIZATION_CONTEXT, ProxyFactoryBeanTests.CLASS));
        Person p = ((Person) (bf.getBean("serializableSingleton")));
        Assert.assertSame("Should be a Singleton", p, bf.getBean("serializableSingleton"));
        Person p2 = ((Person) (SerializationTestUtils.serializeAndDeserialize(p)));
        Assert.assertEquals(p, p2);
        Assert.assertNotSame(p, p2);
        Assert.assertEquals("serializableSingleton", p2.getName());
        // Add unserializable advice
        Advice nop = new NopInterceptor();
        ((Advised) (p)).addAdvice(nop);
        // Check it still works
        Assert.assertEquals(p2.getName(), p2.getName());
        Assert.assertFalse("Not serializable because an interceptor isn't serializable", SerializationTestUtils.isSerializable(p));
        // Remove offending interceptor...
        Assert.assertTrue(((Advised) (p)).removeAdvice(nop));
        Assert.assertTrue("Serializable again because offending interceptor was removed", SerializationTestUtils.isSerializable(p));
    }

    @Test
    public void testSerializablePrototypeProxy() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.SERIALIZATION_CONTEXT, ProxyFactoryBeanTests.CLASS));
        Person p = ((Person) (bf.getBean("serializablePrototype")));
        Assert.assertNotSame("Should not be a Singleton", p, bf.getBean("serializablePrototype"));
        Person p2 = ((Person) (SerializationTestUtils.serializeAndDeserialize(p)));
        Assert.assertEquals(p, p2);
        Assert.assertNotSame(p, p2);
        Assert.assertEquals("serializablePrototype", p2.getName());
    }

    @Test
    public void testSerializableSingletonProxyFactoryBean() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.SERIALIZATION_CONTEXT, ProxyFactoryBeanTests.CLASS));
        Person p = ((Person) (bf.getBean("serializableSingleton")));
        ProxyFactoryBean pfb = ((ProxyFactoryBean) (bf.getBean("&serializableSingleton")));
        ProxyFactoryBean pfb2 = ((ProxyFactoryBean) (SerializationTestUtils.serializeAndDeserialize(pfb)));
        Person p2 = ((Person) (pfb2.getObject()));
        Assert.assertEquals(p, p2);
        Assert.assertNotSame(p, p2);
        Assert.assertEquals("serializableSingleton", p2.getName());
    }

    @Test
    public void testProxyNotSerializableBecauseOfAdvice() throws Exception {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.SERIALIZATION_CONTEXT, ProxyFactoryBeanTests.CLASS));
        Person p = ((Person) (bf.getBean("interceptorNotSerializableSingleton")));
        Assert.assertFalse("Not serializable because an interceptor isn't serializable", SerializationTestUtils.isSerializable(p));
    }

    @Test
    public void testPrototypeAdvisor() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.CONTEXT, ProxyFactoryBeanTests.CLASS));
        ITestBean bean1 = ((ITestBean) (bf.getBean("prototypeTestBeanProxy")));
        ITestBean bean2 = ((ITestBean) (bf.getBean("prototypeTestBeanProxy")));
        bean1.setAge(3);
        bean2.setAge(4);
        Assert.assertEquals(3, bean1.getAge());
        Assert.assertEquals(4, bean2.getAge());
        ((Lockable) (bean1)).lock();
        try {
            bean1.setAge(5);
            Assert.fail("expected LockedException");
        } catch (LockedException ex) {
            // expected
        }
        try {
            bean2.setAge(6);
        } catch (LockedException ex) {
            Assert.fail("did not expect LockedException");
        }
    }

    @Test
    public void testPrototypeInterceptorSingletonTarget() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.CONTEXT, ProxyFactoryBeanTests.CLASS));
        ITestBean bean1 = ((ITestBean) (bf.getBean("prototypeTestBeanProxySingletonTarget")));
        ITestBean bean2 = ((ITestBean) (bf.getBean("prototypeTestBeanProxySingletonTarget")));
        bean1.setAge(1);
        bean2.setAge(2);
        Assert.assertEquals(2, bean1.getAge());
        ((Lockable) (bean1)).lock();
        try {
            bean1.setAge(5);
            Assert.fail("expected LockedException");
        } catch (LockedException ex) {
            // expected
        }
        try {
            bean2.setAge(6);
        } catch (LockedException ex) {
            Assert.fail("did not expect LockedException");
        }
    }

    /**
     * Simple test of a ProxyFactoryBean that has an inner bean as target that specifies autowiring.
     * Checks for correct use of getType() by bean factory.
     */
    @Test
    public void testInnerBeanTargetUsingAutowiring() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.AUTOWIRING_CONTEXT, ProxyFactoryBeanTests.CLASS));
        bf.getBean("testBean");
    }

    @Test
    public void testFrozenFactoryBean() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(bf).loadBeanDefinitions(new ClassPathResource(ProxyFactoryBeanTests.FROZEN_CONTEXT, ProxyFactoryBeanTests.CLASS));
        Advised advised = ((Advised) (bf.getBean("frozen")));
        Assert.assertTrue("The proxy should be frozen", advised.isFrozen());
    }

    @Test
    public void testDetectsInterfaces() throws Exception {
        ProxyFactoryBean fb = new ProxyFactoryBean();
        fb.setTarget(new TestBean());
        fb.addAdvice(new DebugInterceptor());
        fb.setBeanFactory(new DefaultListableBeanFactory());
        ITestBean proxy = ((ITestBean) (fb.getObject()));
        Assert.assertTrue(AopUtils.isJdkDynamicProxy(proxy));
    }

    /**
     * Fires only on void methods. Saves list of methods intercepted.
     */
    @SuppressWarnings("serial")
    public static class PointcutForVoid extends DefaultPointcutAdvisor {
        public static List<String> methodNames = new LinkedList<>();

        public static void reset() {
            ProxyFactoryBeanTests.PointcutForVoid.methodNames.clear();
        }

        public PointcutForVoid() {
            setAdvice(new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    ProxyFactoryBeanTests.PointcutForVoid.methodNames.add(invocation.getMethod().getName());
                    return invocation.proceed();
                }
            });
            setPointcut(new DynamicMethodMatcherPointcut() {
                @Override
                public boolean matches(Method m, @Nullable
                Class<?> targetClass, Object... args) {
                    return (m.getReturnType()) == (Void.TYPE);
                }
            });
        }
    }

    public static class DependsOnITestBean {
        public final ITestBean tb;

        public DependsOnITestBean(ITestBean tb) {
            this.tb = tb;
        }
    }

    /**
     * Aspect interface
     */
    public interface AddedGlobalInterface {
        int globalsAdded();
    }

    /**
     * Use as a global interceptor. Checks that
     * global interceptors can add aspect interfaces.
     * NB: Add only via global interceptors in XML file.
     */
    public static class GlobalAspectInterfaceInterceptor implements IntroductionInterceptor {
        @Override
        public boolean implementsInterface(Class<?> intf) {
            return intf.equals(ProxyFactoryBeanTests.AddedGlobalInterface.class);
        }

        @Override
        public Object invoke(MethodInvocation mi) throws Throwable {
            if (mi.getMethod().getDeclaringClass().equals(ProxyFactoryBeanTests.AddedGlobalInterface.class)) {
                return new Integer((-1));
            }
            return mi.proceed();
        }
    }

    public static class GlobalIntroductionAdvice implements IntroductionAdvisor {
        private IntroductionInterceptor gi = new ProxyFactoryBeanTests.GlobalAspectInterfaceInterceptor();

        @Override
        public ClassFilter getClassFilter() {
            return ClassFilter.TRUE;
        }

        @Override
        public Advice getAdvice() {
            return this.gi;
        }

        @Override
        public Class<?>[] getInterfaces() {
            return new Class<?>[]{ ProxyFactoryBeanTests.AddedGlobalInterface.class };
        }

        @Override
        public boolean isPerInstance() {
            return false;
        }

        @Override
        public void validateInterfaces() {
        }
    }
}

