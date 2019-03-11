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


import ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import KafkaSpoutRetryExponentialBackoff.TimeInterval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.storm.kafka.KafkaUnitExtension;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.utils.Time;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static KafkaSpout.TIMER_DELAY_MS;


public class MaxUncommittedOffsetTest {
    @RegisterExtension
    public KafkaUnitExtension kafkaUnitExtension = new KafkaUnitExtension();

    private final TopologyContext topologyContext = Mockito.mock(TopologyContext.class);

    private final Map<String, Object> conf = new HashMap<>();

    private final SpoutOutputCollector collector = Mockito.mock(SpoutOutputCollector.class);

    private final long commitOffsetPeriodMs = 2000;

    private final int numMessages = 100;

    private final int maxUncommittedOffsets = 10;

    private final int maxPollRecords = 5;

    private final int initialRetryDelaySecs = 60;

    private final KafkaSpoutConfig<String, String> spoutConfig = // Retry once after a minute
    SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(kafkaUnitExtension.getKafkaUnit().getKafkaPort()).setOffsetCommitPeriodMs(commitOffsetPeriodMs).setProp(MAX_POLL_RECORDS_CONFIG, maxPollRecords).setMaxUncommittedOffsets(maxUncommittedOffsets).setRetry(new KafkaSpoutRetryExponentialBackoff(TimeInterval.seconds(initialRetryDelaySecs), TimeInterval.seconds(0), 1, TimeInterval.seconds(initialRetryDelaySecs))).build();

    private KafkaSpout<String, String> spout;

    @Test
    public void testNextTupleCanEmitMoreMessagesWhenDroppingBelowMaxUncommittedOffsetsDueToCommit() throws Exception {
        // The spout must respect maxUncommittedOffsets after committing a set of records
        try (Time.SimulatedTime simulatedTime = new Time.SimulatedTime()) {
            // First check that maxUncommittedOffsets is respected when emitting from scratch
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = emitMaxUncommittedOffsetsMessagesAndCheckNoMoreAreEmitted(numMessages);
            Mockito.reset(collector);
            // Ack all emitted messages and commit them
            for (KafkaSpoutMessageId messageId : messageIds.getAllValues()) {
                spout.ack(messageId);
            }
            Time.advanceTime(((commitOffsetPeriodMs) + (TIMER_DELAY_MS)));
            spout.nextTuple();
            // Now check that the spout will emit another maxUncommittedOffsets messages
            for (int i = 0; i < (numMessages); i++) {
                spout.nextTuple();
            }
            Mockito.verify(collector, Mockito.times(maxUncommittedOffsets)).emit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Test
    public void testNextTupleWillRespectMaxUncommittedOffsetsWhenThereAreAckedUncommittedTuples() throws Exception {
        // The spout must respect maxUncommittedOffsets even if some tuples have been acked but not committed
        try (Time.SimulatedTime simulatedTime = new Time.SimulatedTime()) {
            // First check that maxUncommittedOffsets is respected when emitting from scratch
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = emitMaxUncommittedOffsetsMessagesAndCheckNoMoreAreEmitted(numMessages);
            Mockito.reset(collector);
            // Fail all emitted messages except the last one. Try to commit.
            List<KafkaSpoutMessageId> messageIdList = messageIds.getAllValues();
            for (int i = 0; i < ((messageIdList.size()) - 1); i++) {
                spout.fail(messageIdList.get(i));
            }
            spout.ack(messageIdList.get(((messageIdList.size()) - 1)));
            Time.advanceTime(((commitOffsetPeriodMs) + (TIMER_DELAY_MS)));
            spout.nextTuple();
            // Now check that the spout will not emit anything else since nothing has been committed
            for (int i = 0; i < (numMessages); i++) {
                spout.nextTuple();
            }
            Mockito.verify(collector, Mockito.times(0)).emit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Test
    public void testNextTupleWillNotEmitMoreThanMaxUncommittedOffsetsPlusMaxPollRecordsMessages() throws Exception {
        /* For each partition the spout is allowed to retry all tuples between the committed offset, and maxUncommittedOffsets ahead.
        It is not allowed to retry tuples past that limit.
        This makes the actual limit per partition maxUncommittedOffsets + maxPollRecords - 1,
        reached if the tuple at the maxUncommittedOffsets limit is the earliest retriable tuple,
        or if the spout is 1 tuple below the limit, and receives a full maxPollRecords tuples in the poll.
         */
        try (Time.SimulatedTime simulatedTime = new Time.SimulatedTime()) {
            // First check that maxUncommittedOffsets is respected when emitting from scratch
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = emitMaxUncommittedOffsetsMessagesAndCheckNoMoreAreEmitted(numMessages);
            Mockito.reset(collector);
            // Fail only the last tuple
            List<KafkaSpoutMessageId> messageIdList = messageIds.getAllValues();
            KafkaSpoutMessageId failedMessageId = messageIdList.get(((messageIdList.size()) - 1));
            spout.fail(failedMessageId);
            // Offset 0 to maxUncommittedOffsets - 2 are pending, maxUncommittedOffsets - 1 is failed but not retriable
            // The spout should not emit any more tuples.
            spout.nextTuple();
            Mockito.verify(collector, Mockito.never()).emit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
            // Allow the failed record to retry
            Time.advanceTimeSecs(initialRetryDelaySecs);
            for (int i = 0; i < (maxPollRecords); i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> secondRunMessageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collector, Mockito.times(maxPollRecords)).emit(ArgumentMatchers.any(), ArgumentMatchers.any(), secondRunMessageIds.capture());
            Mockito.reset(collector);
            MatcherAssert.assertThat(secondRunMessageIds.getAllValues().get(0), CoreMatchers.is(failedMessageId));
            // There should now be maxUncommittedOffsets + maxPollRecords emitted in all.
            // Fail the last emitted tuple and verify that the spout won't retry it because it's above the emit limit.
            spout.fail(secondRunMessageIds.getAllValues().get(((secondRunMessageIds.getAllValues().size()) - 1)));
            Time.advanceTimeSecs(initialRetryDelaySecs);
            spout.nextTuple();
            Mockito.verify(collector, Mockito.never()).emit(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        }
    }

    @Test
    public void testNextTupleWillAllowRetryForTuplesBelowEmitLimit() throws Exception {
        /* For each partition the spout is allowed to retry all tuples between the committed offset, and maxUncommittedOffsets ahead.
        It must retry tuples within that limit, even if more tuples were emitted.
         */
        try (Time.SimulatedTime simulatedTime = new Time.SimulatedTime()) {
            // First check that maxUncommittedOffsets is respected when emitting from scratch
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = emitMaxUncommittedOffsetsMessagesAndCheckNoMoreAreEmitted(numMessages);
            Mockito.reset(collector);
            failAllExceptTheFirstMessageThenCommit(messageIds);
            // Offset 0 is committed, 1 to maxUncommittedOffsets - 1 are failed but not retriable
            // The spout should now emit another maxPollRecords messages
            // This is allowed because the committed message brings the numUncommittedOffsets below the cap
            for (int i = 0; i < (maxUncommittedOffsets); i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> secondRunMessageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collector, Mockito.times(maxPollRecords)).emit(ArgumentMatchers.any(), ArgumentMatchers.any(), secondRunMessageIds.capture());
            Mockito.reset(collector);
            List<Long> firstRunOffsets = messageIds.getAllValues().stream().map(( messageId) -> messageId.offset()).collect(Collectors.toList());
            List<Long> secondRunOffsets = secondRunMessageIds.getAllValues().stream().map(( messageId) -> messageId.offset()).collect(Collectors.toList());
            MatcherAssert.assertThat("Expected the newly emitted messages to have no overlap with the first batch", secondRunOffsets.removeAll(firstRunOffsets), CoreMatchers.is(false));
            // Offset 0 is committed, 1 to maxUncommittedOffsets-1 are failed, maxUncommittedOffsets to maxUncommittedOffsets + maxPollRecords-1 are emitted
            // Fail the last tuples so only offset 0 is not failed.
            // Advance time so the failed tuples become ready for retry, and check that the spout will emit retriable tuples
            // for all the failed tuples that are within maxUncommittedOffsets tuples of the committed offset
            // This means 1 to maxUncommitteddOffsets, but not maxUncommittedOffsets+1...maxUncommittedOffsets+maxPollRecords-1
            for (KafkaSpoutMessageId msgId : secondRunMessageIds.getAllValues()) {
                spout.fail(msgId);
            }
            Time.advanceTimeSecs(initialRetryDelaySecs);
            for (int i = 0; i < (numMessages); i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> thirdRunMessageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collector, Mockito.times(maxUncommittedOffsets)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), thirdRunMessageIds.capture());
            Mockito.reset(collector);
            List<Long> thirdRunOffsets = thirdRunMessageIds.getAllValues().stream().map(( msgId) -> msgId.offset()).collect(Collectors.toList());
            MatcherAssert.assertThat("Expected the emitted messages to be retries of the failed tuples from the first batch, plus the first failed tuple from the second batch", thirdRunOffsets, CoreMatchers.everyItem(CoreMatchers.either(Matchers.isIn(firstRunOffsets)).or(CoreMatchers.is(secondRunMessageIds.getAllValues().get(0).offset()))));
        }
    }
}

