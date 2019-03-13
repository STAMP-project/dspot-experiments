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
package org.apache.geode.internal.cache;


import TXCommitMessage.CommitProcessQueryMessage;
import TXCommitMessage.CommitProcessQueryReplyProcessor;
import java.util.HashSet;
import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.internal.DistributionManager;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.junit.Test;
import org.mockito.Mockito;


public class TXCommitMessageTest {
    @Test
    public void commitProcessQueryMessageIsSentIfHostDeparted() {
        DistributionManager manager = Mockito.mock(DistributionManager.class);
        InternalDistributedMember member = Mockito.mock(InternalDistributedMember.class);
        DistributionManager dm = Mockito.mock(DistributionManager.class);
        TXCommitMessage.CommitProcessQueryReplyProcessor processor = Mockito.mock(CommitProcessQueryReplyProcessor.class);
        TXCommitMessage.CommitProcessQueryMessage queryMessage = Mockito.mock(CommitProcessQueryMessage.class);
        HashSet farSiders = Mockito.mock(HashSet.class);
        TXCommitMessage message = Mockito.spy(new TXCommitMessage());
        Mockito.doReturn(dm).when(message).getDistributionManager();
        Mockito.when(dm.getCancelCriterion()).thenReturn(Mockito.mock(CancelCriterion.class));
        Mockito.doReturn(member).when(message).getSender();
        Mockito.doReturn(false).when(message).isProcessing();
        Mockito.doReturn(processor).when(message).createReplyProcessor();
        Mockito.doReturn(farSiders).when(message).getFarSiders();
        Mockito.doReturn(queryMessage).when(message).createQueryMessage(processor);
        Mockito.when(farSiders.isEmpty()).thenReturn(false);
        message.memberDeparted(manager, member, false);
        Mockito.verify(dm, Mockito.timeout(60000)).putOutgoing(queryMessage);
        Mockito.verify(processor, Mockito.timeout(60000)).waitForRepliesUninterruptibly();
    }
}

