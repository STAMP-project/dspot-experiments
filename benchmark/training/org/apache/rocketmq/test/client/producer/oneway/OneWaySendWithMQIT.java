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
package org.apache.rocketmq.test.client.producer.oneway;


import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.test.base.BaseConf;
import org.apache.rocketmq.test.client.consumer.tag.TagMessageWith1ConsumerIT;
import org.apache.rocketmq.test.client.rmq.RMQAsyncSendProducer;
import org.apache.rocketmq.test.client.rmq.RMQNormalConsumer;
import org.apache.rocketmq.test.listener.rmq.concurrent.RMQNormalListener;
import org.apache.rocketmq.test.util.VerifyUtils;
import org.junit.Test;


public class OneWaySendWithMQIT extends BaseConf {
    private static Logger logger = Logger.getLogger(TagMessageWith1ConsumerIT.class);

    private static boolean sendFail = false;

    private RMQAsyncSendProducer producer = null;

    private String topic = null;

    @Test
    public void testAsyncSendWithMQ() {
        int msgSize = 20;
        int queueId = 0;
        RMQNormalConsumer consumer = BaseConf.getConsumer(BaseConf.nsAddr, topic, "*", new RMQNormalListener());
        MessageQueue mq = new MessageQueue(topic, BaseConf.broker1Name, queueId);
        producer.sendOneWay(msgSize, mq);
        producer.waitForResponse((5 * 1000));
        consumer.getListener().waitForMessageConsume(producer.getAllMsgBody(), BaseConf.consumeTime);
        assertThat(VerifyUtils.getFilterdMessage(producer.getAllMsgBody(), consumer.getListener().getAllMsgBody())).containsExactlyElementsIn(producer.getAllMsgBody());
        producer.clearMsg();
        consumer.clearMsg();
        mq = new MessageQueue(topic, BaseConf.broker2Name, queueId);
        producer.asyncSend(msgSize, mq);
        producer.waitForResponse((5 * 1000));
        consumer.getListener().waitForMessageConsume(producer.getAllMsgBody(), BaseConf.consumeTime);
        assertThat(VerifyUtils.getFilterdMessage(producer.getAllMsgBody(), consumer.getListener().getAllMsgBody())).containsExactlyElementsIn(producer.getAllMsgBody());
    }
}

