/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.internal.partition.impl;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ReplicaSyncInfoTest {
    private ReplicaFragmentSyncInfo replicaSyncInfo;

    private ReplicaFragmentSyncInfo replicaSyncInfoSameAttributes;

    private ReplicaFragmentSyncInfo replicaSyncInfoOtherTarget;

    private ReplicaFragmentSyncInfo replicaSyncInfoOtherPartitionId;

    private ReplicaFragmentSyncInfo replicaSyncInfoOtherReplicaIndex;

    @Test
    public void testEquals() {
        Assert.assertEquals(replicaSyncInfo, replicaSyncInfo);
        Assert.assertEquals(replicaSyncInfo, replicaSyncInfoSameAttributes);
        Assert.assertEquals(replicaSyncInfo, replicaSyncInfoOtherTarget);
        Assert.assertNotEquals(replicaSyncInfo, null);
        Assert.assertNotEquals(replicaSyncInfo, new Object());
        Assert.assertNotEquals(replicaSyncInfo, replicaSyncInfoOtherPartitionId);
        Assert.assertNotEquals(replicaSyncInfo, replicaSyncInfoOtherReplicaIndex);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(replicaSyncInfo.hashCode(), replicaSyncInfo.hashCode());
        Assert.assertEquals(replicaSyncInfo.hashCode(), replicaSyncInfoSameAttributes.hashCode());
        Assert.assertEquals(replicaSyncInfo.hashCode(), replicaSyncInfoOtherTarget.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(replicaSyncInfo.hashCode(), replicaSyncInfoOtherPartitionId.hashCode());
        Assert.assertNotEquals(replicaSyncInfo.hashCode(), replicaSyncInfoOtherReplicaIndex.hashCode());
    }

    @Test
    public void testToString() {
        Assert.assertNotNull(replicaSyncInfo.toString());
    }
}

