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


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.GenericMessageConverter;
import org.springframework.messaging.converter.MessageConversionException;


/**
 * Unit tests for receiving operations in {@link AbstractMessagingTemplate}.
 *
 * @author Rossen Stoyanchev
 * @see MessageRequestReplyTemplateTests
 */
public class MessageReceivingTemplateTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private MessageReceivingTemplateTests.TestMessagingTemplate template;

    @Test
    public void receive() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage("payload");
        setDefaultDestination("home");
        this.template.setReceiveMessage(expected);
        Message<?> actual = this.template.receive();
        Assert.assertEquals("home", this.template.destination);
        Assert.assertSame(expected, actual);
    }

    @Test(expected = IllegalStateException.class)
    public void receiveMissingDefaultDestination() {
        this.template.receive();
    }

    @Test
    public void receiveFromDestination() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage("payload");
        this.template.setReceiveMessage(expected);
        Message<?> actual = this.template.receive("somewhere");
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertSame(expected, actual);
    }

    @Test
    public void receiveAndConvert() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage("payload");
        setDefaultDestination("home");
        this.template.setReceiveMessage(expected);
        String payload = this.template.receiveAndConvert(String.class);
        Assert.assertEquals("home", this.template.destination);
        Assert.assertSame("payload", payload);
    }

    @Test
    public void receiveAndConvertFromDestination() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage("payload");
        this.template.setReceiveMessage(expected);
        String payload = this.template.receiveAndConvert("somewhere", String.class);
        Assert.assertEquals("somewhere", this.template.destination);
        Assert.assertSame("payload", payload);
    }

    @Test
    public void receiveAndConvertFailed() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage("not a number test");
        this.template.setReceiveMessage(expected);
        this.template.setMessageConverter(new GenericMessageConverter());
        thrown.expect(MessageConversionException.class);
        thrown.expectCause(CoreMatchers.isA(ConversionFailedException.class));
        this.template.receiveAndConvert("somewhere", Integer.class);
    }

    @Test
    public void receiveAndConvertNoConverter() {
        Message<?> expected = new org.springframework.messaging.support.GenericMessage("payload");
        setDefaultDestination("home");
        this.template.setReceiveMessage(expected);
        this.template.setMessageConverter(new GenericMessageConverter());
        try {
            this.template.receiveAndConvert(java.io.Writer.class);
        } catch (MessageConversionException ex) {
            Assert.assertTrue((("Invalid exception message '" + (ex.getMessage())) + "'"), ex.getMessage().contains("payload"));
            Assert.assertSame(expected, ex.getFailedMessage());
        }
    }

    private static class TestMessagingTemplate extends AbstractMessagingTemplate<String> {
        private String destination;

        private Message<?> receiveMessage;

        private void setReceiveMessage(Message<?> receiveMessage) {
            this.receiveMessage = receiveMessage;
        }

        @Override
        protected void doSend(String destination, Message<?> message) {
        }

        @Override
        protected Message<?> doReceive(String destination) {
            this.destination = destination;
            return this.receiveMessage;
        }

        @Override
        protected Message<?> doSendAndReceive(String destination, Message<?> requestMessage) {
            this.destination = destination;
            return null;
        }
    }
}

