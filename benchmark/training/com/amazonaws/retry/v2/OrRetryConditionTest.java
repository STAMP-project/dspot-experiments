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
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class OrRetryConditionTest {
    @Mock
    private RetryCondition conditionOne;

    @Mock
    private RetryCondition conditionTwo;

    private RetryCondition orCondition;

    @Test
    public void allFalseConditions_ReturnsFalse() {
        Assert.assertFalse(orCondition.shouldRetry(RetryPolicyContexts.EMPTY));
    }

    @Test
    public void firstConditionIsTrue_ReturnsTrue() {
        Mockito.when(conditionOne.shouldRetry(ArgumentMatchers.any(RetryPolicyContext.class))).thenReturn(true);
        Assert.assertTrue(orCondition.shouldRetry(RetryPolicyContexts.EMPTY));
    }

    @Test
    public void secondConditionIsTrue_ReturnsTrue() {
        Mockito.when(conditionTwo.shouldRetry(ArgumentMatchers.any(RetryPolicyContext.class))).thenReturn(true);
        Assert.assertTrue(orCondition.shouldRetry(RetryPolicyContexts.EMPTY));
    }

    @Test
    public void noConditions_ReturnsFalse() {
        Assert.assertFalse(new OrRetryCondition().shouldRetry(RetryPolicyContexts.EMPTY));
    }

    @Test
    public void singleConditionThatReturnsTrue_ReturnsTrue() {
        Mockito.when(conditionOne.shouldRetry(RetryPolicyContexts.EMPTY)).thenReturn(true);
        Assert.assertTrue(new OrRetryCondition(conditionOne).shouldRetry(RetryPolicyContexts.EMPTY));
    }

    @Test
    public void singleConditionThatReturnsFalse_ReturnsFalse() {
        Mockito.when(conditionOne.shouldRetry(RetryPolicyContexts.EMPTY)).thenReturn(false);
        Assert.assertFalse(new OrRetryCondition(conditionOne).shouldRetry(RetryPolicyContexts.EMPTY));
    }
}

