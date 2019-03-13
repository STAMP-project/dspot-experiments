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
package org.eclipse.jetty.client.ssl;


import HttpStatus.OK_200;
import java.security.cert.Certificate;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSession;
import org.eclipse.jetty.client.EmptyServerHandler;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.io.ssl.SslHandshakeListener;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * In order to work, client authentication needs a certificate
 * signed by a CA that also signed the server certificate.
 * <p>
 * For this test, the client certificate is signed with the server
 * certificate, and the server certificate is self-signed.
 */
public class NeedWantClientAuthTest {
    private Server server;

    private ServerConnector connector;

    private HttpClient client;

    @Test
    public void testWantClientAuthWithoutAuth() throws Exception {
        SslContextFactory serverSSL = createSslContextFactory();
        serverSSL.setWantClientAuth(true);
        startServer(serverSSL, new EmptyServerHandler());
        SslContextFactory clientSSL = new SslContextFactory(true);
        startClient(clientSSL);
        ContentResponse response = client.newRequest(("https://localhost:" + (connector.getLocalPort()))).timeout(10, TimeUnit.SECONDS).send();
        Assertions.assertEquals(OK_200, response.getStatus());
    }

    @Test
    public void testWantClientAuthWithAuth() throws Exception {
        SslContextFactory serverSSL = createSslContextFactory();
        serverSSL.setWantClientAuth(true);
        startServer(serverSSL, new EmptyServerHandler());
        CountDownLatch handshakeLatch = new CountDownLatch(1);
        connector.addBean(new SslHandshakeListener() {
            @Override
            public void handshakeSucceeded(Event event) {
                try {
                    SSLSession session = event.getSSLEngine().getSession();
                    Certificate[] clientCerts = session.getPeerCertificates();
                    Assertions.assertNotNull(clientCerts);
                    MatcherAssert.assertThat(clientCerts.length, Matchers.greaterThan(0));
                    handshakeLatch.countDown();
                } catch (Throwable x) {
                    x.printStackTrace();
                }
            }
        });
        SslContextFactory clientSSL = new SslContextFactory(true);
        clientSSL.setKeyStorePath("src/test/resources/client_keystore.jks");
        clientSSL.setKeyStorePassword("storepwd");
        startClient(clientSSL);
        ContentResponse response = client.newRequest(("https://localhost:" + (connector.getLocalPort()))).timeout(10, TimeUnit.SECONDS).send();
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertTrue(handshakeLatch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testNeedClientAuthWithoutAuth() throws Exception {
        // In TLS 1.2, the TLS handshake on the client finishes after the TLS handshake on the server.
        // The server detects the lack of the client certificate, fails its TLS handshake and sends
        // bad_certificate to the client, which then fails its own TLS handshake.
        // In TLS 1.3, the TLS handshake on the client finishes before the TLS handshake on the server.
        // The server still sends bad_certificate to the client, but the client handshake has already
        // completed successfully its TLS handshake.
        SslContextFactory serverSSL = createSslContextFactory();
        serverSSL.setNeedClientAuth(true);
        startServer(serverSSL, new EmptyServerHandler());
        SslContextFactory clientSSL = new SslContextFactory(true);
        startClient(clientSSL);
        CountDownLatch handshakeLatch = new CountDownLatch(1);
        client.addBean(new SslHandshakeListener() {
            @Override
            public void handshakeSucceeded(Event event) {
                if ("TLSv1.3".equals(event.getSSLEngine().getSession().getProtocol()))
                    handshakeLatch.countDown();

            }

            @Override
            public void handshakeFailed(Event event, Throwable failure) {
                MatcherAssert.assertThat(failure, Matchers.instanceOf(SSLHandshakeException.class));
                handshakeLatch.countDown();
            }
        });
        CountDownLatch latch = new CountDownLatch(1);
        client.newRequest(("https://localhost:" + (connector.getLocalPort()))).timeout(10, TimeUnit.SECONDS).send(( result) -> {
            if (result.isFailed()) {
                Throwable failure = result.getFailure();
                if (failure instanceof SSLException)
                    latch.countDown();

            }
        });
        Assertions.assertTrue(handshakeLatch.await(10, TimeUnit.SECONDS));
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testNeedClientAuthWithAuth() throws Exception {
        SslContextFactory serverSSL = createSslContextFactory();
        serverSSL.setNeedClientAuth(true);
        startServer(serverSSL, new EmptyServerHandler());
        CountDownLatch handshakeLatch = new CountDownLatch(1);
        connector.addBean(new SslHandshakeListener() {
            @Override
            public void handshakeSucceeded(Event event) {
                try {
                    SSLSession session = event.getSSLEngine().getSession();
                    Certificate[] clientCerts = session.getPeerCertificates();
                    Assertions.assertNotNull(clientCerts);
                    MatcherAssert.assertThat(clientCerts.length, Matchers.greaterThan(0));
                    handshakeLatch.countDown();
                } catch (Throwable x) {
                    x.printStackTrace();
                }
            }
        });
        SslContextFactory clientSSL = new SslContextFactory(true);
        clientSSL.setKeyStorePath("src/test/resources/client_keystore.jks");
        clientSSL.setKeyStorePassword("storepwd");
        startClient(clientSSL);
        ContentResponse response = client.newRequest(("https://localhost:" + (connector.getLocalPort()))).timeout(10, TimeUnit.SECONDS).send();
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertTrue(handshakeLatch.await(10, TimeUnit.SECONDS));
    }
}

