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
package com.amazonaws.http.timers.client;


import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.MockServerTestBase;
import com.amazonaws.http.apache.client.impl.ApacheHttpClientFactory;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.http.request.RequestHandlerTestUtils;
import com.amazonaws.http.request.SlowRequestHandler;
import com.amazonaws.http.response.DummyResponseHandler;
import com.amazonaws.http.response.UnresponsiveResponseHandler;
import com.amazonaws.http.settings.HttpClientSettings;
import com.amazonaws.http.timers.ClientExecutionAndRequestTimerTestUtils;
import com.amazonaws.http.timers.TimeoutTestConstants;
import java.io.IOException;
import java.util.List;
import org.apache.http.pool.ConnPoolControl;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DummySuccessfulResponseServerIntegrationTests extends MockServerTestBase {
    private static final int STATUS_CODE = 200;

    private AmazonHttpClient httpClient;

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowResponseHandler_ThrowsClientExecutionTimeoutException() throws Exception {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT));
        requestBuilder().execute(new UnresponsiveResponseHandler());
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowAfterResponseRequestHandler_ThrowsClientExecutionTimeoutException() throws Exception {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT));
        List<RequestHandler2> requestHandlers = RequestHandlerTestUtils.buildRequestHandlerList(new SlowRequestHandler().withAfterResponseWaitInSeconds(TimeoutTestConstants.SLOW_REQUEST_HANDLER_TIMEOUT));
        requestBuilder().executionContext(withHandlers(requestHandlers)).execute(new DummyResponseHandler());
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowBeforeRequestRequestHandler_ThrowsClientExecutionTimeoutException() throws Exception {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT));
        List<RequestHandler2> requestHandlers = RequestHandlerTestUtils.buildRequestHandlerList(new SlowRequestHandler().withBeforeRequestWaitInSeconds(TimeoutTestConstants.SLOW_REQUEST_HANDLER_TIMEOUT));
        requestBuilder().executionContext(withHandlers(requestHandlers)).execute(new DummyResponseHandler());
    }

    /**
     * Tests that a streaming operation has it's request properly cleaned up if the client is interrupted after the
     * response is received.
     *
     * @see TT0070103230
     */
    @Test
    public void clientInterruptedDuringResponseHandlers_DoesNotLeakConnection() throws IOException {
        ClientConfiguration config = new ClientConfiguration();
        ConnectionManagerAwareHttpClient rawHttpClient = new ApacheHttpClientFactory().create(HttpClientSettings.adapt(config));
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        ClientExecutionAndRequestTimerTestUtils.interruptCurrentThreadAfterDelay(1000);
        List<RequestHandler2> requestHandlers = RequestHandlerTestUtils.buildRequestHandlerList(new SlowRequestHandler().withAfterResponseWaitInSeconds(10));
        try {
            requestBuilder().executionContext(withHandlers(requestHandlers)).execute(new DummyResponseHandler().leaveConnectionOpen());
            Assert.fail("Expected exception");
        } catch (AmazonClientException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(InterruptedException.class));
        }
        @SuppressWarnings("deprecation")
        int leasedConnections = ((ConnPoolControl<?>) (getHttpClientConnectionManager())).getTotalStats().getLeased();
        Assert.assertEquals(0, leasedConnections);
    }
}

