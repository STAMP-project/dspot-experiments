/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import HTTPUtils.HTTP_CONTEXT_ID;
import HTTPUtils.HTTP_LOCAL_NAME;
import HTTPUtils.HTTP_PORT;
import HTTPUtils.HTTP_REMOTE_HOST;
import HTTPUtils.HTTP_REQUEST_URI;
import HTTPUtils.HTTP_SSL_CERT;
import HandleHttpResponse.HTTP_CONTEXT_MAP;
import HandleHttpResponse.REL_FAILURE;
import HandleHttpResponse.REL_SUCCESS;
import HandleHttpResponse.STATUS_CODE;
import ProvenanceEventType.SEND;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.http.HttpContextMap;
import org.apache.nifi.processor.exception.FlowFileAccessException;
import org.apache.nifi.processor.exception.ProcessException;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestHandleHttpResponse {
    @Test
    public void testEnsureCompleted() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpResponse.class);
        final TestHandleHttpResponse.MockHttpContextMap contextMap = new TestHandleHttpResponse.MockHttpContextMap("my-id", "");
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        runner.setProperty(STATUS_CODE, "${status.code}");
        runner.setProperty("my-attr", "${my-attr}");
        runner.setProperty("no-valid-attr", "${no-valid-attr}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(HTTP_CONTEXT_ID, "my-id");
        attributes.put(HTTP_REQUEST_URI, "/test");
        attributes.put(HTTP_LOCAL_NAME, "server");
        attributes.put(HTTP_PORT, "8443");
        attributes.put(HTTP_REMOTE_HOST, "client");
        attributes.put(HTTP_SSL_CERT, "sslDN");
        attributes.put("my-attr", "hello");
        attributes.put("status.code", "201");
        runner.enqueue("hello".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        Assert.assertTrue(((runner.getProvenanceEvents().size()) == 1));
        Assert.assertEquals(SEND, runner.getProvenanceEvents().get(0).getEventType());
        Assert.assertEquals("https://client@server:8443/test", runner.getProvenanceEvents().get(0).getTransitUri());
        Assert.assertEquals("hello", contextMap.baos.toString());
        Assert.assertEquals("hello", contextMap.headersSent.get("my-attr"));
        Assert.assertNull(contextMap.headersSent.get("no-valid-attr"));
        Assert.assertEquals(201, contextMap.statusCode);
        Assert.assertEquals(1, contextMap.getCompletionCount());
        Assert.assertTrue(contextMap.headersWithNoValue.isEmpty());
    }

    @Test
    public void testWithExceptionThrown() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpResponse.class);
        final TestHandleHttpResponse.MockHttpContextMap contextMap = new TestHandleHttpResponse.MockHttpContextMap("my-id", "FlowFileAccessException");
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        runner.setProperty(STATUS_CODE, "${status.code}");
        runner.setProperty("my-attr", "${my-attr}");
        runner.setProperty("no-valid-attr", "${no-valid-attr}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(HTTP_CONTEXT_ID, "my-id");
        attributes.put("my-attr", "hello");
        attributes.put("status.code", "201");
        runner.enqueue("hello".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        Assert.assertEquals(0, contextMap.getCompletionCount());
    }

    @Test
    public void testCannotWriteResponse() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpResponse.class);
        final TestHandleHttpResponse.MockHttpContextMap contextMap = new TestHandleHttpResponse.MockHttpContextMap("my-id", "ProcessException");
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        runner.setProperty(STATUS_CODE, "${status.code}");
        runner.setProperty("my-attr", "${my-attr}");
        runner.setProperty("no-valid-attr", "${no-valid-attr}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(HTTP_CONTEXT_ID, "my-id");
        attributes.put("my-attr", "hello");
        attributes.put("status.code", "201");
        runner.enqueue("hello".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        Assert.assertEquals(1, contextMap.getCompletionCount());
    }

    @Test
    public void testStatusCodeEmpty() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpResponse.class);
        final TestHandleHttpResponse.MockHttpContextMap contextMap = new TestHandleHttpResponse.MockHttpContextMap("my-id", "");
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        runner.setProperty(STATUS_CODE, "${status.code}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put(HTTP_CONTEXT_ID, "my-id");
        attributes.put("my-attr", "hello");
        runner.enqueue("hello".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        Assert.assertEquals(0, contextMap.getCompletionCount());
    }

    private static class MockHttpContextMap extends AbstractControllerService implements HttpContextMap {
        private final String id;

        private final AtomicInteger completedCount = new AtomicInteger(0);

        private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        private final ConcurrentMap<String, String> headersSent = new ConcurrentHashMap<>();

        private final String shouldThrowExceptionClass;

        private volatile int statusCode = -1;

        private final List<String> headersWithNoValue = new CopyOnWriteArrayList<>();

        public MockHttpContextMap(final String expectedIdentifier, final String shouldThrowExceptionClass) {
            this.id = expectedIdentifier;
            this.shouldThrowExceptionClass = shouldThrowExceptionClass;
        }

        @Override
        public boolean register(String identifier, HttpServletRequest request, HttpServletResponse response, AsyncContext context) {
            return true;
        }

        @Override
        public HttpServletResponse getResponse(final String identifier) {
            if (!(id.equals(identifier))) {
                Assert.fail(((("attempting to respond to wrong request; should have been " + (id)) + " but was ") + identifier));
            }
            try {
                final HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
                if (((shouldThrowExceptionClass) != null) && (shouldThrowExceptionClass.equals("FlowFileAccessException"))) {
                    Mockito.when(response.getOutputStream()).thenThrow(new FlowFileAccessException("exception"));
                } else
                    if (((shouldThrowExceptionClass) != null) && (shouldThrowExceptionClass.equals("ProcessException"))) {
                        Mockito.when(response.getOutputStream()).thenThrow(new ProcessException("exception"));
                    } else {
                        Mockito.when(response.getOutputStream()).thenReturn(new ServletOutputStream() {
                            @Override
                            public boolean isReady() {
                                return true;
                            }

                            @Override
                            public void setWriteListener(WriteListener writeListener) {
                            }

                            @Override
                            public void write(int b) throws IOException {
                                baos.write(b);
                            }

                            @Override
                            public void write(byte[] b) throws IOException {
                                baos.write(b);
                            }

                            @Override
                            public void write(byte[] b, int off, int len) throws IOException {
                                baos.write(b, off, len);
                            }
                        });
                    }

                Mockito.doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(final InvocationOnMock invocation) throws Throwable {
                        final String key = getArgumentAt(0, String.class);
                        final String value = getArgumentAt(1, String.class);
                        if (value == null) {
                            headersWithNoValue.add(key);
                        } else {
                            headersSent.put(key, value);
                        }
                        return null;
                    }
                }).when(response).setHeader(Mockito.any(String.class), Mockito.any(String.class));
                Mockito.doAnswer(new Answer<Object>() {
                    @Override
                    public Object answer(final InvocationOnMock invocation) throws Throwable {
                        statusCode = invocation.getArgumentAt(0, int.class);
                        return null;
                    }
                }).when(response).setStatus(Mockito.anyInt());
                return response;
            } catch (final Exception e) {
                e.printStackTrace();
                Assert.fail(e.toString());
                return null;
            }
        }

        @Override
        public void complete(final String identifier) {
            if (!(id.equals(identifier))) {
                Assert.fail(((("attempting to respond to wrong request; should have been " + (id)) + " but was ") + identifier));
            }
            completedCount.incrementAndGet();
        }

        public int getCompletionCount() {
            return completedCount.get();
        }

        @Override
        public long getRequestTimeout(TimeUnit timeUnit) {
            return timeUnit.convert(30000, TimeUnit.MILLISECONDS);
        }
    }
}

