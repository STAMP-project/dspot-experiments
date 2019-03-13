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
package org.apache.rocketmq.client.impl.consumer;


import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullAPIWrapper;
import org.apache.rocketmq.client.consumer.RebalancePushImpl;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.stat.ConsumerStatsManager;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.body.ConsumeStatus;
import org.apache.rocketmq.common.stats.StatsItem;
import org.apache.rocketmq.common.stats.StatsItemSet;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsumeMessageConcurrentlyServiceTest {
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
    public void testPullMessage_ConsumeSuccess() throws Exception, InterruptedException, NoSuchFieldException, MQBrokerException, RemotingException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final MessageExt[] messageExts = new MessageExt[1];
        ConsumeMessageConcurrentlyService normalServie = new ConsumeMessageConcurrentlyService(pushConsumer.getDefaultMQPushConsumerImpl(), new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                messageExts[0] = msgs.get(0);
                countDownLatch.countDown();
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        pushConsumer.getDefaultMQPushConsumerImpl().setConsumeMessageService(normalServie);
        PullMessageService pullMessageService = mQClientFactory.getPullMessageService();
        pullMessageService.executePullRequestImmediately(createPullRequest());
        countDownLatch.await();
        Thread.sleep(1000);
        ConsumeStatus stats = normalServie.getConsumerStatsManager().consumeStatus(pushConsumer.getDefaultMQPushConsumerImpl().groupName(), topic);
        ConsumerStatsManager mgr = normalServie.getConsumerStatsManager();
        Field statItmeSetField = mgr.getClass().getDeclaredField("topicAndGroupConsumeOKTPS");
        statItmeSetField.setAccessible(true);
        StatsItemSet itemSet = ((StatsItemSet) (statItmeSetField.get(mgr)));
        StatsItem item = itemSet.getAndCreateStatsItem((((topic) + "@") + (pushConsumer.getDefaultMQPushConsumerImpl().groupName())));
        assertThat(item.getValue().get()).isGreaterThan(0L);
        assertThat(messageExts[0].getTopic()).isEqualTo(topic);
        assertThat(messageExts[0].getBody()).isEqualTo(new byte[]{ 'a' });
    }
}

