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


import SendStatus.SEND_OK;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.test.base.BaseConf;
import org.apache.rocketmq.test.factory.MessageFactory;
import org.apache.rocketmq.test.util.RandomUtils;
import org.junit.Test;


public class MessageExceptionIT extends BaseConf {
    private static DefaultMQProducer producer = null;

    private static String topic = null;

    @Test
    public void testProducerSmoke() {
        Message msg = new Message(MessageExceptionIT.topic, RandomUtils.getStringByUUID().getBytes());
        SendResult sendResult = null;
        try {
            sendResult = MessageExceptionIT.producer.send(msg);
        } catch (Exception e) {
        }
        assertThat(sendResult).isNotEqualTo(null);
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
    }

    @Test(expected = MQClientException.class)
    public void testSynSendNullMessage() throws Exception {
        MessageExceptionIT.producer.send(((Message) (null)));
    }

    @Test(expected = MQClientException.class)
    public void testSynSendNullBodyMessage() throws Exception {
        Message msg = new Message(MessageExceptionIT.topic, RandomUtils.getStringByUUID().getBytes());
        msg.setBody(null);
        MessageExceptionIT.producer.send(msg);
    }

    @Test(expected = MQClientException.class)
    public void testSynSendZeroSizeBodyMessage() throws Exception {
        Message msg = new Message(MessageExceptionIT.topic, RandomUtils.getStringByUUID().getBytes());
        msg.setBody(new byte[0]);
        MessageExceptionIT.producer.send(msg);
    }

    @Test(expected = MQClientException.class)
    public void testSynSendOutOfSizeBodyMessage() throws Exception {
        Message msg = new Message(MessageExceptionIT.topic, RandomUtils.getStringByUUID().getBytes());
        msg.setBody(new byte[((1024 * 1024) * 4) + 1]);
        MessageExceptionIT.producer.send(msg);
    }

    @Test(expected = MQClientException.class)
    public void testSynSendNullTopicMessage() throws Exception {
        Message msg = new Message(null, RandomUtils.getStringByUUID().getBytes());
        MessageExceptionIT.producer.send(msg);
    }

    @Test(expected = MQClientException.class)
    public void testSynSendBlankTopicMessage() throws Exception {
        Message msg = new Message("", RandomUtils.getStringByUUID().getBytes());
        MessageExceptionIT.producer.send(msg);
    }

    @Test(expected = MQClientException.class)
    public void testSend128kMsg() throws Exception {
        Message msg = new Message(MessageExceptionIT.topic, RandomUtils.getStringWithNumber((((1024 * 1024) * 4) + 1)).getBytes());
        MessageExceptionIT.producer.send(msg);
    }

    @Test
    public void testSendLess128kMsg() {
        Message msg = new Message(MessageExceptionIT.topic, RandomUtils.getStringWithNumber((128 * 1024)).getBytes());
        SendResult sendResult = null;
        try {
            sendResult = MessageExceptionIT.producer.send(msg);
        } catch (Exception e) {
        }
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
    }

    @Test
    public void testSendMsgWithUserProperty() {
        Message msg = MessageFactory.getRandomMessage(MessageExceptionIT.topic);
        msg.putUserProperty("key", RandomUtils.getCheseWord((10 * 1024)));
        SendResult sendResult = null;
        try {
            sendResult = MessageExceptionIT.producer.send(msg);
        } catch (Exception e) {
        }
        assertThat(sendResult.getSendStatus()).isEqualTo(SEND_OK);
    }
}

