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
package org.springframework.security.config.annotation.configuration;


import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.test.SpringTestRule;
import org.springframework.web.context.ServletContextAware;


/**
 *
 *
 * @author Rob Winch
 */
public class AutowireBeanFactoryObjectPostProcessorTests {
    @Rule
    public final SpringTestRule spring = new SpringTestRule();

    @Autowired
    private ObjectPostProcessor<Object> objectObjectPostProcessor;

    @Test
    public void postProcessWhenApplicationContextAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        ApplicationContextAware toPostProcess = Mockito.mock(ApplicationContextAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setApplicationContext(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenApplicationEventPublisherAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        ApplicationEventPublisherAware toPostProcess = Mockito.mock(ApplicationEventPublisherAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setApplicationEventPublisher(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenBeanClassLoaderAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        BeanClassLoaderAware toPostProcess = Mockito.mock(BeanClassLoaderAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setBeanClassLoader(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenBeanFactoryAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        BeanFactoryAware toPostProcess = Mockito.mock(BeanFactoryAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setBeanFactory(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenEnvironmentAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        EnvironmentAware toPostProcess = Mockito.mock(EnvironmentAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setEnvironment(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenMessageSourceAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        MessageSourceAware toPostProcess = Mockito.mock(MessageSourceAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setMessageSource(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenServletContextAwareThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        ServletContextAware toPostProcess = Mockito.mock(ServletContextAware.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        Mockito.verify(toPostProcess).setServletContext(ArgumentMatchers.isNotNull());
    }

    @Test
    public void postProcessWhenDisposableBeanThenAwareInvoked() throws Exception {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class).autowire();
        DisposableBean toPostProcess = Mockito.mock(DisposableBean.class);
        this.objectObjectPostProcessor.postProcess(toPostProcess);
        this.spring.getContext().close();
        Mockito.verify(toPostProcess).destroy();
    }

    @Configuration
    static class Config {
        @Bean
        public ObjectPostProcessor objectPostProcessor(AutowireCapableBeanFactory beanFactory) {
            return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
        }
    }

    @Test
    public void postProcessWhenSmartInitializingSingletonThenAwareInvoked() {
        this.spring.register(AutowireBeanFactoryObjectPostProcessorTests.Config.class, AutowireBeanFactoryObjectPostProcessorTests.SmartConfig.class).autowire();
        AutowireBeanFactoryObjectPostProcessorTests.SmartConfig config = this.spring.getContext().getBean(AutowireBeanFactoryObjectPostProcessorTests.SmartConfig.class);
        Mockito.verify(config.toTest).afterSingletonsInstantiated();
    }

    @Configuration
    static class SmartConfig {
        SmartInitializingSingleton toTest = Mockito.mock(SmartInitializingSingleton.class);

        @Autowired
        public void configure(ObjectPostProcessor<Object> p) {
            p.postProcess(this.toTest);
        }
    }

    // SEC-2382
    @Test
    public void autowireBeanFactoryWhenBeanNameAutoProxyCreatorThenWorks() throws Exception {
        this.spring.testConfigLocations("AutowireBeanFactoryObjectPostProcessorTests-aopconfig.xml").autowire();
        MyAdvisedBean bean = this.spring.getContext().getBean(MyAdvisedBean.class);
        assertThat(bean.doStuff()).isEqualTo("null");
    }

    @Configuration
    static class WithBeanNameAutoProxyCreatorConfig {
        @Bean
        public ObjectPostProcessor objectPostProcessor(AutowireCapableBeanFactory beanFactory) {
            return new AutowireBeanFactoryObjectPostProcessor(beanFactory);
        }

        @Autowired
        public void configure(ObjectPostProcessor<Object> p) {
            p.postProcess(new Object());
        }
    }
}

