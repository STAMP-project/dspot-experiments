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
package org.eclipse.jetty.server.handler;


import HttpStatus.OK_200;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.NetworkConnector;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


public class ThreadLimitHandlerTest {
    private Server _server;

    private NetworkConnector _connector;

    private LocalConnector _local;

    @Test
    public void testNoForwardHeaders() throws Exception {
        AtomicReference<String> last = new AtomicReference<>();
        ThreadLimitHandler handler = new ThreadLimitHandler(null, false) {
            @Override
            protected int getThreadLimit(String ip) {
                last.set(ip);
                return super.getThreadLimit(ip);
            }
        };
        handler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                response.setStatus(OK_200);
            }
        });
        _server.setHandler(handler);
        _server.start();
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nX-Forwarded-For: 1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nForwarded: for=1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
    }

    @Test
    public void testXForwardForHeaders() throws Exception {
        AtomicReference<String> last = new AtomicReference<>();
        ThreadLimitHandler handler = new ThreadLimitHandler("X-Forwarded-For") {
            @Override
            protected int getThreadLimit(String ip) {
                last.set(ip);
                return super.getThreadLimit(ip);
            }
        };
        _server.setHandler(handler);
        _server.start();
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nX-Forwarded-For: 1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("1.2.3.4"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nForwarded: for=1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nX-Forwarded-For: 1.1.1.1\r\nX-Forwarded-For: 6.6.6.6,1.2.3.4\r\nForwarded: for=1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("1.2.3.4"));
    }

    @Test
    public void testForwardHeaders() throws Exception {
        AtomicReference<String> last = new AtomicReference<>();
        ThreadLimitHandler handler = new ThreadLimitHandler("Forwarded") {
            @Override
            protected int getThreadLimit(String ip) {
                last.set(ip);
                return super.getThreadLimit(ip);
            }
        };
        _server.setHandler(handler);
        _server.start();
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nX-Forwarded-For: 1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("0.0.0.0"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nForwarded: for=1.2.3.4\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("1.2.3.4"));
        last.set(null);
        _local.getResponse("GET / HTTP/1.0\r\nX-Forwarded-For: 1.1.1.1\r\nForwarded: for=6.6.6.6; for=1.2.3.4\r\nX-Forwarded-For: 6.6.6.6\r\nForwarded: proto=https\r\n\r\n");
        MatcherAssert.assertThat(last.get(), Matchers.is("1.2.3.4"));
    }

    @Test
    public void testLimit() throws Exception {
        ThreadLimitHandler handler = new ThreadLimitHandler("Forwarded");
        handler.setThreadLimit(4);
        AtomicInteger count = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(1);
        handler.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                baseRequest.setHandled(true);
                response.setStatus(OK_200);
                if ("/other".equals(target))
                    return;

                try {
                    count.incrementAndGet();
                    total.incrementAndGet();
                    latch.await();
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                } finally {
                    count.decrementAndGet();
                }
            }
        });
        _server.setHandler(handler);
        _server.start();
        Socket[] client = new Socket[10];
        for (int i = 0; i < (client.length); i++) {
            client[i] = new Socket("127.0.0.1", _connector.getLocalPort());
            client[i].getOutputStream().write((("GET /" + i) + " HTTP/1.0\r\nForwarded: for=1.2.3.4\r\n\r\n").getBytes());
            client[i].getOutputStream().flush();
        }
        long wait = (System.nanoTime()) + (TimeUnit.SECONDS.toNanos(10));
        while (((count.get()) < 4) && ((System.nanoTime()) < wait))
            Thread.sleep(1);

        MatcherAssert.assertThat(count.get(), Matchers.is(4));
        // check that other requests are not blocked
        MatcherAssert.assertThat(_local.getResponse("GET /other HTTP/1.0\r\nForwarded: for=6.6.6.6\r\n\r\n"), Matchers.containsString(" 200 OK"));
        // let the other requests go
        latch.countDown();
        while (((total.get()) < 10) && ((System.nanoTime()) < wait))
            Thread.sleep(10);

        MatcherAssert.assertThat(total.get(), Matchers.is(10));
        while (((count.get()) > 0) && ((System.nanoTime()) < wait))
            Thread.sleep(10);

        MatcherAssert.assertThat(count.get(), Matchers.is(0));
    }
}

