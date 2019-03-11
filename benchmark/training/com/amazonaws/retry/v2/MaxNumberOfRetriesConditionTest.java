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
package com.amazonaws.retry.v2;


import org.junit.Assert;
import org.junit.Test;


public class MaxNumberOfRetriesConditionTest {
    @Test(expected = IllegalArgumentException.class)
    public void negativeMaxRetries_ThrowsException() {
        new MaxNumberOfRetriesCondition((-1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void zeroMaxRetries_ThrowsException() {
        new MaxNumberOfRetriesCondition(0);
    }

    @Test
    public void positiveMaxRetries_OneMoreAttemptToMax_ReturnsTrue() {
        Assert.assertTrue(new MaxNumberOfRetriesCondition(3).shouldRetry(RetryPolicyContexts.withRetriesAttempted(2)));
    }

    @Test
    public void positiveMaxRetries_AtMaxAttempts_ReturnsFalse() {
        Assert.assertFalse(new MaxNumberOfRetriesCondition(3).shouldRetry(RetryPolicyContexts.withRetriesAttempted(3)));
    }

    @Test
    public void positiveMaxRetries_PastMaxAttempts_ReturnsFalse() {
        Assert.assertFalse(new MaxNumberOfRetriesCondition(3).shouldRetry(RetryPolicyContexts.withRetriesAttempted(4)));
    }
}

