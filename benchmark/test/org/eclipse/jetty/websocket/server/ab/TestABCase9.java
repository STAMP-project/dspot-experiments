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
package org.eclipse.jetty.websocket.server.ab;


import Fuzzer.SendMode.BULK;
import OpCode.BINARY;
import OpCode.TEXT;
import Timeouts.POLL_EVENT_UNIT;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.BinaryFrame;
import org.eclipse.jetty.websocket.common.frames.TextFrame;
import org.eclipse.jetty.websocket.common.test.Fuzzer;
import org.eclipse.jetty.websocket.common.test.Timeouts;
import org.junit.jupiter.api.Test;


/**
 * Big frame/message tests
 */
public class TestABCase9 extends AbstractABCase {
    private static final int KBYTE = 1024;

    private static final int MBYTE = (TestABCase9.KBYTE) * (TestABCase9.KBYTE);

    /**
     * Echo 64KB text message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_1_1() throws Exception {
        byte[] utf = new byte[64 * (TestABCase9.KBYTE)];
        Arrays.fill(utf, ((byte) ('y')));
        String msg = StringUtil.toUTF8String(utf, 0, utf.length);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload(msg));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new TextFrame().setPayload(msg));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Echo 256KB text message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_1_2() throws Exception {
        byte[] utf = new byte[256 * (TestABCase9.KBYTE)];
        Arrays.fill(utf, ((byte) ('y')));
        ByteBuffer buf = ByteBuffer.wrap(utf);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new TextFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, ((Timeouts.POLL_EVENT) * 2), POLL_EVENT_UNIT);
        }
    }

    /**
     * Echo 1MB text message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_1_3() throws Exception {
        byte[] utf = new byte[1 * (TestABCase9.MBYTE)];
        Arrays.fill(utf, ((byte) ('y')));
        ByteBuffer buf = ByteBuffer.wrap(utf);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new TextFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 4, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 4MB text message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_1_4() throws Exception {
        byte[] utf = new byte[4 * (TestABCase9.MBYTE)];
        Arrays.fill(utf, ((byte) ('y')));
        ByteBuffer buf = ByteBuffer.wrap(utf);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new TextFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 8, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 8MB text message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_1_5() throws Exception {
        byte[] utf = new byte[8 * (TestABCase9.MBYTE)];
        Arrays.fill(utf, ((byte) ('y')));
        ByteBuffer buf = ByteBuffer.wrap(utf);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new TextFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 16, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 16MB text message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_1_6() throws Exception {
        byte[] utf = new byte[16 * (TestABCase9.MBYTE)];
        Arrays.fill(utf, ((byte) ('y')));
        ByteBuffer buf = ByteBuffer.wrap(utf);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new TextFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new TextFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 32, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 64KB binary message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_2_1() throws Exception {
        byte[] data = new byte[64 * (TestABCase9.KBYTE)];
        Arrays.fill(data, ((byte) (33)));
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new BinaryFrame().setPayload(data));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new BinaryFrame().setPayload(copyOf(data)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Echo 256KB binary message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_2_2() throws Exception {
        byte[] data = new byte[256 * (TestABCase9.KBYTE)];
        Arrays.fill(data, ((byte) (34)));
        ByteBuffer buf = ByteBuffer.wrap(data);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new BinaryFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new BinaryFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Echo 1MB binary message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_2_3() throws Exception {
        byte[] data = new byte[1 * (TestABCase9.MBYTE)];
        Arrays.fill(data, ((byte) (35)));
        ByteBuffer buf = ByteBuffer.wrap(data);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new BinaryFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new BinaryFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 4, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 4MB binary message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_2_4() throws Exception {
        byte[] data = new byte[4 * (TestABCase9.MBYTE)];
        Arrays.fill(data, ((byte) (36)));
        ByteBuffer buf = ByteBuffer.wrap(data);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new BinaryFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new BinaryFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 8, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 8MB binary message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_2_5() throws Exception {
        byte[] data = new byte[8 * (TestABCase9.MBYTE)];
        Arrays.fill(data, ((byte) (37)));
        ByteBuffer buf = ByteBuffer.wrap(data);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new BinaryFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new BinaryFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 16, TimeUnit.SECONDS);
        }
    }

    /**
     * Echo 16MB binary message (1 frame)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_2_6() throws Exception {
        byte[] data = new byte[16 * (TestABCase9.MBYTE)];
        Arrays.fill(data, ((byte) (38)));
        ByteBuffer buf = ByteBuffer.wrap(data);
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new BinaryFrame().setPayload(buf));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new BinaryFrame().setPayload(clone(buf)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect, 32, TimeUnit.SECONDS);
        }
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_1() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), 64);
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_2() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), 256);
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_3() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (1 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_4() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (4 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_5() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (16 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_6() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (64 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_7() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (256 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_8() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (1 * (TestABCase9.MBYTE)));
    }

    /**
     * Send 4MB text message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_3_9() throws Exception {
        assertMultiFrameEcho(TEXT, (4 * (TestABCase9.MBYTE)), (4 * (TestABCase9.MBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_1() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), 64);
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_2() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), 256);
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_3() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (1 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_4() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (4 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_5() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (16 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_6() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (64 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_7() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (256 * (TestABCase9.KBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_8() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (1 * (TestABCase9.MBYTE)));
    }

    /**
     * Send 4MB binary message in multiple frames.
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_4_9() throws Exception {
        assertMultiFrameEcho(BINARY, (4 * (TestABCase9.MBYTE)), (4 * (TestABCase9.MBYTE)));
    }

    /**
     * Send 1MB text message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_5_1() throws Exception {
        assertSlowFrameEcho(TEXT, (1 * (TestABCase9.MBYTE)), 64);
    }

    /**
     * Send 1MB text message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_5_2() throws Exception {
        assertSlowFrameEcho(TEXT, (1 * (TestABCase9.MBYTE)), 128);
    }

    /**
     * Send 1MB text message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_5_3() throws Exception {
        assertSlowFrameEcho(TEXT, (1 * (TestABCase9.MBYTE)), 256);
    }

    /**
     * Send 1MB text message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_5_4() throws Exception {
        assertSlowFrameEcho(TEXT, (1 * (TestABCase9.MBYTE)), 512);
    }

    /**
     * Send 1MB text message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_5_5() throws Exception {
        assertSlowFrameEcho(TEXT, (1 * (TestABCase9.MBYTE)), 1024);
    }

    /**
     * Send 1MB text message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_5_6() throws Exception {
        assertSlowFrameEcho(TEXT, (1 * (TestABCase9.MBYTE)), 2048);
    }

    /**
     * Send 1MB binary message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_6_1() throws Exception {
        assertSlowFrameEcho(BINARY, (1 * (TestABCase9.MBYTE)), 64);
    }

    /**
     * Send 1MB binary message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_6_2() throws Exception {
        assertSlowFrameEcho(BINARY, (1 * (TestABCase9.MBYTE)), 128);
    }

    /**
     * Send 1MB binary message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_6_3() throws Exception {
        assertSlowFrameEcho(BINARY, (1 * (TestABCase9.MBYTE)), 256);
    }

    /**
     * Send 1MB binary message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_6_4() throws Exception {
        assertSlowFrameEcho(BINARY, (1 * (TestABCase9.MBYTE)), 512);
    }

    /**
     * Send 1MB binary message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_6_5() throws Exception {
        assertSlowFrameEcho(BINARY, (1 * (TestABCase9.MBYTE)), 1024);
    }

    /**
     * Send 1MB binary message in 1 frame, but slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase9_6_6() throws Exception {
        assertSlowFrameEcho(BINARY, (1 * (TestABCase9.MBYTE)), 2048);
    }
}

