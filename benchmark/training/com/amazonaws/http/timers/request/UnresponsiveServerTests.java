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
import com.amazonaws.http.UnresponsiveMockServerTestBase;
import com.amazonaws.http.exception.HttpRequestTimeoutException;
import com.amazonaws.http.request.EmptyHttpRequest;
import com.amazonaws.http.timers.ClientExecutionAndRequestTimerTestUtils;
import com.amazonaws.http.timers.TimeoutTestConstants;
import java.net.SocketTimeoutException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests requiring an unresponsive server, that is a server that accepts a connection but doesn't
 * write any data to the response
 */
public class UnresponsiveServerTests extends UnresponsiveMockServerTestBase {
    private static final int REQUEST_TIMEOUT = 5 * 1000;

    private static final int LONGER_SOCKET_TIMEOUT = (UnresponsiveServerTests.REQUEST_TIMEOUT) * (TimeoutTestConstants.PRECISION_MULTIPLIER);

    private static final int SHORTER_SOCKET_TIMEOUT = (UnresponsiveServerTests.REQUEST_TIMEOUT) / (TimeoutTestConstants.PRECISION_MULTIPLIER);

    private AmazonHttpClient httpClient;

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT)
    public void requestTimeoutDisabled_ConnectionClosedBySocketTimeout_NoThreadsCreated() {
        final int socketTimeout = 1000;
        httpClient = new AmazonHttpClient(new ClientConfiguration().withSocketTimeout(socketTimeout).withRequestTimeout(0).withMaxErrorRetry(0));
        try {
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, newGetRequest());
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(SocketTimeoutException.class));
            ClientExecutionAndRequestTimerTestUtils.assertRequestTimerExecutorNotCreated(httpClient.getHttpRequestTimer());
        }
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT)
    public void requestTimeoutSetInRequestObject_WithLongerSocketTimeout_ThrowsRequestTimeoutException() {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withSocketTimeout(UnresponsiveServerTests.LONGER_SOCKET_TIMEOUT).withMaxErrorRetry(0));
        try {
            EmptyHttpRequest request = newGetRequest();
            request.setOriginalRequest(withSdkRequestTimeout(UnresponsiveServerTests.REQUEST_TIMEOUT));
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, request);
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(HttpRequestTimeoutException.class));
        }
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT)
    public void requestTimeoutSetInRequestObject_WithShorterSocketTimeout_ThrowsRequestTimeoutException() {
        httpClient = new AmazonHttpClient(new ClientConfiguration().withSocketTimeout(UnresponsiveServerTests.SHORTER_SOCKET_TIMEOUT).withMaxErrorRetry(0));
        try {
            EmptyHttpRequest request = newGetRequest();
            request.setOriginalRequest(withSdkRequestTimeout(UnresponsiveServerTests.REQUEST_TIMEOUT));
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, request);
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(SocketTimeoutException.class));
        }
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT)
    public void requestTimeoutSetInRequestObject_TakesPrecedenceOverClientConfiguration() {
        // Client configuration is set arbitrarily high so that the test will timeout if the
        // client configuration is incorrectly honored over the request config
        httpClient = new AmazonHttpClient(new ClientConfiguration().withSocketTimeout(UnresponsiveServerTests.LONGER_SOCKET_TIMEOUT).withRequestTimeout(((UnresponsiveServerTests.REQUEST_TIMEOUT) * 1000)).withMaxErrorRetry(0));
        try {
            EmptyHttpRequest request = newGetRequest();
            request.setOriginalRequest(withSdkRequestTimeout(UnresponsiveServerTests.REQUEST_TIMEOUT));
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, request);
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(HttpRequestTimeoutException.class));
        }
    }

    @Test(timeout = TimeoutTestConstants.TEST_TIMEOUT)
    public void requestTimeoutDisabledInRequestObject_TakesPrecedenceOverClientConfiguration() {
        final int socketTimeout = UnresponsiveServerTests.REQUEST_TIMEOUT;
        // Client configuration is set arbitrarily low so that the request will be aborted if
        // the client configuration is incorrectly honored over the request config
        httpClient = new AmazonHttpClient(new ClientConfiguration().withSocketTimeout(socketTimeout).withRequestTimeout(1).withMaxErrorRetry(0));
        try {
            EmptyHttpRequest request = newGetRequest();
            request.setOriginalRequest(withSdkRequestTimeout(0));
            ClientExecutionAndRequestTimerTestUtils.execute(httpClient, request);
            Assert.fail("Exception expected");
        } catch (AmazonClientException e) {
            Assert.assertThat(e.getCause(), Matchers.instanceOf(SocketTimeoutException.class));
        }
    }
}

