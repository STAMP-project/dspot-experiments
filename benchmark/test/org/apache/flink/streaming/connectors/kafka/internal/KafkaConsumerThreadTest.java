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
package org.apache.flink.streaming.connectors.kafka.internal;


import KafkaTopicPartitionStateSentinel.EARLIEST_OFFSET;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.flink.api.common.io.ratelimiting.FlinkConnectorRateLimiter;
import org.apache.flink.api.common.io.ratelimiting.GuavaFlinkConnectorRateLimiter;
import org.apache.flink.core.testutils.MultiShotLatch;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.streaming.api.operators.StreamingRuntimeContext;
import org.apache.flink.streaming.connectors.kafka.internals.ClosableBlockingQueue;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaCommitCallback;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartition;
import org.apache.flink.streaming.connectors.kafka.internals.KafkaTopicPartitionState;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;


/**
 * Unit tests for the {@link KafkaConsumerThread}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Handover.class)
public class KafkaConsumerThreadTest {
    @Test(timeout = 10000)
    public void testCloseWithoutAssignedPartitions() throws Exception {
        // no initial assignment
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(new LinkedHashMap<TopicPartition, Long>(), Collections.<TopicPartition, Long>emptyMap(), false, null, null);
        // setup latch so the test waits until testThread is blocked on getBatchBlocking method
        final MultiShotLatch getBatchBlockingInvoked = new MultiShotLatch();
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>>() {
            @Override
            public List<KafkaTopicPartitionState<TopicPartition>> getBatchBlocking() throws InterruptedException {
                getBatchBlockingInvoked.trigger();
                return super.getBatchBlocking();
            }
        };
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        getBatchBlockingInvoked.await();
        shutdown();
        join();
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer initially had no assignments
     *  - new unassigned partitions already have defined offsets
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassigningPartitionsWithDefinedOffsetsWhenNoInitialAssignment() throws Exception {
        final String testTopic = "test-topic";
        // -------- new partitions with defined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        newPartition1.setOffset(23L);
        KafkaTopicPartitionState<TopicPartition> newPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        newPartition2.setOffset(31L);
        final List<KafkaTopicPartitionState<TopicPartition>> newPartitions = new ArrayList<>(2);
        newPartitions.add(newPartition1);
        newPartitions.add(newPartition2);
        // -------- setup mock KafkaConsumer --------
        // no initial assignment
        final Map<TopicPartition, Long> mockConsumerAssignmentsAndPositions = new LinkedHashMap<>();
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsAndPositions, Collections.<TopicPartition, Long>emptyMap(), false, null, null);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            unassignedPartitionsQueue.add(newPartition);
        }
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        testThread.startPartitionReassignment();
        testThread.waitPartitionReassignmentComplete();
        // verify that the consumer called assign() with all new partitions, and that positions are correctly advanced
        Assert.assertEquals(newPartitions.size(), mockConsumerAssignmentsAndPositions.size());
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            Assert.assertTrue(mockConsumerAssignmentsAndPositions.containsKey(newPartition.getKafkaPartitionHandle()));
            // should be seeked to (offset in state + 1) because offsets in state represent the last processed record
            Assert.assertEquals(((newPartition.getOffset()) + 1), mockConsumerAssignmentsAndPositions.get(newPartition.getKafkaPartitionHandle()).longValue());
        }
        Assert.assertEquals(0, unassignedPartitionsQueue.size());
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer initially had no assignments
     *  - new unassigned partitions have undefined offsets (e.g. EARLIEST_OFFSET sentinel value)
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassigningPartitionsWithoutDefinedOffsetsWhenNoInitialAssignment() throws Exception {
        final String testTopic = "test-topic";
        // -------- new partitions with undefined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        newPartition1.setOffset(EARLIEST_OFFSET);
        KafkaTopicPartitionState<TopicPartition> newPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        newPartition2.setOffset(EARLIEST_OFFSET);
        final List<KafkaTopicPartitionState<TopicPartition>> newPartitions = new ArrayList<>(2);
        newPartitions.add(newPartition1);
        newPartitions.add(newPartition2);
        // -------- setup mock KafkaConsumer --------
        // no initial assignment
        final Map<TopicPartition, Long> mockConsumerAssignmentsAndPositions = new LinkedHashMap<>();
        // mock retrieved values that should replace the EARLIEST_OFFSET sentinels
        final Map<TopicPartition, Long> mockRetrievedPositions = new HashMap<>();
        mockRetrievedPositions.put(newPartition1.getKafkaPartitionHandle(), 23L);
        mockRetrievedPositions.put(newPartition2.getKafkaPartitionHandle(), 32L);
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsAndPositions, mockRetrievedPositions, false, null, null);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            unassignedPartitionsQueue.add(newPartition);
        }
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        testThread.startPartitionReassignment();
        testThread.waitPartitionReassignmentComplete();
        // the sentinel offset states should have been replaced with defined values according to the retrieved values
        Assert.assertEquals(((mockRetrievedPositions.get(newPartition1.getKafkaPartitionHandle())) - 1), newPartition1.getOffset());
        Assert.assertEquals(((mockRetrievedPositions.get(newPartition2.getKafkaPartitionHandle())) - 1), newPartition2.getOffset());
        // verify that the consumer called assign() with all new partitions, and that positions are correctly advanced
        Assert.assertEquals(newPartitions.size(), mockConsumerAssignmentsAndPositions.size());
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            Assert.assertTrue(mockConsumerAssignmentsAndPositions.containsKey(newPartition.getKafkaPartitionHandle()));
            // should be seeked to (offset in state + 1) because offsets in state represent the last processed record
            Assert.assertEquals(((newPartition.getOffset()) + 1), mockConsumerAssignmentsAndPositions.get(newPartition.getKafkaPartitionHandle()).longValue());
        }
        Assert.assertEquals(0, unassignedPartitionsQueue.size());
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer already have some assignments
     *  - new unassigned partitions already have defined offsets
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassigningPartitionsWithDefinedOffsets() throws Exception {
        final String testTopic = "test-topic";
        // -------- old partitions --------
        KafkaTopicPartitionState<TopicPartition> oldPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        oldPartition1.setOffset(23L);
        KafkaTopicPartitionState<TopicPartition> oldPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        oldPartition2.setOffset(32L);
        List<KafkaTopicPartitionState<TopicPartition>> oldPartitions = new ArrayList<>(2);
        oldPartitions.add(oldPartition1);
        oldPartitions.add(oldPartition2);
        // -------- new partitions with defined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 2), new TopicPartition(testTopic, 2));
        newPartition.setOffset(29L);
        List<KafkaTopicPartitionState<TopicPartition>> totalPartitions = new ArrayList<>(3);
        totalPartitions.add(oldPartition1);
        totalPartitions.add(oldPartition2);
        totalPartitions.add(newPartition);
        // -------- setup mock KafkaConsumer --------
        // has initial assignments
        final Map<TopicPartition, Long> mockConsumerAssignmentsAndPositions = new HashMap<>();
        for (KafkaTopicPartitionState<TopicPartition> oldPartition : oldPartitions) {
            mockConsumerAssignmentsAndPositions.put(oldPartition.getKafkaPartitionHandle(), ((oldPartition.getOffset()) + 1));
        }
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsAndPositions, Collections.<TopicPartition, Long>emptyMap(), false, null, null);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        unassignedPartitionsQueue.add(newPartition);
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        testThread.startPartitionReassignment();
        testThread.waitPartitionReassignmentComplete();
        // verify that the consumer called assign() with all new partitions, and that positions are correctly advanced
        Assert.assertEquals(totalPartitions.size(), mockConsumerAssignmentsAndPositions.size());
        // old partitions should be re-seeked to their previous positions
        for (KafkaTopicPartitionState<TopicPartition> partition : totalPartitions) {
            Assert.assertTrue(mockConsumerAssignmentsAndPositions.containsKey(partition.getKafkaPartitionHandle()));
            // should be seeked to (offset in state + 1) because offsets in state represent the last processed record
            Assert.assertEquals(((partition.getOffset()) + 1), mockConsumerAssignmentsAndPositions.get(partition.getKafkaPartitionHandle()).longValue());
        }
        Assert.assertEquals(0, unassignedPartitionsQueue.size());
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer already have some assignments
     *  - new unassigned partitions have undefined offsets (e.g. EARLIEST_OFFSET sentinel value)
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassigningPartitionsWithoutDefinedOffsets() throws Exception {
        final String testTopic = "test-topic";
        // -------- old partitions --------
        KafkaTopicPartitionState<TopicPartition> oldPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        oldPartition1.setOffset(23L);
        KafkaTopicPartitionState<TopicPartition> oldPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        oldPartition2.setOffset(32L);
        List<KafkaTopicPartitionState<TopicPartition>> oldPartitions = new ArrayList<>(2);
        oldPartitions.add(oldPartition1);
        oldPartitions.add(oldPartition2);
        // -------- new partitions with undefined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 2), new TopicPartition(testTopic, 2));
        newPartition.setOffset(EARLIEST_OFFSET);
        List<KafkaTopicPartitionState<TopicPartition>> totalPartitions = new ArrayList<>(3);
        totalPartitions.add(oldPartition1);
        totalPartitions.add(oldPartition2);
        totalPartitions.add(newPartition);
        // -------- setup mock KafkaConsumer --------
        // has initial assignments
        final Map<TopicPartition, Long> mockConsumerAssignmentsAndPositions = new HashMap<>();
        for (KafkaTopicPartitionState<TopicPartition> oldPartition : oldPartitions) {
            mockConsumerAssignmentsAndPositions.put(oldPartition.getKafkaPartitionHandle(), ((oldPartition.getOffset()) + 1));
        }
        // mock retrieved values that should replace the EARLIEST_OFFSET sentinels
        final Map<TopicPartition, Long> mockRetrievedPositions = new HashMap<>();
        mockRetrievedPositions.put(newPartition.getKafkaPartitionHandle(), 30L);
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsAndPositions, mockRetrievedPositions, false, null, null);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        unassignedPartitionsQueue.add(newPartition);
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        testThread.startPartitionReassignment();
        testThread.waitPartitionReassignmentComplete();
        // the sentinel offset states should have been replaced with defined values according to the retrieved positions
        Assert.assertEquals(((mockRetrievedPositions.get(newPartition.getKafkaPartitionHandle())) - 1), newPartition.getOffset());
        // verify that the consumer called assign() with all new partitions, and that positions are correctly advanced
        Assert.assertEquals(totalPartitions.size(), mockConsumerAssignmentsAndPositions.size());
        // old partitions should be re-seeked to their previous positions
        for (KafkaTopicPartitionState<TopicPartition> partition : totalPartitions) {
            Assert.assertTrue(mockConsumerAssignmentsAndPositions.containsKey(partition.getKafkaPartitionHandle()));
            // should be seeked to (offset in state + 1) because offsets in state represent the last processed record
            Assert.assertEquals(((partition.getOffset()) + 1), mockConsumerAssignmentsAndPositions.get(partition.getKafkaPartitionHandle()).longValue());
        }
        Assert.assertEquals(0, unassignedPartitionsQueue.size());
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer already have some assignments
     *  - new unassigned partitions already have defined offsets
     *  - the consumer was woken up prior to the reassignment
     *
     * <p>In this case, reassignment should not have occurred at all, and the consumer retains the original assignment.
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassigningPartitionsWithDefinedOffsetsWhenEarlyWakeup() throws Exception {
        final String testTopic = "test-topic";
        // -------- old partitions --------
        KafkaTopicPartitionState<TopicPartition> oldPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        oldPartition1.setOffset(23L);
        KafkaTopicPartitionState<TopicPartition> oldPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        oldPartition2.setOffset(32L);
        List<KafkaTopicPartitionState<TopicPartition>> oldPartitions = new ArrayList<>(2);
        oldPartitions.add(oldPartition1);
        oldPartitions.add(oldPartition2);
        // -------- new partitions with defined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 2), new TopicPartition(testTopic, 2));
        newPartition.setOffset(29L);
        // -------- setup mock KafkaConsumer --------
        // initial assignments
        final Map<TopicPartition, Long> mockConsumerAssignmentsToPositions = new LinkedHashMap<>();
        for (KafkaTopicPartitionState<TopicPartition> oldPartition : oldPartitions) {
            mockConsumerAssignmentsToPositions.put(oldPartition.getKafkaPartitionHandle(), ((oldPartition.getOffset()) + 1));
        }
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsToPositions, Collections.<TopicPartition, Long>emptyMap(), true, null, null);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        unassignedPartitionsQueue.add(newPartition);
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        // pause just before the reassignment so we can inject the wakeup
        testThread.waitPartitionReassignmentInvoked();
        setOffsetsToCommit(new HashMap<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata>(), Mockito.mock(KafkaCommitCallback.class));
        Mockito.verify(mockConsumer, Mockito.times(1)).wakeup();
        testThread.startPartitionReassignment();
        testThread.waitPartitionReassignmentComplete();
        // the consumer's assignment should have remained untouched
        Assert.assertEquals(oldPartitions.size(), mockConsumerAssignmentsToPositions.size());
        for (KafkaTopicPartitionState<TopicPartition> oldPartition : oldPartitions) {
            Assert.assertTrue(mockConsumerAssignmentsToPositions.containsKey(oldPartition.getKafkaPartitionHandle()));
            Assert.assertEquals(((oldPartition.getOffset()) + 1), mockConsumerAssignmentsToPositions.get(oldPartition.getKafkaPartitionHandle()).longValue());
        }
        // the new partitions should have been re-added to the unassigned partitions queue
        Assert.assertEquals(1, unassignedPartitionsQueue.size());
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer has no initial assignments
     *  - new unassigned partitions have undefined offsets
     *  - the consumer was woken up prior to the reassignment
     *
     * <p>In this case, reassignment should not have occurred at all, and the consumer retains the original assignment.
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassignPartitionsDefinedOffsetsWithoutInitialAssignmentsWhenEarlyWakeup() throws Exception {
        final String testTopic = "test-topic";
        // -------- new partitions with defined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        newPartition1.setOffset(EARLIEST_OFFSET);
        KafkaTopicPartitionState<TopicPartition> newPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        newPartition2.setOffset(EARLIEST_OFFSET);
        List<KafkaTopicPartitionState<TopicPartition>> newPartitions = new ArrayList<>(2);
        newPartitions.add(newPartition1);
        newPartitions.add(newPartition2);
        // -------- setup mock KafkaConsumer --------
        // no initial assignments
        final Map<TopicPartition, Long> mockConsumerAssignmentsAndPositions = new LinkedHashMap<>();
        // mock retrieved values that should replace the EARLIEST_OFFSET sentinels
        final Map<TopicPartition, Long> mockRetrievedPositions = new HashMap<>();
        mockRetrievedPositions.put(newPartition1.getKafkaPartitionHandle(), 23L);
        mockRetrievedPositions.put(newPartition2.getKafkaPartitionHandle(), 32L);
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsAndPositions, mockRetrievedPositions, true, null, null);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            unassignedPartitionsQueue.add(newPartition);
        }
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        // pause just before the reassignment so we can inject the wakeup
        testThread.waitPartitionReassignmentInvoked();
        setOffsetsToCommit(new HashMap<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata>(), Mockito.mock(KafkaCommitCallback.class));
        // make sure the consumer was actually woken up
        Mockito.verify(mockConsumer, Mockito.times(1)).wakeup();
        testThread.startPartitionReassignment();
        testThread.waitPartitionReassignmentComplete();
        // the consumer's assignment should have remained untouched (in this case, empty)
        Assert.assertEquals(0, mockConsumerAssignmentsAndPositions.size());
        // the new partitions should have been re-added to the unassigned partitions queue
        Assert.assertEquals(2, unassignedPartitionsQueue.size());
    }

    /**
     * Tests reassignment works correctly in the case when:
     *  - the consumer has no initial assignments
     *  - new unassigned partitions have undefined offsets
     *  - the consumer was woken up during the reassignment
     *
     * <p>In this case, reassignment should have completed, and the consumer is restored the wakeup call after the reassignment.
     *
     * <p>Setting a timeout because the test will not finish if there is logic error with
     * the reassignment flow.
     */
    @SuppressWarnings("unchecked")
    @Test(timeout = 10000)
    public void testReassignPartitionsDefinedOffsetsWithoutInitialAssignmentsWhenWakeupMidway() throws Exception {
        final String testTopic = "test-topic";
        // -------- new partitions with defined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        newPartition1.setOffset(EARLIEST_OFFSET);
        KafkaTopicPartitionState<TopicPartition> newPartition2 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 1), new TopicPartition(testTopic, 1));
        newPartition2.setOffset(EARLIEST_OFFSET);
        List<KafkaTopicPartitionState<TopicPartition>> newPartitions = new ArrayList<>(2);
        newPartitions.add(newPartition1);
        newPartitions.add(newPartition2);
        // -------- setup mock KafkaConsumer --------
        // no initial assignments
        final Map<TopicPartition, Long> mockConsumerAssignmentsAndPositions = new LinkedHashMap<>();
        // mock retrieved values that should replace the EARLIEST_OFFSET sentinels
        final Map<TopicPartition, Long> mockRetrievedPositions = new HashMap<>();
        mockRetrievedPositions.put(newPartition1.getKafkaPartitionHandle(), 23L);
        mockRetrievedPositions.put(newPartition2.getKafkaPartitionHandle(), 32L);
        // these latches are used to pause midway the reassignment process
        final OneShotLatch midAssignmentLatch = new OneShotLatch();
        final OneShotLatch continueAssigmentLatch = new OneShotLatch();
        final KafkaConsumer<byte[], byte[]> mockConsumer = KafkaConsumerThreadTest.createMockConsumer(mockConsumerAssignmentsAndPositions, mockRetrievedPositions, false, midAssignmentLatch, continueAssigmentLatch);
        // -------- setup new partitions to be polled from the unassigned partitions queue --------
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            unassignedPartitionsQueue.add(newPartition);
        }
        // -------- start test --------
        final KafkaConsumerThreadTest.TestKafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThread(mockConsumer, unassignedPartitionsQueue, new Handover());
        start();
        testThread.startPartitionReassignment();
        // wait until the reassignment has started
        midAssignmentLatch.await();
        setOffsetsToCommit(new HashMap<TopicPartition, org.apache.kafka.clients.consumer.OffsetAndMetadata>(), Mockito.mock(KafkaCommitCallback.class));
        // the wakeup in the setOffsetsToCommit() call should have been buffered, and not called on the consumer
        Mockito.verify(mockConsumer, Mockito.never()).wakeup();
        continueAssigmentLatch.trigger();
        testThread.waitPartitionReassignmentComplete();
        // verify that the consumer called assign() with all new partitions, and that positions are correctly advanced
        Assert.assertEquals(newPartitions.size(), mockConsumerAssignmentsAndPositions.size());
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            Assert.assertTrue(mockConsumerAssignmentsAndPositions.containsKey(newPartition.getKafkaPartitionHandle()));
            // should be seeked to (offset in state + 1) because offsets in state represent the last processed record
            Assert.assertEquals(((newPartition.getOffset()) + 1), mockConsumerAssignmentsAndPositions.get(newPartition.getKafkaPartitionHandle()).longValue());
        }
        // after the reassignment, the consumer should be restored the wakeup call
        Mockito.verify(mockConsumer, Mockito.times(1)).wakeup();
        Assert.assertEquals(0, unassignedPartitionsQueue.size());
    }

    @Test(timeout = 10000)
    public void testRatelimiting() throws Exception {
        final String testTopic = "test-topic-ratelimit";
        // -------- setup mock KafkaConsumer with test data --------
        final int partition = 0;
        final byte[] payload = new byte[]{ 1 };
        final List<ConsumerRecord<byte[], byte[]>> records = Arrays.asList(new ConsumerRecord(testTopic, partition, 15, payload, payload), new ConsumerRecord(testTopic, partition, 16, payload, payload));
        final Map<TopicPartition, List<ConsumerRecord<byte[], byte[]>>> data = new HashMap<>();
        data.put(new TopicPartition(testTopic, partition), records);
        final ConsumerRecords<byte[], byte[]> consumerRecords = new ConsumerRecords(data);
        // Sleep for one second in each consumer.poll() call to return 24 bytes / second
        final KafkaConsumer<byte[], byte[]> mockConsumer = Mockito.mock(KafkaConsumer.class);
        PowerMockito.when(mockConsumer.poll(ArgumentMatchers.anyLong())).thenAnswer(( invocationOnMock) -> consumerRecords);
        whenNew(KafkaConsumer.class).withAnyArguments().thenReturn(mockConsumer);
        // -------- new partitions with defined offsets --------
        KafkaTopicPartitionState<TopicPartition> newPartition1 = new KafkaTopicPartitionState(new KafkaTopicPartition(testTopic, 0), new TopicPartition(testTopic, 0));
        newPartition1.setOffset(EARLIEST_OFFSET);
        List<KafkaTopicPartitionState<TopicPartition>> newPartitions = new ArrayList<>(1);
        newPartitions.add(newPartition1);
        final ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue = new ClosableBlockingQueue();
        for (KafkaTopicPartitionState<TopicPartition> newPartition : newPartitions) {
            unassignedPartitionsQueue.add(newPartition);
        }
        // --- ratelimiting properties ---
        StreamingRuntimeContext mockRuntimeContext = Mockito.mock(StreamingRuntimeContext.class);
        Mockito.when(mockRuntimeContext.getNumberOfParallelSubtasks()).thenReturn(1);
        Properties properties = new Properties();
        KafkaConsumerCallBridge09 mockBridge = Mockito.mock(KafkaConsumerCallBridge09.class);
        // -- mock Handover and logger ---
        Handover mockHandover = PowerMockito.mock(Handover.class);
        doNothing().when(mockHandover).produce(ArgumentMatchers.any());
        Logger mockLogger = Mockito.mock(Logger.class);
        MetricGroup metricGroup = new UnregisteredMetricsGroup();
        FlinkConnectorRateLimiter rateLimiter = new GuavaFlinkConnectorRateLimiter();
        rateLimiter.setRate(1L);
        rateLimiter.open(mockRuntimeContext);
        // -- Test Kafka Consumer thread ---
        KafkaConsumerThread testThread = new KafkaConsumerThreadTest.TestKafkaConsumerThreadRateLimit(mockLogger, mockHandover, properties, unassignedPartitionsQueue, mockBridge, "test", 30L, false, metricGroup, metricGroup, mockConsumer, rateLimiter);
        testThread.start();
        // Wait for 4 seconds to ensure atleast 2 calls to consumer.poll()
        testThread.join(5000);
        Assert.assertNotNull(testThread.getRateLimiter());
        Assert.assertEquals(testThread.getRateLimiter().getRate(), 1, 0);
        // In a period of 5 seconds, no more than 3 calls to poll should be made.
        // The expected rate is 1 byte / second and we read 4 bytes in every consumer.poll()
        // call. The rate limiter should thus slow down the call by 4 seconds when the rate takes
        // effect.
        Mockito.verify(mockConsumer, Mockito.times(3)).poll(ArgumentMatchers.anyLong());
        testThread.shutdown();
    }

    /**
     * A testable {@link KafkaConsumerThread} that injects multiple latches exactly before and after
     * partition reassignment, so that tests are eligible to setup various conditions before the reassignment happens
     * and inspect reassignment results after it is completed.
     */
    private static class TestKafkaConsumerThread extends KafkaConsumerThread {
        private final KafkaConsumer<byte[], byte[]> mockConsumer;

        private final MultiShotLatch preReassignmentLatch = new MultiShotLatch();

        private final MultiShotLatch startReassignmentLatch = new MultiShotLatch();

        private final MultiShotLatch reassignmentCompleteLatch = new MultiShotLatch();

        private final MultiShotLatch postReassignmentLatch = new MultiShotLatch();

        public TestKafkaConsumerThread(KafkaConsumer<byte[], byte[]> mockConsumer, ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue, Handover handover) {
            super(Mockito.mock(Logger.class), handover, new Properties(), unassignedPartitionsQueue, new KafkaConsumerCallBridge09(), "test-kafka-consumer-thread", 0, false, new UnregisteredMetricsGroup(), new UnregisteredMetricsGroup(), null);
            this.mockConsumer = mockConsumer;
        }

        public void waitPartitionReassignmentInvoked() throws InterruptedException {
            preReassignmentLatch.await();
        }

        public void startPartitionReassignment() {
            startReassignmentLatch.trigger();
        }

        public void waitPartitionReassignmentComplete() throws InterruptedException {
            reassignmentCompleteLatch.await();
        }

        public void endPartitionReassignment() {
            postReassignmentLatch.trigger();
        }

        @Override
        KafkaConsumer<byte[], byte[]> getConsumer(Properties kafkaProperties) {
            return mockConsumer;
        }

        @Override
        void reassignPartitions(List<KafkaTopicPartitionState<TopicPartition>> newPartitions) throws Exception {
            // triggers blocking calls on waitPartitionReassignmentInvoked()
            preReassignmentLatch.trigger();
            // waits for startPartitionReassignment() to be called
            startReassignmentLatch.await();
            try {
                super.reassignPartitions(newPartitions);
            } finally {
                // triggers blocking calls on waitPartitionReassignmentComplete()
                reassignmentCompleteLatch.trigger();
                // waits for endPartitionReassignment() to be called
                postReassignmentLatch.await();
            }
        }
    }

    /**
     * A testable KafkaConsumer thread to test the ratelimiting feature using user-defined properties.
     * The mockConsumer does not mock all the methods mocked in {@link TestKafkaConsumerThread}.
     */
    private static class TestKafkaConsumerThreadRateLimit extends KafkaConsumerThread {
        KafkaConsumer mockConsumer;

        public TestKafkaConsumerThreadRateLimit(Logger log, Handover handover, Properties kafkaProperties, ClosableBlockingQueue<KafkaTopicPartitionState<TopicPartition>> unassignedPartitionsQueue, KafkaConsumerCallBridge09 consumerCallBridge, String threadName, long pollTimeout, boolean useMetrics, MetricGroup consumerMetricGroup, MetricGroup subtaskMetricGroup, KafkaConsumer mockConsumer, FlinkConnectorRateLimiter rateLimiter) {
            super(log, handover, kafkaProperties, unassignedPartitionsQueue, consumerCallBridge, threadName, pollTimeout, useMetrics, consumerMetricGroup, subtaskMetricGroup, rateLimiter);
            this.mockConsumer = mockConsumer;
        }

        @Override
        public KafkaConsumer getConsumer(Properties properties) {
            return mockConsumer;
        }
    }
}

