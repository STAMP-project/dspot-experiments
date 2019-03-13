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


import FirstPollOffsetStrategy.EARLIEST;
import FirstPollOffsetStrategy.UNCOMMITTED_EARLIEST;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.storm.kafka.KafkaUnitExtension;
import org.apache.storm.kafka.spout.config.builder.SingleTopicKafkaSpoutConfiguration;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class KafkaSpoutReactivationTest {
    @RegisterExtension
    public KafkaUnitExtension kafkaUnitExtension = new KafkaUnitExtension();

    @Captor
    private ArgumentCaptor<Map<TopicPartition, OffsetAndMetadata>> commitCapture;

    private final TopologyContext topologyContext = Mockito.mock(TopologyContext.class);

    private final Map<String, Object> conf = new HashMap<>();

    private final SpoutOutputCollector collector = Mockito.mock(SpoutOutputCollector.class);

    private final long commitOffsetPeriodMs = 2000;

    private Consumer<String, String> consumerSpy;

    private KafkaSpout<String, String> spout;

    private final int maxPollRecords = 10;

    @Test
    public void testSpoutShouldResumeWhereItLeftOffWithUncommittedEarliestStrategy() throws Exception {
        // With uncommitted earliest the spout should pick up where it left off when reactivating.
        doReactivationTest(UNCOMMITTED_EARLIEST);
    }

    @Test
    public void testSpoutShouldResumeWhereItLeftOffWithEarliestStrategy() throws Exception {
        // With earliest, the spout should also resume where it left off, rather than restart at the earliest offset.
        doReactivationTest(EARLIEST);
    }

    @Test
    public void testSpoutMustHandleGettingMetricsWhileDeactivated() throws Exception {
        // Storm will try to get metrics from the spout even while deactivated, the spout must be able to handle this
        prepareSpout(10, UNCOMMITTED_EARLIEST);
        for (int i = 0; i < 5; i++) {
            KafkaSpoutMessageId msgId = emitOne();
            spout.ack(msgId);
        }
        spout.deactivate();
        Map<String, Long> offsetMetric = ((Map<String, Long>) (spout.getKafkaOffsetMetric().getValueAndReset()));
        Assert.assertThat(offsetMetric.get(((SingleTopicKafkaSpoutConfiguration.TOPIC) + "/totalSpoutLag")), CoreMatchers.is(5L));
    }
}

