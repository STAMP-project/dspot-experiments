/**
 * Copyright 2016 The Netty Project
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
package io.netty.handler.codec.http;


import HttpClientUpgradeHandler.SourceCodec;
import HttpClientUpgradeHandler.UpgradeCodec;
import HttpClientUpgradeHandler.UpgradeEvent.UPGRADE_ISSUED;
import HttpClientUpgradeHandler.UpgradeEvent.UPGRADE_REJECTED;
import HttpClientUpgradeHandler.UpgradeEvent.UPGRADE_SUCCESSFUL;
import HttpHeaderNames.UPGRADE;
import HttpResponseStatus.OK;
import LastHttpContent.EMPTY_LAST_CONTENT;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

import static HttpMethod.GET;
import static HttpResponseStatus.OK;
import static HttpResponseStatus.SWITCHING_PROTOCOLS;
import static HttpVersion.HTTP_1_1;


public class HttpClientUpgradeHandlerTest {
    private static final class FakeSourceCodec implements HttpClientUpgradeHandler.SourceCodec {
        @Override
        public void prepareUpgradeFrom(ChannelHandlerContext ctx) {
        }

        @Override
        public void upgradeFrom(ChannelHandlerContext ctx) {
        }
    }

    private static final class FakeUpgradeCodec implements HttpClientUpgradeHandler.UpgradeCodec {
        @Override
        public CharSequence protocol() {
            return "fancyhttp";
        }

        @Override
        public Collection<CharSequence> setUpgradeHeaders(ChannelHandlerContext ctx, HttpRequest upgradeRequest) {
            return Collections.emptyList();
        }

        @Override
        public void upgradeTo(ChannelHandlerContext ctx, FullHttpResponse upgradeResponse) throws Exception {
        }
    }

    private static final class UserEventCatcher extends ChannelInboundHandlerAdapter {
        private Object evt;

        public Object getUserEvent() {
            return evt;
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
            this.evt = evt;
        }
    }

    @Test
    public void testSuccessfulUpgrade() {
        HttpClientUpgradeHandler.SourceCodec sourceCodec = new HttpClientUpgradeHandlerTest.FakeSourceCodec();
        HttpClientUpgradeHandler.UpgradeCodec upgradeCodec = new HttpClientUpgradeHandlerTest.FakeUpgradeCodec();
        HttpClientUpgradeHandler handler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 1024);
        HttpClientUpgradeHandlerTest.UserEventCatcher catcher = new HttpClientUpgradeHandlerTest.UserEventCatcher();
        EmbeddedChannel channel = new EmbeddedChannel(catcher);
        channel.pipeline().addFirst("upgrade", handler);
        Assert.assertTrue(channel.writeOutbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "netty.io")));
        FullHttpRequest request = channel.readOutbound();
        Assert.assertEquals(2, request.headers().size());
        Assert.assertTrue(request.headers().contains(UPGRADE, "fancyhttp", false));
        Assert.assertTrue(request.headers().contains("connection", "upgrade", false));
        Assert.assertTrue(request.release());
        Assert.assertEquals(UPGRADE_ISSUED, catcher.getUserEvent());
        HttpResponse upgradeResponse = new DefaultHttpResponse(HTTP_1_1, SWITCHING_PROTOCOLS);
        upgradeResponse.headers().add(UPGRADE, "fancyhttp");
        Assert.assertFalse(channel.writeInbound(upgradeResponse));
        Assert.assertFalse(channel.writeInbound(EMPTY_LAST_CONTENT));
        Assert.assertEquals(UPGRADE_SUCCESSFUL, catcher.getUserEvent());
        Assert.assertNull(channel.pipeline().get("upgrade"));
        Assert.assertTrue(channel.writeInbound(new DefaultFullHttpResponse(HTTP_1_1, OK)));
        FullHttpResponse response = channel.readInbound();
        Assert.assertEquals(OK, response.status());
        Assert.assertTrue(response.release());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testUpgradeRejected() {
        HttpClientUpgradeHandler.SourceCodec sourceCodec = new HttpClientUpgradeHandlerTest.FakeSourceCodec();
        HttpClientUpgradeHandler.UpgradeCodec upgradeCodec = new HttpClientUpgradeHandlerTest.FakeUpgradeCodec();
        HttpClientUpgradeHandler handler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 1024);
        HttpClientUpgradeHandlerTest.UserEventCatcher catcher = new HttpClientUpgradeHandlerTest.UserEventCatcher();
        EmbeddedChannel channel = new EmbeddedChannel(catcher);
        channel.pipeline().addFirst("upgrade", handler);
        Assert.assertTrue(channel.writeOutbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "netty.io")));
        FullHttpRequest request = channel.readOutbound();
        Assert.assertEquals(2, request.headers().size());
        Assert.assertTrue(request.headers().contains(UPGRADE, "fancyhttp", false));
        Assert.assertTrue(request.headers().contains("connection", "upgrade", false));
        Assert.assertTrue(request.release());
        Assert.assertEquals(UPGRADE_ISSUED, catcher.getUserEvent());
        HttpResponse upgradeResponse = new DefaultHttpResponse(HTTP_1_1, SWITCHING_PROTOCOLS);
        upgradeResponse.headers().add(UPGRADE, "fancyhttp");
        Assert.assertTrue(channel.writeInbound(new DefaultHttpResponse(HTTP_1_1, OK)));
        Assert.assertTrue(channel.writeInbound(EMPTY_LAST_CONTENT));
        Assert.assertEquals(UPGRADE_REJECTED, catcher.getUserEvent());
        Assert.assertNull(channel.pipeline().get("upgrade"));
        HttpResponse response = channel.readInbound();
        Assert.assertEquals(OK, response.status());
        LastHttpContent last = channel.readInbound();
        Assert.assertEquals(EMPTY_LAST_CONTENT, last);
        Assert.assertFalse(last.release());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void testEarlyBailout() {
        HttpClientUpgradeHandler.SourceCodec sourceCodec = new HttpClientUpgradeHandlerTest.FakeSourceCodec();
        HttpClientUpgradeHandler.UpgradeCodec upgradeCodec = new HttpClientUpgradeHandlerTest.FakeUpgradeCodec();
        HttpClientUpgradeHandler handler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 1024);
        HttpClientUpgradeHandlerTest.UserEventCatcher catcher = new HttpClientUpgradeHandlerTest.UserEventCatcher();
        EmbeddedChannel channel = new EmbeddedChannel(catcher);
        channel.pipeline().addFirst("upgrade", handler);
        Assert.assertTrue(channel.writeOutbound(new DefaultFullHttpRequest(HTTP_1_1, GET, "netty.io")));
        FullHttpRequest request = channel.readOutbound();
        Assert.assertEquals(2, request.headers().size());
        Assert.assertTrue(request.headers().contains(UPGRADE, "fancyhttp", false));
        Assert.assertTrue(request.headers().contains("connection", "upgrade", false));
        Assert.assertTrue(request.release());
        Assert.assertEquals(UPGRADE_ISSUED, catcher.getUserEvent());
        HttpResponse upgradeResponse = new DefaultHttpResponse(HTTP_1_1, SWITCHING_PROTOCOLS);
        upgradeResponse.headers().add(UPGRADE, "fancyhttp");
        Assert.assertTrue(channel.writeInbound(new DefaultHttpResponse(HTTP_1_1, OK)));
        Assert.assertEquals(UPGRADE_REJECTED, catcher.getUserEvent());
        Assert.assertNull(channel.pipeline().get("upgrade"));
        HttpResponse response = channel.readInbound();
        Assert.assertEquals(OK, response.status());
        Assert.assertFalse(channel.finish());
    }

    @Test
    public void dontStripConnectionHeaders() {
        HttpClientUpgradeHandler.SourceCodec sourceCodec = new HttpClientUpgradeHandlerTest.FakeSourceCodec();
        HttpClientUpgradeHandler.UpgradeCodec upgradeCodec = new HttpClientUpgradeHandlerTest.FakeUpgradeCodec();
        HttpClientUpgradeHandler handler = new HttpClientUpgradeHandler(sourceCodec, upgradeCodec, 1024);
        HttpClientUpgradeHandlerTest.UserEventCatcher catcher = new HttpClientUpgradeHandlerTest.UserEventCatcher();
        EmbeddedChannel channel = new EmbeddedChannel(catcher);
        channel.pipeline().addFirst("upgrade", handler);
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HTTP_1_1, GET, "netty.io");
        request.headers().add("connection", "extra");
        request.headers().add("extra", "value");
        Assert.assertTrue(channel.writeOutbound(request));
        FullHttpRequest readRequest = channel.readOutbound();
        List<String> connectionHeaders = readRequest.headers().getAll("connection");
        Assert.assertTrue(connectionHeaders.contains("extra"));
        Assert.assertTrue(readRequest.release());
        Assert.assertFalse(channel.finish());
    }
}

