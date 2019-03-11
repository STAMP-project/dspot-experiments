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
package io.undertow.servlet.test.listener.request.async;


import StatusCodes.OK;
import io.undertow.servlet.test.util.TestListener;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
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
public class RequestListenerAsyncRequestTestCase {
    public static final String HELLO_WORLD = "Hello World";

    @Test
    public void testSimpleHttpServlet() throws IOException {
        TestListener.init(4);
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(RequestListenerAsyncRequestTestCase.HELLO_WORLD, response);
            Assert.assertArrayEquals(new String[]{ "created REQUEST", "destroyed REQUEST", "created ASYNC", "destroyed ASYNC" }, TestListener.results().toArray());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSimpleHttpServletCompleteInInitialRequest() throws IOException {
        TestListener.init(3);
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/asynccomplete"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("asynccomplete", response);
            Assert.assertArrayEquals(new String[]{ "created REQUEST", "onComplete", "destroyed REQUEST" }, TestListener.results().toArray());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testSimpleAsyncHttpServletWithoutDispatch() throws IOException {
        TestListener.init(2);
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async2"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(AnotherAsyncServlet.class.getSimpleName(), response);
            Assert.assertArrayEquals(new String[]{ "created REQUEST", "destroyed REQUEST", "created REQUEST", "destroyed REQUEST" }, TestListener.results().toArray());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

