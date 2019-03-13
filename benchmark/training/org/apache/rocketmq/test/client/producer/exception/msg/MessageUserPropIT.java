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
package org.apache.rocketmq.test.client.producer.exception.msg;


import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.test.base.BaseConf;
import org.apache.rocketmq.test.client.consumer.balance.NormalMsgStaticBalanceIT;
import org.apache.rocketmq.test.client.rmq.RMQNormalConsumer;
import org.apache.rocketmq.test.client.rmq.RMQNormalProducer;
import org.apache.rocketmq.test.factory.MessageFactory;
import org.apache.rocketmq.test.listener.rmq.concurrent.RMQNormalListener;
import org.junit.Test;


public class MessageUserPropIT extends BaseConf {
    private static Logger logger = Logger.getLogger(NormalMsgStaticBalanceIT.class);

    private RMQNormalProducer producer = null;

    private String topic = null;

    /**
     *
     *
     * @since version3.4.6
     */
    @Test
    public void testSendEnglishUserProp() {
        Message msg = MessageFactory.getRandomMessage(topic);
        String msgKey = "jueyinKey";
        String msgValue = "jueyinValue";
        msg.putUserProperty(msgKey, msgValue);
        RMQNormalConsumer consumer = BaseConf.getConsumer(BaseConf.nsAddr, topic, "*", new RMQNormalListener());
        producer.send(msg, null);
        assertThat(producer.getAllMsgBody().size()).isEqualTo(1);
        consumer.getListener().waitForMessageConsume(producer.getAllMsgBody(), BaseConf.consumeTime);
        Message sendMsg = ((Message) (producer.getFirstMsg()));
        Message recvMsg = ((Message) (consumer.getListener().getFirstMsg()));
        assertThat(recvMsg.getUserProperty(msgKey)).isEqualTo(sendMsg.getUserProperty(msgKey));
    }

    /**
     *
     *
     * @since version3.4.6
     */
    @Test
    public void testSendChinaUserProp() {
        Message msg = MessageFactory.getRandomMessage(topic);
        String msgKey = "jueyinKey";
        String msgValue = "jueyinzhi";
        msg.putUserProperty(msgKey, msgValue);
        RMQNormalConsumer consumer = BaseConf.getConsumer(BaseConf.nsAddr, topic, "*", new RMQNormalListener());
        producer.send(msg, null);
        assertThat(producer.getAllMsgBody().size()).isEqualTo(1);
        consumer.getListener().waitForMessageConsume(producer.getAllMsgBody(), BaseConf.consumeTime);
        Message sendMsg = ((Message) (producer.getFirstMsg()));
        Message recvMsg = ((Message) (consumer.getListener().getFirstMsg()));
        assertThat(recvMsg.getUserProperty(msgKey)).isEqualTo(sendMsg.getUserProperty(msgKey));
    }
}

