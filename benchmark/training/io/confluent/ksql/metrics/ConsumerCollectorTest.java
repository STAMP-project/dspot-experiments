/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.metrics;


import TopicSensors.Stat;
import com.google.common.collect.ImmutableMap;
import io.confluent.common.utils.SystemTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.metrics.Metrics;
import org.apache.kafka.common.record.TimestampType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ConsumerCollectorTest {
    private static final String TEST_TOPIC = "testtopic";

    @Test
    public void shouldDisplayRateThroughput() {
        final ConsumerCollector collector = new ConsumerCollector();// 

        collector.configure(new Metrics(), "group", new SystemTime());
        for (int i = 0; i < 100; i++) {
            final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = ImmutableMap.of(new TopicPartition(ConsumerCollectorTest.TEST_TOPIC, 1), Arrays.asList(new ConsumerRecord(ConsumerCollectorTest.TEST_TOPIC, 1, i, 1L, TimestampType.CREATE_TIME, 1L, 10, 10, "key", "1234567890")));
            final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
            collector.onConsume(consumerRecords);
        }
        final Collection<TopicSensors.Stat> stats = collector.stats(ConsumerCollectorTest.TEST_TOPIC, false);
        Assert.assertNotNull(stats);
        Assert.assertThat(stats.toString(), CoreMatchers.containsString("name=consumer-messages-per-sec,"));
        Assert.assertThat(stats.toString(), CoreMatchers.containsString("total-messages, value=100.0"));
    }
}

