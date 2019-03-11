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
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.handler.HandlerWrapper;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;


// TODO: needs investigation
@Disabled
@Tag("stress")
@DisabledOnOs(OS.MAC)
public class StressTest {
    private static final Logger LOG = Log.getLogger(StressTest.class);

    private static QueuedThreadPool _threads;

    private static Server _server;

    private static ServerConnector _connector;

    private static final AtomicInteger _handled = new AtomicInteger(0);

    private static final ConcurrentLinkedQueue[] _latencies = new ConcurrentLinkedQueue[]{ new ConcurrentLinkedQueue<Long>(), new ConcurrentLinkedQueue<Long>(), new ConcurrentLinkedQueue<Long>(), new ConcurrentLinkedQueue<Long>(), new ConcurrentLinkedQueue<Long>(), new ConcurrentLinkedQueue<Long>() };

    private volatile AtomicInteger[] _loops;

    private final Random _random = new Random();

    private static final String[] __tests = new String[]{ "/path/0", "/path/1", "/path/2", "/path/3", "/path/4", "/path/5", "/path/6", "/path/7", "/path/8", "/path/9", "/path/a", "/path/b", "/path/c", "/path/d", "/path/e", "/path/f" };

    @Test
    public void testMinNonPersistent() throws Throwable {
        doThreads(10, 10, false);
    }

    @Test
    public void testNonPersistent() throws Throwable {
        doThreads(20, 20, false);
        Thread.sleep(1000);
        doThreads(200, 10, false);
        Thread.sleep(1000);
        doThreads(200, 200, false);
    }

    @Test
    public void testMinPersistent() throws Throwable {
        doThreads(10, 10, true);
    }

    @Test
    public void testPersistent() throws Throwable {
        doThreads(40, 40, true);
        Thread.sleep(1000);
        doThreads(200, 10, true);
        Thread.sleep(1000);
        doThreads(200, 200, true);
    }

    private static class TestHandler extends HandlerWrapper {
        @Override
        public void handle(String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            long now = TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
            long start = Long.parseLong(baseRequest.getHeader("start"));
            long received = baseRequest.getTimeStamp();
            StressTest._handled.incrementAndGet();
            long delay = received - start;
            if (delay < 0)
                delay = 0;

            StressTest._latencies[2].add(new Long(delay));
            StressTest._latencies[3].add(new Long((now - start)));
            response.setStatus(200);
            response.getOutputStream().print((("DATA " + (request.getPathInfo())) + "\n\n"));
            baseRequest.setHandled(true);
            StressTest._latencies[4].add(new Long(((TimeUnit.NANOSECONDS.toMillis(System.nanoTime())) - start)));
            return;
        }
    }
}

