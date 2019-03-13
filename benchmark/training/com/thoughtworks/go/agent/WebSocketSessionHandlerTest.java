/**
 * Copyright 2017 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent;


import com.thoughtworks.go.websocket.Action;
import com.thoughtworks.go.websocket.Message;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.extensions.OutgoingFrames;
import org.eclipse.jetty.websocket.common.LogicalConnection;
import org.eclipse.jetty.websocket.common.WebSocketRemoteEndpoint;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class WebSocketSessionHandlerTest {
    private WebSocketSessionHandler handler;

    private Session session;

    @Test
    public void shouldWaitForAcknowledgementWhileSendingMessages() throws Exception {
        final Message message = new Message(Action.reportCurrentStatus);
        Mockito.when(session.getRemote()).thenReturn(new WebSocketSessionHandlerTest.FakeWebSocketEndpoint(new Runnable() {
            @Override
            public void run() {
                handler.acknowledge(new Message(Action.acknowledge, message.getAcknowledgementId()));
            }
        }));
        Thread sendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.sendAndWaitForAcknowledgement(message);
            }
        });
        sendThread.start();
        Assert.assertThat(sendThread.isAlive(), Matchers.is(true));
        sendThread.join();
        Assert.assertThat(sendThread.isAlive(), Matchers.is(false));
    }

    @Test
    public void shouldReturnTrueIfNotRunning() throws Exception {
        Assert.assertThat(handler.isNotRunning(), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseIfRunning() throws Exception {
        Mockito.when(session.isOpen()).thenReturn(true);
        Assert.assertThat(handler.isNotRunning(), Matchers.is(false));
    }

    @Test
    public void shouldSetSessionNameToNoSessionWhenStopped() throws Exception {
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getRemoteAddress()).thenReturn(null);
        handler.stop();
        Assert.assertThat(handler.getSessionName(), Matchers.is("[No Session]"));
    }

    @Test
    public void shouldSetSessionToNullWhenStopped() throws Exception {
        Mockito.when(session.isOpen()).thenReturn(true);
        Mockito.when(session.getRemoteAddress()).thenReturn(null);
        handler.stop();
        Mockito.verify(session).close();
        Assert.assertThat(handler.isNotRunning(), Matchers.is(true));
    }

    class FakeWebSocketEndpoint extends WebSocketRemoteEndpoint {
        private Runnable runnable;

        public FakeWebSocketEndpoint(Runnable runnable) {
            super(Mockito.mock(LogicalConnection.class), Mockito.mock(OutgoingFrames.class));
            this.runnable = runnable;
        }

        @Override
        public Future<Void> sendBytesByFuture(ByteBuffer data) {
            runnable.run();
            return null;
        }
    }
}

