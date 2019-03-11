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
package org.apache.geode.internal.cache.tx;


import org.apache.geode.CancelException;
import org.apache.geode.distributed.internal.InternalDistributedSystem;
import org.apache.geode.distributed.internal.ReplyException;
import org.apache.geode.distributed.internal.membership.InternalDistributedMember;
import org.apache.geode.internal.cache.GemFireCacheImpl;
import org.apache.geode.internal.cache.LocalRegion;
import org.apache.geode.internal.cache.RemoteOperationException;
import org.apache.geode.internal.cache.tx.RemoteOperationMessage.RemoteOperationResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class RemoteOperationResponseTest {
    private RemoteOperationResponse replyProcessor;// the class under test


    private InternalDistributedMember recipient;

    private final String regionPath = "regionPath";

    private GemFireCacheImpl cache;

    private InternalDistributedSystem system;

    private LocalRegion r;

    @Test
    public void whenMemberDepartedThatWeAreWaitingForExceptionIsSet() {
        replyProcessor.memberDeparted(system.getDistributionManager(), recipient, false);
        assertThat(replyProcessor.getMemberDepartedException()).hasMessageContaining("memberDeparted event");
    }

    @Test
    public void whenMemberDepartedThatWeAreNotWaitingForExceptionIsNotSet() {
        replyProcessor.memberDeparted(system.getDistributionManager(), Mockito.mock(InternalDistributedMember.class), false);
        assertThat(replyProcessor.getMemberDepartedException()).isNull();
    }

    @Test
    public void waitForRemoteResponseReturnsNormallyWhenWaitForRepliesUninterruptiblyDoesNothing() throws Exception {
        Mockito.doNothing().when(replyProcessor).waitForRepliesUninterruptibly();
        replyProcessor.waitForRemoteResponse();
        Mockito.verify(replyProcessor, Mockito.times(1)).waitForRepliesUninterruptibly();
    }

    @Test
    public void waitForRemoteResponseWithResponseRequiredThrowsException() throws Exception {
        Mockito.doNothing().when(replyProcessor).waitForRepliesUninterruptibly();
        replyProcessor.requireResponse();
        assertThatThrownBy(() -> replyProcessor.waitForRemoteResponse()).isInstanceOf(RemoteOperationException.class).hasMessage("response required but not received");
        Mockito.verify(replyProcessor, Mockito.times(1)).waitForRepliesUninterruptibly();
    }

    @Test
    public void waitForRemoteResponseWithMemberDepartedThrowsException() throws Exception {
        Mockito.doNothing().when(replyProcessor).waitForRepliesUninterruptibly();
        replyProcessor.memberDeparted(system.getDistributionManager(), recipient, false);
        assertThatThrownBy(() -> replyProcessor.waitForRemoteResponse()).isInstanceOf(RemoteOperationException.class).hasMessageContaining("memberDeparted event");
        Mockito.verify(replyProcessor, Mockito.times(1)).waitForRepliesUninterruptibly();
    }

    @Test
    public void waitForRemoteResponseWithReplyExceptionWithNoCauseCallsHandleCause() throws Exception {
        ReplyException replyException = Mockito.mock(ReplyException.class);
        Mockito.doThrow(replyException).when(replyProcessor).waitForRepliesUninterruptibly();
        replyProcessor.waitForRemoteResponse();
        Mockito.verify(replyException, Mockito.times(1)).handleCause();
    }

    @Test
    public void waitForRemoteResponseWithReplyExceptionWithUnhandledCauseCallsHandleCause() throws Exception {
        ReplyException replyException = Mockito.mock(ReplyException.class);
        RuntimeException cause = Mockito.mock(RuntimeException.class);
        Mockito.when(replyException.getCause()).thenReturn(cause);
        Mockito.doThrow(replyException).when(replyProcessor).waitForRepliesUninterruptibly();
        replyProcessor.waitForRemoteResponse();
        Mockito.verify(replyException, Mockito.times(1)).handleCause();
    }

    @Test
    public void waitForRemoteResponseWithReplyExceptionWithRemoteOperationExceptionCauseThrowsThatCause() throws Exception {
        ReplyException replyException = Mockito.mock(ReplyException.class);
        RemoteOperationException cause = new RemoteOperationException("msg");
        Mockito.when(replyException.getCause()).thenReturn(cause);
        Mockito.doThrow(replyException).when(replyProcessor).waitForRepliesUninterruptibly();
        assertThatThrownBy(() -> replyProcessor.waitForRemoteResponse()).isSameAs(cause);
    }

    @Test
    public void waitForRemoteResponseWithReplyExceptionWithCancelExceptionnCauseThrowsRemoteOperationException() throws Exception {
        ReplyException replyException = Mockito.mock(ReplyException.class);
        CancelException cause = Mockito.mock(CancelException.class);
        Mockito.when(replyException.getCause()).thenReturn(cause);
        Mockito.doThrow(replyException).when(replyProcessor).waitForRepliesUninterruptibly();
        assertThatThrownBy(() -> replyProcessor.waitForRemoteResponse()).isInstanceOf(RemoteOperationException.class).hasMessage("remote cache was closed");
    }
}

