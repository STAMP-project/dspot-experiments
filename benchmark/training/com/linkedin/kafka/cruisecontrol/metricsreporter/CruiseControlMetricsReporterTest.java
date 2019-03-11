/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.metricsreporter;


import ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
import ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import ConsumerConfig.GROUP_ID_CONFIG;
import ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import com.linkedin.kafka.clients.utils.tests.AbstractKafkaClientsIntegrationTestHarness;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.CruiseControlMetric;
import com.linkedin.kafka.cruisecontrol.metricsreporter.metric.MetricSerde;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.Assert;
import org.junit.Test;


public class CruiseControlMetricsReporterTest extends AbstractKafkaClientsIntegrationTestHarness {
    protected static final String TOPIC = "CruiseControlMetricsReporterTest";

    @Test
    public void testReportingMetrics() {
        Properties props = new Properties();
        props.setProperty(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers());
        props.setProperty(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(VALUE_DESERIALIZER_CLASS_CONFIG, MetricSerde.class.getName());
        props.setProperty(GROUP_ID_CONFIG, "testReportingMetrics");
        props.setProperty(AUTO_OFFSET_RESET_CONFIG, "earliest");
        setSecurityConfigs(props, "consumer");
        Consumer<String, CruiseControlMetric> consumer = new org.apache.kafka.clients.consumer.KafkaConsumer(props);
        consumer.subscribe(Collections.singletonList(CruiseControlMetricsReporterTest.TOPIC));
        long startMs = System.currentTimeMillis();
        HashSet<Integer> expectedMetricTypes = new HashSet<>(Arrays.asList(((int) (ALL_TOPIC_BYTES_IN.id())), ((int) (ALL_TOPIC_BYTES_OUT.id())), ((int) (TOPIC_BYTES_IN.id())), ((int) (TOPIC_BYTES_OUT.id())), ((int) (PARTITION_SIZE.id())), ((int) (BROKER_CPU_UTIL.id())), ((int) (ALL_TOPIC_REPLICATION_BYTES_IN.id())), ((int) (ALL_TOPIC_REPLICATION_BYTES_OUT.id())), ((int) (ALL_TOPIC_PRODUCE_REQUEST_RATE.id())), ((int) (ALL_TOPIC_FETCH_REQUEST_RATE.id())), ((int) (ALL_TOPIC_MESSAGES_IN_PER_SEC.id())), ((int) (TOPIC_PRODUCE_REQUEST_RATE.id())), ((int) (TOPIC_FETCH_REQUEST_RATE.id())), ((int) (TOPIC_MESSAGES_IN_PER_SEC.id())), ((int) (BROKER_PRODUCE_REQUEST_RATE.id())), ((int) (BROKER_CONSUMER_FETCH_REQUEST_RATE.id())), ((int) (BROKER_FOLLOWER_FETCH_REQUEST_RATE.id())), ((int) (BROKER_REQUEST_HANDLER_AVG_IDLE_PERCENT.id())), ((int) (BROKER_REQUEST_QUEUE_SIZE.id())), ((int) (BROKER_RESPONSE_QUEUE_SIZE.id())), ((int) (BROKER_PRODUCE_REQUEST_QUEUE_TIME_MS_MAX.id())), ((int) (BROKER_PRODUCE_REQUEST_QUEUE_TIME_MS_MEAN.id())), ((int) (BROKER_CONSUMER_FETCH_REQUEST_QUEUE_TIME_MS_MAX.id())), ((int) (BROKER_CONSUMER_FETCH_REQUEST_QUEUE_TIME_MS_MEAN.id())), ((int) (BROKER_FOLLOWER_FETCH_REQUEST_QUEUE_TIME_MS_MAX.id())), ((int) (BROKER_FOLLOWER_FETCH_REQUEST_QUEUE_TIME_MS_MEAN.id())), ((int) (BROKER_PRODUCE_TOTAL_TIME_MS_MAX.id())), ((int) (BROKER_PRODUCE_TOTAL_TIME_MS_MEAN.id())), ((int) (BROKER_CONSUMER_FETCH_TOTAL_TIME_MS_MAX.id())), ((int) (BROKER_CONSUMER_FETCH_TOTAL_TIME_MS_MEAN.id())), ((int) (BROKER_FOLLOWER_FETCH_TOTAL_TIME_MS_MAX.id())), ((int) (BROKER_FOLLOWER_FETCH_TOTAL_TIME_MS_MEAN.id())), ((int) (BROKER_PRODUCE_LOCAL_TIME_MS_MAX.id())), ((int) (BROKER_PRODUCE_LOCAL_TIME_MS_MEAN.id())), ((int) (BROKER_CONSUMER_FETCH_LOCAL_TIME_MS_MAX.id())), ((int) (BROKER_CONSUMER_FETCH_LOCAL_TIME_MS_MEAN.id())), ((int) (BROKER_FOLLOWER_FETCH_LOCAL_TIME_MS_MAX.id())), ((int) (BROKER_FOLLOWER_FETCH_LOCAL_TIME_MS_MEAN.id())), ((int) (BROKER_LOG_FLUSH_RATE.id())), ((int) (BROKER_LOG_FLUSH_TIME_MS_MAX.id())), ((int) (BROKER_LOG_FLUSH_TIME_MS_MEAN.id())), ((int) (BROKER_PRODUCE_REQUEST_QUEUE_TIME_MS_50TH.id())), ((int) (BROKER_PRODUCE_REQUEST_QUEUE_TIME_MS_999TH.id())), ((int) (BROKER_CONSUMER_FETCH_REQUEST_QUEUE_TIME_MS_50TH.id())), ((int) (BROKER_CONSUMER_FETCH_REQUEST_QUEUE_TIME_MS_999TH.id())), ((int) (BROKER_FOLLOWER_FETCH_REQUEST_QUEUE_TIME_MS_50TH.id())), ((int) (BROKER_FOLLOWER_FETCH_REQUEST_QUEUE_TIME_MS_999TH.id())), ((int) (BROKER_PRODUCE_TOTAL_TIME_MS_50TH.id())), ((int) (BROKER_PRODUCE_TOTAL_TIME_MS_999TH.id())), ((int) (BROKER_CONSUMER_FETCH_TOTAL_TIME_MS_50TH.id())), ((int) (BROKER_CONSUMER_FETCH_TOTAL_TIME_MS_999TH.id())), ((int) (BROKER_FOLLOWER_FETCH_TOTAL_TIME_MS_50TH.id())), ((int) (BROKER_FOLLOWER_FETCH_TOTAL_TIME_MS_999TH.id())), ((int) (BROKER_PRODUCE_LOCAL_TIME_MS_50TH.id())), ((int) (BROKER_PRODUCE_LOCAL_TIME_MS_999TH.id())), ((int) (BROKER_CONSUMER_FETCH_LOCAL_TIME_MS_50TH.id())), ((int) (BROKER_CONSUMER_FETCH_LOCAL_TIME_MS_999TH.id())), ((int) (BROKER_FOLLOWER_FETCH_LOCAL_TIME_MS_50TH.id())), ((int) (BROKER_FOLLOWER_FETCH_LOCAL_TIME_MS_999TH.id())), ((int) (BROKER_LOG_FLUSH_TIME_MS_50TH.id())), ((int) (BROKER_LOG_FLUSH_TIME_MS_999TH.id()))));
        Set<Integer> metricTypes = new HashSet<>();
        ConsumerRecords<String, CruiseControlMetric> records;
        while (((metricTypes.size()) < (expectedMetricTypes.size())) && ((System.currentTimeMillis()) < (startMs + 15000))) {
            records = consumer.poll(10);
            for (ConsumerRecord<String, CruiseControlMetric> record : records) {
                metricTypes.add(((int) (record.value().rawMetricType().id())));
            }
        } 
        Assert.assertEquals(((("Expected " + expectedMetricTypes) + ", but saw ") + metricTypes), expectedMetricTypes, metricTypes);
    }
}

