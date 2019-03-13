/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.server.initialization;


import HttpServletResponse.SC_OK;
import HttpServletResponse.SC_UNAUTHORIZED;
import MediaType.TEXT_PLAIN;
import com.google.common.base.Throwables;
import com.google.common.util.concurrent.ListenableFuture;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.druid.java.util.http.client.Request;
import org.apache.druid.java.util.http.client.response.InputStreamResponseHandler;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.druid.server.initialization.BaseJettyTest.DummyAuthFilter.AUTH_HDR;
import static org.apache.druid.server.initialization.BaseJettyTest.DummyAuthFilter.SECRET_USER;


public class JettyTest extends BaseJettyTest {
    @Test
    public void testGzipResponseCompression() throws Exception {
        final URL url = new URL((("http://localhost:" + (port)) + "/default"));
        final HttpURLConnection get = ((HttpURLConnection) (url.openConnection()));
        get.setRequestProperty("Accept-Encoding", "gzip");
        Assert.assertEquals("gzip", get.getContentEncoding());
        Assert.assertEquals(BaseJettyTest.DEFAULT_RESPONSE_CONTENT, IOUtils.toString(new GZIPInputStream(get.getInputStream()), StandardCharsets.UTF_8));
        final HttpURLConnection post = ((HttpURLConnection) (url.openConnection()));
        post.setRequestProperty("Accept-Encoding", "gzip");
        post.setRequestMethod("POST");
        Assert.assertEquals("gzip", post.getContentEncoding());
        Assert.assertEquals(BaseJettyTest.DEFAULT_RESPONSE_CONTENT, IOUtils.toString(new GZIPInputStream(post.getInputStream()), StandardCharsets.UTF_8));
        final HttpURLConnection getNoGzip = ((HttpURLConnection) (url.openConnection()));
        Assert.assertNotEquals("gzip", getNoGzip.getContentEncoding());
        Assert.assertEquals(BaseJettyTest.DEFAULT_RESPONSE_CONTENT, IOUtils.toString(getNoGzip.getInputStream(), StandardCharsets.UTF_8));
        final HttpURLConnection postNoGzip = ((HttpURLConnection) (url.openConnection()));
        postNoGzip.setRequestMethod("POST");
        Assert.assertNotEquals("gzip", postNoGzip.getContentEncoding());
        Assert.assertEquals(BaseJettyTest.DEFAULT_RESPONSE_CONTENT, IOUtils.toString(postNoGzip.getInputStream(), StandardCharsets.UTF_8));
    }

    @Test
    public void testThreadNotStuckOnException() throws Exception {
        final CountDownLatch latch = new CountDownLatch(1);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    ListenableFuture<InputStream> go = client.go(new Request(HttpMethod.GET, new URL((("http://localhost:" + (port)) + "/exception/exception"))), new InputStreamResponseHandler());
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(go.get(), writer, "utf-8");
                } catch (IOException e) {
                    // Expected.
                } catch (Throwable t) {
                    Throwables.propagate(t);
                }
                latch.countDown();
            }
        });
        latch.await(5, TimeUnit.SECONDS);
    }

    @Test
    public void testExtensionAuthFilter() throws Exception {
        URL url = new URL((("http://localhost:" + (port)) + "/default"));
        HttpURLConnection get = ((HttpURLConnection) (url.openConnection()));
        get.setRequestProperty(AUTH_HDR, SECRET_USER);
        Assert.assertEquals(SC_OK, get.getResponseCode());
        get = ((HttpURLConnection) (url.openConnection()));
        get.setRequestProperty(AUTH_HDR, "hacker");
        Assert.assertEquals(SC_UNAUTHORIZED, get.getResponseCode());
    }

    @Test
    public void testGzipRequestDecompression() throws Exception {
        String text = "hello";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(out)) {
            gzipOutputStream.write(text.getBytes(Charset.defaultCharset()));
        }
        Request request = new Request(HttpMethod.POST, new URL((("http://localhost:" + (port)) + "/return")));
        request.setHeader("Content-Encoding", "gzip");
        request.setContent(TEXT_PLAIN, out.toByteArray());
        Assert.assertEquals(text, new String(IOUtils.toByteArray(client.go(request, new InputStreamResponseHandler()).get()), Charset.defaultCharset()));
    }
}

