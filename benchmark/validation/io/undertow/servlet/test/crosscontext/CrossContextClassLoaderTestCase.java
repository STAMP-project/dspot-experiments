/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.crosscontext;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class CrossContextClassLoaderTestCase {
    @Test
    public void testCrossContextRequest() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/includer/a"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(("Including Servlet Class Loader: IncluderClassLoader\n" + (("Including Servlet Context Path: /includer\n" + "Included Servlet Class Loader: IncludedClassLoader\n") + "Including Servlet Context Path: /included\n")), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    private static final class IncludeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.getWriter().println(("Including Servlet Class Loader: " + (Thread.currentThread().getContextClassLoader().toString())));
            resp.getWriter().println(("Including Servlet Context Path: " + (req.getServletContext().getContextPath())));
            ServletContext context = req.getServletContext().getContext("/included");
            context.getRequestDispatcher("/a").include(req, resp);
        }
    }

    private static final class IncludedServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            resp.getWriter().println(("Included Servlet Class Loader: " + (Thread.currentThread().getContextClassLoader().toString())));
            resp.getWriter().println(("Including Servlet Context Path: " + (req.getServletContext().getContextPath())));
        }
    }

    private static final class TempClassLoader extends ClassLoader {
        private final String name;

        private TempClassLoader(String name) {
            super(CrossContextClassLoaderTestCase.TempClassLoader.class.getClassLoader());
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}

