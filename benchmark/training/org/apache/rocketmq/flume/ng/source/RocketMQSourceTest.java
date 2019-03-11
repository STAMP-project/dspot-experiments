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
package org.apache.rocketmq.flume.ng.source;


import PollableSource.Status;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.flume.Channel;
import org.apache.flume.ChannelSelector;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.Transaction;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.channel.ReplicatingChannelSelector;
import org.apache.flume.conf.Configurables;
import org.apache.rocketmq.broker.BrokerController;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.namesrv.NamesrvController;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 */
public class RocketMQSourceTest {
    private static final Logger log = LoggerFactory.getLogger(RocketMQSourceTest.class);

    private static String nameServer = "localhost:9876";

    private static NamesrvController namesrvController;

    private static BrokerController brokerController;

    private String tag = ((RocketMQSourceConstants.TAG_DEFAULT) + "_SOURCE_TEST_") + (new Random().nextInt(99));

    private String producerGroup = "PRODUCER_GROUP_SOURCE_TEST";

    @Test
    public void testEvent() throws UnsupportedEncodingException, InterruptedException, EventDeliveryException, MQBrokerException, MQClientException {
        // publish test message
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(RocketMQSourceTest.nameServer);
        String sendMsg = ("\"Hello Flume\"" + ",") + (DateFormatUtils.format(new Date(), "yyyy-MM-DD hh:mm:ss"));
        try {
            producer.start();
            Message msg = new Message(RocketMQSourceConstants.TOPIC_DEFAULT, tag, sendMsg.getBytes("UTF-8"));
            SendResult sendResult = producer.send(msg);
            RocketMQSourceTest.log.info("publish message : {}, sendResult:{}", sendMsg, sendResult);
        } catch (Exception e) {
            throw new MQClientException("Failed to publish messages", e);
        } finally {
            producer.shutdown();
        }
        // start source
        Context context = new Context();
        context.put(RocketMQSourceConstants.NAME_SERVER_CONFIG, RocketMQSourceTest.nameServer);
        context.put(RocketMQSourceConstants.TAG_CONFIG, tag);
        Channel channel = new MemoryChannel();
        Configurables.configure(channel, context);
        List<Channel> channels = new ArrayList<>();
        channels.add(channel);
        ChannelSelector channelSelector = new ReplicatingChannelSelector();
        channelSelector.setChannels(channels);
        ChannelProcessor channelProcessor = new ChannelProcessor(channelSelector);
        RocketMQSource source = new RocketMQSource();
        source.setChannelProcessor(channelProcessor);
        Configurables.configure(source, context);
        source.start();
        PollableSource.Status status = source.process();
        if (status == (Status.BACKOFF)) {
            Assert.fail("Error");
        }
        /* wait for processQueueTable init */
        Thread.sleep(1000);
        source.stop();
        /* mock flume sink */
        Transaction transaction = channel.getTransaction();
        transaction.begin();
        Event event = channel.take();
        if (event == null) {
            transaction.commit();
            Assert.fail("Error");
        }
        byte[] body = event.getBody();
        String receiveMsg = new String(body, "UTF-8");
        RocketMQSourceTest.log.info("receive message : {}", receiveMsg);
        Assert.assertEquals(sendMsg, receiveMsg);
    }
}

