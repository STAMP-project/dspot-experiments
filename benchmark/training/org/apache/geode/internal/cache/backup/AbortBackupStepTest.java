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


import java.util.HashSet;
import java.util.Set;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.persistence.PersistentID;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.ReplyException;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class AbortBackupStepTest {
    private DistributionManager dm;

    private InternalDistributedMember sender;

    private InternalDistributedMember member1;

    private InternalDistributedMember member2;

    private BackupReplyProcessor backupReplyProcessor;

    private AbortBackupRequest abortBackupRequest;

    private AbortBackup abortBackup;

    private AbortBackupStep abortBackupStep;

    @Test
    public void sendShouldSendAbortBackupMessage() {
        abortBackupStep.send();
        Mockito.verify(dm, Mockito.times(1)).putOutgoing(abortBackupRequest);
    }

    @Test
    public void sendReturnsResultsForLocalMember() {
        assertThat(abortBackupStep.send()).containsOnlyKeys(sender);
    }

    @Test
    public void sendReturnsResultsForAllMembers() throws Exception {
        AbortBackupStepTest.MemberWithPersistentIds[] ids = new AbortBackupStepTest.MemberWithPersistentIds[]{ createMemberWithPersistentIds(member1), createMemberWithPersistentIds(member2) };
        Mockito.doAnswer(invokeAddToResults(ids)).when(backupReplyProcessor).waitForReplies();
        assertThat(abortBackupStep.send()).containsOnlyKeys(member1, member2, sender);
    }

    @Test
    public void getResultsShouldReturnEmptyMapByDefault() {
        assertThat(abortBackupStep.getResults()).isEmpty();
    }

    @Test
    public void addToResultsWithNullShouldBeNoop() {
        abortBackupStep.addToResults(member1, null);
        assertThat(abortBackupStep.getResults()).isEmpty();
    }

    @Test
    public void addToResultsShouldShowUpInGetResults() {
        abortBackupStep.addToResults(member1, createPersistentIds());
        assertThat(abortBackupStep.getResults()).containsOnlyKeys(member1);
    }

    @Test
    public void sendShouldHandleCancelExceptionFromWaitForReplies() throws Exception {
        ReplyException replyException = new ReplyException("expected exception", new CacheClosedException("expected exception"));
        Mockito.doThrow(replyException).when(backupReplyProcessor).waitForReplies();
        abortBackupStep.send();
    }

    @Test
    public void sendShouldHandleInterruptedExceptionFromWaitForReplies() throws Exception {
        Mockito.doThrow(new InterruptedException("expected exception")).when(backupReplyProcessor).waitForReplies();
        abortBackupStep.send();
    }

    @Test(expected = ReplyException.class)
    public void sendShouldThrowReplyExceptionWithNoCauseFromWaitForReplies() throws Exception {
        Mockito.doThrow(new ReplyException("expected exception")).when(backupReplyProcessor).waitForReplies();
        abortBackupStep.send();
    }

    @Test(expected = ReplyException.class)
    public void sendShouldThrowReplyExceptionWithCauseThatIsNotACancelFromWaitForReplies() throws Exception {
        Mockito.doThrow(new ReplyException("expected exception", new RuntimeException("expected"))).when(backupReplyProcessor).waitForReplies();
        abortBackupStep.send();
    }

    @Test
    public void sendShouldAbortBackupInLocalMemberBeforeWaitingForReplies() throws Exception {
        InOrder inOrder = Mockito.inOrder(abortBackup, backupReplyProcessor);
        abortBackupStep.send();
        inOrder.verify(abortBackup, Mockito.times(1)).run();
        inOrder.verify(backupReplyProcessor, Mockito.times(1)).waitForReplies();
    }

    private static class MemberWithPersistentIds {
        InternalDistributedMember member;

        Set<PersistentID> persistentIds;

        MemberWithPersistentIds(InternalDistributedMember member, HashSet<PersistentID> persistentIds) {
            this.member = member;
            this.persistentIds = persistentIds;
        }
    }
}

