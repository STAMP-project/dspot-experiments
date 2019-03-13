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
package com.hazelcast.spi;


import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class CallsPerMemberTest {
    private Address address;

    private Address anotherAddress;

    private CallsPerMember callsPerMember;

    @Test
    public void testAdd() {
        callsPerMember.add(anotherAddress, 23);
        Set<Address> addresses = callsPerMember.addresses();
        Assert.assertEquals(1, addresses.size());
        Assert.assertEquals(anotherAddress, addresses.iterator().next());
    }

    @Test
    public void testAdd_whenAddressIsNull_thenAddsLocalAddress() {
        callsPerMember.add(null, 42);
        Set<Address> addresses = callsPerMember.addresses();
        Assert.assertEquals(1, addresses.size());
        Assert.assertEquals(address, addresses.iterator().next());
    }

    @Test
    public void testAdd_whenCallIdIsZero_thenNoAddressIsAdded() {
        callsPerMember.add(anotherAddress, 0);
        Set<Address> addresses = callsPerMember.addresses();
        Assert.assertEquals(0, addresses.size());
    }

    @Test
    public void testCallIds() {
        callsPerMember.add(anotherAddress, 23);
        long[] callIds = callsPerMember.toOpControl(anotherAddress).runningOperations();
        Assert.assertEquals(1, callIds.length);
        Assert.assertEquals(23, callIds[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCallIds_whenAddressIsUnknown_thenThrowException() {
        callsPerMember.toOpControl(address);
    }

    @Test
    public void testClearAndInitMember() {
        callsPerMember.add(address, 23);
        callsPerMember.clear();
        callsPerMember.getOrCreateCallIdsForMember(anotherAddress);
        long[] callIds = callsPerMember.toOpControl(anotherAddress).runningOperations();
        Assert.assertEquals(0, callIds.length);
    }
}

