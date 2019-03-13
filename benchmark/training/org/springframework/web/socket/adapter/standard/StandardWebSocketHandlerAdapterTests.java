/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.socket.adapter.standard;


import CloseStatus.NORMAL;
import MessageHandler.Whole;
import java.net.URI;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.Session;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketHandler;


/**
 * Test fixture for {@link org.springframework.web.socket.adapter.standard.StandardWebSocketHandlerAdapter}.
 *
 * @author Rossen Stoyanchev
 */
public class StandardWebSocketHandlerAdapterTests {
    private StandardWebSocketHandlerAdapter adapter;

    private WebSocketHandler webSocketHandler;

    private StandardWebSocketSession webSocketSession;

    private Session session;

    @Test
    public void onOpen() throws Throwable {
        URI uri = URI.create("http://example.org");
        BDDMockito.given(this.session.getRequestURI()).willReturn(uri);
        this.adapter.onOpen(this.session, null);
        Mockito.verify(this.webSocketHandler).afterConnectionEstablished(this.webSocketSession);
        Mockito.verify(this.session, Mockito.atLeast(2)).addMessageHandler(ArgumentMatchers.any(Whole.class));
        BDDMockito.given(this.session.getRequestURI()).willReturn(uri);
        Assert.assertEquals(uri, this.webSocketSession.getUri());
    }

    @Test
    public void onClose() throws Throwable {
        this.adapter.onClose(this.session, new javax.websocket.CloseReason(CloseCodes.NORMAL_CLOSURE, "reason"));
        Mockito.verify(this.webSocketHandler).afterConnectionClosed(this.webSocketSession, NORMAL.withReason("reason"));
    }

    @Test
    public void onError() throws Throwable {
        Exception exception = new Exception();
        this.adapter.onError(this.session, exception);
        Mockito.verify(this.webSocketHandler).handleTransportError(this.webSocketSession, exception);
    }
}

