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
package io.undertow.servlet.test.session;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * basic test of in memory session functionality
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ServletURLRewritingSessionTestCase {
    public static final String COUNT = "count";

    @Test
    public void testURLRewriting() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/foo;foo=bar"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String url = HttpClientUtils.readResponse(result);
            Header[] header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("0", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("1", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("2", header[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testURLRewritingWithQueryParameters() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/foo?a=b;c"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String url = HttpClientUtils.readResponse(result);
            Header[] header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("0", header[0].getValue());
            Assert.assertEquals("b;c", result.getHeaders("a")[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("1", header[0].getValue());
            Assert.assertEquals("b;c", result.getHeaders("a")[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("2", header[0].getValue());
            Assert.assertEquals("b;c", result.getHeaders("a")[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testURLRewritingWithExistingOldSessionId() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/foo;jsessionid=foobar"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String url = HttpClientUtils.readResponse(result);
            Header[] header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("0", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("1", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("2", header[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testURLRewritingWithExistingOldSessionIdAndOtherPathParams() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/foo;jsessionid=foobar&a=b"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String url = HttpClientUtils.readResponse(result);
            Header[] header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("0", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("1", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(ServletURLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("2", header[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    public static class URLRewritingServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
            HttpSession session = req.getSession(true);
            Object existing = session.getAttribute(ServletURLRewritingSessionTestCase.COUNT);
            if (existing == null) {
                session.setAttribute(ServletURLRewritingSessionTestCase.COUNT, 0);
            } else {
                Assert.assertTrue(req.getRequestURI().startsWith("/servletContext/foo;"));
                Assert.assertTrue(req.getRequestURI().contains(("jsessionid=" + (session.getId()))));
            }
            Integer count = ((Integer) (session.getAttribute(ServletURLRewritingSessionTestCase.COUNT)));
            resp.addHeader(ServletURLRewritingSessionTestCase.COUNT, count.toString());
            session.setAttribute(ServletURLRewritingSessionTestCase.COUNT, (++count));
            for (Map.Entry<String, String[]> qp : req.getParameterMap().entrySet()) {
                resp.addHeader(qp.getKey(), qp.getValue()[0]);
            }
            if ((req.getQueryString()) == null) {
                resp.getWriter().write(resp.encodeURL(((DefaultServer.getDefaultServerURL()) + (req.getRequestURI()))));
            } else {
                resp.getWriter().write(resp.encodeURL(((((DefaultServer.getDefaultServerURL()) + (req.getRequestURI())) + "?") + (req.getQueryString()))));
            }
        }
    }
}

