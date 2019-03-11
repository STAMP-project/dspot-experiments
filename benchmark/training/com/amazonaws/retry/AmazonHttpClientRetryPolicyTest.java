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


import AWSRequestMetrics.Field.RequestCount;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.Request;
import com.amazonaws.http.AmazonHttpClient;
import com.amazonaws.http.ExecutionContext;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that {@link AmazonHttpClient#executeHelper()} method passes the correct
 * context information into the configured RetryPolicy.
 */
public class AmazonHttpClientRetryPolicyTest extends RetryPolicyTestBase {
    private static final int EXPECTED_RETRY_COUNT = 5;

    private static final int EXPECTED_SHOULD_RETRY_CALL_COUNT = (AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT) + 1;

    private static final Random random = new Random();

    private AmazonHttpClient testedClient;

    /**
     * Tests AmazonHttpClient's behavior upon simulated service exceptions when the
     * request payload is repeatable.
     */
    @Test
    public void testServiceExceptionHandling() {
        int random500StatusCode = 500 + (AmazonHttpClientRetryPolicyTest.random.nextInt(100));
        String randomErrorCode = UUID.randomUUID().toString();
        // A mock HttpClient that always returns the specified status and error code.
        RetryPolicyTestBase.injectMockHttpClient(testedClient, new RetryPolicyTestBase.ReturnServiceErrorHttpClient(random500StatusCode, randomErrorCode));
        // The ExecutionContext should collect the expected RequestCount
        ExecutionContext context = new ExecutionContext(true);
        Request<?> testedRepeatableRequest = RetryPolicyTestBase.getSampleRequestWithRepeatableContent(RetryPolicyTestBase.originalRequest);
        // It should keep retrying until it reaches the max retry limit and
        // throws the simulated ASE.
        AmazonServiceException expectedServiceException = null;
        try {
            testedClient.requestExecutionBuilder().request(testedRepeatableRequest).errorResponseHandler(RetryPolicyTestBase.errorResponseHandler).executionContext(context).execute();
            Assert.fail("AmazonServiceException is expected.");
        } catch (AmazonServiceException ase) {
            // We should see the original service exception
            Assert.assertEquals(random500StatusCode, ase.getStatusCode());
            Assert.assertEquals(randomErrorCode, ase.getErrorCode());
            expectedServiceException = ase;
        }
        // Verifies that the correct information was passed into the RetryCondition and BackoffStrategy
        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.retryCondition, RetryPolicyTestBase.originalRequest, expectedServiceException, AmazonHttpClientRetryPolicyTest.EXPECTED_SHOULD_RETRY_CALL_COUNT);// shouldRetry is being called again to record MaxRetriesExceeded

        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.backoffStrategy, RetryPolicyTestBase.originalRequest, expectedServiceException, AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT);
        // We also want to check the RequestCount metric is correctly captured.
        // request count = retries + 1
        Assert.assertEquals(((AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT) + 1), context.getAwsRequestMetrics().getTimingInfo().getCounter(RequestCount.toString()).intValue());
    }

    /**
     * Tests AmazonHttpClient's behavior upon simulated IOException during
     * executing the http request when the request payload is repeatable.
     */
    @Test
    public void testIOExceptioinHandling() {
        // A mock HttpClient that always throws the specified IOException object
        IOException simulatedIOException = new IOException("fake IOException");
        RetryPolicyTestBase.injectMockHttpClient(testedClient, new RetryPolicyTestBase.ThrowingExceptionHttpClient(simulatedIOException));
        // The ExecutionContext should collect the expected RequestCount
        ExecutionContext context = new ExecutionContext(true);
        Request<?> testedRepeatableRequest = RetryPolicyTestBase.getSampleRequestWithRepeatableContent(RetryPolicyTestBase.originalRequest);
        // It should keep retrying until it reaches the max retry limit and
        // throws the an ACE containing the simulated IOException.
        AmazonClientException expectedClientException = null;
        try {
            testedClient.requestExecutionBuilder().request(testedRepeatableRequest).errorResponseHandler(RetryPolicyTestBase.errorResponseHandler).executionContext(context).execute();
            Assert.fail("AmazonClientException is expected.");
        } catch (AmazonClientException ace) {
            Assert.assertTrue((simulatedIOException == (ace.getCause())));
            expectedClientException = ace;
        }
        // Verifies that the correct information was passed into the RetryCondition and BackoffStrategy
        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.retryCondition, RetryPolicyTestBase.originalRequest, expectedClientException, AmazonHttpClientRetryPolicyTest.EXPECTED_SHOULD_RETRY_CALL_COUNT);// shouldRetry is being called again to record MaxRetriesExceeded

        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.backoffStrategy, RetryPolicyTestBase.originalRequest, expectedClientException, AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT);
        // We also want to check the RequestCount metric is correctly captured.
        // request count = retries + 1
        Assert.assertEquals(((AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT) + 1), context.getAwsRequestMetrics().getTimingInfo().getCounter(RequestCount.toString()).intValue());
    }

    /**
     * Tests AmazonHttpClient's behavior upon simulated service exceptions when the
     * request payload is not repeatable.
     */
    @Test
    public void testServiceExceptionHandlingWithNonRepeatableRequestContent() {
        int random500StatusCode = 500 + (AmazonHttpClientRetryPolicyTest.random.nextInt(100));
        String randomErrorCode = UUID.randomUUID().toString();
        // A mock HttpClient that always returns the specified status and error code.
        RetryPolicyTestBase.injectMockHttpClient(testedClient, new RetryPolicyTestBase.ReturnServiceErrorHttpClient(random500StatusCode, randomErrorCode));
        // The ExecutionContext should collect the expected RequestCount
        ExecutionContext context = new ExecutionContext(true);
        // A non-repeatable request
        Request<?> testedNonRepeatableRequest = RetryPolicyTestBase.getSampleRequestWithNonRepeatableContent(RetryPolicyTestBase.originalRequest);
        // It should fail directly and throw the ASE, without consulting the
        // custom shouldRetry(..) method.
        try {
            testedClient.requestExecutionBuilder().request(testedNonRepeatableRequest).errorResponseHandler(RetryPolicyTestBase.errorResponseHandler).executionContext(context).execute();
            Assert.fail("AmazonServiceException is expected.");
        } catch (AmazonServiceException ase) {
            Assert.assertEquals(random500StatusCode, ase.getStatusCode());
            Assert.assertEquals(randomErrorCode, ase.getErrorCode());
        }
        // Verifies that shouldRetry and calculateSleepTime were never called
        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.retryCondition, null, null, AmazonHttpClientRetryPolicyTest.EXPECTED_SHOULD_RETRY_CALL_COUNT);// shouldRetry is being called again to record MaxRetriesExceeded

        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.backoffStrategy, null, null, AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT);
        // request count = retries + 1
        Assert.assertEquals(((AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT) + 1), context.getAwsRequestMetrics().getTimingInfo().getCounter(RequestCount.toString()).intValue());
    }

    /**
     * Tests AmazonHttpClient's behavior upon simulated IOException when the
     * request payload is not repeatable.
     */
    @Test
    public void testIOExceptionHandlingWithNonRepeatableRequestContent() {
        // A mock HttpClient that always throws the specified IOException object
        IOException simulatedIOException = new IOException("fake IOException");
        RetryPolicyTestBase.injectMockHttpClient(testedClient, new RetryPolicyTestBase.ThrowingExceptionHttpClient(simulatedIOException));
        // The ExecutionContext should collect the expected RequestCount
        ExecutionContext context = new ExecutionContext(true);
        // A non-repeatable request
        Request<?> testedRepeatableRequest = RetryPolicyTestBase.getSampleRequestWithNonRepeatableContent(RetryPolicyTestBase.originalRequest);
        // It should fail directly and throw an ACE containing the simulated
        // IOException, without consulting the
        // custom shouldRetry(..) method.
        try {
            testedClient.requestExecutionBuilder().request(testedRepeatableRequest).errorResponseHandler(RetryPolicyTestBase.errorResponseHandler).executionContext(context).execute();
            Assert.fail("AmazonClientException is expected.");
        } catch (AmazonClientException ace) {
            Assert.assertTrue((simulatedIOException == (ace.getCause())));
        }
        // Verifies that shouldRetry and calculateSleepTime are still called
        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.retryCondition, null, null, AmazonHttpClientRetryPolicyTest.EXPECTED_SHOULD_RETRY_CALL_COUNT);// shouldRetry is being called again to record MaxRetriesExceeded

        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.backoffStrategy, null, null, AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT);
        // request count = retries + 1
        Assert.assertEquals(((AmazonHttpClientRetryPolicyTest.EXPECTED_RETRY_COUNT) + 1), context.getAwsRequestMetrics().getTimingInfo().getCounter(RequestCount.toString()).intValue());
    }

    /**
     * Tests AmazonHttpClient's behavior upon simulated RuntimeException (which
     * should be handled as an unexpected failure and not retried).
     */
    @Test
    public void testUnexpectedFailureHandling() {
        // A mock HttpClient that always throws an NPE
        NullPointerException simulatedNPE = new NullPointerException("fake NullPointerException");
        RetryPolicyTestBase.injectMockHttpClient(testedClient, new RetryPolicyTestBase.ThrowingExceptionHttpClient(simulatedNPE));
        // The ExecutionContext should collect the expected RequestCount
        ExecutionContext context = new ExecutionContext(true);
        Request<?> testedRepeatableRequest = RetryPolicyTestBase.getSampleRequestWithRepeatableContent(RetryPolicyTestBase.originalRequest);
        // It should fail directly and throw the simulated NPE, without
        // consulting the custom shouldRetry(..) method.
        try {
            testedClient.requestExecutionBuilder().request(testedRepeatableRequest).errorResponseHandler(RetryPolicyTestBase.errorResponseHandler).executionContext(context).execute();
            Assert.fail("AmazonClientException is expected.");
        } catch (NullPointerException npe) {
            Assert.assertTrue((simulatedNPE == npe));
        }
        // Verifies that shouldRetry and calculateSleepTime were never called
        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.retryCondition, null, null, 0);
        RetryPolicyTestBase.verifyExpectedContextData(RetryPolicyTestBase.backoffStrategy, null, null, 0);
        // The captured RequestCount should be 1
        Assert.assertEquals(1, context.getAwsRequestMetrics().getTimingInfo().getCounter(RequestCount.toString()).intValue());
    }
}

