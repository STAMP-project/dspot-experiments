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


import Stream.Listener;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.http2.api.Stream;
import org.eclipse.jetty.http2.api.server.ServerSessionListener;
import org.eclipse.jetty.http2.frames.HeadersFrame;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SessionFailureTest extends AbstractTest {
    @Test
    public void testWrongPreface() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public void onFailure(Session session, Throwable failure) {
                latch.countDown();
            }
        });
        try (Socket socket = new Socket("localhost", connector.getLocalPort())) {
            // Preface starts with byte 0x50, send something different.
            OutputStream output = socket.getOutputStream();
            output.write(0);
            output.flush();
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
            // The server will reply with a GOAWAY frame, and then shutdown.
            // Read until EOF.
            socket.setSoTimeout(1000);
            InputStream input = socket.getInputStream();
            while (true) {
                if ((input.read()) < 0)
                    break;

            } 
        }
    }

    @Test
    public void testWriteFailure() throws Exception {
        final CountDownLatch writeLatch = new CountDownLatch(1);
        final CountDownLatch serverFailureLatch = new CountDownLatch(1);
        start(new ServerSessionListener.Adapter() {
            @Override
            public Listener onNewStream(Stream stream, HeadersFrame frame) {
                // Forcibly close the connection.
                getEndPoint().close();
                // Now try to write something: it should fail.
                stream.headers(frame, new Callback() {
                    @Override
                    public void failed(Throwable x) {
                        writeLatch.countDown();
                    }
                });
                return null;
            }

            @Override
            public void onFailure(Session session, Throwable failure) {
                serverFailureLatch.countDown();
            }
        });
        final CountDownLatch clientFailureLatch = new CountDownLatch(1);
        Session session = newClient(new Session.Listener.Adapter() {
            @Override
            public void onFailure(Session session, Throwable failure) {
                clientFailureLatch.countDown();
            }
        });
        HeadersFrame frame = new HeadersFrame(newRequest("GET", new HttpFields()), null, true);
        Promise<Stream> promise = new Promise.Adapter<>();
        session.newStream(frame, promise, null);
        Assertions.assertTrue(writeLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(serverFailureLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(clientFailureLatch.await(5, TimeUnit.SECONDS));
        long start = System.nanoTime();
        long now = System.nanoTime();
        while (getEndPoint().isOpen()) {
            MatcherAssert.assertThat(TimeUnit.NANOSECONDS.toSeconds((now - start)), Matchers.lessThanOrEqualTo(5L));
            Thread.sleep(10);
            now = System.nanoTime();
        } 
    }
}

