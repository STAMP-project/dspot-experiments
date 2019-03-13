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
package com.hazelcast.client.proxy;


import com.hazelcast.client.spi.ClientProxy;
import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ProxyEqualityTest {
    private final TestHazelcastFactory hazelcastFactoryGroupA = new TestHazelcastFactory();

    private final TestHazelcastFactory hazelcastFactoryGroupB = new TestHazelcastFactory();

    private final String atomicName = "foo";

    private HazelcastInstance client1GroupA;

    private HazelcastInstance client2GroupA;

    private HazelcastInstance client1GroupB;

    @Test
    public void testTwoClientProxiesFromTheSameInstanceAreEquals() {
        ClientProxy ref1 = ((ClientProxy) (client1GroupA.getAtomicLong(atomicName)));
        ClientProxy ref2 = ((ClientProxy) (client1GroupA.getAtomicLong(atomicName)));
        Assert.assertEquals(ref1, ref2);
    }

    @Test
    public void testProxiesAreCached() {
        ClientProxy ref1 = ((ClientProxy) (client1GroupA.getAtomicLong(atomicName)));
        ClientProxy ref2 = ((ClientProxy) (client1GroupA.getAtomicLong(atomicName)));
        Assert.assertSame(ref1, ref2);
    }

    @Test
    public void testTwoClientProxiesFromDifferentInstancesAreNotEquals() {
        ClientProxy ref1 = ((ClientProxy) (client1GroupA.getAtomicLong(atomicName)));
        ClientProxy ref2 = ((ClientProxy) (client1GroupB.getAtomicLong(atomicName)));
        Assert.assertNotEquals(ref1, ref2);
    }

    @Test
    public void testTwoClientProxiesFromTwoDifferentClientsConnectedToTheSameInstanceAreNotEquals() {
        ClientProxy ref1 = ((ClientProxy) (client1GroupA.getAtomicLong(atomicName)));
        ClientProxy ref2 = ((ClientProxy) (client2GroupA.getAtomicLong(atomicName)));
        Assert.assertNotEquals(ref1, ref2);
    }
}

