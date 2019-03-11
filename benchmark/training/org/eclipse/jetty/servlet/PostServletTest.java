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
package org.eclipse.jetty.servlet;


import LocalConnector.LocalEndPoint;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class PostServletTest {
    private static final Logger LOG = Log.getLogger(PostServletTest.class);

    private static final AtomicBoolean posted = new AtomicBoolean(false);

    private static final AtomicReference<Throwable> ex0 = new AtomicReference<>();

    private static final AtomicReference<Throwable> ex1 = new AtomicReference<>();

    private static CountDownLatch complete;

    public static class BasicReadPostServlet extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) {
            PostServletTest.posted.set(true);
            byte[] buffer = new byte[1024];
            try {
                int len = request.getInputStream().read(buffer);
                while (len > 0) {
                    response.getOutputStream().println(("read " + len));
                    response.getOutputStream().flush();
                    len = request.getInputStream().read(buffer);
                } 
            } catch (Exception e0) {
                PostServletTest.ex0.set(e0);
                try {
                    // this read-call should fail immediately
                    request.getInputStream().read(buffer);
                } catch (Exception e1) {
                    PostServletTest.ex1.set(e1);
                    PostServletTest.LOG.warn(e1.toString());
                }
            } finally {
                PostServletTest.complete.countDown();
            }
        }
    }

    private Server server;

    private LocalConnector connector;

    @Test
    public void testGoodPost() throws Exception {
        StringBuilder req = new StringBuilder();
        req.append("POST /post HTTP/1.1\r\n");
        req.append("Host: localhost\r\n");
        req.append("Transfer-Encoding: chunked\r\n");
        req.append("\r\n");
        req.append("6\r\n");
        req.append("Hello ");
        req.append("\r\n");
        req.append("7\r\n");
        req.append("World!\n");
        req.append("\r\n");
        req.append("0\r\n");
        req.append("\r\n");
        String resp = connector.getResponse(req.toString());
        MatcherAssert.assertThat("resp", resp, Matchers.containsString("HTTP/1.1 200 OK"));
        MatcherAssert.assertThat("resp", resp, Matchers.containsString("chunked"));
        MatcherAssert.assertThat("resp", resp, Matchers.containsString("read 6"));
        MatcherAssert.assertThat("resp", resp, Matchers.containsString("read 7"));
        MatcherAssert.assertThat("resp", resp, Matchers.containsString("\r\n0\r\n"));
        MatcherAssert.assertThat(PostServletTest.ex0.get(), Matchers.nullValue());
        MatcherAssert.assertThat(PostServletTest.ex1.get(), Matchers.nullValue());
    }

    @Test
    public void testBadPost() throws Exception {
        StringBuilder req = new StringBuilder((16 * 1024));
        req.append("POST /post HTTP/1.1\r\n");
        req.append("Host: localhost\r\n");
        req.append("Transfer-Encoding: chunked\r\n");
        req.append("\r\n");
        // intentionally bad (not a valid chunked char here)
        for (int i = 1024; (i--) > 0;)
            req.append("xxxxxxxxxxxx");

        req.append("\r\n");
        req.append("\r\n");
        String resp = connector.getResponse(req.toString());
        MatcherAssert.assertThat(resp, Matchers.startsWith("HTTP/1.1 200 OK"));// exception eaten by handler

        Assertions.assertTrue(PostServletTest.complete.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(PostServletTest.ex0.get(), Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(PostServletTest.ex1.get(), Matchers.not(Matchers.nullValue()));
    }

    @Test
    public void testDeferredBadPost() throws Exception {
        StringBuilder req = new StringBuilder((16 * 1024));
        req.append("POST /post HTTP/1.1\r\n");
        req.append("Host: localhost\r\n");
        req.append("Transfer-Encoding: chunked\r\n");
        req.append("\r\n");
        LocalConnector.LocalEndPoint endp = connector.executeRequest(req.toString());
        Thread.sleep(1000);
        Assertions.assertFalse(PostServletTest.posted.get());
        req.setLength(0);
        // intentionally bad (not a valid chunked char here)
        for (int i = 1024; (i--) > 0;)
            req.append("xxxxxxxxxxxx");

        req.append("\r\n");
        req.append("\r\n");
        endp.addInput(req.toString());
        endp.waitUntilClosedOrIdleFor(1, TimeUnit.SECONDS);
        String resp = endp.takeOutputString();
        MatcherAssert.assertThat(resp, Matchers.startsWith("HTTP/1.1 200 OK"));// exception eaten by handler

        Assertions.assertTrue(PostServletTest.complete.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(PostServletTest.ex0.get(), Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(PostServletTest.ex1.get(), Matchers.not(Matchers.nullValue()));
    }

    @Test
    public void testBadSplitPost() throws Exception {
        StringBuilder req = new StringBuilder();
        req.append("POST /post HTTP/1.1\r\n");
        req.append("Host: localhost\r\n");
        req.append("Connection: close\r\n");
        req.append("Transfer-Encoding: chunked\r\n");
        req.append("\r\n");
        req.append("6\r\n");
        req.append("Hello ");
        req.append("\r\n");
        try (StacklessLogging scope = new StacklessLogging(ServletHandler.class)) {
            LocalConnector.LocalEndPoint endp = connector.executeRequest(req.toString());
            req.setLength(0);
            while (!(PostServletTest.posted.get()))
                Thread.sleep(100);

            Thread.sleep(100);
            req.append("x\r\n");
            req.append("World\n");
            req.append("\r\n");
            req.append("0\r\n");
            req.append("\r\n");
            endp.addInput(req.toString());
            endp.waitUntilClosedOrIdleFor(1, TimeUnit.SECONDS);
            String resp = endp.takeOutputString();
            MatcherAssert.assertThat("resp", resp, Matchers.containsString("HTTP/1.1 200 "));
            MatcherAssert.assertThat("resp", resp, Matchers.not(Matchers.containsString("\r\n0\r\n")));// aborted

        }
        Assertions.assertTrue(PostServletTest.complete.await(5, TimeUnit.SECONDS));
        MatcherAssert.assertThat(PostServletTest.ex0.get(), Matchers.not(Matchers.nullValue()));
        MatcherAssert.assertThat(PostServletTest.ex1.get(), Matchers.not(Matchers.nullValue()));
    }
}

