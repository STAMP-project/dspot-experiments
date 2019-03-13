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
package io.confluent.kafkarest;


import ConsumerManager.ConsumerFactory;
import ConsumerManager.RunnableReadTask;
import EmbeddedFormat.BINARY;
import Errors.CONSUMER_ALREADY_EXISTS_ERROR_CODE;
import Errors.CONSUMER_ALREADY_SUBSCRIBED_ERROR_CODE;
import KafkaRestConfig.CONSUMER_ITERATOR_BACKOFF_MS_CONFIG;
import KafkaRestConfig.CONSUMER_ITERATOR_TIMEOUT_MS_DEFAULT;
import KafkaRestConfig.CONSUMER_REQUEST_TIMEOUT_MS_CONFIG;
import KafkaRestConfig.PROXY_FETCH_MIN_BYTES_CONFIG;
import io.confluent.kafkarest.entities.BinaryConsumerRecord;
import io.confluent.kafkarest.entities.ConsumerInstanceConfig;
import io.confluent.kafkarest.entities.ConsumerRecord;
import io.confluent.kafkarest.entities.EmbeddedFormat;
import io.confluent.kafkarest.entities.TopicPartitionOffset;
import io.confluent.kafkarest.mock.MockConsumerConnector;
import io.confluent.rest.exceptions.RestException;
import io.confluent.rest.exceptions.RestNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import kafka.consumer.ConsumerConfig;
import kafka.javaapi.consumer.ConsumerConnector;
import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests basic create/read/commit/delete functionality of ConsumerManager. This only exercises the
 * functionality for binary data because it uses a mock consumer that only works with byte[] data.
 */
public class ConsumerManagerTest {
    private Properties properties;

    private KafkaRestConfig config;

    private MetadataObserver mdObserver;

    private ConsumerFactory consumerFactory;

    private ConsumerManager consumerManager;

    private static final String groupName = "testgroup";

    private static final String topicName = "testtopic";

    private static final String secondTopicName = "testtopic2";

    // Setup holding vars for results from callback
    private boolean sawCallback = false;

    private static Exception actualException = null;

    private static List<? extends ConsumerRecord<byte[], byte[]>> actualRecords = null;

    private int actualLength = 0;

    private static List<TopicPartitionOffset> actualOffsets = null;

    private Capture<ConsumerConfig> capturedConsumerConfig;

    @Test
    public void testConsumerOverrides() {
        ConsumerConnector consumer = new MockConsumerConnector(config.getTime(), "testclient", null, Integer.parseInt(CONSUMER_ITERATOR_TIMEOUT_MS_DEFAULT), true);
        final Capture<ConsumerConfig> consumerConfig = Capture.newInstance();
        EasyMock.expect(consumerFactory.createConsumer(EasyMock.capture(consumerConfig))).andReturn(consumer);
        EasyMock.replay(consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        // The exclude.internal.topics setting is overridden via the constructor when the
        // ConsumerManager is created, and we can make sure it gets set properly here.
        Assert.assertFalse(consumerConfig.getValue().excludeInternalTopics());
        EasyMock.verify(consumerFactory);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testConsumerNormalOps() throws Exception {
        // Tests create instance, read, and delete
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = referenceRecords(3);
        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<>();
        referenceSchedule.put(50, referenceRecords);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        readFromDefault(cid);
        verifyRead(referenceRecords, null);
        sawCallback = false;
        ConsumerManagerTest.actualException = null;
        ConsumerManagerTest.actualOffsets = null;
        consumerManager.commitOffsets(ConsumerManagerTest.groupName, cid, new ConsumerManager.CommitCallback() {
            @Override
            public void onCompletion(List<TopicPartitionOffset> offsets, Exception e) {
                sawCallback = true;
                ConsumerManagerTest.actualException = e;
                ConsumerManagerTest.actualOffsets = offsets;
            }
        }).get();
        Assert.assertTrue("Callback not called", sawCallback);
        Assert.assertNull("Callback exception", ConsumerManagerTest.actualException);
        // Mock consumer doesn't handle offsets, so we just check we get some output for the
        // right partitions
        Assert.assertNotNull("Callback Offsets", ConsumerManagerTest.actualOffsets);
        Assert.assertEquals("Callback Offsets Size", 3, ConsumerManagerTest.actualOffsets.size());
        consumerManager.deleteConsumer(ConsumerManagerTest.groupName, cid);
        EasyMock.verify(mdObserver, consumerFactory);
    }

    /**
     * Response should return no sooner than KafkaRestConfig.CONSUMER_REQUEST_TIMEOUT_MS_CONFIG
     */
    @Test
    public void testConsumerRequestTimeoutMs() throws Exception {
        Integer expectedRequestTimeoutMs = 400;
        properties.setProperty(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, expectedRequestTimeoutMs.toString());
        setUp(properties);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<>();
        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        long startTime = System.currentTimeMillis();
        readFromDefault(consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY)));
        Assert.assertTrue((((System.currentTimeMillis()) - startTime) > expectedRequestTimeoutMs));
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", ConsumerManagerTest.actualException);
    }

    /**
     * When min.bytes is fulfilled, we should return immediately
     */
    @Test
    public void testConsumerTimeoutMsMsAndMinBytes() throws Exception {
        properties.setProperty(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, "1303");
        properties.setProperty(PROXY_FETCH_MIN_BYTES_CONFIG, "1");
        setUp(properties);
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = referenceRecords(3);
        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<>();
        referenceSchedule.put(50, referenceRecords);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        long startTime = System.currentTimeMillis();
        readFromDefault(cid);
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", ConsumerManagerTest.actualException);
        // should return first record immediately since min.bytes is fulfilled
        Assert.assertEquals("Records returned not as expected", Arrays.asList(referenceRecords.get(0)), ConsumerManagerTest.actualRecords);
        long estimatedTime = (System.currentTimeMillis()) - startTime;
        int timeoutMs = config.getInt(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG);
        Assert.assertTrue((estimatedTime < timeoutMs));// should have returned earlier than consumer.request.timeout.ms

    }

    @Test
    public void testConsumeMinBytesIsOverridablePerConsumer() throws Exception {
        properties.setProperty(PROXY_FETCH_MIN_BYTES_CONFIG, "10");
        // global settings should return more than one record immediately
        setUp(properties);
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = referenceRecords(3);
        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<>();
        referenceSchedule.put(50, referenceRecords);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        ConsumerInstanceConfig config = new ConsumerInstanceConfig(EmbeddedFormat.BINARY);
        // we expect one record to be returned since the setting is overridden
        config.setResponseMinBytes(1);
        readFromDefault(consumerManager.createConsumer(ConsumerManagerTest.groupName, config));
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", ConsumerManagerTest.actualException);
        // should return first record immediately since min.bytes is fulfilled
        Assert.assertEquals("Records returned not as expected", Arrays.asList(referenceRecords.get(0)), ConsumerManagerTest.actualRecords);
    }

    /**
     * Response should return no sooner than the overridden CONSUMER_REQUEST_TIMEOUT_MS_CONFIG
     */
    @Test
    public void testConsumerRequestTimeoutMsIsOverriddablePerConsumer() throws Exception {
        Integer overriddenRequestTimeMs = 111;
        Integer globalRequestTimeMs = 1201;
        properties.setProperty(CONSUMER_REQUEST_TIMEOUT_MS_CONFIG, globalRequestTimeMs.toString());
        setUp(properties);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<>();
        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        ConsumerInstanceConfig consumerConfig = new ConsumerInstanceConfig(null, null, BINARY.name(), null, null, null, overriddenRequestTimeMs);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, consumerConfig);
        long startTime = System.currentTimeMillis();
        readFromDefault(cid);
        long elapsedTime = (System.currentTimeMillis()) - startTime;
        Assert.assertTrue((elapsedTime < globalRequestTimeMs));
        Assert.assertTrue((elapsedTime > overriddenRequestTimeMs));
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("No exception in callback", ConsumerManagerTest.actualException);
    }

    @Test
    public void testConsumerMaxBytesResponse() throws Exception {
        // Tests that when there are more records available than the max bytes to be included in the
        // response, not all of it is returned.
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = // Don't use 512 as this happens to fall on boundary
        Arrays.<ConsumerRecord<byte[], byte[]>>asList(new BinaryConsumerRecord(ConsumerManagerTest.topicName, null, new byte[511], 0, 0), new BinaryConsumerRecord(ConsumerManagerTest.topicName, null, new byte[511], 1, 0), new BinaryConsumerRecord(ConsumerManagerTest.topicName, null, new byte[511], 2, 0), new BinaryConsumerRecord(ConsumerManagerTest.topicName, null, new byte[511], 3, 0));
        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<Integer, List<ConsumerRecord<byte[], byte[]>>>();
        referenceSchedule.put(50, referenceRecords);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        // Ensure vars used by callback are correctly initialised.
        sawCallback = false;
        ConsumerManagerTest.actualException = null;
        actualLength = 0;
        readTopic(cid, new ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                sawCallback = true;
                ConsumerManagerTest.actualException = e;
                // Should only see the first two messages since the third pushes us over the limit.
                actualLength = records.size();
            }
        });
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("Callback received exception", ConsumerManagerTest.actualException);
        // Should only see the first two messages since the third pushes us over the limit.
        Assert.assertEquals("List of records returned incorrect", 2, actualLength);
        // Also check the user-submitted limit
        sawCallback = false;
        ConsumerManagerTest.actualException = null;
        actualLength = 0;
        readTopic(cid, 512, new ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                sawCallback = true;
                ConsumerManagerTest.actualException = e;
                // Should only see the first two messages since the third pushes us over the limit.
                actualLength = records.size();
            }
        });
        Assert.assertTrue("Callback failed to fire", sawCallback);
        Assert.assertNull("Callback received exception", ConsumerManagerTest.actualException);
        // Should only see the first two messages since the third pushes us over the limit.
        Assert.assertEquals("List of records returned incorrect", 1, actualLength);
        consumerManager.deleteConsumer(ConsumerManagerTest.groupName, cid);
        EasyMock.verify(mdObserver, consumerFactory);
    }

    @Test
    public void testIDOverridesName() {
        // We should remain compatible with the original use of consumer IDs, even if it shouldn't
        // really be used. Specifying any ID should override any naming to ensure the same behavior
        expectCreateNoData("id");
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig("id", "name", BINARY.toString(), null, null, null, null));
        Assert.assertEquals("id", cid);
        Assert.assertEquals("id", capturedConsumerConfig.getValue().consumerId().getOrElse(null));
        EasyMock.verify(mdObserver, consumerFactory);
    }

    @Test
    public void testDuplicateConsumerName() {
        expectCreateNoData();
        EasyMock.replay(mdObserver, consumerFactory);
        consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(null, "name", BINARY.toString(), null, null, null, null));
        try {
            consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(null, "name", BINARY.toString(), null, null, null, null));
            Assert.fail("Expected to see exception because consumer already exists");
        } catch (RestException e) {
            // expected
            Assert.assertEquals(CONSUMER_ALREADY_EXISTS_ERROR_CODE, e.getErrorCode());
        }
        EasyMock.verify(mdObserver, consumerFactory);
    }

    @Test
    public void testMultipleTopicSubscriptionsFail() throws Exception {
        expectCreateNoData();
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.secondTopicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        sawCallback = false;
        ConsumerManagerTest.actualException = null;
        ConsumerManagerTest.actualRecords = null;
        readTopic(cid);
        verifyRead(Collections.<ConsumerRecord<byte[], byte[]>>emptyList(), null);
        Assert.assertTrue("Callback not called", sawCallback);
        Assert.assertNull("Callback exception", ConsumerManagerTest.actualException);
        Assert.assertEquals("Callback records should be valid but of 0 size", 0, ConsumerManagerTest.actualRecords.size());
        // Attempt to read from second topic should result in an exception
        sawCallback = false;
        ConsumerManagerTest.actualException = null;
        ConsumerManagerTest.actualRecords = null;
        readTopic(cid, ConsumerManagerTest.secondTopicName);
        verifyRead(null, RestException.class);
        Assert.assertEquals("Callback Exception should be for already subscribed consumer", CONSUMER_ALREADY_SUBSCRIBED_ERROR_CODE, getErrorCode());
        consumerManager.deleteConsumer(ConsumerManagerTest.groupName, cid);
        EasyMock.verify(mdObserver, consumerFactory);
    }

    @Test
    public void testBackoffMsControlsPollCalls() throws Exception {
        Properties props = new Properties();
        props.setProperty(CONSUMER_ITERATOR_BACKOFF_MS_CONFIG, "250");
        // This setting supports the testConsumerOverrides test. It is otherwise benign and should
        // not affect other tests.
        props.setProperty("exclude.internal.topics", "false");
        config = new KafkaRestConfig(props, new SystemTime());
        mdObserver = EasyMock.createMock(MetadataObserver.class);
        consumerFactory = EasyMock.createMock(ConsumerFactory.class);
        consumerManager = new ConsumerManager(config, mdObserver, consumerFactory);
        expectCreateNoData();
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        Future f = readTopicFuture(cid, ConsumerManagerTest.topicName, Long.MAX_VALUE, new ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                sawCallback = true;
                ConsumerManagerTest.actualException = e;
            }
        });
        // backoff is 250
        Thread.sleep(100);
        // backoff should be in place right now. the read task should be delayed and re-ran until the max.bytes or timeout is hit
        Assert.assertEquals(1, consumerManager.delayedReadTasks.size());
        Thread.sleep(100);
        Assert.assertEquals(1, consumerManager.delayedReadTasks.size());
        f.get();
        Assert.assertTrue("Callback not called", sawCallback);
        Assert.assertNull("Callback exception should not be populated", ConsumerManagerTest.actualException);
    }

    @Test
    public void testBackoffMsUpdatesReadTaskExpiry() throws Exception {
        Properties props = new Properties();
        int backoffMs = 1000;
        props.setProperty(CONSUMER_ITERATOR_BACKOFF_MS_CONFIG, Integer.toString(backoffMs));
        // This setting supports the testConsumerOverrides test. It is otherwise benign and should
        // not affect other tests.
        props.setProperty("exclude.internal.topics", "false");
        config = new KafkaRestConfig(props, new SystemTime());
        mdObserver = EasyMock.createMock(MetadataObserver.class);
        consumerFactory = EasyMock.createMock(ConsumerFactory.class);
        consumerManager = new ConsumerManager(config, mdObserver, consumerFactory);
        expectCreateNoData();
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        Future f = readTopicFuture(cid, ConsumerManagerTest.topicName, Long.MAX_VALUE, new ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                sawCallback = true;
                ConsumerManagerTest.actualRecords = records;
                ConsumerManagerTest.actualException = e;
            }
        });
        Thread.sleep(100);
        ConsumerManager.RunnableReadTask readTask = consumerManager.delayedReadTasks.peek();
        if (readTask == null) {
            Assert.fail("Could not get read task in time. It should not be null");
        }
        long delay = readTask.getDelay(TimeUnit.MILLISECONDS);
        Assert.assertTrue((delay < backoffMs));
        Assert.assertTrue((delay > (backoffMs * 0.5)));
        f.get();
        verifyRead(Collections.<ConsumerRecord<byte[], byte[]>>emptyList(), null);
    }

    @Test
    public void testConsumerExpirationIsUpdated() throws Exception {
        expectCreateNoData();
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        ConsumerState state = consumerManager.getConsumerInstance(ConsumerManagerTest.groupName, cid);
        long initialExpiration = state.expiration;
        Future f = readTopicFuture(cid, ConsumerManagerTest.topicName, Long.MAX_VALUE, new ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                sawCallback = true;
                ConsumerManagerTest.actualRecords = records;
                ConsumerManagerTest.actualException = e;
            }
        });
        Thread.sleep(100);
        Assert.assertTrue(((state.expiration) > initialExpiration));
        initialExpiration = state.expiration;
        f.get();
        Assert.assertTrue(((state.expiration) > initialExpiration));
        verifyRead(Collections.<ConsumerRecord<byte[], byte[]>>emptyList(), null);
        initialExpiration = state.expiration;
        consumerManager.commitOffsets(ConsumerManagerTest.groupName, cid, new ConsumerManager.CommitCallback() {
            @Override
            public void onCompletion(List<TopicPartitionOffset> offsets, Exception e) {
                sawCallback = true;
                ConsumerManagerTest.actualException = e;
                ConsumerManagerTest.actualOffsets = offsets;
            }
        }).get();
        Assert.assertTrue(((state.expiration) > initialExpiration));
    }

    @Test
    public void testReadInvalidInstanceFails() {
        readAndExpectImmediateNotFound("invalid", ConsumerManagerTest.topicName);
    }

    @Test
    public void testReadInvalidTopicFails() throws Exception, ExecutionException {
        final String invalidTopicName = "invalidtopic";
        expectCreate(null);
        EasyMock.expect(mdObserver.topicExists(invalidTopicName)).andReturn(false);
        EasyMock.replay(mdObserver, consumerFactory);
        String instanceId = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        readAndExpectImmediateNotFound(instanceId, invalidTopicName);
        EasyMock.verify(mdObserver, consumerFactory);
    }

    @Test(expected = RestNotFoundException.class)
    public void testDeleteInvalidConsumer() {
        consumerManager.deleteConsumer(ConsumerManagerTest.groupName, "invalidinstance");
    }

    @Test
    public void testConsumerExceptions() throws Exception {
        // We should be able to handle an exception thrown by the consumer, then issue another
        // request that succeeds and still see all the data
        final List<ConsumerRecord<byte[], byte[]>> referenceRecords = referenceRecords(3);
        referenceRecords.add(null);// trigger exception

        Map<Integer, List<ConsumerRecord<byte[], byte[]>>> referenceSchedule = new HashMap<Integer, List<ConsumerRecord<byte[], byte[]>>>();
        referenceSchedule.put(50, referenceRecords);
        Map<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>> schedules = new HashMap<String, List<Map<Integer, List<ConsumerRecord<byte[], byte[]>>>>>();
        schedules.put(ConsumerManagerTest.topicName, Arrays.asList(referenceSchedule));
        expectCreate(schedules);
        EasyMock.expect(mdObserver.topicExists(ConsumerManagerTest.topicName)).andReturn(true).times(2);
        EasyMock.replay(mdObserver, consumerFactory);
        String cid = consumerManager.createConsumer(ConsumerManagerTest.groupName, new ConsumerInstanceConfig(EmbeddedFormat.BINARY));
        // First read should result in exception.
        sawCallback = false;
        ConsumerManagerTest.actualException = null;
        ConsumerManagerTest.actualRecords = null;
        readTopic(cid);
        Assert.assertTrue("Callback not called", sawCallback);
        Assert.assertNotNull("Callback exception should be populated", ConsumerManagerTest.actualException);
        Assert.assertNull("Callback with exception should not have any records", ConsumerManagerTest.actualRecords);
        // Second read should recover and return all the data.
        sawCallback = false;
        readTopic(cid, new ConsumerReadCallback<byte[], byte[]>() {
            @Override
            public void onCompletion(List<? extends ConsumerRecord<byte[], byte[]>> records, RestException e) {
                sawCallback = true;
                Assert.assertNull(e);
                Assert.assertEquals(referenceRecords, records);
            }
        });
        Assert.assertTrue(sawCallback);
        EasyMock.verify(mdObserver, consumerFactory);
    }
}

