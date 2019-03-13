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


import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.util.TestUtil;
import org.junit.Assert;
import org.junit.Test;


public class SpdyFrameDecoderTest {
    @Test
    public void testTooLargeHeaderNameOnSynStreamRequest() throws Exception {
        for (int version = SPDY_MIN_VERSION; version <= (SPDY_MAX_VERSION); version++) {
            List<Integer> headerSizes = Arrays.asList(90, 900);
            for (int maxHeaderSize : headerSizes) {
                // 90 catches the header name, 900 the value
                SpdyHeadersFrame frame = new DefaultSpdySynStreamFrame(1, 0, ((byte) (0)));
                SpdyFrameDecoderTest.addHeader(frame, 100, 1000);
                SpdyFrameDecoderTest.CaptureHandler captureHandler = new SpdyFrameDecoderTest.CaptureHandler();
                ServerBootstrap sb = new ServerBootstrap(newServerSocketChannelFactory(Executors.newCachedThreadPool()));
                ClientBootstrap cb = new ClientBootstrap(newClientSocketChannelFactory(Executors.newCachedThreadPool()));
                sb.getPipeline().addLast("decoder", new SpdyFrameDecoder(version, 10000, maxHeaderSize));
                sb.getPipeline().addLast("sessionHandler", new SpdySessionHandler(version, true));
                sb.getPipeline().addLast("handler", captureHandler);
                cb.getPipeline().addLast("encoder", new SpdyFrameEncoder(version));
                Channel sc = sb.bind(new InetSocketAddress(0));
                int port = ((InetSocketAddress) (sc.getLocalAddress())).getPort();
                ChannelFuture ccf = cb.connect(new InetSocketAddress(TestUtil.getLocalHost(), port));
                Assert.assertTrue(ccf.awaitUninterruptibly().isSuccess());
                Channel cc = ccf.getChannel();
                SpdyFrameDecoderTest.sendAndWaitForFrame(cc, frame, captureHandler);
                Assert.assertNotNull((("version " + version) + ", not null message"), captureHandler.message);
                String message = (("version " + version) + ", should be SpdyHeadersFrame, was ") + (captureHandler.message.getClass());
                Assert.assertTrue(message, ((captureHandler.message) instanceof SpdyHeadersFrame));
                SpdyHeadersFrame writtenFrame = ((SpdyHeadersFrame) (captureHandler.message));
                Assert.assertTrue("should be truncated", writtenFrame.isTruncated());
                Assert.assertFalse("should not be invalid", writtenFrame.isInvalid());
                sc.close().awaitUninterruptibly();
                cb.shutdown();
                sb.shutdown();
                cb.releaseExternalResources();
                sb.releaseExternalResources();
            }
        }
    }

    private static class CaptureHandler extends SimpleChannelUpstreamHandler {
        public volatile Object message;

        @Override
        public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
            message = e.getMessage();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
            e.getCause().printStackTrace();
            message = e.getCause();
        }
    }
}

