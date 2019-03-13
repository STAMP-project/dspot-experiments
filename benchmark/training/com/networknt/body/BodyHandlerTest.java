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
package com.networknt.body;


import Headers.CONTENT_TYPE;
import Headers.HOST;
import Headers.TRANSFER_ENCODING;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Http2Client.SSL;
import Http2Client.WORKER;
import Methods.GET;
import Methods.POST;
import OptionMap.EMPTY;
import com.networknt.client.Http2Client;
import com.networknt.config.Config;
import com.networknt.exception.ClientException;
import com.networknt.status.Status;
import io.undertow.Undertow;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;


/**
 * Created by steve on 23/09/16.
 * Updated on 07/12/18
 */
public class BodyHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(BodyHandlerTest.class);

    static Undertow server = null;

    @Test
    public void testGet() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/get").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BodyHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(RESPONSE_BODY);
        Assert.assertEquals(200, statusCode);
        Assert.assertEquals("nobody", body);
    }

    @Test
    public void testPostNonJson() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "post";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        // as content type and body is mismatched, the body will be ignored.
        Assert.assertEquals(400, statusCode);
        if (statusCode == 400) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10015", status.getCode());
        }
    }

    @Test
    public void testPostInvalidJson() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "{post";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        // as content type and body is mismatched, the body will be ignored.
        Assert.assertEquals(400, statusCode);
        if (statusCode == 400) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10015", status.getCode());
        }
    }

    @Test
    public void testPostJsonList() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "[{\"key1\":\"value1\"}, {\"key2\":\"value2\"}]";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("[{key1=value1},{key2=value2}]", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostJsonListEmpty() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "[]";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("[]", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostJsonMap() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "{\"key1\":\"value1\", \"key2\":\"value2\"}";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("{key1:value1,key2:value2}", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostJsonMapEmpty() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "{}";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("{}", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostJsonMapWithoutContentTypeHeader() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "{\"key\":\"value\"}";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    // request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("nobody", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostFormWithoutContentTypeHeader() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "name=value";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    // request.getRequestHeaders().put(Headers.CONTENT_TYPE, "application/x-www-form-urlencoded");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("nobody", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostFormUrlEncoded() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "key1=value1&key2=value2%20with%20space&keylist[]=1&keylist[]=2";
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "application/x-www-form-urlencoded");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("{key1:[value1],key2:[value2 with space],keylist[]:[1,2]}", reference.get().getAttachment(RESPONSE_BODY));
    }

    @Test
    public void testPostFormMultipart() throws Exception {
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:8080"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        try {
            String post = "--???\r\n" + ((((((("Content-Disposition: form-data; name=\"key1\"\r\n" + "\r\n") + "value1\r\n") + "--???\r\n") + "Content-Disposition: form-data; name=\"key2\"\r\n") + "\r\n") + "value2\r\n") + "--???--\r\n");
            connection.getIoThread().execute(new Runnable() {
                @Override
                public void run() {
                    final ClientRequest request = new ClientRequest().setMethod(POST).setPath("/post");
                    request.getRequestHeaders().put(HOST, "localhost");
                    request.getRequestHeaders().put(CONTENT_TYPE, "multipart/form-data; boundary=\"???\"");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            BodyHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        Assert.assertEquals("{key1:[value1],key2:[value2]}", reference.get().getAttachment(RESPONSE_BODY));
    }
}

