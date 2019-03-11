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


import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.FindRemoteTXMessage.FindRemoteTXMessageReplyProcessor;
import org.apache.geode.internal.cache.InternalCache;
import org.apache.geode.internal.cache.TXId;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXStateProxyImpl;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.apache.geode.test.junit.categories.ClientServerTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;


@Category({ ClientServerTest.class })
public class TXFailoverCommandTest {
    @Test
    public void testTXFailoverSettingTargetNode() throws Exception {
        ClientProxyMembershipID clientProxyMembershipID = Mockito.mock(ClientProxyMembershipID.class);
        FindRemoteTXMessageReplyProcessor processor = Mockito.mock(FindRemoteTXMessageReplyProcessor.class);
        InternalCache cache = Mockito.mock(InternalCache.class);
        InternalDistributedMember client = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedMember host = Mockito.mock(InternalDistributedMember.class);
        InternalDistributedSystem system = Mockito.mock(InternalDistributedSystem.class);
        Message message = Mockito.mock(Message.class);
        ServerConnection serverConnection = Mockito.mock(ServerConnection.class);
        TXManagerImpl txManager = Mockito.mock(TXManagerImpl.class);
        int uniqueId = 1;
        TXId txId = new TXId(client, uniqueId);
        TXStateProxyImpl proxy = new TXStateProxyImpl(cache, txManager, txId, null);
        Mockito.when(cache.getCacheTransactionManager()).thenReturn(txManager);
        Mockito.when(cache.getCancelCriterion()).thenReturn(Mockito.mock(CancelCriterion.class));
        Mockito.when(cache.getDistributedSystem()).thenReturn(system);
        Mockito.when(clientProxyMembershipID.getDistributedMember()).thenReturn(client);
        Mockito.when(message.getTransactionId()).thenReturn(uniqueId);
        Mockito.when(processor.getHostingMember()).thenReturn(host);
        Mockito.when(serverConnection.getProxyID()).thenReturn(clientProxyMembershipID);
        Mockito.when(serverConnection.getCache()).thenReturn(cache);
        Mockito.when(txManager.getTXState()).thenReturn(proxy);
        Mockito.when(serverConnection.getReplyMessage()).thenReturn(Mockito.mock(Message.class));
        TXFailoverCommand command = Mockito.spy(new TXFailoverCommand());
        Mockito.doReturn(txId).when(command).createTXId(client, uniqueId);
        Mockito.doReturn(processor).when(command).sendFindRemoteTXMessage(cache, txId);
        command.cmdExecute(message, serverConnection, null, 1);
        Assert.assertNotNull(proxy.getRealDeal(host));
        Assert.assertEquals(proxy.getTarget(), host);
    }
}

