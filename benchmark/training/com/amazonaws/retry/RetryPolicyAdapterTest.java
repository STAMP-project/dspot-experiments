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
package com.amazonaws.retry;


import RetryPolicy.BackoffStrategy;
import RetryPolicy.RetryCondition;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.retry.v2.RetryPolicyContext;
import com.amazonaws.retry.v2.RetryPolicyContexts;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RetryPolicyAdapterTest {
    @Mock
    private RetryCondition retryCondition;

    @Mock
    private BackoffStrategy backoffStrategy;

    private RetryPolicy legacyPolicy;

    private ClientConfiguration clientConfiguration = new ClientConfiguration().withMaxErrorRetry(10);

    private RetryPolicyAdapter adapter;

    @Test
    public void getLegacyRetryPolicy_ReturnsSamePolicy() {
        Assert.assertEquals(legacyPolicy, adapter.getLegacyRetryPolicy());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullRetryPolicy_ThrowsException() {
        new RetryPolicyAdapter(null, clientConfiguration);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullClientConfiguration_ThrowsException() {
        new RetryPolicyAdapter(legacyPolicy, null);
    }

    @Test
    public void computeDelayBeforeNextRetry_DelegatesToLegacyPolicy() {
        final RetryPolicyContext context = RetryPolicyContexts.LEGACY;
        adapter.computeDelayBeforeNextRetry(context);
        Mockito.verify(backoffStrategy).delayBeforeNextRetry(ArgumentMatchers.eq(((AmazonWebServiceRequest) (context.originalRequest()))), ArgumentMatchers.eq(((AmazonClientException) (context.exception()))), ArgumentMatchers.eq(context.retriesAttempted()));
    }

    @Test
    public void shouldRetry_MaxErrorRetryReached() {
        Assert.assertFalse(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(3)));
    }

    @Test
    public void shouldRetry_MaxErrorInClientConfigHonored_DoesNotUseMaxErrorInPolicy() {
        Mockito.when(retryCondition.shouldRetry(ArgumentMatchers.any(AmazonWebServiceRequest.class), ArgumentMatchers.any(AmazonClientException.class), ArgumentMatchers.anyInt())).thenReturn(true);
        legacyPolicy = new RetryPolicy(retryCondition, backoffStrategy, 3, true);
        adapter = new RetryPolicyAdapter(legacyPolicy, clientConfiguration);
        Assert.assertTrue(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(3)));
        Assert.assertFalse(adapter.shouldRetry(RetryPolicyContexts.withRetriesAttempted(10)));
    }

    @Test
    public void shouldRetry_MaxErrorNotExceeded_DelegatesToLegacyRetryCondition() {
        final RetryPolicyContext context = RetryPolicyContexts.LEGACY;
        adapter.shouldRetry(context);
        Mockito.verify(retryCondition).shouldRetry(ArgumentMatchers.eq(((AmazonWebServiceRequest) (context.originalRequest()))), ArgumentMatchers.eq(((AmazonClientException) (context.exception()))), ArgumentMatchers.eq(context.retriesAttempted()));
    }
}

