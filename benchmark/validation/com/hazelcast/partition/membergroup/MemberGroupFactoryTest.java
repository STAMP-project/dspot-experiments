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
package com.hazelcast.partition.membergroup;


import com.hazelcast.config.MemberGroupConfig;
import com.hazelcast.core.Member;
import com.hazelcast.instance.BuildInfoProvider;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.version.MemberVersion;
import java.net.InetAddress;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(QuickTest.class)
public class MemberGroupFactoryTest {
    private static final MemberVersion VERSION = MemberVersion.of(BuildInfoProvider.getBuildInfo().getVersion());

    private InetAddress fakeAddress;

    @Test
    public void testHostAwareMemberGroupFactoryCreateMemberGroups() {
        MemberGroupFactory groupFactory = new HostAwareMemberGroupFactory();
        Collection<Member> members = createMembers();
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 8, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 2, memberGroup.size());
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testZoneAwareMemberGroupFactoryThrowsIllegalArgumentExceptionWhenNoMetadataIsProvided() {
        MemberGroupFactory groupFactory = new ZoneAwareMemberGroupFactory();
        Collection<Member> members = createMembersWithNoMetadata();
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 3, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 1, memberGroup.size());
        }
    }

    @Test
    public void testZoneMetadataAwareMemberGroupFactoryCreateMemberGroups() {
        MemberGroupFactory groupFactory = new ZoneAwareMemberGroupFactory();
        Collection<Member> members = createMembersWithZoneAwareMetadata();
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 3, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 1, memberGroup.size());
        }
    }

    @Test
    public void testRackMetadataAwareMemberGroupFactoryCreateMemberGroups() {
        MemberGroupFactory groupFactory = new ZoneAwareMemberGroupFactory();
        Collection<Member> members = createMembersWithRackAwareMetadata();
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 3, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 1, memberGroup.size());
        }
    }

    @Test
    public void testHostMetadataAwareMemberGroupFactoryCreateMemberGroups() {
        MemberGroupFactory groupFactory = new ZoneAwareMemberGroupFactory();
        Collection<Member> members = createMembersWithHostAwareMetadata();
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 3, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 1, memberGroup.size());
        }
    }

    /**
     * When there is a matching {@link MemberGroupConfig} for a {@link Member}, it will be assigned to a {@link MemberGroup}.
     * <p>
     * In this test all members will have a matching configuration, so there will be 4 groups with 2 members each.
     */
    @Test
    public void testConfigMemberGroupFactoryCreateMemberGroups() {
        Collection<Member> members = createMembers();
        Collection<MemberGroupConfig> groupConfigs = createMemberGroupConfigs(true);
        MemberGroupFactory groupFactory = new ConfigMemberGroupFactory(groupConfigs);
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 4, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 2, memberGroup.size());
        }
    }

    /**
     * When there is a matching {@link MemberGroupConfig} for a {@link Member}, it will be assigned to a {@link MemberGroup}.
     * <p>
     * In this test half of the members will have a matching configuration, so there will be 2 groups with 2 members each.
     */
    @Test
    public void testConfigMemberGroupFactoryCreateMemberGroups_withNonMatchingMembers() {
        Collection<Member> members = createMembers();
        Collection<MemberGroupConfig> groupConfigs = createMemberGroupConfigs(false);
        MemberGroupFactory groupFactory = new ConfigMemberGroupFactory(groupConfigs);
        Collection<MemberGroup> memberGroups = groupFactory.createMemberGroups(members);
        Assert.assertEquals(("Member Groups: " + (String.valueOf(memberGroups))), 2, memberGroups.size());
        for (MemberGroup memberGroup : memberGroups) {
            Assert.assertEquals(("Member Group: " + (String.valueOf(memberGroup))), 2, memberGroup.size());
        }
    }
}

