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
package org.apache.kafka.connect.storage;


import CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import DistributedConfig.CONFIG_STORAGE_REPLICATION_FACTOR_CONFIG;
import DistributedConfig.CONFIG_TOPIC_CONFIG;
import DistributedConfig.GROUP_ID_CONFIG;
import DistributedConfig.INTERNAL_KEY_CONVERTER_CLASS_CONFIG;
import DistributedConfig.INTERNAL_VALUE_CONVERTER_CLASS_CONFIG;
import DistributedConfig.KEY_CONVERTER_CLASS_CONFIG;
import DistributedConfig.OFFSET_STORAGE_PARTITIONS_CONFIG;
import DistributedConfig.OFFSET_STORAGE_REPLICATION_FACTOR_CONFIG;
import DistributedConfig.OFFSET_STORAGE_TOPIC_CONFIG;
import DistributedConfig.STATUS_STORAGE_TOPIC_CONFIG;
import DistributedConfig.VALUE_CONVERTER_CLASS_CONFIG;
import ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.record.TimestampType;
import org.apache.kafka.connect.runtime.distributed.DistributedConfig;
import org.apache.kafka.connect.util.Callback;
import org.apache.kafka.connect.util.KafkaBasedLog;
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
@PrepareForTest(KafkaOffsetBackingStore.class)
@PowerMockIgnore("javax.management.*")
@SuppressWarnings({ "unchecked", "deprecation" })
public class KafkaOffsetBackingStoreTest {
    private static final String TOPIC = "connect-offsets";

    private static final short TOPIC_PARTITIONS = 2;

    private static final short TOPIC_REPLICATION_FACTOR = 5;

    private static final Map<String, String> DEFAULT_PROPS = new HashMap<>();

    private static final DistributedConfig DEFAULT_DISTRIBUTED_CONFIG;

    static {
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(BOOTSTRAP_SERVERS_CONFIG, "broker1:9092,broker2:9093");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(OFFSET_STORAGE_TOPIC_CONFIG, KafkaOffsetBackingStoreTest.TOPIC);
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(OFFSET_STORAGE_REPLICATION_FACTOR_CONFIG, Short.toString(KafkaOffsetBackingStoreTest.TOPIC_REPLICATION_FACTOR));
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(OFFSET_STORAGE_PARTITIONS_CONFIG, Integer.toString(KafkaOffsetBackingStoreTest.TOPIC_PARTITIONS));
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(CONFIG_TOPIC_CONFIG, "connect-configs");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(CONFIG_STORAGE_REPLICATION_FACTOR_CONFIG, Short.toString(KafkaOffsetBackingStoreTest.TOPIC_REPLICATION_FACTOR));
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(GROUP_ID_CONFIG, "connect");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(STATUS_STORAGE_TOPIC_CONFIG, "status-topic");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(INTERNAL_KEY_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        KafkaOffsetBackingStoreTest.DEFAULT_PROPS.put(INTERNAL_VALUE_CONVERTER_CLASS_CONFIG, "org.apache.kafka.connect.json.JsonConverter");
        DEFAULT_DISTRIBUTED_CONFIG = new DistributedConfig(KafkaOffsetBackingStoreTest.DEFAULT_PROPS);
    }

    private static final Map<ByteBuffer, ByteBuffer> FIRST_SET = new HashMap<>();

    static {
        KafkaOffsetBackingStoreTest.FIRST_SET.put(KafkaOffsetBackingStoreTest.buffer("key"), KafkaOffsetBackingStoreTest.buffer("value"));
        KafkaOffsetBackingStoreTest.FIRST_SET.put(null, null);
    }

    private static final ByteBuffer TP0_KEY = KafkaOffsetBackingStoreTest.buffer("TP0KEY");

    private static final ByteBuffer TP1_KEY = KafkaOffsetBackingStoreTest.buffer("TP1KEY");

    private static final ByteBuffer TP2_KEY = KafkaOffsetBackingStoreTest.buffer("TP2KEY");

    private static final ByteBuffer TP0_VALUE = KafkaOffsetBackingStoreTest.buffer("VAL0");

    private static final ByteBuffer TP1_VALUE = KafkaOffsetBackingStoreTest.buffer("VAL1");

    private static final ByteBuffer TP2_VALUE = KafkaOffsetBackingStoreTest.buffer("VAL2");

    private static final ByteBuffer TP0_VALUE_NEW = KafkaOffsetBackingStoreTest.buffer("VAL0_NEW");

    private static final ByteBuffer TP1_VALUE_NEW = KafkaOffsetBackingStoreTest.buffer("VAL1_NEW");

    @Mock
    KafkaBasedLog<byte[], byte[]> storeLog;

    private KafkaOffsetBackingStore store;

    private Capture<String> capturedTopic = EasyMock.newCapture();

    private Capture<Map<String, Object>> capturedProducerProps = EasyMock.newCapture();

    private Capture<Map<String, Object>> capturedConsumerProps = EasyMock.newCapture();

    private Capture<Map<String, Object>> capturedAdminProps = EasyMock.newCapture();

    private Capture<NewTopic> capturedNewTopic = EasyMock.newCapture();

    private Capture<Callback<ConsumerRecord<byte[], byte[]>>> capturedConsumedCallback = EasyMock.newCapture();

    @Test
    public void testStartStop() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList());
        expectStop();
        PowerMock.replayAll();
        store.configure(KafkaOffsetBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        Assert.assertEquals(KafkaOffsetBackingStoreTest.TOPIC, capturedTopic.getValue());
        Assert.assertEquals("org.apache.kafka.common.serialization.ByteArraySerializer", capturedProducerProps.getValue().get(KEY_SERIALIZER_CLASS_CONFIG));
        Assert.assertEquals("org.apache.kafka.common.serialization.ByteArraySerializer", capturedProducerProps.getValue().get(VALUE_SERIALIZER_CLASS_CONFIG));
        Assert.assertEquals("org.apache.kafka.common.serialization.ByteArrayDeserializer", capturedConsumerProps.getValue().get(KEY_DESERIALIZER_CLASS_CONFIG));
        Assert.assertEquals("org.apache.kafka.common.serialization.ByteArrayDeserializer", capturedConsumerProps.getValue().get(VALUE_DESERIALIZER_CLASS_CONFIG));
        Assert.assertEquals(KafkaOffsetBackingStoreTest.TOPIC, capturedNewTopic.getValue().name());
        Assert.assertEquals(KafkaOffsetBackingStoreTest.TOPIC_PARTITIONS, capturedNewTopic.getValue().numPartitions());
        Assert.assertEquals(KafkaOffsetBackingStoreTest.TOPIC_REPLICATION_FACTOR, capturedNewTopic.getValue().replicationFactor());
        store.start();
        store.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testReloadOnStart() throws Exception {
        expectConfigure();
        expectStart(Arrays.asList(new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP0_KEY.array(), KafkaOffsetBackingStoreTest.TP0_VALUE.array()), new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP1_KEY.array(), KafkaOffsetBackingStoreTest.TP1_VALUE.array()), new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP0_KEY.array(), KafkaOffsetBackingStoreTest.TP0_VALUE_NEW.array()), new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP1_KEY.array(), KafkaOffsetBackingStoreTest.TP1_VALUE_NEW.array())));
        expectStop();
        PowerMock.replayAll();
        store.configure(KafkaOffsetBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        store.start();
        HashMap<ByteBuffer, ByteBuffer> data = Whitebox.getInternalState(store, "data");
        Assert.assertEquals(KafkaOffsetBackingStoreTest.TP0_VALUE_NEW, data.get(KafkaOffsetBackingStoreTest.TP0_KEY));
        Assert.assertEquals(KafkaOffsetBackingStoreTest.TP1_VALUE_NEW, data.get(KafkaOffsetBackingStoreTest.TP1_KEY));
        store.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testGetSet() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList());
        expectStop();
        // First get() against an empty store
        final Capture<Callback<Void>> firstGetReadToEndCallback = EasyMock.newCapture();
        storeLog.readToEnd(EasyMock.capture(firstGetReadToEndCallback));
        PowerMock.expectLastCall().andAnswer(() -> {
            firstGetReadToEndCallback.getValue().onCompletion(null, null);
            return null;
        });
        // Set offsets
        Capture<org.apache.kafka.clients.producer.Callback> callback0 = EasyMock.newCapture();
        storeLog.send(EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP0_KEY.array()), EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP0_VALUE.array()), EasyMock.capture(callback0));
        PowerMock.expectLastCall();
        Capture<org.apache.kafka.clients.producer.Callback> callback1 = EasyMock.newCapture();
        storeLog.send(EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP1_KEY.array()), EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP1_VALUE.array()), EasyMock.capture(callback1));
        PowerMock.expectLastCall();
        // Second get() should get the produced data and return the new values
        final Capture<Callback<Void>> secondGetReadToEndCallback = EasyMock.newCapture();
        storeLog.readToEnd(EasyMock.capture(secondGetReadToEndCallback));
        PowerMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                capturedConsumedCallback.getValue().onCompletion(null, new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP0_KEY.array(), KafkaOffsetBackingStoreTest.TP0_VALUE.array()));
                capturedConsumedCallback.getValue().onCompletion(null, new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP1_KEY.array(), KafkaOffsetBackingStoreTest.TP1_VALUE.array()));
                secondGetReadToEndCallback.getValue().onCompletion(null, null);
                return null;
            }
        });
        // Third get() should pick up data produced by someone else and return those values
        final Capture<Callback<Void>> thirdGetReadToEndCallback = EasyMock.newCapture();
        storeLog.readToEnd(EasyMock.capture(thirdGetReadToEndCallback));
        PowerMock.expectLastCall().andAnswer(new org.easymock.IAnswer<Object>() {
            @Override
            public Object answer() throws Throwable {
                capturedConsumedCallback.getValue().onCompletion(null, new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 0, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP0_KEY.array(), KafkaOffsetBackingStoreTest.TP0_VALUE_NEW.array()));
                capturedConsumedCallback.getValue().onCompletion(null, new ConsumerRecord(KafkaOffsetBackingStoreTest.TOPIC, 1, 1, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, KafkaOffsetBackingStoreTest.TP1_KEY.array(), KafkaOffsetBackingStoreTest.TP1_VALUE_NEW.array()));
                thirdGetReadToEndCallback.getValue().onCompletion(null, null);
                return null;
            }
        });
        PowerMock.replayAll();
        store.configure(KafkaOffsetBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        store.start();
        // Getting from empty store should return nulls
        final AtomicBoolean getInvokedAndPassed = new AtomicBoolean(false);
        store.get(Arrays.asList(KafkaOffsetBackingStoreTest.TP0_KEY, KafkaOffsetBackingStoreTest.TP1_KEY), new Callback<Map<ByteBuffer, ByteBuffer>>() {
            @Override
            public void onCompletion(Throwable error, Map<ByteBuffer, ByteBuffer> result) {
                // Since we didn't read them yet, these will be null
                Assert.assertEquals(null, result.get(KafkaOffsetBackingStoreTest.TP0_KEY));
                Assert.assertEquals(null, result.get(KafkaOffsetBackingStoreTest.TP1_KEY));
                getInvokedAndPassed.set(true);
            }
        }).get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(getInvokedAndPassed.get());
        // Set some offsets
        Map<ByteBuffer, ByteBuffer> toSet = new HashMap<>();
        toSet.put(KafkaOffsetBackingStoreTest.TP0_KEY, KafkaOffsetBackingStoreTest.TP0_VALUE);
        toSet.put(KafkaOffsetBackingStoreTest.TP1_KEY, KafkaOffsetBackingStoreTest.TP1_VALUE);
        final AtomicBoolean invoked = new AtomicBoolean(false);
        Future<Void> setFuture = store.set(toSet, new Callback<Void>() {
            @Override
            public void onCompletion(Throwable error, Void result) {
                invoked.set(true);
            }
        });
        Assert.assertFalse(setFuture.isDone());
        // Out of order callbacks shouldn't matter, should still require all to be invoked before invoking the callback
        // for the store's set callback
        callback1.getValue().onCompletion(null, null);
        Assert.assertFalse(invoked.get());
        callback0.getValue().onCompletion(null, null);
        setFuture.get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(invoked.get());
        // Getting data should read to end of our published data and return it
        final AtomicBoolean secondGetInvokedAndPassed = new AtomicBoolean(false);
        store.get(Arrays.asList(KafkaOffsetBackingStoreTest.TP0_KEY, KafkaOffsetBackingStoreTest.TP1_KEY), new Callback<Map<ByteBuffer, ByteBuffer>>() {
            @Override
            public void onCompletion(Throwable error, Map<ByteBuffer, ByteBuffer> result) {
                Assert.assertEquals(KafkaOffsetBackingStoreTest.TP0_VALUE, result.get(KafkaOffsetBackingStoreTest.TP0_KEY));
                Assert.assertEquals(KafkaOffsetBackingStoreTest.TP1_VALUE, result.get(KafkaOffsetBackingStoreTest.TP1_KEY));
                secondGetInvokedAndPassed.set(true);
            }
        }).get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(secondGetInvokedAndPassed.get());
        // Getting data should read to end of our published data and return it
        final AtomicBoolean thirdGetInvokedAndPassed = new AtomicBoolean(false);
        store.get(Arrays.asList(KafkaOffsetBackingStoreTest.TP0_KEY, KafkaOffsetBackingStoreTest.TP1_KEY), new Callback<Map<ByteBuffer, ByteBuffer>>() {
            @Override
            public void onCompletion(Throwable error, Map<ByteBuffer, ByteBuffer> result) {
                Assert.assertEquals(KafkaOffsetBackingStoreTest.TP0_VALUE_NEW, result.get(KafkaOffsetBackingStoreTest.TP0_KEY));
                Assert.assertEquals(KafkaOffsetBackingStoreTest.TP1_VALUE_NEW, result.get(KafkaOffsetBackingStoreTest.TP1_KEY));
                thirdGetInvokedAndPassed.set(true);
            }
        }).get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(thirdGetInvokedAndPassed.get());
        store.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testGetSetNull() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList());
        // Set offsets
        Capture<org.apache.kafka.clients.producer.Callback> callback0 = EasyMock.newCapture();
        storeLog.send(EasyMock.isNull(byte[].class), EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP0_VALUE.array()), EasyMock.capture(callback0));
        PowerMock.expectLastCall();
        Capture<org.apache.kafka.clients.producer.Callback> callback1 = EasyMock.newCapture();
        storeLog.send(EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP1_KEY.array()), EasyMock.isNull(byte[].class), EasyMock.capture(callback1));
        PowerMock.expectLastCall();
        // Second get() should get the produced data and return the new values
        final Capture<Callback<Void>> secondGetReadToEndCallback = EasyMock.newCapture();
        storeLog.readToEnd(EasyMock.capture(secondGetReadToEndCallback));
        PowerMock.expectLastCall().andAnswer(() -> {
            capturedConsumedCallback.getValue().onCompletion(null, new ConsumerRecord<>(TOPIC, 0, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, ((byte[]) (null)), TP0_VALUE.array()));
            capturedConsumedCallback.getValue().onCompletion(null, new ConsumerRecord<>(TOPIC, 1, 0, 0L, TimestampType.CREATE_TIME, 0L, 0, 0, TP1_KEY.array(), ((byte[]) (null))));
            secondGetReadToEndCallback.getValue().onCompletion(null, null);
            return null;
        });
        expectStop();
        PowerMock.replayAll();
        store.configure(KafkaOffsetBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        store.start();
        // Set offsets using null keys and values
        Map<ByteBuffer, ByteBuffer> toSet = new HashMap<>();
        toSet.put(null, KafkaOffsetBackingStoreTest.TP0_VALUE);
        toSet.put(KafkaOffsetBackingStoreTest.TP1_KEY, null);
        final AtomicBoolean invoked = new AtomicBoolean(false);
        Future<Void> setFuture = store.set(toSet, new Callback<Void>() {
            @Override
            public void onCompletion(Throwable error, Void result) {
                invoked.set(true);
            }
        });
        Assert.assertFalse(setFuture.isDone());
        // Out of order callbacks shouldn't matter, should still require all to be invoked before invoking the callback
        // for the store's set callback
        callback1.getValue().onCompletion(null, null);
        Assert.assertFalse(invoked.get());
        callback0.getValue().onCompletion(null, null);
        setFuture.get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(invoked.get());
        // Getting data should read to end of our published data and return it
        final AtomicBoolean secondGetInvokedAndPassed = new AtomicBoolean(false);
        store.get(Arrays.asList(null, KafkaOffsetBackingStoreTest.TP1_KEY), new Callback<Map<ByteBuffer, ByteBuffer>>() {
            @Override
            public void onCompletion(Throwable error, Map<ByteBuffer, ByteBuffer> result) {
                Assert.assertEquals(KafkaOffsetBackingStoreTest.TP0_VALUE, result.get(null));
                Assert.assertNull(result.get(KafkaOffsetBackingStoreTest.TP1_KEY));
                secondGetInvokedAndPassed.set(true);
            }
        }).get(10000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(secondGetInvokedAndPassed.get());
        store.stop();
        PowerMock.verifyAll();
    }

    @Test
    public void testSetFailure() throws Exception {
        expectConfigure();
        expectStart(Collections.emptyList());
        expectStop();
        // Set offsets
        Capture<org.apache.kafka.clients.producer.Callback> callback0 = EasyMock.newCapture();
        storeLog.send(EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP0_KEY.array()), EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP0_VALUE.array()), EasyMock.capture(callback0));
        PowerMock.expectLastCall();
        Capture<org.apache.kafka.clients.producer.Callback> callback1 = EasyMock.newCapture();
        storeLog.send(EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP1_KEY.array()), EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP1_VALUE.array()), EasyMock.capture(callback1));
        PowerMock.expectLastCall();
        Capture<org.apache.kafka.clients.producer.Callback> callback2 = EasyMock.newCapture();
        storeLog.send(EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP2_KEY.array()), EasyMock.aryEq(KafkaOffsetBackingStoreTest.TP2_VALUE.array()), EasyMock.capture(callback2));
        PowerMock.expectLastCall();
        PowerMock.replayAll();
        store.configure(KafkaOffsetBackingStoreTest.DEFAULT_DISTRIBUTED_CONFIG);
        store.start();
        // Set some offsets
        Map<ByteBuffer, ByteBuffer> toSet = new HashMap<>();
        toSet.put(KafkaOffsetBackingStoreTest.TP0_KEY, KafkaOffsetBackingStoreTest.TP0_VALUE);
        toSet.put(KafkaOffsetBackingStoreTest.TP1_KEY, KafkaOffsetBackingStoreTest.TP1_VALUE);
        toSet.put(KafkaOffsetBackingStoreTest.TP2_KEY, KafkaOffsetBackingStoreTest.TP2_VALUE);
        final AtomicBoolean invoked = new AtomicBoolean(false);
        final AtomicBoolean invokedFailure = new AtomicBoolean(false);
        Future<Void> setFuture = store.set(toSet, new Callback<Void>() {
            @Override
            public void onCompletion(Throwable error, Void result) {
                invoked.set(true);
                if (error != null)
                    invokedFailure.set(true);

            }
        });
        Assert.assertFalse(setFuture.isDone());
        // Out of order callbacks shouldn't matter, should still require all to be invoked before invoking the callback
        // for the store's set callback
        callback1.getValue().onCompletion(null, null);
        Assert.assertFalse(invoked.get());
        callback2.getValue().onCompletion(null, new KafkaException("bogus error"));
        Assert.assertTrue(invoked.get());
        Assert.assertTrue(invokedFailure.get());
        callback0.getValue().onCompletion(null, null);
        try {
            setFuture.get(10000, TimeUnit.MILLISECONDS);
            Assert.fail("Should have seen KafkaException thrown when waiting on KafkaOffsetBackingStore.set() future");
        } catch (ExecutionException e) {
            // expected
            Assert.assertNotNull(e.getCause());
            Assert.assertTrue(((e.getCause()) instanceof KafkaException));
        }
        store.stop();
        PowerMock.verifyAll();
    }
}

