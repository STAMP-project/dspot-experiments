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
import java.io.InputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector.LocalEndPoint;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.function.Executable;


public class GracefulStopTest {
    /**
     * Test of standard graceful timeout mechanism when a block request does
     * not complete
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testGracefulNoWaiter() throws Exception {
        Server server = new Server();
        server.setStopTimeout(1000);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);
        GracefulStopTest.TestHandler handler = new GracefulStopTest.TestHandler();
        server.setHandler(handler);
        server.start();
        final int port = connector.getLocalPort();
        Socket client = new Socket("127.0.0.1", port);
        client.getOutputStream().write(((((((("POST / HTTP/1.0\r\n" + "Host: localhost:") + port) + "\r\n") + "Content-Type: plain/text\r\n") + "Content-Length: 10\r\n") + "\r\n") + "12345").getBytes());
        client.getOutputStream().flush();
        handler.latch.await();
        long start = System.nanoTime();
        server.stop();
        long stop = System.nanoTime();
        // No Graceful waiters
        MatcherAssert.assertThat(TimeUnit.NANOSECONDS.toMillis((stop - start)), Matchers.lessThan(900L));
        MatcherAssert.assertThat(client.getInputStream().read(), Matchers.is((-1)));
        MatcherAssert.assertThat(handler.handling.get(), Matchers.is(false));
        MatcherAssert.assertThat(handler.thrown.get(), Matchers.notNullValue());
        client.close();
    }

    /**
     * Test of standard graceful timeout mechanism when a block request does
     * not complete
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testGracefulTimeout() throws Exception {
        Server server = new Server();
        server.setStopTimeout(1000);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);
        GracefulStopTest.TestHandler handler = new GracefulStopTest.TestHandler();
        StatisticsHandler stats = new StatisticsHandler();
        server.setHandler(stats);
        stats.setHandler(handler);
        server.start();
        final int port = connector.getLocalPort();
        Socket client = new Socket("127.0.0.1", port);
        client.getOutputStream().write(((((((("POST / HTTP/1.0\r\n" + "Host: localhost:") + port) + "\r\n") + "Content-Type: plain/text\r\n") + "Content-Length: 10\r\n") + "\r\n") + "12345").getBytes());
        client.getOutputStream().flush();
        handler.latch.await();
        long start = System.nanoTime();
        Assertions.assertThrows(TimeoutException.class, () -> server.stop());
        long stop = System.nanoTime();
        // No Graceful waiters
        MatcherAssert.assertThat(TimeUnit.NANOSECONDS.toMillis((stop - start)), Matchers.greaterThan(900L));
        MatcherAssert.assertThat(client.getInputStream().read(), Matchers.is((-1)));
        MatcherAssert.assertThat(handler.handling.get(), Matchers.is(false));
        MatcherAssert.assertThat(handler.thrown.get(), Matchers.instanceOf(ClosedChannelException.class));
        client.close();
    }

    /**
     * Test of standard graceful timeout mechanism when a block request does
     * complete. Note that even though the request completes after 100ms, the
     * stop always takes 1000ms
     *
     * @throws Exception
     * 		on test failure
     */
    // TODO: needs more investigation
    @Test
    @DisabledOnOs(OS.WINDOWS)
    public void testGracefulComplete() throws Exception {
        Server server = new Server();
        server.setStopTimeout(10000);
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(0);
        server.addConnector(connector);
        GracefulStopTest.TestHandler handler = new GracefulStopTest.TestHandler();
        StatisticsHandler stats = new StatisticsHandler();
        server.setHandler(stats);
        stats.setHandler(handler);
        server.start();
        final int port = connector.getLocalPort();
        try (final Socket client1 = new Socket("127.0.0.1", port);final Socket client2 = new Socket("127.0.0.1", port)) {
            client1.getOutputStream().write(((((((("POST / HTTP/1.0\r\n" + "Host: localhost:") + port) + "\r\n") + "Content-Type: plain/text\r\n") + "Content-Length: 10\r\n") + "\r\n") + "12345").getBytes());
            client1.getOutputStream().flush();
            handler.latch.await();
            new Thread() {
                @Override
                public void run() {
                    long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                    long end = now + 500;
                    try {
                        Thread.sleep(100);
                        // Try creating a new connection
                        try {
                            try (Socket s = new Socket("127.0.0.1", port)) {
                            }
                            throw new IllegalStateException();
                        } catch (ConnectException e) {
                        }
                        // Try another request on existing connection
                        client2.getOutputStream().write((((("GET / HTTP/1.0\r\n" + "Host: localhost:") + port) + "\r\n") + "\r\n").getBytes());
                        client2.getOutputStream().flush();
                        String response2 = IO.toString(client2.getInputStream());
                        MatcherAssert.assertThat(response2, Matchers.containsString(" 503 "));
                        now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
                        Thread.sleep(Math.max(1, (end - now)));
                        client1.getOutputStream().write("567890".getBytes());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            long start = System.nanoTime();
            server.stop();
            long stop = System.nanoTime();
            MatcherAssert.assertThat(TimeUnit.NANOSECONDS.toMillis((stop - start)), Matchers.greaterThan(490L));
            MatcherAssert.assertThat(TimeUnit.NANOSECONDS.toMillis((stop - start)), Matchers.lessThan(10000L));
            String response = IO.toString(client1.getInputStream());
            MatcherAssert.assertThat(handler.handling.get(), Matchers.is(false));
            MatcherAssert.assertThat(response, Matchers.containsString(" 200 OK"));
            MatcherAssert.assertThat(response, Matchers.containsString("read 10/10"));
            MatcherAssert.assertThat(stats.getRequests(), Matchers.is(2));
            MatcherAssert.assertThat(stats.getResponses5xx(), Matchers.is(1));
        }
    }

    /**
     * Test of non graceful stop when a connection close is slow
     *
     * @throws Exception
     * 		on test failure
     */
    @Test
    public void testSlowCloseNotGraceful() throws Exception {
        Log.getLogger(QueuedThreadPool.class).info("Expect some threads can't be stopped");
        testSlowClose(0, 5000, Matchers.lessThan(750L));
    }

    @Test
    public void testResponsesAreClosed() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        StatisticsHandler stats = new StatisticsHandler();
        server.setHandler(stats);
        ContextHandler context = new ContextHandler(stats, "/");
        Exchanger<Void> exchanger0 = new Exchanger<>();
        Exchanger<Void> exchanger1 = new Exchanger<>();
        context.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                response.setStatus(200);
                response.setContentLength(13);
                response.flushBuffer();
                try {
                    exchanger0.exchange(null);
                    exchanger1.exchange(null);
                } catch (Throwable x) {
                    throw new ServletException(x);
                }
                response.getOutputStream().print("The Response\n");
            }
        });
        server.setStopTimeout(1000);
        server.start();
        LocalEndPoint endp = connector.executeRequest("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n");
        exchanger0.exchange(null);
        exchanger1.exchange(null);
        String response = endp.getResponse();
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        endp.addInputAndExecute(BufferUtil.toBuffer("GET / HTTP/1.1\r\nHost: localhost\r\n\r\n"));
        exchanger0.exchange(null);
        server.getConnectors()[0].shutdown().get();
        // Check completed 200 does not have close
        exchanger1.exchange(null);
        response = endp.getResponse();
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Connection: close")));
        // But endpoint is still closes soon after
        long end = (System.nanoTime()) + (TimeUnit.SECONDS.toNanos(1));
        while ((endp.isOpen()) && ((System.nanoTime()) < end))
            Thread.sleep(10);

        Assertions.assertFalse(endp.isOpen());
    }

    @Test
    public void testCommittedResponsesAreClosed() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        StatisticsHandler stats = new StatisticsHandler();
        server.setHandler(stats);
        ContextHandler context = new ContextHandler(stats, "/");
        Exchanger<Void> exchanger0 = new Exchanger<>();
        Exchanger<Void> exchanger1 = new Exchanger<>();
        context.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                try {
                    exchanger0.exchange(null);
                    exchanger1.exchange(null);
                } catch (Throwable x) {
                    throw new ServletException(x);
                }
                baseRequest.setHandled(true);
                response.setStatus(200);
                response.getWriter().println("The Response");
                response.getWriter().close();
            }
        });
        server.setStopTimeout(1000);
        server.start();
        LocalEndPoint endp = connector.executeRequest(("GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n")));
        exchanger0.exchange(null);
        exchanger1.exchange(null);
        String response = endp.getResponse();
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Connection: close")));
        endp.addInputAndExecute(BufferUtil.toBuffer("GET / HTTP/1.1\r\nHost:localhost\r\n\r\n"));
        exchanger0.exchange(null);
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                server.stop();
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        while (server.isStarted())
            Thread.sleep(10);

        // Check new connections rejected!
        String unavailable = connector.getResponse("GET / HTTP/1.1\r\nHost:localhost\r\n\r\n");
        MatcherAssert.assertThat(unavailable, Matchers.containsString(" 503 Service Unavailable"));
        MatcherAssert.assertThat(unavailable, Matchers.containsString("Connection: close"));
        // Check completed 200 has close
        exchanger1.exchange(null);
        response = endp.getResponse();
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(response, Matchers.containsString("Connection: close"));
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testContextStop() throws Exception {
        Server server = new Server();
        LocalConnector connector = new LocalConnector(server);
        server.addConnector(connector);
        ContextHandler context = new ContextHandler(server, "/");
        StatisticsHandler stats = new StatisticsHandler();
        context.setHandler(stats);
        Exchanger<Void> exchanger0 = new Exchanger<>();
        Exchanger<Void> exchanger1 = new Exchanger<>();
        stats.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                try {
                    exchanger0.exchange(null);
                    exchanger1.exchange(null);
                } catch (Throwable x) {
                    throw new ServletException(x);
                }
                baseRequest.setHandled(true);
                response.setStatus(200);
                response.getWriter().println("The Response");
                response.getWriter().close();
            }
        });
        context.setStopTimeout(1000);
        server.start();
        LocalEndPoint endp = connector.executeRequest(("GET / HTTP/1.1\r\n" + ("Host: localhost\r\n" + "\r\n")));
        exchanger0.exchange(null);
        exchanger1.exchange(null);
        String response = endp.getResponse();
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Connection: close")));
        endp.addInputAndExecute(BufferUtil.toBuffer("GET / HTTP/1.1\r\nHost:localhost\r\n\r\n"));
        exchanger0.exchange(null);
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            try {
                context.stop();
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        while (context.isStarted())
            Thread.sleep(10);

        // Check new connections accepted, but don't find context!
        String unavailable = connector.getResponse("GET / HTTP/1.1\r\nHost:localhost\r\n\r\n");
        MatcherAssert.assertThat(unavailable, Matchers.containsString(" 404 Not Found"));
        // Check completed 200 does not have close
        exchanger1.exchange(null);
        response = endp.getResponse();
        MatcherAssert.assertThat(response, Matchers.containsString("200 OK"));
        MatcherAssert.assertThat(response, Matchers.not(Matchers.containsString("Connection: close")));
        Assertions.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    static class NoopHandler extends AbstractHandler {
        final CountDownLatch latch = new CountDownLatch(1);

        NoopHandler() {
        }

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            baseRequest.setHandled(true);
            latch.countDown();
        }
    }

    static class TestHandler extends AbstractHandler {
        final CountDownLatch latch = new CountDownLatch(1);

        final AtomicReference<Throwable> thrown = new AtomicReference<Throwable>();

        final AtomicBoolean handling = new AtomicBoolean(false);

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            handling.set(true);
            latch.countDown();
            int c = 0;
            try {
                int content_length = request.getContentLength();
                InputStream in = request.getInputStream();
                while (true) {
                    if ((in.read()) < 0)
                        break;

                    c++;
                } 
                baseRequest.setHandled(true);
                response.setStatus(200);
                response.getWriter().printf("read %d/%d%n", c, content_length);
            } catch (Throwable th) {
                thrown.set(th);
            } finally {
                handling.set(false);
            }
        }
    }
}

