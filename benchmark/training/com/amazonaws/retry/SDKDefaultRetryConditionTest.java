/**
 * Copyright 2011-2019 Amazon.com, Inc. or its affiliates. All Rights
 * Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is
 * distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either
 * express or implied. See the License for the specific language
 * governing
 * permissions and limitations under the License.
 */
package com.amazonaws.retry;


import RetryPolicy.RetryCondition;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static PredefinedRetryPolicies.DEFAULT_RETRY_CONDITION;
import static RetryUtils.CLOCK_SKEW_ERROR_CODES;
import static RetryUtils.RETRYABLE_STATUS_CODES;
import static RetryUtils.THROTTLING_ERROR_CODES;


public class SDKDefaultRetryConditionTest {
    private static final RetryCondition defaultRetryCondition = DEFAULT_RETRY_CONDITION;

    private static final Random random = new Random();

    @Test
    public void shouldRetryIOExceptionAce() {
        Assert.assertTrue(shouldRetry(getAce(new IOException())));
    }

    @Test
    public void shouldNotRetryNonIOExceptionAce() {
        Assert.assertFalse(shouldRetry(getAce(new Exception())));
    }

    @Test
    public void shouldAlwaysRetryOnRetryableStatusCodes() {
        for (int statusCode : RETRYABLE_STATUS_CODES) {
            Assert.assertTrue(shouldRetry(getAse(statusCode, "IrrelevantCode")));
        }
    }

    @Test
    public void shouldNotRetryNonRetryable5xx() {
        Assert.assertFalse(shouldRetry(getAse(550, "IrrelevantCode")));
    }

    @Test
    public void shouldAlwaysRetryClockSkewCodes() {
        for (String errorCode : CLOCK_SKEW_ERROR_CODES) {
            Assert.assertTrue(shouldRetry(getAse(SDKDefaultRetryConditionTest.random.nextInt(), errorCode)));
        }
    }

    @Test
    public void shouldAlwaysRetryThrottlingCodes() {
        for (String errorCode : THROTTLING_ERROR_CODES) {
            Assert.assertTrue(shouldRetry(getAse(SDKDefaultRetryConditionTest.random.nextInt(), errorCode)));
        }
    }

    @Test
    public void shouldNotRetryBad4xxErrorCodeAse() {
        Assert.assertFalse(shouldRetry(getAse((400 + (SDKDefaultRetryConditionTest.random.nextInt(100))), "BogusException")));
    }
}

