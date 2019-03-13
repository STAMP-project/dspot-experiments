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
package org.springframework.jms.core;


import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.jms.support.destination.DestinationResolutionException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.converter.GenericMessageConverter;

import static org.mockito.ArgumentMatchers.eq;


/**
 * Tests for {@link JmsMessagingTemplate}.
 *
 * @author Stephane Nicoll
 */
public class JmsMessagingTemplateTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Captor
    private ArgumentCaptor<MessageCreator> messageCreator;

    @Mock
    private JmsTemplate jmsTemplate;

    private JmsMessagingTemplate messagingTemplate;

    @Test
    public void validateJmsTemplate() {
        Assert.assertSame(this.jmsTemplate, this.messagingTemplate.getJmsTemplate());
    }

    @Test
    public void payloadConverterIsConsistentConstructor() {
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.given(this.jmsTemplate.getMessageConverter()).willReturn(messageConverter);
        JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate(this.jmsTemplate);
        messagingTemplate.afterPropertiesSet();
        assertPayloadConverter(messagingTemplate, messageConverter);
    }

    @Test
    public void payloadConverterIsConsistentSetter() {
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.given(this.jmsTemplate.getMessageConverter()).willReturn(messageConverter);
        JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate();
        messagingTemplate.setJmsTemplate(this.jmsTemplate);
        messagingTemplate.afterPropertiesSet();
        assertPayloadConverter(messagingTemplate, messageConverter);
    }

    @Test
    public void customConverterAlwaysTakesPrecedence() {
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.given(this.jmsTemplate.getMessageConverter()).willReturn(messageConverter);
        MessageConverter customMessageConverter = Mockito.mock(MessageConverter.class);
        JmsMessagingTemplate messagingTemplate = new JmsMessagingTemplate();
        messagingTemplate.setJmsMessageConverter(new org.springframework.jms.support.converter.MessagingMessageConverter(customMessageConverter));
        messagingTemplate.setJmsTemplate(this.jmsTemplate);
        messagingTemplate.afterPropertiesSet();
        assertPayloadConverter(messagingTemplate, customMessageConverter);
    }

    @Test
    public void send() {
        Destination destination = new Destination() {};
        Message<String> message = createTextMessage();
        this.messagingTemplate.send(destination, message);
        Mockito.verify(this.jmsTemplate).send(ArgumentMatchers.eq(destination), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());
    }

    @Test
    public void sendName() {
        Message<String> message = createTextMessage();
        this.messagingTemplate.send("myQueue", message);
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());
    }

    @Test
    public void sendDefaultDestination() {
        Destination destination = new Destination() {};
        this.messagingTemplate.setDefaultDestination(destination);
        Message<String> message = createTextMessage();
        this.messagingTemplate.send(message);
        Mockito.verify(this.jmsTemplate).send(ArgumentMatchers.eq(destination), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());
    }

    @Test
    public void sendDefaultDestinationName() {
        this.messagingTemplate.setDefaultDestinationName("myQueue");
        Message<String> message = createTextMessage();
        this.messagingTemplate.send(message);
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());
    }

    @Test
    public void sendNoDefaultSet() {
        Message<String> message = createTextMessage();
        this.thrown.expect(IllegalStateException.class);
        this.messagingTemplate.send(message);
    }

    @Test
    public void sendPropertyInjection() {
        JmsMessagingTemplate t = new JmsMessagingTemplate();
        t.setJmsTemplate(this.jmsTemplate);
        t.setDefaultDestinationName("myQueue");
        t.afterPropertiesSet();
        Message<String> message = createTextMessage();
        t.send(message);
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());
    }

    @Test
    public void convertAndSendPayload() throws JMSException {
        Destination destination = new Destination() {};
        this.messagingTemplate.convertAndSend(destination, "my Payload");
        Mockito.verify(this.jmsTemplate).send(ArgumentMatchers.eq(destination), this.messageCreator.capture());
        TextMessage textMessage = createTextMessage(this.messageCreator.getValue());
        Assert.assertEquals("my Payload", textMessage.getText());
    }

    @Test
    public void convertAndSendPayloadName() throws JMSException {
        this.messagingTemplate.convertAndSend("myQueue", "my Payload");
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        TextMessage textMessage = createTextMessage(this.messageCreator.getValue());
        Assert.assertEquals("my Payload", textMessage.getText());
    }

    @Test
    public void convertAndSendDefaultDestination() throws JMSException {
        Destination destination = new Destination() {};
        this.messagingTemplate.setDefaultDestination(destination);
        this.messagingTemplate.convertAndSend("my Payload");
        Mockito.verify(this.jmsTemplate).send(ArgumentMatchers.eq(destination), this.messageCreator.capture());
        TextMessage textMessage = createTextMessage(this.messageCreator.getValue());
        Assert.assertEquals("my Payload", textMessage.getText());
    }

    @Test
    public void convertAndSendDefaultDestinationName() throws JMSException {
        this.messagingTemplate.setDefaultDestinationName("myQueue");
        this.messagingTemplate.convertAndSend("my Payload");
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        TextMessage textMessage = createTextMessage(this.messageCreator.getValue());
        Assert.assertEquals("my Payload", textMessage.getText());
    }

    @Test
    public void convertAndSendNoDefaultSet() throws JMSException {
        this.thrown.expect(IllegalStateException.class);
        this.messagingTemplate.convertAndSend("my Payload");
    }

    @Test
    public void convertAndSendCustomJmsMessageConverter() throws JMSException {
        this.messagingTemplate.setJmsMessageConverter(new SimpleMessageConverter() {
            @Override
            public Message toMessage(Object object, Session session) throws JMSException, MessageConversionException {
                throw new org.springframework.jms.support.converter.MessageConversionException("Test exception");
            }
        });
        this.messagingTemplate.convertAndSend("myQueue", "msg to convert");
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        this.thrown.expect(org.springframework.messaging.converter.MessageConversionException.class);
        this.thrown.expectMessage(new StringContains("Test exception"));
        this.messageCreator.getValue().createMessage(Mockito.mock(Session.class));
    }

    @Test
    public void convertAndSendPayloadAndHeaders() throws JMSException {
        Destination destination = new Destination() {};
        Map<String, Object> headers = new HashMap<>();
        headers.put("foo", "bar");
        this.messagingTemplate.convertAndSend(destination, "Hello", headers);
        Mockito.verify(this.jmsTemplate).send(ArgumentMatchers.eq(destination), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());// see createTextMessage

    }

    @Test
    public void convertAndSendPayloadAndHeadersName() throws JMSException {
        Map<String, Object> headers = new HashMap<>();
        headers.put("foo", "bar");
        this.messagingTemplate.convertAndSend("myQueue", "Hello", headers);
        Mockito.verify(this.jmsTemplate).send(eq("myQueue"), this.messageCreator.capture());
        assertTextMessage(this.messageCreator.getValue());// see createTextMessage

    }

    @Test
    public void receive() {
        Destination destination = new Destination() {};
        javax.jms.Message jmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.receive(destination)).willReturn(jmsMessage);
        Message<?> message = this.messagingTemplate.receive(destination);
        Mockito.verify(this.jmsTemplate).receive(destination);
        assertTextMessage(message);
    }

    @Test
    public void receiveName() {
        javax.jms.Message jmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(jmsMessage);
        Message<?> message = this.messagingTemplate.receive("myQueue");
        Mockito.verify(this.jmsTemplate).receive("myQueue");
        assertTextMessage(message);
    }

    @Test
    public void receiveDefaultDestination() {
        Destination destination = new Destination() {};
        this.messagingTemplate.setDefaultDestination(destination);
        javax.jms.Message jmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.receive(destination)).willReturn(jmsMessage);
        Message<?> message = this.messagingTemplate.receive();
        Mockito.verify(this.jmsTemplate).receive(destination);
        assertTextMessage(message);
    }

    @Test
    public void receiveDefaultDestinationName() {
        this.messagingTemplate.setDefaultDestinationName("myQueue");
        javax.jms.Message jmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(jmsMessage);
        Message<?> message = this.messagingTemplate.receive();
        Mockito.verify(this.jmsTemplate).receive("myQueue");
        assertTextMessage(message);
    }

    @Test
    public void receiveNoDefaultSet() {
        this.thrown.expect(IllegalStateException.class);
        this.messagingTemplate.receive();
    }

    @Test
    public void receiveAndConvert() {
        Destination destination = new Destination() {};
        javax.jms.Message jmsMessage = createJmsTextMessage("my Payload");
        BDDMockito.given(this.jmsTemplate.receive(destination)).willReturn(jmsMessage);
        String payload = this.messagingTemplate.receiveAndConvert(destination, String.class);
        Assert.assertEquals("my Payload", payload);
        Mockito.verify(this.jmsTemplate).receive(destination);
    }

    @Test
    public void receiveAndConvertName() {
        javax.jms.Message jmsMessage = createJmsTextMessage("my Payload");
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(jmsMessage);
        String payload = this.messagingTemplate.receiveAndConvert("myQueue", String.class);
        Assert.assertEquals("my Payload", payload);
        Mockito.verify(this.jmsTemplate).receive("myQueue");
    }

    @Test
    public void receiveAndConvertDefaultDestination() {
        Destination destination = new Destination() {};
        this.messagingTemplate.setDefaultDestination(destination);
        javax.jms.Message jmsMessage = createJmsTextMessage("my Payload");
        BDDMockito.given(this.jmsTemplate.receive(destination)).willReturn(jmsMessage);
        String payload = this.messagingTemplate.receiveAndConvert(String.class);
        Assert.assertEquals("my Payload", payload);
        Mockito.verify(this.jmsTemplate).receive(destination);
    }

    @Test
    public void receiveAndConvertDefaultDestinationName() {
        this.messagingTemplate.setDefaultDestinationName("myQueue");
        javax.jms.Message jmsMessage = createJmsTextMessage("my Payload");
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(jmsMessage);
        String payload = this.messagingTemplate.receiveAndConvert(String.class);
        Assert.assertEquals("my Payload", payload);
        Mockito.verify(this.jmsTemplate).receive("myQueue");
    }

    @Test
    public void receiveAndConvertWithConversion() {
        javax.jms.Message jmsMessage = createJmsTextMessage("123");
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(jmsMessage);
        this.messagingTemplate.setMessageConverter(new GenericMessageConverter());
        Integer payload = this.messagingTemplate.receiveAndConvert("myQueue", Integer.class);
        Assert.assertEquals(Integer.valueOf(123), payload);
        Mockito.verify(this.jmsTemplate).receive("myQueue");
    }

    @Test
    public void receiveAndConvertNoConverter() {
        javax.jms.Message jmsMessage = createJmsTextMessage("Hello");
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(jmsMessage);
        this.thrown.expect(org.springframework.messaging.converter.MessageConversionException.class);
        this.messagingTemplate.receiveAndConvert("myQueue", Writer.class);
    }

    @Test
    public void receiveAndConvertNoInput() {
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(null);
        Assert.assertNull(this.messagingTemplate.receiveAndConvert("myQueue", String.class));
    }

    @Test
    public void sendAndReceive() {
        Destination destination = new Destination() {};
        Message<String> request = createTextMessage();
        javax.jms.Message replyJmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        Message<?> actual = this.messagingTemplate.sendAndReceive(destination, request);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any());
        assertTextMessage(actual);
    }

    @Test
    public void sendAndReceiveName() {
        Message<String> request = createTextMessage();
        javax.jms.Message replyJmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.sendAndReceive(eq("myQueue"), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        Message<?> actual = this.messagingTemplate.sendAndReceive("myQueue", request);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(eq("myQueue"), ArgumentMatchers.any());
        assertTextMessage(actual);
    }

    @Test
    public void sendAndReceiveDefaultDestination() {
        Destination destination = new Destination() {};
        this.messagingTemplate.setDefaultDestination(destination);
        Message<String> request = createTextMessage();
        javax.jms.Message replyJmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        Message<?> actual = this.messagingTemplate.sendAndReceive(request);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any());
        assertTextMessage(actual);
    }

    @Test
    public void sendAndReceiveDefaultDestinationName() {
        this.messagingTemplate.setDefaultDestinationName("myQueue");
        Message<String> request = createTextMessage();
        javax.jms.Message replyJmsMessage = createJmsTextMessage();
        BDDMockito.given(this.jmsTemplate.sendAndReceive(eq("myQueue"), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        Message<?> actual = this.messagingTemplate.sendAndReceive(request);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(eq("myQueue"), ArgumentMatchers.any());
        assertTextMessage(actual);
    }

    @Test
    public void sendAndReceiveNoDefaultSet() {
        Message<String> message = createTextMessage();
        this.thrown.expect(IllegalStateException.class);
        this.messagingTemplate.sendAndReceive(message);
    }

    @Test
    public void convertSendAndReceivePayload() throws JMSException {
        Destination destination = new Destination() {};
        javax.jms.Message replyJmsMessage = createJmsTextMessage("My reply");
        BDDMockito.given(this.jmsTemplate.sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        String reply = this.messagingTemplate.convertSendAndReceive(destination, "my Payload", String.class);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any());
        Assert.assertEquals("My reply", reply);
    }

    @Test
    public void convertSendAndReceivePayloadName() throws JMSException {
        javax.jms.Message replyJmsMessage = createJmsTextMessage("My reply");
        BDDMockito.given(this.jmsTemplate.sendAndReceive(eq("myQueue"), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        String reply = this.messagingTemplate.convertSendAndReceive("myQueue", "my Payload", String.class);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(eq("myQueue"), ArgumentMatchers.any());
        Assert.assertEquals("My reply", reply);
    }

    @Test
    public void convertSendAndReceiveDefaultDestination() throws JMSException {
        Destination destination = new Destination() {};
        this.messagingTemplate.setDefaultDestination(destination);
        javax.jms.Message replyJmsMessage = createJmsTextMessage("My reply");
        BDDMockito.given(this.jmsTemplate.sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        String reply = this.messagingTemplate.convertSendAndReceive("my Payload", String.class);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any());
        Assert.assertEquals("My reply", reply);
    }

    @Test
    public void convertSendAndReceiveDefaultDestinationName() throws JMSException {
        this.messagingTemplate.setDefaultDestinationName("myQueue");
        javax.jms.Message replyJmsMessage = createJmsTextMessage("My reply");
        BDDMockito.given(this.jmsTemplate.sendAndReceive(eq("myQueue"), ArgumentMatchers.any())).willReturn(replyJmsMessage);
        String reply = this.messagingTemplate.convertSendAndReceive("my Payload", String.class);
        Mockito.verify(this.jmsTemplate, Mockito.times(1)).sendAndReceive(eq("myQueue"), ArgumentMatchers.any());
        Assert.assertEquals("My reply", reply);
    }

    @Test
    public void convertSendAndReceiveNoDefaultSet() throws JMSException {
        this.thrown.expect(IllegalStateException.class);
        this.messagingTemplate.convertSendAndReceive("my Payload", String.class);
    }

    @Test
    public void convertMessageConversionExceptionOnSend() throws JMSException {
        Message<String> message = createTextMessage();
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.willThrow(MessageConversionException.class).given(messageConverter).toMessage(ArgumentMatchers.eq(message), ArgumentMatchers.any());
        this.messagingTemplate.setJmsMessageConverter(messageConverter);
        invokeMessageCreator();
        this.thrown.expect(org.springframework.messaging.converter.MessageConversionException.class);
        this.messagingTemplate.send("myQueue", message);
    }

    @Test
    public void convertMessageConversionExceptionOnReceive() throws JMSException {
        javax.jms.Message message = createJmsTextMessage();
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.willThrow(MessageConversionException.class).given(messageConverter).fromMessage(message);
        this.messagingTemplate.setJmsMessageConverter(messageConverter);
        BDDMockito.given(this.jmsTemplate.receive("myQueue")).willReturn(message);
        this.thrown.expect(org.springframework.messaging.converter.MessageConversionException.class);
        this.messagingTemplate.receive("myQueue");
    }

    @Test
    public void convertMessageNotReadableException() throws JMSException {
        BDDMockito.willThrow(org.springframework.jms.MessageNotReadableException.class).given(this.jmsTemplate).receive("myQueue");
        this.thrown.expect(MessagingException.class);
        this.messagingTemplate.receive("myQueue");
    }

    @Test
    public void convertDestinationResolutionExceptionOnSend() {
        Destination destination = new Destination() {};
        BDDMockito.willThrow(DestinationResolutionException.class).given(this.jmsTemplate).send(ArgumentMatchers.eq(destination), ArgumentMatchers.any());
        this.thrown.expect(DestinationResolutionException.class);
        this.messagingTemplate.send(destination, createTextMessage());
    }

    @Test
    public void convertDestinationResolutionExceptionOnReceive() {
        Destination destination = new Destination() {};
        BDDMockito.willThrow(DestinationResolutionException.class).given(this.jmsTemplate).receive(destination);
        this.thrown.expect(DestinationResolutionException.class);
        this.messagingTemplate.receive(destination);
    }

    @Test
    public void convertMessageFormatException() throws JMSException {
        Message<String> message = createTextMessage();
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.willThrow(javax.jms.MessageFormatException.class).given(messageConverter).toMessage(ArgumentMatchers.eq(message), ArgumentMatchers.any());
        this.messagingTemplate.setJmsMessageConverter(messageConverter);
        invokeMessageCreator();
        this.thrown.expect(org.springframework.messaging.converter.MessageConversionException.class);
        this.messagingTemplate.send("myQueue", message);
    }

    @Test
    public void convertMessageNotWritableException() throws JMSException {
        Message<String> message = createTextMessage();
        MessageConverter messageConverter = Mockito.mock(MessageConverter.class);
        BDDMockito.willThrow(javax.jms.MessageNotWriteableException.class).given(messageConverter).toMessage(ArgumentMatchers.eq(message), ArgumentMatchers.any());
        this.messagingTemplate.setJmsMessageConverter(messageConverter);
        invokeMessageCreator();
        this.thrown.expect(org.springframework.messaging.converter.MessageConversionException.class);
        this.messagingTemplate.send("myQueue", message);
    }

    @Test
    public void convertInvalidDestinationExceptionOnSendAndReceiveWithName() {
        BDDMockito.willThrow(org.springframework.jms.InvalidDestinationException.class).given(this.jmsTemplate).sendAndReceive(eq("unknownQueue"), ArgumentMatchers.any());
        this.thrown.expect(DestinationResolutionException.class);
        this.messagingTemplate.sendAndReceive("unknownQueue", createTextMessage());
    }

    @Test
    public void convertInvalidDestinationExceptionOnSendAndReceive() {
        Destination destination = new Destination() {};
        BDDMockito.willThrow(org.springframework.jms.InvalidDestinationException.class).given(this.jmsTemplate).sendAndReceive(ArgumentMatchers.eq(destination), ArgumentMatchers.any());
        this.thrown.expect(DestinationResolutionException.class);
        this.messagingTemplate.sendAndReceive(destination, createTextMessage());
    }
}

