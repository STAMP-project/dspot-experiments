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


import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SocketCustomizationListener;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class SniSslConnectionFactoryTest {
    private Server _server;

    private ServerConnector _connector;

    private HttpConfiguration _https_config;

    private int _port;

    @Test
    public void testConnect() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        String response = getResponse("127.0.0.1", null);
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: 127.0.0.1"));
    }

    @Test
    public void testSNIConnectNoWild() throws Exception {
        start("src/test/resources/keystore_sni_nowild.p12");
        String response = getResponse("www.acme.org", null);
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: www.acme.org"));
        MatcherAssert.assertThat(response, Matchers.containsString("X-Cert: OU=default"));
        response = getResponse("www.example.com", null);
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: www.example.com"));
        MatcherAssert.assertThat(response, Matchers.containsString("X-Cert: OU=example"));
    }

    @Test
    public void testSNIConnect() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        String response = getResponse("jetty.eclipse.org", "jetty.eclipse.org");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: jetty.eclipse.org"));
        response = getResponse("www.example.com", "www.example.com");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: www.example.com"));
        response = getResponse("foo.domain.com", "*.domain.com");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: foo.domain.com"));
        response = getResponse("m.san.com", "san example");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: m.san.com"));
        response = getResponse("www.san.com", "san example");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: www.san.com"));
    }

    @Test
    public void testWildSNIConnect() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        String response = getResponse("domain.com", "www.domain.com", "*.domain.com");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: www.domain.com"));
        response = getResponse("domain.com", "domain.com", "*.domain.com");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: domain.com"));
        response = getResponse("www.domain.com", "www.domain.com", "*.domain.com");
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: www.domain.com"));
    }

    @Test
    public void testBadSNIConnect() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        String response = getResponse("www.example.com", "some.other.com", "www.example.com");
        MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 400 "));
        MatcherAssert.assertThat(response, Matchers.containsString("Host does not match SNI"));
    }

    @Test
    public void testSameConnectionRequestsForManyDomains() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        SslContextFactory clientContextFactory = new SslContextFactory(true);
        clientContextFactory.start();
        SSLSocketFactory factory = clientContextFactory.getSslContext().getSocketFactory();
        try (SSLSocket sslSocket = ((SSLSocket) (factory.createSocket("127.0.0.1", _port)))) {
            SNIHostName serverName = new SNIHostName("m.san.com");
            SSLParameters params = sslSocket.getSSLParameters();
            params.setServerNames(Collections.singletonList(serverName));
            sslSocket.setSSLParameters(params);
            sslSocket.startHandshake();
            // The first request binds the socket to an alias.
            String request = "" + (("GET /ctx/path HTTP/1.1\r\n" + "Host: m.san.com\r\n") + "\r\n");
            OutputStream output = sslSocket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            InputStream input = sslSocket.getInputStream();
            String response = response(input);
            Assertions.assertTrue(response.startsWith("HTTP/1.1 200 "));
            // Same socket, send a request for a different domain but same alias.
            request = "" + (("GET /ctx/path HTTP/1.1\r\n" + "Host: www.san.com\r\n") + "\r\n");
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            response = response(input);
            Assertions.assertTrue(response.startsWith("HTTP/1.1 200 "));
            // Same socket, send a request for a different domain but different alias.
            request = "" + (("GET /ctx/path HTTP/1.1\r\n" + "Host: www.example.com\r\n") + "\r\n");
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            response = response(input);
            MatcherAssert.assertThat(response, Matchers.startsWith("HTTP/1.1 400 "));
            MatcherAssert.assertThat(response, Matchers.containsString("Host does not match SNI"));
        } finally {
            clientContextFactory.stop();
        }
    }

    @Test
    public void testSameConnectionRequestsForManyWildDomains() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        SslContextFactory clientContextFactory = new SslContextFactory(true);
        clientContextFactory.start();
        SSLSocketFactory factory = clientContextFactory.getSslContext().getSocketFactory();
        try (SSLSocket sslSocket = ((SSLSocket) (factory.createSocket("127.0.0.1", _port)))) {
            SNIHostName serverName = new SNIHostName("www.domain.com");
            SSLParameters params = sslSocket.getSSLParameters();
            params.setServerNames(Collections.singletonList(serverName));
            sslSocket.setSSLParameters(params);
            sslSocket.startHandshake();
            String request = "" + (("GET /ctx/path HTTP/1.1\r\n" + "Host: www.domain.com\r\n") + "\r\n");
            OutputStream output = sslSocket.getOutputStream();
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            InputStream input = sslSocket.getInputStream();
            String response = response(input);
            Assertions.assertTrue(response.startsWith("HTTP/1.1 200 "));
            // Now, on the same socket, send a request for a different valid domain.
            request = "" + (("GET /ctx/path HTTP/1.1\r\n" + "Host: assets.domain.com\r\n") + "\r\n");
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            response = response(input);
            Assertions.assertTrue(response.startsWith("HTTP/1.1 200 "));
            // Now make a request for an invalid domain for this connection.
            request = "" + (("GET /ctx/path HTTP/1.1\r\n" + "Host: www.example.com\r\n") + "\r\n");
            output.write(request.getBytes(StandardCharsets.UTF_8));
            output.flush();
            response = response(input);
            Assertions.assertTrue(response.startsWith("HTTP/1.1 400 "));
            MatcherAssert.assertThat(response, Matchers.containsString("Host does not match SNI"));
        } finally {
            clientContextFactory.stop();
        }
    }

    @Test
    public void testSocketCustomization() throws Exception {
        start("src/test/resources/keystore_sni.p12");
        final Queue<String> history = new LinkedBlockingQueue<>();
        _connector.addBean(new SocketCustomizationListener() {
            @Override
            protected void customize(Socket socket, Class<? extends Connection> connection, boolean ssl) {
                history.add(((("customize connector " + connection) + ",") + ssl));
            }
        });
        _connector.getBean(SslConnectionFactory.class).addBean(new SocketCustomizationListener() {
            @Override
            protected void customize(Socket socket, Class<? extends Connection> connection, boolean ssl) {
                history.add(((("customize ssl " + connection) + ",") + ssl));
            }
        });
        _connector.getBean(HttpConnectionFactory.class).addBean(new SocketCustomizationListener() {
            @Override
            protected void customize(Socket socket, Class<? extends Connection> connection, boolean ssl) {
                history.add(((("customize http " + connection) + ",") + ssl));
            }
        });
        String response = getResponse("127.0.0.1", null);
        MatcherAssert.assertThat(response, Matchers.containsString("X-HOST: 127.0.0.1"));
        Assertions.assertEquals("customize connector class org.eclipse.jetty.io.ssl.SslConnection,false", history.poll());
        Assertions.assertEquals("customize ssl class org.eclipse.jetty.io.ssl.SslConnection,false", history.poll());
        Assertions.assertEquals("customize connector class org.eclipse.jetty.server.HttpConnection,true", history.poll());
        Assertions.assertEquals("customize http class org.eclipse.jetty.server.HttpConnection,true", history.poll());
        Assertions.assertEquals(0, history.size());
    }
}

