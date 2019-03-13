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
package org.springframework.aop.aspectj.autoproxy;


import AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME;
import TestGroup.PERFORMANCE;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.aspectj.annotation.AspectMetadata;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.MethodInvokingFactoryBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.Assume;
import org.springframework.tests.sample.beans.INestedTestBean;
import org.springframework.tests.sample.beans.ITestBean;
import org.springframework.tests.sample.beans.NestedTestBean;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.StopWatch;


/**
 * Integration tests for AspectJ auto-proxying. Includes mixing with Spring AOP Advisors
 * to demonstrate that existing autoproxying contract is honoured.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @author Sam Brannen
 */
public class AspectJAutoProxyCreatorTests {
    private static final Log factoryLog = LogFactory.getLog(DefaultListableBeanFactory.class);

    @Test
    public void testAspectsAreApplied() {
        ClassPathXmlApplicationContext bf = newContext("aspects.xml");
        ITestBean tb = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertEquals(68, tb.getAge());
        MethodInvokingFactoryBean factoryBean = ((MethodInvokingFactoryBean) (bf.getBean("&factoryBean")));
        Assert.assertTrue(AopUtils.isAopProxy(factoryBean.getTargetObject()));
        Assert.assertEquals(68, getAge());
    }

    @Test
    public void testMultipleAspectsWithParameterApplied() {
        ClassPathXmlApplicationContext bf = newContext("aspects.xml");
        ITestBean tb = ((ITestBean) (bf.getBean("adrian")));
        tb.setAge(10);
        Assert.assertEquals(20, tb.getAge());
    }

    @Test
    public void testAspectsAreAppliedInDefinedOrder() {
        ClassPathXmlApplicationContext bf = newContext("aspectsWithOrdering.xml");
        ITestBean tb = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertEquals(71, tb.getAge());
    }

    @Test
    public void testAspectsAndAdvisorAreApplied() {
        ClassPathXmlApplicationContext ac = newContext("aspectsPlusAdvisor.xml");
        ITestBean shouldBeWeaved = ((ITestBean) (ac.getBean("adrian")));
        doTestAspectsAndAdvisorAreApplied(ac, shouldBeWeaved);
    }

    @Test
    public void testAspectsAndAdvisorAppliedToPrototypeIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(AspectJAutoProxyCreatorTests.factoryLog);
        ClassPathXmlApplicationContext ac = newContext("aspectsPlusAdvisor.xml");
        StopWatch sw = new StopWatch();
        sw.start("Prototype Creation");
        for (int i = 0; i < 10000; i++) {
            ITestBean shouldBeWeaved = ((ITestBean) (ac.getBean("adrian2")));
            if (i < 10) {
                doTestAspectsAndAdvisorAreApplied(ac, shouldBeWeaved);
            }
        }
        sw.stop();
        // What's a reasonable expectation for _any_ server or developer machine load?
        // 9 seconds?
        assertStopWatchTimeLimit(sw, 9000);
    }

    @Test
    public void testAspectsAndAdvisorNotAppliedToPrototypeIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(AspectJAutoProxyCreatorTests.factoryLog);
        ClassPathXmlApplicationContext ac = newContext("aspectsPlusAdvisor.xml");
        StopWatch sw = new StopWatch();
        sw.start("Prototype Creation");
        for (int i = 0; i < 100000; i++) {
            INestedTestBean shouldNotBeWeaved = ((INestedTestBean) (ac.getBean("i21")));
            if (i < 10) {
                Assert.assertFalse(AopUtils.isAopProxy(shouldNotBeWeaved));
            }
        }
        sw.stop();
        // What's a reasonable expectation for _any_ server or developer machine load?
        // 3 seconds?
        assertStopWatchTimeLimit(sw, 6000);
    }

    @Test
    public void testAspectsAndAdvisorNotAppliedToManySingletonsIsFastEnough() {
        Assume.group(PERFORMANCE);
        Assume.notLogging(AspectJAutoProxyCreatorTests.factoryLog);
        GenericApplicationContext ac = new GenericApplicationContext();
        new org.springframework.beans.factory.xml.XmlBeanDefinitionReader(ac).loadBeanDefinitions(new ClassPathResource(qName("aspectsPlusAdvisor.xml"), getClass()));
        for (int i = 0; i < 10000; i++) {
            ac.registerBeanDefinition(("singleton" + i), new RootBeanDefinition(NestedTestBean.class));
        }
        StopWatch sw = new StopWatch();
        sw.start("Singleton Creation");
        ac.refresh();
        sw.stop();
        // What's a reasonable expectation for _any_ server or developer machine load?
        // 8 seconds?
        assertStopWatchTimeLimit(sw, 8000);
    }

    @Test
    public void testAspectsAndAdvisorAreAppliedEvenIfComingFromParentFactory() {
        ClassPathXmlApplicationContext ac = newContext("aspectsPlusAdvisor.xml");
        GenericApplicationContext childAc = new GenericApplicationContext(ac);
        // Create a child factory with a bean that should be woven
        RootBeanDefinition bd = new RootBeanDefinition(TestBean.class);
        bd.getPropertyValues().addPropertyValue(new PropertyValue("name", "Adrian")).addPropertyValue(new PropertyValue("age", 34));
        childAc.registerBeanDefinition("adrian2", bd);
        // Register the advisor auto proxy creator with subclass
        childAc.registerBeanDefinition(AnnotationAwareAspectJAutoProxyCreator.class.getName(), new RootBeanDefinition(AnnotationAwareAspectJAutoProxyCreator.class));
        childAc.refresh();
        ITestBean beanFromChildContextThatShouldBeWeaved = ((ITestBean) (childAc.getBean("adrian2")));
        // testAspectsAndAdvisorAreApplied(childAc, (ITestBean) ac.getBean("adrian"));
        doTestAspectsAndAdvisorAreApplied(childAc, beanFromChildContextThatShouldBeWeaved);
    }

    @Test
    public void testPerThisAspect() {
        ClassPathXmlApplicationContext bf = newContext("perthis.xml");
        ITestBean adrian1 = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertTrue(AopUtils.isAopProxy(adrian1));
        Assert.assertEquals(0, adrian1.getAge());
        Assert.assertEquals(1, adrian1.getAge());
        ITestBean adrian2 = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertNotSame(adrian1, adrian2);
        Assert.assertTrue(AopUtils.isAopProxy(adrian1));
        Assert.assertEquals(0, adrian2.getAge());
        Assert.assertEquals(1, adrian2.getAge());
        Assert.assertEquals(2, adrian2.getAge());
        Assert.assertEquals(3, adrian2.getAge());
        Assert.assertEquals(2, adrian1.getAge());
    }

    @Test
    public void testPerTargetAspect() throws NoSuchMethodException, SecurityException {
        ClassPathXmlApplicationContext bf = newContext("pertarget.xml");
        ITestBean adrian1 = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertTrue(AopUtils.isAopProxy(adrian1));
        // Does not trigger advice or count
        int explicitlySetAge = 25;
        adrian1.setAge(explicitlySetAge);
        Assert.assertEquals("Setter does not initiate advice", explicitlySetAge, adrian1.getAge());
        // Fire aspect
        AspectMetadata am = new AspectMetadata(PerTargetAspect.class, "someBean");
        Assert.assertTrue(am.getPerClausePointcut().getMethodMatcher().matches(TestBean.class.getMethod("getSpouse"), null));
        adrian1.getSpouse();
        Assert.assertEquals("Advice has now been instantiated", 0, adrian1.getAge());
        adrian1.setAge(11);
        Assert.assertEquals("Any int setter increments", 2, adrian1.getAge());
        adrian1.setName("Adrian");
        // assertEquals("Any other setter does not increment", 2, adrian1.getAge());
        ITestBean adrian2 = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertNotSame(adrian1, adrian2);
        Assert.assertTrue(AopUtils.isAopProxy(adrian1));
        Assert.assertEquals(34, adrian2.getAge());
        adrian2.getSpouse();
        Assert.assertEquals("Aspect now fired", 0, adrian2.getAge());
        Assert.assertEquals(1, adrian2.getAge());
        Assert.assertEquals(2, adrian2.getAge());
        Assert.assertEquals(3, adrian1.getAge());
    }

    @Test
    public void testTwoAdviceAspect() {
        ClassPathXmlApplicationContext bf = newContext("twoAdviceAspect.xml");
        ITestBean adrian1 = ((ITestBean) (bf.getBean("adrian")));
        testAgeAspect(adrian1, 0, 2);
    }

    @Test
    public void testTwoAdviceAspectSingleton() {
        ClassPathXmlApplicationContext bf = newContext("twoAdviceAspectSingleton.xml");
        ITestBean adrian1 = ((ITestBean) (bf.getBean("adrian")));
        testAgeAspect(adrian1, 0, 1);
        ITestBean adrian2 = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertNotSame(adrian1, adrian2);
        testAgeAspect(adrian2, 2, 1);
    }

    @Test
    public void testTwoAdviceAspectPrototype() {
        ClassPathXmlApplicationContext bf = newContext("twoAdviceAspectPrototype.xml");
        ITestBean adrian1 = ((ITestBean) (bf.getBean("adrian")));
        testAgeAspect(adrian1, 0, 1);
        ITestBean adrian2 = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertNotSame(adrian1, adrian2);
        testAgeAspect(adrian2, 0, 1);
    }

    @Test
    public void testAdviceUsingJoinPoint() {
        ClassPathXmlApplicationContext bf = newContext("usesJoinPointAspect.xml");
        ITestBean adrian1 = ((ITestBean) (bf.getBean("adrian")));
        adrian1.getAge();
        AdviceUsingThisJoinPoint aspectInstance = ((AdviceUsingThisJoinPoint) (bf.getBean("aspect")));
        // (AdviceUsingThisJoinPoint) Aspects.aspectOf(AdviceUsingThisJoinPoint.class);
        // assertEquals("method-execution(int TestBean.getAge())",aspectInstance.getLastMethodEntered());
        Assert.assertTrue(((aspectInstance.getLastMethodEntered().indexOf("TestBean.getAge())")) != 0));
    }

    @Test
    public void testIncludeMechanism() {
        ClassPathXmlApplicationContext bf = newContext("usesInclude.xml");
        ITestBean adrian = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertTrue(AopUtils.isAopProxy(adrian));
        Assert.assertEquals(68, adrian.getAge());
    }

    @Test
    public void testForceProxyTargetClass() {
        ClassPathXmlApplicationContext bf = newContext("aspectsWithCGLIB.xml");
        ProxyConfig pc = ((ProxyConfig) (bf.getBean(AUTO_PROXY_CREATOR_BEAN_NAME)));
        Assert.assertTrue("should be proxying classes", pc.isProxyTargetClass());
        Assert.assertTrue("should expose proxy", pc.isExposeProxy());
    }

    @Test
    public void testWithAbstractFactoryBeanAreApplied() {
        ClassPathXmlApplicationContext bf = newContext("aspectsWithAbstractBean.xml");
        ITestBean adrian = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertTrue(AopUtils.isAopProxy(adrian));
        Assert.assertEquals(68, adrian.getAge());
    }

    @Test
    public void testRetryAspect() {
        ClassPathXmlApplicationContext bf = newContext("retryAspect.xml");
        UnreliableBean bean = ((UnreliableBean) (bf.getBean("unreliableBean")));
        RetryAspect aspect = ((RetryAspect) (bf.getBean("retryAspect")));
        int attempts = bean.unreliable();
        Assert.assertEquals(2, attempts);
        Assert.assertEquals(2, aspect.getBeginCalls());
        Assert.assertEquals(1, aspect.getRollbackCalls());
        Assert.assertEquals(1, aspect.getCommitCalls());
    }

    @Test
    public void testWithBeanNameAutoProxyCreator() {
        ClassPathXmlApplicationContext bf = newContext("withBeanNameAutoProxyCreator.xml");
        ITestBean tb = ((ITestBean) (bf.getBean("adrian")));
        Assert.assertEquals(68, tb.getAge());
    }
}

