/**
 * Copyright 2017 The Netty Project
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
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.junit.Test;


public class Http2ServerUpgradeCodecTest {
    @Test
    public void testUpgradeToHttp2ConnectionHandler() {
        Http2ServerUpgradeCodecTest.testUpgrade(new Http2ConnectionHandlerBuilder().frameListener(new Http2FrameAdapter()).build());
    }

    @Test
    public void testUpgradeToHttp2FrameCodec() {
        Http2ServerUpgradeCodecTest.testUpgrade(new Http2FrameCodecBuilder(true).build());
    }

    @Test
    public void testUpgradeToHttp2MultiplexCodec() {
        Http2ServerUpgradeCodecTest.testUpgrade(new Http2MultiplexCodecBuilder(true, new Http2ServerUpgradeCodecTest.HttpInboundHandler()).build());
    }

    @ChannelHandler.Sharable
    private static final class HttpInboundHandler extends ChannelInboundHandlerAdapter {}
}

