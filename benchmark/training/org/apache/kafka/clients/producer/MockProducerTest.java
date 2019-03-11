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
package org.apache.kafka.clients.producer;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


public class MockProducerTest {
    private final String topic = "topic";

    private MockProducer<byte[], byte[]> producer;

    private final ProducerRecord<byte[], byte[]> record1 = new ProducerRecord(topic, "key1".getBytes(), "value1".getBytes());

    private final ProducerRecord<byte[], byte[]> record2 = new ProducerRecord(topic, "key2".getBytes(), "value2".getBytes());

    @Test
    public void testAutoCompleteMock() throws Exception {
        buildMockProducer(true);
        Future<RecordMetadata> metadata = producer.send(record1);
        Assert.assertTrue("Send should be immediately complete", metadata.isDone());
        Assert.assertFalse("Send should be successful", isError(metadata));
        Assert.assertEquals("Offset should be 0", 0L, metadata.get().offset());
        Assert.assertEquals(topic, metadata.get().topic());
        Assert.assertEquals("We should have the record in our history", Collections.singletonList(record1), producer.history());
        producer.clear();
        Assert.assertEquals("Clear should erase our history", 0, producer.history().size());
    }

    @Test
    public void testPartitioner() throws Exception {
        PartitionInfo partitionInfo0 = new PartitionInfo(topic, 0, null, null, null);
        PartitionInfo partitionInfo1 = new PartitionInfo(topic, 1, null, null, null);
        Cluster cluster = new Cluster(null, new ArrayList<org.apache.kafka.common.Node>(0), Arrays.asList(partitionInfo0, partitionInfo1), Collections.<String>emptySet(), Collections.<String>emptySet());
        MockProducer<String, String> producer = new MockProducer(cluster, true, new DefaultPartitioner(), new StringSerializer(), new StringSerializer());
        ProducerRecord<String, String> record = new ProducerRecord(topic, "key", "value");
        Future<RecordMetadata> metadata = producer.send(record);
        Assert.assertEquals("Partition should be correct", 1, metadata.get().partition());
        producer.clear();
        Assert.assertEquals("Clear should erase our history", 0, producer.history().size());
        producer.close();
    }

    @Test
    public void testManualCompletion() throws Exception {
        buildMockProducer(false);
        Future<RecordMetadata> md1 = producer.send(record1);
        Assert.assertFalse("Send shouldn't have completed", md1.isDone());
        Future<RecordMetadata> md2 = producer.send(record2);
        Assert.assertFalse("Send shouldn't have completed", md2.isDone());
        Assert.assertTrue("Complete the first request", producer.completeNext());
        Assert.assertFalse("Requst should be successful", isError(md1));
        Assert.assertFalse("Second request still incomplete", md2.isDone());
        IllegalArgumentException e = new IllegalArgumentException("blah");
        Assert.assertTrue("Complete the second request with an error", producer.errorNext(e));
        try {
            md2.get();
            Assert.fail("Expected error to be thrown");
        } catch (ExecutionException err) {
            Assert.assertEquals(e, err.getCause());
        }
        Assert.assertFalse("No more requests to complete", producer.completeNext());
        Future<RecordMetadata> md3 = producer.send(record1);
        Future<RecordMetadata> md4 = producer.send(record2);
        Assert.assertTrue("Requests should not be completed.", ((!(md3.isDone())) && (!(md4.isDone()))));
        producer.flush();
        Assert.assertTrue("Requests should be completed.", ((md3.isDone()) && (md4.isDone())));
    }

    @Test
    public void shouldInitTransactions() {
        buildMockProducer(true);
        producer.initTransactions();
        Assert.assertTrue(producer.transactionInitialized());
    }

    @Test
    public void shouldThrowOnInitTransactionIfProducerAlreadyInitializedForTransactions() {
        buildMockProducer(true);
        producer.initTransactions();
        try {
            producer.initTransactions();
            Assert.fail("Should have thrown as producer is already initialized");
        } catch (IllegalStateException e) {
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowOnBeginTransactionIfTransactionsNotInitialized() {
        buildMockProducer(true);
        producer.beginTransaction();
    }

    @Test
    public void shouldBeginTransactions() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        Assert.assertTrue(producer.transactionInFlight());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowOnSendOffsetsToTransactionIfTransactionsNotInitialized() {
        buildMockProducer(true);
        producer.sendOffsetsToTransaction(null, null);
    }

    @Test
    public void shouldThrowOnSendOffsetsToTransactionTransactionIfNoTransactionGotStarted() {
        buildMockProducer(true);
        producer.initTransactions();
        try {
            producer.sendOffsetsToTransaction(null, null);
            Assert.fail("Should have thrown as producer has no open transaction");
        } catch (IllegalStateException e) {
        }
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowOnCommitIfTransactionsNotInitialized() {
        buildMockProducer(true);
        producer.commitTransaction();
    }

    @Test
    public void shouldThrowOnCommitTransactionIfNoTransactionGotStarted() {
        buildMockProducer(true);
        producer.initTransactions();
        try {
            producer.commitTransaction();
            Assert.fail("Should have thrown as producer has no open transaction");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldCommitEmptyTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.commitTransaction();
        Assert.assertFalse(producer.transactionInFlight());
        Assert.assertTrue(producer.transactionCommitted());
        Assert.assertFalse(producer.transactionAborted());
    }

    @Test
    public void shouldCountCommittedTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        MatcherAssert.assertThat(producer.commitCount(), CoreMatchers.equalTo(0L));
        producer.commitTransaction();
        MatcherAssert.assertThat(producer.commitCount(), CoreMatchers.equalTo(1L));
    }

    @Test
    public void shouldNotCountAbortedTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.abortTransaction();
        producer.beginTransaction();
        producer.commitTransaction();
        MatcherAssert.assertThat(producer.commitCount(), CoreMatchers.equalTo(1L));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowOnAbortIfTransactionsNotInitialized() {
        buildMockProducer(true);
        producer.abortTransaction();
    }

    @Test
    public void shouldThrowOnAbortTransactionIfNoTransactionGotStarted() {
        buildMockProducer(true);
        producer.initTransactions();
        try {
            producer.abortTransaction();
            Assert.fail("Should have thrown as producer has no open transaction");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldAbortEmptyTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.abortTransaction();
        Assert.assertFalse(producer.transactionInFlight());
        Assert.assertTrue(producer.transactionAborted());
        Assert.assertFalse(producer.transactionCommitted());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowFenceProducerIfTransactionsNotInitialized() {
        buildMockProducer(true);
        producer.fenceProducer();
    }

    @Test
    public void shouldThrowOnBeginTransactionsIfProducerGotFenced() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.fenceProducer();
        try {
            producer.beginTransaction();
            Assert.fail("Should have thrown as producer is fenced off");
        } catch (ProducerFencedException e) {
        }
    }

    @Test
    public void shouldThrowOnSendIfProducerGotFenced() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.fenceProducer();
        try {
            producer.send(null);
            Assert.fail("Should have thrown as producer is fenced off");
        } catch (KafkaException e) {
            Assert.assertTrue("The root cause of the exception should be ProducerFenced", ((e.getCause()) instanceof ProducerFencedException));
        }
    }

    @Test
    public void shouldThrowOnSendOffsetsToTransactionIfProducerGotFenced() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.fenceProducer();
        try {
            producer.sendOffsetsToTransaction(null, null);
            Assert.fail("Should have thrown as producer is fenced off");
        } catch (ProducerFencedException e) {
        }
    }

    @Test
    public void shouldThrowOnCommitTransactionIfProducerGotFenced() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.fenceProducer();
        try {
            producer.commitTransaction();
            Assert.fail("Should have thrown as producer is fenced off");
        } catch (ProducerFencedException e) {
        }
    }

    @Test
    public void shouldThrowOnAbortTransactionIfProducerGotFenced() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.fenceProducer();
        try {
            producer.abortTransaction();
            Assert.fail("Should have thrown as producer is fenced off");
        } catch (ProducerFencedException e) {
        }
    }

    @Test
    public void shouldPublishMessagesOnlyAfterCommitIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.send(record1);
        producer.send(record2);
        Assert.assertTrue(producer.history().isEmpty());
        producer.commitTransaction();
        List<ProducerRecord<byte[], byte[]>> expectedResult = new ArrayList<>();
        expectedResult.add(record1);
        expectedResult.add(record2);
        MatcherAssert.assertThat(producer.history(), CoreMatchers.equalTo(expectedResult));
    }

    @Test
    public void shouldFlushOnCommitForNonAutoCompleteIfTransactionsAreEnabled() {
        buildMockProducer(false);
        producer.initTransactions();
        producer.beginTransaction();
        Future<RecordMetadata> md1 = producer.send(record1);
        Future<RecordMetadata> md2 = producer.send(record2);
        Assert.assertFalse(md1.isDone());
        Assert.assertFalse(md2.isDone());
        producer.commitTransaction();
        Assert.assertTrue(md1.isDone());
        Assert.assertTrue(md2.isDone());
    }

    @Test
    public void shouldDropMessagesOnAbortIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.send(record1);
        producer.send(record2);
        producer.abortTransaction();
        Assert.assertTrue(producer.history().isEmpty());
        producer.beginTransaction();
        producer.commitTransaction();
        Assert.assertTrue(producer.history().isEmpty());
    }

    @Test
    public void shouldThrowOnAbortForNonAutoCompleteIfTransactionsAreEnabled() throws Exception {
        buildMockProducer(false);
        producer.initTransactions();
        producer.beginTransaction();
        Future<RecordMetadata> md1 = producer.send(record1);
        Assert.assertFalse(md1.isDone());
        producer.abortTransaction();
        Assert.assertTrue(md1.isDone());
    }

    @Test
    public void shouldPreserveCommittedMessagesOnAbortIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.send(record1);
        producer.send(record2);
        producer.commitTransaction();
        producer.beginTransaction();
        producer.abortTransaction();
        List<ProducerRecord<byte[], byte[]>> expectedResult = new ArrayList<>();
        expectedResult.add(record1);
        expectedResult.add(record2);
        MatcherAssert.assertThat(producer.history(), CoreMatchers.equalTo(expectedResult));
    }

    @Test
    public void shouldPublishConsumerGroupOffsetsOnlyAfterCommitIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        String group1 = "g1";
        Map<TopicPartition, OffsetAndMetadata> group1Commit = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(73L, null));
            }
        };
        String group2 = "g2";
        Map<TopicPartition, OffsetAndMetadata> group2Commit = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(101L, null));
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(21L, null));
            }
        };
        producer.sendOffsetsToTransaction(group1Commit, group1);
        producer.sendOffsetsToTransaction(group2Commit, group2);
        Assert.assertTrue(producer.consumerGroupOffsetsHistory().isEmpty());
        Map<String, Map<TopicPartition, OffsetAndMetadata>> expectedResult = new HashMap<>();
        expectedResult.put(group1, group1Commit);
        expectedResult.put(group2, group2Commit);
        producer.commitTransaction();
        MatcherAssert.assertThat(producer.consumerGroupOffsetsHistory(), CoreMatchers.equalTo(Collections.singletonList(expectedResult)));
    }

    @Test
    public void shouldThrowOnNullConsumerGroupIdWhenSendOffsetsToTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        try {
            producer.sendOffsetsToTransaction(Collections.<TopicPartition, OffsetAndMetadata>emptyMap(), null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void shouldIgnoreEmptyOffsetsWhenSendOffsetsToTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        producer.sendOffsetsToTransaction(Collections.<TopicPartition, OffsetAndMetadata>emptyMap(), "groupId");
        Assert.assertFalse(producer.sentOffsets());
    }

    @Test
    public void shouldAddOffsetsWhenSendOffsetsToTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        Assert.assertFalse(producer.sentOffsets());
        Map<TopicPartition, OffsetAndMetadata> groupCommit = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
            }
        };
        producer.sendOffsetsToTransaction(groupCommit, "groupId");
        Assert.assertTrue(producer.sentOffsets());
    }

    @Test
    public void shouldResetSentOffsetsFlagOnlyWhenBeginningNewTransaction() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        Assert.assertFalse(producer.sentOffsets());
        Map<TopicPartition, OffsetAndMetadata> groupCommit = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
            }
        };
        producer.sendOffsetsToTransaction(groupCommit, "groupId");
        producer.commitTransaction();// commit should not reset "sentOffsets" flag

        Assert.assertTrue(producer.sentOffsets());
        producer.beginTransaction();
        Assert.assertFalse(producer.sentOffsets());
    }

    @Test
    public void shouldPublishLatestAndCumulativeConsumerGroupOffsetsOnlyAfterCommitIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        String group = "g";
        Map<TopicPartition, OffsetAndMetadata> groupCommit1 = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(73L, null));
            }
        };
        Map<TopicPartition, OffsetAndMetadata> groupCommit2 = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(101L, null));
                put(new TopicPartition(topic, 2), new OffsetAndMetadata(21L, null));
            }
        };
        producer.sendOffsetsToTransaction(groupCommit1, group);
        producer.sendOffsetsToTransaction(groupCommit2, group);
        Assert.assertTrue(producer.consumerGroupOffsetsHistory().isEmpty());
        Map<String, Map<TopicPartition, OffsetAndMetadata>> expectedResult = new HashMap<>();
        expectedResult.put(group, new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(101L, null));
                put(new TopicPartition(topic, 2), new OffsetAndMetadata(21L, null));
            }
        });
        producer.commitTransaction();
        MatcherAssert.assertThat(producer.consumerGroupOffsetsHistory(), CoreMatchers.equalTo(Collections.singletonList(expectedResult)));
    }

    @Test
    public void shouldDropConsumerGroupOffsetsOnAbortIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        String group = "g";
        Map<TopicPartition, OffsetAndMetadata> groupCommit = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(73L, null));
            }
        };
        producer.sendOffsetsToTransaction(groupCommit, group);
        producer.abortTransaction();
        producer.beginTransaction();
        producer.commitTransaction();
        Assert.assertTrue(producer.consumerGroupOffsetsHistory().isEmpty());
    }

    @Test
    public void shouldPreserveCommittedConsumerGroupsOffsetsOnAbortIfTransactionsAreEnabled() {
        buildMockProducer(true);
        producer.initTransactions();
        producer.beginTransaction();
        String group = "g";
        Map<TopicPartition, OffsetAndMetadata> groupCommit = new HashMap<TopicPartition, OffsetAndMetadata>() {
            {
                put(new TopicPartition(topic, 0), new OffsetAndMetadata(42L, null));
                put(new TopicPartition(topic, 1), new OffsetAndMetadata(73L, null));
            }
        };
        producer.sendOffsetsToTransaction(groupCommit, group);
        producer.commitTransaction();
        producer.beginTransaction();
        producer.abortTransaction();
        Map<String, Map<TopicPartition, OffsetAndMetadata>> expectedResult = new HashMap<>();
        expectedResult.put(group, groupCommit);
        MatcherAssert.assertThat(producer.consumerGroupOffsetsHistory(), CoreMatchers.equalTo(Collections.singletonList(expectedResult)));
    }

    @Test
    public void shouldThrowOnInitTransactionIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.initTransactions();
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowOnSendIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.send(null);
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowOnBeginTransactionIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.beginTransaction();
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowSendOffsetsToTransactionIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.sendOffsetsToTransaction(null, null);
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowOnCommitTransactionIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.commitTransaction();
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowOnAbortTransactionIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.abortTransaction();
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowOnFenceProducerIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.fenceProducer();
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldThrowOnFlushProducerIfProducerIsClosed() {
        buildMockProducer(true);
        producer.close();
        try {
            producer.flush();
            Assert.fail("Should have thrown as producer is already closed");
        } catch (IllegalStateException e) {
        }
    }

    @Test
    public void shouldBeFlushedIfNoBufferedRecords() {
        buildMockProducer(true);
        Assert.assertTrue(producer.flushed());
    }

    @Test
    public void shouldBeFlushedWithAutoCompleteIfBufferedRecords() {
        buildMockProducer(true);
        producer.send(record1);
        Assert.assertTrue(producer.flushed());
    }

    @Test
    public void shouldNotBeFlushedWithNoAutoCompleteIfBufferedRecords() {
        buildMockProducer(false);
        producer.send(record1);
        Assert.assertFalse(producer.flushed());
    }

    @Test
    public void shouldNotBeFlushedAfterFlush() {
        buildMockProducer(false);
        producer.send(record1);
        producer.flush();
        Assert.assertTrue(producer.flushed());
    }
}

