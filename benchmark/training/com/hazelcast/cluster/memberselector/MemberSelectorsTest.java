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
package com.hazelcast.cluster.memberselector;


import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class MemberSelectorsTest {
    @Mock
    private Member member = Mockito.mock(Member.class);

    @Test
    public void testLiteMemberSelector() {
        Mockito.when(member.isLiteMember()).thenReturn(true, true);
        Assert.assertTrue(MemberSelectors.LITE_MEMBER_SELECTOR.select(member));
        Assert.assertFalse(MemberSelectors.DATA_MEMBER_SELECTOR.select(member));
    }

    @Test
    public void testDataMemberSelector() {
        Assert.assertFalse(MemberSelectors.LITE_MEMBER_SELECTOR.select(member));
        Assert.assertTrue(MemberSelectors.DATA_MEMBER_SELECTOR.select(member));
    }

    @Test
    public void testLocalMemberSelector() {
        Mockito.when(member.localMember()).thenReturn(true, true);
        Assert.assertTrue(MemberSelectors.LOCAL_MEMBER_SELECTOR.select(member));
        Assert.assertFalse(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR.select(member));
    }

    @Test
    public void testNonLocalMemberSelector() {
        Assert.assertFalse(MemberSelectors.LOCAL_MEMBER_SELECTOR.select(member));
        Assert.assertTrue(MemberSelectors.NON_LOCAL_MEMBER_SELECTOR.select(member));
    }

    @Test
    public void testAndMemberSelector() {
        Mockito.when(member.localMember()).thenReturn(true);
        MemberSelector selector = MemberSelectors.and(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertFalse(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member).isLiteMember();
    }

    @Test
    public void testAndMemberSelector2() {
        MemberSelector selector = MemberSelectors.and(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertFalse(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member, Mockito.never()).isLiteMember();
    }

    @Test
    public void testAndMemberSelector3() {
        Mockito.when(member.localMember()).thenReturn(true);
        Mockito.when(member.isLiteMember()).thenReturn(true);
        MemberSelector selector = MemberSelectors.and(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertTrue(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member).isLiteMember();
    }

    @Test
    public void testOrMemberSelector() {
        Mockito.when(member.localMember()).thenReturn(true);
        MemberSelector selector = MemberSelectors.or(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertTrue(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member, Mockito.never()).isLiteMember();
    }

    @Test
    public void testOrMemberSelector2() {
        MemberSelector selector = MemberSelectors.or(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertFalse(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member).isLiteMember();
    }

    @Test
    public void testOrMemberSelector3() {
        Mockito.when(member.localMember()).thenReturn(true);
        Mockito.when(member.isLiteMember()).thenReturn(true);
        MemberSelector selector = MemberSelectors.or(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertTrue(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member, Mockito.never()).isLiteMember();
    }

    @Test
    public void testOrMemberSelector4() {
        Mockito.when(member.isLiteMember()).thenReturn(true);
        MemberSelector selector = MemberSelectors.or(MemberSelectors.LOCAL_MEMBER_SELECTOR, MemberSelectors.LITE_MEMBER_SELECTOR);
        Assert.assertTrue(selector.select(member));
        Mockito.verify(member).localMember();
        Mockito.verify(member).isLiteMember();
    }
}

