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
package org.eclipse.jetty.client;


import HttpClientTransport.HTTP_CONNECTION_PROMISE_CONTEXT_KEY;
import HttpClientTransport.HTTP_DESTINATION_CONTEXT_KEY;
import HttpServletResponse.SC_NOT_ACCEPTABLE;
import Origin.Address;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.api.Connection;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.io.AbstractConnection;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.AbstractConnectionFactory;
import org.eclipse.jetty.server.ConnectionFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.Promise;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class HttpClientCustomProxyTest {
    public static final byte[] CAFE_BABE = new byte[]{ ((byte) (202)), ((byte) (254)), ((byte) (186)), ((byte) (190)) };

    private Server server;

    private ServerConnector connector;

    private HttpClient client;

    @Test
    public void testCustomProxy() throws Exception {
        final String serverHost = "server";
        final int status = HttpStatus.NO_CONTENT_204;
        prepare(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                if (serverHost.equals(request.getServerName()))
                    response.setStatus(status);
                else
                    response.setStatus(SC_NOT_ACCEPTABLE);

            }
        });
        // Setup the custom proxy
        int proxyPort = connector.getLocalPort();
        int serverPort = proxyPort + 1;// Any port will do for these tests - just not the same as the proxy

        client.getProxyConfiguration().getProxies().add(new HttpClientCustomProxyTest.CAFEBABEProxy(new Origin.Address("localhost", proxyPort), false));
        ContentResponse response = client.newRequest(serverHost, serverPort).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(status, response.getStatus());
    }

    private class CAFEBABEProxy extends ProxyConfiguration.Proxy {
        private CAFEBABEProxy(Origin.Address address, boolean secure) {
            super(address, secure);
        }

        @Override
        public ClientConnectionFactory newClientConnectionFactory(ClientConnectionFactory connectionFactory) {
            return new HttpClientCustomProxyTest.CAFEBABEClientConnectionFactory(connectionFactory);
        }
    }

    private class CAFEBABEClientConnectionFactory implements ClientConnectionFactory {
        private final ClientConnectionFactory connectionFactory;

        private CAFEBABEClientConnectionFactory(ClientConnectionFactory connectionFactory) {
            this.connectionFactory = connectionFactory;
        }

        @Override
        public Connection newConnection(EndPoint endPoint, Map<String, Object> context) throws IOException {
            HttpDestination destination = ((HttpDestination) (context.get(HTTP_DESTINATION_CONTEXT_KEY)));
            Executor executor = destination.getHttpClient().getExecutor();
            HttpClientCustomProxyTest.CAFEBABEConnection connection = new HttpClientCustomProxyTest.CAFEBABEConnection(endPoint, executor, connectionFactory, context);
            return customize(connection, context);
        }
    }

    private class CAFEBABEConnection extends AbstractConnection implements Callback {
        private final ClientConnectionFactory connectionFactory;

        private final Map<String, Object> context;

        public CAFEBABEConnection(EndPoint endPoint, Executor executor, ClientConnectionFactory connectionFactory, Map<String, Object> context) {
            super(endPoint, executor);
            this.connectionFactory = connectionFactory;
            this.context = context;
        }

        @Override
        public void onOpen() {
            super.onOpen();
            getEndPoint().write(this, ByteBuffer.wrap(HttpClientCustomProxyTest.CAFE_BABE));
        }

        @Override
        public void succeeded() {
            fillInterested();
        }

        @Override
        public void failed(Throwable x) {
            close();
        }

        @Override
        public void onFillable() {
            try {
                ByteBuffer buffer = BufferUtil.allocate(4);
                int filled = getEndPoint().fill(buffer);
                Assertions.assertEquals(4, filled);
                Assertions.assertArrayEquals(HttpClientCustomProxyTest.CAFE_BABE, buffer.array());
                // We are good, upgrade the connection
                getEndPoint().upgrade(connectionFactory.newConnection(getEndPoint(), context));
            } catch (Throwable x) {
                close();
                @SuppressWarnings("unchecked")
                Promise<Connection> promise = ((Promise<Connection>) (context.get(HTTP_CONNECTION_PROMISE_CONTEXT_KEY)));
                promise.failed(x);
            }
        }
    }

    private class CAFEBABEServerConnectionFactory extends AbstractConnectionFactory {
        private final ConnectionFactory connectionFactory;

        private CAFEBABEServerConnectionFactory(ConnectionFactory connectionFactory) {
            super("cafebabe");
            this.connectionFactory = connectionFactory;
        }

        @Override
        public Connection newConnection(Connector connector, EndPoint endPoint) {
            return new HttpClientCustomProxyTest.CAFEBABEServerConnection(connector, endPoint, connectionFactory);
        }
    }

    private class CAFEBABEServerConnection extends AbstractConnection implements Callback {
        private final ConnectionFactory connectionFactory;

        public CAFEBABEServerConnection(Connector connector, EndPoint endPoint, ConnectionFactory connectionFactory) {
            super(endPoint, connector.getExecutor());
            this.connectionFactory = connectionFactory;
        }

        @Override
        public void onOpen() {
            super.onOpen();
            fillInterested();
        }

        @Override
        public void onFillable() {
            try {
                ByteBuffer buffer = BufferUtil.allocate(4);
                int filled = getEndPoint().fill(buffer);
                Assertions.assertEquals(4, filled);
                Assertions.assertArrayEquals(HttpClientCustomProxyTest.CAFE_BABE, buffer.array());
                getEndPoint().write(this, buffer);
            } catch (Throwable x) {
                close();
            }
        }

        @Override
        public void succeeded() {
            // We are good, upgrade the connection
            getEndPoint().upgrade(connectionFactory.newConnection(connector, getEndPoint()));
        }

        @Override
        public void failed(Throwable x) {
            close();
        }
    }
}

