/**
 * Copyright 2002-2015 the original author or authors.
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
package org.springframework.web.socket.sockjs.transport.session;


import CloseStatus.NOT_ACCEPTABLE;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TestWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.sockjs.transport.SockJsServiceConfig;


/**
 * Unit tests for {@link WebSocketServerSockJsSession}.
 *
 * @author Rossen Stoyanchev
 */
public class WebSocketServerSockJsSessionTests extends AbstractSockJsSessionTests<WebSocketServerSockJsSessionTests.TestWebSocketServerSockJsSession> {
    private TestWebSocketSession webSocketSession;

    @Test
    public void isActive() throws Exception {
        Assert.assertFalse(this.session.isActive());
        initializeDelegateSession(this.webSocketSession);
        Assert.assertTrue(this.session.isActive());
        this.webSocketSession.setOpen(false);
        Assert.assertFalse(this.session.isActive());
    }

    @Test
    public void afterSessionInitialized() throws Exception {
        initializeDelegateSession(this.webSocketSession);
        Assert.assertEquals(Collections.singletonList(new TextMessage("o")), this.webSocketSession.getSentMessages());
        Assert.assertEquals(Arrays.asList("schedule"), this.session.heartbeatSchedulingEvents);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(this.session);
        Mockito.verifyNoMoreInteractions(this.taskScheduler, this.webSocketHandler);
    }

    @Test
    @SuppressWarnings("resource")
    public void afterSessionInitializedOpenFrameFirst() throws Exception {
        TextWebSocketHandler handler = new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                session.sendMessage(new TextMessage("go go"));
            }
        };
        WebSocketServerSockJsSessionTests.TestWebSocketServerSockJsSession session = new WebSocketServerSockJsSessionTests.TestWebSocketServerSockJsSession(this.sockJsConfig, handler, null);
        initializeDelegateSession(this.webSocketSession);
        List<TextMessage> expected = Arrays.asList(new TextMessage("o"), new TextMessage("a[\"go go\"]"));
        Assert.assertEquals(expected, this.webSocketSession.getSentMessages());
    }

    @Test
    public void handleMessageEmptyPayload() throws Exception {
        this.session.handleMessage(new TextMessage(""), this.webSocketSession);
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void handleMessage() throws Exception {
        TextMessage message = new TextMessage("[\"x\"]");
        this.session.handleMessage(message, this.webSocketSession);
        Mockito.verify(this.webSocketHandler).handleMessage(this.session, new TextMessage("x"));
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void handleMessageBadData() throws Exception {
        TextMessage message = new TextMessage("[\"x]");
        this.session.handleMessage(message, this.webSocketSession);
        isClosed();
        Mockito.verify(this.webSocketHandler).handleTransportError(ArgumentMatchers.same(this.session), ArgumentMatchers.any(IOException.class));
        Mockito.verifyNoMoreInteractions(this.webSocketHandler);
    }

    @Test
    public void sendMessageInternal() throws Exception {
        initializeDelegateSession(this.webSocketSession);
        this.session.sendMessageInternal("x");
        Assert.assertEquals(Arrays.asList(new TextMessage("o"), new TextMessage("a[\"x\"]")), this.webSocketSession.getSentMessages());
        Assert.assertEquals(Arrays.asList("schedule", "cancel", "schedule"), this.session.heartbeatSchedulingEvents);
    }

    @Test
    public void disconnect() throws Exception {
        initializeDelegateSession(this.webSocketSession);
        this.session.close(NOT_ACCEPTABLE);
        Assert.assertEquals(NOT_ACCEPTABLE, this.webSocketSession.getCloseStatus());
    }

    static class TestWebSocketServerSockJsSession extends WebSocketServerSockJsSession {
        private final List<String> heartbeatSchedulingEvents = new ArrayList<>();

        public TestWebSocketServerSockJsSession(SockJsServiceConfig config, WebSocketHandler handler, Map<String, Object> attributes) {
            super("1", config, handler, attributes);
        }

        @Override
        protected void scheduleHeartbeat() {
            this.heartbeatSchedulingEvents.add("schedule");
        }

        @Override
        protected void cancelHeartbeat() {
            this.heartbeatSchedulingEvents.add("cancel");
        }
    }
}

