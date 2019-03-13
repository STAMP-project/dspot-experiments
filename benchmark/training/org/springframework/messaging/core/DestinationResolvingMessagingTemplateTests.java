/**
 * Copyright 2002-2013 the original author or authors.
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
package org.springframework.messaging.core;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ExecutorSubscribableChannel;


/**
 * Unit tests for {@link AbstractDestinationResolvingMessagingTemplate}.
 *
 * @author Rossen Stoyanchev
 */
public class DestinationResolvingMessagingTemplateTests {
    private DestinationResolvingMessagingTemplateTests.TestDestinationResolvingMessagingTemplate template;

    private ExecutorSubscribableChannel myChannel;

    private Map<String, Object> headers;

    private TestMessagePostProcessor postProcessor;

    @Test
    public void send() {
        Message<?> message = new org.springframework.messaging.support.GenericMessage<Object>("payload");
        this.template.send("myChannel", message);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
        Assert.assertSame(message, this.template.message);
    }

    @Test(expected = IllegalStateException.class)
    public void sendNoDestinationResolver() {
        DestinationResolvingMessagingTemplateTests.TestDestinationResolvingMessagingTemplate template = new DestinationResolvingMessagingTemplateTests.TestDestinationResolvingMessagingTemplate();
        template.send("myChannel", new org.springframework.messaging.support.GenericMessage<Object>("payload"));
    }

    @Test
    public void convertAndSendPayload() {
        this.template.convertAndSend("myChannel", "payload");
        Assert.assertSame(this.myChannel, this.template.messageChannel);
        Assert.assertNotNull(this.template.message);
        Assert.assertSame("payload", this.template.message.getPayload());
    }

    @Test
    public void convertAndSendPayloadAndHeaders() {
        this.template.convertAndSend("myChannel", "payload", this.headers);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("value", this.template.message.getHeaders().get("key"));
        Assert.assertEquals("payload", this.template.message.getPayload());
    }

    @Test
    public void convertAndSendPayloadWithPostProcessor() {
        this.template.convertAndSend("myChannel", "payload", this.postProcessor);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("payload", this.template.message.getPayload());
        Assert.assertNotNull(this.postProcessor.getMessage());
        Assert.assertSame(this.postProcessor.getMessage(), this.template.message);
    }

    @Test
    public void convertAndSendPayloadAndHeadersWithPostProcessor() {
        convertAndSend("myChannel", "payload", this.headers, this.postProcessor);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("value", this.template.message.getHeaders().get("key"));
        Assert.assertEquals("payload", this.template.message.getPayload());
        Assert.assertNotNull(this.postProcessor.getMessage());
        Assert.assertSame(this.postProcessor.getMessage(), this.template.message);
    }

    @Test
    public void receive() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage<Object>("payload");
        this.template.setReceiveMessage(expected);
        Message<?> actual = this.template.receive("myChannel");
        Assert.assertSame(expected, actual);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    @Test
    public void receiveAndConvert() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage<Object>("payload");
        this.template.setReceiveMessage(expected);
        String payload = this.template.receiveAndConvert("myChannel", String.class);
        Assert.assertEquals("payload", payload);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    @Test
    public void sendAndReceive() {
        Message<?> requestMessage = new org.springframework.messaging.support.GenericMessage<Object>("request");
        Message<?> responseMessage = new org.springframework.messaging.support.GenericMessage<Object>("response");
        this.template.setReceiveMessage(responseMessage);
        Message<?> actual = this.template.sendAndReceive("myChannel", requestMessage);
        Assert.assertEquals(requestMessage, this.template.message);
        Assert.assertSame(responseMessage, actual);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    @Test
    public void convertSendAndReceive() {
        Message<?> responseMessage = new org.springframework.messaging.support.GenericMessage<Object>("response");
        this.template.setReceiveMessage(responseMessage);
        String actual = this.template.convertSendAndReceive("myChannel", "request", String.class);
        Assert.assertEquals("request", this.template.message.getPayload());
        Assert.assertSame("response", actual);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    @Test
    public void convertSendAndReceiveWithHeaders() {
        Message<?> responseMessage = new org.springframework.messaging.support.GenericMessage<Object>("response");
        this.template.setReceiveMessage(responseMessage);
        String actual = this.template.convertSendAndReceive("myChannel", "request", this.headers, String.class);
        Assert.assertEquals("value", this.template.message.getHeaders().get("key"));
        Assert.assertEquals("request", this.template.message.getPayload());
        Assert.assertSame("response", actual);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    @Test
    public void convertSendAndReceiveWithPostProcessor() {
        Message<?> responseMessage = new org.springframework.messaging.support.GenericMessage<Object>("response");
        this.template.setReceiveMessage(responseMessage);
        String actual = this.template.convertSendAndReceive("myChannel", "request", String.class, this.postProcessor);
        Assert.assertEquals("request", this.template.message.getPayload());
        Assert.assertSame("request", this.postProcessor.getMessage().getPayload());
        Assert.assertSame("response", actual);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    @Test
    public void convertSendAndReceiveWithHeadersAndPostProcessor() {
        Message<?> responseMessage = new org.springframework.messaging.support.GenericMessage<Object>("response");
        this.template.setReceiveMessage(responseMessage);
        String actual = this.template.convertSendAndReceive("myChannel", "request", this.headers, String.class, this.postProcessor);
        Assert.assertEquals("value", this.template.message.getHeaders().get("key"));
        Assert.assertEquals("request", this.template.message.getPayload());
        Assert.assertSame("request", this.postProcessor.getMessage().getPayload());
        Assert.assertSame("response", actual);
        Assert.assertSame(this.myChannel, this.template.messageChannel);
    }

    private static class TestDestinationResolvingMessagingTemplate extends AbstractDestinationResolvingMessagingTemplate<MessageChannel> {
        private MessageChannel messageChannel;

        private Message<?> message;

        private Message<?> receiveMessage;

        private void setReceiveMessage(Message<?> receiveMessage) {
            this.receiveMessage = receiveMessage;
        }

        @Override
        protected void doSend(MessageChannel channel, Message<?> message) {
            this.messageChannel = channel;
            this.message = message;
        }

        @Override
        protected Message<?> doReceive(MessageChannel channel) {
            this.messageChannel = channel;
            return this.receiveMessage;
        }

        @Override
        protected Message<?> doSendAndReceive(MessageChannel channel, Message<?> requestMessage) {
            this.message = requestMessage;
            this.messageChannel = channel;
            return this.receiveMessage;
        }
    }
}

