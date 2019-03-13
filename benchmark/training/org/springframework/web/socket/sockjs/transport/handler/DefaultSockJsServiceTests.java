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
package org.springframework.web.socket.sockjs.transport.handler;


import HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaders.ORIGIN;
import TransportType.EVENT_SOURCE;
import TransportType.HTML_FILE;
import TransportType.WEBSOCKET;
import TransportType.XHR;
import TransportType.XHR_SEND;
import TransportType.XHR_STREAMING;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.handler.TestPrincipal;
import org.springframework.web.socket.server.HandshakeHandler;
import org.springframework.web.socket.server.support.OriginHandshakeInterceptor;
import org.springframework.web.socket.sockjs.transport.SockJsSessionFactory;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportHandlingSockJsService;
import org.springframework.web.socket.sockjs.transport.TransportType;
import org.springframework.web.socket.sockjs.transport.session.TestSockJsSession;


/**
 * Test fixture for {@link DefaultSockJsService}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 * @author Ben Kiefer
 */
public class DefaultSockJsServiceTests extends AbstractHttpRequestTests {
    private static final String sockJsPrefix = "/mysockjs";

    private static final String sessionId = "session1";

    private static final String sessionUrlPrefix = ("/server1/" + (DefaultSockJsServiceTests.sessionId)) + "/";

    @Mock
    private DefaultSockJsServiceTests.SessionCreatingTransportHandler xhrHandler;

    @Mock
    private TransportHandler xhrSendHandler;

    @Mock
    private DefaultSockJsServiceTests.HandshakeTransportHandler wsTransportHandler;

    @Mock
    private WebSocketHandler wsHandler;

    @Mock
    private TaskScheduler taskScheduler;

    private TestSockJsSession session;

    private TransportHandlingSockJsService service;

    @Test
    public void defaultTransportHandlers() {
        DefaultSockJsService service = new DefaultSockJsService(Mockito.mock(TaskScheduler.class));
        Map<TransportType, TransportHandler> handlers = service.getTransportHandlers();
        Assert.assertEquals(6, handlers.size());
        Assert.assertNotNull(handlers.get(WEBSOCKET));
        Assert.assertNotNull(handlers.get(XHR));
        Assert.assertNotNull(handlers.get(XHR_SEND));
        Assert.assertNotNull(handlers.get(XHR_STREAMING));
        Assert.assertNotNull(handlers.get(HTML_FILE));
        Assert.assertNotNull(handlers.get(EVENT_SOURCE));
    }

    @Test
    public void defaultTransportHandlersWithOverride() {
        XhrReceivingTransportHandler xhrHandler = new XhrReceivingTransportHandler();
        DefaultSockJsService service = new DefaultSockJsService(Mockito.mock(TaskScheduler.class), xhrHandler);
        Map<TransportType, TransportHandler> handlers = service.getTransportHandlers();
        Assert.assertEquals(6, handlers.size());
        Assert.assertSame(xhrHandler, handlers.get(xhrHandler.getTransportType()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidAllowedOrigins() {
        this.service.setAllowedOrigins(null);
    }

    @Test
    public void customizedTransportHandlerList() {
        TransportHandlingSockJsService service = new TransportHandlingSockJsService(Mockito.mock(TaskScheduler.class), new XhrPollingTransportHandler(), new XhrReceivingTransportHandler());
        Map<TransportType, TransportHandler> actualHandlers = service.getTransportHandlers();
        Assert.assertEquals(2, actualHandlers.size());
    }

    @Test
    public void handleTransportRequestXhr() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(200, this.servletResponse.getStatus());
        Mockito.verify(this.xhrHandler).handleRequest(this.request, this.response, this.wsHandler, this.session);
        Mockito.verify(taskScheduler).scheduleAtFixedRate(ArgumentMatchers.any(Runnable.class), ArgumentMatchers.eq(service.getDisconnectDelay()));
        Assert.assertEquals("no-store, no-cache, must-revalidate, max-age=0", this.response.getHeaders().getCacheControl());
        Assert.assertNull(this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertNull(this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
    }

    // SPR-12226
    @Test
    public void handleTransportRequestXhrAllowedOriginsMatch() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.setAllowedOrigins(Arrays.asList("http://mydomain1.com", "http://mydomain2.com"));
        this.servletRequest.addHeader(ORIGIN, "http://mydomain1.com");
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(200, this.servletResponse.getStatus());
    }

    // SPR-12226
    @Test
    public void handleTransportRequestXhrAllowedOriginsNoMatch() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.setAllowedOrigins(Arrays.asList("http://mydomain1.com", "http://mydomain2.com"));
        this.servletRequest.addHeader(ORIGIN, "http://mydomain3.com");
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(403, this.servletResponse.getStatus());
    }

    // SPR-13464
    @Test
    public void handleTransportRequestXhrSameOrigin() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.setAllowedOrigins(Arrays.asList("http://mydomain1.com"));
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        this.servletRequest.setServerName("mydomain2.com");
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(200, this.servletResponse.getStatus());
    }

    // SPR-13545
    @Test
    public void handleInvalidTransportType() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "invalid";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.setAllowedOrigins(Arrays.asList("http://mydomain1.com"));
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        this.servletRequest.setServerName("mydomain2.com");
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(404, this.servletResponse.getStatus());
    }

    @Test
    public void handleTransportRequestXhrOptions() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("OPTIONS", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(204, this.servletResponse.getStatus());
        Assert.assertNull(this.servletResponse.getHeader("Access-Control-Allow-Origin"));
        Assert.assertNull(this.servletResponse.getHeader("Access-Control-Allow-Credentials"));
        Assert.assertNull(this.servletResponse.getHeader("Access-Control-Allow-Methods"));
    }

    @Test
    public void handleTransportRequestNoSuitableHandler() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "eventsource";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(404, this.servletResponse.getStatus());
    }

    @Test
    public void handleTransportRequestXhrSend() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr_send";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(404, this.servletResponse.getStatus());// no session yet

        resetResponse();
        sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(200, this.servletResponse.getStatus());// session created

        Mockito.verify(this.xhrHandler).handleRequest(this.request, this.response, this.wsHandler, this.session);
        resetResponse();
        sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr_send";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        BDDMockito.given(this.xhrSendHandler.checkSessionType(this.session)).willReturn(true);
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(200, this.servletResponse.getStatus());// session exists

        Mockito.verify(this.xhrSendHandler).handleRequest(this.request, this.response, this.wsHandler, this.session);
    }

    @Test
    public void handleTransportRequestXhrSendWithDifferentUser() throws Exception {
        String sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(200, this.servletResponse.getStatus());// session created

        Mockito.verify(this.xhrHandler).handleRequest(this.request, this.response, this.wsHandler, this.session);
        this.session.setPrincipal(new TestPrincipal("little red riding hood"));
        this.servletRequest.setUserPrincipal(new TestPrincipal("wolf"));
        resetResponse();
        Mockito.reset(this.xhrSendHandler);
        sockJsPath = (DefaultSockJsServiceTests.sessionUrlPrefix) + "xhr_send";
        setRequest("POST", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(404, this.servletResponse.getStatus());
        Mockito.verifyNoMoreInteractions(this.xhrSendHandler);
    }

    @Test
    public void handleTransportRequestWebsocket() throws Exception {
        TransportHandlingSockJsService wsService = new TransportHandlingSockJsService(this.taskScheduler, this.wsTransportHandler);
        String sockJsPath = "/websocket";
        setRequest("GET", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        wsService.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertNotEquals(403, this.servletResponse.getStatus());
        resetRequestAndResponse();
        List<String> allowed = Collections.singletonList("http://mydomain1.com");
        OriginHandshakeInterceptor interceptor = new OriginHandshakeInterceptor(allowed);
        wsService.setHandshakeInterceptors(Collections.singletonList(interceptor));
        setRequest("GET", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.servletRequest.addHeader(ORIGIN, "http://mydomain1.com");
        wsService.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertNotEquals(403, this.servletResponse.getStatus());
        resetRequestAndResponse();
        setRequest("GET", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        wsService.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(403, this.servletResponse.getStatus());
    }

    @Test
    public void handleTransportRequestIframe() throws Exception {
        String sockJsPath = "/iframe.html";
        setRequest("GET", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertNotEquals(404, this.servletResponse.getStatus());
        Assert.assertEquals("SAMEORIGIN", this.servletResponse.getHeader("X-Frame-Options"));
        resetRequestAndResponse();
        setRequest("GET", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertEquals(404, this.servletResponse.getStatus());
        Assert.assertNull(this.servletResponse.getHeader("X-Frame-Options"));
        resetRequestAndResponse();
        setRequest("GET", ((DefaultSockJsServiceTests.sockJsPrefix) + sockJsPath));
        this.service.setAllowedOrigins(Collections.singletonList("*"));
        this.service.handleRequest(this.request, this.response, sockJsPath, this.wsHandler);
        Assert.assertNotEquals(404, this.servletResponse.getStatus());
        Assert.assertNull(this.servletResponse.getHeader("X-Frame-Options"));
    }

    interface SessionCreatingTransportHandler extends SockJsSessionFactory , TransportHandler {}

    interface HandshakeTransportHandler extends HandshakeHandler , TransportHandler {}
}

