/**
 * Copyright (c) 2016 Network New Technologies Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.networknt.client;


import Headers.AUTHORIZATION;
import Headers.CLOSE;
import Headers.CONNECTION;
import Headers.CONTENT_TYPE;
import Headers.HOST;
import Headers.TRANSFER_ENCODING;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Methods.GET;
import Methods.POST;
import OptionMap.EMPTY;
import UndertowOptions.ENABLE_HTTP2;
import io.undertow.Undertow;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.util.StringReadChannelListener;
import io.undertow.util.StringWriteChannelListener;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.ClosedChannelException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.net.ssl.SSLContext;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.XnioWorker;
import org.xnio.ssl.XnioSsl;

import static Http2Client.BUFFER_POOL;


public class Http2ClientTest {
    static final Logger logger = LoggerFactory.getLogger(Http2ClientTest.class);

    static Undertow server = null;

    static SSLContext sslContext;

    private static final String message = "Hello World!";

    public static final String MESSAGE = "/message";

    public static final String POST = "/post";

    public static final String FORM = "/form";

    public static final String TOKEN = "/oauth2/token";

    public static final String API = "/api";

    public static final String KEY = "/oauth2/key";

    private static final String SERVER_KEY_STORE = "server.keystore";

    private static final String SERVER_TRUST_STORE = "server.truststore";

    private static final String CLIENT_KEY_STORE = "client.keystore";

    private static final String CLIENT_TRUST_STORE = "client.truststore";

    private static final char[] STORE_PASSWORD = "password".toCharArray();

    private static XnioWorker worker;

    private static final URI ADDRESS;

    static {
        try {
            ADDRESS = new URI("http://localhost:7777");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAddNullToken() {
        final Http2Client client = Http2Client.getInstance();
        final ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(Http2ClientTest.POST);
        client.addAuthToken(request, null);
        Assert.assertNull(request.getRequestHeaders().getFirst(AUTHORIZATION));
    }

    @Test
    public void testAddToken() {
        final Http2Client client = Http2Client.getInstance();
        final ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(Http2ClientTest.POST);
        client.addAuthToken(request, "token");
        Assert.assertEquals("Bearer token", request.getRequestHeaders().getFirst(AUTHORIZATION));
    }

    @Test
    public void testSingleHttp2PostSsl() throws Exception {
        // 
        final Http2Client client = Http2ClientTest.createClient();
        final String postMessage = "This is a post request";
        final List<String> responses = new CopyOnWriteArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        SSLContext context = Http2Client.createSSLContext();
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        final ClientConnection connection = client.connect(new URI("https://localhost:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        try {
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(Http2ClientTest.POST);
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, new io.undertow.client.ClientCallback<ClientExchange>() {
                        @Override
                        public void completed(ClientExchange result) {
                            new StringWriteChannelListener(postMessage).setup(result.getRequestChannel());
                            result.setResponseListener(new io.undertow.client.ClientCallback<ClientExchange>() {
                                @Override
                                public void completed(ClientExchange result) {
                                    new StringReadChannelListener(Http2Client.BUFFER_POOL) {
                                        @Override
                                        protected void stringDone(String string) {
                                            responses.add(string);
                                            latch.countDown();
                                        }

                                        @Override
                                        protected void error(IOException e) {
                                            e.printStackTrace();
                                            latch.countDown();
                                        }
                                    }.setup(result.getResponseChannel());
                                }

                                @Override
                                public void failed(IOException e) {
                                    e.printStackTrace();
                                    latch.countDown();
                                }
                            });
                        }

                        @Override
                        public void failed(IOException e) {
                            e.printStackTrace();
                            latch.countDown();
                        }
                    });
                }
            });
            latch.await(10, TimeUnit.SECONDS);
            Assert.assertEquals(1, responses.size());
            for (final String response : responses) {
                Assert.assertEquals(postMessage, response);
            }
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testSingleHttp2FormSsl() throws Exception {
        // 
        final Http2Client client = Http2ClientTest.createClient();
        Map<String, String> params = new HashMap<>();
        params.put("key1", "value1");
        params.put("key2", "value2");
        final String postMessage = Http2Client.getFormDataString(params);
        final List<String> responses = new CopyOnWriteArrayList<>();
        final CountDownLatch latch = new CountDownLatch(1);
        SSLContext context = Http2Client.createSSLContext();
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        final ClientConnection connection = client.connect(new URI("https://localhost:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        try {
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(Http2ClientTest.FORM);
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/x-www-form-urlencoded");
                    connection.sendRequest(request, new io.undertow.client.ClientCallback<ClientExchange>() {
                        @Override
                        public void completed(ClientExchange result) {
                            new StringWriteChannelListener(postMessage).setup(result.getRequestChannel());
                            result.setResponseListener(new io.undertow.client.ClientCallback<ClientExchange>() {
                                @Override
                                public void completed(ClientExchange result) {
                                    new StringReadChannelListener(Http2Client.BUFFER_POOL) {
                                        @Override
                                        protected void stringDone(String string) {
                                            System.out.println(("string = " + string));
                                            responses.add(string);
                                            latch.countDown();
                                        }

                                        @Override
                                        protected void error(IOException e) {
                                            e.printStackTrace();
                                            latch.countDown();
                                        }
                                    }.setup(result.getResponseChannel());
                                }

                                @Override
                                public void failed(IOException e) {
                                    e.printStackTrace();
                                    latch.countDown();
                                }
                            });
                        }

                        @Override
                        public void failed(IOException e) {
                            e.printStackTrace();
                            latch.countDown();
                        }
                    });
                }
            });
            latch.await(10, TimeUnit.SECONDS);
            Assert.assertEquals(1, responses.size());
            for (final String response : responses) {
                Assert.assertEquals(postMessage, response);
            }
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testConnectionClose() throws Exception {
        // 
        final Http2Client client = Http2ClientTest.createClient();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection = client.connect(Http2ClientTest.ADDRESS, Http2ClientTest.worker, BUFFER_POOL, EMPTY).get();
        try {
            ClientRequest request = new ClientRequest().setPath(Http2ClientTest.MESSAGE).setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            final AtomicReference<ClientResponse> reference = new AtomicReference<>();
            request.getRequestHeaders().add(CONNECTION, CLOSE.toString());
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
            final ClientResponse response = reference.get();
            Assert.assertEquals(Http2ClientTest.message, response.getAttachment(RESPONSE_BODY));
            Assert.assertEquals(false, connection.isOpen());
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testResponseTime() throws Exception {
        // 
        final Http2Client client = Http2ClientTest.createClient();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection = client.connect(Http2ClientTest.ADDRESS, Http2ClientTest.worker, BUFFER_POOL, EMPTY).get();
        try {
            ClientRequest request = new ClientRequest().setPath(Http2ClientTest.MESSAGE).setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            final AtomicReference<AsyncResult<AsyncResponse>> reference = new AtomicReference<>();
            request.getRequestHeaders().add(CONNECTION, CLOSE.toString());
            connection.sendRequest(request, client.createFullCallback(reference, latch));
            latch.await();
            final AsyncResult<AsyncResponse> ar = reference.get();
            if (ar.succeeded()) {
                Assert.assertEquals(Http2ClientTest.message, ar.result().getResponseBody());
                Assert.assertTrue(((ar.result().getResponseTime()) > 0));
                System.out.println(("responseTime = " + (ar.result().getResponseTime())));
            } else {
                ar.cause().printStackTrace();
            }
            Assert.assertEquals(false, connection.isOpen());
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testSingleAsych() throws Exception {
        callApiAsync();
    }

    @Test
    public void testGetFormDataString() throws UnsupportedEncodingException {
        // This is to reproduce and fix #172
        Map<String, String> params = new HashMap<>();
        params.put("scope", "a b c d");
        String s = Http2Client.getFormDataString(params);
        Assert.assertEquals("scope=a%20b%20c%20d", s);
    }

    @Test
    public void testAsyncAboutToExpire() throws InterruptedException, ExecutionException {
        for (int i = 0; i < 1; i++) {
            callApiAsyncMultiThread(4);
            Http2ClientTest.logger.info(("called times: " + i));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Test
    public void testAsyncExpired() throws InterruptedException, ExecutionException {
        for (int i = 0; i < 1; i++) {
            callApiAsyncMultiThread(4);
            Http2ClientTest.logger.info(("called times: " + i));
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Test
    public void testMixed() throws InterruptedException, ExecutionException {
        for (int i = 0; i < 1; i++) {
            callApiAsyncMultiThread(4);
            Http2ClientTest.logger.info(("called times: " + i));
            try {
                int sleepTime = (Http2ClientTest.randInt(1, 6)) * 1000;
                if (sleepTime > 3000) {
                    sleepTime = 6000;
                } else {
                    sleepTime = 1000;
                }
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Test
    public void server_identity_check_positive_case() throws Exception {
        final Http2Client client = Http2ClientTest.createClient();
        SSLContext context = Http2Client.createSSLContext("trustedNames.local");
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        final ClientConnection connection = client.connect(new URI("https://localhost:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        Assert.assertTrue(connection.isOpen());
        IoUtils.safeClose(connection);
    }

    @Test(expected = ClosedChannelException.class)
    public void server_identity_check_negative_case() throws Exception {
        final Http2Client client = Http2ClientTest.createClient();
        SSLContext context = Http2Client.createSSLContext("trustedNames.negativeTest");
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        client.connect(new URI("https://localhost:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        // should not be reached
        Assert.fail();
    }

    @Test(expected = ClosedChannelException.class)
    public void standard_https_hostname_check_kicks_in_if_trustednames_are_empty() throws Exception {
        final Http2Client client = Http2ClientTest.createClient();
        SSLContext context = Http2Client.createSSLContext("trustedNames.empty");
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        client.connect(new URI("https://127.0.0.1:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        // should not be reached
        Assert.fail();
    }

    @Test(expected = ClosedChannelException.class)
    public void standard_https_hostname_check_kicks_in_if_trustednames_are_not_used_or_not_provided() throws Exception {
        final Http2Client client = Http2ClientTest.createClient();
        SSLContext context = Http2Client.createSSLContext();
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        client.connect(new URI("https://127.0.0.1:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        // should not be reached
        Assert.fail();
    }

    @Test
    public void invalid_hostname_is_accepted_if_verifyhostname_is_disabled() throws Exception {
        final Http2Client client = Http2ClientTest.createClient();
        SSLContext context = Http2ClientTest.createTestSSLContext(false, null);
        XnioSsl ssl = new io.undertow.protocols.ssl.UndertowXnioSsl(Http2ClientTest.worker.getXnio(), OptionMap.EMPTY, BUFFER_POOL, context);
        final ClientConnection connection = client.connect(new URI("https://127.0.0.1:7778"), Http2ClientTest.worker, ssl, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        Assert.assertTrue(connection.isOpen());
        IoUtils.safeClose(connection);
    }
}

