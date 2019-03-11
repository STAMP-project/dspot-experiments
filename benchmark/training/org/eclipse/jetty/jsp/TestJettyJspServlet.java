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
package org.eclipse.jetty.jsp;


import HttpTester.Response;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpTester;
import org.eclipse.jetty.servlet.ServletTester;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDir;
import org.eclipse.jetty.toolchain.test.jupiter.WorkDirExtension;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(WorkDirExtension.class)
public class TestJettyJspServlet {
    public WorkDir workdir;

    private File _dir;

    private ServletTester _tester;

    public static class DfltServlet extends HttpServlet {
        public DfltServlet() {
            super();
        }

        /**
         *
         *
         * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.setContentType("html/text");
            resp.getOutputStream().println("This.Is.The.Default.");
        }
    }

    @Test
    public void testWithJsp() throws Exception {
        // test that an ordinary jsp is served by jsp servlet
        String request = "" + ((("GET /context/foo.jsp HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n");
        String rawResponse = _tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getContent(), Matchers.not(Matchers.containsString("This.Is.The.Default.")));
    }

    @Test
    public void testWithDirectory() throws Exception {
        // test that a dir is served by the default servlet
        String request = "" + ((("GET /context/dir HTTP/1.1\r\n" + "Host: localhost\r\n") + "Connection: close\r\n") + "\r\n");
        String rawResponse = _tester.getResponses(request);
        HttpTester.Response response = HttpTester.parseResponse(rawResponse);
        MatcherAssert.assertThat(response.toString(), response.getContent(), Matchers.containsString("This.Is.The.Default."));
    }
}

