/**
 * Copyright 2018 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.kafka.spout.trident;


import FirstPollOffsetStrategy.EARLIEST;
import FirstPollOffsetStrategy.LATEST;
import FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST;
import FirstPollOffsetStrategy.UNCOMMITTED_LATEST;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.SpoutWithMockedConsumerSetupHelper;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.topology.TransactionAttempt;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class KafkaTridentSpoutEmitterEmitTest {
    @Captor
    public ArgumentCaptor<List<Object>> emitCaptor;

    @Mock
    public TopologyContext topologyContextMock;

    @Mock
    public TridentCollector collectorMock = Mockito.mock(TridentCollector.class);

    private final MockConsumer<String, String> consumer = new MockConsumer(OffsetResetStrategy.NONE);

    private final TopicPartition partition = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 0);

    private final String topologyId = "topologyId";

    private final long firstOffsetInKafka = 0;

    private final int recordsInKafka = 100;

    private final long lastOffsetInKafka = 99;

    @Test
    public void testEmitNewBatchWithNullMetaUncommittedEarliest() {
        // Check that null meta makes the spout seek to EARLIEST, and that the returned meta is correct
        Map<String, Object> batchMeta = doEmitNewBatchTest(UNCOMMITTED_EARLIEST, collectorMock, partition, null);
        Mockito.verify(collectorMock, Mockito.times(recordsInKafka)).emit(emitCaptor.capture());
        List<List<Object>> emits = emitCaptor.getAllValues();
        Assert.assertThat(emits.get(0).get(0), Matchers.is(firstOffsetInKafka));
        Assert.assertThat(emits.get(((emits.size()) - 1)).get(0), Matchers.is(lastOffsetInKafka));
        KafkaTridentSpoutBatchMetadata deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(batchMeta);
        Assert.assertThat("The batch should start at the first offset of the polled records", deserializedMeta.getFirstOffset(), Matchers.is(firstOffsetInKafka));
        Assert.assertThat("The batch should end at the last offset of the polled messages", deserializedMeta.getLastOffset(), Matchers.is(lastOffsetInKafka));
    }

    @Test
    public void testEmitNewBatchWithNullMetaUncommittedLatest() {
        // Check that null meta makes the spout seek to LATEST, and that the returned meta is correct
        Map<String, Object> batchMeta = doEmitNewBatchTest(UNCOMMITTED_LATEST, collectorMock, partition, null);
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyList());
        KafkaTridentSpoutBatchMetadata deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(batchMeta);
        Assert.assertThat("The batch should start at the first offset of the polled records", deserializedMeta.getFirstOffset(), Matchers.is(lastOffsetInKafka));
        Assert.assertThat("The batch should end at the last offset of the polled messages", deserializedMeta.getLastOffset(), Matchers.is(lastOffsetInKafka));
    }

    @Test
    public void testEmitEmptyBatches() throws Exception {
        // Check that the emitter can handle emitting empty batches on a new partition.
        // If the spout is configured to seek to LATEST, or the partition is empty, the initial batches may be empty
        KafkaTridentSpoutEmitter<String, String> emitter = createEmitter(LATEST);
        KafkaTridentSpoutTopicPartition kttp = new KafkaTridentSpoutTopicPartition(partition);
        Map<String, Object> lastBatchMeta = null;
        // Emit 10 empty batches, simulating no new records being present in Kafka
        for (int i = 0; i < 10; i++) {
            TransactionAttempt txid = new TransactionAttempt(((long) (i)), 0);
            lastBatchMeta = emitter.emitPartitionBatchNew(txid, collectorMock, kttp, lastBatchMeta);
            KafkaTridentSpoutBatchMetadata deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(lastBatchMeta);
            Assert.assertThat("Since the first poll strategy is LATEST, the meta should indicate that the last message has already been emitted", deserializedMeta.getFirstOffset(), Matchers.is(lastOffsetInKafka));
            Assert.assertThat("Since the first poll strategy is LATEST, the meta should indicate that the last message has already been emitted", deserializedMeta.getLastOffset(), Matchers.is(lastOffsetInKafka));
        }
        // Add new records to Kafka, and check that the next batch contains these records
        long firstNewRecordOffset = (lastOffsetInKafka) + 1;
        int numNewRecords = 10;
        List<ConsumerRecord<String, String>> newRecords = SpoutWithMockedConsumerSetupHelper.createRecords(partition, firstNewRecordOffset, numNewRecords);
        newRecords.forEach(consumer::addRecord);
        lastBatchMeta = emitter.emitPartitionBatchNew(new TransactionAttempt(11L, 0), collectorMock, kttp, lastBatchMeta);
        Mockito.verify(collectorMock, Mockito.times(numNewRecords)).emit(emitCaptor.capture());
        List<List<Object>> emits = emitCaptor.getAllValues();
        Assert.assertThat(emits.get(0).get(0), Matchers.is(firstNewRecordOffset));
        Assert.assertThat(emits.get(((emits.size()) - 1)).get(0), Matchers.is(((firstNewRecordOffset + numNewRecords) - 1)));
        KafkaTridentSpoutBatchMetadata deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(lastBatchMeta);
        Assert.assertThat("The batch should start at the first offset of the polled records", deserializedMeta.getFirstOffset(), Matchers.is(firstNewRecordOffset));
        Assert.assertThat("The batch should end at the last offset of the polled messages", deserializedMeta.getLastOffset(), Matchers.is(((firstNewRecordOffset + numNewRecords) - 1)));
    }

    @Test
    public void testReEmitBatch() {
        // Check that a reemit emits exactly the same tuples as the last batch, even if Kafka returns more messages
        long firstEmittedOffset = 50;
        int numEmittedRecords = 10;
        KafkaTridentSpoutBatchMetadata batchMeta = new KafkaTridentSpoutBatchMetadata(firstEmittedOffset, ((firstEmittedOffset + numEmittedRecords) - 1), topologyId);
        KafkaTridentSpoutEmitter<String, String> emitter = createEmitter(UNCOMMITTED_EARLIEST);
        TransactionAttempt txid = new TransactionAttempt(10L, 0);
        KafkaTridentSpoutTopicPartition kttp = new KafkaTridentSpoutTopicPartition(partition);
        emitter.reEmitPartitionBatch(txid, collectorMock, kttp, batchMeta.toMap());
        Mockito.verify(collectorMock, Mockito.times(numEmittedRecords)).emit(emitCaptor.capture());
        List<List<Object>> emits = emitCaptor.getAllValues();
        Assert.assertThat(emits.get(0).get(0), Matchers.is(firstEmittedOffset));
        Assert.assertThat(emits.get(((emits.size()) - 1)).get(0), Matchers.is(((firstEmittedOffset + numEmittedRecords) - 1)));
    }

    @Test
    public void testReEmitBatchForOldTopologyWhenIgnoringCommittedOffsets() {
        // In some cases users will want to drop retrying old batches, e.g. if the topology should start over from scratch.
        // If the FirstPollOffsetStrategy ignores committed offsets, we should not retry batches for old topologies
        // The batch retry should be skipped entirely
        KafkaTridentSpoutBatchMetadata batchMeta = new KafkaTridentSpoutBatchMetadata(firstOffsetInKafka, lastOffsetInKafka, "a new storm id");
        KafkaTridentSpoutEmitter<String, String> emitter = createEmitter(EARLIEST);
        TransactionAttempt txid = new TransactionAttempt(10L, 0);
        KafkaTridentSpoutTopicPartition kttp = new KafkaTridentSpoutTopicPartition(partition);
        emitter.reEmitPartitionBatch(txid, collectorMock, kttp, batchMeta.toMap());
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyList());
    }

    @Test
    public void testEmitEmptyFirstBatch() {
        /**
         * Check that when the first batch after a redeploy is empty, the emitter does not restart at the pre-redeploy offset. STORM-3279.
         */
        long firstEmittedOffset = 50;
        int emittedRecords = 10;
        KafkaTridentSpoutBatchMetadata preRedeployLastMeta = new KafkaTridentSpoutBatchMetadata(firstEmittedOffset, ((firstEmittedOffset + emittedRecords) - 1), "an old topology");
        KafkaTridentSpoutEmitter<String, String> emitter = createEmitter(LATEST);
        TransactionAttempt txid = new TransactionAttempt(0L, 0);
        KafkaTridentSpoutTopicPartition kttp = new KafkaTridentSpoutTopicPartition(partition);
        Map<String, Object> meta = emitter.emitPartitionBatchNew(txid, collectorMock, kttp, preRedeployLastMeta.toMap());
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyList());
        KafkaTridentSpoutBatchMetadata deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(meta);
        Assert.assertThat(deserializedMeta.getFirstOffset(), Matchers.is(lastOffsetInKafka));
        Assert.assertThat(deserializedMeta.getLastOffset(), Matchers.is(lastOffsetInKafka));
        long firstNewRecordOffset = (lastOffsetInKafka) + 1;
        int numNewRecords = 10;
        List<ConsumerRecord<String, String>> newRecords = SpoutWithMockedConsumerSetupHelper.createRecords(partition, firstNewRecordOffset, numNewRecords);
        newRecords.forEach(consumer::addRecord);
        meta = emitter.emitPartitionBatchNew(txid, collectorMock, kttp, meta);
        Mockito.verify(collectorMock, Mockito.times(numNewRecords)).emit(emitCaptor.capture());
        List<List<Object>> emits = emitCaptor.getAllValues();
        Assert.assertThat(emits.get(0).get(0), Matchers.is(firstNewRecordOffset));
        Assert.assertThat(emits.get(((emits.size()) - 1)).get(0), Matchers.is(((firstNewRecordOffset + numNewRecords) - 1)));
        deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(meta);
        Assert.assertThat("The batch should start at the first offset of the polled records", deserializedMeta.getFirstOffset(), Matchers.is(firstNewRecordOffset));
        Assert.assertThat("The batch should end at the last offset of the polled messages", deserializedMeta.getLastOffset(), Matchers.is(((firstNewRecordOffset + numNewRecords) - 1)));
    }

    @Test
    public void testEarliestStrategyWhenTopologyIsRedeployed() {
        /**
         * EARLIEST should be applied if the emitter is new and the topology has been redeployed (storm id has changed)
         */
        long preRestartEmittedOffset = 20;
        int preRestartEmittedRecords = 10;
        KafkaTridentSpoutBatchMetadata preExecutorRestartLastMeta = new KafkaTridentSpoutBatchMetadata(preRestartEmittedOffset, ((preRestartEmittedOffset + preRestartEmittedRecords) - 1), "Some older topology");
        KafkaTridentSpoutEmitter<String, String> emitter = createEmitter(EARLIEST);
        TransactionAttempt txid = new TransactionAttempt(0L, 0);
        KafkaTridentSpoutTopicPartition kttp = new KafkaTridentSpoutTopicPartition(partition);
        Map<String, Object> meta = emitter.emitPartitionBatchNew(txid, collectorMock, kttp, preExecutorRestartLastMeta.toMap());
        Mockito.verify(collectorMock, Mockito.times(recordsInKafka)).emit(emitCaptor.capture());
        List<List<Object>> emits = emitCaptor.getAllValues();
        Assert.assertThat(emits.get(0).get(0), Matchers.is(firstOffsetInKafka));
        Assert.assertThat(emits.get(((emits.size()) - 1)).get(0), Matchers.is(lastOffsetInKafka));
        KafkaTridentSpoutBatchMetadata deserializedMeta = KafkaTridentSpoutBatchMetadata.fromMap(meta);
        Assert.assertThat("The batch should start at the first offset of the polled records", deserializedMeta.getFirstOffset(), Matchers.is(firstOffsetInKafka));
        Assert.assertThat("The batch should end at the last offset of the polled messages", deserializedMeta.getLastOffset(), Matchers.is(lastOffsetInKafka));
    }

    @Test
    public void testLatestStrategyWhenTopologyIsRedeployed() {
        /**
         * EARLIEST should be applied if the emitter is new and the topology has been redeployed (storm id has changed)
         */
        long preRestartEmittedOffset = 20;
        int preRestartEmittedRecords = 10;
        KafkaTridentSpoutBatchMetadata preExecutorRestartLastMeta = new KafkaTridentSpoutBatchMetadata(preRestartEmittedOffset, ((preRestartEmittedOffset + preRestartEmittedRecords) - 1), "Some older topology");
        KafkaTridentSpoutEmitter<String, String> emitter = createEmitter(LATEST);
        TransactionAttempt txid = new TransactionAttempt(0L, 0);
        KafkaTridentSpoutTopicPartition kttp = new KafkaTridentSpoutTopicPartition(partition);
        Map<String, Object> meta = emitter.emitPartitionBatchNew(txid, collectorMock, kttp, preExecutorRestartLastMeta.toMap());
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyList());
    }
}

