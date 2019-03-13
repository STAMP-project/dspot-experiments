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
package org.apache.rocketmq.broker.processor;


import MessageSysFlag.TRANSACTION_COMMIT_TYPE;
import MessageSysFlag.TRANSACTION_NOT_TYPE;
import MessageSysFlag.TRANSACTION_ROLLBACK_TYPE;
import ResponseCode.SUCCESS;
import io.netty.channel.ChannelHandlerContext;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.transaction.TransactionalMessageService;
import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.common.protocol.header.EndTransactionRequestHeader;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.store.AppendMessageStatus;
import org.apache.rocketmq.store.MessageExtBrokerInner;
import org.apache.rocketmq.store.MessageStore;
import org.apache.rocketmq.store.PutMessageStatus;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EndTransactionProcessorTest {
    private EndTransactionProcessor endTransactionProcessor;

    @Mock
    private ChannelHandlerContext handlerContext;

    @Spy
    private BrokerController brokerController = new BrokerController(new BrokerConfig(), new NettyServerConfig(), new NettyClientConfig(), new MessageStoreConfig());

    @Mock
    private MessageStore messageStore;

    @Mock
    private TransactionalMessageService transactionMsgService;

    @Test
    public void testProcessRequest() throws RemotingCommandException {
        Mockito.when(transactionMsgService.commitMessage(ArgumentMatchers.any(EndTransactionRequestHeader.class))).thenReturn(createResponse(SUCCESS));
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        RemotingCommand request = createEndTransactionMsgCommand(TRANSACTION_COMMIT_TYPE, false);
        RemotingCommand response = endTransactionProcessor.processRequest(handlerContext, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
    }

    @Test
    public void testProcessRequest_CheckMessage() throws RemotingCommandException {
        Mockito.when(transactionMsgService.commitMessage(ArgumentMatchers.any(EndTransactionRequestHeader.class))).thenReturn(createResponse(SUCCESS));
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        RemotingCommand request = createEndTransactionMsgCommand(TRANSACTION_COMMIT_TYPE, true);
        RemotingCommand response = endTransactionProcessor.processRequest(handlerContext, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
    }

    @Test
    public void testProcessRequest_NotType() throws RemotingCommandException {
        RemotingCommand request = createEndTransactionMsgCommand(TRANSACTION_NOT_TYPE, true);
        RemotingCommand response = endTransactionProcessor.processRequest(handlerContext, request);
        assertThat(response).isNull();
    }

    @Test
    public void testProcessRequest_RollBack() throws RemotingCommandException {
        Mockito.when(transactionMsgService.rollbackMessage(ArgumentMatchers.any(EndTransactionRequestHeader.class))).thenReturn(createResponse(SUCCESS));
        RemotingCommand request = createEndTransactionMsgCommand(TRANSACTION_ROLLBACK_TYPE, true);
        RemotingCommand response = endTransactionProcessor.processRequest(handlerContext, request);
        assertThat(response.getCode()).isEqualTo(SUCCESS);
    }
}

