/**
 * Copyright 2014 The Netty Project
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
package io.netty.handler.codec.http.websocketx.extensions;


import HttpHeaderNames.SEC_WEBSOCKET_EXTENSIONS;
import WebSocketExtension.RSV1;
import WebSocketExtension.RSV2;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class WebSocketClientExtensionHandlerTest {
    WebSocketClientExtensionHandshaker mainHandshakerMock = Mockito.mock(WebSocketClientExtensionHandshaker.class, "mainHandshaker");

    WebSocketClientExtensionHandshaker fallbackHandshakerMock = Mockito.mock(WebSocketClientExtensionHandshaker.class, "fallbackHandshaker");

    WebSocketClientExtension mainExtensionMock = Mockito.mock(WebSocketClientExtension.class, "mainExtension");

    WebSocketClientExtension fallbackExtensionMock = Mockito.mock(WebSocketClientExtension.class, "fallbackExtension");

    @Test
    public void testMainSuccess() {
        // initialize
        Mockito.when(mainHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("main", Collections.<String, String>emptyMap()));
        Mockito.when(mainHandshakerMock.handshakeExtension(ArgumentMatchers.any(WebSocketExtensionData.class))).thenReturn(mainExtensionMock);
        Mockito.when(fallbackHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("fallback", Collections.<String, String>emptyMap()));
        Mockito.when(mainExtensionMock.rsv()).thenReturn(RSV1);
        Mockito.when(mainExtensionMock.newExtensionEncoder()).thenReturn(new WebSocketExtensionTestUtil.DummyEncoder());
        Mockito.when(mainExtensionMock.newExtensionDecoder()).thenReturn(new WebSocketExtensionTestUtil.DummyDecoder());
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketClientExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest(null);
        ch.writeOutbound(req);
        HttpRequest req2 = ch.readOutbound();
        List<WebSocketExtensionData> reqExts = WebSocketExtensionUtil.extractExtensions(req2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse("main");
        ch.writeInbound(res);
        HttpResponse res2 = ch.readInbound();
        List<WebSocketExtensionData> resExts = WebSocketExtensionUtil.extractExtensions(res2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        // test
        Assert.assertEquals(2, reqExts.size());
        Assert.assertEquals("main", reqExts.get(0).name());
        Assert.assertEquals("fallback", reqExts.get(1).name());
        Assert.assertEquals(1, resExts.size());
        Assert.assertEquals("main", resExts.get(0).name());
        Assert.assertTrue(resExts.get(0).parameters().isEmpty());
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyDecoder.class));
        Assert.assertNotNull(((ch.pipeline().get(WebSocketExtensionTestUtil.DummyEncoder.class)) != null));
        Mockito.verify(mainHandshakerMock).newRequestData();
        Mockito.verify(mainHandshakerMock).handshakeExtension(ArgumentMatchers.any(WebSocketExtensionData.class));
        Mockito.verify(fallbackHandshakerMock).newRequestData();
        Mockito.verify(mainExtensionMock, Mockito.atLeastOnce()).rsv();
        Mockito.verify(mainExtensionMock).newExtensionEncoder();
        Mockito.verify(mainExtensionMock).newExtensionDecoder();
    }

    @Test
    public void testFallbackSuccess() {
        // initialize
        Mockito.when(mainHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("main", Collections.<String, String>emptyMap()));
        Mockito.when(mainHandshakerMock.handshakeExtension(ArgumentMatchers.any(WebSocketExtensionData.class))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("fallback", Collections.<String, String>emptyMap()));
        Mockito.when(fallbackHandshakerMock.handshakeExtension(ArgumentMatchers.any(WebSocketExtensionData.class))).thenReturn(fallbackExtensionMock);
        Mockito.when(fallbackExtensionMock.rsv()).thenReturn(RSV1);
        Mockito.when(fallbackExtensionMock.newExtensionEncoder()).thenReturn(new WebSocketExtensionTestUtil.DummyEncoder());
        Mockito.when(fallbackExtensionMock.newExtensionDecoder()).thenReturn(new WebSocketExtensionTestUtil.DummyDecoder());
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketClientExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest(null);
        ch.writeOutbound(req);
        HttpRequest req2 = ch.readOutbound();
        List<WebSocketExtensionData> reqExts = WebSocketExtensionUtil.extractExtensions(req2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse("fallback");
        ch.writeInbound(res);
        HttpResponse res2 = ch.readInbound();
        List<WebSocketExtensionData> resExts = WebSocketExtensionUtil.extractExtensions(res2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        // test
        Assert.assertEquals(2, reqExts.size());
        Assert.assertEquals("main", reqExts.get(0).name());
        Assert.assertEquals("fallback", reqExts.get(1).name());
        Assert.assertEquals(1, resExts.size());
        Assert.assertEquals("fallback", resExts.get(0).name());
        Assert.assertTrue(resExts.get(0).parameters().isEmpty());
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyDecoder.class));
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyEncoder.class));
        Mockito.verify(mainHandshakerMock).newRequestData();
        Mockito.verify(mainHandshakerMock).handshakeExtension(ArgumentMatchers.any(WebSocketExtensionData.class));
        Mockito.verify(fallbackHandshakerMock).newRequestData();
        Mockito.verify(fallbackHandshakerMock).handshakeExtension(ArgumentMatchers.any(WebSocketExtensionData.class));
        Mockito.verify(fallbackExtensionMock, Mockito.atLeastOnce()).rsv();
        Mockito.verify(fallbackExtensionMock).newExtensionEncoder();
        Mockito.verify(fallbackExtensionMock).newExtensionDecoder();
    }

    @Test
    public void testAllSuccess() {
        // initialize
        Mockito.when(mainHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("main", Collections.<String, String>emptyMap()));
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(mainExtensionMock);
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("fallback", Collections.<String, String>emptyMap()));
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(fallbackExtensionMock);
        WebSocketExtensionTestUtil.DummyEncoder mainEncoder = new WebSocketExtensionTestUtil.DummyEncoder();
        WebSocketExtensionTestUtil.DummyDecoder mainDecoder = new WebSocketExtensionTestUtil.DummyDecoder();
        Mockito.when(mainExtensionMock.rsv()).thenReturn(RSV1);
        Mockito.when(mainExtensionMock.newExtensionEncoder()).thenReturn(mainEncoder);
        Mockito.when(mainExtensionMock.newExtensionDecoder()).thenReturn(mainDecoder);
        WebSocketExtensionTestUtil.Dummy2Encoder fallbackEncoder = new WebSocketExtensionTestUtil.Dummy2Encoder();
        WebSocketExtensionTestUtil.Dummy2Decoder fallbackDecoder = new WebSocketExtensionTestUtil.Dummy2Decoder();
        Mockito.when(fallbackExtensionMock.rsv()).thenReturn(RSV2);
        Mockito.when(fallbackExtensionMock.newExtensionEncoder()).thenReturn(fallbackEncoder);
        Mockito.when(fallbackExtensionMock.newExtensionDecoder()).thenReturn(fallbackDecoder);
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketClientExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest(null);
        ch.writeOutbound(req);
        HttpRequest req2 = ch.readOutbound();
        List<WebSocketExtensionData> reqExts = WebSocketExtensionUtil.extractExtensions(req2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse("main, fallback");
        ch.writeInbound(res);
        HttpResponse res2 = ch.readInbound();
        List<WebSocketExtensionData> resExts = WebSocketExtensionUtil.extractExtensions(res2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        // test
        Assert.assertEquals(2, reqExts.size());
        Assert.assertEquals("main", reqExts.get(0).name());
        Assert.assertEquals("fallback", reqExts.get(1).name());
        Assert.assertEquals(2, resExts.size());
        Assert.assertEquals("main", resExts.get(0).name());
        Assert.assertEquals("fallback", resExts.get(1).name());
        Assert.assertNotNull(ch.pipeline().context(mainEncoder));
        Assert.assertNotNull(ch.pipeline().context(mainDecoder));
        Assert.assertNotNull(ch.pipeline().context(fallbackEncoder));
        Assert.assertNotNull(ch.pipeline().context(fallbackDecoder));
        Mockito.verify(mainHandshakerMock).newRequestData();
        Mockito.verify(mainHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"));
        Mockito.verify(mainHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(fallbackHandshakerMock).newRequestData();
        Mockito.verify(fallbackHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(mainExtensionMock, Mockito.atLeastOnce()).rsv();
        Mockito.verify(mainExtensionMock).newExtensionEncoder();
        Mockito.verify(mainExtensionMock).newExtensionDecoder();
        Mockito.verify(fallbackExtensionMock, Mockito.atLeastOnce()).rsv();
        Mockito.verify(fallbackExtensionMock).newExtensionEncoder();
        Mockito.verify(fallbackExtensionMock).newExtensionDecoder();
    }

    @Test(expected = CodecException.class)
    public void testIfMainAndFallbackUseRSV1WillFail() {
        // initialize
        Mockito.when(mainHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("main", Collections.<String, String>emptyMap()));
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(mainExtensionMock);
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.newRequestData()).thenReturn(new WebSocketExtensionData("fallback", Collections.<String, String>emptyMap()));
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(fallbackExtensionMock);
        Mockito.when(mainExtensionMock.rsv()).thenReturn(RSV1);
        Mockito.when(fallbackExtensionMock.rsv()).thenReturn(RSV1);
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketClientExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest(null);
        ch.writeOutbound(req);
        HttpRequest req2 = ch.readOutbound();
        List<WebSocketExtensionData> reqExts = WebSocketExtensionUtil.extractExtensions(req2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse("main, fallback");
        ch.writeInbound(res);
        // test
        Assert.assertEquals(2, reqExts.size());
        Assert.assertEquals("main", reqExts.get(0).name());
        Assert.assertEquals("fallback", reqExts.get(1).name());
        Mockito.verify(mainHandshakerMock).newRequestData();
        Mockito.verify(mainHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"));
        Mockito.verify(mainHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(fallbackHandshakerMock).newRequestData();
        Mockito.verify(fallbackHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"));
        Mockito.verify(fallbackHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(mainExtensionMock, Mockito.atLeastOnce()).rsv();
        Mockito.verify(fallbackExtensionMock, Mockito.atLeastOnce()).rsv();
    }
}

