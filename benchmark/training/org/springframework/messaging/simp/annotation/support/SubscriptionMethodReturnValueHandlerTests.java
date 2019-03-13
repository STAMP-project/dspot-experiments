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
package org.springframework.messaging.simp.annotation.support;


import SimpMessagingTemplate.CONVERSION_HINT_HEADER;
import com.fasterxml.jackson.annotation.JsonView;
import java.nio.charset.StandardCharsets;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeType;


/**
 * Test fixture for {@link SubscriptionMethodReturnValueHandler}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class SubscriptionMethodReturnValueHandlerTests {
    public static final MimeType MIME_TYPE = new MimeType("text", "plain", StandardCharsets.UTF_8);

    private static final String PAYLOAD = "payload";

    private SubscriptionMethodReturnValueHandler handler;

    private SubscriptionMethodReturnValueHandler jsonHandler;

    @Mock
    private MessageChannel messageChannel;

    @Captor
    private ArgumentCaptor<Message<?>> messageCaptor;

    private MethodParameter subscribeEventReturnType;

    private MethodParameter subscribeEventSendToReturnType;

    private MethodParameter messageMappingReturnType;

    private MethodParameter subscribeEventJsonViewReturnType;

    @Test
    public void supportsReturnType() throws Exception {
        Assert.assertTrue(this.handler.supportsReturnType(this.subscribeEventReturnType));
        Assert.assertFalse(this.handler.supportsReturnType(this.subscribeEventSendToReturnType));
        Assert.assertFalse(this.handler.supportsReturnType(this.messageMappingReturnType));
    }

    @Test
    public void testMessageSentToChannel() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        String subscriptionId = "subs1";
        String destination = "/dest";
        Message<?> inputMessage = createInputMessage(sessionId, subscriptionId, destination, null);
        this.handler.handleReturnValue(SubscriptionMethodReturnValueHandlerTests.PAYLOAD, this.subscribeEventReturnType, inputMessage);
        Mockito.verify(this.messageChannel).send(this.messageCaptor.capture());
        Assert.assertNotNull(this.messageCaptor.getValue());
        Message<?> message = this.messageCaptor.getValue();
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(message);
        Assert.assertNull("SimpMessageHeaderAccessor should have disabled id", headerAccessor.getId());
        Assert.assertNull("SimpMessageHeaderAccessor should have disabled timestamp", headerAccessor.getTimestamp());
        Assert.assertEquals(sessionId, headerAccessor.getSessionId());
        Assert.assertEquals(subscriptionId, headerAccessor.getSubscriptionId());
        Assert.assertEquals(destination, headerAccessor.getDestination());
        Assert.assertEquals(SubscriptionMethodReturnValueHandlerTests.MIME_TYPE, headerAccessor.getContentType());
        Assert.assertEquals(this.subscribeEventReturnType, headerAccessor.getHeader(CONVERSION_HINT_HEADER));
    }

    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testHeadersPassedToMessagingTemplate() throws Exception {
        String sessionId = "sess1";
        String subscriptionId = "subs1";
        String destination = "/dest";
        Message<?> inputMessage = createInputMessage(sessionId, subscriptionId, destination, null);
        MessageSendingOperations messagingTemplate = Mockito.mock(MessageSendingOperations.class);
        SubscriptionMethodReturnValueHandler handler = new SubscriptionMethodReturnValueHandler(messagingTemplate);
        handler.handleReturnValue(SubscriptionMethodReturnValueHandlerTests.PAYLOAD, this.subscribeEventReturnType, inputMessage);
        ArgumentCaptor<MessageHeaders> captor = ArgumentCaptor.forClass(MessageHeaders.class);
        Mockito.verify(messagingTemplate).convertAndSend(ArgumentMatchers.eq("/dest"), ArgumentMatchers.eq(SubscriptionMethodReturnValueHandlerTests.PAYLOAD), captor.capture());
        SimpMessageHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(captor.getValue(), SimpMessageHeaderAccessor.class);
        Assert.assertNotNull(headerAccessor);
        Assert.assertTrue(headerAccessor.isMutable());
        Assert.assertEquals(sessionId, headerAccessor.getSessionId());
        Assert.assertEquals(subscriptionId, headerAccessor.getSubscriptionId());
        Assert.assertEquals(this.subscribeEventReturnType, headerAccessor.getHeader(CONVERSION_HINT_HEADER));
    }

    @Test
    public void testJsonView() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        String subscriptionId = "subs1";
        String destination = "/dest";
        Message<?> inputMessage = createInputMessage(sessionId, subscriptionId, destination, null);
        this.jsonHandler.handleReturnValue(getJsonView(), this.subscribeEventJsonViewReturnType, inputMessage);
        Mockito.verify(this.messageChannel).send(this.messageCaptor.capture());
        Message<?> message = this.messageCaptor.getValue();
        Assert.assertNotNull(message);
        Assert.assertEquals("{\"withView1\":\"with\"}", new String(((byte[]) (message.getPayload())), StandardCharsets.UTF_8));
    }

    private interface MyJacksonView1 {}

    private interface MyJacksonView2 {}

    private static class JacksonViewBean {
        @JsonView(SubscriptionMethodReturnValueHandlerTests.MyJacksonView1.class)
        private String withView1;

        @JsonView(SubscriptionMethodReturnValueHandlerTests.MyJacksonView2.class)
        private String withView2;

        private String withoutView;

        public String getWithView1() {
            return withView1;
        }

        public void setWithView1(String withView1) {
            this.withView1 = withView1;
        }

        public String getWithView2() {
            return withView2;
        }

        public void setWithView2(String withView2) {
            this.withView2 = withView2;
        }

        public String getWithoutView() {
            return withoutView;
        }

        public void setWithoutView(String withoutView) {
            this.withoutView = withoutView;
        }
    }
}

