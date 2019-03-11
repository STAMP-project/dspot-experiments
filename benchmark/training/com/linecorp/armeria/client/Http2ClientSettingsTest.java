/**
 * Copyright 2017 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client;


import Http2CodecUtil.DEFAULT_WINDOW_SIZE;
import Http2Error.FRAME_SIZE_ERROR;
import com.linecorp.armeria.common.AggregatedHttpMessage;
import com.linecorp.armeria.testing.common.EventLoopRule;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http2.DefaultHttp2FrameReader;
import io.netty.handler.codec.http2.Http2CodecUtil;
import io.netty.handler.codec.http2.Http2EventAdapter;
import io.netty.handler.codec.http2.Http2Exception;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;


public class Http2ClientSettingsTest {
    private static final byte[] EMPTY_DATA = new byte[Http2CodecUtil.DEFAULT_MAX_FRAME_SIZE];

    @ClassRule
    public static final EventLoopRule eventLoop = new EventLoopRule();

    @Rule
    public TestRule globalTimeout = new DisableOnDebug(new Timeout(10, TimeUnit.SECONDS));

    @Test
    public void initialConnectionAndStreamWindowSize() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);ClientFactory clientFactory = // Client sends a WINDOW_UPDATE frame for stream when it receives 48 * 1024 bytes.
        // Client sends a WINDOW_UPDATE frame for connection when it receives 64 * 1024 bytes.
        new ClientFactoryBuilder().useHttp2Preface(true).http2InitialConnectionWindowSize((128 * 1024)).http2InitialStreamWindowSize((96 * 1024)).build()) {
            final int port = ss.getLocalPort();
            final HttpClient client = HttpClient.of(clientFactory, ("h2c://127.0.0.1:" + port));
            final CompletableFuture<AggregatedHttpMessage> future = client.get("/").aggregate();
            try (Socket s = ss.accept()) {
                final InputStream in = s.getInputStream();
                final BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
                // Read the connection preface and discard it.
                Http2ClientSettingsTest.readBytes(in, Http2CodecUtil.connectionPrefaceBuf().readableBytes());
                // Read a SETTINGS frame and validate it.
                Http2ClientSettingsTest.assertSettingsFrameOfWindowSize(in);
                Http2ClientSettingsTest.sendEmptySettingsAndAckFrame(bos);
                // Read a WINDOW_UPDATE frame and validate it.
                Http2ClientSettingsTest.assertInitialWindowUpdateFrame(in);
                Http2ClientSettingsTest.readBytes(in, 9);// Read a SETTINGS_ACK frame and discard it.

                Http2ClientSettingsTest.readHeadersFrame(in);// Read a HEADERS frame and discard it.

                Http2ClientSettingsTest.sendHeaderFrame(bos);
                // //////////////////////////////////////
                // Transmission of data gets started. //
                // //////////////////////////////////////
                Http2ClientSettingsTest.send49151Bytes(bos);// 49151 == (96 * 1024 / 2 - 1) half of initial stream window size.

                int availableBytes = Http2ClientSettingsTest.checkReadableForShortPeriod(in);
                assertThat(availableBytes).isZero();// Nothing to read.

                // Send a DATA frame that indicates sending data 1 byte for stream id 03.
                bos.write(new byte[]{ 0, 0, 1, 0, 0, 0, 0, 0, 3 });
                bos.write(new byte[1]);// Triggers the client to send a WINDOW_UPDATE frame for stream id 03.

                bos.flush();
                // Read a WINDOW_UPDATE frame and validate it.
                Http2ClientSettingsTest.assertWindowUpdateFrameFor03(in);
                // Send a DATA frame that indicates sending data as much as (0x4000 - 1) for stream id 03.
                bos.write(new byte[]{ 0, 63, ((byte) (255)), 0, 0, 0, 0, 0, 3 });
                bos.write(Http2ClientSettingsTest.EMPTY_DATA, 0, 16383);
                availableBytes = Http2ClientSettingsTest.checkReadableForShortPeriod(in);
                assertThat(availableBytes).isZero();// Nothing to read.

                // Send a DATA frame that indicates sending data 1 byte for stream id 03.
                bos.write(new byte[]{ 0, 0, 1, 0, 0, 0, 0, 0, 3 });
                bos.write(new byte[1]);// Triggers the client to send a WINDOW_UPDATE frame for the connection.

                bos.flush();
                // Read an WINDOW_UPDATE frame and validate it.
                Http2ClientSettingsTest.assertConnectionWindowUpdateFrame(in);
                // Send a DATA frame that indicates the end of stream id 03.
                bos.write(new byte[]{ 0, 0, 0, 0, 1, 0, 0, 0, 3 });
                bos.flush();
                future.join();
            }
        }
    }

    @Test
    public void maxFrameSize() throws Exception {
        try (ServerSocket ss = new ServerSocket(0);ClientFactory clientFactory = // == 16384 * 2
        // Set the window size to the HTTP/2 default values to simplify the traffic.
        new ClientFactoryBuilder().useHttp2Preface(true).http2InitialConnectionWindowSize(DEFAULT_WINDOW_SIZE).http2InitialStreamWindowSize(DEFAULT_WINDOW_SIZE).http2MaxFrameSize(((Http2CodecUtil.DEFAULT_MAX_FRAME_SIZE) * 2)).build()) {
            final int port = ss.getLocalPort();
            final HttpClient client = HttpClient.of(clientFactory, ("http://127.0.0.1:" + port));
            client.get("/").aggregate();
            try (Socket s = ss.accept()) {
                final InputStream in = s.getInputStream();
                final BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());
                Http2ClientSettingsTest.readBytes(in, Http2CodecUtil.connectionPrefaceBuf().capacity());// Read the connection preface and discard it.

                // Read a SETTINGS frame and validate it.
                Http2ClientSettingsTest.assertSettingsFrameOfMaxFrameSize(in);
                Http2ClientSettingsTest.sendEmptySettingsAndAckFrame(bos);
                Http2ClientSettingsTest.readBytes(in, 9);// Read a SETTINGS_ACK frame and discard it.

                Http2ClientSettingsTest.readHeadersFrame(in);// Read a HEADERS frame and discard it.

                Http2ClientSettingsTest.sendHeaderFrame(bos);
                // //////////////////////////////////////
                // Transmission of data gets started. //
                // //////////////////////////////////////
                // Send a DATA frame that indicates sending data as much as 0x8000 for stream id 03.
                bos.write(new byte[]{ 0, ((byte) (128)), 0, 0, 0, 0, 0, 0, 3 });
                bos.write(Http2ClientSettingsTest.EMPTY_DATA);
                bos.write(Http2ClientSettingsTest.EMPTY_DATA);
                bos.flush();
                Http2ClientSettingsTest.readBytes(in, 13);// Read a WINDOW_UPDATE frame for connection and discard it.

                Http2ClientSettingsTest.readBytes(in, 13);// Read a WINDOW_UPDATE frame for stream id 03 and discard it.

                // Send a DATA frame that exceed MAX_FRAME_SIZE by 1.
                bos.write(new byte[]{ 0, ((byte) (128)), 1, 0, 0, 0, 0, 0, 3 });
                bos.flush();// Triggers the client to send a GOAWAY frame for the connection.

                // The client send a GOAWAY frame and the server read it.
                final ByteBuf buffer = Http2ClientSettingsTest.readGoAwayFrame(in);
                final DefaultHttp2FrameReader frameReader = new DefaultHttp2FrameReader();
                final CountDownLatch latch = new CountDownLatch(1);
                frameReader.readFrame(null, buffer, new Http2EventAdapter() {
                    @Override
                    public void onGoAwayRead(ChannelHandlerContext ctx, int lastStreamId, long errorCode, ByteBuf debugData) throws Http2Exception {
                        assertThat(lastStreamId).isZero();// 0: connection error

                        assertThat(errorCode).isEqualTo(FRAME_SIZE_ERROR.code());
                        latch.countDown();
                    }
                });
                latch.await();
                buffer.release();
                // Client should disconnect after receiving a GOAWAY frame.
                assertThat(in.read()).isEqualTo((-1));
            }
        }
    }
}

