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


import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.rocketmq.client.ClientConfig;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.impl.MQClientAPIImpl;
import org.apache.rocketmq.client.impl.MQClientManager;
import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class DefaultMQProducerWithTraceTest {
    @Spy
    private MQClientInstance mQClientFactory = MQClientManager.getInstance().getAndCreateMQClientInstance(new ClientConfig());

    @Mock
    private MQClientAPIImpl mQClientAPIImpl;

    private AsyncTraceDispatcher asyncTraceDispatcher;

    private DefaultMQProducer producer;

    private DefaultMQProducer customTraceTopicproducer;

    private DefaultMQProducer traceProducer;

    private DefaultMQProducer normalProducer;

    private Message message;

    private String topic = "FooBar";

    private String producerGroupPrefix = "FooBar_PID";

    private String producerGroupTemp = (producerGroupPrefix) + (System.currentTimeMillis());

    private String producerGroupTraceTemp = (MixAll.RMQ_SYS_TRACE_TOPIC) + (System.currentTimeMillis());

    private String customerTraceTopic = "rmq_trace_topic_12345";

    @Test
    public void testSendMessageSync_WithTrace_Success() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        traceProducer.getDefaultMQProducerImpl().getmQClientFactory().registerProducer(producerGroupTraceTemp, traceProducer.getDefaultMQProducerImpl());
        Mockito.when(mQClientAPIImpl.getTopicRouteInfoFromNameServer(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(DefaultMQProducerWithTraceTest.createTopicRoute());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            producer.send(message);
        } catch (MQClientException e) {
        }
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSendMessageSync_WithTrace_NoBrokerSet_Exception() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        Mockito.when(mQClientAPIImpl.getTopicRouteInfoFromNameServer(ArgumentMatchers.anyString(), ArgumentMatchers.anyLong())).thenReturn(DefaultMQProducerWithTraceTest.createTopicRoute());
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            producer.send(message);
        } catch (MQClientException e) {
        }
        countDownLatch.await(3000L, TimeUnit.MILLISECONDS);
    }
}

