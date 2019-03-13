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
package org.springframework.web.socket.client.jetty;


import java.net.URI;
import java.util.Arrays;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.util.CollectionUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.jetty.JettyWebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/**
 * Tests for {@link JettyWebSocketClient}.
 *
 * @author Rossen Stoyanchev
 */
public class JettyWebSocketClientTests {
    private JettyWebSocketClient client;

    private JettyWebSocketClientTests.TestJettyWebSocketServer server;

    private String wsUrl;

    private WebSocketSession wsSession;

    @Test
    public void doHandshake() throws Exception {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.setSecWebSocketProtocol(Arrays.asList("echo"));
        this.wsSession = this.client.doHandshake(new TextWebSocketHandler(), headers, new URI(this.wsUrl)).get();
        Assert.assertEquals(this.wsUrl, this.wsSession.getUri().toString());
        Assert.assertEquals("echo", this.wsSession.getAcceptedProtocol());
    }

    @Test
    public void doHandshakeWithTaskExecutor() throws Exception {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.setSecWebSocketProtocol(Arrays.asList("echo"));
        this.client.setTaskExecutor(new SimpleAsyncTaskExecutor());
        this.wsSession = this.client.doHandshake(new TextWebSocketHandler(), headers, new URI(this.wsUrl)).get();
        Assert.assertEquals(this.wsUrl, this.wsSession.getUri().toString());
        Assert.assertEquals("echo", this.wsSession.getAcceptedProtocol());
    }

    private static class TestJettyWebSocketServer {
        private final Server server;

        public TestJettyWebSocketServer(final WebSocketHandler webSocketHandler) {
            this.server = new Server();
            ServerConnector connector = new ServerConnector(this.server);
            connector.setPort(0);
            this.server.addConnector(connector);
            this.server.setHandler(new org.eclipse.jetty.websocket.server.WebSocketHandler() {
                @Override
                public void configure(WebSocketServletFactory factory) {
                    factory.setCreator(new WebSocketCreator() {
                        @Override
                        public Object createWebSocket(ServletUpgradeRequest req, ServletUpgradeResponse resp) {
                            if (!(CollectionUtils.isEmpty(req.getSubProtocols()))) {
                                resp.setAcceptedSubProtocol(req.getSubProtocols().get(0));
                            }
                            JettyWebSocketSession session = new JettyWebSocketSession(null, null);
                            return new org.springframework.web.socket.adapter.jetty.JettyWebSocketHandlerAdapter(webSocketHandler, session);
                        }
                    });
                }
            });
        }

        public void start() throws Exception {
            this.server.start();
        }

        public void stop() throws Exception {
            this.server.stop();
        }

        public int getPort() {
            return getLocalPort();
        }
    }
}

