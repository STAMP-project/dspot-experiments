/**
 *
 */
/**
 * ========================================================================
 */
/**
 * Copyright (c) 1995-2019 Mort Bay Consulting Pty. Ltd.
 */
/**
 * ------------------------------------------------------------------------
 */
/**
 * All rights reserved. This program and the accompanying materials
 */
/**
 * are made available under the terms of the Eclipse Public License v1.0
 */
/**
 * and Apache License v2.0 which accompanies this distribution.
 */
/**
 *
 */
/**
 * The Eclipse Public License is available at
 */
/**
 * http://www.eclipse.org/legal/epl-v10.html
 */
/**
 *
 */
/**
 * The Apache License v2.0 is available at
 */
/**
 * http://www.opensource.org/licenses/apache2.0.php
 */
/**
 *
 */
/**
 * You may elect to redistribute this code under either of these licenses.
 */
/**
 * ========================================================================
 */
/**
 *
 */
package org.eclipse.jetty.proxy;


import HttpHeader.CONNECTION;
import HttpHeader.CONTENT_ENCODING;
import HttpHeader.WWW_AUTHENTICATE;
import HttpHeaderValue.CLOSE;
import HttpStatus.BAD_GATEWAY_502;
import HttpStatus.OK_200;
import HttpStatus.REQUEST_TIMEOUT_408;
import HttpStatus.UNAUTHORIZED_401;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.GZIPInputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.client.util.BytesContentProvider;
import org.eclipse.jetty.client.util.DeferredContentProvider;
import org.eclipse.jetty.client.util.FutureResponseListener;
import org.eclipse.jetty.server.HttpChannel;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Utf8StringBuilder;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.log.StacklessLogging;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.OS;

import static ContentTransformer.IDENTITY;


public class AsyncMiddleManServletTest {
    private static final Logger LOG = Log.getLogger(AsyncMiddleManServletTest.class);

    private static final String PROXIED_HEADER = "X-Proxied";

    private HttpClient client;

    private Server proxy;

    private ServerConnector proxyConnector;

    private Server server;

    private ServerConnector serverConnector;

    private StacklessLogging stackless;

    @Test
    public void testZeroContentLength() throws Exception {
        startServer(new EchoHttpServlet());
        startProxy(new AsyncMiddleManServlet());
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void testClientRequestSmallContentKnownLengthGzipped() throws Exception {
        // Lengths smaller than the buffer sizes preserve the Content-Length header.
        testClientRequestContentKnownLengthGzipped(1024, false);
    }

    @Test
    public void testClientRequestLargeContentKnownLengthGzipped() throws Exception {
        // Lengths bigger than the buffer sizes will force chunked mode.
        testClientRequestContentKnownLengthGzipped((1024 * 1024), true);
    }

    @Test
    public void testServerResponseContentKnownLengthGzipped() throws Exception {
        byte[] bytes = new byte[1024];
        new Random().nextBytes(bytes);
        final byte[] gzipBytes = gzip(bytes);
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setHeader(CONTENT_ENCODING.asString(), "gzip");
                response.getOutputStream().write(gzipBytes);
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new GZIPContentTransformer(IDENTITY);
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertArrayEquals(bytes, response.getContent());
    }

    @Test
    public void testTransformUpstreamAndDownstreamKnownContentLengthGzipped() throws Exception {
        String data = "<a href=\"http://google.com\">Google</a>";
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        startServer(new EchoHttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.setHeader(CONTENT_ENCODING.asString(), "gzip");
                super.service(request, response);
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.HrefTransformer.Client());
            }

            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.HrefTransformer.Server());
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).header(CONTENT_ENCODING, "gzip").content(new BytesContentProvider(gzip(bytes))).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertArrayEquals(bytes, response.getContent());
    }

    @Test
    public void testTransformGzippedHead() throws Exception {
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setHeader(CONTENT_ENCODING.asString(), "gzip");
                String sample = "<a href=\"http://webtide.com/\">Webtide</a>\n<a href=\"http://google.com\">Google</a>\n";
                byte[] bytes = sample.getBytes(StandardCharsets.UTF_8);
                ServletOutputStream out = response.getOutputStream();
                out.write(gzip(bytes));
                // create a byte buffer larger enough to create 2 (or more) transforms.
                byte[] randomFiller = new byte[64 * 1024];
                /* fill with nonsense
                Using random data to ensure compressed buffer size is large
                enough to trigger at least 2 transform() events.
                 */
                new Random().nextBytes(randomFiller);
                out.write(gzip(randomFiller));
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.HeadTransformer());
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).header(CONTENT_ENCODING, "gzip").timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        String expectedStr = "<a href=\"http://webtide.com/\">Webtide</a>";
        byte[] expected = expectedStr.getBytes(StandardCharsets.UTF_8);
        Assertions.assertArrayEquals(expected, response.getContent());
    }

    @Test
    public void testManySequentialTransformations() throws Exception {
        for (int i = 0; i < 8; ++i)
            testTransformUpstreamAndDownstreamKnownContentLengthGzipped();

    }

    @Test
    public void testUpstreamTransformationBufferedGzipped() throws Exception {
        startServer(new EchoHttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
                response.setHeader(CONTENT_ENCODING.asString(), "gzip");
                super.service(request, response);
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.BufferingContentTransformer());
            }
        });
        startClient();
        DeferredContentProvider content = new DeferredContentProvider();
        Request request = client.newRequest("localhost", serverConnector.getLocalPort());
        FutureResponseListener listener = new FutureResponseListener(request);
        request.header(CONTENT_ENCODING, "gzip").content(content).send(listener);
        byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.UTF_8);
        content.offer(ByteBuffer.wrap(gzip(bytes)));
        sleep(1000);
        content.close();
        ContentResponse response = listener.get(5, TimeUnit.SECONDS);
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertArrayEquals(bytes, response.getContent());
    }

    @Test
    public void testDownstreamTransformationBufferedGzipped() throws Exception {
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setHeader(CONTENT_ENCODING.asString(), "gzip");
                ServletInputStream input = request.getInputStream();
                ServletOutputStream output = response.getOutputStream();
                int read;
                while ((read = input.read()) >= 0) {
                    output.write(read);
                    output.flush();
                } 
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.BufferingContentTransformer());
            }
        });
        startClient();
        byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.UTF_8);
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).header(CONTENT_ENCODING, "gzip").content(new BytesContentProvider(gzip(bytes))).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertArrayEquals(bytes, response.getContent());
    }

    @Test
    public void testDiscardUpstreamAndDownstreamKnownContentLengthGzipped() throws Exception {
        final byte[] bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".getBytes(StandardCharsets.UTF_8);
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                // decode input stream thru gzip
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                IO.copy(new GZIPInputStream(request.getInputStream()), bos);
                // ensure decompressed is 0 length
                Assertions.assertEquals(0, bos.toByteArray().length);
                response.setHeader(CONTENT_ENCODING.asString(), "gzip");
                response.getOutputStream().write(gzip(bytes));
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.DiscardContentTransformer());
            }

            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new GZIPContentTransformer(new AsyncMiddleManServletTest.DiscardContentTransformer());
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).header(CONTENT_ENCODING, "gzip").content(new BytesContentProvider(gzip(bytes))).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(0, response.getContent().length);
    }

    @Test
    public void testUpstreamTransformationThrowsBeforeCommittingProxyRequest() throws Exception {
        startServer(new EchoHttpServlet());
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return ( input, finished, output) -> {
                    throw new NullPointerException("explicitly_thrown_by_test");
                };
            }
        });
        startClient();
        byte[] bytes = new byte[1024];
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).content(new BytesContentProvider(bytes)).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(500, response.getStatus());
    }

    @Test
    public void testUpstreamTransformationThrowsAfterCommittingProxyRequest() throws Exception {
        try (StacklessLogging scope = new StacklessLogging(HttpChannel.class)) {
            startServer(new EchoHttpServlet());
            startProxy(new AsyncMiddleManServlet() {
                @Override
                protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                    return new ContentTransformer() {
                        private int count;

                        @Override
                        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
                            if ((++(count)) < 2)
                                output.add(input);
                            else
                                throw new NullPointerException("explicitly_thrown_by_test");

                        }
                    };
                }
            });
            startClient();
            final CountDownLatch latch = new CountDownLatch(1);
            DeferredContentProvider content = new DeferredContentProvider();
            client.newRequest("localhost", serverConnector.getLocalPort()).content(content).send(( result) -> {
                if ((result.isSucceeded()) && ((result.getResponse().getStatus()) == 502))
                    latch.countDown();

            });
            content.offer(ByteBuffer.allocate(512));
            sleep(1000);
            content.offer(ByteBuffer.allocate(512));
            content.close();
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testDownstreamTransformationThrowsAtOnContent() throws Exception {
        testDownstreamTransformationThrows(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                // To trigger the test failure we need that onContent()
                // is called twice, so the second time the test throws.
                ServletOutputStream output = response.getOutputStream();
                output.write(new byte[512]);
                output.flush();
                output.write(new byte[512]);
                output.flush();
            }
        });
    }

    @Test
    public void testDownstreamTransformationThrowsAtOnSuccess() throws Exception {
        testDownstreamTransformationThrows(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                // To trigger the test failure we need that onContent()
                // is called only once, so the the test throws from onSuccess().
                ServletOutputStream output = response.getOutputStream();
                output.write(new byte[512]);
                output.flush();
            }
        });
    }

    @Test
    public void testLargeChunkedBufferedDownstreamTransformation() throws Exception {
        testLargeChunkedBufferedDownstreamTransformation(false);
    }

    @Test
    public void testLargeChunkedGzippedBufferedDownstreamTransformation() throws Exception {
        testLargeChunkedBufferedDownstreamTransformation(true);
    }

    @Test
    public void testDownstreamTransformationKnownContentLengthDroppingLastChunk() throws Exception {
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                byte[] chunk = new byte[1024];
                int contentLength = 2 * (chunk.length);
                response.setContentLength(contentLength);
                ServletOutputStream output = response.getOutputStream();
                output.write(chunk);
                output.flush();
                sleep(1000);
                output.write(chunk);
            }
        });
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return ( input, finished, output) -> {
                    if (!finished)
                        output.add(input);

                };
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void testClientRequestReadFailsOnFirstRead() throws Exception {
        startServer(new EchoHttpServlet());
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected int readClientRequestContent(ServletInputStream input, byte[] buffer) throws IOException {
                throw new IOException("explicitly_thrown_by_test");
            }
        });
        startClient();
        final CountDownLatch latch = new CountDownLatch(1);
        DeferredContentProvider content = new DeferredContentProvider();
        client.newRequest("localhost", serverConnector.getLocalPort()).content(content).send(( result) -> {
            System.err.println(result);
            if ((result.getResponse().getStatus()) == 500)
                latch.countDown();

        });
        content.offer(ByteBuffer.allocate(512));
        sleep(1000);
        content.offer(ByteBuffer.allocate(512));
        content.close();
        Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testClientRequestReadFailsOnSecondRead() throws Exception {
        try (StacklessLogging scope = new StacklessLogging(HttpChannel.class)) {
            startServer(new EchoHttpServlet());
            startProxy(new AsyncMiddleManServlet() {
                private int count;

                @Override
                protected int readClientRequestContent(ServletInputStream input, byte[] buffer) throws IOException {
                    if ((++(count)) < 2)
                        return super.readClientRequestContent(input, buffer);
                    else
                        throw new IOException("explicitly_thrown_by_test");

                }
            });
            startClient();
            final CountDownLatch latch = new CountDownLatch(1);
            DeferredContentProvider content = new DeferredContentProvider();
            client.newRequest("localhost", serverConnector.getLocalPort()).content(content).send(( result) -> {
                if ((result.getResponse().getStatus()) == 502)
                    latch.countDown();

            });
            content.offer(ByteBuffer.allocate(512));
            sleep(1000);
            content.offer(ByteBuffer.allocate(512));
            content.close();
            Assertions.assertTrue(latch.await(5, TimeUnit.SECONDS));
        }
    }

    @Test
    public void testProxyResponseWriteFailsOnFirstWrite() throws Exception {
        testProxyResponseWriteFails(1);
    }

    @Test
    public void testProxyResponseWriteFailsOnSecondWrite() throws Exception {
        testProxyResponseWriteFails(2);
    }

    @Test
    public void testAfterContentTransformer() throws Exception {
        final String key0 = "id";
        long value0 = 1;
        final String key1 = "channel";
        String value1 = "foo";
        final String json = ((((((("{ \"" + key0) + "\":") + value0) + ", \"") + key1) + "\":\"") + value1) + "\" }";
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            }
        });
        final String key2 = "c";
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new AfterContentTransformer() {
                    @Override
                    public boolean transform(Source source, Sink sink) throws IOException {
                        InputStream input = source.getInputStream();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> obj = ((Map<String, Object>) (JSON.parse(new InputStreamReader(input, StandardCharsets.UTF_8))));
                        // Transform the object.
                        obj.put(key2, obj.remove(key1));
                        try (OutputStream output = sink.getOutputStream()) {
                            output.write(JSON.toString(obj).getBytes(StandardCharsets.UTF_8));
                            return true;
                        }
                    }
                };
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> obj = ((Map<String, Object>) (JSON.parse(response.getContentAsString())));
        Assertions.assertNotNull(obj);
        Assertions.assertEquals(2, obj.size());
        Assertions.assertEquals(value0, obj.get(key0));
        Assertions.assertEquals(value1, obj.get(key2));
    }

    @Test
    public void testAfterContentTransformerMemoryInputStreamReset() throws Exception {
        testAfterContentTransformerInputStreamReset(false);
    }

    @Test
    public void testAfterContentTransformerDiskInputStreamReset() throws Exception {
        testAfterContentTransformerInputStreamReset(true);
    }

    @Test
    public void testAfterContentTransformerOverflowingToDisk() throws Exception {
        // Make sure the temporary directory we use exists and it's empty.
        final Path targetTestsDir = prepareTargetTestsDir();
        final String key0 = "id";
        long value0 = 1;
        final String key1 = "channel";
        String value1 = "foo";
        final String json = ((((((("{ \"" + key0) + "\":") + value0) + ", \"") + key1) + "\":\"") + value1) + "\" }";
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.getOutputStream().write(json.getBytes(StandardCharsets.UTF_8));
            }
        });
        final String inputPrefix = "in_";
        final String outputPrefix = "out_";
        final String key2 = "c";
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                AfterContentTransformer transformer = new AfterContentTransformer() {
                    @Override
                    public boolean transform(Source source, Sink sink) throws IOException {
                        InputStream input = source.getInputStream();
                        @SuppressWarnings("unchecked")
                        Map<String, Object> obj = ((Map<String, Object>) (JSON.parse(new InputStreamReader(input, StandardCharsets.UTF_8))));
                        // Transform the object.
                        obj.put(key2, obj.remove(key1));
                        try (OutputStream output = sink.getOutputStream()) {
                            output.write(JSON.toString(obj).getBytes(StandardCharsets.UTF_8));
                            return true;
                        }
                    }
                };
                transformer.setOverflowDirectory(targetTestsDir);
                int maxBufferSize = (json.length()) / 4;
                transformer.setMaxInputBufferSize(maxBufferSize);
                transformer.setInputFilePrefix(inputPrefix);
                transformer.setMaxOutputBufferSize(maxBufferSize);
                transformer.setOutputFilePrefix(outputPrefix);
                return transformer;
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        Map<String, Object> obj = ((Map<String, Object>) (JSON.parse(response.getContentAsString())));
        Assertions.assertNotNull(obj);
        Assertions.assertEquals(2, obj.size());
        Assertions.assertEquals(value0, obj.get(key0));
        Assertions.assertEquals(value1, obj.get(key2));
        // Make sure the files do not exist.
        try (DirectoryStream<Path> paths = Files.newDirectoryStream(targetTestsDir, (inputPrefix + "*.*"))) {
            Assertions.assertFalse(paths.iterator().hasNext());
        }
        // File deletion is delayed on windows, testing for deletion is not going to work
        if (!(OS.WINDOWS.isCurrentOs())) {
            try (DirectoryStream<Path> paths = Files.newDirectoryStream(targetTestsDir, (outputPrefix + "*.*"))) {
                Assertions.assertFalse(paths.iterator().hasNext());
            }
        }
    }

    @Test
    public void testAfterContentTransformerClosingFilesOnClientRequestException() throws Exception {
        final Path targetTestsDir = prepareTargetTestsDir();
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                IO.copy(request.getInputStream(), IO.getNullStream());
            }
        });
        final CountDownLatch destroyLatch = new CountDownLatch(1);
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new AfterContentTransformer() {
                    {
                        setOverflowDirectory(targetTestsDir);
                        setMaxInputBufferSize(0);
                        setMaxOutputBufferSize(0);
                    }

                    @Override
                    public boolean transform(Source source, Sink sink) throws IOException {
                        IO.copy(source.getInputStream(), sink.getOutputStream());
                        return true;
                    }

                    @Override
                    public void destroy() {
                        super.destroy();
                        destroyLatch.countDown();
                    }
                };
            }
        });
        long idleTimeout = 1000;
        proxyConnector.setIdleTimeout(idleTimeout);
        startClient();
        // Send only part of the content; the proxy will idle timeout.
        final byte[] data = new byte[]{ 'c', 'a', 'f', 'e' };
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).content(new BytesContentProvider(data) {
            @Override
            public long getLength() {
                return (data.length) + 1;
            }
        }).timeout((5 * idleTimeout), TimeUnit.MILLISECONDS).send();
        Assertions.assertTrue(destroyLatch.await((5 * idleTimeout), TimeUnit.MILLISECONDS));
        Assertions.assertEquals(REQUEST_TIMEOUT_408, response.getStatus());
    }

    @Test
    public void testAfterContentTransformerClosingFilesOnServerResponseException() throws Exception {
        final Path targetTestsDir = prepareTargetTestsDir();
        final CountDownLatch serviceLatch = new CountDownLatch(1);
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                response.setHeader(CONNECTION.asString(), CLOSE.asString());
                response.setContentLength(2);
                // Send only part of the content.
                OutputStream output = response.getOutputStream();
                output.write('x');
                output.flush();
                serviceLatch.countDown();
            }
        });
        final CountDownLatch destroyLatch = new CountDownLatch(1);
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new AfterContentTransformer() {
                    {
                        setOverflowDirectory(targetTestsDir);
                        setMaxInputBufferSize(0);
                        setMaxOutputBufferSize(0);
                    }

                    @Override
                    public boolean transform(Source source, Sink sink) throws IOException {
                        IO.copy(source.getInputStream(), sink.getOutputStream());
                        return true;
                    }

                    @Override
                    public void destroy() {
                        super.destroy();
                        destroyLatch.countDown();
                    }
                };
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertTrue(serviceLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertTrue(destroyLatch.await(5, TimeUnit.SECONDS));
        Assertions.assertEquals(BAD_GATEWAY_502, response.getStatus());
    }

    @Test
    public void testAfterContentTransformerDoNotReadSourceDoNotTransform() throws Exception {
        testAfterContentTransformerDoNoTransform(false, false);
    }

    @Test
    public void testAfterContentTransformerReadSourceDoNotTransform() throws Exception {
        testAfterContentTransformerDoNoTransform(true, true);
    }

    @Test
    public void testServer401() throws Exception {
        startServer(new HttpServlet() {
            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) {
                response.setStatus(UNAUTHORIZED_401);
                response.setHeader(WWW_AUTHENTICATE.asString(), "Basic realm=\"test\"");
            }
        });
        final AtomicBoolean transformed = new AtomicBoolean();
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return new AfterContentTransformer() {
                    @Override
                    public boolean transform(Source source, Sink sink) {
                        transformed.set(true);
                        return false;
                    }
                };
            }
        });
        startClient();
        ContentResponse response = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(UNAUTHORIZED_401, response.getStatus());
        Assertions.assertFalse(transformed.get());
    }

    @Test
    public void testProxyRequestHeadersSentWhenDiscardingContent() throws Exception {
        startServer(new EchoHttpServlet());
        final CountDownLatch proxyRequestLatch = new CountDownLatch(1);
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new AsyncMiddleManServletTest.DiscardContentTransformer();
            }

            @Override
            protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
                proxyRequestLatch.countDown();
                super.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
            }
        });
        startClient();
        DeferredContentProvider content = new DeferredContentProvider();
        Request request = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).content(content);
        FutureResponseListener listener = new FutureResponseListener(request);
        request.send(listener);
        // Send one chunk of content, the proxy request must not be sent.
        ByteBuffer chunk1 = ByteBuffer.allocate(1024);
        content.offer(chunk1);
        Assertions.assertFalse(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        // Send another chunk of content, the proxy request must not be sent.
        ByteBuffer chunk2 = ByteBuffer.allocate(512);
        content.offer(chunk2);
        Assertions.assertFalse(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        // Finish the content, request must be sent.
        content.close();
        Assertions.assertTrue(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        ContentResponse response = listener.get(5, TimeUnit.SECONDS);
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertEquals(0, response.getContent().length);
    }

    @Test
    public void testProxyRequestHeadersNotSentUntilContent() throws Exception {
        startServer(new EchoHttpServlet());
        final CountDownLatch proxyRequestLatch = new CountDownLatch(1);
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new AsyncMiddleManServletTest.BufferingContentTransformer();
            }

            @Override
            protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
                proxyRequestLatch.countDown();
                super.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
            }
        });
        startClient();
        DeferredContentProvider content = new DeferredContentProvider();
        Request request = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).content(content);
        FutureResponseListener listener = new FutureResponseListener(request);
        request.send(listener);
        // Send one chunk of content, the proxy request must not be sent.
        ByteBuffer chunk1 = ByteBuffer.allocate(1024);
        content.offer(chunk1);
        Assertions.assertFalse(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        // Send another chunk of content, the proxy request must not be sent.
        ByteBuffer chunk2 = ByteBuffer.allocate(512);
        content.offer(chunk2);
        Assertions.assertFalse(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        // Finish the content, request must be sent.
        content.close();
        Assertions.assertTrue(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        ContentResponse response = listener.get(5, TimeUnit.SECONDS);
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertEquals(((chunk1.capacity()) + (chunk2.capacity())), response.getContent().length);
    }

    @Test
    public void testProxyRequestHeadersNotSentUntilFirstContent() throws Exception {
        startServer(new EchoHttpServlet());
        final CountDownLatch proxyRequestLatch = new CountDownLatch(1);
        startProxy(new AsyncMiddleManServlet() {
            @Override
            protected ContentTransformer newClientRequestContentTransformer(HttpServletRequest clientRequest, Request proxyRequest) {
                return new ContentTransformer() {
                    private ByteBuffer buffer;

                    @Override
                    public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
                        // Buffer only the first chunk.
                        if ((buffer) == null) {
                            buffer = ByteBuffer.allocate(input.remaining());
                            buffer.put(input).flip();
                        } else
                            if (buffer.hasRemaining()) {
                                output.add(buffer);
                                output.add(input);
                            } else {
                                output.add(input);
                            }

                    }
                };
            }

            @Override
            protected void sendProxyRequest(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Request proxyRequest) {
                proxyRequestLatch.countDown();
                super.sendProxyRequest(clientRequest, proxyResponse, proxyRequest);
            }
        });
        startClient();
        DeferredContentProvider content = new DeferredContentProvider();
        Request request = client.newRequest("localhost", serverConnector.getLocalPort()).timeout(5, TimeUnit.SECONDS).content(content);
        FutureResponseListener listener = new FutureResponseListener(request);
        request.send(listener);
        // Send one chunk of content, the proxy request must not be sent.
        ByteBuffer chunk1 = ByteBuffer.allocate(1024);
        content.offer(chunk1);
        Assertions.assertFalse(proxyRequestLatch.await(1, TimeUnit.SECONDS));
        // Send another chunk of content, the proxy request must be sent.
        ByteBuffer chunk2 = ByteBuffer.allocate(512);
        content.offer(chunk2);
        Assertions.assertTrue(proxyRequestLatch.await(5, TimeUnit.SECONDS));
        // Finish the content.
        content.close();
        ContentResponse response = listener.get(5, TimeUnit.SECONDS);
        Assertions.assertEquals(OK_200, response.getStatus());
        Assertions.assertEquals(((chunk1.capacity()) + (chunk2.capacity())), response.getContent().length);
    }

    @Test
    public void testTransparentProxyWithIdentityContentTransformer() throws Exception {
        final String target = "/test";
        startServer(new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
                if ((req.getHeader("Via")) != null)
                    resp.addHeader(AsyncMiddleManServletTest.PROXIED_HEADER, "true");

                resp.setStatus((target.equals(req.getRequestURI()) ? 200 : 404));
            }
        });
        final String proxyTo = "http://localhost:" + (serverConnector.getLocalPort());
        AsyncMiddleManServlet proxyServlet = new AsyncMiddleManServlet.Transparent() {
            @Override
            protected ContentTransformer newServerResponseContentTransformer(HttpServletRequest clientRequest, HttpServletResponse proxyResponse, Response serverResponse) {
                return ContentTransformer.IDENTITY;
            }
        };
        Map<String, String> initParams = new HashMap<>();
        initParams.put("proxyTo", proxyTo);
        startProxy(proxyServlet, initParams);
        startClient();
        // Make the request to the proxy, it should transparently forward to the server
        ContentResponse response = client.newRequest("localhost", proxyConnector.getLocalPort()).path(target).timeout(5, TimeUnit.SECONDS).send();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertTrue(response.getHeaders().containsKey(AsyncMiddleManServletTest.PROXIED_HEADER));
    }

    private abstract static class HrefTransformer implements AsyncMiddleManServlet.ContentTransformer {
        private static final String PREFIX = "http://localhost/q=";

        private final AsyncMiddleManServletTest.HrefParser parser = new AsyncMiddleManServletTest.HrefParser();

        private final List<ByteBuffer> matches = new ArrayList<>();

        private boolean matching;

        @Override
        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) throws IOException {
            int position = input.position();
            while (input.hasRemaining()) {
                boolean match = parser.parse(input);
                // Get the slice of what has been parsed so far.
                int limit = input.limit();
                input.limit(input.position());
                input.position(position);
                ByteBuffer slice = input.slice();
                input.position(input.limit());
                input.limit(limit);
                position = input.position();
                if (matching) {
                    if (match) {
                        ByteBuffer copy = ByteBuffer.allocate(slice.remaining());
                        copy.put(slice).flip();
                        matches.add(copy);
                    } else {
                        matching = false;
                        // Transform the matches.
                        Utf8StringBuilder builder = new Utf8StringBuilder();
                        for (ByteBuffer buffer : matches)
                            builder.append(buffer);

                        String transformed = transform(builder.toString());
                        output.add(ByteBuffer.wrap(transformed.getBytes(StandardCharsets.UTF_8)));
                        output.add(slice);
                    }
                } else {
                    if (match) {
                        matching = true;
                        ByteBuffer copy = ByteBuffer.allocate(slice.remaining());
                        copy.put(slice).flip();
                        matches.add(copy);
                    } else {
                        output.add(slice);
                    }
                }
            } 
        }

        protected abstract String transform(String value) throws IOException;

        private static class Client extends AsyncMiddleManServletTest.HrefTransformer {
            @Override
            protected String transform(String value) throws IOException {
                String result = (AsyncMiddleManServletTest.HrefTransformer.PREFIX) + (URLEncoder.encode(value, "UTF-8"));
                AsyncMiddleManServletTest.LOG.debug("{} -> {}", value, result);
                return result;
            }
        }

        private static class Server extends AsyncMiddleManServletTest.HrefTransformer {
            @Override
            protected String transform(String value) throws IOException {
                String result = URLDecoder.decode(value.substring(AsyncMiddleManServletTest.HrefTransformer.PREFIX.length()), "UTF-8");
                AsyncMiddleManServletTest.LOG.debug("{} <- {}", value, result);
                return result;
            }
        }
    }

    private static class HrefParser {
        private final byte[] token = new byte[]{ 'h', 'r', 'e', 'f', '=', '"' };

        private int state;

        private boolean parse(ByteBuffer buffer) {
            while (buffer.hasRemaining()) {
                int current = (buffer.get()) & 255;
                if ((state) < (token.length)) {
                    if ((Character.toLowerCase(current)) != (token[state])) {
                        state = 0;
                        continue;
                    }
                    ++(state);
                    if ((state) == (token.length))
                        return false;

                } else {
                    // Look for the ending quote.
                    if (current == '"') {
                        buffer.position(((buffer.position()) - 1));
                        state = 0;
                        return true;
                    }
                }
            } 
            return (state) == (token.length);
        }
    }

    private static class BufferingContentTransformer implements AsyncMiddleManServlet.ContentTransformer {
        private final List<ByteBuffer> buffers = new ArrayList<>();

        @Override
        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
            if (input.hasRemaining()) {
                ByteBuffer copy = ByteBuffer.allocate(input.remaining());
                copy.put(input).flip();
                buffers.add(copy);
            }
            if (finished) {
                Assertions.assertFalse(buffers.isEmpty());
                output.addAll(buffers);
                buffers.clear();
            }
        }
    }

    /**
     * A transformer that discards all but the first line of text.
     */
    private static class HeadTransformer implements AsyncMiddleManServlet.ContentTransformer {
        private StringBuilder head = new StringBuilder();

        @Override
        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
            if ((input.hasRemaining()) && ((head) != null)) {
                int lnPos = findLineFeed(input);
                if (lnPos == (-1)) {
                    // no linefeed found, copy it all
                    copyHeadBytes(input, input.limit());
                } else {
                    // found linefeed
                    copyHeadBytes(input, lnPos);
                    output.addAll(getHeadBytes());
                    // mark head as sent
                    head = null;
                }
            }
            if (finished && ((head) != null)) {
                output.addAll(getHeadBytes());
            }
        }

        private void copyHeadBytes(ByteBuffer input, int pos) {
            ByteBuffer dup = input.duplicate();
            dup.limit(pos);
            String str = BufferUtil.toUTF8String(dup);
            head.append(str);
        }

        private int findLineFeed(ByteBuffer input) {
            for (int i = input.position(); i < (input.limit()); i++) {
                byte b = input.get(i);
                if ((b == ((byte) ('\n'))) || (b == ((byte) ('\r')))) {
                    return i;
                }
            }
            return -1;
        }

        private List<ByteBuffer> getHeadBytes() {
            ByteBuffer buf = BufferUtil.toBuffer(head.toString(), StandardCharsets.UTF_8);
            return Collections.singletonList(buf);
        }
    }

    private static class DiscardContentTransformer implements AsyncMiddleManServlet.ContentTransformer {
        @Override
        public void transform(ByteBuffer input, boolean finished, List<ByteBuffer> output) {
        }
    }
}

