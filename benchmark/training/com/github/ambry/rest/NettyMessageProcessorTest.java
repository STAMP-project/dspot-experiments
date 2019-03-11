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


import HttpMethod.DELETE;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.POST;
import HttpMethod.TRACE;
import HttpResponseStatus.BAD_REQUEST;
import HttpResponseStatus.INTERNAL_SERVER_ERROR;
import HttpResponseStatus.OK;
import LastHttpContent.EMPTY_LAST_CONTENT;
import MockBlobStorageService.ECHO_REST_METHOD;
import MockRestRequestResponseHandler.REST_EXCEPTION_ON_HANDLE;
import MockRestRequestResponseHandler.RUNTIME_EXCEPTION_ON_HANDLE;
import PooledByteBufAllocator.DEFAULT;
import RestServiceErrorCode.InternalServerError;
import RestUtils.Headers.SERVICE_ID;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.account.Account;
import com.github.ambry.account.Container;
import com.github.ambry.clustermap.MockClusterMap;
import com.github.ambry.config.NettyConfig;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.messageformat.BlobProperties;
import com.github.ambry.notification.BlobReplicaSourceType;
import com.github.ambry.notification.NotificationBlobType;
import com.github.ambry.notification.NotificationSystem;
import com.github.ambry.notification.UpdateType;
import com.github.ambry.router.InMemoryRouter;
import com.github.ambry.store.MessageInfo;
import com.github.ambry.utils.TestUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.util.ReferenceCountUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link NettyMessageProcessor}.
 */
public class NettyMessageProcessorTest {
    private final InMemoryRouter router;

    private final BlobStorageService blobStorageService;

    private final MockRestRequestResponseHandler requestHandler;

    private final NettyMessageProcessorTest.HelperNotificationSystem notificationSystem = new NettyMessageProcessorTest.HelperNotificationSystem();

    private static final AtomicLong REQUEST_ID_GENERATOR = new AtomicLong(0);

    private static final NettyMetrics NETTY_METRICS = new NettyMetrics(new MetricRegistry());

    private static final NettyConfig NETTY_CONFIG = new NettyConfig(new VerifiableProperties(new Properties()));

    /**
     * Sets up the mock services that {@link NettyMessageProcessor} can use.
     *
     * @throws InstantiationException
     * 		
     * @throws IOException
     * 		
     */
    public NettyMessageProcessorTest() throws IOException, InstantiationException {
        VerifiableProperties verifiableProperties = new VerifiableProperties(new Properties());
        RestRequestMetricsTracker.setDefaults(new MetricRegistry());
        router = new InMemoryRouter(verifiableProperties, notificationSystem, new MockClusterMap());
        requestHandler = new MockRestRequestResponseHandler();
        blobStorageService = new MockBlobStorageService(verifiableProperties, requestHandler, router);
        requestHandler.setBlobStorageService(blobStorageService);
        blobStorageService.start();
        requestHandler.start();
    }

    /**
     * Tests for the common case request handling flow.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void requestHandleWithGoodInputTest() throws IOException {
        doRequestHandleWithoutKeepAlive(GET, RestMethod.GET);
        doRequestHandleWithoutKeepAlive(DELETE, RestMethod.DELETE);
        doRequestHandleWithoutKeepAlive(HEAD, RestMethod.HEAD);
        EmbeddedChannel channel = createChannel();
        doRequestHandleWithKeepAlive(channel, GET, RestMethod.GET);
        doRequestHandleWithKeepAlive(channel, DELETE, RestMethod.DELETE);
        doRequestHandleWithKeepAlive(channel, HEAD, RestMethod.HEAD);
    }

    /**
     * Tests the case where raw bytes are POSTed as chunks.
     *
     * @throws InterruptedException
     * 		
     */
    @Test
    public void rawBytesPostTest() throws InterruptedException {
        Random random = new Random();
        // request also contains content.
        ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes(((random.nextInt(128)) + 128)));
        HttpRequest postRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/", Unpooled.wrappedBuffer(content));
        postRequest.headers().set(SERVICE_ID, "rawBytesPostTest");
        postRequest = ReferenceCountUtil.retain(postRequest);
        ByteBuffer receivedContent = doPostTest(postRequest, null);
        compareContent(receivedContent, Collections.singletonList(content));
        // request and content separate.
        final int NUM_CONTENTS = 5;
        postRequest = RestTestUtils.createRequest(POST, "/", null);
        List<ByteBuffer> contents = new ArrayList<ByteBuffer>(NUM_CONTENTS);
        int blobSize = 0;
        for (int i = 0; i < NUM_CONTENTS; i++) {
            ByteBuffer buffer = ByteBuffer.wrap(TestUtils.getRandomBytes(((random.nextInt(128)) + 128)));
            blobSize += buffer.remaining();
            contents.add(i, buffer);
        }
        postRequest.headers().set(SERVICE_ID, "rawBytesPostTest");
        receivedContent = doPostTest(postRequest, contents);
        compareContent(receivedContent, contents);
    }

    /**
     * Tests the case where multipart upload is used.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void multipartPostTest() throws Exception {
        Random random = new Random();
        ByteBuffer content = ByteBuffer.wrap(TestUtils.getRandomBytes(((random.nextInt(128)) + 128)));
        HttpRequest httpRequest = RestTestUtils.createRequest(POST, "/", null);
        httpRequest.headers().set(SERVICE_ID, "rawBytesPostTest");
        HttpPostRequestEncoder encoder = createEncoder(httpRequest, content);
        HttpRequest postRequest = encoder.finalizeRequest();
        List<ByteBuffer> contents = new ArrayList<ByteBuffer>();
        while (!(encoder.isEndOfInput())) {
            // Sending null for ctx because the encoder is OK with that.
            contents.add(encoder.readChunk(DEFAULT).content().nioBuffer());
        } 
        ByteBuffer receivedContent = doPostTest(postRequest, contents);
        compareContent(receivedContent, Collections.singletonList(content));
    }

    /**
     * Tests for error handling flow when bad input streams are provided to the {@link NettyMessageProcessor}.
     */
    @Test
    public void requestHandleWithBadInputTest() throws IOException {
        String content = "@@randomContent@@@";
        // content without request.
        EmbeddedChannel channel = createChannel();
        channel.writeInbound(new io.netty.handler.codec.http.DefaultLastHttpContent(Unpooled.wrappedBuffer(content.getBytes())));
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        // content without request on a channel that was kept alive
        channel = createChannel();
        // send and receive response for a good request and keep the channel alive
        channel.writeInbound(RestTestUtils.createRequest(GET, ECHO_REST_METHOD, null));
        channel.writeInbound(EMPTY_LAST_CONTENT);
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", OK, response.status());
        // drain the content
        while ((channel.readOutbound()) != null) {
        } 
        Assert.assertTrue("Channel is not active", channel.isActive());
        // send content without request
        channel.writeInbound(EMPTY_LAST_CONTENT);
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        // content when no content is expected.
        channel = createChannel();
        channel.writeInbound(RestTestUtils.createRequest(GET, "/", null));
        channel.writeInbound(new io.netty.handler.codec.http.DefaultLastHttpContent(Unpooled.wrappedBuffer(content.getBytes())));
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        // wrong HTTPObject.
        channel = createChannel();
        channel.writeInbound(new io.netty.handler.codec.http.DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK));
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        // request while another request is in progress.
        channel = createChannel();
        channel.writeInbound(RestTestUtils.createRequest(GET, "/", null));
        channel.writeInbound(RestTestUtils.createRequest(GET, "/", null));
        // channel should be closed by now
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
        // decoding failure
        channel = createChannel();
        HttpRequest httpRequest = RestTestUtils.createRequest(GET, "/", null);
        httpRequest.setDecoderResult(DecoderResult.failure(new IllegalStateException("Induced failure")));
        channel.writeInbound(httpRequest);
        // channel should be closed by now
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
        // unsupported method
        channel = createChannel();
        channel.writeInbound(RestTestUtils.createRequest(TRACE, "/", null));
        // channel should be closed by now
        Assert.assertFalse("Channel is not closed", channel.isOpen());
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", BAD_REQUEST, response.status());
    }

    /**
     * Tests for error handling flow when the {@link RestRequestHandler} throws exceptions.
     */
    @Test
    public void requestHandlerExceptionTest() {
        try {
            // RuntimeException
            Properties properties = new Properties();
            properties.setProperty(RUNTIME_EXCEPTION_ON_HANDLE, "true");
            requestHandler.breakdown(new VerifiableProperties(properties));
            doRequestHandlerExceptionTest(GET, INTERNAL_SERVER_ERROR);
            // RestServiceException
            properties.clear();
            properties.setProperty(REST_EXCEPTION_ON_HANDLE, InternalServerError.toString());
            requestHandler.breakdown(new VerifiableProperties(properties));
            doRequestHandlerExceptionTest(GET, INTERNAL_SERVER_ERROR);
        } finally {
            requestHandler.fix();
        }
    }

    /**
     * A notification system that helps track events in the {@link InMemoryRouter}. Not thread safe and has to be
     * {@link #reset()} before every operation for which it is used.
     */
    private class HelperNotificationSystem implements NotificationSystem {
        /**
         * The blob id of the blob that the last operation was on.
         */
        protected volatile String blobIdOperatedOn = null;

        /**
         * Latch for awaiting the completion of an operation.
         */
        protected volatile CountDownLatch operationCompleted = new CountDownLatch(1);

        @Override
        public void onBlobCreated(String blobId, BlobProperties blobProperties, Account account, Container container, NotificationBlobType notificationBlobType) {
            blobIdOperatedOn = blobId;
            operationCompleted.countDown();
        }

        @Override
        public void onBlobTtlUpdated(String blobId, String serviceId, long expiresAtMs, Account account, Container container) {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void onBlobDeleted(String blobId, String serviceId, Account account, Container container) {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void onBlobReplicaCreated(String sourceHost, int port, String blobId, BlobReplicaSourceType sourceType) {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void onBlobReplicaDeleted(String sourceHost, int port, String blobId, BlobReplicaSourceType sourceType) {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void onBlobReplicaUpdated(String sourceHost, int port, String blobId, BlobReplicaSourceType sourceType, UpdateType updateType, MessageInfo info) {
            throw new IllegalStateException("Not implemented");
        }

        @Override
        public void close() {
            // no op.
        }

        /**
         * Resets the state and prepares this instance for another operation.
         */
        protected void reset() {
            blobIdOperatedOn = null;
            operationCompleted = new CountDownLatch(1);
        }
    }
}

