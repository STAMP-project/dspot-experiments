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
package org.springframework.web.socket.adapter.jetty;


import CloseStatus.NORMAL;
import org.eclipse.jetty.websocket.api.Session;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketHandler;


/**
 * Test fixture for {@link org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter}.
 *
 * @author Rossen Stoyanchev
 */
public class JettyWebSocketHandlerAdapterTests {
    private JettyWebSocketHandlerAdapter adapter;

    private WebSocketHandler webSocketHandler;

    private JettyWebSocketSession webSocketSession;

    private Session session;

    @Test
    public void onOpen() throws Throwable {
        this.adapter.onWebSocketConnect(this.session);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(this.webSocketSession);
    }

    @Test
    public void onClose() throws Throwable {
        this.adapter.onWebSocketClose(1000, "reason");
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(this.webSocketSession, NORMAL.withReason("reason"));
    }

    @Test
    public void onError() throws Throwable {
        Exception exception = new Exception();
        this.adapter.onWebSocketError(exception);
        Mockito.verify(this.webSocketHandler).handleTransportError(this.webSocketSession, exception);
    }
}

