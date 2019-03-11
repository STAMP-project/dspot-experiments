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
package io.undertow.servlet.test.async;


import StatusCodes.INTERNAL_SERVER_ERROR;
import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class SimpleAsyncTestCase {
    public static final String HELLO_WORLD = "Hello World";

    @Test
    public void testSimpleHttpServlet() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleAsyncTestCase.HELLO_WORLD, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSimpleHttpAsyncServletWithoutDispatch() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async2"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(AnotherAsyncServlet.class.getSimpleName(), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testErrorServlet() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/error"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(INTERNAL_SERVER_ERROR, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("500", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testErrorListenerServlet() throws Exception {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/errorlistener"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(INTERNAL_SERVER_ERROR, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("500", response);
            Assert.assertEquals("ERROR", AsyncErrorListenerServlet.EVENTS.poll(10, TimeUnit.SECONDS));
            Assert.assertEquals("COMPLETED", AsyncErrorListenerServlet.EVENTS.poll(10, TimeUnit.SECONDS));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testWrappedDispatch() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/dispatch"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(("wrapped: " + (SimpleAsyncTestCase.HELLO_WORLD)), response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testErrorServletWithPostData() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpPost post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/error"));
            post.setEntity(new StringEntity("Post body stuff"));
            HttpResponse result = client.execute(post);
            Assert.assertEquals(INTERNAL_SERVER_ERROR, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("500", response);
            post = new HttpPost(((DefaultServer.getDefaultServerURL()) + "/servletContext/error"));
            post.setEntity(new StringEntity("Post body stuff"));
            result = client.execute(post);
            Assert.assertEquals(INTERNAL_SERVER_ERROR, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("500", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testServletCompletesTwiceOnInitialThread() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/double-complete"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleAsyncTestCase.HELLO_WORLD, response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

