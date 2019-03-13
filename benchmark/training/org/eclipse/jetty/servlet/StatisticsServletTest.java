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


import HttpServletResponse.SC_OK;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.StatisticsHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class StatisticsServletTest {
    private Server _server;

    private LocalConnector _connector;

    @Test
    public void getStats() throws Exception {
        StatisticsHandler statsHandler = new StatisticsHandler();
        _server.setHandler(statsHandler);
        ServletContextHandler statsContext = new ServletContextHandler(statsHandler, "/");
        statsContext.addServlet(new ServletHolder(new StatisticsServletTest.TestServlet()), "/test1");
        ServletHolder servletHolder = new ServletHolder(new StatisticsServlet());
        servletHolder.setInitParameter("restrictToLocalhost", "false");
        statsContext.addServlet(servletHolder, "/stats");
        statsContext.setSessionHandler(new SessionHandler());
        _server.start();
        getResponse("/test1");
        String response = getResponse("/stats?xml=true");
        StatisticsServletTest.Stats stats = parseStats(response);
        Assertions.assertEquals(1, stats.responses2xx);
        getResponse("/stats?statsReset=true");
        response = getResponse("/stats?xml=true");
        stats = parseStats(response);
        Assertions.assertEquals(1, stats.responses2xx);
        getResponse("/test1");
        getResponse("/nothing");
        response = getResponse("/stats?xml=true");
        stats = parseStats(response);
        MatcherAssert.assertThat(("2XX Response Count" + response), stats.responses2xx, Matchers.is(3));
        MatcherAssert.assertThat(("4XX Response Count" + response), stats.responses4xx, Matchers.is(1));
    }

    public static class Stats {
        int responses2xx;

        int responses4xx;

        public Stats(int responses2xx, int responses4xx) {
            this.responses2xx = responses2xx;
            this.responses4xx = responses4xx;
        }
    }

    public static class TestServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setStatus(SC_OK);
            PrintWriter writer = resp.getWriter();
            writer.write("Yup!!");
        }
    }
}

