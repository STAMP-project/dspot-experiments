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
package org.apache.rocketmq.flume.ng.sink;


import Sink.Status;
import Sink.Status.BACKOFF;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Sink;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.PullStatus;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.namesrv.NamesrvController;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class RocketMQSinkTest {
    private static final Logger log = LoggerFactory.getLogger(RocketMQSinkTest.class);

    private static String nameServer = "localhost:9876";

    private static NamesrvController namesrvController;

    private static BrokerController brokerController;

    private DefaultMQPullConsumer consumer;

    private String tag = ((RocketMQSinkConstants.TAG_DEFAULT) + "_SINK_TEST_") + (new Random().nextInt(99));

    private String consumerGroup = "CONSUMER_GROUP_SINK_TEST";

    private int batchSize = 100;

    @Test
    public void testEvent() throws UnsupportedEncodingException, InterruptedException, EventDeliveryException, MQBrokerException, MQClientException, RemotingException {
        /* start sink */
        Context context = new Context();
        context.put(RocketMQSinkConstants.NAME_SERVER_CONFIG, RocketMQSinkTest.nameServer);
        context.put(RocketMQSinkConstants.TAG_CONFIG, tag);
        RocketMQSink sink = new RocketMQSink();
        Configurables.configure(sink, context);
        MemoryChannel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        /* mock flume source */
        String sendMsg = ("\"Hello RocketMQ\"" + ",") + (DateFormatUtils.format(new Date(), "yyyy-MM-DD hh:mm:ss"));
        Transaction tx = channel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(sendMsg.getBytes(), null);
        channel.put(event);
        tx.commit();
        tx.close();
        RocketMQSinkTest.log.info("publish message : {}", sendMsg);
        Sink.Status status = sink.process();
        if (status == (Status.BACKOFF)) {
            Assert.fail("Error");
        }
        sink.stop();
        /* consumer message */
        consumer = new DefaultMQPullConsumer(consumerGroup);
        consumer.setNamesrvAddr(RocketMQSinkTest.nameServer);
        consumer.setMessageModel(MessageModel.valueOf("BROADCASTING"));
        consumer.registerMessageQueueListener(RocketMQSinkConstants.TOPIC_DEFAULT, null);
        consumer.start();
        String receiveMsg = null;
        Set<MessageQueue> queues = consumer.fetchSubscribeMessageQueues(RocketMQSinkConstants.TOPIC_DEFAULT);
        for (MessageQueue queue : queues) {
            long offset = getMessageQueueOffset(queue);
            PullResult pullResult = consumer.pull(queue, tag, offset, 32);
            if ((pullResult.getPullStatus()) == (PullStatus.FOUND)) {
                for (MessageExt message : pullResult.getMsgFoundList()) {
                    byte[] body = message.getBody();
                    receiveMsg = new String(body, "UTF-8");
                    RocketMQSinkTest.log.info("receive message : {}", receiveMsg);
                }
                long nextBeginOffset = pullResult.getNextBeginOffset();
                putMessageQueueOffset(queue, nextBeginOffset);
            }
        }
        /* wait for processQueueTable init */
        Thread.sleep(1000);
        consumer.shutdown();
        Assert.assertEquals(sendMsg, receiveMsg);
    }

    @Test
    public void testBatchEvent() throws UnsupportedEncodingException, InterruptedException, EventDeliveryException, MQBrokerException, MQClientException, RemotingException {
        /* start sink */
        Context context = new Context();
        context.put(RocketMQSinkConstants.NAME_SERVER_CONFIG, RocketMQSinkTest.nameServer);
        context.put(RocketMQSinkConstants.TAG_CONFIG, tag);
        context.put(RocketMQSinkConstants.BATCH_SIZE_CONFIG, String.valueOf(batchSize));
        RocketMQSink sink = new RocketMQSink();
        Configurables.configure(sink, context);
        MemoryChannel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        /* mock flume source */
        Map<String, String> msgs = new HashMap<>();
        Transaction tx = channel.getTransaction();
        tx.begin();
        int sendNum = 0;
        for (int i = 0; i < (batchSize); i++) {
            String sendMsg = ("\"Hello RocketMQ\"" + ",") + (DateFormatUtils.format(new Date(), "yyyy-MM-DD hh:mm:ss:SSSS"));
            Event event = EventBuilder.withBody(sendMsg.getBytes(), null);
            channel.put(event);
            RocketMQSinkTest.log.info("publish message : {}", sendMsg);
            String[] sendMsgKv = sendMsg.split(",");
            msgs.put(sendMsgKv[1], sendMsgKv[0]);
            sendNum++;
            Thread.sleep(10);
        }
        RocketMQSinkTest.log.info("send message num={}", sendNum);
        tx.commit();
        tx.close();
        Sink.Status status = sink.process();
        if (status == (Status.BACKOFF)) {
            Assert.fail("Error");
        }
        sink.stop();
        /* consumer message */
        consumer = new DefaultMQPullConsumer(consumerGroup);
        consumer.setNamesrvAddr(RocketMQSinkTest.nameServer);
        consumer.setMessageModel(MessageModel.valueOf("BROADCASTING"));
        consumer.registerMessageQueueListener(RocketMQSinkConstants.TOPIC_DEFAULT, null);
        consumer.start();
        int receiveNum = 0;
        String receiveMsg = null;
        Set<MessageQueue> queues = consumer.fetchSubscribeMessageQueues(RocketMQSinkConstants.TOPIC_DEFAULT);
        for (MessageQueue queue : queues) {
            long offset = getMessageQueueOffset(queue);
            PullResult pullResult = consumer.pull(queue, tag, offset, batchSize);
            if ((pullResult.getPullStatus()) == (PullStatus.FOUND)) {
                for (MessageExt message : pullResult.getMsgFoundList()) {
                    byte[] body = message.getBody();
                    receiveMsg = new String(body, "UTF-8");
                    String[] receiveMsgKv = receiveMsg.split(",");
                    msgs.remove(receiveMsgKv[1]);
                    RocketMQSinkTest.log.info("receive message : {}", receiveMsg);
                    receiveNum++;
                }
                long nextBeginOffset = pullResult.getNextBeginOffset();
                putMessageQueueOffset(queue, nextBeginOffset);
            }
        }
        RocketMQSinkTest.log.info("receive message num={}", receiveNum);
        /* wait for processQueueTable init */
        Thread.sleep(1000);
        consumer.shutdown();
        Assert.assertEquals(msgs.size(), 0);
    }

    @Test
    public void testNullEvent() throws UnsupportedEncodingException, InterruptedException, EventDeliveryException, MQBrokerException, MQClientException, RemotingException {
        /* start sink */
        Context context = new Context();
        context.put(RocketMQSinkConstants.NAME_SERVER_CONFIG, RocketMQSinkTest.nameServer);
        context.put(RocketMQSinkConstants.TAG_CONFIG, tag);
        RocketMQSink sink = new RocketMQSink();
        Configurables.configure(sink, context);
        MemoryChannel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        sink.setChannel(channel);
        sink.start();
        Sink.Status status = sink.process();
        Assert.assertEquals(status, BACKOFF);
        sink.stop();
    }
}

