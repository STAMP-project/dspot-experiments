/**
 * Copyright 2002-2014 the original author or authors.
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


import TransportType.WEBSOCKET;
import TransportType.XHR_STREAMING;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@code SockJsUrlInfo}.
 *
 * @author Rossen Stoyanchev
 */
public class SockJsUrlInfoTests {
    @Test
    public void serverId() throws Exception {
        SockJsUrlInfo info = new SockJsUrlInfo(new URI("http://example.com"));
        int serverId = Integer.valueOf(info.getServerId());
        Assert.assertTrue(("Invalid serverId: " + serverId), ((serverId >= 0) && (serverId < 1000)));
    }

    @Test
    public void sessionId() throws Exception {
        SockJsUrlInfo info = new SockJsUrlInfo(new URI("http://example.com"));
        Assert.assertEquals(("Invalid sessionId: " + (info.getSessionId())), 32, info.getSessionId().length());
    }

    @Test
    public void infoUrl() throws Exception {
        testInfoUrl("http", "http");
        testInfoUrl("http", "http");
        testInfoUrl("https", "https");
        testInfoUrl("https", "https");
        testInfoUrl("ws", "http");
        testInfoUrl("ws", "http");
        testInfoUrl("wss", "https");
        testInfoUrl("wss", "https");
    }

    @Test
    public void transportUrl() throws Exception {
        testTransportUrl("http", "http", XHR_STREAMING);
        testTransportUrl("http", "ws", WEBSOCKET);
        testTransportUrl("https", "https", XHR_STREAMING);
        testTransportUrl("https", "wss", WEBSOCKET);
        testTransportUrl("ws", "http", XHR_STREAMING);
        testTransportUrl("ws", "ws", WEBSOCKET);
        testTransportUrl("wss", "https", XHR_STREAMING);
        testTransportUrl("wss", "wss", WEBSOCKET);
    }
}

