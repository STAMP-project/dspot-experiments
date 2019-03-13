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
package org.springframework.jms.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.JmsListenerContainerTestFactory;
import org.springframework.jms.config.JmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.config.MessageListenerTestContainer;
import org.springframework.jms.config.MethodJmsListenerEndpoint;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;


/**
 *
 *
 * @author Stephane Nicoll
 * @author Juergen Hoeller
 */
public class JmsListenerAnnotationBeanPostProcessorTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void simpleMessageListener() throws Exception {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(JmsListenerAnnotationBeanPostProcessorTests.Config.class, JmsListenerAnnotationBeanPostProcessorTests.SimpleMessageListenerTestBean.class);
        JmsListenerContainerTestFactory factory = context.getBean(JmsListenerContainerTestFactory.class);
        Assert.assertEquals("One container should have been registered", 1, factory.getListenerContainers().size());
        MessageListenerTestContainer container = factory.getListenerContainers().get(0);
        JmsListenerEndpoint endpoint = container.getEndpoint();
        Assert.assertEquals("Wrong endpoint type", MethodJmsListenerEndpoint.class, endpoint.getClass());
        MethodJmsListenerEndpoint methodEndpoint = ((MethodJmsListenerEndpoint) (endpoint));
        Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.SimpleMessageListenerTestBean.class, methodEndpoint.getBean().getClass());
        Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.SimpleMessageListenerTestBean.class.getMethod("handleIt", String.class), methodEndpoint.getMethod());
        Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.SimpleMessageListenerTestBean.class.getMethod("handleIt", String.class), methodEndpoint.getMostSpecificMethod());
        SimpleMessageListenerContainer listenerContainer = new SimpleMessageListenerContainer();
        methodEndpoint.setupListenerContainer(listenerContainer);
        Assert.assertNotNull(listenerContainer.getMessageListener());
        Assert.assertTrue(("Should have been started " + container), container.isStarted());
        context.close();// Close and stop the listeners

        Assert.assertTrue(("Should have been stopped " + container), container.isStopped());
    }

    @Test
    public void metaAnnotationIsDiscovered() throws Exception {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(JmsListenerAnnotationBeanPostProcessorTests.Config.class, JmsListenerAnnotationBeanPostProcessorTests.MetaAnnotationTestBean.class);
        try {
            JmsListenerContainerTestFactory factory = context.getBean(JmsListenerContainerTestFactory.class);
            Assert.assertEquals("one container should have been registered", 1, factory.getListenerContainers().size());
            JmsListenerEndpoint endpoint = factory.getListenerContainers().get(0).getEndpoint();
            Assert.assertEquals("Wrong endpoint type", MethodJmsListenerEndpoint.class, endpoint.getClass());
            MethodJmsListenerEndpoint methodEndpoint = ((MethodJmsListenerEndpoint) (endpoint));
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.MetaAnnotationTestBean.class, methodEndpoint.getBean().getClass());
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.MetaAnnotationTestBean.class.getMethod("handleIt", String.class), methodEndpoint.getMethod());
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.MetaAnnotationTestBean.class.getMethod("handleIt", String.class), methodEndpoint.getMostSpecificMethod());
            Assert.assertEquals("metaTestQueue", getDestination());
        } finally {
            context.close();
        }
    }

    @Test
    public void sendToAnnotationFoundOnInterfaceProxy() throws Exception {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(JmsListenerAnnotationBeanPostProcessorTests.Config.class, JmsListenerAnnotationBeanPostProcessorTests.ProxyConfig.class, JmsListenerAnnotationBeanPostProcessorTests.InterfaceProxyTestBean.class);
        try {
            JmsListenerContainerTestFactory factory = context.getBean(JmsListenerContainerTestFactory.class);
            Assert.assertEquals("one container should have been registered", 1, factory.getListenerContainers().size());
            JmsListenerEndpoint endpoint = factory.getListenerContainers().get(0).getEndpoint();
            Assert.assertEquals("Wrong endpoint type", MethodJmsListenerEndpoint.class, endpoint.getClass());
            MethodJmsListenerEndpoint methodEndpoint = ((MethodJmsListenerEndpoint) (endpoint));
            Assert.assertTrue(AopUtils.isJdkDynamicProxy(methodEndpoint.getBean()));
            Assert.assertTrue(((methodEndpoint.getBean()) instanceof JmsListenerAnnotationBeanPostProcessorTests.SimpleService));
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.SimpleService.class.getMethod("handleIt", String.class, String.class), methodEndpoint.getMethod());
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.InterfaceProxyTestBean.class.getMethod("handleIt", String.class, String.class), methodEndpoint.getMostSpecificMethod());
            Method method = ReflectionUtils.findMethod(endpoint.getClass(), "getDefaultResponseDestination");
            ReflectionUtils.makeAccessible(method);
            Object destination = ReflectionUtils.invokeMethod(method, endpoint);
            Assert.assertEquals("SendTo annotation not found on proxy", "foobar", destination);
        } finally {
            context.close();
        }
    }

    @Test
    public void sendToAnnotationFoundOnCglibProxy() throws Exception {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(JmsListenerAnnotationBeanPostProcessorTests.Config.class, JmsListenerAnnotationBeanPostProcessorTests.ProxyConfig.class, JmsListenerAnnotationBeanPostProcessorTests.ClassProxyTestBean.class);
        try {
            JmsListenerContainerTestFactory factory = context.getBean(JmsListenerContainerTestFactory.class);
            Assert.assertEquals("one container should have been registered", 1, factory.getListenerContainers().size());
            JmsListenerEndpoint endpoint = factory.getListenerContainers().get(0).getEndpoint();
            Assert.assertEquals("Wrong endpoint type", MethodJmsListenerEndpoint.class, endpoint.getClass());
            MethodJmsListenerEndpoint methodEndpoint = ((MethodJmsListenerEndpoint) (endpoint));
            Assert.assertTrue(AopUtils.isCglibProxy(methodEndpoint.getBean()));
            Assert.assertTrue(((methodEndpoint.getBean()) instanceof JmsListenerAnnotationBeanPostProcessorTests.ClassProxyTestBean));
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.ClassProxyTestBean.class.getMethod("handleIt", String.class, String.class), methodEndpoint.getMethod());
            Assert.assertEquals(JmsListenerAnnotationBeanPostProcessorTests.ClassProxyTestBean.class.getMethod("handleIt", String.class, String.class), methodEndpoint.getMostSpecificMethod());
            Method method = ReflectionUtils.findMethod(endpoint.getClass(), "getDefaultResponseDestination");
            ReflectionUtils.makeAccessible(method);
            Object destination = ReflectionUtils.invokeMethod(method, endpoint);
            Assert.assertEquals("SendTo annotation not found on proxy", "foobar", destination);
        } finally {
            context.close();
        }
    }

    @Test
    @SuppressWarnings("resource")
    public void invalidProxy() {
        thrown.expect(BeanCreationException.class);
        thrown.expectCause(CoreMatchers.is(CoreMatchers.instanceOf(IllegalStateException.class)));
        thrown.expectMessage("handleIt2");
        new AnnotationConfigApplicationContext(JmsListenerAnnotationBeanPostProcessorTests.Config.class, JmsListenerAnnotationBeanPostProcessorTests.ProxyConfig.class, JmsListenerAnnotationBeanPostProcessorTests.InvalidProxyTestBean.class);
    }

    @Component
    static class SimpleMessageListenerTestBean {
        @JmsListener(destination = "testQueue")
        public void handleIt(String body) {
        }
    }

    @Component
    static class MetaAnnotationTestBean {
        @JmsListenerAnnotationBeanPostProcessorTests.FooListener
        public void handleIt(String body) {
        }
    }

    @JmsListener(destination = "metaTestQueue")
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FooListener {}

    @Configuration
    static class Config {
        @Bean
        public JmsListenerAnnotationBeanPostProcessor postProcessor() {
            JmsListenerAnnotationBeanPostProcessor postProcessor = new JmsListenerAnnotationBeanPostProcessor();
            postProcessor.setContainerFactoryBeanName("testFactory");
            postProcessor.setEndpointRegistry(jmsListenerEndpointRegistry());
            return postProcessor;
        }

        @Bean
        public JmsListenerEndpointRegistry jmsListenerEndpointRegistry() {
            return new JmsListenerEndpointRegistry();
        }

        @Bean
        public JmsListenerContainerTestFactory testFactory() {
            return new JmsListenerContainerTestFactory();
        }
    }

    @Configuration
    @EnableTransactionManagement
    static class ProxyConfig {
        @Bean
        public PlatformTransactionManager transactionManager() {
            return Mockito.mock(PlatformTransactionManager.class);
        }
    }

    interface SimpleService {
        void handleIt(String value, String body);
    }

    @Component
    static class InterfaceProxyTestBean implements JmsListenerAnnotationBeanPostProcessorTests.SimpleService {
        @Override
        @Transactional
        @JmsListener(destination = "testQueue")
        @SendTo("foobar")
        public void handleIt(@Header
        String value, String body) {
        }
    }

    @Component
    static class ClassProxyTestBean {
        @Transactional
        @JmsListener(destination = "testQueue")
        @SendTo("foobar")
        public void handleIt(@Header
        String value, String body) {
        }
    }

    @Component
    static class InvalidProxyTestBean implements JmsListenerAnnotationBeanPostProcessorTests.SimpleService {
        @Override
        public void handleIt(String value, String body) {
        }

        @Transactional
        @JmsListener(destination = "testQueue")
        @SendTo("foobar")
        public void handleIt2(String body) {
        }
    }
}

