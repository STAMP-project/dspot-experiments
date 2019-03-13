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
package org.eclipse.jetty.websocket.common;


import OpCode.CLOSE;
import java.nio.ByteBuffer;
import org.eclipse.jetty.websocket.common.frames.CloseFrame;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class CloseInfoTest {
    /**
     * A test where no close is provided
     */
    @Test
    public void testAnonymousClose() {
        CloseInfo close = new CloseInfo();
        MatcherAssert.assertThat("close.code", close.getStatusCode(), Matchers.is(NO_CODE));
        MatcherAssert.assertThat("close.reason", close.getReason(), Matchers.nullValue());
        CloseFrame frame = close.asFrame();
        MatcherAssert.assertThat("close frame op code", frame.getOpCode(), Matchers.is(CLOSE));
        // should result in no payload
        MatcherAssert.assertThat("close frame has payload", frame.hasPayload(), Matchers.is(false));
        MatcherAssert.assertThat("close frame payload length", frame.getPayloadLength(), Matchers.is(0));
    }

    /**
     * A test where NO_CODE (1005) is provided
     */
    @Test
    public void testNoCode() {
        CloseInfo close = new CloseInfo(NO_CODE);
        MatcherAssert.assertThat("close.code", close.getStatusCode(), Matchers.is(NO_CODE));
        MatcherAssert.assertThat("close.reason", close.getReason(), Matchers.nullValue());
        CloseFrame frame = close.asFrame();
        MatcherAssert.assertThat("close frame op code", frame.getOpCode(), Matchers.is(CLOSE));
        // should result in no payload
        MatcherAssert.assertThat("close frame has payload", frame.hasPayload(), Matchers.is(false));
        MatcherAssert.assertThat("close frame payload length", frame.getPayloadLength(), Matchers.is(0));
    }

    /**
     * A test where NO_CLOSE (1006) is provided
     */
    @Test
    public void testNoClose() {
        CloseInfo close = new CloseInfo(NO_CLOSE);
        MatcherAssert.assertThat("close.code", close.getStatusCode(), Matchers.is(NO_CLOSE));
        MatcherAssert.assertThat("close.reason", close.getReason(), Matchers.nullValue());
        CloseFrame frame = close.asFrame();
        MatcherAssert.assertThat("close frame op code", frame.getOpCode(), Matchers.is(CLOSE));
        // should result in no payload
        MatcherAssert.assertThat("close frame has payload", frame.hasPayload(), Matchers.is(false));
        MatcherAssert.assertThat("close frame payload length", frame.getPayloadLength(), Matchers.is(0));
    }

    /**
     * A test of FAILED_TLS_HANDSHAKE (1007)
     */
    @Test
    public void testFailedTlsHandshake() {
        CloseInfo close = new CloseInfo(FAILED_TLS_HANDSHAKE);
        MatcherAssert.assertThat("close.code", close.getStatusCode(), Matchers.is(FAILED_TLS_HANDSHAKE));
        MatcherAssert.assertThat("close.reason", close.getReason(), Matchers.nullValue());
        CloseFrame frame = close.asFrame();
        MatcherAssert.assertThat("close frame op code", frame.getOpCode(), Matchers.is(CLOSE));
        // should result in no payload
        MatcherAssert.assertThat("close frame has payload", frame.hasPayload(), Matchers.is(false));
        MatcherAssert.assertThat("close frame payload length", frame.getPayloadLength(), Matchers.is(0));
    }

    /**
     * A test of NORMAL (1000)
     */
    @Test
    public void testNormal() {
        CloseInfo close = new CloseInfo(NORMAL);
        MatcherAssert.assertThat("close.code", close.getStatusCode(), Matchers.is(NORMAL));
        MatcherAssert.assertThat("close.reason", close.getReason(), Matchers.nullValue());
        CloseFrame frame = close.asFrame();
        MatcherAssert.assertThat("close frame op code", frame.getOpCode(), Matchers.is(CLOSE));
        MatcherAssert.assertThat("close frame payload length", frame.getPayloadLength(), Matchers.is(2));
    }

    @Test
    public void testFromFrame() {
        ByteBuffer payload = asByteBuffer(NORMAL, null);
        MatcherAssert.assertThat("payload length", payload.remaining(), Matchers.is(2));
        CloseFrame frame = new CloseFrame();
        frame.setPayload(payload);
        // create from frame
        CloseInfo close = new CloseInfo(frame);
        MatcherAssert.assertThat("close.code", close.getStatusCode(), Matchers.is(NORMAL));
        MatcherAssert.assertThat("close.reason", close.getReason(), Matchers.nullValue());
        // and back again
        frame = close.asFrame();
        MatcherAssert.assertThat("close frame op code", frame.getOpCode(), Matchers.is(CLOSE));
        MatcherAssert.assertThat("close frame payload length", frame.getPayloadLength(), Matchers.is(2));
    }
}

