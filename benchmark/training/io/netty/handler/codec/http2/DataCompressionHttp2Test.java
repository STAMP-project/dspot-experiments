/**
 * Copyright 2014 The Netty Project
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


import CharsetUtil.UTF_8;
import HttpHeaderNames.CONTENT_ENCODING;
import HttpHeaderValues.DEFLATE;
import HttpHeaderValues.GZIP;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AsciiString;
import java.io.ByteArrayOutputStream;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Test for data decompression in the HTTP/2 codec.
 */
public class DataCompressionHttp2Test {
    private static final AsciiString GET = new AsciiString("GET");

    private static final AsciiString POST = new AsciiString("POST");

    private static final AsciiString PATH = new AsciiString("/some/path");

    @Mock
    private Http2FrameListener serverListener;

    @Mock
    private Http2FrameListener clientListener;

    private Http2ConnectionEncoder clientEncoder;

    private ServerBootstrap sb;

    private Bootstrap cb;

    private Channel serverChannel;

    private Channel clientChannel;

    private volatile Channel serverConnectedChannel;

    private CountDownLatch serverLatch;

    private Http2Connection serverConnection;

    private Http2Connection clientConnection;

    private Http2ConnectionHandler clientHandler;

    private ByteArrayOutputStream serverOut;

    @Test
    public void justHeadersNoData() throws Exception {
        bootstrapEnv(0);
        final Http2Headers headers = new DefaultHttp2Headers().method(DataCompressionHttp2Test.GET).path(DataCompressionHttp2Test.PATH).set(CONTENT_ENCODING, GZIP);
        Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
            @Override
            public void run() throws Http2Exception {
                clientEncoder.writeHeaders(ctxClient(), 3, headers, 0, true, newPromiseClient());
                clientHandler.flush(ctxClient());
            }
        });
        awaitServer();
        Mockito.verify(serverListener).onHeadersRead(ArgumentMatchers.any(ChannelHandlerContext.class), ArgumentMatchers.eq(3), ArgumentMatchers.eq(headers), ArgumentMatchers.eq(0), ArgumentMatchers.eq(Http2CodecUtil.DEFAULT_PRIORITY_WEIGHT), ArgumentMatchers.eq(false), ArgumentMatchers.eq(0), ArgumentMatchers.eq(true));
    }

    @Test
    public void gzipEncodingSingleEmptyMessage() throws Exception {
        final String text = "";
        final ByteBuf data = Unpooled.copiedBuffer(text.getBytes());
        bootstrapEnv(data.readableBytes());
        try {
            final Http2Headers headers = new DefaultHttp2Headers().method(DataCompressionHttp2Test.POST).path(DataCompressionHttp2Test.PATH).set(CONTENT_ENCODING, GZIP);
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientEncoder.writeHeaders(ctxClient(), 3, headers, 0, false, newPromiseClient());
                    clientEncoder.writeData(ctxClient(), 3, data.retain(), 0, true, newPromiseClient());
                    clientHandler.flush(ctxClient());
                }
            });
            awaitServer();
            Assert.assertEquals(text, serverOut.toString(UTF_8.name()));
        } finally {
            data.release();
        }
    }

    @Test
    public void gzipEncodingSingleMessage() throws Exception {
        final String text = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbccccccccccccccccccccccc";
        final ByteBuf data = Unpooled.copiedBuffer(text.getBytes());
        bootstrapEnv(data.readableBytes());
        try {
            final Http2Headers headers = new DefaultHttp2Headers().method(DataCompressionHttp2Test.POST).path(DataCompressionHttp2Test.PATH).set(CONTENT_ENCODING, GZIP);
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientEncoder.writeHeaders(ctxClient(), 3, headers, 0, false, newPromiseClient());
                    clientEncoder.writeData(ctxClient(), 3, data.retain(), 0, true, newPromiseClient());
                    clientHandler.flush(ctxClient());
                }
            });
            awaitServer();
            Assert.assertEquals(text, serverOut.toString(UTF_8.name()));
        } finally {
            data.release();
        }
    }

    @Test
    public void gzipEncodingMultipleMessages() throws Exception {
        final String text1 = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbbbbbbbbccccccccccccccccccccccc";
        final String text2 = "dddddddddddddddddddeeeeeeeeeeeeeeeeeeeffffffffffffffffffff";
        final ByteBuf data1 = Unpooled.copiedBuffer(text1.getBytes());
        final ByteBuf data2 = Unpooled.copiedBuffer(text2.getBytes());
        bootstrapEnv(((data1.readableBytes()) + (data2.readableBytes())));
        try {
            final Http2Headers headers = new DefaultHttp2Headers().method(DataCompressionHttp2Test.POST).path(DataCompressionHttp2Test.PATH).set(CONTENT_ENCODING, GZIP);
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientEncoder.writeHeaders(ctxClient(), 3, headers, 0, false, newPromiseClient());
                    clientEncoder.writeData(ctxClient(), 3, data1.retain(), 0, false, newPromiseClient());
                    clientEncoder.writeData(ctxClient(), 3, data2.retain(), 0, true, newPromiseClient());
                    clientHandler.flush(ctxClient());
                }
            });
            awaitServer();
            Assert.assertEquals((text1 + text2), serverOut.toString(UTF_8.name()));
        } finally {
            data1.release();
            data2.release();
        }
    }

    @Test
    public void deflateEncodingWriteLargeMessage() throws Exception {
        final int BUFFER_SIZE = 1 << 12;
        final byte[] bytes = new byte[BUFFER_SIZE];
        new Random().nextBytes(bytes);
        bootstrapEnv(BUFFER_SIZE);
        final ByteBuf data = Unpooled.wrappedBuffer(bytes);
        try {
            final Http2Headers headers = new DefaultHttp2Headers().method(DataCompressionHttp2Test.POST).path(DataCompressionHttp2Test.PATH).set(CONTENT_ENCODING, DEFLATE);
            Http2TestUtil.runInChannel(clientChannel, new Http2TestUtil.Http2Runnable() {
                @Override
                public void run() throws Http2Exception {
                    clientEncoder.writeHeaders(ctxClient(), 3, headers, 0, false, newPromiseClient());
                    clientEncoder.writeData(ctxClient(), 3, data.retain(), 0, true, newPromiseClient());
                    clientHandler.flush(ctxClient());
                }
            });
            awaitServer();
            Assert.assertEquals(data.resetReaderIndex().toString(UTF_8), serverOut.toString(UTF_8.name()));
        } finally {
            data.release();
        }
    }
}

