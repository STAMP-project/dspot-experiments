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
package org.springframework.messaging.simp.stomp;


import MimeTypeUtils.APPLICATION_JSON;
import StompCommand.CONNECT;
import StompCommand.CONNECTED;
import StompCommand.MESSAGE;
import StompCommand.SEND;
import StompCommand.SUBSCRIBE;
import StompCommand.UNSUBSCRIBE;
import StompHeaderAccessor.DESTINATION_HEADER;
import StompHeaderAccessor.STOMP_CONTENT_TYPE_HEADER;
import StompHeaderAccessor.STOMP_DESTINATION_HEADER;
import StompHeaderAccessor.STOMP_ID_HEADER;
import StompHeaderAccessor.STOMP_LOGIN_HEADER;
import StompHeaderAccessor.STOMP_MESSAGE_ID_HEADER;
import StompHeaderAccessor.STOMP_PASSCODE_HEADER;
import StompHeaderAccessor.STOMP_SUBSCRIPTION_HEADER;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.IdTimestampMessageHeaderInitializer;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.MimeType;
import org.springframework.util.MultiValueMap;


/**
 * Unit tests for {@link StompHeaderAccessor}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class StompHeaderAccessorTests {
    @Test
    public void createWithCommand() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(CONNECTED);
        Assert.assertEquals(CONNECTED, accessor.getCommand());
        accessor = StompHeaderAccessor.create(CONNECTED, new org.springframework.util.LinkedMultiValueMap());
        Assert.assertEquals(CONNECTED, accessor.getCommand());
    }

    @Test
    public void createWithSubscribeNativeHeaders() {
        MultiValueMap<String, String> extHeaders = new org.springframework.util.LinkedMultiValueMap();
        extHeaders.add(STOMP_ID_HEADER, "s1");
        extHeaders.add(STOMP_DESTINATION_HEADER, "/d");
        StompHeaderAccessor headers = StompHeaderAccessor.create(SUBSCRIBE, extHeaders);
        Assert.assertEquals(SUBSCRIBE, headers.getCommand());
        Assert.assertEquals(SimpMessageType.SUBSCRIBE, headers.getMessageType());
        Assert.assertEquals("/d", headers.getDestination());
        Assert.assertEquals("s1", headers.getSubscriptionId());
    }

    @Test
    public void createWithUnubscribeNativeHeaders() {
        MultiValueMap<String, String> extHeaders = new org.springframework.util.LinkedMultiValueMap();
        extHeaders.add(STOMP_ID_HEADER, "s1");
        StompHeaderAccessor headers = StompHeaderAccessor.create(UNSUBSCRIBE, extHeaders);
        Assert.assertEquals(UNSUBSCRIBE, headers.getCommand());
        Assert.assertEquals(SimpMessageType.UNSUBSCRIBE, headers.getMessageType());
        Assert.assertEquals("s1", headers.getSubscriptionId());
    }

    @Test
    public void createWithMessageFrameNativeHeaders() {
        MultiValueMap<String, String> extHeaders = new org.springframework.util.LinkedMultiValueMap();
        extHeaders.add(DESTINATION_HEADER, "/d");
        extHeaders.add(STOMP_SUBSCRIPTION_HEADER, "s1");
        extHeaders.add(STOMP_CONTENT_TYPE_HEADER, "application/json");
        StompHeaderAccessor headers = StompHeaderAccessor.create(MESSAGE, extHeaders);
        Assert.assertEquals(MESSAGE, headers.getCommand());
        Assert.assertEquals(SimpMessageType.MESSAGE, headers.getMessageType());
        Assert.assertEquals("s1", headers.getSubscriptionId());
    }

    @Test
    public void createWithConnectNativeHeaders() {
        MultiValueMap<String, String> extHeaders = new org.springframework.util.LinkedMultiValueMap();
        extHeaders.add(STOMP_LOGIN_HEADER, "joe");
        extHeaders.add(STOMP_PASSCODE_HEADER, "joe123");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(CONNECT, extHeaders);
        Assert.assertEquals(CONNECT, headerAccessor.getCommand());
        Assert.assertEquals(SimpMessageType.CONNECT, headerAccessor.getMessageType());
        Assert.assertNotNull(headerAccessor.getHeader("stompCredentials"));
        Assert.assertEquals("joe", headerAccessor.getLogin());
        Assert.assertEquals("joe123", headerAccessor.getPasscode());
        Assert.assertThat(headerAccessor.toString(), CoreMatchers.containsString("passcode=[PROTECTED]"));
        Map<String, List<String>> output = headerAccessor.toNativeHeaderMap();
        Assert.assertEquals("joe", output.get(STOMP_LOGIN_HEADER).get(0));
        Assert.assertEquals("PROTECTED", output.get(STOMP_PASSCODE_HEADER).get(0));
    }

    @Test
    public void toNativeHeadersSubscribe() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(SUBSCRIBE);
        headers.setSubscriptionId("s1");
        headers.setDestination("/d");
        Map<String, List<String>> actual = headers.toNativeHeaderMap();
        Assert.assertEquals(2, actual.size());
        Assert.assertEquals("s1", actual.get(STOMP_ID_HEADER).get(0));
        Assert.assertEquals("/d", actual.get(STOMP_DESTINATION_HEADER).get(0));
    }

    @Test
    public void toNativeHeadersUnsubscribe() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(UNSUBSCRIBE);
        headers.setSubscriptionId("s1");
        Map<String, List<String>> actual = headers.toNativeHeaderMap();
        Assert.assertEquals(1, actual.size());
        Assert.assertEquals("s1", actual.get(STOMP_ID_HEADER).get(0));
    }

    @Test
    public void toNativeHeadersMessageFrame() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(MESSAGE);
        headers.setSubscriptionId("s1");
        headers.setDestination("/d");
        headers.setContentType(APPLICATION_JSON);
        headers.updateStompCommandAsServerMessage();
        Map<String, List<String>> actual = headers.toNativeHeaderMap();
        Assert.assertEquals(actual.toString(), 4, actual.size());
        Assert.assertEquals("s1", actual.get(STOMP_SUBSCRIPTION_HEADER).get(0));
        Assert.assertEquals("/d", actual.get(STOMP_DESTINATION_HEADER).get(0));
        Assert.assertEquals("application/json", actual.get(STOMP_CONTENT_TYPE_HEADER).get(0));
        Assert.assertNotNull("message-id was not created", actual.get(STOMP_MESSAGE_ID_HEADER).get(0));
    }

    @Test
    public void toNativeHeadersContentType() {
        SimpMessageHeaderAccessor simpHeaderAccessor = SimpMessageHeaderAccessor.create();
        simpHeaderAccessor.setContentType(MimeType.valueOf("application/atom+xml"));
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], simpHeaderAccessor.getMessageHeaders());
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);
        Map<String, List<String>> map = stompHeaderAccessor.toNativeHeaderMap();
        Assert.assertEquals("application/atom+xml", map.get(STOMP_CONTENT_TYPE_HEADER).get(0));
    }

    @Test
    public void encodeConnectWithLoginAndPasscode() throws UnsupportedEncodingException {
        MultiValueMap<String, String> extHeaders = new org.springframework.util.LinkedMultiValueMap();
        extHeaders.add(STOMP_LOGIN_HEADER, "joe");
        extHeaders.add(STOMP_PASSCODE_HEADER, "joe123");
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(CONNECT, extHeaders);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
        byte[] bytes = new StompEncoder().encode(message);
        Assert.assertEquals("CONNECT\nlogin:joe\npasscode:joe123\n\n\u0000", new String(bytes, "UTF-8"));
    }

    @Test
    public void modifyCustomNativeHeader() {
        MultiValueMap<String, String> extHeaders = new org.springframework.util.LinkedMultiValueMap();
        extHeaders.add(STOMP_ID_HEADER, "s1");
        extHeaders.add(STOMP_DESTINATION_HEADER, "/d");
        extHeaders.add("accountId", "ABC123");
        StompHeaderAccessor headers = StompHeaderAccessor.create(SUBSCRIBE, extHeaders);
        String accountId = headers.getFirstNativeHeader("accountId");
        headers.setNativeHeader("accountId", accountId.toLowerCase());
        Map<String, List<String>> actual = headers.toNativeHeaderMap();
        Assert.assertEquals(3, actual.size());
        Assert.assertEquals("s1", actual.get(STOMP_ID_HEADER).get(0));
        Assert.assertEquals("/d", actual.get(STOMP_DESTINATION_HEADER).get(0));
        Assert.assertNotNull("abc123", actual.get("accountId").get(0));
    }

    @Test
    public void messageIdAndTimestampDefaultBehavior() {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(SEND);
        MessageHeaders headers = headerAccessor.getMessageHeaders();
        Assert.assertNull(headers.getId());
        Assert.assertNull(headers.getTimestamp());
    }

    @Test
    public void messageIdAndTimestampEnabled() {
        IdTimestampMessageHeaderInitializer headerInitializer = new IdTimestampMessageHeaderInitializer();
        headerInitializer.setIdGenerator(new AlternativeJdkIdGenerator());
        headerInitializer.setEnableTimestamp(true);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(SEND);
        headerInitializer.initHeaders(headerAccessor);
        Assert.assertNotNull(headerAccessor.getMessageHeaders().getId());
        Assert.assertNotNull(headerAccessor.getMessageHeaders().getTimestamp());
    }

    @Test
    public void getAccessor() {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.create(CONNECT);
        Message<byte[]> message = MessageBuilder.createMessage(new byte[0], headerAccessor.getMessageHeaders());
        Assert.assertSame(headerAccessor, MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class));
    }

    @Test
    public void getShortLogMessage() {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(SEND);
        accessor.setDestination("/foo");
        accessor.setContentType(APPLICATION_JSON);
        accessor.setSessionId("123");
        String actual = accessor.getShortLogMessage("payload".getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals("SEND /foo session=123 application/json payload=payload", actual);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 80; i++) {
            sb.append("a");
        }
        final String payload = (sb.toString()) + " > 80";
        actual = accessor.getShortLogMessage(payload.getBytes(StandardCharsets.UTF_8));
        Assert.assertEquals((("SEND /foo session=123 application/json payload=" + sb) + "...(truncated)"), actual);
    }
}

