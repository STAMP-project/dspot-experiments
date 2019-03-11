/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http.timers.request;


import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.http.response.ErrorDuringUnmarshallingResponseHandler;
import com.amazonaws.http.response.HttpResponseProxy;
import com.amazonaws.http.response.NullResponseHandler;
import com.amazonaws.http.timers.ClientExecutionAndRequestTimerTestUtils;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HttpContext;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * These tests don't actually start up a mock server. They use a partially mocked Apache HTTP client
 * to return the desired response
 */
public class MockedClientTests {
    private AmazonHttpClient httpClient;

    @Test
    public void requestTimeoutEnabled_RequestCompletesWithinTimeout_TaskCanceledAndEntityBuffered() throws Exception {
        ClientConfiguration config = new ClientConfiguration().withRequestTimeout((5 * 1000)).withMaxErrorRetry(0);
        ConnectionManagerAwareHttpClient rawHttpClient = ClientExecutionAndRequestTimerTestUtils.createRawHttpClientSpy(config);
        HttpResponseProxy responseProxy = ClientExecutionAndRequestTimerTestUtils.createHttpResponseProxySpy();
        Mockito.doReturn(responseProxy).when(rawHttpClient).execute(ArgumentMatchers.any(HttpRequestBase.class), ArgumentMatchers.any(HttpContext.class));
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        try {
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, ClientExecutionAndRequestTimerTestUtils.createMockGetRequest());
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            NullResponseHandler.assertIsUnmarshallingException(e);
        }
        ClientExecutionAndRequestTimerTestUtils.assertResponseIsBuffered(responseProxy);
        ScheduledThreadPoolExecutor requestTimerExecutor = httpClient.getHttpRequestTimer().getExecutor();
        ClientExecutionAndRequestTimerTestUtils.assertTimerNeverTriggered(requestTimerExecutor);
        ClientExecutionAndRequestTimerTestUtils.assertCanceledTasksRemoved(requestTimerExecutor);
        // Core threads should be spun up on demand. Since only one task was submitted only one
        // thread should exist
        Assert.assertEquals(1, requestTimerExecutor.getPoolSize());
        ClientExecutionAndRequestTimerTestUtils.assertCoreThreadsShutDownAfterBeingIdle(requestTimerExecutor);
    }

    /**
     * Response to HEAD requests don't have an entity so we shouldn't try to wrap the response in a
     * {@link BufferedHttpEntity}.
     */
    @Test
    public void requestTimeoutEnabled_HeadRequestCompletesWithinTimeout_EntityNotBuffered() throws Exception {
        ClientConfiguration config = new ClientConfiguration().withRequestTimeout((5 * 1000)).withMaxErrorRetry(0);
        ConnectionManagerAwareHttpClient rawHttpClient = ClientExecutionAndRequestTimerTestUtils.createRawHttpClientSpy(config);
        HttpResponseProxy responseProxy = ClientExecutionAndRequestTimerTestUtils.createHttpHeadResponseProxy();
        Mockito.doReturn(responseProxy).when(rawHttpClient).execute(ArgumentMatchers.any(HttpHead.class), ArgumentMatchers.any(HttpContext.class));
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        try {
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, ClientExecutionAndRequestTimerTestUtils.createMockHeadRequest());
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            NullResponseHandler.assertIsUnmarshallingException(e);
        }
        Assert.assertNull(responseProxy.getEntity());
    }

    @Test
    public void requestTimeoutDisabled_RequestCompletesWithinTimeout_EntityNotBuffered() throws Exception {
        ClientConfiguration config = new ClientConfiguration().withRequestTimeout(0);
        ConnectionManagerAwareHttpClient rawHttpClient = ClientExecutionAndRequestTimerTestUtils.createRawHttpClientSpy(config);
        HttpResponseProxy responseProxy = ClientExecutionAndRequestTimerTestUtils.createHttpResponseProxySpy();
        Mockito.doReturn(responseProxy).when(rawHttpClient).execute(ArgumentMatchers.any(HttpRequestBase.class), ArgumentMatchers.any(HttpContext.class));
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        try {
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, ClientExecutionAndRequestTimerTestUtils.createMockGetRequest());
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
        }
        ClientExecutionAndRequestTimerTestUtils.assertResponseWasNotBuffered(responseProxy);
    }

    @Test
    public void requestTimeoutEnabled_RequestCompletesWithinTimeout_EntityNotBufferedForStreamedResponse() throws Exception {
        ClientConfiguration config = new ClientConfiguration().withRequestTimeout((5 * 1000));
        ConnectionManagerAwareHttpClient rawHttpClient = ClientExecutionAndRequestTimerTestUtils.createRawHttpClientSpy(config);
        HttpResponseProxy responseProxy = ClientExecutionAndRequestTimerTestUtils.createHttpResponseProxySpy();
        Mockito.doReturn(responseProxy).when(rawHttpClient).execute(ArgumentMatchers.any(HttpRequestBase.class), ArgumentMatchers.any(HttpContext.class));
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        try {
            httpClient.requestExecutionBuilder().request(ClientExecutionAndRequestTimerTestUtils.createMockGetRequest()).execute(new ErrorDuringUnmarshallingResponseHandler().leaveConnectionOpen());
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
        }
        ClientExecutionAndRequestTimerTestUtils.assertResponseWasNotBuffered(responseProxy);
    }
}

