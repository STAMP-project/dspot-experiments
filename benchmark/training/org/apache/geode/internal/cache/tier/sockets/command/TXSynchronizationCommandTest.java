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


import Command.RESPONDED;
import Status.STATUS_COMMITTED;
import TXSynchronizationOp.CompletionType.AFTER_COMPLETION;
import TXSynchronizationOp.CompletionType.BEFORE_COMPLETION;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.TXCommitMessage;
import org.apache.geode.internal.cache.TXId;
import org.apache.geode.internal.cache.TXManagerImpl;
import org.apache.geode.internal.cache.TXStateProxyImpl;
import org.apache.geode.internal.cache.tier.sockets.Message;
import org.apache.geode.internal.cache.tier.sockets.Part;
import org.apache.geode.internal.cache.tier.sockets.ServerConnection;
import org.junit.Test;
import org.mockito.Mockito;


public class TXSynchronizationCommandTest {
    private Message clientMessage;

    private ServerConnection serverConnection;

    private TXManagerImpl txManager;

    private TXStateProxyImpl txStateProxy;

    private TXId txId;

    private TXCommitMessage txCommitMessage;

    private InternalDistributedMember member;

    private Part part0;

    private Part part1;

    private Part part2;

    private RuntimeException exception;

    private TXSynchronizationCommand command;

    @Test
    public void commandCanSendBackCommitMessageIfAlreadyCommitted() throws Exception {
        Mockito.when(part0.getInt()).thenReturn(AFTER_COMPLETION.ordinal());
        Mockito.when(txManager.getRecentlyCompletedMessage(txId)).thenReturn(txCommitMessage);
        Mockito.doNothing().when(command).writeCommitResponse(clientMessage, serverConnection, txCommitMessage);
        Mockito.doCallRealMethod().when(command).cmdExecute(clientMessage, serverConnection, null, 1);
        command.cmdExecute(clientMessage, serverConnection, null, 1);
        Mockito.verify(command, Mockito.times(1)).writeCommitResponse(clientMessage, serverConnection, txCommitMessage);
        Mockito.verify(serverConnection, Mockito.times(1)).setAsTrue(RESPONDED);
    }

    @Test
    public void commandCanInvokeBeforeCompletion() throws Exception {
        Mockito.when(part0.getInt()).thenReturn(BEFORE_COMPLETION.ordinal());
        Mockito.doCallRealMethod().when(command).cmdExecute(clientMessage, serverConnection, null, 1);
        command.cmdExecute(clientMessage, serverConnection, null, 1);
        Mockito.verify(txStateProxy, Mockito.times(1)).beforeCompletion();
        Mockito.verify(serverConnection, Mockito.times(1)).setAsTrue(RESPONDED);
    }

    @Test
    public void commandCanSendBackCommitMessageAfterInvokeAfterCompletion() throws Exception {
        Mockito.when(part0.getInt()).thenReturn(AFTER_COMPLETION.ordinal());
        Mockito.when(part2.getInt()).thenReturn(STATUS_COMMITTED);
        Mockito.when(txStateProxy.getCommitMessage()).thenReturn(txCommitMessage);
        Mockito.doCallRealMethod().when(command).cmdExecute(clientMessage, serverConnection, null, 1);
        command.cmdExecute(clientMessage, serverConnection, null, 1);
        Mockito.verify(txStateProxy, Mockito.times(1)).afterCompletion(STATUS_COMMITTED);
        Mockito.verify(command, Mockito.times(1)).writeCommitResponse(clientMessage, serverConnection, txCommitMessage);
        Mockito.verify(serverConnection, Mockito.times(1)).setAsTrue(RESPONDED);
    }
}

