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
package org.springframework.messaging.support;


import MessageHeaders.CONTENT_TYPE;
import MimeTypeUtils.TEXT_PLAIN;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.SerializationTestUtils;


/**
 * Test fixture for {@link MessageHeaderAccessor}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @author Juergen Hoeller
 */
public class MessageHeaderAccessorTests {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void newEmptyHeaders() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        Assert.assertEquals(0, accessor.toMap().size());
    }

    @Test
    public void existingHeaders() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("bar", "baz");
        GenericMessage<String> message = new GenericMessage("payload", map);
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        MessageHeaders actual = accessor.getMessageHeaders();
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals("bar", actual.get("foo"));
        Assert.assertEquals("baz", actual.get("bar"));
    }

    @Test
    public void existingHeadersModification() throws InterruptedException {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("bar", "baz");
        GenericMessage<String> message = new GenericMessage("payload", map);
        Thread.sleep(50);
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        accessor.setHeader("foo", "BAR");
        MessageHeaders actual = accessor.getMessageHeaders();
        Assert.assertEquals(3, actual.size());
        Assert.assertNotEquals(message.getHeaders().getId(), actual.getId());
        Assert.assertEquals("BAR", actual.get("foo"));
        Assert.assertEquals("baz", actual.get("bar"));
    }

    @Test
    public void testRemoveHeader() {
        Message<?> message = new GenericMessage("payload", Collections.singletonMap("foo", "bar"));
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        accessor.removeHeader("foo");
        Map<String, Object> headers = accessor.toMap();
        Assert.assertFalse(headers.containsKey("foo"));
    }

    @Test
    public void testRemoveHeaderEvenIfNull() {
        Message<?> message = new GenericMessage("payload", Collections.singletonMap("foo", null));
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        accessor.removeHeader("foo");
        Map<String, Object> headers = accessor.toMap();
        Assert.assertFalse(headers.containsKey("foo"));
    }

    @Test
    public void removeHeaders() {
        Map<String, Object> map = new HashMap<>();
        map.put("foo", "bar");
        map.put("bar", "baz");
        GenericMessage<String> message = new GenericMessage("payload", map);
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        accessor.removeHeaders("fo*");
        MessageHeaders actual = accessor.getMessageHeaders();
        Assert.assertEquals(2, actual.size());
        Assert.assertNull(actual.get("foo"));
        Assert.assertEquals("baz", actual.get("bar"));
    }

    @Test
    public void copyHeaders() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("foo", "bar");
        GenericMessage<String> message = new GenericMessage("payload", map1);
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("foo", "BAR");
        map2.put("bar", "baz");
        accessor.copyHeaders(map2);
        MessageHeaders actual = accessor.getMessageHeaders();
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals("BAR", actual.get("foo"));
        Assert.assertEquals("baz", actual.get("bar"));
    }

    @Test
    public void copyHeadersIfAbsent() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("foo", "bar");
        GenericMessage<String> message = new GenericMessage("payload", map1);
        MessageHeaderAccessor accessor = new MessageHeaderAccessor(message);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("foo", "BAR");
        map2.put("bar", "baz");
        accessor.copyHeadersIfAbsent(map2);
        MessageHeaders actual = accessor.getMessageHeaders();
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals("bar", actual.get("foo"));
        Assert.assertEquals("baz", actual.get("bar"));
    }

    @Test
    public void copyHeadersFromNullMap() {
        MessageHeaderAccessor headers = new MessageHeaderAccessor();
        headers.copyHeaders(null);
        headers.copyHeadersIfAbsent(null);
        Assert.assertEquals(1, headers.getMessageHeaders().size());
        Assert.assertEquals(Collections.singleton("id"), headers.getMessageHeaders().keySet());
    }

    @Test
    public void toMap() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("foo", "bar1");
        Map<String, Object> map1 = accessor.toMap();
        accessor.setHeader("foo", "bar2");
        Map<String, Object> map2 = accessor.toMap();
        accessor.setHeader("foo", "bar3");
        Map<String, Object> map3 = accessor.toMap();
        Assert.assertEquals(1, map1.size());
        Assert.assertEquals(1, map2.size());
        Assert.assertEquals(1, map3.size());
        Assert.assertEquals("bar1", map1.get("foo"));
        Assert.assertEquals("bar2", map2.get("foo"));
        Assert.assertEquals("bar3", map3.get("foo"));
    }

    @Test
    public void leaveMutable() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("foo", "bar");
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        Message<?> message = MessageBuilder.createMessage("payload", headers);
        accessor.setHeader("foo", "baz");
        Assert.assertEquals("baz", headers.get("foo"));
        Assert.assertSame(accessor, MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class));
    }

    @Test
    public void leaveMutableDefaultBehavior() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setHeader("foo", "bar");
        MessageHeaders headers = accessor.getMessageHeaders();
        Message<?> message = MessageBuilder.createMessage("payload", headers);
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("Already immutable");
        accessor.setLeaveMutable(true);
        this.thrown.expect(IllegalStateException.class);
        this.thrown.expectMessage("Already immutable");
        accessor.setHeader("foo", "baz");
        Assert.assertEquals("bar", headers.get("foo"));
        Assert.assertSame(accessor, MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class));
    }

    @Test
    public void getAccessor() {
        MessageHeaderAccessor expected = new MessageHeaderAccessor();
        Message<?> message = MessageBuilder.createMessage("payload", expected.getMessageHeaders());
        Assert.assertSame(expected, MessageHeaderAccessor.getAccessor(message, MessageHeaderAccessor.class));
    }

    @Test
    public void getMutableAccessorSameInstance() {
        MessageHeaderAccessorTests.TestMessageHeaderAccessor expected = new MessageHeaderAccessorTests.TestMessageHeaderAccessor();
        setLeaveMutable(true);
        Message<?> message = MessageBuilder.createMessage("payload", getMessageHeaders());
        MessageHeaderAccessor actual = MessageHeaderAccessor.getMutableAccessor(message);
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isMutable());
        Assert.assertSame(expected, actual);
    }

    @Test
    public void getMutableAccessorNewInstance() {
        Message<?> message = MessageBuilder.withPayload("payload").build();
        MessageHeaderAccessor actual = MessageHeaderAccessor.getMutableAccessor(message);
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isMutable());
    }

    @Test
    public void getMutableAccessorNewInstanceMatchingType() {
        MessageHeaderAccessorTests.TestMessageHeaderAccessor expected = new MessageHeaderAccessorTests.TestMessageHeaderAccessor();
        Message<?> message = MessageBuilder.createMessage("payload", getMessageHeaders());
        MessageHeaderAccessor actual = MessageHeaderAccessor.getMutableAccessor(message);
        Assert.assertNotNull(actual);
        Assert.assertTrue(actual.isMutable());
        Assert.assertEquals(MessageHeaderAccessorTests.TestMessageHeaderAccessor.class, actual.getClass());
    }

    @Test
    public void timestampEnabled() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setEnableTimestamp(true);
        Assert.assertNotNull(accessor.getMessageHeaders().getTimestamp());
    }

    @Test
    public void timestampDefaultBehavior() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        Assert.assertNull(accessor.getMessageHeaders().getTimestamp());
    }

    @Test
    public void idGeneratorCustom() {
        final UUID id = new UUID(0L, 23L);
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setIdGenerator(() -> id);
        Assert.assertSame(id, accessor.getMessageHeaders().getId());
    }

    @Test
    public void idGeneratorDefaultBehavior() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        Assert.assertNotNull(accessor.getMessageHeaders().getId());
    }

    @Test
    public void idTimestampWithMutableHeaders() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setIdGenerator(() -> MessageHeaders.ID_VALUE_NONE);
        accessor.setEnableTimestamp(false);
        accessor.setLeaveMutable(true);
        MessageHeaders headers = accessor.getMessageHeaders();
        Assert.assertNull(headers.getId());
        Assert.assertNull(headers.getTimestamp());
        final UUID id = new UUID(0L, 23L);
        accessor.setIdGenerator(() -> id);
        accessor.setEnableTimestamp(true);
        accessor.setImmutable();
        Assert.assertSame(id, accessor.getMessageHeaders().getId());
        Assert.assertNotNull(headers.getTimestamp());
    }

    @Test
    public void getShortLogMessagePayload() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setContentType(TEXT_PLAIN);
        String expected = "headers={contentType=text/plain} payload=p";
        Assert.assertEquals(expected, accessor.getShortLogMessage("p"));
        Assert.assertEquals(expected, accessor.getShortLogMessage("p".getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(expected, accessor.getShortLogMessage(new Object() {
            @Override
            public String toString() {
                return "p";
            }
        }));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            sb.append("a");
        }
        final String payload = (sb.toString()) + " > 80";
        String actual = accessor.getShortLogMessage(payload);
        Assert.assertEquals((("headers={contentType=text/plain} payload=" + sb) + "...(truncated)"), actual);
        actual = accessor.getShortLogMessage(payload.getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals((("headers={contentType=text/plain} payload=" + sb) + "...(truncated)"), actual);
        actual = accessor.getShortLogMessage(new Object() {
            @Override
            public String toString() {
                return payload;
            }
        });
        Assert.assertThat(actual, CoreMatchers.startsWith((("headers={contentType=text/plain} payload=" + (getClass().getName())) + "$")));
    }

    @Test
    public void getDetailedLogMessagePayload() {
        MessageHeaderAccessor accessor = new MessageHeaderAccessor();
        accessor.setContentType(TEXT_PLAIN);
        String expected = "headers={contentType=text/plain} payload=p";
        Assert.assertEquals(expected, accessor.getDetailedLogMessage("p"));
        Assert.assertEquals(expected, accessor.getDetailedLogMessage("p".getBytes(StandardCharsets.UTF_8)));
        Assert.assertEquals(expected, accessor.getDetailedLogMessage(new Object() {
            @Override
            public String toString() {
                return "p";
            }
        }));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            sb.append("a");
        }
        final String payload = (sb.toString()) + " > 80";
        String actual = accessor.getDetailedLogMessage(payload);
        Assert.assertEquals((("headers={contentType=text/plain} payload=" + sb) + " > 80"), actual);
        actual = accessor.getDetailedLogMessage(payload.getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals((("headers={contentType=text/plain} payload=" + sb) + " > 80"), actual);
        actual = accessor.getDetailedLogMessage(new Object() {
            @Override
            public String toString() {
                return payload;
            }
        });
        Assert.assertEquals((("headers={contentType=text/plain} payload=" + sb) + " > 80"), actual);
    }

    @Test
    public void serializeMutableHeaders() throws Exception {
        Map<String, Object> headers = new HashMap<>();
        headers.put("foo", "bar");
        Message<String> message = new GenericMessage("test", headers);
        MessageHeaderAccessor mutableAccessor = MessageHeaderAccessor.getMutableAccessor(message);
        mutableAccessor.setContentType(TEXT_PLAIN);
        message = new GenericMessage(message.getPayload(), mutableAccessor.getMessageHeaders());
        Message<?> output = ((Message<?>) (SerializationTestUtils.serializeAndDeserialize(message)));
        Assert.assertEquals("test", output.getPayload());
        Assert.assertEquals("bar", output.getHeaders().get("foo"));
        Assert.assertNotNull(output.getHeaders().get(CONTENT_TYPE));
    }

    public static class TestMessageHeaderAccessor extends MessageHeaderAccessor {
        public TestMessageHeaderAccessor() {
        }

        private TestMessageHeaderAccessor(Message<?> message) {
            super(message);
        }

        public static MessageHeaderAccessorTests.TestMessageHeaderAccessor wrap(Message<?> message) {
            return new MessageHeaderAccessorTests.TestMessageHeaderAccessor(message);
        }

        @Override
        protected MessageHeaderAccessorTests.TestMessageHeaderAccessor createAccessor(Message<?> message) {
            return MessageHeaderAccessorTests.TestMessageHeaderAccessor.wrap(message);
        }
    }
}

