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
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.rocketmq.test.client.consumer.broadcast.order;


import java.util.List;
import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.test.base.BaseConf;
import org.apache.rocketmq.test.client.consumer.broadcast.BaseBroadCastIT;
import org.apache.rocketmq.test.client.rmq.RMQBroadCastConsumer;
import org.apache.rocketmq.test.client.rmq.RMQNormalProducer;
import org.apache.rocketmq.test.listener.rmq.order.RMQOrderListener;
import org.apache.rocketmq.test.message.MessageQueueMsg;
import org.apache.rocketmq.test.util.TestUtils;
import org.apache.rocketmq.test.util.VerifyUtils;
import org.junit.Test;


public class OrderMsgBroadCastIT extends BaseBroadCastIT {
    private static Logger logger = Logger.getLogger(OrderMsgBroadCastIT.class);

    private RMQNormalProducer producer = null;

    private String topic = null;

    private int broadcastConsumeTime = (1 * 60) * 1000;

    @Test
    public void testTwoConsumerSubTag() {
        int msgSize = 10;
        RMQBroadCastConsumer consumer1 = BaseBroadCastIT.getBroadCastConsumer(BaseConf.nsAddr, topic, "*", new RMQOrderListener());
        RMQBroadCastConsumer consumer2 = BaseBroadCastIT.getBroadCastConsumer(BaseConf.nsAddr, consumer1.getConsumerGroup(), topic, "*", new RMQOrderListener());
        TestUtils.waitForSeconds(BaseConf.waitTime);
        List<MessageQueue> mqs = producer.getMessageQueue();
        MessageQueueMsg mqMsgs = new MessageQueueMsg(mqs, msgSize);
        producer.send(mqMsgs.getMsgsWithMQ());
        consumer1.getListener().waitForMessageConsume(producer.getAllMsgBody(), broadcastConsumeTime);
        consumer2.getListener().waitForMessageConsume(producer.getAllMsgBody(), broadcastConsumeTime);
        assertThat(VerifyUtils.verifyOrder(getMsgs())).isEqualTo(true);
        assertThat(VerifyUtils.verifyOrder(getMsgs())).isEqualTo(true);
    }
}

