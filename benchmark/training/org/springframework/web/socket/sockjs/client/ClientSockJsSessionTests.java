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
package org.springframework.web.socket.sockjs.client;


import CloseStatus.BAD_DATA;
import CloseStatus.NORMAL;
import CloseStatus.SERVER_ERROR;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.util.concurrent.SettableListenableFuture;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;
import org.springframework.web.socket.sockjs.frame.SockJsFrame;


/**
 * Unit tests for
 * {@link org.springframework.web.socket.sockjs.client.AbstractClientSockJsSession}.
 *
 * @author Rossen Stoyanchev
 */
public class ClientSockJsSessionTests {
    private static final Jackson2SockJsMessageCodec CODEC = new Jackson2SockJsMessageCodec();

    private ClientSockJsSessionTests.TestClientSockJsSession session;

    private WebSocketHandler handler;

    private SettableListenableFuture<WebSocketSession> connectFuture;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void handleFrameOpen() throws Exception {
        Assert.assertThat(isOpen(), CoreMatchers.is(false));
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        Assert.assertThat(isOpen(), CoreMatchers.is(true));
        Assert.assertTrue(this.connectFuture.isDone());
        Assert.assertThat(this.connectFuture.get(), CoreMatchers.sameInstance(this.session));
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void handleFrameOpenWhenStatusNotNew() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        Assert.assertThat(isOpen(), CoreMatchers.is(true));
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        Assert.assertThat(this.session.disconnectStatus, CoreMatchers.equalTo(new CloseStatus(1006, "Server lost session")));
    }

    @Test
    public void handleFrameOpenWithWebSocketHandlerException() throws Exception {
        BDDMockito.willThrow(new IllegalStateException("Fake error")).given(this.handler).afterConnectionEstablished(this.session);
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        Assert.assertThat(isOpen(), CoreMatchers.is(true));
    }

    @Test
    public void handleFrameMessage() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.handleFrame(SockJsFrame.messageFrame(ClientSockJsSessionTests.CODEC, "foo", "bar").getContent());
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verify(this.handler).handleMessage(this.session, new TextMessage("foo"));
        Mockito.verify(this.handler).handleMessage(this.session, new TextMessage("bar"));
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void handleFrameMessageWhenNotOpen() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.close();
        Mockito.reset(this.handler);
        this.session.handleFrame(SockJsFrame.messageFrame(ClientSockJsSessionTests.CODEC, "foo", "bar").getContent());
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void handleFrameMessageWithBadData() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        handleFrame("a['bad data");
        Assert.assertThat(isOpen(), CoreMatchers.equalTo(false));
        Assert.assertThat(this.session.disconnectStatus, CoreMatchers.equalTo(BAD_DATA));
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void handleFrameMessageWithWebSocketHandlerException() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        BDDMockito.willThrow(new IllegalStateException("Fake error")).given(this.handler).handleMessage(this.session, new TextMessage("foo"));
        BDDMockito.willThrow(new IllegalStateException("Fake error")).given(this.handler).handleMessage(this.session, new TextMessage("bar"));
        this.session.handleFrame(SockJsFrame.messageFrame(ClientSockJsSessionTests.CODEC, "foo", "bar").getContent());
        Assert.assertThat(isOpen(), CoreMatchers.equalTo(true));
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verify(this.handler).handleMessage(this.session, new TextMessage("foo"));
        Mockito.verify(this.handler).handleMessage(this.session, new TextMessage("bar"));
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void handleFrameClose() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.handleFrame(SockJsFrame.closeFrame(1007, "").getContent());
        Assert.assertThat(isOpen(), CoreMatchers.equalTo(false));
        Assert.assertThat(this.session.disconnectStatus, CoreMatchers.equalTo(new CloseStatus(1007, "")));
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void handleTransportError() throws Exception {
        final IllegalStateException ex = new IllegalStateException("Fake error");
        this.session.handleTransportError(ex);
        Mockito.verify(this.handler).handleTransportError(this.session, ex);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void afterTransportClosed() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.afterTransportClosed(SERVER_ERROR);
        Assert.assertThat(isOpen(), CoreMatchers.equalTo(false));
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verify(this.handler).afterConnectionClosed(this.session, SERVER_ERROR);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void close() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.close();
        Assert.assertThat(isOpen(), CoreMatchers.equalTo(false));
        Assert.assertThat(this.session.disconnectStatus, CoreMatchers.equalTo(NORMAL));
        Mockito.verify(this.handler).afterConnectionEstablished(this.session);
        Mockito.verifyNoMoreInteractions(this.handler);
    }

    @Test
    public void closeWithStatus() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.close(new CloseStatus(3000, "reason"));
        Assert.assertThat(this.session.disconnectStatus, CoreMatchers.equalTo(new CloseStatus(3000, "reason")));
    }

    @Test
    public void closeWithNullStatus() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Invalid close status");
        this.session.close(null);
    }

    @Test
    public void closeWithStatusOutOfRange() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.thrown.expect(IllegalArgumentException.class);
        this.thrown.expectMessage("Invalid close status");
        this.session.close(new CloseStatus(2999, "reason"));
    }

    @Test
    public void timeoutTask() {
        getTimeoutTask().run();
        Assert.assertThat(this.session.disconnectStatus, CoreMatchers.equalTo(new CloseStatus(2007, "Transport timed out")));
    }

    @Test
    public void send() throws Exception {
        this.session.handleFrame(SockJsFrame.openFrame().getContent());
        this.session.sendMessage(new TextMessage("foo"));
        Assert.assertThat(this.session.sentMessage, CoreMatchers.equalTo(new TextMessage("[\"foo\"]")));
    }

    private static class TestClientSockJsSession extends AbstractClientSockJsSession {
        private TextMessage sentMessage;

        private CloseStatus disconnectStatus;

        protected TestClientSockJsSession(TransportRequest request, WebSocketHandler handler, SettableListenableFuture<WebSocketSession> connectFuture) {
            super(request, handler, connectFuture);
        }

        @Override
        protected void sendInternal(TextMessage textMessage) throws IOException {
            this.sentMessage = textMessage;
        }

        @Override
        protected void disconnect(CloseStatus status) throws IOException {
            this.disconnectStatus = status;
        }

        @Override
        public InetSocketAddress getLocalAddress() {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteAddress() {
            return null;
        }

        @Override
        public String getAcceptedProtocol() {
            return null;
        }

        @Override
        public void setTextMessageSizeLimit(int messageSizeLimit) {
        }

        @Override
        public int getTextMessageSizeLimit() {
            return 0;
        }

        @Override
        public void setBinaryMessageSizeLimit(int messageSizeLimit) {
        }

        @Override
        public int getBinaryMessageSizeLimit() {
            return 0;
        }

        @Override
        public List<WebSocketExtension> getExtensions() {
            return null;
        }
    }
}

