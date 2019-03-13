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


public class Http2ClientUpgradeCodecTest {
    @Test
    public void testUpgradeToHttp2ConnectionHandler() throws Exception {
        Http2ClientUpgradeCodecTest.testUpgrade(new Http2ConnectionHandlerBuilder().server(false).frameListener(new Http2FrameAdapter()).build());
    }

    @Test
    public void testUpgradeToHttp2FrameCodec() throws Exception {
        Http2ClientUpgradeCodecTest.testUpgrade(Http2FrameCodecBuilder.forClient().build());
    }

    @Test
    public void testUpgradeToHttp2MultiplexCodec() throws Exception {
        Http2ClientUpgradeCodecTest.testUpgrade(Http2MultiplexCodecBuilder.forClient(new Http2ClientUpgradeCodecTest.HttpInboundHandler()).withUpgradeStreamHandler(new ChannelInboundHandlerAdapter()).build());
    }

    @ChannelHandler.Sharable
    private static final class HttpInboundHandler extends ChannelInboundHandlerAdapter {}
}

