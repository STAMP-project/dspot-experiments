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
package com.hazelcast.internal.cluster.impl;


import ClusterState.ACTIVE;
import ClusterState.FROZEN;
import Version.UNKNOWN;
import com.hazelcast.cluster.ClusterState;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.Version;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClusterStateChangeTest {
    private ClusterStateChange clusterStateChange;

    private ClusterStateChange clusterStateChangeSameAttributes;

    private ClusterStateChange clusterStateChangeOtherType;

    private ClusterStateChange clusterStateChangeOtherNewState;

    @Test
    public void testGetType() {
        Assert.assertEquals(ClusterState.class, clusterStateChange.getType());
        Assert.assertEquals(ClusterState.class, clusterStateChangeSameAttributes.getType());
        Assert.assertEquals(Version.class, clusterStateChangeOtherType.getType());
        Assert.assertEquals(ClusterState.class, clusterStateChangeOtherNewState.getType());
    }

    @Test
    public void testGetNewState() {
        Assert.assertEquals(ACTIVE, clusterStateChange.getNewState());
        Assert.assertEquals(ACTIVE, clusterStateChangeSameAttributes.getNewState());
        Assert.assertEquals(UNKNOWN, clusterStateChangeOtherType.getNewState());
        Assert.assertEquals(FROZEN, clusterStateChangeOtherNewState.getNewState());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(clusterStateChange, clusterStateChange);
        Assert.assertEquals(clusterStateChange, clusterStateChangeSameAttributes);
        Assert.assertNotEquals(clusterStateChange, null);
        Assert.assertNotEquals(clusterStateChange, new Object());
        Assert.assertNotEquals(clusterStateChange, clusterStateChangeOtherType);
        Assert.assertNotEquals(clusterStateChange, clusterStateChangeOtherNewState);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(clusterStateChange.hashCode(), clusterStateChange.hashCode());
        Assert.assertEquals(clusterStateChange.hashCode(), clusterStateChangeSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(clusterStateChange.hashCode(), clusterStateChangeOtherType.hashCode());
        Assert.assertNotEquals(clusterStateChange.hashCode(), clusterStateChangeOtherNewState.hashCode());
    }
}

