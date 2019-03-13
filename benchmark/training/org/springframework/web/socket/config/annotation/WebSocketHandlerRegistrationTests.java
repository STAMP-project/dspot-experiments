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
package org.springframework.web.socket.config.annotation;


import TransportType.WEBSOCKET;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.sockjs.SockJsService;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;


/**
 * Test fixture for
 * {@link org.springframework.web.socket.config.annotation.AbstractWebSocketHandlerRegistration}.
 *
 * @author Rossen Stoyanchev
 */
public class WebSocketHandlerRegistrationTests {
    private WebSocketHandlerRegistrationTests.TestWebSocketHandlerRegistration registration;

    private TaskScheduler taskScheduler;

    @Test
    public void minimal() {
        WebSocketHandler handler = new TextWebSocketHandler();
        this.registration.addHandler(handler, "/foo", "/bar");
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(2, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping m1 = mappings.get(0);
        Assert.assertEquals(handler, m1.webSocketHandler);
        Assert.assertEquals("/foo", m1.path);
        Assert.assertNotNull(m1.interceptors);
        Assert.assertEquals(1, m1.interceptors.length);
        Assert.assertEquals(OriginHandshakeInterceptor.class, m1.interceptors[0].getClass());
        WebSocketHandlerRegistrationTests.Mapping m2 = mappings.get(1);
        Assert.assertEquals(handler, m2.webSocketHandler);
        Assert.assertEquals("/bar", m2.path);
        Assert.assertNotNull(m2.interceptors);
        Assert.assertEquals(1, m2.interceptors.length);
        Assert.assertEquals(OriginHandshakeInterceptor.class, m2.interceptors[0].getClass());
    }

    @Test
    public void interceptors() {
        WebSocketHandler handler = new TextWebSocketHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        this.registration.addHandler(handler, "/foo").addInterceptors(interceptor);
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping mapping = mappings.get(0);
        Assert.assertEquals(handler, mapping.webSocketHandler);
        Assert.assertEquals("/foo", mapping.path);
        Assert.assertNotNull(mapping.interceptors);
        Assert.assertEquals(2, mapping.interceptors.length);
        Assert.assertEquals(interceptor, mapping.interceptors[0]);
        Assert.assertEquals(OriginHandshakeInterceptor.class, mapping.interceptors[1].getClass());
    }

    @Test
    public void emptyAllowedOrigin() {
        WebSocketHandler handler = new TextWebSocketHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        this.registration.addHandler(handler, "/foo").addInterceptors(interceptor).setAllowedOrigins();
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping mapping = mappings.get(0);
        Assert.assertEquals(handler, mapping.webSocketHandler);
        Assert.assertEquals("/foo", mapping.path);
        Assert.assertNotNull(mapping.interceptors);
        Assert.assertEquals(2, mapping.interceptors.length);
        Assert.assertEquals(interceptor, mapping.interceptors[0]);
        Assert.assertEquals(OriginHandshakeInterceptor.class, mapping.interceptors[1].getClass());
    }

    @Test
    public void interceptorsWithAllowedOrigins() {
        WebSocketHandler handler = new TextWebSocketHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        this.registration.addHandler(handler, "/foo").addInterceptors(interceptor).setAllowedOrigins("http://mydomain1.com");
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping mapping = mappings.get(0);
        Assert.assertEquals(handler, mapping.webSocketHandler);
        Assert.assertEquals("/foo", mapping.path);
        Assert.assertNotNull(mapping.interceptors);
        Assert.assertEquals(2, mapping.interceptors.length);
        Assert.assertEquals(interceptor, mapping.interceptors[0]);
        Assert.assertEquals(OriginHandshakeInterceptor.class, mapping.interceptors[1].getClass());
    }

    @Test
    public void interceptorsPassedToSockJsRegistration() {
        WebSocketHandler handler = new TextWebSocketHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        this.registration.addHandler(handler, "/foo").addInterceptors(interceptor).setAllowedOrigins("http://mydomain1.com").withSockJS();
        getSockJsServiceRegistration().setTaskScheduler(this.taskScheduler);
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping mapping = mappings.get(0);
        Assert.assertEquals(handler, mapping.webSocketHandler);
        Assert.assertEquals("/foo/**", mapping.path);
        Assert.assertNotNull(mapping.sockJsService);
        Assert.assertTrue(mapping.sockJsService.getAllowedOrigins().contains("http://mydomain1.com"));
        List<HandshakeInterceptor> interceptors = mapping.sockJsService.getHandshakeInterceptors();
        Assert.assertEquals(interceptor, interceptors.get(0));
        Assert.assertEquals(OriginHandshakeInterceptor.class, interceptors.get(1).getClass());
    }

    @Test
    public void handshakeHandler() {
        WebSocketHandler handler = new TextWebSocketHandler();
        HandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        this.registration.addHandler(handler, "/foo").setHandshakeHandler(handshakeHandler);
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping mapping = mappings.get(0);
        Assert.assertEquals(handler, mapping.webSocketHandler);
        Assert.assertEquals("/foo", mapping.path);
        Assert.assertSame(handshakeHandler, mapping.handshakeHandler);
    }

    @Test
    public void handshakeHandlerPassedToSockJsRegistration() {
        WebSocketHandler handler = new TextWebSocketHandler();
        HandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        this.registration.addHandler(handler, "/foo").setHandshakeHandler(handshakeHandler).withSockJS();
        getSockJsServiceRegistration().setTaskScheduler(this.taskScheduler);
        List<WebSocketHandlerRegistrationTests.Mapping> mappings = getMappings();
        Assert.assertEquals(1, mappings.size());
        WebSocketHandlerRegistrationTests.Mapping mapping = mappings.get(0);
        Assert.assertEquals(handler, mapping.webSocketHandler);
        Assert.assertEquals("/foo/**", mapping.path);
        Assert.assertNotNull(mapping.sockJsService);
        WebSocketTransportHandler transportHandler = ((WebSocketTransportHandler) (mapping.sockJsService.getTransportHandlers().get(WEBSOCKET)));
        Assert.assertSame(handshakeHandler, transportHandler.getHandshakeHandler());
    }

    private static class TestWebSocketHandlerRegistration extends AbstractWebSocketHandlerRegistration<List<WebSocketHandlerRegistrationTests.Mapping>> {
        @Override
        protected List<WebSocketHandlerRegistrationTests.Mapping> createMappings() {
            return new ArrayList<>();
        }

        @Override
        protected void addSockJsServiceMapping(List<WebSocketHandlerRegistrationTests.Mapping> mappings, SockJsService sockJsService, WebSocketHandler wsHandler, String pathPattern) {
            mappings.add(new WebSocketHandlerRegistrationTests.Mapping(wsHandler, pathPattern, sockJsService));
        }

        @Override
        protected void addWebSocketHandlerMapping(List<WebSocketHandlerRegistrationTests.Mapping> mappings, WebSocketHandler handler, HandshakeHandler handshakeHandler, HandshakeInterceptor[] interceptors, String path) {
            mappings.add(new WebSocketHandlerRegistrationTests.Mapping(handler, path, handshakeHandler, interceptors));
        }
    }

    private static class Mapping {
        private final WebSocketHandler webSocketHandler;

        private final String path;

        private final HandshakeHandler handshakeHandler;

        private final HandshakeInterceptor[] interceptors;

        private final DefaultSockJsService sockJsService;

        public Mapping(WebSocketHandler handler, String path, SockJsService sockJsService) {
            this.webSocketHandler = handler;
            this.path = path;
            this.handshakeHandler = null;
            this.interceptors = null;
            this.sockJsService = ((DefaultSockJsService) (sockJsService));
        }

        public Mapping(WebSocketHandler h, String path, HandshakeHandler hh, HandshakeInterceptor[] interceptors) {
            this.webSocketHandler = h;
            this.path = path;
            this.handshakeHandler = hh;
            this.interceptors = interceptors;
            this.sockJsService = null;
        }
    }
}

