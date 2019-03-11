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


import InternalResourceManager.ResourceObserver;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.PartitionedRegion;
import org.apache.geode.internal.cache.partitioned.PartitionedRegionRebalanceOp;
import org.apache.geode.internal.cache.partitioned.rebalance.BucketOperator.Completion;
import org.junit.Test;
import org.mockito.Mockito;


public class BucketOperatorImplTest {
    private ResourceObserver resourceObserver;

    private BucketOperatorImpl operator;

    private PartitionedRegion region;

    private PartitionedRegionRebalanceOp rebalanceOp;

    private Completion completion;

    private Map<String, Long> colocatedRegionBytes = new HashMap<String, Long>();

    private int bucketId = 1;

    private InternalDistributedMember sourceMember;

    private InternalDistributedMember targetMember;

    @Test
    public void moveBucketShouldDelegateToParRegRebalanceOpMoveBucketForRegion() throws UnknownHostException {
        Mockito.doReturn(true).when(rebalanceOp).moveBucketForRegion(sourceMember, targetMember, bucketId);
        operator.moveBucket(sourceMember, targetMember, bucketId, colocatedRegionBytes);
        Mockito.verify(resourceObserver, Mockito.times(1)).movingBucket(region, bucketId, sourceMember, targetMember);
        Mockito.verify(rebalanceOp, Mockito.times(1)).moveBucketForRegion(sourceMember, targetMember, bucketId);
    }

    @Test
    public void movePrimaryShouldDelegateToParRegRebalanceOpMovePrimaryBucketForRegion() throws UnknownHostException {
        Mockito.doReturn(true).when(rebalanceOp).movePrimaryBucketForRegion(targetMember, bucketId);
        operator.movePrimary(sourceMember, targetMember, bucketId);
        Mockito.verify(resourceObserver, Mockito.times(1)).movingPrimary(region, bucketId, sourceMember, targetMember);
        Mockito.verify(rebalanceOp, Mockito.times(1)).movePrimaryBucketForRegion(targetMember, bucketId);
    }

    @Test
    public void createBucketShouldDelegateToParRegRebalanceOpCreateRedundantBucketForRegion() throws UnknownHostException {
        Mockito.doReturn(true).when(rebalanceOp).createRedundantBucketForRegion(targetMember, bucketId);
        operator.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completion);
        Mockito.verify(rebalanceOp, Mockito.times(1)).createRedundantBucketForRegion(targetMember, bucketId);
    }

    @Test
    public void createBucketShouldInvokeOnSuccessIfCreateBucketSucceeds() {
        Mockito.doReturn(true).when(rebalanceOp).createRedundantBucketForRegion(targetMember, bucketId);
        operator.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completion);
        Mockito.verify(rebalanceOp, Mockito.times(1)).createRedundantBucketForRegion(targetMember, bucketId);
        Mockito.verify(completion, Mockito.times(1)).onSuccess();
    }

    @Test
    public void createBucketShouldInvokeOnFailureIfCreateBucketFails() {
        Mockito.doReturn(false).when(rebalanceOp).createRedundantBucketForRegion(targetMember, bucketId);// return

        // false
        // for
        // create
        // fail
        operator.createRedundantBucket(targetMember, bucketId, colocatedRegionBytes, completion);
        Mockito.verify(rebalanceOp, Mockito.times(1)).createRedundantBucketForRegion(targetMember, bucketId);
        Mockito.verify(completion, Mockito.times(1)).onFailure();
    }

    @Test
    public void removeBucketShouldDelegateToParRegRebalanceOpRemoveRedundantBucketForRegion() {
        Mockito.doReturn(true).when(rebalanceOp).removeRedundantBucketForRegion(targetMember, bucketId);
        operator.removeBucket(targetMember, bucketId, colocatedRegionBytes);
        Mockito.verify(rebalanceOp, Mockito.times(1)).removeRedundantBucketForRegion(targetMember, bucketId);
    }
}

