/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.devtools.livereload;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.PongMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/**
 * Tests for {@link LiveReloadServer}.
 *
 * @author Phillip Webb
 * @author Andy Wilkinson
 */
public class LiveReloadServerTests {
    private static final String HANDSHAKE = "{command: 'hello', " + "protocols: ['http://livereload.com/protocols/official-7']}";

    private int port;

    private LiveReloadServerTests.MonitoredLiveReloadServer server;

    @Test
    public void triggerReload() throws Exception {
        LiveReloadServerTests.LiveReloadWebSocketHandler handler = connect();
        this.server.triggerReload();
        Thread.sleep(200);
        stop();
        assertThat(handler.getMessages().get(0)).contains("http://livereload.com/protocols/official-7");
        assertThat(handler.getMessages().get(1)).contains("command\":\"reload\"");
    }

    @Test
    public void pingPong() throws Exception {
        LiveReloadServerTests.LiveReloadWebSocketHandler handler = connect();
        handler.sendMessage(new PingMessage());
        Thread.sleep(200);
        assertThat(handler.getPongCount()).isEqualTo(1);
        stop();
    }

    @Test
    public void clientClose() throws Exception {
        LiveReloadServerTests.LiveReloadWebSocketHandler handler = connect();
        handler.close();
        awaitClosedException();
        assertThat(this.server.getClosedExceptions().size()).isGreaterThan(0);
    }

    @Test
    public void serverClose() throws Exception {
        LiveReloadServerTests.LiveReloadWebSocketHandler handler = connect();
        stop();
        Thread.sleep(200);
        assertThat(handler.getCloseStatus().getCode()).isEqualTo(1006);
    }

    /**
     * {@link LiveReloadServer} with additional monitoring.
     */
    private static class MonitoredLiveReloadServer extends LiveReloadServer {
        private final List<ConnectionClosedException> closedExceptions = new ArrayList<>();

        private final Object monitor = new Object();

        MonitoredLiveReloadServer(int port) {
            super(port);
        }

        @Override
        protected Connection createConnection(Socket socket, InputStream inputStream, OutputStream outputStream) throws IOException {
            return new LiveReloadServerTests.MonitoredLiveReloadServer.MonitoredConnection(socket, inputStream, outputStream);
        }

        public List<ConnectionClosedException> getClosedExceptions() {
            synchronized(this.monitor) {
                return new ArrayList(this.closedExceptions);
            }
        }

        private class MonitoredConnection extends Connection {
            MonitoredConnection(Socket socket, InputStream inputStream, OutputStream outputStream) throws IOException {
                super(socket, inputStream, outputStream);
            }

            @Override
            public void run() throws Exception {
                try {
                    super.run();
                } catch (ConnectionClosedException ex) {
                    synchronized(LiveReloadServerTests.MonitoredLiveReloadServer.this.monitor) {
                        LiveReloadServerTests.MonitoredLiveReloadServer.this.closedExceptions.add(ex);
                    }
                    throw ex;
                }
            }
        }
    }

    private static class LiveReloadWebSocketHandler extends TextWebSocketHandler {
        private WebSocketSession session;

        private final CountDownLatch helloLatch = new CountDownLatch(2);

        private final List<String> messages = new ArrayList<>();

        private int pongCount;

        private CloseStatus closeStatus;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            this.session = session;
            session.sendMessage(new TextMessage(LiveReloadServerTests.HANDSHAKE));
            this.helloLatch.countDown();
        }

        public void awaitHello() throws InterruptedException {
            this.helloLatch.await(1, TimeUnit.MINUTES);
            Thread.sleep(200);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) {
            if (message.getPayload().contains("hello")) {
                this.helloLatch.countDown();
            }
            this.messages.add(message.getPayload());
        }

        @Override
        protected void handlePongMessage(WebSocketSession session, PongMessage message) {
            (this.pongCount)++;
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            this.closeStatus = status;
        }

        public void sendMessage(WebSocketMessage<?> message) throws IOException {
            this.session.sendMessage(message);
        }

        public void close() throws IOException {
            this.session.close();
        }

        public List<String> getMessages() {
            return this.messages;
        }

        public int getPongCount() {
            return this.pongCount;
        }

        public CloseStatus getCloseStatus() {
            return this.closeStatus;
        }
    }
}

