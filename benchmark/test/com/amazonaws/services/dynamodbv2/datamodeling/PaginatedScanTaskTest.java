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
package com.amazonaws.services.dynamodbv2.datamodeling;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PaginatedScanTaskTest {
    private static final String TABLE_NAME = "FooTable";

    private static final int TOTAL_SEGMENTS = 5;

    private ParallelScanTask parallelScanTask;

    private ExecutorService executorService;

    @Mock
    private AmazonDynamoDB dynamoDB;

    /**
     * A failed segment makes the scan task unusable and will always rethrow the same exception. In
     * this case it makes sense to shutdown the executor so that applications can shutdown faster. A
     * future enhancement could be to either retry failed segments, explicitly resume a failed scan,
     * or include metadata in the thrown exception about the state of the scan at the time it was
     * aborted. See <a href="https://github.com/aws/aws-sdk-java/pull/624">PR #624</a> and <a
     * href="https://github.com/aws/aws-sdk-java/issues/624">Issue #624</a> for more details.
     */
    @Test
    public void segmentFailsToScan_ExecutorServiceIsShutdown() throws InterruptedException {
        stubSuccessfulScan(0);
        stubSuccessfulScan(1);
        Mockito.when(dynamoDB.scan(PaginatedScanTaskTest.isSegmentNumber(2))).thenThrow(new ProvisionedThroughputExceededException("Slow Down!"));
        stubSuccessfulScan(3);
        stubSuccessfulScan(4);
        try {
            parallelScanTask.getNextBatchOfScanResults();
            Assert.fail("Expected ProvisionedThroughputExceededException");
        } catch (ProvisionedThroughputExceededException expected) {
        }
        executorService.awaitTermination(5, TimeUnit.SECONDS);
        Assert.assertTrue(executorService.isShutdown());
    }

    /**
     * Custom argument matcher to match a {@link ScanRequest} on the segment number.
     */
    private static class SegmentArgumentMatcher extends ArgumentMatcher<ScanRequest> {
        private final int matchingSegmentNumber;

        private SegmentArgumentMatcher(int matchingSegmentNumber) {
            this.matchingSegmentNumber = matchingSegmentNumber;
        }

        @Override
        public boolean matches(Object argument) {
            if (!(argument instanceof ScanRequest)) {
                return false;
            }
            return (matchingSegmentNumber) == (getSegment());
        }
    }
}

