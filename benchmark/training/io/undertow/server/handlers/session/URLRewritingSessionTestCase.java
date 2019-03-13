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
package io.undertow.server.handlers.session;


import StatusCodes.OK;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * basic test of in memory session functionality
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class URLRewritingSessionTestCase {
    public static final String COUNT = "count";

    @Test
    public void testURLRewriting() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/notamatchingpath;foo=bar"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String url = HttpClientUtils.readResponse(result);
            Header[] header = result.getHeaders(URLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("0", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(URLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("1", header[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(URLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("2", header[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testURLRewritingWithQueryParameters() throws IOException {
        TestHttpClient client = new TestHttpClient();
        client.setCookieStore(new BasicCookieStore());
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/notamatchingpath?a=b;c"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String url = HttpClientUtils.readResponse(result);
            Header[] header = result.getHeaders(URLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("0", header[0].getValue());
            Assert.assertEquals("b;c", result.getHeaders("a")[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(URLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("1", header[0].getValue());
            Assert.assertEquals("b;c", result.getHeaders("a")[0].getValue());
            get = new HttpGet(url);
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            url = HttpClientUtils.readResponse(result);
            header = result.getHeaders(URLRewritingSessionTestCase.COUNT);
            Assert.assertEquals("2", header[0].getValue());
            Assert.assertEquals("b;c", result.getHeaders("a")[0].getValue());
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

