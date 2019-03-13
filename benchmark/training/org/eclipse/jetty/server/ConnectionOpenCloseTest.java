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
package org.eclipse.jetty.server;


import HttpTester.Response;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.toolchain.test.MavenTestingUtils;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;


public class ConnectionOpenCloseTest extends AbstractHttpTest {
    // TODO: SLOW, needs review
    @Test
    @Tag("Slow")
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testOpenClose() throws Exception {
        AbstractHttpTest.server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                throw new IllegalStateException();
            }
        });
        AbstractHttpTest.server.start();
        final AtomicInteger callbacks = new AtomicInteger();
        final CountDownLatch openLatch = new CountDownLatch(1);
        final CountDownLatch closeLatch = new CountDownLatch(1);
        AbstractHttpTest.connector.addBean(new Connection.Listener.Adapter() {
            @Override
            public void onOpened(Connection connection) {
                callbacks.incrementAndGet();
                openLatch.countDown();
            }

            @Override
            public void onClosed(Connection connection) {
                callbacks.incrementAndGet();
                closeLatch.countDown();
            }
        });
        try (Socket socket = new Socket("localhost", AbstractHttpTest.connector.getLocalPort())) {
            socket.setSoTimeout(((int) (AbstractHttpTest.connector.getIdleTimeout())));
            Assertions.assertTrue(openLatch.await(5, TimeUnit.SECONDS));
            socket.shutdownOutput();
            Assertions.assertTrue(closeLatch.await(5, TimeUnit.SECONDS));
            String response = IO.toString(socket.getInputStream());
            Assertions.assertEquals(0, response.length());
            // Wait some time to see if the callbacks are called too many times
            TimeUnit.MILLISECONDS.sleep(200);
            Assertions.assertEquals(2, callbacks.get());
        }
    }

    // TODO: SLOW, needs review
    @Test
    @Tag("Slow")
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testOpenRequestClose() throws Exception {
        AbstractHttpTest.server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                baseRequest.setHandled(true);
            }
        });
        AbstractHttpTest.server.start();
        final AtomicInteger callbacks = new AtomicInteger();
        final CountDownLatch openLatch = new CountDownLatch(1);
        final CountDownLatch closeLatch = new CountDownLatch(1);
        AbstractHttpTest.connector.addBean(new Connection.Listener.Adapter() {
            @Override
            public void onOpened(Connection connection) {
                callbacks.incrementAndGet();
                openLatch.countDown();
            }

            @Override
            public void onClosed(Connection connection) {
                callbacks.incrementAndGet();
                closeLatch.countDown();
            }
        });
        try (Socket socket = new Socket("localhost", AbstractHttpTest.connector.getLocalPort())) {
            socket.setSoTimeout(((int) (AbstractHttpTest.connector.getIdleTimeout())));
            OutputStream output = socket.getOutputStream();
            output.write(((((("GET / HTTP/1.1\r\n" + "Host: localhost:") + (AbstractHttpTest.connector.getLocalPort())) + "\r\n") + "Connection: close\r\n") + "\r\n").getBytes(StandardCharsets.UTF_8));
            output.flush();
            InputStream inputStream = socket.getInputStream();
            HttpTester.Response response = HttpTester.parseResponse(inputStream);
            MatcherAssert.assertThat("Status Code", response.getStatus(), Matchers.is(200));
            Assertions.assertEquals((-1), inputStream.read());
            socket.close();
            Assertions.assertTrue(openLatch.await(5, TimeUnit.SECONDS));
            Assertions.assertTrue(closeLatch.await(5, TimeUnit.SECONDS));
            // Wait some time to see if the callbacks are called too many times
            TimeUnit.SECONDS.sleep(1);
            Assertions.assertEquals(2, callbacks.get());
        }
    }

    // TODO: SLOW, needs review
    @Test
    @Tag("Slow")
    @DisabledIfSystemProperty(named = "env", matches = "ci")
    public void testSSLOpenRequestClose() throws Exception {
        SslContextFactory sslContextFactory = new SslContextFactory();
        File keystore = MavenTestingUtils.getTestResourceFile("keystore");
        sslContextFactory.setKeyStoreResource(Resource.newResource(keystore));
        sslContextFactory.setKeyStorePassword("storepwd");
        sslContextFactory.setKeyManagerPassword("keypwd");
        AbstractHttpTest.server.addBean(sslContextFactory);
        AbstractHttpTest.server.removeConnector(AbstractHttpTest.connector);
        AbstractHttpTest.connector = new ServerConnector(AbstractHttpTest.server, sslContextFactory);
        AbstractHttpTest.server.addConnector(AbstractHttpTest.connector);
        AbstractHttpTest.server.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) {
                baseRequest.setHandled(true);
            }
        });
        AbstractHttpTest.server.start();
        final AtomicInteger callbacks = new AtomicInteger();
        final CountDownLatch openLatch = new CountDownLatch(2);
        final CountDownLatch closeLatch = new CountDownLatch(2);
        AbstractHttpTest.connector.addBean(new Connection.Listener.Adapter() {
            @Override
            public void onOpened(Connection connection) {
                callbacks.incrementAndGet();
                openLatch.countDown();
            }

            @Override
            public void onClosed(Connection connection) {
                callbacks.incrementAndGet();
                closeLatch.countDown();
            }
        });
        Socket socket = sslContextFactory.getSslContext().getSocketFactory().createSocket("localhost", AbstractHttpTest.connector.getLocalPort());
        socket.setSoTimeout(((int) (AbstractHttpTest.connector.getIdleTimeout())));
        OutputStream output = socket.getOutputStream();
        output.write(((((("" + ("GET / HTTP/1.1\r\n" + "Host: localhost:")) + (AbstractHttpTest.connector.getLocalPort())) + "\r\n") + "Connection: close\r\n") + "\r\n").getBytes(StandardCharsets.UTF_8));
        output.flush();
        // Read to EOF
        String response = BufferUtil.toString(ByteBuffer.wrap(IO.readBytes(socket.getInputStream())));
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        socket.close();
        Assertions.assertTrue(openLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(closeLatch.await(5, TimeUnit.SECONDS));
        // Wait some time to see if the callbacks are called too many times
        TimeUnit.SECONDS.sleep(1);
        Assertions.assertEquals(4, callbacks.get());
    }
}

