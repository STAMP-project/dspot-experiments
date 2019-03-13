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


import KafkaSpoutRetryExponentialBackoff.TimeInterval;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Time.SimulatedTime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static KafkaSpout.TIMER_DELAY_MS;


public class KafkaSpoutRetryLimitTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    private final long offsetCommitPeriodMs = 2000;

    private final TopologyContext contextMock = Mockito.mock(TopologyContext.class);

    private final SpoutOutputCollector collectorMock = Mockito.mock(SpoutOutputCollector.class);

    private final Map<String, Object> conf = new HashMap<>();

    private final TopicPartition partition = new TopicPartition(SingleTopicKafkaSpoutConfiguration.TOPIC, 1);

    private KafkaConsumer<String, String> consumerMock;

    private KafkaSpoutConfig<String, String> spoutConfig;

    public static final KafkaSpoutRetryService ZERO_RETRIES_RETRY_SERVICE = new KafkaSpoutRetryExponentialBackoff(TimeInterval.seconds(0), TimeInterval.milliSeconds(0), 0, TimeInterval.milliSeconds(0));

    @Captor
    private ArgumentCaptor<Map<TopicPartition, OffsetAndMetadata>> commitCapture;

    @Test
    public void testFailingTupleCompletesAckAfterRetryLimitIsMet() {
        // Spout should ack failed messages after they hit the retry limit
        try (SimulatedTime simulatedTime = new SimulatedTime()) {
            KafkaSpout<String, String> spout = SpoutWithMockedConsumerSetupHelper.setupSpout(spoutConfig, conf, contextMock, collectorMock, consumerMock, partition);
            Map<TopicPartition, List<ConsumerRecord<String, String>>> records = new HashMap<>();
            int lastOffset = 3;
            int numRecords = lastOffset + 1;
            records.put(partition, SpoutWithMockedConsumerSetupHelper.createRecords(partition, 0, numRecords));
            Mockito.when(consumerMock.poll(ArgumentMatchers.anyLong())).thenReturn(new ConsumerRecords(records));
            for (int i = 0; i < numRecords; i++) {
                spout.nextTuple();
            }
            ArgumentCaptor<KafkaSpoutMessageId> messageIds = ArgumentCaptor.forClass(KafkaSpoutMessageId.class);
            Mockito.verify(collectorMock, Mockito.times(numRecords)).emit(ArgumentMatchers.anyString(), ArgumentMatchers.anyList(), messageIds.capture());
            for (KafkaSpoutMessageId messageId : messageIds.getAllValues()) {
                spout.fail(messageId);
            }
            // Advance time and then trigger call to kafka consumer commit
            Time.advanceTime(((TIMER_DELAY_MS) + (offsetCommitPeriodMs)));
            spout.nextTuple();
            InOrder inOrder = Mockito.inOrder(consumerMock);
            inOrder.verify(consumerMock).commitSync(commitCapture.capture());
            inOrder.verify(consumerMock).poll(ArgumentMatchers.anyLong());
            // verify that offset 4 was committed for the given TopicPartition, since processing should resume at 4.
            Assert.assertTrue(commitCapture.getValue().containsKey(partition));
            Assert.assertEquals((lastOffset + 1), offset());
        }
    }
}

