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


import ByteBufferRSC.Event;
import RestMethod.GET;
import RestServiceErrorCode.RequestResponseQueuingFailure;
import RestServiceErrorCode.ServiceUnavailable;
import com.codahale.metrics.MetricRegistry;
import com.github.ambry.commons.ByteBufferReadableStreamChannel;
import com.github.ambry.config.VerifiableProperties;
import com.github.ambry.router.ByteBufferRSC;
import com.github.ambry.router.ReadableStreamChannel;
import com.github.ambry.router.Router;
import com.github.ambry.utils.TestUtils;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import static MockRestResponseChannel.Event.OnRequestComplete;
import static RestMethod.PUT;
import static RestMethod.UNKNOWN;


/**
 * Tests functionality of {@link AsyncRequestResponseHandler}.
 */
public class AsyncRequestResponseHandlerTest {
    private static VerifiableProperties verifiableProperties;

    private static Router router;

    private static MockBlobStorageService blobStorageService;

    private static AsyncRequestResponseHandler asyncRequestResponseHandler;

    /**
     * Tests {@link AsyncRequestResponseHandler#start()} and {@link AsyncRequestResponseHandler#shutdown()}.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void startShutdownTest() throws IOException {
        final int EXPECTED_WORKER_COUNT = new Random().nextInt(10);
        AsyncRequestResponseHandler handler = AsyncRequestResponseHandlerTest.getAsyncRequestResponseHandler(EXPECTED_WORKER_COUNT);
        Assert.assertEquals("Number of workers alive is incorrect", 0, handler.getWorkersAlive());
        Assert.assertFalse("IsRunning should be false", handler.isRunning());
        handler.start();
        try {
            Assert.assertTrue("IsRunning should be true", handler.isRunning());
            Assert.assertEquals("Number of workers alive is incorrect", EXPECTED_WORKER_COUNT, handler.getWorkersAlive());
        } finally {
            handler.shutdown();
        }
        Assert.assertFalse("IsRunning should be false", handler.isRunning());
    }

    /**
     * Tests for {@link AsyncRequestResponseHandler#shutdown()} when {@link AsyncRequestResponseHandler#start()} has not
     * been called previously. This test is for cases where {@link AsyncRequestResponseHandler#start()} has failed and
     * {@link AsyncRequestResponseHandler#shutdown()} needs to be run.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void shutdownWithoutStart() throws IOException {
        AsyncRequestResponseHandler handler = AsyncRequestResponseHandlerTest.getAsyncRequestResponseHandler(1);
        handler.shutdown();
    }

    /**
     * This tests for exceptions thrown when a {@link AsyncRequestResponseHandler} is used without calling
     * {@link AsyncRequestResponseHandler#start()}first.
     *
     * @throws IOException
     * 		
     * @throws JSONException
     * 		
     * @throws URISyntaxException
     * 		
     */
    @Test
    public void useServiceWithoutStartTest() throws IOException, URISyntaxException, JSONException {
        AsyncRequestResponseHandler handler = AsyncRequestResponseHandlerTest.getAsyncRequestResponseHandler(1);
        RestRequest restRequest = createRestRequest(GET, "/", null, null);
        try {
            handler.handleRequest(restRequest, new MockRestResponseChannel());
            Assert.fail("Should have thrown RestServiceException because the AsyncRequestResponseHandler has not been started");
        } catch (RestServiceException e) {
            Assert.assertEquals("The RestServiceErrorCode does not match", ServiceUnavailable, e.getErrorCode());
        }
        restRequest = createRestRequest(GET, "/", null, null);
        restRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
        try {
            handler.handleResponse(restRequest, new MockRestResponseChannel(), new ByteBufferReadableStreamChannel(ByteBuffer.allocate(0)), null);
            Assert.fail("Should have thrown RestServiceException because the AsyncRequestResponseHandler has not been started");
        } catch (RestServiceException e) {
            Assert.assertEquals("The RestServiceErrorCode does not match", ServiceUnavailable, e.getErrorCode());
        }
    }

    /**
     * Tests the behavior of {@link AsyncRequestResponseHandler} when request worker count is not set or is zero.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void edgeCaseWorkerCountsTest() throws Exception {
        RequestResponseHandlerMetrics metrics = new RequestResponseHandlerMetrics(new MetricRegistry());
        AsyncRequestResponseHandler requestResponseHandler = new AsyncRequestResponseHandler(metrics);
        noRequestHandlersTest(requestResponseHandler);
        requestResponseHandler = AsyncRequestResponseHandlerTest.getAsyncRequestResponseHandler(0);
        noRequestHandlersTest(requestResponseHandler);
    }

    @Test
    public void setFunctionsBadArgumentsTest() {
        RequestResponseHandlerMetrics metrics = new RequestResponseHandlerMetrics(new MetricRegistry());
        AsyncRequestResponseHandler requestResponseHandler = new AsyncRequestResponseHandler(metrics);
        // set request workers < 0
        try {
            requestResponseHandler.setupRequestHandling((-1), AsyncRequestResponseHandlerTest.blobStorageService);
            Assert.fail("Setting request workers < 0 should have thrown exception");
        } catch (IllegalArgumentException e) {
            // expected. nothing to do.
        }
        // set null BlobStorageService
        try {
            requestResponseHandler.setupRequestHandling(1, null);
            Assert.fail("Setting BlobStorageService to null should have thrown exception");
        } catch (IllegalArgumentException e) {
            // expected. nothing to do.
        }
    }

    /**
     * Tests behavior of {@link AsyncRequestResponseHandler#setupRequestHandling(int, BlobStorageService)} after the
     * {@link AsyncRequestResponseHandler} has been started.
     */
    @Test
    public void setupRequestHandlingStartTest() {
        // set request workers.
        try {
            AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.setupRequestHandling(5, AsyncRequestResponseHandlerTest.blobStorageService);
            Assert.fail("Setting request workers after start should have thrown exception");
        } catch (IllegalStateException e) {
            // expected. nothing to do.
        }
    }

    /**
     * Tests handling of all {@link RestMethod}s. The {@link MockBlobStorageService} instance being used is
     * asked to only echo the method.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void allRestMethodsSuccessTest() throws Exception {
        for (int i = 0; i < 25; i++) {
            for (RestMethod restMethod : RestMethod.values()) {
                // PUT is not supported, so it always fails.
                if ((restMethod != (UNKNOWN)) && (restMethod != (PUT))) {
                    doHandleRequestSuccessTest(restMethod, AsyncRequestResponseHandlerTest.asyncRequestResponseHandler);
                }
            }
        }
    }

    /**
     * Tests that right exceptions are thrown on bad input to
     * {@link AsyncRequestResponseHandler#handleRequest(RestRequest, RestResponseChannel)}. These are exceptions that get
     * thrown before queuing.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void handleRequestFailureBeforeQueueTest() throws Exception {
        // RestRequest null.
        try {
            AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.handleRequest(null, new MockRestResponseChannel());
            Assert.fail("Test should have thrown exception, but did not");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // RestResponseChannel null.
        RestRequest restRequest = createRestRequest(GET, "/", new JSONObject(), null);
        try {
            AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.handleRequest(restRequest, null);
            Assert.fail("Test should have thrown exception, but did not");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // AsyncRequestResponseHandler should still be alive and serving requests
        Assert.assertTrue("AsyncRequestResponseHandler is dead", AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.isRunning());
    }

    /**
     * Tests that right exceptions are thrown on bad input while a de-queued {@link RestRequest} is being handled.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void handleRequestFailureOnDequeueTest() throws Exception {
        unknownRestMethodTest(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler);
        putRestMethodTest(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler);
        delayedHandleRequestThatThrowsRestException(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler);
        delayedHandleRequestThatThrowsRuntimeException(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler);
    }

    /**
     * Tests {@link AsyncRequestResponseHandler#handleResponse(RestRequest, RestResponseChannel, ReadableStreamChannel, Exception)} with good input.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void handleResponseSuccessTest() throws Exception {
        for (int i = 0; i < 100; i++) {
            doHandleResponseSuccessTest(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler);
        }
    }

    /**
     * Tests the reaction of {@link AsyncRequestResponseHandler#handleResponse(RestRequest, RestResponseChannel, ReadableStreamChannel, Exception)} to some misbehaving components.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void handleResponseExceptionTest() throws Exception {
        ByteBufferRSC response = new ByteBufferRSC(ByteBuffer.allocate(0));
        // RestRequest null.
        try {
            AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.handleResponse(null, new MockRestResponseChannel(), response, null);
            Assert.fail("Test should have thrown exception, but did not");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // RestResponseChannel null.
        MockRestRequest restRequest = createRestRequest(GET, "/", new JSONObject(), null);
        restRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
        try {
            AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.handleResponse(restRequest, null, response, null);
            Assert.fail("Test should have thrown exception, but did not");
        } catch (IllegalArgumentException e) {
            // expected. Nothing to do.
        }
        // Writing response throws exception.
        MockRestRequest goodRestRequest = createRestRequest(GET, "/", null, null);
        goodRestRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
        MockRestResponseChannel restResponseChannel = new MockRestResponseChannel();
        String exceptionMsg = "@@randomMsg@@";
        ReadableStreamChannel badResponse = new IncompleteReadReadableStreamChannel(null, new Exception(exceptionMsg));
        awaitResponse(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler, goodRestRequest, restResponseChannel, badResponse, null);
        Assert.assertNotNull("MockRestResponseChannel would have been passed an exception", restResponseChannel.getException());
        Assert.assertEquals("Exception message does not match", exceptionMsg, restResponseChannel.getException().getMessage());
        // Writing response is incomplete
        goodRestRequest = createRestRequest(GET, "/", null, null);
        goodRestRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
        restResponseChannel = new MockRestResponseChannel();
        badResponse = new IncompleteReadReadableStreamChannel(0L, null);
        awaitResponse(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler, goodRestRequest, restResponseChannel, badResponse, null);
        Assert.assertNotNull("MockRestResponseChannel would have been passed an exception", restResponseChannel.getException());
        Assert.assertEquals("Unexpected exception", IllegalStateException.class, restResponseChannel.getException().getClass());
        // No bytes read and no exception
        goodRestRequest = createRestRequest(GET, "/", null, null);
        goodRestRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
        restResponseChannel = new MockRestResponseChannel();
        badResponse = new IncompleteReadReadableStreamChannel(null, null);
        awaitResponse(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler, goodRestRequest, restResponseChannel, badResponse, null);
        Assert.assertNotNull("MockRestResponseChannel would have been passed an exception", restResponseChannel.getException());
        Assert.assertEquals("Unexpected exception", IllegalStateException.class, restResponseChannel.getException().getClass());
        // RestRequest is bad.
        BadRestRequest badRestRequest = new BadRestRequest();
        badRestRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
        restResponseChannel = new MockRestResponseChannel();
        ByteBufferRSC goodResponse = new ByteBufferRSC(ByteBuffer.allocate(0));
        EventMonitor<ByteBufferRSC.Event> responseCloseMonitor = new EventMonitor<ByteBufferRSC.Event>(Event.Close);
        goodResponse.addListener(responseCloseMonitor);
        awaitResponse(AsyncRequestResponseHandlerTest.asyncRequestResponseHandler, badRestRequest, restResponseChannel, goodResponse, null);
        Assert.assertTrue("Response is not closed", responseCloseMonitor.awaitEvent(1, TimeUnit.SECONDS));
        // AsyncRequestResponseHandler should still be alive and serving requests
        Assert.assertTrue("AsyncRequestResponseHandler is dead", AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.isRunning());
    }

    /**
     * Tests various functions when multiple responses are in flight.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void multipleResponsesInFlightTest() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        AsyncRequestResponseHandler responseHandler = AsyncRequestResponseHandlerTest.getAsyncRequestResponseHandler(0);
        responseHandler.start();
        try {
            final int EXPECTED_QUEUE_SIZE = 5;
            // test for duplicate request submission
            // submit a few responses that halt on read
            CountDownLatch releaseRead = new CountDownLatch(1);
            byte[] data = null;
            RestRequest restRequest = null;
            MockRestResponseChannel restResponseChannel = null;
            ReadableStreamChannel response = null;
            for (int i = 0; i < EXPECTED_QUEUE_SIZE; i++) {
                data = TestUtils.getRandomBytes(32);
                response = new HaltingRSC(ByteBuffer.wrap(data), releaseRead, executorService);
                restRequest = createRestRequest(GET, "/", null, null);
                restRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
                restResponseChannel = new MockRestResponseChannel(restRequest);
                responseHandler.handleResponse(restRequest, restResponseChannel, response, null);
            }
            Assert.assertEquals("Response set size unexpected", EXPECTED_QUEUE_SIZE, responseHandler.getResponseSetSize());
            // attach a listener to the last MockRestResponseChannel
            EventMonitor<MockRestResponseChannel.Event> eventMonitor = new EventMonitor<MockRestResponseChannel.Event>(OnRequestComplete);
            restResponseChannel.addListener(eventMonitor);
            // we try to re submit the last response. This should fail.
            try {
                responseHandler.handleResponse(restRequest, restResponseChannel, response, null);
                Assert.fail("Trying to resubmit a response that is already in flight should have failed");
            } catch (RestServiceException e) {
                Assert.assertEquals("Unexpected error code", RequestResponseQueuingFailure, e.getErrorCode());
            }
            Assert.assertEquals("Response set size unexpected", EXPECTED_QUEUE_SIZE, responseHandler.getResponseSetSize());
            releaseRead.countDown();
            if (!(eventMonitor.awaitEvent(1, TimeUnit.SECONDS))) {
                Assert.fail("awaitResponse took too long. There might be a problem or the timeout may need to be increased");
            }
            // compare the output. We care only about the last one because we want to make sure the duplicate submission
            // did not mess with the original
            Assert.assertArrayEquals("Unexpected data in the response", data, restResponseChannel.getResponseBody());
            // test for shutdown when responses are still in progress
            releaseRead = new CountDownLatch(1);
            List<MockRestResponseChannel> responseChannels = new ArrayList<MockRestResponseChannel>(EXPECTED_QUEUE_SIZE);
            for (int i = 0; i < EXPECTED_QUEUE_SIZE; i++) {
                response = new HaltingRSC(ByteBuffer.allocate(0), releaseRead, executorService);
                restRequest = createRestRequest(GET, "/", null, null);
                restRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
                restResponseChannel = new MockRestResponseChannel(restRequest);
                responseHandler.handleResponse(restRequest, restResponseChannel, response, null);
                responseChannels.add(restResponseChannel);
            }
            Assert.assertEquals("Response set size unexpected", EXPECTED_QUEUE_SIZE, responseHandler.getResponseSetSize());
            responseHandler.shutdown();
            Assert.assertEquals("Response set size unexpected", 0, responseHandler.getResponseSetSize());
            releaseRead.countDown();
            // all of the responses should have exceptions
            for (MockRestResponseChannel channel : responseChannels) {
                Assert.assertNotNull("There should be an exception", channel.getException());
                if ((channel.getException()) instanceof RestServiceException) {
                    RestServiceException rse = ((RestServiceException) (channel.getException()));
                    Assert.assertEquals("Unexpected RestServiceErrorCode", ServiceUnavailable, rse.getErrorCode());
                } else {
                    throw channel.getException();
                }
            }
        } finally {
            responseHandler.shutdown();
            executorService.shutdown();
        }
    }

    /**
     * Tests various functions when multiple requests are in queue.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void multipleRequestsInQueueTest() throws Exception {
        final int EXPECTED_MIN_QUEUE_SIZE = 5;
        AsyncRequestResponseHandlerTest.blobStorageService.blockAllOperations();
        try {
            // the first request that each worker processes will block.
            for (int i = 0; i < (EXPECTED_MIN_QUEUE_SIZE + (AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.getWorkersAlive())); i++) {
                MockRestRequest restRequest = createRestRequest(GET, "/", null, null);
                restRequest.getMetricsTracker().scalingMetricsTracker.markRequestReceived();
                MockRestResponseChannel restResponseChannel = new MockRestResponseChannel(restRequest);
                AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.handleRequest(restRequest, restResponseChannel);
            }
            Assert.assertTrue(((("Request queue size should be at least " + EXPECTED_MIN_QUEUE_SIZE) + ". Is ") + (AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.getRequestQueueSize())), ((AsyncRequestResponseHandlerTest.asyncRequestResponseHandler.getRequestQueueSize()) >= EXPECTED_MIN_QUEUE_SIZE));
        } finally {
            AsyncRequestResponseHandlerTest.blobStorageService.releaseAllOperations();
        }
    }
}

