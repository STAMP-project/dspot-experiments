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
package org.apache.geode.internal.cache.partitioned;


import DistributedPutAllOperation.PutAllEntryData;
import org.apache.geode.cache.RegionDestroyedException;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.BucketRegion;
import org.apache.geode.internal.cache.DistributedPutAllOperation;
import org.apache.geode.internal.cache.InternalDataView;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.PartitionedRegionDataStore;
import org.apache.geode.internal.cache.PrimaryBucketException;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class PutAllPRMessageTest {
    private PartitionedRegion partitionedRegion;

    private PartitionedRegionDataStore dataStore;

    private BucketRegion bucketRegion;

    private Object[] keys;

    private PutAllEntryData entryData;

    private final int bucketId = 1;

    @Test
    public void doPostPutAllCallsCheckReadinessBeforeAndAfter() throws Exception {
        DistributedPutAllOperation distributedPutAllOperation = Mockito.mock(DistributedPutAllOperation.class);
        InternalDataView internalDataView = Mockito.mock(InternalDataView.class);
        Mockito.when(bucketRegion.getDataView()).thenReturn(internalDataView);
        PutAllPRMessage putAllPRMessage = new PutAllPRMessage();
        putAllPRMessage.doPostPutAll(partitionedRegion, distributedPutAllOperation, bucketRegion, true);
        InOrder inOrder = Mockito.inOrder(partitionedRegion, internalDataView);
        inOrder.verify(partitionedRegion).checkReadiness();
        inOrder.verify(internalDataView).postPutAll(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        inOrder.verify(partitionedRegion).checkReadiness();
    }

    @Test(expected = PrimaryBucketException.class)
    public void lockedKeysAreRemoved() throws Exception {
        PutAllPRMessage message = Mockito.spy(new PutAllPRMessage(bucketId, 1, false, false, false, null));
        message.addEntry(entryData);
        Mockito.doReturn(keys).when(message).getKeysToBeLocked();
        Mockito.when(bucketRegion.waitUntilLocked(keys)).thenReturn(true);
        Mockito.when(bucketRegion.doLockForPrimary(false)).thenThrow(new PrimaryBucketException());
        message.doLocalPutAll(partitionedRegion, Mockito.mock(InternalDistributedMember.class), 1);
        Mockito.verify(bucketRegion).removeAndNotifyKeys(ArgumentMatchers.eq(keys));
    }

    @Test
    public void removeAndNotifyKeysIsNotInvokedIfKeysNotLocked() throws Exception {
        PutAllPRMessage message = Mockito.spy(new PutAllPRMessage(bucketId, 1, false, false, false, null));
        RegionDestroyedException regionDestroyedException = new RegionDestroyedException("", "");
        message.addEntry(entryData);
        Mockito.doReturn(keys).when(message).getKeysToBeLocked();
        Mockito.when(bucketRegion.waitUntilLocked(keys)).thenThrow(regionDestroyedException);
        message.doLocalPutAll(partitionedRegion, Mockito.mock(InternalDistributedMember.class), 1);
        Mockito.verify(bucketRegion, Mockito.never()).removeAndNotifyKeys(ArgumentMatchers.eq(keys));
        Mockito.verify(dataStore).checkRegionDestroyedOnBucket(ArgumentMatchers.eq(bucketRegion), ArgumentMatchers.eq(true), ArgumentMatchers.eq(regionDestroyedException));
    }
}

