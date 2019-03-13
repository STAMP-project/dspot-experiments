/**
 * Copyright 2013 The Netty Project
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
package io.netty.handler.codec.spdy;


import SpdySettingsFrame.SETTINGS_MAX_CONCURRENT_STREAMS;
import SpdyVersion.SPDY_3_1;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Map;
import org.junit.Test;

import static SpdyCodecUtil.SPDY_SETTINGS_MAX_ID;


public class SpdySessionHandlerTest {
    private static final InternalLogger logger = InternalLoggerFactory.getInstance(SpdySessionHandlerTest.class);

    private static final int closeSignal = SPDY_SETTINGS_MAX_ID;

    private static final SpdySettingsFrame closeMessage = new DefaultSpdySettingsFrame();

    static {
        SpdySessionHandlerTest.closeMessage.setValue(SpdySessionHandlerTest.closeSignal, 0);
    }

    @Test
    public void testSpdyClientSessionHandler() {
        SpdySessionHandlerTest.logger.info("Running: testSpdyClientSessionHandler v3.1");
        SpdySessionHandlerTest.testSpdySessionHandler(SPDY_3_1, false);
    }

    @Test
    public void testSpdyClientSessionHandlerPing() {
        SpdySessionHandlerTest.logger.info("Running: testSpdyClientSessionHandlerPing v3.1");
        SpdySessionHandlerTest.testSpdySessionHandlerPing(SPDY_3_1, false);
    }

    @Test
    public void testSpdyClientSessionHandlerGoAway() {
        SpdySessionHandlerTest.logger.info("Running: testSpdyClientSessionHandlerGoAway v3.1");
        SpdySessionHandlerTest.testSpdySessionHandlerGoAway(SPDY_3_1, false);
    }

    @Test
    public void testSpdyServerSessionHandler() {
        SpdySessionHandlerTest.logger.info("Running: testSpdyServerSessionHandler v3.1");
        SpdySessionHandlerTest.testSpdySessionHandler(SPDY_3_1, true);
    }

    @Test
    public void testSpdyServerSessionHandlerPing() {
        SpdySessionHandlerTest.logger.info("Running: testSpdyServerSessionHandlerPing v3.1");
        SpdySessionHandlerTest.testSpdySessionHandlerPing(SPDY_3_1, true);
    }

    @Test
    public void testSpdyServerSessionHandlerGoAway() {
        SpdySessionHandlerTest.logger.info("Running: testSpdyServerSessionHandlerGoAway v3.1");
        SpdySessionHandlerTest.testSpdySessionHandlerGoAway(SPDY_3_1, true);
    }

    // Echo Handler opens 4 half-closed streams on session connection
    // and then sets the number of concurrent streams to 1
    private static class EchoHandler extends ChannelInboundHandlerAdapter {
        private final int closeSignal;

        private final boolean server;

        EchoHandler(int closeSignal, boolean server) {
            this.closeSignal = closeSignal;
            this.server = server;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            // Initiate 4 new streams
            int streamId = (server) ? 2 : 1;
            SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, 0, ((byte) (0)));
            spdySynStreamFrame.setLast(true);
            ctx.writeAndFlush(spdySynStreamFrame);
            spdySynStreamFrame.setStreamId(((spdySynStreamFrame.streamId()) + 2));
            ctx.writeAndFlush(spdySynStreamFrame);
            spdySynStreamFrame.setStreamId(((spdySynStreamFrame.streamId()) + 2));
            ctx.writeAndFlush(spdySynStreamFrame);
            spdySynStreamFrame.setStreamId(((spdySynStreamFrame.streamId()) + 2));
            ctx.writeAndFlush(spdySynStreamFrame);
            // Limit the number of concurrent streams to 1
            SpdySettingsFrame spdySettingsFrame = new DefaultSpdySettingsFrame();
            spdySettingsFrame.setValue(SETTINGS_MAX_CONCURRENT_STREAMS, 1);
            ctx.writeAndFlush(spdySettingsFrame);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            if (msg instanceof SpdySynStreamFrame) {
                SpdySynStreamFrame spdySynStreamFrame = ((SpdySynStreamFrame) (msg));
                if (!(spdySynStreamFrame.isUnidirectional())) {
                    int streamId = spdySynStreamFrame.streamId();
                    SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                    spdySynReplyFrame.setLast(spdySynStreamFrame.isLast());
                    for (Map.Entry<CharSequence, CharSequence> entry : spdySynStreamFrame.headers()) {
                        spdySynReplyFrame.headers().add(entry.getKey(), entry.getValue());
                    }
                    ctx.writeAndFlush(spdySynReplyFrame);
                }
                return;
            }
            if (msg instanceof SpdySynReplyFrame) {
                return;
            }
            if (((msg instanceof SpdyDataFrame) || (msg instanceof SpdyPingFrame)) || (msg instanceof SpdyHeadersFrame)) {
                ctx.writeAndFlush(msg);
                return;
            }
            if (msg instanceof SpdySettingsFrame) {
                SpdySettingsFrame spdySettingsFrame = ((SpdySettingsFrame) (msg));
                if (spdySettingsFrame.isSet(closeSignal)) {
                    ctx.close();
                }
            }
        }
    }
}

