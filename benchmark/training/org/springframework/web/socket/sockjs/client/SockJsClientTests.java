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


import java.net.URI;
import java.net.URISyntaxException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;


/**
 * Unit tests for {@link org.springframework.web.socket.sockjs.client.SockJsClient}.
 *
 * @author Rossen Stoyanchev
 */
public class SockJsClientTests {
    private static final String URL = "http://example.com";

    private static final WebSocketHandler handler = Mockito.mock(WebSocketHandler.class);

    private SockJsClient sockJsClient;

    private InfoReceiver infoReceiver;

    private TestTransport webSocketTransport;

    private TestTransport.XhrTestTransport xhrTransport;

    private ListenableFutureCallback<WebSocketSession> connectCallback;

    @Test
    public void connectWebSocket() throws Exception {
        setupInfoRequest(true);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL).addCallback(this.connectCallback);
        Assert.assertTrue(this.webSocketTransport.invoked());
        WebSocketSession session = Mockito.mock(WebSocketSession.class);
        this.webSocketTransport.getConnectCallback().onSuccess(session);
        Mockito.verify(this.connectCallback).onSuccess(session);
        Mockito.verifyNoMoreInteractions(this.connectCallback);
    }

    @Test
    public void connectWebSocketDisabled() throws URISyntaxException {
        setupInfoRequest(false);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL);
        Assert.assertFalse(this.webSocketTransport.invoked());
        Assert.assertTrue(this.xhrTransport.invoked());
        Assert.assertTrue(this.xhrTransport.getRequest().getTransportUrl().toString().endsWith("xhr_streaming"));
    }

    @Test
    public void connectXhrStreamingDisabled() throws Exception {
        setupInfoRequest(false);
        this.xhrTransport.setStreamingDisabled(true);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL).addCallback(this.connectCallback);
        Assert.assertFalse(this.webSocketTransport.invoked());
        Assert.assertTrue(this.xhrTransport.invoked());
        Assert.assertTrue(this.xhrTransport.getRequest().getTransportUrl().toString().endsWith("xhr"));
    }

    // SPR-13254
    @Test
    public void connectWithHandshakeHeaders() throws Exception {
        ArgumentCaptor<HttpHeaders> headersCaptor = setupInfoRequest(false);
        this.xhrTransport.setStreamingDisabled(true);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set("foo", "bar");
        headers.set("auth", "123");
        this.sockJsClient.doHandshake(SockJsClientTests.handler, headers, new URI(SockJsClientTests.URL)).addCallback(this.connectCallback);
        HttpHeaders httpHeaders = headersCaptor.getValue();
        Assert.assertEquals(2, httpHeaders.size());
        Assert.assertEquals("bar", httpHeaders.getFirst("foo"));
        Assert.assertEquals("123", httpHeaders.getFirst("auth"));
        httpHeaders = this.xhrTransport.getRequest().getHttpRequestHeaders();
        Assert.assertEquals(2, httpHeaders.size());
        Assert.assertEquals("bar", httpHeaders.getFirst("foo"));
        Assert.assertEquals("123", httpHeaders.getFirst("auth"));
    }

    @Test
    public void connectAndUseSubsetOfHandshakeHeadersForHttpRequests() throws Exception {
        ArgumentCaptor<HttpHeaders> headersCaptor = setupInfoRequest(false);
        this.xhrTransport.setStreamingDisabled(true);
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        headers.set("foo", "bar");
        headers.set("auth", "123");
        this.sockJsClient.setHttpHeaderNames("auth");
        this.sockJsClient.doHandshake(SockJsClientTests.handler, headers, new URI(SockJsClientTests.URL)).addCallback(this.connectCallback);
        Assert.assertEquals(1, headersCaptor.getValue().size());
        Assert.assertEquals("123", headersCaptor.getValue().getFirst("auth"));
        Assert.assertEquals(1, this.xhrTransport.getRequest().getHttpRequestHeaders().size());
        Assert.assertEquals("123", this.xhrTransport.getRequest().getHttpRequestHeaders().getFirst("auth"));
    }

    @Test
    public void connectSockJsInfo() throws Exception {
        setupInfoRequest(true);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL);
        Mockito.verify(this.infoReceiver, Mockito.times(1)).executeInfoRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void connectSockJsInfoCached() throws Exception {
        setupInfoRequest(true);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL);
        Mockito.verify(this.infoReceiver, Mockito.times(1)).executeInfoRequest(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void connectInfoRequestFailure() throws URISyntaxException {
        HttpServerErrorException exception = new HttpServerErrorException(HttpStatus.SERVICE_UNAVAILABLE);
        BDDMockito.given(this.infoReceiver.executeInfoRequest(ArgumentMatchers.any(), ArgumentMatchers.any())).willThrow(exception);
        this.sockJsClient.doHandshake(SockJsClientTests.handler, SockJsClientTests.URL).addCallback(this.connectCallback);
        Mockito.verify(this.connectCallback).onFailure(exception);
        Assert.assertFalse(this.webSocketTransport.invoked());
        Assert.assertFalse(this.xhrTransport.invoked());
    }
}

