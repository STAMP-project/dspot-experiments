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
package com.networknt.server;


import Headers.HOST;
import Http2Client.BUFFER_POOL;
import Http2Client.RESPONSE_BODY;
import Http2Client.SSL;
import Http2Client.WORKER;
import Methods.GET;
import UndertowOptions.ENABLE_HTTP2;
import com.networknt.client.Http2Client;
import com.networknt.exception.ApiException;
import com.networknt.exception.ClientException;
import io.undertow.client.ClientConnection;
import io.undertow.client.ClientRequest;
import io.undertow.client.ClientResponse;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnio.IoUtils;
import org.xnio.OptionMap;


public class ServerTest {
    @ClassRule
    public static TestServer server = TestServer.getInstance();

    static final Logger logger = LoggerFactory.getLogger(TestServer.class);

    static final boolean enableHttp2 = ServerTest.server.getServerConfig().isEnableHttp2();

    static final boolean enableHttps = ServerTest.server.getServerConfig().isEnableHttps();

    static final int httpPort = ServerTest.server.getServerConfig().getHttpPort();

    static final int httpsPort = ServerTest.server.getServerConfig().getHttpsPort();

    static final String url = ((ServerTest.enableHttp2) || (ServerTest.enableHttps)) ? "https://localhost:" + (ServerTest.httpsPort) : "http://localhost:" + (ServerTest.httpPort);

    @Test
    public void getHandlerTest() throws ApiException, ClientException {
        final Http2Client client = Http2Client.getInstance();
        final CountDownLatch latch = new CountDownLatch(1);
        final ClientConnection connection;
        try {
            connection = client.connect(new URI(ServerTest.url), WORKER, SSL, BUFFER_POOL, (ServerTest.enableHttp2 ? OptionMap.create(ENABLE_HTTP2, true) : OptionMap.EMPTY)).get();
        } catch (Exception e) {
            throw new ClientException(e);
        }
        final AtomicReference<ClientResponse> reference = new AtomicReference<>();
        try {
            ClientRequest request = new ClientRequest().setPath("/test").setMethod(GET);
            request.getRequestHeaders().put(HOST, "localhost");
            connection.sendRequest(request, client.createClientCallback(reference, latch));
            latch.await();
        } catch (Exception e) {
            ServerTest.logger.error("Exception: ", e);
            throw new ClientException(e);
        } finally {
            IoUtils.safeClose(connection);
        }
        int statusCode = reference.get().getResponseCode();
        String body = reference.get().getAttachment(RESPONSE_BODY);
        Assert.assertEquals(200, statusCode);
        Assert.assertNotNull(body);
    }
}

