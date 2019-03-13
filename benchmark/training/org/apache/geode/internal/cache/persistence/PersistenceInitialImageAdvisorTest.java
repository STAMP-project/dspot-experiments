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
package org.apache.geode.internal.cache.persistence;


import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.persistence.ConflictingPersistentDataException;
import org.apache.geode.distributed.internal.ReplyException;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.CacheDistributionAdvisor;
import org.apache.geode.internal.cache.CacheDistributionAdvisor.InitialImageAdvice;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class PersistenceInitialImageAdvisorTest {
    @Rule
    public RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    private InternalPersistenceAdvisor persistenceAdvisor;

    private CacheDistributionAdvisor cacheDistributionAdvisor = Mockito.mock(CacheDistributionAdvisor.class, Mockito.RETURNS_DEEP_STUBS);

    private PersistenceInitialImageAdvisor persistenceInitialImageAdvisor;

    @Test
    public void clearsEqualMembers_ifHasDiskImageAndAllReplicatesAreUnequal() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceFromCacheDistributionAdvisor = PersistenceInitialImageAdvisorTest.adviceWithReplicates(3);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(adviceFromCacheDistributionAdvisor);
        persistenceInitialImageAdvisor.getAdvice(null);
        Mockito.verify(persistenceAdvisor, Mockito.times(1)).clearEqualMembers();
    }

    @Test
    public void adviceIncludesAllReplicates_ifHasDiskImageAndAllReplicatesAreUnequal() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceFromCacheDistributionAdvisor = PersistenceInitialImageAdvisorTest.adviceWithReplicates(3);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(adviceFromCacheDistributionAdvisor);
        InitialImageAdvice result = persistenceInitialImageAdvisor.getAdvice(null);
        assertThat(result.getReplicates()).isEqualTo(adviceFromCacheDistributionAdvisor.getReplicates());
    }

    @Test
    public void adviceIncludesNoReplicates_ifHasDiskImageAndAnyReplicateIsEqual() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(PersistenceInitialImageAdvisorTest.adviceWithReplicates(9));
        Mockito.when(persistenceAdvisor.checkMyStateOnMembers(ArgumentMatchers.any())).thenReturn(true);
        InitialImageAdvice result = persistenceInitialImageAdvisor.getAdvice(null);
        assertThat(result.getReplicates()).isEmpty();
    }

    @Test
    public void adviceIncludesAllReplicates_ifReplicatesAndNoDiskImage() {
        boolean hasDiskImage = false;
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisor(hasDiskImage);
        InitialImageAdvice adviceFromCacheDistributionAdvisor = PersistenceInitialImageAdvisorTest.adviceWithReplicates(9);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(adviceFromCacheDistributionAdvisor);
        InitialImageAdvice result = persistenceInitialImageAdvisor.getAdvice(null);
        assertThat(result.getReplicates()).isEqualTo(adviceFromCacheDistributionAdvisor.getReplicates());
    }

    @Test
    public void obtainsFreshAdvice_ifAdviceIncludesNoReplicatesAndPreviousAdviceHasReplicates() {
        InitialImageAdvice previousAdviceWithReplicates = PersistenceInitialImageAdvisorTest.adviceWithReplicates(1);
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNotNull(), ArgumentMatchers.anyBoolean())).thenReturn(PersistenceInitialImageAdvisorTest.adviceWithReplicates(0));
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(PersistenceInitialImageAdvisorTest.adviceWithReplicates(1));
        persistenceInitialImageAdvisor.getAdvice(previousAdviceWithReplicates);
        InOrder inOrder = Mockito.inOrder(cacheDistributionAdvisor);
        inOrder.verify(cacheDistributionAdvisor, Mockito.times(1)).adviseInitialImage(ArgumentMatchers.same(previousAdviceWithReplicates), ArgumentMatchers.anyBoolean());
        inOrder.verify(cacheDistributionAdvisor, Mockito.times(1)).adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean());
        inOrder.verify(cacheDistributionAdvisor, Mockito.times(0)).adviseInitialImage(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test
    public void attemptsToUpdateMembershipViewFromEachNonPersistentReplicate_ifAllReplicatesAreNonPersistent() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceWithNonPersistentReplicates = PersistenceInitialImageAdvisorTest.adviceWithNonPersistentReplicates(4);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(adviceWithNonPersistentReplicates, PersistenceInitialImageAdvisorTest.adviceWithReplicates(1));
        // Make every attempt fail, forcing the advisor to try them all.
        Mockito.doThrow(ReplyException.class).when(persistenceAdvisor).updateMembershipView(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        persistenceInitialImageAdvisor.getAdvice(null);
        for (InternalDistributedMember peer : adviceWithNonPersistentReplicates.getNonPersistent()) {
            Mockito.verify(persistenceAdvisor, Mockito.times(1)).updateMembershipView(peer, true);
        }
    }

    @Test
    public void updatesMembershipViewFromFirstNonPersistentReplicateThatReplies() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceWithNonPersistentReplicates = PersistenceInitialImageAdvisorTest.adviceWithNonPersistentReplicates(4);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(adviceWithNonPersistentReplicates, PersistenceInitialImageAdvisorTest.adviceWithReplicates(1));
        // Then return without throwing
        // Throw on first call
        Mockito.doThrow(ReplyException.class).doNothing().when(persistenceAdvisor).updateMembershipView(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
        persistenceInitialImageAdvisor.getAdvice(null);
        // The second call succeeds. Expect no further calls.
        Mockito.verify(persistenceAdvisor, Mockito.times(2)).updateMembershipView(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean());
    }

    @Test(expected = CacheClosedException.class)
    public void propagatesException_ifCancelInProgress() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        CancelCriterion cancelCriterion = Mockito.mock(CancelCriterion.class);
        Mockito.when(cacheDistributionAdvisor.getAdvisee().getCancelCriterion()).thenReturn(cancelCriterion);
        Mockito.doThrow(new CacheClosedException()).when(cancelCriterion).checkCancelInProgress(ArgumentMatchers.any());
        persistenceInitialImageAdvisor.getAdvice(null);
    }

    @Test
    public void returnsAdviceFromCacheDistributionAdvisor_ifNoOnlineOrPreviouslyOnlineMembers() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceFromCacheDistributionAdvisor = new InitialImageAdvice();
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.isNull(), ArgumentMatchers.anyBoolean())).thenReturn(adviceFromCacheDistributionAdvisor);
        Mockito.when(persistenceAdvisor.getPersistedOnlineOrEqualMembers()).thenReturn(new HashSet());
        InitialImageAdvice result = persistenceInitialImageAdvisor.getAdvice(null);
        assertThat(result).isSameAs(adviceFromCacheDistributionAdvisor);
    }

    @Test
    public void adviceIncludesReplicatesThatAppearWhileAcquiringTieLock() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceBeforeAcquiringTieLock = new InitialImageAdvice();
        InitialImageAdvice adviceAfterAcquiringTieLock = PersistenceInitialImageAdvisorTest.adviceWithReplicates(4);
        Mockito.when(persistenceAdvisor.getPersistedOnlineOrEqualMembers()).thenReturn(PersistenceInitialImageAdvisorTest.persistentMemberIDs(1));
        Mockito.when(persistenceAdvisor.getMembersToWaitFor(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Collections.emptySet());
        Mockito.when(persistenceAdvisor.acquireTieLock()).thenReturn(true);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(adviceBeforeAcquiringTieLock, adviceAfterAcquiringTieLock);
        InitialImageAdvice result = persistenceInitialImageAdvisor.getAdvice(null);
        assertThat(result.getReplicates()).isEqualTo(adviceAfterAcquiringTieLock.getReplicates());
    }

    @Test(expected = ConflictingPersistentDataException.class)
    public void propagatesException_ifIncompatibleWithReplicateThatAppearsWhileAcquiringTieLock() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        InitialImageAdvice adviceBeforeAcquiringTieLock = new InitialImageAdvice();
        InitialImageAdvice adviceAfterAcquiringTieLock = PersistenceInitialImageAdvisorTest.adviceWithReplicates(4);
        Mockito.when(persistenceAdvisor.getPersistedOnlineOrEqualMembers()).thenReturn(PersistenceInitialImageAdvisorTest.persistentMemberIDs(1));
        Mockito.when(persistenceAdvisor.getMembersToWaitFor(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(Collections.emptySet());
        Mockito.when(persistenceAdvisor.acquireTieLock()).thenReturn(true);
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(ArgumentMatchers.any(), ArgumentMatchers.anyBoolean())).thenReturn(adviceBeforeAcquiringTieLock, adviceAfterAcquiringTieLock);
        Mockito.doThrow(ConflictingPersistentDataException.class).when(persistenceAdvisor).checkMyStateOnMembers(ArgumentMatchers.any());
        persistenceInitialImageAdvisor.getAdvice(null);
    }

    @Test
    public void announcesProgressToPersistenceAdvisor_whenWaitingForMissingMembers() {
        persistenceInitialImageAdvisor = persistenceInitialImageAdvisorWithDiskImage();
        PersistenceInitialImageAdvisorTest.setMembershipChangePollDuration(Duration.ofSeconds(0));
        HashSet<PersistentMemberID> offlineMembersToWaitFor = PersistenceInitialImageAdvisorTest.memberIDs("offline member", 1);
        Set<PersistentMemberID> membersToWaitFor = new HashSet(offlineMembersToWaitFor);
        Mockito.when(persistenceAdvisor.getPersistedOnlineOrEqualMembers()).thenReturn(offlineMembersToWaitFor);
        Mockito.when(persistenceAdvisor.getMembersToWaitFor(ArgumentMatchers.any(), ArgumentMatchers.any())).thenAnswer(( invocation) -> {
            Set<PersistentMemberID> offlineMembers = invocation.getArgument(1);
            offlineMembers.addAll(offlineMembersToWaitFor);
            return membersToWaitFor;
        });
        Mockito.when(cacheDistributionAdvisor.adviseInitialImage(null, true)).thenReturn(PersistenceInitialImageAdvisorTest.adviceWithReplicates(0), PersistenceInitialImageAdvisorTest.adviceWithReplicates(1));
        persistenceInitialImageAdvisor.getAdvice(null);
        InOrder inOrder = Mockito.inOrder(persistenceAdvisor);
        inOrder.verify(persistenceAdvisor, Mockito.times(1)).beginWaitingForMembershipChange(membersToWaitFor);
        inOrder.verify(persistenceAdvisor, Mockito.times(1)).setWaitingOnMembers(ArgumentMatchers.isNotNull(), ArgumentMatchers.eq(offlineMembersToWaitFor));
        inOrder.verify(persistenceAdvisor, Mockito.times(1)).endWaitingForMembershipChange();
        inOrder.verify(persistenceAdvisor, Mockito.times(1)).setWaitingOnMembers(ArgumentMatchers.isNull(), ArgumentMatchers.isNull());
        inOrder.verify(persistenceAdvisor, Mockito.times(0)).setWaitingOnMembers(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}

