/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.kafkarest.v2;


import ConsumerConfig.EXCLUDE_INTERNAL_TOPICS_CONFIG;
import KafkaConsumerManager.KafkaConsumerFactory;
import KafkaConsumerManager.RunnableReadTask;
import KafkaRestConfig.CONSUMER_ITERATOR_BACKOFF_MS_CONFIG;
import KafkaRestConfig.CONSUMER_REQUEST_MAX_BYTES_CONFIG;
import KafkaRestConfig.CONSUMER_REQUEST_TIMEOUT_MS_CONFIG;
import KafkaRestConfig.CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT;
import KafkaRestConfig.PROXY_FETCH_MIN_BYTES_CONFIG;
import io.confluent.kafkarest.KafkaRestConfig;
import io.confluent.kafkarest.MetadataObserver;
import io.confluent.kafkarest.SystemTime;
import io.confluent.kafkarest.entities.BinaryConsumerRecord;
import io.confluent.kafkarest.entities.ConsumerInstanceConfig;
import io.confluent.kafkarest.entities.ConsumerOffsetCommitRequest;
import io.confluent.kafkarest.entities.ConsumerRecord;
import io.confluent.kafkarest.entities.ConsumerSubscriptionRecord;
import io.confluent.kafkarest.entities.EmbeddedFormat;
import io.confluent.kafkarest.entities.TopicPartitionOffset;
import io.confluent.rest.exceptions.RestException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Tests basic create/read/commit/delete functionality of ConsumerManager. This only exercises the
 * functionality for binary data because it uses a mock consumer that only works with byte[] data.
 */
@RunWith(EasyMockRunner.class)
public class KafkaConsumerManagerTest {
    private KafkaRestConfig config;

    @Mock
    private MetadataObserver mdObserver;

    @Mock
    private KafkaConsumerFactory consumerFactory;

    private KafkaConsumerManager consumerManager;

    private static final String groupName = "testgroup";

    private static final String topicName = "testtopic";

    // Setup holding vars for results from callback
    private boolean sawCallback = false;

    private static Exception actualException = null;

    private static List<? extends ConsumerRecord<byte[], byte[]>> actualRecords = null;

    private static List<TopicPartitionOffset> actualOffsets = null;

    private Capture<Properties> capturedConsumerConfig;

    private MockConsumer<byte[], byte[]> consumer;

    @Test
    public void testConsumerOverrides() {
        final Capture<Properties> consumerConfig = Capture.newInstance();
        EasyMock.expect(consumerFactory.createConsumer(EasyMock.capture(consumerConfig))).andReturn(consumer);
        EasyMock.replay(consumerFactory);
        consumerManager.createConsumer(KafkaConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        // The exclude.internal.topics setting is overridden via the constructor when the
        // ConsumerManager is created, and we can make sure it gets set properly here.
        Assert.assertEquals("false", consumerConfig.getValue().get(EXCLUDE_INTERNAL_TOPICS_CONFIG));
        EasyMock.verify(consumerFactory);
    }

    /**
     * Response should return no sooner than KafkaRestConfig.CONSUMER_REQUEST_TIMEOUT_MS_CONFIG
     */
    @Test
    public void testConsumerRequestTimeoutms() throws Exception {
        Properties props = setUpProperties(new Properties());
        props.setProperty(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, "2500");
        setUpConsumer(props);
        expectCreate(consumer);
        String cid = consumerManager.createConsumer(KafkaConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        consumerManager.subscribe(KafkaConsumerManagerTest.groupName, cid, new ConsumerSubscriptionRecord(Collections.singletonList(KafkaConsumerManagerTest.topicName), null));
        readFromDefault(cid);
        Thread.sleep(((long) ((config.getInt(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG)) * 0.5)));
        Assert.assertFalse("Callback failed early", sawCallback);
        Thread.sleep(((long) ((config.getInt(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG)) * 0.7)));
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", KafkaConsumerManagerTest.actualException);
    }

    /**
     * Response should return no sooner than KafkaRestConfig.PROXY_FETCH_MAX_WAIT_MS_CONFIG
     */
    @Test
    public void testConsumerWaitMs() throws Exception {
        Properties props = setUpProperties(new Properties());
        Integer expectedRequestTimeoutms = 400;
        props.setProperty(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, expectedRequestTimeoutms.toString());
        setUpConsumer(props);
        expectCreate(consumer);
        schedulePoll();
        String cid = consumerManager.createConsumer(KafkaConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        consumerManager.subscribe(KafkaConsumerManagerTest.groupName, cid, new ConsumerSubscriptionRecord(Collections.singletonList(KafkaConsumerManagerTest.topicName), null));
        consumer.rebalance(Collections.singletonList(new TopicPartition(KafkaConsumerManagerTest.topicName, 0)));
        consumer.updateBeginningOffsets(Collections.singletonMap(new TopicPartition(KafkaConsumerManagerTest.topicName, 0), 0L));
        readFromDefault(cid);
        long startTime = System.currentTimeMillis();
        while (((System.currentTimeMillis()) - startTime) < expectedRequestTimeoutms) {
            Assert.assertFalse(sawCallback);
            Thread.sleep(40);
        } 
        Thread.sleep(((long) (expectedRequestTimeoutms * 0.5)));
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", KafkaConsumerManagerTest.actualException);
    }

    /**
     * When min.bytes is not fulfilled, we should return after consumer.request.timeout.ms
     * When min.bytes is fulfilled, we should return immediately
     */
    @Test
    public void testConsumerRequestTimeoutmsAndMinBytes() throws Exception {
        Properties props = setUpProperties(new Properties());
        props.setProperty(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, "1303");
        props.setProperty(PROXY_FETCH_MIN_BYTES_CONFIG, "5");
        setUpConsumer(props);
        expectCreate(consumer);
        String cid = consumerManager.createConsumer(KafkaConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        consumerManager.subscribe(KafkaConsumerManagerTest.groupName, cid, new ConsumerSubscriptionRecord(Collections.singletonList(KafkaConsumerManagerTest.topicName), null));
        consumer.rebalance(Collections.singletonList(new TopicPartition(KafkaConsumerManagerTest.topicName, 0)));
        consumer.updateBeginningOffsets(Collections.singletonMap(new TopicPartition(KafkaConsumerManagerTest.topicName, 0), 0L));
        long startTime = System.currentTimeMillis();
        readFromDefault(cid);
        int expectedRequestTimeoutms = config.getInt(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG);
        while (((System.currentTimeMillis()) - startTime) < expectedRequestTimeoutms) {
            Assert.assertFalse(sawCallback);
            Thread.sleep(100);
        } 
        Thread.sleep(200);
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", KafkaConsumerManagerTest.actualException);
        Assert.assertTrue("Records returned not empty", KafkaConsumerManagerTest.actualRecords.isEmpty());
        sawCallback = false;
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = schedulePoll();
        readFromDefault(cid);
        Thread.sleep((expectedRequestTimeoutms / 2));// should return in less time

        Assert.assertEquals("Records returned not as expected", referenceRecords, KafkaConsumerManagerTest.actualRecords);
        Assert.assertTrue("Callback not called", sawCallback);
        Assert.assertNull("Callback exception", KafkaConsumerManagerTest.actualException);
    }

    /**
     * Should return more than min bytes of records and less than max bytes.
     * Should not poll() twice after min bytes have been reached
     */
    @Test
    public void testConsumerMinAndMaxBytes() throws Exception {
        BinaryConsumerRecord sampleRecord = binaryConsumerRecord(0);
        int sampleRecordSize = (sampleRecord.getKey().length) + (sampleRecord.getValue().length);
        // we expect all the records from the first poll to be returned
        Properties props = setUpProperties(new Properties());
        props.setProperty(PROXY_FETCH_MIN_BYTES_CONFIG, Integer.toString(sampleRecordSize));
        props.setProperty(CONSUMER_REQUEST_MAX_BYTES_CONFIG, Integer.toString((sampleRecordSize * 10)));
        setUpConsumer(props);
        final List<ConsumerRecord<byte[], byte[]>> scheduledRecords = schedulePoll();
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = Arrays.asList(scheduledRecords.get(0), scheduledRecords.get(1), scheduledRecords.get(2));
        schedulePoll(3);
        expectCreate(consumer);
        String cid = consumerManager.createConsumer(KafkaConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        consumerManager.subscribe(KafkaConsumerManagerTest.groupName, cid, new ConsumerSubscriptionRecord(Collections.singletonList(KafkaConsumerManagerTest.topicName), null));
        consumer.rebalance(Collections.singletonList(new TopicPartition(KafkaConsumerManagerTest.topicName, 0)));
        consumer.updateBeginningOffsets(Collections.singletonMap(new TopicPartition(KafkaConsumerManagerTest.topicName, 0), 0L));
        readFromDefault(cid);
        Thread.sleep(((long) ((Integer.parseInt(CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT)) * 0.5)));// should return sooner since min bytes hit

        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", KafkaConsumerManagerTest.actualException);
        Assert.assertEquals("Records returned not as expected", referenceRecords, KafkaConsumerManagerTest.actualRecords);
    }

    @Test
    public void testConsumeMinBytesIsOverridablePerConsumer() throws Exception {
        BinaryConsumerRecord sampleRecord = binaryConsumerRecord(0);
        int sampleRecordSize = (sampleRecord.getKey().length) + (sampleRecord.getValue().length);
        Properties props = setUpProperties(new Properties());
        props.setProperty(PROXY_FETCH_MIN_BYTES_CONFIG, Integer.toString((sampleRecordSize * 5)));
        props.setProperty(CONSUMER_REQUEST_MAX_BYTES_CONFIG, Integer.toString((sampleRecordSize * 6)));
        setUpConsumer(props);
        final List<ConsumerRecord<byte[], byte[]>> scheduledRecords = schedulePoll();
        // global settings would make the consumer call poll twice and get more than 3 records,
        // overridden settings should make him poll once since the min bytes will be reached
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = Arrays.asList(scheduledRecords.get(0), scheduledRecords.get(1), scheduledRecords.get(2));
        schedulePoll(3);
        expectCreate(consumer);
        ConsumerInstanceConfig config = new ConsumerInstanceConfig(EmbeddedFormat.BINARY);
        // we expect three records to be returned since the setting is overridden and poll() wont be called a second time
        config.setResponseMinBytes((sampleRecordSize * 2));
        String cid = consumerManager.createConsumer(KafkaConsumerManagerTest.groupName, config);
        consumerManager.subscribe(KafkaConsumerManagerTest.groupName, cid, new ConsumerSubscriptionRecord(Collections.singletonList(KafkaConsumerManagerTest.topicName), null));
        consumer.rebalance(Collections.singletonList(new TopicPartition(KafkaConsumerManagerTest.topicName, 0)));
        consumer.updateBeginningOffsets(Collections.singletonMap(new TopicPartition(KafkaConsumerManagerTest.topicName, 0), 0L));
        readFromDefault(cid);
        Thread.sleep(((long) ((Integer.parseInt(CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT)) * 0.5)));// should return sooner since min bytes hit

        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", KafkaConsumerManagerTest.actualException);
        Assert.assertEquals("Records returned not as expected", referenceRecords, KafkaConsumerManagerTest.actualRecords);
    }

    @Test
    public void testConsumerNormalOps() throws InterruptedException, ExecutionException {
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = bootstrapConsumer(consumer);
        sawCallback = false;
        KafkaConsumerManagerTest.actualException = null;
        KafkaConsumerManagerTest.actualRecords = null;
        readFromDefault(consumer.cid());
        Thread.sleep(((long) ((Integer.parseInt(CONSUMER_REQUEST_TIMEOUT_MS_DEFAULT)) * 1.1)));
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", KafkaConsumerManagerTest.actualException);
        Assert.assertEquals("Records returned not as expected", referenceRecords, KafkaConsumerManagerTest.actualRecords);
        sawCallback = false;
        KafkaConsumerManagerTest.actualException = null;
        KafkaConsumerManagerTest.actualOffsets = null;
        ConsumerOffsetCommitRequest commitRequest = null;// Commit all offsets

        consumerManager.commitOffsets(KafkaConsumerManagerTest.groupName, consumer.cid(), null, commitRequest, new KafkaConsumerManager.CommitCallback() {
            @Override
            public void onCompletion(List<TopicPartitionOffset> offsets, Exception e) {
                sawCallback = true;
                KafkaConsumerManagerTest.actualException = e;
                KafkaConsumerManagerTest.actualOffsets = offsets;
            }
        }).get();
        Assert.assertTrue("Callback not called", sawCallback);
        Assert.assertNull("Callback exception", KafkaConsumerManagerTest.actualException);
        // Mock consumer doesn't handle offsets, so we just check we get some output for the
        // right partitions
        Assert.assertNotNull("Callback Offsets", KafkaConsumerManagerTest.actualOffsets);
        // TODO: Currently the values are not actually returned in the callback nor in the response.
        // assertEquals("Callback Offsets Size", 3, actualOffsets.size());
        consumerManager.deleteConsumer(KafkaConsumerManagerTest.groupName, consumer.cid());
    }

    @Test
    public void testBackoffMsControlsPollCalls() throws Exception {
        bootstrapConsumer(consumer);
        consumerManager.readRecords(KafkaConsumerManagerTest.groupName, consumer.cid(), BinaryKafkaConsumerState.class, (-1), Long.MAX_VALUE, new io.confluent.kafkarest.ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                KafkaConsumerManagerTest.actualException = e;
                KafkaConsumerManagerTest.actualRecords = records;
                sawCallback = true;
            }
        });
        // backoff is 250
        Thread.sleep(100);
        // backoff should be in place right now. the read task should be delayed and re-ran until the max.bytes or timeout is hit
        Assert.assertEquals(1, consumerManager.delayedReadTasks.size());
        Thread.sleep(100);
        Assert.assertEquals(1, consumerManager.delayedReadTasks.size());
    }

    @Test
    public void testBackoffMsUpdatesReadTaskExpiry() throws Exception {
        Properties props = setUpProperties();
        props.put(CONSUMER_ITERATOR_BACKOFF_MS_CONFIG, "1000");
        config = new KafkaRestConfig(props, new SystemTime());
        consumerManager = new KafkaConsumerManager(config, consumerFactory);
        consumer = new MockConsumer(OffsetResetStrategy.EARLIEST, KafkaConsumerManagerTest.groupName);
        bootstrapConsumer(consumer);
        consumerManager.readRecords(KafkaConsumerManagerTest.groupName, consumer.cid(), BinaryKafkaConsumerState.class, (-1), Long.MAX_VALUE, new io.confluent.kafkarest.ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                KafkaConsumerManagerTest.actualException = e;
                KafkaConsumerManagerTest.actualRecords = records;
                sawCallback = true;
            }
        });
        Thread.sleep(100);
        KafkaConsumerManager.RunnableReadTask readTask = consumerManager.delayedReadTasks.peek();
        if (readTask == null) {
            Assert.fail("Could not get read task in time. It should not be null");
        }
        long delay = readTask.getDelay(TimeUnit.MILLISECONDS);
        Assert.assertTrue((delay < 1000));
        Assert.assertTrue((delay > 700));
    }

    @Test
    public void testConsumerExpirationIsUpdated() throws Exception {
        bootstrapConsumer(consumer);
        KafkaConsumerState state = consumerManager.getConsumerInstance(KafkaConsumerManagerTest.groupName, consumer.cid());
        long initialExpiration = state.expiration;
        consumerManager.readRecords(KafkaConsumerManagerTest.groupName, consumer.cid(), BinaryKafkaConsumerState.class, (-1), Long.MAX_VALUE, new io.confluent.kafkarest.ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                KafkaConsumerManagerTest.actualException = e;
                KafkaConsumerManagerTest.actualRecords = records;
                sawCallback = true;
            }
        });
        Thread.sleep(100);
        Assert.assertTrue(((state.expiration) > initialExpiration));
        initialExpiration = state.expiration;
        awaitRead();
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertTrue(((state.expiration) > initialExpiration));
        initialExpiration = state.expiration;
        consumerManager.commitOffsets(KafkaConsumerManagerTest.groupName, consumer.cid(), null, null, new KafkaConsumerManager.CommitCallback() {
            @Override
            public void onCompletion(List<TopicPartitionOffset> offsets, Exception e) {
                sawCallback = true;
                KafkaConsumerManagerTest.actualException = e;
                KafkaConsumerManagerTest.actualOffsets = offsets;
            }
        }).get();
        Assert.assertTrue(((state.expiration) > initialExpiration));
    }
}

