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
package org.springframework.messaging.converter;


import MessageHeaders.CONTENT_TYPE;
import MimeTypeUtils.APPLICATION_JSON;
import MimeTypeUtils.TEXT_PLAIN;
import SimpMessageType.MESSAGE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeType;


/**
 * Unit tests for
 * {@link org.springframework.messaging.converter.AbstractMessageConverter}.
 *
 * @author Rossen Stoyanchev
 */
public class MessageConverterTests {
    private MessageConverterTests.TestMessageConverter converter = new MessageConverterTests.TestMessageConverter();

    @Test
    public void supportsTargetClass() {
        Message<String> message = MessageBuilder.withPayload("ABC").build();
        Assert.assertEquals("success-from", this.converter.fromMessage(message, String.class));
        Assert.assertNull(this.converter.fromMessage(message, Integer.class));
    }

    @Test
    public void supportsMimeType() {
        Message<String> message = MessageBuilder.withPayload("ABC").setHeader(CONTENT_TYPE, TEXT_PLAIN).build();
        Assert.assertEquals("success-from", this.converter.fromMessage(message, String.class));
    }

    @Test
    public void supportsMimeTypeNotSupported() {
        Message<String> message = MessageBuilder.withPayload("ABC").setHeader(CONTENT_TYPE, APPLICATION_JSON).build();
        Assert.assertNull(this.converter.fromMessage(message, String.class));
    }

    @Test
    public void supportsMimeTypeNotSpecified() {
        Message<String> message = MessageBuilder.withPayload("ABC").build();
        Assert.assertEquals("success-from", this.converter.fromMessage(message, String.class));
    }

    @Test
    public void supportsMimeTypeNoneConfigured() {
        Message<String> message = MessageBuilder.withPayload("ABC").setHeader(CONTENT_TYPE, APPLICATION_JSON).build();
        this.converter = new MessageConverterTests.TestMessageConverter(Collections.<MimeType>emptyList());
        Assert.assertEquals("success-from", this.converter.fromMessage(message, String.class));
    }

    @Test
    public void canConvertFromStrictContentTypeMatch() {
        this.converter = new MessageConverterTests.TestMessageConverter(Arrays.asList(TEXT_PLAIN));
        setStrictContentTypeMatch(true);
        Message<String> message = MessageBuilder.withPayload("ABC").build();
        Assert.assertFalse(this.converter.canConvertFrom(message, String.class));
        message = MessageBuilder.withPayload("ABC").setHeader(CONTENT_TYPE, TEXT_PLAIN).build();
        Assert.assertTrue(this.converter.canConvertFrom(message, String.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setStrictContentTypeMatchWithNoSupportedMimeTypes() {
        this.converter = new MessageConverterTests.TestMessageConverter(Collections.<MimeType>emptyList());
        setStrictContentTypeMatch(true);
    }

    @Test
    public void toMessageWithHeaders() {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        MessageHeaders headers = new MessageHeaders(map);
        Message<?> message = this.converter.toMessage("ABC", headers);
        Assert.assertNotNull(message.getHeaders().getId());
        Assert.assertNotNull(message.getHeaders().getTimestamp());
        Assert.assertEquals(TEXT_PLAIN, message.getHeaders().get(CONTENT_TYPE));
        Assert.assertEquals("bar", message.getHeaders().get("foo"));
    }

    @Test
    public void toMessageWithMutableMessageHeaders() {
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create(MESSAGE);
        accessor.setHeader("foo", "bar");
        accessor.setNativeHeader("fooNative", "barNative");
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        Message<?> message = this.converter.toMessage("ABC", headers);
        Assert.assertSame(headers, message.getHeaders());
        Assert.assertNull(message.getHeaders().getId());
        Assert.assertNull(message.getHeaders().getTimestamp());
        Assert.assertEquals(TEXT_PLAIN, message.getHeaders().get(CONTENT_TYPE));
    }

    @Test
    public void toMessageContentTypeHeader() {
        Message<?> message = toMessage("ABC", null);
        Assert.assertEquals(TEXT_PLAIN, message.getHeaders().get(CONTENT_TYPE));
    }

    private static class TestMessageConverter extends AbstractMessageConverter {
        public TestMessageConverter() {
            super(TEXT_PLAIN);
        }

        public TestMessageConverter(Collection<MimeType> supportedMimeTypes) {
            super(supportedMimeTypes);
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return String.class.equals(clazz);
        }

        @Override
        protected Object convertFromInternal(Message<?> message, Class<?> targetClass, @Nullable
        Object conversionHint) {
            return "success-from";
        }

        @Override
        protected Object convertToInternal(Object payload, @Nullable
        MessageHeaders headers, @Nullable
        Object conversionHint) {
            return "success-to";
        }
    }
}

