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


import HttpHeaderNames.CONNECTION;
import HttpHeaderNames.CONTENT_LENGTH;
import HttpHeaderValues.CLOSE;
import HttpHeaderValues.KEEP_ALIVE;
import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.OPTIONS;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpMethod.TRACE;
import RestServiceErrorCode.InvalidArgs;
import RestServiceErrorCode.RequestChannelClosed;
import RestServiceErrorCode.UnsupportedHttpMethod;
import RestUtils.Headers.BLOB_SIZE;
import RestUtils.Headers.COOKIE;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.ByteBufferAsyncWritableChannel;
import com.github.ambry.config.NettyConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.router.AsyncWritableChannel;
import com.github.ambry.utils.TestUtils;
import com.github.ambry.utils.Utils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import javax.net.ssl.SSLException;
import org.junit.Assert;
import org.junit.Test;

import static NettyRequest.bufferWatermark;


/**
 * Tests functionality of {@link NettyRequest}.
 */
public class NettyRequestTest {
    private static final int GENERATED_CONTENT_SIZE = 10240;

    private static final int GENERATED_CONTENT_PART_COUNT = 10;

    private static String BLACKLISTED_QUERY_PARAM = "paramBlacklisted";

    private static final Set<String> BLACKLISTED_QUERY_PARAM_SET = Collections.singleton(NettyRequestTest.BLACKLISTED_QUERY_PARAM);

    private static final int DEFAULT_WATERMARK;

    static {
        DEFAULT_WATERMARK = new NettyConfig(new VerifiableProperties(new Properties())).nettyServerRequestBufferWatermark;
    }

    public NettyRequestTest() {
        bufferWatermark = NettyRequestTest.DEFAULT_WATERMARK;
    }

    /**
     * Tests conversion of {@link HttpRequest} to {@link NettyRequest} given good input.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void conversionWithGoodInputTest() throws RestServiceException, CertificateException, SSLException {
        // headers
        HttpHeaders headers = new DefaultHttpHeaders(false);
        headers.add(CONTENT_LENGTH, new Random().nextInt(Integer.MAX_VALUE));
        headers.add("headerKey", "headerValue1");
        headers.add("headerKey", "headerValue2");
        headers.add("overLoadedKey", "headerOverloadedValue");
        headers.add("paramNoValueInUriButValueInHeader", "paramValueInHeader");
        // params
        Map<String, List<String>> params = new HashMap<String, List<String>>();
        List<String> values = new ArrayList<String>(2);
        values.add("paramValue1");
        values.add("paramValue2");
        params.put("paramKey", values);
        values = new ArrayList<String>(1);
        values.add("paramOverloadedValue");
        params.put("overLoadedKey", values);
        params.put("paramNoValue", null);
        params.put("paramNoValueInUriButValueInHeader", null);
        params.put(NettyRequestTest.BLACKLISTED_QUERY_PARAM, values);
        StringBuilder uriAttachmentBuilder = new StringBuilder("?");
        for (Map.Entry<String, List<String>> param : params.entrySet()) {
            if ((param.getValue()) != null) {
                for (String value : param.getValue()) {
                    uriAttachmentBuilder.append(param.getKey()).append("=").append(value).append("&");
                }
            } else {
                uriAttachmentBuilder.append(param.getKey()).append("&");
            }
        }
        uriAttachmentBuilder.deleteCharAt(((uriAttachmentBuilder.length()) - 1));
        String uriAttachment = uriAttachmentBuilder.toString();
        NettyRequest nettyRequest;
        String uri;
        Set<Cookie> cookies = new HashSet<>();
        Cookie httpCookie = new DefaultCookie("CookieKey1", "CookieValue1");
        cookies.add(httpCookie);
        httpCookie = new DefaultCookie("CookieKey2", "CookieValue2");
        cookies.add(httpCookie);
        headers.add(COOKIE, getCookiesHeaderValue(cookies));
        for (MockChannel channel : Arrays.asList(new MockChannel(), new MockChannel().addSslHandlerToPipeline())) {
            uri = "/GET" + uriAttachment;
            nettyRequest = createNettyRequest(GET, uri, headers, channel);
            validateRequest(nettyRequest, RestMethod.GET, uri, headers, params, cookies, channel);
            closeRequestAndValidate(nettyRequest, channel);
            RecvByteBufAllocator savedAllocator = channel.config().getRecvByteBufAllocator();
            int[] bufferWatermarks = new int[]{ -1, 0, 1, NettyRequestTest.DEFAULT_WATERMARK };
            for (int bufferWatermark : bufferWatermarks) {
                bufferWatermark = bufferWatermark;
                uri = "/POST" + uriAttachment;
                nettyRequest = createNettyRequest(POST, uri, headers, channel);
                validateRequest(nettyRequest, RestMethod.POST, uri, headers, params, cookies, channel);
                if (bufferWatermark > 0) {
                    Assert.assertTrue("RecvAllocator should have changed", ((channel.config().getRecvByteBufAllocator()) instanceof DefaultMaxBytesRecvByteBufAllocator));
                } else {
                    Assert.assertEquals("RecvAllocator not as expected", savedAllocator, channel.config().getRecvByteBufAllocator());
                }
                closeRequestAndValidate(nettyRequest, channel);
                Assert.assertEquals("Allocator not as expected", savedAllocator, channel.config().getRecvByteBufAllocator());
            }
            for (int bufferWatermark : bufferWatermarks) {
                bufferWatermark = bufferWatermark;
                uri = "/PUT" + uriAttachment;
                nettyRequest = createNettyRequest(PUT, uri, headers, channel);
                validateRequest(nettyRequest, RestMethod.PUT, uri, headers, params, cookies, channel);
                if (bufferWatermark > 0) {
                    Assert.assertTrue("RecvAllocator should have changed", ((channel.config().getRecvByteBufAllocator()) instanceof DefaultMaxBytesRecvByteBufAllocator));
                } else {
                    Assert.assertEquals("RecvAllocator not as expected", savedAllocator, channel.config().getRecvByteBufAllocator());
                }
                closeRequestAndValidate(nettyRequest, channel);
                Assert.assertEquals("Allocator not as expected", savedAllocator, channel.config().getRecvByteBufAllocator());
            }
            bufferWatermark = NettyRequestTest.DEFAULT_WATERMARK;
            uri = "/DELETE" + uriAttachment;
            nettyRequest = createNettyRequest(DELETE, uri, headers, channel);
            validateRequest(nettyRequest, RestMethod.DELETE, uri, headers, params, cookies, channel);
            closeRequestAndValidate(nettyRequest, channel);
            uri = "/HEAD" + uriAttachment;
            nettyRequest = createNettyRequest(HEAD, uri, headers, channel);
            validateRequest(nettyRequest, RestMethod.HEAD, uri, headers, params, cookies, channel);
            closeRequestAndValidate(nettyRequest, channel);
            uri = "/OPTIONS" + uriAttachment;
            nettyRequest = createNettyRequest(OPTIONS, uri, headers, channel);
            validateRequest(nettyRequest, RestMethod.OPTIONS, uri, headers, params, cookies, channel);
            closeRequestAndValidate(nettyRequest, channel);
        }
    }

    /**
     * Tests conversion of {@link HttpRequest} to {@link NettyRequest} given bad input (i.e. checks for the correct
     * exception and {@link RestServiceErrorCode} if any).
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void conversionWithBadInputTest() throws RestServiceException {
        HttpRequest httpRequest = new io.netty.handler.codec.http.DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "");
        // HttpRequest null.
        try {
            new NettyRequest(null, new MockChannel(), new NettyMetrics(new MetricRegistry()), NettyRequestTest.BLACKLISTED_QUERY_PARAM_SET);
            Assert.fail("Provided null HttpRequest to NettyRequest, yet it did not fail");
        } catch (IllegalArgumentException e) {
            // expected. nothing to do.
        }
        // Channel null.
        try {
            new NettyRequest(httpRequest, null, new NettyMetrics(new MetricRegistry()), NettyRequestTest.BLACKLISTED_QUERY_PARAM_SET);
            Assert.fail("Provided null Channel to NettyRequest, yet it did not fail");
        } catch (IllegalArgumentException e) {
            // expected. nothing to do.
        }
        // unknown http method
        try {
            createNettyRequest(TRACE, "/", null, new MockChannel());
            Assert.fail("Unknown http method was supplied to NettyRequest. It should have failed to construct");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", UnsupportedHttpMethod, e.getErrorCode());
        }
        String[] invalidBlobSizeStrs = new String[]{ "aba123", "12ab", "-1", "ddsdd", "999999999999999999999999999", "1.234" };
        for (String blobSizeStr : invalidBlobSizeStrs) {
            // bad blob size
            try {
                createNettyRequest(GET, "/", new DefaultHttpHeaders().add(BLOB_SIZE, blobSizeStr), new MockChannel());
                Assert.fail("Bad blob size header was supplied to NettyRequest. It should have failed to construct");
            } catch (RestServiceException e) {
                Assert.assertEquals("Unexpected RestServiceErrorCode", InvalidArgs, e.getErrorCode());
            }
        }
    }

    /**
     * Tests for behavior of multiple operations after {@link NettyRequest#close()} has been called. Some should be ok to
     * do and some should throw exceptions.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void operationsAfterCloseTest() throws Exception {
        Channel channel = new MockChannel();
        NettyRequest nettyRequest = createNettyRequest(POST, "/", null, channel);
        closeRequestAndValidate(nettyRequest, channel);
        // operations that should be ok to do (does not include all operations).
        nettyRequest.close();
        // operations that will throw exceptions.
        AsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        ReadIntoCallback callback = new ReadIntoCallback();
        try {
            nettyRequest.readInto(writeChannel, callback).get();
            Assert.fail("Request channel has been closed, so read should have thrown ClosedChannelException");
        } catch (ExecutionException e) {
            Exception exception = ((Exception) (Utils.getRootCause(e)));
            Assert.assertTrue("Exception is not ClosedChannelException", (exception instanceof ClosedChannelException));
            callback.awaitCallback();
            Assert.assertEquals("Exceptions of callback and future differ", exception.getMessage(), callback.exception.getMessage());
        }
        try {
            byte[] content = TestUtils.getRandomBytes(1024);
            nettyRequest.addContent(new DefaultLastHttpContent(Unpooled.wrappedBuffer(content)));
            Assert.fail("Request channel has been closed, so addContent() should have thrown ClosedChannelException");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", RequestChannelClosed, e.getErrorCode());
        }
    }

    /**
     * Tests {@link NettyRequest#addContent(HttpContent)} and
     * {@link NettyRequest#readInto(AsyncWritableChannel, Callback)} with different digest algorithms (including a test
     * with no digest algorithm).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void contentAddAndReadTest() throws Exception {
        String[] digestAlgorithms = new String[]{ "", "MD5", "SHA-1", "SHA-256" };
        HttpMethod[] methods = new HttpMethod[]{ HttpMethod.POST, HttpMethod.PUT };
        for (HttpMethod method : methods) {
            for (String digestAlgorithm : digestAlgorithms) {
                contentAddAndReadTest(digestAlgorithm, true, method);
                contentAddAndReadTest(digestAlgorithm, false, method);
            }
        }
    }

    /**
     * Tests {@link NettyRequest#addContent(HttpContent)} and
     * {@link NettyRequest#readInto(AsyncWritableChannel, Callback)} with different digest algorithms (including a test
     * with no digest algorithm) and checks that back pressure is applied correctly.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void backPressureTest() throws Exception {
        String[] digestAlgorithms = new String[]{ "", "MD5", "SHA-1", "SHA-256" };
        HttpMethod[] methods = new HttpMethod[]{ HttpMethod.POST, HttpMethod.PUT };
        for (HttpMethod method : methods) {
            for (String digestAlgorithm : digestAlgorithms) {
                backPressureTest(digestAlgorithm, true, method);
                backPressureTest(digestAlgorithm, false, method);
            }
        }
    }

    /**
     * Tests exception scenarios of {@link NettyRequest#readInto(AsyncWritableChannel, Callback)} and behavior of
     * {@link NettyRequest} when {@link AsyncWritableChannel} instances fail.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void readIntoExceptionsTest() throws Exception {
        Channel channel = new MockChannel();
        // try to call readInto twice.
        NettyRequest nettyRequest = createNettyRequest(POST, "/", null, channel);
        AsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        nettyRequest.readInto(writeChannel, null);
        try {
            nettyRequest.readInto(writeChannel, null);
            Assert.fail("Calling readInto twice should have failed");
        } catch (IllegalStateException e) {
            // expected. Nothing to do.
        }
        closeRequestAndValidate(nettyRequest, channel);
        // write into a channel that throws exceptions
        // non RuntimeException
        nettyRequest = createNettyRequest(POST, "/", null, channel);
        List<HttpContent> httpContents = new ArrayList<HttpContent>();
        generateContent(httpContents);
        Assert.assertTrue("Not enough content has been generated", ((httpContents.size()) > 2));
        String expectedMsg = "@@expectedMsg@@";
        Exception exception = new Exception(expectedMsg);
        writeChannel = new BadAsyncWritableChannel(exception);
        ReadIntoCallback callback = new ReadIntoCallback();
        // add content initially
        int addedCount = 0;
        for (; addedCount < ((httpContents.size()) / 2); addedCount++) {
            HttpContent httpContent = httpContents.get(addedCount);
            nettyRequest.addContent(httpContent);
            Assert.assertEquals("Reference count is not as expected", 2, httpContent.refCnt());
        }
        Future<Long> future = nettyRequest.readInto(writeChannel, callback);
        // add some more content
        for (; addedCount < (httpContents.size()); addedCount++) {
            HttpContent httpContent = httpContents.get(addedCount);
            nettyRequest.addContent(httpContent);
        }
        writeChannel.close();
        verifyRefCnts(httpContents);
        callback.awaitCallback();
        Assert.assertNotNull("Exception was not piped correctly", callback.exception);
        Assert.assertEquals("Exception message mismatch (callback)", expectedMsg, callback.exception.getMessage());
        try {
            future.get();
            Assert.fail("Future should have thrown exception");
        } catch (ExecutionException e) {
            Assert.assertEquals("Exception message mismatch (future)", expectedMsg, Utils.getRootCause(e).getMessage());
        }
        closeRequestAndValidate(nettyRequest, channel);
        // RuntimeException
        // during readInto
        nettyRequest = createNettyRequest(POST, "/", null, channel);
        httpContents = new ArrayList<HttpContent>();
        generateContent(httpContents);
        exception = new IllegalStateException(expectedMsg);
        writeChannel = new BadAsyncWritableChannel(exception);
        callback = new ReadIntoCallback();
        for (HttpContent httpContent : httpContents) {
            nettyRequest.addContent(httpContent);
            Assert.assertEquals("Reference count is not as expected", 2, httpContent.refCnt());
        }
        try {
            nettyRequest.readInto(writeChannel, callback);
            Assert.fail("readInto did not throw expected exception");
        } catch (Exception e) {
            Assert.assertEquals("Exception caught does not match expected exception", expectedMsg, e.getMessage());
        }
        writeChannel.close();
        closeRequestAndValidate(nettyRequest, channel);
        verifyRefCnts(httpContents);
        // after readInto
        nettyRequest = createNettyRequest(POST, "/", null, channel);
        httpContents = new ArrayList<HttpContent>();
        generateContent(httpContents);
        exception = new IllegalStateException(expectedMsg);
        writeChannel = new BadAsyncWritableChannel(exception);
        callback = new ReadIntoCallback();
        nettyRequest.readInto(writeChannel, callback);
        // add content
        HttpContent httpContent = httpContents.get(1);
        try {
            nettyRequest.addContent(httpContent);
            Assert.fail("addContent did not throw expected exception");
        } catch (Exception e) {
            Assert.assertEquals("Exception caught does not match expected exception", expectedMsg, e.getMessage());
        }
        writeChannel.close();
        closeRequestAndValidate(nettyRequest, channel);
        verifyRefCnts(httpContents);
    }

    /**
     * Tests that {@link NettyRequest#close()} leaves any added {@link HttpContent} the way it was before it was added.
     * (i.e no reference count changes).
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void closeTest() throws RestServiceException {
        Channel channel = new MockChannel();
        NettyRequest nettyRequest = createNettyRequest(POST, "/", null, channel);
        Queue<HttpContent> httpContents = new LinkedBlockingQueue<HttpContent>();
        for (int i = 0; i < 5; i++) {
            ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes(1024));
            HttpContent httpContent = new io.netty.handler.codec.http.DefaultHttpContent(Unpooled.wrappedBuffer(content));
            nettyRequest.addContent(httpContent);
            httpContents.add(httpContent);
        }
        closeRequestAndValidate(nettyRequest, channel);
        while ((httpContents.peek()) != null) {
            Assert.assertEquals("Reference count of http content has changed", 1, httpContents.poll().refCnt());
        } 
    }

    /**
     * Tests different state transitions that can happen with {@link NettyRequest#addContent(HttpContent)} for GET
     * requests. Some transitions are valid and some should necessarily throw exceptions.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void addContentForGetTest() throws RestServiceException {
        byte[] content = TestUtils.getRandomBytes(16);
        // adding non LastHttpContent to nettyRequest
        NettyRequest nettyRequest = createNettyRequest(GET, "/", null, new MockChannel());
        try {
            nettyRequest.addContent(new io.netty.handler.codec.http.DefaultHttpContent(Unpooled.wrappedBuffer(content)));
            Assert.fail("GET requests should not accept non-LastHTTPContent");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
        // adding LastHttpContent with some content to nettyRequest
        nettyRequest = createNettyRequest(GET, "/", null, new MockChannel());
        try {
            nettyRequest.addContent(new DefaultLastHttpContent(Unpooled.wrappedBuffer(content)));
            Assert.fail("GET requests should not accept actual content in LastHTTPContent");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
        // should accept LastHttpContent just fine.
        nettyRequest = createNettyRequest(GET, "/", null, new MockChannel());
        nettyRequest.addContent(new DefaultLastHttpContent());
        // should not accept LastHttpContent after close
        nettyRequest = createNettyRequest(GET, "/", null, new MockChannel());
        nettyRequest.close();
        try {
            nettyRequest.addContent(new DefaultLastHttpContent());
            Assert.fail("Request channel has been closed, so addContent() should have thrown ClosedChannelException");
        } catch (RestServiceException e) {
            Assert.assertEquals("Unexpected RestServiceErrorCode", RequestChannelClosed, e.getErrorCode());
        }
    }

    @Test
    public void keepAliveTest() throws RestServiceException {
        NettyRequest request = createNettyRequest(GET, "/", null, new MockChannel());
        // by default, keep-alive is true for HTTP 1.1
        Assert.assertTrue("Keep-alive not as expected", request.isKeepAlive());
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(CONNECTION, KEEP_ALIVE);
        request = createNettyRequest(GET, "/", headers, new MockChannel());
        Assert.assertTrue("Keep-alive not as expected", request.isKeepAlive());
        headers = new DefaultHttpHeaders();
        headers.set(CONNECTION, CLOSE);
        request = createNettyRequest(GET, "/", headers, new MockChannel());
        Assert.assertFalse("Keep-alive not as expected", request.isKeepAlive());
    }

    /**
     * Tests the {@link NettyRequest#getSize()} function to see that it respects priorities.
     *
     * @throws RestServiceException
     * 		
     */
    @Test
    public void sizeTest() throws RestServiceException {
        // no length headers provided.
        NettyRequest nettyRequest = createNettyRequest(GET, "/", null, new MockChannel());
        Assert.assertEquals("Size not as expected", (-1), nettyRequest.getSize());
        // deliberate mismatch to check priorities.
        int xAmbryBlobSize = 20;
        int contentLength = 10;
        // Content-Length header set
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.add(CONTENT_LENGTH, contentLength);
        nettyRequest = createNettyRequest(GET, "/", headers, new MockChannel());
        Assert.assertEquals("Size not as expected", contentLength, nettyRequest.getSize());
        // xAmbryBlobSize set
        headers = new DefaultHttpHeaders();
        headers.add(BLOB_SIZE, xAmbryBlobSize);
        nettyRequest = createNettyRequest(GET, "/", headers, new MockChannel());
        Assert.assertEquals("Size not as expected", xAmbryBlobSize, nettyRequest.getSize());
        // both set
        headers = new DefaultHttpHeaders();
        headers.add(BLOB_SIZE, xAmbryBlobSize);
        headers.add(CONTENT_LENGTH, contentLength);
        nettyRequest = createNettyRequest(GET, "/", headers, new MockChannel());
        Assert.assertEquals("Size not as expected", xAmbryBlobSize, nettyRequest.getSize());
    }

    /**
     * Tests for POST request that has no content.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void zeroSizeContentTest() throws Exception {
        Channel channel = new MockChannel();
        NettyRequest nettyRequest = createNettyRequest(POST, "/", null, channel);
        HttpContent httpContent = new DefaultLastHttpContent();
        nettyRequest.addContent(httpContent);
        Assert.assertEquals("Reference count is not as expected", 2, httpContent.refCnt());
        ByteBufferAsyncWritableChannel writeChannel = new ByteBufferAsyncWritableChannel();
        ReadIntoCallback callback = new ReadIntoCallback();
        Future<Long> future = nettyRequest.readInto(writeChannel, callback);
        Assert.assertEquals("There should be no content", 0, writeChannel.getNextChunk().remaining());
        writeChannel.resolveOldestChunk(null);
        closeRequestAndValidate(nettyRequest, channel);
        writeChannel.close();
        Assert.assertEquals("Reference count of http content has changed", 1, httpContent.refCnt());
        callback.awaitCallback();
        if ((callback.exception) != null) {
            throw callback.exception;
        }
        long futureBytesRead = future.get();
        Assert.assertEquals("Total bytes read does not match (callback)", 0, callback.bytesRead);
        Assert.assertEquals("Total bytes read does not match (future)", 0, futureBytesRead);
    }

    /**
     * Tests reaction of NettyRequest when content size is different from the size specified in the headers.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void headerAndContentSizeMismatchTest() throws Exception {
        sizeInHeaderMoreThanContentTest();
        sizeInHeaderLessThanContentTest();
    }

    /**
     * Does any left over tests for {@link NettyRequest.ContentWriteCallback}
     */
    @Test
    public void contentWriteCallbackTests() throws RestServiceException {
        ReadIntoCallback readIntoCallback = new ReadIntoCallback();
        NettyRequest nettyRequest = createNettyRequest(GET, "/", null, new MockChannel());
        NettyRequest.ReadIntoCallbackWrapper wrapper = nettyRequest.new ReadIntoCallbackWrapper(readIntoCallback);
        NettyRequest.ContentWriteCallback callback = nettyRequest.new ContentWriteCallback(null, true, wrapper);
        long bytesRead = new Random().nextInt(Integer.MAX_VALUE);
        // there should be no problem even though httpContent is null.
        callback.onCompletion(bytesRead, null);
        Assert.assertEquals("Bytes read does not match", bytesRead, readIntoCallback.bytesRead);
    }

    /**
     * Tests for incorrect usage of {@link NettyRequest#setDigestAlgorithm(String)} and {@link NettyRequest#getDigest()}.
     *
     * @throws NoSuchAlgorithmException
     * 		
     * @throws RestServiceException
     * 		
     */
    @Test
    public void digestIncorrectUsageTest() throws RestServiceException, NoSuchAlgorithmException {
        setDigestAfterReadTest();
        setBadAlgorithmTest();
        getDigestWithoutSettingAlgorithmTest();
        getDigestBeforeAllContentProcessedTest();
    }
}

