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


import KafkaSpoutConfig.ProcessingGuarantee;
import KafkaSpoutConfig.ProcessingGuarantee.AT_MOST_ONCE;
import KafkaSpoutConfig.ProcessingGuarantee.NO_GUARANTEE;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.kafka.spout.internal.CommitMetadataManager;
import org.apache.storm.kafka.spout.subscription.ManualPartitioner;
import org.apache.storm.kafka.spout.subscription.TopicFilter;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Time.SimulatedTime;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static KafkaSpout.TIMER_DELAY_MS;


@RunWith(MockitoJUnitRunner.class)
public class KafkaSpoutMessagingGuaranteeTest {
    @Captor
    private ArgumentCaptor<Map<TopicPartition, OffsetAndMetadata>> commitCapture;

    private final TopologyContext contextMock = Mockito.mock(TopologyContext.class);

    private final SpoutOutputCollector collectorMock = Mockito.mock(SpoutOutputCollector.class);

    private final Map<String, Object> conf = new HashMap<>();

    private final TopicPartition partition = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 1);

    private KafkaConsumer<String, String> consumerMock;

    @Test
    public void testAtMostOnceModeCommitsBeforeEmit() throws Exception {
        // At-most-once mode must commit tuples before they are emitted to the topology to ensure that a spout crash won't cause replays.
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(AT_MOST_ONCE).build();
        KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition);
        Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(Collections.singletonMap(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, 1))));
        spout.nextTuple();
        Mockito.when(consumerMock.position(partition)).thenReturn(1L);
        // The spout should have emitted the tuple, and must have committed it before emit
        InOrder inOrder = Mockito.inOrder(consumerMock, collectorMock);
        inOrder.verify(consumerMock).poll(ArgumentMatchers.anyLong());
        inOrder.verify(consumerMock).commitSync(commitCapture.capture());
        inOrder.verify(collectorMock).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.anyList());
        CommitMetadataManager metadataManager = new CommitMetadataManager(contextMock, ProcessingGuarantee.AT_MOST_ONCE);
        Map<TopicPartition, OffsetAndMetadata> committedOffsets = commitCapture.getValue();
        Assert.assertThat(committedOffsets.get(partition).offset(), CoreMatchers.is(0L));
        Assert.assertThat(committedOffsets.get(partition).metadata(), CoreMatchers.is(metadataManager.getCommitMetadata()));
    }

    @Test
    public void testAtMostOnceModeDisregardsMaxUncommittedOffsets() throws Exception {
        // The maxUncommittedOffsets limit should not be enforced, since it is only meaningful in at-least-once mode
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(AT_MOST_ONCE).build();
        doTestModeDisregardsMaxUncommittedOffsets(spoutConfig);
    }

    @Test
    public void testNoGuaranteeModeDisregardsMaxUncommittedOffsets() throws Exception {
        // The maxUncommittedOffsets limit should not be enforced, since it is only meaningful in at-least-once mode
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(NO_GUARANTEE).build();
        doTestModeDisregardsMaxUncommittedOffsets(spoutConfig);
    }

    @Test
    public void testAtMostOnceModeCannotReplayTuples() throws Exception {
        // When tuple tracking is enabled, the spout must not replay tuples in at-most-once mode
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(AT_MOST_ONCE).setTupleTrackingEnforced(true).build();
        doTestModeCannotReplayTuples(spoutConfig);
    }

    @Test
    public void testNoGuaranteeModeCannotReplayTuples() throws Exception {
        // When tuple tracking is enabled, the spout must not replay tuples in no guarantee mode
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(NO_GUARANTEE).setTupleTrackingEnforced(true).build();
        doTestModeCannotReplayTuples(spoutConfig);
    }

    @Test
    public void testAtMostOnceModeDoesNotCommitAckedTuples() throws Exception {
        // When tuple tracking is enabled, the spout must not commit acked tuples in at-most-once mode because they were committed before being emitted
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(AT_MOST_ONCE).setTupleTrackingEnforced(true).build();
        try (SimulatedTime time = new SimulatedTime()) {
            KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition);
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(Collections.singletonMap(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, 1))));
            spout.nextTuple();
            Mockito.clearInvocations(consumerMock);
            ArgumentCaptor<KafkaSpoutMessageId> msgIdCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collectorMock).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.anyList(), msgIdCaptor.capture());
            Assert.assertThat("Should have captured a message id", msgIdCaptor.getValue(), CoreMatchers.not(CoreMatchers.nullValue()));
            spout.ack(msgIdCaptor.getValue());
            Time.advanceTime(((TIMER_DELAY_MS) + (spoutConfig.getOffsetsCommitPeriodMs())));
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(Collections.emptyMap()));
            spout.nextTuple();
            Mockito.verify(consumerMock, Mockito.never()).commitSync(ArgumentMatchers.argThat((Map<TopicPartition, OffsetAndMetadata> arg) -> {
                return !(arg.containsKey(partition));
            }));
        }
    }

    @Test
    public void testNoGuaranteeModeCommitsPolledTuples() throws Exception {
        // When using the no guarantee mode, the spout must commit tuples periodically, regardless of whether they've been acked
        KafkaSpoutConfig<String, String> spoutConfig = SingleTopicKafkaSpoutConfiguration.createKafkaSpoutConfigBuilder(Mockito.mock(TopicFilter.class), Mockito.mock(ManualPartitioner.class), (-1)).setProcessingGuarantee(NO_GUARANTEE).setTupleTrackingEnforced(true).build();
        try (SimulatedTime time = new SimulatedTime()) {
            KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition);
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new org.apache.kafka.clients.consumer.ConsumerRecords(Collections.singletonMap(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, 1))));
            spout.nextTuple();
            Mockito.when(consumerMock.position(partition)).thenReturn(1L);
            ArgumentCaptor<KafkaSpoutMessageId> msgIdCaptor = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collectorMock).emit(ArgumentMatchers.eq(SingleTopicKafkaSpoutConfiguration.STREAM), ArgumentMatchers.anyList(), msgIdCaptor.capture());
            Assert.assertThat("Should have captured a message id", msgIdCaptor.getValue(), CoreMatchers.not(CoreMatchers.nullValue()));
            Time.advanceTime(((TIMER_DELAY_MS) + (spoutConfig.getOffsetsCommitPeriodMs())));
            spout.nextTuple();
            Mockito.verify(consumerMock).commitAsync(commitCapture.capture(), ArgumentMatchers.isNull());
            CommitMetadataManager metadataManager = new CommitMetadataManager(contextMock, ProcessingGuarantee.NO_GUARANTEE);
            Map<TopicPartition, OffsetAndMetadata> committedOffsets = commitCapture.getValue();
            Assert.assertThat(committedOffsets.get(partition).offset(), CoreMatchers.is(1L));
            Assert.assertThat(committedOffsets.get(partition).metadata(), CoreMatchers.is(metadataManager.getCommitMetadata()));
        }
    }

    @Test
    public void testAtMostOnceModeCanFilterNullTuples() {
        doFilterNullTupleTest(AT_MOST_ONCE);
    }

    @Test
    public void testNoGuaranteeModeCanFilterNullTuples() {
        doFilterNullTupleTest(NO_GUARANTEE);
    }
}

