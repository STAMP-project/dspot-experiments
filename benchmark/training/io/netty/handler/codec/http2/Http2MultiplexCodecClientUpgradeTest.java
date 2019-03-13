/**
 * Copyright 2018 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package io.netty.handler.codec.http2;


import ChannelHandler.Sharable;
import Http2Stream.State;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;


public class Http2MultiplexCodecClientUpgradeTest {
    @ChannelHandler.Sharable
    private final class NoopHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.channel().close();
        }
    }

    private final class UpgradeHandler extends ChannelInboundHandlerAdapter {
        State stateOnActive;

        int streamId;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Http2StreamChannel ch = ((Http2StreamChannel) (ctx.channel()));
            stateOnActive = ch.stream().state();
            streamId = ch.stream().id();
            super.channelActive(ctx);
        }
    }

    @Test
    public void upgradeHandlerGetsActivated() throws Exception {
        Http2MultiplexCodecClientUpgradeTest.UpgradeHandler upgradeHandler = new Http2MultiplexCodecClientUpgradeTest.UpgradeHandler();
        Http2MultiplexCodec codec = newCodec(upgradeHandler);
        EmbeddedChannel ch = new EmbeddedChannel(codec);
        codec.onHttpClientUpgrade();
        Assert.assertFalse(upgradeHandler.stateOnActive.localSideOpen());
        Assert.assertTrue(upgradeHandler.stateOnActive.remoteSideOpen());
        Assert.assertEquals(1, upgradeHandler.streamId);
        Assert.assertTrue(ch.finishAndReleaseAll());
    }

    @Test(expected = Http2Exception.class)
    public void clientUpgradeWithoutUpgradeHandlerThrowsHttp2Exception() throws Http2Exception {
        Http2MultiplexCodec codec = Http2MultiplexCodecBuilder.forClient(new Http2MultiplexCodecClientUpgradeTest.NoopHandler()).build();
        EmbeddedChannel ch = new EmbeddedChannel(codec);
        try {
            codec.onHttpClientUpgrade();
        } finally {
            Assert.assertTrue(ch.finishAndReleaseAll());
        }
    }
}

