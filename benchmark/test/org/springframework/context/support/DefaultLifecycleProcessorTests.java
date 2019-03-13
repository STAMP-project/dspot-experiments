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
package org.springframework.context.support;


import TestGroup.PERFORMANCE;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.Lifecycle;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.SmartLifecycle;
import org.springframework.tests.Assume;


/**
 *
 *
 * @author Mark Fisher
 * @since 3.0
 */
public class DefaultLifecycleProcessorTests {
    @Test
    public void defaultLifecycleProcessorInstance() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.refresh();
        Object lifecycleProcessor = getPropertyValue("lifecycleProcessor");
        Assert.assertNotNull(lifecycleProcessor);
        Assert.assertEquals(DefaultLifecycleProcessor.class, lifecycleProcessor.getClass());
    }

    @Test
    public void customLifecycleProcessorInstance() {
        BeanDefinition beanDefinition = new RootBeanDefinition(DefaultLifecycleProcessor.class);
        beanDefinition.getPropertyValues().addPropertyValue("timeoutPerShutdownPhase", 1000);
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerBeanDefinition("lifecycleProcessor", beanDefinition);
        context.refresh();
        LifecycleProcessor bean = context.getBean("lifecycleProcessor", LifecycleProcessor.class);
        Object contextLifecycleProcessor = getPropertyValue("lifecycleProcessor");
        Assert.assertNotNull(contextLifecycleProcessor);
        Assert.assertSame(bean, contextLifecycleProcessor);
        Assert.assertEquals(1000L, new DirectFieldAccessor(contextLifecycleProcessor).getPropertyValue("timeoutPerShutdownPhase"));
    }

    @Test
    public void singleSmartLifecycleAutoStartup() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(1, startedBeans);
        bean.setAutoStartup(true);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean", bean);
        Assert.assertFalse(bean.isRunning());
        context.refresh();
        Assert.assertTrue(bean.isRunning());
        context.stop();
        Assert.assertFalse(bean.isRunning());
        Assert.assertEquals(1, startedBeans.size());
    }

    @Test
    public void singleSmartLifecycleAutoStartupWithLazyInit() throws Exception {
        StaticApplicationContext context = new StaticApplicationContext();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultLifecycleProcessorTests.DummySmartLifecycleBean.class);
        bd.setLazyInit(true);
        context.registerBeanDefinition("bean", bd);
        context.refresh();
        DefaultLifecycleProcessorTests.DummySmartLifecycleBean bean = context.getBean("bean", DefaultLifecycleProcessorTests.DummySmartLifecycleBean.class);
        Assert.assertTrue(bean.isRunning());
        context.stop();
        Assert.assertFalse(bean.isRunning());
    }

    @Test
    public void singleSmartLifecycleAutoStartupWithLazyInitFactoryBean() throws Exception {
        StaticApplicationContext context = new StaticApplicationContext();
        RootBeanDefinition bd = new RootBeanDefinition(DefaultLifecycleProcessorTests.DummySmartLifecycleFactoryBean.class);
        bd.setLazyInit(true);
        context.registerBeanDefinition("bean", bd);
        context.refresh();
        DefaultLifecycleProcessorTests.DummySmartLifecycleFactoryBean bean = context.getBean("&bean", DefaultLifecycleProcessorTests.DummySmartLifecycleFactoryBean.class);
        Assert.assertTrue(bean.isRunning());
        context.stop();
        Assert.assertFalse(bean.isRunning());
    }

    @Test
    public void singleSmartLifecycleWithoutAutoStartup() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(1, startedBeans);
        bean.setAutoStartup(false);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean", bean);
        Assert.assertFalse(bean.isRunning());
        context.refresh();
        Assert.assertFalse(bean.isRunning());
        Assert.assertEquals(0, startedBeans.size());
        context.start();
        Assert.assertTrue(bean.isRunning());
        Assert.assertEquals(1, startedBeans.size());
        context.stop();
    }

    @Test
    public void singleSmartLifecycleAutoStartupWithNonAutoStartupDependency() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(1, startedBeans);
        bean.setAutoStartup(true);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean dependency = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(1, startedBeans);
        dependency.setAutoStartup(false);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean", bean);
        context.getBeanFactory().registerSingleton("dependency", dependency);
        context.getBeanFactory().registerDependentBean("dependency", "bean");
        Assert.assertFalse(bean.isRunning());
        Assert.assertFalse(dependency.isRunning());
        context.refresh();
        Assert.assertTrue(bean.isRunning());
        Assert.assertFalse(dependency.isRunning());
        context.stop();
        Assert.assertFalse(bean.isRunning());
        Assert.assertFalse(dependency.isRunning());
        Assert.assertEquals(1, startedBeans.size());
    }

    @Test
    public void smartLifecycleGroupStartup() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMin = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(Integer.MIN_VALUE, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(1, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(2, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean3 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(3, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMax = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(Integer.MAX_VALUE, startedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean3", bean3);
        context.getBeanFactory().registerSingleton("beanMin", beanMin);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("beanMax", beanMax);
        context.getBeanFactory().registerSingleton("bean1", bean1);
        Assert.assertFalse(beanMin.isRunning());
        Assert.assertFalse(bean1.isRunning());
        Assert.assertFalse(bean2.isRunning());
        Assert.assertFalse(bean3.isRunning());
        Assert.assertFalse(beanMax.isRunning());
        context.refresh();
        Assert.assertTrue(beanMin.isRunning());
        Assert.assertTrue(bean1.isRunning());
        Assert.assertTrue(bean2.isRunning());
        Assert.assertTrue(bean3.isRunning());
        Assert.assertTrue(beanMax.isRunning());
        context.stop();
        Assert.assertEquals(5, startedBeans.size());
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(0)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(1)));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(2)));
        Assert.assertEquals(3, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(3)));
        Assert.assertEquals(Integer.MAX_VALUE, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(4)));
    }

    @Test
    public void contextRefreshThenStartWithMixedBeans() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean1 = DefaultLifecycleProcessorTests.TestLifecycleBean.forStartupTests(startedBeans);
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean2 = DefaultLifecycleProcessorTests.TestLifecycleBean.forStartupTests(startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean smartBean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(5, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean smartBean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests((-3), startedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("simpleBean1", simpleBean1);
        context.getBeanFactory().registerSingleton("smartBean1", smartBean1);
        context.getBeanFactory().registerSingleton("simpleBean2", simpleBean2);
        context.getBeanFactory().registerSingleton("smartBean2", smartBean2);
        Assert.assertFalse(simpleBean1.isRunning());
        Assert.assertFalse(simpleBean2.isRunning());
        Assert.assertFalse(smartBean1.isRunning());
        Assert.assertFalse(smartBean2.isRunning());
        context.refresh();
        Assert.assertTrue(smartBean1.isRunning());
        Assert.assertTrue(smartBean2.isRunning());
        Assert.assertFalse(simpleBean1.isRunning());
        Assert.assertFalse(simpleBean2.isRunning());
        Assert.assertEquals(2, startedBeans.size());
        Assert.assertEquals((-3), DefaultLifecycleProcessorTests.getPhase(startedBeans.get(0)));
        Assert.assertEquals(5, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(1)));
        context.start();
        Assert.assertTrue(smartBean1.isRunning());
        Assert.assertTrue(smartBean2.isRunning());
        Assert.assertTrue(simpleBean1.isRunning());
        Assert.assertTrue(simpleBean2.isRunning());
        Assert.assertEquals(4, startedBeans.size());
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(2)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(3)));
    }

    @Test
    public void contextRefreshThenStopAndRestartWithMixedBeans() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean1 = DefaultLifecycleProcessorTests.TestLifecycleBean.forStartupTests(startedBeans);
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean2 = DefaultLifecycleProcessorTests.TestLifecycleBean.forStartupTests(startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean smartBean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(5, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean smartBean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests((-3), startedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("simpleBean1", simpleBean1);
        context.getBeanFactory().registerSingleton("smartBean1", smartBean1);
        context.getBeanFactory().registerSingleton("simpleBean2", simpleBean2);
        context.getBeanFactory().registerSingleton("smartBean2", smartBean2);
        Assert.assertFalse(simpleBean1.isRunning());
        Assert.assertFalse(simpleBean2.isRunning());
        Assert.assertFalse(smartBean1.isRunning());
        Assert.assertFalse(smartBean2.isRunning());
        context.refresh();
        Assert.assertTrue(smartBean1.isRunning());
        Assert.assertTrue(smartBean2.isRunning());
        Assert.assertFalse(simpleBean1.isRunning());
        Assert.assertFalse(simpleBean2.isRunning());
        Assert.assertEquals(2, startedBeans.size());
        Assert.assertEquals((-3), DefaultLifecycleProcessorTests.getPhase(startedBeans.get(0)));
        Assert.assertEquals(5, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(1)));
        context.stop();
        Assert.assertFalse(simpleBean1.isRunning());
        Assert.assertFalse(simpleBean2.isRunning());
        Assert.assertFalse(smartBean1.isRunning());
        Assert.assertFalse(smartBean2.isRunning());
        context.start();
        Assert.assertTrue(smartBean1.isRunning());
        Assert.assertTrue(smartBean2.isRunning());
        Assert.assertTrue(simpleBean1.isRunning());
        Assert.assertTrue(simpleBean2.isRunning());
        Assert.assertEquals(6, startedBeans.size());
        Assert.assertEquals((-3), DefaultLifecycleProcessorTests.getPhase(startedBeans.get(2)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(3)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(4)));
        Assert.assertEquals(5, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(5)));
    }

    @Test
    public void smartLifecycleGroupShutdown() throws Exception {
        Assume.group(PERFORMANCE);
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(1, 300, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(3, 100, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean3 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(1, 600, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean4 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(2, 400, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean5 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(2, 700, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean6 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MAX_VALUE, 200, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(3, 200, stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean1", bean1);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("bean3", bean3);
        context.getBeanFactory().registerSingleton("bean4", bean4);
        context.getBeanFactory().registerSingleton("bean5", bean5);
        context.getBeanFactory().registerSingleton("bean6", bean6);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.refresh();
        context.stop();
        Assert.assertEquals(Integer.MAX_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(0)));
        Assert.assertEquals(3, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(1)));
        Assert.assertEquals(3, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(2)));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(3)));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(4)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(5)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(6)));
    }

    @Test
    public void singleSmartLifecycleShutdown() throws Exception {
        Assume.group(PERFORMANCE);
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(99, 300, stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean", bean);
        context.refresh();
        Assert.assertTrue(bean.isRunning());
        context.stop();
        Assert.assertEquals(1, stoppedBeans.size());
        Assert.assertFalse(bean.isRunning());
        Assert.assertEquals(bean, stoppedBeans.get(0));
    }

    @Test
    public void singleLifecycleShutdown() throws Exception {
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        Lifecycle bean = new DefaultLifecycleProcessorTests.TestLifecycleBean(null, stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean", bean);
        context.refresh();
        Assert.assertFalse(bean.isRunning());
        bean.start();
        Assert.assertTrue(bean.isRunning());
        context.stop();
        Assert.assertEquals(1, stoppedBeans.size());
        Assert.assertFalse(bean.isRunning());
        Assert.assertEquals(bean, stoppedBeans.get(0));
    }

    @Test
    public void mixedShutdown() throws Exception {
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        Lifecycle bean1 = DefaultLifecycleProcessorTests.TestLifecycleBean.forShutdownTests(stoppedBeans);
        Lifecycle bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(500, 200, stoppedBeans);
        Lifecycle bean3 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MAX_VALUE, 100, stoppedBeans);
        Lifecycle bean4 = DefaultLifecycleProcessorTests.TestLifecycleBean.forShutdownTests(stoppedBeans);
        Lifecycle bean5 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(1, 200, stoppedBeans);
        Lifecycle bean6 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests((-1), 100, stoppedBeans);
        Lifecycle bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MIN_VALUE, 300, stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("bean1", bean1);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("bean3", bean3);
        context.getBeanFactory().registerSingleton("bean4", bean4);
        context.getBeanFactory().registerSingleton("bean5", bean5);
        context.getBeanFactory().registerSingleton("bean6", bean6);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.refresh();
        Assert.assertTrue(bean2.isRunning());
        Assert.assertTrue(bean3.isRunning());
        Assert.assertTrue(bean5.isRunning());
        Assert.assertTrue(bean6.isRunning());
        Assert.assertTrue(bean7.isRunning());
        Assert.assertFalse(bean1.isRunning());
        Assert.assertFalse(bean4.isRunning());
        bean1.start();
        bean4.start();
        Assert.assertTrue(bean1.isRunning());
        Assert.assertTrue(bean4.isRunning());
        context.stop();
        Assert.assertFalse(bean1.isRunning());
        Assert.assertFalse(bean2.isRunning());
        Assert.assertFalse(bean3.isRunning());
        Assert.assertFalse(bean4.isRunning());
        Assert.assertFalse(bean5.isRunning());
        Assert.assertFalse(bean6.isRunning());
        Assert.assertFalse(bean7.isRunning());
        Assert.assertEquals(7, stoppedBeans.size());
        Assert.assertEquals(Integer.MAX_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(0)));
        Assert.assertEquals(500, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(1)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(2)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(3)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(4)));
        Assert.assertEquals((-1), DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(5)));
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(6)));
    }

    @Test
    public void dependencyStartedFirstEvenIfItsPhaseIsHigher() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMin = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(Integer.MIN_VALUE, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(2, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean99 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(99, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMax = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(Integer.MAX_VALUE, startedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("beanMin", beanMin);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("bean99", bean99);
        context.getBeanFactory().registerSingleton("beanMax", beanMax);
        context.getBeanFactory().registerDependentBean("bean99", "bean2");
        context.refresh();
        Assert.assertTrue(beanMin.isRunning());
        Assert.assertTrue(bean2.isRunning());
        Assert.assertTrue(bean99.isRunning());
        Assert.assertTrue(beanMax.isRunning());
        Assert.assertEquals(4, startedBeans.size());
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(0)));
        Assert.assertEquals(99, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(1)));
        Assert.assertEquals(bean99, startedBeans.get(1));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(2)));
        Assert.assertEquals(bean2, startedBeans.get(2));
        Assert.assertEquals(Integer.MAX_VALUE, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(3)));
        context.stop();
    }

    @Test
    public void dependentShutdownFirstEvenIfItsPhaseIsLower() throws Exception {
        Assume.group(PERFORMANCE);
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMin = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MIN_VALUE, 100, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(1, 200, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean99 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(99, 100, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(2, 300, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(7, 400, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMax = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MAX_VALUE, 400, stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("beanMin", beanMin);
        context.getBeanFactory().registerSingleton("bean1", bean1);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.getBeanFactory().registerSingleton("bean99", bean99);
        context.getBeanFactory().registerSingleton("beanMax", beanMax);
        context.getBeanFactory().registerDependentBean("bean99", "bean2");
        context.refresh();
        Assert.assertTrue(beanMin.isRunning());
        Assert.assertTrue(bean1.isRunning());
        Assert.assertTrue(bean2.isRunning());
        Assert.assertTrue(bean7.isRunning());
        Assert.assertTrue(bean99.isRunning());
        Assert.assertTrue(beanMax.isRunning());
        context.stop();
        Assert.assertFalse(beanMin.isRunning());
        Assert.assertFalse(bean1.isRunning());
        Assert.assertFalse(bean2.isRunning());
        Assert.assertFalse(bean7.isRunning());
        Assert.assertFalse(bean99.isRunning());
        Assert.assertFalse(beanMax.isRunning());
        Assert.assertEquals(6, stoppedBeans.size());
        Assert.assertEquals(Integer.MAX_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(0)));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(1)));
        Assert.assertEquals(bean2, stoppedBeans.get(1));
        Assert.assertEquals(99, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(2)));
        Assert.assertEquals(bean99, stoppedBeans.get(2));
        Assert.assertEquals(7, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(3)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(4)));
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(5)));
    }

    @Test
    public void dependencyStartedFirstAndIsSmartLifecycle() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanNegative = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests((-99), startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean99 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(99, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(7, startedBeans);
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean = DefaultLifecycleProcessorTests.TestLifecycleBean.forStartupTests(startedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("beanNegative", beanNegative);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.getBeanFactory().registerSingleton("bean99", bean99);
        context.getBeanFactory().registerSingleton("simpleBean", simpleBean);
        context.getBeanFactory().registerDependentBean("bean7", "simpleBean");
        context.refresh();
        context.stop();
        startedBeans.clear();
        // clean start so that simpleBean is included
        context.start();
        Assert.assertTrue(beanNegative.isRunning());
        Assert.assertTrue(bean99.isRunning());
        Assert.assertTrue(bean7.isRunning());
        Assert.assertTrue(simpleBean.isRunning());
        Assert.assertEquals(4, startedBeans.size());
        Assert.assertEquals((-99), DefaultLifecycleProcessorTests.getPhase(startedBeans.get(0)));
        Assert.assertEquals(7, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(1)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(2)));
        Assert.assertEquals(99, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(3)));
        context.stop();
    }

    @Test
    public void dependentShutdownFirstAndIsSmartLifecycle() throws Exception {
        Assume.group(PERFORMANCE);
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMin = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MIN_VALUE, 400, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanNegative = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests((-99), 100, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(1, 200, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(2, 300, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(7, 400, stoppedBeans);
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean = DefaultLifecycleProcessorTests.TestLifecycleBean.forShutdownTests(stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("beanMin", beanMin);
        context.getBeanFactory().registerSingleton("beanNegative", beanNegative);
        context.getBeanFactory().registerSingleton("bean1", bean1);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.getBeanFactory().registerSingleton("simpleBean", simpleBean);
        context.getBeanFactory().registerDependentBean("simpleBean", "beanNegative");
        context.refresh();
        Assert.assertTrue(beanMin.isRunning());
        Assert.assertTrue(beanNegative.isRunning());
        Assert.assertTrue(bean1.isRunning());
        Assert.assertTrue(bean2.isRunning());
        Assert.assertTrue(bean7.isRunning());
        // should start since it's a dependency of an auto-started bean
        Assert.assertTrue(simpleBean.isRunning());
        context.stop();
        Assert.assertFalse(beanMin.isRunning());
        Assert.assertFalse(beanNegative.isRunning());
        Assert.assertFalse(bean1.isRunning());
        Assert.assertFalse(bean2.isRunning());
        Assert.assertFalse(bean7.isRunning());
        Assert.assertFalse(simpleBean.isRunning());
        Assert.assertEquals(6, stoppedBeans.size());
        Assert.assertEquals(7, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(0)));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(1)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(2)));
        Assert.assertEquals((-99), DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(3)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(4)));
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(5)));
    }

    @Test
    public void dependencyStartedFirstButNotSmartLifecycle() throws Exception {
        CopyOnWriteArrayList<Lifecycle> startedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMin = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(Integer.MIN_VALUE, startedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forStartupTests(7, startedBeans);
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean = DefaultLifecycleProcessorTests.TestLifecycleBean.forStartupTests(startedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("beanMin", beanMin);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.getBeanFactory().registerSingleton("simpleBean", simpleBean);
        context.getBeanFactory().registerDependentBean("simpleBean", "beanMin");
        context.refresh();
        Assert.assertTrue(beanMin.isRunning());
        Assert.assertTrue(bean7.isRunning());
        Assert.assertTrue(simpleBean.isRunning());
        Assert.assertEquals(3, startedBeans.size());
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(0)));
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(1)));
        Assert.assertEquals(7, DefaultLifecycleProcessorTests.getPhase(startedBeans.get(2)));
        context.stop();
    }

    @Test
    public void dependentShutdownFirstButNotSmartLifecycle() throws Exception {
        Assume.group(PERFORMANCE);
        CopyOnWriteArrayList<Lifecycle> stoppedBeans = new CopyOnWriteArrayList<>();
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean1 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(1, 200, stoppedBeans);
        DefaultLifecycleProcessorTests.TestLifecycleBean simpleBean = DefaultLifecycleProcessorTests.TestLifecycleBean.forShutdownTests(stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean2 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(2, 300, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean bean7 = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(7, 400, stoppedBeans);
        DefaultLifecycleProcessorTests.TestSmartLifecycleBean beanMin = DefaultLifecycleProcessorTests.TestSmartLifecycleBean.forShutdownTests(Integer.MIN_VALUE, 400, stoppedBeans);
        StaticApplicationContext context = new StaticApplicationContext();
        context.getBeanFactory().registerSingleton("beanMin", beanMin);
        context.getBeanFactory().registerSingleton("bean1", bean1);
        context.getBeanFactory().registerSingleton("bean2", bean2);
        context.getBeanFactory().registerSingleton("bean7", bean7);
        context.getBeanFactory().registerSingleton("simpleBean", simpleBean);
        context.getBeanFactory().registerDependentBean("bean2", "simpleBean");
        context.refresh();
        Assert.assertTrue(beanMin.isRunning());
        Assert.assertTrue(bean1.isRunning());
        Assert.assertTrue(bean2.isRunning());
        Assert.assertTrue(bean7.isRunning());
        Assert.assertFalse(simpleBean.isRunning());
        simpleBean.start();
        Assert.assertTrue(simpleBean.isRunning());
        context.stop();
        Assert.assertFalse(beanMin.isRunning());
        Assert.assertFalse(bean1.isRunning());
        Assert.assertFalse(bean2.isRunning());
        Assert.assertFalse(bean7.isRunning());
        Assert.assertFalse(simpleBean.isRunning());
        Assert.assertEquals(5, stoppedBeans.size());
        Assert.assertEquals(7, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(0)));
        Assert.assertEquals(0, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(1)));
        Assert.assertEquals(2, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(2)));
        Assert.assertEquals(1, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(3)));
        Assert.assertEquals(Integer.MIN_VALUE, DefaultLifecycleProcessorTests.getPhase(stoppedBeans.get(4)));
    }

    private static class TestLifecycleBean implements Lifecycle {
        private final CopyOnWriteArrayList<Lifecycle> startedBeans;

        private final CopyOnWriteArrayList<Lifecycle> stoppedBeans;

        private volatile boolean running;

        static DefaultLifecycleProcessorTests.TestLifecycleBean forStartupTests(CopyOnWriteArrayList<Lifecycle> startedBeans) {
            return new DefaultLifecycleProcessorTests.TestLifecycleBean(startedBeans, null);
        }

        static DefaultLifecycleProcessorTests.TestLifecycleBean forShutdownTests(CopyOnWriteArrayList<Lifecycle> stoppedBeans) {
            return new DefaultLifecycleProcessorTests.TestLifecycleBean(null, stoppedBeans);
        }

        private TestLifecycleBean(CopyOnWriteArrayList<Lifecycle> startedBeans, CopyOnWriteArrayList<Lifecycle> stoppedBeans) {
            this.startedBeans = startedBeans;
            this.stoppedBeans = stoppedBeans;
        }

        @Override
        public boolean isRunning() {
            return this.running;
        }

        @Override
        public void start() {
            if ((this.startedBeans) != null) {
                this.startedBeans.add(this);
            }
            this.running = true;
        }

        @Override
        public void stop() {
            if ((this.stoppedBeans) != null) {
                this.stoppedBeans.add(this);
            }
            this.running = false;
        }
    }

    private static class TestSmartLifecycleBean extends DefaultLifecycleProcessorTests.TestLifecycleBean implements SmartLifecycle {
        private final int phase;

        private final int shutdownDelay;

        private volatile boolean autoStartup = true;

        static DefaultLifecycleProcessorTests.TestSmartLifecycleBean forStartupTests(int phase, CopyOnWriteArrayList<Lifecycle> startedBeans) {
            return new DefaultLifecycleProcessorTests.TestSmartLifecycleBean(phase, 0, startedBeans, null);
        }

        static DefaultLifecycleProcessorTests.TestSmartLifecycleBean forShutdownTests(int phase, int shutdownDelay, CopyOnWriteArrayList<Lifecycle> stoppedBeans) {
            return new DefaultLifecycleProcessorTests.TestSmartLifecycleBean(phase, shutdownDelay, null, stoppedBeans);
        }

        private TestSmartLifecycleBean(int phase, int shutdownDelay, CopyOnWriteArrayList<Lifecycle> startedBeans, CopyOnWriteArrayList<Lifecycle> stoppedBeans) {
            super(startedBeans, stoppedBeans);
            this.phase = phase;
            this.shutdownDelay = shutdownDelay;
        }

        @Override
        public int getPhase() {
            return this.phase;
        }

        @Override
        public boolean isAutoStartup() {
            return this.autoStartup;
        }

        public void setAutoStartup(boolean autoStartup) {
            this.autoStartup = autoStartup;
        }

        @Override
        public void stop(final Runnable callback) {
            // calling stop() before the delay to preserve
            // invocation order in the 'stoppedBeans' list
            stop();
            final int delay = this.shutdownDelay;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        // ignore
                    } finally {
                        callback.run();
                    }
                }
            }).start();
        }
    }

    public static class DummySmartLifecycleBean implements SmartLifecycle {
        public boolean running = false;

        @Override
        public boolean isAutoStartup() {
            return true;
        }

        @Override
        public void stop(Runnable callback) {
            this.running = false;
            callback.run();
        }

        @Override
        public void start() {
            this.running = true;
        }

        @Override
        public void stop() {
            this.running = false;
        }

        @Override
        public boolean isRunning() {
            return this.running;
        }

        @Override
        public int getPhase() {
            return 0;
        }
    }

    public static class DummySmartLifecycleFactoryBean implements FactoryBean<Object> , SmartLifecycle {
        public boolean running = false;

        DefaultLifecycleProcessorTests.DummySmartLifecycleBean bean = new DefaultLifecycleProcessorTests.DummySmartLifecycleBean();

        @Override
        public Object getObject() throws Exception {
            return this.bean;
        }

        @Override
        public Class<?> getObjectType() {
            return DefaultLifecycleProcessorTests.DummySmartLifecycleBean.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }

        @Override
        public boolean isAutoStartup() {
            return true;
        }

        @Override
        public void stop(Runnable callback) {
            this.running = false;
            callback.run();
        }

        @Override
        public void start() {
            this.running = true;
        }

        @Override
        public void stop() {
            this.running = false;
        }

        @Override
        public boolean isRunning() {
            return this.running;
        }

        @Override
        public int getPhase() {
            return 0;
        }
    }
}

