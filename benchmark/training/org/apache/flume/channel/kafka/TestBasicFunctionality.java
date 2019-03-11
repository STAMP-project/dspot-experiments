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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel.kafka;


import CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.GROUP_ID_CONFIG;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.conf.Configurables;
import org.apache.flume.instrumentation.kafka.KafkaChannelCounter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static KafkaChannelConfiguration.KAFKA_CONSUMER_PREFIX;
import static KafkaChannelConfiguration.KAFKA_PRODUCER_PREFIX;


public class TestBasicFunctionality extends TestKafkaChannelBase {
    @Test
    public void testProps() throws Exception {
        Context context = new Context();
        context.put("kafka.producer.some-parameter", "1");
        context.put("kafka.consumer.another-parameter", "1");
        context.put(KafkaChannelConfiguration.BOOTSTRAP_SERVERS_CONFIG, TestKafkaChannelBase.testUtil.getKafkaServerUrl());
        context.put(KafkaChannelConfiguration.TOPIC_CONFIG, topic);
        final KafkaChannel channel = new KafkaChannel();
        Configurables.configure(channel, context);
        Properties consumerProps = channel.getConsumerProps();
        Properties producerProps = channel.getProducerProps();
        Assert.assertEquals(producerProps.getProperty("some-parameter"), "1");
        Assert.assertEquals(consumerProps.getProperty("another-parameter"), "1");
    }

    @Test
    public void testOldConfig() throws Exception {
        Context context = new Context();
        context.put(KafkaChannelConfiguration.BROKER_LIST_FLUME_KEY, TestKafkaChannelBase.testUtil.getKafkaServerUrl());
        context.put(KafkaChannelConfiguration.GROUP_ID_FLUME, "flume-something");
        context.put(KafkaChannelConfiguration.READ_SMALLEST_OFFSET, "true");
        context.put("topic", topic);
        final KafkaChannel channel = new KafkaChannel();
        Configurables.configure(channel, context);
        Properties consumerProps = channel.getConsumerProps();
        Properties producerProps = channel.getProducerProps();
        Assert.assertEquals(producerProps.getProperty(BOOTSTRAP_SERVERS_CONFIG), TestKafkaChannelBase.testUtil.getKafkaServerUrl());
        Assert.assertEquals(consumerProps.getProperty(GROUP_ID_CONFIG), "flume-something");
        Assert.assertEquals(consumerProps.getProperty(AUTO_OFFSET_RESET_CONFIG), "earliest");
    }

    @Test
    public void testStopAndStart() throws Exception {
        doTestStopAndStart(false, false);
    }

    @Test
    public void testStopAndStartWithRollback() throws Exception {
        doTestStopAndStart(true, true);
    }

    @Test
    public void testStopAndStartWithRollbackAndNoRetry() throws Exception {
        doTestStopAndStart(true, false);
    }

    @Test
    public void testNullKeyNoHeader() throws Exception {
        doTestNullKeyNoHeader();
    }

    /**
     * Tests that sub-properties get set correctly if you run the configure() method twice
     * (fix for FLUME-2857)
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaultSettingsOnReConfigure() throws Exception {
        String sampleProducerProp = "compression.type";
        String sampleProducerVal = "snappy";
        String sampleConsumerProp = "fetch.min.bytes";
        String sampleConsumerVal = "99";
        Context context = prepareDefaultContext(false);
        context.put(((KAFKA_PRODUCER_PREFIX) + sampleProducerProp), sampleProducerVal);
        context.put(((KAFKA_CONSUMER_PREFIX) + sampleConsumerProp), sampleConsumerVal);
        final KafkaChannel channel = createChannel(context);
        Assert.assertEquals(sampleProducerVal, channel.getProducerProps().getProperty(sampleProducerProp));
        Assert.assertEquals(sampleConsumerVal, channel.getConsumerProps().getProperty(sampleConsumerProp));
        context = prepareDefaultContext(false);
        channel.configure(context);
        Assert.assertNull(channel.getProducerProps().getProperty(sampleProducerProp));
        Assert.assertNull(channel.getConsumerProps().getProperty(sampleConsumerProp));
    }

    @Test
    public void testMetricsCount() throws Exception {
        final KafkaChannel channel = startChannel(true);
        ExecutorService underlying = Executors.newCachedThreadPool();
        ExecutorCompletionService<Void> submitterSvc = new ExecutorCompletionService<Void>(underlying);
        final List<List<Event>> events = createBaseList();
        putEvents(channel, events, submitterSvc);
        takeEventsWithCommittingTxn(channel, 50);
        KafkaChannelCounter counter = ((KafkaChannelCounter) (Whitebox.getInternalState(channel, "counter")));
        Assert.assertEquals(50, counter.getEventPutAttemptCount());
        Assert.assertEquals(50, counter.getEventPutSuccessCount());
        Assert.assertEquals(50, counter.getEventTakeAttemptCount());
        Assert.assertEquals(50, counter.getEventTakeSuccessCount());
        channel.stop();
    }
}

