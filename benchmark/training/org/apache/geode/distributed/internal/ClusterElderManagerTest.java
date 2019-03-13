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
package org.apache.geode.distributed.internal;


import ClusterDistributionManager.ADMIN_ONLY_DM_TYPE;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.internal.locks.ElderState;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.distributed.internal.membership.MembershipManager;
import org.apache.geode.test.junit.rules.ConcurrencyRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ClusterElderManagerTest {
    private MembershipManager memberManager;

    private CancelCriterion systemCancelCriterion;

    private InternalDistributedSystem system;

    private CancelCriterion cancelCriterion;

    private ClusterDistributionManager clusterDistributionManager;

    private InternalDistributedMember member0;

    private final InternalDistributedMember member1 = Mockito.mock(InternalDistributedMember.class);

    private final InternalDistributedMember member2 = Mockito.mock(InternalDistributedMember.class);

    @Rule
    public ConcurrencyRule concurrencyRule = new ConcurrencyRule();

    @Test
    public void getElderIdReturnsOldestMember() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member2));
        assertThat(clusterElderManager.getElderId()).isEqualTo(member1);
    }

    @Test
    public void getElderIdWithNoMembers() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList());
        assertThat(clusterElderManager.getElderId()).isNull();
    }

    @Test
    public void getElderIdIgnoresAdminMembers() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(member1.getVmKind()).thenReturn(ADMIN_ONLY_DM_TYPE);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member2));
        assertThat(clusterElderManager.getElderId()).isEqualTo(member2);
    }

    @Test
    public void getElderIdIgnoresSurpriseMembers() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(memberManager.isSurpriseMember(ArgumentMatchers.eq(member1))).thenReturn(true);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member2));
        assertThat(clusterElderManager.getElderId()).isEqualTo(member2);
    }

    @Test
    public void isElderIfOldestMember() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member0, member1));
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        assertThat(clusterElderManager.isElder()).isTrue();
    }

    @Test
    public void isNotElderIfOldestMember() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member0));
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        assertThat(clusterElderManager.isElder()).isFalse();
    }

    @Test
    public void waitForElderReturnsTrueIfAnotherMemberIsElder() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member0));
        assertThat(clusterElderManager.waitForElder(member1)).isTrue();
    }

    @Test
    public void waitForElderReturnsFalseIfWeAreElder() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.isCurrentMember(ArgumentMatchers.eq(member1))).thenReturn(true);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member0, member1));
        assertThat(clusterElderManager.waitForElder(member1)).isFalse();
    }

    @Test
    public void waitForElderReturnsFalseIfDesiredElderIsNotACurrentMember() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member2, member0, member1));
        assertThat(clusterElderManager.waitForElder(member1)).isFalse();
    }

    @Test
    public void waitForElderWaits() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member0));
        Mockito.when(clusterDistributionManager.isCurrentMember(ArgumentMatchers.eq(member0))).thenReturn(true);
        assertThatRunnableWaits(() -> clusterElderManager.waitForElder(member0));
    }

    @Test
    public void waitForElderStopsWaitingWhenUpdated() {
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.isCurrentMember(ArgumentMatchers.eq(member0))).thenReturn(true);
        AtomicReference<List<InternalDistributedMember>> currentMembers = new AtomicReference(Arrays.asList(member1, member0));
        Mockito.when(clusterDistributionManager.getViewMembers()).then(( invocation) -> currentMembers.get());
        AtomicReference<MembershipListener> membershipListener = new AtomicReference<>();
        Mockito.doAnswer(( invocation) -> {
            membershipListener.set(invocation.getArgument(0));
            return null;
        }).when(clusterDistributionManager).addMembershipListener(ArgumentMatchers.any());
        Callable<Boolean> waitForElder = () -> clusterElderManager.waitForElder(member0);
        Callable<Void> updateMembershipView = () -> {
            // Wait for membership listener to be added
            await().until(() -> (membershipListener.get()) != null);
            currentMembers.set(Arrays.asList(member0));
            membershipListener.get().memberDeparted(clusterDistributionManager, member1, true);
            return null;
        };
        concurrencyRule.add(waitForElder).expectValue(true);
        concurrencyRule.add(updateMembershipView);
        concurrencyRule.executeInParallel();
        assertThat(clusterElderManager.getElderId()).isEqualTo(member0);
    }

    @Test
    public void getElderStateAsElder() {
        Supplier<ElderState> elderStateSupplier = Mockito.mock(Supplier.class);
        ElderState elderState = Mockito.mock(ElderState.class);
        Mockito.when(elderStateSupplier.get()).thenReturn(elderState);
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager, elderStateSupplier);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member0, member1));
        assertThat(clusterElderManager.getElderState(false)).isEqualTo(elderState);
        Mockito.verify(elderStateSupplier, Mockito.times(1)).get();
    }

    @Test
    public void getElderStateGetsBuiltOnceAsElder() {
        Supplier<ElderState> elderStateSupplier = Mockito.mock(Supplier.class);
        ElderState elderState = Mockito.mock(ElderState.class);
        Mockito.when(elderStateSupplier.get()).thenReturn(elderState);
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager, elderStateSupplier);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member0, member1));
        assertThat(clusterElderManager.getElderState(false)).isEqualTo(elderState);
        assertThat(clusterElderManager.getElderState(false)).isEqualTo(elderState);
        // Make sure that we only create the elder state once
        Mockito.verify(elderStateSupplier, Mockito.times(1)).get();
    }

    @Test
    public void getElderStateFromMultipleThreadsAsElder() {
        Supplier<ElderState> elderStateSupplier = Mockito.mock(Supplier.class);
        ElderState elderState = Mockito.mock(ElderState.class);
        Mockito.when(elderStateSupplier.get()).thenReturn(elderState);
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager, elderStateSupplier);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member0, member1));
        Callable<ElderState> callable = () -> clusterElderManager.getElderState(false);
        concurrencyRule.add(callable).expectValue(elderState);
        concurrencyRule.add(callable).expectValue(elderState);
        concurrencyRule.executeInParallel();
        // Make sure that we only create the elder state once
        Mockito.verify(elderStateSupplier, Mockito.times(1)).get();
    }

    @Test
    public void getElderStateNotAsElder() {
        Supplier<ElderState> elderStateSupplier = Mockito.mock(Supplier.class);
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager, elderStateSupplier);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member0));
        assertThat(clusterElderManager.getElderState(false)).isEqualTo(null);
        Mockito.verify(elderStateSupplier, Mockito.times(0)).get();
    }

    @Test
    public void getElderStateWaitsToBecomeElder() {
        Supplier<ElderState> elderStateSupplier = Mockito.mock(Supplier.class);
        ClusterElderManager clusterElderManager = new ClusterElderManager(clusterDistributionManager, elderStateSupplier);
        Mockito.when(clusterDistributionManager.getId()).thenReturn(member0);
        Mockito.when(clusterDistributionManager.getViewMembers()).thenReturn(Arrays.asList(member1, member0));
        Mockito.when(clusterDistributionManager.isCurrentMember(ArgumentMatchers.eq(member0))).thenReturn(true);
        assertThatRunnableWaits(() -> clusterElderManager.getElderState(true));
        Mockito.verify(elderStateSupplier, Mockito.times(0)).get();
    }
}

