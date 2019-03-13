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


import GetMessageStatus.MESSAGE_WAS_REMOVING;
import GetMessageStatus.NO_MESSAGE_IN_QUEUE;
import RequestCode.PULL_MESSAGE;
import ResponseCode.PULL_OFFSET_MOVED;
import ResponseCode.PULL_RETRY_IMMEDIATELY;
import ResponseCode.SUBSCRIPTION_NOT_EXIST;
import ResponseCode.SUBSCRIPTION_NOT_LATEST;
import ResponseCode.SUCCESS;
import ResponseCode.TOPIC_NOT_EXIST;
import io.netty.channel.ChannelHandlerContext;
import java.util.ArrayList;
import java.util.List;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.broker.client.ClientChannelInfo;
import org.apache.rocketmq.broker.filter.ExpressionMessageFilter;
import org.apache.rocketmq.broker.mqtrace.ConsumeMessageContext;
import org.apache.rocketmq.broker.mqtrace.ConsumeMessageHook;
import org.apache.rocketmq.common.BrokerConfig;
import org.apache.rocketmq.remoting.exception.RemotingCommandException;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.NettyServerConfig;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.apache.rocketmq.store.GetMessageResult;
import org.apache.rocketmq.store.MessageStore;
import org.apache.rocketmq.store.config.MessageStoreConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PullMessageProcessorTest {
    private PullMessageProcessor pullMessageProcessor;

    @Spy
    private BrokerController brokerController = new BrokerController(new BrokerConfig(), new NettyServerConfig(), new NettyClientConfig(), new MessageStoreConfig());

    @Mock
    private ChannelHandlerContext handlerContext;

    @Mock
    private MessageStore messageStore;

    private ClientChannelInfo clientChannelInfo;

    private String group = "FooBarGroup";

    private String topic = "FooBar";

    @Test
    public void testProcessRequest_TopicNotExist() throws RemotingCommandException {
        brokerController.getTopicConfigManager().getTopicConfigTable().remove(topic);
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(TOPIC_NOT_EXIST);
        assertThat(response.getRemark()).contains((("topic[" + (topic)) + "] not exist"));
    }

    @Test
    public void testProcessRequest_SubNotExist() throws RemotingCommandException {
        brokerController.getConsumerManager().unregisterConsumer(group, clientChannelInfo, false);
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUBSCRIPTION_NOT_EXIST);
        assertThat(response.getRemark()).contains("consumer's group info not exist");
    }

    @Test
    public void testProcessRequest_SubNotLatest() throws RemotingCommandException {
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        request.addExtField("subVersion", String.valueOf(101));
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUBSCRIPTION_NOT_LATEST);
        assertThat(response.getRemark()).contains("subscription not latest");
    }

    @Test
    public void testProcessRequest_Found() throws RemotingCommandException {
        GetMessageResult getMessageResult = createGetMessageResult();
        Mockito.when(messageStore.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ExpressionMessageFilter.class))).thenReturn(getMessageResult);
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUCCESS);
    }

    @Test
    public void testProcessRequest_FoundWithHook() throws RemotingCommandException {
        GetMessageResult getMessageResult = createGetMessageResult();
        Mockito.when(messageStore.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ExpressionMessageFilter.class))).thenReturn(getMessageResult);
        List<ConsumeMessageHook> consumeMessageHookList = new ArrayList<>();
        final ConsumeMessageContext[] messageContext = new ConsumeMessageContext[1];
        ConsumeMessageHook consumeMessageHook = new ConsumeMessageHook() {
            @Override
            public String hookName() {
                return "TestHook";
            }

            @Override
            public void consumeMessageBefore(ConsumeMessageContext context) {
                messageContext[0] = context;
            }

            @Override
            public void consumeMessageAfter(ConsumeMessageContext context) {
            }
        };
        consumeMessageHookList.add(consumeMessageHook);
        pullMessageProcessor.registerConsumeMessageHook(consumeMessageHookList);
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(SUCCESS);
        assertThat(messageContext[0]).isNotNull();
        assertThat(messageContext[0].getConsumerGroup()).isEqualTo(group);
        assertThat(messageContext[0].getTopic()).isEqualTo(topic);
        assertThat(messageContext[0].getQueueId()).isEqualTo(1);
    }

    @Test
    public void testProcessRequest_MsgWasRemoving() throws RemotingCommandException {
        GetMessageResult getMessageResult = createGetMessageResult();
        getMessageResult.setStatus(MESSAGE_WAS_REMOVING);
        Mockito.when(messageStore.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ExpressionMessageFilter.class))).thenReturn(getMessageResult);
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(PULL_RETRY_IMMEDIATELY);
    }

    @Test
    public void testProcessRequest_NoMsgInQueue() throws RemotingCommandException {
        GetMessageResult getMessageResult = createGetMessageResult();
        getMessageResult.setStatus(NO_MESSAGE_IN_QUEUE);
        Mockito.when(messageStore.getMessage(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyInt(), ArgumentMatchers.any(ExpressionMessageFilter.class))).thenReturn(getMessageResult);
        final RemotingCommand request = createPullMsgCommand(PULL_MESSAGE);
        RemotingCommand response = pullMessageProcessor.processRequest(handlerContext, request);
        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(PULL_OFFSET_MOVED);
    }
}

