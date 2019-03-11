/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights
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


import PredefinedRetryPolicies.DEFAULT_MAX_ERROR_RETRY;
import PredefinedRetryPolicies.DYNAMODB_DEFAULT;
import PredefinedRetryPolicies.DYNAMODB_DEFAULT_MAX_ERROR_RETRY;
import PredefinedRetryPolicies.NO_RETRY_POLICY;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.http.AmazonHttpClient;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;

import static PredefinedRetryPolicies.DEFAULT;


/**
 * Tests the behavior when both
 * {@link ClientConfiguration#setMaxErrorRetry(int)} and
 * {@link ClientConfiguration#setRetryPolicy(RetryPolicy)} are used.
 */
public class ClientConfigurationMaxErrorRetryTest extends RetryPolicyTestBase {
    private static final Random random = new Random();

    private AmazonHttpClient testedClient;

    /**
     * -- No explicit calls on ClientConfiguration#setMaxErrorRetry(int);
     * -- Default RetryPolicy's.
     */
    @Test
    public void testDefaultMaxErrorRetry() {
        /* SDK default */
        Assert.assertTrue(((RetryPolicyTestBase.clientConfiguration.getRetryPolicy()) == (DEFAULT)));
        // Don't change any of the default settings in ClientConfiguration
        testActualRetries(DEFAULT_MAX_ERROR_RETRY);
        /* DynamoDB default */
        // Change to dynamodb default policy.
        RetryPolicyTestBase.clientConfiguration.setRetryPolicy(DYNAMODB_DEFAULT);
        testActualRetries(DYNAMODB_DEFAULT_MAX_ERROR_RETRY);
    }

    /**
     * -- Explicitly set maxErrorRetry in ClientConfiguration level;
     * -- Default/custom RetryPolicy's that don't override such setting.
     */
    @Test
    public void testClientConfigLevelMaxErrorRetry() {
        int CLIENT_CONFIG_LEVEL_MAX_RETRY = ClientConfigurationMaxErrorRetryTest.random.nextInt(3);
        RetryPolicyTestBase.clientConfiguration.setMaxErrorRetry(CLIENT_CONFIG_LEVEL_MAX_RETRY);
        // SDK default policy should honor the ClientConfig level maxErrorRetry
        testActualRetries(CLIENT_CONFIG_LEVEL_MAX_RETRY);
        // DynamoDB default policy should also honor that
        RetryPolicyTestBase.clientConfiguration.setRetryPolicy(DYNAMODB_DEFAULT);
        testActualRetries(CLIENT_CONFIG_LEVEL_MAX_RETRY);
        // A custom policy that honors the ClientConfig level maxErrorRetry
        RetryPolicyTestBase.clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, 5, true));
        testActualRetries(CLIENT_CONFIG_LEVEL_MAX_RETRY);
    }

    @Test
    public void testNoRetry() {
        final int CLIENT_CONFIG_LEVEL_MAX_RETRY = 3;
        RetryPolicyTestBase.clientConfiguration.setRetryPolicy(NO_RETRY_POLICY);
        RetryPolicyTestBase.clientConfiguration.setMaxErrorRetry(CLIENT_CONFIG_LEVEL_MAX_RETRY);
        // Ignore the ClientConfig level maxErrorRetry
        testActualRetries(0);
    }

    /**
     * -- Explicitly set maxErrorRetry in ClientConfiguration level;
     * -- Custom RetryPolicy's that want to override such setting.
     */
    @Test
    public void testRetryPolicyLevelMaxErrorRetry() {
        // This should be ignored
        RetryPolicyTestBase.clientConfiguration.setMaxErrorRetry(ClientConfigurationMaxErrorRetryTest.random.nextInt(3));
        // A custom policy that doesn't honor the ClientConfig level maxErrorRetry
        int RETRY_POLICY_LEVEL_MAX_ERROR_RETRY = 5;
        RetryPolicyTestBase.clientConfiguration.setRetryPolicy(new RetryPolicy(null, null, RETRY_POLICY_LEVEL_MAX_ERROR_RETRY, false));
        testActualRetries(RETRY_POLICY_LEVEL_MAX_ERROR_RETRY);
        // A custom policy that "honors" the ClientConfig level maxErrorRetry,
        // but actually denies any retry in its condition.
        RetryPolicyTestBase.clientConfiguration.setRetryPolicy(new RetryPolicy(new RetryPolicy.RetryCondition() {
            @Override
            public boolean shouldRetry(AmazonWebServiceRequest originalRequest, AmazonClientException exception, int retriesAttempted) {
                return false;
            }
        }, null, RETRY_POLICY_LEVEL_MAX_ERROR_RETRY, true));
        // No retry is expected
        testActualRetries(0);
    }
}

