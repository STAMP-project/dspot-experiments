/**
 * Copyright 2017 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.metricsreporter.metric;


import CruiseControlMetric.MetricClassId.BROKER_METRIC;
import CruiseControlMetric.MetricClassId.PARTITION_METRIC;
import CruiseControlMetric.MetricClassId.TOPIC_METRIC;
import RawMetricType.ALL_TOPIC_BYTES_IN;
import RawMetricType.PARTITION_SIZE;
import RawMetricType.TOPIC_BYTES_IN;
import com.linkedin.kafka.cruisecontrol.metricsreporter.exception.UnknownVersionException;
import org.junit.Assert;
import org.junit.Test;

import static RawMetricType.ALL_TOPIC_BYTES_IN;
import static RawMetricType.PARTITION_SIZE;
import static RawMetricType.TOPIC_BYTES_IN;


public class MetricSerdeTest {
    private static final long TIME = 123L;

    private static final int BROKER_ID = 0;

    private static final String TOPIC = "topic";

    private static final int PARTITION = 100;

    private static final double VALUE = 0.1;

    @Test
    public void testBrokerMetricSerde() throws UnknownVersionException {
        BrokerMetric brokerMetric = new BrokerMetric(ALL_TOPIC_BYTES_IN, 123L, 0, 0.1);
        CruiseControlMetric deserialized = MetricSerde.fromBytes(MetricSerde.toBytes(brokerMetric));
        Assert.assertEquals(BROKER_METRIC.id(), deserialized.metricClassId().id());
        Assert.assertEquals(ALL_TOPIC_BYTES_IN.id(), deserialized.rawMetricType().id());
        Assert.assertEquals(MetricSerdeTest.TIME, deserialized.time());
        Assert.assertEquals(MetricSerdeTest.BROKER_ID, deserialized.brokerId());
        Assert.assertEquals(MetricSerdeTest.VALUE, deserialized.value(), 1.0E-6);
    }

    @Test
    public void testTopicMetricSerde() throws UnknownVersionException {
        TopicMetric topicMetric = new TopicMetric(TOPIC_BYTES_IN, 123L, 0, MetricSerdeTest.TOPIC, 0.1);
        CruiseControlMetric deserialized = MetricSerde.fromBytes(MetricSerde.toBytes(topicMetric));
        Assert.assertEquals(TOPIC_METRIC.id(), deserialized.metricClassId().id());
        Assert.assertEquals(TOPIC_BYTES_IN.id(), deserialized.rawMetricType().id());
        Assert.assertEquals(MetricSerdeTest.TIME, deserialized.time());
        Assert.assertEquals(MetricSerdeTest.BROKER_ID, deserialized.brokerId());
        Assert.assertEquals(MetricSerdeTest.TOPIC, topic());
        Assert.assertEquals(MetricSerdeTest.VALUE, deserialized.value(), 1.0E-6);
    }

    @Test
    public void testPartitionMetricSerde() throws UnknownVersionException {
        PartitionMetric partitionMetric = new PartitionMetric(PARTITION_SIZE, 123L, 0, MetricSerdeTest.TOPIC, MetricSerdeTest.PARTITION, 0.1);
        CruiseControlMetric deserialized = MetricSerde.fromBytes(MetricSerde.toBytes(partitionMetric));
        Assert.assertEquals(PARTITION_METRIC.id(), deserialized.metricClassId().id());
        Assert.assertEquals(PARTITION_SIZE.id(), deserialized.rawMetricType().id());
        Assert.assertEquals(MetricSerdeTest.TIME, deserialized.time());
        Assert.assertEquals(MetricSerdeTest.BROKER_ID, deserialized.brokerId());
        Assert.assertEquals(MetricSerdeTest.TOPIC, topic());
        Assert.assertEquals(MetricSerdeTest.PARTITION, partition());
        Assert.assertEquals(MetricSerdeTest.VALUE, deserialized.value(), 1.0E-6);
    }
}

