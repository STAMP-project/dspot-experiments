/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.commandhandling.distributed;


import AcceptAll.INSTANCE;
import ConsistentHash.ConsistentHashMember;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;

import static ConsistentHash.ConsistentHashMember;


public class ConsistentHashTest {
    private ConsistentHash testSubject;

    private Member member1;

    private Member member2;

    @Test
    public void testToString() {
        Assert.assertEquals(testSubject.toString(), "ConsistentHash [member1(2),member2(2),member3(2)]");
    }

    @Test
    public void testConsistentHashChangesVersionWhenModified() {
        Assert.assertEquals(3, testSubject.version());
        Assert.assertEquals(4, testSubject.without(member1).version());
        Assert.assertEquals(4, testSubject.without(member1).without(member1).version());
    }

    @Test
    public void testMessageRoutedToFirstEligibleMember() {
        Optional<Member> actual = testSubject.getMember("routingKey", new org.axonframework.commandhandling.GenericCommandMessage(new org.axonframework.messaging.GenericMessage("test"), "name1"));
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals("member1", actual.get().name());
    }

    @Test
    public void testMessageRoutedToNextEligibleMemberIfFirstChoiceIsRemoved() {
        Optional<Member> actual = testSubject.without(member1).getMember("routingKey", new org.axonframework.commandhandling.GenericCommandMessage(new org.axonframework.messaging.GenericMessage("test"), "name1"));
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals("member2", actual.get().name());
    }

    @Test
    public void testNonEligibleMembersIgnored() {
        Optional<Member> actual = testSubject.getMember("routingKey", new org.axonframework.commandhandling.GenericCommandMessage(new org.axonframework.messaging.GenericMessage("test"), "name3"));
        Assert.assertTrue(actual.isPresent());
        Assert.assertEquals("member3", actual.get().name());
    }

    @Test
    public void testNoMemberReturnedWhenNoEligibleMembers() {
        Optional<Member> actual = testSubject.getMember("routingKey", new org.axonframework.commandhandling.GenericCommandMessage(new org.axonframework.messaging.GenericMessage("test"), "unknown"));
        Assert.assertFalse(actual.isPresent());
    }

    @Test
    public void testEligibleMembersCorrectlyOrdered() {
        Collection<ConsistentHash.ConsistentHashMember> actual = testSubject.getEligibleMembers("someOtherKey");
        Assert.assertEquals(Arrays.asList("member2", "member1", "member3"), actual.stream().map(ConsistentHashMember::name).collect(Collectors.toList()));
    }

    @Test
    public void testConflictingHashesDoNotImpactMembership() {
        ConsistentHash consistentHash = new ConsistentHash(( s) -> "fixed").with(member1, 1, INSTANCE);
        ConsistentHash consistentHashModified = consistentHash.with(member2, 1, INSTANCE).without(member2);
        Assert.assertEquals(member1.name(), consistentHash.getMembers().iterator().next().name());
        Assert.assertEquals(consistentHash.getMembers(), consistentHashModified.getMembers());
    }
}

