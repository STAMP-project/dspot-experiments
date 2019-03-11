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
package org.eclipse.jetty.websocket.common.io;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConnectionStateTest {
    @Test
    public void testHandshakeToOpened() {
        ConnectionState state = new ConnectionState();
        Assertions.assertFalse(state.canWriteWebSocketFrames(), "Handshaking canWriteWebSocketFrames");
        Assertions.assertFalse(state.canReadWebSocketFrames(), "Handshaking canReadWebSocketFrames");
        Assertions.assertTrue(state.opening(), "Opening");
        Assertions.assertTrue(state.canWriteWebSocketFrames(), "Opening canWriteWebSocketFrames");
        Assertions.assertFalse(state.canReadWebSocketFrames(), "Opening canReadWebSocketFrames");
        Assertions.assertTrue(state.opened(), "Opened");
        Assertions.assertTrue(state.canWriteWebSocketFrames(), "Opened canWriteWebSocketFrames");
        Assertions.assertTrue(state.canReadWebSocketFrames(), "Opened canReadWebSocketFrames");
    }

    @Test
    public void testOpened_Closing() {
        ConnectionState state = new ConnectionState();
        Assertions.assertTrue(state.opening(), "Opening");
        Assertions.assertTrue(state.opened(), "Opened");
        Assertions.assertTrue(state.closing(), "Closing (initial)");
        // A closing state allows for read, but not write
        Assertions.assertFalse(state.canWriteWebSocketFrames(), "Closing canWriteWebSocketFrames");
        Assertions.assertTrue(state.canReadWebSocketFrames(), "Closing canReadWebSocketFrames");
        // Closing again shouldn't allow for another close frame to be sent
        Assertions.assertFalse(state.closing(), "Closing (extra)");
    }

    @Test
    public void testOpened_Closing_Disconnected() {
        ConnectionState state = new ConnectionState();
        Assertions.assertTrue(state.opening(), "Opening");
        Assertions.assertTrue(state.opened(), "Opened");
        Assertions.assertTrue(state.closing(), "Closing");
        Assertions.assertTrue(state.disconnected(), "Disconnected");
        Assertions.assertFalse(state.canWriteWebSocketFrames(), "Disconnected canWriteWebSocketFrames");
        Assertions.assertFalse(state.canReadWebSocketFrames(), "Disconnected canReadWebSocketFrames");
    }

    @Test
    public void testOpened_Harsh_Disconnected() {
        ConnectionState state = new ConnectionState();
        Assertions.assertTrue(state.opening(), "Opening");
        Assertions.assertTrue(state.opened(), "Opened");
        // INTENTIONALLY HAD NO CLOSING - assertTrue(state.closing(), "Closing");
        Assertions.assertTrue(state.disconnected(), "Disconnected");
        Assertions.assertFalse(state.canWriteWebSocketFrames(), "Disconnected canWriteWebSocketFrames");
        Assertions.assertFalse(state.canReadWebSocketFrames(), "Disconnected canReadWebSocketFrames");
    }
}

