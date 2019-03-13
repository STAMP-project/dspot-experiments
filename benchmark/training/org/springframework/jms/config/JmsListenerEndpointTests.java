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
package org.springframework.jms.config;


import javax.jms.MessageListener;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.listener.endpoint.JmsActivationSpecConfig;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class JmsListenerEndpointTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void setupJmsMessageContainerFullConfig() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        MessageListener messageListener = new MessageListenerAdapter();
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setDestination("myQueue");
        endpoint.setSelector("foo = 'bar'");
        endpoint.setSubscription("mySubscription");
        endpoint.setConcurrency("5-10");
        endpoint.setMessageListener(messageListener);
        endpoint.setupListenerContainer(container);
        Assert.assertEquals("myQueue", container.getDestinationName());
        Assert.assertEquals("foo = 'bar'", container.getMessageSelector());
        Assert.assertEquals("mySubscription", container.getSubscriptionName());
        Assert.assertEquals(5, container.getConcurrentConsumers());
        Assert.assertEquals(10, container.getMaxConcurrentConsumers());
        Assert.assertEquals(messageListener, container.getMessageListener());
    }

    @Test
    public void setupJcaMessageContainerFullConfig() {
        JmsMessageEndpointManager container = new JmsMessageEndpointManager();
        MessageListener messageListener = new MessageListenerAdapter();
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setDestination("myQueue");
        endpoint.setSelector("foo = 'bar'");
        endpoint.setSubscription("mySubscription");
        endpoint.setConcurrency("10");
        endpoint.setMessageListener(messageListener);
        endpoint.setupListenerContainer(container);
        JmsActivationSpecConfig config = container.getActivationSpecConfig();
        Assert.assertEquals("myQueue", config.getDestinationName());
        Assert.assertEquals("foo = 'bar'", config.getMessageSelector());
        Assert.assertEquals("mySubscription", config.getSubscriptionName());
        Assert.assertEquals(10, config.getMaxConcurrency());
        Assert.assertEquals(messageListener, container.getMessageListener());
    }

    @Test
    public void setupConcurrencySimpleContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        MessageListener messageListener = new MessageListenerAdapter();
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setConcurrency("5-10");// simple implementation only support max value

        endpoint.setMessageListener(messageListener);
        endpoint.setupListenerContainer(container);
        Assert.assertEquals(10, getPropertyValue("concurrentConsumers"));
    }

    @Test
    public void setupMessageContainerNoListener() {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        thrown.expect(IllegalStateException.class);
        endpoint.setupListenerContainer(container);
    }

    @Test
    public void setupMessageContainerUnsupportedContainer() {
        MessageListenerContainer container = Mockito.mock(MessageListenerContainer.class);
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(new MessageListenerAdapter());
        thrown.expect(IllegalArgumentException.class);
        endpoint.setupListenerContainer(container);
    }
}

