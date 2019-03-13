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


import LifecycleState.START;
import java.util.Arrays;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Test;


public class TestOffsetsAndMigration extends TestKafkaChannelBase {
    @Test
    public void testOffsetsNotCommittedOnStop() throws Exception {
        String message = "testOffsetsNotCommittedOnStop-" + (System.nanoTime());
        KafkaChannel channel = startChannel(false);
        KafkaProducer<String, byte[]> producer = new KafkaProducer(channel.getProducerProps());
        ProducerRecord<String, byte[]> data = new ProducerRecord(topic, ("header-" + message), message.getBytes());
        producer.send(data).get();
        producer.flush();
        producer.close();
        Event event = takeEventWithoutCommittingTxn(channel);
        Assert.assertNotNull(event);
        Assert.assertTrue(Arrays.equals(message.getBytes(), event.getBody()));
        // Stop the channel without committing the transaction
        channel.stop();
        channel = startChannel(false);
        // Message should still be available
        event = takeEventWithoutCommittingTxn(channel);
        Assert.assertNotNull(event);
        Assert.assertTrue(Arrays.equals(message.getBytes(), event.getBody()));
    }

    @Test
    public void testMigrateOffsetsNone() throws Exception {
        doTestMigrateZookeeperOffsets(false, false, "testMigrateOffsets-none");
    }

    @Test
    public void testMigrateOffsetsZookeeper() throws Exception {
        doTestMigrateZookeeperOffsets(true, false, "testMigrateOffsets-zookeeper");
    }

    @Test
    public void testMigrateOffsetsKafka() throws Exception {
        doTestMigrateZookeeperOffsets(false, true, "testMigrateOffsets-kafka");
    }

    @Test
    public void testMigrateOffsetsBoth() throws Exception {
        doTestMigrateZookeeperOffsets(true, true, "testMigrateOffsets-both");
    }

    @Test
    public void testMigrateZookeeperOffsetsWhenTopicNotExists() throws Exception {
        topic = findUnusedTopic();
        Context context = prepareDefaultContext(false);
        context.put(KafkaChannelConfiguration.ZOOKEEPER_CONNECT_FLUME_KEY, TestKafkaChannelBase.testUtil.getZkUrl());
        context.put(KafkaChannelConfiguration.GROUP_ID_FLUME, "testMigrateOffsets-nonExistingTopic");
        KafkaChannel channel = createChannel(context);
        channel.start();
        Assert.assertEquals(START, channel.getLifecycleState());
        channel.stop();
    }
}

