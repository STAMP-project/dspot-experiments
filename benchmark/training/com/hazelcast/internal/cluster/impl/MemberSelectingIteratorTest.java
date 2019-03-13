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


import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberSelectingIteratorTest extends HazelcastTestSupport {
    private MemberImpl thisMember;

    private MemberImpl matchingMember;

    private MemberImpl matchingMember2;

    private MemberImpl nonMatchingMember;

    @Test
    public void testSelectingLiteMembersWithThisAddress() {
        final Set<MemberImpl> members = createMembers();
        final Iterator<MemberImpl> iterator = new MemberSelectingCollection<MemberImpl>(members, MemberSelectors.LITE_MEMBER_SELECTOR).iterator();
        final Set<MemberImpl> filteredMembers = new HashSet<MemberImpl>();
        while (iterator.hasNext()) {
            filteredMembers.add(iterator.next());
        } 
        Assert.assertEquals(3, filteredMembers.size());
        HazelcastTestSupport.assertContains(filteredMembers, thisMember);
        HazelcastTestSupport.assertContains(filteredMembers, matchingMember);
        HazelcastTestSupport.assertContains(filteredMembers, matchingMember2);
    }

    @Test
    public void testSelectingLiteMembersWithoutThisAddress() {
        final Set<MemberImpl> members = createMembers();
        final Iterator<MemberImpl> iterator = new MemberSelectingCollection<MemberImpl>(members, MemberSelectors.and(MemberSelectors.LITE_MEMBER_SELECTOR, MemberSelectors.NON_LOCAL_MEMBER_SELECTOR)).iterator();
        final Set<MemberImpl> filteredMembers = new HashSet<MemberImpl>();
        while (iterator.hasNext()) {
            filteredMembers.add(iterator.next());
        } 
        Assert.assertEquals(2, filteredMembers.size());
        HazelcastTestSupport.assertContains(filteredMembers, matchingMember);
        HazelcastTestSupport.assertContains(filteredMembers, matchingMember2);
    }

    @Test
    public void testSelectingMembersWithThisAddress() {
        final Set<MemberImpl> members = createMembers();
        final Iterator<MemberImpl> iterator = new MemberSelectingCollection<MemberImpl>(members, MemberSelectors.DATA_MEMBER_SELECTOR).iterator();
        final Set<MemberImpl> filteredMembers = new HashSet<MemberImpl>();
        while (iterator.hasNext()) {
            filteredMembers.add(iterator.next());
        } 
        Assert.assertEquals(1, filteredMembers.size());
        HazelcastTestSupport.assertContains(filteredMembers, nonMatchingMember);
    }

    @Test
    public void testSelectingMembersWithoutThisAddress() {
        final Set<MemberImpl> members = createMembers();
        final Iterator<MemberImpl> iterator = new MemberSelectingCollection<MemberImpl>(members, MemberSelectors.and(MemberSelectors.DATA_MEMBER_SELECTOR, MemberSelectors.NON_LOCAL_MEMBER_SELECTOR)).iterator();
        final Set<MemberImpl> filteredMembers = new HashSet<MemberImpl>();
        while (iterator.hasNext()) {
            filteredMembers.add(iterator.next());
        } 
        Assert.assertEquals(1, filteredMembers.size());
        HazelcastTestSupport.assertContains(filteredMembers, nonMatchingMember);
    }

    @Test
    public void testHasNextCalledTwice() {
        final Set<MemberImpl> members = createMembers();
        final Iterator<MemberImpl> iterator = new MemberSelectingCollection<MemberImpl>(members, MemberSelectors.and(MemberSelectors.LITE_MEMBER_SELECTOR, MemberSelectors.NON_LOCAL_MEMBER_SELECTOR)).iterator();
        while (iterator.hasNext()) {
            iterator.hasNext();
            iterator.next();
        } 
    }

    @Test(expected = NoSuchElementException.class)
    public void testIterationFailsAfterConsumed() {
        final Set<MemberImpl> members = createMembers();
        final Iterator<MemberImpl> iterator = new MemberSelectingCollection<MemberImpl>(members, MemberSelectors.and(MemberSelectors.LITE_MEMBER_SELECTOR, MemberSelectors.NON_LOCAL_MEMBER_SELECTOR)).iterator();
        while (iterator.hasNext()) {
            iterator.next();
        } 
        iterator.next();
    }
}

