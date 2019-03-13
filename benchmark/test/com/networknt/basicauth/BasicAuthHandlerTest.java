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
package com.networknt.basicauth;


import Headers.AUTHORIZATION;
import Headers.HOST;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Http2Client.SSL;
import Http2Client.WORKER;
import Methods.GET;
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
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;


/**
 * This is a test class for BasicAuthHandler. It tests all scenarios of positive and negative.
 *
 * @author Steve Hu
 */
public class BasicAuthHandlerTest {
    static final Logger logger = LoggerFactory.getLogger(BasicAuthHandlerTest.class);

    static Undertow server = null;

    @Test
    public void testWithRightCredentials() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(AUTHORIZATION, ("BASIC " + (BasicAuthHandlerTest.encodeCredentials("user1", "user1pass"))));
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            Assert.assertNotNull(reference.get().getAttachment(RESPONSE_BODY));
        }
    }

    @Test
    public void testEncryptedPassword() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(AUTHORIZATION, ("BASIC " + (BasicAuthHandlerTest.encodeCredentials("user2", "password"))));
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(200, statusCode);
        if (statusCode == 200) {
            Assert.assertNotNull(reference.get().getAttachment(RESPONSE_BODY));
        }
    }

    @Test
    public void testMissingToken() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(401, statusCode);
        if (statusCode == 401) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10002", status.getCode());
        }
    }

    @Test
    public void testInvalidBasicHeaderCredentialInfo() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(AUTHORIZATION, ("BASIC " + (BasicAuthHandlerTest.encodeCredentialsFullFormat("user1", "user1pass", "/"))));
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(401, statusCode);
        if (statusCode == 401) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10046", status.getCode());
        }
    }

    @Test
    public void testInvalidBasicHeaderPrefixText() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(AUTHORIZATION, ("Bearer " + (BasicAuthHandlerTest.encodeCredentials("user1", "user1pass"))));
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(401, statusCode);
        if (statusCode == 401) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10046", status.getCode());
        }
    }

    @Test
    public void testInvalidUsername() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(AUTHORIZATION, ("BASIC " + (BasicAuthHandlerTest.encodeCredentials("user3", "user1pass"))));
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(401, statusCode);
        if (statusCode == 401) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10047", status.getCode());
        }
    }

    @Test
    public void testInvalidPassword() throws Exception {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI("http://localhost:17352"), WORKER, SSL, BUFFER_POOL, EMPTY).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/v2/pet").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            request.getRequestHeaders().put(AUTHORIZATION, ("BASIC " + (BasicAuthHandlerTest.encodeCredentials("user2", "ppp"))));
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            BasicAuthHandlerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        Assert.assertEquals(401, statusCode);
        if (statusCode == 401) {
            Status status = Config.getInstance().getMapper().readValue(reference.get().getAttachment(RESPONSE_BODY), Status.class);
            Assert.assertNotNull(status);
            Assert.assertEquals("ERR10047", status.getCode());
        }
    }
}

