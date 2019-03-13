/**
 * Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
package com.amazonaws.waiters;


import PollingStrategy.DelayStrategy;
import PollingStrategy.RetryStrategy;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static WaiterState.FAILURE;
import static WaiterState.RETRY;
import static WaiterState.SUCCESS;


@RunWith(MockitoJUnitRunner.class)
public class WaiterTest {
    @Mock
    private RetryStrategy mockRetryStrategy = Mockito.mock(RetryStrategy.class);

    private DelayStrategy mockDelayStrategy = Mockito.mock(DelayStrategy.class);

    WaiterExecutionBuilder waiterExecutionBuilder;

    WaiterTest.MockDescribeRequest request = new WaiterTest.MockDescribeRequest();

    @Test
    public void successStatePolling() throws Exception {
        List<WaiterAcceptor> acceptors = new ArrayList<WaiterAcceptor>();
        acceptors.add(new WaiterTest.SuccessStateResultAcceptor());
        acceptors.add(new WaiterTest.ExceptionAcceptor());
        waiterExecutionBuilder.withAcceptors(acceptors);
        WaiterExecution waiter = new WaiterExecution(waiterExecutionBuilder);
        Assert.assertTrue("Resource status transitioned to failure", waiter.pollResource());
        Mockito.verify(mockDelayStrategy, Mockito.times(2)).delayBeforeNextRetry(ArgumentMatchers.any(PollingStrategyContext.class));
    }

    @Test(expected = WaiterUnrecoverableException.class)
    public void failureStatePolling() throws Exception {
        List<WaiterAcceptor> acceptors = new ArrayList<WaiterAcceptor>();
        acceptors.add(new WaiterTest.FailureStateResultAcceptor());
        acceptors.add(new WaiterTest.ExceptionAcceptor());
        waiterExecutionBuilder.withAcceptors(acceptors);
        WaiterExecution waiter = new WaiterExecution(waiterExecutionBuilder);
        waiter.pollResource();
    }

    @Test(expected = WaiterTimedOutException.class)
    public void retryStateFailDefaultPolling() throws Exception {
        List<WaiterAcceptor> acceptors = new ArrayList<WaiterAcceptor>();
        acceptors.add(new WaiterTest.ExceptionAcceptor());
        waiterExecutionBuilder.withAcceptors(acceptors);
        WaiterExecution waiter = new WaiterExecution(waiterExecutionBuilder);
        waiter.pollResource();
    }

    @Test(expected = WaiterTimedOutException.class)
    public void retryStateFailCustomPolling() throws Exception {
        List<WaiterAcceptor> acceptors = new ArrayList<WaiterAcceptor>();
        acceptors.add(new WaiterTest.ExceptionAcceptor());
        PollingStrategy pollingStrategy = new PollingStrategy(new PollingStrategy.RetryStrategy() {
            int retryCount = 0;

            @Override
            public boolean shouldRetry(PollingStrategyContext retryStrategyParameters) {
                if ((retryStrategyParameters.getRetriesAttempted()) < 4) {
                    (retryCount)++;
                    return true;
                }
                Assert.assertEquals("It didn't retry the expected number of times", 4, retryCount);
                return false;
            }
        }, new PollingStrategy.DelayStrategy() {
            int retries = 0;

            @Override
            public void delayBeforeNextRetry(PollingStrategyContext pollingStrategyContext) throws InterruptedException {
                Assert.assertEquals("Request object is different from the expected request", request, pollingStrategyContext.getOriginalRequest());
                Assert.assertEquals("Number of retries is different from the expected retries", retries, pollingStrategyContext.getRetriesAttempted());
                (retries)++;
                if ((pollingStrategyContext.getRetriesAttempted()) < 4) {
                    Thread.sleep(2000);
                    return;
                }
                Assert.assertEquals("It didn't back off the expected number of times", 4, retries);
            }
        });
        waiterExecutionBuilder.withAcceptors(acceptors).withPollingStrategy(pollingStrategy);
        WaiterExecution waiter = new WaiterExecution(waiterExecutionBuilder);
        waiter.pollResource();
    }

    class MockDescribeRequest extends AmazonWebServiceRequest {
        private String tableName;
    }

    class MockDescribeResult {
        private String tableName;
    }

    class MockDescribeFunction implements SdkFunction<WaiterTest.MockDescribeRequest, WaiterTest.MockDescribeResult> {
        private int numberOfCalls = 0;

        @Override
        public WaiterTest.MockDescribeResult apply(WaiterTest.MockDescribeRequest describeTableRequest) {
            (numberOfCalls)++;
            return mockDescribeTable(describeTableRequest);
        }
    }

    class FailureStateResultAcceptor extends WaiterAcceptor<WaiterTest.MockDescribeResult> {
        public boolean matches(WaiterTest.MockDescribeResult result) {
            return true;
        }

        public WaiterState getState() {
            return FAILURE;
        }
    }

    class SuccessStateResultAcceptor extends WaiterAcceptor<WaiterTest.MockDescribeResult> {
        int retryCount = 0;

        public boolean matches(WaiterTest.MockDescribeResult result) {
            return true;
        }

        public WaiterState getState() {
            if ((retryCount) <= 1) {
                (retryCount)++;
                return RETRY;
            }
            return SUCCESS;
        }
    }

    class ExceptionAcceptor extends WaiterAcceptor<WaiterTest.MockDescribeResult> {
        public boolean matches(Exception e) {
            return e instanceof WaiterTest.MockResourceNotFoundException;
        }

        public WaiterState getState() {
            return WaiterState.RETRY;
        }
    }

    class MockResourceNotFoundException extends AmazonServiceException {
        public MockResourceNotFoundException(String message) {
            super(message);
        }
    }
}

