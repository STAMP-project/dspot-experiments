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
package io.undertow.servlet.test.listener.request.async.onError;


import StatusCodes.OK;
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
 * @author Jozef Hartinger
 * @see https://issues.jboss.org/browse/UNDERTOW-30
 * @see https://issues.jboss.org/browse/UNDERTOW-31
 * @see https://issues.jboss.org/browse/UNDERTOW-32
 */
@RunWith(DefaultServer.class)
public class AsyncListenerOnErrorTest {
    @Test
    public void testAsyncListenerOnErrorInvoked1() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async1"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleAsyncListener.MESSAGE, response);
            Assert.assertArrayEquals(new String[]{ "ERROR", "COMPLETE" }, AsyncEventListener.results(2));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testAsyncListenerOnErrorInvoked2() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async2"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleAsyncListener.MESSAGE, response);
            Assert.assertArrayEquals(new String[]{ "ERROR", "COMPLETE" }, AsyncEventListener.results(2));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultiAsyncDispatchError() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async3"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleAsyncListener.MESSAGE, response);
            Assert.assertArrayEquals(new String[]{ "START", "ERROR", "COMPLETE" }, AsyncEventListener.results(3));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    /**
     * Regression test for UNDERTOW-1455
     *
     * Compared to testAsyncListenerOnErrorInvoked* tests, exception is thrown in
     * entering servlet not in asynchronous dispatch part.
     */
    @Test
    public void testAsyncListenerOnErrorExceptionInFirstServlet() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/async4"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            final String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals(SimpleAsyncListener.MESSAGE, response);
            Assert.assertArrayEquals(new String[]{ "ERROR", "COMPLETE" }, AsyncEventListener.results(2));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

