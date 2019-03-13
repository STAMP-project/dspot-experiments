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
import HandleHttpRequest.HTTP_CONTEXT_MAP;
import HandleHttpRequest.PORT;
import HandleHttpRequest.REL_SUCCESS;
import MultipartBody.FORM;
import StandardSSLContextService.SSL_ALGORITHM;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.nifi.controller.AbstractControllerService;
import org.apache.nifi.http.HttpContextMap;
import org.apache.nifi.reporting.InitializationException;
import org.apache.nifi.stream.io.NullOutputStream;
import org.apache.nifi.stream.io.StreamUtils;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class ITestHandleHttpRequest {
    @Test(timeout = 30000)
    public void testRequestAddedToService() throws IOException, InterruptedException, MalformedURLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpRequest.class);
        runner.setProperty(PORT, "0");
        final ITestHandleHttpRequest.MockHttpContextMap contextMap = new ITestHandleHttpRequest.MockHttpContextMap();
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        // trigger processor to stop but not shutdown.
        runner.run(1, false);
        try {
            final Thread httpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final int port = getPort();
                        final HttpURLConnection connection = ((HttpURLConnection) (new URL((("http://localhost:" + port) + "/my/path?query=true&value1=value1&value2=&value3&value4=apple=orange")).openConnection()));
                        connection.setDoOutput(false);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("header1", "value1");
                        connection.setRequestProperty("header2", "");
                        connection.setRequestProperty("header3", "apple=orange");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        StreamUtils.copy(connection.getInputStream(), new NullOutputStream());
                    } catch (final Throwable t) {
                        t.printStackTrace();
                        Assert.fail(t.toString());
                    }
                }
            });
            httpThread.start();
            while (runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty()) {
                // process the request.
                runner.run(1, false, false);
            } 
            runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            Assert.assertEquals(1, contextMap.size());
            final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            mff.assertAttributeEquals("http.query.param.query", "true");
            mff.assertAttributeEquals("http.query.param.value1", "value1");
            mff.assertAttributeEquals("http.query.param.value2", "");
            mff.assertAttributeEquals("http.query.param.value3", "");
            mff.assertAttributeEquals("http.query.param.value4", "apple=orange");
            mff.assertAttributeEquals("http.headers.header1", "value1");
            mff.assertAttributeEquals("http.headers.header3", "apple=orange");
        } finally {
            // shut down the server
            runner.run(1, true);
        }
    }

    @Test(timeout = 30000)
    public void testMultipartFormDataRequest() throws IOException, InterruptedException, MalformedURLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpRequest.class);
        runner.setProperty(PORT, "0");
        final ITestHandleHttpRequest.MockHttpContextMap contextMap = new ITestHandleHttpRequest.MockHttpContextMap();
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        // trigger processor to stop but not shutdown.
        runner.run(1, false);
        try {
            final Thread httpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final int port = getPort();
                        MultipartBody multipartBody = new MultipartBody.Builder().setType(FORM).addFormDataPart("p1", "v1").addFormDataPart("p2", "v2").addFormDataPart("file1", "my-file-text.txt", RequestBody.create(MediaType.parse("text/plain"), createTextFile("my-file-text.txt", "Hello", "World"))).addFormDataPart("file2", "my-file-data.json", RequestBody.create(MediaType.parse("application/json"), createTextFile("my-file-text.txt", "{ \"name\":\"John\", \"age\":30 }"))).addFormDataPart("file3", "my-file-binary.bin", RequestBody.create(MediaType.parse("application/octet-stream"), generateRandomBinaryData(100))).build();
                        Request request = new Request.Builder().url(String.format("http://localhost:%s/my/path", port)).post(multipartBody).build();
                        OkHttpClient client = new OkHttpClient.Builder().readTimeout(3000, TimeUnit.MILLISECONDS).writeTimeout(3000, TimeUnit.MILLISECONDS).build();
                        try (Response response = client.newCall(request).execute()) {
                            Assert.assertTrue(String.format("Unexpected code: %s, body: %s", response.code(), response.body().string()), response.isSuccessful());
                        }
                    } catch (final Throwable t) {
                        t.printStackTrace();
                        Assert.fail(t.toString());
                    }
                }
            });
            httpThread.start();
            while (runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty()) {
                // process the request.
                runner.run(1, false, false);
            } 
            runner.assertAllFlowFilesTransferred(REL_SUCCESS, 5);
            Assert.assertEquals(1, contextMap.size());
            List<MockFlowFile> flowFilesForRelationship = runner.getFlowFilesForRelationship(REL_SUCCESS);
            // Part fragments are not processed in the order we submitted them.
            // We cannot rely on the order we sent them in.
            MockFlowFile mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "p1");
            String contextId = mff.getAttribute(HTTP_CONTEXT_ID);
            mff.assertAttributeEquals("http.multipart.name", "p1");
            mff.assertAttributeExists("http.multipart.size");
            mff.assertAttributeEquals("http.multipart.fragments.sequence.number", "1");
            mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
            mff.assertAttributeExists("http.headers.multipart.content-disposition");
            mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "p2");
            // each part generates a corresponding flow file - yet all parts are coming from the same request,
            mff.assertAttributeEquals(HTTP_CONTEXT_ID, contextId);
            mff.assertAttributeEquals("http.multipart.name", "p2");
            mff.assertAttributeExists("http.multipart.size");
            mff.assertAttributeExists("http.multipart.fragments.sequence.number");
            mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
            mff.assertAttributeExists("http.headers.multipart.content-disposition");
            mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "file1");
            mff.assertAttributeEquals(HTTP_CONTEXT_ID, contextId);
            mff.assertAttributeEquals("http.multipart.name", "file1");
            mff.assertAttributeEquals("http.multipart.filename", "my-file-text.txt");
            mff.assertAttributeEquals("http.headers.multipart.content-type", "text/plain");
            mff.assertAttributeExists("http.multipart.size");
            mff.assertAttributeExists("http.multipart.fragments.sequence.number");
            mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
            mff.assertAttributeExists("http.headers.multipart.content-disposition");
            mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "file2");
            mff.assertAttributeEquals(HTTP_CONTEXT_ID, contextId);
            mff.assertAttributeEquals("http.multipart.name", "file2");
            mff.assertAttributeEquals("http.multipart.filename", "my-file-data.json");
            mff.assertAttributeEquals("http.headers.multipart.content-type", "application/json");
            mff.assertAttributeExists("http.multipart.size");
            mff.assertAttributeExists("http.multipart.fragments.sequence.number");
            mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
            mff.assertAttributeExists("http.headers.multipart.content-disposition");
            mff = findFlowFile(flowFilesForRelationship, "http.multipart.name", "file3");
            mff.assertAttributeEquals(HTTP_CONTEXT_ID, contextId);
            mff.assertAttributeEquals("http.multipart.name", "file3");
            mff.assertAttributeEquals("http.multipart.filename", "my-file-binary.bin");
            mff.assertAttributeEquals("http.headers.multipart.content-type", "application/octet-stream");
            mff.assertAttributeExists("http.multipart.size");
            mff.assertAttributeExists("http.multipart.fragments.sequence.number");
            mff.assertAttributeEquals("http.multipart.fragments.total.number", "5");
            mff.assertAttributeExists("http.headers.multipart.content-disposition");
        } finally {
            // shut down the server
            runner.run(1, true);
        }
    }

    @Test(timeout = 30000)
    public void testMultipartFormDataRequestFailToRegisterContext() throws IOException, InterruptedException, MalformedURLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpRequest.class);
        runner.setProperty(PORT, "0");
        final ITestHandleHttpRequest.MockHttpContextMap contextMap = new ITestHandleHttpRequest.MockHttpContextMap();
        contextMap.setRegisterSuccessfully(false);
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        // trigger processor to stop but not shutdown.
        runner.run(1, false);
        try {
            AtomicInteger responseCode = new AtomicInteger(0);
            final Thread httpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final int port = getPort();
                        MultipartBody multipartBody = new MultipartBody.Builder().setType(FORM).addFormDataPart("p1", "v1").addFormDataPart("p2", "v2").addFormDataPart("file1", "my-file-text.txt", RequestBody.create(MediaType.parse("text/plain"), createTextFile("my-file-text.txt", "Hello", "World"))).addFormDataPart("file2", "my-file-data.json", RequestBody.create(MediaType.parse("application/json"), createTextFile("my-file-text.txt", "{ \"name\":\"John\", \"age\":30 }"))).addFormDataPart("file3", "my-file-binary.bin", RequestBody.create(MediaType.parse("application/octet-stream"), generateRandomBinaryData(100))).build();
                        Request request = new Request.Builder().url(String.format("http://localhost:%s/my/path", port)).post(multipartBody).build();
                        OkHttpClient client = new OkHttpClient.Builder().readTimeout(20000, TimeUnit.MILLISECONDS).writeTimeout(20000, TimeUnit.MILLISECONDS).build();
                        try (Response response = client.newCall(request).execute()) {
                            responseCode.set(response.code());
                        }
                    } catch (final Throwable t) {
                        t.printStackTrace();
                        Assert.fail(t.toString());
                    }
                }
            });
            httpThread.start();
            while ((responseCode.get()) == 0) {
                // process the request.
                runner.run(1, false, false);
            } 
            runner.assertAllFlowFilesTransferred(REL_SUCCESS, 0);
            Assert.assertEquals(0, contextMap.size());
            Assert.assertEquals(503, responseCode.get());
        } finally {
            // shut down the server
            runner.run(1, true);
        }
    }

    @Test(timeout = 30000)
    public void testFailToRegister() throws IOException, InterruptedException, MalformedURLException, InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpRequest.class);
        runner.setProperty(PORT, "0");
        final ITestHandleHttpRequest.MockHttpContextMap contextMap = new ITestHandleHttpRequest.MockHttpContextMap();
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        contextMap.setRegisterSuccessfully(false);
        // trigger processor to stop but not shutdown.
        runner.run(1, false);
        try {
            final int[] responseCode = new int[1];
            responseCode[0] = 0;
            final Thread httpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpURLConnection connection = null;
                    try {
                        final int port = getPort();
                        connection = ((HttpURLConnection) (new URL((("http://localhost:" + port) + "/my/path?query=true&value1=value1&value2=&value3&value4=apple=orange")).openConnection()));
                        connection.setDoOutput(false);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("header1", "value1");
                        connection.setRequestProperty("header2", "");
                        connection.setRequestProperty("header3", "apple=orange");
                        connection.setConnectTimeout(20000);
                        connection.setReadTimeout(20000);
                        StreamUtils.copy(connection.getInputStream(), new NullOutputStream());
                    } catch (final Throwable t) {
                        t.printStackTrace();
                        if (connection != null) {
                            try {
                                responseCode[0] = connection.getResponseCode();
                            } catch (IOException e) {
                                responseCode[0] = -1;
                            }
                        } else {
                            responseCode[0] = -2;
                        }
                    }
                }
            });
            httpThread.start();
            while ((responseCode[0]) == 0) {
                // process the request.
                runner.run(1, false, false);
            } 
            runner.assertTransferCount(REL_SUCCESS, 0);
            Assert.assertEquals(503, responseCode[0]);
        } finally {
            // shut down the server
            runner.run(1, true);
        }
    }

    @Test
    public void testSecure() throws InitializationException {
        final TestRunner runner = TestRunners.newTestRunner(HandleHttpRequest.class);
        runner.setProperty(PORT, "0");
        final ITestHandleHttpRequest.MockHttpContextMap contextMap = new ITestHandleHttpRequest.MockHttpContextMap();
        runner.addControllerService("http-context-map", contextMap);
        runner.enableControllerService(contextMap);
        runner.setProperty(HTTP_CONTEXT_MAP, "http-context-map");
        final Map<String, String> sslProperties = ITestHandleHttpRequest.getKeystoreProperties();
        sslProperties.putAll(ITestHandleHttpRequest.getTruststoreProperties());
        sslProperties.put(SSL_ALGORITHM.getName(), "TLSv1.2");
        final SSLContext sslContext = ITestHandleHttpRequest.useSSLContextService(runner, sslProperties);
        // trigger processor to stop but not shutdown.
        runner.run(1, false);
        try {
            final Thread httpThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final int port = getPort();
                        final HttpsURLConnection connection = ((HttpsURLConnection) (new URL((("https://localhost:" + port) + "/my/path?query=true&value1=value1&value2=&value3&value4=apple=orange")).openConnection()));
                        connection.setSSLSocketFactory(sslContext.getSocketFactory());
                        connection.setDoOutput(false);
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("header1", "value1");
                        connection.setRequestProperty("header2", "");
                        connection.setRequestProperty("header3", "apple=orange");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);
                        StreamUtils.copy(connection.getInputStream(), new NullOutputStream());
                    } catch (final Throwable t) {
                        t.printStackTrace();
                        Assert.fail(t.toString());
                    }
                }
            });
            httpThread.start();
            while (runner.getFlowFilesForRelationship(REL_SUCCESS).isEmpty()) {
                // process the request.
                runner.run(1, false, false);
            } 
            runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
            Assert.assertEquals(1, contextMap.size());
            final MockFlowFile mff = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
            mff.assertAttributeEquals("http.query.param.query", "true");
            mff.assertAttributeEquals("http.query.param.value1", "value1");
            mff.assertAttributeEquals("http.query.param.value2", "");
            mff.assertAttributeEquals("http.query.param.value3", "");
            mff.assertAttributeEquals("http.query.param.value4", "apple=orange");
            mff.assertAttributeEquals("http.headers.header1", "value1");
            mff.assertAttributeEquals("http.headers.header3", "apple=orange");
            mff.assertAttributeEquals("http.protocol", "HTTP/1.1");
        } finally {
            // shut down the server
            runner.run(1, true);
        }
    }

    private static class MockHttpContextMap extends AbstractControllerService implements HttpContextMap {
        private boolean registerSuccessfully = true;

        private final ConcurrentMap<String, HttpServletResponse> responseMap = new ConcurrentHashMap<>();

        @Override
        public boolean register(final String identifier, final HttpServletRequest request, final HttpServletResponse response, final AsyncContext context) {
            if (registerSuccessfully) {
                responseMap.put(identifier, response);
            }
            return registerSuccessfully;
        }

        @Override
        public HttpServletResponse getResponse(final String identifier) {
            return responseMap.get(identifier);
        }

        @Override
        public void complete(final String identifier) {
            responseMap.remove(identifier);
        }

        public int size() {
            return responseMap.size();
        }

        public boolean isRegisterSuccessfully() {
            return registerSuccessfully;
        }

        public void setRegisterSuccessfully(boolean registerSuccessfully) {
            this.registerSuccessfully = registerSuccessfully;
        }

        @Override
        public long getRequestTimeout(TimeUnit timeUnit) {
            return timeUnit.convert(30000, TimeUnit.MILLISECONDS);
        }
    }
}

