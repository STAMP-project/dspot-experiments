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


import Operation.DESTROY;
import org.apache.geode.cache.EntryNotFoundException;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.internal.cache.wan.AbstractGatewaySender;
import org.junit.Test;
import org.mockito.Mockito;


public class BucketRegionQueueJUnitTest {
    private static final String GATEWAY_SENDER_ID = "ny";

    private static final int BUCKET_ID = 85;

    private static final long KEY = 198;

    private GemFireCacheImpl cache;

    private PartitionedRegion queueRegion;

    private AbstractGatewaySender sender;

    private PartitionedRegion rootRegion;

    private BucketRegionQueue bucketRegionQueue;

    @Test
    public void testBasicDestroyConflationEnabledAndValueInRegionAndIndex() {
        // Create the event
        EntryEventImpl event = EntryEventImpl.create(this.bucketRegionQueue, DESTROY, BucketRegionQueueJUnitTest.KEY, "value", null, false, Mockito.mock(DistributedMember.class));
        // Don't allow hasSeenEvent to be invoked
        Mockito.doReturn(false).when(this.bucketRegionQueue).hasSeenEvent(event);
        // Set conflation enabled and the appropriate return values for containsKey and removeIndex
        Mockito.when(this.queueRegion.isConflationEnabled()).thenReturn(true);
        Mockito.when(this.bucketRegionQueue.containsKey(BucketRegionQueueJUnitTest.KEY)).thenReturn(true);
        Mockito.doReturn(true).when(this.bucketRegionQueue).removeIndex(BucketRegionQueueJUnitTest.KEY);
        // Invoke basicDestroy
        this.bucketRegionQueue.basicDestroy(event, true, null, false);
        // Verify mapDestroy is invoked
        Mockito.verify(this.bucketRegionQueue).mapDestroy(event, true, false, null);
    }

    @Test(expected = EntryNotFoundException.class)
    public void testBasicDestroyConflationEnabledAndValueNotInRegion() {
        // Create the event
        EntryEventImpl event = EntryEventImpl.create(this.bucketRegionQueue, DESTROY, BucketRegionQueueJUnitTest.KEY, "value", null, false, Mockito.mock(DistributedMember.class));
        // Don't allow hasSeenEvent to be invoked
        Mockito.doReturn(false).when(this.bucketRegionQueue).hasSeenEvent(event);
        // Set conflation enabled and the appropriate return values for containsKey and removeIndex
        Mockito.when(this.queueRegion.isConflationEnabled()).thenReturn(true);
        Mockito.when(this.bucketRegionQueue.containsKey(BucketRegionQueueJUnitTest.KEY)).thenReturn(false);
        // Invoke basicDestroy
        this.bucketRegionQueue.basicDestroy(event, true, null, false);
    }
}

