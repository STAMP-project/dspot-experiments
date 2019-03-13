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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.MultiValueMap;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.socket.messaging.SubProtocolWebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.server.support.WebSocketHttpRequestHandler;
import org.springframework.web.socket.sockjs.support.SockJsHttpRequestHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.handler.DefaultSockJsService;
import org.springframework.web.socket.sockjs.transport.handler.WebSocketTransportHandler;


/**
 * Test fixture for
 * {@link org.springframework.web.socket.config.annotation.WebMvcStompWebSocketEndpointRegistration}.
 *
 * @author Rossen Stoyanchev
 */
public class WebMvcStompWebSocketEndpointRegistrationTests {
    private SubProtocolWebSocketHandler handler;

    private TaskScheduler scheduler;

    @Test
    public void minimalRegistration() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        Map.Entry<HttpRequestHandler, List<String>> entry = mappings.entrySet().iterator().next();
        Assert.assertNotNull(getWebSocketHandler());
        Assert.assertEquals(1, getHandshakeInterceptors().size());
        Assert.assertEquals(Arrays.asList("/foo"), entry.getValue());
    }

    @Test
    public void allowedOrigins() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        registration.setAllowedOrigins();
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        HttpRequestHandler handler = mappings.entrySet().iterator().next().getKey();
        WebSocketHttpRequestHandler wsHandler = ((WebSocketHttpRequestHandler) (handler));
        Assert.assertNotNull(wsHandler.getWebSocketHandler());
        Assert.assertEquals(1, wsHandler.getHandshakeInterceptors().size());
        Assert.assertEquals(OriginHandshakeInterceptor.class, wsHandler.getHandshakeInterceptors().get(0).getClass());
    }

    @Test
    public void sameOrigin() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        registration.setAllowedOrigins();
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        HttpRequestHandler handler = mappings.entrySet().iterator().next().getKey();
        WebSocketHttpRequestHandler wsHandler = ((WebSocketHttpRequestHandler) (handler));
        Assert.assertNotNull(wsHandler.getWebSocketHandler());
        Assert.assertEquals(1, wsHandler.getHandshakeInterceptors().size());
        Assert.assertEquals(OriginHandshakeInterceptor.class, wsHandler.getHandshakeInterceptors().get(0).getClass());
    }

    @Test
    public void allowedOriginsWithSockJsService() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        String origin = "http://mydomain.com";
        registration.setAllowedOrigins(origin).withSockJS();
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        SockJsHttpRequestHandler requestHandler = ((SockJsHttpRequestHandler) (mappings.entrySet().iterator().next().getKey()));
        Assert.assertNotNull(requestHandler.getSockJsService());
        DefaultSockJsService sockJsService = ((DefaultSockJsService) (requestHandler.getSockJsService()));
        Assert.assertTrue(sockJsService.getAllowedOrigins().contains(origin));
        Assert.assertFalse(sockJsService.shouldSuppressCors());
        registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        registration.withSockJS().setAllowedOrigins(origin);
        mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        requestHandler = ((SockJsHttpRequestHandler) (mappings.entrySet().iterator().next().getKey()));
        Assert.assertNotNull(requestHandler.getSockJsService());
        sockJsService = ((DefaultSockJsService) (requestHandler.getSockJsService()));
        Assert.assertTrue(sockJsService.getAllowedOrigins().contains(origin));
        Assert.assertFalse(sockJsService.shouldSuppressCors());
    }

    // SPR-12283
    @Test
    public void disableCorsWithSockJsService() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        registration.withSockJS().setSupressCors(true);
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        SockJsHttpRequestHandler requestHandler = ((SockJsHttpRequestHandler) (mappings.entrySet().iterator().next().getKey()));
        Assert.assertNotNull(requestHandler.getSockJsService());
        DefaultSockJsService sockJsService = ((DefaultSockJsService) (requestHandler.getSockJsService()));
        Assert.assertTrue(sockJsService.shouldSuppressCors());
    }

    @Test
    public void handshakeHandlerAndInterceptor() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        DefaultHandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        registration.setHandshakeHandler(handshakeHandler).addInterceptors(interceptor);
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        Map.Entry<HttpRequestHandler, List<String>> entry = mappings.entrySet().iterator().next();
        Assert.assertEquals(Arrays.asList("/foo"), entry.getValue());
        WebSocketHttpRequestHandler requestHandler = ((WebSocketHttpRequestHandler) (entry.getKey()));
        Assert.assertNotNull(requestHandler.getWebSocketHandler());
        Assert.assertSame(handshakeHandler, requestHandler.getHandshakeHandler());
        Assert.assertEquals(2, requestHandler.getHandshakeInterceptors().size());
        Assert.assertEquals(interceptor, requestHandler.getHandshakeInterceptors().get(0));
        Assert.assertEquals(OriginHandshakeInterceptor.class, requestHandler.getHandshakeInterceptors().get(1).getClass());
    }

    @Test
    public void handshakeHandlerAndInterceptorWithAllowedOrigins() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        DefaultHandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        String origin = "http://mydomain.com";
        registration.setHandshakeHandler(handshakeHandler).addInterceptors(interceptor).setAllowedOrigins(origin);
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        Map.Entry<HttpRequestHandler, List<String>> entry = mappings.entrySet().iterator().next();
        Assert.assertEquals(Arrays.asList("/foo"), entry.getValue());
        WebSocketHttpRequestHandler requestHandler = ((WebSocketHttpRequestHandler) (entry.getKey()));
        Assert.assertNotNull(requestHandler.getWebSocketHandler());
        Assert.assertSame(handshakeHandler, requestHandler.getHandshakeHandler());
        Assert.assertEquals(2, requestHandler.getHandshakeInterceptors().size());
        Assert.assertEquals(interceptor, requestHandler.getHandshakeInterceptors().get(0));
        Assert.assertEquals(OriginHandshakeInterceptor.class, requestHandler.getHandshakeInterceptors().get(1).getClass());
    }

    @Test
    public void handshakeHandlerInterceptorWithSockJsService() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        DefaultHandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        registration.setHandshakeHandler(handshakeHandler).addInterceptors(interceptor).withSockJS();
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        Map.Entry<HttpRequestHandler, List<String>> entry = mappings.entrySet().iterator().next();
        Assert.assertEquals(Arrays.asList("/foo/**"), entry.getValue());
        SockJsHttpRequestHandler requestHandler = ((SockJsHttpRequestHandler) (entry.getKey()));
        Assert.assertNotNull(requestHandler.getWebSocketHandler());
        DefaultSockJsService sockJsService = ((DefaultSockJsService) (requestHandler.getSockJsService()));
        Assert.assertNotNull(sockJsService);
        Map<TransportType, TransportHandler> handlers = sockJsService.getTransportHandlers();
        WebSocketTransportHandler transportHandler = ((WebSocketTransportHandler) (handlers.get(WEBSOCKET)));
        Assert.assertSame(handshakeHandler, transportHandler.getHandshakeHandler());
        Assert.assertEquals(2, sockJsService.getHandshakeInterceptors().size());
        Assert.assertEquals(interceptor, sockJsService.getHandshakeInterceptors().get(0));
        Assert.assertEquals(OriginHandshakeInterceptor.class, sockJsService.getHandshakeInterceptors().get(1).getClass());
    }

    @Test
    public void handshakeHandlerInterceptorWithSockJsServiceAndAllowedOrigins() {
        WebMvcStompWebSocketEndpointRegistration registration = new WebMvcStompWebSocketEndpointRegistration(new String[]{ "/foo" }, this.handler, this.scheduler);
        DefaultHandshakeHandler handshakeHandler = new DefaultHandshakeHandler();
        HttpSessionHandshakeInterceptor interceptor = new HttpSessionHandshakeInterceptor();
        String origin = "http://mydomain.com";
        registration.setHandshakeHandler(handshakeHandler).addInterceptors(interceptor).setAllowedOrigins(origin).withSockJS();
        MultiValueMap<HttpRequestHandler, String> mappings = registration.getMappings();
        Assert.assertEquals(1, mappings.size());
        Map.Entry<HttpRequestHandler, List<String>> entry = mappings.entrySet().iterator().next();
        Assert.assertEquals(Arrays.asList("/foo/**"), entry.getValue());
        SockJsHttpRequestHandler requestHandler = ((SockJsHttpRequestHandler) (entry.getKey()));
        Assert.assertNotNull(requestHandler.getWebSocketHandler());
        DefaultSockJsService sockJsService = ((DefaultSockJsService) (requestHandler.getSockJsService()));
        Assert.assertNotNull(sockJsService);
        Map<TransportType, TransportHandler> handlers = sockJsService.getTransportHandlers();
        WebSocketTransportHandler transportHandler = ((WebSocketTransportHandler) (handlers.get(WEBSOCKET)));
        Assert.assertSame(handshakeHandler, transportHandler.getHandshakeHandler());
        Assert.assertEquals(2, sockJsService.getHandshakeInterceptors().size());
        Assert.assertEquals(interceptor, sockJsService.getHandshakeInterceptors().get(0));
        Assert.assertEquals(OriginHandshakeInterceptor.class, sockJsService.getHandshakeInterceptors().get(1).getClass());
        Assert.assertTrue(sockJsService.getAllowedOrigins().contains(origin));
    }
}

