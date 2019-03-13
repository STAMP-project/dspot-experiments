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


import HttpCompliance.RFC7230_LEGACY;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.ManagedSelector;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.thread.Scheduler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Extended Server Tester.
 */
public class ExtendedServerTest extends HttpServerTestBase {
    private static class ExtendedEndPoint extends SocketChannelEndPoint {
        private volatile long _lastSelected;

        public ExtendedEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler) {
            super(channel, selector, key, scheduler);
        }

        public ExtendedEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler) {
            super(channel, selector, key, scheduler);
        }

        @Override
        public Runnable onSelected() {
            _lastSelected = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            return super.onSelected();
        }

        long getLastSelected() {
            return _lastSelected;
        }
    }

    private static class ExtendedHttpConnection extends HttpConnection {
        public ExtendedHttpConnection(HttpConfiguration config, Connector connector, EndPoint endPoint) {
            super(config, connector, endPoint, RFC7230_LEGACY, false);
        }

        @Override
        protected HttpChannelOverHttp newHttpChannel() {
            return new HttpChannelOverHttp(this, getConnector(), getHttpConfiguration(), getEndPoint(), this) {
                @Override
                public boolean startRequest(String method, String uri, HttpVersion version) {
                    getRequest().setAttribute("DispatchedAt", ((ExtendedServerTest.ExtendedEndPoint) (getEndPoint())).getLastSelected());
                    return super.startRequest(method, uri, version);
                }
            };
        }
    }

    @Test
    public void testExtended() throws Exception {
        configureServer(new ExtendedServerTest.DispatchedAtHandler());
        try (Socket client = newSocket(_serverURI.getHost(), _serverURI.getPort())) {
            OutputStream os = client.getOutputStream();
            long start = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            os.write("GET / HTTP/1.0\r\n".getBytes(StandardCharsets.ISO_8859_1));
            os.flush();
            Thread.sleep(200);
            long end = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            os.write("\r\n".getBytes(StandardCharsets.ISO_8859_1));
            // Read the response.
            String response = HttpServerTestBase.readResponse(client);
            MatcherAssert.assertThat(response, Matchers.containsString("HTTP/1.1 200 OK"));
            MatcherAssert.assertThat(response, Matchers.containsString("DispatchedAt="));
            String s = response.substring(((response.indexOf("DispatchedAt=")) + 13));
            s = s.substring(0, s.indexOf('\n'));
            long dispatched = Long.parseLong(s);
            MatcherAssert.assertThat(dispatched, Matchers.greaterThanOrEqualTo(start));
            MatcherAssert.assertThat(dispatched, Matchers.lessThan(end));
        }
    }

    protected static class DispatchedAtHandler extends AbstractHandler {
        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setStatus(200);
            response.getOutputStream().print((("DispatchedAt=" + (request.getAttribute("DispatchedAt"))) + "\r\n"));
        }
    }
}

