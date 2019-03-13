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
package org.springframework.jms.config;


import JmsHeaders.MESSAGE_ID;
import JmsHeaders.TYPE;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.InvalidDestinationException;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.QueueSender;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.jms.StubTextMessage;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.ListenerExecutionFailedException;
import org.springframework.jms.listener.adapter.MessagingMessageListenerAdapter;
import org.springframework.jms.listener.adapter.ReplyFailureException;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.jms.support.QosSettings;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.validation.annotation.Validated;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class MethodJmsListenerEndpointTests {
    private final DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

    private final DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();

    private final MethodJmsListenerEndpointTests.JmsEndpointSampleBean sample = new MethodJmsListenerEndpointTests.JmsEndpointSampleBean();

    @Rule
    public final TestName name = new TestName();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void createMessageListenerNoFactory() {
        MethodJmsListenerEndpoint endpoint = new MethodJmsListenerEndpoint();
        endpoint.setBean(this);
        endpoint.setMethod(getTestMethod());
        this.thrown.expect(IllegalStateException.class);
        endpoint.createMessageListener(this.container);
    }

    @Test
    public void createMessageListener() {
        MethodJmsListenerEndpoint endpoint = new MethodJmsListenerEndpoint();
        endpoint.setBean(this);
        endpoint.setMethod(getTestMethod());
        endpoint.setMessageHandlerMethodFactory(this.factory);
        Assert.assertNotNull(endpoint.createMessageListener(this.container));
    }

    @Test
    public void setExtraCollaborators() {
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        DestinationResolver destinationResolver = Mockito.mock(DestinationResolver.class);
        this.container.setMessageConverter(messageConverter);
        this.container.setDestinationResolver(destinationResolver);
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod("resolveObjectPayload", MethodJmsListenerEndpointTests.MyBean.class), this.container);
        DirectFieldAccessor accessor = new DirectFieldAccessor(listener);
        Assert.assertSame(messageConverter, accessor.getPropertyValue("messageConverter"));
        Assert.assertSame(destinationResolver, accessor.getPropertyValue("destinationResolver"));
    }

    @Test
    public void resolveMessageAndSession() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(Message.class, Session.class);
        Session session = Mockito.mock(Session.class);
        listener.onMessage(createSimpleJmsTextMessage("test"), session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveGenericMessage() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(Message.class);
        Session session = Mockito.mock(Session.class);
        listener.onMessage(createSimpleJmsTextMessage("test"), session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveHeaderAndPayload() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class, int.class);
        Session session = Mockito.mock(Session.class);
        StubTextMessage message = createSimpleJmsTextMessage("my payload");
        message.setIntProperty("myCounter", 55);
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveCustomHeaderNameAndPayload() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class, int.class);
        Session session = Mockito.mock(Session.class);
        StubTextMessage message = createSimpleJmsTextMessage("my payload");
        message.setIntProperty("myCounter", 24);
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveCustomHeaderNameAndPayloadWithHeaderNameSet() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class, int.class);
        Session session = Mockito.mock(Session.class);
        StubTextMessage message = createSimpleJmsTextMessage("my payload");
        message.setIntProperty("myCounter", 24);
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveHeaders() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class, Map.class);
        Session session = Mockito.mock(Session.class);
        StubTextMessage message = createSimpleJmsTextMessage("my payload");
        message.setIntProperty("customInt", 1234);
        message.setJMSMessageID("abcd-1234");
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveMessageHeaders() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(MessageHeaders.class);
        Session session = Mockito.mock(Session.class);
        StubTextMessage message = createSimpleJmsTextMessage("my payload");
        message.setLongProperty("customLong", 4567L);
        message.setJMSType("myMessageType");
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveJmsMessageHeaderAccessor() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(JmsMessageHeaderAccessor.class);
        Session session = Mockito.mock(Session.class);
        StubTextMessage message = createSimpleJmsTextMessage("my payload");
        message.setBooleanProperty("customBoolean", true);
        message.setJMSPriority(9);
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveObjectPayload() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(MethodJmsListenerEndpointTests.MyBean.class);
        MethodJmsListenerEndpointTests.MyBean myBean = new MethodJmsListenerEndpointTests.MyBean();
        myBean.name = "myBean name";
        Session session = Mockito.mock(Session.class);
        ObjectMessage message = Mockito.mock(ObjectMessage.class);
        BDDMockito.given(message.getObject()).willReturn(myBean);
        listener.onMessage(message, session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void resolveConvertedPayload() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(Integer.class);
        Session session = Mockito.mock(Session.class);
        listener.onMessage(createSimpleJmsTextMessage("33"), session);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void processAndReply() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class);
        String body = "echo text";
        String correlationId = "link-1234";
        Destination replyDestination = new Destination() {};
        TextMessage reply = Mockito.mock(TextMessage.class);
        QueueSender queueSender = Mockito.mock(QueueSender.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createTextMessage(body)).willReturn(reply);
        BDDMockito.given(session.createProducer(replyDestination)).willReturn(queueSender);
        listener.setDefaultResponseDestination(replyDestination);
        StubTextMessage inputMessage = createSimpleJmsTextMessage(body);
        inputMessage.setJMSCorrelationID(correlationId);
        listener.onMessage(inputMessage, session);
        assertDefaultListenerMethodInvocation();
        Mockito.verify(reply).setJMSCorrelationID(correlationId);
        Mockito.verify(queueSender).send(reply);
        Mockito.verify(queueSender).close();
    }

    @Test
    public void processAndReplyWithSendToQueue() throws JMSException {
        String methodName = "processAndReplyWithSendTo";
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod(methodName, String.class), container);
        processAndReplyWithSendTo(listener, "replyDestination", false);
        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void processFromTopicAndReplyWithSendToQueue() throws JMSException {
        String methodName = "processAndReplyWithSendTo";
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setPubSubDomain(true);
        container.setReplyPubSubDomain(false);
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod(methodName, String.class), container);
        processAndReplyWithSendTo(listener, "replyDestination", false);
        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void processAndReplyWithSendToTopic() throws JMSException {
        String methodName = "processAndReplyWithSendTo";
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setPubSubDomain(true);
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod(methodName, String.class), container);
        processAndReplyWithSendTo(listener, "replyDestination", true);
        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void processFromQueueAndReplyWithSendToTopic() throws JMSException {
        String methodName = "processAndReplyWithSendTo";
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setReplyPubSubDomain(true);
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod(methodName, String.class), container);
        processAndReplyWithSendTo(listener, "replyDestination", true);
        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void processAndReplyWithDefaultSendTo() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class);
        processAndReplyWithSendTo(listener, "defaultReply", false);
        assertDefaultListenerMethodInvocation();
    }

    @Test
    public void processAndReplyWithCustomReplyQosSettings() throws JMSException {
        String methodName = "processAndReplyWithSendTo";
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        QosSettings replyQosSettings = new QosSettings(1, 6, 6000);
        container.setReplyQosSettings(replyQosSettings);
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod(methodName, String.class), container);
        processAndReplyWithSendTo(listener, "replyDestination", false, replyQosSettings);
        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void processAndReplyWithNullReplyQosSettings() throws JMSException {
        String methodName = "processAndReplyWithSendTo";
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setReplyQosSettings(null);
        MessagingMessageListenerAdapter listener = createInstance(this.factory, getListenerMethod(methodName, String.class), container);
        processAndReplyWithSendTo(listener, "replyDestination", false);
        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void emptySendTo() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(String.class);
        TextMessage reply = Mockito.mock(TextMessage.class);
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createTextMessage("content")).willReturn(reply);
        this.thrown.expect(ReplyFailureException.class);
        this.thrown.expectCause(Matchers.isA(InvalidDestinationException.class));
        listener.onMessage(createSimpleJmsTextMessage("content"), session);
    }

    @Test
    public void invalidSendTo() {
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("firstDestination");
        this.thrown.expectMessage("secondDestination");
        createDefaultInstance(String.class);
    }

    @Test
    public void validatePayloadValid() throws JMSException {
        String methodName = "validatePayload";
        DefaultMessageHandlerMethodFactory customFactory = new DefaultMessageHandlerMethodFactory();
        customFactory.setValidator(testValidator("invalid value"));
        initializeFactory(customFactory);
        Method method = getListenerMethod(methodName, String.class);
        MessagingMessageListenerAdapter listener = createInstance(customFactory, method);
        Session session = Mockito.mock(Session.class);
        listener.onMessage(createSimpleJmsTextMessage("test"), session);// test is a valid value

        assertListenerMethodInvocation(this.sample, methodName);
    }

    @Test
    public void validatePayloadInvalid() throws JMSException {
        DefaultMessageHandlerMethodFactory customFactory = new DefaultMessageHandlerMethodFactory();
        customFactory.setValidator(testValidator("invalid value"));
        Method method = getListenerMethod("validatePayload", String.class);
        MessagingMessageListenerAdapter listener = createInstance(customFactory, method);
        Session session = Mockito.mock(Session.class);
        this.thrown.expect(ListenerExecutionFailedException.class);
        listener.onMessage(createSimpleJmsTextMessage("invalid value"), session);// test is an invalid value

    }

    // failure scenario
    @Test
    public void invalidPayloadType() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(Integer.class);
        Session session = Mockito.mock(Session.class);
        this.thrown.expect(ListenerExecutionFailedException.class);
        this.thrown.expectCause(Matchers.isA(MessageConversionException.class));
        this.thrown.expectMessage(getDefaultListenerMethod(Integer.class).toGenericString());// ref to method

        listener.onMessage(createSimpleJmsTextMessage("test"), session);// test is not a valid integer

    }

    @Test
    public void invalidMessagePayloadType() throws JMSException {
        MessagingMessageListenerAdapter listener = createDefaultInstance(Message.class);
        Session session = Mockito.mock(Session.class);
        this.thrown.expect(ListenerExecutionFailedException.class);
        this.thrown.expectCause(Matchers.isA(MessageConversionException.class));
        listener.onMessage(createSimpleJmsTextMessage("test"), session);// Message<String> as Message<Integer>

    }

    @SendTo("defaultReply")
    @SuppressWarnings("unused")
    static class JmsEndpointSampleBean {
        private final Map<String, Boolean> invocations = new HashMap<>();

        public void resolveMessageAndSession(javax.jms.Message message, Session session) {
            this.invocations.put("resolveMessageAndSession", true);
            Assert.assertNotNull("Message not injected", message);
            Assert.assertNotNull("Session not injected", session);
        }

        public void resolveGenericMessage(Message<String> message) {
            this.invocations.put("resolveGenericMessage", true);
            Assert.assertNotNull("Generic message not injected", message);
            Assert.assertEquals("Wrong message payload", "test", message.getPayload());
        }

        public void resolveHeaderAndPayload(@Payload
        String content, @Header
        int myCounter) {
            this.invocations.put("resolveHeaderAndPayload", true);
            Assert.assertEquals("Wrong @Payload resolution", "my payload", content);
            Assert.assertEquals("Wrong @Header resolution", 55, myCounter);
        }

        public void resolveCustomHeaderNameAndPayload(@Payload
        String content, @Header("myCounter")
        int counter) {
            this.invocations.put("resolveCustomHeaderNameAndPayload", true);
            Assert.assertEquals("Wrong @Payload resolution", "my payload", content);
            Assert.assertEquals("Wrong @Header resolution", 24, counter);
        }

        public void resolveCustomHeaderNameAndPayloadWithHeaderNameSet(@Payload
        String content, @Header(name = "myCounter")
        int counter) {
            this.invocations.put("resolveCustomHeaderNameAndPayloadWithHeaderNameSet", true);
            Assert.assertEquals("Wrong @Payload resolution", "my payload", content);
            Assert.assertEquals("Wrong @Header resolution", 24, counter);
        }

        public void resolveHeaders(String content, @Headers
        Map<String, Object> headers) {
            this.invocations.put("resolveHeaders", true);
            Assert.assertEquals("Wrong payload resolution", "my payload", content);
            Assert.assertNotNull("headers not injected", headers);
            Assert.assertEquals("Missing JMS message id header", "abcd-1234", headers.get(MESSAGE_ID));
            Assert.assertEquals("Missing custom header", 1234, headers.get("customInt"));
        }

        public void resolveMessageHeaders(MessageHeaders headers) {
            this.invocations.put("resolveMessageHeaders", true);
            Assert.assertNotNull("MessageHeaders not injected", headers);
            Assert.assertEquals("Missing JMS message type header", "myMessageType", headers.get(TYPE));
            Assert.assertEquals("Missing custom header", 4567L, ((long) (headers.get("customLong"))), 0.0);
        }

        public void resolveJmsMessageHeaderAccessor(JmsMessageHeaderAccessor headers) {
            this.invocations.put("resolveJmsMessageHeaderAccessor", true);
            Assert.assertNotNull("MessageHeaders not injected", headers);
            Assert.assertEquals("Missing JMS message priority header", Integer.valueOf(9), headers.getPriority());
            Assert.assertEquals("Missing custom header", true, headers.getHeader("customBoolean"));
        }

        public void resolveObjectPayload(MethodJmsListenerEndpointTests.MyBean bean) {
            this.invocations.put("resolveObjectPayload", true);
            Assert.assertNotNull("Object payload not injected", bean);
            Assert.assertEquals("Wrong content for payload", "myBean name", bean.name);
        }

        public void resolveConvertedPayload(Integer counter) {
            this.invocations.put("resolveConvertedPayload", true);
            Assert.assertNotNull("Payload not injected", counter);
            Assert.assertEquals("Wrong content for payload", Integer.valueOf(33), counter);
        }

        public String processAndReply(@Payload
        String content) {
            this.invocations.put("processAndReply", true);
            return content;
        }

        @SendTo("replyDestination")
        public String processAndReplyWithSendTo(String content) {
            this.invocations.put("processAndReplyWithSendTo", true);
            return content;
        }

        public String processAndReplyWithDefaultSendTo(String content) {
            this.invocations.put("processAndReplyWithDefaultSendTo", true);
            return content;
        }

        @SendTo("")
        public String emptySendTo(String content) {
            this.invocations.put("emptySendTo", true);
            return content;
        }

        @SendTo({ "firstDestination", "secondDestination" })
        public String invalidSendTo(String content) {
            this.invocations.put("invalidSendTo", true);
            return content;
        }

        public void validatePayload(@Validated
        String payload) {
            this.invocations.put("validatePayload", true);
        }

        public void invalidPayloadType(@Payload
        Integer payload) {
            throw new IllegalStateException("Should never be called.");
        }

        public void invalidMessagePayloadType(Message<Integer> message) {
            throw new IllegalStateException("Should never be called.");
        }
    }

    @SuppressWarnings("serial")
    static class MyBean implements Serializable {
        private String name;
    }
}

