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
package org.apache.storm.kafka.spout.trident;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.spout.subscription.ManualPartitioner;
import org.apache.storm.kafka.spout.subscription.TopicFilter;
import org.apache.storm.kafka.spout.trident.config.builder.SingleTopicKafkaTridentSpoutConfiguration;
import org.apache.storm.utils.Time;
import org.apache.storm.utils.Time.SimulatedTime;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static KafkaTridentSpoutCoordinator.TIMER_DELAY_MS;


public class KafkaTridentSpoutOpaqueCoordinatorTest {
    private final TopicPartitionSerializer tpSerializer = new TopicPartitionSerializer();

    @Test
    public void testCanGetPartitions() {
        KafkaConsumer<String, String> mockConsumer = Mockito.mock(KafkaConsumer.class);
        TopicPartition expectedPartition = new TopicPartition("test", 0);
        TopicFilter mockFilter = Mockito.mock(TopicFilter.class);
        Mockito.when(mockFilter.getAllSubscribedPartitions(ArgumentMatchers.any())).thenReturn(Collections.singleton(expectedPartition));
        KafkaTridentSpoutConfig<String, String> spoutConfig = SingleTopicKafkaTridentSpoutConfiguration.createKafkaSpoutConfigBuilder(mockFilter, Mockito.mock(ManualPartitioner.class), (-1)).build();
        KafkaTridentSpoutCoordinator<String, String> coordinator = new KafkaTridentSpoutCoordinator(spoutConfig, ( ignored) -> mockConsumer);
        List<Map<String, Object>> partitionsForBatch = coordinator.getPartitionsForBatch();
        List<TopicPartition> tps = deserializePartitions(partitionsForBatch);
        Mockito.verify(mockFilter).getAllSubscribedPartitions(mockConsumer);
        Assert.assertThat(tps, Matchers.contains(expectedPartition));
    }

    @Test
    public void testCanUpdatePartitions() {
        try (SimulatedTime time = new SimulatedTime()) {
            KafkaConsumer<String, String> mockConsumer = Mockito.mock(KafkaConsumer.class);
            TopicPartition expectedPartition = new TopicPartition("test", 0);
            TopicPartition addedLaterPartition = new TopicPartition("test-2", 0);
            HashSet<TopicPartition> allPartitions = new HashSet<>();
            allPartitions.add(expectedPartition);
            allPartitions.add(addedLaterPartition);
            TopicFilter mockFilter = Mockito.mock(TopicFilter.class);
            Mockito.when(mockFilter.getAllSubscribedPartitions(ArgumentMatchers.any())).thenReturn(Collections.singleton(expectedPartition)).thenReturn(allPartitions);
            KafkaTridentSpoutConfig<String, String> spoutConfig = SingleTopicKafkaTridentSpoutConfiguration.createKafkaSpoutConfigBuilder(mockFilter, Mockito.mock(ManualPartitioner.class), (-1)).build();
            KafkaTridentSpoutCoordinator<String, String> coordinator = new KafkaTridentSpoutCoordinator(spoutConfig, ( ignored) -> mockConsumer);
            List<Map<String, Object>> partitionsForBatch = coordinator.getPartitionsForBatch();
            List<TopicPartition> firstBatchTps = deserializePartitions(partitionsForBatch);
            Mockito.verify(mockFilter).getAllSubscribedPartitions(mockConsumer);
            Assert.assertThat(firstBatchTps, Matchers.contains(expectedPartition));
            Time.advanceTime(((TIMER_DELAY_MS) + (spoutConfig.getPartitionRefreshPeriodMs())));
            List<Map<String, Object>> partitionsForSecondBatch = coordinator.getPartitionsForBatch();
            List<TopicPartition> secondBatchTps = deserializePartitions(partitionsForSecondBatch);
            Mockito.verify(mockFilter, Mockito.times(2)).getAllSubscribedPartitions(mockConsumer);
            Assert.assertThat(new HashSet(secondBatchTps), Matchers.is(allPartitions));
        }
    }
}

