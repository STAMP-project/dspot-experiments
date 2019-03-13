/**
 * Copyright 2002-2014 the original author or authors.
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


import MessageHeaders.CONTENT_TYPE;
import MimeTypeUtils.APPLICATION_XML;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeType;


/**
 * Unit tests for {@link AbstractMessageSendingTemplate}.
 *
 * @author Rossen Stoyanchev
 */
public class MessageSendingTemplateTests {
    private MessageSendingTemplateTests.TestMessageSendingTemplate template;

    private TestMessagePostProcessor postProcessor;

    private Map<String, Object> headers;

    @Test
    public void send() {
        Message<?> message = new org.springframework.messaging.support.GenericMessage<Object>("payload");
        setDefaultDestination("home");
        this.template.send(message);
        Assert.assertEquals("home", this.template.destination);
        Assert.assertSame(message, this.template.message);
    }

    @Test
    public void sendToDestination() {
        Message<?> message = new org.springframework.messaging.support.GenericMessage<Object>("payload");
        this.template.send("somewhere", message);
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertSame(message, this.template.message);
    }

    @Test(expected = IllegalStateException.class)
    public void sendMissingDestination() {
        Message<?> message = new org.springframework.messaging.support.GenericMessage<Object>("payload");
        this.template.send(message);
    }

    @Test
    public void convertAndSend() {
        this.template.convertAndSend("somewhere", "payload", headers, this.postProcessor);
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("value", this.template.message.getHeaders().get("key"));
        Assert.assertEquals("payload", this.template.message.getPayload());
        Assert.assertNotNull(this.postProcessor.getMessage());
        Assert.assertSame(this.template.message, this.postProcessor.getMessage());
    }

    @Test
    public void convertAndSendPayload() {
        setDefaultDestination("home");
        this.template.convertAndSend("payload");
        Assert.assertEquals("home", this.template.destination);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("expected 'id' and 'timestamp' headers only", 2, this.template.message.getHeaders().size());
        Assert.assertEquals("payload", this.template.message.getPayload());
    }

    @Test
    public void convertAndSendPayloadToDestination() {
        this.template.convertAndSend("somewhere", "payload");
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("expected 'id' and 'timestamp' headers only", 2, this.template.message.getHeaders().size());
        Assert.assertEquals("payload", this.template.message.getPayload());
    }

    @Test
    public void convertAndSendPayloadAndHeadersToDestination() {
        this.template.convertAndSend("somewhere", "payload", headers);
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("value", this.template.message.getHeaders().get("key"));
        Assert.assertEquals("payload", this.template.message.getPayload());
    }

    @Test
    public void convertAndSendPayloadAndMutableHeadersToDestination() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("foo", "bar");
        accessor.setLeaveMutable(true);
        MessageHeaders messageHeaders = accessor.getMessageHeaders();
        this.template.setMessageConverter(new StringMessageConverter());
        this.template.convertAndSend("somewhere", "payload", messageHeaders);
        MessageHeaders actual = this.template.message.getHeaders();
        Assert.assertSame(messageHeaders, actual);
        Assert.assertEquals(new MimeType("text", "plain", StandardCharsets.UTF_8), actual.get(CONTENT_TYPE));
        Assert.assertEquals("bar", actual.get("foo"));
    }

    @Test
    public void convertAndSendPayloadWithPostProcessor() {
        setDefaultDestination("home");
        this.template.convertAndSend(((Object) ("payload")), this.postProcessor);
        Assert.assertEquals("home", this.template.destination);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("expected 'id' and 'timestamp' headers only", 2, this.template.message.getHeaders().size());
        Assert.assertEquals("payload", this.template.message.getPayload());
        Assert.assertNotNull(this.postProcessor.getMessage());
        Assert.assertSame(this.template.message, this.postProcessor.getMessage());
    }

    @Test
    public void convertAndSendPayloadWithPostProcessorToDestination() {
        this.template.convertAndSend("somewhere", "payload", this.postProcessor);
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertNotNull(this.template.message);
        Assert.assertEquals("expected 'id' and 'timestamp' headers only", 2, this.template.message.getHeaders().size());
        Assert.assertEquals("payload", this.template.message.getPayload());
        Assert.assertNotNull(this.postProcessor.getMessage());
        Assert.assertSame(this.template.message, this.postProcessor.getMessage());
    }

    @Test(expected = MessageConversionException.class)
    public void convertAndSendNoMatchingConverter() {
        MessageConverter converter = new org.springframework.messaging.converter.CompositeMessageConverter(Arrays.<MessageConverter>asList(new MappingJackson2MessageConverter()));
        this.template.setMessageConverter(converter);
        this.headers.put(CONTENT_TYPE, APPLICATION_XML);
        this.template.convertAndSend("home", "payload", new MessageHeaders(this.headers));
    }

    private static class TestMessageSendingTemplate extends AbstractMessageSendingTemplate<String> {
        private String destination;

        private Message<?> message;

        @Override
        protected void doSend(String destination, Message<?> message) {
            this.destination = destination;
            this.message = message;
        }
    }
}

