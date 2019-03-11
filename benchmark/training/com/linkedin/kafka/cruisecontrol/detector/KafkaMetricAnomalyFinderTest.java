/**
 * Copyright 2018 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.detector;


import com.linkedin.cruisecontrol.detector.metricanomaly.MetricAnomaly;
import com.linkedin.cruisecontrol.detector.metricanomaly.MetricAnomalyFinder;
import com.linkedin.cruisecontrol.monitor.sampling.aggregator.ValuesAndExtrapolations;
import com.linkedin.kafka.cruisecontrol.monitor.sampling.BrokerEntity;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class KafkaMetricAnomalyFinderTest {
    private final BrokerEntity _brokerEntity = new BrokerEntity("test-host", 0);

    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void testMetricAnomaliesWithNullArguments() {
        MetricAnomalyFinder<BrokerEntity> anomalyFinder = createKafkaMetricAnomalyFinder();
        expected.expect(IllegalArgumentException.class);
        Assert.assertTrue("IllegalArgumentException is expected for null history or null current metrics.", anomalyFinder.metricAnomalies(null, null).isEmpty());
    }

    @Test
    public void testMetricAnomalies() {
        MetricAnomalyFinder<BrokerEntity> anomalyFinder = createKafkaMetricAnomalyFinder();
        Map<BrokerEntity, ValuesAndExtrapolations> history = createHistory(20);
        Map<BrokerEntity, ValuesAndExtrapolations> currentMetrics = createCurrentMetrics(21, 30.0);
        Collection<MetricAnomaly<BrokerEntity>> anomalies = anomalyFinder.metricAnomalies(history, currentMetrics);
        Assert.assertTrue("There should be exactly a single metric anomaly", ((anomalies.size()) == 1));
        MetricAnomaly<BrokerEntity> anomaly = anomalies.iterator().next();
        List<Long> expectedWindow = new ArrayList<>();
        expectedWindow.add(21L);
        Assert.assertEquals(55, anomaly.metricId().intValue());
        Assert.assertEquals(_brokerEntity, anomaly.entity());
        Assert.assertEquals(expectedWindow, anomaly.windows());
    }

    @Test
    public void testInsufficientData() {
        MetricAnomalyFinder<BrokerEntity> anomalyFinder = createKafkaMetricAnomalyFinder();
        Map<BrokerEntity, ValuesAndExtrapolations> history = createHistory(19);
        Map<BrokerEntity, ValuesAndExtrapolations> currentMetrics = createCurrentMetrics(20, 20.0);
        Collection<MetricAnomaly<BrokerEntity>> anomalies = anomalyFinder.metricAnomalies(history, currentMetrics);
        Assert.assertTrue(anomalies.isEmpty());
    }
}

