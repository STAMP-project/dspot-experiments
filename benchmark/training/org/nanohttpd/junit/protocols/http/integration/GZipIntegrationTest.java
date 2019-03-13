/**
 * #%L
 * NanoHttpd-Core
 * %%
 * Copyright (C) 2012 - 2015 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package org.nanohttpd.junit.protocols.http.integration;


import Status.OK;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DecompressingHttpClient;
import org.apache.http.util.EntityUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.nanohttpd.protocols.http.IHTTPSession;
import org.nanohttpd.protocols.http.NanoHTTPD;
import org.nanohttpd.protocols.http.response.Response;


public class GZipIntegrationTest extends IntegrationTestBase<GZipIntegrationTest.TestServer> {
    public static class TestServer extends NanoHTTPD {
        public Response response;

        public TestServer() {
            super(8192);
        }

        @Override
        public Response serve(IHTTPSession session) {
            return response.setUseGzip(true);
        }
    }

    @Test
    public void contentEncodingShouldBeAddedToFixedLengthResponses() throws IOException {
        testServer.response = Response.newFixedLengthResponse("This is a test");
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "gzip");
        HttpResponse response = httpclient.execute(request);
        Header contentEncoding = response.getFirstHeader("content-encoding");
        Assert.assertNotNull("Content-Encoding should be set", contentEncoding);
        Assert.assertEquals("gzip", contentEncoding.getValue());
    }

    @Test
    public void contentEncodingShouldBeAddedToChunkedResponses() throws IOException {
        InputStream data = new ByteArrayInputStream("This is a test".getBytes("UTF-8"));
        testServer.response = Response.newChunkedResponse(OK, "text/plain", data);
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "gzip");
        HttpResponse response = httpclient.execute(request);
        Header contentEncoding = response.getFirstHeader("content-encoding");
        Assert.assertNotNull("Content-Encoding should be set", contentEncoding);
        Assert.assertEquals("gzip", contentEncoding.getValue());
    }

    @Test
    public void shouldFindCorrectAcceptEncodingAmongMany() throws IOException {
        testServer.response = Response.newFixedLengthResponse("This is a test");
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "deflate,gzip");
        HttpResponse response = httpclient.execute(request);
        Header contentEncoding = response.getFirstHeader("content-encoding");
        Assert.assertNotNull("Content-Encoding should be set", contentEncoding);
        Assert.assertEquals("gzip", contentEncoding.getValue());
    }

    @Test
    public void contentLengthShouldBeRemovedFromZippedResponses() throws IOException {
        testServer.response = Response.newFixedLengthResponse("This is a test");
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "gzip");
        HttpResponse response = httpclient.execute(request);
        Header contentLength = response.getFirstHeader("content-length");
        Assert.assertNull("Content-Length should not be set when gzipping response", contentLength);
    }

    @Test
    public void fixedLengthContentIsEncodedProperly() throws IOException {
        testServer.response = Response.newFixedLengthResponse("This is a test");
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "gzip");
        HttpResponse response = new DecompressingHttpClient(httpclient).execute(request);
        Assert.assertEquals("This is a test", EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void chunkedContentIsEncodedProperly() throws IOException {
        InputStream data = new ByteArrayInputStream("This is a test".getBytes("UTF-8"));
        testServer.response = Response.newChunkedResponse(OK, "text/plain", data);
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "gzip");
        HttpResponse response = new DecompressingHttpClient(httpclient).execute(request);
        Assert.assertEquals("This is a test", EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void noGzipWithoutAcceptEncoding() throws IOException {
        testServer.response = Response.newFixedLengthResponse("This is a test");
        HttpGet request = new HttpGet("http://localhost:8192/");
        HttpResponse response = httpclient.execute(request);
        Header contentEncoding = response.getFirstHeader("content-encoding");
        Assert.assertThat(contentEncoding, CoreMatchers.is(CoreMatchers.nullValue()));
        Assert.assertEquals("This is a test", EntityUtils.toString(response.getEntity()));
    }

    @Test
    public void contentShouldNotBeGzippedIfContentLengthIsAddedManually() throws IOException {
        testServer.response = Response.newFixedLengthResponse("This is a test");
        testServer.response.addHeader("Content-Length", ("" + ("This is a test".getBytes("UTF-8").length)));
        HttpGet request = new HttpGet("http://localhost:8192/");
        request.addHeader("Accept-encoding", "gzip");
        HttpResponse response = httpclient.execute(request);
        Header contentEncoding = response.getFirstHeader("content-encoding");
        Assert.assertNull("Content-Encoding should not be set when manually setting content-length", contentEncoding);
        Assert.assertEquals("This is a test", EntityUtils.toString(response.getEntity()));
    }
}

