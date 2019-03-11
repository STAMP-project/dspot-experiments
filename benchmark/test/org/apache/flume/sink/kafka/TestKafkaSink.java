/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * limitations under the License.
 */
package org.apache.flume.sink.kafka;


import CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG;
import KafkaSinkConstants.ALLOW_TOPIC_OVERRIDE_HEADER;
import KafkaSinkConstants.DEFAULT_TOPIC_OVERRIDE_HEADER;
import KafkaSinkConstants.TOPIC_OVERRIDE_HEADER;
import PartitionOption.NOTANUMBER;
import PartitionOption.NOTSET;
import PartitionOption.VALIDBUTOUTOFRANGE;
import PartitionTestScenario.NO_PARTITION_HEADERS;
import PartitionTestScenario.PARTITION_ID_HEADER_ONLY;
import PartitionTestScenario.STATIC_HEADER_AND_PARTITION_ID;
import PartitionTestScenario.STATIC_HEADER_ONLY;
import ProducerConfig.ACKS_CONFIG;
import ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG;
import ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG;
import Sink.Status;
import com.google.common.base.Charsets;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.util.Utf8;
import org.apache.flume.Channel;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.Sink;
import org.apache.flume.Transaction;
import org.apache.flume.channel.MemoryChannel;
import org.apache.flume.conf.Configurables;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.instrumentation.SinkCounter;
import org.apache.flume.sink.kafka.util.TestUtil;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;

import static KafkaSinkConstants.KAFKA_PRODUCER_PREFIX;


/**
 * Unit tests for Kafka Sink
 */
public class TestKafkaSink {
    private static final TestUtil testUtil = TestUtil.getInstance();

    private final Set<String> usedTopics = new HashSet<String>();

    @Test
    public void testKafkaProperties() {
        KafkaSink kafkaSink = new KafkaSink();
        Context context = new Context();
        context.put(((KafkaSinkConstants.KAFKA_PREFIX) + (KafkaSinkConstants.TOPIC_CONFIG)), "");
        context.put(((KafkaSinkConstants.KAFKA_PRODUCER_PREFIX) + (ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG)), "override.default.serializer");
        context.put("kafka.producer.fake.property", "kafka.property.value");
        context.put("kafka.bootstrap.servers", "localhost:9092,localhost:9092");
        context.put("brokerList", "real-broker-list");
        Configurables.configure(kafkaSink, context);
        Properties kafkaProps = kafkaSink.getKafkaProps();
        // check that we have defaults set
        Assert.assertEquals(kafkaProps.getProperty(KEY_SERIALIZER_CLASS_CONFIG), KafkaSinkConstants.DEFAULT_KEY_SERIALIZER);
        // check that kafka properties override the default and get correct name
        Assert.assertEquals(kafkaProps.getProperty(VALUE_SERIALIZER_CLASS_CONFIG), "override.default.serializer");
        // check that any kafka-producer property gets in
        Assert.assertEquals(kafkaProps.getProperty("fake.property"), "kafka.property.value");
        // check that documented property overrides defaults
        Assert.assertEquals(kafkaProps.getProperty("bootstrap.servers"), "localhost:9092,localhost:9092");
    }

    @Test
    public void testOldProperties() {
        KafkaSink kafkaSink = new KafkaSink();
        Context context = new Context();
        context.put("topic", "test-topic");
        context.put(KafkaSinkConstants.OLD_BATCH_SIZE, "300");
        context.put(KafkaSinkConstants.BROKER_LIST_FLUME_KEY, "localhost:9092,localhost:9092");
        context.put(KafkaSinkConstants.REQUIRED_ACKS_FLUME_KEY, "all");
        Configurables.configure(kafkaSink, context);
        Properties kafkaProps = kafkaSink.getKafkaProps();
        Assert.assertEquals(kafkaSink.getTopic(), "test-topic");
        Assert.assertEquals(kafkaSink.getBatchSize(), 300);
        Assert.assertEquals(kafkaProps.getProperty(BOOTSTRAP_SERVERS_CONFIG), "localhost:9092,localhost:9092");
        Assert.assertEquals(kafkaProps.getProperty(ACKS_CONFIG), "all");
    }

    @Test
    public void testDefaultTopic() {
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        String msg = "default-topic-test";
        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg.getBytes());
        memoryChannel.put(event);
        tx.commit();
        tx.close();
        try {
            Sink.Status status = kafkaSink.process();
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        checkMessageArrived(msg, KafkaSinkConstants.DEFAULT_TOPIC);
    }

    @Test
    public void testStaticTopic() {
        Context context = prepareDefaultContext();
        // add the static topic
        context.put(KafkaSinkConstants.TOPIC_CONFIG, TestConstants.STATIC_TOPIC);
        String msg = "static-topic-test";
        try {
            Sink.Status status = prepareAndSend(context, msg);
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        checkMessageArrived(msg, TestConstants.STATIC_TOPIC);
    }

    @Test
    public void testTopicAndKeyFromHeader() {
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        String msg = "test-topic-and-key-from-header";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("topic", TestConstants.CUSTOM_TOPIC);
        headers.put("key", TestConstants.CUSTOM_KEY);
        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg.getBytes(), headers);
        memoryChannel.put(event);
        tx.commit();
        tx.close();
        try {
            Sink.Status status = kafkaSink.process();
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        checkMessageArrived(msg, TestConstants.CUSTOM_TOPIC);
    }

    /**
     * Tests that a message will be produced to a topic as specified by a
     * custom topicHeader parameter (FLUME-3046).
     */
    @Test
    public void testTopicFromConfHeader() {
        String customTopicHeader = "customTopicHeader";
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        context.put(TOPIC_OVERRIDE_HEADER, customTopicHeader);
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        String msg = "test-topic-from-config-header";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(customTopicHeader, TestConstants.CUSTOM_TOPIC);
        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg.getBytes(), headers);
        memoryChannel.put(event);
        tx.commit();
        tx.close();
        try {
            Sink.Status status = kafkaSink.process();
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        checkMessageArrived(msg, TestConstants.CUSTOM_TOPIC);
    }

    /**
     * Tests that the topicHeader parameter will be ignored if the allowTopicHeader
     * parameter is set to false (FLUME-3046).
     */
    @Test
    public void testTopicNotFromConfHeader() {
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        context.put(ALLOW_TOPIC_OVERRIDE_HEADER, "false");
        context.put(TOPIC_OVERRIDE_HEADER, "foo");
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        String msg = "test-topic-from-config-header";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(DEFAULT_TOPIC_OVERRIDE_HEADER, TestConstants.CUSTOM_TOPIC);
        headers.put("foo", TestConstants.CUSTOM_TOPIC);
        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg.getBytes(), headers);
        memoryChannel.put(event);
        tx.commit();
        tx.close();
        try {
            Sink.Status status = kafkaSink.process();
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        checkMessageArrived(msg, KafkaSinkConstants.DEFAULT_TOPIC);
    }

    @Test
    public void testReplaceSubStringOfTopicWithHeaders() {
        String topic = (TestConstants.HEADER_1_VALUE) + "-topic";
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        context.put(KafkaSinkConstants.TOPIC_CONFIG, TestConstants.HEADER_TOPIC);
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        String msg = "test-replace-substring-of-topic-with-headers";
        Map<String, String> headers = new HashMap<>();
        headers.put(TestConstants.HEADER_1_KEY, TestConstants.HEADER_1_VALUE);
        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg.getBytes(), headers);
        memoryChannel.put(event);
        tx.commit();
        tx.close();
        try {
            Sink.Status status = kafkaSink.process();
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        checkMessageArrived(msg, topic);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testAvroEvent() throws IOException {
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        context.put(KafkaSinkConstants.AVRO_EVENT, "true");
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        String msg = "test-avro-event";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("topic", TestConstants.CUSTOM_TOPIC);
        headers.put("key", TestConstants.CUSTOM_KEY);
        headers.put(TestConstants.HEADER_1_KEY, TestConstants.HEADER_1_VALUE);
        Transaction tx = memoryChannel.getTransaction();
        tx.begin();
        Event event = EventBuilder.withBody(msg.getBytes(), headers);
        memoryChannel.put(event);
        tx.commit();
        tx.close();
        try {
            Sink.Status status = kafkaSink.process();
            if (status == (Status.BACKOFF)) {
                Assert.fail("Error Occurred");
            }
        } catch (EventDeliveryException ex) {
            // ignore
        }
        String topic = TestConstants.CUSTOM_TOPIC;
        ConsumerRecords<String, String> recs = pollConsumerRecords(topic);
        Assert.assertNotNull(recs);
        Assert.assertTrue(((recs.count()) > 0));
        ConsumerRecord<String, String> consumerRecord = recs.iterator().next();
        ByteArrayInputStream in = new ByteArrayInputStream(consumerRecord.value().getBytes());
        BinaryDecoder decoder = DecoderFactory.get().directBinaryDecoder(in, null);
        SpecificDatumReader<AvroFlumeEvent> reader = new SpecificDatumReader(AvroFlumeEvent.class);
        AvroFlumeEvent avroevent = reader.read(null, decoder);
        String eventBody = new String(avroevent.getBody().array(), Charsets.UTF_8);
        Map<CharSequence, CharSequence> eventHeaders = avroevent.getHeaders();
        Assert.assertEquals(msg, eventBody);
        Assert.assertEquals(TestConstants.CUSTOM_KEY, consumerRecord.key());
        Assert.assertEquals(TestConstants.HEADER_1_VALUE, eventHeaders.get(new Utf8(TestConstants.HEADER_1_KEY)).toString());
        Assert.assertEquals(TestConstants.CUSTOM_KEY, eventHeaders.get(new Utf8("key")).toString());
    }

    @Test
    public void testEmptyChannel() throws EventDeliveryException {
        Sink kafkaSink = new KafkaSink();
        Context context = prepareDefaultContext();
        Configurables.configure(kafkaSink, context);
        Channel memoryChannel = new MemoryChannel();
        Configurables.configure(memoryChannel, context);
        kafkaSink.setChannel(memoryChannel);
        kafkaSink.start();
        Sink.Status status = kafkaSink.process();
        if (status != (Status.BACKOFF)) {
            Assert.fail("Error Occurred");
        }
        ConsumerRecords recs = pollConsumerRecords(KafkaSinkConstants.DEFAULT_TOPIC, 2);
        Assert.assertNotNull(recs);
        Assert.assertEquals(recs.count(), 0);
    }

    @Test
    public void testPartitionHeaderSet() throws Exception {
        doPartitionHeader(PARTITION_ID_HEADER_ONLY);
    }

    @Test
    public void testPartitionHeaderNotSet() throws Exception {
        doPartitionHeader(NO_PARTITION_HEADERS);
    }

    @Test
    public void testStaticPartitionAndHeaderSet() throws Exception {
        doPartitionHeader(STATIC_HEADER_AND_PARTITION_ID);
    }

    @Test
    public void testStaticPartitionHeaderNotSet() throws Exception {
        doPartitionHeader(STATIC_HEADER_ONLY);
    }

    @Test
    public void testPartitionHeaderMissing() throws Exception {
        doPartitionErrors(NOTSET);
    }

    @Test
    public void testPartitionHeaderOutOfRange() throws Exception {
        Sink kafkaSink = new KafkaSink();
        try {
            doPartitionErrors(VALIDBUTOUTOFRANGE, kafkaSink);
            Assert.fail();
        } catch (EventDeliveryException e) {
            // 
        }
        SinkCounter sinkCounter = ((SinkCounter) (Whitebox.getInternalState(kafkaSink, "counter")));
        Assert.assertEquals(1, sinkCounter.getEventWriteFail());
    }

    @Test(expected = EventDeliveryException.class)
    public void testPartitionHeaderInvalid() throws Exception {
        doPartitionErrors(NOTANUMBER);
    }

    /**
     * Tests that sub-properties (kafka.producer.*) apply correctly across multiple invocations
     * of configure() (fix for FLUME-2857).
     */
    @Test
    public void testDefaultSettingsOnReConfigure() {
        String sampleProducerProp = "compression.type";
        String sampleProducerVal = "snappy";
        Context context = prepareDefaultContext();
        context.put(((KAFKA_PRODUCER_PREFIX) + sampleProducerProp), sampleProducerVal);
        KafkaSink kafkaSink = new KafkaSink();
        Configurables.configure(kafkaSink, context);
        Assert.assertEquals(sampleProducerVal, kafkaSink.getKafkaProps().getProperty(sampleProducerProp));
        context = prepareDefaultContext();
        Configurables.configure(kafkaSink, context);
        Assert.assertNull(kafkaSink.getKafkaProps().getProperty(sampleProducerProp));
    }
}

