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


import DestinationVariableMethodArgumentResolver.DESTINATION_TEMPLATE_VARIABLES_HEADER;
import SimpMessagingTemplate.CONVERSION_HINT_HEADER;
import com.fasterxml.jackson.annotation.JsonView;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.security.auth.Subject;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.user.DestinationUserNameProvider;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.util.MimeType;


/**
 * Test fixture for {@link SendToMethodReturnValueHandlerTests}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @author Stephane Nicoll
 */
public class SendToMethodReturnValueHandlerTests {
    private static final MimeType MIME_TYPE = new MimeType("text", "plain", StandardCharsets.UTF_8);

    private static final String PAYLOAD = "payload";

    private SendToMethodReturnValueHandler handler;

    private SendToMethodReturnValueHandler handlerAnnotationNotRequired;

    private SendToMethodReturnValueHandler jsonHandler;

    @Mock
    private MessageChannel messageChannel;

    @Captor
    private ArgumentCaptor<Message<?>> messageCaptor;

    private MethodParameter noAnnotationsReturnType = param("handleNoAnnotations");

    private MethodParameter sendToReturnType = param("handleAndSendTo");

    private MethodParameter sendToDefaultDestReturnType = param("handleAndSendToDefaultDest");

    private MethodParameter sendToWithPlaceholdersReturnType = param("handleAndSendToWithPlaceholders");

    private MethodParameter sendToUserReturnType = param("handleAndSendToUser");

    private MethodParameter sendToUserInSessionReturnType = param("handleAndSendToUserInSession");

    private MethodParameter sendToSendToUserReturnType = param("handleAndSendToAndSendToUser");

    private MethodParameter sendToUserDefaultDestReturnType = param("handleAndSendToUserDefaultDest");

    private MethodParameter sendToUserInSessionDefaultDestReturnType = param("handleAndSendToUserDefaultDestInSession");

    private MethodParameter jsonViewReturnType = param("handleAndSendToJsonView");

    private MethodParameter defaultNoAnnotation = SendToMethodReturnValueHandlerTests.param(SendToMethodReturnValueHandlerTests.SendToTestBean.class, "handleNoAnnotation");

    private MethodParameter defaultEmptyAnnotation = SendToMethodReturnValueHandlerTests.param(SendToMethodReturnValueHandlerTests.SendToTestBean.class, "handleAndSendToDefaultDest");

    private MethodParameter defaultOverrideAnnotation = SendToMethodReturnValueHandlerTests.param(SendToMethodReturnValueHandlerTests.SendToTestBean.class, "handleAndSendToOverride");

    private MethodParameter userDefaultNoAnnotation = SendToMethodReturnValueHandlerTests.param(SendToMethodReturnValueHandlerTests.SendToUserTestBean.class, "handleNoAnnotation");

    private MethodParameter userDefaultEmptyAnnotation = SendToMethodReturnValueHandlerTests.param(SendToMethodReturnValueHandlerTests.SendToUserTestBean.class, "handleAndSendToDefaultDest");

    private MethodParameter userDefaultOverrideAnnotation = SendToMethodReturnValueHandlerTests.param(SendToMethodReturnValueHandlerTests.SendToUserTestBean.class, "handleAndSendToOverride");

    @Test
    public void supportsReturnType() throws Exception {
        Assert.assertTrue(this.handler.supportsReturnType(this.sendToReturnType));
        Assert.assertTrue(this.handler.supportsReturnType(this.sendToUserReturnType));
        Assert.assertFalse(this.handler.supportsReturnType(this.noAnnotationsReturnType));
        Assert.assertTrue(this.handlerAnnotationNotRequired.supportsReturnType(this.noAnnotationsReturnType));
        Assert.assertTrue(this.handler.supportsReturnType(this.defaultNoAnnotation));
        Assert.assertTrue(this.handler.supportsReturnType(this.defaultEmptyAnnotation));
        Assert.assertTrue(this.handler.supportsReturnType(this.defaultOverrideAnnotation));
        Assert.assertTrue(this.handler.supportsReturnType(this.userDefaultNoAnnotation));
        Assert.assertTrue(this.handler.supportsReturnType(this.userDefaultEmptyAnnotation));
        Assert.assertTrue(this.handler.supportsReturnType(this.userDefaultOverrideAnnotation));
    }

    @Test
    public void sendToNoAnnotations() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", "/app", "/dest", null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.noAnnotationsReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(this.noAnnotationsReturnType, sessionId, 0, "/topic/dest");
    }

    @Test
    public void sendTo() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        assertResponse(this.sendToReturnType, sessionId, 0, "/dest1");
        assertResponse(this.sendToReturnType, sessionId, 1, "/dest2");
    }

    @Test
    public void sendToDefaultDestination() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", "/app", "/dest", null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToDefaultDestReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(this.sendToDefaultDestReturnType, sessionId, 0, "/topic/dest");
    }

    @Test
    public void sendToClassDefaultNoAnnotation() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.defaultNoAnnotation, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(this.defaultNoAnnotation, sessionId, 0, "/dest-default");
    }

    @Test
    public void sendToClassDefaultEmptyAnnotation() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.defaultEmptyAnnotation, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(this.defaultEmptyAnnotation, sessionId, 0, "/dest-default");
    }

    @Test
    public void sendToClassDefaultOverride() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.defaultOverrideAnnotation, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        assertResponse(this.defaultOverrideAnnotation, sessionId, 0, "/dest3");
        assertResponse(this.defaultOverrideAnnotation, sessionId, 1, "/dest4");
    }

    @Test
    public void sendToUserClassDefaultNoAnnotation() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.userDefaultNoAnnotation, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(this.userDefaultNoAnnotation, sessionId, 0, "/user/sess1/dest-default");
    }

    @Test
    public void sendToUserClassDefaultEmptyAnnotation() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.userDefaultEmptyAnnotation, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(this.userDefaultEmptyAnnotation, sessionId, 0, "/user/sess1/dest-default");
    }

    @Test
    public void sendToUserClassDefaultOverride() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.userDefaultOverrideAnnotation, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        assertResponse(this.userDefaultOverrideAnnotation, sessionId, 0, "/user/sess1/dest3");
        assertResponse(this.userDefaultOverrideAnnotation, sessionId, 1, "/user/sess1/dest4");
    }

    // SPR-14238
    @Test
    public void sendToUserWithSendToDefaultOverride() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        Class<?> clazz = SendToMethodReturnValueHandlerTests.SendToUserWithSendToOverrideTestBean.class;
        Method method = clazz.getDeclaredMethod("handleAndSendToDefaultDestination");
        MethodParameter parameter = new SynthesizingMethodParameter(method, (-1));
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, parameter, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        assertResponse(parameter, sessionId, 0, "/user/sess1/dest-default");
    }

    // SPR-14238
    @Test
    public void sendToUserWithSendToOverride() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        Class<?> clazz = SendToMethodReturnValueHandlerTests.SendToUserWithSendToOverrideTestBean.class;
        Method method = clazz.getDeclaredMethod("handleAndSendToOverride");
        MethodParameter parameter = new SynthesizingMethodParameter(method, (-1));
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, parameter, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        assertResponse(parameter, sessionId, 0, "/dest3");
        assertResponse(parameter, sessionId, 1, "/dest4");
    }

    @Test
    public void sendToDefaultDestinationWhenUsingDotPathSeparator() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        Message<?> inputMessage = createMessage("sess1", "sub1", "/app/", "dest.foo.bar", null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToDefaultDestReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertEquals("/topic/dest.foo.bar", accessor.getDestination());
    }

    @Test
    public void testHeadersToSend() throws Exception {
        Message<?> message = createMessage("sess1", "sub1", "/app", "/dest", null);
        SimpMessageSendingOperations messagingTemplate = Mockito.mock(SimpMessageSendingOperations.class);
        SendToMethodReturnValueHandler handler = new SendToMethodReturnValueHandler(messagingTemplate, false);
        handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.noAnnotationsReturnType, message);
        ArgumentCaptor<MessageHeaders> captor = ArgumentCaptor.forClass(MessageHeaders.class);
        Mockito.verify(messagingTemplate).convertAndSend(ArgumentMatchers.eq("/topic/dest"), ArgumentMatchers.eq(SendToMethodReturnValueHandlerTests.PAYLOAD), captor.capture());
        MessageHeaders headers = captor.getValue();
        SimpMessageHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(headers, SimpMessageHeaderAccessor.class);
        Assert.assertNotNull(accessor);
        Assert.assertTrue(accessor.isMutable());
        Assert.assertEquals("sess1", accessor.getSessionId());
        Assert.assertNull("Subscription id should not be copied", accessor.getSubscriptionId());
        Assert.assertEquals(this.noAnnotationsReturnType, accessor.getHeader(CONVERSION_HINT_HEADER));
    }

    @Test
    public void sendToUser() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.TestUser();
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertNull(accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/dest1"), accessor.getDestination());
        accessor = getCapturedAccessor(1);
        Assert.assertNull(accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/dest2"), accessor.getDestination());
    }

    @Test
    public void sendToAndSendToUser() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.TestUser();
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToSendToUserReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(4)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertNull(accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/dest1"), accessor.getDestination());
        accessor = getCapturedAccessor(1);
        Assert.assertNull(accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/dest2"), accessor.getDestination());
        accessor = getCapturedAccessor(2);
        Assert.assertEquals("sess1", accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals("/dest1", accessor.getDestination());
        accessor = getCapturedAccessor(3);
        Assert.assertEquals("sess1", accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals("/dest2", accessor.getDestination());
    }

    // SPR-12170
    @Test
    public void sendToWithDestinationPlaceholders() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        Map<String, String> vars = new LinkedHashMap<>(1);
        vars.put("roomName", "roomA");
        String sessionId = "sess1";
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.create();
        accessor.setSessionId(sessionId);
        accessor.setSubscriptionId("sub1");
        accessor.setHeader(DESTINATION_TEMPLATE_VARIABLES_HEADER, vars);
        Message<?> message = MessageBuilder.createMessage(SendToMethodReturnValueHandlerTests.PAYLOAD, accessor.getMessageHeaders());
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToWithPlaceholdersReturnType, message);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor actual = getCapturedAccessor(0);
        Assert.assertEquals(sessionId, actual.getSessionId());
        Assert.assertEquals("/topic/chat.message.filtered.roomA", actual.getDestination());
    }

    @Test
    public void sendToUserSingleSession() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.TestUser();
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserInSessionReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertEquals(sessionId, accessor.getSessionId());
        Assert.assertEquals(SendToMethodReturnValueHandlerTests.MIME_TYPE, accessor.getContentType());
        Assert.assertEquals((("/user/" + (user.getName())) + "/dest1"), accessor.getDestination());
        Assert.assertNull("Subscription id should not be copied", accessor.getSubscriptionId());
        Assert.assertEquals(this.sendToUserInSessionReturnType, accessor.getHeader(CONVERSION_HINT_HEADER));
        accessor = getCapturedAccessor(1);
        Assert.assertEquals(sessionId, accessor.getSessionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/dest2"), accessor.getDestination());
        Assert.assertEquals(SendToMethodReturnValueHandlerTests.MIME_TYPE, accessor.getContentType());
        Assert.assertNull("Subscription id should not be copied", accessor.getSubscriptionId());
        Assert.assertEquals(this.sendToUserInSessionReturnType, accessor.getHeader(CONVERSION_HINT_HEADER));
    }

    @Test
    public void sendToUserWithUserNameProvider() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.UniqueUser();
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertEquals("/user/Me myself and I/dest1", accessor.getDestination());
        accessor = getCapturedAccessor(1);
        Assert.assertEquals("/user/Me myself and I/dest2", accessor.getDestination());
    }

    @Test
    public void sendToUserDefaultDestination() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.TestUser();
        Message<?> inputMessage = createMessage(sessionId, "sub1", "/app", "/dest", user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserDefaultDestReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertNull(accessor.getSessionId());
        Assert.assertNull(accessor.getSubscriptionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/queue/dest"), accessor.getDestination());
    }

    @Test
    public void sendToUserDefaultDestinationWhenUsingDotPathSeparator() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.TestUser();
        Message<?> inputMessage = createMessage("sess1", "sub1", "/app/", "dest.foo.bar", user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserDefaultDestReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertEquals((("/user/" + (user.getName())) + "/queue/dest.foo.bar"), accessor.getDestination());
    }

    @Test
    public void sendToUserDefaultDestinationSingleSession() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        SendToMethodReturnValueHandlerTests.TestUser user = new SendToMethodReturnValueHandlerTests.TestUser();
        Message<?> message = createMessage(sessionId, "sub1", "/app", "/dest", user);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserInSessionDefaultDestReturnType, message);
        Mockito.verify(this.messageChannel, Mockito.times(1)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertEquals(sessionId, accessor.getSessionId());
        Assert.assertEquals((("/user/" + (user.getName())) + "/queue/dest"), accessor.getDestination());
        Assert.assertEquals(SendToMethodReturnValueHandlerTests.MIME_TYPE, accessor.getContentType());
        Assert.assertNull("Subscription id should not be copied", accessor.getSubscriptionId());
        Assert.assertEquals(this.sendToUserInSessionDefaultDestReturnType, accessor.getHeader(CONVERSION_HINT_HEADER));
    }

    @Test
    public void sendToUserSessionWithoutUserName() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", null, null, null);
        this.handler.handleReturnValue(SendToMethodReturnValueHandlerTests.PAYLOAD, this.sendToUserReturnType, inputMessage);
        Mockito.verify(this.messageChannel, Mockito.times(2)).send(this.messageCaptor.capture());
        SimpMessageHeaderAccessor accessor = getCapturedAccessor(0);
        Assert.assertEquals("/user/sess1/dest1", accessor.getDestination());
        Assert.assertEquals("sess1", accessor.getSessionId());
        accessor = getCapturedAccessor(1);
        Assert.assertEquals("/user/sess1/dest2", accessor.getDestination());
        Assert.assertEquals("sess1", accessor.getSessionId());
    }

    @Test
    public void jsonView() throws Exception {
        BDDMockito.given(this.messageChannel.send(ArgumentMatchers.any(Message.class))).willReturn(true);
        String sessionId = "sess1";
        Message<?> inputMessage = createMessage(sessionId, "sub1", "/app", "/dest", null);
        this.jsonHandler.handleReturnValue(handleAndSendToJsonView(), this.jsonViewReturnType, inputMessage);
        Mockito.verify(this.messageChannel).send(this.messageCaptor.capture());
        Message<?> message = this.messageCaptor.getValue();
        Assert.assertNotNull(message);
        String bytes = new String(((byte[]) (message.getPayload())), StandardCharsets.UTF_8);
        Assert.assertEquals("{\"withView1\":\"with\"}", bytes);
    }

    private static class TestUser implements Principal {
        public String getName() {
            return "joe";
        }

        public boolean implies(Subject subject) {
            return false;
        }
    }

    private static class UniqueUser extends SendToMethodReturnValueHandlerTests.TestUser implements DestinationUserNameProvider {
        @Override
        public String getDestinationUserName() {
            return "Me myself and I";
        }
    }

    @SendTo
    @Retention(RetentionPolicy.RUNTIME)
    @interface MySendTo {
        @AliasFor(annotation = SendTo.class, attribute = "value")
        String[] dest();
    }

    @SendToUser
    @Retention(RetentionPolicy.RUNTIME)
    @interface MySendToUser {
        @AliasFor(annotation = SendToUser.class, attribute = "destinations")
        String[] dest();
    }

    @SendToMethodReturnValueHandlerTests.MySendTo(dest = "/dest-default")
    @SuppressWarnings("unused")
    private static class SendToTestBean {
        String handleNoAnnotation() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }

        @SendTo
        String handleAndSendToDefaultDest() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }

        @SendToMethodReturnValueHandlerTests.MySendTo(dest = { "/dest3", "/dest4" })
        String handleAndSendToOverride() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }
    }

    @SendToMethodReturnValueHandlerTests.MySendToUser(dest = "/dest-default")
    @SuppressWarnings("unused")
    private static class SendToUserTestBean {
        String handleNoAnnotation() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }

        @SendToUser
        String handleAndSendToDefaultDest() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }

        @SendToMethodReturnValueHandlerTests.MySendToUser(dest = { "/dest3", "/dest4" })
        String handleAndSendToOverride() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }
    }

    @SendToMethodReturnValueHandlerTests.MySendToUser(dest = "/dest-default")
    @SuppressWarnings("unused")
    private static class SendToUserWithSendToOverrideTestBean {
        @SendTo
        String handleAndSendToDefaultDestination() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }

        @SendToMethodReturnValueHandlerTests.MySendTo(dest = { "/dest3", "/dest4" })
        String handleAndSendToOverride() {
            return SendToMethodReturnValueHandlerTests.PAYLOAD;
        }
    }

    private interface MyJacksonView1 {}

    private interface MyJacksonView2 {}

    @SuppressWarnings("unused")
    private static class JacksonViewBean {
        @JsonView(SendToMethodReturnValueHandlerTests.MyJacksonView1.class)
        private String withView1;

        @JsonView(SendToMethodReturnValueHandlerTests.MyJacksonView2.class)
        private String withView2;

        private String withoutView;

        public String getWithView1() {
            return withView1;
        }

        void setWithView1(String withView1) {
            this.withView1 = withView1;
        }

        String getWithView2() {
            return withView2;
        }

        void setWithView2(String withView2) {
            this.withView2 = withView2;
        }

        String getWithoutView() {
            return withoutView;
        }

        void setWithoutView(String withoutView) {
            this.withoutView = withoutView;
        }
    }
}

