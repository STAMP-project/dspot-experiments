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
package org.springframework.jms.listener.adapter;


import DeliveryMode.NON_PERSISTENT;
import JmsHeaders.REPLY_TO;
import JmsHeaders.TYPE;
import com.fasterxml.jackson.annotation.JsonView;
import java.util.ArrayList;
import java.util.List;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.jms.StubTextMessage;
import org.springframework.jms.support.QosSettings;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.javax.jms.Message;
import org.springframework.messaging.support.MessageBuilder;


/**
 *
 *
 * @author Stephane Nicoll
 */
public class MessagingMessageListenerAdapterTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private static final Destination sharedReplyDestination = Mockito.mock(Destination.class);

    private final DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

    private final MessagingMessageListenerAdapterTests.SampleBean sample = new MessagingMessageListenerAdapterTests.SampleBean();

    @Test
    public void buildMessageWithStandardMessage() throws JMSException {
        Destination replyTo = new Destination() {};
        Message<String> result = MessageBuilder.withPayload("Response").setHeader("foo", "bar").setHeader(TYPE, "msg_type").setHeader(REPLY_TO, replyTo).build();
        Session session = Mockito.mock(Session.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(new StubTextMessage("Response"));
        MessagingMessageListenerAdapter listener = getSimpleInstance("echo", Message.class);
        javax.jms.Message replyMessage = listener.buildMessage(session, result);
        Mockito.verify(session).createTextMessage("Response");
        Assert.assertNotNull("reply should never be null", replyMessage);
        Assert.assertEquals("Response", getText());
        Assert.assertEquals("custom header not copied", "bar", replyMessage.getStringProperty("foo"));
        Assert.assertEquals("type header not copied", "msg_type", replyMessage.getJMSType());
        Assert.assertEquals("replyTo header not copied", replyTo, replyMessage.getJMSReplyTo());
    }

    @Test
    public void exceptionInListener() {
        javax.jms.Message message = new StubTextMessage("foo");
        Session session = Mockito.mock(Session.class);
        MessagingMessageListenerAdapter listener = getSimpleInstance("fail", String.class);
        try {
            listener.onMessage(message, session);
            Assert.fail("Should have thrown an exception");
        } catch (JMSException ex) {
            Assert.fail("Should not have thrown a JMS exception");
        } catch (ListenerExecutionFailedException ex) {
            Assert.assertEquals(IllegalArgumentException.class, ex.getCause().getClass());
            Assert.assertEquals("Expected test exception", ex.getCause().getMessage());
        }
    }

    @Test
    public void exceptionInInvocation() {
        javax.jms.Message message = new StubTextMessage("foo");
        Session session = Mockito.mock(Session.class);
        MessagingMessageListenerAdapter listener = getSimpleInstance("wrongParam", Integer.class);
        try {
            listener.onMessage(message, session);
            Assert.fail("Should have thrown an exception");
        } catch (JMSException ex) {
            Assert.fail("Should not have thrown a JMS exception");
        } catch (ListenerExecutionFailedException ex) {
            Assert.assertEquals(MessageConversionException.class, ex.getCause().getClass());
        }
    }

    @Test
    public void payloadConversionLazilyInvoked() throws JMSException {
        javax.jms.Message jmsMessage = Mockito.mock(Message.class);
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.given(messageConverter.fromMessage(jmsMessage)).willReturn("FooBar");
        MessagingMessageListenerAdapter listener = getSimpleInstance("simple", Message.class);
        listener.setMessageConverter(messageConverter);
        Message<?> message = listener.toMessagingMessage(jmsMessage);
        Mockito.verify(messageConverter, Mockito.never()).fromMessage(jmsMessage);
        Assert.assertEquals("FooBar", message.getPayload());
        Mockito.verify(messageConverter, Mockito.times(1)).fromMessage(jmsMessage);
    }

    @Test
    public void headerConversionLazilyInvoked() throws JMSException {
        javax.jms.Message jmsMessage = Mockito.mock(Message.class);
        Mockito.when(jmsMessage.getPropertyNames()).thenThrow(new IllegalArgumentException("Header failure"));
        MessagingMessageListenerAdapter listener = getSimpleInstance("simple", Message.class);
        Message<?> message = listener.toMessagingMessage(jmsMessage);
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Header failure");
        message.getHeaders();// Triggers headers resolution

    }

    @Test
    public void incomingMessageUsesMessageConverter() throws JMSException {
        javax.jms.Message jmsMessage = Mockito.mock(Message.class);
        Session session = Mockito.mock(Session.class);
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.given(messageConverter.fromMessage(jmsMessage)).willReturn("FooBar");
        MessagingMessageListenerAdapter listener = getSimpleInstance("simple", Message.class);
        listener.setMessageConverter(messageConverter);
        listener.onMessage(jmsMessage, session);
        Mockito.verify(messageConverter, Mockito.times(1)).fromMessage(jmsMessage);
        Assert.assertEquals(1, sample.simples.size());
        Assert.assertEquals("FooBar", sample.simples.get(0).getPayload());
    }

    @Test
    public void replyUsesMessageConverterForPayload() throws JMSException {
        Session session = Mockito.mock(Session.class);
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.given(messageConverter.toMessage("Response", session)).willReturn(new StubTextMessage("Response"));
        Message<String> result = MessageBuilder.withPayload("Response").build();
        MessagingMessageListenerAdapter listener = getSimpleInstance("echo", Message.class);
        listener.setMessageConverter(messageConverter);
        javax.jms.Message replyMessage = listener.buildMessage(session, result);
        Mockito.verify(messageConverter, Mockito.times(1)).toMessage("Response", session);
        Assert.assertNotNull("reply should never be null", replyMessage);
        Assert.assertEquals("Response", getText());
    }

    @Test
    public void replyPayloadToQueue() throws JMSException {
        Session session = Mockito.mock(Session.class);
        Queue replyDestination = Mockito.mock(Queue.class);
        BDDMockito.given(session.createQueue("queueOut")).willReturn(replyDestination);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(responseMessage);
        BDDMockito.given(session.createProducer(replyDestination)).willReturn(messageProducer);
        MessagingMessageListenerAdapter listener = getPayloadInstance("Response", "replyPayloadToQueue", Message.class);
        listener.onMessage(Mockito.mock(Message.class), session);
        Mockito.verify(session).createQueue("queueOut");
        Mockito.verify(session).createTextMessage("Response");
        Mockito.verify(messageProducer).send(responseMessage);
        Mockito.verify(messageProducer).close();
    }

    @Test
    public void replyWithCustomTimeToLive() throws JMSException {
        Session session = Mockito.mock(Session.class);
        Queue replyDestination = Mockito.mock(Queue.class);
        BDDMockito.given(session.createQueue("queueOut")).willReturn(replyDestination);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(responseMessage);
        BDDMockito.given(session.createProducer(replyDestination)).willReturn(messageProducer);
        MessagingMessageListenerAdapter listener = getPayloadInstance("Response", "replyPayloadToQueue", Message.class);
        QosSettings settings = new QosSettings();
        settings.setTimeToLive(6000);
        listener.setResponseQosSettings(settings);
        listener.onMessage(Mockito.mock(Message.class), session);
        Mockito.verify(session).createQueue("queueOut");
        Mockito.verify(session).createTextMessage("Response");
        Mockito.verify(messageProducer).send(responseMessage, javax.jms.Message, javax.jms.Message, 6000);
        Mockito.verify(messageProducer).close();
    }

    @Test
    public void replyWithFullQoS() throws JMSException {
        Session session = Mockito.mock(Session.class);
        Queue replyDestination = Mockito.mock(Queue.class);
        BDDMockito.given(session.createQueue("queueOut")).willReturn(replyDestination);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(responseMessage);
        BDDMockito.given(session.createProducer(replyDestination)).willReturn(messageProducer);
        MessagingMessageListenerAdapter listener = getPayloadInstance("Response", "replyPayloadToQueue", Message.class);
        QosSettings settings = new QosSettings(DeliveryMode.NON_PERSISTENT, 6, 6000);
        listener.setResponseQosSettings(settings);
        listener.onMessage(Mockito.mock(Message.class), session);
        Mockito.verify(session).createQueue("queueOut");
        Mockito.verify(session).createTextMessage("Response");
        Mockito.verify(messageProducer).send(responseMessage, NON_PERSISTENT, 6, 6000);
        Mockito.verify(messageProducer).close();
    }

    @Test
    public void replyPayloadToTopic() throws JMSException {
        Session session = Mockito.mock(Session.class);
        Topic replyDestination = Mockito.mock(Topic.class);
        BDDMockito.given(session.createTopic("topicOut")).willReturn(replyDestination);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(responseMessage);
        BDDMockito.given(session.createProducer(replyDestination)).willReturn(messageProducer);
        MessagingMessageListenerAdapter listener = getPayloadInstance("Response", "replyPayloadToTopic", Message.class);
        listener.onMessage(Mockito.mock(Message.class), session);
        Mockito.verify(session).createTopic("topicOut");
        Mockito.verify(session).createTextMessage("Response");
        Mockito.verify(messageProducer).send(responseMessage);
        Mockito.verify(messageProducer).close();
    }

    @Test
    public void replyPayloadToDestination() throws JMSException {
        Session session = Mockito.mock(Session.class);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(responseMessage);
        BDDMockito.given(session.createProducer(MessagingMessageListenerAdapterTests.sharedReplyDestination)).willReturn(messageProducer);
        MessagingMessageListenerAdapter listener = getPayloadInstance("Response", "replyPayloadToDestination", Message.class);
        listener.onMessage(Mockito.mock(Message.class), session);
        Mockito.verify(session, Mockito.times(0)).createQueue(ArgumentMatchers.anyString());
        Mockito.verify(session).createTextMessage("Response");
        Mockito.verify(messageProducer).send(responseMessage);
        Mockito.verify(messageProducer).close();
    }

    @Test
    public void replyPayloadNoDestination() throws JMSException {
        Queue replyDestination = Mockito.mock(Queue.class);
        Session session = Mockito.mock(Session.class);
        MessageProducer messageProducer = Mockito.mock(MessageProducer.class);
        TextMessage responseMessage = Mockito.mock(TextMessage.class);
        BDDMockito.given(session.createTextMessage("Response")).willReturn(responseMessage);
        BDDMockito.given(session.createProducer(replyDestination)).willReturn(messageProducer);
        MessagingMessageListenerAdapter listener = getPayloadInstance("Response", "replyPayloadNoDestination", Message.class);
        listener.setDefaultResponseDestination(replyDestination);
        listener.onMessage(Mockito.mock(Message.class), session);
        Mockito.verify(session, Mockito.times(0)).createQueue(ArgumentMatchers.anyString());
        Mockito.verify(session).createTextMessage("Response");
        Mockito.verify(messageProducer).send(responseMessage);
        Mockito.verify(messageProducer).close();
    }

    @Test
    public void replyJackson() throws JMSException {
        TextMessage reply = testReplyWithJackson("replyJackson", "{\"counter\":42,\"name\":\"Response\",\"description\":\"lengthy description\"}");
        Mockito.verify(reply).setObjectProperty("foo", "bar");
    }

    @Test
    public void replyJacksonMessageAndJsonView() throws JMSException {
        TextMessage reply = testReplyWithJackson("replyJacksonMessageAndJsonView", "{\"name\":\"Response\"}");
        Mockito.verify(reply).setObjectProperty("foo", "bar");
    }

    @Test
    public void replyJacksonPojoAndJsonView() throws JMSException {
        TextMessage reply = testReplyWithJackson("replyJacksonPojoAndJsonView", "{\"name\":\"Response\"}");
        Mockito.verify(reply, Mockito.never()).setObjectProperty("foo", "bar");
    }

    @SuppressWarnings("unused")
    private static class SampleBean {
        public final List<Message<String>> simples = new ArrayList<>();

        public void simple(Message<String> input) {
            simples.add(input);
        }

        public Message<String> echo(Message<String> input) {
            return MessageBuilder.withPayload(input.getPayload()).setHeader(TYPE, "reply").build();
        }

        public JmsResponse<String> replyPayloadToQueue(Message<String> input) {
            return JmsResponse.forQueue(input.getPayload(), "queueOut");
        }

        public JmsResponse<String> replyPayloadToTopic(Message<String> input) {
            return JmsResponse.forTopic(input.getPayload(), "topicOut");
        }

        public JmsResponse<String> replyPayloadToDestination(Message<String> input) {
            return JmsResponse.forDestination(input.getPayload(), MessagingMessageListenerAdapterTests.sharedReplyDestination);
        }

        public JmsResponse<String> replyPayloadNoDestination(Message<String> input) {
            return new JmsResponse(input.getPayload(), null);
        }

        public Message<MessagingMessageListenerAdapterTests.SampleResponse> replyJackson(Message<String> input) {
            return MessageBuilder.withPayload(createSampleResponse(input.getPayload())).setHeader("foo", "bar").build();
        }

        @JsonView(MessagingMessageListenerAdapterTests.Summary.class)
        public Message<MessagingMessageListenerAdapterTests.SampleResponse> replyJacksonMessageAndJsonView(Message<String> input) {
            return MessageBuilder.withPayload(createSampleResponse(input.getPayload())).setHeader("foo", "bar").build();
        }

        @JsonView(MessagingMessageListenerAdapterTests.Summary.class)
        public MessagingMessageListenerAdapterTests.SampleResponse replyJacksonPojoAndJsonView(Message<String> input) {
            return createSampleResponse(input.getPayload());
        }

        private MessagingMessageListenerAdapterTests.SampleResponse createSampleResponse(String name) {
            return new MessagingMessageListenerAdapterTests.SampleResponse(name, "lengthy description");
        }

        public void fail(String input) {
            throw new IllegalArgumentException("Expected test exception");
        }

        public void wrongParam(Integer i) {
            throw new IllegalArgumentException("Should not have been called");
        }
    }

    interface Summary {}

    interface Full extends MessagingMessageListenerAdapterTests.Summary {}

    private static class SampleResponse {
        private int counter = 42;

        @JsonView(MessagingMessageListenerAdapterTests.Summary.class)
        private String name;

        @JsonView(MessagingMessageListenerAdapterTests.Full.class)
        private String description;

        SampleResponse() {
        }

        public SampleResponse(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public int getCounter() {
            return counter;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}

