/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.rocketmq.broker.transaction.queue;


import MessageSysFlag.TRANSACTION_COMMIT_TYPE;
import MessageSysFlag.TRANSACTION_ROLLBACK_TYPE;
import MixAll.RMQ_SYS_TRANS_HALF_TOPIC;
import MixAll.RMQ_SYS_TRANS_OP_HALF_TOPIC;
import ResponseCode.SUCCESS;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.transaction.AbstractTransactionalMessageCheckListener;
import org.apache.rocketmq.broker.transaction.OperationResult;
import org.apache.rocketmq.broker.transaction.TransactionalMessageService;
import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.apache.rocketmq.store.AppendMessageStatus;
import org.apache.rocketmq.store.MessageExtBrokerInner;
import org.apache.rocketmq.store.PutMessageResult;
import org.apache.rocketmq.store.PutMessageStatus;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class TransactionalMessageServiceImplTest {
    private TransactionalMessageService queueTransactionMsgService;

    @Mock
    private TransactionalMessageBridge bridge;

    @Spy
    private BrokerController brokerController = new BrokerController(new BrokerConfig(), new NettyServerConfig(), new NettyClientConfig(), new MessageStoreConfig());

    @Mock
    private AbstractTransactionalMessageCheckListener listener;

    @Test
    public void testPrepareMessage() {
        MessageExtBrokerInner inner = createMessageBrokerInner();
        Mockito.when(bridge.putHalfMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        PutMessageResult result = queueTransactionMsgService.prepareMessage(inner);
        assert result.isOk();
    }

    @Test
    public void testCommitMessage() {
        Mockito.when(bridge.lookMessageByOffset(ArgumentMatchers.anyLong())).thenReturn(createMessageBrokerInner());
        OperationResult result = queueTransactionMsgService.commitMessage(createEndTransactionRequestHeader(TRANSACTION_COMMIT_TYPE));
        assertThat(result.getResponseCode()).isEqualTo(SUCCESS);
    }

    @Test
    public void testRollbackMessage() {
        Mockito.when(bridge.lookMessageByOffset(ArgumentMatchers.anyLong())).thenReturn(createMessageBrokerInner());
        OperationResult result = queueTransactionMsgService.commitMessage(createEndTransactionRequestHeader(TRANSACTION_ROLLBACK_TYPE));
        assertThat(result.getResponseCode()).isEqualTo(SUCCESS);
    }

    @Test
    public void testCheck_withDiscard() {
        Mockito.when(bridge.fetchMessageQueues(RMQ_SYS_TRANS_HALF_TOPIC)).thenReturn(createMessageQueueSet(RMQ_SYS_TRANS_HALF_TOPIC));
        Mockito.when(bridge.getHalfMessage(0, 0, 1)).thenReturn(createDiscardPullResult(RMQ_SYS_TRANS_HALF_TOPIC, 5, "hellp", 1));
        Mockito.when(bridge.getHalfMessage(0, 1, 1)).thenReturn(createPullResult(RMQ_SYS_TRANS_HALF_TOPIC, 6, "hellp", 0));
        Mockito.when(bridge.getOpMessage(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(createOpPulResult(RMQ_SYS_TRANS_OP_HALF_TOPIC, 1, "10", 1));
        long timeOut = this.brokerController.getBrokerConfig().getTransactionTimeOut();
        int checkMax = this.brokerController.getBrokerConfig().getTransactionCheckMax();
        final AtomicInteger checkMessage = new AtomicInteger(0);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                checkMessage.addAndGet(1);
                return null;
            }
        }).when(listener).resolveDiscardMsg(ArgumentMatchers.any(MessageExt.class));
        queueTransactionMsgService.check(timeOut, checkMax, listener);
        assertThat(checkMessage.get()).isEqualTo(1);
    }

    @Test
    public void testCheck_withCheck() {
        Mockito.when(bridge.fetchMessageQueues(RMQ_SYS_TRANS_HALF_TOPIC)).thenReturn(createMessageQueueSet(RMQ_SYS_TRANS_HALF_TOPIC));
        Mockito.when(bridge.getHalfMessage(0, 0, 1)).thenReturn(createPullResult(RMQ_SYS_TRANS_HALF_TOPIC, 5, "hello", 1));
        Mockito.when(bridge.getHalfMessage(0, 1, 1)).thenReturn(createPullResult(RMQ_SYS_TRANS_HALF_TOPIC, 6, "hellp", 0));
        Mockito.when(bridge.getOpMessage(ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt())).thenReturn(createPullResult(RMQ_SYS_TRANS_OP_HALF_TOPIC, 1, "5", 0));
        Mockito.when(bridge.getBrokerController()).thenReturn(this.brokerController);
        Mockito.when(bridge.renewHalfMessageInner(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(createMessageBrokerInner());
        Mockito.when(bridge.putMessageReturnResult(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        long timeOut = this.brokerController.getBrokerConfig().getTransactionTimeOut();
        final int checkMax = this.brokerController.getBrokerConfig().getTransactionCheckMax();
        final AtomicInteger checkMessage = new AtomicInteger(0);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                checkMessage.addAndGet(1);
                return checkMessage;
            }
        }).when(listener).resolveHalfMsg(ArgumentMatchers.any(MessageExt.class));
        queueTransactionMsgService.check(timeOut, checkMax, listener);
        assertThat(checkMessage.get()).isEqualTo(1);
    }

    @Test
    public void testDeletePrepareMessage() {
        Mockito.when(bridge.putOpMessage(ArgumentMatchers.any(MessageExt.class), ArgumentMatchers.anyString())).thenReturn(true);
        boolean res = queueTransactionMsgService.deletePrepareMessage(createMessageBrokerInner());
        assertThat(res).isTrue();
    }

    @Test
    public void testOpen() {
        boolean isOpen = queueTransactionMsgService.open();
        assertThat(isOpen).isTrue();
    }
}

