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


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.WebSocketTestServer;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.RequestUpgradeStrategy;


/**
 * Abstract base class for integration tests using the
 * {@link org.springframework.web.socket.sockjs.client.SockJsClient SockJsClient}
 * against actual SockJS server endpoints.
 *
 * @author Rossen Stoyanchev
 * @author Sam Brannen
 */
public abstract class AbstractSockJsIntegrationTests {
    @Rule
    public final TestName testName = new TestName();

    protected Log logger = LogFactory.getLog(getClass());

    private SockJsClient sockJsClient;

    private WebSocketTestServer server;

    private AnnotationConfigWebApplicationContext wac;

    private AbstractSockJsIntegrationTests.TestFilter testFilter;

    private String baseUrl;

    @Test
    public void echoWebSocket() throws Exception {
        testEcho(100, createWebSocketTransport(), null);
    }

    @Test
    public void echoXhrStreaming() throws Exception {
        testEcho(100, createXhrTransport(), null);
    }

    @Test
    public void echoXhr() throws Exception {
        AbstractXhrTransport xhrTransport = createXhrTransport();
        xhrTransport.setXhrStreamingDisabled(true);
        testEcho(100, xhrTransport, null);
    }

    // SPR-13254
    @Test
    public void echoXhrWithHeaders() throws Exception {
        AbstractXhrTransport xhrTransport = createXhrTransport();
        xhrTransport.setXhrStreamingDisabled(true);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.add("auth", "123");
        testEcho(10, xhrTransport, headers);
        for (Map.Entry<String, HttpHeaders> entry : this.testFilter.requests.entrySet()) {
            HttpHeaders httpHeaders = entry.getValue();
            Assert.assertEquals(("No auth header for: " + (entry.getKey())), "123", httpHeaders.getFirst("auth"));
        }
    }

    @Test
    public void receiveOneMessageWebSocket() throws Exception {
        testReceiveOneMessage(createWebSocketTransport(), null);
    }

    @Test
    public void receiveOneMessageXhrStreaming() throws Exception {
        testReceiveOneMessage(createXhrTransport(), null);
    }

    @Test
    public void receiveOneMessageXhr() throws Exception {
        AbstractXhrTransport xhrTransport = createXhrTransport();
        xhrTransport.setXhrStreamingDisabled(true);
        testReceiveOneMessage(xhrTransport, null);
    }

    @Test
    public void infoRequestFailure() throws Exception {
        AbstractSockJsIntegrationTests.TestClientHandler handler = new AbstractSockJsIntegrationTests.TestClientHandler();
        this.testFilter.sendErrorMap.put("/info", 500);
        CountDownLatch latch = new CountDownLatch(1);
        initSockJsClient(createWebSocketTransport());
        this.sockJsClient.doHandshake(handler, ((this.baseUrl) + "/echo")).addCallback(new org.springframework.util.concurrent.ListenableFutureCallback<WebSocketSession>() {
            @Override
            public void onSuccess(WebSocketSession result) {
            }

            @Override
            public void onFailure(Throwable ex) {
                latch.countDown();
            }
        });
        Assert.assertTrue(latch.await(5000, TimeUnit.MILLISECONDS));
    }

    @Test
    public void fallbackAfterTransportFailure() throws Exception {
        this.testFilter.sendErrorMap.put("/websocket", 200);
        this.testFilter.sendErrorMap.put("/xhr_streaming", 500);
        AbstractSockJsIntegrationTests.TestClientHandler handler = new AbstractSockJsIntegrationTests.TestClientHandler();
        initSockJsClient(createWebSocketTransport(), createXhrTransport());
        WebSocketSession session = this.sockJsClient.doHandshake(handler, ((this.baseUrl) + "/echo")).get();
        Assert.assertEquals("Fallback didn't occur", XhrClientSockJsSession.class, session.getClass());
        TextMessage message = new TextMessage("message1");
        session.sendMessage(message);
        handler.awaitMessage(message, 5000);
    }

    @Test(timeout = 5000)
    public void fallbackAfterConnectTimeout() throws Exception {
        AbstractSockJsIntegrationTests.TestClientHandler clientHandler = new AbstractSockJsIntegrationTests.TestClientHandler();
        this.testFilter.sleepDelayMap.put("/xhr_streaming", 10000L);
        this.testFilter.sendErrorMap.put("/xhr_streaming", 503);
        initSockJsClient(createXhrTransport());
        this.sockJsClient.setConnectTimeoutScheduler(this.wac.getBean(ThreadPoolTaskScheduler.class));
        WebSocketSession clientSession = sockJsClient.doHandshake(clientHandler, ((this.baseUrl) + "/echo")).get();
        Assert.assertEquals("Fallback didn't occur", XhrClientSockJsSession.class, clientSession.getClass());
        TextMessage message = new TextMessage("message1");
        clientSession.sendMessage(message);
        clientHandler.awaitMessage(message, 5000);
        clientSession.close();
    }

    @Configuration
    @EnableWebSocket
    static class TestConfig implements WebSocketConfigurer {
        @Autowired
        private RequestUpgradeStrategy upgradeStrategy;

        @Override
        public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
            HandshakeHandler handshakeHandler = new org.springframework.web.socket.server.support.DefaultHandshakeHandler(this.upgradeStrategy);
            registry.addHandler(new AbstractSockJsIntegrationTests.EchoHandler(), "/echo").setHandshakeHandler(handshakeHandler).withSockJS();
            registry.addHandler(testServerHandler(), "/test").setHandshakeHandler(handshakeHandler).withSockJS();
        }

        @Bean
        public AbstractSockJsIntegrationTests.TestServerHandler testServerHandler() {
            return new AbstractSockJsIntegrationTests.TestServerHandler();
        }
    }

    private static class TestClientHandler extends TextWebSocketHandler {
        private final BlockingQueue<TextMessage> receivedMessages = new LinkedBlockingQueue<>();

        private volatile WebSocketSession session;

        private volatile Throwable transportError;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            this.session = session;
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            this.receivedMessages.add(message);
        }

        @Override
        public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
            this.transportError = exception;
        }

        public void awaitMessageCount(final int count, long timeToWait) throws Exception {
            AbstractSockJsIntegrationTests.awaitEvent(() -> (receivedMessages.size()) >= count, timeToWait, ((count + " number of messages. Received so far: ") + (this.receivedMessages)));
        }

        public void awaitMessage(TextMessage expected, long timeToWait) throws InterruptedException {
            TextMessage actual = this.receivedMessages.poll(timeToWait, TimeUnit.MILLISECONDS);
            if (actual != null) {
                Assert.assertEquals(expected, actual);
            } else
                if ((this.transportError) != null) {
                    throw new AssertionError("Transport error", this.transportError);
                } else {
                    Assert.fail((("Timed out waiting for [" + expected) + "]"));
                }

        }
    }

    private static class EchoHandler extends TextWebSocketHandler {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            session.sendMessage(message);
        }
    }

    private static class TestServerHandler extends TextWebSocketHandler {
        private WebSocketSession session;

        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            this.session = session;
        }

        public WebSocketSession awaitSession(long timeToWait) throws InterruptedException {
            AbstractSockJsIntegrationTests.awaitEvent(() -> (this.session) != null, timeToWait, " session");
            return this.session;
        }
    }

    private static class TestFilter implements Filter {
        private final Map<String, HttpHeaders> requests = new HashMap<>();

        private final Map<String, Long> sleepDelayMap = new HashMap<>();

        private final Map<String, Integer> sendErrorMap = new HashMap<>();

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            HttpServletRequest httpRequest = ((HttpServletRequest) (request));
            String uri = httpRequest.getRequestURI();
            HttpHeaders headers = getHeaders();
            this.requests.put(uri, headers);
            for (String suffix : this.sleepDelayMap.keySet()) {
                if (httpRequest.getRequestURI().endsWith(suffix)) {
                    try {
                        Thread.sleep(this.sleepDelayMap.get(suffix));
                        break;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            for (String suffix : this.sendErrorMap.keySet()) {
                if (httpRequest.getRequestURI().endsWith(suffix)) {
                    sendError(this.sendErrorMap.get(suffix));
                    return;
                }
            }
            chain.doFilter(request, response);
        }

        @Override
        public void init(FilterConfig filterConfig) {
        }

        @Override
        public void destroy() {
        }
    }
}

