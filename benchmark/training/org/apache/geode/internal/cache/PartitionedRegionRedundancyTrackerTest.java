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
import org.mockito.Mockito;


public class PartitionedRegionRedundancyTrackerTest {
    private static final int TARGET_COPIES = 2;

    private static final int TOTAL_BUCKETS = 3;

    private PartitionedRegionStats stats;

    private PartitionedRegionRedundancyTracker redundancyTracker;

    @Test
    public void incrementsAndDecrementsLowRedundancyBucketCount() {
        redundancyTracker.incrementLowRedundancyBucketCount();
        Mockito.verify(stats, Mockito.times(1)).incLowRedundancyBucketCount(1);
        redundancyTracker.decrementLowRedundancyBucketCount();
        Mockito.verify(stats, Mockito.times(1)).incLowRedundancyBucketCount((-1));
    }

    @Test
    public void willNotIncrementLowRedundancyBucketCountBeyondTotalsBuckets() {
        for (int i = 0; i < (PartitionedRegionRedundancyTrackerTest.TOTAL_BUCKETS); i++) {
            redundancyTracker.incrementLowRedundancyBucketCount();
        }
        Mockito.verify(stats, Mockito.times(PartitionedRegionRedundancyTrackerTest.TOTAL_BUCKETS)).incLowRedundancyBucketCount(1);
        redundancyTracker.incrementLowRedundancyBucketCount();
        Mockito.verifyNoMoreInteractions(stats);
    }

    @Test
    public void willNotDecrementLowRedundancyBucketCountBelowZero() {
        redundancyTracker.decrementLowRedundancyBucketCount();
        Mockito.verifyZeroInteractions(stats);
    }

    @Test
    public void incrementsAndDecrementsNoCopiesBucketCount() {
        redundancyTracker.incrementNoCopiesBucketCount();
        Mockito.verify(stats, Mockito.times(1)).incNoCopiesBucketCount(1);
        redundancyTracker.decrementNoCopiesBucketCount();
        Mockito.verify(stats, Mockito.times(1)).incNoCopiesBucketCount((-1));
    }

    @Test
    public void willNotIncrementNoCopiesBucketCountBeyondTotalsBuckets() {
        for (int i = 0; i < (PartitionedRegionRedundancyTrackerTest.TOTAL_BUCKETS); i++) {
            redundancyTracker.incrementNoCopiesBucketCount();
        }
        Mockito.verify(stats, Mockito.times(PartitionedRegionRedundancyTrackerTest.TOTAL_BUCKETS)).incNoCopiesBucketCount(1);
        redundancyTracker.incrementNoCopiesBucketCount();
        Mockito.verifyNoMoreInteractions(stats);
    }

    @Test
    public void willNotDecrementNoCopiesBucketCountBelowZero() {
        redundancyTracker.decrementNoCopiesBucketCount();
        Mockito.verify(stats, Mockito.times(0)).incNoCopiesBucketCount((-1));
    }

    @Test
    public void reportsCorrectLowestBucketCopies() {
        redundancyTracker.reportBucketCount(1);
        Assert.assertEquals(1, redundancyTracker.getLowestBucketCopies());
        redundancyTracker.reportBucketCount(0);
        Assert.assertEquals(0, redundancyTracker.getLowestBucketCopies());
        redundancyTracker.reportBucketCount(1);
        Assert.assertEquals(0, redundancyTracker.getLowestBucketCopies());
    }

    @Test
    public void lowestBucketCopiesResetsOnRedundancyRegained() {
        redundancyTracker.incrementLowRedundancyBucketCount();
        redundancyTracker.reportBucketCount(1);
        redundancyTracker.decrementLowRedundancyBucketCount();
        Assert.assertEquals(2, redundancyTracker.getLowestBucketCopies());
    }

    @Test
    public void lowestBucketCopiesSetToOneOnHavingABucketAgain() {
        redundancyTracker.incrementNoCopiesBucketCount();
        redundancyTracker.reportBucketCount(0);
        redundancyTracker.decrementNoCopiesBucketCount();
        Assert.assertEquals(1, redundancyTracker.getLowestBucketCopies());
    }

    @Test
    public void willNotSetActualRedundantCopiesStatBelowZero() {
        redundancyTracker.setActualRedundancy((-1));
        Assert.assertEquals(0, stats.getActualRedundantCopies());
    }
}

