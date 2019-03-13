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
package org.springframework.jms.config;


import DefaultMessageListenerContainer.CACHE_CONNECTION;
import DefaultMessageListenerContainer.DEFAULT_RECOVERY_INTERVAL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.beans.factory.parsing.EmptyReaderEventListener;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jca.endpoint.GenericMessageEndpointManager;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;
import org.springframework.tests.sample.beans.TestBean;
import org.springframework.util.ErrorHandler;
import org.springframework.util.backoff.BackOff;


/**
 *
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @author Christian Dupuis
 * @author Stephane Nicoll
 */
public class JmsNamespaceHandlerTests {
    private static final String DEFAULT_CONNECTION_FACTORY = "connectionFactory";

    private static final String EXPLICIT_CONNECTION_FACTORY = "testConnectionFactory";

    private JmsNamespaceHandlerTests.ToolingTestApplicationContext context;

    @Test
    public void testBeansCreated() {
        Map<String, ?> containers = getBeansOfType(DefaultMessageListenerContainer.class);
        Assert.assertEquals("Context should contain 3 JMS listener containers", 3, containers.size());
        containers = getBeansOfType(GenericMessageEndpointManager.class);
        Assert.assertEquals("Context should contain 3 JCA endpoint containers", 3, containers.size());
        Map<String, JmsListenerContainerFactory> containerFactories = getBeansOfType(JmsListenerContainerFactory.class);
        Assert.assertEquals("Context should contain 3 JmsListenerContainerFactory instances", 3, containerFactories.size());
    }

    @Test
    public void testContainerConfiguration() throws Exception {
        Map<String, DefaultMessageListenerContainer> containers = getBeansOfType(DefaultMessageListenerContainer.class);
        ConnectionFactory defaultConnectionFactory = context.getBean(JmsNamespaceHandlerTests.DEFAULT_CONNECTION_FACTORY, ConnectionFactory.class);
        ConnectionFactory explicitConnectionFactory = context.getBean(JmsNamespaceHandlerTests.EXPLICIT_CONNECTION_FACTORY, ConnectionFactory.class);
        int defaultConnectionFactoryCount = 0;
        int explicitConnectionFactoryCount = 0;
        for (DefaultMessageListenerContainer container : containers.values()) {
            if (container.getConnectionFactory().equals(defaultConnectionFactory)) {
                defaultConnectionFactoryCount++;
            } else
                if (container.getConnectionFactory().equals(explicitConnectionFactory)) {
                    explicitConnectionFactoryCount++;
                }

        }
        Assert.assertEquals("1 container should have the default connectionFactory", 1, defaultConnectionFactoryCount);
        Assert.assertEquals("2 containers should have the explicit connectionFactory", 2, explicitConnectionFactoryCount);
    }

    @Test
    public void testJcaContainerConfiguration() throws Exception {
        Map<String, JmsMessageEndpointManager> containers = getBeansOfType(JmsMessageEndpointManager.class);
        Assert.assertTrue("listener3 not found", containers.containsKey("listener3"));
        JmsMessageEndpointManager listener3 = containers.get("listener3");
        Assert.assertEquals("Wrong resource adapter", context.getBean("testResourceAdapter"), listener3.getResourceAdapter());
        Assert.assertEquals("Wrong activation spec factory", context.getBean("testActivationSpecFactory"), getPropertyValue("activationSpecFactory"));
        Object endpointFactory = new DirectFieldAccessor(listener3).getPropertyValue("endpointFactory");
        Object messageListener = new DirectFieldAccessor(endpointFactory).getPropertyValue("messageListener");
        Assert.assertEquals("Wrong message listener", MessageListenerAdapter.class, messageListener.getClass());
        MessageListenerAdapter adapter = ((MessageListenerAdapter) (messageListener));
        DirectFieldAccessor adapterFieldAccessor = new DirectFieldAccessor(adapter);
        Assert.assertEquals("Message converter not set properly", context.getBean("testMessageConverter"), adapterFieldAccessor.getPropertyValue("messageConverter"));
        Assert.assertEquals("Wrong delegate", context.getBean("testBean1"), adapterFieldAccessor.getPropertyValue("delegate"));
        Assert.assertEquals("Wrong method name", "setName", adapterFieldAccessor.getPropertyValue("defaultListenerMethod"));
    }

    @Test
    public void testJmsContainerFactoryConfiguration() {
        Map<String, DefaultJmsListenerContainerFactory> containers = getBeansOfType(DefaultJmsListenerContainerFactory.class);
        DefaultJmsListenerContainerFactory factory = containers.get("testJmsFactory");
        Assert.assertNotNull("No factory registered with testJmsFactory id", factory);
        DefaultMessageListenerContainer container = factory.createListenerContainer(createDummyEndpoint());
        Assert.assertEquals("explicit connection factory not set", context.getBean(JmsNamespaceHandlerTests.EXPLICIT_CONNECTION_FACTORY), container.getConnectionFactory());
        Assert.assertEquals("explicit destination resolver not set", context.getBean("testDestinationResolver"), container.getDestinationResolver());
        Assert.assertEquals("explicit message converter not set", context.getBean("testMessageConverter"), container.getMessageConverter());
        Assert.assertEquals("Wrong pub/sub", true, container.isPubSubDomain());
        Assert.assertEquals("Wrong durable flag", true, container.isSubscriptionDurable());
        Assert.assertEquals("wrong cache", CACHE_CONNECTION, container.getCacheLevel());
        Assert.assertEquals("wrong concurrency", 3, container.getConcurrentConsumers());
        Assert.assertEquals("wrong concurrency", 5, container.getMaxConcurrentConsumers());
        Assert.assertEquals("wrong prefetch", 50, container.getMaxMessagesPerTask());
        Assert.assertEquals("Wrong phase", 99, container.getPhase());
        Assert.assertSame(context.getBean("testBackOff"), getPropertyValue("backOff"));
    }

    @Test
    public void testJcaContainerFactoryConfiguration() {
        Map<String, DefaultJcaListenerContainerFactory> containers = getBeansOfType(DefaultJcaListenerContainerFactory.class);
        DefaultJcaListenerContainerFactory factory = containers.get("testJcaFactory");
        Assert.assertNotNull("No factory registered with testJcaFactory id", factory);
        JmsMessageEndpointManager container = factory.createListenerContainer(createDummyEndpoint());
        Assert.assertEquals("explicit resource adapter not set", context.getBean("testResourceAdapter"), container.getResourceAdapter());
        Assert.assertEquals("explicit message converter not set", context.getBean("testMessageConverter"), container.getActivationSpecConfig().getMessageConverter());
        Assert.assertEquals("Wrong pub/sub", true, container.isPubSubDomain());
        Assert.assertEquals("wrong concurrency", 5, container.getActivationSpecConfig().getMaxConcurrency());
        Assert.assertEquals("Wrong prefetch", 50, container.getActivationSpecConfig().getPrefetchSize());
        Assert.assertEquals("Wrong phase", 77, container.getPhase());
    }

    @Test
    public void testListeners() throws Exception {
        TestBean testBean1 = context.getBean("testBean1", TestBean.class);
        TestBean testBean2 = context.getBean("testBean2", TestBean.class);
        JmsNamespaceHandlerTests.TestMessageListener testBean3 = context.getBean("testBean3", JmsNamespaceHandlerTests.TestMessageListener.class);
        Assert.assertNull(testBean1.getName());
        Assert.assertNull(testBean2.getName());
        Assert.assertNull(testBean3.message);
        TextMessage message1 = Mockito.mock(TextMessage.class);
        BDDMockito.given(message1.getText()).willReturn("Test1");
        MessageListener listener1 = getListener("listener1");
        listener1.onMessage(message1);
        Assert.assertEquals("Test1", testBean1.getName());
        TextMessage message2 = Mockito.mock(TextMessage.class);
        BDDMockito.given(message2.getText()).willReturn("Test2");
        MessageListener listener2 = getListener("listener2");
        listener2.onMessage(message2);
        Assert.assertEquals("Test2", testBean2.getName());
        TextMessage message3 = Mockito.mock(TextMessage.class);
        MessageListener listener3 = getListener(((DefaultMessageListenerContainer.class.getName()) + "#0"));
        listener3.onMessage(message3);
        Assert.assertSame(message3, testBean3.message);
    }

    @Test
    public void testRecoveryInterval() {
        Object testBackOff = context.getBean("testBackOff");
        BackOff backOff1 = getBackOff("listener1");
        BackOff backOff2 = getBackOff("listener2");
        long recoveryInterval3 = getRecoveryInterval(((DefaultMessageListenerContainer.class.getName()) + "#0"));
        Assert.assertSame(testBackOff, backOff1);
        Assert.assertSame(testBackOff, backOff2);
        Assert.assertEquals(DEFAULT_RECOVERY_INTERVAL, recoveryInterval3);
    }

    @Test
    public void testConcurrency() {
        // JMS
        DefaultMessageListenerContainer listener0 = getBean(((DefaultMessageListenerContainer.class.getName()) + "#0"), DefaultMessageListenerContainer.class);
        DefaultMessageListenerContainer listener1 = getBean("listener1", DefaultMessageListenerContainer.class);
        DefaultMessageListenerContainer listener2 = getBean("listener2", DefaultMessageListenerContainer.class);
        Assert.assertEquals("Wrong concurrency on listener using placeholder", 2, listener0.getConcurrentConsumers());
        Assert.assertEquals("Wrong concurrency on listener using placeholder", 3, listener0.getMaxConcurrentConsumers());
        Assert.assertEquals("Wrong concurrency on listener1", 3, listener1.getConcurrentConsumers());
        Assert.assertEquals("Wrong max concurrency on listener1", 5, listener1.getMaxConcurrentConsumers());
        Assert.assertEquals("Wrong custom concurrency on listener2", 5, listener2.getConcurrentConsumers());
        Assert.assertEquals("Wrong custom max concurrency on listener2", 10, listener2.getMaxConcurrentConsumers());
        // JCA
        JmsMessageEndpointManager listener3 = this.context.getBean("listener3", JmsMessageEndpointManager.class);
        JmsMessageEndpointManager listener4 = this.context.getBean("listener4", JmsMessageEndpointManager.class);
        Assert.assertEquals("Wrong concurrency on listener3", 5, listener3.getActivationSpecConfig().getMaxConcurrency());
        Assert.assertEquals("Wrong custom concurrency on listener4", 7, listener4.getActivationSpecConfig().getMaxConcurrency());
    }

    @Test
    public void testResponseDestination() {
        // JMS
        DefaultMessageListenerContainer listener1 = getBean("listener1", DefaultMessageListenerContainer.class);
        DefaultMessageListenerContainer listener2 = getBean("listener2", DefaultMessageListenerContainer.class);
        Assert.assertEquals("Wrong destination type on listener1", true, listener1.isPubSubDomain());
        Assert.assertEquals("Wrong destination type on listener2", true, listener2.isPubSubDomain());
        Assert.assertEquals("Wrong response destination type on listener1", false, listener1.isReplyPubSubDomain());
        Assert.assertEquals("Wrong response destination type on listener2", false, listener2.isReplyPubSubDomain());
        // JCA
        JmsMessageEndpointManager listener3 = this.context.getBean("listener3", JmsMessageEndpointManager.class);
        JmsMessageEndpointManager listener4 = this.context.getBean("listener4", JmsMessageEndpointManager.class);
        Assert.assertEquals("Wrong destination type on listener3", true, listener3.isPubSubDomain());
        Assert.assertEquals("Wrong destination type on listener4", true, listener4.isPubSubDomain());
        Assert.assertEquals("Wrong response destination type on listener3", false, listener3.isReplyPubSubDomain());
        Assert.assertEquals("Wrong response destination type on listener4", false, listener4.isReplyPubSubDomain());
    }

    @Test
    public void testErrorHandlers() {
        ErrorHandler expected = this.context.getBean("testErrorHandler", ErrorHandler.class);
        ErrorHandler errorHandler1 = getErrorHandler("listener1");
        ErrorHandler errorHandler2 = getErrorHandler("listener2");
        ErrorHandler defaultErrorHandler = getErrorHandler(((DefaultMessageListenerContainer.class.getName()) + "#0"));
        Assert.assertSame(expected, errorHandler1);
        Assert.assertSame(expected, errorHandler2);
        Assert.assertNull(defaultErrorHandler);
    }

    @Test
    public void testPhases() {
        int phase1 = getPhase("listener1");
        int phase2 = getPhase("listener2");
        int phase3 = getPhase("listener3");
        int phase4 = getPhase("listener4");
        int defaultPhase = getPhase(((DefaultMessageListenerContainer.class.getName()) + "#0"));
        Assert.assertEquals(99, phase1);
        Assert.assertEquals(99, phase2);
        Assert.assertEquals(77, phase3);
        Assert.assertEquals(77, phase4);
        Assert.assertEquals(Integer.MAX_VALUE, defaultPhase);
    }

    @Test
    public void testComponentRegistration() {
        Assert.assertTrue("Parser should have registered a component named 'listener1'", context.containsComponentDefinition("listener1"));
        Assert.assertTrue("Parser should have registered a component named 'listener2'", context.containsComponentDefinition("listener2"));
        Assert.assertTrue("Parser should have registered a component named 'listener3'", context.containsComponentDefinition("listener3"));
        Assert.assertTrue((("Parser should have registered a component named '" + (DefaultMessageListenerContainer.class.getName())) + "#0'"), context.containsComponentDefinition(((DefaultMessageListenerContainer.class.getName()) + "#0")));
        Assert.assertTrue((("Parser should have registered a component named '" + (JmsMessageEndpointManager.class.getName())) + "#0'"), context.containsComponentDefinition(((JmsMessageEndpointManager.class.getName()) + "#0")));
        Assert.assertTrue("Parser should have registered a component named 'testJmsFactory", context.containsComponentDefinition("testJmsFactory"));
        Assert.assertTrue("Parser should have registered a component named 'testJcaFactory", context.containsComponentDefinition("testJcaFactory"));
        Assert.assertTrue("Parser should have registered a component named 'testJcaFactory", context.containsComponentDefinition("onlyJmsFactory"));
    }

    @Test
    public void testSourceExtraction() {
        Iterator<ComponentDefinition> iterator = context.getRegisteredComponents();
        while (iterator.hasNext()) {
            ComponentDefinition compDef = iterator.next();
            Assert.assertNotNull((("CompositeComponentDefinition '" + (compDef.getName())) + "' has no source attachment"), compDef.getSource());
            validateComponentDefinition(compDef);
        } 
    }

    public static class TestMessageListener implements MessageListener {
        public Message message;

        @Override
        public void onMessage(Message message) {
            this.message = message;
        }
    }

    /**
     * Internal extension that registers a {@link ReaderEventListener} to store
     * registered {@link ComponentDefinition}s.
     */
    private static class ToolingTestApplicationContext extends ClassPathXmlApplicationContext {
        private Set<ComponentDefinition> registeredComponents;

        public ToolingTestApplicationContext(String path, Class<?> clazz) {
            super(path, clazz);
        }

        @Override
        protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
            this.registeredComponents = new HashSet();
            beanDefinitionReader.setEventListener(new JmsNamespaceHandlerTests.StoringReaderEventListener(this.registeredComponents));
            beanDefinitionReader.setSourceExtractor(new PassThroughSourceExtractor());
        }

        public boolean containsComponentDefinition(String name) {
            for (ComponentDefinition cd : this.registeredComponents) {
                if (cd instanceof CompositeComponentDefinition) {
                    ComponentDefinition[] innerCds = getNestedComponents();
                    for (ComponentDefinition innerCd : innerCds) {
                        if (innerCd.getName().equals(name)) {
                            return true;
                        }
                    }
                } else {
                    if (cd.getName().equals(name)) {
                        return true;
                    }
                }
            }
            return false;
        }

        public Iterator<ComponentDefinition> getRegisteredComponents() {
            return this.registeredComponents.iterator();
        }
    }

    private static class StoringReaderEventListener extends EmptyReaderEventListener {
        protected final Set<ComponentDefinition> registeredComponents;

        public StoringReaderEventListener(Set<ComponentDefinition> registeredComponents) {
            this.registeredComponents = registeredComponents;
        }

        @Override
        public void componentRegistered(ComponentDefinition componentDefinition) {
            this.registeredComponents.add(componentDefinition);
        }
    }

    static class TestErrorHandler implements ErrorHandler {
        @Override
        public void handleError(Throwable t) {
        }
    }
}

