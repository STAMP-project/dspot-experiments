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
package org.apache.rocketmq.client.producer;


import CommunicationMode.SYNC;
import SendStatus.SEND_OK;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.hook.SendMessageContext;
import org.apache.rocketmq.client.hook.SendMessageHook;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.remoting.netty.NettyRemotingClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMQProducerTest {
    @Spy
    private MQClientInstance mQClientFactory = MQClientManager.getInstance().getAndCreateMQClientInstance(new ClientConfig());

    @Mock
    private MQClientAPIImpl mQClientAPIImpl;

    @Mock
    private NettyRemotingClient nettyRemotingClient;

    private DefaultMQProducer producer;

    private Message message;

    private Message zeroMsg;

    private Message bigMessage;

    private String topic = "FooBar";

    private String producerGroupPrefix = "FooBar_PID";

    @Test
    public void testSendMessage_ZeroMessage() throws InterruptedException, MQBrokerException, RemotingException {
        try {
            producer.send(zeroMsg);
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("message body length is zero");
        }
    }

    @Test
    public void testSendMessage_NoNameSrv() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.when(mQClientAPIImpl.getNameServerAddressList()).thenReturn(new ArrayList<String>());
        try {
            producer.send(message);
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("No name server address");
        }
    }

    @Test
    public void testSendMessage_NoRoute() throws InterruptedException, MQBrokerException, RemotingException {
        Mockito.when(mQClientAPIImpl.getNameServerAddressList()).thenReturn(Collections.singletonList("127.0.0.1:9876"));
        try {
            producer.send(message);
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("No route info of this topic");
        }
    }

    @Test
    public void testSendMessageSync_Success() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        Mockito.when(mQClientAPIImpl.getTopicRouteInfoFromNameServer(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(DefaultMQProducerTest.createTopicRoute());
        SendResult sendResult = producer.send(message);
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
        assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
        assertThat(sendResult.getQueueOffset()).isEqualTo(456L);
    }

    @Test
    public void testSendMessageSync_WithBodyCompressed() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        Mockito.when(mQClientAPIImpl.getTopicRouteInfoFromNameServer(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(DefaultMQProducerTest.createTopicRoute());
        SendResult sendResult = producer.send(bigMessage);
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
        assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
        assertThat(sendResult.getQueueOffset()).isEqualTo(456L);
    }

    @Test
    public void testSendMessageAsync_Success() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        producer.send(message, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
                assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
                assertThat(sendResult.getQueueOffset()).isEqualTo(456L);
                countDownLatch.countDown();
            }

            @Override
            public void onException(Throwable e) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSendMessageAsync() throws InterruptedException, MQClientException, RemotingException {
        final AtomicInteger cc = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(6);
        SendCallback sendCallback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(Throwable e) {
                e.printStackTrace();
                cc.incrementAndGet();
                countDownLatch.countDown();
            }
        };
        MessageQueueSelector messageQueueSelector = new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                return null;
            }
        };
        Message message = new Message();
        message.setTopic("test");
        message.setBody("hello world".getBytes());
        producer.send(new Message(), sendCallback);
        producer.send(message, sendCallback, 1000);
        producer.send(message, new MessageQueue(), sendCallback);
        producer.send(new Message(), new MessageQueue(), sendCallback, 1000);
        producer.send(new Message(), messageQueueSelector, null, sendCallback);
        producer.send(message, messageQueueSelector, null, sendCallback, 1000);
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
        assertThat(cc.get()).isEqualTo(6);
    }

    @Test
    public void testSendMessageAsync_BodyCompressed() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        producer.send(bigMessage, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
                assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
                assertThat(sendResult.getQueueOffset()).isEqualTo(456L);
                countDownLatch.countDown();
            }

            @Override
            public void onException(Throwable e) {
                countDownLatch.countDown();
            }
        });
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSendMessageSync_SuccessWithHook() throws Throwable {
        Mockito.when(mQClientAPIImpl.getTopicRouteInfoFromNameServer(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(DefaultMQProducerTest.createTopicRoute());
        final Throwable[] assertionErrors = new Throwable[1];
        final CountDownLatch countDownLatch = new CountDownLatch(2);
        producer.getDefaultMQProducerImpl().registerSendMessageHook(new SendMessageHook() {
            @Override
            public String hookName() {
                return "TestHook";
            }

            @Override
            public void sendMessageBefore(final SendMessageContext context) {
                assertionErrors[0] = assertInOtherThread(new Runnable() {
                    @Override
                    public void run() {
                        assertThat(context.getMessage()).isEqualTo(message);
                        assertThat(context.getProducer()).isEqualTo(producer);
                        assertThat(context.getCommunicationMode()).isEqualTo(SYNC);
                        assertThat(context.getSendResult()).isNull();
                    }
                });
                countDownLatch.countDown();
            }

            @Override
            public void sendMessageAfter(final SendMessageContext context) {
                assertionErrors[0] = assertInOtherThread(new Runnable() {
                    @Override
                    public void run() {
                        assertThat(context.getMessage()).isEqualTo(message);
                        assertThat(context.getProducer()).isEqualTo(producer.getDefaultMQProducerImpl());
                        assertThat(context.getCommunicationMode()).isEqualTo(SYNC);
                        assertThat(context.getSendResult()).isNotNull();
                    }
                });
                countDownLatch.countDown();
            }
        });
        SendResult sendResult = producer.send(message);
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
        assertThat(sendResult.getOffsetMsgId()).isEqualTo("123");
        assertThat(sendResult.getQueueOffset()).isEqualTo(456L);
        countDownLatch.await();
        if ((assertionErrors[0]) != null) {
            throw assertionErrors[0];
        }
    }

    @Test
    public void testSetCallbackExecutor() throws MQClientException {
        String producerGroupTemp = "testSetCallbackExecutor_" + (System.currentTimeMillis());
        producer = new DefaultMQProducer(producerGroupTemp);
        producer.setNamesrvAddr("127.0.0.1:9876");
        producer.start();
        ExecutorService customized = Executors.newCachedThreadPool();
        producer.setCallbackExecutor(customized);
        NettyRemotingClient remotingClient = ((NettyRemotingClient) (producer.getDefaultMQProducerImpl().getmQClientFactory().getMQClientAPIImpl().getRemotingClient()));
        assertThat(remotingClient.getCallbackExecutor()).isEqualTo(customized);
    }
}

