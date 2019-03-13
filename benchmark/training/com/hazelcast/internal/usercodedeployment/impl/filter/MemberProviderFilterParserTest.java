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
package com.hazelcast.internal.usercodedeployment.impl.filter;


import com.google.common.collect.ImmutableMap;
import com.hazelcast.core.Member;
import com.hazelcast.internal.util.filter.AlwaysApplyFilter;
import com.hazelcast.internal.util.filter.Filter;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MemberProviderFilterParserTest extends HazelcastTestSupport {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(MemberProviderFilterParser.class);
    }

    @Test
    public void whenStringIsNull_thenReturnAlwaysMatchingInstance() {
        Filter<Member> memberFilter = MemberProviderFilterParser.parseMemberFilter(null);
        Assert.assertTrue((memberFilter instanceof AlwaysApplyFilter));
    }

    @Test
    public void whenStringIsWhitespace_thenReturnAlwaysMatchingInstance() {
        Filter<Member> memberFilter = MemberProviderFilterParser.parseMemberFilter("  ");
        Assert.assertTrue((memberFilter instanceof AlwaysApplyFilter));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenStringHasUnknownPrefix_thenThrowIllegalArgumentException() {
        MemberProviderFilterParser.parseMemberFilter("FOOO");
    }

    @Test
    public void givenMemberAttributeFilterIsUsed_whenMemberAttributeIsPresent_thenFilterMatches() {
        Filter<Member> memberFilter = MemberProviderFilterParser.parseMemberFilter("HAS_ATTRIBUTE:foo");
        Map<String, Object> attributes = ImmutableMap.of("foo", ((Object) ("bar")));
        Member mockMember = MemberProviderFilterParserTest.createMockMemberWithAttributes(attributes);
        Assert.assertTrue(memberFilter.accept(mockMember));
    }

    @Test
    public void givenMemberAttributeFilterIsUsed_whenMemberAttributeIsNotPresent_thenFilterDoesNotMatch() {
        Filter<Member> memberFilter = MemberProviderFilterParser.parseMemberFilter("HAS_ATTRIBUTE:foo");
        Map<String, Object> attributes = ImmutableMap.of("bar", ((Object) ("other")));
        Member mockMember = MemberProviderFilterParserTest.createMockMemberWithAttributes(attributes);
        Assert.assertFalse(memberFilter.accept(mockMember));
    }
}

