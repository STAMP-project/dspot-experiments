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
package org.eclipse.jetty.servlets;


import DispatcherType.ASYNC;
import DispatcherType.REQUEST;
import HttpTester.Request;
import QoSFilter.MAX_REQUESTS_INIT_PARAM;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletTester;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static QoSFilter.__DEFAULT_MAX_PRIORITY;


public class QoSFilterTest {
    private static final Logger LOG = Log.getLogger(QoSFilterTest.class);

    private ServletTester _tester;

    private LocalConnector[] _connectors;

    private final int NUM_CONNECTIONS = 8;

    private final int NUM_LOOPS = 6;

    private final int MAX_QOS = 4;

    @Test
    public void testNoFilter() throws Exception {
        List<QoSFilterTest.Worker> workers = new ArrayList<>();
        for (int i = 0; i < (NUM_CONNECTIONS); ++i) {
            workers.add(new QoSFilterTest.Worker(i));
        }
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CONNECTIONS);
        List<Future<Void>> futures = executor.invokeAll(workers, 10, TimeUnit.SECONDS);
        rethrowExceptions(futures);
        if ((QoSFilterTest.TestServlet.__maxSleepers) <= (MAX_QOS))
            QoSFilterTest.LOG.warn("TEST WAS NOT PARALLEL ENOUGH!");
        else
            MatcherAssert.assertThat(QoSFilterTest.TestServlet.__maxSleepers, Matchers.lessThanOrEqualTo(NUM_CONNECTIONS));

    }

    @Test
    public void testQosFilter() throws Exception {
        FilterHolder holder = new FilterHolder(QoSFilterTest.QoSFilter2.class);
        holder.setAsyncSupported(true);
        holder.setInitParameter(MAX_REQUESTS_INIT_PARAM, String.valueOf(MAX_QOS));
        _tester.getContext().getServletHandler().addFilterWithMapping(holder, "/*", EnumSet.of(REQUEST, ASYNC));
        List<QoSFilterTest.Worker2> workers = new ArrayList<>();
        for (int i = 0; i < (NUM_CONNECTIONS); ++i) {
            workers.add(new QoSFilterTest.Worker2(i));
        }
        ExecutorService executor = Executors.newFixedThreadPool(NUM_CONNECTIONS);
        List<Future<Void>> futures = executor.invokeAll(workers, 20, TimeUnit.SECONDS);
        rethrowExceptions(futures);
        if ((QoSFilterTest.TestServlet.__maxSleepers) < (MAX_QOS))
            QoSFilterTest.LOG.warn("TEST WAS NOT PARALLEL ENOUGH!");
        else
            Assertions.assertEquals(QoSFilterTest.TestServlet.__maxSleepers, MAX_QOS);

    }

    class Worker implements Callable<Void> {
        private int _num;

        public Worker(int num) {
            _num = num;
        }

        @Override
        public Void call() throws Exception {
            for (int i = 0; i < (NUM_LOOPS); i++) {
                HttpTester.Request request = HttpTester.newRequest();
                request.setMethod("GET");
                request.setHeader("host", "tester");
                request.setURI(("/context/test?priority=" + ((_num) % (__DEFAULT_MAX_PRIORITY))));
                request.setHeader("num", ((_num) + ""));
                String responseString = _connectors[_num].getResponse(BufferUtil.toString(request.generate()));
                MatcherAssert.assertThat("Response contains", responseString, Matchers.containsString("HTTP"));
            }
            return null;
        }
    }

    class Worker2 implements Callable<Void> {
        private int _num;

        public Worker2(int num) {
            _num = num;
        }

        @Override
        public Void call() throws Exception {
            URL url = null;
            try {
                String addr = _tester.createConnector(true);
                for (int i = 0; i < (NUM_LOOPS); i++) {
                    url = new URL(((((((addr + "/context/test?priority=") + ((_num) % (QoSFilter.__DEFAULT_MAX_PRIORITY))) + "&n=") + (_num)) + "&l=") + i));
                    url.getContent();
                }
            } catch (Exception e) {
                QoSFilterTest.LOG.debug((("Request " + url) + " failed"), e);
            }
            return null;
        }
    }

    public static class TestServlet extends HttpServlet {
        private static int __sleepers;

        private static int __maxSleepers;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            try {
                synchronized(QoSFilterTest.TestServlet.class) {
                    (QoSFilterTest.TestServlet.__sleepers)++;
                    if ((QoSFilterTest.TestServlet.__sleepers) > (QoSFilterTest.TestServlet.__maxSleepers))
                        QoSFilterTest.TestServlet.__maxSleepers = QoSFilterTest.TestServlet.__sleepers;

                }
                Thread.sleep(50);
                synchronized(QoSFilterTest.TestServlet.class) {
                    (QoSFilterTest.TestServlet.__sleepers)--;
                }
                response.setContentType("text/plain");
                response.getWriter().println("DONE!");
            } catch (InterruptedException e) {
                response.sendError(500);
            }
        }
    }

    public static class QoSFilter2 extends QoSFilter {
        @Override
        public int getPriority(ServletRequest request) {
            String p = request.getParameter("priority");
            if (p != null)
                return Integer.parseInt(p);

            return 0;
        }
    }
}

