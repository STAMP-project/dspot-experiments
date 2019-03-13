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


import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.MemberInfo;
import com.hazelcast.nio.Address;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MembersViewTest {
    @Test
    public void createNew() throws Exception {
        int version = 7;
        MemberImpl[] members = MemberMapTest.newMembers(5);
        MembersView view = MembersView.createNew(version, Arrays.asList(members));
        Assert.assertEquals(version, view.getVersion());
        MembersViewTest.assertMembersViewEquals(members, view);
    }

    @Test
    public void cloneAdding() throws Exception {
        int version = 6;
        MemberImpl[] members = MemberMapTest.newMembers(4);
        List<MemberInfo> additionalMembers = Arrays.asList(new MemberInfo(MemberMapTest.newMember(6000)), new MemberInfo(MemberMapTest.newMember(7000)));
        MembersView view = MembersView.cloneAdding(MembersView.createNew(version, Arrays.asList(members)), additionalMembers);
        Assert.assertEquals((version + (additionalMembers.size())), view.getVersion());
        MemberImpl[] newMembers = Arrays.copyOf(members, ((members.length) + (additionalMembers.size())));
        for (int i = 0; i < (additionalMembers.size()); i++) {
            newMembers[((members.length) + i)] = additionalMembers.get(i).toMember();
        }
        MembersViewTest.assertMembersViewEquals(newMembers, view);
    }

    @Test
    public void toMemberMap() throws Exception {
        int version = 5;
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MembersView view = MembersView.createNew(version, Arrays.asList(members));
        MemberMap memberMap = view.toMemberMap();
        Assert.assertEquals(version, memberMap.getVersion());
        MembersViewTest.assertMembersViewEquals(memberMap.getMembers().toArray(new MemberImpl[0]), view);
    }

    @Test
    public void containsAddress() throws Exception {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MembersView view = MembersView.createNew(1, Arrays.asList(members));
        for (MemberImpl member : members) {
            Assert.assertTrue(view.containsAddress(member.getAddress()));
        }
    }

    @Test
    public void containsMember() throws Exception {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MembersView view = MembersView.createNew(1, Arrays.asList(members));
        for (MemberImpl member : members) {
            Assert.assertTrue(view.containsMember(member.getAddress(), member.getUuid()));
        }
    }

    @Test
    public void getAddresses() throws Exception {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MembersView view = MembersView.createNew(1, Arrays.asList(members));
        Set<Address> addresses = view.getAddresses();
        Assert.assertEquals(members.length, addresses.size());
        for (MemberImpl member : members) {
            Assert.assertTrue(addresses.contains(member.getAddress()));
        }
    }

    @Test
    public void getMember() throws Exception {
        MemberImpl[] members = MemberMapTest.newMembers(3);
        MembersView view = MembersView.createNew(1, Arrays.asList(members));
        MemberInfo member = view.getMember(members[0].getAddress());
        Assert.assertNotNull(member);
        Assert.assertEquals(members[0].getUuid(), member.getUuid());
    }

    @Test
    public void isLaterThan() throws Exception {
        MembersView view1 = MembersView.createNew(1, Arrays.asList(MemberMapTest.newMembers(5)));
        MembersView view2 = MembersView.createNew(3, Arrays.asList(MemberMapTest.newMembers(5)));
        MembersView view3 = MembersView.createNew(5, Arrays.asList(MemberMapTest.newMembers(5)));
        Assert.assertTrue(view2.isLaterThan(view1));
        Assert.assertTrue(view3.isLaterThan(view1));
        Assert.assertTrue(view3.isLaterThan(view2));
        Assert.assertFalse(view1.isLaterThan(view1));
        Assert.assertFalse(view1.isLaterThan(view2));
        Assert.assertFalse(view1.isLaterThan(view3));
        Assert.assertFalse(view2.isLaterThan(view2));
        Assert.assertFalse(view2.isLaterThan(view3));
        Assert.assertFalse(view3.isLaterThan(view3));
    }
}

