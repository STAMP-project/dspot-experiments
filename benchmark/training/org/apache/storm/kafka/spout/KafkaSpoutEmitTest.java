/**
 * Copyright 2017 The Apache Software Foundation.
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
package org.apache.storm.kafka.spout;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Time.SimulatedTime;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class KafkaSpoutEmitTest {
    private final long offsetCommitPeriodMs = 2000;

    private final TopologyContext contextMock = Mockito.mock(TopologyContext.class);

    private final SpoutOutputCollector collectorMock = Mockito.mock(SpoutOutputCollector.class);

    private final Map<String, Object> conf = new HashMap<>();

    private final TopicPartition partition = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 1);

    private KafkaConsumer<String, String> consumerMock;

    private KafkaSpoutConfig<String, String> spoutConfig;

    @Test
    public void testNextTupleEmitsAtMostOneTuple() {
        // The spout should emit at most one message per call to nextTuple
        // This is necessary for Storm to be able to throttle the spout according to maxSpoutPending
        KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition);
        Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>();
        records.put(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, 10));
        Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(records));
        spout.nextTuple();
        Mockito.verify(collectorMock, Mockito.times(1)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.any(KafkaSpoutMessageId.class));
    }

    @Test
    public void testNextTupleEmitsFailedMessagesEvenWhenMaxUncommittedOffsetsIsExceeded() throws IOException {
        // The spout must reemit failed messages waiting for retry even if it is not allowed to poll for new messages due to maxUncommittedOffsets being exceeded
        // Emit maxUncommittedOffsets messages, and fail all of them. Then ensure that the spout will retry them when the retry backoff has passed
        try (SimulatedTime simulatedTime = new SimulatedTime()) {
            KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition);
            Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>();
            int numRecords = spoutConfig.getMaxUncommittedOffsets();
            // This is cheating a bit since maxPollRecords would normally spread this across multiple polls
            records.put(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, numRecords));
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(records));
            for (int i = 0; i < numRecords; i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collectorMock, Mockito.times(numRecords)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIds.capture());
            for (KafkaSpoutMessageId messageId : messageIds.getAllValues()) {
                spout.fail(messageId);
            }
            Mockito.reset(collectorMock);
            Time.advanceTime(50);
            // No backoff for test retry service, just check that messages will retry immediately
            for (int i = 0; i < numRecords; i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> retryMessageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collectorMock, Mockito.times(numRecords)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), retryMessageIds.capture());
            // Verify that the poll started at the earliest retriable tuple offset
            List<Long> failedOffsets = new ArrayList<>();
            for (KafkaSpoutMessageId msgId : messageIds.getAllValues()) {
                failedOffsets.add(msgId.offset());
            }
            InOrder inOrder = Mockito.inOrder(consumerMock);
            inOrder.verify(consumerMock).seek(partition, failedOffsets.get(0));
            inOrder.verify(consumerMock).poll(ArgumentMatchers.anyLong());
        }
    }

    @Test
    public void testSpoutWillSkipPartitionsAtTheMaxUncommittedOffsetsLimit() {
        // This verifies that partitions can't prevent each other from retrying tuples due to the maxUncommittedOffsets limit.
        try (SimulatedTime simulatedTime = new SimulatedTime()) {
            TopicPartition partitionTwo = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 2);
            KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition, partitionTwo);
            Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>();
            // This is cheating a bit since maxPollRecords would normally spread this across multiple polls
            records.put(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, spoutConfig.getMaxUncommittedOffsets()));
            records.put(partitionTwo, SpoutWithMockedConsumerSetupHelper.createRecords(partitionTwo, 0, ((spoutConfig.getMaxUncommittedOffsets()) + 1)));
            int numMessages = ((spoutConfig.getMaxUncommittedOffsets()) * 2) + 1;
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(records));
            for (int i = 0; i < numMessages; i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collectorMock, Mockito.times(numMessages)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIds.capture());
            // Now fail a tuple on partition one and verify that it is allowed to retry, because the failed tuple is below the maxUncommittedOffsets limit
            Optional<KafkaSpoutMessageId> failedMessageIdPartitionOne = messageIds.getAllValues().stream().filter(( messageId) -> (messageId.partition()) == (partition.partition())).findAny();
            spout.fail(failedMessageIdPartitionOne.get());
            // Also fail the last tuple from partition two. Since the failed tuple is beyond the maxUncommittedOffsets limit, it should not be retried until earlier messages are acked.
            Optional<KafkaSpoutMessageId> failedMessagePartitionTwo = messageIds.getAllValues().stream().filter(( messageId) -> (messageId.partition()) == (partitionTwo.partition())).max(( msgId, msgId2) -> ((int) ((msgId.offset()) - (msgId2.offset()))));
            spout.fail(failedMessagePartitionTwo.get());
            Mockito.reset(collectorMock);
            Time.advanceTime(50);
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(Collections.singletonMap(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, failedMessageIdPartitionOne.get().offset(), 1))));
            spout.nextTuple();
            Mockito.verify(collectorMock, Mockito.times(1)).emit(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
            InOrder inOrder = Mockito.inOrder(consumerMock);
            inOrder.verify(consumerMock).seek(partition, failedMessageIdPartitionOne.get().offset());
            // Should not seek on the paused partition
            inOrder.verify(consumerMock, Mockito.never()).seek(ArgumentMatchers.eq(partitionTwo), ArgumentMatchers.anyLong());
            inOrder.verify(consumerMock).pause(Collections.singleton(partitionTwo));
            inOrder.verify(consumerMock).poll(ArgumentMatchers.anyLong());
            inOrder.verify(consumerMock).resume(Collections.singleton(partitionTwo));
            Mockito.reset(collectorMock);
            // Now also check that no more tuples are polled for, since both partitions are at their limits
            spout.nextTuple();
            Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject(), ArgumentMatchers.anyObject());
        }
    }
}

