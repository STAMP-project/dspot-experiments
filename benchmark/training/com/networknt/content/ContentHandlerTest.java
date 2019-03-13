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
package com.networknt.content;


import Headers.HOST;
import Http2Client.BUFFER_POOL;
import Http2Client.SSL;
import Http2Client.WORKER;
import Methods.GET;
import OptionMap.EMPTY;
import com.networknt.client.Http2Client;
import com.networknt.exception.ClientException;
import io.undertow.Undertow;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;


/**
 * Created by Ricardo Pina Arellano on 13/06/18.
 */
public class ContentHandlerTest {
    private static final Logger logger = LoggerFactory.getLogger(ContentHandlerTest.class);

    private static Undertow server = null;

    private static final String url = "http://localhost:8080";

    @Test
    public void testTextPlainContentType() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(ContentHandlerTest.url), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final String defaultContentType = "text/plain";
        final String defaultHeader = "Content-Type";
        try {
            final ClientRequest request = new ClientRequest().setPath("/").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(new HttpString(defaultHeader), defaultContentType);
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            ContentHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        final int statusCode = reference.get().getResponseCode();
        final HeaderMap headerMap = reference.get().getResponseHeaders();
        final String header = headerMap.getFirst(defaultHeader);
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(header);
        Assert.assertEquals(header, defaultContentType);
    }

    @Test
    public void testXMLContentType() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(ContentHandlerTest.url), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final String defaultContentType = "application/xml";
        final String defaultHeader = "Content-Type";
        try {
            final ClientRequest request = new ClientRequest().setPath("/xml").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(new HttpString(defaultHeader), defaultContentType);
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            ContentHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        final int statusCode = reference.get().getResponseCode();
        final HeaderMap headerMap = reference.get().getResponseHeaders();
        final String header = headerMap.getFirst(defaultHeader);
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(header);
        Assert.assertEquals(header, defaultContentType);
    }

    @Test
    public void testJSONContentType() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(ContentHandlerTest.url), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        final String defaultContentType = "application/json";
        final String defaultHeader = "Content-Type";
        try {
            final ClientRequest request = new ClientRequest().setPath("/json").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(new HttpString(defaultHeader), defaultContentType);
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            ContentHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        final int statusCode = reference.get().getResponseCode();
        final HeaderMap headerMap = reference.get().getResponseHeaders();
        final String header = headerMap.getFirst(defaultHeader);
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(header);
        Assert.assertEquals(header, defaultContentType);
    }

    @Test
    public void testDefaultContentType() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(ContentHandlerTest.url), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            final ClientRequest request = new ClientRequest().setPath("/json").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            ContentHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        final int statusCode = reference.get().getResponseCode();
        final HeaderMap headerMap = reference.get().getResponseHeaders();
        final String header = headerMap.getFirst("Content-Type");
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(header);
        Assert.assertEquals(header, "application/json");
    }
}

