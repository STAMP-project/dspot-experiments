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
package org.springframework.jms.config;


import DefaultMessageListenerContainer.CACHE_CONSUMER;
import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import javax.transaction.TransactionManager;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.jms.StubConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;
import org.springframework.jms.listener.endpoint.StubJmsActivationSpecFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class JmsListenerContainerFactoryTests {
    private final ConnectionFactory connectionFactory = new StubConnectionFactory();

    private final DestinationResolver destinationResolver = new DynamicDestinationResolver();

    private final MessageConverter messageConverter = new SimpleMessageConverter();

    private final TransactionManager transactionManager = Mockito.mock(TransactionManager.class);

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void createSimpleContainer() {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        setDefaultJmsConfig(factory);
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        MessageListener messageListener = new MessageListenerAdapter();
        endpoint.setMessageListener(messageListener);
        endpoint.setDestination("myQueue");
        SimpleMessageListenerContainer container = factory.createListenerContainer(endpoint);
        assertDefaultJmsConfig(container);
        Assert.assertEquals(messageListener, container.getMessageListener());
        Assert.assertEquals("myQueue", container.getDestinationName());
    }

    @Test
    public void createJmsContainerFullConfig() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        setDefaultJmsConfig(factory);
        factory.setCacheLevel(CACHE_CONSUMER);
        factory.setConcurrency("3-10");
        factory.setMaxMessagesPerTask(5);
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        MessageListener messageListener = new MessageListenerAdapter();
        endpoint.setMessageListener(messageListener);
        endpoint.setDestination("myQueue");
        DefaultMessageListenerContainer container = factory.createListenerContainer(endpoint);
        assertDefaultJmsConfig(container);
        Assert.assertEquals(CACHE_CONSUMER, container.getCacheLevel());
        Assert.assertEquals(3, container.getConcurrentConsumers());
        Assert.assertEquals(10, container.getMaxConcurrentConsumers());
        Assert.assertEquals(5, container.getMaxMessagesPerTask());
        Assert.assertEquals(messageListener, container.getMessageListener());
        Assert.assertEquals("myQueue", container.getDestinationName());
    }

    @Test
    public void createJcaContainerFullConfig() {
        DefaultJcaListenerContainerFactory factory = new DefaultJcaListenerContainerFactory();
        setDefaultJcaConfig(factory);
        factory.setConcurrency("10");
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        MessageListener messageListener = new MessageListenerAdapter();
        endpoint.setMessageListener(messageListener);
        endpoint.setDestination("myQueue");
        JmsMessageEndpointManager container = factory.createListenerContainer(endpoint);
        assertDefaultJcaConfig(container);
        Assert.assertEquals(10, container.getActivationSpecConfig().getMaxConcurrency());
        Assert.assertEquals(messageListener, container.getMessageListener());
        Assert.assertEquals("myQueue", container.getActivationSpecConfig().getDestinationName());
    }

    @Test
    public void jcaExclusiveProperties() {
        DefaultJcaListenerContainerFactory factory = new DefaultJcaListenerContainerFactory();
        factory.setDestinationResolver(this.destinationResolver);
        factory.setActivationSpecFactory(new StubJmsActivationSpecFactory());
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(new MessageListenerAdapter());
        this.thrown.expect(IllegalStateException.class);
        factory.createListenerContainer(endpoint);
    }

    @Test
    public void backOffOverridesRecoveryInterval() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        BackOff backOff = new FixedBackOff();
        factory.setBackOff(backOff);
        factory.setRecoveryInterval(2000L);
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        MessageListener messageListener = new MessageListenerAdapter();
        endpoint.setMessageListener(messageListener);
        endpoint.setDestination("myQueue");
        DefaultMessageListenerContainer container = factory.createListenerContainer(endpoint);
        Assert.assertSame(backOff, getPropertyValue("backOff"));
    }

    @Test
    public void endpointConcurrencyTakesPrecedence() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConcurrency("2-10");
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        MessageListener messageListener = new MessageListenerAdapter();
        endpoint.setMessageListener(messageListener);
        endpoint.setDestination("myQueue");
        endpoint.setConcurrency("4-6");
        DefaultMessageListenerContainer container = factory.createListenerContainer(endpoint);
        Assert.assertEquals(4, container.getConcurrentConsumers());
        Assert.assertEquals(6, container.getMaxConcurrentConsumers());
    }
}

