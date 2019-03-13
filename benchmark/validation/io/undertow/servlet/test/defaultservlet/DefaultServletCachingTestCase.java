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


import StatusCodes.NOT_FOUND;
import StatusCodes.OK;
import StatusCodes.PARTIAL_CONTENT;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.session.SecureRandomSessionIdGenerator;
import io.undertow.testutils.DefaultServer;
import io.undertow.testutils.HttpClientUtils;
import io.undertow.testutils.TestHttpClient;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xnio.BufferAllocator;


/**
 *
 *
 * @author Stuart Douglas
 */
@RunWith(DefaultServer.class)
public class DefaultServletCachingTestCase {
    private static final int MAX_FILE_SIZE = 20;

    private static final int METADATA_MAX_AGE = 2000;

    public static final String DIR_NAME = "cacheTest";

    static Path tmpDir;

    static DirectBufferCache dataCache = new DirectBufferCache(1000, 10, ((1000 * 10) * 1000), BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, DefaultServletCachingTestCase.METADATA_MAX_AGE);

    @Test
    public void testFileExistanceCheckCached() throws IOException, InterruptedException {
        TestHttpClient client = new TestHttpClient();
        String fileName = (new SecureRandomSessionIdGenerator().createSessionId()) + ".html";
        try {
            HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(NOT_FOUND, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            Path f = DefaultServletCachingTestCase.tmpDir.resolve(fileName);
            Files.write(f, "hello".getBytes());
            get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            result = client.execute(get);
            Assert.assertEquals(NOT_FOUND, result.getStatusLine().getStatusCode());
            HttpClientUtils.readResponse(result);
            Thread.sleep(DefaultServletCachingTestCase.METADATA_MAX_AGE);
            get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("hello", response);
            Files.delete(f);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testFileContentsCached() throws IOException, InterruptedException {
        TestHttpClient client = new TestHttpClient();
        String fileName = "hello.html";
        Path f = DefaultServletCachingTestCase.tmpDir.resolve(fileName);
        Files.write(f, "hello".getBytes());
        try {
            for (int i = 0; i < 10; ++i) {
                HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
                HttpResponse result = client.execute(get);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                String response = HttpClientUtils.readResponse(result);
                Assert.assertEquals("hello", response);
            }
            Files.write(f, "hello world".getBytes());
            HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("hello", response);
            Thread.sleep(DefaultServletCachingTestCase.METADATA_MAX_AGE);
            get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("hello world", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testFileContentsCachedWithFilter() throws IOException, InterruptedException {
        TestHttpClient client = new TestHttpClient();
        String fileName = "hello.txt";
        Path f = DefaultServletCachingTestCase.tmpDir.resolve(fileName);
        Files.write(f, "hello".getBytes());
        try {
            for (int i = 0; i < 10; ++i) {
                HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
                HttpResponse result = client.execute(get);
                Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
                String response = HttpClientUtils.readResponse(result);
                Assert.assertEquals("FILTER_TEXT hello", response);
            }
            Files.write(f, "hello world".getBytes());
            HttpGet get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            HttpResponse result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("FILTER_TEXT hello", response);
            Thread.sleep(DefaultServletCachingTestCase.METADATA_MAX_AGE);
            get = new HttpGet((((DefaultServer.getDefaultServerURL()) + "/servletContext/") + fileName));
            result = client.execute(get);
            Assert.assertEquals(OK, result.getStatusLine().getStatusCode());
            response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("FILTER_TEXT hello world", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    @Test
    public void testRangeRequest() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String fileName = "range.html";
            Path f = DefaultServletCachingTestCase.tmpDir.resolve(fileName);
            Files.write(f, "hello".getBytes());
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/range.html"));
            get.addHeader("range", "bytes=2-3");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("ll", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }

    /**
     * Regression test for UNDERTOW-1444.
     *
     * Tested file is bigger then {@value #MAX_FILE_SIZE} bytes.
     */
    @Test
    public void testRangeRequestFileNotInCache() throws IOException {
        TestHttpClient client = new TestHttpClient();
        try {
            String fileName = "range_not_in_cache.html";
            Path f = DefaultServletCachingTestCase.tmpDir.resolve(fileName);
            Files.write(f, "hello world and once again hello world".getBytes());
            HttpGet get = new HttpGet(((DefaultServer.getDefaultServerURL()) + "/servletContext/range_not_in_cache.html"));
            get.addHeader("range", "bytes=2-3");
            HttpResponse result = client.execute(get);
            Assert.assertEquals(PARTIAL_CONTENT, result.getStatusLine().getStatusCode());
            String response = HttpClientUtils.readResponse(result);
            Assert.assertEquals("ll", response);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}

