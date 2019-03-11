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
package com.amazonaws.http;


import HttpMethodName.OPTIONS;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Request;
import org.junit.Test;
import utils.http.WireMockTestBase;


public class AmazonHttpClientIntegrationTest extends WireMockTestBase {
    private static final String OPERATION = "/some-operation";

    private static final String HEADER = "Some-Header";

    private static final String CONFIG_HEADER_VALUE = "client config header value";

    private static final String REQUEST_HEADER_VALUE = "request header value";

    @Test
    public void headersSpecifiedInClientConfigurationArePutOnRequest() throws Exception {
        Request<?> request = newGetRequest(AmazonHttpClientIntegrationTest.OPERATION);
        AmazonHttpClient sut = createClient(AmazonHttpClientIntegrationTest.HEADER, AmazonHttpClientIntegrationTest.CONFIG_HEADER_VALUE);
        sut.requestExecutionBuilder().request(request).execute();
        verify(getRequestedFor(urlPathEqualTo(AmazonHttpClientIntegrationTest.OPERATION)).withHeader(AmazonHttpClientIntegrationTest.HEADER, matching(AmazonHttpClientIntegrationTest.CONFIG_HEADER_VALUE)));
    }

    @Test
    public void headersOnRequestsWinOverClientConfigurationHeaders() throws Exception {
        Request<?> request = newGetRequest(AmazonHttpClientIntegrationTest.OPERATION);
        request.getOriginalRequest().putCustomRequestHeader(AmazonHttpClientIntegrationTest.HEADER, AmazonHttpClientIntegrationTest.REQUEST_HEADER_VALUE);
        AmazonHttpClient sut = createClient(AmazonHttpClientIntegrationTest.HEADER, AmazonHttpClientIntegrationTest.CONFIG_HEADER_VALUE);
        sut.requestExecutionBuilder().request(request).execute();
        verify(getRequestedFor(urlPathEqualTo(AmazonHttpClientIntegrationTest.OPERATION)).withHeader(AmazonHttpClientIntegrationTest.HEADER, matching(AmazonHttpClientIntegrationTest.REQUEST_HEADER_VALUE)));
    }

    @Test
    public void canHandleOptionsRequest() throws Exception {
        Request<?> request = newRequest(AmazonHttpClientIntegrationTest.OPERATION);
        request.setHttpMethod(OPTIONS);
        AmazonHttpClient sut = new AmazonHttpClient(new ClientConfiguration());
        sut.requestExecutionBuilder().request(request).execute();
        verify(optionsRequestedFor(urlPathEqualTo(AmazonHttpClientIntegrationTest.OPERATION)));
    }
}

