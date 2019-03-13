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


import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.RequireAssertEnabled;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.UuidUtil;
import com.hazelcast.version.MemberVersion;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberMapTest {
    private static final MemberVersion VERSION = MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion());

    @Test(expected = AssertionError.class)
    @RequireAssertEnabled
    public void testConstructor_whenMapsHaveDifferentMembers_thenThrowAssertionError() {
        Map<Address, MemberImpl> addressMap = new HashMap<Address, MemberImpl>();
        Map<String, MemberImpl> uuidMap = new HashMap<String, MemberImpl>();
        MemberImpl addressMember = MemberMapTest.newMember(5701);
        MemberImpl uuidMember = MemberMapTest.newMember(5702);
        addressMap.put(addressMember.getAddress(), addressMember);
        uuidMap.put(uuidMember.getUuid(), uuidMember);
        new MemberMap(0, addressMap, uuidMap);
    }

    @Test
    public void createEmpty() {
        MemberMap map = MemberMap.empty();
        Assert.assertTrue(map.getMembers().isEmpty());
        Assert.assertTrue(map.getAddresses().isEmpty());
        Assert.assertEquals(0, map.size());
    }

    @Test
    public void createSingleton() {
        MemberImpl member = MemberMapTest.newMember(5000);
        MemberMap map = MemberMap.singleton(member);
        Assert.assertEquals(1, map.getMembers().size());
        Assert.assertEquals(1, map.getAddresses().size());
        Assert.assertEquals(1, map.size());
        MemberMapTest.assertContains(map, member.getAddress());
        MemberMapTest.assertContains(map, member.getUuid());
        Assert.assertSame(member, map.getMember(member.getAddress()));
        Assert.assertSame(member, map.getMember(member.getUuid()));
        MemberMapTest.assertMemberSet(map);
    }

    @Test
    public void createNew() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        Assert.assertEquals(members.length, map.getMembers().size());
        Assert.assertEquals(members.length, map.getAddresses().size());
        Assert.assertEquals(members.length, map.size());
        for (MemberImpl member : members) {
            MemberMapTest.assertContains(map, member.getAddress());
            MemberMapTest.assertContains(map, member.getUuid());
            Assert.assertSame(member, map.getMember(member.getAddress()));
            Assert.assertSame(member, map.getMember(member.getUuid()));
        }
        MemberMapTest.assertMemberSet(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_failsWithDuplicateAddress() {
        MemberImpl member1 = MemberMapTest.newMember(5000);
        MemberImpl member2 = MemberMapTest.newMember(5000);
        MemberMap.createNew(member1, member2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void create_failsWithDuplicateUuid() {
        MemberImpl member1 = MemberMapTest.newMember(5000);
        MemberImpl member2 = new MemberImpl.Builder(MemberMapTest.newAddress(5001)).version(MemberMapTest.VERSION).uuid(member1.getUuid()).build();
        MemberMap.createNew(member1, member2);
    }

    @Test
    public void cloneExcluding() {
        MemberImpl[] members = MemberMapTest.newMembers(6);
        MemberImpl exclude0 = members[0];
        MemberImpl exclude1 = new MemberImpl.Builder(MemberMapTest.newAddress(6000)).version(MemberMapTest.VERSION).uuid(members[1].getUuid()).build();
        MemberImpl exclude2 = new MemberImpl.Builder(members[2].getAddress()).version(MemberMapTest.VERSION).uuid(UuidUtil.newUnsecureUuidString()).build();
        MemberMap map = MemberMap.cloneExcluding(MemberMap.createNew(members), exclude0, exclude1, exclude2);
        int numOfExcludedMembers = 3;
        Assert.assertEquals(((members.length) - numOfExcludedMembers), map.getMembers().size());
        Assert.assertEquals(((members.length) - numOfExcludedMembers), map.getAddresses().size());
        Assert.assertEquals(((members.length) - numOfExcludedMembers), map.size());
        for (int i = 0; i < numOfExcludedMembers; i++) {
            MemberImpl member = members[i];
            MemberMapTest.assertNotContains(map, member.getAddress());
            MemberMapTest.assertNotContains(map, member.getUuid());
            Assert.assertNull(map.getMember(member.getAddress()));
            Assert.assertNull(map.getMember(member.getUuid()));
        }
        for (int i = numOfExcludedMembers; i < (members.length); i++) {
            MemberImpl member = members[i];
            MemberMapTest.assertContains(map, member.getAddress());
            MemberMapTest.assertContains(map, member.getUuid());
            Assert.assertSame(member, map.getMember(member.getAddress()));
            Assert.assertSame(member, map.getMember(member.getUuid()));
        }
        MemberMapTest.assertMemberSet(map);
    }

    @Test
    public void cloneExcluding_emptyMap() {
        MemberMap empty = MemberMap.empty();
        MemberMap map = MemberMap.cloneExcluding(empty, MemberMapTest.newMember(5000));
        Assert.assertEquals(0, map.size());
        Assert.assertSame(empty, map);
    }

    @Test
    public void cloneAdding() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.cloneAdding(MemberMap.createNew(members[0], members[1], members[2]), members[3], members[4]);
        Assert.assertEquals(members.length, map.getMembers().size());
        Assert.assertEquals(members.length, map.getAddresses().size());
        Assert.assertEquals(members.length, map.size());
        for (MemberImpl member : members) {
            MemberMapTest.assertContains(map, member.getAddress());
            MemberMapTest.assertContains(map, member.getUuid());
            Assert.assertSame(member, map.getMember(member.getAddress()));
            Assert.assertSame(member, map.getMember(member.getUuid()));
        }
        MemberMapTest.assertMemberSet(map);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cloneAdding_failsWithDuplicateAddress() {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MemberImpl member = MemberMapTest.newMember(5000);
        MemberMap.cloneAdding(MemberMap.createNew(members), member);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cloneAdding_failsWithDuplicateUuid() {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MemberImpl member = new MemberImpl.Builder(MemberMapTest.newAddress(6000)).version(MemberMapTest.VERSION).uuid(members[1].getUuid()).build();
        MemberMap.cloneAdding(MemberMap.createNew(members), member);
    }

    @Test
    public void getMembers_ordered() {
        MemberImpl[] members = MemberMapTest.newMembers(10);
        MemberMap map = MemberMap.createNew(members);
        Set<MemberImpl> memberSet = map.getMembers();
        int index = 0;
        for (MemberImpl member : memberSet) {
            Assert.assertSame(members[(index++)], member);
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getMembers_unmodifiable() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        map.getMembers().add(MemberMapTest.newMember(9000));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAddresses_unmodifiable() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        map.getAddresses().add(MemberMapTest.newAddress(9000));
    }

    @Test
    public void getMember_withAddressAndUuid_whenFound() {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MemberMap map = MemberMap.createNew(members);
        MemberImpl member = members[0];
        Assert.assertEquals(member, map.getMember(member.getAddress(), member.getUuid()));
    }

    @Test
    public void getMember_withAddressAndUuid_whenOnlyAddressFound() {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MemberMap map = MemberMap.createNew(members);
        Assert.assertNull(map.getMember(members[0].getAddress(), UuidUtil.newUnsecureUuidString()));
    }

    @Test
    public void getMember_withAddressAndUuid_whenOnlyUuidFound() {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MemberMap map = MemberMap.createNew(members);
        Assert.assertNull(map.getMember(MemberMapTest.newAddress(6000), members[0].getUuid()));
    }

    @Test
    public void getMember_withAddressAndUuid_whenNotFound() {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MemberMap map = MemberMap.createNew(members);
        Assert.assertNull(map.getMember(MemberMapTest.newAddress(6000), UuidUtil.newUnsecureUuidString()));
    }

    @Test
    public void tailMemberSet_inclusive() {
        MemberImpl[] members = MemberMapTest.newMembers(7);
        MemberMap map = MemberMap.createNew(members);
        MemberImpl member = members[3];
        Set<MemberImpl> set = map.tailMemberSet(member, true);
        Assert.assertEquals(4, set.size());
        int k = 3;
        for (MemberImpl m : set) {
            Assert.assertEquals(members[(k++)], m);
        }
    }

    @Test
    public void tailMemberSet_exclusive() {
        MemberImpl[] members = MemberMapTest.newMembers(7);
        MemberMap map = MemberMap.createNew(members);
        MemberImpl member = members[3];
        Set<MemberImpl> set = map.tailMemberSet(member, false);
        Assert.assertEquals(3, set.size());
        int k = 4;
        for (MemberImpl m : set) {
            Assert.assertEquals(members[(k++)], m);
        }
    }

    @Test
    public void headMemberSet_inclusive() {
        MemberImpl[] members = MemberMapTest.newMembers(7);
        MemberMap map = MemberMap.createNew(members);
        MemberImpl member = members[3];
        Set<MemberImpl> set = map.headMemberSet(member, true);
        Assert.assertEquals(4, set.size());
        int k = 0;
        for (MemberImpl m : set) {
            Assert.assertEquals(members[(k++)], m);
        }
    }

    @Test
    public void headMemberSet_exclusive() {
        MemberImpl[] members = MemberMapTest.newMembers(7);
        MemberMap map = MemberMap.createNew(members);
        MemberImpl member = members[3];
        Set<MemberImpl> set = map.headMemberSet(member, false);
        Assert.assertEquals(3, set.size());
        int k = 0;
        for (MemberImpl m : set) {
            Assert.assertEquals(members[(k++)], m);
        }
    }

    @Test
    public void isBeforeThan_success() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        Assert.assertTrue(map.isBeforeThan(members[1].getAddress(), members[3].getAddress()));
    }

    @Test
    public void isBeforeThan_fail() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        Assert.assertFalse(map.isBeforeThan(members[4].getAddress(), members[1].getAddress()));
    }

    @Test
    public void isBeforeThan_whenFirstAddressNotExist() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        map.isBeforeThan(MemberMapTest.newAddress(6000), members[0].getAddress());
    }

    @Test
    public void isBeforeThan_whenSecondAddressNotExist() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        map.isBeforeThan(members[0].getAddress(), MemberMapTest.newAddress(6000));
    }

    @Test
    public void isBeforeThan_whenAddressesNotExist() {
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MemberMap map = MemberMap.createNew(members);
        map.isBeforeThan(MemberMapTest.newAddress(6000), MemberMapTest.newAddress(7000));
    }
}

