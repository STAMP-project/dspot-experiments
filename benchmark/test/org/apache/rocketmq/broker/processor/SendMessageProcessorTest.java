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


import RequestCode.CONSUMER_SEND_MSG_BACK;
import RequestCode.SEND_MESSAGE;
import ResponseCode.FLUSH_DISK_TIMEOUT;
import ResponseCode.FLUSH_SLAVE_TIMEOUT;
import ResponseCode.MESSAGE_ILLEGAL;
import ResponseCode.SERVICE_NOT_AVAILABLE;
import ResponseCode.SLAVE_NOT_AVAILABLE;
import ResponseCode.SUCCESS;
import ResponseCode.SYSTEM_ERROR;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.mqtrace.SendMessageContext;
import org.apache.rocketmq.broker.mqtrace.SendMessageHook;
import org.apache.rocketmq.broker.transaction.TransactionalMessageService;
import org.apache.rocketmq.common.BrokerConfig;
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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class SendMessageProcessorTest {
    private SendMessageProcessor sendMessageProcessor;

    @Mock
    private ChannelHandlerContext handlerContext;

    @Spy
    private BrokerController brokerController = new BrokerController(new BrokerConfig(), new NettyServerConfig(), new NettyClientConfig(), new MessageStoreConfig());

    @Mock
    private MessageStore messageStore;

    @Mock
    private TransactionalMessageService transactionMsgService;

    private String topic = "FooBar";

    private String group = "FooBarGroup";

    @Test
    public void testProcessRequest() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        assertPutResult(SUCCESS);
    }

    @Test
    public void testProcessRequest_WithHook() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        List<SendMessageHook> sendMessageHookList = new ArrayList<>();
        final SendMessageContext[] sendMessageContext = new SendMessageContext[1];
        SendMessageHook sendMessageHook = new SendMessageHook() {
            @Override
            public String hookName() {
                return null;
            }

            @Override
            public void sendMessageBefore(SendMessageContext context) {
                sendMessageContext[0] = context;
            }

            @Override
            public void sendMessageAfter(SendMessageContext context) {
            }
        };
        sendMessageHookList.add(sendMessageHook);
        sendMessageProcessor.registerSendMessageHook(sendMessageHookList);
        assertPutResult(SUCCESS);
        System.out.println(sendMessageContext[0]);
        assertThat(sendMessageContext[0]).isNotNull();
        assertThat(sendMessageContext[0].getTopic()).isEqualTo(topic);
        assertThat(sendMessageContext[0].getProducerGroup()).isEqualTo(group);
    }

    @Test
    public void testProcessRequest_FlushTimeOut() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.FLUSH_DISK_TIMEOUT, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(FLUSH_DISK_TIMEOUT);
    }

    @Test
    public void testProcessRequest_MessageIllegal() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.MESSAGE_ILLEGAL, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(MESSAGE_ILLEGAL);
    }

    @Test
    public void testProcessRequest_CreateMappedFileFailed() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.CREATE_MAPEDFILE_FAILED, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(SYSTEM_ERROR);
    }

    @Test
    public void testProcessRequest_FlushSlaveTimeout() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.FLUSH_SLAVE_TIMEOUT, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(FLUSH_SLAVE_TIMEOUT);
    }

    @Test
    public void testProcessRequest_PageCacheBusy() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.OS_PAGECACHE_BUSY, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(SYSTEM_ERROR);
    }

    @Test
    public void testProcessRequest_PropertiesTooLong() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PROPERTIES_SIZE_EXCEEDED, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(MESSAGE_ILLEGAL);
    }

    @Test
    public void testProcessRequest_ServiceNotAvailable() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.SERVICE_NOT_AVAILABLE, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(SERVICE_NOT_AVAILABLE);
    }

    @Test
    public void testProcessRequest_SlaveNotAvailable() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.SLAVE_NOT_AVAILABLE, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.UNKNOWN_ERROR)));
        assertPutResult(SLAVE_NOT_AVAILABLE);
    }

    @Test
    public void testProcessRequest_WithMsgBack() throws RemotingCommandException {
        Mockito.when(messageStore.putMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        final RemotingCommand request = createSendMsgBackCommand(CONSUMER_SEND_MSG_BACK);
        sendMessageProcessor = new SendMessageProcessor(brokerController);
        final RemotingCommand response = sendMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUCCESS);
    }

    @Test
    public void testProcessRequest_Transaction() throws RemotingCommandException {
        brokerController.setTransactionalMessageService(transactionMsgService);
        Mockito.when(brokerController.getTransactionalMessageService().prepareMessage(ArgumentMatchers.any(MessageExtBrokerInner.class))).thenReturn(new org.apache.rocketmq.store.PutMessageResult(PutMessageStatus.PUT_OK, new org.apache.rocketmq.store.AppendMessageResult(AppendMessageStatus.PUT_OK)));
        RemotingCommand request = createSendTransactionMsgCommand(SEND_MESSAGE);
        final RemotingCommand[] response = new RemotingCommand[1];
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                response[0] = invocation.getArgument(0);
                return null;
            }
        }).when(handlerContext).writeAndFlush(ArgumentMatchers.any(Object.class));
        RemotingCommand responseToReturn = sendMessageProcessor.processRequest(handlerContext, request);
        if (responseToReturn != null) {
            assertThat(response[0]).isNull();
            response[0] = responseToReturn;
        }
        assertThat(response[0].getCode()).isEqualTo(SUCCESS);
    }
}

