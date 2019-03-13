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


import com.amazonaws.ClientConfiguration;
import com.amazonaws.handlers.RequestHandler2;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import com.amazonaws.http.MockServerTestBase;
import com.amazonaws.http.request.RequestHandlerTestUtils;
import com.amazonaws.http.request.SlowRequestHandler;
import com.amazonaws.http.response.NullErrorResponseHandler;
import com.amazonaws.http.response.UnresponsiveErrorResponseHandler;
import com.amazonaws.http.timers.TimeoutTestConstants;
import java.util.List;
import org.junit.Test;


/**
 * Tests that use a server that returns a predetermined error response within the timeout limit
 */
public class DummyErrorResponseServerIntegrationTests extends MockServerTestBase {
    private static final int STATUS_CODE = 500;

    private AmazonHttpClient httpClient;

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowErrorResponseHandler_ThrowsClientExecutionTimeoutException() throws Exception {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT));
        httpClient.requestExecutionBuilder().request(newGetRequest()).errorResponseHandler(new UnresponsiveErrorResponseHandler()).execute();
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT, expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_SlowAfterErrorRequestHandler_ThrowsClientExecutionTimeoutException() throws Exception {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT));
        List<RequestHandler2> requestHandlers = RequestHandlerTestUtils.buildRequestHandlerList(new SlowRequestHandler().withAfterErrorWaitInSeconds(TimeoutTestConstants.SLOW_REQUEST_HANDLER_TIMEOUT));
        httpClient.requestExecutionBuilder().request(newGetRequest()).errorResponseHandler(new NullErrorResponseHandler()).executionContext(ExecutionContext.builder().withRequestHandler2s(requestHandlers).build()).execute();
    }
}

