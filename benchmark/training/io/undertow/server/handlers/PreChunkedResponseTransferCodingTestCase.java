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
package io.undertow.server.handlers;


import StatusCodes.OK;
import io.undertow.server.ServerConnection;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@HttpOneOnly
public class PreChunkedResponseTransferCodingTestCase {
    private static final String MESSAGE = "My HTTP Request!";

    private static volatile String message;

    private static volatile String chunkedMessage;

    private static volatile ServerConnection connection;

    @Test
    public void sendHttpRequest() throws IOException {
        Assume.assumeFalse(DefaultServer.isH2());// this test will still run under h2-upgrade, but will fail

        PreChunkedResponseTransferCodingTestCase.connection = null;
        HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
        TestHttpClient client = new TestHttpClient();
        try {
            PreChunkedResponseTransferCodingTestCase.generateMessage(0);
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(PreChunkedResponseTransferCodingTestCase.message, HttpClientUtils.readResponse(result));
            PreChunkedResponseTransferCodingTestCase.generateMessage(1);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(PreChunkedResponseTransferCodingTestCase.message, HttpClientUtils.readResponse(result));
            PreChunkedResponseTransferCodingTestCase.generateMessage(1000);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            Assert.assertEquals(PreChunkedResponseTransferCodingTestCase.message, HttpClientUtils.readResponse(result));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

