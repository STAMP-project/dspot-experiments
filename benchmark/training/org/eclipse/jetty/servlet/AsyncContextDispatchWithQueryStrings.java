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
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.LocalConnector;
import org.eclipse.jetty.server.Server;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static ServletContextHandler.NO_SESSIONS;


/**
 * This tests verifies that merging of queryStrings works when dispatching
 * Requests via {@link AsyncContext} multiple times.
 */
public class AsyncContextDispatchWithQueryStrings {
    private Server _server = new Server();

    private ServletContextHandler _contextHandler = new ServletContextHandler(NO_SESSIONS);

    private LocalConnector _connector = new LocalConnector(_server);

    @Test
    public void testMultipleDispatchesWithNewQueryStrings() throws Exception {
        String request = "GET /initialCall?initialParam=right HTTP/1.1\r\n" + ((("Host: localhost\r\n" + "Content-Type: application/x-www-form-urlencoded\r\n") + "Connection: close\r\n") + "\r\n");
        String responseString = _connector.getResponse(request);
        MatcherAssert.assertThat(responseString, Matchers.startsWith("HTTP/1.1 200"));
    }

    private class TestServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            if ("/initialCall".equals(uri)) {
                AsyncContext async = request.startAsync();
                async.dispatch("/firstDispatchWithNewQueryString?newQueryString=initialValue");
                Assertions.assertEquals("initialParam=right", queryString);
            } else
                if ("/firstDispatchWithNewQueryString".equals(uri)) {
                    AsyncContext async = request.startAsync();
                    async.dispatch("/secondDispatchNewValueForExistingQueryString?newQueryString=newValue");
                    Assertions.assertEquals("newQueryString=initialValue&initialParam=right", queryString);
                } else {
                    response.setContentType("text/html");
                    response.setStatus(SC_OK);
                    response.getWriter().println("<h1>woohhooooo</h1>");
                    Assertions.assertEquals("newQueryString=newValue&initialParam=right", queryString);
                }

        }
    }
}

