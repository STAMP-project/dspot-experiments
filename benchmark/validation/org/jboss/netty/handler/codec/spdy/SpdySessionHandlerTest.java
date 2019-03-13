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
package org.jboss.netty.handler.codec.spdy;


import SpdySettingsFrame.SETTINGS_MAX_CONCURRENT_STREAMS;
import java.util.Map;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.junit.Test;


public class SpdySessionHandlerTest {
    private static final int closeSignal = SPDY_SETTINGS_MAX_ID;

    private static final SpdySettingsFrame closeMessage = new DefaultSpdySettingsFrame();

    static {
        SpdySessionHandlerTest.closeMessage.setValue(SpdySessionHandlerTest.closeSignal, 0);
    }

    @Test
    public void testSpdyClientSessionHandler() {
        for (int version = SPDY_MIN_VERSION; version <= (SPDY_MAX_VERSION); version++) {
            SpdySessionHandlerTest.testSpdySessionHandler(version, false);
        }
    }

    @Test
    public void testSpdyServerSessionHandler() {
        for (int version = SPDY_MIN_VERSION; version <= (SPDY_MAX_VERSION); version++) {
            SpdySessionHandlerTest.testSpdySessionHandler(version, true);
        }
    }

    // Echo Handler opens 4 half-closed streams on session connection
    // and then sets the number of concurrent streams to 3
    private static class EchoHandler extends SimpleChannelUpstreamHandler {
        private final int closeSignal;

        private final boolean server;

        EchoHandler(int closeSignal, boolean server) {
            this.closeSignal = closeSignal;
            this.server = server;
        }

        @Override
        public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
            // Initiate 4 new streams
            int streamId = (server) ? 2 : 1;
            SpdySynStreamFrame spdySynStreamFrame = new DefaultSpdySynStreamFrame(streamId, 0, ((byte) (0)));
            spdySynStreamFrame.setLast(true);
            Channels.write(e.getChannel(), spdySynStreamFrame);
            spdySynStreamFrame.setStreamId(((spdySynStreamFrame.getStreamId()) + 2));
            Channels.write(e.getChannel(), spdySynStreamFrame);
            spdySynStreamFrame.setStreamId(((spdySynStreamFrame.getStreamId()) + 2));
            Channels.write(e.getChannel(), spdySynStreamFrame);
            spdySynStreamFrame.setStreamId(((spdySynStreamFrame.getStreamId()) + 2));
            Channels.write(e.getChannel(), spdySynStreamFrame);
            // Limit the number of concurrent streams to 3
            SpdySettingsFrame spdySettingsFrame = new DefaultSpdySettingsFrame();
            spdySettingsFrame.setValue(SETTINGS_MAX_CONCURRENT_STREAMS, 3);
            Channels.write(e.getChannel(), spdySettingsFrame);
        }

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            Object msg = e.getMessage();
            if (msg instanceof SpdySynStreamFrame) {
                SpdySynStreamFrame spdySynStreamFrame = ((SpdySynStreamFrame) (msg));
                int streamId = spdySynStreamFrame.getStreamId();
                SpdySynReplyFrame spdySynReplyFrame = new DefaultSpdySynReplyFrame(streamId);
                spdySynReplyFrame.setLast(spdySynStreamFrame.isLast());
                for (Map.Entry<String, String> entry : spdySynStreamFrame.getHeaders()) {
                    spdySynReplyFrame.addHeader(entry.getKey(), entry.getValue());
                }
                Channels.write(e.getChannel(), spdySynReplyFrame, e.getRemoteAddress());
                return;
            }
            if (msg instanceof SpdySynReplyFrame) {
                return;
            }
            if (((msg instanceof SpdyDataFrame) || (msg instanceof SpdyPingFrame)) || (msg instanceof SpdyHeadersFrame)) {
                Channels.write(e.getChannel(), msg, e.getRemoteAddress());
                return;
            }
            if (msg instanceof SpdySettingsFrame) {
                SpdySettingsFrame spdySettingsFrame = ((SpdySettingsFrame) (msg));
                if (spdySettingsFrame.isSet(closeSignal)) {
                    Channels.close(e.getChannel());
                }
            }
        }
    }
}

