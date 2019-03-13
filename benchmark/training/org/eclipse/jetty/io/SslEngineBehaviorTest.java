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
package org.eclipse.jetty.io;


import BufferUtil.EMPTY_BUFFER;
import java.nio.ByteBuffer;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_TASK;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_UNWRAP;
import static javax.net.ssl.SSLEngineResult.HandshakeStatus.NEED_WRAP;
import static javax.net.ssl.SSLEngineResult.Status.BUFFER_OVERFLOW;
import static javax.net.ssl.SSLEngineResult.Status.OK;


public class SslEngineBehaviorTest {
    private static SslContextFactory sslCtxFactory;

    @Test
    @EnabledOnJre(JRE.JAVA_8)
    public void checkSslEngineBehaviour() throws Exception {
        SSLEngine server = SslEngineBehaviorTest.sslCtxFactory.newSSLEngine();
        SSLEngine client = SslEngineBehaviorTest.sslCtxFactory.newSSLEngine();
        ByteBuffer netC2S = ByteBuffer.allocate(server.getSession().getPacketBufferSize());
        ByteBuffer netS2C = ByteBuffer.allocate(server.getSession().getPacketBufferSize());
        ByteBuffer serverIn = ByteBuffer.allocate(server.getSession().getApplicationBufferSize());
        ByteBuffer serverOut = ByteBuffer.allocate(server.getSession().getApplicationBufferSize());
        ByteBuffer clientIn = ByteBuffer.allocate(client.getSession().getApplicationBufferSize());
        SSLEngineResult result;
        // start the client
        client.setUseClientMode(true);
        client.beginHandshake();
        Assertions.assertEquals(NEED_WRAP, client.getHandshakeStatus());
        // what if we try an unwrap?
        netS2C.flip();
        result = client.unwrap(netS2C, clientIn);
        // unwrap is a noop
        Assertions.assertEquals(OK, result.getStatus());
        Assertions.assertEquals(0, result.bytesConsumed());
        Assertions.assertEquals(0, result.bytesProduced());
        Assertions.assertEquals(NEED_WRAP, result.getHandshakeStatus());
        netS2C.clear();
        // do the needed WRAP of empty buffer
        result = client.wrap(EMPTY_BUFFER, netC2S);
        // unwrap is a noop
        Assertions.assertEquals(OK, result.getStatus());
        Assertions.assertEquals(0, result.bytesConsumed());
        MatcherAssert.assertThat(result.bytesProduced(), Matchers.greaterThan(0));
        Assertions.assertEquals(NEED_UNWRAP, result.getHandshakeStatus());
        netC2S.flip();
        Assertions.assertEquals(netC2S.remaining(), result.bytesProduced());
        // start the server
        server.setUseClientMode(false);
        server.beginHandshake();
        Assertions.assertEquals(NEED_UNWRAP, server.getHandshakeStatus());
        // what if we try a needless wrap?
        serverOut.put(BufferUtil.toBuffer("Hello World"));
        serverOut.flip();
        result = server.wrap(serverOut, netS2C);
        // wrap is a noop
        Assertions.assertEquals(OK, result.getStatus());
        Assertions.assertEquals(0, result.bytesConsumed());
        Assertions.assertEquals(0, result.bytesProduced());
        Assertions.assertEquals(NEED_UNWRAP, result.getHandshakeStatus());
        // Do the needed unwrap, to an empty buffer
        result = server.unwrap(netC2S, EMPTY_BUFFER);
        Assertions.assertEquals(BUFFER_OVERFLOW, result.getStatus());
        Assertions.assertEquals(0, result.bytesConsumed());
        Assertions.assertEquals(0, result.bytesProduced());
        Assertions.assertEquals(NEED_UNWRAP, result.getHandshakeStatus());
        // Do the needed unwrap, to a full buffer
        serverIn.position(serverIn.limit());
        result = server.unwrap(netC2S, serverIn);
        Assertions.assertEquals(BUFFER_OVERFLOW, result.getStatus());
        Assertions.assertEquals(0, result.bytesConsumed());
        Assertions.assertEquals(0, result.bytesProduced());
        Assertions.assertEquals(NEED_UNWRAP, result.getHandshakeStatus());
        // Do the needed unwrap, to an empty buffer
        serverIn.clear();
        result = server.unwrap(netC2S, serverIn);
        Assertions.assertEquals(OK, result.getStatus());
        MatcherAssert.assertThat(result.bytesConsumed(), Matchers.greaterThan(0));
        Assertions.assertEquals(0, result.bytesProduced());
        Assertions.assertEquals(NEED_TASK, result.getHandshakeStatus());
        server.getDelegatedTask().run();
        Assertions.assertEquals(NEED_WRAP, server.getHandshakeStatus());
    }
}

