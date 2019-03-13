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
package org.eclipse.jetty.websocket.jsr356.server;


import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.MappedByteBufferPool;
import org.eclipse.jetty.websocket.jsr356.server.samples.beans.DateDecoder;
import org.eclipse.jetty.websocket.jsr356.server.samples.beans.TimeEncoder;
import org.eclipse.jetty.websocket.jsr356.server.samples.echo.EchoSocketConfigurator;
import org.junit.jupiter.api.Test;


/**
 * Example of an annotated echo server discovered via annotation scanning.
 */
public class AnnotatedServerEndpointTest {
    public ByteBufferPool bufferPool = new MappedByteBufferPool();

    private static WSServer server;

    @Test
    public void testConfigurator() throws Exception {
        assertResponse("configurator", EchoSocketConfigurator.class.getName());
    }

    @Test
    public void testTextMax() throws Exception {
        assertResponse("text-max", "111,222");
    }

    @Test
    public void testBinaryMax() throws Exception {
        assertResponse("binary-max", "333,444");
    }

    @Test
    public void testDecoders() throws Exception {
        assertResponse("decoders", DateDecoder.class.getName());
    }

    @Test
    public void testEncoders() throws Exception {
        assertResponse("encoders", TimeEncoder.class.getName());
    }

    @Test
    public void testSubProtocols() throws Exception {
        assertResponse("subprotocols", "chat, echo, test");
    }
}

