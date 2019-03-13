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
package org.eclipse.jetty.server.ssl;


import HttpMethod.POST;
import HttpStatus.OK_200;
import HttpTester.Response;
import SslContextFactory.TRUST_ALL_CERTS;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SslContextFactoryReloadTest {
    public static final String KEYSTORE_1 = "src/test/resources/reload_keystore_1.jks";

    public static final String KEYSTORE_2 = "src/test/resources/reload_keystore_2.jks";

    private Server server;

    private SslContextFactory sslContextFactory;

    private ServerConnector connector;

    @Test
    public void testReload() throws Exception {
        start(new SslContextFactoryReloadTest.EchoHandler());
        SSLContext ctx = SSLContext.getInstance("TLSv1.2");
        ctx.init(null, TRUST_ALL_CERTS, null);
        SSLSocketFactory socketFactory = ctx.getSocketFactory();
        try (SSLSocket client1 = ((SSLSocket) (socketFactory.createSocket("localhost", connector.getLocalPort())))) {
            String serverDN1 = client1.getSession().getPeerPrincipal().getName();
            MatcherAssert.assertThat(serverDN1, Matchers.startsWith("CN=localhost1"));
            String request = "" + (("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "\r\n");
            OutputStream output1 = client1.getOutputStream();
            output1.write(request.getBytes(StandardCharsets.UTF_8));
            output1.flush();
            HttpTester.Response response1 = HttpTester.parseResponse(HttpTester.from(client1.getInputStream()));
            Assertions.assertNotNull(response1);
            MatcherAssert.assertThat(response1.getStatus(), Matchers.equalTo(OK_200));
            // Reconfigure SslContextFactory.
            sslContextFactory.reload(( sslContextFactory) -> {
                sslContextFactory.setKeyStorePath(KEYSTORE_2);
                sslContextFactory.setKeyStorePassword("storepwd");
            });
            // New connection should use the new keystore.
            try (SSLSocket client2 = ((SSLSocket) (socketFactory.createSocket("localhost", connector.getLocalPort())))) {
                String serverDN2 = client2.getSession().getPeerPrincipal().getName();
                MatcherAssert.assertThat(serverDN2, Matchers.startsWith("CN=localhost2"));
                OutputStream output2 = client1.getOutputStream();
                output2.write(request.getBytes(StandardCharsets.UTF_8));
                output2.flush();
                HttpTester.Response response2 = HttpTester.parseResponse(HttpTester.from(client1.getInputStream()));
                Assertions.assertNotNull(response2);
                MatcherAssert.assertThat(response2.getStatus(), Matchers.equalTo(OK_200));
            }
            // Must still be possible to make requests with the first connection.
            output1.write(request.getBytes(StandardCharsets.UTF_8));
            output1.flush();
            response1 = HttpTester.parseResponse(HttpTester.from(client1.getInputStream()));
            Assertions.assertNotNull(response1);
            MatcherAssert.assertThat(response1.getStatus(), Matchers.equalTo(OK_200));
        }
    }

    @Test
    public void testReloadWhileServing() throws Exception {
        start(new SslContextFactoryReloadTest.EchoHandler());
        Scheduler scheduler = new ScheduledExecutorScheduler();
        scheduler.start();
        try {
            SSLContext ctx = SSLContext.getInstance("TLSv1.2");
            ctx.init(null, TRUST_ALL_CERTS, null);
            SSLSocketFactory socketFactory = ctx.getSocketFactory();
            // Perform 4 reloads while connections are being served.
            AtomicInteger reloads = new AtomicInteger(4);
            long reloadPeriod = 500;
            AtomicBoolean running = new AtomicBoolean(true);
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    if ((reloads.decrementAndGet()) == 0) {
                        running.set(false);
                    } else {
                        try {
                            sslContextFactory.reload(( sslContextFactory) -> {
                                if (sslContextFactory.getKeyStorePath().endsWith(SslContextFactoryReloadTest.KEYSTORE_1))
                                    sslContextFactory.setKeyStorePath(SslContextFactoryReloadTest.KEYSTORE_2);
                                else
                                    sslContextFactory.setKeyStorePath(SslContextFactoryReloadTest.KEYSTORE_1);

                            });
                            scheduler.schedule(this, reloadPeriod, TimeUnit.MILLISECONDS);
                        } catch (Exception x) {
                            running.set(false);
                            reloads.set((-1));
                        }
                    }
                }
            }, reloadPeriod, TimeUnit.MILLISECONDS);
            byte[] content = new byte[16 * 1024];
            while (running.get()) {
                try (SSLSocket client = ((SSLSocket) (socketFactory.createSocket("localhost", connector.getLocalPort())))) {
                    // We need to invalidate the session every time we open a new SSLSocket.
                    // This is because when the client uses session resumption, it caches
                    // the server certificates and then checks that it is the same during
                    // a new TLS handshake. If the SslContextFactory is reloaded during the
                    // TLS handshake, the client will see the new certificate and blow up.
                    // Note that browsers can handle this case better: they will just not
                    // use session resumption and fallback to the normal TLS handshake.
                    client.getSession().invalidate();
                    String request1 = ((("" + (("POST / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Content-Length: ")) + (content.length)) + "\r\n") + "\r\n";
                    OutputStream outputStream = client.getOutputStream();
                    outputStream.write(request1.getBytes(StandardCharsets.UTF_8));
                    outputStream.write(content);
                    outputStream.flush();
                    InputStream inputStream = client.getInputStream();
                    HttpTester.Response response1 = HttpTester.parseResponse(HttpTester.from(inputStream));
                    Assertions.assertNotNull(response1);
                    MatcherAssert.assertThat(response1.getStatus(), Matchers.equalTo(OK_200));
                    String request2 = "" + ((("GET / HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n");
                    outputStream.write(request2.getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    HttpTester.Response response2 = HttpTester.parseResponse(HttpTester.from(inputStream));
                    Assertions.assertNotNull(response2);
                    MatcherAssert.assertThat(response2.getStatus(), Matchers.equalTo(OK_200));
                }
            } 
            Assertions.assertEquals(0, reloads.get());
        } finally {
            scheduler.stop();
        }
    }

    private static class EchoHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            if (POST.is(request.getMethod()))
                IO.copy(request.getInputStream(), response.getOutputStream());
            else
                response.setContentLength(0);

        }
    }
}

