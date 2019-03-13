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
package com.hazelcast.mapreduce.impl.task;


import com.hazelcast.nio.Address;
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
public class JobPartitionStateImplTest {
    private Address address;

    private Address otherAddress;

    private JobPartitionStateImpl jobPartitionState;

    private JobPartitionStateImpl jobPartitionStateSameAttributes;

    private JobPartitionStateImpl jobPartitionStateOtherAddress;

    private JobPartitionStateImpl jobPartitionStateOtherState;

    @Test
    public void testGetOwner() {
        Assert.assertEquals(address, jobPartitionState.getOwner());
        Assert.assertEquals(otherAddress, jobPartitionStateOtherAddress.getOwner());
    }

    @Test
    public void testGetState() {
        Assert.assertEquals(WAITING, jobPartitionState.getState());
        Assert.assertEquals(CANCELLED, jobPartitionStateOtherState.getState());
    }

    @Test
    public void testToString() {
        Assert.assertNotNull(jobPartitionState.toString());
    }

    @Test
    public void testEquals() {
        Assert.assertEquals(jobPartitionState, jobPartitionState);
        Assert.assertEquals(jobPartitionState, jobPartitionStateSameAttributes);
        Assert.assertNotEquals(jobPartitionState, null);
        Assert.assertNotEquals(jobPartitionState, new Object());
        Assert.assertNotEquals(jobPartitionState, jobPartitionStateOtherAddress);
        Assert.assertNotEquals(jobPartitionState, jobPartitionStateOtherState);
    }

    @Test
    public void testHashCode() {
        Assert.assertEquals(jobPartitionState.hashCode(), jobPartitionState.hashCode());
        Assert.assertEquals(jobPartitionState.hashCode(), jobPartitionStateSameAttributes.hashCode());
        HazelcastTestSupport.assumeDifferentHashCodes();
        Assert.assertNotEquals(jobPartitionState.hashCode(), jobPartitionStateOtherAddress.hashCode());
        Assert.assertNotEquals(jobPartitionState.hashCode(), jobPartitionStateOtherState.hashCode());
    }
}

