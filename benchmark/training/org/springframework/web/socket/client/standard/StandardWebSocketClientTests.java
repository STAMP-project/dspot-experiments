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
package org.springframework.web.socket.client.standard;


import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.WebSocketContainer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;


/**
 * Test fixture for {@link StandardWebSocketClient}.
 *
 * @author Rossen Stoyanchev
 */
public class StandardWebSocketClientTests {
    private StandardWebSocketClient wsClient;

    private WebSocketContainer wsContainer;

    private WebSocketHandler wsHandler;

    private WebSocketHttpHeaders headers;

    @Test
    public void testGetLocalAddress() throws Exception {
        URI uri = new URI("ws://localhost/abc");
        WebSocketSession session = this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        Assert.assertNotNull(session.getLocalAddress());
        Assert.assertEquals(80, session.getLocalAddress().getPort());
    }

    @Test
    public void testGetLocalAddressWss() throws Exception {
        URI uri = new URI("wss://localhost/abc");
        WebSocketSession session = this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        Assert.assertNotNull(session.getLocalAddress());
        Assert.assertEquals(443, session.getLocalAddress().getPort());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetLocalAddressNoScheme() throws Exception {
        URI uri = new URI("localhost/abc");
        this.wsClient.doHandshake(this.wsHandler, this.headers, uri);
    }

    @Test
    public void testGetRemoteAddress() throws Exception {
        URI uri = new URI("wss://localhost/abc");
        WebSocketSession session = this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        Assert.assertNotNull(session.getRemoteAddress());
        Assert.assertEquals("localhost", session.getRemoteAddress().getHostName());
        Assert.assertEquals(443, session.getLocalAddress().getPort());
    }

    @Test
    public void handshakeHeaders() throws Exception {
        URI uri = new URI("ws://localhost/abc");
        List<String> protocols = Collections.singletonList("abc");
        this.headers.setSecWebSocketProtocol(protocols);
        this.headers.add("foo", "bar");
        WebSocketSession session = this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        Assert.assertEquals(1, session.getHandshakeHeaders().size());
        Assert.assertEquals("bar", session.getHandshakeHeaders().getFirst("foo"));
    }

    @Test
    public void clientEndpointConfig() throws Exception {
        URI uri = new URI("ws://localhost/abc");
        List<String> protocols = Collections.singletonList("abc");
        this.headers.setSecWebSocketProtocol(protocols);
        this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        ArgumentCaptor<ClientEndpointConfig> captor = ArgumentCaptor.forClass(ClientEndpointConfig.class);
        Mockito.verify(this.wsContainer).connectToServer(ArgumentMatchers.any(Endpoint.class), captor.capture(), ArgumentMatchers.any(URI.class));
        ClientEndpointConfig endpointConfig = captor.getValue();
        Assert.assertEquals(protocols, endpointConfig.getPreferredSubprotocols());
    }

    @Test
    public void clientEndpointConfigWithUserProperties() throws Exception {
        Map<String, Object> userProperties = Collections.singletonMap("foo", "bar");
        URI uri = new URI("ws://localhost/abc");
        this.wsClient.setUserProperties(userProperties);
        this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        ArgumentCaptor<ClientEndpointConfig> captor = ArgumentCaptor.forClass(ClientEndpointConfig.class);
        Mockito.verify(this.wsContainer).connectToServer(ArgumentMatchers.any(Endpoint.class), captor.capture(), ArgumentMatchers.any(URI.class));
        ClientEndpointConfig endpointConfig = captor.getValue();
        Assert.assertEquals(userProperties, endpointConfig.getUserProperties());
    }

    @Test
    public void standardWebSocketClientConfiguratorInsertsHandshakeHeaders() throws Exception {
        URI uri = new URI("ws://localhost/abc");
        this.headers.add("foo", "bar");
        this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        ArgumentCaptor<ClientEndpointConfig> captor = ArgumentCaptor.forClass(ClientEndpointConfig.class);
        Mockito.verify(this.wsContainer).connectToServer(ArgumentMatchers.any(Endpoint.class), captor.capture(), ArgumentMatchers.any(URI.class));
        ClientEndpointConfig endpointConfig = captor.getValue();
        Map<String, List<String>> headers = new HashMap<>();
        endpointConfig.getConfigurator().beforeRequest(headers);
        Assert.assertEquals(1, headers.size());
    }

    @Test
    public void taskExecutor() throws Exception {
        URI uri = new URI("ws://localhost/abc");
        this.wsClient.setTaskExecutor(new SimpleAsyncTaskExecutor());
        WebSocketSession session = this.wsClient.doHandshake(this.wsHandler, this.headers, uri).get();
        Assert.assertNotNull(session);
    }
}

