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
package org.apache.rocketmq.client.impl;


import CommunicationMode.ASYNC;
import CommunicationMode.ONEWAY;
import CommunicationMode.SYNC;
import ResponseCode.SYSTEM_ERROR;
import SendStatus.SEND_OK;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.protocol.header.SendMessageRequestHeader;
import org.apache.rocketmq.common.protocol.header.SendMessageResponseHeader;
import org.apache.rocketmq.remoting.InvokeCallback;
import org.apache.rocketmq.remoting.RemotingClient;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.exception.RemotingTimeoutException;
import org.apache.rocketmq.remoting.netty.NettyClientConfig;
import org.apache.rocketmq.remoting.netty.ResponseFuture;
import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;


@RunWith(MockitoJUnitRunner.class)
public class MQClientAPIImplTest {
    private MQClientAPIImpl mqClientAPI = new MQClientAPIImpl(new NettyClientConfig(), null, null, new ClientConfig());

    @Mock
    private RemotingClient remotingClient;

    @Mock
    private DefaultMQProducerImpl defaultMQProducerImpl;

    private String brokerAddr = "127.0.0.1";

    private String brokerName = "DefaultBroker";

    private static String group = "FooBarGroup";

    private static String topic = "FooBar";

    private Message msg = new Message("FooBar", new byte[]{  });

    @Test
    public void testSendMessageOneWay_Success() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.doNothing().when(remotingClient).invokeOneway(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong());
        SendResult sendResult = mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ONEWAY, new SendMessageContext(), defaultMQProducerImpl);
        assertThat(sendResult).isNull();
    }

    @Test
    public void testSendMessageOneWay_WithException() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.doThrow(new RemotingTimeoutException("Remoting Exception in Test")).when(remotingClient).invokeOneway(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong());
        try {
            mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ONEWAY, new SendMessageContext(), defaultMQProducerImpl);
            failBecauseExceptionWasNotThrown(RemotingException.class);
        } catch (RemotingException e) {
            assertThat(e).hasMessage("Remoting Exception in Test");
        }
        Mockito.doThrow(new InterruptedException("Interrupted Exception in Test")).when(remotingClient).invokeOneway(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong());
        try {
            mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ONEWAY, new SendMessageContext(), defaultMQProducerImpl);
            failBecauseExceptionWasNotThrown(InterruptedException.class);
        } catch (InterruptedException e) {
            assertThat(e).hasMessage("Interrupted Exception in Test");
        }
    }

    @Test
    public void testSendMessageSync_Success() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock mock) throws Throwable {
                RemotingCommand request = mock.getArgument(1);
                return createSuccessResponse(request);
            }
        }).when(remotingClient).invokeSync(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong());
        SendMessageRequestHeader requestHeader = createSendMessageRequestHeader();
        SendResult sendResult = mqClientAPI.sendMessage(brokerAddr, brokerName, msg, requestHeader, (3 * 1000), SYNC, new SendMessageContext(), defaultMQProducerImpl);
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
        assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
        assertThat(sendResult.getQueueOffset()).isEqualTo(123L);
        assertThat(sendResult.getMessageQueue().getQueueId()).isEqualTo(1);
    }

    @Test
    public void testSendMessageSync_WithException() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock mock) throws Throwable {
                RemotingCommand request = mock.getArgument(1);
                RemotingCommand response = RemotingCommand.createResponseCommand(SendMessageResponseHeader.class);
                response.setCode(SYSTEM_ERROR);
                response.setOpaque(request.getOpaque());
                response.setRemark("Broker is broken.");
                return response;
            }
        }).when(remotingClient).invokeSync(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong());
        SendMessageRequestHeader requestHeader = createSendMessageRequestHeader();
        try {
            mqClientAPI.sendMessage(brokerAddr, brokerName, msg, requestHeader, (3 * 1000), SYNC, new SendMessageContext(), defaultMQProducerImpl);
            failBecauseExceptionWasNotThrown(MQBrokerException.class);
        } catch (MQBrokerException e) {
            assertThat(e).hasMessageContaining("Broker is broken.");
        }
    }

    @Test
    public void testSendMessageAsync_Success() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.doNothing().when(remotingClient).invokeAsync(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(InvokeCallback.class));
        SendResult sendResult = mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ASYNC, new SendMessageContext(), defaultMQProducerImpl);
        assertThat(sendResult).isNull();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock mock) throws Throwable {
                InvokeCallback callback = mock.getArgument(3);
                RemotingCommand request = mock.getArgument(1);
                ResponseFuture responseFuture = new ResponseFuture(null, request.getOpaque(), (3 * 1000), null, null);
                responseFuture.setResponseCommand(createSuccessResponse(request));
                callback.operationComplete(responseFuture);
                return null;
            }
        }).when(remotingClient).invokeAsync(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(InvokeCallback.class));
        SendMessageContext sendMessageContext = new SendMessageContext();
        sendMessageContext.setProducer(new DefaultMQProducerImpl(new DefaultMQProducer()));
        mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ASYNC, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
                assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
                assertThat(sendResult.getQueueOffset()).isEqualTo(123L);
                assertThat(sendResult.getMessageQueue().getQueueId()).isEqualTo(1);
            }

            @Override
            public void onException(Throwable e) {
            }
        }, null, null, 0, sendMessageContext, defaultMQProducerImpl);
    }

    @Test
    public void testSendMessageAsync_WithException() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.doThrow(new RemotingTimeoutException("Remoting Exception in Test")).when(remotingClient).invokeAsync(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(InvokeCallback.class));
        try {
            mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ASYNC, new SendMessageContext(), defaultMQProducerImpl);
            failBecauseExceptionWasNotThrown(RemotingException.class);
        } catch (RemotingException e) {
            assertThat(e).hasMessage("Remoting Exception in Test");
        }
        Mockito.doThrow(new InterruptedException("Interrupted Exception in Test")).when(remotingClient).invokeAsync(ArgumentMatchers.anyString(), ArgumentMatchers.any(RemotingCommand.class), ArgumentMatchers.anyLong(), ArgumentMatchers.any(InvokeCallback.class));
        try {
            mqClientAPI.sendMessage(brokerAddr, brokerName, msg, new SendMessageRequestHeader(), (3 * 1000), ASYNC, new SendMessageContext(), defaultMQProducerImpl);
            failBecauseExceptionWasNotThrown(InterruptedException.class);
        } catch (InterruptedException e) {
            assertThat(e).hasMessage("Interrupted Exception in Test");
        }
    }
}

