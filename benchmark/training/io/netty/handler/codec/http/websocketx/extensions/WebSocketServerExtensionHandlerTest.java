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
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class WebSocketServerExtensionHandlerTest {
    WebSocketServerExtensionHandshaker mainHandshakerMock = Mockito.mock(WebSocketServerExtensionHandshaker.class, "mainHandshaker");

    WebSocketServerExtensionHandshaker fallbackHandshakerMock = Mockito.mock(WebSocketServerExtensionHandshaker.class, "fallbackHandshaker");

    WebSocketServerExtension mainExtensionMock = Mockito.mock(WebSocketServerExtension.class, "mainExtension");

    WebSocketServerExtension fallbackExtensionMock = Mockito.mock(WebSocketServerExtension.class, "fallbackExtension");

    @Test
    public void testMainSuccess() {
        // initialize
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(mainExtensionMock);
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(fallbackExtensionMock);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(null);
        Mockito.when(mainExtensionMock.rsv()).thenReturn(RSV1);
        Mockito.when(mainExtensionMock.newReponseData()).thenReturn(new WebSocketExtensionData("main", Collections.<String, String>emptyMap()));
        Mockito.when(mainExtensionMock.newExtensionEncoder()).thenReturn(new WebSocketExtensionTestUtil.DummyEncoder());
        Mockito.when(mainExtensionMock.newExtensionDecoder()).thenReturn(new WebSocketExtensionTestUtil.DummyDecoder());
        Mockito.when(fallbackExtensionMock.rsv()).thenReturn(RSV1);
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketServerExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest("main, fallback");
        ch.writeInbound(req);
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse(null);
        ch.writeOutbound(res);
        HttpResponse res2 = ch.readOutbound();
        List<WebSocketExtensionData> resExts = WebSocketExtensionUtil.extractExtensions(res2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        // test
        Assert.assertEquals(1, resExts.size());
        Assert.assertEquals("main", resExts.get(0).name());
        Assert.assertTrue(resExts.get(0).parameters().isEmpty());
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyDecoder.class));
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyEncoder.class));
        Mockito.verify(mainHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"));
        Mockito.verify(mainHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(fallbackHandshakerMock, Mockito.atLeastOnce()).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(mainExtensionMock, Mockito.atLeastOnce()).rsv();
        Mockito.verify(mainExtensionMock).newReponseData();
        Mockito.verify(mainExtensionMock).newExtensionEncoder();
        Mockito.verify(mainExtensionMock).newExtensionDecoder();
        Mockito.verify(fallbackExtensionMock, Mockito.atLeastOnce()).rsv();
    }

    @Test
    public void testCompatibleExtensionTogetherSuccess() {
        // initialize
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(mainExtensionMock);
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"))).thenReturn(fallbackExtensionMock);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"))).thenReturn(null);
        Mockito.when(mainExtensionMock.rsv()).thenReturn(RSV1);
        Mockito.when(mainExtensionMock.newReponseData()).thenReturn(new WebSocketExtensionData("main", Collections.<String, String>emptyMap()));
        Mockito.when(mainExtensionMock.newExtensionEncoder()).thenReturn(new WebSocketExtensionTestUtil.DummyEncoder());
        Mockito.when(mainExtensionMock.newExtensionDecoder()).thenReturn(new WebSocketExtensionTestUtil.DummyDecoder());
        Mockito.when(fallbackExtensionMock.rsv()).thenReturn(RSV2);
        Mockito.when(fallbackExtensionMock.newReponseData()).thenReturn(new WebSocketExtensionData("fallback", Collections.<String, String>emptyMap()));
        Mockito.when(fallbackExtensionMock.newExtensionEncoder()).thenReturn(new WebSocketExtensionTestUtil.Dummy2Encoder());
        Mockito.when(fallbackExtensionMock.newExtensionDecoder()).thenReturn(new WebSocketExtensionTestUtil.Dummy2Decoder());
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketServerExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest("main, fallback");
        ch.writeInbound(req);
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse(null);
        ch.writeOutbound(res);
        HttpResponse res2 = ch.readOutbound();
        List<WebSocketExtensionData> resExts = WebSocketExtensionUtil.extractExtensions(res2.headers().get(SEC_WEBSOCKET_EXTENSIONS));
        // test
        Assert.assertEquals(2, resExts.size());
        Assert.assertEquals("main", resExts.get(0).name());
        Assert.assertEquals("fallback", resExts.get(1).name());
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyDecoder.class));
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.DummyEncoder.class));
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.Dummy2Decoder.class));
        Assert.assertNotNull(ch.pipeline().get(WebSocketExtensionTestUtil.Dummy2Encoder.class));
        Mockito.verify(mainHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("main"));
        Mockito.verify(mainHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(fallbackHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("fallback"));
        Mockito.verify(mainExtensionMock, Mockito.times(2)).rsv();
        Mockito.verify(mainExtensionMock).newReponseData();
        Mockito.verify(mainExtensionMock).newExtensionEncoder();
        Mockito.verify(mainExtensionMock).newExtensionDecoder();
        Mockito.verify(fallbackExtensionMock, Mockito.times(2)).rsv();
        Mockito.verify(fallbackExtensionMock).newReponseData();
        Mockito.verify(fallbackExtensionMock).newExtensionEncoder();
        Mockito.verify(fallbackExtensionMock).newExtensionDecoder();
    }

    @Test
    public void testNoneExtensionMatchingSuccess() {
        // initialize
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown"))).thenReturn(null);
        Mockito.when(mainHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown2"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown"))).thenReturn(null);
        Mockito.when(fallbackHandshakerMock.handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown2"))).thenReturn(null);
        // execute
        EmbeddedChannel ch = new EmbeddedChannel(new WebSocketServerExtensionHandler(mainHandshakerMock, fallbackHandshakerMock));
        HttpRequest req = WebSocketExtensionTestUtil.newUpgradeRequest("unknown, unknown2");
        ch.writeInbound(req);
        HttpResponse res = WebSocketExtensionTestUtil.newUpgradeResponse(null);
        ch.writeOutbound(res);
        HttpResponse res2 = ch.readOutbound();
        // test
        Assert.assertFalse(res2.headers().contains(SEC_WEBSOCKET_EXTENSIONS));
        Mockito.verify(mainHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown"));
        Mockito.verify(mainHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown2"));
        Mockito.verify(fallbackHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown"));
        Mockito.verify(fallbackHandshakerMock).handshakeExtension(WebSocketExtensionTestUtil.webSocketExtensionDataMatcher("unknown2"));
    }
}

