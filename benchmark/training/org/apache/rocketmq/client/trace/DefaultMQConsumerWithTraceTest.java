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
package org.apache.rocketmq.client.trace;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.consumer.PullAPIWrapper;
import org.apache.rocketmq.client.impl.consumer.PullMessageService;
import org.apache.rocketmq.client.impl.consumer.RebalancePushImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMQConsumerWithTraceTest {
    private String consumerGroup;

    private String consumerGroupNormal;

    private String producerGroupTraceTemp = (MixAll.RMQ_SYS_TRACE_TOPIC) + (System.currentTimeMillis());

    private String topic = "FooBar";

    private String brokerName = "BrokerA";

    private MQClientInstance mQClientFactory;

    @Mock
    private MQClientAPIImpl mQClientAPIImpl;

    private PullAPIWrapper pullAPIWrapper;

    private RebalancePushImpl rebalancePushImpl;

    private DefaultMQPushConsumer pushConsumer;

    private DefaultMQPushConsumer normalPushConsumer;

    private DefaultMQPushConsumer customTraceTopicpushConsumer;

    private AsyncTraceDispatcher asyncTraceDispatcher;

    private MQClientInstance mQClientTraceFactory;

    @Mock
    private MQClientAPIImpl mQClientTraceAPIImpl;

    private DefaultMQProducer traceProducer;

    private String customerTraceTopic = "rmq_trace_topic_12345";

    @Test
    public void testPullMessage_WithTrace_Success() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        traceProducer.getDefaultMQProducerImpl().getmQClientFactory().registerProducer(producerGroupTraceTemp, traceProducer.getDefaultMQProducerImpl());
        // when(mQClientAPIImpl.getTopicRouteInfoFromNameServer(anyString(), anyLong())).thenReturn(createTopicRoute());
        // when(mQClientTraceAPIImpl.getTopicRouteInfoFromNameServer(anyString(), anyLong())).thenReturn(createTraceTopicRoute());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessageExt[] messageExts = new MessageExt[1];
        pushConsumer.getDefaultMQPushConsumerImpl().setConsumeMessageService(new org.apache.rocketmq.client.impl.consumer.ConsumeMessageConcurrentlyService(pushConsumer.getDefaultMQPushConsumerImpl(), new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                messageExts[0] = msgs.get(0);
                countDownLatch.countDown();
                return null;
            }
        }));
        PullMessageService pullMessageService = mQClientFactory.getPullMessageService();
        pullMessageService.executePullRequestImmediately(createPullRequest());
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
        assertThat(messageExts[0].getTopic()).isEqualTo(topic);
        assertThat(messageExts[0].getBody()).isEqualTo(new byte[]{ 'a' });
    }
}

