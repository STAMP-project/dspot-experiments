/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.http2.server;


import ByteBufferPool.Lease;
import ErrorCode.FRAME_SIZE_ERROR.code;
import FrameType.CONTINUATION;
import MetaData.Request;
import MetaData.Response;
import PriorityFrame.PRIORITY_LENGTH;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.UnaryOperator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.MetaData;
import org.eclipse.jetty.http2.Flags;
import org.eclipse.jetty.http2.frames.DataFrame;
import org.eclipse.jetty.http2.frames.GoAwayFrame;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.http2.frames.PingFrame;
import org.eclipse.jetty.http2.frames.PrefaceFrame;
import org.eclipse.jetty.http2.frames.PriorityFrame;
import org.eclipse.jetty.http2.frames.SettingsFrame;
import org.eclipse.jetty.http2.parser.Parser;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.ChannelEndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HTTP2ServerTest extends AbstractServerTest {
    @Test
    public void testNoPrefaceBytes() throws Exception {
        startServer(new HttpServlet() {});
        // No preface bytes.
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
        generator.control(lease, new HeadersFrame(1, metaData, null, true));
        try (Socket client = new Socket("localhost", connector.getLocalPort())) {
            OutputStream output = client.getOutputStream();
            for (ByteBuffer buffer : lease.getByteBuffers()) {
                output.write(BufferUtil.toArray(buffer));
            }
            final CountDownLatch latch = new CountDownLatch(1);
            Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter() {
                @Override
                public void onGoAway(GoAwayFrame frame) {
                    latch.countDown();
                }
            }, 4096, 8192);
            parser.init(UnaryOperator.identity());
            parseResponse(client, parser);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testRequestResponseNoContent() throws Exception {
        final CountDownLatch latch = new CountDownLatch(3);
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                latch.countDown();
            }
        });
        ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
        generator.control(lease, new PrefaceFrame());
        generator.control(lease, new SettingsFrame(new HashMap(), false));
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        generator.control(lease, new HeadersFrame(1, metaData, null, true));
        try (Socket client = new Socket("localhost", connector.getLocalPort())) {
            OutputStream output = client.getOutputStream();
            for (ByteBuffer buffer : lease.getByteBuffers()) {
                output.write(BufferUtil.toArray(buffer));
            }
            final AtomicReference<HeadersFrame> frameRef = new AtomicReference<>();
            Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter() {
                @Override
                public void onSettings(SettingsFrame frame) {
                    latch.countDown();
                }

                @Override
                public void onHeaders(HeadersFrame frame) {
                    frameRef.set(frame);
                    latch.countDown();
                }
            }, 4096, 8192);
            parser.init(UnaryOperator.identity());
            parseResponse(client, parser);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
            HeadersFrame response = frameRef.get();
            Assertions.assertNotNull(response);
            MetaData.Response responseMetaData = ((MetaData.Response) (response.getMetaData()));
            Assertions.assertEquals(200, responseMetaData.getStatus());
        }
    }

    @Test
    public void testRequestResponseContent() throws Exception {
        final byte[] content = "Hello, world!".getBytes(StandardCharsets.UTF_8);
        final CountDownLatch latch = new CountDownLatch(4);
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
                latch.countDown();
                resp.getOutputStream().write(content);
            }
        });
        ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
        generator.control(lease, new PrefaceFrame());
        generator.control(lease, new SettingsFrame(new HashMap(), false));
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        generator.control(lease, new HeadersFrame(1, metaData, null, true));
        try (Socket client = new Socket("localhost", connector.getLocalPort())) {
            OutputStream output = client.getOutputStream();
            for (ByteBuffer buffer : lease.getByteBuffers()) {
                output.write(BufferUtil.toArray(buffer));
            }
            final AtomicReference<HeadersFrame> headersRef = new AtomicReference<>();
            final AtomicReference<DataFrame> dataRef = new AtomicReference<>();
            Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter() {
                @Override
                public void onSettings(SettingsFrame frame) {
                    latch.countDown();
                }

                @Override
                public void onHeaders(HeadersFrame frame) {
                    headersRef.set(frame);
                    latch.countDown();
                }

                @Override
                public void onData(DataFrame frame) {
                    dataRef.set(frame);
                    latch.countDown();
                }
            }, 4096, 8192);
            parser.init(UnaryOperator.identity());
            parseResponse(client, parser);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
            HeadersFrame response = headersRef.get();
            Assertions.assertNotNull(response);
            MetaData.Response responseMetaData = ((MetaData.Response) (response.getMetaData()));
            Assertions.assertEquals(200, responseMetaData.getStatus());
            DataFrame responseData = dataRef.get();
            Assertions.assertNotNull(responseData);
            Assertions.assertArrayEquals(content, BufferUtil.toArray(responseData.getData()));
        }
    }

    @Test
    public void testBadPingWrongPayload() throws Exception {
        startServer(new HttpServlet() {});
        ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
        generator.control(lease, new PrefaceFrame());
        generator.control(lease, new SettingsFrame(new HashMap(), false));
        generator.control(lease, new PingFrame(new byte[8], false));
        // Modify the length of the frame to a wrong one.
        lease.getByteBuffers().get(2).putShort(0, ((short) (7)));
        final CountDownLatch latch = new CountDownLatch(1);
        try (Socket client = new Socket("localhost", connector.getLocalPort())) {
            OutputStream output = client.getOutputStream();
            for (ByteBuffer buffer : lease.getByteBuffers()) {
                output.write(BufferUtil.toArray(buffer));
            }
            Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter() {
                @Override
                public void onGoAway(GoAwayFrame frame) {
                    Assertions.assertEquals(code, frame.getError());
                    latch.countDown();
                }
            }, 4096, 8192);
            parser.init(UnaryOperator.identity());
            parseResponse(client, parser);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testBadPingWrongStreamId() throws Exception {
        startServer(new HttpServlet() {});
        ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
        generator.control(lease, new PrefaceFrame());
        generator.control(lease, new SettingsFrame(new HashMap(), false));
        generator.control(lease, new PingFrame(new byte[8], false));
        // Modify the streamId of the frame to non zero.
        lease.getByteBuffers().get(2).putInt(4, 1);
        final CountDownLatch latch = new CountDownLatch(1);
        try (Socket client = new Socket("localhost", connector.getLocalPort())) {
            OutputStream output = client.getOutputStream();
            for (ByteBuffer buffer : lease.getByteBuffers()) {
                output.write(BufferUtil.toArray(buffer));
            }
            Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter() {
                @Override
                public void onGoAway(GoAwayFrame frame) {
                    Assertions.assertEquals(ErrorCode.PROTOCOL_ERROR.code, frame.getError());
                    latch.countDown();
                }
            }, 4096, 8192);
            parser.init(UnaryOperator.identity());
            parseResponse(client, parser);
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testCommitFailure() throws Exception {
        final long delay = 1000;
        final AtomicBoolean broken = new AtomicBoolean();
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                try {
                    // Wait for the SETTINGS frames to be exchanged.
                    Thread.sleep(delay);
                    broken.set(true);
                } catch (InterruptedException x) {
                    throw new InterruptedIOException();
                }
            }
        });
        server.stop();
        ServerConnector connector2 = new ServerConnector(server, new HTTP2ServerConnectionFactory(new HttpConfiguration())) {
            @Override
            protected ChannelEndPoint newEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key) throws IOException {
                return new SocketChannelEndPoint(channel, selectSet, key, getScheduler()) {
                    @Override
                    public void write(Callback callback, ByteBuffer... buffers) throws IllegalStateException {
                        if (broken.get())
                            callback.failed(new IOException("explicitly_thrown_by_test"));
                        else
                            super.write(callback, buffers);

                    }
                };
            }
        };
        server.addConnector(connector2);
        server.start();
        ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
        generator.control(lease, new PrefaceFrame());
        generator.control(lease, new SettingsFrame(new HashMap(), false));
        MetaData.Request metaData = newRequest("GET", new HttpFields());
        generator.control(lease, new HeadersFrame(1, metaData, null, true));
        try (Socket client = new Socket("localhost", connector2.getLocalPort())) {
            OutputStream output = client.getOutputStream();
            for (ByteBuffer buffer : lease.getByteBuffers())
                output.write(BufferUtil.toArray(buffer));

            // The server will close the connection abruptly since it
            // cannot write and therefore cannot even send the GO_AWAY.
            Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter(), 4096, 8192);
            parser.init(UnaryOperator.identity());
            boolean closed = parseResponse(client, parser, (2 * delay));
            Assertions.assertTrue(closed);
        }
    }

    @Test
    public void testNonISOHeader() throws Exception {
        try (StacklessLogging stackless = new StacklessLogging(HttpChannel.class)) {
            startServer(new HttpServlet() {
                @Override
                protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                    // Invalid header name, the connection must be closed.
                    response.setHeader("Euro_(\u20ac)", "42");
                }
            });
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, null, true));
            try (Socket client = new Socket("localhost", connector.getLocalPort())) {
                OutputStream output = client.getOutputStream();
                for (ByteBuffer buffer : lease.getByteBuffers())
                    output.write(BufferUtil.toArray(buffer));

                output.flush();
                Parser parser = new Parser(byteBufferPool, new Parser.Listener.Adapter(), 4096, 8192);
                parser.init(UnaryOperator.identity());
                boolean closed = parseResponse(client, parser);
                Assertions.assertTrue(closed);
            }
        }
    }

    @Test
    public void testRequestWithContinuationFrames() throws Exception {
        testRequestWithContinuationFrames(null, () -> {
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, null, true));
            return lease;
        });
    }

    @Test
    public void testRequestWithPriorityWithContinuationFrames() throws Exception {
        PriorityFrame priority = new PriorityFrame(1, 13, 200, true);
        testRequestWithContinuationFrames(priority, () -> {
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap<>(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, priority, true));
            return lease;
        });
    }

    @Test
    public void testRequestWithContinuationFramesWithEmptyHeadersFrame() throws Exception {
        testRequestWithContinuationFrames(null, () -> {
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, null, true));
            // Take the HeadersFrame header and set the length to zero.
            List<ByteBuffer> buffers = lease.getByteBuffers();
            ByteBuffer headersFrameHeader = buffers.get(2);
            headersFrameHeader.put(0, ((byte) (0)));
            headersFrameHeader.putShort(1, ((short) (0)));
            // Insert a CONTINUATION frame header for the body of the HEADERS frame.
            lease.insert(3, buffers.get(4).slice(), false);
            return lease;
        });
    }

    @Test
    public void testRequestWithPriorityWithContinuationFramesWithEmptyHeadersFrame() throws Exception {
        PriorityFrame priority = new PriorityFrame(1, 13, 200, true);
        testRequestWithContinuationFrames(null, () -> {
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, priority, true));
            // Take the HeadersFrame header and set the length to just the priority frame.
            List<ByteBuffer> buffers = lease.getByteBuffers();
            ByteBuffer headersFrameHeader = buffers.get(2);
            headersFrameHeader.put(0, ((byte) (0)));
            headersFrameHeader.putShort(1, ((short) (PRIORITY_LENGTH)));
            // Insert a CONTINUATION frame header for the body of the HEADERS frame.
            lease.insert(3, buffers.get(4).slice(), false);
            return lease;
        });
    }

    @Test
    public void testRequestWithContinuationFramesWithEmptyContinuationFrame() throws Exception {
        testRequestWithContinuationFrames(null, () -> {
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, null, true));
            // Take the ContinuationFrame header, duplicate it, and set the length to zero.
            List<ByteBuffer> buffers = lease.getByteBuffers();
            ByteBuffer continuationFrameHeader = buffers.get(4);
            ByteBuffer duplicate = ByteBuffer.allocate(continuationFrameHeader.remaining());
            duplicate.put(continuationFrameHeader).flip();
            continuationFrameHeader.flip();
            continuationFrameHeader.put(0, ((byte) (0)));
            continuationFrameHeader.putShort(1, ((short) (0)));
            // Insert a CONTINUATION frame header for the body of the previous CONTINUATION frame.
            lease.insert(5, duplicate, false);
            return lease;
        });
    }

    @Test
    public void testRequestWithContinuationFramesWithEmptyLastContinuationFrame() throws Exception {
        testRequestWithContinuationFrames(null, () -> {
            ByteBufferPool.Lease lease = new ByteBufferPool.Lease(byteBufferPool);
            generator.control(lease, new PrefaceFrame());
            generator.control(lease, new SettingsFrame(new HashMap(), false));
            MetaData.Request metaData = newRequest("GET", new HttpFields());
            generator.control(lease, new HeadersFrame(1, metaData, null, true));
            // Take the last CONTINUATION frame and reset the flag.
            List<ByteBuffer> buffers = lease.getByteBuffers();
            ByteBuffer continuationFrameHeader = buffers.get(((buffers.size()) - 2));
            continuationFrameHeader.put(4, ((byte) (0)));
            // Add a last, empty, CONTINUATION frame.
            ByteBuffer last = ByteBuffer.wrap(new byte[]{ 0, 0, 0// Length
            , ((byte) (CONTINUATION.getType())), ((byte) (Flags.END_HEADERS)), 0, 0, 0, 1// Stream ID
             });
            lease.append(last, false);
            return lease;
        });
    }
}

