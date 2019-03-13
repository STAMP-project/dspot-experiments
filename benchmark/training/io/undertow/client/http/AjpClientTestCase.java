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
package io.undertow.client.http;


import Headers.CLOSE;
import Headers.CONNECTION;
import Headers.HOST;
import Headers.TRANSFER_ENCODING;
import Methods.GET;
import Methods.POST;
import OptionMap.Builder;
import OptionMap.EMPTY;
import Options.KEEP_ALIVE;
import Options.TCP_NODELAY;
import Options.WORKER_IO_THREADS;
import Options.WORKER_NAME;
import io.undertow.Undertow;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientExchange;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.client.UndertowClient;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.util.AttachmentKey;
import io.undertow.util.StringReadChannelListener;
import io.undertow.util.StringWriteChannelListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.FutureResult;
import org.xnio.IoUtils;
import org.xnio.OptionMap;
import org.xnio.XnioWorker;


/**
 *
 *
 * @author Emanuel Muckenhuber
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
public class AjpClientTestCase {
    private static final String message = "Hello World!";

    public static final String MESSAGE = "/message";

    public static final String POST = "/post";

    private static final int AJP_PORT = (DefaultServer.getHostPort()) + 10;

    private static XnioWorker worker;

    private static Undertow undertow;

    private static final OptionMap DEFAULT_OPTIONS;

    private static final URI ADDRESS;

    private static final AttachmentKey<String> RESPONSE_BODY = AttachmentKey.create(String.class);

    static {
        final OptionMap.Builder builder = OptionMap.builder().set(WORKER_IO_THREADS, 8).set(TCP_NODELAY, true).set(KEEP_ALIVE, true).set(WORKER_NAME, "Client");
        DEFAULT_OPTIONS = builder.getMap();
        try {
            ADDRESS = new URI(((("ajp://" + (DefaultServer.getHostAddress())) + ":") + (AjpClientTestCase.AJP_PORT)));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSimpleBasic() throws Exception {
        // 
        final UndertowClient client = AjpClientTestCase.createClient();
        final List<ClientResponse> responses = new CopyOnWriteArrayList<>();
        final CountDownLatch latch = new CountDownLatch(10);
        final ClientConnection connection = client.connect(AjpClientTestCase.ADDRESS, AjpClientTestCase.worker, DefaultServer.getBufferPool(), EMPTY).get();
        try {
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        final ClientRequest request = new ClientRequest().setMethod(GET).setPath(AjpClientTestCase.MESSAGE);
                        request.getRequestHeaders().put(HOST, DefaultServer.getHostAddress());
                        connection.sendRequest(request, createClientCallback(responses, latch));
                    }
                }
            });
            latch.await(10, TimeUnit.SECONDS);
            Assert.assertEquals(10, responses.size());
            for (final ClientResponse response : responses) {
                Assert.assertEquals(AjpClientTestCase.message, response.getAttachment(AjpClientTestCase.RESPONSE_BODY));
            }
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testSendPing() throws Exception {
        // 
        final UndertowClient client = AjpClientTestCase.createClient();
        final List<ClientResponse> responses = new CopyOnWriteArrayList<>();
        final FutureResult<Boolean> result = new FutureResult();
        final CountDownLatch latch = new CountDownLatch(3);
        final ClientConnection connection = client.connect(AjpClientTestCase.ADDRESS, AjpClientTestCase.worker, DefaultServer.getBufferPool(), EMPTY).get();
        Assert.assertTrue(connection.isPingSupported());
        try {
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(GET).setPath(AjpClientTestCase.MESSAGE);
                    request.getRequestHeaders().put(HOST, DefaultServer.getHostAddress());
                    connection.sendRequest(request, createClientCallback(responses, latch));
                    connection.sendPing(new ClientConnection.PingListener() {
                        @Override
                        public void acknowledged() {
                            result.setResult(true);
                            latch.countDown();
                        }

                        @Override
                        public void failed(IOException e) {
                            result.setException(e);
                            latch.countDown();
                        }
                    }, 5, TimeUnit.SECONDS);
                    connection.sendRequest(request, createClientCallback(responses, latch));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
            Assert.assertEquals(2, responses.size());
            Assert.assertTrue(result.getIoFuture().get());
            for (final ClientResponse response : responses) {
                Assert.assertEquals(AjpClientTestCase.message, response.getAttachment(AjpClientTestCase.RESPONSE_BODY));
            }
            // now try a failed ping
            try {
                AjpClientTestCase.undertow.stop();
                final FutureResult<Boolean> failResult = new FutureResult();
                connection.getIoThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        connection.sendPing(new ClientConnection.PingListener() {
                            @Override
                            public void acknowledged() {
                                failResult.setResult(true);
                            }

                            @Override
                            public void failed(IOException e) {
                                failResult.setException(e);
                            }
                        }, 4, TimeUnit.SECONDS);
                    }
                });
                try {
                    failResult.getIoFuture().get();
                    Assert.fail("ping should have failed");
                } catch (IOException e) {
                    // ignored
                }
            } finally {
                AjpClientTestCase.undertow.start();
            }
        } finally {
            IoUtils.safeClose(connection);
        }
    }

    @Test
    public void testPostRequest() throws Exception {
        // 
        final UndertowClient client = AjpClientTestCase.createClient();
        final String postMessage = "This is a post request";
        final List<String> responses = new CopyOnWriteArrayList<>();
        final CountDownLatch latch = new CountDownLatch(10);
        final ClientConnection connection = client.connect(AjpClientTestCase.ADDRESS, AjpClientTestCase.worker, DefaultServer.getBufferPool(), EMPTY).get();
        try {
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        final ClientRequest request = new ClientRequest().setMethod(Methods.POST).setPath(AjpClientTestCase.POST);
                        request.getRequestHeaders().put(HOST, DefaultServer.getHostAddress());
                        request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                        connection.sendRequest(request, new io.undertow.client.ClientCallback<ClientExchange>() {
                            @Override
                            public void completed(ClientExchange result) {
                                new StringWriteChannelListener(postMessage).setup(result.getRequestChannel());
                                result.setResponseListener(new io.undertow.client.ClientCallback<ClientExchange>() {
                                    @Override
                                    public void completed(ClientExchange result) {
                                        new StringReadChannelListener(DefaultServer.getBufferPool()) {
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
                }
            });
            latch.await(10, TimeUnit.SECONDS);
            Assert.assertEquals(10, responses.size());
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
        final UndertowClient client = AjpClientTestCase.createClient();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection = client.connect(AjpClientTestCase.ADDRESS, AjpClientTestCase.worker, DefaultServer.getBufferPool(), EMPTY).get();
        try {
            ClientRequest request = new ClientRequest().setPath(AjpClientTestCase.MESSAGE).setMethod(GET);
            request.getRequestHeaders().put(HOST, DefaultServer.getHostAddress());
            final List<ClientResponse> responses = new CopyOnWriteArrayList<>();
            request.getRequestHeaders().add(CONNECTION, CLOSE.toString());
            connection.sendRequest(request, createClientCallback(responses, latch));
            latch.await();
            final ClientResponse response = responses.iterator().next();
            Assert.assertEquals(AjpClientTestCase.message, response.getAttachment(AjpClientTestCase.RESPONSE_BODY));
            Assert.assertEquals(false, connection.isOpen());
        } finally {
            IoUtils.safeClose(connection);
        }
    }
}

