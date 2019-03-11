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
package org.apache.geode.internal.cache.tier.sockets.command;


import java.net.InetAddress;
import org.apache.geode.CancelCriterion;
import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.TransactionInDoubtException;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.distributed.internal.membership.MembershipManager;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXStateProxy;
import org.apache.geode.internal.cache.tier.ServerSideHandshake;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Exposes GEODE-537: NPE in JTA AFTER_COMPLETION command processing
 */
@Category({ ClientServerTest.class })
public class CommitCommandTest {
    /**
     * Test for GEODE-537 No NPE should be thrown from the
     * {@link CommitCommand#writeCommitResponse(org.apache.geode.internal.cache.TXCommitMessage, Message, ServerConnection)}
     * if the response message is null as it is the case when JTA transaction is rolled back with
     * TX_SYNCHRONIZATION AFTER_COMPLETION STATUS_ROLLEDBACK
     */
    @Test
    public void testWriteNullResponse() throws Exception {
        InternalCache cache = Mockito.mock(InternalCache.class);
        Message origMsg = Mockito.mock(Message.class);
        ServerConnection servConn = Mockito.mock(ServerConnection.class);
        Mockito.when(servConn.getResponseMessage()).thenReturn(Mockito.mock(Message.class));
        Mockito.when(servConn.getCache()).thenReturn(cache);
        Mockito.when(cache.getCancelCriterion()).thenReturn(Mockito.mock(CancelCriterion.class));
        CommitCommand.writeCommitResponse(null, origMsg, servConn);
    }

    /**
     * GEODE-5269 CommitConflictException after TransactionInDoubtException
     * CommitCommand needs to stall waiting for the host of a transaction to
     * finish shutting down before sending a TransactionInDoubtException to
     * the client.
     */
    @Test
    public void testTransactionInDoubtWaitsForTargetDeparture() throws Exception {
        CommitCommand command = ((CommitCommand) (CommitCommand.getCommand()));
        Message clientMessage = Mockito.mock(Message.class);
        ServerConnection serverConnection = Mockito.mock(ServerConnection.class);
        TXManagerImpl txMgr = Mockito.mock(TXManagerImpl.class);
        TXStateProxy txProxy = Mockito.mock(TXStateProxy.class);
        InternalCache cache = Mockito.mock(InternalCache.class);
        DistributionManager distributionManager = Mockito.mock(DistributionManager.class);
        MembershipManager membershipManager = Mockito.mock(MembershipManager.class);
        ServerSideHandshake handshake = Mockito.mock(ServerSideHandshake.class);
        boolean wasInProgress = false;
        Mockito.doReturn(cache).when(serverConnection).getCache();
        Mockito.doReturn(distributionManager).when(cache).getDistributionManager();
        Mockito.doReturn(membershipManager).when(distributionManager).getMembershipManager();
        Mockito.doReturn(false).when(distributionManager).isCurrentMember(ArgumentMatchers.isA(InternalDistributedMember.class));
        Mockito.doReturn(Mockito.mock(Message.class)).when(serverConnection).getErrorResponseMessage();
        Mockito.doReturn(handshake).when(serverConnection).getHandshake();
        Mockito.doReturn(1000).when(handshake).getClientReadTimeout();
        Mockito.doReturn(new InternalDistributedMember(InetAddress.getLocalHost(), 1234)).when(txProxy).getTarget();
        TransactionInDoubtException transactionInDoubtException = new TransactionInDoubtException("tx in doubt");
        transactionInDoubtException.initCause(new CacheClosedException("testing"));
        Mockito.doThrow(transactionInDoubtException).when(txMgr).commit();
        command.commitTransaction(clientMessage, serverConnection, txMgr, wasInProgress, txProxy);
        Mockito.verify(txMgr, Mockito.atLeastOnce()).commit();
        Mockito.verify(membershipManager, Mockito.times(1)).waitForDeparture(ArgumentMatchers.isA(DistributedMember.class), ArgumentMatchers.isA(Integer.class));
    }
}

