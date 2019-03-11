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
package org.apache.geode.internal.cache.partitioned.rebalance;


import java.util.Map;
import java.util.Set;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.control.PartitionRebalanceDetailsImpl;
import org.apache.geode.internal.cache.control.ResourceManagerStats;
import org.apache.geode.internal.cache.partitioned.rebalance.BucketOperator.Completion;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class BucketOperatorWrapperTest {
    private ResourceManagerStats stats;

    private PartitionedRegion leaderRegion;

    private PartitionedRegion colocatedRegion;

    private Set<PartitionRebalanceDetailsImpl> rebalanceDetails;

    private BucketOperatorWrapper wrapper;

    private BucketOperatorImpl delegate;

    private Map<String, Long> colocatedRegionBytes;

    private int bucketId = 1;

    private InternalDistributedMember sourceMember;

    private InternalDistributedMember targetMember;

    private static final String PR_LEADER_REGION_NAME = "leadregion1";

    private static final String PR_COLOCATED_REGION_NAME = "coloregion1";

    @Test
    public void bucketWrapperShouldDelegateCreateBucketToEnclosedOperator() {
        Completion completionSentToWrapper = Mockito.mock(Completion.class);
        Mockito.doNothing().when(delegate).createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completionSentToWrapper);
        wrapper.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completionSentToWrapper);
        Mockito.verify(delegate, Mockito.times(1)).createRedundantBucket(ArgumentMatchers.eq(targetMember), ArgumentMatchers.eq(bucketId), ArgumentMatchers.eq(colocatedRegionBytes), ArgumentMatchers.any(Completion.class));
    }

    @Test
    public void bucketWrapperShouldRecordNumberOfBucketsCreatedIfCreateBucketSucceeds() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                // 3rd argument is Completion object sent to BucketOperatorImpl.createRedundantBucket
                onSuccess();
                return null;
            }
        }).when(delegate).createRedundantBucket(ArgumentMatchers.eq(targetMember), ArgumentMatchers.eq(bucketId), ArgumentMatchers.eq(colocatedRegionBytes), ArgumentMatchers.any(Completion.class));
        Completion completionSentToWrapper = Mockito.mock(Completion.class);
        wrapper.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completionSentToWrapper);
        // verify create buckets is recorded
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            if (details.getRegionPath().equalsIgnoreCase(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME))
                Mockito.verify(details, Mockito.times(1)).incCreates(ArgumentMatchers.eq(colocatedRegionBytes.get(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME)), ArgumentMatchers.anyLong());
            // elapsed is recorded only if its leader
            else
                if (details.getRegionPath().equals(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME))
                    Mockito.verify(details, Mockito.times(1)).incTransfers(colocatedRegionBytes.get(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME), 0);

            // elapsed is recorded only if its leader

        }
    }

    @Test
    public void bucketWrapperShouldNotRecordNumberOfBucketsCreatedIfCreateBucketFails() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                // 3rd argument is Completion object sent to BucketOperatorImpl.createRedundantBucket
                onFailure();
                return null;
            }
        }).when(delegate).createRedundantBucket(ArgumentMatchers.eq(targetMember), ArgumentMatchers.eq(bucketId), ArgumentMatchers.eq(colocatedRegionBytes), ArgumentMatchers.any(Completion.class));
        Completion completionSentToWrapper = Mockito.mock(Completion.class);
        wrapper.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completionSentToWrapper);
        // verify create buckets is not recorded
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            Mockito.verify(details, Mockito.times(0)).incTransfers(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        }
    }

    @Test
    public void bucketWrapperShouldInvokeOnFailureWhenCreateBucketFails() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                // 3rd argument is Completion object sent to BucketOperatorImpl.createRedundantBucket
                onFailure();
                return null;
            }
        }).when(delegate).createRedundantBucket(ArgumentMatchers.eq(targetMember), ArgumentMatchers.eq(bucketId), ArgumentMatchers.eq(colocatedRegionBytes), ArgumentMatchers.any(Completion.class));
        Completion completionSentToWrapper = Mockito.mock(Completion.class);
        wrapper.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completionSentToWrapper);
        // verify onFailure is invoked
        onFailure();
    }

    @Test
    public void bucketWrapperShouldInvokeOnSuccessWhenCreateBucketSucceeds() {
        Mockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                // 3rd argument is Completion object sent to BucketOperatorImpl.createRedundantBucket
                onSuccess();
                return null;
            }
        }).when(delegate).createRedundantBucket(ArgumentMatchers.eq(targetMember), ArgumentMatchers.eq(bucketId), ArgumentMatchers.eq(colocatedRegionBytes), ArgumentMatchers.any(Completion.class));
        Completion completionSentToWrapper = Mockito.mock(Completion.class);
        wrapper.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completionSentToWrapper);
        onSuccess();
    }

    @Test
    public void bucketWrapperShouldDelegateMoveBucketToEnclosedOperator() {
        Mockito.doReturn(true).when(delegate).moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        wrapper.moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        // verify the delegate is invoked
        Mockito.verify(delegate, Mockito.times(1)).moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startBucketTransfer(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endBucketTransfer(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldRecordBytesTransferredPerRegionAfterMoveBucketIsSuccessful() {
        Mockito.doReturn(true).when(delegate).moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        wrapper.moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        // verify the details is updated with bytes transfered
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            if (details.getRegionPath().equalsIgnoreCase(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME))
                Mockito.verify(details, Mockito.times(1)).incTransfers(ArgumentMatchers.eq(colocatedRegionBytes.get(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME)), ArgumentMatchers.anyLong());
            // elapsed is recorded only if its leader
            else
                if (details.getRegionPath().equals(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME))
                    Mockito.verify(details, Mockito.times(1)).incTransfers(colocatedRegionBytes.get(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME), 0);

            // elapsed is recorded only if its leader

        }
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startBucketTransfer(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endBucketTransfer(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldDoNotRecordBytesTransferedIfMoveBucketFails() {
        Mockito.doReturn(false).when(delegate).moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        wrapper.moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        // verify the details is not updated with bytes transfered
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            Mockito.verify(details, Mockito.times(0)).incTransfers(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        }
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startBucketTransfer(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endBucketTransfer(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldDelegateRemoveBucketToEnclosedOperator() {
        wrapper.removeBucket(targetMember, bucketId, colocatedRegionBytes);
        // verify the delegate is invoked
        Mockito.verify(delegate, Mockito.times(1)).removeBucket(targetMember, bucketId, colocatedRegionBytes);
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startBucketRemove(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endBucketRemove(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldRecordBucketRemovesPerRegionAfterRemoveBucketIsSuccessful() {
        Mockito.doReturn(true).when(delegate).removeBucket(targetMember, bucketId, colocatedRegionBytes);
        wrapper.removeBucket(targetMember, bucketId, colocatedRegionBytes);
        // verify the details is updated with bytes transfered
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            if (details.getRegionPath().equalsIgnoreCase(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME))
                Mockito.verify(details, Mockito.times(1)).incRemoves(ArgumentMatchers.eq(colocatedRegionBytes.get(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME)), ArgumentMatchers.anyLong());
            // elapsed
            else
                if (details.getRegionPath().equals(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME))
                    Mockito.verify(details, Mockito.times(1)).incRemoves(colocatedRegionBytes.get(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME), 0);

            // elapsed

            // is
            // recorded
            // only
            // if
            // its
            // leader
        }
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startBucketRemove(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endBucketRemove(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldDoNotRecordBucketRemovesIfMoveBucketFails() {
        Mockito.doReturn(false).when(delegate).removeBucket(targetMember, bucketId, colocatedRegionBytes);
        wrapper.removeBucket(targetMember, bucketId, colocatedRegionBytes);
        // verify the details is not updated with bytes transfered
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            Mockito.verify(details, Mockito.times(0)).incTransfers(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        }
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startBucketRemove(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endBucketRemove(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldDelegateMovePrimaryToEnclosedOperator() {
        wrapper.movePrimary(sourceMember, targetMember, bucketId);
        // verify the delegate is invoked
        Mockito.verify(delegate, Mockito.times(1)).movePrimary(sourceMember, targetMember, bucketId);
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startPrimaryTransfer(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endPrimaryTransfer(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldRecordPrimaryTransfersPerRegionAfterMovePrimaryIsSuccessful() {
        Mockito.doReturn(true).when(delegate).movePrimary(sourceMember, targetMember, bucketId);
        wrapper.movePrimary(sourceMember, targetMember, bucketId);
        // verify the details is updated with bytes transfered
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            if (details.getRegionPath().equalsIgnoreCase(BucketOperatorWrapperTest.PR_LEADER_REGION_NAME))
                Mockito.verify(details, Mockito.times(1)).incPrimaryTransfers(ArgumentMatchers.anyLong());
            // elapsed is recorded only if its leader
            else
                if (details.getRegionPath().equals(BucketOperatorWrapperTest.PR_COLOCATED_REGION_NAME))
                    Mockito.verify(details, Mockito.times(1)).incPrimaryTransfers(0);

            // elapsed is recorded only if its leader

        }
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startPrimaryTransfer(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endPrimaryTransfer(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
    }

    @Test
    public void bucketWrapperShouldNotRecordPrimaryTransfersPerRegionAfterMovePrimaryFails() {
        Mockito.doReturn(false).when(delegate).movePrimary(sourceMember, targetMember, bucketId);
        wrapper.movePrimary(sourceMember, targetMember, bucketId);
        // verify the details is not updated with bytes transfered
        for (PartitionRebalanceDetailsImpl details : rebalanceDetails) {
            Mockito.verify(details, Mockito.times(0)).incTransfers(ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong());
        }
        // verify we recorded necessary stats
        Mockito.verify(stats, Mockito.times(1)).startPrimaryTransfer(ArgumentMatchers.anyInt());
        Mockito.verify(stats, Mockito.times(1)).endPrimaryTransfer(ArgumentMatchers.anyInt(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
    }
}

