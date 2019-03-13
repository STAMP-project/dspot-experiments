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
package org.apache.rocketmq.client.consumer;


import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.consumer.PullAPIWrapper;
import org.apache.rocketmq.client.impl.consumer.PullMessageService;
import org.apache.rocketmq.client.impl.consumer.RebalancePushImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMQPushConsumerTest {
    private String consumerGroup;

    private String topic = "FooBar";

    private String brokerName = "BrokerA";

    private MQClientInstance mQClientFactory;

    @Mock
    private MQClientAPIImpl mQClientAPIImpl;

    private PullAPIWrapper pullAPIWrapper;

    private RebalancePushImpl rebalancePushImpl;

    private DefaultMQPushConsumer pushConsumer;

    @Test
    public void testStart_OffsetShouldNotNUllAfterStart() {
        Assert.assertNotNull(pushConsumer.getOffsetStore());
    }

    @Test
    public void testPullMessage_Success() throws InterruptedException, MQBrokerException, RemotingException {
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
        countDownLatch.await();
        assertThat(messageExts[0].getTopic()).isEqualTo(topic);
        assertThat(messageExts[0].getBody()).isEqualTo(new byte[]{ 'a' });
    }

    @Test
    public void testPullMessage_SuccessWithOrderlyService() throws Exception {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessageExt[] messageExts = new MessageExt[1];
        MessageListenerOrderly listenerOrderly = new MessageListenerOrderly() {
            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                messageExts[0] = msgs.get(0);
                countDownLatch.countDown();
                return null;
            }
        };
        pushConsumer.registerMessageListener(listenerOrderly);
        pushConsumer.getDefaultMQPushConsumerImpl().setConsumeMessageService(new org.apache.rocketmq.client.impl.consumer.ConsumeMessageOrderlyService(pushConsumer.getDefaultMQPushConsumerImpl(), listenerOrderly));
        pushConsumer.getDefaultMQPushConsumerImpl().setConsumeOrderly(true);
        pushConsumer.getDefaultMQPushConsumerImpl().doRebalance();
        PullMessageService pullMessageService = mQClientFactory.getPullMessageService();
        pullMessageService.executePullRequestLater(createPullRequest(), 100);
        countDownLatch.await(10, TimeUnit.SECONDS);
        assertThat(messageExts[0].getTopic()).isEqualTo(topic);
        assertThat(messageExts[0].getBody()).isEqualTo(new byte[]{ 'a' });
    }

    @Test
    public void testCheckConfig() {
        DefaultMQPushConsumer pushConsumer = createPushConsumer();
        pushConsumer.setPullThresholdForQueue((65535 + 1));
        try {
            pushConsumer.start();
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("pullThresholdForQueue Out of range [1, 65535]");
        }
        pushConsumer = createPushConsumer();
        pushConsumer.setPullThresholdForTopic(((65535 * 100) + 1));
        try {
            pushConsumer.start();
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("pullThresholdForTopic Out of range [1, 6553500]");
        }
        pushConsumer = createPushConsumer();
        pushConsumer.setPullThresholdSizeForQueue((1024 + 1));
        try {
            pushConsumer.start();
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("pullThresholdSizeForQueue Out of range [1, 1024]");
        }
        pushConsumer = createPushConsumer();
        pushConsumer.setPullThresholdSizeForTopic(((1024 * 100) + 1));
        try {
            pushConsumer.start();
            failBecauseExceptionWasNotThrown(MQClientException.class);
        } catch (MQClientException e) {
            assertThat(e).hasMessageContaining("pullThresholdSizeForTopic Out of range [1, 102400]");
        }
    }
}

