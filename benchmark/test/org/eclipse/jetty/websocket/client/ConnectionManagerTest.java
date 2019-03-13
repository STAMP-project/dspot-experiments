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
package org.eclipse.jetty.websocket.client;


import org.junit.jupiter.api.Test;


public class ConnectionManagerTest {
    @Test
    public void testToSocketAddress_AltWsPort() throws Exception {
        assertToSocketAddress("ws://localhost:8099", "localhost", 8099);
    }

    @Test
    public void testToSocketAddress_AltWssPort() throws Exception {
        assertToSocketAddress("wss://localhost", "localhost", 443);
    }

    @Test
    public void testToSocketAddress_DefaultWsPort() throws Exception {
        assertToSocketAddress("ws://localhost", "localhost", 80);
    }

    @Test
    public void testToSocketAddress_DefaultWsPort_Path() throws Exception {
        assertToSocketAddress("ws://localhost/sockets/chat", "localhost", 80);
    }

    @Test
    public void testToSocketAddress_DefaultWssPort() throws Exception {
        assertToSocketAddress("wss://localhost:9443", "localhost", 9443);
    }

    @Test
    public void testToSocketAddress_DefaultWssPort_Path() throws Exception {
        assertToSocketAddress("wss://localhost/sockets/chat", "localhost", 443);
    }
}

