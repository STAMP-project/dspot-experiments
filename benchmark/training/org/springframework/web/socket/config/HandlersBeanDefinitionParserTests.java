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
package org.springframework.web.socket.config;


import TransportType.WEBSOCKET;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.context.support.GenericWebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.EventSourceTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.HtmlFileTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.XhrPollingTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.XhrReceivingTransportHandler;
import org.springframework.web.socket.sockjs.transport.handler.XhrStreamingTransportHandler;


/**
 * Test fixture for HandlersBeanDefinitionParser.
 * See test configuration files websocket-config-handlers-*.xml.
 *
 * @author Brian Clozel
 * @author Rossen Stoyanchev
 */
public class HandlersBeanDefinitionParserTests {
    private final GenericWebApplicationContext appContext = new GenericWebApplicationContext();

    @Test
    public void webSocketHandlers() {
        loadBeanDefinitions("websocket-config-handlers.xml");
        Map<String, HandlerMapping> handlersMap = this.appContext.getBeansOfType(HandlerMapping.class);
        Assert.assertNotNull(handlersMap);
        Assert.assertThat(handlersMap.values(), hasSize(2));
        for (HandlerMapping hm : handlersMap.values()) {
            Assert.assertTrue((hm instanceof SimpleUrlHandlerMapping));
            SimpleUrlHandlerMapping shm = ((SimpleUrlHandlerMapping) (hm));
            if (shm.getUrlMap().keySet().contains("/foo")) {
                Assert.assertThat(shm.getUrlMap().keySet(), contains("/foo", "/bar"));
                WebSocketHttpRequestHandler handler = ((WebSocketHttpRequestHandler) (shm.getUrlMap().get("/foo")));
                Assert.assertNotNull(handler);
                HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(handler.getWebSocketHandler(), FooWebSocketHandler.class);
                HandshakeHandler handshakeHandler = handler.getHandshakeHandler();
                Assert.assertNotNull(handshakeHandler);
                Assert.assertTrue((handshakeHandler instanceof DefaultHandshakeHandler));
                Assert.assertFalse(handler.getHandshakeInterceptors().isEmpty());
                Assert.assertTrue(((handler.getHandshakeInterceptors().get(0)) instanceof OriginHandshakeInterceptor));
            } else {
                Assert.assertThat(shm.getUrlMap().keySet(), contains("/test"));
                WebSocketHttpRequestHandler handler = ((WebSocketHttpRequestHandler) (shm.getUrlMap().get("/test")));
                Assert.assertNotNull(handler);
                HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(handler.getWebSocketHandler(), TestWebSocketHandler.class);
                HandshakeHandler handshakeHandler = handler.getHandshakeHandler();
                Assert.assertNotNull(handshakeHandler);
                Assert.assertTrue((handshakeHandler instanceof DefaultHandshakeHandler));
                Assert.assertFalse(handler.getHandshakeInterceptors().isEmpty());
                Assert.assertTrue(((handler.getHandshakeInterceptors().get(0)) instanceof OriginHandshakeInterceptor));
            }
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void webSocketHandlersAttributes() {
        loadBeanDefinitions("websocket-config-handlers-attributes.xml");
        HandlerMapping handlerMapping = this.appContext.getBean(HandlerMapping.class);
        Assert.assertNotNull(handlerMapping);
        Assert.assertTrue((handlerMapping instanceof SimpleUrlHandlerMapping));
        SimpleUrlHandlerMapping urlHandlerMapping = ((SimpleUrlHandlerMapping) (handlerMapping));
        Assert.assertEquals(2, urlHandlerMapping.getOrder());
        WebSocketHttpRequestHandler handler = ((WebSocketHttpRequestHandler) (urlHandlerMapping.getUrlMap().get("/foo")));
        Assert.assertNotNull(handler);
        HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(handler.getWebSocketHandler(), FooWebSocketHandler.class);
        HandshakeHandler handshakeHandler = handler.getHandshakeHandler();
        Assert.assertNotNull(handshakeHandler);
        Assert.assertTrue((handshakeHandler instanceof TestHandshakeHandler));
        List<HandshakeInterceptor> interceptors = handler.getHandshakeInterceptors();
        Assert.assertThat(interceptors, contains(instanceOf(FooTestInterceptor.class), instanceOf(BarTestInterceptor.class), instanceOf(OriginHandshakeInterceptor.class)));
        handler = ((WebSocketHttpRequestHandler) (urlHandlerMapping.getUrlMap().get("/test")));
        Assert.assertNotNull(handler);
        HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(handler.getWebSocketHandler(), TestWebSocketHandler.class);
        handshakeHandler = handler.getHandshakeHandler();
        Assert.assertNotNull(handshakeHandler);
        Assert.assertTrue((handshakeHandler instanceof TestHandshakeHandler));
        interceptors = handler.getHandshakeInterceptors();
        Assert.assertThat(interceptors, contains(instanceOf(FooTestInterceptor.class), instanceOf(BarTestInterceptor.class), instanceOf(OriginHandshakeInterceptor.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void sockJs() {
        loadBeanDefinitions("websocket-config-handlers-sockjs.xml");
        SimpleUrlHandlerMapping handlerMapping = this.appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(handlerMapping);
        SockJsHttpRequestHandler testHandler = ((SockJsHttpRequestHandler) (handlerMapping.getUrlMap().get("/test/**")));
        Assert.assertNotNull(testHandler);
        HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(testHandler.getWebSocketHandler(), TestWebSocketHandler.class);
        SockJsService testSockJsService = testHandler.getSockJsService();
        SockJsHttpRequestHandler fooHandler = ((SockJsHttpRequestHandler) (handlerMapping.getUrlMap().get("/foo/**")));
        Assert.assertNotNull(fooHandler);
        HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(fooHandler.getWebSocketHandler(), FooWebSocketHandler.class);
        SockJsService sockJsService = fooHandler.getSockJsService();
        Assert.assertNotNull(sockJsService);
        Assert.assertSame(testSockJsService, sockJsService);
        Assert.assertThat(sockJsService, instanceOf(DefaultSockJsService.class));
        DefaultSockJsService defaultSockJsService = ((DefaultSockJsService) (sockJsService));
        Assert.assertThat(defaultSockJsService.getTaskScheduler(), instanceOf(ThreadPoolTaskScheduler.class));
        Assert.assertFalse(defaultSockJsService.shouldSuppressCors());
        Map<TransportType, TransportHandler> handlerMap = defaultSockJsService.getTransportHandlers();
        Assert.assertThat(handlerMap.values(), containsInAnyOrder(instanceOf(XhrPollingTransportHandler.class), instanceOf(XhrReceivingTransportHandler.class), instanceOf(XhrStreamingTransportHandler.class), instanceOf(EventSourceTransportHandler.class), instanceOf(HtmlFileTransportHandler.class), instanceOf(WebSocketTransportHandler.class)));
        WebSocketTransportHandler handler = ((WebSocketTransportHandler) (handlerMap.get(WEBSOCKET)));
        Assert.assertEquals(TestHandshakeHandler.class, handler.getHandshakeHandler().getClass());
        List<HandshakeInterceptor> interceptors = defaultSockJsService.getHandshakeInterceptors();
        Assert.assertThat(interceptors, contains(instanceOf(FooTestInterceptor.class), instanceOf(BarTestInterceptor.class), instanceOf(OriginHandshakeInterceptor.class)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void sockJsAttributes() {
        loadBeanDefinitions("websocket-config-handlers-sockjs-attributes.xml");
        SimpleUrlHandlerMapping handlerMapping = appContext.getBean(SimpleUrlHandlerMapping.class);
        Assert.assertNotNull(handlerMapping);
        SockJsHttpRequestHandler handler = ((SockJsHttpRequestHandler) (handlerMapping.getUrlMap().get("/test/**")));
        Assert.assertNotNull(handler);
        HandlersBeanDefinitionParserTests.unwrapAndCheckDecoratedHandlerType(handler.getWebSocketHandler(), TestWebSocketHandler.class);
        SockJsService sockJsService = handler.getSockJsService();
        Assert.assertNotNull(sockJsService);
        Assert.assertThat(sockJsService, instanceOf(TransportHandlingSockJsService.class));
        TransportHandlingSockJsService transportService = ((TransportHandlingSockJsService) (sockJsService));
        Assert.assertThat(transportService.getTaskScheduler(), instanceOf(TestTaskScheduler.class));
        Assert.assertThat(transportService.getTransportHandlers().values(), containsInAnyOrder(instanceOf(XhrPollingTransportHandler.class), instanceOf(XhrStreamingTransportHandler.class)));
        Assert.assertEquals("testSockJsService", transportService.getName());
        Assert.assertFalse(transportService.isWebSocketEnabled());
        Assert.assertFalse(transportService.isSessionCookieNeeded());
        Assert.assertEquals(2048, transportService.getStreamBytesLimit());
        Assert.assertEquals(256, transportService.getDisconnectDelay());
        Assert.assertEquals(1024, transportService.getHttpMessageCacheSize());
        Assert.assertEquals(20, transportService.getHeartbeatTime());
        Assert.assertEquals("/js/sockjs.min.js", transportService.getSockJsClientLibraryUrl());
        Assert.assertEquals(TestMessageCodec.class, transportService.getMessageCodec().getClass());
        List<HandshakeInterceptor> interceptors = transportService.getHandshakeInterceptors();
        Assert.assertThat(interceptors, contains(instanceOf(OriginHandshakeInterceptor.class)));
        Assert.assertTrue(transportService.shouldSuppressCors());
        Assert.assertTrue(transportService.getAllowedOrigins().contains("http://mydomain1.com"));
        Assert.assertTrue(transportService.getAllowedOrigins().contains("http://mydomain2.com"));
    }
}

