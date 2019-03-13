/**
 * Copyright 2002-2017 the original author or authors.
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


import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.tests.sample.beans.TestBean;


/**
 * Tests the interaction between {@link ApplicationContext} implementations and
 * any registered {@link BeanFactoryPostProcessor} implementations.  Specifically
 * {@link StaticApplicationContext} is used for the tests, but what's represented
 * here is any {@link AbstractApplicationContext} implementation.
 *
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 02.10.2003
 */
public class BeanFactoryPostProcessorTests {
    @Test
    public void testRegisteredBeanFactoryPostProcessor() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("tb1", TestBean.class);
        ac.registerSingleton("tb2", TestBean.class);
        BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor bfpp = new BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor();
        ac.addBeanFactoryPostProcessor(bfpp);
        Assert.assertFalse(bfpp.wasCalled);
        ac.refresh();
        Assert.assertTrue(bfpp.wasCalled);
    }

    @Test
    public void testDefinedBeanFactoryPostProcessor() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("tb1", TestBean.class);
        ac.registerSingleton("tb2", TestBean.class);
        ac.registerSingleton("bfpp", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class);
        ac.refresh();
        BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor bfpp = ((BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor) (ac.getBean("bfpp")));
        Assert.assertTrue(bfpp.wasCalled);
    }

    @Test
    public void testMultipleDefinedBeanFactoryPostProcessors() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("tb1", TestBean.class);
        ac.registerSingleton("tb2", TestBean.class);
        MutablePropertyValues pvs1 = new MutablePropertyValues();
        pvs1.add("initValue", "${key}");
        ac.registerSingleton("bfpp1", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class, pvs1);
        MutablePropertyValues pvs2 = new MutablePropertyValues();
        pvs2.add("properties", "key=value");
        ac.registerSingleton("bfpp2", PropertyPlaceholderConfigurer.class, pvs2);
        ac.refresh();
        BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor bfpp = ((BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor) (ac.getBean("bfpp1")));
        Assert.assertEquals("value", bfpp.initValue);
        Assert.assertTrue(bfpp.wasCalled);
    }

    @Test
    public void testBeanFactoryPostProcessorNotExecutedByBeanFactory() {
        DefaultListableBeanFactory bf = new DefaultListableBeanFactory();
        bf.registerBeanDefinition("tb1", new RootBeanDefinition(TestBean.class));
        bf.registerBeanDefinition("tb2", new RootBeanDefinition(TestBean.class));
        bf.registerBeanDefinition("bfpp", new RootBeanDefinition(BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class));
        BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor bfpp = ((BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor) (bf.getBean("bfpp")));
        Assert.assertFalse(bfpp.wasCalled);
    }

    @Test
    public void testBeanDefinitionRegistryPostProcessor() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("tb1", TestBean.class);
        ac.registerSingleton("tb2", TestBean.class);
        ac.addBeanFactoryPostProcessor(new BeanFactoryPostProcessorTests.PrioritizedBeanDefinitionRegistryPostProcessor());
        BeanFactoryPostProcessorTests.TestBeanDefinitionRegistryPostProcessor bdrpp = new BeanFactoryPostProcessorTests.TestBeanDefinitionRegistryPostProcessor();
        ac.addBeanFactoryPostProcessor(bdrpp);
        Assert.assertFalse(bdrpp.wasCalled);
        ac.refresh();
        Assert.assertTrue(bdrpp.wasCalled);
        Assert.assertTrue(ac.getBean("bfpp1", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class).wasCalled);
        Assert.assertTrue(ac.getBean("bfpp2", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class).wasCalled);
    }

    @Test
    public void testBeanDefinitionRegistryPostProcessorRegisteringAnother() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("tb1", TestBean.class);
        ac.registerSingleton("tb2", TestBean.class);
        ac.registerBeanDefinition("bdrpp2", new RootBeanDefinition(BeanFactoryPostProcessorTests.OuterBeanDefinitionRegistryPostProcessor.class));
        ac.refresh();
        Assert.assertTrue(ac.getBean("bfpp1", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class).wasCalled);
        Assert.assertTrue(ac.getBean("bfpp2", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class).wasCalled);
    }

    @Test
    public void testPrioritizedBeanDefinitionRegistryPostProcessorRegisteringAnother() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerSingleton("tb1", TestBean.class);
        ac.registerSingleton("tb2", TestBean.class);
        ac.registerBeanDefinition("bdrpp2", new RootBeanDefinition(BeanFactoryPostProcessorTests.PrioritizedOuterBeanDefinitionRegistryPostProcessor.class));
        ac.refresh();
        Assert.assertTrue(ac.getBean("bfpp1", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class).wasCalled);
        Assert.assertTrue(ac.getBean("bfpp2", BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class).wasCalled);
    }

    @Test
    public void testBeanFactoryPostProcessorAsApplicationListener() {
        StaticApplicationContext ac = new StaticApplicationContext();
        ac.registerBeanDefinition("bfpp", new RootBeanDefinition(BeanFactoryPostProcessorTests.ListeningBeanFactoryPostProcessor.class));
        ac.refresh();
        Assert.assertTrue(((ac.getBean(BeanFactoryPostProcessorTests.ListeningBeanFactoryPostProcessor.class).received) instanceof ContextRefreshedEvent));
    }

    @Test
    public void testBeanFactoryPostProcessorWithInnerBeanAsApplicationListener() {
        StaticApplicationContext ac = new StaticApplicationContext();
        RootBeanDefinition rbd = new RootBeanDefinition(BeanFactoryPostProcessorTests.NestingBeanFactoryPostProcessor.class);
        rbd.getPropertyValues().add("listeningBean", new RootBeanDefinition(BeanFactoryPostProcessorTests.ListeningBean.class));
        ac.registerBeanDefinition("bfpp", rbd);
        ac.refresh();
        Assert.assertTrue(((ac.getBean(BeanFactoryPostProcessorTests.NestingBeanFactoryPostProcessor.class).getListeningBean().received) instanceof ContextRefreshedEvent));
    }

    public static class TestBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        public String initValue;

        public void setInitValue(String initValue) {
            this.initValue = initValue;
        }

        public boolean wasCalled = false;

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
            wasCalled = true;
        }
    }

    public static class PrioritizedBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor , Ordered {
        @Override
        public int getOrder() {
            return Ordered.HIGHEST_PRECEDENCE;
        }

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            registry.registerBeanDefinition("bfpp1", new RootBeanDefinition(BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class));
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }
    }

    public static class TestBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
        public boolean wasCalled;

        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            Assert.assertTrue(registry.containsBeanDefinition("bfpp1"));
            registry.registerBeanDefinition("bfpp2", new RootBeanDefinition(BeanFactoryPostProcessorTests.TestBeanFactoryPostProcessor.class));
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            this.wasCalled = true;
        }
    }

    public static class OuterBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
        @Override
        public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
            registry.registerBeanDefinition("anotherpp", new RootBeanDefinition(BeanFactoryPostProcessorTests.TestBeanDefinitionRegistryPostProcessor.class));
            registry.registerBeanDefinition("ppp", new RootBeanDefinition(BeanFactoryPostProcessorTests.PrioritizedBeanDefinitionRegistryPostProcessor.class));
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }
    }

    public static class PrioritizedOuterBeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessorTests.OuterBeanDefinitionRegistryPostProcessor implements PriorityOrdered {
        @Override
        public int getOrder() {
            return HIGHEST_PRECEDENCE;
        }
    }

    public static class ListeningBeanFactoryPostProcessor implements BeanFactoryPostProcessor , ApplicationListener {
        public ApplicationEvent received;

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            org.springframework.util.Assert.state(((this.received) == null), "Just one ContextRefreshedEvent expected");
            this.received = event;
        }
    }

    public static class ListeningBean implements ApplicationListener {
        public ApplicationEvent received;

        @Override
        public void onApplicationEvent(ApplicationEvent event) {
            org.springframework.util.Assert.state(((this.received) == null), "Just one ContextRefreshedEvent expected");
            this.received = event;
        }
    }

    public static class NestingBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        private BeanFactoryPostProcessorTests.ListeningBean listeningBean;

        public void setListeningBean(BeanFactoryPostProcessorTests.ListeningBean listeningBean) {
            this.listeningBean = listeningBean;
        }

        public BeanFactoryPostProcessorTests.ListeningBean getListeningBean() {
            return listeningBean;
        }

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        }
    }
}

