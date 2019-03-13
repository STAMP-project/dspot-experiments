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
import Fuzzer.SendMode.SLOW;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.eclipse.jetty.websocket.common.Parser;
import org.eclipse.jetty.websocket.common.WebSocketFrame;
import org.eclipse.jetty.websocket.common.frames.PingFrame;
import org.eclipse.jetty.websocket.common.frames.PongFrame;
import org.eclipse.jetty.websocket.common.test.Fuzzer;
import org.junit.jupiter.api.Test;


public class TestABCase2 extends AbstractABCase {
    /**
     * Ping without payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_1() throws Exception {
        WebSocketFrame send = new PingFrame();
        WebSocketFrame expect = new PongFrame();
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * 10 pings
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_10() throws Exception {
        // send 10 pings each with unique payload
        // send close
        // expect 10 pongs with our unique payload
        // expect close
        int pingCount = 10;
        List<WebSocketFrame> send = new ArrayList<>();
        List<WebSocketFrame> expect = new ArrayList<>();
        for (int i = 0; i < pingCount; i++) {
            String payload = String.format("ping-%d[%X]", i, i);
            send.add(new PingFrame().setPayload(payload));
            expect.add(new PongFrame().setPayload(payload));
        }
        send.add(asFrame());
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * 10 pings, sent slowly
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_11() throws Exception {
        // send 10 pings (slowly) each with unique payload
        // send close
        // expect 10 pongs with OUR payload
        // expect close
        int pingCount = 10;
        List<WebSocketFrame> send = new ArrayList<>();
        List<WebSocketFrame> expect = new ArrayList<>();
        for (int i = 0; i < pingCount; i++) {
            String payload = String.format("ping-%d[%X]", i, i);
            send.add(new PingFrame().setPayload(payload));
            expect.add(new PongFrame().setPayload(payload));
        }
        send.add(asFrame());
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(SLOW);
            fuzzer.setSlowSendSegmentSize(5);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Ping with small text payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_2() throws Exception {
        byte[] payload = StringUtil.getUtf8Bytes("Hello world");
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PingFrame().setPayload(payload));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new PongFrame().setPayload(copyOf(payload)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Ping with small binary (non-utf8) payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_3() throws Exception {
        byte[] payload = new byte[]{ 0, ((byte) (255)), ((byte) (254)), ((byte) (253)), ((byte) (252)), ((byte) (251)), 0, ((byte) (255)) };
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PingFrame().setPayload(payload));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new PongFrame().setPayload(copyOf(payload)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Ping with 125 byte binary payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_4() throws Exception {
        byte[] payload = new byte[125];
        Arrays.fill(payload, ((byte) (254)));
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PingFrame().setPayload(payload));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new PongFrame().setPayload(copyOf(payload)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Ping with 126 byte binary payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_5() throws Exception {
        try (StacklessLogging scope = new StacklessLogging(Parser.class)) {
            byte[] payload = new byte[126];// intentionally too big

            Arrays.fill(payload, ((byte) ('5')));
            ByteBuffer buf = ByteBuffer.wrap(payload);
            List<WebSocketFrame> send = new ArrayList<>();
            // trick websocket frame into making extra large payload for ping
            send.add(setPayload(buf));
            send.add(asFrame());
            List<WebSocketFrame> expect = new ArrayList<>();
            expect.add(asFrame());
            try (Fuzzer fuzzer = new Fuzzer(this)) {
                fuzzer.connect();
                fuzzer.setSendMode(BULK);
                fuzzer.send(send);
                fuzzer.expect(expect);
            }
        }
    }

    /**
     * Ping with 125 byte binary payload (slow send)
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_6() throws Exception {
        byte[] payload = new byte[125];
        Arrays.fill(payload, ((byte) ('6')));
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PingFrame().setPayload(payload));
        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new PongFrame().setPayload(copyOf(payload)));
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(SLOW);
            fuzzer.setSlowSendSegmentSize(1);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Unsolicited pong frame without payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_7() throws Exception {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PongFrame());// unsolicited pong

        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Unsolicited pong frame with basic payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_8() throws Exception {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PongFrame().setPayload("unsolicited"));// unsolicited pong

        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }

    /**
     * Unsolicited pong frame, then ping with basic payload
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testCase2_9() throws Exception {
        List<WebSocketFrame> send = new ArrayList<>();
        send.add(new PongFrame().setPayload("unsolicited"));// unsolicited pong

        send.add(new PingFrame().setPayload("our ping"));// our ping

        send.add(asFrame());
        List<WebSocketFrame> expect = new ArrayList<>();
        expect.add(new PongFrame().setPayload("our ping"));// our pong

        expect.add(asFrame());
        try (Fuzzer fuzzer = new Fuzzer(this)) {
            fuzzer.connect();
            fuzzer.setSendMode(BULK);
            fuzzer.send(send);
            fuzzer.expect(expect);
        }
    }
}

