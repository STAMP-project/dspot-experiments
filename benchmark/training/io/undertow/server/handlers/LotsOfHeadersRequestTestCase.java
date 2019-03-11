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
import UndertowOptions.MAX_HEADERS;
import io.undertow.testutils.AjpIgnore;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.StatusCodes;
import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.OptionMap;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
@AjpIgnore(apacheOnly = true)
public class LotsOfHeadersRequestTestCase {
    private static final String HEADER = "HEADER";

    private static final String MESSAGE = "Hello Header";

    private static final int DEFAULT_MAX_HEADERS = 200;

    private static final int TEST_MAX_HEADERS = 20;

    @Test
    @AjpIgnore
    public void testLotsOfHeadersInRequest_Default_Ok() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            for (int i = 0; i < (LotsOfHeadersRequestTestCase.getDefaultMaxHeaders()); ++i) {
                get.addHeader(((LotsOfHeadersRequestTestCase.HEADER) + i), ((LotsOfHeadersRequestTestCase.MESSAGE) + i));
            }
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            for (int i = 0; i < (LotsOfHeadersRequestTestCase.getDefaultMaxHeaders()); ++i) {
                Header[] header = result.getHeaders(((LotsOfHeadersRequestTestCase.HEADER) + i));
                Assert.assertEquals(((LotsOfHeadersRequestTestCase.MESSAGE) + i), header[0].getValue());
            }
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testLotsOfHeadersInRequest_Default_BadRequest() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            // add request headers more than MAX_HEADERS
            for (int i = 0; i < ((LotsOfHeadersRequestTestCase.getDefaultMaxHeaders()) + 1); ++i) {
                get.addHeader(((LotsOfHeadersRequestTestCase.HEADER) + i), ((LotsOfHeadersRequestTestCase.MESSAGE) + i));
            }
            HttpResponse result = client.execute(get);
            Assert.assertEquals((DefaultServer.isH2() ? StatusCodes.SERVICE_UNAVAILABLE : StatusCodes.BAD_REQUEST), result.getStatusLine().getStatusCode());// this is not great, but the HTTP/2 impl sends a stream error which is translated to a 503. Should not be a big deal in practice

        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    @AjpIgnore
    public void testLotsOfHeadersInRequest_MaxHeaders_Ok() throws IOException {
        OptionMap existing = DefaultServer.getUndertowOptions();
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            for (int i = 0; i < (LotsOfHeadersRequestTestCase.getTestMaxHeaders()); ++i) {
                get.addHeader(((LotsOfHeadersRequestTestCase.HEADER) + i), ((LotsOfHeadersRequestTestCase.MESSAGE) + i));
            }
            DefaultServer.setUndertowOptions(OptionMap.create(MAX_HEADERS, LotsOfHeadersRequestTestCase.TEST_MAX_HEADERS));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            for (int i = 0; i < (LotsOfHeadersRequestTestCase.getTestMaxHeaders()); ++i) {
                Header[] header = result.getHeaders(((LotsOfHeadersRequestTestCase.HEADER) + i));
                Assert.assertEquals(((LotsOfHeadersRequestTestCase.MESSAGE) + i), header[0].getValue());
            }
        } finally {
            DefaultServer.setUndertowOptions(existing);
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testLotsOfHeadersInRequest_MaxHeaders_BadRequest() throws IOException {
        OptionMap existing = DefaultServer.getUndertowOptions();
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/path"));
            // add request headers more than MAX_HEADERS
            for (int i = 0; i < ((LotsOfHeadersRequestTestCase.getTestMaxHeaders()) + 1); ++i) {
                get.addHeader(((LotsOfHeadersRequestTestCase.HEADER) + i), ((LotsOfHeadersRequestTestCase.MESSAGE) + i));
            }
            DefaultServer.setUndertowOptions(OptionMap.create(MAX_HEADERS, LotsOfHeadersRequestTestCase.TEST_MAX_HEADERS));
            HttpResponse result = client.execute(get);
            Assert.assertEquals((DefaultServer.isH2() ? StatusCodes.SERVICE_UNAVAILABLE : StatusCodes.BAD_REQUEST), result.getStatusLine().getStatusCode());
        } finally {
            DefaultServer.setUndertowOptions(existing);
            client.getConnectionManager().shutdown();
        }
    }
}

