/**
 * Licensed to the Apache Software Foundation (ASF) under one
 *   or more contributor license agreements.  See the NOTICE file
 *   distributed with this work for additional information
 *   regarding copyright ownership.  The ASF licenses this file
 *   to you under the Apache License, Version 2.0 (the
 *   "License"); you may not use this file except in compliance
 *   with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.storm.kafka.spout;


import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static KafkaSpout.TIMER_DELAY_MS;
import static KafkaSpoutConfig.DEFAULT_PARTITION_REFRESH_PERIOD_MS;


public class KafkaSpoutSingleTopicTest extends KafkaSpoutAbstractTest {
    private final int maxPollRecords = 10;

    private final int maxRetries = 3;

    public KafkaSpoutSingleTopicTest() {
        super(2000);
    }

    @Test
    public void testSeekToCommittedOffsetIfConsumerPositionIsBehindWhenCommitting() throws Exception {
        final int messageCount = (maxPollRecords) * 2;
        prepareSpout(messageCount);
        // Emit all messages and fail the first one while acking the rest
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }
        ArgumentCaptor<KafkaSpoutMessageId> messageIdCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        Mockito.verify(collectorMock, Mockito.times(messageCount)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdCaptor.capture());
        List<KafkaSpoutMessageId> messageIds = messageIdCaptor.getAllValues();
        for (int i = 1; i < (messageIds.size()); i++) {
            spout.ack(messageIds.get(i));
        }
        KafkaSpoutMessageId failedTuple = messageIds.get(0);
        spout.fail(failedTuple);
        // Advance the time and replay the failed tuple.
        Mockito.reset(collectorMock);
        spout.nextTuple();
        ArgumentCaptor<KafkaSpoutMessageId> failedIdReplayCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), failedIdReplayCaptor.capture());
        MatcherAssert.assertThat("Expected replay of failed tuple", failedIdReplayCaptor.getValue(), CoreMatchers.is(failedTuple));
        /* Ack the tuple, and commit.
        Since the tuple is more than max poll records behind the most recent emitted tuple, the consumer won't catch up in this poll.
         */
        Mockito.clearInvocations(collectorMock);
        Time.advanceTime(((TIMER_DELAY_MS) + (commitOffsetPeriodMs)));
        spout.ack(failedIdReplayCaptor.getValue());
        spout.nextTuple();
        Mockito.verify(getKafkaConsumer()).commitSync(commitCapture.capture());
        Map<TopicPartition, OffsetAndMetadata> capturedCommit = commitCapture.getValue();
        TopicPartition expectedTp = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 0);
        MatcherAssert.assertThat("Should have committed to the right topic", capturedCommit, Matchers.hasKey(expectedTp));
        MatcherAssert.assertThat("Should have committed all the acked messages", capturedCommit.get(expectedTp).offset(), CoreMatchers.is(((long) (messageCount))));
        /* Verify that the following acked (now committed) tuples are not emitted again
        Since the consumer position was somewhere in the middle of the acked tuples when the commit happened,
        this verifies that the spout keeps the consumer position ahead of the committed offset when committing
         */
        // Just do a few polls to check that nothing more is emitted
        for (int i = 0; i < 3; i++) {
            spout.nextTuple();
        }
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.anyObject());
    }

    @Test
    public void testClearingWaitingToEmitIfConsumerPositionIsNotBehindWhenCommitting() throws Exception {
        final int messageCountExcludingLast = maxPollRecords;
        int messagesInKafka = messageCountExcludingLast + 1;
        prepareSpout(messagesInKafka);
        // Emit all messages and fail the first one while acking the rest
        for (int i = 0; i < messageCountExcludingLast; i++) {
            spout.nextTuple();
        }
        ArgumentCaptor<KafkaSpoutMessageId> messageIdCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        Mockito.verify(collectorMock, Mockito.times(messageCountExcludingLast)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdCaptor.capture());
        List<KafkaSpoutMessageId> messageIds = messageIdCaptor.getAllValues();
        for (int i = 1; i < (messageIds.size()); i++) {
            spout.ack(messageIds.get(i));
        }
        KafkaSpoutMessageId failedTuple = messageIds.get(0);
        spout.fail(failedTuple);
        // Advance the time and replay the failed tuple.
        // Since the last tuple on the partition is more than maxPollRecords ahead of the failed tuple, it shouldn't be emitted here
        Mockito.reset(collectorMock);
        spout.nextTuple();
        ArgumentCaptor<KafkaSpoutMessageId> failedIdReplayCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), failedIdReplayCaptor.capture());
        MatcherAssert.assertThat("Expected replay of failed tuple", failedIdReplayCaptor.getValue(), CoreMatchers.is(failedTuple));
        /* Ack the tuple, and commit.

        The waiting to emit list should now be cleared, and the next emitted tuple should be the last tuple on the partition,
        which hasn't been emitted yet
         */
        Mockito.reset(collectorMock);
        Time.advanceTime(((TIMER_DELAY_MS) + (commitOffsetPeriodMs)));
        spout.ack(failedIdReplayCaptor.getValue());
        spout.nextTuple();
        Mockito.verify(getKafkaConsumer()).commitSync(commitCapture.capture());
        Map<TopicPartition, OffsetAndMetadata> capturedCommit = commitCapture.getValue();
        TopicPartition expectedTp = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 0);
        MatcherAssert.assertThat("Should have committed to the right topic", capturedCommit, Matchers.hasKey(expectedTp));
        MatcherAssert.assertThat("Should have committed all the acked messages", capturedCommit.get(expectedTp).offset(), CoreMatchers.is(((long) (messageCountExcludingLast))));
        ArgumentCaptor<KafkaSpoutMessageId> lastOffsetMessageCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), lastOffsetMessageCaptor.capture());
        MatcherAssert.assertThat("Expected emit of the final tuple in the partition", lastOffsetMessageCaptor.getValue().offset(), CoreMatchers.is((messagesInKafka - 1L)));
        Mockito.reset(collectorMock);
        // Nothing else should be emitted, all tuples are acked except for the final tuple, which is pending.
        for (int i = 0; i < 3; i++) {
            spout.nextTuple();
        }
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.anyObject());
    }

    @Test
    public void testShouldContinueWithSlowDoubleAcks() throws Exception {
        final int messageCount = 20;
        prepareSpout(messageCount);
        // play 1st tuple
        ArgumentCaptor<Object> messageIdToDoubleAck = ArgumentCaptor.forClass(Object.class);
        spout.nextTuple();
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdToDoubleAck.capture());
        spout.ack(messageIdToDoubleAck.getValue());
        // Emit some more messages
        for (int i = 0; i < (messageCount / 2); i++) {
            spout.nextTuple();
        }
        spout.ack(messageIdToDoubleAck.getValue());
        // Emit any remaining messages
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }
        // Verify that all messages are emitted, ack all the messages
        ArgumentCaptor<Object> messageIds = ArgumentCaptor.forClass(Object.class);
        Mockito.verify(collectorMock, Mockito.times(messageCount)).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.anyList(), messageIds.capture());
        for (Object id : messageIds.getAllValues()) {
            spout.ack(id);
        }
        Time.advanceTime(((commitOffsetPeriodMs) + (TIMER_DELAY_MS)));
        // Commit offsets
        spout.nextTuple();
        verifyAllMessagesCommitted(messageCount);
    }

    @Test
    public void testShouldEmitAllMessages() throws Exception {
        final int messageCount = 10;
        prepareSpout(messageCount);
        // Emit all messages and check that they are emitted. Ack the messages too
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
            ArgumentCaptor<Object> messageId = ArgumentCaptor.forClass(Object.class);
            Mockito.verify(collectorMock).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.eq(new Values(SingleTopicKafkaSpoutConfiguration.TOPIC, Integer.toString(i), Integer.toString(i))), messageId.capture());
            spout.ack(messageId.getValue());
            Mockito.reset(collectorMock);
        }
        Time.advanceTime(((commitOffsetPeriodMs) + (TIMER_DELAY_MS)));
        // Commit offsets
        spout.nextTuple();
        verifyAllMessagesCommitted(messageCount);
    }

    @Test
    public void testShouldReplayInOrderFailedMessages() throws Exception {
        final int messageCount = 10;
        prepareSpout(messageCount);
        // play and ack 1 tuple
        ArgumentCaptor<Object> messageIdAcked = ArgumentCaptor.forClass(Object.class);
        spout.nextTuple();
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdAcked.capture());
        spout.ack(messageIdAcked.getValue());
        Mockito.reset(collectorMock);
        // play and fail 1 tuple
        ArgumentCaptor<Object> messageIdFailed = ArgumentCaptor.forClass(Object.class);
        spout.nextTuple();
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdFailed.capture());
        spout.fail(messageIdFailed.getValue());
        Mockito.reset(collectorMock);
        // Emit all remaining messages. Failed tuples retry immediately with current configuration, so no need to wait.
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }
        ArgumentCaptor<Object> remainingMessageIds = ArgumentCaptor.forClass(Object.class);
        // All messages except the first acked message should have been emitted
        Mockito.verify(collectorMock, Mockito.times((messageCount - 1))).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.anyList(), remainingMessageIds.capture());
        for (Object id : remainingMessageIds.getAllValues()) {
            spout.ack(id);
        }
        Time.advanceTime(((commitOffsetPeriodMs) + (TIMER_DELAY_MS)));
        // Commit offsets
        spout.nextTuple();
        verifyAllMessagesCommitted(messageCount);
    }

    @Test
    public void testShouldReplayFirstTupleFailedOutOfOrder() throws Exception {
        final int messageCount = 10;
        prepareSpout(messageCount);
        // play 1st tuple
        ArgumentCaptor<Object> messageIdToFail = ArgumentCaptor.forClass(Object.class);
        spout.nextTuple();
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdToFail.capture());
        Mockito.reset(collectorMock);
        // play 2nd tuple
        ArgumentCaptor<Object> messageIdToAck = ArgumentCaptor.forClass(Object.class);
        spout.nextTuple();
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIdToAck.capture());
        Mockito.reset(collectorMock);
        // ack 2nd tuple
        spout.ack(messageIdToAck.getValue());
        // fail 1st tuple
        spout.fail(messageIdToFail.getValue());
        // Emit all remaining messages. Failed tuples retry immediately with current configuration, so no need to wait.
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }
        ArgumentCaptor<Object> remainingIds = ArgumentCaptor.forClass(Object.class);
        // All messages except the first acked message should have been emitted
        Mockito.verify(collectorMock, Mockito.times((messageCount - 1))).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.anyList(), remainingIds.capture());
        for (Object id : remainingIds.getAllValues()) {
            spout.ack(id);
        }
        Time.advanceTime(((commitOffsetPeriodMs) + (TIMER_DELAY_MS)));
        // Commit offsets
        spout.nextTuple();
        verifyAllMessagesCommitted(messageCount);
    }

    @Test
    public void testShouldReplayAllFailedTuplesWhenFailedOutOfOrder() throws Exception {
        // The spout must reemit retriable tuples, even if they fail out of order.
        // The spout should be able to skip tuples it has already emitted when retrying messages, even if those tuples are also retries.
        final int messageCount = 10;
        prepareSpout(messageCount);
        // play all tuples
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }
        ArgumentCaptor<KafkaSpoutMessageId> messageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        Mockito.verify(collectorMock, Mockito.times(messageCount)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIds.capture());
        Mockito.reset(collectorMock);
        // Fail tuple 5 and 3, call nextTuple, then fail tuple 2
        List<KafkaSpoutMessageId> capturedMessageIds = messageIds.getAllValues();
        spout.fail(capturedMessageIds.get(5));
        spout.fail(capturedMessageIds.get(3));
        spout.nextTuple();
        spout.fail(capturedMessageIds.get(2));
        // Check that the spout will reemit all 3 failed tuples and no other tuples
        ArgumentCaptor<KafkaSpoutMessageId> reemittedMessageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
        for (int i = 0; i < messageCount; i++) {
            spout.nextTuple();
        }
        Mockito.verify(collectorMock, Mockito.times(3)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), reemittedMessageIds.capture());
        Set<KafkaSpoutMessageId> expectedReemitIds = new HashSet<>();
        expectedReemitIds.add(capturedMessageIds.get(5));
        expectedReemitIds.add(capturedMessageIds.get(3));
        expectedReemitIds.add(capturedMessageIds.get(2));
        MatcherAssert.assertThat("Expected reemits to be the 3 failed tuples", new HashSet(reemittedMessageIds.getAllValues()), CoreMatchers.is(expectedReemitIds));
    }

    @Test
    public void testShouldDropMessagesAfterMaxRetriesAreReached() throws Exception {
        // Check that if one message fails repeatedly, the retry cap limits how many times the message can be reemitted
        final int messageCount = 1;
        prepareSpout(messageCount);
        // Emit and fail the same tuple until we've reached retry limit
        for (int i = 0; i <= (maxRetries); i++) {
            ArgumentCaptor<KafkaSpoutMessageId> messageIdFailed = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            spout.nextTuple();
            Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyListOf(Object.class), messageIdFailed.capture());
            KafkaSpoutMessageId msgId = messageIdFailed.getValue();
            spout.fail(msgId);
            MatcherAssert.assertThat("Expected message id number of failures to match the number of times the message has failed", msgId.numFails(), CoreMatchers.is((i + 1)));
            Mockito.reset(collectorMock);
        }
        // Verify that the tuple is not emitted again
        spout.nextTuple();
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyListOf(Object.class), ArgumentMatchers.anyObject());
    }

    @Test
    public void testSpoutMustRefreshPartitionsEvenIfNotPolling() throws Exception {
        SingleTopicKafkaUnitSetupHelper.initializeSpout(spout, conf, topologyContext, collectorMock);
        // Nothing is assigned yet, should emit nothing
        spout.nextTuple();
        Mockito.verify(collectorMock, Mockito.never()).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.any(KafkaSpoutMessageId.class));
        SingleTopicKafkaUnitSetupHelper.populateTopicData(kafkaUnitExtension.getKafkaUnit(), SingleTopicKafkaSpoutConfiguration.TOPIC, 1);
        Time.advanceTime(((DEFAULT_PARTITION_REFRESH_PERIOD_MS) + (TIMER_DELAY_MS)));
        // The new partition should be discovered and the message should be emitted
        spout.nextTuple();
        Mockito.verify(collectorMock).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), ArgumentMatchers.any(KafkaSpoutMessageId.class));
    }

    @Test
    public void testOffsetMetrics() throws Exception {
        final int messageCount = 10;
        prepareSpout(messageCount);
        Map<String, Long> offsetMetric = ((Map<String, Long>) (spout.getKafkaOffsetMetric().getValueAndReset()));
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalEarliestTimeOffset")).longValue(), 0);
        // the offset of the last available message + 1.
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalLatestTimeOffset")).longValue(), 10);
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalRecordsInPartitions")).longValue(), 10);
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalLatestEmittedOffset")).longValue(), 0);
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalLatestCompletedOffset")).longValue(), 0);
        // totalSpoutLag = totalLatestTimeOffset-totalLatestCompletedOffset
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalSpoutLag")).longValue(), 10);
        // Emit all messages and check that they are emitted. Ack the messages too
        for (int i = 0; i < messageCount; i++) {
            nextTuple_verifyEmitted_ack_resetCollector(i);
        }
        commitAndVerifyAllMessagesCommitted(messageCount);
        offsetMetric = ((Map<String, Long>) (spout.getKafkaOffsetMetric().getValueAndReset()));
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalEarliestTimeOffset")).longValue(), 0);
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalLatestTimeOffset")).longValue(), 10);
        // latest offset
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalLatestEmittedOffset")).longValue(), 9);
        // offset where processing will resume upon spout restart
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalLatestCompletedOffset")).longValue(), 10);
        Assertions.assertEquals(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalSpoutLag")).longValue(), 0);
    }
}

