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
package org.eclipse.jetty.websocket.server;


import HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL;
import OpCode.CLOSE;
import StatusCode.BAD_PAYLOAD;
import StatusCode.SERVER_ERROR;
import Timeouts.CONNECT;
import Timeouts.CONNECT_UNIT;
import Timeouts.POLL_EVENT;
import Timeouts.POLL_EVENT_UNIT;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.util.Utf8Appendable.NotUtf8Exception;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.api.extensions.Frame;
import org.eclipse.jetty.websocket.common.CloseInfo;
import org.eclipse.jetty.websocket.common.Generator;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.events.EventDriver;
import org.eclipse.jetty.websocket.common.frames.BinaryFrame;
import org.eclipse.jetty.websocket.common.frames.ContinuationFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.BlockheadClient;
import org.eclipse.jetty.websocket.common.test.BlockheadClientRequest;
import org.eclipse.jetty.websocket.common.test.BlockheadConnection;
import org.eclipse.jetty.websocket.common.test.UnitGenerator;
import org.eclipse.jetty.websocket.common.util.Hex;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Test various <a href="http://tools.ietf.org/html/rfc6455">RFC 6455</a> specified requirements placed on {@link WebSocketServlet}
 */
public class WebSocketServletRFCTest {
    private static final String REQUEST_HASH_KEY = "dGhlIHNhbXBsZSBub25jZQ==";

    private static Generator generator = new UnitGenerator();

    private static SimpleServletServer server;

    private static BlockheadClient client;

    /**
     * Test that aggregation of binary frames into a single message occurs
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testBinaryAggregate() throws Exception {
        BlockheadClientRequest request = WebSocketServletRFCTest.client.newWsRequest(WebSocketServletRFCTest.server.getServerUri());
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Generate binary frames
            byte[] buf1 = new byte[128];
            byte[] buf2 = new byte[128];
            byte[] buf3 = new byte[128];
            Arrays.fill(buf1, ((byte) (170)));
            Arrays.fill(buf2, ((byte) (187)));
            Arrays.fill(buf3, ((byte) (204)));
            WebSocketFrame bin;
            bin = new BinaryFrame().setPayload(buf1).setFin(false);
            clientConn.write(bin);// write buf1 (fin=false)

            bin = new ContinuationFrame().setPayload(buf2).setFin(false);
            clientConn.write(bin);// write buf2 (fin=false)

            bin = new ContinuationFrame().setPayload(buf3).setFin(true);
            clientConn.write(bin);// write buf3 (fin=true)

            // Read frame echo'd back (hopefully a single binary frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            Frame binmsg = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            int expectedSize = ((buf1.length) + (buf2.length)) + (buf3.length);
            MatcherAssert.assertThat("BinaryFrame.payloadLength", binmsg.getPayloadLength(), Matchers.is(expectedSize));
            int aaCount = 0;
            int bbCount = 0;
            int ccCount = 0;
            ByteBuffer echod = binmsg.getPayload();
            while ((echod.remaining()) >= 1) {
                byte b = echod.get();
                switch (b) {
                    case ((byte) (170)) :
                        aaCount++;
                        break;
                    case ((byte) (187)) :
                        bbCount++;
                        break;
                    case ((byte) (204)) :
                        ccCount++;
                        break;
                    default :
                        Assertions.fail(String.format("Encountered invalid byte 0x%02X", ((byte) (255 & b))));
                }
            } 
            MatcherAssert.assertThat("Echoed data count for 0xAA", aaCount, Matchers.is(buf1.length));
            MatcherAssert.assertThat("Echoed data count for 0xBB", bbCount, Matchers.is(buf2.length));
            MatcherAssert.assertThat("Echoed data count for 0xCC", ccCount, Matchers.is(buf3.length));
        }
    }

    @Test
    public void testDetectBadUTF8() {
        byte[] buf = new byte[]{ ((byte) (194)), ((byte) (195)) };
        Utf8StringBuilder utf = new Utf8StringBuilder();
        Assertions.assertThrows(NotUtf8Exception.class, () -> {
            utf.append(buf, 0, buf.length);
        });
    }

    /**
     * Test the requirement of issuing socket and receiving echo response
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testEcho() throws Exception {
        BlockheadClientRequest request = WebSocketServletRFCTest.client.newWsRequest(WebSocketServletRFCTest.server.getServerUri());
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            clientConn.write(new TextFrame().setPayload(msg));
            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame.status code", tf.getPayloadAsUTF8(), Matchers.is(msg));
        }
    }

    /**
     * Test the requirement of responding with server terminated close code 1011 when there is an unhandled (internal server error) being produced by the
     * WebSocket POJO.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testInternalError() throws Exception {
        BlockheadClientRequest request = WebSocketServletRFCTest.client.newWsRequest(WebSocketServletRFCTest.server.getServerUri());
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT);StacklessLogging ignore = new StacklessLogging(EventDriver.class)) {
            // Generate text frame
            clientConn.write(new TextFrame().setPayload("CRASH"));
            // Read frame (hopefully close frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            Frame cf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            CloseInfo close = new CloseInfo(cf);
            MatcherAssert.assertThat("Close Frame.status code", close.getStatusCode(), Matchers.is(SERVER_ERROR));
        }
    }

    /**
     * Test http://tools.ietf.org/html/rfc6455#section-4.1 where server side upgrade handling is supposed to be case insensitive.
     * <p>
     * This test will simulate a client requesting upgrade with all lowercase headers.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testLowercaseUpgrade() throws Exception {
        BlockheadClientRequest request = WebSocketServletRFCTest.client.newWsRequest(WebSocketServletRFCTest.server.getServerUri());
        request.header("upgrade", "websocket");
        request.header("connection", "upgrade");
        request.header("sec-websocket-key", WebSocketServletRFCTest.REQUEST_HASH_KEY);
        request.header("sec-websocket-origin", WebSocketServletRFCTest.server.getServerUri().toASCIIString());
        request.header("sec-websocket-protocol", "echo");
        request.header("sec-websocket-version", "13");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            clientConn.write(new TextFrame().setPayload(msg));
            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame.status code", tf.getPayloadAsUTF8(), Matchers.is(msg));
        }
    }

    @Test
    public void testTextNotUTF8() throws Exception {
        BlockheadClientRequest request = WebSocketServletRFCTest.client.newWsRequest(WebSocketServletRFCTest.server.getServerUri());
        request.header(SEC_WEBSOCKET_SUBPROTOCOL, "other");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT);StacklessLogging ignore = new StacklessLogging(Parser.class)) {
            byte[] buf = new byte[]{ ((byte) (194)), ((byte) (195)) };
            WebSocketFrame txt = new TextFrame().setPayload(ByteBuffer.wrap(buf));
            txt.setMask(Hex.asByteArray("11223344"));
            ByteBuffer bbHeader = WebSocketServletRFCTest.generator.generateHeaderBytes(txt);
            clientConn.writeRaw(bbHeader);
            clientConn.writeRaw(txt.getPayload());
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame frame = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("frames[0].opcode", frame.getOpCode(), Matchers.is(CLOSE));
            CloseInfo close = new CloseInfo(frame);
            MatcherAssert.assertThat("Close Status Code", close.getStatusCode(), Matchers.is(BAD_PAYLOAD));
        }
    }

    /**
     * Test http://tools.ietf.org/html/rfc6455#section-4.1 where server side upgrade handling is supposed to be case insensitive.
     * <p>
     * This test will simulate a client requesting upgrade with all uppercase headers.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testUppercaseUpgrade() throws Exception {
        BlockheadClientRequest request = WebSocketServletRFCTest.client.newWsRequest(WebSocketServletRFCTest.server.getServerUri());
        request.header("UPGRADE", "WEBSOCKET");
        request.header("CONNECTION", "UPGRADE");
        request.header("SEC-WEBSOCKET-KEY", WebSocketServletRFCTest.REQUEST_HASH_KEY.toUpperCase(Locale.US));
        request.header("SEC-WEBSOCKET-ORIGIN", WebSocketServletRFCTest.server.getServerUri().toASCIIString());
        request.header("SEC-WEBSOCKET-PROTOCOL", "ECHO");
        request.header("SEC-WEBSOCKET-VERSION", "13");
        Future<BlockheadConnection> connFut = request.sendAsync();
        try (BlockheadConnection clientConn = connFut.get(CONNECT, CONNECT_UNIT)) {
            // Generate text frame
            String msg = "this is an echo ... cho ... ho ... o";
            clientConn.write(new TextFrame().setPayload(msg));
            // Read frame (hopefully text frame)
            LinkedBlockingQueue<WebSocketFrame> frames = clientConn.getFrameQueue();
            WebSocketFrame tf = frames.poll(POLL_EVENT, POLL_EVENT_UNIT);
            MatcherAssert.assertThat("Text Frame.status code", tf.getPayloadAsUTF8(), Matchers.is(msg));
        }
    }
}

