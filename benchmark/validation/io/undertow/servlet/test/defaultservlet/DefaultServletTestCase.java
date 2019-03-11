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
package io.undertow.servlet.test.defaultservlet;


import Headers.CONTENT_RANGE_STRING;
import Headers.CONTENT_TYPE_STRING;
import Headers.IF_RANGE_STRING;
import Headers.RANGE_STRING;
import StatusCodes.NOT_MODIFIED;
import StatusCodes.OK;
import StatusCodes.PARTIAL_CONTENT;
import StatusCodes.REQUEST_RANGE_NOT_SATISFIABLE;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import io.undertow.util.DateUtils;
import java.io.IOException;
import java.util.Date;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class DefaultServletTestCase {
    @Test
    public void testSimpleResource() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/index.html"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertTrue(response.contains("Redirected home page"));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testRangeRequest() throws IOException, InterruptedException {
        String uri = (DefaultServer.getDefaultServerURL()) + "/servletContext/range.txt";
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/index.html"));
            get.addHeader(RANGE_STRING, "bytes=2-3");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("--", response);
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=3-100");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("3456789", response);
            Assert.assertEquals("bytes 3-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=3-9");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("3456789", response);
            Assert.assertEquals("bytes 3-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=2-3");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("23", response);
            Assert.assertEquals("bytes 2-3/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=0-0");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("0", response);
            Assert.assertEquals("bytes 0-0/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=1-");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("123456789", response);
            Assert.assertEquals("bytes 1-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=0-");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("0123456789", response);
            Assert.assertEquals("bytes 0-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=9-");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("9", response);
            Assert.assertEquals("bytes 9-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=-1");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("9", response);
            Assert.assertEquals("bytes 9-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=-100");
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("0123456789", response);
            Assert.assertEquals("bytes 0-9/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=99-100");
            result = client.execute(get);
            Assert.assertEquals(REQUEST_RANGE_NOT_SATISFIABLE, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("", response);
            Assert.assertEquals("bytes */10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=2-1");
            result = client.execute(get);
            Assert.assertEquals(REQUEST_RANGE_NOT_SATISFIABLE, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("", response);
            Assert.assertEquals("bytes */10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            // test if-range
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=2-3");
            get.addHeader(IF_RANGE_STRING, DateUtils.toDateString(new Date(((System.currentTimeMillis()) + 1000))));
            result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("23", response);
            Assert.assertEquals("bytes 2-3/10", result.getFirstHeader(CONTENT_RANGE_STRING).getValue());
            get = new HttpGet(uri);
            get.addHeader(RANGE_STRING, "bytes=2-3");
            get.addHeader(IF_RANGE_STRING, DateUtils.toDateString(new Date(0)));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = EntityUtils.toString(result.getEntity());
            Assert.assertEquals("0123456789", response);
            Assert.assertNull(result.getFirstHeader(CONTENT_RANGE_STRING));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testResourceWithFilter() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/filterpath/filtered.txt"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("Hello Stuart", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testDisallowedResource() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/disallowed.sh"));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testDirectoryListing() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/path"));
            HttpResponse result = client.execute(get);
            Header contentType = result.getFirstHeader(CONTENT_TYPE_STRING);
            Assert.assertNotNull(contentType);
            Assert.assertTrue(contentType.getValue().contains("text/html"));
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testIfMoodifiedSince() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/index.html"));
            // UNDERTOW-458
            get.addHeader("date-header", "Fri, 10 Oct 2014 21:35:55 CEST");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertTrue(response.contains("Redirected home page"));
            String lm = result.getHeaders("Last-Modified")[0].getValue();
            System.out.println(lm);
            Assert.assertTrue(lm.endsWith("GMT"));
            get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/index.html"));
            get.addHeader("IF-Modified-Since", lm);
            result = client.execute(get);
            Assert.assertEquals(NOT_MODIFIED, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertFalse(response.contains("Redirected home page"));
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

