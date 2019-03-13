/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.connect.util;


import CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import Errors.COORDINATOR_NOT_AVAILABLE;
import ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.LeaderNotAvailableException;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.common.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.api.easymock.annotation.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


@RunWith(PowerMockRunner.class)
@PrepareForTest(KafkaBasedLog.class)
@PowerMockIgnore("javax.management.*")
public class KafkaBasedLogTest {
    private static final String TOPIC = "connect-log";

    private static final TopicPartition TP0 = new TopicPartition(KafkaBasedLogTest.TOPIC, 0);

    private static final TopicPartition TP1 = new TopicPartition(KafkaBasedLogTest.TOPIC, 1);

    private static final Map<String, Object> PRODUCER_PROPS = new HashMap<>();

    static {
        KafkaBasedLogTest.PRODUCER_PROPS.put(BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9093");
        KafkaBasedLogTest.PRODUCER_PROPS.put(KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        KafkaBasedLogTest.PRODUCER_PROPS.put(VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    }

    private static final Map<String, Object> CONSUMER_PROPS = new HashMap<>();

    static {
        KafkaBasedLogTest.CONSUMER_PROPS.put(BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9093");
        KafkaBasedLogTest.CONSUMER_PROPS.put(KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaBasedLogTest.CONSUMER_PROPS.put(VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
    }

    private static final Set<TopicPartition> CONSUMER_ASSIGNMENT = new java.util.HashSet(Arrays.asList(KafkaBasedLogTest.TP0, KafkaBasedLogTest.TP1));

    private static final Map<String, String> FIRST_SET = new HashMap<>();

    static {
        KafkaBasedLogTest.FIRST_SET.put("key", "value");
        KafkaBasedLogTest.FIRST_SET.put(null, null);
    }

    private static final Node LEADER = new Node(1, "broker1", 9092);

    private static final Node REPLICA = new Node(1, "broker2", 9093);

    private static final PartitionInfo TPINFO0 = new PartitionInfo(KafkaBasedLogTest.TOPIC, 0, KafkaBasedLogTest.LEADER, new Node[]{ KafkaBasedLogTest.REPLICA }, new Node[]{ KafkaBasedLogTest.REPLICA });

    private static final PartitionInfo TPINFO1 = new PartitionInfo(KafkaBasedLogTest.TOPIC, 1, KafkaBasedLogTest.LEADER, new Node[]{ KafkaBasedLogTest.REPLICA }, new Node[]{ KafkaBasedLogTest.REPLICA });

    private static final String TP0_KEY = "TP0KEY";

    private static final String TP1_KEY = "TP1KEY";

    private static final String TP0_VALUE = "VAL0";

    private static final String TP1_VALUE = "VAL1";

    private static final String TP0_VALUE_NEW = "VAL0_NEW";

    private static final String TP1_VALUE_NEW = "VAL1_NEW";

    private Time time = new MockTime();

    private KafkaBasedLog<String, String> store;

    @Mock
    private Runnable initializer;

    @Mock
    private KafkaProducer<String, String> producer;

    private MockConsumer<String, String> consumer;

    private Map<TopicPartition, List<ConsumerRecord<String, String>>> consumedRecords = new HashMap<>();

    private Callback<ConsumerRecord<String, String>> consumedCallback = new Callback<ConsumerRecord<String, String>>() {
        @Override
        public void onCompletion(Throwable error, ConsumerRecord<String, String> record) {
            TopicPartition partition = new TopicPartition(record.topic(), record.partition());
            List<ConsumerRecord<String, String>> records = consumedRecords.get(partition);
            if (records == null) {
                records = new ArrayList();
                consumedRecords.put(partition, records);
            }
            records.add(record);
        }
    };

    @Test
    public void testStartStop() throws Exception {
        expectStart();
        expectStop();
        PowerMock.replayAll();
        Map<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(KafkaBasedLogTest.TP0, 0L);
        endOffsets.put(KafkaBasedLogTest.TP1, 0L);
        consumer.updateEndOffsets(endOffsets);
        store.start();
        Assert.assertEquals(KafkaBasedLogTest.CONSUMER_ASSIGNMENT, consumer.assignment());
        store.stop();
        Assert.assertFalse(Whitebox.<Thread>getInternalState(store, "thread").isAlive());
        Assert.assertTrue(consumer.closed());
        PowerMock.verifyAll();
    }

    @Test
    public void testReloadOnStart() throws Exception {
        expectStart();
        expectStop();
        PowerMock.replayAll();
        Map<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(KafkaBasedLogTest.TP0, 1L);
        endOffsets.put(KafkaBasedLogTest.TP1, 1L);
        consumer.updateEndOffsets(endOffsets);
        final CountDownLatch finishedLatch = new CountDownLatch(1);
        consumer.schedulePollTask(new Runnable() {
            // Use first poll task to setup sequence of remaining responses to polls
            @Override
            public void run() {
                // Should keep polling until it reaches current log end offset for all partitions. Should handle
                // as many empty polls as needed
                consumer.scheduleNopPollTask();
                consumer.scheduleNopPollTask();
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE));
                    }
                });
                consumer.scheduleNopPollTask();
                consumer.scheduleNopPollTask();
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP1_KEY, KafkaBasedLogTest.TP1_VALUE));
                    }
                });
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        finishedLatch.countDown();
                    }
                });
            }
        });
        store.start();
        Assert.assertTrue(finishedLatch.await(10000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(KafkaBasedLogTest.CONSUMER_ASSIGNMENT, consumer.assignment());
        Assert.assertEquals(2, consumedRecords.size());
        Assert.assertEquals(KafkaBasedLogTest.TP0_VALUE, consumedRecords.get(KafkaBasedLogTest.TP0).get(0).value());
        Assert.assertEquals(KafkaBasedLogTest.TP1_VALUE, consumedRecords.get(KafkaBasedLogTest.TP1).get(0).value());
        store.stop();
        Assert.assertFalse(Whitebox.<Thread>getInternalState(store, "thread").isAlive());
        Assert.assertTrue(consumer.closed());
        PowerMock.verifyAll();
    }

    @Test
    public void testReloadOnStartWithNoNewRecordsPresent() throws Exception {
        expectStart();
        expectStop();
        PowerMock.replayAll();
        Map<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(KafkaBasedLogTest.TP0, 7L);
        endOffsets.put(KafkaBasedLogTest.TP1, 7L);
        consumer.updateEndOffsets(endOffsets);
        // Better test with an advanced offset other than just 0L
        consumer.updateBeginningOffsets(endOffsets);
        consumer.schedulePollTask(new Runnable() {
            @Override
            public void run() {
                // Throw an exception that will not be ignored or handled by Connect framework. In
                // reality a misplaced call to poll blocks indefinitely and connect aborts due to
                // time outs (for instance via ConnectRestException)
                throw new WakeupException();
            }
        });
        store.start();
        Assert.assertEquals(KafkaBasedLogTest.CONSUMER_ASSIGNMENT, consumer.assignment());
        Assert.assertEquals(7L, consumer.position(KafkaBasedLogTest.TP0));
        Assert.assertEquals(7L, consumer.position(KafkaBasedLogTest.TP1));
        store.stop();
        Assert.assertFalse(Whitebox.<Thread>getInternalState(store, "thread").isAlive());
        Assert.assertTrue(consumer.closed());
        PowerMock.verifyAll();
    }

    @Test
    public void testSendAndReadToEnd() throws Exception {
        expectStart();
        TestFuture<RecordMetadata> tp0Future = new TestFuture<>();
        ProducerRecord<String, String> tp0Record = new ProducerRecord(KafkaBasedLogTest.TOPIC, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE);
        Capture<org.apache.kafka.clients.producer.Callback> callback0 = EasyMock.newCapture();
        EasyMock.expect(producer.send(EasyMock.eq(tp0Record), EasyMock.capture(callback0))).andReturn(tp0Future);
        TestFuture<RecordMetadata> tp1Future = new TestFuture<>();
        ProducerRecord<String, String> tp1Record = new ProducerRecord(KafkaBasedLogTest.TOPIC, KafkaBasedLogTest.TP1_KEY, KafkaBasedLogTest.TP1_VALUE);
        Capture<org.apache.kafka.clients.producer.Callback> callback1 = EasyMock.newCapture();
        EasyMock.expect(producer.send(EasyMock.eq(tp1Record), EasyMock.capture(callback1))).andReturn(tp1Future);
        // Producer flushes when read to log end is called
        producer.flush();
        PowerMock.expectLastCall();
        expectStop();
        PowerMock.replayAll();
        Map<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(KafkaBasedLogTest.TP0, 0L);
        endOffsets.put(KafkaBasedLogTest.TP1, 0L);
        consumer.updateEndOffsets(endOffsets);
        store.start();
        Assert.assertEquals(KafkaBasedLogTest.CONSUMER_ASSIGNMENT, consumer.assignment());
        Assert.assertEquals(0L, consumer.position(KafkaBasedLogTest.TP0));
        Assert.assertEquals(0L, consumer.position(KafkaBasedLogTest.TP1));
        // Set some keys
        final AtomicInteger invoked = new AtomicInteger(0);
        org.apache.kafka.clients.producer.Callback producerCallback = new org.apache.kafka.clients.producer.Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                invoked.incrementAndGet();
            }
        };
        store.send(KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE, producerCallback);
        store.send(KafkaBasedLogTest.TP1_KEY, KafkaBasedLogTest.TP1_VALUE, producerCallback);
        Assert.assertEquals(0, invoked.get());
        tp1Future.resolve(((RecordMetadata) (null)));// Output not used, so safe to not return a real value for testing

        callback1.getValue().onCompletion(null, null);
        Assert.assertEquals(1, invoked.get());
        tp0Future.resolve(((RecordMetadata) (null)));
        callback0.getValue().onCompletion(null, null);
        Assert.assertEquals(2, invoked.get());
        // Now we should have to wait for the records to be read back when we call readToEnd()
        final AtomicBoolean getInvoked = new AtomicBoolean(false);
        final FutureCallback<Void> readEndFutureCallback = new FutureCallback(new Callback<Void>() {
            @Override
            public void onCompletion(Throwable error, Void result) {
                getInvoked.set(true);
            }
        });
        consumer.schedulePollTask(new Runnable() {
            @Override
            public void run() {
                // Once we're synchronized in a poll, start the read to end and schedule the exact set of poll events
                // that should follow. This readToEnd call will immediately wakeup this consumer.poll() call without
                // returning any data.
                Map<TopicPartition, Long> newEndOffsets = new HashMap<>();
                newEndOffsets.put(KafkaBasedLogTest.TP0, 2L);
                newEndOffsets.put(KafkaBasedLogTest.TP1, 2L);
                consumer.updateEndOffsets(newEndOffsets);
                store.readToEnd(readEndFutureCallback);
                // Should keep polling until it reaches current log end offset for all partitions
                consumer.scheduleNopPollTask();
                consumer.scheduleNopPollTask();
                consumer.scheduleNopPollTask();
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE));
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE_NEW));
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP1_KEY, KafkaBasedLogTest.TP1_VALUE));
                    }
                });
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP1_KEY, KafkaBasedLogTest.TP1_VALUE_NEW));
                    }
                });
                // Already have FutureCallback that should be invoked/awaited, so no need for follow up finishedLatch
            }
        });
        readEndFutureCallback.get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(getInvoked.get());
        Assert.assertEquals(2, consumedRecords.size());
        Assert.assertEquals(2, consumedRecords.get(KafkaBasedLogTest.TP0).size());
        Assert.assertEquals(KafkaBasedLogTest.TP0_VALUE, consumedRecords.get(KafkaBasedLogTest.TP0).get(0).value());
        Assert.assertEquals(KafkaBasedLogTest.TP0_VALUE_NEW, consumedRecords.get(KafkaBasedLogTest.TP0).get(1).value());
        Assert.assertEquals(2, consumedRecords.get(KafkaBasedLogTest.TP1).size());
        Assert.assertEquals(KafkaBasedLogTest.TP1_VALUE, consumedRecords.get(KafkaBasedLogTest.TP1).get(0).value());
        Assert.assertEquals(KafkaBasedLogTest.TP1_VALUE_NEW, consumedRecords.get(KafkaBasedLogTest.TP1).get(1).value());
        // Cleanup
        store.stop();
        Assert.assertFalse(Whitebox.<Thread>getInternalState(store, "thread").isAlive());
        Assert.assertTrue(consumer.closed());
        PowerMock.verifyAll();
    }

    @Test
    public void testConsumerError() throws Exception {
        expectStart();
        expectStop();
        PowerMock.replayAll();
        final CountDownLatch finishedLatch = new CountDownLatch(1);
        Map<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(KafkaBasedLogTest.TP0, 1L);
        endOffsets.put(KafkaBasedLogTest.TP1, 1L);
        consumer.updateEndOffsets(endOffsets);
        consumer.schedulePollTask(new Runnable() {
            @Override
            public void run() {
                // Trigger exception
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        consumer.setException(COORDINATOR_NOT_AVAILABLE.exception());
                    }
                });
                // Should keep polling until it reaches current log end offset for all partitions
                consumer.scheduleNopPollTask();
                consumer.scheduleNopPollTask();
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE_NEW));
                        consumer.addRecord(new ConsumerRecord(KafkaBasedLogTest.TOPIC, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE_NEW));
                    }
                });
                consumer.schedulePollTask(new Runnable() {
                    @Override
                    public void run() {
                        finishedLatch.countDown();
                    }
                });
            }
        });
        store.start();
        Assert.assertTrue(finishedLatch.await(10000, TimeUnit.MILLISECONDS));
        Assert.assertEquals(KafkaBasedLogTest.CONSUMER_ASSIGNMENT, consumer.assignment());
        Assert.assertEquals(1L, consumer.position(KafkaBasedLogTest.TP0));
        store.stop();
        Assert.assertFalse(Whitebox.<Thread>getInternalState(store, "thread").isAlive());
        Assert.assertTrue(consumer.closed());
        PowerMock.verifyAll();
    }

    @Test
    public void testProducerError() throws Exception {
        expectStart();
        TestFuture<RecordMetadata> tp0Future = new TestFuture<>();
        ProducerRecord<String, String> tp0Record = new ProducerRecord(KafkaBasedLogTest.TOPIC, KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE);
        Capture<org.apache.kafka.clients.producer.Callback> callback0 = EasyMock.newCapture();
        EasyMock.expect(producer.send(EasyMock.eq(tp0Record), EasyMock.capture(callback0))).andReturn(tp0Future);
        expectStop();
        PowerMock.replayAll();
        Map<TopicPartition, Long> endOffsets = new HashMap<>();
        endOffsets.put(KafkaBasedLogTest.TP0, 0L);
        endOffsets.put(KafkaBasedLogTest.TP1, 0L);
        consumer.updateEndOffsets(endOffsets);
        store.start();
        Assert.assertEquals(KafkaBasedLogTest.CONSUMER_ASSIGNMENT, consumer.assignment());
        Assert.assertEquals(0L, consumer.position(KafkaBasedLogTest.TP0));
        Assert.assertEquals(0L, consumer.position(KafkaBasedLogTest.TP1));
        final AtomicReference<Throwable> setException = new AtomicReference<>();
        store.send(KafkaBasedLogTest.TP0_KEY, KafkaBasedLogTest.TP0_VALUE, new org.apache.kafka.clients.producer.Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {
                Assert.assertNull(setException.get());// Should only be invoked once

                setException.set(exception);
            }
        });
        KafkaException exc = new LeaderNotAvailableException("Error");
        tp0Future.resolve(exc);
        callback0.getValue().onCompletion(null, exc);
        Assert.assertNotNull(setException.get());
        store.stop();
        Assert.assertFalse(Whitebox.<Thread>getInternalState(store, "thread").isAlive());
        Assert.assertTrue(consumer.closed());
        PowerMock.verifyAll();
    }
}

