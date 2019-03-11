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


import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.channels.ServerSocketChannel;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.function.Executable;


public class ServerConnectorTest {
    public static class ReuseInfoHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            response.setContentType("text/plain");
            EndPoint endPoint = baseRequest.getHttpChannel().getEndPoint();
            MatcherAssert.assertThat("Endpoint", endPoint, Matchers.instanceOf(SocketChannelEndPoint.class));
            SocketChannelEndPoint channelEndPoint = ((SocketChannelEndPoint) (endPoint));
            Socket socket = channelEndPoint.getSocket();
            ServerConnector connector = ((ServerConnector) (baseRequest.getHttpChannel().getConnector()));
            PrintWriter out = response.getWriter();
            out.printf("connector.getReuseAddress() = %b%n", connector.getReuseAddress());
            try {
                Field fld = connector.getClass().getDeclaredField("_reuseAddress");
                MatcherAssert.assertThat("Field[_reuseAddress]", fld, Matchers.notNullValue());
                fld.setAccessible(true);
                Object val = fld.get(connector);
                out.printf("connector._reuseAddress() = %b%n", val);
            } catch (Throwable t) {
                t.printStackTrace(out);
            }
            out.printf("socket.getReuseAddress() = %b%n", socket.getReuseAddress());
            baseRequest.setHandled(true);
        }
    }

    @Test
    public void testReuseAddress_Default() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ServerConnectorTest.ReuseInfoHandler());
        handlers.addHandler(new DefaultHandler());
        server.setHandler(handlers);
        try {
            server.start();
            URI uri = toServerURI(connector);
            String response = getResponse(uri);
            MatcherAssert.assertThat("Response", response, Matchers.containsString("connector.getReuseAddress() = true"));
            MatcherAssert.assertThat("Response", response, Matchers.containsString("connector._reuseAddress() = true"));
            // Java on Windows is incapable of propagating reuse-address this to the opened socket.
            if (!(OS.WINDOWS.isCurrentOs())) {
                MatcherAssert.assertThat("Response", response, Matchers.containsString("socket.getReuseAddress() = true"));
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testReuseAddress_True() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        connector.setReuseAddress(true);
        server.addConnector(connector);
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ServerConnectorTest.ReuseInfoHandler());
        handlers.addHandler(new DefaultHandler());
        server.setHandler(handlers);
        try {
            server.start();
            URI uri = toServerURI(connector);
            String response = getResponse(uri);
            MatcherAssert.assertThat("Response", response, Matchers.containsString("connector.getReuseAddress() = true"));
            MatcherAssert.assertThat("Response", response, Matchers.containsString("connector._reuseAddress() = true"));
            // Java on Windows is incapable of propagating reuse-address this to the opened socket.
            if (!(OS.WINDOWS.isCurrentOs())) {
                MatcherAssert.assertThat("Response", response, Matchers.containsString("socket.getReuseAddress() = true"));
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testReuseAddress_False() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        connector.setReuseAddress(false);
        server.addConnector(connector);
        HandlerList handlers = new HandlerList();
        handlers.addHandler(new ServerConnectorTest.ReuseInfoHandler());
        handlers.addHandler(new DefaultHandler());
        server.setHandler(handlers);
        try {
            server.start();
            URI uri = toServerURI(connector);
            String response = getResponse(uri);
            MatcherAssert.assertThat("Response", response, Matchers.containsString("connector.getReuseAddress() = false"));
            MatcherAssert.assertThat("Response", response, Matchers.containsString("connector._reuseAddress() = false"));
            // Java on Windows is incapable of propagating reuse-address this to the opened socket.
            if (!(OS.WINDOWS.isCurrentOs())) {
                MatcherAssert.assertThat("Response", response, Matchers.containsString("socket.getReuseAddress() = false"));
            }
        } finally {
            server.stop();
        }
    }

    @Test
    public void testAddFirstConnectionFactory() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        server.addConnector(connector);
        HttpConnectionFactory http = new HttpConnectionFactory();
        connector.addConnectionFactory(http);
        ProxyConnectionFactory proxy = new ProxyConnectionFactory();
        connector.addFirstConnectionFactory(proxy);
        Collection<ConnectionFactory> factories = connector.getConnectionFactories();
        Assertions.assertEquals(2, factories.size());
        Assertions.assertSame(proxy, factories.iterator().next());
        Assertions.assertEquals(2, connector.getBeans(ConnectionFactory.class).size());
        Assertions.assertEquals(proxy.getProtocol(), connector.getDefaultProtocol());
    }

    @Test
    public void testExceptionWhileAccepting() throws Exception {
        Server server = new Server();
        try (StacklessLogging stackless = new StacklessLogging(AbstractConnector.class)) {
            AtomicLong spins = new AtomicLong();
            ServerConnector connector = new ServerConnector(server, 1, 1) {
                @Override
                public void accept(int acceptorID) throws IOException {
                    spins.incrementAndGet();
                    throw new IOException("explicitly_thrown_by_test");
                }
            };
            server.addConnector(connector);
            server.start();
            Thread.sleep(1500);
            MatcherAssert.assertThat(spins.get(), Matchers.lessThan(5L));
        } finally {
            server.stop();
        }
    }

    @Test
    public void testOpenWithServerSocketChannel() throws Exception {
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        server.addConnector(connector);
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.bind(new InetSocketAddress(0));
        Assertions.assertTrue(channel.isOpen());
        int port = channel.socket().getLocalPort();
        MatcherAssert.assertThat(port, Matchers.greaterThan(0));
        connector.open(channel);
        MatcherAssert.assertThat(connector.getLocalPort(), Matchers.is(port));
        server.start();
        MatcherAssert.assertThat(connector.getLocalPort(), Matchers.is(port));
        MatcherAssert.assertThat(connector.getTransport(), Matchers.is(channel));
        server.stop();
        MatcherAssert.assertThat(connector.getTransport(), Matchers.nullValue());
    }

    @Test
    public void testBindToAddressWhichIsInUse() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            final int port = socket.getLocalPort();
            Server server = new Server();
            ServerConnector connector = new ServerConnector(server);
            connector.setPort(port);
            server.addConnector(connector);
            HandlerList handlers = new HandlerList();
            handlers.addHandler(new DefaultHandler());
            server.setHandler(handlers);
            IOException x = Assertions.assertThrows(IOException.class, () -> server.start());
            MatcherAssert.assertThat(x.getCause(), Matchers.instanceOf(BindException.class));
            MatcherAssert.assertThat(x.getMessage(), Matchers.containsString(("0.0.0.0:" + port)));
        }
    }
}

