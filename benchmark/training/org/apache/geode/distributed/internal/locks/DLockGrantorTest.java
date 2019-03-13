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
package org.apache.geode.distributed.internal.locks;


import DLockRequestProcessor.DLockRequestMessage;
import java.util.concurrent.TimeUnit;
import org.apache.geode.cache.TransactionDataNodeHasDepartedException;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.Mockito;


public class DLockGrantorTest {
    private DLockService dLockService;

    private DistributionManager distributionManager;

    private DLockGrantor grantor;

    @Test
    public void handleLockBatchThrowsIfRequesterHasDeparted() {
        DLockLessorDepartureHandler handler = Mockito.mock(DLockLessorDepartureHandler.class);
        InternalDistributedMember requester = Mockito.mock(InternalDistributedMember.class);
        DLockRequestProcessor.DLockRequestMessage requestMessage = Mockito.mock(DLockRequestMessage.class);
        when(dLockService.getDLockLessorDepartureHandler()).thenReturn(handler);
        DLockBatch lockBatch = Mockito.mock(DLockBatch.class);
        when(requestMessage.getObjectName()).thenReturn(lockBatch);
        when(lockBatch.getOwner()).thenReturn(requester);
        grantor.makeReady(true);
        grantor.getLockBatches(requester);
        assertThatThrownBy(() -> grantor.handleLockBatch(requestMessage)).isInstanceOf(TransactionDataNodeHasDepartedException.class);
    }

    @Test
    public void recordMemberDepartedTimeRecords() {
        InternalDistributedMember owner = Mockito.mock(InternalDistributedMember.class);
        grantor.recordMemberDepartedTime(owner);
        assertThat(grantor.getMembersDepartedTimeRecords()).containsKey(owner);
    }

    @Test
    public void recordMemberDepartedTimeRemovesExpiredMembers() {
        DLockGrantor spy = Mockito.spy(grantor);
        long currentTime = System.currentTimeMillis();
        Mockito.doReturn(currentTime).doReturn(currentTime).doReturn(((currentTime + 1) + (TimeUnit.DAYS.toMillis(1)))).when(spy).getCurrentTime();
        for (int i = 0; i < 2; i++) {
            spy.recordMemberDepartedTime(Mockito.mock(InternalDistributedMember.class));
        }
        assertThat(spy.getMembersDepartedTimeRecords().size()).isEqualTo(2);
        InternalDistributedMember owner = Mockito.mock(InternalDistributedMember.class);
        spy.recordMemberDepartedTime(owner);
        assertThat(spy.getMembersDepartedTimeRecords().size()).isEqualTo(1);
        assertThat(spy.getMembersDepartedTimeRecords()).containsKey(owner);
    }
}

