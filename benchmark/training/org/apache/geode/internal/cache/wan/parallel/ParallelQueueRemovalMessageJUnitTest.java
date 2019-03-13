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
package org.apache.geode.internal.cache.wan.parallel;


import java.util.concurrent.BlockingQueue;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.internal.cache.AbstractBucketRegionQueue;
import org.apache.geode.internal.cache.BucketRegionQueue;
import org.apache.geode.internal.cache.BucketRegionQueueHelper;
import org.apache.geode.internal.cache.ForceReattemptException;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.KeyInfo;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.wan.AbstractGatewaySender;
import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;
import org.apache.geode.internal.cache.wan.GatewaySenderStats;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class ParallelQueueRemovalMessageJUnitTest {
    private static final String GATEWAY_SENDER_ID = "ny";

    private static final int BUCKET_ID = 85;

    private static final long KEY = 198;

    private GemFireCacheImpl cache;

    private PartitionedRegion queueRegion;

    private AbstractGatewaySender sender;

    private PartitionedRegion rootRegion;

    private BucketRegionQueue bucketRegionQueue;

    private BucketRegionQueueHelper bucketRegionQueueHelper;

    private GatewaySenderStats stats;

    @Test
    public void ifIsFailedBatchRemovalMessageKeysClearedFlagSetThenAddToFailedBatchRemovalMessageKeysNotCalled() throws ForceReattemptException {
        ParallelQueueRemovalMessage pqrm = new ParallelQueueRemovalMessage();
        Object object = new Object();
        PartitionedRegion partitionedRegion = Mockito.mock(PartitionedRegion.class);
        AbstractBucketRegionQueue brq = Mockito.mock(AbstractBucketRegionQueue.class);
        Mockito.doThrow(new EntryNotFoundException("ENTRY NOT FOUND")).when(brq).destroyKey(object);
        Mockito.when(brq.isFailedBatchRemovalMessageKeysClearedFlag()).thenReturn(true);
        Mockito.doNothing().when(brq).addToFailedBatchRemovalMessageKeys(object);
        pqrm.destroyKeyFromBucketQueue(brq, object, partitionedRegion);
        Mockito.verify(brq, Mockito.times(1)).destroyKey(object);
        Mockito.verify(brq, Mockito.times(1)).isFailedBatchRemovalMessageKeysClearedFlag();
        Mockito.verify(brq, Mockito.times(0)).addToFailedBatchRemovalMessageKeys(object);
    }

    @Test
    public void validateFailedBatchRemovalMessageKeysInUninitializedBucketRegionQueue() throws Exception {
        // Validate initial BucketRegionQueue state
        Assert.assertFalse(this.bucketRegionQueue.isInitialized());
        Assert.assertEquals(0, this.bucketRegionQueue.getFailedBatchRemovalMessageKeys().size());
        stats.setSecondaryQueueSize(1);
        // Create and process a ParallelQueueRemovalMessage (causes the failedBatchRemovalMessageKeys to
        // add a key)
        createAndProcessParallelQueueRemovalMessage();
        // Validate BucketRegionQueue after processing ParallelQueueRemovalMessage
        Assert.assertEquals(1, this.bucketRegionQueue.getFailedBatchRemovalMessageKeys().size());
        // failed BatchRemovalMessage will not modify stats
        Assert.assertEquals(1, stats.getSecondaryEventQueueSize());
    }

    @Test
    public void validateDestroyKeyFromBucketQueueInUninitializedBucketRegionQueue() throws Exception {
        // Validate initial BucketRegionQueue state
        Assert.assertEquals(0, this.bucketRegionQueue.size());
        Assert.assertFalse(this.bucketRegionQueue.isInitialized());
        // Add an event to the BucketRegionQueue and verify BucketRegionQueue state
        this.bucketRegionQueueHelper.addEvent(ParallelQueueRemovalMessageJUnitTest.KEY);
        Assert.assertEquals(1, this.bucketRegionQueue.size());
        Assert.assertEquals(1, stats.getSecondaryEventQueueSize());
        // Create and process a ParallelQueueRemovalMessage (causes the value of the entry to be set to
        // DESTROYED)
        Mockito.when(this.queueRegion.getKeyInfo(ParallelQueueRemovalMessageJUnitTest.KEY, null, null)).thenReturn(new KeyInfo(ParallelQueueRemovalMessageJUnitTest.KEY, null, null));
        createAndProcessParallelQueueRemovalMessage();
        // Clean up destroyed tokens and validate BucketRegionQueue
        this.bucketRegionQueueHelper.cleanUpDestroyedTokensAndMarkGIIComplete();
        Assert.assertEquals(0, this.bucketRegionQueue.size());
        Assert.assertEquals(0, stats.getSecondaryEventQueueSize());
    }

    @Test
    public void validateDestroyFromTempQueueInUninitializedBucketRegionQueue() throws Exception {
        // Validate initial BucketRegionQueue state
        Assert.assertFalse(this.bucketRegionQueue.isInitialized());
        // Create a real ConcurrentParallelGatewaySenderQueue
        ParallelGatewaySenderEventProcessor processor = ParallelGatewaySenderHelper.createParallelGatewaySenderEventProcessor(this.sender);
        // Add a mock GatewaySenderEventImpl to the temp queue
        BlockingQueue<GatewaySenderEventImpl> tempQueue = createTempQueueAndAddEvent(processor, Mockito.mock(GatewaySenderEventImpl.class));
        Assert.assertEquals(1, tempQueue.size());
        // Create and process a ParallelQueueRemovalMessage (causes the failedBatchRemovalMessageKeys to
        // add a key)
        createAndProcessParallelQueueRemovalMessage();
        // Validate temp queue is empty after processing ParallelQueueRemovalMessage
        Assert.assertEquals(0, tempQueue.size());
    }

    @Test
    public void validateDestroyFromBucketQueueAndTempQueueInUninitializedBucketRegionQueue() {
        // Validate initial BucketRegionQueue state
        Assert.assertFalse(this.bucketRegionQueue.isInitialized());
        Assert.assertEquals(0, this.bucketRegionQueue.size());
        // Create a real ConcurrentParallelGatewaySenderQueue
        ParallelGatewaySenderEventProcessor processor = ParallelGatewaySenderHelper.createParallelGatewaySenderEventProcessor(this.sender);
        // Add an event to the BucketRegionQueue and verify BucketRegionQueue state
        GatewaySenderEventImpl event = this.bucketRegionQueueHelper.addEvent(ParallelQueueRemovalMessageJUnitTest.KEY);
        Assert.assertEquals(1, this.bucketRegionQueue.size());
        Assert.assertEquals(1, stats.getSecondaryEventQueueSize());
        // Add a mock GatewaySenderEventImpl to the temp queue
        BlockingQueue<GatewaySenderEventImpl> tempQueue = createTempQueueAndAddEvent(processor, event);
        Assert.assertEquals(1, tempQueue.size());
        // Create and process a ParallelQueueRemovalMessage (causes the value of the entry to be set to
        // DESTROYED)
        Mockito.when(this.queueRegion.getKeyInfo(ParallelQueueRemovalMessageJUnitTest.KEY, null, null)).thenReturn(new KeyInfo(ParallelQueueRemovalMessageJUnitTest.KEY, null, null));
        createAndProcessParallelQueueRemovalMessage();
        // Validate temp queue is empty after processing ParallelQueueRemovalMessage
        Assert.assertEquals(0, tempQueue.size());
        Assert.assertEquals(0, stats.getSecondaryEventQueueSize());
        // Clean up destroyed tokens
        this.bucketRegionQueueHelper.cleanUpDestroyedTokensAndMarkGIIComplete();
        // Validate BucketRegionQueue is empty after processing ParallelQueueRemovalMessage
        Assert.assertEquals(0, this.bucketRegionQueue.size());
    }
}

