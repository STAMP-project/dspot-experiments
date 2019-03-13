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
package org.springframework.web.socket.sockjs.support;


import HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS;
import HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS;
import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.ORIGIN;
import HttpHeaders.VARY;
import HttpStatus.FORBIDDEN;
import HttpStatus.NOT_FOUND;
import HttpStatus.NOT_MODIFIED;
import HttpStatus.NO_CONTENT;
import HttpStatus.OK;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.socket.AbstractHttpRequestTests;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.sockjs.SockJsException;


/**
 * Test fixture for {@link AbstractSockJsService}.
 *
 * @author Rossen Stoyanchev
 * @author Sebastien Deleuze
 */
public class SockJsServiceTests extends AbstractHttpRequestTests {
    private SockJsServiceTests.TestSockJsService service;

    private WebSocketHandler handler;

    @Test
    public void validateRequest() {
        setWebSocketEnabled(false);
        resetResponseAndHandleRequest("GET", "/echo/server/session/websocket", NOT_FOUND);
        setWebSocketEnabled(true);
        resetResponseAndHandleRequest("GET", "/echo/server/session/websocket", OK);
        resetResponseAndHandleRequest("GET", "/echo//", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo///", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo/other", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo//service/websocket", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo/server//websocket", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo/server/session/", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo/s.erver/session/websocket", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo/server/s.ession/websocket", NOT_FOUND);
        resetResponseAndHandleRequest("GET", "/echo/server/session/jsonp;Setup.pl", NOT_FOUND);
    }

    @Test
    public void handleInfoGet() throws IOException {
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        Assert.assertEquals("application/json;charset=UTF-8", this.servletResponse.getContentType());
        String header = this.servletResponse.getHeader(CACHE_CONTROL);
        Assert.assertEquals("no-store, no-cache, must-revalidate, max-age=0", header);
        Assert.assertNull(this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertNull(this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertNull(this.servletResponse.getHeader(VARY));
        String body = this.servletResponse.getContentAsString();
        Assert.assertEquals("{\"entropy\"", body.substring(0, body.indexOf(':')));
        Assert.assertEquals(",\"origins\":[\"*:*\"],\"cookie_needed\":true,\"websocket\":true}", body.substring(body.indexOf(',')));
        setSessionCookieNeeded(false);
        setWebSocketEnabled(false);
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        body = this.servletResponse.getContentAsString();
        Assert.assertEquals(",\"origins\":[\"*:*\"],\"cookie_needed\":false,\"websocket\":false}", body.substring(body.indexOf(',')));
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        Assert.assertNull(this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
        Assert.assertNull(this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_CREDENTIALS));
        Assert.assertNull(this.servletResponse.getHeader(VARY));
    }

    // SPR-12226 and SPR-12660
    @Test
    public void handleInfoGetWithOrigin() throws IOException {
        this.servletRequest.setServerName("mydomain2.com");
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        Assert.assertEquals("application/json;charset=UTF-8", this.servletResponse.getContentType());
        String header = this.servletResponse.getHeader(CACHE_CONTROL);
        Assert.assertEquals("no-store, no-cache, must-revalidate, max-age=0", header);
        String body = this.servletResponse.getContentAsString();
        Assert.assertEquals("{\"entropy\"", body.substring(0, body.indexOf(':')));
        Assert.assertEquals(",\"origins\":[\"*:*\"],\"cookie_needed\":true,\"websocket\":true}", body.substring(body.indexOf(',')));
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        setAllowedOrigins(Arrays.asList("http://mydomain1.com", "http://mydomain2.com", "http://mydomain3.com"));
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        setAllowedOrigins(Collections.singletonList("*"));
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        this.servletRequest.setServerName("mydomain3.com");
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("GET", "/echo/info", FORBIDDEN);
    }

    // SPR-11443
    @Test
    public void handleInfoGetCorsFilter() {
        // Simulate scenario where Filter would have already set CORS headers
        this.servletResponse.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN, "foobar:123");
        handleRequest("GET", "/echo/info", OK);
        Assert.assertEquals("foobar:123", this.servletResponse.getHeader(ACCESS_CONTROL_ALLOW_ORIGIN));
    }

    // SPR-11919
    @Test
    public void handleInfoGetWildflyNPE() throws IOException {
        HttpServletResponse mockResponse = Mockito.mock(HttpServletResponse.class);
        ServletOutputStream ous = Mockito.mock(ServletOutputStream.class);
        BDDMockito.given(mockResponse.getHeaders(ACCESS_CONTROL_ALLOW_ORIGIN)).willThrow(NullPointerException.class);
        BDDMockito.given(mockResponse.getOutputStream()).willReturn(ous);
        this.response = new org.springframework.http.server.ServletServerHttpResponse(mockResponse);
        handleRequest("GET", "/echo/info", OK);
        Mockito.verify(mockResponse, Mockito.times(1)).getOutputStream();
    }

    // SPR-12660
    @Test
    public void handleInfoOptions() {
        this.servletRequest.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Last-Modified");
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNull(this.service.getCorsConfiguration(this.servletRequest));
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNull(this.service.getCorsConfiguration(this.servletRequest));
    }

    // SPR-12226 and SPR-12660
    @Test
    public void handleInfoOptionsWithAllowedOrigin() {
        this.servletRequest.setServerName("mydomain2.com");
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        this.servletRequest.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.servletRequest.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Last-Modified");
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNotNull(this.service.getCorsConfiguration(this.servletRequest));
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNotNull(this.service.getCorsConfiguration(this.servletRequest));
        setAllowedOrigins(Arrays.asList("http://mydomain1.com", "http://mydomain2.com", "http://mydomain3.com"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNotNull(this.service.getCorsConfiguration(this.servletRequest));
        setAllowedOrigins(Collections.singletonList("*"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNotNull(this.service.getCorsConfiguration(this.servletRequest));
    }

    // SPR-16304
    @Test
    public void handleInfoOptionsWithForbiddenOrigin() {
        this.servletRequest.setServerName("mydomain3.com");
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        this.servletRequest.addHeader(ACCESS_CONTROL_REQUEST_METHOD, "GET");
        this.servletRequest.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Last-Modified");
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", FORBIDDEN);
        CorsConfiguration corsConfiguration = this.service.getCorsConfiguration(this.servletRequest);
        Assert.assertTrue(corsConfiguration.getAllowedOrigins().isEmpty());
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", FORBIDDEN);
        corsConfiguration = this.service.getCorsConfiguration(this.servletRequest);
        Assert.assertEquals(Collections.singletonList("http://mydomain1.com"), corsConfiguration.getAllowedOrigins());
    }

    // SPR-12283
    @Test
    public void handleInfoOptionsWithOriginAndCorsHeadersDisabled() {
        this.servletRequest.addHeader(ORIGIN, "http://mydomain2.com");
        setAllowedOrigins(Collections.singletonList("*"));
        setSuppressCors(true);
        this.servletRequest.addHeader(ACCESS_CONTROL_REQUEST_HEADERS, "Last-Modified");
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNull(this.service.getCorsConfiguration(this.servletRequest));
        setAllowedOrigins(Collections.singletonList("http://mydomain1.com"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", FORBIDDEN);
        Assert.assertNull(this.service.getCorsConfiguration(this.servletRequest));
        setAllowedOrigins(Arrays.asList("http://mydomain1.com", "http://mydomain2.com", "http://mydomain3.com"));
        resetResponseAndHandleRequest("OPTIONS", "/echo/info", NO_CONTENT);
        Assert.assertNull(this.service.getCorsConfiguration(this.servletRequest));
    }

    @Test
    public void handleIframeRequest() throws IOException {
        resetResponseAndHandleRequest("GET", "/echo/iframe.html", OK);
        Assert.assertEquals("text/html;charset=UTF-8", this.servletResponse.getContentType());
        Assert.assertTrue(this.servletResponse.getContentAsString().startsWith("<!DOCTYPE html>\n"));
        Assert.assertEquals(490, this.servletResponse.getContentLength());
        Assert.assertEquals("no-store, no-cache, must-revalidate, max-age=0", this.response.getHeaders().getCacheControl());
        Assert.assertEquals("\"0096cbd37f2a5218c33bb0826a7c74cbf\"", this.response.getHeaders().getETag());
    }

    @Test
    public void handleIframeRequestNotModified() {
        this.servletRequest.addHeader("If-None-Match", "\"0096cbd37f2a5218c33bb0826a7c74cbf\"");
        resetResponseAndHandleRequest("GET", "/echo/iframe.html", NOT_MODIFIED);
    }

    @Test
    public void handleRawWebSocketRequest() throws IOException {
        resetResponseAndHandleRequest("GET", "/echo", OK);
        Assert.assertEquals("Welcome to SockJS!\n", this.servletResponse.getContentAsString());
        resetResponseAndHandleRequest("GET", "/echo/websocket", OK);
        Assert.assertNull("Raw WebSocket should not open a SockJS session", this.service.sessionId);
        Assert.assertSame(this.handler, this.service.handler);
    }

    @Test
    public void handleEmptyContentType() {
        this.servletRequest.setContentType("");
        resetResponseAndHandleRequest("GET", "/echo/info", OK);
        Assert.assertEquals("Invalid/empty content should have been ignored", 200, this.servletResponse.getStatus());
    }

    private static class TestSockJsService extends AbstractSockJsService {
        private String sessionId;

        @SuppressWarnings("unused")
        private String transport;

        private WebSocketHandler handler;

        public TestSockJsService(TaskScheduler scheduler) {
            super(scheduler);
        }

        @Override
        protected void handleRawWebSocketRequest(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler handler) throws IOException {
            this.handler = handler;
        }

        @Override
        protected void handleTransportRequest(ServerHttpRequest req, ServerHttpResponse res, WebSocketHandler handler, String sessionId, String transport) throws SockJsException {
            this.sessionId = sessionId;
            this.transport = transport;
            this.handler = handler;
        }
    }
}

