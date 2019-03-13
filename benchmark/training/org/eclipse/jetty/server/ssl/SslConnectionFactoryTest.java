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


import HttpVersion.HTTP_1_1;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SocketCustomizationListener;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


public class SslConnectionFactoryTest {
    private Server _server;

    private ServerConnector _connector;

    private int _port;

    @Test
    public void testConnect() throws Exception {
        String response = getResponse("127.0.0.1", null);
        MatcherAssert.assertThat(response, Matchers.containsString("host=127.0.0.1"));
    }

    @Test
    public void testSNIConnect() throws Exception {
        String response = getResponse("localhost", "localhost", "jetty.eclipse.org");
        MatcherAssert.assertThat(response, Matchers.containsString("host=localhost"));
    }

    @Test
    public void testBadHandshake() throws Exception {
        try (Socket socket = new Socket("127.0.0.1", _port);OutputStream out = socket.getOutputStream()) {
            out.write("Rubbish".getBytes());
            out.flush();
            socket.setSoTimeout(1000);
            // Expect TLS message type == 21: Alert
            MatcherAssert.assertThat(socket.getInputStream().read(), Matchers.equalTo(21));
        }
    }

    @Test
    public void testSocketCustomization() throws Exception {
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
        MatcherAssert.assertThat(response, Matchers.containsString("host=127.0.0.1"));
        Assertions.assertEquals("customize connector class org.eclipse.jetty.io.ssl.SslConnection,false", history.poll());
        Assertions.assertEquals("customize ssl class org.eclipse.jetty.io.ssl.SslConnection,false", history.poll());
        Assertions.assertEquals("customize connector class org.eclipse.jetty.server.HttpConnection,true", history.poll());
        Assertions.assertEquals("customize http class org.eclipse.jetty.server.HttpConnection,true", history.poll());
        Assertions.assertEquals(0, history.size());
    }

    @Test
    public void testServerWithoutHttpConnectionFactory() throws Exception {
        _server.stop();
        Assertions.assertNotNull(_connector.removeConnectionFactory(HTTP_1_1.asString()));
        Assertions.assertThrows(IllegalStateException.class, () -> _server.start());
    }
}

