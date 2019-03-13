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
package org.eclipse.jetty.http2.client;


import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class ConnectTimeoutTest extends AbstractTest {
    @Test
    public void testConnectTimeout() throws Exception {
        final String host = "10.255.255.1";
        final int port = 80;
        int connectTimeout = 1000;
        assumeConnectTimeout(host, port, connectTimeout);
        start(new ServerSessionListener.Adapter());
        client.setConnectTimeout(connectTimeout);
        InetSocketAddress address = new InetSocketAddress(host, port);
        final CountDownLatch latch = new CountDownLatch(1);
        client.connect(address, new Session.Listener.Adapter(), new Promise.Adapter<Session>() {
            @Override
            public void failed(Throwable x) {
                assertThat(x, instanceOf(.class));
                latch.countDown();
            }
        });
        Assertions.assertTrue(latch.await((2 * connectTimeout), TimeUnit.MILLISECONDS));
    }
}

