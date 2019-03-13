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


import HttpServletResponse.SC_OK;
import HttpTester.Input;
import HttpTester.Response;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector.LocalEndPoint;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class NotAcceptingTest {
    private final long IDLE_TIMEOUT = 2000;

    Server server;

    LocalConnector localConnector;

    ServerConnector blockingConnector;

    ServerConnector asyncConnector;

    @Test
    public void testServerConnectorBlockingAccept() throws Exception {
        NotAcceptingTest.TestHandler handler = new NotAcceptingTest.TestHandler();
        server.setHandler(handler);
        server.start();
        try (Socket client0 = new Socket("localhost", blockingConnector.getLocalPort())) {
            HttpTester.Input in0 = HttpTester.from(client0.getInputStream());
            client0.getOutputStream().write("GET /one HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
            String uri = handler.exchange.exchange("data");
            MatcherAssert.assertThat(uri, Matchers.is("/one"));
            HttpTester.Response response = HttpTester.parseResponse(in0);
            MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
            MatcherAssert.assertThat(response.getContent(), Matchers.is("data"));
            blockingConnector.setAccepting(false);
            // 0th connection still working
            client0.getOutputStream().write("GET /two HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
            uri = handler.exchange.exchange("more data");
            MatcherAssert.assertThat(uri, Matchers.is("/two"));
            response = HttpTester.parseResponse(in0);
            MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
            MatcherAssert.assertThat(response.getContent(), Matchers.is("more data"));
            try (Socket client1 = new Socket("localhost", blockingConnector.getLocalPort())) {
                // can't stop next connection being accepted
                HttpTester.Input in1 = HttpTester.from(client1.getInputStream());
                client1.getOutputStream().write("GET /three HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
                uri = handler.exchange.exchange("new connection");
                MatcherAssert.assertThat(uri, Matchers.is("/three"));
                response = HttpTester.parseResponse(in1);
                MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                MatcherAssert.assertThat(response.getContent(), Matchers.is("new connection"));
                try (Socket client2 = new Socket("localhost", blockingConnector.getLocalPort())) {
                    HttpTester.Input in2 = HttpTester.from(client2.getInputStream());
                    client2.getOutputStream().write("GET /four HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
                    try {
                        uri = handler.exchange.exchange("delayed connection", IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
                        Assertions.fail(("Failed near URI: " + uri));// this displays last URI, not current (obviously)

                    } catch (TimeoutException e) {
                        // Can we accept the original?
                        blockingConnector.setAccepting(true);
                        uri = handler.exchange.exchange("delayed connection");
                        MatcherAssert.assertThat(uri, Matchers.is("/four"));
                        response = HttpTester.parseResponse(in2);
                        MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                        MatcherAssert.assertThat(response.getContent(), Matchers.is("delayed connection"));
                    }
                }
            }
        }
    }

    @Test
    public void testServerConnectorAsyncAccept() throws Exception {
        NotAcceptingTest.TestHandler handler = new NotAcceptingTest.TestHandler();
        server.setHandler(handler);
        server.start();
        try (Socket client0 = new Socket("localhost", asyncConnector.getLocalPort())) {
            HttpTester.Input in0 = HttpTester.from(client0.getInputStream());
            client0.getOutputStream().write("GET /one HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
            String uri = handler.exchange.exchange("data");
            MatcherAssert.assertThat(uri, Matchers.is("/one"));
            HttpTester.Response response = HttpTester.parseResponse(in0);
            MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
            MatcherAssert.assertThat(response.getContent(), Matchers.is("data"));
            asyncConnector.setAccepting(false);
            // 0th connection still working
            client0.getOutputStream().write("GET /two HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
            uri = handler.exchange.exchange("more data");
            MatcherAssert.assertThat(uri, Matchers.is("/two"));
            response = HttpTester.parseResponse(in0);
            MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
            MatcherAssert.assertThat(response.getContent(), Matchers.is("more data"));
            try (Socket client1 = new Socket("localhost", asyncConnector.getLocalPort())) {
                HttpTester.Input in1 = HttpTester.from(client1.getInputStream());
                client1.getOutputStream().write("GET /three HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
                try {
                    uri = handler.exchange.exchange("delayed connection", IDLE_TIMEOUT, TimeUnit.MILLISECONDS);
                    Assertions.fail(uri);
                } catch (TimeoutException e) {
                    // Can we accept the original?
                    asyncConnector.setAccepting(true);
                    uri = handler.exchange.exchange("delayed connection");
                    MatcherAssert.assertThat(uri, Matchers.is("/three"));
                    response = HttpTester.parseResponse(in1);
                    MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                    MatcherAssert.assertThat(response.getContent(), Matchers.is("delayed connection"));
                }
            }
        }
    }

    public static class TestHandler extends AbstractHandler {
        final Exchanger<String> exchange = new Exchanger<>();

        transient int handled;

        public TestHandler() {
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                String content = exchange.exchange(baseRequest.getRequestURI());
                baseRequest.setHandled(true);
                (handled)++;
                response.setContentType("text/html;charset=utf-8");
                response.setStatus(SC_OK);
                response.getWriter().print(content);
            } catch (InterruptedException e) {
                throw new ServletException(e);
            }
        }

        public int getHandled() {
            return handled;
        }
    }

    @Test
    public void testAcceptRateLimit() throws Exception {
        AcceptRateLimit limit = new AcceptRateLimit(4, 1, TimeUnit.HOURS, server);
        server.addBean(limit);
        server.setHandler(new NotAcceptingTest.HelloHandler());
        server.start();
        try (Socket async0 = new Socket("localhost", asyncConnector.getLocalPort());Socket async1 = new Socket("localhost", asyncConnector.getLocalPort());Socket async2 = new Socket("localhost", asyncConnector.getLocalPort())) {
            String expectedContent = "Hello" + (System.lineSeparator());
            for (Socket client : new Socket[]{ async2 }) {
                HttpTester.Input in = HttpTester.from(client.getInputStream());
                client.getOutputStream().write("GET /test HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
                HttpTester.Response response = HttpTester.parseResponse(in);
                MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                MatcherAssert.assertThat(response.getContent(), Matchers.is(expectedContent));
            }
            MatcherAssert.assertThat(localConnector.isAccepting(), Matchers.is(true));
            MatcherAssert.assertThat(blockingConnector.isAccepting(), Matchers.is(true));
            MatcherAssert.assertThat(asyncConnector.isAccepting(), Matchers.is(true));
        }
        limit.age(45, TimeUnit.MINUTES);
        try (Socket async0 = new Socket("localhost", asyncConnector.getLocalPort());Socket async1 = new Socket("localhost", asyncConnector.getLocalPort())) {
            String expectedContent = "Hello" + (System.lineSeparator());
            for (Socket client : new Socket[]{ async1 }) {
                HttpTester.Input in = HttpTester.from(client.getInputStream());
                client.getOutputStream().write("GET /test HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
                HttpTester.Response response = HttpTester.parseResponse(in);
                MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                MatcherAssert.assertThat(response.getContent(), Matchers.is(expectedContent));
            }
            MatcherAssert.assertThat(localConnector.isAccepting(), Matchers.is(false));
            MatcherAssert.assertThat(blockingConnector.isAccepting(), Matchers.is(false));
            MatcherAssert.assertThat(asyncConnector.isAccepting(), Matchers.is(false));
        }
        limit.age(45, TimeUnit.MINUTES);
        MatcherAssert.assertThat(localConnector.isAccepting(), Matchers.is(false));
        MatcherAssert.assertThat(blockingConnector.isAccepting(), Matchers.is(false));
        MatcherAssert.assertThat(asyncConnector.isAccepting(), Matchers.is(false));
        limit.run();
        MatcherAssert.assertThat(localConnector.isAccepting(), Matchers.is(true));
        MatcherAssert.assertThat(blockingConnector.isAccepting(), Matchers.is(true));
        MatcherAssert.assertThat(asyncConnector.isAccepting(), Matchers.is(true));
    }

    @Test
    public void testConnectionLimit() throws Exception {
        server.addBean(new ConnectionLimit(9, server));
        server.setHandler(new NotAcceptingTest.HelloHandler());
        server.start();
        Log.getLogger(ConnectionLimit.class).debug("CONNECT:");
        try (LocalEndPoint local0 = localConnector.connect();LocalEndPoint local1 = localConnector.connect();LocalEndPoint local2 = localConnector.connect();Socket blocking0 = new Socket("localhost", blockingConnector.getLocalPort());Socket blocking1 = new Socket("localhost", blockingConnector.getLocalPort());Socket blocking2 = new Socket("localhost", blockingConnector.getLocalPort());Socket async0 = new Socket("localhost", asyncConnector.getLocalPort());Socket async1 = new Socket("localhost", asyncConnector.getLocalPort());Socket async2 = new Socket("localhost", asyncConnector.getLocalPort())) {
            String expectedContent = "Hello" + (System.lineSeparator());
            Log.getLogger(ConnectionLimit.class).debug("LOCAL:");
            for (LocalEndPoint client : new LocalEndPoint[]{ local0, local1, local2 }) {
                client.addInputAndExecute(BufferUtil.toBuffer("GET /test HTTP/1.1\r\nHost:localhost\r\n\r\n"));
                HttpTester.Response response = HttpTester.parseResponse(client.getResponse());
                MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                MatcherAssert.assertThat(response.getContent(), Matchers.is(expectedContent));
            }
            Log.getLogger(ConnectionLimit.class).debug("NETWORK:");
            for (Socket client : new Socket[]{ blocking0, blocking1, blocking2, async0, async1, async2 }) {
                HttpTester.Input in = HttpTester.from(client.getInputStream());
                client.getOutputStream().write("GET /test HTTP/1.1\r\nHost:localhost\r\n\r\n".getBytes());
                HttpTester.Response response = HttpTester.parseResponse(in);
                MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                MatcherAssert.assertThat(response.getContent(), Matchers.is(expectedContent));
            }
            MatcherAssert.assertThat(localConnector.isAccepting(), Matchers.is(false));
            MatcherAssert.assertThat(blockingConnector.isAccepting(), Matchers.is(false));
            MatcherAssert.assertThat(asyncConnector.isAccepting(), Matchers.is(false));
            {
                // Close a async connection
                HttpTester.Input in = HttpTester.from(async1.getInputStream());
                async1.getOutputStream().write("GET /test HTTP/1.1\r\nHost:localhost\r\nConnection: close\r\n\r\n".getBytes());
                HttpTester.Response response = HttpTester.parseResponse(in);
                MatcherAssert.assertThat(response.getStatus(), Matchers.is(200));
                MatcherAssert.assertThat(response.getContent(), Matchers.is(expectedContent));
            }
        }
        NotAcceptingTest.waitFor(localConnector::isAccepting, Matchers.is(true), (2 * (IDLE_TIMEOUT)), TimeUnit.MILLISECONDS);
        NotAcceptingTest.waitFor(blockingConnector::isAccepting, Matchers.is(true), (2 * (IDLE_TIMEOUT)), TimeUnit.MILLISECONDS);
        NotAcceptingTest.waitFor(asyncConnector::isAccepting, Matchers.is(true), (2 * (IDLE_TIMEOUT)), TimeUnit.MILLISECONDS);
    }

    public static class HelloHandler extends AbstractHandler {
        public HelloHandler() {
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(SC_OK);
            response.getWriter().println("Hello");
        }
    }
}

