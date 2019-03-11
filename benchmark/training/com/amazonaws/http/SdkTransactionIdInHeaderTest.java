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


import org.junit.Test;
import utils.http.WireMockTestBase;


public class SdkTransactionIdInHeaderTest extends WireMockTestBase {
    private static final String RESOURCE_PATH = "/transaction-id/";

    @Test
    public void retriedRequest_HasSameTransactionIdForAllRetries() throws Exception {
        stubFor(get(urlEqualTo(SdkTransactionIdInHeaderTest.RESOURCE_PATH)).willReturn(aResponse().withStatus(500)));
        executeRequest();
        assertTransactionIdIsUnchangedAcrossRetries();
    }
}

