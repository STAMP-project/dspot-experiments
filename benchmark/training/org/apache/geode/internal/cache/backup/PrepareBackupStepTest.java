/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache.backup;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.persistence.PersistentID;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.ReplyException;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.InternalCache;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class PrepareBackupStepTest {
    private DistributionManager dm;

    private InternalCache cache;

    private Set<InternalDistributedMember> recipients;

    private InternalDistributedMember sender;

    private InternalDistributedMember member1;

    private InternalDistributedMember member2;

    private PrepareBackupFactory prepareBackupFactory;

    private BackupReplyProcessor prepareBackupReplyProcessor;

    private PrepareBackupRequest prepareBackupRequest;

    private PrepareBackup prepareBackup;

    private PrepareBackupStep prepareBackupStep;

    @Test
    public void sendShouldSendPrepareBackupMessage() throws Exception {
        prepareBackupStep.send();
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(prepareBackupRequest);
    }

    @Test
    public void sendReturnsResultsForRemoteRecipient() throws Exception {
        HashSet<PersistentID> persistentIdsForMember1 = new HashSet<>();
        persistentIdsForMember1.add(Mockito.mock(PersistentID.class));
        Mockito.doAnswer(invokeAddToResults(new PrepareBackupStepTest.MemberWithPersistentIds(member1, persistentIdsForMember1))).when(prepareBackupReplyProcessor).waitForReplies();
        assertThat(prepareBackupStep.send()).containsOnlyKeys(member1).containsValues(persistentIdsForMember1);
    }

    @Test
    public void sendReturnsResultsForLocalMember() throws Exception {
        HashSet<PersistentID> persistentIdsForSender = new HashSet<>();
        persistentIdsForSender.add(Mockito.mock(PersistentID.class));
        Mockito.when(prepareBackup.run()).thenReturn(persistentIdsForSender);
        assertThat(prepareBackupStep.send()).containsOnlyKeys(sender).containsValue(persistentIdsForSender);
    }

    @Test
    public void sendReturnsResultsForAllMembers() throws Exception {
        HashSet<PersistentID> persistentIdsForMember1 = new HashSet<>();
        persistentIdsForMember1.add(Mockito.mock(PersistentID.class));
        HashSet<PersistentID> persistentIdsForMember2 = new HashSet<>();
        persistentIdsForMember2.add(Mockito.mock(PersistentID.class));
        PrepareBackupStepTest.MemberWithPersistentIds[] ids = new PrepareBackupStepTest.MemberWithPersistentIds[]{ new PrepareBackupStepTest.MemberWithPersistentIds(member1, persistentIdsForMember1), new PrepareBackupStepTest.MemberWithPersistentIds(member2, persistentIdsForMember2) };
        Mockito.doAnswer(invokeAddToResults(ids)).when(prepareBackupReplyProcessor).waitForReplies();
        // prepareBackupStep.addToResults(ids[0].member, ids[0].persistentIds);
        // prepareBackupStep.addToResults(ids[1].member, ids[1].persistentIds);
        HashSet<PersistentID> persistentIdsForSender = new HashSet<>();
        persistentIdsForSender.add(Mockito.mock(PersistentID.class));
        Mockito.when(prepareBackup.run()).thenReturn(persistentIdsForSender);
        assertThat(prepareBackupStep.send()).containsOnlyKeys(member1, member2, sender).containsValues(persistentIdsForSender, persistentIdsForMember1, persistentIdsForMember2);
    }

    @Test
    public void getResultsShouldReturnEmptyMapByDefault() throws Exception {
        assertThat(prepareBackupStep.getResults()).isEmpty();
    }

    @Test
    public void addToResultsWithNullShouldBeNoop() throws Exception {
        prepareBackupStep.addToResults(member1, null);
        assertThat(prepareBackupStep.getResults()).isEmpty();
    }

    @Test
    public void addToResultsWithEmptySetShouldBeNoop() throws Exception {
        prepareBackupStep.addToResults(member1, new HashSet());
        assertThat(prepareBackupStep.getResults()).isEmpty();
    }

    @Test
    public void addToResultsShouldShowUpInGetResults() throws Exception {
        HashSet<PersistentID> persistentIdsForMember1 = new HashSet<>();
        persistentIdsForMember1.add(Mockito.mock(PersistentID.class));
        prepareBackupStep.addToResults(member1, persistentIdsForMember1);
        assertThat(prepareBackupStep.getResults()).containsOnlyKeys(member1).containsValue(persistentIdsForMember1);
    }

    @Test
    public void sendShouldHandleIOExceptionThrownFromRun() throws Exception {
        Mockito.when(prepareBackup.run()).thenThrow(new IOException("expected exception"));
        prepareBackupStep.send();
    }

    @Test(expected = RuntimeException.class)
    public void sendShouldThrowNonIOExceptionThrownFromRun() throws Exception {
        Mockito.when(prepareBackup.run()).thenThrow(new RuntimeException("expected exception"));
        prepareBackupStep.send();
    }

    @Test
    public void sendShouldHandleCancelExceptionFromWaitForReplies() throws Exception {
        ReplyException replyException = new ReplyException("expected exception", new CacheClosedException("expected exception"));
        Mockito.doThrow(replyException).when(prepareBackupReplyProcessor).waitForReplies();
        prepareBackupStep.send();
    }

    @Test
    public void sendShouldHandleInterruptedExceptionFromWaitForReplies() throws Exception {
        Mockito.doThrow(new InterruptedException("expected exception")).when(prepareBackupReplyProcessor).waitForReplies();
        prepareBackupStep.send();
    }

    @Test(expected = ReplyException.class)
    public void sendShouldThrowReplyExceptionWithNoCauseFromWaitForReplies() throws Exception {
        Mockito.doThrow(new ReplyException("expected exception")).when(prepareBackupReplyProcessor).waitForReplies();
        prepareBackupStep.send();
    }

    @Test(expected = ReplyException.class)
    public void sendShouldThrowReplyExceptionWithCauseThatIsNotACancelFromWaitForReplies() throws Exception {
        Mockito.doThrow(new ReplyException("expected exception", new RuntimeException("expected"))).when(prepareBackupReplyProcessor).waitForReplies();
        prepareBackupStep.send();
    }

    @Test
    public void sendShouldPrepareForBackupInLocalMemberBeforeWaitingForReplies() throws Exception {
        InOrder inOrder = Mockito.inOrder(prepareBackup, prepareBackupReplyProcessor);
        prepareBackupStep.send();
        inOrder.verify(prepareBackup, Mockito.times(1)).run();
        inOrder.verify(prepareBackupReplyProcessor, Mockito.times(1)).waitForReplies();
    }

    private static class MemberWithPersistentIds {
        InternalDistributedMember member;

        HashSet<PersistentID> persistentIds;

        MemberWithPersistentIds(InternalDistributedMember member, HashSet<PersistentID> persistentIds) {
            this.member = member;
            this.persistentIds = persistentIds;
        }
    }
}

