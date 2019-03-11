/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://aws.amazon.com/apache2.0
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and
 * limitations under the License.
 */
package com.amazonaws.http;


import PredefinedRetryPolicies.NO_RETRY_POLICY;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalTime;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import utils.http.WireMockTestBase;


public class AmazonHttpClientClockSkewErrorTest extends WireMockTestBase {
    private static final String RESOURCE_PATH = "/transaction-id/";

    private long hour = 3600 * 1000;

    private Date skewedDate = new Date(((System.currentTimeMillis()) - (hour)));

    @Test
    public void globalTimeOffset_IsAdjusted_WhenClockSkewErrorHappens_And_RequestIsNotRetried() throws Exception {
        stub(500, skewedDate);
        ClientConfiguration config = new ClientConfiguration();
        config.setRetryPolicy(NO_RETRY_POLICY);
        executeRequest(config);
        // Asserts global time offset is adjusted by atleast an hour
        Assert.assertTrue(((SDKGlobalTime.getGlobalTimeOffset()) >= 3600));
    }

    @Test
    public void globalTimeOffset_IsAdjusted_WhenClockSkewErrorHappens_And_RequestIsRetried() throws Exception {
        stub(500, skewedDate);
        ClientConfiguration config = new ClientConfiguration();
        executeRequest(config);
        // Asserts global time offset is adjusted by atleast an hour
        Assert.assertTrue(((SDKGlobalTime.getGlobalTimeOffset()) >= 3600));
    }

    @Test
    public void globalTimeOffset_IsAdjusted_When403ClockSkewErrorHappens() throws Exception {
        stub(403, skewedDate);
        executeRequest(new ClientConfiguration());
        // Asserts global time offset is adjusted by atleast an hour
        Assert.assertTrue(((SDKGlobalTime.getGlobalTimeOffset()) >= 3600));
    }
}

