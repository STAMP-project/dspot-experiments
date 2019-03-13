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


import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.jms.StubTextMessage;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessagingMessageConverter;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class JmsListenerContainerFactoryIntegrationTests {
    private final DefaultJmsListenerContainerFactory containerFactory = new DefaultJmsListenerContainerFactory();

    private final DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

    private final JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleBean sample = new JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleBean();

    private JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleInterface listener = sample;

    @Test
    public void messageConverterUsedIfSet() throws JMSException {
        this.containerFactory.setMessageConverter(new JmsListenerContainerFactoryIntegrationTests.UpperCaseMessageConverter());
        testMessageConverterIsUsed();
    }

    @Test
    public void messagingMessageConverterCanBeUsed() throws JMSException {
        MessagingMessageConverter converter = new MessagingMessageConverter();
        converter.setPayloadConverter(new JmsListenerContainerFactoryIntegrationTests.UpperCaseMessageConverter());
        this.containerFactory.setMessageConverter(converter);
        testMessageConverterIsUsed();
    }

    @Test
    public void parameterAnnotationWithJdkProxy() throws JMSException {
        ProxyFactory pf = new ProxyFactory(sample);
        listener = ((JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleInterface) (pf.getProxy()));
        containerFactory.setMessageConverter(new JmsListenerContainerFactoryIntegrationTests.UpperCaseMessageConverter());
        MethodJmsListenerEndpoint endpoint = createDefaultMethodJmsEndpoint(JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleInterface.class, "handleIt", String.class, String.class);
        Message message = new StubTextMessage("foo-bar");
        message.setStringProperty("my-header", "my-value");
        invokeListener(endpoint, message);
        assertListenerMethodInvocation("handleIt");
    }

    @Test
    public void parameterAnnotationWithCglibProxy() throws JMSException {
        ProxyFactory pf = new ProxyFactory(sample);
        pf.setProxyTargetClass(true);
        listener = ((JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleBean) (pf.getProxy()));
        containerFactory.setMessageConverter(new JmsListenerContainerFactoryIntegrationTests.UpperCaseMessageConverter());
        MethodJmsListenerEndpoint endpoint = createDefaultMethodJmsEndpoint(JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleBean.class, "handleIt", String.class, String.class);
        Message message = new StubTextMessage("foo-bar");
        message.setStringProperty("my-header", "my-value");
        invokeListener(endpoint, message);
        assertListenerMethodInvocation("handleIt");
    }

    interface JmsEndpointSampleInterface {
        void handleIt(@Payload
        String msg, @Header("my-header")
        String myHeader);
    }

    static class JmsEndpointSampleBean implements JmsListenerContainerFactoryIntegrationTests.JmsEndpointSampleInterface {
        private final Map<String, Boolean> invocations = new HashMap<>();

        public void handleIt(@Payload
        String msg, @Header("my-header")
        String myHeader) {
            invocations.put("handleIt", true);
            Assert.assertEquals("Unexpected payload message", "FOO-BAR", msg);
            Assert.assertEquals("Unexpected header value", "my-value", myHeader);
        }
    }

    private static class UpperCaseMessageConverter implements MessageConverter {
        @Override
        public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
            return new StubTextMessage(object.toString().toUpperCase());
        }

        @Override
        public Object fromMessage(Message message) throws JMSException, MessageConversionException {
            String content = getText();
            return content.toUpperCase();
        }
    }
}

