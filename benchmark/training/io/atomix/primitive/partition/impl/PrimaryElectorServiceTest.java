/**
 * Copyright 2019-present Open Networking Foundation
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
package io.atomix.primitive.partition.impl;


import io.atomix.primitive.partition.GroupMember;
import io.atomix.primitive.partition.PartitionId;
import io.atomix.primitive.partition.PrimaryTerm;
import io.atomix.primitive.session.Session;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class PrimaryElectorServiceTest {
    static long sessionNum = 0;

    @Test
    public void testEnterSinglePartition() {
        PartitionId partition = new PartitionId("test", 1);
        PrimaryElectorService elector = newService();
        PrimaryTerm term;
        // 1st member to enter should be primary.
        GroupMember m1 = createGroupMember("node1", "group1");
        Session<?> s1 = createSession(m1);
        term = elector.enter(createEnterOp(partition, m1, s1));
        Assert.assertEquals(1L, term.term());
        Assert.assertEquals(m1, term.primary());
        Assert.assertEquals(1, term.candidates().size());
        // 2nd member to enter should be added to candidates.
        GroupMember m2 = createGroupMember("node2", "group1");
        Session<?> s2 = createSession(m2);
        term = elector.enter(createEnterOp(partition, m2, s2));
        Assert.assertEquals(1L, term.term());
        Assert.assertEquals(m1, term.primary());
        Assert.assertEquals(2, term.candidates().size());
        Assert.assertEquals(m2, term.candidates().get(1));
    }

    @Test
    public void testEnterSeveralPartitions() {
        PrimaryElectorService elector = newService();
        PrimaryTerm term = null;
        int numParts = 10;
        int numMembers = 20;
        List<List<GroupMember>> allMembers = new ArrayList<>();
        List<PrimaryTerm> terms = new ArrayList<>();
        for (int p = 0; p < numParts; p++) {
            PartitionId partId = new PartitionId("test", p);
            allMembers.add(new ArrayList());
            // Add all members in same group.
            for (int i = 0; i < numMembers; i++) {
                GroupMember m = createGroupMember(("node" + i), "group1");
                allMembers.get(p).add(m);
                Session<?> s = createSession(m);
                term = elector.enter(createEnterOp(partId, m, s));
            }
            if (term != null) {
                terms.add(term);
            }
        }
        // Check primary and candidates in each partition.
        for (int p = 0; p < numParts; p++) {
            Assert.assertEquals(1L, terms.get(p).term());
            Assert.assertEquals(allMembers.get(p).get(0), terms.get(p).primary());
            Assert.assertEquals(numMembers, terms.get(p).candidates().size());
            for (int i = 0; i < numMembers; i++) {
                Assert.assertEquals(allMembers.get(p).get(i), terms.get(p).candidates().get(i));
            }
        }
    }

    @Test
    public void testEnterSinglePartitionWithGroups() {
        PrimaryElectorService elector = newService();
        PartitionId partId = new PartitionId("test", 1);
        PrimaryTerm term = null;
        int numMembers = 9;
        // Add 9 members in 3 different groups.
        List<GroupMember> members = new ArrayList<>();
        for (int i = 0; i < numMembers; i++) {
            GroupMember m = createGroupMember(("node" + i), ("group" + (i / 3)));
            members.add(m);
            Session<?> s = createSession(m);
            term = elector.enter(createEnterOp(partId, m, s));
        }
        // Check primary and candidates.
        Assert.assertEquals(1L, term.term());
        Assert.assertEquals(members.get(0), term.primary());
        Assert.assertEquals(numMembers, term.candidates().size());
        // Check backups are selected in different groups.
        List<GroupMember> backups2 = term.backups(2);
        Assert.assertEquals(members.get(3), backups2.get(0));
        Assert.assertEquals(members.get(6), backups2.get(1));
        List<GroupMember> backups3 = term.backups(3);
        Assert.assertEquals(members.get(3), backups3.get(0));
        Assert.assertEquals(members.get(6), backups3.get(1));
        Assert.assertEquals(members.get(1), backups3.get(2));
    }

    @Test
    public void testEnterAndExpireSessions() {
        PrimaryElectorService elector = newService();
        PartitionId partId = new PartitionId("test", 1);
        PrimaryTerm term = null;
        int numMembers = 9;
        // Add 9 members in 3 different groups.
        List<Session<?>> sessions = new ArrayList<>();
        List<GroupMember> members = new ArrayList<>();
        for (int i = 0; i < numMembers; i++) {
            GroupMember m = createGroupMember(("node" + i), ("group" + (i / 3)));
            members.add(m);
            Session<?> s = createSession(m);
            sessions.add(s);
            term = elector.enter(createEnterOp(partId, m, s));
        }
        // Check current primary.
        Assert.assertEquals(1L, term.term());
        Assert.assertEquals(members.get(0), term.primary());
        Assert.assertEquals(numMembers, term.candidates().size());
        List<GroupMember> backups1 = term.backups(2);
        Assert.assertEquals(members.get(3), backups1.get(0));
        Assert.assertEquals(members.get(6), backups1.get(1));
        // Expire session of primary and check new term.
        // New primary should be the first of the old backups.
        elector.onExpire(sessions.get(0));
        term = elector.getTerm(createGetTermOp(partId, members.get(3), sessions.get(3)));
        Assert.assertEquals(2L, term.term());
        Assert.assertEquals(members.get(3), term.primary());
        Assert.assertEquals((numMembers - 1), term.candidates().size());
        List<GroupMember> backups2 = term.backups(2);
        Assert.assertEquals(members.get(6), backups2.get(0));
        Assert.assertEquals(members.get(1), backups2.get(1));
        // Expire session of backup and check term updated.
        elector.onExpire(sessions.get(6));
        term = elector.getTerm(createGetTermOp(partId, members.get(5), sessions.get(5)));
        Assert.assertEquals(2L, term.term());
        Assert.assertEquals(members.get(3), term.primary());
        Assert.assertEquals((numMembers - 2), term.candidates().size());
        List<GroupMember> backups3 = term.backups(2);
        Assert.assertEquals(members.get(1), backups3.get(0));
        Assert.assertEquals(members.get(4), backups3.get(1));
    }

    @Test
    public void testSortCandidatesByGroup() {
        PrimaryElectorService elector = newService();
        PrimaryTerm term = null;
        term = enter("node1", "group1", elector);
        Assert.assertEquals("node1", term.primary().memberId().id());
        term = enter("node2", "group1", elector);
        Assert.assertEquals("node1", term.primary().memberId().id());
        Assert.assertEquals("node2", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node2", term.backups(2).get(0).memberId().id());
        term = enter("node3", "group1", elector);
        Assert.assertEquals("node1", term.primary().memberId().id());
        Assert.assertEquals("node2", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node2", term.backups(2).get(0).memberId().id());
        Assert.assertEquals("node3", term.candidates().get(2).memberId().id());
        Assert.assertEquals("node3", term.backups(2).get(1).memberId().id());
        term = enter("node4", "group2", elector);
        Assert.assertEquals("node1", term.primary().memberId().id());
        Assert.assertEquals("node4", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node4", term.backups(2).get(0).memberId().id());
        Assert.assertEquals("node2", term.candidates().get(2).memberId().id());
        Assert.assertEquals("node2", term.backups(2).get(1).memberId().id());
        term = enter("node5", "group3", elector);
        Assert.assertEquals("node1", term.primary().memberId().id());
        Assert.assertEquals("node4", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node4", term.backups(2).get(0).memberId().id());
        Assert.assertEquals("node5", term.candidates().get(2).memberId().id());
        Assert.assertEquals("node5", term.backups(2).get(1).memberId().id());
        term = enter("node6", "group3", elector);
        Assert.assertEquals("node1", term.primary().memberId().id());
        Assert.assertEquals("node4", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node4", term.backups(2).get(0).memberId().id());
        Assert.assertEquals("node5", term.candidates().get(2).memberId().id());
        Assert.assertEquals("node5", term.backups(2).get(1).memberId().id());
        Assert.assertEquals("node1", term.candidates().get(0).memberId().id());
        Assert.assertEquals("node4", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node5", term.candidates().get(2).memberId().id());
        Assert.assertEquals("node2", term.candidates().get(3).memberId().id());
        Assert.assertEquals("node6", term.candidates().get(4).memberId().id());
        Assert.assertEquals("node3", term.candidates().get(5).memberId().id());
    }

    @Test
    public void testSortCandidatesWithoutGroup() {
        PrimaryElectorService elector = newService();
        PrimaryTerm term = null;
        term = enter("node1", "node1", elector);
        term = enter("node2", "node2", elector);
        term = enter("node3", "node3", elector);
        term = enter("node4", "node4", elector);
        term = enter("node5", "node5", elector);
        term = enter("node6", "node6", elector);
        Assert.assertEquals("node1", term.candidates().get(0).memberId().id());
        Assert.assertEquals("node2", term.candidates().get(1).memberId().id());
        Assert.assertEquals("node3", term.candidates().get(2).memberId().id());
        Assert.assertEquals("node4", term.candidates().get(3).memberId().id());
        Assert.assertEquals("node5", term.candidates().get(4).memberId().id());
        Assert.assertEquals("node6", term.candidates().get(5).memberId().id());
    }
}

