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
package io.undertow.servlet.test.streams;


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
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class ServletOutputStreamTestCase {
    public static String message;

    public static final String HELLO_WORLD = "Hello World";

    public static final String BLOCKING_SERVLET = "blockingOutput";

    public static final String ASYNC_SERVLET = "asyncOutput";

    public static final String CONTENT_LENGTH_SERVLET = "contentLength";

    public static final String RESET = "reset";

    public static final String START = "START";

    public static final String END = "END";

    @Test
    public void testFlushAndCloseWithContentLength() throws Exception {
        TestHttpClient client = createClient();
        try {
            String uri = ((getBaseUrl()) + "/servletContext/") + (ServletOutputStreamTestCase.CONTENT_LENGTH_SERVLET);
            HttpGet get = new HttpGet(uri);
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("a", response);
            get = new HttpGet(uri);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("OK", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testResetBuffer() throws Exception {
        TestHttpClient client = createClient();
        try {
            String uri = ((getBaseUrl()) + "/servletContext/") + (ServletOutputStreamTestCase.RESET);
            HttpGet get = new HttpGet(uri);
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("hello world", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testBlockingServletOutputStream() throws IOException {
        ServletOutputStreamTestCase.message = ((ServletOutputStreamTestCase.START) + (ServletOutputStreamTestCase.HELLO_WORLD)) + (ServletOutputStreamTestCase.END);
        runTest(ServletOutputStreamTestCase.message, ServletOutputStreamTestCase.BLOCKING_SERVLET, false, true, 1, true, false, false);
        StringBuilder builder = new StringBuilder((1000 * (ServletOutputStreamTestCase.HELLO_WORLD.length())));
        builder.append(ServletOutputStreamTestCase.START);
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 1000; ++j) {
                    builder.append(ServletOutputStreamTestCase.HELLO_WORLD);
                }
                String message = (builder.toString()) + (ServletOutputStreamTestCase.END);
                runTest(message, ServletOutputStreamTestCase.BLOCKING_SERVLET, false, false, 1, false, false, false);
                runTest(message, ServletOutputStreamTestCase.BLOCKING_SERVLET, true, false, 10, false, false, false);
                runTest(message, ServletOutputStreamTestCase.BLOCKING_SERVLET, false, true, 3, false, false, false);
                runTest(message, ServletOutputStreamTestCase.BLOCKING_SERVLET, true, true, 7, false, false, false);
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testChunkedResponseWithInitialFlush() throws IOException {
        ServletOutputStreamTestCase.message = ((ServletOutputStreamTestCase.START) + (ServletOutputStreamTestCase.HELLO_WORLD)) + (ServletOutputStreamTestCase.END);
        runTest(ServletOutputStreamTestCase.message, ServletOutputStreamTestCase.BLOCKING_SERVLET, false, true, 1, true, false, false);
    }

    @Test
    public void testAsyncServletOutputStream() {
        StringBuilder builder = new StringBuilder((1000 * (ServletOutputStreamTestCase.HELLO_WORLD.length())));
        builder.append(ServletOutputStreamTestCase.START);
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(ServletOutputStreamTestCase.HELLO_WORLD);
                }
                String message = (builder.toString()) + (ServletOutputStreamTestCase.END);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, false, 1, false, false, false);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, false, 10, false, false, false);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, true, 3, false, false, false);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, true, 7, false, false, false);
            } catch (Exception e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletOutputStreamOffIOThread() {
        StringBuilder builder = new StringBuilder((1000 * (ServletOutputStreamTestCase.HELLO_WORLD.length())));
        builder.append(ServletOutputStreamTestCase.START);
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(ServletOutputStreamTestCase.HELLO_WORLD);
                }
                String message = (builder.toString()) + (ServletOutputStreamTestCase.END);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, false, 1, false, false, true);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, false, 10, false, false, true);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, true, 3, false, false, true);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, true, 7, false, false, true);
            } catch (Exception e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletOutputStreamWithPreableOffIOThread() {
        StringBuilder builder = new StringBuilder((1000 * (ServletOutputStreamTestCase.HELLO_WORLD.length())));
        builder.append(ServletOutputStreamTestCase.START);
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(ServletOutputStreamTestCase.HELLO_WORLD);
                }
                String message = (builder.toString()) + (ServletOutputStreamTestCase.END);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, false, 1, false, true, true);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, false, 10, false, true, true);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, true, 3, false, true, true);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, true, 7, false, true, true);
            } catch (Exception e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletOutputStreamWithPreable() {
        StringBuilder builder = new StringBuilder((1000 * (ServletOutputStreamTestCase.HELLO_WORLD.length())));
        builder.append(ServletOutputStreamTestCase.START);
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(ServletOutputStreamTestCase.HELLO_WORLD);
                }
                String message = (builder.toString()) + (ServletOutputStreamTestCase.END);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, false, 1, false, true, false);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, false, 10, false, true, false);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, false, true, 3, false, true, false);
                runTest(message, ServletOutputStreamTestCase.ASYNC_SERVLET, true, true, 7, false, true, false);
            } catch (Exception e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }
}

