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
package io.undertow.servlet.test.dispatcher;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class DispatcherIncludeTestCase {
    @Test
    public void testPathBasedInclude() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            get.setHeader("include", "/include");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(((IncludeServlet.MESSAGE) + "Path!Name!included"), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testNameBasedInclude() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            get.setHeader("include", "include");
            get.setHeader("name", "true");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(((IncludeServlet.MESSAGE) + "Name!included"), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testPathBasedStaticInclude() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            get.setHeader("include", "/snippet.html");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(((IncludeServlet.MESSAGE) + "SnippetText"), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testPathBasedStaticIncludePost() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            post.setHeader("include", "/snippet.html");
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(((IncludeServlet.MESSAGE) + "SnippetText"), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testIncludeAggregatesQueryString() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch?a=b"));
            get.setHeader("include", "/path");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(((IncludeServlet.MESSAGE) + "pathInfo:null queryString:a=b servletPath:/dispatch requestUri:/servletContext/dispatch"), response);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch?a=b"));
            get.setHeader("include", "/path?foo=bar");
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(((IncludeServlet.MESSAGE) + "pathInfo:null queryString:a=b servletPath:/dispatch requestUri:/servletContext/dispatch"), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

