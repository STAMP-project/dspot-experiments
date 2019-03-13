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


import ConsumerConfig.GROUP_ID_CONFIG;
import ProducerConfig.CLIENT_ID_CONFIG;
import TopicSensors.Stat;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.record.TimestampType;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class MetricCollectorsTest {
    private static final String TEST_TOPIC = "shared-topic";

    @Test
    public void shouldAggregateStats() {
        final List<TopicSensors.Stat> stats = Arrays.asList(new TopicSensors.Stat("metric", 1, 1L), new TopicSensors.Stat("metric", 1, 1L), new TopicSensors.Stat("metric", 1, 1L));
        final Map<String, TopicSensors.Stat> aggregateMetrics = MetricCollectors.getAggregateMetrics(stats);
        Assert.assertThat(aggregateMetrics.size(), CoreMatchers.equalTo(1));
        Assert.assertThat(aggregateMetrics.values().iterator().next().getValue(), CoreMatchers.equalTo(3.0));
    }

    @Test
    public void shouldKeepWorkingWhenDuplicateTopicConsumerIsRemoved() {
        final ConsumerCollector collector1 = new ConsumerCollector();
        collector1.configure(ImmutableMap.of(GROUP_ID_CONFIG, "stream-thread-1"));
        final ConsumerCollector collector2 = new ConsumerCollector();
        collector2.configure(ImmutableMap.of(GROUP_ID_CONFIG, "stream-thread-2"));
        final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = ImmutableMap.of(new TopicPartition(MetricCollectorsTest.TEST_TOPIC, 1), Arrays.asList(new ConsumerRecord(MetricCollectorsTest.TEST_TOPIC, 1, 1, 1L, TimestampType.CREATE_TIME, 1L, 10, 10, "key", "1234567890")));
        final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
        collector1.onConsume(consumerRecords);
        collector2.onConsume(consumerRecords);
        final String firstPassStats = MetricCollectors.getAndFormatStatsFor(MetricCollectorsTest.TEST_TOPIC, false);
        Assert.assertTrue(("Missed stats, got:" + firstPassStats), firstPassStats.contains("total-messages:         2"));
        collector2.close();
        collector1.onConsume(consumerRecords);
        final String statsForTopic2 = MetricCollectors.getAndFormatStatsFor(MetricCollectorsTest.TEST_TOPIC, false);
        Assert.assertTrue(("Missed stats, got:" + statsForTopic2), statsForTopic2.contains("total-messages:         2"));
    }

    @Test
    public void shouldAggregateStatsAcrossAllProducers() {
        final ProducerCollector collector1 = new ProducerCollector();
        collector1.configure(ImmutableMap.of(CLIENT_ID_CONFIG, "client1"));
        final ProducerCollector collector2 = new ProducerCollector();
        collector2.configure(ImmutableMap.of(CLIENT_ID_CONFIG, "client2"));
        for (int i = 0; i < 500; i++) {
            collector1.onSend(new org.apache.kafka.clients.producer.ProducerRecord(MetricCollectorsTest.TEST_TOPIC, "key", Integer.toString(i)));
            collector2.onSend(new org.apache.kafka.clients.producer.ProducerRecord((((MetricCollectorsTest.TEST_TOPIC) + "_") + i), "key", Integer.toString((i * 100))));
        }
        // The Kafka metrics in MetricCollectors is configured so that sampled stats (like the Rate
        // measurable stat) have a 100 samples, each with a duration of 1 second. In this test we
        // record a 1000 events, but only in a single sample since they all belong to the same second.
        // So 99 samples are empty. Hence the rate is computed as a tenth of what it should be. This
        // won't be a problem for a longer running program.
        Assert.assertEquals(10, Math.floor(MetricCollectors.currentProductionRate()), 0);
    }

    @Test
    public void shouldAggregateStatsAcrossAllConsumers() {
        final ConsumerCollector collector1 = new ConsumerCollector();
        collector1.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "client1"));
        final ConsumerCollector collector2 = new ConsumerCollector();
        collector2.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "client2"));
        final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = new HashMap<>();
        final List<ConsumerRecord<Object, Object>> recordList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            recordList.add(new ConsumerRecord(MetricCollectorsTest.TEST_TOPIC, 1, 1, 1L, TimestampType.CREATE_TIME, 1L, 10, 10, "key", "1234567890"));
        }
        records.put(new TopicPartition(MetricCollectorsTest.TEST_TOPIC, 1), recordList);
        final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
        collector1.onConsume(consumerRecords);
        collector2.onConsume(consumerRecords);
        // Same as the above test, the kafka `Rate` measurable stat reports the rate as a tenth
        // of what it should be because all the samples haven't been filled out yet.
        Assert.assertEquals(10, Math.floor(MetricCollectors.currentConsumptionRate()), 0);
    }

    @Test
    public void shouldAggregateTotalMessageConsumptionAcrossAllConsumers() {
        final ConsumerCollector collector1 = new ConsumerCollector();
        collector1.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "client1"));
        final ConsumerCollector collector2 = new ConsumerCollector();
        collector2.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "client2"));
        final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = new HashMap<>();
        final List<ConsumerRecord<Object, Object>> recordList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            recordList.add(new ConsumerRecord(MetricCollectorsTest.TEST_TOPIC, 1, 1, 1L, TimestampType.CREATE_TIME, 1L, 10, 10, "key", "1234567890"));
        }
        records.put(new TopicPartition(MetricCollectorsTest.TEST_TOPIC, 1), recordList);
        final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
        collector1.onConsume(consumerRecords);
        collector2.onConsume(consumerRecords);
        Assert.assertEquals(20, MetricCollectors.totalMessageConsumption(), 0);
    }

    @Test
    public void shouldAggregateTotalBytesConsumptionAcrossAllConsumers() {
        final ConsumerCollector collector1 = new ConsumerCollector();
        collector1.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "client1"));
        final ConsumerCollector collector2 = new ConsumerCollector();
        collector2.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "client2"));
        final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = new HashMap<>();
        final List<ConsumerRecord<Object, Object>> recordList = new ArrayList<>();
        int totalSz = 0;
        for (int i = 0; i < 10; i++) {
            recordList.add(new ConsumerRecord(MetricCollectorsTest.TEST_TOPIC, 1, 1, 1L, TimestampType.CREATE_TIME, 1L, (5 + i), (10 + i), "key", "1234567890"));
            totalSz += 15 + (2 * i);
        }
        records.put(new TopicPartition(MetricCollectorsTest.TEST_TOPIC, 1), recordList);
        final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
        collector1.onConsume(consumerRecords);
        collector2.onConsume(consumerRecords);
        Assert.assertEquals((2 * totalSz), MetricCollectors.totalBytesConsumption(), 0);
    }

    @Test
    public void shouldAggregateConsumptionStatsByQuery() {
        final ConsumerCollector collector1 = new ConsumerCollector();
        collector1.configure(ImmutableMap.of(GROUP_ID_CONFIG, "group1"));
        final ConsumerCollector collector2 = new ConsumerCollector();
        collector2.configure(ImmutableMap.of(GROUP_ID_CONFIG, "group1"));
        final ConsumerCollector collector3 = new ConsumerCollector();
        collector3.configure(ImmutableMap.of(GROUP_ID_CONFIG, "group2"));
        final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = new HashMap<>();
        final List<ConsumerRecord<Object, Object>> recordList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            recordList.add(new ConsumerRecord(MetricCollectorsTest.TEST_TOPIC, 1, 1, 1L, TimestampType.CREATE_TIME, 1L, 10, 10, "key", "1234567890"));
        }
        records.put(new TopicPartition(MetricCollectorsTest.TEST_TOPIC, 1), recordList);
        final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
        collector1.onConsume(consumerRecords);
        collector2.onConsume(consumerRecords);
        collector3.onConsume(consumerRecords);
        final List<Double> consumptionByQuery = new ArrayList(MetricCollectors.currentConsumptionRateByQuery());
        consumptionByQuery.sort(Comparator.naturalOrder());
        // Each query will have a unique consumer group id. In this case we have two queries and 3
        // consumers. So we should expect two results from the currentConsumptionRateByQuery call.
        Assert.assertEquals(2, consumptionByQuery.size());
        // Same as the above test, the kafka `Rate` measurable stat reports the rate as a tenth
        // of what it should be because all the samples haven't been filled out yet.
        Assert.assertEquals(5.0, Math.floor(consumptionByQuery.get(0)), 0.1);
        Assert.assertEquals(10.0, Math.floor(consumptionByQuery.get(1)), 0.1);
    }

    @Test
    public void shouldNotIncludeRestoreConsumersWhenComputingPerQueryStats() {
        final ConsumerCollector collector1 = new ConsumerCollector();
        collector1.configure(ImmutableMap.of(GROUP_ID_CONFIG, "group1"));
        final ConsumerCollector collector2 = new ConsumerCollector();
        collector2.configure(ImmutableMap.of(GROUP_ID_CONFIG, "group1"));
        final ConsumerCollector collector3 = new ConsumerCollector();
        collector3.configure(ImmutableMap.of(GROUP_ID_CONFIG, "group2"));
        // The restore consumer doesn't have a group id, and hence we should not count it as part of
        // the overall query stats.
        final ConsumerCollector collector4 = new ConsumerCollector();
        collector4.configure(ImmutableMap.of(ConsumerConfig.CLIENT_ID_CONFIG, "restore-consumer-client"));
        final Map<TopicPartition, List<ConsumerRecord<Object, Object>>> records = new HashMap<>();
        final List<ConsumerRecord<Object, Object>> recordList = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            recordList.add(new ConsumerRecord(MetricCollectorsTest.TEST_TOPIC, 1, 1, 1L, TimestampType.CREATE_TIME, 1L, 10, 10, "key", "1234567890"));
        }
        records.put(new TopicPartition(MetricCollectorsTest.TEST_TOPIC, 1), recordList);
        final ConsumerRecords<Object, Object> consumerRecords = new ConsumerRecords(records);
        collector1.onConsume(consumerRecords);
        collector2.onConsume(consumerRecords);
        collector3.onConsume(consumerRecords);
        collector4.onConsume(consumerRecords);
        final List<Double> consumptionByQuery = new ArrayList(MetricCollectors.currentConsumptionRateByQuery());
        consumptionByQuery.sort(Comparator.naturalOrder());
        // Each query will have a unique consumer group id. In this case we have two queries and 3
        // consumers. So we should expect two results from the currentConsumptionRateByQuery call.
        Assert.assertEquals(2, consumptionByQuery.size());
        // Same as the above test, the kafka `Rate` measurable stat reports the rate as a tenth
        // of what it should be because all the samples haven't been filled out yet.
        Assert.assertEquals(5.0, Math.floor(consumptionByQuery.get(0)), 0.1);
        Assert.assertEquals(10.0, Math.floor(consumptionByQuery.get(1)), 0.1);
    }

    @Test
    public void shouldAggregateDeserializationErrors() {
        for (int i = 0; i < 2000; i++) {
            StreamsErrorCollector.recordError("test-application", MetricCollectorsTest.TEST_TOPIC);
        }
        // we have 2000 errors in one sample out of a 100. So the effective error rate computed
        // should be 20 for this run.
        Assert.assertEquals(20.0, Math.floor(MetricCollectors.currentErrorRate()), 0.1);
        StreamsErrorCollector.notifyApplicationClose("test-application");
    }
}

