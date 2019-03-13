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


import Operation.GET;
import Operation.GET_FOR_REGISTER_INTEREST;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class PartitionedRegionTest {
    String regionName = "prTestRegion";

    PartitionedRegion partitionedRegion;

    @Test
    public void getBucketNodeForReadOrWriteReturnsPrimaryNodeForRegisterInterest() throws Exception {
        int bucketId = 0;
        InternalDistributedMember primaryMember = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember secondaryMember = Mockito.mock(InternalDistributedMember.class);
        EntryEventImpl clientEvent = Mockito.mock(EntryEventImpl.class);
        Mockito.when(clientEvent.getOperation()).thenReturn(GET_FOR_REGISTER_INTEREST);
        PartitionedRegion spyPR = Mockito.spy(partitionedRegion);
        Mockito.doReturn(primaryMember).when(spyPR).getNodeForBucketWrite(ArgumentMatchers.eq(bucketId), ArgumentMatchers.isNull());
        Mockito.doReturn(secondaryMember).when(spyPR).getNodeForBucketRead(ArgumentMatchers.eq(bucketId));
        InternalDistributedMember memberForRegisterInterestRead = spyPR.getBucketNodeForReadOrWrite(bucketId, clientEvent);
        assertThat(memberForRegisterInterestRead).isSameAs(primaryMember);
        Mockito.verify(spyPR, Mockito.times(1)).getNodeForBucketWrite(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
    }

    @Test
    public void getBucketNodeForReadOrWriteReturnsSecondaryNodeForNonRegisterInterest() throws Exception {
        int bucketId = 0;
        InternalDistributedMember primaryMember = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember secondaryMember = Mockito.mock(InternalDistributedMember.class);
        EntryEventImpl clientEvent = Mockito.mock(EntryEventImpl.class);
        Mockito.when(clientEvent.getOperation()).thenReturn(GET);
        PartitionedRegion spyPR = Mockito.spy(partitionedRegion);
        Mockito.doReturn(primaryMember).when(spyPR).getNodeForBucketWrite(ArgumentMatchers.eq(bucketId), ArgumentMatchers.isNull());
        Mockito.doReturn(secondaryMember).when(spyPR).getNodeForBucketRead(ArgumentMatchers.eq(bucketId));
        InternalDistributedMember memberForRegisterInterestRead = spyPR.getBucketNodeForReadOrWrite(bucketId, clientEvent);
        assertThat(memberForRegisterInterestRead).isSameAs(secondaryMember);
        Mockito.verify(spyPR, Mockito.times(1)).getNodeForBucketRead(ArgumentMatchers.anyInt());
    }

    @Test
    public void getBucketNodeForReadOrWriteReturnsSecondaryNodeWhenClientEventIsNotPresent() throws Exception {
        int bucketId = 0;
        InternalDistributedMember primaryMember = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember secondaryMember = Mockito.mock(InternalDistributedMember.class);
        PartitionedRegion spyPR = Mockito.spy(partitionedRegion);
        Mockito.doReturn(primaryMember).when(spyPR).getNodeForBucketWrite(ArgumentMatchers.eq(bucketId), ArgumentMatchers.isNull());
        Mockito.doReturn(secondaryMember).when(spyPR).getNodeForBucketRead(ArgumentMatchers.eq(bucketId));
        InternalDistributedMember memberForRegisterInterestRead = spyPR.getBucketNodeForReadOrWrite(bucketId, null);
        assertThat(memberForRegisterInterestRead).isSameAs(secondaryMember);
        Mockito.verify(spyPR, Mockito.times(1)).getNodeForBucketRead(ArgumentMatchers.anyInt());
    }

    @Test
    public void getBucketNodeForReadOrWriteReturnsSecondaryNodeWhenClientEventOperationIsNotPresent() throws Exception {
        int bucketId = 0;
        InternalDistributedMember primaryMember = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember secondaryMember = Mockito.mock(InternalDistributedMember.class);
        EntryEventImpl clientEvent = Mockito.mock(EntryEventImpl.class);
        Mockito.when(clientEvent.getOperation()).thenReturn(null);
        PartitionedRegion spyPR = Mockito.spy(partitionedRegion);
        Mockito.doReturn(primaryMember).when(spyPR).getNodeForBucketWrite(ArgumentMatchers.eq(bucketId), ArgumentMatchers.isNull());
        Mockito.doReturn(secondaryMember).when(spyPR).getNodeForBucketRead(ArgumentMatchers.eq(bucketId));
        InternalDistributedMember memberForRegisterInterestRead = spyPR.getBucketNodeForReadOrWrite(bucketId, null);
        assertThat(memberForRegisterInterestRead).isSameAs(secondaryMember);
        Mockito.verify(spyPR, Mockito.times(1)).getNodeForBucketRead(ArgumentMatchers.anyInt());
    }

    @Test
    public void updateBucketMapsForInterestRegistrationWithSetOfKeysFetchesPrimaryBucketsForRead() {
        Integer[] bucketIds = new Integer[]{ 0, 1 };
        InternalDistributedMember primaryMember = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember secondaryMember = Mockito.mock(InternalDistributedMember.class);
        PartitionedRegion spyPR = Mockito.spy(partitionedRegion);
        Mockito.doReturn(primaryMember).when(spyPR).getNodeForBucketWrite(ArgumentMatchers.anyInt(), ArgumentMatchers.isNull());
        Mockito.doReturn(secondaryMember).when(spyPR).getNodeForBucketRead(ArgumentMatchers.anyInt());
        HashMap<InternalDistributedMember, HashSet<Integer>> nodeToBuckets = new HashMap<InternalDistributedMember, HashSet<Integer>>();
        Set buckets = Arrays.stream(bucketIds).collect(Collectors.toCollection(HashSet::new));
        spyPR.updateNodeToBucketMap(nodeToBuckets, buckets);
        Mockito.verify(spyPR, Mockito.times(2)).getNodeForBucketWrite(ArgumentMatchers.anyInt(), ArgumentMatchers.isNull());
    }

    @Test
    public void updateBucketMapsForInterestRegistrationWithAllKeysFetchesPrimaryBucketsForRead() {
        Integer[] bucketIds = new Integer[]{ 0, 1 };
        InternalDistributedMember primaryMember = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember secondaryMember = Mockito.mock(InternalDistributedMember.class);
        PartitionedRegion spyPR = Mockito.spy(partitionedRegion);
        Mockito.doReturn(primaryMember).when(spyPR).getNodeForBucketWrite(ArgumentMatchers.anyInt(), ArgumentMatchers.isNull());
        Mockito.doReturn(secondaryMember).when(spyPR).getNodeForBucketRead(ArgumentMatchers.anyInt());
        HashMap<InternalDistributedMember, HashMap<Integer, HashSet>> nodeToBuckets = new HashMap<InternalDistributedMember, HashMap<Integer, HashSet>>();
        HashSet buckets = Arrays.stream(bucketIds).collect(Collectors.toCollection(HashSet::new));
        HashMap<Integer, HashSet> bucketKeys = new HashMap<>();
        bucketKeys.put(Integer.valueOf(0), buckets);
        spyPR.updateNodeToBucketMap(nodeToBuckets, bucketKeys);
        Mockito.verify(spyPR, Mockito.times(1)).getNodeForBucketWrite(ArgumentMatchers.anyInt(), ArgumentMatchers.isNull());
    }
}

