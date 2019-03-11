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


import HttpHeaderNames.ALLOW;
import HttpMethod.GET;
import HttpMethod.HEAD;
import HttpMethod.POST;
import HttpMethod.PUT;
import HttpResponseStatus.ACCEPTED;
import HttpResponseStatus.INTERNAL_SERVER_ERROR;
import HttpResponseStatus.OK;
import HttpStatusClass.CLIENT_ERROR;
import NettyResponseChannel.CLOSE_CONNECTION_ERROR_STATUSES;
import NettyResponseChannel.ERROR_CODE_HEADER;
import NettyResponseChannel.FAILURE_REASON_HEADER;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.utils.Utils;
import com.github.ambry.utils.UtilsTest;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests functionality of {@link NettyResponseChannel}.
 * <p/>
 * To understand what each {@link TestingUri} is doing, refer to
 * {@link MockNettyMessageProcessor#handleRequest(HttpRequest)} and
 * {@link MockNettyMessageProcessor#handleContent(HttpContent)}.
 */
public class NettyResponseChannelTest {
    /**
     * Tests the common workflow of the {@link NettyResponseChannel} i.e., add some content to response body via
     * {@link NettyResponseChannel#write(ByteBuffer, Callback)} and then complete the response.
     * <p/>
     * These responses have the header Transfer-Encoding set to chunked.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void responsesWithTransferEncodingChunkedTest() throws Exception {
        String content = "@@randomContent@@@";
        String lastContent = "@@randomLastContent@@@";
        EmbeddedChannel channel = createEmbeddedChannel();
        MockNettyMessageProcessor processor = channel.pipeline().get(MockNettyMessageProcessor.class);
        AtomicLong contentIdGenerator = new AtomicLong(0);
        final int ITERATIONS = 10;
        for (int i = 0; i < ITERATIONS; i++) {
            boolean isKeepAlive = i != (ITERATIONS - 1);
            HttpRequest httpRequest = RestTestUtils.createRequest(POST, "/", null);
            HttpUtil.setKeepAlive(httpRequest, isKeepAlive);
            channel.writeInbound(httpRequest);
            ArrayList<String> contents = new ArrayList<>();
            for (int j = 0; j <= i; j++) {
                String contentToSend = content + (contentIdGenerator.getAndIncrement());
                channel.writeInbound(createContent(contentToSend, false));
                contents.add(contentToSend);
            }
            channel.writeInbound(createContent(lastContent, true));
            verifyCallbacks(processor);
            // first outbound has to be response.
            HttpResponse response = ((HttpResponse) (channel.readOutbound()));
            Assert.assertEquals("Unexpected response status", OK, response.status());
            Assert.assertTrue("Response must say 'Transfer-Encoding : chunked'", HttpUtil.isTransferEncodingChunked(response));
            // content echoed back.
            for (String srcOfTruth : contents) {
                String returnedContent = RestTestUtils.getContentString(((HttpContent) (channel.readOutbound())));
                Assert.assertEquals("Content does not match with expected content", srcOfTruth, returnedContent);
            }
            // last content echoed back.
            String returnedContent = RestTestUtils.getContentString(((HttpContent) (channel.readOutbound())));
            Assert.assertEquals("Content does not match with expected content", lastContent, returnedContent);
            Assert.assertTrue("Did not receive end marker", ((channel.readOutbound()) instanceof LastHttpContent));
            Assert.assertEquals("Unexpected channel state on the server", isKeepAlive, channel.isActive());
        }
    }

    /**
     * Tests the common workflow of the {@link NettyResponseChannel} i.e., add some content to response body via
     * {@link NettyResponseChannel#write(ByteBuffer, Callback)} and then complete the response.
     * <p/>
     * These responses have the header Content-Length set.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void responsesWithContentLengthTest() throws Exception {
        EmbeddedChannel channel = createEmbeddedChannel();
        MockNettyMessageProcessor processor = channel.pipeline().get(MockNettyMessageProcessor.class);
        final int ITERATIONS = 10;
        for (int i = 0; i < ITERATIONS; i++) {
            boolean isKeepAlive = i != (ITERATIONS - 1);
            HttpHeaders httpHeaders = new DefaultHttpHeaders();
            httpHeaders.set(MockNettyMessageProcessor.CHUNK_COUNT_HEADER_NAME, i);
            HttpRequest httpRequest = RestTestUtils.createRequest(POST, TestingUri.ResponseWithContentLength.toString(), httpHeaders);
            HttpUtil.setKeepAlive(httpRequest, isKeepAlive);
            channel.writeInbound(httpRequest);
            verifyCallbacks(processor);
            // first outbound has to be response.
            HttpResponse response = ((HttpResponse) (channel.readOutbound()));
            Assert.assertEquals("Unexpected response status", OK, response.status());
            long contentLength = HttpUtil.getContentLength(response, (-1));
            Assert.assertEquals("Unexpected Content-Length", ((MockNettyMessageProcessor.CHUNK.length) * i), contentLength);
            if (contentLength == 0) {
                // special case. Since Content-Length is set, the response should be an instance of FullHttpResponse.
                Assert.assertTrue("Response not instance of FullHttpResponse", (response instanceof FullHttpResponse));
            } else {
                HttpContent httpContent = null;
                for (int j = 0; j < i; j++) {
                    httpContent = ((HttpContent) (channel.readOutbound()));
                    byte[] returnedContent = httpContent.content().array();
                    Assert.assertArrayEquals("Content does not match with expected content", MockNettyMessageProcessor.CHUNK, returnedContent);
                }
                // the last HttpContent should also be an instance of LastHttpContent
                Assert.assertTrue("The last part of the content is not LastHttpContent", (httpContent instanceof LastHttpContent));
            }
            Assert.assertEquals("Unexpected channel state on the server", isKeepAlive, channel.isActive());
        }
    }

    /**
     * Checks the case where no body needs to be returned but just a
     * {@link RestResponseChannel#onResponseComplete(Exception)} is called on the server. This should return just
     * response metadata.
     */
    @Test
    public void noResponseBodyTest() {
        EmbeddedChannel channel = createEmbeddedChannel();
        // with Transfer-Encoding:Chunked
        HttpRequest httpRequest = RestTestUtils.createRequest(GET, TestingUri.ImmediateResponseComplete.toString(), null);
        channel.writeInbound(httpRequest);
        // There should be a response.
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", OK, response.status());
        Assert.assertTrue("Response must say 'Transfer-Encoding : chunked'", HttpUtil.isTransferEncodingChunked(response));
        // since this is Transfer-Encoding:chunked, there should be a LastHttpContent
        Assert.assertTrue("Did not receive end marker", ((channel.readOutbound()) instanceof LastHttpContent));
        Assert.assertTrue("Channel should be alive", channel.isActive());
        // with Content-Length set
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(MockNettyMessageProcessor.CHUNK_COUNT_HEADER_NAME, 0);
        httpRequest = RestTestUtils.createRequest(GET, TestingUri.ImmediateResponseComplete.toString(), headers);
        HttpUtil.setKeepAlive(httpRequest, false);
        channel.writeInbound(httpRequest);
        // There should be a response.
        response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Response must have Content-Length set to 0", 0, HttpUtil.getContentLength(response, (-1)));
        Assert.assertEquals("Unexpected response status", OK, response.status());
        // since Content-Length is set, the response should be an instance of FullHttpResponse.
        Assert.assertTrue("Response not instance of FullHttpResponse", (response instanceof FullHttpResponse));
        Assert.assertFalse("Channel should not be alive", channel.isActive());
    }

    /**
     * Performs bad state transitions and verifies that they throw the right exceptions.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void badStateTransitionsTest() throws Exception {
        // write after close.
        doBadStateTransitionTest(TestingUri.WriteAfterClose, IOException.class);
        // modify response data after it has been written to the channel
        doBadStateTransitionTest(TestingUri.ModifyResponseMetadataAfterWrite, IllegalStateException.class);
    }

    /**
     * Tests that no exceptions are thrown on repeating idempotent operations. Does <b><i>not</i></b> currently test that
     * state changes are idempotent.
     */
    @Test
    public void idempotentOperationsTest() {
        doIdempotentOperationsTest(TestingUri.MultipleClose);
        doIdempotentOperationsTest(TestingUri.MultipleOnResponseComplete);
    }

    /**
     * Tests behaviour of various functions of {@link NettyResponseChannel} under write failures.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void behaviourUnderWriteFailuresTest() throws Exception {
        onResponseCompleteUnderWriteFailureTest(TestingUri.ImmediateResponseComplete);
        onResponseCompleteUnderWriteFailureTest(TestingUri.OnResponseCompleteWithNonRestException);
        // writing to channel with a outbound handler that generates an Exception
        String message = UtilsTest.getRandomString(10);
        try {
            String content = "@@randomContent@@@";
            MockNettyMessageProcessor processor = new MockNettyMessageProcessor();
            ChannelOutboundHandler badOutboundHandler = new ExceptionOutboundHandler(new Exception(message));
            EmbeddedChannel channel = new EmbeddedChannel(badOutboundHandler, processor);
            channel.writeInbound(RestTestUtils.createRequest(GET, "/", null));
            // channel has been closed because of write failure
            channel.writeInbound(createContent(content, true));
            verifyCallbacks(processor);
            Assert.fail("Callback for write would have thrown an Exception");
        } catch (Exception e) {
            Assert.assertEquals("Exception not as expected", message, e.getMessage());
        }
        // writing to channel with a outbound handler that encounters a ClosedChannelException
        try {
            String content = "@@randomContent@@@";
            MockNettyMessageProcessor processor = new MockNettyMessageProcessor();
            ChannelOutboundHandler badOutboundHandler = new ExceptionOutboundHandler(new ClosedChannelException());
            EmbeddedChannel channel = new EmbeddedChannel(badOutboundHandler, processor);
            channel.writeInbound(RestTestUtils.createRequest(GET, "/", null));
            // channel has been closed because of write failure
            channel.writeInbound(createContent(content, true));
            verifyCallbacks(processor);
            Assert.fail("Callback for write would have thrown an Exception");
        } catch (IOException e) {
            Assert.assertTrue("Should be recognized as a client termination", Utils.isPossibleClientTermination(e));
        }
        // writing to channel with a outbound handler that generates an Error
        MockNettyMessageProcessor processor = new MockNettyMessageProcessor();
        EmbeddedChannel channel = new EmbeddedChannel(new ErrorOutboundHandler(), processor);
        try {
            channel.writeInbound(RestTestUtils.createRequest(GET, TestingUri.WriteFailureWithThrowable.toString(), null));
            verifyCallbacks(processor);
        } catch (Error e) {
            Assert.assertEquals("Unexpected error", ErrorOutboundHandler.ERROR_MESSAGE, e.getMessage());
        }
        channel = createEmbeddedChannel();
        processor = channel.pipeline().get(MockNettyMessageProcessor.class);
        channel.writeInbound(RestTestUtils.createRequest(GET, TestingUri.ResponseFailureMidway.toString(), null));
        verifyCallbacks(processor);
        Assert.assertFalse("Channel is not closed at the remote end", channel.isActive());
    }

    /**
     * Asks the server to write more data than the set Content-Length and checks behavior.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void writeMoreThanContentLengthTest() throws Exception {
        doWriteMoreThanContentLengthTest(0);
        doWriteMoreThanContentLengthTest(5);
    }

    /**
     * Tests handling of content that is larger than write buffer size. In this test case, the write buffer low and high
     * watermarks are requested to be set to 1 and 2 respectively so the content will be written byte by byte into the
     * {@link NettyResponseChannel}. This does <b><i>not</i></b> test for the same situation in a async scenario since
     * {@link EmbeddedChannel} only provides blocking semantics.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void fillWriteBufferTest() throws IOException {
        String content = "@@randomContent@@@";
        String lastContent = "@@randomLastContent@@@";
        EmbeddedChannel channel = createEmbeddedChannel();
        HttpRequest httpRequest = RestTestUtils.createRequest(GET, TestingUri.FillWriteBuffer.toString(), null);
        HttpUtil.setKeepAlive(httpRequest, false);
        channel.writeInbound(httpRequest);
        channel.writeInbound(createContent(content, false));
        channel.writeInbound(createContent(lastContent, true));
        // first outbound has to be response.
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", OK, response.status());
        // content echoed back.
        StringBuilder returnedContent = new StringBuilder();
        while ((returnedContent.length()) < (content.length())) {
            returnedContent.append(RestTestUtils.getContentString(((HttpContent) (channel.readOutbound()))));
        } 
        Assert.assertEquals("Content does not match with expected content", content, returnedContent.toString());
        // last content echoed back.
        StringBuilder returnedLastContent = new StringBuilder();
        while ((returnedLastContent.length()) < (lastContent.length())) {
            returnedLastContent.append(RestTestUtils.getContentString(((HttpContent) (channel.readOutbound()))));
        } 
        Assert.assertEquals("Content does not match with expected content", lastContent, returnedLastContent.toString());
        Assert.assertFalse("Channel not closed on the server", channel.isActive());
    }

    /**
     * Sends a request with certain headers that will copied into the response. Checks the response for those headers to
     * see that values match.
     *
     * @throws ParseException
     * 		
     */
    @Test
    public void headersPresenceTest() throws ParseException {
        HttpRequest request = createRequestWithHeaders(GET, TestingUri.CopyHeaders.toString());
        HttpUtil.setKeepAlive(request, false);
        EmbeddedChannel channel = createEmbeddedChannel();
        channel.writeInbound(request);
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertFalse("Channel not closed on the server", channel.isActive());
        checkHeaders(request, response);
    }

    /**
     * Sends null input to {@link NettyResponseChannel#setHeader(String, Object)} (through
     * {@link MockNettyMessageProcessor}) and tests for reaction.
     */
    @Test
    public void nullHeadersSetTest() {
        HttpRequest request = createRequestWithHeaders(GET, TestingUri.SetNullHeader.toString());
        HttpUtil.setKeepAlive(request, false);
        EmbeddedChannel channel = createEmbeddedChannel();
        channel.writeInbound(request);
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", ACCEPTED, response.status());
        Assert.assertFalse("Channel not closed on the server", channel.isActive());
    }

    /**
     * Tries different exception scenarios for {@link NettyResponseChannel#setRequest(NettyRequest)}.
     */
    @Test
    public void setRequestTest() {
        HttpRequest request = createRequestWithHeaders(GET, TestingUri.SetRequest.toString());
        HttpUtil.setKeepAlive(request, false);
        EmbeddedChannel channel = createEmbeddedChannel();
        channel.writeInbound(request);
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", ACCEPTED, response.status());
        Assert.assertFalse("Channel not closed on the server", channel.isActive());
    }

    /**
     * Tests setting of different available {@link ResponseStatus} codes and sees that they are recognized and converted
     * in {@link NettyResponseChannel}.
     * <p/>
     * If this test fails, a case for conversion probably needs to be added in {@link NettyResponseChannel}.
     */
    @Test
    public void setStatusTest() {
        // ask for every status to be set
        for (ResponseStatus expectedResponseStatus : ResponseStatus.values()) {
            HttpRequest request = createRequestWithHeaders(GET, TestingUri.SetStatus.toString());
            request.headers().set(MockNettyMessageProcessor.STATUS_HEADER_NAME, expectedResponseStatus);
            HttpUtil.setKeepAlive(request, false);
            EmbeddedChannel channel = createEmbeddedChannel();
            channel.writeInbound(request);
            // pull but discard response
            channel.readOutbound();
            Assert.assertFalse("Channel not closed on the server", channel.isActive());
        }
        // check if all the ResponseStatus codes were recognized.
        String metricName = MetricRegistry.name(NettyResponseChannel.class, "UnknownResponseStatusCount");
        long metricCount = MockNettyMessageProcessor.METRIC_REGISTRY.getCounters().get(metricName).getCount();
        Assert.assertEquals("Some of the ResponseStatus codes were not recognized", 0, metricCount);
    }

    /**
     * Tests that error responses are correctly formed.
     */
    @Test
    public void errorResponseTest() {
        EmbeddedChannel channel = createEmbeddedChannel();
        for (RestServiceErrorCode errorCode : RestServiceErrorCode.values()) {
            for (boolean includeExceptionMessageInResponse : new boolean[]{ true, false }) {
                HttpHeaders httpHeaders = new DefaultHttpHeaders();
                httpHeaders.set(MockNettyMessageProcessor.REST_SERVICE_ERROR_CODE_HEADER_NAME, errorCode);
                if (includeExceptionMessageInResponse) {
                    httpHeaders.set(MockNettyMessageProcessor.INCLUDE_EXCEPTION_MESSAGE_IN_RESPONSE_HEADER_NAME, "true");
                }
                channel.writeInbound(RestTestUtils.createRequest(HEAD, TestingUri.OnResponseCompleteWithRestException.toString(), httpHeaders));
                HttpResponse response = channel.readOutbound();
                HttpResponseStatus expectedStatus = getExpectedHttpResponseStatus(errorCode);
                Assert.assertEquals("Unexpected response status", expectedStatus, response.status());
                boolean containsFailureReasonHeader = response.headers().contains(FAILURE_REASON_HEADER);
                if ((expectedStatus == (HttpResponseStatus.BAD_REQUEST)) || includeExceptionMessageInResponse) {
                    Assert.assertTrue("Could not find failure reason header.", containsFailureReasonHeader);
                } else {
                    Assert.assertFalse("Should not have found failure reason header.", containsFailureReasonHeader);
                }
                if (CLIENT_ERROR.contains(response.status().code())) {
                    Assert.assertEquals("Wrong error code", errorCode, RestServiceErrorCode.valueOf(response.headers().get(ERROR_CODE_HEADER)));
                } else {
                    Assert.assertFalse("Should not have found error code header", response.headers().contains(ERROR_CODE_HEADER));
                }
                if (expectedStatus == (HttpResponseStatus.METHOD_NOT_ALLOWED)) {
                    Assert.assertEquals(("Unexpected value for " + (HttpHeaderNames.ALLOW)), MockNettyMessageProcessor.METHOD_NOT_ALLOWED_ALLOW_HEADER_VALUE, response.headers().get(ALLOW));
                }
                if (response instanceof FullHttpResponse) {
                    // assert that there is no content
                    Assert.assertEquals("The response should not contain content", 0, content().readableBytes());
                } else {
                    HttpContent content = channel.readOutbound();
                    Assert.assertTrue("End marker should be received", (content instanceof LastHttpContent));
                }
                Assert.assertNull("There should be no more data in the channel", channel.readOutbound());
                boolean shouldBeAlive = !(CLOSE_CONNECTION_ERROR_STATUSES.contains(expectedStatus));
                Assert.assertEquals("Channel state (open/close) not as expected", shouldBeAlive, channel.isActive());
                Assert.assertEquals("Connection header should be consistent with channel state", shouldBeAlive, HttpUtil.isKeepAlive(response));
                if (!shouldBeAlive) {
                    channel = createEmbeddedChannel();
                }
            }
        }
        channel.close();
    }

    /**
     * Tests that tracking headers are copied over correctly for error responses.
     */
    @Test
    public void errorResponseTrackingHeadersTest() {
        EmbeddedChannel channel = createEmbeddedChannel();
        for (boolean shouldTrackingHeadersExist : new boolean[]{ true, false }) {
            TestingUri uri;
            HttpHeaders httpHeaders;
            HttpResponse response;
            for (RestServiceErrorCode errorCode : RestServiceErrorCode.values()) {
                httpHeaders = new DefaultHttpHeaders();
                if (shouldTrackingHeadersExist) {
                    addTrackingHeaders(httpHeaders);
                }
                uri = (shouldTrackingHeadersExist) ? TestingUri.CopyHeadersAndOnResponseCompleteWithRestException : TestingUri.OnResponseCompleteWithRestException;
                httpHeaders.set(MockNettyMessageProcessor.REST_SERVICE_ERROR_CODE_HEADER_NAME, errorCode);
                channel.writeInbound(RestTestUtils.createRequest(HEAD, uri.toString(), httpHeaders));
                response = channel.readOutbound();
                verifyTrackingHeaders(response, shouldTrackingHeadersExist);
            }
            httpHeaders = new DefaultHttpHeaders();
            if (shouldTrackingHeadersExist) {
                addTrackingHeaders(httpHeaders);
            }
            uri = (shouldTrackingHeadersExist) ? TestingUri.CopyHeadersAndOnResponseCompleteWithNonRestException : TestingUri.OnResponseCompleteWithNonRestException;
            channel.writeInbound(RestTestUtils.createRequest(HEAD, uri.toString(), httpHeaders));
            response = channel.readOutbound();
            verifyTrackingHeaders(response, shouldTrackingHeadersExist);
        }
    }

    /**
     * Tests keep-alive for different HTTP methods and error statuses.
     */
    @Test
    public void keepAliveTest() {
        HttpMethod[] HTTP_METHODS = new HttpMethod[]{ HttpMethod.POST, HttpMethod.PUT, HttpMethod.GET, HttpMethod.HEAD, HttpMethod.DELETE };
        EmbeddedChannel channel = createEmbeddedChannel();
        for (HttpMethod httpMethod : HTTP_METHODS) {
            for (RestServiceErrorCode errorCode : RestServiceErrorCode.values()) {
                channel = doKeepAliveTest(channel, httpMethod, errorCode, getExpectedHttpResponseStatus(errorCode), 0, null);
            }
            channel = doKeepAliveTest(channel, httpMethod, null, INTERNAL_SERVER_ERROR, 0, null);
            channel = doKeepAliveTest(channel, httpMethod, null, INTERNAL_SERVER_ERROR, 0, true);
            channel = doKeepAliveTest(channel, httpMethod, null, INTERNAL_SERVER_ERROR, 0, false);
        }
        // special test for put because the keep alive depends on content size (0 already tested above)
        channel = doKeepAliveTest(channel, PUT, null, INTERNAL_SERVER_ERROR, 1, null);
        channel = doKeepAliveTest(channel, PUT, null, INTERNAL_SERVER_ERROR, 100, null);
        channel.close();
    }

    /**
     * Tests that client initiated terminations don't count towards {@link HttpResponseStatus#INTERNAL_SERVER_ERROR}.
     */
    @Test
    public void clientEarlyTerminationTest() throws Exception {
        EmbeddedChannel channel = createEmbeddedChannel();
        TestingUri uri = TestingUri.OnResponseCompleteWithEarlyClientTermination;
        HttpRequest httpRequest = RestTestUtils.createRequest(POST, uri.toString(), null);
        HttpUtil.setKeepAlive(httpRequest, false);
        String iseMetricName = MetricRegistry.name(NettyResponseChannel.class, "InternalServerErrorCount");
        long iseBeforeCount = MockNettyMessageProcessor.METRIC_REGISTRY.getCounters().get(iseMetricName).getCount();
        String cetMetricName = MetricRegistry.name(NettyResponseChannel.class, "ClientEarlyTerminationCount");
        long cetBeforeCount = MockNettyMessageProcessor.METRIC_REGISTRY.getCounters().get(cetMetricName).getCount();
        channel.writeInbound(httpRequest);
        // first outbound has to be response.
        HttpResponse response = channel.readOutbound();
        Assert.assertEquals("Unexpected response status", INTERNAL_SERVER_ERROR, response.status());
        if (!(response instanceof FullHttpResponse)) {
            // empty the channel
            while ((channel.readOutbound()) != null) {
            } 
        }
        Assert.assertEquals("Client terminations should not count towards InternalServerError count", iseBeforeCount, MockNettyMessageProcessor.METRIC_REGISTRY.getCounters().get(iseMetricName).getCount());
        Assert.assertEquals("Client terminations should have been tracked", (cetBeforeCount + 1), MockNettyMessageProcessor.METRIC_REGISTRY.getCounters().get(cetMetricName).getCount());
    }

    /**
     * Tests that the underlying network channel is closed when {@link NettyResponseChannel#close()} is called.
     */
    @Test
    public void closeTest() {
        // request is keep-alive by default.
        HttpRequest request = createRequestWithHeaders(GET, TestingUri.Close.toString());
        EmbeddedChannel channel = createEmbeddedChannel();
        channel.writeInbound(request);
        HttpResponse response = ((HttpResponse) (channel.readOutbound()));
        Assert.assertEquals("Unexpected response status", INTERNAL_SERVER_ERROR, response.status());
        Assert.assertFalse("Inconsistent value for Connection header", HttpUtil.isKeepAlive(response));
        // drain the channel of content.
        while ((channel.readOutbound()) != null) {
        } 
        Assert.assertFalse("Channel should be closed", channel.isOpen());
    }
}

