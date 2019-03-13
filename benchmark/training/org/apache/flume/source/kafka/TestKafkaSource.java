/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flume.source.kafka;


import ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import ConsumerConfig.GROUP_ID_CONFIG;
import KafkaSourceConstants.DEFAULT_GROUP_ID;
import KafkaSourceConstants.SET_TOPIC_HEADER;
import KafkaSourceConstants.TOPIC_HEADER;
import LifecycleState.START;
import Status.BACKOFF;
import Status.READY;
import com.google.common.base.Charsets;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.flume.ChannelException;
import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.FlumeException;
import org.apache.flume.PollableSource.Status;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.instrumentation.SourceCounter;
import org.apache.flume.source.avro.AvroFlumeEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static KafkaSourceConstants.KAFKA_CONSUMER_PREFIX;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;


public class TestKafkaSource {
    private static final Logger log = LoggerFactory.getLogger(TestKafkaSource.class);

    private KafkaSource kafkaSource;

    private static KafkaSourceEmbeddedKafka kafkaServer;

    private Context context;

    private List<Event> events;

    private final List<String> usedTopics = new ArrayList<>();

    private String topic0;

    private String topic1;

    @SuppressWarnings("unchecked")
    @Test
    public void testOffsets() throws InterruptedException, EventDeliveryException {
        long batchDuration = 2000;
        context.put(KafkaSourceConstants.TOPICS, topic1);
        context.put(KafkaSourceConstants.BATCH_DURATION_MS, String.valueOf(batchDuration));
        context.put(KafkaSourceConstants.BATCH_SIZE, "3");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(BACKOFF, status);
        Assert.assertEquals(0, events.size());
        TestKafkaSource.kafkaServer.produce(topic1, "", "record1");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record2");
        Thread.sleep(500L);
        status = kafkaSource.process();
        Assert.assertEquals(READY, status);
        Assert.assertEquals(2, events.size());
        events.clear();
        TestKafkaSource.kafkaServer.produce(topic1, "", "record3");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record4");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record5");
        Thread.sleep(500L);
        Assert.assertEquals(READY, kafkaSource.process());
        Assert.assertEquals(3, events.size());
        Assert.assertEquals("record3", new String(events.get(0).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record4", new String(events.get(1).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record5", new String(events.get(2).getBody(), Charsets.UTF_8));
        events.clear();
        TestKafkaSource.kafkaServer.produce(topic1, "", "record6");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record7");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record8");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record9");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record10");
        Thread.sleep(500L);
        Assert.assertEquals(READY, kafkaSource.process());
        Assert.assertEquals(3, events.size());
        Assert.assertEquals("record6", new String(events.get(0).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record7", new String(events.get(1).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record8", new String(events.get(2).getBody(), Charsets.UTF_8));
        events.clear();
        TestKafkaSource.kafkaServer.produce(topic1, "", "record11");
        // status must be READY due to time out exceed.
        Assert.assertEquals(READY, kafkaSource.process());
        Assert.assertEquals(3, events.size());
        Assert.assertEquals("record9", new String(events.get(0).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record10", new String(events.get(1).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record11", new String(events.get(2).getBody(), Charsets.UTF_8));
        events.clear();
        TestKafkaSource.kafkaServer.produce(topic1, "", "record12");
        TestKafkaSource.kafkaServer.produce(topic1, "", "record13");
        // stop kafka source
        kafkaSource.stop();
        // start again
        kafkaSource = new KafkaSource();
        kafkaSource.setChannelProcessor(createGoodChannel());
        kafkaSource.configure(context);
        startKafkaSource();
        TestKafkaSource.kafkaServer.produce(topic1, "", "record14");
        Thread.sleep(1000L);
        Assert.assertEquals(READY, kafkaSource.process());
        Assert.assertEquals(3, events.size());
        Assert.assertEquals("record12", new String(events.get(0).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record13", new String(events.get(1).getBody(), Charsets.UTF_8));
        Assert.assertEquals("record14", new String(events.get(2).getBody(), Charsets.UTF_8));
        events.clear();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessItNotEmpty() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world");
        Thread.sleep(500L);
        assertEquals(READY, kafkaSource.process());
        assertEquals(BACKOFF, kafkaSource.process());
        junit.framework.Assert.assertEquals(1, events.size());
        assertEquals("hello, world", new String(events.get(0).getBody(), Charsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessItNotEmptyBatch() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "2");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world");
        TestKafkaSource.kafkaServer.produce(topic0, "", "foo, bar");
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(READY, status);
        assertEquals("hello, world", new String(events.get(0).getBody(), Charsets.UTF_8));
        assertEquals("foo, bar", new String(events.get(1).getBody(), Charsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testProcessItEmpty() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(BACKOFF, status);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNonExistingTopic() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, "faketopic");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        Assert.assertEquals(START, kafkaSource.getLifecycleState());
        Status status = kafkaSource.process();
        Assert.assertEquals(BACKOFF, status);
    }

    @SuppressWarnings("unchecked")
    @Test(expected = FlumeException.class)
    public void testNonExistingKafkaServer() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BOOTSTRAP_SERVERS, "blabla:666");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(BACKOFF, status);
    }

    @Test
    public void testBatchTime() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_DURATION_MS, "250");
        kafkaSource.configure(context);
        startKafkaSource();
        kafkaSource.process();// timing magic

        Thread.sleep(500L);
        for (int i = 1; i < 5000; i++) {
            TestKafkaSource.kafkaServer.produce(topic0, "", ("hello, world " + i));
        }
        Thread.sleep(500L);
        long error = 50;
        long startTime = System.currentTimeMillis();
        Status status = kafkaSource.process();
        long endTime = System.currentTimeMillis();
        Assert.assertEquals(READY, status);
        Assert.assertTrue(((endTime - startTime) < ((context.getLong(KafkaSourceConstants.BATCH_DURATION_MS)) + error)));
    }

    // Consume event, stop source, start again and make sure we are not
    // consuming same event again
    @Test
    public void testCommit() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world");
        Thread.sleep(500L);
        assertEquals(READY, kafkaSource.process());
        kafkaSource.stop();
        Thread.sleep(500L);
        startKafkaSource();
        Thread.sleep(500L);
        assertEquals(BACKOFF, kafkaSource.process());
    }

    // Remove channel processor and test if we can consume events again
    @Test
    public void testNonCommit() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        context.put(KafkaSourceConstants.BATCH_DURATION_MS, "30000");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world");
        Thread.sleep(500L);
        kafkaSource.setChannelProcessor(createBadChannel());
        TestKafkaSource.log.debug("processing from kafka to bad channel");
        assertEquals(BACKOFF, kafkaSource.process());
        TestKafkaSource.log.debug("repairing channel");
        kafkaSource.setChannelProcessor(createGoodChannel());
        TestKafkaSource.log.debug("re-process to good channel - this should work");
        kafkaSource.process();
        assertEquals("hello, world", new String(events.get(0).getBody(), Charsets.UTF_8));
    }

    @Test
    public void testTwoBatches() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        context.put(KafkaSourceConstants.BATCH_DURATION_MS, "30000");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "event 1");
        Thread.sleep(500L);
        kafkaSource.process();
        assertEquals("event 1", new String(events.get(0).getBody(), Charsets.UTF_8));
        events.clear();
        TestKafkaSource.kafkaServer.produce(topic0, "", "event 2");
        Thread.sleep(500L);
        kafkaSource.process();
        assertEquals("event 2", new String(events.get(0).getBody(), Charsets.UTF_8));
    }

    @Test
    public void testTwoBatchesWithAutocommit() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        context.put(KafkaSourceConstants.BATCH_DURATION_MS, "30000");
        context.put(((KafkaSourceConstants.KAFKA_CONSUMER_PREFIX) + "enable.auto.commit"), "true");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "event 1");
        Thread.sleep(500L);
        kafkaSource.process();
        assertEquals("event 1", new String(events.get(0).getBody(), Charsets.UTF_8));
        events.clear();
        TestKafkaSource.kafkaServer.produce(topic0, "", "event 2");
        Thread.sleep(500L);
        kafkaSource.process();
        assertEquals("event 2", new String(events.get(0).getBody(), Charsets.UTF_8));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testNullKey() throws IllegalAccessException, IllegalArgumentException, InterruptedException, NoSuchFieldException, SecurityException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, null, "hello, world");
        Thread.sleep(500L);
        assertEquals(READY, kafkaSource.process());
        assertEquals(BACKOFF, kafkaSource.process());
        junit.framework.Assert.assertEquals(1, events.size());
        assertEquals("hello, world", new String(events.get(0).getBody(), Charsets.UTF_8));
    }

    @Test
    public void testErrorCounters() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        kafkaSource.configure(context);
        ChannelProcessor cp = Mockito.mock(ChannelProcessor.class);
        Mockito.doThrow(new ChannelException("dummy")).doThrow(new RuntimeException("dummy")).when(cp).processEventBatch(ArgumentMatchers.any(List.class));
        kafkaSource.setChannelProcessor(cp);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world");
        Thread.sleep(500L);
        kafkaSource.doProcess();
        kafkaSource.doProcess();
        SourceCounter sc = ((SourceCounter) (Whitebox.getInternalState(kafkaSource, "counter")));
        junit.framework.Assert.assertEquals(1, sc.getChannelWriteFail());
        junit.framework.Assert.assertEquals(1, sc.getEventReadFail());
        kafkaSource.stop();
    }

    @Test
    public void testSourceProperties() {
        Context context = new Context();
        context.put(KafkaSourceConstants.TOPICS, "test1, test2");
        context.put(KafkaSourceConstants.TOPICS_REGEX, "^stream[0-9]$");
        context.put(KafkaSourceConstants.BOOTSTRAP_SERVERS, "bootstrap-servers-list");
        KafkaSource source = new KafkaSource();
        source.doConfigure(context);
        // check that kafka.topics.regex has higher priority than topics
        // type of subscriber should be PatternSubscriber
        KafkaSource.Subscriber<Pattern> subscriber = source.getSubscriber();
        Pattern pattern = subscriber.get();
        assertTrue(pattern.matcher("stream1").find());
    }

    @Test
    public void testKafkaProperties() {
        Context context = new Context();
        context.put(KafkaSourceConstants.TOPICS, "test1, test2");
        context.put(((KafkaSourceConstants.KAFKA_CONSUMER_PREFIX) + (ConsumerConfig.GROUP_ID_CONFIG)), "override.default.group.id");
        context.put(((KafkaSourceConstants.KAFKA_CONSUMER_PREFIX) + "fake.property"), "kafka.property.value");
        context.put(KafkaSourceConstants.BOOTSTRAP_SERVERS, "real-bootstrap-servers-list");
        context.put(((KafkaSourceConstants.KAFKA_CONSUMER_PREFIX) + "bootstrap.servers"), "bad-bootstrap-servers-list");
        KafkaSource source = new KafkaSource();
        source.doConfigure(context);
        Properties kafkaProps = source.getConsumerProps();
        // check that we have defaults set
        Assert.assertEquals(String.valueOf(KafkaSourceConstants.DEFAULT_AUTO_COMMIT), kafkaProps.getProperty(ENABLE_AUTO_COMMIT_CONFIG));
        // check that kafka properties override the default and get correct name
        Assert.assertEquals("override.default.group.id", kafkaProps.getProperty(GROUP_ID_CONFIG));
        // check that any kafka property gets in
        Assert.assertEquals("kafka.property.value", kafkaProps.getProperty("fake.property"));
        // check that documented property overrides defaults
        Assert.assertEquals("real-bootstrap-servers-list", kafkaProps.getProperty("bootstrap.servers"));
    }

    @Test
    public void testOldProperties() {
        Context context = new Context();
        context.put(KafkaSourceConstants.TOPIC, "old.topic");
        context.put(KafkaSourceConstants.OLD_GROUP_ID, "old.groupId");
        context.put(KafkaSourceConstants.BOOTSTRAP_SERVERS, "real-bootstrap-servers-list");
        KafkaSource source = new KafkaSource();
        source.doConfigure(context);
        Properties kafkaProps = source.getConsumerProps();
        KafkaSource.Subscriber<List<String>> subscriber = source.getSubscriber();
        // check topic was set
        Assert.assertEquals("old.topic", subscriber.get().get(0));
        // check that kafka old properties override the default and get correct name
        Assert.assertEquals("old.groupId", kafkaProps.getProperty(GROUP_ID_CONFIG));
        source = new KafkaSource();
        context.put(((KafkaSourceConstants.KAFKA_CONSUMER_PREFIX) + (ConsumerConfig.GROUP_ID_CONFIG)), "override.old.group.id");
        source.doConfigure(context);
        kafkaProps = source.getConsumerProps();
        // check that kafka new properties override old
        Assert.assertEquals("override.old.group.id", kafkaProps.getProperty(GROUP_ID_CONFIG));
        context.clear();
        context.put(KafkaSourceConstants.BOOTSTRAP_SERVERS, "real-bootstrap-servers-list");
        context.put(KafkaSourceConstants.TOPIC, "old.topic");
        source = new KafkaSource();
        source.doConfigure(context);
        kafkaProps = source.getConsumerProps();
        // check defaults set
        Assert.assertEquals(DEFAULT_GROUP_ID, kafkaProps.getProperty(GROUP_ID_CONFIG));
    }

    @Test
    public void testPatternBasedSubscription() {
        Context context = new Context();
        context.put(KafkaSourceConstants.TOPICS_REGEX, "^topic[0-9]$");
        context.put(KafkaSourceConstants.OLD_GROUP_ID, "old.groupId");
        context.put(KafkaSourceConstants.BOOTSTRAP_SERVERS, "real-bootstrap-servers-list");
        KafkaSource source = new KafkaSource();
        source.doConfigure(context);
        KafkaSource.Subscriber<Pattern> subscriber = source.getSubscriber();
        for (int i = 0; i < 10; i++) {
            assertTrue(subscriber.get().matcher(("topic" + i)).find());
        }
        assertFalse(subscriber.get().matcher("topic").find());
    }

    @Test
    public void testAvroEvent() throws IOException, InterruptedException, EventDeliveryException {
        SpecificDatumWriter<AvroFlumeEvent> writer;
        ByteArrayOutputStream tempOutStream;
        BinaryEncoder encoder;
        byte[] bytes;
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(KafkaSourceConstants.BATCH_SIZE, "1");
        context.put(KafkaSourceConstants.AVRO_EVENT, "true");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        tempOutStream = new ByteArrayOutputStream();
        writer = new SpecificDatumWriter<AvroFlumeEvent>(AvroFlumeEvent.class);
        Map<CharSequence, CharSequence> headers = new HashMap<CharSequence, CharSequence>();
        headers.put("header1", "value1");
        headers.put("header2", "value2");
        AvroFlumeEvent e = new AvroFlumeEvent(headers, ByteBuffer.wrap("hello, world".getBytes()));
        encoder = EncoderFactory.get().directBinaryEncoder(tempOutStream, null);
        writer.write(e, encoder);
        encoder.flush();
        bytes = tempOutStream.toByteArray();
        TestKafkaSource.kafkaServer.produce(topic0, "", bytes);
        String currentTimestamp = Long.toString(System.currentTimeMillis());
        headers.put(KafkaSourceConstants.TIMESTAMP_HEADER, currentTimestamp);
        headers.put(KafkaSourceConstants.PARTITION_HEADER, "1");
        headers.put(KafkaSourceConstants.DEFAULT_TOPIC_HEADER, "topic0");
        e = new AvroFlumeEvent(headers, ByteBuffer.wrap("hello, world2".getBytes()));
        tempOutStream.reset();
        encoder = EncoderFactory.get().directBinaryEncoder(tempOutStream, null);
        writer.write(e, encoder);
        encoder.flush();
        bytes = tempOutStream.toByteArray();
        TestKafkaSource.kafkaServer.produce(topic0, "", bytes);
        Thread.sleep(500L);
        assertEquals(READY, kafkaSource.process());
        assertEquals(READY, kafkaSource.process());
        assertEquals(BACKOFF, kafkaSource.process());
        junit.framework.Assert.assertEquals(2, events.size());
        Event event = events.get(0);
        assertEquals("hello, world", new String(event.getBody(), Charsets.UTF_8));
        assertEquals("value1", e.getHeaders().get("header1"));
        assertEquals("value2", e.getHeaders().get("header2"));
        event = events.get(1);
        assertEquals("hello, world2", new String(event.getBody(), Charsets.UTF_8));
        assertEquals("value1", e.getHeaders().get("header1"));
        assertEquals("value2", e.getHeaders().get("header2"));
        assertEquals(currentTimestamp, e.getHeaders().get(KafkaSourceConstants.TIMESTAMP_HEADER));
        assertEquals(e.getHeaders().get(KafkaSourceConstants.PARTITION_HEADER), "1");
        assertEquals(e.getHeaders().get(KafkaSourceConstants.DEFAULT_TOPIC_HEADER), "topic0");
    }

    @Test
    public void testBootstrapLookup() {
        Context context = new Context();
        context.put(KafkaSourceConstants.ZOOKEEPER_CONNECT_FLUME_KEY, TestKafkaSource.kafkaServer.getZkConnectString());
        context.put(KafkaSourceConstants.TOPIC, "old.topic");
        context.put(KafkaSourceConstants.OLD_GROUP_ID, "old.groupId");
        KafkaSource source = new KafkaSource();
        source.doConfigure(context);
        String bootstrapServers = source.getBootstrapServers();
        assertEquals(TestKafkaSource.kafkaServer.getBootstrapServers(), bootstrapServers);
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

    /**
     * Tests that sub-properties (kafka.consumer.*) apply correctly across multiple invocations
     * of configure() (fix for FLUME-2857).
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaultSettingsOnReConfigure() throws Exception {
        String sampleConsumerProp = "auto.offset.reset";
        String sampleConsumerVal = "earliest";
        String group = "group";
        Context context = prepareDefaultContext(group);
        context.put(((KAFKA_CONSUMER_PREFIX) + sampleConsumerProp), sampleConsumerVal);
        context.put(KafkaSourceConstants.TOPIC, "random-topic");
        kafkaSource.configure(context);
        assertEquals(sampleConsumerVal, kafkaSource.getConsumerProps().getProperty(sampleConsumerProp));
        context = prepareDefaultContext(group);
        context.put(KafkaSourceConstants.TOPIC, "random-topic");
        kafkaSource.configure(context);
        assertNull(kafkaSource.getConsumerProps().getProperty(sampleConsumerProp));
    }

    /**
     * Tests the availability of the topic header in the output events,
     * based on the configuration parameters added in FLUME-3046
     *
     * @throws InterruptedException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testTopicHeaderSet() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world");
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(READY, status);
        assertEquals("hello, world", new String(events.get(0).getBody(), Charsets.UTF_8));
        assertEquals(topic0, events.get(0).getHeaders().get("topic"));
        kafkaSource.stop();
        events.clear();
    }

    /**
     * Tests the availability of the custom topic header in the output events,
     * based on the configuration parameters added in FLUME-3046
     *
     * @throws InterruptedException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testTopicCustomHeaderSet() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(TOPIC_HEADER, "customTopicHeader");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world2");
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(READY, status);
        assertEquals("hello, world2", new String(events.get(0).getBody(), Charsets.UTF_8));
        assertEquals(topic0, events.get(0).getHeaders().get("customTopicHeader"));
        kafkaSource.stop();
        events.clear();
    }

    /**
     * Tests the unavailability of the topic header in the output events,
     * based on the configuration parameters added in FLUME-3046
     *
     * @throws InterruptedException
     * 		
     * @throws EventDeliveryException
     * 		
     */
    @Test
    public void testTopicCustomHeaderNotSet() throws InterruptedException, EventDeliveryException {
        context.put(KafkaSourceConstants.TOPICS, topic0);
        context.put(SET_TOPIC_HEADER, "false");
        kafkaSource.configure(context);
        startKafkaSource();
        Thread.sleep(500L);
        TestKafkaSource.kafkaServer.produce(topic0, "", "hello, world3");
        Thread.sleep(500L);
        Status status = kafkaSource.process();
        Assert.assertEquals(READY, status);
        assertEquals("hello, world3", new String(events.get(0).getBody(), Charsets.UTF_8));
        assertNull(events.get(0).getHeaders().get("customTopicHeader"));
        kafkaSource.stop();
    }

    @Test
    public void testMigrateZookeeperOffsetsWhenTopicNotExists() throws Exception {
        String topic = findUnusedTopic();
        Context context = prepareDefaultContext("testMigrateOffsets-nonExistingTopic");
        context.put(KafkaSourceConstants.ZOOKEEPER_CONNECT_FLUME_KEY, TestKafkaSource.kafkaServer.getZkConnectString());
        context.put(KafkaSourceConstants.TOPIC, topic);
        KafkaSource source = new KafkaSource();
        source.doConfigure(context);
        source.setChannelProcessor(createGoodChannel());
        source.start();
        Assert.assertEquals(START, source.getLifecycleState());
        Status status = source.process();
        Assert.assertEquals(BACKOFF, status);
        source.stop();
    }
}

