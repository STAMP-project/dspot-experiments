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
package org.eclipse.jetty.http2.client;


import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntFunction;
import java.util.stream.IntStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http2.api.Session;
import org.eclipse.jetty.util.ByteArrayOutputStream2;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;


@Disabled
public class SmallThreadPoolLoadTest extends AbstractTest {
    private final Logger logger = Log.getLogger(SmallThreadPoolLoadTest.class);

    private final AtomicLong requestIds = new AtomicLong();

    @Test
    public void testConcurrentWithSmallServerThreadPool() throws Exception {
        start(new SmallThreadPoolLoadTest.LoadServlet());
        // Only one connection to the server.
        Session session = newClient(new Session.Listener.Adapter());
        int runs = 10;
        int iterations = 512;
        boolean result = IntStream.range(0, 16).parallel().mapToObj(( i) -> IntStream.range(0, runs).mapToObj(( j) -> run(session, iterations)).reduce(true, ( acc, res) -> acc && res)).reduce(true, ( acc, res) -> acc && res);
        Assertions.assertTrue(result);
    }

    private static class LoadServlet extends HttpServlet {
        @Override
        protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String method = request.getMethod().toUpperCase(Locale.ENGLISH);
            switch (method) {
                case "GET" :
                    {
                        int contentLength = request.getIntHeader("X-Download");
                        if (contentLength > 0)
                            response.getOutputStream().write(new byte[contentLength]);

                        break;
                    }
                case "POST" :
                    {
                        int content_length = request.getContentLength();
                        ByteArrayOutputStream2 bout = new ByteArrayOutputStream2((content_length > 0 ? content_length : 16 * 1024));
                        IO.copy(request.getInputStream(), bout);
                        response.getOutputStream().write(bout.getBuf(), 0, bout.getCount());
                        break;
                    }
            }
        }
    }
}

