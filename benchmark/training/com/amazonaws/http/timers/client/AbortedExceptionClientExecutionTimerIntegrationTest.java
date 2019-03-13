/**
 * Copyright (c) 2016. Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.amazonaws.http.timers.client;


import com.amazonaws.AbortedException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.http.MockServerTestBase;
import com.amazonaws.http.apache.client.impl.ConnectionManagerAwareHttpClient;
import com.amazonaws.http.request.EmptyHttpRequest;
import com.amazonaws.http.timers.ClientExecutionAndRequestTimerTestUtils;
import com.amazonaws.http.timers.TimeoutTestConstants;
import com.amazonaws.internal.SdkBufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 */
public class AbortedExceptionClientExecutionTimerIntegrationTest extends MockServerTestBase {
    private AmazonHttpClient httpClient;

    @Test(expected = AbortedException.class)
    public void clientExecutionTimeoutEnabled_aborted_exception_occurs_timeout_not_expired() throws Exception {
        ClientConfiguration config = new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT).withMaxErrorRetry(0);
        ConnectionManagerAwareHttpClient rawHttpClient = ClientExecutionAndRequestTimerTestUtils.createRawHttpClientSpy(config);
        Mockito.doThrow(new AbortedException()).when(rawHttpClient).execute(ArgumentMatchers.any(HttpRequestBase.class), ArgumentMatchers.any(HttpContext.class));
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        ClientExecutionAndRequestTimerTestUtils.execute(httpClient, ClientExecutionAndRequestTimerTestUtils.createMockGetRequest());
    }

    @Test(expected = ClientExecutionTimeoutException.class)
    public void clientExecutionTimeoutEnabled_aborted_exception_occurs_timeout_expired() throws Exception {
        ClientConfiguration config = new ClientConfiguration().withClientExecutionTimeout(TimeoutTestConstants.CLIENT_EXECUTION_TIMEOUT).withMaxErrorRetry(0);
        ConnectionManagerAwareHttpClient rawHttpClient = ClientExecutionAndRequestTimerTestUtils.createRawHttpClientSpy(config);
        httpClient = new AmazonHttpClient(config, rawHttpClient, null);
        ClientExecutionAndRequestTimerTestUtils.execute(httpClient, new EmptyHttpRequest(server.getEndpoint(), HttpMethodName.PUT, new SdkBufferedInputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                // Sleeping here to avoid OOM issues from a limitless InputStream
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return 1;
            }
        })));
    }
}

