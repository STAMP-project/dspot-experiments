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
package com.amazonaws.http;


import org.apache.log4j.BasicConfigurator;
import org.junit.Test;
import utils.http.WireMockTestBase;


public class RetryCountInUserAgentTest extends WireMockTestBase {
    private static final int[] BACKOFF_VALUES = new int[]{ 0, 10, 20 };

    private static final String RESOURCE_PATH = "/user-agent/";

    @Test
    public void retriedRequest_AppendsCorrectRetryCountInUserAgent() throws Exception {
        BasicConfigurator.configure();
        stubFor(get(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).willReturn(aResponse().withStatus(500)));
        executeRequest();
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("0/0/")));
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("1/0/")));
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("2/10/")));
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("3/20/")));
    }

    @Test
    public void retriedRequest_AppendsCorrectRetryCountInUserAgent_throttlingEnabled() throws Exception {
        BasicConfigurator.configure();
        stubFor(get(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).willReturn(aResponse().withStatus(500)));
        executeRequest();
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("0/0/500")));
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("1/0/495")));
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("2/10/490")));
        verify(1, getRequestedFor(urlEqualTo(RetryCountInUserAgentTest.RESOURCE_PATH)).withHeader(AmazonHttpClient.HEADER_SDK_RETRY_INFO, containing("3/20/485")));
    }
}

