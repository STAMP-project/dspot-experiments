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
import io.undertow.server.handlers.accesslog.AccessLogReceiver;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.Protocols;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
public class DispatcherForwardTestCase {
    private static volatile String message;

    private static volatile CountDownLatch latch = new CountDownLatch(1);

    private static final AccessLogReceiver RECEIVER = new AccessLogReceiver() {
        @Override
        public void logMessage(final String msg) {
            DispatcherForwardTestCase.message = msg;
            DispatcherForwardTestCase.latch.countDown();
        }
    };

    @Test
    public void testPathBasedInclude() throws IOException, InterruptedException {
        resetLatch();
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            get.setHeader("forward", "/forward");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("Path!Name!forwarded", response);
            DispatcherForwardTestCase.latch.await(30, TimeUnit.SECONDS);
            // UNDERTOW-327 make sure that the access log includes the original path
            String protocol = (DefaultServer.isH2()) ? Protocols.HTTP_2_0_STRING : Protocols.HTTP_1_1_STRING;
            Assert.assertEquals((("GET /servletContext/dispatch " + protocol) + " /servletContext/dispatch /dispatch"), DispatcherForwardTestCase.message);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testNameBasedInclude() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            get.setHeader("forward", "forward");
            get.setHeader("name", "true");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("Name!forwarded", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testPathBasedStaticInclude() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            get.setHeader("forward", "/snippet.html");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("SnippetText", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testPathBasedStaticIncludePost() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            post.setHeader("forward", "/snippet.html");
            HttpResponse result = client.execute(post);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("SnippetText", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testIncludeAggregatesQueryString() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch?a=b"));
            get.setHeader("forward", "/path");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("pathInfo:null queryString:a=b servletPath:/path requestUri:/servletContext/path", response);
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch?a=b"));
            get.setHeader("forward", "/path?foo=bar");
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("pathInfo:null queryString:foo=bar servletPath:/path requestUri:/servletContext/path", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testIncludesPathParameters() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch?a=b"));
            get.setHeader("forward", "/path;pathparam=foo");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("pathInfo:null queryString:a=b servletPath:/path requestUri:/servletContext/path;pathparam=foo", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

