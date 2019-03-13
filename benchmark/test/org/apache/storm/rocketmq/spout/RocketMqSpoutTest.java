/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.rocketmq.spout;


import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.Message;
import org.apache.storm.rocketmq.ConsumerBatchMessage;
import org.apache.storm.rocketmq.RocketMqUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class RocketMqSpoutTest {
    private RocketMqSpout spout;

    private DefaultMQPushConsumer consumer;

    private SpoutOutputCollector collector;

    private BlockingQueue<ConsumerBatchMessage> queue;

    private Properties properties;

    @Test
    public void nextTuple() throws Exception {
        List<List<Object>> list = new ArrayList<>();
        list.add(RocketMqUtils.generateTuples(new Message("tpc", "body".getBytes()), RocketMqUtils.createScheme(properties)));
        ConsumerBatchMessage<List<Object>> batchMessage = new ConsumerBatchMessage(list);
        queue.put(batchMessage);
        spout.nextTuple();
        Mockito.verify(collector).emit(ArgumentMatchers.anyList(), ArgumentMatchers.anyString());
    }

    @Test
    public void close() throws Exception {
        spout.close();
        Mockito.verify(consumer).shutdown();
    }
}

