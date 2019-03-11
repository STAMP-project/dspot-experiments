/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class BucketRedundancyTrackerTest {
    private static final int TARGET_COPIES = 2;

    private PartitionedRegionRedundancyTracker regionRedundancyTracker;

    private BucketRedundancyTracker bucketRedundancyTracker;

    @Test
    public void whenRedundancyNeverMetDoesNotWarnOnLowRedundancy() {
        bucketRedundancyTracker.updateStatistics(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        Mockito.verify(regionRedundancyTracker, Mockito.never()).reportBucketCount(ArgumentMatchers.anyInt());
    }

    @Test
    public void incrementsBucketCountOnLowRedundancyForBucket() {
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        bucketRedundancyTracker.updateStatistics(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).incrementLowRedundancyBucketCount();
        Assert.assertEquals(0, bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void decrementsBucketCountOnRegainingRedundancyForBucket() {
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        bucketRedundancyTracker.updateStatistics(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).decrementLowRedundancyBucketCount();
        Assert.assertEquals(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1), bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void decrementsBucketCountOnClosingBucketBelowRedundancy() {
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        bucketRedundancyTracker.updateStatistics(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        bucketRedundancyTracker.closeBucket();
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).decrementLowRedundancyBucketCount();
        Assert.assertEquals(0, bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void decrementsBucketCountOnClosingABucketWithNoCopies() {
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        bucketRedundancyTracker.updateStatistics(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        bucketRedundancyTracker.updateStatistics(0);
        bucketRedundancyTracker.closeBucket();
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).decrementLowRedundancyBucketCount();
        Assert.assertEquals((-1), bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void decrementsBucketCountOnIncrementBeforeNoCopies() {
        bucketRedundancyTracker = new BucketRedundancyTracker(2, regionRedundancyTracker);
        bucketRedundancyTracker.updateStatistics(3);
        bucketRedundancyTracker.updateStatistics(2);
        // Verify incrementLowRedundancyBucketCount is invoked.
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).incrementLowRedundancyBucketCount();
        bucketRedundancyTracker.updateStatistics(1);
        bucketRedundancyTracker.updateStatistics(2);
        // Verify incrementLowRedundancyBucketCount is not invoked again when the count goes 2.
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).incrementLowRedundancyBucketCount();
        Assert.assertEquals(1, bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void bucketCountNotDecrementedOnClosingBucketThatNeverHadCopies() {
        Mockito.verify(regionRedundancyTracker, Mockito.never()).decrementLowRedundancyBucketCount();
        Assert.assertEquals((-1), bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void doesNotWarnWhenNeverHadAnyCopies() {
        bucketRedundancyTracker.updateStatistics(0);
        Mockito.verify(regionRedundancyTracker, Mockito.never()).reportBucketCount(ArgumentMatchers.anyInt());
        Assert.assertEquals((-1), bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void doesNotIncrementNoCopiesWhenNeverHadAnyCopies() {
        bucketRedundancyTracker.updateStatistics(0);
        Mockito.verify(regionRedundancyTracker, Mockito.never()).incrementNoCopiesBucketCount();
        Assert.assertEquals((-1), bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void incrementsBucketCountOnHavingNoCopiesForBucket() {
        bucketRedundancyTracker.updateStatistics(1);
        bucketRedundancyTracker.updateStatistics(0);
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).incrementNoCopiesBucketCount();
        Assert.assertEquals((-1), bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void decrementsBucketCountOnHavingAtLeastOneCopyOfBucket() {
        bucketRedundancyTracker.updateStatistics(1);
        // Verify incrementLowRedundancyBucketCount is invoked.
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).incrementLowRedundancyBucketCount();
        bucketRedundancyTracker.updateStatistics(0);
        bucketRedundancyTracker.updateStatistics(1);
        // Verify incrementLowRedundancyBucketCount is not invoked again when the count goes to 1.
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).incrementLowRedundancyBucketCount();
        // Verify decrementLowRedundancyBucketCount is not invoked.
        Mockito.verify(regionRedundancyTracker, Mockito.never()).decrementLowRedundancyBucketCount();
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).decrementNoCopiesBucketCount();
        Assert.assertEquals(0, bucketRedundancyTracker.getCurrentRedundancy());
    }

    @Test
    public void updatesRedundancyOnlyIfChanged() {
        bucketRedundancyTracker.updateStatistics(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).setActualRedundancy(((BucketRedundancyTrackerTest.TARGET_COPIES) - 2));
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        Mockito.verify(regionRedundancyTracker, Mockito.times(1)).setActualRedundancy(((BucketRedundancyTrackerTest.TARGET_COPIES) - 1));
        bucketRedundancyTracker.updateStatistics(BucketRedundancyTrackerTest.TARGET_COPIES);
        Mockito.verify(regionRedundancyTracker, Mockito.times(2)).setActualRedundancy(ArgumentMatchers.anyInt());
    }
}

