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
package io.undertow.server;


import Headers.CONTENT_LENGTH_STRING;
import Headers.TRANSFER_ENCODING_STRING;
import StatusCodes.BAD_REQUEST;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpOneOnly;
import io.undertow.testutils.ProxyIgnore;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@ProxyIgnore
@HttpOneOnly
public class InvalidHtpRequestTestCase {
    @Test
    public void testInvalidCharacterInMethod() throws IOException {
        final TestHttpClient client = new TestHttpClient();
        try {
            HttpRequestBase method = new HttpRequestBase() {
                @Override
                public String getMethod() {
                    return "GET;POST";
                }

                @Override
                public URI getURI() {
                    try {
                        return new URI(DefaultServer.getDefaultServerURL());
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            HttpResponse result = client.execute(method);
            Assert.assertEquals(BAD_REQUEST, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testInvalidCharacterInHeader() throws IOException {
        final TestHttpClient client = new TestHttpClient();
        try {
            HttpRequestBase method = new HttpGet(DefaultServer.getDefaultServerURL());
            method.addHeader("fake;header", "value");
            HttpResponse result = client.execute(method);
            Assert.assertEquals(BAD_REQUEST, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultipleContentLengths() throws IOException {
        final TestHttpClient client = new TestHttpClient();
        try {
            HttpRequestBase method = new HttpGet(DefaultServer.getDefaultServerURL());
            method.addHeader(CONTENT_LENGTH_STRING, "0");
            method.addHeader(CONTENT_LENGTH_STRING, "10");
            HttpResponse result = client.execute(method);
            Assert.assertEquals(BAD_REQUEST, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testContentLengthAndTransferEncoding() throws IOException {
        final TestHttpClient client = new TestHttpClient();
        try {
            HttpRequestBase method = new HttpGet(DefaultServer.getDefaultServerURL());
            method.addHeader(CONTENT_LENGTH_STRING, "0");
            method.addHeader(TRANSFER_ENCODING_STRING, "chunked");
            HttpResponse result = client.execute(method);
            Assert.assertEquals(BAD_REQUEST, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testMultipleTransferEncoding() throws IOException {
        final TestHttpClient client = new TestHttpClient();
        try {
            HttpRequestBase method = new HttpGet(DefaultServer.getDefaultServerURL());
            method.addHeader(TRANSFER_ENCODING_STRING, "chunked");
            method.addHeader(TRANSFER_ENCODING_STRING, "gzip, chunked");
            HttpResponse result = client.execute(method);
            Assert.assertEquals(BAD_REQUEST, result.getStatusLine().getStatusCode());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

