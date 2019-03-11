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
package com.networknt.traceability;


import Headers.CONTENT_TYPE;
import Headers.HOST;
import Headers.TRANSFER_ENCODING;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Http2Client.SSL;
import Http2Client.WORKER;
import HttpStringConstants.TRACEABILITY_ID;
import Methods.GET;
import Methods.POST;
import OptionMap.EMPTY;
import com.networknt.client.Http2Client;
import com.networknt.exception.ClientException;
import io.undertow.Undertow;
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
 */
public class TraceabilityHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(TraceabilityHandlerTest.class);

    static Undertow server = null;

    @Test
    public void testGetWithTid() throws Exception {
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
            request.getRequestHeaders().put(TRACEABILITY_ID, "12345");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            TraceabilityHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            String body = reference.get().getAttachment(RESPONSE_BODY);
            Assert.assertNotNull(body);
            Assert.assertEquals("get", body);
            String tid = reference.get().getResponseHeaders().getFirst(TRACEABILITY_ID);
            Assert.assertEquals("12345", tid);
        }
    }

    @Test
    public void testGetWithoutTid() throws Exception {
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
            // request.getRequestHeaders().put(Constants.TRACEABILITY_ID, "12345");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            TraceabilityHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            String body = reference.get().getAttachment(RESPONSE_BODY);
            Assert.assertNotNull(body);
            Assert.assertEquals("get", body);
            String tid = reference.get().getResponseHeaders().getFirst(TRACEABILITY_ID);
            Assert.assertNull(tid);
        }
    }

    @Test
    public void testPostWithTid() throws Exception {
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
                    request.getRequestHeaders().put(TRACEABILITY_ID, "12345");
                    request.getRequestHeaders().put(TRANSFER_ENCODING, "chunked");
                    connection.sendRequest(request, client.createClientCallback(reference, latch, post));
                }
            });
            latch.await(10, TimeUnit.SECONDS);
        } catch (Exception e) {
            TraceabilityHandlerTest.logger.error("IOException: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            String body = reference.get().getAttachment(RESPONSE_BODY);
            Assert.assertNotNull(body);
            Assert.assertEquals("post", body);
            String tid = reference.get().getResponseHeaders().getFirst(TRACEABILITY_ID);
            Assert.assertEquals("12345", tid);
        }
    }
}

