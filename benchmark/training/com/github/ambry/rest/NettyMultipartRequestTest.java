/**
 * Copyright 2016 LinkedIn Corp. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package com.github.ambry.rest;


import PooledByteBufAllocator.DEFAULT;
import RestServiceErrorCode.BadRequest;
import RestServiceErrorCode.MalformedRequest;
import RestServiceErrorCode.RequestChannelClosed;
import RestUtils.Headers.BLOB_SIZE;
import RestUtils.MultipartPost;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.ByteBufferAsyncWritableChannel;
import com.github.ambry.config.NettyConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.router.AsyncWritableChannel;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import io.netty.buffer.Unpooled;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;

import static NettyRequest.bufferWatermark;


/**
 * Tests functionality of {@link NettyMultipartRequest}.
 */
public class NettyMultipartRequestTest {
    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    private static final NettyMetrics NETTY_METRICS = new NettyMetrics(NettyMultipartRequestTest.METRIC_REGISTRY);

    private static final int DEFAULT_WATERMARK;

    static {
        DEFAULT_WATERMARK = new NettyConfig(new VerifiableProperties(new Properties())).nettyServerRequestBufferWatermark;
        RestRequestMetricsTracker.setDefaults(NettyMultipartRequestTest.METRIC_REGISTRY);
    }

    public NettyMultipartRequestTest() {
        bufferWatermark = NettyMultipartRequestTest.DEFAULT_WATERMARK;
    }

    /**
     * Tests instantiation of {@link NettyMultipartRequest} with different {@link HttpMethod} types.
     * </p>
     * Only {@link HttpMethod#POST} should succeed.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void instantiationTest() throws RestServiceException {
        HttpMethod[] successMethods = new HttpMethod[]{ HttpMethod.POST, HttpMethod.PUT };
        // POST and PUT will succeed.
        for (HttpMethod method : successMethods) {
            bufferWatermark = 1;
            HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, method, "/");
            MockChannel channel = new MockChannel();
            RecvByteBufAllocator expected = channel.config().getRecvByteBufAllocator();
            NettyMultipartRequest request = new NettyMultipartRequest(httpRequest, channel, NettyMultipartRequestTest.NETTY_METRICS, Collections.emptySet(), Long.MAX_VALUE);
            Assert.assertTrue("Auto-read should not have been changed", channel.config().isAutoRead());
            Assert.assertEquals("RecvByteBufAllocator should not have changed", expected, channel.config().getRecvByteBufAllocator());
            closeRequestAndValidate(request);
        }
        // Methods that will fail. Can include other methods, but these should be enough.
        HttpMethod[] methods = new HttpMethod[]{ HttpMethod.GET, HttpMethod.DELETE, HttpMethod.HEAD };
        for (HttpMethod method : methods) {
            HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, method, "/");
            try {
                new NettyMultipartRequest(httpRequest, new MockChannel(), NettyMultipartRequestTest.NETTY_METRICS, Collections.emptySet(), Long.MAX_VALUE);
                Assert.fail(("Creation of NettyMultipartRequest should have failed for " + method));
            } catch (IllegalArgumentException e) {
                // expected. Nothing to do.
            }
        }
    }

    /**
     * Tests that multipart requests are decoded successfully and verifies that the decoded data matches the source data.
     * Request kinds tested:
     * 1. Request without content.
     * 2. Request without a {@link RestUtils.MultipartPost#BLOB_PART} but with other parts.
     * 3. Request with a {@link RestUtils.MultipartPost#BLOB_PART} and with other parts.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void multipartRequestDecodeTest() throws Exception {
        String[] digestAlgorithms = new String[]{ "", "MD5", "SHA-1", "SHA-256" };
        for (String digestAlgorithm : digestAlgorithms) {
            // request without content
            doMultipartDecodeTest(0, null, digestAlgorithm);
            final int BLOB_PART_SIZE = 1024;
            // number of parts including the Blob
            final int NUM_TOTAL_PARTS = 5;
            Random random = new Random();
            NettyMultipartRequestTest.InMemoryFile[] files = new NettyMultipartRequestTest.InMemoryFile[NUM_TOTAL_PARTS];
            for (int i = 0; i < NUM_TOTAL_PARTS; i++) {
                files[i] = new NettyMultipartRequestTest.InMemoryFile(("part-" + i), ByteBuffer.wrap(TestUtils.getRandomBytes(((random.nextInt(128)) + 128))));
            }
            // request without blob (but has other parts)
            doMultipartDecodeTest(0, files, digestAlgorithm);
            // request with blob and other parts
            files[(NUM_TOTAL_PARTS - 1)] = new NettyMultipartRequestTest.InMemoryFile(MultipartPost.BLOB_PART, ByteBuffer.wrap(TestUtils.getRandomBytes(BLOB_PART_SIZE)));
            doMultipartDecodeTest(BLOB_PART_SIZE, files, digestAlgorithm);
        }
    }

    /**
     * Tests that reference counts are correct when a {@link NettyMultipartRequest} is closed without being read.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void refCountsAfterCloseTest() throws Exception {
        NettyMultipartRequest requestCloseBeforePrepare = createRequest(null, null);
        NettyMultipartRequest requestCloseAfterPrepare = createRequest(null, null);
        List<HttpContent> httpContents = new ArrayList<HttpContent>(5);
        for (int i = 0; i < 5; i++) {
            HttpContent httpContent = new io.netty.handler.codec.http.DefaultHttpContent(Unpooled.wrappedBuffer(TestUtils.getRandomBytes(10)));
            requestCloseBeforePrepare.addContent(httpContent);
            requestCloseAfterPrepare.addContent(httpContent);
            Assert.assertEquals("Reference count is not as expected", 3, httpContent.refCnt());
            httpContents.add(httpContent);
        }
        closeRequestAndValidate(requestCloseBeforePrepare);
        requestCloseAfterPrepare.prepare();
        closeRequestAndValidate(requestCloseAfterPrepare);
        for (HttpContent httpContent : httpContents) {
            Assert.assertEquals("Reference count is not as expected", 1, httpContent.refCnt());
        }
    }

    /**
     * Tests the expected behavior of operations after {@link NettyMultipartRequest#close()} has been called.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void operationsAfterCloseTest() throws Exception {
        NettyMultipartRequest request = createRequest(null, null);
        request.prepare();
        closeRequestAndValidate(request);
        // close should be idempotent.
        request.close();
        try {
            request.readInto(new ByteBufferAsyncWritableChannel(), null).get();
            Assert.fail("Reading should have failed because request is closed");
        } catch (ExecutionException e) {
            Assert.assertEquals("Unexpected exception", ClosedChannelException.class, Utils.getRootCause(e).getClass());
        }
        try {
            request.prepare();
            Assert.fail("Preparing should have failed because request is closed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", RequestChannelClosed, e.getErrorCode());
        }
        try {
            request.addContent(new io.netty.handler.codec.http.DefaultHttpContent(Unpooled.wrappedBuffer(TestUtils.getRandomBytes(10))));
            Assert.fail("Content addition should have failed because request is closed");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", RequestChannelClosed, e.getErrorCode());
        }
    }

    /**
     * Tests exception scenarios of {@link NettyMultipartRequest#readInto(AsyncWritableChannel, Callback)}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void readIntoExceptionsTest() throws Exception {
        // most tests are in NettyRequest. Adding tests for differing code in NettyMultipartRequest
        // try to call readInto twice.
        NettyMultipartRequest request = createRequest(null, null);
        AsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        request.prepare();
        request.readInto(writeChannel, null);
        try {
            request.readInto(writeChannel, null);
            Assert.fail("Calling readInto twice should have failed");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        } finally {
            closeRequestAndValidate(request);
        }
        // call readInto when not ready for read.
        request = createRequest(null, null);
        writeChannel = new ByteBufferAsyncWritableChannel();
        try {
            request.readInto(writeChannel, null);
            Assert.fail("Calling readInto without calling prepare() should have failed");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        } finally {
            closeRequestAndValidate(request);
        }
    }

    /**
     * Tests different scenarios with {@link NettyMultipartRequest#prepare()}.
     * Currently tests:
     * 1. Idempotency of {@link NettyMultipartRequest#prepare()}.
     * 2. Exception scenarios of {@link NettyMultipartRequest#prepare()}.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void prepareTest() throws Exception {
        // prepare half baked data
        HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/");
        HttpPostRequestEncoder encoder = createEncoder(httpRequest, null);
        NettyMultipartRequest request = new NettyMultipartRequest(encoder.finalizeRequest(), new MockChannel(), NettyMultipartRequestTest.NETTY_METRICS, Collections.emptySet(), Long.MAX_VALUE);
        Assert.assertTrue("Request channel is not open", request.isOpen());
        // insert random data
        HttpContent httpContent = new io.netty.handler.codec.http.DefaultHttpContent(Unpooled.wrappedBuffer(TestUtils.getRandomBytes(10)));
        request.addContent(httpContent);
        // prepare should fail
        try {
            request.prepare();
            Assert.fail("Preparing request should have failed");
        } catch (HttpPostRequestDecoder e) {
            Assert.assertEquals("Reference count is not as expected", 1, httpContent.refCnt());
        } finally {
            closeRequestAndValidate(request);
        }
        // more than one blob part
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        httpHeaders.set(BLOB_SIZE, 256);
        NettyMultipartRequestTest.InMemoryFile[] files = new NettyMultipartRequestTest.InMemoryFile[2];
        files[0] = new NettyMultipartRequestTest.InMemoryFile(MultipartPost.BLOB_PART, ByteBuffer.wrap(TestUtils.getRandomBytes(256)));
        files[1] = new NettyMultipartRequestTest.InMemoryFile(MultipartPost.BLOB_PART, ByteBuffer.wrap(TestUtils.getRandomBytes(256)));
        request = createRequest(httpHeaders, files);
        Assert.assertEquals("Request size does not match", 256, request.getSize());
        try {
            request.prepare();
            Assert.fail(("Prepare should have failed because there was more than one " + (MultipartPost.BLOB_PART)));
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", MalformedRequest, e.getErrorCode());
        } finally {
            closeRequestAndValidate(request);
        }
        // more than one part named "part-1"
        files = new NettyMultipartRequestTest.InMemoryFile[2];
        files[0] = new NettyMultipartRequestTest.InMemoryFile("Part-1", ByteBuffer.wrap(TestUtils.getRandomBytes(256)));
        files[1] = new NettyMultipartRequestTest.InMemoryFile("Part-1", ByteBuffer.wrap(TestUtils.getRandomBytes(256)));
        request = createRequest(null, files);
        try {
            request.prepare();
            Assert.fail("Prepare should have failed because there was more than one part named Part-1");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", MalformedRequest, e.getErrorCode());
        } finally {
            closeRequestAndValidate(request);
        }
        // size of blob does not match the advertized size
        httpHeaders = new DefaultHttpHeaders();
        httpHeaders.set(BLOB_SIZE, 256);
        files = new NettyMultipartRequestTest.InMemoryFile[1];
        files[0] = new NettyMultipartRequestTest.InMemoryFile(MultipartPost.BLOB_PART, ByteBuffer.wrap(TestUtils.getRandomBytes(128)));
        request = createRequest(httpHeaders, files);
        try {
            request.prepare();
            Assert.fail("Prepare should have failed because the size advertised does not match the actual size");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", BadRequest, e.getErrorCode());
        } finally {
            closeRequestAndValidate(request);
        }
        // non fileupload (file attribute present)
        httpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/");
        httpRequest.headers().set(BLOB_SIZE, 256);
        files = new NettyMultipartRequestTest.InMemoryFile[1];
        files[0] = new NettyMultipartRequestTest.InMemoryFile(MultipartPost.BLOB_PART, ByteBuffer.wrap(TestUtils.getRandomBytes(256)));
        encoder = createEncoder(httpRequest, files);
        encoder.addBodyAttribute("dummyKey", "dummyValue");
        request = new NettyMultipartRequest(encoder.finalizeRequest(), new MockChannel(), NettyMultipartRequestTest.NETTY_METRICS, Collections.emptySet(), Long.MAX_VALUE);
        Assert.assertTrue("Request channel is not open", request.isOpen());
        while (!(encoder.isEndOfInput())) {
            // Sending null for ctx because the encoder is OK with that.
            request.addContent(encoder.readChunk(DEFAULT));
        } 
        try {
            request.prepare();
            Assert.fail("Prepare should have failed because there was non fileupload");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", BadRequest, e.getErrorCode());
        } finally {
            closeRequestAndValidate(request);
        }
        // size of blob is not set. Prepare should succeed.
        httpHeaders = new DefaultHttpHeaders();
        files = new NettyMultipartRequestTest.InMemoryFile[1];
        files[0] = new NettyMultipartRequestTest.InMemoryFile(MultipartPost.BLOB_PART, ByteBuffer.wrap(TestUtils.getRandomBytes(128)));
        request = createRequest(httpHeaders, files);
        try {
            request.prepare();
        } finally {
            closeRequestAndValidate(request);
        }
    }

    /**
     * In memory representation of content.
     */
    private class InMemoryFile {
        public final String name;

        public final ByteBuffer content;

        InMemoryFile(String name, ByteBuffer content) {
            this.name = name;
            this.content = content;
        }
    }
}

