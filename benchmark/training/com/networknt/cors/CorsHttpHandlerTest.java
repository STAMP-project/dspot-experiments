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
package com.networknt.cors;


import Headers.HOST;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Http2Client.SSL;
import Http2Client.WORKER;
import Methods.OPTIONS;
import UndertowOptions.ENABLE_HTTP2;
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
import org.xnio.OptionMap;


/**
 * Created by stevehu on 2017-02-17.
 */
public class CorsHttpHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(CorsHttpHandlerTest.class);

    static Undertow server = null;

    @Test
    public void testOptionsWrongOrigin() throws Exception {
        String url = "http://localhost:8080";
        Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(url), WORKER, SSL, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/").setMethod(OPTIONS);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(new HttpString("Origin"), "http://example.com");
            request.getRequestHeaders().put(new HttpString("Access-Control-Request-Method"), "POST");
            request.getRequestHeaders().put(new HttpString("Access-Control-Request-Headers"), "X-Requested-With");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            CorsHttpHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(RESPONSE_BODY);
        HeaderMap headerMap = reference.get().getResponseHeaders();
        String header = headerMap.getFirst("Access-Control-Allow-Origin");
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            Assert.assertNull(header);
        }
    }

    @Test
    public void testOptionsCorrectOrigin() throws Exception {
        String url = "http://localhost:8080";
        Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(url), WORKER, SSL, BUFFER_POOL, OptionMap.create(ENABLE_HTTP2, true)).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/").setMethod(OPTIONS);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(new HttpString("Origin"), "http://localhost");
            request.getRequestHeaders().put(new HttpString("Access-Control-Request-Method"), "POST");
            request.getRequestHeaders().put(new HttpString("Access-Control-Request-Headers"), "X-Requested-With");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            CorsHttpHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(RESPONSE_BODY);
        HeaderMap headerMap = reference.get().getResponseHeaders();
        String header = headerMap.getFirst("Access-Control-Allow-Origin");
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            Assert.assertNotNull(header);
        }
    }
}

