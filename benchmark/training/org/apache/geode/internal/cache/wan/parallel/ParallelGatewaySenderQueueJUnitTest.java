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


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.apache.geode.internal.cache.AbstractBucketRegionQueue;
import org.apache.geode.internal.cache.BucketRegionQueue;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.PartitionedRegionDataStore;
import org.apache.geode.internal.cache.wan.AbstractGatewaySender;
import org.apache.geode.internal.cache.wan.GatewaySenderEventImpl;
import org.apache.geode.internal.cache.wan.GatewaySenderStats;
import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue.MetaRegionFactory;
import org.apache.geode.internal.cache.wan.parallel.ParallelGatewaySenderQueue.ParallelGatewaySenderQueueMetaRegion;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ParallelGatewaySenderQueueJUnitTest {
    private ParallelGatewaySenderQueue queue;

    private MetaRegionFactory metaRegionFactory;

    private GemFireCacheImpl cache;

    private AbstractGatewaySender sender;

    @Test
    public void whenEventReleaseFromOffHeapFailsExceptionShouldNotBeThrownToAckReaderThread() throws Exception {
        GatewaySenderEventImpl event = Mockito.mock(GatewaySenderEventImpl.class);
        Mockito.when(event.makeHeapCopyIfOffHeap()).thenReturn(event);
        Mockito.doThrow(new IllegalStateException()).when(event).release();
        Queue backingList = new LinkedList();
        backingList.add(event);
        BucketRegionQueue bucketRegionQueue = mockBucketRegionQueue(backingList);
        ParallelGatewaySenderQueueJUnitTest.TestableParallelGatewaySenderQueue queue = new ParallelGatewaySenderQueueJUnitTest.TestableParallelGatewaySenderQueue(sender, Collections.emptySet(), 0, 1, metaRegionFactory);
        queue.setMockedAbstractBucketRegionQueue(bucketRegionQueue);
        List peeked = peek(1, 1000);
        Assert.assertEquals(1, peeked.size());
        remove();
    }

    @Test
    public void whenGatewayEventUnableToResolveFromOffHeapTheStatForNotQueuedConflatedShouldBeIncremented() throws Exception {
        GatewaySenderStats stats = mockGatewaySenderStats();
        GatewaySenderEventImpl event = Mockito.mock(GatewaySenderEventImpl.class);
        Mockito.when(event.makeHeapCopyIfOffHeap()).thenReturn(null);
        GatewaySenderEventImpl eventResolvesFromOffHeap = Mockito.mock(GatewaySenderEventImpl.class);
        Mockito.when(eventResolvesFromOffHeap.makeHeapCopyIfOffHeap()).thenReturn(eventResolvesFromOffHeap);
        Queue backingList = new LinkedList();
        backingList.add(event);
        backingList.add(eventResolvesFromOffHeap);
        BucketRegionQueue bucketRegionQueue = mockBucketRegionQueue(backingList);
        ParallelGatewaySenderQueueJUnitTest.TestableParallelGatewaySenderQueue queue = new ParallelGatewaySenderQueueJUnitTest.TestableParallelGatewaySenderQueue(sender, Collections.emptySet(), 0, 1, metaRegionFactory);
        queue.setMockedAbstractBucketRegionQueue(bucketRegionQueue);
        List peeked = peek(1, 1000);
        Assert.assertEquals(1, peeked.size());
        Mockito.verify(stats, Mockito.times(1)).incEventsNotQueuedConflated();
    }

    @Test
    public void whenNullPeekedEventFromBucketRegionQueueTheStatForNotQueuedConflatedShouldBeIncremented() throws Exception {
        GatewaySenderStats stats = mockGatewaySenderStats();
        GatewaySenderEventImpl eventResolvesFromOffHeap = Mockito.mock(GatewaySenderEventImpl.class);
        Mockito.when(eventResolvesFromOffHeap.makeHeapCopyIfOffHeap()).thenReturn(eventResolvesFromOffHeap);
        Queue backingList = new LinkedList();
        backingList.add(null);
        backingList.add(eventResolvesFromOffHeap);
        BucketRegionQueue bucketRegionQueue = mockBucketRegionQueue(backingList);
        ParallelGatewaySenderQueueJUnitTest.TestableParallelGatewaySenderQueue queue = new ParallelGatewaySenderQueueJUnitTest.TestableParallelGatewaySenderQueue(sender, Collections.emptySet(), 0, 1, metaRegionFactory);
        queue.setMockedAbstractBucketRegionQueue(bucketRegionQueue);
        List peeked = peek(1, 1000);
        Assert.assertEquals(1, peeked.size());
        Mockito.verify(stats, Mockito.times(1)).incEventsNotQueuedConflated();
    }

    @Test
    public void testLocalSize() throws Exception {
        ParallelGatewaySenderQueueMetaRegion mockMetaRegion = Mockito.mock(ParallelGatewaySenderQueueMetaRegion.class);
        PartitionedRegionDataStore dataStore = Mockito.mock(PartitionedRegionDataStore.class);
        Mockito.when(mockMetaRegion.getDataStore()).thenReturn(dataStore);
        Mockito.when(dataStore.getSizeOfLocalPrimaryBuckets()).thenReturn(3);
        Mockito.when(metaRegionFactory.newMetataRegion(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockMetaRegion);
        Mockito.when(cache.createVMRegion(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(mockMetaRegion);
        queue.addShadowPartitionedRegionForUserPR(mockPR("region1"));
        Assert.assertEquals(3, queue.localSize());
    }

    // @Override
    // public int localSizeForProcessor() {
    // return 1;
    // }
    private class TestableParallelGatewaySenderQueue extends ParallelGatewaySenderQueue {
        private BucketRegionQueue mockedAbstractBucketRegionQueue;

        public TestableParallelGatewaySenderQueue(final AbstractGatewaySender sender, final Set<org.apache.geode.cache.Region> userRegions, final int idx, final int nDispatcher) {
            super(sender, userRegions, idx, nDispatcher);
        }

        public TestableParallelGatewaySenderQueue(final AbstractGatewaySender sender, final Set<org.apache.geode.cache.Region> userRegions, final int idx, final int nDispatcher, final MetaRegionFactory metaRegionFactory) {
            super(sender, userRegions, idx, nDispatcher, metaRegionFactory);
        }

        public void setMockedAbstractBucketRegionQueue(BucketRegionQueue mocked) {
            this.mockedAbstractBucketRegionQueue = mocked;
        }

        public AbstractBucketRegionQueue getBucketRegion(final PartitionedRegion prQ, final int bucketId) {
            return mockedAbstractBucketRegionQueue;
        }

        @Override
        public boolean areLocalBucketQueueRegionsPresent() {
            return true;
        }

        @Override
        protected PartitionedRegion getRandomShadowPR() {
            return mockedAbstractBucketRegionQueue.getPartitionedRegion();
        }

        @Override
        protected int getRandomPrimaryBucket(PartitionedRegion pr) {
            return 0;
        }

        @Override
        protected BucketRegionQueue getBucketRegionQueueByBucketId(PartitionedRegion prQ, int bucketId) {
            return mockedAbstractBucketRegionQueue;
        }
    }
}

