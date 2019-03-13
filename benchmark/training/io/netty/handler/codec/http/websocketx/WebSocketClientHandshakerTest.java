/**
 * Copyright 2015 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.handler.codec.http.websocketx;


import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import java.net.URI;
import org.junit.Assert;
import org.junit.Test;


public abstract class WebSocketClientHandshakerTest {
    @Test
    public void hostHeaderWs() {
        for (String scheme : new String[]{ "ws://", "http://" }) {
            for (String host : new String[]{ "localhost", "127.0.0.1", "[::1]", "Netty.io" }) {
                String enter = scheme + host;
                testHostHeader(enter, host);
                testHostHeader((enter + '/'), host);
                testHostHeader((enter + ":80"), host);
                testHostHeader((enter + ":443"), (host + ":443"));
                testHostHeader((enter + ":9999"), (host + ":9999"));
                testHostHeader((enter + "/path"), host);
                testHostHeader((enter + ":80/path"), host);
                testHostHeader((enter + ":443/path"), (host + ":443"));
                testHostHeader((enter + ":9999/path"), (host + ":9999"));
            }
        }
    }

    @Test
    public void hostHeaderWss() {
        for (String scheme : new String[]{ "wss://", "https://" }) {
            for (String host : new String[]{ "localhost", "127.0.0.1", "[::1]", "Netty.io" }) {
                String enter = scheme + host;
                testHostHeader(enter, host);
                testHostHeader((enter + '/'), host);
                testHostHeader((enter + ":80"), (host + ":80"));
                testHostHeader((enter + ":443"), host);
                testHostHeader((enter + ":9999"), (host + ":9999"));
                testHostHeader((enter + "/path"), host);
                testHostHeader((enter + ":80/path"), (host + ":80"));
                testHostHeader((enter + ":443/path"), host);
                testHostHeader((enter + ":9999/path"), (host + ":9999"));
            }
        }
    }

    @Test
    public void hostHeaderWithoutScheme() {
        testHostHeader("//localhost/", "localhost");
        testHostHeader("//localhost/path", "localhost");
        testHostHeader("//localhost:80/", "localhost:80");
        testHostHeader("//localhost:443/", "localhost:443");
        testHostHeader("//localhost:9999/", "localhost:9999");
    }

    @Test
    public void originHeaderWs() {
        for (String scheme : new String[]{ "ws://", "http://" }) {
            for (String host : new String[]{ "localhost", "127.0.0.1", "[::1]", "NETTY.IO" }) {
                String enter = scheme + host;
                String expect = "http://" + (host.toLowerCase());
                testOriginHeader(enter, expect);
                testOriginHeader((enter + '/'), expect);
                testOriginHeader((enter + ":80"), expect);
                testOriginHeader((enter + ":443"), (expect + ":443"));
                testOriginHeader((enter + ":9999"), (expect + ":9999"));
                testOriginHeader((enter + "/path%20with%20ws"), expect);
                testOriginHeader((enter + ":80/path%20with%20ws"), expect);
                testOriginHeader((enter + ":443/path%20with%20ws"), (expect + ":443"));
                testOriginHeader((enter + ":9999/path%20with%20ws"), (expect + ":9999"));
            }
        }
    }

    @Test
    public void originHeaderWss() {
        for (String scheme : new String[]{ "wss://", "https://" }) {
            for (String host : new String[]{ "localhost", "127.0.0.1", "[::1]", "NETTY.IO" }) {
                String enter = scheme + host;
                String expect = "https://" + (host.toLowerCase());
                testOriginHeader(enter, expect);
                testOriginHeader((enter + '/'), expect);
                testOriginHeader((enter + ":80"), (expect + ":80"));
                testOriginHeader((enter + ":443"), expect);
                testOriginHeader((enter + ":9999"), (expect + ":9999"));
                testOriginHeader((enter + "/path%20with%20ws"), expect);
                testOriginHeader((enter + ":80/path%20with%20ws"), (expect + ":80"));
                testOriginHeader((enter + ":443/path%20with%20ws"), expect);
                testOriginHeader((enter + ":9999/path%20with%20ws"), (expect + ":9999"));
            }
        }
    }

    @Test
    public void originHeaderWithoutScheme() {
        testOriginHeader("//localhost/", "http://localhost");
        testOriginHeader("//localhost/path", "http://localhost");
        // http scheme by port
        testOriginHeader("//localhost:80/", "http://localhost");
        testOriginHeader("//localhost:80/path", "http://localhost");
        // https scheme by port
        testOriginHeader("//localhost:443/", "https://localhost");
        testOriginHeader("//localhost:443/path", "https://localhost");
        // http scheme for non standard port
        testOriginHeader("//localhost:9999/", "http://localhost:9999");
        testOriginHeader("//localhost:9999/path", "http://localhost:9999");
        // convert host to lower case
        testOriginHeader("//LOCALHOST/", "http://localhost");
    }

    @Test
    @SuppressWarnings("deprecation")
    public void testRawPath() {
        URI uri = URI.create("ws://localhost:9999/path%20with%20ws");
        WebSocketClientHandshaker handshaker = newHandshaker(uri);
        FullHttpRequest request = handshaker.newHandshakeRequest();
        try {
            Assert.assertEquals("/path%20with%20ws", request.getUri());
        } finally {
            request.release();
        }
    }

    @Test
    public void testRawPathWithQuery() {
        URI uri = URI.create("ws://localhost:9999/path%20with%20ws?a=b%20c");
        WebSocketClientHandshaker handshaker = newHandshaker(uri);
        FullHttpRequest request = handshaker.newHandshakeRequest();
        try {
            Assert.assertEquals("/path%20with%20ws?a=b%20c", request.uri());
        } finally {
            request.release();
        }
    }

    @Test(timeout = 3000)
    public void testHttpResponseAndFrameInSameBuffer() {
        testHttpResponseAndFrameInSameBuffer(false);
    }

    @Test(timeout = 3000)
    public void testHttpResponseAndFrameInSameBufferCodec() {
        testHttpResponseAndFrameInSameBuffer(true);
    }

    @Test
    public void testDuplicateWebsocketHandshakeHeaders() {
        URI uri = URI.create("ws://localhost:9999/foo");
        HttpHeaders inputHeaders = new DefaultHttpHeaders();
        String bogusSubProtocol = "bogusSubProtocol";
        String bogusHeaderValue = "bogusHeaderValue";
        // add values for the headers that are reserved for use in the websockets handshake
        for (CharSequence header : getHandshakeHeaderNames()) {
            inputHeaders.add(header, bogusHeaderValue);
        }
        inputHeaders.add(getProtocolHeaderName(), bogusSubProtocol);
        String realSubProtocol = "realSubProtocol";
        WebSocketClientHandshaker handshaker = newHandshaker(uri, realSubProtocol, inputHeaders);
        FullHttpRequest request = handshaker.newHandshakeRequest();
        HttpHeaders outputHeaders = request.headers();
        // the header values passed in originally have been replaced with values generated by the Handshaker
        for (CharSequence header : getHandshakeHeaderNames()) {
            Assert.assertEquals(1, outputHeaders.getAll(header).size());
            Assert.assertNotEquals(bogusHeaderValue, outputHeaders.get(header));
        }
        // the subprotocol header value is that of the subprotocol string passed into the Handshaker
        Assert.assertEquals(1, outputHeaders.getAll(getProtocolHeaderName()).size());
        Assert.assertEquals(realSubProtocol, outputHeaders.get(getProtocolHeaderName()));
        request.release();
    }
}

